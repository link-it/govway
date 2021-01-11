/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.security.PrivateKey;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;

/**	
 * Signature
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Signature {

	private KeyStore keystore;
	private PrivateKey privateKey;
	
	public Signature(KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException{
		this.keystore = keystore;
		this.privateKey = this.keystore.getPrivateKey(alias, passwordPrivateKey);
	}
	
	public byte[] sign(String data, String charsetName, String algorithm) throws UtilsException{
		try{
			return this.sign(data.getBytes(charsetName), algorithm);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public byte[] sign(byte[] data, String algorithm) throws UtilsException{
		try{
			java.security.Signature sig = java.security.Signature.getInstance(algorithm);
			sig.initSign(this.privateKey);
			sig.update(data);
			return sig.sign();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
