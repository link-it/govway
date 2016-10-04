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
package org.openspcoop2.utils.id;

import java.util.Hashtable;
import java.util.Random;

/**
 * Identity
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDUtilities {

	private static long uniqueSerialNumber = 0;
	public static synchronized long getUniqueSerialNumber(){
		if((IDUtilities.uniqueSerialNumber+1) > Long.MAX_VALUE){
			IDUtilities.uniqueSerialNumber = 0;
		} 
		IDUtilities.uniqueSerialNumber++;
		return IDUtilities.uniqueSerialNumber;
	}
	
	private static final char[] symbols;
	static {
	    StringBuilder tmp = new StringBuilder();
	    for (char ch = '0'; ch <= '9'; ++ch)
	      tmp.append(ch);
	    for (char ch = 'a'; ch <= 'z'; ++ch)
	      tmp.append(ch);
	    symbols = tmp.toString().toCharArray();
	}   
	private static final Random random = new Random();
	private static final Hashtable<String, char[]> mapRandom = new Hashtable<String, char[]>();
	private static synchronized char[] getBufferForRandom(int length){
		String key = length+"";
		if(mapRandom.containsKey(key)){
			return mapRandom.get(key);
		}
		else{
			char[] buf = new char[length];
			mapRandom.put(key, buf);
			return buf;
		}
	}
	public static String generateAlphaNumericRandomString(int length) {
		char[] buf = getBufferForRandom(length);
		for (int idx = 0; idx < buf.length; ++idx) 
			buf[idx] = symbols[random.nextInt(symbols.length)];
		return new String(buf);
	}
}
