/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.autenticazione;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione.Utilities;
import org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione.ValidazioneJWTTest;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* IpRichiedenteTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class CredenzialeMittenteTest extends ConfigLoader {

	
	private static String API = "gw/SOGDEFAULT:TestCredenzialeMittente:1";
	static {
		API = API.replace("SOGDEFAULT", DBVerifier.getSoggettoDefault());
	}
	private static final String INDIRIZZO_IP = "124.2.3.2";
	private static final String EVENTI = "##Out=200##";
	private static final String GRUPPI = "##AltroTag##TestCredenzialeMittente##";
	private static final String USERNAME = "ApplicativoSoggettoInternoTestFruitore1";
	private static final String TOKEN_ISSUER = "testAuthEnte";
	private static final String TOKEN_SUBJECT = "10623542342342323";
	private static final String TOKEN_CLIENT_ID = "18192.apps";
	private static final String TOKEN_USERNAME = "Utente di Prova";
	private static final String TOKEN_MAIL = "mariorossi@govway.org";
	
	private static final HashMap<TipoCredenzialeMittente, String> mappaCredenziali = new HashMap<>();
	static {
		mappaCredenziali.put(TipoCredenzialeMittente.API, API);
		mappaCredenziali.put(TipoCredenzialeMittente.CLIENT_ADDRESS, INDIRIZZO_IP);
		mappaCredenziali.put(TipoCredenzialeMittente.EVENTI, EVENTI);
		mappaCredenziali.put(TipoCredenzialeMittente.GRUPPI, GRUPPI);
		mappaCredenziali.put(TipoCredenzialeMittente.TRASPORTO, USERNAME);
		mappaCredenziali.put(TipoCredenzialeMittente.TOKEN_ISSUER, TOKEN_ISSUER);
		mappaCredenziali.put(TipoCredenzialeMittente.TOKEN_SUBJECT, TOKEN_SUBJECT);
		mappaCredenziali.put(TipoCredenzialeMittente.TOKEN_CLIENT_ID, TOKEN_CLIENT_ID);
		mappaCredenziali.put(TipoCredenzialeMittente.TOKEN_USERNAME, TOKEN_USERNAME);
		mappaCredenziali.put(TipoCredenzialeMittente.TOKEN_EMAIL, TOKEN_MAIL);
	}
	
	private static final String AZIONE_ALL = "all";
	
	
	// API
	@Test
	public void erogazioneApiIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.API, API);
	}
	@Test
	public void erogazioneApiNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, "api",
				TipoCredenzialeMittente.API, null);
	}
	@Test
	public void fruizioneApiIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.API, API);
	}
	@Test
	public void frizioneApiNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, "api",
				TipoCredenzialeMittente.API, null);
	}
	
	
	
	
	// Client Address
	@Test
	public void erogazioneClientAddressIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.CLIENT_ADDRESS, INDIRIZZO_IP);
	}
	@Test
	public void erogazioneClientAddressNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, "client_address",
				TipoCredenzialeMittente.CLIENT_ADDRESS, null);
	}
	@Test
	public void fruizioneClientAddressIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.CLIENT_ADDRESS, INDIRIZZO_IP);
	}
	@Test
	public void frizioneClientAddressNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, "client_address",
				TipoCredenzialeMittente.CLIENT_ADDRESS, null);
	}

	
	
	
	
	// EVENTI
	@Test
	public void erogazioneEventiIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.EVENTI, EVENTI);
	}
	@Test
	public void erogazioneEventiNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, "eventi",
				TipoCredenzialeMittente.EVENTI, null);
	}
	@Test
	public void fruizioneEventiIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.EVENTI, EVENTI);
	}
	@Test
	public void frizioneEventiNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, "eventi",
				TipoCredenzialeMittente.EVENTI, null);
	}
	
	
	
	
	
	// GRUPPI
	@Test
	public void erogazioneGruppiIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.GRUPPI, GRUPPI);
	}
	@Test
	public void erogazioneGruppiNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, "gruppi",
				TipoCredenzialeMittente.GRUPPI, null);
	}
	@Test
	public void fruizioneGruppiIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.GRUPPI, GRUPPI);
	}
	@Test
	public void frizioneGruppiNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, "gruppi",
				TipoCredenzialeMittente.GRUPPI, null);
	}
	
	
	
	
	
	// TRASPORTO
	@Test
	public void erogazioneTrasportoIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.TRASPORTO, USERNAME);
	}
	@Test
	public void erogazioneTrasportoNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, "trasporto",
				TipoCredenzialeMittente.TRASPORTO, null);
	}
	@Test
	public void fruizioneTrasportoIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.TRASPORTO, USERNAME);
	}
	@Test
	public void frizioneTrasportoNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, "trasporto",
				TipoCredenzialeMittente.TRASPORTO, null);
	}
	
	
	
	
	
	// TOKEN ISSUER
	@Test
	public void erogazioneTokenIssuerIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.TOKEN_ISSUER, TOKEN_ISSUER);
	}
	@Test
	public void erogazioneTokenIssuerNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, "token_issuer",
				TipoCredenzialeMittente.TOKEN_ISSUER, null);
	}
	@Test
	public void fruizioneTokenIssuerIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.TOKEN_ISSUER, TOKEN_ISSUER);
	}
	@Test
	public void frizioneTokenIssuerNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, "token_issuer",
				TipoCredenzialeMittente.TOKEN_ISSUER, null);
	}
	
	
	
	
	// TOKEN SUBJECT
	@Test
	public void erogazioneTokenSubjectIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.TOKEN_SUBJECT, TOKEN_SUBJECT);
	}
	@Test
	public void erogazioneTokenSubjectNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, "token_subject",
				TipoCredenzialeMittente.TOKEN_SUBJECT, null);
	}
	@Test
	public void fruizioneTokenSubjectIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.TOKEN_SUBJECT, TOKEN_SUBJECT);
	}
	@Test
	public void frizioneTokenSubjectNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, "token_subject",
				TipoCredenzialeMittente.TOKEN_SUBJECT, null);
	}
	
	
	
	
	// TOKEN CLIENT_ID
	@Test
	public void erogazioneTokenClientIdIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.TOKEN_CLIENT_ID, TOKEN_CLIENT_ID);
	}
	@Test
	public void erogazioneTokenClientIdNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, "token_clientId",
				TipoCredenzialeMittente.TOKEN_CLIENT_ID, null);
	}
	@Test
	public void fruizioneTokenClientIdIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.TOKEN_CLIENT_ID, TOKEN_CLIENT_ID);
	}
	@Test
	public void frizioneTokenClientIdNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, "token_clientId",
				TipoCredenzialeMittente.TOKEN_CLIENT_ID, null);
	}
	
	
	
	
	// TOKEN USERNAME
	@Test
	public void erogazioneTokenUsernameIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.TOKEN_USERNAME, TOKEN_USERNAME);
	}
	@Test
	public void erogazioneTokenUsernameNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, "token_username",
				TipoCredenzialeMittente.TOKEN_USERNAME, null);
	}
	@Test
	public void fruizioneTokenUsernameIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.TOKEN_USERNAME, TOKEN_USERNAME);
	}
	@Test
	public void frizioneTokenUsernameNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, "token_username",
				TipoCredenzialeMittente.TOKEN_USERNAME, null);
	}
	
	
	
	
	// TOKEN EMAIL
	@Test
	public void erogazioneTokenMailIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.TOKEN_EMAIL, TOKEN_MAIL);
	}
	@Test
	public void erogazioneTokenMailNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.EROGAZIONE, "token_eMail",
				TipoCredenzialeMittente.TOKEN_EMAIL, null);
	}
	@Test
	public void fruizioneTokenMailIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, AZIONE_ALL,
				TipoCredenzialeMittente.TOKEN_EMAIL, TOKEN_MAIL);
	}
	@Test
	public void frizioneTokenMailNonIndicizzato() throws UtilsException, ProtocolException {
		testEngine(TipoServizio.FRUIZIONE, "token_eMail",
				TipoCredenzialeMittente.TOKEN_EMAIL, null);
	}
	
	
	
	
	


	// ====================================================================
	// Test Engine
	// ====================================================================

	public static HttpResponse testEngine(
			TipoServizio tipoServizio,
			String operazione,
			TipoCredenzialeMittente credenzialeMittente, String valoreAttesoIndicizzato) throws UtilsException, ProtocolException {

		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.X_FORWARDED_FOR, INDIRIZZO_IP);

		List<String> mapExpectedTokenInfo = new ArrayList<>();
		headers.put("test-username", Utilities.username);
		
		String api = "TestCredenzialeMittente";

		String contentType=null;
		byte[]content=null;
		contentType = HttpConstants.CONTENT_TYPE_JSON;
		content=Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+operazione;

		try {
			url = url + "?access_token=" +
				ValidazioneJWTTest.buildJWS(true, 
						mapExpectedTokenInfo);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
		HttpRequest request = new HttpRequest();

		request.setUsername(USERNAME);
		request.setPassword("123456");
		
		request.setReadTimeout(20000);


		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);

		request.setContent(content);

		request.setUrl(url);

		for (Map.Entry<String,String> entry : headers.entrySet()) {
			request.addHeader(entry.getKey(), entry.getValue());
		}

		HttpResponse response = HttpUtilities.httpInvoke(request);

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);

		assertEquals(200, response.getResultHTTPOperation());
		assertEquals(contentType, response.getContentType());

		DBVerifier.verify(idTransazione, esitoExpected);

		DBVerifier.verifyCredenziale(idTransazione,
				credenzialeMittente, valoreAttesoIndicizzato);

		@SuppressWarnings("unchecked")
		Map<TipoCredenzialeMittente, String> mappaCredenzialiCheck = (HashMap<TipoCredenzialeMittente, String>) mappaCredenziali.clone();
		mappaCredenzialiCheck.remove(credenzialeMittente);
		for (Map.Entry<TipoCredenzialeMittente,String> entry: mappaCredenzialiCheck.entrySet()) {
			DBVerifier.verifyCredenziale(idTransazione,
					entry.getKey(), entry.getValue());
		}
		
		return response;
	}
}
