/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.registrazione_messaggi.DumpUtils;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {

	// dump abilitato
	@Test
	public void erogazione_xml_small() throws Exception {
		_testDumpAbilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void fruizione_xml_small() throws Exception {
		_testDumpAbilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void erogazione_xml_big() throws Exception {
		_testDumpAbilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.BIG_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void fruizione_xml_big() throws Exception {
		_testDumpAbilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.BIG_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void erogazione_json_small() throws Exception {
		_testDumpAbilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(), MessageType.JSON, 0);
	}
	@Test
	public void fruizione_json_small() throws Exception {
		_testDumpAbilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(), MessageType.JSON, 0);
	}
	@Test
	public void erogazione_json_big() throws Exception {
		_testDumpAbilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.BIG_SIZE).getBytes(), MessageType.JSON, 0);
	}
	@Test
	public void fruizione_json_big() throws Exception {
		_testDumpAbilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.BIG_SIZE).getBytes(), MessageType.JSON, 0);
	}
	@Test
	public void erogazione_pdf() throws Exception {
		_testDumpAbilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_PDF, Bodies.getPdf(), MessageType.BINARY, 0);
	}
	@Test
	public void fruizione_pdf() throws Exception {
		_testDumpAbilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_PDF, Bodies.getPdf(), MessageType.BINARY, 0);
	}
	@Test
	public void erogazione_zip() throws Exception {
		_testDumpAbilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_ZIP, Bodies.getZip(), MessageType.BINARY, 0);
	}
	@Test
	public void fruizione_zip() throws Exception {
		_testDumpAbilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_ZIP, Bodies.getZip(), MessageType.BINARY, 0);
	}
	@Test
	public void erogazione_multipart_related_small() throws Exception {
		MimeMultipart mm = Bodies.getMultipartRelated(Bodies.SMALL_SIZE);
		_testDumpMultipart(TipoServizio.EROGAZIONE, mm);
	}
	@Test
	public void fruizione_multipart_related_small() throws Exception {
		MimeMultipart mm = Bodies.getMultipartRelated(Bodies.SMALL_SIZE);
		_testDumpMultipart(TipoServizio.FRUIZIONE, mm);
	}
	@Test
	public void erogazione_multipart_related_big() throws Exception {
		MimeMultipart mm = Bodies.getMultipartRelated(Bodies.BIG_SIZE);
		_testDumpMultipart(TipoServizio.EROGAZIONE, mm);
	}
	@Test
	public void fruizione_multipart_related_big() throws Exception {
		MimeMultipart mm = Bodies.getMultipartRelated(Bodies.BIG_SIZE);
		_testDumpMultipart(TipoServizio.FRUIZIONE, mm);
	}
	@Test
	public void erogazione_multipart_mixed_small() throws Exception {
		MimeMultipart mm = Bodies.getMultipartMixed(Bodies.SMALL_SIZE);
		_testDumpMultipart(TipoServizio.EROGAZIONE, mm);
	}
	@Test
	public void fruizione_multipart_mixed_small() throws Exception {
		MimeMultipart mm = Bodies.getMultipartMixed(Bodies.SMALL_SIZE);
		_testDumpMultipart(TipoServizio.FRUIZIONE, mm);
	}
	@Test
	public void erogazione_multipart_mixed_big() throws Exception {
		MimeMultipart mm = Bodies.getMultipartMixed(Bodies.BIG_SIZE);
		_testDumpMultipart(TipoServizio.EROGAZIONE, mm);
	}
	
	@Test
	public void fruizione_multipart_mixed_big() throws Exception {
		MimeMultipart mm = Bodies.getMultipartMixed(Bodies.BIG_SIZE);
		_testDumpMultipart(TipoServizio.FRUIZIONE, mm);
	}
	
	private void _testDumpMultipart(TipoServizio tipo, MimeMultipart mmParams) throws Exception {
		byte [] content = Bodies.toByteArray(mmParams);
		HttpResponse response = _testDumpAbilitato(tipo, mmParams.getContentType(), content, MessageType.MIME_MULTIPART, mmParams.countBodyParts());
		MimeMultipart mm = new MimeMultipart(new ByteArrayInputStream(content), mmParams.getContentType());
		MimeMultipart mmResponse = new MimeMultipart(new ByteArrayInputStream(response.getContent()), response.getContentType());
		for(int i = 0; i < mm.countBodyParts(); i++) {
			getLoggerRegistrazioneMessaggi().debug("BodyParts["+i+"] ContentType ["+mm.getBodyPart(i).getContentType()+"] == ["+mmResponse.getBodyPart(i).getContentType()+"]");
			assertEquals(mm.getBodyPart(i).getContentType(),mmResponse.getBodyPart(i).getContentType());
			getLoggerRegistrazioneMessaggi().debug("BodyParts["+i+"] Size ["+mm.getBodyPart(i).getSize()+"] == ["+mmResponse.getBodyPart(i).getSize()+"]");
			assertEquals(mm.getBodyPart(i).getSize(),mmResponse.getBodyPart(i).getSize());
//							Assert.assertEquals(mm.getBodyPart(i).getContent(),mmAtteso.getBodyPart(i).getContent());
		}
	}
	private HttpResponse _testDumpAbilitato(TipoServizio tipo, String contentType, byte[] content, MessageType formatoMessaggio, int numAttachments) throws Exception {
		return DumpUtils.testRest(tipo, "abilitato", contentType, content, content.length, formatoMessaggio,
				true, true,
				true, true,
				true, true,
				true, true,
				false, numAttachments);
	}
	
	
	
	
	
	
	
	
	
	// dump disabilitato
	@Test
	public void disabilitato_erogazione_xml_small() throws Exception {
		_testDumpDisabilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void disabilitato_fruizione_xml_small() throws Exception {
		_testDumpDisabilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void disabilitato_erogazione_xml_big() throws Exception {
		_testDumpDisabilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.BIG_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void disabilitato_fruizione_xml_big() throws Exception {
		_testDumpDisabilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.BIG_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void disabilitato_erogazione_json_small() throws Exception {
		_testDumpDisabilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(), MessageType.JSON, 0);
	}
	@Test
	public void disabilitato_fruizione_json_small() throws Exception {
		_testDumpDisabilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(), MessageType.JSON, 0);
	}
	@Test
	public void disabilitato_erogazione_json_big() throws Exception {
		_testDumpDisabilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.BIG_SIZE).getBytes(), MessageType.JSON, 0);
	}
	@Test
	public void disabilitato_fruizione_json_big() throws Exception {
		_testDumpDisabilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.BIG_SIZE).getBytes(), MessageType.JSON, 0);
	}
	@Test
	public void disabilitato_erogazione_pdf() throws Exception {
		_testDumpDisabilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_PDF, Bodies.getPdf(), MessageType.BINARY, 0);
	}
	@Test
	public void disabilitato_fruizione_pdf() throws Exception {
		_testDumpDisabilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_PDF, Bodies.getPdf(), MessageType.BINARY, 0);
	}
	@Test
	public void disabilitato_erogazione_zip() throws Exception {
		_testDumpDisabilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_ZIP, Bodies.getZip(), MessageType.BINARY, 0);
	}
	@Test
	public void disabilitato_fruizione_zip() throws Exception {
		_testDumpDisabilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_ZIP, Bodies.getZip(), MessageType.BINARY, 0);
	}
	@Test
	public void disabilitato_erogazione_multipart_related_small() throws Exception {
		MimeMultipart mm = Bodies.getMultipartRelated(Bodies.SMALL_SIZE);
		_testDumpMultipartDisabilitato(TipoServizio.EROGAZIONE, mm);
	}
	@Test
	public void disabilitato_fruizione_multipart_related_small() throws Exception {
		MimeMultipart mm = Bodies.getMultipartRelated(Bodies.SMALL_SIZE);
		_testDumpMultipartDisabilitato(TipoServizio.FRUIZIONE, mm);
	}
	@Test
	public void disabilitato_erogazione_multipart_related_big() throws Exception {
		MimeMultipart mm = Bodies.getMultipartRelated(Bodies.BIG_SIZE);
		_testDumpMultipartDisabilitato(TipoServizio.EROGAZIONE, mm);
	}
	@Test
	public void disabilitato_fruizione_multipart_related_big() throws Exception {
		MimeMultipart mm = Bodies.getMultipartRelated(Bodies.BIG_SIZE);
		_testDumpMultipartDisabilitato(TipoServizio.FRUIZIONE, mm);
	}
	@Test
	public void disabilitato_erogazione_multipart_mixed_small() throws Exception {
		MimeMultipart mm = Bodies.getMultipartMixed(Bodies.SMALL_SIZE);
		_testDumpMultipartDisabilitato(TipoServizio.EROGAZIONE, mm);
	}
	@Test
	public void disabilitato_fruizione_multipart_mixed_small() throws Exception {
		MimeMultipart mm = Bodies.getMultipartMixed(Bodies.SMALL_SIZE);
		_testDumpMultipartDisabilitato(TipoServizio.FRUIZIONE, mm);
	}
	@Test
	public void disabilitato_erogazione_multipart_mixed_big() throws Exception {
		MimeMultipart mm = Bodies.getMultipartMixed(Bodies.BIG_SIZE);
		_testDumpMultipartDisabilitato(TipoServizio.EROGAZIONE, mm);
	}
	
	@Test
	public void disabilitato_fruizione_multipart_mixed_big() throws Exception {
		MimeMultipart mm = Bodies.getMultipartMixed(Bodies.BIG_SIZE);
		_testDumpMultipartDisabilitato(TipoServizio.FRUIZIONE, mm);
	}
	
	private void _testDumpMultipartDisabilitato(TipoServizio tipo, MimeMultipart mmParams) throws Exception {
		byte [] content = Bodies.toByteArray(mmParams);
		HttpResponse response = _testDumpAbilitato(tipo, mmParams.getContentType(), content, MessageType.MIME_MULTIPART, mmParams.countBodyParts());
		MimeMultipart mm = new MimeMultipart(new ByteArrayInputStream(content), mmParams.getContentType());
		MimeMultipart mmResponse = new MimeMultipart(new ByteArrayInputStream(response.getContent()), response.getContentType());
		for(int i = 0; i < mm.countBodyParts(); i++) {
			getLoggerRegistrazioneMessaggi().debug("BodyParts["+i+"] ContentType ["+mm.getBodyPart(i).getContentType()+"] == ["+mmResponse.getBodyPart(i).getContentType()+"]");
			assertEquals(mm.getBodyPart(i).getContentType(),mmResponse.getBodyPart(i).getContentType());
			getLoggerRegistrazioneMessaggi().debug("BodyParts["+i+"] Size ["+mm.getBodyPart(i).getSize()+"] == ["+mmResponse.getBodyPart(i).getSize()+"]");
			assertEquals(mm.getBodyPart(i).getSize(),mmResponse.getBodyPart(i).getSize());
//							Assert.assertEquals(mm.getBodyPart(i).getContent(),mmAtteso.getBodyPart(i).getContent());
		}
	}
	private HttpResponse _testDumpDisabilitato(TipoServizio tipo, String contentType, byte[] content, MessageType formatoMessaggio, int numAttachments) throws Exception {
		return DumpUtils.testRest(tipo, "disabilitato", contentType, content, content.length, formatoMessaggio,
				false, false,
				false, false,
				false, false,
				false, false,
				false, numAttachments);
	}
	
	
	
	
	
	
	// dump abilitatoRichiesta
	@Test
	public void abilitatoRichiesta_erogazione_xml_small() throws Exception {
		_testDumpAbilitatoRichiesta(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void abilitatoRichiesta_fruizione_xml_small() throws Exception {
		_testDumpAbilitatoRichiesta(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	private HttpResponse _testDumpAbilitatoRichiesta(TipoServizio tipo, String contentType, byte[] content, MessageType formatoMessaggio, int numAttachments) throws Exception {
		return DumpUtils.testRest(tipo, "abilitatoRichiesta", contentType, content, content.length, formatoMessaggio,
				true, true,
				true, true,
				false, false,
				false, false,
				false, numAttachments);
	}
	
	
	// dump abilitatoRisposta
	@Test
	public void abilitatoRisposta_erogazione_xml_small() throws Exception {
		_testDumpAbilitatoRisposta(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void abilitatoRisposta_fruizione_xml_small() throws Exception {
		_testDumpAbilitatoRisposta(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	private HttpResponse _testDumpAbilitatoRisposta(TipoServizio tipo, String contentType, byte[] content, MessageType formatoMessaggio, int numAttachments) throws Exception {
		return DumpUtils.testRest(tipo, "abilitatoRisposta", contentType, content, content.length, formatoMessaggio,
				false, false,
				false, false,
				true, true,
				true, true,
				false, numAttachments);
	}
	

	// dump client
	@Test
	public void abilitatoClient_erogazione_xml_small() throws Exception {
		_testDumpAbilitatoClient(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void abilitatoClient_fruizione_xml_small() throws Exception {
		_testDumpAbilitatoClient(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	private HttpResponse _testDumpAbilitatoClient(TipoServizio tipo, String contentType, byte[] content, MessageType formatoMessaggio, int numAttachments) throws Exception {
		return DumpUtils.testRest(tipo, "client", contentType, content, content.length, formatoMessaggio,
				true, true,
				false, false,
				false, false,
				true, true,
				false, numAttachments);
	}
	
	
	
	// dump server
	@Test
	public void abilitatoServer_erogazione_xml_small() throws Exception {
		_testDumpAbilitatoServer(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void abilitatoServer_fruizione_xml_small() throws Exception {
		_testDumpAbilitatoServer(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	private HttpResponse _testDumpAbilitatoServer(TipoServizio tipo, String contentType, byte[] content, MessageType formatoMessaggio, int numAttachments) throws Exception {
		return DumpUtils.testRest(tipo, "server", contentType, content, content.length, formatoMessaggio,
				false, false,
				true, true,
				true, true,
				false, false,
				false, numAttachments);
	}
	
	
	
	// dump headers
	@Test
	public void abilitatoHeaders_erogazione_xml_small() throws Exception {
		_testDumpAbilitatoHeaders(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void abilitatoHeaders_fruizione_xml_small() throws Exception {
		_testDumpAbilitatoHeaders(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	private HttpResponse _testDumpAbilitatoHeaders(TipoServizio tipo, String contentType, byte[] content, MessageType formatoMessaggio, int numAttachments) throws Exception {
		return DumpUtils.testRest(tipo, "headers", contentType, content, content.length, formatoMessaggio,
				true, false,
				true, false,
				true, false,
				true, false,
				false, numAttachments);
	}
	
	
	
	
	// dump body
	@Test
	public void abilitatoBody_erogazione_xml_small() throws Exception {
		_testDumpAbilitatoBody(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void abilitatoBody_fruizione_xml_small() throws Exception {
		_testDumpAbilitatoBody(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	private HttpResponse _testDumpAbilitatoBody(TipoServizio tipo, String contentType, byte[] content, MessageType formatoMessaggio, int numAttachments) throws Exception {
		return DumpUtils.testRest(tipo, "payload", contentType, content, content.length, formatoMessaggio,
				false, true,
				false, true,
				false, true,
				false, true,
				false, numAttachments);
	}
	
	
	
	// dump binarioLogConnettore
	@Test
	public void abilitatoDumpBinarioLogConnettore_erogazione_xml_small() throws Exception {
		_testDumpAbilitatoDumpBinarioLogConnettore(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	@Test
	public void abilitatoDumpBinarioLogConnettore_fruizione_xml_small() throws Exception {
		_testDumpAbilitatoDumpBinarioLogConnettore(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(Bodies.SMALL_SIZE).getBytes(), MessageType.XML, 0);
	}
	private HttpResponse _testDumpAbilitatoDumpBinarioLogConnettore(TipoServizio tipo, String contentType, byte[] content, MessageType formatoMessaggio, int numAttachments) throws Exception {
		return DumpUtils.testRest(tipo, "dumpBinarioConnettore", contentType, content, content.length, formatoMessaggio,
				false, false,
				false, false,
				false, false,
				false, false,
				false, numAttachments);
	}
}
