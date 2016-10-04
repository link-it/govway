/*
 * OpenSPCoop - Customizable API Gateway 
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
import org.richfaces.component.html.HtmlListShuttle;

/**
* MultipleChoiceItemConverter converter per gli elementi degli input con selezione multipla. 
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class MultipleChoiceItemConverter implements Converter {
	
	
//	private static Logger log = LogUtilities.getLogger("console.gui");
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		
//		log.debug("Valore da controllare["+value+"]"); 
		
		if(StringUtils.isEmpty(value))
			value = "*";
		
		if(component instanceof HtmlListShuttle){
			HtmlListShuttle shuttle = (HtmlListShuttle) component;
			
			Object sourceValue = shuttle.getSourceValue();
			
			if(sourceValue instanceof List){
				List<org.openspcoop2.generic_project.web.input.SelectItem> sourceList = (List<org.openspcoop2.generic_project.web.input.SelectItem>) sourceValue;
				
				for (org.openspcoop2.generic_project.web.input.SelectItem selectItem : sourceList) {
					if(selectItem.getLabel() != null) {
						String trimmedValue = value.replace("  " , " ");
						String selectItemTrimmedValue = selectItem.getLabel().replace("  ", " ");
						if(StringUtils.equals(selectItemTrimmedValue,trimmedValue)){
//							log.debug("SOURCE ValueTrimmed["+trimmedValue+"] SelectItemTrimmed["+(selectItemTrimmedValue)+"] Presente");
							return selectItem;
						}
					}
				}
			}
			
			Object targetValue = shuttle.getTargetValue();
			
			if(targetValue instanceof List){
				List<org.openspcoop2.generic_project.web.input.SelectItem> targetList = (List<org.openspcoop2.generic_project.web.input.SelectItem>) targetValue;
				
				for (org.openspcoop2.generic_project.web.input.SelectItem selectItem : targetList) {
					if(selectItem.getLabel() != null) {
						String trimmedValue = value.replace("  " , " ");
						String selectItemTrimmedValue = selectItem.getLabel().replace("  ", " ");
						if(StringUtils.equals(selectItemTrimmedValue,trimmedValue)){
//							log.debug("TARGET ValueTrimmed["+trimmedValue+"] SelectItemTrimmed["+(selectItemTrimmedValue)+"] Presente");
//							System.out.println("SELECTITEMCONVERTER Value da controllare ["+(value)+"] Presente in lista target");
							return selectItem;
						}
					}
						
				}
			}
		}
		
//		log.debug("Value ["+(value)+"] non trovato ne nel source ne nel target");

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
			org.openspcoop2.generic_project.web.input.SelectItem comboBoxItem = 
					(org.openspcoop2.generic_project.web.input.SelectItem)selectItem.getValue();
			if(comboBoxItem.getLabel() != null && comboBoxItem.getLabel().equalsIgnoreCase(value)) {
				return comboBoxItem;
			}
		}
		
//		log.debug("Value ["+(value)+"] non trovato");
		
		
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value == null) {
			return "";
		}

		if(value instanceof org.openspcoop2.generic_project.web.input.SelectItem ){
			org.openspcoop2.generic_project.web.input.SelectItem comboBoxItem = (org.openspcoop2.generic_project.web.input.SelectItem) value;
			if(comboBoxItem.getLabel()!= null)
				return comboBoxItem.getLabel().trim();
			else
				return null;
		}


		if(value instanceof SelectItem){
			SelectItem facesSelectItem = (SelectItem) value;

			if(facesSelectItem.getValue() instanceof org.openspcoop2.generic_project.web.input.SelectItem ){
				org.openspcoop2.generic_project.web.input.SelectItem comboBoxItem = (org.openspcoop2.generic_project.web.input.SelectItem) facesSelectItem.getValue();
				if(comboBoxItem.getLabel()!= null)
					return comboBoxItem.getLabel().trim();
				else
					return null;
			}
		}

		return "";
	}

}
