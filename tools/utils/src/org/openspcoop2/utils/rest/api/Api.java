/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
import org.openspcoop2.utils.rest.BaseSpecConfig;
import org.openspcoop2.utils.rest.BaseSpecValidator;
import org.openspcoop2.utils.rest.ParseWarningException;
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

	
	private Map<String, Serializable> mapSerializableVendorImpl = new HashMap<>();
	
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
	
	public void validate() throws ProcessingException,ParseWarningException {
		this.validate(false);
	}
	public void validate(boolean validateBodyParameterElement) throws ProcessingException, ParseWarningException {
		if(validateBodyParameterElement) {
			// nop
		}
		this.validate(false, false);
	}
	public void validate(boolean usingFromSetProtocolInfo, boolean validateBodyParameterElement) throws ProcessingException, ParseWarningException {
		BaseSpecConfig config = new BaseSpecConfig();
		config.setUsingFromSetProtocolInfo(usingFromSetProtocolInfo);
		config.setValidateBodyParameterElement(validateBodyParameterElement);
		
		BaseSpecValidator validator = new BaseSpecValidator();
		validator.init(null, config);
		try {
			validator.validate(null, this);
		} catch (Exception e) {
			validator.close(null);
			throw e;
		}
	}
}
