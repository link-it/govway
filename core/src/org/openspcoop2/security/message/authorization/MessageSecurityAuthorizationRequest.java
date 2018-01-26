/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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



package org.openspcoop2.security.message.authorization;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * MessageSecurityAuthorizationRequest
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MessageSecurityAuthorizationRequest {

	private String wssSecurityPrincipal;
	private Busta busta;
	private org.openspcoop2.security.message.MessageSecurityContext messageSecurityContext;

	private OpenSPCoop2Message message;
	
	public String getWssSecurityPrincipal() {
		return this.wssSecurityPrincipal;
	}
	public void setWssSecurityPrincipal(String wssSecurityPrincipal) {
		this.wssSecurityPrincipal = wssSecurityPrincipal;
	}
	public Busta getBusta() {
		return this.busta;
	}
	public void setBusta(Busta busta) {
		this.busta = busta;
	}
	public org.openspcoop2.security.message.MessageSecurityContext getMessageSecurityContext() {
		return this.messageSecurityContext;
	}
	public void setMessageSecurityContext(
			org.openspcoop2.security.message.MessageSecurityContext messageSecurityContext) {
		this.messageSecurityContext = messageSecurityContext;
	}
	public OpenSPCoop2Message getMessage() {
		return this.message;
	}
	public void setMessage(OpenSPCoop2Message message) {
		this.message = message;
	}
	
}
