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
import java.net.HttpCookie;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.transport.TransportUtils;
import org.slf4j.Logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
* CookieSameSiteFilter
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class CookieSameSiteFilter implements jakarta.servlet.Filter {

	private boolean enabled = false;
	private String strictValue = "Strict";
	private Logger log;
	
	private static final String SAME_SITE_CONFIG_ENABLED = "sameSite.enabled";
	private static final String SAME_SITE_CONFIG_VAUE = "sameSite.value";
	private static final String SAME_SITE_CONFIG_LOG = "sameSite.logCategory";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if(filterConfig!=null) {
			String tmp = filterConfig.getInitParameter(SAME_SITE_CONFIG_ENABLED);
			if(tmp!=null && "true".equalsIgnoreCase(tmp.trim())) {
				this.enabled = true;
			}
			
			tmp = filterConfig.getInitParameter(SAME_SITE_CONFIG_VAUE);
			if(tmp!=null) {
				this.strictValue = tmp.trim();
			}
			
			tmp = filterConfig.getInitParameter(SAME_SITE_CONFIG_LOG);
			if(tmp!=null) {
				this.log = LoggerWrapperFactory.getLogger(tmp.trim());
			}
			else {
				this.log = LoggerWrapperFactory.getLogger(CookieSameSiteFilter.class);
			}
		}
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

	    if (!this.enabled) {
	    	chain.doFilter(request, response);
	    	return;
	    }

		chain.doFilter(request, new CookieSameSiteResponseProxy((HttpServletResponse)response, this.log, this.strictValue));
		
	}
	
}

class CookieSameSiteResponseProxy extends HttpServletResponseWrapper{

	private final HttpServletResponse response;
	private Logger log;
	private String strictValue;

	public CookieSameSiteResponseProxy(final HttpServletResponse resp, Logger log, String strictValue) {
		super(resp);
		this.response = resp;
		this.log = log;
		this.strictValue = strictValue;
	}

	@Override
	public void sendError(final int sc) throws IOException {
		processSameSiteCookie();
		super.sendError(sc);
	}
	@Override
	public void sendError(final int sc, final String msg) throws IOException {
		processSameSiteCookie();
		super.sendError(sc, msg);
	}
	
	@Override
	public void sendRedirect(final String location) throws IOException {
		processSameSiteCookie();
		super.sendRedirect(location);
	}


	@Override
	public PrintWriter getWriter() throws IOException {
		processSameSiteCookie();
		return super.getWriter();
	}
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		processSameSiteCookie();
		return super.getOutputStream();
	}

	private void processSameSiteCookie() {

		List<String> headers = TransportUtils.getHeaderValues(this.response, HttpConstants.SET_COOKIE);
		if(headers==null || headers.isEmpty()) {
			return;
		}
		
		boolean firstHeader = true;
		for (final String hdr : headers) {

			if(hdr==null || StringUtils.isEmpty(hdr.trim())) {
				continue;
			}
			
			try {
				//this parser only parses name and value, we only need the name.
				List<HttpCookie> parsedCookies = HttpCookie.parse(hdr);

				if (parsedCookies != null && !parsedCookies.isEmpty()) {
					set(hdr, firstHeader);
				}
	
				firstHeader=false;
				
			} catch(Exception e) {
				// Should not get here
				this.log.trace("Process cookie header '"+hdr+"' failed: "+e.getMessage(), e);
			}

		}
	}
	private void set(String hdr, boolean firstHeader) {
		String newValue = hdr;
		if (!hdr.contains(HttpConstants.SET_COOKIE_SAME_SITE_PARAMETER)) {
			newValue = String.format("%s; %s", hdr,
					HttpConstants.SET_COOKIE_SAME_SITE_PARAMETER + "=" + this.strictValue);                
		} 
		
		if (firstHeader) {                
			this.response.setHeader(HttpConstants.SET_COOKIE, newValue);
		} else {
			this.response.addHeader(HttpConstants.SET_COOKIE, newValue);
		}
	}

}