/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.openapi4j.core.model.v3.OAI3Context;
import org.openapi4j.core.model.v3.OAI3SchemaKeywords;
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
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.json.AbstractUtils;
import org.openspcoop2.utils.json.IJsonSchemaValidator;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
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
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.rest.AbstractApiValidator;
import org.openspcoop2.utils.rest.ApiFormats;
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
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;

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
	private OpenapiApi4jValidatorConfig openApi4jConfig;
	
	boolean onlySchemas = false;
	
	private Logger log;
	
	private final static String VALIDATION_STRUCTURE = "VALIDATION_STRUCTURE";
	
	@Override
	public void init(Logger log, Api api, ApiValidatorConfig config)
			throws ProcessingException {

		this.log = log;
		if(api == null)
			throw new ProcessingException("Api cannot be null");

		// la sincronizzazione sull'API serve per evitare che venga inizializzati più volte in maniera concorrente l'API
		synchronized (api) {
					
			this.api = api;
			Api apiRest = null;
			OpenapiApi openapiApi = null;
			OpenapiApiValidatorStructure apiValidatorStructure = null;
			if((api instanceof OpenapiApi)) {
				openapiApi = (OpenapiApi) this.api;
				apiRest = this.api;
				apiValidatorStructure = openapiApi.getValidationStructure();
			}
			else if(api instanceof Api) {
				apiRest = this.api;
				if(apiRest.containsKey(VALIDATION_STRUCTURE)) {
					apiValidatorStructure = (OpenapiApiValidatorStructure) apiRest.getVendorImpl(VALIDATION_STRUCTURE);
				}
				this.onlySchemas = true;
			}
			
			ApiName jsonValidatorAPI = null;
			boolean useOpenApi4j = false; // ottimizzazione per OpenAPI
			ADDITIONAL policyAdditionalProperties = config.getPolicyAdditionalProperties();
			if(config instanceof OpenapiApiValidatorConfig) {
				jsonValidatorAPI = ((OpenapiApiValidatorConfig)config).getJsonValidatorAPI();
				if(openapiApi!=null) {
					OpenapiApiValidatorConfig c = (OpenapiApiValidatorConfig) config;
					if(c.getOpenApi4JConfig()!=null) {
						useOpenApi4j = c.getOpenApi4JConfig().isUseOpenApi4J();
						if(useOpenApi4j) {
							this.openApi4jConfig = c.getOpenApi4JConfig();
						}
					}
				}
			} 
			if(jsonValidatorAPI==null) {
				jsonValidatorAPI = ApiName.NETWORK_NT;
			}
			
			try {
			
				if(useOpenApi4j) {
					
					// leggo JSON Node degli schemi
					
					JsonNode schemaNodeRoot = null;
					URL uriSchemaNodeRoot = null;
					Map<URL, JsonNode> schemaMap = null;
					String root = "file:/";
					if(apiValidatorStructure!=null && apiValidatorStructure.getNodeValidatorePrincipale()!=null && !apiValidatorStructure.getNodeValidatorePrincipale().isEmpty()) {
						for (String nome : apiValidatorStructure.getNodeValidatorePrincipale().keySet()) {
							if(root.equals(nome)) {
								schemaNodeRoot = apiValidatorStructure.getNodeValidatorePrincipale().get(nome);
								uriSchemaNodeRoot = new URL(root);
							}
							else {
								if(schemaMap==null) {
									schemaMap = new HashMap<URL, JsonNode>();
								}
								schemaMap.put(new URL(nome), apiValidatorStructure.getNodeValidatorePrincipale().get(nome));
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
							
							Map<String, String> attachments = new HashMap<String, String>();
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
							schemaNodeRoot = yamlUtils.getAsNode(apiRaw);
						}
						else {
							schemaNodeRoot = jsonUtils.getAsNode(apiRaw);
						}
						normalizeRefs(schemaNodeRoot);
						uriSchemaNodeRoot = new URL(root);
						
						if(readApiSchemas && api.getSchemas()!=null && api.getSchemas().size()>0) {
							
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
										schemaNodeInternal = yamlUtils.getAsNode(schema);
									}
								}
								if(schemaNodeInternal==null) {
									continue;
								}
								normalizeRefs(schemaNodeInternal);
								if(schemaMap==null) {
									schemaMap = new HashMap<URL, JsonNode>();
								}
								schemaMap.put(new URL(root+apiSchema.getName()), schemaNodeInternal);
								
							}
							
						}
					}
					
					// Costruisco OpenAPI3					
					OAI3Context context = new OAI3Context(uriSchemaNodeRoot, schemaNodeRoot, schemaMap);
					this.openApi4j = TreeUtil.json.convertValue(context.getBaseDocument(), OpenApi3.class);
					this.openApi4j.setContext(context);
					
					// Explicit validation of the API spec
					
					if(this.openApi4jConfig.isValidateAPISpec()) {
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
							List<String> refPath = engine.getStringMatchPattern(schemaNode, "$..$ref");
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
									//System.out.println("ADD SCHEMA PER ["+apiSchemaName+"#"+nameInternal+"] ["+new String(bout.toByteArray())+"]");
									validator.setSchema(schema, schemaValidationConfig, log);
									
									this.validatorMap.put(apiSchema.getName(), validator);
									
								}
								else {
		
									File tmp = File.createTempFile("validator", "."+ apiSchema.getType().name().toLowerCase());
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
									String apiSchemaName = (String) itSchemas.next();
									JsonNode schemaNode = tmpNode.get(apiSchemaName);
									JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
									List<String> refPath = engine.getStringMatchPattern(schemaNode, "$..$ref");
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
									List<String> refPath = engine.getStringMatchPattern(schemaNode, "$..$ref");
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
														schemaRebuild = schemaRebuild.replace(path, "file://"+file.getAbsolutePath());
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
							List<String> refPath = engine.getStringMatchPattern(schemaNode, "$..$ref");
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
												schemaRebuild = schemaRebuild.replace(path, "file://"+file.getAbsolutePath());
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
			
		}
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
		if(path.startsWith("http://") || path.startsWith("https://") || path.startsWith("file://")){	
			try {
				URL url = new URL(path);
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
				//System.out.println("REF ("+jsonNodeRef.getClass().getName()+") : "+jsonNodeRef);
				if(jsonNodeRef instanceof ObjectNode) {
					ObjectNode oNode = (ObjectNode) jsonNodeRef;
					JsonNode valore = oNode.get(OAI3SchemaKeywords.$REF);
					String ref = valore.asText();
					//System.out.println("VALORE:"+v);
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
		if(this.openApi4j==null) {
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
			OperationValidator val = new OperationValidator(this.openApi4j, pathOpenApi4j, operationOpenApi4j);
			
			if(httpEntity instanceof HttpBaseRequestEntity) {
				
				HttpBaseRequestEntity<?> httpRequest = (HttpBaseRequestEntity<?>) httpEntity;
				Request requestOpenApi4j = buildRequestOpenApi4j(httpRequest.getUrl(), httpRequest.getMethod().toString(), 
						httpRequest.getParametersQuery(), httpRequest.getCookies(), httpRequest.getParametersTrasporto(),
						httpRequest.getContent());
				//val.validatePath(requestOpenApi4j, vData); LA URL deve corrispondere al base path del server
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
				Response responseOpenApi4j = buildResponseOpenApi4j(response.getStatus(), response.getParametersTrasporto(), 
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
			Map<String, String> queryParams, List<Cookie> cookies, Map<String, String> headers, 
			Object content) throws ProcessingException {

		try {
		
			// Method & path
		    final DefaultRequest.Builder builder = new DefaultRequest.Builder(
		    		urlInvocazione,
		    		Request.Method.getMethod(method));
	
		    String queryString = null;
			if(queryParams!=null && !queryParams.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				Iterator<String> keys = queryParams.keySet().iterator();
				while (keys.hasNext()) {
					if(sb.length()>0) {
						sb.append("&");
					}
					String key = (String) keys.next();
					String value = (String) queryParams.get(key);
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
	
	private static Response buildResponseOpenApi4j(int status, Map<String, String> headers, Object content) throws ProcessingException {
		
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


	@Override
	public void validatePostConformanceCheck(HttpBaseEntity<?> httpEntity,
			ApiOperation operation, Object... args) throws ProcessingException,
	ValidatorException {
		
	}

	@Override
	public void validateValueAsType(String value, String type, ApiSchemaTypeRestriction typeRestriction)
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
					numberValue = new BigDecimal(v);
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("float".equalsIgnoreCase(type)){
				try{
					float v = Float.parseFloat(value);
					numberValue = new BigDecimal(v);
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
					numberValue = new BigDecimal(i);
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
						numberValue = new BigDecimal(d);
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
					DateUtils.validateDateTimeAsRFC3339_sec5_6(value);
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("date".equalsIgnoreCase(type)){
				try {
					DateUtils.validateDateAsRFC3339_sec5_6(value);
				}catch(Throwable e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
//			else if("time".equalsIgnoreCase(type)){
//				try {
//					DateUtils.validateTimeAsRFC3339_sec5_6(value);
//				}catch(Throwable e){
//					throw new ValidatorException(e.getMessage(),e);
//				}
//			}
			
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
							   numberValue.divide(typeRestriction.getMultipleOf(), 0, RoundingMode.UNNECESSARY);
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
						boolean found = false;
						StringBuilder sbList = new StringBuilder();
						for (Object o : typeRestriction.getEnumValues()) {
							if(o!=null) {
								String check = o.toString();
								if(sbList.length()>0) {
									sbList.append(",");
								}
								sbList.append(check);
								if(stringValue.equals(check)) {
									found = true;
									break;
								}
							}
						}
						if(!found) {
							throw new ValidatorException("Uncorrect enum value, expected: '"+sbList.toString()+"'");
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

	
}
