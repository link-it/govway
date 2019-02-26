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
package org.openspcoop2.monitor.sdk.parameters;

import org.openspcoop2.monitor.sdk.constants.ParameterType;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;

/**
 * ParameterFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ParameterFactory {

	public static Parameter<?> createParameter(ParameterType type,String id) throws ParameterException{
		switch (type) {
		case CHECK_BOX:
			return new CheckBoxParameter(id);			
		case CALENDAR:
			return new CalendarParameter(id);
		case INPUT_SECRET:
			return new InputSecretParameter(id);
		case INPUT_TEXT:
			return new InputTextParameter(id);
		case OUTPUT_TEXT:
			return new OutputTextParameter(id);
		case RADIO_BUTTON:
			return new RadioButtonParameter(id);
		case SELECT_LIST:
			return new SelectListParameter(id);
		case TEXT_AREA:
			return new TextAreaParameter(id);			
		}
		throw new ParameterException("Unsupported type ["+type+"] for parameter "+id);
	}
	
}
