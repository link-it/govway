/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.utils.transport.http.HttpRequestMethod;

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
	private HttpRequestMethod method;
	private String contentType;

	/* ---- Coppie nome/valori di invocazione inserite nell'header del trasporto --- */
	private java.util.Properties parametersTrasporto = new Properties();
	/* ---- Coppie nome/valori di invocazione form ----- */
	private java.util.Properties parametersForm = new Properties();
	/* ---- Cookies ----- */
	private List<Cookie> cookies = new ArrayList<>();
	
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
	public java.util.Properties getParametersForm() {
		return this.parametersForm;
	}
	public void setParametersForm(java.util.Properties parametersForm) {
		this.parametersForm = parametersForm;
	}
	public java.util.Properties getParametersTrasporto() {
		return this.parametersTrasporto;
	}
	public void setParametersTrasporto(java.util.Properties parametersTrasporto) {
		this.parametersTrasporto = parametersTrasporto;
	}
	public List<Cookie> getCookies() {
		return this.cookies;
	}
	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}
}
