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

package org.openspcoop2.utils.random;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.io.Base64Utilities;

/**
 * RandomGenerator
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RandomGenerator {

	private boolean useSecureRandom = false;
	// https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#securerandom-number-generation-algorithms
	private String algorithmSecureRandom = null;
	
	public boolean isUseSecureRandom() {
		return this.useSecureRandom;
	}
	public void setUseSecureRandom(boolean useSecureRandom) {
		this.useSecureRandom = useSecureRandom;
	}
	public String getAlgorithmSecureRandom() {
		return this.algorithmSecureRandom;
	}
	public void setAlgorithmSecureRandom(String algorithmSecureRandom) {
		this.algorithmSecureRandom = algorithmSecureRandom;
	}
	
	public static RandomGenerator getDefaultInstance() {
		return new RandomGenerator(true);
	} 
	
	public RandomGenerator() {
		initEngine(false, null);
	}
	public RandomGenerator(boolean useSecureRandom) {
		initEngine(useSecureRandom, null);
	}
	public RandomGenerator(boolean useSecureRandom, SecureRandomAlgorithm algo) {
		initEngine(useSecureRandom, algo.getValue());
	}
	public RandomGenerator(boolean useSecureRandom, String algorithm) {
		initEngine(useSecureRandom, algorithm);
	}
	private void initEngine(boolean useSecureRandom, String algorithm) {
		this.useSecureRandom = useSecureRandom;
		this.algorithmSecureRandom = algorithm;
	}
	
	public String nextRandom(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(Base64Utilities.B64_STRING.charAt(this.nextInt(Base64Utilities.B64_STRING.length())));
		}
		return sb.toString();
	}
	
	public String nextRandomInt(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(this.nextInt(10));
		}
		return sb.toString();
	}

	public byte[] nextRandomBytes(int length) {
		byte[] arrByte = new byte[length];
		nextRandomBytes(arrByte);
		return arrByte;
	}
	public void nextRandomBytes(byte[] arrByte) {
		if(this.useSecureRandom) {
			java.security.SecureRandom sr = (java.security.SecureRandom) getRandomEngine();
		    sr.nextBytes(arrByte);
		}
		else {
			java.util.Random rd = (java.util.Random) getRandomEngine();
			rd.nextBytes(arrByte);
		}
	}
	
	public boolean nextBoolean() {
		if(this.useSecureRandom) {
			java.security.SecureRandom sr = (java.security.SecureRandom) getRandomEngine();
			return sr.nextBoolean();
		}
		else {
			java.util.Random rd = (java.util.Random) getRandomEngine();
			return rd.nextBoolean();
		}
	}
	
	public int nextInt() {
		if(this.useSecureRandom) {
			java.security.SecureRandom sr = (java.security.SecureRandom) getRandomEngine();
			return sr.nextInt();
		}
		else {
			java.util.Random rd = (java.util.Random) getRandomEngine();
			return rd.nextInt();
		}
	}
	public int nextInt(int bound) {
		if(this.useSecureRandom) {
			java.security.SecureRandom sr = (java.security.SecureRandom) getRandomEngine();
			return sr.nextInt(bound);
		}
		else {
			java.util.Random rd = (java.util.Random) getRandomEngine();
			return rd.nextInt(bound);
		}
	}
	public long nextLong() {
		if(this.useSecureRandom) {
			java.security.SecureRandom sr = (java.security.SecureRandom) getRandomEngine();
			return sr.nextLong();
		}
		else {
			java.util.Random rd = (java.util.Random) getRandomEngine();
			return rd.nextLong();
		}
	}
	
	public double nextDouble() {
		if(this.useSecureRandom) {
			java.security.SecureRandom sr = (java.security.SecureRandom) getRandomEngine();
			return sr.nextDouble();
		}
		else {
			java.util.Random rd = (java.util.Random) getRandomEngine();
			return rd.nextDouble();
		}
	}
	public float nextFloat() {
		if(this.useSecureRandom) {
			java.security.SecureRandom sr = (java.security.SecureRandom) getRandomEngine();
			return sr.nextFloat();
		}
		else {
			java.util.Random rd = (java.util.Random) getRandomEngine();
			return rd.nextFloat();
		}
	}
	public double nextGaussian() {
		if(this.useSecureRandom) {
			java.security.SecureRandom sr = (java.security.SecureRandom) getRandomEngine();
			return sr.nextGaussian();
		}
		else {
			java.util.Random rd = (java.util.Random) getRandomEngine();
			return rd.nextGaussian();
		}
	}
	
	public String getAlgorithm() {
		if(this.useSecureRandom) {
			java.security.SecureRandom sr = (java.security.SecureRandom) getRandomEngine();
			return sr.getAlgorithm();
		}
		else {
			java.util.Random rd = (java.util.Random) getRandomEngine();
			return rd.getClass().getName();
		}
	}
	
	
	public Object getRandomEngine() {
		if(this.useSecureRandom) {
			java.security.SecureRandom sr = null;
			if(this.algorithmSecureRandom!=null) {
				sr = getSecureRandom(this.algorithmSecureRandom);
			}
			else {
				sr = getSecureRandomDefault();
			}
			/**System.out.println("algorithmSecureRandom: "+sr.getAlgorithm());*/
		    return sr;
		}
		else {
			return getSecureRandomDefault();
		}
	}
	
	
	private static Map<String, java.security.SecureRandom> mapAlgoSecureRandom = new HashMap<>();
	private static synchronized void initSecureRandom(String algorithmSecureRandom){
		if(!mapAlgoSecureRandom.containsKey(algorithmSecureRandom)) {
			try {
				java.security.SecureRandom sr = java.security.SecureRandom.getInstance(algorithmSecureRandom);
				mapAlgoSecureRandom.put(algorithmSecureRandom, sr);
			}catch(Exception e) {
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
		}
	}
	private static java.security.SecureRandom getSecureRandom(String algorithmSecureRandom){
		if(!mapAlgoSecureRandom.containsKey(algorithmSecureRandom)) {
			initSecureRandom(algorithmSecureRandom);
		}
		return mapAlgoSecureRandom.get(algorithmSecureRandom);
	}
	
	private static java.security.SecureRandom secureRandomDefault = null;
	private static synchronized void initSecureRandomDefault(){
		if(secureRandomDefault==null) {
			secureRandomDefault = new java.security.SecureRandom();
		}
	}
	private static java.security.SecureRandom getSecureRandomDefault(){
		if(secureRandomDefault==null) {
			initSecureRandomDefault();
		}
		return secureRandomDefault;
	}
}
