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

package org.openspcoop2.utils.crypt.test;

import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.CryptFactory;
import org.openspcoop2.utils.crypt.CryptType;
import org.openspcoop2.utils.crypt.ICrypt;
import org.openspcoop2.utils.crypt.JasyptCrypt;
import org.openspcoop2.utils.crypt.JasyptType;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;

/**
 * TestCodecCrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JasyptCryptTest {

	public static void main(String[] args) throws Exception {
		test();
	}
	
	public static void test() throws Exception {
		
		JasyptType [] types = JasyptType.values();
		
		String [] digestAlgo = new String[5];
		digestAlgo[0] = null; // default
		digestAlgo[1] = "MD5";
		digestAlgo[2] = "SHA1";
		digestAlgo[3] = "SHA-256";
		digestAlgo[4] = "SHA-512";
		
		for (int j = 0; j < digestAlgo.length; j++) {
		
			String algo = digestAlgo[j];
			
			for (JasyptType jasyptType : types) {
				test(jasyptType, false, null, null, algo);
			}
			
			test(null, false, null, null, algo);
			
			test(null, true, null, null, algo);
			
			for (int i = 1; i <= 5; i++) {
				test(null, false, (4*i), null, algo);
			
				test(null, true, (4*i), null, algo);
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
				
				test(null, false, (4*i), iteration, algo);
				
				test(null, true, (4*i), iteration, algo);
			}
			
		}
		
	}
	
	public static void test(JasyptType type, boolean useSecureRandom, Integer saltLength, Integer iteration, String digestAlgo) throws Exception {
		
		StringBuilder sb = null;
		CryptConfig config = null;
		if(type!=null) {
			sb = new StringBuilder(type.name());
		}
		else {
			sb = new StringBuilder("Custom");
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
		
		 
		
		
		ICrypt passwordEngine = null;
		if(type!=null) {
			passwordEngine = CryptFactory.getCrypt(type.toCryptType());
		}
		else {
			passwordEngine = CryptFactory.getCrypt(CryptType.JASYPT_CUSTOM_PASSWORD, config);
		}
		
		
		
		
		// test nuova generazione e verifica
			
		String password = "Pr@va.diUn@altroDiverso";
		boolean verificaPasswordDiversa = true;
		if(JasyptType.RFC2307_MD5.equals(type) || JasyptType.RFC2307_SHA.equals(type)) {
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
		
		if(type==null) {
			int saltLengthVerify = -1;
			int defaultSaltLength = 8;
			int maxSaltLength = Integer.MAX_VALUE; // non mi sembra esista
			int salt = ((JasyptCrypt)passwordEngine).getCustomSaltGenerator().getLastSizeGenerated();
			if(saltLength!=null) {
				saltLengthVerify = saltLength.intValue();
			}
			else {
				saltLengthVerify = defaultSaltLength;
			}
			
			if(saltLengthVerify> maxSaltLength) {
				if(salt!=defaultSaltLength) {
					throw new Exception("Lunghezza salt attesa '"+defaultSaltLength+"' (force default) differente da quella trovata '"+salt+"'");
				}
			}
			else {
				if(salt!=saltLengthVerify) {
					throw new Exception("Lunghezza salt attesa '"+saltLengthVerify+"' differente da quella trovata '"+salt+"'");
				}
			}
			
			System.out.println("salt: "+salt);
		}
		
		if(config!=null) {
			config.setUseBase64Encoding(false);
			passwordEngine = CryptFactory.getCrypt(CryptType.JASYPT_CUSTOM_PASSWORD, config);
			String passwordEncryptedHex = passwordEngine.crypt(password);
			verifica = passwordEngine.check(password, passwordEncryptedHex);
			if(!verifica) {
				throw new Exception("Verifica password hex '"+password+"' fallita");
			}
			if(passwordEncrypted.equals(passwordEncryptedHex) ) {
				throw new Exception("Attesa generazione di una password cifrata 'hex' differente; trovata sempre '1' "+passwordEncrypted);
			}
			try {
				Base64Utilities.decode(passwordEncrypted);
			}catch(Exception e) {
				throw new Exception("Verifica fallita della struttura base64 della password cifrata ["+passwordEncrypted+"]: "+e.getMessage(),e);
			}
			try {
				HexBinaryUtilities.decode(passwordEncryptedHex);
			}catch(Exception e) {
				throw new Exception("Verifica fallita della struttura hex della password cifrata ["+passwordEncryptedHex+"]: "+e.getMessage(),e);
			}
			
			System.out.println("password generata 'hex': "+passwordEncryptedHex);
		}
		
		System.out.println("password generata 'base64': "+passwordEncrypted);
		System.out.println("OK");
	}
	
}
