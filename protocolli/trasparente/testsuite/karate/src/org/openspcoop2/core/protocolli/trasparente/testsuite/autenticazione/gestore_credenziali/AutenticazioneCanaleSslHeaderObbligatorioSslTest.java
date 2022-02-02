/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
* AutenticazioneCanaleSslHeaderObbligatorioSslTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AutenticazioneCanaleSslHeaderObbligatorioSslTest extends ConfigLoader {

	// autenticazioneHttps [EROGAZIONE]

	@Test
	public void erogazione_autenticazioneHttps() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setTrustAllCerts(true);
		request.setKeyStorePath("/etc/govway/keys/soggetto2.jks");
		request.setKeyStoreType("JKS");
		request.setKeyStorePassword("openspcoopjks");
		request.setKeyAlias("soggetto2");
		request.setKeyPassword("openspcoop");
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Erogazione-SSL-Subject", "l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM");
		headers.put("X-Erogazione-SSL-Issuer", "/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/");
		Utilities._test(request, 
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				"https",
				headers,
				"SoggettoInternoTestFruitore",
				"HSMClient1",
				"@@GatewayCredenziali@@WebServerErogazioniSoggettoAuthSsl@@SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM'\nSSL-Issuer '/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/'",
				null,
				null,
				"Ottenute credenziali di accesso ( SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM' ) fornite da WebServerErogazioniSoggettoAuthSsl");
	}

	@Test
	public void erogazione_autenticazioneHttps_credenzialiNonFornite() throws Exception {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Erogazione-SSL-Subject", "l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM");
		headers.put("X-Erogazione-SSL-Issuer", "/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/");
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				"https",
				headers,
				null,
				null,
				null,
				"ProxyAuth realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required client certificate\"",
				Utilities.CREDENZIALI_PROXY_NON_FORNITE,
				"Autenticazione ssl del Gestore delle Credenziali 'WebServerErogazioniSoggettoAuthSsl' fallita, nessun tipo di credenziali ssl riscontrata nel trasporto");
	}
	
	@Test
	public void erogazione_autenticazioneHttps_credenzialiNonCorrette() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setTrustAllCerts(true);
		request.setKeyStorePath("/etc/govway/keys/soggetto1.jks");
		request.setKeyStoreType("JKS");
		request.setKeyStorePassword("openspcoopjks");
		request.setKeyAlias("soggetto1");
		request.setKeyPassword("openspcoop");
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Erogazione-SSL-Subject", "l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM");
		headers.put("X-Erogazione-SSL-Issuer", "/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/");
		Utilities._test(request,
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				"https",
				headers,
				null,
				null,
				"SSL-Subject 'CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-Issuer 'CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-ClientCert-SerialNumber '1634113758'",
				"ProxyAuth realm=\"GovWay\", error=\"invalid_request\", error_description=\"Invalid client certificate\"",
				Utilities.CREDENZIALI_PROXY_NON_CORRETTE,
				"Autenticazione ssl del Gestore delle Credenziali 'WebServerErogazioniSoggettoAuthSsl' fallita, credenziali presenti nel trasporto ( SSL-Subject 'CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it' )");
	}

	@Test
	public void erogazione_autenticazioneHttps_credenzialiBasicValide_nonPermesse() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setTrustAllCerts(true);
		request.setKeyStorePath("/etc/govway/keys/soggetto2.jks");
		request.setKeyStoreType("JKS");
		request.setKeyStorePassword("openspcoopjks");
		request.setKeyAlias("soggetto2");
		request.setKeyPassword("openspcoop");
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Erogazione-BASIC-Username", "ApplicativoSoggettoInternoTestFruitore1");
		headers.put("X-Erogazione-BASIC-Password", "123456");
		Utilities._test(request, 
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				"basic",
				headers,
				null,
				null,
				"SSL-Subject 'CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-Issuer 'CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-ClientCert-SerialNumber '1634114074'",
				"mTLS realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required client certificate\"",
				Utilities.CREDENZIALI_PROXY_FORNITE_NON_CONFORMI,
				"Header HTTP 'X-Erogazione-SSL-Subject' o 'X-Erogazione-SSL-Cert' non presente");
	}

	@Test
	public void erogazione_autenticazioneHttps_credenzialiPrincipalValide_nonPermesse() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setTrustAllCerts(true);
		request.setKeyStorePath("/etc/govway/keys/soggetto2.jks");
		request.setKeyStoreType("JKS");
		request.setKeyStorePassword("openspcoopjks");
		request.setKeyAlias("soggetto2");
		request.setKeyPassword("openspcoop");
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Erogazione-Principal", "ApplicativoSoggettoInternoTestFruitore1");
		Utilities._test(request, 
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				"principal",
				headers,
				null,
				null,
				"SSL-Subject 'CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-Issuer 'CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-ClientCert-SerialNumber '1634114074'",
				"mTLS realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required client certificate\"",
				Utilities.CREDENZIALI_PROXY_FORNITE_NON_CONFORMI,
				"Header HTTP 'X-Erogazione-SSL-Subject' o 'X-Erogazione-SSL-Cert' non presente");
	}

	@Test
	public void erogazione_autenticazioneHttps_credenzialiNegliHeaderNonFornite() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setTrustAllCerts(true);
		request.setKeyStorePath("/etc/govway/keys/soggetto2.jks");
		request.setKeyStoreType("JKS");
		request.setKeyStorePassword("openspcoopjks");
		request.setKeyAlias("soggetto2");
		request.setKeyPassword("openspcoop");
		
		Map<String, String> headers = new HashMap<String, String>();
		Utilities._test(request, 
				TipoServizio.EROGAZIONE,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				"public",
				headers,
				null,
				null,
				"SSL-Subject 'CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-Issuer 'CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-ClientCert-SerialNumber '1634114074'",
				"mTLS realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required client certificate\"",
				Utilities.CREDENZIALI_PROXY_FORNITE_NON_CONFORMI,
				"Header HTTP 'X-Erogazione-SSL-Subject' o 'X-Erogazione-SSL-Cert' non presente");
	}
	
	
	
	
	// autenticazioneHttps [FRUIZIONE]

	@Test
	public void fruizione_autenticazioneHttps() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setTrustAllCerts(true);
		request.setKeyStorePath("/etc/govway/keys/soggetto2.jks");
		request.setKeyStoreType("JKS");
		request.setKeyStorePassword("openspcoopjks");
		request.setKeyAlias("soggetto2");
		request.setKeyPassword("openspcoop");
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Fruizione-SSL-Subject", "/cn=ApplicativoSoggettoInternoTestGestoreCredenzialiSsl/");
		Utilities._test(request, 
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				"https",
				headers,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				"ApplicativoSoggettoInternoTestGestoreCredenzialiSsl",
				"@@GatewayCredenziali@@WebServerFruizioniSoggettoAuthSsl@@SSL-Subject '/cn=ApplicativoSoggettoInternoTestGestoreCredenzialiSsl/'",
				null,
				null,
				"Ottenute credenziali di accesso ( SSL-Subject '/cn=ApplicativoSoggettoInternoTestGestoreCredenzialiSsl/' ) fornite da WebServerFruizioniSoggettoAuthSsl");
	}

	@Test
	public void fruizione_autenticazioneHttps_credenzialiNonFornite() throws Exception {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Fruizione-SSL-Subject", "/cn=ApplicativoSoggettoInternoTestGestoreCredenzialiSsl/");
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				"https",
				headers,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				null,
				null,
				"ProxyAuth realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required client certificate\"",
				Utilities.CREDENZIALI_PROXY_NON_FORNITE,
				"Autenticazione ssl del Gestore delle Credenziali 'WebServerFruizioniSoggettoAuthSsl' fallita, nessun tipo di credenziali ssl riscontrata nel trasporto");
	}
	
	@Test
	public void fruizione_autenticazioneHttps_credenzialiNonCorrette() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setTrustAllCerts(true);
		request.setKeyStorePath("/etc/govway/keys/soggetto1.jks");
		request.setKeyStoreType("JKS");
		request.setKeyStorePassword("openspcoopjks");
		request.setKeyAlias("soggetto1");
		request.setKeyPassword("openspcoop");
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Fruizione-SSL-Subject", "/cn=ApplicativoSoggettoInternoTestGestoreCredenzialiSsl/");
		Utilities._test(request,
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				"https",
				headers,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				null,
				"SSL-Subject 'CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-Issuer 'CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-ClientCert-SerialNumber '1634113758'",
				"ProxyAuth realm=\"GovWay\", error=\"invalid_request\", error_description=\"Invalid client certificate\"",
				Utilities.CREDENZIALI_PROXY_NON_CORRETTE,
				"Autenticazione ssl del Gestore delle Credenziali 'WebServerFruizioniSoggettoAuthSsl' fallita, credenziali presenti nel trasporto ( SSL-Subject 'CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it' )");
	}

	@Test
	public void fruizione_autenticazioneHttps_credenzialiBasicValide_nonPermesse() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setTrustAllCerts(true);
		request.setKeyStorePath("/etc/govway/keys/soggetto2.jks");
		request.setKeyStoreType("JKS");
		request.setKeyStorePassword("openspcoopjks");
		request.setKeyAlias("soggetto2");
		request.setKeyPassword("openspcoop");
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Fruizione-BASIC-Username", "ApplicativoSoggettoInternoTestFruitore1");
		headers.put("X-Fruizione-BASIC-Password", "123456");
		Utilities._test(request, 
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				"basic",
				headers,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				null,
				"SSL-Subject 'CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-Issuer 'CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-ClientCert-SerialNumber '1634114074'",
				"mTLS realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required client certificate\"",
				Utilities.CREDENZIALI_PROXY_FORNITE_NON_CONFORMI,
				"Header HTTP 'X-Fruizione-SSL-Subject' o 'X-Fruizione-SSL-Cert' non presente");
	}

	@Test
	public void fruizione_autenticazioneHttps_credenzialiPrincipalValide_nonPermesse() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setTrustAllCerts(true);
		request.setKeyStorePath("/etc/govway/keys/soggetto2.jks");
		request.setKeyStoreType("JKS");
		request.setKeyStorePassword("openspcoopjks");
		request.setKeyAlias("soggetto2");
		request.setKeyPassword("openspcoop");
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Fruizione-Principal", "ApplicativoSoggettoInternoTestFruitore1");
		Utilities._test(request, 
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				"principal",
				headers,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				null,
				"SSL-Subject 'CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-Issuer 'CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-ClientCert-SerialNumber '1634114074'",
				"mTLS realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required client certificate\"",
				Utilities.CREDENZIALI_PROXY_FORNITE_NON_CONFORMI,
				"Header HTTP 'X-Fruizione-SSL-Subject' o 'X-Fruizione-SSL-Cert' non presente");
	}
	
	@Test
	public void fruizione_autenticazioneHttps_credenzialiNegliHeaderNonFornite() throws Exception {
		
		HttpRequest request = new HttpRequest();
		request.setTrustAllCerts(true);
		request.setKeyStorePath("/etc/govway/keys/soggetto2.jks");
		request.setKeyStoreType("JKS");
		request.setKeyStorePassword("openspcoopjks");
		request.setKeyAlias("soggetto2");
		request.setKeyPassword("openspcoop");
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Fruizione-Principal", "ApplicativoSoggettoInternoTestFruitore1");
		Utilities._test(request, 
				TipoServizio.FRUIZIONE,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				"public",
				headers,
				"SoggettoInternoTestGestoreCredenzialeSsl",
				null,
				"SSL-Subject 'CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-Issuer 'CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it'\nSSL-ClientCert-SerialNumber '1634114074'",
				"mTLS realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required client certificate\"",
				Utilities.CREDENZIALI_PROXY_FORNITE_NON_CONFORMI,
				"Header HTTP 'X-Fruizione-SSL-Subject' o 'X-Fruizione-SSL-Cert' non presente");
	}
	
}
