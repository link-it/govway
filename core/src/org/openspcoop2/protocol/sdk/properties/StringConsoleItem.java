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
 * StringConsoleItem
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StringConsoleItem extends AbstractConsoleItem<String> {

	protected StringConsoleItem(String id, String label, ConsoleItemType type) throws ProtocolException {
		super(id, label, type);
	}
	
	private Integer rows;
	public Integer getRows() {
		return this.rows;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	
	@Override
	protected String cloneValue(String value) throws ProtocolException {
		try {
			return new String(value);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
}
