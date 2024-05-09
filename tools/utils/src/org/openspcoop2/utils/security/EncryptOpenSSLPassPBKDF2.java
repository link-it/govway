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

import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.SymmetricKeyUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;

/**	
 * EncryptOpenSSLPassw
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EncryptOpenSSLPassPBKDF2 extends AbstractCipher {

	 /**
	  * 
	  * Openssl encrypts data using the following steps:
	  * 1. salt = <8-byte cryptographically-strong random number>
	  * 2. key = PBKDF2(password+salt)
	  * 3. iv = derivated with PBKDF2
	  * 4. cipherTextRaw = encrypt("aes256cbc", key, iv, textPlain)
	  * 5. cipherText = "Salted__"+salt+cipherTextRaw
    */
	private static CipherInfo buildCipherInfo(String password, Integer iterationCount, OpenSSLEncryptionMode mode) throws UtilsException {
		
		CipherInfo cipherInfo = new CipherInfo();
		
		cipherInfo.setSalt(EncryptOpenSSLPass.buildSalt());
		
		buildSecretKeyAndIV(password, cipherInfo.getSalt(), iterationCount, mode, cipherInfo);
		
		return cipherInfo;
	}
	static void buildSecretKeyAndIV(String password, byte[] salt, Integer iterationCountParam, OpenSSLEncryptionMode modeParam, CipherInfo cipherInfo) throws UtilsException {
		try {
			int keylen = -1;
			int ivlen = 16;
			OpenSSLEncryptionMode mode = modeParam!=null ? modeParam : OpenSSLEncryptionMode.AES_256_CBC;
			switch (mode) {
			case AES_128_CBC:
				keylen = 16; // AES-128 richiede una chiave di 128 bit (16 byte).
				break;
			case AES_192_CBC:
				keylen = 24;// AES-192 richiede una chiave di 192 bit (24 byte).
				break;
			case AES_256_CBC:
				keylen = 32; // AES-256 richiede una chiave di 256 bit (32 byte). 
				break;
			} 
			
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        int iterationCount = iterationCountParam == null || iterationCountParam.intValue()<=0 ? 10000 : iterationCountParam.intValue();
	        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, (keylen+ivlen)*8); 
	        SecretKey tmp = factory.generateSecret(spec);
	        byte[] keyandIV = tmp.getEncoded();
	        
	        SecretKey secretKey = new SecretKeySpec(keyandIV,0,keylen,SymmetricKeyUtils.ALGO_AES);
	        cipherInfo.setKey(secretKey);
	        cipherInfo.setEncodedKey(secretKey.getEncoded());
	        
	        // Derive IV using PBKDF2
	        IvParameterSpec iv = convertTo(keyandIV,keylen,ivlen);
	        cipherInfo.setIv(iv.getIV());
	        cipherInfo.setIvParameterSpec(iv);
	        
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	static IvParameterSpec convertTo(byte[] keyandIV, int keylen, int ivlen) {
		return new IvParameterSpec(keyandIV,keylen,ivlen);
	}
	

	private CipherInfo cipherInfo;
	private OpenSSLEncryptionMode mode;
	
	public EncryptOpenSSLPassPBKDF2(String password) throws UtilsException {
		this(password, null, null);
	}
	public EncryptOpenSSLPassPBKDF2(String password, Integer iterationCount) throws UtilsException {
		this(password, iterationCount, null);
	}
	public EncryptOpenSSLPassPBKDF2(String password, OpenSSLEncryptionMode modeParam) throws UtilsException {
		this(password, null, modeParam);
	}
	public EncryptOpenSSLPassPBKDF2(String password, Integer iterationCount, OpenSSLEncryptionMode modeParam) throws UtilsException {
		super(javax.crypto.Cipher.ENCRYPT_MODE);
		this.mode = modeParam!=null ? modeParam : OpenSSLEncryptionMode.AES_256_CBC;
		this.cipherInfo = buildCipherInfo(password, iterationCount, this.mode);
		this.key = this.cipherInfo.getKey();
		this.ivParameterSpec = this.cipherInfo.getIvParameterSpec();
	}
	
	
	public byte[] encrypt(String data, String charsetName) throws UtilsException{
		return EncryptOpenSSLPass.formatOutput(this.cipherInfo.getSalt(), super.process(data, charsetName, EncryptOpenSSLPass.getAlgorithm(this.mode)));
	}
	public byte[] encrypt(byte[] data) throws UtilsException{
		return EncryptOpenSSLPass.formatOutput(this.cipherInfo.getSalt(), super.process(data, EncryptOpenSSLPass.getAlgorithm(this.mode)));
	}
	
	public byte[] encryptBase64(String data, String charsetName) throws UtilsException{
		return Base64Utilities.encode(this.encrypt(data, charsetName));
	}
	public byte[] encryptBase64(byte[] data) throws UtilsException{
		return Base64Utilities.encode(this.encrypt(data));
	}
	
	public String encryptBase64AsString(String data, String charsetName) throws UtilsException{
		return Base64Utilities.encodeAsString(this.encrypt(data, charsetName));
	}
	public String encryptBase64AsString(byte[] data) throws UtilsException{
		return Base64Utilities.encodeAsString(this.encrypt(data));
	}
	
	public char[] encryptHexBinary(String data, String charsetName) throws UtilsException{
		return HexBinaryUtilities.encode(this.encrypt(data, charsetName));
	}
	public char[] encryptHexBinary(byte[] data) throws UtilsException{
		return HexBinaryUtilities.encode(this.encrypt(data));
	}
	
	public String encryptHexBinaryAsString(String data, String charsetName) throws UtilsException{
		return HexBinaryUtilities.encodeAsString(this.encrypt(data, charsetName));
	}
	public String encryptHexBinaryAsString(byte[] data) throws UtilsException{
		return HexBinaryUtilities.encodeAsString(this.encrypt(data));
	}
	
	@Override
	public void initIV(String algorithm) throws UtilsException{
		// NOP
		// Non deve fare nulla questa chiamata, viene gestita dalla funzione sopra l'IV
	}
}
