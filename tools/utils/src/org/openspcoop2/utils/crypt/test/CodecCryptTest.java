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

package org.openspcoop2.utils.crypt.test;

import org.openspcoop2.utils.crypt.CodecType;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.CryptFactory;
import org.openspcoop2.utils.crypt.ICrypt;

/**
 * CodecCryptTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CodecCryptTest {

	public static void main(String[] args) throws Exception {
		test();
	}
	
	public static void test() throws Exception {
		
		CodecType [] types = CodecType.values();
		
		for (CodecType cryptType : types) {
			
			test(cryptType, false, null, null);
			
			test(cryptType, true, null, null);
			
			for (int i = 1; i <= 5; i++) {
				test(cryptType, false, (4*i), null);
				
				test(cryptType, true, (4*i), null);
			}
			
		}
		types = new CodecType[2];
		types[0] = CodecType.SHA2_BASED_UNIX_CRYPT_SHA256;
		types[1] = CodecType.SHA2_BASED_UNIX_CRYPT_SHA512;
		for (CodecType cryptType : types) {
			
			int iteration = 1;
			for (int i = 1; i <= 5; i++) {
				
				if(i==2) {
					iteration = 100;
				}
				else if(i==3) {
					iteration = 1000;
				}
				else if(i==4) {
					iteration = 5000;
				}
				else if(i==5) {
					iteration = 10000;
				}
				
				test(cryptType, false, (4*i), iteration);
				
				test(cryptType, true, (4*i), iteration);
			}
						
		}
		
	}
	
	public static void test(CodecType type, boolean useSecureRandom, Integer saltLength, Integer iteration) throws Exception {
		
		StringBuilder sb = new StringBuilder(type.name());
		sb.append(" secureRandom:").append(useSecureRandom);
		if(saltLength!=null) {
			sb.append(" saltLength:").append(saltLength);
		}
		if(iteration!=null) {
			sb.append(" iteration:").append(iteration);
		}
		
		System.out.println("\n\n ------------------- "+sb.toString()+" --------------------");
		
		CryptConfig config = new CryptConfig();
		config.setUseSecureRandom(useSecureRandom);
		if(saltLength!=null) {
			config.setSaltLength(saltLength);
		}
		if(iteration!=null) {
			config.setIteration(iteration);
		}
		
		
		ICrypt passwordEngine = CryptFactory.getCrypt(type.toCryptType(), config);
		
		
		// Verifica password prelevata dal database (solo se MD5)
		
		if(CodecType.LIBC_CRYPT_MD5.equals(type)) {
			String passwordEncrypted = "$1$il$SPDpFtwmXna8U/e.t9IuP.";
			boolean verifica = passwordEngine.check("123456", passwordEncrypted);
			if(!verifica) {
				throw new Exception("["+sb.toString()+"] Verifica password di default fallita");
			}
			verifica = passwordEngine.check("1234567", passwordEncrypted);
			if(verifica) {
				throw new Exception("["+sb.toString()+"] Attesa verifica fallita per password di default");
			}
			
			System.out.println("OK; password di default 123456");
		}
		
		
		// Verifica password generata con openssl:
		/*
		 * LIBC_CRYPT_MD5: 
		 * openssl passwd -1 123456
		 *	$1$4hG9hTWx$getysg42XDQzoOmMswdX51
		 *
		 * LIBC_CRYPT_MD5_APACHE
		 * openssl passwd -apr1 123456
		 *  $apr1$JjhIpgEA$DqHJzYnkPMZgk0bm6OLmk0
		 *  
		 * SHA2_BASED_UNIX_CRYPT_SHA256
		 * openssl passwd -5 123456
		 *  $5$D7NZJ1aJP6v7z2fc$CVRwPYaZYnCEJbbb9HmGW2dXWVv0mTJ5BLT5.wT8h.0
		 *  
		 * SHA2_BASED_UNIX_CRYPT_SHA512
		 * openssl passwd -6 123456
		 *  $6$nO2cyTasmEO/jRKE$x87pBA4H3XEZx1Y9jVZ7JwueMIlBIluxe6rcXPghJO0tuBICx6Ni2g4oxofVrONuOVbHJGp5vQkTYxFo79wx10
		 *  
		 * DES_UNIX_CRYPT
		 * openssl passwd -crypt 123456
		 *  IGN5Kj/UUU.nY
		 */
		
		String passwordEncrypted = null;
		switch (type) {
		case LIBC_CRYPT_MD5:
			passwordEncrypted = "$1$4hG9hTWx$getysg42XDQzoOmMswdX51";
			break;
		case LIBC_CRYPT_MD5_APACHE:
			passwordEncrypted = "$apr1$JjhIpgEA$DqHJzYnkPMZgk0bm6OLmk0";
			break;
		case SHA2_BASED_UNIX_CRYPT_SHA256:
			passwordEncrypted = "$5$D7NZJ1aJP6v7z2fc$CVRwPYaZYnCEJbbb9HmGW2dXWVv0mTJ5BLT5.wT8h.0";
			break;
		case SHA2_BASED_UNIX_CRYPT_SHA512:
			passwordEncrypted = "$6$nO2cyTasmEO/jRKE$x87pBA4H3XEZx1Y9jVZ7JwueMIlBIluxe6rcXPghJO0tuBICx6Ni2g4oxofVrONuOVbHJGp5vQkTYxFo79wx10";
			break;
		case DES_UNIX_CRYPT:
			passwordEncrypted = "IGN5Kj/UUU.nY";
			break;
		}
		boolean verifica = passwordEngine.check("123456", passwordEncrypted);
		if(!verifica) {
			throw new Exception("["+sb.toString()+"] Verifica password 'openssl' fallita");
		}
		verifica = passwordEngine.check("1234567", passwordEncrypted);
		if(verifica) {
			throw new Exception("["+sb.toString()+"] Attesa verifica fallita per password 'openssl'");
		}
		
		System.out.println("OK; verifica openssl: "+passwordEncrypted);
		
		
		// test nuova generazione e verifica
			
		String password = "Pr@va.diUn@altroDiverso";
		if(CodecType.DES_UNIX_CRYPT.equals(type)) {
			// Only the first 8 chars of the passwords are used in the DES algorithm!
			password = "Pr@va";
		}
		passwordEncrypted = passwordEngine.crypt(password);
		String passwordEncrypted2 = passwordEngine.crypt(password);
		String passwordEncrypted3 = passwordEngine.crypt(password);
		if(passwordEncrypted.equals(passwordEncrypted2) ) {
			throw new Exception("["+sb.toString()+"] Attesa generazione di una password cifrata differente; trovata sempre '1' "+passwordEncrypted);
		}
		if(passwordEncrypted.equals(passwordEncrypted3) ) {
			throw new Exception("["+sb.toString()+"] Attesa generazione di una password cifrata differente; trovata sempre '2' "+passwordEncrypted);
		}
		if(passwordEncrypted2.equals(passwordEncrypted3) ) {
			throw new Exception("["+sb.toString()+"] Attesa generazione di una password cifrata differente; trovata sempre '3' "+passwordEncrypted2);
		}
		verifica = passwordEngine.check(password, passwordEncrypted);
		if(!verifica) {
			throw new Exception("["+sb.toString()+"] Verifica password '"+password+"' fallita");
		}
		verifica = passwordEngine.check(password+"7", passwordEncrypted);
		if(verifica) {
			throw new Exception("["+sb.toString()+"] Attesa verifica fallita per password '"+password+"7'");
		}
		verifica = passwordEngine.check(password+"ERR", passwordEncrypted);
		if(verifica) {
			throw new Exception("["+sb.toString()+"] Attesa verifica fallita per password '"+password+"ERR'");
		}
		
		String salt = null;
		if(!CodecType.DES_UNIX_CRYPT.equals(type)) {
			salt = getCryptoSalt(passwordEncrypted, iteration, type);
			
			int defaultSaltLength = -1;
			int maxSaltLength = Integer.MAX_VALUE;
			switch (type) {
			case LIBC_CRYPT_MD5:
			case LIBC_CRYPT_MD5_APACHE:
				defaultSaltLength = 8;
				maxSaltLength = 8;
				break;
			case SHA2_BASED_UNIX_CRYPT_SHA256:
			case SHA2_BASED_UNIX_CRYPT_SHA512:
				defaultSaltLength = 16;
				maxSaltLength = 16;
				break;
			case DES_UNIX_CRYPT:
				defaultSaltLength = 2;
				maxSaltLength = 2;
				break;
			}
			
			int saltLengthVerify = -1;
			if(saltLength!=null) {
				saltLengthVerify = saltLength.intValue();
			}
			else {
				saltLengthVerify = defaultSaltLength;
			}
			
			if(saltLengthVerify> maxSaltLength) {
				if(salt.length()!=defaultSaltLength) {
					throw new Exception("["+sb.toString()+"] Lunghezza salt attesa '"+defaultSaltLength+"' (force default) differente da quella trovata '"+salt.length()+"': "+salt);
				}
			}
			else {
				if(salt.length()!=saltLengthVerify) {
					throw new Exception("["+sb.toString()+"] Lunghezza salt attesa '"+saltLengthVerify+"' differente da quella trovata '"+salt.length()+"': "+salt);
				}
			}
		}
		
		if(salt!=null) {
			System.out.println("salt ("+salt.length()+"): "+salt);
		}
		System.out.println("OK; password generata: "+passwordEncrypted);
	}
	
	private static String getCryptoSalt(String newS, Integer iteration, CodecType type) {
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		int limit = 3;
		if(iteration!=null && (CodecType.SHA2_BASED_UNIX_CRYPT_SHA256.equals(type) || CodecType.SHA2_BASED_UNIX_CRYPT_SHA512.equals(type))) {
			limit = 4;
		} 
		for (int i = 0; i < newS.length(); i++) {
			char c = newS.charAt(i);
			if('$' ==  c) {
				counter++;
				if(counter==limit) {
					break;
				}
			}
			else {
				if(counter==(limit-1)) {
					sb.append(c);
				}
			}
			//System.out.println("AAA ["+i+"]=["+newS.charAt(i)+"] (equals: "+"$".equals(newS.charAt(i))+") (equals2: "+('$' == newS.charAt(i))+")");
		}
		return sb.toString();
	}
}
