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

package org.openspcoop2.utils.rest.api;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.beans.BaseBean;

/**
 * ApiResponse
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiResponse extends BaseBean {

	private String name;
	private String description;
	private int httpReturnCode;
	private String mediaType;
	private Object element;

	private List<ApiCookieParameter> cookieParameters = new ArrayList<>();
	private List<ApiHeaderParameter> headerParameters = new ArrayList<>();
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getHttpReturnCode() {
		return this.httpReturnCode;
	}
	public void setHttpReturnCode(int httpReturnCode) {
		this.httpReturnCode = httpReturnCode;
	}
	public String getMediaType() {
		return this.mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public Object getElement() {
		return this.element;
	}
	public void setElement(Object element) {
		this.element = element;
	}
	
	public void addCookieParameter(ApiCookieParameter parameter) {
		this.cookieParameters.add(parameter);
	}

	public ApiCookieParameter getCookieParameter(int index) {
		return this.cookieParameters.get( index );
	}

	public ApiCookieParameter removeCookieParameter(int index) {
		return this.cookieParameters.remove( index );
	}

	public List<ApiCookieParameter> getCookieParameters() {
		return this.cookieParameters;
	}

	public void setCookieParameters(List<ApiCookieParameter> parameters) {
		this.cookieParameters=parameters;
	}

	public int sizeCookieParameters() {
		return this.cookieParameters.size();
	}
	
	
	public void addHeaderParameter(ApiHeaderParameter parameter) {
		this.headerParameters.add(parameter);
	}

	public ApiHeaderParameter getHeaderParameter(int index) {
		return this.headerParameters.get( index );
	}

	public ApiHeaderParameter removeHeaderParameter(int index) {
		return this.headerParameters.remove( index );
	}

	public List<ApiHeaderParameter> getHeaderParameters() {
		return this.headerParameters;
	}

	public void setHeaderParameters(List<ApiHeaderParameter> parameters) {
		this.headerParameters=parameters;
	}

	public int sizeHeaderParameters() {
		return this.headerParameters.size();
	}
}
