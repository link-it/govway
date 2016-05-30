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
package org.openspcoop2.utils.logger.beans.proxy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.logger.beans.Property;
import org.openspcoop2.utils.logger.constants.proxy.ResultProcessing;

/**
 * AbstractFlow
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractFlow implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date inDate;
	private Date outDate;
	
	private	Long inSize;
	private Long outSize;
	
	private Identifier identifier;
	
	private ResultProcessing resultProcessing;
	
	private String correlationIdentifier;
	
	private List<String> _genericProperties_position = new ArrayList<String>();
	private Map<String,Property> genericProperties = new java.util.Hashtable<String,Property>();
	
	public Date getInDate() {
		return this.inDate;
	}

	public void setInDate(Date inDate) {
		this.inDate = inDate;
	}

	public Date getOutDate() {
		return this.outDate;
	}

	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}

	public Long getInSize() {
		return this.inSize;
	}

	public void setInSize(Long inSize) {
		this.inSize = inSize;
	}

	public Long getOutSize() {
		return this.outSize;
	}

	public void setOutSize(Long outSize) {
		this.outSize = outSize;
	}

	public String getId() {
		if(this.identifier!=null)
			return this.identifier.getId();
		return null;
	}
	
	public Identifier getIdentifier() {
		return this.identifier;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	
	public String getCorrelationIdentifier() {
		return this.correlationIdentifier;
	}

	public void setCorrelationIdentifier(String correlationIdentifier) {
		this.correlationIdentifier = correlationIdentifier;
	}
	
	

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
	
	public ResultProcessing getResultProcessing() {
		return this.resultProcessing;
	}

	public void setResultProcessing(ResultProcessing resultProcessing) {
		this.resultProcessing = resultProcessing;
	}

}
