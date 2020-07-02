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

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.openspcoop2.utils.random.RandomGenerator;
import org.slf4j.Logger;

/**
 * PBEKeySpecCrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PBEKeySpecCrypt implements ICrypt {

	private static final String PBE_ALGORITHM = "PBKDF2WithHmacSHA1";
	
	private Logger log;
	private CryptConfig config;
	private RandomGenerator randomGenerator;

	public PBEKeySpecCrypt(){
		
	}
	
	@Override
	public void init(Logger log, CryptConfig config) {
		this.log = log;
		
		this.config = config;
		if(this.config == null) {
			this.config = new CryptConfig();
		}
		
		this.randomGenerator = new RandomGenerator(this.config.isUseSecureRandom(), this.config.getAlgorithmSecureRandom());
	}
	
	@Override
	public String crypt(String password) throws UtilsException {
		
		int iterations = 1000;
		if(this.config.getIteration()!=null && this.config.getIteration()>0) {
			iterations = this.config.getIteration().intValue();
		}
		
		char[] chars = password.toCharArray();
		
		int saltLength = 16;
		if(this.config.getSaltLength()!=null && this.config.getSaltLength()>0) {
			saltLength = this.config.getSaltLength().intValue();
		}
		byte[] salt = this.randomGenerator.nextRandomBytes(saltLength);
		
		try {
			PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
			String algo = PBE_ALGORITHM;
			if(this.config.getDigestAlgorithm()!=null) {
				algo = this.config.getDigestAlgorithm();
			}
			SecretKeyFactory skf = SecretKeyFactory.getInstance(algo);
			byte[] hash = skf.generateSecret(spec).getEncoded();
			String prefix = iterations + ":";
			if(this.config.isUseBase64Encoding()) {
				return prefix +  Base64Utilities.encodeAsString(salt) + ":" + Base64Utilities.encodeAsString(hash);
			}
			else {
				return prefix +  HexBinaryUtilities.encodeAsString(salt) + ":" + HexBinaryUtilities.encodeAsString(hash);
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
	}
	
	@Override
	public boolean check(String password, String pwcrypt) {
		try {
			String[] parts = pwcrypt.split(":");
			if(parts.length!=3) {
				throw new Exception("Wrong format");
			}
			int iterations = Integer.parseInt(parts[0]);
			byte[] salt = null;
			byte[] hash = null;
			if(this.config.isUseBase64Encoding()) {
				salt = Base64Utilities.decode(parts[1]);
				hash = Base64Utilities.decode(parts[2]);
			}
			else {
				salt = HexBinaryUtilities.decode(parts[1]);
				hash = HexBinaryUtilities.decode(parts[2]);
			}
			
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, hash.length * 8);
			
			String algo = PBE_ALGORITHM;
			if(this.config.getDigestAlgorithm()!=null) {
				algo = this.config.getDigestAlgorithm();
			}
			SecretKeyFactory skf = SecretKeyFactory.getInstance(algo);
			
			byte[] testHash = skf.generateSecret(spec).getEncoded();
			
			int diff = hash.length ^ testHash.length;
			
			for(int i = 0; i < hash.length && i < testHash.length; i++)
			{
				diff |= hash[i] ^ testHash[i];
			}
			
			return diff == 0;
		}catch(Throwable e){
			if(this.log!=null) {
				this.log.error("Verifica password '"+pwcrypt+"' fallita: "+e.getMessage(),e);
			}
			return false;
		}
	}
	
}