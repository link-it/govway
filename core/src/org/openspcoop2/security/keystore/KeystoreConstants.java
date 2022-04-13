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
package org.openspcoop2.security.keystore;

import org.apache.wss4j.common.crypto.Merlin;

/**
 * KeystoreConstants
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KeystoreConstants {

	// https://ws.apache.org/wss4j/config.html

	// the property names ${PREFIX} below is "org.apache.wss4j.crypto". 
	// For Apache WSS4J 1.6.x, the property names ${PREFIX} below is "org.apache.ws.security.crypto". 
	// WSS4J 2.0.0 onwards will also accept the older ${PREFIX} value. The property values for the standard Merlin implementation are as follows:

	public static final String PREFIX = "org.apache.wss4j.crypto.";
	public static final String OLD_PREFIX = "org.apache.ws.security.crypto.";

	// ${PREFIX}.provider - WSS4J specific provider used to create Crypto instances. Defaults to "org.apache.wss4j.common.crypto.Merlin".

	public static final String PROPERTY_PROVIDER = OLD_PREFIX+"provider";
	public static final String PROVIDER_DEFAULT = org.apache.wss4j.common.crypto.Merlin.class.getName();
	public static final String OLD_PROVIDER_DEFAULT = "org.apache.ws.security.components.crypto.Merlin";
	public static final String PROVIDER_GOVWAY = org.openspcoop2.security.keystore.Merlin.class.getName();

	// ${PREFIX}.merlin.x509crl.file - The location of an (X509) CRL file to use.

	public static final String PROPERTY_CRL = Merlin.OLD_PREFIX+Merlin.X509_CRL_FILE;

	
	// Proprietà che permette di fornire la request info di govway

	public static final String PROPERTY_REQUEST_INFO = "requestInfo";
	
	

	// *** Merlin Keystore Properties ***

	public static final String KEYSTORE = "keystore";

	// ${PREFIX}.merlin.keystore.provider - The provider used to load keystores. Defaults to installed provider.

	public static final String PROPERTY_KEYSTORE_PROVIDER = Merlin.OLD_PREFIX+Merlin.CRYPTO_KEYSTORE_PROVIDER;

	//	${PREFIX}.merlin.cert.provider - The provider used to load certificates. Defaults to keystore provider.

	public static final String PROPERTY_CERT_PROVIDER = Merlin.OLD_PREFIX+Merlin.CRYPTO_CERT_PROVIDER;

	//	${PREFIX}.merlin.keystore.file - The location of the keystore

	public static final String PROPERTY_KEYSTORE_PATH = Merlin.OLD_PREFIX+Merlin.OLD_KEYSTORE_FILE; // vecchio modalita' utilizzata in 1.6.x
	public static final String PROPERTY_KEYSTORE_FILE = Merlin.OLD_PREFIX+Merlin.KEYSTORE_FILE;

	//	${PREFIX}.merlin.keystore.password - The password used to load the keystore. Default value is "security".

	public static final String PROPERTY_KEYSTORE_PASSWORD = Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD;

	//	${PREFIX}.merlin.keystore.type - Type of keystore. Defaults to: java.security.KeyStore.getDefaultType())

	public static final String PROPERTY_KEYSTORE_TYPE = Merlin.OLD_PREFIX+Merlin.KEYSTORE_TYPE;

	//	${PREFIX}.merlin.keystore.alias - The default keystore alias to use, if none is specified.

	public static final String PROPERTY_KEYSTORE_ALIAS = Merlin.OLD_PREFIX+Merlin.KEYSTORE_ALIAS;

	//	${PREFIX}.merlin.keystore.private.password - The default password used to load the private key.

	public static final String PROPERTY_KEYSTORE_PRIVATE_PASSWORD = Merlin.OLD_PREFIX+Merlin.KEYSTORE_PRIVATE_PASSWORD;

	// Proprietà che permette di fornire direttamente un keystore utilizzando l'implementazione del PROPERTY_PROVIDER di govway

	public static final String PROPERTY_KEYSTORE_ARCHIVE = Merlin.OLD_PREFIX+KEYSTORE;


	// *** Merlin Truststore Properties ***

	public static final String TRUSTSTORE = "truststore";

	// ${PREFIX}.merlin.load.cacerts - Whether or not to load the CA certs in ${java.home}/lib/security/cacerts (default is false)

	public static final String PROPERTY_LOAD_CA_CERTS = Merlin.OLD_PREFIX+Merlin.LOAD_CA_CERTS;

	// ${PREFIX}.merlin.truststore.file - The location of the truststore
	
	public static final String PROPERTY_TRUSTSTORE_PATH = Merlin.OLD_PREFIX+Merlin.TRUSTSTORE_FILE;

	// ${PREFIX}.merlin.truststore.password - The truststore password. Defaults to "changeit".
	
	public static final String PROPERTY_TRUSTSTORE_PASSWORD = Merlin.OLD_PREFIX+Merlin.TRUSTSTORE_PASSWORD;

	// ${PREFIX}.merlin.truststore.type - The truststore type. Defaults to: java.security.KeyStore.getDefaultType().

	public static final String PROPERTY_TRUSTSTORE_TYPE = Merlin.OLD_PREFIX+Merlin.TRUSTSTORE_TYPE;
	
	// ${PREFIX}.merlin.truststore.provider - WSS4J 2.1.5 The provider used to load truststores. 
	// By default it’s the same as the keystore provider. Set to an empty value to force use of the JRE’s default provider.

	public static final String PROPERTY_TRUSTSTORE_PROVIDER = Merlin.OLD_PREFIX+Merlin.TRUSTSTORE_PROVIDER;

	// Proprietà che permette di fornire direttamente un truststore utilizzando l'implementazione del PROPERTY_PROVIDER di govway
		
	public static final String PROPERTY_TRUSTSTORE_ARCHIVE = Merlin.OLD_PREFIX+TRUSTSTORE;

}
