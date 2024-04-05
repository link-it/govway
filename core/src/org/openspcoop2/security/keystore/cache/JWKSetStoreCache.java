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
import org.openspcoop2.security.keystore.JWKSetStore;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;

/**
 * JWKSetStoreCache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JWKSetStoreCache extends AbstractKeystoreCache<JWKSetStore> {

	@Override
	public JWKSetStore createKeystore(String key, Object... params) throws SecurityException{
		if(params==null){
			throw new SecurityException("Params is null");
		}
		String path = key;
		if(params.length==0){
			return new JWKSetStore(path);
		}
		else if(params.length==1){
			if( ! (params[0] instanceof byte[]) && ! (params[0] instanceof BYOKRequestParams) ){
				throw new SecurityException("Param[0] must be byte[] (store) or BYOKRequestParams");
			}
			if((params[0] instanceof byte[])) {
				byte [] store = (byte[]) params[0];
				return new JWKSetStore(store);
			}
			else {
				BYOKRequestParams requestParams = (BYOKRequestParams) params[0];
				return new JWKSetStore(path, requestParams);
			}
		}
		else if(params.length==2){
			if( ! (params[0] instanceof byte[]) ){
				throw new SecurityException("Param[0] must be byte[] (store)");
			}
			if( ! (params[1] instanceof BYOKRequestParams) ){
				throw new SecurityException("Param[1] must be BYOKRequestParams");
			}
			byte [] store = (byte[]) params[0];
			BYOKRequestParams requestParams = (BYOKRequestParams) params[1];
			return new JWKSetStore(store, requestParams);
		}
		else{
			throw new SecurityException("Params [lenght:"+params.length+"] not supported");
		}
	}

	@Override
	public String getPrefixKey() {
		return "JWKSet ";
	}
}
