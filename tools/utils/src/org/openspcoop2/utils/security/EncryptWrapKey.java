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

import java.security.Key;
import java.security.cert.Certificate;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.SymmetricKeyUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;

/**	
 * EncryptWrapKey
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EncryptWrapKey  {

	private java.security.Key key;
	private java.security.cert.Certificate certificate;
	
	private byte[] wrappedKey;
	private byte[] iv;

	public EncryptWrapKey(Certificate certificate) {
		this.certificate = certificate;
	}

	public EncryptWrapKey(Key key) {
		this.key = key;
	}

	public EncryptWrapKey(KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException {
		this.key = keystore.getPrivateKey(alias, passwordPrivateKey);
	}

	public EncryptWrapKey(KeyStore keystore, String alias) throws UtilsException {
		this.certificate = keystore.getCertificate(alias);
	}

	public EncryptWrapKey(KeyStore keystore) throws UtilsException {
		this.certificate = keystore.getCertificate();
	}
	
	
	protected byte[] process(String data, String charsetName, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		try{
			return this.process(data.getBytes(charsetName), wrappedKeyAlgorithm, contentAlgorithm);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	protected byte[] process(byte[] data, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		try{
			int symmetricKeySize = 256;
			if(wrappedKeyAlgorithm.contains("128")) {
				symmetricKeySize = 128;
			}
			else if(wrappedKeyAlgorithm.contains("192")) {
				symmetricKeySize = 192;
			}
			
			// Genero chiave simmetrica
			KeyGenerator keyGen = KeyGenerator.getInstance(SymmetricKeyUtils.ALGO_AES);
	        keyGen.init(symmetricKeySize);
	        SecretKey secretKey = keyGen.generateKey();
			
			// operazione di wrapping con la chiave asincrona
            Cipher cipher = Cipher.getInstance(wrappedKeyAlgorithm);
            if(this.certificate!=null) {
            	cipher.init(Cipher.WRAP_MODE, this.certificate);
            }
            else {
            	cipher.init(Cipher.WRAP_MODE, this.key);
            }

            // Esegue l'operazione di wrapping della chiave simmetrica con la chiave pubblica RSA
            this.wrappedKey = cipher.wrap(secretKey);
            
            // Cifro 
            Encrypt encrypt = new Encrypt(secretKey);
            encrypt.initIV(contentAlgorithm);
            this.iv = encrypt.getIV();
			
			return encrypt.encrypt(data, contentAlgorithm);
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public String getWrappedKeyBase64() {
		return Base64Utilities.encodeAsString(this.wrappedKey);
	}
	public String getWrappedKeyHexBinary() throws UtilsException {
		return HexBinaryUtilities.encodeAsString(this.wrappedKey);
	}
	public byte[] getWrappedKey() {
		return this.wrappedKey;
	}
	
	public byte[] getIV() {
		return this.iv;
	}
	public byte[] getIVBase64() {
		return Base64Utilities.encode(this.iv);
	}
	public String getIVBase64AsString() {
		return Base64Utilities.encodeAsString(this.iv);
	}
	public char[] getIVHexBinary() throws UtilsException{
		return HexBinaryUtilities.encode(this.iv);
	}	
	public String getIVHexBinaryAsString() throws UtilsException{
		return HexBinaryUtilities.encodeAsString(this.iv);
	}
	
	
	public byte[] encrypt(String data, String charsetName, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return this.process(data, charsetName, wrappedKeyAlgorithm, contentAlgorithm);
	}
	public byte[] encrypt(byte[] data, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return this.process(data, wrappedKeyAlgorithm, contentAlgorithm);
	}
	
	public byte[] encryptBase64(String data, String charsetName, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return Base64Utilities.encode(this.process(data, charsetName, wrappedKeyAlgorithm, contentAlgorithm));
	}
	public byte[] encryptBase64(byte[] data, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return Base64Utilities.encode(this.process(data, wrappedKeyAlgorithm, contentAlgorithm));
	}
	
	public String encryptBase64AsString(String data, String charsetName, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return Base64Utilities.encodeAsString(this.process(data, charsetName, wrappedKeyAlgorithm, contentAlgorithm));
	}
	public String encryptBase64AsString(byte[] data, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return Base64Utilities.encodeAsString(this.process(data, wrappedKeyAlgorithm, contentAlgorithm));
	}
	
	public char[] encryptHexBinary(String data, String charsetName, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return HexBinaryUtilities.encode(this.process(data, charsetName, wrappedKeyAlgorithm, contentAlgorithm));
	}
	public char[] encryptHexBinary(byte[] data, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return HexBinaryUtilities.encode(this.process(data, wrappedKeyAlgorithm, contentAlgorithm));
	}
	
	public String encryptHexBinaryAsString(String data, String charsetName, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return HexBinaryUtilities.encodeAsString(this.process(data, charsetName, wrappedKeyAlgorithm, contentAlgorithm));
	}
	public String encryptHexBinaryAsString(byte[] data, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return HexBinaryUtilities.encodeAsString(this.process(data, wrappedKeyAlgorithm, contentAlgorithm));
	}
}
