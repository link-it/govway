/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.multipart;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import javax.mail.BodyPart;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.parametri.OpenAPI30_Utils;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.id.UUIDUtilsGenerator;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.openapi.validator.MultipartUtilities;
import org.openspcoop2.utils.openapi.validator.TestOpenApi3Extended;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
* OpenAPI30_HeaderParameterSerializationTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class OpenAPI30_MultipartRequestTest extends ConfigLoader {
	
	// https://swagger.io/docs/specification/describing-request-body/multipart-requests/
		
	@Test
	public void erogazione_form_data_ok() throws Exception {
		requestOk(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void fruizione_form_data_ok() throws Exception {
		requestOk(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void erogazione_mixed_ok() throws Exception {
		requestOk(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	@Test
	public void fruizione_mixed_ok() throws Exception {
		requestOk(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	private void requestOk(TipoServizio tipo, String subtype) throws Exception {
		
		String cat = "{\"pet_type\": \"Cat\",  \"age\": 3}";
		String contenuto_cat = "{\"altro\":\"descrizione generica\", \"pet\":"+cat+"}";
		byte [] pdf = Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test.pdf"));
		byte [] pdfEncodedBase64 = Base64Utilities.encode(pdf);
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(subtype,
				Integer.MAX_VALUE+"", HttpConstants.CONTENT_TYPE_PLAIN, "\"id\"", null,
				contenuto_cat, HttpConstants.CONTENT_TYPE_JSON, "\"metadati\"", null,
				pdf, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf\"", "\"attachment.pdf\"",
				pdfEncodedBase64, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf2\"", "\"attachment2.pdf\"",
				null, null, null, null);
		
		test(logCore, tipo, subtype, mm,
				null, 
				false,
				null);
	}
	
	
	@Test
	public void erogazione_form_data_error_partNonPrevista() throws Exception {
		request_error_partNonPrevista(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void fruizione_form_data_error_partNonPrevista() throws Exception {
		request_error_partNonPrevista(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void erogazione_mixed_error_partNonPrevista() throws Exception {
		request_error_partNonPrevista(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	@Test
	public void fruizione_mixed_error_partNonPrevista() throws Exception {
		request_error_partNonPrevista(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	private void request_error_partNonPrevista(TipoServizio tipo, String subtype) throws Exception {
		
		String cat = "{\"pet_type\": \"Cat\",  \"age\": 3}";
		String contenuto_cat = "{\"altro\":\"descrizione generica\", \"pet\":"+cat+"}";
		byte [] pdf = Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test.pdf"));
		byte [] pdfEncodedBase64 = Base64Utilities.encode(pdf);
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(subtype,
				Integer.MAX_VALUE+"", HttpConstants.CONTENT_TYPE_PLAIN, "\"id\"", null,
				contenuto_cat, HttpConstants.CONTENT_TYPE_JSON, "\"metadati\"", null,
				pdf, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf\"", "\"attachment.pdf\"",
				pdfEncodedBase64, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf2\"", "\"attachment2.pdf\"",
				pdf, HttpConstants.CONTENT_TYPE_ZIP, "\"docOther\"", "\"attachment.bin\"");
		
		test(logCore, tipo, subtype, mm,
				"body: Additional property ''docOther'' is not allowed. (code: 1000)", 
				false,
				null);
	}
	
	
	
	
	
	@Test
	public void erogazione_form_data_error_contenuto1() throws Exception {
		request_error_contenuto1(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void fruizione_form_data_error_contenuto1() throws Exception {
		request_error_contenuto1(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void erogazione_mixed_error_contenuto1() throws Exception {
		request_error_contenuto1(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	@Test
	public void fruizione_mixed_error_contenuto1() throws Exception {
		request_error_contenuto1(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	private void request_error_contenuto1(TipoServizio tipo, String subtype) throws Exception {
		
		String dog2 = "{\"pet_type\": \"Dog\",  \"bark\": true }";
		String contenuto_errato1 = "{\"altroErrore\":\"descrizione generica\", \"pet\":"+dog2+"}";
		byte [] pdf = Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test.pdf"));
		byte [] pdfEncodedBase64 = Base64Utilities.encode(pdf);
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(subtype,
				Integer.MAX_VALUE+"", HttpConstants.CONTENT_TYPE_PLAIN, "\"id\"", null,
				contenuto_errato1, HttpConstants.CONTENT_TYPE_JSON, "\"metadati\"", null,
				pdf, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf\"", "\"attachment.pdf\"",
				pdfEncodedBase64, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf2\"", "\"attachment2.pdf\"",
				pdf, HttpConstants.CONTENT_TYPE_ZIP, "\"docOther\"", "\"attachment.bin\"");
		
		test(logCore, tipo, subtype, mm,
				"body.metadati: Field ''altro'' is required. (code: 1026)", 
				false,
				null);
	}
	
	
	
	
	@Test
	public void erogazione_form_data_error_contenuto2() throws Exception {
		request_error_contenuto2(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void fruizione_form_data_error_contenuto2() throws Exception {
		request_error_contenuto2(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void erogazione_mixed_error_contenuto2() throws Exception {
		request_error_contenuto2(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	@Test
	public void fruizione_mixed_error_contenuto2() throws Exception {
		request_error_contenuto2(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	private void request_error_contenuto2(TipoServizio tipo, String subtype) throws Exception {
		
		String catErrato = "{\"pet_type\": \"CatErrato\",  \"age\": 3}";
		String contenuto_errato = "{\"altro\":\"descrizione generica\", \"pet\":"+catErrato+"}";
		byte [] pdf = Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test.pdf"));
		byte [] pdfEncodedBase64 = Base64Utilities.encode(pdf);
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(subtype,
				Integer.MAX_VALUE+"", HttpConstants.CONTENT_TYPE_PLAIN, "\"id\"", null,
				contenuto_errato, HttpConstants.CONTENT_TYPE_JSON, "\"metadati\"", null,
				pdf, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf\"", "\"attachment.pdf\"",
				pdfEncodedBase64, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf2\"", "\"attachment2.pdf\"",
				pdf, HttpConstants.CONTENT_TYPE_ZIP, "\"docOther\"", "\"attachment.bin\"");
		
		test(logCore, tipo, subtype, mm,
				"body.metadati.pet: Schema selection can''t be made for discriminator ''pet_type'' with value ''CatErrato''. (code: 1003)", 
				false,
				null);
	}
	
	
	
	@Test
	public void erogazione_form_data_error_required_part_not_found() throws Exception {
		request_required_part_not_found(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void fruizione_form_data_required_part_not_found() throws Exception {
		request_required_part_not_found(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void erogazione_mixed_required_part_not_found() throws Exception {
		request_required_part_not_found(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	@Test
	public void fruizione_mixed_required_part_not_found() throws Exception {
		request_required_part_not_found(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	private void request_required_part_not_found(TipoServizio tipo, String subtype) throws Exception {
		
		String cat = "{\"pet_type\": \"Cat\",  \"age\": 3}";
		String contenuto_cat = "{\"altro\":\"descrizione generica\", \"pet\":"+cat+"}";
		byte [] pdf = Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test.pdf"));
		byte [] pdfEncodedBase64 = Base64Utilities.encode(pdf);
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(subtype,
				Integer.MAX_VALUE+"", HttpConstants.CONTENT_TYPE_PLAIN, "\"id\"", null,
				contenuto_cat, HttpConstants.CONTENT_TYPE_JSON, "\"metadati\"", null,
				null, null, null, null,
				pdfEncodedBase64, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf2\"", "\"attachment2.pdf\"",
				null, null, null, null);
		
		test(logCore, tipo, subtype, mm,
				"body: Field ''docPdf'' is required. (code: 1026)", 
				false,
				null);
	}
	
	
	
	
	@Test
	public void erogazione_form_data_error_invalid_content_type() throws Exception {
		request_error_invalid_content_type(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void fruizione_form_data_error_invalid_content_type() throws Exception {
		request_error_invalid_content_type(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void erogazione_mixed_error_invalid_content_type() throws Exception {
		request_error_invalid_content_type(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	@Test
	public void fruizione_mixed_error_invalid_content_type() throws Exception {
		request_error_invalid_content_type(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	private void request_error_invalid_content_type(TipoServizio tipo, String subtypeParam) throws Exception {
		
		String cat = "{\"pet_type\": \"Cat\",  \"age\": 3}";
		String contenuto_cat = "{\"altro\":\"descrizione generica\", \"pet\":"+cat+"}";
		byte [] pdf = Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test.pdf"));
		byte [] pdfEncodedBase64 = Base64Utilities.encode(pdf);
		
		String subtypeWrong = HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE.equals(subtypeParam) ? HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE : HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE;
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(subtypeWrong,
				Integer.MAX_VALUE+"", HttpConstants.CONTENT_TYPE_PLAIN, "\"id\"", null,
				contenuto_cat, HttpConstants.CONTENT_TYPE_JSON, "\"metadati\"", null,
				pdf, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf\"", "\"attachment.pdf\"",
				pdfEncodedBase64, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf2\"", "\"attachment2.pdf\"",
				null, null, null, null);
		
		test(logCore, tipo, subtypeParam, mm,
				"Content type ''multipart/"+subtypeWrong+"%is not allowed for body content. (code: 203)", 
				false,
				null);
	}
	
	
	
	
	@Test
	public void erogazione_form_data_ok_dump() throws Exception {
		requestOkDump(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void fruizione_form_data_ok_dump() throws Exception {
		requestOkDump(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void erogazione_mixed_ok_dump() throws Exception {
		requestOkDump(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	@Test
	public void fruizione_mixed_ok_dump() throws Exception {
		requestOkDump(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	private void requestOkDump(TipoServizio tipo, String subtype) throws Exception {
		
		String dog1 = "{\"pet_type\": \"Dog\",  \"bark\": false,  \"breed\": \"Dingo\" }";
		String contenuto_dog1 = "{\"altro\":\"descrizione generica\", \"pet\":"+dog1+"}";
		byte [] pdf = Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test.pdf"));
		byte [] pdfEncodedBase64 = Base64Utilities.encode(pdf);
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE,
				Integer.MAX_VALUE+"", HttpConstants.CONTENT_TYPE_PLAIN, "\"id\"", null,
				contenuto_dog1, HttpConstants.CONTENT_TYPE_JSON, "\"metadati\"", null,
				pdf, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf\"", "\"attachment.pdf\"",
				pdfEncodedBase64, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf2\"", "\"attachment2.pdf\"",
				null, null, null, null);
		
		test(logCore, tipo, "test-dump", mm,
				null, 
				true,
				null);
	}
	
	
	
	
	@Test
	public void erogazione_form_data_ok_correlazione() throws Exception {
		requestOkcorrelazione(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void fruizione_form_data_ok_correlazione() throws Exception {
		requestOkcorrelazione(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void erogazione_mixed_ok_correlazione() throws Exception {
		requestOkcorrelazione(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	@Test
	public void fruizione_mixed_ok_correlazione() throws Exception {
		requestOkcorrelazione(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	private void requestOkcorrelazione(TipoServizio tipo, String subtype) throws Exception {
		
		String id = UUIDUtilsGenerator.newUUID();
		
		String dog1 = "{\"pet_type\": \"Dog\",  \"bark\": false,  \"breed\": \"Dingo\" }";
		String contenuto_dog1 = "{\"altro\":\""+id+"\", \"pet\":"+dog1+"}";
		byte [] pdf = Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test.pdf"));
		byte [] pdfEncodedBase64 = Base64Utilities.encode(pdf);
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE,
				Integer.MAX_VALUE+"", HttpConstants.CONTENT_TYPE_PLAIN, "\"id\"", null,
				contenuto_dog1, HttpConstants.CONTENT_TYPE_JSON, "\"metadati\"", null,
				pdf, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf\"", "\"attachment.pdf\"",
				pdfEncodedBase64, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf2\"", "\"attachment2.pdf\"",
				null, null, null, null);
		
		test(logCore, tipo, "test-correlazione", mm,
				null, 
				false,
				id);
	}
	
	
	
	
	
	static void test(Logger logCore, TipoServizio tipoServizio, String resource, MimeMultipart mm, 
			String errore, boolean checkDump, String idCorrelazioneAttesa) throws Exception {
		

		final String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/OpenAPIValidazioneMultipartRequest/v1/multipart/"+resource
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/OpenAPIValidazioneMultipartRequest/v1/multipart/"+resource;
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		request.setUrl(sb.toString());
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		mm.writeTo(os);
		os.flush();
		os.close();
		
		request.setContentType(mm.getContentType());
		request.setContent(os.toByteArray());
		
		logCore.info("Test con resource "+resource+" ...");
		HttpResponse response = HttpUtilities.httpInvoke(request);
		
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		int esitoExpected = -1;
		if(errore==null) {
			assertEquals(200, response.getResultHTTPOperation());
			logCore.info("CT_req: ["+mm.getContentType().replace("\r", "").replace("\n", "").replace("\t", "")+"]");
			logCore.info("CT_res: ["+response.getContentType()+"]");
			assertEquals(mm.getContentType().replace("\r", "").replace("\n", "").replace("\t", ""), response.getContentType());
			
			MimeMultipart mmResponse = new MimeMultipart(new ByteArrayInputStream(response.getContent()), response.getContentType());
			
			for (int i = 0; i < mm.countBodyParts(); i++) {
				
				logCore.info("Analizzo part-"+i);
				
				BodyPart partReq = mm.getBodyPart(i);
				BodyPart partRes = mmResponse.getBodyPart(i);
				
				assertEquals(partReq.getContentType(),partRes.getContentType());
				
				InputStream isReq = partReq.getInputStream();
				InputStream isRes = partRes.getInputStream();
				byte[] req = Utilities.getAsByteArray(isReq);
				byte[] res = Utilities.getAsByteArray(isRes);
				try {
					assertArrayEquals(req, res);
				}catch(Throwable t) {
					File fReq = File.createTempFile("req", ".test"); 
					File fRes = File.createTempFile("res", ".test");
					FileSystemUtilities.writeFile(fReq, req);
					FileSystemUtilities.writeFile(fRes, res);
					throw new Exception("Assert equals failed for part-"+i+" (req:"+fReq.getAbsolutePath()+"  res:"+fRes.getAbsolutePath()+"): "+t.getMessage(),t);
				}
				
			}
			
			esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
						
		}
		else {
			OpenAPI30_Utils.verifyKo(response);
			
			esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_VALIDAZIONE_RICHIESTA);
		}

		
		DBVerifier.verify(idTransazione, esitoExpected, errore);
		
		if(checkDump) {
			
			org.openspcoop2.core.protocolli.trasparente.testsuite.registrazione_messaggi.DBVerifier.verify(idTransazione, 
					true, true,
					true, true,
					true, true,
					true, true,
					false, -1,
					mm.getContentType(), os.size(), MessageType.MIME_MULTIPART.name());
			
		}
		
		if(idCorrelazioneAttesa!=null) {
		
			String idCorrelazioneRichiesta = DBVerifier.getIdCorrelazioneApplicativaRichiesta(idTransazione);
			String idCorrelazioneRisposta = DBVerifier.getIdCorrelazioneApplicativaRisposta(idTransazione);
			
			assertEquals(idCorrelazioneAttesa, idCorrelazioneRichiesta); 
			assertEquals(idCorrelazioneAttesa, idCorrelazioneRisposta); 	
					
		}
		
	}

	
}
