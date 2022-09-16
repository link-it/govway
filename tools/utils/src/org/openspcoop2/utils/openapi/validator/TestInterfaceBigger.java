/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.entity.TextHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.TextHttpResponseEntity;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * Test Interfacce grandi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TestInterfaceBigger {

	private static boolean logSystemOutError = true;
	
	public static void main(String[] args) throws Exception {
		

		// Con le altre librerie, la costruzione non riesce
		OpenAPILibrary openAPILibrary = OpenAPILibrary.swagger_request_validator;
		if(args!=null && args.length>0) {
			openAPILibrary = OpenAPILibrary.valueOf(args[0]);
		}
		
		boolean mergeSpec = false;
		if(args!=null && args.length>1) {
			mergeSpec = Boolean.valueOf(args[1]);
		}
		
		// fix per evitare troppo output su jenkins:
		logSystemOutError = !OpenAPILibrary.json_schema.equals(openAPILibrary);
		
		
		// ** FHIR
		{
			
			System.out.println("Test Schema#1 (testBigInterface.yaml) ...");
			
			URL url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/testBigInterface.yaml");
			
			long initT = DateManager.getTimeMillis();
			IApiReader apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
			ApiReaderConfig configOpenApi4j = new ApiReaderConfig();
			configOpenApi4j.setProcessInclude(false);
			apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi4j);
			Api apiOpenApi4j = apiReaderOpenApi4j.read();
			long endT = DateManager.getTimeMillis();
			long time = endT - initT;
			
			long maxAtteso = -1;
			switch (openAPILibrary) {
			case json_schema:
			case openapi4j:
				maxAtteso = 2000; // 2 secondi (jenkins)
				break;
			case swagger_request_validator:
				maxAtteso = 2000; // 2 secondi (jenkins)
				break;
			}	
			System.out.println("\tReader time:"+Utilities.convertSystemTimeIntoString_millisecondi(time, true));
			if(time>maxAtteso) {
				throw new Exception("Atteso un tempo inferiore a '"+maxAtteso+"'ms, trovato '"+time+"'ms");
			}
			
			initT = DateManager.getTimeMillis();
			IApiValidator apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
			OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
			configO.setEmitLogError(logSystemOutError);
			configO.setOpenApiValidatorConfig(new OpenapiLibraryValidatorConfig());
			configO.getOpenApiValidatorConfig().setOpenApiLibrary(openAPILibrary);
			configO.getOpenApiValidatorConfig().setValidateAPISpec(true);
			configO.getOpenApiValidatorConfig().setMergeAPISpec(mergeSpec);
			configO.getOpenApiValidatorConfig().setSwaggerRequestValidator_ResolveFullyApiSpec(true); // !!!!!!!!!!!! con opzione disabilitata va in out of memory
			apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi4j, configO);
			endT = DateManager.getTimeMillis();
			time = endT - initT;
			
			maxAtteso = -1;
			switch (openAPILibrary) {
			case json_schema:
			case openapi4j:
				maxAtteso = Long.MAX_VALUE;
				break;
			case swagger_request_validator:
				maxAtteso = 5000; // 5 secondi (jenkins)
				break;
			}			
			System.out.println("\tInit validator time:"+Utilities.convertSystemTimeIntoString_millisecondi(time, true));
			if(time>maxAtteso) {
				throw new Exception("Atteso un tempo inferiore a '"+maxAtteso+"'ms, trovato '"+time+"'ms");
			}
			
			initT = DateManager.getTimeMillis();
			apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
			apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi4j, configO);
			endT = DateManager.getTimeMillis();
			time = endT - initT;
			System.out.println("\tInit second validator time:"+Utilities.convertSystemTimeIntoString_millisecondi(time, true));
			if(time>maxAtteso) {
				throw new Exception("Atteso un tempo inferiore a '"+maxAtteso+"'ms, trovato '"+time+"'ms");
			}
			
			
			
			String requestContent = 
					"    \"resourceType\": \"Invoice\",\n"
					+ "    \"identifier\": [\n"
					+ "        {\n"
					+ "            \"system\": \"http://dati.ente_esempio.it/dataset/regioni\",\n"
					+ "            \"value\": \"090\"\n"
					+ "        },\n"
					+ "        {\n"
					+ "            \"system\": \"http://dati.ente_esempio.it/dataset/aziende_sanitarie\",\n"
					+ "            \"value\": \"202\"\n"
					+ "        },\n"
					+ "        {\n"
					+ "            \"system\": \"http://dati.ente_esempio.it/dataset/strutture_operative_skno\",\n"
					+ "            \"value\": \"09061301\"\n"
					+ "        },\n"
					+ "        {\n"
					+ "            \"value\": \"software A\"\n"
					+ "        },\n"
					+ "        {\n"
					+ "            \"type\": {\n"
					+ "                \"coding\": [\n"
					+ "                    {\n"
					+ "                        \"system\": \"http://dati.ente_esempio.it/dataset/tipo-documento-spesa730\",\n"
					+ "                        \"code\": \"F\",\n"
					+ "                        \"display\": \"Fattura\"\n"
					+ "                    }\n"
					+ "                ]\n"
					+ "            },\n"
					+ "            \"value\": \"2021/10/A000123\"\n"
					+ "        }\n"
					+ "    ],\n"
					+ "    \"status\": \"issued\",\n"
					+ "    \"recipient\": {\n"
					+ "        \"type\": \"Patient\",\n"
					+ "        \"identifier\": {\n"
					+ "            \"system\": \"http://hl7.it/sid/codiceFiscale\",\n"
					+ "            \"value\": \"AAABBB90C12D612X\"\n"
					+ "        },\n"
					+ "        \"display\": \"Anagrafe delle persone fisiche\"\n"
					+ "    },\n"
					+ "    \"date\": \"2021-09-23\",\n"
					+ "    \"participant\": [\n"
					+ "        {\n"
					+ "            \"role\": {\n"
					+ "                \"coding\": [\n"
					+ "                    {\n"
					+ "                        \"system\": \"http://dati.ente_esempio.it/dataset/ruolistruttura\",\n"
					+ "                        \"code\": \"1\",\n"
					+ "                        \"display\": \"Riferimento struttura per 730\"\n"
					+ "                    }\n"
					+ "                ]\n"
					+ "            },\n"
					+ "            \"actor\": {\n"
					+ "                \"type\": \"RelatedPerson\",\n"
					+ "                \"identifier\": {\n"
					+ "                    \"system\": \"http://hl7.it/sid/codiceFiscale\",\n"
					+ "                    \"value\": \"AAABBB90C12D612X\"\n"
					+ "                },\n"
					+ "                \"display\": \"Anagrafe delle persone fisiche\"\n"
					+ "            }\n"
					+ "        }\n"
					+ "    ],\n"
					+ "    \"issuer\": {\n"
					+ "        \"type\": \"Organization\",\n"
					+ "        \"identifier\": {\n"
					+ "            \"system\": \"http://hl7.it/sid/partitaIva\",\n"
					+ "            \"value\": \"09876543213\"\n"
					+ "        },\n"
					+ "        \"display\": \"Anagrafe delle strutture sanitarie\"\n"
					+ "    },\n"
					+ "    \"lineItem\": [\n"
					+ "        {\n"
					+ "            \"sequence\": 1,\n"
					+ "            \"chargeItemCodeableConcept\": {\n"
					+ "                \"coding\": [\n"
					+ "                    {\n"
					+ "                        \"system\": \"http://dati.ente_esempio.it/dataset/aliquotaIVA\",\n"
					+ "                        \"code\": \"22\"\n"
					+ "                    },\n"
					+ "                    {\n"
					+ "                        \"system\": \"http://dati.ente_esempio.it/dataset/flagtipospesa\",\n"
					+ "                        \"code\": \"2\"\n"
					+ "                    }\n"
					+ "                ]\n"
					+ "            },\n"
					+ "            \"priceComponent\": [\n"
					+ "                {\n"
					+ "                    \"type\": \"base\",\n"
					+ "                    \"code\": {\n"
					+ "                        \"coding\": [\n"
					+ "                            {\n"
					+ "                                \"system\": \"http://dati.ente_esempio.it/dataset/tipospesa\",\n"
					+ "                                \"code\": \"SR\"\n"
					+ "                            }\n"
					+ "                        ]\n"
					+ "                    },\n"
					+ "                    \"amount\": {\n"
					+ "                        \"value\": 122.00\n"
					+ "                    }\n"
					+ "                }\n"
					+ "            ]\n"
					+ "        }\n"
					+ "    ],\n"
					+ "    \"totalNet\": {\n"
					+ "        \"value\": 100.00\n"
					+ "    },\n"
					+ "    \"totalGross\": {\n"
					+ "        \"value\": 122.00\n"
					+ "    },\n"
					+ "    \"note\": [\n"
					+ "        {\n"
					+ "            \"time\": \"2021-09-20\",\n"
					+ "            \"text\": \"1\"\n"
					+ "        },\n"
					+ "        {\n"
					+ "            \"text\": \"SI\"\n"
					+ "        },\n"
					+ "        {\n"
					+ "            \"text\": \"0\"\n"
					+ "        }\n"
					+ "    ]\n";
			String requestValidContent = "{\n" + requestContent + "}";
			String requestInvalidContent = "{\n" + 
					"    \"resourceNonEsistente\": \"Invoice\",\n" +
					requestContent + "}";
						
			System.out.println("\n\nTest Schema#1.1 Validazione richiesta 'valida'...");
			{
				TextHttpRequestEntity validRequest = new TextHttpRequestEntity();
				validRequest.setUrl("http://hapi.fhir.org/baseR4/Invoice");	
				validRequest.setMethod(HttpRequestMethod.POST);
				validRequest.setContent(requestValidContent);
				validRequest.setContentType("application/fhir+json");
				Map<String, List<String>> parametersTrasporto = new HashMap<>();
				TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, "application/fhir+json");
				validRequest.setHeaders(parametersTrasporto);
				validRequest.setContentType("application/fhir+json");
				apiValidatorOpenApi4j.validate(validRequest);
				
				System.out.println("Nessun errore sollevato, ok!");
				
				System.out.println("Test Schema#1 Validazione richiesta 'valida' ok");
			}
			
			
			System.out.println("\n\nTest Schema#1.2 Validazione richiesta 'invalida'...");
			{
				TextHttpRequestEntity validRequest = new TextHttpRequestEntity();
				validRequest.setUrl("http://hapi.fhir.org/baseR4/Invoice");	
				validRequest.setMethod(HttpRequestMethod.POST);
				validRequest.setContent(requestInvalidContent);
				validRequest.setContentType("application/fhir+json");
				Map<String, List<String>> parametersTrasporto = new HashMap<>();
				TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, "application/fhir+json");
				validRequest.setHeaders(parametersTrasporto);
				validRequest.setContentType("application/fhir+json");
				
				try {
					apiValidatorOpenApi4j.validate(validRequest);
					throw new Exception("Atteso errore di validazione");
				}catch(Exception e) {
					System.out.println("Test errore trovato: " + e.getMessage());
					String msgErroreAtteso = null;
					switch (openAPILibrary) {
					case json_schema:
						msgErroreAtteso = "aaaaaaaaa";
						break;
					case openapi4j:
						msgErroreAtteso = "aaaaaaaa";
						break;
					case swagger_request_validator:
						msgErroreAtteso = "[ERROR][REQUEST][POST http://hapi.fhir.org/baseR4/Invoice @body] Object instance has properties which are not allowed by the schema: [\"resourceNonEsistente\"]";
						break;
					}
					if(!e.getMessage().contains(msgErroreAtteso)) {
						throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
					}					
				}
				
				System.out.println("Test Schema#1.2 Validazione richiesta 'invalida' ok");
			}
			

			
			
			
			
			
			String responseContent = 
					"  \"resourceType\": \"Invoice\",\n"
					+ "  \"id\": \"2688652\",\n"
					+ "  \"meta\": {\n"
					+ "    \"versionId\": \"1\",\n"
					+ "    \"lastUpdated\": \"2021-11-18T14:34:03.947+00:00\"\n"
					+ "  },\n"
					+ "  \"identifier\": [ {\n"
					+ "    \"system\": \"http://dati.ente_esempio.it/dataset/regioni\",\n"
					+ "    \"value\": \"090\"\n"
					+ "  }, {\n"
					+ "    \"system\": \"http://dati.ente_esempio.it/dataset/aziende_sanitarie\",\n"
					+ "    \"value\": \"202\"\n"
					+ "  }, {\n"
					+ "    \"system\": \"http://dati.ente_esempio.it/dataset/strutture_operative_skno\",\n"
					+ "    \"value\": \"09061301\"\n"
					+ "  }, {\n"
					+ "    \"value\": \"software A\"\n"
					+ "  }, {\n"
					+ "    \"type\": {\n"
					+ "      \"coding\": [ {\n"
					+ "        \"system\": \"http://dati.ente_esempio.it/dataset/tipo-documento-spesa730\",\n"
					+ "        \"code\": \"F\",\n"
					+ "        \"display\": \"Fattura\"\n"
					+ "      } ]\n"
					+ "    },\n"
					+ "    \"value\": \"2021/10/A000123\"\n"
					+ "  } ],\n"
					+ "  \"status\": \"issued\",\n"
					+ "  \"recipient\": {\n"
					+ "    \"type\": \"Patient\",\n"
					+ "    \"identifier\": {\n"
					+ "      \"system\": \"http://hl7.it/sid/codiceFiscale\",\n"
					+ "      \"value\": \"AAABBB90C12D612X\"\n"
					+ "    },\n"
					+ "    \"display\": \"Anagrafe delle persone fisiche\"\n"
					+ "  },\n"
					+ "  \"date\": \"2021-09-23\",\n"
					+ "  \"participant\": [ {\n"
					+ "    \"role\": {\n"
					+ "      \"coding\": [ {\n"
					+ "        \"system\": \"http://dati.ente_esempio.it/dataset/ruolistruttura\",\n"
					+ "        \"code\": \"1\",\n"
					+ "        \"display\": \"Riferimento struttura per 730\"\n"
					+ "      } ]\n"
					+ "    },\n"
					+ "    \"actor\": {\n"
					+ "      \"type\": \"RelatedPerson\",\n"
					+ "      \"identifier\": {\n"
					+ "        \"system\": \"http://hl7.it/sid/codiceFiscale\",\n"
					+ "        \"value\": \"AAABBB90C12D612X\"\n"
					+ "      },\n"
					+ "      \"display\": \"Anagrafe delle persone fisiche\"\n"
					+ "    }\n"
					+ "  } ],\n"
					+ "  \"issuer\": {\n"
					+ "    \"type\": \"Organization\",\n"
					+ "    \"identifier\": {\n"
					+ "      \"system\": \"http://hl7.it/sid/partitaIva\",\n"
					+ "      \"value\": \"09876543213\"\n"
					+ "    },\n"
					+ "    \"display\": \"Anagrafe delle strutture sanitarie\"\n"
					+ "  },\n"
					+ "  \"lineItem\": [ {\n"
					+ "    \"sequence\": 1,\n"
					+ "    \"chargeItemCodeableConcept\": {\n"
					+ "      \"coding\": [ {\n"
					+ "        \"system\": \"http://dati.ente_esempio.it/dataset/aliquotaIVA\",\n"
					+ "        \"code\": \"22\"\n"
					+ "      }, {\n"
					+ "        \"system\": \"http://dati.ente_esempio.it/dataset/flagtipospesa\",\n"
					+ "        \"code\": \"2\"\n"
					+ "      } ]\n"
					+ "    },\n"
					+ "    \"priceComponent\": [ {\n"
					+ "      \"type\": \"base\",\n"
					+ "      \"code\": {\n"
					+ "        \"coding\": [ {\n"
					+ "          \"system\": \"http://dati.ente_esempio.it/dataset/tipospesa\",\n"
					+ "          \"code\": \"SR\"\n"
					+ "        } ]\n"
					+ "      },\n"
					+ "      \"amount\": {\n"
					+ "        \"value\": 122.00\n"
					+ "      }\n"
					+ "    } ]\n"
					+ "  } ],\n"
					+ "  \"totalNet\": {\n"
					+ "    \"value\": 100.00\n"
					+ "  },\n"
					+ "  \"totalGross\": {\n"
					+ "    \"value\": 122.00\n"
					+ "  },\n"
					+ "  \"note\": [ {\n"
					+ "    \"time\": \"2021-09-20\",\n"
					+ "    \"text\": \"1\"\n"
					+ "  }, {\n"
					+ "    \"text\": \"SI\"\n"
					+ "  }, {\n"
					+ "    \"text\": \"0\"\n"
					+ "  } ]\n";
			String responseValidContent = "{\n" + responseContent + "}";
			String responseInvalidContent = "{\n" + 
					"    \"resourceNonEsistente\": \"Invoice\",\n" +
					responseContent + "}";
			
			System.out.println("\n\nTest Schema#2.1 Valido risposta 'valida' ...");
			{
				
				TextHttpResponseEntity validResponse = new TextHttpResponseEntity();
				validResponse.setUrl("http://hapi.fhir.org/baseR4/Invoice");	
				validResponse.setMethod(HttpRequestMethod.POST);
				validResponse.setStatus(201);
				validResponse.setContent(responseValidContent);
				Map<String, List<String>> parametersTrasporto = new HashMap<>();
				TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE,"application/fhir+json");
				validResponse.setContentType("application/fhir+json");
				validResponse.setHeaders(parametersTrasporto);
				apiValidatorOpenApi4j.validate(validResponse);
				
				System.out.println("Nessun errore sollevato, ok!");
				
				System.out.println("Test Schema#2.1 Validazione risposta 'valida' ok");
			}
			
			System.out.println("\n\nTest Schema#2.2 Valido risposta 'invalida' ...");
			{
				
				TextHttpResponseEntity validResponse = new TextHttpResponseEntity();
				validResponse.setUrl("http://hapi.fhir.org/baseR4/Invoice");	
				validResponse.setMethod(HttpRequestMethod.POST);
				validResponse.setStatus(201);
				validResponse.setContent(responseInvalidContent);
				Map<String, List<String>> parametersTrasporto = new HashMap<>();
				TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE,"application/fhir+json");
				validResponse.setContentType("application/fhir+json");
				validResponse.setHeaders(parametersTrasporto);
				
				try {
					apiValidatorOpenApi4j.validate(validResponse);
					throw new Exception("Atteso errore di validazione");
				}catch(Exception e) {
					System.out.println("Test errore trovato: " + e.getMessage());
					String msgErroreAtteso = null;
					switch (openAPILibrary) {
					case json_schema:
						msgErroreAtteso = "aaaaaaaaa";
						break;
					case openapi4j:
						msgErroreAtteso = "aaaaaaaa";
						break;
					case swagger_request_validator:
						msgErroreAtteso = "[ERROR][RESPONSE][] Object instance has properties which are not allowed by the schema: [\"resourceNonEsistente\"]";
						break;
					}
					if(!e.getMessage().contains(msgErroreAtteso)) {
						throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
					}					
				}
				
				System.out.println("Test Schema#2.2 Validazione risposta 'invalida' ok");
			}
			
			
		}
		

	}

}
