/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

/**
 * PluginConverter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
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
			
			if(fSelectItems!=null) {
				selectItems = (List<SelectItem>)  fSelectItems.getValue();
			}
		}
		
		if(selectItems!=null) {
			for (SelectItem selectItem : selectItems) {
				if(selectItem.getLabel().equalsIgnoreCase(value)) {
					return selectItem.getValue();
				}
			}
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value == null) {
			return PluginConverter.NONE_STRING;
		}

		if(value instanceof org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin){
			org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin facesSelectItem = (org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin) value;
			return facesSelectItem.getLabel();
		}
		else if(value instanceof org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin){
			org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin facesSelectItem = (org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin) value;
			return facesSelectItem.getLabel();
		}
		else if(value instanceof org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin){
			org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin facesSelectItem = (org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin) value;
			return facesSelectItem.getLabel();
		}

		return PluginConverter.NONE_STRING;
	}

}
