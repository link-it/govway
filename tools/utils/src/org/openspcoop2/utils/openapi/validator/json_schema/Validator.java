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

package org.openspcoop2.utils.openapi.validator.json_schema;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.json.AbstractUtils;
import org.openspcoop2.utils.json.IJsonSchemaValidator;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.JsonPathException;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.json.JsonPathNotFoundException;
import org.openspcoop2.utils.json.JsonPathNotValidException;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig.POLITICA_INCLUSIONE_TIPI;
import org.openspcoop2.utils.json.JsonValidatorAPI.ApiName;
import org.openspcoop2.utils.json.ValidationException;
import org.openspcoop2.utils.json.ValidationResponse;
import org.openspcoop2.utils.json.ValidationResponse.ESITO;
import org.openspcoop2.utils.json.ValidatorFactory;
import org.openspcoop2.utils.json.YAMLUtils;
import org.openspcoop2.utils.openapi.OpenapiApi;
import org.openspcoop2.utils.openapi.OpenapiApiValidatorStructure;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.rest.AbstractApiValidator;
import org.openspcoop2.utils.rest.ApiValidatorConfig;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.ValidatorException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiBodyParameter;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.api.ApiReference;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;
import org.openspcoop2.utils.rest.entity.HttpBaseEntity;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.networknt.schema.SpecVersion.VersionFlag;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;

