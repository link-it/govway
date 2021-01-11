/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.converter;

import org.openspcoop2.web.monitor.core.logger.LoggerManager;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.slf4j.Logger;

/**
 * LatenzaConverter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class LatenzaConverter  implements Converter {

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
		return null;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent component, Object value) {

		try{
			if(value!=null){
				if(value instanceof Date){
					long t = ((Date)value).getTime();
					if(t >=0)
						return DurataConverter.convertSystemTimeIntoString_millisecondi(t, true);
				}
				if(value instanceof Long){
					long t = ((Long)value).longValue();
					if(t >=0)
						return DurataConverter.convertSystemTimeIntoString_millisecondi(t, true);
				}
			} else {
				//return "N.D.";
				return DurataConverter.convertSystemTimeIntoString_millisecondi(0, true);
			}
		}catch(Exception e){
			log.error(e.getMessage(), e); 
		}

		//return "N.D.";
		return DurataConverter.convertSystemTimeIntoString_millisecondi(0, true);
	}



}
