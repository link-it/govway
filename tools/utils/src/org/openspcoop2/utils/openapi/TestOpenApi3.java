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
public class TestOpenApi3 {

	public static void main(String[] args) throws Exception {

		String tipo = null;
		if(args!=null && args.length>0) {
			tipo = args[0];
		}
		
		String baseUri = "http://petstore.swagger.io/api";

		if(tipo==null || "json".equalsIgnoreCase(tipo)) {
			URI jsonUri = TestOpenApi3.class.getResource("/org/openspcoop2/utils/openapi/testOpenAPI_3.0.json").toURI();
			Test.test(jsonUri,"json", ApiFormats.OPEN_API_3, baseUri);
		}
		
		if(tipo==null) {
			System.out.println("\n\n\n==============================================================");
		}
		
		if(tipo==null || "yaml".equalsIgnoreCase(tipo)) {
			URI yamlUri = TestOpenApi3.class.getResource("/org/openspcoop2/utils/openapi/testOpenAPI_3.0.yaml").toURI();
			Test.test(yamlUri,"yaml", ApiFormats.OPEN_API_3, baseUri);
		}

	}

}
