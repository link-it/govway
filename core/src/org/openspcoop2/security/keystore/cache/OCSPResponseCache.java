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

import java.security.cert.X509Certificate;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.OCSPResponse;
import org.openspcoop2.utils.transport.http.IOCSPValidator;

/**
 * OCSPResponseCache
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OCSPResponseCache extends AbstractKeystoreCache<OCSPResponse> {

	@Override
	public OCSPResponse createKeystore(String key, Object... params) throws SecurityException{
		if(params==null){
			throw new SecurityException("Params is null");
		}
		else if(params.length==2){
			if(params[0]==null) {
				throw new SecurityException("Param[0] must be not null");
			}
			if( ! (params[0] instanceof IOCSPValidator ) ){
				throw new SecurityException("Param[0] must be IOCSPValidator");
			}
			IOCSPValidator validator = (IOCSPValidator) params[0];
			
			if(params[1]==null) {
				throw new SecurityException("Param[1] must be not null");
			}
			if( ! (params[1] instanceof X509Certificate ) ){
				throw new SecurityException("Param[1] must be X509Certificate");
			}
			X509Certificate cer = (X509Certificate) params[1];
			
			return new OCSPResponse(validator, cer);
		}
		else{
			throw new SecurityException("Params [lenght:"+params.length+"] not supported");
		}
	}

	@Override
	public String getPrefixKey() {
		return "OCSPResponse ";
	}
}
