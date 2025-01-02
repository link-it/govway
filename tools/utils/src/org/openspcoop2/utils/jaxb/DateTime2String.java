/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.jaxb;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jakarta.xml.bind.DatatypeConverter;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * DateTime2Date
 *
 *
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DateTime2String extends XmlAdapter<String, Date>
{
	@Override
	public String marshal(Date dt) throws Exception {
		if(dt==null){
			return null;
		}
		Calendar cal = new GregorianCalendar();
	    cal.setTime(dt);
	    return DatatypeConverter.printDateTime(cal);
	}
	@Override
	public Date unmarshal(String sParam) throws Exception {
		if(sParam==null){
			return null;
		}
		String s = sParam.trim();
		return DatatypeConverter.parseDateTime(s).getTime();
	}
}

