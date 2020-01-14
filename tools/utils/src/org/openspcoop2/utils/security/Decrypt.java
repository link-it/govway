/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
public class Decrypt extends AbstractCipher {


	public Decrypt(byte[] secretKey, String algorithm) throws UtilsException {
		super(javax.crypto.Cipher.DECRYPT_MODE, secretKey, algorithm);
	}

	public Decrypt(Certificate certificate) throws UtilsException {
		super(javax.crypto.Cipher.DECRYPT_MODE, certificate);
	}

	public Decrypt(Key key) throws UtilsException {
		super(javax.crypto.Cipher.DECRYPT_MODE, key);
	}

	public Decrypt(KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException {
		super(javax.crypto.Cipher.DECRYPT_MODE, keystore, alias, passwordPrivateKey);
	}

	public Decrypt(KeyStore keystore, String alias) throws UtilsException {
		super(javax.crypto.Cipher.DECRYPT_MODE, keystore, alias);
	}

	public Decrypt(KeyStore keystore) throws UtilsException {
		super(javax.crypto.Cipher.DECRYPT_MODE, keystore);
	}

	public byte[] decrypt(byte[] data, String algorithm) throws UtilsException{
		return super.process(data, algorithm);
	}
	
	public byte[] decryptBase64(byte[] data, String algorithm) throws UtilsException{
		return super.process(Base64Utilities.decode(data),algorithm);
	}
	
	public byte[] decryptBase64(String data, String algorithm) throws UtilsException{
		return super.process(Base64Utilities.decode(data),algorithm);
	}
	
	public byte[] decryptHexBinary(char[] data, String algorithm) throws UtilsException{
		return super.process(HexBinaryUtilities.decode(data),algorithm);
	}
	
	public byte[] decryptHexBinary(String data, String algorithm) throws UtilsException{
		return super.process(HexBinaryUtilities.decode(data),algorithm);
	}
	

}
