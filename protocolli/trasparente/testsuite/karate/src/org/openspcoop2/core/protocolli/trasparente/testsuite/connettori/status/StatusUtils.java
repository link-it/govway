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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.connettori.ConnettoreStatus;
import org.openspcoop2.pdd.core.connettori.ConnettoreStatusResponseType;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.mime.MimeMultipart;
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
public class StatusUtils {
	
	private StatusUtils() {}
	
	// ******************************** UTILITY *****************************************
	
	private static final String SERVIZIO_REST = "TestConnettoreStatusRest";
	private static final String SERVIZIO_SOAP = "TestConnettoreStatusSoap";
	
	private static final String OPERATION_STATUS_STATISTICA = "/statusStatistica";
	private static final String OPERATION_STATUS_CACHE_LIFETIME = "/statusStatisticaLifeTime";
	private static final String OPERATION_STATUS_CONNETTIVITA = "/statusConnettivita";
	private static final String OPERATION_TEST_OK = "/test1";
	private static final String OPERATION_TEST_ERR = "/test3";
	
	public static final String CACHE_CONTROLLO_TRAFFICO = "ControlloTraffico";
	
	private static String getIdTransazione(HttpResponse response) {
		return  response.getHeaderFirstValue("GovWay-Transaction-ID");
	}
	
	private static boolean isSoap(MessageType messageType) {
		return (MessageType.SOAP_11.equals(messageType) || MessageType.SOAP_12.equals(messageType));
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
	private static HttpResponse sendRequest(MessageType messageType, TipoServizio tipoServizio, String operation, Integer acceptedCode) throws UtilsException {
		
		boolean isSoap = isSoap(messageType);
		
		StringBuilder urlBuilder = new StringBuilder()
				.append(System.getProperty("govway_base_path"));
		if (tipoServizio == TipoServizio.FRUIZIONE)
			urlBuilder.append("/out/SoggettoInternoTestFruitore");
		urlBuilder.append("/SoggettoInternoTest")
			.append("/TestConnettoreStatus")
			.append( isSoap ? "Soap" : "Rest")
			.append("/v1")
			.append(operation);
		
		
		HttpRequest request = new HttpRequest();
		request.setUrl(urlBuilder.toString());

		if(isSoap) {
			request.setMethod(HttpRequestMethod.POST);
			String contentType = null;
			byte[]content = null;
			if(MessageType.SOAP_11.equals(messageType)) {
				request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, operation.substring(1));
				contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
				content = Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes();
			}
			else {
				contentType = HttpConstants.CONTENT_TYPE_SOAP_1_2;
				content = Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes();
			}
			
			request.setContent(content);
			request.setContentType(contentType);
		
		} else {
			if(MessageType.BINARY.equals(messageType)) {
				request.setMethod(HttpRequestMethod.GET);
			}
			else {
				request.setMethod(HttpRequestMethod.POST);
				String contentType = null;
				byte[]content = null;
				if(MessageType.JSON.equals(messageType)) {
					contentType = HttpConstants.CONTENT_TYPE_JSON;
					content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
				}
				else if(MessageType.XML.equals(messageType)) {
					contentType = HttpConstants.CONTENT_TYPE_XML;
					content = Bodies.getXML(Bodies.SMALL_SIZE).getBytes();
				}
				else if(MessageType.MIME_MULTIPART.equals(messageType)) {
					try {
						MimeMultipart mm = Bodies.getMultipartMixed(Bodies.SMALL_SIZE);
						contentType = mm.getContentType();
						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						mm.writeTo(bout);
						bout.flush();
						bout.close();
						content = bout.toByteArray();
					}catch(Exception e) {
						throw new UtilsException(e.getMessage(),e);
					}
				}
				request.setContent(content);
				request.setContentType(contentType);
			}
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
	
	private static int getReturnCodeError(MessageType messageType) {
		return isSoap(messageType) ? 500 : 503;
	}
	
	public static void testSingleOkResponse(MessageType messageType, String operazione, TipoServizio tipoServizio, ConnettoreStatusResponseType responseType) throws UtilsException {
		ConfigLoader.getLoggerCore().debug("Inizio test verifica response {}, operazione: {}, tipoServizio:{})", messageType, operazione, tipoServizio);
		
		HttpResponse response = sendRequest(messageType, tipoServizio, operazione, 200);
		
		assertEquals(ConnettoreStatus.getContentType(responseType, messageType), response.getContentType());
		assertEquals(ConnettoreStatus.getMessage(responseType, messageType), new String(response.getContent()));
	}
	
	public static void testSingleConnettivita(MessageType messageType, TipoServizio tipoServizio) throws UtilsException, SQLQueryObjectException {
		ConfigLoader.getLoggerCore().debug("Inizio test verifica connettivita {}, tipoServizio:{})", messageType, tipoServizio);
		
		HttpResponse response = sendRequest(messageType, tipoServizio, StatusUtils.OPERATION_STATUS_CONNETTIVITA, getReturnCodeError(messageType));
		String idTransazione = getIdTransazione(response);
		
		String check = ".*rilevata anomalia; connettività fallita verso il connettore 'ConnettoreErrorRefused' del gruppo 'testRefused': Connection refused.*";
		if(TipoServizio.FRUIZIONE.equals(tipoServizio)) {
			check = ".*rilevata anomalia; connettività fallita verso il connettore del gruppo 'testRefused': Connection refused.*";
		}
		
		DBVerifier.checkMsgDiag(idTransazione, check);
	}
	
	public static void testSingleStatistica(MessageType messageType, TipoServizio tipoServizio) throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		ConfigLoader.getLoggerCore().debug("Inizio test verifica statistica {}, tipoServizio:{})", messageType, tipoServizio);
		
		StatusUtils.testSingleStatisticaEmpty(messageType, tipoServizio);
		StatusUtils.testSingleStatisticaErrors(messageType, tipoServizio);
		StatusUtils.testSingleStatisticaOk(messageType, tipoServizio);
	}
	
	private static void testSingleStatisticaEmpty(MessageType messageType, TipoServizio tipoServizio) throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		ConfigLoader.getLoggerCore().debug("Inizio test verifica statistica con statistiche vuote");
		DBVerifier.resetStatistiche(isSoap(messageType) ? SERVIZIO_SOAP : SERVIZIO_REST);
		ConfigLoader.resetCache(false, CACHE_CONTROLLO_TRAFFICO);
		
		sendRequest(messageType, tipoServizio, StatusUtils.OPERATION_STATUS_STATISTICA, 200);
	}
		
