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

package org.openspcoop2.web.lib.mvc.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.security.exception.ValidationException;
import org.slf4j.Logger;

/**
 * SecurityWrappedHttpServletRequest
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityWrappedHttpServletRequest extends HttpServletRequestWrapper implements HttpServletRequest {
	
	private Logger log;
	private SecurityProperties sc;
	private Validatore validator;

	private Integer headerNameMaxLength = 256;
	private Integer queryParamNameMaxLength = 256;

	public SecurityWrappedHttpServletRequest(HttpServletRequest httpServletRequest, Logger log) {
		super(httpServletRequest);
		this.log = log;
		this.sc = SecurityProperties.getInstance();
		this.validator = Validatore.getInstance();

		try {
			this.headerNameMaxLength = this.sc.getIntProp(Costanti.REQUEST_HEADER_NAME_MAX_LENGTH);
			this.queryParamNameMaxLength = this.sc.getIntProp(Costanti.REQUEST_QUERY_PARAM_NAME_MAX_LENGTH);
		} catch (UtilsException e) {
			this.log.error("Errore durante la lettura delle properties: " + e.getMessage(),e);
		}
	}
	
    private HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest)super.getRequest();
    }

	@Override
	public int getContentLength() {
		return this.getHttpServletRequest().getContentLength();
	}
	
	@Override
	public String getContentType() {
		return this.getHttpServletRequest().getContentType();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return this.getHttpServletRequest().getInputStream();
	}
	
	@Override
	public BufferedReader getReader() throws IOException {
		return this.getHttpServletRequest().getReader();
	}
	
	public String getOriginalParameter(String key) {
		return this.getHttpServletRequest().getParameter(key);
	}
	
	@Override
	public String getParameter(String key) {
		String val = this.getHttpServletRequest().getParameter(key);
		if(val != null) {
			try {
				boolean usaValidazioneTextArea = ServletUtils.usaValidazioneTextArea(getHttpServletRequest(), key);
				String pattern = usaValidazioneTextArea ? Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE_TEXT_AREA : Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE;
				return this.validator.validate("Il valore del parametro [" + key + "]:["+val+"]", val, null, true, pattern);
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: "+ e.getMessage(),e);
				return "";
			}
		}
		return null;
	}

	@Override
	public Map<java.lang.String,java.lang.String[]> getParameterMap() {
		Map<String,String[]> map = this.getHttpServletRequest().getParameterMap();
		Map<String,String[]> cleanMap = new HashMap<>();
		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			try {
				String name = (String) entry.getKey();

				boolean usaValidazioneTextArea = ServletUtils.usaValidazioneTextArea(getHttpServletRequest(), name);
				String pattern = usaValidazioneTextArea ? Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE_TEXT_AREA : Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE;
				
				String cleanName = this.validator.validate("Il nome del parametro [" + name + "]", name, this.queryParamNameMaxLength, true, Costanti.PATTERN_REQUEST_HTTP_PARAMETER_NAME);
				
				String[] value = (String[]) entry.getValue();
				String[] cleanValues = new String[value.length];
				for (int j = 0; j < value.length; j++) {
					String cleanValue = this.validator.validate("Il valore del parametro [" + name + "]:["+value[j]+"]", value[j], null, true, pattern);
					cleanValues[j] = cleanValue;
				}
				cleanMap.put(cleanName, cleanValues);
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: "+ e.getMessage(),e);
			}
		}
		return cleanMap;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		List<String> v = new ArrayList<>();
		Enumeration<String> en = this.getHttpServletRequest().getParameterNames();
		while (en.hasMoreElements()) {
			try {

				String name = (String) en.nextElement();
				String clean = this.validator.validate("Il nome del parametro [" + name + "]", name, this.queryParamNameMaxLength, true, Costanti.PATTERN_REQUEST_HTTP_PARAMETER_NAME);
				v.add(clean);
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: "+ e.getMessage(),e);
			}
		}
		return Collections.enumeration(v);
	}

	@Override
	public String[] getParameterValues(String arg0) {
		String[] values = this.getHttpServletRequest().getParameterValues(arg0);
		List<String> newValues;

		if(values == null)
			return null;
		newValues = new ArrayList<>();
		
		boolean usaValidazioneTextArea = ServletUtils.usaValidazioneTextArea(getHttpServletRequest(), arg0);
		String pattern = usaValidazioneTextArea ? Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE_TEXT_AREA : Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE;

		for (String value : values) {
			try {
				newValues.add(this.validator.validate("Il valore del parametro [" + arg0 + "]:["+value+"]", value, null, true, pattern));
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: "+ e.getMessage(),e);
			}
		}
		return newValues.toArray(new String[newValues.size()]);
	}

	@Override
	public String getRequestURI() {
		return this.getHttpServletRequest().getRequestURI();
	}
		
	@Override
	public String getContextPath() {
		return this.getHttpServletRequest().getContextPath();
	}
	
	@Override
	public String getPathInfo() {
		return this.getHttpServletRequest().getPathInfo();
	}
	
	@Override
	public String getPathTranslated() {
		return this.getHttpServletRequest().getPathTranslated();
	}

	@Override
	public String getQueryString() {
		return this.getHttpServletRequest().getQueryString();
	}

	@Override
	public StringBuffer getRequestURL() {
		return this.getHttpServletRequest().getRequestURL();
	}

	@Override
	public String getServletPath() {
		return this.getHttpServletRequest().getServletPath();
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public String getRealPath(String arg0) {
		return this.getHttpServletRequest().getRealPath(arg0);
	}
	
	@Override
	public Object getAttribute(String arg0) {
		return this.getHttpServletRequest().getAttribute(arg0);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return this.getHttpServletRequest().getAttributeNames();
	}

	@Override
	public String getCharacterEncoding() {
		return this.getHttpServletRequest().getCharacterEncoding();
	}

	@Override
	public String getLocalAddr() {
		return this.getHttpServletRequest().getLocalAddr();
	}

	@Override
	public String getLocalName() {
		return this.getHttpServletRequest().getLocalName();
	}

	@Override
	public int getLocalPort() {
		return this.getHttpServletRequest().getLocalPort();
	}

	@Override
	public Locale getLocale() {
		return this.getHttpServletRequest().getLocale();
	}

	@Override
	public Enumeration<java.util.Locale> getLocales() {
		return this.getHttpServletRequest().getLocales();
	}

	@Override
	public String getProtocol() {
		return this.getHttpServletRequest().getProtocol();
	}

	@Override
	public String getRemoteAddr() {
		return this.getHttpServletRequest().getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return this.getHttpServletRequest().getRemoteHost();
	}

	@Override
	public int getRemotePort() {
		return this.getHttpServletRequest().getRemotePort();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return this.getHttpServletRequest().getRequestDispatcher(arg0);
	}

	@Override
	public String getScheme() {
		return this.getHttpServletRequest().getScheme();
	}

	@Override
	public String getServerName() {
		return this.getHttpServletRequest().getServerName();
	}

	@Override
	public int getServerPort() {
		return this.getHttpServletRequest().getServerPort();
	}

	@Override
	public boolean isSecure() {
		return this.getHttpServletRequest().isSecure();
	}

	@Override
	public void removeAttribute(String arg0) {
		this.getHttpServletRequest().removeAttribute(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		this.getHttpServletRequest().setAttribute(arg0,arg1);
	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		this.getHttpServletRequest().setCharacterEncoding(arg0);
	}

	@Override
	public String getAuthType() {
		return this.getHttpServletRequest().getAuthType();
	}

	@Override
	public Cookie[] getCookies() {
		Cookie[] cookies = this.getHttpServletRequest().getCookies();
        if (cookies == null) return new Cookie[0];
        
        List<Cookie> newCookies = new ArrayList<>();
        for (Cookie c : cookies) {
            // build a new clean cookie
            try {
                // get data from original cookie
                String name = this.validator.validate("Il nome del Cookie [" + c.getName()+ "]", c.getName(), this.headerNameMaxLength, false, Costanti.PATTERN_HTTP_COOKIE_NAME);
                String value = this.validator.validate("Il valore del Cookie [" + c.getName()+ "]:["+c.getValue()+"]", c.getValue(), null, true, Costanti.PATTERN_HTTP_COOKIE_VALUE);
                int maxAge = c.getMaxAge();
                String domain = c.getDomain();
                String path = c.getPath();

                Cookie n = new Cookie(name, value);
                n.setMaxAge(maxAge);

                if (domain != null) {
                    n.setDomain(this.validator.validate("Il domain del Cookie [" + c.getName()+ "]:["+domain+"]", domain, null, false, Costanti.PATTERN_RESPONSE_HTTP_HEADER_VALUE));
                }
                if (path != null) {
                    n.setPath(this.validator.validate("Il path del Cookie [" + c.getName()+ "]:["+path+"]", path, null, false, Costanti.PATTERN_RESPONSE_HTTP_HEADER_VALUE));
                }
                newCookies.add(n);
            } catch (ValidationException e) {
                this.log.warn("Ignoro cookie malformato: " + c.getName() + "=" + c.getValue(), e );
            }
        }
        return newCookies.toArray(new Cookie[newCookies.size()]);
	}

	@Override
	public long getDateHeader(String arg0) {
		return this.getHttpServletRequest().getDateHeader(arg0);
	}

	@Override
	public String getHeader(String name) {
		String value = this.getHttpServletRequest().getHeader(name);

		if(value != null) {
			try {
				return this.validator.validate("Il valore dell'Header [" + name+ "]:["+value+"]", value, null, true, Costanti.PATTERN_REQUEST_HTTP_HEADER_VALUE);
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: "+ e.getMessage(),e);
				return "";
			}
		}
		return null;
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		List<String> v = new ArrayList<>();
		Enumeration<String> en = this.getHttpServletRequest().getHeaderNames();

		while (en.hasMoreElements()) {
			try {
				String name = en.nextElement();
				String validValue = this.validator.validate("Il nome dell'Header [" + name+ "]", name, null, true, Costanti.PATTERN_REQUEST_HTTP_HEADER_NAME);
				v.add(validValue);
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: "+ e.getMessage(),e);
			}
		}
		return Collections.enumeration(v);
	}

	@Override
	public Enumeration<String> getHeaders(String arg0) {
		List<String> v = new ArrayList<>();
		Enumeration<String> en = this.getHttpServletRequest().getHeaders(arg0);

		while (en.hasMoreElements()) {
			try {
				String value = (String) en.nextElement();
				v.add(this.validator.validate("Il valore dell'Header [" + arg0+ "]:["+value+"]", value, null, true, Costanti.PATTERN_REQUEST_HTTP_HEADER_VALUE));
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: "+ e.getMessage(),e);
			}
		}
		return Collections.enumeration(v);
	}

	@Override
	public int getIntHeader(String arg0) {
		return this.getHttpServletRequest().getIntHeader(arg0);
	}

	@Override
	public String getMethod() {
		return this.getHttpServletRequest().getMethod();
	}

	@Override
	public String getRemoteUser() {
		return this.getHttpServletRequest().getRemoteUser();
	}

	@Override
	public String getRequestedSessionId() {
		return this.getHttpServletRequest().getRequestedSessionId();
	}

	@Override
	public HttpSession getSession() {
		return this.getHttpServletRequest().getSession();
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		return this.getHttpServletRequest().getSession(arg0);
	}

	@Override
	public Principal getUserPrincipal() {
		return this.getHttpServletRequest().getUserPrincipal();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return this.getHttpServletRequest().isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return this.getHttpServletRequest().isRequestedSessionIdFromURL();
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public boolean isRequestedSessionIdFromUrl() {
		return this.getHttpServletRequest().isRequestedSessionIdFromUrl();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return this.getHttpServletRequest().isRequestedSessionIdValid();
	}

	@Override
	public boolean isUserInRole(String arg0) {
		return this.getHttpServletRequest().isUserInRole(arg0);
	}

	// v3
	
	@Override
	public AsyncContext getAsyncContext() {
		return this.getHttpServletRequest().getAsyncContext();
	}

	@Override
	public long getContentLengthLong() {
		return this.getHttpServletRequest().getContentLengthLong();
	}

	@Override
	public DispatcherType getDispatcherType() {
		return this.getHttpServletRequest().getDispatcherType();
	}

	@Override
	public ServletContext getServletContext() {
		return this.getHttpServletRequest().getServletContext();
	}

	@Override
	public boolean isAsyncStarted() {
		return this.getHttpServletRequest().isAsyncStarted();
	}

	@Override
	public boolean isAsyncSupported() {
		return this.getHttpServletRequest().isAsyncSupported();
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		return this.getHttpServletRequest().startAsync();
	}

	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
		return this.getHttpServletRequest().startAsync(arg0,arg1);
	}

	@Override
	public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException {
		return this.getHttpServletRequest().authenticate(arg0);
	}

	@Override
	public String changeSessionId() {
		return this.getHttpServletRequest().changeSessionId();
	}

	@Override
	public Part getPart(String arg0) throws IOException, ServletException {
		return this.getHttpServletRequest().getPart(arg0);
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		return this.getHttpServletRequest().getParts();
	}

	@Override
	public void login(String arg0, String arg1) throws ServletException {
		this.getHttpServletRequest().login(arg0,arg1);
	}

	@Override
	public void logout() throws ServletException {
		this.getHttpServletRequest().logout();
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0) throws IOException, ServletException {
		return this.getHttpServletRequest().upgrade(arg0);
	}
	
}
