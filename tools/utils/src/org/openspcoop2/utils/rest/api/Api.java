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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.beans.BaseBean;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * ApiOperation
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class Api extends BaseBean {

	private String name;
	private String description;
	private URL baseURL;
	
	private List<ApiOperation> operations = new ArrayList<>();

	public void addOperation(ApiOperation operation) {
		this.operations.add(operation);
	}

	public ApiOperation getOperation(int index) {
		return this.operations.get( index );
	}

	public ApiOperation removeOperation(int index) {
		return this.operations.remove( index );
	}

	public List<ApiOperation> getOperations() {
		return this.operations;
	}

	public void setOperations(List<ApiOperation> operations) {
		this.operations=operations;
	}

	public int sizeOperations() {
		return this.operations.size();
	}
	
	public ApiOperation findOperation(HttpRequestMethod httpMethod,String url) throws ProcessingException{
		return ApiUtilities.findOperation(this, httpMethod, url, true);
	}
	
	
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

	public URL getBaseURL() {
		return this.baseURL;
	}

	public void setBaseURL(URL baseURL) {
		this.baseURL = baseURL;
	}
}
