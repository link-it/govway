/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.rest.entity;

import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * HttpBaseEntity
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12566 $, $Date: 2017-01-11 15:21:56 +0100 (Wed, 11 Jan 2017) $
 */
public abstract class HttpBaseEntity<T> {

	private String url;
	private HttpRequestMethod method;
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
	public HttpRequestMethod getMethod() {
		return this.method;
	}
	public void setMethod(HttpRequestMethod method) {
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
