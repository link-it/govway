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

package org.openspcoop2.utils.transport;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

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
	protected Map<String, List<String>> parameters;
	/* ---- Coppie nome/valori di invocazione inserite nell'header del trasporto --- */
	protected Map<String, List<String>> headers;
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
	
	protected Logger log = null;
	
	public TransportRequestContext() throws UtilsException{
		this(null);
	}
	public TransportRequestContext(Logger log) throws UtilsException{
		if(log==null) {
			this.log = LoggerWrapperFactory.getLogger(TransportRequestContext.class);
		}
		else {
			this.log = log;
		}
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
		
	@Deprecated
	public Map<String, String> getParametersFormBased(){
		return TransportUtils.convertToMapSingleValue(this.parameters);
	}
	public Map<String, List<String>> getParameters(){
		return this.parameters;
	}
	
	@Deprecated
	public String getParameterFormBased(String name){
		if(this.parameters==null){
			return null;
		}
		return TransportUtils.getObjectAsString(this.parameters, name);
	}
	public String getParameter_compactMultipleValues(String name){
		if(this.parameters==null){
			return null;
		}
		return TransportUtils.getObjectAsString(this.parameters, name);
	}
	public String getParameterFirstValue(String name){
		List<String> l = getParameterValues(name);
		if(l!=null && !l.isEmpty()) {
			return l.get(0);
		}
		return null;
	}
	public List<String> getParameterValues(String name){
		if(this.parameters==null){
			return null;
		}
		return TransportUtils.getRawObject(this.parameters, name);
	}
	
	@Deprecated
	public Object removeParameterFormBased(String name){
		if(this.parameters==null){
			return null;
		}
		return TransportUtils.removeObjectAsString(this.parameters, name);
	}
	public String removeParameter_compactMultipleValues(String name){
		if(this.parameters==null){
			return null;
		}
		return TransportUtils.removeObjectAsString(this.parameters, name);
	}
	public List<String> removeParameter(String name){
		if(this.parameters==null){
			return null;
		}
		return TransportUtils.removeRawObject(this.parameters, name);
	}
	
	@Deprecated
	public Map<String, String> getParametersTrasporto() {
		return TransportUtils.convertToMapSingleValue(this.headers);
	}
	public Map<String, List<String>> getHeaders(){
		return this.headers;
	}
	
	@Deprecated
	public String getParameterTrasporto(String name){
		if(this.headers==null){
			return null;
		}
		return TransportUtils.getObjectAsString(this.headers, name);
	}
	public String getHeader_compactMultipleValues(String name){
		if(this.headers==null){
			return null;
		}
		return TransportUtils.getObjectAsString(this.headers, name);
	}
	public String getHeaderFirstValue(String name){
		List<String> l = getHeaderValues(name);
		if(l!=null && !l.isEmpty()) {
			return l.get(0);
		}
		return null;
	}
	public List<String> getHeaderValues(String name){
		if(this.headers==null){
			return null;
		}
		return TransportUtils.getRawObject(this.headers, name);
	}
	
	@Deprecated
	public Object removeParameterTrasporto(String name){
		if(this.headers==null){
			return null;
		}
		return TransportUtils.removeObjectAsString(this.headers, name);
	}
	public String removeHeader_compactMultipleValues(String name){
		if(this.headers==null){
			return null;
		}
		return TransportUtils.removeObjectAsString(this.headers, name);
	}
	public List<String> removeHeader(String name){
		if(this.headers==null){
			return null;
		}
		return TransportUtils.removeRawObject(this.headers, name);
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
		return TransportUtils.getRawObject(this.cookiesValue, name);
	}
	public Map<String, Integer> getCookiesMaxAge() {
		return this.cookiesMaxAge;
	}
	public Integer getCookieMaxAge(String name){
		if(this.cookiesMaxAge==null){
			return null;
		}
		return TransportUtils.getRawObject(this.cookiesMaxAge, name);
	}
	
	public String getContentType(){
		if(this.headers!=null){
			return this.getHeaderFirstValue(HttpConstants.CONTENT_TYPE);
		}
		return null;
	}
	public String getContentType(boolean returnMsgErroreIfNotFound) throws Exception{

		if(this.headers!=null && 
				TransportUtils.containsKey(this.headers, HttpConstants.CONTENT_TYPE)
			){
			String ct = this.getHeaderFirstValue(HttpConstants.CONTENT_TYPE);
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
		return TransportUtils.buildUrlWithParameters(this.parameters, this.requestURI, this.log);
	}
	public String getUrlInvocazioneWithParameters() {
		return this.getUrlInvocazione_formBased();
	}
	public String getUrlInvocazioneWithoutParameters() {
		return this.requestURI;
	}

	@Deprecated
	public void setParametersFormBased(Map<String, String> parametersFormBased) {
		this.parameters = TransportUtils.convertToMapListValues(parametersFormBased);
	}
	public void setParameters(Map<String, List<String>> parametersFormBased) {
		this.parameters = parametersFormBased;
	}

	@Deprecated
	public void setParametersTrasporto(Map<String, String> parametersTrasporto) {
		this.headers = TransportUtils.convertToMapListValues(parametersTrasporto);
	}
	public void setHeaders(Map<String, List<String>> parametersFormBased) {
		this.headers = parametersFormBased;
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
	
	
	@Override
	public String toString() {
		return this.toString("");
	}
	public String toString(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append("DatiRichiesta");
		if(this.webContext!=null) {
			sb.append("\n").append(prefix).append("webContext: ").append(this.webContext);
		}
		if(this.requestURI!=null) {
			sb.append("\n").append(prefix).append("requestURI: ").append(this.requestURI);
		}
		if(this.requestType!=null) {
			sb.append("\n").append(prefix).append("requestType: ").append(this.requestType);
		}
		if(this.source!=null) {
			sb.append("\n").append(prefix).append("source: ").append(this.source);
		}
		if(this.protocolName!=null) {
			sb.append("\n").append(prefix).append("protocolName: ").append(this.protocolName);
		}
		if(this.protocolWebContext!=null) {
			sb.append("\n").append(prefix).append("protocolWebContext: ").append(this.protocolWebContext);
		}
		if(this.function!=null) {
			sb.append("\n").append(prefix).append("function: ").append(this.function);
		}
		if(this.functionParameters!=null) {
			sb.append("\n").append(prefix).append("functionParameters: ").append(this.functionParameters);
		}
		if(this.interfaceName!=null) {
			sb.append("\n").append(prefix).append("interfaceName: ").append(this.interfaceName);
		}
		if(this.credential!=null) {
			sb.append("\n").append(prefix).append("credential: ").append(this.credential);
		}
		if(this.parameters!=null && !this.parameters.isEmpty()) {
			sb.append("\n").append(prefix).append("parameters: ").append(this.convertWithStream(this.parameters));
		}
		if(this.headers!=null && !this.headers.isEmpty()) {
			sb.append("\n").append(prefix).append("headers: ").append(this.convertWithStream(this.headers));
		}
		if(this.cookiesValue!=null && !this.cookiesValue.isEmpty()) {
			sb.append("\n").append(prefix).append("cookiesValue: ").append(this.convertWithStream(this.cookiesValue));
		}
		if(this.cookiesMaxAge!=null && !this.cookiesMaxAge.isEmpty()) {
			sb.append("\n").append(prefix).append("cookiesMaxAge: ").append(this.convertWithStream(this.cookiesMaxAge));
		}
		return sb.toString();
	}
	
	private String convertWithStream(Map<?, ?> map) {
	    String mapAsString = map.keySet().stream()
	      .map(key -> key + "=" + map.get(key))
	      .collect(Collectors.joining(", ", "{", "}"));
	    return mapAsString;
	}
	
	

}
