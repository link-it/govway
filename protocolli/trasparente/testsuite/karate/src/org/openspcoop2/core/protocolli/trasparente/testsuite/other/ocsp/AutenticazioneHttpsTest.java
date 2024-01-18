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
package org.openspcoop2.core.protocolli.trasparente.testsuite.other.ocsp;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;

/**
* AutenticazioneHttpsTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AutenticazioneHttpsTest extends ConfigLoader {

	public static final String api = "TestOCSPAutenticazioneHTTPS";
	public static final String soggetto_ocsp = "SoggettoInternoTestAutenticazioneHttpsOcsp"; 
	public static final String soggetto_xca = "SoggettoInternoTestAutenticazioneHttpsXca"; 
	
	public static final String opensslCommand = System.getProperty(Utils.PROPERTY_OCSP_OPENSSL_COMMAND);
	
	public static final int waitStartupServer = Integer.valueOf(System.getProperty(Utils.PROPERTY_OCSP_WAIT_STARTUP_SERVER));
	
	public static final int waitStopServer = Integer.valueOf(System.getProperty(Utils.PROPERTY_OCSP_WAIT_STOP_SERVER));
	

	@Test
	public void erogazione_case2_certificateValid() throws Exception {
		Utils.composedTestSuccess(logCore, TipoServizio.EROGAZIONE, api, soggetto_ocsp,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2", 
				Utils.CERTIFICATE_VALID_CASE2_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	@Test
	public void fruizione_case2_certificateValid() throws Exception {
		Utils.composedTestSuccess(logCore, TipoServizio.FRUIZIONE, api, soggetto_ocsp,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2", 
				Utils.CERTIFICATE_VALID_CASE2_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	
	
	@Test
	public void erogazione_case2_certificateRevoked() throws Exception {
		Utils.composedTestError(logCore, TipoServizio.EROGAZIONE, api, soggetto_ocsp,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2-revoked", 
				Utils.CERTIFICATE_REVOKED_CESSATION_OF_OPERATION, Utils.ERROR_CACHED,
				Utils.CERTIFICATE_REVOKED_CASE2_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	@Test
	public void fruizione_case2_certificateRevoked() throws Exception {
		Utils.composedTestError(logCore, TipoServizio.FRUIZIONE, api, soggetto_ocsp,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2-revoked", 
				Utils.CERTIFICATE_REVOKED_CESSATION_OF_OPERATION, Utils.ERROR_CACHED,
				Utils.CERTIFICATE_REVOKED_CASE2_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	
	
	
	@Test
	public void erogazione_case3_certificateValid() throws Exception {
		Utils.composedTestSuccess(logCore, TipoServizio.EROGAZIONE, api, soggetto_ocsp,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3", 
				Utils.CERTIFICATE_VALID_CASE3_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	@Test
	public void fruizione_case3_certificateValid() throws Exception {
		Utils.composedTestSuccess(logCore, TipoServizio.FRUIZIONE, api, soggetto_ocsp,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3", 
				Utils.CERTIFICATE_VALID_CASE3_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	
	
	@Test
	public void erogazione_case3_certificateRevoked() throws Exception {
		Utils.composedTestError(logCore, TipoServizio.EROGAZIONE, api, soggetto_ocsp,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3-revoked", 
				Utils.CERTIFICATE_REVOKED_CESSATION_OF_OPERATION, Utils.ERROR_CACHED,
				Utils.CERTIFICATE_REVOKED_CASE3_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	@Test
	public void fruizione_case3_certificateRevoked() throws Exception {
		Utils.composedTestError(logCore, TipoServizio.FRUIZIONE, api, soggetto_ocsp,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3-revoked", 
				Utils.CERTIFICATE_REVOKED_CESSATION_OF_OPERATION, Utils.ERROR_CACHED,
				Utils.CERTIFICATE_REVOKED_CASE3_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	

	
	
	@Test
	public void erogazione_crl_certificateValid() throws Exception {
		Utils.get(logCore, TipoServizio.EROGAZIONE, api, soggetto_xca, "case2");
	}
	@Test
	public void fruizione_crl_certificateValid() throws Exception {
		Utils.get(logCore, TipoServizio.FRUIZIONE, api, soggetto_xca, "case2");
	}
	
	@Test
	public void erogazione_crl_certificateRevoked() throws Exception {
		Utils.get(logCore, TipoServizio.EROGAZIONE, api, soggetto_xca, "case2-revoked", 
				Utils.CERTIFICATE_CRL_REVOKED_UNSPECIFIED_MSG_KEY_COMPROMISE);
	}
	@Test
	public void fruizione_crl_certificateRevoked() throws Exception {
		Utils.get(logCore, TipoServizio.FRUIZIONE, api, soggetto_xca, "case2-revoked", 
				Utils.CERTIFICATE_CRL_REVOKED_UNSPECIFIED_MSG_KEY_COMPROMISE);
	}
	
	@Test
	public void erogazione_crl_certificateExpired() throws Exception {
		Utils.get(logCore,TipoServizio.EROGAZIONE, api, soggetto_xca, "case3-revoked", 
				Utils.CERTIFICATE_CRL_EXPIRED);
	}
	@Test
	public void fruizione_crl_certificateExpired() throws Exception {
		Utils.get(logCore,TipoServizio.FRUIZIONE, api, soggetto_xca, "case3-revoked", 
				Utils.CERTIFICATE_CRL_EXPIRED);
	}
	
}