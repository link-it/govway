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
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.richfaces.component.html.HtmlInputText;
import org.richfaces.component.html.HtmlListShuttle;

/***
 * 
 * Converter da utilizzare nella suggestion list di support agli elementi di tipo Choice.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class SuggestionSelectItemConverter extends InputTextSelectItemConverter {

	
//	private static Logger log = LogUtilities.getLogger("console.gui");
	
//	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		
//		log.debug("Valore da controllare["+value+"]"); 
	
		if(StringUtils.isEmpty(value))
			value = "*";
				 
		org.openspcoop2.generic_project.web.input.SelectItem selectItem = null;
		if(component instanceof HtmlInputText){
			HtmlInputText inputText = (HtmlInputText) component;

			Object valueAsObj = inputText.getValue();

			if(valueAsObj != null)
				if(valueAsObj instanceof org.openspcoop2.generic_project.web.input.SelectItem)
					selectItem = (org.openspcoop2.generic_project.web.input.SelectItem) valueAsObj;
			
			valueAsObj = inputText.getSubmittedValue();
			
			if(valueAsObj != null)
				if(valueAsObj instanceof org.openspcoop2.generic_project.web.input.SelectItem)
					selectItem = (org.openspcoop2.generic_project.web.input.SelectItem) valueAsObj;
			
		}
		
		if(selectItem == null){
			UIComponent parentC = component.getParent();
			
			if(parentC instanceof HtmlPanelGrid){
				HtmlPanelGrid parent = (HtmlPanelGrid) parentC;
				
				List<UIComponent> children = parent.getChildren();
				
				for (UIComponent uiComponent : children) {
					// uso il converter della picklist
					if(uiComponent instanceof HtmlListShuttle){
						HtmlListShuttle pickList = (HtmlListShuttle) uiComponent;
						
						return pickList.getConverter().getAsObject(context, pickList, value);
					}
					
//					if(uiComponent instanceof HtmlSuggestionBox){
//						HtmlSuggestionBox suggestionBox = (HtmlSuggestionBox) uiComponent;
//						
//						Object value2 = suggestionBox.getValue();
//						
//						if(value2 instanceof List){
//							List<org.openspcoop2.generic_project.web.input.SelectItem> listaSuggestion =( List<org.openspcoop2.generic_project.web.input.SelectItem> )value2;
//							
//							for (org.openspcoop2.generic_project.web.input.SelectItem item : listaSuggestion) {
//								if(item.getLabel() != null && item.getLabel().equals(value))
//									return item;
//							}
//						}
//							
//						
//						break;
//					}
				}
			}
		}
			

		return selectItem;
	}
}
