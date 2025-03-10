/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpUtilities;



/**
 * IdentificazioneFallitaTest
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdentificazioneFallitaTest extends ConfigLoader {

	public static final String CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR = "007041";
	public  static final String CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO = "007042";
	
	public static final String CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR = "007045";
	public static final String CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_INFO = "007046";
	public static final String CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_UTILIZZO_CONNETTORE_DEFAULT = "007047";
	public static final String CODICE_DIAGNOSTICO_CONSEGNA_NOTIFICHE_IDENTIFICAZIONE_FALLITA_UTILIZZO_CONNETTORE_DEFAULT = "007048";
	
	public static final String CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO_UTILIZZO_TUTTI_CONNETTORI =  "007063";
	public static final String CODICE_DIAGNOSTICO_NOTIFICHE_NESSUN_CONNETTORE_TROVATO_UTILIZZO_TUTTI_CONNETTORI =  "007064";
	public static final String CODICE_DIAGNOSTICO_NOTIFICHE_NESSUN_CONNETTORE_TROVATO_UTILIZZO_NESSUN_CONNETTORE =  "007065";

	public static final String CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_UTILIZZO_TUTTI_CONNETTORI = "007051";
	public static final String CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_UTILIZZO_CONNETTORE_DEFAULT = "007061";
	public static final String CODICE_DIAGNOSTICO_NOTIFICHE_NESSUN_CONNETTORE_UTILIZZABILE_UTILIZZO_CONNETTORE_DEFAULT = "007062";
	
	
	public static final int DIAGNOSTICO_SEVERITA_INFO = 4;
	public static final int DIAGNOSTICO_SEVERITA_ERROR = 2;
	
	public static final String MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO = "Il valore estratto dalla richiesta 'CONNETTORE_INESISTENTE', ottenuto tramite identificazione 'HeaderBased' (Header HTTP: GovWay-TestSuite-Connettore), non corrisponde al nome di nessun connettore";
	
	public static final String MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA = "Identificazione 'HeaderBased' (Header HTTP: GovWay-TestSuite-Connettore) non è riuscita ad estrarre dalla richiesta l'informazione utile ad identificare il connettore da utilizzare: header non presente";
	
	public static final String MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_FALLBACK = "Per la consegna viene utilizzato il connettore 'Connettore0', configurato per essere utilizzato in caso di identificazione condizionale fallita";
	
	public static final String MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_FALLBACK_TUTTI_CONNETTORI = "Il messaggio di richiesta verrà notificato a tutti i connettori indiscriminatamente poichè l'identificazione condizionale è fallita";
	
	public static final String MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO_FALLBACK = "Per la consegna viene utilizzato il connettore 'Connettore0', configurato per essere utilizzato nel caso in cui la condizione estratta dalla richiesta non ha permesso di identificare alcun connettore";
	
	public static final String MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO_FALLBACK_TUTTI_CONNETTORI = "Il messaggio verrà consegnato a tutti i connettori indiscriminatamente poichè la condizione estratta dalla richiesta non ha permesso di identificare alcun connettore";
	
	public static final String CODICE_DIAGNOSTICO_NOTIFICA_NESSUN_CONNETTORE_UTILIZZABILE_INFO_2 = "007044";
	public static String CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_INFO_2 = "007044";
	public static String CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR_2 = "007043";
	


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
		
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR);
		checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_UTILIZZO_CONNETTORE_DEFAULT, MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_FALLBACK);		
	}
	
	
	
	@Test
	public void identificazioneFallitaDiagnosticoInfo() throws UtilsException {
		// Come per identificazioneFallitaNoDisagnostico, il connettore di fallback è il 0
		// Devo inoltre controllare che sia stato emesso il messaggio sul db
		
		final String erogazione = "ConsegnaCondizionaleIdentificazioneFallitaDiagnosticoInfo";

		HttpRequest request = buildRequest_IdentificazioneFallita(erogazione);
		var response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO, MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_UTILIZZO_CONNETTORE_DEFAULT, MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_FALLBACK);
	}
	
	
	@Test
	public void identificazioneFallitaDiagnosticoError() throws UtilsException {
		
		// Come per identificazioneFallitaNoDisagnostico, il connettore di fallback è il 0
		// Devo inoltre controllare che sia stato emesso il messaggio sul db
		
		final String erogazione = "ConsegnaCondizionaleIdentificazioneFallitaDiagnosticoError";
		
		HttpRequest request = buildRequest_IdentificazioneFallita(erogazione);
		var response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_ERROR, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_ERROR, MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_UTILIZZO_CONNETTORE_DEFAULT, MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_FALLBACK);
	}

	
	
	@Test
	public void nessunConnettoreUtilizzabileNoDiagnostico() throws UtilsException {
		// Come per identificazioneFallitaNoDisagnostico, il connettore di fallback è il 0
		// Devo inoltre controllare che non sia stato emesso il messaggio sul db
				
		final String erogazione = "ConsegnaCondizionaleNessunConnettoreUtilizzabileNoDiagnostico";
		
		var request = buildRequest_NessunConnettoreUtilizzabile(erogazione);
		var response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		//System.out.println("ID TRANSAZIONE: " + id_transazione);
		checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_INFO);
		checkAssenzaDiagnosticoTransazione(id_transazione, CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR);
		checkDiagnosticoTransazione(
				id_transazione,
				DIAGNOSTICO_SEVERITA_INFO,
				CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_UTILIZZO_CONNETTORE_DEFAULT, 
				MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO_FALLBACK);
	}
	
	
	@Test
	public void nessunConnettoreUtilizzabileDiagnosticoInfo() throws UtilsException {
		
		final String erogazione = "ConsegnaCondizionaleNessunConnettoreUtilizzabileDiagnosticoInfo";
		
		var request = buildRequest_NessunConnettoreUtilizzabile(erogazione);
		var response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_INFO, MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO);
		checkDiagnosticoTransazione(
				id_transazione, 
				DIAGNOSTICO_SEVERITA_INFO,
				CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_UTILIZZO_CONNETTORE_DEFAULT, 
				MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO_FALLBACK);
	}

	
	@Test
	public void nessunConnettoreUtilizzabileDiagnosticoError() throws UtilsException {
		
		final String erogazione = "ConsegnaCondizionaleNessunConnettoreUtilizzabileDiagnosticoError";

		var request = buildRequest_NessunConnettoreUtilizzabile(erogazione);
		var response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_ERROR, CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR, MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO);
		checkDiagnosticoTransazione(
				id_transazione, 
				DIAGNOSTICO_SEVERITA_INFO,
				CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_UTILIZZO_CONNETTORE_DEFAULT, 
				MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO_FALLBACK);
	}
	
	
	@Test
	public void identificazioneCondizioneFallitaENessunConnettoreUtilizzabile() throws UtilsException {
		final String erogazione = "ConsegnaCondizionaleIdentificazioneFallitaNessunConnettoreUtilizzabile";

		// Identificazione fallita va sul connettore 0 con livello log info
		HttpRequest request = buildRequest_IdentificazioneFallita(erogazione);
		var response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_0, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		String id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_INFO, MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_INFO, CODICE_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_UTILIZZO_CONNETTORE_DEFAULT, MESSAGGIO_DIAGNOSTICO_IDENTIFICAZIONE_FALLITA_FALLBACK);
		
		// Nessun connettore utilizzabile va sul connettore_1 con livello log error
		
		request = buildRequest_NessunConnettoreUtilizzabile(erogazione);
		response = HttpUtilities.httpInvoke(request);
		
		assertEquals(Common.CONNETTORE_1, response.getHeaderFirstValue(Common.HEADER_ID_CONNETTORE));
		
		final String diagnostico = "Per la consegna viene utilizzato il connettore 'Connettore1', configurato per essere utilizzato nel caso in cui la condizione estratta dalla richiesta non ha permesso di identificare alcun connettore";
		
		id_transazione = response.getHeaderFirstValue(Common.HEADER_ID_TRANSAZIONE);
		checkDiagnosticoTransazione(id_transazione, DIAGNOSTICO_SEVERITA_ERROR, CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_ERROR, MESSAGGIO_DIAGNOSTICO_NESSUN_CONNETTORE_TROVATO);
		checkDiagnosticoTransazione(
				id_transazione, 
				DIAGNOSTICO_SEVERITA_INFO,
				CODICE_DIAGNOSTICO_NESSUN_CONNETTORE_UTILIZZABILE_UTILIZZO_CONNETTORE_DEFAULT, 
				diagnostico);
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


	
	public static void checkAssenzaDiagnosticoTransazione(String id_transazione, String codice) {
		String query = "select count(*) from msgdiagnostici where id_transazione=? AND codice=?";
		ConfigLoader.getLoggerCore().info("CheckAssenzaDiagnosticoTransazione: " + id_transazione + " " + codice);
		int nrows = getDbUtils().readValue(query, Integer.class, id_transazione, codice);
		assertEquals(0, nrows);
	}
	
	
	public static void checkDiagnosticoTransazione(String id_transazione, Integer severita, String codice, String messaggio) {
		String query = "select count(*) from msgdiagnostici where id_transazione=? AND severita=? AND codice=? AND messaggio LIKE ?";
		ConfigLoader.getLoggerCore().info("CheckDiagnosticoTransazione: " + id_transazione + " " + severita + " " + codice + " " + messaggio);
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
		request_template.setMethod(HttpRequestMethod.GET);
		request_template.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-template"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request="); // faccio mancare il valore del parametro
		
		resp = HttpUtilities.httpInvoke(request_template);
		assertEquals(400, resp.getResultHTTPOperation());
		
		
		HttpRequest request_freemarker = new HttpRequest();
		request_freemarker.setMethod(HttpRequestMethod.GET);
		request_freemarker.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test-regola-freemarker-template"
				+ "?replyQueryParameter=id_connettore&replyPrefixQueryParameter="+Common.ID_CONNETTORE_REPLY_PREFIX
				+ "&govway-testsuite-id_connettore_request=");	// faccio mancare il valore del parametro
		
		resp = HttpUtilities.httpInvoke(request_freemarker);
		assertEquals(400, resp.getResultHTTPOperation());
	
	
		HttpRequest request_velocity = new HttpRequest();
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
