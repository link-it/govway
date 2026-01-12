/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

/**
 * Classe per simulare una richiesta servlet http in cui si possono sovrascrivere
 * vari elementi.
 *
 * Estende HttpServletRequestWrapper per garantire la compatibilità con
 * l'opzione Tomcat STRICT_SERVLET_COMPLIANCE (verifica SRV.8.2 e SRV.14.2.5.1).
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MockHttpServletRequest extends HttpServletRequestWrapper {

	private AsyncContext asyncContext = null;
	private Map<String, Object> attributes = null;
	private Map<String, List<String>> headers = null;
	private String characterEncoding = null;
	private DispatcherType dispatcherType = null;
	private MockServletInputStream is = null;

	public MockHttpServletRequest(HttpServletRequest req) {
		super(req);
	}

	private HttpServletRequest getHttpServletRequest() {
		return (HttpServletRequest) getRequest();
	}

	@Override
	public AsyncContext getAsyncContext() {
		if (this.asyncContext != null)
			return this.asyncContext;
		return getHttpServletRequest().getAsyncContext();
	}
	public void setAsyncContext(AsyncContext asyncContext) {
		this.asyncContext = asyncContext;
	}

	@Override
	public Object getAttribute(String arg0) {
		if (this.attributes != null)
			return this.attributes.get(arg0);
		return getHttpServletRequest().getAttribute(arg0);
	}
	@Override
	public Enumeration<String> getAttributeNames() {
		if (this.attributes != null)
			return Collections.enumeration(this.attributes.keySet());
		return getHttpServletRequest().getAttributeNames();
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getCharacterEncoding() {
		if (this.characterEncoding != null)
			return this.characterEncoding;
		return getHttpServletRequest().getCharacterEncoding();
	}
	public void setWrappedCharacterEncoding(String enc) {
		this.characterEncoding = enc;
	}

	@Override
	public int getContentLength() {
		return this.getIntHeader("Content-Length");
	}

	@Override
	public long getContentLengthLong() {
		return this.getIntHeader("Content-Length");
	}

	@Override
	public String getContentType() {
		return this.getHeader("Content-Type");
	}

	@Override
	public DispatcherType getDispatcherType() {
		if (this.dispatcherType != null)
			return this.dispatcherType;
		return getHttpServletRequest().getDispatcherType();
	}
	public void setDispatcherType(DispatcherType dispatcherType) {
		this.dispatcherType = dispatcherType;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (this.is != null)
			return this.is;
		return getHttpServletRequest().getInputStream();
	}

	@Override
	public String getLocalAddr() {
		return getHttpServletRequest().getLocalAddr();
	}

	@Override
	public String getLocalName() {
		return getHttpServletRequest().getLocalName();
	}

	@Override
	public int getLocalPort() {
		return getHttpServletRequest().getLocalPort();
	}

	@Override
	public Locale getLocale() {
		return getHttpServletRequest().getLocale();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return getHttpServletRequest().getLocales();
	}

	@Override
	public String getParameter(String arg0) {
		return getHttpServletRequest().getParameter(arg0);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return getHttpServletRequest().getParameterMap();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return getHttpServletRequest().getParameterNames();
	}

	@Override
	public String[] getParameterValues(String arg0) {
		return getHttpServletRequest().getParameterValues(arg0);
	}

	@Override
	public String getProtocol() {
		return getHttpServletRequest().getProtocol();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		if (this.is != null)
			return new BufferedReader(new InputStreamReader(this.is, StandardCharsets.UTF_8));
		return getHttpServletRequest().getReader();
	}

	@Override
	public String getRemoteAddr() {
		return getHttpServletRequest().getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return getHttpServletRequest().getRemoteHost();
	}

	@Override
	public int getRemotePort() {
		return getHttpServletRequest().getRemotePort();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return getHttpServletRequest().getRequestDispatcher(arg0);
	}

	@Override
	public String getScheme() {
		return getHttpServletRequest().getScheme();
	}

	@Override
	public String getServerName() {
		return getHttpServletRequest().getServerName();
	}

	@Override
	public int getServerPort() {
		return getHttpServletRequest().getServerPort();
	}

	@Override
	public ServletContext getServletContext() {
		return getHttpServletRequest().getServletContext();
	}

	@Override
	public boolean isAsyncStarted() {
		return getHttpServletRequest().isAsyncStarted();
	}

	@Override
	public boolean isAsyncSupported() {
		return getHttpServletRequest().isAsyncSupported();
	}

	@Override
	public boolean isSecure() {
		return getHttpServletRequest().isSecure();
	}

	@Override
	public void removeAttribute(String arg0) {
		getHttpServletRequest().removeAttribute(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		getHttpServletRequest().setAttribute(arg0, arg1);

	}

	@Override
	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		getHttpServletRequest().setCharacterEncoding(arg0);
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		return getHttpServletRequest().startAsync();
	}

	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
		return getHttpServletRequest().startAsync(arg0, arg1);
	}

	@Override
	public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException {
		return getHttpServletRequest().authenticate(arg0);
	}

	@Override
	public String changeSessionId() {
		return getHttpServletRequest().changeSessionId();
	}

	@Override
	public String getAuthType() {
		return getHttpServletRequest().getAuthType();
	}

	@Override
	public String getContextPath() {
		return getHttpServletRequest().getContextPath();
	}

	@Override
	public Cookie[] getCookies() {
		return getHttpServletRequest().getCookies();
	}

	@Override
	public long getDateHeader(String arg0) {
		return Long.valueOf(this.getHeader(arg0));
	}

	private String preProcessHeaderName(String name) {
		if (name != null) {
			return name.toLowerCase();
		}
		return name;
	}
	
	@Override
	public String getHeader(String arg0) {
		arg0 = preProcessHeaderName(arg0);
		if (this.headers != null && this.headers.containsKey(arg0)) {
			List<String> v = this.headers.get(arg0);
			if(v!=null && !v.isEmpty()) {
				return String.join(",", this.headers.get(arg0));
			}
		}
		return getHttpServletRequest().getHeader(arg0);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		Set<String> names = new HashSet<>();
		if (this.headers != null)
			names.addAll(this.headers.keySet());
		if (getHttpServletRequest() != null)
			getHttpServletRequest().getHeaderNames().asIterator().forEachRemaining(name ->
				names.add(preProcessHeaderName(name))
			);
		return Collections.enumeration(names);
	}

	@Override
	public Enumeration<String> getHeaders(String arg0) {
		arg0 = preProcessHeaderName(arg0);
		Set<String> values = new HashSet<>();
		if (this.headers != null)
			values.addAll(Objects.requireNonNullElse(this.headers.getOrDefault(arg0, List.of()), List.of()));
		if (getHttpServletRequest() != null && values.isEmpty())
			getHttpServletRequest().getHeaders(arg0).asIterator().forEachRemaining(values::add);
		return Collections.enumeration(values.stream().filter(o -> o != null).collect(Collectors.toList()));
	}

	@Override
	public int getIntHeader(String arg0) {
		return Integer.valueOf(this.getHeader(arg0));
	}

	@Override
	public String getMethod() {
		return getHttpServletRequest().getMethod();
	}

	@Override
	public Part getPart(String arg0) throws IOException, ServletException {
		return getHttpServletRequest().getPart(arg0);
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		return getHttpServletRequest().getParts();
	}

	@Override
	public String getPathInfo() {
		return getHttpServletRequest().getPathInfo();
	}

	@Override
	public String getPathTranslated() {
		return getHttpServletRequest().getPathTranslated();
	}

	@Override
	public String getQueryString() {
		return getHttpServletRequest().getQueryString();
	}

	@Override
	public String getRemoteUser() {
		return getHttpServletRequest().getRemoteUser();
	}

	@Override
	public String getRequestURI() {
		return getHttpServletRequest().getRequestURI();
	}

	@Override
	public StringBuffer getRequestURL() {
		return getHttpServletRequest().getRequestURL();
	}

	@Override
	public String getRequestedSessionId() {
		return null;
	}

	@Override
	public String getServletPath() {
		return getHttpServletRequest().getServletPath();
	}

	@Override
	public HttpSession getSession() {
		return getHttpServletRequest().getSession();
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		return getHttpServletRequest().getSession(arg0);
	}

	@Override
	public Principal getUserPrincipal() {
		return getHttpServletRequest().getUserPrincipal();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return getHttpServletRequest().isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return getHttpServletRequest().isRequestedSessionIdFromURL();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return getHttpServletRequest().isRequestedSessionIdValid();
	}

	@Override
	public boolean isUserInRole(String arg0) {
		return getHttpServletRequest().isUserInRole(arg0);
	}

	@Override
	public void login(String arg0, String arg1) throws ServletException {
		getHttpServletRequest().login(arg0, arg1);
	}

	@Override
	public void logout() throws ServletException {
		getHttpServletRequest().logout();
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0) throws IOException, ServletException {
		return getHttpServletRequest().upgrade(arg0);
	}

	@Override
	public String getProtocolRequestId() {
		return getHttpServletRequest().getProtocolRequestId();
	}

	@Override
	public String getRequestId() {
		return getHttpServletRequest().getRequestId();
	}

	@Override
	public ServletConnection getServletConnection() {
		return getHttpServletRequest().getServletConnection();
	}
	
	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = new HashMap<>();
		for (Map.Entry<String, List<String>> entry : headers.entrySet())
			this.headers.put(preProcessHeaderName(entry.getKey()), entry.getValue());
	}
	
	public void setHeader(String name, List<String> value) {
		name = preProcessHeaderName(name);
		if (this.headers == null)
			this.headers = new HashMap<>();
		this.headers.put(name, value);
	}
	
	public void setInputStream(MockServletInputStream is) {
		this.is = is;
	}

}
