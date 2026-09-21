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

package org.openspcoop2.core.protocolli.trasparente.testsuite.registrazione_messaggi.dump_binario;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.DumpBinarioUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* Verifica il repository di overflow dei buffer dei messaggi sui messaggi XML.
*
* Il test e' scritto sul contratto atteso e non sull'implementazione: entro la soglia nessun file
* deve essere prodotto, oltre la soglia il file deve essere prodotto e successivamente rilasciato.
* Resta quindi significativo anche se in futuro la validazione dovesse trattare i messaggi XML con
* la bufferizzazione 'lazy' oggi riservata ai messaggi binari e multipart: in quel caso sarebbe
* proprio questo test ad accorgersene.
*
* Per lo stesso motivo sono coperte tutte e tre le fasce, confine compreso, e non le sole due
* estremita'.
*
* @author $Author$
* @version $Rev$, $Date$
*/
public class XmlSogliaTest extends ConfigLoader {

	private static final int SOGLIA = 1048576;

	private static final int SOTTO_SOGLIA = 100 * 1024;

	private static final String API_VALIDAZIONE_RICHIESTA = "/SoggettoInternoTest/TestAPIRestPayloadXmlGrandeValidazioneRichiesta/v1";
	private static final String API_VALIDAZIONE_RISPOSTA = "/SoggettoInternoTest/TestAPIRestPayloadXmlGrandeValidazioneRisposta/v1";

	private static final String RISORSA = "/documenti/doc123/xml";


	// Entro soglia: il contenuto deve restare in memoria

	@BeforeClass
	public static void rimuoviFotografieScadute() {
		DumpBinarioUtils.deleteSnapshotScaduti(logCore);
	}

	@Test
	public void sottoSoglia_richiesta() throws Exception {
		invoca(API_VALIDAZIONE_RICHIESTA, SOTTO_SOGLIA, false);
	}
	@Test
	public void sottoSoglia_risposta() throws Exception {
		invoca(API_VALIDAZIONE_RISPOSTA, SOTTO_SOGLIA, false);
	}

	// Confine: dimensione esattamente pari alla soglia, il riversamento scatta solo al superamento

	@Test
	public void sogliaEsatta_richiesta() throws Exception {
		invoca(API_VALIDAZIONE_RICHIESTA, SOGLIA, false);
	}
	@Test
	public void sogliaEsatta_risposta() throws Exception {
		invoca(API_VALIDAZIONE_RISPOSTA, SOGLIA, false);
	}

	// Oltre soglia: il file deve essere prodotto e poi rilasciato

	@Test
	public void oltreSoglia_richiesta() throws Exception {
		invoca(API_VALIDAZIONE_RICHIESTA, SOGLIA + 1, true);
	}
	@Test
	public void oltreSoglia_risposta() throws Exception {
		invoca(API_VALIDAZIONE_RISPOSTA, SOGLIA + 1, true);
	}

	/**
	 * Controllo di attendibilita' dell'intero gruppo: se la validazione non fosse realmente attiva su
	 * questa API, i test precedenti risulterebbero verdi senza aver esercitato nulla.
	 *
	 * Il body XML e' dichiarato nell'interfaccia come contenuto non modellato, quindi la sua struttura
	 * interna non viene verificata e non e' possibile provocare un errore alterandola. Si utilizza
	 * allora il media type: l'interfaccia dichiara il solo 'application/xml', percio' una richiesta
	 * inviata con un media type differente deve essere rifiutata.
	 */
	@Test
	public void validazioneRealmenteAttiva() throws Exception {

		HttpRequest request = buildRequest(API_VALIDAZIONE_RICHIESTA, "{\"dati\":\"x\"}");
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);

		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals("La validazione dei contenuti non risulta attiva sull'API: un media type non dichiarato e' stato accettato",
				400, response.getResultHTTPOperation());
	}


	private static void invoca(String api, int dimensioneDati, boolean attesoSpill) throws Exception {

		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);

		String payload = buildDocumento(dimensioneDati);
		HttpRequest request = buildRequest(api, payload);

		logCore.info("Invocazione XML '"+api+"' con payload di "+payload.length()+" bytes ...");

		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		verifica(idTransazione, DumpBinarioUtils.FASE_OUT_REQUEST, attesoSpill && api.equals(API_VALIDAZIONE_RICHIESTA));
		verifica(idTransazione, DumpBinarioUtils.FASE_OUT_RESPONSE, attesoSpill);

		DumpBinarioUtils.verifyNoResidui(logCore, idTransazione, api + " (" + payload.length() + " bytes)");
		DumpBinarioUtils.deleteSnapshot(idTransazione);
	}

	private static void verifica(String idTransazione, String fase, boolean attesoSpill) {
		if(attesoSpill) {
			DumpBinarioUtils.verifySpill(logCore, idTransazione, fase);
		}
		else {
			DumpBinarioUtils.verifyNessunSpill(logCore, idTransazione, fase);
		}
	}

	private static HttpRequest buildRequest(String api, String payload) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + api + RISORSA);
		request.setContentType(HttpConstants.CONTENT_TYPE_XML);
		request.setContent(payload.getBytes());
		request.addHeader(DumpBinarioUtils.HEADER_SNAPSHOT_DIR, DumpBinarioUtils.getSnapshotDirHeaderValue());
		return request;
	}

	/** Produce un documento della dimensione complessiva richiesta, gonfiando l'elemento 'dati' */
	private static String buildDocumento(int dimensioneComplessiva) {
		String apertura = "<documento><dati>";
		String chiusura = "</dati></documento>";
		int riempimento = dimensioneComplessiva - apertura.length() - chiusura.length();
		StringBuilder sb = new StringBuilder(apertura);
		for (int i = 0; i < riempimento; i++) {
			sb.append('x');
		}
		sb.append(chiusura);
		return sb.toString();
	}

}
