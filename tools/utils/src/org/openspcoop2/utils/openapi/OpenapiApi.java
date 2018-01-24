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


package org.openspcoop2.utils.openapi;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.rest.api.Api;

import io.swagger.v3.oas.models.OpenAPI;


/**
 * SwaggerApi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13372 $, $Date: 2017-10-24 16:06:36 +0200(mar, 24 ott 2017) $
 *
 */
public class OpenapiApi extends Api {
	private OpenAPI api;
	private Map<String, io.swagger.v3.oas.models.media.Schema<?>> definitions;


	public OpenapiApi(OpenAPI swagger) {
		this.api = swagger;
		this.definitions = new HashMap<String, io.swagger.v3.oas.models.media.Schema<?>>();
	}
	
	public OpenAPI getApi() {
		return this.api;
	}

	public void setApi(OpenAPI swagger) {
		this.api = swagger;
	}

	public Map<String, io.swagger.v3.oas.models.media.Schema<?>> getDefinitions() {
		return this.definitions;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, io.swagger.v3.oas.models.media.Schema> getAllDefinitions() {
		Map<String, io.swagger.v3.oas.models.media.Schema> map = new HashMap<>();
		map.putAll(this.getDefinitions());
		if(this.api.getComponents() != null && this.api.getComponents().getSchemas() != null)
			map.putAll(this.api.getComponents().getSchemas());
		return map;
	}

	public void setDefinitions(Map<String, io.swagger.v3.oas.models.media.Schema<?>> definitions) {
		this.definitions = definitions;
	}

}
