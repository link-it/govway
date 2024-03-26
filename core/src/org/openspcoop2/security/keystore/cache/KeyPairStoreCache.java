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
import org.openspcoop2.security.keystore.KeyPairStore;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;

/**
 * KeyPairStoreCache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KeyPairStoreCache extends AbstractKeystoreCache<KeyPairStore> {

	@Override
	public KeyPairStore createKeystore(String key, Object... params) throws SecurityException{
		if(params==null){
			throw new SecurityException("Params is null");
		}
		
		if(params.length==3){
			return createFromPath(key, params);
		}
		else if(params.length==4){
			if(params[0] instanceof String) {
				return createFromPath(key, params);
			}
			else {
				return createFromByteArray(params);
			}
		}
		else if(params.length==5){
			return createFromByteArray(params);
		}
		else{
			throw new SecurityException("Params [lenght:"+params.length+"] not supported");
		}
	}
	private KeyPairStore createFromPath(String key, Object... params) throws SecurityException {
		if(params[0] instanceof String) {
			if( params[1]!=null && !(params[1] instanceof String) ){
				throw new SecurityException("Param[1] must be String (privateKeyPassword) or null");
			}
			if( ! (params[2] instanceof String) ){
				throw new SecurityException("Param[2] must be String (algorithm)");
			}
			String pathPrivateKey = key;
			String pathPublicKey = (String) params[0];
			String privateKeyPassword = (String) params[1];
			String algorithm = (String) params[2];
			if(params.length==4){
				if( ! (params[3] instanceof BYOKRequestParams) ){
					throw new SecurityException("Param[3] must be BYOKRequestParams");
				}
				BYOKRequestParams requestParams = (BYOKRequestParams) params[3];
				return new KeyPairStore(pathPrivateKey, pathPublicKey, privateKeyPassword, algorithm, requestParams);
			}
			else {
				return new KeyPairStore(pathPrivateKey, pathPublicKey, privateKeyPassword, algorithm);
			}
		}
		else {
			throw new SecurityException("Param[0] must be String (pathPublicKey)");
		}
	}
	private KeyPairStore createFromByteArray(Object... params) throws SecurityException {
		if(params[0] instanceof byte[]) {
			if( ! (params[1] instanceof byte[]) ){
				throw new SecurityException("Param[1] must be byte[] (publicKey)");
			}
			if( params[2]!=null && !(params[2] instanceof String) ){
				throw new SecurityException("Param[2] must be String (privateKeyPassword) or null");
			}
			if( ! (params[3] instanceof String) ){
				throw new SecurityException("Param[3] must be String (algorithm)");
			}
			byte [] privateKey = (byte[]) params[0];
			byte [] publicKey = (byte[]) params[1];
			String privateKeyPassword = (String) params[2];
			String algorithm = (String) params[3];
			if(params.length==5){
				if( ! (params[4] instanceof BYOKRequestParams) ){
					throw new SecurityException("Param[4] must be BYOKRequestParams");
				}
				BYOKRequestParams requestParams = (BYOKRequestParams) params[3];
				return new KeyPairStore(privateKey, publicKey, privateKeyPassword, algorithm, requestParams);
			}
			else {
				return new KeyPairStore(privateKey, publicKey, privateKeyPassword, algorithm);
			}
		}
		else {
			throw new SecurityException("Param[0] must be byte[] (privateKey)");
		}
	}

	@Override
	public String getPrefixKey() {
		return "KeyPair ";
	}
}
