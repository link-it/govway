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


package org.openspcoop2.utils.openapi.validator;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.json.JsonValidatorAPI.ApiName;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.ValidatorException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.entity.HttpBaseEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseRequestEntity;
import org.openspcoop2.utils.rest.entity.TextHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.TextHttpResponseEntity;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;


/**
 * Test
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class Test {

	// TODO: Aggiungere parametro per indicare la OpenApiLibrary
	public static void testValidation(URI uri, String baseUrl, String testName, ApiFormats format, boolean useOpenApi4j, ApiSchema ...apiSchemas) throws Exception {
		try {
	
			boolean testAdditionalProperties = !ApiFormats.SWAGGER_2.equals(format); // il parser dello swagger non legge l'additiona properties
			
			boolean addParameterTipizzati =  !ApiFormats.SWAGGER_2.equals(format); // aggiunti solo in openapi
			
			boolean bodyInDelete = ApiFormats.SWAGGER_2.equals(format); // openapi3 non lo permette
			
			IApiReader apiReader = ApiFactory.newApiReader(format);
			apiReader.init(LoggerWrapperFactory.getLogger(Test.class), new File(uri), new ApiReaderConfig(), apiSchemas);
			Api api = apiReader.read();
			IApiValidator apiValidator = ApiFactory.newApiValidator(format);
			OpenapiApiValidatorConfig config = new OpenapiApiValidatorConfig();
			config.setJsonValidatorAPI(ApiName.NETWORK_NT);
			if(useOpenApi4j) {
				config.setOpenApi4JConfig(new OpenapiApi4jValidatorConfig());
				config.getOpenApi4JConfig().setOpenApiLibrary(OpenAPILibrary.openapi4j); 	// OpenAPILibrary.swagger_request_validator; TODO:
			}
			apiValidator.init(LoggerWrapperFactory.getLogger(Test.class), api, config);
			try {
			
				System.out.println("["+testName+"] Test #1 (Richiesta GET con parametro path)");
				String testUrl1 = baseUrl + "/pets/2";
				HttpBaseEntity<?> httpEntity = new TextHttpRequestEntity();
				httpEntity.setMethod(HttpRequestMethod.GET);
				httpEntity.setUrl(testUrl1);	
				Map<String, List<String>> parametersTrasporto = new HashMap<>();
				List<String> headersValues = new ArrayList<String>();
				headersValues.add("aaa");
				parametersTrasporto.put("api_key", headersValues);
				httpEntity.setHeaders(parametersTrasporto);
				apiValidator.validate(httpEntity);	
				System.out.println("["+testName+"] Test #1 completato\n\n");
		
				System.out.println("["+testName+"] Test #2 (Richiesta GET senza parametri query ove richiesti)");
				String testUrl2 = baseUrl + "/pets/findByStatus";
				HttpBaseEntity<?> httpEntity2 = new TextHttpRequestEntity();
				httpEntity2.setMethod(HttpRequestMethod.GET);
				httpEntity2.setUrl(testUrl2);
				try {
					apiValidator.validate(httpEntity2);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				} catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					System.out.println("["+testName+"] Test #2 completato\n\n");
				}
				
				System.out.println("["+testName+"] Test #3 (Richiesta GET con parametri query)");
				Map<String, List<String>> parametersQuery = new HashMap<String, List<String>>();
				HttpBaseRequestEntity<?> httpEntity3 = new TextHttpRequestEntity();
				TransportUtils.setParameter(parametersQuery,"status", "available");
				if(addParameterTipizzati) {
					TransportUtils.setParameter(parametersQuery,"profiloRefInLineByStatus", "APIGateway");
					TransportUtils.setParameter(parametersQuery,"soggettoRefInLineByStatus", "PROVA");
					TransportUtils.setParameter(parametersQuery,"numeroLimitatoRefInLineByStatus", "130");
					
					TransportUtils.setParameter(parametersQuery,"profiloInLineByStatus", "SPCoop");
					TransportUtils.setParameter(parametersQuery,"soggettoInLineByStatus", "PROVA2");
					TransportUtils.setParameter(parametersQuery,"numeroLimitatoInLineByStatus", "150");
					
					TransportUtils.setParameter(parametersQuery,"profiloRef", "FatturaPA");
					TransportUtils.setParameter(parametersQuery,"soggettoRef", "PROVA3");
					TransportUtils.setParameter(parametersQuery,"esempioNumericoRef", "200");
					
					TransportUtils.setParameter(parametersQuery,"profilo", "eDelivery");
					TransportUtils.setParameter(parametersQuery,"soggetto", "PROVA4");
					TransportUtils.setParameter(parametersQuery,"esempioNumerico", "500");
				}
				httpEntity3.setParameters(parametersQuery);
				httpEntity3.setMethod(HttpRequestMethod.GET);
				httpEntity3.setUrl(testUrl2);
				apiValidator.validate(httpEntity3);		
				System.out.println("["+testName+"] Test #3 completato\n\n");
				
				if(addParameterTipizzati) {
					System.out.println("["+testName+"] Test #3-a (Richiesta GET con parametri query errati)");
					httpEntity3.getParameters().remove("profiloRefInLineByStatus");
					TransportUtils.setParameter(parametersQuery,"profiloRefInLineByStatus", "APIGatewayERRATO");
					try {
						apiValidator.validate(httpEntity3);
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					} catch(ValidatorException e) {
						System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
						if(useOpenApi4j) {
							String msgErroreAtteso = "profiloRefInLineByStatus: Value 'APIGatewayERRATO' is not defined in the schema. (code: 1006)";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"', trovato: "+e.getMessage());
							}
						}
						else {
							if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
								String msgErroreAtteso = "Invalid value 'APIGatewayERRATO' in query parameter 'profiloRefInLineByStatus' (expected type 'string'): Uncorrect enum value 'APIGatewayERRATO', expected: 'APIGateway,SPCoop,FatturaPA,eDelivery'";
								if(!e.getMessage().contains(msgErroreAtteso)) {
									throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"', trovato: "+e.getMessage());
								}
							}
						}
						System.out.println("["+testName+"] Test #3-a (Richiesta GET con parametri query errati) completato\n\n");
					}
					httpEntity3.getParameters().remove("profiloRefInLineByStatus");
					TransportUtils.setParameter(httpEntity3.getParameters(),"profiloRefInLineByStatus", "APIGateway");
					
					System.out.println("["+testName+"] Test #3-b (Richiesta GET con parametri query errati)");
					httpEntity3.getParameters().remove("soggettoInLineByStatus");
					TransportUtils.setParameter(parametersQuery,"soggettoInLineByStatus", "PROVA_PROVA");
					try {
						apiValidator.validate(httpEntity3);
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					} catch(ValidatorException e) {
						System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
						if(useOpenApi4j) {
							String msgErroreAtteso = "soggettoInLineByStatus: 'PROVA_PROVA' does not respect pattern '^[0-9A-Za-z]+$'. (code: 1025)";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"': "+e.getMessage());
							}
						}
						else {
							if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
								String msgErroreAtteso = "Invalid value 'PROVA_PROVA' in query parameter 'soggettoInLineByStatus' (expected type 'string'): Pattern match failed ('^[0-9A-Za-z]+$')";
								if(!e.getMessage().contains(msgErroreAtteso)) {
									throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"': "+e.getMessage());
								}
							}
						}
						System.out.println("["+testName+"] Test #3-b (Richiesta GET con parametri query errati) completato\n\n");
					}
					httpEntity3.getParameters().remove("soggettoInLineByStatus");
					TransportUtils.setParameter(httpEntity3.getParameters(),"soggettoInLineByStatus", "PROVA");
					
					System.out.println("["+testName+"] Test #3-c (Richiesta GET con parametri query errati)");
					httpEntity3.getParameters().remove("soggettoInLineByStatus");
					TransportUtils.setParameter(parametersQuery,"soggettoInLineByStatus", "P");
					try {
						apiValidator.validate(httpEntity3);
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					} catch(ValidatorException e) {
						System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
						if(useOpenApi4j) {
							String msgErroreAtteso = "soggettoInLineByStatus: Min length is '2', found '1'. (code: 1017)";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
						else {
							if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
								String msgErroreAtteso = "Invalid value 'P' in query parameter 'soggettoInLineByStatus' (expected type 'string'): Too short, expected min length '2'";
								if(!e.getMessage().contains(msgErroreAtteso)) {
									throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
								}
							}
						}
						System.out.println("["+testName+"] Test #3-c (Richiesta GET con parametri query errati) completato\n\n");
					}
					httpEntity3.getParameters().remove("soggettoInLineByStatus");
					TransportUtils.setParameter(httpEntity3.getParameters(),"soggettoInLineByStatus", "PROVA");
					
					System.out.println("["+testName+"] Test #3-d (Richiesta GET con parametri query errati)");
					httpEntity3.getParameters().remove("soggettoRef");
					String pLongValue = "P12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
					TransportUtils.setParameter(parametersQuery,"soggettoRef", pLongValue);
					try {
						apiValidator.validate(httpEntity3);
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					} catch(ValidatorException e) {
						System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
						if(useOpenApi4j) {
							String msgErroreAtteso = "soggettoRef: Max length is '255', found '291'. (code: 1012)";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
						else {
							if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
								String msgErroreAtteso = "Invalid value '"+pLongValue+"' in query parameter 'soggettoRef' (expected type 'string'): Too big, expected max length '255'";
								if(!e.getMessage().contains(msgErroreAtteso)) {
									throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
								}
							}
						}
						System.out.println("["+testName+"] Test #3-d (Richiesta GET con parametri query errati) completato\n\n");
					}
					httpEntity3.getParameters().remove("soggettoRef");
					TransportUtils.setParameter(httpEntity3.getParameters(),"soggettoRef", "PROVA");
					
					System.out.println("["+testName+"] Test #3-e (Richiesta GET con parametri query errati)");
					httpEntity3.getParameters().remove("esempioNumerico");
					TransportUtils.setParameter(parametersQuery,"esempioNumerico", "23");
					try {
						apiValidator.validate(httpEntity3);
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					} catch(ValidatorException e) {
						System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
						if(useOpenApi4j) {
							String msgErroreAtteso = "Minimum is '100', found '23'. (code: 1015)";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
						else {
							if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
								String msgErroreAtteso = "Invalid value '23' in query parameter 'esempioNumerico' (expected type 'int32'): Value lowest than the minimum '100'"; 
								if(!e.getMessage().contains(msgErroreAtteso)) {
									throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
								}
							}
						}
						System.out.println("["+testName+"] Test #3-e (Richiesta GET con parametri query errati) completato\n\n");
					}
					httpEntity3.getParameters().remove("esempioNumerico");
					TransportUtils.setParameter(httpEntity3.getParameters(),"esempioNumerico", "500");
					
					System.out.println("["+testName+"] Test #3-f (Richiesta GET con parametri query errati)");
					httpEntity3.getParameters().remove("esempioNumerico");
					TransportUtils.setParameter(parametersQuery,"esempioNumerico", "600");
					try {
						apiValidator.validate(httpEntity3);
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					} catch(ValidatorException e) {
						System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
						if(useOpenApi4j) {
							String msgErroreAtteso = "esempioNumerico: Excluded maximum is '600', found '600'. (code: 1009)";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
						else {
							if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
								String msgErroreAtteso = "Invalid value '600' in query parameter 'esempioNumerico' (expected type 'int32'): Value equals to the maximum '600' and exclusive maximum is enabled"; 
								if(!e.getMessage().contains(msgErroreAtteso)) {
									throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
								}
							}
						}
						System.out.println("["+testName+"] Test #3-f (Richiesta GET con parametri query errati) completato\n\n");
					}
					httpEntity3.getParameters().remove("esempioNumerico");
					TransportUtils.setParameter(httpEntity3.getParameters(),"esempioNumerico", "500");
					
					System.out.println("["+testName+"] Test #3-g (Richiesta GET con parametri query errati)");
					httpEntity3.getParameters().remove("esempioNumerico");
					TransportUtils.setParameter(parametersQuery,"esempioNumerico", "800");
					try {
						apiValidator.validate(httpEntity3);
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					} catch(ValidatorException e) {
						System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
						if(useOpenApi4j) {
							String msgErroreAtteso = "esempioNumerico: Maximum is '600', found '800'. (code: 1010)";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
						else if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "Invalid value '800' in query parameter 'esempioNumerico' (expected type 'int32'): Value higher than the maximum '600'"; 
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
						System.out.println("["+testName+"] Test #3-g (Richiesta GET con parametri query errati) completato\n\n");
					}
					httpEntity3.getParameters().remove("esempioNumerico");
					TransportUtils.setParameter(httpEntity3.getParameters(),"esempioNumerico", "500");
					
					System.out.println("["+testName+"] Test #3-h (Richiesta GET con parametri query errati)");
					httpEntity3.getParameters().remove("esempioNumerico");
					TransportUtils.setParameter(parametersQuery,"esempioNumerico", "55GG33");
					try {
						apiValidator.validate(httpEntity3);
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					} catch(ValidatorException e) {
						System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
						if(useOpenApi4j) {
							String msgErroreAtteso = "esempioNumerico: Value '55GG33' does not match format 'int32'. (code: 1007)";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
						else {
							if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
								String msgErroreAtteso = "Invalid value '55GG33' in query parameter 'esempioNumerico' (expected type 'int32'): For input string: \"55GG33\""; 
								if(!e.getMessage().contains(msgErroreAtteso)) {
									throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
								}
							}
						}
						System.out.println("["+testName+"] Test #3-h (Richiesta GET con parametri query errati) completato\n\n");
					}
					httpEntity3.getParameters().remove("esempioNumerico");
					TransportUtils.setParameter(httpEntity3.getParameters(),"esempioNumerico", "500");
				}
				
				// [request] Test senza import
				
				System.out.println("["+testName+"] Test #4 (Richiesta POST con body json corretto)");
				TextHttpRequestEntity httpEntity4 = new TextHttpRequestEntity();
				httpEntity4.setMethod(HttpRequestMethod.POST);
				httpEntity4.setUrl("/pets");
				httpEntity4.setContent("{\"name\" : \"aaa\",  \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"]}");
				setContentType("application/json",httpEntity4);
				apiValidator.validate(httpEntity4);	
				System.out.println("["+testName+"] Test #4 completato\n\n");
				
				System.out.println("["+testName+"] Test #4-empty (Richiesta POST senza body json)");
				TextHttpRequestEntity httpEntity4_empty = new TextHttpRequestEntity();
				httpEntity4_empty.setMethod(HttpRequestMethod.POST);
				httpEntity4_empty.setUrl("/pets");
				setContentType("application/json",httpEntity4_empty);
				try {
					apiValidator.validate(httpEntity4_empty);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "Body is required but none provided. (code: 200)";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						String msgErroreAtteso = "Required body undefined"; 
						if(!e.getMessage().equals(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					System.out.println("["+testName+"] Test #4-empty completato\n\n");
				}	
				
				System.out.println("["+testName+"] Test #4-contentTypeSconosciuto (Richiesta POST con contentType sconosciuto)");
				TextHttpRequestEntity httpEntity4_contentTypeSconosciuto = new TextHttpRequestEntity();
				httpEntity4_contentTypeSconosciuto.setMethod(HttpRequestMethod.POST);
				httpEntity4_contentTypeSconosciuto.setUrl("/pets");
				httpEntity4_contentTypeSconosciuto.setContent("{\"name\" : \"aaa\",  \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"]}");
				setContentType("application/ERRORE",httpEntity4_contentTypeSconosciuto);
				try {
					apiValidator.validate(httpEntity4_contentTypeSconosciuto);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "Content type 'application/ERRORE' is not allowed for body content";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						String msgErroreAtteso = "Content-Type 'application/ERRORE' unsupported"; 
						if(!e.getMessage().equals(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					System.out.println("["+testName+"] Test #4-contentTypeSconosciuto completato\n\n");
				}
	
				if(testAdditionalProperties) { 
					System.out.println("["+testName+"] Test #5-additionalProperties (Richiesta POST con body json errato )");
					TextHttpRequestEntity httpEntity5_additionalProperties = new TextHttpRequestEntity();
					httpEntity5_additionalProperties.setMethod(HttpRequestMethod.POST);
					httpEntity5_additionalProperties.setUrl("/pets");
					httpEntity5_additionalProperties.setContent("{\"name\" : \"aaa\", \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"], \"a\":\"b\"}");
					setContentType("application/json",httpEntity5_additionalProperties);
					try {
						apiValidator.validate(httpEntity5_additionalProperties);
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					} catch(ValidatorException e) {
						System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
						if(useOpenApi4j) {
							String msgErroreAtteso = "body: Additional property 'a' is not allowed.";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
						else {
							if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
								String msgErroreAtteso = "$.a: is not defined in the schema and the schema does not allow additional properties";
								if(!e.getMessage().contains(msgErroreAtteso)) {
									throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
								}
							}
						}
						System.out.println("["+testName+"] Test #5-additionalProperties completato\n\n");
					}
				}
				
				System.out.println("["+testName+"] Test #5-required (Richiesta POST con body json errato )");
				TextHttpRequestEntity httpEntity5_required = new TextHttpRequestEntity();
				httpEntity5_required.setMethod(HttpRequestMethod.POST);
				httpEntity5_required.setUrl("/pets");
				httpEntity5_required.setContent("{\"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"]}");
				setContentType("application/json",httpEntity5_required);
				try {
					apiValidator.validate(httpEntity5_required);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				} catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "body: Field 'name' is required.";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.name: is missing but it is required";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
					}
					System.out.println("["+testName+"] Test #5-required completato\n\n");
				}
				
				// [response] Test senza import
	
				System.out.println("["+testName+"] Test #6 (Risposta POST con body json corretto)");
				TextHttpResponseEntity httpEntity6 = new TextHttpResponseEntity();
				httpEntity6.setMethod(HttpRequestMethod.POST);
				httpEntity6.setUrl("/pets");
				httpEntity6.setContent("{\"name\" : \"aaa\",  \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"], \"id\" : 1 }");
				setContentType("application/json",httpEntity6);
				httpEntity6.setStatus(200);
				apiValidator.validate(httpEntity6);	
				System.out.println("["+testName+"] Test #6 completato\n\n");
				
				System.out.println("["+testName+"] Test #6-empty (Risposta POST senza body json)");
				TextHttpResponseEntity httpEntity6_empty = new TextHttpResponseEntity();
				httpEntity6_empty.setMethod(HttpRequestMethod.POST);
				httpEntity6_empty.setUrl("/pets");
				setContentType("application/json",httpEntity6_empty);
				httpEntity6_empty.setStatus(200);
				try{
					apiValidator.validate(httpEntity6_empty);	
				}catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "body: Null value is not allowed. (code: 1021)";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						String msgErroreAtteso = "Required body undefined"; 
						if(!e.getMessage().equals(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					System.out.println("["+testName+"] Test #6-empty completato\n\n");
				}
				
				System.out.println("["+testName+"] Test #6-contentTypeSconosciuto (Risposta POST con body json corretto)");
				TextHttpResponseEntity httpEntity6_contentTypeSconosciuto = new TextHttpResponseEntity();
				httpEntity6_contentTypeSconosciuto.setMethod(HttpRequestMethod.POST);
				httpEntity6_contentTypeSconosciuto.setUrl("/pets");
				httpEntity6_contentTypeSconosciuto.setContent("{\"name\" : \"aaa\",  \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"], \"id\" : 1 }");
				setContentType("application/ERRORE",httpEntity6_contentTypeSconosciuto);
				httpEntity6_contentTypeSconosciuto.setStatus(200);
				try {
					apiValidator.validate(httpEntity6_contentTypeSconosciuto);
				}catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "Content type 'application/ERRORE' is not allowed for body content.";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						String msgErroreAtteso = "Content-Type 'application/ERRORE' (http response status '200') unsupported"; 
						if(!e.getMessage().equals(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					System.out.println("["+testName+"] Test #6-empty completato\n\n");
				}
				System.out.println("["+testName+"] Test #6-contentTypeSconosciuto completato\n\n");
		
				System.out.println("["+testName+"] Test #6-contentTypeOkSenzaBody (Risposta POST senza body ma con content_type json corretto)");
				TextHttpResponseEntity httpEntity6_empty_ok = new TextHttpResponseEntity();
				httpEntity6_empty_ok.setMethod(HttpRequestMethod.POST);
				httpEntity6_empty_ok.setUrl("/pets");
				setContentType("application/json",httpEntity6_empty_ok);
				httpEntity6_empty_ok.setStatus(405);
				try{
					apiValidator.validate(httpEntity6_empty_ok);
				}catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "Content type 'application/json' is not allowed for body content. (code: 203)";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						String msgErroreAtteso = "Content-Type 'application/json' (http response status '405') unsupported"; 
						if(!e.getMessage().equals(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					System.out.println("["+testName+"] Test #6-contentTypeOkSenzaBody completato\n\n");
				}
				System.out.println("["+testName+"] Test #6 completato\n\n");
				
				System.out.println("["+testName+"] Test #6-error (Risposta POST senza body json corretto)");
				TextHttpResponseEntity httpEntity6_error = new TextHttpResponseEntity();
				httpEntity6_error.setMethod(HttpRequestMethod.POST);
				httpEntity6_error.setUrl("/pets");
				httpEntity6_error.setContent("{\"code\" : 345,  \"message\": \"esempio di errore\" }");
				if(ApiFormats.OPEN_API_3.equals(format)) {
					setContentType("application/problem+json",httpEntity6_error);
				}
				else {
					setContentType("application/json",httpEntity6_error);
				}
				httpEntity6_error.setStatus(500);
				apiValidator.validate(httpEntity6_error);	
				System.out.println("["+testName+"] Test #6-error completato\n\n");
				
				System.out.println("["+testName+"] Test #6-error-empty (Risposta POST senza body json)");
				TextHttpResponseEntity httpEntity6_error_empty = new TextHttpResponseEntity();
				httpEntity6_error_empty.setMethod(HttpRequestMethod.POST);
				httpEntity6_error_empty.setUrl("/pets");
				setContentType("application/json",httpEntity6_error_empty);
				httpEntity6_error_empty.setStatus(500);
				try{
					apiValidator.validate(httpEntity6_error_empty);	
				}catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "Content type 'application/json' is not allowed for body content. (code: 203)";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						String msgErroreAtteso = "Required body undefined"; 
						if(!e.getMessage().equals(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					System.out.println("["+testName+"] Test #6-error-empty completato\n\n");
				}
	
				if(testAdditionalProperties) { 
					System.out.println("["+testName+"] Test #7-additionalProperties (Risposta POST con body json errato)");
					TextHttpResponseEntity httpEntity7_additionalProperties = new TextHttpResponseEntity();
					httpEntity7_additionalProperties.setMethod(HttpRequestMethod.POST);
					httpEntity7_additionalProperties.setUrl("/pets");
					httpEntity7_additionalProperties.setContent("{\"name\" : \"aaa\", \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"], \"a\":\"b\", \"id\": 23}");
					setContentType("application/json",httpEntity7_additionalProperties);
					httpEntity7_additionalProperties.setStatus(200);
					try {
						apiValidator.validate(httpEntity7_additionalProperties);
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					} catch(ValidatorException e) {
						System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
						if(useOpenApi4j) {
							String msgErroreAtteso = "body: Additional property 'a' is not allowed.";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
						else {
							if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
								String msgErroreAtteso = "$.a: is not defined in the schema and the schema does not allow additional properties";
								if(!e.getMessage().contains(msgErroreAtteso)) {
									throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
								}
							}
						}
						System.out.println("["+testName+"] Test #7-additionalProperties completato\n\n");
					}
				}
				
				System.out.println("["+testName+"] Test #7-required (Risposta POST con body json errato)");
				TextHttpResponseEntity httpEntity7_required = new TextHttpResponseEntity();
				httpEntity7_required.setMethod(HttpRequestMethod.POST);
				httpEntity7_required.setUrl("/pets");
				httpEntity7_required.setContent("{\"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"], \"id\": 23}");
				setContentType("application/json",httpEntity7_required);
				httpEntity7_required.setStatus(200);
				try {
					apiValidator.validate(httpEntity7_required);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				} catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "body: Field 'name' is required.";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.name: is missing but it is required";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
					}
					System.out.println("["+testName+"] Test #7-required completato\n\n");
				}
				
				if(testAdditionalProperties) { 
					System.out.println("["+testName+"] Test #7-error-additionalProperties (Risposta POST con body json errato)");
					TextHttpResponseEntity httpEntity7_error_additionalProperties = new TextHttpResponseEntity();
					httpEntity7_error_additionalProperties.setMethod(HttpRequestMethod.POST);
					httpEntity7_error_additionalProperties.setUrl("/pets");
					httpEntity7_error_additionalProperties.setContent("{\"code\" : 345, \"message\": \"Esempio di errore\", \"a\":\"b\"}");
					setContentType("application/json",httpEntity7_error_additionalProperties);
					httpEntity7_error_additionalProperties.setStatus(400);
					try {
						apiValidator.validate(httpEntity7_error_additionalProperties);
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					} catch(ValidatorException e) {
						System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
						if(useOpenApi4j) {
							String msgErroreAtteso = "body: Additional property 'a' is not allowed.";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
						else {
							if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
								String msgErroreAtteso = "$.a: is not defined in the schema and the schema does not allow additional properties";
								if(!e.getMessage().contains(msgErroreAtteso)) {
									throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
								}
							}
						}
						System.out.println("["+testName+"] Test #7-error-additionalProperties completato\n\n");
					}
				}
				
				System.out.println("["+testName+"] Test #7-error-required (Risposta POST con body json errato)");
				TextHttpResponseEntity httpEntity7_error_required = new TextHttpResponseEntity();
				httpEntity7_error_required.setMethod(HttpRequestMethod.POST);
				httpEntity7_error_required.setUrl("/pets");
				httpEntity7_error_required.setContent("{\"message\": \"Esempio di errore\"}");
				setContentType("application/json",httpEntity7_error_required);
				httpEntity7_error_required.setStatus(400);
				try {
					apiValidator.validate(httpEntity7_error_required);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				} catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "body: Field 'code' is required.";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.code: is missing but it is required";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
					}
					System.out.println("["+testName+"] Test #7-error-required completato\n\n");
				}
	
				
				// [request] Test con import
				
				System.out.println("["+testName+"] Test #9 (Richiesta PUT con body json corretto)");
				TextHttpRequestEntity httpEntity9 = new TextHttpRequestEntity();
				httpEntity9.setMethod(HttpRequestMethod.PUT);
				httpEntity9.setUrl("/pets");
				httpEntity9.setContent("{\"name\" : \"aaa\",  \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"]}");
				setContentType("application/json",httpEntity9);
				apiValidator.validate(httpEntity9);
				System.out.println("["+testName+"] Test #9 completato\n\n");
				
				System.out.println("["+testName+"] Test #10-additionalProperties (Richiesta PUT con body json errato)");
				TextHttpRequestEntity httpEntity10_additionalProperties = new TextHttpRequestEntity();
				httpEntity10_additionalProperties.setMethod(HttpRequestMethod.PUT);
				httpEntity10_additionalProperties.setUrl("/pets");
				httpEntity10_additionalProperties.setContent("{\"name\" : \"aaa\", \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"], \"a\":\"b\"}");
				setContentType("application/json",httpEntity10_additionalProperties);
				try {
					apiValidator.validate(httpEntity10_additionalProperties);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				} catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "body: Additional property 'a' is not allowed.";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.a: is not defined in the schema and the schema does not allow additional properties";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
					}
					System.out.println("["+testName+"] Test #10-additionalProperties completato\n\n");
				}
				
				System.out.println("["+testName+"] Test #10-required (Richiesta PUT con body json errato)");
				TextHttpRequestEntity httpEntity10_required = new TextHttpRequestEntity();
				httpEntity10_required.setMethod(HttpRequestMethod.PUT);
				httpEntity10_required.setUrl("/pets");
				httpEntity10_required.setContent("{ \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"]}");
				setContentType("application/json",httpEntity10_required);
				try {
					apiValidator.validate(httpEntity10_required);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				} catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "body: Field 'name' is required.";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.name: is missing but it is required";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
					}
					System.out.println("["+testName+"] Test #10-required completato\n\n");
				}
				
				// [response] Test con import 
				
				System.out.println("["+testName+"] Test #11 (Risposta PUT con body json corretto)");
				TextHttpResponseEntity httpEntity11 = new TextHttpResponseEntity();
				httpEntity11.setMethod(HttpRequestMethod.PUT);
				httpEntity11.setUrl("/pets");
				httpEntity11.setContent("{\"name\" : \"aaa\",  \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"], \"id\" : 1 }");
				setContentType("application/json",httpEntity11);
				httpEntity11.setStatus(200);
				apiValidator.validate(httpEntity11);
				System.out.println("["+testName+"] Test #11 completato\n\n");
				
				System.out.println("["+testName+"] Test #11-error (Risposta PUT con body json corretto)");
				TextHttpResponseEntity httpEntity11_error = new TextHttpResponseEntity();
				httpEntity11_error.setMethod(HttpRequestMethod.PUT);
				httpEntity11_error.setUrl("/pets");
				httpEntity11_error.setContent("{\"code\" : 234,  \"message\": \"Esempio di errore\" }");
				setContentType("application/json",httpEntity11_error);
				httpEntity11_error.setStatus(400);
				apiValidator.validate(httpEntity11_error);	
				System.out.println("["+testName+"] Test #11-error completato\n\n");
	
				System.out.println("["+testName+"] Test #12-additionalProperties (Risposta PUT con body json errato)");
				TextHttpResponseEntity httpEntity12_additionalProperties = new TextHttpResponseEntity();
				httpEntity12_additionalProperties.setMethod(HttpRequestMethod.PUT);
				httpEntity12_additionalProperties.setUrl("/pets");
				httpEntity12_additionalProperties.setContent("{\"name\" : \"aaa\", \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"], \"id\" : 1 , \"a\":\"b\"}");
				setContentType("application/json",httpEntity12_additionalProperties);
				httpEntity12_additionalProperties.setStatus(200);
				try {
					apiValidator.validate(httpEntity12_additionalProperties);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				} catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "body: Additional property 'a' is not allowed.";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.a: is not defined in the schema and the schema does not allow additional properties";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
					}
					System.out.println("["+testName+"] Test #12-additionalProperties completato\n\n");
				}
				
				System.out.println("["+testName+"] Test #12-required (Risposta PUT con body json errato)");
				TextHttpResponseEntity httpEntity12_required = new TextHttpResponseEntity();
				httpEntity12_required.setMethod(HttpRequestMethod.PUT);
				httpEntity12_required.setUrl("/pets");
				httpEntity12_required.setContent("{\"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"], \"id\" : 1}");
				setContentType("application/json",httpEntity12_required);
				httpEntity12_required.setStatus(200);
				try {
					apiValidator.validate(httpEntity12_required);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				} catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "body: Field 'name' is required.";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.name: is missing but it is required";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
					}
					System.out.println("["+testName+"] Test #12-required completato\n\n");
				}
				
				System.out.println("["+testName+"] Test #12-error-additionalProperties (Risposta PUT con body json errato)");
				TextHttpResponseEntity httpEntity12_error_additionalProperties = new TextHttpResponseEntity();
				httpEntity12_error_additionalProperties.setMethod(HttpRequestMethod.PUT);
				httpEntity12_error_additionalProperties.setUrl("/pets");
				httpEntity12_error_additionalProperties.setContent("{\"code\" : 234, \"message\": \"Esempio di errore\", \"a\":\"b\"}");
				setContentType("application/json",httpEntity12_error_additionalProperties);
				httpEntity12_error_additionalProperties.setStatus(400);
				try {
					apiValidator.validate(httpEntity12_error_additionalProperties);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				} catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "body: Additional property 'a' is not allowed.";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.a: is not defined in the schema and the schema does not allow additional properties";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
					}
					System.out.println("["+testName+"] Test #12-error-additionalProperties completato\n\n");
				}
				
				System.out.println("["+testName+"] Test #12-error-required (Risposta PUT con body json errato)");
				TextHttpResponseEntity httpEntity12_error_required = new TextHttpResponseEntity();
				httpEntity12_error_required.setMethod(HttpRequestMethod.PUT);
				httpEntity12_error_required.setUrl("/pets");
				httpEntity12_error_required.setContent("{\"message\": \"Esempio di errore\"}");
				setContentType("application/json",httpEntity12_error_required);
				httpEntity12_error_required.setStatus(400);
				try {
					apiValidator.validate(httpEntity12_error_required);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				} catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "body: Field 'code' is required.";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.code: is missing but it is required";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
					}
					System.out.println("["+testName+"] Test #12-error-required completato\n\n");
				}
				
				
				// [request] Test con import interni

				System.out.println("["+testName+"] Test #13 (Richiesta DELETE con body json corretto)");
				TextHttpRequestEntity httpEntity13 = new TextHttpRequestEntity();
				httpEntity13.setMethod(HttpRequestMethod.DELETE);
				httpEntity13.setUrl("/pets");
				// La delete non puo' avere una richiesta in openapi3
				if(bodyInDelete) {
					httpEntity13.setContent("{\"name\" : \"aaa\",  \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"] }");
					setContentType("application/json",httpEntity13);
				}
				apiValidator.validate(httpEntity13);	
				System.out.println("["+testName+"] Test #13 completato\n\n");
				
				// La delete non puo' avere una richiesta in openapi3
				if(bodyInDelete) {
					System.out.println("["+testName+"] Test #14-additionalProperties (Richiesta DELETE con body json errato)");
					TextHttpRequestEntity httpEntity14_additionalProperties = new TextHttpRequestEntity();
					httpEntity14_additionalProperties.setMethod(HttpRequestMethod.DELETE);
					httpEntity14_additionalProperties.setUrl("/pets");
					httpEntity14_additionalProperties.setContent("{\"name\" : \"aaa\", \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"], \"a\":\"b\"}");
					setContentType("application/json",httpEntity14_additionalProperties);
					try {
						apiValidator.validate(httpEntity14_additionalProperties);
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					} catch(ValidatorException e) {
						System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.a: is not defined in the schema and the schema does not allow additional properties";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
						System.out.println("["+testName+"] Test #14-additionalProperties completato\n\n");
					}
					
					System.out.println("["+testName+"] Test #14-required (Richiesta DELETE con body json errato)");
					TextHttpRequestEntity httpEntity14_required = new TextHttpRequestEntity();
					httpEntity14_required.setMethod(HttpRequestMethod.DELETE);
					httpEntity14_required.setUrl("/pets");
					httpEntity14_required.setContent("{ \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"]}");
					setContentType("application/json",httpEntity14_required);
					try {
						apiValidator.validate(httpEntity14_required);
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					} catch(ValidatorException e) {
						System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.name: is missing but it is required";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
						System.out.println("["+testName+"] Test #14-required completato\n\n");
					}
				}
				
				// [response] Test con import 
				
				System.out.println("["+testName+"] Test #15 (Risposta DELETE con body json corretto)");
				TextHttpResponseEntity httpEntity15 = new TextHttpResponseEntity();
				httpEntity15.setMethod(HttpRequestMethod.DELETE);
				httpEntity15.setUrl("/pets");
				httpEntity15.setContent("{\"name\" : \"aaa\",  \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"], \"id\" : 1 }");
				setContentType("application/json",httpEntity15);
				httpEntity15.setStatus(200);
				apiValidator.validate(httpEntity15);	
				System.out.println("["+testName+"] Test #15 completato\n\n");
				
				System.out.println("["+testName+"] Test #15-error (Risposta DELETE con body json corretto)");
				TextHttpResponseEntity httpEntity15_error = new TextHttpResponseEntity();
				httpEntity15_error.setMethod(HttpRequestMethod.DELETE);
				httpEntity15_error.setUrl("/pets");
				httpEntity15_error.setContent("{\"code\" : 234,  \"message\": \"Esempio di errore\" }");
				setContentType("application/json",httpEntity15_error);
				httpEntity15_error.setStatus(400);
				apiValidator.validate(httpEntity15_error);	
				System.out.println("["+testName+"] Test #15-error completato\n\n");
	
				System.out.println("["+testName+"] Test #16-additionalProperties (Risposta DELETE con body json errato)");
				TextHttpResponseEntity httpEntity16_additionalProperties = new TextHttpResponseEntity();
				httpEntity16_additionalProperties.setMethod(HttpRequestMethod.DELETE);
				httpEntity16_additionalProperties.setUrl("/pets");
				httpEntity16_additionalProperties.setContent("{\"name\" : \"aaa\", \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"], \"id\" : 1 , \"a\":\"b\"}");
				setContentType("application/json",httpEntity16_additionalProperties);
				httpEntity16_additionalProperties.setStatus(200);
				try {
					apiValidator.validate(httpEntity16_additionalProperties);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				} catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "body: Additional property 'a' is not allowed.";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.a: is not defined in the schema and the schema does not allow additional properties";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
					}
					System.out.println("["+testName+"] Test #16 -additionalProperties completato\n\n");
				}
				
				System.out.println("["+testName+"] Test #16-required (Risposta DELETE con body json errato)");
				TextHttpResponseEntity httpEntity16_required = new TextHttpResponseEntity();
				httpEntity16_required.setMethod(HttpRequestMethod.DELETE);
				httpEntity16_required.setUrl("/pets");
				httpEntity16_required.setContent("{\"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"], \"id\" : 1}");
				setContentType("application/json",httpEntity16_required);
				httpEntity16_required.setStatus(200);
				try {
					apiValidator.validate(httpEntity16_required);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				} catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "body: Field 'name' is required.";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.name: is missing but it is required";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
					}
					System.out.println("["+testName+"] Test #16 -required completato\n\n");
				}
				
				System.out.println("["+testName+"] Test #16-error-additionalProperties (Risposta DELETE con body json errato)");
				TextHttpResponseEntity httpEntity16_error_additionalProperties = new TextHttpResponseEntity();
				httpEntity16_error_additionalProperties.setMethod(HttpRequestMethod.DELETE);
				httpEntity16_error_additionalProperties.setUrl("/pets");
				httpEntity16_error_additionalProperties.setContent("{\"code\" : 234, \"message\": \"Esempio di errore\", \"a\":\"b\"}");
				setContentType("application/json",httpEntity16_error_additionalProperties);
				httpEntity16_error_additionalProperties.setStatus(400);
				try {
					apiValidator.validate(httpEntity16_error_additionalProperties);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				} catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "body: Additional property 'a' is not allowed.";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.a: is not defined in the schema and the schema does not allow additional properties";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
					}
					System.out.println("["+testName+"] Test #16-error-additionalProperties completato\n\n");
				}
				
				System.out.println("["+testName+"] Test #16-error-required (Risposta DELETE con body json errato)");
				TextHttpResponseEntity httpEntity16_error_required = new TextHttpResponseEntity();
				httpEntity16_error_required.setMethod(HttpRequestMethod.DELETE);
				httpEntity16_error_required.setUrl("/pets");
				httpEntity16_error_required.setContent("{\"message\": \"Esempio di errore\"}");
				setContentType("application/json",httpEntity16_error_required);
				httpEntity16_error_required.setStatus(400);
				try {
					apiValidator.validate(httpEntity16_error_required);
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				} catch(ValidatorException e) {
					System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
					if(useOpenApi4j) {
						String msgErroreAtteso = "body: Field 'code' is required.";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"':"+e.getMessage());
						}
					}
					else {
						if(ApiName.NETWORK_NT.equals(config.getJsonValidatorAPI())) {
							String msgErroreAtteso = "$.code: is missing but it is required";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								throw new Exception("Errore: atteso messaggio di errore '"+msgErroreAtteso+"':"+e.getMessage());
							}
						}
					}
					System.out.println("["+testName+"] Test #16-error-required completato\n\n");
				}
				

			}finally {
				apiValidator.close(LoggerWrapperFactory.getLogger(Test.class), api, config);
			}
				
		} catch(Exception e) {
			System.err.println("["+testName+"] Errore durante l'esecuzione dei test: " + e.getMessage());
			e.printStackTrace(System.err);
			throw e;
		}
	}
	
	private static void setContentType(String contentType, HttpBaseEntity<?> httpEntity) {
		httpEntity.setContentType(contentType);
		if(httpEntity.getHeaders()==null) {
			httpEntity.setHeaders(new HashMap<String, List<String>>());
		}
		httpEntity.getHeaders().remove(HttpConstants.CONTENT_TYPE);
		httpEntity.getHeaders().remove(HttpConstants.CONTENT_TYPE.toUpperCase());
		httpEntity.getHeaders().remove(HttpConstants.CONTENT_TYPE.toLowerCase());
		List<String> l = new ArrayList<String>();
		l.add(contentType);
		httpEntity.getHeaders().put(HttpConstants.CONTENT_TYPE, l);
	}
	
}
