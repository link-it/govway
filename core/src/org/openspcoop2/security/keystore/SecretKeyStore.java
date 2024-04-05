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
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;

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
				throw new SecurityException("Store publicKey non indicato");
			}
			this.secretKeyContent = secretKey;
			
			this.secretKeyContent = StoreUtils.unwrapBYOK(this.secretKeyContent, requestParams);

			this.algorithm = algorithm==null ? SymmetricKeyUtils.ALGO_AES : algorithm;
			
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
	
}
