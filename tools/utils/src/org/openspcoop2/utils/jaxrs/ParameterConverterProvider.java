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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Calendar;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import org.joda.time.DateTimeZone;
import org.openspcoop2.utils.date.DateManager;

/**	
 * ParameterConverterProvider
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ParameterConverterProvider implements ParamConverterProvider {

	private String timeZoneId = null;
	public String getTimeZoneId() {
		return this.timeZoneId;
	}
	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}
	
	private DateTimeZone timeZone = null;
	private DateTimeZone getDateTimeZone() {
		if(this.timeZone==null) {
			this.initTimeZone();
		}
		return this.timeZone;
	}
	
	private synchronized void initTimeZone() {
		if(this.timeZone == null) {
			if(this.timeZoneId==null) {
				try {
					Calendar c = DateManager.getCalendar();
					this.setTimeZoneId(c.getTimeZone().getID());
				}catch(Exception e) {
					throw new RuntimeException(e.getMessage(),e);
				}
			}
			this.timeZone = DateTimeZone.forID(this.timeZoneId);
		}
	}
	
	@Override
    public <T> ParamConverter<T> getConverter(Class<T> type, Type type1, Annotation[] antns) {
        if (org.joda.time.DateTime.class.equals(type)) {
            @SuppressWarnings("unchecked")
            ParamConverter<T> paramConverter = (ParamConverter<T>) new JodaDateTimeConverter(this.getDateTimeZone());
            return paramConverter;
        }
        else if (org.joda.time.LocalDate.class.equals(type)) {
            @SuppressWarnings("unchecked")
            ParamConverter<T> paramConverter = (ParamConverter<T>) new JodaLocalDateConverter(this.getDateTimeZone());
            return paramConverter;
        }
        return null;
    }

}

