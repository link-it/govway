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

package org.openspcoop2.core.protocolli.trasparente.testsuite.registrazione_messaggi.dump_binario;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.DumpBinarioUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.rpc.RPCServerThread;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* Verifica il repository di overflow dei buffer dei messaggi sui tipi di messaggio che la validazione
* dei contenuti tratta senza bufferizzazione 'lazy'.
*
* Per i messaggi JSON, XML e SOAP il contenuto viene costruito valorizzando il buffer del messaggio,
* mentre per i messaggi BINARY e MIME_MULTIPART si passa da un buffer che il messaggio non prende in
* carico. Il file oltre soglia deve essere prodotto in entrambi i casi: qui si verifica che su questi
* tipi venga poi rilasciato, e che il rilascio sia tempestivo, cioe' che il file della richiesta non
* sopravviva fino alla serializzazione della risposta.
*
* I payload sono costruiti gonfiando un campo ripetibile delle interfacce gia' presenti, in modo da
* superare la soglia restando validi rispetto allo schema.
*
* @author $Author$
* @version $Rev$, $Date$
*/
public class TipiMessaggioSogliaTest extends ConfigLoader {

	private static final int SOGLIA = 1048576;

	/** Il backend dell'API SOAP non e' dispiegato sull'application server: lo avviano i test stessi */
	private static RPCServerThread serverSoap;

	@BeforeClass
	public static void startServer() throws Exception {
		DumpBinarioUtils.deleteSnapshotScaduti(logCore);
		serverSoap = new RPCServerThread(8888, true, false);
		serverSoap.start();
		int attesa = 0;
		while(attesa<60 && !serverSoap.isInitialized()) {
			Utilities.sleep(1000);
			attesa++;
		}
	}

	@AfterClass
	public static void stopServer() throws Exception {
		if(serverSoap!=null) {
			serverSoap.setStop(true);
			while(!serverSoap.isFinished()) {
				Utilities.sleep(1000);
			}
		}
	}


	// ---------------- JSON ----------------

	/**
	 * I messaggi JSON non utilizzano il repository di overflow: 'OpenSPCoop2Message_json_impl'
	 * dichiara 'supportReadOnly = false', percio' la richiesta di bufferizzazione read-only avanzata
	 * dalla validazione non produce alcun buffer e il contenuto viene costruito direttamente dallo
	 * stream. Nessun file deve quindi comparire nemmeno oltre soglia: la soglia
	 * 'org.openspcoop2.pdd.logger.dumpBinario.inMemory.threshold' non ha effetto su questo tipo di
	 * messaggio, che resta interamente in memoria.
	 */
	@Test
	public void json_oltreSoglia_erogazione() throws Exception {
		invocaJson(2 * SOGLIA, false);
	}
	@Test
	public void json_sottoSoglia_erogazione() throws Exception {
		invocaJson(100 * 1024, false);
	}

	private static void invocaJson(int dimensioneMinima, boolean attesoSpill) throws Exception {

		String payload = buildInvoice(dimensioneMinima);

		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/BigInterface-SwaggerRequestValidatorTest/v1/Invoice");
		request.setContentType("application/fhir+json"); // unico media type ammesso dall'interfaccia
		request.setContent(payload.getBytes());

		invoca(request, "JSON", payload.length(), attesoSpill, 201);
	}

	/**
	 * Replica gli elementi dell'array 'identifier' finche' il documento non supera la dimensione
	 * richiesta: lo schema non pone un limite superiore al numero di elementi.
	 */
	private static String buildInvoice(int dimensioneMinima) {
		String identifier = "{\"system\": \"http://dati.ente_esempio.it/dataset/regioni\", \"value\": \"090\"}";
		StringBuilder sb = new StringBuilder();
		sb.append("{\n\"resourceType\": \"Invoice\",\n\"identifier\": [");
		boolean first = true;
		while(sb.length() < dimensioneMinima) {
			if(!first) {
				sb.append(",");
			}
			sb.append(identifier);
			first = false;
		}
		sb.append("],\n\"status\": \"issued\",\n\"date\": \"2021-09-23\"\n}");
		return sb.toString();
	}


	// ---------------- SOAP 1.1 ----------------

	@Test
	public void soap11_oltreSoglia_erogazione() throws Exception {
		invocaSoap11(2 * SOGLIA, true);
	}
	@Test
	public void soap11_sottoSoglia_erogazione() throws Exception {
		invocaSoap11(100 * 1024, false);
	}

	private static void invocaSoap11(int dimensioneIndirizzo, boolean attesoSpill) throws Exception {

		String payload = buildEnvelopeRpcLiteral(dimensioneIndirizzo);

		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		// il backend distingue la validazione lato server tramite il parametro 'tipo', come negli altri
		// test sulla stessa API
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/ServiceRPCLiteralValidazioneContenuti/v1/?tipo=ValidazioneAbilitata");
		request.setContentType(HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"RPCL-element\"");
		request.setContent(payload.getBytes());

		invoca(request, "SOAP 1.1", payload.length(), attesoSpill, 200);
	}

