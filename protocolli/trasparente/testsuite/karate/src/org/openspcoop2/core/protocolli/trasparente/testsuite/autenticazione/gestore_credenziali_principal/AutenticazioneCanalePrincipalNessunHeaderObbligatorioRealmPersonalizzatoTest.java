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
package org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali_principal;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali.Utilities;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpRequest;

/**
* AutenticazioneCanaleBasicHeaderAtLeastOneTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AutenticazioneCanalePrincipalNessunHeaderObbligatorioRealmPersonalizzatoTest extends ConfigLoader {

	// autenticazionePrincipal [EROGAZIONE]

	@Test
	public void erogazione_autenticazionePrincipal() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("esempioFruitoreTrasparentePrincipal");
		request.setPassword("Op3nSPC@@p2");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-Principal", "ApplicativoSoggettoInternoTestFruitore1");
		Utilities._test(request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"principal",
				headers,
				"SoggettoInternoTestFruitore",
				"ApplicativoSoggettoInternoTestFruitore1Principal",
				"@@GatewayCredenziali@@WebServerErogazioniSoggettoAuthPrincipal@@Principal 'ApplicativoSoggettoInternoTestFruitore1'",
				null,
				null,
				"Ottenute credenziali di accesso ( Principal 'ApplicativoSoggettoInternoTestFruitore1' ) fornite da WebServerErogazioniSoggettoAuthPrincipal");
	}
	
	@Test
	public void erogazione_autenticazionePrincipal_credenzialiNonFornite() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-Principal", "ApplicativoSoggettoInternoTestFruitore1");
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"principal",
				headers,
				null,
				null,
				null,
				"ProxyAuthErogazioni realm=\"GovWayErogazioni\", error=\"invalid_request\", error_description=\"The request is missing a required principal credentials\"",
				Utilities.CREDENZIALI_PROXY_NON_FORNITE,
				"Autenticazione principal del Gestore delle Credenziali 'WebServerErogazioniSoggettoAuthPrincipal' fallita, nessun tipo di credenziale principal riscontrata nel trasporto");
	}
	
	@Test
	public void erogazione_autenticazionePrincipal_credenzialiNonCorrette() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("esempioFruitoreTrasparentePrincipal2");
		request.setPassword("Op3nSPC@@p2");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-Principal", "ApplicativoSoggettoInternoTestFruitore1");
		Utilities._test(request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"principal",
				headers,
				null,
				null,
				"BasicUsername/Principal 'esempioFruitoreTrasparentePrincipal2'",
				"ProxyAuthErogazioni realm=\"GovWayErogazioni\", error=\"invalid_request\", error_description=\"Invalid principal credentials\"",
				Utilities.CREDENZIALI_PROXY_NON_CORRETTE,
				"Autenticazione principal del Gestore delle Credenziali 'WebServerErogazioniSoggettoAuthPrincipal' fallita, credenziali presenti nel trasporto ( BasicUsername/Principal 'esempioFruitoreTrasparentePrincipal2' )");
	}
	
	@Test
	public void erogazione_autenticazionePrincipal_credenzialiHttpsValide() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("esempioFruitoreTrasparentePrincipal");
		request.setPassword("Op3nSPC@@p2");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-SSL-Subject", "l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM");
		headers.put("X-Erogazione-SSL-Issuer", "/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/");
		Utilities._test(request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"https",
				headers,
				"SoggettoInternoTestFruitore",
				"HSMClient1",
				"@@GatewayCredenziali@@WebServerErogazioniSoggettoAuthPrincipal@@SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM'\nSSL-Issuer '/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/'",
				null,
				null,
				"Ottenute credenziali di accesso ( SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM' ) fornite da WebServerErogazioniSoggettoAuthPrincipal");
	}
	
	@Test
	public void erogazione_autenticazionePrincipal_credenzialiBasicValide() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("esempioFruitoreTrasparentePrincipal");
		request.setPassword("Op3nSPC@@p2");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-BASIC-Username", "ApplicativoSoggettoInternoTestFruitore1");
		headers.put("X-Erogazione-BASIC-Password", "123456");
		Utilities._test(request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"basic",
				headers,
				"SoggettoInternoTestFruitore",
				"ApplicativoSoggettoInternoTestFruitore1",
				"@@GatewayCredenziali@@WebServerErogazioniSoggettoAuthPrincipal@@BasicUsername 'ApplicativoSoggettoInternoTestFruitore1'",
				null,
				null,
				"Ottenute credenziali di accesso ( BasicUsername 'ApplicativoSoggettoInternoTestFruitore1' ) fornite da WebServerErogazioniSoggettoAuthPrincipal");
	}
	
	@Test
	public void erogazione_autenticazionePrincipal_public() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("esempioFruitoreTrasparentePrincipal");
		request.setPassword("Op3nSPC@@p2");
		
		Map<String, String> headers = new HashMap<>();
		Utilities._test(request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"public",
				headers,
				null,
				null,
				"BasicUsername/Principal 'esempioFruitoreTrasparentePrincipal'",
				null,
				null);
	}
	
	
	
	// autenticazionePrincipal [FRUIZIONE]

	@Test
	public void fruizione_autenticazionePrincipal() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("esempioFruitoreTrasparentePrincipal");
		request.setPassword("Op3nSPC@@p2");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-Principal", "ApplicativoSoggettoInternoTestGestoreCredenzialiPrincipal");
		Utilities._test(request,
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"principal",
				headers,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"ApplicativoSoggettoInternoTestGestoreCredenzialiPrincipal",
				"@@GatewayCredenziali@@WebServerFruizioniSoggettoAuthPrincipal@@Principal 'ApplicativoSoggettoInternoTestGestoreCredenzialiPrincipal'",
				null,
				null,
				"Ottenute credenziali di accesso ( Principal 'ApplicativoSoggettoInternoTestGestoreCredenzialiPrincipal' ) fornite da WebServerFruizioniSoggettoAuthPrincipal");
	}
	
	@Test
	public void fruizione_autenticazionePrincipal_credenzialiNonFornite() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-Principal", "ApplicativoSoggettoInternoTestGestoreCredenzialiPrincipal");
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"principal",
				headers,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				null,
				null,
				"ProxyAuthFruizioni realm=\"GovWayFruizioni\", error=\"invalid_request\", error_description=\"The request is missing a required principal credentials\"",
				Utilities.CREDENZIALI_PROXY_NON_FORNITE,
				"Autenticazione principal del Gestore delle Credenziali 'WebServerFruizioniSoggettoAuthPrincipal' fallita, nessun tipo di credenziale principal riscontrata nel trasporto");
	}
	
	@Test
	public void fruizione_autenticazionePrincipal_credenzialiNonCorrette() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("esempioFruitoreTrasparentePrincipal2");
		request.setPassword("Op3nSPC@@p2");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-Principal", "ApplicativoSoggettoInternoTestGestoreCredenzialiPrincipal");
		Utilities._test(request,
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"principal",
				headers,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				null,
				"BasicUsername/Principal 'esempioFruitoreTrasparentePrincipal2'",
				"ProxyAuthFruizioni realm=\"GovWayFruizioni\", error=\"invalid_request\", error_description=\"Invalid principal credentials\"",
				Utilities.CREDENZIALI_PROXY_NON_CORRETTE,
				"Autenticazione principal del Gestore delle Credenziali 'WebServerFruizioniSoggettoAuthPrincipal' fallita, credenziali presenti nel trasporto ( BasicUsername/Principal 'esempioFruitoreTrasparentePrincipal2' )");
	}
	
	@Test
	public void fruizione_autenticazionePrincipal_credenzialiHttpsValide() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("esempioFruitoreTrasparentePrincipal");
		request.setPassword("Op3nSPC@@p2");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-SSL-Subject", "/cn=ApplicativoSoggettoInternoTestGestoreCredenzialiPrincipal/");
		Utilities._test(request,
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"https",
				headers,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"ApplicativoSoggettoInternoTestGestoreCredenzialiPrincipal_ssl",
				"@@GatewayCredenziali@@WebServerFruizioniSoggettoAuthPrincipal@@SSL-Subject '/cn=ApplicativoSoggettoInternoTestGestoreCredenzialiPrincipal/'",
				null,
				null,
				"Ottenute credenziali di accesso ( SSL-Subject '/cn=ApplicativoSoggettoInternoTestGestoreCredenzialiPrincipal/' ) fornite da WebServerFruizioniSoggettoAuthPrincipal");
	}
	
	@Test
	public void fruizione_autenticazionePrincipal_credenzialiBasicValide() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("esempioFruitoreTrasparentePrincipal");
		request.setPassword("Op3nSPC@@p2");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-BASIC-Username", "ApplicativoSoggettoInternoTestGestoreCredenzialiPrincipal");
		headers.put("X-Fruizione-BASIC-Password", "123456");
		Utilities._test(request,
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"basic",
				headers,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"ApplicativoSoggettoInternoTestGestoreCredenzialiPrincipal_basic",
				"@@GatewayCredenziali@@WebServerFruizioniSoggettoAuthPrincipal@@BasicUsername 'ApplicativoSoggettoInternoTestGestoreCredenzialiPrincipal'",
				null,
				null,
				"Ottenute credenziali di accesso ( BasicUsername 'ApplicativoSoggettoInternoTestGestoreCredenzialiPrincipal' ) fornite da WebServerFruizioniSoggettoAuthPrincipal");
	}
	
	@Test
	public void fruizione_autenticazionePrincipal_public() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setUsername("esempioFruitoreTrasparentePrincipal");
		request.setPassword("Op3nSPC@@p2");
		
		Map<String, String> headers = new HashMap<>();
		Utilities._test(request,
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				"public",
				headers,
				"SoggettoInternoTestGestoreCredenzialePrincipal",
				null,
				"BasicUsername/Principal 'esempioFruitoreTrasparentePrincipal'",
				null,
				null);
	}
}
