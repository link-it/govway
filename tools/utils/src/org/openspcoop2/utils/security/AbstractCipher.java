/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

import javax.crypto.spec.SecretKeySpec;

import org.openspcoop2.utils.UtilsException;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractCipher {

	private java.security.Key key;
	private java.security.cert.Certificate certificate;
	private int mode;
	
	protected AbstractCipher(int mode, java.security.Key key) throws UtilsException{
		this.mode = mode;
		this.key = key;
	}
	protected AbstractCipher(int mode, KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException{
		this.mode = mode;
		this.key = keystore.getPrivateKey(alias, passwordPrivateKey);
	}
	protected AbstractCipher(int mode, byte[] secretKey, String algorithm) throws UtilsException{
		this.mode = mode;
		this.key = new SecretKeySpec(secretKey, algorithm); // cifratura simmetrica
	}
	protected AbstractCipher(int mode, java.security.cert.Certificate certificate) throws UtilsException{
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
				cipher.init(this.mode, this.key);
			}
			else{
				cipher.init(this.mode, this.certificate);
			}
			return cipher.doFinal(data);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
