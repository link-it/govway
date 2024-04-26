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
package org.openspcoop2.protocol.sdk.properties;

import org.openspcoop2.core.mapping.DBProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;

/**
 * BaseConsoleItem
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BaseConsoleItem {

	private String id; // non modificabile
	private String label;
	private ConsoleItemType type;
		
	protected BaseConsoleItem(String id, String label, ConsoleItemType type) throws ProtocolException{
		if(id==null){
			throw new ProtocolException("Id undefined");
		}
		this.id = id;
		this.setLabel(label);
		this.setType(type);
	}

	public String getId() {
		return this.id;
	}
	
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) throws ProtocolException {
		if(label==null){
			throw new ProtocolException("Label undefined");
		}
		this.label = label;
	}
	
	public boolean isHidden() {
		return this.type!=null && (ConsoleItemType.HIDDEN.equals(this.type) || ConsoleItemType.LOCK_HIDDEN.equals(this.type));
	}
	
	public boolean isLockedType() {
		return this.type!=null && (ConsoleItemType.LOCK.equals(this.type) || ConsoleItemType.LOCK_HIDDEN.equals(this.type));
	}
	public ConsoleItemType getType() {
		return this.type;
	}
	public void setType(ConsoleItemType type) throws ProtocolException {
		if(type==null){
			throw new ProtocolException("Type undefined");
		}
		this.type = type;
		if(this.isLockedType()) {
			DBProtocolPropertiesUtils.addConfidentialProtocolProperty(this.id);
		}
	}
	
}
