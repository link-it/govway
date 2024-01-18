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

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.security.PublicKey;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.JWK;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.certificate.remote.IRemoteStoreProvider;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;

/**
 * RemoteStore
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStore implements Serializable {

	private static final long serialVersionUID = 1L;

	private String remoteStoreName;
	private String keyId;
	private byte[] resource;
	private RemoteKeyType keyType;
	private String keyAlgorithm;
	private transient Certificate x509;
	private transient PublicKey publicKey;
	private transient JWK jwk;
	
	public RemoteStore(String keyId, RemoteKeyType keyType, RemoteStoreConfig remoteStoreConfig, IRemoteStoreProvider provider) throws SecurityException {
		try {
			this.keyType = keyType;
			this.keyId = keyId;
			if(keyId==null) {
				throw new SecurityException("KeyId undefined");
			}
			if(keyType==null) {
				throw new SecurityException("KeyType undefined");
			}
			if(remoteStoreConfig==null) {
				throw new SecurityException("RemoteStoreConfig undefined");
			}
			this.remoteStoreName = remoteStoreConfig.getStoreName();
			if(this.remoteStoreName==null) {
				throw new SecurityException("RemoteStoreConfig name undefined");
			}
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			switch (keyType) {
			case JWK:
				this.jwk = provider.readJWK(keyId, remoteStoreConfig, bout);	
				break;
			case PUBLIC_KEY:
				this.publicKey = provider.readPublicKey(this.keyId, remoteStoreConfig, bout);
				this.keyAlgorithm = remoteStoreConfig.getKeyAlgorithm();
				break;
			case X509:
				this.x509 = provider.readX509(this.keyId, remoteStoreConfig, bout);		
				break;
			}
			this.resource = bout.toByteArray();
		}catch(Exception e) {
			throw new SecurityException(e.getMessage(),e);
		}
	}
		
	private SecurityException newSecurityExceptionDifferentKeyType() {
		return new SecurityException("Different KeyType '"+this.keyType+"'");
	}
	
	private synchronized void initCertificate() throws SecurityException {
		if(this.x509==null) {
			if(RemoteKeyType.X509.equals(this.keyType)) {
				try {
					this.x509 = ArchiveLoader.load(this.resource);
				}catch(Exception e) {
					throw new SecurityException(e.getMessage(),e);
				}
			}
			else {
				throw newSecurityExceptionDifferentKeyType();
			}
		}
	}
	public Certificate getCertificate() throws SecurityException {
		if(this.x509==null) {
			this.initCertificate();
		}
		return this.x509;
	}
	
	private synchronized void initPublicKey() throws SecurityException {
		if(this.publicKey==null) {
			if(RemoteKeyType.PUBLIC_KEY.equals(this.keyType)) {
				try {
					this.publicKey = KeyUtils.getInstance(this.keyAlgorithm).getPublicKey(this.resource);
				}catch(Exception e) {
					throw new SecurityException(e.getMessage(),e);
				}
			}
			else {
				throw newSecurityExceptionDifferentKeyType();
			}
		}
	}
	public PublicKey getPublicKey() throws SecurityException {
		if(this.publicKey==null) {
			this.initPublicKey();
		}
		return this.publicKey;
	}
	
	private synchronized void initJWK() throws SecurityException {
		if(this.jwk==null) {
			if(RemoteKeyType.JWK.equals(this.keyType)) {
				try {
					this.jwk = new JWK(new String(this.resource));
				}catch(Exception e) {
					throw new SecurityException(e.getMessage(),e);
				}
			}
			else {
				throw newSecurityExceptionDifferentKeyType();
			}
		}
	}
	public JWK getJWK() throws SecurityException {
		if(this.jwk==null) {
			this.initJWK();
		}
		return this.jwk;
	}
	
	
	public String getRemoteStoreName() {
		return this.remoteStoreName;
	}

	public void setRemoteStoreName(String remoteStoreName) {
		this.remoteStoreName = remoteStoreName;
	}
	
	public String getKeyId() {
		return this.keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public byte[] getResource() {
		return this.resource;
	}

	public void setResource(byte[] resource) {
		this.resource = resource;
	}

	public RemoteKeyType getKeyType() {
		return this.keyType;
	}

	public void setKeyType(RemoteKeyType keyType) {
		this.keyType = keyType;
	}	
}
