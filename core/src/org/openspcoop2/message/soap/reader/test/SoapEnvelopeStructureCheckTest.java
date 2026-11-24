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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.SoapEnvelopeStructureException;
import org.openspcoop2.message.exception.SoapEnvelopeStructureIOException;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_soap_impl;
import org.openspcoop2.message.soap.SoapEnvelopeStructureValidator;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.message.soap.reader.SoapEnvelopeStructureCheckInputStream;
import org.openspcoop2.message.soap.reader.SoapEnvelopeStructureStreamChecker;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * Test del controllo della struttura dell'Envelope SOAP in streaming:
 * - {@link SoapEnvelopeStructureStreamChecker}: anomalie rilevate (intero messaggio, a blocchi, un byte alla volta), casi in cui
 *   il controllo si disattiva, multipart, opzioni;
 * - {@link SoapEnvelopeStructureCheckInputStream}: interruzione prima dei byte anomali, lettura completa dello stream di origine
 *   (dump), registrazione dell'errore di parsing sul messaggio, metodi di lettura;
 * - verifica durante la serializzazione del messaggio (controllo durante l'inoltro).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapEnvelopeStructureCheckTest {

	public static void main(String[] args) throws Exception {
		test();
	}

	public static void test() throws Exception {
		for (MessageType messageType : new MessageType[] {MessageType.SOAP_11, MessageType.SOAP_12}) {
			System.out.println("\n\n*** TEST Controllo struttura Envelope "+messageType+" ***");
			for (StrutturaEnvelopeCasi.Caso caso : StrutturaEnvelopeCasi.getCasi(messageType)) {
				testCaso(messageType, caso);
			}
			for (StrutturaEnvelopeCasi.Caso caso : StrutturaEnvelopeCasi.getCasiMultipart(messageType)) {
				testCaso(messageType, caso);
			}
			testControlloInattivo(messageType);
			testOltreSoglia(messageType);
			testStatoDopoErrore(messageType);
			testEnvelopeTroncato(messageType);
			testNamespaceAtteso(messageType);
			testElementiDopoBodyAmmessi(messageType);
			testMetodiStream(messageType);
			testSerializzazioneMessaggio(messageType);
		}
		System.out.println("\n\nTestsuite completata con successo");
	}


	// ---- Controllo ----

	private static String getNamespace(String contentType, MessageType messageType) {
		return contentType.contains("multipart") ? StrutturaEnvelopeCasi.getNamespace(messageType) :
			(contentType.startsWith(HttpConstants.CONTENT_TYPE_SOAP_1_2) ? StrutturaEnvelopeCasi.getNamespace(MessageType.SOAP_12) : StrutturaEnvelopeCasi.getNamespace(MessageType.SOAP_11));
	}

	/**
	 * Verifica il controllo alimentato con blocchi della dimensione indicata (0 = intero messaggio)
	 * @return l'anomalia rilevata o null
	 */
	private static String verifica(byte[] bytes, String namespace, String contentType, boolean soap11ElementiDopoBody, int blocco, boolean[] completo) {
		SoapEnvelopeStructureStreamChecker checker = new SoapEnvelopeStructureStreamChecker(namespace, contentType, soap11ElementiDopoBody);
		int dim = blocco<=0 ? bytes.length : blocco;
		try {
			for (int i = 0; i < bytes.length; i+=dim) {
				checker.update(bytes, i, Math.min(dim, bytes.length-i));
			}
			checker.end();
		}catch(SoapEnvelopeStructureException e) {
			if(!e.getMessage().startsWith(SoapEnvelopeStructureException.PREFIX)) {
				throw new IllegalStateException("Messaggio di errore senza prefisso: "+e.getMessage());
			}
			if(checker.getError()!=e || checker.isCompleted()) {
				throw new IllegalStateException("Stato del controllo non coerente dopo l'anomalia");
			}
			return e.getMessage();
		}
		if(completo!=null) {
			completo[0] = checker.isCompleted();
		}
		return null;
	}

	private static void testCaso(MessageType messageType, StrutturaEnvelopeCasi.Caso caso) throws Exception {
		String descrizione = caso.getDescrizione();
		System.out.println("- "+descrizione+" ...");
		byte[] bytes = caso.getMessaggio().getBytes(Charset.UTF_8.getValue());
		String ns = getNamespace(caso.getContentType(), messageType);
		String errore = null;
		for (int blocco : new int[] {0, 512, 7, 1}) {
			boolean[] completo = new boolean[1];
			errore = verifica(bytes, ns, caso.getContentType(), false, blocco, completo);
			ReaderTest.verificaErrore(descrizione, "controllo (blocchi da "+blocco+" byte)", errore, caso.getErroreCompleto());
			if(errore==null && !completo[0]) {
				throw new Exception("["+descrizione+"] controllo (blocchi da "+blocco+" byte): messaggio corretto non verificato interamente");
			}
		}
		for (int blocco : new int[] {512, 1}) {
			String erroreInoltro = inoltra(descrizione, bytes, ns, caso.getContentType(), blocco, null);
			ReaderTest.verificaErrore(descrizione, "inoltro (blocchi da "+blocco+" byte)", erroreInoltro, caso.getErroreCompleto());
		}
		System.out.println("- "+descrizione+" ok"+(errore!=null ? " ("+errore+")" : ""));
	}

	/**
	 * Legge il messaggio attraverso lo stream di verifica usato durante l'inoltro.
	 * @return l'anomalia rilevata o null
	 */
	private static String inoltra(String descrizione, byte[] bytes, String namespace, String contentType, int blocco, OpenSPCoop2Message msg) throws Exception {
		ByteArrayInputStream sorgente = new ByteArrayInputStream(bytes);
		SoapEnvelopeStructureCheckInputStream is = new SoapEnvelopeStructureCheckInputStream(sorgente,
				new SoapEnvelopeStructureStreamChecker(namespace, contentType, false), msg);
		ByteArrayOutputStream inoltrati = new ByteArrayOutputStream();
		byte[] buffer = new byte[blocco];
		String errore = null;
		try {
			int letti = 0;
			while((letti=is.read(buffer))!=-1) {
				inoltrati.write(buffer, 0, letti);
			}
		}catch(SoapEnvelopeStructureIOException e) {
			errore = e.getMessage();
			if(e.getStructureException()==null || !errore.equals(e.getStructureException().getMessage())) {
				throw new Exception("["+descrizione+"] inoltro: causa dell'eccezione non coerente");
			}
			// letture successive: stessa anomalia
			try {
				is.read(buffer);
				throw new Exception("["+descrizione+"] inoltro: attesa eccezione anche sulle letture successive all'anomalia");
			}catch(SoapEnvelopeStructureIOException eSuccessiva) {
				// atteso
			}
		}
		// lo stream di origine deve essere consumato interamente (es. dump del messaggio ricevuto completo)
		if(sorgente.available()>0) {
			throw new Exception("["+descrizione+"] inoltro: stream di origine non consumato interamente ("+sorgente.available()+" byte residui)");
		}
		if(errore==null && !Arrays.equals(bytes, inoltrati.toByteArray())) {
			throw new Exception("["+descrizione+"] inoltro: messaggio inoltrato differente da quello originale");
		}
		if(errore!=null) {
			// la parte inoltrata deve essere l'inizio del messaggio, senza il blocco contenente l'anomalia
			byte[] parte = inoltrati.toByteArray();
			if(parte.length>=bytes.length || !Arrays.equals(parte, Arrays.copyOf(bytes, parte.length))) {
				throw new Exception("["+descrizione+"] inoltro: parte inoltrata ("+parte.length+" byte) non coerente con l'interruzione all'anomalia");
			}
			// con un byte alla volta l'interruzione avviene esattamente all'anomalia: verifico che quanto inoltrato sia corretto
			if(blocco==1) {
				String nsCheck = namespace;
				String erroreParte = verifica(parte, nsCheck, contentType, false, 0, null);
				if(erroreParte!=null) {
					throw new Exception("["+descrizione+"] inoltro: la parte inoltrata contiene già l'anomalia: "+erroreParte);
				}
			}
		}
		return errore;
	}

	private static void testControlloInattivo(MessageType messageType) throws Exception {
		StrutturaEnvelopeCasi.Parti p = new StrutturaEnvelopeCasi.Parti(messageType);
		String ct = StrutturaEnvelopeCasi.getContentType(messageType);
		String baseCt = StrutturaEnvelopeCasi.getBaseContentType(messageType);
		String ns = p.ns;
		String errato = p.envelope(p.h, p.b, p.b2);
		testControlloInattivo("entità nel testo figlio dell'envelope", p.envelope(p.h, p.b, "&#32;", p.b2), ns, ct);
		testControlloInattivo("charset UTF-16", errato, ns, baseCt+"; charset=UTF-16");
		byte[] erratoBytes = errato.getBytes(Charset.UTF_8.getValue());
		byte[] bomUtf16 = new byte[erratoBytes.length+2];
		bomUtf16[0] = (byte)0xFE;
		bomUtf16[1] = (byte)0xFF;
		System.arraycopy(erratoBytes, 0, bomUtf16, 2, erratoBytes.length);
		testControlloInattivo("BOM UTF-16", bomUtf16, ns, ct);
		testControlloInattivo("prefisso non dichiarato", p.envelope(p.h, p.b, "<x:Body>x</x:Body>", p.b2), ns, ct);
		testControlloInattivo("dichiarazione DOCTYPE", "<!DOCTYPE x>"+errato, ns, ct);
		testControlloInattivo("testo prima dell'envelope", "testo"+errato, ns, ct);
		testControlloInattivo("radice diversa dall'envelope", "<a>"+errato+"</a>", ns, ct);
		testControlloInattivo("envelope dell'altra versione soap", errato.replace(ns, p.nsAltro), ns, ct);
		StringBuilder attributo = new StringBuilder();
		while(attributo.length() <= SoapEnvelopeStructureStreamChecker.MAX_TAG_LENGTH) {
			attributo.append("valore lungo ");
		}
		testControlloInattivo("tag dell'envelope oltre la dimensione massima", errato.replaceFirst("<soap:Envelope ", "<soap:Envelope a=\""+attributo+"\" "), ns, ct);
		
		String type = baseCt;
		String boundary = "----=_Part_Struttura_Envelope";
		String ctMultipart = HttpConstants.CONTENT_TYPE_MULTIPART_RELATED+"; type=\""+type+"\"; boundary=\""+boundary+"\"";
		testControlloInattivo("multipart, parte radice in base64", StrutturaEnvelopeCasi.multipart(boundary, type, errato, "allegato", true, "base64"), ns, ctMultipart);
		testControlloInattivo("multipart, parte radice indicata da start non presente", StrutturaEnvelopeCasi.multipart(boundary, type, errato, "allegato", true),
				ns, ctMultipart+"; start=\"<altro@govway.org>\"");
		testControlloInattivo("multipart senza boundary", errato, ns, HttpConstants.CONTENT_TYPE_MULTIPART_RELATED+"; type=\""+type+"\"");
	}
	private static void testControlloInattivo(String descrizione, String messaggio, String namespace, String contentType) throws Exception {
		testControlloInattivo(descrizione, messaggio.getBytes(Charset.UTF_8.getValue()), namespace, contentType);
	}
	private static void testControlloInattivo(String descrizione, byte[] bytes, String namespace, String contentType) throws Exception {
		System.out.println("- "+descrizione+" (controllo non applicabile) ...");
		for (int blocco : new int[] {0, 1}) {
			SoapEnvelopeStructureStreamChecker checker = new SoapEnvelopeStructureStreamChecker(namespace, contentType, false);
			int dim = blocco<=0 ? bytes.length : blocco;
			for (int i = 0; i < bytes.length; i+=dim) {
				checker.update(bytes, i, Math.min(dim, bytes.length-i));
			}
			checker.end();
			if(checker.isActive() || checker.isCompleted() || checker.getError()!=null) {
				throw new Exception("["+descrizione+"] atteso controllo disattivato");
			}
		}
		// lo stream di verifica inoltra il messaggio invariato
		if(inoltra(descrizione, bytes, namespace, contentType, 512, null)!=null) {
			throw new Exception("["+descrizione+"] inoltro: anomalia non attesa con il controllo disattivato");
		}
		System.out.println("- "+descrizione+" ok (controllo disattivato)");
	}

	private static void testOltreSoglia(MessageType messageType) throws Exception {
		System.out.println("- messaggio di grandi dimensioni ...");
		StrutturaEnvelopeCasi.Parti p = new StrutturaEnvelopeCasi.Parti(messageType);
		String ct = StrutturaEnvelopeCasi.getContentType(messageType);
		String bGrande = p.bodyGrande(200*1024);
		byte[] corretto = p.envelope(p.h, bGrande).getBytes(Charset.UTF_8.getValue());
		byte[] errato = p.envelope(p.h, bGrande, p.b2).getBytes(Charset.UTF_8.getValue());
		ReaderTest.verificaErrore("messaggio di grandi dimensioni corretto", "inoltro", inoltra("grande", corretto, p.ns, ct, Utilities.DIMENSIONE_BUFFER, null), null);
		ReaderTest.verificaErrore("messaggio di grandi dimensioni con 2 body", "inoltro", inoltra("grande", errato, p.ns, ct, Utilities.DIMENSIONE_BUFFER, null), StrutturaEnvelopeCasi.ERR_BODY);
		System.out.println("- messaggio di grandi dimensioni ok");
	}

	private static void testStatoDopoErrore(MessageType messageType) throws Exception {
		System.out.println("- stato dopo l'anomalia ...");
		StrutturaEnvelopeCasi.Parti p = new StrutturaEnvelopeCasi.Parti(messageType);
		byte[] errato = p.envelope(p.h, p.b, p.b2).getBytes(Charset.UTF_8.getValue());
		SoapEnvelopeStructureStreamChecker checker = new SoapEnvelopeStructureStreamChecker(p.ns, StrutturaEnvelopeCasi.getContentType(messageType), false);
		SoapEnvelopeStructureException prima = null;
		try {
			checker.update(errato, 0, errato.length);
		}catch(SoapEnvelopeStructureException e) {
			prima = e;
		}
		if(prima==null || !checker.isActive() || checker.isCompleted()) {
			throw new Exception("stato dopo l'anomalia: atteso errore con controllo attivo e non completato");
		}
		for (int i = 0; i < 2; i++) {
			try {
				if(i==0) {
					checker.update(' ');
				}
				else {
					checker.end();
				}
				throw new Exception("stato dopo l'anomalia: attesa la stessa anomalia");
			}catch(SoapEnvelopeStructureException e) {
				if(e!=prima) {
					throw new Exception("stato dopo l'anomalia: attesa la stessa anomalia, ricevuta: "+e.getMessage());
				}
			}
		}
		System.out.println("- stato dopo l'anomalia ok");
	}

	private static void testEnvelopeTroncato(MessageType messageType) throws Exception {
		System.out.println("- envelope troncato ...");
		StrutturaEnvelopeCasi.Parti p = new StrutturaEnvelopeCasi.Parti(messageType);
		String troncato = p.envOpen+p.h+p.b;
		boolean[] completo = new boolean[1];
		String errore = verifica(troncato.getBytes(Charset.UTF_8.getValue()), p.ns, StrutturaEnvelopeCasi.getContentType(messageType), false, 0, completo);
		// XML non ben formato: non è un'anomalia della struttura (lo rileva il parser), ma la verifica non è completa
		if(errore!=null || completo[0]) {
			throw new Exception("envelope troncato: atteso nessun errore e verifica non completa (errore: "+errore+")");
		}
		System.out.println("- envelope troncato ok");
	}

	private static void testNamespaceAtteso(MessageType messageType) throws Exception {
		System.out.println("- namespace atteso non indicato ...");
		StrutturaEnvelopeCasi.Parti p = new StrutturaEnvelopeCasi.Parti(messageType);
		String ct = StrutturaEnvelopeCasi.getContentType(messageType);
		ReaderTest.verificaErrore("namespace atteso non indicato", "controllo", 
				verifica(p.envelope(p.h, p.b, p.b2).getBytes(Charset.UTF_8.getValue()), null, ct, false, 0, null), StrutturaEnvelopeCasi.ERR_BODY);
		boolean[] completo = new boolean[1];
		ReaderTest.verificaErrore("namespace atteso non indicato", "controllo", 
				verifica(p.envelope(p.h, p.b).getBytes(Charset.UTF_8.getValue()), null, ct, false, 0, completo), null);
		if(!completo[0]) {
			throw new Exception("namespace atteso non indicato: messaggio corretto non verificato interamente");
		}
		System.out.println("- namespace atteso non indicato ok");
	}

	private static void testElementiDopoBodyAmmessi(MessageType messageType) throws Exception {
		System.out.println("- elementi di altro namespace dopo il body ammessi (solo SOAP 1.1) ...");
		StrutturaEnvelopeCasi.Parti p = new StrutturaEnvelopeCasi.Parti(messageType);
		String ct = StrutturaEnvelopeCasi.getContentType(messageType);
		String estensione = "<t:estensione xmlns:t=\""+StrutturaEnvelopeCasi.NS_TEST+"\">estensione</t:estensione>";
		byte[] conEstensione = p.envelope(p.h, p.b, estensione).getBytes(Charset.UTF_8.getValue());
		byte[] conSecondoBody = p.envelope(p.h, p.b, p.b2).getBytes(Charset.UTF_8.getValue());
		boolean soap11 = MessageType.SOAP_11.equals(messageType);
		ReaderTest.verificaErrore("elementi dopo il body ammessi", "controllo", verifica(conEstensione, p.ns, ct, true, 0, null), 
				soap11 ? null : StrutturaEnvelopeCasi.ERR_DOPO_BODY);
		ReaderTest.verificaErrore("elementi dopo il body ammessi", "controllo (secondo body)", verifica(conSecondoBody, p.ns, ct, true, 0, null), 
				StrutturaEnvelopeCasi.ERR_BODY);
		System.out.println("- elementi di altro namespace dopo il body ammessi ok");
	}


	// ---- Stream ----

	private static void testMetodiStream(MessageType messageType) throws Exception {
		System.out.println("- metodi dello stream di verifica ...");
		StrutturaEnvelopeCasi.Parti p = new StrutturaEnvelopeCasi.Parti(messageType);
		String ct = StrutturaEnvelopeCasi.getContentType(messageType);
		byte[] corretto = p.envelope(p.h, p.b).getBytes(Charset.UTF_8.getValue());
		byte[] errato = p.envelope(p.h, p.b, p.b2).getBytes(Charset.UTF_8.getValue());

		// read() un byte alla volta
		try(SoapEnvelopeStructureCheckInputStream is = new SoapEnvelopeStructureCheckInputStream(new ByteArrayInputStream(corretto),
				new SoapEnvelopeStructureStreamChecker(p.ns, ct, false), null)){
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int b;
			while((b=is.read())!=-1) {
				out.write(b);
			}
			if(!Arrays.equals(corretto, out.toByteArray()) || is.read()!=-1 || !is.getChecker().isCompleted()) {
				throw new Exception("stream: lettura a singoli byte non corretta");
			}
		}
		try(SoapEnvelopeStructureCheckInputStream is = new SoapEnvelopeStructureCheckInputStream(new ByteArrayInputStream(errato),
				new SoapEnvelopeStructureStreamChecker(p.ns, ct, false), null)){
			try {
				while(is.read()!=-1) {
					// lettura
				}
				throw new Exception("stream: anomalia non rilevata nella lettura a singoli byte");
			}catch(SoapEnvelopeStructureIOException e) {
				ReaderTest.verificaErrore("stream read()", "inoltro", e.getMessage(), StrutturaEnvelopeCasi.ERR_BODY);
			}
		}

		// skip: i byte saltati vengono comunque verificati
		ByteArrayInputStream sorgente = new ByteArrayInputStream(errato);
		try(SoapEnvelopeStructureCheckInputStream is = new SoapEnvelopeStructureCheckInputStream(sorgente,
				new SoapEnvelopeStructureStreamChecker(p.ns, ct, false), null)){
			try {
				is.skip(errato.length);
				throw new Exception("stream: anomalia non rilevata nei byte saltati");
			}catch(SoapEnvelopeStructureIOException e) {
				ReaderTest.verificaErrore("stream skip()", "inoltro", e.getMessage(), StrutturaEnvelopeCasi.ERR_BODY);
			}
			if(sorgente.available()>0) {
				throw new Exception("stream skip(): stream di origine non consumato interamente");
			}
		}
		try(SoapEnvelopeStructureCheckInputStream is = new SoapEnvelopeStructureCheckInputStream(new ByteArrayInputStream(corretto),
				new SoapEnvelopeStructureStreamChecker(p.ns, ct, false), null)){
			if(is.skip(0)!=0 || is.skip(10)!=10 || is.skip(corretto.length)!=corretto.length-10 || is.read()!=-1) {
				throw new Exception("stream skip(): numero di byte saltati non corretto");
			}
		}

		// mark/reset non supportati: la verifica non può tornare indietro
		try(SoapEnvelopeStructureCheckInputStream is = new SoapEnvelopeStructureCheckInputStream(new ByteArrayInputStream(corretto),
				new SoapEnvelopeStructureStreamChecker(p.ns, ct, false), null)){
			if(is.markSupported()) {
				throw new Exception("stream: mark non deve essere supportato");
			}
			is.mark(10);
			try {
				is.reset();
				throw new Exception("stream: reset non deve essere supportato");
			}catch(IOException e) {
				// atteso
			}
		}

		// stream di origine vuoto
		try(SoapEnvelopeStructureCheckInputStream is = new SoapEnvelopeStructureCheckInputStream(new ByteArrayInputStream(new byte[0]),
				new SoapEnvelopeStructureStreamChecker(p.ns, ct, false), null)){
			if(is.read(new byte[10])!=-1 || is.getChecker().isCompleted()) {
				throw new Exception("stream vuoto: attesa fine dello stream senza verifica completa");
			}
		}
		System.out.println("- metodi dello stream di verifica ok");
	}


	// ---- Messaggio ----

	private static OpenSPCoop2Message buildMessaggio(MessageType messageType, String contentType, byte[] bytes, boolean controlloCompletoReader) throws Exception {
		TransportRequestContext req = new TransportRequestContext();
		Map<String, List<String>> headers = new HashMap<>();
		List<String> l = new ArrayList<>();
		l.add(contentType);
		headers.put(HttpConstants.CONTENT_TYPE, l);
		req.setHeaders(headers);
		OpenSPCoop2MessageSoapStreamReader streamReader = new OpenSPCoop2MessageSoapStreamReader(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
				contentType, new ByteArrayInputStream(bytes), 10);
		streamReader.setEnvelopeStructureFullCheck(controlloCompletoReader);
		InputStream is = null;
		try {
			streamReader.read();
		}finally {
			is = streamReader.getBufferedInputStream();
		}
		OpenSPCoop2MessageParseResult parser = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(messageType, req, is, null, streamReader);
		OpenSPCoop2Message msg = parser.getMessage_throwParseException();
		if(!(msg instanceof AbstractOpenSPCoop2Message_soap_impl<?>)) {
			throw new Exception("Atteso messaggio con tipo '"+AbstractOpenSPCoop2Message_soap_impl.class.getName()+"', trovato: "+msg.getClass().getName());
		}
		return msg;
	}

	/**
	 * @return l'anomalia rilevata durante la serializzazione o null
	 */
	private static String serializza(OpenSPCoop2Message msg, ByteArrayOutputStream bout) throws Exception {
		try {
			msg.writeTo(bout, true);
			return null;
		}catch(Exception e) {
			Throwable t = e;
			while(t!=null && !(t instanceof SoapEnvelopeStructureException)) {
				t = t.getCause();
			}
			if(t==null) {
				throw e;
			}
			return t.getMessage();
		}
	}

	private static void testSerializzazioneMessaggio(MessageType messageType) throws Exception {
		System.out.println("- verifica durante la serializzazione del messaggio ...");
		StrutturaEnvelopeCasi.Parti p = new StrutturaEnvelopeCasi.Parti(messageType);
		String ct = StrutturaEnvelopeCasi.getContentType(messageType);
		String bGrande = p.bodyGrande(20*1024);
		byte[] corretto = p.envelope(p.h, bGrande).getBytes(Charset.UTF_8.getValue());
		byte[] errato = p.envelope(p.h, bGrande, p.b2).getBytes(Charset.UTF_8.getValue());

		// controllo durante l'inoltro disabilitato: messaggio inoltrato invariato
		OpenSPCoop2Message msg = buildMessaggio(messageType, ct, errato, false);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ReaderTest.verificaErrore("serializzazione senza controllo", "messaggio", serializza(msg, bout), null);
		if(!Arrays.equals(errato, bout.toByteArray())) {
			throw new Exception("serializzazione senza controllo: messaggio differente da quello originale");
		}

		// controllo durante l'inoltro abilitato
		msg = buildMessaggio(messageType, ct, corretto, false);
		((AbstractOpenSPCoop2Message_soap_impl<?>)msg).setEnvelopeStructureForwardCheck(true);
		bout = new ByteArrayOutputStream();
		ReaderTest.verificaErrore("serializzazione con controllo, messaggio corretto", "messaggio", serializza(msg, bout), null);
		if(!Arrays.equals(corretto, bout.toByteArray()) || msg.getParseException()!=null) {
			throw new Exception("serializzazione con controllo: messaggio corretto differente da quello originale");
		}
		msg = buildMessaggio(messageType, ct, errato, false);
		((AbstractOpenSPCoop2Message_soap_impl<?>)msg).setEnvelopeStructureForwardCheck(true);
		bout = new ByteArrayOutputStream();
		ReaderTest.verificaErrore("serializzazione con controllo, 2 body", "messaggio", serializza(msg, bout), StrutturaEnvelopeCasi.ERR_BODY);
		if(bout.size()>=errato.length) {
			throw new Exception("serializzazione con controllo: messaggio errato serializzato per intero");
		}
		if(msg.getParseException()==null || !(msg.getParseException().getParseException() instanceof SoapEnvelopeStructureException)) {
			throw new Exception("serializzazione con controllo: anomalia non registrata come errore di parsing del messaggio");
		}

		// verifica della struttura disabilitata per le richieste: nessun controllo
		boolean richiesta = SoapEnvelopeStructureValidator.isRequestValidationEnabled();
		try {
			SoapEnvelopeStructureValidator.setRequestValidationEnabled(false);
			msg = buildMessaggio(messageType, ct, errato, false);
			((AbstractOpenSPCoop2Message_soap_impl<?>)msg).setEnvelopeStructureForwardCheck(true);
			bout = new ByteArrayOutputStream();
			ReaderTest.verificaErrore("serializzazione con verifica della richiesta disabilitata", "messaggio", serializza(msg, bout), null);
			if(!Arrays.equals(errato, bout.toByteArray())) {
				throw new Exception("serializzazione con verifica della richiesta disabilitata: messaggio differente da quello originale");
			}
		}finally {
			SoapEnvelopeStructureValidator.setRequestValidationEnabled(richiesta);
		}

		// controllo completo nel reader: anomalia sollevata alla costruzione del messaggio (messaggio entro la soglia)
		byte[] erratoPiccolo = p.envelope(p.h, p.b, p.b2).getBytes(Charset.UTF_8.getValue());
		try {
			buildMessaggio(messageType, ct, erratoPiccolo, true);
			throw new Exception("controllo completo nel reader: attesa anomalia alla costruzione del messaggio");
		}catch(Exception e) {
			Throwable t = e;
			while(t!=null && !(t instanceof SoapEnvelopeStructureException)) {
				t = t.getCause();
			}
			if(t==null) {
				throw e;
			}
			ReaderTest.verificaErrore("controllo completo nel reader", "messaggio", t.getMessage(), StrutturaEnvelopeCasi.ERR_BODY);
		}
		System.out.println("- verifica durante la serializzazione del messaggio ok");
	}
}
