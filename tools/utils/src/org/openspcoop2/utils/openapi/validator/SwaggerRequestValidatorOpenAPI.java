/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.openapi.validator;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.openapi.validator.swagger.SwaggerValidatorUtils;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.models.Swagger;
import io.swagger.parser.Swagger20Parser;
import io.swagger.parser.util.SwaggerDeserializationResult;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIResolver;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.converter.SwaggerConverter;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import io.swagger.v3.parser.util.ResolverFully;

/**
 * SwaggerRequestValidatorAPI
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SwaggerRequestValidatorOpenAPI implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private JsonNode schemaNodeRoot;
	private OpenapiLibraryValidatorConfig openApi4jConfig;
	private Api api;
	
	public SwaggerRequestValidatorOpenAPI(JsonNode schemaNodeRoot, OpenapiLibraryValidatorConfig openApi4jConfig, Api api) {
		this.schemaNodeRoot = schemaNodeRoot;
		this.openApi4jConfig = openApi4jConfig;
		this.api = api;
	}
	
	private transient OpenAPI openApiSwagger;
	
	public OpenAPI getOpenApiSwagger() throws ProcessingException, IOException {
		if(this.openApiSwagger==null) {
			init();
		}
		if(this.openApiSwagger==null) {
			throw new ProcessingException("OpenAPI init failed");
		}
		return this.openApiSwagger;
	}

	private transient org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("SwaggerRequestValidatorOpenAPI");
	public void init() throws ProcessingException, IOException {
		if(this.openApiSwagger!=null) {
			return;
		}
		this.semaphore.acquireThrowRuntime("init");
		try {
			// Parsing
			SwaggerParseResult result;
			String version = SwaggerValidatorUtils.getSchemaVersion(this.schemaNodeRoot);
			if (SwaggerValidatorUtils.isSchemaV2(version)) {
				Swagger20Parser swaggerParser = new Swagger20Parser();
				SwaggerConverter swaggerConverter = new SwaggerConverter();
				Swagger swagger = swaggerParser.read(this.schemaNodeRoot);
				
				if (swagger != null) {
					SwaggerDeserializationResult foo = new SwaggerDeserializationResult();
					foo.setMessages(Arrays.asList());
					foo.setSwagger(swagger);
					result = swaggerConverter.convert(foo);
				} else {
					throw new ProcessingException("Unknown error while parsing the Swagger root node.");								
				}
				
			} else {		
				OpenAPIV3Parser v3Parser = new OpenAPIV3Parser();
				result = v3Parser.parseJsonNode(null, this.schemaNodeRoot);
			}
			if (result.getOpenAPI() == null) {
				throw new ProcessingException("Error while parsing the OpenAPI root node: " + String.join("\n", result.getMessages()));
			}
			
			// Se l'api non è stata mergiata, recuperiamo gli schemi dei riferimenti esterni
			
			Map<String,String> schemaMapSerialized = new HashMap<String,String>();
			if (!this.openApi4jConfig.isMergeAPISpec() && this.api.getSchemas()!=null) {							
				for (ApiSchema apiSchema : this.api.getSchemas()) {
					if(ApiSchemaType.JSON.equals(apiSchema.getType()) || ApiSchemaType.YAML.equals(apiSchema.getType())) {
						String schemaBytes = new String(apiSchema.getContent());
						schemaMapSerialized.put(apiSchema.getName(), schemaBytes);
						schemaMapSerialized.put("./"+apiSchema.getName(), schemaBytes);
					}
				}
			}
			OpenAPIResolver v3Resolver = new OpenAPIResolver(result.getOpenAPI(), schemaMapSerialized, null, 0);
			result.setOpenAPI(v3Resolver.resolve());
			if (!this.openApi4jConfig.isSwaggerRequestValidator_ResolveFullyApiSpec()) {
				
				// Passo false per non risolvere i combinators, poichè quando vengono risolti non c'è modo
				// di ricordarsi i singoli attributi degli schemi combinati (oneOf, allOf ecc..)
				ResolverFully v3ResolverFully = new ResolverFully(false);
				v3ResolverFully.resolveFully(result.getOpenAPI());
			}
	
	        // Pulisco la openApi con le utility dell' OpenApiLoader di atlassian
			Validator.removeRegexPatternOnStringsOfFormatByte(result.getOpenAPI());
			Validator.removeTypeObjectAssociationWithOneOfAndAnyOfModels(result.getOpenAPI());
		    
			// Validazione semantica se richiesta
	
			if(this.openApi4jConfig.isValidateAPISpec()) {
				if (result.getMessages().size() != 0) {
					throw new ProcessingException(
							"OpenAPI3 not valid: " + String.join("\n", result.getMessages())
							);
				}
			}
			
			this.openApiSwagger = result.getOpenAPI();
			
		}finally {
			this.semaphore.release("init");
		}
	}
	
}
