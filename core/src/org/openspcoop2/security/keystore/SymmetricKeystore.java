/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.security.keystore;

import java.security.Key;
import java.security.KeyStore;

import javax.crypto.spec.SecretKeySpec;

import org.openspcoop2.security.Constants;
import org.openspcoop2.security.SecurityException;

/**
 * SymmetricKeystore
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SymmetricKeystore {

	private SecretKeySpec key = null;
	private KeyStore keyStore = null;
	private String passwordKey = null;
	
	public SymmetricKeystore(String alias,String key,String algoritmo) throws SecurityException{
		try{
			String algorithm = null;
			if(Constants.TRIPLE_DES.equals(algoritmo)){
				algorithm = "DESede";
			}
			else{
				algorithm = "DES"; // default
			}

			this.key = new SecretKeySpec(key.getBytes(),algorithm);
			this.keyStore = KeyStore.getInstance("JCEKS");
			this.keyStore.load(null);
			this.passwordKey = "PASSWORD_CUSTOM";
			this.keyStore.setKeyEntry(alias, this.key,this.passwordKey.toCharArray(), null);
			FixTrustAnchorsNotEmpty.addCertificate(this.keyStore);			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	public Key getKey() throws SecurityException {
		return this.key;
	}

	public KeyStore getKeyStore() throws SecurityException {
		return this.keyStore; 
	}

	public String getPasswordKey() {
		return this.passwordKey;
	}
	
	

}
