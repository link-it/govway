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

package org.openspcoop2.utils.date;

/**
 * DateType
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum DateEngineType {

	JAVA_UTIL, 
	JAVA_TIME,
	JODA;
	
	public DateType toDateTimeType() {
		switch (this) {
			case JAVA_UTIL:
				return DateType.JAVA_UTIL_DATE_TIME;
			case JAVA_TIME:
				return DateType.JAVA_TIME_DATE_TIME;
			case JODA:
				return DateType.JODA_DATE_TIME;
		}
		return null;
	}
	
	public DateType toDateType() {
		switch (this) {
			case JAVA_UTIL:
				return DateType.JAVA_UTIL_DATE;
			case JAVA_TIME:
				return DateType.JAVA_TIME_DATE;
			case JODA:
				return DateType.JODA_DATE;
		}
		return null;
	}
	
	public DateType toTimeType() {
		switch (this) {
			case JAVA_UTIL:
				return DateType.JAVA_UTIL_TIME;
			case JAVA_TIME:
				return DateType.JAVA_TIME_TIME;
			case JODA:
				return DateType.JODA_TIME;
		}
		return null;
	}
	
}
