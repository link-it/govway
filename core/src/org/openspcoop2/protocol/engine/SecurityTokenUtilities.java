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

package org.openspcoop2.protocol.engine;

import org.openspcoop2.protocol.sdk.ChannelSecurityToken;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.state.RequestInfo;

/**     
 * SecurityTokenUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityTokenUtilities {
	
	private SecurityTokenUtilities() {}

	public static SecurityToken readSecurityToken(Context context) {
		SecurityToken securityTokenForContext = null;
		if(context.containsKey(org.openspcoop2.core.constants.Costanti.SECURITY_TOKEN)) {
			securityTokenForContext = (SecurityToken) context.getObject(org.openspcoop2.core.constants.Costanti.SECURITY_TOKEN);
		}
		return securityTokenForContext;
	}
	public static SecurityToken newSecurityToken(Context context) {
		SecurityToken securityTokenForContext = null;
		if(context.containsKey(org.openspcoop2.core.constants.Costanti.SECURITY_TOKEN)) {
			securityTokenForContext = (SecurityToken) context.getObject(org.openspcoop2.core.constants.Costanti.SECURITY_TOKEN);
		}
		else {
			securityTokenForContext = new SecurityToken();
			context.addObject(org.openspcoop2.core.constants.Costanti.SECURITY_TOKEN, securityTokenForContext);
			
			if(context.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
				RequestInfo requestInfo = (RequestInfo) context.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
				if(requestInfo!=null && requestInfo.getProtocolContext()!=null && requestInfo.getProtocolContext().getCredential()!=null && 
						requestInfo.getProtocolContext().getCredential().getCertificate()!=null &&
						requestInfo.getProtocolContext().getCredential().getCertificate().getCertificate()!=null) {
					ChannelSecurityToken channelSecurityToken = new ChannelSecurityToken();
					channelSecurityToken.setCertificate(requestInfo.getProtocolContext().getCredential().getCertificate().getCertificate());
					securityTokenForContext.setChannel(channelSecurityToken);
				}
			}
		}
		
		return securityTokenForContext;
	}
	
}
