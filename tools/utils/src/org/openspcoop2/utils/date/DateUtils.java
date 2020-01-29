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

package org.openspcoop2.utils.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
	
	private static DateEngineType DEFAULT_DATA_ENGINE_TYPE = DateEngineType.JAVA_TIME;
	public static DateEngineType getDEFAULT_DATA_ENGINE_TYPE() {
		return DEFAULT_DATA_ENGINE_TYPE;
	}
	public static void setDEFAULT_DATA_ENGINE_TYPE(DateEngineType dEFAULT_DATA_ENGINE_TYPE) {
		DEFAULT_DATA_ENGINE_TYPE = dEFAULT_DATA_ENGINE_TYPE;
	}

	public static final String SIMPLE_DATE_FORMAT_MS = "yyyy-MM-dd_HH:mm:ss.SSS";
	public static SimpleDateFormat getSimpleDateFormatMs() {
		return getDefaultDateTimeFormatterMs();
	}
	public static SimpleDateFormat getOldSimpleDateFormatMs() {
		return new SimpleDateFormat (SIMPLE_DATE_FORMAT_MS); // SimpleDateFormat non e' thread-safe
	}
	public static DateTimeFormatter getDateTimeFormatterMs() {
		return DateUtils.getDateTimeFormatter(SIMPLE_DATE_FORMAT_MS);
	}
	public static org.joda.time.format.DateTimeFormatter getJodaDateTimeFormatterMs() {
		return DateUtils.getJodaDateTimeFormatter(SIMPLE_DATE_FORMAT_MS);
	}
	public static DateTimeFormatterWrapper getDateTimeFormatterMs(DateEngineType type) {
		return new DateTimeFormatterWrapper(type.toDateType(true), SIMPLE_DATE_FORMAT_MS, false);
	}
	public static DateTimeFormatterWrapper getDefaultDateTimeFormatterMs() {
		return new DateTimeFormatterWrapper(DEFAULT_DATA_ENGINE_TYPE.toDateType(true), SIMPLE_DATE_FORMAT_MS, false);
	}
	
	public static final String SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ = "yyyy-MM-dd_HH:mm:ss.SSSX";
	public static SimpleDateFormat getSimpleDateFormatMs_ISO_8601_TZ() {
		return getDefaultDateTimeFormatterMs_ISO_8601_TZ();
	}
	public static SimpleDateFormat getOldSimpleDateFormatMs_ISO_8601_TZ() {
		SimpleDateFormat sdf = new SimpleDateFormat (SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ); // SimpleDateFormat non e' thread-safe
		sdf.setCalendar(Calendar.getInstance());
		return sdf;
	}
	public static DateTimeFormatter getDateTimeFormatterMs_ISO_8601_TZ() {
		return DateUtils.getDateTimeFormatter(SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public static org.joda.time.format.DateTimeFormatter getJodaDateTimeFormatterMs_ISO_8601_TZ() {
		return DateUtils.getJodaDateTimeFormatter(SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ);
	}
	public static DateTimeFormatterWrapper getDateTimeFormatterMs_ISO_8601_TZ(DateEngineType type) {
		return new DateTimeFormatterWrapper(type.toDateType(true), SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ, true);
	}
	public static DateTimeFormatterWrapper getDefaultDateTimeFormatterMs_ISO_8601_TZ() {
		return new DateTimeFormatterWrapper(DEFAULT_DATA_ENGINE_TYPE.toDateType(true), SIMPLE_DATE_FORMAT_MS_ISO_8601_TZ, true);
	}
	
	public static final String SIMPLE_DATE_FORMAT_SECOND = "yyyy-MM-dd_HH:mm:ss";
	public static SimpleDateFormat getSimpleDateFormatSecond() {
		return getDefaultDateTimeFormatterSecond();
	}
	public static SimpleDateFormat getOldSimpleDateFormatSecond() {
		return new SimpleDateFormat (SIMPLE_DATE_FORMAT_SECOND); // SimpleDateFormat non e' thread-safe
	}
	public static DateTimeFormatter getDateTimeFormatterSecond() {
		return DateUtils.getDateTimeFormatter(SIMPLE_DATE_FORMAT_SECOND);
	}
	public static org.joda.time.format.DateTimeFormatter getJodaDateTimeFormatterSecond() {
		return DateUtils.getJodaDateTimeFormatter(SIMPLE_DATE_FORMAT_SECOND);
	}
	public static DateTimeFormatterWrapper getDateTimeFormatterSecond(DateEngineType type) {
		return new DateTimeFormatterWrapper(type.toDateType(true), SIMPLE_DATE_FORMAT_SECOND, false);
	}
	public static DateTimeFormatterWrapper getDefaultDateTimeFormatterSecond() {
		return new DateTimeFormatterWrapper(DEFAULT_DATA_ENGINE_TYPE.toDateType(true), SIMPLE_DATE_FORMAT_SECOND, false);
	}
	
	public static final String SIMPLE_DATE_FORMAT_SECOND_ISO_8601_TZ = "yyyy-MM-dd_HH:mm:ssX";
	public static SimpleDateFormat getSimpleDateFormatSecond_ISO_8601_TZ() {
		return getDefaultDateTimeFormatterSecond_ISO_8601_TZ();
	}
	public static SimpleDateFormat getOldSimpleDateFormatSecond_ISO_8601_TZ() {
		SimpleDateFormat sdf =  new SimpleDateFormat (SIMPLE_DATE_FORMAT_SECOND_ISO_8601_TZ); // SimpleDateFormat non e' thread-safe
		sdf.setCalendar(Calendar.getInstance());
		return sdf;
	}
	public static DateTimeFormatter getDateTimeFormatterSecond_ISO_8601_TZ() {
		return DateUtils.getDateTimeFormatter(SIMPLE_DATE_FORMAT_SECOND_ISO_8601_TZ);
	}
	public static org.joda.time.format.DateTimeFormatter getJodaDateTimeFormatterSecond_ISO_8601_TZ() {
		return DateUtils.getJodaDateTimeFormatter(SIMPLE_DATE_FORMAT_SECOND_ISO_8601_TZ);
	}
	public static DateTimeFormatterWrapper getDateTimeFormatterSecond_ISO_8601_TZ(DateEngineType type) {
		return new DateTimeFormatterWrapper(type.toDateType(true), SIMPLE_DATE_FORMAT_SECOND_ISO_8601_TZ, true);
	}
	public static DateTimeFormatterWrapper getDefaultDateTimeFormatterSecond_ISO_8601_TZ() {
		return new DateTimeFormatterWrapper(DEFAULT_DATA_ENGINE_TYPE.toDateType(true), SIMPLE_DATE_FORMAT_SECOND_ISO_8601_TZ, true);
	}
	
	public static final String SIMPLE_DATE_FORMAT_MINUTE = "yyyy-MM-dd_HH:mm";
	public static SimpleDateFormat getSimpleDateFormatMinute() {
		return getDefaultDateTimeFormatterMinute();
	}
	public static SimpleDateFormat getOldSimpleDateFormatMinute() {
		return new SimpleDateFormat (SIMPLE_DATE_FORMAT_MINUTE); // SimpleDateFormat non e' thread-safe
	}
	public static DateTimeFormatter getDateTimeFormatterMinute() {
		return DateUtils.getDateTimeFormatter(SIMPLE_DATE_FORMAT_MINUTE);
	}
	public static org.joda.time.format.DateTimeFormatter getJodaDateTimeFormatterMinute() {
		return DateUtils.getJodaDateTimeFormatter(SIMPLE_DATE_FORMAT_MINUTE);
	}
	public static DateTimeFormatterWrapper getDateTimeFormatterMinute(DateEngineType type) {
		return new DateTimeFormatterWrapper(type.toDateType(true), SIMPLE_DATE_FORMAT_MINUTE, false);
	}
	public static DateTimeFormatterWrapper getDefaultDateTimeFormatterMinute() {
		return new DateTimeFormatterWrapper(DEFAULT_DATA_ENGINE_TYPE.toDateType(true), SIMPLE_DATE_FORMAT_MINUTE, false);
	}
	
	public static final String SIMPLE_DATE_FORMAT_MINUTE_ISO_8601_TZ = "yyyy-MM-dd_HH:mmX";
	public static SimpleDateFormat getSimpleDateFormatMinute_ISO_8601_TZ() {
		return getDefaultDateTimeFormatterMinute_ISO_8601_TZ();
	}
	public static SimpleDateFormat getOldSimpleDateFormatMinute_ISO_8601_TZ() {
		SimpleDateFormat sdf =  new SimpleDateFormat (SIMPLE_DATE_FORMAT_MINUTE_ISO_8601_TZ); // SimpleDateFormat non e' thread-safe
		sdf.setCalendar(Calendar.getInstance());
		return sdf;
	}
	public static DateTimeFormatter getDateTimeFormatterMinute_ISO_8601_TZ() {
		return DateUtils.getDateTimeFormatter(SIMPLE_DATE_FORMAT_MINUTE_ISO_8601_TZ);
	}
	public static org.joda.time.format.DateTimeFormatter getJodaDateTimeFormatterMinute_ISO_8601_TZ() {
		return DateUtils.getJodaDateTimeFormatter(SIMPLE_DATE_FORMAT_MINUTE_ISO_8601_TZ);
	}
	public static DateTimeFormatterWrapper getDateTimeFormatterMinute_ISO_8601_TZ(DateEngineType type) {
		return new DateTimeFormatterWrapper(type.toDateType(true), SIMPLE_DATE_FORMAT_MINUTE_ISO_8601_TZ, true);
	}
	public static DateTimeFormatterWrapper getDefaultDateTimeFormatterMinute_ISO_8601_TZ() {
		return new DateTimeFormatterWrapper(DEFAULT_DATA_ENGINE_TYPE.toDateType(true), SIMPLE_DATE_FORMAT_MINUTE_ISO_8601_TZ, true);
	}
	
	public static final String SIMPLE_DATE_FORMAT_HOUR = "yyyy-MM-dd_HH";
	public static SimpleDateFormat getSimpleDateFormatHour() {
		return getDefaultDateTimeFormatterHour();
	}
	public static SimpleDateFormat getOldSimpleDateFormatHour() {
		return new SimpleDateFormat (SIMPLE_DATE_FORMAT_HOUR); // SimpleDateFormat non e' thread-safe
	}
	public static DateTimeFormatter getDateTimeFormatterHour() {
		return DateUtils.getDateTimeFormatter(SIMPLE_DATE_FORMAT_HOUR);
	}
	public static org.joda.time.format.DateTimeFormatter getJodaDateTimeFormatterHour() {
		return DateUtils.getJodaDateTimeFormatter(SIMPLE_DATE_FORMAT_HOUR);
	}
	public static DateTimeFormatterWrapper getDateTimeFormatterHour(DateEngineType type) {
		return new DateTimeFormatterWrapper(type.toDateType(true), SIMPLE_DATE_FORMAT_HOUR, false);
	}
	public static DateTimeFormatterWrapper getDefaultDateTimeFormatterHour() {
		return new DateTimeFormatterWrapper(DEFAULT_DATA_ENGINE_TYPE.toDateType(true), SIMPLE_DATE_FORMAT_HOUR, false);
	}
	
	public static final String SIMPLE_DATE_FORMAT_HOUR_ISO_8601_TZ = "yyyy-MM-dd_HHX";
	public static SimpleDateFormat getSimpleDateFormatHour_ISO_8601_TZ() {
		return getDefaultDateTimeFormatterHour_ISO_8601_TZ();
	}
	public static SimpleDateFormat getOldSimpleDateFormatHour_ISO_8601_TZ() {
		SimpleDateFormat sdf =  new SimpleDateFormat (SIMPLE_DATE_FORMAT_HOUR_ISO_8601_TZ); // SimpleDateFormat non e' thread-safe
		sdf.setCalendar(Calendar.getInstance());
		return sdf;
	}
	public static DateTimeFormatter getDateTimeFormatterHour_ISO_8601_TZ() {
		return DateUtils.getDateTimeFormatter(SIMPLE_DATE_FORMAT_HOUR_ISO_8601_TZ);
	}
	public static org.joda.time.format.DateTimeFormatter getJodaDateTimeFormatterHour_ISO_8601_TZ() {
		return DateUtils.getJodaDateTimeFormatter(SIMPLE_DATE_FORMAT_HOUR_ISO_8601_TZ);
	}
	public static DateTimeFormatterWrapper getDateTimeFormatterHour_ISO_8601_TZ(DateEngineType type) {
		return new DateTimeFormatterWrapper(type.toDateType(true), SIMPLE_DATE_FORMAT_HOUR_ISO_8601_TZ, true);
	}
	public static DateTimeFormatterWrapper getDefaultDateTimeFormatterHour_ISO_8601_TZ() {
		return new DateTimeFormatterWrapper(DEFAULT_DATA_ENGINE_TYPE.toDateType(true), SIMPLE_DATE_FORMAT_HOUR_ISO_8601_TZ, true);
	}
	
	public static final String SIMPLE_DATE_FORMAT_DAY = "yyyy-MM-dd";
	public static SimpleDateFormat getSimpleDateFormatDay() {
		return getDefaultDateTimeFormatterDay();
	}
	public static SimpleDateFormat getOldSimpleDateFormatDay() {
		return new SimpleDateFormat (SIMPLE_DATE_FORMAT_DAY); // SimpleDateFormat non e' thread-safe
	}
	public static DateTimeFormatter getDateTimeFormatterDay() {
		return DateUtils.getDateTimeFormatter(SIMPLE_DATE_FORMAT_DAY);
	}
	public static org.joda.time.format.DateTimeFormatter getJodaDateTimeFormatterDay() {
		return DateUtils.getJodaDateTimeFormatter(SIMPLE_DATE_FORMAT_DAY);
	}
	public static DateTimeFormatterWrapper getDateTimeFormatterDay(DateEngineType type) {
		return new DateTimeFormatterWrapper(type.toDateType(true), SIMPLE_DATE_FORMAT_DAY, false);
	}
	public static DateTimeFormatterWrapper getDefaultDateTimeFormatterDay() {
		return new DateTimeFormatterWrapper(DEFAULT_DATA_ENGINE_TYPE.toDateType(true), SIMPLE_DATE_FORMAT_DAY, false);
	}
	
	public static final String SIMPLE_DATE_FORMAT_DAY_ISO_8601_TZ = "yyyy-MM-ddX";
	public static SimpleDateFormat getSimpleDateFormatDay_ISO_8601_TZ() {
		return getDefaultDateTimeFormatterDay_ISO_8601_TZ();
	}
	public static SimpleDateFormat getOldSimpleDateFormatDay_ISO_8601_TZ() {
		return new SimpleDateFormat (SIMPLE_DATE_FORMAT_DAY_ISO_8601_TZ); // SimpleDateFormat non e' thread-safe
	}
	public static DateTimeFormatter getDateTimeFormatterDay_ISO_8601_TZ() {
		return DateUtils.getDateTimeFormatter(SIMPLE_DATE_FORMAT_DAY_ISO_8601_TZ);
	}
	public static org.joda.time.format.DateTimeFormatter getJodaDateTimeFormatterDay_ISO_8601_TZ() {
		return DateUtils.getJodaDateTimeFormatter(SIMPLE_DATE_FORMAT_DAY_ISO_8601_TZ);
	}
	public static DateTimeFormatterWrapper getDateTimeFormatterDay_ISO_8601_TZ(DateEngineType type) {
		return new DateTimeFormatterWrapper(type.toDateType(true), SIMPLE_DATE_FORMAT_DAY_ISO_8601_TZ, true);
	}
	public static DateTimeFormatterWrapper getDefaultDateTimeFormatterDay_ISO_8601_TZ() {
		return new DateTimeFormatterWrapper(DEFAULT_DATA_ENGINE_TYPE.toDateType(true), SIMPLE_DATE_FORMAT_DAY_ISO_8601_TZ, true);
	}

	
	
	
	/** Simple Date Format che supporta tutti i formati rfc3339 */
	
	public static Date parseDate(String dateParam) throws ParseException {
		return parseDateRFC3339_sec5_6(dateParam);
	} 
	public static Date parseDateRFC3339_sec5_6(String dateParam) throws ParseException {
		String date = dateParam;
		
		// https://tools.ietf.org/html/rfc3339
		boolean withTimeZone = false;
		if(date.length()>SIMPLE_DATE_FORMAT_SECOND.length()) {
			// prendo la parte dopo l'ora, almeno non confondo il '-' del giorno con il '-' dell'offset
			String check = date.substring(SIMPLE_DATE_FORMAT_SECOND.length());
			withTimeZone = check.endsWith("Z") || check.contains("+") || check.contains("-");
		}
						
		if(date.contains("T")) {
			date = date.replace("T", "_");
			
			if(date.contains(".")) {
				if(withTimeZone) {
					return getSimpleDateFormatMs_ISO_8601_TZ().parse(date);
				}
				else {
					return getSimpleDateFormatMs().parse(date);
				}
			}
			else {
				if(date.contains(":")) {
					String [] split = date.split(":");
					if(split.length>1) {
						if(withTimeZone) {
							return getSimpleDateFormatSecond_ISO_8601_TZ().parse(date);
						}
						else {
							return getSimpleDateFormatSecond().parse(date);
						}
					}
					else {
						if(withTimeZone) {
							return getSimpleDateFormatMinute_ISO_8601_TZ().parse(date);
						}
						else {
							return getSimpleDateFormatMinute().parse(date);
						}
					}
				}
				else {
					if(withTimeZone) {
						return getSimpleDateFormatHour_ISO_8601_TZ().parse(date);
					}
					else {
						return getSimpleDateFormatHour().parse(date);
					}
				}
			}
		}
		else {
			if(withTimeZone) {
				return getSimpleDateFormatDay_ISO_8601_TZ().parse(date);
			}
			else {
				return getSimpleDateFormatDay().parse(date);
			}
		}
	}
	
	
	
	/** java.time DateTimeFormatter */
	
	private static Map<String, DateTimeFormatter> mapFormatToDateTimeFormatter = new HashMap<String, DateTimeFormatter>();
	private static synchronized void initDateTimeFormatter(String format) {
		if(mapFormatToDateTimeFormatter.containsKey(format)==false) {
			mapFormatToDateTimeFormatter.put(format, DateTimeFormatter.ofPattern(format));
		}
	}
	public static DateTimeFormatter getDateTimeFormatter(String format) {
		if(mapFormatToDateTimeFormatter.containsKey(format)==false) {
			initDateTimeFormatter(format);
		}
		return mapFormatToDateTimeFormatter.get(format);
	}
	
	
	/** java.time Converter */
	
	private static ZoneId zoneId = ZoneId.systemDefault();
	
	/** java.time Converter (LocalDate) */
	
	public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		return convertToLocalDateViaInstant(dateToConvert, DateUtils.zoneId);
	}
	public static LocalDate convertToLocalDateViaInstant(Date dateToConvert, ZoneId zoneIdParam) {
	    return dateToConvert.toInstant()
	      .atZone(zoneIdParam)
	      .toLocalDate();
	}
	
	public static LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
		return convertToLocalDateViaMilisecond(dateToConvert, DateUtils.zoneId);
	}
	public static LocalDate convertToLocalDateViaMilisecond(Date dateToConvert, ZoneId zoneIdParam) {
	    return Instant.ofEpochMilli(dateToConvert.getTime())
	      .atZone(zoneIdParam)
	      .toLocalDate();
	}
	
	public static Date convertToDateViaSqlDate(LocalDate dateToConvert) {
	    return java.sql.Date.valueOf(dateToConvert);
	}
	
	public static Date convertToDateViaInstant(LocalDate dateToConvert) {
		return convertToDateViaInstant(dateToConvert, DateUtils.zoneId); 
	}
	public static Date convertToDateViaInstant(LocalDate dateToConvert, ZoneId zoneIdParam) {
	    return java.util.Date.from(dateToConvert.atStartOfDay()
	      .atZone(zoneIdParam)
	      .toInstant());
	}
	
	/** java.time Converter (LocalDateTime) */
	
	public static ZonedDateTime convertToZonedDateTimeViaInstant(Date dateToConvert) {
		return convertToZonedDateTimeViaInstant(dateToConvert, DateUtils.zoneId);
	}
	public static ZonedDateTime convertToZonedDateTimeViaInstant(Date dateToConvert, ZoneId zoneIdParam) {
	    return dateToConvert.toInstant()
	      .atZone(zoneIdParam);
	}
	
	public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
		return convertToLocalDateTimeViaInstant(dateToConvert, DateUtils.zoneId);
	}
	public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert, ZoneId zoneIdParam) {
	    return convertToZonedDateTimeViaInstant(dateToConvert, zoneIdParam)
	      .toLocalDateTime();
	}
	
	public static ZonedDateTime convertToZonedDateTimeViaMilisecond(Date dateToConvert) {
		return convertToZonedDateTimeViaMilisecond(dateToConvert, DateUtils.zoneId);
	}
	public static ZonedDateTime convertToZonedDateTimeViaMilisecond(Date dateToConvert, ZoneId zoneIdParam) {
	    return Instant.ofEpochMilli(dateToConvert.getTime())
	      .atZone(zoneIdParam);
	}
	
	public static LocalDateTime convertToLocalDateTimeViaMilisecond(Date dateToConvert) {
		return convertToLocalDateTimeViaMilisecond(dateToConvert, DateUtils.zoneId);
	}
	public static LocalDateTime convertToLocalDateTimeViaMilisecond(Date dateToConvert, ZoneId zoneIdParam) {
	    return convertToZonedDateTimeViaMilisecond(dateToConvert, zoneIdParam)
	      .toLocalDateTime();
	}
	
	public static LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date dateToConvert) {
	    return new java.sql.Timestamp(dateToConvert.getTime()).toLocalDateTime();
	}

	public static Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
	    return java.sql.Timestamp.valueOf(dateToConvert);
	}
	
	public static Date convertToDateViaInstant(LocalDateTime dateToConvert) {
		return convertToDateViaInstant(dateToConvert, DateUtils.zoneId);
	}
	public static Date convertToDateViaInstant(LocalDateTime dateToConvert, ZoneId zoneIdParam) {
	    return java.util.Date
	      .from(dateToConvert.atZone(zoneIdParam)
	      .toInstant());
	}
	
	public static Date convertToDateViaInstant(ZonedDateTime dateToConvert) {
		return convertToDateViaInstant(dateToConvert, DateUtils.zoneId);
	}
	public static Date convertToDateViaInstant(ZonedDateTime dateToConvert, ZoneId zoneIdParam) {
	    return java.util.Date
	      .from(dateToConvert
	      .toInstant());
	}
	
	
	
	/** joda.time DateTimeFormatter */
	
	private static Map<String, org.joda.time.format.DateTimeFormatter> mapFormatToJodaDateTimeFormatter = new HashMap<String, org.joda.time.format.DateTimeFormatter>();
	private static String normalizeTimeZoneFormatForJoda(String formato) {
		return formato.contains("X") ? formato.replaceAll("X", "Z") : formato;
	}
	private static synchronized void initJodaDateTimeFormatter(String format) {
		if(mapFormatToJodaDateTimeFormatter.containsKey(format)==false) {
			mapFormatToJodaDateTimeFormatter.put(format, org.joda.time.format.DateTimeFormat.forPattern(normalizeTimeZoneFormatForJoda(format)));
		}
	}
	public static org.joda.time.format.DateTimeFormatter getJodaDateTimeFormatter(String format) {
		if(mapFormatToJodaDateTimeFormatter.containsKey(format)==false) {
			initJodaDateTimeFormatter(format);
		}
		return mapFormatToJodaDateTimeFormatter.get(format);
	}
	
	
	
	/** joda.time Converter */
	
	private static DateTimeZone dtz = DateTimeZone.getDefault();
	
	public static org.joda.time.LocalDate convertToJodaLocalDate(Date dateToConvert) {
		return convertToJodaLocalDate(dateToConvert, DateUtils.dtz);
	}
	public static org.joda.time.LocalDate convertToJodaLocalDate(Date dateToConvert, DateTimeZone dtzParam) {
		return new org.joda.time.LocalDate(dateToConvert.getTime(), dtzParam);
	}
	
	public static org.joda.time.LocalDateTime convertToJodaLocalDateTime(Date dateToConvert) {
		return convertToJodaLocalDateTime(dateToConvert, DateUtils.dtz);
	}
	public static org.joda.time.LocalDateTime convertToJodaLocalDateTime(Date dateToConvert, DateTimeZone dtzParam) {
		return new org.joda.time.LocalDateTime(dateToConvert.getTime(), dtzParam);
	}
	
	public static DateTime convertToJodaDateTime(Date dateToConvert) {
		return convertToJodaDateTime(dateToConvert, DateUtils.dtz);
	}
	public static DateTime convertToJodaDateTime(Date dateToConvert, DateTimeZone dtzParam) {
		return new DateTime(dateToConvert.getTime(), dtzParam);
	}
	
	public static Date convertToDate(org.joda.time.LocalDate dateToConvert) {
		return dateToConvert.toDate();
	}
	public static Date convertToDate(org.joda.time.LocalDateTime dateToConvert) {
		return dateToConvert.toDate();
	}
	public static Date convertToDate(org.joda.time.DateTime dateToConvert) {
		return dateToConvert.toDate();
	}
	
}
