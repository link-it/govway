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
package org.openspcoop2.utils.digest;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;

/**
 * DigestUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DigestUtils {

	public static byte[] getDigestValue(byte[] content, String algorithm) throws UtilsException{
		if(content==null) {
			throw new UtilsException("Digest content undefined");
		}
		if(algorithm==null) {
			throw new UtilsException("Digest algorithm undefined");
		}
		
		MessageDigest digest = null;
		try {
			digest = MessageDigestFactory.getMessageDigest(algorithm);
		}catch(Throwable e) {
			throw new UtilsException("Message digest (algorithm: '"+algorithm+"') initialization failed: "+e.getMessage(),e);
		}
		try {
			byte[]md5Data = digest.digest(content);
			return md5Data;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static String getDigestValue(byte[] content, String algorithm, DigestEncoding digestEncoding) throws UtilsException{
		return getDigestValue(content, algorithm, digestEncoding, false);
	}
	public static String getDigestValue(byte[] content, String algorithm, DigestEncoding digestEncoding, boolean rfc3230) throws UtilsException{
		byte[]md5Data = getDigestValue(content, algorithm);
		if(digestEncoding==null) {
			throw new UtilsException("Digest encoding undefined");
		}
		return encode(md5Data, digestEncoding, rfc3230, algorithm);
	}
	
	public static Map<DigestEncoding, String> getDigestValues(byte[] content, String algorithm, DigestEncoding ... digestEncoding) throws UtilsException{
		return getDigestValues(content, algorithm, false, digestEncoding);
	}
	public static Map<DigestEncoding, String> getDigestValues(byte[] content, String algorithm, boolean rfc3230, DigestEncoding ... digestEncoding) throws UtilsException{

		byte[]md5Data = getDigestValue(content, algorithm);
		if(digestEncoding==null || digestEncoding.length<=0) {
			throw new UtilsException("Digest encoding undefined");
		}
		
		Map<DigestEncoding, String> map = new HashMap<DigestEncoding, String>();
		for (DigestEncoding de : digestEncoding) {
			try {
				String digestValue = encode(md5Data, de, rfc3230, algorithm);
				map.put(de, digestValue);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
		return map;
	}
	
	private static String encode(byte[] md5Data, DigestEncoding digestEncoding, boolean rfc3230, String algorithm) throws UtilsException{

		if(digestEncoding==null) {
			throw new UtilsException("Digest encoding undefined");
		}
		
		try {
			String digestValue = null;
			switch (digestEncoding) {
			case HEX:
				digestValue = HexBinaryUtilities.encodeAsString(md5Data);
				break;
			case BASE64:
				digestValue = Base64Utilities.encodeAsString(md5Data);
				break;
			}
			if(rfc3230) {
				return algorithm+"="+digestValue;
			}
			else {
				return digestValue;
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
}
