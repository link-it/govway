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

package org.openspcoop2.utils.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.certificate.byok.BYOKCostanti;

/**	
 * OpenSSLEncryptionMode
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum OpenSSLEncryptionMode implements Serializable {

	AES_128_CBC, AES_192_CBC, AES_256_CBC;
	
	public static OpenSSLEncryptionMode toMode(String mode) {
		return localPwdTypeToOpenSSLEncryptionModeMap.get(mode);
	}
	
	private static final Map<String, OpenSSLEncryptionMode> localPwdTypeToOpenSSLEncryptionModeMap = new HashMap<>();
	static {
		/**localPwdTypeToOpenSSLEncryptionModeMap.put(BYOKCostanti.PROPERTY_LOCAL_PWD_TYPE_OPENSSL_AES_128_CBC, OpenSSLEncryptionMode.AES_128_CBC);
		localPwdTypeToOpenSSLEncryptionModeMap.put(BYOKCostanti.PROPERTY_LOCAL_PWD_TYPE_OPENSSL_AES_192_CBC, OpenSSLEncryptionMode.AES_192_CBC);*/
		localPwdTypeToOpenSSLEncryptionModeMap.put(BYOKCostanti.PROPERTY_LOCAL_PW_TYPE_OPENSSL_AES_256_CBC, OpenSSLEncryptionMode.AES_256_CBC);
		localPwdTypeToOpenSSLEncryptionModeMap.put(BYOKCostanti.PROPERTY_LOCAL_PW_TYPE_OPENSSL_PBKDF2_AES_128_CBC, OpenSSLEncryptionMode.AES_128_CBC);
		localPwdTypeToOpenSSLEncryptionModeMap.put(BYOKCostanti.PROPERTY_LOCAL_PW_TYPE_OPENSSL_PBKDF2_AES_192_CBC, OpenSSLEncryptionMode.AES_192_CBC);
		localPwdTypeToOpenSSLEncryptionModeMap.put(BYOKCostanti.PROPERTY_LOCAL_PW_TYPE_OPENSSL_PBKDF2_AES_256_CBC, OpenSSLEncryptionMode.AES_256_CBC);
	}
	
}
