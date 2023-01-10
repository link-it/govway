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
package org.openspcoop2.protocol.sdk.properties;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;

/**
 * BooleanConsoleItem
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BooleanConsoleItem extends AbstractConsoleItem<Boolean> {

	// serve per gestire i 3 stati null, false e true
	// Tale opzione non e' utilizzabile se il default di una checkbox e' true ed e' abilitato il reload 
	private boolean convertFalseAsNull = true; // lascio il default attuale
	public void setConvertFalseAsNull(boolean convertFalseAsNull) {
		this.convertFalseAsNull = convertFalseAsNull;
	}
	public boolean isConvertFalseAsNull() {
		if(ConsoleItemType.CHECKBOX.equals(this.getType()) && this.getDefaultValue()!=null && this.getDefaultValue() && this.isReloadOnChange()) {
			return false;
		}
		else {
			return this.convertFalseAsNull;
		}
	}

	protected BooleanConsoleItem(String id, String label, ConsoleItemType type) throws ProtocolException {
		super(id, label, type);
	}
	
	@Override
	protected Boolean cloneValue(Boolean value) throws ProtocolException {
		try {
			return Boolean.valueOf(value.booleanValue());
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
}
