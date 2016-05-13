/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.utils.logger.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.logger.beans.Property;

/**
 * Contenitore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Contenitore{
	
	private List<String> listPrimitive = new ArrayList<String>();
	
	private List<Property> listProperty = new ArrayList<Property>();
		
	private Integer [] arrayPrimitive = new Integer[3];
		
	private Property [] arrayProperty = new Property[3];

	private Map<String, Long> mapPrimitive = new java.util.Hashtable<String,Long>(); 
	
	private Map<String, Property> mapProperty = new java.util.Hashtable<String,Property>();
	
	public List<String> getListPrimitive() {
		return this.listPrimitive;
	}

	public void setListPrimitive(List<String> listPrimitive) {
		this.listPrimitive = listPrimitive;
	}

	public List<Property> getListProperty() {
		return this.listProperty;
	}

	public void setListProperty(List<Property> listProperty) {
		this.listProperty = listProperty;
	}

	public Integer[] getArrayPrimitive() {
		return this.arrayPrimitive;
	}

	public void setArrayPrimitive(Integer[] arrayPrimitive) {
		this.arrayPrimitive = arrayPrimitive;
	}

	public Property[] getArrayProperty() {
		return this.arrayProperty;
	}

	public void setArrayProperty(Property[] arrayProperty) {
		this.arrayProperty = arrayProperty;
	}

	public Map<String, Long> getMapPrimitive() {
		return this.mapPrimitive;
	}

	public void setMapPrimitive(Map<String, Long> mapPrimitive) {
		this.mapPrimitive = mapPrimitive;
	}

	public Map<String, Property> getMapProperty() {
		return this.mapProperty;
	}

	public void setMapProperty(Map<String, Property> mapProperty) {
		this.mapProperty = mapProperty;
	}
}
