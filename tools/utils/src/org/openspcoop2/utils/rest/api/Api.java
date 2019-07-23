/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public abstract class Api extends BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private URL baseURL;
	
	private List<ApiSchema> schemas = new ArrayList<>();

	
	private Map<String, Serializable> mapSerializableVendorImpl = new HashMap<String, Serializable>();
	
	public Serializable getVendorImpl(String key) {
		return this.mapSerializableVendorImpl.get(key);
	}
	public void removeVendorImpl(String key) {
		this.mapSerializableVendorImpl.remove(key);
	}
	public void addVendorImpl(String key, Serializable serializable) {
		this.mapSerializableVendorImpl.put(key, serializable);
	}
	public boolean containsKey(String key) {
		return this.mapSerializableVendorImpl.containsKey(key);
	}
	
	
	public void addSchema(ApiSchema schema) {
		this.schemas.add(schema);
	}

	public ApiSchema getSchema(int index) {
		return this.schemas.get( index );
	}

	public ApiSchema removeSchema(int index) {
		return this.schemas.remove( index );
	}

	public List<ApiSchema> getSchemas() {
		return this.schemas;
	}

	public void setSchemas(List<ApiSchema> schemas) {
		this.schemas=schemas;
	}

	public int sizeSchemas() {
		return this.schemas.size();
	}
	
	
	
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
	
	public void validate() throws ProcessingException {
		this.validate(false);
	}
	public void validate(boolean validateBodyParameterElement) throws ProcessingException {
		
		for (int i = 0; i < this.operations.size(); i++) {
			
			ApiOperation op = this.operations.get(i);
			
			String prefix = "Operation["+i+"] ";
			
			if(op.getHttpMethod()==null) {
				throw new ProcessingException("HttpMethod is null");
			}
			if(op.getPath()==null) {
				throw new ProcessingException("Path is null");
			}
			prefix = "Operation["+op.getHttpMethod().name()+" "+op.getPath()+"] ";
			
			if(op.getRequest()!=null) {
				String pRequest = prefix +"[request] ";
				
				for (int j = 0; j < op.getRequest().sizeBodyParameters(); j++) {
					ApiBodyParameter bodyParm = op.getRequest().getBodyParameter(j);
					if(bodyParm.getMediaType()==null) {
						throw new ProcessingException(pRequest+"MediaType undefined in body parameter (position '"+j+"')");
					}
					if(validateBodyParameterElement) {
						if(bodyParm.getElement()==null) {
							throw new ProcessingException(pRequest+"Element undefined in body parameter '"+bodyParm.getMediaType()+"'");
						}
					}
				}
				
				for (int j = 0; j < op.getRequest().sizeCookieParameters(); j++) {
					ApiCookieParameter par = op.getRequest().getCookieParameter(j);
					if(par.getName()==null) {
						throw new ProcessingException(pRequest+"Name undefined in cookie parameter (position '"+j+"')");
					}
					if(par.getType()==null) {
						throw new ProcessingException(pRequest+"Type undefined in cookie parameter '"+par.getName()+"'");
					}
				}
				
				for (int j = 0; j < op.getRequest().sizeDynamicPathParameters(); j++) {
					ApiRequestDynamicPathParameter par = op.getRequest().getDynamicPathParameter(j);
					if(par.getName()==null) {
						throw new ProcessingException(pRequest+"Name undefined in dynamic path parameter (position '"+j+"')");
					}
					if(par.getType()==null) {
						throw new ProcessingException(pRequest+"Type undefined in dynamic path parameter '"+par.getName()+"'");
					}
				}
				
				for (int j = 0; j < op.getRequest().sizeFormParameters(); j++) {
					ApiRequestFormParameter par = op.getRequest().getFormParameter(j);
					if(par.getName()==null) {
						throw new ProcessingException(pRequest+"Name undefined in form parameter (position '"+j+"')");
					}
					if(par.getType()==null) {
						throw new ProcessingException(pRequest+"Type undefined in form parameter '"+par.getName()+"'");
					}
				}
				
				for (int j = 0; j < op.getRequest().sizeHeaderParameters(); j++) {
					ApiHeaderParameter par = op.getRequest().getHeaderParameter(j);
					if(par.getName()==null) {
						throw new ProcessingException(pRequest+"Name undefined in header parameter (position '"+j+"')");
					}
					if(par.getType()==null) {
						throw new ProcessingException(pRequest+"Type undefined in header parameter '"+par.getName()+"'");
					}
				}
				
				for (int j = 0; j < op.getRequest().sizeQueryParameters(); j++) {
					ApiRequestQueryParameter par = op.getRequest().getQueryParameter(j);
					if(par.getName()==null) {
						throw new ProcessingException(pRequest+"Name undefined in query parameter (position '"+j+"')");
					}
					if(par.getType()==null) {
						throw new ProcessingException(pRequest+"Type undefined in query parameter '"+par.getName()+"'");
					}
				}
			}
			
			boolean defaultResponse = false;
			
			for (int k = 0; k < op.sizeResponses(); k++) {
		
				String pResponse = prefix +"[response '"+k+"'] ";
				
				ApiResponse response = op.getResponse(k);
				
				if(response.isDefaultHttpReturnCode()) {
					if(defaultResponse) {
						throw new ProcessingException(pResponse+"Http Return Code Default already defined");
					}
					else {
						defaultResponse = true;
					}
				}
				if(response.getHttpReturnCode()<=0 && !response.isDefaultHttpReturnCode()) {
					throw new ProcessingException(pResponse+"Http Return Code undefined");
				}
		
				if(response.isDefaultHttpReturnCode()) {
					pResponse = prefix +"[response status 'default'] ";
				}
				else {
					pResponse = prefix +"[response status '"+response.getHttpReturnCode()+"'] ";
				}
				
				for (int j = 0; j < response.sizeBodyParameters(); j++) {
					ApiBodyParameter bodyParm = response.getBodyParameter(j);
					if(bodyParm.getMediaType()==null) {
						throw new ProcessingException(pResponse+"MediaType undefined in body parameter (position '"+j+"')");
					}
					if(validateBodyParameterElement) {
						if(bodyParm.getElement()==null) {
							throw new ProcessingException(pResponse+"Element undefined in body parameter '"+bodyParm.getMediaType()+"'");
						}
					}
				}
				
				for (int j = 0; j < response.sizeCookieParameters(); j++) {
					ApiCookieParameter par = response.getCookieParameter(j);
					if(par.getName()==null) {
						throw new ProcessingException(pResponse+"Name undefined in cookie parameter (position '"+j+"')");
					}
					if(par.getType()==null) {
						throw new ProcessingException(pResponse+"Type undefined in cookie parameter '"+par.getName()+"'");
					}
				}
				
				for (int j = 0; j < response.sizeHeaderParameters(); j++) {
					ApiHeaderParameter par = response.getHeaderParameter(j);
					if(par.getName()==null) {
						throw new ProcessingException(pResponse+"Name undefined in header parameter (position '"+j+"')");
					}
					if(par.getType()==null) {
						throw new ProcessingException(pResponse+"Type undefined in header parameter '"+par.getName()+"'");
					}
				}
			}
		}
	}
}
