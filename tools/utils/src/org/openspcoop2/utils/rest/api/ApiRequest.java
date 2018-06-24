/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

package org.openspcoop2.utils.rest.api;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.beans.BaseBean;

/**
 * ApiRequest
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiRequest extends BaseBean {
	
	private List<ApiCookieParameter> cookieParameters = new ArrayList<>();
	private List<ApiHeaderParameter> headerParameters = new ArrayList<>();
	private List<ApiRequestQueryParameter> queryParameters = new ArrayList<>();
	private List<ApiRequestDynamicPathParameter> dynamicPathParameters = new ArrayList<>();
	private List<ApiRequestFormParameter> formParameters = new ArrayList<>();
	
	private List<ApiBodyParameter> bodyParameters = new ArrayList<>();
		
	
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
	
	
	public void addQueryParameter(ApiRequestQueryParameter parameter) {
		this.queryParameters.add(parameter);
	}

	public ApiRequestQueryParameter getQueryParameter(int index) {
		return this.queryParameters.get( index );
	}

	public ApiRequestQueryParameter removeQueryParameter(int index) {
		return this.queryParameters.remove( index );
	}

	public List<ApiRequestQueryParameter> getQueryParameters() {
		return this.queryParameters;
	}

	public void setQueryParameters(List<ApiRequestQueryParameter> parameters) {
		this.queryParameters=parameters;
	}

	public int sizeQueryParameters() {
		return this.queryParameters.size();
	}
	
	
	
	public void addDynamicPathParameter(ApiRequestDynamicPathParameter parameter) {
		this.dynamicPathParameters.add(parameter);
	}

	public ApiRequestDynamicPathParameter getDynamicPathParameter(int index) {
		return this.dynamicPathParameters.get( index );
	}

	public ApiRequestDynamicPathParameter removeDynamicPathParameter(int index) {
		return this.dynamicPathParameters.remove( index );
	}

	public List<ApiRequestDynamicPathParameter> getDynamicPathParameters() {
		return this.dynamicPathParameters;
	}

	public void setDynamicPathParameters(List<ApiRequestDynamicPathParameter> parameters) {
		this.dynamicPathParameters=parameters;
	}

	public int sizeDynamicPathParameters() {
		return this.dynamicPathParameters.size();
	}

	
	public void addFormParameter(ApiRequestFormParameter parameter) {
		this.formParameters.add(parameter);
	}

	public ApiRequestFormParameter getFormParameter(int index) {
		return this.formParameters.get( index );
	}

	public ApiRequestFormParameter removeFormParameter(int index) {
		return this.formParameters.remove( index );
	}

	public List<ApiRequestFormParameter> getFormParameters() {
		return this.formParameters;
	}

	public void setFormParameters(List<ApiRequestFormParameter> parameters) {
		this.formParameters=parameters;
	}

	public int sizeFormParameters() {
		return this.formParameters.size();
	}
	
	
	public void addBodyParameter(ApiBodyParameter parameter) {
		this.bodyParameters.add(parameter);
	}

	public ApiBodyParameter getBodyParameter(int index) {
		return this.bodyParameters.get( index );
	}

	public ApiBodyParameter removeBodyParameter(int index) {
		return this.bodyParameters.remove( index );
	}

	public List<ApiBodyParameter> getBodyParameters() {
		return this.bodyParameters;
	}

	public void setBodyParameters(List<ApiBodyParameter> parameters) {
		this.bodyParameters=parameters;
	}

	public int sizeBodyParameters() {
		return this.bodyParameters.size();
	}
}
