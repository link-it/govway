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

package org.openspcoop2.security.keystore;

import java.io.Serializable;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import org.openspcoop2.security.Constants;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.certificate.KeyStore;

/**
 * SymmetricKeystore
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SymmetricKeystore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SecretKeySpec key = null;
	private transient KeyStore keyStore = null;
	private String pwKey = null;
	private String alias = null;
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("SymmetricKeystore (").append(this.alias).append(") ");
		return bf.toString();
	}
	
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
			
			this.alias = alias;
			this.pwKey = "PW_CUSTOM";
			
			this.initKS();
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	private void checkInit() throws SecurityException{
		if(this.keyStore==null) {
			this.initKS();
		}
	}
	private synchronized void initKS() throws SecurityException{
		if(this.keyStore==null) {
			try {
				java.security.KeyStore keyStoreJCEKS = java.security.KeyStore.getInstance("JCEKS");
				keyStoreJCEKS.load(null);
				keyStoreJCEKS.setKeyEntry(this.alias, this.key,this.pwKey.toCharArray(), null);
				this.keyStore = new KeyStore(keyStoreJCEKS);
				FixTrustAnchorsNotEmpty.addCertificate(this.keyStore.getKeystore());			
			}
			catch(Exception e){
				throw new SecurityException(e.getMessage(),e);
			}
		}
	}
	
	
	public Key getKey() {
		return this.key;
	}

	public KeyStore getKeyStore() throws SecurityException {
		this.checkInit(); // per ripristino da Serializable
		return this.keyStore; 
	}

	public String getPasswordKey() {
		return this.pwKey;
	}
	
	

}
