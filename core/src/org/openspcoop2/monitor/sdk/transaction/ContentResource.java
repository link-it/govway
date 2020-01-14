/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.sdk.transaction;

import org.openspcoop2.monitor.sdk.constants.MessageType;

/**
 * ContentResource
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentResource extends AbstractContentResource {
		
	public ContentResource(MessageType messageType) {
		super(messageType);
	}
	
	/**
	 * Restituisce il nome della risorsa
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Restituisce il valore della risorsa
	 * 
	 */
	@Override
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value; 
	}
	
	protected String name = "";
	protected String value = "";

}
