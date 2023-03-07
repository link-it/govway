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

package org.openspcoop2.utils.crypt;

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

	private Logger log;
	private JasyptType type;
	private CryptConfig config;
	private RandomGenerator randomGenerator;
	private JasyptCustomSaltGenerator customSaltGenerator;
	
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
			this.customSaltGenerator = new JasyptCustomSaltGenerator(this.randomGenerator);
		}
	}
	
	public JasyptCustomSaltGenerator getCustomSaltGenerator() {
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
		JasyptCustomDigesterConfig config = new JasyptCustomDigesterConfig(this.config, this.customSaltGenerator);
		configurablePasswordEncryptor.setConfig(config);
		configurablePasswordEncryptor.setStringOutputType(this.config.isUseBase64Encoding() ? "BASE64" : "hexadecimal");
		configurablePasswordEncryptor.setAlgorithm(config.getAlgorithm());
		return configurablePasswordEncryptor;
	}
}
