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

package org.openspcoop2.security.message.jose;

import org.apache.cxf.rt.security.rs.RSSecurityConstants;

/**     
 * JOSECostanti
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JOSECostanti {
	
	private JOSECostanti() {}

	public static final String JOSE_ENGINE_DESCRIPTION = "JOSE Engine";
	public static final String JOSE_ENGINE_SIGNATURE_DESCRIPTION = "JOSE Signature Engine";
	public static final String JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION = "JOSE SignatureVerifier Engine";
	public static final String JOSE_ENGINE_ENCRYPT_DESCRIPTION = "JOSE Encrypt Engine";
	public static final String JOSE_ENGINE_DECRYPT_DESCRIPTION = "JOSE Decrypt Engine";
	
	// SELECT LIST ID
	
	public static final String ID_SIGNATURE_ALGORITHM = "signatureAlgorithm";
	
	public static final String ID_ENCRYPT_KEY_ALGORITHM = "encryptionKeyAlgorithm";
	
	public static final String ID_ENCRYPT_CONTENT_ALGORITHM = "encryptionContentAlgorithm";

	
	private static final String JOSE_SSL_SUFFIX = ".ssl";
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_FILE = RSSecurityConstants.RSSEC_KEY_STORE_FILE+JOSE_SSL_SUFFIX;
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_PASSWORD = RSSecurityConstants.RSSEC_KEY_STORE_PSWD+JOSE_SSL_SUFFIX;
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_TYPE = RSSecurityConstants.RSSEC_KEY_STORE_TYPE+JOSE_SSL_SUFFIX;
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_CRL = RSSecurityConstants.RSSEC_KEY_STORE+".crl"+JOSE_SSL_SUFFIX;
	
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_TRUSTALL = RSSecurityConstants.RSSEC_KEY_STORE+".trustAll"+JOSE_SSL_SUFFIX;
	
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_CONNECTION_TIMEOUT = RSSecurityConstants.RSSEC_KEY_STORE+JOSE_SSL_SUFFIX+".connectionTimeout";
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_READ_TIMEOUT = RSSecurityConstants.RSSEC_KEY_STORE+JOSE_SSL_SUFFIX+".readTimeout";
	
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_HOSTNAME_VERIFIER = RSSecurityConstants.RSSEC_KEY_STORE+JOSE_SSL_SUFFIX+".hostNameVerifier";
	
	private static final String ID_FORWARD_PROXY_PREFIX = "rs.security.forwardProxy.";
	public static final String ID_FORWARD_PROXY_ENDPOINT = ID_FORWARD_PROXY_PREFIX+"endpoint";
	public static final String ID_FORWARD_PROXY_HEADER = ID_FORWARD_PROXY_PREFIX+"header";
	public static final String ID_FORWARD_PROXY_HEADER_BASE64 = ID_FORWARD_PROXY_PREFIX+"header.base64";
	public static final String ID_FORWARD_PROXY_QUERY = ID_FORWARD_PROXY_PREFIX+"query";
	public static final String ID_FORWARD_PROXY_QUERY_BASE64 = ID_FORWARD_PROXY_PREFIX+"query.base64";
	
	private static final String ID_PROXY_PREFIX = "rs.security.proxy.";
	public static final String ID_PROXY_TYPE = ID_PROXY_PREFIX+"type";
	public static final String ID_PROXY_HOSTNAME = ID_PROXY_PREFIX+"hostname";
	public static final String ID_PROXY_PORT = ID_PROXY_PREFIX+"port";
	public static final String ID_PROXY_USERNAME = ID_PROXY_PREFIX+"username";
	public static final String ID_PROXY_PASSWORD = ID_PROXY_PREFIX+"password";
	
}
