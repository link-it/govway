/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.io.File;
import java.util.UUID;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {

	private static final String AZIONE_TEST = "test";
	private static final String AZIONE_COMPATIBILITA = "compatibilita";
	
	@Test
	public void erogazioneCorrelazioneApplicativaRequest() throws UtilsException {
		test(TipoServizio.EROGAZIONE, AZIONE_TEST, HttpConstants.CONTENT_TYPE_JSON, false, true, true);
	}
	@Test
	public void fruizioneCorrelazioneApplicativaRequest() throws UtilsException {
		test(TipoServizio.FRUIZIONE, AZIONE_TEST, HttpConstants.CONTENT_TYPE_JSON, false, true, true);
	}
	
	@Test
	public void erogazioneCorrelazioneApplicativaCompatibilita() throws UtilsException {
		test(TipoServizio.EROGAZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_JSON, false, true, true);
	}
	@Test
	public void fruizioneCorrelazioneApplicativaCompatibilita() throws UtilsException {
		test(TipoServizio.FRUIZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_JSON, false, true, true);
	}
	
	@Test
	public void erogazioneCorrelazioneApplicativaCompatibilitaCharset() throws UtilsException {
		test(TipoServizio.EROGAZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_JSON+"; "+HttpConstants.CONTENT_TYPE_PARAMETER_CHARSET+"="+Charset.UTF_8.getValue(), false, true, true);
	}
	@Test
	public void fruizioneCorrelazioneApplicativaCompatibilitaCharset() throws UtilsException {
		test(TipoServizio.FRUIZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_JSON+";"+HttpConstants.CONTENT_TYPE_PARAMETER_CHARSET+"=\""+Charset.UTF_8.getValue()+"\"", false, true, true);
	}
	
	@Test
	public void erogazioneCorrelazioneApplicativaCompatibilitaErrorContentType() throws UtilsException {
		test(TipoServizio.EROGAZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_XML, false, false, false);
	}
	@Test
	public void fruizioneCorrelazioneApplicativaCompatibilitaErrorContentType() throws UtilsException {
		test(TipoServizio.FRUIZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_XML, false, false, false);
	}
	
	@Test
	public void erogazioneCorrelazioneApplicativaCompatibilitaCharsetErrorContentType() throws UtilsException {
		test(TipoServizio.EROGAZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_XML+"; "+HttpConstants.CONTENT_TYPE_PARAMETER_CHARSET+"="+Charset.UTF_8.getValue(), false, false, false);
	}
	@Test
	public void fruizioneCorrelazioneApplicativaCompatibilitaCharsetErrorContentType() throws UtilsException {
		test(TipoServizio.FRUIZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_XML+";"+HttpConstants.CONTENT_TYPE_PARAMETER_CHARSET+"=\""+Charset.UTF_8.getValue()+"\"", false, false, false);
	}
		
	@Test
	public void erogazioneCorrelazioneApplicativaCompatibilitaErrorPayload() throws UtilsException {
		test(TipoServizio.EROGAZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_JSON, true, false, false);
	}
	@Test
	public void fruizioneCorrelazioneApplicativaCompatibilitaErrorPayload() throws UtilsException {
		test(TipoServizio.FRUIZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_JSON, true, false, false);
	}
	
	
	
	@Test
	public void erogazioneCorrelazioneApplicativaCompatibilitaErrorContentTypeRisposta() throws UtilsException {
		test(TipoServizio.EROGAZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_JSON, false, true, false);
	}
	@Test
	public void fruizioneCorrelazioneApplicativaCompatibilitaErrorContentTypeRisposta() throws UtilsException {
		test(TipoServizio.FRUIZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_JSON, false, true, false);
	}
	
	@Test
	public void erogazioneCorrelazioneApplicativaCompatibilitaCharsetErrorContentTypeRisposta() throws UtilsException {
		test(TipoServizio.EROGAZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_JSON+"; "+HttpConstants.CONTENT_TYPE_PARAMETER_CHARSET+"="+Charset.UTF_8.getValue(), false, true, false);
	}
	@Test
	public void fruizioneCorrelazioneApplicativaCompatibilitaCharsetErrorContentTypeRisposta() throws UtilsException {
		test(TipoServizio.FRUIZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_JSON+";"+HttpConstants.CONTENT_TYPE_PARAMETER_CHARSET+"=\""+Charset.UTF_8.getValue()+"\"", false, true, false);
	}
		
	@Test
	public void erogazioneCorrelazioneApplicativaCompatibilitaErrorPayloadRisposta() throws UtilsException {
		test(TipoServizio.EROGAZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_JSON, true, true, false);
	}
	@Test
	public void fruizioneCorrelazioneApplicativaCompatibilitaErrorPayloadRisposta() throws UtilsException {
		test(TipoServizio.FRUIZIONE, AZIONE_COMPATIBILITA, HttpConstants.CONTENT_TYPE_JSON, true, true, false);
	}
	
	
	
	private static HttpResponse test(TipoServizio tipoServizio, String azione, String contentType, boolean modificaElemento1, boolean expectedRequestMatch, boolean expectedResponseMatch) throws UtilsException {
		
		File f = null;
		try {
			
			String url = tipoServizio == TipoServizio.EROGAZIONE
					? System.getProperty("govway_base_path") + "/SoggettoInternoTest/TestTrasformazioneInfoIntegrazione/v1/"+azione
					: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestTrasformazioneInfoIntegrazione/v1/"+azione;
	
			HttpRequest request = new HttpRequest();
			
			String idCorrelazioneTest = UUID.randomUUID().toString();
			request.addHeader("x-id-correlazione-test",idCorrelazioneTest);
			
			String contentTypeRequest = contentType;
							
			request.setReadTimeout(20000);
							
			request.setMethod(HttpRequestMethod.POST);
			request.setContentType(contentTypeRequest);
			
			String req = Bodies.getJson(Bodies.SMALL_SIZE);
			if(contentType.startsWith(HttpConstants.CONTENT_TYPE_XML)) {
				req = Bodies.getXML(Bodies.SMALL_SIZE);
			}
			if(modificaElemento1 && !expectedRequestMatch) {
				req = req.replace("\"claim-1\"", "\"claim-ModificatoPerErrore\"");
			}
			request.setContent(req.getBytes());
			
			if(!expectedResponseMatch) {
				try {
					if(modificaElemento1 && expectedRequestMatch) {
						req = req.replace("\"claim-1\"", "\"claim-ModificatoPerErrore\"");
					}
					f = File.createTempFile("test", "example");
					FileSystemUtilities.writeFile(f, req.getBytes());
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
			
			String atteso = contentTypeRequest.replace("; ", ";");
			atteso = atteso.replace("\""+Charset.UTF_8.getValue()+"\"", Charset.UTF_8.getValue());
						
			if(!expectedResponseMatch) {
				url = url + "?";
				url = url + "destFile="+TransportUtils.urlEncodeParam(f.getAbsolutePath(), Charset.UTF_8.getValue());
				if(!modificaElemento1) {
					url = url + "&";
					url = url + "destFileContentType="+TransportUtils.urlEncodeParam(HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM, Charset.UTF_8.getValue());
					atteso = HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
				}
			}
			
			request.setUrl(url);
			
			HttpResponse response = HttpUtilities.httpInvoke(request);
	
			String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
			assertNotNull(idTransazione);
			
			assertEquals(200, response.getResultHTTPOperation());
			assertEquals("Ricevuto ["+response.getContentType()+"] atteso ["+atteso+"]", atteso, response.getContentType());
					
			verifiche(azione, response, idTransazione, idCorrelazioneTest, expectedRequestMatch, expectedResponseMatch);
			
			return response;
			
		}finally {
			if(f!=null) {
				FileSystemUtilities.deleteFile(f);
			}
		}
		
	}
	
	public static void verifiche(String azione, HttpResponse response, String idTransazione, String idCorrelazioneTest, boolean expectedRequestMatch, boolean expectedResponseMatch) {
		String headerName = "x-test-trasformazione-correlazione"+(AZIONE_COMPATIBILITA.equals(azione) ? "-compatibilita" : "");
		String responseIntegrazioneInfo = response.getHeaderFirstValue(headerName);
		if(expectedRequestMatch) {
			assertNotNull("check responseIntegrazioneInfo ("+headerName+") per idTransazione not null: "+idTransazione, responseIntegrazioneInfo);
			assertEquals("check responseIntegrazioneInfo ("+headerName+") per idTransazione: "+idTransazione,responseIntegrazioneInfo,idCorrelazioneTest);
		}
		else {
			assertNull("check-null responseIntegrazioneInfo ("+headerName+") per idTransazione null: "+idTransazione, responseIntegrazioneInfo);
		}
		
		if(AZIONE_COMPATIBILITA.equals(azione)) {
			headerName = "x-test-trasformazione-correlazione-compatibilita-risposta";
			responseIntegrazioneInfo = response.getHeaderFirstValue(headerName);
			if(expectedResponseMatch) {
				assertNotNull("check responseIntegrazioneInfo risposta ("+headerName+") per idTransazione not null: "+idTransazione, responseIntegrazioneInfo);
				assertEquals("check responseIntegrazioneInfo risposta ("+headerName+") per idTransazione: "+idTransazione,responseIntegrazioneInfo,idCorrelazioneTest);
			}
			else {
				assertNull("check-null responseIntegrazioneInfo risposta ("+headerName+") per idTransazione null: "+idTransazione, responseIntegrazioneInfo);
			}
		}
	}
	
}
