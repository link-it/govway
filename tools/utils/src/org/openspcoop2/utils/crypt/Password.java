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


package org.openspcoop2.utils.crypt;

import java.util.Random;

/**
 * Gestione password
 *
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Password {

	public static void main(String[] args) {
		// Metodo utilizzato dal setup antinstaller
		String pwsu = (String) args[0];
		Password procToCall = new Password();
		System.out.println(procToCall.cryptPw(pwsu));
	}
	
	
	public int number;
	public String[][] datiConf = new String[20][2];


	public String newPw(String pwLower) {

		Random rd;
		int c = 0;
		byte[] arrByte = new byte[8];

		rd = new Random();
		rd.nextBytes(arrByte);

		for (int i = 0; i < 8; i++) {
			c = arrByte[i] >> 6; // div(64)
			c = (arrByte[i] - (c << 6));
			if (c <= 11) {
				c += 46;
			}
			if ((c >= 12) && (c <= 37)) {
				if (pwLower.equals("no")) {
					c += (65 - 12);
				} else {
					c += (97 - 12);
				}
			}
			if ((c >= 38) && (c <= 63)) {
				c += (97 - 38);
			}
			arrByte[i] = (byte) c;
		}

		String pw = new String(arrByte);
		return pw;
	}

	public String newSalt() {

        Random rd;
        int c = 0;
        byte[] arrByte = new byte[2];

        rd = new Random();
        rd.nextBytes(arrByte);
        
        for (int i = 0; i<2; i++) {
            c = arrByte[i] >> 6;  // div(64)
            c = (arrByte[i] - (c<<6));
            if ( c <= 11 ) // 46-57 ./0..9
                c+=46;
            if ( c >= 12 && c <= 37 ) // 65-90 a..z
               c+=(65-12);     
            if ( c >= 38 && c <= 63 ) // 97-122 A..Z
                c+=(97-38);
            arrByte[i] = (byte) c;
        }

        String pw = new String(arrByte);
        return pw;
    }
		
	public String cryptPw(String password) {
		String mypwcryptS = "";

		// Genero un salt casuale
		//byte[] saltByte = new byte[12];
		//SecureRandom sr = new SecureRandom();
		//sr.nextBytes(saltByte);
		//String salt = new String(saltByte);
		String salt = newSalt();
		
		// MD5Crypt md5c = new MD5Crypt();
		mypwcryptS = MD5Crypt.crypt(password, salt);

		return mypwcryptS;
	}

	public boolean checkPw(String password, String pwcrypt) {
		boolean checkPwS = false;

		// MD5Crypt md5c = new MD5Crypt();
		String salt = pwcrypt.substring(3, 11);
		String newPw = MD5Crypt.crypt(password, salt);
		if (newPw.equals(pwcrypt)) {
			checkPwS = true;
		}

		return checkPwS;
	}
}
