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
package org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_esterni;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali.CredenzialeTrasporto;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpRequest;

/**
* AutenticazioneTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AutenticazioneTest extends ConfigLoader {

	private static final String API = "TestApplicativiEsterni";
	
	@Test
	public void autenticazioneBasic() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("ApplicativoSoggettoEsternoTestFruitoreBasic");
		request.setPassword("123456");
		
		Map<String, String> headers = new HashMap<>(); 
		org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali.Utilities._test(API,
				request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"basic",
				headers,
				"SoggettoEsternoTest",
				"ApplicativoSoggettoEsternoTestFruitoreBasic",
				new CredenzialeTrasporto(
						"BasicUsername 'ApplicativoSoggettoEsternoTestFruitoreBasic'", 
						"basic",
						"ApplicativoSoggettoEsternoTestFruitoreBasic"),
				null,
				null);
	}
	
	@Test
	public void autenticazionePrincipal() throws Exception {
		
		HttpRequest request = new HttpRequest();
		
		Map<String, String> headers = new HashMap<>();
		headers.put("TEST-PRINCIPAL", "ApplicativoSoggettoEsternoTestFruitorePrincipal");
		org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali.Utilities._test(API,
				request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"principal",
				headers,
				"SoggettoEsternoTest",
				"ApplicativoSoggettoEsternoTestFruitorePrincipal",
				new CredenzialeTrasporto(
					"Principal (http) 'TEST-PRINCIPAL: ApplicativoSoggettoEsternoTestFruitorePrincipal'", 
					"principal",
					"ApplicativoSoggettoEsternoTestFruitorePrincipal"),
				null,
				null);
	}
	
	@Test
	public void autenticazioneHttps() throws Exception {
		
		HttpRequest request = new HttpRequest();
		
		request.setTrustAllCerts(true);
		request.setKeyStorePath(getGovwayCfgKeys() + "/applicativo1_dominio_esterno.jks");
		request.setKeyStorePassword("openspcoopjks");
		request.setKeyPassword("openspcoop");
		
		Map<String, String> headers = new HashMap<>();
		org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali.Utilities._test(API,
				request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"https",
				headers,
				"SoggettoEsternoTest",
				"ApplicativoSoggettoEsternoTestFruitoreHttps",
				new CredenzialeTrasporto(
					"SSL-Subject 'CN=applicativoDominioEsterno, OU=DominioEsterno, L=Pisa, ST=Italy, C=IT'\nSSL-Issuer 'CN=applicativoDominioEsterno, OU=DominioEsterno, L=Pisa, ST=Italy, C=IT'\nSSL-ClientCert-SerialNumber '1657887859'",
					"ssl",
					"/st=Italy/c=IT/ou=DominioEsterno/cn=applicativoDominioEsterno/l=Pisa/"),
				null,
				null);
	}
	
	@Test
	public void autenticazioneApiKey() throws Exception {
		
		HttpRequest request = new HttpRequest();
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-API-Key", "QXBwbGljYXRpdm9Tb2dnZXR0b0VzdGVybm9UZXN0RnJ1aXRvcmVBcGlLZXlAREFFTElNSU5BUkUuZ3c=.WW44N2NBOjhCdTEwVE5BcDY4ZmNhVzMwelF6ZEgzUXg=");
		org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali.Utilities._test(API,
				request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"api-key",
				headers,
				"SoggettoEsternoTest",
				"ApplicativoSoggettoEsternoTestFruitoreApiKey",
				new CredenzialeTrasporto(
						"ApiKey (http) 'X-API-KEY'",
						"apikey",
						"ApplicativoSoggettoEsternoTestFruitoreApiKey@DAELIMINARE.gw"),
				null,
				null);
	}
	
	@Test
	public void autenticazioneApiKeyAppId() throws Exception {
		
		HttpRequest request = new HttpRequest();
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-API-Key", "UFZyM3RFaz5jRzdhNHUzY1p1WnpWOTY0Q2NVWDE4ZDM=");
		headers.put("X-APP-ID", "ApplicativoSoggettoEsternoTestFruitoreApiKeyAppID@DAELIMINARE.gw");
		org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali.Utilities._test(API,
				request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"api-key_app-id",
				headers,
				"SoggettoEsternoTest",
				"ApplicativoSoggettoEsternoTestFruitoreApiKeyAppID",
				new CredenzialeTrasporto(
						"ApiKey (http) 'X-API-KEY'\nAppId (http) 'X-APP-ID: ApplicativoSoggettoEsternoTestFruitoreApiKeyAppID@DAELIMINARE.gw'",
						"apikey",
						"ApplicativoSoggettoEsternoTestFruitoreApiKeyAppID@DAELIMINARE.gw"),
				null,
				null);
	}
	
}
