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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.DumpBinarioUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.multipart.DBVerifier;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.id.UUIDUtilsGenerator;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.openapi.validator.MultipartUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* Verifica il repository di overflow dei buffer dei messaggi sui messaggi MIME_MULTIPART.
*
* La validazione dei contenuti bufferizza il payload con due modalita' distinte: per i messaggi
* SOAP, XML e JSON il contenuto viene costruito valorizzando il buffer del messaggio, mentre per i
* messaggi BINARY e MIME_MULTIPART si passa dalla bufferizzazione 'lazy'. I test su payload binario
* coprono gia' la prima delle due, qui si esercita la seconda.
*
* Le risorse 'test-correlazione' e 'test-trasformazione' dell'API aggiungono alla validazione una
* seconda funzionalita' che legge il contenuto: servono a capire se la compresenza cambia l'esito,
* dato che entrambe le modalita' di bufferizzazione insistono sullo stesso messaggio.
*
* @author $Author$
* @version $Rev$, $Date$
*/
public class MimeMultipartSogliaTest extends ConfigLoader {

	private static final String API = "OpenAPIValidazioneMultipartRequest";

	private static final String FORM_DATA = HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE;
	private static final String MIXED = HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE;

	/** Allegato oltre la soglia di 1MB: il payload complessivo supera abbondantemente il limite */
	private static final String PDF_GRANDE = "/org/openspcoop2/core/protocolli/trasparente/testsuite/other/api_grandi/file_nullbyte_1MB.pdf";

	@BeforeClass
	public static void rimuoviFotografieScadute() {
		DumpBinarioUtils.deleteSnapshotScaduti(logCore);
	}

	/** Allegato da 127KB: anche sommato alla sua codifica il payload resta sotto la soglia */
	private static final String PDF_PICCOLO = "/org/openspcoop2/utils/openapi/test/test.pdf";


	// Oltre soglia: bufferizzazione lazy, il file prodotto deve essere rilasciato

	@Test
	public void oltreSoglia_erogazione_formData() throws Exception {
		invoca(TipoServizio.EROGAZIONE, FORM_DATA, "form-data", PDF_GRANDE, true, true, false);
	}
	@Test
	public void oltreSoglia_erogazione_mixed() throws Exception {
		invoca(TipoServizio.EROGAZIONE, MIXED, "mixed", PDF_GRANDE, true, true, false);
	}
	@Test
	public void oltreSoglia_fruizione_formData() throws Exception {
		invoca(TipoServizio.FRUIZIONE, FORM_DATA, "form-data", PDF_GRANDE, true, true, false);
	}

	// Entro soglia: il payload deve restare in memoria

	@Test
	public void sottoSoglia_erogazione_formData() throws Exception {
		invoca(TipoServizio.EROGAZIONE, FORM_DATA, "form-data", PDF_PICCOLO, false, false, false);
	}
	@Test
	public void sottoSoglia_erogazione_mixed() throws Exception {
		invoca(TipoServizio.EROGAZIONE, MIXED, "mixed", PDF_PICCOLO, false, false, false);
	}

	/**
	 * La correlazione applicativa legge il contenuto prima della validazione, e lo fa costruendo il
	 * buffer del messaggio: quando la validazione interviene, il buffer risulta gia' valorizzato e la
	 * bufferizzazione lazy non viene attivata. Sulla richiesta, quindi, non deve essere prodotto alcun
	 * file nemmeno oltre soglia; sulla risposta il file viene invece prodotto.
	 */
	@Test
	public void oltreSoglia_erogazione_correlazioneApplicativa() throws Exception {
		invoca(TipoServizio.EROGAZIONE, FORM_DATA, "test-correlazione", PDF_GRANDE, false, true, true);
	}

	/** La trasformazione, a differenza della correlazione, non previene la bufferizzazione lazy */
	@Test
	public void oltreSoglia_erogazione_trasformazione() throws Exception {
		invoca(TipoServizio.EROGAZIONE, FORM_DATA, "test-trasformazione", PDF_GRANDE, true, true, false);
	}


	private static void invoca(TipoServizio tipoServizio, String subtype, String resource, String pathPdf,
			boolean attesoSpillRichiesta, boolean attesoSpillRisposta, boolean verificaCorrelazione) throws Exception {

		String id = UUIDUtilsGenerator.newUUID();

		String dog = "{\"pet_type\": \"Dog\",  \"bark\": false,  \"breed\": \"Dingo\" }";
		String metadati = "{\"altro\":\""+id+"\", \"pet\":"+dog+"}";

		byte[] pdf = leggiRisorsa(pathPdf);
		byte[] pdfBase64 = Base64Utilities.encode(pdf);

		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(subtype,
				Integer.MAX_VALUE+"", HttpConstants.CONTENT_TYPE_PLAIN, "\"id\"", null,
				metadati, HttpConstants.CONTENT_TYPE_JSON, "\"metadati\"", null,
				pdf, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf\"", "\"attachment.pdf\"",
				pdfBase64, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf2\"", "\"attachment2.pdf\"",
				null, null, null, null);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		mm.writeTo(os);
		os.flush();
		os.close();

		StringBuilder url = new StringBuilder();
		url.append(System.getProperty("govway_base_path"));
		if(TipoServizio.EROGAZIONE.equals(tipoServizio)) {
			url.append("/SoggettoInternoTest/").append(API).append("/v1/multipart/").append(resource);
		}
		else {
			url.append("/out/SoggettoInternoTestFruitore/SoggettoInternoTest/").append(API).append("/v1/multipart/").append(resource);
		}
		if(resource.contains("trasformazione")) {
			url.append("?replyHttpHeader=X-Verifica-Req");
		}

		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setUrl(url.toString());
		request.setContentType(mm.getContentType());
		request.setContent(os.toByteArray());
		request.addHeader(DumpBinarioUtils.HEADER_SNAPSHOT_DIR, DumpBinarioUtils.getSnapshotDirHeaderValue());

		logCore.info("Invocazione '"+url+"' con payload di "+os.size()+" bytes ...");

		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		// la funzionalita' aggiuntiva deve avere realmente operato, altrimenti lo scenario non e' quello atteso
		if(verificaCorrelazione) {
			assertEquals(id, DBVerifier.getIdCorrelazioneApplicativaRichiesta(idTransazione));
			assertEquals(id, DBVerifier.getIdCorrelazioneApplicativaRisposta(idTransazione));
		}
		if(resource.contains("trasformazione")) {
			assertEquals(id, response.getHeaderFirstValue("X-Verifica-Req"));
			assertEquals(id, response.getHeaderFirstValue("X-Verifica-Res"));
		}

		// la fotografia scattata a transazione viva distingue il rilascio corretto dal caso in cui il
		// file non e' mai stato prodotto: senza, un esito verde non direbbe nulla
		verifica(idTransazione, DumpBinarioUtils.FASE_OUT_REQUEST, attesoSpillRichiesta);
		verifica(idTransazione, DumpBinarioUtils.FASE_OUT_RESPONSE, attesoSpillRisposta);

		DumpBinarioUtils.verifyNoResidui(logCore, idTransazione, resource + " (" + os.size() + " bytes)");
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

	private static byte[] leggiRisorsa(String path) throws Exception {
		try (InputStream is = MimeMultipartSogliaTest.class.getResourceAsStream(path)) {
			assertNotNull("Risorsa '"+path+"' non presente nel classpath", is);
			return Utilities.getAsByteArray(is);
		}
	}

}
