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

import java.io.File;
import java.net.URI;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiOperation;
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

	public static void test(URI uri, String testName, ApiFormats format, String baseUri) throws Exception {

		IApiReader apiReader = ApiFactory.newApiReader(format);
		apiReader.init(LoggerWrapperFactory.getLogger(Test.class), new File(uri), new ApiReaderConfig());
		if(apiReader instanceof AbstractOpenapiApiReader) {
			((AbstractOpenapiApiReader)apiReader).setDebug(true);
		}
		Api api = apiReader.read();

		File f = File.createTempFile("test", "");
		f.delete();
		if(f.mkdir()==false) {
			System.out.println("["+testName+"] Creazione dir non riuscita: "+f.getAbsolutePath());
			return;
		}
		System.out.println("["+testName+"] Test scritti nella directory: "+f.getAbsolutePath());
		
		File fApi = new File(f,"API.txt");
		FileSystemUtilities.writeFile(fApi, api.toString().getBytes());
		System.out.println("["+testName+"] API COMPLESSIVA scritta in: "+fApi.getAbsolutePath());

		System.out.println("["+testName+"] Validazione ... ");
		api.validate(false);
		System.out.println("["+testName+"] Validazione effettuata con successo");
		
		String test = baseUri+"/pets";
		System.out.println("["+testName+"] API-Op ["+test+"]: "+
				checkNotNull(api.findOperation(HttpRequestMethod.POST, test), f, "POST_petsAbsolute"));

		String testSenzaBaseUri = "/pets";
		System.out.println("["+testName+"] API-Op ["+testSenzaBaseUri+"]: "+
				checkNotNull(api.findOperation(HttpRequestMethod.POST, testSenzaBaseUri), f, "POST_pets"));
		
		testSenzaBaseUri = "/pets";
		System.out.println("["+testName+"] API-Op ["+testSenzaBaseUri+"]: "+
				checkNotNull(api.findOperation(HttpRequestMethod.PUT, testSenzaBaseUri), f, "PUT_pets"));
		
		testSenzaBaseUri = "/pets";
		System.out.println("["+testName+"] API-Op ["+testSenzaBaseUri+"]: "+
				checkNotNull(api.findOperation(HttpRequestMethod.DELETE, testSenzaBaseUri), f, "DELETE_pets"));

		String testConPetid = "/pets/2";
		System.out.println("["+testName+"] API-Op ["+testConPetid+"]: "+
				checkNotNull(api.findOperation(HttpRequestMethod.GET, testConPetid), f, "GET_pets_2"));
		System.out.println("["+testName+"] API-Op ["+testConPetid+"]: "+
				checkNotNull(api.findOperation(HttpRequestMethod.PUT, testConPetid), f, "PUT_pets_2"));

		String testConPetid2 = "/pets/2/uploadImage";
		System.out.println("["+testName+"] API-Op ["+testConPetid2+"]: "+
				checkNotNull(api.findOperation(HttpRequestMethod.POST, testConPetid2), f, "POST_pets_2_uploadImage"));

		String testPathInesistente = "/pets/find/inesistente";
		System.out.println("["+testName+"] API-Op ["+testPathInesistente+"]: "+
				checkNull(api.findOperation(HttpRequestMethod.GET, testPathInesistente), f, "GET_pets_find_inesistente"));

		String testConRequestConParametriInline = "/pets/findByStatus";
		System.out.println("["+testName+"] API-Op ["+testConRequestConParametriInline+"]: "+
				checkNotNull(api.findOperation(HttpRequestMethod.GET, testConRequestConParametriInline),f,"GET_pets_findByStatus"));

		String testConRequestCompleta = "/pets/2/completa";
		System.out.println("["+testName+"] API-Op ["+testConRequestCompleta+"]: "+
				checkCompleta(api.findOperation(HttpRequestMethod.POST, testConRequestCompleta),f,"POST_pets_2_completa",format));

	}
	
	private static String checkCompleta(ApiOperation api, File f, String nome, ApiFormats apiFormat) throws Exception {
		String resp = checkNotNull(api, f, nome);
		
		if(api.getRequest().sizeBodyParameters() <=0)
			throw new Exception("Resource "+nome+" non contiene body parameters");
		
		if(api.getRequest().sizeHeaderParameters() <=0)
			throw new Exception("Resource "+nome+" non contiene header parameters");
		
		if(api.getRequest().sizeQueryParameters() <=0)
			throw new Exception("Resource "+nome+" non contiene query parameters");
		
		if(api.getRequest().sizeDynamicPathParameters() <=0)
			throw new Exception("Resource "+nome+" non contiene dynamic path parameters");

		if(!apiFormat.equals(ApiFormats.SWAGGER_2)) { //non supportato da Swagger 2.0
			if(api.getRequest().sizeCookieParameters() <=0)
				throw new Exception("Resource "+nome+" non contiene cookie parameters");
		}
		
		if(api.getResponses().size() < 0) {
			throw new Exception("Resource "+nome+" non contiene responses");
		}

		if(api.getResponses().get(0).sizeHeaderParameters() <= 0) {
			throw new Exception("Resource "+nome+" non contiene response header parameters");
		}
		
		if(api.getResponses().get(0).sizeBodyParameters() <= 0) {
			throw new Exception("Resource "+nome+" non contiene response body parameters");
		}

		
		return resp;

	}

	private static String checkNull(ApiOperation api, File f, String nome) throws Exception {
		if(api!=null) {
			throw new Exception("Resource "+nome+" found ??");
		}
		else {
			return "Resource non trovata come atteso nel test";
		}
	}
	private static String checkNotNull(ApiOperation api, File f, String nome) throws Exception {
		if(api==null) {
			throw new Exception("Resource "+nome+" not found");
		}
		else {
			File fApi = new File(f,nome+".txt");
			FileSystemUtilities.writeFile(fApi, api.toString().getBytes());
			return "Resource "+nome+" scritta in: "+fApi.getAbsolutePath();
		}
	}
}
