/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openspcoop2.pdd.core.dynamic.DynamicException;
import org.openspcoop2.pdd.core.dynamic.PatternExtractor;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import net.minidev.json.JSONObject;

/**
* Utils
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
* 
*/
public class Utils {

	public static boolean isJenkins() {
		String j = System.getProperty("jenkins");
		if(j!=null) {
			return "true".equalsIgnoreCase(j);
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static String buildUrl(Map<String,String> propertiesURLBased, String urlBase) {
		return TransportUtils.buildLocationWithURLBasedParameter(propertiesURLBased, urlBase);
	}
	
	private static final DocumentBuilder docBuilder;
	static {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			docBuilder = dbf.newDocumentBuilder();
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	public static Element buildXmlElement(byte[] content) {
		InputStream is = new ByteArrayInputStream(content);
		Document doc;
		try {
			doc = docBuilder.parse(is);
			return doc.getDocumentElement();
		} catch (SAXException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * Testa la risposta del server di echo quando passo il parametro query problem=true
	 * @param jsonResp
	 * @throws Exception
	 */
	public static void matchEchoFaultResponseRest(JSONObject jsonResp) throws Exception {
		Logger log = ConfigLoader.getLoggerCore();
		matchEchoFaultResponseRest(jsonResp, log);
	}
	public static void matchEchoFaultResponseRest(JSONObject jsonResp, Logger log) throws Exception {
		JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();

		assertEquals("https://httpstatuses.com/500", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
		assertEquals("Internal Server Error", jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
		assertEquals("Problem ritornato dalla servlet di trace, esempio di OpenSPCoop", jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0));
	}
	
	public static void matchApiUnavaliableSoap(String idTransazione, Element element) throws DynamicException {
		Logger log = ConfigLoader.getLoggerCore();
		matchApiUnavaliableSoap(idTransazione, element, log);
	}
	public static void matchApiUnavaliableSoap(String idTransazione, Element element, Logger log) throws DynamicException {
		PatternExtractor matcher = PatternExtractor.getXmlPatternExtractor(element, log, false, null); 
		String prefix = "env:";
		assertEquals("idTransazione:"+idTransazione, prefix+"Receiver", matcher.read("/"+prefix+"Envelope/"+prefix+"Body/"+prefix+"Fault/"+prefix+"Code/"+prefix+"Value/text()"));
		assertEquals("idTransazione:"+idTransazione, "integration:APIUnavailable", matcher.read("/"+prefix+"Envelope/"+prefix+"Body/"+prefix+"Fault/"+prefix+"Code/"+prefix+"Subcode/"+prefix+"Value/text()"));
		assertEquals("idTransazione:"+idTransazione, "The API Implementation is temporary unavailable", matcher.read("/"+prefix+"Envelope/"+prefix+"Body/"+prefix+"Fault/"+prefix+"Reason/"+prefix+"Text/text()"));
		assertEquals("idTransazione:"+idTransazione, "http://govway.org/integration", matcher.read("/"+prefix+"Envelope/"+prefix+"Body/"+prefix+"Fault/"+prefix+"Role/text()"));	
	}
	
	/**
	 * Testa la risposta del server di echo quando passo il parametro query fault=true
	 * 
	 * @param element
	 * @throws DynamicException
	 */
	public static void matchEchoFaultResponseSoap(String idTransazione, Element element) throws DynamicException {
		Logger log = ConfigLoader.getLoggerCore();
		matchEchoFaultResponseSoap(idTransazione, element, log);
	}
	public static void matchEchoFaultResponseSoap(String idTransazione, Element element, Logger log) throws DynamicException {
		PatternExtractor matcher = PatternExtractor.getXmlPatternExtractor(element, log, false, null); 
		String prefix = "env:";
		assertEquals("idTransazione:"+idTransazione, prefix+"Receiver", matcher.read("/"+prefix+"Envelope/"+prefix+"Body/"+prefix+"Fault/"+prefix+"Code/"+prefix+"Value/text()"));
		assertEquals("idTransazione:"+idTransazione, "ns1:Server.OpenSPCoopExampleFault", matcher.read("/"+prefix+"Envelope/"+prefix+"Body/"+prefix+"Fault/"+prefix+"Code/"+prefix+"Subcode/"+prefix+"Value/text()"));
		assertEquals("idTransazione:"+idTransazione, "Fault ritornato dalla servlet di trace, esempio di OpenSPCoop", matcher.read("/"+prefix+"Envelope/"+prefix+"Body/"+prefix+"Fault/"+prefix+"Reason/"+prefix+"Text/text()"));
		assertEquals("idTransazione:"+idTransazione, "OpenSPCoopTrace", matcher.read("/"+prefix+"Envelope/"+prefix+"Body/"+prefix+"Fault/"+prefix+"Role/text()"));
	}

	
	public static void toggleErrorDisclosure(boolean enabled) {
		Logger log = ConfigLoader.getLoggerCore();
		toggleErrorDisclosure(enabled, log);
	}
	public static void toggleErrorDisclosure(boolean enabled, Logger log) {
		
		String value = "false";
		if (enabled) {
			value = "true";
		}
		
		List<Map<String, String>> requestsParams = List.of(
				Map.of(
						"resourceName", "ConfigurazionePdD",
						"attributeName", "transactionErrorForceSpecificTypeInternalResponseError",
						"attributeBooleanValue", value
					),
				Map.of(
						"resourceName", "ConfigurazionePdD",
						"attributeName", "transactionErrorForceSpecificTypeBadResponse",
						"attributeBooleanValue", value
					),
				Map.of(
						"resourceName", "ConfigurazionePdD",
						"attributeName", "transactionErrorForceSpecificDetails",
						"attributeBooleanValue", value
					)
				);
				
		requestsParams.forEach( queryParams -> {
			String jmxUrl = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
			log.info("Imposto la error disclosure: " + jmxUrl );
				
			try {
				HttpUtilities.check(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password"));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	
	
	
	
	public static HttpResponse makeRequest(HttpRequest request) {
		Logger log = ConfigLoader.getLoggerCore();
		return makeRequest(request, log);
	}
	public static HttpResponse makeRequest(HttpRequest request, Logger log) {
		
		try {
			log.info(request.getMethod() + " " + request.getUrl());
			HttpResponse ret = HttpUtilities.httpInvoke(request);
			log.info("statusCode: " + ret.getResultHTTPOperation());
			log.info("headers: " + ret.getHeadersValues());
			return ret;
		} catch (UtilsException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static Vector<HttpResponse> makeSequentialRequests(HttpRequest request, int count) {
		Logger log = ConfigLoader.getLoggerCore();
		return makeSequentialRequests(request, count, log);
	}
	public static Vector<HttpResponse> makeSequentialRequests(HttpRequest request, int count, Logger log) {
		final Vector<HttpResponse> responses = new Vector<>();

		for(int i=0; i<count;i++) {
			log.info(request.getMethod() + " " + request.getUrl());
			try {
				responses.add(HttpUtilities.httpInvoke(request));
				log.info("Richiesta effettuata..");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}		
		}
		
		log.info("RESPONSES: ");
		responses.forEach(r -> {
			log.info("statusCode: " + r.getResultHTTPOperation());
			log.info("headers: " + r.getHeadersValues());
		});
		
		return responses;		
	}
	
	
	
	
	public static Future<HttpResponse> makeBackgroundRequest(HttpRequest request) {
		Logger log = ConfigLoader.getLoggerCore();
		return makeBackgroundRequest(request, log);
	}
	public static Future<HttpResponse> makeBackgroundRequest(HttpRequest request, Logger log) {

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
		
		return executor.submit(() -> {
				try {
					log.info(request.getMethod() + " " + request.getUrl());
					HttpResponse resp = HttpUtilities.httpInvoke(request);
					log.info("Richiesta effettuata..");
					return resp;
				} catch (UtilsException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			});
	}
	
	
	/**
	 * Esegue `count` richieste `request` parallele in background e restituisce le futures per le relative
	 * risposte 
	 * 
	 */
	public static Vector<Future<HttpResponse>> makeBackgroundRequests(HttpRequest request, int count, int request_delay) {
		Logger log = ConfigLoader.getLoggerCore();
		return makeBackgroundRequests(request, count, request_delay, log);
	}
	public static Vector<Future<HttpResponse>> makeBackgroundRequests(HttpRequest request, int count, int request_delay, Logger log) {
		
		final Vector<Future<HttpResponse>> responses = new Vector<>();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(count);

		for (int i = 0; i < count; i++) {
			
			if (request_delay > 0) {
				org.openspcoop2.utils.Utilities.sleep(request_delay);
			}
			
			var future = executor.submit(() -> {
				try {
					log.info(request.getMethod() + " " + request.getUrl());
					HttpResponse resp = HttpUtilities.httpInvoke(request);
					log.info("Richiesta effettuata..");
					return resp;
				} catch (UtilsException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			});
			responses.add(future);
		}		

		return responses;
	}
	
	
	
	/**
	 * Esegue `count` richieste `request` parallele 
	 * 
	 */
	public static Vector<HttpResponse> makeParallelRequests(HttpRequest request, int count) {
		Logger log = ConfigLoader.getLoggerCore();
		return makeParallelRequests(request, count, log);
	}
	public static Vector<HttpResponse> makeParallelRequests(HttpRequest request, int count, Logger log) {
		
		if (count < 0) {
			throw new IllegalArgumentException("Request count must be > 0");
		} else if (count == 0) {
			return new Vector<>();
		}
		
		final Vector<HttpResponse> responses = new Vector<>();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(count);

		for (int i = 0; i < count; i++) {
			executor.execute(() -> {
				try {
					log.info(request.getMethod() + " " + request.getUrl());
					responses.add(HttpUtilities.httpInvoke(request));
					log.info("Richiesta effettuata..");
				} catch (UtilsException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			});
		}
		
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		
		log.info("RESPONSES: ");
		responses.forEach(r -> {
			log.info("statusCode: " + r.getResultHTTPOperation());
			log.info("headers: " + r.getHeadersValues());
		});

		return responses;
	}
	
	
	
	public static Vector<HttpResponse> awaitResponses(Vector<Future<HttpResponse>> futureBlockingResponses) {
		Vector<HttpResponse> responses = new Vector<>();
		
		for(var blockingRespFuture : futureBlockingResponses) {
			try {
				responses.add(blockingRespFuture.get());
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		return responses;
	}

	
	
	
	/**
	 * Sospende il thread corrente finchè non è scoccato il prossimo minuto
	 * 
	 * @throws InterruptedException
	 */
	public static void waitForNewMinute() {
		Logger log = ConfigLoader.getLoggerCore();
		waitForNewMinute(log);
	}
	public static void waitForNewMinute(Logger log) {
		if ("false".equals(System.getProperty("wait"))) {
			return;
		}
		
		final int threshold = Integer.valueOf(System.getProperty("threshold_minute"));
	
		Calendar now = Calendar.getInstance();
		int remaining = 60 - now.get(Calendar.SECOND);
		if (remaining <= threshold) {

			int to_wait = (remaining+2) * 1000;
			log.info("Aspetto " + to_wait/1000 + " secondi per lo scoccare del minuto..");
			org.openspcoop2.utils.Utilities.sleep(to_wait);
		}
		

				
	}
	
	/**
	 * Sospende il thread corrente finchè non è scoccata la prossima ora.
	 * NOTA: il timer viene attivato solo se mancano meno di due minuti allo scoccare dell'ora.
	 * 
	 * @throws InterruptedException
	 */
	public static void waitForNewHour() {
		Logger log = ConfigLoader.getLoggerCore();
		waitForNewHour(log);
	}
	public static void waitForNewHour(Logger log) {
		if ("false".equals(System.getProperty("wait"))) {
			return;
		}
		
		int threshold = Integer.valueOf(System.getProperty("threshold_hour"));
		
		Calendar now = Calendar.getInstance();
		int remaining_min = 60 - now.get(Calendar.MINUTE);
		int remaining_sec = 60 - now.get(Calendar.SECOND);
		int remaining = (remaining_min - 1)*60 + remaining_sec;
		
		if (remaining <= threshold) {
			int to_wait = (remaining+1) * 1000;
			log.info("Aspetto " + to_wait/1000 + " secondi per lo scoccare dell'ora..");
			org.openspcoop2.utils.Utilities.sleep(to_wait);
		}					
	}
	
	/**
	 * Sospende il thread corrente finchè non è scoccata il prossimo giorno
	 * NOTA: il timer viene attivato solo se mancano meno di due minuti allo scoccare del giorno
	 * 
	 * @throws InterruptedException
	 */
	public static void waitForNewDay() {
		Logger log = ConfigLoader.getLoggerCore();
		waitForNewDay(log);
	}
	public static void waitForNewDay(Logger log) {
		if ("false".equals(System.getProperty("wait"))) {
			return;
		}

		int threshold = Integer.valueOf(System.getProperty("threshold_day"));

		Calendar now = Calendar.getInstance();
		if (now.get(Calendar.HOUR_OF_DAY) == 23) {
			int remaining_min = 60 - now.get(Calendar.MINUTE);
			int remaining_sec = 60 - now.get(Calendar.SECOND);
			int remaining = (remaining_min - 1)*60 + remaining_sec;
		if (remaining <= threshold) {
				int to_wait = (remaining+1) * 1000;
				log.info("Aspetto " + to_wait/1000 + " secondi per lo scoccare del nuovo giorno..");
				org.openspcoop2.utils.Utilities.sleep(to_wait);
			}							
		}			
	}
	
	
	public static void waitForDbStats() {
		Logger log = ConfigLoader.getLoggerCore();
		waitForDbStats(log);
	}
	public static void waitForDbStats(Logger log) {
		int to_wait = Integer.valueOf(System.getProperty("statistiche_delay"));
		log.info("Aspetto " + to_wait/1000 + " secondi affinchè le statistiche vengano generate...");
		org.openspcoop2.utils.Utilities.sleep(to_wait);
	}
	
	public static void resetCacheToken(Logger log) {
		Map<String,String> queryParams = Map.of(
				"resourceName", "GestioneToken",
				"methodName", "resetCache"
				//,
				//"paramValue", idPolicy
			);
		String jmxUrl = buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
		log.info("Resetto la policy di rate limiting sulla url: " + jmxUrl );
		
		try {
			String resp = new String(HttpUtilities.getHTTPResponse(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password")).getContent());
			log.info(resp);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//HttpUtilities.check(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password"));
	}
	
	public static void resetCacheAutenticazione(Logger log) {
		Map<String,String> queryParams = Map.of(
				"resourceName", "DatiAutenticazione",
				"methodName", "resetCache"
				//,
				//"paramValue", idPolicy
			);
		String jmxUrl = buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
		log.info("Resetto la policy di rate limiting sulla url: " + jmxUrl );
		
		try {
			String resp = new String(HttpUtilities.getHTTPResponse(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password")).getContent());
			log.info(resp);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//HttpUtilities.check(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password"));
	}
	
	public static void resetCacheAutorizzazione(Logger log) {
		Map<String,String> queryParams = Map.of(
				"resourceName", "DatiAutorizzazione",
				"methodName", "resetCache"
				//,
				//"paramValue", idPolicy
			);
		String jmxUrl = buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
		log.info("Resetto la policy di rate limiting sulla url: " + jmxUrl );
		
		try {
			String resp = new String(HttpUtilities.getHTTPResponse(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password")).getContent());
			log.info(resp);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//HttpUtilities.check(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password"));
	}
	
	public static void resetAllCache(Logger log) {
		
		List<String> resource = new ArrayList<String>();
		resource.add("AccessoRegistroServizi");
		resource.add("ConfigurazionePdD");
		resource.add("DatiAutorizzazione");
		resource.add("DatiAutenticazione");
		resource.add("GestioneToken");
		resource.add("Keystore");
		resource.add("ResponseCaching");
		// disabilitata resource.add("ControlloTraffico");
		resource.add("LoadBalancer");
		for (String r : resource) {
			Map<String,String> queryParams = Map.of(
					"resourceName", r,
					"methodName", "resetCache"
				);
			String jmxUrl = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
			log.info("Resetto la cache "+r+" sulla url: " + jmxUrl );
			
			try {
				String resp = new String(HttpUtilities.getHTTPResponse(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password")).getContent());
				log.info(resp);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			//HttpUtilities.check(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password"));
		}
		
	}
}
