/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
import org.openspcoop2.web.lib.mvc.properties.exception.UserInputValidationException;
import org.openspcoop2.core.mvc.properties.Conditions;
import org.openspcoop2.core.mvc.properties.Property;
import org.openspcoop2.core.mvc.properties.constants.ItemType;
import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;

/***
 * 
 * Classe astratta che definisce le informazioni relative alla grafica degli elementi della configurazione.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BaseItemBean<T> {
	
	// elemento corrispondente all'interno del config (Section/SubSection/Item)
	protected IProvider provider = null;	
	protected T item = null;
	protected String name = null;
	protected String value = null;
	protected Boolean visible = null;
	protected Boolean oldVisible = null;
	
	public BaseItemBean(T item, String name, IProvider provider) {
		this.item = item;
		this.name = name;
		this.provider = provider;
	}

	public abstract DataElement toDataElement() throws ProviderException;
	
	public abstract void setValueFromRequest(String parameterValue)  throws ProviderException;
	
	public abstract Property getSaveProperty();
	
	public abstract String getPropertyValue();
	
	public abstract void init(String value) throws ProviderException;
	
	public abstract Conditions getConditions();
	
	public abstract ItemType getItemType();
	
	public abstract String getLabel();
	
	public abstract void validate() throws UserInputValidationException;

	public IProvider getProvider() {
		return this.provider;
	}
	
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
	
	public boolean isVisible() {
		return this.visible != null && this.visible.booleanValue();
	}
	
	public void setOldVisible(Boolean oldVisible) {
		this.oldVisible = oldVisible;
	}
	
	public Boolean getOldVisible() {
		return this.oldVisible;
	}
	
	public boolean isOldVisible() {
		return this.oldVisible != null && this.oldVisible.booleanValue();
	}
	
}
