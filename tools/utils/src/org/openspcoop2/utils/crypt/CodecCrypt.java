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
import org.openspcoop2.utils.random.RandomGenerator;
import org.slf4j.Logger;

/**
 * CodecCrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CodecCrypt implements ICrypt {

	private static final String ITERATOR = "rounds=";
	
	private Logger log;
	private CodecType type;
	private CryptConfig config;
	private RandomGenerator randomGenerator;

	public CodecCrypt(CodecType type){
		this.type = type;
		if(this.type == null) {
			this.type = CodecType.SHA2_BASED_UNIX_CRYPT_SHA256;
		}
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
		
		Integer saltLength = this.config.getSaltLength();
		if(saltLength==null) {
			switch (this.type) {
			case LIBC_CRYPT_MD5:
			case LIBC_CRYPT_MD5_APACHE:
				saltLength = 8;
				break;
			case SHA2_BASED_UNIX_CRYPT_SHA256:
			case SHA2_BASED_UNIX_CRYPT_SHA512:
				saltLength = 16;
				break;
			case DES_UNIX_CRYPT:
				saltLength = 2;
				break;
			}
		}
		
		String salt = this.randomGenerator.nextRandom(saltLength);
				
		if(!CodecType.DES_UNIX_CRYPT.equals(this.type)) {
			
			//	 a) the salt salt_prefix, $5$ or $6$ respectively
			//	 b) the rounds=<N> specification, if one was present in the input salt string. A trailing '$' is added in this case to separate the rounds specification from the following text.
			//   c) the salt string truncated to 16 characters (solo per SHA2)
			//   d) a '$' character
			
			StringBuilder sb = new StringBuilder();
			String prefix = this.type.getPrefix();
			if(salt.startsWith(prefix) && salt.length()>prefix.length()) {
				salt = salt.substring(prefix.length());
			}
			sb.append(prefix);
			
			if(this.config.getIteration()!=null && this.config.getIteration().intValue()>0 &&
					(CodecType.SHA2_BASED_UNIX_CRYPT_SHA256.equals(this.type) || CodecType.SHA2_BASED_UNIX_CRYPT_SHA512.equals(this.type)) &&
					!salt.startsWith(ITERATOR)) {
				sb.append(ITERATOR).append(this.config.getIteration().intValue()).append("$");
				sb.append(salt);
			}
			else {
				sb.append(salt);
			}
			
			if(!salt.endsWith("$")) {
				sb.append("$");
			}
			
			salt = sb.toString();
		}
		return this._crypt(password, salt);
	}

	@Override
	public boolean check(String password, String pwcrypt) {
		try{
			boolean checkPwS = false;
			String newPw = this._crypt(password, pwcrypt);
			if (newPw.equals(pwcrypt)) {
				checkPwS = true;
			}
			return checkPwS;
		}catch(Throwable e){
			if(this.log!=null) {
				this.log.error("Verifica password '"+pwcrypt+"' fallita: "+e.getMessage(),e);
			}
			return false;
		}
	}

	private String _crypt(String password, String salt) throws UtilsException {
		
		byte [] keyBytes = null;
		try {
			keyBytes = password.getBytes(this.config.getCharsetName());
		}catch(Throwable e){
			throw new UtilsException(e.getMessage(), e);
		}
		switch (this.type) {
			case LIBC_CRYPT_MD5:
				if(salt!=null) {
					return org.apache.commons.codec.digest.Md5Crypt.md5Crypt(keyBytes, salt);
				}
				else {
					return org.apache.commons.codec.digest.Md5Crypt.md5Crypt(keyBytes);
				}
			case LIBC_CRYPT_MD5_APACHE:
				if(salt!=null) {
					return org.apache.commons.codec.digest.Md5Crypt.apr1Crypt(keyBytes, salt);
				}
				else {
					return org.apache.commons.codec.digest.Md5Crypt.apr1Crypt(keyBytes);
				}
			case SHA2_BASED_UNIX_CRYPT_SHA256:
				if(salt!=null) {
					return org.apache.commons.codec.digest.Sha2Crypt.sha256Crypt(keyBytes, salt);
				}
				else {
					return org.apache.commons.codec.digest.Sha2Crypt.sha256Crypt(keyBytes);
				}
			case SHA2_BASED_UNIX_CRYPT_SHA512:
				if(salt!=null) {
					return org.apache.commons.codec.digest.Sha2Crypt.sha512Crypt(keyBytes, salt);
				}
				else {
					return org.apache.commons.codec.digest.Sha2Crypt.sha512Crypt(keyBytes);
				}
			case DES_UNIX_CRYPT:
				if(salt!=null) {
					return org.apache.commons.codec.digest.UnixCrypt.crypt(keyBytes, salt);
				}
				else {
					return org.apache.commons.codec.digest.UnixCrypt.crypt(keyBytes);
				}
		}
		
		throw new UtilsException("Unsupported type '"+this.type+"'");
	}

}
