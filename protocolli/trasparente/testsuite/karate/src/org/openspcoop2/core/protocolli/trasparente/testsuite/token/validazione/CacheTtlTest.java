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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import com.fasterxml.jackson.databind.JsonNode;

/**
* CacheTtlTest
*
* Verifica l'intervallo di riutilizzo degli esiti di introspection e userInfo presenti nella cache dei token:
* trascorso l'intervallo il servizio remoto viene nuovamente invocato, consentendo di rilevare un token revocato.
*
* Le richieste di ogni scenario vengono effettuate in sequenza e le verifiche su database vengono attuate al termine,
* in modo che i tempi di scrittura delle transazioni non influiscano sugli intervalli verificati.
*
* Il riutilizzo dell'esito in cache viene verificato tramite il claim '*_step' della risposta simulata, l'istante di invocazione del servizio
* riportato nel token info della transazione e i diagnostici di validazione.
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class CacheTtlTest extends ConfigLoader {

	/**
	 * Deve corrispondere al valore delle proprietà 'org.openspcoop2.pdd.gestioneToken.introspection.cache.ttlSeconds'
	 * e 'org.openspcoop2.pdd.gestioneToken.userInfo.cache.ttlSeconds' impostate nel file govway_local.properties (vedi RUN.README)
	 **/
	private static final int TTL_GLOBALE = 10;
	/** Valori ridefiniti nelle token policy 'PolicyShort' e 'PolicyLong' */
	private static final int TTL_POLICY_SHORT = 4;
	private static final int TTL_POLICY_LONG = 20;
	/** Margine applicato alle attese per superare l'intervallo */
	private static final long MARGINE_MS = 1500;

	private static final String HEADER_TOKEN = "test-cache-ttl";

	private static final String API_INTROSPECTION = "TestValidazioneTokenCacheTtlIntrospection";
	private static final String API_INTROSPECTION_POLICY_SHORT = "TestValidazioneTokenCacheTtlIntrospectionPolicyShort";
	private static final String API_INTROSPECTION_POLICY_LONG = "TestValidazioneTokenCacheTtlIntrospectionPolicyLong";
	private static final String API_USER_INFO = "TestValidazioneTokenCacheTtlUserInfo";
	private static final String API_USER_INFO_POLICY_SHORT = "TestValidazioneTokenCacheTtlUserInfoPolicyShort";
	private static final String API_USER_INFO_POLICY_LONG = "TestValidazioneTokenCacheTtlUserInfoPolicyLong";
	private static final String API_INTROSPECTION_USER_INFO = "TestValidazioneTokenCacheTtlIntrospectionUserInfo";

	private static final String POLICY_INTROSPECTION = "TestCacheTtlIntrospection";
	private static final String POLICY_INTROSPECTION_POLICY_SHORT = "TestCacheTtlIntrospectionPolicyShort";
	private static final String POLICY_INTROSPECTION_POLICY_LONG = "TestCacheTtlIntrospectionPolicyLong";
	private static final String POLICY_USER_INFO = "TestCacheTtlUserInfo";
	private static final String POLICY_USER_INFO_POLICY_SHORT = "TestCacheTtlUserInfoPolicyShort";
	private static final String POLICY_USER_INFO_POLICY_LONG = "TestCacheTtlUserInfoPolicyLong";
	private static final String POLICY_INTROSPECTION_USER_INFO = "TestCacheTtlIntrospectionUserInfo";

	/** Diagnostici con severità debugLow: sulle erogazioni utilizzate è abilitato il relativo livello di registrazione */
	private static final String DIAGNOSTICO_IN_CACHE = "Token, presente in cache, contenente le seguenti informazioni:";
	private static final String DIAGNOSTICO_INVOCATO = "Individuate le seguenti informazioni all'interno del token:";
	private static final String ERRORE_INTROSPECTION = "fallita: Token non valido";
	private static final String ERRORE_USER_INFO = "Risposta del servizio di UserInfo non valida";

	private enum Servizio {

		INTROSPECTION("introspection", "Introspection", "introspectionCheckedAt", ERRORE_INTROSPECTION),
		USER_INFO("userInfo", "UserInfo", "userInfoCheckedAt", ERRORE_USER_INFO);

		private final String suffissoFile;
		private final String label;
		private final String campoTokenInfo;
		private final String errore;
		Servizio(String suffissoFile, String label, String campoTokenInfo, String errore){
			this.suffissoFile = suffissoFile;
			this.label = label;
			this.campoTokenInfo = campoTokenInfo;
			this.errore = errore;
		}
		File getFile(String policy) {
			return new File("/tmp/"+policy+"_"+this.suffissoFile+".json");
		}
		String getClaimStep() {
			return this.suffissoFile+"_step";
		}
	}



	// ********* INTROSPECTION ****************** */

	@Test
	public void introspectionTtlGlobale() throws Exception {
		revoca(API_INTROSPECTION, POLICY_INTROSPECTION, Servizio.INTROSPECTION, TTL_GLOBALE);
	}

	@Test
	public void introspectionTtlPolicyShort() throws Exception {
		revoca(API_INTROSPECTION_POLICY_SHORT, POLICY_INTROSPECTION_POLICY_SHORT, Servizio.INTROSPECTION, TTL_POLICY_SHORT);
	}

	@Test
	public void introspectionTtlPolicyLong() throws Exception {
		revocaTtlSuperioreGlobale(API_INTROSPECTION_POLICY_LONG, POLICY_INTROSPECTION_POLICY_LONG, Servizio.INTROSPECTION, TTL_POLICY_LONG);
	}



	// ********* USER INFO ****************** */

	@Test
	public void userInfoTtlGlobale() throws Exception {
		revoca(API_USER_INFO, POLICY_USER_INFO, Servizio.USER_INFO, TTL_GLOBALE);
	}

	@Test
	public void userInfoTtlPolicyShort() throws Exception {
		revoca(API_USER_INFO_POLICY_SHORT, POLICY_USER_INFO_POLICY_SHORT, Servizio.USER_INFO, TTL_POLICY_SHORT);
	}

	@Test
	public void userInfoTtlPolicyLong() throws Exception {
		revocaTtlSuperioreGlobale(API_USER_INFO_POLICY_LONG, POLICY_USER_INFO_POLICY_LONG, Servizio.USER_INFO, TTL_POLICY_LONG);
	}



	// ********* INTROSPECTION + USER INFO ****************** */

	/**
	 * Policy con entrambi i servizi attivi: introspection con ttl ridefinito nella policy (TTL_POLICY_SHORT), userInfo con ttl globale (TTL_GLOBALE).
	 * Gli intervalli devono essere applicati in modo indipendente e il token info della transazione deve riportarli entrambi.
	 */
	@Test
	public void introspectionUserInfo() throws Exception {

		resetCacheToken();
		Map<String, String> headers = buildHeaders();
		String policy = POLICY_INTROSPECTION_USER_INFO;
		String api = API_INTROSPECTION_USER_INFO;

		try {
			// 1. prima invocazione: entrambi i servizi vengono invocati
			scriviRisposta(policy, Servizio.INTROSPECTION, 1);
			scriviRisposta(policy, Servizio.USER_INFO, 1);
			long t1 = System.currentTimeMillis();
			String id1 = invokeOk(api, headers);

			// 2. scaduto il solo ttl di introspection: introspection reinvocata, userInfo ancora in cache
			sleepUntil(t1 + (TTL_POLICY_SHORT*1000l) + MARGINE_MS);
			scriviRisposta(policy, Servizio.INTROSPECTION, 2);
			scriviRisposta(policy, Servizio.USER_INFO, 2);
			long t2 = System.currentTimeMillis();
			String id2 = invokeOk(api, headers);

			// 3. scaduto anche il ttl di userInfo: entrambi i servizi reinvocati (per introspection è trascorso nuovamente il ttl dalla verifica precedente)
			sleepUntil(Math.max(t1 + (TTL_GLOBALE*1000l), t2 + (TTL_POLICY_SHORT*1000l)) + MARGINE_MS);
			scriviRisposta(policy, Servizio.INTROSPECTION, 3);
			scriviRisposta(policy, Servizio.USER_INFO, 3);
			long t3 = System.currentTimeMillis();
			String id3 = invokeOk(api, headers);

			// 4. revoca rilevata da introspection trascorso il suo ttl, mentre l'esito di userInfo è ancora in cache
			revoca(policy, Servizio.INTROSPECTION);
			sleepUntil(t3 + (TTL_POLICY_SHORT*1000l) + MARGINE_MS);
			String id4 = invokeKo(api, headers);

			// verifiche
			Map<Servizio, String> check1 = verificaOk(id1, invocato(Servizio.INTROSPECTION, 1), invocato(Servizio.USER_INFO, 1));
			Map<Servizio, String> check2 = verificaOk(id2, invocato(Servizio.INTROSPECTION, 2), inCache(Servizio.USER_INFO, 1));
			assertNotEquals(check1.get(Servizio.INTROSPECTION), check2.get(Servizio.INTROSPECTION));
			assertEquals(check1.get(Servizio.USER_INFO), check2.get(Servizio.USER_INFO));
			Map<Servizio, String> check3 = verificaOk(id3, invocato(Servizio.INTROSPECTION, 3), invocato(Servizio.USER_INFO, 3));
			assertNotEquals(check2.get(Servizio.INTROSPECTION), check3.get(Servizio.INTROSPECTION));
			assertNotEquals(check2.get(Servizio.USER_INFO), check3.get(Servizio.USER_INFO));
			verificaKo(id4, headers, Servizio.INTROSPECTION);
		}
		finally {
			Servizio.INTROSPECTION.getFile(policy).delete();
			Servizio.USER_INFO.getFile(policy).delete();
		}
	}



	// ********* SCENARI ****************** */

	/**
	 * Entro il ttl l'esito in cache viene riutilizzato anche se la risposta del servizio cambia (compresa una revoca);
	 * trascorso il ttl il servizio viene reinvocato e la revoca rilevata.
	 * Ripristinata la validità, l'esito negativo (non salvato in cache) non impedisce una nuova validazione, che torna ad essere riutilizzata.
	 */
	private void revoca(String api, String policy, Servizio servizio, int ttlSeconds) throws Exception {

		resetCacheToken();
		Map<String, String> headers = buildHeaders();

		try {
			// 1. prima invocazione: servizio invocato
			scriviRisposta(policy, servizio, 1);
			long t1 = System.currentTimeMillis();
			String id1 = invokeOk(api, headers);

			// 2. entro il ttl: esito in cache nonostante la risposta del servizio sia cambiata
			scriviRisposta(policy, servizio, 2);
			String id2 = invokeOk(api, headers);

			// 3. revoca entro il ttl: il token continua ad essere accettato
			revoca(policy, servizio);
			String id3 = invokeOk(api, headers);

			// 4. trascorso il ttl: servizio reinvocato e revoca rilevata
			sleepUntil(t1 + (ttlSeconds*1000l) + MARGINE_MS);
			String id4 = invokeKo(api, headers);

			// 5. ripristino: servizio nuovamente invocato
			scriviRisposta(policy, servizio, 3);
			String id5 = invokeOk(api, headers);

			// 6. entro il ttl: nuovo esito in cache
			scriviRisposta(policy, servizio, 4);
			String id6 = invokeOk(api, headers);

			// verifiche
			String checkedAt1 = verificaOk(id1, invocato(servizio, 1)).get(servizio);
			assertEquals(checkedAt1, verificaOk(id2, inCache(servizio, 1)).get(servizio));
			assertEquals(checkedAt1, verificaOk(id3, inCache(servizio, 1)).get(servizio));
			verificaKo(id4, headers, servizio);
			String checkedAt5 = verificaOk(id5, invocato(servizio, 3)).get(servizio);
			assertNotEquals(checkedAt1, checkedAt5);
			assertEquals(checkedAt5, verificaOk(id6, inCache(servizio, 3)).get(servizio));
		}
		finally {
			servizio.getFile(policy).delete();
		}
	}

	/**
	 * Il ttl ridefinito nella policy, superiore a quello globale, deve prevalere:
	 * trascorso il ttl globale l'esito è ancora in cache e la revoca viene rilevata solamente trascorso quello della policy.
	 */
	private void revocaTtlSuperioreGlobale(String api, String policy, Servizio servizio, int ttlSeconds) throws Exception {

		resetCacheToken();
		Map<String, String> headers = buildHeaders();

		try {
			// 1. prima invocazione: servizio invocato
			scriviRisposta(policy, servizio, 1);
			long t1 = System.currentTimeMillis();
			String id1 = invokeOk(api, headers);

			// 2. revoca e superamento del ttl globale: il token continua ad essere accettato
			revoca(policy, servizio);
			sleepUntil(t1 + (TTL_GLOBALE*1000l) + MARGINE_MS);
			String id2 = invokeOk(api, headers);

			// 3. trascorso il ttl della policy: revoca rilevata
			sleepUntil(t1 + (ttlSeconds*1000l) + MARGINE_MS);
			String id3 = invokeKo(api, headers);

			// verifiche
			String checkedAt1 = verificaOk(id1, invocato(servizio, 1)).get(servizio);
			assertEquals(checkedAt1, verificaOk(id2, inCache(servizio, 1)).get(servizio));
			verificaKo(id3, headers, servizio);
		}
		finally {
			servizio.getFile(policy).delete();
		}
	}



	// ********* UTILITIES ****************** */

	private static void resetCacheToken() throws Exception {
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
	}

	private static Map<String, String> buildHeaders(){
		// token differente per ogni esecuzione, in modo da non essere influenzati da esiti presenti in cache
		Map<String, String> headers = new HashMap<>();
		headers.put(HEADER_TOKEN, "cache-ttl-"+UUID.randomUUID().toString());
		return headers;
	}

	private static String step(Servizio servizio, int step) {
		return "\""+servizio.getClaimStep()+"\":\""+step+"\"";
	}

	/** Esito atteso per un servizio: 'step' della risposta simulata utilizzata e indicazione se proveniente dalla cache */
	private static class Atteso {
		private final Servizio servizio;
		private final int step;
		private final boolean inCache;
		Atteso(Servizio servizio, int step, boolean inCache){
			this.servizio = servizio;
			this.step = step;
			this.inCache = inCache;
		}
	}
	private static Atteso invocato(Servizio servizio, int step) {
		return new Atteso(servizio, step, false);
	}
	private static Atteso inCache(Servizio servizio, int step) {
		return new Atteso(servizio, step, true);
	}

	private static void scriviRisposta(String policy, Servizio servizio, int step) throws Exception {
		long now = System.currentTimeMillis()/1000l;
		String json = "{"+
				(Servizio.INTROSPECTION.equals(servizio) ? "\"active\":true," : "")+
				"\"iat\":"+now+","+
				"\"exp\":"+(now+300)+","+
				"\""+servizio.getClaimStep()+"\":\""+step+"\"}";
		FileSystemUtilities.writeFile(servizio.getFile(policy), json.getBytes());
	}

	private static void revoca(String policy, Servizio servizio) throws Exception {
		if(Servizio.INTROSPECTION.equals(servizio)) {
			// risposta RFC 7662 con active=false
			long now = System.currentTimeMillis()/1000l;
			String json = "{\"active\":false,\"iat\":"+now+",\"exp\":"+(now+300)+"}";
			FileSystemUtilities.writeFile(servizio.getFile(policy), json.getBytes());
		}
		else {
			// il servizio di userInfo risponde con errore (in assenza del file il servizio di echo restituisce un codice 500)
			servizio.getFile(policy).delete();
		}
	}

	private static void sleepUntil(long time) {
		long now = System.currentTimeMillis();
		if(time>now) {
			org.openspcoop2.utils.Utilities.sleep(time-now);
		}
	}

	private static String invokeOk(String api, Map<String, String> headers) throws Exception {
		HttpResponse response = invoke(api, headers);
		Utilities.verifyOk(response, 200, HttpConstants.CONTENT_TYPE_JSON);
		return response.getHeaderFirstValue("GovWay-Transaction-ID");
	}

	private static String invokeKo(String api, Map<String, String> headers) throws Exception {
		HttpResponse response = invoke(api, headers);
		Utilities.verifyKo(response, "TokenAuthenticationFailed", 401, "Invalid token", true);
		return response.getHeaderFirstValue("GovWay-Transaction-ID");
	}

	private static HttpResponse invoke(String api, Map<String, String> headers) throws Exception {

		HttpRequest request = new HttpRequest();
		for (Map.Entry<String, String> hdr : headers.entrySet()) {
			request.addHeader(hdr.getKey(), hdr.getValue());
		}
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		request.setContent(Bodies.getJson(Bodies.SMALL_SIZE).getBytes());
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/success");

		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertNotNull(response.getHeaderFirstValue("GovWay-Transaction-ID"));
		return response;
	}

	/**
	 * Verifica la transazione e restituisce, per ogni servizio atteso, l'istante di invocazione riportato nel token info.
	 *
	 * I diagnostici di validazione riportano la risposta del servizio, tramite la quale (claim '*_step')
	 * si distingue il servizio a cui si riferiscono anche quando entrambi sono attivi.
	 */
	private static Map<Servizio, String> verificaOk(String idTransazione, Atteso ... attesi) throws Exception {

		List<String> check = new ArrayList<>();
		for (Atteso atteso : attesi) {
			check.add(step(atteso.servizio, atteso.step));
		}
		DBVerifier.verify(idTransazione,
				EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK),
				null, null, check);

		String tokenInfo = DBVerifier.getTokenInfo(idTransazione);
		JsonNode node = JSONUtils.getInstance().getAsNode(tokenInfo);
		Map<Servizio, String> map = new HashMap<>();
		for (Servizio servizio : Servizio.values()) {
			JsonNode checkedAt = node.get(servizio.campoTokenInfo);
			if(checkedAt!=null && checkedAt.isNull()) {
				checkedAt = null;
			}
			Atteso atteso = null;
			for (Atteso a : attesi) {
				if(a.servizio.equals(servizio)) {
					atteso = a;
				}
			}
			if(atteso!=null) {
				assertNotNull("IdTransazione: "+idTransazione+" '"+servizio.campoTokenInfo+"' atteso nel token info: "+tokenInfo, checkedAt);
				map.put(servizio, checkedAt.asText());
				verificaDiagnostici(idTransazione, atteso);
			}
			else {
				assertNull("IdTransazione: "+idTransazione+" '"+servizio.campoTokenInfo+"' non atteso nel token info: "+tokenInfo, checkedAt);
			}
		}
		return map;
	}

	private static void verificaDiagnostici(String idTransazione, Atteso atteso) {

		// validazione avviata (registrata sia in caso di esito in cache che di invocazione del servizio)
		DBVerifier.checkDiagnostic(idTransazione, "Validazione del token, tramite il servizio di "+atteso.servizio.label+" (");

		String claimStep = "\""+atteso.servizio.getClaimStep()+"\"";
		if(atteso.inCache) {
			DBVerifier.checkDiagnostic(idTransazione, DIAGNOSTICO_IN_CACHE+"%"+step(atteso.servizio, atteso.step));
			DBVerifier.checkNoDiagnostic(idTransazione, DIAGNOSTICO_INVOCATO+"%"+claimStep);
		}
		else {
			DBVerifier.checkDiagnostic(idTransazione, DIAGNOSTICO_INVOCATO+"%"+step(atteso.servizio, atteso.step));
			DBVerifier.checkNoDiagnostic(idTransazione, DIAGNOSTICO_IN_CACHE+"%"+claimStep);
		}
	}

	private static void verificaKo(String idTransazione, Map<String, String> headers, Servizio servizio) throws Exception {
		DBVerifier.verify(idTransazione,
				EsitiProperties.getInstanceFromProtocolName(logCore, org.openspcoop2.protocol.engine.constants.Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_TOKEN),
				servizio.errore, null, Utilities.getMapExpectedTokenInfoInvalid(headers.get(HEADER_TOKEN)));
	}

}
