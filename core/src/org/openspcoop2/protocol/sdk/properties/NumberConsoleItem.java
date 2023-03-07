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
 * NumberConsoleItem
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NumberConsoleItem extends AbstractConsoleItem<Long> {

	protected NumberConsoleItem(String id, String label, ConsoleItemType type) throws ProtocolException {
		super(id, label, type);
	}

	private long min = -1;
	private long max = Integer.MAX_VALUE;
	
	public long getMin() {
		return this.min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public long getMax() {
		return this.max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	
	@Override
	protected Long cloneValue(Long value) throws ProtocolException {
		try {
			return Long.valueOf(value.longValue()+"");
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
}
