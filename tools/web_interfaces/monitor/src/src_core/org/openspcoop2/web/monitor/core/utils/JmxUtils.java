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
package org.openspcoop2.web.monitor.core.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.slf4j.Logger;

/**
 * JmxUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class JmxUtils {

	private PddMonitorProperties monitorProperties = null;
	private transient Logger log;
	public JmxUtils(Logger log) throws Exception{
		this.log = log;
		this.monitorProperties = PddMonitorProperties.getInstance(this.log);
	}
	
	public boolean isJmxPdD_tipoAccessoOpenSPCoop(String alias)throws Exception {
		return PddMonitorProperties.RESOURCE_JMX_PDD_TIPOLOGIA_ACCESSO_OPENSPCOOP.equals(this.monitorProperties.getJmxPdD_tipoAccesso(alias));
	}
	
	public Object getGestoreRisorseJMX(String alias)  throws Exception{
		try {
			if(isJmxPdD_tipoAccessoOpenSPCoop(alias)){
				//System.out.println("=================== REMOTA OPENSPCOOP =======================");
				String remoteUrl = this.monitorProperties.getJmxPdD_remoteAccess_url(alias);
				if(remoteUrl==null){
					throw new Exception("Configurazione errata (pdd:"+alias+") accesso via check. Non e' stata indicata la url");
				}
				return remoteUrl;
			}
			else{
				org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX gestoreJMX = null;
				
				if(this.monitorProperties.getJmxPdD_remoteAccess_url(alias)!=null && !"".equals(this.monitorProperties.getJmxPdD_remoteAccess_url(alias))
						&& !"locale".equals(this.monitorProperties.getJmxPdD_remoteAccess_url(alias))
						){
					//System.out.println("=================== REMOTA =======================");
					String remoteUrl = this.monitorProperties.getJmxPdD_remoteAccess_url(alias);
					String factory = this.monitorProperties.getJmxPdD_remoteAccess_factory(alias);
					if(factory==null){
						throw new Exception("Configurazione errata (pdd:"+alias+") per l'accesso alla url ["+remoteUrl+"] via jmx. Non e' stata indicata una factory");
					}
					String as = this.monitorProperties.getJmxPdD_remoteAccess_applicationServer(alias);
					if(as==null){
						throw new Exception("Configurazione errata (pdd:"+alias+") per l'accesso alla url ["+remoteUrl+"] via jmx. Non e' stato indicato il tipo di application server");
					}
					gestoreJMX = new org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX(as, factory, remoteUrl, 
							this.monitorProperties.getJmxPdD_remoteAccess_username(alias), 
							this.monitorProperties.getJmxPdD_remoteAccess_password(alias), this.log);
				}
				else{
					//System.out.println("=================== LOCALE =======================");
					gestoreJMX = new org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX(this.log);
					
				}
				
				return gestoreJMX;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	private HttpResponse invokeHttp(String urlWithParameters, String alias) throws Exception {
		String username = this.monitorProperties.getJmxPdD_remoteAccess_username(alias);
		String password = this.monitorProperties.getJmxPdD_remoteAccess_password(alias);
		boolean https = this.monitorProperties.isJmxPdD_remoteAccess_https(alias);
		boolean https_verificaHostName = true;
		boolean https_autenticazioneServer = true;
		String https_truststorePath = null;
		String https_truststoreType = null;
		String https_truststorePassword = null;
		if(https) {
			https_verificaHostName = this.monitorProperties.isJmxPdD_remoteAccess_https_verificaHostName(alias);
			https_autenticazioneServer = this.monitorProperties.isJmxPdD_remoteAccess_https_autenticazioneServer(alias);
			if(https_autenticazioneServer) {
				https_truststorePath = this.monitorProperties.getJmxPdD_remoteAccess_https_autenticazioneServer_truststorePath(alias);
				if(StringUtils.isEmpty(https_truststorePath)) {
					throw new Exception("[alias:"+alias+"] TLS Truststore path non fornito");
				}
				https_truststoreType = this.monitorProperties.getJmxPdD_remoteAccess_https_autenticazioneServer_truststoreType(alias);
				if(StringUtils.isEmpty(https_truststoreType)) {
					throw new Exception("[alias:"+alias+"] TLS Truststore type non fornito");
				}
				https_truststorePassword = this.monitorProperties.getJmxPdD_remoteAccess_https_autenticazioneServer_truststorePassword(alias);
				if(StringUtils.isEmpty(https_truststorePassword)) {
					throw new Exception("[alias:"+alias+"] TLS Truststore password non fornito");
				}
			}
		}
		
		Integer connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
		Integer readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
		// Fix abbasso i tempi di default
		connectionTimeout = 5000;
		readConnectionTimeout = 5000;
		String connectionTimeoutS = this.monitorProperties.getJmxPdD_remoteAccess_connectionTimeout(alias);
		if(connectionTimeoutS!=null) {
			connectionTimeout = Integer.valueOf(connectionTimeoutS);
		}
		String readConnectionTimeoutS = this.monitorProperties.getJmxPdD_remoteAccess_readConnectionTimeout(alias);
		if(readConnectionTimeoutS!=null) {
			readConnectionTimeout = Integer.valueOf(readConnectionTimeoutS);
		}
		
		HttpResponse response = null;
		if(https) {
			HttpRequest httpRequest = new HttpRequest();
			httpRequest.setUrl(urlWithParameters);
			httpRequest.setConnectTimeout(connectionTimeout);
			httpRequest.setReadTimeout(readConnectionTimeout);
			httpRequest.setUsername(username);
			httpRequest.setPassword(password);
			httpRequest.setMethod(HttpRequestMethod.GET);
			httpRequest.setHostnameVerifier(https_verificaHostName);
			if(https_autenticazioneServer) {
				httpRequest.setTrustStorePath(https_truststorePath);
				httpRequest.setTrustStoreType(https_truststoreType);
				httpRequest.setTrustStorePassword(https_truststorePassword);
			}
			else {
				httpRequest.setTrustAllCerts(true);
			}
			response = HttpUtilities.httpInvoke(httpRequest);
		}
		else {
			response = HttpUtilities.getHTTPResponse(urlWithParameters,
					readConnectionTimeout, connectionTimeout,
					username, password);
		}
		return response;
	}
	
	public String invokeJMXMethod(Object gestore, String alias, String type, String nomeRisorsa, String nomeMetodo) throws Exception{
		return invokeJMXMethod(gestore, alias, type, nomeRisorsa, nomeMetodo, null);
	}
	public String invokeJMXMethod(Object gestore, String alias, String type, String nomeRisorsa, String nomeMetodo, String parametro) throws Exception{
		try {
			if(gestore instanceof org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX){
				
				Object [] params = null;
				String [] signatures = null;
				if(parametro!=null && !"".equals(parametro)){
					params = new Object[] {parametro};
					signatures = new String[] {String.class.getName()};
				}
				
				String tmp = (String) ((org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX)gestore).invoke(this.monitorProperties.getJmxPdD_dominio(alias), 
						type, nomeRisorsa, nomeMetodo, params, signatures);
				if(tmp.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)){
					throw new Exception(tmp); 
				}
				return tmp;
			}
			else if(gestore instanceof String){
				String url = (String) gestore;
			
				Map<String, String> p = new HashMap<String,String>();
				p.put(CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME, nomeRisorsa);
				p.put(CostantiPdD.CHECK_STATO_PDD_METHOD_NAME, nomeMetodo);
				if(parametro!=null && !"".equals(parametro)){
					p.put(CostantiPdD.CHECK_STATO_PDD_PARAM_VALUE, parametro);
				}
				String urlWithParameters = TransportUtils.buildLocationWithURLBasedParameter(p, url);
				
				HttpResponse response = invokeHttp(urlWithParameters, alias);
				if(response.getResultHTTPOperation()!=200){
					String error = "[httpCode "+response.getResultHTTPOperation()+"]";
					if(response.getContent()!=null){
						error+= " "+new String(response.getContent());
					}
					return error;
				}
				else{
					return new String(response.getContent());
				}
			}
			else {
				throw new Exception("Gestore di tipo ["+gestore.getClass().getName()+"] non gestito");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String readJMXAttribute(Object gestore, String alias, String type, String nomeRisorsa, String nomeAttributo) throws Exception{
		try {
			if(gestore instanceof org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX){
				Object t = ((org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX)gestore).getAttribute(this.monitorProperties.getJmxPdD_dominio(alias), type, nomeRisorsa, nomeAttributo);
				if(t instanceof String){
					String tmp = (String) t; 
					if(tmp.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)){
						throw new Exception(tmp); 
					}
					return tmp;
				}
				else if(t instanceof Boolean){
					return ((Boolean)t).toString();
				}
				else{
					return t.toString();
				}
			}
			else if(gestore instanceof String){
				String url = (String) gestore;
				
				Map<String, String> p = new HashMap<String, String>();
				p.put(CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME, nomeRisorsa);
				p.put(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_NAME, nomeAttributo);
				String urlWithParameters = TransportUtils.buildLocationWithURLBasedParameter(p, url);
				
				HttpResponse response = invokeHttp(urlWithParameters, alias);
				if(response.getResultHTTPOperation()!=200){
					String error = "[httpCode "+response.getResultHTTPOperation()+"]";
					if(response.getContent()!=null){
						error+= " "+new String(response.getContent());
					}
					return error;
				}
				else{
					return new String(response.getContent());
				}
			}
			else {
				throw new Exception("Gestore di tipo ["+gestore.getClass().getName()+"] non gestito");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void setJMXAttribute(Object gestore, String alias, String type, String nomeRisorsa, String nomeAttributo, Object value) throws Exception{
		try {
			if(gestore instanceof org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX){
				((org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX)gestore).setAttribute(this.monitorProperties.getJmxPdD_dominio(alias), type, nomeRisorsa, nomeAttributo, value);
			}
			else if(gestore instanceof String){
				String url = (String) gestore;
				
				Map<String, String> p = new HashMap<String, String>();
				p.put(CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME, nomeRisorsa);
				p.put(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_NAME, nomeAttributo);
				if(value instanceof Boolean){
					p.put(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_BOOLEAN_VALUE, value.toString());
				}
				else{
					p.put(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_VALUE, value.toString());
				}
				String urlWithParameters = TransportUtils.buildLocationWithURLBasedParameter(p, url);
				
				HttpResponse response = invokeHttp(urlWithParameters, alias);
				if(response.getResultHTTPOperation()!=200){
					String error = "[httpCode "+response.getResultHTTPOperation()+"]";
					if(response.getContent()!=null){
						error+= " "+new String(response.getContent());
					}
					throw new Exception(error);
				}
			}
			else {
				throw new Exception("Gestore di tipo ["+gestore.getClass().getName()+"] non gestito");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
}
