/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openapi4j.core.model.v3.OAI3Context;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.v3.OpenApi3Validator;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.json.JsonPathNotFoundException;
import org.openspcoop2.utils.json.YAMLUtils;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ParseWarningException;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.api.Api;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

/**
 * SwaggerApi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class OpenapiApi extends Api {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private transient OpenAPI api;
	private transient Map<String, Schema<?>> definitions;
	private String apiRaw;
	private String parseWarningResult; 
	private ApiFormats format;

	// struttura una volta che l'api è stata inizializzata per la validazione (e' serializzabile e cachabile)
	private OpenapiApiValidatorStructure validationStructure;

	public OpenapiApi(ApiFormats format, OpenAPI swagger, String apiRaw, String parseWarningResult) {
		this.format = format;
		this.api = swagger;
		this.apiRaw = apiRaw;
		this.parseWarningResult = parseWarningResult;
		this.definitions = new HashMap<String, Schema<?>>();
	}
	
	public ApiFormats getFormat() {
		return this.format;
	}
	
	public OpenAPI getApi() {
		return this.api;
	}

	public String getApiRaw() {
		return this.apiRaw;
	}

	public String getParseWarningResult() {
		return this.parseWarningResult;
	}
	
	public Map<String, Schema<?>> getDefinitions() {
		return this.definitions;
	}

	public Map<String, Schema<?>> getAllDefinitions() {
		Map<String, Schema<?>> map = new HashMap<>();
		map.putAll(this.getDefinitions());
		if(this.api.getComponents() != null && this.api.getComponents().getSchemas() != null)
			for(String k: this.api.getComponents().getSchemas().keySet()) {
				map.put(k, this.api.getComponents().getSchemas().get(k));
			}
		return map;
	}

	public void setDefinitions(Map<String, Schema<?>> definitions) {
		this.definitions = definitions;
	}

	public OpenapiApiValidatorStructure getValidationStructure() {
		return this.validationStructure;
	}

	public void setValidationStructure(OpenapiApiValidatorStructure validationStructure) {
		this.validationStructure = validationStructure;
	}
	
	@Override
	public void validate() throws ProcessingException, ParseWarningException {
		this.validate(false);
	}
	@Override
	public void validate(boolean validateBodyParameterElement) throws ProcessingException, ParseWarningException {
		
		// Primo valido l'openapi
		if(ApiFormats.OPEN_API_3.equals(this.format)) {
			this._validateOpenAPI3();
		}
		
		// Valido la struttura
		super.validate(validateBodyParameterElement);
		
		// Se ho trovato dei warning li emetto
		if(this.parseWarningResult!=null && StringUtils.isNotEmpty(this.parseWarningResult)) {
			throw new ParseWarningException("\n"+this.parseWarningResult);
		}
	}
	
	private void _validateOpenAPI3() throws ProcessingException {
		
		try {
		
			YAMLUtils yamlUtils = YAMLUtils.getInstance();
			JSONUtils jsonUtils = JSONUtils.getInstance();
									
			boolean apiRawIsYaml = yamlUtils.isYaml(this.apiRaw);
			JsonNode schemaNodeRoot = null;
			if(apiRawIsYaml) {
				schemaNodeRoot = yamlUtils.getAsNode(this.apiRaw);
			}
			else {
				schemaNodeRoot = jsonUtils.getAsNode(this.apiRaw);
			}
		
			JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
			List<String> refPath = null;
			try {
				refPath = engine.getStringMatchPattern(schemaNodeRoot, "$..$ref");
			}catch(JsonPathNotFoundException notFound){
				//System.out.println("NOT FOUND: "+notFound.getMessage());
			}
			boolean refNonRisolvibili = false;
			if(refPath!=null && !refPath.isEmpty()) {
				for (String refP : refPath) {
					if(refP!=null) {
						String ref = refP.trim(); 
						if(ref.startsWith("#")) {
							continue; // relativo verso il file stesso
						}
						else {
							refNonRisolvibili = true; // ref verso altri file (emettero solo il warning se c'è)
							break;
						}
					}
				}
			}
			if(!refNonRisolvibili) {
				// Costruisco OpenAPI3					
				OAI3Context context = null;
				OpenApi3 openApi4j = null;
				try {
					context = new OAI3Context(new URL("file:/"), schemaNodeRoot, null);
					openApi4j = TreeUtil.json.convertValue(context.getBaseDocument(), OpenApi3.class);
					openApi4j.setContext(context);
				}catch(Throwable e) {
					throw new ProcessingException(e.getMessage(), e);
				}
				try {
					ValidationResults results = OpenApi3Validator.instance().validate(openApi4j);
					if(!results.isValid()) {
						throw new ProcessingException(results.toString());
					}
				}catch(org.openapi4j.core.validation.ValidationException valExc) {
					if(valExc.results()!=null) {
						throw new ProcessingException(valExc.results().toString());
					}
					else {
						throw new ProcessingException(valExc.getMessage());
					}
				}catch(Throwable e) {
					throw new ProcessingException(e.getMessage(), e);
				}
			}
		}
		catch(ProcessingException pe) {
			if(this.parseWarningResult!=null && StringUtils.isNotEmpty(this.parseWarningResult)) {
				throw new ProcessingException("\n"+this.parseWarningResult+"\n"+pe.getMessage());
			}
			else {
				throw new ProcessingException(pe.getMessage());
			}
		}
		catch(Exception e) {
			throw new ProcessingException(e.getMessage(),e);
		}
	}
}
