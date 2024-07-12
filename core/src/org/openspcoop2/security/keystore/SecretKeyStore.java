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

import javax.crypto.SecretKey;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.certificate.SymmetricKeyUtils;
import org.openspcoop2.utils.certificate.byok.BYOKCostanti;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;
import org.openspcoop2.utils.security.CipherInfo;
import org.openspcoop2.utils.security.EncryptOpenSSLPass;
import org.openspcoop2.utils.security.EncryptOpenSSLPassPBKDF2;
import org.openspcoop2.utils.security.OpenSSLEncryptionMode;

/**
 * SecretKeyStore
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecretKeyStore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String secretKeyPath;
	
	private byte[] secretKeyContent;
	private transient SecretKey secretKey;
	
	private String algorithm;
	
	private byte[] iv;
	private byte[] salt;
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("SecretKeyStore (public:").append(this.secretKeyPath).append(")");
		return bf.toString();
	}
	
	public SecretKeyStore(String secretKeyPath, String algorithm) throws SecurityException{
		this(secretKeyPath, algorithm, null);
	}
	public SecretKeyStore(String secretKeyPath, String algorithm, BYOKRequestParams requestParams) throws SecurityException{
	
		this.secretKeyPath = secretKeyPath;
				
		this.algorithm = algorithm==null ? SymmetricKeyUtils.ALGO_AES : algorithm;
		
		this.secretKeyContent = StoreUtils.readContent("SecretKey", this.secretKeyPath);
		
		this.secretKeyContent = StoreUtils.unwrapBYOK(this.secretKeyContent, requestParams);
			
	}
	
	public SecretKeyStore(byte[] secretKey, String algorithm) throws SecurityException{
		this(secretKey, algorithm, null);
	}
	public SecretKeyStore(byte[] secretKey, String algorithm, BYOKRequestParams requestParams) throws SecurityException{

		try{			
			if(secretKey==null){
				throw new SecurityException("Store publicKey undefined");
			}
			this.secretKeyContent = secretKey;
			
			this.secretKeyContent = StoreUtils.unwrapBYOK(this.secretKeyContent, requestParams);

			this.algorithm = algorithm==null ? SymmetricKeyUtils.ALGO_AES : algorithm;
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
	}
	
	public SecretKeyStore(SecretPasswordKeyDerivationConfig passwordKeyDerivationConfig) throws SecurityException{
		this(passwordKeyDerivationConfig, null);
	}
	public SecretKeyStore(SecretPasswordKeyDerivationConfig passwordKeyDerivationConfig, BYOKRequestParams requestParams) throws SecurityException{
		/** NOTA: Ha senso SOLO per ottenere una chiave per cifrare; mentre per la decifratura la chiave deve essere derivata anche analizzando il testo cifrato */
		try{			
			if(passwordKeyDerivationConfig==null){
				throw new SecurityException("Password Key Derivation config undefined");
			}
			if(passwordKeyDerivationConfig.getPasswordEncryptionMode()==null){
				throw new SecurityException("Password Key Derivation mode undefined");
			}
			if(passwordKeyDerivationConfig.getPassword()==null){
				throw new SecurityException("Password Key Derivation undefined");
			}
			String pwd = null;
			if(requestParams!=null) {
				pwd = new String(StoreUtils.unwrapBYOK(passwordKeyDerivationConfig.getPassword().getBytes(), requestParams));
			}
			else {
				pwd = passwordKeyDerivationConfig.getPassword();
			}
			if(BYOKCostanti.isOpenSSLPasswordDerivationKeyMode(passwordKeyDerivationConfig.getPasswordEncryptionMode())) {
				CipherInfo cipherInfo = null;
				if(BYOKCostanti.isOpenSSLPBKDF2PasswordDerivationKeyMode(passwordKeyDerivationConfig.getPasswordEncryptionMode())) {
					cipherInfo = EncryptOpenSSLPassPBKDF2.buildCipherInfo(pwd, passwordKeyDerivationConfig.getPasswordIterator(), 
							OpenSSLEncryptionMode.toMode(passwordKeyDerivationConfig.getPasswordEncryptionMode()));
				}
				else {
					cipherInfo = EncryptOpenSSLPass.buildCipherInfo(pwd, null, 
							OpenSSLEncryptionMode.toMode(passwordKeyDerivationConfig.getPasswordEncryptionMode()));
				}
				this.secretKeyContent = cipherInfo.getEncodedKey();
				this.secretKey = (SecretKey) cipherInfo.getKey();
				this.algorithm = SymmetricKeyUtils.ALGO_AES;
				this.iv = cipherInfo.getIv();
				this.salt = cipherInfo.getSalt();
			}
			else {
				throw new SecurityException("Password Key Derivation mode '"+passwordKeyDerivationConfig.getPasswordEncryptionMode()+"' unsupported");
			}
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
		
	}

	public SecretKey getSecretKey() throws SecurityException {
		if(this.secretKey==null) {
			initializeSecretKey();
		}
		return this.secretKey;
	}
	private synchronized void initializeSecretKey() throws SecurityException {
		if(this.secretKey==null) {
			try {
				this.secretKey = SymmetricKeyUtils.getInstance(this.algorithm).getSecretKey(this.secretKeyContent);
			}catch(Exception e){
				throw new SecurityException("Load public key failed: "+e.getMessage(),e);
			}
		}
	}
	
	public byte[] getIv() {
		return this.iv;
	}
	public byte[] getSalt() {
		return this.salt;
	}
	
}
