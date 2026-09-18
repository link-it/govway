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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.openspcoop2.utils.mime.MimeMultipart;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
* TransferModeUtils
*
* Motore condiviso dai test sulla modalita' di data transfer del connettore.
* La verifica si basa sugli header che il backend di test dichiara di aver ricevuto:
* il connettore configurato sui servizi applicativi di test invoca
*   /TestService/echo?notExistsHttpHeaders=${header:check-not-exists-header}
*                    &existsHttpHeaders=${header:check-exists-header}
*                    &replyHttpHeader=${header:check-exists-header}&replyPrefixHttpHeader=GovWay-TestSuite-
* per cui e' il test stesso, tramite i due header di controllo, a dichiarare cosa il backend
* debba trovare, e il valore effettivamente ricevuto torna indietro come 'GovWay-TestSuite-<header>'.
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TransferModeUtils {

	private TransferModeUtils() {}

	public static final String MODE_DEFAULT = "default";
	public static final String MODE_TRANSFER_ENCODING_CHUNKED = "transfer";
	public static final String MODE_CONTENT_LENGTH = "length";

	public static final String CONFIG_STREAMING = "streaming";
	public static final String CONFIG_DEBUG_CONNETTORI_LOG = "debugConnettoriLog";
	public static final String CONFIG_REGISTRAZIONE_MESSAGGIO = "registrazioneMessaggio";
	public static final String CONFIG_CONTENT_BUILD = "contentBuild";
	public static final String CONFIG_SBUSTAMENTO_SOAP = "sbustamentoSoap";

	public static final String API_REST = "TestDataTransferRest";
	public static final String API_SOAP = "TestDataTransferSoap";

	private static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String SOAP12_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";


	public static HttpResponse test(Logger logCore, String api, MessageType messageType,
			String mode, String config, int size) throws Exception {

		String url = System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+mode+"."+config;

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);

		Payload payload = buildPayload(messageType, size);

		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(payload.contentType);
		request.setContent(payload.content);

		if(MessageType.SOAP_11.equals(messageType)) {
			// header obbligatorio nel binding http di SOAP 1.1; l'azione viene comunque
			// individuata dalla url (identificazione 'urlBased' sulla porta applicativa)
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\""+mode+"."+config+"\"");
		}

		boolean expectedTransferMode = MODE_TRANSFER_ENCODING_CHUNKED.equals(mode) ||
				MODE_DEFAULT.equals(mode);

		if(expectedTransferMode) {
			request.addHeader("check-not-exists-header", HttpConstants.CONTENT_LENGTH);
			request.addHeader("check-exists-header", HttpConstants.TRANSFER_ENCODING);
		}
		else {
			request.addHeader("check-not-exists-header", HttpConstants.TRANSFER_ENCODING);
			request.addHeader("check-exists-header", HttpConstants.CONTENT_LENGTH);
		}

		request.setUrl(url);

		HttpResponse response = HttpUtilities.httpInvoke(request);

		assertEquals(200, response.getResultHTTPOperation());

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);

		DBVerifier.verify(idTransazione, esitoExpected, null);

		if(expectedTransferMode) {
			String hdrTest = response.getHeaderFirstValue("GovWay-TestSuite-"+HttpConstants.TRANSFER_ENCODING);
			assertNotNull(hdrTest);
			assertEquals(hdrTest, HttpConstants.TRANSFER_ENCODING_VALUE_CHUNCKED);
		}
		else {
			String hdrTest = response.getHeaderFirstValue("GovWay-TestSuite-"+HttpConstants.CONTENT_LENGTH);
			assertNotNull("Atteso header 'GovWay-TestSuite-"+HttpConstants.CONTENT_LENGTH+"'", hdrTest);
			int value = Integer.parseInt(hdrTest);

			if(CONFIG_CONTENT_BUILD.equals(config) || CONFIG_SBUSTAMENTO_SOAP.equals(config)) {
				// il contenuto viene modificato dalla configurazione, la dimensione non e' quella inviata
				assertTrue("Ricevuto '"+value+"' > 0", value>0);
			}
			else {
				assertEquals(value, payload.content.length);
			}
		}

		verificaContenutoRisposta(messageType, config, response);

		return response;
	}


	private static void verificaContenutoRisposta(MessageType messageType, String config, HttpResponse response) {

		if(CONFIG_CONTENT_BUILD.equals(config)) {
			String r = new String(response.getContent());
			if(isSoap(messageType)) {
				// la configurazione aggiunge un header soap; il template e' version-aware:
				// namespace e mustUnderstand differiscono tra soap 1.1 e soap 1.2
				boolean soap11 = MessageType.SOAP_11.equals(messageType);
				String elemento = "<nstest:test xmlns:nstest=\"http://www.example.org/test\" xmlns:soapenv=\""+
						(soap11 ? SOAP11_NAMESPACE : SOAP12_NAMESPACE)+"\" soapenv:mustUnderstand=\""+
						(soap11 ? "1" : "true")+"\">";
				String elementoInterno = "<nstest:testInterno>Hello World</nstest:testInterno>";
				assertContiene(r, elemento);
				assertContiene(r, elementoInterno);
			}
			else {
				// la configurazione aggiunge un elemento json
				assertContiene(r, "\"addElement\":\"valoreEsempioAggiunto\"");
			}
		}
		else if(CONFIG_SBUSTAMENTO_SOAP.equals(config)) {
			// l'envelope viene ricostruito da GovWay: si verifica il namespace e non il prefisso,
			// che non e' garantito coincidere con quello del messaggio inviato
			String r = new String(response.getContent());
			boolean soap11 = MessageType.SOAP_11.equals(messageType);
			assertContiene(r, ":Envelope");
			assertContiene(r, soap11 ? SOAP11_NAMESPACE : SOAP12_NAMESPACE);
		}
	}

	private static void assertContiene(String ricevuto, String atteso) {
		if(!ricevuto.contains(atteso)) {
			System.out.println("ATTESO: ["+atteso+"]");
			System.out.println("RICEVUTO: ["+ricevuto+"]");
		}
		assertTrue("Atteso elemento aggiuntivo", ricevuto.contains(atteso));
	}

	private static boolean isSoap(MessageType messageType) {
		return MessageType.SOAP_11.equals(messageType) || MessageType.SOAP_12.equals(messageType);
	}


	/* Contenuto e content-type devono provenire dalla medesima costruzione: per il multipart
	 * ogni istanza genera un boundary ed identificativi di parte differenti. */
	private static class Payload {
		private final String contentType;
		private final byte[] content;
		private Payload(String contentType, byte[] content) {
			this.contentType = contentType;
			this.content = content;
		}
	}

	private static Payload buildPayload(MessageType messageType, int size) throws Exception {
		switch (messageType) {
		case SOAP_11:
			return new Payload(HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(size).getBytes());
		case SOAP_12:
			return new Payload(HttpConstants.CONTENT_TYPE_SOAP_1_2, Bodies.getSOAPEnvelope12(size).getBytes());
		case JSON:
			return new Payload(HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(size).getBytes());
		case XML:
			return new Payload(HttpConstants.CONTENT_TYPE_XML, Bodies.getXML(size).getBytes());
		case MIME_MULTIPART:
			MimeMultipart mm = Bodies.getMultipartMixed(size);
			return new Payload(mm.getContentType(), Bodies.toByteArray(mm));
		case BINARY:
			return new Payload(HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM, getBinary(size));
		default:
			throw new Exception("MessageType '"+messageType+"' non gestito");
		}
	}

	private static byte[] getBinary(int size) {
		byte[] content = new byte[size];
		for (int i = 0; i < size; i++) {
			content[i] = (byte) (i % 256);
		}
		return content;
	}

}
