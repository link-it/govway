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

package org.openspcoop2.web.lib.mvc;

/**
 * MessageType
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum MessageType {

	INFO("info"),
	INFO_SINTETICO("info-sintetico"),
	CONFIRM("confirm"),
	WARN("warn"),
	WARN_SINTETICO("warn-sintetico"),
	ERROR("errors"),
	ERROR_SINTETICO("errors-sintetico");
	
	private String value;
	MessageType(String value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
	
	public static MessageType fromValue(String mT) {
		for (MessageType message : values()) {
			if(message.toString().equals(mT))
				return message;
		}
		
		return null;
	}
	
}
