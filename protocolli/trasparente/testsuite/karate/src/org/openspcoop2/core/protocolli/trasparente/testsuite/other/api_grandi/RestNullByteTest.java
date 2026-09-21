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
package org.openspcoop2.core.protocolli.trasparente.testsuite.other.api_grandi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.DumpBinarioUtils;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* RestNullByteTest
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestNullByteTest extends ConfigLoader {

	private static final String ID_DOCUMENTO = "doc123";

	private static final String API_VALIDAZIONE_RICHIESTA = "/SoggettoInternoTest/TestAPIRestPayloadBinarioGrandeConNullByteValidazioneRichiesta/v1";
	private static final String API_VALIDAZIONE_RISPOSTA = "/SoggettoInternoTest/TestAPIRestPayloadBinarioGrandeConNullByteValidazioneRisposta/v1";

	private static final String RISORSA_BINARY = "/documenti/" + ID_DOCUMENTO + "/binary";
	private static final String RISORSA_BASE64 = "/documenti/" + ID_DOCUMENTO;

	@BeforeClass
	public static void rimuoviFotografieScadute() {
		DumpBinarioUtils.deleteSnapshotScaduti(logCore);
	}

	/**
	 * Soglia oltre la quale il payload non viene piu' mantenuto in memoria ma riversato nel repository
	 * di overflow. DEVE coincidere con la proprieta' di govway
	 * 'org.openspcoop2.pdd.logger.dumpBinario.inMemory.threshold'.
	 */
	private static final int SOGLIA = 1048576;

	private static final int DIMENSIONE_SOTTO_SOGLIA = 100 * 1024;

	// Validazione Richiesta

	@Test
	public void validazione_richiesta_binary() throws Exception {
		_test(API_VALIDAZIONE_RICHIESTA + RISORSA_BINARY, false, false);
	}
	@Test
	public void validazione_richiesta_base64() throws Exception {
		_test(API_VALIDAZIONE_RICHIESTA + RISORSA_BASE64, true, false);
	}
	@Test
	public void validazione_richiesta_binary_inviatosubase64resource() throws Exception {
		_test(API_VALIDAZIONE_RICHIESTA + RISORSA_BASE64, false, true);
	}

	// Validazione Risposta

	@Test
	public void validazione_risposta_binary() throws Exception {
		_test(API_VALIDAZIONE_RISPOSTA + RISORSA_BINARY, false, false);
	}
	@Test
	public void validazione_risposta_base64() throws Exception {
		_test(API_VALIDAZIONE_RISPOSTA + RISORSA_BASE64, true, false);
	}
	@Test
	public void validazione_risposta_binary_inviatosubase64resource() throws Exception {
		_test(API_VALIDAZIONE_RISPOSTA + RISORSA_BASE64, false, true);
	}

	// Soglia di overflow dei buffer dei messaggi.
	//
	// I casi sopra riportati inviano un payload di 1.048.738 byte, appena oltre la soglia, e servono
	// a verificare che il file prodotto venga poi rilasciato. Qui si verifica il verso opposto e il
	// comportamento al confine: fino alla soglia il payload deve restare in memoria, senza che alcun
	// file venga prodotto.

	@Test
	public void sottoSoglia_richiesta_binary() throws Exception {
		_testSoglia(API_VALIDAZIONE_RICHIESTA + RISORSA_BINARY, false, DIMENSIONE_SOTTO_SOGLIA);
	}
	@Test
	public void sottoSoglia_richiesta_base64() throws Exception {
		_testSoglia(API_VALIDAZIONE_RICHIESTA + RISORSA_BASE64, true, DIMENSIONE_SOTTO_SOGLIA);
	}
	@Test
	public void sottoSoglia_risposta_binary() throws Exception {
		_testSoglia(API_VALIDAZIONE_RISPOSTA + RISORSA_BINARY, false, DIMENSIONE_SOTTO_SOGLIA);
	}
	@Test
	public void sottoSoglia_risposta_base64() throws Exception {
		_testSoglia(API_VALIDAZIONE_RISPOSTA + RISORSA_BASE64, true, DIMENSIONE_SOTTO_SOGLIA);
	}

	// Confine: payload di dimensione esattamente pari alla soglia. Il buffer riversa su file system
	// solo al superamento, quindi anche in questo caso non deve essere prodotto alcun file.

	@Test
	public void sogliaEsatta_richiesta_binary() throws Exception {
		_testSoglia(API_VALIDAZIONE_RICHIESTA + RISORSA_BINARY, false, SOGLIA);
	}
	@Test
	public void sogliaEsatta_richiesta_base64() throws Exception {
		_testSoglia(API_VALIDAZIONE_RICHIESTA + RISORSA_BASE64, true, SOGLIA);
	}
	@Test
	public void sogliaEsatta_risposta_binary() throws Exception {
		_testSoglia(API_VALIDAZIONE_RISPOSTA + RISORSA_BINARY, false, SOGLIA);
	}
	@Test
	public void sogliaEsatta_risposta_base64() throws Exception {
		_testSoglia(API_VALIDAZIONE_RISPOSTA + RISORSA_BASE64, true, SOGLIA);
	}

	// Confine: primo payload che supera la soglia. Il file deve essere prodotto e poi rilasciato.

	@Test
	public void oltreSoglia_richiesta_binary() throws Exception {
		_testSoglia(API_VALIDAZIONE_RICHIESTA + RISORSA_BINARY, false, SOGLIA + 1);
	}
	@Test
	public void oltreSoglia_richiesta_base64() throws Exception {
		_testSoglia(API_VALIDAZIONE_RICHIESTA + RISORSA_BASE64, true, SOGLIA + 4);
	}
	@Test
	public void oltreSoglia_risposta_binary() throws Exception {
		_testSoglia(API_VALIDAZIONE_RISPOSTA + RISORSA_BINARY, false, SOGLIA + 1);
	}
	@Test
	public void oltreSoglia_risposta_base64() throws Exception {
		_testSoglia(API_VALIDAZIONE_RISPOSTA + RISORSA_BASE64, true, SOGLIA + 4);
	}

	private static void _test(String path, boolean base64Encode, boolean expectedError) throws Exception {

		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);

		byte[] fileContent;
		try (InputStream is = RestNullByteTest.class.getResourceAsStream("file_nullbyte_1MB.pdf")) {
			assertNotNull("File file_nullbyte_1MB.pdf not found in classpath", is);
			fileContent = Utilities.getAsByteArray(is);
		}

		byte[] content;
		if (base64Encode) {
			content = Base64Utilities.encodeAsString(fileContent).getBytes();
		} else {
			content = fileContent;
		}

		String url = System.getProperty("govway_base_path") + path;

		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		request.setContent(content);
		request.setUrl(url);
		request.addHeader(DumpBinarioUtils.HEADER_SNAPSHOT_DIR, DumpBinarioUtils.getSnapshotDirHeaderValue());

		
		int result = 200;
		int esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		String errore = null;
		if(expectedError) {
			if(path.startsWith(API_VALIDAZIONE_RISPOSTA)) {
				result = 502;
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_VALIDAZIONE_RISPOSTA);
				errore = "Validazione [interface] del contenuto applicativo della risposta fallita: ";
			}
			else {
				result = 400;
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_VALIDAZIONE_RICHIESTA);
				errore = "Validazione [interface] del contenuto applicativo della richiesta fallita: ";
			}
		}
		
		HttpResponse resp = HttpUtilities.httpInvoke(request);
		
		assertEquals(result, resp.getResultHTTPOperation());

		String idTransazione = resp.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		DBVerifier.verifyEsito(idTransazione, esitoExpected, errore);

		// Il payload supera la soglia 'org.openspcoop2.pdd.logger.dumpBinario.inMemory.threshold':
		// il buffer viene riversato su file system e deve poi essere rilasciato al termine della
		// transazione. Nei casi di errore la transazione si interrompe prima, quindi le fotografie
		// scattate dagli handler non sono disponibili per tutte le fasi.
		if(!expectedError) {
			verificaSpill(path, idTransazione);
		}
		DumpBinarioUtils.verifyNoResidui(logCore, idTransazione, path);
		DumpBinarioUtils.deleteSnapshot(idTransazione);
		
		if(expectedError) {
			String diag = DBVerifier.existsDiagnostico(idTransazione, errore);
			assertTrue("Verifico lunghezza diagnostico ("+diag.length()+")",(diag.length()<4100));
		}
		else {
			
			if(!path.startsWith(API_VALIDAZIONE_RISPOSTA)) {
				String risposta = "Validazione [interface] del contenuto applicativo della richiesta completata con successo";
				DBVerifier.existsDiagnostico(idTransazione, risposta);
			}
			
			String risposta = "Validazione [interface] del contenuto applicativo della risposta completata con successo";
			DBVerifier.existsDiagnostico(idTransazione, risposta);
			
		}
	}
	/**
	 * La validazione della richiesta e' attiva solo sulla prima delle due API; sull'altra la porta
	 * riporta la proprieta' 'validation.request.enabled=false' e viene validata la sola risposta.
	 * Solo la validazione produce la bufferizzazione read-only del contenuto, e quindi l'eventuale
	 * riversamento su file system.
	 */
	private static boolean isValidazioneRichiestaAttiva(String path) {
		return path.startsWith(API_VALIDAZIONE_RICHIESTA);
	}

	/** Payload oltre soglia: il file deve risultare prodotto in tutte le fasi in cui si valida */
	private static void verificaSpill(String path, String idTransazione) {
		if(isValidazioneRichiestaAttiva(path)) {
			DumpBinarioUtils.verifySpill(logCore, idTransazione, DumpBinarioUtils.FASE_OUT_REQUEST);
		}
		else {
			DumpBinarioUtils.verifyNessunSpill(logCore, idTransazione, DumpBinarioUtils.FASE_OUT_REQUEST);
		}
		DumpBinarioUtils.verifySpill(logCore, idTransazione, DumpBinarioUtils.FASE_OUT_RESPONSE);
	}

	/** Payload entro la soglia: nessun file deve essere prodotto, in nessuna fase */
	private static void verificaNessunSpill(String idTransazione) {
		DumpBinarioUtils.verifyNessunSpill(logCore, idTransazione, DumpBinarioUtils.FASE_OUT_REQUEST);
		DumpBinarioUtils.verifyNessunSpill(logCore, idTransazione, DumpBinarioUtils.FASE_OUT_RESPONSE);
	}


	/**
	 * Costruisce un payload della dimensione indicata, misurata su cio' che viene effettivamente
	 * inviato sulla rete: e' quel valore, non quello del contenuto originario, a essere confrontato
	 * con la soglia mentre GovWay bufferizza il messaggio.
	 *
	 * Per la risorsa che accetta il contenuto codificato, la dimensione richiesta deve essere un
	 * multiplo di 4, cosi' che la codifica base64 di un contenuto di 3/4 di tale dimensione la
	 * restituisca esatta, senza caratteri di riempimento.
	 */
	private static byte[] buildPayload(boolean base64Encode, int dimensioneInviata) throws Exception {
		byte[] fileContent = readFileNullByte();
		if(base64Encode) {
			assertTrue("La dimensione richiesta ("+dimensioneInviata+") non e' un multiplo di 4", (dimensioneInviata % 4)==0);
			int dimensioneOriginaria = (dimensioneInviata / 4) * 3;
			byte[] content = Base64Utilities.encodeAsString(Arrays.copyOf(fileContent, dimensioneOriginaria)).getBytes();
			assertEquals("Dimensione del payload codificato", dimensioneInviata, content.length);
			return content;
		}
		return Arrays.copyOf(fileContent, dimensioneInviata);
	}

	private static byte[] readFileNullByte() throws Exception {
		try (InputStream is = RestNullByteTest.class.getResourceAsStream("file_nullbyte_1MB.pdf")) {
			assertNotNull("File file_nullbyte_1MB.pdf not found in classpath", is);
			return Utilities.getAsByteArray(is);
		}
	}


	/**
	 * Invoca l'API con un payload della dimensione indicata e verifica il comportamento del buffer:
	 * entro la soglia il contenuto deve restare in memoria, oltre la soglia deve essere prodotto un
	 * file che al termine della transazione risulti rilasciato.
	 */
	private static void _testSoglia(String path, boolean base64Encode, int dimensioneInviata) throws Exception {

		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);

		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		request.setContent(buildPayload(base64Encode, dimensioneInviata));
		request.setUrl(System.getProperty("govway_base_path") + path);
		request.addHeader(DumpBinarioUtils.HEADER_SNAPSHOT_DIR, DumpBinarioUtils.getSnapshotDirHeaderValue());

		HttpResponse resp = HttpUtilities.httpInvoke(request);
		assertEquals(200, resp.getResultHTTPOperation());

		String idTransazione = resp.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		int esitoOk = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		DBVerifier.verifyEsito(idTransazione, esitoOk, null);

		if(dimensioneInviata > SOGLIA) {
			verificaSpill(path, idTransazione);
		}
		else {
			verificaNessunSpill(idTransazione);
		}

		DumpBinarioUtils.verifyNoResidui(logCore, idTransazione, path + " (" + dimensioneInviata + " bytes)");
		DumpBinarioUtils.deleteSnapshot(idTransazione);
	}

}
