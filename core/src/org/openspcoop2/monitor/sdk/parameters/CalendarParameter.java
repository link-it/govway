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
package org.openspcoop2.monitor.sdk.parameters;

import java.lang.reflect.Method;
import java.util.Date;

import org.openspcoop2.monitor.sdk.constants.ParameterType;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;

/**
 * CalendarParameter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CalendarParameter extends Parameter<Date> {

	public CalendarParameter(String id) {
		super(id, ParameterType.CALENDAR);
	}

	@Override
	public void setValueAsString(String value) throws ParameterException{
		if(value!=null){
			try{
				Class<?> c = Class.forName("org.openspcoop2.monitor.engine.utils.ContentFormatter");
				Method m = c.getMethod("toDate", String.class);
				Date d = (Date) m.invoke(null, value);
				this.setValue(d);
			}
			catch(Exception e){
				throw new ParameterException("Value ["+value+"] uncorrected: "+e.getMessage(),e);
			}
		}
	}
	
	@Override
	public String getValueAsString() throws ParameterException{
		if(this.getValue()!=null){
			try{
				Class<?> c = Class.forName("org.openspcoop2.monitor.engine.utils.ContentFormatter");
				Method m = c.getMethod("toString", Date.class);
				String s = (String) m.invoke(null, this.getValue());
				return s;
			}
			catch(Exception e){
				throw new ParameterException("Error occurs: "+e.getMessage(),e);
			}
		}
		return null;
	}
}
