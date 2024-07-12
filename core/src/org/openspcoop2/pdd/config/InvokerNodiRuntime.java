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
package org.openspcoop2.pdd.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
 * JmxUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class InvokerNodiRuntime {

	private ConfigurazioneNodiRuntime configurazioneNodiRuntime = null;
	private Logger log;
	public InvokerNodiRuntime(Logger log, ConfigurazioneNodiRuntime config) {
		this.log = log;
		this.configurazioneNodiRuntime = config;
	}
	
	public static final String RESOURCE_URL_LOCALE = "locale";
	
	public static final String PREFIX_HTTP_CODE = "[httpCode ";
	private static final String PREFIX_ALIAS = "[alias:";
	
	public boolean isJmxPdD_tipoAccessoOpenSPCoop(String alias) {
		return ConfigurazioneNodiRuntime.RESOURCE_TIPOLOGIA_ACCESSO_OPENSPCOOP.equals(this.configurazioneNodiRuntime.getTipoAccesso(alias))
				||
				ConfigurazioneNodiRuntime.RESOURCE_TIPOLOGIA_ACCESSO_GOVWAY.equals(this.configurazioneNodiRuntime.getTipoAccesso(alias));
	}
	
	public Object getGestoreRisorseJMX(String alias)  throws UtilsException{
		try {
			if(isJmxPdD_tipoAccessoOpenSPCoop(alias)){
				/**System.out.println("=================== REMOTA OPENSPCOOP =======================");*/
				String remoteUrl = this.configurazioneNodiRuntime.getResourceUrl(alias);
				if(remoteUrl==null){
					throw new Exception("Configurazione errata (pdd:"+alias+") accesso via check. Non e' stata indicata la url");
				}
				return remoteUrl;
			}
			else{
				org.openspcoop2.pdd.core.jmx.GestoreRisorseJMXGovWay gestoreJMX = null;
				
				if(this.configurazioneNodiRuntime.getResourceUrl(alias)!=null && !"".equals(this.configurazioneNodiRuntime.getResourceUrl(alias))
						&& !RESOURCE_URL_LOCALE.equals(this.configurazioneNodiRuntime.getResourceUrl(alias))
						){
					/**System.out.println("=================== REMOTA =======================");*/
					String remoteUrl = this.configurazioneNodiRuntime.getResourceUrl(alias);
					String factory = this.configurazioneNodiRuntime.getFactory(alias);
					if(factory==null){
						throw new UtilsException("Configurazione errata (pdd:"+alias+") per l'accesso alla url ["+remoteUrl+"] via jmx. Non e' stata indicata una factory");
					}
					String as = this.configurazioneNodiRuntime.getAs(alias);
					if(as==null){
						throw new UtilsException("Configurazione errata (pdd:"+alias+") per l'accesso alla url ["+remoteUrl+"] via jmx. Non e' stato indicato il tipo di application server");
					}
					gestoreJMX = new org.openspcoop2.pdd.core.jmx.GestoreRisorseJMXGovWay(as, factory, remoteUrl, 
							this.configurazioneNodiRuntime.getUsername(alias), 
							this.configurazioneNodiRuntime.getPassword(alias), this.log);
				}
				else{
					/**System.out.println("=================== LOCALE =======================");*/
					gestoreJMX = new org.openspcoop2.pdd.core.jmx.GestoreRisorseJMXGovWay(this.log);
					
				}
				
				if(this.log==null) {
					gestoreJMX.setLogActive(false);
				}
				
				return gestoreJMX;
			}
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	private HttpResponse invokeHttp(String urlWithParameters, String alias, Boolean slowOperation) throws UtilsException {
		String username = this.configurazioneNodiRuntime.getUsername(alias);
		String password = this.configurazioneNodiRuntime.getPassword(alias);
		boolean https = this.configurazioneNodiRuntime.isHttps(alias);
		boolean httpsVerificaHostName = true;
		boolean httpsAutenticazioneServer = true;
		String httpsTruststorePath = null;
		String httpsTruststoreType = null;
		String httpsTruststorePassword = null;
		if(https) {
			httpsVerificaHostName = this.configurazioneNodiRuntime.isHttps_verificaHostName(alias);
			httpsAutenticazioneServer = this.configurazioneNodiRuntime.isHttps_autenticazioneServer(alias);
			if(httpsAutenticazioneServer) {
				httpsTruststorePath = this.configurazioneNodiRuntime.getHttps_autenticazioneServer_truststorePath(alias);
				if(StringUtils.isEmpty(httpsTruststorePath)) {
					throw new UtilsException(PREFIX_ALIAS+alias+"] TLS Truststore path non fornito");
				}
				httpsTruststoreType = this.configurazioneNodiRuntime.getHttps_autenticazioneServer_truststoreType(alias);
				if(StringUtils.isEmpty(httpsTruststoreType)) {
					throw new UtilsException(PREFIX_ALIAS+alias+"] TLS Truststore type non fornito");
				}
				httpsTruststorePassword = this.configurazioneNodiRuntime.getHttps_autenticazioneServer_truststorePassword(alias);
				if(StringUtils.isEmpty(httpsTruststorePassword)) {
					throw new UtilsException(PREFIX_ALIAS+alias+"] TLS Truststore password non fornito");
				}
			}
		}
		
		/**Integer connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
		Integer readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;*/
		// Fix abbasso i tempi di default
		Integer connectionTimeout = 5000;
		Integer readConnectionTimeout = 5000;
		String connectionTimeoutS = this.configurazioneNodiRuntime.getConnectionTimeout(alias);
		if(connectionTimeoutS!=null) {
			connectionTimeout = Integer.valueOf(connectionTimeoutS);
		}
		String readConnectionTimeoutS = this.configurazioneNodiRuntime.getReadConnectionTimeout(alias);
		if(readConnectionTimeoutS!=null) {
			readConnectionTimeout = Integer.valueOf(readConnectionTimeoutS);
		}
		if(slowOperation!=null && slowOperation) {
			String readConnectionTimeoutSlowOperationS = this.configurazioneNodiRuntime.getReadConnectionTimeout_slowOperation(alias);
			if(readConnectionTimeoutSlowOperationS!=null) {
				readConnectionTimeout = Integer.valueOf(readConnectionTimeoutSlowOperationS);
			}
			else {
				readConnectionTimeout = 60000; // default 60 secondi
			}
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
			httpRequest.setHostnameVerifier(httpsVerificaHostName);
			if(httpsAutenticazioneServer) {
				httpRequest.setTrustStorePath(httpsTruststorePath);
				httpRequest.setTrustStoreType(httpsTruststoreType);
				httpRequest.setTrustStorePassword(httpsTruststorePassword);
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
	
	public String invokeJMXMethod(String alias, String type, String nomeRisorsa, String nomeMetodo) throws UtilsException{
		return invokeJMXMethodEngine(alias, type, nomeRisorsa, nomeMetodo, false);
	}
	public String invokeJMXMethod(String alias, String type, String nomeRisorsa, String nomeMetodo, Boolean slowOperation) throws UtilsException{
		return invokeJMXMethodEngine(alias, type, nomeRisorsa, nomeMetodo, slowOperation);
	}
	public String invokeJMXMethod(String alias, String type, String nomeRisorsa, String nomeMetodo, String parametro) throws UtilsException{
		return invokeJMXMethodEngine(alias, type, nomeRisorsa, nomeMetodo, false, 
				(parametro!=null && !"".equals(parametro)) ? parametro : null);
	}
	public String invokeJMXMethod(String alias, String type, String nomeRisorsa, String nomeMetodo, Boolean slowOperation, String parametro) throws UtilsException{
		return invokeJMXMethodEngine(alias, type, nomeRisorsa, nomeMetodo, slowOperation, 
				(parametro!=null && !"".equals(parametro)) ? parametro : null);
	}
	public String invokeJMXMethod(String alias, String type, String nomeRisorsa, String nomeMetodo, Object ... parametri ) throws UtilsException{
		return invokeJMXMethodEngine(alias, type, nomeRisorsa, nomeMetodo, false, parametri );
	}
	public String invokeJMXMethod(String alias, String type, String nomeRisorsa, String nomeMetodo, Boolean slowOperation, Object ... parametri ) throws UtilsException{
		return invokeJMXMethodEngine(alias, type, nomeRisorsa, nomeMetodo, slowOperation, parametri );
	}
	private String invokeJMXMethodEngine(String alias, String type, String nomeRisorsa, String nomeMetodo, Boolean slowOperation, Object ... parametri ) throws UtilsException{
		
		Object gestore = this.getGestoreRisorseJMX(alias);
		
		try {
			if(gestore instanceof org.openspcoop2.pdd.core.jmx.GestoreRisorseJMXGovWay){
				
				Object [] params = null;
				String [] signatures = null;
				if(parametri!=null && parametri.length>0){
					params = parametri;
					signatures = new String[parametri.length];
					for (int i = 0; i < parametri.length; i++) {
						signatures[i] = parametri[i].getClass().getName();
					}
				}
				
				org.openspcoop2.pdd.core.jmx.GestoreRisorseJMXGovWay gestoreJMXGovWay = ((org.openspcoop2.pdd.core.jmx.GestoreRisorseJMXGovWay)gestore);
				if(this.log==null) {
					gestoreJMXGovWay.setLogActive(false);
				}
				
				String tmp = (String) gestoreJMXGovWay.invoke(this.configurazioneNodiRuntime.getDominio(alias), 
						type, nomeRisorsa, nomeMetodo, params, signatures);
				if(tmp.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)){
					throw new UtilsException(tmp); 
				}
				return tmp;
			}
			else if(gestore instanceof String){
				String url = (String) gestore;
			
				Map<String, List<String>> p = new HashMap<>();
				TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME, nomeRisorsa);
				TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_METHOD_NAME, nomeMetodo);
				if(parametri!=null && parametri.length>0){
					for (int i = 0; i < parametri.length; i++) {
						Object o = parametri[i];
						if(o!=null) {
							if(o instanceof Integer) {
								Integer intValue = (Integer) o;
								if(i==0) {
									TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_PARAM_INT_VALUE, intValue.intValue()+"");
								}
								else if(i==1) {
									TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_PARAM_INT_VALUE_2, intValue.intValue()+"");
								}
								else if(i==2) {
									TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_PARAM_INT_VALUE_3, intValue.intValue()+"");
								}
								else {
									throw new UtilsException("Gestore di tipo ["+gestore.getClass().getName()+"] non gestisce un numero maggiore di 3 parametri");
								}
							}
							else if(o instanceof Long) {
								Long longValue = (Long) o;
								if(i==0) {
									TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE, longValue.longValue()+"");
								}
								else if(i==1) {
									TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE_2, longValue.longValue()+"");
								}
								else if(i==2) {
									TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE_3, longValue.longValue()+"");
								}
								else {
									throw new UtilsException("Gestore di tipo ["+gestore.getClass().getName()+"] non gestisce un numero maggiore di 3 parametri");
								}
							}
							else if(o instanceof Boolean) {
								Boolean booleanValue = (Boolean) o;
								if(i==0) {
									TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_PARAM_BOOLEAN_VALUE, booleanValue.booleanValue()+"");
								}
								else if(i==1) {
									TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_PARAM_BOOLEAN_VALUE_2, booleanValue.booleanValue()+"");
								}
								else if(i==2) {
									TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_PARAM_BOOLEAN_VALUE_3, booleanValue.booleanValue()+"");
								}
								else {
									throw new UtilsException("Gestore di tipo ["+gestore.getClass().getName()+"] non gestisce un numero maggiore di 3 parametri");
								}
							}
							else {
								String stringValue = o.toString();
								if(i==0) {
									TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_PARAM_VALUE, stringValue);
								}
								else if(i==1) {
									TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_PARAM_VALUE_2, stringValue);
								}
								else if(i==2) {
									TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_PARAM_VALUE_3, stringValue);
								}
								else {
									throw new UtilsException("Gestore di tipo ["+gestore.getClass().getName()+"] non gestisce un numero maggiore di 3 parametri");
								}
							}
						}
					}
				}
				
				String urlWithParameters = TransportUtils.buildUrlWithParameters(p, url);
				
				HttpResponse response = invokeHttp(urlWithParameters, alias, slowOperation);
				if(response.getResultHTTPOperation()!=200){
					String error = PREFIX_HTTP_CODE+response.getResultHTTPOperation()+"]";
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
				throw new UtilsException("Gestore di tipo ["+gestore.getClass().getName()+"] non gestito");
			}
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public String readJMXAttribute(String alias, String type, String nomeRisorsa, String nomeAttributo) throws UtilsException{
		return readJMXAttribute(alias, type, nomeRisorsa, nomeAttributo, false);
	}
	public String readJMXAttribute(String alias, String type, String nomeRisorsa, String nomeAttributo, Boolean slowOperation) throws UtilsException{
		
		Object gestore = this.getGestoreRisorseJMX(alias);
		
		try {
			if(gestore instanceof org.openspcoop2.pdd.core.jmx.GestoreRisorseJMXGovWay){
				org.openspcoop2.pdd.core.jmx.GestoreRisorseJMXGovWay gestoreJMXGovWay = ((org.openspcoop2.pdd.core.jmx.GestoreRisorseJMXGovWay)gestore);
				if(this.log==null) {
					gestoreJMXGovWay.setLogActive(false);
				}
				Object t = gestoreJMXGovWay.getAttribute(this.configurazioneNodiRuntime.getDominio(alias), type, nomeRisorsa, nomeAttributo);
				if(t instanceof String){
					String tmp = (String) t; 
					if(tmp.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)){
						throw new UtilsException(tmp); 
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
				
				Map<String, List<String>> p = new HashMap<>();
				TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME, nomeRisorsa);
				TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_NAME, nomeAttributo);
				String urlWithParameters = TransportUtils.buildUrlWithParameters(p, url);
				
				HttpResponse response = invokeHttp(urlWithParameters, alias, slowOperation);
				if(response.getResultHTTPOperation()!=200){
					String error = PREFIX_HTTP_CODE+response.getResultHTTPOperation()+"]";
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
				throw new UtilsException("Gestore di tipo ["+gestore.getClass().getName()+"] non gestito");
			}
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public void setJMXAttribute(String alias, String type, String nomeRisorsa, String nomeAttributo, Object value) throws UtilsException{
		setJMXAttribute(alias, type, nomeRisorsa, nomeAttributo, value, false);	
	}
	public void setJMXAttribute(String alias, String type, String nomeRisorsa, String nomeAttributo, Object value, Boolean slowOperation) throws UtilsException{
		
		Object gestore = this.getGestoreRisorseJMX(alias);
		
		try {
			if(gestore instanceof org.openspcoop2.pdd.core.jmx.GestoreRisorseJMXGovWay){
				org.openspcoop2.pdd.core.jmx.GestoreRisorseJMXGovWay gestoreJMXGovWay = ((org.openspcoop2.pdd.core.jmx.GestoreRisorseJMXGovWay)gestore);
				if(this.log==null) {
					gestoreJMXGovWay.setLogActive(false);
				}
				gestoreJMXGovWay.setAttribute(this.configurazioneNodiRuntime.getDominio(alias), type, nomeRisorsa, nomeAttributo, value);
			}
			else if(gestore instanceof String){
				String url = (String) gestore;
				
				Map<String, List<String>> p = new HashMap<>();
				TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME, nomeRisorsa);
				TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_NAME, nomeAttributo);
				if(value instanceof Boolean){
					TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_BOOLEAN_VALUE, value.toString());
				}
				else{
					TransportUtils.setParameter(p,CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_VALUE, value.toString());
				}
				String urlWithParameters = TransportUtils.buildUrlWithParameters(p, url);
				
				HttpResponse response = invokeHttp(urlWithParameters, alias, slowOperation);
				if(response.getResultHTTPOperation()!=200){
					String error = PREFIX_HTTP_CODE+response.getResultHTTPOperation()+"]";
					if(response.getContent()!=null){
						error+= " "+new String(response.getContent());
					}
					throw new UtilsException(error);
				}
			}
			else {
				throw new UtilsException("Gestore di tipo ["+gestore.getClass().getName()+"] non gestito");
			}
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
}
