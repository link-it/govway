/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

import java.io.File;
import java.net.URI;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;


/**
 * Test
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13435 $, $Date: 2017-11-15 17:02:49 +0100(mer, 15 nov 2017) $
 *
 */
public class Test {

	public static void main(String[] args) throws Exception {

		URI jsonUri = Test.class.getResource("/org/openspcoop2/utils/openapi/test.json").toURI();
		URI yamlUri = Test.class.getResource("/org/openspcoop2/utils/openapi/test.yaml").toURI();

		test(jsonUri,"json");
		
		System.out.println("\n\n\n==============================================================");
		test(yamlUri,"yaml");

	}

	public static void test(URI uri, String testName) throws Exception {

		IApiReader apiReader = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		apiReader.init(LoggerWrapperFactory.getLogger(Test.class), new File(uri), new ApiReaderConfig());
		Api api = apiReader.read();

		System.out.println("["+testName+"] API COMPLESSIVA: "+api);

		System.out.println("["+testName+"] Validazione ... ");
		api.validate(false);
		System.out.println("["+testName+"] Validazione effettuata con successo");
		
		String test = "http://petstore.swagger.io/api/pets";
		System.out.println("["+testName+"] API-Op ["+test+"]: "+api.findOperation(HttpRequestMethod.POST, test));

		String testSenzaBaseUri = "/pets";
		System.out.println("["+testName+"] API-Op ["+testSenzaBaseUri+"]: "+api.findOperation(HttpRequestMethod.POST, testSenzaBaseUri));

		String testConPetid = "/pets/2";
		System.out.println("["+testName+"] API-Op ["+testConPetid+"]: "+api.findOperation(HttpRequestMethod.GET, testConPetid));
		System.out.println("["+testName+"] API-Op PUT ["+testConPetid+"]: "+api.findOperation(HttpRequestMethod.PUT, testConPetid));

		String testConPetid2 = "/pets/2/uploadImage";
		System.out.println("["+testName+"] API-Op ["+testConPetid2+"]: "+api.findOperation(HttpRequestMethod.POST, testConPetid2));

		String testPathInesistente = "/pets/find/inesistente";
		System.out.println("["+testName+"] API-Op ["+testPathInesistente+"]: "+api.findOperation(HttpRequestMethod.GET, testPathInesistente));

		String testConRequestConParametriInline = "/pets/findByStatus";
		System.out.println("["+testName+"] API-Op ["+testConRequestConParametriInline+"]: "+api.findOperation(HttpRequestMethod.GET, testConRequestConParametriInline));

	}
}
