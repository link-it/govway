/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
 * SecurityToken
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ChannelSecurityToken channel;
	private RestMessageSecurityToken accessToken;
	private SecurityTokenModI modI;
	
	public ChannelSecurityToken getChannel() {
		return this.channel;
	}
	public void setChannel(ChannelSecurityToken channel) {
		this.channel = channel;
	}
	public RestMessageSecurityToken getAccessToken() {
		return this.accessToken;
	}
	public void setAccessToken(RestMessageSecurityToken accessToken) {
		this.accessToken = accessToken;
	}
	
	public SecurityTokenModI getModI() {
		return this.modI;
	}
	public SecurityTokenModI getModi() {
		return this.modI;
	}
	public void setModI(SecurityTokenModI modI) {
		this.modI = modI;
	}
	
	// -- shortcut
	public RestMessageSecurityToken getAuthorization() {
		return this.modI!=null ? this.modI.getAuthorization() : null;
	}
	public void setAuthorization(RestMessageSecurityToken authorization) {
		if(this.modI==null) {
			this.modI = new SecurityTokenModI();
		}
		this.modI.setAuthorization(authorization);
	}
	public RestMessageSecurityToken getIntegrity() {
		return this.modI!=null ? this.modI.getIntegrity() : null;
	}
	public void setIntegrity(RestMessageSecurityToken integrity) {
		if(this.modI==null) {
			this.modI = new SecurityTokenModI();
		}
		this.modI.setIntegrity(integrity);
	}
	public SoapMessageSecurityToken getEnvelope() {
		return this.modI!=null ? this.modI.getEnvelope() : null;
	}
	public void setEnvelope(SoapMessageSecurityToken envelope) {
		if(this.modI==null) {
			this.modI = new SecurityTokenModI();
		}
		this.modI.setEnvelope(envelope);
	}

}
