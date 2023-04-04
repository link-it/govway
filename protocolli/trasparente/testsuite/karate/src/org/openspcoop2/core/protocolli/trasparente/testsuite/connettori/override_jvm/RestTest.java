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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.override_jvm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {
	
	// configurazione valida con configurazione attesa con nome di default
	@Test
	public void erogazione_configFileDefault() throws Exception {
		_test(TipoServizio.EROGAZIONE,
				"test1", 
				null,
				"HttpsOverrideJvmConfigSoggetto1");
	}
	@Test
	public void fruizione_configFileDefault() throws Exception {
		_test(TipoServizio.FRUIZIONE, 
				"test1", 
				null,
				"HttpsOverrideJvmConfigSoggetto1");
	}
	
	
	// configurazione valida con configurazione attesa con nome dinamico (rispetto al nome dell'applicativo)
	@Test
	public void erogazione_configFileDinamica_applicativoMittente() throws Exception {
		_test(TipoServizio.EROGAZIONE,
				"test2", 
				null,
				"HttpsOverrideJvmConfigSoggetto1");
	}
	@Test
	public void fruizione_configFileDinamica_applicativoMittente() throws Exception {
		_test(TipoServizio.FRUIZIONE, 
				"test2", 
				null,
				"HttpsOverrideJvmConfigSoggetto1");
	}
	
	
	@Test
	public void erogazione_configFileDinamica_applicativoMittente_error() throws Exception {
		_test(TipoServizio.EROGAZIONE,
				"test2", 
				"Ricevuto un Problem Detail (RFC 7807) in seguito all''invio del messaggio applicativo: {\"type\":\"https://govway.org/handling-errors/403/AuthorizationContentDeny.html\",\"title\":\"AuthorizationContentDeny\",\"status\":403,\"detail\":\"Unauthorized request content\"",
				"HttpsOverrideJvmConfigSoggetto2");
	}
	@Test
	public void fruizione_configFileDinamica_applicativoMittente_error() throws Exception {
		_test(TipoServizio.FRUIZIONE, 
				"test2", 
				"Ricevuto un Problem Detail (RFC 7807) in seguito all''invio della busta di cooperazione: {\"type\":\"https://govway.org/handling-errors/403/AuthorizationContentDeny.html\",\"title\":\"AuthorizationContentDeny\",\"status\":403,\"detail\":\"Unauthorized request content\"",
				"HttpsOverrideJvmConfigSoggetto2");
	}
	
	
	
	// configurazione valida con configurazione attesa con nome dinamico (rispetto al nome dell'applicativo token)
	private static final String APPLICATIVO_TOKEN = "APPLICATIVO_TOKEN";
	@Test
	public void erogazione_configFileDinamica_applicativoTokenMittente() throws Exception {
		_test(TipoServizio.EROGAZIONE,
				"test3", 
				null,
				APPLICATIVO_TOKEN);
	}
	@Test
	public void fruizione_configFileDinamica_applicativoTokenMittente() throws Exception {
		_test(TipoServizio.FRUIZIONE, 
				"test3", 
				null,
				APPLICATIVO_TOKEN);
	}
	
	
	
	// configurazione valida con configurazione attesa con nome dinamico (rispetto al nome dell'applicativo) e repository di default
	@Test
	public void erogazione_configFileDinamica_applicativoMittente_repositoryDefault() throws Exception {
		_test(TipoServizio.EROGAZIONE,
				"test4", 
				null,
				"HttpsOverrideJvmConfigSoggetto1");
	}
	@Test
	public void fruizione_configFileDinamica_applicativoMittente_repositoryDefault() throws Exception {
		_test(TipoServizio.FRUIZIONE, 
				"test4", 
				null,
				"HttpsOverrideJvmConfigSoggetto1");
	}
	
	
	
	private static HttpResponse _test(
			TipoServizio tipoServizio, 
			String operazione, String msgErrore,
			String username) throws Exception {
		
		String API = "SoggettoInternoTest/TestHttpsOverrideJvmConfig/v1";

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/"+API+"/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/"+API+"/"+operazione;
		url=url+"?test="+operazione;
		
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
				
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		
		if(APPLICATIVO_TOKEN.equals(username)) {
			String token = org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token.Utilities.buildJWT(Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null, false);
			request.addHeader(HttpConstants.AUTHORIZATION,HttpConstants.AUTHORIZATION_PREFIX_BEARER+token);
		}
		else {
			request.setUsername(username);
			request.setPassword("123456");
		}
		
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		request.setUrl(url);
		
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		if(msgErrore!=null) {
			esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_APPLICATIVO);
		}

		if(msgErrore!=null) {
			response.addHeader(Headers.GovWayTransactionErrorType, "AuthorizationContentDeny"); // arrivando come errore applicativo non viene impostato l'header http
			org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.errori.RestTest.verifyKo(response, null, "AuthorizationContentDeny", 403, "Unauthorized request content");
		}
		else {
			assertEquals(200, response.getResultHTTPOperation());
			assertEquals(contentType, response.getContentType());
		}
		
		DBVerifier.verify(idTransazione, esitoExpected, msgErrore);
				
		return response;
		
	}
	
	
}
