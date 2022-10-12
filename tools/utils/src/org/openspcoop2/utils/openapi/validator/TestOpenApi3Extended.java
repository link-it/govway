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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.ParseWarningException;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.ValidatorException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;
import org.openspcoop2.utils.rest.entity.BinaryHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.BinaryHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.Cookie;
import org.openspcoop2.utils.rest.entity.DocumentHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.ElementHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseRequestEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseResponseEntity;
import org.openspcoop2.utils.rest.entity.InputStreamHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.InputStreamHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.TextHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.TextHttpResponseEntity;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test (Estende i test già effettuati in TestOpenApi3)
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TestOpenApi3Extended {

	private static boolean logSystemOutError = true;

	public static void main(String[] args) throws Exception {
		
		
		OpenAPILibrary openAPILibrary = OpenAPILibrary.openapi4j;
		if(args!=null && args.length>0) {
			openAPILibrary = OpenAPILibrary.valueOf(args[0]);
		}
		
		boolean mergeSpec = true;
		if(args!=null && args.length>1) {
			mergeSpec = Boolean.valueOf(args[1]);
		}
		
		// fix per evitare troppo output su jenkins:
		logSystemOutError = !OpenAPILibrary.json_schema.equals(openAPILibrary);
		


		// *** TEST per il Parser e validazione dello schema *** //
		
		{
			System.out.println("Test Schema#1 (openapi.yaml) [Elementi aggiuntivi come 'allowEmptyValue'] ...");
			
			URL url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/openapi.yaml");
			
			IApiReader apiReaderOpenApi = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
			ApiReaderConfig configOpenApi = new ApiReaderConfig();
			configOpenApi.setProcessInclude(false);
			apiReaderOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi);
			Api apiOpenApi = apiReaderOpenApi.read();
			
			try {
				apiOpenApi.validate();
			}catch(ProcessingException pe) {
				pe.printStackTrace(System.out);
				throw new Exception(" Documento contenente errori: "+pe.getMessage(), pe);
			}catch(ParseWarningException warning) {
				//warning.printStackTrace(System.out);
				System.out.println("Documento contenente anomalie: "+warning.getMessage());
			}
			
			IApiValidator apiValidatorOpenApi = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
			OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
			configO.setEmitLogError(logSystemOutError);
			configO.setOpenApiValidatorConfig(new OpenapiLibraryValidatorConfig());
			configO.getOpenApiValidatorConfig().setOpenApiLibrary(openAPILibrary);
			configO.getOpenApiValidatorConfig().setValidateAPISpec(true);
			configO.getOpenApiValidatorConfig().setMergeAPISpec(mergeSpec);
			apiValidatorOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi, configO);
					
			System.out.println("Test Schema#1 (openapi.yaml) [Elementi aggiuntivi come 'allowEmptyValue'] ok");
			
			System.out.println("Test Schema#2 (allegati.yaml) [Discriminator non presente o non required Step 1/2] ...");
			
			if( openAPILibrary == OpenAPILibrary.openapi4j ){
			
				url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/parser.yaml");
				
				apiReaderOpenApi = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
				apiReaderOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi);
				apiOpenApi = apiReaderOpenApi.read();
				
				try {
					apiOpenApi.validate();
					throw new Exception("Atteso errore");
				}catch(ProcessingException e) {
					String msgErrore1 = "components.schemas.Pet.discriminator: The discriminator 'pet_type' is not a property of this schema (code: 134)";
					if(!e.getMessage().contains(msgErrore1)) {
						throw new Exception("Errore atteso '"+msgErrore1+"' non rilevato",e);
					}
					String msgErrore2 = "components.schemas.Pet.discriminator: The discriminator 'pet_type' is required in this schema (code: 135)";
					if(!e.getMessage().contains(msgErrore2)) {
						throw new Exception("Errore atteso '"+msgErrore2+"' non rilevato",e);
					}
					String msgErrore3 = "components.schemas.Pet2.properties.pet.discriminator: The discriminator 'pet_type' is not required or not a property of the allOf schemas (code: 133)";
					if(!e.getMessage().contains(msgErrore3)) {
						throw new Exception("Errore atteso '"+msgErrore3+"' non rilevato",e);
					}
					String msgErrore4 = "components.schemas.Pet3.properties.pet.discriminator: The discriminator 'pet_type' is not required or not a property of the allOf schemas (code: 133)";
					if(!e.getMessage().contains(msgErrore4)) {
						throw new Exception("Errore atteso '"+msgErrore4+"' non rilevato",e);
					}
					String msgErrore5 = "components.schemas.Pet5.properties.pet.discriminator: The discriminator 'pet_type' is not required or not a property of the allOf schemas (code: 133)";
					if(!e.getMessage().contains(msgErrore5)) {
						throw new Exception("Errore atteso '"+msgErrore5+"' non rilevato",e);
					}
					String msgErrore6 = "components.schemas.Pet6.properties.pet.discriminator: The discriminator 'pet_type' is not required or not a property of the allOf schemas (code: 133)";
					if(!e.getMessage().contains(msgErrore6)) {
						throw new Exception("Errore atteso '"+msgErrore6+"' non rilevato",e);
					}
					String msgErrore7 = "components.schemas.Pet7.properties.pet.discriminator: The discriminator 'pet_type' is not required or not a property of the allOf schemas (code: 133)";
					if(!e.getMessage().contains(msgErrore7)) {
						throw new Exception("Errore atteso '"+msgErrore7+"' non rilevato",e);
					}
					String msgErrore8 = "components.schemas.Pet8.properties.pet.discriminator: The discriminator 'pet_type' is not required or not a property of the allOf schemas (code: 133)";
					if(!e.getMessage().contains(msgErrore8)) {
						throw new Exception("Errore atteso '"+msgErrore8+"' non rilevato",e);
					}
//					pe.printStackTrace(System.out);
//					throw new Exception(" Documento contenente errori: "+pe.getMessage(), pe);
				}catch(ParseWarningException warning) {
					//warning.printStackTrace(System.out);
					System.out.println("Documento contenente anomalie: "+warning.getMessage());
				}
				
				apiValidatorOpenApi = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
				try {
					apiValidatorOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi, configO);
					throw new Exception("Atteso errore");
				}catch(Exception e) {
					String msgErrore1 = "components.schemas.Pet.discriminator: The discriminator 'pet_type' is not a property of this schema (code: 134)";
					if(!e.getMessage().contains(msgErrore1)) {
						throw new Exception("Errore atteso '"+msgErrore1+"' non rilevato",e);
					}
					String msgErrore2 = "components.schemas.Pet.discriminator: The discriminator 'pet_type' is required in this schema (code: 135)";
					if(!e.getMessage().contains(msgErrore2)) {
						throw new Exception("Errore atteso '"+msgErrore2+"' non rilevato",e);
					}
					String msgErrore3 = "components.schemas.Pet2.properties.pet.discriminator: The discriminator 'pet_type' is not required or not a property of the allOf schemas (code: 133)";
					if(!e.getMessage().contains(msgErrore3)) {
						throw new Exception("Errore atteso '"+msgErrore3+"' non rilevato",e);
					}
					String msgErrore4 = "components.schemas.Pet3.properties.pet.discriminator: The discriminator 'pet_type' is not required or not a property of the allOf schemas (code: 133)";
					if(!e.getMessage().contains(msgErrore4)) {
						throw new Exception("Errore atteso '"+msgErrore4+"' non rilevato",e);
					}
					String msgErrore5 = "components.schemas.Pet5.properties.pet.discriminator: The discriminator 'pet_type' is not required or not a property of the allOf schemas (code: 133)";
					if(!e.getMessage().contains(msgErrore5)) {
						throw new Exception("Errore atteso '"+msgErrore5+"' non rilevato",e);
					}
					String msgErrore6 = "components.schemas.Pet6.properties.pet.discriminator: The discriminator 'pet_type' is not required or not a property of the allOf schemas (code: 133)";
					if(!e.getMessage().contains(msgErrore6)) {
						throw new Exception("Errore atteso '"+msgErrore6+"' non rilevato",e);
					}
					String msgErrore7 = "components.schemas.Pet7.properties.pet.discriminator: The discriminator 'pet_type' is not required or not a property of the allOf schemas (code: 133)";
					if(!e.getMessage().contains(msgErrore7)) {
						throw new Exception("Errore atteso '"+msgErrore7+"' non rilevato",e);
					}
					String msgErrore8 = "components.schemas.Pet8.properties.pet.discriminator: The discriminator 'pet_type' is not required or not a property of the allOf schemas (code: 133)";
					if(!e.getMessage().contains(msgErrore8)) {
						throw new Exception("Errore atteso '"+msgErrore8+"' non rilevato",e);
					}
				}
			}
			else {
				System.out.println("Skipped test per libreria "+openAPILibrary);
			}
			
			System.out.println("Test Schema#2 (allegati.yaml) [Discriminator non presente o non required Step 1/2] ok");
			
			System.out.println("Test Schema#3 (parser2.yaml) [Discriminator non presente o non required Step 2/2] ...");
			
			if( openAPILibrary == OpenAPILibrary.openapi4j ){
			
				url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/parser2.yaml");
				
				apiReaderOpenApi = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
				apiReaderOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi);
				apiOpenApi = apiReaderOpenApi.read();
				
				try {
					apiOpenApi.validate();
					throw new Exception("Atteso errore");
				}catch(ProcessingException e) {
					String msgErrore = "components.schemas.Pet4.properties.pet.discriminator: The discriminator 'pet_type' is required in this schema (code: 135)";
					if(!e.getMessage().contains(msgErrore)) {
						throw new Exception("Errore atteso '"+msgErrore+"' non rilevato",e);
					}
				}catch(ParseWarningException warning) {
					//warning.printStackTrace(System.out);
					System.out.println("Documento contenente anomalie: "+warning.getMessage());
				}
				
				apiValidatorOpenApi = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
				try {
					apiValidatorOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi, configO);
					throw new Exception("Atteso errore");
				}catch(Exception e) {
					String msgErrore = "components.schemas.Pet4.properties.pet.discriminator: The discriminator 'pet_type' is required in this schema (code: 135)";
					if(!e.getMessage().contains(msgErrore)) {
						throw new Exception("Errore atteso '"+msgErrore+"' non rilevato",e);
					}
				}
				
			}else {
				System.out.println("Skipped test per libreria "+openAPILibrary);
			}
			
			System.out.println("Test Schema#3 (parser2.yaml) [Discriminator non presente o non required Step 2/2] ok");
			
			
			System.out.println("Test Schema#4 (default.yaml) [Valori default non coerenti con il tipo] ...");
			
			if( openAPILibrary == OpenAPILibrary.openapi4j ){
			
				url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/default.yaml");
				
				apiReaderOpenApi = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
				apiReaderOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi);
				apiOpenApi = apiReaderOpenApi.read();
				
				try {
					apiOpenApi.validate();
				}catch(ProcessingException pe) {
					pe.printStackTrace(System.out);
					throw new Exception(" Documento contenente errori: "+pe.getMessage(), pe);
				}catch(ParseWarningException warning) {
					//warning.printStackTrace(System.out);
					System.out.println("Documento contenente anomalie: "+warning.getMessage());
				}
				
				apiValidatorOpenApi = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
				try {
					apiValidatorOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi, configO);
				}catch(Exception e) {
					throw new Exception("Errore non atteso (org.openapi4j.parser.validation.ValidationContext.convertDefaultStringValueInPrimitiveType="+org.openapi4j.parser.validation.ValidationContext.convertDefaultStringValueInPrimitiveType+"): "+e.getMessage(),e);
				}
				
				// disabilito patch
				org.openapi4j.parser.validation.ValidationContext.convertDefaultStringValueInPrimitiveType=false;
				try {
				
					apiReaderOpenApi = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
					apiReaderOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi);
					apiOpenApi = apiReaderOpenApi.read();
					
					apiValidatorOpenApi = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
					try {
						apiValidatorOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi, configO);
						throw new Exception("Atteso errore");
					}catch(Exception e) {
						
						String erroreInteger1 = "default: Value '1' is incompatible with schema type 'integer' (code: 138)";
						String erroreInteger2 = "default: Value '32' is incompatible with schema type 'integer/int32' (code: 138)";
						String erroreInteger3 = "default: Value '3147483647' is incompatible with schema type 'integer/int64' (code: 138)";
						
						String erroreNumber1 = "default: Value '1.2' is incompatible with schema type 'number' (code: 138)";
						String erroreNumber2 = "default: Value '2.3' is incompatible with schema type 'number/double' (code: 138)";
						String erroreNumber3 = "default: Value '2.3' is incompatible with schema type 'number/float' (code: 138)";
						
						String erroreBoolean = "default: Value 'true' is incompatible with schema type 'boolean' (code: 138)";
											
						if(!e.getMessage().contains(erroreInteger1)) {
							throw new Exception("Errore atteso '"+erroreInteger1+"' non rilevato in :"+e.getMessage(),e);
						}
						if(!e.getMessage().contains(erroreInteger2)) {
							throw new Exception("Errore atteso '"+erroreInteger2+"' non rilevato in :"+e.getMessage(),e);
						}
						if(!e.getMessage().contains(erroreInteger3)) {
							throw new Exception("Errore atteso '"+erroreInteger3+"' non rilevato in :"+e.getMessage(),e);
						}
						
						if(!e.getMessage().contains(erroreNumber1)) {
							throw new Exception("Errore atteso '"+erroreNumber1+"' non rilevato in :"+e.getMessage(),e);
						}
						if(!e.getMessage().contains(erroreNumber2)) {
							throw new Exception("Errore atteso '"+erroreNumber2+"' non rilevato in :"+e.getMessage(),e);
						}
						if(!e.getMessage().contains(erroreNumber3)) {
							throw new Exception("Errore atteso '"+erroreNumber3+"' non rilevato in :"+e.getMessage(),e);
						}
						
						if(!e.getMessage().contains(erroreBoolean)) {
							throw new Exception("Errore atteso '"+erroreBoolean+"' non rilevato in :"+e.getMessage(),e);
						}
					}
					
				}finally {
					// riabilito patch
					org.openapi4j.parser.validation.ValidationContext.convertDefaultStringValueInPrimitiveType=true;
				}
				
			}else {
				System.out.println("Skipped test per libreria "+openAPILibrary);
			}
			
			System.out.println("Test Schema#4 (default.yaml) [Valori default non coerenti con il tipo] ok");
			
			
			System.out.println("Test Schema#5 (info.yaml) [Elementi Info] ...");
			
			if( openAPILibrary == OpenAPILibrary.openapi4j ){
			
				org.openapi4j.parser.validation.v3.OpenApi3Validator.VALIDATE_URI_REFERENCE_AS_URL = false;
				
				url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/info.yaml");
				
				apiReaderOpenApi = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
				configOpenApi = new ApiReaderConfig();
				configOpenApi.setProcessInclude(false);
				apiReaderOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi);
				apiOpenApi = apiReaderOpenApi.read();
				
				try {
					apiOpenApi.validate();
				}catch(ProcessingException pe) {
					pe.printStackTrace(System.out);
					throw new Exception(" Documento contenente errori: "+pe.getMessage(), pe);
				}catch(ParseWarningException warning) {
					//warning.printStackTrace(System.out);
					System.out.println("Documento contenente anomalie: "+warning.getMessage());
				}
				
				apiValidatorOpenApi = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
				apiValidatorOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi, configO);
				
			}
									
			System.out.println("Test Schema#5 (info.yaml) [Elementi Info] ok");
			
			
			System.out.println("Test Schema#6 (openapi_all_methods.yaml) [Verifica che tutti i metodi HTTP siano caricati] ...");
			
			url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/openapi_all_methods.yaml");
				
			apiReaderOpenApi = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
			configOpenApi = new ApiReaderConfig();
			configOpenApi.setProcessInclude(false);
			apiReaderOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi);
			apiOpenApi = apiReaderOpenApi.read();
				
			try {
				apiOpenApi.validate();
			}catch(ProcessingException pe) {
				pe.printStackTrace(System.out);
				throw new Exception(" Documento contenente errori: "+pe.getMessage(), pe);
			}catch(ParseWarningException warning) {
				//warning.printStackTrace(System.out);
				System.out.println("Documento contenente anomalie: "+warning.getMessage());
			}
				
			apiValidatorOpenApi = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
			apiValidatorOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi, configO);
					
			List<HttpRequestMethod> metodiHttpd = new ArrayList<HttpRequestMethod>();
			for (ApiOperation op : apiOpenApi.getOperations()) {
				metodiHttpd.add(op.getHttpMethod());
			}
			System.out.println("Metodi: "+metodiHttpd);
			for (HttpRequestMethod httpRequestMethod : HttpRequestMethod.values()) {
				if(HttpRequestMethod.LINK.equals(httpRequestMethod)
						||
						HttpRequestMethod.UNLINK.equals(httpRequestMethod)) 
				{
					continue; // non supportato da openapi
				}
				if(!metodiHttpd.contains(httpRequestMethod)) {
					throw new Exception("Metodo '"+httpRequestMethod+"' non rilevato");
				}
			}
			
			System.out.println("Test Schema#6 (openapi_all_methods.yaml) [Verifica che tutti i metodi HTTP siano caricati] ok");
			
		} 
			
		
		
		
		// *** TEST per la validazione delle richieste *** //
		
		URL url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/allegati.yaml");
		
		ApiSchema apiSchemaYaml = new ApiSchema("teamdigitale-openapi_definitions.yaml", 
				Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/service/schemi/standard/teamdigitale-openapi_definitions.yaml")), ApiSchemaType.YAML);
		
		//String baseUri = "http://petstore.swagger.io/api";
		String baseUri = ""; // non va definita
		
		IApiReader apiReaderOpenApi = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		ApiReaderConfig configOpenApi = new ApiReaderConfig();
		configOpenApi.setProcessInclude(false);
		apiReaderOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi, apiSchemaYaml);
		Api apiOpenApi = apiReaderOpenApi.read();
			
		try {
			apiOpenApi.validate();
		}catch(ProcessingException pe) {
			pe.printStackTrace(System.out);
			throw new Exception(" Documento contenente errori: "+pe.getMessage(), pe);
		}catch(ParseWarningException warning) {
			//warning.printStackTrace(System.out);
			System.out.println("Documento contenente anomalie: "+warning.getMessage());
		}
		
		IApiValidator apiValidatorOpenApi = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
		OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
		configO.setEmitLogError(logSystemOutError);
		configO.setOpenApiValidatorConfig(new OpenapiLibraryValidatorConfig());
		configO.getOpenApiValidatorConfig().setOpenApiLibrary(openAPILibrary);
		configO.getOpenApiValidatorConfig().setValidateAPISpec(true);
		configO.getOpenApiValidatorConfig().setMergeAPISpec(mergeSpec);
		apiValidatorOpenApi.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi, configO);
				
		
		byte [] pdf = Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test.pdf"));
		
		String xml = "<prova>Hello World</prova>";
		Document docXml = XMLUtils.getInstance().newDocument(xml.getBytes());
		Element elementXml = docXml.getDocumentElement();
		
		
		
		
		

		
		System.out.println("Test #1 (Richiesta POST con parametro /documenti/mixed/send)");
		String testUrl1 = baseUri+"/documenti/mixed/send";
		TextHttpRequestEntity httpEntity = new TextHttpRequestEntity();
		httpEntity.setMethod(HttpRequestMethod.POST);
		httpEntity.setUrl(testUrl1);	
		Map<String, List<String>> parametersTrasporto = new HashMap<>();
		TransportUtils.addHeader(parametersTrasporto,"api_key", "aaa");
		TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
		httpEntity.setHeaders(parametersTrasporto);
		httpEntity.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		String json = "{\"mittente\":\"Mittente\",\"destinatario\":\"EnteDestinatario\",\"procedimento\":\"DescrizioneGenerica ...\",\"allegati\":"+
				"["+
					"{\"nome\":\"HelloWorld.pdf\",\"descrizione\":\"File di esempio 'HelloWorld.pdf'\",\"tipoMIME\":\"application/pdf\","+
					  "\"dataDocumento\":\"2020-04-24T13:06:18.823+02:00\","+
					  "\"documento\":{\"tipoDocumento\":\"riferimento-uri\","+
					  				  "\"uri\":\"https://api.agenziaentrate.it/retrieve-document/0.1//documenti/f6892e27-5cbd-4789-b875-bdcb18f4557f\","+
					  				  "\"impronta\":\"KNdo5OCzZu8Hh7FwKxfpqPMTAHsC2ZRxOds5WTiu4QA=\"}"+
					"},"+
					"{\"nome\":\"PROVA.txt\",\"descrizione\":\"File di esempio 'PROVA.txt'\",\"tipoMIME\":\"text/plain\","+
					  "\"dataDocumento\":\"2020-04-24T13:06:18.851+02:00\","+
					  "\"documento\":{\"tipoDocumento\":\"inline\",\"contenuto\":\"SGVsbG8gV29ybGQhCg==\"}"+
					"}"+
				"]}";
		httpEntity.setContent(json);
		apiValidatorOpenApi.validate(httpEntity);	
		System.out.println("Test #1 completato\n\n");
		
		
		
		System.out.println("Test #2 (Richiesta POST con parametro /documenti/mixed/send) errata (elemento mancante)");
		String jsonErrato2 = "{\"mittente\":\"Mittente\",\"destinatario\":\"EnteDestinatario\",\"procedimento\":\"DescrizioneGenerica ...\",\"allegati\":"+
				"["+
					"{\"nome\":\"HelloWorld.pdf\",\"descrizione\":\"File di esempio 'HelloWorld.pdf'\",\"tipoMIME\":\"application/pdf\","+
					  "\"dataDocumento\":\"2020-04-24T13:06:18.823+02:00\","+
					  "\"documento\":{\"tipoDocumento\":\"riferimento-uri\","+
					  				  "\"uriERRATO\":\"https://api.agenziaentrate.it/retrieve-document/0.1//documenti/f6892e27-5cbd-4789-b875-bdcb18f4557f\","+
					  				  "\"impronta\":\"KNdo5OCzZu8Hh7FwKxfpqPMTAHsC2ZRxOds5WTiu4QA=\"}"+
					"},"+
					"{\"nome\":\"PROVA.txt\",\"descrizione\":\"File di esempio 'PROVA.txt'\",\"tipoMIME\":\"text/plain\","+
					  "\"dataDocumento\":\"2020-04-24T13:06:18.851+02:00\","+
					  "\"documento\":{\"tipoDocumento\":\"inline\",\"contenuto\":\"SGVsbG8gV29ybGQhCg==\"}"+
					"}"+
				"]}";
		httpEntity.setContent(jsonErrato2);
		try {
			apiValidatorOpenApi.validate(httpEntity);
			throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
		} catch(ValidatorException e) {
			System.out.println("Test #2 Errore trovato: " + e.getMessage());
			String msgErroreAtteso = null;
			switch (openAPILibrary) {
			case json_schema:
				msgErroreAtteso = "1028 $.allegati[0].documento.uri: is missing but it is required";
				break;
			case openapi4j:
				msgErroreAtteso = "body.allegati.0.documento: Field 'uri' is required.";
				break;
			case swagger_request_validator:
				msgErroreAtteso = "- [ERROR][] [Path '/allegati/0/documento'] Object has missing required properties ([\"uri\"])";
				break;
			}
			if(!e.getMessage().contains(msgErroreAtteso)) {
				throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
			}
			System.out.println("Test #2 completato\n\n");
		}
		
		
		System.out.println("Test #3 (Richiesta POST con parametro /documenti/mixed/send) errata (enumeration discriminator errata)");
		String jsonErrato3 = "{\"mittente\":\"Mittente\",\"destinatario\":\"EnteDestinatario\",\"procedimento\":\"DescrizioneGenerica ...\",\"allegati\":"+
				"["+
					"{\"nome\":\"HelloWorld.pdf\",\"descrizione\":\"File di esempio 'HelloWorld.pdf'\",\"tipoMIME\":\"application/pdf\","+
					  "\"dataDocumento\":\"2020-04-24T13:06:18.823+02:00\","+
					  "\"documento\":{\"tipoDocumento\":\"riferimento-uriERRATA\","+
					  				  "\"uri\":\"https://api.agenziaentrate.it/retrieve-document/0.1//documenti/f6892e27-5cbd-4789-b875-bdcb18f4557f\","+
					  				  "\"impronta\":\"KNdo5OCzZu8Hh7FwKxfpqPMTAHsC2ZRxOds5WTiu4QA=\"}"+
					"},"+
					"{\"nome\":\"PROVA.txt\",\"descrizione\":\"File di esempio 'PROVA.txt'\",\"tipoMIME\":\"text/plain\","+
					  "\"dataDocumento\":\"2020-04-24T13:06:18.851+02:00\","+
					  "\"documento\":{\"tipoDocumento\":\"inline\",\"contenuto\":\"SGVsbG8gV29ybGQhCg==\"}"+
					"}"+
				"]}";
		httpEntity.setContent(jsonErrato3);
		try {
			apiValidatorOpenApi.validate(httpEntity);
			throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
		} catch(ValidatorException e) {
			System.out.println("Test #3 Errore trovato: " + e.getMessage());
			String msgErroreAtteso = null;
			switch (openAPILibrary) {
			case json_schema:
				msgErroreAtteso = "1008 $.allegati[0].documento.tipoDocumento: does not have a value in the enumeration [inline, riferimento-uri]";
				break;
			case openapi4j:
				msgErroreAtteso = "body.allegati.0.documento: Schema selection can't be made for discriminator 'tipoDocumento' with value 'riferimento-uriERRATA'.";
				break;
			case swagger_request_validator:
				msgErroreAtteso = "[ERROR][] [Path '/allegati/0/documento/tipoDocumento'] Instance value (\"riferimento-uriERRATA\") not found in enum (possible values: [\"inline\",\"riferimento-uri\"])";
				break;
			}
			if(!e.getMessage().contains(msgErroreAtteso)) {
				throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
			}
			System.out.println("Test #3 completato\n\n");
		}
		
		
		
		System.out.println("Test #4 (Richiesta POST con parametro /documenti/mixed/send) errata (enumeration discriminator non presente)");
		String jsonErrato4 = "{\"mittente\":\"Mittente\",\"destinatario\":\"EnteDestinatario\",\"procedimento\":\"DescrizioneGenerica ...\",\"allegati\":"+
				"["+
					"{\"nome\":\"HelloWorld.pdf\",\"descrizione\":\"File di esempio 'HelloWorld.pdf'\",\"tipoMIME\":\"application/pdf\","+
					  "\"dataDocumento\":\"2020-04-24T13:06:18.823+02:00\","+
					  "\"documento\":{\"tipoDocumento\":\"riferimento-uri\","+
					  				  "\"uri\":\"https://api.agenziaentrate.it/retrieve-document/0.1//documenti/f6892e27-5cbd-4789-b875-bdcb18f4557f\","+
					  				  "\"impronta\":\"KNdo5OCzZu8Hh7FwKxfpqPMTAHsC2ZRxOds5WTiu4QA=\"}"+
					"},"+
					"{\"nome\":\"PROVA.txt\",\"descrizione\":\"File di esempio 'PROVA.txt'\",\"tipoMIME\":\"text/plain\","+
					  "\"dataDocumento\":\"2020-04-24T13:06:18.851+02:00\","+
					  "\"documento\":{\"contenuto\":\"SGVsbG8gV29ybGQhCg==\"}"+
					"}"+
				"]}";
		httpEntity.setContent(jsonErrato4);
		try {
			apiValidatorOpenApi.validate(httpEntity);
			throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
		} catch(ValidatorException e) {
			System.out.println("Test #4 Errore trovato: " + e.getMessage());
			
			String msgErroreAtteso = null;
			switch (openAPILibrary) {
			case json_schema:
				msgErroreAtteso = "1028 $.allegati[1].documento.tipoDocumento: is missing but it is required";
				break;
			case openapi4j:
				msgErroreAtteso = "body.allegati.1.documento: Property name in content 'tipoDocumento' is not set.";
				break;
			case swagger_request_validator:
				msgErroreAtteso = "[ERROR][] [Path '/allegati/1/documento'] Object has missing required properties ([\"tipoDocumento\"])";
				break;
			}		
			if(!e.getMessage().contains(msgErroreAtteso)) {
				throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
			}
			
			switch (openAPILibrary) {
			case json_schema:
				msgErroreAtteso = "1028 $.allegati[1].documento.tipoDocumento: is missing but it is required"; // uso solito messaggio
				break;
			case openapi4j:
				msgErroreAtteso = "From: body.<allOf>.allegati.1.<items>.<#/components/schemas/AllegatoRiferimentoMixed>.<allOf>.documento.<discriminator>";
				break;
			case swagger_request_validator:
				msgErroreAtteso = "* /allOf/1/properties/allegati/items/allOf/1: Discriminator field 'tipoDocumento' is required";
				break;
			}	
			if(!e.getMessage().contains(msgErroreAtteso)) {
				throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
			}
			System.out.println("Test #4 completato\n\n");
		}
		
		
		
		
		// Non supera la validazione perchè lo swagger-validator quando incontra il content type json, prima
		// prova a parsare il body in json e poi cerca di verificare lo schema, che non centra più nulla con un
		// type: string, format: binary
		// se infatti metto un octet_stream, la validazione passa.
		// Dovrei dire al validatore di non convertire i json a tutti costi quando si trova content_type_json
		// Anzi meglio, può convertirlo e verificare sia un json, ma non deve validarlo per forza
		
		System.out.println("Test #5 (Richiesta POST con parametro dinamico /documenti/XYZ)");
		String testUrl5 = baseUri+"/documenti/test/"+UUID.randomUUID().toString();
		
		TextHttpRequestEntity httpEntityDynamicPath = new TextHttpRequestEntity();
		httpEntityDynamicPath.setMethod(HttpRequestMethod.POST);
		httpEntityDynamicPath.setUrl(testUrl5);	
		Map<String, List<String>> parametersTrasportoDynamicPath = new HashMap<>();
		TransportUtils.addHeader(parametersTrasportoDynamicPath,"api_key", "aaa");
		TransportUtils.addHeader(parametersTrasportoDynamicPath,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
		httpEntityDynamicPath.setHeaders(parametersTrasportoDynamicPath);
		httpEntityDynamicPath.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		httpEntityDynamicPath.setContent(json); // volutamente metto un json che comunque dovrebbe trattare come binario!
		apiValidatorOpenApi.validate(httpEntityDynamicPath);
			
		System.out.println("Test #5 completato\n\n");
		
		
		
		
		
		
		
		System.out.println("Test #6 (Risposta GET con parametro dinamico /documenti/XYZ)");
		testUrl5 = baseUri+"/documenti/"+UUID.randomUUID().toString();
		
		TextHttpRequestEntity httpEntityGET = new TextHttpRequestEntity();
		httpEntityGET.setMethod(HttpRequestMethod.GET);
		httpEntityGET.setUrl(testUrl5);	
		apiValidatorOpenApi.validate(httpEntityGET);	
		
		List<String> contentTypes_test5 = new ArrayList<String>();
		contentTypes_test5.add(HttpConstants.CONTENT_TYPE_JSON);
		contentTypes_test5.add("jsonUncorrect");
		contentTypes_test5.add(HttpConstants.CONTENT_TYPE_PLAIN);
		contentTypes_test5.add(HttpConstants.CONTENT_TYPE_XML);
		contentTypes_test5.add("xmlUncorrect");
		contentTypes_test5.add(HttpConstants.CONTENT_TYPE_SOAP_1_1);
		contentTypes_test5.add(HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
		contentTypes_test5.add(HttpConstants.CONTENT_TYPE_PDF);
		contentTypes_test5.add(HttpConstants.CONTENT_TYPE_ZIP);
		
		for (String ct : contentTypes_test5) {
		
			HttpBaseResponseEntity<?> httpEntityResponse = null;
			if(ct.contains("json") || ct.contains("plain") || ct.contains("Uncorrect")) {
				httpEntityResponse = new TextHttpResponseEntity();
			}
			else if(ct.contains("xml")) {
				if(HttpConstants.CONTENT_TYPE_XML.equals(ct)) {
					httpEntityResponse = new DocumentHttpResponseEntity();
				}
				else {
					httpEntityResponse = new ElementHttpResponseEntity();
				}
			}
			else {
				httpEntityResponse = new BinaryHttpResponseEntity();
			}
			httpEntityResponse.setStatus(200);
			httpEntityResponse.setMethod(HttpRequestMethod.GET);
			httpEntityResponse.setUrl(testUrl5);	
			Map<String, List<String>> parametersTrasportoRisposta = new HashMap<>();
			TransportUtils.addHeader(parametersTrasportoRisposta,"api_key", "aaa");
			TransportUtils.addHeader(parametersTrasportoRisposta,HttpConstants.CONTENT_TYPE, ct);
			httpEntityResponse.setHeaders(parametersTrasportoRisposta);
			httpEntityResponse.setContentType(ct);
			if(ct.contains("json") || ct.contains("plain")) {
				if(ct.contains("Uncorrect")) {
					((TextHttpResponseEntity)httpEntityResponse).setContent("{ a }");
					parametersTrasportoRisposta.remove(HttpConstants.CONTENT_TYPE);
					TransportUtils.setHeader(parametersTrasportoRisposta,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
					httpEntityResponse.setContentType(HttpConstants.CONTENT_TYPE_JSON);
				}
				else if(ct.contains("plain")) {
					((TextHttpResponseEntity)httpEntityResponse).setContent("Hello World!");
				}
				else {
					((TextHttpResponseEntity)httpEntityResponse).setContent(json); // volutamente metto un json che comunque dovrebbe trattare come binario!		
				}
			}
			else if(ct.contains("xml")) {
				if(ct.contains("Uncorrect")) {
					((TextHttpResponseEntity)httpEntityResponse).setContent("<prova>aaaprova>");
					parametersTrasportoRisposta.remove(HttpConstants.CONTENT_TYPE);
					TransportUtils.setHeader(parametersTrasportoRisposta,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_XML);
					httpEntityResponse.setContentType(HttpConstants.CONTENT_TYPE_XML);
				}
				else if(HttpConstants.CONTENT_TYPE_XML.equals(ct)) {
					((DocumentHttpResponseEntity)httpEntityResponse).setContent(docXml);
				}
				else {
					((ElementHttpResponseEntity)httpEntityResponse).setContent(elementXml);
				}
			}
			else {
				((BinaryHttpResponseEntity)httpEntityResponse).setContent(pdf);
			}
			
			String tipoTest = "ContentType:"+ct+" ";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
								
			try {
				System.out.println("\t "+tipoTest+" validate response ...");
				
				if(ct.contains("xml") && openAPILibrary == OpenAPILibrary.swagger_request_validator) {
					System.out.println("\t Content-Type " + ct + ": funzionalità non supportata dalla libreria swagger-request-validator"); // swagger-request-validator-unsupported
					continue;
				}
				
				apiValidatorOpenApi.validate(httpEntityResponse);	
				System.out.println("\t "+tipoTest+" validate response ok");
				
				if(openAPILibrary!=OpenAPILibrary.json_schema && ct.contains("Uncorrect")) {
					throw new Exception("Attesa eccezione");
				}
				
			} catch(Throwable e) {
				String error = e.getMessage();
				if(error.length()>500) {
					error = error.substring(0, 498)+" ...";
				}
				if(ct.contains("Uncorrect")) {
					String erroreAtteso = null;
					if(ct.contains("json")) {
						erroreAtteso = "Unexpected character ('a' (code 97))";
					}
					else {
						erroreAtteso = "Unclosed tag prova at 16 [character 17 line 1]";
					}
					if(!e.getMessage().contains(erroreAtteso)) {
						System.out.println("\t "+tipoTest+" rilevato errore di validazione diverso da quello atteso ("+erroreAtteso+") : "+error);
						throw new Exception(""+tipoTest+" rilevato errore di validazione diverso da quello atteso ("+erroreAtteso+") : "+e.getMessage(),e);
					}
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
			}
			
		}
		
		System.out.println("Test #6 completato\n\n");
		
		
		
		System.out.println("Test #7 (Richiesta POST con parametro /documenti/mixed/send e elemento null)");
		String testUrl7 = baseUri+"/documenti/mixed/send";
		TextHttpRequestEntity httpEntity7 = new TextHttpRequestEntity();
		httpEntity7.setMethod(HttpRequestMethod.POST);
		httpEntity7.setUrl(testUrl7);	
		Map<String, List<String>> parametersTrasporto7 = new HashMap<>();
		TransportUtils.addHeader(parametersTrasporto7,"api_key", "aaa");
		TransportUtils.addHeader(parametersTrasporto7,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
		httpEntity7.setHeaders(parametersTrasporto7);
		httpEntity7.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		String json7 = "{\"mittente\":\"Mittente\",\"destinatario\":\"EnteDestinatario\",\"procedimento\":\"DescrizioneGenerica ...\","+
				"\"string_nullable\":null,"+
				"\"number_nullable\":null,"+
				"\"enum_nullable\":null,"+
				"\"allegati\":"+
				"["+
					"{\"nome\":\"HelloWorld.pdf\",\"descrizione\":\"File di esempio 'HelloWorld.pdf'\",\"tipoMIME\":\"application/pdf\","+
					  "\"dataDocumento\":\"2020-04-24T13:06:18.823+02:00\","+
					  "\"documento\":{\"tipoDocumento\":\"riferimento-uri\","+
					  				  "\"uri\":\"https://api.agenziaentrate.it/retrieve-document/0.1//documenti/f6892e27-5cbd-4789-b875-bdcb18f4557f\","+
					  				  "\"impronta\":\"KNdo5OCzZu8Hh7FwKxfpqPMTAHsC2ZRxOds5WTiu4QA=\"}"+
					"},"+
					"{\"nome\":\"PROVA.txt\",\"descrizione\":\"File di esempio 'PROVA.txt'\",\"tipoMIME\":\"text/plain\","+
					  "\"dataDocumento\":\"2020-04-24T13:06:18.851+02:00\","+
					  "\"documento\":{\"tipoDocumento\":\"inline\",\"contenuto\":\"SGVsbG8gV29ybGQhCg==\"}"+
					"}"+
				"]}";
		httpEntity7.setContent(json7);
		apiValidatorOpenApi.validate(httpEntity7);	
		System.out.println("Test #7 completato\n\n");
		
		
		
		
		
		System.out.println("Test #8 (Richiesta POST con parametro /documenti/mixed/send e elemento valido/nonValido secondo il pattern)");
		String testUrl8 = baseUri+"/documenti/mixed/send";
		List<String> valori_test8 = new ArrayList<String>();
		List<Boolean> esiti_test8 = new ArrayList<Boolean>();
		valori_test8.add("234567");esiti_test8.add(true);
		valori_test8.add("2345676");esiti_test8.add(false);
		valori_test8.add("23456");esiti_test8.add(false);
		valori_test8.add("");esiti_test8.add(false);
		valori_test8.add("234A67");esiti_test8.add(false);
		valori_test8.add("234567A");esiti_test8.add(false);
		valori_test8.add("234-67");esiti_test8.add(false);
		valori_test8.add("234.67");esiti_test8.add(false);
		valori_test8.add("234867\\n");esiti_test8.add(false);
		valori_test8.add("234867\\r\\n");esiti_test8.add(false);
		valori_test8.add("234867\\t");esiti_test8.add(false);
		for (int i = 0; i < valori_test8.size(); i++) {
			String valore = valori_test8.get(i);
			boolean esito = esiti_test8.get(i);
			
			TextHttpRequestEntity httpEntity8 = new TextHttpRequestEntity();
			httpEntity8.setMethod(HttpRequestMethod.POST);
			httpEntity8.setUrl(testUrl8);	
			Map<String, List<String>> parametersTrasporto8 = new HashMap<>();
			TransportUtils.addHeader(parametersTrasporto8,"api_key", "aaa");
			TransportUtils.addHeader(parametersTrasporto8,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			httpEntity8.setHeaders(parametersTrasporto8);
			httpEntity8.setContentType(HttpConstants.CONTENT_TYPE_JSON);
			String json8 = "{\"mittente\":\"Mittente\",\"destinatario\":\"EnteDestinatario\",\"procedimento\":\"DescrizioneGenerica ...\","+
					"\"allegati\":"+
					"["+
						"{\"nome\":\"HelloWorld.pdf\",\"descrizione\":\"File di esempio 'HelloWorld.pdf'\",\"tipoMIME\":\"application/pdf\","+
						  "\"dataDocumento\":\"2020-04-24T13:06:18.823+02:00\","+
						  "\"codiceOpzionaleNumerico\":\""+valore+"\","+
						  "\"documento\":{\"tipoDocumento\":\"riferimento-uri\","+
						  				  "\"uri\":\"https://api.agenziaentrate.it/retrieve-document/0.1//documenti/f6892e27-5cbd-4789-b875-bdcb18f4557f\","+
						  				  "\"impronta\":\"KNdo5OCzZu8Hh7FwKxfpqPMTAHsC2ZRxOds5WTiu4QA=\"}"+
						"},"+
						"{\"nome\":\"PROVA.txt\",\"descrizione\":\"File di esempio 'PROVA.txt'\",\"tipoMIME\":\"text/plain\","+
						  "\"dataDocumento\":\"2020-04-24T13:06:18.851+02:00\","+
						  "\"documento\":{\"tipoDocumento\":\"inline\",\"contenuto\":\"SGVsbG8gV29ybGQhCg==\"}"+
						"}"+
					"]}";
			httpEntity8.setContent(json8);
			try {
				System.out.println("\t (Valore:"+valore+") validate ...");
				
				if(valore.endsWith("\\n") && openAPILibrary == OpenAPILibrary.json_schema) {
					System.out.println("\t (Valore:"+valore+") test skipped; validazione json non individua l'errore di un ritorno a capo");
					continue;
				}
				
				apiValidatorOpenApi.validate(httpEntity8);
				if(esito) {
					System.out.println("\t (Valore:"+valore+") validate ok");
				}
				else {
					System.out.println("\t (Valore:"+valore+") ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				if(!esito) {
					System.out.println("\t (Valore:"+valore+") atteso errore di validazione, rilevato: "+e.getMessage());
				}
				else {
					System.out.println("\t (Valore:"+valore+") rilevato errore di validazione non atteso: "+e.getMessage());
					throw new Exception("(Valore:"+valore+") rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "1023 $.allegati[0].codiceOpzionaleNumerico: does not match the regex pattern ^\\d{6}$"; 
					break;
				case openapi4j:
					msgErroreAtteso = "body.allegati.0.codiceOpzionaleNumerico: '"+valore+"' does not respect pattern '^\\d{6}$'.";
					break;
				case swagger_request_validator:
					msgErroreAtteso = "- [ERROR][] [Path '/allegati/0/codiceOpzionaleNumerico'] ECMA 262 regex \"^\\d{6}$\" does not match input string \""+StringEscapeUtils.unescapeJava(valore)+"";
					break;
				}				
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t (Valore:"+valore+") ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'" +"\nTrovato invece: '"+e.getMessage()+"'");
				}
				
				msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "1023 $.allegati[0].codiceOpzionaleNumerico: does not match the regex pattern ^\\d{6}$"; // uso solito messaggio
					break;
				case openapi4j:
					msgErroreAtteso = "From: body.<allOf>.allegati.0.<items>.<#/components/schemas/AllegatoRiferimentoMixed>.<allOf>.<#/components/schemas/Allegato>.codiceOpzionaleNumerico.<pattern>";
					break;
				case swagger_request_validator:
					msgErroreAtteso = "* /allOf/1/properties/allegati/items/allOf/0";
					break;
				}	
				if(!e.getMessage().contains(msgErroreAtteso)) {
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
		}
		System.out.println("Test #8 completato\n\n");
		
		
		
		
		
		System.out.println("Test #9 (Richiesta POST con parametro /documenti/mixed/send e elemento valido/nonValido secondo il pattern definito in OR)");
		String testUrl9 = baseUri+"/documenti/mixed/send";
		List<String> valori_test9 = new ArrayList<String>();
		List<Boolean> esiti_test9 = new ArrayList<Boolean>();
		valori_test9.add("NGRLLI04L54D969B");esiti_test9.add(true); // CF1
		valori_test9.add("brtBGI06c16d612D");esiti_test9.add(true); // CF2
		valori_test9.add("ABC323");esiti_test9.add(true); // Codice1
		valori_test9.add("345323");esiti_test9.add(true); // Codice2
		valori_test9.add("A2C323");esiti_test9.add(true); // Codice3
		valori_test9.add("3GRLLI04L54D969B");esiti_test9.add(false); // CF errato
		valori_test9.add("GRLLI04L54D969B");esiti_test9.add(false); // CF errato
		valori_test9.add("NGRLLI04L54D969BA");esiti_test9.add(false); // CF errato
		valori_test9.add("NGRLLI04L54D969B\\r\\n");esiti_test9.add(false); // CF errato
		valori_test9.add("NGRLLI04L54D969B\\n");esiti_test9.add(false); // CF errato
		valori_test9.add("NGRLLI04L54D969B\\t");esiti_test9.add(false); // CF errato
		valori_test9.add("NGRLLIA4L54D969B");esiti_test9.add(false); // CF errato
		valori_test9.add("AABC323");esiti_test9.add(false); // Codice errato
		valori_test9.add("ABC32");esiti_test9.add(false); // Codice errato
		valori_test9.add("ABC32A");esiti_test9.add(false); // Codice errato
		valori_test9.add("abC323");esiti_test9.add(false); // Codice errato
		for (int i = 0; i < valori_test9.size(); i++) {
			String valore = valori_test9.get(i);
			boolean esito = esiti_test9.get(i);
			
			TextHttpRequestEntity httpEntity9 = new TextHttpRequestEntity();
			httpEntity9.setMethod(HttpRequestMethod.POST);
			httpEntity9.setUrl(testUrl9);	
			Map<String, List<String>> parametersTrasporto9 = new HashMap<>();
			TransportUtils.addHeader(parametersTrasporto9,"api_key", "aaa");
			TransportUtils.addHeader(parametersTrasporto9,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			httpEntity9.setHeaders(parametersTrasporto9);
			httpEntity9.setContentType(HttpConstants.CONTENT_TYPE_JSON);
			
			String json9 = "{\"mittente\":\"Mittente\",\"destinatario\":\"EnteDestinatario\",\"procedimento\":\"DescrizioneGenerica ...\","+
					"\"allegati\":"+
					"["+
						"{\"nome\":\"HelloWorld.pdf\",\"descrizione\":\"File di esempio 'HelloWorld.pdf'\",\"tipoMIME\":\"application/pdf\","+
						  "\"dataDocumento\":\"2020-04-24T13:06:18.823+02:00\","+
						  "\"codiceOpzionaleCodiceFiscaleOrCodiceEsterno\":\""+valore+"\","+
						  "\"documento\":{\"tipoDocumento\":\"riferimento-uri\","+
						  				  "\"uri\":\"https://api.agenziaentrate.it/retrieve-document/0.1//documenti/f6892e27-5cbd-4789-b875-bdcb18f4557f\","+
						  				  "\"impronta\":\"KNdo5OCzZu8Hh7FwKxfpqPMTAHsC2ZRxOds5WTiu4QA=\"}"+
						"},"+
						"{\"nome\":\"PROVA.txt\",\"descrizione\":\"File di esempio 'PROVA.txt'\",\"tipoMIME\":\"text/plain\","+
						  "\"dataDocumento\":\"2020-04-24T13:06:18.851+02:00\","+
						  "\"documento\":{\"tipoDocumento\":\"inline\",\"contenuto\":\"SGVsbG8gV29ybGQhCg==\"}"+
						"}"+
					"]}";
			httpEntity9.setContent(json9);
			try {
				System.out.println("\t (Valore:"+valore+") validate ...");
				
				if(valore.endsWith("\\n") && openAPILibrary == OpenAPILibrary.json_schema) {
					System.out.println("\t (Valore:"+valore+") test skipped; validazione json non individua l'errore di un ritorno a capo");
					continue;
				}
				
				apiValidatorOpenApi.validate(httpEntity9);
				if(esito) {
					System.out.println("\t (Valore:"+valore+") validate ok");
				}
				else {
					System.out.println("\t (Valore:"+valore+") ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				if(!esito) {
					System.out.println("\t (Valore:"+valore+") atteso errore di validazione, rilevato: "+e.getMessage());
				}
				else {
					System.out.println("\t (Valore:"+valore+") rilevato errore di validazione non atteso: "+e.getMessage());
					throw new Exception("(Valore:"+valore+") rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "1023 $.allegati[0].codiceOpzionaleCodiceFiscaleOrCodiceEsterno: does not match the regex pattern ^[a-zA-Z]{6}[0-9]{2}[a-zA-Z0-9]{3}[a-zA-Z0-9]{5}$|^[A-Z0-9]{3}\\d{3}$";
					break;
				case openapi4j:
					msgErroreAtteso = "body.allegati.0.codiceOpzionaleCodiceFiscaleOrCodiceEsterno: '"+valore+"' does not respect pattern '^[a-zA-Z]{6}[0-9]{2}[a-zA-Z0-9]{3}[a-zA-Z0-9]{5}$|^[A-Z0-9]{3}\\d{3}$'.";
					break;
				case swagger_request_validator:
					msgErroreAtteso = "- [ERROR][] [Path '/allegati/0/codiceOpzionaleCodiceFiscaleOrCodiceEsterno'] ECMA 262 regex \"^[a-zA-Z]{6}[0-9]{2}[a-zA-Z0-9]{3}[a-zA-Z0-9]{5}$|^[A-Z0-9]{3}\\d{3}$\" does not match input string \""+StringEscapeUtils.unescapeJava(valore);
					break;
				}	
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t (Valore:"+valore+") ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
				
				msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "1023 $.allegati[0].codiceOpzionaleCodiceFiscaleOrCodiceEsterno: does not match the regex pattern ^[a-zA-Z]{6}[0-9]{2}[a-zA-Z0-9]{3}[a-zA-Z0-9]{5}$|^[A-Z0-9]{3}\\d{3}$"; // uso solito messaggio
					break;
				case openapi4j:
					msgErroreAtteso = "From: body.<allOf>.allegati.0.<items>.<#/components/schemas/AllegatoRiferimentoMixed>.<allOf>.<#/components/schemas/Allegato>.codiceOpzionaleCodiceFiscaleOrCodiceEsterno.<pattern>";
					break;
				case swagger_request_validator:
					msgErroreAtteso = "* /allOf/1/properties/allegati/items/allOf/0:";
					break;
				}	
				if(!e.getMessage().contains(msgErroreAtteso)) {
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
		}
		System.out.println("Test #9 completato\n\n");
		
		
		
		
		
		System.out.println("Test #10 (Richiesta POST con parametro /documenti/noresponse/send)");
		String testUrl10 = baseUri+"/documenti/noresponse/send";
		TextHttpRequestEntity httpEntityTest10 = new TextHttpRequestEntity();
		httpEntityTest10.setMethod(HttpRequestMethod.POST);
		httpEntityTest10.setUrl(testUrl10);	
		Map<String, List<String>> parametersTrasportoTest10 = new HashMap<>();
		TransportUtils.addHeader(parametersTrasportoTest10,"api_key", "aaa");
		TransportUtils.addHeader(parametersTrasportoTest10,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
		httpEntityTest10.setHeaders(parametersTrasportoTest10);
		httpEntityTest10.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		String jsonTest10 = "{\"mittente\":\"Mittente\",\"destinatario\":\"EnteDestinatario\",\"procedimento\":\"DescrizioneGenerica ...\",\"allegati\":"+
				"["+
					"{\"nome\":\"HelloWorld.pdf\",\"descrizione\":\"File di esempio 'HelloWorld.pdf'\",\"tipoMIME\":\"application/pdf\","+
					  "\"dataDocumento\":\"2020-04-24T13:06:18.823+02:00\","+
					  "\"documento\":{\"tipoDocumento\":\"riferimento-uri\","+
					  				  "\"uri\":\"https://api.agenziaentrate.it/retrieve-document/0.1//documenti/f6892e27-5cbd-4789-b875-bdcb18f4557f\","+
					  				  "\"impronta\":\"KNdo5OCzZu8Hh7FwKxfpqPMTAHsC2ZRxOds5WTiu4QA=\"}"+
					"},"+
					"{\"nome\":\"PROVA.txt\",\"descrizione\":\"File di esempio 'PROVA.txt'\",\"tipoMIME\":\"text/plain\","+
					  "\"dataDocumento\":\"2020-04-24T13:06:18.851+02:00\","+
					  "\"documento\":{\"tipoDocumento\":\"inline\",\"contenuto\":\"SGVsbG8gV29ybGQhCg==\"}"+
					"}"+
				"]}";
		httpEntityTest10.setContent(jsonTest10);
		apiValidatorOpenApi.validate(httpEntityTest10);	
		
		TextHttpResponseEntity httpEntityResponseTest10 = new TextHttpResponseEntity();
		httpEntityResponseTest10.setStatus(201);
		httpEntityResponseTest10.setMethod(HttpRequestMethod.POST);
		httpEntityResponseTest10.setUrl(testUrl10);	
		Map<String, List<String>> parametersTrasportoRispostaTest10 = new HashMap<>();
		TransportUtils.addHeader(parametersTrasportoRispostaTest10,"api_key", "aaa");
		httpEntityResponseTest10.setHeaders(parametersTrasportoRispostaTest10);
		
		System.out.println("\t Validazione senza content-type ...");
		apiValidatorOpenApi.validate(httpEntityResponseTest10);	
		System.out.println("\t Validazione senza content-type ok");
		
		List<String> contentTypeTest10List = new ArrayList<String>();
		contentTypeTest10List.add(HttpConstants.CONTENT_TYPE_PLAIN);
		contentTypeTest10List.add(HttpConstants.CONTENT_TYPE_TEXT_XML);
		contentTypeTest10List.add(HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807);
		contentTypeTest10List.add(HttpConstants.CONTENT_TYPE_JSON);
		contentTypeTest10List.add(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);
		for (String contentTypeTest10 : contentTypeTest10List) {
			
			for (int i = 0; i < 2; i++) {
				
				boolean addContenuto = (i==1);
				String tipoTest = "senza";
				if(addContenuto) {
					tipoTest = "con";
				}
				
				tipoTest = "["+openAPILibrary+"] "+ tipoTest;

				
				System.out.println("\t Validazione con content-type '"+contentTypeTest10+"' "+tipoTest+" contenuto ...");
				TransportUtils.setHeader(parametersTrasportoRispostaTest10,HttpConstants.CONTENT_TYPE, contentTypeTest10);
				httpEntityResponseTest10.setContentType(contentTypeTest10);
				httpEntityResponseTest10.setHeaders(parametersTrasportoRispostaTest10);
				if(addContenuto) {
					httpEntityResponseTest10.setContent(jsonTest10); // contenuto a caso
				}
				else {
					httpEntityResponseTest10.setContent(null);
				}
				boolean esito = false;
				try {
					apiValidatorOpenApi.validate(httpEntityResponseTest10);	
					esito = true;
				}
				catch(Exception e) {
					String msg = e.getMessage();
					
					String atteso = null;
					switch (openAPILibrary) {
					case json_schema:
						atteso = "Content-Type '"+contentTypeTest10+"' (http response status '201') unsupported";
						break;
					case openapi4j:
						atteso = "Content type '"+contentTypeTest10+"' is not allowed for body content. (code: 203)";
						break;
					case swagger_request_validator:
						if(addContenuto) {
							atteso = "[ERROR][RESPONSE][] No response body is expected but one was found.";
						} else {
							atteso = "Content-Type '"+contentTypeTest10+"' (http response status '201') unsupported"; // swagger-request-validator-unsupported Check risolto da govway
						}
						break;
					}	
					if(!msg.contains(atteso)) {
						String checkErrore = "Validazione con content-type '"+contentTypeTest10+"' "+tipoTest+" contenuto terminato con un errore diverso da quello atteso: '"+msg+"'";
						System.out.println("\t "+checkErrore);
						throw new Exception(checkErrore);
					}
					else {
						System.out.println("\t Validazione con content-type '"+contentTypeTest10+"' "+tipoTest+" contenuto terminato con l'errore atteso: "+msg);
					}
				}
				if(esito) {
					System.out.println("\t Validazione con content-type '"+contentTypeTest10+"' "+tipoTest+" contenuto: atteso errore");
					throw new Exception("Validazione con content-type '"+contentTypeTest10+"' "+tipoTest+" contenuto: atteso errore");
				}
			}
			
		}
		
		System.out.println("Test #10 completato\n\n");
		
		
		
		
		
		System.out.println("Test #11 (Richiesta POST con parametro /documenti/norequestresponse/send)");
		String testUrl11 = baseUri+"/documenti/norequestresponse/send";
		
		List<String> contentTypeTest11List = new ArrayList<String>();
		contentTypeTest11List.add(HttpConstants.CONTENT_TYPE_PLAIN);
		contentTypeTest11List.add(HttpConstants.CONTENT_TYPE_TEXT_XML);
		contentTypeTest11List.add(HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807);
		contentTypeTest11List.add(HttpConstants.CONTENT_TYPE_JSON);
		contentTypeTest11List.add(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);
		
		String jsonTest11 = "{\"mittente\":\"Mittente\",\"destinatario\":\"EnteDestinatario\",\"procedimento\":\"DescrizioneGenerica ...\",\"allegati\":"+
				"["+
					"{\"nome\":\"HelloWorld.pdf\",\"descrizione\":\"File di esempio 'HelloWorld.pdf'\",\"tipoMIME\":\"application/pdf\","+
					  "\"dataDocumento\":\"2020-04-24T13:06:18.823+02:00\","+
					  "\"documento\":{\"tipoDocumento\":\"riferimento-uri\","+
					  				  "\"uri\":\"https://api.agenziaentrate.it/retrieve-document/0.1//documenti/f6892e27-5cbd-4789-b875-bdcb18f4557f\","+
					  				  "\"impronta\":\"KNdo5OCzZu8Hh7FwKxfpqPMTAHsC2ZRxOds5WTiu4QA=\"}"+
					"},"+
					"{\"nome\":\"PROVA.txt\",\"descrizione\":\"File di esempio 'PROVA.txt'\",\"tipoMIME\":\"text/plain\","+
					  "\"dataDocumento\":\"2020-04-24T13:06:18.851+02:00\","+
					  "\"documento\":{\"tipoDocumento\":\"inline\",\"contenuto\":\"SGVsbG8gV29ybGQhCg==\"}"+
					"}"+
				"]}";
		
		TextHttpRequestEntity httpEntityTest11 = new TextHttpRequestEntity();
		httpEntityTest11.setMethod(HttpRequestMethod.POST);
		httpEntityTest11.setUrl(testUrl11);	
		Map<String, List<String>> parametersTrasportoTest11 = new HashMap<>();
		TransportUtils.addHeader(parametersTrasportoTest11,"api_key", "aaa");
		httpEntityTest11.setHeaders(parametersTrasportoTest11);
		
		System.out.println("\t Validazione richiesta senza content-type ...");
		apiValidatorOpenApi.validate(httpEntityTest11);	
		System.out.println("\t Validazione richiesta senza content-type ok");
		
		for (String contentTypeTest11 : contentTypeTest11List) {
			
			for (int i = 0; i < 2; i++) {
				boolean addContenuto = (i==1);
				String tipoTest = "senza";
				if(addContenuto) {
					tipoTest = "con";
				}
		
				tipoTest = "["+openAPILibrary+"] "+ tipoTest;
				
				System.out.println("\t Validazione richiesta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto ...");
				TransportUtils.setHeader(parametersTrasportoTest11,HttpConstants.CONTENT_TYPE, contentTypeTest11);
				httpEntityTest11.setContentType(contentTypeTest11);
				if(addContenuto) {
					httpEntityTest11.setContent(jsonTest11);
				}
				else {
					httpEntityTest11.setContent(null);
				}
				boolean esito = false;
				try {
					apiValidatorOpenApi.validate(httpEntityTest11);	
					esito = true;
				}
				catch(Exception e) {
					String msg = e.getMessage();
					
					String atteso = null;
					switch (openAPILibrary) {
					case json_schema:
						atteso = "Content-Type '"+contentTypeTest11+"' unsupported";
						break;
					case openapi4j:
						atteso = "Content-Type '"+contentTypeTest11+"' unsupported"; // openapi4j non si accorge dell'errore, check risolto da govway
						break;
					case swagger_request_validator:
						if (httpEntityTest11.getContent() != null) {
							atteso = "[ERROR][REQUEST][POST /documenti/norequestresponse/send] No request body is expected but one was found."; 
						} else {
							atteso = "Content-Type '"+contentTypeTest11+"' unsupported"; // swagger-request-validator-unsupported, check risolto da govway
						}
						break;
					}	
					if(!msg.contains(atteso)) {
						String checkErrore = "Validazione richiesta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto terminato con un errore diverso da quello atteso: '"+msg+"'";
						System.out.println("\t "+checkErrore);
						System.out.println("\tatteso: "+atteso);
						throw new Exception(checkErrore);
					}
					else {
						System.out.println("\t Validazione richiesta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto terminato con l'errore atteso: "+msg);
					}
				}
				if(esito) {
					System.out.println("\t Validazione richiesta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto: atteso errore");
					throw new Exception("Validazione richiesta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto: atteso errore");
				}
				
			}
		}
		
		TextHttpResponseEntity httpEntityResponseTest11 = new TextHttpResponseEntity();
		httpEntityResponseTest11.setStatus(201);
		httpEntityResponseTest11.setMethod(HttpRequestMethod.POST);
		httpEntityResponseTest11.setUrl(testUrl11);	
		Map<String, List<String>> parametersTrasportoRispostaTest11 = new HashMap<>();
		TransportUtils.addHeader(parametersTrasportoRispostaTest11,"api_key", "aaa");
		httpEntityResponseTest11.setHeaders(parametersTrasportoRispostaTest11);
		
		System.out.println("\t Validazione risposta senza content-type ...");
		apiValidatorOpenApi.validate(httpEntityResponseTest11);	
		System.out.println("\t Validazione risposta senza content-type ok");

		for (String contentTypeTest11 : contentTypeTest11List) {
			
			for (int i = 0; i < 2; i++) {
				boolean addContenuto = (i==1);
				String tipoTest = "senza";
				if(addContenuto) {
					tipoTest = "con";
				}
				
				tipoTest = "["+openAPILibrary+"] "+ tipoTest;
				
				System.out.println("\t Validazione risposta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto ...");
				TransportUtils.setHeader(parametersTrasportoRispostaTest11,HttpConstants.CONTENT_TYPE, contentTypeTest11);
				httpEntityResponseTest11.setContentType(contentTypeTest11);
				httpEntityResponseTest11.setHeaders(parametersTrasportoRispostaTest11);
				if(addContenuto) {
					httpEntityResponseTest11.setContent(jsonTest11); // contenuto a caso
				}
				else {
					httpEntityResponseTest11.setContent(null);
				}
				boolean esito = false;
				try {
					apiValidatorOpenApi.validate(httpEntityResponseTest11);	
					esito = true;
				}
				catch(Exception e) {
					String msg = e.getMessage();
					
					String atteso = null;
					switch (openAPILibrary) {
					case json_schema:
						atteso = "Content-Type '"+contentTypeTest11+"' (http response status '201') unsupported";
						break;
					case openapi4j:
						atteso = "Content type '"+contentTypeTest11+"' is not allowed for body content. (code: 203)";
						break;
					case swagger_request_validator:
						if(addContenuto) {
							atteso = "[ERROR][RESPONSE][] No response body is expected but one was found.";
						} else {
							atteso = "Content-Type '"+contentTypeTest11+"' (http response status '201') unsupported"; // swagger-request-validator-unsupported Check risolto da govway
						}
						break;
					}	
					if(!msg.contains(atteso)) {
						String checkErrore = "Atteso errore: '"+atteso+"'\nValidazione risposta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto terminato con un errore diverso da quello atteso: '"+msg+"'";
						System.out.println("\t "+checkErrore);
						throw new Exception(checkErrore);
					}
					else {
						System.out.println("\t Validazione risposta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto terminato con l'errore atteso: "+msg);
					}
				}
				if(esito) {
					System.out.println("\t Validazione risposta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto: atteso errore");
					throw new Exception("Validazione risposta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto: atteso errore");
				}
			}
			
		}
		
		System.out.println("Test #11 completato\n\n");
		
		
		
		
		
		
		
		
		System.out.println("Test #12 (Richiesta POST senza contenuto /documenti/in-line/send[Optional/Required]  (Request Body 'required'))");
		
		// https://swagger.io/docs/specification/describing-request-body/
		//  Request bodies are optional by default. To mark the body as required, use required: true.
		
		String testUrl12_default = baseUri+"/documenti/in-line/send";
		TextHttpRequestEntity httpEntity12_default = new TextHttpRequestEntity();
		httpEntity12_default.setMethod(HttpRequestMethod.POST);
		httpEntity12_default.setUrl(testUrl12_default);
		Map<String, List<String>> parametersTrasporto12_default = new HashMap<>();
		httpEntity12_default.setHeaders(parametersTrasporto12_default);
		
		String testUrl12_optional = baseUri+"/documenti/in-line/sendOptional";
		TextHttpRequestEntity httpEntity12_optional = new TextHttpRequestEntity();
		httpEntity12_optional.setMethod(HttpRequestMethod.POST);
		httpEntity12_optional.setUrl(testUrl12_optional);
		Map<String, List<String>> parametersTrasporto12_optional = new HashMap<>();
		httpEntity12_optional.setHeaders(parametersTrasporto12_optional);
		
		String testUrl12_required = baseUri+"/documenti/in-line/sendRequired";
		TextHttpRequestEntity httpEntity12_required = new TextHttpRequestEntity();
		httpEntity12_required.setMethod(HttpRequestMethod.POST);
		httpEntity12_required.setUrl(testUrl12_required);
		Map<String, List<String>> parametersTrasporto12_required = new HashMap<>();
		httpEntity12_required.setHeaders(parametersTrasporto12_required);
		
		List<String> contentTypeTest12List = new ArrayList<String>();
		contentTypeTest12List.add(null);
		contentTypeTest12List.add(HttpConstants.CONTENT_TYPE_PLAIN);
		contentTypeTest12List.add(HttpConstants.CONTENT_TYPE_JSON);
		
		for (String contentTypeTest12 : contentTypeTest12List) {
			
			String tipoTestPrefix = "Content-Type:'"+contentTypeTest12+"' ";
		
			TransportUtils.setHeader(parametersTrasporto12_default,HttpConstants.CONTENT_TYPE, contentTypeTest12);
			httpEntity12_default.setContentType(contentTypeTest12);
			TransportUtils.setHeader(parametersTrasporto12_optional,HttpConstants.CONTENT_TYPE, contentTypeTest12);
			httpEntity12_optional.setContentType(contentTypeTest12);
			TransportUtils.setHeader(parametersTrasporto12_required,HttpConstants.CONTENT_TYPE, contentTypeTest12);
			httpEntity12_required.setContentType(contentTypeTest12);
			
			for (int i = 0; i < 3; i++) {
				
				String tipoTest = tipoTestPrefix;
				tipoTest = tipoTest+"["+openAPILibrary+"]";
				
				TextHttpRequestEntity httpEntity12 = null;
				boolean required = false;
				if(i==0) {
					httpEntity12 = httpEntity12_default;
					tipoTest = tipoTest + " (required:default)";
				}
				else if(i==1) {
					httpEntity12 = httpEntity12_optional;
					tipoTest = tipoTest + " (required:false)";
				}
				else if(i==2) {
					httpEntity12 = httpEntity12_required;
					required = true;
					tipoTest = tipoTest + " (required:true)";
				}
				
				
				System.out.println("\t Validazione richiesta senza contenuto "+tipoTest+"  ...");
				boolean esito = false;
				try {
					apiValidatorOpenApi.validate(httpEntity12);
					esito = true;
				}
				catch(Exception e) {
					
					String msg = e.getMessage();
					if(msg==null || "null".equals(msg)) {
						e.printStackTrace(System.out);
					}
					
					if(!required && contentTypeTest12==null) {
						String checkErrore = "Validazione richiesta "+tipoTest+" senza contenuto terminato con un errore non atteso: '"+msg+"'";
						System.out.println("\t "+checkErrore);
						throw new Exception(checkErrore);
					}
					
					String atteso = null;
					switch (openAPILibrary) {
					case json_schema:
						atteso = "Required body undefined";
						if(!required && contentTypeTest12!=null) {
							atteso = "Content-Type '"+contentTypeTest12+"' unsupported";
							if(HttpConstants.CONTENT_TYPE_JSON.equals(contentTypeTest12)) {
								atteso = "Content undefined";
							}
						}
						break;
					case openapi4j:
						atteso = "Body is required but none provided. (code: 200)";
						if(!required && contentTypeTest12!=null) {
							atteso = "Content-Type '"+contentTypeTest12+"' unsupported";
						}
						break;
					case swagger_request_validator:
						if (httpEntity12.getContentType() == null) {
							atteso = "Required Content-Type is missing";
						} else {
							atteso = "A request body is required but none found.";
						}
						if(!required && contentTypeTest12!=null) {
							atteso = "Request Content-Type header '[text/plain]' does not match any allowed types. Must be one of: [application/json].";
						}
						break;
					}					
					if(!msg.contains(atteso)) {
						String checkErrore = "Validazione richiesta "+tipoTest+" senza contenuto terminato con un errore diverso da quello atteso: '"+msg+"'";
						System.out.println("\t "+checkErrore);
						throw new Exception(checkErrore);
					}
					else {
						System.out.println("\t Validazione richiesta "+tipoTest+" senza contenuto terminato con l'errore atteso: "+msg);
					}
				}
				if(required) {
					if(esito) {
						System.out.println("\t Validazione richiesta "+tipoTest+" senza contenuto: atteso errore");
						throw new Exception("Validazione richiesta "+tipoTest+" senza contenuto: atteso errore");
					}
				}
				else {
					System.out.println("\t Validazione richiesta senza contenuto "+tipoTest+" ok");
				}
			}
			
			TextHttpResponseEntity httpEntityResponseTest12 = new TextHttpResponseEntity();
			httpEntityResponseTest12.setStatus(200);
			httpEntityResponseTest12.setMethod(HttpRequestMethod.POST);
			httpEntityResponseTest12.setUrl(testUrl12_default);	 // per la risposta una risorsa vale l'altra come test
			Map<String, List<String>> parametersTrasportoRispostaTest12 = new HashMap<>();
			httpEntityResponseTest12.setHeaders(parametersTrasportoRispostaTest12);
			
			String tipoTest = tipoTestPrefix;
			tipoTest = tipoTest+"["+openAPILibrary+"]";

			System.out.println("\t Validazione risposta senza contenuto "+tipoTest+"  " + httpEntityResponseTest12.getUrl() + " ...");
			boolean esito = false;
			try {
				apiValidatorOpenApi.validate(httpEntityResponseTest12);	
				esito = true;
			}
			catch(Exception e) {
				//e.printStackTrace(System.out);
				String msg = e.getMessage();
				
				String atteso = null;
				switch (openAPILibrary) {
				case json_schema:
					atteso = "Required body undefined";
					break;
				case openapi4j:
					atteso = "Content type 'null' is not allowed for body content. (code: 203)";
					break;
				case swagger_request_validator:
					atteso = "Required Content-Type is missing";
					break;
				}			
				if(!msg.contains(atteso)) {
					String checkErrore = "Validazione "+tipoTest+" senza contenuto terminato con un errore diverso da quello atteso: '"+msg+"'";
					System.out.println("\t "+checkErrore);
					throw new Exception(checkErrore);
				}
				else {
					System.out.println("\t Validazione risposta "+tipoTest+" senza contenuto terminato con l'errore atteso: "+msg);
				}
			}
			if(esito) {
				System.out.println("\t Validazione risposta "+tipoTest+" senza contenuto: atteso errore");
				throw new Exception("Validazione risposta "+tipoTest+" senza contenuto: atteso errore");
			}
		}
		
			
		System.out.println("Test #12 completato\n\n");
		
		
		
		

		System.out.println("Test #13 (Richiesta POST con parametro /documenti/datetest e elemento valido/nonValido secondo il pattern definito per la data in RFC 3339, section 5.6)");
		
		String testUrl13= baseUri+"/documenti/datetest/";
		List<String> valori_test13 = new ArrayList<String>();
		List<Boolean> esiti_test13 = new ArrayList<Boolean>();
		valori_test13.add("2020-07-22");esiti_test13.add(true); // ok
		valori_test13.add("2020 07 21");esiti_test13.add(false); // ko
		valori_test13.add("2020/07/21");esiti_test13.add(false); // ko
		valori_test13.add("20200721");esiti_test13.add(false); // ko
		valori_test13.add("2020-07");esiti_test13.add(false); // ok
		valori_test13.add("2017-07-21T17:32:28");esiti_test13.add(false); // date-time valido, ma il tipo è date
		valori_test13.add("2017-07-21T17:32:28Z");esiti_test13.add(false); // date-time valido, ma il tipo è date
		valori_test13.add("2017-07-21T17:32:28+01:00");esiti_test13.add(false); // date-time valido, ma il tipo è date

		// ** Test sul body **
		for (int i = 0; i < valori_test13.size(); i++) {
			String valore = valori_test13.get(i);
			boolean esito = esiti_test13.get(i);
			
			TextHttpRequestEntity httpEntity13 = new TextHttpRequestEntity();
			httpEntity13.setMethod(HttpRequestMethod.POST);
			httpEntity13.setUrl(testUrl13+"2020-07-21"); // uso data valida, il test e' sul body
			Map<String, List<String>> parametersUrl13 = new HashMap<>();
			TransportUtils.setParameter(parametersUrl13,"data_documento_query","2020-07-19"); // uso data valida, il test e' sul body	
			httpEntity13.setParameters(parametersUrl13);
			Map<String, List<String>> parametersTrasporto13 = new HashMap<>();
			TransportUtils.setHeader(parametersTrasporto13,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			TransportUtils.setHeader(parametersTrasporto13,"data_documento_header","2020-07-23"); // uso data valida, il test e' sul body	
			httpEntity13.setHeaders(parametersTrasporto13);
			httpEntity13.setContentType(HttpConstants.CONTENT_TYPE_JSON);
			String json13 = "{\"data\": \""+valore+"\"}";
			httpEntity13.setContent(json13);
			
			String tipoTest = esito ? "[body con valore ok '"+valore+"']" : "[body con valore errato '"+valore+"']";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
			
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntity13);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>200) {
					error = error.substring(0, 198)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "1034 $.data: "+valore+" is an invalid date";
					break;
				case openapi4j:
					msgErroreAtteso = "body.data: Value '"+valore+"' does not match format 'date'. (code: 1007)";
					break;
				case swagger_request_validator:
					msgErroreAtteso = "[ERROR][REQUEST][POST /documenti/datetest/2020-07-21 @body] [Path '/data'] String \""+valore+"\" is invalid against requested date format(s) yyyy-MM-dd";
					break;
				}	
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
			
			
			TextHttpResponseEntity httpEntityResponseTest13 = new TextHttpResponseEntity();
			httpEntityResponseTest13.setStatus(200);
			httpEntityResponseTest13.setMethod(HttpRequestMethod.POST);
			httpEntityResponseTest13.setUrl(testUrl13+"2020-07-21"); // uso data valida, il test e' sul body
			Map<String, List<String>> parametersTrasportoRispostaTest13 = new HashMap<>();
			httpEntityResponseTest13.setHeaders(parametersTrasportoRispostaTest13);
			TransportUtils.setHeader(parametersTrasportoRispostaTest13,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			TransportUtils.setHeader(parametersTrasportoRispostaTest13,"data_documento_risposta_header","2020-07-23"); // uso data valida, il test e' sul body	
			httpEntityResponseTest13.setContentType(HttpConstants.CONTENT_TYPE_JSON);
			httpEntityResponseTest13.setContent(json13); 
			
			tipoTest = esito ? "[body response con valore ok '"+valore+"']" : "[body response con valore errato '"+valore+"']";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
			
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntityResponseTest13);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>200) {
					error = error.substring(0, 198)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "1034 $.data: "+valore+" is an invalid date";
					break;
				case openapi4j:
					msgErroreAtteso = "body.data: Value '"+valore+"' does not match format 'date'. (code: 1007)";
					break;
				case swagger_request_validator:
					msgErroreAtteso = "[ERROR][RESPONSE][] [Path '/data'] String \""+valore+"\" is invalid against requested date format(s) yyyy-MM-dd";
					break;
				}	
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
		}
		
		// ** Test su header **
		
		for (int i = 0; i < valori_test13.size(); i++) {
			String valore = valori_test13.get(i);
			boolean esito = esiti_test13.get(i);
			
			TextHttpRequestEntity httpEntity13 = new TextHttpRequestEntity();
			httpEntity13.setMethod(HttpRequestMethod.POST);
			httpEntity13.setUrl(testUrl13+"2020-07-21"); // uso data valida, il test e' su header
			Map<String, List<String>> parametersUrl13 = new HashMap<>();
			TransportUtils.setParameter(parametersUrl13,"data_documento_query","2020-07-19"); // uso data valida, il test e' su header	
			httpEntity13.setParameters(parametersUrl13);
			Map<String, List<String>> parametersTrasporto13 = new HashMap<>();
			TransportUtils.setHeader(parametersTrasporto13,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			TransportUtils.setHeader(parametersTrasporto13,"data_documento_header",valore);	
			httpEntity13.setHeaders(parametersTrasporto13);
			httpEntity13.setContentType(HttpConstants.CONTENT_TYPE_JSON);  // uso data valida, il test e' su header	
			String json13 = "{\"data\": \"2020-07-20\"}";
			httpEntity13.setContent(json13);
			
			String tipoTest = esito ? "[header con valore ok '"+valore+"']" : "[header con valore errato '"+valore+"']";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
			
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntity13);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>200) {
					error = error.substring(0, 198)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "Invalid value '"+valore+"' in http header 'data_documento_header' (expected type 'date'): Found date '"+valore+"' has wrong format (see RFC 3339, section 5.6): Uncorrect format";
					break;
				case openapi4j:
					msgErroreAtteso = "data_documento_header: Value '"+valore+"' does not match format 'date'. (code: 1007)";
					break;
				case swagger_request_validator:
					msgErroreAtteso = "[ERROR][REQUEST][POST /documenti/datetest/2020-07-21 @header.data_documento_header] String \""+valore+"\" is invalid against requested date format(s)";
					break;
				}
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}

			}
			
			
			TextHttpResponseEntity httpEntityResponseTest13 = new TextHttpResponseEntity();
			httpEntityResponseTest13.setStatus(200);
			httpEntityResponseTest13.setMethod(HttpRequestMethod.POST);
			httpEntityResponseTest13.setUrl(testUrl13+"2020-07-21"); // uso data valida, il test e' su header
			Map<String, List<String>> parametersTrasportoRispostaTest13 = new HashMap<>();
			httpEntityResponseTest13.setHeaders(parametersTrasportoRispostaTest13);
			TransportUtils.setHeader(parametersTrasportoRispostaTest13,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			TransportUtils.setHeader(parametersTrasportoRispostaTest13,"data_documento_risposta_header",valore);
			httpEntityResponseTest13.setContentType(HttpConstants.CONTENT_TYPE_JSON);
			httpEntityResponseTest13.setContent(json13); 
			
			tipoTest = esito ? "[header response con valore ok '"+valore+"']" : "[header response con valore errato '"+valore+"']";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
			
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntityResponseTest13);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>200) {
					error = error.substring(0, 198)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "Invalid value '"+valore+"' in http header 'data_documento_risposta_header' (expected type 'date'): Found date '"+valore+"' has wrong format (see RFC 3339, section 5.6): Uncorrect format";
					break;
				case openapi4j:
					msgErroreAtteso = "data_documento_risposta_header: Value '"+valore+"' does not match format 'date'. (code: 1007)";
					break;
				case swagger_request_validator:
					msgErroreAtteso = "[ERROR][RESPONSE][] String \""+valore+"\" is invalid against requested date format(s) yyyy-MM-dd";
					break;
				}
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
		}
		
		// ** Test su query paramater **
		
		for (int i = 0; i < valori_test13.size(); i++) {
			String valore = valori_test13.get(i);
			boolean esito = esiti_test13.get(i);
			
			TextHttpRequestEntity httpEntity13 = new TextHttpRequestEntity();
			httpEntity13.setMethod(HttpRequestMethod.POST);
			httpEntity13.setUrl(testUrl13+"2020-07-21"); // uso data valida, il test e' su query parameter
			Map<String, List<String>> parametersUrl13 = new HashMap<>();
			TransportUtils.setParameter(parametersUrl13,"data_documento_query",valore); 	
			httpEntity13.setParameters(parametersUrl13);
			Map<String, List<String>> parametersTrasporto13 = new HashMap<>();
			TransportUtils.setHeader(parametersTrasporto13,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			TransportUtils.setHeader(parametersTrasporto13,"data_documento_header","2020-07-19"); // uso data valida, il test e' su query parameter	
			httpEntity13.setHeaders(parametersTrasporto13);
			httpEntity13.setContentType(HttpConstants.CONTENT_TYPE_JSON);  // uso data valida, il test e' su query parameter	
			String json13 = "{\"data\": \"2020-07-20\"}";
			httpEntity13.setContent(json13);
			
			String tipoTest = esito ? "[query parameter con valore ok '"+valore+"']" : "[query parameter con valore errato '"+valore+"']";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
			
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntity13);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>200) {
					error = error.substring(0, 198)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "Invalid value '"+valore+"' in query parameter 'data_documento_query' (expected type 'date'): Found date '"+valore+"' has wrong format (see RFC 3339, section 5.6): Uncorrect format";
					break;
				case openapi4j:
					msgErroreAtteso = "data_documento_query: Value '"+valore+"' does not match format 'date'. (code: 1007)";
					break;
				case swagger_request_validator:
					msgErroreAtteso = "[ERROR][REQUEST][POST /documenti/datetest/2020-07-21 @query.data_documento_query] String \""+valore+"\" is invalid against requested date format(s) ";
					break;
				}
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
		}
		
		// ** Test su path **
		
		for (int i = 0; i < valori_test13.size(); i++) {
			String valore = valori_test13.get(i);
			boolean esito = esiti_test13.get(i);
			
			if(valore.contains(" ")) { // non permesso in un path
				continue;
			}
			if(valore.contains("/")) { // indica un altra risorsa
				continue;
			}
			
			TextHttpRequestEntity httpEntity13 = new TextHttpRequestEntity();
			httpEntity13.setMethod(HttpRequestMethod.POST);
			httpEntity13.setUrl(testUrl13+valore); 
			Map<String, List<String>> parametersUrl13 = new HashMap<>();
			TransportUtils.setParameter(parametersUrl13,"data_documento_query","2020-07-21"); // uso data valida, il test e' su path
			httpEntity13.setParameters(parametersUrl13);
			Map<String, List<String>> parametersTrasporto13 = new HashMap<>();
			TransportUtils.setHeader(parametersTrasporto13,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			TransportUtils.setHeader(parametersTrasporto13,"data_documento_header","2020-07-19"); // uso data valida, il test e' su path
			httpEntity13.setHeaders(parametersTrasporto13);
			httpEntity13.setContentType(HttpConstants.CONTENT_TYPE_JSON);  // uso data valida, il test e' path
			String json13 = "{\"data\": \"2020-07-20\"}";
			httpEntity13.setContent(json13);
			
			String tipoTest = esito ? "[path parameter con valore ok '"+valore+"']" : "[path parameter con valore errato '"+valore+"']";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
			
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntity13);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>200) {
					error = error.substring(0, 198)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "Invalid value '"+valore+"' in dynamic path 'data_documento_path' (expected type 'date'): Found date '"+valore+"' has wrong format (see RFC 3339, section 5.6): Uncorrect format";
					break;
				case openapi4j:
					msgErroreAtteso = "data_documento_path: Value '"+valore+"' does not match format 'date'. (code: 1007)\n"
							+ "From: data_documento_path.<format>";
					break;
				case swagger_request_validator:
					msgErroreAtteso = "Invalid value '"+valore+"' in dynamic path 'data_documento_path' (expected type 'date'): Found date '"+valore+"' has wrong format (see RFC 3339, section 5.6): Uncorrect format";
					break;
				}
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
		}
		
		System.out.println("Test #13 completato\n\n");
		
		
		
		
		
		System.out.println("Test #14 (Richiesta POST con parametro /documenti/datetimetest e elemento valido/nonValido secondo il pattern definito per la data in RFC 3339, section 5.6)");
		
		String testUrl14= baseUri+"/documenti/datetimetest/";
		List<String> valori_test14 = new ArrayList<String>();
		List<Boolean> esiti_test14 = new ArrayList<Boolean>();
		// Lasciare i primi sopra, poiche' la validazione json non si accorge dell'errore che manca Z o offset.
		valori_test14.add("2017-07-21T17:32:28");esiti_test14.add(false); // manca o l'offset o Z
		valori_test14.add("2017-07-21T17:32:28.000");esiti_test14.add(false); // manca o l'offset o Z
		valori_test14.add("2017-07-21T17:32:28+0100");esiti_test14.add(false); // manca il :
		// altri
		valori_test14.add("2017-07-21T17:32:28Z");esiti_test14.add(true); // date-time valido, ma il tipo è date
		valori_test14.add("2017-07-21T17:32:28+01:00");esiti_test14.add(true); // date-time valido, ma il tipo è date
		valori_test14.add("2017-07-21T17:32:27-02:00");esiti_test14.add(true); // date-time valido, ma il tipo è date
		valori_test14.add("2017-07-21T17:32:28.303Z");esiti_test14.add(true); // date-time valido, ma il tipo è date
		valori_test14.add("2017-07-21T17:32:28.929+01:00");esiti_test14.add(true); // date-time valido, ma il tipo è date
		valori_test14.add("2017-07-21T17:32:27.123-02:00");esiti_test14.add(true); // date-time valido, ma il tipo è date
		valori_test14.add("2020-07-22");esiti_test14.add(false); // date valido, ma il tipo è date
		valori_test14.add("2017-07-21 17:32:28");esiti_test14.add(false); // ko
		valori_test14.add("2017/07/21T17:32:28");esiti_test14.add(false); // ko
		valori_test14.add("2017 07 21T17:32:28");esiti_test14.add(false); // ko
		valori_test14.add("2017-07-21T17 32 28");esiti_test14.add(false); // ko
		valori_test14.add("2017-07-21T173228");esiti_test14.add(false); // ko
		valori_test14.add("2017-07-21T17:32:28 01:00");esiti_test14.add(false); // ko
		valori_test14.add("2017-07-21T17:32:28+01");esiti_test14.add(false); // ko
		
		Set<String> swagger_validator_fallimenti_datetime = Set.of( "2017-07-21T17:32:28+0100", "2017-07-21T17:32:28+01" );

		// ** Test sul body **
		for (int i = 0; i < valori_test14.size(); i++) {
			String valore = valori_test14.get(i);
			boolean esito = esiti_test14.get(i);
			
			TextHttpRequestEntity httpEntity14 = new TextHttpRequestEntity();
			httpEntity14.setMethod(HttpRequestMethod.POST);
			httpEntity14.setUrl(testUrl14+"2020-07-21T17:32:28Z"); // uso data valida, il test e' sul body
			Map<String, List<String>> parametersUrl14 = new HashMap<>();
			TransportUtils.setParameter(parametersUrl14,"datetime_documento_query","2020-07-19T17:32:28Z"); // uso data valida, il test e' sul body	
			httpEntity14.setParameters(parametersUrl14);
			Map<String, List<String>> parametersTrasporto14 = new HashMap<>();
			TransportUtils.setHeader(parametersTrasporto14,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			TransportUtils.setHeader(parametersTrasporto14,"datetime_documento_header","2020-07-23T17:32:28Z"); // uso data valida, il test e' sul body	
			httpEntity14.setHeaders(parametersTrasporto14);
			httpEntity14.setContentType(HttpConstants.CONTENT_TYPE_JSON);
			String json14 = "{\"data\": \""+valore+"\"}";
			httpEntity14.setContent(json14);
			
			String tipoTest = esito ? "[body con valore ok '"+valore+"']" : "[body con valore errato '"+valore+"']";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
			
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntity14);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					if(i<3 && openAPILibrary == OpenAPILibrary.json_schema) {
						System.out.println("\t "+tipoTest+" validate, la validazione "+OpenAPILibrary.json_schema+" non rileva l'accezione!!!!");
						continue;
					} else if( openAPILibrary == OpenAPILibrary.swagger_request_validator && swagger_validator_fallimenti_datetime.contains(valore)) {
						System.out.println("\t "+tipoTest+" validate, la validazione "+OpenAPILibrary.swagger_request_validator+" non rileva l'accezione!!!!");
						continue;
					}
					
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>200) {
					error = error.substring(0, 198)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "1034 $.data: "+valore+" is an invalid date";
					break;
				case openapi4j:
					msgErroreAtteso = "body.data: Value '"+valore+"' does not match format 'date-time'. (code: 1007)";
					break;
				case swagger_request_validator:
					msgErroreAtteso = "[ERROR][REQUEST][POST /documenti/datetimetest/2020-07-21T17:32:28Z @body] [Path '/data'] String \""+valore+"\" is invalid against requested ";
					break;
				}
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
			
			TextHttpResponseEntity httpEntityResponseTest14 = new TextHttpResponseEntity();
			httpEntityResponseTest14.setStatus(200);
			httpEntityResponseTest14.setMethod(HttpRequestMethod.POST);
			httpEntityResponseTest14.setUrl(testUrl14+"2020-07-21T17:32:28Z"); // uso data valida, il test e' sul body
			Map<String, List<String>> parametersTrasportoRispostaTest14 = new HashMap<>();
			httpEntityResponseTest14.setHeaders(parametersTrasportoRispostaTest14);
			TransportUtils.setHeader(parametersTrasportoRispostaTest14,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			TransportUtils.setHeader(parametersTrasportoRispostaTest14,"data_documento_risposta_header","2020-07-23T17:32:28Z"); // uso data valida, il test e' sul body	
			httpEntityResponseTest14.setContentType(HttpConstants.CONTENT_TYPE_JSON);
			httpEntityResponseTest14.setContent(json14); 
			
			tipoTest = esito ? "[body response con valore ok '"+valore+"']" : "[body response con valore errato '"+valore+"']";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
			
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntityResponseTest14);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					if(i<3 && openAPILibrary == OpenAPILibrary.json_schema) {
						System.out.println("\t "+tipoTest+" validate, la validazione "+OpenAPILibrary.json_schema+" non rileva l'accezione!!!!");
						continue;
					} else if(openAPILibrary == OpenAPILibrary.swagger_request_validator && swagger_validator_fallimenti_datetime.contains(valore)) {
						System.out.println("\t "+tipoTest+" validate, la validazione "+OpenAPILibrary.swagger_request_validator+" non rileva l'accezione!!!!");
						continue;
					}
					
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>200) {
					error = error.substring(0, 198)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "1034 $.data: "+valore+" is an invalid date";
					break;
				case openapi4j:
					msgErroreAtteso = "body.data: Value '"+valore+"' does not match format 'date-time'. (code: 1007)";
					break;
				case swagger_request_validator:
					msgErroreAtteso = "[ERROR][RESPONSE][] [Path '/data'] String \""+valore+"\" is invalid against requested date format(s) [yyyy-MM-dd'T'HH:mm:ssZ, yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,12}Z]";
					break;
				}
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
		}
		
		// ** Test su header **
		
		for (int i = 0; i < valori_test14.size(); i++) {
			String valore = valori_test14.get(i);
						
			boolean esito = esiti_test14.get(i);
			
			TextHttpRequestEntity httpEntity14 = new TextHttpRequestEntity();
			httpEntity14.setMethod(HttpRequestMethod.POST);
			httpEntity14.setUrl(testUrl14+"2020-07-21T17:32:28Z"); // uso data valida, il test e' su header
			Map<String, List<String>> parametersUrl14 = new HashMap<>();
			TransportUtils.setParameter(parametersUrl14,"datetime_documento_query","2020-07-19T17:32:28Z"); // uso data valida, il test e' su header	
			httpEntity14.setParameters(parametersUrl14);
			Map<String, List<String>> parametersTrasporto14 = new HashMap<>();
			TransportUtils.setHeader(parametersTrasporto14,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			TransportUtils.setHeader(parametersTrasporto14,"datetime_documento_header",valore);	
			httpEntity14.setHeaders(parametersTrasporto14);
			httpEntity14.setContentType(HttpConstants.CONTENT_TYPE_JSON);  // uso data valida, il test e' su header	
			String json14 = "{\"data\": \"2020-07-20T17:32:28Z\"}";
			httpEntity14.setContent(json14);
			
			String tipoTest = esito ? "[header con valore ok '"+valore+"']" : "[header con valore errato '"+valore+"']";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
			
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntity14);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>200) {
					error = error.substring(0, 198)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "Invalid value '"+valore+"' in http header 'datetime_documento_header' (expected type 'date-time'): Found dateTime '"+valore+"' has wrong format (see RFC 3339, section 5.6): ";
					if(!valore.contains("T")) {
						msgErroreAtteso = msgErroreAtteso+ "Expected 'T' separator";
					}
					else {
						msgErroreAtteso = msgErroreAtteso+ "Uncorrect format";
					}
					break;
				case openapi4j:
					msgErroreAtteso = "datetime_documento_header: Value '"+valore+"' does not match format 'date-time'. (code: 1007)";
					break;
				case swagger_request_validator:
					if (swagger_validator_fallimenti_datetime.contains(valore)) {
						System.out.println("\t "+tipoTest+" validate, lo swagger-request-validator non rileva l'accezione!!!!");
						continue;
					}
					msgErroreAtteso = "[ERROR][REQUEST][POST /documenti/datetimetest/2020-07-21T17:32:28Z @header.datetime_documento_header] String \""+valore+"\" is invalid against requested date format(s) [yyyy-MM-dd'T'HH:mm:ssZ, yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,12}Z]";
					break;
				}
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
			
			TextHttpResponseEntity httpEntityResponseTest14 = new TextHttpResponseEntity();
			httpEntityResponseTest14.setStatus(200);
			httpEntityResponseTest14.setMethod(HttpRequestMethod.POST);
			httpEntityResponseTest14.setUrl(testUrl14+"2020-07-21T17:32:28Z"); // uso data valida, il test e' su header
			Map<String, List<String>> parametersTrasportoRispostaTest14 = new HashMap<>();
			httpEntityResponseTest14.setHeaders(parametersTrasportoRispostaTest14);
			TransportUtils.setHeader(parametersTrasportoRispostaTest14,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			TransportUtils.setHeader(parametersTrasportoRispostaTest14,"datetime_documento_risposta_header",valore);
			httpEntityResponseTest14.setContentType(HttpConstants.CONTENT_TYPE_JSON);
			httpEntityResponseTest14.setContent(json14); 
			
			tipoTest = esito ? "[header response con valore ok '"+valore+"']" : "[header response con valore errato '"+valore+"']";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
			
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntityResponseTest14);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>200) {
					error = error.substring(0, 198)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "Invalid value '"+valore+"' in http header 'datetime_documento_risposta_header' (expected type 'date-time'): Found dateTime '"+valore+"' has wrong format (see RFC 3339, section 5.6): ";
					if(!valore.contains("T")) {
						msgErroreAtteso = msgErroreAtteso+ "Expected 'T' separator";
					}
					else {
						msgErroreAtteso = msgErroreAtteso+ "Uncorrect format";
					}
					break;
				case openapi4j:
					msgErroreAtteso = "datetime_documento_risposta_header: Value '"+valore+"' does not match format 'date-time'. (code: 1007)";
					break;
				case swagger_request_validator:
					if (swagger_validator_fallimenti_datetime.contains(valore)) {
						System.out.println("\t "+tipoTest+" validate, lo swagger-request-validator non rileva l'accezione!!!!");
						continue;
					}
					msgErroreAtteso = "[ERROR][RESPONSE][] String \""+valore+"\" is invalid against requested date format(s) [yyyy-MM-dd'T'HH:mm:ssZ, yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,12}Z]";
					break;
				}
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
		}
		
		// ** Test su query paramater **
		
		for (int i = 0; i < valori_test14.size(); i++) {
			String valore = valori_test14.get(i);
			boolean esito = esiti_test14.get(i);
			
			TextHttpRequestEntity httpEntity14 = new TextHttpRequestEntity();
			httpEntity14.setMethod(HttpRequestMethod.POST);
			httpEntity14.setUrl(testUrl14+"2020-07-21T17:32:28Z"); // uso data valida, il test e' su query parameter
			Map<String, List<String>> parametersUrl14 = new HashMap<>();
			TransportUtils.setParameter(parametersUrl14,"datetime_documento_query",valore); 	
			httpEntity14.setParameters(parametersUrl14);
			Map<String, List<String>> parametersTrasporto14 = new HashMap<>();
			TransportUtils.setHeader(parametersTrasporto14,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			TransportUtils.setHeader(parametersTrasporto14,"datetime_documento_header","2020-07-19T17:32:28Z"); // uso data valida, il test e' su query parameter	
			httpEntity14.setHeaders(parametersTrasporto14);
			httpEntity14.setContentType(HttpConstants.CONTENT_TYPE_JSON);  // uso data valida, il test e' su query parameter	
			String json14 = "{\"data\": \"2020-07-20T17:32:28Z\"}";
			httpEntity14.setContent(json14);
			
			String tipoTest = esito ? "[query parameter con valore ok '"+valore+"']" : "[query parameter con valore errato '"+valore+"']";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
			
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntity14);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>200) {
					error = error.substring(0, 198)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "Invalid value '"+valore+"' in query parameter 'datetime_documento_query' (expected type 'date-time'): Found dateTime '"+valore+"' has wrong format (see RFC 3339, section 5.6): ";
					if(!valore.contains("T")) {
						msgErroreAtteso = msgErroreAtteso+ "Expected 'T' separator";
					}
					else {
						msgErroreAtteso = msgErroreAtteso+ "Uncorrect format";
					}
					break;
				case openapi4j:
					msgErroreAtteso = "datetime_documento_query: Value '"+valore+"' does not match format 'date-time'. (code: 1007)";
					break;
				case swagger_request_validator:
					if (swagger_validator_fallimenti_datetime.contains(valore)) {
						System.out.println("\t "+tipoTest+" validate, lo swagger-request-validator non rileva l'accezione!!!!");
						continue;
					}
					msgErroreAtteso = "[ERROR][REQUEST][POST /documenti/datetimetest/2020-07-21T17:32:28Z @query.datetime_documento_query] String \""+valore+"\" is invalid against r";
					break;
				}
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
		}
		
		// ** Test su path **
		
		for (int i = 0; i < valori_test14.size(); i++) {
			String valore = valori_test14.get(i);
			boolean esito = esiti_test14.get(i);
			
			if(valore.contains(" ")) { // non permesso in un path
				continue;
			}
			if(valore.contains("/")) { // indica un altra risorsa
				continue;
			}
			
			TextHttpRequestEntity httpEntity14 = new TextHttpRequestEntity();
			httpEntity14.setMethod(HttpRequestMethod.POST);
			httpEntity14.setUrl(testUrl14+valore); 
			Map<String, List<String>> parametersUrl14 = new HashMap<>();
			TransportUtils.setParameter(parametersUrl14,"datetime_documento_query","2020-07-21T17:32:28Z"); // uso data valida, il test e' su path
			httpEntity14.setParameters(parametersUrl14);
			Map<String, List<String>> parametersTrasporto14 = new HashMap<>();
			TransportUtils.setHeader(parametersTrasporto14,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			TransportUtils.setHeader(parametersTrasporto14,"datetime_documento_header","2020-07-19T17:32:28Z"); // uso data valida, il test e' su path
			httpEntity14.setHeaders(parametersTrasporto14);
			httpEntity14.setContentType(HttpConstants.CONTENT_TYPE_JSON);  // uso data valida, il test e' path
			String json14 = "{\"data\": \"2020-07-20T17:32:28Z\"}";
			httpEntity14.setContent(json14);
			
			String tipoTest = esito ? "[path parameter con valore ok '"+valore+"']" : "[path parameter con valore errato '"+valore+"']";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
		
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntity14);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>200) {
					error = error.substring(0, 198)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "Invalid value '"+valore+"' in dynamic path 'datetime_documento_path' (expected type 'date-time'): Found dateTime '"+valore+"' has wrong format (see RFC 3339, section 5.6): ";
					if(!valore.contains("T")) {
						msgErroreAtteso = msgErroreAtteso+ "Expected 'T' separator";
					}
					else {
						msgErroreAtteso = msgErroreAtteso+ "Uncorrect format";
					}
					break;
				case openapi4j:
					msgErroreAtteso = "datetime_documento_path: Value '"+valore+"' does not match format 'date-time'. (code: 1007)\n"
							+ "From: datetime_documento_path.<format>";
					break;
				case swagger_request_validator:
					msgErroreAtteso = "Invalid value '"+valore+"' in dynamic path 'datetime_documento_path' (expected type 'date-time'): Found dateTime '"+valore+"' has wrong format (see RFC 3339, section 5.6): ";
					if(!valore.contains("T")) {
						msgErroreAtteso = msgErroreAtteso+ "Expected 'T' separator";
					}
					else {
						msgErroreAtteso = msgErroreAtteso+ "Uncorrect format";
					}
					break;
				}
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
		}
		
		System.out.println("Test #14 completato\n\n");
		
		
		
		
		
		
		System.out.println("Test #15 (Risposte che mappano il default)");
		
		String json15RispostaOK = "{\"esito\": \"OK\"}";
		
		String json15RispostaOK_fault = "{\n"+
			   "\"type\": \"https://example.com/probs/out-of-credit\","+
			   "\"title\": \"You do not have enough credit.\","+
			   "\"detail\": \"Your current balance is 30, but that costs 50.\""+
			    "}";
		
		String json15RispostaOK_completamente_diversa= "{\n"+
				   "\"altro\": \"https://example.com/probs/out-of-credit\","+
				   "\"altro2\": \"discorso casuale.\","+
				   "\"altro3\": 23,"+
				   "\"oggetto\": {"+
				     "\"esito\": \"OK\","+
					  "\"desc\": \"descr\""+
				   "}"+
				    "}";
		
		String testUrl15= baseUri+"/oggetti/"+UUID.randomUUID().toString();
		List<Integer> valori_test15 = new ArrayList<Integer>();
		List<String> msg_test15 = new ArrayList<String>();
		List<String> ct_test15 = new ArrayList<String>();
		List<Boolean> esiti_test15 = new ArrayList<Boolean>();
		
		valori_test15.add(200);msg_test15.add(json15RispostaOK);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON);esiti_test15.add(true); 
		valori_test15.add(200);msg_test15.add(json15RispostaOK_fault);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);esiti_test15.add(false); 
		valori_test15.add(200);msg_test15.add(json15RispostaOK_completamente_diversa);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);esiti_test15.add(false); 
		valori_test15.add(200);msg_test15.add(json15RispostaOK_fault);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON);esiti_test15.add(false); 
		valori_test15.add(200);msg_test15.add(json15RispostaOK_completamente_diversa);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON);esiti_test15.add(false); 
		valori_test15.add(200);msg_test15.add(null);ct_test15.add(null);esiti_test15.add(false); 
		
		valori_test15.add(201);msg_test15.add(json15RispostaOK_fault);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);esiti_test15.add(true); // ricade in default 
		valori_test15.add(201);msg_test15.add(json15RispostaOK);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);esiti_test15.add(false); // ricade in default 
		valori_test15.add(201);msg_test15.add(json15RispostaOK_completamente_diversa);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);esiti_test15.add(false); // ricade in default 
		valori_test15.add(201);msg_test15.add(json15RispostaOK);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON);esiti_test15.add(false); // ricade in default 
		valori_test15.add(201);msg_test15.add(json15RispostaOK_fault);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON);esiti_test15.add(false); // ricade in default 
		valori_test15.add(201);msg_test15.add(null);ct_test15.add(null);esiti_test15.add(false); // ricade in default 
		
		valori_test15.add(401);msg_test15.add(json15RispostaOK_fault);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);esiti_test15.add(true); // ricade in default 
		valori_test15.add(401);msg_test15.add(json15RispostaOK);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);esiti_test15.add(false); // ricade in default 
		valori_test15.add(401);msg_test15.add(json15RispostaOK_completamente_diversa);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);esiti_test15.add(false); // ricade in default 
		valori_test15.add(401);msg_test15.add(json15RispostaOK);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON);esiti_test15.add(false); // ricade in default 
		valori_test15.add(401);msg_test15.add(json15RispostaOK_fault);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON);esiti_test15.add(false); // ricade in default 
		valori_test15.add(401);msg_test15.add(null);ct_test15.add(null);esiti_test15.add(false); // ricade in default 
		
		// ricade in 400 dove vi e' il problem detail originale e quindi un messaggio json di risposta OK può essere 'scambiato' per una qualsiasi risposta
		valori_test15.add(400);msg_test15.add(json15RispostaOK_fault);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);esiti_test15.add(true); 
		valori_test15.add(400);msg_test15.add(json15RispostaOK);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);esiti_test15.add(true); // QUESTO CASO SOPRA FALLIVA, QUA PASSA!!!!!
		valori_test15.add(400);msg_test15.add(json15RispostaOK_completamente_diversa);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);esiti_test15.add(true); // QUESTO CASO SOPRA FALLIVA, QUA PASSA!!!!!
		valori_test15.add(400);msg_test15.add(json15RispostaOK);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON);esiti_test15.add(false); 
		valori_test15.add(400);msg_test15.add(json15RispostaOK_fault);ct_test15.add(HttpConstants.CONTENT_TYPE_JSON);esiti_test15.add(false);
		valori_test15.add(400);msg_test15.add(null);ct_test15.add(null);esiti_test15.add(false);

		// ** Test sul body **
		for (int i = 0; i < valori_test15.size(); i++) {
			Integer code = valori_test15.get(i);
			boolean esito = esiti_test15.get(i);
			String msg = msg_test15.get(i);
			String ct = ct_test15.get(i);
			
			TextHttpResponseEntity httpEntityResponseTest15 = new TextHttpResponseEntity();
			httpEntityResponseTest15.setStatus(code);
			httpEntityResponseTest15.setMethod(HttpRequestMethod.GET);
			httpEntityResponseTest15.setUrl(testUrl15);	
			Map<String, List<String>> parametersTrasportoRispostaTest15 = new HashMap<>();
			if(ct!=null) {
				TransportUtils.setHeader(parametersTrasportoRispostaTest15,HttpConstants.CONTENT_TYPE, ct);
			}
			httpEntityResponseTest15.setHeaders(parametersTrasportoRispostaTest15);
			httpEntityResponseTest15.setContentType(ct);
			httpEntityResponseTest15.setContent(msg);
			
			String msgRisposta = "RispostaOk";
			if(msg==null) {
				 msgRisposta = "EmptyResponse";
			}
			else if(json15RispostaOK_fault.equals(msg)) {
				 msgRisposta = "ProblemDetail";
			}
			else if(json15RispostaOK_completamente_diversa.equals(msg)) {
				 msgRisposta = "RispostaCompletamenteDiversa";
			}
			String tipoTest = "AttesoEsitoOk:"+esito+" Risposta:"+msgRisposta+" ContentType:"+ct+" ReturnCode:"+code;
			tipoTest = tipoTest+"["+openAPILibrary+"]";
		
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntityResponseTest15);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>200) {
					error = error.substring(0, 198)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				String msgErroreAtteso2 = null;
				switch (openAPILibrary) {
				case json_schema:
					if(ct==null) {
						msgErroreAtteso = "Required body undefined";
					}
					else {
						msgErroreAtteso = "Content-Type '"+ct+"' (http response status '"+code+"') unsupported";
						msgErroreAtteso2 = "1028 $.esito: is missing but it is required";
						if(code.intValue() != 200) {
							msgErroreAtteso2 = "1028 $.type: is missing but it is required";
						}
					}
					break;
				case openapi4j:
					msgErroreAtteso = "Content type '"+ct+"' is not allowed for body content. (code: 203)";
					msgErroreAtteso2 = "body: Field 'esito' is required. (code: 1026)";
					if(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807.equals(ct) && code.intValue() != 200) {
						msgErroreAtteso2 = "body: Field 'type' is required. (code: 1026)";
					}
					break;
				case swagger_request_validator:
					msgErroreAtteso = "[ERROR][RESPONSE][] Response Content-Type header '"+ct+"' does not match any allowed types. Must be one of: [application/";
					msgErroreAtteso2 = "[ERROR][RESPONSE][] Object has missing required properties ([\"esito\"])";
					if (ct==null) {
						msgErroreAtteso="Required Content-Type is missing";
					}
					if(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807.equals(ct) && code.intValue() != 200) {
						msgErroreAtteso2 = "[ERROR][RESPONSE][] Object has missing required properties ([\"type\"])"; 
					}
					break;
				}
				if(msgErroreAtteso2==null) {
					if(!e.getMessage().contains(msgErroreAtteso)) {
						System.out.println("\t "+tipoTest+" ERRORE!");
						throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
					}
				}
				else {
					if(!e.getMessage().contains(msgErroreAtteso) && !e.getMessage().contains(msgErroreAtteso2)) {
						System.out.println("\t "+tipoTest+" ERRORE!");
						throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"' o '"+msgErroreAtteso2+"'");
					}
				}
			}
			
		}
		
		System.out.println("Test #15 completato\n\n");
		
		
		
		
		
		
		
		System.out.println("Test #16 (Risposte con '/' come casi speciali)");
		
		String json16RispostaOK = "{\"esito\": \"OK\"}";
				
		List<String> url_test16 = new ArrayList<String>();
		List<Boolean> esiti_test16 = new ArrayList<Boolean>();
		
		url_test16.add("/oggettislashfinale/");esiti_test16.add(true); 
		url_test16.add("/oggettislashfinale");esiti_test16.add(true);
		url_test16.add("/");esiti_test16.add(true); 
		
		
		// ** Test sul body **
		for (int i = 0; i < url_test16.size(); i++) {
			
			String urltest = url_test16.get(i);
			boolean esito = esiti_test16.get(i);
			
			String msg = json16RispostaOK;
			String ct = HttpConstants.CONTENT_TYPE_JSON;
			int code = 200;
			
			TextHttpResponseEntity httpEntityResponseTest16 = new TextHttpResponseEntity();
			httpEntityResponseTest16.setStatus(code);
			httpEntityResponseTest16.setMethod(HttpRequestMethod.GET);
			httpEntityResponseTest16.setUrl(urltest);	
			Map<String, List<String>> parametersTrasportoRispostaTest16 = new HashMap<>();
			TransportUtils.setHeader(parametersTrasportoRispostaTest16,HttpConstants.CONTENT_TYPE, ct);
			httpEntityResponseTest16.setHeaders(parametersTrasportoRispostaTest16);
			httpEntityResponseTest16.setContentType(ct);
			httpEntityResponseTest16.setContent(msg);
			
			String tipoTest = "Url '"+urltest+"'";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
			
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntityResponseTest16);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>200) {
					error = error.substring(0, 198)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione atteso: "+e.getMessage(),e); // gestire se serve
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
			}
			
		}
		
		System.out.println("Test #16 completato\n\n");
		
		
		
		
		
		System.out.println("Test #17 (Problem Detail)");
		
		String json17_fault_type = "{\n"+
				   "\"type\": \"https://example.com/probs/out-of-credit\","+
				   "\"altro\": \"You do not have enough credit.\","+
				   "\"detail\": \"Your current balance is 30, but that costs 50.\""+
				    "}";
		String json17_fault_title = "{\n"+
				   "\"codice\": \"https://example.com/probs/out-of-credit\","+
				   "\"title\": \"You do not have enough credit.\","+
				   "\"detail\": \"Your current balance is 30, but that costs 50.\""+
				    "}";
		String json17_fault_status = "{\n"+
				   "\"altro\": \"https://example.com/probs/out-of-credit\","+
				   "\"status\": 200,"+
				   "\"detail\": \"Your current balance is 30, but that costs 50.\""+
				    "}";
		String json17_fault_type_title = "{\n"+
				   "\"type\": \"https://example.com/probs/out-of-credit\","+
				   "\"title\": \"You do not have enough credit.\","+
				   "\"detail\": \"Your current balance is 30, but that costs 50.\""+
				    "}";
		String json17_fault_type_status = "{\n"+
				   "\"type\": \"https://example.com/probs/out-of-credit\","+
				   "\"status\": 200,"+
				   "\"detail\": \"Your current balance is 30, but that costs 50.\""+
				    "}";
		String json17_fault_title_status = "{\n"+
				   "\"altro\": \"https://example.com/probs/out-of-credit\","+
				   "\"title\": \"You do not have enough credit.\","+
				   "\"status\": 200,"+
				   "\"detail\": \"Your current balance is 30, but that costs 50.\""+
				    "}";
		String json17_fault_type_title_status = "{\n"+
				   "\"type\": \"https://example.com/probs/out-of-credit\","+
				   "\"title\": \"You do not have enough credit.\","+
				   "\"status\": 200,"+
				   "\"detail\": \"Your current balance is 30, but that costs 50.\""+
				    "}";
		String json17_fault_none = "{\n"+
				   "\"altro\": \"https://example.com/probs/out-of-credit\","+
				   "\"error\": 200,"+
				   "\"detail\": \"Your current balance is 30, but that costs 50.\""+
				    "}";
		
		String testUrl17= baseUri+"/problem/"+UUID.randomUUID().toString();
		List<Integer> valori_test17 = new ArrayList<Integer>();
		List<String> msg_test17 = new ArrayList<String>();
		List<Boolean> esiti_test17 = new ArrayList<Boolean>();
		
		// ricade in 400 dove vi e' il problem detail rivisto per far si che almeno un elemento tra type, title e status sia required
		valori_test17.add(400);msg_test17.add(json17_fault_type);esiti_test17.add(true); 
		valori_test17.add(400);msg_test17.add(json17_fault_title);esiti_test17.add(true); 
		valori_test17.add(400);msg_test17.add(json17_fault_status);esiti_test17.add(true); 
		valori_test17.add(400);msg_test17.add(json17_fault_type_title);esiti_test17.add(true); 
		valori_test17.add(400);msg_test17.add(json17_fault_type_status);esiti_test17.add(true);
		valori_test17.add(400);msg_test17.add(json17_fault_title_status);esiti_test17.add(true);
		valori_test17.add(400);msg_test17.add(json17_fault_type_title_status);esiti_test17.add(true);
		valori_test17.add(400);msg_test17.add(json17_fault_none);esiti_test17.add(false);
		
		// ricade in default dove vi e' il problem detail originale e quindi un messaggio json di risposta non ha vincoli
		valori_test17.add(404);msg_test17.add(json17_fault_type);esiti_test17.add(true); 
		valori_test17.add(404);msg_test17.add(json17_fault_title);esiti_test17.add(true); 
		valori_test17.add(404);msg_test17.add(json17_fault_status);esiti_test17.add(true); 
		valori_test17.add(404);msg_test17.add(json17_fault_type_title);esiti_test17.add(true); 
		valori_test17.add(404);msg_test17.add(json17_fault_type_status);esiti_test17.add(true);
		valori_test17.add(404);msg_test17.add(json17_fault_title_status);esiti_test17.add(true);
		valori_test17.add(404);msg_test17.add(json17_fault_type_title_status);esiti_test17.add(true);
		valori_test17.add(404);msg_test17.add(json17_fault_none);esiti_test17.add(true);

		// ** Test sul body **
		for (int i = 0; i < valori_test17.size(); i++) {
			Integer code = valori_test17.get(i);
			boolean esito = esiti_test17.get(i);
			String msg = msg_test17.get(i);
			String ct = HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807;
			
			TextHttpResponseEntity httpEntityResponseTest17 = new TextHttpResponseEntity();
			httpEntityResponseTest17.setStatus(code);
			httpEntityResponseTest17.setMethod(HttpRequestMethod.GET);
			httpEntityResponseTest17.setUrl(testUrl17);	
			Map<String, List<String>> parametersTrasportoRispostaTest17 = new HashMap<>();
			TransportUtils.setHeader(parametersTrasportoRispostaTest17,HttpConstants.CONTENT_TYPE, ct);
			httpEntityResponseTest17.setHeaders(parametersTrasportoRispostaTest17);
			httpEntityResponseTest17.setContentType(ct);
			httpEntityResponseTest17.setContent(msg);
			
			String msgRisposta = "None";
			if(json17_fault_type_title.equals(msg)) {
				 msgRisposta = "Type";
			}
			else if(json17_fault_title.equals(msg)) {
				 msgRisposta = "Title";
			}
			else if(json17_fault_status.equals(msg)) {
				 msgRisposta = "Status";
			}
			else if(json17_fault_type_title.equals(msg)) {
				 msgRisposta = "Type e Title";
			}
			else if(json17_fault_type_status.equals(msg)) {
				 msgRisposta = "Type e Status";
			}
			else if(json17_fault_title_status.equals(msg)) {
				 msgRisposta = "Title e Status";
			}
			else if(json17_fault_type_title_status.equals(msg)) {
				 msgRisposta = "Type e Title e Status";
			}
			String tipoTest = "Code:"+code+" "+msgRisposta;
			tipoTest = tipoTest+"["+openAPILibrary+"]";
		
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntityResponseTest17);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>500) {
					error = error.substring(0, 498)+" ...";
				}
				if(!esito) {
					System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
				}
				else {
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				String msgErroreAtteso = null;
				String msgErroreAtteso2 = null;
				String msgErroreAtteso3 = null;
				switch (openAPILibrary) {
				case json_schema:
					msgErroreAtteso = "1028 $.type: is missing but it is required";
					msgErroreAtteso2 = "1028 $.title: is missing but it is required";
					msgErroreAtteso3 = "1028 $.status: is missing but it is required";
					break;
				case openapi4j:
					msgErroreAtteso = "body: Field 'type' is required. (code: 1026)";
					msgErroreAtteso2 = "body: Field 'title' is required. (code: 1026)";
					msgErroreAtteso3 = "body: Field 'status' is required. (code: 1026)";
					break;
				case swagger_request_validator:
					msgErroreAtteso = "* /allOf/0/anyOf/0: Object has missing required properties ([\"type\"])";
					msgErroreAtteso2 = "* /allOf/0/anyOf/1: Object has missing required properties ([\"title\"])";
					msgErroreAtteso3 = "* /allOf/0/anyOf/2: Object has missing required properties ([\"status\"])";
					break;
				}
				if(!e.getMessage().contains(msgErroreAtteso) || !e.getMessage().contains(msgErroreAtteso2) || !e.getMessage().contains(msgErroreAtteso3)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"' o '"+msgErroreAtteso2+"' o '"+msgErroreAtteso3+"'");
				}
				
			}
			
		}
		
		System.out.println("Test #17 completato\n\n");
		
		
		
		
		
		
		
		System.out.println("Test #18 (Risposta GET con parametro dinamico /documenti/XYZ)");
		String testUrl18 = baseUri+"/documenti/qualsiasi/"+UUID.randomUUID().toString();
		
		List<Integer> valori_test18 = new ArrayList<Integer>();
		valori_test18.add(200);
		valori_test18.add(201);
		valori_test18.add(202); // default
		valori_test18.add(203); // defaut
		valori_test18.add(204);
		valori_test18.add(400);
		valori_test18.add(401);
		valori_test18.add(500);
		valori_test18.add(501); // default
		
		List<String> contentTypes_test18 = new ArrayList<String>();
		contentTypes_test18.add(HttpConstants.CONTENT_TYPE_JSON);
		contentTypes_test18.add("jsonUncorrect");
		contentTypes_test18.add(HttpConstants.CONTENT_TYPE_PLAIN);
		contentTypes_test18.add(HttpConstants.CONTENT_TYPE_ZIP);
		
		for (int i = 0; i < valori_test18.size(); i++) {
			Integer code = valori_test18.get(i);
		
			String tipoTest = "Code:"+code+" ";
			tipoTest = tipoTest+"["+openAPILibrary+"]";
						
			TextHttpRequestEntity httpEntityGET_test18 = new TextHttpRequestEntity();
			httpEntityGET_test18.setMethod(HttpRequestMethod.GET);
			httpEntityGET_test18.setUrl(testUrl18);	
			
			try {
				System.out.println("\t "+tipoTest+" validate request ...");
				apiValidatorOpenApi.validate(httpEntityGET_test18);	
				System.out.println("\t "+tipoTest+" validate request ok");
			} catch(ValidatorException e) {
				String error = e.getMessage();
				if(error.length()>500) {
					error = error.substring(0, 498)+" ...";
				}
				System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
				throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
			}
			
			String oldTipoTest = tipoTest;
			
			for (String ct : contentTypes_test18) {
				
				tipoTest = oldTipoTest + " Content-Type:"+ct;
				
				HttpBaseResponseEntity<?> httpEntityResponse_test18 = null;
				if(ct.contains("json") || ct.contains("plain") || ct.contains("Uncorrect")) {
					httpEntityResponse_test18 = new TextHttpResponseEntity();
				}
				else {
					httpEntityResponse_test18 = new BinaryHttpResponseEntity();
				}
				
				httpEntityResponse_test18.setStatus(code);
				httpEntityResponse_test18.setMethod(HttpRequestMethod.GET);
				httpEntityResponse_test18.setUrl(testUrl18);	
				Map<String, List<String>> parametersTrasportoRisposta_test18 = new HashMap<>();
				TransportUtils.setHeader(parametersTrasportoRisposta_test18,"api_key", "aaa");
				TransportUtils.setHeader(parametersTrasportoRisposta_test18,HttpConstants.CONTENT_TYPE, ct);
				httpEntityResponse_test18.setHeaders(parametersTrasportoRisposta_test18);
				httpEntityResponse_test18.setContentType(ct);
				
				if(ct.contains("json") || ct.contains("plain")) {
					if(ct.contains("Uncorrect")) {
						((TextHttpResponseEntity)httpEntityResponse_test18).setContent("{ a }");
						parametersTrasportoRisposta_test18.remove(HttpConstants.CONTENT_TYPE);
						TransportUtils.setHeader(parametersTrasportoRisposta_test18,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
						httpEntityResponse_test18.setContentType(HttpConstants.CONTENT_TYPE_JSON);
					}
					else if(ct.contains("plain")) {
						((TextHttpResponseEntity)httpEntityResponse_test18).setContent("Hello World!");
					}
					else {
						((TextHttpResponseEntity)httpEntityResponse_test18).setContent(json); // volutamente metto un json che comunque dovrebbe trattare come binario!		
					}
				}
				else {
					((BinaryHttpResponseEntity)httpEntityResponse_test18).setContent(pdf);
				}
				
				try {
					System.out.println("\t "+tipoTest+" validate response ...");
					apiValidatorOpenApi.validate(httpEntityResponse_test18);	
					System.out.println("\t "+tipoTest+" validate response ok");
					
					if((openAPILibrary != OpenAPILibrary.json_schema) && (ct.contains("Uncorrect") || !ct.contains("json"))) {
						throw new Exception("Attesa eccezione");
					}
					
				} catch(ValidatorException e) {
					String error = e.getMessage();
					if(error.length()>500) {
						error = error.substring(0, 498)+" ...";
					}
					String erroreAtteso = null;
					if(ct.contains("Uncorrect")) {
						erroreAtteso = "Unexpected character ('a' (code 97))";
					}
					else if(!ct.contains("json")) {
						
						erroreAtteso = null;
						switch (openAPILibrary) {
						case json_schema:
							// non non si passa qua
							break;
						case openapi4j:
							erroreAtteso = "body: Type expected 'object', found 'string'. (code: 1027)";
							break;
						case swagger_request_validator:
							erroreAtteso = "[ERROR] Unable to parse JSON";
							break;
						}

					}
					if(erroreAtteso!=null) {
						if(!e.getMessage().contains(erroreAtteso)) {
							System.out.println("\t "+tipoTest+" rilevato errore di validazione diverso da quello atteso ("+erroreAtteso+") : "+error);
							throw new Exception(""+tipoTest+" rilevato errore di validazione diverso da quello atteso ("+erroreAtteso+") : "+e.getMessage(),e);
						}
						else {
							System.out.println("\t "+tipoTest+" rilevato errore di validazione atteso: "+error);
						}
					}
					else {
						System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
						throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
					}
				}
			}
		}
		System.out.println("Test #18 completato\n\n");
		
		
		
		
		
		
		
		
		
		// ** Test su enumeration con valori NO,YES,... **
		
		System.out.println("Test #19 (Enumeration con valori NO, YES ...) ...");
		String testUrl19 = baseUri+"/test-enum-no-yes";
		
		List<Object> valori_test19 = new ArrayList<Object>();
		List<Boolean> esito_test19 = new ArrayList<Boolean>();
		valori_test19.add("SI"); esito_test19.add(true);
		valori_test19.add("NO"); esito_test19.add(true);
		valori_test19.add("YES"); esito_test19.add(true);
		valori_test19.add("S"); esito_test19.add(true);
		valori_test19.add("N"); esito_test19.add(true);
		valori_test19.add("Y"); esito_test19.add(true);
		valori_test19.add("0"); esito_test19.add(true);
		valori_test19.add("1"); esito_test19.add(true);
		valori_test19.add("ON"); esito_test19.add(true);
		valori_test19.add("OFF"); esito_test19.add(true);
		valori_test19.add(0); esito_test19.add(false);
		valori_test19.add(1); esito_test19.add(false);
		valori_test19.add(false); esito_test19.add(false);
		valori_test19.add(true); esito_test19.add(false);
		
		int TEST_NUMERO = 1;
		// TODO: impostare a 2 per verificare ISSUE 'OP-1136' e risolvere problematica enum non quotata con i valori previsti in yaml1
		//TEST_NUMERO = 2;
		
		for (int k = 0; k < TEST_NUMERO; k++) {
			
			String testYaml = "[test con enum quotata] ";
			if(k==1) {
				testUrl19 = testUrl19 + "-yaml1";
				testYaml = "[test con enum non quotata] ";
			}
			
			for (int i = 0; i < valori_test19.size(); i++) {
				Object valore = valori_test19.get(i);
				boolean esito = esito_test19.get(i);
				
				String valoreInserito = null;
				if(valore instanceof String || valore instanceof Boolean) {
					valoreInserito = "\""+valore+"\"";
					if(valore instanceof String) {
						String v = (String) valore;
						if(v.equals("ON") || v.equals("OFF")) {
							if(k==1) {
								testUrl19 = testUrl19 + "-onOff";
							}
						}
					}
				}
				else {
					valoreInserito = valore.toString();
				}
				
				TextHttpRequestEntity httpEntity19 = new TextHttpRequestEntity();
				httpEntity19.setMethod(HttpRequestMethod.POST);
				httpEntity19.setUrl(testUrl19); 
				Map<String, List<String>> parametersTrasporto19 = new HashMap<>();
				TransportUtils.setHeader(parametersTrasporto19, HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
				httpEntity19.setHeaders(parametersTrasporto19);
				httpEntity19.setContentType(HttpConstants.CONTENT_TYPE_JSON);  // uso data valida, il test e' path
				String json19 = "{\"stato1\": "+valoreInserito.toUpperCase()+",\"stato2\": "+valoreInserito.toLowerCase()+"}";
				httpEntity19.setContent(json19);
				
				
				TextHttpResponseEntity httpEntityResponse_test19 = new TextHttpResponseEntity();
				httpEntityResponse_test19.setStatus(200);
				httpEntityResponse_test19.setMethod(HttpRequestMethod.POST);
				httpEntityResponse_test19.setUrl(testUrl19);	
				Map<String, List<String>> parametersTrasportoRisposta_test19 = new HashMap<>();
				TransportUtils.setHeader(parametersTrasportoRisposta_test19,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
				httpEntityResponse_test19.setHeaders(parametersTrasportoRisposta_test19);
				httpEntityResponse_test19.setContentType(HttpConstants.CONTENT_TYPE_JSON);
				httpEntityResponse_test19.setContent(json19);
	
				
				String tipoTest = testYaml+ (esito ? "[stato con valore (type:"+valore.getClass().getName()+") '"+valore+"']" : "[stato con valore errato (type:"+valore.getClass().getName()+") '"+valore+"']");
				tipoTest = tipoTest+"["+openAPILibrary+"]";
								
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidatorOpenApi.validate(httpEntity19);
					if(esito) {
						System.out.println("\t "+tipoTest+" validate ok");
					}
					else {
						System.out.println("\t "+tipoTest+" ERRORE!");
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					}
				} catch(ValidatorException e) {
					String error = e.getMessage();
					if(error.length()>200) {
						error = error.substring(0, 198)+" ...";
					}
					if(!esito) {
						System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
					}
					else {
						System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
						throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
					}
					
					String msgErroreAtteso = null;
					switch (openAPILibrary) {
					case json_schema:
						if(valore instanceof Integer) {
							String tipoJava = valore instanceof Integer ? "integer" : "boolean";
							msgErroreAtteso = "1029 $.stato2: "+tipoJava+" found, string expected";
						}
						break;
					case openapi4j:
						if(valore instanceof Integer) {
							msgErroreAtteso = "Type expected 'string', found 'integer'. (code: 1027)"; 
						}
						else {
							msgErroreAtteso = "body.stato1: Value '"+valore.toString().toUpperCase()+"' is not defined in the schema. (code: 1006)"; 
						}
						break;
					case swagger_request_validator:
						if(valore instanceof Integer) {
							msgErroreAtteso = "[ERROR][REQUEST][POST /test-enum-no-yes @body] [Path '/stato1'] Instance value ("+valore+") not found in enum (possible values: [\"SI\",\"NO\""; 
						}
						else {
							msgErroreAtteso = "[ERROR][REQUEST][POST /test-enum-no-yes @body] [Path '/stato1'] Instance value (\""+valore.toString().toUpperCase()+"\") not found in enum (possible values: [\"SI\",\"NO\",\"YES\",\"S\",\"N\""; 
						}
						break;
					}
					if(msgErroreAtteso!=null && !e.getMessage().contains(msgErroreAtteso)) {
						System.out.println("\t "+tipoTest+" ERRORE!");
						throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
					}
					
					msgErroreAtteso = null;
					switch (openAPILibrary) {
					case json_schema:
						if(valore instanceof Integer) {
							String tipoJava = valore instanceof Integer ? "integer" : "boolean";
							msgErroreAtteso = "1029 $.stato1: "+tipoJava+" found, string expected";
						}
						break;
					case openapi4j:
						if(valore instanceof Integer) {
							msgErroreAtteso = "Value '"+valore+"' is not defined in the schema. (code: 1006)";
						}
						else {
							msgErroreAtteso = "body.stato2: Value '"+valore.toString().toLowerCase()+"' is not defined in the schema. (code: 1006)"; 
						}
						break;
					case swagger_request_validator:
						if(valore instanceof Integer) {
							msgErroreAtteso = "[ERROR][REQUEST][POST /test-enum-no-yes @body] [Path '/stato1'] Instance value ("+valore+") not found in enum (possible values: [\"SI\",\"NO\",\"YES\",\"S\",\"N\",\"Y\",\"";							
						}
						else {
							msgErroreAtteso = "[ERROR][REQUEST][POST /test-enum-no-yes @body] [Path '/stato1'] Instance value (\""+valore.toString().toUpperCase()+"\") not found in enum (possible values: [\"SI\",\"NO\",\"YES\",\"S\",\"N\"";							
						}
						break;
					}
					if(msgErroreAtteso!=null && !e.getMessage().contains(msgErroreAtteso)) {
						System.out.println("\t "+tipoTest+" ERRORE!");
						throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
					}
					
					if(openAPILibrary == OpenAPILibrary.json_schema) {
						msgErroreAtteso = "does not have a value in the enumeration [si, no, yes, s, n, y, 0, 1, on, off]";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
				}
				
				try {
					System.out.println("\t "+tipoTest+" validate response ...");
					apiValidatorOpenApi.validate(httpEntityResponse_test19);	
					System.out.println("\t "+tipoTest+" validate response ok");
				} catch(ValidatorException e) {
					String error = e.getMessage();
					if(error.length()>200) {
						error = error.substring(0, 198)+" ...";
					}
					if(!esito) {
						System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
					}
					else {
						System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
						throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
					}
					
					String msgErroreAtteso = null;
					switch (openAPILibrary) {
					case json_schema:
						if(valore instanceof Integer) {
							String tipoJava = valore instanceof Integer ? "integer" : "boolean";
							msgErroreAtteso = "1029 $.stato2: "+tipoJava+" found, string expected";
						}
						break;
					case openapi4j:
						if(valore instanceof Integer) {
							msgErroreAtteso = "Type expected 'string', found 'integer'. (code: 1027)";									
						}
						else {
							msgErroreAtteso = "body.stato1: Value '"+valore.toString().toUpperCase()+"' is not defined in the schema. (code: 1006)";
						}
						break;
					case swagger_request_validator:
						if(valore instanceof Integer) {
							msgErroreAtteso = "[ERROR][RESPONSE][] [Path '/stato1'] Instance value ("+valore+") not found in enum (possible values: [\"SI\",\"NO\",\"YES\",\"S\",\"N\",\"Y\",\"0\",\"1\",\"ON\",\"OFF\"])";															
						}
						else {
							msgErroreAtteso = "[ERROR][RESPONSE][] [Path '/stato1'] Instance value (\""+valore.toString().toUpperCase()+"\") not found in enum (possible values: [\"SI\",\"NO\",\"YES\",\"S\",\"N\",\"Y\",\"0\",\"1\",\"ON\",\"OFF\"])"; 
						}
						break;
					}
					if(msgErroreAtteso!=null && !e.getMessage().contains(msgErroreAtteso)) {
						System.out.println("\t "+tipoTest+" ERRORE!");
						throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
					}
					
					msgErroreAtteso = null;
					switch (openAPILibrary) {
					case json_schema:
						if(valore instanceof Integer) {
							String tipoJava = valore instanceof Integer ? "integer" : "boolean";
							msgErroreAtteso = "1029 $.stato1: "+tipoJava+" found, string expected";
						}
						break;
					case openapi4j:
						if(valore instanceof Integer) {
							msgErroreAtteso = "Value '"+valore+"' is not defined in the schema. (code: 1006)";
						}
						else {
							msgErroreAtteso = "body.stato2: Value '"+valore.toString().toLowerCase()+"' is not defined in the schema. (code: 1006)";
						}
						break;
					case swagger_request_validator:
						if(valore instanceof Integer) {
							msgErroreAtteso = "[ERROR][RESPONSE][] [Path '/stato1'] Instance value ("+valore+") not found in enum (possible values: [\"SI\",\"NO\",\"YES\",\"S\",\"N\",\"Y\",\"0\",\"1\",\"ON\",\"OFF\"])";
						}
						else {
							msgErroreAtteso = "[ERROR][RESPONSE][] [Path '/stato1'] Instance value (\""+valore.toString().toUpperCase()+"\") not found in enum (possible values: [\"SI\",\"NO\",\"YES\",\"S\",\"N\",\"Y\",\"0\",\"1\",\"ON\",\"OFF\"])";
						}
						break;
					}
					if(msgErroreAtteso!=null && !e.getMessage().contains(msgErroreAtteso)) {
						System.out.println("\t "+tipoTest+" ERRORE!");
						throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
					}
					
					if(openAPILibrary == OpenAPILibrary.json_schema) {
						msgErroreAtteso = "does not have a value in the enumeration [si, no, yes, s, n, y, 0, 1, on, off]";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
				}
			}
		}
		
		System.out.println("Test #19 (Enumeration con valori NO, YES ...) completato\n\n");
		
		
		
		
		
		
		
		
		// ** Test su oggetti che contengono elementi nullable ... **
		
		System.out.println("Test #20 (Elementi nullable ...) ...");
		String baseTestUrl20 = baseUri;
		
		String json20_object = "{"+
				"\"identificativo\": \"IDXX\", \"stato\": \"ON\""+
				", \"valoreintero\": 3, \"descrizione\": \"esempio di test\""+
				"}";
		
		String json20_messaggio_valorizzato = "{"+
					"\"string_required\": \"ValoreEsempio\", \"string_optional\": \"ValoreEsempio2\""+
					", \"enum_required\": \"ON\", \"enum_optional\": \"OFF\""+
					", \"integer_required\": 2, \"integer_optional\": 3"+
					", \"boolean_required\": false, \"boolean_optional\": true"+
					", \"array_required\": [\"ValoreEsempio\",\"ValoreEsempio2\" ], \"array_optional\": [\"ValoreEsempio\",\"ValoreEsempio2\" ]"+
					", \"array_nullable_values_required\": [\"ValoreEsempio\",\"ValoreEsempio2\" ], \"array_nullable_values_optional\": [\"ValoreEsempio\",\"ValoreEsempio2\" ]"+
					", \"object_required\": "+json20_object+",\"object_optional\": "+json20_object+
					"}";
		
		String json20_messaggio_non_valorizzato_string = "{"+
				"\"string_required\": null, \"string_optional\": null"+
				", \"enum_required\": \"ON\", \"enum_optional\": \"OFF\""+
				", \"integer_required\": 2, \"integer_optional\": 3"+
				", \"boolean_required\": false, \"boolean_optional\": true"+
				", \"array_required\": [\"ValoreEsempio\",\"ValoreEsempio2\" ], \"array_optional\": [\"ValoreEsempio\",\"ValoreEsempio2\" ]"+
				", \"array_nullable_values_required\": [\"ValoreEsempio\",\"ValoreEsempio2\" ], \"array_nullable_values_optional\": [\"ValoreEsempio\",\"ValoreEsempio2\" ]"+
				", \"object_required\": "+json20_object+",\"object_optional\": "+json20_object+
				"}";
		
		String json20_messaggio_non_valorizzato_enum = "{"+
				"\"string_required\": \"ValoreEsempio\", \"string_optional\": \"ValoreEsempio2\""+
				", \"enum_required\": null, \"enum_optional\": null"+
				", \"integer_required\": 2, \"integer_optional\": 3"+
				", \"boolean_required\": false, \"boolean_optional\": true"+
				", \"array_required\": [\"ValoreEsempio\",\"ValoreEsempio2\" ], \"array_optional\": [\"ValoreEsempio\",\"ValoreEsempio2\" ]"+
				", \"array_nullable_values_required\": [\"ValoreEsempio\",\"ValoreEsempio2\" ], \"array_nullable_values_optional\": [\"ValoreEsempio\",\"ValoreEsempio2\" ]"+
				", \"object_required\": "+json20_object+",\"object_optional\": "+json20_object+
				"}";

		String json20_messaggio_non_valorizzato_integer = "{"+
				"\"string_required\": \"ValoreEsempio\", \"string_optional\": \"ValoreEsempio2\""+
				", \"enum_required\": \"ON\", \"enum_optional\": \"OFF\""+
				", \"integer_required\": null, \"integer_optional\": null"+
				", \"boolean_required\": false, \"boolean_optional\": true"+
				", \"array_required\": [\"ValoreEsempio\",\"ValoreEsempio2\" ], \"array_optional\": [\"ValoreEsempio\",\"ValoreEsempio2\" ]"+
				", \"array_nullable_values_required\": [\"ValoreEsempio\",\"ValoreEsempio2\" ], \"array_nullable_values_optional\": [\"ValoreEsempio\",\"ValoreEsempio2\" ]"+
				", \"object_required\": "+json20_object+",\"object_optional\": "+json20_object+
				"}";
		
		String json20_messaggio_non_valorizzato_boolean = "{"+
				"\"string_required\": \"ValoreEsempio\", \"string_optional\": \"ValoreEsempio2\""+
				", \"enum_required\": \"ON\", \"enum_optional\": \"OFF\""+
				", \"integer_required\": 2, \"integer_optional\": 3"+
				", \"boolean_required\": null, \"boolean_optional\": null"+
				", \"array_required\": [\"ValoreEsempio\",\"ValoreEsempio2\" ], \"array_optional\": [\"ValoreEsempio\",\"ValoreEsempio2\" ]"+
				", \"array_nullable_values_required\": [\"ValoreEsempio\",\"ValoreEsempio2\" ], \"array_nullable_values_optional\": [\"ValoreEsempio\",\"ValoreEsempio2\" ]"+
				", \"object_required\": "+json20_object+",\"object_optional\": "+json20_object+
				"}";
		
		String json20_messaggio_non_valorizzato_array = "{"+
				"\"string_required\": \"ValoreEsempio\", \"string_optional\": \"ValoreEsempio2\""+
				", \"enum_required\": \"ON\", \"enum_optional\": \"OFF\""+
				", \"integer_required\": 2, \"integer_optional\": 3"+
				", \"boolean_required\": false, \"boolean_optional\": true"+
				", \"array_required\": null, \"array_optional\": null"+
				", \"array_nullable_values_required\": [\"ValoreEsempio\",\"ValoreEsempio2\" ], \"array_nullable_values_optional\": [\"ValoreEsempio\",\"ValoreEsempio2\" ]"+
				", \"object_required\": "+json20_object+",\"object_optional\": "+json20_object+
				"}";
		
		String json20_messaggio_non_valorizzato_array_values = "{"+
				"\"string_required\": \"ValoreEsempio\", \"string_optional\": \"ValoreEsempio2\""+
				", \"enum_required\": \"ON\", \"enum_optional\": \"OFF\""+
				", \"integer_required\": 2, \"integer_optional\": 3"+
				", \"boolean_required\": false, \"boolean_optional\": true"+
				", \"array_required\": [\"ValoreEsempio\",\"ValoreEsempio2\" ], \"array_optional\": [\"ValoreEsempio\",\"ValoreEsempio2\" ]"+
				", \"array_nullable_values_required\": [null,\"ValoreEsempio2\" ], \"array_nullable_values_optional\": [null,null]"+
				", \"object_required\": "+json20_object+",\"object_optional\": "+json20_object+
				"}";
		
		String json20_messaggio_valorizzato_array_vuoto = "{"+
				"\"string_required\": \"ValoreEsempio\", \"string_optional\": \"ValoreEsempio2\""+
				", \"enum_required\": \"ON\", \"enum_optional\": \"OFF\""+
				", \"integer_required\": 2, \"integer_optional\": 3"+
				", \"boolean_required\": false, \"boolean_optional\": true"+
				", \"array_required\": [], \"array_optional\": []"+
				", \"array_nullable_values_required\": [], \"array_nullable_values_optional\": []"+
				", \"object_required\": "+json20_object+",\"object_optional\": "+json20_object+
				"}";
		
		String json20_messaggio_non_valorizzato_object = "{"+
				"\"string_required\": \"ValoreEsempio\", \"string_optional\": \"ValoreEsempio2\""+
				", \"enum_required\": \"ON\", \"enum_optional\": \"OFF\""+
				", \"integer_required\": 2, \"integer_optional\": 3"+
				", \"boolean_required\": false, \"boolean_optional\": true"+
				", \"array_required\": [\"ValoreEsempio\",\"ValoreEsempio2\" ], \"array_optional\": [\"ValoreEsempio\",\"ValoreEsempio2\" ]"+
				", \"array_nullable_values_required\": [\"ValoreEsempio\",\"ValoreEsempio2\" ], \"array_nullable_values_optional\": [\"ValoreEsempio\",\"ValoreEsempio2\" ]"+
				", \"object_required\": null,\"object_optional\": null"+
				"}";
		
		List<String> valori_test20 = new ArrayList<String>();
		List<Boolean> esito_test20 = new ArrayList<Boolean>();
		List<String> tipoTest_test20 = new ArrayList<String>();
		List<String> path_test20 = new ArrayList<String>();
		
		tipoTest_test20.add("ValorizzazioneCompleta"); valori_test20.add(json20_messaggio_valorizzato); esito_test20.add(true); path_test20.add("test-nullable");
		tipoTest_test20.add("ValorizzazioneCompleta"); valori_test20.add(json20_messaggio_valorizzato); esito_test20.add(true); path_test20.add("test-not-nullable");
		tipoTest_test20.add("ValorizzazioneCompleta"); valori_test20.add(json20_messaggio_valorizzato); esito_test20.add(true); path_test20.add("test-not-nullable-default");
		
		tipoTest_test20.add("StringNull"); valori_test20.add(json20_messaggio_non_valorizzato_string); esito_test20.add(true); path_test20.add("test-nullable");
		tipoTest_test20.add("StringNull"); valori_test20.add(json20_messaggio_non_valorizzato_string); esito_test20.add(false); path_test20.add("test-not-nullable");
		tipoTest_test20.add("StringNull"); valori_test20.add(json20_messaggio_non_valorizzato_string); esito_test20.add(false); path_test20.add("test-not-nullable-default");
				
		tipoTest_test20.add("EnumNull"); valori_test20.add(json20_messaggio_non_valorizzato_enum); esito_test20.add(true); path_test20.add("test-nullable");
		tipoTest_test20.add("EnumNull"); valori_test20.add(json20_messaggio_non_valorizzato_enum); esito_test20.add(false); path_test20.add("test-not-nullable");
		tipoTest_test20.add("EnumNull"); valori_test20.add(json20_messaggio_non_valorizzato_enum); esito_test20.add(false); path_test20.add("test-not-nullable-default");
		
		tipoTest_test20.add("IntegerNull"); valori_test20.add(json20_messaggio_non_valorizzato_integer); esito_test20.add(true); path_test20.add("test-nullable");
		tipoTest_test20.add("IntegerNull"); valori_test20.add(json20_messaggio_non_valorizzato_integer); esito_test20.add(false); path_test20.add("test-not-nullable");
		tipoTest_test20.add("IntegerNull"); valori_test20.add(json20_messaggio_non_valorizzato_integer); esito_test20.add(false); path_test20.add("test-not-nullable-default");
		
		tipoTest_test20.add("BooleanNull"); valori_test20.add(json20_messaggio_non_valorizzato_boolean); esito_test20.add(true); path_test20.add("test-nullable");
		tipoTest_test20.add("BooleanNull"); valori_test20.add(json20_messaggio_non_valorizzato_boolean); esito_test20.add(false); path_test20.add("test-not-nullable");
		tipoTest_test20.add("BooleanNull"); valori_test20.add(json20_messaggio_non_valorizzato_boolean); esito_test20.add(false); path_test20.add("test-not-nullable-default");
		
		tipoTest_test20.add("ArrayNull"); valori_test20.add(json20_messaggio_non_valorizzato_array); esito_test20.add(true); path_test20.add("test-nullable");
		tipoTest_test20.add("ArrayNull"); valori_test20.add(json20_messaggio_non_valorizzato_array); esito_test20.add(false); path_test20.add("test-not-nullable");
		tipoTest_test20.add("ArrayNull"); valori_test20.add(json20_messaggio_non_valorizzato_array); esito_test20.add(false); path_test20.add("test-not-nullable-default");
		
		tipoTest_test20.add("ArrayValuesNull"); valori_test20.add(json20_messaggio_non_valorizzato_array_values); esito_test20.add(true); path_test20.add("test-nullable");
		tipoTest_test20.add("ArrayValuesNull"); valori_test20.add(json20_messaggio_non_valorizzato_array_values); esito_test20.add(false); path_test20.add("test-not-nullable");
		tipoTest_test20.add("ArrayValuesNull"); valori_test20.add(json20_messaggio_non_valorizzato_array_values); esito_test20.add(false); path_test20.add("test-not-nullable-default");
		
		tipoTest_test20.add("ArrayEmpty"); valori_test20.add(json20_messaggio_valorizzato_array_vuoto); esito_test20.add(true); path_test20.add("test-nullable");
		tipoTest_test20.add("ArrayEmpty"); valori_test20.add(json20_messaggio_valorizzato_array_vuoto); esito_test20.add(true); path_test20.add("test-not-nullable");
		tipoTest_test20.add("ArrayEmpty"); valori_test20.add(json20_messaggio_valorizzato_array_vuoto); esito_test20.add(true); path_test20.add("test-not-nullable-default");
		
		tipoTest_test20.add("ObjectNull"); valori_test20.add(json20_messaggio_non_valorizzato_object); esito_test20.add(true); path_test20.add("test-nullable");
		tipoTest_test20.add("ObjectNull"); valori_test20.add(json20_messaggio_non_valorizzato_object); esito_test20.add(false); path_test20.add("test-not-nullable");
		tipoTest_test20.add("ObjectNull"); valori_test20.add(json20_messaggio_non_valorizzato_object); esito_test20.add(false); path_test20.add("test-not-nullable-default");
		
		
		
		String testYaml = "[test con elementi nullable] ";
		
		for (int i = 0; i < valori_test20.size(); i++) {
			String tipologia = tipoTest_test20.get(i);
			String valore = valori_test20.get(i);
			boolean esito = esito_test20.get(i);
			String contextTestPath = path_test20.get(i);
			
			String testUrl20 = baseTestUrl20+"/"+contextTestPath;
			
			TextHttpRequestEntity httpEntity20 = new TextHttpRequestEntity();
			httpEntity20.setMethod(HttpRequestMethod.POST);
			httpEntity20.setUrl(testUrl20); 
			Map<String, List<String>> parametersTrasporto20 = new HashMap<>();
			TransportUtils.setHeader(parametersTrasporto20, HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			httpEntity20.setHeaders(parametersTrasporto20);
			httpEntity20.setContentType(HttpConstants.CONTENT_TYPE_JSON); 
			httpEntity20.setContent(valore);
			
			
			TextHttpResponseEntity httpEntityResponse_test20 = new TextHttpResponseEntity();
			httpEntityResponse_test20.setStatus(200);
			httpEntityResponse_test20.setMethod(HttpRequestMethod.POST);
			httpEntityResponse_test20.setUrl(testUrl20);	
			Map<String, List<String>> parametersTrasportoRisposta_test20 = new HashMap<>();
			TransportUtils.setHeader(parametersTrasportoRisposta_test20,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			httpEntityResponse_test20.setHeaders(parametersTrasportoRisposta_test20);
			httpEntityResponse_test20.setContentType(HttpConstants.CONTENT_TYPE_JSON);
			httpEntityResponse_test20.setContent(valore);

			
			String tipoTest = testYaml+ (esito ? "["+tipologia+" [/"+contextTestPath+"] MessaggioConforme: '"+valore+"']" : "["+tipologia+" [/"+contextTestPath+"] MessaggioNonConforme: '"+valore+"']");
			tipoTest = tipoTest+"["+openAPILibrary+"]";
			
			try {
				System.out.println("\t "+tipoTest+" validate ...");
				apiValidatorOpenApi.validate(httpEntity20);
				if(esito) {
					System.out.println("\t "+tipoTest+" validate ok");
				}
				else {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
				}
			} catch(ValidatorException e) {
				checkErrorTest20(esito, tipoTest, e, tipologia, openAPILibrary);
			}
			
			try {
				System.out.println("\t "+tipoTest+" validate response ...");
				apiValidatorOpenApi.validate(httpEntityResponse_test20);	
				System.out.println("\t "+tipoTest+" validate response ok");
			} catch(ValidatorException e) {
				checkErrorTest20(esito, tipoTest, e, tipologia, openAPILibrary);
			}
		}
		
		System.out.println("Test #20 (Elementi nullable) completato\n\n");
		
		
		
		
		
		// ** Test su discriminator ... **
		
		System.out.println("Test #21 Discriminator ...");
		String testUrl21Base = baseUri+"/pets";
		
		String cat = "{\"pet_type\": \"Cat\",  \"age\": 3}";
		String dog1 = "{\"pet_type\": \"Dog\",  \"bark\": false,  \"breed\": \"Dingo\" }";
		String dog2 = "{\"pet_type\": \"Dog\",  \"bark\": true }";
		List<String> tipoTest_test21 = new ArrayList<String>();
		tipoTest_test21.add(cat);
		tipoTest_test21.add(dog1);
		tipoTest_test21.add(dog2);
		
		int NUMERO_RISORSE_PET = 5;
		
		Set<Integer> swagger_validator_fallimenti_allof = Set.of(0,3);
		
		for (int k = 0; k < NUMERO_RISORSE_PET; k++) {
						
			int numeroPet = (k+1);
			String testPet = "[test pets"+numeroPet+"] ";
			String testUrl21 = testUrl21Base+numeroPet;
			
			for (String contenutoParam : tipoTest_test21) {
				
				String contenuto = contenutoParam;
				if(k==1 || k==2 || k==4) {
					contenuto = "{\"altro\":\"descrizione generica\", \"pet\":"+contenutoParam+"}";
				}
				
				TextHttpRequestEntity httpEntity21 = new TextHttpRequestEntity();
				httpEntity21.setMethod(HttpRequestMethod.PATCH);
				httpEntity21.setUrl(testUrl21); 
				Map<String, List<String>> parametersTrasporto19 = new HashMap<>();
				TransportUtils.setHeader(parametersTrasporto19, HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
				httpEntity21.setHeaders(parametersTrasporto19);
				httpEntity21.setContentType(HttpConstants.CONTENT_TYPE_JSON);  // uso data valida, il test e' path
				httpEntity21.setContent(contenuto);
				
				TextHttpResponseEntity httpEntityResponse_test21 = new TextHttpResponseEntity();
				httpEntityResponse_test21.setStatus(200);
				httpEntityResponse_test21.setMethod(HttpRequestMethod.PATCH);
				httpEntityResponse_test21.setUrl(testUrl21);	
				Map<String, List<String>> parametersTrasportoRisposta_test21 = new HashMap<>();
				TransportUtils.setHeader(parametersTrasportoRisposta_test21,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
				httpEntityResponse_test21.setHeaders(parametersTrasportoRisposta_test21);
				httpEntityResponse_test21.setContentType(HttpConstants.CONTENT_TYPE_JSON);
				httpEntityResponse_test21.setContent(contenuto);
				
				String tipoTest = testPet+" ";
				tipoTest = tipoTest+"["+openAPILibrary+"]";
				tipoTest = tipoTest + "(url:"+testUrl21+") contenuto("+contenuto+")";
				
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					
					if(openAPILibrary == OpenAPILibrary.json_schema) {
						
						//  la libreria json schema normale non supporta il discriminator e si ottiene un errore simile al seguente se si abilita: 1022 $: should be valid to one and only one of the schemas  
						System.out.println("Discriminator con allOf non supportato da "+openAPILibrary);
						continue;
						
					}
					else if (openAPILibrary == OpenAPILibrary.swagger_request_validator && swagger_validator_fallimenti_allof.contains(k)) {
						
						// TODO: 
						/*
						Presenta problemi durante la validazione di oggetti oneOf allOf e/o con discriminator.
						Gli sviluppatori di atlassian sono al corrente della situazione e contano di migrare a una nuova libreria di validazione degli 
						schemi che supporti la draft v7 (dove questi problemi sembrano fixati o attenuati) fino alla v12 utilizzata tra l'altro da OpenAPI3.1.
						*/
						
						/*
						 * https://bitbucket.org/atlassian/swagger-request-validator/issues/269/validation-loop-error-occurred-when-allof
						   https://bitbucket.org/atlassian/swagger-request-validator/issues/349/v3-replace-json-schema-validation-engine
						 * */
						
						// Si ottiene un errore simile al seguente: Caused by: org.openspcoop2.utils.rest.ValidatorException: Validation failed.
						// [ERROR][REQUEST][PATCH /pets1 @body] Validation loop: schema "#/components/schemas/Cat" visited twice for pointer "" of validated instance
						
						System.out.println("Discriminator con allOf non supportato da swagger-request-validator!");
						continue;
					}
					
					
					apiValidatorOpenApi.validate(httpEntity21);
					System.out.println("\t "+tipoTest+" validate ok");
				} catch(ValidatorException e) {
					String error = e.getMessage();
					if(error.length()>200) {
						error = error.substring(0, 198)+" ...";
					}
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
				
				try {
					System.out.println("\t "+tipoTest+" validate response ...");
					apiValidatorOpenApi.validate(httpEntityResponse_test21);	
					System.out.println("\t "+tipoTest+" validate response ok");
				} catch(ValidatorException e) {
					String error = e.getMessage();
					if(error.length()>200) {
						error = error.substring(0, 198)+" ...";
					}
					System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
					throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
				}
			}
		}
		
		System.out.println("Test #21 Discriminator completato\n\n");
		
		
		
		
		
		// ** Test per validazione json con wildcard nel subtype ... **
		
		System.out.println("Test #22 validazione json con wildcard nel subtype ...");
		
		// *** TEST per validazione json con wildcard nel subtype *** //
		testSubMediatypeWildcardJsonValidation(openAPILibrary, mergeSpec, true);
		testSubMediatypeWildcardJsonValidation(openAPILibrary, mergeSpec, false);
		
		System.out.println("Test #22 validazione json con wildcard nel subtype completato\n\n");
		
		
			
		
		// ** Test per validazione contenuto base64 ... **
		
		System.out.println("Test #23 validazione contenuto base64 ...");
		
		if (openAPILibrary == OpenAPILibrary.openapi4j || openAPILibrary == OpenAPILibrary.swagger_request_validator)  {
			boolean oldValue = false;
			try {
				if (openAPILibrary == OpenAPILibrary.openapi4j) {
					// per default è disabilitata
					oldValue = org.openapi4j.schema.validator.v3.ValidationOptions.VALIDATE_BASE64_VALUES;
					org.openapi4j.schema.validator.v3.ValidationOptions.VALIDATE_BASE64_VALUES=true;
				}
				testBase64Validation(openAPILibrary, mergeSpec, apiValidatorOpenApi);
			}finally {
				if (openAPILibrary == OpenAPILibrary.openapi4j) {
					org.openapi4j.schema.validator.v3.ValidationOptions.VALIDATE_BASE64_VALUES=oldValue;
				}
			}
		} else {
			System.out.println("Skippo Test validazione file upload in base64 per libreria differente da swagger_request_validator e openapi4j");
		}
		
		System.out.println("Test #23 validazione contenuto base64 completato\n\n");
		
		
		
		
		// ** Test per validazione con openapi che usano testMergeKey ... **
		
		System.out.println("Test #24 openapi che usano testMergeKey ...");
		
		testMergeKey(openAPILibrary, mergeSpec);
		
		System.out.println("Test #24 openapi che usano testMergeKey completato\n\n");
		
		
		
		// ** Test per validazione con openapi che usano multipart request ... **
		
		System.out.println("Test #25 openapi che usano multipart request ...");
		
		if (openAPILibrary == OpenAPILibrary.openapi4j)  {
		
			boolean stream = true;
			boolean multipartOptimization = true;
			
			testMultipartRequest(openAPILibrary, mergeSpec, !stream, !multipartOptimization);
			testMultipartRequest(openAPILibrary, mergeSpec, stream, !multipartOptimization);
			testMultipartRequest(openAPILibrary, mergeSpec, !stream, multipartOptimization);
			testMultipartRequest(openAPILibrary, mergeSpec, stream, multipartOptimization);
			
			testMultipartRequestAsArray(openAPILibrary, mergeSpec, !stream, !multipartOptimization);
			testMultipartRequestAsArray(openAPILibrary, mergeSpec, stream, !multipartOptimization);
			testMultipartRequestAsArray(openAPILibrary, mergeSpec, !stream, multipartOptimization);
			testMultipartRequestAsArray(openAPILibrary, mergeSpec, stream, multipartOptimization);
			
		}
		else {
			
			System.out.println("Skippo Test multipart request per libreria differente da openapi4j");
			
			// swagger request: https://bitbucket.org/atlassian/swagger-request-validator/issues/149/v20-add-support-for-multipart-form-data
			
		}
		System.out.println("Test #25 openapi che usano multipart request completato\n\n");
		
	
		
		// ** Test per validazione con openapi che usano format string ... **
		
		System.out.println("Test #26 openapi che usano format string ...");
		
		testFormatString(openAPILibrary, mergeSpec);
		
		System.out.println("Test #26 openapi che usano format string completato\n\n");
		
		
		
		// ** Test per validazione con openapi che usano path dinamici ... **
		
		System.out.println("Test #27 openapi che usano path dinamici ...");
		
		testDynamicPath(openAPILibrary, mergeSpec);
	
		System.out.println("Test #27 openapi che usano path dinamici completato\n\n");
		
		// ** Test per validazione con openapi che usano parametri definiti con schemi composti ... **
		
		System.out.println("Test #28 openapi che usano parametri definiti con schemi composti ...");
		
		testComposedSchemaParameters(openAPILibrary, mergeSpec);
		
		System.out.println("Test #28 openapi che usano parametri definiti con schemi composti completato\n\n");
		
		
		
		// ** Test per validazione con la presenza di header Accept ... **
		
		/*
		 * Serve principalmente per la libreria swagger request validator che possiede i seguenti bug che sono stati risolti in org.openspcoop2.utils.openapi.validator.Validator.buildSwaggerRequest:
		 */
		// BugFix: 
		// Request Accept header '*; q=.2' is not a valid media type
		// BugFix2:
		// Request Accept header '[text/xml]' does not match any defined response types. Must be one of: [text/*, application/*].
				
		System.out.println("Test #29 header Accept ...");
		
		testAccept(openAPILibrary, mergeSpec);
	
		System.out.println("Test #29 header Accept\n\n");
	
	}

	
	private static void checkErrorTest20(boolean esito, String tipoTest, Exception e, String tipologia, OpenAPILibrary openAPILibrary) throws Exception {
		
		String error = e.getMessage();
		if(error.length()>200) {
			error = error.substring(0, 198)+" ...";
		}
		
		String element = "-";
		String type = "-";
		boolean arrayValuesNull = false;
		if("StringNull".equals(tipologia)) {
			element = "string_";
			type = "string";
		}
		else if("EnumNull".equals(tipologia)) {
			element = "enum_";
			type = "string";
		}
		else if("IntegerNull".equals(tipologia)) {
			element = "integer_";
			type = "integer";
		}
		else if("BooleanNull".equals(tipologia)) {
			element = "boolean_";
			type = "boolean";
		}
		else if("ArrayNull".equals(tipologia)) {
			element = "array_";
			type = "array";
		}
		else if("ArrayValuesNull".equals(tipologia)) {
			arrayValuesNull = true;
		}
		else if("ObjectNull".equals(tipologia)) {
			element = "object_";
			type = "object";
		}
		
		if(!esito) {
			System.out.println("\t "+tipoTest+" atteso errore di validazione, rilevato: "+error);
		}
		else {
			System.out.println("\t "+tipoTest+" rilevato errore di validazione non atteso: "+error);
			throw new Exception(""+tipoTest+" rilevato errore di validazione non atteso: "+e.getMessage(),e);
		}
	
		switch (openAPILibrary) {
		case json_schema:
			if(arrayValuesNull) {
				String msgErroreAtteso = "1029 $.array_nullable_values_optional[0]: null found, string expected";
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
				msgErroreAtteso = "1029 $.array_nullable_values_optional[1]: null found, string expected";
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
				msgErroreAtteso = "1029 $.array_nullable_values_required[0]: null found, string expected";
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
			else {
				for (int k = 0; k < 2; k++) {
					String msgErroreAtteso = "1029 $."+element+(k==0 ? "required" : "optional")+": null found, "+type+" expected";
					if(!e.getMessage().contains(msgErroreAtteso)) {
						System.out.println("\t "+tipoTest+" ERRORE!");
						throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
					}	
				}	
			}
			break;
		case openapi4j:
			if(arrayValuesNull) {
				
				String msgErroreAtteso = "body.array_nullable_values_optional.0: Null value is not allowed. (code: 1021)";
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
				
				msgErroreAtteso = "body.array_nullable_values_optional.1: Null value is not allowed. (code: 1021)";				
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
				
				msgErroreAtteso = "body.array_nullable_values_required.0: Null value is not allowed. (code: 1021)";
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
			else {
				for (int k = 0; k < 2; k++) {
					String suffix = (k==0 ? "required" : "optional");
					String msgErroreAtteso = "body."+element+suffix+": Null value is not allowed. (code: 1021)";
					if(!e.getMessage().contains(msgErroreAtteso)) {
						System.out.println("\t "+tipoTest+" ERRORE!");
						throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
					}
				}
			}	
			break;
		case swagger_request_validator:
			if(arrayValuesNull) {
				
				String msgErroreAtteso = "[Path '/array_nullable_values_optional/0'] Instance type (null) does not match any allowed primitive type (allowed: [\"string\"])";
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
				
				msgErroreAtteso = "[Path '/array_nullable_values_optional/1'] Instance type (null) does not match any allowed primitive type (allowed: [\"string\"])";				
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
				
				msgErroreAtteso = "[Path '/array_nullable_values_required/0'] Instance type (null) does not match any allowed primitive type (allowed: [\"string\"])";
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
			else {
				for (int k = 0; k < 2; k++) {
					String suffix = (k==0 ? "required" : "optional");
					String msgErroreAtteso = "[Path '/"+element+suffix+"'] Instance type (null) does not match any allowed primitive type (allowed:";
					if(!e.getMessage().contains(msgErroreAtteso)) {
						System.out.println("\t "+tipoTest+" ERRORE!");
						throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
					}
				}
			}	
			break;
		}
		
	}
	
	private static void testSubMediatypeWildcardJsonValidation(OpenAPILibrary openAPILibrary, boolean mergeSpec, boolean validateWildcard)
			throws UtilsException, ProcessingException, URISyntaxException, Exception {
		System.out.println("#### Verifica per validazione json con wildcard ("+validateWildcard+") nel subtype ####");
		
		URL url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/allegati.yaml");
		
		ApiSchema apiSchemaYaml = new ApiSchema("teamdigitale-openapi_definitions.yaml", 
				Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/service/schemi/standard/teamdigitale-openapi_definitions.yaml")), ApiSchemaType.YAML);
					
		IApiReader apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		ApiReaderConfig configOpenApi4j = new ApiReaderConfig();
		configOpenApi4j.setProcessInclude(false);
		apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi4j, apiSchemaYaml);
		Api apiOpenApi4j = apiReaderOpenApi4j.read();
								
		IApiValidator apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
		OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
		configO.setEmitLogError(logSystemOutError);
		configO.setOpenApiValidatorConfig(new OpenapiLibraryValidatorConfig());
		configO.getOpenApiValidatorConfig().setOpenApiLibrary(openAPILibrary);
		configO.getOpenApiValidatorConfig().setValidateAPISpec(true);
		configO.getOpenApiValidatorConfig().setMergeAPISpec(mergeSpec);
		configO.getOpenApiValidatorConfig().setValidateWildcardSubtypeAsJson(validateWildcard);
		apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi4j, configO);

		System.out.println("Test Richiesta...");

		{ 
			TextHttpRequestEntity requestS1 = new TextHttpRequestEntity();
			
			requestS1.setMethod(HttpRequestMethod.POST);
			requestS1.setUrl("documenti/qualsiasi/"+UUID.randomUUID().toString());	
			Map<String, List<String>> headersS1 = new HashMap<>();
			TransportUtils.setHeader(headersS1,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_PLAIN);
			requestS1.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
			requestS1.setHeaders(headersS1);
			requestS1.setContent("{ a }");
					
			String erroreAttesoRichiesta = openAPILibrary == OpenAPILibrary.openapi4j ?
					"body: Type expected 'object', found 'string'. (code: 1027)" : 
					"[ERROR] Unable to parse JSON - Unexpected character ('a' (code 97)): was expecting double-quote to start field name";
			try {				
				apiValidatorOpenApi4j.validate(requestS1);
				
				if (openAPILibrary != OpenAPILibrary.json_schema && validateWildcard) {
					throw new Exception("Errore atteso '"+erroreAttesoRichiesta+"' non rilevato");
				}
				
			} catch (ValidatorException e) {
				System.out.println(e.getMessage());
				if (!e.getMessage().contains(erroreAttesoRichiesta)) {
					throw new Exception("Errore atteso '"+erroreAttesoRichiesta+"' non rilevato");
				}
			}
		}
		System.out.println("Test Richiesta Superato!");
		
		System.out.println("Test Risposta...");
		
		{
			TextHttpResponseEntity httpResponseTestS1 = new TextHttpResponseEntity();
			
			httpResponseTestS1.setStatus(200);		
			httpResponseTestS1.setMethod(HttpRequestMethod.GET);
			httpResponseTestS1.setUrl("documenti/qualsiasi/"+UUID.randomUUID().toString());	
			Map<String, List<String>> headersS1 = new HashMap<>();
			TransportUtils.setHeader(headersS1,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_PLAIN);
			httpResponseTestS1.setHeaders(headersS1);
			httpResponseTestS1.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
			httpResponseTestS1.setContent("{ a }");
		
			String erroreAttesoRisposta = openAPILibrary == OpenAPILibrary.openapi4j ?
					"body: Type expected 'object', found 'string'. (code: 1027)" : 
					"[ERROR] Unable to parse JSON - Unexpected character ('a' (code 97)): was expecting double-quote to start field name";
			try {				
				apiValidatorOpenApi4j.validate(httpResponseTestS1);
				
				if (openAPILibrary != OpenAPILibrary.json_schema && validateWildcard) {
					throw new Exception("Errore atteso '"+erroreAttesoRisposta+"' non rilevato");
				}
				
			} catch (ValidatorException e) {
				System.out.println(e.getMessage());
				if (!e.getMessage().contains(erroreAttesoRisposta)) {
					throw new Exception("Errore atteso '"+erroreAttesoRisposta+"' non rilevato");
				}
			}
		}
			
		System.out.println("Test Risposta Superato!");
		
		System.out.println("TEST #S-1 per validazione json con wildcard ("+validateWildcard+") nel subtype completato!");

	}

	private static void testBase64Validation(OpenAPILibrary openAPILibrary, boolean mergeSpec, IApiValidator validator) throws Exception {
		
		System.out.println("TEST #S-2 per validazione file in base64");
		
		System.out.println("Valido richiesta con contenuto corretto..");
		{
			TextHttpRequestEntity validRequest = new TextHttpRequestEntity();
			validRequest.setUrl("/documenti/testbase64/"+UUID.randomUUID().toString());	
			validRequest.setMethod(HttpRequestMethod.POST);
			validRequest.setContent("Q2lhbyBiYmVsbG8h");
			Map<String, List<String>> parametersTrasporto = new HashMap<>();
			TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
			validRequest.setHeaders(parametersTrasporto);
			validRequest.setContentType(HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
			validator.validate(validRequest);
		}
		
		System.out.println("Nessun errore sollevato, ok!");
		
		System.out.println("Valido richiesta con contenuto non corretto..");
		{
			TextHttpRequestEntity invalidRequest = new TextHttpRequestEntity();
			invalidRequest.setUrl("/documenti/testbase64/"+UUID.randomUUID().toString());	
			invalidRequest.setMethod(HttpRequestMethod.POST);
			invalidRequest.setContent("{ asper");
			Map<String, List<String>> parametersTrasporto = new HashMap<>();
			TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
			invalidRequest.setHeaders(parametersTrasporto);
			invalidRequest.setContentType(HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
	
			String erroreAttesoRichiesta = null;
			if (openAPILibrary == OpenAPILibrary.openapi4j)  {
				erroreAttesoRichiesta = "body: Value '{ asper' does not match format 'base64'. (code: 1007)";
			}
			else if (openAPILibrary == OpenAPILibrary.swagger_request_validator) {
				erroreAttesoRichiesta = "[ERROR] [REQUEST] err.format.base64.badLength, should be multiple of 4: 7";
			}
			try {				
				validator.validate(invalidRequest);
				throw new Exception("Errore atteso '"+erroreAttesoRichiesta+"' non rilevato");			
			} catch (ValidatorException e) {
				System.out.println(e.getMessage());
				if (!e.getMessage().contains(erroreAttesoRichiesta)) {
					throw new Exception("Errore atteso '"+erroreAttesoRichiesta+"' non rilevato");
				}
			}
			System.out.println("Errore rilevato, ok!");
		}
		
		System.out.println("Valido risposta con contenuto corretto..");
		{
			TextHttpResponseEntity validResponse = new TextHttpResponseEntity();
			validResponse.setUrl("/documenti/testbase64/"+UUID.randomUUID().toString());	
			validResponse.setMethod(HttpRequestMethod.POST);
			validResponse.setStatus(200);
			validResponse.setContent("Q2lhbyBiYmVsbG8h");
			Map<String, List<String>> parametersTrasporto = new HashMap<>();
			TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
			validResponse.setHeaders(parametersTrasporto);
			validResponse.setContentType(HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
			validator.validate(validResponse);
		}
		
		System.out.println("Nessun errore sollevato, ok!");
		
		System.out.println("Valido risposta con contenuto non corretto..");
		
		{
			TextHttpResponseEntity invalidResponse = new TextHttpResponseEntity();
			invalidResponse.setUrl("/documenti/testbase64/"+UUID.randomUUID().toString());	
			invalidResponse.setMethod(HttpRequestMethod.POST);
			invalidResponse.setStatus(200);
			invalidResponse.setContent("{ asper");
			Map<String, List<String>> parametersTrasporto = new HashMap<>();
			TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
			invalidResponse.setContentType(HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
			invalidResponse.setHeaders(parametersTrasporto);
	
			String erroreAttesoRisposta = null;
			if (openAPILibrary == OpenAPILibrary.openapi4j)  {
				erroreAttesoRisposta = "body: Value '{ asper' does not match format 'base64'. (code: 1007)";
			}
			else if (openAPILibrary == OpenAPILibrary.swagger_request_validator) {
				erroreAttesoRisposta = "[ERROR] [RESPONSE] err.format.base64.badLength, should be multiple of 4: 7";
			}
			try {				
				validator.validate(invalidResponse);
				throw new Exception("Errore atteso '"+erroreAttesoRisposta+"' non rilevato");			
			} catch (ValidatorException e) {
				System.out.println(e.getMessage());
				if (!e.getMessage().contains(erroreAttesoRisposta)) {
					throw new Exception("Errore atteso '"+erroreAttesoRisposta+"' non rilevato");
				}
			}
		}
		System.out.println("Errore rilevato, ok!");
				
		System.out.println("TEST #S-2 per validazione file in base64 completato!");
	}

	
	private static void testMergeKey(OpenAPILibrary openAPILibrary, boolean mergeSpec)
			throws UtilsException, ProcessingException, URISyntaxException, Exception {
		System.out.println("#### Verifica OpenAPI YAML con mergeKey ####");
		
		URL url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/mergeKey.yaml");
					
		IApiReader apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		ApiReaderConfig configOpenApi4j = new ApiReaderConfig();
		configOpenApi4j.setProcessInclude(false);
		apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi4j);
		Api apiOpenApi4j = apiReaderOpenApi4j.read();
					
		try {
			apiOpenApi4j.validate();
		}catch(ProcessingException pe) {
			pe.printStackTrace(System.out);
			throw new Exception(" Documento contenente errori: "+pe.getMessage(), pe);
		}catch(ParseWarningException warning) {
			//warning.printStackTrace(System.out);
			System.out.println("Documento contenente anomalie: "+warning.getMessage());
		}
		
		IApiValidator apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
		OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
		configO.setEmitLogError(logSystemOutError);
		configO.setOpenApiValidatorConfig(new OpenapiLibraryValidatorConfig());
		configO.getOpenApiValidatorConfig().setOpenApiLibrary(openAPILibrary);
		configO.getOpenApiValidatorConfig().setValidateAPISpec(true);
		configO.getOpenApiValidatorConfig().setMergeAPISpec(mergeSpec);
		apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi4j, configO);

		System.out.println("Test Richiesta...");

		{ 
			TextHttpRequestEntity requestS1 = new TextHttpRequestEntity();
			
			requestS1.setMethod(HttpRequestMethod.GET);
			requestS1.setUrl("/test/"+UUID.randomUUID().toString());	
					
			try {				
				apiValidatorOpenApi4j.validate(requestS1);
				
			} catch (ValidatorException e) {
				System.out.println(e.getMessage());
				throw new Exception("Errore non atteso");
			}
		}
		System.out.println("Test Richiesta Superato!");
		
		System.out.println("Test Risposta...");
		
		{
			TextHttpResponseEntity httpResponseTestS1 = new TextHttpResponseEntity();
			
			httpResponseTestS1.setStatus(200);		
			httpResponseTestS1.setMethod(HttpRequestMethod.GET);
			httpResponseTestS1.setUrl("/test/"+UUID.randomUUID().toString());	
			Map<String, List<String>> headersS1 = new HashMap<>();
			TransportUtils.setHeader(headersS1,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			httpResponseTestS1.setHeaders(headersS1);
			httpResponseTestS1.setContentType(HttpConstants.CONTENT_TYPE_JSON);
			httpResponseTestS1.setContent("{ \"test\": \"01043931007\", \"valida\": false }");
		
			try {				
				apiValidatorOpenApi4j.validate(httpResponseTestS1);
			} catch (ValidatorException e) {
				System.out.println(e.getMessage());
				throw new Exception("Errore non atteso");
			}
			
			
			httpResponseTestS1.setStatus(400);	
			headersS1 = new HashMap<>();
			TransportUtils.setHeader(headersS1,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);
			httpResponseTestS1.setHeaders(headersS1);
			httpResponseTestS1.setContentType(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);
			httpResponseTestS1.setContent("{ \"type\": \"https://example.com/400\", \"title\": \"example\", \"detail\": \"example\" }");
			
			try {				
				apiValidatorOpenApi4j.validate(httpResponseTestS1);
			} catch (ValidatorException e) {
				System.out.println(e.getMessage());
				throw new Exception("Errore non atteso");
			}
			
			
			httpResponseTestS1.setStatus(400);	
			headersS1 = new HashMap<>();
			TransportUtils.setHeader(headersS1,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			httpResponseTestS1.setHeaders(headersS1);
			httpResponseTestS1.setContentType(HttpConstants.CONTENT_TYPE_JSON);
			httpResponseTestS1.setContent("{ \"type\": \"https://example.com/400\", \"title\": \"example\", \"detail\": \"example\" }");
			
			String erroreAttesoRisposta = null;
			switch (openAPILibrary) {
			case json_schema:
				erroreAttesoRisposta = "Content-Type 'application/json' (http response status '400') unsupported";
				break;
			case openapi4j:
				erroreAttesoRisposta = "Content type 'application/json' is not allowed for body content. (code: 203)";
				break;
			case swagger_request_validator:
				erroreAttesoRisposta = "[ERROR][RESPONSE][] Response Content-Type header 'application/json' does not match any allowed types. Must be one of: [application/problem+json].";
				break;
			}
			try {				
				apiValidatorOpenApi4j.validate(httpResponseTestS1);
				
				//if (openAPILibrary != OpenAPILibrary.json_schema) {
					throw new Exception("Errore atteso '"+erroreAttesoRisposta+"' non rilevato");
			//	}
				
			} catch (ValidatorException e) {
				System.out.println(e.getMessage());
				if (!e.getMessage().contains(erroreAttesoRisposta)) {
					throw new Exception("Errore atteso '"+erroreAttesoRisposta+"' non rilevato");
				}
			}
		}
			
		System.out.println("Test Risposta Superato!");
		
		System.out.println("TEST OpenAPI YAML con mergeKey completato!");

	}
	
	private static void testMultipartRequest(OpenAPILibrary openAPILibrary, boolean mergeSpec,
			boolean stream, boolean multipartOptimization)
			throws UtilsException, ProcessingException, URISyntaxException, Exception {
		System.out.println("#### Verifica Multipart Request (stream:"+stream+" multipartOptimization:"+multipartOptimization+") ####");
		
		URL url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/allegati.yaml");
					
		ApiSchema apiSchemaYaml = new ApiSchema("teamdigitale-openapi_definitions.yaml", 
				Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/service/schemi/standard/teamdigitale-openapi_definitions.yaml")), ApiSchemaType.YAML);
					
		IApiReader apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		ApiReaderConfig configOpenApi4j = new ApiReaderConfig();
		configOpenApi4j.setProcessInclude(false);
		apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi4j, apiSchemaYaml);
		
		Api apiOpenApi4j = apiReaderOpenApi4j.read();
								
		IApiValidator apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
		OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
		configO.setEmitLogError(logSystemOutError);
		configO.setOpenApiValidatorConfig(new OpenapiLibraryValidatorConfig());
		configO.getOpenApiValidatorConfig().setOpenApiLibrary(openAPILibrary);
		configO.getOpenApiValidatorConfig().setValidateAPISpec(true);
		configO.getOpenApiValidatorConfig().setMergeAPISpec(mergeSpec);
		configO.getOpenApiValidatorConfig().setValidateMultipartOptimization(multipartOptimization);
		apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi4j, configO);

		String cat = "{\"pet_type\": \"Cat\",  \"age\": 3}";
		String dog1 = "{\"pet_type\": \"Dog\",  \"bark\": false,  \"breed\": \"Dingo\" }";
		String dog2 = "{\"pet_type\": \"Dog\",  \"bark\": true }";
		String contenuto_cat = "{\"altro\":\"descrizione generica\", \"pet\":"+cat+"}";
		String contenuto_dog1 = "{\"altro\":\"descrizione generica\", \"pet\":"+dog1+"}";
		String contenuto_dog2 = "{\"altro\":\"descrizione generica\", \"pet\":"+dog2+"}";
		String contenuto_errato1 = "{\"altroErrore\":\"descrizione generica\", \"pet\":"+dog2+"}";
		String contenuto_errato2 = "{\"pet\":"+dog2+"}";
		String catErrato = "{\"pet_type\": \"CatErrato\",  \"age\": 3}";
		String contenuto_errato3 = "{\"altro\":\"descrizione generica\", \"pet\":"+catErrato+"}";
		
		byte [] pdf = Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test.pdf"));
		byte [] pdfEncodedBase64 = Base64Utilities.encode(pdf);
		
		
		
		List<String> macroTest_path = new ArrayList<String>();
		List<String> macroTest_subtype = new ArrayList<String>();
		
		macroTest_path.add("/documenti/multipart/form-data/strict");
		macroTest_subtype.add(HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
		
		macroTest_path.add("/documenti/multipart/mixed/strict");
		macroTest_subtype.add(HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
		
		macroTest_path.add("/documenti/multipart/form-data/optionalWithoutEncoding");
		macroTest_subtype.add(HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
		
		macroTest_path.add("/documenti/multipart/mixed/optionalWithoutEncoding");
		macroTest_subtype.add(HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
		
		macroTest_path.add("/documenti/multipart/form-data-and-mixed/strict");
		macroTest_subtype.add(HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
		
		macroTest_path.add("/documenti/multipart/form-data-and-mixed/strict");
		macroTest_subtype.add(HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
		
		for (int j = 0; j < macroTest_path.size(); j++) {
			
			String macroTestPath = macroTest_path.get(j);
			String macroTestSubtype = macroTest_subtype.get(j);
			

			List<String> tipoTest = new ArrayList<String>();
			List<Boolean> erroreAttesoTest = new ArrayList<Boolean>();
			List<String> msgErroreAttesoTest = new ArrayList<String>();
			List<String> msgErroreAttesoRispostaTest = new ArrayList<String>();
			List<String> pathTest = new ArrayList<String>();
			List<HttpRequestMethod> methodTest = new ArrayList<HttpRequestMethod>();
			List<String> attachment_subtype = new ArrayList<String>();
			
			List<String> attachment1_name = new ArrayList<String>();
			List<String> attachment1_content = new ArrayList<String>();
			List<String> attachment1_contentType = new ArrayList<String>();
			List<String> attachment1_fileName = new ArrayList<String>();
			
			List<String> attachment2_name = new ArrayList<String>();
			List<String> attachment2_content = new ArrayList<String>();
			List<String> attachment2_contentType = new ArrayList<String>();
			List<String> attachment2_fileName = new ArrayList<String>();
			
			List<String> attachment3_name = new ArrayList<String>();
			List<byte[]> attachment3_content = new ArrayList<byte[]>();
			List<String> attachment3_contentType = new ArrayList<String>();
			List<String> attachment3_fileName = new ArrayList<String>();
			
			List<String> attachment4_name = new ArrayList<String>();
			List<byte[]> attachment4_content = new ArrayList<byte[]>();
			List<String> attachment4_contentType = new ArrayList<String>();
			List<String> attachment4_fileName = new ArrayList<String>();
			
			List<String> attachment5_name = new ArrayList<String>();
			List<byte[]> attachment5_content = new ArrayList<byte[]>();
			List<String> attachment5_contentType = new ArrayList<String>();
			List<String> attachment5_fileName = new ArrayList<String>();
			
			
			// *** Test1: corretto ***
			
			tipoTest.add("OK-1: cat");
			erroreAttesoTest.add(false);
			msgErroreAttesoTest.add(null);
			msgErroreAttesoRispostaTest.add(null);
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add(Integer.MAX_VALUE+"");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add(null);
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_cat);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add(null);
			attachment3_name.add("\"docPdf\"");
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment3_fileName.add("\"attachment.pdf\"");
			attachment4_name.add("\"docPdf2\"");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add("\"attachment2.pdf\"");
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
			// *** Test2: corretto2 ***
			
			tipoTest.add("OK-2: dog1");
			erroreAttesoTest.add(false);
			msgErroreAttesoTest.add(null);
			msgErroreAttesoRispostaTest.add(null);
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add(Integer.MAX_VALUE+"");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add(null);
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_dog1);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add(null);
			attachment3_name.add("\"docPdf\"");
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);	
			attachment3_fileName.add("\"attachment.pdf\"");
			attachment4_name.add("\"docPdf2\"");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add("\"attachment2.pdf\"");
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
			// *** Test3: corretto3 ***
			
			tipoTest.add("OK-3: dog2 con file name in tutti i parametri");
			erroreAttesoTest.add(false);
			msgErroreAttesoTest.add(null);
			msgErroreAttesoRispostaTest.add(null);
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add(Integer.MAX_VALUE+"");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add("\"id.txt\"");
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_dog2);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add("\"metadati.json\"");
			attachment3_name.add("\"docPdf\"");
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);	
			attachment3_fileName.add("\"attachment.pdf\"");
			attachment4_name.add("\"docPdf2\"");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add("\"attachment2.pdf\"");
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
			
			// *** Test4: corretto, parameter senza doppi apici ***
			
			tipoTest.add("OK-4: parameter senza doppi apici \"");
			erroreAttesoTest.add(false);
			msgErroreAttesoTest.add(null);
			msgErroreAttesoRispostaTest.add(null);
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("id");
			attachment1_content.add(Integer.MAX_VALUE+"");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add(null);
			attachment2_name.add("metadati");
			attachment2_content.add(contenuto_dog2);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add(null);
			attachment3_name.add("docPdf");
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);	
			attachment3_fileName.add("\"attachment.pdf\"");
			attachment4_name.add("docPdf2");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add("\"attachment2.pdf\"");
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
			
			// *** Test5: corretto, fileName senza doppi apici ***
			
			tipoTest.add("OK-5: fileName senza doppi apici \"");
			erroreAttesoTest.add(false);
			msgErroreAttesoTest.add(null);
			msgErroreAttesoRispostaTest.add(null);
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add(Integer.MAX_VALUE+"");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add(null);
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_dog2);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add(null);
			attachment3_name.add("\"docPdf\"");
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);	
			attachment3_fileName.add("attachment.pdf");
			attachment4_name.add("\"docPdf2\"");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add("attachment2.pdf");
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
			
			// *** Test6: corretto, fileName non presente ***
			
			tipoTest.add("OK-6: fileName non presente \"");
			erroreAttesoTest.add(false);
			msgErroreAttesoTest.add(null);
			msgErroreAttesoRispostaTest.add(null);
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add(Integer.MAX_VALUE+"");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add(null);
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_dog2);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add(null);
			attachment3_name.add("\"docPdf\"");
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);	
			attachment3_fileName.add(null);
			attachment4_name.add("\"docPdf2\"");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add(null);
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
					
			
			
			// *** Test7: parametro docPdf2 (opzionale) non presente ***
			
			tipoTest.add("OK-7: parametro docPdf2 (opzionale) non presente");
			erroreAttesoTest.add(false);
			msgErroreAttesoTest.add(null);
			msgErroreAttesoRispostaTest.add(null);
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add(Integer.MAX_VALUE+"");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add(null);
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_dog2);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add(null);
			attachment3_name.add("\"docPdf\"");
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);	
			attachment3_fileName.add("\"attachment.pdf\"");
			attachment4_name.add(null);
			attachment4_content.add(null);
			attachment4_contentType.add(null);
			attachment4_fileName.add(null);
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
			
			// *** Test8: posizioni invertite degli attachments ***
			
			tipoTest.add("OK-8: posizioni invertite");
			erroreAttesoTest.add(false);
			msgErroreAttesoTest.add(null);
			msgErroreAttesoRispostaTest.add(null);
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment2_name.add("\"id\"");
			attachment2_content.add(Integer.MAX_VALUE+"");
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment2_fileName.add(null);
			attachment1_name.add("\"metadati\"");
			attachment1_content.add(contenuto_dog2);
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment1_fileName.add(null);
			attachment4_name.add("\"docPdf\"");
			attachment4_content.add(pdf);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);	
			attachment4_fileName.add("\"attachment.pdf\"");
			attachment3_name.add("\"docPdf2\"");
			attachment3_content.add(pdfEncodedBase64);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment3_fileName.add("\"attachment2.pdf\"");
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
			
			
			
			
			
			
			
			// *** Test ERRORE 1: ulteriore attachment non previsto ***
			
			tipoTest.add("ERROR-1: attachment ulteriore, non previsto");
			if(macroTestPath.endsWith("optionalWithoutEncoding") || multipartOptimization) {
				erroreAttesoTest.add(false);
				msgErroreAttesoTest.add(null);
				msgErroreAttesoRispostaTest.add(null);
			}
			else {
				erroreAttesoTest.add(true);
				msgErroreAttesoTest.add("Additional property 'docUlteriore' is not allowed. (code: 1000)");
				msgErroreAttesoRispostaTest.add("Additional property 'docUlteriore' is not allowed. (code: 1000)");
			}
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add(Integer.MAX_VALUE+"");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add(null);
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_cat);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add(null);
			attachment3_name.add("\"docPdf\"");
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment3_fileName.add("\"attachment.pdf\"");
			attachment4_name.add("\"docPdf2\"");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add("\"attachment2.pdf\"");
			attachment5_name.add("\"docUlteriore\"");
			attachment5_content.add(pdfEncodedBase64);
			attachment5_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment5_fileName.add("\"attachmentOther.bin\"");
			
			
			
			// *** Test ERRORE 2: contenuto errato nei metadati, required claim non presente ***
			
			tipoTest.add("ERROR-2: contenuto errato nei metadati, required claim 'altro' presente con un nome differente");
			erroreAttesoTest.add(true);
			msgErroreAttesoTest.add("body.metadati: Field 'altro' is required. (code: 1026)");
			msgErroreAttesoRispostaTest.add("body.metadati: Field 'altro' is required. (code: 1026)");
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add(Integer.MAX_VALUE+"");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add(null);
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_errato1);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add(null);
			attachment3_name.add("\"docPdf\"");
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment3_fileName.add("\"attachment.pdf\"");
			attachment4_name.add("\"docPdf2\"");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add("\"attachment2.pdf\"");
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
	
					
			// *** Test ERRORE 3: contenuto errato nei metadati, required claim non presente e fileName presente in tutti i parametri ***
			
			tipoTest.add("ERROR-3: contenuto errato nei metadati, required claim 'altro' non presente e fileName presente in tutti i parametri");
			erroreAttesoTest.add(true);
			msgErroreAttesoTest.add("body.metadati: Field 'altro' is required. (code: 1026)");
			msgErroreAttesoRispostaTest.add("body.metadati: Field 'altro' is required. (code: 1026)");
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add(Integer.MAX_VALUE+"");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add("\"id.txt\"");
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_errato2);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add("\"metadati.json\"");
			attachment3_name.add("\"docPdf\"");
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment3_fileName.add("\"attachment.pdf\"");
			attachment4_name.add("\"docPdf2\"");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add("\"attachment2.pdf\"");
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
			
			
			// *** Test ERRORE 4: contenuto errato nei metadati, discriminator value errato ***
			
			tipoTest.add("ERROR-4: contenuto errato nei metadati, discriminator value errato");
			erroreAttesoTest.add(true);
			msgErroreAttesoTest.add("body.metadati.pet: Schema selection can't be made for discriminator 'pet_type' with value 'CatErrato'. (code: 1003)");
			msgErroreAttesoRispostaTest.add("body.metadati.pet: Schema selection can't be made for discriminator 'pet_type' with value 'CatErrato'. (code: 1003)");
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add(Integer.MAX_VALUE+"");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add(null);
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_errato3);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add(null);
			attachment3_name.add("\"docPdf\"");
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment3_fileName.add("\"attachment.pdf\"");
			attachment4_name.add("\"docPdf2\"");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add("\"attachment2.pdf\"");
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
			
			
			// *** Test ERRORE 5: parametro primitivo id con value errato (string) ***
			
			tipoTest.add("ERROR-5: parametro primitivo id con value errato (string)");
			erroreAttesoTest.add(true);
			msgErroreAttesoTest.add("body.id: Value 'ValoreNonInteroERR' does not match format 'int32'. (code: 1007)");
			msgErroreAttesoRispostaTest.add("body.id: Value 'ValoreNonInteroERR' does not match format 'int32'. (code: 1007)");
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add("ValoreNonInteroERR");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add(null);
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_cat);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add(null);
			attachment3_name.add("\"docPdf\"");
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment3_fileName.add("\"attachment.pdf\"");
			attachment4_name.add("\"docPdf2\"");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add("\"attachment2.pdf\"");
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
			
			
			
			// *** Test ERRORE 6: parametro primitivo id con value errato (string) e fileName presente ***
			
			tipoTest.add("ERROR-6: parametro primitivo id con value errato (string) e fileName presente");
			erroreAttesoTest.add(true);
			msgErroreAttesoTest.add("body.id: Value 'ValoreNonInteroERR' does not match format 'int32'. (code: 1007)");
			msgErroreAttesoRispostaTest.add("body.id: Value 'ValoreNonInteroERR' does not match format 'int32'. (code: 1007)");
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add("ValoreNonInteroERR");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add("\"id.txt\"");
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_cat);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add("\"metadati.json\"");
			attachment3_name.add("\"docPdf\"");
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment3_fileName.add("\"attachment.pdf\"");
			attachment4_name.add("\"docPdf2\"");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add("\"attachment2.pdf\"");
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
			
			
	
			// *** Test ERRORE 7: parametro docPdf non presente ***
			
			tipoTest.add("ERROR-7: parametro docPdf non presente");
			if(macroTestPath.endsWith("optionalWithoutEncoding") || multipartOptimization) {
				erroreAttesoTest.add(false);
				msgErroreAttesoTest.add(null);
				msgErroreAttesoRispostaTest.add(null);
			}
			else {
				erroreAttesoTest.add(true);
				msgErroreAttesoTest.add("body: Field 'docPdf' is required. (code: 1026)");
				msgErroreAttesoRispostaTest.add("body: Field 'docPdf' is required. (code: 1026)");
			}
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add(Integer.MAX_VALUE+"");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add(null);
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_cat);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add(null);
			attachment3_name.add(null);
			attachment3_content.add(null);
			attachment3_contentType.add(null);
			attachment3_fileName.add(null);
			attachment4_name.add("\"docPdf2\"");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add("\"attachment2.pdf\"");
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
			
			
			// *** Test ERRORE 8: parametro docPdf non dichiarato tramite name ***
			
			tipoTest.add("ERROR-8: parametro docPdf non dichiarato tramite name");
			if(macroTestPath.endsWith("optionalWithoutEncoding") || multipartOptimization) {
				erroreAttesoTest.add(false);
				msgErroreAttesoTest.add(null);
				msgErroreAttesoRispostaTest.add(null);
			}
			else {
				erroreAttesoTest.add(true);
				msgErroreAttesoTest.add("body: Field 'docPdf' is required. (code: 1026)");
				msgErroreAttesoRispostaTest.add("body: Field 'docPdf' is required. (code: 1026)");
			}
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add(Integer.MAX_VALUE+"");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add(null);
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_cat);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add(null);
			attachment3_name.add(null);
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment3_fileName.add("\"attachment.pdf\"");
			attachment4_name.add("\"docPdf2\"");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add("\"attachment2.pdf\"");
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
			
			
			
			// *** Test ERRORE 9: content-type multipart opposto tra mixed e form-data ***
			if(!macroTestPath.contains("form-data-and-mixed")) {
				tipoTest.add("ERROR-9: content-type multipart opposto tra mixed e form-data");
				erroreAttesoTest.add(true);
				String subtype = HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE.equals(macroTestSubtype) ? HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE : HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE;
				msgErroreAttesoTest.add("Content type 'multipart/"+subtype+";");
				msgErroreAttesoRispostaTest.add("is not allowed for body content. (code: 203)");
				pathTest.add(macroTestPath);
				methodTest.add(HttpRequestMethod.POST);
				attachment_subtype.add(subtype);
				attachment1_name.add("\"id\"");
				attachment1_content.add(Integer.MAX_VALUE+"");
				attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
				attachment1_fileName.add(null);
				attachment2_name.add("\"metadati\"");
				attachment2_content.add(contenuto_cat);
				attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
				attachment2_fileName.add(null);
				attachment3_name.add("\"docPdf\"");
				attachment3_content.add(pdf);
				attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
				attachment3_fileName.add("\"attachment.pdf\"");
				attachment4_name.add("\"docPdf2\"");
				attachment4_content.add(pdfEncodedBase64);
				attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
				attachment4_fileName.add("\"attachment2.pdf\"");
				attachment5_name.add(null);
				attachment5_content.add(null);
				attachment5_contentType.add(null);
				attachment5_fileName.add(null);
			}
			
			
			
			
			// *** Test ERRORE 10: content-type multipart diverso da quello definito ***
			
			tipoTest.add("ERROR-10: content-type multipart diverso da quello definito");
			erroreAttesoTest.add(true);
			String subtype = HttpConstants.CONTENT_TYPE_MULTIPART_RELATED_SUBTYPE;
			msgErroreAttesoTest.add("Content type 'multipart/"+subtype+";");
			msgErroreAttesoRispostaTest.add("is not allowed for body content. (code: 203)");
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(subtype);
			attachment1_name.add("\"id\"");
			attachment1_content.add(Integer.MAX_VALUE+"");
			attachment1_contentType.add(HttpConstants.CONTENT_TYPE_PLAIN);
			attachment1_fileName.add(null);
			attachment2_name.add("\"metadati\"");
			attachment2_content.add(contenuto_cat);
			attachment2_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
			attachment2_fileName.add(null);
			attachment3_name.add("\"docPdf\"");
			attachment3_content.add(pdf);
			attachment3_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment3_fileName.add("\"attachment.pdf\"");
			attachment4_name.add("\"docPdf2\"");
			attachment4_content.add(pdfEncodedBase64);
			attachment4_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
			attachment4_fileName.add("\"attachment2.pdf\"");
			attachment5_name.add(null);
			attachment5_content.add(null);
			attachment5_contentType.add(null);
			attachment5_fileName.add(null);
			
			
			
	
			
			
		
	
			
			
			// Esecuzione test
			
			
			for (int i = 0; i < attachment1_name.size(); i++) {
			
				String tipo = tipoTest.get(i);
				
				boolean erroreAtteso = erroreAttesoTest.get(i);
				String msgErroreAttesoRichiesta = msgErroreAttesoTest.get(i);
				String msgErroreAttesoRisposta = msgErroreAttesoRispostaTest.get(i);
				
				String path = pathTest.get(i);
				HttpRequestMethod method = methodTest.get(i);
				
				
				MimeMultipart mm = MultipartUtilities.buildMimeMultipart(attachment_subtype.get(i),
						attachment1_content.get(i), attachment1_contentType.get(i), attachment1_name.get(i), attachment1_fileName.get(i),
						attachment2_content.get(i), attachment2_contentType.get(i), attachment2_name.get(i), attachment2_fileName.get(i),
						attachment3_content.get(i), attachment3_contentType.get(i), attachment3_name.get(i), attachment3_fileName.get(i),
						attachment4_content.get(i), attachment4_contentType.get(i), attachment4_name.get(i), attachment4_fileName.get(i),
						attachment5_content.get(i), attachment5_contentType.get(i), attachment5_name.get(i), attachment5_fileName.get(i));
				
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				mm.writeTo(os);
				os.flush();
				os.close();
				
				String contentTypeSwA = mm.getContentType();
				
			    //System.out.println("Multipart ("+contentTypeSwA+"): \n"+os.toString());
	
				
				System.out.println("\tTest Richiesta ["+tipo+"] path:"+path+" ...");
				
				HttpBaseRequestEntity<?> request = null;
				if(stream) {
					request= new InputStreamHttpRequestEntity();
					((InputStreamHttpRequestEntity)request).setContent(new ByteArrayInputStream(os.toByteArray()));
				}
				else {
					request = new BinaryHttpRequestEntity();
					((BinaryHttpRequestEntity)request).setContent(os.toByteArray());
				}
				request.setUrl(path);	
				request.setMethod(method);
				Map<String, List<String>> parametersTrasporto = new HashMap<>();
				TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, contentTypeSwA);
				request.setHeaders(parametersTrasporto);
				request.setContentType(contentTypeSwA);
									
				try {				
					apiValidatorOpenApi4j.validate(request);
					if(erroreAtteso) {
						throw new Exception("Atteso errore '"+msgErroreAttesoRichiesta+"' non rilevato");
					}
				} catch (ValidatorException e) {
					if(erroreAtteso && e.getMessage()!=null && e.getMessage().contains(msgErroreAttesoRichiesta)) {
						System.out.println("Errore atteso: "+e.getMessage());
					}
					else {
						throw new Exception("Errore non atteso: "+e.getMessage());
					}
				}
				
				System.out.println("\tTest Richiesta ["+tipo+"] path:"+path+" superato");
			
				
				System.out.println("\tTest Risposta ["+tipo+"] path:"+path+" ...");
				
				HttpBaseResponseEntity<?> response = null;
				if(stream) {
					response= new InputStreamHttpResponseEntity();
					((InputStreamHttpResponseEntity)response).setContent(new ByteArrayInputStream(os.toByteArray()));
				}
				else {
					response = new BinaryHttpResponseEntity();
					((BinaryHttpResponseEntity)response).setContent(os.toByteArray());
				}
				
				response.setStatus(200);		
				response.setMethod(method);
				response.setUrl(path);	
				Map<String, List<String>> responseHeaders = new HashMap<>();
				TransportUtils.setHeader(responseHeaders,HttpConstants.CONTENT_TYPE, contentTypeSwA);
				response.setHeaders(responseHeaders);
				response.setContentType(contentTypeSwA);
			
				try {				
					apiValidatorOpenApi4j.validate(response);
					if(erroreAtteso) {
						throw new Exception("Atteso errore '"+msgErroreAttesoRichiesta+"' non rilevato");
					}
				} catch (ValidatorException e) {
					if(erroreAtteso && e.getMessage()!=null && e.getMessage().contains(msgErroreAttesoRisposta)) {
						System.out.println("Errore atteso: "+e.getMessage());
					}
					else {
						throw new Exception("Errore non atteso: "+e.getMessage());
					}
				}
				
				System.out.println("\tTest Risposta ["+tipo+"] path:"+path+" superato");
			}
				
		}

		System.out.println("TEST Verifica Multipart Request completato (stream:"+stream+" multipartOptimization:"+multipartOptimization+")!");

	}
	
	private static void testMultipartRequestAsArray(OpenAPILibrary openAPILibrary, boolean mergeSpec,
			boolean stream, boolean multipartOptimization)
			throws UtilsException, ProcessingException, URISyntaxException, Exception {
		System.out.println("#### Verifica Multipart Request as array (stream:"+stream+" multipartOptimization:"+multipartOptimization+") ####");
		
		URL url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/allegati.yaml");
					
		ApiSchema apiSchemaYaml = new ApiSchema("teamdigitale-openapi_definitions.yaml", 
				Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/service/schemi/standard/teamdigitale-openapi_definitions.yaml")), ApiSchemaType.YAML);
					
		IApiReader apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		ApiReaderConfig configOpenApi4j = new ApiReaderConfig();
		configOpenApi4j.setProcessInclude(false);
		apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi4j, apiSchemaYaml);
		
		Api apiOpenApi4j = apiReaderOpenApi4j.read();
								
		IApiValidator apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
		OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
		configO.setEmitLogError(logSystemOutError);
		configO.setOpenApiValidatorConfig(new OpenapiLibraryValidatorConfig());
		configO.getOpenApiValidatorConfig().setOpenApiLibrary(openAPILibrary);
		configO.getOpenApiValidatorConfig().setValidateAPISpec(true);
		configO.getOpenApiValidatorConfig().setMergeAPISpec(mergeSpec);
		apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi4j, configO);

		String cat = "{\"pet_type\": \"Cat\",  \"age\": 3}";
		String dog1 = "{\"pet_type\": \"Dog\",  \"bark\": false,  \"breed\": \"Dingo\" }";
		String dog2 = "{\"pet_type\": \"Dog\",  \"bark\": true }";
		String contenuto_cat = "{\"altro\":\"descrizione generica\", \"pet\":"+cat+"}";
		String contenuto_dog1 = "{\"altro\":\"descrizione generica\", \"pet\":"+dog1+"}";
		String contenuto_dog2 = "{\"altro\":\"descrizione generica\", \"pet\":"+dog2+"}";
		String contenuto_errato1 = "{\"altroErrore\":\"descrizione generica\", \"pet\":"+dog2+"}";
		String contenuto_errato2 = "{\"pet\":"+dog2+"}";
		String catErrato = "{\"pet_type\": \"CatErrato\",  \"age\": 3}";
		String contenuto_errato3 = "{\"altro\":\"descrizione generica\", \"pet\":"+catErrato+"}";
		
		byte [] pdf = Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test.pdf"));
				
		
		List<String> macroTest_path = new ArrayList<String>();
		List<String> macroTest_subtype = new ArrayList<String>();
		
		macroTest_path.add("/documenti/multipart/form-data/array-binary");
		macroTest_subtype.add(HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
		
		macroTest_path.add("/documenti/multipart/mixed/array-binary");
		macroTest_subtype.add(HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
		
		macroTest_path.add("/documenti/multipart/form-data/array-json");
		macroTest_subtype.add(HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
		
		macroTest_path.add("/documenti/multipart/mixed/array-json");
		macroTest_subtype.add(HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
		
		
		
		for (int j = 0; j < macroTest_path.size(); j++) {
			
			String macroTestPath = macroTest_path.get(j);
			String macroTestSubtype = macroTest_subtype.get(j);
			

			List<String> tipoTest = new ArrayList<String>();
			List<Boolean> erroreAttesoTest = new ArrayList<Boolean>();
			List<String> msgErroreAttesoTest = new ArrayList<String>();
			List<String> msgErroreAttesoRispostaTest = new ArrayList<String>();
			List<String> pathTest = new ArrayList<String>();
			List<HttpRequestMethod> methodTest = new ArrayList<HttpRequestMethod>();
			List<String> attachment_subtype = new ArrayList<String>();
			
			List<String> attachment_name = new ArrayList<String>();
			List<List<byte[]>> attachment_content = new ArrayList<List<byte[]>>();
			List<String> attachment_contentType = new ArrayList<String>();
			List<String> attachment_fileName = new ArrayList<String>();
			String templateNumero = MultipartUtilities.templateNumero;
						
			
			// *** Test1: corretto ***
			
			tipoTest.add("OK-1: cat");
			erroreAttesoTest.add(false);
			msgErroreAttesoTest.add(null);
			msgErroreAttesoRispostaTest.add(null);
			pathTest.add(macroTestPath);
			methodTest.add(HttpRequestMethod.POST);
			attachment_subtype.add(macroTestSubtype);
			attachment_name.add("\"archivi\"");
			List<byte[]> l = new ArrayList<byte[]>();
			if(macroTestPath.endsWith("array-json")) {
				l.add(contenuto_cat.getBytes());
				l.add(contenuto_dog1.getBytes());
				l.add(contenuto_dog2.getBytes());
			}
			else {
				l.add(pdf);
				l.add(pdf);
				l.add(pdf);
			}
			attachment_content.add(l);
			if(macroTestPath.endsWith("array-json")) {
				attachment_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
				attachment_fileName.add("\"attachment"+templateNumero+".json\"");
				//attachment_fileName.add(null);
			}
			else {
				attachment_contentType.add(HttpConstants.CONTENT_TYPE_PDF);
				attachment_fileName.add("\"attachment"+templateNumero+".pdf\"");
			}
			
			
			
			// *** Test1: errore ***
			
			if(macroTestPath.endsWith("array-json")) {
				tipoTest.add("ERROR-1: contenuto errato nei metadati, required claim 'altro' presente con un nome differente");
				erroreAttesoTest.add(true);
				msgErroreAttesoTest.add("body.archivi.0: Field 'altro' is required. (code: 1026)");
				msgErroreAttesoRispostaTest.add("body.archivi.0: Field 'altro' is required. (code: 1026)");
				pathTest.add(macroTestPath);
				methodTest.add(HttpRequestMethod.POST);
				attachment_subtype.add(macroTestSubtype);
				attachment_name.add("\"archivi\"");
				l = new ArrayList<byte[]>();
				l.add(contenuto_errato1.getBytes());
				l.add(contenuto_dog1.getBytes());
				l.add(contenuto_dog2.getBytes());
				attachment_content.add(l);
				attachment_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
				attachment_fileName.add("\"attachment"+templateNumero+".json\"");
				
				tipoTest.add("ERROR-2: contenuto errato nei metadati, required claim 'altro' non presente e fileName non presente");
				erroreAttesoTest.add(true);
				msgErroreAttesoTest.add("body.archivi.1: Field 'altro' is required. (code: 1026)");
				msgErroreAttesoRispostaTest.add("body.archivi.1: Field 'altro' is required. (code: 1026)");
				pathTest.add(macroTestPath);
				methodTest.add(HttpRequestMethod.POST);
				attachment_subtype.add(macroTestSubtype);
				attachment_name.add("\"archivi\"");
				l = new ArrayList<byte[]>();
				l.add(contenuto_cat.getBytes());
				l.add(contenuto_errato2.getBytes());
				l.add(contenuto_dog2.getBytes());
				attachment_content.add(l);
				attachment_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
				attachment_fileName.add(null);
				
				tipoTest.add("ERROR-3: contenuto errato nei metadati, discriminator value errato");
				erroreAttesoTest.add(true);
				msgErroreAttesoTest.add("body.archivi.2.pet: Schema selection can't be made for discriminator 'pet_type' with value 'CatErrato'. (code: 1003)");
				msgErroreAttesoRispostaTest.add("body.archivi.2.pet: Schema selection can't be made for discriminator 'pet_type' with value 'CatErrato'. (code: 1003)");
				pathTest.add(macroTestPath);
				methodTest.add(HttpRequestMethod.POST);
				attachment_subtype.add(macroTestSubtype);
				attachment_name.add("\"archivi\"");
				l = new ArrayList<byte[]>();
				l.add(contenuto_cat.getBytes());
				l.add(contenuto_dog1.getBytes());
				l.add(contenuto_errato3.getBytes());
				attachment_content.add(l);
				attachment_contentType.add(HttpConstants.CONTENT_TYPE_JSON);
				attachment_fileName.add("\"attachment"+templateNumero+".json\"");
			}
			
		
			
			// Esecuzione test
			
			
			for (int i = 0; i < attachment_name.size(); i++) {
			
				String tipo = tipoTest.get(i);
				
				boolean erroreAtteso = erroreAttesoTest.get(i);
				String msgErroreAttesoRichiesta = msgErroreAttesoTest.get(i);
				String msgErroreAttesoRisposta = msgErroreAttesoRispostaTest.get(i);
				
				String path = pathTest.get(i);
				HttpRequestMethod method = methodTest.get(i);
				
				MimeMultipart mm = MultipartUtilities.buildMimeMultipart(attachment_subtype.get(i),
						attachment_content.get(i), attachment_contentType.get(i), attachment_name.get(i), attachment_fileName.get(i));
								
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				mm.writeTo(os);
				os.flush();
				os.close();
				
				String contentTypeSwA = mm.getContentType();
				
			   // System.out.println("Multipart ("+contentTypeSwA+"): \n"+os.toString());
				if(macroTestPath.endsWith("array-json")) {
					System.out.println("Multipart ("+contentTypeSwA+"): \n"+os.toString());
				}
				
				System.out.println("\tTest Richiesta ["+tipo+"] path:"+path+" ...");
				
				HttpBaseRequestEntity<?> request = null;
				if(stream) {
					request= new InputStreamHttpRequestEntity();
					((InputStreamHttpRequestEntity)request).setContent(new ByteArrayInputStream(os.toByteArray()));
				}
				else {
					request = new BinaryHttpRequestEntity();
					((BinaryHttpRequestEntity)request).setContent(os.toByteArray());
				}
				request.setUrl(path);	
				request.setMethod(method);
				Map<String, List<String>> parametersTrasporto = new HashMap<>();
				TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, contentTypeSwA);
				request.setHeaders(parametersTrasporto);
				request.setContentType(contentTypeSwA);
									
				try {				
					apiValidatorOpenApi4j.validate(request);
					if(erroreAtteso) {
						throw new Exception("Atteso errore '"+msgErroreAttesoRichiesta+"' non rilevato");
					}
				} catch (ValidatorException e) {
					if(erroreAtteso && e.getMessage()!=null && e.getMessage().contains(msgErroreAttesoRichiesta)) {
						System.out.println("Errore atteso: "+e.getMessage());
					}
					else {
						throw new Exception("Errore non atteso: "+e.getMessage());
					}
				}
				
				System.out.println("\tTest Richiesta ["+tipo+"] path:"+path+" superato");
			
				
				System.out.println("\tTest Risposta ["+tipo+"] path:"+path+" ...");
				
				HttpBaseResponseEntity<?> response = null;
				if(stream) {
					response= new InputStreamHttpResponseEntity();
					((InputStreamHttpResponseEntity)response).setContent(new ByteArrayInputStream(os.toByteArray()));
				}
				else {
					response = new BinaryHttpResponseEntity();
					((BinaryHttpResponseEntity)response).setContent(os.toByteArray());
				}
				
				response.setStatus(200);		
				response.setMethod(method);
				response.setUrl(path);	
				Map<String, List<String>> responseHeaders = new HashMap<>();
				TransportUtils.setHeader(responseHeaders,HttpConstants.CONTENT_TYPE, contentTypeSwA);
				response.setHeaders(responseHeaders);
				response.setContentType(contentTypeSwA);
			
				try {				
					apiValidatorOpenApi4j.validate(response);
					if(erroreAtteso) {
						throw new Exception("Atteso errore '"+msgErroreAttesoRichiesta+"' non rilevato");
					}
				} catch (ValidatorException e) {
					if(erroreAtteso && e.getMessage()!=null && e.getMessage().contains(msgErroreAttesoRisposta)) {
						System.out.println("Errore atteso: "+e.getMessage());
					}
					else {
						throw new Exception("Errore non atteso: "+e.getMessage());
					}
				}
				
				System.out.println("\tTest Risposta ["+tipo+"] path:"+path+" superato");
			}
			
			
		}

		System.out.println("TEST Verifica Multipart Request as array completato (stream:"+stream+" multipartOptimization:"+multipartOptimization+")!");

	}
	
	
	
	private static void testFormatString(OpenAPILibrary openAPILibrary, boolean mergeSpec)
			throws UtilsException, ProcessingException, URISyntaxException, Exception {
		System.out.println("#### Verifica Format String ####");
		
		URL url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/allegati.yaml");
					
		ApiSchema apiSchemaYaml = new ApiSchema("teamdigitale-openapi_definitions.yaml", 
				Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/service/schemi/standard/teamdigitale-openapi_definitions.yaml")), ApiSchemaType.YAML);
					
		IApiReader apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		ApiReaderConfig configOpenApi4j = new ApiReaderConfig();
		configOpenApi4j.setProcessInclude(false);
		apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi4j, apiSchemaYaml);
		
		Api apiOpenApi4j = apiReaderOpenApi4j.read();
								
		IApiValidator apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
		OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
		configO.setEmitLogError(logSystemOutError);
		configO.setOpenApiValidatorConfig(new OpenapiLibraryValidatorConfig());
		configO.getOpenApiValidatorConfig().setOpenApiLibrary(openAPILibrary);
		configO.getOpenApiValidatorConfig().setValidateAPISpec(true);
		configO.getOpenApiValidatorConfig().setMergeAPISpec(mergeSpec);
		apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi4j, configO);
		
		String emailCorrette = "\"info@govway.org\", \"Info@GovWay.org\", \"InfoTest@TEST.govway.org\"";
		String uuidCorretti = "\"843bdd09-f3ad-11ec-8c78-5254003636a4\", \"000bdd09-f3ad-11ec-8c78-0004003636a4\"";
		String uriCorrette = "\"http://govway.org/test\",\"https://TEST.GovWay.org/test?prova=1\",\"https://TEST.GovWay.org/test?prova=2&prova=3\","+
				"\"urn:prova@govway.info\",\"type:id\","+
				"\"file://c:/test\",\"file:///tmp/test\",\"file://../../relative\","+
				"\"foo://example.com:8042/over/there?name=ferret#nose\","+ // https://datatracker.ietf.org/doc/html/rfc3986#section-3
				"\"urn:example:animal:ferret:nose\","+
				"\"https://datatracker.ietf.org/doc/html/rfc3986#section-3\"";
		String hostnameCorretti = "\"prova1\",\"govway-test.prova.org\",\"AltroHostname.TEST-PROVA2\",\"EsempioDiTestcon123\"";
		String ipCorretti = "\"127.0.0.1\",\"10.220.22.0\"";
		String ip6Corretti = "\"2001:0db8:85a3:0000:0000:8a2e:0370:7334\"";
		
		String jsonCorretto = "{ \"email\": ["+emailCorrette+"], \"uuid\": ["+uuidCorretti+"], \"uri\":["+uriCorrette+"], \"hostname\":["+hostnameCorretti+"], \"ipv4\":["+ipCorretti+"], \"ipv6\":["+ip6Corretti+"] }";
		
		

		

		List<String> tipoTest = new ArrayList<String>();
		List<String> messagggioTest = new ArrayList<String>();
		List<Boolean> erroreAttesoTest = new ArrayList<Boolean>();
		List<String> msgErroreAttesoTest_request_openapi4j = new ArrayList<String>();
		List<String> msgErroreAttesoTest_response_openapi4j = new ArrayList<String>();
		List<String> msgErroreAttesoTest_request_swagger_request= new ArrayList<String>();
		List<String> msgErroreAttesoTest_response_swagger_request = new ArrayList<String>();
		List<String> msgErroreAttesoTest_request_json_schema= new ArrayList<String>();
		List<String> msgErroreAttesoTest_response_json_schema = new ArrayList<String>();
		
		
		// *** Test1: corretto ***
		
		tipoTest.add("OK-1");
		messagggioTest.add(jsonCorretto);
		erroreAttesoTest.add(false);
		msgErroreAttesoTest_request_openapi4j.add(null);
		msgErroreAttesoTest_response_openapi4j.add(null);
		msgErroreAttesoTest_request_swagger_request.add(null);
		msgErroreAttesoTest_response_swagger_request.add(null);
		msgErroreAttesoTest_request_json_schema.add(null);
		msgErroreAttesoTest_response_json_schema.add(null);
		
		
		// *** Test2: email errata ***
		
		String emailErrata1 = "\"info.it\"";
		String jsonErrateMail1 = "{ \"email\": ["+emailErrata1+"], \"uuid\": ["+uuidCorretti+"], \"uri\":["+uriCorrette+"], \"hostname\":["+hostnameCorretti+"], \"ipv4\":["+ipCorretti+"], \"ipv6\":["+ip6Corretti+"] }";
		tipoTest.add("ERROR-MAIL-1");
		messagggioTest.add(jsonErrateMail1);
		erroreAttesoTest.add(true);
		msgErroreAttesoTest_request_openapi4j.add("body.email.0: Value 'info.it' does not match format 'email'. (code: 1007)");
		msgErroreAttesoTest_response_openapi4j.add("body.email.0: Value 'info.it' does not match format 'email'. (code: 1007)");
		msgErroreAttesoTest_request_swagger_request.add("[ERROR][REQUEST][POST /documenti/format-string @body] [Path '/email/0'] String \"info.it\" is not a valid email address");
		msgErroreAttesoTest_response_swagger_request.add("[ERROR][RESPONSE][] [Path '/email/0'] String \"info.it\" is not a valid email address");
		msgErroreAttesoTest_request_json_schema.add("1034 $.email[0]: info.it is an invalid email");
		msgErroreAttesoTest_response_json_schema.add("1034 $.email[0]: info.it is an invalid email");
		
		String emailErrata2 = "\"Info\"";
		String jsonErrateMail2 = "{ \"email\": ["+emailErrata2+"], \"uuid\": ["+uuidCorretti+"], \"uri\":["+uriCorrette+"], \"hostname\":["+hostnameCorretti+"], \"ipv4\":["+ipCorretti+"], \"ipv6\":["+ip6Corretti+"] }";
		tipoTest.add("ERROR-MAIL-2");
		messagggioTest.add(jsonErrateMail2);
		erroreAttesoTest.add(true);
		msgErroreAttesoTest_request_openapi4j.add("body.email.0: Value 'Info' does not match format 'email'. (code: 1007)");
		msgErroreAttesoTest_response_openapi4j.add("body.email.0: Value 'Info' does not match format 'email'. (code: 1007)");
		msgErroreAttesoTest_request_swagger_request.add("[ERROR][REQUEST][POST /documenti/format-string @body] [Path '/email/0'] String \"Info\" is not a valid email address");
		msgErroreAttesoTest_response_swagger_request.add("[ERROR][RESPONSE][] [Path '/email/0'] String \"Info\" is not a valid email address");
		msgErroreAttesoTest_request_json_schema.add("1034 $.email[0]: Info is an invalid email");
		msgErroreAttesoTest_response_json_schema.add("1034 $.email[0]: Info is an invalid email");
		

		
		// *** Test3: uuid errata ***
		
		String uuidErrata1 = "\"43bdd09-f3ad-11ec-8c78-5254003636a4\""; // manca un numero all'inizio
		String jsonErrateUuid1 = "{ \"email\": ["+emailCorrette+"], \"uuid\": ["+uuidErrata1+"], \"uri\":["+uriCorrette+"], \"hostname\":["+hostnameCorretti+"], \"ipv4\":["+ipCorretti+"], \"ipv6\":["+ip6Corretti+"] }";
		tipoTest.add("ERROR-UUID-1");
		messagggioTest.add(jsonErrateUuid1);
		erroreAttesoTest.add(true);
		msgErroreAttesoTest_request_openapi4j.add("body.uuid.0: Value '43bdd09-f3ad-11ec-8c78-5254003636a4' does not match format 'uuid'. (code: 1007)");
		msgErroreAttesoTest_response_openapi4j.add("body.uuid.0: Value '43bdd09-f3ad-11ec-8c78-5254003636a4' does not match format 'uuid'. (code: 1007)");
		msgErroreAttesoTest_request_swagger_request.add("SKIP");
		msgErroreAttesoTest_response_swagger_request.add("SKIP");
		msgErroreAttesoTest_request_json_schema.add("1034 $.uuid[0]: 43bdd09-f3ad-11ec-8c78-5254003636a4 is an invalid uuid");
		msgErroreAttesoTest_response_json_schema.add("1034 $.uuid[0]: 43bdd09-f3ad-11ec-8c78-5254003636a4 is an invalid uuid");
		
		String uuidErrata2 = "\"843bdd09-f3ad-11ec-8c78-5254003636a\""; // manca un numero alla fine
		String jsonErrateUuid2 = "{ \"email\": ["+emailCorrette+"], \"uuid\": ["+uuidErrata2+"], \"uri\":["+uriCorrette+"], \"hostname\":["+hostnameCorretti+"], \"ipv4\":["+ipCorretti+"], \"ipv6\":["+ip6Corretti+"] }";
		tipoTest.add("ERROR-UUID-2");
		messagggioTest.add(jsonErrateUuid2);
		erroreAttesoTest.add(true);
		msgErroreAttesoTest_request_openapi4j.add("body.uuid.0: Value '843bdd09-f3ad-11ec-8c78-5254003636a' does not match format 'uuid'. (code: 1007)");
		msgErroreAttesoTest_response_openapi4j.add("body.uuid.0: Value '843bdd09-f3ad-11ec-8c78-5254003636a' does not match format 'uuid'. (code: 1007)");
		msgErroreAttesoTest_request_swagger_request.add("SKIP");
		msgErroreAttesoTest_response_swagger_request.add("SKIP");
		msgErroreAttesoTest_request_json_schema.add("1034 $.uuid[0]: 843bdd09-f3ad-11ec-8c78-5254003636a is an invalid uuid");
		msgErroreAttesoTest_response_json_schema.add("1034 $.uuid[0]: 843bdd09-f3ad-11ec-8c78-5254003636a is an invalid uuid");
		
		String uuidErrata3 = "\"843bdd09f3ad11ec8c785254003636a4\""; // manca i trattini
		String jsonErrateUuid3 = "{ \"email\": ["+emailCorrette+"], \"uuid\": ["+uuidErrata3+"], \"uri\":["+uriCorrette+"], \"hostname\":["+hostnameCorretti+"], \"ipv4\":["+ipCorretti+"], \"ipv6\":["+ip6Corretti+"] }";
		tipoTest.add("ERROR-UUID-3");
		messagggioTest.add(jsonErrateUuid3);
		erroreAttesoTest.add(true);
		msgErroreAttesoTest_request_openapi4j.add("body.uuid.0: Value '843bdd09f3ad11ec8c785254003636a4' does not match format 'uuid'. (code: 1007)");
		msgErroreAttesoTest_response_openapi4j.add("body.uuid.0: Value '843bdd09f3ad11ec8c785254003636a4' does not match format 'uuid'. (code: 1007)");
		msgErroreAttesoTest_request_swagger_request.add("[ERROR][REQUEST][POST /documenti/format-string @body] [Path '/uuid/0'] Input string \"843bdd09f3ad11ec8c785254003636a4\" is not a valid UUID");
		msgErroreAttesoTest_response_swagger_request.add("[ERROR][RESPONSE][] [Path '/uuid/0'] Input string \"843bdd09f3ad11ec8c785254003636a4\" is not a valid UUID");
		msgErroreAttesoTest_request_json_schema.add("1034 $.uuid[0]: 843bdd09f3ad11ec8c785254003636a4 is an invalid uuid");
		msgErroreAttesoTest_response_json_schema.add("1034 $.uuid[0]: 843bdd09f3ad11ec8c785254003636a4 is an invalid uuid");
		
		
		
		// *** Test4: uri errata ***
		
		// https://datatracker.ietf.org/doc/html/rfc3986#section-3
		/*	 The generic URI syntax consists of a hierarchical sequence of
		   components referred to as the scheme, authority, path, query, and
		   fragment.

		      URI         = scheme ":" hier-part [ "?" query ] [ "#" fragment ]

		      hier-part   = "//" authority path-abempty
		                  / path-absolute
		                  / path-rootless
		                  / path-empty
		 */
		
		String uriErrata1 = "\"govway.org\""; // manca lo scheme 
		String jsonErrateUri1 = "{ \"email\": ["+emailCorrette+"], \"uuid\": ["+uuidCorretti+"], \"uri\":["+uriErrata1+"], \"hostname\":["+hostnameCorretti+"], \"ipv4\":["+ipCorretti+"], \"ipv6\":["+ip6Corretti+"] }";
		tipoTest.add("ERROR-URI-1");
		messagggioTest.add(jsonErrateUri1);
		erroreAttesoTest.add(true);
		msgErroreAttesoTest_request_openapi4j.add("body.uri.0: Value 'govway.org' does not match format 'uri'. (code: 1007)");
		msgErroreAttesoTest_response_openapi4j.add("body.uri.0: Value 'govway.org' does not match format 'uri'. (code: 1007)");
		msgErroreAttesoTest_request_swagger_request.add("SKIP");
		msgErroreAttesoTest_response_swagger_request.add("SKIP");
		msgErroreAttesoTest_request_json_schema.add("1009 $.uri[0]: does not match the uri pattern");
		msgErroreAttesoTest_response_json_schema.add("1009 $.uri[0]: does not match the uri pattern");
		
		// Nessuna libreria lo rileva
//		String uriErrata2 = "\"http:\""; // manca lo hier part 
//		String jsonErrateUri2 = "{ \"email\": ["+emailCorrette+"], \"uuid\": ["+uuidCorretti+"], \"uri\":["+uriErrata2+"], \"hostname\":["+hostnameCorretti+"], \"ipv4\":["+ipCorretti+"], \"ipv6\":["+ip6Corretti+"] }";
//		tipoTest.add("ERROR-URI-2");
//		messagggioTest.add(jsonErrateUri2);
//		erroreAttesoTest.add(true);

		
		
		
		// *** Test5: hostname errata ***
		
		String hostnameErrata1 = "\"test_con_underscore\"";
		String jsonErrateHostname1 = "{ \"email\": ["+emailCorrette+"], \"uuid\": ["+uuidCorretti+"], \"uri\":["+uriCorrette+"], \"hostname\":["+hostnameErrata1+"], \"ipv4\":["+ipCorretti+"], \"ipv6\":["+ip6Corretti+"] }";
		tipoTest.add("ERROR-HOSTNAME-1");
		messagggioTest.add(jsonErrateHostname1);
		erroreAttesoTest.add(true);
		msgErroreAttesoTest_request_openapi4j.add("body.hostname.0: Value 'test_con_underscore' does not match format 'hostname'. (code: 1007)");
		msgErroreAttesoTest_response_openapi4j.add("body.hostname.0: Value 'test_con_underscore' does not match format 'hostname'. (code: 1007)");
		msgErroreAttesoTest_request_swagger_request.add("SKIP");
		msgErroreAttesoTest_response_swagger_request.add("SKIP");
		msgErroreAttesoTest_request_json_schema.add("1009 $.hostname[0]: does not match the hostname pattern"); // ^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]{0,61}[a-zA-Z0-9])(\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]{0,61}[a-zA-Z0-9]))*$");
		msgErroreAttesoTest_response_json_schema.add("1009 $.hostname[0]: does not match the hostname pattern");
		
		
		
		// *** Test6: ip errata ***
		
		String ipErrata1 = "\"indirizzoSenzaNumeri\"";
		String jsonErrateIp1 = "{ \"email\": ["+emailCorrette+"], \"uuid\": ["+uuidCorretti+"], \"uri\":["+uriCorrette+"], \"hostname\":["+hostnameCorretti+"], \"ipv4\":["+ipErrata1+"], \"ipv6\":["+ip6Corretti+"] }";
		tipoTest.add("ERROR-IPv4-1");
		messagggioTest.add(jsonErrateIp1);
		erroreAttesoTest.add(true);
		msgErroreAttesoTest_request_openapi4j.add("body.ipv4.0: Value 'indirizzoSenzaNumeri' does not match format 'ipv4'. (code: 1007)");
		msgErroreAttesoTest_response_openapi4j.add("body.ipv4.0: Value 'indirizzoSenzaNumeri' does not match format 'ipv4'. (code: 1007)");
		msgErroreAttesoTest_request_swagger_request.add("[ERROR][REQUEST][POST /documenti/format-string @body] [Path '/ipv4/0'] String \"indirizzoSenzaNumeri\" is not a valid IPv4 address");
		msgErroreAttesoTest_response_swagger_request.add("[ERROR][RESPONSE][] [Path '/ipv4/0'] String \"indirizzoSenzaNumeri\" is not a valid IPv4 address");
		msgErroreAttesoTest_request_json_schema.add("1009 $.ipv4[0]: does not match the ipv4 pattern");// ^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"); 
		msgErroreAttesoTest_response_json_schema.add("1009 $.ipv4[0]: does not match the ipv4 pattern");
		
		
		
		// *** Test7: ipv6 errata ***
		
		String ipv6Errata1 = "\"indirizzoSenzaNumeri\"";
		String jsonErrateIpv61 = "{ \"email\": ["+emailCorrette+"], \"uuid\": ["+uuidCorretti+"], \"uri\":["+uriCorrette+"], \"hostname\":["+hostnameCorretti+"], \"ipv4\":["+ipCorretti+"], \"ipv6\":["+ipv6Errata1+"] }";
		tipoTest.add("ERROR-IPv6-1");
		messagggioTest.add(jsonErrateIpv61);
		erroreAttesoTest.add(true);
		msgErroreAttesoTest_request_openapi4j.add("body.ipv6.0: Value 'indirizzoSenzaNumeri' does not match format 'ipv6'. (code: 1007)");
		msgErroreAttesoTest_response_openapi4j.add("body.ipv6.0: Value 'indirizzoSenzaNumeri' does not match format 'ipv6'. (code: 1007)");
		msgErroreAttesoTest_request_swagger_request.add("[ERROR][REQUEST][POST /documenti/format-string @body] [Path '/ipv6/0'] String \"indirizzoSenzaNumeri\" is not a valid IPv6 address");
		msgErroreAttesoTest_response_swagger_request.add("[ERROR][RESPONSE][] [Path '/ipv6/0'] String \"indirizzoSenzaNumeri\" is not a valid IPv6 address");
		msgErroreAttesoTest_request_json_schema.add("1009 $.ipv6[0]: does not match the ipv6 pattern");// ^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$"); 
		msgErroreAttesoTest_response_json_schema.add("1009 $.ipv6[0]: does not match the ipv6 pattern");
		
		
		
		
				
		// Esecuzione test
		
		
		for (int i = 0; i < tipoTest.size(); i++) {
		
			String tipo = tipoTest.get(i);
			
			boolean erroreAtteso = erroreAttesoTest.get(i);
			String content = messagggioTest.get(i);
			String msgErroreAttesoRichiesta = null;
			String msgErroreAttesoRisposta = null;
			switch (openAPILibrary) {
			case openapi4j:
				msgErroreAttesoRichiesta = msgErroreAttesoTest_request_openapi4j.get(i);
				msgErroreAttesoRisposta =msgErroreAttesoTest_response_openapi4j.get(i);
				break;
			case swagger_request_validator:
				msgErroreAttesoRichiesta = msgErroreAttesoTest_request_swagger_request.get(i);
				msgErroreAttesoRisposta =msgErroreAttesoTest_response_swagger_request.get(i);
				break;
			case json_schema:
				msgErroreAttesoRichiesta = msgErroreAttesoTest_request_json_schema.get(i);
				msgErroreAttesoRisposta =msgErroreAttesoTest_response_json_schema.get(i);
				break;
			default:
				break;
			}

			
			String path = "/documenti/format-string";
			HttpRequestMethod method = HttpRequestMethod.POST;
			String contentType = HttpConstants.CONTENT_TYPE_JSON;

			
			System.out.println("\tTest Richiesta ["+tipo+"] path:"+path+" ...");
			
			HttpBaseRequestEntity<?> request = new TextHttpRequestEntity();
			request.setUrl(path);	
			request.setMethod(method);
			((TextHttpRequestEntity)request).setContent(content);
			Map<String, List<String>> parametersTrasporto = new HashMap<>();
			TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, contentType);
			request.setHeaders(parametersTrasporto);
			request.setContentType(contentType);
								
			try {				
				apiValidatorOpenApi4j.validate(request);
				if(erroreAtteso) {
					if("SKIP".equals(msgErroreAttesoRichiesta)) {
						System.out.println("WARN Libreria non supporta ancora correttamente la validazione, si attendeva un errore");
					}
					else {
						throw new Exception("Atteso errore ("+tipo+") '"+msgErroreAttesoRichiesta+"' non rilevato");
					}
				}
			} catch (ValidatorException e) {
				if(erroreAtteso && e.getMessage()!=null && e.getMessage().contains(msgErroreAttesoRichiesta)) {
					System.out.println("Errore atteso: "+e.getMessage());
				}
				else {
					throw new Exception("Errore non atteso ("+tipo+"): "+e.getMessage());
				}
			}
			
			System.out.println("\tTest Richiesta ["+tipo+"] path:"+path+" superato");
		
			
			System.out.println("\tTest Risposta ["+tipo+"] path:"+path+" ...");
			
			HttpBaseResponseEntity<?> response = new TextHttpResponseEntity();
			((TextHttpResponseEntity)response).setContent(content);
			
			response.setStatus(200);		
			response.setMethod(method);
			response.setUrl(path);	
			Map<String, List<String>> responseHeaders = new HashMap<>();
			TransportUtils.setHeader(responseHeaders,HttpConstants.CONTENT_TYPE, contentType);
			response.setHeaders(responseHeaders);
			response.setContentType(contentType);
		
			try {				
				apiValidatorOpenApi4j.validate(response);
				if(erroreAtteso) {
					if("SKIP".equals(msgErroreAttesoRisposta)) {
						System.out.println("WARN Libreria non supporta ancora correttamente la validazione, si attendeva un errore");
					}
					else {
						throw new Exception("Atteso errore ("+tipo+") '"+msgErroreAttesoRisposta+"' non rilevato");
					}
				}
			} catch (ValidatorException e) {
				if(erroreAtteso && e.getMessage()!=null && e.getMessage().contains(msgErroreAttesoRisposta)) {
					System.out.println("Errore atteso: "+e.getMessage());
				}
				else {
					throw new Exception("Errore non atteso ("+tipo+"): "+e.getMessage());
				}
			}
			
			System.out.println("\tTest Risposta ["+tipo+"] path:"+path+" superato");
		}
			
			
		System.out.println("TEST Verifica Format String completato!");

	}
	
	
	private static void testDynamicPath(OpenAPILibrary openAPILibrary, boolean mergeSpec)
			throws UtilsException, ProcessingException, URISyntaxException, Exception {
		System.out.println("#### Verifica Dynamic Path ####");
		
		URL url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/allegati.yaml");
					
		ApiSchema apiSchemaYaml = new ApiSchema("teamdigitale-openapi_definitions.yaml", 
				Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/service/schemi/standard/teamdigitale-openapi_definitions.yaml")), ApiSchemaType.YAML);
					
		IApiReader apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		ApiReaderConfig configOpenApi4j = new ApiReaderConfig();
		configOpenApi4j.setProcessInclude(false);
		apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi4j, apiSchemaYaml);
		
		Api apiOpenApi4j = apiReaderOpenApi4j.read();
								
		IApiValidator apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
		OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
		configO.setEmitLogError(logSystemOutError);
		configO.setOpenApiValidatorConfig(new OpenapiLibraryValidatorConfig());
		configO.getOpenApiValidatorConfig().setOpenApiLibrary(openAPILibrary);
		configO.getOpenApiValidatorConfig().setValidateAPISpec(true);
		configO.getOpenApiValidatorConfig().setMergeAPISpec(mergeSpec);
		apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi4j, configO);
		
		String parameter1 = "080.253.61401A";
		
		String parameter2 = "080.253.61401A.KP%2F2022%2F293.02175680483.2022-01-12T00:00:00+02:00"; // url encoded (essendoci gli / riportati anche sotto, per essere letti nel singolo parametro devono essere url encoded)
		String parameter2_risorsaNonTrovata = "080.253.61401A.KP/2022/293.02175680483.2022-01-12T00:00:00+02:00"; // RISORSA NON TROVATA!
		
		String parameterNonValido = "prova=fr"; 
		String parameterNonValido_url_encoded = "prova%3Dfr"; 
		
		String parameterIllegalForUrl = "prova\\=fr"; 
		String parameterIllegalForUrl_url_encoded = "prova%5C%3Dfr"; 
		
		List<String> tipoTest = new ArrayList<String>();
		List<HttpRequestMethod> methodTest = new ArrayList<HttpRequestMethod>();
		List<String> parametroTest = new ArrayList<String>();
		List<Boolean> erroreAttesoTest = new ArrayList<Boolean>();
		List<Boolean> erroreAttesoProcessingTest = new ArrayList<Boolean>();
		List<String> erroreAtteso_openapi4j = new ArrayList<String>();
		List<String> erroreAtteso_swagger_request = new ArrayList<String>();
		List<String> erroreAtteso_json_schema = new ArrayList<String>();
		
		
		// *** Test: corretto GET ***
		
		tipoTest.add("GET-1");
		methodTest.add(HttpRequestMethod.GET);
		parametroTest.add(parameter1);
		erroreAttesoTest.add(false);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add(null);
		erroreAtteso_swagger_request.add(null);
		erroreAtteso_json_schema.add(null);
		
		tipoTest.add("GET-2");
		methodTest.add(HttpRequestMethod.GET);
		parametroTest.add(parameter2);
		erroreAttesoTest.add(false);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add(null);
		erroreAtteso_swagger_request.add(null);
		erroreAtteso_json_schema.add(null);
		
		tipoTest.add("PUT-1");
		methodTest.add(HttpRequestMethod.PUT);
		parametroTest.add(parameter1);
		erroreAttesoTest.add(false);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add(null);
		erroreAtteso_swagger_request.add(null);
		erroreAtteso_json_schema.add(null);
		
		tipoTest.add("PUT-2");
		methodTest.add(HttpRequestMethod.PUT);
		parametroTest.add(parameter2);
		erroreAttesoTest.add(false);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add(null);
		erroreAtteso_swagger_request.add(null);
		erroreAtteso_json_schema.add(null);
		
		tipoTest.add("POST-1");
		methodTest.add(HttpRequestMethod.PUT);
		parametroTest.add(parameter1);
		erroreAttesoTest.add(false);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add(null);
		erroreAtteso_swagger_request.add(null);
		erroreAtteso_json_schema.add(null);
		
		tipoTest.add("POST-2");
		methodTest.add(HttpRequestMethod.PUT);
		parametroTest.add(parameter2);
		erroreAttesoTest.add(false);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add(null);
		erroreAtteso_swagger_request.add(null);
		erroreAtteso_json_schema.add(null);
		
		
		tipoTest.add("GET-OPERATION-NOT-FOUND");
		methodTest.add(HttpRequestMethod.GET);
		parametroTest.add(parameter2_risorsaNonTrovata);
		erroreAttesoTest.add(false);
		erroreAttesoProcessingTest.add(true);
		erroreAtteso_openapi4j.add("Resource GET '/documenti/dynamic-path/"+parameter2_risorsaNonTrovata+"' not found");
		erroreAtteso_swagger_request.add("Resource GET '/documenti/dynamic-path/"+parameter2_risorsaNonTrovata+"' not found");
		erroreAtteso_json_schema.add("Resource GET '/documenti/dynamic-path/"+parameter2_risorsaNonTrovata+"' not found");

		tipoTest.add("PUT-OPERATION-NOT-FOUND");
		methodTest.add(HttpRequestMethod.PUT);
		parametroTest.add(parameter2_risorsaNonTrovata);
		erroreAttesoTest.add(false);
		erroreAttesoProcessingTest.add(true);
		erroreAtteso_openapi4j.add("Resource PUT '/documenti/dynamic-path/"+parameter2_risorsaNonTrovata+"' not found");
		erroreAtteso_swagger_request.add("Resource PUT '/documenti/dynamic-path/"+parameter2_risorsaNonTrovata+"' not found");
		erroreAtteso_json_schema.add("Resource PUT '/documenti/dynamic-path/"+parameter2_risorsaNonTrovata+"' not found");
		
		tipoTest.add("POST-OPERATION-NOT-FOUND");
		methodTest.add(HttpRequestMethod.POST);
		parametroTest.add(parameter2_risorsaNonTrovata);
		erroreAttesoTest.add(false);
		erroreAttesoProcessingTest.add(true);
		erroreAtteso_openapi4j.add("Resource POST '/documenti/dynamic-path/"+parameter2_risorsaNonTrovata+"' not found");
		erroreAtteso_swagger_request.add("Resource POST '/documenti/dynamic-path/"+parameter2_risorsaNonTrovata+"' not found");
		erroreAtteso_json_schema.add("Resource POST '/documenti/dynamic-path/"+parameter2_risorsaNonTrovata+"' not found");
		
		
		tipoTest.add("GET-NON_VALIDO-1");
		methodTest.add(HttpRequestMethod.GET);
		parametroTest.add(parameterNonValido);
		erroreAttesoTest.add(true);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add("dynamic_id: 'prova=fr' does not respect pattern '^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$'. (code: 1025)\n"
				+ "From: dynamic_id.<pattern>");
		erroreAtteso_swagger_request.add("Invalid value 'prova=fr' in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		erroreAtteso_json_schema.add("Invalid value 'prova=fr' in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		
		tipoTest.add("GET-NON_VALIDO-UrlEncoded");
		methodTest.add(HttpRequestMethod.GET);
		parametroTest.add(parameterNonValido_url_encoded);
		erroreAttesoTest.add(true);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add("dynamic_id: 'prova=fr' does not respect pattern '^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$'. (code: 1025)\n"
				+ "From: dynamic_id.<pattern>");
		erroreAtteso_swagger_request.add("Invalid value 'prova=fr' (urlEncoded: 'prova%3Dfr') in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		erroreAtteso_json_schema.add("Invalid value 'prova=fr' (urlEncoded: 'prova%3Dfr') in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		
		
		tipoTest.add("POST-NON_VALIDO-1");
		methodTest.add(HttpRequestMethod.POST);
		parametroTest.add(parameterNonValido);
		erroreAttesoTest.add(true);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add("dynamic_id: 'prova=fr' does not respect pattern '^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$'. (code: 1025)\n"
				+ "From: dynamic_id.<pattern>");
		erroreAtteso_swagger_request.add("Invalid value 'prova=fr' in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		erroreAtteso_json_schema.add("Invalid value 'prova=fr' in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		
		tipoTest.add("POST-NON_VALIDO-UrlEncoded");
		methodTest.add(HttpRequestMethod.POST);
		parametroTest.add(parameterNonValido_url_encoded);
		erroreAttesoTest.add(true);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add("dynamic_id: 'prova=fr' does not respect pattern '^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$'. (code: 1025)\n"
				+ "From: dynamic_id.<pattern>");
		erroreAtteso_swagger_request.add("Invalid value 'prova=fr' (urlEncoded: 'prova%3Dfr') in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		erroreAtteso_json_schema.add("Invalid value 'prova=fr' (urlEncoded: 'prova%3Dfr') in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		
		
		tipoTest.add("PUT-NON_VALIDO-1");
		methodTest.add(HttpRequestMethod.PUT);
		parametroTest.add(parameterNonValido);
		erroreAttesoTest.add(true);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add("dynamic_id: 'prova=fr' does not respect pattern '^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$'. (code: 1025)\n"
				+ "From: dynamic_id.<pattern>");
		erroreAtteso_swagger_request.add("Invalid value 'prova=fr' in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		erroreAtteso_json_schema.add("Invalid value 'prova=fr' in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		
		tipoTest.add("PUT-NON_VALIDO-UrlEncoded");
		methodTest.add(HttpRequestMethod.PUT);
		parametroTest.add(parameterNonValido_url_encoded);
		erroreAttesoTest.add(true);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add("dynamic_id: 'prova=fr' does not respect pattern '^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$'. (code: 1025)\n"
				+ "From: dynamic_id.<pattern>");
		erroreAtteso_swagger_request.add("Invalid value 'prova=fr' (urlEncoded: 'prova%3Dfr') in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		erroreAtteso_json_schema.add("Invalid value 'prova=fr' (urlEncoded: 'prova%3Dfr') in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		
		
		
		tipoTest.add("GET-ILLEGAL_CHARACTER-1");
		methodTest.add(HttpRequestMethod.GET);
		parametroTest.add(parameterIllegalForUrl);
		erroreAttesoTest.add(true);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add("dynamic_id: 'prova\\\\=fr' does not respect pattern '^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$'. (code: 1025)\n"
				+ "From: dynamic_id.<pattern>");
		erroreAtteso_swagger_request.add("Invalid value 'prova\\=fr' in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		erroreAtteso_json_schema.add("Invalid value 'prova\\=fr' in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		
		tipoTest.add("GET-ILLEGAL_CHARACTER-UrlEncoded");
		methodTest.add(HttpRequestMethod.GET);
		parametroTest.add(parameterIllegalForUrl_url_encoded);
		erroreAttesoTest.add(true);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add("dynamic_id: 'prova\\\\=fr' does not respect pattern '^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$'. (code: 1025)\n"
				+ "From: dynamic_id.<pattern>");
		erroreAtteso_swagger_request.add("Invalid value 'prova\\=fr' (urlEncoded: 'prova%5C%3Dfr') in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		erroreAtteso_json_schema.add("Invalid value 'prova\\=fr' (urlEncoded: 'prova%5C%3Dfr') in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		
		
		tipoTest.add("POST-ILLEGAL_CHARACTER-1");
		methodTest.add(HttpRequestMethod.POST);
		parametroTest.add(parameterIllegalForUrl);
		erroreAttesoTest.add(true);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add("dynamic_id: 'prova\\\\=fr' does not respect pattern '^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$'. (code: 1025)\n"
				+ "From: dynamic_id.<pattern>");
		erroreAtteso_swagger_request.add("Invalid value 'prova\\=fr' in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		erroreAtteso_json_schema.add("Invalid value 'prova\\=fr' in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
				
		tipoTest.add("POST-ILLEGAL_CHARACTER-UrlEncoded");
		methodTest.add(HttpRequestMethod.POST);
		parametroTest.add(parameterIllegalForUrl_url_encoded);
		erroreAttesoTest.add(true);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add("dynamic_id: 'prova\\\\=fr' does not respect pattern '^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$'. (code: 1025)\n"
				+ "From: dynamic_id.<pattern>");
		erroreAtteso_swagger_request.add("Invalid value 'prova\\=fr' (urlEncoded: 'prova%5C%3Dfr') in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		erroreAtteso_json_schema.add("Invalid value 'prova\\=fr' (urlEncoded: 'prova%5C%3Dfr') in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		
		
		tipoTest.add("PUT-ILLEGAL_CHARACTER-1");
		methodTest.add(HttpRequestMethod.PUT);
		parametroTest.add(parameterIllegalForUrl);
		erroreAttesoTest.add(true);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add("dynamic_id: 'prova\\\\=fr' does not respect pattern '^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$'. (code: 1025)\n"
				+ "From: dynamic_id.<pattern>");
		erroreAtteso_swagger_request.add("Invalid value 'prova\\=fr' in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		erroreAtteso_json_schema.add("Invalid value 'prova\\=fr' in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
				
		tipoTest.add("PUT-ILLEGAL_CHARACTER-UrlEncoded");
		methodTest.add(HttpRequestMethod.PUT);
		parametroTest.add(parameterIllegalForUrl_url_encoded);
		erroreAttesoTest.add(true);
		erroreAttesoProcessingTest.add(false);
		erroreAtteso_openapi4j.add("dynamic_id: 'prova\\\\=fr' does not respect pattern '^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$'. (code: 1025)\n"
				+ "From: dynamic_id.<pattern>");
		erroreAtteso_swagger_request.add("Invalid value 'prova\\=fr' (urlEncoded: 'prova%5C%3Dfr') in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		erroreAtteso_json_schema.add("Invalid value 'prova\\=fr' (urlEncoded: 'prova%5C%3Dfr') in dynamic path 'dynamic_id' (expected type 'string'): Pattern match failed ('^[A-Za-z0-9\\-\\.\\+_:\\/\\\\]{1,73}$')");
		
		
		
		
		
				
		// Esecuzione test
		
		
		for (int i = 0; i < tipoTest.size(); i++) {
		
			String tipo = tipoTest.get(i);
			
			boolean erroreAtteso = erroreAttesoTest.get(i);
			boolean erroreAttesoProcessing = erroreAttesoProcessingTest.get(i);
			String content = "HelloWorld";
			String msgErroreAttesoRichiesta = null;
			switch (openAPILibrary) {
			case openapi4j:
				msgErroreAttesoRichiesta = erroreAtteso_openapi4j.get(i);
				break;
			case swagger_request_validator:
				msgErroreAttesoRichiesta = erroreAtteso_swagger_request.get(i);
				break;
			case json_schema:
				msgErroreAttesoRichiesta = erroreAtteso_json_schema.get(i);
				break;
			default:
				break;
			}

			
			String path = "/documenti/dynamic-path/"+parametroTest.get(i);
			HttpRequestMethod method = methodTest.get(i);
			String contentType = HttpConstants.CONTENT_TYPE_PLAIN;

			
			System.out.println("\tTest Richiesta ["+tipo+"] path:"+path+" ...");
			
			HttpBaseRequestEntity<?> request = new TextHttpRequestEntity();
			request.setUrl(path);
			request.setMethod(method);
			if(!HttpRequestMethod.GET.equals(method)) {
				((TextHttpRequestEntity)request).setContent(content);
				Map<String, List<String>> parametersTrasporto = new HashMap<>();
				TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, contentType);
				request.setHeaders(parametersTrasporto);
				request.setContentType(contentType);
			}
			
								
			try {				
				apiValidatorOpenApi4j.validate(request);
				if(erroreAtteso) {
					if("SKIP".equals(msgErroreAttesoRichiesta)) {
						System.out.println("WARN Libreria non supporta ancora correttamente la validazione, si attendeva un errore");
					}
					else {
						throw new Exception("Atteso errore ("+tipo+") '"+msgErroreAttesoRichiesta+"' non rilevato");
					}
				}
			} catch (ValidatorException e) {
				if(erroreAtteso && e.getMessage()!=null && e.getMessage().contains(msgErroreAttesoRichiesta)) {
					System.out.println("Errore atteso: "+e.getMessage());
				}
				else {
					throw new Exception("Errore non atteso ("+tipo+"): "+e.getMessage());
				}
			} catch(org.openspcoop2.utils.rest.ProcessingException pe) {
				if(erroreAttesoProcessing && pe.getMessage()!=null && pe.getMessage().contains(msgErroreAttesoRichiesta)) {
					System.out.println("Errore atteso: "+pe.getMessage());
				}
				else {
					// Su jenkins e via maven non succede
					if( "GET-ILLEGAL_CHARACTER-1".equals(tipo) || "POST-ILLEGAL_CHARACTER-1".equals(tipo) || "PUT-ILLEGAL_CHARACTER-1".equals(tipo)) {
						String errore2 = "Illegal character in path at index 29: /documenti/dynamic-path/prova\\=fr";
						if(pe.getMessage()!=null && pe.getMessage().contains(errore2)) {
							System.out.println("Errore atteso: "+pe.getMessage());
						}
						else {
							throw new Exception("Errore non atteso ("+tipo+"): "+pe.getMessage());
						}
					} else {
						throw new Exception("Errore non atteso ("+tipo+"): "+pe.getMessage());
					}
				}
			}
			
			System.out.println("\tTest Richiesta ["+tipo+"] path:"+path+" superato");
		
		}
			
			
		System.out.println("TEST Verifica Format String completato!");

	}
	
	
	
	private static void testComposedSchemaParameters(OpenAPILibrary openAPILibrary, boolean mergeSpec)
			throws UtilsException, ProcessingException, URISyntaxException, Exception {
		System.out.println("#### Verifica ComposedSchemaParameters ####");
		
		URL url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/allegati.yaml");
					
		ApiSchema apiSchemaYaml = new ApiSchema("teamdigitale-openapi_definitions.yaml", 
				Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/service/schemi/standard/teamdigitale-openapi_definitions.yaml")), ApiSchemaType.YAML);
					
		IApiReader apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		ApiReaderConfig configOpenApi4j = new ApiReaderConfig();
		configOpenApi4j.setProcessInclude(false);
		apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi4j, apiSchemaYaml);
		
		Api apiOpenApi4j = apiReaderOpenApi4j.read();
								
		IApiValidator apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
		OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
		configO.setEmitLogError(logSystemOutError);
		configO.setOpenApiValidatorConfig(new OpenapiLibraryValidatorConfig());
		configO.getOpenApiValidatorConfig().setOpenApiLibrary(openAPILibrary);
		configO.getOpenApiValidatorConfig().setValidateAPISpec(true);
		configO.getOpenApiValidatorConfig().setMergeAPISpec(mergeSpec);
		apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi4j, configO);
		

	
	
		List<String> tipoTest = new ArrayList<String>();
		List<String> complexTypeTest = new ArrayList<String>();
		List<String> pathTest = new ArrayList<String>();
		List<String> queryTest = new ArrayList<String>();
		List<String> headerTest = new ArrayList<String>();
		List<String> cookieTest = new ArrayList<String>();
		List<String> pathInLineTest = new ArrayList<String>();
		List<String> queryInLineTest = new ArrayList<String>();
		List<String> headerInLineTest = new ArrayList<String>();
		List<String> cookieInLineTest = new ArrayList<String>();
		List<Boolean> erroreAttesoRichiestaTest = new ArrayList<Boolean>();
		List<Boolean> erroreAttesoRispostaTest = new ArrayList<Boolean>();
		List<String> msgErroreAttesoTest_openapi4j = new ArrayList<String>();
		List<String> msgErroreAttesoTest_swagger_request= new ArrayList<String>();
		List<String> msgErroreAttesoTest_swagger_response= new ArrayList<String>();
		List<String> msgErroreAttesoTest_json_schema= new ArrayList<String>();
		
		
		
		// ######## ANY OF ######################
		
		String valoreCorretto1 = "ABCDEFGHILK"; // esattamente 11 caratteri
		String valoreCorretto2 = "01234"; // da 3 a 5 cifre
		String valoreNonCorretto1 = "ABCDEFGHILKALTRECIFRE"; // esattamente 11 caratteri
		String valoreNonCorretto2 = "0123456789aa"; // da 3 a 5 cifre
		
		String valoreCorrettoInt1 = "20"; //  maximum: 20
		String valoreCorrettoInt2 = "53"; // da 50 a 55
		String valoreNonCorrettoInt = "59"; // da 50 a 55
		
		// *** Test1: corretto ***
		
		tipoTest.add("OK-1");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(false);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add(null);
		msgErroreAttesoTest_swagger_request.add(null);
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add(null);
		
		
		tipoTest.add("OK-2");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto2);
		queryTest.add(valoreCorretto2);
		headerTest.add(valoreCorretto2);
		cookieTest.add(valoreCorretto2);
		pathInLineTest.add(valoreCorrettoInt2);
		queryInLineTest.add(valoreCorrettoInt2);
		headerInLineTest.add(valoreCorrettoInt2);
		cookieInLineTest.add(valoreCorrettoInt2);
		erroreAttesoRichiestaTest.add(false);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add(null);
		msgErroreAttesoTest_swagger_request.add(null);
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add(null);
		
		
		
		tipoTest.add("ERRORE-PATH-1");
		complexTypeTest.add("any_of");
		pathTest.add(valoreNonCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_path: '"+valoreNonCorretto1+"' does not respect pattern '^[A-Z]{11}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_path.<anyOf>.<pattern>\n"
				+ "composed_schema_any_of_path: '"+valoreNonCorretto1+"' does not respect pattern '^[0-9]{3,5}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_path.<anyOf>.<pattern>");
		msgErroreAttesoTest_swagger_request.add("Invalid value '"+valoreNonCorretto1+"' in dynamic path 'composed_schema_any_of_path' (expected type 'string'): Pattern match failed ('^[A-Z]{11}$')\n"
				+ "Invalid value '"+valoreNonCorretto1+"' in dynamic path 'composed_schema_any_of_path' (expected type 'string'): Pattern match failed ('^[0-9]{3,5}$')");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto1+"' in dynamic path 'composed_schema_any_of_path' (expected type 'string'): Pattern match failed ('^[A-Z]{11}$')\n"
				+ "Invalid value '"+valoreNonCorretto1+"' in dynamic path 'composed_schema_any_of_path' (expected type 'string'): Pattern match failed ('^[0-9]{3,5}$')");
		msgErroreAttesoTest_swagger_response.add(null);
		
		tipoTest.add("ERRORE-PATH-2");
		complexTypeTest.add("any_of");
		pathTest.add(valoreNonCorretto2);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_path: '"+valoreNonCorretto2+"' does not respect pattern '^[A-Z]{11}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_path.<anyOf>.<pattern>\n"
				+ "composed_schema_any_of_path: '"+valoreNonCorretto2+"' does not respect pattern '^[0-9]{3,5}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_path.<anyOf>.<pattern>");
		
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto2+"' in dynamic path 'composed_schema_any_of_path' (expected type 'string'): Pattern match failed ('^[A-Z]{11}$')\n"
				+ "Invalid value '"+valoreNonCorretto2+"' in dynamic path 'composed_schema_any_of_path' (expected type 'string'): Pattern match failed ('^[0-9]{3,5}$')");
		msgErroreAttesoTest_swagger_request.add(msgErroreAttesoTest_json_schema.get(msgErroreAttesoTest_json_schema.size()-1));
		msgErroreAttesoTest_swagger_response.add(null);

		
		tipoTest.add("ERRORE-PATH-3");
		complexTypeTest.add("any_of");
		pathTest.add(valoreNonCorrettoInt);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_path: '"+valoreNonCorrettoInt+"' does not respect pattern '^[A-Z]{11}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_path.<anyOf>.<pattern>\n"
				+ "composed_schema_any_of_path: Min length is '3', found '2'. (code: 1017)\n"
				+ "From: composed_schema_any_of_path.<anyOf>.<minLength>\n"
				+ "composed_schema_any_of_path: '"+valoreNonCorrettoInt+"' does not respect pattern '^[0-9]{3,5}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_path.<anyOf>.<pattern>");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInt+"' in dynamic path 'composed_schema_any_of_path' (expected type 'string'): Pattern match failed ('^[A-Z]{11}$')\n"
				+ "Invalid value '"+valoreNonCorrettoInt+"' in dynamic path 'composed_schema_any_of_path' (expected type 'string'): Too short, expected min length '3'");
		msgErroreAttesoTest_swagger_request.add(msgErroreAttesoTest_json_schema.get(msgErroreAttesoTest_json_schema.size()-1));
		msgErroreAttesoTest_swagger_response.add(null);

		
		
		tipoTest.add("ERRORE-PATH-INLINE-1");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreNonCorretto1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_path_inline: Value '"+valoreNonCorretto1+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_path_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_path_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_path_inline.<anyOf>.<type>\n"
				+ "composed_schema_any_of_path_inline: Value '"+valoreNonCorretto1+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_path_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_path_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_path_inline.<anyOf>.<type>");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto1+"' in dynamic path 'composed_schema_any_of_path_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto1+"\"\n"
				+ "Invalid value '"+valoreNonCorretto1+"' in dynamic path 'composed_schema_any_of_path_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto1+"\"");
		msgErroreAttesoTest_swagger_request.add(msgErroreAttesoTest_json_schema.get(msgErroreAttesoTest_json_schema.size()-1));
		msgErroreAttesoTest_swagger_response.add(null);

		
		
		tipoTest.add("ERRORE-PATH-INLINE-2");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreNonCorretto2);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_path_inline: Value '"+valoreNonCorretto2+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_path_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_path_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_path_inline.<anyOf>.<type>\n"
				+ "composed_schema_any_of_path_inline: Value '"+valoreNonCorretto2+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_path_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_path_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_path_inline.<anyOf>.<type>");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto2+"' in dynamic path 'composed_schema_any_of_path_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto2+"\"\n"
				+ "Invalid value '"+valoreNonCorretto2+"' in dynamic path 'composed_schema_any_of_path_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto2+"\"");		
		msgErroreAttesoTest_swagger_request.add(msgErroreAttesoTest_json_schema.get(msgErroreAttesoTest_json_schema.size()-1));
		msgErroreAttesoTest_swagger_response.add(null);

		
		tipoTest.add("ERRORE-PATH-INLINE-3");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreNonCorrettoInt);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_path_inline: Maximum is '20', found '"+valoreNonCorrettoInt+"'. (code: 1010)\n"
				+ "From: composed_schema_any_of_path_inline.<anyOf>.<maximum>\n"
				+ "composed_schema_any_of_path_inline: Maximum is '55', found '"+valoreNonCorrettoInt+"'. (code: 1010)\n"
				+ "From: composed_schema_any_of_path_inline.<anyOf>.<maximum>");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInt+"' in dynamic path 'composed_schema_any_of_path_inline' (expected type 'int32'): Value higher than the maximum '20'\n"
				+ "Invalid value '"+valoreNonCorrettoInt+"' in dynamic path 'composed_schema_any_of_path_inline' (expected type 'int32'): Value higher than the maximum '55'");
		msgErroreAttesoTest_swagger_request.add(msgErroreAttesoTest_json_schema.get(msgErroreAttesoTest_json_schema.size()-1));
		msgErroreAttesoTest_swagger_response.add(null);

		
		
		
		
		tipoTest.add("ERRORE-QUERY-1");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreNonCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_query: '"+valoreNonCorretto1+"' does not respect pattern '^[A-Z]{11}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_query.<anyOf>.<pattern>\n"
				+ "composed_schema_any_of_query: '"+valoreNonCorretto1+"' does not respect pattern '^[0-9]{3,5}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_query.<anyOf>.<pattern>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @query.composed_schema_any_of_query] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto1+"\"\n"
				+ "	* /anyOf/1: ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto1+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto1+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto1+"\"");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto1+"' in query parameter 'composed_schema_any_of_query' (expected type 'string'): Pattern match failed ('^[A-Z]{11}$')\n"
				+ "Invalid value '"+valoreNonCorretto1+"' in query parameter 'composed_schema_any_of_query' (expected type 'string'): Pattern match failed ('^[0-9]{3,5}$')");
		msgErroreAttesoTest_swagger_response.add(null);

		
		tipoTest.add("ERRORE-QUERY-2");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreNonCorretto2);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_query: '"+valoreNonCorretto2+"' does not respect pattern '^[A-Z]{11}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_query.<anyOf>.<pattern>\n"
				+ "composed_schema_any_of_query: '"+valoreNonCorretto2+"' does not respect pattern '^[0-9]{3,5}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_query.<anyOf>.<pattern>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @query.composed_schema_any_of_query] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto2+"\"\n"
				+ "	* /anyOf/1: ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto2+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto2+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto2+"\"");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto2+"' in query parameter 'composed_schema_any_of_query' (expected type 'string'): Pattern match failed ('^[A-Z]{11}$')\n"
				+ "Invalid value '"+valoreNonCorretto2+"' in query parameter 'composed_schema_any_of_query' (expected type 'string'): Pattern match failed ('^[0-9]{3,5}$')");
		msgErroreAttesoTest_swagger_response.add(null);

		
		tipoTest.add("ERRORE-QUERY-3");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreNonCorrettoInt);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_query: '"+valoreNonCorrettoInt+"' does not respect pattern '^[A-Z]{11}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_query.<anyOf>.<pattern>\n"
				+ "composed_schema_any_of_query: Min length is '3', found '2'. (code: 1017)\n"
				+ "From: composed_schema_any_of_query.<anyOf>.<minLength>\n"
				+ "composed_schema_any_of_query: '"+valoreNonCorrettoInt+"' does not respect pattern '^[0-9]{3,5}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_query.<anyOf>.<pattern>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @query.composed_schema_any_of_query] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorrettoInt+"\"\n"
				+ "	* /anyOf/1: String \""+valoreNonCorrettoInt+"\" is too short (length: 2, required minimum: 3)\n"
				+ "	* /anyOf/1: ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorrettoInt+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorrettoInt+"\"	\n"
				+ "	- [ERROR][] String \""+valoreNonCorrettoInt+"\" is too short (length: 2, required minimum: 3)	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorrettoInt+"\"");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInt+"' in query parameter 'composed_schema_any_of_query' (expected type 'string'): Pattern match failed ('^[A-Z]{11}$')\n"
				+ "Invalid value '"+valoreNonCorrettoInt+"' in query parameter 'composed_schema_any_of_query' (expected type 'string'): Too short, expected min length '3'");
		
		
		tipoTest.add("ERRORE-QUERY-INLINE-1");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreNonCorretto1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_query_inline: Value '"+valoreNonCorretto1+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_query_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_query_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_query_inline.<anyOf>.<type>\n"
				+ "composed_schema_any_of_query_inline: Value '"+valoreNonCorretto1+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_query_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_query_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_query_inline.<anyOf>.<type>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @query.composed_schema_any_of_query_inline] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])\n"
				+ "	* /anyOf/1: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto1+"' in query parameter 'composed_schema_any_of_query_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto1+"\"\n"
				+ "Invalid value '"+valoreNonCorretto1+"' in query parameter 'composed_schema_any_of_query_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto1+"\"");
		
		
		tipoTest.add("ERRORE-QUERY-INLINE-2");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreNonCorretto2);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_query_inline: Value '"+valoreNonCorretto2+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_query_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_query_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_query_inline.<anyOf>.<type>\n"
				+ "composed_schema_any_of_query_inline: Value '"+valoreNonCorretto2+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_query_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_query_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_query_inline.<anyOf>.<type>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @query.composed_schema_any_of_query_inline] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])\n"
				+ "	* /anyOf/1: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto2+"' in query parameter 'composed_schema_any_of_query_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto2+"\"\n"
				+ "Invalid value '"+valoreNonCorretto2+"' in query parameter 'composed_schema_any_of_query_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto2+"\"");		
		
		
		tipoTest.add("ERRORE-QUERY-INLINE-3");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreNonCorrettoInt);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_query_inline: Maximum is '20', found '"+valoreNonCorrettoInt+"'. (code: 1010)\n"
				+ "From: composed_schema_any_of_query_inline.<anyOf>.<maximum>\n"
				+ "composed_schema_any_of_query_inline: Maximum is '55', found '"+valoreNonCorrettoInt+"'. (code: 1010)\n"
				+ "From: composed_schema_any_of_query_inline.<anyOf>.<maximum>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @query.composed_schema_any_of_query_inline] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: Numeric instance is greater than the required maximum (maximum: 20, found: 59)\n"
				+ "	* /anyOf/1: Numeric instance is greater than the required maximum (maximum: 55, found: 59)	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 20, found: 59)	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 55, found: 59)");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInt+"' in query parameter 'composed_schema_any_of_query_inline' (expected type 'int32'): Value higher than the maximum '20'\n"
				+ "Invalid value '"+valoreNonCorrettoInt+"' in query parameter 'composed_schema_any_of_query_inline' (expected type 'int32'): Value higher than the maximum '55'");
		
		
		
		
		
	
		tipoTest.add("ERRORE-COOKIE-1");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreNonCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_cookie: '"+valoreNonCorretto1+"' does not respect pattern '^[A-Z]{11}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_cookie.<anyOf>.<pattern>\n"
				+ "composed_schema_any_of_cookie: '"+valoreNonCorretto1+"' does not respect pattern '^[0-9]{3,5}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_cookie.<anyOf>.<pattern>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @cookie.composed_schema_any_of_cookie] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto1+"\"\n"
				+ "	* /anyOf/1: ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto1+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto1+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto1+"\"");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto1+"' in cookie 'composed_schema_any_of_cookie' (expected type 'string'): Pattern match failed ('^[A-Z]{11}$')\n"
				+ "Invalid value '"+valoreNonCorretto1+"' in cookie 'composed_schema_any_of_cookie' (expected type 'string'): Pattern match failed ('^[0-9]{3,5}$')");
		
		
		tipoTest.add("ERRORE-COOKIE-2");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreNonCorretto2);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_cookie: '"+valoreNonCorretto2+"' does not respect pattern '^[A-Z]{11}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_cookie.<anyOf>.<pattern>\n"
				+ "composed_schema_any_of_cookie: '"+valoreNonCorretto2+"' does not respect pattern '^[0-9]{3,5}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_cookie.<anyOf>.<pattern>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @cookie.composed_schema_any_of_cookie] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto2+"\"\n"
				+ "	* /anyOf/1: ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto2+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto2+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto2+"\"");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto2+"' in cookie 'composed_schema_any_of_cookie' (expected type 'string'): Pattern match failed ('^[A-Z]{11}$')\n"
				+ "Invalid value '"+valoreNonCorretto2+"' in cookie 'composed_schema_any_of_cookie' (expected type 'string'): Pattern match failed ('^[0-9]{3,5}$')");
		
		
		tipoTest.add("ERRORE-COOKIE-3");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreNonCorrettoInt);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_cookie: '"+valoreNonCorrettoInt+"' does not respect pattern '^[A-Z]{11}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_cookie.<anyOf>.<pattern>\n"
				+ "composed_schema_any_of_cookie: Min length is '3', found '2'. (code: 1017)\n"
				+ "From: composed_schema_any_of_cookie.<anyOf>.<minLength>\n"
				+ "composed_schema_any_of_cookie: '"+valoreNonCorrettoInt+"' does not respect pattern '^[0-9]{3,5}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_cookie.<anyOf>.<pattern>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @cookie.composed_schema_any_of_cookie] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorrettoInt+"\"\n"
				+ "	* /anyOf/1: String \""+valoreNonCorrettoInt+"\" is too short (length: 2, required minimum: 3)\n"
				+ "	* /anyOf/1: ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorrettoInt+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorrettoInt+"\"	\n"
				+ "	- [ERROR][] String \""+valoreNonCorrettoInt+"\" is too short (length: 2, required minimum: 3)	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorrettoInt+"\"");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInt+"' in cookie 'composed_schema_any_of_cookie' (expected type 'string'): Pattern match failed ('^[A-Z]{11}$')\n"
				+ "Invalid value '"+valoreNonCorrettoInt+"' in cookie 'composed_schema_any_of_cookie' (expected type 'string'): Too short, expected min length '3'");
		
		
		tipoTest.add("ERRORE-COOKIE-INLINE-1");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreNonCorretto1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_cookie_inline: Value '"+valoreNonCorretto1+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_cookie_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_cookie_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_cookie_inline.<anyOf>.<type>\n"
				+ "composed_schema_any_of_cookie_inline: Value '"+valoreNonCorretto1+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_cookie_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_cookie_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_cookie_inline.<anyOf>.<type>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @cookie.composed_schema_any_of_cookie_inline] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])\n"
				+ "	* /anyOf/1: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto1+"' in cookie 'composed_schema_any_of_cookie_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto1+"\"\n"
				+ "Invalid value '"+valoreNonCorretto1+"' in cookie 'composed_schema_any_of_cookie_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto1+"\"");
		
		
		tipoTest.add("ERRORE-COOKIE-INLINE-2");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreNonCorretto2);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_cookie_inline: Value '"+valoreNonCorretto2+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_cookie_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_cookie_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_cookie_inline.<anyOf>.<type>\n"
				+ "composed_schema_any_of_cookie_inline: Value '"+valoreNonCorretto2+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_cookie_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_cookie_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_cookie_inline.<anyOf>.<type>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @cookie.composed_schema_any_of_cookie_inline] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])\n"
				+ "	* /anyOf/1: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto2+"' in cookie 'composed_schema_any_of_cookie_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto2+"\"\n"
				+ "Invalid value '"+valoreNonCorretto2+"' in cookie 'composed_schema_any_of_cookie_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto2+"\"");		
		
		
		tipoTest.add("ERRORE-COOKIE-INLINE-3");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreNonCorrettoInt);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_cookie_inline: Maximum is '20', found '"+valoreNonCorrettoInt+"'. (code: 1010)\n"
				+ "From: composed_schema_any_of_cookie_inline.<anyOf>.<maximum>\n"
				+ "composed_schema_any_of_cookie_inline: Maximum is '55', found '"+valoreNonCorrettoInt+"'. (code: 1010)\n"
				+ "From: composed_schema_any_of_cookie_inline.<anyOf>.<maximum>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @cookie.composed_schema_any_of_cookie_inline] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: Numeric instance is greater than the required maximum (maximum: 20, found: 59)\n"
				+ "	* /anyOf/1: Numeric instance is greater than the required maximum (maximum: 55, found: 59)	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 20, found: 59)	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 55, found: 59)");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInt+"' in cookie 'composed_schema_any_of_cookie_inline' (expected type 'int32'): Value higher than the maximum '20'\n"
				+ "Invalid value '"+valoreNonCorrettoInt+"' in cookie 'composed_schema_any_of_cookie_inline' (expected type 'int32'): Value higher than the maximum '55'");
		
		
		
		
		
		tipoTest.add("ERRORE-HEADER-1");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreNonCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(true);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_header: '"+valoreNonCorretto1+"' does not respect pattern '^[A-Z]{11}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_header.<anyOf>.<pattern>\n"
				+ "composed_schema_any_of_header: '"+valoreNonCorretto1+"' does not respect pattern '^[0-9]{3,5}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_header.<anyOf>.<pattern>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @header.composed_schema_any_of_header] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto1+"\"\n"
				+ "	* /anyOf/1: ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto1+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto1+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto1+"\"");
		msgErroreAttesoTest_swagger_response.add("Validation failed.\n"
				+ "[ERROR][RESPONSE][] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto1+"\"\n"
				+ "	* /anyOf/1: ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto1+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto1+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto1+"\"");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto1+"' in http header 'composed_schema_any_of_header' (expected type 'string'): Pattern match failed ('^[A-Z]{11}$')\n"
				+ "Invalid value '"+valoreNonCorretto1+"' in http header 'composed_schema_any_of_header' (expected type 'string'): Pattern match failed ('^[0-9]{3,5}$')");
		
		
		tipoTest.add("ERRORE-HEADER-2");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreNonCorretto2);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(true);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_header: '"+valoreNonCorretto2+"' does not respect pattern '^[A-Z]{11}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_header.<anyOf>.<pattern>\n"
				+ "composed_schema_any_of_header: '"+valoreNonCorretto2+"' does not respect pattern '^[0-9]{3,5}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_header.<anyOf>.<pattern>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @header.composed_schema_any_of_header] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto2+"\"\n"
				+ "	* /anyOf/1: ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto2+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto2+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto2+"\"");
		msgErroreAttesoTest_swagger_response.add("[ERROR][RESPONSE][] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto2+"\"\n"
				+ "	* /anyOf/1: ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto2+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorretto2+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorretto2+"\"");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto2+"' in http header 'composed_schema_any_of_header' (expected type 'string'): Pattern match failed ('^[A-Z]{11}$')\n"
				+ "Invalid value '"+valoreNonCorretto2+"' in http header 'composed_schema_any_of_header' (expected type 'string'): Pattern match failed ('^[0-9]{3,5}$')");
		
		
		tipoTest.add("ERRORE-HEADER-3");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreNonCorrettoInt);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(true);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_header: '"+valoreNonCorrettoInt+"' does not respect pattern '^[A-Z]{11}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_header.<anyOf>.<pattern>\n"
				+ "composed_schema_any_of_header: Min length is '3', found '2'. (code: 1017)\n"
				+ "From: composed_schema_any_of_header.<anyOf>.<minLength>\n"
				+ "composed_schema_any_of_header: '"+valoreNonCorrettoInt+"' does not respect pattern '^[0-9]{3,5}$'. (code: 1025)\n"
				+ "From: composed_schema_any_of_header.<anyOf>.<pattern>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @header.composed_schema_any_of_header] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorrettoInt+"\"\n"
				+ "	* /anyOf/1: String \""+valoreNonCorrettoInt+"\" is too short (length: 2, required minimum: 3)\n"
				+ "	* /anyOf/1: ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorrettoInt+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorrettoInt+"\"	\n"
				+ "	- [ERROR][] String \""+valoreNonCorrettoInt+"\" is too short (length: 2, required minimum: 3)	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorrettoInt+"\"");
		msgErroreAttesoTest_swagger_response.add("Validation failed.\n"
				+ "[ERROR][RESPONSE][] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorrettoInt+"\"\n"
				+ "	* /anyOf/1: String \""+valoreNonCorrettoInt+"\" is too short (length: 2, required minimum: 3)\n"
				+ "	* /anyOf/1: ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorrettoInt+"\"	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{11}$\" does not match input string \""+valoreNonCorrettoInt+"\"	\n"
				+ "	- [ERROR][] String \""+valoreNonCorrettoInt+"\" is too short (length: 2, required minimum: 3)	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[0-9]{3,5}$\" does not match input string \""+valoreNonCorrettoInt+"\"");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInt+"' in http header 'composed_schema_any_of_header' (expected type 'string'): Pattern match failed ('^[A-Z]{11}$')\n"
				+ "Invalid value '"+valoreNonCorrettoInt+"' in http header 'composed_schema_any_of_header' (expected type 'string'): Too short, expected min length '3'");
		
		
		tipoTest.add("ERRORE-HEADER-INLINE-1");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreNonCorretto1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(true);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_header_inline: Value '"+valoreNonCorretto1+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_header_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_header_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_header_inline.<anyOf>.<type>\n"
				+ "composed_schema_any_of_header_inline: Value '"+valoreNonCorretto1+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_header_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_header_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_header_inline.<anyOf>.<type>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @header.composed_schema_any_of_header_inline] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])\n"
				+ "	* /anyOf/1: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])");
		msgErroreAttesoTest_swagger_response.add("Validation failed.\n"
				+ "[ERROR][RESPONSE][] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])\n"
				+ "	* /anyOf/1: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto1+"' in http header 'composed_schema_any_of_header_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto1+"\"\n"
				+ "Invalid value '"+valoreNonCorretto1+"' in http header 'composed_schema_any_of_header_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto1+"\"");
		
		
		tipoTest.add("ERRORE-HEADER-INLINE-2");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreNonCorretto2);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(true);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_header_inline: Value '"+valoreNonCorretto2+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_header_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_header_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_header_inline.<anyOf>.<type>\n"
				+ "composed_schema_any_of_header_inline: Value '"+valoreNonCorretto2+"' does not match format 'int32'. (code: 1007)\n"
				+ "From: composed_schema_any_of_header_inline.<anyOf>.<format>\n"
				+ "composed_schema_any_of_header_inline: Type expected 'integer', found 'string'. (code: 1027)\n"
				+ "From: composed_schema_any_of_header_inline.<anyOf>.<type>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @header.composed_schema_any_of_header_inline] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])\n"
				+ "	* /anyOf/1: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])");
		msgErroreAttesoTest_swagger_response.add("Validation failed.\n"
				+ "[ERROR][RESPONSE][] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])\n"
				+ "	* /anyOf/1: Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])	\n"
				+ "	- [ERROR][] Instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto2+"' in http header 'composed_schema_any_of_header_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto2+"\"\n"
				+ "Invalid value '"+valoreNonCorretto2+"' in http header 'composed_schema_any_of_header_inline' (expected type 'int32'): For input string: \""+valoreNonCorretto2+"\"");		
		
		
		tipoTest.add("ERRORE-HEADER-INLINE-3");
		complexTypeTest.add("any_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreNonCorrettoInt);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(true);
		msgErroreAttesoTest_openapi4j.add("composed_schema_any_of_header_inline: Maximum is '20', found '"+valoreNonCorrettoInt+"'. (code: 1010)\n"
				+ "From: composed_schema_any_of_header_inline.<anyOf>.<maximum>\n"
				+ "composed_schema_any_of_header_inline: Maximum is '55', found '"+valoreNonCorrettoInt+"'. (code: 1010)\n"
				+ "From: composed_schema_any_of_header_inline.<anyOf>.<maximum>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/any_of/ABCDEFGHILK/20 @header.composed_schema_any_of_header_inline] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: Numeric instance is greater than the required maximum (maximum: 20, found: 59)\n"
				+ "	* /anyOf/1: Numeric instance is greater than the required maximum (maximum: 55, found: 59)	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 20, found: 59)	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 55, found: 59)");
		msgErroreAttesoTest_swagger_response.add("Validation failed.\n"
				+ "[ERROR][RESPONSE][] Instance failed to match at least one required schema among 2\n"
				+ "	* /anyOf/0: Numeric instance is greater than the required maximum (maximum: 20, found: 59)\n"
				+ "	* /anyOf/1: Numeric instance is greater than the required maximum (maximum: 55, found: 59)	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 20, found: 59)	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 55, found: 59)");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInt+"' in http header 'composed_schema_any_of_header_inline' (expected type 'int32'): Value higher than the maximum '20'\n"
				+ "Invalid value '"+valoreNonCorrettoInt+"' in http header 'composed_schema_any_of_header_inline' (expected type 'int32'): Value higher than the maximum '55'");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// ######## ALL OF ######################
		
		valoreCorretto1 = "ABCDEFGHILK"; // da 5 a 11 caratteri la prima condizione, min 7 caratteri la seconda
		String valoreNonCorrettoInAssoluto = "A";
		String valoreNonCorrettoRegolaDue = "ABCDE";
		
		valoreCorrettoInt1 = "50"; //   maximum: 100 la prima condizione, min 50, max 55 la seconda
		String valoreNonCorrettoIntInAssoluto = "104"; 
		String valoreNonCorrettoIntRegolaDue = "90";
		
		
		// *** Test1: corretto ***
		
		tipoTest.add("OK-1");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(false);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add(null);
		msgErroreAttesoTest_swagger_request.add(null);
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add(null);
		
				
		
		tipoTest.add("ERRORE-PATH-1");
		complexTypeTest.add("all_of");
		pathTest.add(valoreNonCorrettoRegolaDue);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_path: Min length is '7', found '"+valoreNonCorrettoRegolaDue.length()+"'. (code: 1017)\n"
				+ "From: composed_schema_all_of_path.<allOf>.<minLength>");			
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoRegolaDue+"' in dynamic path 'composed_schema_all_of_path' (expected type 'string'): Too short, expected min length '7'");
		msgErroreAttesoTest_swagger_request.add(msgErroreAttesoTest_json_schema.get(msgErroreAttesoTest_json_schema.size()-1));
		msgErroreAttesoTest_swagger_response.add(null);
		
				
		tipoTest.add("ERRORE-PATH-2");
		complexTypeTest.add("all_of");
		pathTest.add(valoreNonCorrettoInAssoluto);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_path: '"+valoreNonCorrettoInAssoluto+"' does not respect pattern '^[A-Z]{5,11}$'. (code: 1025)\n"
				+ "From: composed_schema_all_of_path.<allOf>.<pattern>");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInAssoluto+"' in dynamic path 'composed_schema_all_of_path' (expected type 'string'): Pattern match failed ('^[A-Z]{5,11}$')\n"
				+ "Invalid value '"+valoreNonCorrettoInAssoluto+"' in dynamic path 'composed_schema_all_of_path' (expected type 'string'): Too short, expected min length '7'");
		msgErroreAttesoTest_swagger_request.add(msgErroreAttesoTest_json_schema.get(msgErroreAttesoTest_json_schema.size()-1));
		msgErroreAttesoTest_swagger_response.add(null);

	
		
		tipoTest.add("ERRORE-PATH-INLINE-1");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreNonCorrettoIntRegolaDue);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_path_inline: Maximum is '55', found '"+valoreNonCorrettoIntRegolaDue+"'. (code: 1010)\n"
				+ "From: composed_schema_all_of_path_inline.<allOf>.<maximum>");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoIntRegolaDue+"' in dynamic path 'composed_schema_all_of_path_inline' (expected type 'int32'): Value higher than the maximum '55'");
		msgErroreAttesoTest_swagger_request.add(msgErroreAttesoTest_json_schema.get(msgErroreAttesoTest_json_schema.size()-1));
		msgErroreAttesoTest_swagger_response.add(null);
				
		tipoTest.add("ERRORE-PATH-INLINE-2");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreNonCorrettoIntInAssoluto);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_path_inline: Maximum is '100', found '"+valoreNonCorrettoIntInAssoluto+"'. (code: 1010)\n"
				+ "From: composed_schema_all_of_path_inline.<allOf>.<maximum>");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoIntInAssoluto+"' in dynamic path 'composed_schema_all_of_path_inline' (expected type 'int32'): Value higher than the maximum '100'\n"
				+ "Invalid value '"+valoreNonCorrettoIntInAssoluto+"' in dynamic path 'composed_schema_all_of_path_inline' (expected type 'int32'): Value higher than the maximum '55'");
		msgErroreAttesoTest_swagger_request.add(msgErroreAttesoTest_json_schema.get(msgErroreAttesoTest_json_schema.size()-1));
		msgErroreAttesoTest_swagger_response.add(null);		
		
		
		
		tipoTest.add("ERRORE-QUERY-1");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreNonCorrettoRegolaDue);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_query: Min length is '7', found '"+valoreNonCorrettoRegolaDue.length()+"'. (code: 1017)\n"
				+ "From: composed_schema_all_of_query.<allOf>.<minLength>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/all_of/ABCDEFGHILK/50 @query.composed_schema_all_of_query] Instance failed to match all required schemas (matched only 1 out of 2)\n"
				+ "	* /allOf/1: String \"ABCDE\" is too short (length: "+valoreNonCorrettoRegolaDue.length()+", required minimum: 7)	\n"
				+ "	- [ERROR][] String \"ABCDE\" is too short (length: "+valoreNonCorrettoRegolaDue.length()+", required minimum: 7)");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoRegolaDue+"' in query parameter 'composed_schema_all_of_query' (expected type 'string'): Too short, expected min length '7'");
		

		tipoTest.add("ERRORE-QUERY-2");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreNonCorrettoInAssoluto);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_query: '"+valoreNonCorrettoInAssoluto+"' does not respect pattern '^[A-Z]{5,11}$'. (code: 1025)\n"
				+ "From: composed_schema_all_of_query.<allOf>.<pattern>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/all_of/ABCDEFGHILK/50 @query.composed_schema_all_of_query] Instance failed to match all required schemas (matched only 0 out of 2)\n"
				+ "	* /allOf/0: ECMA 262 regex \"^[A-Z]{5,11}$\" does not match input string \""+valoreNonCorrettoInAssoluto+"\"\n"
				+ "	* /allOf/1: String \"A\" is too short (length: 1, required minimum: 7)	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{5,11}$\" does not match input string \""+valoreNonCorrettoInAssoluto+"\"	\n"
				+ "	- [ERROR][] String \"A\" is too short (length: 1, required minimum: 7)");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInAssoluto+"' in query parameter 'composed_schema_all_of_query' (expected type 'string'): Pattern match failed ('^[A-Z]{5,11}$')\n"
				+ "Invalid value '"+valoreNonCorrettoInAssoluto+"' in query parameter 'composed_schema_all_of_query' (expected type 'string'): Too short, expected min length '7'");
	
		
		tipoTest.add("ERRORE-QUERY-INLINE-1");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreNonCorrettoIntRegolaDue);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_query_inline: Maximum is '55', found '"+valoreNonCorrettoIntRegolaDue+"'. (code: 1010)\n"
				+ "From: composed_schema_all_of_query_inline.<allOf>.<maximum>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/all_of/ABCDEFGHILK/50 @query.composed_schema_all_of_query_inline] Instance failed to match all required schemas (matched only 1 out of 2)\n"
				+ "	* /allOf/1: Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntRegolaDue+")	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntRegolaDue+")");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoIntRegolaDue+"' in query parameter 'composed_schema_all_of_query_inline' (expected type 'int32'): Value higher than the maximum '55'");
		
				
		tipoTest.add("ERRORE-QUERY-INLINE-2");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreNonCorrettoIntInAssoluto);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_query_inline: Maximum is '100', found '"+valoreNonCorrettoIntInAssoluto+"'. (code: 1010)\n"
				+ "From: composed_schema_all_of_query_inline.<allOf>.<maximum>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/all_of/ABCDEFGHILK/50 @query.composed_schema_all_of_query_inline] Instance failed to match all required schemas (matched only 0 out of 2)\n"
				+ "	* /allOf/0: Numeric instance is greater than the required maximum (maximum: 100, found: "+valoreNonCorrettoIntInAssoluto+")\n"
				+ "	* /allOf/1: Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntInAssoluto+")	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 100, found: "+valoreNonCorrettoIntInAssoluto+")	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntInAssoluto+")");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoIntInAssoluto+"' in query parameter 'composed_schema_all_of_query_inline' (expected type 'int32'): Value higher than the maximum '100'\n"
				+ "Invalid value '"+valoreNonCorrettoIntInAssoluto+"' in query parameter 'composed_schema_all_of_query_inline' (expected type 'int32'): Value higher than the maximum '55'");
		
		
		
		
		tipoTest.add("ERRORE-COOKIE-1");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreNonCorrettoRegolaDue);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_cookie: Min length is '7', found '"+valoreNonCorrettoRegolaDue.length()+"'. (code: 1017)\n"
				+ "From: composed_schema_all_of_cookie.<allOf>.<minLength>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/all_of/ABCDEFGHILK/50 @cookie.composed_schema_all_of_cookie] Instance failed to match all required schemas (matched only 1 out of 2)\n"
				+ "	* /allOf/1: String \"ABCDE\" is too short (length: "+valoreNonCorrettoRegolaDue.length()+", required minimum: 7)	\n"
				+ "	- [ERROR][] String \"ABCDE\" is too short (length: "+valoreNonCorrettoRegolaDue.length()+", required minimum: 7)");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoRegolaDue+"' in cookie 'composed_schema_all_of_cookie' (expected type 'string'): Too short, expected min length '7'");
		
				
		tipoTest.add("ERRORE-COOKIE-2");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreNonCorrettoInAssoluto);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_cookie: '"+valoreNonCorrettoInAssoluto+"' does not respect pattern '^[A-Z]{5,11}$'. (code: 1025)\n"
				+ "From: composed_schema_all_of_cookie.<allOf>.<pattern>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/all_of/ABCDEFGHILK/50 @cookie.composed_schema_all_of_cookie] Instance failed to match all required schemas (matched only 0 out of 2)\n"
				+ "	* /allOf/0: ECMA 262 regex \"^[A-Z]{5,11}$\" does not match input string \""+valoreNonCorrettoInAssoluto+"\"\n"
				+ "	* /allOf/1: String \""+valoreNonCorrettoInAssoluto+"\" is too short (length: 1, required minimum: 7)	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{5,11}$\" does not match input string \""+valoreNonCorrettoInAssoluto+"\"	\n"
				+ "	- [ERROR][] String \""+valoreNonCorrettoInAssoluto+"\" is too short (length: 1, required minimum: 7)");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInAssoluto+"' in cookie 'composed_schema_all_of_cookie' (expected type 'string'): Pattern match failed ('^[A-Z]{5,11}$')\n"
				+ "Invalid value '"+valoreNonCorrettoInAssoluto+"' in cookie 'composed_schema_all_of_cookie' (expected type 'string'): Too short, expected min length '7'");
	
		
		tipoTest.add("ERRORE-COOKIE-INLINE-1");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreNonCorrettoIntRegolaDue);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_cookie_inline: Maximum is '55', found '"+valoreNonCorrettoIntRegolaDue+"'. (code: 1010)\n"
				+ "From: composed_schema_all_of_cookie_inline.<allOf>.<maximum>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/all_of/ABCDEFGHILK/50 @cookie.composed_schema_all_of_cookie_inline] Instance failed to match all required schemas (matched only 1 out of 2)\n"
				+ "	* /allOf/1: Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntRegolaDue+")	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntRegolaDue+")");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoIntRegolaDue+"' in cookie 'composed_schema_all_of_cookie_inline' (expected type 'int32'): Value higher than the maximum '55'");
		
				
		tipoTest.add("ERRORE-COOKIE-INLINE-2");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreNonCorrettoIntInAssoluto);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_cookie_inline: Maximum is '100', found '"+valoreNonCorrettoIntInAssoluto+"'. (code: 1010)\n"
				+ "From: composed_schema_all_of_cookie_inline.<allOf>.<maximum>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/all_of/ABCDEFGHILK/50 @cookie.composed_schema_all_of_cookie_inline] Instance failed to match all required schemas (matched only 0 out of 2)\n"
				+ "	* /allOf/0: Numeric instance is greater than the required maximum (maximum: 100, found: "+valoreNonCorrettoIntInAssoluto+")\n"
				+ "	* /allOf/1: Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntInAssoluto+")	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 100, found: "+valoreNonCorrettoIntInAssoluto+")	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntInAssoluto+")");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoIntInAssoluto+"' in cookie 'composed_schema_all_of_cookie_inline' (expected type 'int32'): Value higher than the maximum '100'\n"
				+ "Invalid value '"+valoreNonCorrettoIntInAssoluto+"' in cookie 'composed_schema_all_of_cookie_inline' (expected type 'int32'): Value higher than the maximum '55'");
		
		
		
		
		tipoTest.add("ERRORE-HEADER-1");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreNonCorrettoRegolaDue);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(true);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_header: Min length is '7', found '"+valoreNonCorrettoRegolaDue.length()+"'. (code: 1017)\n"
				+ "From: composed_schema_all_of_header.<allOf>.<minLength>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/all_of/ABCDEFGHILK/50 @header.composed_schema_all_of_header] Instance failed to match all required schemas (matched only 1 out of 2)\n"
				+ "	* /allOf/1: String \"ABCDE\" is too short (length: "+valoreNonCorrettoRegolaDue.length()+", required minimum: 7)	\n"
				+ "	- [ERROR][] String \"ABCDE\" is too short (length: "+valoreNonCorrettoRegolaDue.length()+", required minimum: 7)");
		msgErroreAttesoTest_swagger_response.add("Validation failed.\n"
				+ "[ERROR][RESPONSE][] Instance failed to match all required schemas (matched only 1 out of 2)\n"
				+ "	* /allOf/1: String \"ABCDE\" is too short (length: "+valoreNonCorrettoRegolaDue.length()+", required minimum: 7)	\n"
				+ "	- [ERROR][] String \"ABCDE\" is too short (length: "+valoreNonCorrettoRegolaDue.length()+", required minimum: 7)");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoRegolaDue+"' in http header 'composed_schema_all_of_header' (expected type 'string'): Too short, expected min length '7'");
		
				
		tipoTest.add("ERRORE-HEADER-2");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreNonCorrettoInAssoluto);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(true);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_header: '"+valoreNonCorrettoInAssoluto+"' does not respect pattern '^[A-Z]{5,11}$'. (code: 1025)\n"
				+ "From: composed_schema_all_of_header.<allOf>.<pattern>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/all_of/ABCDEFGHILK/50 @header.composed_schema_all_of_header] Instance failed to match all required schemas (matched only 0 out of 2)\n"
				+ "	* /allOf/0: ECMA 262 regex \"^[A-Z]{5,11}$\" does not match input string \""+valoreNonCorrettoInAssoluto+"\"\n"
				+ "	* /allOf/1: String \""+valoreNonCorrettoInAssoluto+"\" is too short (length: 1, required minimum: 7)	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{5,11}$\" does not match input string \""+valoreNonCorrettoInAssoluto+"\"	\n"
				+ "	- [ERROR][] String \""+valoreNonCorrettoInAssoluto+"\" is too short (length: 1, required minimum: 7)");
		msgErroreAttesoTest_swagger_response.add("Validation failed.\n"
				+ "[ERROR][RESPONSE][] Instance failed to match all required schemas (matched only 0 out of 2)\n"
				+ "	* /allOf/0: ECMA 262 regex \"^[A-Z]{5,11}$\" does not match input string \""+valoreNonCorrettoInAssoluto+"\"\n"
				+ "	* /allOf/1: String \""+valoreNonCorrettoInAssoluto+"\" is too short (length: 1, required minimum: 7)	\n"
				+ "	- [ERROR][] ECMA 262 regex \"^[A-Z]{5,11}$\" does not match input string \""+valoreNonCorrettoInAssoluto+"\"	\n"
				+ "	- [ERROR][] String \""+valoreNonCorrettoInAssoluto+"\" is too short (length: 1, required minimum: 7)");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInAssoluto+"' in http header 'composed_schema_all_of_header' (expected type 'string'): Pattern match failed ('^[A-Z]{5,11}$')\n"
				+ "Invalid value '"+valoreNonCorrettoInAssoluto+"' in http header 'composed_schema_all_of_header' (expected type 'string'): Too short, expected min length '7'");
	
		
		tipoTest.add("ERRORE-HEADER-INLINE-1");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreNonCorrettoIntRegolaDue);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(true);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_header_inline: Maximum is '55', found '"+valoreNonCorrettoIntRegolaDue+"'. (code: 1010)\n"
				+ "From: composed_schema_all_of_header_inline.<allOf>.<maximum>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/all_of/ABCDEFGHILK/50 @header.composed_schema_all_of_header_inline] Instance failed to match all required schemas (matched only 1 out of 2)\n"
				+ "	* /allOf/1: Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntRegolaDue+")	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntRegolaDue+")");
		msgErroreAttesoTest_swagger_response.add("Validation failed.\n"
				+ "[ERROR][RESPONSE][] Instance failed to match all required schemas (matched only 1 out of 2)\n"
				+ "	* /allOf/1: Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntRegolaDue+")	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntRegolaDue+")");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoIntRegolaDue+"' in http header 'composed_schema_all_of_header_inline' (expected type 'int32'): Value higher than the maximum '55'");
		
				
		tipoTest.add("ERRORE-HEADER-INLINE-2");
		complexTypeTest.add("all_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreNonCorrettoIntInAssoluto);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(true);
		msgErroreAttesoTest_openapi4j.add("composed_schema_all_of_header_inline: Maximum is '100', found '"+valoreNonCorrettoIntInAssoluto+"'. (code: 1010)\n"
				+ "From: composed_schema_all_of_header_inline.<allOf>.<maximum>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/all_of/ABCDEFGHILK/50 @header.composed_schema_all_of_header_inline] Instance failed to match all required schemas (matched only 0 out of 2)\n"
				+ "	* /allOf/0: Numeric instance is greater than the required maximum (maximum: 100, found: "+valoreNonCorrettoIntInAssoluto+")\n"
				+ "	* /allOf/1: Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntInAssoluto+")	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 100, found: "+valoreNonCorrettoIntInAssoluto+")	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntInAssoluto+")");
		msgErroreAttesoTest_swagger_response.add("Validation failed.\n"
				+ "[ERROR][RESPONSE][] Instance failed to match all required schemas (matched only 0 out of 2)\n"
				+ "	* /allOf/0: Numeric instance is greater than the required maximum (maximum: 100, found: "+valoreNonCorrettoIntInAssoluto+")\n"
				+ "	* /allOf/1: Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntInAssoluto+")	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 100, found: "+valoreNonCorrettoIntInAssoluto+")	\n"
				+ "	- [ERROR][] Numeric instance is greater than the required maximum (maximum: 55, found: "+valoreNonCorrettoIntInAssoluto+")");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoIntInAssoluto+"' in http header 'composed_schema_all_of_header_inline' (expected type 'int32'): Value higher than the maximum '100'\n"
				+ "Invalid value '"+valoreNonCorrettoIntInAssoluto+"' in http header 'composed_schema_all_of_header_inline' (expected type 'int32'): Value higher than the maximum '55'");
		
		
		
		
		
		
		
		// ######## ONE OF ######################
		
		valoreCorretto1 = "ABCDE"; // da 5 a 11 caratteri la prima condizione, min 7 caratteri la seconda, faccio avere un match solo con la prima
		valoreNonCorretto1 = "ABCDEFGHILK"; // ha un match con entrambe le regole
		
		valoreCorrettoInt1 = "90"; //   maximum: 100 la prima condizione, min 50, max 55 la seconda, faccio avere un match solo con la prima
		valoreNonCorrettoInt = "50"; // ha un match con entrambe le regole
		
		
		// *** Test1: corretto ***
		
		tipoTest.add("OK-1");
		complexTypeTest.add("one_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(false);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add(null);
		msgErroreAttesoTest_swagger_request.add(null);
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add(null);
		
				
		
		
		tipoTest.add("ERRORE-PATH-1");
		complexTypeTest.add("one_of");
		pathTest.add(valoreNonCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_one_of_path: More than 1 schema is valid. (code: 1023)\n"
				+ "From: composed_schema_one_of_path.<oneOf>");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto1+"' in dynamic path 'composed_schema_one_of_path': expected validates the value against exactly one of the subschemas; founded valid in 2 schemas");
		msgErroreAttesoTest_swagger_request.add(msgErroreAttesoTest_json_schema.get(msgErroreAttesoTest_json_schema.size()-1));
		msgErroreAttesoTest_swagger_response.add(null);
		
		
		tipoTest.add("ERRORE-PATH-INLINE-1");
		complexTypeTest.add("one_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreNonCorrettoInt);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_one_of_path_inline: More than 1 schema is valid. (code: 1023)\n"
				+ "From: composed_schema_one_of_path_inline.<oneOf>");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInt+"' in dynamic path 'composed_schema_one_of_path_inline': expected validates the value against exactly one of the subschemas; founded valid in 2 schemas");
		msgErroreAttesoTest_swagger_request.add(msgErroreAttesoTest_json_schema.get(msgErroreAttesoTest_json_schema.size()-1));
		msgErroreAttesoTest_swagger_response.add(null);
		
		
		
		tipoTest.add("ERRORE-QUERY-1");
		complexTypeTest.add("one_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreNonCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_one_of_query: More than 1 schema is valid. (code: 1023)\n"
				+ "From: composed_schema_one_of_query.<oneOf>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/one_of/ABCDE/90 @query.composed_schema_one_of_query] Instance failed to match exactly one schema (matched 2 out of 2)");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto1+"' in query parameter 'composed_schema_one_of_query': expected validates the value against exactly one of the subschemas; founded valid in 2 schemas");
		
		
		tipoTest.add("ERRORE-QUERY-INLINE-1");
		complexTypeTest.add("one_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreNonCorrettoInt);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_one_of_query_inline: More than 1 schema is valid. (code: 1023)\n"
				+ "From: composed_schema_one_of_query_inline.<oneOf>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/one_of/ABCDE/90 @query.composed_schema_one_of_query_inline] Instance failed to match exactly one schema (matched 2 out of 2)");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInt+"' in query parameter 'composed_schema_one_of_query_inline': expected validates the value against exactly one of the subschemas; founded valid in 2 schemas");
		
		
		
		tipoTest.add("ERRORE-COOKIE-1");
		complexTypeTest.add("one_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreNonCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_one_of_cookie: More than 1 schema is valid. (code: 1023)\n"
				+ "From: composed_schema_one_of_cookie.<oneOf>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/one_of/ABCDE/90 @cookie.composed_schema_one_of_cookie] Instance failed to match exactly one schema (matched 2 out of 2)");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto1+"' in cookie 'composed_schema_one_of_cookie': expected validates the value against exactly one of the subschemas; founded valid in 2 schemas");
		
		
		tipoTest.add("ERRORE-COOKIE-INLINE-1");
		complexTypeTest.add("one_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreNonCorrettoInt);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(false);
		msgErroreAttesoTest_openapi4j.add("composed_schema_one_of_cookie_inline: More than 1 schema is valid. (code: 1023)\n"
				+ "From: composed_schema_one_of_cookie_inline.<oneOf>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/one_of/ABCDE/90 @cookie.composed_schema_one_of_cookie_inline] Instance failed to match exactly one schema (matched 2 out of 2)");
		msgErroreAttesoTest_swagger_response.add(null);
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInt+"' in cookie 'composed_schema_one_of_cookie_inline': expected validates the value against exactly one of the subschemas; founded valid in 2 schemas");
		
		
		
		tipoTest.add("ERRORE-HEADER-1");
		complexTypeTest.add("one_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreNonCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreCorrettoInt1);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(true);
		msgErroreAttesoTest_openapi4j.add("composed_schema_one_of_header: More than 1 schema is valid. (code: 1023)\n"
				+ "From: composed_schema_one_of_header.<oneOf>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/one_of/ABCDE/90 @header.composed_schema_one_of_header] Instance failed to match exactly one schema (matched 2 out of 2)");
		msgErroreAttesoTest_swagger_response.add("Validation failed.\n"
				+ "[ERROR][RESPONSE][] Instance failed to match exactly one schema (matched 2 out of 2)");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorretto1+"' in http header 'composed_schema_one_of_header': expected validates the value against exactly one of the subschemas; founded valid in 2 schemas");
		
		
		tipoTest.add("ERRORE-HEADER-INLINE-1");
		complexTypeTest.add("one_of");
		pathTest.add(valoreCorretto1);
		queryTest.add(valoreCorretto1);
		headerTest.add(valoreCorretto1);
		cookieTest.add(valoreCorretto1);
		pathInLineTest.add(valoreCorrettoInt1);
		queryInLineTest.add(valoreCorrettoInt1);
		headerInLineTest.add(valoreNonCorrettoInt);
		cookieInLineTest.add(valoreCorrettoInt1);
		erroreAttesoRichiestaTest.add(true);
		erroreAttesoRispostaTest.add(true);
		msgErroreAttesoTest_openapi4j.add("composed_schema_one_of_header_inline: More than 1 schema is valid. (code: 1023)\n"
				+ "From: composed_schema_one_of_header_inline.<oneOf>");
		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/one_of/ABCDE/90 @header.composed_schema_one_of_header_inline] Instance failed to match exactly one schema (matched 2 out of 2)");
		msgErroreAttesoTest_swagger_response.add("Validation failed.\n"
				+ "[ERROR][RESPONSE][] Instance failed to match exactly one schema (matched 2 out of 2)");
		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInt+"' in http header 'composed_schema_one_of_header_inline': expected validates the value against exactly one of the subschemas; founded valid in 2 schemas");
		
		
//		tipoTest.add("ERRORE-PATH-MULTI-TYPE-1");
//		complexTypeTest.add("one_of_multi_type");
//		pathTest.add(valoreCorretto1);
//		queryTest.add(valoreCorrettoInt1);
//		headerTest.add(valoreCorrettoInt1);
//		cookieTest.add(valoreCorretto1);
//		pathInLineTest.add(valoreCorrettoInt1);
//		queryInLineTest.add(valoreCorrettoInt1);
//		headerInLineTest.add(valoreCorrettoInt1);
//		cookieInLineTest.add(valoreCorrettoInt1);
//		erroreAttesoRichiestaTest.add(true);
//		erroreAttesoRispostaTest.add(true);
//		msgErroreAttesoTest_openapi4j.add("composed_schema_one_of_header_inline: More than 1 schema is valid. (code: 1023)\n"
//				+ "From: composed_schema_one_of_header_inline.<oneOf>");
//		msgErroreAttesoTest_swagger_request.add("Validation failed.\n"
//				+ "[ERROR][REQUEST][POST /documenti/composed-schema-parameters/one_of/ABCDE/90 @header.composed_schema_one_of_header_inline] Instance failed to match exactly one schema (matched 2 out of 2)");
//		msgErroreAttesoTest_swagger_response.add("Validation failed.\n"
//				+ "[ERROR][RESPONSE][] Instance failed to match exactly one schema (matched 2 out of 2)");
//		msgErroreAttesoTest_json_schema.add("Invalid value '"+valoreNonCorrettoInt+"' in http header 'composed_schema_one_of_header_inline': expected validates the value against exactly one of the subschemas; founded valid in 2 schemas");
		
		
		
				
		
				
		// Esecuzione test
		
		
		for (int i = 0; i < tipoTest.size(); i++) {
		
			String tipo = tipoTest.get(i);
			String complexType = complexTypeTest.get(i);
			
			String pathParam = pathTest.get(i);
			String headerParam = headerTest.get(i);
			String queryParam = queryTest.get(i);
			String cookieParam = cookieTest.get(i);
			
			String pathInLineParam = pathInLineTest.get(i);
			String headerInLineParam = headerInLineTest.get(i);
			String queryInLineParam = queryInLineTest.get(i);
			String cookieInLineParam = cookieInLineTest.get(i);
						
			boolean erroreAttesoRichiesta = erroreAttesoRichiestaTest.get(i);
			boolean erroreAttesoRisposta = erroreAttesoRispostaTest.get(i);
			
			String content = "Hello World";
			String msgErroreAtteso = null;
			switch (openAPILibrary) {
			case openapi4j:
				msgErroreAtteso = msgErroreAttesoTest_openapi4j.get(i);
				break;
			case swagger_request_validator:
				msgErroreAtteso = msgErroreAttesoTest_swagger_request.get(i);
				break;
			case json_schema:
				msgErroreAtteso = msgErroreAttesoTest_json_schema.get(i);
				break;
			default:
				break;
			}
			if(msgErroreAtteso==null) {
				msgErroreAtteso = "undefined";
			}

			
			String path = "/documenti/composed-schema-parameters/"+complexType+"/"+pathParam+"/"+pathInLineParam;
			HttpRequestMethod method = HttpRequestMethod.POST;
			String contentType = HttpConstants.CONTENT_TYPE_PLAIN;

			
			System.out.println("\tTest Richiesta ("+complexType+") ["+tipo+"] path:"+path+" ...");
			
			HttpBaseRequestEntity<?> request = new TextHttpRequestEntity();
			request.setUrl(path);	
			request.setMethod(method);
			((TextHttpRequestEntity)request).setContent(content);
			Map<String, List<String>> parametersTrasporto = new HashMap<>();
			TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, contentType);
			TransportUtils.addHeader(parametersTrasporto, "composed_schema_"+complexType+"_header", headerParam);
			TransportUtils.addHeader(parametersTrasporto, "composed_schema_"+complexType+"_header_inline", headerInLineParam);
			request.setHeaders(parametersTrasporto);
			request.setContentType(contentType);
			Map<String, List<String>> parametersQuery = new HashMap<>();
			TransportUtils.addHeader(parametersQuery, "composed_schema_"+complexType+"_query", queryParam);
			TransportUtils.addHeader(parametersQuery, "composed_schema_"+complexType+"_query_inline", queryInLineParam);
			request.setParameters(parametersQuery);
			List<Cookie> cookies = new ArrayList<Cookie>();
			Cookie c1 = new Cookie("composed_schema_"+complexType+"_cookie", cookieParam);
			Cookie c2 = new Cookie("composed_schema_"+complexType+"_cookie_inline", cookieInLineParam);
			cookies.add(c1);
			cookies.add(c2);
			request.setCookies(cookies);
								
			try {				
				apiValidatorOpenApi4j.validate(request);
				if(erroreAttesoRichiesta) {
					if("SKIP".equals(msgErroreAtteso)) {
						System.out.println("WARN Libreria non supporta ancora correttamente la validazione, si attendeva un errore");
					}
					else {
						throw new Exception("Atteso errore ("+tipo+") '"+msgErroreAtteso+"' non rilevato");
					}
				}
			} catch (ValidatorException e) {
				if(erroreAttesoRichiesta && e.getMessage()!=null && e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("Errore atteso: "+e.getMessage());
				}
				else {
					throw new Exception("Errore non atteso ("+tipo+"): "+e.getMessage() + "\n Errore Invece Atteso: " + msgErroreAtteso);
				}
			}
			
			System.out.println("\tTest Richiesta ("+complexType+") ["+tipo+"] path:"+path+" superato");
		
			
			System.out.println("\tTest Risposta ("+complexType+") ["+tipo+"] path:"+path+" ...");
			
			// Per la libreria swagger_request_validator il messaggio di errore cambia in caso
			// di richiesta o risposta
			if (openAPILibrary == OpenAPILibrary.swagger_request_validator) {
				msgErroreAtteso = msgErroreAttesoTest_swagger_response.get(i);
				if(msgErroreAtteso==null) {
					msgErroreAtteso = "undefined";
				}
			}
			
			HttpBaseResponseEntity<?> response = new TextHttpResponseEntity();
			((TextHttpResponseEntity)response).setContent(content);
			
			response.setStatus(200);		
			response.setMethod(method);
			response.setUrl(path);	
			Map<String, List<String>> responseHeaders = new HashMap<>();
			TransportUtils.setHeader(responseHeaders,HttpConstants.CONTENT_TYPE, contentType);
			TransportUtils.addHeader(responseHeaders, "composed_schema_"+complexType+"_header", headerParam);
			TransportUtils.addHeader(responseHeaders, "composed_schema_"+complexType+"_header_inline", headerInLineParam);
			response.setHeaders(responseHeaders);
			response.setContentType(contentType);
		
			try {				
				apiValidatorOpenApi4j.validate(response);
				if(erroreAttesoRisposta) {
					if("SKIP".equals(msgErroreAtteso)) {
						System.out.println("WARN Libreria non supporta ancora correttamente la validazione, si attendeva un errore");
					}
					else {
						throw new Exception("Atteso errore ("+tipo+") '"+msgErroreAtteso+"' non rilevato");
					}
				}
			} catch (ValidatorException e) {
				if(erroreAttesoRisposta && e.getMessage()!=null && e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("Errore atteso: "+e.getMessage());
				}
				else {
					throw new Exception("Errore non atteso ("+tipo+"): "+e.getMessage() + "\n Errore Invece Atteso: " + msgErroreAtteso);
				}
			}
			
			System.out.println("\tTest Risposta ("+complexType+") ["+tipo+"] path:"+path+" superato");
		}
			
			
		System.out.println("TEST Verifica ComposedSchemaParameters completato!");

	}
	
	
	
	private static void testAccept(OpenAPILibrary openAPILibrary, boolean mergeSpec)
			throws UtilsException, ProcessingException, URISyntaxException, Exception {
		System.out.println("#### Verifica Format Accept ####");
		
		URL url = TestOpenApi3Extended.class.getResource("/org/openspcoop2/utils/openapi/allegati.yaml");
					
		ApiSchema apiSchemaYaml = new ApiSchema("teamdigitale-openapi_definitions.yaml", 
				Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/service/schemi/standard/teamdigitale-openapi_definitions.yaml")), ApiSchemaType.YAML);
					
		IApiReader apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		ApiReaderConfig configOpenApi4j = new ApiReaderConfig();
		configOpenApi4j.setProcessInclude(false);
		apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), new File(url.toURI()), configOpenApi4j, apiSchemaYaml);
		
		Api apiOpenApi4j = apiReaderOpenApi4j.read();
								
		IApiValidator apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
		OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
		configO.setEmitLogError(logSystemOutError);
		configO.setOpenApiValidatorConfig(new OpenapiLibraryValidatorConfig());
		configO.getOpenApiValidatorConfig().setOpenApiLibrary(openAPILibrary);
		configO.getOpenApiValidatorConfig().setValidateAPISpec(true);
		configO.getOpenApiValidatorConfig().setMergeAPISpec(mergeSpec);
		apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi3Extended.class), apiOpenApi4j, configO);
		

		String path = "/documenti/eef036bf-48af-11ed-97b9-005056ae1884";
		HttpRequestMethod method = HttpRequestMethod.GET;
		String contentType = HttpConstants.CONTENT_TYPE_PLAIN;

		List<String> tipoTest = new ArrayList<String>();
		
		tipoTest.add("text/xml");
		tipoTest.add("application/json ");
		tipoTest.add(" application/json ");
		tipoTest.add("application/json;q=0.2");
		tipoTest.add("text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
		tipoTest.add("text/html, image/gif, image/jpeg,*;q=.2,*/*;q=.2");
		tipoTest.add("text/html, image/gif, image/jpeg, * ; q=.2, */* ; q=.2");
		tipoTest.add("text/html, image/gif, image/jpeg, *; q= .2, */*; q= .2");
		tipoTest.add("text/html, image/gif, image/jpeg,*;q= .2,*/*;q= .2");
		tipoTest.add("text/html, image/gif, image/jpeg, * ; q= .2, */* ; q= .2");
		tipoTest.add("text/html, image/gif, image/jpeg, *; q=0.5, */*; q=0.5");
		tipoTest.add("text/html, image/gif, image/jpeg,*;q=0.5,*/*;q=0.5");
		tipoTest.add("text/html, image/gif, image/jpeg, * ; q=0.5, */* ; q=0.5");
		tipoTest.add("text/html, image/gif, image/jpeg, *; q= 0.5, */*; q= 0.5");
		tipoTest.add("text/html, image/gif, image/jpeg,*;q= 0.5,*/*;q= 0.5");
		tipoTest.add("text/html, image/gif, image/jpeg, * ; q= 0.5, */* ; q= 0.2");
		
		for (int i = 0; i < tipoTest.size(); i++) {
			
			String tipo = tipoTest.get(i);
		
			
			System.out.println("\tTest Richiesta path:"+path+" ("+tipo+") ...");
				
			HttpBaseRequestEntity<?> request = new TextHttpRequestEntity();
			request.setUrl(path);	
			request.setMethod(method);
			Map<String, List<String>> parametersTrasporto = new HashMap<>();
			TransportUtils.addHeader(parametersTrasporto,HttpConstants.ACCEPT, tipo);
			request.setHeaders(parametersTrasporto);						
			try {				
				apiValidatorOpenApi4j.validate(request);
			} catch (ValidatorException e) {
				throw new Exception("Errore non atteso: "+e.getMessage(),e);
			}
			
			// verifico che sia ancora presente
			String valueHeaderAccept = TransportUtils.getFirstValue(request.getHeaders(),HttpConstants.ACCEPT);
			if(valueHeaderAccept==null || !valueHeaderAccept.equals(tipo)) {
				throw new Exception("Header Accept '"+valueHeaderAccept+"' diverso da quello atteso '"+"+valueHeaderAccept+"+"'");
			}
			
			System.out.println("\tTest Richiesta  path:"+path+" ("+tipo+") superato");
			
				
			System.out.println("\tTest Risposta path:"+path+" ("+tipo+") ...");
				
			HttpBaseResponseEntity<?> response = new TextHttpResponseEntity();
			((TextHttpResponseEntity)response).setContent("PROVA");
			response.setStatus(200);		
			response.setMethod(method);
			response.setUrl(path);	
			Map<String, List<String>> responseHeaders = new HashMap<>();
			TransportUtils.setHeader(responseHeaders,HttpConstants.CONTENT_TYPE, contentType);
			TransportUtils.addHeader(responseHeaders,HttpConstants.ACCEPT, tipo);
			response.setHeaders(responseHeaders);
			response.setContentType(contentType);
			
			try {				
				apiValidatorOpenApi4j.validate(response);
			} catch (ValidatorException e) {
				throw new Exception("Errore non atteso: "+e.getMessage());
			}
					
			// verifico che sia ancora presente
			valueHeaderAccept = TransportUtils.getFirstValue(response.getHeaders(),HttpConstants.ACCEPT);
			if(valueHeaderAccept==null || !valueHeaderAccept.equals(tipo)) {
				throw new Exception("Header Accept '"+valueHeaderAccept+"' diverso da quello atteso '"+"+valueHeaderAccept+"+"'");
			}
			
			System.out.println("\tTest Risposta path:"+path+" ("+tipo+") superato");
			
		}
			
			
		System.out.println("TEST Format Accept completato!");

	}
}
