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
package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.dimensione_messaggi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.xml.soap.SOAPFault;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ProblemUtilities;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.id.IDUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.w3c.dom.Node;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {

	
	// *** globale (gruppo globale) ***
	@Test
	public void erogazione_globale_small() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"globale", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_globale_small() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"globale", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_globale_50kb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"globale", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_globale_50kb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"globale", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_globale_2mb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"globale", "2mb", null,
				null, false, false);
	}
	@Test
	public void fruizione_globale_2mb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"globale", "2mb", null,
				null, false, false);
	}
	@Test
	public void erogazione_globale_10mb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(1024*1024*10).getBytes(),
				"globale", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_globale_10mb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(1024*1024*10).getBytes(),
				"globale", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void erogazione_globale_10mb_header() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(1024*1024*10,1024).getBytes(),
				"globale", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_globale_10mb_header() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(1024*1024*10,1024).getBytes(),
				"globale", "2mb", "Request Payload too large",
				null, true, true);
	}
	
	
	
	// *** sendRegistrazioneDisabilitata (gruppo predefinito) ***
	@Test
	public void erogazione_registrazioneDisabilitata_small() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_small() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_50kb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_50kb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_2mb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_2mb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "2mb", "Request Payload too large",
				null, true, true);
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_small_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_small_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_50kb_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_50kb_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_50kb_requestPolicy_header() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K,1024).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_50kb_requestPolicy_header() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K,1024).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_2mb_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_2mb_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_small_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_small_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_50kb_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_50kb_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_2mb_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_2mb_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	
	
	
	// *** sendRegistrazioneAbilitata.client ***
	@Test
	public void erogazione_registrazioneAbilitata_client_small() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_small() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneAbilitata_client_50kb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.client", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_50kb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.client", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneAbilitata_client_2mb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_2mb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "2mb", "Request Payload too large",
				null, true, true);
	}
	
	@Test
	public void erogazione_registrazioneAbilitata_client_small_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_small_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_registrazioneAbilitata_client_50kb_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.client", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_50kb_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.client", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_registrazioneAbilitata_client_2mb_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_2mb_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_registrazioneAbilitata_client_small_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_small_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "small", null,
				"Risposta", false, false);
	}
	private static final Boolean semaphore = true; // risolve problema di tempistische sul controllo degli eventi per questo caso, che faceva andare in errore uno dei 4 test
	@Test
	public void erogazione_registrazioneAbilitata_client_50kb_responsePolicy() throws Exception {
		synchronized (semaphore) {
			Utilities.sleep(5000);
			_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
					"sendRegistrazioneAbilitata.client", "50kb", "Response Payload too large",
					"Risposta", true, true);
		}
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_50kb_responsePolicy() throws Exception {
		synchronized (semaphore) {
			Utilities.sleep(5000);
			_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.client", "50kb", "Response Payload too large",
				"Risposta", true, true);
		}
	}
	@Test
	public void erogazione_registrazioneAbilitata_client_2mb_responsePolicy() throws Exception {
		synchronized (semaphore) {
			Utilities.sleep(5000);
			_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "2mb", "Response Payload too large",
				"Risposta", true,
				true); // gia verificato eventi per 50kb, ma riverifico per discorso synchronized
		}
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_2mb_responsePolicy() throws Exception {
		synchronized (semaphore) {
			Utilities.sleep(5000);
			_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "2mb", "Response Payload too large",
				"Risposta", true,
				true); // gia verificato eventi per 50kb, ma riverifico per discorso synchronized
		}
	}
	
	
	
	
	// *** sendRegistrazioneAbilitata.server ***
	@Test
	public void erogazione_registrazioneAbilitata_server_small() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_small() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneAbilitata_server_50kb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.server", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_50kb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.server", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneAbilitata_server_2mb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_2mb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "2mb", "Request Payload too large",
				null, true, true);
	}
	
	@Test
	public void erogazione_registrazioneAbilitata_server_small_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_small_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_registrazioneAbilitata_server_50kb_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.server", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_50kb_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.server", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_registrazioneAbilitata_server_2mb_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_2mb_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_registrazioneAbilitata_server_small_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_small_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void erogazione_registrazioneAbilitata_server_50kb_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.server", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_50kb_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.server", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void erogazione_registrazioneAbilitata_server_2mb_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_2mb_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	
	
	
	// *** sendCorrelazioneApplicativa ***
	@Test
	public void erogazione_correlazioneApplicativa_small() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "small", null,
				idApplicativo,
				null, false, false);
	}
	@Test
	public void fruizione_correlazioneApplicativa_small() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "small", null,
				idApplicativo,
				null, false, false);
	}
	@Test
	public void erogazione_correlazioneApplicativa_50kb() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "50kb", null,
				idApplicativo,
				null, false, false);
	}
	@Test
	public void fruizione_correlazioneApplicativa_50kb() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "\"identificativoApplicativo\":\""+idApplicativo+"\"";
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "50kb", null,
				idApplicativo,
				null, false, false);
	}
	@Test
	public void erogazione_correlazioneApplicativa_2mb() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "2mb", "Request Payload too large",
				idApplicativo,
				null, true, true);
	}
	@Test
	public void fruizione_correlazioneApplicativa_2mb() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "\"identificativoApplicativo\":\""+idApplicativo+"\"";
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "2mb", "Request Payload too large",
				idApplicativo,
				null, true, true);
	}
	
	@Test
	public void erogazione_correlazioneApplicativa_small_requestPolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "small", null,
				idApplicativo,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_correlazioneApplicativa_small_requestPolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "small", null,
				idApplicativo,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_correlazioneApplicativa_50kb_requestPolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "\"identificativoApplicativo\":\""+idApplicativo+"\"";
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "50kb", "Request Payload too large",
				idApplicativo,
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_correlazioneApplicativa_50kb_requestPolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "50kb", "Request Payload too large",
				idApplicativo,
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_correlazioneApplicativa_2mb_requestPolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "2mb", "Request Payload too large",
				idApplicativo,
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_correlazioneApplicativa_2mb_requestPolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";	
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "2mb", "Request Payload too large",
				idApplicativo,
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_correlazioneApplicativa_small_responsePolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "\"identificativoApplicativo\":\""+idApplicativo+"\"";
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "small", null,
				idApplicativo,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_correlazioneApplicativa_small_responsePolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";	
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "small", null,
				idApplicativo,
				"Risposta", false, false);
	}
	@Test
	public void erogazione_correlazioneApplicativa_50kb_responsePolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "50kb", "Response Payload too large",
				idApplicativo,
				"Risposta", true, true);
	}
	@Test
	public void fruizione_correlazioneApplicativa_50kb_responsePolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "50kb", "Response Payload too large",
				idApplicativo,
				"Risposta", true, true);
	}
	@Test
	public void erogazione_correlazioneApplicativa_2mb_responsePolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";	
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "2mb", "Response Payload too large",
				idApplicativo,
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_correlazioneApplicativa_2mb_responsePolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "\"identificativoApplicativo\":\""+idApplicativo+"\"";
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "2mb", "Response Payload too large",
				idApplicativo,
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	
	
	
	
	
	// *** sendRegistrazioneDisabilitata.dumpBinarioConnettore ***
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_small() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_small() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_50kb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_50kb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_2mb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_2mb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "2mb", "Request Payload too large",
				null, true, true);
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_small_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_small_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_50kb_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_50kb_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_2mb_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_2mb_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_small_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_small_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_50kb_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_50kb_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_2mb_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_2mb_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	
	
	
	
	// *** sendRegistrazioneDisabilitata.fileTraceClient ***
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_small() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_small() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_50kb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_50kb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_2mb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_2mb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "2mb", "Request Payload too large",
				null, true, true);
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_small_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_small_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_50kb_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_50kb_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_2mb_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_2mb_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_small_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_small_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_50kb_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_50kb_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_2mb_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_2mb_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	
	
	
	
	
	// *** sendRegistrazioneDisabilitata.fileTraceServer ***
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_small() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_small() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_50kb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_50kb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_2mb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_2mb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "2mb", "Request Payload too large",
				null, true, true);
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_small_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_small_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_50kb_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_50kb_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_2mb_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_2mb_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_small_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_small_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_50kb_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_50kb_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_2mb_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_2mb_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	
	
	
	
	// *** filtro (gruppo filtro) ***
	@Test
	public void erogazione_filtro_small() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"filtro", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_filtro_small() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"filtro", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_filtro_50kb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"filtro", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_filtro_50kb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"filtro", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_filtro_2mb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_filtro_2mb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void erogazione_filtro2_2mb() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro2", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_filtro2_2mb() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro2", "50kb", null,
				null, false, false);
	}
	
	@Test
	public void erogazione_filtro_small_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"filtro", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_filtro_small_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"filtro", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_filtro_50kb_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"filtro", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_filtro_50kb_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"filtro", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_filtro_2mb_requestPolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_filtro_2mb_requestPolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_filtro_small_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"filtro", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_filtro_small_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"filtro", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void erogazione_filtro_50kb_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"filtro", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void fruizione_filtro_50kb_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"filtro", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void erogazione_filtro_2mb_responsePolicy() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_filtro_2mb_responsePolicy() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	
	
	
	
	
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, String msgErrore,
			String headerTest, boolean expectedError,
			boolean verifyEvents) throws Exception {
		return _test(
				tipoServizio, contentType, content,
				operazione, tipoTest, msgErrore,
				null,
				headerTest, expectedError,
				verifyEvents);
	}
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, String msgErrore,
			String applicativeId, 
			String headerTest, boolean expectedError,
			boolean verifyEvents) throws Exception {

		LocalDateTime dataSpedizione = LocalDateTime.now();

		String idServizio = "SoggettoInternoTest/DimensioneMassimaMessaggiSOAP/v1";
		Optional<String> gruppo = null;
		if(!"sendRegistrazioneDisabilitata".equals(operazione)) {
			gruppo = Optional.of(operazione);
		}
		if("filtro2".equals(operazione)) {
			gruppo= Optional.of("filtro");
		}
		
		boolean policyApi = true;
		if("globale".equals(operazione)) {
			policyApi = false;
		}
		
		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/"+idServizio+"/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/"+idServizio+"/"+operazione;
		url=url+"?test="+tipoTest;
		if(headerTest!=null) {
			url=url+"&TipoTestDimensioneMassimaMessaggio="+headerTest;
		}
		
		HttpRequest request = new HttpRequest();
		request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\""+operazione+"\"");
		request.setReadTimeout(20000);
						
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		request.setUrl(url);
		
		if(headerTest!=null) {
			request.addHeader("TipoTestDimensioneMassimaMessaggio", headerTest);
		}
		
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
			
		int httpCodeError = 413;
		String soapPrefixError = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT;
		long esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA);
		String errorCode = REQUEST_SIZE_EXCEEDED;
		String errorMessage = REQUEST_SIZE_EXCEEDED_MESSAGE;
		String nomePolicy = "DimensioneMassimaMessaggi";
		if(!expectedError) {
			esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		}
		if(headerTest!=null && headerTest.equals("Risposta")) {
			nomePolicy = "Risposta50K";
			if(expectedError) {
				httpCodeError = 502;
				esitoExpected = EsitiProperties.getInstance(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA);
				errorCode = RESPONSE_SIZE_EXCEEDED;
				errorMessage = RESPONSE_SIZE_EXCEEDED_MESSAGE;
				soapPrefixError = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER;
			}
		}
		else if(headerTest!=null && headerTest.equals("Richiesta")) {
			nomePolicy = "Richiesta50K";
		}
		
		if("Risposta".equals(headerTest) && expectedError && 
				(
					"sendRegistrazioneAbilitata.client".equals(operazione)
						|| 
					"sendRegistrazioneDisabilitata".equals(operazione)
						||
					"sendRegistrazioneDisabilitata.fileTraceClient".equals(operazione)
						||
					"filtro".equals(operazione)
				)
			){
			verifyOk(response, 200); // il codice http e' gia' stato impostato
		}
		else {
			if(expectedError) {
				verifyKo(response, soapPrefixError, errorCode, httpCodeError, errorMessage);
			}
			else {
				verifyOk(response, 200); // il codice http e' gia' stato impostato
			}
		}
		
		DBVerifier.verify(idTransazione, esitoExpected, msgErrore);
		
		if(expectedError && verifyEvents) {
			DBVerifier.checkEventiConViolazioneRL(idServizio, gruppo, dataSpedizione,
					response, logRateLimiting, nomePolicy, policyApi);
		}
		
		return response;
		
	}
	
	public static final String API_UNAVAILABLE = "APIUnavailable";
	public static final String API_UNAVAILABLE_MESSAGE = "The API Implementation is temporary unavailable";
	
	public static final String REQUEST_SIZE_EXCEEDED = "RequestSizeExceeded";
	public static final String REQUEST_SIZE_EXCEEDED_MESSAGE = "Request size exceeded detected";
	
	public static final String RESPONSE_SIZE_EXCEEDED = "ResponseSizeExceeded";
	public static final String RESPONSE_SIZE_EXCEEDED_MESSAGE = "Response size exceeded detected";

	public static void verifyKo(HttpResponse response, String soapPrefixError, String error, int code, String errorMsg) throws Exception {
		
		assertEquals(500, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_SOAP_1_1, response.getContentType());
		
		try {
			OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			OpenSPCoop2MessageParseResult parse = factory.createMessage(MessageType.SOAP_11, MessageRole.NONE, response.getContentType(), response.getContent());
			OpenSPCoop2Message msg = parse.getMessage_throwParseThrowable();
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			assertEquals(true ,soapMsg.isFault());
			SOAPFault soapFault = soapMsg.getSOAPBody().getFault();
			assertNotNull(soapFault);
			
			assertNotNull(soapFault.getFaultCodeAsQName());
			String faultCode = soapFault.getFaultCodeAsQName().getLocalPart();
			assertNotNull(faultCode);
			assertEquals(soapPrefixError+"."+error, faultCode);
			
			String faultString = soapFault.getFaultString();
			assertNotNull(faultString);
			assertEquals(errorMsg, faultString);
			
			String faultActor = soapFault.getFaultActor();
			assertNotNull(faultActor);
			assertEquals("http://govway.org/integration", faultActor);
						
			assertEquals(true ,ProblemUtilities.existsProblem(soapFault.getDetail(), getLoggerCore()));
			Node problemNode = ProblemUtilities.getProblem(soapFault.getDetail(), getLoggerCore());
			
			ProblemUtilities.verificaProblem(problemNode, 
					code, error, errorMsg, true, 
					getLoggerCore());
			
			assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
			
			String headerAfter = response.getHeaderFirstValue(HttpConstants.RETRY_AFTER);
			assertEquals("RetryAfter verifica non presente ("+headerAfter+")", true, headerAfter==null);
						
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void verifyOk(HttpResponse response, int code) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_SOAP_1_1, response.getContentType());
		
	}
}