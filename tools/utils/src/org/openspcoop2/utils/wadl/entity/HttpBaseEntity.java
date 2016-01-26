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

package org.openspcoop2.utils.wadl.entity;

/**
 * HttpBaseEntity
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class HttpBaseEntity<T> {

	private String url;
	private org.jvnet.ws.wadl.HTTPMethods method;
	private String contentType;
	/* ---- Coppie nome/valori di invocazione form-based --- */
	private java.util.Properties parametersFormBased;
	/* ---- Coppie nome/valori di invocazione inserite nell'header del trasporto --- */
	private java.util.Properties parametersTrasporto;
	
	private T content;
	
	public T getContent() {
		return this.content;
	}
	public void setContent(T content) {
		this.content = content;
	}
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public org.jvnet.ws.wadl.HTTPMethods getMethod() {
		return this.method;
	}
	public void setMethod(org.jvnet.ws.wadl.HTTPMethods method) {
		this.method = method;
	}
	public String getContentType() {
		return this.contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public java.util.Properties getParametersFormBased() {
		return this.parametersFormBased;
	}
	public void setParametersFormBased(java.util.Properties parametersFormBased) {
		this.parametersFormBased = parametersFormBased;
	}
	public java.util.Properties getParametersTrasporto() {
		return this.parametersTrasporto;
	}
	public void setParametersTrasporto(java.util.Properties parametersTrasporto) {
		this.parametersTrasporto = parametersTrasporto;
	}
}
