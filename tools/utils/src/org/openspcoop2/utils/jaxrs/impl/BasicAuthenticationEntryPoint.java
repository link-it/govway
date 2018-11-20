/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

package org.openspcoop2.utils.jaxrs.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.openspcoop2.utils.Costanti;
import org.openspcoop2.utils.jaxrs.JacksonJsonProvider;
import org.openspcoop2.utils.jaxrs.fault.FaultCode;
import org.springframework.security.core.AuthenticationException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * BasicAuthenticationEntryPoint
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicAuthenticationEntryPoint extends org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint {

	private String realname = Costanti.OPENSPCOOP2;
	
	public String getRealname() {
		return this.realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}

	@Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws IOException, ServletException {
        response.addHeader("WWW-Authenticate", "Basic realm=\"" + this.realname + "\"");
        this.fillResponse(authException, response);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName(this.realname);
        super.afterPropertiesSet();
    }
	
	private void fillResponse(AuthenticationException authException, HttpServletResponse httpResponse) {
		ByteArrayInputStream bais = null;
		ServletOutputStream outputStream = null;
		try{
			Response response = FaultCode.AUTENTICAZIONE.toFaultResponse(authException);
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

			ObjectMapper mapper = JacksonJsonProvider.getObjectMapper();
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

	
}
