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
/**
 * 
 */
package org.openspcoop2.protocol.as4.pmode.beans;

import org.w3c.dom.Node;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Property {

	private String name;
	private String key;
	private String datatype;
	private Boolean required;
	/**
	 * @param node
	 */
	public Property(Node node) {
		this.name = node.getAttributes().getNamedItem("name").getNodeValue();
		this.key = node.getAttributes().getNamedItem("key").getNodeValue();
		this.datatype = node.getAttributes().getNamedItem("datatype").getNodeValue();
		this.required = Boolean.parseBoolean(node.getAttributes().getNamedItem("required").getNodeValue());
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getDatatype() {
		return this.datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public Boolean getRequired() {
		return this.required;
	}
	public void setRequired(Boolean required) {
		this.required = required;
	}

	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null)
			return false;
		
		if(!(obj instanceof Property))
			return false;
		
		Property p = (Property) obj;
		
		if(!this.name.equals(p.getName()))
			return false;
		
		if(!this.key.equals(p.getKey()))
			return false;
		
		if(!this.datatype.equals(p.getDatatype()))
			return false;
		
		if(!this.required.equals(p.getRequired()))
			return false;
			
		return true;
	}
	
	@Override 
	public String toString(){
		StringBuilder bf = new StringBuilder();
		if(this.name!=null){
			bf.append("name");
			bf.append(":");
			bf.append(this.name);
		}
		if(this.key!=null){
			if(bf.length()>0) {
				bf.append("\n");
			}
			bf.append("key");
			bf.append(":");
			bf.append(this.key);
		}
		if(this.datatype!=null){
			if(bf.length()>0) {
				bf.append("\n");
			}
			bf.append("datatype");
			bf.append(":");
			bf.append(this.datatype);
		}
		if(this.required!=null){
			if(bf.length()>0) {
				bf.append("\n");
			}
			bf.append("required");
			bf.append(":");
			bf.append(this.required);
		}
		return bf.toString();
	}
	
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
}
