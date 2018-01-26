/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

package org.openspcoop2.generic_project.web.impl.jsf1.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.openspcoop2.generic_project.web.impl.jsf1.converter.SelectListItemConverter;

/**
 * RadioButtonConverter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RadioButtonConverter extends SelectListItemConverter{
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Object o = super.getAsObject(context, component, value);
		
		return o;
		
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String s = super.getAsString(context, component, value);
		
		return s;
	}

}
