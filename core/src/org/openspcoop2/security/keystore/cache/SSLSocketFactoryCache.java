/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.SSLSocketFactory;
import org.openspcoop2.utils.transport.http.SSLConfig;

/**
 * SSLSocketFactoryCache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SSLSocketFactoryCache extends AbstractKeystoreCache<SSLSocketFactory> {

	@Override
	public SSLSocketFactory createKeystore(String key, Object... params)
			throws SecurityException {
		if(params==null){
			throw new SecurityException("Params is null");
		}
		if(params.length==2){
			if( ! (params[0] instanceof RequestInfo) ){
				throw new SecurityException("Param[0] must be RequestInfo");
			}
			RequestInfo requestInfo = (RequestInfo) params[0];
			if( ! (params[1] instanceof SSLConfig) ){
				throw new SecurityException("Param[1] must be SSLConfig");
			}
			SSLConfig sslConfig = (SSLConfig) params[1];
			return new SSLSocketFactory(requestInfo,sslConfig);
		}
		else{
			throw new SecurityException("Params [lenght:"+params.length+"] not supported");
		}
	}

	@Override
	public String getPrefixKey() {
		return "SSLSocketFactory ";
	}
}
