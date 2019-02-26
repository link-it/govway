/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import java.util.ArrayList;
import java.util.List;

/**
 * ProtocolProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolProperties {

	private List<AbstractProperty<?>> list = new ArrayList<AbstractProperty<?>>();
	
	public void addProperty(AbstractProperty<?> p){
		this.list.add(p);
	}
	public void addProperty(String id, String value){
		this.list.add(ProtocolPropertiesFactory.newProperty(id, value));
	}
	public void addProperty(String id, int value){
		this.list.add(ProtocolPropertiesFactory.newProperty(id, value));
	}
	public void addProperty(String id, long value){
		this.list.add(ProtocolPropertiesFactory.newProperty(id, value));
	}
	public void addProperty(String id, byte[] value, String fileName, String fileId){
		this.list.add(ProtocolPropertiesFactory.newProperty(id, value,fileName, fileId));
	}
	
	public AbstractProperty<?> getProperty(int index){
		return this.list.get(index);
	}
	public String getIdProperty(int index){
		return this.list.get(index).getId();
	}
	public Object getValueProperty(int index){
		return this.list.get(index).getValue();
	}
	
	public AbstractProperty<?> removeProperty(int index){
		return this.list.remove(index);
	}
	
	public void clearProperties(){
		this.list.clear();
	}
	
	public int sizeProperties(){
		return this.list.size();
	}
}
