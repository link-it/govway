/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.utils.resources;

import javax.mail.internet.MimeUtility;

import org.openspcoop2.utils.UtilsException;

/**
* RFC2047Utilities
*
* @author Andrea Poli <apoli@link.it>
* @author $Author$
* @version $Rev$, $Date$
*/

public class RFC2047Utilities {

	public static void main(String[] args) throws Exception {
		
		// Test String con caratteri appartenenti all'ISO ISO_8859_1
		String string_iso_8859_1 = "für psychisch";
		String string_us_ascii = "fur psychisch";
		
		System.out.println("IS ["+string_iso_8859_1+"] in CHARSET ISO_8859_1 ?: "+isAllCharactersInCharset(string_iso_8859_1, Charset.ISO_8859_1));
		System.out.println("IS ["+string_iso_8859_1+"] in CHARSET US_ASCII ?: "+isAllCharactersInCharset(string_iso_8859_1, Charset.US_ASCII));
		System.out.println("IS ["+string_us_ascii+"] in CHARSET ISO_8859_1 ?: "+isAllCharactersInCharset(string_us_ascii, Charset.ISO_8859_1));
		System.out.println("IS ["+string_us_ascii+"] in CHARSET US_ASCII ?: "+isAllCharactersInCharset(string_us_ascii, Charset.US_ASCII));
		
		System.out.println("\n*** RFC2047 ISO_8859_1 B ***");
		String var_RFC2047_ISO_8859_1_B = encode(string_iso_8859_1, Charset.ISO_8859_1, RFC2047Encoding.B);
		System.out.println("["+string_iso_8859_1+"] encoded in RFC2047("+Charset.ISO_8859_1+","+RFC2047Encoding.B+"): "+var_RFC2047_ISO_8859_1_B);
		System.out.println("Decode: "+decode(var_RFC2047_ISO_8859_1_B));
		
		System.out.println("\n*** RFC2047 ISO_8859_1 Q ***");
		String var_RFC2047_ISO_8859_1_Q = encode(string_iso_8859_1, Charset.ISO_8859_1, RFC2047Encoding.Q);
		System.out.println("["+string_iso_8859_1+"] encoded in RFC2047("+Charset.ISO_8859_1+","+RFC2047Encoding.Q+"): "+var_RFC2047_ISO_8859_1_Q);
		System.out.println("Decode: "+decode(var_RFC2047_ISO_8859_1_Q));
		
		System.out.println("\n*** RFC2047 US_ASCII B ***");
		String var_RFC2047_US_ASCII_B = encode(string_iso_8859_1, Charset.US_ASCII, RFC2047Encoding.B);
		System.out.println("["+string_iso_8859_1+"] encoded in RFC2047("+Charset.US_ASCII+","+RFC2047Encoding.B+"): "+var_RFC2047_US_ASCII_B);
		System.out.println("Decode: "+decode(var_RFC2047_US_ASCII_B));
		
		System.out.println("\n*** RFC2047 US_ASCII Q ***");
		String var_RFC2047_US_ASCII_Q = encode(string_iso_8859_1, Charset.US_ASCII, RFC2047Encoding.Q);
		System.out.println("["+string_iso_8859_1+"] encoded in RFC2047("+Charset.US_ASCII+","+RFC2047Encoding.Q+"): "+var_RFC2047_US_ASCII_Q);
		System.out.println("Decode: "+decode(var_RFC2047_US_ASCII_Q));
	}
	
	public static String encode(String value,Charset charset,RFC2047Encoding encoding) throws UtilsException{
		try{
			return MimeUtility.encodeWord(value,charset.getValue(),encoding.name());
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static String decode(String value) throws UtilsException{
		try{
			return MimeUtility.decodeWord(value);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static boolean isAllCharactersInCharset(String value,Charset charset) throws UtilsException{
		try{
			java.nio.charset.CharsetEncoder encoder = 
					java.nio.charset.Charset.forName(charset.getValue()).newEncoder();
			return encoder.canEncode(value);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	
	public static void validHeader(String key, String value) throws UtilsException{
		
		// jdk/openjdk/6-b14/sun/net/www/protocol/http/HttpURLConnection.java
		
        char LF = '\n';
        int index = key.indexOf(LF);
        if (index != -1) {
        	throw new UtilsException("Found illegal character(s) in message header field ["+key+"]");
        }
        else {
            if (value == null) {
                return;
            }

            index = value.indexOf(LF);
            while (index != -1) {
                index++;
                if (index < value.length()) {
                    char c = value.charAt(index);
                    if ((c==' ') || (c=='\t')) {
                        // ok, check the next occurrence
                        index = value.indexOf(LF, index);
                        continue;
                    }
                }
                throw new UtilsException("Found Illegal character(s) in message header ["+key+"]. Found value: ["+value+"]");
            }
        }
    }
}


