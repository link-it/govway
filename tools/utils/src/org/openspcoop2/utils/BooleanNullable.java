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
package org.openspcoop2.utils;

/**
 * BooleanNullable
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BooleanNullable {

	public static BooleanNullable NULL() {
		return new BooleanNullable(null);
	}
	public static BooleanNullable TRUE() {
		return new BooleanNullable(Boolean.TRUE);
	}
	public static BooleanNullable FALSE() {
		return new BooleanNullable(Boolean.FALSE);
	}
	
	private Boolean value = null;

	public BooleanNullable(Boolean value) {
		this.value = value;
	}

	public Boolean getValue() {
		return this.value;
	}
	public void setValue(Boolean value) {
		this.value = value;
	}
}
