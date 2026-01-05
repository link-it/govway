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
package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.dimensione_messaggi;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* RestInvalidHeaderTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestCheckHeaderContenLengthDisabledTest extends ConfigLoader {

	public static final String ID_API_CONFIG = "DimensioneMassimaMessaggiRESTConfigProps";
	
	@Test
	public void erogazione_checkHeaderDisabled() throws Exception {
		RestUtilities.test(false, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(1024*1024*10).getBytes(),
				"globale", "2mb", "Request Payload too large", //"Request Content-Length exceeds the allowed limit",   non viene rilevato content length
				null,
				null, true, true,
				ID_API_CONFIG);
	}
	@Test
	public void fruizione_checkHeaderDisabled() throws Exception {
		RestUtilities.test(false, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(1024*1024*10).getBytes(),
				"globale", "2mb", "Request Payload too large", //"Request Content-Length exceeds the allowed limit",   non viene rilevato content length
				null,
				null, true, true,
				ID_API_CONFIG);
	}
	
	
	@Test
	public void erogazione_registrazioneDisabilitata_2mb() throws Exception {
		RestUtilities.test(false, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "2mb", "Request Payload too large", //"Request Content-Length exceeds the allowed limit",   non viene rilevato content length
				null,
				null, true, true,
				ID_API_CONFIG);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_2mb() throws Exception {
		RestUtilities.test(false, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.BIG_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "2mb", "Request Payload too large", //"Request Content-Length exceeds the allowed limit",   non viene rilevato content length
				null,
				null, true, true,
				ID_API_CONFIG);
	}
	
	
	@Test
	public void erogazione_registrazioneDisabilitata_50kb_responsePolicy() throws Exception {
		RestUtilities.test(false, TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", "Response Payload too large", //"Response Content-Length exceeds the allowed limit",   non viene rilevato content length
				null,
				"Risposta", true, true,
				ID_API_CONFIG);
	}
	@Test
	public void fruizione_registrazioneDisabilitata_50kb_responsePolicy() throws Exception {
		RestUtilities.test(false, TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitata", "50kb", "Response Payload too large", //"Response Content-Length exceeds the allowed limit",   non viene rilevato content length
				null,
				"Risposta", true, true,
				ID_API_CONFIG);
	}
}
