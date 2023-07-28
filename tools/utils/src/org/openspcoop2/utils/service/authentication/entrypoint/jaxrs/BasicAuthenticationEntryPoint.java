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

package org.openspcoop2.utils.service.authentication.entrypoint.jaxrs;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;

import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.springframework.security.core.AuthenticationException;

/**
 * BasicAuthenticationEntryPoint
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicAuthenticationEntryPoint extends AbstractBasicAuthenticationEntryPoint {

	private boolean wwwAuthenticate = true;
	
	public boolean isWwwAuthenticate() {
		return this.wwwAuthenticate;
	}
	public void setWwwAuthenticate(boolean wwwAuthenticate) {
		this.wwwAuthenticate = wwwAuthenticate;
	}

	@Override
	protected void addCustomHeaders(HttpServletResponse httpResponse) {
		if(this.wwwAuthenticate) {
			httpResponse.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
		}
	}
	
	@Override
	protected Response getPayload(AuthenticationException authException, HttpServletResponse httpResponse) {
		return FaultCode.AUTENTICAZIONE.toFaultResponse(authException);
	}
}
