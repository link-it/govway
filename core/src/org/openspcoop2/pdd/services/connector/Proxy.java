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



package org.openspcoop2.pdd.services.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.pdd.config.DynamicClusterManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.jmx.ConfigurazionePdD;
import org.openspcoop2.pdd.core.jmx.GestoreConsegnaApplicativi;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.pdd.core.jmx.MonitoraggioRisorse;
import org.openspcoop2.pdd.core.jmx.StatoServiziJMXResource;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.datasource.JmxDataSource;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
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

	

	@Override public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopProxy();
		if(log==null)
			log = LoggerWrapperFactory.getLogger(Proxy.class);
		
		if( OpenSPCoop2Startup.initialize == false){
			CheckStatoPdD.serializeNotInitializedResponse(res, log);
			return;
		}
		
		OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
		
		boolean proxyEnabled = false;
		if(properties!=null && properties.isProxyReadJMXResourcesEnabled() ){
			proxyEnabled = true;
		}
		if(proxyEnabled==false){
			String msg = "Servizio non abilitato";
			log.error("[Proxy] "+msg);
			res.setStatus(500);
			res.getOutputStream().write(msg.getBytes());
			return;
		}
				
		// verifico l'esistenza di nodi
		List<String> list = null;
		try {
			list = DynamicClusterManager.getInstance().getHostnames(log);
		}catch(Throwable e) {
			String msg = "Servizio non disponibile";
			log.error("[Proxy] "+msg+": "+e.getMessage(),e);
			res.setStatus(500);
			res.getOutputStream().write(msg.getBytes());
			return;
		}
		
		// Costruisco nuova url
		String protocol = req.getProtocol().trim().toLowerCase().startsWith("htts") ? "https://" : "http://";
		int port = req.getLocalPort();
		String context = req.getContextPath();
		if(!context.endsWith("/")) {
			context = context + "/";
		}
		context = context + URLProtocolContext.Check_FUNCTION;
		Map<String, List<String>> parameters = buildParameters(req);
		int readTimeout = properties.getProxyReadJMXResourcesReadTimeout();
		int connectTimeout = properties.getProxyReadJMXResourcesConnectionTimeout();
		// Vengono utilizzate le credenziali del servizio check che dovranno essere uguali su tutti i nodi
		String usernameCheck = properties.getCheckReadJMXResourcesUsername();
		String passwordCheck = properties.getCheckReadJMXResourcesPassword();
		
		String resourceName = req.getParameter(CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME);
		if(resourceName!=null && !"".equals(resourceName)){
			
			// prima di procedere verifico una eventuale autenticazione
			String username = properties.getProxyReadJMXResourcesUsername();
			String password = properties.getProxyReadJMXResourcesPassword();
			if(username!=null && password!=null){
				HttpServletCredential identity = new HttpServletCredential(req, log);
				if(username.equals(identity.getUsername())==false){
					String msg = "Servizio non autorizzato";
					log.error("[Proxy] "+msg+". Richiesta effettuata da username ["+identity.getUsername()+"] sconosciuto");
					res.setStatus(500);
					res.getOutputStream().write(msg.getBytes());	
					return;
				}
				if(password.equals(identity.getPassword())==false){
					String msg = "Servizio non autorizzato";
					log.error("[Proxy] "+msg+". Richiesta effettuata da username ["+identity.getUsername()+"] (password errata)");
					res.setStatus(500);
					res.getOutputStream().write(msg.getBytes());
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
			String paramValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_PARAM_VALUE);
			log.debug("==============");
			if(attributeName!=null){
				log.debug("resourceName["+resourceName+"] attributeName["+attributeName+"] attributeValue["+attributeValue+"] attributeBooleanValue["+attributeBooleanValue+"] ...");
			}
			else {
				log.debug("resourceName["+resourceName+"] methodName["+methodName+"] paramValue["+paramValue+"] ...");
			}
			boolean invokeAllNodes = false;
			boolean aggregate = false;
			if(attributeName!=null && (attributeValue!=null || attributeBooleanValue!=null)) {
				invokeAllNodes = true; // setAttribute
			}
			else if(methodName!=null){
				if(JMXUtils.CACHE_METHOD_NAME_RESET.equals(methodName) || 
						JMXUtils.CACHE_METHOD_NAME_REMOVE_OBJECT.equals(methodName)) {
					invokeAllNodes = true;
				}
				else if(CostantiPdD.JMX_CONFIGURAZIONE_PDD.equals(resourceName)
						&&
						(
								ConfigurazionePdD.ABILITA_PORTA_DELEGATA.equals(methodName)
								||
								ConfigurazionePdD.DISABILITA_PORTA_DELEGATA.equals(methodName)
								||
								ConfigurazionePdD.ABILITA_PORTA_APPLICATIVA.equals(methodName)
								||
								ConfigurazionePdD.DISABILITA_PORTA_APPLICATIVA.equals(methodName)
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
				else if(CostantiPdD.JMX_MONITORAGGIO_RISORSE.equals(resourceName) 
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
						) {
					invokeAllNodes = true;
					aggregate = true;
				}
				else if(Costanti.JMX_NAME_DATASOURCE_PDD.equals(resourceName)
						&&
						JmxDataSource.CONNESSIONI_ALLOCATE.equals(methodName)){
					invokeAllNodes = true;
					aggregate = true;
				} 
				else if(CostantiPdD.JMX_LOAD_BALANCER.equals(resourceName)
						&&
						GestoreConsegnaApplicativi.THREAD_POOL_STATUS.equals(methodName)) {
					invokeAllNodes = true;
					aggregate = true;
				}
						
			}
			
			if(invokeAllNodes) {
				
				// Se anche solo uno dei nodi restituisce errore, viene ritornato quell’errore (si tratta di un caso anormale).
				@SuppressWarnings("unused")
				String url_httpResponseFailed = null;
				HttpResponse httpResponseFailed = null;
				@SuppressWarnings("unused")
				String url_httpResponseOk = null;
				HttpResponse httpResponseOk = null;
				String url_t = null;
				Throwable t = null;
				ResultAggregate resultAggregate = null;
				
				log.debug("Invoke all node ...");
				
				for (String hostname : list) {
					String url = null;
					try {
//						System.out.println("DEBUG");
//						if("Erogatore".equals(hostname)) {
//							port = 8180;
//						}
//						else {
//							port = 8080;
//						}
//						System.out.println("DEBUG");
						
						url = buildUrl(log, protocol, hostname, port, context, parameters);
						HttpResponse httpResponse = HttpUtilities.getHTTPResponse(url, readTimeout, connectTimeout, usernameCheck, passwordCheck);
						String sResponse = null;
						if(httpResponse.getContent()!=null) {
							sResponse = new String(httpResponse.getContent());
						}
						boolean error = sResponse!=null && sResponse.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA);
						
						if(httpResponse.getResultHTTPOperation()==200 && !error) {
							if(httpResponseOk==null) {
								httpResponseOk = httpResponse;
								url_httpResponseOk = url;
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
								url_httpResponseFailed = url;
							}
						}
						
						log.debug("Invoked '"+hostname+"' ("+httpResponse.getResultHTTPOperation()+")");
						
					}catch(Throwable e) {
						String msg = "("+hostname+") Servizio non disponibile";
						t = new Exception(msg, e);
						url_t = url;
					}
				}
				
				if(t!=null) {
					log.error("[Proxy] error occurs: "+t.getMessage(),t);
					res.setStatus(500);
					res.getOutputStream().write(t.getMessage().getBytes());
				}
				else if(httpResponseFailed!=null) {
					log.debug("Invoke all node complete 'ERROR'");
					writeResponse(httpResponseFailed, res);
				}
				else if(resultAggregate!=null) {
					log.debug("Invoke all node complete 'Aggregate OK'");
					writeResponse(resultAggregate.getHttpResponse(), res);
				}
				else if(httpResponseOk!=null) {
					log.debug("Invoke all node complete 'OK'");
					writeResponse(httpResponseOk, res);
				}
				else {
					String msg = "Servizio non disponibile";
					log.error("[Proxy] 'CasoNonPrevisto' (url: "+url_t+") "+msg);
					res.setStatus(500);
					res.getOutputStream().write(msg.getBytes());
				}
				return;
			}
			else {
				
				log.debug("Invoke single node '"+list.get(0)+"' ...");
				
				// Viene invocato un nodo a caso (il primo essendo quello piu' vecchio)
				String url = null;
				try {
					url = buildUrl(log, protocol, list.get(0), port, context, parameters);
					HttpResponse httpResponse = HttpUtilities.getHTTPResponse(url, readTimeout, connectTimeout, usernameCheck, passwordCheck);
					writeResponse(httpResponse, res);
				}catch(Throwable e) {
					String msg = "Servizio non disponibile";
					log.error("[Proxy] "+msg+" (url: "+url+"): "+e.getMessage(),e);
					res.setStatus(500);
					res.getOutputStream().write(msg.getBytes());
					return;
				}
			}
			
		}
		else {
			
			log.debug("Invoke single node CHECK '"+list.get(0)+"' ...");
			
			// Servizio check del gateway
			// Viene invocato un nodo a caso (il primo essendo quello piu' vecchio)
			String url = null;
			try {
				url = buildUrl(log, protocol, list.get(0), port, context, parameters);
				HttpResponse httpResponse = HttpUtilities.getHTTPResponse(url, readTimeout, connectTimeout, usernameCheck, passwordCheck);
				writeResponse(httpResponse, res);
			}catch(Throwable e) {
				String msg = "Servizio non disponibile";
				log.error("[Proxy] "+msg+" (url: "+url+"): "+e.getMessage(),e);
				res.setStatus(500);
				res.getOutputStream().write(msg.getBytes());
				return;
			}
		}
		
		return;

	}

	private void writeResponse(HttpResponse httpResponse, HttpServletResponse res) throws IOException {
		res.setStatus(httpResponse.getResultHTTPOperation());
		if(httpResponse.getContentType()!=null) {
			res.setContentType(httpResponse.getContentType());
		}
		if(httpResponse.getContent()!=null) {
			res.getOutputStream().write(httpResponse.getContent());
		}
	}
	
	private String buildUrl(Logger log, String protocol, String hostname, int port, String context, Map<String, List<String>> parameters ) {
		StringBuilder sb = new StringBuilder();
		sb.append(protocol);
		sb.append(hostname);
		sb.append(":");
		sb.append(port);
		sb.append(context);
		return TransportUtils.buildUrlWithParameters(parameters, sb.toString(), log);
	}
	
	private Map<String, List<String>> buildParameters(HttpServletRequest req) {
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();	       
		java.util.Enumeration<?> en = req.getParameterNames();
		while(en.hasMoreElements()){
			String nomeProperty = (String)en.nextElement();
			String [] s = req.getParameterValues(nomeProperty);
			List<String> values = new ArrayList<String>();
			if(s!=null && s.length>0) {
				for (int i = 0; i < s.length; i++) {
					String value = s[i];
					values.add(value);
					//logCore.info("Parameter ["+nomeProperty+"] valore-"+i+" ["+value+"]");
				}
			}
			else {
				//logCore.info("Parameter ["+nomeProperty+"] valore ["+req.getParameter(nomeProperty)+"]");
				values.add(req.getParameter(nomeProperty));
			}
			parameters.put(nomeProperty,values);
		}
		return parameters;
	}

}

