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

import org.openspcoop2.utils.UtilsException;

/**
 * Gestione password
 *
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Password {

	public static void main(String[] args) throws UtilsException {
		// Metodo utilizzato dal setup antinstaller
		String pwsu = (String) args[0];
		ICrypt crypt = CryptFactory.getCrypt(CryptType.SHA2_BASED_UNIX_CRYPT_SHA512);
		System.out.println(crypt.crypt(pwsu));
	}
	
}
