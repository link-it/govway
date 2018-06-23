/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.utils;

/**
* EsitoIdentificationModeSoapFault
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class EsitoIdentificationModeSoapFault {

	private String faultCode;
	private String faultNamespaceCode;
	private String faultReason;
	private Boolean faultReasonContains;
	private String faultActor;
	private Boolean faultActorNotDefined;
	
	public String getFaultReason() {
		return this.faultReason;
	}
	public void setFaultReason(String faultReason) {
		this.faultReason = faultReason;
	}
	public Boolean getFaultReasonContains() {
		return this.faultReasonContains;
	}
	public void setFaultReasonContains(Boolean faultReasonContains) {
		this.faultReasonContains = faultReasonContains;
	}
	public Boolean getFaultActorNotDefined() {
		return this.faultActorNotDefined;
	}
	public void setFaultActorNotDefined(Boolean faultActorNotDefined) {
		this.faultActorNotDefined = faultActorNotDefined;
	}
	
	public String getFaultNamespaceCode() {
		return this.faultNamespaceCode;
	}
	public void setFaultNamespaceCode(String faultNamespaceCode) {
		this.faultNamespaceCode = faultNamespaceCode;
	}
	public String getFaultCode() {
		return this.faultCode;
	}
	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}
	public String getFaultActor() {
		return this.faultActor;
	}
	public void setFaultActor(String faultActor) {
		this.faultActor = faultActor;
	}
}
