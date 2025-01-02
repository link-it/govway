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
package org.openspcoop2.generic_project.web.impl.jsf1.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.richfaces.component.html.HtmlInputText;


/**
 * InputTextSelectItemConverter converter per gli elementi di tipo input text con value di tipo complesso SelectItem
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InputTextSelectItemConverter implements Converter {


	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		if(StringUtils.isEmpty(value))
			value = "*";
				 
		org.openspcoop2.generic_project.web.input.SelectItem selectItem = null;
		if(component instanceof HtmlInputText){
			HtmlInputText inputText = (HtmlInputText) component;

			Object valueAsObj = inputText.getValue();

			if(valueAsObj != null)
				if(valueAsObj instanceof SelectItem) {
					SelectItem s = (SelectItem) valueAsObj;
					if(s!=null && s.getLabel()!=null && s.getValue()!=null && (s.getValue() instanceof String))
					selectItem = new org.openspcoop2.generic_project.web.input.SelectItem(s.getLabel(), (String) s.getValue());
				}
		}
		
		if(selectItem == null)
			selectItem = new org.openspcoop2.generic_project.web.input.SelectItem(value, value);

		return selectItem;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value == null) {
			return "";
		}

		if(value instanceof org.openspcoop2.generic_project.web.input.SelectItem ){
			org.openspcoop2.generic_project.web.input.SelectItem comboBoxItem = (org.openspcoop2.generic_project.web.input.SelectItem) value;
			return comboBoxItem.getLabel();
		}


		if(value instanceof SelectItem){
			SelectItem facesSelectItem = (SelectItem) value;

			if(facesSelectItem.getValue() instanceof org.openspcoop2.generic_project.web.input.SelectItem ){
				org.openspcoop2.generic_project.web.input.SelectItem comboBoxItem = (org.openspcoop2.generic_project.web.input.SelectItem) facesSelectItem.getValue();
				return comboBoxItem.getLabel();
			}
		}

		return "";
	}

}