/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
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

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste.NumeroRichiestePolicyInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicException;
import org.openspcoop2.pdd.core.dynamic.PatternExtractor;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
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
	
	@SuppressWarnings("deprecation")
	private static String buildUrl(Map<String,String> propertiesURLBased, String urlBase) {
		return TransportUtils.buildLocationWithURLBasedParameter(propertiesURLBased, urlBase);
	}
	
	public enum PolicyAlias {
		RICHIESTE_SIMULTANEE("RichiesteSimultanee"), 
		MINUTO("Minuto"),	
		ORARIO("Orario"),
		GIORNALIERO("Giornaliero"),
		MINUTODEFAULT("MinutoDefault"),
		FILTROHEADER("FiltroHeader"),
		FILTROURLINVOCAZIONE("FiltroUrlInvocazione"),
		FILTROPARAMETROURL("FiltroParametroUrl"),
		FILTROSOAPACTION("FiltroSoapAction"),
		FILTROCONTENUTO("FiltroContenuto"),
		FILTROXFORWARDEDFOR("FiltroX-Forwarded-For"),
		FILTRORISORSA("FiltroRisorsa"),
		NO_POLICY("NO_POLICY");
				
		public final String value;
		
		PolicyAlias(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return this.value;
		}
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
	
	public static Logger logRateLimiting = ConfigLoader.getLoggerRateLimiting();
	 
	
	public static Future<HttpResponse> makeBackgroundRequest(HttpRequest request) {
		logRateLimiting = ConfigLoader.getLoggerCore();

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
		
		return executor.submit(() -> {
				try {
					logRateLimiting.info(request.getMethod() + " " + request.getUrl());
					HttpResponse resp = HttpUtilities.httpInvoke(request);
					logRateLimiting.info("Richiesta effettuata..");
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
		logRateLimiting = ConfigLoader.getLoggerRateLimiting();

		final Vector<Future<HttpResponse>> responses = new Vector<>();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(count);

		for (int i = 0; i < count; i++) {
			
			if (request_delay > 0) {
				org.openspcoop2.utils.Utilities.sleep(request_delay);
			}
			
			var future = executor.submit(() -> {
				try {
					logRateLimiting.info(request.getMethod() + " " + request.getUrl());
					HttpResponse resp = HttpUtilities.httpInvoke(request);
					logRateLimiting.info("Richiesta effettuata..");
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
	 * Esegue `count` richieste `request` parallele 
	 * 
	 */
	public static Vector<HttpResponse> makeParallelRequests(HttpRequest request, int count) {
		
		if (count < 0) {
			throw new IllegalArgumentException("Request count must be > 0");
		} else if (count == 0) {
			return new Vector<>();
		}
		
		logRateLimiting = ConfigLoader.getLoggerRateLimiting();

		final Vector<HttpResponse> responses = new Vector<>();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(count);

		for (int i = 0; i < count; i++) {
			executor.execute(() -> {
				try {
					logRateLimiting.info(request.getMethod() + " " + request.getUrl());
					responses.add(HttpUtilities.httpInvoke(request));
					logRateLimiting.info("Richiesta effettuata..");
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
			logRateLimiting.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		
		logRateLimiting.info("RESPONSES: ");
		responses.forEach(r -> {
			logRateLimiting.info("statusCode: " + r.getResultHTTPOperation());
			logRateLimiting.info("headers: " + r.getHeadersValues());
		});

		return responses;
	}
	
	/*
	 * Esegue `count` richieste sequenziali per ognuno dei `nthread' threads.
	 * Le richieste dei threads vanno in parallelo.
	 */
	public static Vector<HttpResponse> makeParallelRequests(HttpRequest request, int nthreads, int reqsPerThread) {
		logRateLimiting = ConfigLoader.getLoggerRateLimiting();

		final Vector<HttpResponse> responses = new Vector<>();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nthreads);

		for (int i = 0; i < nthreads; i++) {
			executor.execute(() -> {
				responses.addAll(makeSequentialRequests(request, reqsPerThread));				
			});
		}
		
		try {
			executor.shutdown();
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logRateLimiting.error("Le richieste hanno impiegato più di venti secondi!");
			throw new RuntimeException(e);
		}
		
		logRateLimiting.info("RESPONSES: ");
		responses.forEach(r -> {
			logRateLimiting.info("statusCode: " + r.getResultHTTPOperation());
			logRateLimiting.info("headers: " + r.getHeadersValues());
		});

		return responses;
	}
	
	
	public static HttpResponse makeRequest(HttpRequest request) {
		
		try {
			logRateLimiting.info(request.getMethod() + " " + request.getUrl());
			HttpResponse ret = HttpUtilities.httpInvoke(request);
			logRateLimiting.info("statusCode: " + ret.getResultHTTPOperation());
			logRateLimiting.info("headers: " + ret.getHeadersValues());
			return ret;
		} catch (UtilsException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static Vector<HttpResponse> makeSequentialRequests(HttpRequest request, int count) {
		final Vector<HttpResponse> responses = new Vector<>();

		for(int i=0; i<count;i++) {
			logRateLimiting.info(request.getMethod() + " " + request.getUrl());
			try {
				responses.add(HttpUtilities.httpInvoke(request));
				logRateLimiting.info("Richiesta effettuata..");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}		
		}
		
		logRateLimiting.info("RESPONSES: ");
		responses.forEach(r -> {
			logRateLimiting.info("statusCode: " + r.getResultHTTPOperation());
			logRateLimiting.info("headers: " + r.getHeadersValues());
		});
		
		return responses;		
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
	
	public static void matchLimitExceededRest(JSONObject jsonResp) throws Exception {
		JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
		assertEquals("https://govway.org/handling-errors/429/LimitExceeded.html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
		assertEquals("LimitExceeded", jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
		assertEquals(429, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
		assertNotEquals(null, jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0));	
		assertEquals("Limit exceeded detected", jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0));
		
	}
	
	/**
	 * Testa la risposta del server di echo quando passo il parametro query problem=true
	 * @param jsonResp
	 * @throws Exception
	 */
	public static void matchEchoFaultResponseRest(JSONObject jsonResp) throws Exception {
		JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();

		assertEquals("https://httpstatuses.com/500", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
		assertEquals("Internal Server Error", jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
		assertEquals("Problem ritornato dalla servlet di trace, esempio di OpenSPCoop", jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0));
	}


	
	/*
	 * Usare il metodo con i bytes, viene lasciato se servisse, ma la creazione di un XML a partire da un HTML produce un <META non chiuso
	public static void matchLimitExceededSoap(Element element) {
		_matchLimitSoap(element, "Limit Exceeded", "Limit exceeded detected");
	}
	public static void matchTooManyRequestsSoap(Element element) {
		_matchLimitSoap(element, "Too Many Requests", "Too many requests detected");
	}
	*/
	@SuppressWarnings("unused")
	private static void _matchLimitSoap(Element element,String code, String details) {
		
		String forSearch = null;
		try {
			
			try {
				String s = XMLUtils.getInstance().toString(element);
				if(s.contains("<META") && !s.contains("</META")) {
					int fromIndex = s.indexOf("<META");
					int endIndex = s.indexOf(">", fromIndex+"<META".length());
					String meta = s.substring(fromIndex, endIndex+1);
					//System.out.println("META ["+meta+"]");
					forSearch = s.replace(meta, "");
					//System.out.println("S FINALE ["+forSearch+"]");
				}
				else {
					forSearch = s;
				}
			}catch(Throwable t) {
				System.out.println("Errore: "+t.getMessage());
				throw new Exception(t.getMessage(),t);
			}
			
			XPathExpressionEngine xpathEngine = new XPathExpressionEngine();
			assertEquals(code, xpathEngine.getStringMatchPattern(forSearch, null, "/html/head/title/text()"));
			assertEquals(code, xpathEngine.getStringMatchPattern(forSearch, null, "/html/body/h1/text()"));
			assertEquals(details, xpathEngine.getStringMatchPattern(forSearch, null, "/html/body/p/text()"));
		} catch (Exception e) {
			String errore = "MESSAGGIO ERRATO '"+forSearch+"': ";
			throw new RuntimeException(errore+e.getMessage(),e);
		}
	}
	
	
	public static void matchLimitExceededSoap(byte[] element) {
		_matchLimitSoap(element, "Limit Exceeded", "Limit exceeded detected");
	}
	public static void matchTooManyRequestsSoap(byte[] element) {
		_matchLimitSoap(element, "Too Many Requests", "Too many requests detected");
	}
	private static void _matchLimitSoap(byte[] element,String code, String details) {
		
		String forSearch = null;
		try {
			
			forSearch = new String(element);
			
			XPathExpressionEngine xpathEngine = new XPathExpressionEngine();
			assertEquals(code, xpathEngine.getStringMatchPattern(forSearch, null, "/html/head/title/text()"));
			assertEquals(code, xpathEngine.getStringMatchPattern(forSearch, null, "/html/body/h1/text()"));
			assertEquals(details, xpathEngine.getStringMatchPattern(forSearch, null, "/html/body/p/text()"));
		} catch (Exception e) {
			String errore = "MESSAGGIO ERRATO '"+forSearch+"': ";
			throw new RuntimeException(errore+e.getMessage(),e);
		}
	}
	
	
	public static void matchApiUnavaliableSoap(String idTransazione, Element element) throws DynamicException {
		PatternExtractor matcher = PatternExtractor.getXmlPatternExtractor(element, logRateLimiting, false, null); 
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
		PatternExtractor matcher = PatternExtractor.getXmlPatternExtractor(element, logRateLimiting, false, null); 
		String prefix = "env:";
		assertEquals("idTransazione:"+idTransazione, prefix+"Receiver", matcher.read("/"+prefix+"Envelope/"+prefix+"Body/"+prefix+"Fault/"+prefix+"Code/"+prefix+"Value/text()"));
		assertEquals("idTransazione:"+idTransazione, "ns1:Server.OpenSPCoopExampleFault", matcher.read("/"+prefix+"Envelope/"+prefix+"Body/"+prefix+"Fault/"+prefix+"Code/"+prefix+"Subcode/"+prefix+"Value/text()"));
		assertEquals("idTransazione:"+idTransazione, "Fault ritornato dalla servlet di trace, esempio di OpenSPCoop", matcher.read("/"+prefix+"Envelope/"+prefix+"Body/"+prefix+"Fault/"+prefix+"Reason/"+prefix+"Text/text()"));
		assertEquals("idTransazione:"+idTransazione, "OpenSPCoopTrace", matcher.read("/"+prefix+"Envelope/"+prefix+"Body/"+prefix+"Fault/"+prefix+"Role/text()"));
	}
	
	
	public static void resetCounters(String idPolicy) {
		Map<String,String> queryParams = Map.of(
				"resourceName", "ControlloTraffico",
				"methodName", "resetPolicyCounters",
				"paramValue", idPolicy
			);
		String jmxUrl = buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
		logRateLimiting.info("Resetto la policy di rate limiting sulla url: " + jmxUrl );
		
		try {
			String resp = new String(HttpUtilities.getHTTPResponse(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password")).getContent());
			logRateLimiting.info(resp);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//HttpUtilities.check(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password"));
	}
	
	
	public static String getPolicy(String idPolicy) {
		Map<String,String> queryParams = Map.of(
				"resourceName", "ControlloTraffico",
				"methodName", "getPolicy",
				"paramValue", idPolicy
			);
		String jmxUrl = buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
		logRateLimiting.info("Ottengo le informazioni sullo stato della policy: " + jmxUrl );
		try {
			return new String(HttpUtilities.getHTTPResponse(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password")).getContent());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static void toggleErrorDisclosure(boolean enabled) {
	
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
			String jmxUrl = buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
			logRateLimiting.info("Imposto la error disclosure: " + jmxUrl );
				
			try {
				HttpUtilities.check(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password"));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
		
	
	public static Integer getThreadsAttiviGovWay() {
		
		Map<String,String> queryParams = Map.of(
				"resourceName", "ControlloTraffico",
				"attributeName", "threadsAttivi"
			);
		
		String jmxUrl = buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
		logRateLimiting.info("Ottengo le informazioni sul numero dei threads attivi: " + jmxUrl );
		try {
			return Integer.valueOf(
					new String(HttpUtilities.getHTTPResponse(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password")).getContent())
				);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}


	public static void waitForPolicy(PolicyAlias policy) {
		switch (policy) {
		case MINUTO:
		case MINUTODEFAULT:
			waitForNewMinute();
			break;
		case ORARIO:
		case FILTROHEADER:
		case FILTROCONTENUTO:
		case FILTROPARAMETROURL:
		case FILTROSOAPACTION:
		case FILTROURLINVOCAZIONE:
		case FILTROXFORWARDEDFOR:
		case FILTRORISORSA:
			waitForNewHour();
			break;
		case GIORNALIERO:
			waitForNewDay();
			break;
		case RICHIESTE_SIMULTANEE:
			break;
		default:
			break;
		}
		
	}	
	
	
	/**
	 * Sospende il thread corrente finchè non è scoccato il prossimo minuto
	 * 
	 * @throws InterruptedException
	 */
	public static void waitForNewMinute() {
		if ("false".equals(System.getProperty("wait"))) {
			return;
		}
		
		final int threshold = Integer.valueOf(System.getProperty("threshold_minute"));
	
		Calendar now = Calendar.getInstance();
		int remaining = 60 - now.get(Calendar.SECOND);
		if (remaining <= threshold) {

			int to_wait = (remaining+2) * 1000;
			logRateLimiting.info("Aspetto " + to_wait/1000 + " secondi per lo scoccare del minuto..");
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
			logRateLimiting.info("Aspetto " + to_wait/1000 + " secondi per lo scoccare dell'ora..");
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
				logRateLimiting.info("Aspetto " + to_wait/1000 + " secondi per lo scoccare del nuovo giorno..");
				org.openspcoop2.utils.Utilities.sleep(to_wait);
			}							
		}			
	}


	
	public static void waitForZeroGovWayThreads() {
		
		int remainingChecks = Integer.valueOf(System.getProperty("rl_check_policy_conditions_retry"));
		int delay = Integer.valueOf(System.getProperty("rl_check_policy_conditions_delay"));
		
		while(true) {
			try {
				Integer threadAttivi = getThreadsAttiviGovWay();
				assertEquals(Integer.valueOf(0), threadAttivi);
				break;
			} catch (AssertionError e) {
				if(remainingChecks == 0) {
					throw e;
				}
				remainingChecks--;
				org.openspcoop2.utils.Utilities.sleep(delay);
			}
		}
		
	}
	
	
	public static void waitForDbStats() {
		int to_wait = Integer.valueOf(System.getProperty("statistiche_delay"));
		logRateLimiting.info("Aspetto " + to_wait/1000 + " secondi affinchè le statistiche vengano generate...");
		org.openspcoop2.utils.Utilities.sleep(to_wait);
	}

	public static void checkConditionsNumeroRichieste(String idPolicy, Integer attive, Integer conteggiate, Integer bloccate) {
		int remainingChecks = Integer.valueOf(System.getProperty("rl_check_policy_conditions_retry"));
		int delay = Integer.valueOf(System.getProperty("rl_check_policy_conditions_delay"));
		
		while(true) {
			try {
				String jmxPolicyInfo = getPolicy(idPolicy);
				logRateLimiting.info(jmxPolicyInfo);
				
				if (jmxPolicyInfo.equals(PolicyFields.NOPOLICYINFO)) {
					break;
				}
				
				NumeroRichiestePolicyInfo polInfo = new NumeroRichiestePolicyInfo(jmxPolicyInfo);
			
				assertEquals(attive, polInfo.richiesteAttive);
				assertEquals(conteggiate, polInfo.richiesteConteggiate);
				assertEquals(bloccate, polInfo.richiesteBloccate);
				break;
			} catch (AssertionError e) {
				if(remainingChecks == 0) {
					throw e;
				}
				remainingChecks--;
				org.openspcoop2.utils.Utilities.sleep(delay);
			}
		}		
	}
	

	public static void waitForZeroActiveRequests(String idPolicy, int richiesteConteggiate) {
		Logger logRateLimiting = LoggerWrapperFactory.getLogger("testsuite.rate_limiting");
		
		int remainingChecks = Integer.valueOf(System.getProperty("rl_check_policy_conditions_retry"));
		int delay = Integer.valueOf(System.getProperty("rl_check_policy_conditions_delay"));

		while(true) {
			try {
				String jmxPolicyInfo = getPolicy(idPolicy);
				if (jmxPolicyInfo.equals(PolicyFields.NOPOLICYINFO)) {
					break;
				}				
				logRateLimiting.info(jmxPolicyInfo);
				Map<String, String> policyValues = parsePolicy(jmxPolicyInfo);
				assertEquals("0", policyValues.get(PolicyFields.RichiesteAttive));
				assertEquals(Integer.valueOf(richiesteConteggiate), Integer.valueOf(policyValues.get(PolicyFields.RichiesteConteggiate)));
	
				break;
			} catch (AssertionError e) {
				if(remainingChecks == 0) {
					throw e;
				}
				remainingChecks--;
				org.openspcoop2.utils.Utilities.sleep(delay);
			}
		} 
	}


	public static void checkXLimitWindows(String header, Integer currentValue, Map<Integer, Integer> windowMap) {
		String[] values = header.split(",");
		assertEquals(currentValue, Integer.valueOf(values[0]));
		for(int i=1;i<values.length;i++) {
			String[] window = values[i].trim().split(";");
			String windowSize = window[1].split("=")[1];
			assertEquals(
					windowMap.get(Integer.valueOf(windowSize)),
					Integer.valueOf(window[0])
				);
		}
	}

	/**
	 * Parsa le informazioni sulla policy ottenute tramite jmx in una map di propietà. 
	 * 
	 * @param idPolicy
	 * @return
	 */
	public static Map<String,String> parsePolicy(String jmxPolicyInfo) {
		Map<String, String> ret = new HashMap<>();
		
		String[] lines = jmxPolicyInfo.split(System.lineSeparator());
		for (String l : lines) {
			l = l.strip();
			String[] keyValue = l.split(":");
			if(keyValue.length == 2) {
				ret.put(keyValue[0].strip(), keyValue[1].strip());
			}
		}
		return ret;
	}


	public static void checkXLimitHeader(Logger log, String nomeHeader, String header, int maxLimit) {
		// La configurazione di govway potrebbe utilizzare le window anche se nel test la proprietà
		// è disabilitata. Scriviamo un test che sia indipendente da questa cosa, e che controlli
		// in ogni caso il primo valore.
		log.info("Verifica valore header '"+nomeHeader+"'; trovato valore '"+header+"'");
		if(header==null) {
			log.error("Header '"+nomeHeader+"' non presente o non valorizzato");
		}
		String limit = header.split(",")[0].trim();
		assertEquals(String.valueOf(maxLimit),limit);
	}


	public static int getPolicyWindowSize(PolicyAlias policy) {
		switch (policy) {
		case GIORNALIERO:
			return 86400;
		case MINUTO:
			return 60;
		case MINUTODEFAULT:
			return 60;
		case ORARIO:
		case FILTROHEADER:
		case FILTROCONTENUTO:
		case FILTROPARAMETROURL:
		case FILTROSOAPACTION:
		case FILTROURLINVOCAZIONE:
		case FILTROXFORWARDEDFOR:
		case FILTRORISORSA:
			return 3600;
		case RICHIESTE_SIMULTANEE:
			return 0;
		default:
			return 0;		
		}
	}


	public static String getPolicyPath(PolicyAlias policy) {
		switch (policy) {
		case GIORNALIERO:
			return "giornaliero";
		case MINUTO:
			return "minuto";
		case ORARIO:
		case FILTROHEADER:
		case FILTROCONTENUTO:
		case FILTROPARAMETROURL:
		case FILTROSOAPACTION:
		case FILTROURLINVOCAZIONE:
		case FILTROXFORWARDEDFOR:
		case FILTRORISORSA:
			return "orario";
		case RICHIESTE_SIMULTANEE:
			return "richieste-simultanee";
		case MINUTODEFAULT:
			return "minuto-default";
		case NO_POLICY:
			return "no-policy";
		default:
			return "";		
		}
	}


	/**
	 * Effettua n richieste sequenziali che ci si aspetta non vengano bloccate dalla policy
	 * e che vengano effettivamente conteggiate. Tra una richiesta e l'altra si aspetta
	 * che i contatori della policy vengano aggiornati.
	 * 
	 */
	
	public static Vector<HttpResponse> makeRequestsAndCheckPolicy(HttpRequest request, int count, String idPolicy) {
		final Vector<HttpResponse> responses = new Vector<>();
	
		for(int i=0; i<count;i++) {
			logRateLimiting.info(request.getMethod() + " " + request.getUrl());
			try {
				HttpResponse r = HttpUtilities.httpInvoke(request); 
				responses.add(r);
				logRateLimiting.info("Richiesta effettuata..");
				logRateLimiting.info("statusCode: " + r.getResultHTTPOperation());
				logRateLimiting.info("headers: " + r.getHeadersValues());
				checkConditionsNumeroRichieste(idPolicy, 0, i+1, 0);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}		
		}
		
		return responses;		
	}


	/**
	 * Controlla che le risposteabbiamo il valore dello header *-Remaining decrescente
	 * a partire da maxRequests-1
	 * 
	 * @param responses
	 * @param header
	 */
	
	public static void checkHeaderRemaining(Vector<HttpResponse> responses, String header, int limit) {
		for(int i=0;i<limit;i++) {
			var r = responses.get(i);
			assertEquals(limit-i-1, Integer.parseInt(r.getHeaderFirstValue(header)));
		}
	}

	public static void checkHeaderTooManyRequest(HttpResponse r) {
		String returnCode = r.getHeaderFirstValue(Headers.ReturnCode);
		boolean equalsWithReason = HeaderValues.RETURNCODE_TOO_MANY_REQUESTS.equalsIgnoreCase(returnCode);
		boolean equalsWithoutReason = HeaderValues.RETURNCODE_TOO_MANY_REQUESTS_NO_REASON.equalsIgnoreCase(returnCode);
		assertTrue("Verifico return code 429: '"+returnCode+"'", (equalsWithReason || equalsWithoutReason));
	}
}
