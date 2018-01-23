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


package org.openspcoop2.utils.swagger;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.rest.api.Api;

import v2.io.swagger.models.Model;
import v2.io.swagger.models.Swagger;


/**
 * SwaggerApi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SwaggerApi extends Api {
	private Swagger swagger;
	private Map<String, Model> definitions;


	public SwaggerApi(Swagger swagger) {
		this.swagger = swagger;
		this.definitions = new HashMap<String, Model>();
	}
	
	public Swagger getSwagger() {
		return this.swagger;
	}

	public void setSwagger(Swagger swagger) {
		this.swagger = swagger;
	}

	public Map<String, Model> getDefinitions() {
		return this.definitions;
	}

	public void setDefinitions(Map<String, Model> definitions) {
		this.definitions = definitions;
	}
	
	public Map<String, Model> getAllDefinitions() {
		Map<String, Model>  defs = new HashMap<String, Model>();
		defs.putAll(this.swagger.getDefinitions());
		defs.putAll(this.getDefinitions());
		return defs;
	}

}
