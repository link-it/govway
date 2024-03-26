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
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;

/**
 * MerlinKeystoreCache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MerlinKeystoreCache extends AbstractKeystoreCache<MerlinKeystore> {

	@Override
	public MerlinKeystore createKeystore(String key, Object... params) throws SecurityException{
		if(params==null){
			throw new SecurityException("Params is null");
		}
		if(params.length==0){
			String propertyFilePath = key;
			return new MerlinKeystore(propertyFilePath);
		}
		else if(params.length==1){
			if( ! (params[0] instanceof String) && ! (params[0] instanceof BYOKRequestParams) ){
				throw new SecurityException("Param[0] must be String (passwordPrivateKey) or BYOKRequestParams");
			}
			String propertyFilePath = key;
			if(params[0] instanceof String) {
				String passwordPrivateKey = (String) params[0];
				return new MerlinKeystore(propertyFilePath, passwordPrivateKey);
			}
			else {
				BYOKRequestParams requestParams = (BYOKRequestParams) params[0];
				return new MerlinKeystore(propertyFilePath, requestParams);
			}
		}
		else if(params.length==2){
			if( ! (params[0] instanceof String) ){
				throw new SecurityException("Param[0] must be String (tipoStore or passwordPrivateKey)");
			}
			if( ! (params[1] instanceof String) && ! (params[1] instanceof BYOKRequestParams) ){
				throw new SecurityException("Param[1] must be String (passwordStore) or BYOKRequestParams");
			}
			String pathStore = key;
			if( (params[1] instanceof String) ){
				String tipoStore = (String) params[0];
				String passwordStore = (String) params[1];
				return new MerlinKeystore(pathStore, tipoStore, passwordStore);
			}
			else {
				String passwordPrivateKey = (String) params[0];
				BYOKRequestParams requestParams = (BYOKRequestParams) params[1];
				return new MerlinKeystore(pathStore, passwordPrivateKey, requestParams);
			}
		}
		else if(params.length==3){
			if(params[0] instanceof String) {
				if( ! (params[1] instanceof String) ){
					throw new SecurityException("Param[1] must be String (passwordStore)");
				}
				if( ! (params[2] instanceof String) && ! (params[2] instanceof BYOKRequestParams) ){
					throw new SecurityException("Param[2] must be String (passwordPrivateKey) or BYOKRequestParams");
				}
				String pathStore = key;
				String tipoStore = (String) params[0];
				String passwordStore = (String) params[1];
				if(params[2] instanceof String) {
					String passwordPrivateKey = (String) params[2];
					return new MerlinKeystore(pathStore, tipoStore, passwordStore, passwordPrivateKey);
				}
				else {
					BYOKRequestParams requestParams = (BYOKRequestParams) params[2];
					return new MerlinKeystore(pathStore, tipoStore, passwordStore, requestParams);
				}
			}
			else if(params[0] instanceof byte[]) {
				if( ! (params[1] instanceof String) ){
					throw new SecurityException("Param[1] must be String (tipoStore)");
				}
				if( ! (params[2] instanceof String) ){
					throw new SecurityException("Param[2] must be String (passwordStore)");
				}
				byte [] store = (byte[]) params[0];
				String tipoStore = (String) params[1];
				String passwordStore = (String) params[2];
				return new MerlinKeystore(store, tipoStore, passwordStore);
			}
			else {
				throw new SecurityException("Param[0] must be String (tipoStore) or byte[] (store)");
			}
		}
		else if(params.length==4){
			if( ! (params[0] instanceof byte[]) && ! (params[0] instanceof String) ){
				throw new SecurityException("Param[0] must be byte[] (store) or String (tipoStore)");
			}
			if( params[0] instanceof byte[] ){
				if( ! (params[1] instanceof String) ){
					throw new SecurityException("Param[1] must be String (tipoStore)");
				}
				if( ! (params[2] instanceof String) ){
					throw new SecurityException("Param[2] must be String (passwordStore)");
				}
				if( ! (params[3] instanceof String) && ! (params[3] instanceof BYOKRequestParams) ){
					throw new SecurityException("Param[3] must be String (passwordPrivateKey) or BYOKRequestParams");
				}
				byte [] store = (byte[]) params[0];
				String tipoStore = (String) params[1];
				String passwordStore = (String) params[2];
				if(params[3] instanceof String) {
					String passwordPrivateKey = (String) params[3];
					return new MerlinKeystore(store, tipoStore, passwordStore, passwordPrivateKey);
				}
				else {
					BYOKRequestParams requestParams = (BYOKRequestParams) params[3];
					return new MerlinKeystore(store, tipoStore, passwordStore, requestParams);
				}
			}
			else {
				if( ! (params[1] instanceof String) ){
					throw new SecurityException("Param[1] must be String (passwordStore)");
				}
				if( ! (params[2] instanceof String) ){
					throw new SecurityException("Param[2] must be String (passwordPrivateKey)");
				}
				if( ! (params[3] instanceof BYOKRequestParams) ){
					throw new SecurityException("Param[3] must be BYOKRequestParams");
				}
				String pathStore = key;
				String tipoStore = (String) params[0];
				String passwordStore = (String) params[1];
				String passwordPrivateKey = (String) params[2];
				BYOKRequestParams requestParams = (BYOKRequestParams) params[3];
				return new MerlinKeystore(pathStore, tipoStore, passwordStore, passwordPrivateKey, requestParams);
			}
		}
		else if(params.length==5){
			if( ! (params[0] instanceof byte[])){
				throw new SecurityException("Param[0] must be byte[] (store)");
			}
			if( ! (params[1] instanceof String) ){
				throw new SecurityException("Param[1] must be String (tipoStore)");
			}
			if( ! (params[2] instanceof String) ){
				throw new SecurityException("Param[2] must be String (passwordStore)");
			}
			if( ! (params[3] instanceof String) ){
				throw new SecurityException("Param[3] must be String (passwordPrivateKey)");
			}
			if( ! (params[4] instanceof BYOKRequestParams) ){
				throw new SecurityException("Param[4] must be BYOKRequestParams");
			}
			byte [] store = (byte[]) params[0];
			String tipoStore = (String) params[1];
			String passwordStore = (String) params[2];
			String passwordPrivateKey = (String) params[3];
			BYOKRequestParams requestParams = (BYOKRequestParams) params[4];
			return new MerlinKeystore(store, tipoStore, passwordStore, passwordPrivateKey, requestParams);
		}
		else{
			throw new SecurityException("Params [lenght:"+params.length+"] not supported");
		}
	}

	@Override
	public String getPrefixKey() {
		return "Keystore ";
	}

}
