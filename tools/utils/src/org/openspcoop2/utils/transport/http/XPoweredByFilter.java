/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.transport.http;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.lang.StringUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
* XPoweredByFilter
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class XPoweredByFilter implements Filter {
	
	private boolean enabled = false;
	private String value = null;
	
	private static final String XPOWEREDBY_CONFIG_ENABLED = "xPoweredBy.enabled";
	private static final String XPOWEREDBY_CONFIG_VAUE = "xPoweredBy.value"; // impostare a string vuota o non impostarlo per non far generare l'header
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if(filterConfig!=null) {
			String tmp = filterConfig.getInitParameter(XPOWEREDBY_CONFIG_ENABLED);
			if(tmp!=null && "true".equalsIgnoreCase(tmp.trim())) {
				this.enabled = true;
			}
			
			tmp = filterConfig.getInitParameter(XPOWEREDBY_CONFIG_VAUE);
			if(tmp!=null) {
				this.value = tmp.trim();
				if(StringUtils.isEmpty(this.value)) {
					this.value=null;
				}
			}
		}
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

	    if (!this.enabled) {
	    	chain.doFilter(request, response);
	    	return;
	    }

        chain.doFilter(request, new XPoweredByResponseProxy((HttpServletResponse)response, this.value));
    }

}

class XPoweredByResponseProxy extends HttpServletResponseWrapper{

	private final HttpServletResponse response;
	private String value;

	public XPoweredByResponseProxy(final HttpServletResponse resp, String value) {
		super(resp);
		this.response = resp;
		this.value = value;
	}

	@Override
	public void sendError(final int sc) throws IOException {
		setXPoweredBy();
		super.sendError(sc);
	}
	@Override
	public void sendError(final int sc, final String msg) throws IOException {
		setXPoweredBy();
		super.sendError(sc, msg);
	}
	
	@Override
	public void sendRedirect(final String location) throws IOException {
		setXPoweredBy();
		super.sendRedirect(location);
	}


	@Override
	public PrintWriter getWriter() throws IOException {
		setXPoweredBy();
		return super.getWriter();
	}
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		setXPoweredBy();
		return super.getOutputStream();
	}

	private void setXPoweredBy() {

		this.response.setHeader(HttpConstants.X_POWERED_BY, this.value);
		
	}

}
