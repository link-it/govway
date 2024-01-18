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

package org.openspcoop2.utils.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
* CharsetUtilities
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class CharsetUtilities {

	public static void main(String [] args) throws Exception {
		
        /*
         IMPORTAMTE:
		
		   JVM caches value of default character encoding once JVM starts and 
 	       so is the case for default constructors of InputStreamReader and other core Java classes. 
 	       So calling System.setProperty("file.encoding" , "UTF-16") may not have desire effect.

		   Read more: https://javarevisited.blogspot.com/2012/01/get-set-default-character-encoding.html#ixzz5pxOQjcal
         * */

		
		System.out.println("defaultCharacterEncoding by property: " + getDefaultCharsetByProperties());
		System.out.println("defaultCharacterEncoding by code: " + getDefaultCharsetByCode());
		System.out.println("defaultCharacterEncoding by charSet: " + getDefaultCharsetByCharset());
		
		// Quanto spiegato sopra si può vedere modificando il charset e si vedra che gli altri due metodi 'ByCode' e 'ByCharset' continuano ad utilizzare il charset originale
		
		System.setProperty("file.encoding" , "ISO-8859-1");
		
		System.out.println("defaultCharacterEncoding by property: " + getDefaultCharsetByProperties());
		System.out.println("defaultCharacterEncoding by code: " + getDefaultCharsetByCode());
		System.out.println("defaultCharacterEncoding by charSet: " + getDefaultCharsetByCharset());
	}
	
	public static String getDefaultCharsetByProperties() {
		String defaultCharacterEncoding = System.getProperty("file.encoding");
		return defaultCharacterEncoding;
	}
	
	public static String getDefaultCharsetByCode() throws IOException {
		byte [] bArray = {'w'};
        try(
        	InputStream is = new ByteArrayInputStream(bArray);
        	InputStreamReader reader = new InputStreamReader(is);
        	){    	
        	String defaultCharacterEncoding = reader.getEncoding();
        	return defaultCharacterEncoding;
        }

	}
	
	public static String getDefaultCharsetByCharset() {
		return java.nio.charset.Charset.defaultCharset().toString();
	}
}
