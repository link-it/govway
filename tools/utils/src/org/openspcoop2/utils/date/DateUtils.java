/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;

/**     
 * DateUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DateUtils {


	public static Date convertToLeftInterval(Date date, UnitaTemporale unitaTemporale) {
		
		Calendar calendar = null;
		try{
			calendar = DateManager.getCalendar();
		}catch(Exception e){
			calendar = Calendar.getInstance();
		}
		calendar.setTime(date);
		switch (unitaTemporale) {
		case SECONDI:
			calendar.set(Calendar.MILLISECOND, 0);
			break;
		case MINUTI:		
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			break;
		case ORARIO:		
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			break;
		case GIORNALIERO:	
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			break;
		case SETTIMANALE:
			while(Calendar.MONDAY!=calendar.get(Calendar.DAY_OF_WEEK)){
				calendar.add(Calendar.DAY_OF_WEEK, -1);
			}
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			break;
		case MENSILE:
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		}
		
		return calendar.getTime();
	}
	
	public static Date convertToRightInterval(Date date, UnitaTemporale unitaTemporale) {
				
		Calendar calendar = null;
		try{
			calendar = DateManager.getCalendar();
		}catch(Exception e){
			calendar = Calendar.getInstance();
		}
		calendar.setTime(date);
		switch (unitaTemporale) {
		case SECONDI:
			calendar.set(Calendar.MILLISECOND, 999);
			break;
		case MINUTI:		
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			break;
		case ORARIO:		
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			break;
		case GIORNALIERO:	
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			break;
		case SETTIMANALE:
			while(Calendar.SUNDAY!=calendar.get(Calendar.DAY_OF_WEEK)){
				calendar.add(Calendar.DAY_OF_WEEK, 1);
			}
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			break;
		case MENSILE:
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			// prelevo ultimo giorno del mese impostato nell'attuale data
			int lastDayActualMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			calendar.set(Calendar.DAY_OF_MONTH, lastDayActualMonth);
			break;
		}
		
		return calendar.getTime();
	}
		
	public static Date incrementDate(Date date, UnitaTemporale unitaTemporale, int increment) {
		
		if(increment==0){
			return date;
		}
		
		Calendar calendar = null;
		try{
			calendar = DateManager.getCalendar();
		}catch(Exception e){
			calendar = Calendar.getInstance();
		}
		calendar.setTime(date);
		switch (unitaTemporale) {
		case SECONDI:
			calendar.add(Calendar.SECOND, increment);
			break;
		case MINUTI:		
			calendar.add(Calendar.MINUTE, increment);
			break;
		case ORARIO:		
			calendar.add(Calendar.HOUR_OF_DAY, increment);
			break;
		case GIORNALIERO:	
			calendar.add(Calendar.DAY_OF_YEAR, increment);
			break;
		case SETTIMANALE:
			calendar.add(Calendar.DAY_OF_YEAR, (increment*7));
			break;
		case MENSILE:
			calendar.add(Calendar.MONTH, increment);
			// prelevo ultimo giorno del mese impostato nell'attuale data
			int lastDayActualMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			calendar.set(Calendar.DAY_OF_MONTH, lastDayActualMonth);
			break;
		}
		
		return calendar.getTime();
	}
	
	public static void validateDateTimeAsRFC3339_sec5_6(String dateTime) throws UtilsException {
		// RFC 3339, section 5.6
		// es 2017-07-21T17:32:28Z
		String fullDate = null;
		String fullTime = null;
		try {
			// date-time = full-date "T" full-time
			if(dateTime.contains("T")==false) {
				throw new Exception("Expected 'T' separator");
			}
			String [] split = dateTime.split("T");
			if(split==null || split.length!=2) {
				throw new Exception("Expected 'full-date T full-time' format");
			}
			
			// full-date = date-fullyear "-" date-month "-" date-mday
			// date-fullyear   = 4DIGIT
			// date-month      = 2DIGIT  ; 01-12
			// date-mday       = 2DIGIT  ; 01-28, 01-29, 01-30, 01-31 based on month/year
			fullDate = split[0];
			fullTime = split[1];
		}catch(Throwable e){
			throw new UtilsException("Found dateTime '"+dateTime+"' has wrong format (see RFC 3339, section 5.6): "+e.getMessage(),e);
		}
		
		validateDateAsRFC3339_sec5_6(fullDate);
		validateTimeAsRFC3339_sec5_6(fullTime);
	}
	
	private static final String FULL_DATE_FORMAT = "yyyy-MM-dd";
	public static void validateDateAsRFC3339_sec5_6(String fullDate) throws UtilsException {
		// RFC 3339, section 5.6
		// es 2017-07-21
		try {
			// full-date = date-fullyear "-" date-month "-" date-mday
			// date-fullyear   = 4DIGIT
			// date-month      = 2DIGIT  ; 01-12
			// date-mday       = 2DIGIT  ; 01-28, 01-29, 01-30, 01-31 based on month/year
			if(fullDate==null || "".equals(fullDate)) {
				throw new Exception("undefined");
			}
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(FULL_DATE_FORMAT);
				Date d = sdf.parse(fullDate);
				d.toString();
			}catch(Exception e) {
				throw new Exception("uncorrect format: "+e.getMessage(),e);
			}
		}catch(Throwable e){
			throw new UtilsException("Found date '"+fullDate+"' has wrong format (see RFC 3339, section 5.6): "+e.getMessage(),e);
		}
	}
	
	private static final String FULL_TIME_FORMAT_SECONDS = "HH:mm:ss.SSS";
	private static final String FULL_TIME_FORMAT_WITHOUT_SECONDS = "HH:mm:ss";
	private static final String FULL_TIME_FORMAT_OFFSET = "HH:mm";
	public static void validateTimeAsRFC3339_sec5_6(String fullTime) throws UtilsException {
		// RFC 3339, section 5.6
		// es 17:32:28Z
		try {
			// full-time = partial-time time-offset
			// partial-time    = time-hour ":" time-minute ":" time-second [time-secfrac]
			// time-hour       = 2DIGIT  ; 00-23
			// time-minute     = 2DIGIT  ; 00-59
			// time-second     = 2DIGIT  ; 00-58, 00-59, 00-60 based on leap second
			// time-secfrac    = "." 1*DIGIT
			// time-offset     = "Z" / time-numoffset
			// time-numoffset  = ("+" / "-") time-hour ":" time-minute
			if(fullTime==null || "".equals(fullTime)) {
				throw new Exception("undefined");
			}
			if(fullTime.length()<9) {
				throw new Exception("too short");
			}
			String partialTime = null;
			if(fullTime.endsWith("Z")) {
				partialTime = fullTime.substring(0, fullTime.length()-1);
			}
			else {
				if(fullTime.contains("+")==false && fullTime.contains("-")==false) {
					throw new Exception("expected '(\"+\" / \"-\") or \"Z\" time-offset character");
				}
				String offset = null;
				String [] splitFullTime = null;
				if(fullTime.contains("+")) {
					splitFullTime = fullTime.split("\\+");
				}
				else {
					splitFullTime = fullTime.split("-");
				}
				if(splitFullTime==null || splitFullTime.length!=2) {
					throw new Exception("expected partial-time time-offset");
				}
				partialTime = splitFullTime[0];
				offset = splitFullTime[1];
				
				if(offset==null || "".equals(fullTime)) {
					throw new Exception("undefined offset time");
				}
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(FULL_TIME_FORMAT_OFFSET);
					Date d = sdf.parse(offset);
					d.toString();
				}catch(Exception e) {
					throw new Exception("uncorrect offset format: "+e.getMessage(),e);
				}
			}
			
			if(partialTime==null || "".equals(partialTime)) {
				throw new Exception("undefined partial time");
			}
			try {
				SimpleDateFormat sdf = null;
				if(partialTime.contains(".")) {
					sdf = new SimpleDateFormat(FULL_TIME_FORMAT_SECONDS);
				}
				else {
					sdf = new SimpleDateFormat(FULL_TIME_FORMAT_WITHOUT_SECONDS);
				}
				Date d = sdf.parse(partialTime);
				d.toString();
			}catch(Exception e) {
				throw new Exception("uncorrect offset format: "+e.getMessage(),e);
			}
			
		}catch(Throwable e){
			throw new UtilsException("Found time '"+fullTime+"' has wrong format (see RFC 3339, section 5.6): "+e.getMessage(),e);
		}
	}
	
	
	
	

	/** FORMAT Simple Date Format */
	
	public static final String SIMPLE_DATE_FORMAT_MS = Utilities.SIMPLE_DATE_FORMAT_MS;
	public static SimpleDateFormat getSimpleDateFormatMs() {
		return Utilities.getSimpleDateFormatMs();
	}
	
	public static final String SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ = Utilities.SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ;
	public static SimpleDateFormat getSimpleDateFormatMs_ISO_8601_TZ() {
		return Utilities.getSimpleDateFormatMs_ISO_8601_TZ();
	}
	
	public static final String SIMPLE_DATE_FORMAT_SECOND = Utilities.SIMPLE_DATE_FORMAT_SECOND;
	public static SimpleDateFormat getSimpleDateFormatSecond() {
		return Utilities.getSimpleDateFormatSecond();
	}
	
	public static final String SIMPLE_DATE_FORMAT_SECOND_ISO_8601_TZ = Utilities.SIMPLE_DATE_FORMAT_SECOND_ISO_8601_TZ;
	public static SimpleDateFormat getSimpleDateFormatSecond_ISO_8601_TZ() {
		return Utilities.getSimpleDateFormatSecond_ISO_8601_TZ();
	}
	
	public static final String SIMPLE_DATE_FORMAT_MINUTE = Utilities.SIMPLE_DATE_FORMAT_MINUTE;
	public static SimpleDateFormat getSimpleDateFormatMinute() {
		return Utilities.getSimpleDateFormatMinute();
	}
	
	public static final String SIMPLE_DATE_FORMAT_MINUTE_ISO_8601_TZ = Utilities.SIMPLE_DATE_FORMAT_MINUTE_ISO_8601_TZ;
	public static SimpleDateFormat getSimpleDateFormatMinute_ISO_8601_TZ() {
		return Utilities.getSimpleDateFormatMinute_ISO_8601_TZ();
	}
	
	public static final String SIMPLE_DATE_FORMAT_HOUR = Utilities.SIMPLE_DATE_FORMAT_HOUR;
	public static SimpleDateFormat getSimpleDateFormatHour() {
		return Utilities.getSimpleDateFormatHour();
	}
	
	public static final String SIMPLE_DATE_FORMAT_HOUR_ISO_8601_TZ = Utilities.SIMPLE_DATE_FORMAT_HOUR_ISO_8601_TZ;
	public static SimpleDateFormat getSimpleDateFormatHour_ISO_8601_TZ() {
		return Utilities.getSimpleDateFormatHour_ISO_8601_TZ();
	}
	
	public static final String SIMPLE_DATE_FORMAT_DAY = Utilities.SIMPLE_DATE_FORMAT_DAY;
	public static SimpleDateFormat getSimpleDateFormatDay() {
		return Utilities.getSimpleDateFormatDay();
	}
	
	public static final String SIMPLE_DATE_FORMAT_DAY_ISO_8601_TZ = Utilities.SIMPLE_DATE_FORMAT_DAY_ISO_8601_TZ;
	public static SimpleDateFormat getSimpleDateFormatDay_ISO_8601_TZ() {
		return Utilities.getSimpleDateFormatDay_ISO_8601_TZ();
	}

	public static Date parseDate(String dateParam) throws ParseException {
		return Utilities.parseDateRFC3339_sec5_6(dateParam);
	} 
	public static Date parseDateRFC3339_sec5_6(String dateParam) throws ParseException {
		return Utilities.parseDateRFC3339_sec5_6(dateParam);
	} 
	
	
}
