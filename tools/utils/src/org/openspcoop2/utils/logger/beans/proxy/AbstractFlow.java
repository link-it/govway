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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.utils.logger.beans.Property;

/**
 * AbstractFlow
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 11425 $, $Date: 2016-01-26 11:12:54 +0100 (Tue, 26 Jan 2016) $
 */
public abstract class AbstractFlow {

	private Date inDate;
	private Date outDate;
	
	private	Long inSize;
	private Long outSize;
	
	private Identifier identifier;
	
	private String correlationIdentifier;
	
	private List<Property> genericProperties = new ArrayList<Property>();
	
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
	
	

	public List<Property> getGenericProperties() {
		return this.genericProperties;
	}
	
	public void addGenericProperty(Property Property){
		this.genericProperties.add(Property);
	}
	
	public Property getGenericProperty(int index){
		return this.genericProperties.get(index);
	}
	
	public Property removeGenericProperty(int index){
		return this.genericProperties.remove(index);
	}
	
	public int sizeGenericProperties(){
		return this.genericProperties.size();
	}
	
	public void clearGenericProperties(){
		this.genericProperties.clear();
	}

}
