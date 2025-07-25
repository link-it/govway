/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openapi4j.core.model.v3.OAI3Context;
import org.openapi4j.core.model.v3.OAI3SchemaKeywords;
import org.openapi4j.core.util.StringUtil;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.Response;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.model.impl.DefaultResponse;
import org.openapi4j.operation.validator.validation.OperationValidator;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;
import org.openapi4j.parser.validation.v3.OpenApi3Validator;
import org.openapi4j.schema.validator.ValidationData;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.json.AbstractUtils;
import org.openspcoop2.utils.json.IJsonSchemaValidator;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.JsonPathException;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.json.JsonPathNotFoundException;
import org.openspcoop2.utils.json.JsonPathNotValidException;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig.ADDITIONAL;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig.POLITICA_INCLUSIONE_TIPI;
import org.openspcoop2.utils.json.JsonValidatorAPI.ApiName;
import org.openspcoop2.utils.json.ValidationException;
import org.openspcoop2.utils.json.ValidationResponse;
import org.openspcoop2.utils.json.ValidationResponse.ESITO;
import org.openspcoop2.utils.json.ValidatorFactory;
import org.openspcoop2.utils.json.YAMLUtils;
import org.openspcoop2.utils.openapi.OpenapiApi;
import org.openspcoop2.utils.openapi.OpenapiApiValidatorStructure;
import org.openspcoop2.utils.openapi.UniqueInterfaceGenerator;
import org.openspcoop2.utils.openapi.UniqueInterfaceGeneratorConfig;
import org.openspcoop2.utils.openapi.validator.swagger.SwaggerOpenApiValidator;
import org.openspcoop2.utils.openapi.validator.swagger.SwaggerRequestValidator;
import org.openspcoop2.utils.openapi.validator.swagger.SwaggerResponseValidator;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.rest.AbstractApiValidator;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiParameterType;
import org.openspcoop2.utils.rest.ApiValidatorConfig;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.ValidatorException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiBodyParameter;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.api.ApiReference;
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;
import org.openspcoop2.utils.rest.api.ApiSchemaTypeRestriction;
import org.openspcoop2.utils.rest.entity.Cookie;
import org.openspcoop2.utils.rest.entity.HttpBaseEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseRequestEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseResponseEntity;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.atlassian.oai.validator.model.ApiPath;
import com.atlassian.oai.validator.model.ApiPathImpl;
import com.atlassian.oai.validator.model.NormalisedPath;
import com.atlassian.oai.validator.model.NormalisedPathImpl;
import com.atlassian.oai.validator.model.Request.Method;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.model.SimpleResponse;
import com.atlassian.oai.validator.report.SimpleValidationReportFormat;
import com.atlassian.oai.validator.report.ValidationReport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.networknt.schema.SpecVersion.VersionFlag;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.PathItem.HttpMethod;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;

