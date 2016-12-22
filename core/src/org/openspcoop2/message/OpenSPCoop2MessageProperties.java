/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.message;

import java.util.Enumeration;
import java.util.Properties;

/**
 * OpenSPCoop2MessageProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2MessageProperties {

	private Properties props = new Properties();	

	private boolean initialize = false;
	
	public boolean isInitialize() {
		return this.initialize;
	}

	public void setInitialize(boolean initialize) {
		this.initialize = initialize;
	}

	public void addProperty(String key,String value){
		this.props.setProperty(key, value);
	}
	
	public String removeProperty(String key){
		return (String) this.props.remove(key);
	}
	
	public Enumeration<?> getKeys(){
		return this.props.keys();
	}
	
	public String getProperty(String key){
		return this.props.getProperty(key);
	}
	
	public Properties getAsProperties(){
		return this.props;
	}
	
	public int size(){
		return this.props.size();
	}
}
