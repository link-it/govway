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

package org.openspcoop2.utils.certificate.byok;

/**
 * BYOKCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKCostanti {
	
	private BYOKCostanti() {}

	public static final String SECURITY_PROPERTY_PREFIX = "security.";
	
	public static final String SECURITY_PROPERTY_SUFFIX_WRAP = "ksm.wrap";
	public static final String SECURITY_PROPERTY_SUFFIX_UNWRAP = "ksm.unwrap";
	
	public static final String SECURITY_PROPERTY_SUFFIX_INPUT = "ksm.param.";
	
	
	
	public static final String PROPERTY_PREFIX = "ksm.";
	
	public static final String PROPERTY_SUFFIX_LABEL = "label";
	public static final String PROPERTY_SUFFIX_TYPE = "type";
	
	public static final String PROPERTY_SUFFIX_MODE = "mode";
	public static final String PROPERTY_MODE_WRAP = "wrap";
	public static final String PROPERTY_MODE_UNWRAP = "unwrap";
	
	public static final String PROPERTY_SUFFIX_ENCRYPTION_MODE = "encryptionMode";
	public static final String PROPERTY_ENCRYPTION_MODE_LOCAL = "local";
	public static final String PROPERTY_ENCRYPTION_MODE_REMOTE = "remote";
	
	public static final String PROPERTY_SUFFIX_INPUT = "input.";
	public static final String PROPERTY_SUFFIX_INPUT_NAME = ".name";
	public static final String PROPERTY_SUFFIX_INPUT_LABEL = ".label";
	
	
	public static final String PROPERTY_SUFFIX_HTTP_ENDPOINT = "http.endpoint";
	public static final String PROPERTY_SUFFIX_HTTP_METHOD = "http.method";
	public static final String PROPERTY_SUFFIX_HTTP_HEADER = "http.header.";
	public static final String PROPERTY_SUFFIX_HTTP_PAYLOAD_INLINE = "http.payload.inline";
	public static final String PROPERTY_SUFFIX_HTTP_PAYLOAD_PATH = "http.payload.path";
	
	public static final String PROPERTY_SUFFIX_HTTP_USERNAME = "http.username";
	public static final String PROPERTY_SUFFIX_HTTP_PASSWORD = "http.password";
	
	public static final String PROPERTY_SUFFIX_HTTP_CONNECTION_TIMEOUT = "http.connectionTimeout";
	public static final String PROPERTY_SUFFIX_HTTP_READ_TIMEOUT = "http.readTimeout";

	public static final String PROPERTY_SUFFIX_HTTPS = "https";
	
	public static final String PROPERTY_SUFFIX_HTTPS_VERIFICA_HOSTNAME = "https.hostnameVerifier";
	
	public static final String PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER = "https.serverAuth";
	public static final String PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_TRUSTSTORE_PATH = "https.serverAuth.trustStore.path";
	public static final String PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_TRUSTSTORE_TYPE = "https.serverAuth.trustStore.type";
	public static final String PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_TRUSTSTORE_PASSWORD = "https.serverAuth.trustStore.password";
	public static final String PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_CRLS = "https.serverAuth.trustStore.crls";
	public static final String PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_SERVER_OCSP_POLICY = "https.serverAuth.trustStore.ocspPolicy";
	
	public static final String PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT = "https.clientAuth";
	public static final String PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEYSTORE_PATH = "https.clientAuth.keyStore.path";
	public static final String PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEYSTORE_TYPE = "https.clientAuth.keyStore.type";
	public static final String PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEYSTORE_PASSWORD = "https.clientAuth.keyStore.password";
	public static final String PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEY_ALIAS = "https.clientAuth.key.alias";
	public static final String PROPERTY_SUFFIX_HTTPS_AUTENTICAZIONE_CLIENT_KEY_PASSWORD = "https.clientAuth.key.password";

	
	
	
	public static final String PROPERTY_LOCAL_ENCRYPTION_ENGINE_JAVA = "java";
	public static final String PROPERTY_LOCAL_ENCRYPTION_ENGINE_JOSE = "jose";
	
	public static final String PROPERTY_LOCAL_JAVA_ENCODING_BASE64 = "base64";
	public static final String PROPERTY_LOCAL_JAVA_ENCODING_HEX = "hex";
		
	public static final String PROPERTY_SUFFIX_LOCAL_IMPL = "local.impl";
	public static final String PROPERTY_SUFFIX_LOCAL_KEYSTORE_TYPE = "local.keystore.type";
	public static final String PROPERTY_SUFFIX_LOCAL_KEYSTORE_PATH = "local.keystore.path";
	public static final String PROPERTY_SUFFIX_LOCAL_KEYSTORE_PASSWORD = "local.keystore.password";
	public static final String PROPERTY_SUFFIX_LOCAL_KEY_PATH = "local.key.path";
	public static final String PROPERTY_SUFFIX_LOCAL_KEY_ALGORITHM = "local.key.algorithm";
	public static final String PROPERTY_SUFFIX_LOCAL_KEY_ALIAS = "local.key.alias";
	public static final String PROPERTY_SUFFIX_LOCAL_KEY_PASSWORD = "local.key.password";
	public static final String PROPERTY_SUFFIX_LOCAL_KEY_ID = "local.key.id";
	public static final String PROPERTY_SUFFIX_LOCAL_KEY_WRAP = "local.key.wrap";
	
	public static final String PROPERTY_SUFFIX_LOCAL_PUBLIC_KEY_PATH = "local.publicKey.path";
	
	public static final String PROPERTY_SUFFIX_LOCAL_CONTENT_ALGORITHM = "local.algorithm";
	
	public static final String PROPERTY_SUFFIX_LOCAL_JAVA_ENCODING = "local.encoding";

	public static final String PROPERTY_SUFFIX_LOCAL_JOSE_INCLUDE_CERT = "local.include.cert";
	public static final String PROPERTY_SUFFIX_LOCAL_JOSE_INCLUDE_PUBLIC_KEY = "local.include.public.key";
	public static final String PROPERTY_SUFFIX_LOCAL_JOSE_INCLUDE_KEY_ID = "local.include.key.id";
	public static final String PROPERTY_SUFFIX_LOCAL_JOSE_INCLUDE_CERT_SHA1 = "local.include.cert.sha1";
	public static final String PROPERTY_SUFFIX_LOCAL_JOSE_INCLUDE_CERT_SHA256 = "local.include.cert.sha256";
	
	
	
	
	public static final String VARIABILE_KSM_KEY = "${ksm-key}";
	public static final String VARIABILE_KSM_KEY_BASE64 = "${ksm-base64-key}";
	public static final String VARIABILE_KSM_KEY_HEX = "${ksm-hex-key}";
	
	public static final String VARIABILE_KSM = "ksm";
	public static final String VARIABILE_KSM_KEY_PREFIX = "${ksm:";
}
