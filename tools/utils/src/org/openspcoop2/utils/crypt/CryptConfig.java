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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.Charset;

/**
 * PasswordCryptConfig
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class CryptConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// http://www.jasypt.org/howtoencryptuserpasswords.html
	
	protected static final String CRYPT_SPECIAL_PATH_PLAIN = "PLAIN";
	
	protected static final String CRYPT_TYPE = "passwordEncrypt.type";
	protected static final String CRYPT_CUSTOM_TYPE = "passwordEncrypt.customType";
	
	private static final String CRYPT_CHARSET_NAME = "passwordEncrypt.charsetName";
	
	private static final String CRYPT_SALT_LENGTH = "passwordEncrypt.salt.length";
	private static final String CRYPT_SALT_SECURE_RANDOM = "passwordEncrypt.salt.secureRandom";
	private static final String CRYPT_SALT_SECURE_RANDOM_ALGORITHM = "passwordEncrypt.salt.secureRandomAlgorithm";
	
	private static final String CRYPT_DIGEST_ALGORITHM = "passwordEncrypt.digestAlgorithm";
	private static final String CRYPT_ITERATION = "passwordEncrypt.iteration";
	
	private static final String CRYPT_BASE64_ENCODING = "passwordEncrypt.base64Encoding";

	private static final String CRYPT_BACKWARD_COMPATIBILITY = "passwordEncrypt.backwardCompatibility";
	
	private CryptType cryptType = null;
	
	private String cryptCustomType = null;
	
	private String digestAlgorithm = null;

	private String charsetName = Charset.UTF_8.getValue(); // per la password
	
	private Integer iteration = null;
	
	private Integer saltLength = null;
	private boolean useSecureRandom = true; // default
	private String algorithmSecureRandom = null;
	
	private boolean useBase64Encoding = true; // in alternativa hex
	
	private boolean backwardCompatibility = false;


	public CryptConfig() {}
	
	public CryptConfig(String resource) throws UtilsException{
		
		if(CRYPT_SPECIAL_PATH_PLAIN.equals(resource)) {
			this.cryptType = CryptType.PLAIN;
			return;
		}
		
		InputStream is = null;
		try{
			File f = new File(resource);
			if(f.exists()){
				is = new FileInputStream(f);
			}
			else{
				is = PasswordVerifier.class.getResourceAsStream(resource);
			}
			if(is!=null){
				Properties p = new Properties();
				p.load(is);
				this._init(p);
			}
			else{
				throw new Exception("Resource ["+resource+"] not found");
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){}
		}
	}
	public CryptConfig(InputStream is) throws UtilsException{
		try{
			Properties p = new Properties();
			p.load(is);
			this._init(p);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public CryptConfig(Properties p) throws UtilsException {
		this._init(p);
	}
	private void _init(Properties p) throws UtilsException {
		
		String tipo = CryptConfig.getProperty(p, CryptConfig.CRYPT_TYPE, false);
		if(StringUtils.isNotEmpty(tipo)) {
			CryptType cryptType = null;
			try {
				cryptType = CryptType.valueOf(tipo);
			}catch(Throwable e) {
				throw new UtilsException("Property '"+CryptConfig.CRYPT_TYPE+"' value '"+tipo+"' uncorrect: "+e.getMessage(), e);
			}
			this.cryptType = cryptType;
		}
		else {
			String className = CryptConfig.getProperty(p, CryptConfig.CRYPT_CUSTOM_TYPE, false);
			if(StringUtils.isEmpty(className)) {
				throw new UtilsException("Property '"+CryptConfig.CRYPT_TYPE+"' or '"+CryptConfig.CRYPT_CUSTOM_TYPE+"' are required");
			}
			this.cryptCustomType = className;
		}
		
		String charset = getProperty(p, CRYPT_CHARSET_NAME, false);
		if(StringUtils.isNotEmpty(charset)) {
			this.charsetName = charset;
		}
		
		String saltLength = getProperty(p, CRYPT_SALT_LENGTH, false);
		if(StringUtils.isNotEmpty(saltLength)) {
			try {
				this.saltLength = Integer.valueOf(saltLength);
			}catch(Throwable e) {
				throw new UtilsException("Property '"+CRYPT_SALT_LENGTH+"' value '"+saltLength+"' uncorrect: "+e.getMessage(), e);
			}
		}
		
		String secureRandom = getProperty(p, CRYPT_SALT_SECURE_RANDOM, false);
		if(StringUtils.isNotEmpty(secureRandom)) {
			try {
				this.useSecureRandom = Boolean.valueOf(secureRandom);
			}catch(Throwable e) {
				throw new UtilsException("Property '"+CRYPT_SALT_SECURE_RANDOM+"' value '"+secureRandom+"' uncorrect: "+e.getMessage(), e);
			}
		}
		
		String secureRandomAlgo = getProperty(p, CRYPT_SALT_SECURE_RANDOM_ALGORITHM, false);
		if(StringUtils.isNotEmpty(secureRandomAlgo)) {
			this.algorithmSecureRandom = secureRandomAlgo;
		}
		
		
		String digest = getProperty(p, CRYPT_DIGEST_ALGORITHM, false);
		if(StringUtils.isNotEmpty(digest)) {
			this.digestAlgorithm = digest;
		}
		
		String iteration = getProperty(p, CRYPT_ITERATION, false);
		if(StringUtils.isNotEmpty(iteration)) {
			try {
				this.iteration = Integer.valueOf(iteration);
			}catch(Throwable e) {
				throw new UtilsException("Property '"+CRYPT_ITERATION+"' value '"+iteration+"' uncorrect: "+e.getMessage(), e);
			}
		}
		
		
		String base64 = getProperty(p, CRYPT_BASE64_ENCODING, false);
		if(StringUtils.isNotEmpty(base64)) {
			try {
				this.useBase64Encoding = Boolean.valueOf(base64);
			}catch(Throwable e) {
				throw new UtilsException("Property '"+CRYPT_BASE64_ENCODING+"' value '"+base64+"' uncorrect: "+e.getMessage(), e);
			}
		}
		
		
		String back = getProperty(p, CRYPT_BACKWARD_COMPATIBILITY, false);
		if(StringUtils.isNotEmpty(back)) {
			try {
				this.backwardCompatibility = Boolean.valueOf(back);
			}catch(Throwable e) {
				throw new UtilsException("Property '"+CRYPT_BACKWARD_COMPATIBILITY+"' value '"+back+"' uncorrect: "+e.getMessage(), e);
			}
		}
	}
	
	protected static String getProperty(Properties p, String name, boolean  required) throws UtilsException {
		String pValue = p.getProperty(name);
		if(pValue==null) {
			if(required) {
				throw new UtilsException("Property '"+name+"' not found");
			}
			return null;
		}
		else {
			return pValue.trim();
		}
	} 
	
	public CryptType getCryptType() {
		return this.cryptType;
	}

	public void setCryptType(CryptType cryptType) {
		this.cryptType = cryptType;
	}

	public String getCryptCustomType() {
		return this.cryptCustomType;
	}

	public void setCryptCustomType(String cryptCustomType) {
		this.cryptCustomType = cryptCustomType;
	}
	
	public boolean isUseBase64Encoding() {
		return this.useBase64Encoding;
	}

	public void setUseBase64Encoding(boolean useBase64Encoding) {
		this.useBase64Encoding = useBase64Encoding;
	}

	public String getDigestAlgorithm() {
		return this.digestAlgorithm;
	}

	public void setDigestAlgorithm(String digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
	}

	public String getCharsetName() {
		return this.charsetName;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	public Integer getIteration() {
		return this.iteration;
	}

	public void setIteration(Integer iteration) {
		this.iteration = iteration;
	}
	
	public Integer getSaltLength() {
		return this.saltLength;
	}

	public void setSaltLength(Integer saltLength) {
		this.saltLength = saltLength;
	}

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

	public boolean isBackwardCompatibility() {
		return this.backwardCompatibility;
	}

	public void setBackwardCompatibility(boolean backwardCompatibility) {
		this.backwardCompatibility = backwardCompatibility;
	}
}
