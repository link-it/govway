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
package org.openspcoop2.core.protocolli.trasparente.testsuite.other.soap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import jakarta.xml.soap.SOAPFault;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ProblemUtilities;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.w3c.dom.Node;

/**
 * Utilità per i test sulla struttura dell'Envelope SOAP (API TestWSSecurityStrutturaEnvelope).
 *
 * Il backend dell'erogazione e il connettore della fruizione puntano a TestService con 'destFile': la risposta
 * restituita è il contenuto del file indicato nell'header {@link #HEADER_FILE_RISPOSTA}. In questo modo le
 * anomalie arrivano al gateway anche nelle risposte, senza passare da un parser SOAP.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StrutturaEnvelopeTestUtils {

	private StrutturaEnvelopeTestUtils() {}

	public static final String API = "TestWSSecurityStrutturaEnvelope";

	public static final String HEADER_FILE_RISPOSTA = "govway-testsuite-struttura-envelope-file";

	/** Nessuna funzionalità: verifica della sola struttura */
	public static final String AZIONE_ECHO = "echo";
	/** Erogazione: firma (Timestamp e Body) della risposta del backend */
	public static final String AZIONE_FIRMA_RISPOSTA = "firma-risposta";
	/** Erogazione: verifica della firma della richiesta; Fruizione: verifica della firma della risposta */
	public static final String AZIONE_VERIFICA_FIRMA = "verifica-firma";
	/** Erogazione: cifratura del Body della risposta del backend */
	public static final String AZIONE_CIFRA_RISPOSTA = "cifra-risposta";
	/** Erogazione: decifratura della richiesta; Fruizione: decifratura della risposta */
	public static final String AZIONE_DECIFRA = "decifra";

	public static final String REQUEST_ERROR = "UnprocessableRequestContent";
	public static final String REQUEST_ERROR_MESSAGE = "Request payload is different from what declared in 'Content-Type' header";
	public static final String SECURITY_REQUEST_ERROR = "BadRequest";
	public static final String SECURITY_REQUEST_ERROR_MESSAGE = "Bad request";
	public static final String RESPONSE_ERROR = "InvalidResponse";
	public static final String RESPONSE_ERROR_MESSAGE = "Invalid response received from the API Implementation";

	public enum Porta { EROGAZIONE, FRUIZIONE }
	public enum Direzione { RICHIESTA, RISPOSTA }


	// ---- Controlli opzionali della struttura per i messaggi gestiti in streaming senza DOM ----

	/** Configurazione predefinita con 'soap.envelopeStructure.reader.fullCheck.enabled=true' */
	public static final String API_CONTROLLO_COMPLETO = "TestStrutturaEnvelopeControlloCompleto";
	/** Configurazione predefinita con 'soap.envelopeStructure.forward.check.enabled=true' e registrazione binaria dei messaggi */
	public static final String API_CONTROLLO_INOLTRO = "TestStrutturaEnvelopeControlloInoltro";
	/** Gruppo con 'soap.envelopeStructure.forward.check.enabled=true' (su API_CONTROLLO_COMPLETO anche con il controllo completo) */
	public static final String AZIONE_ECHO_CONTROLLO_INOLTRO = "echo-controllo-inoltro";
	/** Gruppo con 'soap.envelopeStructure.reader.fullCheck.enabled=true': in richiesta viene ignorata (vale solo sulla configurazione predefinita) */
	public static final String AZIONE_ECHO_CONTROLLO_COMPLETO = "echo-controllo-completo";

	public enum Configurazione {
		/** Nessun controllo opzionale (default) */
		NESSUN_CONTROLLO(API, AZIONE_ECHO, false, false, false, false),
		/** Controllo durante l'inoltro definito sul gruppo */
		CONTROLLO_INOLTRO_GRUPPO(API, AZIONE_ECHO_CONTROLLO_INOLTRO, false, true, false, true),
		/** Controllo completo definito sul gruppo: si applica alla sola risposta */
		CONTROLLO_COMPLETO_GRUPPO(API, AZIONE_ECHO_CONTROLLO_COMPLETO, false, false, true, false),
		/** Controllo completo definito sulla configurazione predefinita */
		CONTROLLO_COMPLETO(API_CONTROLLO_COMPLETO, AZIONE_ECHO, true, false, true, false),
		/** Controllo completo sulla configurazione predefinita e sul gruppo, controllo durante l'inoltro sul gruppo */
		CONTROLLO_COMPLETO_E_INOLTRO(API_CONTROLLO_COMPLETO, AZIONE_ECHO_CONTROLLO_INOLTRO, true, true, true, true),
		/** Controllo durante l'inoltro sulla configurazione predefinita, con registrazione binaria dei messaggi */
		CONTROLLO_INOLTRO_CON_DUMP(API_CONTROLLO_INOLTRO, AZIONE_ECHO, false, true, false, true);

		private final String api;
		private final String azione;
		private final boolean richiestaControlloCompleto;
		private final boolean richiestaControlloInoltro;
		private final boolean rispostaControlloCompleto;
		private final boolean rispostaControlloInoltro;
		Configurazione(String api, String azione, boolean richiestaControlloCompleto, boolean richiestaControlloInoltro,
				boolean rispostaControlloCompleto, boolean rispostaControlloInoltro){
			this.api = api;
			this.azione = azione;
			this.richiestaControlloCompleto = richiestaControlloCompleto;
			this.richiestaControlloInoltro = richiestaControlloInoltro;
			this.rispostaControlloCompleto = rispostaControlloCompleto;
			this.rispostaControlloInoltro = rispostaControlloInoltro;
		}
		public String getApi() {
			return this.api;
		}
		public String getAzione() {
			return this.azione;
		}
		public boolean isControlloCompleto(Direzione direzione) {
			return Direzione.RICHIESTA.equals(direzione) ? this.richiestaControlloCompleto : this.rispostaControlloCompleto;
		}
		public boolean isControlloInoltro(Direzione direzione) {
			return Direzione.RICHIESTA.equals(direzione) ? this.richiestaControlloInoltro : this.rispostaControlloInoltro;
		}
	}

	public enum EsitoAtteso {
		/** Messaggio inoltrato */
		ACCETTATO,
		/** Messaggio rifiutato prima dell'inoltro: SOAP Fault al client */
		RIFIUTATO,
		/** Anomalia rilevata durante l'inoltro della risposta al client, già iniziato: il client riceve una risposta incompleta */
		RISPOSTA_INTERROTTA
	}

	/**
	 * @param porta erogazione o fruizione
	 * @param anomaliaOltreSoglia true se l'anomalia si trova oltre la soglia del buffer del reader (30 KB)
	 */
	public static EsitoAtteso getEsitoAtteso(Configurazione configurazione, Porta porta, Direzione direzione, StrutturaEnvelopeSoap.Caso caso, boolean anomaliaOltreSoglia) {
		if(caso.isCorretto()) {
			return EsitoAtteso.ACCETTATO;
		}
		if(caso.isRilevatoParteIniziale()) {
			return EsitoAtteso.RIFIUTATO;
		}
		if(caso.isFaultPrimoFiglioBody() && (Direzione.RISPOSTA.equals(direzione) || Porta.EROGAZIONE.equals(porta))) {
			// il Fault viene interpretato costruendo il DOM, sul quale viene verificata la struttura
			return EsitoAtteso.RIFIUTATO;
		}
		if(configurazione.isControlloCompleto(direzione) && !anomaliaOltreSoglia) {
			return EsitoAtteso.RIFIUTATO;
		}
		if(configurazione.isControlloInoltro(direzione)) {
			return Direzione.RICHIESTA.equals(direzione) ? EsitoAtteso.RIFIUTATO : EsitoAtteso.RISPOSTA_INTERROTTA;
		}
		// limite noto: senza i controlli opzionali l'anomalia nella coda di un messaggio gestito in streaming non viene rilevata
		return EsitoAtteso.ACCETTATO;
	}

	public static void verify(HttpResponse response, EsitoAtteso esito, Direzione direzione, boolean soap11, StrutturaEnvelopeSoap.Caso caso, String rispostaBackend) throws Exception {
		switch (esito) {
		case ACCETTATO:
			verifyOk(response);
			break;
		case RIFIUTATO:
			if(Direzione.RICHIESTA.equals(direzione)) {
				verifyKoRichiesta(response, soap11, caso);
			}
			else {
				verifyKoRisposta(response, soap11, caso);
			}
			break;
		case RISPOSTA_INTERROTTA:
			verifyRispostaInterrotta(response, soap11, caso, rispostaBackend);
			break;
		}
	}

	/**
	 * Anomalia rilevata durante l'inoltro della risposta: se la consegna al client è già iniziata lo stato HTTP non è più modificabile
	 * e il client riceve la sola parte della risposta precedente all'anomalia; la transazione viene registrata come contenuto risposta non riconosciuto.
	 * Se invece la risposta non era ancora stata consegnata (es. modalità Content-Length) viene restituito il SOAP Fault.
	 */
	public static void verifyRispostaInterrotta(HttpResponse response, boolean soap11, StrutturaEnvelopeSoap.Caso caso, String rispostaBackend) throws Exception {
		if(response.getResultHTTPOperation()==500) {
			verifyKoRisposta(response, soap11, caso);
			return;
		}
		assertEquals("Attesa risposta incompleta con codice 200", 200, response.getResultHTTPOperation());
		byte[] ricevuta = response.getContent()!=null ? response.getContent() : new byte[0];
		byte[] attesa = rispostaBackend.getBytes(StandardCharsets.UTF_8);
		assertTrue("Attesa risposta incompleta (ricevuti "+ricevuta.length+" byte su "+attesa.length+"): "+new String(ricevuta), 
				ricevuta.length < attesa.length);
		assertTrue("La parte ricevuta deve coincidere con l'inizio della risposta del backend: "+new String(ricevuta), 
				java.util.Arrays.equals(ricevuta, java.util.Arrays.copyOf(attesa, ricevuta.length)));
		DBVerifier.verify(getIdTransazione(response), getEsito(EsitoTransazioneName.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO),
				caso.getPatternDiagnostico(), null);
	}


	public static String getMessaggioValido(boolean soap11) {
		return getMessaggioValido(soap11, 500);
	}
	/** Dimensione del payload del Body che porta il messaggio oltre la soglia del buffer del reader (30 KB) */
	public static final int DIMENSIONE_PAYLOAD_OLTRE_SOGLIA = 40 * 1024;
	public static String getMessaggioValido(boolean soap11, int dimensionePayload) {
		// Header presente: serve ai casi che lo duplicano o lo spostano
		return soap11 ?
				org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies.getSOAPEnvelope11(200, dimensionePayload) :
				org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies.getSOAPEnvelope12(200, dimensionePayload);
	}

	public static String getUrl(Porta porta, String azione) {
		return getUrl(API, porta, azione);
	}
	public static String getUrl(String api, Porta porta, String azione) {
		if(Porta.EROGAZIONE.equals(porta)) {
			return System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+azione;
		}
		return System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+azione;
	}

	/**
	 * Invia la richiesta come byte grezzi; il backend restituirà 'rispostaBackend' così com'è.
	 */
	public static HttpResponse invoke(Porta porta, String azione, boolean soap11, String richiesta, String rispostaBackend) throws Exception {
		return invoke(API, porta, azione, soap11, richiesta, rispostaBackend);
	}
	public static HttpResponse invoke(Configurazione configurazione, Porta porta, boolean soap11, String richiesta, String rispostaBackend) throws Exception {
		return invoke(configurazione.getApi(), porta, configurazione.getAzione(), soap11, richiesta, rispostaBackend);
	}
	public static HttpResponse invoke(String api, Porta porta, String azione, boolean soap11, String richiesta, String rispostaBackend) throws Exception {

		File f = File.createTempFile("strutturaEnvelope", ".xml");
		try {
			Files.write(f.toPath(), rispostaBackend.getBytes(StandardCharsets.UTF_8));
			// GovWay e TestService possono girare con un utente diverso da quello della testsuite
			f.setReadable(true, false);
			f.setWritable(true, false);

			HttpRequest request = new HttpRequest();
			request.setReadTimeout(20000);
			request.setMethod(HttpRequestMethod.POST);
			request.setContentType(soap11 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2);
			if(soap11) {
				request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, azione);
			}
			request.addHeader(HEADER_FILE_RISPOSTA, f.getAbsolutePath());
			request.setContent(richiesta.getBytes(StandardCharsets.UTF_8));
			request.setUrl(getUrl(api, porta, azione));

			HttpResponse response = HttpUtilities.httpInvoke(request);
			assertNotNull(getIdTransazione(response));
			return response;
		}finally {
			Files.deleteIfExists(f.toPath());
		}
	}

	public static String getIdTransazione(HttpResponse response) {
		return response.getHeaderFirstValue("GovWay-Transaction-ID");
	}

	private static long getEsito(EsitoTransazioneName name) throws Exception {
		return EsitiProperties.getInstanceFromProtocolName(ConfigLoader.getLoggerCore(), Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(name);
	}

	public static void verifyOk(HttpResponse response) throws Exception {
		assertEquals("Atteso esito positivo, ricevuto: "+new String(response.getContent()), 200, response.getResultHTTPOperation());
		DBVerifier.verify(getIdTransazione(response), getEsito(EsitoTransazioneName.OK));
	}

	public static void verifyKoRichiesta(HttpResponse response, boolean soap11, StrutturaEnvelopeSoap.Caso caso) throws Exception {
		if(caso.isCoperturaWSSecurity()) {
			// struttura corretta: l'anomalia è un errore di sicurezza del messaggio
			verifyFault(response, soap11,
					org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT, SECURITY_REQUEST_ERROR, 400, SECURITY_REQUEST_ERROR_MESSAGE);
			DBVerifier.verify(getIdTransazione(response), getEsito(EsitoTransazioneName.ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA),
					caso.getPatternDiagnostico(), null);
			return;
		}
		verifyFault(response, soap11,
				org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT, REQUEST_ERROR, 400, REQUEST_ERROR_MESSAGE);
		DBVerifier.verify(getIdTransazione(response), getEsito(EsitoTransazioneName.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO),
				caso.getPatternDiagnostico(), null);
	}

	public static void verifyKoRisposta(HttpResponse response, boolean soap11, StrutturaEnvelopeSoap.Caso caso) throws Exception {
		verifyFault(response, soap11,
				org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER, RESPONSE_ERROR, 502, RESPONSE_ERROR_MESSAGE);
		EsitoTransazioneName esito = caso.isCoperturaWSSecurity() ?
				EsitoTransazioneName.ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA : // struttura corretta: errore di sicurezza del messaggio
				EsitoTransazioneName.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO;
		DBVerifier.verify(getIdTransazione(response), getEsito(esito),
				caso.getPatternDiagnostico(), null);
	}

	private static void verifyFault(HttpResponse response, boolean soap11, String soapPrefixError, String error, int code, String errorMsg) throws Exception {

		String risposta = response.getContent()!=null ? new String(response.getContent()) : null;
		assertEquals("Atteso fault, ricevuto: "+risposta, 500, response.getResultHTTPOperation());
		assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));

		MessageType messageType = soap11 ? MessageType.SOAP_11 : MessageType.SOAP_12;
		OpenSPCoop2MessageParseResult parse = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(messageType,
				MessageRole.NONE, response.getContentType(), response.getContent());
		OpenSPCoop2Message msg = null;
		try {
			msg = parse.getMessage_throwParseThrowable();
		}catch(Throwable t) {
			throw new AssertionError("Fault ricevuto non processabile: "+risposta, t);
		}
		OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();

		assertTrue(soapMsg.isFault());
		SOAPFault soapFault = soapMsg.getSOAPBody().getFault();
		assertNotNull(soapFault);
		if(soap11) {
			assertNotNull(soapFault.getFaultCodeAsQName());
			assertEquals(soapPrefixError+"."+error, soapFault.getFaultCodeAsQName().getLocalPart());
		}
		assertEquals(errorMsg, soapFault.getFaultString());
		assertEquals("http://govway.org/integration", soapFault.getFaultActor());

		assertTrue(ProblemUtilities.existsProblem(soapFault.getDetail(), ConfigLoader.getLoggerCore()));
		Node problemNode = ProblemUtilities.getProblem(soapFault.getDetail(), ConfigLoader.getLoggerCore());
		ProblemUtilities.verificaProblem(problemNode, code, error, errorMsg, true, ConfigLoader.getLoggerCore());
	}
}
