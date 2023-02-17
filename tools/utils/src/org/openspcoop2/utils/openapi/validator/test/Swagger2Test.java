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
public class Swagger2Test {

	public static void main(String[] args) throws Exception {

		try {
		
			String tipo = null;
			if(args!=null && args.length>0) {
				tipo = args[0];
			}
			
			OpenAPILibrary openAPILibrary = OpenAPILibrary.json_schema;
			if(args!=null && args.length>1) {
				openAPILibrary = OpenAPILibrary.valueOf(args[1]);
			}
			
			boolean mergeSpec = false;
			if(args!=null && args.length>2) {
				mergeSpec = Boolean.valueOf(args[2]);
			}
			
			
			String baseUri = "http://petstore.swagger.io/v2";
			
			if(tipo==null || "json".equalsIgnoreCase(tipo)) {
			
				URI jsonUri = Swagger2Test.class.getResource("/org/openspcoop2/utils/openapi/test/testSwagger_2.0.json").toURI();
				
				ApiSchema apiSchemaJson = new ApiSchema("test_import.json", 
						Utilities.getAsByteArray(OpenApi3Test.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test/test_import.json")), ApiSchemaType.JSON);
				ApiSchema apiSchemaJson2 = new ApiSchema("test_import2.json", 
						Utilities.getAsByteArray(OpenApi3Test.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test/test_import2.json")), ApiSchemaType.JSON);
				CoreTest.testValidation(jsonUri, baseUri, "json", ApiFormats.SWAGGER_2, openAPILibrary, mergeSpec,
						apiSchemaJson, apiSchemaJson2);
				
			}
			
			if(tipo==null) {
				System.out.println("\n\n\n==============================================================");
			}
			
			if(tipo==null || "yaml".equalsIgnoreCase(tipo)) {
			
				URI yamlUri = Swagger2Test.class.getResource("/org/openspcoop2/utils/openapi/test/testSwagger_2.0.yaml").toURI();
	
				ApiSchema apiSchemaYaml = new ApiSchema("test_import.yaml", 
						Utilities.getAsByteArray(OpenApi3Test.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test/test_import.yaml")), ApiSchemaType.YAML);
				ApiSchema apiSchemaYaml2 = new ApiSchema("test_import2.yaml", 
						Utilities.getAsByteArray(OpenApi3Test.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test/test_import2.yaml")), ApiSchemaType.YAML);
				
				CoreTest.testValidation(yamlUri, baseUri, "yaml", ApiFormats.SWAGGER_2, openAPILibrary, mergeSpec,
						apiSchemaYaml, apiSchemaYaml2);
				
			}
			
		} catch(Exception e) {
			System.err.println("Errore durante l'esecuzione dei test: " + e.getMessage());
			e.printStackTrace(System.err);
			throw e;
		}

	}

}
