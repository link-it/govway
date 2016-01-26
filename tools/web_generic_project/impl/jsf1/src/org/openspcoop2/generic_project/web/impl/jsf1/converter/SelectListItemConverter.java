/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.richfaces.taglib.ComboBoxTag;

import com.sun.faces.taglib.html_basic.SelectOneMenuTag;

/**
* SelectListItemConverter converter per gli elementi delle select list implementate con rich combobox
* 
* Il componente {@link ComboBoxTag} genera gli oggetti dell'interfaccia in maniera diversa dal tag {@link SelectOneMenuTag},
* genera una lista di label da visualizzare nella pagina invece 
* della struttura classica <select><option value="val">LABEL</option></select>
* E' necessario riconvertire la label visualizzata nel value da utilizzare nel backend. 
* 
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SelectListItemConverter implements Converter {
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		
		if(StringUtils.isEmpty(value))
			value = "*";

		List<SelectItem> selectItems = (List<SelectItem>)component.getAttributes().get("suggestionValues");
		if(selectItems == null){
			List<UIComponent> children = component.getChildren();
			UISelectItems fSelectItems = null;
			for (UIComponent child : children) {
				if(child instanceof UISelectItems){
					fSelectItems = (UISelectItems) child;
					break;
				}
			}
			
			selectItems = (List<SelectItem>)  fSelectItems.getValue();
		}
		
		for (SelectItem selectItem : selectItems) {
			org.openspcoop2.generic_project.web.input.SelectItem comboBoxItem = 
					(org.openspcoop2.generic_project.web.input.SelectItem)selectItem.getValue();
			if(comboBoxItem.getLabel().equalsIgnoreCase(value)) {
				return comboBoxItem;
			}
		}
		return null;
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
