/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.id.IDUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapChunkedTest extends ConfigLoader {

	private static final boolean chunkedMode = true;
	
	// *** globale (gruppo globale) ***
	@Test
	public void erogazione_globale_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"globale", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_globale_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"globale", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_globale_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"globale", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_globale_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"globale", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_globale_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"globale", "2mb", null,
				null, false, false);
	}
	@Test
	public void fruizione_globale_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"globale", "2mb", null,
				null, false, false);
	}
	@Test
	public void erogazione_globale_10mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(1024*1024*10).getBytes(),
				"globale", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_globale_10mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(1024*1024*10).getBytes(),
				"globale", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void erogazione_globale_10mb_header() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(1024*1024*10,1024).getBytes(),
				"globale", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_globale_10mb_header() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(1024*1024*10,1024).getBytes(),
				"globale", "2mb", "Request Payload too large",
				null, true, true);
	}
	
	
	
	// *** sendRegistrazioneDisabilitata (gruppo predefinito) ***
	@Test
	public void erogazione_registrazioneDisabilitata_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "2mb", "Request Payload too large",
				null, true, true);
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_small_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_small_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_50kb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_50kb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_50kb_requestPolicy_header() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K,1024).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_50kb_requestPolicy_header() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K,1024).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_2mb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_2mb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_small_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_small_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_50kb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_50kb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_2mb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_2mb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	
	
	
	// *** sendRegistrazioneAbilitata.client ***
	@Test
	public void erogazione_registrazioneAbilitata_client_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneAbilitata_client_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.client", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.client", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneAbilitata_client_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "2mb", "Request Payload too large",
				null, true, true);
	}
	
	@Test
	public void erogazione_registrazioneAbilitata_client_small_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_small_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_registrazioneAbilitata_client_50kb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.client", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_50kb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.client", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_registrazioneAbilitata_client_2mb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_2mb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_registrazioneAbilitata_client_small_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_small_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "small", null,
				"Risposta", false, false);
	}
	private static final String semaphore = "true"; // risolve problema di tempistische sul controllo degli eventi per questo caso, che faceva andare in errore uno dei 4 test
	@Test
	public void erogazione_registrazioneAbilitata_client_50kb_responsePolicy() throws Exception {
		synchronized (semaphore) {
			Utilities.sleep(5000);
			SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
					"sendRegistrazioneAbilitata.client", "50kb", "Response Payload too large",
					"Risposta", true, true);
		}
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_50kb_responsePolicy() throws Exception {
		synchronized (semaphore) {
			Utilities.sleep(5000);
			SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.client", "50kb", "Response Payload too large",
				"Risposta", true, true);
		}
	}
	@Test
	public void erogazione_registrazioneAbilitata_client_2mb_responsePolicy() throws Exception {
		synchronized (semaphore) {
			Utilities.sleep(5000);
			SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "2mb", "Response Payload too large",
				"Risposta", true,
				true); // gia verificato eventi per 50kb, ma riverifico per discorso synchronized
		}
	}
	@Test
	public void fruizione_registrazioneAbilitata_client_2mb_responsePolicy() throws Exception {
		synchronized (semaphore) {
			Utilities.sleep(5000);
			SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.client", "2mb", "Response Payload too large",
				"Risposta", true,
				true); // gia verificato eventi per 50kb, ma riverifico per discorso synchronized
		}
	}
	
	
	
	
	// *** sendRegistrazioneAbilitata.server ***
	@Test
	public void erogazione_registrazioneAbilitata_server_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneAbilitata_server_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.server", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.server", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneAbilitata_server_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "2mb", "Request Payload too large",
				null, true, true);
	}
	
	@Test
	public void erogazione_registrazioneAbilitata_server_small_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_small_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_registrazioneAbilitata_server_50kb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.server", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_50kb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.server", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_registrazioneAbilitata_server_2mb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_2mb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_registrazioneAbilitata_server_small_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_small_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void erogazione_registrazioneAbilitata_server_50kb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.server", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_50kb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitata.server", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void erogazione_registrazioneAbilitata_server_2mb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneAbilitata_server_2mb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneAbilitata.server", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	
	
	
	// *** sendCorrelazioneApplicativa ***
	@Test
	public void erogazione_correlazioneApplicativa_small() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "small", null,
				idApplicativo,
				null, false, false);
	}
	@Test
	public void fruizione_correlazioneApplicativa_small() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "small", null,
				idApplicativo,
				null, false, false);
	}
	@Test
	public void erogazione_correlazioneApplicativa_50kb() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "50kb", null,
				idApplicativo,
				null, false, false);
	}
	@Test
	public void fruizione_correlazioneApplicativa_50kb() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "\"identificativoApplicativo\":\""+idApplicativo+"\"";
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "50kb", null,
				idApplicativo,
				null, false, false);
	}
	@Test
	public void erogazione_correlazioneApplicativa_2mb() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "2mb", "Request Payload too large",
				idApplicativo,
				null, true, true);
	}
	@Test
	public void fruizione_correlazioneApplicativa_2mb() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "\"identificativoApplicativo\":\""+idApplicativo+"\"";
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "2mb", "Request Payload too large",
				idApplicativo,
				null, true, true);
	}
	
	@Test
	public void erogazione_correlazioneApplicativa_small_requestPolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "small", null,
				idApplicativo,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_correlazioneApplicativa_small_requestPolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "small", null,
				idApplicativo,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_correlazioneApplicativa_50kb_requestPolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "\"identificativoApplicativo\":\""+idApplicativo+"\"";
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "50kb", "Request Payload too large",
				idApplicativo,
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_correlazioneApplicativa_50kb_requestPolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "50kb", "Request Payload too large",
				idApplicativo,
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_correlazioneApplicativa_2mb_requestPolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "2mb", "Request Payload too large",
				idApplicativo,
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_correlazioneApplicativa_2mb_requestPolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";	
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "2mb", "Request Payload too large",
				idApplicativo,
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_correlazioneApplicativa_small_responsePolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "\"identificativoApplicativo\":\""+idApplicativo+"\"";
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "small", null,
				idApplicativo,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_correlazioneApplicativa_small_responsePolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";	
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "small", null,
				idApplicativo,
				"Risposta", false, false);
	}
	@Test
	public void erogazione_correlazioneApplicativa_50kb_responsePolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "50kb", "Response Payload too large",
				idApplicativo,
				"Risposta", true, true);
	}
	@Test
	public void fruizione_correlazioneApplicativa_50kb_responsePolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "50kb", "Response Payload too large",
				idApplicativo,
				"Risposta", true, true);
	}
	@Test
	public void erogazione_correlazioneApplicativa_2mb_responsePolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";	
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "2mb", "Response Payload too large",
				idApplicativo,
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_correlazioneApplicativa_2mb_responsePolicy() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "\"identificativoApplicativo\":\""+idApplicativo+"\"";
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE, idApplicativoClaim).getBytes(),
				"sendCorrelazioneApplicativa", "2mb", "Response Payload too large",
				idApplicativo,
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	
	
	
	
	
	// *** sendRegistrazioneDisabilitata.dumpBinarioConnettore ***
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "2mb", "Request Payload too large",
				null, true, true);
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_small_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_small_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_50kb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_50kb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_2mb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_2mb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_small_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_small_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_50kb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_50kb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_dumpBinarioConnettore_2mb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_dumpBinarioConnettore_2mb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.dumpBinarioConnettore", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	
	
	
	
	// *** sendRegistrazioneDisabilitata.fileTraceClient ***
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "2mb", "Request Payload too large",
				null, true, true);
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_small_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_small_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_50kb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_50kb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_2mb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_2mb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_small_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_small_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_50kb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_50kb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceClient_2mb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceClient_2mb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceClient", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	
	
	
	
	
	// *** sendRegistrazioneDisabilitata.fileTraceServer ***
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "2mb", "Request Payload too large",
				null, true, true);
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_small_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_small_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_50kb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_50kb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_2mb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_2mb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_small_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_small_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_50kb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_50kb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void erogazione_registrazioneDisabilitata_fileTraceServer_2mb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_registrazioneDisabilitata_fileTraceServer_2mb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata.fileTraceServer", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	
	
	
	
	// *** filtro (gruppo filtro) ***
	@Test
	public void erogazione_filtro_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"filtro", "small", null,
				null, false, false);
	}
	@Test
	public void fruizione_filtro_small() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"filtro", "small", null,
				null, false, false);
	}
	@Test
	public void erogazione_filtro_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"filtro", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_filtro_50kb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"filtro", "50kb", null,
				null, false, false);
	}
	@Test
	public void erogazione_filtro_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void fruizione_filtro_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro", "2mb", "Request Payload too large",
				null, true, true);
	}
	@Test
	public void erogazione_filtro2_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro2", "50kb", null,
				null, false, false);
	}
	@Test
	public void fruizione_filtro2_2mb() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro2", "50kb", null,
				null, false, false);
	}
	
	@Test
	public void erogazione_filtro_small_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"filtro", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void fruizione_filtro_small_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"filtro", "small", null,
				"Richiesta", false, false);
	}
	@Test
	public void erogazione_filtro_50kb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"filtro", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void fruizione_filtro_50kb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"filtro", "50kb", "Request Payload too large",
				"Richiesta", true, true);
	}
	@Test
	public void erogazione_filtro_2mb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_filtro_2mb_requestPolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro", "2mb", "Request Payload too large",
				"Richiesta", true,
				false); // gia verificato eventi per 50kb
	}
	
	@Test
	public void erogazione_filtro_small_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"filtro", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void fruizione_filtro_small_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"filtro", "small", null,
				"Risposta", false, false);
	}
	@Test
	public void erogazione_filtro_50kb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"filtro", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void fruizione_filtro_50kb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"filtro", "50kb", "Response Payload too large",
				"Risposta", true, true);
	}
	@Test
	public void erogazione_filtro_2mb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	@Test
	public void fruizione_filtro_2mb_responsePolicy() throws Exception {
		SoapUtilities.test(chunkedMode, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.BIG_SIZE).getBytes(),
				"filtro", "2mb", "Response Payload too large",
				"Risposta", true,
				false); // gia verificato eventi per 50kb
	}
	

}