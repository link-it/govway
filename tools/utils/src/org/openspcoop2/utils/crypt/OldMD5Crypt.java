/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.crypt;

import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * OldMD5Crypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@Deprecated
public class OldMD5Crypt implements ICrypt {

	private Logger log;
	public OldMD5Crypt(){
	}
	
	@Override
	public void init(Logger log, CryptConfig config) {
		this.log = log;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String crypt(String password) throws UtilsException {
		String salt = MD5Crypt.newSalt();
		return MD5Crypt.crypt(password, salt);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean check(String password, String pwcrypt) {
		try{
			boolean checkPwS = false;
			String salt = pwcrypt.substring(3, 11);
			String newPw = MD5Crypt.crypt(password, salt);
			if (newPw.equals(pwcrypt)) {
				checkPwS = true;
			}
			return checkPwS;
		}catch(Throwable e){
			if(this.log!=null) {
				this.log.error("Verifica password '"+pwcrypt+"' fallita: "+e.getMessage(),e);
			}
			return false;
		}
	}

}
