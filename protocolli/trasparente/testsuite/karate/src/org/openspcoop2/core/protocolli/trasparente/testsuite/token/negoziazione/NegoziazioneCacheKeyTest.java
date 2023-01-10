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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.negoziazione;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* NegoziazioneTestCacheKey
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class NegoziazioneCacheKeyTest extends ConfigLoader {

	@Test
	public void signedJWT_PDND_endpoint() throws Exception {
		
		// endpoint viene cachato; 
		// la parte dinamica risiede nella url della richiesta
		// per il motivo di cui sopra si attende una nuova negoziazione, e poichè l'azione non viene riconosciuta un errore 404
		_cacheKey_key("test-azione", 
				true, true, 
				true, true);
		
	}
	
	
	@Test
	public void signedJWT_PDND_kid() throws Exception {
		
		// kid viene cachato; 
		// la parte dinamica risiede nell'header
		// il test sull'header fallisce poichè vi è un controllo sul valore nel controllo degli accessi
		// poichè il kid fa parte della chiave di cache, nel caso del payload si attende invece una nuova sessione 
		_cacheKey_key("test-kid", 
				true, false, 
				false, true);
		
	}
	
	@Test
	public void signedJWT_PDND_issuer() throws Exception {
		
		// issuer viene cachato; 
		// la parte dinamica risiede nel payload
		// poichè l'issuer fa parte della chiave di cache, nel caso dell'header si attende una nuova sessione 
		// il test sul payload fallisce poichè vi è un controllo sul valore nel controllo degli accessi
		_cacheKey_key("test-issuer", 
				false, true, 
				true, false);
		
	}
	
	@Test
	public void signedJWT_PDND_subject() throws Exception {
		
		// subject viene cachato; 
		// la parte dinamica risiede nel payload
		// poichè il subject fa parte della chiave di cache, nel caso dell'header si attende una nuova sessione 
		// il test sul payload fallisce poichè vi è un controllo sul valore nel controllo degli accessi
		_cacheKey_key("test-subject", 
				false, true, 
				true, false);
		
	}
	
	@Test
	public void signedJWT_PDND_clientId() throws Exception {
		
		// clientId viene cachato; 
		// la parte dinamica risiede nel payload
		// poichè il clientId fa parte della chiave di cache, nel caso dell'header si attende una nuova sessione 
		// il test sul payload fallisce poichè vi è un controllo sul valore nel controllo degli accessi
		_cacheKey_key("test-clientId", 
				false, true, 
				true, false);
		
	}
	
	@Test
	public void signedJWT_PDND_aud() throws Exception {
		
		// aud viene cachato; 
		// la parte dinamica risiede nel payload
		// poichè il aud fa parte della chiave di cache, nel caso dell'header si attende una nuova sessione 
		// il test sul payload fallisce poichè vi è un controllo sul valore nel controllo degli accessi
		_cacheKey_key("test-aud", 
				false, true, 
				true, false);
		
	}
	
	@Test
	public void signedJWT_PDND_jti() throws Exception {
		
		// jti non viene cachato; 
		// la parte dinamica risiede nel payload
		// poichè il jti non fa parte della chiave di cache, nel caso dell'header si attende la stessa sessione 
		// il test sul payload non dovrebbe fallire poichè il jti non fa parte della chiave di cache e quindi viene usato il precedente token negoziato.
		_cacheKey_key("test-jti", 
				false, false, 
				false, false);
		
	}
	
	@Test
	public void signedJWT_PDND_purposeId() throws Exception {
		
		// purposeId viene cachato; 
		// la parte dinamica risiede nel payload
		// poichè il purposeId fa parte della chiave di cache, nel caso dell'header si attende una nuova sessione 
		// il test sul payload fallisce poichè vi è un controllo sul valore nel controllo degli accessi
		_cacheKey_key("test-purposeId", 
				false, true, 
				true, false);
		
	}
	
	@Test
	public void signedJWT_PDND_sessionInfo() throws Exception {
		
		// sessionInfo non viene cachato; 
		// la parte dinamica risiede nel payload
		// poichè il sessionInfo non fa parte della chiave di cache, nel caso dell'header si attende la stessa sessione 
		// il test sul payload non dovrebbe fallire poichè il sessionInfo non fa parte della chiave di cache e quindi viene usato il precedente token negoziato.
		_cacheKey_key("test-sessionInfo", 
				false, false, 
				false, false);
		
	}
	
	@Test
	public void signedJWT_PDND_claim() throws Exception {
		
		// claim viene cachato; 
		// la parte dinamica risiede nel payload
		// poichè il claim fa parte della chiave di cache, nel caso dell'header si attende una nuova sessione 
		// il test sul payload fallisce poichè vi è un controllo sul valore nel controllo degli accessi
		_cacheKey_key("test-claim", 
				false, true, 
				true, false);
		
	}
	
	@Test
	public void signedJWT_PDND_formScope() throws Exception {
		
		// scope viene cachato; 
		// la parte dinamica risiede nei parametri della richiesta form
		// per il motivo di cui sopra si attende una nuova negoziazione che però fallirà poichè vi è un controllo degli accessi che verifica lo scope
		_cacheKey_key("test-scope", 
				true, true, 
				true, true);
		
	}
	
	@Test
	public void signedJWT_PDND_formClientId() throws Exception {
		
		// formClientId viene cachato; 
		// la parte dinamica risiede nei parametri della richiesta form
		// per il motivo di cui sopra si attende una nuova negoziazione che però fallirà poichè vi è un controllo degli accessi che verifica il form client id
		_cacheKey_key("test-formClientId", 
				true, true, 
				true, true);
		
	}
	
	@Test
	public void signedJWT_PDND_formParameters() throws Exception {
		
		// p2 viene cachato; 
		// la parte dinamica risiede nei parametri della richiesta form
		// per il motivo di cui sopra si attende una nuova negoziazione che però fallirà poichè vi è un controllo degli accessi che verifica i parameters
		_cacheKey_key("test-p2", 
				true, true, 
				true, true);
		
	}
	
	
	private void _cacheKey_key(String header, 
			boolean attesoErroreHeader, boolean attesaNuovaSessioneHeader, 
			boolean attesoErrorePayload, boolean attesaNuovaSessionePayload) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		// Test HDR
		
		Map<String, String> headers_0 = new HashMap<String, String>();
		headers_0.put("test-azione", "signedJWT-PDND");
		
		headers_0.put("test-kid", "PDND");
		
		headers_0.put("test-issuer", "DYNAMIC");
		headers_0.put("test-subject", "DYNAMIC");
		headers_0.put("test-clientId", "DYNAMIC");
		headers_0.put("test-aud", "DYNAMIC");
		headers_0.put("test-jti", "DYNAMIC");
		headers_0.put("test-purposeId", "DYNAMIC");
		headers_0.put("test-sessionInfo", "DYNAMIC");
		headers_0.put("test-claim", "DYNAMIC");
		
		headers_0.put("test-scope", "DYNAMIC");
		headers_0.put("test-formClientId", "DYNAMIC");
		headers_0.put("test-p2", "testNegoziazioneP2");
				
		headers_0.put("test-suffix", "DYNAMIC");
		headers_0.put("test-decode-position", "0");
		
		HttpResponse response = NegoziazioneTest._test(logCore, NegoziazioneTest.api_negoziazione, "signedJWT-PDND", headers_0,
				false,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT-PDND\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-PDND\"");
		String idRichiestaOriginale_0 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		// serviva prima di implementare i parametri dinamici all'interno della chiave della cache.
		//org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		
		// Test PAYLOAD
		
		Map<String, String> headers_1 = new HashMap<String, String>();
		headers_1.put("test-azione", "signedJWT-PDND");
		
		headers_1.put("test-kid", "PDND");
		
		headers_1.put("test-issuer", "DYNAMIC");
		headers_1.put("test-subject", "DYNAMIC");
		headers_1.put("test-clientId", "DYNAMIC");
		headers_1.put("test-aud", "DYNAMIC");
		headers_1.put("test-jti", "DYNAMIC");
		headers_1.put("test-purposeId", "DYNAMIC");
		headers_1.put("test-sessionInfo", "DYNAMIC");
		headers_1.put("test-claim", "DYNAMIC");
		
		headers_1.put("test-scope", "DYNAMIC");
		headers_1.put("test-formClientId", "DYNAMIC");
		headers_1.put("test-p2", "testNegoziazioneP2");
		
		headers_1.put("test-suffix", "DYNAMIC");
		headers_1.put("test-decode-position", "1");
		
		response = NegoziazioneTest._test(logCore, NegoziazioneTest.api_negoziazione, "signedJWT-PDND", headers_1,
				false,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"rfc7523_x509\"",
				"\"jwtClientAssertion\":{\"token\":\"",
				"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT-PDND\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneSignedJWT-PDND\"");
		String idRichiestaOriginale_1 = response.getHeaderFirstValue("GovWay-Transaction-ID");
		
		
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		
		String oldValue_0 = headers_0.get(header);
		headers_0.put(header, oldValue_0+"-ERRATA");
		if(attesoErroreHeader) {
			NegoziazioneTest._test(logCore, NegoziazioneTest.api_negoziazione, "signedJWT-PDND", headers_0,
					true,
					header.equals("test-azione") ? 
							"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation"
							:
							"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny"
							);
		}
		else {
			if(attesaNuovaSessioneHeader) {
				response = NegoziazioneTest._test(logCore, NegoziazioneTest.api_negoziazione, "signedJWT-PDND", headers_0,
						false,
						"\"type\":\"retrieved_token\"",
						"\"grantType\":\"rfc7523_x509\"",
						"\"jwtClientAssertion\":{\"token\":\"",
						"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT-PDND\"",
						"\"accessToken\":\"",
						"\"expiresIn\":",
						"\"policy\":\"TestNegoziazioneSignedJWT-PDND\""
						);
				String nuovoId = response.getHeaderFirstValue("GovWay-Transaction-ID");
				long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
				DBVerifier.verify(nuovoId, esitoExpected, null, "\"transactionId\":\""+nuovoId+"\""); 
			}
			else {
				NegoziazioneTest._test(logCore, NegoziazioneTest.api_negoziazione, "signedJWT-PDND", headers_0,
						false,
						"\"type\":\"retrieved_token\"",
						"\"grantType\":\"rfc7523_x509\"",
						"\"jwtClientAssertion\":{\"token\":\"",
						"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT-PDND\"",
						"\"accessToken\":\"",
						"\"expiresIn\":",
						"\"policy\":\"TestNegoziazioneSignedJWT-PDND\"",
						"\"transactionId\":\""+idRichiestaOriginale_0+"\""
						);
			}
		}
		
		
		String oldValue_1 = headers_1.get(header);
		headers_1.put(header, oldValue_1+"-ERRATA");
		if(attesoErrorePayload) {
			NegoziazioneTest._test(logCore, NegoziazioneTest.api_negoziazione, "signedJWT-PDND", headers_1,
					true,
					header.equals("test-azione") ? 
							"Connessione terminata con errore (codice trasporto: 404)%UndefinedOperation"
							:
							"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny"
							);
		}
		else {
			if(attesaNuovaSessionePayload) {
				response = NegoziazioneTest._test(logCore, NegoziazioneTest.api_negoziazione, "signedJWT-PDND", headers_1,
						false,
						"\"type\":\"retrieved_token\"",
						"\"grantType\":\"rfc7523_x509\"",
						"\"jwtClientAssertion\":{\"token\":\"",
						"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT-PDND\"",
						"\"accessToken\":\"",
						"\"expiresIn\":",
						"\"policy\":\"TestNegoziazioneSignedJWT-PDND\""
						);
				String nuovoId = response.getHeaderFirstValue("GovWay-Transaction-ID");
				long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
				DBVerifier.verify(nuovoId, esitoExpected, null, "\"transactionId\":\""+nuovoId+"\""); 
			}
			else {
				NegoziazioneTest._test(logCore, NegoziazioneTest.api_negoziazione, "signedJWT-PDND", headers_1,
						false,
						"\"type\":\"retrieved_token\"",
						"\"grantType\":\"rfc7523_x509\"",
						"\"jwtClientAssertion\":{\"token\":\"",
						"\"endpoint\":\"http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerClientCredentialsDummy/v1/signedJWT-PDND\"",
						"\"accessToken\":\"",
						"\"expiresIn\":",
						"\"policy\":\"TestNegoziazioneSignedJWT-PDND\"",
						"\"transactionId\":\""+idRichiestaOriginale_1+"\""
						);
			}
		}
		
		
	}
	
}
