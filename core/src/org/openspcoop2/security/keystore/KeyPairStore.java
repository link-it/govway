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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.certificate.JWKPrivateKeyConverter;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;

/**
 * KeyPairStore
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KeyPairStore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String privateKeyPath;
	private String publicKeyPath;
	
	private byte[] privateKeyContent;
	private byte[] publicKeyContent;
	private transient PrivateKey privateKey;
	private transient PublicKey publicKey;
	
	private String privateKeyPassword;
	private String algorithm;
	
	private String jwkSetContent;
	private String jwkSetKid;
	private transient JWKSet jwkSet;

	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("KeyPairStore (private:").append(this.privateKeyPath).append(" public:").append(this.publicKeyPath).append(")");
		return bf.toString();
	}
	
	public KeyPairStore(String privateKeyPath, String publicKeyPath, String privateKeyPassword, String algorithm) throws SecurityException{
		this(privateKeyPath, publicKeyPath, privateKeyPassword, algorithm, null);
	}
	public KeyPairStore(String privateKeyPath, String publicKeyPath, String privateKeyPassword, String algorithm, BYOKRequestParams requestParams) throws SecurityException{

		this.privateKeyPath = privateKeyPath;
		this.publicKeyPath = publicKeyPath;
				
		this.privateKeyPassword = privateKeyPassword;
		this.algorithm = algorithm==null ? KeyUtils.ALGO_RSA : algorithm;
		
		this.privateKeyContent = StoreUtils.readContent("PrivateKey", this.privateKeyPath);
		this.privateKeyContent = StoreUtils.unwrapBYOK(this.privateKeyContent, requestParams);
		
		this.publicKeyContent = StoreUtils.readContent("PublicKey", this.publicKeyPath);
		
	}
	
	public KeyPairStore(byte[] privateKey, byte[] publicKey, String privateKeyPassword, String algorithm) throws SecurityException{
		this(privateKey, publicKey, privateKeyPassword, algorithm, null);
	}
	public KeyPairStore(byte[] privateKey, byte[] publicKey, String privateKeyPassword, String algorithm, BYOKRequestParams requestParams) throws SecurityException{

		try{
			if(privateKey==null){
				throw new SecurityException("Store privateKey non indicato");
			}
			this.privateKeyContent = privateKey;
			this.privateKeyContent = StoreUtils.unwrapBYOK(this.privateKeyContent, requestParams);
			
			if(publicKey==null){
				throw new SecurityException("Store publicKey non indicato");
			}
			this.publicKeyContent = publicKey;
			
			this.privateKeyPassword = privateKeyPassword;
			this.algorithm = algorithm==null ? KeyUtils.ALGO_RSA : algorithm;
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
	}

	
	public PrivateKey getPrivateKey() throws SecurityException {
		if(this.privateKey==null) {
			initializePrivateKey();
		}
		return this.privateKey;
	}
	private synchronized void initializePrivateKey() throws SecurityException {
		if(this.privateKey==null) {
			try {
				if(this.privateKeyPassword==null) {
					this.privateKey = KeyUtils.getInstance(this.algorithm).getPrivateKey(this.privateKeyContent);
				}
				else {
					this.privateKey = KeyUtils.getInstance(this.algorithm).getPrivateKey(this.privateKeyContent, this.privateKeyPassword);
				}
			}catch(Exception e){
				if(this.privateKeyPassword==null && e.getMessage().contains("org.bouncycastle.openssl.PEMEncryptedKeyPair cannot be cast to class org.bouncycastle.openssl.PEMKeyPair")) {
					throw new SecurityException("Load private key failed: encrypted key require key password",e);
				}
				else {
					throw new SecurityException("Load private key failed: "+e.getMessage(),e);
				}
			}
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
					this.jwkSetContent = JWKPrivateKeyConverter.convert(this.getPublicKey(), this.getPrivateKey(), this.jwkSetKid, true, false);
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
