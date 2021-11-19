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
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

	public static void main(String[] args) throws Exception {
		
		String tipo = null;
		if(args!=null && args.length>0) {
			tipo = args[0];
		}

		OpenAPILibrary openAPILibrary = OpenAPILibrary.json_schema;
		if(args!=null && args.length>1) {
			openAPILibrary = OpenAPILibrary.valueOf(args[1]);
		}
		
		openAPILibrary = OpenAPILibrary.swagger_request_validator;
		
		
		// ** FHIR
		{
			
			System.out.println("Test Schema#1 (testBigInterface.yaml) ...");
			
			URL url = TestOpenApi4j.class.getResource("/org/openspcoop2/utils/openapi/testBigInterface.yaml");
			
			long initT = DateManager.getTimeMillis();
			IApiReader apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
			ApiReaderConfig configOpenApi4j = new ApiReaderConfig();
			configOpenApi4j.setProcessInclude(false);
			apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), new File(url.toURI()), configOpenApi4j);
			Api apiOpenApi4j = apiReaderOpenApi4j.read();
			long endT = DateManager.getTimeMillis();
			long time = endT - initT;
			System.out.println("\tReader time:"+Utilities.convertSystemTimeIntoString_millisecondi(time, true));
			
			
			initT = DateManager.getTimeMillis();
			IApiValidator apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
			OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
			configO.setOpenApi4JConfig(new OpenapiApi4jValidatorConfig());
			configO.getOpenApi4JConfig().setOpenApiLibrary(openAPILibrary);
			configO.getOpenApi4JConfig().setValidateAPISpec(true);
			configO.getOpenApi4JConfig().setResolveFullyApiSpec(false);
			apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), apiOpenApi4j, configO);
			endT = DateManager.getTimeMillis();
			time = endT - initT;
			System.out.println("\tInit validator time:"+Utilities.convertSystemTimeIntoString_millisecondi(time, true));
			
			initT = DateManager.getTimeMillis();
			apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
			apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), apiOpenApi4j, configO);
			endT = DateManager.getTimeMillis();
			time = endT - initT;
			System.out.println("\tInit second validator time:"+Utilities.convertSystemTimeIntoString_millisecondi(time, true));
			
			
			String requestContent = "{\n"
					+ "    \"resourceType\": \"Invoice\",\n"
					+ "    \"identifier\": [\n"
					+ "        {\n"
					+ "            \"system\": \"http://dati.toscana.it/dataset/regioni\",\n"
					+ "            \"value\": \"090\"\n"
					+ "        },\n"
					+ "        {\n"
					+ "            \"system\": \"http://dati.toscana.it/dataset/aziende_sanitarie\",\n"
					+ "            \"value\": \"202\"\n"
					+ "        },\n"
					+ "        {\n"
					+ "            \"system\": \"http://dati.toscana.it/dataset/strutture_operative_skno\",\n"
					+ "            \"value\": \"09061301\"\n"
					+ "        },\n"
					+ "        {\n"
					+ "            \"value\": \"software A\"\n"
					+ "        },\n"
					+ "        {\n"
					+ "            \"type\": {\n"
					+ "                \"coding\": [\n"
					+ "                    {\n"
					+ "                        \"system\": \"http://dati.toscana.it/dataset/tipo-documento-spesa730\",\n"
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
					+ "                        \"system\": \"http://dati.toscana.it/dataset/ruolistruttura\",\n"
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
					+ "                        \"system\": \"http://dati.toscana.it/dataset/aliquotaIVA\",\n"
					+ "                        \"code\": \"22\"\n"
					+ "                    },\n"
					+ "                    {\n"
					+ "                        \"system\": \"http://dati.toscana.it/dataset/flagtipospesa\",\n"
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
					+ "                                \"system\": \"http://dati.toscana.it/dataset/tipospesa\",\n"
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
					+ "    ]\n"
					+ "}";
			
			String responseContent = "{\n"
					+ "  \"resourceType\": \"Invoice\",\n"
					+ "  \"id\": \"2688652\",\n"
					+ "  \"meta\": {\n"
					+ "    \"versionId\": \"1\",\n"
					+ "    \"lastUpdated\": \"2021-11-18T14:34:03.947+00:00\"\n"
					+ "  },\n"
					+ "  \"identifier\": [ {\n"
					+ "    \"system\": \"http://dati.toscana.it/dataset/regioni\",\n"
					+ "    \"value\": \"090\"\n"
					+ "  }, {\n"
					+ "    \"system\": \"http://dati.toscana.it/dataset/aziende_sanitarie\",\n"
					+ "    \"value\": \"202\"\n"
					+ "  }, {\n"
					+ "    \"system\": \"http://dati.toscana.it/dataset/strutture_operative_skno\",\n"
					+ "    \"value\": \"09061301\"\n"
					+ "  }, {\n"
					+ "    \"value\": \"software A\"\n"
					+ "  }, {\n"
					+ "    \"type\": {\n"
					+ "      \"coding\": [ {\n"
					+ "        \"system\": \"http://dati.toscana.it/dataset/tipo-documento-spesa730\",\n"
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
					+ "        \"system\": \"http://dati.toscana.it/dataset/ruolistruttura\",\n"
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
					+ "        \"system\": \"http://dati.toscana.it/dataset/aliquotaIVA\",\n"
					+ "        \"code\": \"22\"\n"
					+ "      }, {\n"
					+ "        \"system\": \"http://dati.toscana.it/dataset/flagtipospesa\",\n"
					+ "        \"code\": \"2\"\n"
					+ "      } ]\n"
					+ "    },\n"
					+ "    \"priceComponent\": [ {\n"
					+ "      \"type\": \"base\",\n"
					+ "      \"code\": {\n"
					+ "        \"coding\": [ {\n"
					+ "          \"system\": \"http://dati.toscana.it/dataset/tipospesa\",\n"
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
					+ "  } ]\n"
					+ "}";
			
			System.out.println("Test Schema#1.1 Valido risposta complessa..");
			{
				
				TextHttpResponseEntity validResponse = new TextHttpResponseEntity();
				validResponse.setUrl("http://hapi.fhir.org/baseR4/Invoice");	
				validResponse.setMethod(HttpRequestMethod.POST);
				validResponse.setStatus(201);
				validResponse.setContent(responseContent);
				Map<String, List<String>> parametersTrasporto = new HashMap<>();
				TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE,"application/fhir+json");
				validResponse.setContentType("application/fhir+json");
				validResponse.setHeaders(parametersTrasporto);
				apiValidatorOpenApi4j.validate(validResponse);
			}
			
			System.out.println("Nessun errore sollevato, ok!");
			
			
			System.out.println("Test Schema#1.1 Valido richiesta complessa..");
			{
				TextHttpRequestEntity validRequest = new TextHttpRequestEntity();
				validRequest.setUrl("http://hapi.fhir.org/baseR4/Invoice");	
				validRequest.setMethod(HttpRequestMethod.POST);
				validRequest.setContent(requestContent);
				validRequest.setContentType("application/fhir+json");
				Map<String, List<String>> parametersTrasporto = new HashMap<>();
				TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, "application/fhir+json");
				validRequest.setHeaders(parametersTrasporto);
				validRequest.setContentType("application/fhir+json");
				apiValidatorOpenApi4j.validate(validRequest);
				
				System.out.println("Nessun errore sollevato, ok!");
				
				System.out.println("Test Schema#1 (testBigInterface.yaml) ok");
			}
			
			
			
		}
		

	}

}
