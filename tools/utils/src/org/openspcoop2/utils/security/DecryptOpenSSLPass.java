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

import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.SymmetricKeyUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DecryptOpenSSLPass extends AbstractCipher {

	/**
	  * 
	  * Openssl encrypts data using the following steps:
	  * 1. bytes = cipherText
	  * 2. salt = bytes[8,16)
	  * 3. key = messageDigest("sha256", password+salt)
	  * 4. iv = messageDigest(key+password+salt)[0,16)
	  * 5. plainText = decrypt("aes256cbc", key, iv, bytes[16..end])
	*/

	public static CipherInfo buildCipherInfo(byte[] cipherBytes, String password, String digestAlgoParam, OpenSSLEncryptionMode mode) throws UtilsException {
		
		CipherInfo cipherInfo = new CipherInfo();
		
		cipherInfo.setSalt(buildSalt(cipherBytes));
		
		cipherInfo.setEncodedKey(EncryptOpenSSLPass.buildSecretKey(password, cipherInfo.getSalt(), digestAlgoParam, mode));
		cipherInfo.setKey(new SecretKeySpec(cipherInfo.getEncodedKey(), SymmetricKeyUtils.ALGO_AES));
		
		cipherInfo.setIv(EncryptOpenSSLPass.buildIV(password, cipherInfo.getSalt(), cipherInfo.getEncodedKey(), digestAlgoParam));
		cipherInfo.setIvParameterSpec(EncryptOpenSSLPass.convertTo(cipherInfo.getIv()));
		
		return cipherInfo;
	}
	static byte[] buildSalt(byte[] cipherBytes) throws UtilsException {
		try {				 
			return Arrays.copyOfRange(cipherBytes, 8, 16);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] extractCipherBytes(byte[] data) {
		return Arrays.copyOfRange(
				data, 16, data.length);
	}
	
	
	private OpenSSLEncryptionMode mode;
	private String password;
	
	public DecryptOpenSSLPass(String password) {
		this(password, null);
	}
	public DecryptOpenSSLPass(String password, OpenSSLEncryptionMode modeParam) {
		super(javax.crypto.Cipher.DECRYPT_MODE);
		this.mode = modeParam!=null ? modeParam : OpenSSLEncryptionMode.AES_256_CBC;
		this.password = password;
	}


	public byte[] decrypt(byte[] data) throws UtilsException{
		CipherInfo cipherInfo = buildCipherInfo(data, this.password, null, this.mode);
		this.key = cipherInfo.getKey();
		this.ivParameterSpec = cipherInfo.getIvParameterSpec();
		byte[] cipherBytes = extractCipherBytes(data);
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
