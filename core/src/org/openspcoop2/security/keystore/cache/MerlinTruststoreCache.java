/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import org.openspcoop2.security.keystore.MerlinTruststore;

/**
 * MerlinTruststoreCache
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MerlinTruststoreCache extends AbstractKeystoreCache<MerlinTruststore> {

	@Override
	public MerlinTruststore createKeystore(String key, Object... params) throws SecurityException{
		if(params==null){
			throw new SecurityException("Params is null");
		}
		if(params.length==0){
			String propertyFilePath = key;
			return new MerlinTruststore(propertyFilePath);
		}
		else if(params.length==2){
			if( ! (params[0] instanceof String) ){
				throw new SecurityException("Param[0] must be String (tipoStore)");
			}
			if( ! (params[1] instanceof String) ){
				throw new SecurityException("Param[1] must be String (passwordStore)");
			}
			String pathStore = key;
			String tipoStore = (String) params[0];
			String passwordStore = (String) params[1];
			return new MerlinTruststore(pathStore, tipoStore, passwordStore);
		}
		else{
			throw new SecurityException("Params [lenght:"+params.length+"] not supported");
		}
	}

}
