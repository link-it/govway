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

package org.openspcoop2.utils.random.test;

import org.openspcoop2.utils.random.RandomGenerator;
import org.openspcoop2.utils.random.SecureRandomAlgorithm;

/**
 * Test
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RandomGeneratorTest {

	public static void main(String[] args) throws Exception {
	
		test(false, null);
		
		test(true, null);
		
		SecureRandomAlgorithm [] v = SecureRandomAlgorithm.values();
		for (SecureRandomAlgorithm secureRandomAlgorithm : v) {
			if(SecureRandomAlgorithm.PKCS11.equals(secureRandomAlgorithm)) {
				continue; // deve essere installato sul sistema
			}
			else if(SecureRandomAlgorithm.WINDOWS_PRNG.equals(secureRandomAlgorithm)) {
				continue; // deve essere installato sul sistema
			}
			test(true, secureRandomAlgorithm.getValue());
		}
		
	}

	public static void test(boolean useSecureRandom, String secureRandomAlgorithm) throws Exception {
	
		System.out.println("\n---------- (secureRandom:"+useSecureRandom+" algo:"+secureRandomAlgorithm+")----------------");
		
		RandomGenerator generator = new RandomGenerator(useSecureRandom, secureRandomAlgorithm);
		
		String algorithm = generator.getAlgorithm();
		System.out.println("ALGO: "+algorithm);
		
		byte [] array = generator.nextRandomBytes(16);
		if(array.length!=16) {
			throw new Exception("Lunghezza array "+array.length+" differente da quella attesa (test nextRandomBytes(16))");
		}
		array = generator.nextRandomBytes(8);
		if(array.length!=8) {
			throw new Exception("Lunghezza array "+array.length+" differente da quella attesa (test nextRandomBytes(8))");
		}
				
		array = new byte[16];
		generator.nextRandomBytes(array);
		if(array.length!=16) {
			throw new Exception("Lunghezza array "+array.length+" differente da quella attesa(test nextRandomBytes(array) con lenght 16)");
		}
		array = new byte[8];
		generator.nextRandomBytes(array);
		if(array.length!=8) {
			throw new Exception("Lunghezza array "+array.length+" differente da quella attesa(test nextRandomBytes(array) con lenght 8)");
		}
		
		String s = generator.nextRandom(16);
		//System.out.println("["+s+"]");
		if(s.length()!=16) {
			throw new Exception("Lunghezza stringa "+s.length()+" differente da quella attesa (test nextRandom(16))");
		}
		s = generator.nextRandom(8);
		if(s.length()!=8) {
			throw new Exception("Lunghezza stringa "+s.length()+" differente da quella attesa (test nextRandom(8))");
		}
		
		@SuppressWarnings("unused")
		int intNumber = generator.nextInt();
		intNumber = generator.nextInt(1872);
		intNumber = generator.nextInt(23);
		
		@SuppressWarnings("unused")
		long longNumber = generator.nextLong();
		
		@SuppressWarnings("unused")
		boolean booleanValue = generator.nextBoolean();
		
		@SuppressWarnings("unused")
		double dValue = generator.nextDouble();
		dValue = generator.nextGaussian();
		
		@SuppressWarnings("unused")
		float fValue = generator.nextFloat();
	}
}
