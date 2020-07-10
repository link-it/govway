/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.transport;

import java.util.Map;
import java.util.Set;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * RequestContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class TransportRequestContext implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	/* ---- Coppie nome/valori di invocazione form-based --- */
	protected Map<String, String> parametersFormBased;
	/* ---- Coppie nome/valori di invocazione inserite nell'header del trasporto --- */
	protected Map<String, String> parametersTrasporto;
	/* ---- Coppie nome/valori dei cookie --- */
	protected Map<String, String> cookiesValue;
	protected Map<String, Integer> cookiesMaxAge;
	
	protected Credential credential = null;
	
	protected String webContext = null; // es. openspcoop2
	protected String requestURI = null;
	
	protected String requestType = null; // GET/POST/...
	
	protected String source = null; // location es. https://ip:socketPort
	
	protected String protocolName = null; // identificativo del protocollo
	protected String protocolWebContext = null; // webcontext utilizzato per indirizzare il protocollo, ad esempio verra' assegnato api per trasparente.
	
	protected String function = null;
	protected String functionParameters = null;
	protected String interfaceName = null; // Nome PD o PA
	
	public TransportRequestContext() throws UtilsException{
		
	}
	
	
	public Credential getCredential() {
		return this.credential;
	}
	public String getWebContext() {
		return this.webContext;
	}
	public String getRequestURI() {
		return this.requestURI;
	}
	public String getSource() {
		return this.source;
	}
	public String getProtocolName() {
		return this.protocolName;
	}
	public String getProtocolWebContext() {
		return this.protocolWebContext;
	}
	public String getFunction() {
		return this.function;
	}
	public String getFunctionParameters() {
		return this.functionParameters;
	}
	public String getRequestType() {
		return this.requestType;
	}
	public String getInterfaceName() {
		return this.interfaceName;
	}
	
	public Map<String, String> getParametersFormBased(){
		return this.parametersFormBased;
	}
	public String getParameterFormBased(String name){
		if(this.parametersFormBased==null){
			return null;
		}
		return TransportUtils.get(this.parametersFormBased, name);
	}
	public Object removeParameterFormBased(String name){
		if(this.parametersFormBased==null){
			return null;
		}
		return TransportUtils.remove(this.parametersFormBased, name);
	}
	
	public Map<String, String> getParametersTrasporto() {
		return this.parametersTrasporto;
	}
	public String getParameterTrasporto(String name){
		if(this.parametersTrasporto==null){
			return null;
		}
		return TransportUtils.get(this.parametersTrasporto, name);
	}
	public Object removeParameterTrasporto(String name){
		if(this.parametersTrasporto==null){
			return null;
		}
		return TransportUtils.remove(this.parametersTrasporto, name);
	}
	
	public Set<String> getCookies(){
		return this.cookiesValue.keySet();
	}
	public Map<String, String> getCookiesValue() {
		return this.cookiesValue;
	}
	public String getCookieValue(String name){
		if(this.cookiesValue==null){
			return null;
		}
		return TransportUtils.get(this.cookiesValue, name);
	}
	public Map<String, Integer> getCookiesMaxAge() {
		return this.cookiesMaxAge;
	}
	public Integer getCookieMaxAge(String name){
		if(this.cookiesMaxAge==null){
			return null;
		}
		return TransportUtils.getObject(this.cookiesMaxAge, name);
	}
	
	public String getContentType(){
		if(this.parametersTrasporto!=null){
			return this.getParameterTrasporto(HttpConstants.CONTENT_TYPE);
		}
		return null;
	}
	public String getContentType(boolean returnMsgErroreIfNotFound) throws Exception{

		if(this.parametersTrasporto!=null && 
				TransportUtils.hasKey(this.parametersTrasporto, HttpConstants.CONTENT_TYPE)
			){
			String ct = TransportUtils.get(this.parametersTrasporto, HttpConstants.CONTENT_TYPE);
			if(ct==null){
				if(returnMsgErroreIfNotFound)
					return HttpConstants.CONTENT_TYPE_NON_VALORIZZATO;
				else
					return null;
			}
			return ct;
		}
		
		if(returnMsgErroreIfNotFound)
			return HttpConstants.CONTENT_TYPE_NON_PRESENTE;
		else
			return null;
	}


	
	
	/**
	 * Ritorna l'url di invocazione 
	 *
	 * @return url di invocazione 
	 */
	public String getUrlInvocazione_formBased() {
		if(this.requestURI==null){
			return null;
		}
		return TransportUtils.buildLocationWithURLBasedParameter(this.parametersFormBased, this.requestURI);
	}

	public void setParametersFormBased(Map<String, String> parametersFormBased) {
		this.parametersFormBased = parametersFormBased;
	}

	public void setParametersTrasporto(Map<String, String> parametersTrasporto) {
		this.parametersTrasporto = parametersTrasporto;
	}

	public void setCredentials(Credential credentials) {
		this.credential = credentials;
	}	
	
	public void setWebContext(String webContext) {
		this.webContext = webContext;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	public void setProtocol(String protocolName,String protocolWebContext) {
		this.protocolName = protocolName;
		this.protocolWebContext = protocolWebContext;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public void setFunctionParameters(String functionParameters) {
		this.functionParameters = functionParameters;
	}
	
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
}
