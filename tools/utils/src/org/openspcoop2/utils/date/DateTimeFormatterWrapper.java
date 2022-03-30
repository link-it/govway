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

package org.openspcoop2.utils.date;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * DateTimeFormatterWrapper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DateTimeFormatterWrapper extends SimpleDateFormat {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DateType dataType;
	private String format;
	private boolean timeZone;

	public DateTimeFormatterWrapper(DateType dataType, String format, boolean timeZone) {
		this.dataType = dataType;
		this.format = format;
		this.timeZone = timeZone;
	}
	
	private TimeZone zone;
	@Override
	public void setTimeZone(TimeZone zone) {
		this.zone = zone;
	}
	
	@Override
	public Date parse(String source) throws ParseException {
		
		try {
			switch (this.dataType) {
			case JAVA_UTIL_DATE:
			case JAVA_UTIL_DATE_TIME:
			case JAVA_UTIL_TIME:
				SimpleDateFormat sdf =  new SimpleDateFormat (this.format); // SimpleDateFormat non e' thread-safe
				if(this.timeZone) {
					sdf.setCalendar(Calendar.getInstance());
				}
				else if(this.zone!=null) {
					sdf.setTimeZone(this.zone);
				}
				return sdf.parse(source);
				
			case JAVA_TIME_DATE:
				return DateUtils.convertToDateViaInstant( DateUtils.parseToLocalDate(this.format, source) );
			case JAVA_TIME_DATE_TIME:
				return DateUtils.convertToDateViaInstant( DateUtils.parseToLocalDateTime(this.format, source) );
			case JAVA_TIME_TIME:
				return DateUtils.convertToDateViaInstant( DateUtils.parseToLocalTime(this.format, source) );
			
			case JODA_DATE:
			case JODA_DATE_TIME:
			case JODA_TIME:
				return DateUtils.convertToDate(DateUtils.parseToJodaDateTime(this.format, source));
				
			}
		}catch(Exception e) {
			throw new RuntimeException("["+this.dataType+"] "+e.getMessage(),e);
		}
		
		throw new RuntimeException("["+this.dataType+"] unsupported");
	}

	@Override
	public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
		
		try {
			switch (this.dataType) {
			case JAVA_UTIL_DATE:
			case JAVA_UTIL_DATE_TIME:
			case JAVA_UTIL_TIME:
				SimpleDateFormat sdf =  new SimpleDateFormat (this.format); // SimpleDateFormat non e' thread-safe
				if(this.timeZone) {
					sdf.setCalendar(Calendar.getInstance());
				}
				else if(this.zone!=null) {
					sdf.setTimeZone(this.zone);
				}
				toAppendTo.append(sdf.format(date));
				return toAppendTo;
				
			case JAVA_TIME_DATE:
			case JAVA_TIME_DATE_TIME:
			case JAVA_TIME_TIME:
				toAppendTo.append(DateUtils.getDateTimeFormatter(this.format).format(DateUtils.convertToZonedDateTimeViaInstant(date)));
				return toAppendTo;
			
			case JODA_DATE:
			case JODA_DATE_TIME:
			case JODA_TIME:
				toAppendTo.append(DateUtils.convertToJodaDateTime(date).toString(DateUtils.getJodaDateTimeFormatter(this.format)));
				return toAppendTo;
			}
		}catch(Exception e) {
			throw new RuntimeException("["+this.dataType+"] "+e.getMessage(),e);
		}
		
		throw new RuntimeException("["+this.dataType+"] unsupported");
	}
	
}
