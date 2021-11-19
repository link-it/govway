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
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.ValidatorException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;
import org.openspcoop2.utils.rest.entity.BinaryHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.DocumentHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.ElementHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseResponseEntity;
import org.openspcoop2.utils.rest.entity.TextHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.TextHttpResponseEntity;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test (Estende i test già effettuati in TestOpenApi3
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TestOpenApi4j {

	public static void main(String[] args) throws Exception {
		
		
		OpenAPILibrary openAPILibrary = OpenAPILibrary.openapi4j;
		if(args!=null && args.length>0) {
			openAPILibrary = OpenAPILibrary.valueOf(args[0]);
		}
		
		openAPILibrary = OpenAPILibrary.swagger_request_validator;
		
		// TODO: Renderlo parametrico anche su mergeSpec, che di default è a false.
		// per adesso non supportiamo schemi non mergiati con la swagger_request_validator
		boolean mergeSpec = true;
		
		
		// *** TEST per validazione json con wildcard nel subtype *** //
		testSubMediatypeWildcardJsonValidation(openAPILibrary, mergeSpec, true);
		testSubMediatypeWildcardJsonValidation(openAPILibrary, mergeSpec, false);
			
		if (openAPILibrary == OpenAPILibrary.swagger_request_validator)  {
			testBase64Validation(openAPILibrary, mergeSpec);
		} else {
			System.out.println("Skippo Test validazione file upload in base64 per libreria openapi4j");
		}
		
		// *** TEST per il Parser e validazione dello schema *** //
		
		if( openAPILibrary != OpenAPILibrary.swagger_request_validator ){
		
			System.out.println("Test Schema#1 (openapi.yaml) [Elementi aggiuntivi come 'allowEmptyValue'] ...");
			
			URL url = TestOpenApi4j.class.getResource("/org/openspcoop2/utils/openapi/openapi.yaml");
			
			IApiReader apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
			ApiReaderConfig configOpenApi4j = new ApiReaderConfig();
			configOpenApi4j.setProcessInclude(false);
			apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), new File(url.toURI()), configOpenApi4j);
			Api apiOpenApi4j = apiReaderOpenApi4j.read();
			
			IApiValidator apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
			OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
			configO.setOpenApi4JConfig(new OpenapiApi4jValidatorConfig());
			configO.getOpenApi4JConfig().setOpenApiLibrary(openAPILibrary);
			configO.getOpenApi4JConfig().setValidateAPISpec(true);
			configO.getOpenApi4JConfig().setMergeAPISpec(mergeSpec);
			apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), apiOpenApi4j, configO);
					
			System.out.println("Test Schema#1 (openapi.yaml) [Elementi aggiuntivi come 'allowEmptyValue'] ok");
			
			System.out.println("Test Schema#2 (allegati.yaml) [Discriminator non presente o non required Step 1/2] ...");
			
			url = TestOpenApi4j.class.getResource("/org/openspcoop2/utils/openapi/parser.yaml");
			
			apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
			apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), new File(url.toURI()), configOpenApi4j);
			apiOpenApi4j = apiReaderOpenApi4j.read();
			
			apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
			try {
				apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), apiOpenApi4j, configO);
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
			
			System.out.println("Test Schema#2 (allegati.yaml) [Discriminator non presente o non required Step 1/2] ok");
			
			System.out.println("Test Schema#2 (allegati.yaml) [Discriminator non presente o non required Step 2/2] ...");
			
			url = TestOpenApi4j.class.getResource("/org/openspcoop2/utils/openapi/parser2.yaml");
			
			apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
			apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), new File(url.toURI()), configOpenApi4j);
			apiOpenApi4j = apiReaderOpenApi4j.read();
			
			apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
			try {
				apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), apiOpenApi4j, configO);
				throw new Exception("Atteso errore");
			}catch(Exception e) {
				String msgErrore = "components.schemas.Pet4.properties.pet.discriminator: The discriminator 'pet_type' is required in this schema (code: 135)";
				if(!e.getMessage().contains(msgErrore)) {
					throw new Exception("Errore atteso '"+msgErrore+"' non rilevato",e);
				}
			}
			
			System.out.println("Test Schema#2 (allegati.yaml) [Discriminator non presente o non required Step 1/2] ok");
			
		} else {
			System.out.println("Skippo Test Schema (allegati.yaml) per libreria swagger-request-validator");
		}
			
		
		
		
		// *** TEST per la validazione delle richieste *** //
		
		URL url = TestOpenApi4j.class.getResource("/org/openspcoop2/utils/openapi/allegati.yaml");
		
		ApiSchema apiSchemaYaml = new ApiSchema("teamdigitale-openapi_definitions.yaml", 
				Utilities.getAsByteArray(TestOpenApi4j.class.getResourceAsStream("/org/openspcoop2/utils/service/schemi/standard/teamdigitale-openapi_definitions.yaml")), ApiSchemaType.YAML);
		
		String baseUri = "http://petstore.swagger.io/api";
		
		IApiReader apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		ApiReaderConfig configOpenApi4j = new ApiReaderConfig();
		configOpenApi4j.setProcessInclude(false);
		apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), new File(url.toURI()), configOpenApi4j, apiSchemaYaml);
		Api apiOpenApi4j = apiReaderOpenApi4j.read();
		
		IApiReader apiReaderNoOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		ApiReaderConfig configNoOpenApi4j = new ApiReaderConfig();
		configNoOpenApi4j.setProcessInclude(false);
		apiReaderNoOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), new File(url.toURI()), configNoOpenApi4j, apiSchemaYaml);
		Api apiNoOpenApi4j = apiReaderNoOpenApi4j.read();
		
		IApiValidator apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
		OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
		configO.setOpenApi4JConfig(new OpenapiApi4jValidatorConfig());
		configO.getOpenApi4JConfig().setOpenApiLibrary(openAPILibrary);
		configO.getOpenApi4JConfig().setValidateAPISpec(true);
		configO.getOpenApi4JConfig().setMergeAPISpec(mergeSpec);
		apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), apiOpenApi4j, configO);
		
		IApiValidator apiValidatorNoOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
		OpenapiApiValidatorConfig configNo = new OpenapiApiValidatorConfig();
		configNo.setOpenApi4JConfig(new OpenapiApi4jValidatorConfig());
		configNo.getOpenApi4JConfig().setOpenApiLibrary(OpenAPILibrary.json_schema);
		apiValidatorNoOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), apiNoOpenApi4j, configNo);
		
		byte [] pdf = Utilities.getAsByteArray(TestOpenApi4j.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test.pdf"));
		
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
		apiValidatorOpenApi4j.validate(httpEntity);	
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
			apiValidatorOpenApi4j.validate(httpEntity);
			throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
		} catch(ValidatorException e) {
			System.out.println("Test #2 Errore trovato: " + e.getMessage());
			String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
					"body.allegati.0.documento: Field 'uri' is required." :
					"- [ERROR][] [Path '/allegati/0/documento'] Object has missing required properties ([\"uri\"])";
			
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
			apiValidatorOpenApi4j.validate(httpEntity);
			throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
		} catch(ValidatorException e) {
			System.out.println("Test #3 Errore trovato: " + e.getMessage());
			String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
					"body.allegati.0.documento: Schema selection can't be made for discriminator 'tipoDocumento' with value 'riferimento-uriERRATA'."
					: "[ERROR][] [Path '/allegati/0/documento/tipoDocumento'] Instance value (\"riferimento-uriERRATA\") not found in enum (possible values: [\"inline\",\"riferimento-uri\"])";
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
			apiValidatorOpenApi4j.validate(httpEntity);
			throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
		} catch(ValidatorException e) {
			System.out.println("Test #4 Errore trovato: " + e.getMessage());
			String msgErroreAtteso =  openAPILibrary == OpenAPILibrary.openapi4j ? 
					"body.allegati.1.documento: Property name in content 'tipoDocumento' is not set." :
					"[ERROR][] [Path '/allegati/1/documento'] Object has missing required properties ([\"tipoDocumento\"])";			
			if(!e.getMessage().contains(msgErroreAtteso)) {
				throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
			}
			msgErroreAtteso =  openAPILibrary == OpenAPILibrary.openapi4j ? 
					"From: body.<allOf>.allegati.1.<items>.<#/components/schemas/AllegatoRiferimentoMixed>.<allOf>.documento.<discriminator>" :
					"* /allOf/1/properties/allegati/items/allOf/1: Discriminator field 'tipoDocumento' is required";
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
		apiValidatorOpenApi4j.validate(httpEntityDynamicPath);
			
		System.out.println("Test #5 completato\n\n");
		
		
		
		System.out.println("Test #6 (Risposta GET con parametro dinamico /documenti/XYZ)");
		testUrl5 = baseUri+"/documenti/"+UUID.randomUUID().toString();
		
		TextHttpRequestEntity httpEntityGET = new TextHttpRequestEntity();
		httpEntityGET.setMethod(HttpRequestMethod.GET);
		httpEntityGET.setUrl(testUrl5);	
		apiValidatorOpenApi4j.validate(httpEntityGET);	
		
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = "ContentType:"+ct+" ";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
				
				if(ct.contains("xml") && openAPILibrary == OpenAPILibrary.swagger_request_validator) {
					System.out.println("\t "+tipoTest+" validate response ...");
					System.out.println("\t Content-Type " + ct + ": funzionalità non supportata dalla libreria swagger-request-validator"); // swagger-request-validator-unsupported
					continue;
				}
						
				try {
					System.out.println("\t "+tipoTest+" validate response ...");
					apiValidator.validate(httpEntityResponse);	
					System.out.println("\t "+tipoTest+" validate response ok");
					
					if(openapi4j && ct.contains("Uncorrect")) {
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
		apiValidatorOpenApi4j.validate(httpEntity7);	
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
				apiValidatorOpenApi4j.validate(httpEntity8);
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
				String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
						"body.allegati.0.codiceOpzionaleNumerico: '"+valore+"' does not respect pattern '^\\d{6}$'." :
						"- [ERROR][] [Path '/allegati/0/codiceOpzionaleNumerico'] ECMA 262 regex \"^\\d{6}$\" does not match input string \""+StringEscapeUtils.unescapeJava(valore)+"";				
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t (Valore:"+valore+") ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'" +"\nTrovato invece: '"+e.getMessage()+"'");
				}
				msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ? 
						"From: body.<allOf>.allegati.0.<items>.<#/components/schemas/AllegatoRiferimentoMixed>.<allOf>.<#/components/schemas/Allegato>.codiceOpzionaleNumerico.<pattern>" :
						"* /allOf/1/properties/allegati/items/allOf/0";
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
				apiValidatorOpenApi4j.validate(httpEntity9);
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
				String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
						"body.allegati.0.codiceOpzionaleCodiceFiscaleOrCodiceEsterno: '"+valore+"' does not respect pattern '^[a-zA-Z]{6}[0-9]{2}[a-zA-Z0-9]{3}[a-zA-Z0-9]{5}$|^[A-Z0-9]{3}\\d{3}$'." :
						"- [ERROR][] [Path '/allegati/0/codiceOpzionaleCodiceFiscaleOrCodiceEsterno'] ECMA 262 regex \"^[a-zA-Z]{6}[0-9]{2}[a-zA-Z0-9]{3}[a-zA-Z0-9]{5}$|^[A-Z0-9]{3}\\d{3}$\" does not match input string \""+StringEscapeUtils.unescapeJava(valore);
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t (Valore:"+valore+") ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
				msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ? 
						"From: body.<allOf>.allegati.0.<items>.<#/components/schemas/AllegatoRiferimentoMixed>.<allOf>.<#/components/schemas/Allegato>.codiceOpzionaleCodiceFiscaleOrCodiceEsterno.<pattern>" :
						"* /allOf/1/properties/allegati/items/allOf/0:";
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
		apiValidatorOpenApi4j.validate(httpEntityTest10);	
		
		TextHttpResponseEntity httpEntityResponseTest10 = new TextHttpResponseEntity();
		httpEntityResponseTest10.setStatus(201);
		httpEntityResponseTest10.setMethod(HttpRequestMethod.POST);
		httpEntityResponseTest10.setUrl(testUrl10);	
		Map<String, List<String>> parametersTrasportoRispostaTest10 = new HashMap<>();
		TransportUtils.addHeader(parametersTrasportoRispostaTest10,"api_key", "aaa");
		httpEntityResponseTest10.setHeaders(parametersTrasportoRispostaTest10);
		
		System.out.println("\t Validazione senza content-type ...");
		apiValidatorOpenApi4j.validate(httpEntityResponseTest10);	
		System.out.println("\t Validazione senza content-type ok");
		
		List<String> contentTypeTest10List = new ArrayList<String>();
		contentTypeTest10List.add(HttpConstants.CONTENT_TYPE_PLAIN);
		contentTypeTest10List.add(HttpConstants.CONTENT_TYPE_TEXT_XML);
		contentTypeTest10List.add(HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807);
		contentTypeTest10List.add(HttpConstants.CONTENT_TYPE_JSON);
		contentTypeTest10List.add(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);
		for (String contentTypeTest10 : contentTypeTest10List) {
			
			for (int i = 0; i < 4; i++) {
				
				boolean addContenuto = (i==1 || i==3);
				String tipoTest = "senza";
				if(addContenuto) {
					tipoTest = "con";
				}
				
				boolean openapi4j = (i==0 || i==1);
				IApiValidator apiValidator = null;
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = "[openapi4j] "+ tipoTest;
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = "[json] "+ tipoTest;
				}

				
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
					apiValidator.validate(httpEntityResponseTest10);	
					esito = true;
				}
				catch(Exception e) {
					String msg = e.getMessage();
					if(openapi4j) {
						String atteso = openAPILibrary == OpenAPILibrary.openapi4j ?
								"Content type '"+contentTypeTest10+"' is not allowed for body content. (code: 203)" :
								"Content-Type '"+contentTypeTest10+"' (http response status '201') unsupported";	// swagger-request-validator-unsupported 
									
						if(!msg.contains(atteso)) {
							String checkErrore = "Validazione con content-type '"+contentTypeTest10+"' "+tipoTest+" contenuto terminato con un errore diverso da quello atteso: '"+msg+"'";
							System.out.println("\t "+checkErrore);
							throw new Exception(checkErrore);
						}
						else {
							System.out.println("\t Validazione con content-type '"+contentTypeTest10+"' "+tipoTest+" contenuto terminato con l'errore atteso: "+msg);
						}
					}
					else {
						String atteso = "Content-Type '"+contentTypeTest10+"' (http response status '201') unsupported";
						if(!msg.equals(atteso)) {
							String checkErrore = "Validazione con content-type '"+contentTypeTest10+"' "+tipoTest+" contenuto terminato con un errore diverso da quello atteso: '"+msg+"'";
							System.out.println("\t "+checkErrore);
							throw new Exception(checkErrore);
						}
						else {
							System.out.println("\t Validazione con content-type '"+contentTypeTest10+"' "+tipoTest+" contenuto terminato con l'errore atteso: "+msg);
						}
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
		apiValidatorOpenApi4j.validate(httpEntityTest11);	
		System.out.println("\t Validazione richiesta senza content-type ok");
		
		for (String contentTypeTest11 : contentTypeTest11List) {
			
			for (int i = 0; i < 2; i++) {
				boolean addContenuto = (i==1);
				String tipoTest = "senza";
				if(addContenuto) {
					tipoTest = "con";
				}
		
				boolean openapi4j = (i==0 || i==1);
				IApiValidator apiValidator = null;
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = "[openapi4j] "+ tipoTest;
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = "[json] "+ tipoTest;
				}
				
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
					apiValidator.validate(httpEntityTest11);	
					esito = true;
				}
				catch(Exception e) {
					String msg = e.getMessage();
					if(openapi4j) {
						// Per adesso openapi4j non sembra accorgersi dell'errore, neanche swagger-validator
						//String atteso = "Content type '"+contentTypeTest11+"' is not allowed for body content. (code: 203)";
						//if(!msg.contains(atteso)) {
						String atteso = "Content-Type '"+contentTypeTest11+"' unsupported";
						if (openAPILibrary == OpenAPILibrary.swagger_request_validator) {
							 
							if (httpEntityTest11.getContent() != null) {
								atteso = "[ERROR][REQUEST][POST http://petstore.swagger.io/api/documenti/norequestresponse/send] No request body is expected but one was found."; 
							} else {
								atteso = "Content-Type '"+contentTypeTest11+"' unsupported"; // swagger-request-validator-unsupported
							}
						}
						
						if(!msg.contains(atteso)) {
							String checkErrore = "Validazione richiesta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto terminato con un errore diverso da quello atteso: '"+msg+"'";
							System.out.println("\t "+checkErrore);
							throw new Exception(checkErrore);
						}
						else {
							System.out.println("\t Validazione richiesta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto terminato con l'errore atteso: "+msg);
						}
					}
					else {
						String atteso = "Content-Type '"+contentTypeTest11+"' unsupported";
						if(!msg.equals(atteso)) {
							String checkErrore = "Validazione richiesta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto terminato con un errore diverso da quello atteso: '"+msg+"'";
							System.out.println("\t "+checkErrore);
							throw new Exception(checkErrore);
						}
						else {
							System.out.println("\t Validazione richiesta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto terminato con l'errore atteso: "+msg);
						}
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
		apiValidatorOpenApi4j.validate(httpEntityResponseTest11);	
		System.out.println("\t Validazione risposta senza content-type ok");

		for (String contentTypeTest11 : contentTypeTest11List) {
			
			for (int i = 0; i < 2; i++) {
				boolean addContenuto = (i==1);
				String tipoTest = "senza";
				if(addContenuto) {
					tipoTest = "con";
				}
				
				boolean openapi4j = (i==0 || i==1);
				IApiValidator apiValidator = null;
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = "[openapi4j] "+ tipoTest;
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = "[json] "+ tipoTest;
				}
				
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
					apiValidator.validate(httpEntityResponseTest11);	
					esito = true;
				}
				catch(Exception e) {
					String msg = e.getMessage();
					if(openapi4j) {
						String atteso = "Content type '"+contentTypeTest11+"' is not allowed for body content. (code: 203)";
						//String atteso = "Content-Type '"+contentTypeTest11+"' unsupported";
						if (openAPILibrary == OpenAPILibrary.swagger_request_validator) {
							// Quando il content-type non è specificato nello schema, la swagger-request-validator
							// non effettua validazione per cui ci si rifà al validatore custom
							// swagger-request-validator-unsupported
							atteso = "Content-Type '"+contentTypeTest11+"' (http response status '201') unsupported";
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
					else {
						String atteso = "Content-Type '"+contentTypeTest11+"' (http response status '201') unsupported";
						if(!msg.equals(atteso)) {
							String checkErrore = "Validazione risposta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto terminato con un errore diverso da quello atteso: '"+msg+"'";
							System.out.println("\t "+checkErrore);
							throw new Exception(checkErrore);
						}
						else {
							System.out.println("\t Validazione risposta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto terminato con l'errore atteso: "+msg);
						}
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
			
			for (int i = 0; i < 6; i++) {
				
				boolean openapi4j = (i==3 || i==4 || i==5);
				IApiValidator apiValidator = null;
				
				String tipoTest = tipoTestPrefix;
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
				
				TextHttpRequestEntity httpEntity12 = null;
				boolean required = false;
				if(i==0 || i==3) {
					httpEntity12 = httpEntity12_default;
					tipoTest = tipoTest + " (required:default)";
				}
				else if(i==1 || i==4) {
					httpEntity12 = httpEntity12_optional;
					tipoTest = tipoTest + " (required:false)";
				}
				else if(i==2 || i==5) {
					httpEntity12 = httpEntity12_required;
					required = true;
					tipoTest = tipoTest + " (required:true)";
				}
				
				
				System.out.println("\t Validazione richiesta senza contenuto "+tipoTest+"  ...");
				boolean esito = false;
				try {
					apiValidator.validate(httpEntity12);
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
					
					if(openapi4j) {
						
						String atteso = "Body is required but none provided. (code: 200)";
						if (openAPILibrary == OpenAPILibrary.swagger_request_validator) {
							if (httpEntity12.getContentType() == null) {
								atteso = "Required Content-Type is missing";
							} else {
								atteso = "A request body is required but none found.";
							}
						}
						
						
						if(!required && contentTypeTest12!=null) {
							atteso = openAPILibrary == OpenAPILibrary.openapi4j ?
									"Content-Type '"+contentTypeTest12+"' unsupported" :
									"Request Content-Type header '[text/plain]' does not match any allowed types. Must be one of: [application/json].";
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
					else {
						String atteso = "Required body undefined";
						if(!required && contentTypeTest12!=null) {
							atteso = "Content-Type '"+contentTypeTest12+"' unsupported";
							if(HttpConstants.CONTENT_TYPE_JSON.equals(contentTypeTest12) && !openapi4j) {
								atteso = "Content undefined";
							}
						}
						if(!msg.equals(atteso)) {
							String checkErrore = "Validazione richiesta "+tipoTest+" senza contenuto terminato con un errore diverso da quello atteso: '"+msg+"'";
							System.out.println("\t "+checkErrore);
							throw new Exception(checkErrore);
						}
						else {
							System.out.println("\t Validazione richiesta "+tipoTest+" senza contenuto terminato con l'errore atteso: "+msg);
						}
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
			
			for (int i = 0; i < 2; i++) {
				
				boolean openapi4j = (i==0);
				IApiValidator apiValidator = null;
				String tipoTest = tipoTestPrefix;
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
				
				System.out.println("\t Validazione risposta senza contenuto "+tipoTest+"  " + httpEntityResponseTest12.getUrl() + " ...");
				boolean esito = false;
				try {
					apiValidator.validate(httpEntityResponseTest12);	
					esito = true;
				}
				catch(Exception e) {
					//e.printStackTrace(System.out);
					String msg = e.getMessage();
					if(openapi4j) {
						String atteso = openAPILibrary == OpenAPILibrary.openapi4j ?
								"Content type 'null' is not allowed for body content. (code: 203)" :
								"Required Content-Type is missing";
									
						if(!msg.contains(atteso)) {
							String checkErrore = "Validazione "+tipoTest+" senza contenuto terminato con un errore diverso da quello atteso: '"+msg+"'";
							System.out.println("\t "+checkErrore);
							throw new Exception(checkErrore);
						}
						else {
							System.out.println("\t Validazione risposta "+tipoTest+" senza contenuto terminato con l'errore atteso: "+msg);
						}
					}
					else {
						String atteso = "Required body undefined";
						if(!msg.equals(atteso)) {
							String checkErrore = "Validazione risposta "+tipoTest+" senza contenuto terminato con un errore diverso da quello atteso: '"+msg+"'";
							System.out.println("\t "+checkErrore);
							throw new Exception(checkErrore);
						}
						else {
							System.out.println("\t Validazione risposta "+tipoTest+" senza contenuto terminato con l'errore atteso: "+msg);
						}
					}
				}
				if(esito) {
					System.out.println("\t Validazione risposta "+tipoTest+" senza contenuto: atteso errore");
					throw new Exception("Validazione risposta "+tipoTest+" senza contenuto: atteso errore");
				}
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = esito ? "[body con valore ok '"+valore+"']" : "[body con valore errato '"+valore+"']";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntity13);
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
					if(openapi4j) {
						String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
								"body.data: Value '"+valore+"' does not match format 'date'. (code: 1007)" :
								"[ERROR][REQUEST][POST http://petstore.swagger.io/api/documenti/datetest/2020-07-21 @body] [Path '/data'] String \""+valore+"\" is invalid against requested date format(s) yyyy-MM-dd";
						
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
					else {
						String msgErroreAtteso = "1034 $.data: "+valore+" is an invalid date";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = esito ? "[body response con valore ok '"+valore+"']" : "[body response con valore errato '"+valore+"']";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntityResponseTest13);
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
					if(openapi4j) {
						String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
								"body.data: Value '"+valore+"' does not match format 'date'. (code: 1007)" :
								"[ERROR][RESPONSE][] [Path '/data'] String \""+valore+"\" is invalid against requested date format(s) yyyy-MM-dd";
						
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
					else {
						String msgErroreAtteso = "1034 $.data: "+valore+" is an invalid date";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = esito ? "[header con valore ok '"+valore+"']" : "[header con valore errato '"+valore+"']";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntity13);
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
					if(openapi4j) {
						String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
								"data_documento_header: Value '"+valore+"' does not match format 'date'. (code: 1007)" :
								"[ERROR][REQUEST][POST http://petstore.swagger.io/api/documenti/datetest/2020-07-21 @header.data_documento_header] String \""+valore+"\" is invalid against requested date format(s)";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
					else {
						String msgErroreAtteso = "Invalid value '"+valore+"' in http header 'data_documento_header' (expected type 'date'): Found date '"+valore+"' has wrong format (see RFC 3339, section 5.6): Uncorrect format";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = esito ? "[header response con valore ok '"+valore+"']" : "[header response con valore errato '"+valore+"']";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntityResponseTest13);
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
					if(openapi4j) {
						String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
								"data_documento_risposta_header: Value '"+valore+"' does not match format 'date'. (code: 1007)" : 
								"[ERROR][RESPONSE][] String \""+valore+"\" is invalid against requested date format(s) yyyy-MM-dd"; 
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
					else {
						String msgErroreAtteso = "Invalid value '"+valore+"' in http header 'data_documento_risposta_header' (expected type 'date'): Found date '"+valore+"' has wrong format (see RFC 3339, section 5.6): Uncorrect format";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = esito ? "[query parameter con valore ok '"+valore+"']" : "[query parameter con valore errato '"+valore+"']";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntity13);
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
					if(openapi4j) {
						String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j?
								"data_documento_query: Value '"+valore+"' does not match format 'date'. (code: 1007)" :
								"[ERROR][REQUEST][POST http://petstore.swagger.io/api/documenti/datetest/2020-07-21 @query.data_documento_query] String \""+valore+"\" is invalid against requested date format(s) "; 
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
					else {
						String msgErroreAtteso = "Invalid value '"+valore+"' in query parameter 'data_documento_query' (expected type 'date'): Found date '"+valore+"' has wrong format (see RFC 3339, section 5.6): Uncorrect format";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = esito ? "[path parameter con valore ok '"+valore+"']" : "[path parameter con valore errato '"+valore+"']";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntity13);
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
					if(openapi4j) {
						String msgErroreAtteso = "Invalid value '"+valore+"' in dynamic path 'data_documento_path' (expected type 'date'): Found date '"+valore+"' has wrong format (see RFC 3339, section 5.6): Uncorrect format";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
					else {
						String msgErroreAtteso = "Invalid value '"+valore+"' in dynamic path 'data_documento_path' (expected type 'date'): Found date '"+valore+"' has wrong format (see RFC 3339, section 5.6): Uncorrect format";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = esito ? "[body con valore ok '"+valore+"']" : "[body con valore errato '"+valore+"']";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntity14);
					if(esito) {
						System.out.println("\t "+tipoTest+" validate ok");
					}
					else {
						if(i<3 && !openapi4j) {
							System.out.println("\t "+tipoTest+" validate, la validazione JSON non rileva l'accezione!!!!");
							continue;
						} else if( openAPILibrary == OpenAPILibrary.swagger_request_validator && swagger_validator_fallimenti_datetime.contains(valore)) {
							System.out.println("\t "+tipoTest+" validate, lo swagger-request-validator non rileva l'accezione!!!!");
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
					if(openapi4j) {
						String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ? 
								"body.data: Value '"+valore+"' does not match format 'date-time'. (code: 1007)" :
								"[ERROR][REQUEST][POST http://petstore.swagger.io/api/documenti/datetimetest/2020-07-21T17:32:28Z @body] [Path '/data'] String \""+valore+"\" is invalid against requested "; 
						
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
					else {
						String msgErroreAtteso = "1034 $.data: "+valore+" is an invalid date";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = esito ? "[body response con valore ok '"+valore+"']" : "[body response con valore errato '"+valore+"']";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntityResponseTest14);
					if(esito) {
						System.out.println("\t "+tipoTest+" validate ok");
					}
					else {
						if(i<3 && !openapi4j) {
							System.out.println("\t "+tipoTest+" validate, la validazione JSON non rileva l'accezione!!!!");
							continue;
						} else if(openAPILibrary == OpenAPILibrary.swagger_request_validator && swagger_validator_fallimenti_datetime.contains(valore)) {
							System.out.println("\t "+tipoTest+" validate, lo swagger-request-validator non rileva l'accezione!!!!");
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
					if(openapi4j) {
						String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
								"body.data: Value '"+valore+"' does not match format 'date-time'. (code: 1007)" :
								"[ERROR][RESPONSE][] [Path '/data'] String \""+valore+"\" is invalid against requested date format(s) [yyyy-MM-dd'T'HH:mm:ssZ, yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,12}Z]"; 
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
					else {
						String msgErroreAtteso = "1034 $.data: "+valore+" is an invalid date";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = esito ? "[header con valore ok '"+valore+"']" : "[header con valore errato '"+valore+"']";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntity14);
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
					if(openapi4j) {
						if (openAPILibrary == OpenAPILibrary.swagger_request_validator && swagger_validator_fallimenti_datetime.contains(valore)) {
							System.out.println("\t "+tipoTest+" validate, lo swagger-request-validator non rileva l'accezione!!!!");
							continue;
						}
						String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ? 
								"datetime_documento_header: Value '"+valore+"' does not match format 'date-time'. (code: 1007)" :
								"[ERROR][REQUEST][POST http://petstore.swagger.io/api/documenti/datetimetest/2020-07-21T17:32:28Z @header.datetime_documento_header] String \""+valore+"\" is invalid against requested date format(s) [yyyy-MM-dd'T'HH:mm:ssZ, yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,12}Z]";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
					else {
						String msgErroreAtteso = "Invalid value '"+valore+"' in http header 'datetime_documento_header' (expected type 'date-time'): Found dateTime '"+valore+"' has wrong format (see RFC 3339, section 5.6): ";
						if(!valore.contains("T")) {
							msgErroreAtteso = msgErroreAtteso+ "Expected 'T' separator";
						}
						else {
							msgErroreAtteso = msgErroreAtteso+ "Uncorrect format";
						}
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = esito ? "[header response con valore ok '"+valore+"']" : "[header response con valore errato '"+valore+"']";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntityResponseTest14);
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
					if(openapi4j) {
						if (openAPILibrary == OpenAPILibrary.swagger_request_validator && swagger_validator_fallimenti_datetime.contains(valore)) {
							System.out.println("\t "+tipoTest+" validate, lo swagger-request-validator non rileva l'accezione!!!!");
							continue;
						}
						String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
								"datetime_documento_risposta_header: Value '"+valore+"' does not match format 'date-time'. (code: 1007)" :
								"[ERROR][RESPONSE][] String \""+valore+"\" is invalid against requested date format(s) [yyyy-MM-dd'T'HH:mm:ssZ, yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,12}Z]";
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
					else {
						String msgErroreAtteso = "Invalid value '"+valore+"' in http header 'datetime_documento_risposta_header' (expected type 'date-time'): Found dateTime '"+valore+"' has wrong format (see RFC 3339, section 5.6): ";
						if(!valore.contains("T")) {
							msgErroreAtteso = msgErroreAtteso+ "Expected 'T' separator";
						}
						else {
							msgErroreAtteso = msgErroreAtteso+ "Uncorrect format";
						}
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = esito ? "[query parameter con valore ok '"+valore+"']" : "[query parameter con valore errato '"+valore+"']";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntity14);
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
					if(openapi4j) {
						if (openAPILibrary == OpenAPILibrary.swagger_request_validator && swagger_validator_fallimenti_datetime.contains(valore)) {
							System.out.println("\t "+tipoTest+" validate, lo swagger-request-validator non rileva l'accezione!!!!");
							continue;
						}
						String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
								"datetime_documento_query: Value '"+valore+"' does not match format 'date-time'. (code: 1007)" :
								"[ERROR][REQUEST][POST http://petstore.swagger.io/api/documenti/datetimetest/2020-07-21T17:32:28Z @query.datetime_documento_query] String \""+valore+"\" is invalid against r";								
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
					else {
						String msgErroreAtteso = "Invalid value '"+valore+"' in query parameter 'datetime_documento_query' (expected type 'date-time'): Found dateTime '"+valore+"' has wrong format (see RFC 3339, section 5.6): ";
						if(!valore.contains("T")) {
							msgErroreAtteso = msgErroreAtteso+ "Expected 'T' separator";
						}
						else {
							msgErroreAtteso = msgErroreAtteso+ "Uncorrect format";
						}
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = esito ? "[path parameter con valore ok '"+valore+"']" : "[path parameter con valore errato '"+valore+"']";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntity14);
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
					if(openapi4j) {
						String msgErroreAtteso = "Invalid value '"+valore+"' in dynamic path 'datetime_documento_path' (expected type 'date-time'): Found dateTime '"+valore+"' has wrong format (see RFC 3339, section 5.6): ";
						if(!valore.contains("T")) {
							msgErroreAtteso = msgErroreAtteso+ "Expected 'T' separator";
						}
						else {
							msgErroreAtteso = msgErroreAtteso+ "Uncorrect format";
						}
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
					else {
						String msgErroreAtteso = "Invalid value '"+valore+"' in dynamic path 'datetime_documento_path' (expected type 'date-time'): Found dateTime '"+valore+"' has wrong format (see RFC 3339, section 5.6): ";
						if(!valore.contains("T")) {
							msgErroreAtteso = msgErroreAtteso+ "Expected 'T' separator";
						}
						else {
							msgErroreAtteso = msgErroreAtteso+ "Uncorrect format";
						}
						if(!e.getMessage().contains(msgErroreAtteso)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
						}
					}
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
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
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntityResponseTest15);
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
					if(openapi4j) {
						String msgErroreAtteso = "Content type '"+ct+"' is not allowed for body content. (code: 203)";
						String msgErroreAtteso2 = "body: Field 'esito' is required. (code: 1026)";
						String msgErroreAttesoSwagger = "[ERROR][RESPONSE][] Response Content-Type header '"+ct+"' does not match any allowed types. Must be one of: [application/";
						String msgErroreAttesoSwagger2 = "[ERROR][RESPONSE][] Object has missing required properties ([\"esito\"])"; 
						
						if (ct==null) {
							msgErroreAttesoSwagger="Required Content-Type is missing";
						}

						if(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807.equals(ct) && code.intValue() != 200) {
							msgErroreAtteso2 = "body: Field 'type' is required. (code: 1026)";
							msgErroreAttesoSwagger2 = "[ERROR][RESPONSE][] Object has missing required properties ([\"type\"])"; 
						}					
						
						if(!e.getMessage().contains(msgErroreAtteso) && 
								!e.getMessage().contains(msgErroreAtteso2) && 
								!e.getMessage().contains(msgErroreAttesoSwagger) && 
								!e.getMessage().contains(msgErroreAttesoSwagger2)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"' o '"+msgErroreAtteso2+"' o " + "'"+msgErroreAttesoSwagger+"'" );
						}
					}
					else {
						if(ct==null) {
							String msgErroreAtteso = "Required body undefined";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								System.out.println("\t "+tipoTest+" ERRORE!");
								throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
							}
						}
						else {
							String msgErroreAtteso = "Content-Type '"+ct+"' (http response status '"+code+"') unsupported";
							String msgErroreAtteso2 = "1028 $.esito: is missing but it is required";
							if(code.intValue() != 200) {
								msgErroreAtteso2 = "1028 $.type: is missing but it is required";
							}
							if(!e.getMessage().contains(msgErroreAtteso) && !e.getMessage().contains(msgErroreAtteso2)) {
								System.out.println("\t "+tipoTest+" ERRORE!");
								throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"' o '"+msgErroreAtteso2+"'");
							}
						}
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = "Url '"+urltest+"'";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntityResponseTest16);
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
			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
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
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntityResponseTest17);
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
					if(openapi4j) {
						String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ? 
								"body: Field 'type' is required. (code: 1026)" :
								"* /allOf/0/anyOf/0: Object has missing required properties ([\"type\"])";
						
						String msgErroreAtteso2 = openAPILibrary == OpenAPILibrary.openapi4j ? 
								"body: Field 'title' is required. (code: 1026)" :
								"* /allOf/0/anyOf/1: Object has missing required properties ([\"title\"])";
						
						String msgErroreAtteso3 = openAPILibrary == OpenAPILibrary.openapi4j ? 
								"body: Field 'status' is required. (code: 1026)" :
								"* /allOf/0/anyOf/2: Object has missing required properties ([\"status\"])";
						
						if(!e.getMessage().contains(msgErroreAtteso) || !e.getMessage().contains(msgErroreAtteso2) || !e.getMessage().contains(msgErroreAtteso3)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"' o '"+msgErroreAtteso2+"'");
						}
					}
					else {
						String msgErroreAtteso = "1028 $.type: is missing but it is required";
						String msgErroreAtteso2 = "1028 $.title: is missing but it is required";
						String msgErroreAtteso3 = "1028 $.status: is missing but it is required";
						if(!e.getMessage().contains(msgErroreAtteso) || !e.getMessage().contains(msgErroreAtteso2) || !e.getMessage().contains(msgErroreAtteso3)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"' o '"+msgErroreAtteso2+"'");
						}
					}
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
		
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = "Code:"+code+" ";
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
						
				TextHttpRequestEntity httpEntityGET_test18 = new TextHttpRequestEntity();
				httpEntityGET_test18.setMethod(HttpRequestMethod.GET);
				httpEntityGET_test18.setUrl(testUrl18);	
				
				try {
					System.out.println("\t "+tipoTest+" validate request ...");
					apiValidator.validate(httpEntityGET_test18);	
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
						apiValidator.validate(httpEntityResponse_test18);	
						System.out.println("\t "+tipoTest+" validate response ok");
						
						if(openapi4j && (ct.contains("Uncorrect") || !ct.contains("json"))) {
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
							erroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
									"body: Type expected 'object', found 'string'. (code: 1027)" :
									"[ERROR] Unable to parse JSON";
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
		// TEST_NUMERO = 2;
		
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
	
				
				for (int j = 0; j < 2; j++) {
					
					boolean openapi4j = (j==0);
					IApiValidator apiValidator = null;
					String tipoTest = testYaml+ (esito ? "[stato con valore (type:"+valore.getClass().getName()+") '"+valore+"']" : "[stato con valore errato (type:"+valore.getClass().getName()+") '"+valore+"']");
					if(openapi4j) {
						apiValidator = apiValidatorOpenApi4j;
						tipoTest = tipoTest+"[openapi4j]";
					}
					else {
						apiValidator = apiValidatorNoOpenApi4j;
						tipoTest = tipoTest+"[json]";
					}
				
					try {
						System.out.println("\t "+tipoTest+" validate ...");
						apiValidator.validate(httpEntity19);
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
						if(openapi4j) {
							if(valore instanceof Integer) {
								String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
									"Type expected 'string', found 'integer'. (code: 1027)" :
									"[ERROR][REQUEST][POST http://petstore.swagger.io/api/test-enum-no-yes @body] [Path '/stato1'] Instance value ("+valore+") not found in enum (possible values: [\"SI\",\"NO\""; 
								if(!e.getMessage().contains(msgErroreAtteso)) {
									System.out.println("\t "+tipoTest+" ERRORE!");
									throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
								}
								msgErroreAtteso =  openAPILibrary == OpenAPILibrary.openapi4j ?
										"Value '"+valore+"' is not defined in the schema. (code: 1006)" :
										"[ERROR][REQUEST][POST http://petstore.swagger.io/api/test-enum-no-yes @body] [Path '/stato1'] Instance value ("+valore+") not found in enum (possible values: [\"SI\",\"NO\",\"YES\",\"S\",\"N\",\"Y\",\"";
								if(!e.getMessage().contains(msgErroreAtteso)) {
									System.out.println("\t "+tipoTest+" ERRORE!");
									throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
								}
							}
							else {
								String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
										"body.stato1: Value '"+valore.toString().toUpperCase()+"' is not defined in the schema. (code: 1006)" :
										"[ERROR][REQUEST][POST http://petstore.swagger.io/api/test-enum-no-yes @body] [Path '/stato1'] Instance value (\""+valore.toString().toUpperCase()+"\") not found in enum (possible values: [\"SI\",\"NO\",\"YES\",\"S\",\"N\""; 
								
								if(!e.getMessage().contains(msgErroreAtteso)) {
									System.out.println("\t "+tipoTest+" ERRORE!");
									throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
								}
								msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
										"body.stato2: Value '"+valore.toString().toLowerCase()+"' is not defined in the schema. (code: 1006)" :
										"[ERROR][REQUEST][POST http://petstore.swagger.io/api/test-enum-no-yes @body] [Path '/stato1'] Instance value (\""+valore.toString().toUpperCase()+"\") not found in enum (possible values: [\"SI\",\"NO\",\"YES\",\"S\",\"N\"";
								if(!e.getMessage().contains(msgErroreAtteso)) {
									System.out.println("\t "+tipoTest+" ERRORE!");
									throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
								}
							}
						}
						else {
							if(valore instanceof Integer) {
								String tipoJava = valore instanceof Integer ? "integer" : "boolean";
								String msgErroreAtteso = "1029 $.stato2: "+tipoJava+" found, string expected";
								if(!e.getMessage().contains(msgErroreAtteso)) {
									System.out.println("\t "+tipoTest+" ERRORE!");
									throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
								}
								msgErroreAtteso = "1029 $.stato1: "+tipoJava+" found, string expected";
								if(!e.getMessage().contains(msgErroreAtteso)) {
									System.out.println("\t "+tipoTest+" ERRORE!");
									throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
								}
							}
							String msgErroreAtteso = "does not have a value in the enumeration [si, no, yes, s, n, y, 0, 1, on, off]";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								System.out.println("\t "+tipoTest+" ERRORE!");
								throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
							}
						}
					}
					
					try {
						System.out.println("\t "+tipoTest+" validate response ...");
						apiValidator.validate(httpEntityResponse_test19);	
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
						if(openapi4j) {
							if(valore instanceof Integer) {
								String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
										"Type expected 'string', found 'integer'. (code: 1027)" :
										"[ERROR][RESPONSE][] [Path '/stato1'] Instance value ("+valore+") not found in enum (possible values: [\"SI\",\"NO\",\"YES\",\"S\",\"N\",\"Y\",\"0\",\"1\",\"ON\",\"OFF\"])";								
								if(!e.getMessage().contains(msgErroreAtteso)) {
									System.out.println("\t "+tipoTest+" ERRORE!");
									throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
								}
								msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
										"Value '"+valore+"' is not defined in the schema. (code: 1006)" :
										"[ERROR][RESPONSE][] [Path '/stato1'] Instance value ("+valore+") not found in enum (possible values: [\"SI\",\"NO\",\"YES\",\"S\",\"N\",\"Y\",\"0\",\"1\",\"ON\",\"OFF\"])";
								if(!e.getMessage().contains(msgErroreAtteso)) {
									System.out.println("\t "+tipoTest+" ERRORE!");
									throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
								}
							}
							else {
								String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
										"body.stato1: Value '"+valore.toString().toUpperCase()+"' is not defined in the schema. (code: 1006)" :
										"[ERROR][RESPONSE][] [Path '/stato1'] Instance value (\""+valore.toString().toUpperCase()+"\") not found in enum (possible values: [\"SI\",\"NO\",\"YES\",\"S\",\"N\",\"Y\",\"0\",\"1\",\"ON\",\"OFF\"])"; 
								
								if(!e.getMessage().contains(msgErroreAtteso)) {
									System.out.println("\t "+tipoTest+" ERRORE!");
									throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
								}
								
								msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ? 
										"body.stato2: Value '"+valore.toString().toLowerCase()+"' is not defined in the schema. (code: 1006)" :
											"[ERROR][RESPONSE][] [Path '/stato1'] Instance value (\""+valore.toString().toUpperCase()+"\") not found in enum (possible values: [\"SI\",\"NO\",\"YES\",\"S\",\"N\",\"Y\",\"0\",\"1\",\"ON\",\"OFF\"])";

								if(!e.getMessage().contains(msgErroreAtteso)) {
									System.out.println("\t "+tipoTest+" ERRORE!");
									throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
								}
							}
						}
						else {
							if(valore instanceof Integer) {
								String tipoJava = valore instanceof Integer ? "integer" : "boolean";
								String msgErroreAtteso = "1029 $.stato2: "+tipoJava+" found, string expected";
								if(!e.getMessage().contains(msgErroreAtteso)) {
									System.out.println("\t "+tipoTest+" ERRORE!");
									throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
								}
								msgErroreAtteso = "1029 $.stato1: "+tipoJava+" found, string expected";
								if(!e.getMessage().contains(msgErroreAtteso)) {
									System.out.println("\t "+tipoTest+" ERRORE!");
									throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
								}
							}
							String msgErroreAtteso = "does not have a value in the enumeration [si, no, yes, s, n, y, 0, 1, on, off]";
							if(!e.getMessage().contains(msgErroreAtteso)) {
								System.out.println("\t "+tipoTest+" ERRORE!");
								throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
							}
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

			
			for (int j = 0; j < 2; j++) {
				
				boolean openapi4j = (j==0);
				IApiValidator apiValidator = null;
				String tipoTest = testYaml+ (esito ? "["+tipologia+" [/"+contextTestPath+"] MessaggioConforme: '"+valore+"']" : "["+tipologia+" [/"+contextTestPath+"] MessaggioNonConforme: '"+valore+"']");
				if(openapi4j) {
					apiValidator = apiValidatorOpenApi4j;
					tipoTest = tipoTest+"[openapi4j]";
				}
				else {
					apiValidator = apiValidatorNoOpenApi4j;
					tipoTest = tipoTest+"[json]";
				}
			
				
				try {
					System.out.println("\t "+tipoTest+" validate ...");
					apiValidator.validate(httpEntity20);
					if(esito) {
						System.out.println("\t "+tipoTest+" validate ok");
					}
					else {
						System.out.println("\t "+tipoTest+" ERRORE!");
						throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
					}
				} catch(ValidatorException e) {
					checkErrorTest20(esito, tipoTest, e, tipologia, openapi4j, openAPILibrary);
				}
				
				try {
					System.out.println("\t "+tipoTest+" validate response ...");
					apiValidator.validate(httpEntityResponse_test20);	
					System.out.println("\t "+tipoTest+" validate response ok");
				} catch(ValidatorException e) {
					checkErrorTest20(esito, tipoTest, e, tipologia, openapi4j, openAPILibrary);
				}
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
			
			if (openAPILibrary == OpenAPILibrary.swagger_request_validator && swagger_validator_fallimenti_allof.contains(k)) {
				System.out.println("Discriminator con allOf non supportato da swagger-request-validator!");
				continue;
			}
			
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
				
				int numeroTest = 2; // con 2 viene verificato anche libreria non openapi4j.
				numeroTest = 1; // la libreria json schema normale non supporta il discriminator e si ottiene un errore simile al seguente se si abilita: 1022 $: should be valid to one and only one of the schemas 
				for (int j = 0; j < numeroTest; j++) {
					
					boolean openapi4j = (j==0);
					IApiValidator apiValidator = null;
					String tipoTest = testPet+" ";
					if(openapi4j) {
						apiValidator = apiValidatorOpenApi4j;
						tipoTest = tipoTest+"[openapi4j]";
					}
					else {
						apiValidator = apiValidatorNoOpenApi4j;
						tipoTest = tipoTest+"[json]";
					}
					tipoTest = tipoTest + "(url:"+testUrl21+") contenuto("+contenuto+")";
				
					try {
						System.out.println("\t "+tipoTest+" validate ...");
						apiValidator.validate(httpEntity21);
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
						apiValidator.validate(httpEntityResponse_test21);	
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
		}
		
		System.out.println("Test #21 Discriminator completato\n\n");
	}

	private static void testBase64Validation(OpenAPILibrary openAPILibrary, boolean mergeSpec) throws Exception {
		
		System.out.println("TEST #S-2 per validazione file in base64");
		
		URL url = TestOpenApi4j.class.getResource("/org/openspcoop2/utils/openapi/allegati.yaml");
		ApiSchema apiSchemaYaml = new ApiSchema("teamdigitale-openapi_definitions.yaml", 
				Utilities.getAsByteArray(TestOpenApi4j.class.getResourceAsStream("/org/openspcoop2/utils/service/schemi/standard/teamdigitale-openapi_definitions.yaml")), ApiSchemaType.YAML);
					
		IApiReader apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		ApiReaderConfig configOpenApi4j = new ApiReaderConfig();
		configOpenApi4j.setProcessInclude(false);
		
		apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), new File(url.toURI()), configOpenApi4j, apiSchemaYaml);
		Api apiOpenApi4j = apiReaderOpenApi4j.read();
								
		IApiValidator validator = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
		OpenapiApiValidatorConfig config = new OpenapiApiValidatorConfig();
		config.setOpenApi4JConfig(new OpenapiApi4jValidatorConfig());
		config.getOpenApi4JConfig().setOpenApiLibrary(openAPILibrary);
		config.getOpenApi4JConfig().setValidateAPISpec(true);
		config.getOpenApi4JConfig().setMergeAPISpec(mergeSpec);
		
		validator.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), apiOpenApi4j, config);
		
		System.out.println("Valido richiesta con contenuto corretto..");
		{
			TextHttpRequestEntity validRequest = new TextHttpRequestEntity();
			validRequest.setUrl("documenti/testbase64/"+UUID.randomUUID().toString());	
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
			invalidRequest.setUrl("documenti/testbase64/"+UUID.randomUUID().toString());	
			invalidRequest.setMethod(HttpRequestMethod.POST);
			invalidRequest.setContent("{ asper");
			Map<String, List<String>> parametersTrasporto = new HashMap<>();
			TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
			invalidRequest.setHeaders(parametersTrasporto);
			invalidRequest.setContentType(HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
	
			String erroreAttesoRichiesta = "[ERROR] [REQUEST] err.format.base64.badLength, should be multiple of 4: 7"; 
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
			validResponse.setUrl("documenti/testbase64/"+UUID.randomUUID().toString());	
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
			invalidResponse.setUrl("documenti/testbase64/"+UUID.randomUUID().toString());	
			invalidResponse.setMethod(HttpRequestMethod.POST);
			invalidResponse.setStatus(200);
			invalidResponse.setContent("{ asper");
			Map<String, List<String>> parametersTrasporto = new HashMap<>();
			TransportUtils.addHeader(parametersTrasporto,HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
			invalidResponse.setContentType(HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM);
			invalidResponse.setHeaders(parametersTrasporto);
	
			String erroreAttesoRisposta = "[ERROR] [RESPONSE] err.format.base64.badLength, should be multiple of 4: 7"; 
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

	
	
	private static void testSubMediatypeWildcardJsonValidation(OpenAPILibrary openAPILibrary, boolean mergeSpec, boolean validateWildcard)
			throws UtilsException, ProcessingException, URISyntaxException, Exception {
		System.out.println("TEST #S-1 per validazione json con wildcard ("+validateWildcard+") nel subtype");
		
		URL url = TestOpenApi4j.class.getResource("/org/openspcoop2/utils/openapi/allegati.yaml");
		
		ApiSchema apiSchemaYaml = new ApiSchema("teamdigitale-openapi_definitions.yaml", 
				Utilities.getAsByteArray(TestOpenApi4j.class.getResourceAsStream("/org/openspcoop2/utils/service/schemi/standard/teamdigitale-openapi_definitions.yaml")), ApiSchemaType.YAML);
					
		IApiReader apiReaderOpenApi4j = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		ApiReaderConfig configOpenApi4j = new ApiReaderConfig();
		configOpenApi4j.setProcessInclude(false);
		apiReaderOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), new File(url.toURI()), configOpenApi4j, apiSchemaYaml);
		Api apiOpenApi4j = apiReaderOpenApi4j.read();
								
		IApiValidator apiValidatorOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
		OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
		configO.setOpenApi4JConfig(new OpenapiApi4jValidatorConfig());
		configO.getOpenApi4JConfig().setOpenApiLibrary(openAPILibrary);
		configO.getOpenApi4JConfig().setValidateAPISpec(true);
		configO.getOpenApi4JConfig().setMergeAPISpec(mergeSpec);
		configO.getOpenApi4JConfig().setValidateWildcardSubtypeAsJson(validateWildcard);
		apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), apiOpenApi4j, configO);

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
				
				if (validateWildcard) {
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
				
				if (validateWildcard) {
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

		
		System.out.println("TEST #S-1 per validazione json con wildcard ("+validateWildcard+") nel subtype completato!");

	}

	private static void checkErrorTest20(boolean esito, String tipoTest, Exception e, String tipologia, boolean openapi4j, OpenAPILibrary openAPILibrary) throws Exception {
		
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
		if(openapi4j) {
			if(arrayValuesNull) {
				
				String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
						"body.array_nullable_values_optional.0: Null value is not allowed. (code: 1021)" :
						"[Path '/array_nullable_values_optional/0'] Instance type (null) does not match any allowed primitive type (allowed: [\"string\"])";
				
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
				
				msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
						"body.array_nullable_values_optional.1: Null value is not allowed. (code: 1021)" :
						"[Path '/array_nullable_values_optional/1'] Instance type (null) does not match any allowed primitive type (allowed: [\"string\"])";				
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
				
				msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ? 
						"body.array_nullable_values_required.0: Null value is not allowed. (code: 1021)" :
						"[Path '/array_nullable_values_required/0'] Instance type (null) does not match any allowed primitive type (allowed: [\"string\"])";
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t "+tipoTest+" ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
			}
			else {
				for (int k = 0; k < 2; k++) {
					String suffix = (k==0 ? "required" : "optional");
					String msgErroreAtteso = openAPILibrary == OpenAPILibrary.openapi4j ?
							"body."+element+suffix+": Null value is not allowed. (code: 1021)" :
							"[Path '/"+element+suffix+"'] Instance type (null) does not match any allowed primitive type (allowed:";
					if(!e.getMessage().contains(msgErroreAtteso)) {
						System.out.println("\t "+tipoTest+" ERRORE!");
						throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
					}
				}
			}						
		}
		else {
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
		}
	}
	
}
