/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity.WSSecurityFirmaCifraturaUtils.Direzione;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity.WSSecurityFirmaCifraturaUtils.Messaggio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity.WSSecurityFirmaCifraturaUtils.MessaggioProtetto;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity.WSSecurityFirmaCifraturaUtils.Ordine;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Messaggi cifrati e poi firmati alterati per ottenere la decifratura prima della verifica della firma.
 *
 * In ricezione WSS4J elabora gli elementi di wsse:Security nell'ordine in cui compaiono: un messaggio costruito ad arte,
 * con xenc:EncryptedKey prima di ds:Signature o senza firma, viene decifrato prima di qualsiasi verifica della firma.
 * Se l'esito distingue un padding non valido da un testo decifrato non XML, il mittente ottiene informazioni
 * sull'esito della decifratura (padding oracle sulla modalità CBC).
 *
 * Il messaggio protetto dal mittente 'Cifratura >> Firma' viene alterato e inviato, sulla richiesta e sulla risposta,
 * al ricevente con e senza la protezione degli elementi cifrati ('Decifra solo Elementi Firmati'). Elementi cifrati alterati:
 * - BODY: messaggio SOAP con Timestamp e Body firmati e Body cifrato;
 * - ALLEGATI: messaggio SOAP with Attachments con Timestamp, Body e allegati firmati e allegati cifrati; il testo cifrato
 *   è il contenuto della parte MIME referenziata dal CipherReference ('cid:') dell'EncryptedData nell'header WS-Security.
 *   Gli allegati cifrati e firmati vengono accettati con la protezione solo con la libreria WSS4J che estende la verifica
 *   agli allegati (patch 'sorgenti_dom_require_signed_encrypted_attachment' del fork gov4j).
 * Verifiche:
 * - il messaggio non alterato viene accettato (la protezione non altera il flusso normale);
 * - ogni messaggio alterato viene rifiutato;
 * - con la protezione il rifiuto avviene prima della decifratura ("... is not signed"), i diagnostici non contengono
 *   dettagli della decifratura e la risposta restituita al client è identica per le varianti 'padding non valido'
 *   e 'testo decifrato non XML'; senza protezione le eventuali differenze vengono solo registrate nel log della testsuite.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@RunWith(Parameterized.class)
public class WSSecurityFirmaCifraturaManomissioniTest extends ConfigLoader {

	/** Elementi cifrati alterati e mittente che li protegge */
	enum Elemento {
		BODY("mitt-cf-ts-firma-ts-body-cifra-body", Messaggio.SOAP),
		ALLEGATI("mitt-cf-ts-firma-ts-body-att-cifra-att", Messaggio.SOAP_WITH_ATTACHMENTS);

		private final String mittente;
		private final Messaggio messaggio;
		Elemento(String mittente, Messaggio messaggio) {
			this.mittente = mittente;
			this.messaggio = messaggio;
		}
	}

	private static final String NS_WSSE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	private static final String NS_DSIG = "http://www.w3.org/2000/09/xmldsig#";
	private static final String NS_XENC = "http://www.w3.org/2001/04/xmlenc#";

	/** Dettagli della decifratura che non devono comparire nei diagnostici con la protezione */
	private static final String[] DETTAGLI_DECIFRATURA = {
			"padding", "Padding", "final block", "BadPadding", "IllegalBlockSize",
			"well-formed", "well formed", "Content is not allowed", "SAXParse", "XMLStreamException", "Invalid byte", "markup"
	};

