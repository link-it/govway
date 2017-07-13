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
 * ApiRequest
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12564 $, $Date: 2017-01-11 14:31:31 +0100 (Wed, 11 Jan 2017) $
 */
public class ApiRequest extends BaseBean {

	private List<ApiRequestHeaderParameter> headerParameters = new ArrayList<>();
	private List<ApiRequestQueryParameter> queryParameters = new ArrayList<>();
	private List<ApiRequestDynamicPathParameter> dynamicPathParameters = new ArrayList<>();
	private List<ApiRequestBodyParameter> bodyParameters = new ArrayList<>();
		
	
	public void addHeaderParameter(ApiRequestHeaderParameter parameter) {
		this.headerParameters.add(parameter);
	}

	public ApiRequestHeaderParameter getHeaderParameter(int index) {
		return this.headerParameters.get( index );
	}

	public ApiRequestHeaderParameter removeHeaderParameter(int index) {
		return this.headerParameters.remove( index );
	}

	public List<ApiRequestHeaderParameter> getHeaderParameters() {
		return this.headerParameters;
	}

	public void setHeaderParameters(List<ApiRequestHeaderParameter> parameters) {
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

	
	
	
	public void addBodyParameter(ApiRequestBodyParameter parameter) {
		this.bodyParameters.add(parameter);
	}

	public ApiRequestBodyParameter getBodyParameter(int index) {
		return this.bodyParameters.get( index );
	}

	public ApiRequestBodyParameter removeBodyParameter(int index) {
		return this.bodyParameters.remove( index );
	}

	public List<ApiRequestBodyParameter> getBodyParameters() {
		return this.bodyParameters;
	}

	public void setBodyParameters(List<ApiRequestBodyParameter> parameters) {
		this.bodyParameters=parameters;
	}

	public int sizeBodyParameters() {
		return this.bodyParameters.size();
	}
}
