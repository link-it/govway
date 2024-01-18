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

package org.openspcoop2.utils.test.security;

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestEncrypt
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestEncrypt {

	/* !!NOTA!!: 
	 * l'esecuzione dei test P11 richiedono la configurazione descritta in org/openspcoop2/utils/certificate/hsm/HSM.example 
	 * Deve inoltre essere impostata la variabile di sistema (utilizzando path assoluti!!!):
	 * export SOFTHSM2_CONF=DIRECTORY_SCELTA_FASE_INSTALLAZIONE/lib/softhsm/softhsm2.conf
	 */
	
	private static final String ID_TEST = "Encrypt";
	
	@DataProvider(name="encryptProvider")
	public Object[][] provider(){
		return new Object[][]{
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JAVA_ENCRYPT_JKS, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JAVA_ENCRYPT_PKCS12, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JAVA_ENCRYPT_PKCS11, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JAVA_ENCRYPT_PKCS11, true},
				
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.XML_ENCRYPT_JKS, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.XML_ENCRYPT_PKCS12, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.XML_ENCRYPT_PKCS11, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.XML_ENCRYPT_PKCS11, true},
				
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_JKS, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11, true},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_JWK, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC, false},
				
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12_HEADER_CUSTOM, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM, true},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_JCEKS_HEADER_CUSTOM, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_SYMMETRIC_HEADER_CUSTOM, false},
				
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_JKS_HEADER_CUSTOM_KID_ONLY, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS12_HEADER_CUSTOM_KID_ONLY, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM_KID_ONLY, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_PKCS11_HEADER_CUSTOM_KID_ONLY, true},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_JWK_HEADER_CUSTOM_KID_ONLY, false},
				
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_JKS_KEYSTORE, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_JKS_KEYSTORE_HEADER_CUSTOM, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PKCS12_KEYSTORE, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PKCS12_KEYSTORE_HEADER_CUSTOM, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE_HEADER_CUSTOM, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE, true},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE_HEADER_CUSTOM, true},
				
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_JKS_KEYSTORE_JCE, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_JKS_KEYSTORE_JCE_HEADER_CUSTOM, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PKCS12_KEYSTORE_JCE, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PKCS12_KEYSTORE_JCE_HEADER_CUSTOM, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE_JCE, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE_JCE_HEADER_CUSTOM, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE_JCE, true},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PKCS11_KEYSTORE_JCE_HEADER_CUSTOM, true},
				
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_JWK_KEYS, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_JWK_KEYS_HEADER_CUSTOM, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_JWK_KEY, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_JWK_KEY_HEADER_CUSTOM, false},
				
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEYS, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEYS_HEADER_CUSTOM, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEY, false},
				{org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_JWK_SYMMETRIC_KEY_HEADER_CUSTOM, false}
		};
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST},dataProvider="encryptProvider")
	public void testEncrypt(org.openspcoop2.utils.security.test.EncryptTest.TipoTest tipo, boolean useP11asTrustStore) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.security.test.EncryptTest.main(new String [] { tipo.name(), useP11asTrustStore+"" } );
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
	
}
