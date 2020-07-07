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

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * CryptFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CryptFactory {

	public static ICrypt getCrypt(Logger log, CryptConfig config) throws UtilsException {
		
		if(config.getCryptType()!=null) {
			return getCrypt(log, config.getCryptType(), config);
		}
		else {
			String className = config.getCryptCustomType();
			if(StringUtils.isEmpty(className)) {
				throw new UtilsException("Property '"+CryptConfig.CRYPT_TYPE+"' or '"+CryptConfig.CRYPT_CUSTOM_TYPE+"' are required");
			}
			return getCrypt(log, className, config);
		}

	}
	
	public static ICrypt getCrypt(Logger log, CryptType type, CryptConfig config) {
		return _getCrypt(log, type, config);
	}
	public static ICrypt getCrypt(CryptType type, CryptConfig config) {
		return _getCrypt(null, type, config);
	}
	public static ICrypt getCrypt(Logger log, CryptType type) {
		return _getCrypt(log, type, null);
	}
	public static ICrypt getCrypt(CryptType type) {
		return _getCrypt(null, type, null);
	}
	private static ICrypt _getCrypt(Logger log, CryptType type, CryptConfig config) {
		switch (type) {
		
		case LIBC_CRYPT_MD5:
			return getCodecCrypt(log, CodecType.LIBC_CRYPT_MD5, config);
		case LIBC_CRYPT_MD5_APACHE:
			return getCodecCrypt(log, CodecType.LIBC_CRYPT_MD5_APACHE, config);
		case SHA2_BASED_UNIX_CRYPT_SHA256:
			return getCodecCrypt(log, CodecType.SHA2_BASED_UNIX_CRYPT_SHA256, config);
		case SHA2_BASED_UNIX_CRYPT_SHA512:
			return getCodecCrypt(log, CodecType.SHA2_BASED_UNIX_CRYPT_SHA512, config);
		case DES_UNIX_CRYPT:
			return getCodecCrypt(log, CodecType.DES_UNIX_CRYPT, config);
			
		case RFC2307_MD5:
			return getJasyptCrypt(log, JasyptType.RFC2307_MD5);
		case RFC2307_SMD5:
			return getJasyptCrypt(log, JasyptType.RFC2307_SMD5);
		case RFC2307_SHA:
			return getJasyptCrypt(log, JasyptType.RFC2307_SHA);
		case RFC2307_SSHA:
			return getJasyptCrypt(log, JasyptType.RFC2307_SSHA);
			
		case JASYPT_BASIC_PASSWORD:
			return getJasyptCrypt(log, JasyptType.JASYPT_BASIC_PASSWORD);
		case JASYPT_STRONG_PASSWORD:
			return getJasyptCrypt(log, JasyptType.JASYPT_STRONG_PASSWORD);
		case JASYPT_CUSTOM_PASSWORD:
			return getJasyptCrypt(log, config);
			
		case PBE_KEY_SPEC:{
			PBEKeySpecCrypt pbe = new PBEKeySpecCrypt();
			pbe.init(log, config);
			return pbe;
		}
		
		case B_CRYPT:
			return getSpringCrypt(log, SpringType.B_CRYPT);
		case S_CRYPT:
			return getSpringCrypt(log, SpringType.S_CRYPT);
				
		case PLAIN:{
			PlainCrypt pbe = new PlainCrypt();
			pbe.init(log, config);
			return pbe;
		}
		
		}
		
		return null;
	}
	
	public static ICrypt getCrypt(Logger log, String className, CryptConfig config) throws UtilsException {
		return _getCrypt(log, className, config);
	}
	public static ICrypt getCrypt( String className, CryptConfig config) throws UtilsException {
		return _getCrypt(null, className, config);
	}
	public static ICrypt getCrypt(Logger log, String className) throws UtilsException {
		return _getCrypt(log, className, null);
	}
	public static ICrypt getCrypt(String className) throws UtilsException {
		return _getCrypt(null, className, null);
	}
	@SuppressWarnings("unchecked")
	private static ICrypt _getCrypt(Logger log, String className, CryptConfig config) throws UtilsException {
		Class<ICrypt> c = null;
		try {
			c = (Class<ICrypt>) Class.forName(className);
		}catch(Throwable e) {
			throw new UtilsException(e.getMessage(), e);
		}
		return _getCrypt(log, c, config);
	}
	
	public static ICrypt getCrypt(Logger log, Class<ICrypt> c, CryptConfig config) throws UtilsException {
		return _getCrypt(log, c, config);
	}
	public static ICrypt getCrypt( Class<ICrypt> c, CryptConfig config) throws UtilsException {
		return _getCrypt(null, c, config);
	}
	public static ICrypt getCrypt(Logger log, Class<ICrypt> c) throws UtilsException {
		return _getCrypt(log, c, null);
	}
	public static ICrypt getCrypt(Class<ICrypt> c) throws UtilsException {
		return _getCrypt(null, c, null);
	}
	private static ICrypt _getCrypt(Logger log, Class<ICrypt> c, CryptConfig config) throws UtilsException {
		try {
			ICrypt crypt = Utilities.newInstance(c);
			crypt.init(log, config);
			return crypt;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(), e);
		}
	}
	
	
	// Sottoclassi specifiche
	
	@SuppressWarnings("deprecation")
	public static OldMD5Crypt getOldMD5Crypt(Logger log) {
		OldMD5Crypt old = new OldMD5Crypt();
		old.init(log, null);
		return old;
	}
	@SuppressWarnings("deprecation")
	public static OldMD5Crypt getOldMD5Crypt() {
		return getOldMD5Crypt(null);
	}
	
	public static CodecCrypt getCodecCrypt(Logger log, CodecType type, CryptConfig config) {
		CodecCrypt c = new CodecCrypt(type);
		c.init(log, config);
		return c;
	}
	public static CodecCrypt getCodecCrypt(CodecType type, CryptConfig config) {
		return getCodecCrypt(null, type, config);
	}
	public static CodecCrypt getCodecCrypt(Logger log, CodecType type) {
		return getCodecCrypt(log, type, null);
	}
	public static CodecCrypt getCodecCrypt(CodecType type) {
		return getCodecCrypt(null, type, null);
	}
	
	public static JasyptCrypt getJasyptCrypt(Logger log, CryptConfig config) {
		JasyptCrypt j = new JasyptCrypt();
		j.init(log, config);
		return j;
	}
	public static JasyptCrypt getJasyptCrypt(CryptConfig config) {
		return getJasyptCrypt(null, config);
	}
	public static JasyptCrypt getJasyptCrypt(Logger log, JasyptType type) {
		JasyptCrypt j = new JasyptCrypt(type);
		j.init(log, null);
		return j;
	}
	public static JasyptCrypt getJasyptCrypt(JasyptType type) {
		return getJasyptCrypt(null, type);
	}
	
	public static SpringCrypt getSpringCrypt(Logger log, SpringType type) {
		SpringCrypt s = new SpringCrypt(type);
		s.init(log, null);
		return s;
	}
	public static SpringCrypt getSpringCrypt(SpringType type) {
		return getSpringCrypt(null, type);
	}
}
