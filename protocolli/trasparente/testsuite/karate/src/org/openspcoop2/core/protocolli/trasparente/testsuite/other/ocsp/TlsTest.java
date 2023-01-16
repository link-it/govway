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
package org.openspcoop2.core.protocolli.trasparente.testsuite.other.ocsp;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;

/**
* TlsTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TlsTest extends ConfigLoader {

	public final static String api = "TestOCSPConnettoreHTTPS";
	public final static String soggetto = "SoggettoInternoTest";
	
	public final static String opensslCommand = System.getProperty(Utils.PROPERTY_OCSP_OPENSSL_COMMAND);
	
	public final static int waitStartupServer = Integer.valueOf(System.getProperty(Utils.PROPERTY_OCSP_WAIT_STARTUP_SERVER));
	
	public final static int waitStopServer = Integer.valueOf(System.getProperty(Utils.PROPERTY_OCSP_WAIT_STOP_SERVER));
	
	@Test
	public void govway() throws Exception {
		Utils.get(logCore, api, soggetto, "govway", null);
	}
	
	@Test
	public void govway_alternative_url() throws Exception {
		Utils.get(logCore, api, soggetto, "govway-alternative-url", null);
	}
	
	@Test
	public void google() throws Exception {
		Utils.get(logCore, api, soggetto, "google", null);
	}
	
	
	@Test
	public void case2_certificateValid() throws Exception {
		Utils.composedTestSuccess(logCore, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2", 
				Utils.CERTIFICATE_VALID_CASE2_CONNECTION_REFUSED);
	}
	@Test
	public void case2_no_nonce_certificateValid() throws Exception {
		Utils.composedTestSuccess(logCore, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2-no-nonce", 
				Utils.CERTIFICATE_VALID_CASE2_CONNECTION_REFUSED);
	}
	
	@Test
	public void case2_certificateRevoked() throws Exception {
		Utils.composedTestError(logCore, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2-revoked", 
				Utils.CERTIFICATE_REVOKED_CESSATION_OF_OPERATION, Utils.ERROR_CACHED,
				Utils.CERTIFICATE_REVOKED_CASE2_CONNECTION_REFUSED);
	}
	@Test
	public void case2_no_nonce_certificateRevoked() throws Exception {
		Utils.composedTestError(logCore, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2-no-nonce-revoked", 
				Utils.CERTIFICATE_REVOKED_CESSATION_OF_OPERATION, Utils.ERROR_CACHED,
				Utils.CERTIFICATE_REVOKED_CASE2_CONNECTION_REFUSED);
	}
	
	@Test
	public void case2_differente_nonce_certificateValid() throws Exception {
		Utils.composedTestError(logCore, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2-different-nonce", 
				Utils.CERTIFICATE_VALID_DIFFERENT_NONCE, !Utils.ERROR_CACHED, // l'errore non è una risposta OCSP valida quindi non viene cachata
				Utils.CERTIFICATE_VALID_CASE2_DIFFERENT_NONCE_CONNECTION_REFUSED);
	}
	
	
	@Test
	public void case3_certificateValid() throws Exception {
		Utils.composedTestSuccess(logCore, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3", 
				Utils.CERTIFICATE_VALID_CASE3_CONNECTION_REFUSED);
	}
	@Test
	public void case3_no_nonce_certificateValid() throws Exception {
		Utils.composedTestSuccess(logCore, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3-no-nonce", 
				Utils.CERTIFICATE_VALID_CASE3_CONNECTION_REFUSED);
	}
	
	@Test
	public void case3_certificateRevoked() throws Exception {
		Utils.composedTestError(logCore, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3-revoked", 
				Utils.CERTIFICATE_REVOKED_CESSATION_OF_OPERATION, Utils.ERROR_CACHED,
				Utils.CERTIFICATE_REVOKED_CASE3_CONNECTION_REFUSED);
	}
	@Test
	public void case3_no_nonce_certificateRevoked() throws Exception {
		Utils.composedTestError(logCore, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3-no-nonce-revoked", 
				Utils.CERTIFICATE_REVOKED_CESSATION_OF_OPERATION, Utils.ERROR_CACHED,
				Utils.CERTIFICATE_REVOKED_CASE3_CONNECTION_REFUSED);
	}
	
	
	@Test
	public void case3_no_extendedKeyUsage_certificateValid() throws Exception {
		Utils.composedTestError(logCore, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3-no-extended-key-usage", 
				Utils.CERTIFICATE_VALID_KEY_USAGE_NOT_FOUND, !Utils.ERROR_CACHED, // l'errore non è una risposta OCSP valida quindi non viene cachata
				Utils.CERTIFICATE_VALID_CASE3_CONNECTION_REFUSED);
	}
	
	@Test
	public void case3_no_ca_certificateValid() throws Exception {
		Utils.composedTestError(logCore, api, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3-no-ca", 
				Utils.CERTIFICATE_VALID_UNAUTHORIZED_DIFFERENT_ISSUER_CERTIFICATE, !Utils.ERROR_CACHED, // l'errore non è una risposta OCSP valida quindi non viene cachata
				Utils.CERTIFICATE_VALID_CASE3_CONNECTION_REFUSED);
	}

}