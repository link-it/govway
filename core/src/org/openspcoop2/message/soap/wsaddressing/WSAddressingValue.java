/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

/**
 * WSAddressingValue
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSAddressingValue {

	private String to = null;
	private String from = null;
	private String action = null;
	private String id = null;
	private String relatesTo = null;
	private String replyTo = null;
	private String faultTo = null; 
	
	public String getTo() {
		return this.to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getFrom() {
		return this.from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getAction() {
		return this.action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRelatesTo() {
		return this.relatesTo;
	}
	public void setRelatesTo(String relatesTo) {
		this.relatesTo = relatesTo;
	}
	public String getReplyTo() {
		return this.replyTo;
	}
	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}
	public void setReplyToAnonymouys() {
		this.replyTo = Costanti.WSA_SOAP_HEADER_EPR_ADDRESS_ANONYMOUS;
	}
	public String getFaultTo() {
		return this.faultTo;
	}
	public void setFaultTo(String faultTo) {
		this.faultTo = faultTo;
	}
}
