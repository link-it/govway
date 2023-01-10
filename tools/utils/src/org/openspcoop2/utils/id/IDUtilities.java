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
package org.openspcoop2.utils.id;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateEngineType;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;

/**
 * Identity
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDUtilities {

	private static long uniqueSerialNumber = 0;
	private static org.openspcoop2.utils.Semaphore semaphore_getUniqueSerialNumber = new org.openspcoop2.utils.Semaphore("IDUtilities.getUniqueSerialNumber");
	public static long getUniqueSerialNumber(){
		return getUniqueSerialNumber("anonymous", null);
	}
	public static long getUniqueSerialNumber(String methodName){
		return getUniqueSerialNumber(methodName, null);
	}
	public static long getUniqueSerialNumber(String methodName, String idTransazione){
		semaphore_getUniqueSerialNumber.acquireThrowRuntime(methodName, idTransazione);
		try {
			
			if((IDUtilities.uniqueSerialNumber+1) > Long.MAX_VALUE){
				IDUtilities.uniqueSerialNumber = 0;
			} 
			IDUtilities.uniqueSerialNumber++;
			return IDUtilities.uniqueSerialNumber;
			
		}finally {
			semaphore_getUniqueSerialNumber.release(methodName, idTransazione);
		}
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
	private static final Map<String, char[]> mapRandom = new ConcurrentHashMap<String, char[]>();
	private static synchronized char[] getBufferForRandom(int length){
		String key = length+"";
		if(IDUtilities.mapRandom.containsKey(key)){
			return IDUtilities.mapRandom.get(key);
		}
		else{
			char[] buf = new char[length];
			IDUtilities.mapRandom.put(key, buf);
			return buf;
		}
	}
	public static String generateAlphaNumericRandomString(int length) {
		char[] buf = IDUtilities.getBufferForRandom(length);
		for (int idx = 0; idx < buf.length; ++idx) 
			buf[idx] = IDUtilities.symbols[IDUtilities.random.nextInt(IDUtilities.symbols.length)];
		return new String(buf);
	}
	
	private static org.openspcoop2.utils.Semaphore semaphore_generateDateTime = new org.openspcoop2.utils.Semaphore("IDUtilities.generateDateTime");
	public static String generateDateTime(String format, int syncMs) {
		return IDUtilities.generateDateTime(DateUtils.getDEFAULT_DATA_ENGINE_TYPE(), format, syncMs);
	}
	public static String generateDateTime(DateEngineType type, String format, int syncMs) {
		semaphore_generateDateTime.acquireThrowRuntime("generateDateTime");
		try {
			Utilities.sleep(syncMs);
			return DateUtils.getTimeFormatter(type, format).format(DateManager.getDate());
		}finally {
			semaphore_generateDateTime.release("generateDateTime");
		}
	}
	public static String generateDateTime_ISO_8601_TZ(String format, int syncMs) {
		return IDUtilities.generateDateTime_ISO_8601_TZ(DateUtils.getDEFAULT_DATA_ENGINE_TYPE(), format, syncMs);
	}
	public static String generateDateTime_ISO_8601_TZ(DateEngineType type, String format, int syncMs) {
		semaphore_generateDateTime.acquireThrowRuntime("generateDateTime_ISO_8601_TZ");
		try {
			Utilities.sleep(syncMs);
			return DateUtils.getDateTimeFormatter_ISO_8601_TZ(type, format).format(DateManager.getDate());
		}finally {
			semaphore_generateDateTime.release("generateDateTime_ISO_8601_TZ");
		}
	}
}