	enum Manomissione {
		/** EncryptedKey prima di ds:Signature, padding del testo cifrato non valido */
		CHIAVE_PRIMA_DELLA_FIRMA_PADDING_ALTERATO(true, true, true),
		/** EncryptedKey prima di ds:Signature, padding valido e testo decifrato non XML */
		CHIAVE_PRIMA_DELLA_FIRMA_CONTENUTO_ALTERATO(true, true, false),
		/** ds:Signature rimossa, padding del testo cifrato non valido */
		FIRMA_RIMOSSA_PADDING_ALTERATO(false, true, true),
		/** ds:Signature rimossa, padding valido e testo decifrato non XML */
		FIRMA_RIMOSSA_CONTENUTO_ALTERATO(false, true, false),
		/** EncryptedKey prima di ds:Signature, testo cifrato intatto */
		CHIAVE_PRIMA_DELLA_FIRMA(true, false, false),
		/** ds:Signature rimossa, testo cifrato intatto */
		FIRMA_RIMOSSA(false, false, false);

		private final boolean chiavePrimaDellaFirma;
		private final boolean testoCifratoAlterato;
		private final boolean padding;
		Manomissione(boolean chiavePrimaDellaFirma, boolean testoCifratoAlterato, boolean padding) {
			this.chiavePrimaDellaFirma = chiavePrimaDellaFirma;
			this.testoCifratoAlterato = testoCifratoAlterato;
			this.padding = padding;
		}
	}

	@Parameters(name = "{index}: {0} {1} protezione={2}")
	public static Collection<Object[]> data() {
		Collection<Object[]> l = new ArrayList<>();
		for (Elemento elemento : Elemento.values()) {
			for (Direzione direzione : Direzione.values()) {
				for (boolean protezione : new boolean[] {false, true}) {
					l.add(new Object[] {elemento, direzione, protezione});
				}
			}
		}
		return l;
	}

	private final Elemento elemento;
	private final Direzione direzione;
	private final boolean protezione;
	public WSSecurityFirmaCifraturaManomissioniTest(Elemento elemento, Direzione direzione, boolean protezione) {
		this.elemento = elemento;
		this.direzione = direzione;
		this.protezione = protezione;
	}

	@Test
	public void manomissioni() throws Exception {
		String ricevente = WSSecurityFirmaCifraturaUtils.getAzioneRiceventeQualsiasi(Ordine.CIFRATURA_FIRMA, this.protezione, true);
		String mittente = this.elemento.mittente;
		String tag = "["+this.elemento+" "+this.direzione+" "+ricevente+" <- "+mittente+"]";

		// controllo: il messaggio non alterato viene accettato
		MessaggioProtetto protetto = WSSecurityFirmaCifraturaUtils.proteggi(WSSecurityFirmaCifraturaUtils.getApiFirmaCifratura(mittente), mittente, this.elemento.messaggio);
		HttpResponse response = WSSecurityFirmaCifraturaUtils.verifica(WSSecurityFirmaCifraturaUtils.getApiFirmaCifratura(ricevente), this.direzione, ricevente, protetto);
		WSSecurityFirmaCifraturaUtils.verifyOk(response, tag+" messaggio non alterato");

		Map<Manomissione, String> risposte = new EnumMap<>(Manomissione.class);
		for (Manomissione m : Manomissione.values()) {
			protetto = WSSecurityFirmaCifraturaUtils.proteggi(WSSecurityFirmaCifraturaUtils.getApiFirmaCifratura(mittente), mittente, this.elemento.messaggio);
			MessaggioProtetto alterato = new MessaggioProtetto(applica(protetto, this.elemento, m), protetto.getContentType());
			response = WSSecurityFirmaCifraturaUtils.verifica(WSSecurityFirmaCifraturaUtils.getApiFirmaCifratura(ricevente), this.direzione, ricevente, alterato);
			String idTransazione = WSSecurityFirmaCifraturaUtils.getIdTransazione(response);
			String descrizione = tag+" "+m;
			if(this.protezione) {
				WSSecurityFirmaCifraturaUtils.verifyKo(response, this.direzione, descrizione,
						Collections.singletonList(WSSecurityFirmaCifraturaUtils.ERRORE_NON_FIRMATO));
				for (String dettaglio : DETTAGLI_DECIFRATURA) {
					DBVerifier.notExistsDiagnostico(idTransazione, dettaglio);
				}
			}
			else {
				WSSecurityFirmaCifraturaUtils.verifyKo(response, this.direzione, descrizione, Collections.emptyList());
			}
			risposte.put(m, getRispostaClient(response));
		}

		// uniformità della risposta restituita al client tra le varianti 'padding non valido' e 'testo decifrato non XML'
		List<String> differenze = new ArrayList<>();
		confronta(differenze, risposte, Manomissione.CHIAVE_PRIMA_DELLA_FIRMA_PADDING_ALTERATO, Manomissione.CHIAVE_PRIMA_DELLA_FIRMA_CONTENUTO_ALTERATO);
		confronta(differenze, risposte, Manomissione.FIRMA_RIMOSSA_PADDING_ALTERATO, Manomissione.FIRMA_RIMOSSA_CONTENUTO_ALTERATO);
		if(this.protezione) {
			assertTrue(tag+" Con la protezione le risposte devono essere identiche: "+differenze, differenze.isEmpty());
		}
		else if(!differenze.isEmpty()) {
			ConfigLoader.getLoggerCore().info("{} senza protezione la risposta distingue l'esito della decifratura: {}", tag, differenze);
		}
	}

