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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.id.UUIDUtilsGenerator;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.openapi.validator.MultipartUtilities;
import org.openspcoop2.utils.openapi.validator.TestOpenApi3Extended;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* OpenAPI30_MultipartRequestArrayTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class OpenAPI30_MultipartRequestArrayTest extends ConfigLoader {

	// https://swagger.io/docs/specification/describing-request-body/file-upload/
		
	@Test
	public void erogazione_form_data_ok_array_binary() throws Exception {
		request_ok_array_binary(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void fruizione_form_data_ok_array_binary() throws Exception {
		request_ok_array_binary(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void erogazione_mixed_ok_array_binary() throws Exception {
		request_ok_array_binary(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	@Test
	public void fruizione_mixed_ok_array_binary() throws Exception {
		request_ok_array_binary(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	private void request_ok_array_binary(TipoServizio tipo, String subtype) throws Exception {
		
		byte [] pdf = Utilities.getAsByteArray(TestOpenApi3Extended.class.getResourceAsStream("/org/openspcoop2/utils/openapi/test.pdf"));
		List<byte[]> l = new ArrayList<byte[]>();
		l.add(pdf);
		l.add(pdf);
		l.add(pdf);
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(subtype,
				l, HttpConstants.CONTENT_TYPE_PDF, "\"archivi\"", "\"attachment"+MultipartUtilities.templateNumero+".pdf\"");
		
		OpenAPI30_MultipartRequestTest.test(logCore, tipo, subtype+"-array-binary", mm,
				null, 
				false,
				null);
	}
	
	@Test
	public void erogazione_form_data_ok_array_json() throws Exception {
		request_ok_array_json(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void fruizione_form_data_ok_array_json() throws Exception {
		request_ok_array_json(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void erogazione_mixed_ok_array_json() throws Exception {
		request_ok_array_json(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	@Test
	public void fruizione_mixed_ok_array_json() throws Exception {
		request_ok_array_json(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	private void request_ok_array_json(TipoServizio tipo, String subtype) throws Exception {
		
		String cat = "{\"pet_type\": \"Cat\",  \"age\": 3}";
		String dog1 = "{\"pet_type\": \"Dog\",  \"bark\": false,  \"breed\": \"Dingo\" }";
		String dog2 = "{\"pet_type\": \"Dog\",  \"bark\": true }";
		String contenuto_cat = "{\"altro\":\"descrizione generica\", \"pet\":"+cat+"}";
		String contenuto_dog1 = "{\"altro\":\"descrizione generica\", \"pet\":"+dog1+"}";
		String contenuto_dog2 = "{\"altro\":\"descrizione generica\", \"pet\":"+dog2+"}";
		List<byte[]> l = new ArrayList<byte[]>();
		l.add(contenuto_cat.getBytes());
		l.add(contenuto_dog1.getBytes());
		l.add(contenuto_dog2.getBytes());
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(subtype,
				l, HttpConstants.CONTENT_TYPE_JSON, "\"archivi\"", "\"attachment"+MultipartUtilities.templateNumero+".json\"");
		
		OpenAPI30_MultipartRequestTest.test(logCore, tipo, subtype+"-array-json", mm,
				null, 
				false,
				null);
	}
	
	@Test
	public void erogazione_form_data_error_array_json() throws Exception {
		request_error_array_json(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void fruizione_form_data_error_array_json() throws Exception {
		request_error_array_json(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void erogazione_mixed_error_array_json() throws Exception {
		request_error_array_json(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	@Test
	public void fruizione_mixed_error_array_json() throws Exception {
		request_error_array_json(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	private void request_error_array_json(TipoServizio tipo, String subtype) throws Exception {
		
		String dog1 = "{\"pet_type\": \"Dog\",  \"bark\": false,  \"breed\": \"Dingo\" }";
		String dog2 = "{\"pet_type\": \"Dog\",  \"bark\": true }";
		String contenuto_errato1 = "{\"altroErrore\":\"descrizione generica\", \"pet\":"+dog2+"}";
		String contenuto_dog1 = "{\"altro\":\"descrizione generica\", \"pet\":"+dog1+"}";
		String contenuto_dog2 = "{\"altro\":\"descrizione generica\", \"pet\":"+dog2+"}";
		List<byte[]> l = new ArrayList<byte[]>();
		l.add(contenuto_errato1.getBytes());
		l.add(contenuto_dog1.getBytes());
		l.add(contenuto_dog2.getBytes());
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(subtype,
				l, HttpConstants.CONTENT_TYPE_JSON, "\"archivi\"", "\"attachment"+MultipartUtilities.templateNumero+".json\"");
		
		OpenAPI30_MultipartRequestTest.test(logCore, tipo, subtype+"-array-json", mm,
				"body.archivi.0: Field ''altro'' is required. (code: 1026)", 
				false,
				null);
	}
	
	
	@Test
	public void erogazione_form_data_ok_array_json_correlazione() throws Exception {
		request_ok_array_json_correlazione(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void fruizione_form_data_ok_array_json_correlazione() throws Exception {
		request_ok_array_json_correlazione(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void erogazione_mixed_ok_array_json_correlazione() throws Exception {
		request_ok_array_json_correlazione(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	@Test
	public void fruizione_mixed_ok_array_json_correlazione() throws Exception {
		request_ok_array_json_correlazione(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	private void request_ok_array_json_correlazione(TipoServizio tipo, String subtype) throws Exception {
		
		String id = UUIDUtilsGenerator.newUUID();
		
		String cat = "{\"pet_type\": \"Cat\",  \"age\": 3}";
		String dog1 = "{\"pet_type\": \"Dog\",  \"bark\": false,  \"breed\": \"Dingo\" }";
		String dog2 = "{\"pet_type\": \"Dog\",  \"bark\": true }";
		String contenuto_cat = "{\"altro\":\""+id+"\", \"pet\":"+cat+"}";
		String contenuto_dog1 = "{\"altro\":\"descrizione generica\", \"pet\":"+dog1+"}";
		String contenuto_dog2 = "{\"altro\":\"descrizione generica\", \"pet\":"+dog2+"}";
		List<byte[]> l = new ArrayList<byte[]>();
		l.add(contenuto_cat.getBytes());
		l.add(contenuto_dog1.getBytes());
		l.add(contenuto_dog2.getBytes());
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(subtype,
				l, HttpConstants.CONTENT_TYPE_JSON, "\"archivi\"", "\"attachment"+MultipartUtilities.templateNumero+".json\"");
		
		OpenAPI30_MultipartRequestTest.test(logCore, tipo, "test-correlazione-array-json", mm,
				null, 
				false,
				id);
	}
	
	@Test
	public void erogazione_form_data_ok_array_json_correlazioneSecondoPartNonSupportata() throws Exception {
		request_ok_array_json_correlazioneSecondoPartNonSupportata(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void fruizione_form_data_ok_array_json_correlazioneSecondoPartNonSupportata() throws Exception {
		request_ok_array_json_correlazioneSecondoPartNonSupportata(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void erogazione_mixed_ok_array_json_correlazioneSecondoPartNonSupportata() throws Exception {
		request_ok_array_json_correlazioneSecondoPartNonSupportata(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	@Test
	public void fruizione_mixed_ok_array_json_correlazioneSecondoPartNonSupportata() throws Exception {
		request_ok_array_json_correlazioneSecondoPartNonSupportata(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	private void request_ok_array_json_correlazioneSecondoPartNonSupportata(TipoServizio tipo, String subtype) throws Exception {
		
		String id = UUIDUtilsGenerator.newUUID();
		
		String cat = "{\"pet_type\": \"Cat\",  \"age\": 3}";
		String dog1 = "{\"pet_type\": \"Dog\",  \"bark\": false,  \"breed\": \"Dingo\" }";
		String dog2 = "{\"pet_type\": \"Dog\",  \"bark\": true }";
		String contenuto_cat = "{\"altro\":\"descrizione generica\", \"pet\":"+cat+"}";
		String contenuto_dog1 = "{\"altro\":\""+id+"\", \"pet\":"+dog1+"}";
		String contenuto_dog2 = "{\"altro\":\"descrizione generica\", \"pet\":"+dog2+"}";
		List<byte[]> l = new ArrayList<byte[]>();
		l.add(contenuto_cat.getBytes());
		l.add(contenuto_dog1.getBytes());
		l.add(contenuto_dog2.getBytes());
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(subtype,
				l, HttpConstants.CONTENT_TYPE_JSON, "\"archivi\"", "\"attachment"+MultipartUtilities.templateNumero+".json\"");
		
		OpenAPI30_MultipartRequestTest.test(logCore, tipo, "test-correlazione-array-json", mm,
				null, 
				false,
				"descrizione generica");
	}
	
	
	
	
	
	
	@Test
	public void erogazione_form_data_ok_array_json_trasformazione() throws Exception {
		request_ok_array_json_trasformazione(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void fruizione_form_data_ok_array_json_trasformazione() throws Exception {
		request_ok_array_json_trasformazione(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE);
	}
	@Test
	public void erogazione_mixed_ok_array_json_trasformazione() throws Exception {
		request_ok_array_json_trasformazione(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	@Test
	public void fruizione_mixed_ok_array_json_trasformazione() throws Exception {
		request_ok_array_json_trasformazione(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
	}
	private void request_ok_array_json_trasformazione(TipoServizio tipo, String subtype) throws Exception {
		
		String id = UUIDUtilsGenerator.newUUID();
		
		String cat = "{\"pet_type\": \"Cat\",  \"age\": 3}";
		String dog1 = "{\"pet_type\": \"Dog\",  \"bark\": false,  \"breed\": \"Dingo\" }";
		String dog2 = "{\"pet_type\": \"Dog\",  \"bark\": true }";
		String contenuto_cat = "{\"altro\":\""+id+"\", \"pet\":"+cat+"}";
		String contenuto_dog1 = "{\"altro\":\"descrizione generica\", \"pet\":"+dog1+"}";
		String contenuto_dog2 = "{\"altro\":\"descrizione generica\", \"pet\":"+dog2+"}";
		List<byte[]> l = new ArrayList<byte[]>();
		l.add(contenuto_cat.getBytes());
		l.add(contenuto_dog1.getBytes());
		l.add(contenuto_dog2.getBytes());
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(subtype,
				l, HttpConstants.CONTENT_TYPE_JSON, "\"archivi\"", "\"attachment"+MultipartUtilities.templateNumero+".json\"");
		
		OpenAPI30_MultipartRequestTest.test(logCore, tipo, "test-trasformazione-array-json", mm,
				null, 
				false,
				id);
	}
}
