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

/**     
 * SecurityToken
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityToken {

	private ChannelSecurityToken channel;
	private RestMessageSecurityToken authorization;
	private RestMessageSecurityToken integrity;
	private SoapMessageSecurityToken envelope;
	
	public ChannelSecurityToken getChannel() {
		return this.channel;
	}
	public void setChannel(ChannelSecurityToken channel) {
		this.channel = channel;
	}
	public RestMessageSecurityToken getAuthorization() {
		return this.authorization;
	}
	public void setAuthorization(RestMessageSecurityToken authorization) {
		this.authorization = authorization;
	}
	public RestMessageSecurityToken getIntegrity() {
		return this.integrity;
	}
	public void setIntegrity(RestMessageSecurityToken integrity) {
		this.integrity = integrity;
	}
	public SoapMessageSecurityToken getEnvelope() {
		return this.envelope;
	}
	public void setEnvelope(SoapMessageSecurityToken envelope) {
		this.envelope = envelope;
	}
}