class ResultAggregate {
	
	private String resourceName;
	private String methodName;
	private List<String> list1 = new ArrayList<String>();
	private List<String> list2 = new ArrayList<String>();
	private List<String> list3 = new ArrayList<String>();
	
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
				if(this.list1!=null && this.list1.size()>0) {
					risorse = this.list1.toArray(new String[1]);
				}
				if(this.list2!=null && this.list2.size()>0) {
					risorseTransaction = this.list2.toArray(new String[1]);
				}
				if(this.list3!=null && this.list3.size()>0) {
					risorseStatistiche = this.list3.toArray(new String[1]);
				}
				if(MonitoraggioRisorse.CONNESSIONI_ALLOCATE_DB_MANAGER.equals(this.methodName)) {
					content =  MonitoraggioRisorse.getResultUsedDBConnections(risorse, risorseTransaction, risorseStatistiche);
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
			if(this.list1!=null && this.list1.size()>0) {
				risorse = this.list1.toArray(new String[1]);
			}
			content = JmxDataSource.getResultUsedDBConnections(risorse);
		}
		else if(CostantiPdD.JMX_LOAD_BALANCER.equals(this.resourceName)) {
			if(this.list1!=null && this.list1.size()>0) {
				StringBuilder sb = new StringBuilder();
				for (String l : this.list1) {
					if(sb.length()>0) {
						sb.append("\n");
					}
					sb.append(l);
				}
				content = sb.toString();
			}
		}
		
		if(content!=null) {
			httpResponse.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
			httpResponse.setContent(content.getBytes());
		}
		return httpResponse;
	}
	
}
