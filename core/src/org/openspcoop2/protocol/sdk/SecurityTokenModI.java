/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.sdk;

import java.io.Serializable;

/**     
 * SecurityTokenModI
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityTokenModI implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private RestMessageSecurityToken authorization;
	private PDNDTokenInfo pdnd;
	private RestMessageSecurityToken integrity;
	private SoapMessageSecurityToken envelope;
	private RestMessageSecurityToken audit;
	
	public RestMessageSecurityToken getAuthorization() {
		return this.authorization;
	}
	protected void setAuthorization(RestMessageSecurityToken authorization) {
		this.authorization = authorization;
	}
	public RestMessageSecurityToken getIntegrity() {
		return this.integrity;
	}
	protected void setIntegrity(RestMessageSecurityToken integrity) {
		this.integrity = integrity;
	}
	public SoapMessageSecurityToken getEnvelope() {
		return this.envelope;
	}
	protected void setEnvelope(SoapMessageSecurityToken envelope) {
		this.envelope = envelope;
	}
	public RestMessageSecurityToken getAudit() {
		return this.audit;
	}
	protected void setAudit(RestMessageSecurityToken audit) {
		this.audit = audit;
	}
	public PDNDTokenInfo getPdnd() {
		return this.pdnd;
	}
	public void setPdnd(PDNDTokenInfo pdnd) {
		this.pdnd = pdnd;
	}

}
