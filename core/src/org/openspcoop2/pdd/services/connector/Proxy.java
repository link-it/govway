/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.services.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.pdd.config.DynamicClusterManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.jmx.ConfigurazionePdD;
import org.openspcoop2.pdd.core.jmx.GestoreConsegnaApplicativi;
import org.openspcoop2.pdd.core.jmx.GestoreRichieste;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.pdd.core.jmx.MonitoraggioRisorse;
import org.openspcoop2.pdd.core.jmx.StatoServiziJMXResource;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.connector.proxy.IProxyOperationService;
import org.openspcoop2.pdd.services.connector.proxy.ProxyOperation;
import org.openspcoop2.pdd.services.connector.proxy.ProxyOperationServiceFactory;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.datasource.JmxDataSource;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpServletCredential;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
 * Proxy
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Proxy extends HttpServlet {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private static final String SERVIZIO_NON_ABILITATO = "Servizio non abilitato";
	private static final String SERVIZIO_NON_DISPONIBILE = "Servizio non disponibile";
	
	private static void logError(Logger log, String msg, Throwable e) {
		log.error(msg, e);
	}
	private static void logError(Logger log, String msg) {
		log.error(msg);
	}
	private static void logDebug(Logger log, String msg) {
		log.debug(msg);
	}
	
	private static void sendError(HttpServletResponse res, Logger log, String msg, int code) {
		sendError(res, log, msg, code, msg, null);
	}
	@SuppressWarnings("unused")
	private static void sendError(HttpServletResponse res, Logger log, String msg, int code, Throwable e) {
		sendError(res, log, msg, code, msg, e);
	}
	private static void sendError(HttpServletResponse res, Logger log, String msg, int code, String logMsg) {
		sendError(res, log, msg, code, logMsg, null);
	}
	private static void sendError(HttpServletResponse res, Logger log, String msg, int code, String logMsg, Throwable e) {
		String prefix = "[Proxy] ";
		if(e!=null) {
			logError(log,prefix+logMsg, e);
		}
		else {
			logError(log,prefix+logMsg);
		}
		res.setStatus(code);
		res.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		try {
			res.getOutputStream().write(msg.getBytes());
		}catch(Exception t) {
			logError(log,"[Proxy] SendError failed: "+t.getMessage(),t);
		}
	}

	private static boolean saveContextOnlyOperation = true;
	public static boolean isSaveContextOnlyOperation() {
		return saveContextOnlyOperation;
	}
	public static void setSaveContextOnlyOperation(boolean saveContextOnlyOperation) {
		Proxy.saveContextOnlyOperation = saveContextOnlyOperation;
	}
	
	@Override public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopProxy();
		if(log==null)
			log = LoggerWrapperFactory.getLogger(Proxy.class);
		
		if( !OpenSPCoop2Startup.initialize){
			CheckStatoPdD.serializeNotInitializedResponse(res, log);
			return;
		}
		
		OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
		
		boolean proxyEnabled = false;
		if(properties!=null && properties.isProxyReadJMXResourcesEnabled() ){
			proxyEnabled = true;
		}
		if(!proxyEnabled){
			String msg = SERVIZIO_NON_ABILITATO;
			sendError(res, log, msg, 500); 
			return;
		}
				
		// verifico l'esistenza di nodi
		List<String> list = null;
		try {
			list = DynamicClusterManager.getInstance().getHostnames(log);
		}catch(Exception e) {
			String msg = SERVIZIO_NON_DISPONIBILE;
			String logMsg = msg+": "+e.getMessage();
			sendError(res, log, msg, 500, logMsg, e); 
			return;
		}
		
		boolean asyncMode = properties.isProxyReadJMXResourcesAsyncProcessByTimer();
		IProxyOperationService proxyOperationService = null;
		if(asyncMode) {
			String className = properties.getProxyReadJMXResourcesAsyncProcessByTimerServiceImplClass();
			try {
				proxyOperationService = ProxyOperationServiceFactory.newInstance(className, log);
			}catch(Exception e) {
				String msg = SERVIZIO_NON_DISPONIBILE;
				String logMsg = msg+": "+e.getMessage();
				sendError(res, log, msg, 500, logMsg, e); 
				return;
			}
		}
				
		// === Costruisco nuova url ===
		
		// schema
		String protocolSchema = properties.getProxyReadJMXResourcesSchema();
		if(asyncMode) {
			String tmp = properties.getProxyReadJMXResourcesAsyncProcessByTimerSchema();
			if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
				protocolSchema = tmp;
			}
		}
		if(protocolSchema==null || StringUtils.isEmpty(protocolSchema)) {
			protocolSchema = req.getScheme();
		}
		String protocol = (protocolSchema!=null && protocolSchema.trim().toLowerCase().startsWith("https")) ? "https://" : "http://";
		
		// hostname (solo in caso async-mode)
		String hostnameAsync = null;
		if(asyncMode) {
			hostnameAsync = properties.getProxyReadJMXResourcesAsyncProcessByTimerHostname();
		}
		
		// port
		int port = req.getLocalPort();
		if(asyncMode && properties.getProxyReadJMXResourcesAsyncProcessByTimerPort()!=null && properties.getProxyReadJMXResourcesAsyncProcessByTimerPort().intValue()>0) {
			port = properties.getProxyReadJMXResourcesAsyncProcessByTimerPort().intValue();
		}
		else if(properties.getProxyReadJMXResourcesPort()!=null && properties.getProxyReadJMXResourcesPort().intValue()>0) {
			port = properties.getProxyReadJMXResourcesPort().intValue();
		}
		
		// context
		String context = req.getContextPath();
		if(!context.endsWith("/")) {
			context = context + "/";
		}
		context = context + URLProtocolContext.Check_FUNCTION;
		
		// paramters
		Map<String, List<String>> parameters = buildParameters(req);
		
		// Https
		boolean https = properties.isProxyReadJMXResourcesHttpsEnabled();
		boolean verificaHostname = false;
		boolean autenticazioneServer = false;
		String autenticazioneServerPath = null;
		String autenticazioneServerType = null;
		String autenticazioneServerPassword = null;
		if(https) {
			verificaHostname = properties.isProxyReadJMXResourcesHttpsEnabledVerificaHostName();
			autenticazioneServer = properties.isProxyReadJMXResourcesHttpsEnabledAutenticazioneServer();
			if(autenticazioneServer) {
				autenticazioneServerPath = properties.getProxyReadJMXResourcesHttpsEnabledAutenticazioneServerTruststorePath();
				autenticazioneServerType = properties.getProxyReadJMXResourcesHttpsEnabledAutenticazioneServerTruststoreType();
				autenticazioneServerPassword = properties.getProxyReadJMXResourcesHttpsEnabledAutenticazioneServerTruststorePassword();
			}
		}
		
		// Timeout
		int readTimeout = properties.getProxyReadJMXResourcesReadTimeout();
		int connectTimeout = properties.getProxyReadJMXResourcesConnectionTimeout();
		
		// Vengono utilizzate le credenziali del servizio check che dovranno essere uguali su tutti i nodi
		String usernameCheck = properties.getCheckReadJMXResourcesUsername();
		String passwordCheck = properties.getCheckReadJMXResourcesPassword();
		
		
		// === Avvio gestione ===
		
		String resourceName = req.getParameter(CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME);
		if(resourceName!=null && !"".equals(resourceName)){
			
			// prima di procedere verifico una eventuale autenticazione
			String username = properties.getProxyReadJMXResourcesUsername();
			String password = properties.getProxyReadJMXResourcesPassword();
			if(username!=null && password!=null){
				HttpServletCredential identity = new HttpServletCredential(req, log);
				if(!username.equals(identity.getUsername())){
					String msg = "Servizio non autorizzato";
					String logMsg = msg+". Richiesta effettuata da username ["+identity.getUsername()+"] sconosciuto";
					sendError(res, log, msg, 500, logMsg); 	
					return;
				}
				if(!password.equals(identity.getPassword())){
					String msg = "Servizio non autorizzato";
					String logMsg = msg+". Richiesta effettuata da username ["+identity.getUsername()+"] (password errata)";
					sendError(res, log, msg, 500, logMsg); 	
					return;
				}
			}
			
			/*
			 *  metodi void: in questo caso rientrano le reset cache. 
			 *                Il servizio proxy invocherà ogni nodo. 
			 *                Se anche solo uno dei nodi restituisce errore, viene ritornato quell’errore (si tratta di un caso anormale).
			 *                
			 *  metodi che devono restituire una stringa: in questo caso il servizio invocherà solamente un nodo a caso 
			 *                poichè ogni nodo sono indistinguibili uno dall’altro. 
			 *                In questa casistica dovranno essere gestiti con eccezione alcuni metodi specifici (es. Numero di thread attivi o numero di connessioni attivi) 
			 *                per i quali si dovranno invocare tutti i nodi e aggregare i risultati.
			 * */
			
			String attributeName = req.getParameter(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_NAME);
			String attributeValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_VALUE);
			String attributeBooleanValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_BOOLEAN_VALUE);
			String methodName = req.getParameter(CostantiPdD.CHECK_STATO_PDD_METHOD_NAME);
			String parameterValues = formatParameters(req);
			logDebug(log,"==============");
			if(attributeName!=null){
				logDebug(log,"resourceName["+resourceName+"] attributeName["+attributeName+"] attributeValue["+attributeValue+"] attributeBooleanValue["+attributeBooleanValue+"] ...");
			}
			else {
				logDebug(log,"resourceName["+resourceName+"] methodName["+methodName+"]"+parameterValues+" ...");
			}
			boolean invokeAllNodes = false;
			boolean aggregate = false;
			
			String resourcePrefix = "[resource: "+resourceName+"] ";
			String tipoOperazioneAsync = null;
			int asyncCheckInterval = properties.getProxyReadJMXResourcesAsyncProcessByTimerCheckInterval();
			if(asyncMode) {
				if(attributeName!=null && (attributeValue!=null || attributeBooleanValue!=null)) {
					tipoOperazioneAsync = resourcePrefix+"setAttribute '"+attributeName+"'";		
				}
				else {
					tipoOperazioneAsync = resourcePrefix+methodName;
				}
			}
			
			if(attributeName!=null && (attributeValue!=null || attributeBooleanValue!=null)) {
				invokeAllNodes = true; // setAttribute
			}
			else if(methodName!=null){
				if(JMXUtils.CACHE_METHOD_NAME_RESET.equals(methodName) || 
						JMXUtils.CACHE_METHOD_NAME_REMOVE_OBJECT.equals(methodName)) {
					invokeAllNodes = true;
				}
				else if(CostantiPdD.JMX_GESTORE_RICHIESTE.equals(resourceName)
						&&
						(GestoreRichieste.CACHE_METHOD_NAME_REMOVE_DATI_CONTROLLO_TRAFFICO_GLOBALE.equals(methodName)
								||
								GestoreRichieste.CACHE_METHOD_NAME_REMOVE_DATI_CONTROLLO_TRAFFICO_API.equals(methodName))
					) {
					invokeAllNodes = true;
				}
				else if(CostantiPdD.JMX_CONFIGURAZIONE_PDD.equals(resourceName)
						&&
						(
								methodName.startsWith(ConfigurazionePdD.RIPULISCI_RIFERIMENTI_CACHE_PREFIX)
								||
								ConfigurazionePdD.ABILITA_PORTA_DELEGATA.equals(methodName)
								||
								ConfigurazionePdD.DISABILITA_PORTA_DELEGATA.equals(methodName)
								||
								ConfigurazionePdD.ABILITA_PORTA_APPLICATIVA.equals(methodName)
								||
								ConfigurazionePdD.DISABILITA_PORTA_APPLICATIVA.equals(methodName)
								||
								ConfigurazionePdD.ABILITA_CONNETTORE_MULTIPLO.equals(methodName)
								||
								ConfigurazionePdD.DISABILITA_CONNETTORE_MULTIPLO.equals(methodName)
								||
								ConfigurazionePdD.ABILITA_SCHEDULING_CONNETTORE_MULTIPLO.equals(methodName)
								||
								ConfigurazionePdD.DISABILITA_SCHEDULING_CONNETTORE_MULTIPLO.equals(methodName)
								||
								ConfigurazionePdD.ABILITA_SCHEDULING_CONNETTORE_MULTIPLO_RUNTIME.equals(methodName)
								||
								ConfigurazionePdD.DISABILITA_SCHEDULING_CONNETTORE_MULTIPLO_RUNTIME.equals(methodName)
						)
						) {
					invokeAllNodes = true;
				}
				else if(CostantiPdD.JMX_STATO_SERVIZI_PDD.equals(resourceName)
						&&
						(
								StatoServiziJMXResource.ABILITA_COMPONENTE_PD.equals(methodName)
								||
								StatoServiziJMXResource.DISABILITA_COMPONENTE_PD.equals(methodName)
								||
								StatoServiziJMXResource.ABILITA_COMPONENTE_PA.equals(methodName)
								||
								StatoServiziJMXResource.DISABILITA_COMPONENTE_PA.equals(methodName)
								||
								StatoServiziJMXResource.ABILITA_COMPONENTE_IM.equals(methodName)
								||
								StatoServiziJMXResource.DISABILITA_COMPONENTE_IM.equals(methodName)
						)
						) {
					invokeAllNodes = true;
				}
				else if(CostantiPdD.JMX_LOAD_BALANCER.equals(resourceName)
						&&
						(
								GestoreConsegnaApplicativi.UPDATE_CONNETTORI_PRIORITARI.equals(methodName)
								||
								GestoreConsegnaApplicativi.RESET_CONNETTORI_PRIORITARI.equals(methodName)
						)
						) {
					invokeAllNodes = true;
				}
				else if(
						(
								CostantiPdD.JMX_MONITORAGGIO_RISORSE.equals(resourceName) 
								&&
								(
									MonitoraggioRisorse.CONNESSIONI_ALLOCATE_DB_MANAGER.equals(methodName)
									||
									MonitoraggioRisorse.CONNESSIONI_ALLOCATE_QUEUE_MANAGER.equals(methodName)
									||
									MonitoraggioRisorse.TRANSAZIONI_ATTIVE_ID.equals(methodName)
									||
									MonitoraggioRisorse.TRANSAZIONI_ATTIVE_ID_PROTOCOLLO.equals(methodName)
									||
									MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PD.equals(methodName)
									||
									MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PA.equals(methodName)
								)	
						)
						
						||
						
						(
								Costanti.JMX_NAME_DATASOURCE_PDD.equals(resourceName)
								&&
								JmxDataSource.CONNESSIONI_ALLOCATE.equals(methodName)
						)
						
						||
						
						(
								CostantiPdD.JMX_LOAD_BALANCER.equals(resourceName)
								&&
								GestoreConsegnaApplicativi.THREAD_POOL_STATUS.equals(methodName)
						)
					){
					invokeAllNodes = true;
					aggregate = true;
					if(asyncMode) {
						String msg = SERVIZIO_NON_DISPONIBILE;
						String logMsg = msg+". L'operazione richiesta ("+resourcePrefix+methodName+") richiede un'aggregazione non attuabile in modalità asincrona";
						sendError(res, log, msg, 500, logMsg); 	
						return;
					}
				}
						
			}
			
			if(invokeAllNodes) {
				
				// Se anche solo uno dei nodi restituisce errore, viene ritornato quell’errore (si tratta di un caso anormale).
				/**String urlHttpResponseFailed = null;*/
				HttpResponse httpResponseFailed = null;
				/**String urlHttpResponseOk = null;*/
				HttpResponse httpResponseOk = null;
				String urlT = null;
				Throwable t = null;
				ResultAggregate resultAggregate = null;
				
				logDebug(log,"Invoke all node ...");
								
				List<String> lUsed = list;
				if(asyncMode) {
					// effettuo solo una registrazione
					lUsed = new ArrayList<>();
					lUsed.add("localhost"); // verra poi sostituito con hostnameAsync
				}
				
				for (String hostname : lUsed) {
					String url = null;
					try {
						/**System.out.println("DEBUG");
						if("Erogatore".equals(hostname)) {
							port = 8180;
						}
						else {
							port = 8080;
						}
						System.out.println("DEBUG");*/
						
						String hostnameUsed = hostname;
						if(asyncMode && hostnameAsync!=null && StringUtils.isNotEmpty(hostnameAsync)) {
							hostnameUsed = hostnameAsync;
						}
						
						int portUsed = port;
						Integer portHostname = properties.getProxyReadJMXResourcesPort(hostname);
						if(portHostname!=null && portHostname.intValue()>0) {
							portUsed = portHostname.intValue();
						}
						
						boolean addContext = !asyncMode || !saveContextOnlyOperation;
						url = buildUrl(log, addContext, protocol, hostnameUsed, portUsed, context, parameters);
						
						if(aggregate && asyncMode) {
							// c'e' un controllo prima e quindi questa eccezione non dovrebbe avvenire
							throw new CoreException("L'operazione richiesta ("+url+") richiede un'aggregazione non attuabile in modalità asincrona");
						}
						
						HttpResponse httpResponse = null;
						if(asyncMode) {
							/**System.out.println("SAVE ALL METHOD BY URL '"+url+"'");*/
							httpResponse = saveOperation(proxyOperationService, url, tipoOperazioneAsync, log, asyncCheckInterval);
						}
						else {
							/**System.out.println("INVOKE ALL METHOD BY URL '"+url+"'");*/
							httpResponse = invokeHttp(url, 
									readTimeout, connectTimeout, 
									usernameCheck, passwordCheck,
									https, verificaHostname, autenticazioneServer,
									autenticazioneServerPath, autenticazioneServerType,  autenticazioneServerPassword);
						}
						
						String sResponse = null;
						if(httpResponse.getContent()!=null) {
							sResponse = new String(httpResponse.getContent());
						}
						boolean error = sResponse!=null && sResponse.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA);
						
						if(httpResponse.getResultHTTPOperation()==200 && !error) {
							if(httpResponseOk==null) {
								httpResponseOk = httpResponse;
								/**urlHttpResponseOk = url;*/
							}
							if(aggregate) {
								if(resultAggregate==null) {
									resultAggregate = new ResultAggregate(resourceName, methodName);
								}
								resultAggregate.addResponse(sResponse, hostname);
							}
						}
						else {
							if(httpResponseFailed==null) {
								httpResponseFailed = httpResponse;
								/**urlHttpResponseFailed = url;*/
							}
						}
						
						logDebug(log,"Invoked '"+hostname+"' ("+httpResponse.getResultHTTPOperation()+")");
						
					}catch(Exception e) {
						String msg = "("+hostname+") Servizio non disponibile";
						t = new Exception(msg, e);
						urlT = url;
					}
				}
				
				if(t!=null) {
					String msg = t.getMessage();
					String logMsg = "error occurs: "+t.getMessage();
					sendError(res, log, msg, 500, logMsg, t); 	
				}
				else if(httpResponseFailed!=null) {
					logDebug(log,"Invoke all node complete 'ERROR'");
					writeResponse(httpResponseFailed, res, log);
				}
				else if(resultAggregate!=null) {
					logDebug(log,"Invoke all node complete 'Aggregate OK'");
					writeResponse(resultAggregate.getHttpResponse(), res, log);
				}
				else if(httpResponseOk!=null) {
					logDebug(log,"Invoke all node complete 'OK'");
					writeResponse(httpResponseOk, res, log);
				}
				else {
					String msg = SERVIZIO_NON_DISPONIBILE;
					String logMsg = "'CasoNonPrevisto' (url: "+urlT+") "+msg;
					sendError(res, log, msg, 500, logMsg); 	
				}

			}
			else {
				
				logDebug(log,"Invoke single node '"+list.get(0)+"' ...");
				
				// Viene invocato un nodo a caso (il primo essendo quello piu' vecchio)
				String url = null;
				try {
					
					String hostname = list.get(0);
					String hostnameUsed = hostname;
					if(asyncMode && hostnameAsync!=null && StringUtils.isNotEmpty(hostnameAsync)) {
						hostnameUsed = hostnameAsync;
					}
					
					int portUsed = port;
					Integer portHostname = properties.getProxyReadJMXResourcesPort(hostname);
					if(portHostname!=null && portHostname.intValue()>0) {
						portUsed = portHostname.intValue();
					}
					
					url = buildUrl(log, true, protocol, hostnameUsed, portUsed, context, parameters);
					/**System.out.println("INVOKE SINGLE METHOD BY URL '"+url+"'");*/
					HttpResponse httpResponse = invokeHttp(url, 
							readTimeout, connectTimeout, 
							usernameCheck, passwordCheck,
							https, verificaHostname, autenticazioneServer,
							autenticazioneServerPath, autenticazioneServerType,  autenticazioneServerPassword);
					writeResponse(httpResponse, res, log);
					
				}catch(Exception e) {
					String msg = SERVIZIO_NON_DISPONIBILE;
					String logMsg = msg+" (url: "+url+"): "+e.getMessage();
					sendError(res, log, msg, 500, logMsg, e); 	
				}
			}
			
		}
		else {
			
			logDebug(log,"Invoke single node CHECK '"+list.get(0)+"' ...");
			
			// Servizio check del gateway
			// Viene invocato un nodo a caso (il primo essendo quello piu' vecchio)
			String url = null;
			try {
				
				String hostname = list.get(0);
				String hostnameUsed = hostname;
				if(asyncMode && hostnameAsync!=null && StringUtils.isNotEmpty(hostnameAsync)) {
					hostnameUsed = hostnameAsync;
				}
				
				int portUsed = port;
				Integer portHostname = properties.getProxyReadJMXResourcesPort(hostname);
				if(portHostname!=null && portHostname.intValue()>0) {
					portUsed = portHostname.intValue();
				}
				
				url = buildUrl(log, true, protocol, hostnameUsed, portUsed, context, parameters);
				/**System.out.println("INVOKE SINGLE ATTRIBUTE BY URL '"+url+"'");*/
				HttpResponse httpResponse = invokeHttp(url, 
								readTimeout, connectTimeout, 
								usernameCheck, passwordCheck,
								https, verificaHostname, autenticazioneServer,
								autenticazioneServerPath, autenticazioneServerType,  autenticazioneServerPassword);
				writeResponse(httpResponse, res, log);
			}catch(Exception e) {
				String msg = SERVIZIO_NON_DISPONIBILE;
				String logMsg = msg+" (url: "+url+"): "+e.getMessage();
				sendError(res, log, msg, 500, logMsg, e); 	
			}
		}
		

	}
	
	public static HttpResponse invokeHttp(String url, 
			int readTimeout, int connectTimeout, 
			String usernameCheck, String passwordCheck,
			boolean https, boolean verificaHostname, boolean autenticazioneServer,
			String autenticazioneServerPath, String autenticazioneServerType, String autenticazioneServerPassword) throws UtilsException {
		HttpResponse response = null;
		if(https) {
			HttpRequest httpRequest = new HttpRequest();
			httpRequest.setUrl(url);
			httpRequest.setConnectTimeout(connectTimeout);
			httpRequest.setReadTimeout(readTimeout);
			httpRequest.setUsername(usernameCheck);
			httpRequest.setPassword(passwordCheck);
			httpRequest.setMethod(HttpRequestMethod.GET);
			httpRequest.setHostnameVerifier(verificaHostname);
			if(autenticazioneServer) {
				httpRequest.setTrustStorePath(autenticazioneServerPath);
				httpRequest.setTrustStoreType(autenticazioneServerType);
				httpRequest.setTrustStorePassword(autenticazioneServerPassword);
			}
			else {
				httpRequest.setTrustAllCerts(true);
			}
			response = HttpUtilities.httpInvoke(httpRequest);
		}
		else {
			response = HttpUtilities.getHTTPResponse(url,
					readTimeout, connectTimeout,
					usernameCheck, passwordCheck);
		}
		return response;
	}
	private HttpResponse saveOperation(IProxyOperationService proxyOperationService, String url, String description, Logger log, int asyncCheckInterval) {
		HttpResponse response = new HttpResponse();
		try {
			
			ProxyOperation proxyOperation = new ProxyOperation();
			proxyOperation.setCommand(url);
			proxyOperation.setDescription(description);
			proxyOperation.setRegistrationTime(DateManager.getDate());
			proxyOperationService.save(proxyOperation);
			
			response.setResultHTTPOperation(200);
			response.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
			response.setContent((JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO_PREFIX+JMXUtils.MSG_OPERAZIONE_REGISTRATA_SUCCESSO.replace(JMXUtils.MSG_OPERAZIONE_REGISTRATA_SUCCESSO_TEMPLATE_SECONDI, asyncCheckInterval+"")).getBytes());
		}catch(Exception t) {
			response.setResultHTTPOperation(500);
			response.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
			logError(log,"[Proxy] saveOperation failed: "+t.getMessage(),t);
			response.setContent((JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+t.getMessage()).getBytes());
		}
		return response;
	}

	private void writeResponse(HttpResponse httpResponse, HttpServletResponse res, Logger log) {
		try {
			res.setStatus(httpResponse.getResultHTTPOperation());
			if(httpResponse.getContentType()!=null) {
				res.setContentType(httpResponse.getContentType());
			}
			if(httpResponse.getContent()!=null) {
				res.getOutputStream().write(httpResponse.getContent());
			}
		}catch(Exception t) {
			logError(log,"[Proxy] WriteResponse failed: "+t.getMessage(),t);
		}
	}
	
	private String buildUrl(Logger log, boolean addContext, String protocol, String hostname, int port, String context, Map<String, List<String>> parameters ) {
		StringBuilder sb = new StringBuilder();
		if(addContext) {
			sb.append(protocol);
			sb.append(hostname);
			sb.append(":");
			sb.append(port);
		}
		sb.append(context);
		return TransportUtils.buildUrlWithParameters(parameters, sb.toString(), log);
	}
	
	private Map<String, List<String>> buildParameters(HttpServletRequest req) {
		Map<String, List<String>> parameters = new HashMap<>();	       
		java.util.Enumeration<?> en = req.getParameterNames();
		while(en.hasMoreElements()){
			String nomeProperty = (String)en.nextElement();
			String [] s = req.getParameterValues(nomeProperty);
			List<String> values = new ArrayList<>();
			if(s!=null && s.length>0) {
				for (int i = 0; i < s.length; i++) {
					String value = s[i];
					values.add(value);
				}
			}
			else {
				values.add(req.getParameter(nomeProperty));
			}
			parameters.put(nomeProperty,values);
		}
		return parameters;
	}
	
	private static String formatParameters(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder("");
		
		String paramValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_PARAM_VALUE);
		if(paramValue!=null) {
			sb.append(" ").append("paramValue[").append(paramValue).append("]");
		}
		paramValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_PARAM_INT_VALUE);
		if(paramValue!=null) {
			sb.append(" ").append("paramIntValue[").append(paramValue).append("]");
		}
		paramValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE);
		if(paramValue!=null) {
			sb.append(" ").append("paramLongValue[").append(paramValue).append("]");
		}
		paramValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_PARAM_BOOLEAN_VALUE);
		if(paramValue!=null) {
			sb.append(" ").append("paramBooleanValue[").append(paramValue).append("]");
		}
		
		paramValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_PARAM_VALUE_2);
		if(paramValue!=null) {
			sb.append(" ").append("paramValue2[").append(paramValue).append("]");
		}
		paramValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_PARAM_INT_VALUE_2);
		if(paramValue!=null) {
			sb.append(" ").append("paramIntValue2[").append(paramValue).append("]");
		}
		paramValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE_2);
		if(paramValue!=null) {
			sb.append(" ").append("paramLongValue2[").append(paramValue).append("]");
		}
		paramValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_PARAM_BOOLEAN_VALUE_2);
		if(paramValue!=null) {
			sb.append(" ").append("paramBooleanValue2[").append(paramValue).append("]");
		}
		
		paramValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_PARAM_VALUE_3);
		if(paramValue!=null) {
			sb.append(" ").append("paramValue3[").append(paramValue).append("]");
		}
		paramValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_PARAM_INT_VALUE_3);
		if(paramValue!=null) {
			sb.append(" ").append("paramIntValue3[").append(paramValue).append("]");
		}
		paramValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE_3);
		if(paramValue!=null) {
			sb.append(" ").append("paramLongValue3[").append(paramValue).append("]");
		}
		paramValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_PARAM_BOOLEAN_VALUE_3);
		if(paramValue!=null) {
			sb.append(" ").append("paramBooleanValue3[").append(paramValue).append("]");
		}
		
		return sb.toString();
	}
}

