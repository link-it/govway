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

package org.openspcoop2.utils.jaxrs;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ParamConverter;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;

/**	
 * JodaLocalDateConverter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JodaLocalDateConverter implements ParamConverter<org.joda.time.LocalDate> {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JodaLocalDateConverter.class);

	private DateTimeZone timeZone = null;
	public JodaLocalDateConverter(DateTimeZone timeZone) {
		this.timeZone = timeZone;	
	}
    
    @Override
    public org.joda.time.LocalDate fromString(String date) {
        try {
        	//DateTimeFormatter formatter = ISODateTimeFormat.dateTimeParser();
        	DateTimeFormatter formatter = ISODateTimeFormat.dateOptionalTimeParser().withZone(this.timeZone);
			LocalDate ld = formatter.parseLocalDate(date);
        	return ld;
        } catch (Exception ex) {
        	log.error(ex.getMessage(),ex);
            throw new WebApplicationException("Converter '"+date+"' to org.joda.time.DateTime failed: "+ex,ex,400);
        }
    }

    @Override
    public String toString(org.joda.time.LocalDate t) {
    	 try {
    		 // DateTimeFormatter formatter = ISODateTimeFormat.dateTimeParser();
    		 DateTimeFormatter formatter = ISODateTimeFormat.dateOptionalTimeParser().withZone(this.timeZone);
 			 return formatter.print(t);
         } catch (Exception ex) {
         	log.error(ex.getMessage(),ex);
         	throw new WebApplicationException("Converter org.joda.time.DateTime to String failed: "+ex,ex,400);
         }
    }

}
