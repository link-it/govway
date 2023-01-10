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
package org.openspcoop2.utils;

import java.io.Serializable;

/**
* MapKey
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class MapKey<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private T value;
	private int index;
	
	public MapKey(T value, int index) {
		this.value = value;
		this.index = index;
	}
		
	@Override
	public String toString() {
		return this.value.toString();
	}
	
	public T getValue() {
		return this.value;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	@Override
	public int hashCode() {
		//return this.value.hashCode();
		return this.index;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MapKey<?>) {
			MapKey<?> mObj = (MapKey<?>) obj;
			return mObj.getIndex() == this.index;
		}
		return false;
	}
	
	
}
