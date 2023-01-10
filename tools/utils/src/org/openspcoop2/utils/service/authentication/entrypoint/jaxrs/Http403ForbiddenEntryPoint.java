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

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.springframework.security.core.AuthenticationException;

/**
 * Http403ForbiddenEntryPoint
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Http403ForbiddenEntryPoint extends AbstractBasicAuthenticationEntryPoint {

	@Override
	protected void addCustomHeaders(HttpServletResponse httpResponse) {
		// Non voglio che appaia la finestra di Basic
		//httpResponse.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
	}
	
	@Override
	protected Response getPayload(AuthenticationException authException, HttpServletResponse httpResponse) {
		return FaultCode.AUTORIZZAZIONE.toFaultResponse(authException);
	}
}
