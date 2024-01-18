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
import org.openspcoop2.security.keystore.CRLCertstore;
import org.openspcoop2.security.keystore.HttpStore;
import org.openspcoop2.security.keystore.MerlinTruststore;

/**
 * MerlinTruststoreCache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpStoreCache extends AbstractKeystoreCache<HttpStore> {

	@Override
	public HttpStore createKeystore(String key, Object... params) throws SecurityException{
		if(params==null){
			throw new SecurityException("Params is null");
		}
		String endpoint = key;
		if(params.length==0){
			return new HttpStore(endpoint);
		}
		else if(params.length==1){
			if( ! (params[0] instanceof MerlinTruststore) ){
				throw new SecurityException("Param[0] must be MerlinTruststore (trustStoreSsl)");
			}
			MerlinTruststore trustStoreSsl = (MerlinTruststore) params[0];
			return new HttpStore(endpoint, trustStoreSsl);
		}
		else if(params.length==2){
			if( ! (params[0] instanceof MerlinTruststore) &&  ! (params[0] instanceof Integer)){
				throw new SecurityException("Param[0] must be Integer (connectionTimeout) or MerlinTruststore (trustStoreSsl)");
			}
			if(params[0] instanceof MerlinTruststore){
				if( ! (params[1] instanceof CRLCertstore) ){
					throw new SecurityException("Param[1] must be CRLCertstore (crlStoreSsl)");
				}
				MerlinTruststore trustStoreSsl = (MerlinTruststore) params[0];
				CRLCertstore crlStoreSsl = (CRLCertstore) params[1];
				return new HttpStore(endpoint, trustStoreSsl, crlStoreSsl);
			}
			else {
				if( ! (params[1] instanceof Integer) ){
					throw new SecurityException("Param[1] must be Integer (readTimeout)");
				}
				Integer connectionTimeout = (Integer) params[0];
				Integer readTimeout = (Integer) params[1];
				return new HttpStore(endpoint, connectionTimeout, readTimeout);
			}
		}
		else if(params.length==3){
			if( ! (params[0] instanceof Integer)){
				throw new SecurityException("Param[0] must be Integer (connectionTimeout)");
			}
			if( ! (params[1] instanceof Integer) ){
				throw new SecurityException("Param[1] must be Integer (readTimeout)");
			}
			if( ! (params[2] instanceof MerlinTruststore) ){
				throw new SecurityException("Param[2] must be MerlinTruststore (trustStoreSsl)");
			}
			Integer connectionTimeout = (Integer) params[0];
			Integer readTimeout = (Integer) params[1];
			MerlinTruststore trustStoreSsl = (MerlinTruststore) params[2];
			return new HttpStore(endpoint, 
					connectionTimeout, readTimeout,
					trustStoreSsl);
		}
		else if(params.length==4){
			if( ! (params[0] instanceof Integer)){
				throw new SecurityException("Param[0] must be Integer (connectionTimeout)");
			}
			if( ! (params[1] instanceof Integer) ){
				throw new SecurityException("Param[1] must be Integer (readTimeout)");
			}
			if( ! (params[2] instanceof MerlinTruststore) ){
				throw new SecurityException("Param[2] must be MerlinTruststore (trustStoreSsl)");
			}
			if( ! (params[3] instanceof CRLCertstore) ){
				throw new SecurityException("Param[3] must be CRLCertstore (crlStoreSsl)");
			}
			Integer connectionTimeout = (Integer) params[0];
			Integer readTimeout = (Integer) params[1];
			MerlinTruststore trustStoreSsl = (MerlinTruststore) params[2];
			CRLCertstore crlStoreSsl = (CRLCertstore) params[3];
			return new HttpStore(endpoint, 
					connectionTimeout, readTimeout,
					trustStoreSsl, crlStoreSsl);
		}
		else{
			throw new SecurityException("Params [lenght:"+params.length+"] not supported");
		}
	}

	@Override
	public String getPrefixKey() {
		return "HttpStore ";
	}
}
