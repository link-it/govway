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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * Supporto ai test sulle configurazioni WS-Security con firma e cifratura insieme (maschere della console
 * messageSecurity/wss4j/sender/SignatureEncrypt.xml e receiver/SignatureEncrypt.xml) e sugli algoritmi di firma accettati.
 *
 * Le configurazioni (API TestWSSecurityFirmaCifratura e TestWSSecurityAlgoritmiFirma) sono quelle che producono le maschere:
 * - le azioni 'mitt-*' dell'erogazione applicano WS-Security alla risposta del backend (il messaggio protetto viene restituito al client);
 * - le azioni 'ric-*' verificano il messaggio protetto: sull'erogazione come richiesta (catena della richiesta),
 *   sulla fruizione come risposta del backend (catena della risposta).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSSecurityFirmaCifraturaUtils {

	private WSSecurityFirmaCifraturaUtils() {}

	/*
	 * API raggruppate per tema (al massimo 20 azioni ciascuna):
	 * - TestWSSecurityFirmaCifratura{Body,Allegati,BodyAllegati}: mittenti, per elementi cifrati
	 * - TestWSSecurityFirmaCifraturaRicezione: riceventi 'Qualsiasi' (ordine e protezione degli elementi cifrati)
	 * - TestWSSecurityFirmaCifraturaAttesi{Body,Allegati,BodyAllegati}: riceventi con elementi attesi, per elementi cifrati attesi
	 * - TestWSSecurityAlgoritmiFirma / TestWSSecurityAlgoritmiFirmaCifratura: algoritmi accettati, solo firma o firma + cifratura
	 */
	private static final String API_FIRMA_CIFRATURA_PREFIX = "TestWSSecurityFirmaCifratura";
	public static final String API_FIRMA_CIFRATURA_RICEZIONE = API_FIRMA_CIFRATURA_PREFIX+"Ricezione";
	public static final String API_ALGORITMI_FIRMA = "TestWSSecurityAlgoritmiFirma";
	public static final String API_ALGORITMI_FIRMA_CIFRATURA = "TestWSSecurityAlgoritmiFirmaCifratura";

	/** API che contiene l'azione della matrice firma + cifratura (mittente 'mitt-*', ricevente 'ric-attesi-*' o 'ric-*') */
	public static String getApiFirmaCifratura(String azione) {
		if(azione.startsWith("mitt-")) {
			return API_FIRMA_CIFRATURA_PREFIX + getSuffissoApiCifrati(azione);
		}
		if(azione.startsWith("ric-attesi")) {
			return API_FIRMA_CIFRATURA_PREFIX + "Attesi" + getSuffissoApiCifrati(azione);
		}
		return API_FIRMA_CIFRATURA_RICEZIONE;
	}
	private static String getSuffissoApiCifrati(String azione) {
		String cifrati = azione.substring(azione.lastIndexOf("-cifra-") + "-cifra-".length());
		switch (cifrati) {
		case "body":
			return "Body";
		case "att":
			return "Allegati";
		case "body-att":
			return "BodyAllegati";
		default:
			throw new IllegalArgumentException("Elementi cifrati non gestiti nell'azione '"+azione+"'");
		}
	}
	public static final String HEADER_FILE_RISPOSTA = "govway-testsuite-struttura-envelope-file";

	static final String SOAP_11 = "http://schemas.xmlsoap.org/soap/envelope/";
	static final String WSU = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";

	public static final String SECURITY_REQUEST_ERROR = "BadRequest";
	public static final String SECURITY_RESPONSE_ERROR = "InvalidResponse";

	/** Errore WSS4J con la protezione degli elementi cifrati: elemento cifrato non coperto da una firma già verificata */
	public static final String ERRORE_NON_FIRMATO = "is not signed";


	/** Parti del messaggio che possono essere protette */
	public enum Parte { TIMESTAMP, BODY, ATTACHMENTS }

	/** Ordine delle operazioni del mittente */
	public enum Ordine {
		/** 'Firma >> Cifratura' */
		FIRMA_CIFRATURA("fc"),
		/** 'Cifratura >> Firma' */
		CIFRATURA_FIRMA("cf");

		private final String codice;
		Ordine(String codice) {
			this.codice = codice;
		}
		public String getCodice() {
			return this.codice;
		}
	}

	/** Messaggio restituito dal backend e protetto dal mittente */
	public enum Messaggio { SOAP, SOAP_WITH_ATTACHMENTS }

	/** Catena verificata: richiesta (ricevente sull'erogazione) o risposta (ricevente sulla fruizione) */
	public enum Direzione { RICHIESTA, RISPOSTA }


	// ---- Matrice firma + cifratura ----

	/** Elementi firmati selezionabili nella maschera ('Elementi da firmare'), senza o con il Timestamp */
	public static List<Set<Parte>> getFirmati(boolean timestamp) {
		List<Set<Parte>> l = new ArrayList<>();
		l.add(EnumSet.of(Parte.BODY));
		if(timestamp) {
			l.add(EnumSet.of(Parte.TIMESTAMP));
			l.add(EnumSet.of(Parte.TIMESTAMP, Parte.BODY));
		}
		l.add(EnumSet.of(Parte.ATTACHMENTS));
		if(timestamp) {
			l.add(EnumSet.of(Parte.TIMESTAMP, Parte.ATTACHMENTS));
		}
		l.add(EnumSet.of(Parte.BODY, Parte.ATTACHMENTS));
		if(timestamp) {
			l.add(EnumSet.of(Parte.TIMESTAMP, Parte.BODY, Parte.ATTACHMENTS));
		}
		return l;
	}
	/** Elementi cifrati selezionabili nella maschera ('Elementi da cifrare') */
	public static List<Set<Parte>> getCifrati() {
		List<Set<Parte>> l = new ArrayList<>();
		l.add(EnumSet.of(Parte.BODY));
		l.add(EnumSet.of(Parte.ATTACHMENTS));
		l.add(EnumSet.of(Parte.BODY, Parte.ATTACHMENTS));
		return l;
	}
	/** Codice delle parti usato nel nome delle azioni (es. 'ts-body-att') */
	public static String getCodice(Set<Parte> parti) {
		List<String> l = new ArrayList<>();
		if(parti.contains(Parte.TIMESTAMP)) {
			l.add("ts");
		}
		if(parti.contains(Parte.BODY)) {
			l.add("body");
		}
		if(parti.contains(Parte.ATTACHMENTS)) {
			l.add("att");
		}
		return String.join("-", l);
	}
	private static String getSuffissoTimestamp(boolean timestamp) {
		return timestamp ? "-ts" : "";
	}

	/** Mittente configurato con la maschera 'WS-Security Signature Encrypt' */
	public static final class Mittente {
		private final Ordine ordine;
		private final boolean timestamp;
		private final Set<Parte> firmati;
		private final Set<Parte> cifrati;
		private final String azione;
		Mittente(Ordine ordine, boolean timestamp, Set<Parte> firmati, Set<Parte> cifrati) {
			this.ordine = ordine;
			this.timestamp = timestamp;
			this.firmati = firmati;
			this.cifrati = cifrati;
			this.azione = "mitt-"+ordine.getCodice()+getSuffissoTimestamp(timestamp)+"-firma-"+getCodice(firmati)+"-cifra-"+getCodice(cifrati);
		}
		public Ordine getOrdine() {
			return this.ordine;
		}
		public boolean isTimestamp() {
			return this.timestamp;
		}
		public Set<Parte> getFirmati() {
			return this.firmati;
		}
		public Set<Parte> getCifrati() {
			return this.cifrati;
		}
		public String getAzione() {
			return this.azione;
		}
		/** Messaggi provati: senza allegati protetti anche il messaggio SOAP senza allegati */
		public List<Messaggio> getMessaggi() {
			if(this.firmati.contains(Parte.ATTACHMENTS) || this.cifrati.contains(Parte.ATTACHMENTS)) {
				return Collections.singletonList(Messaggio.SOAP_WITH_ATTACHMENTS);
			}
			return Arrays.asList(Messaggio.SOAP, Messaggio.SOAP_WITH_ATTACHMENTS);
		}
		/** Con la protezione degli elementi cifrati: ordine 'Cifratura >> Firma' e ogni elemento cifrato anche firmato */
		public boolean isAccettatoConProtezione() {
			return Ordine.CIFRATURA_FIRMA.equals(this.ordine) && this.firmati.containsAll(this.cifrati);
		}
		@Override
		public String toString() {
			return this.azione;
		}
	}

	public static List<Mittente> getMittenti() {
		List<Mittente> l = new ArrayList<>();
		for (Ordine ordine : new Ordine[] {Ordine.FIRMA_CIFRATURA, Ordine.CIFRATURA_FIRMA}) {
			for (boolean timestamp : new boolean[] {false, true}) {
				for (Set<Parte> firmati : getFirmati(timestamp)) {
					for (Set<Parte> cifrati : getCifrati()) {
						l.add(new Mittente(ordine, timestamp, firmati, cifrati));
					}
				}
			}
		}
		return l;
	}

	/** Ricevente con 'Elementi firmati/cifrati attesi' = 'Qualsiasi' */
	public static String getAzioneRiceventeQualsiasi(Ordine ordine, boolean protezione, boolean timestamp) {
		return "ric-"+ordine.getCodice()+(protezione ? "-protetto" : "")+getSuffissoTimestamp(timestamp);
	}
	/** Ricevente con elementi firmati e cifrati attesi (ordine 'Firma >> Cifratura', senza protezione) */
	public static String getAzioneRiceventeAttesi(boolean timestamp, Set<Parte> firmati, Set<Parte> cifrati) {
		return "ric-attesi"+getSuffissoTimestamp(timestamp)+"-firma-"+getCodice(firmati)+"-cifra-"+getCodice(cifrati);
	}


	// ---- Messaggi ----

	private static final String BOUNDARY = "----=_Part_WSSecurityFirmaCifratura";

	public static String getMessaggio() {
		return "<soap:Envelope xmlns:soap=\""+SOAP_11+"\">"+
				"<soap:Header><ns2:TestHdr xmlns:ns2=\"http://govway.org/example/header\">header</ns2:TestHdr></soap:Header>"+
				"<soap:Body><ns2:Test xmlns:ns2=\"http://govway.org/example\"><ns2:valore>firma e cifratura</ns2:valore></ns2:Test></soap:Body>"+
				"</soap:Envelope>";
	}
	/**
	 * Messaggio SOAP with Attachments (due allegati).
	 * La prima riga del boundary termina con il solo LF: il TestService ricava il boundary del Content-Type
	 * con MultipartUtils.findBoundary, che con CRLF includerebbe il '\r'; le righe successive restano CRLF, richiesto da SAAJ.
	 */
	public static String getMessaggioConAllegati() {
		return "--"+BOUNDARY+"\n"+
				"Content-Type: text/xml; charset=UTF-8\r\n"+
				"Content-Transfer-Encoding: binary\r\n"+
				"Content-ID: <root.message@govway.org>\r\n\r\n"+
				getMessaggio()+"\r\n"+
				"--"+BOUNDARY+"\r\n"+
				"Content-Type: text/plain\r\n"+
				"Content-Transfer-Encoding: binary\r\n"+
				"Content-ID: <allegato1@govway.org>\r\n\r\n"+
				"primo allegato\r\n"+
				"--"+BOUNDARY+"\r\n"+
				"Content-Type: application/xml\r\n"+
				"Content-Transfer-Encoding: binary\r\n"+
				"Content-ID: <allegato2@govway.org>\r\n\r\n"+
				"<allegato>secondo allegato</allegato>\r\n"+
				"--"+BOUNDARY+"--\r\n";
	}
	public static String getMessaggio(Messaggio messaggio) {
		return Messaggio.SOAP_WITH_ATTACHMENTS.equals(messaggio) ? getMessaggioConAllegati() : getMessaggio();
	}

	/** Messaggio protetto da un'azione mittente */
	public static final class MessaggioProtetto {
		private final byte[] contenuto;
		private final String contentType;
		MessaggioProtetto(byte[] contenuto, String contentType) {
			this.contenuto = contenuto;
			this.contentType = contentType;
		}
		public byte[] getContenuto() {
			return this.contenuto;
		}
		public String getContentType() {
			return this.contentType;
		}
		public boolean isMultipart() {
			return this.contentType!=null && this.contentType.contains(HttpConstants.CONTENT_TYPE_MULTIPART_RELATED);
		}
	}


	// ---- Invocazioni ----

	private static String getUrl(String api, Direzione direzione, String azione) {
		if(Direzione.RICHIESTA.equals(direzione)) {
			return System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+azione;
		}
		return System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+azione;
	}

	/**
	 * Invia la richiesta all'erogazione (RICHIESTA) o alla fruizione (RISPOSTA); il backend restituirà 'rispostaBackend' così com'è
	 * (multipart se contiene allegati).
	 */
	public static HttpResponse invoke(String api, Direzione direzione, String azione, byte[] richiesta, String contentType, byte[] rispostaBackend) throws Exception {
		File f = File.createTempFile("firmaCifratura", ".bin");
		try {
			Files.write(f.toPath(), rispostaBackend);
			// GovWay e TestService possono girare con un utente diverso da quello della testsuite
			f.setReadable(true, false);
			f.setWritable(true, false);

			HttpRequest request = new HttpRequest();
			request.setReadTimeout(20000);
			request.setMethod(HttpRequestMethod.POST);
			request.setContentType(contentType);
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, azione);
			request.addHeader(HEADER_FILE_RISPOSTA, f.getAbsolutePath());
			request.setContent(richiesta);
			request.setUrl(getUrl(api, direzione, azione));
			HttpResponse response = HttpUtilities.httpInvoke(request);
			assertNotNull("Identificativo di transazione non presente", getIdTransazione(response));
			return response;
		}finally {
			Files.deleteIfExists(f.toPath());
		}
	}

	/** Applica WS-Security con l'azione mittente al messaggio indicato (risposta del backend dell'erogazione) */
	public static MessaggioProtetto proteggi(String api, String azioneMittente, Messaggio messaggio) throws Exception {
		HttpResponse response = invoke(api, Direzione.RICHIESTA, azioneMittente, getMessaggio().getBytes(StandardCharsets.UTF_8), HttpConstants.CONTENT_TYPE_SOAP_1_1,
				getMessaggio(messaggio).getBytes(StandardCharsets.UTF_8));
		assertEquals("Applicazione di WS-Security ("+azioneMittente+") fallita: "+new String(response.getContent()), 200, response.getResultHTTPOperation());
		MessaggioProtetto protetto = new MessaggioProtetto(response.getContent(), response.getContentType());
		if(Messaggio.SOAP_WITH_ATTACHMENTS.equals(messaggio)) {
			assertTrue("Atteso messaggio con allegati, content-type: "+protetto.getContentType(), protetto.isMultipart());
		}
		return protetto;
	}

	/**
	 * Invia il messaggio protetto all'azione ricevente: come richiesta dell'erogazione (RICHIESTA)
	 * o come risposta del backend della fruizione (RISPOSTA).
	 */
	public static HttpResponse verifica(String api, Direzione direzione, String azioneRicevente, MessaggioProtetto protetto) throws Exception {
		byte[] messaggio = getMessaggio().getBytes(StandardCharsets.UTF_8);
		if(Direzione.RICHIESTA.equals(direzione)) {
			return invoke(api, direzione, azioneRicevente, protetto.getContenuto(), protetto.getContentType(), messaggio);
		}
		byte[] rispostaBackend = protetto.isMultipart() ? getMultipartPerTestService(protetto.getContenuto()) : protetto.getContenuto();
		return invoke(api, direzione, azioneRicevente, messaggio, HttpConstants.CONTENT_TYPE_SOAP_1_1, rispostaBackend);
	}

	/**
	 * Il TestService ricava il boundary dalla prima riga del file: la riga deve terminare con il solo LF
	 * (con CRLF il boundary includerebbe il '\r'). Il contenuto delle parti, protetto da WS-Security, non viene modificato.
	 */
	static byte[] getMultipartPerTestService(byte[] multipart) {
		String s = new String(multipart, StandardCharsets.ISO_8859_1);
		int inizio = 0;
		while(inizio < s.length() && (s.charAt(inizio)=='\r' || s.charAt(inizio)=='\n')) {
			inizio++;
		}
		s = s.substring(inizio);
		int fineRiga = s.indexOf("\r\n");
		if(fineRiga>0) {
			s = s.substring(0, fineRiga) + "\n" + s.substring(fineRiga+2);
		}
		return s.getBytes(StandardCharsets.ISO_8859_1);
	}


	// ---- Verifiche ----

	public static String getIdTransazione(HttpResponse response) {
		return response.getHeaderFirstValue("GovWay-Transaction-ID");
	}

	public static long getEsito(EsitoTransazioneName name) throws Exception {
		return EsitiProperties.getInstanceFromProtocolName(ConfigLoader.getLoggerCore(), Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(name);
	}

	public static void verifyOk(HttpResponse response, String descrizione) throws Exception {
		assertEquals("Atteso messaggio accettato ("+descrizione+"): "+new String(response.getContent()), 200, response.getResultHTTPOperation());
		DBVerifier.verify(getIdTransazione(response), getEsito(EsitoTransazioneName.OK));
	}

	/** Verifica il rifiuto per errore di sicurezza del messaggio e la presenza dei frammenti indicati nei diagnostici */
	public static void verifyKo(HttpResponse response, Direzione direzione, String descrizione, List<String> diagnostici) throws Exception {
		assertEquals("Atteso errore di sicurezza ("+descrizione+"): "+new String(response.getContent()), 500, response.getResultHTTPOperation());
		String idTransazione = getIdTransazione(response);
		if(Direzione.RICHIESTA.equals(direzione)) {
			assertEquals(SECURITY_REQUEST_ERROR, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
			DBVerifier.verify(idTransazione, getEsito(EsitoTransazioneName.ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA));
		}
		else {
			assertEquals(SECURITY_RESPONSE_ERROR, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
			DBVerifier.verify(idTransazione, getEsito(EsitoTransazioneName.ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA));
		}
		for (String d : diagnostici) {
			DBVerifier.existsDiagnostico(idTransazione, d);
		}
	}
}
