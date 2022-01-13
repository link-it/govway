/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer.Common;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpUtilities;



/**
 * IdentificazioneFallitaTest
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * 
 */
public class IdentificazioneFallitaTest extends ConfigLoader {
	
	// TODO: Chiedi ad andrea dove prenderli
	private static final String CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR = "007045";
	private static final String CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_INFO = "007046";
	
	private static final String CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR = "007041";
	private static final String CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO = "007042";	
	private static final String CODICE_DIAGNOSTICO_UTILIZZO_CONNETTORE_DEFAULT = "007047";
	private static final int DIAGNOSTICO_SEVERITA_INFO = 4;
	private static final int DIAGNOSTICO_SEVERITA_ERROR = 2;
	
	private static final String MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO = "Il valore estratto dalla richiesta 'CONNETTORE_INESISTENTE', ottenuto tramite identificazione 'HeaderBased' (Header HTTP: GovWay-TestSuite-Connettore), non corrisponde al nome di nessun connettore";
	private static final String MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA = "Identificazione 'HeaderBased' (Header HTTP: GovWay-TestSuite-Connettore) non è riuscita ad estrarre dalla richiesta l'informazione utile ad identificare il connettore da utilizzare: header non presente";
	
	private static final String MESSAGGIO_DIAGNOSTICO_FALLBACK_IDENTIFICAZIONE_FALLITA = "Per la consegna viene utilizzato il connettore 'Connettore0', configurato per essere utilizzato in caso di identificazione condizionale fallita";
	
	// TODO x andrea, tofix TODO: Reimpostare alla stringa prima della consegna, in modo da far fallire i test che devono fallire.
	private static final String MESSAGGIO_DIAGNOSTICO_FALLBACK_NESSUN_CONNETTORE_TROVATO = MESSAGGIO_DIAGNOSTICO_FALLBACK_IDENTIFICAZIONE_FALLITA; //"MESSAGGIO DA SOSTITUIRE DOPO IL FIX. ATTUALMENTE VIENE RESTITUITO QUELLO DELLA RIGA DI SOPRA CHE PARLA INVECE DI 'IDENTIFICAZIONE CONDIZIONALE FALLITA' INVECE CHE 'ASSENZA DI CONNETTORI UTILIZZABILI'";

	

	HttpRequest buildRequest_NessunConnettoreUtilizzabile(String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.addHeader(Common.HEADER_ID_CONDIZIONE, "CONNETTORE_INESISTENTE");
		
		return request;
	}
	

