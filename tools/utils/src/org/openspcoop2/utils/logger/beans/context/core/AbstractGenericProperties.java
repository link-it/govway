/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.logger.beans.context.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.logger.beans.Property;

/**
 * AbstractGenericProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractGenericProperties implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> _genericProperties_position = new ArrayList<String>();
	private Map<String,Property> genericProperties = new java.util.HashMap<String,Property>();

	

	public Map<String,Property> getGenericProperties() {
		return this.genericProperties;
	}
	public List<Property> getGenericPropertiesAsList() {
		List<Property> l = new ArrayList<Property>();
		for (String key : this._genericProperties_position) {
			l.add(this.genericProperties.get(key));
		}
		return l;
	}
	
	public void addGenericProperty(Property property){
		this.genericProperties.put(property.getName(),property);
		this._genericProperties_position.add(property.getName());
	}
	
	public Property getGenericProperty(String key){
		return this.genericProperties.get(key);
	}
	
	public Property removeGenericProperty(String key){
		int index = -1;
		for (int i = 0; i < this._genericProperties_position.size(); i++) {
			if(key.equals(this._genericProperties_position.get(i))){
				index = i;
				break;
			}
		}
		this._genericProperties_position.remove(index);
		return this.genericProperties.remove(key);
	}
	
	public Property getGenericProperty(int index){
		return this.getGenericPropertiesAsList().get(index);
	}
	
	public Property removeGenericProperty(int index){
		Property p = this.getGenericPropertiesAsList().get(index);
		this.genericProperties.remove(p.getName());
		return p;
	}
	
	public int sizeGenericProperties(){
		return this.genericProperties.size();
	}
	
	public void clearGenericProperties(){
		this.genericProperties.clear();
	}

}
