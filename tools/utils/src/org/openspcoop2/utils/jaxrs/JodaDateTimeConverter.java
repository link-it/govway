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

package org.openspcoop2.utils.jaxrs;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ParamConverter;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;

/**	
 * JodaDateTimeConverter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JodaDateTimeConverter implements ParamConverter<org.joda.time.DateTime> {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(JodaDateTimeConverter.class);

	private DateTimeZone timeZone = null;
	public JodaDateTimeConverter(DateTimeZone timeZone) {
		this.timeZone = timeZone;	
	}

	@Override
	public org.joda.time.DateTime fromString(String date) {
		/*
		try {
			Date d = Utilities.parseDate(date);
			return new org.joda.time.DateTime(d);
		} catch (Exception ex) {
			log.error(ex.getMessage(),ex);
			throw new WebApplicationException("Converter '"+date+"' to org.joda.time.DateTime failed: "+ex,ex,400);
		}
		*/
		try {
			//DateTimeFormatter formatter = ISODateTimeFormat.dateTimeParser();
			DateTimeFormatter formatter = ISODateTimeFormat.dateOptionalTimeParser().withZone(this.timeZone);
			return formatter.parseDateTime(date);
		} catch (Exception ex) {
			log.error(ex.getMessage(),ex);
			throw new WebApplicationException("Converter org.joda.time.DateTime to String failed: "+ex,ex,400);
		}
	}

	@Override
	public String toString(org.joda.time.DateTime t) {
		try {
			//DateTimeFormatter formatter = ISODateTimeFormat.dateTimeParser();
			DateTimeFormatter formatter = ISODateTimeFormat.dateOptionalTimeParser().withZone(this.timeZone);
			return formatter.print(t);
		} catch (Exception ex) {
			log.error(ex.getMessage(),ex);
			throw new WebApplicationException("Converter org.joda.time.DateTime to String failed: "+ex,ex,400);
		}
	}

}
