/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.util.TimeZone;

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

	private TimeZone timeZone = TimeZone.getDefault();
	private String timeZoneId = null;
	public String getTimeZoneId() {
		return this.timeZoneId;
	}
	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
		this.timeZone = TimeZone.getTimeZone(timeZoneId);
	}
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse res, AuthenticationException exception) throws IOException, ServletException {
		AbstractBasicAuthenticationEntryPoint.fillResponse(res, getPayload(exception, res), this.timeZone);
	}

	public Response getPayload(AuthenticationException authException, HttpServletResponse httpResponse) {
		return FaultCode.AUTORIZZAZIONE.toFaultResponse(authException);
	}
}
