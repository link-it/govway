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
package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.autenticazione;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {


	@Test
	public void erogazione_secret() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		HttpResponse response = Utilities._test(MessageType.JSON,
				TipoServizio.EROGAZIONE,
				"secret",
				headers);
		
		Utilities.verifyResponseSecretHeader(response);
	}
	

	@Test
	public void erogazione_id_secret() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		HttpResponse response = Utilities._test(MessageType.JSON,
				TipoServizio.EROGAZIONE,
				"id_secret",
				headers);
		
		Utilities.verifyResponseSecretHeader(response);
		Utilities.verifyResponseIdHeader(response);
	}
	
	
	
	@Test
	public void fruizione_secret() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		HttpResponse response = Utilities._test(MessageType.JSON,
				TipoServizio.FRUIZIONE,
				"secret",
				headers);
		
		Utilities.verifyResponseSecretHeader(response);
	}
	

	@Test
	public void fruizione_id_secret() throws Exception {
		
		Map<String, String> headers = new HashMap<String, String>();
		HttpResponse response = Utilities._test(MessageType.JSON,
				TipoServizio.FRUIZIONE,
				"id_secret",
				headers);
		
		Utilities.verifyResponseSecretHeader(response);
		Utilities.verifyResponseIdHeader(response);
	}
	
}
