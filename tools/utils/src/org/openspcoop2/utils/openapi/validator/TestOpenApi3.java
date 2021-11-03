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

import java.net.URI;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.YAMLUtils;
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
public class TestOpenApi3 {

	public static void main(String[] args) throws Exception {

		try {
		
			String tipo = null;
			if(args!=null && args.length>0) {
				tipo = args[0];
			}

			boolean useOpenApi4j = false;
			if(args!=null && args.length>1) {
				useOpenApi4j = Boolean.valueOf(args[1]);
			}
			
			String baseUri = "http://petstore.swagger.io/api";
			
			// yaml inclusi anche da dentro il json
			ApiSchema apiSchemaYaml = new ApiSchema("test_import.yaml", 
					Utilities.getAsByteArray(TestOpenApi3.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test_import.yaml")), ApiSchemaType.YAML);
			ApiSchema apiSchemaYaml2 = new ApiSchema("test_import2.yaml", 
					Utilities.getAsByteArray(TestOpenApi3.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test_import2.yaml")), ApiSchemaType.YAML);
			
			if(tipo==null || "json".equalsIgnoreCase(tipo)) {
			
				URI jsonUri = TestOpenApi3.class.getResource("/org/openspcoop2/utils/openapi/testOpenAPI_3.0.json").toURI();
								
				ApiSchema apiSchemaJson = new ApiSchema("test_import.json", 
						Utilities.getAsByteArray(TestOpenApi3.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test_import.json")), ApiSchemaType.JSON);
				ApiSchema apiSchemaJson2 = new ApiSchema("test_import2.json", 
						Utilities.getAsByteArray(TestOpenApi3.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test_import2.json")), ApiSchemaType.JSON);
				
				Test.testValidation(jsonUri, baseUri, "json", ApiFormats.OPEN_API_3, useOpenApi4j,
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
			
				URI yamlUri = TestOpenApi3.class.getResource("/org/openspcoop2/utils/openapi/testOpenAPI_3.0.yaml").toURI();
						
				Test.testValidation(yamlUri, baseUri, "yaml", ApiFormats.OPEN_API_3, useOpenApi4j,
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