class ResultAggregate {
	
	private String resourceName;
	private String methodName;
	private List<String> list1 = new ArrayList<>();
	private List<String> list2 = new ArrayList<>();
	private List<String> list3 = new ArrayList<>();
	private List<String> list4 = new ArrayList<>();
	private List<String> list5 = new ArrayList<>();
	private List<String> list6 = new ArrayList<>();
	private List<String> list7 = new ArrayList<>();
	private List<String> list8 = new ArrayList<>();
	
	ResultAggregate(String resourceName, String methodName){
		this.resourceName = resourceName;
		this.methodName = methodName;
	}
	
	public void addResponse(String sResponse, String hostname) throws IOException {
		if(sResponse==null) {
			return;
		}
		try(StringReader sReader = new StringReader(sResponse);
			BufferedReader bReader = new BufferedReader(sReader)) {
			String line = bReader.readLine();
			List<String> listUse = this.list1;
			while (line != null) {
				if(MonitoraggioRisorse.MSG_NESSUNA_CONNESSIONE_ALLOCATA.equals(line) ||
						MonitoraggioRisorse.MSG_NESSUNA_TRANSAZIONE_ATTIVA.equals(line) ||
						JmxDataSource.MSG_NESSUNA_CONNESSIONE_ALLOCATA.equals(line)) {
					break;
				}
				try {
					if(line.contains(MonitoraggioRisorse.MSG_CONNESSIONI_ALLOCATE) ||
							line.contains(MonitoraggioRisorse.MSG_CONNESSIONI_HTTP_ALLOCATE) ||
							line.contains(MonitoraggioRisorse.MSG_TRANSAZIONI_ATTIVE) ||
							line.contains(MonitoraggioRisorse.MSG_TRANSAZIONI_ATTIVE_ID_PROTOCOLLO) ||
							line.contains(JmxDataSource.MSG_CONNESSIONI_ALLOCATE)) {
						continue;
					}
					if(line.contains(MonitoraggioRisorse.MSG_CONNESSIONI_ALLOCATE_TRANSAZIONI)) {
						listUse = this.list2;
						continue;
					}
					if(line.contains(MonitoraggioRisorse.MSG_CONNESSIONI_ALLOCATE_STATISTICHE)) {
						listUse = this.list3;
						continue;
					}
					if(line.contains(MonitoraggioRisorse.MSG_CONNESSIONI_ALLOCATE_CONSEGNE_PRESE_IN_CARICO_SMISTATORE)) {
						listUse = this.list4;
						continue;
					}
					if(line.contains(MonitoraggioRisorse.MSG_CONNESSIONI_ALLOCATE_CONSEGNE_PRESE_IN_CARICO_RUNTIME)) {
						listUse = this.list5;
						continue;
					}
					if(line.contains(MonitoraggioRisorse.MSG_CONNESSIONI_ALLOCATE_CONSEGNE_PRESE_IN_CARICO_TRANSAZIONI)) {
						listUse = this.list6;
						continue;
					}
					if(line.contains(MonitoraggioRisorse.MSG_CONNESSIONI_ALLOCATE_CONSEGNE_MESSAGE_BOX_RUNTIME)) {
						listUse = this.list7;
						continue;
					}
					if(line.contains(MonitoraggioRisorse.MSG_CONNESSIONI_ALLOCATE_CONSEGNE_MESSAGE_BOX_TRANSAZIONI)) {
						listUse = this.list8;
						continue;
					}
					
					listUse.add(line+" ["+hostname+"]");
				}finally {
					// read next line
					line = bReader.readLine();
				}
			}
		}
	}
	
