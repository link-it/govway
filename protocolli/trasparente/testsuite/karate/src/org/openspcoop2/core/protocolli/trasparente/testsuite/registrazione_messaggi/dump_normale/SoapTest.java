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
package org.openspcoop2.core.protocolli.trasparente.testsuite.registrazione_messaggi.dump_normale;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.registrazione_messaggi.DumpUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {

	// dump abilitato
	@Test
	public void erogazione_soap11_small() throws Exception {
		_testDumpAbilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), MessageType.SOAP_11, 0);
	}
	@Test
	public void fruizione_soap11_small() throws Exception {
		_testDumpAbilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), MessageType.SOAP_11, 0);
	}
	@Test
	public void erogazione_soap11_big() throws Exception {
		_testDumpAbilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(), MessageType.SOAP_11, 0);
	}
	@Test
	public void fruizione_soap11_big() throws Exception {
		_testDumpAbilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(), MessageType.SOAP_11, 0);
	}
	@Test
	public void erogazione_soap12_small() throws Exception {
		_testDumpAbilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes(), MessageType.SOAP_12, 0);
	}
	@Test
	public void fruizione_soap12_small() throws Exception {
		_testDumpAbilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes(), MessageType.SOAP_12, 0);
	}
	@Test
	public void erogazione_soap12_big() throws Exception {
		_testDumpAbilitato(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(Bodies.BIG_SIZE).getBytes(), MessageType.SOAP_12, 0);
	}
	@Test
	public void fruizione_soap12_big() throws Exception {
		_testDumpAbilitato(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(Bodies.BIG_SIZE).getBytes(), MessageType.SOAP_12, 0);
	}

	@Test
	public void erogazione_multipart_related_soap11_small() throws Throwable {
		OpenSPCoop2Message mm = Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE);
		_testDumpMultipart(TipoServizio.EROGAZIONE, mm, MessageType.SOAP_11);
	}
	@Test
	public void fruizione_multipart_related_soap11_small() throws Throwable {
		OpenSPCoop2Message mm = Bodies.getSOAP11WithAttachments(Bodies.SMALL_SIZE);
		_testDumpMultipart(TipoServizio.FRUIZIONE, mm, MessageType.SOAP_11);
	}
	@Test
	public void erogazione_multipart_related_soap11_big() throws Throwable {
		OpenSPCoop2Message mm = Bodies.getSOAP11WithAttachments(Bodies.BIG_SIZE);
		_testDumpMultipart(TipoServizio.EROGAZIONE, mm, MessageType.SOAP_11);
	}
	@Test
	public void fruizione_multipart_related_soap11_big() throws Throwable {
		OpenSPCoop2Message mm = Bodies.getSOAP11WithAttachments(Bodies.BIG_SIZE);
		_testDumpMultipart(TipoServizio.FRUIZIONE, mm, MessageType.SOAP_11);
	}
	@Test
	public void erogazione_multipart_related_soap12_small() throws Throwable {
		OpenSPCoop2Message mm = Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE);
		_testDumpMultipart(TipoServizio.EROGAZIONE, mm, MessageType.SOAP_12);
	}
	@Test
	public void fruizione_multipart_related_soap12_small() throws Throwable {
		OpenSPCoop2Message mm = Bodies.getSOAP12WithAttachments(Bodies.SMALL_SIZE);
		_testDumpMultipart(TipoServizio.FRUIZIONE, mm, MessageType.SOAP_12);
	}
	@Test
	public void erogazione_multipart_related_soap12_big() throws Throwable {
		OpenSPCoop2Message mm = Bodies.getSOAP12WithAttachments(Bodies.BIG_SIZE);
		_testDumpMultipart(TipoServizio.EROGAZIONE, mm, MessageType.SOAP_12);
	}
	@Test
	public void fruizione_multipart_related_soap12_big() throws Throwable {
		OpenSPCoop2Message mm = Bodies.getSOAP12WithAttachments(Bodies.BIG_SIZE);
		_testDumpMultipart(TipoServizio.FRUIZIONE, mm, MessageType.SOAP_12);
	}
	
	private void _testDumpMultipart(TipoServizio tipo, OpenSPCoop2Message messageParam, MessageType messageType) throws Throwable {
		byte [] content = Bodies.toByteArray(messageParam);
		String contentType = messageParam.getContentType();
		HttpResponse response = _testDumpAbilitato(tipo, contentType, content, messageType, messageParam.castAsSoap().countAttachments());
		
		// Verifica
		DumpUtils.verificaRispostaSoap(messageType, contentType, content, response);
	}
	private HttpResponse _testDumpAbilitato(TipoServizio tipo, String contentType, byte[] content, MessageType formatoMessaggio, int numAttachments) throws Exception {
		return DumpUtils.testSoap(tipo, "payloadParsing", contentType, content, content.length, formatoMessaggio,
				true, true,
				true, true,
				true, true,
				true, true,
				true, numAttachments);
	}
	
}
