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

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

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
	
	
	@Override
	public Date parse(String source) throws ParseException {
		
		try {
			switch (this.dataType) {
			case JAVA_UTIL_DATE:
			case JAVA_UTIL_DATE_TIME:
				SimpleDateFormat sdf =  new SimpleDateFormat (this.format); // SimpleDateFormat non e' thread-safe
				if(this.timeZone) {
					sdf.setCalendar(Calendar.getInstance());
				}
				return sdf.parse(source);
				
			case JAVA_TIME_DATE:
				return DateUtils.convertToDateViaInstant( LocalDate.parse(source, DateUtils.getDateTimeFormatter(this.format)) );
			case JAVA_TIME_DATE_TIME:
				return DateUtils.convertToDateViaInstant( LocalDateTime.parse(source, DateUtils.getDateTimeFormatter(this.format)) );
			
			case JODA_DATE:
			case JODA_DATE_TIME:
				return DateUtils.convertToDate(DateUtils.getJodaDateTimeFormatter(this.format).parseDateTime(source));
				
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
				SimpleDateFormat sdf =  new SimpleDateFormat (this.format); // SimpleDateFormat non e' thread-safe
				if(this.timeZone) {
					sdf.setCalendar(Calendar.getInstance());
				}
				toAppendTo.append(sdf.format(date));
				return toAppendTo;
				
			case JAVA_TIME_DATE:
			case JAVA_TIME_DATE_TIME:
				toAppendTo.append(DateUtils.getDateTimeFormatter(this.format).format(DateUtils.convertToZonedDateTimeViaInstant(date)));
				return toAppendTo;
			
			case JODA_DATE:
			case JODA_DATE_TIME:
				toAppendTo.append(DateUtils.convertToJodaDateTime(date).toString(DateUtils.getJodaDateTimeFormatter(this.format)));
				return toAppendTo;
			}
		}catch(Exception e) {
			throw new RuntimeException("["+this.dataType+"] "+e.getMessage(),e);
		}
		
		throw new RuntimeException("["+this.dataType+"] unsupported");
	}
}
