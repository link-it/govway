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
package org.openspcoop2.core.byok;

/**
 * BYOKWrappedValue
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKWrappedValue {
	
	private String wrappedValue;
	private String wrappedPlainValue;
	
	public BYOKWrappedValue(byte[] wrappedValue, String wrappedPlainValue) {
		this.wrappedValue = new String(wrappedValue);
		this.wrappedPlainValue = wrappedPlainValue;
	}
	public BYOKWrappedValue(String wrappedValue, String wrappedPlainValue) {
		this.wrappedValue = wrappedValue;
		this.wrappedPlainValue = wrappedPlainValue;
	}

	public String getWrappedValue() {
		return this.wrappedValue;
	}
	public void setWrappedValue(String wrappedValue) {
		this.wrappedValue = wrappedValue;
	}

	public String getWrappedPlainValue() {
		return this.wrappedPlainValue;
	}
	public void setWrappedPlainValue(String wrappedPlainValue) {
		this.wrappedPlainValue = wrappedPlainValue;
	}
}
