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

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DecryptOpenSSLPassPBKDF2 extends AbstractCipher {

	/**
	  * 
	  * Openssl encrypts data using the following steps:
	  * 1. bytes = cipherText
	  * 2. salt = bytes[8,16)
	  * 3. key = PBKDF2(password+salt)
	  * 4. iv = derivated with PBKDF2
	  * 5. plainText = decrypt("aes256cbc", key, iv, bytes[16..end])
	*/

	public static CipherInfo buildCipherInfo(byte[] cipherBytes, String password, Integer iterationCount, OpenSSLEncryptionMode mode) throws UtilsException {
		
		CipherInfo cipherInfo = new CipherInfo();
		
		cipherInfo.setSalt(DecryptOpenSSLPass.buildSalt(cipherBytes));
		
		EncryptOpenSSLPassPBKDF2.buildSecretKeyAndIV(password, cipherInfo.getSalt(), iterationCount, mode, cipherInfo);
		
		return cipherInfo;
	}
	
	private OpenSSLEncryptionMode mode;
	private String password;
	private Integer iterationCount;
	
	public DecryptOpenSSLPassPBKDF2(String password) {
		this(password, null, null);
	}
	public DecryptOpenSSLPassPBKDF2(String password, Integer iterationCount) {
		this(password, iterationCount, null);
	}
	public DecryptOpenSSLPassPBKDF2(String password, OpenSSLEncryptionMode modeParam) {
		this(password, null, modeParam);
	}
	public DecryptOpenSSLPassPBKDF2(String password, Integer iterationCount, OpenSSLEncryptionMode modeParam) {
		super(javax.crypto.Cipher.DECRYPT_MODE);
		this.mode = modeParam!=null ? modeParam : OpenSSLEncryptionMode.AES_256_CBC;
		this.password = password;
		this.iterationCount = iterationCount;
	}


	public byte[] decrypt(byte[] data) throws UtilsException{
		CipherInfo cipherInfo = buildCipherInfo(data, this.password, this.iterationCount, this.mode);
		this.key = cipherInfo.getKey();
		this.ivParameterSpec = cipherInfo.getIvParameterSpec();
		byte[] cipherBytes = DecryptOpenSSLPass.extractCipherBytes(data);
		return super.process(cipherBytes, EncryptOpenSSLPass.getAlgorithm(this.mode));
	}
	
	public byte[] decryptBase64(byte[] data) throws UtilsException{
		return this.decrypt(Base64Utilities.decode(data));
	}
	
	public byte[] decryptBase64(String data) throws UtilsException{
		return this.decrypt(Base64Utilities.decode(data));
	}
	
	public byte[] decryptHexBinary(char[] data) throws UtilsException{
		return this.decrypt(HexBinaryUtilities.decode(data));
	}
	
	public byte[] decryptHexBinary(String data) throws UtilsException{
		return this.decrypt(HexBinaryUtilities.decode(data));
	}
	
	
	@Override
	public void initIV(String algorithm) throws UtilsException{
		// NOP
		// Non deve fare nulla questa chiamata, viene gestita dalla funzione sopra l'IV
	}
}
