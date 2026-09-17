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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.negoziazione;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* NegoziazioneClaimsOpzionaliTest
*
* Verifica i claims della client assertion definiti tramite un placeholder opzionale '?{...}':
* - se il placeholder non è risolvibile, il claim non deve essere generato; in particolare non deve
*   essere generato con un valore vuoto, poichè un claim vuoto viene rifiutato da diversi authorization server;
* - se il placeholder è solamente una parte del valore, il claim viene generato con la restante parte;
* - se il placeholder è risolvibile, il claim viene generato con il valore risolto.
*
* Configurazione utilizzata: archivio 'trasparenteTestBundle.zip'
* - token policy di negoziazione 'TestNegoziazioneSignedJWT2', nel cui elenco dei claims sono definiti
*   'cOpt' e 'cOptParziale' tramite il placeholder opzionale '?{header:test-claim-opzionale}';
* - erogazione 'TestNegoziazioneToken', azione 'signedJWT2' (le stesse utilizzate da {@link NegoziazioneTest}).
*
* @author Poli Andrea (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class NegoziazioneClaimsOpzionaliTest extends ConfigLoader {

	private static final String AZIONE = "signedJWT2";

	/** Claims definiti nella policy tramite il placeholder opzionale */
	private static final String CLAIM_OPZIONALE = "cOpt";
	private static final String CLAIM_OPZIONALE_PARZIALE = "cOptParziale";
	private static final String CLAIM_OPZIONALE_PARZIALE_PREFISSO = "prefisso-";

	/** Header di test tramite cui viene risolto il placeholder opzionale */
	private static final String HEADER_TEST_CLAIM_OPZIONALE = "test-claim-opzionale";
	private static final String VALORE_CLAIM_OPZIONALE = "valoreOpzionale1";

	/** La firma della client assertion viene oscurata nelle informazioni registrate sulla transazione */
	private static final String PREFIX_CLIENT_ASSERTION = "\"jwtClientAssertion\":{\"token\":\"";


	/** Il placeholder opzionale non è risolvibile, non essendo stato inviato l'header di test:
	 *  il claim non deve essere presente nella client assertion */
	@Test
	public void claimOpzionaleNonRisolto() throws Exception {

		Utils.resetCacheToken(logCore);

		String payload = negoziaEdEstraiPayloadClientAssertion(headersDefault());

		assertFalse("Claim '"+CLAIM_OPZIONALE+"' non atteso nella client assertion; payload: "+payload,
				payload.contains("\""+CLAIM_OPZIONALE+"\""));

		// il placeholder è solamente una parte del valore: il claim viene generato con la restante parte
		assertTrue("Claim '"+CLAIM_OPZIONALE_PARZIALE+"' atteso nella client assertion; payload: "+payload,
				payload.contains("\""+CLAIM_OPZIONALE_PARZIALE+"\":\""+CLAIM_OPZIONALE_PARZIALE_PREFISSO+"\""));

	}

	/** Il placeholder opzionale è risolvibile: i claims vengono generati con il valore risolto */
	@Test
	public void claimOpzionaleRisolto() throws Exception {

		Utils.resetCacheToken(logCore);

		Map<String, String> headers = headersDefault();
		headers.put(HEADER_TEST_CLAIM_OPZIONALE, VALORE_CLAIM_OPZIONALE);

		String payload = negoziaEdEstraiPayloadClientAssertion(headers);

		assertTrue("Claim '"+CLAIM_OPZIONALE+"' atteso nella client assertion; payload: "+payload,
				payload.contains("\""+CLAIM_OPZIONALE+"\":\""+VALORE_CLAIM_OPZIONALE+"\""));

		assertTrue("Claim '"+CLAIM_OPZIONALE_PARZIALE+"' atteso nella client assertion; payload: "+payload,
				payload.contains("\""+CLAIM_OPZIONALE_PARZIALE+"\":\""+CLAIM_OPZIONALE_PARZIALE_PREFISSO+VALORE_CLAIM_OPZIONALE+"\""));

	}


	/** Header richiesti dalla policy 'TestNegoziazioneSignedJWT2' e dall'authorization server dummy;
	 *  l'header che risolve il placeholder opzionale non viene volutamente inviato */
	private Map<String, String> headersDefault() {
		Map<String, String> headers = new HashMap<>();
		headers.put("test-azione", AZIONE);
		headers.put("test-suffix", "DYNAMIC");
		headers.put("test-decode-position", "1");
		headers.put("test-p2", "testNegoziazioneP2");
		return headers;
	}

	private String negoziaEdEstraiPayloadClientAssertion(Map<String, String> headers) throws Exception {

		HttpResponse response = NegoziazioneTest._test(logCore, NegoziazioneTest.api_negoziazione, AZIONE, headers,
				false,
				null,
				"\"policy\":\"TestNegoziazioneSignedJWT2\"",
				PREFIX_CLIENT_ASSERTION);

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		String tokenInfo = DBVerifier.readTokenInfo(idTransazione);

		int index = tokenInfo.indexOf(PREFIX_CLIENT_ASSERTION);
		assertTrue("Client assertion non presente nelle informazioni registrate sulla transazione: "+tokenInfo, index>=0);
		String token = tokenInfo.substring(index+PREFIX_CLIENT_ASSERTION.length());
		token = token.substring(0, token.indexOf("\""));

		String [] split = token.split("\\.");
		assertTrue("Client assertion '"+token+"' non riconosciuta come JWT", split!=null && split.length>1);

		return new String(Base64Utilities.decode(split[1]));

	}

}
