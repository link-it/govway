/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.utils.openapi.validator.test;

import java.net.URI;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.YAMLUtils;
import org.openspcoop2.utils.openapi.validator.OpenAPILibrary;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;


/**
 * Test
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class OpenApi3Test {

	public static void main(String[] args) throws Exception {

		try {
		
			String tipo = null;
			if(args!=null && args.length>0) {
				tipo = args[0];
			}

			OpenAPILibrary openAPILibrary = OpenAPILibrary.openapi4j;
			if(args!=null && args.length>1) {
				openAPILibrary = OpenAPILibrary.valueOf(args[1]);
			}
			
			boolean mergeSpec = true;
			if(args!=null && args.length>2) {
				mergeSpec = Boolean.valueOf(args[2]);
			}

			
			
			//String baseUri = "http://petstore.swagger.io/api";
			String baseUri = ""; // non va definita
			
			// yaml inclusi anche da dentro il json
			ApiSchema apiSchemaYaml = new ApiSchema("test_import.yaml", 
					Utilities.getAsByteArray(OpenApi3Test.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test/test_import.yaml")), ApiSchemaType.YAML);
			ApiSchema apiSchemaYaml2 = new ApiSchema("test_import2.yaml", 
					Utilities.getAsByteArray(OpenApi3Test.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test/test_import2.yaml")), ApiSchemaType.YAML);
			
			if(tipo==null || "json".equalsIgnoreCase(tipo)) {
			
				URI jsonUri = OpenApi3Test.class.getResource("/org/openspcoop2/utils/openapi/test/testOpenAPI_3.0.json").toURI();
								
				ApiSchema apiSchemaJson = new ApiSchema("test_import.json", 
						Utilities.getAsByteArray(OpenApi3Test.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test/test_import.json")), ApiSchemaType.JSON);
				ApiSchema apiSchemaJson2 = new ApiSchema("test_import2.json", 
						Utilities.getAsByteArray(OpenApi3Test.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test/test_import2.json")), ApiSchemaType.JSON);
				
				CoreTest.testValidation(jsonUri, baseUri, "json", ApiFormats.OPEN_API_3, openAPILibrary, mergeSpec,
						apiSchemaJson, apiSchemaJson2, apiSchemaYaml, apiSchemaYaml2);
				
				System.out.println("\n\n\n==============================================================");
				
				System.out.println("IS JSON atteso:true trovato: "+JSONUtils.getInstance().isJson(apiSchemaJson.getContent()));
				if(!JSONUtils.getInstance().isJson(apiSchemaJson.getContent())) {
					throw new Exception("Atteso un json");
				}
				System.out.println("IS YAML atteso:false trovato: "+YAMLUtils.getInstance().isYaml(apiSchemaJson.getContent()));
				if(YAMLUtils.getInstance().isYaml(apiSchemaJson.getContent())) {
					throw new Exception("Atteso un json");
				}
			}
			
			if(tipo==null) {
				System.out.println("\n\n\n==============================================================");
			}
			
			if(tipo==null || "yaml".equalsIgnoreCase(tipo)) {
			
				URI yamlUri = OpenApi3Test.class.getResource("/org/openspcoop2/utils/openapi/test/testOpenAPI_3.0.yaml").toURI();
						
				CoreTest.testValidation(yamlUri, baseUri, "yaml", ApiFormats.OPEN_API_3, openAPILibrary, mergeSpec,
						apiSchemaYaml, apiSchemaYaml2);
				
				System.out.println("\n\n\n==============================================================");
				
				System.out.println("IS JSON atteso:false trovato: "+JSONUtils.getInstance().isJson(apiSchemaYaml.getContent()));
				if(JSONUtils.getInstance().isJson(apiSchemaYaml.getContent())) {
					throw new Exception("Atteso un yaml");
				}
				System.out.println("IS YAML atteso:true trovato: "+YAMLUtils.getInstance().isYaml(apiSchemaYaml.getContent()));
				if(!YAMLUtils.getInstance().isYaml(apiSchemaYaml.getContent())) {
					throw new Exception("Atteso un yaml");
				}
			}
		
		} catch(Exception e) {
			System.err.println("Errore durante l'esecuzione dei test: " + e.getMessage());
			e.printStackTrace(System.err);
			throw e;
		}
	}

}
