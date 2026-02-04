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
package org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.other.ocsp.DBVerifier;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.json.JsonPathNotFoundException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* JWSCompactPayloadEnrichmentTest
*
* Test per la funzionalità JWS-Compact Payload Enrichment.
*
* Flusso richiesta: Client -> [GovWay Sender enrich] -> [GovWay Receiver valida JWT e sbusta] -> echo
* Flusso risposta: echo -> [GovWay Sender enrich] -> [GovWay Receiver valida JWT e sbusta] -> Client
*
* Il client riceve sempre JSON (dopo sbustamento), quindi i test verificano i claim nel JSON ricevuto.
*
* Configurazione GovWay:
* - Audience: ${header:jw-test-audience}
* - Issuer: ${jsonPath:$.testiss}
* - TTL: ${query:jwt_test_ttl}
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JWSCompactPayloadEnrichmentTest extends ConfigLoader {

	private static final String API = "TestJWSPayloadEnrichment";

	private static final String PREFIX_FAULT = "GenerateFault-";
	
	// Claims JWT standard aggiunti dall'enrichment
	private static final String CLAIM_ISS = "iss";
	private static final String CLAIM_AUD = "aud";
	private static final String CLAIM_EXP = "exp";
	private static final String CLAIM_IAT = "iat";
	private static final String CLAIM_NBF = "nbf";
	private static final String CLAIM_JTI = "jti";

	// Claim applicativi di test (inviati dal client)
	private static final String CLAIM_APP_TEST = "testClaim";
	private static final String CLAIM_APP_TEST_VALUE = "testValue";

	// Claim usato per l'issuer (configurato come ${jsonPath:$.testiss})
	private static final String CLAIM_APP_ISS_TEST = "testiss";
	private static final String CLAIM_APP_ISS_TEST_VALUE = "testissValue";

	// Valori dei claim JWT inviati dall'applicativo (per test conflitto)
	private static final String CLAIM_ISS_APP_VALUE = "issuer-applicativo";
	private static final String CLAIM_AUD_APP_VALUE = "audience-applicativo";
	private static final long CLAIM_EXP_APP_VALUE = 9999999999L;
	private static final long CLAIM_IAT_APP_VALUE = 1000000000L;
	private static final long CLAIM_NBF_APP_VALUE = 1000000001L;
	private static final String CLAIM_JTI_APP_VALUE = "jti-applicativo-12345";

	// Header HTTP per audience (configurato come ${header:jw-test-audience})
	private static final String HEADER_AUDIENCE = "jw-test-audience";
	private static final String HEADER_AUDIENCE_VALUE = "test-audience";

	// Query Param per TTL (configurato come ${query:jwt_test_ttl})
	private static final String QUERY_PARAM_TTL = "jwt_test_ttl";
	private static final String QUERY_PARAM_TTL_VALUE = "60";

	// Valori attesi dopo l'enrichment (derivati dalla configurazione)
	// Issuer = valore di ${jsonPath:$.testiss} = CLAIM_APP_ISS_TEST_VALUE
	// Audience = valore di ${header:jw-test-audience} = HEADER_AUDIENCE_VALUE


	// ========== TEST RICHIESTA ==========

	@Test
	public void richiesta_preserva() throws Exception {
		// Invia payload con claim "iss" applicativo, la policy è "preserva" quindi il claim applicativo deve rimanere
		test("richiesta_preserva", true, ClaimsPolicy.PRESERVE);
	}

	@Test
	public void richiesta_sovrascrivi() throws Exception {
		// Invia payload con claim "iss" applicativo, la policy è "sovrascrivi" quindi il claim deve essere sovrascritto
		test("richiesta_sovrascrivi", true, ClaimsPolicy.OVERRIDE);
	}
	
	@Test
	public void richiesta_rifiuta_ok_content_type_jose_json() throws Exception {
		// Invia payload con claim "iss" applicativo, la policy è "sovrascrivi" quindi il claim deve essere sovrascritto
		test("richiesta_rifiuta", false, null);
	}

	@Test
	public void richiesta_rifiuta_iss() throws Exception {
		testErrore("richiesta_rifiuta", true, CLAIM_ISS, true);
	}

	@Test
	public void richiesta_rifiuta_aud() throws Exception {
		testErrore("richiesta_rifiuta", true, CLAIM_AUD, true);
	}

	@Test
	public void richiesta_rifiuta_exp() throws Exception {
		testErrore("richiesta_rifiuta", true, CLAIM_EXP, true);
	}

	@Test
	public void richiesta_rifiuta_iat() throws Exception {
		testErrore("richiesta_rifiuta", true, CLAIM_IAT, true);
	}

	@Test
	public void richiesta_rifiuta_nbf() throws Exception {
		testErrore("richiesta_rifiuta", true, CLAIM_NBF, true);
	}

	@Test
	public void richiesta_rifiuta_jti() throws Exception {
		testErrore("richiesta_rifiuta", true, CLAIM_JTI, true);
	}

	@Test
	public void richiesta_senza_conflitti() throws Exception {
		// Invia payload senza claim in conflitto, deve usare i valori configurati
		test("richiesta_preserva", false, null);
	}


	// ========== TEST RISPOSTA ==========

	@Test
	public void risposta_preserva() throws Exception {
		test("risposta_preserva", true, ClaimsPolicy.PRESERVE);
	}
	
	@Test
	public void risposta_preserva_applicabilita_non_attiva() throws Exception {
		// Con HTTP 500 la sicurezza non deve essere applicata (applicabilità configurata solo per 200)
		testApplicabilitaNonAttiva(PREFIX_FAULT + "risposta_preserva");
	}

	@Test
	public void risposta_sovrascrivi() throws Exception {
		test("risposta_sovrascrivi", true, ClaimsPolicy.OVERRIDE);
	}

	@Test
	public void risposta_sovrascrivi_applicabilita_non_attiva() throws Exception {
		testApplicabilitaNonAttiva(PREFIX_FAULT + "risposta_sovrascrivi");
	}
	
	@Test
	public void risposta_rifiuta_ok_content_type_jose_json() throws Exception {
		// Invia payload con claim "iss" applicativo, la policy è "sovrascrivi" quindi il claim deve essere sovrascritto
		test("risposta_rifiuta", false, null);
	}
	
	@Test
	public void risposta_rifiuta_iss() throws Exception {
		testErrore("risposta_rifiuta", true, CLAIM_ISS, false);
	}

	@Test
	public void risposta_rifiuta_aud() throws Exception {
		testErrore("risposta_rifiuta", true, CLAIM_AUD, false);
	}

	@Test
	public void risposta_rifiuta_exp() throws Exception {
		testErrore("risposta_rifiuta", true, CLAIM_EXP, false);
	}

	@Test
	public void risposta_rifiuta_iat() throws Exception {
		testErrore("risposta_rifiuta", true, CLAIM_IAT, false);
	}

	@Test
	public void risposta_rifiuta_nbf() throws Exception {
		testErrore("risposta_rifiuta", true, CLAIM_NBF, false);
	}

	@Test
	public void risposta_rifiuta_jti() throws Exception {
		testErrore("risposta_rifiuta", true, CLAIM_JTI, false);
	}

	@Test
	public void risposta_rifiuta_applicabilita_non_attiva() throws Exception {
		testApplicabilitaNonAttiva(PREFIX_FAULT + "risposta_rifiuta");
	}
	
	@Test
	public void risposta_senza_conflitti() throws Exception {
		test("risposta_preserva", false, null);
	}


	// ========== IMPLEMENTAZIONE TEST ==========

	private enum ClaimsPolicy {
		PRESERVE,
		OVERRIDE
	}

	private void test(String operazione, boolean withConflictingClaim, ClaimsPolicy policy) throws Exception {
		HttpResponse response = invokeAndVerifySuccess(operazione, withConflictingClaim);

		// Verifica Content-Type JSON
		String contentType = response.getContentType();
		assertNotNull("Content-Type non presente", contentType);
		assertTrue("Content-Type atteso application/json, trovato: " + contentType,
				contentType.contains("application/json"));

		// Parse della risposta JSON
		String jsonResponse = new String(response.getContent());
		assertNotNull("Risposta vuota", jsonResponse);

		// Verifica claim applicativi originali (devono essere sempre presenti)
		String testClaimValue = JsonPathExpressionEngine.extractAndConvertResultAsString(jsonResponse, "$." + CLAIM_APP_TEST, logCore);
		assertEquals("Claim applicativo '" + CLAIM_APP_TEST + "' non preservato", CLAIM_APP_TEST_VALUE, testClaimValue);

		String testIssClaimValue = JsonPathExpressionEngine.extractAndConvertResultAsString(jsonResponse, "$." + CLAIM_APP_ISS_TEST, logCore);
		assertEquals("Claim applicativo '" + CLAIM_APP_ISS_TEST + "' non preservato", CLAIM_APP_ISS_TEST_VALUE, testIssClaimValue);

		// Verifica claim standard JWT aggiunti dall'enrichment
		String issValue = JsonPathExpressionEngine.extractAndConvertResultAsString(jsonResponse, "$." + CLAIM_ISS, logCore);
		String audValue = JsonPathExpressionEngine.extractAndConvertResultAsString(jsonResponse, "$." + CLAIM_AUD, logCore);
		String expValue = JsonPathExpressionEngine.extractAndConvertResultAsString(jsonResponse, "$." + CLAIM_EXP, logCore);
		String iatValue = JsonPathExpressionEngine.extractAndConvertResultAsString(jsonResponse, "$." + CLAIM_IAT, logCore);
		String nbfValue = JsonPathExpressionEngine.extractAndConvertResultAsString(jsonResponse, "$." + CLAIM_NBF, logCore);
		String jtiValue = JsonPathExpressionEngine.extractAndConvertResultAsString(jsonResponse, "$." + CLAIM_JTI, logCore);

		assertNotNull("Claim '" + CLAIM_ISS + "' non presente", issValue);
		assertNotNull("Claim '" + CLAIM_AUD + "' non presente", audValue);
		assertNotNull("Claim '" + CLAIM_EXP + "' non presente", expValue);
		assertNotNull("Claim '" + CLAIM_IAT + "' non presente", iatValue);
		assertNotNull("Claim '" + CLAIM_NBF + "' non presente", nbfValue);
		assertNotNull("Claim '" + CLAIM_JTI + "' non presente", jtiValue);

		long exp = Long.parseLong(expValue);
		long iat = Long.parseLong(iatValue);
		long nbf = Long.parseLong(nbfValue);

		// Verifica comportamento policy per claim in conflitto
		if (withConflictingClaim) {
			if (policy == ClaimsPolicy.PRESERVE) {
				// Con policy "preserva", i claim applicativi devono rimanere
				assertEquals("Con policy PRESERVE il claim 'iss' applicativo deve rimanere",
						CLAIM_ISS_APP_VALUE, issValue);
				assertEquals("Con policy PRESERVE il claim 'aud' applicativo deve rimanere",
						CLAIM_AUD_APP_VALUE, audValue);
				assertEquals("Con policy PRESERVE il claim 'exp' applicativo deve rimanere",
						CLAIM_EXP_APP_VALUE, exp);
				assertEquals("Con policy PRESERVE il claim 'iat' applicativo deve rimanere",
						CLAIM_IAT_APP_VALUE, iat);
				assertEquals("Con policy PRESERVE il claim 'nbf' applicativo deve rimanere",
						CLAIM_NBF_APP_VALUE, nbf);
				assertEquals("Con policy PRESERVE il claim 'jti' applicativo deve rimanere",
						CLAIM_JTI_APP_VALUE, jtiValue);
			} else if (policy == ClaimsPolicy.OVERRIDE) {
				// Con policy "sovrascrivi", i claim devono essere sovrascritti con quelli generati dall'enrichment
				assertEquals("Con policy OVERRIDE il claim 'iss' deve essere sovrascritto",
						CLAIM_APP_ISS_TEST_VALUE, issValue);
				assertEquals("Con policy OVERRIDE il claim 'aud' deve essere sovrascritto",
						HEADER_AUDIENCE_VALUE, audValue);
				assertNotEquals("Con policy OVERRIDE il claim 'exp' deve essere sovrascritto",
						CLAIM_EXP_APP_VALUE, exp);
				assertNotEquals("Con policy OVERRIDE il claim 'iat' deve essere sovrascritto",
						CLAIM_IAT_APP_VALUE, iat);
				assertNotEquals("Con policy OVERRIDE il claim 'nbf' deve essere sovrascritto",
						CLAIM_NBF_APP_VALUE, nbf);
				assertNotEquals("Con policy OVERRIDE il claim 'jti' deve essere sovrascritto",
						CLAIM_JTI_APP_VALUE, jtiValue);

				// Verifica coerenza temporale per i claim sovrascritti
				assertTrue("Con policy OVERRIDE exp deve essere maggiore di iat", exp > iat);
				long ttl = exp - iat;
				long expectedTtl = Long.parseLong(QUERY_PARAM_TTL_VALUE);
				assertEquals("TTL deve corrispondere al valore del query param", expectedTtl, ttl);
			}
		} else {
			// Senza conflitti, tutti i claim devono avere i valori generati dall'enrichment
			assertEquals("Claim 'iss' deve avere valore da jsonPath:$.testiss", CLAIM_APP_ISS_TEST_VALUE, issValue);
			assertEquals("Claim 'aud' deve avere valore da header", HEADER_AUDIENCE_VALUE, audValue);

			// Verifica coerenza temporale
			assertTrue("exp deve essere maggiore di iat", exp > iat);
			long ttl = exp - iat;
			long expectedTtl = Long.parseLong(QUERY_PARAM_TTL_VALUE);
			assertEquals("TTL deve corrispondere al valore del query param", expectedTtl, ttl);
		}
	}

	private void testApplicabilitaNonAttiva(String operazione) throws Exception {
		HttpResponse response = invoke(operazione, false, null);

		// Verifica HTTP 500
		assertEquals("Atteso HTTP 500", 500, response.getResultHTTPOperation());

		// Verifica Content-Type application/problem+json
		String contentType = response.getContentType();
		assertNotNull("Content-Type non presente", contentType);
		assertTrue("Content-Type atteso application/problem+json, trovato: " + contentType,
				contentType.contains(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807));

		// Parse della risposta JSON
		String jsonResponse = new String(response.getContent());
		assertNotNull("Risposta vuota", jsonResponse);

		// Verifica presenza claim problem detail
		String typeValue = JsonPathExpressionEngine.extractAndConvertResultAsString(jsonResponse, "$.type", logCore);
		String titleValue = JsonPathExpressionEngine.extractAndConvertResultAsString(jsonResponse, "$.title", logCore);
		String detailValue = JsonPathExpressionEngine.extractAndConvertResultAsString(jsonResponse, "$.detail", logCore);

		assertNotNull("Claim 'type' del problem detail non presente", typeValue);
		assertNotNull("Claim 'title' del problem detail non presente", titleValue);
		assertNotNull("Claim 'detail' del problem detail non presente", detailValue);

		// Verifica ASSENZA claim JWT (la sicurezza non deve essere applicata per HTTP 500)
		verificaClaimAssente(jsonResponse, CLAIM_ISS);
		verificaClaimAssente(jsonResponse, CLAIM_AUD);
		verificaClaimAssente(jsonResponse, CLAIM_EXP);
		verificaClaimAssente(jsonResponse, CLAIM_IAT);
		verificaClaimAssente(jsonResponse, CLAIM_NBF);
		verificaClaimAssente(jsonResponse, CLAIM_JTI);

		// Verifica su DB
		String idTransazioneHeader = "GovWay-Transaction-ID";
		String idTransazione = response.getHeaderFirstValue(idTransazioneHeader);
		assertNotNull("Header " + idTransazioneHeader + " non presente", idTransazione);

		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME)
				.convertoToCode(EsitoTransazioneName.ERRORE_APPLICATIVO);

		DBVerifier.verify(idTransazione, esitoExpected);
	}

	private void testErrore(String operazione, boolean withConflictingClaim, String claimName, boolean request) throws Exception {
		HttpResponse response = invoke(operazione, withConflictingClaim, claimName);

		// Ci aspettiamo un errore perché la policy è "rifiuta con errore"
		assertNotEquals("Atteso errore per policy 'rifiuta con errore'", 200, response.getResultHTTPOperation());
		
		String idTransazioneHeader = "GovWay-Transaction-ID";
		String idTransazione = response.getHeaderFirstValue(idTransazioneHeader);
		assertNotNull("Header " + idTransazioneHeader + " non presente", idTransazione);
				
		EsitoTransazioneName eName = request ? EsitoTransazioneName.ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA : EsitoTransazioneName.ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA;
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME)
				.convertoToCode(eName);
		
		String tipo = request ? "richiesta" : "risposta";
		String errore = "Il claim '"+claimName+"' è già presente nella "+tipo;
	
		if(request) {
			DBVerifier.verify(idTransazione, esitoExpected, errore);
		}
		else {
			String idTransazionePeerHeader = "GovWay-Peer-Transaction-ID";
			String idTransazionePeer = response.getHeaderFirstValue(idTransazionePeerHeader);
			assertNotNull("Header Peer " + idTransazionePeerHeader + " non presente", idTransazionePeer);
			
			DBVerifier.verify(idTransazionePeer, esitoExpected, errore);
		}
	}

	private HttpResponse invokeAndVerifySuccess(String operazione, boolean withConflictingClaim) throws Exception {
		HttpResponse response = invoke(operazione, withConflictingClaim, null);

		assertEquals("Atteso HTTP 200", 200, response.getResultHTTPOperation());

		String idTransazioneHeader = "GovWay-Transaction-ID";
		String idTransazione = response.getHeaderFirstValue(idTransazioneHeader);
		assertNotNull("Header " + idTransazioneHeader + " non presente", idTransazione);

		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME)
				.convertoToCode(EsitoTransazioneName.OK);

		DBVerifier.verify(idTransazione, esitoExpected);

		return response;
	}

	private HttpResponse invoke(String operazione, boolean withConflictingClaim, String claimName) throws Exception {
		
		boolean fault = false;
		if(operazione.startsWith(PREFIX_FAULT)) {
			operazione = operazione.substring(PREFIX_FAULT.length());
			fault = true;
		}
		
		String soggetto = "SoggettoInternoTestFruitore";
		HttpRequestMethod method = HttpRequestMethod.POST;
		String contentType = HttpConstants.CONTENT_TYPE_JSON;

		// Costruisce il payload JSON con claim applicativi
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{\n");
		jsonBuilder.append("  \"").append(CLAIM_APP_TEST).append("\": \"").append(CLAIM_APP_TEST_VALUE).append("\",\n");
		jsonBuilder.append("  \"").append(CLAIM_APP_ISS_TEST).append("\": \"").append(CLAIM_APP_ISS_TEST_VALUE).append("\"");

		if (withConflictingClaim) {
			// Aggiunge i claim JWT standard con valori applicativi per testare conflitti
			java.util.List<String> conflictingClaims = new java.util.ArrayList<>();
			if (claimName == null || CLAIM_ISS.equals(claimName)) {
				conflictingClaims.add("  \"" + CLAIM_ISS + "\": \"" + CLAIM_ISS_APP_VALUE + "\"");
			}
			if (claimName == null || CLAIM_AUD.equals(claimName)) {
				conflictingClaims.add("  \"" + CLAIM_AUD + "\": \"" + CLAIM_AUD_APP_VALUE + "\"");
			}
			if (claimName == null || CLAIM_EXP.equals(claimName)) {
				conflictingClaims.add("  \"" + CLAIM_EXP + "\": " + CLAIM_EXP_APP_VALUE);
			}
			if (claimName == null || CLAIM_IAT.equals(claimName)) {
				conflictingClaims.add("  \"" + CLAIM_IAT + "\": " + CLAIM_IAT_APP_VALUE);
			}
			if (claimName == null || CLAIM_NBF.equals(claimName)) {
				conflictingClaims.add("  \"" + CLAIM_NBF + "\": " + CLAIM_NBF_APP_VALUE);
			}
			if (claimName == null || CLAIM_JTI.equals(claimName)) {
				conflictingClaims.add("  \"" + CLAIM_JTI + "\": \"" + CLAIM_JTI_APP_VALUE + "\"");
			}
			if (!conflictingClaims.isEmpty()) {
				jsonBuilder.append(",\n");
				jsonBuilder.append(String.join(",\n", conflictingClaims));
			}
		}

		jsonBuilder.append("\n}");
		byte[] content = jsonBuilder.toString().getBytes();

		String url = System.getProperty("govway_base_path") + "/out/" + soggetto + "/SoggettoInternoTest/" + API + "/v1/" + operazione;

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.addHeader(HEADER_AUDIENCE, HEADER_AUDIENCE_VALUE);
		request.setMethod(method);
		request.setContentType(contentType);
		request.setContent(content);
		request.setUrl(url + "?" + QUERY_PARAM_TTL + "=" + QUERY_PARAM_TTL_VALUE);
		if(fault) {
			request.setUrl(request.getUrl()+ "&" + "problem" + "=" + true);
			request.setUrl(request.getUrl()+ "&" + "returnCode" + "=" + 500);
		}

		return HttpUtilities.httpInvoke(request);
	}

	private void verificaClaimAssente(String jsonResponse, String claimName) {
		try {
			String value = JsonPathExpressionEngine.extractAndConvertResultAsString(jsonResponse, "$." + claimName, logCore);
			// Se arriviamo qui, il claim esiste (non dovrebbe)
			assertNull("Claim '" + claimName + "' non atteso (sicurezza non applicata per HTTP 500)", value);
		} catch (JsonPathNotFoundException e) {
			// Atteso: il claim non deve esistere
		} catch (Exception e) {
			throw new RuntimeException("Errore inatteso durante la verifica del claim '" + claimName + "': " + e.getMessage(), e);
		}
	}
}
