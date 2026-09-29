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
package org.openspcoop2.core.protocolli.trasparente.testsuite.trasformazione.protocollo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * Verifica la trasformazione del contenuto della risposta su API REST quando la regola di trasformazione
 * riscrive il metodo e/o il path della richiesta (elemento 'trasformazione-rest').
 *
 * Su API REST l'elemento 'trasformazione-rest' descrive la sola riscrittura di metodo e path; il gateway
 * lo interpretava invece sempre come una conversione di protocollo SOAP->REST e, in presenza di una
 * trasformazione del contenuto della risposta, tentava di riconvertire la risposta in SOAP fallendo con
 * l'errore "Atteso messaggio di richiesta di tipo SOAP, in presenza di una trasformazione REST attiva"
 * (esito 'Trasformazione Risposta Fallita', HTTP 502 verso il client).
 *
 * Tutti i casi sono configurati su un'unica erogazione/fruizione (API TestTrasformazioneRestRewrite) con
 * una regola per azione. Il backend (TestService/echo) verifica il metodo ricevuto ('test-method') e
 * restituisce il file indicato ('test-response') con il Content-Type indicato ('test-response-content-type').
 *
 * I test verificano il comportamento corretto: prima della correzione i casi affetti falliscono.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RestRewriteTrasformazioneRispostaTest extends ConfigLoader {

	private static final String SERVIZIO = "TestTrasfRestRewrite";

	private static final String BACKEND_BASE = "/TestService/echo";
	private static final String BACKEND_JSON ="/etc/govway/testfiles/HelloWorld.json";
	private static final String BACKEND_SOAP = "/etc/govway/testfiles/soap.xml";
	private static final String BACKEND_JSON_ELEMENTO = "example:operation";

	private static final String BODY_RICHIESTA = "{\"test\":\"rewrite\"}";

	private static final String MESSAGE_ATTESO = "alive";

	private static final String HEADER_RISPOSTA = "X-Test-Rewrite";
	private static final String HEADER_RISPOSTA_VALORE = "risposta-trasformata";

	private static final String DIAG_PREFIX = "Processo di trasformazione";

	private static String diagInCorso(String tipo, String fase) {
		return DIAG_PREFIX+" ("+tipo+") della "+fase+" in corso ...";
	}
	private static String diagEffettuato(String tipo, String fase) {
		return DIAG_PREFIX+" ("+tipo+") della "+fase+" completato con successo";
	}



	// ##############################################################################################################
	// Casi affetti: riscrittura di metodo e/o path + trasformazione del contenuto della risposta
	// ##############################################################################################################

	// replica della configurazione segnalata: rewrite del solo path su GET senza payload, template Freemarker in
	// risposta con aggiornamento del Content-Type ed elemento 'trasformazione-soap' salvato dalla console nella risposta
	@Test public void rewritePathErogazione() throws Exception { rewritePath(TipoServizio.EROGAZIONE); }
	@Test public void rewritePathFruizione() throws Exception { rewritePath(TipoServizio.FRUIZIONE); }

	private void rewritePath(TipoServizio tipo) throws Exception {
		String azione = "rewritePath";
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.GET, false, HttpRequestMethod.GET, BACKEND_JSON, HttpConstants.CONTENT_TYPE_JSON);
		String m = msg(tipo, azione, r);
		verificaMessageAtteso(m, r);
		assertEquals(m, HttpConstants.CONTENT_TYPE_JSON, r.getContentType());
		verificaTransazioneOk(m, r, "path", "freemarker headers");
		verificaLocationConnettore(m, r, HttpRequestMethod.GET, "/TestService/echo/rewritten/path");
	}

	// rewrite del solo metodo (GET -> POST)
	@Test public void rewriteMethodErogazione() throws Exception { rewriteMethod(TipoServizio.EROGAZIONE); }
	@Test public void rewriteMethodFruizione() throws Exception { rewriteMethod(TipoServizio.FRUIZIONE); }

	private void rewriteMethod(TipoServizio tipo) throws Exception {
		String azione = "rewriteMethod";
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.GET, false, HttpRequestMethod.POST, BACKEND_JSON, HttpConstants.CONTENT_TYPE_JSON);
		String m = msg(tipo, azione, r);
		verificaMessageAtteso(m, r);
		verificaTransazioneOk(m, r, "method", "freemarker");
		verificaLocationConnettore(m, r, HttpRequestMethod.POST, "/TestService/echo/"+azione);
	}

	// rewrite di metodo e path (POST -> PUT) su richiesta con payload JSON
	@Test public void rewriteMethodPathErogazione() throws Exception { rewriteMethodPath(TipoServizio.EROGAZIONE); }
	@Test public void rewriteMethodPathFruizione() throws Exception { rewriteMethodPath(TipoServizio.FRUIZIONE); }

	private void rewriteMethodPath(TipoServizio tipo) throws Exception {
		String azione = "rewriteMethodPath";
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpRequestMethod.PUT, BACKEND_JSON, HttpConstants.CONTENT_TYPE_JSON);
		String m = msg(tipo, azione, r);
		verificaMessageAtteso(m, r);
		verificaTransazioneOk(m, r, "method path", "freemarker");
		verificaLocationConnettore(m, r, HttpRequestMethod.PUT, "/TestService/echo/rewritten/methodPath");
	}

	// rewrite del path + trasformazione del contenuto anche della richiesta
	@Test public void rewriteReqContentErogazione() throws Exception { rewriteReqContent(TipoServizio.EROGAZIONE); }
	@Test public void rewriteReqContentFruizione() throws Exception { rewriteReqContent(TipoServizio.FRUIZIONE); }

	private void rewriteReqContent(TipoServizio tipo) throws Exception {
		String azione = "rewriteReqContent";
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpRequestMethod.POST, BACKEND_JSON, HttpConstants.CONTENT_TYPE_JSON);
		String m = msg(tipo, azione, r);
		verificaMessageAtteso(m, r);
		verificaTransazioneOk(m, r, "path freemarker", "freemarker");
		verificaLocationConnettore(m, r, HttpRequestMethod.POST, "/TestService/echo/rewritten/reqContent");
	}

	// rewrite del path + conversione 'HTTP Payload vuoto' della risposta
	@Test public void rewriteRespEmptyErogazione() throws Exception { rewriteRespEmpty(TipoServizio.EROGAZIONE); }
	@Test public void rewriteRespEmptyFruizione() throws Exception { rewriteRespEmpty(TipoServizio.FRUIZIONE); }

	private void rewriteRespEmpty(TipoServizio tipo) throws Exception {
		String azione = "rewriteRespEmpty";
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.GET, false, HttpRequestMethod.GET, BACKEND_JSON, HttpConstants.CONTENT_TYPE_JSON);
		String m = msg(tipo, azione, r);
		assertEquals(m+" "+body(r), 200, r.getResultHTTPOperation());
		assertTrue(m+" payload presente in risposta: "+body(r), r.getContent()==null || r.getContent().length==0);
		verificaTransazioneOk(m, r, "path", "empty-payload");
		verificaLocationConnettore(m, r, HttpRequestMethod.GET, "/TestService/echo/rewritten/respEmpty");
	}

	// rewrite del path + conversione 'Template' della risposta
	@Test public void rewriteRespTemplateErogazione() throws Exception { rewriteRespTemplate(TipoServizio.EROGAZIONE); }
	@Test public void rewriteRespTemplateFruizione() throws Exception { rewriteRespTemplate(TipoServizio.FRUIZIONE); }

	private void rewriteRespTemplate(TipoServizio tipo) throws Exception {
		String azione = "rewriteRespTemplate";
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.GET, false, HttpRequestMethod.GET, BACKEND_JSON, HttpConstants.CONTENT_TYPE_JSON);
		String m = msg(tipo, azione, r);
		verificaMessageAtteso(m, r);
		verificaTransazioneOk(m, r, "path", "template");
		verificaLocationConnettore(m, r, HttpRequestMethod.GET, "/TestService/echo/rewritten/respTemplate");
	}

	// Conversione REST->SOAP con un elemento 'trasformazione-rest' residuo nella richiesta (configurazione non producibile
	// dalla console, dove metodo e path non sono proposti in presenza della trasformazione SOAP, ma importabile):
	// la risposta SOAP deve essere riportata in REST e non riconvertita in SOAP.
	@Test public void rest2SoapConRewriteErogazione() throws Exception { rest2SoapConRewrite(TipoServizio.EROGAZIONE); }
	@Test public void rest2SoapConRewriteFruizione() throws Exception { rest2SoapConRewrite(TipoServizio.FRUIZIONE); }

	private void rest2SoapConRewrite(TipoServizio tipo) throws Exception {
		String azione = "rest2SoapConRewrite";
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpRequestMethod.POST, BACKEND_SOAP, HttpConstants.CONTENT_TYPE_SOAP_1_1);
		String m = msg(tipo, azione, r);
		verificaMessageAtteso(m, r);
		verificaTransazioneOk(m, r, "soap freemarker", "rest freemarker");
		verificaLocationConnettoreSoap(m, r);
	}



	// ##############################################################################################################
	// Casi di controllo (già funzionanti): nessuna trasformazione del contenuto della risposta o nessun rewrite
	// ##############################################################################################################

	// rewrite del path + 'Alimentazione Contesto' in risposta: il contenuto non viene trasformato e la risposta non è
	// affetta dal problema; l'etichetta del diagnostico riportava però erroneamente una conversione 'soap'
	@Test public void rewriteRespContextErogazione() throws Exception { rewriteRespContext(TipoServizio.EROGAZIONE); }
	@Test public void rewriteRespContextFruizione() throws Exception { rewriteRespContext(TipoServizio.FRUIZIONE); }

	private void rewriteRespContext(TipoServizio tipo) throws Exception {
		String azione = "rewriteRespContext";
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.GET, false, HttpRequestMethod.GET, BACKEND_JSON, HttpConstants.CONTENT_TYPE_JSON);
		String m = msg(tipo, azione, r);
		verificaRispostaBackend(m, r);
		verificaTransazioneOk(m, r, "path", "freemarker-context");
		verificaLocationConnettore(m, r, HttpRequestMethod.GET, "/TestService/echo/rewritten/respContext");
	}

	// rewrite del path + trasformazione dei soli header della risposta
	@Test public void rewriteRespHeadersErogazione() throws Exception { rewriteRespHeaders(TipoServizio.EROGAZIONE); }
	@Test public void rewriteRespHeadersFruizione() throws Exception { rewriteRespHeaders(TipoServizio.FRUIZIONE); }

	private void rewriteRespHeaders(TipoServizio tipo) throws Exception {
		String azione = "rewriteRespHeaders";
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.GET, false, HttpRequestMethod.GET, BACKEND_JSON, HttpConstants.CONTENT_TYPE_JSON);
		String m = msg(tipo, azione, r);
		verificaRispostaBackend(m, r);
		assertEquals(m+" "+HEADER_RISPOSTA, HEADER_RISPOSTA_VALORE, r.getHeaderFirstValue(HEADER_RISPOSTA));
		verificaTransazioneOk(m, r, "path", "headers");
		verificaLocationConnettore(m, r, HttpRequestMethod.GET, "/TestService/echo/rewritten/respHeaders");
	}

	// rewrite del path + trasformazione della risposta non applicabile (codice di risposta 201, il backend risponde 200)
	@Test public void rewriteRespNoMatchErogazione() throws Exception { rewriteRespNoMatch(TipoServizio.EROGAZIONE); }
	@Test public void rewriteRespNoMatchFruizione() throws Exception { rewriteRespNoMatch(TipoServizio.FRUIZIONE); }

	private void rewriteRespNoMatch(TipoServizio tipo) throws Exception {
		String azione = "rewriteRespNoMatch";
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.GET, false, HttpRequestMethod.GET, BACKEND_JSON, HttpConstants.CONTENT_TYPE_JSON);
		String m = msg(tipo, azione, r);
		verificaRispostaBackend(m, r);
		verificaTransazioneOk(m, r, "path", null);
		verificaLocationConnettore(m, r, HttpRequestMethod.GET, "/TestService/echo/rewritten/respNoMatch");
	}

	// nessun rewrite + trasformazione del contenuto della risposta
	@Test public void noRewriteErogazione() throws Exception { noRewrite(TipoServizio.EROGAZIONE); }
	@Test public void noRewriteFruizione() throws Exception { noRewrite(TipoServizio.FRUIZIONE); }

	private void noRewrite(TipoServizio tipo) throws Exception {
		String azione = "noRewrite";
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.GET, false, HttpRequestMethod.GET, BACKEND_JSON, HttpConstants.CONTENT_TYPE_JSON);
		String m = msg(tipo, azione, r);
		verificaMessageAtteso(m, r);
		verificaTransazioneOk(m, r, "nessuna", "freemarker");
		verificaLocationConnettore(m, r, HttpRequestMethod.GET, "/TestService/echo/"+azione);
	}

	// conversione REST->SOAP della richiesta e SOAP->REST della risposta
	@Test public void rest2SoapErogazione() throws Exception { rest2Soap(TipoServizio.EROGAZIONE); }
	@Test public void rest2SoapFruizione() throws Exception { rest2Soap(TipoServizio.FRUIZIONE); }

	private void rest2Soap(TipoServizio tipo) throws Exception {
		String azione = "rest2Soap";
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpRequestMethod.POST, BACKEND_SOAP, HttpConstants.CONTENT_TYPE_SOAP_1_1);
		String m = msg(tipo, azione, r);
		verificaMessageAtteso(m, r);
		verificaTransazioneOk(m, r, "soap freemarker", "rest freemarker");
		verificaLocationConnettoreSoap(m, r);
	}



	// ##############################################################################################################
	// Utility
	// ##############################################################################################################

	private static String msg(TipoServizio tipo, String scenario, HttpResponse r) {
		return "["+tipo+"]["+scenario+"] idTransazione:"+r.getHeaderFirstValue("GovWay-Transaction-ID");
	}

	private static String body(HttpResponse r) {
		return r.getContent()!=null ? new String(r.getContent()) : "";
	}

	// risposta prodotta dal template della regola: {"message": "alive"}
	private static void verificaMessageAtteso(String m, HttpResponse r) throws Exception {
		assertEquals(m+" "+body(r), 200, r.getResultHTTPOperation());
		assertNotNull(m+" payload assente", r.getContent());
		String message = JsonPathExpressionEngine.extractAndConvertResultAsString(body(r), "$.message", logCore);
		assertEquals(m+" "+body(r), MESSAGE_ATTESO, message);
	}

	// risposta del backend non trasformata (HelloWorld.json)
	private static void verificaRispostaBackend(String m, HttpResponse r) {
		assertEquals(m+" "+body(r), 200, r.getResultHTTPOperation());
		assertTrue(m+" payload del backend atteso: "+body(r), body(r).contains(BACKEND_JSON_ELEMENTO));
	}

	// Verifica l'esito OK e i diagnostici del processo di trasformazione, che riportano le azioni di trasformazione individuate
	// (es. 'path', 'method path', 'soap freemarker', 'freemarker headers'): per ogni fase devono essere presenti esattamente i
	// diagnostici 'in corso' e 'completato con successo' con il tipo atteso e nessun altro diagnostico del processo di
	// trasformazione (es. 'fallito', o un tipo differente). Con 'tipoRisposta' null la risposta non deve essere trasformata
	// (il diagnostico di nessun match ha severità debug e non è registrato con la configurazione di default).
	private static void verificaTransazioneOk(String m, HttpResponse r, String tipoRichiesta, String tipoRisposta) throws Exception {
		String idTransazione = r.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(m+" GovWay-Transaction-ID", idTransazione);
		long esitoOk = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		DBVerifier.verify(idTransazione, esitoOk, diagEffettuato(tipoRichiesta, "richiesta"));

		verificaDiagnostico(m, idTransazione, diagInCorso(tipoRichiesta, "richiesta"));
		verificaDiagnostico(m, idTransazione, diagEffettuato(tipoRichiesta, "richiesta"));
		int attesi = 2;
		if(tipoRisposta!=null) {
			verificaDiagnostico(m, idTransazione, diagInCorso(tipoRisposta, "risposta"));
			verificaDiagnostico(m, idTransazione, diagEffettuato(tipoRisposta, "risposta"));
			attesi = 4;
		}
		int count = ConfigLoader.getDbUtils().readValue("select count(*) from msgdiagnostici where id_transazione = ? and messaggio LIKE '"+DIAG_PREFIX+"%'", Integer.class, idTransazione);
		assertEquals(m+" numero di diagnostici '"+DIAG_PREFIX+"' inatteso (tipo richiesta '"+tipoRichiesta+"', tipo risposta '"+tipoRisposta+"')", attesi, count);
	}

	private static void verificaDiagnostico(String m, String idTransazione, String diagnostico) throws Exception {
		int count = ConfigLoader.getDbUtils().readValue("select count(*) from msgdiagnostici where id_transazione = ? and messaggio LIKE '"+diagnostico+"'", Integer.class, idTransazione);
		assertEquals(m+" diagnostico atteso '"+diagnostico+"'", 1, count);
	}

	// verifica metodo e url effettivamente utilizzati dal connettore (es. '[GET] http://127.0.0.1:8080/TestService/echo/rewritten/path?...')
	private static void verificaLocationConnettore(String m, HttpResponse r, HttpRequestMethod metodoAtteso, String pathAtteso) throws Exception {
		String location = readLocationConnettore(m, r);
		assertTrue(m+" metodo atteso '"+metodoAtteso+"', location: "+location, location.startsWith("["+metodoAtteso+"] "));
		assertTrue(m+" path atteso '"+pathAtteso+"', location: "+location, getUrlSenzaQueryString(location).endsWith(pathAtteso));
	}

	// per un messaggio SOAP la location non riporta il metodo http e al connettore non viene accodato alcun path
	// (es. 'http://127.0.0.1:8080/TestService/echo?...'): un eventuale path della richiesta non deve essere applicato
	private static void verificaLocationConnettoreSoap(String m, HttpResponse r) throws Exception {
		String location = readLocationConnettore(m, r);
		assertTrue(m+" location senza metodo http attesa, location: "+location, location.startsWith("http"));
		assertTrue(m+" backend atteso '"+BACKEND_BASE+"' senza path aggiuntivi, location: "+location, getUrlSenzaQueryString(location).endsWith(BACKEND_BASE));
	}

	private static String readLocationConnettore(String m, HttpResponse r) throws Exception {
		String idTransazione = r.getHeaderFirstValue("GovWay-Transaction-ID");
		String location = ConfigLoader.getDbUtils().readValue("select location_connettore from transazioni where id = ?", String.class, idTransazione);
		assertNotNull(m+" location_connettore", location);
		return location;
	}

	private static String getUrlSenzaQueryString(String location) {
		return location.contains("?") ? location.substring(0, location.indexOf("?")) : location;
	}

	private static HttpResponse invoca(TipoServizio tipo, String azione, HttpRequestMethod method, boolean conPayload,
			HttpRequestMethod metodoBackend, String rispostaBackend, String contentTypeRispostaBackend) throws Exception {
		String url = tipo == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/in/SoggettoInternoTest/"+SERVIZIO+"/v1/"+azione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+SERVIZIO+"/v1/"+azione;
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(method);
		request.addHeader("test-method", metodoBackend.name());
		request.addHeader("test-response", rispostaBackend);
		request.addHeader("test-response-content-type", contentTypeRispostaBackend);
		if(conPayload) {
			request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
			request.setContent(BODY_RICHIESTA.getBytes());
		}
		request.setUrl(url);
		return HttpUtilities.httpInvoke(request);
	}

}
