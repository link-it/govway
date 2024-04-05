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
import org.openspcoop2.security.keystore.MultiKeystore;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;

/**
 * MultiKeystoreCache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MultiKeystoreCache extends AbstractKeystoreCache<MultiKeystore> {

	@Override
	public MultiKeystore createKeystore(String key, Object... params)
			throws SecurityException {
		String propertyFilePath = key;
		if(params==null || params.length<=0){
			return new MultiKeystore(propertyFilePath);
		}
		if(params.length==1){
			if( ! (params[0] instanceof BYOKRequestParams) ){
				throw new SecurityException("Param[0] must be BYOKRequestParams");
			}
			BYOKRequestParams requestParams = (BYOKRequestParams) params[0];
			return new MultiKeystore(propertyFilePath,requestParams);
		}
		else{
			throw new SecurityException("Params [lenght:"+params.length+"] not supported");
		}
	}

	@Override
	public String getPrefixKey() {
		return "MultiKeystore ";
	}
}
