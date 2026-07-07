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
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * RestTrasformazioneHeaderTest
 *
 * Verifica completa delle trasformazioni degli header HTTP (add / update / updateOrAdd / delete), incluso il
 * Content-Type, in richiesta e in risposta, per erogazione e fruizione REST.
 *
 * Unico accordo parte comune 'TestTrasformazioneHeader', con una erogazione/fruizione (servizio) per tipo di
 * operazione: TestTrasfDelete, TestTrasfUpdate, TestTrasfUpdateOrAdd, TestTrasfAdd, TestTrasfCheck, TestTrasfContent.
 * Il client invoca <code>.../SoggettoInternoTest/{servizio}/v1/{azione}</code> (mappa azione->servizio interna);
 * il connettore associato all'azione punta a TestService (echo o ping) e contiene i query parameter necessari:
 * <ul>
 *   <li><b>richiesta</b>: <code>replyHttpHeader=Content-Type,X-Custom-Header,X-Sanity-Header&amp;replyPrefixHttpHeader=x-ricevuto-</code>
 *       -> il backend rimanda gli header ricevuti (post-trasformazione) prefissati con '{@value #PREFIX}';</li>
 *   <li><b>risposta</b>: <code>returnHttpHeader=Nome:valore</code> -> il backend GENERA gli header di risposta indicati,
 *       che la trasformazione poi modifica e il client verifica.</li>
 * </ul>
 * Errori attesi verificati con codice preciso: 400 (TransformationRuleRequestFailed) per il blocca in richiesta,
 * 502 (TransformationRuleResponseFailed) per il blocca in risposta, 500 per l'eliminazione del Content-Type in risposta con body.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RestTrasformazioneHeaderTest extends ConfigLoader {

	private static final String PREFIX = "x-ricevuto-";

	// una erogazione/fruizione (servizio) per tipo di operazione; tutte sull unico accordo parte comune TestTrasformazioneHeader
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
	@Test public void reqDeleteEchoErogazione() throws UtilsException { reqDelete(TipoServizio.EROGAZIONE, "reqDeleteEcho", HttpRequestMethod.POST, true); }
	@Test public void reqDeleteEchoFruizione() throws UtilsException { reqDelete(TipoServizio.FRUIZIONE, "reqDeleteEcho", HttpRequestMethod.POST, true); }
	@Test public void reqDeletePingErogazione() throws UtilsException { reqDelete(TipoServizio.EROGAZIONE, "reqDeletePing", HttpRequestMethod.GET, false); }
	@Test public void reqDeletePingFruizione() throws UtilsException { reqDelete(TipoServizio.FRUIZIONE, "reqDeletePing", HttpRequestMethod.GET, false); }

	private static void reqDelete(TipoServizio tipo, String azione, HttpRequestMethod method, boolean conPayload) throws UtilsException {
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
	}

	// delete su header non presente in origine -> no-op (uso reqDeletePing senza inviare X-Custom-Header)
	@Test public void reqDeleteAssenteErogazione() throws UtilsException { reqDeleteAssente(TipoServizio.EROGAZIONE); }
	@Test public void reqDeleteAssenteFruizione() throws UtilsException { reqDeleteAssente(TipoServizio.FRUIZIONE); }

	private static void reqDeleteAssente(TipoServizio tipo) throws UtilsException {
		Map<String,String> h = new HashMap<>();
		h.put(H_SANITY, "valore-di-controllo");
		HttpResponse r = invoca(tipo, "reqDeletePing", HttpRequestMethod.GET, false, null, h);
		String m = msg(tipo, "reqDeleteAssente", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNull(m+" "+H_CUSTOM+" presente (non inviato)", ric(r, H_CUSTOM));
		assertNotNull(m+" "+H_SANITY+" (controllo) assente", ric(r, H_SANITY));
	}

	// ---- UPDATE -------------------------------------------------------------------------------------------------
	@Test public void reqUpdatePresenteErogazione() throws UtilsException { reqUpdate(TipoServizio.EROGAZIONE, true); }
	@Test public void reqUpdatePresenteFruizione() throws UtilsException { reqUpdate(TipoServizio.FRUIZIONE, true); }
	@Test public void reqUpdateAssenteErogazione() throws UtilsException { reqUpdate(TipoServizio.EROGAZIONE, false); }
	@Test public void reqUpdateAssenteFruizione() throws UtilsException { reqUpdate(TipoServizio.FRUIZIONE, false); }

	private static void reqUpdate(TipoServizio tipo, boolean inviaCustom) throws UtilsException {
		Map<String,String> h = new HashMap<>();
		if(inviaCustom) h.put(H_CUSTOM, "valore-originale");
		HttpResponse r = invoca(tipo, "reqUpdate", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, h);
		String m = msg(tipo, "reqUpdate inviaCustom="+inviaCustom, r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" Content-Type non aggiornato", CT_AGGIORNATO, ric(r, HttpConstants.CONTENT_TYPE));
		if(inviaCustom) assertEquals(m+" "+H_CUSTOM+" non aggiornato", VALORE_AGGIORNATO, ric(r, H_CUSTOM));
		else assertNull(m+" "+H_CUSTOM+" aggiunto da update su header assente", ric(r, H_CUSTOM));
	}

	// ---- UPDATE_OR_ADD ------------------------------------------------------------------------------------------
	@Test public void reqUpdateOrAddPresenteErogazione() throws UtilsException { reqUpdateOrAddPresente(TipoServizio.EROGAZIONE); }
	@Test public void reqUpdateOrAddPresenteFruizione() throws UtilsException { reqUpdateOrAddPresente(TipoServizio.FRUIZIONE); }

	private static void reqUpdateOrAddPresente(TipoServizio tipo) throws UtilsException {
		Map<String,String> h = new HashMap<>();
		h.put(H_CUSTOM, "valore-originale");
		HttpResponse r = invoca(tipo, "reqUpdateOrAddEcho", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, h);
		String m = msg(tipo, "reqUpdateOrAdd presente", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" Content-Type non aggiornato", CT_AGGIORNATO, ric(r, HttpConstants.CONTENT_TYPE));
		assertEquals(m+" "+H_CUSTOM+" non aggiornato", VALORE_UPSERT, ric(r, H_CUSTOM));
	}

	@Test public void reqUpdateOrAddAssenteErogazione() throws UtilsException { reqUpdateOrAddAssente(TipoServizio.EROGAZIONE); }
	@Test public void reqUpdateOrAddAssenteFruizione() throws UtilsException { reqUpdateOrAddAssente(TipoServizio.FRUIZIONE); }

	private static void reqUpdateOrAddAssente(TipoServizio tipo) throws UtilsException {
		HttpResponse r = invoca(tipo, "reqUpdateOrAddPing", HttpRequestMethod.GET, false, null, null);
		String m = msg(tipo, "reqUpdateOrAdd assente", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" Content-Type non aggiunto", CT_AGGIORNATO, ric(r, HttpConstants.CONTENT_TYPE));
		assertEquals(m+" "+H_CUSTOM+" non aggiunto", VALORE_UPSERT, ric(r, H_CUSTOM));
	}

	// ---- ADD ----------------------------------------------------------------------------------------------------
	@Test public void reqAddErogazione() throws UtilsException { reqAdd(TipoServizio.EROGAZIONE); }
	@Test public void reqAddFruizione() throws UtilsException { reqAdd(TipoServizio.FRUIZIONE); }

	private static void reqAdd(TipoServizio tipo) throws UtilsException {
		HttpResponse r = invoca(tipo, "reqAdd", HttpRequestMethod.GET, false, null, null);
		String m = msg(tipo, "reqAdd", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" Content-Type non aggiunto", CT_AGGIUNTO, ric(r, HttpConstants.CONTENT_TYPE));
		assertEquals(m+" "+H_CUSTOM+" non aggiunto", VALORE_AGGIUNTO, ric(r, H_CUSTOM));
	}

	// ---- CHECK --------------------------------------------------------------------------------------------------
	@Test public void reqCheckEchoErogazione() throws UtilsException { reqCheck(TipoServizio.EROGAZIONE, "reqCheckEcho", HttpRequestMethod.POST, true); }
	@Test public void reqCheckEchoFruizione() throws UtilsException { reqCheck(TipoServizio.FRUIZIONE, "reqCheckEcho", HttpRequestMethod.POST, true); }
	@Test public void reqCheckPingErogazione() throws UtilsException { reqCheck(TipoServizio.EROGAZIONE, "reqCheckPing", HttpRequestMethod.GET, false); }
	@Test public void reqCheckPingFruizione() throws UtilsException { reqCheck(TipoServizio.FRUIZIONE, "reqCheckPing", HttpRequestMethod.GET, false); }

	private static void reqCheck(TipoServizio tipo, String azione, HttpRequestMethod method, boolean conPayload) throws UtilsException {
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
	}

	// ---- FAIL MODE ----------------------------------------------------------------------------------------------
	@Test public void reqUpdateFailBloccaErogazione() throws UtilsException { reqFailBlocca(TipoServizio.EROGAZIONE, "reqUpdateFailBlocca", true); }
	@Test public void reqUpdateFailBloccaFruizione() throws UtilsException { reqFailBlocca(TipoServizio.FRUIZIONE, "reqUpdateFailBlocca", true); }
	@Test public void reqUpdateFailIgnoraErogazione() throws UtilsException { reqUpdateFailIgnora(TipoServizio.EROGAZIONE); }
	@Test public void reqUpdateFailIgnoraFruizione() throws UtilsException { reqUpdateFailIgnora(TipoServizio.FRUIZIONE); }
	@Test public void reqAddFailBloccaErogazione() throws UtilsException { reqFailBlocca(TipoServizio.EROGAZIONE, "reqAddFailBlocca", false); }
	@Test public void reqAddFailBloccaFruizione() throws UtilsException { reqFailBlocca(TipoServizio.FRUIZIONE, "reqAddFailBlocca", false); }
	@Test public void reqAddFailIgnoraErogazione() throws UtilsException { reqAddOrUpsertFailIgnora(TipoServizio.EROGAZIONE, "reqAddFailIgnora"); }
	@Test public void reqAddFailIgnoraFruizione() throws UtilsException { reqAddOrUpsertFailIgnora(TipoServizio.FRUIZIONE, "reqAddFailIgnora"); }
	@Test public void reqUpdateOrAddFailBloccaErogazione() throws UtilsException { reqFailBlocca(TipoServizio.EROGAZIONE, "reqUpdateOrAddFailBlocca", false); }
	@Test public void reqUpdateOrAddFailBloccaFruizione() throws UtilsException { reqFailBlocca(TipoServizio.FRUIZIONE, "reqUpdateOrAddFailBlocca", false); }
	@Test public void reqUpdateOrAddFailIgnoraErogazione() throws UtilsException { reqAddOrUpsertFailIgnora(TipoServizio.EROGAZIONE, "reqUpdateOrAddFailIgnora"); }
	@Test public void reqUpdateOrAddFailIgnoraFruizione() throws UtilsException { reqAddOrUpsertFailIgnora(TipoServizio.FRUIZIONE, "reqUpdateOrAddFailIgnora"); }

	private static void reqFailBlocca(TipoServizio tipo, String azione, boolean inviaCustom) throws UtilsException {
		Map<String,String> h = new HashMap<>();
		if(inviaCustom) h.put(H_CUSTOM, "valore-originale");
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, h);
		String m = msg(tipo, azione, r);
		assertErroreTrasformazioneRichiesta(m, r);
	}

	private static void reqUpdateFailIgnora(TipoServizio tipo) throws UtilsException {
		Map<String,String> h = new HashMap<>();
		h.put(H_CUSTOM, "valore-originale");
		HttpResponse r = invoca(tipo, "reqUpdateFailIgnora", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, h);
		String m = msg(tipo, "reqUpdateFailIgnora", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" "+H_CUSTOM+" modificato nonostante 'ignora'", "valore-originale", ric(r, H_CUSTOM));
	}

	private static void reqAddOrUpsertFailIgnora(TipoServizio tipo, String azione) throws UtilsException {
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, azione, r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNull(m+" "+H_CUSTOM+" aggiunto nonostante 'ignora'", ric(r, H_CUSTOM));
	}


	// ##############################################################################################################
	// RISPOSTA  (il backend genera gli header via returnHttpHeader; il client verifica gli header ricevuti)
	// ##############################################################################################################

	// ---- DELETE custom ------------------------------------------------------------------------------------------
	@Test public void respDeleteErogazione() throws UtilsException { respDelete(TipoServizio.EROGAZIONE); }
	@Test public void respDeleteFruizione() throws UtilsException { respDelete(TipoServizio.FRUIZIONE); }

	private static void respDelete(TipoServizio tipo) throws UtilsException {
		HttpResponse r = invoca(tipo, "respDelete", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respDelete", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNull(m+" "+H_RESP_CUSTOM+" non eliminato in risposta", r.getHeaderFirstValue(H_RESP_CUSTOM));
		assertNotNull(m+" "+H_RESP_SANITY+" (controllo) assente in risposta", r.getHeaderFirstValue(H_RESP_SANITY));
	}

	// delete Content-Type in risposta con body -> errore
	@Test public void respDeleteContentTypeErogazione() throws UtilsException { respDeleteContentType(TipoServizio.EROGAZIONE); }
	@Test public void respDeleteContentTypeFruizione() throws UtilsException { respDeleteContentType(TipoServizio.FRUIZIONE); }

	private static void respDeleteContentType(TipoServizio tipo) throws UtilsException {
		HttpResponse r = invoca(tipo, "respDeleteCt", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respDeleteContentType", r);
		assertErroreContentTypeRispostaMancante(m, r);
	}

	// ---- UPDATE -------------------------------------------------------------------------------------------------
	@Test public void respUpdateErogazione() throws UtilsException { respUpdate(TipoServizio.EROGAZIONE); }
	@Test public void respUpdateFruizione() throws UtilsException { respUpdate(TipoServizio.FRUIZIONE); }

	private static void respUpdate(TipoServizio tipo) throws UtilsException {
		HttpResponse r = invoca(tipo, "respUpdate", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respUpdate", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" "+H_RESP_CUSTOM+" non aggiornato in risposta", VALORE_AGGIORNATO, r.getHeaderFirstValue(H_RESP_CUSTOM));
		assertEquals(m+" Content-Type non aggiornato in risposta", CT_AGGIORNATO, r.getContentType());
	}

	// ---- UPDATE_OR_ADD ------------------------------------------------------------------------------------------
	@Test public void respUpdateOrAddPresenteErogazione() throws UtilsException { respUpdateOrAdd(TipoServizio.EROGAZIONE, "respUpdateOrAddPresente"); }
	@Test public void respUpdateOrAddPresenteFruizione() throws UtilsException { respUpdateOrAdd(TipoServizio.FRUIZIONE, "respUpdateOrAddPresente"); }
	@Test public void respUpdateOrAddAssenteErogazione() throws UtilsException { respUpdateOrAdd(TipoServizio.EROGAZIONE, "respUpdateOrAddAssente"); }
	@Test public void respUpdateOrAddAssenteFruizione() throws UtilsException { respUpdateOrAdd(TipoServizio.FRUIZIONE, "respUpdateOrAddAssente"); }

	private static void respUpdateOrAdd(TipoServizio tipo, String azione) throws UtilsException {
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, azione, r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" "+H_RESP_CUSTOM+" non impostato (updateOrAdd) in risposta", VALORE_UPSERT, r.getHeaderFirstValue(H_RESP_CUSTOM));
	}

	// ---- ADD ----------------------------------------------------------------------------------------------------
	@Test public void respAddErogazione() throws UtilsException { respAdd(TipoServizio.EROGAZIONE); }
	@Test public void respAddFruizione() throws UtilsException { respAdd(TipoServizio.FRUIZIONE); }

	private static void respAdd(TipoServizio tipo) throws UtilsException {
		HttpResponse r = invoca(tipo, "respAdd", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respAdd", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" "+H_RESP_ADD+" non aggiunto in risposta", VALORE_AGGIUNTO, r.getHeaderFirstValue(H_RESP_ADD));
	}

	// ---- CHECK --------------------------------------------------------------------------------------------------
	@Test public void respCheckEchoErogazione() throws UtilsException { respCheck(TipoServizio.EROGAZIONE, "respCheckEcho", HttpRequestMethod.POST, true); }
	@Test public void respCheckEchoFruizione() throws UtilsException { respCheck(TipoServizio.FRUIZIONE, "respCheckEcho", HttpRequestMethod.POST, true); }
	@Test public void respCheckPingErogazione() throws UtilsException { respCheck(TipoServizio.EROGAZIONE, "respCheckPing", HttpRequestMethod.GET, false); }
	@Test public void respCheckPingFruizione() throws UtilsException { respCheck(TipoServizio.FRUIZIONE, "respCheckPing", HttpRequestMethod.GET, false); }

	private static void respCheck(TipoServizio tipo, String azione, HttpRequestMethod method, boolean conPayload) throws UtilsException {
		HttpResponse r = invoca(tipo, azione, method, conPayload, conPayload?HttpConstants.CONTENT_TYPE_JSON:null, null);
		String m = msg(tipo, azione, r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" "+H_RESP_CUSTOM+" alterato in risposta", "valore-custom", r.getHeaderFirstValue(H_RESP_CUSTOM));
		assertEquals(m+" "+H_RESP_SANITY+" alterato in risposta", "valore-sanity", r.getHeaderFirstValue(H_RESP_SANITY));
	}

	// ---- FAIL MODE ----------------------------------------------------------------------------------------------
	@Test public void respUpdateFailBloccaErogazione() throws UtilsException { respFailBlocca(TipoServizio.EROGAZIONE, "respUpdateFailBlocca"); }
	@Test public void respUpdateFailBloccaFruizione() throws UtilsException { respFailBlocca(TipoServizio.FRUIZIONE, "respUpdateFailBlocca"); }
	@Test public void respUpdateFailIgnoraErogazione() throws UtilsException { respUpdateFailIgnora(TipoServizio.EROGAZIONE); }
	@Test public void respUpdateFailIgnoraFruizione() throws UtilsException { respUpdateFailIgnora(TipoServizio.FRUIZIONE); }
	@Test public void respAddFailBloccaErogazione() throws UtilsException { respFailBlocca(TipoServizio.EROGAZIONE, "respAddFailBlocca"); }
	@Test public void respAddFailBloccaFruizione() throws UtilsException { respFailBlocca(TipoServizio.FRUIZIONE, "respAddFailBlocca"); }
	@Test public void respAddFailIgnoraErogazione() throws UtilsException { respFailIgnoraAssente(TipoServizio.EROGAZIONE, "respAddFailIgnora", H_RESP_ADD); }
	@Test public void respAddFailIgnoraFruizione() throws UtilsException { respFailIgnoraAssente(TipoServizio.FRUIZIONE, "respAddFailIgnora", H_RESP_ADD); }
	@Test public void respUpdateOrAddFailBloccaErogazione() throws UtilsException { respFailBlocca(TipoServizio.EROGAZIONE, "respUpdateOrAddFailBlocca"); }
	@Test public void respUpdateOrAddFailBloccaFruizione() throws UtilsException { respFailBlocca(TipoServizio.FRUIZIONE, "respUpdateOrAddFailBlocca"); }
	@Test public void respUpdateOrAddFailIgnoraErogazione() throws UtilsException { respFailIgnoraAssente(TipoServizio.EROGAZIONE, "respUpdateOrAddFailIgnora", H_RESP_CUSTOM); }
	@Test public void respUpdateOrAddFailIgnoraFruizione() throws UtilsException { respFailIgnoraAssente(TipoServizio.FRUIZIONE, "respUpdateOrAddFailIgnora", H_RESP_CUSTOM); }

	private static void respFailBlocca(TipoServizio tipo, String azione) throws UtilsException {
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, azione, r);
		assertErroreTrasformazioneRisposta(m, r);
	}

	private static void respUpdateFailIgnora(TipoServizio tipo) throws UtilsException {
		HttpResponse r = invoca(tipo, "respUpdateFailIgnora", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respUpdateFailIgnora", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertEquals(m+" "+H_RESP_CUSTOM+" modificato nonostante 'ignora'", "valore-originale", r.getHeaderFirstValue(H_RESP_CUSTOM));
	}

	private static void respFailIgnoraAssente(TipoServizio tipo, String azione, String header) throws UtilsException {
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, azione, r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNull(m+" "+header+" aggiunto nonostante 'ignora'", r.getHeaderFirstValue(header));
	}


	// ##############################################################################################################
	// CONTENUTO ABILITATO
	// (la regola di conversione va aggiunta a mano sulle porte: vedi istruzioni; qui l'archivio fornisce solo la plumbing)
	// ##############################################################################################################

	// richiesta: conversione 'HTTP Payload vuoto' -> nessun Content-Type al backend
	@Test public void reqContentEmptyErogazione() throws UtilsException { reqContentCtNull(TipoServizio.EROGAZIONE, "reqContentEmpty"); }
	@Test public void reqContentEmptyFruizione() throws UtilsException { reqContentCtNull(TipoServizio.FRUIZIONE, "reqContentEmpty"); }
	// richiesta: conversione 'Alimentazione Contesto' + delete Content-Type -> Content-Type eliminato
	@Test public void reqContentContextErogazione() throws UtilsException { reqContentCtNull(TipoServizio.EROGAZIONE, "reqContentContext"); }
	@Test public void reqContentContextFruizione() throws UtilsException { reqContentCtNull(TipoServizio.FRUIZIONE, "reqContentContext"); }

	private static void reqContentCtNull(TipoServizio tipo, String azione) throws UtilsException {
		HttpResponse r = invoca(tipo, azione, HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, azione, r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNull(m+" Content-Type presente al backend", ric(r, HttpConstants.CONTENT_TYPE));
	}

	// richiesta: conversione 'Template' (produce JSON) + delete Content-Type -> Content-Type dalla conversione (delete ignorata)
	@Test public void reqContentTemplateErogazione() throws UtilsException { reqContentTemplate(TipoServizio.EROGAZIONE); }
	@Test public void reqContentTemplateFruizione() throws UtilsException { reqContentTemplate(TipoServizio.FRUIZIONE); }

	private static void reqContentTemplate(TipoServizio tipo) throws UtilsException {
		HttpResponse r = invoca(tipo, "reqContentTemplate", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "reqContentTemplate", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNotNull(m+" Content-Type eliminato: con Template deve prevalere la conversione", ric(r, HttpConstants.CONTENT_TYPE));
	}

	// risposta: conversione 'HTTP Payload vuoto' -> nessun Content-Type verso il client
	@Test public void respContentEmptyErogazione() throws UtilsException { respContentEmpty(TipoServizio.EROGAZIONE); }
	@Test public void respContentEmptyFruizione() throws UtilsException { respContentEmpty(TipoServizio.FRUIZIONE); }

	private static void respContentEmpty(TipoServizio tipo) throws UtilsException {
		HttpResponse r = invoca(tipo, "respContentEmpty", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respContentEmpty", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNull(m+" Content-Type presente in risposta nonostante payload vuoto", r.getContentType());
	}

	// risposta: conversione 'Alimentazione Contesto' + delete Content-Type -> body presente + CT eliminato -> errore
	@Test public void respContentContextErogazione() throws UtilsException { respContentContext(TipoServizio.EROGAZIONE); }
	@Test public void respContentContextFruizione() throws UtilsException { respContentContext(TipoServizio.FRUIZIONE); }

	private static void respContentContext(TipoServizio tipo) throws UtilsException {
		HttpResponse r = invoca(tipo, "respContentContext", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respContentContext", r);
		assertErroreContentTypeRispostaMancante(m+" (risposta con body senza Content-Type)", r);
	}

	// risposta: conversione 'Template' (produce JSON) + delete Content-Type -> Content-Type dalla conversione (delete ignorata)
	@Test public void respContentTemplateErogazione() throws UtilsException { respContentTemplate(TipoServizio.EROGAZIONE); }
	@Test public void respContentTemplateFruizione() throws UtilsException { respContentTemplate(TipoServizio.FRUIZIONE); }

	private static void respContentTemplate(TipoServizio tipo) throws UtilsException {
		HttpResponse r = invoca(tipo, "respContentTemplate", HttpRequestMethod.POST, true, HttpConstants.CONTENT_TYPE_JSON, null);
		String m = msg(tipo, "respContentTemplate", r);
		assertEquals(m, 200, r.getResultHTTPOperation());
		assertNotNull(m+" Content-Type eliminato in risposta: con Template deve prevalere la conversione", r.getContentType());
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

	// Content-Type eliminato su risposta REST con body: errore lato risposta -> HTTP 502 (l'error-type specifico non è un
	// transformation-rule-failed, quindi si verifica solo il codice, stabile e comunque diverso dal 404 di config mancante).
	private static void assertErroreContentTypeRispostaMancante(String m, HttpResponse r) {
		assertEquals(m+" atteso errore risposta (502) per Content-Type mancante in risposta con body", 502, r.getResultHTTPOperation());
	}

	private static HttpResponse invoca(TipoServizio tipo, String azione, HttpRequestMethod method, boolean conPayload,
			String contentType, Map<String,String> headerRichiesta) throws UtilsException {
		String servizio = SERVIZIO.get(azione);
		if(servizio==null) throw new UtilsException("Servizio non mappato per azione '"+azione+"'");
		String url = tipo == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+servizio+"/v1/"+azione
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
		return HttpUtilities.httpInvoke(request);
	}

}
