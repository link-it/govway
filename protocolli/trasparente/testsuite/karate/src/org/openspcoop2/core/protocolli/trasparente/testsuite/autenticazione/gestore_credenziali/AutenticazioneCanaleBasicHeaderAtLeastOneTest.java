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
package org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpRequest;

/**
* AutenticazioneCanaleBasicHeaderAtLeastOneTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AutenticazioneCanaleBasicHeaderAtLeastOneTest extends ConfigLoader {

	// autenticazioneBasic [EROGAZIONE]

	@Test
	public void erogazione_autenticazioneBasic() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("WebServerErogazioniSoggettoAuthBasic");
		request.setPassword("123456");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-BASIC-Username", "ApplicativoSoggettoInternoTestFruitore1");
		headers.put("X-Erogazione-BASIC-Password", "123456");
		Utilities._test(request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"basic",
				headers,
				"SoggettoInternoTestFruitore",
				"ApplicativoSoggettoInternoTestFruitore1",
				new CredenzialeTrasporto(
						"@@GatewayCredenziali@@WebServerErogazioniSoggettoAuthBasic@@BasicUsername 'ApplicativoSoggettoInternoTestFruitore1'",
						"basic",
						"ApplicativoSoggettoInternoTestFruitore1"),
				null,
				null,
				"Ottenute credenziali di accesso ( BasicUsername 'ApplicativoSoggettoInternoTestFruitore1' ) fornite da WebServerErogazioniSoggettoAuthBasic");
	}
	
	@Test
	public void erogazione_autenticazioneBasic_credenzialiNonFornite() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-BASIC-Username", "ApplicativoSoggettoInternoTestFruitore1");
		headers.put("X-Erogazione-BASIC-Password", "123456");
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"basic",
				headers,
				null,
				null,
				null,
				"ProxyAuth realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required http-basic credentials\"",
				Utilities.CREDENZIALI_PROXY_NON_FORNITE,
				"Autenticazione basic del Gestore delle Credenziali 'WebServerErogazioniSoggettoAuthBasic' fallita, nessun tipo di credenziali basic riscontrata nel trasporto");
	}

	@Test
	public void erogazione_autenticazioneBasic_credenzialiNonCorrette() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("WebServerErogazioniSoggettoAuthBasicErrate");
		request.setPassword("123456");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-BASIC-Username", "ApplicativoSoggettoInternoTestFruitore1");
		headers.put("X-Erogazione-BASIC-Password", "123456");
		Utilities._test(request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"basic",
				headers,
				null,
				null,
				new CredenzialeTrasporto(
						"BasicUsername 'WebServerErogazioniSoggettoAuthBasicErrate'"),
				"ProxyAuth realm=\"GovWay\", error=\"invalid_request\", error_description=\"Invalid credentials\"",
				Utilities.CREDENZIALI_PROXY_NON_CORRETTE,
				"Autenticazione basic del Gestore delle Credenziali 'WebServerErogazioniSoggettoAuthBasic' fallita, credenziali presenti nel trasporto ( BasicUsername 'WebServerErogazioniSoggettoAuthBasicErrate' )");
	}
	
	@Test
	public void erogazione_autenticazioneBasic_credenzialiHttpsValide_permesseAtLeastOne() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("WebServerErogazioniSoggettoAuthBasic");
		request.setPassword("123456");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-SSL-Subject", "l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM");
		headers.put("X-Erogazione-SSL-Issuer", "/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/");
		Utilities._test(request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"https",
				headers,
				"SoggettoInternoTestFruitore",
				"HSMClient1",
				new CredenzialeTrasporto(
						"@@GatewayCredenziali@@WebServerErogazioniSoggettoAuthBasic@@SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM'\nSSL-Issuer '/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/'",
						"ssl",
						"/st=Italy/c=IT/ou=Test/cn=ExampleClient1HSM/l=Pisa/o=Test/"),
				null,
				null,
				"Ottenute credenziali di accesso ( SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM' ) fornite da WebServerErogazioniSoggettoAuthBasic");
	}

	@Test
	public void erogazione_autenticazioneHttps_credenzialiPrincipalValide_permesseAtLeastOne() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("WebServerErogazioniSoggettoAuthBasic");
		request.setPassword("123456");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-Principal", "ApplicativoSoggettoInternoTestFruitore1");
		Utilities._test(request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"principal",
				headers,
				"SoggettoInternoTestFruitore",
				"ApplicativoSoggettoInternoTestFruitore1Principal",
				new CredenzialeTrasporto(
						"@@GatewayCredenziali@@WebServerErogazioniSoggettoAuthBasic@@Principal 'ApplicativoSoggettoInternoTestFruitore1'",
						"principal",
						"ApplicativoSoggettoInternoTestFruitore1"),
				null,
				null,
				"Ottenute credenziali di accesso ( Principal 'ApplicativoSoggettoInternoTestFruitore1' ) fornite da WebServerErogazioniSoggettoAuthBasic");
	}

	@Test
	public void erogazione_autenticazioneHttps_erogazione_publicNonPermessa() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("WebServerErogazioniSoggettoAuthBasic");
		request.setPassword("123456");
		
		Map<String, String> headers = new HashMap<>();
		Utilities._test(request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"public",
				headers,
				null,
				null,
				new CredenzialeTrasporto(
						"BasicUsername 'WebServerErogazioniSoggettoAuthBasic'"),
				"ProxyAuth realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required credentials\"",
				Utilities.CREDENZIALI_PROXY_FORNITE_NON_CONFORMI,
				"Non sono presenti Header HTTP che veicolano credenziali (header non rilevati: X-Erogazione-BASIC-Username,X-Erogazione-SSL-Subject,X-Erogazione-SSL-Cert,X-Erogazione-Principal)");
	}

	
	
	
	// autenticazioneBasic [FRUIZIONE]

	@Test
	public void fruizione_autenticazioneBasic() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("WebServerFruizioniSoggettoAuthBasic");
		request.setPassword("123456");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-BASIC-Username", "ApplicativoSoggettoInternoTestGestoreCredenzialiBasic");
		headers.put("X-Fruizione-BASIC-Password", "123456");
		Utilities._test(request,
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"basic",
				headers,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"ApplicativoSoggettoInternoTestGestoreCredenzialiBasic",
				new CredenzialeTrasporto(
						"@@GatewayCredenziali@@WebServerFruizioniSoggettoAuthBasic@@BasicUsername 'ApplicativoSoggettoInternoTestGestoreCredenzialiBasic'",
						"basic",
						"ApplicativoSoggettoInternoTestGestoreCredenzialiBasic"),
				null,
				null,
				"Ottenute credenziali di accesso ( BasicUsername 'ApplicativoSoggettoInternoTestGestoreCredenzialiBasic' ) fornite da WebServerFruizioniSoggettoAuthBasic");
	}
	
	@Test
	public void fruizione_autenticazioneBasic_credenzialiNonFornite() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-BASIC-Username", "ApplicativoSoggettoInternoTestGestoreCredenzialiBasic");
		headers.put("X-Fruizione-BASIC-Password", "123456");
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"basic",
				headers,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				null,
				null,
				"ProxyAuth realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required http-basic credentials\"",
				Utilities.CREDENZIALI_PROXY_NON_FORNITE,
				"Autenticazione basic del Gestore delle Credenziali 'WebServerFruizioniSoggettoAuthBasic' fallita, nessun tipo di credenziali basic riscontrata nel trasporto");
	}

	@Test
	public void fruizione_autenticazioneBasic_credenzialiNonCorrette() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("WebServerFruizioniSoggettoAuthBasicErrate");
		request.setPassword("123456");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-BASIC-Username", "ApplicativoSoggettoInternoTestGestoreCredenzialiBasic");
		headers.put("X-Fruizione-BASIC-Password", "123456");
		Utilities._test(request,
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"basic",
				headers,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				null,
				new CredenzialeTrasporto(
						"BasicUsername 'WebServerFruizioniSoggettoAuthBasicErrate'"),
				"ProxyAuth realm=\"GovWay\", error=\"invalid_request\", error_description=\"Invalid credentials\"",
				Utilities.CREDENZIALI_PROXY_NON_CORRETTE,
				"Autenticazione basic del Gestore delle Credenziali 'WebServerFruizioniSoggettoAuthBasic' fallita, credenziali presenti nel trasporto ( BasicUsername 'WebServerFruizioniSoggettoAuthBasicErrate' )");
	}
	
	@Test
	public void fruizione_autenticazioneBasic_credenzialiHttpsValide_permesseAtLeastOne() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("WebServerFruizioniSoggettoAuthBasic");
		request.setPassword("123456");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-SSL-Subject", "/cn=ApplicativoSoggettoInternoTestGestoreCredenzialiBasic/");
		Utilities._test(request,
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"https",
				headers,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"ApplicativoSoggettoInternoTestGestoreCredenzialiBasic_ssl",
				new CredenzialeTrasporto(
						"@@GatewayCredenziali@@WebServerFruizioniSoggettoAuthBasic@@SSL-Subject '/cn=ApplicativoSoggettoInternoTestGestoreCredenzialiBasic/'",
						"ssl",
						"/cn=ApplicativoSoggettoInternoTestGestoreCredenzialiBasic/"),
				null,
				null,
				"Ottenute credenziali di accesso ( SSL-Subject '/cn=ApplicativoSoggettoInternoTestGestoreCredenzialiBasic/' ) fornite da WebServerFruizioniSoggettoAuthBasic");
	}
		
	@Test
	public void fruizione_autenticazioneHttps_credenzialiPrincipalValide_permesseAtLeastOne() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("WebServerFruizioniSoggettoAuthBasic");
		request.setPassword("123456");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-Principal", "ApplicativoSoggettoInternoTestGestoreCredenzialiBasic");
		Utilities._test(request,
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"principal",
				headers,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"ApplicativoSoggettoInternoTestGestoreCredenzialiBasic_principal",
				new CredenzialeTrasporto(
						"@@GatewayCredenziali@@WebServerFruizioniSoggettoAuthBasic@@Principal 'ApplicativoSoggettoInternoTestGestoreCredenzialiBasic'",
						"principal",
						"ApplicativoSoggettoInternoTestGestoreCredenzialiBasic"),
				null,
				null,
				"Ottenute credenziali di accesso ( Principal 'ApplicativoSoggettoInternoTestGestoreCredenzialiBasic' ) fornite da WebServerFruizioniSoggettoAuthBasic");
	}

	@Test
	public void fruizione_autenticazioneHttps_fruizione_publicNonPermessa() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("WebServerFruizioniSoggettoAuthBasic");
		request.setPassword("123456");
		
		Map<String, String> headers = new HashMap<>();
		Utilities._test(request,
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				"public",
				headers,
				"SoggettoInternoTestGestoreCredenzialeBasic",
				null,
				new CredenzialeTrasporto(
						"BasicUsername 'WebServerFruizioniSoggettoAuthBasic'"),
				"ProxyAuth realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required credentials\"",
				Utilities.CREDENZIALI_PROXY_FORNITE_NON_CONFORMI,
				"Non sono presenti Header HTTP che veicolano credenziali (header non rilevati: X-Fruizione-BASIC-Username,X-Fruizione-SSL-Subject,X-Fruizione-SSL-Cert,X-Fruizione-Principal)");
	}
	
}