	private static void testSingleStatisticaErrors(MessageType messageType, TipoServizio tipoServizio) throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		ConfigLoader.getLoggerCore().debug("Inizio test verifica statistica con statistiche solo errori");
		DBVerifier.resetStatistiche(isSoap(messageType) ? SERVIZIO_SOAP : SERVIZIO_REST);
		ConfigLoader.resetCache(false, CACHE_CONTROLLO_TRAFFICO);
		
		// invio richiesta alla porta che produce errori
		sendRequest(messageType, tipoServizio, StatusUtils.OPERATION_TEST_ERR, 500);
		
		// ricarico le statistiche
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.waitForDbStats();
		HttpResponse response = sendRequest(messageType, tipoServizio, StatusUtils.OPERATION_STATUS_STATISTICA, getReturnCodeError(messageType));
		String idTransazione = getIdTransazione(response);

		DBVerifier.checkMsgDiag(idTransazione, ".* trovate: 1 transazioni con errore, nessuna transazione ok");

	}
	
	private static void testSingleStatisticaOk(MessageType messageType, TipoServizio tipoServizio) throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		ConfigLoader.getLoggerCore().debug("Inizio test verifica statistica con statistiche solo errori");
		DBVerifier.resetStatistiche(isSoap(messageType) ? SERVIZIO_SOAP : SERVIZIO_REST);
		ConfigLoader.resetCache(false, CACHE_CONTROLLO_TRAFFICO);
		
		// invio richiesta alla porta che produce errori
		sendRequest(messageType, tipoServizio, StatusUtils.OPERATION_TEST_ERR, 500);
		
		// invio richiesta alla porta che non produce errori
		sendRequest(messageType, tipoServizio,StatusUtils.OPERATION_TEST_OK, 200);
		
		// ricarico le statistiche
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.waitForDbStats();
		sendRequest(messageType, tipoServizio, StatusUtils.OPERATION_STATUS_STATISTICA, 200);
	}
	
	public static void testSingleLifetime(MessageType messageType, TipoServizio tipoServizio) throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		ConfigLoader.getLoggerCore().debug("Inizio test custom cache lifetime {}, tipoServizio:{})", messageType, tipoServizio);
		
		DBVerifier.resetStatistiche(isSoap(messageType) ? SERVIZIO_SOAP : SERVIZIO_REST);
		ConfigLoader.resetCache(false, CACHE_CONTROLLO_TRAFFICO);

		// invio richiesta per inizializzare la cache delle statistiche
		sendRequest(messageType, tipoServizio, StatusUtils.OPERATION_STATUS_CACHE_LIFETIME, 200);
		
		//invio richiesta alla porta che produce errori
		sendRequest(messageType, tipoServizio, StatusUtils.OPERATION_TEST_ERR, 500);
		
		// invio richiesta per ottenere il risultato in cache
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.waitForDbStats();
		sendRequest(messageType, tipoServizio, StatusUtils.OPERATION_STATUS_CACHE_LIFETIME, 200);
		
		// aspetto che il life time custom invalidi la cache
		if(Utils.isJenkins()) {
			Utilities.sleep(15000);
		}
		else {
			Utilities.sleep(5000);
		}

		// invio richiesta per ottenere il nuovo risultato
		sendRequest(messageType, tipoServizio, StatusUtils.OPERATION_STATUS_CACHE_LIFETIME, getReturnCodeError(messageType));
		
		
	}
}
