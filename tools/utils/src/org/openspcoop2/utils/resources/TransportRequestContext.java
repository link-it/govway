/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.utils.resources;

import org.openspcoop2.utils.UtilsException;

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
	protected java.util.Properties parametersFormBased;
	/* ---- Coppie nome/valori di invocazione inserite nell'header del trasporto --- */
	protected java.util.Properties parametersTrasporto;
	
	protected String webContext = null; // es. openspcoop2
	protected String requestURI = null;
	protected String protocol = null;
	protected String function = null;
	protected String functionParameters = null;
	
	
	public TransportRequestContext() throws UtilsException{
		
	}
	
	
	public String getWebContext() {
		return this.webContext;
	}
	public String getRequestURI() {
		return this.requestURI;
	}
	public String getProtocol() {
		return this.protocol;
	}
	public String getFunction() {
		return this.function;
	}
	public String getFunctionParameters() {
		return this.functionParameters;
	}
	public java.util.Properties getParametersFormBased(){
		return this.parametersFormBased;
	}
	public String getParameterFormBased(String name){
		if(this.parametersFormBased==null){
			return null;
		}
		String value = this.parametersFormBased.getProperty(name);
		if(value==null){
			value = this.parametersFormBased.getProperty(name.toLowerCase());
		}
		if(value==null){
			value = this.parametersFormBased.getProperty(name.toUpperCase());
		}
		return value;
	}
	public java.util.Properties getParametersTrasporto() {
		return this.parametersTrasporto;
	}
	public String getParameterTrasporto(String name){
		if(this.parametersTrasporto==null){
			return null;
		}
		String value = this.parametersTrasporto.getProperty(name);
		if(value==null){
			value = this.parametersTrasporto.getProperty(name.toLowerCase());
		}
		if(value==null){
			value = this.parametersTrasporto.getProperty(name.toUpperCase());
		}
		return value;
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
		// Costruisce una url senza escape. Questo provoca applicazione di espressioni regolari non corrette.
//		String urlInvocazione =	this.requestURI;
//		if(this.parametersFormBased!=null){
//			if(this.parametersFormBased.size()>0)
//				urlInvocazione= urlInvocazione + "?";
//			for(java.util.Enumeration<?> en = this.parametersFormBased.propertyNames();en.hasMoreElements(); ){
//				if(urlInvocazione.endsWith("?")==false)
//					urlInvocazione =urlInvocazione  + "&";
//				String nome = (String) en.nextElement();
//				urlInvocazione = urlInvocazione + nome + "=" + this.parametersFormBased.get(nome);
//			}
//		}
		return TransportUtils.buildLocationWithURLBasedParameter(this.parametersFormBased, this.requestURI);
	}

	public void setParametersFormBased(java.util.Properties parametersFormBased) {
		this.parametersFormBased = parametersFormBased;
	}

	public void setParametersTrasporto(java.util.Properties parametersTrasporto) {
		this.parametersTrasporto = parametersTrasporto;
	}

	public void setWebContext(String webContext) {
		this.webContext = webContext;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public void setFunctionParameters(String functionParameters) {
		this.functionParameters = functionParameters;
	}
}
