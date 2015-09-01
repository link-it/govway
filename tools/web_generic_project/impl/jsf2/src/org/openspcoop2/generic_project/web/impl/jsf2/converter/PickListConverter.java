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
package org.openspcoop2.generic_project.web.impl.jsf2.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

/**
 * Converter per l'input PickList.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
//@FacesConverter("pickListConverter" )
public class PickListConverter implements Converter {

	@Override
	@SuppressWarnings("unchecked")
	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		Object ret = null;
		if (component instanceof PickList) {
			Object dualList = ((PickList) component).getValue();

			try{
				DualListModel<org.openspcoop2.generic_project.web.input.SelectItem> dl = (DualListModel<org.openspcoop2.generic_project.web.input.SelectItem>) dualList;
				
				for (org.openspcoop2.generic_project.web.input.SelectItem selectedItem : dl.getSource()) {
					if(selectedItem.getValue().equals(submittedValue)){
						ret = selectedItem;
						break;
					}
				}
				if (ret == null)
					for (org.openspcoop2.generic_project.web.input.SelectItem selectedItem  : dl.getTarget()) {
						if(selectedItem.getValue().equals(submittedValue)){
							ret = selectedItem;
							break;
						}
					}

			}catch(Exception e){

			}
		}
		return ret;
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
		String str = "";
		if(value == null) {
			return "";
		}

		if(value instanceof org.openspcoop2.generic_project.web.input.SelectItem ){
			org.openspcoop2.generic_project.web.input.SelectItem item = (org.openspcoop2.generic_project.web.input.SelectItem) value;
			return item.getLabel();
		}

		if(value instanceof SelectItem){
			SelectItem facesSelectItem = (SelectItem) value;

			if(facesSelectItem.getValue() instanceof org.openspcoop2.generic_project.web.input.SelectItem ){
				org.openspcoop2.generic_project.web.input.SelectItem comboBoxItem = (org.openspcoop2.generic_project.web.input.SelectItem) facesSelectItem.getValue();
				return comboBoxItem.getLabel();
			}
		}

		return str;
	}
}