	public HttpResponse getHttpResponse() {
		HttpResponse httpResponse = new HttpResponse();
		httpResponse.setResultHTTPOperation(200);
		
		String content = null;
		if(CostantiPdD.JMX_MONITORAGGIO_RISORSE.equals(this.resourceName)) {
			if(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_DB_MANAGER.equals(this.methodName) ||
					MonitoraggioRisorse.CONNESSIONI_ALLOCATE_QUEUE_MANAGER.equals(this.methodName)) {
				String[] risorse = null;
				String[] risorseTransaction = null;
				String[] risorseStatistiche = null;
				String[] risorseConsegnePreseInCaricoSmistatore = null;
				String[] risorseConsegnePreseInCaricoRuntime = null;
				String[] risorseConsegnePreseInCaricoTransazioni = null;
				String[] risorseConsegneMessageBoxRuntime = null;
				String[] risorseConsegneMessageBoxTransazioni = null;
				if(this.list1!=null && !this.list1.isEmpty()) {
					risorse = this.list1.toArray(new String[1]);
				}
				if(this.list2!=null && !this.list2.isEmpty()) {
					risorseTransaction = this.list2.toArray(new String[1]);
				}
				if(this.list3!=null && !this.list3.isEmpty()) {
					risorseStatistiche = this.list3.toArray(new String[1]);
				}
				if(this.list4!=null && !this.list4.isEmpty()) {
					risorseConsegnePreseInCaricoSmistatore = this.list4.toArray(new String[1]);
				}
				if(this.list5!=null && !this.list5.isEmpty()) {
					risorseConsegnePreseInCaricoRuntime = this.list5.toArray(new String[1]);
				}
				if(this.list6!=null && !this.list6.isEmpty()) {
					risorseConsegnePreseInCaricoTransazioni = this.list6.toArray(new String[1]);
				}
				if(this.list7!=null && !this.list7.isEmpty()) {
					risorseConsegneMessageBoxRuntime = this.list7.toArray(new String[1]);
				}
				if(this.list8!=null && !this.list8.isEmpty()) {
					risorseConsegneMessageBoxTransazioni = this.list8.toArray(new String[1]);
				}
				if(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_DB_MANAGER.equals(this.methodName)) {
					content =  MonitoraggioRisorse.getResultUsedDBConnections(risorse, risorseTransaction, risorseStatistiche,
							risorseConsegnePreseInCaricoSmistatore, risorseConsegnePreseInCaricoRuntime, risorseConsegnePreseInCaricoTransazioni,
							risorseConsegneMessageBoxRuntime, risorseConsegneMessageBoxTransazioni);
				}
				else if(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_QUEUE_MANAGER.equals(this.methodName)) {
					content =  MonitoraggioRisorse.getResultUsedQueueConnections(risorse);
				}
			}
			else if(MonitoraggioRisorse.TRANSAZIONI_ATTIVE_ID.equals(this.methodName)) {
				content =  MonitoraggioRisorse.getResultTransazioniAttiveId(this.list1);
			}
			else if(MonitoraggioRisorse.TRANSAZIONI_ATTIVE_ID_PROTOCOLLO.equals(this.methodName)) {
				content =  MonitoraggioRisorse.getResultTransazioniAttiveIdProtocollo(this.list1);
			}
			else if(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PD.equals(this.methodName)) {
				content =  MonitoraggioRisorse.getResultActiveConnections(this.list1);
			}
			else if(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PA.equals(this.methodName)) {
				content =  MonitoraggioRisorse.getResultActiveConnections(this.list1);
			}
		}
		else if(Costanti.JMX_NAME_DATASOURCE_PDD.equals(this.resourceName)) {
			String[] risorse = null;
			if(this.list1!=null && !this.list1.isEmpty()) {
				risorse = this.list1.toArray(new String[1]);
			}
			content = JmxDataSource.getResultUsedDBConnections(risorse);
		}
		else if(CostantiPdD.JMX_LOAD_BALANCER.equals(this.resourceName) &&
			this.list1!=null && !this.list1.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String l : this.list1) {
				if(sb.length()>0) {
					sb.append("\n");
				}
				sb.append(l);
			}
			content = sb.toString();
		}
		
		if(content!=null) {
			httpResponse.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
			httpResponse.setContent(content.getBytes());
		}
		return httpResponse;
	}

}
