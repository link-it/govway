/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.config.vault.cli.testsuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.openspcoop2.pdd.config.vault.cli.testsuite.secrets.test.SecretsTest;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
* Utilities
*
* @author Andrea Poli (andrea.poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Utilities {
	
	private Utilities() {}

	public static HttpResponse testSpcoop(Logger log,
			TipoServizio tipoServizio, String api, String operazione, String relatesTo) throws UtilsException {
		return test(log,
				tipoServizio, api, operazione, 
				false, true, relatesTo);
	}
	public static HttpResponse testSoap(Logger log,
			TipoServizio tipoServizio, String api, String operazione) throws UtilsException {
		return test(log,
				tipoServizio, api, operazione, 
				false, false, null);
	}
	public static HttpResponse testRest(Logger log,
			TipoServizio tipoServizio, String api, String operazione) throws UtilsException {
		return test(log,
				tipoServizio, api, operazione, 
				true, false, null);
	}
	private static HttpResponse test(Logger log,
			TipoServizio tipoServizio, String api, String operazione, 
			boolean rest, boolean spcoop, String relatesTo) throws UtilsException {
		
		if(log!=null) {
			// nop
		}
		
		String protocollo = spcoop ? "/spcoop" : "";
		if(!spcoop && 
				(SecretsTest.OP_MODI_PATH.equals(operazione)) || SecretsTest.OP_MODI_ARCHIVIO.equals(operazione)){
			protocollo = "/rest";
		}
		
		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + protocollo + "/SoggettoInternoVaultTest/"+api+"/v1/"+operazione
				: System.getProperty("govway_base_path") + protocollo + "/out/SoggettoInternoVaultTestFruitore/SoggettoInternoVaultTest/"+api+"/v1/"+operazione;
		url+="?govwayTestSuite_IDOP="+operazione;
		HttpRequest request = new HttpRequest();
		
		request.setReadTimeout(20000);

		String contentType= rest ? HttpConstants.CONTENT_TYPE_JSON : HttpConstants.CONTENT_TYPE_SOAP_1_1;
		byte[]content= rest ? Bodies.getJson(Bodies.SIZE_1K).getBytes() : Bodies.getSOAPEnvelope11(Bodies.SIZE_1K).getBytes();
		
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		request.setContent(content);
		
		if(!rest) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, operazione);
		}
		
		if(relatesTo!=null) {
			request.addHeader("GovWay-Relates-To", relatesTo);
		}
		
		setCredenziali(api, operazione, request); 
		
		request.setUrl(url);
		
		HttpResponse response = HttpUtilities.httpInvoke(request);

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		verifyOk(response, contentType, 200); // il codice http e' gia' stato impostato
		
		return response;
	}
	
	private static void setCredenziali(String api, String operazione, HttpRequest request) throws UtilsException {
		if(SecretsTest.API_PROPRIETA.equals(api) &&
				(SecretsTest.OP_PROPRIETA_APPLICATIVO.equals(operazione) || SecretsTest.OP_PROPRIETA_SOGGETTO.equals(operazione)) 
					){
			request.setUsername("TestVaultApplicativoProprieta");
			request.setPassword("123456");
		}
		else if(SecretsTest.API_CONNETTORI_APPLICATIVO.equals(api)){
			if(SecretsTest.OP_MODI_PATH.equals(operazione)) {
				request.setUsername("ApplicativoVaultModiPath");
			}
			else if(SecretsTest.OP_MODI_ARCHIVIO.equals(operazione)) {
				request.setUsername("ApplicativoVaultModiArchivio");
			}
			request.setPassword("123456");
		}
		else if(SecretsTest.API_TOKEN_POLICY_VALIDAZIONE.equals(api)){
			String token = null;
			if(SecretsTest.API_TOKEN_POLICY_VALIDAZIONE_OP_VALIDAZIONE_JWE.equals(operazione) ||
					SecretsTest.API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_JWE.equals(operazione)) {
				token = JWTUtils.buildJweTokenClient2(false);
			}
			else {
				token = JWTUtils.buildJwsTokenClient1(false);
			}
			request.addHeader(HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+token);
		}
	}
	
	public static void verifyOk(HttpResponse response, String contentTypeExpected, int code) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(contentTypeExpected, response.getHeaderFirstValue(HttpConstants.CONTENT_TYPE));
		
	}
}
