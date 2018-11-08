/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

import java.net.URI;

import org.openspcoop2.utils.Utilities;
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
public class TestSwagger2 {

	public static void main(String[] args) throws Exception {

		URI jsonUri = TestSwagger2.class.getResource("/org/openspcoop2/utils/openapi/testSwagger_2.0.json").toURI();
		URI yamlUri = TestSwagger2.class.getResource("/org/openspcoop2/utils/openapi/testSwagger_2.0.yaml").toURI();
		String baseUri = "http://petstore.swagger.io/v2";
		
		ApiSchema apiSchemaJson = new ApiSchema("test_import.json", 
				Utilities.getAsByteArray(TestOpenApi3.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test_import.json")), ApiSchemaType.JSON);
		ApiSchema apiSchemaJson2 = new ApiSchema("test_import2.json", 
				Utilities.getAsByteArray(TestOpenApi3.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test_import2.json")), ApiSchemaType.JSON);
		
		ApiSchema apiSchemaYaml = new ApiSchema("test_import.yaml", 
				Utilities.getAsByteArray(TestOpenApi3.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test_import.yaml")), ApiSchemaType.YAML);
		ApiSchema apiSchemaYaml2 = new ApiSchema("test_import2.yaml", 
				Utilities.getAsByteArray(TestOpenApi3.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test_import2.yaml")), ApiSchemaType.YAML);
		
		Test.testValidation(jsonUri, baseUri, "json", ApiFormats.SWAGGER_2, 
				apiSchemaJson, apiSchemaJson2);
		
		System.out.println("\n\n\n==============================================================");
		Test.testValidation(yamlUri, baseUri, "yaml", ApiFormats.SWAGGER_2, 
				apiSchemaYaml, apiSchemaYaml2);

	}

}
