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

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.SymmetricKeyUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;

/**	
 * DecryptWrapKey
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DecryptWrapKey  {

	private java.security.Key key;
	private java.security.cert.Certificate certificate;
	
	private byte[] wrappedKey;

	public DecryptWrapKey(Certificate certificate) {
		this.certificate = certificate;
	}

	public DecryptWrapKey(Key key) {
		this.key = key;
	}

	public DecryptWrapKey(KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException {
		this.key = keystore.getPrivateKey(alias, passwordPrivateKey);
	}

	public DecryptWrapKey(KeyStore keystore, String alias) throws UtilsException {
		this.certificate = keystore.getCertificate(alias);
	}

	public DecryptWrapKey(KeyStore keystore) throws UtilsException {
		this.certificate = keystore.getCertificate();
	}
	
	
	protected byte[] process(String data, String charsetName, 
			byte[] wrappedKey, byte[] iv, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		try{
			return this.process(data.getBytes(charsetName), 
					wrappedKey, iv, wrappedKeyAlgorithm, contentAlgorithm);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	protected byte[] process(byte[] data, 
			byte[] wrappedKey, byte[] iv, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		try{			
			// operazione di wrapping con la chiave asincrona
            Cipher cipher = Cipher.getInstance(wrappedKeyAlgorithm);
            if(this.certificate!=null) {
            	cipher.init(Cipher.UNWRAP_MODE, this.certificate);
            }
            else {
            	cipher.init(Cipher.UNWRAP_MODE, this.key);
            }

            // Esegue l'operazione di wrapping della chiave simmetrica con la chiave pubblica RSA
            Key secretKey = cipher.unwrap(wrappedKey, SymmetricKeyUtils.ALGO_AES, Cipher.SECRET_KEY);
            
            // Decifro 
            Decrypt decrypt = new Decrypt(secretKey, iv);
			
			return decrypt.decrypt(data, contentAlgorithm);
			
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
	
	
	
	
	public byte[] decrypt(String data, byte[] wrappedKey, byte[] iv, String charsetName, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return this.process(data, charsetName, wrappedKey, iv, wrappedKeyAlgorithm, contentAlgorithm);
	}
	public byte[] decrypt(byte[] data, byte[] wrappedKey, byte[] iv, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return this.process(data, wrappedKey, iv, wrappedKeyAlgorithm, contentAlgorithm);
	}
	
	public byte[] decryptBase64(String data, byte[] wrappedKey, byte[] iv, String charsetName, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return Base64Utilities.encode(this.process(data, charsetName, wrappedKey, iv, wrappedKeyAlgorithm, contentAlgorithm));
	}
	public byte[] decryptBase64(byte[] data, byte[] wrappedKey, byte[] iv, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return Base64Utilities.encode(this.process(data, wrappedKey, iv, wrappedKeyAlgorithm, contentAlgorithm));
	}
	
	public String decryptBase64AsString(String data, byte[] wrappedKey, byte[] iv, String charsetName, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return Base64Utilities.encodeAsString(this.process(data, charsetName, wrappedKey, iv, wrappedKeyAlgorithm, contentAlgorithm));
	}
	public String decryptBase64AsString(byte[] data, byte[] wrappedKey, byte[] iv, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return Base64Utilities.encodeAsString(this.process(data, wrappedKey, iv, wrappedKeyAlgorithm, contentAlgorithm));
	}
	
	public char[] decryptHexBinary(String data, byte[] wrappedKey, byte[] iv, String charsetName, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return HexBinaryUtilities.encode(this.process(data, charsetName, wrappedKey, iv, wrappedKeyAlgorithm, contentAlgorithm));
	}
	public char[] decryptHexBinary(byte[] data, byte[] wrappedKey, byte[] iv, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return HexBinaryUtilities.encode(this.process(data, wrappedKey, iv, wrappedKeyAlgorithm, contentAlgorithm));
	}
	
	public String decryptHexBinaryAsString(String data, byte[] wrappedKey, byte[] iv, String charsetName, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return HexBinaryUtilities.encodeAsString(this.process(data, charsetName, wrappedKey, iv, wrappedKeyAlgorithm, contentAlgorithm));
	}
	public String decryptHexBinaryAsString(byte[] data, byte[] wrappedKey, byte[] iv, String wrappedKeyAlgorithm, String contentAlgorithm) throws UtilsException{
		return HexBinaryUtilities.encodeAsString(this.process(data, wrappedKey, iv, wrappedKeyAlgorithm, contentAlgorithm));
	}
}
