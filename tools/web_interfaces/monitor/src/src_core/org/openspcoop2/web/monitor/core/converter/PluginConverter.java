package org.openspcoop2.web.monitor.core.converter;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

public class PluginConverter implements Converter {
	
	protected final static String NONE_STRING = "[Nessuno]";

	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		
		if(value==null || PluginConverter.NONE_STRING.equals(value))
			return null;

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
			if(selectItem.getLabel().equalsIgnoreCase(value)) {
				return selectItem.getValue();
			}
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value == null) {
			return PluginConverter.NONE_STRING;
		}

		if(value instanceof it.link.pdd.core.plugins.ricerche.InfoPlugin){
			it.link.pdd.core.plugins.ricerche.InfoPlugin facesSelectItem = (it.link.pdd.core.plugins.ricerche.InfoPlugin) value;
			return facesSelectItem.getLabel();
		}
		else if(value instanceof it.link.pdd.core.plugins.transazioni.InfoPlugin){
			it.link.pdd.core.plugins.transazioni.InfoPlugin facesSelectItem = (it.link.pdd.core.plugins.transazioni.InfoPlugin) value;
			return facesSelectItem.getLabel();
		}
		else if(value instanceof it.link.pdd.core.plugins.statistiche.InfoPlugin){
			it.link.pdd.core.plugins.statistiche.InfoPlugin facesSelectItem = (it.link.pdd.core.plugins.statistiche.InfoPlugin) value;
			return facesSelectItem.getLabel();
		}

		return PluginConverter.NONE_STRING;
	}

}
