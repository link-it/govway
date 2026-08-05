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

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* NegoziazioneDatiRichiestaTest
*
* Verifica la sezione 'Dati Richiesta' della token policy di negoziazione:
* - i parametri e gli header http indicati nella policy vengono inviati al token endpoint, risolvendo le parti dinamiche;
* - l'header 'Authorization' è definibile manualmente, non essendo attiva alcuna autenticazione client sulla policy;
* - i parametri e gli header http concorrono alla chiave della cache, ad eccezione dei nomi indicati nella black list
*   'org.openspcoop2.pdd.retrieveToken.cacheKeyBlackList.*' (per default 'Authorization' e 'DPoP' tra gli header).
*
* Configurazione utilizzata: archivio 'datiRichiestaTestBundle.zip'
* - policy di negoziazione 'TestNegoziazioneDatiRichiesta'
* - erogazione 'TestNegoziazioneTokenDatiRichiesta' (invocata dal test)
* - erogazione 'TestNegoziazioneDatiRichiestaTokenEndpoint' (authorization server dummy che verifica, tramite
*   autorizzazione per contenuti, i parametri e gli header http ricevuti confrontandoli con valori statici)
*
* @author Poli Andrea (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class NegoziazioneDatiRichiestaTest extends ConfigLoader {

	public static final String API_NEGOZIAZIONE = "TestNegoziazioneTokenDatiRichiesta";
	public static final String AZIONE = "datiRichiesta";

	/** Valori attesi dall'authorization server dummy; devono combaciare con le proprietà di autorizzazione
	 *  per contenuti definite sulla porta applicativa 'TestNegoziazioneDatiRichiestaTokenEndpoint' */
	private static final String VALORE_ATTESO_PARAMETRO = "paramStatico1";
	private static final String VALORE_ATTESO_HEADER = "headerStatico1";
	private static final String VALORE_ATTESO_AUTHORIZATION = "authzStatico1";

	/** Header di test tramite cui vengono valorizzate le parti dinamiche della policy */
	private static final String HEADER_TEST_PARAMETRO = "test-q2";
	private static final String HEADER_TEST_HEADER = "test-h2";
	private static final String HEADER_TEST_AUTHORIZATION = "test-authz";

	/** Risposta del token endpoint: pre-scritta su /tmp e restituita dall'echo (destFile),
	 *  come già avviene per introspection/userInfo */
	private static final File TOKEN_RESPONSE_FILE = new File("/tmp/negoziazioneDatiRichiestaTokenResponse.json");
	private static final String TOKEN_RESPONSE = "{\"access_token\":\"2YotnFZFEjr1zCsicMWpAA\",\"token_type\":\"Bearer\",\"expires_in\":3600,\"scope\":\"scope1\"}";

	/** Diagnostico emesso quando l'authorization server dummy rifiuta la richiesta, poichè i dati ricevuti
	 *  non corrispondono a quelli attesi. Il carattere '%' viene interpretato come wildcard nella ricerca sul database */
	private static final String DIAGNOSTICO_DATI_RICHIESTA_NON_ATTESI = "Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny";


	/** I parametri e gli header http definiti nella policy vengono inviati al token endpoint,
	 *  risolvendo le parti dinamiche; l'header 'Authorization' è definito manualmente nella policy */
	@Test
	public void datiRichiesta() throws Exception {

		prepare();

		// viene verificato anche l'invio dell'header 'Authorization', definito manualmente nella policy
		_negoziazioneOk(headersDefault(),
				"\"Authorization\":\"Bearer "+VALORE_ATTESO_AUTHORIZATION+"\"");

	}

	/** L'header 'Authorization' rientra nella black list della chiave della cache: modificandone il valore
	 *  dinamico il token presente in cache deve essere riutilizzato, senza effettuare una nuova negoziazione */
	@Test
	public void cacheKeyBlackList_authorization() throws Exception {

		prepare();

		HttpResponse response = _negoziazioneOk(headersDefault());
		String idTransazioneNegoziazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazioneNegoziazione);

		// Il valore dinamico dell'header 'Authorization' viene modificato: non essendo parte della chiave della cache,
		// non viene effettuata una nuova negoziazione e l'informazione sul token riporta l'identificativo
		// della transazione in cui il token è stato originariamente negoziato.
		// Se l'header rientrasse nella chiave, la nuova negoziazione verrebbe rifiutata dall'authorization server
		// dummy, che si attende il valore statico, e il test terminerebbe con un errore di negoziazione.
		Map<String, String> headers = headersDefault();
		headers.put(HEADER_TEST_AUTHORIZATION, VALORE_ATTESO_AUTHORIZATION+"-MODIFICATO");

		_negoziazioneOk(headers, "\"transactionId\":\""+idTransazioneNegoziazione+"\"");

	}

	/** Un header http non presente nella black list concorre alla chiave della cache: modificandone il valore
	 *  dinamico viene effettuata una nuova negoziazione, rifiutata dall'authorization server dummy */
	@Test
	public void cacheKey_httpHeader() throws Exception {

		prepare();

		_negoziazioneOk(headersDefault());

		Map<String, String> headers = headersDefault();
		headers.put(HEADER_TEST_HEADER, VALORE_ATTESO_HEADER+"-MODIFICATO");

		_negoziazioneErrore(headers);

	}

	/** Un parametro concorre alla chiave della cache: modificandone il valore dinamico viene effettuata
	 *  una nuova negoziazione, rifiutata dall'authorization server dummy */
	@Test
	public void cacheKey_parametro() throws Exception {

		prepare();

		_negoziazioneOk(headersDefault());

		Map<String, String> headers = headersDefault();
		headers.put(HEADER_TEST_PARAMETRO, VALORE_ATTESO_PARAMETRO+"-MODIFICATO");

		_negoziazioneErrore(headers);

	}


	private void prepare() throws Exception {
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		FileSystemUtilities.writeFile(TOKEN_RESPONSE_FILE, TOKEN_RESPONSE.getBytes());
	}

	private Map<String, String> headersDefault() {
		Map<String, String> headers = new HashMap<>();
		headers.put(HEADER_TEST_PARAMETRO, VALORE_ATTESO_PARAMETRO);
		headers.put(HEADER_TEST_HEADER, VALORE_ATTESO_HEADER);
		headers.put(HEADER_TEST_AUTHORIZATION, VALORE_ATTESO_AUTHORIZATION);
		return headers;
	}

	private HttpResponse _negoziazioneOk(Map<String, String> headers, String ... tokenInfoCheck) throws Exception {

		String [] check = new String[tokenInfoCheck.length+8];
		int i = 0;
		check[i++] = "\"type\":\"retrieved_token\"";
		check[i++] = "\"grantType\":\"clientCredentials\"";
		check[i++] = "\"policy\":\"TestNegoziazioneDatiRichiesta\"";
		check[i++] = "\"accessToken\":\"2YotnFZFEjr1zCsicMWpAA\"";
		// parametri ed header http inviati al token endpoint, con le parti dinamiche risolte
		check[i++] = "\"q1\":\"v1\"";
		check[i++] = "\"q2\":\""+VALORE_ATTESO_PARAMETRO+"\"";
		check[i++] = "\"h1\":\"v1\"";
		check[i++] = "\"h2\":\""+VALORE_ATTESO_HEADER+"\"";
		for (String tokenInfo : tokenInfoCheck) {
			check[i++] = tokenInfo;
		}

		return NegoziazioneTest._test(logCore, API_NEGOZIAZIONE, AZIONE, headers,
				false,
				null,
				check);
	}

	private HttpResponse _negoziazioneErrore(Map<String, String> headers) throws Exception {
		return NegoziazioneTest._test(logCore, API_NEGOZIAZIONE, AZIONE, headers,
				true,
				DIAGNOSTICO_DATI_RICHIESTA_NON_ATTESI);
	}

}
