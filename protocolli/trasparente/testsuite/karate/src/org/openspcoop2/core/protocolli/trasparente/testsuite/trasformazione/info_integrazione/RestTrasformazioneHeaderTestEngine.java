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
package org.openspcoop2.core.protocolli.trasparente.testsuite.trasformazione.info_integrazione;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * RestTrasformazioneHeaderTestEngine
 *
 * Verifica completa delle trasformazioni degli header HTTP (add / update / updateOrAdd / delete), incluso il
 * Content-Type, in richiesta e in risposta, per erogazione e fruizione REST, incrociata sulle 3 implementazioni
 * di connettore (java.net.HttpURLConnection, httpcore5 BIO, httpcore5 NIO) tramite le sottoclassi concrete.
 *
 * Il connettore effettivamente usato è pilotato dall'header 'GovWay-TestSuite-HttpLibrary' (impostato da
 * {@link HttpLibraryMode#patchRequest}) e la modalità NIO aggiunge il segmento 'async/' alla url ('/in/async/', '/out/async/').
 * Sui casi di successo la verifica del connettore avviene tramite {@link DBVerifier} (diagnostico [httpUrlConn]/[httpcore]/[httpcore-nio]).
 *
 * Unico accordo parte comune 'TestTrasformazioneHeader', con una erogazione/fruizione (servizio) per tipo di operazione:
 * TestTrasfDelete, TestTrasfUpdate, TestTrasfUpdateOrAdd, TestTrasfAdd, TestTrasfCheck, TestTrasfContent.
 * Il client invoca <code>.../in/SoggettoInternoTest/{servizio}/v1/{azione}</code> (erogazione) oppure
 * <code>.../out/SoggettoInternoTestFruitore/SoggettoInternoTest/{servizio}/v1/{azione}</code> (fruizione).
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RestTrasformazioneHeaderTestEngine extends ConfigLoader {

	private static final String PREFIX = "x-ricevuto-";

	private HttpLibraryMode libraryMode = null;
	protected void setHttpLibraryMode(HttpLibraryMode mode) {
		this.libraryMode = mode;
	}

	// una erogazione/fruizione (servizio) per tipo di operazione; tutte sull'unico accordo parte comune TestTrasformazioneHeader
	private static final Map<String,String> SERVIZIO = new HashMap<>();
	static {
		for(String a : new String[]{"reqDeleteEcho","reqDeletePing","respDelete","respDeleteCt"}) SERVIZIO.put(a,"TestTrasfDelete");
		for(String a : new String[]{"reqUpdate","reqUpdateFailBlocca","reqUpdateFailIgnora","respUpdate","respUpdateFailBlocca","respUpdateFailIgnora"}) SERVIZIO.put(a,"TestTrasfUpdate");
		for(String a : new String[]{"reqUpdateOrAddEcho","reqUpdateOrAddPing","reqUpdateOrAddFailBlocca","reqUpdateOrAddFailIgnora","respUpdateOrAddPresente","respUpdateOrAddAssente","respUpdateOrAddFailBlocca","respUpdateOrAddFailIgnora"}) SERVIZIO.put(a,"TestTrasfUpdateOrAdd");
		for(String a : new String[]{"reqAdd","reqAddFailBlocca","reqAddFailIgnora","respAdd","respAddFailBlocca","respAddFailIgnora"}) SERVIZIO.put(a,"TestTrasfAdd");
		for(String a : new String[]{"reqCheckEcho","reqCheckPing","respCheckEcho","respCheckPing"}) SERVIZIO.put(a,"TestTrasfCheck");
		for(String a : new String[]{"reqContentEmpty","reqContentContext","reqContentTemplate","respContentEmpty","respContentContext","respContentTemplate"}) SERVIZIO.put(a,"TestTrasfContent");
	}

	private static final String H_CUSTOM = "X-Custom-Header";
	private static final String H_SANITY = "X-Sanity-Header";
	private static final String H_RESP_CUSTOM = "X-Resp-Custom-Header";
	private static final String H_RESP_SANITY = "X-Resp-Sanity-Header";
	private static final String H_RESP_ADD = "X-Resp-Add-Header";

	private static final String VALORE_AGGIORNATO = "valore-aggiornato";
	private static final String VALORE_UPSERT = "valore-upsert";
	private static final String VALORE_AGGIUNTO = "valore-aggiunto";
	private static final String CT_AGGIORNATO = HttpConstants.CONTENT_TYPE_XML;
	private static final String CT_AGGIUNTO = HttpConstants.CONTENT_TYPE_JSON;


	// ##############################################################################################################
	// RICHIESTA
	// ##############################################################################################################

	// ---- DELETE -------------------------------------------------------------------------------------------------
	@Test public void reqDeleteEchoErogazione() throws Exception { reqDelete(TipoServizio.EROGAZIONE, "reqDeleteEcho", HttpRequestMethod.POST, true); }
	@Test public void reqDeleteEchoFruizione() throws Exception { reqDelete(TipoServizio.FRUIZIONE, "reqDeleteEcho", HttpRequestMethod.POST, true); }
	@Test public void reqDeletePingErogazione() throws Exception { reqDelete(TipoServizio.EROGAZIONE, "reqDeletePing", HttpRequestMethod.GET, false); }
	@Test public void reqDeletePingFruizione() throws Exception { reqDelete(TipoServizio.FRUIZIONE, "reqDeletePing", HttpRequestMethod.GET, false); }

	private void reqDelete(TipoServizio tipo, String azione, HttpRequestMethod method, boolean conPayload) throws Exception {
		Map<String,String> h = new HashMap<>();
		h.put(H_CUSTOM, "valore-da-eliminare");
		h.put(H_SANITY, "valore-di-controllo");
		HttpResponse r = invoca(tipo, azione, method, conPayload, conPayload?HttpConstants.CONTENT_TYPE_JSON:null, h);
		String m = msg(tipo, azione, r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		if(conPayload) {
			assertNull(m+" Content-Type non eliminato", ric(r, HttpConstants.CONTENT_TYPE));
		}
		assertNull(m+" "+H_CUSTOM+" non eliminato", ric(r, H_CUSTOM));
		assertNotNull(m+" "+H_SANITY+" (controllo) assente", ric(r, H_SANITY));
		verificaConnettore(r);
	}

	// delete su header non presente in origine -> no-op (uso reqDeletePing senza inviare X-Custom-Header)
	@Test public void reqDeleteAssenteErogazione() throws Exception { reqDeleteAssente(TipoServizio.EROGAZIONE); }
	@Test public void reqDeleteAssenteFruizione() throws Exception { reqDeleteAssente(TipoServizio.FRUIZIONE); }

	private void reqDeleteAssente(TipoServizio tipo) throws Exception {
		Map<String,String> h = new HashMap<>();
		h.put(H_SANITY, "valore-di-controllo");
		HttpResponse r = invoca(tipo, "reqDeletePing", HttpRequestMethod.GET, false, null, h);
		String m = msg(tipo, "reqDeleteAssente", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNull(m+" "+H_CUSTOM+" presente (non inviato)", ric(r, H_CUSTOM));
		assertNotNull(m+" "+H_SANITY+" (controllo) assente", ric(r, H_SANITY));
		verificaConnettore(r);
	}

	// ---- UPDATE -------------------------------------------------------------------------------------------------
	@Test public void reqUpdatePresenteErogazione() throws Exception { reqUpdate(TipoServizio.EROGAZIONE, true); }
	@Test public void reqUpdatePresenteFruizione() throws Exception { reqUpdate(TipoServizio.FRUIZIONE, true); }
	@Test public void reqUpdateAssenteErogazione() throws Exception { reqUpdate(TipoServizio.EROGAZIONE, false); }
	@Test public void reqUpdateAssenteFruizione() throws Exception { reqUpdate(TipoServizio.FRUIZIONE, false); }

	private void reqUpdate(TipoServizio tipo, boolean inviaCustom) throws Exception {
		Map<String,String> h = new HashMap<>();
		if(inviaCustom) h.put(H_CUSTOM, "valore-originale");
		HttpResponse r = invoca(tipo, "reqUpdate", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, h);
		String m = msg(tipo, "reqUpdate inviaCustom="+inviaCustom, r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" Content-Type non aggiornato", CT_AGGIORNATO, ric(r, HttpConstants.CONTENT_TYPE));
		if(inviaCustom) assertEquals(m+" "+H_CUSTOM+" non aggiornato", VALORE_AGGIORNATO, ric(r, H_CUSTOM));
		else assertNull(m+" "+H_CUSTOM+" aggiunto da update su header assente", ric(r, H_CUSTOM));
		verificaConnettore(r);
	}

	// ---- UPDATE_OR_ADD ------------------------------------------------------------------------------------------
	@Test public void reqUpdateOrAddPresenteErogazione() throws Exception { reqUpdateOrAddPresente(TipoServizio.EROGAZIONE); }
	@Test public void reqUpdateOrAddPresenteFruizione() throws Exception { reqUpdateOrAddPresente(TipoServizio.FRUIZIONE); }

	private void reqUpdateOrAddPresente(TipoServizio tipo) throws Exception {
		Map<String,String> h = new HashMap<>();
		h.put(H_CUSTOM, "valore-originale");
		HttpResponse r = invoca(tipo, "reqUpdateOrAddEcho", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, h);
		String m = msg(tipo, "reqUpdateOrAdd presente", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" Content-Type non aggiornato", CT_AGGIORNATO, ric(r, HttpConstants.CONTENT_TYPE));
		assertEquals(m+" "+H_CUSTOM+" non aggiornato", VALORE_UPSERT, ric(r, H_CUSTOM));
		verificaConnettore(r);
	}

	@Test public void reqUpdateOrAddAssenteErogazione() throws Exception { reqUpdateOrAddAssente(TipoServizio.EROGAZIONE); }
	@Test public void reqUpdateOrAddAssenteFruizione() throws Exception { reqUpdateOrAddAssente(TipoServizio.FRUIZIONE); }

	private void reqUpdateOrAddAssente(TipoServizio tipo) throws Exception {
		HttpResponse r = invoca(tipo, "reqUpdateOrAddPing", HttpRequestMethod.GET, false, null, null);
		String m = msg(tipo, "reqUpdateOrAdd assente", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" Content-Type non aggiunto", CT_AGGIORNATO, ric(r, HttpConstants.CONTENT_TYPE));
		assertEquals(m+" "+H_CUSTOM+" non aggiunto", VALORE_UPSERT, ric(r, H_CUSTOM));
		verificaConnettore(r);
	}

	// ---- ADD ----------------------------------------------------------------------------------------------------
	@Test public void reqAddErogazione() throws Exception { reqAdd(TipoServizio.EROGAZIONE); }
	@Test public void reqAddFruizione() throws Exception { reqAdd(TipoServizio.FRUIZIONE); }

	private void reqAdd(TipoServizio tipo) throws Exception {
		HttpResponse r = invoca(tipo, "reqAdd", HttpRequestMethod.GET, false, null, null);
		String m = msg(tipo, "reqAdd", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" Content-Type non aggiunto", CT_AGGIUNTO, ric(r, HttpConstants.CONTENT_TYPE));
		assertEquals(m+" "+H_CUSTOM+" non aggiunto", VALORE_AGGIUNTO, ric(r, H_CUSTOM));
		verificaConnettore(r);
	}

	// ---- CHECK --------------------------------------------------------------------------------------------------
	@Test public void reqCheckEchoErogazione() throws Exception { reqCheck(TipoServizio.EROGAZIONE, "reqCheckEcho", HttpRequestMethod.POST, true); }
	@Test public void reqCheckEchoFruizione() throws Exception { reqCheck(TipoServizio.FRUIZIONE, "reqCheckEcho", HttpRequestMethod.POST, true); }
	@Test public void reqCheckPingErogazione() throws Exception { reqCheck(TipoServizio.EROGAZIONE, "reqCheckPing", HttpRequestMethod.GET, false); }
	@Test public void reqCheckPingFruizione() throws Exception { reqCheck(TipoServizio.FRUIZIONE, "reqCheckPing", HttpRequestMethod.GET, false); }

	private void reqCheck(TipoServizio tipo, String azione, HttpRequestMethod method, boolean conPayload) throws Exception {
		Map<String,String> h = new HashMap<>();
		h.put(H_CUSTOM, "valore-custom");
		h.put(H_SANITY, "valore-sanity");
		HttpResponse r = invoca(tipo, azione, method, conPayload, conPayload?HttpConstants.CONTENT_TYPE_JSON:null, h);
		String m = msg(tipo, azione, r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		if(conPayload) assertEquals(m+" Content-Type alterato", HttpConstants.CONTENT_TYPE_JSON, ric(r, HttpConstants.CONTENT_TYPE));
		else assertNull(m+" Content-Type presente senza payload", ric(r, HttpConstants.CONTENT_TYPE));
		assertEquals(m+" "+H_CUSTOM+" alterato", "valore-custom", ric(r, H_CUSTOM));
		assertEquals(m+" "+H_SANITY+" alterato", "valore-sanity", ric(r, H_SANITY));
		verificaConnettore(r);
	}

	// ---- FAIL MODE ----------------------------------------------------------------------------------------------
	@Test public void reqUpdateFailBloccaErogazione() throws Exception { reqFailBlocca(TipoServizio.EROGAZIONE, "reqUpdateFailBlocca", true); }
	@Test public void reqUpdateFailBloccaFruizione() throws Exception { reqFailBlocca(TipoServizio.FRUIZIONE, "reqUpdateFailBlocca", true); }
	@Test public void reqUpdateFailIgnoraErogazione() throws Exception { reqUpdateFailIgnora(TipoServizio.EROGAZIONE); }
	@Test public void reqUpdateFailIgnoraFruizione() throws Exception { reqUpdateFailIgnora(TipoServizio.FRUIZIONE); }
	@Test public void reqAddFailBloccaErogazione() throws Exception { reqFailBlocca(TipoServizio.EROGAZIONE, "reqAddFailBlocca", false); }
	@Test public void reqAddFailBloccaFruizione() throws Exception { reqFailBlocca(TipoServizio.FRUIZIONE, "reqAddFailBlocca", false); }
	@Test public void reqAddFailIgnoraErogazione() throws Exception { reqAddOrUpsertFailIgnora(TipoServizio.EROGAZIONE, "reqAddFailIgnora"); }
	@Test public void reqAddFailIgnoraFruizione() throws Exception { reqAddOrUpsertFailIgnora(TipoServizio.FRUIZIONE, "reqAddFailIgnora"); }
	@Test public void reqUpdateOrAddFailBloccaErogazione() throws Exception { reqFailBlocca(TipoServizio.EROGAZIONE, "reqUpdateOrAddFailBlocca", false); }
	@Test public void reqUpdateOrAddFailBloccaFruizione() throws Exception { reqFailBlocca(TipoServizio.FRUIZIONE, "reqUpdateOrAddFailBlocca", false); }
	@Test public void reqUpdateOrAddFailIgnoraErogazione() throws Exception { reqAddOrUpsertFailIgnora(TipoServizio.EROGAZIONE, "reqUpdateOrAddFailIgnora"); }
	@Test public void reqUpdateOrAddFailIgnoraFruizione() throws Exception { reqAddOrUpsertFailIgnora(TipoServizio.FRUIZIONE, "reqUpdateOrAddFailIgnora"); }

	private void reqFailBlocca(TipoServizio tipo, String azione, boolean inviaCustom) throws Exception {
		Map<String,String> h = new HashMap<>();
		if(inviaCustom) h.put(H_CUSTOM, "valore-originale");
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, h);
		String m = msg(tipo, azione, r);
		assertErroreTrasformazioneRichiesta(m, r);
	}

	private void reqUpdateFailIgnora(TipoServizio tipo) throws Exception {
		Map<String,String> h = new HashMap<>();
		h.put(H_CUSTOM, "valore-originale");
		HttpResponse r = invoca(tipo, "reqUpdateFailIgnora", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, h);
		String m = msg(tipo, "reqUpdateFailIgnora", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" "+H_CUSTOM+" modificato nonostante 'ignora'", "valore-originale", ric(r, H_CUSTOM));
		verificaConnettore(r);
	}

	private void reqAddOrUpsertFailIgnora(TipoServizio tipo, String azione) throws Exception {
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, azione, r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNull(m+" "+H_CUSTOM+" aggiunto nonostante 'ignora'", ric(r, H_CUSTOM));
		verificaConnettore(r);
	}


	// ##############################################################################################################
	// RISPOSTA  (il backend genera gli header via returnHttpHeader; il client verifica gli header ricevuti)
	// ##############################################################################################################

	// ---- DELETE custom ------------------------------------------------------------------------------------------
	@Test public void respDeleteErogazione() throws Exception { respDelete(TipoServizio.EROGAZIONE); }
	@Test public void respDeleteFruizione() throws Exception { respDelete(TipoServizio.FRUIZIONE); }

	private void respDelete(TipoServizio tipo) throws Exception {
		HttpResponse r = invoca(tipo, "respDelete", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respDelete", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNull(m+" "+H_RESP_CUSTOM+" non eliminato in risposta", r.getHeaderFirstValue(H_RESP_CUSTOM));
		assertNotNull(m+" "+H_RESP_SANITY+" (controllo) assente in risposta", r.getHeaderFirstValue(H_RESP_SANITY));
		verificaConnettore(r);
	}

	// delete Content-Type in risposta con body -> errore
	@Test public void respDeleteContentTypeErogazione() throws Exception { respDeleteContentType(TipoServizio.EROGAZIONE); }
	@Test public void respDeleteContentTypeFruizione() throws Exception { respDeleteContentType(TipoServizio.FRUIZIONE); }

	private void respDeleteContentType(TipoServizio tipo) throws Exception {
		HttpResponse r = invoca(tipo, "respDeleteCt", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respDeleteContentType", r);
		assertErroreContentTypeRispostaMancante(m, r);
	}

	// ---- UPDATE -------------------------------------------------------------------------------------------------
	@Test public void respUpdateErogazione() throws Exception { respUpdate(TipoServizio.EROGAZIONE); }
	@Test public void respUpdateFruizione() throws Exception { respUpdate(TipoServizio.FRUIZIONE); }

	private void respUpdate(TipoServizio tipo) throws Exception {
		HttpResponse r = invoca(tipo, "respUpdate", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respUpdate", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" "+H_RESP_CUSTOM+" non aggiornato in risposta", VALORE_AGGIORNATO, r.getHeaderFirstValue(H_RESP_CUSTOM));
		assertEquals(m+" Content-Type non aggiornato in risposta", CT_AGGIORNATO, r.getContentType());
		verificaConnettore(r);
	}

	// ---- UPDATE_OR_ADD ------------------------------------------------------------------------------------------
	@Test public void respUpdateOrAddPresenteErogazione() throws Exception { respUpdateOrAdd(TipoServizio.EROGAZIONE, "respUpdateOrAddPresente"); }
	@Test public void respUpdateOrAddPresenteFruizione() throws Exception { respUpdateOrAdd(TipoServizio.FRUIZIONE, "respUpdateOrAddPresente"); }
	@Test public void respUpdateOrAddAssenteErogazione() throws Exception { respUpdateOrAdd(TipoServizio.EROGAZIONE, "respUpdateOrAddAssente"); }
	@Test public void respUpdateOrAddAssenteFruizione() throws Exception { respUpdateOrAdd(TipoServizio.FRUIZIONE, "respUpdateOrAddAssente"); }

	private void respUpdateOrAdd(TipoServizio tipo, String azione) throws Exception {
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, azione, r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" "+H_RESP_CUSTOM+" non impostato (updateOrAdd) in risposta", VALORE_UPSERT, r.getHeaderFirstValue(H_RESP_CUSTOM));
		verificaConnettore(r);
	}

	// ---- ADD ----------------------------------------------------------------------------------------------------
	@Test public void respAddErogazione() throws Exception { respAdd(TipoServizio.EROGAZIONE); }
	@Test public void respAddFruizione() throws Exception { respAdd(TipoServizio.FRUIZIONE); }

	private void respAdd(TipoServizio tipo) throws Exception {
		HttpResponse r = invoca(tipo, "respAdd", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respAdd", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" "+H_RESP_ADD+" non aggiunto in risposta", VALORE_AGGIUNTO, r.getHeaderFirstValue(H_RESP_ADD));
		verificaConnettore(r);
	}

	// ---- CHECK --------------------------------------------------------------------------------------------------
	@Test public void respCheckEchoErogazione() throws Exception { respCheck(TipoServizio.EROGAZIONE, "respCheckEcho", HttpRequestMethod.POST, true); }
	@Test public void respCheckEchoFruizione() throws Exception { respCheck(TipoServizio.FRUIZIONE, "respCheckEcho", HttpRequestMethod.POST, true); }
	@Test public void respCheckPingErogazione() throws Exception { respCheck(TipoServizio.EROGAZIONE, "respCheckPing", HttpRequestMethod.GET, false); }
	@Test public void respCheckPingFruizione() throws Exception { respCheck(TipoServizio.FRUIZIONE, "respCheckPing", HttpRequestMethod.GET, false); }

	private void respCheck(TipoServizio tipo, String azione, HttpRequestMethod method, boolean conPayload) throws Exception {
		HttpResponse r = invoca(tipo, azione, method, conPayload, conPayload?HttpConstants.CONTENT_TYPE_JSON:null, null);
		String m = msg(tipo, azione, r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" "+H_RESP_CUSTOM+" alterato in risposta", "valore-custom", r.getHeaderFirstValue(H_RESP_CUSTOM));
		assertEquals(m+" "+H_RESP_SANITY+" alterato in risposta", "valore-sanity", r.getHeaderFirstValue(H_RESP_SANITY));
		verificaConnettore(r);
	}

	// ---- FAIL MODE ----------------------------------------------------------------------------------------------
	@Test public void respUpdateFailBloccaErogazione() throws Exception { respFailBlocca(TipoServizio.EROGAZIONE, "respUpdateFailBlocca"); }
	@Test public void respUpdateFailBloccaFruizione() throws Exception { respFailBlocca(TipoServizio.FRUIZIONE, "respUpdateFailBlocca"); }
	@Test public void respUpdateFailIgnoraErogazione() throws Exception { respUpdateFailIgnora(TipoServizio.EROGAZIONE); }
	@Test public void respUpdateFailIgnoraFruizione() throws Exception { respUpdateFailIgnora(TipoServizio.FRUIZIONE); }
	@Test public void respAddFailBloccaErogazione() throws Exception { respFailBlocca(TipoServizio.EROGAZIONE, "respAddFailBlocca"); }
	@Test public void respAddFailBloccaFruizione() throws Exception { respFailBlocca(TipoServizio.FRUIZIONE, "respAddFailBlocca"); }
	@Test public void respAddFailIgnoraErogazione() throws Exception { respFailIgnoraAssente(TipoServizio.EROGAZIONE, "respAddFailIgnora", H_RESP_ADD); }
	@Test public void respAddFailIgnoraFruizione() throws Exception { respFailIgnoraAssente(TipoServizio.FRUIZIONE, "respAddFailIgnora", H_RESP_ADD); }
	@Test public void respUpdateOrAddFailBloccaErogazione() throws Exception { respFailBlocca(TipoServizio.EROGAZIONE, "respUpdateOrAddFailBlocca"); }
	@Test public void respUpdateOrAddFailBloccaFruizione() throws Exception { respFailBlocca(TipoServizio.FRUIZIONE, "respUpdateOrAddFailBlocca"); }
	@Test public void respUpdateOrAddFailIgnoraErogazione() throws Exception { respFailIgnoraAssente(TipoServizio.EROGAZIONE, "respUpdateOrAddFailIgnora", H_RESP_CUSTOM); }
	@Test public void respUpdateOrAddFailIgnoraFruizione() throws Exception { respFailIgnoraAssente(TipoServizio.FRUIZIONE, "respUpdateOrAddFailIgnora", H_RESP_CUSTOM); }

	private void respFailBlocca(TipoServizio tipo, String azione) throws Exception {
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, azione, r);
		assertErroreTrasformazioneRisposta(m, r);
	}

	private void respUpdateFailIgnora(TipoServizio tipo) throws Exception {
		HttpResponse r = invoca(tipo, "respUpdateFailIgnora", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respUpdateFailIgnora", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" "+H_RESP_CUSTOM+" modificato nonostante 'ignora'", "valore-originale", r.getHeaderFirstValue(H_RESP_CUSTOM));
		verificaConnettore(r);
	}

	private void respFailIgnoraAssente(TipoServizio tipo, String azione, String header) throws Exception {
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, azione, r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNull(m+" "+header+" aggiunto nonostante 'ignora'", r.getHeaderFirstValue(header));
		verificaConnettore(r);
	}


	// ##############################################################################################################
	// CONTENUTO ABILITATO
	// (la regola di conversione va aggiunta a mano sulle porte: vedi istruzioni; l'archivio fornisce solo la plumbing)
	// ##############################################################################################################

	// richiesta: conversione 'HTTP Payload vuoto' -> nessun Content-Type al backend
	@Test public void reqContentEmptyErogazione() throws Exception { reqContentCtNull(TipoServizio.EROGAZIONE, "reqContentEmpty"); }
	@Test public void reqContentEmptyFruizione() throws Exception { reqContentCtNull(TipoServizio.FRUIZIONE, "reqContentEmpty"); }
	// richiesta: conversione 'Alimentazione Contesto' + delete Content-Type -> Content-Type eliminato
	@Test public void reqContentContextErogazione() throws Exception { reqContentCtNull(TipoServizio.EROGAZIONE, "reqContentContext"); }
	@Test public void reqContentContextFruizione() throws Exception { reqContentCtNull(TipoServizio.FRUIZIONE, "reqContentContext"); }

	private void reqContentCtNull(TipoServizio tipo, String azione) throws Exception {
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, azione, r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNull(m+" Content-Type presente al backend", ric(r, HttpConstants.CONTENT_TYPE));
		verificaConnettore(r);
	}

	// richiesta: conversione 'Template' (produce JSON) + delete Content-Type -> Content-Type dalla conversione (delete ignorata)
	@Test public void reqContentTemplateErogazione() throws Exception { reqContentTemplate(TipoServizio.EROGAZIONE); }
	@Test public void reqContentTemplateFruizione() throws Exception { reqContentTemplate(TipoServizio.FRUIZIONE); }

	private void reqContentTemplate(TipoServizio tipo) throws Exception {
		HttpResponse r = invoca(tipo, "reqContentTemplate", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "reqContentTemplate", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNotNull(m+" Content-Type eliminato: con Template deve prevalere la conversione", ric(r, HttpConstants.CONTENT_TYPE));
		verificaConnettore(r);
	}

	// risposta: conversione 'HTTP Payload vuoto' -> nessun Content-Type verso il client
	@Test public void respContentEmptyErogazione() throws Exception { respContentEmpty(TipoServizio.EROGAZIONE); }
	@Test public void respContentEmptyFruizione() throws Exception { respContentEmpty(TipoServizio.FRUIZIONE); }

	private void respContentEmpty(TipoServizio tipo) throws Exception {
		HttpResponse r = invoca(tipo, "respContentEmpty", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respContentEmpty", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNull(m+" Content-Type presente in risposta nonostante payload vuoto", r.getContentType());
		verificaConnettore(r);
	}

	// risposta: conversione 'Alimentazione Contesto' + delete Content-Type -> body presente + CT eliminato -> errore
	@Test public void respContentContextErogazione() throws Exception { respContentContext(TipoServizio.EROGAZIONE); }
	@Test public void respContentContextFruizione() throws Exception { respContentContext(TipoServizio.FRUIZIONE); }

	private void respContentContext(TipoServizio tipo) throws Exception {
		HttpResponse r = invoca(tipo, "respContentContext", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respContentContext", r);
		assertErroreContentTypeRispostaMancante(m+" (risposta con body senza Content-Type)", r);
	}

	// risposta: conversione 'Template' (produce JSON) + delete Content-Type -> Content-Type dalla conversione (delete ignorata)
	@Test public void respContentTemplateErogazione() throws Exception { respContentTemplate(TipoServizio.EROGAZIONE); }
	@Test public void respContentTemplateFruizione() throws Exception { respContentTemplate(TipoServizio.FRUIZIONE); }

	private void respContentTemplate(TipoServizio tipo) throws Exception {
		HttpResponse r = invoca(tipo, "respContentTemplate", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respContentTemplate", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNotNull(m+" Content-Type eliminato in risposta: con Template deve prevalere la conversione", r.getContentType());
		verificaConnettore(r);
	}


	// ##############################################################################################################
	// Utility
	// ##############################################################################################################

	private static String ric(HttpResponse r, String nome) {
		return r.getHeaderFirstValue(PREFIX+nome);
	}

	private static String msg(TipoServizio tipo, String scenario, HttpResponse r) {
		return "["+tipo+"]["+scenario+"] idTransazione:"+r.getHeaderFirstValue("GovWay-Transaction-ID");
	}

	private static final String H_ERROR_TYPE = "GovWay-Transaction-ErrorType";

	// Di default questo ambiente riporta l'error-type generico (BadRequest per il 400, InvalidResponse per il 502).
	// Via JMX si abilita l'error-type SPECIFICO, così l'header GovWay-Transaction-ErrorType riporta
	// TransformationRuleRequestFailed / TransformationRuleResponseFailed. Ripristinato in coda.
	@BeforeClass public static void abilitaErrorTypeSpecifico() { setErrorTypeSpecifico(true); }
	@AfterClass public static void ripristinaErrorTypeSpecifico() { setErrorTypeSpecifico(false); }

	private static void setErrorTypeSpecifico(boolean enabled) {
		String value = enabled ? "true" : "false";
		List<Map<String,String>> params = List.of(
			Map.of("resourceName","ConfigurazionePdD","attributeName","transactionErrorForceSpecificTypeInternalBadRequest","attributeBooleanValue",value),
			Map.of("resourceName","ConfigurazionePdD","attributeName","transactionErrorForceSpecificTypeBadResponse","attributeBooleanValue",value),
			Map.of("resourceName","ConfigurazionePdD","attributeName","transactionErrorForceSpecificTypeInternalResponseError","attributeBooleanValue",value)
		);
		for(Map<String,String> qp : params) {
			String jmxUrl = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.buildUrl(qp, System.getProperty("govway_base_path")+"/check");
			try {
				HttpUtilities.check(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password"));
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	// errore di trasformazione in RICHIESTA (identificazione-fallita=blocca): HTTP 400 + TransformationRuleRequestFailed.
	private static void assertErroreTrasformazioneRichiesta(String m, HttpResponse r) {
		assertEquals(m+" atteso errore trasformazione richiesta", 400, r.getResultHTTPOperation());
		assertEquals(m+" "+H_ERROR_TYPE, "TransformationRuleRequestFailed", r.getHeaderFirstValue(H_ERROR_TYPE));
	}

	// errore di trasformazione in RISPOSTA (identificazione-fallita=blocca): HTTP 502 + TransformationRuleResponseFailed.
	private static void assertErroreTrasformazioneRisposta(String m, HttpResponse r) {
		assertEquals(m+" atteso errore trasformazione risposta", 502, r.getResultHTTPOperation());
		assertEquals(m+" "+H_ERROR_TYPE, "TransformationRuleResponseFailed", r.getHeaderFirstValue(H_ERROR_TYPE));
	}

	// Content-Type eliminato su risposta REST con body: errore lato risposta -> HTTP 502 (non è un transformation-rule-failed,
	// quindi si verifica solo il codice, stabile e comunque diverso dal 404 di config mancante).
	private static void assertErroreContentTypeRispostaMancante(String m, HttpResponse r) {
		assertEquals(m+" atteso errore risposta (502) per Content-Type mancante in risposta con body", 502, r.getResultHTTPOperation());
	}

	// verifica, sui casi di successo, che sia stato realmente utilizzato il connettore richiesto (diagnostico)
	private void verificaConnettore(HttpResponse r) throws Exception {
		String idTransazione = r.getHeaderFirstValue("GovWay-Transaction-ID");
		long esitoOk = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		DBVerifier.verify(idTransazione, esitoOk, this.libraryMode);
	}

	private HttpResponse invoca(TipoServizio tipo, String azione, HttpRequestMethod method, boolean conPayload,
			String contentType, Map<String,String> headerRichiesta) throws Exception {
		String servizio = SERVIZIO.get(azione);
		if(servizio==null) throw new Exception("Servizio non mappato per azione '"+azione+"'");
		String url = tipo == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/in/SoggettoInternoTest/"+servizio+"/v1/"+azione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+servizio+"/v1/"+azione;
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(method);
		if(contentType!=null) request.setContentType(contentType);
		if(headerRichiesta!=null) {
			for(Map.Entry<String,String> e : headerRichiesta.entrySet()) request.addHeader(e.getKey(), e.getValue());
		}
		if(conPayload) request.setContent(Bodies.getJson(Bodies.SMALL_SIZE).getBytes());
		request.setUrl(url);
		if(this.libraryMode!=null) {
			this.libraryMode.patchRequest(request);
		}
		return HttpUtilities.httpInvoke(request);
	}

}
