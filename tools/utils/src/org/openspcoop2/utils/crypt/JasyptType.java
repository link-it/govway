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

/**
 * JasyptType
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum JasyptType {

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
	JASYPT_STRONG_PASSWORD; // Algorithm: SHA-256, Salt size: 16, Iterations: 100000
	
	public CryptType toCryptType() {
		switch (this) {
		
		case RFC2307_MD5:
			return CryptType.RFC2307_MD5;
		case RFC2307_SMD5:
			return CryptType.RFC2307_SMD5;
		case RFC2307_SHA:
			return CryptType.RFC2307_SHA;
		case RFC2307_SSHA:
			return CryptType.RFC2307_SSHA;
			
		case JASYPT_BASIC_PASSWORD:
			return CryptType.JASYPT_BASIC_PASSWORD;
		case JASYPT_STRONG_PASSWORD:
			return CryptType.JASYPT_STRONG_PASSWORD;
			
		default:
			return null;
		}
	}
}
