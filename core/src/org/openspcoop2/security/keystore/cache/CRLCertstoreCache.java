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

import java.util.Map;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.CRLCertstore;

/**
 * CRLCertstoreCache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CRLCertstoreCache extends AbstractKeystoreCache<CRLCertstore> {

	@SuppressWarnings("unchecked")
	@Override
	public CRLCertstore createKeystore(String key, Object... params) throws SecurityException{
		if(params==null){
			throw new SecurityException("Params is null");
		}
		if(params.length==0){
			String propertyCrlPath = key;
			return new CRLCertstore(propertyCrlPath);
		}
		else if(params.length==1){
			if( ! (params[0] instanceof Map ) ){
				throw new SecurityException("Param[0] must be Map<String, byte[]>");
			}
			Map<String, byte[]>  localResources = null;
			try {
				localResources = (Map<String, byte[]> ) params[0];
			}catch(Exception t) {
				throw new SecurityException("Param[0] must be Map<String, byte[]>: "+t.getMessage(),t);
			}
			String propertyCrlPath = key;
			return new CRLCertstore(propertyCrlPath, localResources);
		}
		else{
			throw new SecurityException("Params [lenght:"+params.length+"] not supported");
		}
	}

	@Override
	public String getPrefixKey() {
		return "CRLCertstore ";
	}
}