/**
 * JSON-Schema fallback api validator (NetworkNT / FGE).
 *
 * Valida i body JSON contro schema ricostruiti:
 *   - se l'Api è OpenapiApi, usa le definitions del documento OpenAPI (convertite in JSON-Schema draft-4)
 *   - se l'Api non è OpenapiApi (es. RegistryAPI), usa solo gli schemi esterni allegati via Api.getSchemas()
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Validator extends AbstractApiValidator implements IApiValidator {

	private static final String VALIDATION_STRUCTURE = "VALIDATION_STRUCTURE";

	private Map<String, IJsonSchemaValidator> validatorMap;
	private Map<String, File> fileSchema;
	private boolean onlySchemas = false;
	private ValidatorConfig config;

	private final org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("JsonSchemaValidator");

	@Override
	public void init(Logger log, Api api, ApiValidatorConfig rawConfig) throws ProcessingException {

		if (!(rawConfig instanceof ValidatorConfig))
			throw new ProcessingException("Config must be a json_schema config class");
		this.config = (ValidatorConfig) rawConfig;

		if (api == null)
			throw new ProcessingException("Api is null");
		this.api = api;
		this.log = log;

		ApiName jsonValidatorAPI = this.config.getJsonValidatorAPI();
		if (jsonValidatorAPI == null) {
			jsonValidatorAPI = ApiName.NETWORK_NT;
		}

		OpenapiApi openapiApi = (api instanceof OpenapiApi) ? (OpenapiApi) api : null;
		this.onlySchemas = (openapiApi == null);

		// Recupera la validationStructure cachata (OpenapiApi ha un getter tipizzato,
		// per gli altri tipi Api usiamo il vendor-impl bus).
		OpenapiApiValidatorStructure apiValidatorStructure = null;
		if (openapiApi != null) {
			apiValidatorStructure = openapiApi.getValidationStructure();
		} else if (api.containsKey(VALIDATION_STRUCTURE)) {
			apiValidatorStructure = (OpenapiApiValidatorStructure) api.getVendorImpl(VALIDATION_STRUCTURE);
		}

		SemaphoreLock lock = this.semaphore.acquireThrowRuntime("init");
		try {

			this.validatorMap = new HashMap<>();
			boolean existsRefInternal = false;

			Map<String, byte[]> schemiValidatorePrincipale = new HashMap<>();
			Map<String, JsonNode> nodeValidatorePrincipale = new HashMap<>();

			if (apiValidatorStructure != null) {
				schemiValidatorePrincipale = apiValidatorStructure.getSchemiValidatorePrincipale();
				nodeValidatorePrincipale = apiValidatorStructure.getNodeValidatorePrincipale();
			} else {
				if (openapiApi != null) {

					Map<String, Schema<?>> definitions = openapiApi.getAllDefinitions();
					String definitionString = Json.mapper().writeValueAsString(definitions);
					definitionString = definitionString.replaceAll("#/components/schemas", "#/definitions");
					for (String schemaName : definitions.keySet()) {

						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						bout.write("{".getBytes());
						String defOggetto = Json.mapper().writeValueAsString(definitions.get(schemaName));
						defOggetto = defOggetto.trim();
						defOggetto = defOggetto.replaceAll("#/components/schemas", "#/definitions");
						if (defOggetto.startsWith("{")) {
							defOggetto = defOggetto.substring(1);
						}
						if (defOggetto.endsWith("}")) {
							defOggetto = defOggetto.substring(0, defOggetto.length() - 1);
						}
						defOggetto = defOggetto.trim();
						bout.write(defOggetto.getBytes());
						bout.write(",".getBytes());
						bout.write("\"definitions\" : ".getBytes());
						bout.write(definitionString.getBytes());
						bout.write("}".getBytes());

						JSONUtils jsonUtils = JSONUtils.getInstance();
						JsonNode schemaNode = jsonUtils.getAsNode(bout.toByteArray());
						nodeValidatorePrincipale.put(schemaName, schemaNode);
						schemiValidatorePrincipale.put(schemaName, bout.toByteArray());

						// Verifico se gli elementi definition, a loro volta importano altri schemi
						JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
						List<String> refPath = getRefPath(engine, schemaNode);
						if (refPath != null && !refPath.isEmpty()) {
							for (String ref : refPath) {
								String path = this.getRefPath(ref);
								if (path != null) {
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
			if (apiValidatorStructure != null) {
				this.fileSchema = apiValidatorStructure.getFileSchema();
			} else {
				if (api.getSchemas() != null && !api.getSchemas().isEmpty()) {

					HashMap<String, JsonNode> tmpNode = new HashMap<>();
					HashMap<String, byte[]> tmpByteArraySchema = new HashMap<>();
					HashMap<String, ApiSchemaType> tmpSchemaType = new HashMap<>();

					for (ApiSchema apiSchema : api.getSchemas()) {

						if (!ApiSchemaType.JSON.equals(apiSchema.getType())
								&& !ApiSchemaType.YAML.equals(apiSchema.getType())) {
							continue;
						}
						byte[] schema = apiSchema.getContent();
						JsonNode schemaNode = null;
						AbstractUtils utils = null;
						JSONUtils jsonUtils = JSONUtils.getInstance();
						if (ApiSchemaType.JSON.equals(apiSchema.getType())) {
							utils = JSONUtils.getInstance();
							if (((JSONUtils) utils).isJson(schema)) {
								schemaNode = utils.getAsNode(schema);
							}
						} else {
							utils = YAMLUtils.getInstance();
							if (((YAMLUtils) utils).isYaml(schema)) {
								schemaNode = utils.getAsNode(schema);
							}
						}
						if (schemaNode == null) {
							continue;
						}

						/* ** Potenziale import from swagger/openapi che usa components/schemas invece di definitions ** */
						if (schemaNode instanceof TextNode) {
							ByteArrayOutputStream bout = new ByteArrayOutputStream();
							bout.write("{".getBytes());
							bout.write(schema);
							bout.write("}".getBytes());
							bout.flush();
							bout.close();
							schemaNode = utils.getAsNode(bout.toByteArray());
						}
						if (schemaNode instanceof ObjectNode objectNode) {

							boolean foundDefinitions = false;
							JsonNode nodeDefinitions = objectNode.get("definitions");
							if (nodeDefinitions instanceof ObjectNode) {
								foundDefinitions = true;
							}

							if (!foundDefinitions) {
								JsonNode nodeComponents = objectNode.get("components");
								if (nodeComponents instanceof ObjectNode objectNodeComponents) {
									JsonNode nodeSchemas = objectNodeComponents.get("schemas");
									if (nodeSchemas instanceof ObjectNode) {

										ObjectNode objectNodeDefinitions = (ObjectNode) utils.newObjectNode();
										objectNodeDefinitions.set("definitions", nodeSchemas);
										String schemaAsString;
										if (ApiSchemaType.YAML.equals(apiSchema.getType())) {
											// converto comunque in json poiché la validazione è supportata solo per json
											schemaAsString = jsonUtils.toString(objectNodeDefinitions);
										} else {
											schemaAsString = utils.toString(objectNodeDefinitions);
										}
										schemaAsString = schemaAsString.replaceAll("#/components/schemas",
												"#/definitions");
										schema = schemaAsString.getBytes();
										schemaNode = objectNodeDefinitions;
									} else {
										schema = null;
										schemaNode = null;
									}
								}
							}
						}

						if (schemaNode != null) {
							if (this.onlySchemas) {

								// Interfaccia Registro senza OpenAPI
								IJsonSchemaValidator validator = ValidatorFactory
										.newJsonSchemaValidator(jsonValidatorAPI);
								JsonSchemaValidatorConfig schemaValidationConfig = new JsonSchemaValidatorConfig();
								schemaValidationConfig.setVerbose(this.config.isVerbose());
								schemaValidationConfig
										.setAdditionalProperties(this.config.getPolicyAdditionalProperties());
								schemaValidationConfig.setPoliticaInclusioneTipi(POLITICA_INCLUSIONE_TIPI.DEFAULT);
								schemaValidationConfig.setEmitLogError(this.config.isEmitLogError());
								schemaValidationConfig.setJsonSchemaVersion(VersionFlag.V4);
								validator.setSchema(schema, schemaValidationConfig, log);

								this.validatorMap.put(apiSchema.getName(), validator);

							} else {
								File tmp = FileSystemUtilities.createTempFile("validator",
										"." + apiSchema.getType().name().toLowerCase());
								this.fileSchema.put(apiSchema.getName(), tmp);
								tmpNode.put(apiSchema.getName(), schemaNode);
								tmpByteArraySchema.put(apiSchema.getName(), schema);
								tmpSchemaType.put(apiSchema.getName(), apiSchema.getType());
							}
						}
					}

					if (!this.onlySchemas) {
						// Verifico se gli schemi importati, a loro volta importano altri schemi
						if (!existsRefInternal && !tmpByteArraySchema.isEmpty()) {
							Iterator<String> itSchemas = tmpByteArraySchema.keySet().iterator();
							while (itSchemas.hasNext()) {
								String apiSchemaName = itSchemas.next();
								JsonNode schemaNode = tmpNode.get(apiSchemaName);
								JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
								List<String> refPath = getRefPath(engine, schemaNode);
								if (refPath != null && !refPath.isEmpty()) {
									for (String ref : refPath) {
										String path = this.getRefPath(ref);
										if (path != null) {
											existsRefInternal = true;
											break;
										}
									}
								}
								if (existsRefInternal) {
									break;
								}
							}
						}

						if (existsRefInternal && !tmpByteArraySchema.isEmpty()) {
							Iterator<String> itSchemas = tmpByteArraySchema.keySet().iterator();
							while (itSchemas.hasNext()) {
								String apiSchemaName = itSchemas.next();
								JsonNode schemaNode = tmpNode.get(apiSchemaName);
								byte[] schemaContent = tmpByteArraySchema.get(apiSchemaName);

								JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
								List<String> refPath = getRefPath(engine, schemaNode);
								String schemaRebuild = null;
								if (refPath != null && !refPath.isEmpty()) {
									for (String ref : refPath) {
										String path = this.getRefPath(ref);
										if (path != null) {
											String normalizePath = this.normalizePath(path);
											if (normalizePath != null
													&& this.fileSchema.containsKey(normalizePath)) {
												if (schemaRebuild == null) {
													schemaRebuild = new String(schemaContent);
												}
												if (!schemaRebuild.contains(path)) {
													if (path.startsWith("./")) {
														path = path.substring(2);
													}
												}
												File file = this.fileSchema.get(normalizePath);
												while (schemaRebuild.contains(path)) {
													schemaRebuild = schemaRebuild.replace(path,
															org.openspcoop2.utils.Costanti.PROTOCOL_FILE_PREFIX
																	+ file.getAbsolutePath());
												}
											}
										}
									}
								}

								File f = this.fileSchema.get(apiSchemaName);
								if (schemaRebuild != null) {
									FileSystemUtilities.writeFile(f, schemaRebuild.getBytes());
									AbstractUtils utils;
									if (ApiSchemaType.JSON.equals(tmpSchemaType.get(apiSchemaName))) {
										utils = JSONUtils.getInstance();
									} else {
										utils = YAMLUtils.getInstance();
									}
									schemaNode = utils.getAsNode(schemaRebuild);
									tmpNode.put(apiSchemaName, schemaNode);
									tmpByteArraySchema.put(apiSchemaName, schemaRebuild.getBytes());
								} else {
									FileSystemUtilities.writeFile(f, schemaContent);
								}
							}

							if (!tmpByteArraySchema.isEmpty()) {
								itSchemas = tmpByteArraySchema.keySet().iterator();
								while (itSchemas.hasNext()) {
									String apiSchemaName = itSchemas.next();
									JsonNode schemaNode = tmpNode.get(apiSchemaName);
									if (schemaNode instanceof ObjectNode objectNode) {
										Iterator<String> it = objectNode.fieldNames();
										String name;
										while (it.hasNext()) {
											name = it.next();
											if ("definitions".equalsIgnoreCase(name)) {
												JsonNode internalNode = objectNode.get(name);
												if (internalNode instanceof ObjectNode internalObjectNode) {
													Iterator<String> itInternal = internalObjectNode.fieldNames();
													while (itInternal.hasNext()) {
														String nameInternal = itInternal.next();
														JsonNode typeDefinition = internalObjectNode
																.get(nameInternal);

														ByteArrayOutputStream bout = new ByteArrayOutputStream();
														bout.write("{".getBytes());
														String defOggetto = JSONUtils.getInstance()
																.toString(typeDefinition);
														defOggetto = defOggetto.trim();
														defOggetto = defOggetto.replaceAll("#/components/schemas",
																"#/definitions");
														if (defOggetto.startsWith("{")) {
															defOggetto = defOggetto.substring(1);
														}
														if (defOggetto.endsWith("}")) {
															defOggetto = defOggetto.substring(0,
																	defOggetto.length() - 1);
														}
														defOggetto = defOggetto.trim();
														bout.write(defOggetto.getBytes());
														bout.write(",".getBytes());
														bout.write("\"definitions\" : ".getBytes());
														String definitionStringSchema = JSONUtils.getInstance()
																.toString(internalNode);
														definitionStringSchema = definitionStringSchema
																.replaceAll("#/components/schemas", "#/definitions");
														bout.write(definitionStringSchema.getBytes());
														bout.write("}".getBytes());

														IJsonSchemaValidator validator = ValidatorFactory
																.newJsonSchemaValidator(jsonValidatorAPI);
														JsonSchemaValidatorConfig schemaValidationConfig = new JsonSchemaValidatorConfig();
														schemaValidationConfig
																.setVerbose(this.config.isVerbose());
														schemaValidationConfig.setAdditionalProperties(
																this.config.getPolicyAdditionalProperties());
														schemaValidationConfig.setPoliticaInclusioneTipi(
																POLITICA_INCLUSIONE_TIPI.DEFAULT);
														schemaValidationConfig
																.setEmitLogError(this.config.isEmitLogError());
														schemaValidationConfig
																.setJsonSchemaVersion(VersionFlag.V4);
														validator.setSchema(bout.toByteArray(),
																schemaValidationConfig, log);

														this.validatorMap.put(apiSchemaName + "#" + nameInternal,
																validator);
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
			if (apiValidatorStructure != null) {
				this.validatorMap = apiValidatorStructure.getValidatorMap();
			} else {
				if (schemiValidatorePrincipale != null && !schemiValidatorePrincipale.isEmpty()) {
					Iterator<String> it = schemiValidatorePrincipale.keySet().iterator();
					while (it.hasNext()) {
						String schemaName = it.next();
						byte[] schema = schemiValidatorePrincipale.get(schemaName);
						JsonNode schemaNode = nodeValidatorePrincipale.get(schemaName);

						JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
						List<String> refPath = getRefPath(engine, schemaNode);
						String schemaRebuild = null;
						if (refPath != null && !refPath.isEmpty()) {
							for (String ref : refPath) {
								String path = this.getRefPath(ref);
								if (path != null) {
									String normalizePath = this.normalizePath(path);
									if (normalizePath != null && this.fileSchema.containsKey(normalizePath)) {
										if (schemaRebuild == null) {
											schemaRebuild = new String(schema);
										}
										if (!schemaRebuild.contains(path)) {
											if (path.startsWith("./")) {
												path = path.substring(2);
											}
										}
										File file = this.fileSchema.get(normalizePath);
										while (schemaRebuild.contains(path)) {
											schemaRebuild = schemaRebuild.replace(path,
													org.openspcoop2.utils.Costanti.PROTOCOL_FILE_PREFIX
															+ file.getAbsolutePath());
										}
									}
								}
							}
						}
						if (schemaRebuild != null) {
							schema = schemaRebuild.getBytes();
						}

						IJsonSchemaValidator validator = ValidatorFactory
								.newJsonSchemaValidator(jsonValidatorAPI);
						JsonSchemaValidatorConfig schemaValidationConfig = new JsonSchemaValidatorConfig();
						schemaValidationConfig.setVerbose(this.config.isVerbose());
						schemaValidationConfig.setAdditionalProperties(this.config.getPolicyAdditionalProperties());
						schemaValidationConfig.setPoliticaInclusioneTipi(POLITICA_INCLUSIONE_TIPI.DEFAULT);
						schemaValidationConfig.setEmitLogError(this.config.isEmitLogError());
						schemaValidationConfig.setJsonSchemaVersion(VersionFlag.V4);
						validator.setSchema(schema, schemaValidationConfig, log);

						this.validatorMap.put(schemaName, validator);
					}
				}
			}

			// Salvo informazioni ricostruite
			if (apiValidatorStructure == null) {
				OpenapiApiValidatorStructure validationStructure = new OpenapiApiValidatorStructure();
				validationStructure.setSchemiValidatorePrincipale(schemiValidatorePrincipale);
				validationStructure.setNodeValidatorePrincipale(nodeValidatorePrincipale);
				validationStructure.setFileSchema(this.fileSchema);
				validationStructure.setValidatorMap(this.validatorMap);
				if (openapiApi != null) {
					openapiApi.setValidationStructure(validationStructure);
				} else {
					api.addVendorImpl(VALIDATION_STRUCTURE, validationStructure);
				}
			}

		} catch (Throwable e) {
			try {
				this.close(log, api, this.config);
			} catch (Throwable t) {
				// ignore
			}
			throw new ProcessingException(e);
		} finally {
			this.semaphore.release(lock, "init");
		}
	}

	/**
	 * Versione di getRefPath che opera su un JsonNode usando JsonPathExpressionEngine.
	 * Specifica del json_schema engine: non spostata in AbstractApiValidator perché gli altri
	 * engine non ne hanno bisogno e il base layer deve restare disaccoppiato da JsonPath.
	 */
	private List<String> getRefPath(JsonPathExpressionEngine engine, JsonNode schemaNode)
			throws JsonPathException, JsonPathNotValidException {
		List<String> l = null;
		try {
			l = engine.getStringMatchPattern(schemaNode, "$..$ref");
		} catch (JsonPathNotFoundException notFound) {
			// ignore
		}
		return l;
	}

	@Override
	public void close(Logger log, Api api, ApiValidatorConfig config) throws ProcessingException {
		if (this.fileSchema != null) {
			Iterator<String> itFiles = this.fileSchema.keySet().iterator();
			while (itFiles.hasNext()) {
				String key = itFiles.next();
				File file = this.fileSchema.get(key);
				if (!file.delete()) {
					log.error("Eliminazione file temporaneo [" + file.getAbsolutePath() + "] associato allo schema ["
							+ key + "] non riuscita");
				}
			}
		}
	}

	@Override
	public void validatePreConformanceCheck(HttpBaseEntity<?> httpEntity, ApiOperation operation, Object... args)
			throws ProcessingException, ValidatorException {

		List<ApiBodyParameter> bodyParameters = this.getBodyParameters(httpEntity, operation);

		// Controllo i campi required come controllo aggiuntivo
		boolean required = false;
		if (bodyParameters != null && !bodyParameters.isEmpty()) {
			for (ApiBodyParameter body : bodyParameters) {
				if (body.isRequired())
					required = true;
			}
		}
		if (required) {
			if (httpEntity.getContent() == null) {
				throw new ValidatorException("Required body undefined");
			}
		}

		// Validazione del body via JSON Schema
		if (bodyParameters != null && !bodyParameters.isEmpty()) {
			try {
				boolean isJson = httpEntity.getContentType() != null
						&& httpEntity.getContentType().toLowerCase().contains("json");
				if (isJson) {
					List<IJsonSchemaValidator> validatorLst = getValidatorList(operation, httpEntity);
					boolean valid = false;
					Exception exc = null;
					if (httpEntity.getContent() != null) {
						byte[] bytes = httpEntity.getContent().toString().getBytes();
						for (IJsonSchemaValidator validator : validatorLst) {
							ValidationResponse response = validator.validate(bytes);
							if (!ESITO.OK.equals(response.getEsito())) {
								exc = response.getException();
							} else {
								valid = true;
							}
						}
					} else {
						throw new ValidatorException("Content undefined");
					}

					if (!valid) {
						throw new ValidatorException(exc);
					}
				}
			} catch (ValidationException e) {
				throw new ValidatorException(e);
			}
		}
	}

	private List<IJsonSchemaValidator> getValidatorList(ApiOperation operation, HttpBaseEntity<?> httpEntity)
			throws ValidatorException {
		List<IJsonSchemaValidator> lst = new ArrayList<>();

		if (this.onlySchemas) {
			if (this.validatorMap != null) {
				lst.addAll(this.validatorMap.values());
			}
		} else {

			List<ApiBodyParameter> bodyParameters = this.getBodyParameters(httpEntity, operation);

			if (bodyParameters != null && !bodyParameters.isEmpty()) {
				for (ApiBodyParameter body : bodyParameters) {

					String key;
					if (body.getElement() instanceof ApiReference apiRef) {
						key = apiRef.getSchemaRef() + "#" + apiRef.getType();
					} else {
						key = body.getElement().toString();
					}

					if (this.validatorMap != null && this.validatorMap.containsKey(key)) {
						lst.add(this.validatorMap.get(key));
					}
				}
			}

		}

		if (lst.isEmpty())
			throw new ValidatorException("Validator not found");

		return lst;
	}
}
