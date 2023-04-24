/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.json;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.message.constants.MessageType;

/**
* RestTest
*
 * @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {


	@Test
	public void erogazione_json_default() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getBase64IntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_11,
				TipoServizio.FRUIZIONE,
				"default",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void fruizione_json_default() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getBase64IntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"default",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	
	
	
	
	@Test
	public void erogazione_json_default_info_corrotta_richiesta() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getBase64IntegrazioneJsonCorrotto());
		
		Utilities._test(logCore, MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"default",
				headers,
				"Content in http header GovWay-Integration isn''t a json: Unexpected character (''}'' (code 125)): was expecting double-quote to start field name",
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void fruizione_json_default_info_corrotta_richiesta() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getBase64IntegrazioneJsonCorrotto());
		
		Utilities._test(logCore, MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"default",
				headers,
				"Content in http header GovWay-Integration isn''t a json: Unexpected character (''}'' (code 125)): was expecting double-quote to start field name",
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void erogazione_json_default_info_corrotta_risposta() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getBase64IntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"risposta_senza_json",
				headers,
				"Content in http header GovWay-Integration isn''t a json: Unexpected character (''}'' (code 125)): was expecting double-quote to start field name",
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void fruizione_json_default_info_corrotta_risposta() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getBase64IntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"risposta_senza_json",
				headers,
				"Content in http header GovWay-Integration isn''t a json: Unexpected character (''}'' (code 125)): was expecting double-quote to start field name",
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	
	
	
	
	@Test
	public void erogazione_json_default_info_required_richiesta() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		
		Utilities._test(logCore, MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"required_request",
				headers,
				"Required http header "+Utilities.DEFAULT_HTTP_HEADER+" not found",
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void fruizione_json_default_info_required_richiesta() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		
		Utilities._test(logCore, MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"required_request",
				headers,
				"Required http header "+Utilities.DEFAULT_HTTP_HEADER+" not found",
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void erogazione_json_default_info_required_risposta() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getBase64IntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"required_response",
				headers,
				"Required http header "+Utilities.DEFAULT_HTTP_HEADER+" not found",
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void fruizione_json_default_info_required_risposta() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getBase64IntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"required_response",
				headers,
				"Required http header "+Utilities.DEFAULT_HTTP_HEADER+" not found",
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	
	
	
	@Test
	public void erogazione_json_default_all_optional() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getBase64IntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"default_all_optional",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void fruizione_json_default_all_optional() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getBase64IntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"default_all_optional",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void erogazione_json_default_all_optional_senza_info() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		
		Utilities._test(logCore, MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"default_all_optional",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void fruizione_json_default_all_optional_senza_info() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		
		Utilities._test(logCore, MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"default_all_optional",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	

	
	@Test
	public void erogazione_json_header_hex() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.HTTP_HEADER_CUSTOM, Utilities.getHexIntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"header_hex",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void fruizione_json_header_hex() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.HTTP_HEADER_CUSTOM, Utilities.getHexIntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"header_hex",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	
	
	@Test
	public void erogazione_json_url_jwt() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.HTTP_HEADER_CUSTOM, Utilities.getJWTIntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"url_jwt",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void fruizione_json_url_jwt() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.HTTP_HEADER_CUSTOM, Utilities.getJWTIntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"url_jwt",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	
	
	@Test
	public void erogazione_json_plain() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getIntegrazioneJsonShort());
		
		Utilities._test(logCore, MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"plain",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void fruizione_json_plain() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getIntegrazioneJsonShort());
		
		Utilities._test(logCore, MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"plain",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	
	
	
	
	
	@Test
	public void erogazione_json_default_freemarker() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getBase64IntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_11,
				TipoServizio.EROGAZIONE,
				"default_freemarker",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void fruizione_json_default_freemarker() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getBase64IntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_12,
				TipoServizio.FRUIZIONE,
				"default_freemarker",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	
	
	
	
	
	@Test
	public void erogazione_json_default_velocity() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getBase64IntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_12,
				TipoServizio.EROGAZIONE,
				"default_velocity",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
	@Test
	public void fruizione_json_default_velocity() throws Exception {
		
		Map<String, String> headers = new HashMap<>();
		headers.put(Utilities.DEFAULT_HTTP_HEADER, Utilities.getBase64IntegrazioneJson());
		
		Utilities._test(logCore, MessageType.SOAP_11,
				TipoServizio.FRUIZIONE,
				"default_velocity",
				headers,
				null,
				Utilities.CORRELAZIONE_RICHIESTA, Utilities.CORRELAZIONE_RISPOSTA);
		
	}
}
