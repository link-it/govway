/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
package org.openspcoop2.utils.service.authentication.handler.jaxrs;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.openspcoop2.utils.service.authentication.entrypoint.jaxrs.AbstractBasicAuthenticationEntryPoint;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * DefaultAuthenticationFailureHandler
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse res, AuthenticationException exception) throws IOException, ServletException {
		AbstractBasicAuthenticationEntryPoint.fillResponse(res, getPayload(exception, res));
	}

	public Response getPayload(AuthenticationException authException, HttpServletResponse httpResponse) {
		return FaultCode.AUTORIZZAZIONE.toFaultResponse(authException);
	}
}
