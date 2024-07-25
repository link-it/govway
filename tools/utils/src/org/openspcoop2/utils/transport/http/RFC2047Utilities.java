/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.utils.transport.http;

import java.util.List;

import javax.mail.internet.MimeUtility;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.Charset;
import org.slf4j.Logger;

/**
* RFC2047Utilities
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/

public class RFC2047Utilities {

	public static void main(String[] args) throws Exception {
		test();
	}
	
	private static Logger log = null;
	
	private static void initLogger() {
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ALL);
		log = LoggerWrapperFactory.getLogger(RFC2047Utilities.class);
	}
	
	private static void logInfo(String msg) {
		log.info(msg);
	}
	
	private static final String VALORE_DIVERSO_ATTESO = "Riscontrato valore diverso da quello atteso";
	private static final String VALORE_DIVERSO_SORGENTE = "Valore decodificato diverso dal sorgente";
	private static final String ENCODED_IN = "] encoded in RFC2047(";
	private static final String DECODE = "Decode: ";
	
	public static void test() throws UtilsException {
		
		initLogger();
		
		// Test String con caratteri appartenenti all'ISO ISO_8859_1
		String stringIso88591 = "für psychisch";
		String stringUsAscii = "fur psychisch";
		String stringLunga = "Non è possibile inviare un header con questi/caratteri nella richiesta";
		
		boolean v = isAllCharactersInCharset(stringIso88591, Charset.ISO_8859_1);
		logInfo("IS ["+stringIso88591+"] in CHARSET ISO_8859_1 ?: "+v);
		if(!v) {
			throw new UtilsException(VALORE_DIVERSO_ATTESO);
		}
		
		v = isAllCharactersInCharset(stringIso88591, Charset.US_ASCII);
		logInfo("IS ["+stringIso88591+"] in CHARSET US_ASCII ?: "+v);
		if(v) {
			throw new UtilsException(VALORE_DIVERSO_ATTESO);
		}
		
		v = isAllCharactersInCharset(stringUsAscii, Charset.ISO_8859_1);
		logInfo("IS ["+stringUsAscii+"] in CHARSET ISO_8859_1 ?: "+v);
		if(!v) {
			throw new UtilsException(VALORE_DIVERSO_ATTESO);
		}
		
		v = isAllCharactersInCharset(stringUsAscii, Charset.US_ASCII);
		logInfo("IS ["+stringUsAscii+"] in CHARSET US_ASCII ?: "+v);
		if(!v) {
			throw new UtilsException(VALORE_DIVERSO_ATTESO);
		}
		
		logInfo("\n*** RFC2047 ISO_8859_1 B ***");
		String varRFC2047ISO88591B = encode(stringIso88591, Charset.ISO_8859_1, RFC2047Encoding.B);
		logInfo("["+stringIso88591+ENCODED_IN+Charset.ISO_8859_1+","+RFC2047Encoding.B+"): "+varRFC2047ISO88591B);
		String decode = decode(varRFC2047ISO88591B);
		logInfo(DECODE+decode);
		if(!stringIso88591.equals(decode)) {
			throw new UtilsException(VALORE_DIVERSO_SORGENTE);
		}
		
		logInfo("\n*** RFC2047 ISO_8859_1 Q ***");
		String varRFC2047ISO88591Q = encode(stringIso88591, Charset.ISO_8859_1, RFC2047Encoding.Q);
		logInfo("["+stringIso88591+ENCODED_IN+Charset.ISO_8859_1+","+RFC2047Encoding.Q+"): "+varRFC2047ISO88591Q);
		decode = decode(varRFC2047ISO88591Q);
		logInfo(DECODE+decode);
		if(!stringIso88591.equals(decode)) {
			throw new UtilsException(VALORE_DIVERSO_SORGENTE);
		}
		
		logInfo("\n*** RFC2047 US_ASCII B ***");
		String varRFC2047USASCIIB = encode(stringIso88591, Charset.US_ASCII, RFC2047Encoding.B);
		logInfo("["+stringIso88591+ENCODED_IN+Charset.US_ASCII+","+RFC2047Encoding.B+"): "+varRFC2047USASCIIB);
		decode = decode(varRFC2047USASCIIB);
		logInfo(DECODE+decode);
		if(!stringIso88591.equals(decode)) {
			throw new UtilsException(VALORE_DIVERSO_SORGENTE);
		}
		
		logInfo("\n*** RFC2047 US_ASCII Q ***");
		String varRFC2047USASCIIQ = encode(stringIso88591, Charset.US_ASCII, RFC2047Encoding.Q);
		logInfo("["+stringIso88591+ENCODED_IN+Charset.US_ASCII+","+RFC2047Encoding.Q+"): "+varRFC2047USASCIIQ);
		decode = decode(varRFC2047USASCIIQ);
		logInfo(DECODE+decode);
		if(!stringIso88591.equals(decode)) {
			throw new UtilsException(VALORE_DIVERSO_SORGENTE);
		}
		
		logInfo("\n*** RFC2047 US_ASCII B (stringa lunga) ***");
		varRFC2047USASCIIB = encode(stringLunga, Charset.US_ASCII, RFC2047Encoding.B);
		logInfo("["+stringLunga+ENCODED_IN+Charset.US_ASCII+","+RFC2047Encoding.B+"): "+varRFC2047USASCIIB);
		decode = decode(varRFC2047USASCIIB);
		logInfo(DECODE+decode);
		if(!stringLunga.equals(decode)) {
			throw new UtilsException(VALORE_DIVERSO_SORGENTE);
		}
		
		logInfo("\n*** RFC2047 US_ASCII Q (stringa lunga) ***");
		varRFC2047USASCIIQ = encode(stringLunga, Charset.US_ASCII, RFC2047Encoding.Q);
		logInfo("["+stringLunga+ENCODED_IN+Charset.US_ASCII+","+RFC2047Encoding.Q+"): "+varRFC2047USASCIIQ);
		decode = decode(varRFC2047USASCIIQ);
		logInfo(DECODE+decode);
		if(!stringLunga.equals(decode)) {
			throw new UtilsException(VALORE_DIVERSO_SORGENTE);
		}
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
			return MimeUtility.decodeText(value);
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
	
	
	
	public static void validHeader(String key, List<String> values) throws UtilsException{
		if(values!=null && !values.isEmpty()) {
    		for (String value : values) {
    			validHeader(key, value);
    		}
		}
	}
	public static void validHeader(String key, String value) throws UtilsException{
		
		// jdk/openjdk/6-b14/sun/net/www/protocol/http/HttpURLConnection.java
		
        char lf = '\n';
        int index = key.indexOf(lf);
        if (index != -1) {
        	throw new UtilsException("Found illegal character(s) in message header field ["+key+"]");
        }
        else {
            if (value == null) {
                return;
            }

            index = value.indexOf(lf);
            while (index != -1) {
                index++;
                if (index < value.length()) {
                    char c = value.charAt(index);
                    if ((c==' ') || (c=='\t')) {
                        // ok, check the next occurrence
                        index = value.indexOf(lf, index);
                        continue;
                    }
                }
                throw new UtilsException("Found Illegal character(s) in message header ["+key+"]. Found value: ["+value+"]");
            }
        }
    }
}


