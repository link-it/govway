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

package org.openspcoop2.testsuite.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Casistiche di Envelope SOAP 1.1 ben formati ma con una struttura non conforme
 * (più Header o Body, ordine errato, Fault multipli o fuori posto, elementi non previsti come figli
 * dell'Envelope), più alcuni casi di controllo che devono continuare ad essere accettati.
 *
 * I messaggi vengono costruiti a partire da un Envelope valido manipolandone il testo: in questo modo
 * l'anomalia arriva al gateway così com'è, senza passare da un parser SOAP (Axis, SAAJ) che potrebbe
 * normalizzarla o rifiutarla prima dell'invio.
 *
 * La classe è usata sia dai client della testsuite (richieste) sia dalle servlet che simulano il backend
 * (risposte): in quest'ultimo caso il caso da applicare viene indicato tramite l'elemento marcatore
 * restituito da {@link #getElementoMarcatore(Caso)} inserito nel Body della richiesta.
 *
 * I frammenti ERRORE_* sono le parti di testo che il diagnostico emesso dal gateway deve contenere per
 * ciascuna anomalia (es. "Invalid SOAP Envelope: more than one Body element (2)").
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StrutturaEnvelopeSoap11 {

	private StrutturaEnvelopeSoap11() {}

	public static final String NAMESPACE_SOAP_11 = "http://schemas.xmlsoap.org/soap/envelope/";
	public static final String NAMESPACE_SOAP_12 = "http://www.w3.org/2003/05/soap-envelope";
	public static final String NAMESPACE_TEST = "urn:govway:testsuite:struttura-envelope";


	// ---- Frammenti attesi nel diagnostico ----

	/** Prefisso dei messaggi di errore della struttura dell'Envelope */
	public static final String PREFISSO_ERRORE_STRUTTURA = "Invalid SOAP Envelope: ";
	public static final String ERRORE_BODY_MULTIPLI = "more than one Body element";
	public static final String ERRORE_HEADER_MULTIPLI = "more than one Header element";
	public static final String ERRORE_HEADER_DOPO_BODY = "Header element after the Body";
	public static final String ERRORE_BODY_ASSENTE = "Body element not found";
	public static final String ERRORE_FAULT_MULTIPLI = "more than one Fault element";
	public static final String ERRORE_FAULT_NON_UNICO = "Fault element must be the only child of the Body";
	public static final String ERRORE_ELEMENTO_NON_ATTESO = "unexpected child element of the Envelope";
	public static final String ERRORE_ELEMENTO_DOPO_BODY = "element after the Body";
	public static final String ERRORE_TESTO_NON_ATTESO = "unexpected text content in the Envelope";
	
	// Struttura corretta, ma l'elemento protetto da WS-Security (firma o cifratura) non è il Body del messaggio:
	// rilevabile solo verificando che il nodo referenziato da WS-Security sia lo stesso nodo del Body elaborato
	public static final String ERRORE_COPERTURA_WSSECURITY = "WS-Security protected element is not the SOAP Body";

	// XML non ben formato: errore sollevato dal parser
	public static final String ERRORE_PARSER_ROOT_MULTIPLI_XERCES = "The markup in the document following the root element must be well-formed";

	// Intestazione di protocollo duplicata (controllo del protocollo SPCoop, già esistente)
	public static final String ERRORE_PROTOCOLLO_INTESTAZIONE_MULTIPLA = "Più di un header e-Gov presente";



	// ---- Casi ----

	private interface Costruttore {
		String costruisci(Parti p);
	}

	public enum Caso {

		// Intestazione di protocollo duplicata nello stesso Header: struttura SOAP corretta, l'anomalia è rilevata dal protocollo
		DOPPIA_INTESTAZIONE_STESSO_HEADER("#01 Header con il contenuto (intestazione di protocollo) ripetuto due volte",
				p -> p.envelope(p.headerConContenutoDuplicato(), p.body),
				ERRORE_PROTOCOLLO_INTESTAZIONE_MULTIPLA),

		// Body
		DOPPIO_BODY("#03 Envelope con 2 Body (il secondo con un payload diverso)",
				p -> p.envelope(p.header(), p.body, p.secondoBody()),
				ERRORE_BODY_MULTIPLI),
		DOPPIO_BODY_INVERTITO("#03 Envelope con 2 Body (il primo con un payload diverso)",
				p -> p.envelope(p.header(), p.secondoBody(), p.body),
				ERRORE_BODY_MULTIPLI),
		DOPPIO_BODY_SECONDO_VUOTO("#03 Envelope con 2 Body, il secondo vuoto",
				p -> p.envelope(p.header(), p.body, p.el("Body", null)),
				ERRORE_BODY_MULTIPLI),
		DOPPIO_BODY_PRIMO_VUOTO("#03 Envelope con 2 Body, il primo vuoto",
				p -> p.envelope(p.header(), p.el("Body", null), p.body),
				ERRORE_BODY_MULTIPLI),
		DOPPIO_BODY_PREFISSO_DIVERSO("#03 Envelope con 2 Body, il secondo con un prefisso diverso associato al namespace SOAP 1.1",
				p -> p.envelope(p.header(), p.body,
						"<s11:Body xmlns:s11=\""+NAMESPACE_SOAP_11+"\">"+Parti.PAYLOAD_SECONDO_BODY+"</s11:Body>"),
				ERRORE_BODY_MULTIPLI),
		TRE_BODY("#03 Envelope con 3 Body",
				p -> p.envelope(p.header(), p.body, p.secondoBody(), p.secondoBody()),
				ERRORE_BODY_MULTIPLI),

		// Header
		DOPPIO_HEADER("#02 Envelope con 2 Header identici",
				p -> p.envelope(p.header(), p.header(), p.body),
				ERRORE_HEADER_MULTIPLI),
		DOPPIO_HEADER_SECONDO_DIVERSO("#02 Envelope con 2 Header, il secondo con un contenuto diverso",
				p -> p.envelope(p.header(), p.secondoHeader(), p.body),
				ERRORE_HEADER_MULTIPLI),
		DOPPIO_HEADER_PRIMO_DIVERSO("#02 Envelope con 2 Header, il primo con un contenuto diverso",
				p -> p.envelope(p.secondoHeader(), p.header(), p.body),
				ERRORE_HEADER_MULTIPLI),
		DOPPIO_HEADER_SECONDO_VUOTO("#02 Envelope con 2 Header, il secondo vuoto",
				p -> p.envelope(p.header(), p.el("Header", null), p.body),
				ERRORE_HEADER_MULTIPLI),
		DOPPIO_HEADER_PRIMO_VUOTO("#02 Envelope con 2 Header, il primo vuoto",
				p -> p.envelope(p.el("Header", null), p.header(), p.body),
				ERRORE_HEADER_MULTIPLI),

		// Ordine
		HEADER_DOPO_BODY("#10 Envelope con l'Header dopo il Body",
				p -> p.envelope(p.body, p.header()),
				ERRORE_HEADER_DOPO_BODY),
		HEADER_BODY_HEADER("#12 Envelope con Header, Body e un secondo Header",
				p -> p.envelope(p.header(), p.body, p.secondoHeader()),
				ERRORE_HEADER_DOPO_BODY),
		BODY_HEADER_BODY("Envelope con Body, Header e un secondo Body",
				p -> p.envelope(p.body, p.header(), p.secondoBody()),
				ERRORE_HEADER_DOPO_BODY),
		DOPPIO_HEADER_DOPPIO_BODY("Envelope con 2 Header e 2 Body",
				p -> p.envelope(p.header(), p.secondoHeader(), p.body, p.secondoBody()),
				ERRORE_HEADER_MULTIPLI),
		HEADER_BODY_ALTERNATI("Envelope con Header, Body, Header, Body",
				p -> p.envelope(p.header(), p.body, p.secondoHeader(), p.secondoBody()),
				ERRORE_HEADER_DOPO_BODY),

		// Body assente
		BODY_ASSENTE("Envelope con il solo Header",
				p -> p.envelope(p.header()),
				ERRORE_BODY_ASSENTE),
		ENVELOPE_VUOTO("Envelope senza figli",
				p -> p.envelope(),
				ERRORE_BODY_ASSENTE),

		// Fault
		DOPPIO_FAULT("#05 Body con 2 Fault",
				p -> p.envelope(p.header(), p.bodyCon(p.fault()+p.fault())),
				ERRORE_FAULT_MULTIPLI),
		FAULT_E_PAYLOAD("#06 Body con un Fault seguito da un payload",
				p -> p.envelope(p.header(), p.bodyCon(p.fault()+p.bodyContent)),
				ERRORE_FAULT_NON_UNICO),
		PAYLOAD_E_FAULT("#07 Body con un payload seguito da un Fault",
				p -> p.envelope(p.header(), p.bodyCon(p.bodyContent+p.fault())),
				ERRORE_FAULT_NON_UNICO),
		BODY_FAULT_E_BODY_PAYLOAD("#08 Envelope con un Body contenente un Fault e un Body contenente il payload",
				p -> p.envelope(p.header(), p.bodyCon(p.fault()), p.body),
				ERRORE_BODY_MULTIPLI),
		BODY_PAYLOAD_E_BODY_FAULT("Envelope con un Body contenente il payload e un Body contenente un Fault",
				p -> p.envelope(p.header(), p.body, p.bodyCon(p.fault())),
				ERRORE_BODY_MULTIPLI),
		FAULT_FIGLIO_ENVELOPE_DOPO_BODY("#09 Fault figlio diretto dell'Envelope, dopo il Body",
				p -> p.envelope(p.header(), p.body, p.fault()),
				ERRORE_ELEMENTO_NON_ATTESO),
		FAULT_FIGLIO_ENVELOPE_PRIMA_BODY("#09 Fault figlio diretto dell'Envelope, tra Header e Body",
				p -> p.envelope(p.header(), p.fault(), p.body),
				ERRORE_ELEMENTO_NON_ATTESO),
		BODY_CON_FAULT_PRIMA_DI_HEADER("#11 Body contenente un Fault prima dell'Header",
				p -> p.envelope(p.bodyCon(p.fault()), p.header()),
				ERRORE_HEADER_DOPO_BODY),

		// Elementi non previsti come figli dell'Envelope
		ELEMENTO_SOAP_NON_PREVISTO_DOPO_BODY("Elemento del namespace SOAP non previsto, dopo il Body",
				p -> p.envelope(p.header(), p.body, p.el("Trailer", "contenuto")),
				ERRORE_ELEMENTO_NON_ATTESO),
		ELEMENTO_SOAP_NON_PREVISTO_PRIMA_BODY("Elemento del namespace SOAP non previsto, tra Header e Body",
				p -> p.envelope(p.header(), p.el("Trailer", "contenuto"), p.body),
				ERRORE_ELEMENTO_NON_ATTESO),
		ENVELOPE_FIGLIO_DOPO_BODY("Envelope annidato come figlio diretto dell'Envelope, dopo il Body",
				p -> p.envelope(p.header(), p.body, p.el("Envelope", p.el("Header", null)+p.secondoBody())),
				ERRORE_ELEMENTO_NON_ATTESO),
		ENVELOPE_FIGLIO_PRIMA_BODY("Envelope annidato come figlio diretto dell'Envelope, tra Header e Body",
				p -> p.envelope(p.header(), p.el("Envelope", p.el("Header", null)+p.secondoBody()), p.body),
				ERRORE_ELEMENTO_NON_ATTESO),
		ELEMENTO_ALTRO_NAMESPACE_DOPO_BODY("Elemento di un altro namespace dopo il Body (ammesso dallo schema SOAP 1.1, vietato da WS-I BP R1011)",
				p -> p.envelope(p.header(), p.body, Parti.ELEMENTO_ALTRO_NAMESPACE),
				ERRORE_ELEMENTO_DOPO_BODY),
		ELEMENTO_ALTRO_NAMESPACE_PRIMA_BODY("Elemento di un altro namespace tra Header e Body",
				p -> p.envelope(p.header(), Parti.ELEMENTO_ALTRO_NAMESPACE, p.body),
				ERRORE_ELEMENTO_NON_ATTESO),
		ELEMENTO_ALTRO_NAMESPACE_PRIMA_HEADER("Elemento di un altro namespace prima dell'Header",
				p -> p.envelope(Parti.ELEMENTO_ALTRO_NAMESPACE, p.header(), p.body),
				ERRORE_ELEMENTO_NON_ATTESO),
		BODY_SOAP12_DOPO_BODY("Body SOAP 1.2 dopo il Body SOAP 1.1",
				p -> p.envelope(p.header(), p.body,
						"<s12:Body xmlns:s12=\""+NAMESPACE_SOAP_12+"\">"+Parti.PAYLOAD_SECONDO_BODY+"</s12:Body>"),
				ERRORE_ELEMENTO_DOPO_BODY),
		BODY_SOAP12_PRIMA_BODY("Body SOAP 1.2 prima del Body SOAP 1.1",
				p -> p.envelope(p.header(),
						"<s12:Body xmlns:s12=\""+NAMESPACE_SOAP_12+"\">"+Parti.PAYLOAD_SECONDO_BODY+"</s12:Body>", p.body),
				ERRORE_ELEMENTO_NON_ATTESO),

		// Testo
		TESTO_DOPO_BODY("Testo come figlio diretto dell'Envelope, dopo il Body",
				p -> p.envelope(p.header(), p.body, "testo non previsto"),
				ERRORE_TESTO_NON_ATTESO),
		TESTO_TRA_HEADER_E_BODY("Testo come figlio diretto dell'Envelope, tra Header e Body",
				p -> p.envelope(p.header(), "testo non previsto", p.body),
				ERRORE_TESTO_NON_ATTESO),

		// XML non ben formato (già rifiutato dal parser, verifica di non regressione)
		DOPPIO_ENVELOPE_CONCATENATO("#04 Due Envelope concatenati",
				p -> p.envelope(p.header(), p.body) + "\n" + p.envelopeSenzaPrologo(p.header(), p.secondoBody()),
				ERRORE_PARSER_ROOT_MULTIPLI_XERCES),
		
		// XML Signature Wrapping con struttura valida (1 Header, 1 Body): il Body protetto viene spostato altrove
		// e sostituito da un Body non protetto. Hanno senso solo su messaggi firmati o cifrati.
		COPERTURA_BODY_PROTETTO_IN_HEADER("XSW: Body protetto spostato in un header block, nuovo Body non protetto",
				p -> p.envelope(p.headerConBlocco("<t:contenitore xmlns:t=\""+NAMESPACE_TEST+"\">"+p.body+"</t:contenitore>"), p.secondoBody()),
				ERRORE_COPERTURA_WSSECURITY),
		COPERTURA_BODY_PROTETTO_NEL_PAYLOAD("XSW: Body protetto annidato nel payload di un nuovo Body non protetto",
				p -> p.envelope(p.header(), p.el("Body", Parti.PAYLOAD_SECONDO_BODY+"<t:contenitore xmlns:t=\""+NAMESPACE_TEST+"\">"+p.body+"</t:contenitore>")),
				ERRORE_COPERTURA_WSSECURITY),


		// ---- Casi corretti: devono continuare ad essere accettati ----

		OK_BUSTA_CORRETTA("#00 Busta corretta",
				p -> p.envelope(p.header(), p.body)),
		OK_COMMENTI_TRA_ELEMENTI("Commenti e spazi tra gli elementi dell'Envelope",
				p -> p.envelope("<!-- commento iniziale -->", p.header(), "<!-- commento -->", p.body, "<!-- commento finale -->")),
		OK_FAULT_ANNIDATO_NEL_PAYLOAD("Elemento Fault annidato all'interno del payload",
				p -> p.envelope(p.header(), p.bodyCon(p.bodyContent+"<t:contenitore xmlns:t=\""+NAMESPACE_TEST+"\">"+p.fault()+"</t:contenitore>"))),
		OK_ELEMENTI_OMONIMI_NEL_PAYLOAD("Elementi Header e Body di un altro namespace all'interno del payload",
				p -> p.envelope(p.header(), p.bodyCon(p.bodyContent+
						"<t:contenitore xmlns:t=\""+NAMESPACE_TEST+"\"><t:Header>h</t:Header><t:Body>b</t:Body></t:contenitore>"))),
		OK_ENVELOPE_ANNIDATO_NEL_PAYLOAD("Envelope SOAP con Header e Body annidato all'interno del payload",
				p -> p.envelope(p.header(), p.bodyCon(p.bodyContent+
						"<t:contenitore xmlns:t=\""+NAMESPACE_TEST+"\"><s11:Envelope xmlns:s11=\""+NAMESPACE_SOAP_11+"\">"+
						"<s11:Header/><s11:Body>"+Parti.PAYLOAD_SECONDO_BODY+"</s11:Body><s11:Body/></s11:Envelope></t:contenitore>"))),
		OK_HEADER_ASSENTE("Envelope senza Header",
				p -> p.envelope(p.body));


		private final String descrizione;
		private final Costruttore costruttore;
		private final String erroreAtteso;

		Caso(String descrizione, Costruttore costruttore){
			this(descrizione, costruttore, null);
		}
		Caso(String descrizione, Costruttore costruttore, String erroreAtteso){
			this.descrizione = descrizione;
			this.costruttore = costruttore;
			this.erroreAtteso = erroreAtteso;
		}

		public String getDescrizione() {
			return this.descrizione;
		}
		/**
		 * @return frammento che deve essere presente nel diagnostico (lista vuota per i casi corretti): è la prima anomalia incontrata
		 * scorrendo il messaggio; per le anomalie della struttura il frammento comprende il prefisso 'Invalid SOAP Envelope: '
		 * (es. 'element after the Body' non deve corrispondere a 'Header element after the Body')
		 */
		public List<String> getErroriAttesi() {
			if(this.erroreAtteso==null) {
				return Collections.emptyList();
			}
			boolean struttura = !this.isControlloProtocollo() && !this.isCoperturaWSSecurity() && this!=DOPPIO_ENVELOPE_CONCATENATO;
			return Collections.singletonList(struttura ? PREFISSO_ERRORE_STRUTTURA+this.erroreAtteso : this.erroreAtteso);
		}
		public boolean isCorretto() {
			return this.erroreAtteso==null;
		}
		/**
		 * @return true per i casi in cui l'Header (e quindi l'intestazione di protocollo) segue il Body: l'intestazione non si trova
		 * nella parte iniziale del messaggio letta in streaming e il messaggio viene rifiutato dal protocollo prima della verifica della struttura
		 */
		public boolean isIntestazioneDopoBody() {
			return this==HEADER_DOPO_BODY || this==BODY_HEADER_BODY || this==BODY_CON_FAULT_PRIMA_DI_HEADER;
		}
		/**
		 * @return true per i casi il cui primo figlio del Body è un Fault: il gateway interpreta il Fault costruendo il DOM
		 */
		public boolean isFaultPrimoFiglioBody() {
			return this==DOPPIO_FAULT || this==FAULT_E_PAYLOAD || this==BODY_FAULT_E_BODY_PAYLOAD || this==BODY_CON_FAULT_PRIMA_DI_HEADER;
		}
		/**
		 * @return true per i casi con struttura corretta in cui il Body protetto da WS-Security è stato spostato:
		 * vanno usati solo su messaggi firmati o cifrati e non fanno parte dei casi di sola struttura
		 */
		public boolean isCoperturaWSSecurity() {
			return this==COPERTURA_BODY_PROTETTO_IN_HEADER || this==COPERTURA_BODY_PROTETTO_NEL_PAYLOAD;
		}
		/**
		 * @return false per i casi che eliminano l'Header: inapplicabili quando l'Header trasporta l'intestazione di protocollo
		 */
		public boolean isHeaderPreservato() {
			return this!=OK_HEADER_ASSENTE;
		}
		/**
		 * @return true per i casi la cui anomalia non riguarda la struttura SOAP ma l'intestazione di protocollo:
		 * hanno senso solo dove l'Header contiene l'intestazione (es. richiesta verso la PA) e sono rilevati dal protocollo, non dal core
		 */
		public boolean isControlloProtocollo() {
			return this==DOPPIA_INTESTAZIONE_STESSO_HEADER;
		}

		public String applica(String envelopeValido) {
			return this.costruttore.costruisci(Parti.parse(envelopeValido));
		}
	}


	/**
	 * @return i casi con struttura dell'Envelope non conforme (esclusi quelli rilevati dal protocollo)
	 */
	public static List<Caso> getCasiErrati(){
		List<Caso> l = new ArrayList<>();
		for (Caso caso : Caso.values()) {
			if(!caso.isCorretto() && !caso.isControlloProtocollo() && !caso.isCoperturaWSSecurity()) {
				l.add(caso);
			}
		}
		return l;
	}
	public static List<Caso> getCasiCorretti(){
		List<Caso> l = new ArrayList<>();
		for (Caso caso : Caso.values()) {
			if(caso.isCorretto() && !caso.isCoperturaWSSecurity()) {
				l.add(caso);
			}
		}
		return l;
	}



	// ---- Marcatore usato per comunicare il caso alle servlet che simulano il backend ----

	private static final String MARCATORE_NOME = "strutturaEnvelope";
	private static final Pattern MARCATORE_PATTERN = Pattern.compile(MARCATORE_NOME+"\\s[^>]*caso=\"([A-Z0-9_]+)\"");

	public static String getElementoMarcatore(Caso caso) {
		return "<t:"+MARCATORE_NOME+" xmlns:t=\""+NAMESPACE_TEST+"\" caso=\""+caso.name()+"\"/>";
	}

	/**
	 * @return il caso indicato dall'elemento marcatore presente nel messaggio, o null se assente
	 */
	public static Caso getCasoDaMarcatore(String messaggio) {
		if(messaggio==null) {
			return null;
		}
		Matcher m = MARCATORE_PATTERN.matcher(messaggio);
		if(m.find()) {
			return Caso.valueOf(m.group(1));
		}
		return null;
	}



	// ---- Scomposizione dell'Envelope ----

	private static final class Parti {

		private static final String PAYLOAD_SECONDO_BODY = "<t:payloadNonFirmato xmlns:t=\""+NAMESPACE_TEST+"\">contenuto del secondo Body</t:payloadNonFirmato>";
		private static final String ELEMENTO_ALTRO_NAMESPACE = "<t:estensione xmlns:t=\""+NAMESPACE_TEST+"\">estensione</t:estensione>";

		private static final Pattern ENVELOPE_PATTERN = Pattern.compile("<(?:([A-Za-z_][\\w.\\-]*):)?Envelope[\\s>/]");

		private String prologo;
		private String envelopeApertura;
		private String envelopeChiusura;
		private String prefisso; // comprensivo di ':' oppure vuoto
		private String header; // null se assente
		private String body;
		private String bodyApertura;
		private String bodyContent;

		private static Parti parse(String xml) {
			Parti p = new Parti();

			Matcher m = ENVELOPE_PATTERN.matcher(xml);
			if(!m.find()) {
				throw new IllegalArgumentException("Envelope SOAP non trovato nel messaggio");
			}
			int envStart = m.start();
			p.prefisso = m.group(1)!=null ? m.group(1)+":" : "";
			int envStartEnd = xml.indexOf('>', envStart)+1;
			p.prologo = xml.substring(0, envStart);
			p.envelopeApertura = xml.substring(envStart, envStartEnd);
			p.envelopeChiusura = "</"+p.prefisso+"Envelope>";
			int envEnd = xml.lastIndexOf(p.envelopeChiusura);
			if(envEnd<envStartEnd) {
				throw new IllegalArgumentException("Chiusura dell'Envelope SOAP non trovata nel messaggio");
			}
			String inner = xml.substring(envStartEnd, envEnd);

			int[] h = trovaElemento(inner, p.prefisso+"Header", 0);
			int from = 0;
			if(h!=null) {
				p.header = inner.substring(h[0], h[1]);
				from = h[1];
			}
			int[] b = trovaElemento(inner, p.prefisso+"Body", from);
			if(b==null) {
				throw new IllegalArgumentException("Body SOAP non trovato nel messaggio");
			}
			p.body = inner.substring(b[0], b[1]);
			int aperturaEnd = p.body.indexOf('>')+1;
			if(p.body.charAt(aperturaEnd-2)=='/') {
				p.bodyApertura = "<"+p.prefisso+"Body>";
				p.bodyContent = "";
			}
			else {
				p.bodyApertura = p.body.substring(0, aperturaEnd);
				p.bodyContent = p.body.substring(aperturaEnd, p.body.length()-("</"+p.prefisso+"Body>").length());
			}
			return p;
		}

		/** @return [inizio, fine) dell'elemento, gestendo elementi omonimi annidati e la forma auto-chiusa */
		private static int[] trovaElemento(String text, String qname, int from) {
			int start = indexOfApertura(text, qname, from);
			if(start<0) {
				return null;
			}
			int tagEnd = text.indexOf('>', start);
			if(text.charAt(tagEnd-1)=='/') {
				return new int[] {start, tagEnd+1};
			}
			String chiusura = "</"+qname+">";
			int depth = 1;
			int pos = tagEnd+1;
			while(depth>0) {
				int nextOpen = indexOfApertura(text, qname, pos);
				int nextClose = text.indexOf(chiusura, pos);
				if(nextClose<0) {
					throw new IllegalArgumentException("Chiusura dell'elemento '"+qname+"' non trovata");
				}
				if(nextOpen>=0 && nextOpen<nextClose) {
					int openEnd = text.indexOf('>', nextOpen);
					if(text.charAt(openEnd-1)!='/') {
						depth++;
					}
					pos = openEnd+1;
				}
				else {
					depth--;
					pos = nextClose+chiusura.length();
				}
			}
			return new int[] {start, pos};
		}
		private static int indexOfApertura(String text, String qname, int from) {
			String apertura = "<"+qname;
			int idx = text.indexOf(apertura, from);
			while(idx>=0) {
				int next = idx+apertura.length();
				if(next<text.length()) {
					char c = text.charAt(next);
					if(c=='>' || c=='/' || Character.isWhitespace(c)) {
						return idx;
					}
				}
				idx = text.indexOf(apertura, next);
			}
			return -1;
		}


		private String el(String localName, String contenuto) {
			if(contenuto==null) {
				return "<"+this.prefisso+localName+"/>";
			}
			return "<"+this.prefisso+localName+">"+contenuto+"</"+this.prefisso+localName+">";
		}
		private String header() {
			return this.header!=null ? this.header : el("Header", null);
		}
		private String headerConContenutoDuplicato() {
			if(this.header==null) {
				return el("Header", null);
			}
			int aperturaEnd = this.header.indexOf('>')+1;
			if(this.header.charAt(aperturaEnd-2)=='/') {
				return this.header;
			}
			String chiusura = "</"+this.prefisso+"Header>";
			String contenuto = this.header.substring(aperturaEnd, this.header.length()-chiusura.length());
			return this.header.substring(0, aperturaEnd)+contenuto+contenuto+chiusura;
		}
		private String headerConBlocco(String blocco) {
			if(this.header==null) {
				return el("Header", blocco);
			}
			int aperturaEnd = this.header.indexOf('>')+1;
			if(this.header.charAt(aperturaEnd-2)=='/') {
				return el("Header", blocco);
			}
			String chiusura = "</"+this.prefisso+"Header>";
			return this.header.substring(0, this.header.length()-chiusura.length())+blocco+chiusura;
		}
		private String secondoHeader() {
			return el("Header", "<t:altroHeader xmlns:t=\""+NAMESPACE_TEST+"\">contenuto del secondo Header</t:altroHeader>");
		}
		private String secondoBody() {
			return el("Body", PAYLOAD_SECONDO_BODY);
		}
		private String bodyCon(String contenuto) {
			return this.bodyApertura+contenuto+"</"+this.prefisso+"Body>";
		}
		private String fault() {
			return "<"+this.prefisso+"Fault xmlns:sf=\""+NAMESPACE_SOAP_11+"\"><faultcode>sf:Server</faultcode><faultstring>Fault simulato</faultstring></"+this.prefisso+"Fault>";
		}
		private String envelope(String ... figli) {
			return this.prologo + envelopeSenzaPrologo(figli);
		}
		private String envelopeSenzaPrologo(String ... figli) {
			StringBuilder sb = new StringBuilder(this.envelopeApertura);
			sb.append("\n");
			if(figli!=null) {
				for (String f : figli) {
					sb.append(f).append("\n");
				}
			}
			sb.append(this.envelopeChiusura);
			return sb.toString();
		}
	}
}
