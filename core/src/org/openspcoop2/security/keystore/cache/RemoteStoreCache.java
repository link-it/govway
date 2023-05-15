/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.security.keystore.cache;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.RemoteStore;
import org.openspcoop2.utils.certificate.remote.IRemoteStoreProvider;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;

/**
 * RemoteStoreCache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoreCache extends AbstractKeystoreCache<RemoteStore> {	

	@Override
	public RemoteStore createKeystore(String key, Object... params) throws SecurityException{
		if(params==null){
			throw new SecurityException("Params is null");
		}
		if(params.length==4){
			// key e' formata dalla tripla remoteStoreConfigName, keyId e RemoteKeyType
			if( ! (params[0] instanceof String) ){
				throw new SecurityException("Param[0] must be String");
			}
			if( ! (params[1] instanceof RemoteKeyType) ){
				throw new SecurityException("Param[1] must be RemoteKeyType");
			}
			if( ! (params[2] instanceof RemoteStoreConfig) ){
				throw new SecurityException("Param[2] must be RemoteStoreConfig");
			}
			if( ! (params[3] instanceof IRemoteStoreProvider) ){
				throw new SecurityException("Param[3] must be IRemoteStoreProvider");
			}
			String keyId = (String) params[0];
			RemoteKeyType keyType = (RemoteKeyType) params[1];
			RemoteStoreConfig remoteStoreConfig = (RemoteStoreConfig) params[2];
			IRemoteStoreProvider provider = (IRemoteStoreProvider) params[3];
			return new RemoteStore(keyId, keyType, remoteStoreConfig, provider);
		}
		else{
			throw new SecurityException("Params [lenght:"+params.length+"] not supported");
		}
	}

	public static final String RESTORE_STORE_PREFIX = "RemoteStore ";
	
	@Override
	public String getPrefixKey() {
		return RESTORE_STORE_PREFIX;
	}
	
	public static String getPrefixKeyCache(RemoteStoreConfig remoteStoreConfig, RemoteKeyType keyType) throws SecurityException {
		if(remoteStoreConfig==null) {
			throw new SecurityException("RemoteStoreConfig undefined");
		}
		String remoteStoreName = remoteStoreConfig.getStoreName();
		if(remoteStoreName==null) {
			throw new SecurityException("RemoteStoreConfig name undefined");
		}
		
		if(keyType==null) {
			throw new SecurityException("KeyType undefined");
		}
		
		return remoteStoreName+"_"+keyType.name()+"_";
	}
	public static String getKeyCache(RemoteStoreConfig remoteStoreConfig, String keyId, RemoteKeyType keyType) throws SecurityException {
		// key e' formata dalla tripla remoteStoreConfigName, keyId e RemoteKeyType
		
		if(keyId==null) {
			throw new SecurityException("KeyId undefined");
		}
		
		return getPrefixKeyCache(remoteStoreConfig, keyType)+keyId;
	}
}
