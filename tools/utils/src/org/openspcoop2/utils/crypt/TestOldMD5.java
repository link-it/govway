/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

/**
 * Test
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestOldMD5 {

	public static void main(String[] args) throws Exception {
		
		test();
		
	}
	
	public static void test() throws Exception {
		
		ICrypt passwordEngine = CryptFactory.getOldMD5Crypt();
		
		// Verifica password prelevata dal database
		
		String passwordEncrypted = "$1$il$SPDpFtwmXna8U/e.t9IuP.";
		boolean verifica = passwordEngine.check("123456", passwordEncrypted);
		if(!verifica) {
			throw new Exception("Verifica password di default fallita");
		}
		verifica = passwordEngine.check("1234567", passwordEncrypted);
		if(verifica) {
			throw new Exception("Attesa verifica fallita per password di default");
		}
		
		
		// Verifica password generata con openssl:
		/*
		 * openssl passwd -1 123456
			$1$4hG9hTWx$getysg42XDQzoOmMswdX51
		 */
		
		passwordEncrypted = "$1$4hG9hTWx$getysg42XDQzoOmMswdX51";
		verifica = passwordEngine.check("123456", passwordEncrypted);
		if(!verifica) {
			throw new Exception("Verifica password 'openssl' fallita");
		}
		verifica = passwordEngine.check("1234567", passwordEncrypted);
		if(verifica) {
			throw new Exception("Attesa verifica fallita per password 'openssl'");
		}
		
		
		// test nuova generazione e verifica
			
		String password = "Pr@va.diUn@altroDiverso";
		passwordEncrypted = passwordEngine.crypt(password);
		verifica = passwordEngine.check(password, passwordEncrypted);
		if(!verifica) {
			throw new Exception("Verifica password '"+password+"' fallita");
		}
		verifica = passwordEngine.check(password+"7", passwordEncrypted);
		if(verifica) {
			throw new Exception("Attesa verifica fallita per password '"+password+"7'");
		}
		verifica = passwordEngine.check(password+"ERR", passwordEncrypted);
		if(verifica) {
			throw new Exception("Attesa verifica fallita per password '"+password+"ERR'");
		}
		
		System.out.println("OK");
	}
}
