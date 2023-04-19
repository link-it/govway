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
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
* WSSecuritySAMLTokenTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WSSecuritySAMLTokenTest extends ConfigLoader {

	public static final String api_saml_prefix = "TestOCSPWSSecuritySaml";
	public static final String api_bearer = api_saml_prefix+"Bearer";
	public static final String api_sender_vouches = api_saml_prefix+"SenderVouches";
	public static final String api_holder_of_key = api_saml_prefix+"HolderOfKey";
	public static final String soggetto = "SoggettoInternoTestFruitore";
	
	public static final String opensslCommand = System.getProperty(Utils.PROPERTY_OCSP_OPENSSL_COMMAND);
	
	public static final int waitStartupServer = Integer.valueOf(System.getProperty(Utils.PROPERTY_OCSP_WAIT_STARTUP_SERVER));
	
	public static final int waitStopServer = Integer.valueOf(System.getProperty(Utils.PROPERTY_OCSP_WAIT_STOP_SERVER));
	

	@Test
	public void saml_bearer_case2_certificateValid() throws Exception {
		Utils.composedTestSuccess(logCore, TipoServizio.FRUIZIONE, api_bearer, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2", 
				Utils.CERTIFICATE_VALID_CASE2_CONNECTION_REFUSED);
	}
	@Test
	public void saml_sender_vouches_case2_certificateValid() throws Exception {
		Utils.composedTestSuccess(logCore, TipoServizio.FRUIZIONE, api_sender_vouches, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2", 
				Utils.CERTIFICATE_VALID_CASE2_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	@Test
	public void saml_holder_of_key_case2_certificateValid() throws Exception {
		Utils.composedTestSuccess(logCore, TipoServizio.FRUIZIONE, api_holder_of_key, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2", 
				Utils.CERTIFICATE_VALID_CASE2_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	
	@Test
	public void saml_bearer_case2_certificateRevoked() throws Exception {
		Utils.composedTestError(logCore, TipoServizio.FRUIZIONE, api_bearer, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2-revoked", 
				Utils.CERTIFICATE_REVOKED_CESSATION_OF_OPERATION, Utils.ERROR_CACHED,
				Utils.CERTIFICATE_REVOKED_CASE2_CONNECTION_REFUSED);
	}
	@Test
	public void saml_sender_vouches_case2_certificateRevoked() throws Exception {
		Utils.composedTestError(logCore, TipoServizio.FRUIZIONE, api_sender_vouches, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2-revoked", 
				Utils.CERTIFICATE_REVOKED_CESSATION_OF_OPERATION, Utils.ERROR_CACHED,
				Utils.CERTIFICATE_REVOKED_CASE2_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	@Test
	public void saml_holder_of_key_case2_certificateRevoked() throws Exception {
		Utils.composedTestError(logCore, TipoServizio.FRUIZIONE, api_holder_of_key, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case2-revoked", 
				Utils.CERTIFICATE_REVOKED_CESSATION_OF_OPERATION, Utils.ERROR_CACHED,
				Utils.CERTIFICATE_REVOKED_CASE2_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	
	
	
	@Test
	public void saml_bearer_case3_certificateValid() throws Exception {
		Utils.composedTestSuccess(logCore, TipoServizio.FRUIZIONE, api_bearer, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3", 
				Utils.CERTIFICATE_VALID_CASE3_CONNECTION_REFUSED);
	}
	@Test
	public void saml_sender_vouches_case3_certificateValid() throws Exception {
		Utils.composedTestSuccess(logCore, TipoServizio.FRUIZIONE, api_sender_vouches, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3", 
				Utils.CERTIFICATE_VALID_CASE3_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	@Test
	public void saml_holder_of_key_case3_certificateValid() throws Exception {
		Utils.composedTestSuccess(logCore, TipoServizio.FRUIZIONE, api_holder_of_key, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3", 
				Utils.CERTIFICATE_VALID_CASE3_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	
	@Test
	public void saml_bearer_case3_certificateRevoked() throws Exception {
		Utils.composedTestError(logCore, TipoServizio.FRUIZIONE, api_bearer, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3-revoked", 
				Utils.CERTIFICATE_REVOKED_CESSATION_OF_OPERATION, Utils.ERROR_CACHED,
				Utils.CERTIFICATE_REVOKED_CASE3_CONNECTION_REFUSED);
	}
	@Test
	public void saml_sender_vouches_case3_certificateRevoked() throws Exception {
		Utils.composedTestError(logCore, TipoServizio.FRUIZIONE, api_sender_vouches, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3-revoked", 
				Utils.CERTIFICATE_REVOKED_CESSATION_OF_OPERATION, Utils.ERROR_CACHED,
				Utils.CERTIFICATE_REVOKED_CASE3_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	@Test
	public void saml_holder_of_key_case3_certificateRevoked() throws Exception {
		Utils.composedTestError(logCore, TipoServizio.FRUIZIONE, api_holder_of_key, soggetto,
				opensslCommand, waitStartupServer, waitStopServer,
				"case3-revoked", 
				Utils.CERTIFICATE_REVOKED_CESSATION_OF_OPERATION, Utils.ERROR_CACHED,
				Utils.CERTIFICATE_REVOKED_CASE3_CONNECTION_REFUSED_ORDINE_DIFFERENTE_CERTIFICATO);
	}
	

	
	
	@Test
	public void saml_bearer_ocsp_crl_certificateValid() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_bearer, soggetto, "ocsp-crl-valid");
	}
	@Test
	public void saml_sender_vouches_ocsp_crl_certificateValid() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_sender_vouches, soggetto, "ocsp-crl-valid");
	}
	@Test
	public void saml_holder_of_key_ocsp_crl_certificateValid() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_holder_of_key, soggetto, "ocsp-crl-valid");
	}
	
	@Test
	public void saml_bearer_ocsp_crl_certificateRevoked() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_bearer, soggetto, "ocsp-crl-revoked", 
				Utils.CERTIFICATE_CRL_REVOKED_UNSPECIFIED_MSG_KEY_COMPROMISE);
	}
	@Test
	public void saml_sender_vouches_ocsp_crl_certificateRevoked() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_sender_vouches, soggetto, "ocsp-crl-revoked", 
				Utils.CERTIFICATE_CRL_REVOKED_UNSPECIFIED_MSG_KEY_COMPROMISE);
	}
	@Test
	public void saml_holder_of_key_ocsp_crl_certificateRevoked() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_holder_of_key, soggetto, "ocsp-crl-revoked", 
				Utils.CERTIFICATE_CRL_REVOKED_UNSPECIFIED_MSG_KEY_COMPROMISE);
	}
	
	@Test
	public void saml_bearer_ocsp_crl_certificateExpired() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_bearer, soggetto, "ocsp-crl-expired", 
				Utils.CERTIFICATE_CRL_EXPIRED_WSS_SAML);
	}
	@Test
	public void saml_sender_vouches_ocsp_crl_certificateExpired() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_sender_vouches, soggetto, "ocsp-crl-expired", 
				Utils.CERTIFICATE_CRL_EXPIRED_WSS);
	}
	@Test
	public void saml_holder_of_key_ocsp_crl_certificateExpired() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_holder_of_key, soggetto, "ocsp-crl-expired", 
				Utils.CERTIFICATE_CRL_EXPIRED_WSS);
	}
	

	
	
	@Test
	public void saml_bearer_crl_certificateValid() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_bearer, soggetto, "crl-valid");
	}
	@Test
	public void saml_sender_vouches_crl_certificateValid() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_sender_vouches, soggetto, "crl-valid");
	}
	@Test
	public void saml_holder_of_key_crl_certificateValid() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_holder_of_key, soggetto, "crl-valid");
	}
	
	@Test
	public void saml_bearer_crl_certificateRevoked() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_bearer, soggetto, "crl-revoked", 
				Utils.CERTIFICATE_CRL_REVOKED_UNSPECIFIED_MSG_KEY_COMPROMISE_SAML);
	}
	@Test
	public void saml_sender_vouches_crl_certificateRevoked() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_sender_vouches, soggetto, "crl-revoked", 
				Utils.CERTIFICATE_CRL_REVOKED_UNSPECIFIED_MSG_KEY_COMPROMISE_SAML);
	}
	@Test
	public void saml_holder_of_key_crl_certificateRevoked() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_holder_of_key, soggetto, "crl-revoked", 
				Utils.CERTIFICATE_CRL_REVOKED_UNSPECIFIED_MSG_KEY_COMPROMISE_SAML);
	}
	
	@Test
	public void saml_bearer_crl_certificateExpired() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_bearer, soggetto, "crl-expired", 
				Utils.CERTIFICATE_CRL_EXPIRED_WSS_SAML_2);
	}
	@Test
	public void saml_sender_vouches_crl_certificateExpired() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_sender_vouches, soggetto, "crl-expired", 
				Utils.CERTIFICATE_CRL_EXPIRED_WSS);
	}
	@Test
	public void saml_holder_of_key_crl_certificateExpired() throws Exception {
		Utils.test(TipoServizio.FRUIZIONE, HttpRequestMethod.POST, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(), 
				logCore, api_holder_of_key, soggetto, "crl-expired", 
				Utils.CERTIFICATE_CRL_EXPIRED_WSS);
	}
	
}