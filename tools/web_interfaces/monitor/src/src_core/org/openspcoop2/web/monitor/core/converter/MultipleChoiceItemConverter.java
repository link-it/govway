/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.richfaces.component.html.HtmlListShuttle;

//import org.openspcoop2.web.monitor.core.logger.LoggerManager;

/**
* MultipleChoiceItemConverter converter per gli elementi degli input con selezione multipla. 
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class MultipleChoiceItemConverter implements Converter {
	
	
//	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		
//		log.debug("Valore da controllare["+value+"]"); 
		
		if(StringUtils.isEmpty(value))
			value = ""; //"*";
		
		if(component instanceof HtmlListShuttle){
			HtmlListShuttle shuttle = (HtmlListShuttle) component;
			
			Object sourceValue = shuttle.getSourceValue();
			
			if(sourceValue instanceof List){
				List<org.openspcoop2.web.monitor.core.bean.SelectItem> sourceList = (List<org.openspcoop2.web.monitor.core.bean.SelectItem>) sourceValue;
				
				for (org.openspcoop2.web.monitor.core.bean.SelectItem selectItem : sourceList) {
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
				List<org.openspcoop2.web.monitor.core.bean.SelectItem> targetList = (List<org.openspcoop2.web.monitor.core.bean.SelectItem>) targetValue;
				
				for (org.openspcoop2.web.monitor.core.bean.SelectItem selectItem : targetList) {
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
			org.openspcoop2.web.monitor.core.bean.SelectItem comboBoxItem = 
					(org.openspcoop2.web.monitor.core.bean.SelectItem)selectItem.getValue();
			String trimmedValue = value.replace("  " , " ");
			String selectItemTrimmedValue = comboBoxItem.getLabel().replace("  ", " ");
			if(StringUtils.equals(selectItemTrimmedValue,trimmedValue)){
//				log.debug("Lista Appoggio ValueTrimmed["+trimmedValue+"] SelectItemTrimmed["+(selectItemTrimmedValue)+"] Presente");
//			if(comboBoxItem.getLabel() != null && comboBoxItem.getLabel().equalsIgnoreCase(value)) {
				return comboBoxItem;
			}
		}
		
//		log.debug("Value ["+(value)+"] non trovato");
		
		
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
//		log.debug("getAsString ["+(value)+"]");
		
		if(value == null) {
			return "";
		}
		
//		log.debug("getAsString Class ["+(value.getClass())+"]");

		if(value instanceof org.openspcoop2.web.monitor.core.bean.SelectItem ){
			org.openspcoop2.web.monitor.core.bean.SelectItem comboBoxItem = (org.openspcoop2.web.monitor.core.bean.SelectItem) value;
			if(comboBoxItem.getLabel()!= null)
				return comboBoxItem.getLabel().trim();
			else
				return null;
		}


		if(value instanceof SelectItem){
			SelectItem facesSelectItem = (SelectItem) value;

			if(facesSelectItem.getValue() instanceof org.openspcoop2.web.monitor.core.bean.SelectItem ){
				org.openspcoop2.web.monitor.core.bean.SelectItem comboBoxItem = (org.openspcoop2.web.monitor.core.bean.SelectItem) facesSelectItem.getValue();
				if(comboBoxItem.getLabel()!= null)
					return comboBoxItem.getLabel().trim();
				else
					return null;
			}
		}

		return "";
	}

}
