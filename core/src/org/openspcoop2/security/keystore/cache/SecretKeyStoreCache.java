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

package org.openspcoop2.security.keystore.cache;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.SecretKeyStore;

/**
 * SecretKeyStoreCache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecretKeyStoreCache extends AbstractKeystoreCache<SecretKeyStore> {

	@Override
	public SecretKeyStore createKeystore(String key, Object... params) throws SecurityException{
		if(params==null){
			throw new SecurityException("Params is null");
		}
		
		if(params.length==1){
			return createFromPath(key, params);
		}
		else if(params.length==2){
			return createFromByteArray(params);
		}
		else{
			throw new SecurityException("Params [lenght:"+params.length+"] not supported");
		}
	}
	private SecretKeyStore createFromPath(String key, Object... params) throws SecurityException {
		if(params[0] instanceof String) {
			String pathSecretKey = key;
			String algorithm = (String) params[0];
			return new SecretKeyStore(pathSecretKey, algorithm);
		}
		else {
			throw new SecurityException("Param[0] must be String (algorithm)");
		}
	}
	private SecretKeyStore createFromByteArray(Object... params) throws SecurityException {
		if(params[0] instanceof byte[]) {
			if( ! (params[1] instanceof String) ){
				throw new SecurityException("Param[1] must be String (algorithm)");
			}
			byte [] secretKey = (byte[]) params[0];
			String algorithm = (String) params[1];
			return new SecretKeyStore(secretKey, algorithm);
		}
		else {
			throw new SecurityException("Param[0] must be byte[] (secretKey)");
		}
	}

	@Override
	public String getPrefixKey() {
		return "SecretKey ";
	}
}
