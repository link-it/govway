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
package org.openspcoop2.core.protocolli.trasparente.testsuite.other.api_grandi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

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
	public void erogazione_options() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpRequestMethod.OPTIONS, "test10/dictio/altri/messaggi/valid");
	}
	@Test
	public void fruizione_options() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpRequestMethod.OPTIONS, "test10/dictio/altri/messaggi/valid");
	}
	
	@Test
	public void erogazione_get() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpRequestMethod.GET, "test10/dictio/altri/messaggi/valid");
	}
	@Test
	public void fruizione_get() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpRequestMethod.GET, "test10/dictio/altri/messaggi/valid");
	}

	private static void _test(TipoServizio tipoServizio, HttpRequestMethod metodo, String operazione) throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/TestAPIRestGrande/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestAPIRestGrande/v1/"+operazione;
		
		
		HttpRequest request = new HttpRequest();

		request.setMethod(metodo);
		
		request.setUrl(url);
		
		int tempoMax = 2000; // su oracle impiega piu' tempo
		if(Utils.isJenkins()) {
			tempoMax = 4000; 
		}
		
		var responses = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeParallelRequests(request, 1); // inizializzo la cache
		for (var resp : responses) {
			assertEquals(200, resp.getResultHTTPOperation());
			
			String idTransazione = resp.getHeaderFirstValue("GovWay-Transaction-ID");
			assertNotNull(idTransazione);
			
			DBVerifier.verify(idTransazione, tempoMax);
		}
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetAllCache(logCore);
		
		if(Utils.isJenkins()) {
			tempoMax = 6000; // thread in parallelo 
		}
		
		responses = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeParallelRequests(request, 10); // inizializzo la cache rispetto a più thread in parallelo
		for (var resp : responses) {
			assertEquals(200, resp.getResultHTTPOperation());
			
			String idTransazione = resp.getHeaderFirstValue("GovWay-Transaction-ID");
			assertNotNull(idTransazione);
			
			DBVerifier.verify(idTransazione, tempoMax);
		}
		
		tempoMax = 500;
		if(Utils.isJenkins()) {
			tempoMax = 4000; 
		}
		
		responses = org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.makeParallelRequests(request, 10);
		for (var resp : responses) {
			assertEquals(200, resp.getResultHTTPOperation());
			
			String idTransazione = resp.getHeaderFirstValue("GovWay-Transaction-ID");
			assertNotNull(idTransazione);
			
			DBVerifier.verify(idTransazione, tempoMax);
				
		}	

		
	}
}
