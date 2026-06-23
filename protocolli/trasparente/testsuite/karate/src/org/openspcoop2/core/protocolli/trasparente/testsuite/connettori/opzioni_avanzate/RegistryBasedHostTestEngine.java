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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * Verifica che il connettore HTTP riesca ad invocare un endpoint il cui host non e' conforme a
 * RFC 2396 (es. contenente underscore '_'), sia in erogazione che in fruizione.
 *
 * Contesto: {@code java.net.URI.getHost()} (RFC 2396) restituisce {@code null} per host
 * "registry-based" (es. con underscore); il connettore httpcore5 BIO costruiva l'host con
 * {@code HttpHost.create(URI)}, andando in {@code NullPointerException: Host name}. Il fix
 * (vedi {@code ConnettoreHTTPCOREUtils.buildHttpHost}) ricava l'host dall'authority della request,
 * gia' correttamente valorizzata (come gia' avviene nel path NIO e nella libreria URLConnection).
 * Questo test e' eseguito dalla tripla {@code HttpCoreBIO} / {@code HttpCoreNIO} / {@code UrlConnBIO}
 * per coprire tutte e tre le librerie; l'effettivo utilizzo della libreria attesa viene verificato
 * sui messaggi diagnostici (vedi {@link #test(boolean, String)}).
 *
 * <h2>Configurazione richiesta</h2>
 *
 * <h3>1) /etc/hosts</h3>
 * Aggiungere gli alias (host con underscore, single-label e dotted) che risolvono verso il loopback:
 * <pre>
 *   127.0.0.1   govway_hostname
 *   127.0.0.1   srv_test.govway_local
 * </pre>
 *
 * <h3>2) Console GovWay</h3>
 * Configurare la URL del connettore (sia dell'erogazione che della fruizione) come URL dinamica
 * che prende l'authority+path da un header HTTP fornito dal test:
 * <pre>
 *   http://${header:govway-testsuite-urldainvocare}
 * </pre>
 * <ul>
 *   <li>API: <code>{@value #API}</code> (v1), REST, con una risorsa che accetta l'invocazione sulla
 *       radice (metodo/path "qualsiasi"), in modo che GovWay non accodi alcun sub-path al connettore;</li>
 *   <li>Erogazione: erogatore <code>{@value #EROGATORE}</code>, connettore con la location dinamica sopra;</li>
 *   <li>Fruizione: fruitore <code>{@value #FRUITORE}</code> dell'API erogata da <code>{@value #EROGATORE}</code>,
 *       connettore con la location dinamica sopra.</li>
 * </ul>
 * In questo modo e' il test a fornire, via header {@value #HEADER_URL}, l'authority+path effettivi
 * dell'endpoint da invocare (es. <code>127.0.0.1:8080/TestService/echo</code> oppure
 * <code>govway_hostname:8080/TestService/echo</code>).
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RegistryBasedHostTestEngine extends ConfigLoader {

	/** API REST (v1) da configurare in console, con connettore a URL dinamica. */
	public static final String API = "TestRegistryBasedHost";
	/** Soggetto erogatore. */
	public static final String EROGATORE = "SoggettoInternoTest";
	/** Soggetto fruitore. */
	public static final String FRUITORE = "SoggettoInternoTestFruitore";
	/** Header HTTP letto dal connettore: contiene authority+path dell'endpoint da invocare. */
	public static final String HEADER_URL = "govway-testsuite-urldainvocare";

	/** Backend di test (echo) raggiungibile in loopback. */
	private static final String BACKEND_PATH = "/TestService/echo";
	private static final int BACKEND_PORT = 8080;

	/** Host conforme: indirizzo IP di loopback IPv4 (baseline). */
	private static final String HOST_CONFORME = "127.0.0.1";
	/** Host NON conforme (underscore), single-label: alias /etc/hosts verso il loopback. */
	private static final String HOST_UNDERSCORE = "govway_hostname";
	/** Host NON conforme (underscore), multi-label/dotted: alias /etc/hosts verso il loopback. */
	private static final String HOST_UNDERSCORE_MULTILABEL = "srv_test.govway_local";

	private HttpLibraryMode mode;
	public RegistryBasedHostTestEngine() {
		this.mode = null;
	}
	public RegistryBasedHostTestEngine(HttpLibraryMode mode) {
		this.mode = mode;
	}

	private String endpoint(String host) {
		return host + ":" + BACKEND_PORT + BACKEND_PATH;
	}

	// ---- EROGAZIONE ----

	@Test
	public void erogazioneHostConforme() throws Exception {
		test(false, endpoint(HOST_CONFORME));
	}
	@Test
	public void erogazioneHostUnderscore() throws Exception {
		test(false, endpoint(HOST_UNDERSCORE));
	}
	@Test
	public void erogazioneHostUnderscoreMultilabel() throws Exception {
		test(false, endpoint(HOST_UNDERSCORE_MULTILABEL));
	}

	// ---- FRUIZIONE ----

	@Test
	public void fruizioneHostConforme() throws Exception {
		test(true, endpoint(HOST_CONFORME));
	}
	@Test
	public void fruizioneHostUnderscore() throws Exception {
		test(true, endpoint(HOST_UNDERSCORE));
	}
	@Test
	public void fruizioneHostUnderscoreMultilabel() throws Exception {
		test(true, endpoint(HOST_UNDERSCORE_MULTILABEL));
	}

	/**
	 * Invoca l'API (in erogazione o fruizione) fornendo via header l'authority+path dell'endpoint
	 * da invocare, e verifica che la chiamata vada a buon fine (HTTP 200 + esito OK) e che sia stata
	 * effettivamente usata la libreria http attesa (BIO/NIO/URLConnection).
	 *
	 * @param fruizione    true per la fruizione ({@code /out/...}), false per l'erogazione ({@code /in/...})
	 * @param urlDaInvocare authority+path forniti al connettore via header {@value #HEADER_URL}
	 *                      (es. {@code govway_hostname:8080/TestService/echo})
	 */
	private void test(boolean fruizione, String urlDaInvocare) throws Exception {

		String basePath = System.getProperty("govway_base_path");
		String url = fruizione
				? basePath + "/out/" + FRUITORE + "/" + EROGATORE + "/" + API + "/v1/"
				: basePath + "/in/" + EROGATORE + "/" + API + "/v1/";

		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[] content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		request.setContent(content);
		request.setUrl(url);

		// L'endpoint effettivo (host eventualmente non conforme) viene fornito al connettore via header
		request.addHeader(HEADER_URL, urlDaInvocare);

		// Forza la libreria http (BIO/NIO/URLConnection) e gestisce il prefisso async per il NIO
		if (this.mode != null)
			this.mode.patchRequest(request);

		HttpResponse response = HttpUtilities.httpInvoke(request);

		// Host non conforme a RFC 2396 (underscore): un backend con parser HTTP RFC-stretto
		// (es. Tomcat 11, usato nell'ambiente Jenkins) rifiuta l'header 'Host' contenente '_'
		// rispondendo 400. Il fix lato GovWay garantisce solo che la richiesta venga *inviata*
		// (niente piu' NPE, delega come 'httpcore5' NIO e 'java.net.HttpURLConnection');
		// l'accettazione dipende dal parser del backend. Per il solo host problematico, e solo
		// su Jenkins, si tollera quindi anche il 400 (oltre al 200 di un backend tollerante).
		boolean hostProblematico = urlDaInvocare.startsWith(HOST_UNDERSCORE)
				|| urlDaInvocare.startsWith(HOST_UNDERSCORE_MULTILABEL);
		if (hostProblematico && Utils.isJenkins()
				&& response.getResultHTTPOperation() == 400) {
			// GovWay ha comunque dispacciato la richiesta (transazione tracciata): e' il backend
			// Tomcat 11 a rifiutare l'host con underscore, comportamento atteso e non un bug del gateway.
			System.out.println("[RegistryBasedHostTest] Eccezione attesa per host non conforme [" + urlDaInvocare
					+ "]: ambiente Jenkins con backend Tomcat 11, che rifiuta a livello di parser RFC l'header 'Host'"
					+ " contenente '_' rispondendo HTTP 400. Il fix garantisce solo l'invio della richiesta da parte di"
					+ " GovWay (niente NPE, delega come 'httpcore5' NIO e 'java.net.HttpURLConnection'); l'accettazione"
					+ " dipende dal parser del backend. Tollerato il 400 (oltre al 200 di un backend tollerante, es. WildFly/Undertow).");
			assertNotNull(response.getHeaderFirstValue("GovWay-Transaction-ID"));
			return;
		}

		assertEquals("Invocazione fallita per urlDaInvocare=[" + urlDaInvocare + "]",
				200, response.getResultHTTPOperation());
		assertEquals(contentType, response.getContentType());

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		long esitoOk = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME)
				.convertoToCode(EsitoTransazioneName.OK);

		// Passando 'this.mode', DBVerifier verifica anche, sui messaggi diagnostici della transazione,
		// che sia stata effettivamente usata la libreria attesa: [httpcore] (BIO), [httpcore-nio] (NIO),
		// [httpUrlConn] (URLConnection) -- cfr. HttpLibraryMode.getExpectedMessage() e DBVerifier.checkMsg().
		DBVerifier.verify(idTransazione, esitoOk, this.mode);
	}

}
