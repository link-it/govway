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

package org.openspcoop2.message.soap.wsaddressing;

import java.util.Iterator;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeaderElement;

/**
 * WSAddressingHeader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSAddressingHeader {

	private SOAPHeaderElement to = null; 
	private SOAPHeaderElement from = null; 
	private SOAPHeaderElement action = null; 
	private SOAPHeaderElement id = null; 
	private SOAPHeaderElement relatesTo = null; 
	private SOAPHeaderElement replyTo = null;
	private SOAPHeaderElement faultTo = null; 

	public SOAPHeaderElement getTo() {
		return this.to;
	}
	public String getToValue() {
		if(this.to!=null) {
			return this.to.getValue();
		}
		return null;
	}
	public void setTo(SOAPHeaderElement to) {
		this.to = to;
	}
	
	public SOAPHeaderElement getFrom() {
		return this.from;
	}
	public String getFromValue() {
		if(this.from!=null) {
			return this.getEPRValue(this.from);
		}
		return null;
	}
	public void setFrom(SOAPHeaderElement from) {
		this.from = from;
	}
	
	public SOAPHeaderElement getAction() {
		return this.action;
	}
	public String getActionValue() {
		if(this.action!=null) {
			return this.action.getValue();
		}
		return null;
	}
	public void setAction(SOAPHeaderElement action) {
		this.action = action;
	}
	
	public SOAPHeaderElement getId() {
		return this.id;
	}
	public String getIdValue() {
		if(this.id!=null) {
			return this.id.getValue();
		}
		return null;
	}
	public void setId(SOAPHeaderElement id) {
		this.id = id;
	}
	
	public SOAPHeaderElement getRelatesTo() {
		return this.relatesTo;
	}
	public String getRelatesToValue() {
		if(this.relatesTo!=null) {
			return this.relatesTo.getValue();
		}
		return null;
	}
	public void setRelatesTo(SOAPHeaderElement relatesTo) {
		this.relatesTo = relatesTo;
	}
	
	public SOAPHeaderElement getReplyTo() {
		return this.replyTo;
	}
	public String getReplyToValue() {
		if(this.replyTo!=null) {
			return this.getEPRValue(this.replyTo);
		}
		return null;
	}
	public void setReplyTo(SOAPHeaderElement replyTo) {
		this.replyTo = replyTo;
	}
	
	public SOAPHeaderElement getFaultTo() {
		return this.faultTo;
	}
	public String getFaultToValue() {
		if(this.faultTo!=null) {
			return this.getEPRValue(this.faultTo);
		}
		return null;
	}
	public void setFaultTo(SOAPHeaderElement faultTo) {
		this.faultTo = faultTo;
	}
	
	
	private String getEPRValue(SOAPHeaderElement hdr) {
		if(hdr!=null) {
			Iterator<?> itFROM = hdr.getChildElements();
			while (itFROM.hasNext()) {
				Object o = itFROM.next();
				if(o!=null && (o instanceof SOAPElement) ){
					SOAPElement s = (SOAPElement) o;
					if(Costanti.WSA_SOAP_HEADER_EPR_ADDRESS.equals(s.getLocalName())){
						return s.getValue();
					}
				}
			}
		}
		return null;
	}

}
