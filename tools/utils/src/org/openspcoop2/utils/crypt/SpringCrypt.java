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

package org.openspcoop2.utils.crypt;

import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * SpringCrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SpringCrypt implements ICrypt {

	private Logger log;
	private SpringType type;
	
	public SpringCrypt(SpringType type){
		this.type = type;
		if(this.type == null) {
			this.type = SpringType.B_CRYPT;
		}
	}
	
	@Override
	public void init(Logger log, CryptConfig config) {
		this.log = log;	
	}
	
	@Override
	public String crypt(String password) throws UtilsException {
		
		switch (this.type) {
		case B_CRYPT:
			org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder b = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
			return b.encode(password);
		case S_CRYPT:
			org.springframework.security.crypto.scrypt.SCryptPasswordEncoder s = new org.springframework.security.crypto.scrypt.SCryptPasswordEncoder();
			return s.encode(password);
		}
		throw new UtilsException("Unsupported type '"+this.type+"'");
		
	}
	
	@Override
	public boolean check(String password, String pwcrypt) {
		try {
			
			switch (this.type) {
			case B_CRYPT:
				org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder b = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
				return b.matches(password, pwcrypt);
			case S_CRYPT:
				org.springframework.security.crypto.scrypt.SCryptPasswordEncoder s = new org.springframework.security.crypto.scrypt.SCryptPasswordEncoder();
				return s.matches(password, pwcrypt);
			}
			throw new UtilsException("Unsupported type '"+this.type+"'");
			
		}catch(Throwable e){
			if(this.log!=null) {
				this.log.error("Verifica password '"+pwcrypt+"' fallita: "+e.getMessage(),e);
			}
			return false;
		}
	}

}