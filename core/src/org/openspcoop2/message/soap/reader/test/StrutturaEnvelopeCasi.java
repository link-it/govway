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
package org.openspcoop2.message.soap.reader.test;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * Casi di struttura dell'Envelope SOAP condivisi dai test del reader ({@link ReaderTest}) e dei controlli in streaming
 * ({@link SoapEnvelopeStructureCheckTest}).
 * Per ogni caso sono indicate l'anomalia rilevabile nella parte iniziale del messaggio (fino al primo figlio del Body, senza
 * leggere byte aggiuntivi) e quella rilevabile verificando tutto il messaggio.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StrutturaEnvelopeCasi {

	private StrutturaEnvelopeCasi() {}

	public static final String NS_TEST = "urn:govway:test:struttura-envelope";
	private static final String SECONDO_HEADER_CONTENUTO = "<t:altroHeader xmlns:t=\""+NS_TEST+"\">altro</t:altroHeader>";
	private static final String SECONDO_BODY_CONTENUTO = "<t:altroPayload xmlns:t=\""+NS_TEST+"\">altro</t:altroPayload>";
	private static final String ESTENSIONE = "<t:estensione xmlns:t=\""+NS_TEST+"\">estensione</t:estensione>";

	public static final String ERR_HEADER = "more than one Header element";
	public static final String ERR_BODY = "more than one Body element";
	public static final String ERR_HEADER_DOPO_BODY = "Header element after the Body";
	public static final String ERR_FAULT = "more than one Fault element";
	public static final String ERR_FAULT_NON_UNICO = "Fault element must be the only child of the Body";
	public static final String ERR_FIGLIO = "unexpected child element of the Envelope";
	public static final String ERR_DOPO_BODY = "element after the Body";
	public static final String ERR_TESTO = "unexpected text content in the Envelope";
	public static final String ERR_BODY_ASSENTE = "Body element not found";
	public static final String ERR_DOPO_ENVELOPE = "content after the end of the Envelope";
	public static final String NON_RILEVATO = null;

	public static class Caso {
		private final String descrizione;
		private final String messaggio;
		private final String contentType;
		private final String erroreParteIniziale;
		private final String erroreCompleto;
		public Caso(String descrizione, String messaggio, String contentType, String erroreParteIniziale, String erroreCompleto) {
			this.descrizione = descrizione;
			this.messaggio = messaggio;
			this.contentType = contentType;
			this.erroreParteIniziale = erroreParteIniziale;
			this.erroreCompleto = erroreCompleto;
		}
		public String getDescrizione() {
			return this.descrizione;
		}
		public String getMessaggio() {
			return this.messaggio;
		}
		public String getContentType() {
			return this.contentType;
		}
		/** @return anomalia rilevabile nella parte iniziale del messaggio, o null */
		public String getErroreParteIniziale() {
			return this.erroreParteIniziale;
		}
		/** @return anomalia rilevabile verificando tutto il messaggio, o null */
		public String getErroreCompleto() {
			return this.erroreCompleto;
		}
	}

	public static String getNamespace(MessageType messageType) {
		return MessageType.SOAP_11.equals(messageType) ? Costanti.SOAP_ENVELOPE_NAMESPACE : Costanti.SOAP12_ENVELOPE_NAMESPACE;
	}
	public static String getBaseContentType(MessageType messageType) {
		return MessageType.SOAP_11.equals(messageType) ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
	}
	public static String getContentType(MessageType messageType) {
		return getBaseContentType(messageType)+"; charset=UTF-8";
	}

	/** Elementi del messaggio per la versione SOAP indicata */
	public static class Parti {
		public final String ns;
		public final String nsAltro;
		public final String envOpen;
		public final String envClose = "</soap:Envelope>";
		public final String h = "<soap:Header><ns2:TestHdr xmlns:ns2=\"http://govway.org/example/header\">header</ns2:TestHdr></soap:Header>";
		public final String h2 = "<soap:Header>"+SECONDO_HEADER_CONTENUTO+"</soap:Header>";
		public final String b = "<soap:Body><ns2:Test xmlns:ns2=\"http://govway.org/example\">payload</ns2:Test></soap:Body>";
		public final String b2 = "<soap:Body>"+SECONDO_BODY_CONTENUTO+"</soap:Body>";
		public final String fault;
		public Parti(MessageType messageType) {
			boolean soap11 = MessageType.SOAP_11.equals(messageType);
			this.ns = getNamespace(messageType);
			this.nsAltro = soap11 ? Costanti.SOAP12_ENVELOPE_NAMESPACE : Costanti.SOAP_ENVELOPE_NAMESPACE;
			this.envOpen = "<soap:Envelope xmlns:soap=\""+this.ns+"\">";
			this.fault = soap11 ?
					"<soap:Fault><faultcode>soap:Server</faultcode><faultstring>Fault</faultstring></soap:Fault>" :
					"<soap:Fault><soap:Code><soap:Value>soap:Receiver</soap:Value></soap:Code><soap:Reason><soap:Text xml:lang=\"it\">Fault</soap:Text></soap:Reason></soap:Fault>";
		}
		public String envelope(String ... figli) {
			StringBuilder sb = new StringBuilder(this.envOpen);
			for (String f : figli) {
				sb.append(f);
			}
			return sb.append(this.envClose).toString();
		}
		/** @return Body con un payload di almeno la dimensione indicata (byte) */
		public String bodyGrande(int dimensione) {
			StringBuilder sb = new StringBuilder();
			while(sb.length() < dimensione) {
				sb.append("<ns2:riga>contenuto di riempimento del payload</ns2:riga>");
			}
			return "<soap:Body><ns2:Test xmlns:ns2=\"http://govway.org/example\">"+sb+"</ns2:Test></soap:Body>";
		}
	}

	public static List<Caso> getCasi(MessageType messageType) {
		Parti p = new Parti(messageType);
		String ct = getContentType(messageType);
		String ns = p.ns;
		String nsAltro = p.nsAltro;
		String envOpen = p.envOpen;
		String envClose = p.envClose;
		String h = p.h;
		String h2 = p.h2;
		String b = p.b;
		String b2 = p.b2;
		String fault = p.fault;
		List<Caso> l = new ArrayList<>();

		// casi corretti
		l.add(new Caso("busta corretta", envOpen+h+b+envClose, ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("senza header", envOpen+b+envClose, ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("header vuoto", envOpen+"<soap:Header/>"+b+envClose, ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("body vuoto", envOpen+h+"<soap:Body/>"+envClose, ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("body vuoto seguito da commento e istruzione di elaborazione", envOpen+h+"<soap:Body/><!-- c --><?pi x?>\n"+envClose, ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("commenti e spazi", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!-- c -->"+envOpen+"\n <!-- c -->\n"+h+"\n <!-- c -->\n"+b+"\n<!-- c -->"+envClose+"\n<!-- c -->\n", ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("prefisso diverso dichiarato nel tag", envOpen+"<s:Header xmlns:s=\""+ns+"\"/><s:Body xmlns:s=\""+ns+"\"><a/></s:Body>"+envClose, ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("namespace di default", "<Envelope xmlns=\""+ns+"\"><Header/><Body><a xmlns=\"\"/></Body></Envelope>", ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("fault", envOpen+"<soap:Body>"+fault+"</soap:Body>"+envClose, ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("payload con radice di nome Header di un altro namespace", envOpen+h+"<soap:Body><t:Header xmlns:t=\""+NS_TEST+"\">x</t:Header></soap:Body>"+envClose, ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("payload con radice di nome Header senza namespace", envOpen+h+"<soap:Body><Header>x</Header></soap:Body>"+envClose, ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("elementi omonimi nel payload", envOpen+h+"<soap:Body><t:c xmlns:t=\""+NS_TEST+"\"><t:Header/><t:Body/></t:c></soap:Body>"+envClose, ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("fault ed envelope annidati nel payload", envOpen+h+"<soap:Body><t:c xmlns:t=\""+NS_TEST+"\">"+fault+envOpen+h+b+b2+envClose+"</t:c></soap:Body>"+envClose, ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("markup in CDATA, commenti e attributi del payload", envOpen+h+"<soap:Body><t:c xmlns:t=\""+NS_TEST+"\"><t:d a=\"&lt;/soap:Body&gt;\" b='/>'/>"+
				"<![CDATA[</soap:Body><soap:Body>]]><!-- </soap:Body><soap:Body> --><?pi </soap:Body>?></t:c></soap:Body>"+envClose, ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("BOM UTF-8", "\uFEFF"+envOpen+h+b+envClose, ct, NON_RILEVATO, NON_RILEVATO));

		// anomalie nella parte iniziale del messaggio
		l.add(new Caso("2 header", envOpen+h+h+b+envClose, ct, ERR_HEADER, ERR_HEADER));
		l.add(new Caso("2 header, il secondo diverso", envOpen+h+h2+b+envClose, ct, ERR_HEADER, ERR_HEADER));
		l.add(new Caso("2 header, il primo vuoto", envOpen+"<soap:Header/>"+h+b+envClose, ct, ERR_HEADER, ERR_HEADER));
		l.add(new Caso("2 header e 2 body", envOpen+h+h2+b+b2+envClose, ct, ERR_HEADER, ERR_HEADER));
		l.add(new Caso("fault figlio dell'envelope prima del body", envOpen+h+fault+b+envClose, ct, ERR_FIGLIO, ERR_FIGLIO));
		l.add(new Caso("elemento soap non previsto prima del body", envOpen+h+"<soap:Trailer>x</soap:Trailer>"+b+envClose, ct, ERR_FIGLIO, ERR_FIGLIO));
		l.add(new Caso("envelope annidato prima del body", envOpen+h+"<soap:Envelope><soap:Header/>"+b2+"</soap:Envelope>"+b+envClose, ct, ERR_FIGLIO, ERR_FIGLIO));
		l.add(new Caso("elemento altro namespace prima del body", envOpen+h+ESTENSIONE+b+envClose, ct, ERR_FIGLIO, ERR_FIGLIO));
		l.add(new Caso("elemento altro namespace prima dell'header", envOpen+ESTENSIONE+h+b+envClose, ct, ERR_FIGLIO, ERR_FIGLIO));
		l.add(new Caso("body dell'altra versione soap prima del body", envOpen+h+"<sx:Body xmlns:sx=\""+nsAltro+"\">"+SECONDO_BODY_CONTENUTO+"</sx:Body>"+b+envClose, ct, ERR_FIGLIO, ERR_FIGLIO));
		l.add(new Caso("header dell'altra versione soap", envOpen+"<sx:Header xmlns:sx=\""+nsAltro+"\"/>"+b+envClose, ct, ERR_FIGLIO, ERR_FIGLIO));
		l.add(new Caso("testo tra header e body", envOpen+h+"testo"+b+envClose, ct, ERR_TESTO, ERR_TESTO));
		l.add(new Caso("testo prima dell'header", envOpen+"testo"+h+b+envClose, ct, ERR_TESTO, ERR_TESTO));
		l.add(new Caso("body assente", envOpen+h+envClose, ct, ERR_BODY_ASSENTE, ERR_BODY_ASSENTE));
		l.add(new Caso("envelope vuoto", envOpen+envClose, ct, ERR_BODY_ASSENTE, ERR_BODY_ASSENTE));
		// elemento che segue un Body vuoto (auto-chiuso): letto insieme al Body, senza byte aggiuntivi
		l.add(new Caso("2 body, il primo vuoto", envOpen+h+"<soap:Body/>"+b2+envClose, ct, ERR_BODY, ERR_BODY));
		l.add(new Caso("header dopo un body vuoto", envOpen+"<soap:Body/>"+h+envClose, ct, ERR_HEADER_DOPO_BODY, ERR_HEADER_DOPO_BODY));
		l.add(new Caso("elemento soap non previsto dopo un body vuoto", envOpen+h+"<soap:Body />"+"<soap:Trailer>x</soap:Trailer>"+envClose, ct, ERR_FIGLIO, ERR_FIGLIO));
		l.add(new Caso("elemento altro namespace dopo un body vuoto", envOpen+h+"<soap:Body/>\n"+ESTENSIONE+envClose, ct, ERR_DOPO_BODY, ERR_DOPO_BODY));

		// anomalie nella coda del messaggio
		l.add(new Caso("2 body", envOpen+h+b+b2+envClose, ct, NON_RILEVATO, ERR_BODY));
		l.add(new Caso("2 body, il secondo con un prefisso diverso", envOpen+h+b+"<sx:Body xmlns:sx=\""+ns+"\">"+SECONDO_BODY_CONTENUTO+"</sx:Body>"+envClose, ct, NON_RILEVATO, ERR_BODY));
		l.add(new Caso("header dopo il body", envOpen+b+h+envClose, ct, NON_RILEVATO, ERR_HEADER_DOPO_BODY));
		l.add(new Caso("header, body, header", envOpen+h+b+h2+envClose, ct, NON_RILEVATO, ERR_HEADER_DOPO_BODY));
		l.add(new Caso("elemento dopo il body", envOpen+h+b+ESTENSIONE+envClose, ct, NON_RILEVATO, ERR_DOPO_BODY));
		l.add(new Caso("elemento soap non previsto dopo il body", envOpen+h+b+"<soap:Trailer>x</soap:Trailer>"+envClose, ct, NON_RILEVATO, ERR_FIGLIO));
		l.add(new Caso("body dell'altra versione soap dopo il body", envOpen+h+b+"<sx:Body xmlns:sx=\""+nsAltro+"\">"+SECONDO_BODY_CONTENUTO+"</sx:Body>"+envClose, ct, NON_RILEVATO, ERR_DOPO_BODY));
		l.add(new Caso("testo dopo il body", envOpen+h+b+"testo"+envClose, ct, NON_RILEVATO, ERR_TESTO));
		l.add(new Caso("CDATA dopo il body", envOpen+h+b+"<![CDATA[testo]]>"+envClose, ct, NON_RILEVATO, ERR_TESTO));
		l.add(new Caso("2 fault", envOpen+h+"<soap:Body>"+fault+fault+"</soap:Body>"+envClose, ct, NON_RILEVATO, ERR_FAULT));
		l.add(new Caso("fault e payload", envOpen+h+"<soap:Body>"+fault+"<a/></soap:Body>"+envClose, ct, NON_RILEVATO, ERR_FAULT_NON_UNICO));
		l.add(new Caso("payload e fault", envOpen+h+"<soap:Body><a/>"+fault+"</soap:Body>"+envClose, ct, NON_RILEVATO, ERR_FAULT_NON_UNICO));
		l.add(new Caso("body con fault prima dell'header", envOpen+"<soap:Body>"+fault+"</soap:Body>"+h+envClose, ct, NON_RILEVATO, ERR_HEADER_DOPO_BODY));
		l.add(new Caso("2 envelope concatenati", envOpen+h+b+envClose+"\n"+envOpen+h+b2+envClose, ct, NON_RILEVATO, ERR_DOPO_ENVELOPE));
		l.add(new Caso("testo dopo l'envelope", envOpen+h+b+envClose+"testo", ct, NON_RILEVATO, ERR_DOPO_ENVELOPE));
		return l;
	}

	/**
	 * Messaggi multipart: la busta corretta o errata (2 Body) nella prima parte o, indicata da 'start', nella seconda.
	 * L'allegato contiene markup SOAP che non deve essere considerato.
	 */
	public static List<Caso> getCasiMultipart(MessageType messageType) {
		Parti p = new Parti(messageType);
		String envelopeCorretto = p.envelope(p.h, p.b);
		String envelopeErrato = p.envelope(p.h, p.b, p.b2);
		String type = getBaseContentType(messageType);
		String boundary = "----=_Part_Struttura_Envelope";
		String allegato = "allegato con markup <soap:Body>x</soap:Body> testo";
		String ct = HttpConstants.CONTENT_TYPE_MULTIPART_RELATED+"; type=\""+type+"\"; boundary=\""+boundary+"\"";
		String ctStart = ct+"; start=\"<root.message@govway.org>\"";
		List<Caso> l = new ArrayList<>();
		l.add(new Caso("multipart, envelope corretto nella prima parte", multipart(boundary, type, envelopeCorretto, allegato, true), ct, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("multipart, envelope errato nella prima parte", multipart(boundary, type, envelopeErrato, allegato, true), ct, NON_RILEVATO, ERR_BODY));
		l.add(new Caso("multipart, envelope corretto nella seconda parte (start)", multipart(boundary, type, envelopeCorretto, envelopeErrato, false), ctStart, NON_RILEVATO, NON_RILEVATO));
		l.add(new Caso("multipart, envelope errato nella seconda parte (start)", multipart(boundary, type, envelopeErrato, envelopeCorretto, false), ctStart, NON_RILEVATO, ERR_BODY));
		return l;
	}
	public static String multipart(String boundary, String type, String envelope, String allegato, boolean envelopePrimaParte) {
		return multipart(boundary, type, envelope, allegato, envelopePrimaParte, "8bit");
	}
	public static String multipart(String boundary, String type, String envelope, String allegato, boolean envelopePrimaParte, String transferEncoding) {
		String parteEnvelope = "--"+boundary+"\r\nContent-Type: "+type+"; charset=UTF-8\r\nContent-Transfer-Encoding: "+transferEncoding+"\r\nContent-ID: <root.message@govway.org>\r\n\r\n"+envelope+"\r\n";
		String parteAllegato = "--"+boundary+"\r\nContent-Type: text/plain\r\nContent-ID: <allegato@govway.org>\r\n\r\n"+allegato+"\r\n";
		return (envelopePrimaParte ? parteEnvelope+parteAllegato : parteAllegato+parteEnvelope) + "--"+boundary+"--\r\n";
	}
}
