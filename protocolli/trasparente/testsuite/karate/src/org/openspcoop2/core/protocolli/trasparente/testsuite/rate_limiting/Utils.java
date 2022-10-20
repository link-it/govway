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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste.NumeroRichiestePolicyInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.slf4j.Logger;
import org.w3c.dom.Element;

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
	

	
	public static Logger logRateLimiting = ConfigLoader.getLoggerRateLimiting();
	 
	
	public static Vector<HttpResponse> makeParallelRequests(HttpRequest request, int count) {
		return org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeParallelRequests(request, count, logRateLimiting);
	}
	
	public static HttpResponse makeRequest(HttpRequest request) {
		return org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeRequest(request, logRateLimiting);
	}

	public static Vector<HttpResponse> makeSequentialRequests(HttpRequest request, int count) {
		return org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeSequentialRequests(request, count, logRateLimiting);
	}
	



	public static void matchLimitExceededRest(JSONObject jsonResp) throws Exception {
		JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
		assertEquals("https://govway.org/handling-errors/429/LimitExceeded.html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
		assertEquals("LimitExceeded", jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
		assertEquals(429, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
		assertNotEquals(null, jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0));	
		assertEquals("Limit exceeded detected", jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0));
		
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
	
	
	
	public static void matchEchoFaultResponseRest(JSONObject jsonResp) throws Exception {
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.matchEchoFaultResponseRest(jsonResp, logRateLimiting);
	}
	
	public static void matchApiUnavaliableSoap(String idTransazione, Element element) throws DynamicException {
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.matchApiUnavaliableSoap(idTransazione, element, logRateLimiting);
	}
	
	public static void matchEchoFaultResponseSoap(String idTransazione, Element element) throws DynamicException {
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.matchEchoFaultResponseSoap(idTransazione, element, logRateLimiting);
	}
	
	
	public static void resetCounters(String idPolicy) {
		Map<String,String> queryParams = Map.of(
				"resourceName", "ControlloTraffico",
				"methodName", "resetPolicyCounters",
				"paramValue", idPolicy
			);
		String jmxUrl = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
		logRateLimiting.info("Resetto la policy di rate limiting sulla url: " + jmxUrl );
		
		try {
			String resp = new String(HttpUtilities.getHTTPResponse(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password")).getContent());
			logRateLimiting.info(resp);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//HttpUtilities.check(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password"));
	}
	
	public static void ripulisciRiferimentiCacheErogazione(long idErogazione) {
		Map<String,String> queryParams = Map.of(
				"resourceName", "ConfigurazionePdD",
				"methodName", "ripulisciRiferimentiCacheErogazione",
				"paramValue", idErogazione+""
			);
		String jmxUrl = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
		logRateLimiting.info("Resetto l'erogazione "+idErogazione+" sulla url: " + jmxUrl );
		
		try {
			String resp = new String(HttpUtilities.getHTTPResponse(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password")).getContent());
			logRateLimiting.info(resp);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//HttpUtilities.check(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password"));
	}
	
	public static void ripulisciRiferimentiCacheFruizione(long idFruizione) {
		Map<String,String> queryParams = Map.of(
				"resourceName", "ConfigurazionePdD",
				"methodName", "ripulisciRiferimentiCacheFruizione",
				"paramValue", idFruizione+""
			);
		String jmxUrl = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
		logRateLimiting.info("Resetto la fruizione "+idFruizione+" sulla url: " + jmxUrl );
		
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
		String jmxUrl = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
		logRateLimiting.info("Ottengo le informazioni sullo stato della policy: " + jmxUrl );
		try {
			return new String(HttpUtilities.getHTTPResponse(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password")).getContent());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	public static Integer getThreadsAttiviGovWay() {
		
		Map<String,String> queryParams = Map.of(
				"resourceName", "ControlloTraffico",
				"attributeName", "threadsAttivi"
			);
		
		String jmxUrl = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
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
	
	
	
	public static void waitForNewMinute() {
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.waitForNewMinute(logRateLimiting);
	}
	public static void waitForNewHour() {
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.waitForNewHour(logRateLimiting);
	}
	public static void waitForNewDay() {
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.waitForNewDay(logRateLimiting);
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
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.waitForDbStats(logRateLimiting);
	}

	public static void checkConditionsNumeroRichieste(String idPolicy, Integer attive, Integer conteggiate, Integer bloccate) {
		checkConditionsNumeroRichieste(idPolicy, attive, conteggiate, bloccate, null, null);
	}
	public static void checkConditionsNumeroRichieste(String idPolicy, Integer attive, Integer conteggiate, Integer bloccate, PolicyGroupByActiveThreadsType policyType, TipoRisorsaPolicyAttiva tipoRisorsa) {
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
			
				if(policyType==null || !policyType.isInconsistent()) {
					try {
						assertEquals(attive, polInfo.richiesteAttive);
					}catch(Throwable t) {
						if(!PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE.equals(policyType) && !PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE.equals(policyType)) {
							throw t;
						}
						else {
							logRateLimiting.debug("["+policyType+"] richieste attive diverse da quelle attese per risorsa ("+tipoRisorsa+"): "+t.getMessage(),t);
						}
					}
				}
				
				logRateLimiting.debug("["+policyType+"]  ("+tipoRisorsa+") attese:"+conteggiate+" conteggiate:"+polInfo.richiesteConteggiate+"  ");
				logRateLimiting.debug("["+policyType+"]  ("+tipoRisorsa+") attese:"+bloccate+" bloccate:"+polInfo.richiesteBloccate+"  ");
				if(policyType!=null && policyType.isInconsistent()) {
					try {
						assertTrue("PolicyConteggiate["+polInfo.richiesteConteggiate+"]<=attese["+conteggiate+"]", polInfo.richiesteConteggiate<=conteggiate);
					}catch(Throwable t) {
						if(!TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI.equals(tipoRisorsa) &&
								!TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE.equals(tipoRisorsa) &&
								!TipoRisorsaPolicyAttiva.NUMERO_FAULT_APPLICATIVI.equals(tipoRisorsa) &&
								!TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO.equals(tipoRisorsa) &&
								!TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE.equals(tipoRisorsa)) {
							throw t;
						}
						else {
							logRateLimiting.debug("["+policyType+"] contatori diversi da quelli che attesi per risorsa ("+tipoRisorsa+"): "+t.getMessage(),t);
						}
					}
					assertTrue("PolicyRichiesteBloccate["+polInfo.richiesteBloccate+"]<=attese["+bloccate+"]", polInfo.richiesteBloccate<=bloccate);
				}
				else if(policyType!=null && policyType.isApproximated()) {
					if( (!org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.isJenkins())) {
						assertTrue("PolicyConteggiate["+polInfo.richiesteConteggiate+"] = (attese["+conteggiate+"] OR intervallo di approssimazione +-2)", 
								( 
										(polInfo.richiesteConteggiate==conteggiate) || 
										(polInfo.richiesteConteggiate==(conteggiate-1)) || 
										(polInfo.richiesteConteggiate==(conteggiate+1)) || 
										(polInfo.richiesteConteggiate==(conteggiate-2)) || 
										(polInfo.richiesteConteggiate==(conteggiate+2))    
								) );
					}
					else {
						// jenkins (essendo un ambiente con una sola CPU le metriche approssimate non sono facilmente verificabili)
						assertTrue("PolicyConteggiate["+polInfo.richiesteConteggiate+"] = (attese["+conteggiate+"] OR intervallo di approssimazione +-5)", 
								( 
										(polInfo.richiesteConteggiate==conteggiate) || 
										(polInfo.richiesteConteggiate==(conteggiate-1)) || 
										(polInfo.richiesteConteggiate==(conteggiate+1)) || 
										(polInfo.richiesteConteggiate==(conteggiate-2)) || 
										(polInfo.richiesteConteggiate==(conteggiate+2)) || 
										(polInfo.richiesteConteggiate==(conteggiate-3)) || 
										(polInfo.richiesteConteggiate==(conteggiate+3)) || 
										(polInfo.richiesteConteggiate==(conteggiate-4)) || 
										(polInfo.richiesteConteggiate==(conteggiate+4)) || 
										(polInfo.richiesteConteggiate==(conteggiate-5)) || 
										(polInfo.richiesteConteggiate==(conteggiate+5))   
								) );
					}
					
					assertTrue("PolicyRichiesteBloccate["+polInfo.richiesteBloccate+"]<=attese["+bloccate+"]", polInfo.richiesteBloccate<=bloccate);
				}
				else {
					try {
						assertEquals("PolicyConteggiate["+polInfo.richiesteConteggiate+"]=attese["+conteggiate+"] (policyType:"+policyType+" jenkins:"+org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.isJenkins()+")",conteggiate, polInfo.richiesteConteggiate);
					}catch(Throwable t) {
						if( (!org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.isJenkins()) || 
								(
										!PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG.equals(policyType) &&
										!PolicyGroupByActiveThreadsType.REDISSON_ATOMIC_LONG.equals(policyType) 
								)
						) {
							throw t;
						}
						else {
							// jenkins
							if( ((polInfo.richiesteConteggiate)==(conteggiate+1))
									||
									((polInfo.richiesteConteggiate)==(conteggiate+2))) {
								logRateLimiting.debug("PolicyConteggiate["+polInfo.richiesteConteggiate+"]=attese["+conteggiate+"] con scarto di 2 [tolleranza] ("+tipoRisorsa+"): "+t.getMessage(),t);
							}
							else {
								throw t;
							}
						}
					}
					
					try {
						assertEquals("PolicyRichiesteBloccate["+polInfo.richiesteBloccate+"]=attese["+bloccate+"]", bloccate, polInfo.richiesteBloccate);
					}catch(Throwable t) {
						if( (!org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.isJenkins()) || 
								(
										!PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG.equals(policyType) &&
										!PolicyGroupByActiveThreadsType.REDISSON_ATOMIC_LONG.equals(policyType) 
								)
						) {
							throw t;
						}
						else {
							// jenkins
							if( ((polInfo.richiesteBloccate)==(bloccate-1))
									||
									((polInfo.richiesteBloccate)==(bloccate-2))) {
								logRateLimiting.debug("PolicyRichiesteBloccate["+polInfo.richiesteBloccate+"]=attese["+bloccate+"] con scarto di 2 [tolleranza] ("+tipoRisorsa+"): "+t.getMessage(),t);
							}
							else {
								throw t;
							}
						}
					}
				}
				
				if(policyType!=null) {
					// Devo controllarlo somanete se non e' null. Quando si fa il reset dell'erogazione/fruizione e non passa ancora una richiesta, rimane il motore precedente
					assertEquals(policyType.toLabel(), polInfo.sincronizzazione);
				}
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
		waitForZeroActiveRequests(idPolicy, richiesteConteggiate, PolicyGroupByActiveThreadsType.LOCAL);
	}
	public static void waitForZeroActiveRequests(String idPolicy, int richiesteConteggiate, PolicyGroupByActiveThreadsType policyType) {
		Logger logRateLimiting = LoggerWrapperFactory.getLogger("testsuite.rate_limiting");
		
		int remainingChecks = Integer.valueOf(System.getProperty("rl_check_policy_conditions_retry"));
		int delay = Integer.valueOf(System.getProperty("rl_check_policy_conditions_delay"));

		if(policyType!=null && policyType.isInconsistent()) {
			// aspetto un solo tempo di delay
			org.openspcoop2.utils.Utilities.sleep(delay);
			return;
		}
		
		while(true) {
			String tmpValueRichiesteAttive = null;
			try {
				String jmxPolicyInfo = getPolicy(idPolicy);
				if (jmxPolicyInfo.equals(PolicyFields.NOPOLICYINFO)) {
					break;
				}				
				logRateLimiting.info(jmxPolicyInfo);
				Map<String, String> policyValues = parsePolicy(jmxPolicyInfo);
				tmpValueRichiesteAttive = policyValues.get(PolicyFields.RichiesteAttive);
				assertEquals("0", tmpValueRichiesteAttive);
				tmpValueRichiesteAttive=null;
				assertEquals(Integer.valueOf(richiesteConteggiate), Integer.valueOf(policyValues.get(PolicyFields.RichiesteConteggiate)));
	
				break;
			} catch (AssertionError e) {
				if(remainingChecks == 0) {
					if(tmpValueRichiesteAttive!=null && 
							(PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE.equals(policyType) || PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE.equals(policyType))) {
					try {
						int richiesteAttiveInt = Integer.valueOf(tmpValueRichiesteAttive);
						if(richiesteAttiveInt<0) {
							return; // non affidabile hazelcast local/near cache
						}
					}catch(Throwable t) {}
					}
					throw e;
				}
				else {
					remainingChecks--;
					org.openspcoop2.utils.Utilities.sleep(delay);
				}
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
			if(l.startsWith(PolicyFields.Sincronizzazione+" ")) {
				ret.put(PolicyFields.Sincronizzazione, l.substring((PolicyFields.Sincronizzazione+" ").length()));
			}
			else {
				String[] keyValue = l.split(":");
				if(keyValue.length == 2) {
					ret.put(keyValue[0].strip(), keyValue[1].strip());
				}
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
