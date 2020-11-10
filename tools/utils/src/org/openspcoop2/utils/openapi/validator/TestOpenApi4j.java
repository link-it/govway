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
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.ValidatorException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;
import org.openspcoop2.utils.rest.entity.TextHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.TextHttpResponseEntity;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

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
		configO.getOpenApi4JConfig().setUseOpenApi4J(true);
		apiValidatorOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), apiOpenApi4j, configO);
		
		IApiValidator apiValidatorNoOpenApi4j = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
		OpenapiApiValidatorConfig configNo = new OpenapiApiValidatorConfig();
		configNo.setOpenApi4JConfig(new OpenapiApi4jValidatorConfig());
		configNo.getOpenApi4JConfig().setUseOpenApi4J(false);
		apiValidatorNoOpenApi4j.init(LoggerWrapperFactory.getLogger(TestOpenApi4j.class), apiNoOpenApi4j, configNo);
		
		
		
		System.out.println("Test #1 (Richiesta POST con parametro /documenti/mixed/send)");
		String testUrl1 = baseUri+"/documenti/mixed/send";
		TextHttpRequestEntity httpEntity = new TextHttpRequestEntity();
		httpEntity.setMethod(HttpRequestMethod.POST);
		httpEntity.setUrl(testUrl1);	
		Map<String, String> parametersTrasporto = new HashMap<>();
		parametersTrasporto.put("api_key", "aaa");
		parametersTrasporto.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
		httpEntity.setParametersTrasporto(parametersTrasporto);
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
			String msgErroreAtteso = "body.allegati.0.documento: Field 'uri' is required.";		
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
			String msgErroreAtteso = "body.allegati.0.documento: Schema selection can't be made for discriminator 'tipoDocumento' with value 'riferimento-uriERRATA'.";
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
			String msgErroreAtteso = "body.allegati.1.documento: Property name in content 'tipoDocumento' is not set.";
			if(!e.getMessage().contains(msgErroreAtteso)) {
				throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
			}
			msgErroreAtteso = "From: body.<allOf>.allegati.1.<items>.<#/components/schemas/AllegatoRiferimentoMixed>.<allOf>.documento.<discriminator>";
			if(!e.getMessage().contains(msgErroreAtteso)) {
				throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
			}
			System.out.println("Test #4 completato\n\n");
		}
		
		
		System.out.println("Test #5 (Richiesta POST con parametro dinamico /documenti/XYZ)");
		String testUrl5 = baseUri+"/documenti/test/"+UUID.randomUUID().toString();
		
		TextHttpRequestEntity httpEntityDynamicPath = new TextHttpRequestEntity();
		httpEntityDynamicPath.setMethod(HttpRequestMethod.POST);
		httpEntityDynamicPath.setUrl(testUrl5);	
		Map<String, String> parametersTrasportoDynamicPath = new HashMap<>();
		parametersTrasportoDynamicPath.put("api_key", "aaa");
		parametersTrasportoDynamicPath.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
		httpEntityDynamicPath.setParametersTrasporto(parametersTrasportoDynamicPath);
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
		
		TextHttpResponseEntity httpEntityResponse = new TextHttpResponseEntity();
		httpEntityResponse.setStatus(200);
		httpEntityResponse.setMethod(HttpRequestMethod.GET);
		httpEntityResponse.setUrl(testUrl5);	
		Map<String, String> parametersTrasportoRisposta = new HashMap<>();
		parametersTrasportoRisposta.put("api_key", "aaa");
		parametersTrasportoRisposta.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
		httpEntityResponse.setParametersTrasporto(parametersTrasportoRisposta);
		httpEntityResponse.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		httpEntityResponse.setContent(json); // volutamente metto un json che comunque dovrebbe trattare come binario!
		apiValidatorOpenApi4j.validate(httpEntityResponse);	
		System.out.println("Test #6 completato\n\n");
		
		
		
		System.out.println("Test #7 (Richiesta POST con parametro /documenti/mixed/send e elemento null)");
		String testUrl7 = baseUri+"/documenti/mixed/send";
		TextHttpRequestEntity httpEntity7 = new TextHttpRequestEntity();
		httpEntity7.setMethod(HttpRequestMethod.POST);
		httpEntity7.setUrl(testUrl7);	
		Map<String, String> parametersTrasporto7 = new HashMap<>();
		parametersTrasporto7.put("api_key", "aaa");
		parametersTrasporto7.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
		httpEntity7.setParametersTrasporto(parametersTrasporto7);
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
			Map<String, String> parametersTrasporto8 = new HashMap<>();
			parametersTrasporto8.put("api_key", "aaa");
			parametersTrasporto8.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			httpEntity8.setParametersTrasporto(parametersTrasporto8);
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
				String msgErroreAtteso = "body.allegati.0.codiceOpzionaleNumerico: '"+valore+"' does not respect pattern '^\\d{6}$'.";
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t (Valore:"+valore+") ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
				msgErroreAtteso = "From: body.<allOf>.allegati.0.<items>.<#/components/schemas/AllegatoRiferimentoMixed>.<allOf>.<#/components/schemas/Allegato>.codiceOpzionaleNumerico.<pattern>";
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
			Map<String, String> parametersTrasporto9 = new HashMap<>();
			parametersTrasporto9.put("api_key", "aaa");
			parametersTrasporto9.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			httpEntity9.setParametersTrasporto(parametersTrasporto9);
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
				String msgErroreAtteso = "body.allegati.0.codiceOpzionaleCodiceFiscaleOrCodiceEsterno: '"+valore+"' does not respect pattern '^[a-zA-Z]{6}[0-9]{2}[a-zA-Z0-9]{3}[a-zA-Z0-9]{5}$|^[A-Z0-9]{3}\\d{3}$'.";
				if(!e.getMessage().contains(msgErroreAtteso)) {
					System.out.println("\t (Valore:"+valore+") ERRORE!");
					throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"'");
				}
				msgErroreAtteso = "From: body.<allOf>.allegati.0.<items>.<#/components/schemas/AllegatoRiferimentoMixed>.<allOf>.<#/components/schemas/Allegato>.codiceOpzionaleCodiceFiscaleOrCodiceEsterno.<pattern>";
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
		Map<String, String> parametersTrasportoTest10 = new HashMap<>();
		parametersTrasportoTest10.put("api_key", "aaa");
		parametersTrasportoTest10.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
		httpEntityTest10.setParametersTrasporto(parametersTrasportoTest10);
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
		Map<String, String> parametersTrasportoRispostaTest10 = new HashMap<>();
		parametersTrasportoRispostaTest10.put("api_key", "aaa");
		httpEntityResponseTest10.setParametersTrasporto(parametersTrasportoRispostaTest10);
		
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
				parametersTrasportoRispostaTest10.put(HttpConstants.CONTENT_TYPE, contentTypeTest10);
				httpEntityResponseTest10.setContentType(contentTypeTest10);
				httpEntityResponseTest10.setParametersTrasporto(parametersTrasportoRispostaTest10);
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
						String atteso = "Content type '"+contentTypeTest10+"' is not allowed for body content. (code: 203)";
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
		Map<String, String> parametersTrasportoTest11 = new HashMap<>();
		parametersTrasportoTest11.put("api_key", "aaa");
		httpEntityTest11.setParametersTrasporto(parametersTrasportoTest11);
		
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
				parametersTrasportoTest11.put(HttpConstants.CONTENT_TYPE, contentTypeTest11);
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
						// Per adesso openapi4j non sembra accorgersi dell'errore.
						//String atteso = "Content type '"+contentTypeTest11+"' is not allowed for body content. (code: 203)";
						//if(!msg.contains(atteso)) {
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
		Map<String, String> parametersTrasportoRispostaTest11 = new HashMap<>();
		parametersTrasportoRispostaTest11.put("api_key", "aaa");
		httpEntityResponseTest11.setParametersTrasporto(parametersTrasportoRispostaTest11);
		
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
				parametersTrasportoRispostaTest11.put(HttpConstants.CONTENT_TYPE, contentTypeTest11);
				httpEntityResponseTest11.setContentType(contentTypeTest11);
				httpEntityResponseTest11.setParametersTrasporto(parametersTrasportoRispostaTest11);
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
						if(!msg.contains(atteso)) {
							String checkErrore = "Validazione risposta con content-type '"+contentTypeTest11+"' "+tipoTest+" contenuto terminato con un errore diverso da quello atteso: '"+msg+"'";
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
		Map<String, String> parametersTrasporto12_default = new HashMap<>();
		httpEntity12_default.setParametersTrasporto(parametersTrasporto12_default);
		
		String testUrl12_optional = baseUri+"/documenti/in-line/sendOptional";
		TextHttpRequestEntity httpEntity12_optional = new TextHttpRequestEntity();
		httpEntity12_optional.setMethod(HttpRequestMethod.POST);
		httpEntity12_optional.setUrl(testUrl12_optional);
		Map<String, String> parametersTrasporto12_optional = new HashMap<>();
		httpEntity12_optional.setParametersTrasporto(parametersTrasporto12_optional);
		
		String testUrl12_required = baseUri+"/documenti/in-line/sendRequired";
		TextHttpRequestEntity httpEntity12_required = new TextHttpRequestEntity();
		httpEntity12_required.setMethod(HttpRequestMethod.POST);
		httpEntity12_required.setUrl(testUrl12_required);
		Map<String, String> parametersTrasporto12_required = new HashMap<>();
		httpEntity12_required.setParametersTrasporto(parametersTrasporto12_required);
		
		List<String> contentTypeTest12List = new ArrayList<String>();
		contentTypeTest12List.add(null);
		contentTypeTest12List.add(HttpConstants.CONTENT_TYPE_PLAIN);
		contentTypeTest12List.add(HttpConstants.CONTENT_TYPE_JSON);
		
		for (String contentTypeTest12 : contentTypeTest12List) {
			
			String tipoTestPrefix = "Content-Type:'"+contentTypeTest12+"' ";
		
			parametersTrasporto12_default.put(HttpConstants.CONTENT_TYPE, contentTypeTest12);
			httpEntity12_default.setContentType(contentTypeTest12);
			parametersTrasporto12_optional.put(HttpConstants.CONTENT_TYPE, contentTypeTest12);
			httpEntity12_optional.setContentType(contentTypeTest12);
			parametersTrasporto12_required.put(HttpConstants.CONTENT_TYPE, contentTypeTest12);
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
						if(!required && contentTypeTest12!=null) {
							atteso = "Content-Type '"+contentTypeTest12+"' unsupported";
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
			Map<String, String> parametersTrasportoRispostaTest12 = new HashMap<>();
			httpEntityResponseTest12.setParametersTrasporto(parametersTrasportoRispostaTest12);
			
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
				
				System.out.println("\t Validazione risposta senza contenuto "+tipoTest+"  ...");
				boolean esito = false;
				try {
					apiValidator.validate(httpEntityResponseTest12);	
					esito = true;
				}
				catch(Exception e) {
					//e.printStackTrace(System.out);
					String msg = e.getMessage();
					if(openapi4j) {
						String atteso = "Content type 'null' is not allowed for body content. (code: 203)";
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
			Map<String, String> parametersUrl13 = new HashMap<>();
			parametersUrl13.put("data_documento_query","2020-07-19"); // uso data valida, il test e' sul body	
			httpEntity13.setParametersQuery(parametersUrl13);
			Map<String, String> parametersTrasporto13 = new HashMap<>();
			parametersTrasporto13.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			parametersTrasporto13.put("data_documento_header","2020-07-23"); // uso data valida, il test e' sul body	
			httpEntity13.setParametersTrasporto(parametersTrasporto13);
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
						String msgErroreAtteso = "body.data: Value '"+valore+"' does not match format 'date'. (code: 1007)";
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
			Map<String, String> parametersTrasportoRispostaTest13 = new HashMap<>();
			httpEntityResponseTest13.setParametersTrasporto(parametersTrasportoRispostaTest13);
			parametersTrasportoRispostaTest13.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			parametersTrasporto13.put("data_documento_risposta_header","2020-07-23"); // uso data valida, il test e' sul body	
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
						String msgErroreAtteso = "body.data: Value '"+valore+"' does not match format 'date'. (code: 1007)";
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
			Map<String, String> parametersUrl13 = new HashMap<>();
			parametersUrl13.put("data_documento_query","2020-07-19"); // uso data valida, il test e' su header	
			httpEntity13.setParametersQuery(parametersUrl13);
			Map<String, String> parametersTrasporto13 = new HashMap<>();
			parametersTrasporto13.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			parametersTrasporto13.put("data_documento_header",valore);	
			httpEntity13.setParametersTrasporto(parametersTrasporto13);
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
						String msgErroreAtteso = "data_documento_header: Value '"+valore+"' does not match format 'date'. (code: 1007)";
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
			Map<String, String> parametersTrasportoRispostaTest13 = new HashMap<>();
			httpEntityResponseTest13.setParametersTrasporto(parametersTrasportoRispostaTest13);
			parametersTrasportoRispostaTest13.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			parametersTrasportoRispostaTest13.put("data_documento_risposta_header",valore);
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
						String msgErroreAtteso = "data_documento_risposta_header: Value '"+valore+"' does not match format 'date'. (code: 1007)";
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
			Map<String, String> parametersUrl13 = new HashMap<>();
			parametersUrl13.put("data_documento_query",valore); 	
			httpEntity13.setParametersQuery(parametersUrl13);
			Map<String, String> parametersTrasporto13 = new HashMap<>();
			parametersTrasporto13.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			parametersTrasporto13.put("data_documento_header","2020-07-19"); // uso data valida, il test e' su query parameter	
			httpEntity13.setParametersTrasporto(parametersTrasporto13);
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
						String msgErroreAtteso = "data_documento_query: Value '"+valore+"' does not match format 'date'. (code: 1007)";						
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
			Map<String, String> parametersUrl13 = new HashMap<>();
			parametersUrl13.put("data_documento_query","2020-07-21"); // uso data valida, il test e' su path
			httpEntity13.setParametersQuery(parametersUrl13);
			Map<String, String> parametersTrasporto13 = new HashMap<>();
			parametersTrasporto13.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			parametersTrasporto13.put("data_documento_header","2020-07-19"); // uso data valida, il test e' su path
			httpEntity13.setParametersTrasporto(parametersTrasporto13);
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

		// ** Test sul body **
		for (int i = 0; i < valori_test14.size(); i++) {
			String valore = valori_test14.get(i);
			boolean esito = esiti_test14.get(i);
			
			TextHttpRequestEntity httpEntity14 = new TextHttpRequestEntity();
			httpEntity14.setMethod(HttpRequestMethod.POST);
			httpEntity14.setUrl(testUrl14+"2020-07-21T17:32:28Z"); // uso data valida, il test e' sul body
			Map<String, String> parametersUrl14 = new HashMap<>();
			parametersUrl14.put("datetime_documento_query","2020-07-19T17:32:28Z"); // uso data valida, il test e' sul body	
			httpEntity14.setParametersQuery(parametersUrl14);
			Map<String, String> parametersTrasporto14 = new HashMap<>();
			parametersTrasporto14.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			parametersTrasporto14.put("datetime_documento_header","2020-07-23T17:32:28Z"); // uso data valida, il test e' sul body	
			httpEntity14.setParametersTrasporto(parametersTrasporto14);
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
						String msgErroreAtteso = "body.data: Value '"+valore+"' does not match format 'date-time'. (code: 1007)";
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
			Map<String, String> parametersTrasportoRispostaTest14 = new HashMap<>();
			httpEntityResponseTest14.setParametersTrasporto(parametersTrasportoRispostaTest14);
			parametersTrasportoRispostaTest14.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			parametersTrasporto14.put("data_documento_risposta_header","2020-07-23T17:32:28Z"); // uso data valida, il test e' sul body	
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
						String msgErroreAtteso = "body.data: Value '"+valore+"' does not match format 'date-time'. (code: 1007)";
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
			Map<String, String> parametersUrl14 = new HashMap<>();
			parametersUrl14.put("datetime_documento_query","2020-07-19T17:32:28Z"); // uso data valida, il test e' su header	
			httpEntity14.setParametersQuery(parametersUrl14);
			Map<String, String> parametersTrasporto14 = new HashMap<>();
			parametersTrasporto14.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			parametersTrasporto14.put("datetime_documento_header",valore);	
			httpEntity14.setParametersTrasporto(parametersTrasporto14);
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
						String msgErroreAtteso = "datetime_documento_header: Value '"+valore+"' does not match format 'date-time'. (code: 1007)";
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
			Map<String, String> parametersTrasportoRispostaTest14 = new HashMap<>();
			httpEntityResponseTest14.setParametersTrasporto(parametersTrasportoRispostaTest14);
			parametersTrasportoRispostaTest14.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			parametersTrasportoRispostaTest14.put("datetime_documento_risposta_header",valore);
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
						String msgErroreAtteso = "datetime_documento_risposta_header: Value '"+valore+"' does not match format 'date-time'. (code: 1007)";
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
			Map<String, String> parametersUrl14 = new HashMap<>();
			parametersUrl14.put("datetime_documento_query",valore); 	
			httpEntity14.setParametersQuery(parametersUrl14);
			Map<String, String> parametersTrasporto14 = new HashMap<>();
			parametersTrasporto14.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			parametersTrasporto14.put("datetime_documento_header","2020-07-19T17:32:28Z"); // uso data valida, il test e' su query parameter	
			httpEntity14.setParametersTrasporto(parametersTrasporto14);
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
						String msgErroreAtteso = "datetime_documento_query: Value '"+valore+"' does not match format 'date-time'. (code: 1007)";
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
			Map<String, String> parametersUrl14 = new HashMap<>();
			parametersUrl14.put("datetime_documento_query","2020-07-21T17:32:28Z"); // uso data valida, il test e' su path
			httpEntity14.setParametersQuery(parametersUrl14);
			Map<String, String> parametersTrasporto14 = new HashMap<>();
			parametersTrasporto14.put(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
			parametersTrasporto14.put("datetime_documento_header","2020-07-19T17:32:28Z"); // uso data valida, il test e' su path
			httpEntity14.setParametersTrasporto(parametersTrasporto14);
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
			Map<String, String> parametersTrasportoRispostaTest15 = new HashMap<>();
			if(ct!=null) {
				parametersTrasportoRispostaTest15.put(HttpConstants.CONTENT_TYPE, ct);
			}
			httpEntityResponseTest15.setParametersTrasporto(parametersTrasportoRispostaTest15);
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
						if(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807.equals(ct) && code.intValue() != 200) {
							msgErroreAtteso2 = "body: Field 'type' is required. (code: 1026)";
						}
						if(!e.getMessage().contains(msgErroreAtteso) && !e.getMessage().contains(msgErroreAtteso2)) {
							System.out.println("\t "+tipoTest+" ERRORE!");
							throw new Exception("Errore: atteso messaggio di errore che contenga '"+msgErroreAtteso+"' o '"+msgErroreAtteso2+"'");
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
			Map<String, String> parametersTrasportoRispostaTest16 = new HashMap<>();
			parametersTrasportoRispostaTest16.put(HttpConstants.CONTENT_TYPE, ct);
			httpEntityResponseTest16.setParametersTrasporto(parametersTrasportoRispostaTest16);
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
		
		System.out.println("Test #15 completato\n\n");
	}

}
