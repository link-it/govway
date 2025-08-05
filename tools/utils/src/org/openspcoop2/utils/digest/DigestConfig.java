/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.digest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.utils.UtilsException;

/**
 * IDigest
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DigestConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String DIGEST_TYPE = "digest.type";
	private static final String DIGEST_SALT_LENGTH = "digest.salt.length";
	private static final String DIGEST_SALT_SECURE_RANDOM_ALGORITHM = "digest.salt.secureRandomAlgorithm";
	private static final String DIGEST_BASE64_ENCODE = "digest.base64Encode";
	private static final String DIGEST_HASH_COMPOSITION = "digest.composition";
	
	private DigestType digestType = null;
	private Integer saltLength = null;
	private String algorithmSecureRandom = "SHA1PRNG";
	private String hashComposition = "${message}${salt}";
	private boolean base64Encode = false;
	
	public DigestConfig() {}
	
	public DigestConfig(String resource) throws UtilsException{
		
		try (InputStream is = getInputStream(resource)){
			
			if(is!=null) {
				Properties p = new Properties();
				p.load(is);
				this.initEngine(p);
			} else {
				throw new UtilsException("Resource ["+resource+"] not found");
			}
		} catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		} 
	}
	private InputStream getInputStream(String resource) throws FileNotFoundException {
		File f = new File(resource);
		InputStream is = null;
		if(f.exists()) {
			is = new FileInputStream(f);
		} else {
			is = DigestConfig.class.getResourceAsStream(resource);
		}
		if(is==null) {
			is = DigestConfig.class.getResourceAsStream("/org/openspcoop2/utils/crypt/digest.properties");
		}
		return is;
	}
	public DigestConfig(InputStream is) throws UtilsException{
		try{
			Properties p = new Properties();
			p.load(is);
			this.initEngine(p);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public DigestConfig(Properties p) throws UtilsException {
		this.initEngine(p);
	}
	
	private void initEngine(Properties p) throws UtilsException {
		final String NOT_CORRECT_PROPERTY = "Property: '%s' value: '%s' not correct: %s";

		String tipo = DigestConfig.getProperty(p, DigestConfig.DIGEST_TYPE, true);
		if(StringUtils.isNotEmpty(tipo)) {
			DigestType digestTypeProp = null;
			try {
				digestTypeProp = DigestType.valueOf(tipo);
			}catch(IllegalArgumentException | NullPointerException e) {
				throw new UtilsException(String.format(NOT_CORRECT_PROPERTY, DigestConfig.DIGEST_TYPE, tipo, e.getMessage()), e);
			}
			this.digestType = digestTypeProp;
		}
		
		String saltLengthProp = getProperty(p, DIGEST_SALT_LENGTH, false);
		if(StringUtils.isNotEmpty(saltLengthProp)) {
			try {
				this.saltLength = Integer.valueOf(saltLengthProp);
			}catch(NumberFormatException e) {
				throw new UtilsException(String.format(NOT_CORRECT_PROPERTY, DigestConfig.DIGEST_SALT_LENGTH, saltLengthProp, e.getMessage()), e);
			}
		}
		
		String secureRandomAlgo = getProperty(p, DIGEST_SALT_SECURE_RANDOM_ALGORITHM, false);
		if(StringUtils.isNotEmpty(secureRandomAlgo)) {
			this.algorithmSecureRandom = secureRandomAlgo;
		}
		
		String digestBase64Encode = getProperty(p, DIGEST_BASE64_ENCODE, false);
		if(StringUtils.isNotEmpty(digestBase64Encode)) {
			this.base64Encode = Boolean.valueOf(digestBase64Encode);
		}
		
		String digestHashComposition = getProperty(p, DIGEST_HASH_COMPOSITION, false);
		if(StringUtils.isNotEmpty(digestHashComposition)) {
			this.hashComposition = digestHashComposition;
		}
	}
	
	protected static String getProperty(Properties p, String name, boolean  required) throws UtilsException {
		String pValue = p.getProperty(name);
		if(pValue == null) {
			if(required) {
				throw new UtilsException("Property '" + name + "' not found");
			}
			return null;
		}
		else {
			return pValue.trim();
		}
	} 
	
	
	private static final Pattern COMPOSITION_VAR_PATTERN = Pattern.compile("\\$\\{([^\\}]*)\\}");
	private static final String SALT_LABEL = "salt";
	private static final String MESSAGE_LABEL = "message";
	public byte[] composeMessage(byte[] input, byte[] salt) {
		String scheme = this.getHashComposition();
		Pattern pattern = DigestConfig.COMPOSITION_VAR_PATTERN;
		
		List<byte[]> bytes = new ArrayList<>();
		Matcher matcher = pattern.matcher(scheme);
		int lastIndex = 0;
		int size = 0;
		
		while (matcher.find(lastIndex)) {
			if (lastIndex < matcher.start()) {
				bytes.add(scheme.substring(lastIndex, matcher.start()).getBytes());
				size += (matcher.start() - lastIndex);
			}
			
			lastIndex = matcher.end();
			
			byte[] sub = null;
			if (matcher.group(1).equals(MESSAGE_LABEL))
				sub = input;
			if (matcher.group(1).equals(SALT_LABEL))
				sub = salt;
			
			if (sub != null) {
				size += sub.length;
				bytes.add(sub);
			}
		}
		
		if (lastIndex < scheme.length()) {
			bytes.add(scheme.substring(lastIndex).getBytes());
			size += scheme.length() - lastIndex;
		}
		
		byte[] buf = new byte[size];
		lastIndex = 0;
		for (byte[] src : bytes) {
			System.arraycopy(src, 0, buf, lastIndex, src.length);
			lastIndex += src.length;
		}
		
		return buf;
	}
	
	public DigestType getDigestType() {
		return this.digestType;
	}

	public void setDigestType(DigestType digestType) {
		this.digestType = digestType;
	}
	
	public Integer getSaltLength() {
		return this.saltLength;
	}

	public void setSaltLength(Integer saltLength) {
		this.saltLength = saltLength;
	}

	public String getAlgorithmSecureRandom() {
		return this.algorithmSecureRandom;
	}

	public void setAlgorithmSecureRandom(String algorithmSecureRandom) {
		this.algorithmSecureRandom = algorithmSecureRandom;
	}

	public boolean isBase64Encode() {
		return this.base64Encode;
	}

	public void setBase64Encode(boolean base64Encode) {
		this.base64Encode = base64Encode;
	}
	
	public String getHashComposition() {
		return this.hashComposition;
	}

	public void setHashComposition(String hashComposition) {
		this.hashComposition = hashComposition;
	}
}
