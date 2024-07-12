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

import org.openspcoop2.security.message.constants.SecurityConstants;

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

	
	
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_FILE = SecurityConstants.JOSE_TRUSTSTORE_SSL_FILE;
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_PASSWORD = SecurityConstants.JOSE_TRUSTSTORE_SSL_PSWD;
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_TYPE = SecurityConstants.JOSE_TRUSTSTORE_SSL_TYPE;
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_CRL = SecurityConstants.JOSE_TRUSTSTORE_SSL_CRL;
	
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_TRUSTALL = SecurityConstants.JOSE_TRUSTSTORE_SSL_TRUSTALL;
	
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_CONNECTION_TIMEOUT = SecurityConstants.JOSE_TRUSTSTORE_SSL_CONNECTION_TIMEOUT;
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_READ_TIMEOUT =  SecurityConstants.JOSE_TRUSTSTORE_SSL_READ_TIMEOUT;
	
	public static final String ID_TRUSTSTORE_SSL_KEYSTORE_HOSTNAME_VERIFIER = SecurityConstants.JOSE_TRUSTSTORE_SSL_HOSTNAME_VERIFIER;
	
	public static final String ID_FORWARD_PROXY_ENDPOINT = SecurityConstants.JOSE_FORWARD_PROXY_ENDPOINT;
	public static final String ID_FORWARD_PROXY_HEADER = SecurityConstants.JOSE_FORWARD_PROXY_HEADER;
	public static final String ID_FORWARD_PROXY_HEADER_BASE64 = SecurityConstants.JOSE_FORWARD_PROXY_HEADER_BASE64;
	public static final String ID_FORWARD_PROXY_QUERY = SecurityConstants.JOSE_FORWARD_PROXY_QUERY;
	public static final String ID_FORWARD_PROXY_QUERY_BASE64 = SecurityConstants.JOSE_FORWARD_PROXY_QUERY_BASE64;
	
	public static final String ID_PROXY_TYPE = SecurityConstants.JOSE_PROXY_TYPE;
	public static final String ID_PROXY_HOSTNAME = SecurityConstants.JOSE_PROXY_HOSTNAME;
	public static final String ID_PROXY_PORT = SecurityConstants.JOSE_PROXY_PORT;
	public static final String ID_PROXY_USERNAME = SecurityConstants.JOSE_PROXY_USERNAME;
	public static final String ID_PROXY_PASSWORD = SecurityConstants.JOSE_PROXY_PASSWORD;
	
}
