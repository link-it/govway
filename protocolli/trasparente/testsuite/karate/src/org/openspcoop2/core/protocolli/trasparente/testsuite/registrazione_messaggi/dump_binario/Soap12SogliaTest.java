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
* Verifica il repository di overflow dei buffer dei messaggi sui messaggi SOAP 1.2.
*
* Come per i messaggi XML, il test asserisce il contratto e non il meccanismo: entro la soglia
* nessun file deve essere prodotto, oltre la soglia il file deve essere prodotto e poi rilasciato.
* Resta quindi valido anche se la gestione di questo tipo di messaggio dovesse cambiare in futuro,
* ed e' proprio per questo che sono coperte tutte e tre le fasce, confine compreso.
*
* Il backend e' il servizio di echo sempre dispiegato sull'application server, percio' il test non
* deve avviare alcun server, a differenza di quello su SOAP 1.1.
*
* @author $Author$
* @version $Rev$, $Date$
*/
public class Soap12SogliaTest extends ConfigLoader {

	private static final int SOGLIA = 1048576;

	private static final int SOTTO_SOGLIA = 100 * 1024;

	private static final String API = "/SoggettoInternoTest/ServiceRPCLiteral12ValidazioneContenuti/v1/Documento";

	@BeforeClass
	public static void rimuoviFotografieScadute() {
		DumpBinarioUtils.deleteSnapshotScaduti(logCore);
	}

	/** Su SOAP 1.2 l'azione viaggia come parametro del content type, non come header dedicato */
	private static final String CONTENT_TYPE = HttpConstants.CONTENT_TYPE_SOAP_1_2 + ";charset=utf-8;action=\"Documento\"";

	private static final String NS_TYPES = "http://openspcoop2.org/ValidazioneContenutiWS/Service/types";


	@Test
	public void sottoSoglia() throws Exception {
		invoca(SOTTO_SOGLIA, false);
	}

	@Test
	public void sogliaEsatta() throws Exception {
		invoca(SOGLIA, false);
	}

	@Test
	public void oltreSoglia() throws Exception {
		invoca(SOGLIA + 1, true);
	}

	/**
	 * Risposta gia' costruita e bufferizzata, poi sostituita da un fault.
	 *
	 * L'operazione 'DocumentoRispostaNonConforme' dichiara la risposta diversa dalla richiesta: poiche' il
	 * backend e' un servizio di echo, la risposta non e' mai conforme e la validazione la respinge. Il
	 * messaggio di risposta viene percio' sostituito dal fault <b>dopo</b> essere stato bufferizzato, e con
	 * esso si perde l'unico riferimento al buffer.
	 *
	 * E' lo scenario che il rilascio legato alla sola serializzazione non riesce a coprire: il messaggio
	 * originale non viene mai serializzato, e nella fase conclusiva il contesto riporta il fault al suo posto.
	 */
	@Test
	public void oltreSoglia_validazioneRispostaKO() throws Exception {

		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);

		String payload = buildEnvelope(SOGLIA + 1);

		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path")
				+ "/SoggettoInternoTest/ServiceRPCLiteral12ValidazioneContenuti/v1/DocumentoRispostaNonConforme");
		request.setContentType(HttpConstants.CONTENT_TYPE_SOAP_1_2 + ";charset=utf-8;action=\"DocumentoRispostaNonConforme\"");
		request.setContent(payload.getBytes());
		request.addHeader(DumpBinarioUtils.HEADER_SNAPSHOT_DIR, DumpBinarioUtils.getSnapshotDirHeaderValue());

		logCore.info("Invocazione SOAP 1.2 con risposta non conforme, payload di "+payload.length()+" bytes ...");

		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals("La validazione della risposta non risulta attiva: una risposta non conforme e' stata accettata",
				500, response.getResultHTTPOperation());

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		DumpBinarioUtils.verifyNoResidui(logCore, idTransazione, "SOAP 1.2 risposta non conforme (" + payload.length() + " bytes)");
		DumpBinarioUtils.deleteSnapshot(idTransazione);
	}

	/**
	 * Controllo di attendibilita': se la validazione non fosse realmente attiva, i test precedenti
	 * risulterebbero verdi senza aver esercitato nulla. L'operazione prevede il solo elemento 'indirizzo',
	 * quindi un messaggio che ne riporti uno diverso deve essere rifiutato.
	 */
	@Test
	public void validazioneRealmenteAttiva() throws Exception {

		String payload = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:typ=\"" + NS_TYPES + "\">"
				+ "<soapenv:Body><typ:nominativo ruolo=\"test\">test</typ:nominativo></soapenv:Body>"
				+ "</soapenv:Envelope>";

		HttpResponse response = HttpUtilities.httpInvoke(buildRequest(payload));
		assertEquals("La validazione dei contenuti non risulta attiva sull'API: un messaggio non conforme e' stato accettato",
				500, response.getResultHTTPOperation());
	}


	private static void invoca(int dimensioneComplessiva, boolean attesoSpill) throws Exception {

		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);

		String payload = buildEnvelope(dimensioneComplessiva);

		logCore.info("Invocazione SOAP 1.2 con payload di "+payload.length()+" bytes ...");

		HttpResponse response = HttpUtilities.httpInvoke(buildRequest(payload));
		assertEquals(200, response.getResultHTTPOperation());

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		verifica(idTransazione, DumpBinarioUtils.FASE_OUT_REQUEST, attesoSpill);
		verifica(idTransazione, DumpBinarioUtils.FASE_OUT_RESPONSE, attesoSpill);

		DumpBinarioUtils.verifyNoResidui(logCore, idTransazione, "SOAP 1.2 (" + payload.length() + " bytes)");
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

	private static HttpRequest buildRequest(String payload) {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(System.getProperty("govway_base_path") + API);
		request.setContentType(CONTENT_TYPE);
		request.setContent(payload.getBytes());
		request.addHeader(DumpBinarioUtils.HEADER_SNAPSHOT_DIR, DumpBinarioUtils.getSnapshotDirHeaderValue());
		return request;
	}

	/** Produce un messaggio della dimensione richiesta, gonfiando l'elemento 'indirizzo' */
	private static String buildEnvelope(int dimensioneComplessiva) {

		String prefisso = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:typ=\"" + NS_TYPES + "\">"
				+ "<soapenv:Body><typ:indirizzo>";
		String suffisso = "</typ:indirizzo></soapenv:Body></soapenv:Envelope>";

		int riempimento = dimensioneComplessiva - prefisso.length() - suffisso.length();
		StringBuilder sb = new StringBuilder(prefisso);
		for (int i = 0; i < riempimento; i++) {
			sb.append('x');
		}
		sb.append(suffisso);
		return sb.toString();
	}

}
