/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

	private Map<String, String> props = new HashMap<String, String>();	

	private boolean initialize = false;
	
	public boolean isInitialize() {
		return this.initialize;
	}

	public void setInitialize(boolean initialize) {
		this.initialize = initialize;
	}

	public void addProperty(String key,String value){
		this.props.put(key, value);
	}
	
	public String removeProperty(String key){
		Object o = TransportUtils.remove(this.props, key);
		return (o!=null && o instanceof String) ? ((String)o) : null;
	}
	
	public Iterator<String> getKeys(){
		return this.props.keySet().iterator();
	}
	
	public String getProperty(String key){
		return TransportUtils.get(this.props, key);
	}
	
	public Map<String, String> getAsMap(){
		return this.props;
	}
	
	public int size(){
		return this.props.size();
	}
}
