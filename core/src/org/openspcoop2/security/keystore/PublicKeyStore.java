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
import java.security.PublicKey;
import java.util.UUID;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.certificate.JWKPublicKeyConverter;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyUtils;

/**
 * PublicKeyStore
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PublicKeyStore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String publicKeyPath;
	
	private byte[] publicKeyContent;
	private transient PublicKey publicKey;
	
	private String algorithm;
	
	private String jwkSetContent;
	private String jwkSetKid;
	private transient JWKSet jwkSet;

	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("PublicKeyStore (public:").append(this.publicKeyPath).append(")");
		return bf.toString();
	}
	
	public PublicKeyStore(String publicKeyPath, String algorithm) throws SecurityException{

		this.publicKeyPath = publicKeyPath;
				
		this.algorithm = algorithm==null ? KeyUtils.ALGO_RSA : algorithm;
		
		this.publicKeyContent = StoreUtils.readContent("PublicKey", this.publicKeyPath);
		
	}
	
	public PublicKeyStore(byte[] publicKey, String algorithm) throws SecurityException{

		try{			
			if(publicKey==null){
				throw new SecurityException("Store publicKey non indicato");
			}
			this.publicKeyContent = publicKey;

			this.algorithm = algorithm==null ? KeyUtils.ALGO_RSA : algorithm;
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
	}

	public PublicKey getPublicKey() throws SecurityException {
		if(this.publicKey==null) {
			initializePublicKey();
		}
		return this.publicKey;
	}
	private synchronized void initializePublicKey() throws SecurityException {
		if(this.publicKey==null) {
			try {
				this.publicKey = KeyUtils.getInstance(this.algorithm).getPublicKey(this.publicKeyContent);
			}catch(Exception e){
				throw new SecurityException("Load public key failed: "+e.getMessage(),e);
			}
		}
	}
	
	
	public JWKSet getJwkSet() throws SecurityException {
		if(this.jwkSet==null) {
			initializeJwkSet();
		}
		return this.jwkSet;
	}
	private synchronized void initializeJwkSet() throws SecurityException {
		if(this.jwkSet==null) {
			if(this.jwkSetContent==null) {
				this.jwkSetKid = UUID.randomUUID().toString();
				try {
					this.jwkSetContent = JWKPublicKeyConverter.convert(this.getPublicKey(), this.jwkSetKid, true, false);
				}catch(Exception e){
					throw new SecurityException(e.getMessage(),e);
				}
			}
						
			this.jwkSet = new JWKSet(this.jwkSetContent);
		}
	}
	
	public String getJwkSetKid() {
		return this.jwkSetKid;
	}
}
