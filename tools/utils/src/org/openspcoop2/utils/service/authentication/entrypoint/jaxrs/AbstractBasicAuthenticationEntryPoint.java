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

package org.openspcoop2.utils.service.authentication.entrypoint.jaxrs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.openspcoop2.utils.Costanti;
import org.openspcoop2.utils.jaxrs.JacksonJsonProviderCustomized;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

/**	
 * Problem
 *
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
	
	private String realname = Costanti.OPENSPCOOP2;
	
	public String getRealname() {
		return this.realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	
	private TimeZone timeZone = TimeZone.getDefault();
	private String timeZoneId = null;
	public String getTimeZoneId() {
		return this.timeZoneId;
	}
	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
		this.timeZone = TimeZone.getTimeZone(timeZoneId);
	}
	
	public void fillResponse(AuthenticationException authException, HttpServletResponse httpResponse) {
		AbstractBasicAuthenticationEntryPoint.fillResponse(httpResponse, getPayload(authException, httpResponse), this.timeZone);
	}
	
	public static void fillResponse(HttpServletResponse httpResponse, Response response, TimeZone timeZone) {
		ByteArrayInputStream bais = null;
		ServletOutputStream outputStream = null;
		try{
			httpResponse.setStatus(response.getStatus());

			MultivaluedMap<String, Object> headers = response.getHeaders();
			if(!headers.isEmpty()) {
				Set<String> keySet = headers.keySet();

				for (String headerKey : keySet) {
					List<Object> list = headers.get(headerKey);
					if(!list.isEmpty()) {
						StringBuilder sb = new StringBuilder();
						for (Object object : list) {
							if(sb.length() > 0)
								sb.append(", ");

							sb.append(object);
						}
						httpResponse.setHeader(headerKey, sb.toString());
					}
				}
			}

			ObjectMapper mapper = JacksonJsonProviderCustomized.getObjectMapper(false, timeZone);
			String fault = mapper.writeValueAsString(response.getEntity());
			bais = new ByteArrayInputStream(fault.getBytes());

			outputStream = httpResponse.getOutputStream();

			IOUtils.copy(bais, outputStream);

			outputStream.flush();
		}catch(Exception e) {

		} finally {
			if(bais!= null) {
				try {
					bais.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	protected abstract Response getPayload(AuthenticationException authException, HttpServletResponse httpResponse);

	protected abstract void addCustomHeaders(javax.servlet.http.HttpServletResponse httpResponse);
	
	@Override
	public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException)  {
		this.addCustomHeaders(response);
		this.fillResponse(authException, response);
	}

	@Override
	public void afterPropertiesSet() {
		setRealmName(this.realname);
		super.afterPropertiesSet();
	}
}