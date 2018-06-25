/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.lang.StringUtils;

public class EnumTypeConverter implements Converter {

	@Override
	@SuppressWarnings({ "unchecked" })
	public Object getAsObject(FacesContext context, UIComponent comp,
			String value) throws ConverterException {
		if(StringUtils.isEmpty(value))
			return null;
		
		@SuppressWarnings("rawtypes")
		Class enumType = comp
							.getValueExpression("value")
							.getType(context.getELContext());
		return Enum.valueOf(enumType, value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object object) throws ConverterException {

		if (object == null) {
			return null;
		}
		
		if(object instanceof String)
			return object.toString();
		
		if(object instanceof Enum){
			@SuppressWarnings("rawtypes")
			Enum type = (Enum) object;
			return type.toString();
		}
		
		return object.toString();
	}

}