	/** Gonfia il campo 'indirizzo', che lo schema dichiara come stringa senza vincoli di lunghezza */
	private static String buildEnvelopeRpcLiteral(int dimensioneIndirizzo) {
		StringBuilder indirizzo = new StringBuilder();
		while(indirizzo.length() < dimensioneIndirizzo) {
			indirizzo.append("via-di-test-0123456789-");
		}
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://openspcoop2.org/ValidazioneContenutiWS/Service\" xmlns:typ=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/types\">\n"
				+ "   <soapenv:Header/>\n"
				+ "   <soapenv:Body>\n"
				+ "      <ser:RPCL-element>\n"
				+ "         <typ:nominativo ruolo=\"test\">test</typ:nominativo>\n"
				+ "         <typ:indirizzo>" + indirizzo + "</typ:indirizzo>\n"
				+ "         <typ:ora-registrazione>2022-12-27T15:22:41.220+01:00</typ:ora-registrazione>\n"
				+ "      </ser:RPCL-element>\n"
				+ "   </soapenv:Body>\n"
				+ "</soapenv:Envelope>";
	}


	// ------- Bufferizzazione senza successiva serializzazione -------

	/**
	 * Il buffer viene rilasciato nel blocco finale della serializzazione del messaggio. Quando la
	 * validazione della richiesta fallisce, pero', il messaggio originario non viene mai serializzato:
	 * GovWay genera un fault e la richiesta non raggiunge il backend. Il test verifica che anche in
	 * questo percorso il file prodotto oltre soglia non sopravviva alla transazione.
	 *
	 * Lo scenario e' costruito sul tipo SOAP, che usa il buffer del messaggio: sui tipi che passano
	 * dalla bufferizzazione lazy il difetto e' gia' presente a prescindere dall'esito.
	 */
	@Test
	public void soap11_oltreSoglia_validazioneRichiestaKO() throws Exception {

		String payload = buildEnvelopeRpcLiteralNonValido(2 * SOGLIA);

		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/ServiceRPCLiteralValidazioneContenuti/v1/?tipo=ValidazioneAbilitata");
		request.setContentType(HttpConstants.CONTENT_TYPE_SOAP_1_1);
		request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"RPCL-type\"");
		request.setContent(payload.getBytes());
		request.addHeader(DumpBinarioUtils.HEADER_SNAPSHOT_DIR, DumpBinarioUtils.getSnapshotDirHeaderValue());

		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);
		logCore.info("Invocazione SOAP 1.1 non valida con payload di "+payload.length()+" bytes ...");

		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(500, response.getResultHTTPOperation());

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		// nessuna verifica sulle fotografie: la richiesta non raggiunge il backend, quindi l'handler
		// associato alla fase di uscita della richiesta non viene invocato
		DumpBinarioUtils.verifyNoResidui(logCore, idTransazione, "SOAP 1.1 non valido (" + payload.length() + " bytes)");
		DumpBinarioUtils.deleteSnapshot(idTransazione);
	}

	/**
	 * Variante in stile 'type' del medesimo messaggio: senza gli attributi xsi:type la validazione la
	 * rifiuta, come gia' verificato dai test dell'API.
	 */
	private static String buildEnvelopeRpcLiteralNonValido(int dimensioneIndirizzo) {
		StringBuilder indirizzo = new StringBuilder();
		while(indirizzo.length() < dimensioneIndirizzo) {
			indirizzo.append("via-di-test-0123456789-");
		}
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://openspcoop2.org/ValidazioneContenutiWS/Service\">\n"
				+ "   <soapenv:Header/>\n"
				+ "   <soapenv:Body>\n"
				+ "      <ser:RPCL-type>\n"
				+ "         <nominativo ruolo=\"test\">test</nominativo>\n"
				+ "         <indirizzo>" + indirizzo + "</indirizzo>\n"
				+ "         <ora-registrazione>2022-12-27T15:22:41.220+01:00</ora-registrazione>\n"
				+ "         <idstring>3</idstring>\n"
				+ "         <idint>3</idint>\n"
				+ "      </ser:RPCL-type>\n"
				+ "   </soapenv:Body>\n"
				+ "</soapenv:Envelope>";
	}


	// ---------------- comune ----------------

	private static void invoca(HttpRequest request, String tipo, int dimensione, boolean attesoSpill, int statoAtteso) throws Exception {

		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);

		request.addHeader(DumpBinarioUtils.HEADER_SNAPSHOT_DIR, DumpBinarioUtils.getSnapshotDirHeaderValue());

		logCore.info("Invocazione "+tipo+" con payload di "+dimensione+" bytes ...");

		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(statoAtteso, response.getResultHTTPOperation());

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		if(attesoSpill) {
			DumpBinarioUtils.verifySpill(logCore, idTransazione, DumpBinarioUtils.FASE_OUT_REQUEST);
		}
		else {
			DumpBinarioUtils.verifyNessunSpill(logCore, idTransazione, DumpBinarioUtils.FASE_OUT_REQUEST);
		}

		DumpBinarioUtils.verifyNoResidui(logCore, idTransazione, tipo + " (" + dimensione + " bytes)");
		DumpBinarioUtils.deleteSnapshot(idTransazione);
	}

}