	private static void confronta(List<String> differenze, Map<Manomissione, String> risposte, Manomissione padding, Manomissione contenuto) {
		String rPadding = risposte.get(padding);
		String rContenuto = risposte.get(contenuto);
		if(!rPadding.equals(rContenuto)) {
			differenze.add(padding+"["+rPadding+"] "+contenuto+"["+rContenuto+"]");
		}
	}

	/** Risposta visibile al client, senza gli identificativi e le date che variano ad ogni invocazione */
	private static String getRispostaClient(HttpResponse response) {
		String contenuto = response.getContent()!=null ? new String(response.getContent()) : "";
		return (response.getResultHTTPOperation()+" "+response.getHeaderFirstValue(Headers.GovWayTransactionErrorType)+" "+contenuto)
				.replaceAll("[0-9A-Fa-f][0-9A-Fa-f_:.-]{7,}", "*");
	}


	// ---- Manomissioni ----

	static byte[] applica(MessaggioProtetto protetto, Elemento elemento, Manomissione m) throws Exception {
		if(!protetto.isMultipart()) {
			return serialize(applica(parse(protetto.getContenuto()), null, elemento, m));
		}
		Multipart mp = new Multipart(protetto.getContenuto(), protetto.getContentType());
		Parte root = mp.parti.get(0);
		root.contenuto = serialize(applica(parse(root.contenuto), mp, elemento, m));
		return mp.toBytes();
	}

	private static Document applica(Document doc, Multipart mp, Elemento elemento, Manomissione m) throws Exception {
		Element security = primo(doc.getDocumentElement(), NS_WSSE, "Security");
		assertNotNull("wsse:Security non presente", security);
		Element firma = figlio(security, NS_DSIG, "Signature");
		assertNotNull("ds:Signature non presente in wsse:Security", firma);
		if(m.chiavePrimaDellaFirma) {
			Element chiave = figlio(security, NS_XENC, "EncryptedKey");
			if(chiave==null) {
				chiave = figlio(security, NS_XENC, "ReferenceList");
			}
			assertNotNull("Né xenc:EncryptedKey né xenc:ReferenceList presenti in wsse:Security", chiave);
			security.removeChild(chiave);
			security.insertBefore(chiave, firma);
		}
		else {
			security.removeChild(firma);
		}
		if(m.testoCifratoAlterato) {
			if(Elemento.ALLEGATI.equals(elemento)) {
				alteraAllegatiCifrati(security, mp, m.padding);
			}
			else {
				alteraTestoCifrato(doc, m.padding);
			}
		}
		return doc;
	}

