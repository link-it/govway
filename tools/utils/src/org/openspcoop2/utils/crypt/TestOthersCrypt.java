/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;

/**
 * TestCodecCrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestOthersCrypt {

	public static void main(String[] args) throws Exception {
		
		CryptType [] type = new CryptType[4];
		type[0] = CryptType.PBE_KEY_SPEC;
		type[1] = CryptType.B_CRYPT;
		type[2] = CryptType.S_CRYPT;
		type[3] = CryptType.PLAIN;
		
		String [] digestAlgo = new String[6];
		digestAlgo[0] = null; // default
		digestAlgo[1] = "PBKDF2WithHmacSHA1";
		digestAlgo[2] = "PBKDF2WithHmacSHA224";
		digestAlgo[3] = "PBKDF2WithHmacSHA256";
		digestAlgo[4] = "PBKDF2WithHmacSHA384";
		digestAlgo[5] = "PBKDF2WithHmacSHA512";

		for (CryptType cryptType : type) {
		
			if(!CryptType.PBE_KEY_SPEC.equals(cryptType)) {
				
				test(cryptType, false, null, null, null);
					
			}
			else {

				for (int j = 0; j < digestAlgo.length; j++) {
			
					String algo = digestAlgo[j];
			
					test(cryptType, false, null, null, algo);
					
					test(cryptType, true, null, null, algo);
					
					for (int i = 1; i <= 5; i++) {
						test(cryptType, false, (4*i), null, algo);
					
						test(cryptType, true, (4*i), null, algo);
					}
					
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
						
						test(cryptType, false, (4*i), iteration, algo);
						
						test(cryptType, true, (4*i), iteration, algo);
					}
				}
				
			}
		
		}

	}
	
	public static void test(CryptType type, boolean useSecureRandom, Integer saltLength, Integer iteration, String digestAlgo) throws Exception {
		
		StringBuilder sb = new StringBuilder(type.name());
		CryptConfig config = null;
		if(CryptType.PBE_KEY_SPEC.equals(type)) {
			sb.append(" secureRandom:").append(useSecureRandom);
			if(saltLength!=null) {
				sb.append(" saltLength:").append(saltLength);
			}
			if(iteration!=null) {
				sb.append(" iteration:").append(iteration);
			}
			if(digestAlgo!=null) {
				sb.append(" digestAlgo:").append(digestAlgo);
			}
				
			config = new CryptConfig();
			config.setUseSecureRandom(useSecureRandom);
			if(saltLength!=null) {
				config.setSaltLength(saltLength);
			}
			if(iteration!=null) {
				config.setIteration(iteration);
			}
			if(digestAlgo!=null) {
				config.setDigestAlgorithm(digestAlgo);
			}
		}
		
		System.out.println("\n\n ------------------- "+sb.toString()+" --------------------");
		
		 
		
		
		ICrypt passwordEngine = CryptFactory.getCrypt(type,config);
		System.out.println("CLASS: "+passwordEngine.getClass().getName());
		
		
		// test nuova generazione e verifica
			
		String password = "Pr@va.diUn@altroDiverso";
		boolean verificaPasswordDiversa = true;
		if(CryptType.PLAIN.equals(type)) {
			verificaPasswordDiversa = false;
		}
		String passwordEncrypted = passwordEngine.crypt(password);
		if(verificaPasswordDiversa) {
			String passwordEncrypted2 = passwordEngine.crypt(password);
			String passwordEncrypted3 = passwordEngine.crypt(password);
			if(passwordEncrypted.equals(passwordEncrypted2) ) {
				throw new Exception("Attesa generazione di una password cifrata differente; trovata sempre '1' "+passwordEncrypted);
			}
			if(passwordEncrypted.equals(passwordEncrypted3) ) {
				throw new Exception("Attesa generazione di una password cifrata differente; trovata sempre '2' "+passwordEncrypted);
			}
			if(passwordEncrypted2.equals(passwordEncrypted3) ) {
				throw new Exception("Attesa generazione di una password cifrata differente; trovata sempre '3' "+passwordEncrypted2);
			}
		}
		boolean verifica = passwordEngine.check(password, passwordEncrypted);
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
		
		if(config!=null && CryptType.PBE_KEY_SPEC.equals(type)) {
			config.setUseBase64Encoding(false);
			passwordEngine = CryptFactory.getCrypt(type,config);
			String passwordEncryptedHex = passwordEngine.crypt(password);
			verifica = passwordEngine.check(password, passwordEncryptedHex);
			if(!verifica) {
				throw new Exception("Verifica password hex '"+password+"' fallita");
			}
			if(passwordEncrypted.equals(passwordEncryptedHex) ) {
				throw new Exception("Attesa generazione di una password cifrata 'hex' differente; trovata sempre '1' "+passwordEncrypted);
			}
			String [] split = passwordEncrypted.split(":");
			try {
				Base64Utilities.decode(split[1]);
			}catch(Exception e) {
				throw new Exception("Verifica fallita della struttura base64 della password cifrata  ["+passwordEncrypted+"] split[1]["+split[1]+"]: "+e.getMessage(),e);
			}
			try {
				Base64Utilities.decode(split[2]);
			}catch(Exception e) {
				throw new Exception("Verifica fallita della struttura base64 della password cifrata  ["+passwordEncrypted+"] split[2]["+split[2]+"]: "+e.getMessage(),e);
			}
			split = passwordEncryptedHex.split(":");
			try {
				HexBinaryUtilities.decode(split[1]);
			}catch(Exception e) {
				throw new Exception("Verifica fallita della struttura hex della password cifrata  ["+passwordEncryptedHex+"] split[1]["+split[1]+"]: "+e.getMessage(),e);
			}
			try {
				HexBinaryUtilities.decode(split[2]);
			}catch(Exception e) {
				throw new Exception("Verifica fallita della struttura hex della password cifrata  ["+passwordEncryptedHex+"] split[2]["+split[2]+"]: "+e.getMessage(),e);
			}
					
			System.out.println("password generata 'hex': "+passwordEncryptedHex);
		}
		
		System.out.println("password generata 'base64': "+passwordEncrypted);
		System.out.println("OK");
	}
	
}
