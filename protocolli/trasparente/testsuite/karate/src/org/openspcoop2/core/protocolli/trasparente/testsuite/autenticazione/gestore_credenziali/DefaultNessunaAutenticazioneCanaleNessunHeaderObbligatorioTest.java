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
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;

/**
* DefaultNessunaAutenticazioneCanaleNessunHeaderObbligatorioTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class DefaultNessunaAutenticazioneCanaleNessunHeaderObbligatorioTest extends ConfigLoader {


	// public [EROGAZIONE]

	@Test
	public void erogazione_public() throws Exception {
		Map<String, String> headers = new HashMap<>();
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"public",
				headers,
				null,
				null,
				null,
				null,
				null);
	}
	

	// autenticazioneBasic [EROGAZIONE]
	@Test
	public void erogazione_autenticazioneBasic() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-BASIC-Username", "ApplicativoSoggettoInternoTestFruitore1");
		headers.put("X-Erogazione-BASIC-Password", "123456");
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"basic",
				headers,
				"SoggettoInternoTestFruitore",
				"ApplicativoSoggettoInternoTestFruitore1",
				"@@GatewayCredenziali@@WebServerErogazioniDefault@@BasicUsername 'ApplicativoSoggettoInternoTestFruitore1'",
				null,
				null,
				"Ottenute credenziali di accesso ( BasicUsername 'ApplicativoSoggettoInternoTestFruitore1' ) fornite da WebServerErogazioniDefault");
	}
	@Test
	public void erogazione_autenticazioneBasic_errate() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-BASIC-Username", "ApplicativoSoggettoInternoTestFruitore1Errate");
		headers.put("X-Erogazione-BASIC-Password", "123456Errate");
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"basic",
				headers,
				null,
				null,
				null,
				"Basic realm=\"GovWay\", error=\"invalid_request\", error_description=\"Invalid credentials\"",
				Utilities.CREDENZIALI_NON_CORRETTE,
				"Ottenute credenziali di accesso ( BasicUsername 'ApplicativoSoggettoInternoTestFruitore1Errate' ) fornite da WebServerErogazioniDefault",
				CostantiProtocollo.CREDENZIALI_FORNITE_NON_CORRETTE
				);
	}
	@Test
	public void erogazione_autenticazioneBasic_nonPresenti() throws Exception {
		Map<String, String> headers = new HashMap<>();
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"basic",
				headers,
				null,
				null,
				null,
				"Basic realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required http-basic credentials\"",
				Utilities.CREDENZIALI_NON_FORNITE,
				CostantiProtocollo.CREDENZIALI_NON_FORNITE
				);
	}
	
	// autenticazioneBasic [FRUIZIONE]
	@Test
	public void fruizione_autenticazioneBasic() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-BASIC-Username", "ApplicativoSoggettoInternoTestFruitore1");
		headers.put("X-Fruizione-BASIC-Password", "123456");
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestFruitore",
				"basic",
				headers,
				"SoggettoInternoTestFruitore",
				"ApplicativoSoggettoInternoTestFruitore1",
				"@@GatewayCredenziali@@WebServerFruizioniDefault@@BasicUsername 'ApplicativoSoggettoInternoTestFruitore1'",
				null,
				null,
				"Ottenute credenziali di accesso ( BasicUsername 'ApplicativoSoggettoInternoTestFruitore1' ) fornite da WebServerFruizioniDefault");
	}
	@Test
	public void fruizione_autenticazioneBasic_errate() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-BASIC-Username", "ApplicativoSoggettoInternoTestFruitore1Errate");
		headers.put("X-Fruizione-BASIC-Password", "123456Errate");
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestFruitore",
				"basic",
				headers,
				"SoggettoInternoTestFruitore",
				null,
				null,
				"Basic realm=\"GovWay\", error=\"invalid_request\", error_description=\"Invalid credentials\"",
				Utilities.CREDENZIALI_NON_CORRETTE,
				"Ottenute credenziali di accesso ( BasicUsername 'ApplicativoSoggettoInternoTestFruitore1Errate' ) fornite da WebServerFruizioniDefault",
				CostantiProtocollo.CREDENZIALI_FORNITE_NON_CORRETTE
				);
	}
	@Test
	public void fruizione_autenticazioneBasic_nonPresenti() throws Exception {
		Map<String, String> headers = new HashMap<>();
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestFruitore",
				"basic",
				headers,
				"SoggettoInternoTestFruitore",
				null,
				null,
				"Basic realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required http-basic credentials\"",
				Utilities.CREDENZIALI_NON_FORNITE,
				CostantiProtocollo.CREDENZIALI_NON_FORNITE
				);
	}
	
	// autenticazioneHttps [EROGAZIONE]
	@Test
	public void erogazione_autenticazioneHttps() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-SSL-Subject", "l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM");
		headers.put("X-Erogazione-SSL-Issuer", "/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/");
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"https",
				headers,
				"SoggettoInternoTestFruitore",
				"HSMClient1",
				"@@GatewayCredenziali@@WebServerErogazioniDefault@@SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM'\nSSL-Issuer '/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/'",
				null,
				null,
				"Ottenute credenziali di accesso ( SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM' ) fornite da WebServerErogazioniDefault");
	}
	@Test
	public void erogazione_autenticazioneHttps_noIssuer() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-SSL-Subject", "l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM");
		// Manca l'issuer
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"https",
				headers,
				null,
				null,
				"@@GatewayCredenziali@@WebServerErogazioniDefault@@SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM'",
				null,
				Utilities.AUTORIZZAZIONE_NEGATA,
				"Ottenute credenziali di accesso ( SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM' ) fornite da WebServerErogazioniDefault",
				"Il mittente non è autorizzato ad invocare il servizio"
				);
	}
	@Test
	public void erogazione_autenticazioneHttps_certs() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-SSL-Cert",Utilities.getCertificate(false));
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"https",
				headers,
				"SoggettoInternoTestFruitore",
				"ApplicativoTestGestoreCredenzialiCertificato",
				"@@GatewayCredenziali@@WebServerErogazioniDefault@@SSL-Subject 'C=IT, O=test, CN=example.gestoreCredenziali'\nSSL-Issuer 'C=IT, O=test, CN=example.gestoreCredenziali'\nSSL-ClientCert-SerialNumber '313156636180879269931219199270197192051648745965'",
				null,
				null,
				"Ottenute credenziali di accesso ( SSL-Subject 'C=IT, O=test, CN=example.gestoreCredenziali' ) fornite da WebServerErogazioniDefault");
	}
	@Test
	public void erogazione_autenticazioneHttps_certs_non_inserito_truststore() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-SSL-Cert",Utilities.getCertificate2(false));
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"https",
				headers,
				null,
				null,
				null,
				"mTLS realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required client certificate\"",
				Utilities.CREDENZIALI_PROXY_FORNITE_NON_CONFORMI,
				"Certificato presente nell'header 'X-Erogazione-SSL-Cert' non è verificabile rispetto alle CA conosciute");
	}
	@Test
	public void erogazione_autenticazioneHttps_errate() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-SSL-Subject", "l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM, Altro=errato");
		headers.put("X-Erogazione-SSL-Issuer", "/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/Altro=errato/");
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"https",
				headers,
				null,
				null,
				"@@GatewayCredenziali@@WebServerErogazioniDefault@@SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM, Altro=errato'\nSSL-Issuer '/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/Altro=errato/'",
				null,
				Utilities.AUTORIZZAZIONE_NEGATA,
				"Ottenute credenziali di accesso ( SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM, Altro=errato' ) fornite da WebServerErogazioniDefault",
				"Il mittente non è autorizzato ad invocare il servizio"
				);
	}
	@Test
	public void erogazione_autenticazioneHttps_nonPresenti() throws Exception {
		Map<String, String> headers = new HashMap<>();
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"https",
				headers,
				null,
				null,
				null,
				"mTLS realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required client certificate\"",
				Utilities.CREDENZIALI_NON_FORNITE,
				CostantiProtocollo.CREDENZIALI_NON_FORNITE
				);
	}
	

	
	
	// public [FRUIZIONE]

	@Test
	public void fruizione_public() throws Exception {
		Map<String, String> headers = new HashMap<>();
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestFruitore",
				"public",
				headers,
				"SoggettoInternoTestFruitore",
				null,
				null,
				null,
				null);
	}
	
	// autenticazioneHttps [FRUIZIONE]
	@Test
	public void fruizione_autenticazioneHttps() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-SSL-Subject", "l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM");
		headers.put("X-Fruizione-SSL-Issuer", "/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/");
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestFruitore",
				"https",
				headers,
				"SoggettoInternoTestFruitore",
				"HSMClient1",
				"@@GatewayCredenziali@@WebServerFruizioniDefault@@SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM'\nSSL-Issuer '/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/'",
				null,
				null,
				"Ottenute credenziali di accesso ( SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM' ) fornite da WebServerFruizioniDefault");
	}
	@Test
	public void fruizione_autenticazioneHttps_noIssuer() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-SSL-Subject", "l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM");
		// Manca l'issuer
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestFruitore",
				"https",
				headers,
				"SoggettoInternoTestFruitore",
				null,
				null,
				null,
				Utilities.AUTORIZZAZIONE_NEGATA,
				"Ottenute credenziali di accesso ( SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM' ) fornite da WebServerFruizioniDefault",
				"non risulta autorizzato a fruire del servizio richiesto"
				);
	}
	@Test
	public void fruizione_autenticazioneHttps_certs() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-SSL-Cert", Utilities.getCertificate(true));
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestFruitore",
				"https",
				headers,
				"SoggettoInternoTestFruitore",
				"ApplicativoTestGestoreCredenzialiCertificato",
				"@@GatewayCredenziali@@WebServerFruizioniDefault@@SSL-Subject 'C=IT, O=test, CN=example.gestoreCredenziali'\nSSL-Issuer 'C=IT, O=test, CN=example.gestoreCredenziali'\nSSL-ClientCert-SerialNumber '313156636180879269931219199270197192051648745965'",
				null,
				null,
				"Ottenute credenziali di accesso ( SSL-Subject 'C=IT, O=test, CN=example.gestoreCredenziali' ) fornite da WebServerFruizioniDefault");
	}
	
	@Test
	public void fruizione_autenticazioneHttps_certs2_non_in_truststore_ma_truststore_non_attivato() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-SSL-Cert", Utilities.getCertificate2(true));
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestFruitore",
				"https",
				headers,
				"SoggettoInternoTestFruitore",
				"ApplicativoTestGestoreCredenzialiCertificato2",
				"@@GatewayCredenziali@@WebServerFruizioniDefault@@SSL-Subject 'C=IT, O=test, CN=example.gestoreCredenziali2'\nSSL-Issuer 'C=IT, O=test, CN=example.gestoreCredenziali2'\nSSL-ClientCert-SerialNumber '464440956505565963808339808380319088904980969620'",
				null,
				null,
				"Ottenute credenziali di accesso ( SSL-Subject 'C=IT, O=test, CN=example.gestoreCredenziali2' ) fornite da WebServerFruizioniDefault");
	}
	
	@Test
	public void fruizione_autenticazioneHttps_errate() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-SSL-Subject", "l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM, Altro=errato");
		headers.put("X-Fruizione-SSL-Issuer", "/l=Pisa/st=Italy/ou=Test/o=Test/c=IT/cn=ExampleClient1HSM/Altro=errato/");
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestFruitore",
				"https",
				headers,
				"SoggettoInternoTestFruitore",
				null,
				null,
				null,
				Utilities.AUTORIZZAZIONE_NEGATA,
				"Ottenute credenziali di accesso ( SSL-Subject 'l=Pisa, st=Italy, ou=Test, o=Test, c=IT, cn=ExampleClient1HSM, Altro=errato' ) fornite da WebServerFruizioniDefault",
				"non risulta autorizzato a fruire del servizio richiesto"
				);
	}
	@Test
	public void fruizione_autenticazioneHttps_nonPresenti() throws Exception {
		Map<String, String> headers = new HashMap<>();
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestFruitore",
				"https",
				headers,
				"SoggettoInternoTestFruitore",
				null,
				null,
				"mTLS realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required client certificate\"",
				Utilities.CREDENZIALI_NON_FORNITE,
				CostantiProtocollo.CREDENZIALI_NON_FORNITE
				);
	}
	
	
	// autenticazionePrincipal [EROGAZIONE]
	@Test
	public void erogazione_autenticazionePrincipal() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-Principal", "ApplicativoSoggettoInternoTestFruitore1");
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"principal",
				headers,
				"SoggettoInternoTestFruitore",
				"ApplicativoSoggettoInternoTestFruitore1Principal",
				"@@GatewayCredenziali@@WebServerErogazioniDefault@@Principal 'ApplicativoSoggettoInternoTestFruitore1'",
				null,
				null,
				"Ottenute credenziali di accesso ( Principal 'ApplicativoSoggettoInternoTestFruitore1' ) fornite da WebServerErogazioniDefault");
	}
	@Test
	public void erogazione_autenticazionePrincipal_errate() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Erogazione-Principal", "ApplicativoSoggettoInternoTestFruitore1Errate");
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"principal",
				headers,
				null,
				null,
				"@@GatewayCredenziali@@WebServerErogazioniDefault@@Principal 'ApplicativoSoggettoInternoTestFruitore1Errate'",
				null,
				Utilities.AUTORIZZAZIONE_NEGATA,
				"Ottenute credenziali di accesso ( Principal 'ApplicativoSoggettoInternoTestFruitore1Errate' ) fornite da WebServerErogazioniDefault",
				"Il mittente non è autorizzato ad invocare il servizio"
				);
	}
	@Test
	public void erogazione_autenticazionePrincipal_nonPresenti() throws Exception {
		Map<String, String> headers = new HashMap<>();
		Utilities._test(TipoServizio.EROGAZIONE,
				"SoggettoInternoTest",
				"principal",
				headers,
				null,
				null,
				null,
				"PrincipalAuth realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required principal credentials\"",
				Utilities.CREDENZIALI_NON_FORNITE,
				CostantiProtocollo.CREDENZIALI_NON_FORNITE
				);
	}
	
	// autenticazionePrincipal [FRUIZIONE]
	@Test
	public void fruizione_autenticazionePrincipal() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-Principal", "ApplicativoSoggettoInternoTestFruitore1");
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestFruitore",
				"principal",
				headers,
				"SoggettoInternoTestFruitore",
				"ApplicativoSoggettoInternoTestFruitore1Principal",
				"@@GatewayCredenziali@@WebServerFruizioniDefault@@Principal 'ApplicativoSoggettoInternoTestFruitore1'",
				null,
				null,
				"Ottenute credenziali di accesso ( Principal 'ApplicativoSoggettoInternoTestFruitore1' ) fornite da WebServerFruizioniDefault");
	}
	@Test
	public void fruizione_autenticazionePrincipal_errate() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Fruizione-Principal", "ApplicativoSoggettoInternoTestFruitore1Errate");
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestFruitore",
				"principal",
				headers,
				"SoggettoInternoTestFruitore",
				null,
				null,
				null,
				Utilities.AUTORIZZAZIONE_NEGATA,
				"Ottenute credenziali di accesso ( Principal 'ApplicativoSoggettoInternoTestFruitore1Errate' ) fornite da WebServerFruizioniDefault",
				"non risulta autorizzato a fruire del servizio richiesto"
				);
	}
	@Test
	public void fruizione_autenticazionePrincipal_nonPresenti() throws Exception {
		Map<String, String> headers = new HashMap<>();
		Utilities._test(TipoServizio.FRUIZIONE,
				"SoggettoInternoTestFruitore",
				"principal",
				headers,
				"SoggettoInternoTestFruitore",
				null,
				null,
				"PrincipalAuth realm=\"GovWay\", error=\"invalid_request\", error_description=\"The request is missing a required principal credentials\"",
				Utilities.CREDENZIALI_NON_FORNITE,
				CostantiProtocollo.CREDENZIALI_NON_FORNITE
				);
	}
	

}