/**
 * Validator
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Validator extends AbstractApiValidator implements IApiValidator {

	private Api api;
	// JSONSchema Validation
	private Map<String, IJsonSchemaValidator> validatorMap;
	private Map<String, File> fileSchema;
	// OpenAPI4j Validation
	private OpenApi3 openApi4j;
	
	// SwaggerRequestValidator
	private SwaggerRequestValidator swaggerRequestValidator;
	private SwaggerResponseValidator swaggerResponseValidator;
	private OpenAPI openApiSwagger;

	// Configuration
	private OpenapiLibraryValidatorConfig openApi4jConfig;
	
	boolean onlySchemas = false;
	
	private Logger log;
	
	private static final String VALIDATION_STRUCTURE = "VALIDATION_STRUCTURE";
	private static final String VALIDATION_SWAGGER_REQUEST_VALIDATOR_OPENAPI = "VALIDATION_SWAGGER_REQUEST_VALIDATOR_OPENAPI";
	private org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("OpenAPIValidator");
	
	@Override
	public void init(Logger log, Api api, ApiValidatorConfig config)
			throws ProcessingException {
		
		this.log = log;
		if(api == null)
			throw new ProcessingException("Api cannot be null");

		// la sincronizzazione sull'API serve per evitare che venga inizializzati più volte in maniera concorrente l'API
		//synchronized (api) {
		SemaphoreLock lock = this.semaphore.acquireThrowRuntime("init");
		try {
					
			this.api = api;
			Api apiRest = null;
			OpenapiApi openapiApi = null;
			OpenapiApiValidatorStructure apiValidatorStructure = null;
			SwaggerRequestValidatorOpenAPI swaggerRequestValidatorOpenAPI = null;
			if((api instanceof OpenapiApi)) {
				openapiApi = (OpenapiApi) this.api;
				apiRest = this.api;
				apiValidatorStructure = openapiApi.getValidationStructure();
				if(apiRest.containsKey(VALIDATION_SWAGGER_REQUEST_VALIDATOR_OPENAPI)) {
					swaggerRequestValidatorOpenAPI = (SwaggerRequestValidatorOpenAPI) apiRest.getVendorImpl(VALIDATION_SWAGGER_REQUEST_VALIDATOR_OPENAPI);
				}
			}
			else {
				apiRest = this.api;
				if(apiRest.containsKey(VALIDATION_STRUCTURE)) {
					apiValidatorStructure = (OpenapiApiValidatorStructure) apiRest.getVendorImpl(VALIDATION_STRUCTURE);
				}
				this.onlySchemas = true;
			}
			
			ApiName jsonValidatorAPI = null;
			OpenAPILibrary openApiLibrary = null; // ottimizzazione per OpenAPI
			ADDITIONAL policyAdditionalProperties = config.getPolicyAdditionalProperties();
			if(config instanceof OpenapiApiValidatorConfig) {
				jsonValidatorAPI = ((OpenapiApiValidatorConfig)config).getJsonValidatorAPI();
				if(openapiApi!=null) {
					OpenapiApiValidatorConfig c = (OpenapiApiValidatorConfig) config;
					if(c.getOpenApiValidatorConfig()!=null) {
						openApiLibrary = c.getOpenApiValidatorConfig().getOpenApiLibrary();
						if(OpenAPILibrary.openapi4j.equals(openApiLibrary) || OpenAPILibrary.swagger_request_validator.equals(openApiLibrary)) {
							this.openApi4jConfig = c.getOpenApiValidatorConfig();
						}
					}
				}
			} 
			if(jsonValidatorAPI==null) {
				jsonValidatorAPI = ApiName.NETWORK_NT;
			}

			try {
			
				if(OpenAPILibrary.openapi4j.equals(openApiLibrary) || 
						OpenAPILibrary.swagger_request_validator.equals(openApiLibrary)) {
					
					// leggo JSON Node degli schemi
					
					JsonNode schemaNodeRoot = null;
					URL uriSchemaNodeRoot = null;
					Map<URL, JsonNode> schemaMap = null;
					String root = "file:/";
					boolean validateSchema = true;
					if(apiValidatorStructure!=null && apiValidatorStructure.getNodeValidatorePrincipale()!=null && !apiValidatorStructure.getNodeValidatorePrincipale().isEmpty()) {
						validateSchema = false; // validazione dello schema effettuata quando viene costruito
						for (String nome : apiValidatorStructure.getNodeValidatorePrincipale().keySet()) {
							if(root.equals(nome)) {
								schemaNodeRoot = apiValidatorStructure.getNodeValidatorePrincipale().get(nome);
								uriSchemaNodeRoot = new URI(root).toURL();
							}
							else {
								if(schemaMap==null) {
									schemaMap = new HashMap<>();
								}
								schemaMap.put(new URI(nome).toURL(), apiValidatorStructure.getNodeValidatorePrincipale().get(nome));
							}
						}
					}
					else {
						
						YAMLUtils yamlUtils = YAMLUtils.getInstance();
						JSONUtils jsonUtils = JSONUtils.getInstance();
												
						String apiRaw = openapiApi.getApiRaw();
						boolean apiRawIsYaml = yamlUtils.isYaml(apiRaw);
						boolean readApiSchemas = true;
						if(this.openApi4jConfig.isMergeAPISpec()) {
							
							readApiSchemas = false;
							
							Map<String, String> attachments = new HashMap<>();
							if(api.getSchemas()!=null && api.getSchemas().size()>0) {

								for (ApiSchema apiSchema : api.getSchemas()) {
								
									if(!ApiSchemaType.JSON.equals(apiSchema.getType()) && !ApiSchemaType.YAML.equals(apiSchema.getType())) {
										continue;
									}
									byte [] schema = apiSchema.getContent();
									if(ApiSchemaType.JSON.equals(apiSchema.getType())) {
										if(jsonUtils.isJson(schema)) {
											attachments.put(apiSchema.getName(), new String(apiSchema.getContent()));
										}
									}
									else {
										if(yamlUtils.isYaml(schema)) {
											attachments.put(apiSchema.getName(), new String(apiSchema.getContent()));
										}
									}
									
								}
							}
							
							if(!attachments.isEmpty()) {							
								UniqueInterfaceGeneratorConfig configUniqueInterfaceGeneratorConfig = new UniqueInterfaceGeneratorConfig();
								configUniqueInterfaceGeneratorConfig.setFormat(ApiFormats.OPEN_API_3);
								configUniqueInterfaceGeneratorConfig.setYaml(apiRawIsYaml);
								configUniqueInterfaceGeneratorConfig.setMaster(apiRaw);
								configUniqueInterfaceGeneratorConfig.setAttachments(attachments);
								try {
									String apiMerged = UniqueInterfaceGenerator.generate(configUniqueInterfaceGeneratorConfig, null, null, true, log);
									if(apiMerged==null) {
										throw new Exception("empty ApiSpec");
									}
									apiRaw = apiMerged;
								}catch(Throwable t) {
									log.error("Merge API Spec failed: "+t.getMessage(),t);
									readApiSchemas = true; // torno al metodo tradizionale
								}
							}
						}
						
						if(apiRawIsYaml) {
							// Fix merge key '<<: *'
							if(YAMLUtils.containsKeyAnchor(apiRaw)) {
								// Risoluzione merge key '<<: *'
								String jsonRepresentation = YAMLUtils.resolveKeyAnchorAndConvertToJson(apiRaw);
								schemaNodeRoot = jsonUtils.getAsNode(jsonRepresentation);
							}
							else {
								schemaNodeRoot = yamlUtils.getAsNode(apiRaw);
							}
						}
						else {
							schemaNodeRoot = jsonUtils.getAsNode(apiRaw);
						}
						normalizeRefs(schemaNodeRoot);
						uriSchemaNodeRoot = new URI(root).toURL();
						
						if(readApiSchemas && api.getSchemas()!=null && !api.getSchemas().isEmpty()) {
							
							for (ApiSchema apiSchema : api.getSchemas()) {
								
								if(!ApiSchemaType.JSON.equals(apiSchema.getType()) && !ApiSchemaType.YAML.equals(apiSchema.getType())) {
									continue;
								}
								byte [] schema = apiSchema.getContent();
								JsonNode schemaNodeInternal = null;
								if(ApiSchemaType.JSON.equals(apiSchema.getType())) {
									if(jsonUtils.isJson(schema)) {
										schemaNodeInternal = jsonUtils.getAsNode(schema);
									}
								}
								else {
									if(yamlUtils.isYaml(schema)) {
										// Vedi fix descritto sopra
										String sSchema = new String(schema);
										// Fix merge key '<<: *'
										if(YAMLUtils.containsKeyAnchor(sSchema)) {
											// Risoluzione merge key '<<: *'
											String jsonRepresentation = YAMLUtils.resolveKeyAnchorAndConvertToJson(sSchema);
											schemaNodeInternal = jsonUtils.getAsNode(jsonRepresentation);
										}
										else {
											schemaNodeInternal = yamlUtils.getAsNode(schema);
										}
									}
								}
								if(schemaNodeInternal==null) {
									continue;
								}
								normalizeRefs(schemaNodeInternal);
								if(schemaMap==null) {
									schemaMap = new HashMap<>();
								}
								schemaMap.put(new URI(root+apiSchema.getName()).toURL(), schemaNodeInternal);
								
							}
							
						}
					}

					if(OpenAPILibrary.openapi4j.equals(openApiLibrary)) {
					
						// Costruisco OpenAPI3					
						OAI3Context context = new OAI3Context(uriSchemaNodeRoot, schemaNodeRoot, schemaMap);
						if(this.openApi4jConfig!=null) {
							context.setMultipartOptimization(this.openApi4jConfig.isValidateMultipartOptimization());
						}
						this.openApi4j = TreeUtil.json.convertValue(context.getBaseDocument(), OpenApi3.class);
						this.openApi4j.setContext(context);
						
						// Explicit validation of the API spec
						
						if(validateSchema && this.openApi4jConfig!=null && this.openApi4jConfig.isValidateAPISpec()) {
							try {
								ValidationResults results = OpenApi3Validator.instance().validate(this.openApi4j);
								if(!results.isValid()) {
									throw new ProcessingException("OpenAPI3 not valid: "+results.toString());
								}
							}catch(org.openapi4j.core.validation.ValidationException valExc) {
								if(valExc.results()!=null) {
									throw new ProcessingException("OpenAPI3 not valid: "+valExc.results().toString());
								}
								else {
									throw new ProcessingException("OpenAPI3 not valid: "+valExc.getMessage());
								}
							}
						}						
					} // fine openapi4j
					
					else if(OpenAPILibrary.swagger_request_validator.equals(openApiLibrary)) {
						// Validazione sintattica se richiesta
						
						if(validateSchema && this.openApi4jConfig.isValidateAPISpec()) {
							var validationResult = new SwaggerOpenApiValidator().validate(schemaNodeRoot);
							if (validationResult.isPresent()) {
								throw new ProcessingException(
										"OpenAPI3 not valid: " + validationResult.get()
										);								
							}					    
						}
						
						// Parsing
						if(swaggerRequestValidatorOpenAPI!=null) {
							this.openApiSwagger = swaggerRequestValidatorOpenAPI.getOpenApiSwagger();
						}
						else {
							SwaggerRequestValidatorOpenAPI newInstanceSwaggerRequestValidatorOpenAPI = new SwaggerRequestValidatorOpenAPI(schemaNodeRoot, this.openApi4jConfig, api);
							this.openApiSwagger = newInstanceSwaggerRequestValidatorOpenAPI.getOpenApiSwagger(); // init
							apiRest.addVendorImpl(VALIDATION_SWAGGER_REQUEST_VALIDATOR_OPENAPI, newInstanceSwaggerRequestValidatorOpenAPI);
						}
						
						this.swaggerRequestValidator = new SwaggerRequestValidator(this.openApiSwagger, this.openApi4jConfig);
				        this.swaggerResponseValidator = new SwaggerResponseValidator(this.openApiSwagger, this.openApi4jConfig);
					
					} // fine swagger_request_validator
					

					// Salvo informazioni ricostruite
					if(apiValidatorStructure==null) {
						OpenapiApiValidatorStructure validationStructure = new OpenapiApiValidatorStructure();
						Map<String, JsonNode> nodeValidatorePrincipale = new HashMap<String, JsonNode>();
						nodeValidatorePrincipale.put(root, schemaNodeRoot);
						if(schemaMap!=null && !schemaMap.isEmpty()) {
							for (URL url : schemaMap.keySet()) {
								nodeValidatorePrincipale.put(url.toString(), schemaMap.get(url));
							}
						}
						validationStructure.setNodeValidatorePrincipale(nodeValidatorePrincipale);
						openapiApi.setValidationStructure(validationStructure);
					}

					return; // finish
				}
				
				
				
				this.validatorMap = new HashMap<>();
				
				
				// Verifico se gli schemi importati o gli elementi definition dentro l'interfaccia api, 
				// a loro volta importano altri schemi tramite il $ref standard di json schema
				// Se cosi non è non serve serializzarli su file system, la cui serializzazione è ovviamente più costosa in termini di performance.
				boolean existsRefInternal = false;
				
				
				
				
				/* *** Validatore Principale *** */
				
				Map<String, byte[]> schemiValidatorePrincipale = new HashMap<>();
				Map<String, JsonNode> nodeValidatorePrincipale = new HashMap<>();
	 			
				if(apiValidatorStructure!=null) {
					schemiValidatorePrincipale = apiValidatorStructure.getSchemiValidatorePrincipale();
					nodeValidatorePrincipale = apiValidatorStructure.getNodeValidatorePrincipale();
				}
				else {

					if(openapiApi!=null) {

						Map<String, Schema<?>> definitions = openapiApi.getAllDefinitions();
						String definitionString = Json.mapper().writeValueAsString(definitions);
						definitionString = definitionString.replaceAll("#/components/schemas", "#/definitions");
						for(String schemaName: definitions.keySet()) {
							
							ByteArrayOutputStream bout = new ByteArrayOutputStream();
							bout.write("{".getBytes());
							String defOggetto = Json.mapper().writeValueAsString(definitions.get(schemaName));
							defOggetto = defOggetto.trim();
							defOggetto = defOggetto.replaceAll("#/components/schemas", "#/definitions");
							if(defOggetto.startsWith("{")) {
								defOggetto = defOggetto.substring(1);
							}
							if(defOggetto.endsWith("}")) {
								defOggetto = defOggetto.substring(0,defOggetto.length()-1);
							}
							defOggetto = defOggetto.trim();
							bout.write(defOggetto.getBytes());
							// DEVE ESSERE DEFINITO NEL JSON SCHEMA, NON POSSO DEFINIRLO STATICAMENTE
							/*
								bout.write(",\"additionalProperties\": ".getBytes());
								if(defOggetto.startsWith("\"allOf\"") || defOggetto.startsWith("\"anyOf\"")){
									// INDICARE ARTICOLO CHE SPIEGA
									bout.write("true".getBytes());
								}
								else {
									bout.write("false".getBytes());
								}
							*/
							bout.write(",".getBytes());
							bout.write("\"definitions\" : ".getBytes());
							bout.write(definitionString.getBytes());
							bout.write("}".getBytes());
							
							// Normalizzo schemi importati
							JSONUtils jsonUtils = JSONUtils.getInstance();
							JsonNode schemaNode = jsonUtils.getAsNode(bout.toByteArray());
							nodeValidatorePrincipale.put(schemaName, schemaNode);
							schemiValidatorePrincipale.put(schemaName, bout.toByteArray());
							
							// Verifico se gli elementi definition, a loro volta importano altri schemi
							JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
							List<String> refPath = getRefPath(engine, schemaNode);
							if(refPath!=null && !refPath.isEmpty()) {
								for (String ref : refPath) {
									String path = this.getRefPath(ref);
									if(path!=null) {
										existsRefInternal = true;
										break;
									}
								}
							}
			
						}
					}
				}
				
				
				
				
				
				// *** Gestione file importati ***
				
				this.fileSchema = new HashMap<>();
				if(apiValidatorStructure!=null) {
					this.fileSchema = apiValidatorStructure.getFileSchema();
				}
				else {
					if(api.getSchemas()!=null && api.getSchemas().size()>0) {
					
						HashMap<String, JsonNode> tmpNode = new HashMap<>();
						HashMap<String, byte[]> tmpByteArraySchema = new HashMap<>();
						HashMap<String, ApiSchemaType> tmpSchemaType = new HashMap<>();
						
						for (ApiSchema apiSchema : api.getSchemas()) {
							
							if(!ApiSchemaType.JSON.equals(apiSchema.getType()) && !ApiSchemaType.YAML.equals(apiSchema.getType())) {
								
								continue;
								
							}
							byte [] schema = apiSchema.getContent();
							JsonNode schemaNode = null;
							AbstractUtils utils = null;
							JSONUtils jsonUtils = JSONUtils.getInstance();
							if(ApiSchemaType.JSON.equals(apiSchema.getType())) {
								utils = JSONUtils.getInstance();
								if(((JSONUtils)utils).isJson(schema)) {
									schemaNode = utils.getAsNode(schema);
								}
							}
							else {
								utils = YAMLUtils.getInstance();
								if(((YAMLUtils)utils).isYaml(schema)) {
									schemaNode = utils.getAsNode(schema);
								}
							}
							if(schemaNode==null) {
								continue;
							}
							
							
							/* ** Potenziale import from swagger/openapi che utilizza uno standard components/schema invece di definition, ed inoltre non inizia con { } **/
							if(schemaNode instanceof TextNode) {
								ByteArrayOutputStream bout = new ByteArrayOutputStream();
								bout.write("{".getBytes());
								bout.write(schema);
								bout.write("}".getBytes());
								bout.flush();
								bout.close();
								schemaNode = utils.getAsNode(bout.toByteArray());
							}
							if(schemaNode instanceof ObjectNode) {
								ObjectNode objectNode = (ObjectNode) schemaNode;
								
								boolean foundDefinitions = false;
								JsonNode nodeDefinitions = objectNode.get("definitions");
								if(nodeDefinitions!=null && nodeDefinitions instanceof ObjectNode) {
									foundDefinitions = true;
								}
								
								if(!foundDefinitions) {
									JsonNode nodeComponents = objectNode.get("components");
									if(nodeComponents instanceof ObjectNode) {
										ObjectNode objectNodeComponents = (ObjectNode) nodeComponents;
										JsonNode nodeSchemas = objectNodeComponents.get("schemas");
										if(nodeSchemas!=null && nodeSchemas instanceof ObjectNode) {
											
											ObjectNode objectNodeDefinitions = (ObjectNode) utils.newObjectNode();
											objectNodeDefinitions.set("definitions", nodeSchemas);
											String schemaAsString = null;
											if(ApiSchemaType.YAML.equals(apiSchema.getType())) {
												// converto comunque in json poichè la validazione è supportata per json solo
												schemaAsString = jsonUtils.toString(objectNodeDefinitions);
											}
											else {
												schemaAsString = utils.toString(objectNodeDefinitions);
											}
											schemaAsString = schemaAsString.replaceAll("#/components/schemas", "#/definitions");
											schema = schemaAsString.getBytes();
											schemaNode = objectNodeDefinitions;
											//System.out.println("SCHEMA ["+new String(schema)+"]");
											
										}
										else {
											schema = null;
											schemaNode = null;
										}
									}
								}
							}
							
							if(schemaNode!=null) {
								if(this.onlySchemas) {
									
									// Interfaccia Registro senza OpenAPI
									
									IJsonSchemaValidator validator = ValidatorFactory.newJsonSchemaValidator(jsonValidatorAPI);
									JsonSchemaValidatorConfig schemaValidationConfig = new JsonSchemaValidatorConfig();
									schemaValidationConfig.setVerbose(config.isVerbose());
									schemaValidationConfig.setAdditionalProperties(policyAdditionalProperties);
									schemaValidationConfig.setPoliticaInclusioneTipi(POLITICA_INCLUSIONE_TIPI.DEFAULT);
									schemaValidationConfig.setEmitLogError(config.isEmitLogError());
									schemaValidationConfig.setJsonSchemaVersion(VersionFlag.V4); // https://github.com/OAI/OpenAPI-Specification/blob/main/schemas/v3.0/schema.json
									//System.out.println("ADD SCHEMA PER ["+apiSchemaName+"#"+nameInternal+"] ["+new String(bout.toByteArray())+"]");
									validator.setSchema(schema, schemaValidationConfig, log);
									
									this.validatorMap.put(apiSchema.getName(), validator);
									
								}
								else {
		
									File tmp = FileSystemUtilities.createTempFile("validator", "."+ apiSchema.getType().name().toLowerCase());
									this.fileSchema.put(apiSchema.getName(), tmp);
									tmpNode.put(apiSchema.getName(), schemaNode);
									tmpByteArraySchema.put(apiSchema.getName(), schema);
									tmpSchemaType.put(apiSchema.getName(), apiSchema.getType());
									
								}
							}
						}
						
						
						if(!this.onlySchemas) {
						
							// Verifico se gli schemi importati, a loro volta importano altri schemi
							// Se cosi non è non serve serializzarli su file system
							if(!existsRefInternal && !tmpByteArraySchema.isEmpty()) {
								Iterator<String> itSchemas = tmpByteArraySchema.keySet().iterator();
								while (itSchemas.hasNext()) {
									String apiSchemaName = itSchemas.next();
									JsonNode schemaNode = tmpNode.get(apiSchemaName);
									JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
									List<String> refPath = getRefPath(engine, schemaNode); 
									if(refPath!=null && !refPath.isEmpty()) {
										for (String ref : refPath) {
											String path = this.getRefPath(ref);
											if(path!=null) {
												existsRefInternal = true;
												break;
											}
										}
									}
									if(existsRefInternal) {
										break;
									}
								}
							}
							
							if(existsRefInternal && !tmpByteArraySchema.isEmpty()) {
								Iterator<String> itSchemas = tmpByteArraySchema.keySet().iterator();
								while (itSchemas.hasNext()) {
									String apiSchemaName = (String) itSchemas.next();
									JsonNode schemaNode = tmpNode.get(apiSchemaName);
									byte [] schemaContent = tmpByteArraySchema.get(apiSchemaName);
									
									JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
									List<String> refPath = getRefPath(engine, schemaNode); 
									String schemaRebuild = null;
									if(refPath!=null && !refPath.isEmpty()) {
										for (String ref : refPath) {
											String path = this.getRefPath(ref);
											if(path!=null) {
												String normalizePath = this.normalizePath(path);
												if(normalizePath!=null && this.fileSchema.containsKey(normalizePath)) {
													if(schemaRebuild==null) {
														schemaRebuild = new String(schemaContent);
													}
													if(schemaRebuild.contains(path)==false) {
														if(path.startsWith("./")) {
															path = path.substring(2);
														}
													}
													File file = this.fileSchema.get(normalizePath);
													while(schemaRebuild.contains(path)) {
														schemaRebuild = schemaRebuild.replace(path, org.openspcoop2.utils.Costanti.PROTOCOL_FILE_PREFIX+file.getAbsolutePath());
													}
												}
											}
										}
									}
									
									File f = this.fileSchema.get(apiSchemaName);
									if(schemaRebuild!=null) {
										FileSystemUtilities.writeFile(f, schemaRebuild.getBytes());
										AbstractUtils utils = null;
										if(ApiSchemaType.JSON.equals(tmpSchemaType.get(apiSchemaName))) {
											utils = JSONUtils.getInstance();
										}
										else {
											utils = YAMLUtils.getInstance();
										}
										schemaNode = utils.getAsNode(schemaRebuild);
										tmpNode.put(apiSchemaName, schemaNode);
										tmpByteArraySchema.put(apiSchemaName,schemaRebuild.getBytes());
									}
									else {
										FileSystemUtilities.writeFile(f, schemaContent);
									}
								}
									
								
								if(!tmpByteArraySchema.isEmpty()) {
									itSchemas = tmpByteArraySchema.keySet().iterator();
									while (itSchemas.hasNext()) {
										String apiSchemaName = (String) itSchemas.next();
										JsonNode schemaNode = tmpNode.get(apiSchemaName);
										if(schemaNode instanceof ObjectNode) {
											ObjectNode objectNode = (ObjectNode) schemaNode;
											Iterator<String> it = objectNode.fieldNames();
											String name = null;
											while (it.hasNext()) {
												name = (String) it.next();
												if("definitions".equalsIgnoreCase(name)) {
													JsonNode internalNode = objectNode.get(name);
													if(internalNode instanceof ObjectNode) {
														ObjectNode internalObjectNode = (ObjectNode) internalNode;
														Iterator<String> itInternal = internalObjectNode.fieldNames();
														while (itInternal.hasNext()) {
															String nameInternal = (String) itInternal.next();
															JsonNode typeDefinition = internalObjectNode.get(nameInternal);
															
															ByteArrayOutputStream bout = new ByteArrayOutputStream();
															bout.write("{".getBytes());
															String defOggetto = JSONUtils.getInstance().toString(typeDefinition);
															defOggetto = defOggetto.trim();
															defOggetto = defOggetto.replaceAll("#/components/schemas", "#/definitions");
															if(defOggetto.startsWith("{")) {
																defOggetto = defOggetto.substring(1);
															}
															if(defOggetto.endsWith("}")) {
																defOggetto = defOggetto.substring(0,defOggetto.length()-1);
															}
															defOggetto = defOggetto.trim();
															bout.write(defOggetto.getBytes());
															// DEVE ESSERE DEFINITO NEL JSON SCHEMA, NON POSSO DEFINIRLO STATICAMENTE
															/*
															bout.write(",\"additionalProperties\": ".getBytes());
															if(defOggetto.startsWith("\"allOf\"") || defOggetto.startsWith("\"anyOf\"")){
																// INDICARE ARTICOLO CHE SPIEGA
																bout.write("true".getBytes());
															}
															else {
																bout.write("false".getBytes());
															}*/
															bout.write(",".getBytes());
															bout.write("\"definitions\" : ".getBytes());
															String definitionStringSchema = JSONUtils.getInstance().toString(internalNode);
															definitionStringSchema = definitionStringSchema.replaceAll("#/components/schemas", "#/definitions");
															bout.write(definitionStringSchema.getBytes());
															bout.write("}".getBytes());
																									
															IJsonSchemaValidator validator = ValidatorFactory.newJsonSchemaValidator(jsonValidatorAPI);
															JsonSchemaValidatorConfig schemaValidationConfig = new JsonSchemaValidatorConfig();
															schemaValidationConfig.setVerbose(config.isVerbose());
															schemaValidationConfig.setAdditionalProperties(policyAdditionalProperties);
															schemaValidationConfig.setPoliticaInclusioneTipi(POLITICA_INCLUSIONE_TIPI.DEFAULT);
															schemaValidationConfig.setEmitLogError(config.isEmitLogError());
															schemaValidationConfig.setJsonSchemaVersion(VersionFlag.V4); // https://github.com/OAI/OpenAPI-Specification/blob/main/schemas/v3.0/schema.json
															//System.out.println("ADD SCHEMA PER ["+apiSchemaName+"#"+nameInternal+"] ["+new String(bout.toByteArray())+"]");
															validator.setSchema(bout.toByteArray(), schemaValidationConfig, log);
															
															this.validatorMap.put(apiSchemaName+"#"+nameInternal, validator);
															
														}
													}
												}
											}
										}
									
									}
									
								}
								
							}
						}
					}
				}
				
				
				/* *** Validatore Principale *** */
				
				if(apiValidatorStructure!=null) {
					this.validatorMap = apiValidatorStructure.getValidatorMap();
				}
				else {
					if(schemiValidatorePrincipale!=null && schemiValidatorePrincipale.size()>0) {
						Iterator<String> it = schemiValidatorePrincipale.keySet().iterator();
						while (it.hasNext()) {
							String schemaName = (String) it.next();
							byte [] schema = schemiValidatorePrincipale.get(schemaName);
							JsonNode schemaNode = nodeValidatorePrincipale.get(schemaName);
						
							JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
							List<String> refPath = getRefPath(engine, schemaNode);
							String schemaRebuild = null;
							if(refPath!=null && !refPath.isEmpty()) {
								for (String ref : refPath) {
									String path = this.getRefPath(ref);
									if(path!=null) {
										String normalizePath = this.normalizePath(path);
										if(normalizePath!=null && this.fileSchema.containsKey(normalizePath)) {
											if(schemaRebuild==null) {
												schemaRebuild = new String(schema);
											}
											if(schemaRebuild.contains(path)==false) {
												if(path.startsWith("./")) {
													path = path.substring(2);
												}
											}
											File file = this.fileSchema.get(normalizePath);
											while(schemaRebuild.contains(path)) {
												schemaRebuild = schemaRebuild.replace(path, org.openspcoop2.utils.Costanti.PROTOCOL_FILE_PREFIX+file.getAbsolutePath());
											}
										}
									}
								}
							}
							if(schemaRebuild!=null) {
								schema = schemaRebuild.getBytes();
							}		
							
							// Costruisco validatore
							IJsonSchemaValidator validator = ValidatorFactory.newJsonSchemaValidator(jsonValidatorAPI);
							JsonSchemaValidatorConfig schemaValidationConfig = new JsonSchemaValidatorConfig();
							schemaValidationConfig.setVerbose(config.isVerbose());
							schemaValidationConfig.setAdditionalProperties(policyAdditionalProperties);
							schemaValidationConfig.setPoliticaInclusioneTipi(POLITICA_INCLUSIONE_TIPI.DEFAULT);
							schemaValidationConfig.setEmitLogError(config.isEmitLogError());
							schemaValidationConfig.setJsonSchemaVersion(VersionFlag.V4); // https://github.com/OAI/OpenAPI-Specification/blob/main/schemas/v3.0/schema.json
							//System.out.println("ADD SCHEMA PER ["+schemaName+"] ["+new String(schema)+"]");
							validator.setSchema(schema, schemaValidationConfig, log);
							
							this.validatorMap.put(schemaName, validator);
						}
					}
				}
				
				
				
				// Salvo informazioni ricostruite
				if(apiValidatorStructure==null) {
					OpenapiApiValidatorStructure validationStructure = new OpenapiApiValidatorStructure();
					validationStructure.setSchemiValidatorePrincipale(schemiValidatorePrincipale);
					validationStructure.setNodeValidatorePrincipale(nodeValidatorePrincipale);
					validationStructure.setFileSchema(this.fileSchema);
					validationStructure.setValidatorMap(this.validatorMap);
					if(openapiApi!=null) {
						openapiApi.setValidationStructure(validationStructure);
					}
					else if(apiRest!=null) {
						apiRest.addVendorImpl(VALIDATION_STRUCTURE, validationStructure);
					}
				}
				
								
			} catch(Throwable e) {
				try {
					this.close(log, api, config); // per chiudere eventuali risorse parzialmente inizializzate
				}catch(Throwable t) {}
				
				throw new ProcessingException(e);
			}
			
		}finally {
			this.semaphore.release(lock, "init");
		}
	}

	private List<String> getRefPath(JsonPathExpressionEngine engine, JsonNode schemaNode) throws JsonPathException, JsonPathNotValidException {
		List<String> l = null;
		try {
			l = engine.getStringMatchPattern(schemaNode, "$..$ref");
		}catch(JsonPathNotFoundException notFound) {
			// ignore
		}
		return l;
	}
	
	private String getRefPath(String ref) {
		if(ref.trim().startsWith("#")) {
			return null;
		}
		return ref.trim().substring(0, ref.indexOf("#"));
	}
	private static String getRefType(String ref) {
		if(ref.trim().startsWith("#")) {
			return ref;
		}
		return ref.trim().substring(ref.indexOf("#"), ref.length());
	}
	private String normalizePath(String path) throws ProcessingException {
		if(path.startsWith(org.openspcoop2.utils.Costanti.PROTOCOL_HTTP_PREFIX) || path.startsWith(org.openspcoop2.utils.Costanti.PROTOCOL_HTTPS_PREFIX) || path.startsWith(org.openspcoop2.utils.Costanti.PROTOCOL_FILE_PREFIX)){	
			try {
				URL url = new URI(path).toURL();
				File fileUrl = new File(url.getFile());
				return fileUrl.getName();
			}catch(Exception e) {
				throw new ProcessingException(e.getMessage(),e);
			}
		}
		else{
			File f = new File(path);
			return f.getName();
		}
	}
	private void normalizeRefs(JsonNode node) throws ProcessingException {
		List<JsonNode> listRef = node.findParents(OAI3SchemaKeywords.$REF);
		if(listRef!=null) {
			for (JsonNode jsonNodeRef : listRef) {
				if(jsonNodeRef instanceof ObjectNode oNode) {
					JsonNode valore = oNode.get(OAI3SchemaKeywords.$REF);
					String ref = valore.asText();
					String path = getRefPath(ref);
					if(path!=null) {
						String normalizePath = normalizePath(path);
						String refType = getRefType(ref);
						//System.out.println("REF ("+jsonNodeRef.getClass().getName()+") : "+jsonNodeRef);
						//System.out.println("Tipo ("+refType+") VALORE:"+normalizePath);
						oNode.remove(OAI3SchemaKeywords.$REF);
						oNode.put(OAI3SchemaKeywords.$REF, normalizePath+refType);
					}
				}
			}
		}
	}
	
	@Override
	public void close(Logger log, Api api, ApiValidatorConfig config) throws ProcessingException{
		if(this.fileSchema!=null) {
			Iterator<String> itFiles = this.fileSchema.keySet().iterator();
			while (itFiles.hasNext()) {
				String key = (String) itFiles.next();
				File file = this.fileSchema.get(key);
				if(file.delete()==false) {
					log.error("Eliminazione file temporaneo ["+file.getAbsolutePath()+"] associato allo schema ["+key+"] non riuscita");
				}
			}
		}
	}
	
	@Override
	public void validate(HttpBaseEntity<?> httpEntity)
			throws ProcessingException, ValidatorException {
		List<Object> args = new ArrayList<>();
		super.validate(this.api, httpEntity, args);
	}

	private List<ApiBodyParameter> getBodyParameters(HttpBaseEntity<?> httpEntity, ApiOperation operation){
		List<ApiBodyParameter> bodyParameters = null;
		if(httpEntity instanceof HttpBaseRequestEntity) {
			if(operation.getRequest()!=null) {
				bodyParameters = operation.getRequest().getBodyParameters();
			}
		}
		else if(httpEntity instanceof HttpBaseResponseEntity<?>) {
				
			HttpBaseResponseEntity<?> response = (HttpBaseResponseEntity<?>) httpEntity;
			ApiResponse apiResponseFound = null;
			ApiResponse apiResponseDefault = null;
			
			if(operation.getResponses()!=null) {
				for (ApiResponse apiResponse : operation.getResponses()) {
					if(apiResponse.isDefaultHttpReturnCode()) {
						apiResponseDefault = apiResponse;
					}
					if(response.getStatus() == apiResponse.getHttpReturnCode()){
						apiResponseFound = apiResponse;
						break;
					}										
				}
			}
			
			if(apiResponseFound==null && apiResponseDefault!=null) {
				apiResponseFound = apiResponseDefault;
			}	
			if(apiResponseFound!=null){
				// eventuali errori di stato non trovato sono gestiti successivavemnte nella validazione
				bodyParameters = apiResponseFound.getBodyParameters();
			}
					
		}
		return bodyParameters;
	}
	
	@Override
	public void validatePreConformanceCheck(HttpBaseEntity<?> httpEntity,
			ApiOperation operation, Object... args) throws ProcessingException, ValidatorException {

		List<ApiBodyParameter> bodyParameters = this.getBodyParameters(httpEntity, operation);

		// se e' attivo openApi4j intanto valido subito
		if(this.openApi4j!=null) {
			validateWithOpenApi4j(httpEntity, operation);
		}
		else if(this.swaggerRequestValidator!=null) {
			validateWithSwaggerRequestValidator(httpEntity, operation);
		}
		
		
		// Controllo poi i campi required come controllo aggiuntivo a openApi4j
		boolean required = false;
		if(bodyParameters!=null && !bodyParameters.isEmpty()) {
			for(ApiBodyParameter body: bodyParameters) {
				if(body.isRequired())
					required = true;
			}
		}
		if(required) {
			if(httpEntity.getContent() == null) {
				throw new ValidatorException("Required body undefined");
			}
		}
		
		// infine se non e' attivo openApi4j effettuo la validazione alternativa
		if(this.openApi4j==null && this.swaggerRequestValidator==null) {
			if(bodyParameters!=null && !bodyParameters.isEmpty()) {
			
				try {
					
					boolean isJson =  httpEntity.getContentType()!=null && httpEntity.getContentType().toLowerCase().contains("json"); // supporta per adesso solo json, la validazione xml non è funzionante
					if(isJson) {
					
						//System.out.println("==================== ("+httpEntity.getClass().getName()+") ====================");
						List<IJsonSchemaValidator> validatorLst = getValidatorList(operation, httpEntity);
						//System.out.println("SIZE: "+validatorLst.size());
						boolean valid = false;
						Exception exc = null;
						if(httpEntity.getContent()!=null) {
							byte[] bytes = httpEntity.getContent().toString().getBytes();
							for(IJsonSchemaValidator validator: validatorLst) {
								ValidationResponse response = validator.validate(bytes);
								if(!ESITO.OK.equals(response.getEsito())) {
									exc = response.getException();
								} else {
									valid = true;
								}
							}
						}
						else {
							throw new ValidatorException("Content undefined");
						}
						
						if(!valid) {
							throw new ValidatorException(exc);
						}
						
					}
					
				} catch (ValidationException e) {
					throw new ValidatorException(e);
				}
			}
		}

	}

	/**
	 * @param operation
	 * @return
	 */
	private List<IJsonSchemaValidator> getValidatorList(ApiOperation operation, HttpBaseEntity<?> httpEntity) throws ValidatorException {
		List<IJsonSchemaValidator> lst = new ArrayList<>();
		
		if(this.onlySchemas) {
			if(this.validatorMap!=null) {
				lst.addAll(this.validatorMap.values());
			}
		}
		else {
		
			List<ApiBodyParameter> bodyParameters = this.getBodyParameters(httpEntity, operation);
			
			if(bodyParameters!=null && !bodyParameters.isEmpty()) {
				for(ApiBodyParameter body: bodyParameters) {
					
					String key = null;
					if(body.getElement() instanceof ApiReference) {
						ApiReference apiRef = (ApiReference) body.getElement();
						//System.out.println("API REF ref["+apiRef.getSchemaRef()+"] ["+apiRef.getType()+"]");
						key = apiRef.getSchemaRef()+"#"+apiRef.getType();
					}
					else {
						key = body.getElement().toString();
					}
					
					//System.out.println("SEARCH ["+body.getElement().getClass().getName()+"] ["+body.getElement()+"] key["+key+"] ...");
					
					if(this.validatorMap!=null && this.validatorMap.containsKey(key)) {
						//System.out.println("ADD VALIDATORE ["+key+"]: ["+this.validatorMap.get(key)+"]");
						lst.add(this.validatorMap.get(key));
					}
				}
			}
			
		}
			
		if(lst.isEmpty())
			throw new ValidatorException("Validator not found");
		
		return lst;
	}
	
	private void validateWithOpenApi4j(HttpBaseEntity<?> httpEntity, ApiOperation operation) throws ProcessingException, ValidatorException {
				
		Operation operationOpenApi4j = null;
		Path pathOpenApi4j = null;
		for (String path :this.openApi4j.getPaths().keySet()) {
			Path pathO = this.openApi4j.getPaths().get(path);
			for (String method : pathO.getOperations().keySet()) {
				Operation op = pathO.getOperation(method);
				//System.out.println("CHECK: ["+method+"] "+path);
				String normalizePath = ApiOperation.normalizePath(path);
				if(operation.getHttpMethod().toString().equalsIgnoreCase(method) && operation.getPath().equals(normalizePath)) {
					operationOpenApi4j = op;
					pathOpenApi4j = pathO;
					break;
				}
			}
		}
		if(operationOpenApi4j==null || pathOpenApi4j==null) {
			throw new ProcessingException("Resource "+operation.getHttpMethod()+" "+operation.getPath()+" not found in OpenAPI 3");
		}
		
		try {
		
			ValidationData<Void> vData = new ValidationData<>();
			this.openApi4j.setServers(null); // se lascio la definizione dei server, il validatePath sottostante verifica che la url corrisponda anche con la parte del server
			OperationValidator val = new OperationValidator(this.openApi4j, pathOpenApi4j, operationOpenApi4j);
			
			if(httpEntity instanceof HttpBaseRequestEntity) {
				
				HttpBaseRequestEntity<?> httpRequest = (HttpBaseRequestEntity<?>) httpEntity;
				Request requestOpenApi4j = buildRequestOpenApi4j(httpRequest.getUrl(), httpRequest.getMethod().toString(), 
						httpRequest.getParameters(), httpRequest.getCookies(), httpRequest.getHeaders(),
						httpRequest.getContent());
				if(this.openApi4jConfig.isValidateRequestPath()) {
					val.validatePath(requestOpenApi4j, vData); // LA url fornita deve corrispondere alla parte delle risorse SENZA la parte del server
				}
				if(this.openApi4jConfig.isValidateRequestQuery()) {
					val.validateQuery(requestOpenApi4j, vData);
				}
				if(this.openApi4jConfig.isValidateRequestHeaders()) {
					val.validateHeaders(requestOpenApi4j, vData);
				}
				if(this.openApi4jConfig.isValidateRequestCookie()) {
					val.validateCookies(requestOpenApi4j, vData);
				}
				if(this.openApi4jConfig.isValidateRequestBody()) {
					val.validateBody(requestOpenApi4j, vData);
				}
			}
			else if(httpEntity instanceof HttpBaseResponseEntity<?>) {
					
				HttpBaseResponseEntity<?> response = (HttpBaseResponseEntity<?>) httpEntity;
				Response responseOpenApi4j = buildResponseOpenApi4j(response.getStatus(), response.getHeaders(), 
						response.getContent());
				if(this.openApi4jConfig.isValidateResponseHeaders()) {
					val.validateHeaders(responseOpenApi4j, vData);
				}
				if(this.openApi4jConfig.isValidateResponseBody()) {
					val.validateBody(responseOpenApi4j, vData);
				}
			}
			
			if(vData.isValid()==false) {
				if(vData.results()!=null) {
					throw new ValidatorException(vData.results().toString());
				}
				else {
					throw new ValidatorException("Validation failed");
				}
			}
			
		}catch(ValidatorException e) {
			throw e;
		}catch(Exception e) {
			throw new ProcessingException(e.getMessage(),e);
		}
	}
	private Request buildRequestOpenApi4j(String urlInvocazione, String method, 
			Map<String, List<String>> queryParams, List<Cookie> cookies, Map<String, List<String>> headers, 
			Object content) throws ProcessingException {

		try {
		
			// Method e path
		    final DefaultRequest.Builder builder = new DefaultRequest.Builder(
		    		urlInvocazione,
		    		Request.Method.getMethod(method));
	
		    String queryString = null;
			if(queryParams!=null && !queryParams.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				Iterator<String> keys = queryParams.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					List<String> values = queryParams.get(key);
					try{
						key = TransportUtils.urlEncodeParam(key,Charset.UTF_8.getValue());
					}catch(Exception e){
						if(this.log!=null) {
							this.log.error("URLEncode key["+key+"] error: "+e.getMessage(),e);
						}
						else {
							e.printStackTrace(System.out);
						}
					}
					
					for (String value : values) {
						if(sb.length()>0) {
							sb.append("&");
						}
						try{
							value = TransportUtils.urlEncodeParam(value,Charset.UTF_8.getValue());
						}catch(Exception e){
							if(this.log!=null) {
								this.log.error("URLEncode value["+value+"] error: "+e.getMessage(),e);
							}
							else {
								e.printStackTrace(System.out);
							}
						}
						sb.append(key);
						sb.append("=");
						sb.append(value);	
					}
				}
				queryString = sb.toString();
			}
		    
		    // Query string or body
		    if (HttpRequestMethod.GET.toString().equalsIgnoreCase(method)) {
		    	builder.query(queryString);
		    } else {
		    	if(queryString!=null) {
		    		builder.query(queryString); // senno non vengono validati i query parameters
		    	}
		    	if(content!=null) {
		    		String s = null;
		    		byte[] b = null;
		    		InputStream is = null;
		    		if(content instanceof String) {
		    			s = (String) content;
		    		}
		    		else if(content instanceof byte[]) {
		    			b=(byte[])content;
		    		}
		    		else if(content instanceof InputStream) {
		    			is = (InputStream) content;
		    		}
		    		if(s!=null) {
		    			builder.body(Body.from(s));
		    		}
		    		else if(b!=null) {
		    			try(ByteArrayInputStream bin =new ByteArrayInputStream(b)){
		    				builder.body(Body.from(bin));
		    			}
		    		}
		    		else if(is!=null) {
		    			builder.body(Body.from(is));
		    		}
		    		else {
		    			throw new Exception("Type '"+content.getClass().getName()+"' unsupported");
		    		}
		    	}
		    }
	
		    // Cookies
		    if (cookies != null) {
		    	for (Cookie cookie : cookies) {
		    		builder.cookie(cookie.getName(), cookie.getValue());
		    	}
		    }
	
		    // Headers
		    if(headers!=null) {
		    	Iterator<String> headerNames = headers.keySet().iterator();
		    	if (headerNames != null) {
		    		while (headerNames.hasNext()) {
		    			String headerName = headerNames.next();
		    			builder.header(headerName, headers.get(headerName));
		    		}
		    	}
		    }
	
		    return builder.build();
		    
		}catch(Exception e) {
			throw new ProcessingException(e.getMessage(),e);
		}
	}
	
	private static Response buildResponseOpenApi4j(int status, Map<String, List<String>> headers, Object content) throws ProcessingException {
		
		try {
		
			// status
			final DefaultResponse.Builder builder = new DefaultResponse.Builder(status);
			
			// body
			if(content!=null) {
	    		String s = null;
	    		byte[] b = null;
	    		InputStream is = null;
	    		if(content instanceof String) {
	    			s = (String) content;
	    		}
	    		else if(content instanceof byte[]) {
	    			b=(byte[])content;
	    		}
	    		else if(content instanceof InputStream) {
	    			is = (InputStream) content;
	    		}
	    		else if(content instanceof Document) {
	    			b=XMLUtils.getInstance().toByteArray(((Document)content));
	    		}
	    		else if(content instanceof Element) {
	    			b=XMLUtils.getInstance().toByteArray(((Element)content));
	    		}
	    		
	    		if(s!=null) {
	    			builder.body(Body.from(s));
	    		}
	    		else if(b!=null) {
	    			try(ByteArrayInputStream bin =new ByteArrayInputStream(b)){
	    				builder.body(Body.from(bin));
	    			}
	    		}
	    		else if(is!=null) {
	    			builder.body(Body.from(is));
	    		}
	    		else {
	    			throw new Exception("Type '"+content.getClass().getName()+"' unsupported");
	    		}
	    	}
			
		    // Headers
		    if(headers!=null) {
			    Iterator<String> headerNames = headers.keySet().iterator();
			    if (headerNames != null) {
			      while (headerNames.hasNext()) {
			        String headerName = headerNames.next();
			        builder.header(headerName, headers.get(headerName));
			      }
			    }
		    }
		    
		    return builder.build();
		    
		}catch(Exception e) {
			throw new ProcessingException(e.getMessage(),e);
		}
	}
	
	

	private void validateWithSwaggerRequestValidator(HttpBaseEntity<?> httpEntity, ApiOperation gwOperation) throws ProcessingException, ValidatorException {
		
		// IMPROVEMENT: Invece di fare la readOperationsMap, meglio uno switch che fa la getGET, getPOST ecc.. giusta.		
		
		// Dentro l'openApiSwagger ho i path col dollaro, nell'oggetto openapi di openspcoop invece
		// ho il path puliti 		
		Optional<Entry<String, PathItem>> item = this.openApiSwagger
				.getPaths()
				.entrySet()
				.stream()
				.filter( pathItem -> 
						ApiOperation.normalizePath(pathItem.getKey()).equals(gwOperation.getPath()))
				.findFirst();
		
		if(item.isEmpty()) {
			throw new ProcessingException(
					"Resource " + gwOperation.getHttpMethod() + " " + gwOperation.getPath() + " not found in OpenAPI 3");
		}
		
		HttpMethod method = HttpMethod.valueOf(gwOperation.getHttpMethod().toString());
		
		io.swagger.v3.oas.models.Operation swaggerOperation = 
				item.get().getValue().readOperationsMap().get(method);
	
		ApiPath apiPath = new ApiPathImpl(httpEntity.getUrl(), null);
		NormalisedPath requestPath = new NormalisedPathImpl(httpEntity.getUrl(), null);
						
		com.atlassian.oai.validator.model.ApiOperation swaggerValidatorOperation = 
				new com.atlassian.oai.validator.model.ApiOperation(apiPath, requestPath, method, swaggerOperation); 
		
		ValidationReport report;
		
		
		
		if(httpEntity instanceof HttpBaseRequestEntity) {			
		
			HttpBaseRequestEntity<?> request = (HttpBaseRequestEntity<?>) httpEntity;
			var swaggerRequest = buildSwaggerRequest(request);		    
		    report = this.swaggerRequestValidator.validateRequest(swaggerRequest, swaggerValidatorOperation);		    
		}
		else if(httpEntity instanceof HttpBaseResponseEntity<?>) {				
			HttpBaseResponseEntity<?> response = (HttpBaseResponseEntity<?>) httpEntity;
			var swaggerResponse = buildSwaggerResponse(response);
			report = this.swaggerResponseValidator.validateResponse(swaggerResponse, swaggerValidatorOperation);			
		} else {
			throw new ProcessingException("Unknown type for HttpBaseEntity: " + httpEntity.getClass().toString());
		
		}
		
		if (report.hasErrors()) {
			String msgReport = 	SimpleValidationReportFormat.getInstance().apply(report);		
			throw new ValidatorException(msgReport);
		}
		
	}
	



	@Override
	public void validatePostConformanceCheck(HttpBaseEntity<?> httpEntity,
			ApiOperation operation, Object... args) throws ProcessingException,
	ValidatorException {
		
	}

	@Override
	public void validateValueAsType(ApiParameterType parameterType, String value, String type, ApiSchemaTypeRestriction typeRestriction)
			throws ProcessingException, ValidatorException {

		if(type!=null){
			type = type.trim();

			BigDecimal numberValue = null;
			String stringValue = null;
			
			if("string".equalsIgnoreCase(type)){
				stringValue = value;
			}
			else if("byte".equalsIgnoreCase(type) || "unsignedByte".equalsIgnoreCase(type)){
				try{
					byte v = Byte.parseByte(value);
					numberValue = new BigDecimal(v);
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("char".equalsIgnoreCase(type)){
				if(value.length()>1){
					throw new ValidatorException("More than one character");
				}
				stringValue = value;
			}
			else if("double".equalsIgnoreCase(type) || "decimal".equalsIgnoreCase(type)){
				try{
					double v = Double.parseDouble(value);
					numberValue = BigDecimal.valueOf(v);
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("float".equalsIgnoreCase(type)){
				try{
					float v = Float.parseFloat(value);
					numberValue = BigDecimal.valueOf(v);
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("int".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type) || 
					"positiveInteger".equalsIgnoreCase(type) || "negativeInteger".equalsIgnoreCase(type) ||
					"nonPositiveInteger".equalsIgnoreCase(type) || "nonNegativeInteger".equalsIgnoreCase(type) || 
					"unsignedInt".equalsIgnoreCase(type) ||
					"int32".equalsIgnoreCase(type)){
				try{
					int i = Integer.parseInt(value);
					if("positiveInteger".equalsIgnoreCase(type)){
						if(i<=0){
							throw new ValidatorException("Expected a positive value");
						}
					}
					else if("nonNegativeInteger".equalsIgnoreCase(type)){
						if(i<0){
							throw new ValidatorException("Expected a non negative value");
						}
					}
					else if("negativeInteger".equalsIgnoreCase(type)){
						if(i>=0){
							throw new ValidatorException("Expected a negative value");
						}
					}
					else if("nonPositiveInteger".equalsIgnoreCase(type)){
						if(i>0){
							throw new ValidatorException("Expected a non positive value");
						}
					}
					else if("unsignedInt".equalsIgnoreCase(type)){
						if(i<0){
							throw new ValidatorException("Expected a unsigned value");
						}
					}
					numberValue = BigDecimal.valueOf(i);
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("long".equalsIgnoreCase(type) || "unsignedLong".equalsIgnoreCase(type)||
					"int64".equalsIgnoreCase(type)){
				try{
					long l = Long.parseLong(value);
					if("unsignedLong".equalsIgnoreCase(type)){
						if(l<0){
							throw new ValidatorException("Expected a unsigned value");
						}
					}
					numberValue = new BigDecimal(l);
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("number".equalsIgnoreCase(type)) {
				try{
					// Any numbers.
					try{
						double d = Double.parseDouble(value);
						numberValue = BigDecimal.valueOf(d);
					}catch(Exception e){
						long l = Long.parseLong(value);
						numberValue = new BigDecimal(l);
					}
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("short".equalsIgnoreCase(type) || "unsignedShort".equalsIgnoreCase(type)){
				try{
					short s = Short.parseShort(value);
					if("unsignedShort".equalsIgnoreCase(type)){
						if(s<0){
							throw new ValidatorException("Expected a unsigned value");
						}
					}
					numberValue = new BigDecimal(s);
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("boolean".equalsIgnoreCase(type)){
				try{
					if(!"true".equals(value) && !"false".equals(value)) {
						throw new Exception("Only true/false value expected (found: "+value+"); Note that truthy and falsy values such as \"true\", \"\", 0 or null are not considered boolean values.");
					}
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("anyURI".equalsIgnoreCase(type)){
				try{
					new URI(value);
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("uuid".equalsIgnoreCase(type)){
				try{
					UUID.fromString(value);
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("date-time".equalsIgnoreCase(type)){
				try {
					DateUtils.validateDateTimeAsRFC3339Sec56(value);
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("date".equalsIgnoreCase(type)){
				try {
					DateUtils.validateDateAsRFC3339Sec56(value);
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			/**else if("time".equalsIgnoreCase(type)){
				try {
					DateUtils.validateTimeAsRFC3339Sec56(value);
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}*/
			
			if(typeRestriction!=null) {
				
				if(numberValue!=null) {
					
					// max
					if(typeRestriction.getMaximum()!=null) {
						int compare = numberValue.compareTo(typeRestriction.getMaximum());
						if(compare<0) {
							// numberValue < maximum
						}
						else if(compare>0) {
							// numberValue > maximum
							throw new ValidatorException("Value higher than the maximum '"+typeRestriction.getMaximum()+"'");
						}
						else {
							// numberValue == maximum
							if(typeRestriction.getExclusiveMaximum()!=null && typeRestriction.getExclusiveMaximum()) {
								throw new ValidatorException("Value equals to the maximum '"+typeRestriction.getMaximum()+"' and exclusive maximum is enabled");
							}
						}
					}
					
					// min
					if(typeRestriction.getMinimum()!=null) {
						int compare = numberValue.compareTo(typeRestriction.getMinimum());
						if(compare<0) {
							// numberValue < minimum
							throw new ValidatorException("Value lowest than the minimum '"+typeRestriction.getMinimum()+"'");
						}
						else if(compare>0) {
							// numberValue > minimum
						}
						else {
							// numberValue == minimum
							if(typeRestriction.getExclusiveMinimum()!=null && typeRestriction.getExclusiveMinimum()) {
								throw new ValidatorException("Value equals to the minimum '"+typeRestriction.getMinimum()+"' and exclusive minimum is enabled");
							}
						}
					}
					
					// multipleOf
					if(typeRestriction.getMultipleOf()!=null) {
						if (numberValue.compareTo(typeRestriction.getMultipleOf()) != 0) {
						   try{
							   @SuppressWarnings("unused")
							   BigDecimal bd = numberValue.divide(typeRestriction.getMultipleOf(), 0, RoundingMode.UNNECESSARY);
						   }
						   catch(ArithmeticException e) {
							   throw new ValidatorException("Value is not multiple of '"+typeRestriction.getMultipleOf()+"'");
						   }
						}
					}
				}
				
				if(stringValue!=null) {
					
					// enum
					if(typeRestriction.getEnumValues()!=null && !typeRestriction.getEnumValues().isEmpty()) {
						
						List<String> valoriPresenti = new ArrayList<>();
						if(typeRestriction.isArrayParameter()) {
							if(ApiParameterType.query.equals(parameterType) || ApiParameterType.form.equals(parameterType)) {
								if(typeRestriction.isStyleQueryForm() || typeRestriction.getStyle()==null) { // form è il default
									if(typeRestriction.isExplodeDisabled()) {
										List<String> l = StringUtil.tokenize(stringValue, ",", false, false);
										if(l!=null && !l.isEmpty()) {
											valoriPresenti.addAll(l);
										}
									}
								}
								else if(typeRestriction.isStyleQuerySpaceDelimited()) {
									if(typeRestriction.isExplodeDisabled()) {
										List<String> l = StringUtil.tokenize(stringValue, Pattern.quote(" "), false, false);
										if(l!=null && !l.isEmpty()) {
											valoriPresenti.addAll(l);
										}
									}
								}
								else if(typeRestriction.isStyleQueryPipeDelimited()) {
									if(typeRestriction.isExplodeDisabled()) {
										List<String> l = StringUtil.tokenize(stringValue, Pattern.quote("|"), false, false);
										if(l!=null && !l.isEmpty()) {
											valoriPresenti.addAll(l);
										}
									}
								}
							}
							else if(ApiParameterType.header.equals(parameterType)) {
								if(typeRestriction.isStyleHeaderSimple() || typeRestriction.getStyle()==null) { // simple è il default
									List<String> l = StringUtil.tokenize(stringValue, ",", false, false);
									if(l!=null && !l.isEmpty()) {
										valoriPresenti.addAll(l);
									}
								}
							}
							else if(ApiParameterType.path.equals(parameterType)) {
								if(typeRestriction.isStylePathSimple() || typeRestriction.getStyle()==null) { // simple è il default
									List<String> l = StringUtil.tokenize(stringValue, ",", false, false);
									if(l!=null && !l.isEmpty()) {
										valoriPresenti.addAll(l);
									}
								}
								else if(typeRestriction.isStylePathLabel()) {
									if(stringValue.length()>1) {
										String splitPattern = typeRestriction.isExplodeEnabled() ?  "\\." : ",";
										String [] v = stringValue.substring(1).split(splitPattern);
										if(v!=null && v.length>0) {
											for (String valore : v) {
												valoriPresenti.add(valore);
											}
										}
									}
								}
								else if(typeRestriction.isStylePathMatrix()) {
									String splitPattern = typeRestriction.isExplodeEnabled() ?  ";" : ",";
									List<String> l = getArrayValues(typeRestriction.isExplodeEnabled(), stringValue, splitPattern);
									if(l!=null && !l.isEmpty()) {
										valoriPresenti.addAll(l);
									}
								}
							}
						}
						if(valoriPresenti.isEmpty()) {
							valoriPresenti.add(stringValue);
						}
						
						for (String valorePresente : valoriPresenti) {
							boolean found = false;
							StringBuilder sbList = new StringBuilder();
							for (Object o : typeRestriction.getEnumValues()) {
								if(o!=null) {
									String check = o.toString();
									if(sbList.length()>0) {
										sbList.append(",");
									}
									sbList.append(check);
									if(valorePresente.equals(check)) {
										found = true;
										break;
									}
								}
							}
							if(!found) {
								throw new ValidatorException("Uncorrect enum value '"+valorePresente+"', expected: '"+sbList.toString()+"'");
							}
						}
					}
					
					// min length
					if(typeRestriction.getMinLength()!=null) {
						if(stringValue.length()<typeRestriction.getMinLength().intValue()) {
							throw new ValidatorException("Too short, expected min length '"+typeRestriction.getMinLength()+"'");
						}
					}
					
					// max length
					if(typeRestriction.getMaxLength()!=null) {
						if(stringValue.length()>typeRestriction.getMaxLength().intValue()) {
							throw new ValidatorException("Too big, expected max length '"+typeRestriction.getMaxLength()+"'");
						}
					}
					
					/*
					 * Note that the regular expression is enclosed in the ^…$ tokens, where ^ means the beginning of the string, and $ means the end of the string. 
					 * Without ^…$, pattern works as a partial match, that is, matches any string that contains the specified regular expression. 
					 * For example, pattern: pet matches pet, petstore and carpet. The ^…$ token forces an exact match.
					 **/
					if(typeRestriction.getPattern()!=null) {
						String pattern = typeRestriction.getPattern().trim();
						try {
							if(pattern.startsWith("^") && pattern.endsWith("$")) {
								if(!RegularExpressionEngine.isMatch(stringValue, pattern)) {
									throw new ValidatorException("Pattern match failed ('"+pattern+"')");
								}
							}
							else {
								if(!RegularExpressionEngine.isFind(stringValue, pattern)) {
									throw new ValidatorException("Pattern match failed ('"+pattern+"')");
								}
							}
						}
						catch(ValidatorException e) {
							throw e;
						}
						catch(Throwable e) {
							throw new ValidatorException("Pattern validation error '"+pattern+"': "+e.getMessage(),e);
						}
					}
				}
			}
			
		}

		// altri tipi non li valido per ora


	}

	private static final Pattern PREFIXED_SEMICOLON_NAME_REGEX = Pattern.compile("(?:;)([^;]+)(?:=)([^;]*)");
	private List<String> getArrayValues(boolean explode, String rawValue, String splitPattern) {
		
		try {
			Matcher matcher = PREFIXED_SEMICOLON_NAME_REGEX.matcher(rawValue);
	
			if (explode) {
				List<String> arrayValues = new ArrayList<>();
				int index = 0;
				int limit = 1000; // per gestire DOS
				while (matcher.find() && index<limit) {
					arrayValues.add(matcher.group(2));
					index++;
				}
				return arrayValues;
			} else {
				return matcher.matches()
						? Arrays.asList(matcher.group(2).split(splitPattern))
								: null;
			}
		}catch(Throwable t) {
			this.log.error(t.getMessage(), t);
			return null;
		}
	}
	
	
	// ================= SWAGGER REQUEST VALIDATOR GLUE CODE =====================

	private static Method fromHttpMethod(HttpRequestMethod method) {
		return Method.valueOf(method.toString());
	}
	
	
	private static com.atlassian.oai.validator.model.Response buildSwaggerResponse(HttpBaseResponseEntity<?> response) throws ProcessingException {

		final SimpleResponse.Builder builder = 
				new SimpleResponse.Builder(response.getStatus());
		
		if(response.getHeaders()!=null && !response.getHeaders().isEmpty()) {
			response.getHeaders().forEach(builder::withHeader);
		}
		
		Object content = response.getContent();
		if(content instanceof String) {
			builder.withBody( (String) content);
		}
		else if(content instanceof byte[]) {
			builder.withBody((byte[]) content);
		}
		else if(content instanceof InputStream) {
			builder.withBody((InputStream) content);
		}
		else if(content instanceof Document) {
			try {
				builder.withBody(XMLUtils.getInstance().toByteArray(((Document)content)));
			} catch (Exception e) {
				throw new ProcessingException(e.getMessage(),e);
			}
		}
		else if(content instanceof Element) {
			try {
				builder.withBody(XMLUtils.getInstance().toByteArray(((Element)content)));
			} catch (Exception e) {
				throw new ProcessingException(e.getMessage(),e);
			}
		}
		else if (content == null) {
			// nop
		} else {
			throw new ProcessingException("Type '"+content.getClass().getName()+"' unsupported");
		}
		
		return builder.build();
	
	}
	
	private static com.atlassian.oai.validator.model.Request buildSwaggerRequest(HttpBaseRequestEntity<?> request) throws ProcessingException {
		Object content = request.getContent();
		final SimpleRequest.Builder builder =
                new SimpleRequest.Builder(fromHttpMethod(request.getMethod()),request.getUrl());
   
		// BugFix: 
		// Request Accept header '*; q=.2' is not a valid media type
		
		// BugFix2:
		// Request Accept header '[text/xml]' does not match any defined response types. Must be one of: [text/*, application/*].
		
		Map<String, List<String>> hdr = request.getHeaders();
		if(hdr!=null && !hdr.isEmpty()) {
			List<String> originalValues = TransportUtils.getValues(hdr, HttpConstants.ACCEPT);
//			List<String> newList = null;
//			if(originalValues!=null && !originalValues.isEmpty()) {
//				newList = new ArrayList<>();
//				for (String original : originalValues) {
//					
//					String [] acceptHeaders = null;
//					if(original.contains(",")) {
//						acceptHeaders = original.split(",");
//						for (int i = 0; i < acceptHeaders.length; i++) {
//							acceptHeaders[i] = acceptHeaders[i].trim();
//						}
//					}
//					else {
//						acceptHeaders = new String [] {original.trim()};
//					}
//					StringBuilder sbNewList = new StringBuilder();
//					for (String hdrCheck : acceptHeaders) {
//						if(hdrCheck!=null && org.apache.commons.lang.StringUtils.isNotEmpty(hdrCheck.trim())) {
//							if(hdrCheck.contains(";")) {
//								String [] tmp = hdrCheck.split(";");
//								String media = tmp[0];
//								if(media!=null && org.apache.commons.lang.StringUtils.isNotEmpty(media.trim())) {
//									if(sbNewList.length()>0) {
//										sbNewList.append(", ");
//									}
//									String v = media.trim();
//									if(v.equals("*")) {
//										v = "*/*";
//									}
//									sbNewList.append(v);
//								}
//							}
//							else {
//								if(sbNewList.length()>0) {
//									sbNewList.append(", ");
//								}
//								String v = hdrCheck.trim();
//								if(v.equals("*")) {
//									v = "*/*";
//								}
//								sbNewList.append(v);
//							}
//						}
//					}
//					if(sbNewList.length()>0) {
//						newList.add(sbNewList.toString());
//					}
//				}
//				TransportUtils.removeRawObject(hdr, HttpConstants.ACCEPT);
//				if(!newList.isEmpty()) {
//					hdr.put(HttpConstants.ACCEPT, newList);
//				}
//			}
			TransportUtils.removeRawObject(hdr, HttpConstants.ACCEPT); // lo rimuovo direttamente e non lo faccio validare per risolvere BugFix2
			
			if(!hdr.isEmpty()) {
				hdr.forEach(builder::withHeader);
			}
			
			//if(newList!=null) {
			if(originalValues!=null) {
				// ripristino
				//TransportUtils.removeRawObject(hdr, HttpConstants.ACCEPT);
				hdr.put(HttpConstants.ACCEPT, originalValues);
			}
		}
		
		if(request.getParameters()!=null && !request.getParameters().isEmpty()) {
			request.getParameters().forEach(builder::withQueryParam);
		}
		if (request.getCookies() != null && !request.getCookies().isEmpty()) {
			Cookie cookie = request.getCookies().get(0);
			String cookiesValue = cookie.getName() + HttpConstants.COOKIE_NAME_VALUE_SEPARATOR + cookie.getValue();
						
			for (int i = 1; i < request.getCookies().size(); i++) {
				cookie = request.getCookies().get(i);
				cookiesValue = cookiesValue + HttpConstants.COOKIE_SEPARATOR+" " + cookie.getName() + HttpConstants.COOKIE_NAME_VALUE_SEPARATOR + cookie.getValue();  
			}
			builder.withHeader(HttpConstants.COOKIE, cookiesValue);
		}
	                        
		if(content instanceof String) {
			builder.withBody((String) content);
		}
		else if(content instanceof byte[]) {
			builder.withBody((byte[]) content);
		}
		else if(content instanceof InputStream) {
			builder.withBody( (InputStream) content);
		}
		else if(content != null){
			throw new ProcessingException("Type '"+content.getClass().getName()+"' unsupported");
		}
		
		return builder.build();
	}
	
	
	/** 
	 * 
	 * 	Questo codice è copiato dall'OpenApiLoader di atlassian, utilizzato perchè il 
	 * 	resolverfully fa modifiche alla openApi che vanno a interferire con la validazione
	 */
	 // Adding this method to strip off the object type association applied by
    // io.swagger.v3.parser.util.ResolverFully (ln 410) where the operation sets
    // type field to "object" if type field is null. This causes issues for anyOf
    // and oneOf validations.
	
    protected static void removeTypeObjectAssociationWithOneOfAndAnyOfModels(final OpenAPI openAPI) {
        if (openAPI.getComponents() != null) {
            removeTypeObjectFromEachValue(openAPI.getComponents().getSchemas(), schema -> schema);
        }
    }

    private static <T> void removeTypeObjectFromEachValue(final Map<String, T> map, final Function<T, Object> function) {
        if (map != null) {
            map.values().forEach(it -> removeTypeObjectAssociationWithOneOfAndAnyOfFromSchema(function.apply(it)));
        }
    }

    private static void removeTypeObjectAssociationWithOneOfAndAnyOfFromSchema(final Object object) {
        if (object instanceof ObjectSchema) {
            removeTypeObjectFromEachValue(((ObjectSchema) object).getProperties(), schema -> schema);
        } else if (object instanceof ArraySchema) {
            removeTypeObjectAssociationWithOneOfAndAnyOfFromSchema(((ArraySchema) object).getItems());
        } else if (object instanceof ComposedSchema) {
            final ComposedSchema composedSchema = (ComposedSchema) object;
            composedSchema.setType(null);
        }
    }

    /**
     * Removes the Base64 pattern on the {@link OpenAPI} model.
     * <p>
     * If that pattern would stay on the model all fields of type string / byte would be validated twice. Once
     * with the {@link com.github.fge.jsonschema.keyword.validator.common.PatternValidator} and once with
     * the {@link com.atlassian.oai.validator.schema.format.Base64Attribute}.
     * To improve validation performance and memory footprint the pattern on string / byte fields will be
     * removed - so the PatternValidator will not be triggered for those kind of fields.
     *
     * @param openAPI the {@link OpenAPI} to correct
     */
    protected static void removeRegexPatternOnStringsOfFormatByte(final OpenAPI openAPI) {
        if (openAPI.getPaths() != null) {
            openAPI.getPaths().values().forEach(pathItem -> {
                pathItem.readOperations().forEach(operation -> {
                    excludeBase64PatternFromEachValue(operation.getResponses(), io.swagger.v3.oas.models.responses.ApiResponse::getContent);
                    if (operation.getRequestBody() != null) {
                        excludeBase64PatternFromSchema(operation.getRequestBody().getContent());
                    }
                    if (operation.getParameters() != null) {
                        operation.getParameters().forEach(it -> excludeBase64PatternFromSchema(it.getContent()));
                        operation.getParameters().forEach(it -> excludeBase64PatternFromSchema(it.getSchema()));
                    }
                });
            });
        }
        if (openAPI.getComponents() != null) {
            excludeBase64PatternFromEachValue(openAPI.getComponents().getResponses(), io.swagger.v3.oas.models.responses.ApiResponse::getContent);
            excludeBase64PatternFromEachValue(openAPI.getComponents().getRequestBodies(), RequestBody::getContent);
            excludeBase64PatternFromEachValue(openAPI.getComponents().getHeaders(), Header::getContent);
            excludeBase64PatternFromEachValue(openAPI.getComponents().getHeaders(), Header::getSchema);
            excludeBase64PatternFromEachValue(openAPI.getComponents().getParameters(), Parameter::getContent);
            excludeBase64PatternFromEachValue(openAPI.getComponents().getParameters(), Parameter::getSchema);
            excludeBase64PatternFromEachValue(openAPI.getComponents().getSchemas(), schema -> schema);
        }
    }

    private static <T> void excludeBase64PatternFromEachValue(final Map<String, T> map, final Function<T, Object> function) {
        if (map != null) {
            map.values().forEach(it -> excludeBase64PatternFromSchema(function.apply(it)));
        }
    }

    private static void excludeBase64PatternFromSchema(final Object object) {
        if (object instanceof Content) {
            excludeBase64PatternFromEachValue((Content) object, MediaType::getSchema);
        } else if (object instanceof ObjectSchema) {
            excludeBase64PatternFromEachValue(((ObjectSchema) object).getProperties(), schema -> schema);
        } else if (object instanceof ArraySchema) {
            excludeBase64PatternFromSchema(((ArraySchema) object).getItems());
        } else if (object instanceof StringSchema) {
            final StringSchema stringSchema = (StringSchema) object;
            // remove the pattern _only_ if it's a String / Byte field
            if ("byte".equals(stringSchema.getFormat())) {
                stringSchema.setPattern(null);
            }
        }
    }
	
}
