/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;
import org.openspcoop2.utils.rest.entity.HttpBaseEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseRequestEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseResponseEntity;
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
	private Map<String, IJsonSchemaValidator> validatorMap;
	private Map<String, File> fileSchema;
	
	@Override
	public void init(Logger log, Api api, ApiValidatorConfig config)
			throws ProcessingException {

		if(api == null)
			throw new ProcessingException("Api cannot be null");

		this.api = api;
		OpenapiApi openapiApi = null;
		if((api instanceof OpenapiApi)) {
			openapiApi = (OpenapiApi) this.api;
		}
		
		ApiName jsonValidatorAPI;
		if(config instanceof OpenapiApiValidatorConfig) {
			jsonValidatorAPI = ((OpenapiApiValidatorConfig)config).getJsonValidatorAPI();
		} else {
			jsonValidatorAPI = ApiName.NETWORK_NT;
		}
		
		try {
			
			this.validatorMap = new HashMap<>();
			
			
			// Verifico se gli schemi importati o gli elementi definition dentro l'interfaccia api, 
			// a loro volta importano altri schemi tramite il $ref standard di json schema
			// Se cosi non è non serve serializzarli su file system, la cui serializzazione è ovviamente più costosa in termini di performance.
			boolean existsRefInternal = false;
			
			
			
			
			/* *** Validatore Principale *** */
			
			Map<String, byte[]> schemiValidatorePrincipale = new HashMap<>();
			Map<String, JsonNode> nodeValidatorePrincipale = new HashMap<>();
 			
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
			
			
			
			
			
			// *** Gestione file importati ***
			
			HashMap<String, JsonNode> tmpNode = new HashMap<>();
			HashMap<String, byte[]> tmpByteArraySchema = new HashMap<>();
			HashMap<String, ApiSchemaType> tmpSchemaType = new HashMap<>();

			this.fileSchema = new HashMap<>();
			if(api.getSchemas()!=null && api.getSchemas().size()>0) {
				
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
						File tmp = File.createTempFile("validator", "."+ apiSchema.getType().name().toLowerCase());
						this.fileSchema.put(apiSchema.getName(), tmp);
						tmpNode.put(apiSchema.getName(), schemaNode);
						tmpByteArraySchema.put(apiSchema.getName(), schema);
						tmpSchemaType.put(apiSchema.getName(), apiSchema.getType());
					}
				}
				
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
												schemaValidationConfig.setAdditionalProperties(ADDITIONAL.DEFAULT);
												schemaValidationConfig.setPoliticaInclusioneTipi(POLITICA_INCLUSIONE_TIPI.DEFAULT);
												//System.out.println("ADD SCHEMA PER ["+apiSchemaName+"#"+nameInternal+"] ["+new String(bout.toByteArray())+"]");
												validator.setSchema(bout.toByteArray(), schemaValidationConfig);
												
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
			
			
			/* *** Validatore Principale *** */
			
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
					schemaValidationConfig.setAdditionalProperties(ADDITIONAL.DEFAULT);
					schemaValidationConfig.setPoliticaInclusioneTipi(POLITICA_INCLUSIONE_TIPI.DEFAULT);
					//System.out.println("ADD SCHEMA PER ["+schemaName+"] ["+new String(schema)+"]");
					validator.setSchema(schema, schemaValidationConfig);
					
					this.validatorMap.put(schemaName, validator);
				}
			}
			
			
					
		} catch(Throwable e) {
			try {
				this.close(log, api, config); // per chiudere eventuali risorse parzialmente inizializzate
			}catch(Throwable t) {}
			
			throw new ProcessingException(e);
		}
	}

	private String getRefPath(String ref) {
		if(ref.trim().startsWith("#")) {
			return null;
		}
		return ref.trim().substring(0, ref.indexOf("#"));
	}
	private String normalizePath(String path) throws ProcessingException {
		if(path.startsWith("http://") || path.startsWith("file://")){	
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
			bodyParameters = operation.getRequest().getBodyParameters();
		}
		else if(httpEntity instanceof HttpBaseResponseEntity<?>) {
				
			HttpBaseResponseEntity<?> response = (HttpBaseResponseEntity<?>) httpEntity;
			ApiResponse apiResponseFound = null;
			ApiResponse apiResponseDefault = null;
			
			for (ApiResponse apiResponse : operation.getResponses()) {
				if(apiResponse.isDefaultHttpReturnCode()) {
					apiResponseDefault = apiResponse;
				}
				if(response.getStatus() == apiResponse.getHttpReturnCode()){
					apiResponseFound = apiResponse;
					break;
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
		
		boolean required = false;
		if(bodyParameters!=null && !bodyParameters.isEmpty()) {
			for(ApiBodyParameter body: bodyParameters) {
				if(body.isRequired())
					required = true;
			}
		}
		
		if(required) {
			
			if(httpEntity.getContent() == null) {
				throw new ValidatorException("Body required ma non trovato");
			}
			
		}
		
		if(bodyParameters!=null && !bodyParameters.isEmpty()) {
			try {
				//System.out.println("==================== ("+httpEntity.getClass().getName()+") ====================");
				List<IJsonSchemaValidator> validatorLst = getValidatorList(operation, httpEntity);
				//System.out.println("SIZE: "+validatorLst.size());
				boolean valid = false;
				Exception exc = null;
				byte[] bytes = httpEntity.getContent().toString().getBytes();
				for(IJsonSchemaValidator validator: validatorLst) {
					ValidationResponse response = validator.validate(bytes);
					if(!ESITO.OK.equals(response.getEsito())) {
						exc = response.getException();
					} else {
						valid = true;
					}
				}
				
				if(!valid) {
					throw new ValidatorException(exc);
				}
			} catch (ValidationException e) {
				throw new ValidatorException(e);
			}
		}
	}

	/**
	 * @param operation
	 * @return
	 */
	private List<IJsonSchemaValidator> getValidatorList(ApiOperation operation, HttpBaseEntity<?> httpEntity) throws ValidatorException {
		List<IJsonSchemaValidator> lst = new ArrayList<>();
		
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
				
				if(this.validatorMap.containsKey(key)) {
					//System.out.println("ADD VALIDATORE ["+key+"]: ["+this.validatorMap.get(key)+"]");
					lst.add(this.validatorMap.get(key));
				}
			}
		}
		
		if(lst.isEmpty())
			throw new ValidatorException("Nessun validatore trovato");
		
		return lst;
	}

	@Override
	public void validatePostConformanceCheck(HttpBaseEntity<?> httpEntity,
			ApiOperation operation, Object... args) throws ProcessingException,
	ValidatorException {
		
	}

	@Override
	public void validateValueAsType(String value, String type)
			throws ProcessingException, ValidatorException {

		if(type!=null){
			type = type.trim();

			if("byte".equalsIgnoreCase(type) || "unsignedByte".equalsIgnoreCase(type)){
				try{
					Byte.parseByte(value);
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("char".equalsIgnoreCase(type)){
				if(value.length()>1){
					throw new ValidatorException("More than one character");
				}
			}
			else if("double".equalsIgnoreCase(type) || "decimal".equalsIgnoreCase(type)){
				try{
					Double.parseDouble(value);
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("float".equalsIgnoreCase(type)){
				try{
					Float.parseFloat(value);
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("int".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type) || 
					"positiveInteger".equalsIgnoreCase(type) || "negativeInteger".equalsIgnoreCase(type) ||
					"nonPositiveInteger".equalsIgnoreCase(type) || "nonNegativeInteger".equalsIgnoreCase(type) || 
					"unsignedInt".equalsIgnoreCase(type)){
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
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("long".equalsIgnoreCase(type) || "unsignedLong".equalsIgnoreCase(type)){
				try{
					long l = Long.parseLong(value);
					if("unsignedLong".equalsIgnoreCase(type)){
						if(l<0){
							throw new ValidatorException("Expected a unsigned value");
						}
					}
				}catch(Exception e){
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
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("boolean".equalsIgnoreCase(type)){
				try{
					Boolean.parseBoolean(value);
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
			else if("anyURI".equalsIgnoreCase(type)){
				try{
					new URI(value);
				}catch(Exception e){
					throw new ValidatorException(e.getMessage(),e);
				}
			}
		}

		// altri tipi non li valido per ora


	}

}
