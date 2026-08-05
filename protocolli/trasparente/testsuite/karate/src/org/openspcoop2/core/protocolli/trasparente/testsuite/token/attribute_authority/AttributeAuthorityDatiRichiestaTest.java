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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.attribute_authority;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* AttributeAuthorityDatiRichiestaTest
*
* Verifica la sottosezione 'Dati Richiesta' della configurazione di una AttributeAuthority:
* - i parametri della url e gli header http indicati nella policy vengono inviati all'AA, risolvendo le parti dinamiche;
* - l'header 'Authorization' è definibile manualmente, non essendo attiva alcuna autenticazione client sulla policy;
* - i parametri e gli header http concorrono alla chiave della cache degli attributi, ad eccezione dei nomi indicati
*   nella black list 'org.openspcoop2.pdd.gestioneAttributeAuthority.cacheKeyBlackList.*'
*   (per default 'Authorization' e 'DPoP' tra gli header).
*
* Configurazione utilizzata: archivio 'datiRichiestaAATestBundle.zip'
* - policy AA 'TestAttributeAuthorityDatiRichiestaHeader': l'attributo 'attr' viene veicolato da un header http
*   definito nella sottosezione 'Dati Richiesta' e restituito dall'AA (echo del TestService), quindi il valore
*   dell'attributo rivela se l'AA è stata realmente invocata o se la risposta proviene dalla cache;
* - policy AA 'TestAttributeAuthorityDatiRichiestaParametro': come sopra, ma l'attributo viene veicolato
*   da un parametro della url;
* - erogazione 'TestAttributeAuthorityDatiRichiesta', con autorizzazione per contenuti sull'attributo 'attr'.
*
* @author Poli Andrea (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AttributeAuthorityDatiRichiestaTest extends ConfigLoader {

	public static final String API = "TestAttributeAuthorityDatiRichiesta";

	private static final String AZIONE_HEADER = "datiRichiestaHeader";
	private static final String AZIONE_PARAMETRO = "datiRichiestaParametro";

	/** Header di test tramite cui vengono valorizzate le parti dinamiche delle policy */
	private static final String HEADER_TEST_ATTRIBUTO_HEADER = "test-attr";
	private static final String HEADER_TEST_ATTRIBUTO_PARAMETRO = "test-attr-param";
	private static final String HEADER_TEST_AUTHORIZATION = "test-authz";

	/** Valore dell'attributo atteso dall'autorizzazione per contenuti definita sulle porte applicative */
	private static final String ATTRIBUTO_ATTESO = "valoreStatico1";
	private static final String ATTRIBUTO_MODIFICATO = "valoreStatico2";

	/** Valore atteso dall'AA per l'header 'Authorization', verificato dal TestService */
	private static final String AUTHORIZATION_ATTESO = "authzStatico1";


	/** I parametri della url e gli header http definiti nella policy vengono inviati all'AA, risolvendo le parti
	 *  dinamiche: l'attributo restituito, ottenuto dall'echo dell'header http, riporta il valore atteso.
	 *  Viene inoltre inviato l'header 'Authorization', definito manualmente nella policy e verificato dal TestService */
	@Test
	public void datiRichiestaHeader() throws Exception {

		resetCacheAttributi();

		invokeOk(AZIONE_HEADER, headersDefault(), ATTRIBUTO_ATTESO);

	}

	/** Come sopra, con l'attributo veicolato da un parametro della url */
	@Test
	public void datiRichiestaParametro() throws Exception {

		resetCacheAttributi();

		invokeOk(AZIONE_PARAMETRO, headersDefault(), ATTRIBUTO_ATTESO);

	}

	/** L'header 'Authorization' rientra nella black list della chiave della cache: modificandone il valore dinamico
	 *  gli attributi presenti in cache devono essere riutilizzati, senza invocare nuovamente l'AA.
	 *  Se l'header rientrasse nella chiave, la nuova invocazione verrebbe rifiutata dal TestService,
	 *  che si attende il valore statico, e gli attributi non sarebbero recuperabili */
	@Test
	public void cacheKeyBlackList_authorization() throws Exception {

		resetCacheAttributi();

		invokeOk(AZIONE_HEADER, headersDefault(), ATTRIBUTO_ATTESO);

		Map<String, String> headers = headersDefault();
		headers.put(HEADER_TEST_AUTHORIZATION, AUTHORIZATION_ATTESO+"-MODIFICATO");

		resetCacheEsitoAutorizzazione();
		invokeOk(AZIONE_HEADER, headers, ATTRIBUTO_ATTESO);

	}

	/** Un header http non presente nella black list concorre alla chiave della cache: modificandone il valore
	 *  dinamico l'AA viene invocata nuovamente e restituisce il nuovo valore dell'attributo,
	 *  che non supera l'autorizzazione per contenuti */
	@Test
	public void cacheKey_httpHeader() throws Exception {

		resetCacheAttributi();

		invokeOk(AZIONE_HEADER, headersDefault(), ATTRIBUTO_ATTESO);

		Map<String, String> headers = headersDefault();
		headers.put(HEADER_TEST_ATTRIBUTO_HEADER, ATTRIBUTO_MODIFICATO);

		resetCacheEsitoAutorizzazione();
		invokeKo(AZIONE_HEADER, headers, ATTRIBUTO_MODIFICATO);

	}

	/** Un parametro della url concorre alla chiave della cache: modificandone il valore dinamico l'AA viene
	 *  invocata nuovamente e restituisce il nuovo valore dell'attributo,
	 *  che non supera l'autorizzazione per contenuti */
	@Test
	public void cacheKey_parametro() throws Exception {

		resetCacheAttributi();

		invokeOk(AZIONE_PARAMETRO, headersDefault(), ATTRIBUTO_ATTESO);

		Map<String, String> headers = headersDefault();
		headers.put(HEADER_TEST_ATTRIBUTO_PARAMETRO, ATTRIBUTO_MODIFICATO);

		resetCacheEsitoAutorizzazione();
		invokeKo(AZIONE_PARAMETRO, headers, ATTRIBUTO_MODIFICATO);

	}


	private void resetCacheAttributi() {
		Utils.resetCacheAttributeAuthority(logCore);
		resetCacheEsitoAutorizzazione();
	}
	private void resetCacheEsitoAutorizzazione() {
		// l'esito dell'autorizzazione non deve essere riutilizzato tra le invocazioni,
		// altrimenti non sarebbe possibile osservare gli attributi recuperati nella seconda invocazione
		Utils.resetCacheAutorizzazione(logCore);
	}

	private Map<String, String> headersDefault() {
		Map<String, String> headers = new HashMap<>();
		headers.put(HEADER_TEST_ATTRIBUTO_HEADER, ATTRIBUTO_ATTESO);
		headers.put(HEADER_TEST_ATTRIBUTO_PARAMETRO, ATTRIBUTO_ATTESO);
		headers.put(HEADER_TEST_AUTHORIZATION, AUTHORIZATION_ATTESO);
		return headers;
	}

	private HttpResponse invokeOk(String azione, Map<String, String> headers, String attributoAtteso) throws Exception {
		HttpResponse response = invoke(azione, headers);
		RestTest.verifyOk(response, 200);
		verifyDB(response, EsitoTransazioneName.OK, attributoAtteso);
		return response;
	}

	private HttpResponse invokeKo(String azione, Map<String, String> headers, String attributoAtteso) throws Exception {
		HttpResponse response = invoke(azione, headers);
		RestTest.verifyKo(response, RestTest.AUTHORIZATION_CONTENT_DENY, 403, RestTest.AUTHORIZATION_CONTENT_DENY_MESSAGE);
		verifyDB(response, EsitoTransazioneName.ERRORE_AUTORIZZAZIONE, attributoAtteso);
		return response;
	}

	private HttpResponse invoke(String azione, Map<String, String> headers) throws Exception {

		String url = System.getProperty("govway_base_path")+"/SoggettoInternoTest/"+API+"/v1/"+azione;

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		request.setContent(Bodies.getJson(Bodies.SMALL_SIZE).getBytes());
		request.setUrl(url);
		for (Map.Entry<String, String> header : headers.entrySet()) {
			request.addHeader(header.getKey(), header.getValue());
		}

		return HttpUtilities.httpInvoke(request);
	}

	private void verifyDB(HttpResponse response, EsitoTransazioneName esito, String attributoAtteso) throws Exception {

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(esito);

		// il valore dell'attributo registrato rivela se l'AA è stata invocata nuovamente (valore aggiornato)
		// oppure se sono stati riutilizzati gli attributi presenti in cache (valore precedente)
		DBVerifier.verify(idTransazione, esitoExpected, null, "\"attr\":\""+attributoAtteso+"\"");

	}

}