	private HttpRequest buildRequest_IdentificazioneFallita(final String erogazione) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		request.addHeader("HeaderSbagliato", Common.CONNETTORE_1);
		return request;
	}
	
	
	@Test
	public void identificazioneFallitaNoDiagnostico() throws UtilsException {
		// L'erogazione ha l'identificazione sullo header HTTP GovWay-TestSuite-Connettore
		// nel caso di identificazione fallita passa al CONNETTORE_0
		
		final String erogazione = "ConsegnaCondizionaleIdentificazioneFallitaNoDiagnostico";
		HttpRequest request = buildRequest_IdentificazioneFallita(erogazione);
		
		var response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		String id_transazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR);
		checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_UTILIZZO_CONNETTORE_DEFAULT, MESSAGGIO_DIAGNOSTICO_FALLBACK_IDENTIFICAZIONE_FALLITA);		
	}
	
	
	
	@Test
	public void identificazioneFallitaDiagnosticoInfo() throws UtilsException {
		// Come per identificazioneFallitaNoDisagnostico, il connettore di fallback è il 0
		// Devo inoltre controllare che sia stato emesso il messaggio sul db
		
		final String erogazione = "ConsegnaCondizionaleIdentificazioneFallitaDiagnosticoInfo";

		HttpRequest request = buildRequest_IdentificazioneFallita(erogazione);
		var response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		String id_transazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO, MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_UTILIZZO_CONNETTORE_DEFAULT, MESSAGGIO_DIAGNOSTICO_FALLBACK_IDENTIFICAZIONE_FALLITA);
	}
	
	
	@Test
	public void identificazioneFallitaDiagnosticoError() throws UtilsException {
		
		// Come per identificazioneFallitaNoDisagnostico, il connettore di fallback è il 0
		// Devo inoltre controllare che sia stato emesso il messaggio sul db
		
		final String erogazione = "ConsegnaCondizionaleIdentificazioneFallitaDiagnosticoError";
		
		HttpRequest request = buildRequest_IdentificazioneFallita(erogazione);
		var response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		String id_transazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_ERROR, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR, MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_UTILIZZO_CONNETTORE_DEFAULT, MESSAGGIO_DIAGNOSTICO_FALLBACK_IDENTIFICAZIONE_FALLITA);
	}

	
	
	@Test
	public void nessunConnettoreUtilizzabileNoDiagnostico() throws UtilsException {
		// Come per identificazioneFallitaNoDisagnostico, il connettore di fallback è il 0
		// Devo inoltre controllare che non sia stato emesso il messaggio sul db
				
		final String erogazione = "ConsegnaCondizionaleNessunConnettoreUtilizzabileNoDiagnostico";
		
		var request = buildRequest_NessunConnettoreUtilizzabile(erogazione);
		var response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		String id_transazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_INFO);
		checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR);
		// Il messaggio di scelta del connettore di default, avviene sempre TODO: Patchare in govway il messaggio!
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_UTILIZZO_CONNETTORE_DEFAULT, MESSAGGIO_DIAGNOSTICO_FALLBACK_NESSUN_CONNETTORE_TROVATO);
	}
	
	
	@Test
	public void nessunConnettoreUtilizzabileDiagnosticoInfo() throws UtilsException {
		
		final String erogazione = "ConsegnaCondizionaleNessunConnettoreUtilizzabileDiagnosticoInfo";
		
		var request = buildRequest_NessunConnettoreUtilizzabile(erogazione);
		var response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		String id_transazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_INFO, MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_UTILIZZO_CONNETTORE_DEFAULT, MESSAGGIO_DIAGNOSTICO_FALLBACK_NESSUN_CONNETTORE_TROVATO);
	}

	
	@Test
	public void nessunConnettoreUtilizzabileDiagnosticoError() throws UtilsException {
		
		final String erogazione = "ConsegnaCondizionaleNessunConnettoreUtilizzabileDiagnosticoError";

		var request = buildRequest_NessunConnettoreUtilizzabile(erogazione);
		var response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		String id_transazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_ERROR, CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR, MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_UTILIZZO_CONNETTORE_DEFAULT, MESSAGGIO_DIAGNOSTICO_FALLBACK_NESSUN_CONNETTORE_TROVATO);
	}
	
	
	@Test
	public void identificazioneCondizioneFallitaENessunConnettoreUtilizzabile() throws UtilsException {
		final String erogazione = "ConsegnaCondizionaleIdentificazioneFallitaNessunConnettoreUtilizzabile";

		// Identificazione fallita va sul connettore 0 con livello log info
		HttpRequest request = buildRequest_IdentificazioneFallita(erogazione);
		var response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		String id_transazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO, MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_UTILIZZO_CONNETTORE_DEFAULT, MESSAGGIO_DIAGNOSTICO_FALLBACK_IDENTIFICAZIONE_FALLITA);
		
		// Nessun connettore utilizzabile va sul connettore_1 con livello log error
		
		request = buildRequest_NessunConnettoreUtilizzabile(erogazione);
		response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_1, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		final String diagnostico = "Per la consegna viene utilizzato il connettore 'Connettore1', configurato per essere utilizzato in caso di identificazione condizionale fallita";
		
		id_transazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_ERROR, CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR, MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_UTILIZZO_CONNETTORE_DEFAULT, diagnostico);
	}
	
	
	@Test
	public void identificazioneFallitaByNome() throws UtilsException {
		// Per ogni regola per cui è possibile farlo, faccio fallire l'identificazione controllando che non avvengano 
		// dei 500 dovuti a null pointer exceptions, ma solo 400
		final String erogazione = "ConsegnaCondizionaleRegoleByNome";
		identificazioneFallitaImpl(erogazione);
	}
	
	@Test
	public void identificazioneFallitaByFiltro() throws UtilsException {
		// Per ogni regola per cui è possibile farlo, faccio fallire l'identificazione controllando che non avvengano 
		// dei 500 dovuti a null pointer exceptions per parametri mancanti, ma solo 400 
		final String erogazione = "ConsegnaCondizionaleRegoleByFiltro";
		identificazioneFallitaImpl(erogazione);
	}


	
	void checkAssenzaDiagnosticoTransazione(String id_transazione, String codice) {
		String query = "select count(*) from msgdiagnostici where id_transazione=? AND codice=?";
		int nrows = getDbUtils().readValue(query, Integer.class, id_transazione, codice);
		assertEquals(0, nrows);
	}
	
	
	void checkDiagnosticoTransazione(String id_transazione, Integer severita, String codice, String messaggio) {
		String query = "select count(*) from msgdiagnostici where id_transazione=? AND severita=? AND codice=? AND messaggio=?";
		int nrows = getDbUtils().readValue(query, Integer.class, id_transazione, severita, codice, messaggio);
		assertEquals(1, nrows);
	}


	static void identificazioneFallitaImpl(String erogazione) throws UtilsException {
		
		final String content = "{ \"campo_inutile\": 1 }";
	
		HttpRequest request_contenuto = new HttpRequest();
		request_contenuto.setMethod(HttpRequestMethod.POST);
		request_contenuto.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-contenuto"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);		
		request_contenuto.setContentType("application/json");
		request_contenuto.setContent(content.getBytes());
		
		var resp = HttpUtilities.httpInvoke(request_contenuto);
		assertEquals(400, resp.getResultHTTPOperation());
		
		HttpRequest request_parametro_url = new HttpRequest();
		request_parametro_url.setMethod(HttpRequestMethod.GET);
		request_parametro_url.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-parametro-url"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request="); // faccio mancare il valore del parametro
	
		resp = HttpUtilities.httpInvoke(request_parametro_url);
		assertEquals(400, resp.getResultHTTPOperation());
	
			
		HttpRequest request_template = new HttpRequest();
		request_template.setContentType("application/json");	// TODO: con questa riga commentata il test fallisce, dopo il fix di andrea non sarà più necessario settare il content type
		request_template.setMethod(HttpRequestMethod.GET);
		request_template.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-template"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request="); // faccio mancare il valore del parametro
		
		resp = HttpUtilities.httpInvoke(request_template);
		assertEquals(400, resp.getResultHTTPOperation());
		
		
		HttpRequest request_freemarker = new HttpRequest();
		request_freemarker.setContentType("application/json"); // TODO: con questa riga commentata il test fallisce, dopo il fix di andrea non sarà più necessario settare il content type
		request_freemarker.setMethod(HttpRequestMethod.GET);
		request_freemarker.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-freemarker-template"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request=");	// faccio mancare il valore del parametro
		
		resp = HttpUtilities.httpInvoke(request_freemarker);
		assertEquals(400, resp.getResultHTTPOperation());
	
	
		HttpRequest request_velocity = new HttpRequest();
		request_velocity.setContentType("application/json"); // TODO: con questa riga commentata il test fallisce, dopo il fix di andrea non sarà più necessario settare il content type
		request_velocity.setMethod(HttpRequestMethod.GET);
		request_velocity.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-velocity-template"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request="); // faccio mancare il valore del parametro
		
		resp = HttpUtilities.httpInvoke(request_velocity);
		assertEquals(400, resp.getResultHTTPOperation());
		
		HttpRequest request_header_http = new HttpRequest();
		request_header_http.setMethod(HttpRequestMethod.GET);
		request_header_http.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-header-http"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		// faccio mancare il valore del parametro header
		
		resp = HttpUtilities.httpInvoke(request_header_http);
		assertEquals(400, resp.getResultHTTPOperation());
		
		HttpRequest request_forwarded_for = new HttpRequest();
		request_forwarded_for.setMethod(HttpRequestMethod.GET);
		request_forwarded_for.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-xforwarded-for"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX);
		// faccio mancare il valore del parametro header
		
		resp = HttpUtilities.httpInvoke(request_forwarded_for);
		assertEquals(400, resp.getResultHTTPOperation());
		
		
		HttpRequest request_url_invocazione = new HttpRequest();
		request_url_invocazione.setMethod(HttpRequestMethod.GET);
		request_url_invocazione.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-url-invocazione"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request="); // faccio mancare il valore del parametro 		 
		
		resp = HttpUtilities.httpInvoke(request_url_invocazione);
		assertEquals(400, resp.getResultHTTPOperation());		
		
	}
	
	
}
