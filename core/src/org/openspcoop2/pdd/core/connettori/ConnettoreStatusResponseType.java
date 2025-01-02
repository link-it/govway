/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.connettori;

/**
 * enum contenente tutti i possibili formati di risposta 
 * del connettore Status
 *
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum ConnettoreStatusResponseType {
	XML("xml"),
	JSON("json"),
	VUOTO("vuoto"),
	MODI("ModI"),
	TEXT("text");
	
	private String value;
	private ConnettoreStatusResponseType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public static ConnettoreStatusResponseType fromString(String value) {
		ConnettoreStatusResponseType[] types = ConnettoreStatusResponseType.values();
		for (ConnettoreStatusResponseType type : types)
			if (type.getValue().equals(value))
				return type;
		return null;
	}
}
