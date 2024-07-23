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

package org.openspcoop2.pdd.core.keystore;

import java.io.ByteArrayOutputStream;
import java.security.PublicKey;

import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.keystore.RemoteStore;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.JWK;
import org.openspcoop2.utils.certificate.remote.IRemoteStoreProvider;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreClientInfo;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;

/**
 * RemoteStoreProvider
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoreProvider implements IRemoteStoreProvider {

	private RemoteKeyType keyType;
	
	private RequestInfo requestInfo;
	
	public RemoteStoreProvider(RequestInfo requestInfo, RemoteKeyType keyType) throws KeystoreException {
		
		this.requestInfo = requestInfo;
		
		if(keyType==null) {
			throw new KeystoreException("RemoteKeyType undefined");
		}
		this.keyType = keyType;
	}
	
		
	@Override
	public JWK readJWK(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException {
		try {
			return GestoreKeystoreCaching.getRemoteStore(this.requestInfo, keyId, this.keyType, remoteConfig, RemoteStoreProviderDriver.getProviderStore(remoteConfig.getStoreName())).getJWK();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	@Override
	public JWK readJWK(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout) throws UtilsException {
		try {
			RemoteStore remoteStore = GestoreKeystoreCaching.getRemoteStore(this.requestInfo, keyId, this.keyType, remoteConfig, RemoteStoreProviderDriver.getProviderStore(remoteConfig.getStoreName()));
			if(bout!=null) {
				bout.write(remoteStore.getResource());
			}
			return remoteStore.getJWK();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	@Override
	public Certificate readX509(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException {
		try {
			return GestoreKeystoreCaching.getRemoteStore(this.requestInfo, keyId, this.keyType, remoteConfig, RemoteStoreProviderDriver.getProviderStore(remoteConfig.getStoreName())).getCertificate();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	@Override
	public Certificate readX509(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout)
			throws UtilsException {
		try {
			RemoteStore remoteStore = GestoreKeystoreCaching.getRemoteStore(this.requestInfo, keyId, this.keyType, remoteConfig, RemoteStoreProviderDriver.getProviderStore(remoteConfig.getStoreName()));
			if(bout!=null) {
				bout.write(remoteStore.getResource());
			}
			return remoteStore.getCertificate();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	@Override
	public PublicKey readPublicKey(String keyId, RemoteStoreConfig remoteConfig) throws UtilsException {
		try {
			return GestoreKeystoreCaching.getRemoteStore(this.requestInfo, keyId, this.keyType, remoteConfig, RemoteStoreProviderDriver.getProviderStore(remoteConfig.getStoreName())).getPublicKey();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	@Override
	public PublicKey readPublicKey(String keyId, RemoteStoreConfig remoteConfig, ByteArrayOutputStream bout)
			throws UtilsException {
		try {
			RemoteStore remoteStore = GestoreKeystoreCaching.getRemoteStore(this.requestInfo, keyId, this.keyType, remoteConfig, RemoteStoreProviderDriver.getProviderStore(remoteConfig.getStoreName()));
			if(bout!=null) {
				bout.write(remoteStore.getResource());
			}
			return remoteStore.getPublicKey();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	@Override
	public RemoteStoreClientInfo readClientInfo(String keyId, String clientId, RemoteStoreConfig remoteConfig, org.openspcoop2.utils.Map<Object> context)
			throws UtilsException {
		try {
			return RemoteStoreProviderDriver.getProviderStore(remoteConfig.getStoreName()).readClientInfo(keyId, clientId, remoteConfig, context);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
}
