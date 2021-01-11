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

import java.security.Provider;

import org.jasypt.digest.config.DigesterConfig;
import org.jasypt.salt.SaltGenerator;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.random.RandomGenerator;
import org.slf4j.Logger;

/**
 * JasyptCrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JasyptCrypt implements ICrypt {

	@SuppressWarnings("unused")
	private Logger log;
	private JasyptType type;
	private CryptConfig config;
	private RandomGenerator randomGenerator;
	private CustomSaltGenerator customSaltGenerator;
	
	public JasyptCrypt(JasyptType type){
		this.type = type;
		if(this.type == null) {
			this.type = JasyptType.JASYPT_STRONG_PASSWORD;
		}
	}
	
	public JasyptCrypt(){
	}
	
	@Override
	public void init(Logger log, CryptConfig config) {
		this.log = log;
		
		if(this.type==null) {
			this.config = config;
			if(this.config == null) {
				this.config = new CryptConfig();
			}
			
			this.randomGenerator = new RandomGenerator(this.config.isUseSecureRandom(), this.config.getAlgorithmSecureRandom());
			this.customSaltGenerator = new CustomSaltGenerator(this.randomGenerator);
		}
	}
	
	public CustomSaltGenerator getCustomSaltGenerator() {
		return this.customSaltGenerator;
	}
	
	@Override
	public String crypt(String password) throws UtilsException {
		
		if(this.type!=null) {
			switch (this.type) {
			case JASYPT_BASIC_PASSWORD:{
				org.jasypt.util.password.BasicPasswordEncryptor basicEncryptor = new org.jasypt.util.password.BasicPasswordEncryptor();
				return basicEncryptor.encryptPassword(password);
			}
			case JASYPT_STRONG_PASSWORD:{
				org.jasypt.util.password.StrongPasswordEncryptor strongEncryptor = new org.jasypt.util.password.StrongPasswordEncryptor();
				return strongEncryptor.encryptPassword(password);
			}
			case RFC2307_MD5:{
				org.jasypt.util.password.rfc2307.RFC2307MD5PasswordEncryptor rfcEncryptor = new org.jasypt.util.password.rfc2307.RFC2307MD5PasswordEncryptor();
				return rfcEncryptor.encryptPassword(password);
			}
			case RFC2307_SMD5:{
				org.jasypt.util.password.rfc2307.RFC2307SMD5PasswordEncryptor rfcEncryptor = new org.jasypt.util.password.rfc2307.RFC2307SMD5PasswordEncryptor();
				return rfcEncryptor.encryptPassword(password);
			}
			case RFC2307_SHA:{
				org.jasypt.util.password.rfc2307.RFC2307SHAPasswordEncryptor rfcEncryptor = new org.jasypt.util.password.rfc2307.RFC2307SHAPasswordEncryptor();
				return rfcEncryptor.encryptPassword(password);
			}
			case RFC2307_SSHA:{
				org.jasypt.util.password.rfc2307.RFC2307SSHAPasswordEncryptor rfcEncryptor = new org.jasypt.util.password.rfc2307.RFC2307SSHAPasswordEncryptor();
				return rfcEncryptor.encryptPassword(password);
			}
			}
			throw new UtilsException("Unsupported type '"+this.type+"'");
		}
		else {
			org.jasypt.util.password.ConfigurablePasswordEncryptor configurablePasswordEncryptor = _getConfigurablePasswordEncryptor();
			return configurablePasswordEncryptor.encryptPassword(password);
		}
		
	}
	
	@Override
	public boolean check(String password, String pwcrypt) {
		try {
			if(this.type!=null) {
				switch (this.type) {
				case JASYPT_BASIC_PASSWORD:{
					org.jasypt.util.password.BasicPasswordEncryptor basicEncryptor = new org.jasypt.util.password.BasicPasswordEncryptor();
					return basicEncryptor.checkPassword(password, pwcrypt);
				}
				case JASYPT_STRONG_PASSWORD:{
					org.jasypt.util.password.StrongPasswordEncryptor strongEncryptor = new org.jasypt.util.password.StrongPasswordEncryptor();
					return strongEncryptor.checkPassword(password, pwcrypt);
				}
				case RFC2307_MD5:{
					org.jasypt.util.password.rfc2307.RFC2307MD5PasswordEncryptor rfcEncryptor = new org.jasypt.util.password.rfc2307.RFC2307MD5PasswordEncryptor();
					return rfcEncryptor.checkPassword(password, pwcrypt);
				}
				case RFC2307_SMD5:{
					org.jasypt.util.password.rfc2307.RFC2307SMD5PasswordEncryptor rfcEncryptor = new org.jasypt.util.password.rfc2307.RFC2307SMD5PasswordEncryptor();
					return rfcEncryptor.checkPassword(password, pwcrypt);
				}
				case RFC2307_SHA:{
					org.jasypt.util.password.rfc2307.RFC2307SHAPasswordEncryptor rfcEncryptor = new org.jasypt.util.password.rfc2307.RFC2307SHAPasswordEncryptor();
					return rfcEncryptor.checkPassword(password, pwcrypt);
				}
				case RFC2307_SSHA:{
					org.jasypt.util.password.rfc2307.RFC2307SSHAPasswordEncryptor rfcEncryptor = new org.jasypt.util.password.rfc2307.RFC2307SSHAPasswordEncryptor();
					return rfcEncryptor.checkPassword(password, pwcrypt);
				}
				}
				throw new RuntimeException("Unsupported type '"+this.type+"'");
			}
			else {
				org.jasypt.util.password.ConfigurablePasswordEncryptor configurablePasswordEncryptor = _getConfigurablePasswordEncryptor();
				return configurablePasswordEncryptor.checkPassword(password, pwcrypt);
			}
		}catch(Throwable e){
			if(this.log!=null) {
				this.log.error("Verifica password '"+pwcrypt+"' fallita: "+e.getMessage(),e);
			}
			return false;
		}
	}

	private org.jasypt.util.password.ConfigurablePasswordEncryptor _getConfigurablePasswordEncryptor() {
		org.jasypt.util.password.ConfigurablePasswordEncryptor configurablePasswordEncryptor = new org.jasypt.util.password.ConfigurablePasswordEncryptor();	
		CustomDigesterConfig config = new CustomDigesterConfig(this.config, this.customSaltGenerator);
		configurablePasswordEncryptor.setConfig(config);
		configurablePasswordEncryptor.setStringOutputType(this.config.isUseBase64Encoding() ? "BASE64" : "hexadecimal");
		configurablePasswordEncryptor.setAlgorithm(config.getAlgorithm());
		return configurablePasswordEncryptor;
	}
}

class CustomSaltGenerator implements SaltGenerator  {
	
	private RandomGenerator randomGenerator;
	private int lastSizeGenerated;
	
	public int getLastSizeGenerated() {
		return this.lastSizeGenerated;
	}

	public CustomSaltGenerator(RandomGenerator randomGenerator){
		this.randomGenerator = randomGenerator;
	} 
	
	@Override
	public boolean includePlainSaltInEncryptionResults() {
		return true;
	}
	
	@Override
	public byte[] generateSalt(int size) {
		try {
			//System.out.println("GENERA ["+size+"]");
			return this.randomGenerator.nextRandomBytes(size);
		}finally {
			this.lastSizeGenerated = size;
		}
	}
}

class CustomDigesterConfig implements DigesterConfig {

	private CryptConfig config;
	private CustomSaltGenerator customSaltGenerator;
	
	public CustomDigesterConfig(CryptConfig config, CustomSaltGenerator customSaltGenerator) {
		this.config = config;
		this.customSaltGenerator = customSaltGenerator;
	}
	
	@Override
	public String getAlgorithm() {
		String digestAlgorithm = this.config.getDigestAlgorithm();
		if(digestAlgorithm==null) {
			digestAlgorithm = "SHA-256"; // default
		}
		return digestAlgorithm;
	}

	@Override
	public Boolean getInvertPositionOfPlainSaltInEncryptionResults() {
		return null;
	}

	@Override
	public Boolean getInvertPositionOfSaltInMessageBeforeDigesting() {
		return null;
	}

	@Override
	public Integer getIterations() {
		if(this.config.getIteration()!=null && this.config.getIteration()>0) {
			return this.config.getIteration().intValue();
		}
		return null;
	}

	@Override
	public Integer getPoolSize() {
		return null;
	}

	@Override
	public Provider getProvider() {
		return null;
	}

	@Override
	public String getProviderName() {
		return null;
	}

	@Override
	public SaltGenerator getSaltGenerator() {
		return this.customSaltGenerator;
	}

	@Override
	public Integer getSaltSizeBytes() {
		if(this.config.getSaltLength()!=null && this.config.getSaltLength()>0) {
			return this.config.getSaltLength().intValue();
		}
		return null;
	}

	@Override
	public Boolean getUseLenientSaltSizeCheck() {
		return null;
	}
	
}