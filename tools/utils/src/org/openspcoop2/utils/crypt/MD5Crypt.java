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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.openspcoop2.utils.random.RandomGenerator;

/**
 * A Java Implementation of the MD5Crypt function Modified from the GANYMEDE
 * network directory management system released under the GNU General Public
 * License by the University of Texas at Austin
 * http://tools.arlut.utexas.edu/gash2/ Original version from :Jonathan Abbey,
 * jonabbey@arlut.utexas.edu Modified by: Vladimir Silva,
 * vladimir_silva@yahoo.com Modification history: 9/2005 - Removed dependencies
 * on a MD5 private implementation - Added built-in java.security.MessageDigest
 * (MD5) support - Code cleanup
 *
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@Deprecated
public class MD5Crypt {
	// Character set allowed for the salt string
	static private final String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

	// Character set of the encrypted password: A-Za-z0-9./
	static private final String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public static String newSalt() {

        int c = 0;
        byte[] arrByte = new byte[2];

        RandomGenerator randomGenerator = new RandomGenerator(false);
        randomGenerator.nextRandomBytes(arrByte);
        
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
	
	/**
	 * Function to return a string from the set: A-Za-z0-9./
	 * 
	 * @return A string of size (size) from the set A-Za-z0-9./
	 * @param size
	 *            Length of the string
	 * @param v
	 *            value to be converted
	 */
	static private final String to64(long v, int size) {
		StringBuilder result = new StringBuilder();

		while (--size >= 0) {
			result.append(MD5Crypt.itoa64.charAt((int) (v & 0x3f)));
			v >>>= 6;
		}

		return result.toString();
	}

	static private final void clearbits(byte bits[]) {
		for (int i = 0; i < bits.length; i++) {
			bits[i] = 0;
		}
	}

	/**
	 * convert an encoded unsigned byte value into a int with the unsigned
	 * value.
	 */
	static private final int bytes2u(byte inp) {
		return inp & 0xff;
	}

	private static java.util.Random _rnd = null;
	private static synchronized void initRandom() {
		if(_rnd==null) {
			_rnd = new java.util.Random();
		}
	}
	private static java.util.Random getRandom() {
		if(_rnd==null) {
			initRandom();
		}
		return _rnd;
	}
	
	/**
	 * LINUX/BSD MD5Crypt function
	 * 
	 * @return The encrypted password as an MD5 hash
	 * @param password
	 *            Password to be encrypted
	 */
	static public final String crypt(String password) {
		StringBuilder salt = new StringBuilder();

		// build a random 8 chars salt
		while (salt.length() < 8) {
			int index = (int) (getRandom().nextFloat() * MD5Crypt.SALTCHARS.length());
			salt.append(MD5Crypt.SALTCHARS.substring(index, index + 1));
		}

		// crypt
		return MD5Crypt.crypt(password, salt.toString(), "$1$");
	}

	/**
	 * LINUX/BSD MD5Crypt function
	 * 
	 * @return The encrypted password as an MD5 hash
	 * @param salt
	 *            Random string used to initialize the MD5 engine
	 * @param password
	 *            Password to be encrypted
	 */
	static public final String crypt(String password, String salt) {
		return MD5Crypt.crypt(password, salt, "$1$");
	}

	/**
	 * Linux/BSD MD5Crypt function
	 * 
	 * @throws java.lang.Exception
	 * @return The encrypted password as an MD5 hash
	 * @param magic
	 *            $1$ for Linux/BSB, $apr1$ for Apache crypt
	 * @param salt
	 *            8 byte permutation string
	 * @param password
	 *            user password
	 */
	static public final String crypt(String password, String salt, String magic) {

		byte finalState[];
		long l;

		/**
		 * Two MD5 hashes are used
		 */
		MessageDigest ctx, ctx1;

		try {
			ctx = MessageDigest.getInstance("md5");
			ctx1 = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException ex) {
			System.err.println(ex);
			return null;
		}

		/* Refine the Salt first */
		/* If it starts with the magic string, then skip that */

		if (salt.startsWith(magic)) {
			salt = salt.substring(magic.length());
		}

		/* It stops at the first '$', max 8 chars */

		if (salt.indexOf('$') != -1) {
			salt = salt.substring(0, salt.indexOf('$'));
		}

		if (salt.length() > 8) {
			salt = salt.substring(0, 8);
		}

		/**
		 * Transformation set #1: The password first, since that is what is most
		 * unknown Magic string Raw salt
		 */
		ctx.update(password.getBytes());
		ctx.update(magic.getBytes());
		ctx.update(salt.getBytes());

		/* Then just as many characters of the MD5(pw,salt,pw) */

		ctx1.update(password.getBytes());
		ctx1.update(salt.getBytes());
		ctx1.update(password.getBytes());
		finalState = ctx1.digest(); // ctx1.Final();

		for (int pl = password.length(); pl > 0; pl -= 16) {
			ctx.update(finalState, 0, pl > 16 ? 16 : pl);
		}

		/**
		 * the original code claimed that finalState was being cleared to keep
		 * dangerous bits out of memory, but doing this is also required in
		 * order to get the right output.
		 */

		MD5Crypt.clearbits(finalState);

		/* Then something really weird... */

		for (int i = password.length(); i != 0; i >>>= 1) {
			if ((i & 1) != 0) {
				ctx.update(finalState, 0, 1);
			} else {
				ctx.update(password.getBytes(), 0, 1);
			}
		}

		finalState = ctx.digest();

		/**
		 * and now, just to make sure things don't run too fast On a 60 Mhz
		 * Pentium this takes 34 msec, so you would need 30 seconds to build a
		 * 1000 entry dictionary... (The above timings from the C version)
		 */

		for (int i = 0; i < 1000; i++) {
			try {
				ctx1 = MessageDigest.getInstance("md5");
			} catch (NoSuchAlgorithmException e0) {
				return null;
			}

			if ((i & 1) != 0) {
				ctx1.update(password.getBytes());
			} else {
				ctx1.update(finalState, 0, 16);
			}

			if ((i % 3) != 0) {
				ctx1.update(salt.getBytes());
			}

			if ((i % 7) != 0) {
				ctx1.update(password.getBytes());
			}

			if ((i & 1) != 0) {
				ctx1.update(finalState, 0, 16);
			} else {
				ctx1.update(password.getBytes());
			}

			finalState = ctx1.digest(); // Final();
		}

		/* Now make the output string */

		StringBuilder result = new StringBuilder();

		result.append(magic);
		result.append(salt);
		result.append("$");

		/**
		 * Build a 22 byte output string from the set: A-Za-z0-9./
		 */
		l = (MD5Crypt.bytes2u(finalState[0]) << 16) | (MD5Crypt.bytes2u(finalState[6]) << 8) | MD5Crypt.bytes2u(finalState[12]);
		result.append(MD5Crypt.to64(l, 4));

		l = (MD5Crypt.bytes2u(finalState[1]) << 16) | (MD5Crypt.bytes2u(finalState[7]) << 8) | MD5Crypt.bytes2u(finalState[13]);
		result.append(MD5Crypt.to64(l, 4));

		l = (MD5Crypt.bytes2u(finalState[2]) << 16) | (MD5Crypt.bytes2u(finalState[8]) << 8) | MD5Crypt.bytes2u(finalState[14]);
		result.append(MD5Crypt.to64(l, 4));

		l = (MD5Crypt.bytes2u(finalState[3]) << 16) | (MD5Crypt.bytes2u(finalState[9]) << 8) | MD5Crypt.bytes2u(finalState[15]);
		result.append(MD5Crypt.to64(l, 4));

		l = (MD5Crypt.bytes2u(finalState[4]) << 16) | (MD5Crypt.bytes2u(finalState[10]) << 8) | MD5Crypt.bytes2u(finalState[5]);
		result.append(MD5Crypt.to64(l, 4));

		l = MD5Crypt.bytes2u(finalState[11]);
		result.append(MD5Crypt.to64(l, 2));

		/* Don't leave anything around in vm they could use. */
		MD5Crypt.clearbits(finalState);

		return result.toString();
	}

	/**
	 * Test subroutine
	 * 
	 * @param args
	 */
	static final String USAGE = "MD5Crypt <password> <salt>";

	public static void main(String[] args) {
		try {
			if (args.length != 2) {
				System.err.println(MD5Crypt.USAGE);
			} else {
				System.out.println(MD5Crypt.crypt(args[0], args[1]));
			}

		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

}
