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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * AllConverter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class AllConverter implements Converter {
	
	
	public final static String ALL_STRING  = "[Qualsiasi]";
	//private final static String NONE_STRING = ALL_STRING;
	
	@Override
	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
		
		if(value==null)
			return AllConverter.ALL_STRING;
		
		if(AllConverter.ALL_STRING.equals(value))
			return "*";
			
		return value;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent component, Object value) {
		
		if(value==null || "".equals(value) || "*".equals(value))
			return AllConverter.ALL_STRING;
		
		
		return value.toString();
	}

}
