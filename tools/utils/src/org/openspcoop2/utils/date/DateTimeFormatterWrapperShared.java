/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

import java.text.AttributedCharacterIterator;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * DateTimeFormatterWrapper condiviso tra piu' thread e in sola lettura.
 * Il wrapper esegue format(Date) e parse(String) senza utilizzare lo stato ereditato da SimpleDateFormat (thread-safe):
 * l'istanza puo' quindi essere riutilizzata evitando il costo del costruttore di SimpleDateFormat.
 * I metodi che modificano lo stato, o che utilizzano lo stato ereditato non thread-safe, sollevano UnsupportedOperationException.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
final class DateTimeFormatterWrapperShared extends DateTimeFormatterWrapper {

	private static final long serialVersionUID = 1L;

	private static final String READ_ONLY = "Shared formatter is read-only";

	DateTimeFormatterWrapperShared(DateType dataType, String format, boolean timeZone) {
		super(dataType, format, timeZone);
	}

	@Override
	public void setTimeZone(TimeZone zone) {
		throw new UnsupportedOperationException(READ_ONLY);
	}
	@Override
	public void setLenient(boolean lenient) {
		throw new UnsupportedOperationException(READ_ONLY);
	}
	@Override
	public void setCalendar(Calendar newCalendar) {
		throw new UnsupportedOperationException(READ_ONLY);
	}
	@Override
	public void setNumberFormat(NumberFormat newNumberFormat) {
		throw new UnsupportedOperationException(READ_ONLY);
	}
	@Override
	public void set2DigitYearStart(Date startDate) {
		throw new UnsupportedOperationException(READ_ONLY);
	}
	@Override
	public void setDateFormatSymbols(DateFormatSymbols newFormatSymbols) {
		throw new UnsupportedOperationException(READ_ONLY);
	}
	@Override
	public void applyPattern(String pattern) {
		throw new UnsupportedOperationException(READ_ONLY);
	}
	@Override
	public void applyLocalizedPattern(String pattern) {
		throw new UnsupportedOperationException(READ_ONLY);
	}
	@Override
	public Date parse(String text, ParsePosition pos) {
		// utilizzerebbe lo stato ereditato da SimpleDateFormat, non thread-safe
		throw new UnsupportedOperationException(READ_ONLY);
	}
	@Override
	public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
		// utilizzerebbe lo stato ereditato da SimpleDateFormat, non thread-safe
		throw new UnsupportedOperationException(READ_ONLY);
	}

	@Override
	public boolean equals(Object obj) {
		return this==obj;
	}
	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
}