	private static Document parse(byte[] xml) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
	}

	private static byte[] serialize(Document doc) throws Exception {
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		t.setOutputProperty(OutputKeys.INDENT, "no");
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		t.transform(new DOMSource(doc), new StreamResult(bout));
		return bout.toByteArray();
	}

	/**
	 * Altera il testo cifrato degli xenc:EncryptedData del Body (modalità CBC: IV seguito dai blocchi cifrati).
	 * - padding: altera l'ultimo byte del penultimo blocco (o dell'IV), che in CBC modifica solo l'ultimo byte del testo decifrato,
	 *   cioè il byte di padding: il nuovo valore (XOR 0xF0) è sempre fuori dall'intervallo valido 1..dimensione blocco;
	 * - contenuto: altera il primo byte dell'IV, che modifica solo il primo carattere del testo decifrato: il padding resta valido
	 *   e il testo decifrato non è più XML ben formato.
	 */
	private static void alteraTestoCifrato(Document doc, boolean padding) {
		Element body = primo(doc.getDocumentElement(), WSSecurityFirmaCifraturaUtils.SOAP_11, "Body");
		assertNotNull("Body non presente", body);
		NodeList enc = body.getElementsByTagNameNS(NS_XENC, "EncryptedData");
		assertTrue("Nessun xenc:EncryptedData nel Body", enc.getLength()>0);
		for (int i = 0; i < enc.getLength(); i++) {
			Element ed = (Element) enc.item(i);
			Element cd = figlio(ed, NS_XENC, "CipherData");
			Element cv = cd!=null ? figlio(cd, NS_XENC, "CipherValue") : null;
			assertNotNull("xenc:CipherData/xenc:CipherValue non presente nell'EncryptedData", cv);
			byte[] cifrato = Base64.getMimeDecoder().decode(cv.getTextContent());
			altera(cifrato, getDimensioneBlocco(ed), padding);
			cv.setTextContent(Base64.getEncoder().encodeToString(cifrato));
		}
	}

	/**
	 * Altera il testo cifrato degli allegati: per ogni xenc:EncryptedData dell'header con xenc:CipherReference ('cid:'),
	 * il contenuto della parte MIME referenziata (IV seguito dai blocchi cifrati), con le stesse modalità del Body.
	 * Con 'contenuto' viene alterato il primo byte del contenuto decifrato dell'allegato.
	 */
	private static void alteraAllegatiCifrati(Element security, Multipart mp, boolean padding) throws Exception {
		NodeList enc = security.getElementsByTagNameNS(NS_XENC, "EncryptedData");
		int alterati = 0;
		for (int i = 0; i < enc.getLength(); i++) {
			Element ed = (Element) enc.item(i);
			Element cd = figlio(ed, NS_XENC, "CipherData");
			Element cr = cd!=null ? figlio(cd, NS_XENC, "CipherReference") : null;
			if(cr==null) {
				continue;
			}
			String uri = cr.getAttribute("URI");
			assertTrue("CipherReference non riferisce un allegato: "+uri, uri.startsWith("cid:"));
			Parte parte = mp.getParte(uri.substring("cid:".length()));
			assertNotNull("Allegato cifrato '"+uri+"' non presente nel messaggio", parte);
			boolean base64 = "base64".equalsIgnoreCase(parte.getHeader("Content-Transfer-Encoding"));
			byte[] cifrato = base64 ? Base64.getMimeDecoder().decode(parte.contenuto) : parte.contenuto;
			altera(cifrato, getDimensioneBlocco(ed), padding);
			parte.contenuto = base64 ? Base64.getMimeEncoder().encode(cifrato) : cifrato;
			alterati++;
		}
		assertTrue("Nessun allegato cifrato (xenc:EncryptedData con xenc:CipherReference) nell'header WS-Security", alterati>0);
	}

	private static void altera(byte[] cifrato, int blocco, boolean padding) {
		assertTrue("Testo cifrato troppo corto ("+cifrato.length+" byte)", cifrato.length >= 2*blocco);
		int indice = padding ? cifrato.length - 1 - blocco : 0;
		cifrato[indice] = (byte) (cifrato[indice] ^ (padding ? 0xF0 : 0x01));
	}

	private static int getDimensioneBlocco(Element encryptedData) {
		Element em = figlio(encryptedData, NS_XENC, "EncryptionMethod");
		String alg = em!=null ? em.getAttribute("Algorithm") : "";
		return alg.toLowerCase().contains("aes") ? 16 : 8;
	}


	// ---- Multipart (gestito a livello di byte per non alterare il contenuto binario delle parti) ----

	private static final class Parte {
		private String intestazioni;
		private byte[] contenuto;
		String getHeader(String nome) {
			for (String riga : this.intestazioni.split("\r\n")) {
				int idx = riga.indexOf(':');
				if(idx>0 && riga.substring(0, idx).trim().equalsIgnoreCase(nome)) {
					return riga.substring(idx+1).trim();
				}
			}
			return null;
		}
	}

	private static final class Multipart {
		private final String delimitatore;
		private final String preambolo;
		private final String epilogo;
		private final List<Parte> parti = new ArrayList<>();

		Multipart(byte[] messaggio, String contentType) {
			Matcher mb = Pattern.compile("boundary=\"?([^\";]+)\"?", Pattern.CASE_INSENSITIVE).matcher(contentType);
			assertTrue("Boundary non presente nel Content-Type: "+contentType, mb.find());
			this.delimitatore = "--"+mb.group(1).trim();
			String s = new String(messaggio, StandardCharsets.ISO_8859_1);
			int inizio = s.indexOf(this.delimitatore);
			assertTrue("Boundary non presente nel messaggio", inizio>=0);
			this.preambolo = s.substring(0, inizio);
			String sep = "\r\n"+this.delimitatore;
			int pos = inizio + this.delimitatore.length();
			while(!s.startsWith("--", pos)) {
				pos = s.indexOf("\r\n", pos) + 2; // fine riga del delimitatore
				int fine = s.indexOf(sep, pos);
				assertTrue("Multipart non terminato correttamente", fine>=0);
				String parte = s.substring(pos, fine);
				int fineIntestazioni = parte.indexOf("\r\n\r\n");
				assertTrue("Intestazioni della parte MIME non terminate", fineIntestazioni>=0);
				Parte p = new Parte();
				p.intestazioni = parte.substring(0, fineIntestazioni);
				p.contenuto = parte.substring(fineIntestazioni+4).getBytes(StandardCharsets.ISO_8859_1);
				this.parti.add(p);
				pos = fine + sep.length();
			}
			this.epilogo = s.substring(pos);
		}

		Parte getParte(String contentId) {
			for (Parte p : this.parti) {
				String cid = p.getHeader("Content-ID");
				if(cid!=null && cid.replaceAll("(^<)|(>$)", "").equals(contentId)) {
					return p;
				}
			}
			return null;
		}

		byte[] toBytes() {
			StringBuilder sb = new StringBuilder(this.preambolo);
			for (Parte p : this.parti) {
				sb.append(this.delimitatore).append("\r\n").append(p.intestazioni).append("\r\n\r\n")
					.append(new String(p.contenuto, StandardCharsets.ISO_8859_1)).append("\r\n");
			}
			sb.append(this.delimitatore).append(this.epilogo);
			return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
		}
	}

	private static Element figlio(Element parent, String ns, String localName) {
		NodeList childs = parent.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node n = childs.item(i);
			if(n instanceof Element && ns.equals(n.getNamespaceURI()) && localName.equals(n.getLocalName())) {
				return (Element) n;
			}
		}
		return null;
	}

	private static Element primo(Element root, String ns, String localName) {
		NodeList nl = root.getElementsByTagNameNS(ns, localName);
		return nl.getLength()>0 ? (Element) nl.item(0) : null;
	}
}
