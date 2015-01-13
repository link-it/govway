/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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
package org.openspcoop2.generic_project.web.converter;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.richfaces.component.html.HtmlListShuttle;

/**
* MultipleChoiceItemConverter converter per gli elementi degli input con selezione multipla. 
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class MultipleChoiceItemConverter implements Converter {
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		
		if(StringUtils.isEmpty(value))
			value = "*";
		
		if(component instanceof HtmlListShuttle){
			HtmlListShuttle shuttle = (HtmlListShuttle) component;
			
			Object sourceValue = shuttle.getSourceValue();
			
			if(sourceValue instanceof List){
				List<org.openspcoop2.generic_project.web.form.field.SelectItem> sourceList = (List<org.openspcoop2.generic_project.web.form.field.SelectItem>) sourceValue;
				
				for (org.openspcoop2.generic_project.web.form.field.SelectItem selectItem : sourceList) {
					if(selectItem.getLabel() != null && selectItem.getLabel().equals(value))
						return selectItem;
				}
			}
			
			Object targetValue = shuttle.getTargetValue();
			
			if(targetValue instanceof List){
				List<org.openspcoop2.generic_project.web.form.field.SelectItem> targetList = (List<org.openspcoop2.generic_project.web.form.field.SelectItem>) targetValue;
				
				for (org.openspcoop2.generic_project.web.form.field.SelectItem selectItem : targetList) {
					if(selectItem.getLabel() != null && selectItem.getLabel().equals(value))
						return selectItem;
				}
			}
		}

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
			
			if(fSelectItems != null && fSelectItems.getValue() != null)
				selectItems = (List<SelectItem>)  fSelectItems.getValue();
		}
		
		if(selectItems != null)
		for (SelectItem selectItem : selectItems) {
			org.openspcoop2.generic_project.web.form.field.SelectItem comboBoxItem = 
					(org.openspcoop2.generic_project.web.form.field.SelectItem)selectItem.getValue();
			if(comboBoxItem.getLabel() != null && comboBoxItem.getLabel().equalsIgnoreCase(value)) {
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

		if(value instanceof org.openspcoop2.generic_project.web.form.field.SelectItem ){
			org.openspcoop2.generic_project.web.form.field.SelectItem comboBoxItem = (org.openspcoop2.generic_project.web.form.field.SelectItem) value;
			return comboBoxItem.getLabel();
		}


		if(value instanceof SelectItem){
			SelectItem facesSelectItem = (SelectItem) value;

			if(facesSelectItem.getValue() instanceof org.openspcoop2.generic_project.web.form.field.SelectItem ){
				org.openspcoop2.generic_project.web.form.field.SelectItem comboBoxItem = (org.openspcoop2.generic_project.web.form.field.SelectItem) facesSelectItem.getValue();
				return comboBoxItem.getLabel();
			}
		}

		return "";
	}

}
