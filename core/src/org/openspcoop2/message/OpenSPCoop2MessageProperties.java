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

package org.openspcoop2.message;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.transport.TransportUtils;

/**
 * OpenSPCoop2MessageProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2MessageProperties {

	private Map<String, List<String>> props = new HashMap<String, List<String>>();	

	private boolean initialize = false;
	
	public boolean isInitialize() {
		return this.initialize;
	}

	public void setInitialize(boolean initialize) {
		this.initialize = initialize;
	}

	public void addProperty(String key,String value){
		TransportUtils.put(this.props, key, value, true);
	}
	public void setProperty(String key,List<String> values){
		this.props.put(key, values);
	}
	
	@Deprecated
	public String removeProperty(String key){
		Object o = TransportUtils.removeObjectAsString(this.props, key);
		return (o!=null && o instanceof String) ? ((String)o) : null;
	}
	public List<String> removePropertyValues(String key){
		return TransportUtils.removeRawObject(this.props, key);
	}
	
	public Iterator<String> getKeys(){
		return this.props.keySet().iterator();
	}
	
	public boolean containsKey(String key){
		return TransportUtils.containsKey(this.props, key);
	}
	
	@Deprecated
	public String getProperty(String key){
		return TransportUtils.getObjectAsString(this.props, key);
	}
	public List<String> getPropertyValues(String key){
		return TransportUtils.getRawObject(this.props, key);
	}
	
	@Deprecated
	public Map<String, String> getAsMap(){
		return TransportUtils.convertToMapSingleValue(this.props);
	}
	public Map<String, List<String>> map(){
		return this.props;
	}
	
	public int size(){
		return this.props.size();
	}
}
