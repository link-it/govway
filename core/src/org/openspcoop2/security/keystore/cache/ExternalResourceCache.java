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
import org.openspcoop2.security.keystore.ExternalResource;
import org.openspcoop2.utils.transport.http.ExternalResourceConfig;

/**
 * ExternalResourceCache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExternalResourceCache extends AbstractKeystoreCache<ExternalResource> {	

	@Override
	public ExternalResource createKeystore(String key, Object... params) throws SecurityException{
		if(params==null){
			throw new SecurityException("Params is null");
		}
		if(params.length==0){
			return new ExternalResource(key, key, new ExternalResourceConfig()); // l'id della risorsa esterna è la url stessa della risorsa, non serve un id aggiuntivo
		}
		else if(params.length==1){
			if( ! (params[0] instanceof ExternalResourceConfig) ){
				throw new SecurityException("Param[0] must be ExternalResourceConfig");
			}
			ExternalResourceConfig externalConfig = (ExternalResourceConfig) params[0];
			return new ExternalResource(key, key, externalConfig); // l'id della risorsa esterna è la url stessa della risorsa, non serve un id aggiuntivo
		}
		else{
			throw new SecurityException("Params [lenght:"+params.length+"] not supported");
		}
	}

	@Override
	public String getPrefixKey() {
		return "ExternalResource ";
	}
}
