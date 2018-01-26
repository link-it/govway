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


package org.openspcoop2.utils.openapi;

import java.net.URI;

import org.openspcoop2.utils.rest.ApiFormats;


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

		Test.test(jsonUri,"json", ApiFormats.SWAGGER_2, baseUri);
		
		System.out.println("\n\n\n==============================================================");
		Test.test(yamlUri,"yaml", ApiFormats.SWAGGER_2, baseUri);

	}

}
