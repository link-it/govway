/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.protocol.modipa.constants;

/**
 * ModISignalHubOperation, contiente le possibile operazione per ciascun 
 * segnale depositato
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum ModISignalHubOperation {
	UPDATE("UPDATE"),
	CREATE("CREATE"),
	DELETE("DELETE"),
	SEEDUPDATE("SEEDUPDATE");
	
	private final String value;
	
	private ModISignalHubOperation(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
	
	public static ModISignalHubOperation fromString(String raw) {
		ModISignalHubOperation[] ops = ModISignalHubOperation.values();
		for (ModISignalHubOperation op : ops) {
			if (op.value.equals(raw))
				return op;
		}
		return null;
	}
}
