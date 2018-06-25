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

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

public class SelectItemConverter implements Converter {
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		
		if(StringUtils.isEmpty(value))
			value = "*";

		List<SelectItem> selectItems = (List<SelectItem>)component.getAttributes().get("suggestionValues");
		if(selectItems == null){
			selectItems = getItems(context, component);
//			List<UIComponent> children = component.getChildren();
//			UISelectItems fSelectItems = null;
//			for (UIComponent child : children) {
//				if(child instanceof UISelectItems){
//					fSelectItems = (UISelectItems) child;
//					break;
//				}
//			}
//			
//			selectItems = (List<SelectItem>)  fSelectItems.getValue();
		}
		
		for (SelectItem selectItem : selectItems) {
			if(selectItem.getLabel().equalsIgnoreCase(value)) {
				return selectItem.getValue();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value == null) {
			return "";
		}

		List<SelectItem> selectItems = (List<SelectItem>)component.getAttributes().get("suggestionValues");
		if(selectItems == null){
			selectItems = getItems(context, component);
		}
		
		if(value instanceof String){
			for (SelectItem selectItem : selectItems) {
				Object value2 = selectItem.getValue();
				
				if(value2.equals(value)) {
					return selectItem.getLabel();
				}
			}
		}
		else if(value instanceof Integer){
			for (SelectItem selectItem : selectItems) {
				Object value2 = selectItem.getValue();
				if(value2!=null){
					if( value2 instanceof Integer){
						int v2 = ((Integer)value2).intValue();
						int v1 = ((Integer)value).intValue();
						if(v2 == v1) {
							return selectItem.getLabel();
						}
					}
					else if( value2 instanceof String){
						int v2 = -1;
						try{
							v2 = Integer.parseInt((String)value2);
						}catch(Exception e){}
						int v1 = ((Integer)value).intValue();
						if(v2 == v1) {
							return selectItem.getLabel();
						}
					}
				}
			}
		}
		else if(value instanceof Long){
			for (SelectItem selectItem : selectItems) {
				Object value2 = selectItem.getValue();
				if( value2 instanceof Long){
					long v2 = ((Long)value2).longValue();
					long v1 = ((Long)value).longValue();
					if(v2 == v1) {
						return selectItem.getLabel();
					}
				}
				else if( value2 instanceof String){
					long v2 = -1;
					try{
						v2 = Long.parseLong((String)value2);
					}catch(Exception e){}
					long v1 = ((Long)value).longValue();
					if(v2 == v1) {
						return selectItem.getLabel();
					}
				}
			}
		}
		
		if(value instanceof SelectItem){
			SelectItem facesSelectItem = (SelectItem) value;
			return facesSelectItem.getLabel();
		}

		return "";
	}

	
	@SuppressWarnings("unchecked")
	private List<SelectItem> getItems(FacesContext context, UIComponent component){
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		
		List<UIComponent> children = component.getChildren();
		// posso trovare diversi componenti all'interno della richcombobox, tag f:selectItems o f:selectItem
		List<UISelectItems> fSelectItems = new ArrayList<UISelectItems>();
		List<UISelectItem> listSelectItem = new ArrayList<UISelectItem>();
		
		for (UIComponent child : children) {
			if(child instanceof UISelectItems){
				fSelectItems.add((UISelectItems) child);
			} else if(child instanceof UISelectItem){
				listSelectItem.add((UISelectItem) child);
			}
		}
		
		if(fSelectItems.size() > 0)
			for (UISelectItems uiSelectItems : fSelectItems) {
				List<SelectItem> lst = (List<SelectItem>)  uiSelectItems.getValue();
				
				if(lst.size() > 0)
					selectItems.addAll(lst);
			}
		
		if(listSelectItem.size() >0)
			for (UISelectItem uiSelectItem : listSelectItem) {
				SelectItem item = null;
				if(uiSelectItem.getValue() != null)
					item = (SelectItem) uiSelectItem.getValue();
				else {
					item = new SelectItem(uiSelectItem.getItemValue(), uiSelectItem.getItemLabel());
				}
				
				selectItems.add(item);
			}
		
		return selectItems;
	}
	
}
