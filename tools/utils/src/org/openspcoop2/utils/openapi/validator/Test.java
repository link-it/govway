/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

import java.io.File;
import java.net.URI;
import java.util.Properties;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.json.JsonValidatorAPI.ApiName;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.ValidatorException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.entity.HttpBaseEntity;
import org.openspcoop2.utils.rest.entity.TextHttpRequestEntity;
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

	public static void testValidation(URI uri, String baseUrl, String testName, ApiFormats format) throws Exception {
		try {
	
			IApiReader apiReader = ApiFactory.newApiReader(format);
			apiReader.init(LoggerWrapperFactory.getLogger(Test.class), new File(uri), new ApiReaderConfig());
			Api api = apiReader.read();
			IApiValidator apiValidator = ApiFactory.newApiValidator(format);
			OpenapiApiValidatorConfig config = new OpenapiApiValidatorConfig();
			config.setJsonValidatorAPI(ApiName.FGE);
			apiValidator.init(LoggerWrapperFactory.getLogger(Test.class), api, config);
	
			System.out.println("["+testName+"] Test #1 (Richiesta GET con parametro path)");
			String testUrl1 = baseUrl + "/pets/2";
			HttpBaseEntity<?> httpEntity = new TextHttpRequestEntity();
			httpEntity.setMethod(HttpRequestMethod.GET);
			httpEntity.setUrl(testUrl1);
	
			Properties parametersTrasporto = new Properties();
			parametersTrasporto.put("api_key", "aaa");
			httpEntity.setParametersTrasporto(parametersTrasporto);
			apiValidator.validate(httpEntity);
	
			System.out.println("["+testName+"] Test #1 completato");
	
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
				System.out.println("["+testName+"] Test #2 completato");
			}
			
			System.out.println("["+testName+"] Test #3 (Richiesta GET con parametri query)");
			Properties parametersQuery = new Properties();
			HttpBaseEntity<?> httpEntity3 = new TextHttpRequestEntity();
			parametersQuery.put("status", "available");
			httpEntity3.setParametersQuery(parametersQuery);
			httpEntity3.setMethod(HttpRequestMethod.GET);
			httpEntity3.setUrl(testUrl2);
			apiValidator.validate(httpEntity3);
	
			System.out.println("["+testName+"] Test #3 completato");
			
			System.out.println("["+testName+"] Test #4 (Richiesta POST con body json corretto)");
			TextHttpRequestEntity httpEntity4 = new TextHttpRequestEntity();
			httpEntity4.setMethod(HttpRequestMethod.POST);
			httpEntity4.setUrl("/pets");
			httpEntity4.setContent("{\"name\" : \"aaa\",  \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"]}");
			httpEntity4.setContentType("application/json");
			apiValidator.validate(httpEntity4);

			System.out.println("["+testName+"] Test #4 completato");

			System.out.println("["+testName+"] Test #5 (Richiesta POST con body json errato)");
			TextHttpRequestEntity httpEntity5 = new TextHttpRequestEntity();
			httpEntity5.setMethod(HttpRequestMethod.POST);
			httpEntity5.setUrl("/pets");
			httpEntity5.setContent("{\"name\" : \"aaa\", \"photoUrls\": [\"http:localhost:8080/a\",\"http:localhost:8080/b\"], \"a\":\"b\"}");
			httpEntity5.setContentType("application/json");
			try {
				apiValidator.validate(httpEntity5);
				throw new Exception("Errore: Attesa " + ValidatorException.class.getName());
			} catch(ValidatorException e) {
				System.out.println("["+testName+"] Errore trovato: " + e.getMessage());
				System.out.println("["+testName+"] Test #5 completato");
			}

//			System.out.println("Test #6 (Richiesta POST con body xml corretto)");
//			TextHttpRequestEntity httpEntity6 = new TextHttpRequestEntity();
//			httpEntity6.setMethod(HttpRequestMethod.POST);
//			httpEntity6.setUrl("/pets");
//			String content = "<Pet><name>aaaa</name><photoUrls><photoUrl>http:localhost:8080/a</photoUrl><photoUrl>http:localhost:8080/b</photoUrl></photoUrls></Pet>";
//			
//			httpEntity6.setContent(content);
//			httpEntity6.setContentType("application/xml");
//			apiValidator.validate(httpEntity6);
//	
//			System.out.println("Test #6 completato");

		} catch(Exception e) {
			System.err.println("["+testName+"] Errore durante l'esecuzione dei test: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
	
}
