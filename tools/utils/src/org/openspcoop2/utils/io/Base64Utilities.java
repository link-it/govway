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


package org.openspcoop2.utils.io;

import org.apache.commons.codec.binary.StringUtils;

/**	
 * Base64Utilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Base64Utilities {

    /**
     * Characters for Base64 transformation.
     */
    public static final String B64_STRING = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * Characters for Base64 transformation.
     */
    public static final char[] B64_ARRAY = B64_STRING.toCharArray();
    
	
	public static byte[] decode(byte [] data){
		return org.apache.commons.codec.binary.Base64.decodeBase64(data);
	}
	public static byte[] decode(String data){
		return org.apache.commons.codec.binary.Base64.decodeBase64(data);
	}
	
	public static byte[] encode(byte [] data){
		return org.apache.commons.codec.binary.Base64.encodeBase64(data);
	}
	public static byte[] encodeChunked(byte [] data){
		return org.apache.commons.codec.binary.Base64.encodeBase64Chunked(data);
	}
	public static byte[] encode(byte [] data, boolean isChunked){
		return org.apache.commons.codec.binary.Base64.encodeBase64(data, isChunked);
	}
	public static byte[] encode(byte [] data, boolean isChunked, boolean urlSafe){
		return org.apache.commons.codec.binary.Base64.encodeBase64(data, isChunked, urlSafe);
	}
	public static byte[] encode(byte [] data, boolean isChunked, boolean urlSafe, int maxResultSize){
		return org.apache.commons.codec.binary.Base64.encodeBase64(data, isChunked, urlSafe, maxResultSize);
	}
	
	public static String encodeAsString(byte [] data){
		return org.apache.commons.codec.binary.Base64.encodeBase64String(data);
	}
	public static String encodeChunkedAsString(byte [] data){
		return StringUtils.newStringUsAscii(encodeChunked(data));
	}
	public static String encodeBase64URLSafeString(byte [] data){
		return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(data);
	}
	public static String encodeAsString(byte [] data, boolean isChunked){
		return StringUtils.newStringUsAscii(encode(data, isChunked));
	}
	public static String encodeAsString(byte [] data, boolean isChunked, boolean urlSafe){
		return StringUtils.newStringUsAscii(encode(data, isChunked, urlSafe));
	}
	public static String encodeAsString(byte [] data, boolean isChunked, boolean urlSafe, int maxResultSize){
		return StringUtils.newStringUsAscii(encode(data, isChunked, urlSafe, maxResultSize));
	}
	
}
