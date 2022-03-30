/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

/**
 * CryptType
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum CodecType {

	/* 
	 * https://commons.apache.org/proper/commons-codec/archives/1.13/apidocs/src-html/org/apache/commons/codec/digest/Md5Crypt.html
	 * The libc crypt() "$1$" and Apache "$apr1$" MD5-based hash algorithm.
	*/
	LIBC_CRYPT_MD5("$1$"),
	LIBC_CRYPT_MD5_APACHE("$apr1$"),
	
	/* 
	 * https://commons.apache.org/proper/commons-codec/archives/1.13/apidocs/src-html/org/apache/commons/codec/digest/Sha2Crypt.html
	 * SHA2-based Unix crypt implementation in SHA-256 e SHA-512 variant. 
	 */
	SHA2_BASED_UNIX_CRYPT_SHA256("$5$"),
	SHA2_BASED_UNIX_CRYPT_SHA512("$6$"),
	
	/*
	 * https://commons.apache.org/proper/commons-codec/archives/1.13/apidocs/src-html/org/apache/commons/codec/digest/UnixCrypt.html
	 * Unix crypt(3) algorithm implementation
	 */
	DES_UNIX_CRYPT("");
	
	CodecType(String prefix){
		this.prefix = prefix;
	}
	
	private String prefix;

	public String getPrefix() {
		return this.prefix;
	}
	
	public CryptType toCryptType() {
		switch (this) {
		case LIBC_CRYPT_MD5:
			return CryptType.LIBC_CRYPT_MD5;
		case LIBC_CRYPT_MD5_APACHE:
			return CryptType.LIBC_CRYPT_MD5_APACHE;
		case SHA2_BASED_UNIX_CRYPT_SHA256:
			return CryptType.SHA2_BASED_UNIX_CRYPT_SHA256;
		case SHA2_BASED_UNIX_CRYPT_SHA512:
			return CryptType.SHA2_BASED_UNIX_CRYPT_SHA512;
		case DES_UNIX_CRYPT:
			return CryptType.DES_UNIX_CRYPT;
		default:
			return null;
		}
	}
	
}
