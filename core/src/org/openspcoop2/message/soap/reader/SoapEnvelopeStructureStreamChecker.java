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

package org.openspcoop2.message.soap.reader;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.exception.SoapEnvelopeStructureException;
import org.openspcoop2.message.soap.SoapEnvelopeStructureValidator;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;

/**
 * Verifica della struttura dell'Envelope SOAP sui byte del messaggio, man mano che vengono letti, senza costruire il DOM
 * e senza un parser XML: le regole e i messaggi di errore sono quelli di {@link SoapEnvelopeStructureValidator}.
 *
 * Vengono interpretati solo i tag dell'Envelope, dei suoi figli diretti e dei figli diretti del Body; il contenuto
 * applicativo più interno viene solo attraversato (conteggio della profondità, commenti, CDATA, istruzioni di elaborazione).
 * L'anomalia viene segnalata appena incontrata, prima di elaborare i byte successivi: chi inoltra il messaggio in streaming
 * può quindi interrompere l'invio senza consegnare la parte non conforme.
 *
 * Il controllo non verifica che l'XML sia ben formato (compito del parser del destinatario) e, quando non è in grado di
 * interpretare il contenuto con certezza, si disattiva senza segnalare errori (nessun falso positivo):
 * charset non compatibile con ASCII (es. UTF-16), DOCTYPE, prefissi non dichiarati, entità nelle dichiarazioni dei namespace o
 * nel testo figlio dell'Envelope, tag dell'Envelope o dei suoi figli più grandi di {@link #MAX_TAG_LENGTH} byte,
 * radice diversa dall'Envelope SOAP atteso, parte MIME radice non trovata o codificata (base64, quoted-printable).
 *
 * Messaggi multipart: viene verificata la sola parte radice, indicata dal parametro 'start' del Content-Type o, in sua assenza,
 * la prima parte; il controllo termina alla chiusura dell'Envelope (gli attachment non vengono analizzati).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapEnvelopeStructureStreamChecker {

	/** Dimensione massima di un tag dell'Envelope, dei suoi figli diretti o dei figli diretti del Body */
	public static final int MAX_TAG_LENGTH = 64 * 1024;
	/** Dimensione massima degli header di una parte MIME */
	private static final int MAX_MIME_HEADERS_LENGTH = 16 * 1024;

	private static final String HEADER = "Header";
	private static final String BODY = "Body";
	private static final String FAULT = "Fault";
	private static final String ENVELOPE = "Envelope";

	private enum Stato {
		MIME_CERCA_BOUNDARY, MIME_HEADER,
		TESTO, TAG_INIZIO, TAG_APERTURA, TAG_CHIUSURA, DICHIARAZIONE, COMMENTO, CDATA, ISTRUZIONE,
		CONCLUSO, INATTIVO, ERRORE
	}

	private final String soapNamespaceAtteso;
	private final boolean soap11OtherNamespaceElementsAfterBodyAllowed;

	private Stato stato;
	private SoapEnvelopeStructureException errore;

	// multipart
	private boolean multipart = false;
	private byte[] delimitatore;
	private int[] delimitatoreFallimento;
	private int delimitatoreMatch = 0;
	private String startContentId;
	private boolean primaParte = true;
	private StringBuilder mimeHeaders;

	// xml
	private boolean inizioXml = true;
	private int bomLetti = 0;
	private int profondita = 0;
	private boolean rootAperta = false;
	private boolean rootChiusa = false;
	private String soapNamespace;
	private Map<String, String> nsEnvelope;
	private Map<String, String> nsFiglioEnvelope;
	private boolean figlioEnvelopeIsBody = false;

	private int headers = 0;
	private int bodies = 0;
	private int elementiBody = 0;
	private int faultBody = 0;

	private byte[] tag = new byte[256];
	private int tagLength = 0;
	private boolean tagMemorizzato = false;
	private byte tagQuote = 0;
	private byte tagUltimoSignificativo = 0;
	private final StringBuilder dichiarazione = new StringBuilder();
	private byte prec1 = 0;
	private byte prec2 = 0;

	/**
	 * @param soapNamespaceAtteso namespace dell'Envelope atteso (SOAP 1.1 o 1.2); se null sono accettati entrambi
	 * @param contentType Content-Type del messaggio (charset, multipart)
	 * @param soap11OtherNamespaceElementsAfterBodyAllowed vedi {@link SoapEnvelopeStructureValidator#isSoap11OtherNamespaceElementsAfterBodyAllowed()}
	 */
	public SoapEnvelopeStructureStreamChecker(String soapNamespaceAtteso, String contentType, boolean soap11OtherNamespaceElementsAfterBodyAllowed) {
		this.soapNamespaceAtteso = soapNamespaceAtteso;
		this.soap11OtherNamespaceElementsAfterBodyAllowed = soap11OtherNamespaceElementsAfterBodyAllowed;
		this.stato = Stato.TESTO;
		try {
			if(contentType!=null && ContentTypeUtilities.isMultipartType(contentType)) {
				initMultipart(contentType);
			}
			else if(contentType!=null && !isCharsetCompatibile(ContentTypeUtilities.readCharsetFromContentType(contentType))) {
				this.stato = Stato.INATTIVO;
			}
		}catch(Exception e) {
			this.stato = Stato.INATTIVO;
		}
	}
	private void initMultipart(String contentType) throws UtilsException {
		String boundary = ContentTypeUtilities.readMultipartBoundaryFromContentType(contentType);
		if(boundary==null || boundary.isEmpty()) {
			this.stato = Stato.INATTIVO;
			return;
		}
		String internalContentType = ContentTypeUtilities.getInternalMultipartContentType(contentType);
		if(internalContentType!=null && !isCharsetCompatibile(ContentTypeUtilities.readCharsetFromContentType(internalContentType))) {
			this.stato = Stato.INATTIVO;
			return;
		}
		this.multipart = true;
		this.startContentId = normalizzaContentId(ContentTypeUtilities.readMultipartStartFromContentType(contentType));
		this.delimitatore = ("--"+boundary).getBytes(StandardCharsets.ISO_8859_1);
		this.delimitatoreFallimento = buildTabellaFallimento(this.delimitatore);
		this.stato = Stato.MIME_CERCA_BOUNDARY;
	}

	/**
	 * Charset in cui i caratteri del markup XML sono codificati con un solo byte uguale al codice ASCII.
	 */
	private static boolean isCharsetCompatibile(String charset) {
		if(charset==null || charset.trim().isEmpty()) {
			return true;
		}
		String c = charset.trim().toLowerCase(Locale.ROOT);
		return c.startsWith("utf-8") || c.equals("utf8") || c.contains("ascii") ||
				c.startsWith("iso-8859") || c.startsWith("iso8859") || c.startsWith("latin") ||
				c.startsWith("windows-125") || c.startsWith("cp125");
	}
	private static String normalizzaContentId(String id) {
		if(id==null) {
			return null;
		}
		String s = id.trim();
		if(s.startsWith("<") && s.endsWith(">") && s.length()>=2) {
			s = s.substring(1, s.length()-1).trim();
		}
		return s.isEmpty() ? null : s;
	}
	private static int[] buildTabellaFallimento(byte[] pattern) {
		int[] f = new int[pattern.length];
		int k = 0;
		for (int i = 1; i < pattern.length; i++) {
			while(k>0 && pattern[i]!=pattern[k]) {
				k = f[k-1];
			}
			if(pattern[i]==pattern[k]) {
				k++;
			}
			f[i] = k;
		}
		return f;
	}


	// ---- Stato ----

	/**
	 * @return false se il controllo si è disattivato perché non in grado di interpretare il contenuto con certezza
	 */
	public boolean isActive() {
		return this.stato!=Stato.INATTIVO;
	}
	/**
	 * @return true se l'Envelope è stato verificato interamente (fino alla sua chiusura) senza anomalie
	 */
	public boolean isCompleted() {
		return this.errore==null && this.stato!=Stato.INATTIVO && this.rootChiusa;
	}
	/**
	 * @return l'anomalia rilevata, o null
	 */
	public SoapEnvelopeStructureException getError() {
		return this.errore;
	}


	// ---- Analisi ----

	public void update(byte[] b, int off, int len) throws SoapEnvelopeStructureException {
		for (int i = off; i < off+len; i++) {
			if(this.stato==Stato.INATTIVO || this.stato==Stato.CONCLUSO) {
				return;
			}
			if(this.stato==Stato.ERRORE) {
				throw this.errore;
			}
			analizza(b[i]);
		}
	}
	public void update(int b) throws SoapEnvelopeStructureException {
		update(new byte[] {(byte)b}, 0, 1);
	}

	/**
	 * Fine del messaggio: segnala l'assenza del Body se l'Envelope è stato chiuso senza contenerlo.
	 * Un messaggio troncato (Envelope non chiuso) non viene segnalato: non è un'anomalia della struttura ma XML non ben formato.
	 */
	public void end() throws SoapEnvelopeStructureException {
		if(this.stato==Stato.ERRORE) {
			throw this.errore;
		}
	}

	private void errore(String msg) throws SoapEnvelopeStructureException {
		this.errore = new SoapEnvelopeStructureException(msg);
		this.stato = Stato.ERRORE;
		throw this.errore;
	}
	private void disattiva() {
		this.stato = Stato.INATTIVO;
	}

	private static boolean isSpazio(byte c) {
		return c==' ' || c=='\t' || c=='\r' || c=='\n';
	}

	private void analizza(byte c) throws SoapEnvelopeStructureException {
		switch (this.stato) {
		case MIME_CERCA_BOUNDARY:
			analizzaCercaBoundary(c);
			break;
		case MIME_HEADER:
			analizzaMimeHeader(c);
			break;
		case TESTO:
			analizzaTesto(c);
			break;
		case TAG_INIZIO:
			analizzaTagInizio(c);
			break;
		case TAG_APERTURA:
			analizzaTagApertura(c);
			break;
		case TAG_CHIUSURA:
			if(c=='>') {
				chiusuraElemento();
			}
			break;
		case DICHIARAZIONE:
			analizzaDichiarazione(c);
			break;
		case COMMENTO:
			if(c=='>' && this.prec1=='-' && this.prec2=='-') {
				this.stato = Stato.TESTO;
			}
			break;
		case CDATA:
			analizzaCdata(c);
			break;
		case ISTRUZIONE:
			if(c=='>' && this.prec1=='?') {
				this.stato = Stato.TESTO;
			}
			break;
		default:
			break;
		}
		this.prec2 = this.prec1;
		this.prec1 = c;
	}


	// ---- MIME ----

	private void analizzaCercaBoundary(byte c) {
		while(this.delimitatoreMatch>0 && c!=this.delimitatore[this.delimitatoreMatch]) {
			this.delimitatoreMatch = this.delimitatoreFallimento[this.delimitatoreMatch-1];
		}
		if(c==this.delimitatore[this.delimitatoreMatch]) {
			this.delimitatoreMatch++;
		}
		if(this.delimitatoreMatch==this.delimitatore.length) {
			this.delimitatoreMatch = 0;
			this.mimeHeaders = new StringBuilder();
			this.stato = Stato.MIME_HEADER;
		}
	}

	private void analizzaMimeHeader(byte c) {
		this.mimeHeaders.append((char)(c & 0xFF));
		int l = this.mimeHeaders.length();
		if(l>MAX_MIME_HEADERS_LENGTH || (l==2 && "--".contentEquals(this.mimeHeaders))) {
			// header troppo grandi, o delimitatore di chiusura (parte radice non trovata)
			disattiva();
			return;
		}
		if(isFineMimeHeaders()) {
			analizzaParte(HeaderParte.parse(this.mimeHeaders.toString()));
		}
	}
	/** @return true se gli header della parte sono terminati (riga vuota) */
	private boolean isFineMimeHeaders() {
		int l = this.mimeHeaders.length();
		if(l<2 || this.mimeHeaders.charAt(l-1)!='\n') {
			return false;
		}
		return this.mimeHeaders.charAt(l-2)=='\n' ||
				(l>=4 && this.mimeHeaders.charAt(l-2)=='\r' && this.mimeHeaders.charAt(l-3)=='\n' && this.mimeHeaders.charAt(l-4)=='\r');
	}
	private void analizzaParte(HeaderParte header) {
		boolean radice = this.startContentId!=null ? this.startContentId.equals(header.contentId) : this.primaParte;
		this.primaParte = false;
		if(!radice) {
			this.stato = Stato.MIME_CERCA_BOUNDARY;
		}
		else if(header.isContenutoInterpretabile()) {
			this.stato = Stato.TESTO;
		}
		else {
			disattiva();
		}
	}

	/** Header di una parte MIME rilevanti per il controllo */
	private static class HeaderParte {
		private String contentId;
		private String transferEncoding;
		private String contentType;

		private static HeaderParte parse(String headers) {
			HeaderParte h = new HeaderParte();
			for (String riga : headers.split("\r?\n")) {
				int sep = riga.indexOf(':');
				if(sep>0) {
					h.add(riga.substring(0, sep).trim().toLowerCase(Locale.ROOT), riga.substring(sep+1).trim());
				}
			}
			return h;
		}
		private void add(String nome, String valore) {
			if("content-id".equals(nome)) {
				this.contentId = normalizzaContentId(valore);
			}
			else if("content-transfer-encoding".equals(nome)) {
				this.transferEncoding = valore.toLowerCase(Locale.ROOT);
			}
			else if("content-type".equals(nome)) {
				this.contentType = valore;
			}
		}
		/** @return false se il contenuto della parte è codificato (base64, quoted-printable) o ha un charset non compatibile con ASCII */
		private boolean isContenutoInterpretabile() {
			if(this.transferEncoding!=null && (this.transferEncoding.contains("base64") || this.transferEncoding.contains("quoted-printable"))) {
				return false;
			}
			if(this.contentType!=null) {
				try {
					return isCharsetCompatibile(ContentTypeUtilities.readCharsetFromContentType(this.contentType));
				}catch(Exception e) {
					// content-type della parte non interpretabile: si prosegue con il charset del messaggio
				}
			}
			return true;
		}
	}


	// ---- XML ----

	private void analizzaTesto(byte c) throws SoapEnvelopeStructureException {
		if(this.inizioXml && !this.rootAperta && verificaBom(c)) {
			return;
		}
		this.inizioXml = false;
		if(c=='<') {
			this.stato = Stato.TAG_INIZIO;
			return;
		}
		if(isSpazio(c)) {
			return;
		}
		testoNonVuoto(c);
	}
	/** @return true se il byte fa parte del BOM UTF-8 iniziale (da ignorare) */
	private boolean verificaBom(byte c) {
		int u = c & 0xFF;
		if(this.bomLetti==0) {
			if(u==0xEF) {
				this.bomLetti = 1;
				return true;
			}
			if(u==0xFE || u==0xFF || u==0x00) {
				// BOM UTF-16/UTF-32
				disattiva();
				return true;
			}
			return false;
		}
		if(this.bomLetti>=3) {
			return false;
		}
		if((this.bomLetti==1 && u==0xBB) || (this.bomLetti==2 && u==0xBF)) {
			this.bomLetti++;
			return true;
		}
		disattiva();
		return true;
	}
	private void testoNonVuoto(byte c) throws SoapEnvelopeStructureException {
		if(this.profondita==1) {
			if(c=='&') {
				// riferimento a entità: potrebbe rappresentare uno spazio
				disattiva();
				return;
			}
			errore(SoapEnvelopeStructureValidator.ERROR_UNEXPECTED_TEXT);
		}
		else if(this.profondita==0) {
			if(this.rootChiusa) {
				errore(SoapEnvelopeStructureValidator.ERROR_CONTENT_AFTER_ENVELOPE);
			}
			else {
				// contenuto prima della radice: non si tratta di un Envelope interpretabile
				disattiva();
			}
		}
		// testo nel Body o più in profondità: non rilevante
	}

	private void analizzaTagInizio(byte c) throws SoapEnvelopeStructureException {
		switch (c) {
		case '/' -> this.stato = Stato.TAG_CHIUSURA;
		case '!' -> {
			this.dichiarazione.setLength(0);
			this.stato = Stato.DICHIARAZIONE;
		}
		case '?' -> this.stato = Stato.ISTRUZIONE;
		default -> {
			if(this.profondita==0 && this.rootChiusa) {
				errore(SoapEnvelopeStructureValidator.ERROR_CONTENT_AFTER_ENVELOPE);
			}
			this.tagMemorizzato = this.profondita<=1 || (this.profondita==2 && this.figlioEnvelopeIsBody);
			this.tagLength = 0;
			this.tagQuote = 0;
			this.tagUltimoSignificativo = 0;
			this.stato = Stato.TAG_APERTURA;
			analizzaTagApertura(c);
		}
		}
	}

	private void analizzaDichiarazione(byte c) {
		this.dichiarazione.append((char)(c & 0xFF));
		String d = this.dichiarazione.toString();
		if("--".equals(d)) {
			this.stato = Stato.COMMENTO;
		}
		else if("[CDATA[".equals(d)) {
			this.stato = Stato.CDATA;
		}
		else if(!"--".startsWith(d) && !"[CDATA[".startsWith(d)) {
			// DOCTYPE (non ammesso in SOAP) o dichiarazione non prevista
			disattiva();
		}
	}

	private void analizzaCdata(byte c) throws SoapEnvelopeStructureException {
		if(c=='>' && this.prec1==']' && this.prec2==']') {
			this.stato = Stato.TESTO;
			return;
		}
		if(c!=']' && !isSpazio(c) && this.profondita<=1) {
			testoNonVuoto(c);
		}
	}

	private void analizzaTagApertura(byte c) throws SoapEnvelopeStructureException {
		if(this.tagQuote!=0) {
			if(c==this.tagQuote) {
				this.tagQuote = 0;
			}
		}
		else if(c=='"' || c=='\'') {
			this.tagQuote = c;
		}
		else if(c=='>') {
			boolean autoChiuso = this.tagUltimoSignificativo=='/';
			this.stato = Stato.TESTO;
			aperturaElemento(autoChiuso);
			return;
		}
		if(this.tagQuote==0 && !isSpazio(c)) {
			this.tagUltimoSignificativo = c;
		}
		if(this.tagMemorizzato) {
			if(this.tagLength>=MAX_TAG_LENGTH) {
				disattiva();
				return;
			}
			if(this.tagLength==this.tag.length) {
				byte[] n = new byte[Math.min(this.tag.length*2, MAX_TAG_LENGTH)];
				System.arraycopy(this.tag, 0, n, 0, this.tagLength);
				this.tag = n;
			}
			this.tag[this.tagLength++] = c;
		}
	}

	private void aperturaElemento(boolean autoChiuso) throws SoapEnvelopeStructureException {
		int livello = this.profondita;
		if(!autoChiuso) {
			this.profondita++;
		}
		if(!this.tagMemorizzato) {
			return;
		}
		Tag t = Tag.parse(new String(this.tag, 0, this.tagLength, StandardCharsets.ISO_8859_1));
		if(t==null) {
			disattiva();
			return;
		}
		switch (livello) {
		case 0:
			aperturaEnvelope(t, autoChiuso);
			break;
		case 1:
			aperturaFiglioEnvelope(t, autoChiuso);
			break;
		default:
			aperturaFiglioBody(t);
			break;
		}
	}

	private void aperturaEnvelope(Tag t, boolean autoChiuso) throws SoapEnvelopeStructureException {
		if(!t.risolviNamespace(null, null)) {
			disattiva();
			return;
		}
		String ns = t.namespace;
		if(!ENVELOPE.equals(t.localName) ||
				(this.soapNamespaceAtteso!=null ? !this.soapNamespaceAtteso.equals(ns) :
					!(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(ns) || Costanti.SOAP12_ENVELOPE_NAMESPACE.equals(ns)))) {
			disattiva();
			return;
		}
		this.soapNamespace = ns;
		this.nsEnvelope = t.dichiarazioni;
		this.rootAperta = true;
		if(autoChiuso) {
			chiusuraEnvelope();
		}
	}

	private void aperturaFiglioEnvelope(Tag t, boolean autoChiuso) throws SoapEnvelopeStructureException {
		if(!t.risolviNamespace(this.nsEnvelope, null)) {
			disattiva();
			return;
		}
		String ns = t.namespace;
		this.nsFiglioEnvelope = t.dichiarazioni;
		this.figlioEnvelopeIsBody = false;
		if(this.soapNamespace.equals(ns)) {
			aperturaFiglioEnvelopeSoap(t.localName, autoChiuso);
		}
		else if(this.bodies==0) {
			errore(SoapEnvelopeStructureValidator.ERROR_UNEXPECTED_ENVELOPE_CHILD+" '"+toQName(ns, t.localName)+"'");
		}
		else if(!(this.soap11OtherNamespaceElementsAfterBodyAllowed && Costanti.SOAP_ENVELOPE_NAMESPACE.equals(this.soapNamespace))) {
			errore(SoapEnvelopeStructureValidator.ERROR_ELEMENT_AFTER_BODY+" '"+toQName(ns, t.localName)+"'");
		}
	}

	private void aperturaFiglioEnvelopeSoap(String localName, boolean autoChiuso) throws SoapEnvelopeStructureException {
		if(HEADER.equals(localName)) {
			if(this.bodies>0) {
				errore(SoapEnvelopeStructureValidator.ERROR_HEADER_AFTER_BODY);
			}
			this.headers++;
			if(this.headers>1) {
				errore(SoapEnvelopeStructureValidator.ERROR_MORE_THAN_ONE_HEADER+" ("+this.headers+")");
			}
		}
		else if(BODY.equals(localName)) {
			this.bodies++;
			if(this.bodies>1) {
				errore(SoapEnvelopeStructureValidator.ERROR_MORE_THAN_ONE_BODY+" ("+this.bodies+")");
			}
			this.figlioEnvelopeIsBody = !autoChiuso;
		}
		else {
			errore(SoapEnvelopeStructureValidator.ERROR_UNEXPECTED_ENVELOPE_CHILD+" '"+toQName(this.soapNamespace, localName)+"'");
		}
	}

	private void aperturaFiglioBody(Tag t) throws SoapEnvelopeStructureException {
		if(!t.risolviNamespace(this.nsFiglioEnvelope, this.nsEnvelope)) {
			disattiva();
			return;
		}
		String ns = t.namespace;
		this.elementiBody++;
		if(this.soapNamespace.equals(ns) && FAULT.equals(t.localName)) {
			this.faultBody++;
		}
		if(this.faultBody>1) {
			errore(SoapEnvelopeStructureValidator.ERROR_MORE_THAN_ONE_FAULT+" ("+this.faultBody+")");
		}
		if(this.faultBody==1 && this.elementiBody>1) {
			errore(SoapEnvelopeStructureValidator.ERROR_FAULT_NOT_ONLY_CHILD);
		}
	}

	private void chiusuraElemento() throws SoapEnvelopeStructureException {
		this.stato = Stato.TESTO;
		this.profondita--;
		if(this.profondita<0) {
			disattiva();
		}
		else if(this.profondita==1) {
			this.figlioEnvelopeIsBody = false;
			this.nsFiglioEnvelope = null;
		}
		else if(this.profondita==0 && this.rootAperta) {
			chiusuraEnvelope();
		}
	}

	private void chiusuraEnvelope() throws SoapEnvelopeStructureException {
		this.rootChiusa = true;
		if(this.bodies==0) {
			errore(SoapEnvelopeStructureValidator.ERROR_BODY_NOT_FOUND);
		}
		if(this.multipart) {
			// gli attachment che seguono non fanno parte dell'Envelope
			this.stato = Stato.CONCLUSO;
		}
	}

	private static String toQName(String namespace, String localName) {
		return namespace!=null ? "{"+namespace+"}"+localName : localName;
	}


	/** Nome e dichiarazioni dei namespace di un tag di apertura */
	private static class Tag {

		private String prefix;
		private String localName;
		private String namespace;
		private Map<String, String> dichiarazioni = null;

		/**
		 * @param s contenuto del tag di apertura, senza '<' e '>'
		 * @return il tag o null se non interpretabile
		 */
		private static Tag parse(String s) {
			int l = s.length();
			int i = 0;
			while(i<l && !isSpazioChar(s.charAt(i)) && s.charAt(i)!='/') {
				i++;
			}
			String qname = s.substring(0, i);
			if(qname.isEmpty()) {
				return null;
			}
			Tag t = new Tag();
			int sep = qname.indexOf(':');
			t.prefix = sep>0 ? qname.substring(0, sep) : "";
			t.localName = sep>0 ? qname.substring(sep+1) : qname;
			while(i>=0 && i<l) {
				i = t.parseAttributo(s, i);
			}
			return i<0 ? null : t;
		}
		/**
		 * Legge l'attributo che inizia (dopo eventuali spazi) alla posizione indicata, registrando le dichiarazioni dei namespace.
		 * @return la posizione successiva all'attributo, o -1 se non interpretabile
		 */
		private int parseAttributo(String s, int from) {
			int l = s.length();
			int i = saltaSpazi(s, from, true);
			if(i>=l) {
				return l;
			}
			int fineNome = getFineNome(s, i);
			String nome = s.substring(i, fineNome);
			int inizioValore = getInizioValore(s, fineNome);
			if(inizioValore<0) {
				return -1;
			}
			int fineValore = s.indexOf(s.charAt(inizioValore), inizioValore+1);
			if(fineValore<0) {
				return -1;
			}
			return registraDichiarazione(nome, s.substring(inizioValore+1, fineValore)) ? fineValore+1 : -1;
		}
		private static int getFineNome(String s, int from) {
			int i = from;
			while(i<s.length() && s.charAt(i)!='=' && !isSpazioChar(s.charAt(i))) {
				i++;
			}
			return i;
		}
		/** @return la posizione dell'apice che apre il valore dell'attributo, o -1 se non interpretabile */
		private static int getInizioValore(String s, int fineNome) {
			int l = s.length();
			int i = saltaSpazi(s, fineNome, false);
			if(i>=l || s.charAt(i)!='=') {
				return -1;
			}
			i = saltaSpazi(s, i+1, false);
			if(i>=l || (s.charAt(i)!='"' && s.charAt(i)!='\'')) {
				return -1;
			}
			return i;
		}
		/** @return false se la dichiarazione di namespace non è interpretabile */
		private boolean registraDichiarazione(String nome, String valore) {
			if(!"xmlns".equals(nome) && !nome.startsWith("xmlns:")) {
				return true; // attributo non rilevante
			}
			if(valore.indexOf('&')>=0) {
				return false; // entità non risolte: namespace non confrontabile
			}
			if(this.dichiarazioni==null) {
				this.dichiarazioni = new HashMap<>();
			}
			this.dichiarazioni.put("xmlns".equals(nome) ? "" : nome.substring("xmlns:".length()), valore);
			return true;
		}
		private static int saltaSpazi(String s, int from, boolean ancheSlash) {
			int i = from;
			while(i<s.length() && (isSpazioChar(s.charAt(i)) || (ancheSlash && s.charAt(i)=='/'))) {
				i++;
			}
			return i;
		}
		private static boolean isSpazioChar(char c) {
			return c==' ' || c=='\t' || c=='\r' || c=='\n';
		}

		/**
		 * Risolve il namespace dell'elemento con le dichiarazioni del tag e, in ordine, con quelle degli antenati.
		 * @return false se il prefisso non è dichiarato (XML non ben formato)
		 */
		private boolean risolviNamespace(Map<String, String> padre, Map<String, String> nonno) {
			String ns = cerca(this.dichiarazioni);
			if(ns==null) {
				ns = cerca(padre);
			}
			if(ns==null) {
				ns = cerca(nonno);
			}
			if(ns==null) {
				// elemento senza prefisso e senza namespace di default: nessun namespace
				this.namespace = null;
				return this.prefix.isEmpty();
			}
			this.namespace = ns.isEmpty() ? null : ns; // xmlns="" annulla il namespace di default
			return true;
		}
		private String cerca(Map<String, String> m) {
			return m!=null ? m.get(this.prefix) : null;
		}
	}
}
