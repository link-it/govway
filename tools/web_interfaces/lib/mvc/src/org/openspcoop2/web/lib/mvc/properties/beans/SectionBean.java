/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.web.lib.mvc.properties.beans;

import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.properties.exception.UserInputValidationException;

import java.util.Map;

import org.openspcoop2.core.mvc.properties.Conditions;
import org.openspcoop2.core.mvc.properties.Property;
import org.openspcoop2.core.mvc.properties.Section;
import org.openspcoop2.core.mvc.properties.constants.ItemType;
import org.openspcoop2.core.mvc.properties.provider.IProvider;

/**
 *  Bean di tipo Section arricchito delle informazioni grafiche.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SectionBean extends BaseItemBean<Section>{

	public SectionBean(Section section, String name, IProvider provider) {
		super(section, name, provider);
	}

	@Override
	public DataElement toDataElement(ConfigBean config, Map<String, String> mapNameValue) {
		DataElement de = new DataElement();
		de.setName(this.getName());
		de.setLabel(this.getItem().getLabel()); 
		if(this.visible != null && this.visible) {
			if(this.getItem()!=null && this.getItem().isHidden()) {
				de.setType(DataElementType.HIDDEN); 
			}
			else {
				de.setType(DataElementType.TITLE);
			}
		}else 
			de.setType(DataElementType.HIDDEN); 
		return de;
	}

	@Override
	public void setValueFromRequest(String parameterValue) {}

	@Override
	public Property getSaveProperty() {	return null; }
	
	@Override
	public String getPropertyValue() { return null;	}
	
	@Override
	public void init(String value) {}
	
	@Override
	public Conditions getConditions() {
		return this.item.getConditions();
	}
	
	@Override
	public ItemType getItemType() {
		return null;
	}
	
	@Override
	public String getLabel() {
		return this.item.getLabel();
	}
	
	@Override
	public void validate() throws UserInputValidationException {
	}
}
