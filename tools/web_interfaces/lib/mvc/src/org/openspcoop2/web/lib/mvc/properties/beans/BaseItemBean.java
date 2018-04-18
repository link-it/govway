/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
package org.openspcoop2.web.lib.mvc.properties.beans;

import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.properties.Conditions;
import org.openspcoop2.web.lib.mvc.properties.Property;
import org.openspcoop2.web.lib.mvc.properties.constants.ItemType;

/***
 * 
 * Classe astratta che definisce le informazioni relative alla grafica degli elementi della configurazione.
 * 
 * @author pintori
 *
 * @param <T>
 */
public abstract class BaseItemBean<T> {
	
	// elemento corrispondente all'interno del config (Section/SubSection/Item)
	protected T item = null;
	protected String name = null;
	protected String value = null;
	protected Boolean visible = null;
	
	public BaseItemBean(T item) {
		this(item, null);
	}
	

	public BaseItemBean(T item, String name) {
		this.item = item;
		this.name = name;
	}
	
	public abstract DataElement toDataElement();
	
	public abstract void setValueFromRequest(String parameterValue);
	
	public abstract Property getSaveProperty();
	
	public abstract String getPropertyValue();
	
	public abstract void init(String value);
	
	public abstract Conditions getConditions();
	
	public abstract ItemType getItemType();

	public T getItem() {
		return this.item;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Boolean getVisible() {
		return this.visible;
	}
	
	
}
