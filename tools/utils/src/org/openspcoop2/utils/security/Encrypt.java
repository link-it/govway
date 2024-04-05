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

import javax.crypto.spec.IvParameterSpec;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Encrypt extends AbstractCipher {


	public Encrypt(byte[] secretKey, String algorithm) {
		super(javax.crypto.Cipher.ENCRYPT_MODE, secretKey, algorithm);
	}
	public Encrypt(byte[] secretKey, String algorithm, IvParameterSpec ivParameterSpec) {
		super(javax.crypto.Cipher.ENCRYPT_MODE, secretKey, algorithm, ivParameterSpec);
	}
	public Encrypt(byte[] secretKey, String algorithm, byte[] ivParameterSpec) {
		super(javax.crypto.Cipher.ENCRYPT_MODE, secretKey, algorithm, ivParameterSpec);
	}

	public Encrypt(Certificate certificate) {
		super(javax.crypto.Cipher.ENCRYPT_MODE, certificate);
	}

	public Encrypt(Key key) {
		super(javax.crypto.Cipher.ENCRYPT_MODE, key);
	}
	public Encrypt(Key key, IvParameterSpec ivParameterSpec) {
		super(javax.crypto.Cipher.ENCRYPT_MODE, key, ivParameterSpec);
	}
	public Encrypt(Key key, byte[] ivParameterSpec) {
		super(javax.crypto.Cipher.ENCRYPT_MODE, key, ivParameterSpec);
	}

	public Encrypt(KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException {
		super(javax.crypto.Cipher.ENCRYPT_MODE, keystore, alias, passwordPrivateKey);
	}

	public Encrypt(KeyStore keystore, String alias) throws UtilsException {
		super(javax.crypto.Cipher.ENCRYPT_MODE, keystore, alias);
	}

	public Encrypt(KeyStore keystore) throws UtilsException {
		super(javax.crypto.Cipher.ENCRYPT_MODE, keystore);
	}

	public byte[] encrypt(String data, String charsetName, String algorithm) throws UtilsException{
		return super.process(data, charsetName, algorithm);
	}
	public byte[] encrypt(byte[] data, String algorithm) throws UtilsException{
		return super.process(data, algorithm);
	}
	
	public byte[] encryptBase64(String data, String charsetName, String algorithm) throws UtilsException{
		return Base64Utilities.encode(super.process(data, charsetName, algorithm));
	}
	public byte[] encryptBase64(byte[] data, String algorithm) throws UtilsException{
		return Base64Utilities.encode(super.process(data, algorithm));
	}
	
	public String encryptBase64AsString(String data, String charsetName, String algorithm) throws UtilsException{
		return Base64Utilities.encodeAsString(super.process(data, charsetName, algorithm));
	}
	public String encryptBase64AsString(byte[] data, String algorithm) throws UtilsException{
		return Base64Utilities.encodeAsString(super.process(data, algorithm));
	}
	
	public char[] encryptHexBinary(String data, String charsetName, String algorithm) throws UtilsException{
		return HexBinaryUtilities.encode(super.process(data, charsetName, algorithm));
	}
	public char[] encryptHexBinary(byte[] data, String algorithm) throws UtilsException{
		return HexBinaryUtilities.encode(super.process(data, algorithm));
	}
	
	public String encryptHexBinaryAsString(String data, String charsetName, String algorithm) throws UtilsException{
		return HexBinaryUtilities.encodeAsString(super.process(data, charsetName, algorithm));
	}
	public String encryptHexBinaryAsString(byte[] data, String algorithm) throws UtilsException{
		return HexBinaryUtilities.encodeAsString(super.process(data, algorithm));
	}
}
