/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.pdd.core.connettori.ConnettoreStatus;
import org.openspcoop2.pdd.core.connettori.ConnettoreStatusResponseType;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.HttpUtilsException;


/**
 * Classe di test connettore 'status' funzioni di utilita per rest e soap
 *
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CoreTest {
	
	private CoreTest() {}
	
	// ******************************** UTILITY *****************************************
	
	private static final String SERVIZIO_REST = "TestConnettoreStatusRest";
	private static final String SERVIZIO_SOAP = "TestConnettoreStatusSoap";
	
	private static final String OPERATION_STATUS_STATISTICA = "/statusStatistica";
	private static final String OPERATION_STATUS_CACHE_LIFETIME = "/statusStatisticaLifeTime";
	private static final String OPERATION_STATUS_CONNETTIVITA = "/statusConnettivita";
	private static final String OPERATION_TEST_OK = "/test1";
	private static final String OPERATION_TEST_ERR = "/test2";
	
	public static final String CACHE_CONTROLLO_TRAFFICO = "ControlloTraffico";
	
	private static String getIdTransazione(HttpResponse response) {
		return  response.getHeaderFirstValue("GovWay-Transaction-ID");
	}
	
	/**
	 * funzione di utilita che serve per inviare richieste al servizio di test connettore status
	 * @param isRest
	 * @param tipoServizio
	 * @param operation
	 * @param predictedCode
	 * @return
	 * @throws UtilsException
	 */
	private static HttpResponse sendRequest(boolean isRest, TipoServizio tipoServizio, String operation, Integer acceptedCode) throws UtilsException {
		StringBuilder urlBuilder = new StringBuilder()
				.append(System.getProperty("govway_base_path"));
		if (tipoServizio == TipoServizio.FRUIZIONE)
			urlBuilder.append("/out/SoggettoInternoTestFruitore");
		urlBuilder.append("/SoggettoInternoTest")
			.append("/TestConnettoreStatus")
			.append(isRest ? "Rest" : "Soap")
			.append("/v1")
			.append(operation);
		
		
		HttpRequest request = new HttpRequest();
		request.setUrl(urlBuilder.toString());

		if(!isRest) {
			request.setMethod(HttpRequestMethod.POST);
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, operation.substring(1));
			String contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
			byte[]content = Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes();
			
			request.setContent(content);
			request.setContentType(contentType);
		
		} else {
			request.setMethod(HttpRequestMethod.GET);
		}
		
		HttpResponse response = HttpUtilities.httpInvoke(request);
		String idTransazione = getIdTransazione(response);
		
		ConfigLoader.getLoggerCore().debug("[{}] invio richiesta alla porta {}, code: {}", idTransazione, operation, response.getResultHTTPOperation());
		
		assertNotNull(idTransazione);
		if (acceptedCode != null)
			assertEquals(acceptedCode.intValue(), response.getResultHTTPOperation());
		
		return response;
	}
	
	// ************************************* TEST ****************************************
	
	public static void testSingleOkResponse(boolean isRest, String operazione, TipoServizio tipoServizio, ConnettoreStatusResponseType responseType) throws UtilsException {
		ConfigLoader.getLoggerCore().debug("Inizio test verifica response {}, operazione: {}, tipoServizio:{})", isRest ? "Rest" : "Soap", operazione, tipoServizio);
		
		HttpResponse response = sendRequest(isRest, tipoServizio, operazione, 200);
		
		assertEquals(ConnettoreStatus.getContentType(responseType, isRest), response.getContentType());
		assertEquals(ConnettoreStatus.getMessage(responseType, isRest), new String(response.getContent()));
	}
	
	public static void testSingleConnettivita(boolean isRest, TipoServizio tipoServizio) throws UtilsException, SQLQueryObjectException {
		ConfigLoader.getLoggerCore().debug("Inizio test verifica connettivita {}, tipoServizio:{})", isRest ? "REST" : "SOAP", tipoServizio);
		
		HttpResponse response = sendRequest(isRest, tipoServizio, CoreTest.OPERATION_STATUS_CONNETTIVITA, isRest ? 503 : 500);
		String idTransazione = getIdTransazione(response);
		
		DBVerifier.checkMsgDiag(idTransazione, ".* il connettore \\[\\d+\\] del gruppo: testRefused check fallito: Connection refused");
	}
	
	public static void testSingleStatistica(boolean isRest, TipoServizio tipoServizio) throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		ConfigLoader.getLoggerCore().debug("Inizio test verifica statistica {}, tipoServizio:{})", isRest ? "REST" : "SOAP", tipoServizio);
		
		CoreTest.testSingleStatisticaEmpty(isRest, tipoServizio);
		CoreTest.testSingleStatisticaErrors(isRest, tipoServizio);
		CoreTest.testSingleStatisticaOk(isRest, tipoServizio);
	}
	
	private static void testSingleStatisticaEmpty(boolean isRest, TipoServizio tipoServizio) throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		ConfigLoader.getLoggerCore().debug("Inizio test verifica statistica con statistiche vuote");
		DBVerifier.resetStatistiche(isRest ? SERVIZIO_REST : SERVIZIO_SOAP);
		ConfigLoader.resetCache(false, CACHE_CONTROLLO_TRAFFICO);
		
		sendRequest(isRest, tipoServizio, CoreTest.OPERATION_STATUS_STATISTICA, 200);
	}
	
	private static void testSingleStatisticaErrors(boolean isRest, TipoServizio tipoServizio) throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		ConfigLoader.getLoggerCore().debug("Inizio test verifica statistica con statistiche solo errori");
		DBVerifier.resetStatistiche(isRest ? SERVIZIO_REST : SERVIZIO_SOAP);
		ConfigLoader.resetCache(false, CACHE_CONTROLLO_TRAFFICO);
		
		// invio richiesta alla porta che produce errori
		sendRequest(isRest, tipoServizio, CoreTest.OPERATION_TEST_ERR, isRest ? 503 : 500);
		
		// ricarico le statistiche
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.waitForDbStats();
		HttpResponse response = sendRequest(isRest, tipoServizio, CoreTest.OPERATION_STATUS_STATISTICA, isRest ? 503 : 500);
		String idTransazione = getIdTransazione(response);

		DBVerifier.checkMsgDiag(idTransazione, ".* trovate: 1 transazioni con errore, nessuna transazione ok");

	}
	
	private static void testSingleStatisticaOk(boolean isRest, TipoServizio tipoServizio) throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		ConfigLoader.getLoggerCore().debug("Inizio test verifica statistica con statistiche solo errori");
		DBVerifier.resetStatistiche(isRest ? SERVIZIO_REST : SERVIZIO_SOAP);
		ConfigLoader.resetCache(false, CACHE_CONTROLLO_TRAFFICO);
		
		// invio richiesta alla porta che produce errori
		sendRequest(isRest, tipoServizio, CoreTest.OPERATION_TEST_ERR, isRest ? 503 : 500);
		
		// invio richiesta alla porta che non produce errori
		sendRequest(isRest, tipoServizio,CoreTest.OPERATION_TEST_OK, 200);
		
		// ricarico le statistiche
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.waitForDbStats();
		sendRequest(isRest, tipoServizio, CoreTest.OPERATION_STATUS_STATISTICA, 200);
	}
	
	public static void testSingleLifetime(boolean isRest, TipoServizio tipoServizio) throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		ConfigLoader.getLoggerCore().debug("Inizio test custom cache lifetime {}, tipoServizio:{})", isRest ? "REST" : "SOAP", tipoServizio);
		
		DBVerifier.resetStatistiche(isRest ? SERVIZIO_REST : SERVIZIO_SOAP);
		ConfigLoader.resetCache(false, CACHE_CONTROLLO_TRAFFICO);

		// invio richiesta per inizializzare la cache delle statistiche
		sendRequest(isRest, tipoServizio, CoreTest.OPERATION_STATUS_CACHE_LIFETIME, 200);
		
		//invio richiesta alla porta che produce errori
		sendRequest(isRest, tipoServizio, CoreTest.OPERATION_TEST_ERR, isRest ? 503 : 500);
		
		// invio richiesta per ottenere il risultato in cache
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.waitForDbStats();
		sendRequest(isRest, tipoServizio, CoreTest.OPERATION_STATUS_CACHE_LIFETIME, 200);
		
		// aspetto che il life time custom invalidi la cache
		Utilities.sleep(5000); 

		// invio richiesta per ottenere il nuovo risultato
		sendRequest(isRest, tipoServizio, CoreTest.OPERATION_STATUS_CACHE_LIFETIME, isRest ? 503 : 500);
		
		
	}
}
