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

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.SymmetricKeyUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.openspcoop2.utils.random.RandomUtilities;

/**	
 * EncryptOpenSSLPassw
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EncryptOpenSSLPass extends AbstractCipher {

	 /**
	  * 
	  * Openssl encrypts data using the following steps:
	  * 1. salt = 8-byte cryptographically-strong random number
	  * 2. key = messageDigest("sha256", password+salt)
	  * 3. iv = messageDigest(key+password+salt)[0,16)
	  * 4. cipherTextRaw = encrypt("aes256cbc", key, iv, textPlain)
	  * 5. cipherText = "Salted__"+salt+cipherTextRaw
    */
	public static CipherInfo buildCipherInfo(String password, String digestAlgoParam, OpenSSLEncryptionMode mode) throws UtilsException {
		
		CipherInfo cipherInfo = new CipherInfo();
		
		cipherInfo.setSalt(buildSalt());
		
		cipherInfo.setEncodedKey(buildSecretKey(password, cipherInfo.getSalt(), digestAlgoParam, mode));
		cipherInfo.setKey(new SecretKeySpec(cipherInfo.getEncodedKey(), SymmetricKeyUtils.ALGO_AES));
		
		cipherInfo.setIv(buildIV(password, cipherInfo.getSalt(), cipherInfo.getEncodedKey(), digestAlgoParam));
		cipherInfo.setIvParameterSpec(convertTo(cipherInfo.getIv()));
		
		return cipherInfo;
	}
	static byte[] buildSalt() throws UtilsException {
		try {				 
			// Create salt
			byte[] salt = new byte[8];
			RandomUtilities.getSecureRandom().nextBytes(salt);
			return salt;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	static byte[] buildSecretKey(String password, byte[] salt, String digestAlgoParam, OpenSSLEncryptionMode modeParam) throws UtilsException {
		try {
			// Create key
			byte[] secretKeyClear = password.getBytes();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(secretKeyClear);
			bout.write(salt);
			bout.flush();
			bout.close();
			byte[]passAndSalt = bout.toByteArray();
			
			String digestAlgo = digestAlgoParam;
			if(digestAlgoParam==null || StringUtils.isEmpty(digestAlgoParam)) {
				digestAlgo = "SHA-256";
			}
			MessageDigest md = MessageDigest.getInstance(digestAlgo);
			byte[] key = md.digest(passAndSalt);
			OpenSSLEncryptionMode mode = modeParam!=null ? modeParam : OpenSSLEncryptionMode.AES_256_CBC;
			switch (mode) {
			case AES_128_CBC:
				key = Arrays.copyOf(key, 16); // AES-128 richiede una chiave di 128 bit (16 byte).
				break;
			case AES_192_CBC:
				key = Arrays.copyOf(key, 24); // AES-192 richiede una chiave di 192 bit (24 byte).
				break;
			case AES_256_CBC:
				key = Arrays.copyOf(key, 32); // AES-256 richiede una chiave di 256 bit (32 byte). 
				break;
			} 
			return key;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	static byte[] buildIV(String password, byte[] salt, byte[]encodedKey, String digestAlgoParam) throws UtilsException {
		try {
			String digestAlgo = digestAlgoParam;
			if(digestAlgoParam==null || StringUtils.isEmpty(digestAlgoParam)) {
				digestAlgo = "SHA-256";
			}
			MessageDigest md = MessageDigest.getInstance(digestAlgo);
			
			// Derive IV
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(encodedKey);
			bout.write(password.getBytes());
			bout.write(salt);
			bout.flush();
			bout.close();
			byte[] keyAndPassAndSalt = bout.toByteArray();
					 
			return Arrays.copyOfRange( md.digest(keyAndPassAndSalt), 0, 16);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	static IvParameterSpec convertTo(byte[] iv) {
		return new IvParameterSpec(iv);
	}

	public static byte[] formatOutput(byte [] salt, byte [] cipherText) throws UtilsException {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.writeBytes("Salted__".getBytes());
			bos.writeBytes(salt);
			bos.writeBytes(cipherText);
			bos.flush();
			bos.close();
			return bos.toByteArray();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	private static final String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding";
	
	
	private CipherInfo cipherInfo;
	private OpenSSLEncryptionMode mode;
	
	public EncryptOpenSSLPass(String password) throws UtilsException {
		this(password, null);
	}
	public EncryptOpenSSLPass(String password, OpenSSLEncryptionMode modeParam) throws UtilsException {
		super(javax.crypto.Cipher.ENCRYPT_MODE);
		this.mode = modeParam!=null ? modeParam : OpenSSLEncryptionMode.AES_256_CBC;
		this.cipherInfo = buildCipherInfo(password, null, this.mode);
		this.key = this.cipherInfo.getKey();
		this.ivParameterSpec = this.cipherInfo.getIvParameterSpec();
	}
	
	
	static String getAlgorithm(OpenSSLEncryptionMode mode) throws UtilsException {
		switch (mode) {
		case AES_128_CBC:
		case AES_192_CBC:
		case AES_256_CBC:
			return AES_CBC_PKCS5PADDING;
		} 
		throw new UtilsException("Unsupported mode");
	}
	
	public byte[] encrypt(String data, String charsetName) throws UtilsException{
		return formatOutput(this.cipherInfo.getSalt(), super.process(data, charsetName, getAlgorithm(this.mode)));
	}
	public byte[] encrypt(byte[] data) throws UtilsException{
		return formatOutput(this.cipherInfo.getSalt(), super.process(data, getAlgorithm(this.mode)));
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
