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

import java.security.SecureRandom;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.openspcoop2.utils.random.RandomUtilities;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractCipher {

	private java.security.Key key;
	private IvParameterSpec ivParameterSpec;
	private java.security.cert.Certificate certificate;
	private int mode;
		
	protected AbstractCipher(int mode, java.security.Key key) {
		this.mode = mode;
		this.key = key;
	}
	protected AbstractCipher(int mode, java.security.Key key, IvParameterSpec ivParameterSpec) {
		this.mode = mode;
		this.key = key;
		this.ivParameterSpec = ivParameterSpec;
	}
	protected AbstractCipher(int mode, java.security.Key key, byte[] ivParameterSpec) {
		this.mode = mode;
		this.key = key;
		if(ivParameterSpec!=null && ivParameterSpec.length>0) {
			this.ivParameterSpec = new IvParameterSpec(ivParameterSpec);
		}
	}
	
	protected AbstractCipher(int mode, KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException{
		this.mode = mode;
		this.key = keystore.getPrivateKey(alias, passwordPrivateKey);
	}
	
	protected AbstractCipher(int mode, byte[] secretKey, String algorithm) {
		this.mode = mode;
		this.key = new SecretKeySpec(secretKey, algorithm); // cifratura simmetrica
	}
	protected AbstractCipher(int mode, byte[] secretKey, String algorithm, IvParameterSpec ivParameterSpec) {
		this.mode = mode;
		this.key = new SecretKeySpec(secretKey, algorithm); // cifratura simmetrica
		this.ivParameterSpec = ivParameterSpec;
	}
	protected AbstractCipher(int mode, byte[] secretKey, String algorithm, byte[] ivParameterSpec) {
		this.mode = mode;
		this.key = new SecretKeySpec(secretKey, algorithm); // cifratura simmetrica
		if(ivParameterSpec!=null && ivParameterSpec.length>0) {
			this.ivParameterSpec = new IvParameterSpec(ivParameterSpec);
		}
	}
	
	protected AbstractCipher(int mode, java.security.cert.Certificate certificate) {
		this.mode = mode;
		this.certificate = certificate;
	}
	protected AbstractCipher(int mode, KeyStore keystore, String alias) throws UtilsException{
		this.mode = mode;
		this.certificate = keystore.getCertificate(alias);
	}
	protected AbstractCipher(int mode, KeyStore keystore) throws UtilsException{
		this.mode = mode;
		this.certificate = keystore.getCertificate();
	}
	
	
	protected byte[] process(String data, String charsetName, String algorithm) throws UtilsException{
		try{
			return this.process(data.getBytes(charsetName), algorithm);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	protected byte[] process(byte[] data, String algorithm) throws UtilsException{
		try{
			javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(algorithm);
			if(this.key!=null){
				if(this.ivParameterSpec!=null) {
					cipher.init(this.mode, this.key, this.ivParameterSpec);
				}
				else {
					cipher.init(this.mode, this.key);
				}
			}
			else{
				cipher.init(this.mode, this.certificate);
			}
			return cipher.doFinal(data);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

	public void initIV(String algorithm) throws UtilsException{
		try{
			javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(algorithm);
			// Genera un vettore di inizializzazione (IV) casuale
	        byte[] ivBytes = new byte[cipher.getBlockSize()];
	        SecureRandom random = RandomUtilities.getSecureRandom();
	        random.nextBytes(ivBytes);
	        this.ivParameterSpec = new IvParameterSpec(ivBytes);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public byte[] getIV() {
		return this.ivParameterSpec.getIV();
	}
	
	public byte[] getIVBase64() {
		return Base64Utilities.encode(getIV());
	}
	public String getIVBase64AsString() {
		return Base64Utilities.encodeAsString(getIV());
	}
	
	public char[] getIVHexBinary() throws UtilsException{
		return HexBinaryUtilities.encode(getIV());
	}	
	public String getIVHexBinaryAsString() throws UtilsException{
		return HexBinaryUtilities.encodeAsString(getIV());
	}

}
