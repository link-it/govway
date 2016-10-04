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
