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

import java.io.Serializable;

/**
 * CryptType
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum CryptType implements Serializable {

	/* 
	 * https://commons.apache.org/proper/commons-codec/archives/1.13/apidocs/src-html/org/apache/commons/codec/digest/Md5Crypt.html
	 * The libc crypt() "$1$" and Apache "$apr1$" MD5-based hash algorithm.
	*/
	LIBC_CRYPT_MD5,  // Algorithm: MD5, Prefix: $1$
	LIBC_CRYPT_MD5_APACHE,  // Algorithm: MD5, Prefix: $apr1$
	
	/* 
	 * https://commons.apache.org/proper/commons-codec/archives/1.13/apidocs/src-html/org/apache/commons/codec/digest/Sha2Crypt.html
	 * SHA2-based Unix crypt implementation in SHA-256 e SHA-512 variant. 
	 */
	SHA2_BASED_UNIX_CRYPT_SHA256, // Algorithm: SHA256, Prefix: $5$
	SHA2_BASED_UNIX_CRYPT_SHA512, // Algorithm: SHA512, Prefix: $6$
	
	/*
	 * https://commons.apache.org/proper/commons-codec/archives/1.13/apidocs/src-html/org/apache/commons/codec/digest/UnixCrypt.html
	 * Unix crypt(3) algorithm implementation
	 */
	DES_UNIX_CRYPT,
	
	/*
	 * http://www.jasypt.org/api/jasypt/1.8/org/jasypt/util/password/rfc2307/package-summary.html
	 */
	RFC2307_MD5, // Algorithm: MD5, Salt size: 0 (no salt), Iterations: 1 (no hash iteration), Prefix: {MD5}
	RFC2307_SMD5, // Algorithm: MD5, Salt size: 8, Iterations: 1 (no hash iteration), Prefix: {SMD5}, Invert position of salt in message before digesting, Invert position of plain salt in encryption results, Use lenient salt size check
	RFC2307_SHA, // Algorithm: SHA-1, Salt size: 0 (no salt), Iterations: 1 (no hash iteration), Prefix: {SHA}
	RFC2307_SSHA, // Algorithm: SHA-1, Salt size: 8, Iterations: 1 (no hash iteration), Prefix: {SSHA}, Invert position of salt in message before digesting, Invert position of plain salt in encryption results, Use lenient salt size check
	
	/*
	 * http://www.jasypt.org/api/jasypt/1.8/org/jasypt/util/password/package-summary.html
	 */
	JASYPT_BASIC_PASSWORD, // Algorithm: MD5, Salt size: 8, Iterations: 1000
	JASYPT_STRONG_PASSWORD, // Algorithm: SHA-256, Salt size: 16, Iterations: 100000
	JASYPT_CUSTOM_PASSWORD,
	
	/*
	 * https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/crypto/spec/PBEKeySpec.html
	 */
	PBE_KEY_SPEC,
	
	B_CRYPT,
	
	S_CRYPT,

	PLAIN;

}
