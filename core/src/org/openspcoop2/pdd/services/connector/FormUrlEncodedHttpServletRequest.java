/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.pdd.services.connector;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.internet.ContentType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;


/**
 * FormUrlEncodedHttpServletRequest
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: mergefairy $
 * @version $Rev: 10491 $, $Date: 2015-01-13 10:33:50 +0100 (Tue, 13 Jan 2015) $
 */
public class FormUrlEncodedHttpServletRequest implements HttpServletRequest {

	private final static String CONTENT_TYPE_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
	
	public static boolean isFormUrlEncodedRequest(HttpServletRequest httpServletRequest) {
		String ct = httpServletRequest.getContentType();
		try {
			String baseType = readBaseType(ct);
			if(CONTENT_TYPE_FORM_URL_ENCODED.equalsIgnoreCase(baseType)) {
				return true;
			}
		}catch(Exception e) {
		}
		return false;
	}
	
	public static HttpServletRequest convert(HttpServletRequest httpServletRequest) {
		String ct = httpServletRequest.getContentType();
		try {
			String baseType = readBaseType(ct);
			if(CONTENT_TYPE_FORM_URL_ENCODED.equalsIgnoreCase(baseType)) {
				return new FormUrlEncodedHttpServletRequest(httpServletRequest);
			}
		}catch(Exception e) {
		}
		return httpServletRequest;
	}
	
	private static String readBaseType(String ct) throws UtilsException {
		if(ct==null || "".equals(ct)) {
			throw new UtilsException("Content-Type not defined");
		}
		String baseType = null;
		try {
			ContentType ctObject = new ContentType(ct);
			baseType = ctObject.getBaseType();
		}catch(Exception e) {
			throw new UtilsException("Content-Type ["+ct+"] (parsing error): "+e.getMessage(),e);
		}
		return baseType;
	}
	
	private HttpServletRequest httpServletRequest;
	private byte[] content;
	private Properties properties;
	public FormUrlEncodedHttpServletRequest(HttpServletRequest httpServletRequest) throws UtilsException{
		this.httpServletRequest = httpServletRequest;
		
		String ct = httpServletRequest.getContentType();
		String baseType = null;
		try {
			baseType = readBaseType(ct);
		}catch(Exception e) {
		}
		if(CONTENT_TYPE_FORM_URL_ENCODED.equalsIgnoreCase(baseType)==false) {
			throw new UtilsException("Content-Type ["+ct+"] non supportato");
		}
		
		try {
			InputStream is = httpServletRequest.getInputStream();
			if(is!=null) {
				this.content = Utilities.getAsByteArray(is);
			}
		}catch(Exception e) {
			throw new UtilsException("Content-Type ["+ct+"] read stream error: "+e.getMessage(),e);
		}
		
		java.util.Enumeration<?> en = httpServletRequest.getParameterNames();
		this.properties = new Properties();
		while(en.hasMoreElements()){
			String nomeProperty = (String)en.nextElement();
			String valueProperty = httpServletRequest.getParameter(nomeProperty);
			this.properties.put(nomeProperty, valueProperty);
		}
	}
	
	// Metodi modificati nei risultati
	
	@Override
	public int getContentLength() {
		if(this.content!=null) {
			return this.content.length;
		}
		return 0;
	}
	
	@Override
	public String getContentType() {
		return this.httpServletRequest.getContentType();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if(this.content!=null) {
			try {				
				return new FormUrlEncodedServletInputStream(new ByteArrayInputStream(this.content));
			}catch(Exception e) {
				throw new IOException(e.getMessage(),e);
			}
		}
		return null;
	}
	
	@Override
	public BufferedReader getReader() throws IOException {
		if(this.content!=null) {
			return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.content), this.httpServletRequest.getCharacterEncoding()));
		}
		return null;
	}
	
	@Override
	public String getParameter(String key) {
		return null;
	}

	@Override
	public Map<java.lang.String,java.lang.String[]> getParameterMap() {
		return new HashMap<java.lang.String,java.lang.String[]>();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		Hashtable<String, String> empty = new Hashtable<String, String>();
		return empty.keys();
	}

	@Override
	public String[] getParameterValues(String arg0) {
		return null;
	}
	
	public String getFormUrlEncodedParameter(String key) {
		return this.properties.getProperty(key);
	}

	public Enumeration<Object> getFormUrlEncodedParameterNames() {
		return this.properties.keys();
	}
	
	
	
	
	// Metodi originali
	
	@Override
	public String getRequestURI() {
		return this.httpServletRequest.getRequestURI();
	}
		
	@Override
	public String getContextPath() {
		return this.httpServletRequest.getContextPath();
	}
	
	@Override
	public String getPathInfo() {
		return this.httpServletRequest.getPathInfo();
	}
	
	@Override
	public String getPathTranslated() {
		return this.httpServletRequest.getPathTranslated();
	}

	@Override
	public String getQueryString() {
		return this.httpServletRequest.getQueryString();
	}
	
	@Override
	public StringBuffer getRequestURL() {
		return this.httpServletRequest.getRequestURL();
	}
	
	@Override
	public String getServletPath() {
		return this.httpServletRequest.getServletPath();
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public String getRealPath(String arg0) {
		return this.httpServletRequest.getRealPath(arg0);
	}
	
	@Override
	public Object getAttribute(String arg0) {
		return this.httpServletRequest.getAttribute(arg0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> getAttributeNames() {
		return this.httpServletRequest.getAttributeNames();
	}

	@Override
	public String getCharacterEncoding() {
		return this.httpServletRequest.getCharacterEncoding();
	}

	@Override
	public String getLocalAddr() {
		return this.httpServletRequest.getLocalAddr();
	}

	@Override
	public String getLocalName() {
		return this.httpServletRequest.getLocalName();
	}

	@Override
	public int getLocalPort() {
		return this.httpServletRequest.getLocalPort();
	}

	@Override
	public Locale getLocale() {
		return this.httpServletRequest.getLocale();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<java.util.Locale> getLocales() {
		return this.httpServletRequest.getLocales();
	}

	@Override
	public String getProtocol() {
		return this.httpServletRequest.getProtocol();
	}

	@Override
	public String getRemoteAddr() {
		return this.httpServletRequest.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return this.httpServletRequest.getRemoteHost();
	}

	@Override
	public int getRemotePort() {
		return this.httpServletRequest.getRemotePort();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return this.httpServletRequest.getRequestDispatcher(arg0);
	}

	@Override
	public String getScheme() {
		return this.httpServletRequest.getScheme();
	}

	@Override
	public String getServerName() {
		return this.httpServletRequest.getServerName();
	}

	@Override
	public int getServerPort() {
		return this.httpServletRequest.getServerPort();
	}

	@Override
	public boolean isSecure() {
		return this.httpServletRequest.isSecure();
	}

	@Override
	public void removeAttribute(String arg0) {
		this.httpServletRequest.removeAttribute(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		this.httpServletRequest.setAttribute(arg0,arg1);
	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		this.httpServletRequest.setCharacterEncoding(arg0);
	}

	@Override
	public String getAuthType() {
		return this.httpServletRequest.getAuthType();
	}

	@Override
	public Cookie[] getCookies() {
		return this.httpServletRequest.getCookies();
	}

	@Override
	public long getDateHeader(String arg0) {
		return this.httpServletRequest.getDateHeader(arg0);
	}

	@Override
	public String getHeader(String arg0) {
		return this.httpServletRequest.getHeader(arg0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> getHeaderNames() {
		return this.httpServletRequest.getHeaderNames();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> getHeaders(String arg0) {
		return this.httpServletRequest.getHeaders(arg0);
	}

	@Override
	public int getIntHeader(String arg0) {
		return this.httpServletRequest.getIntHeader(arg0);
	}

	@Override
	public String getMethod() {
		return this.httpServletRequest.getMethod();
	}

	@Override
	public String getRemoteUser() {
		return this.httpServletRequest.getRemoteUser();
	}

	@Override
	public String getRequestedSessionId() {
		return this.httpServletRequest.getRequestedSessionId();
	}

	@Override
	public HttpSession getSession() {
		return this.httpServletRequest.getSession();
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		return this.httpServletRequest.getSession(arg0);
	}

	@Override
	public Principal getUserPrincipal() {
		return this.httpServletRequest.getUserPrincipal();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return this.httpServletRequest.isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return this.httpServletRequest.isRequestedSessionIdFromURL();
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public boolean isRequestedSessionIdFromUrl() {
		return this.httpServletRequest.isRequestedSessionIdFromUrl();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return this.httpServletRequest.isRequestedSessionIdValid();
	}

	@Override
	public boolean isUserInRole(String arg0) {
		return this.httpServletRequest.isUserInRole(arg0);
	}
	
}


class FormUrlEncodedServletInputStream extends ServletInputStream {

	private InputStream is;
	public FormUrlEncodedServletInputStream(InputStream is) {
		this.is = is;
	}
	
	@Override
	public int readLine(byte[] b, int off, int len) throws IOException {
		return this.is.read(b, off, len);
	}

	@Override
	public int read() throws IOException {
		return this.is.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return this.is.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return this.is.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return this.is.skip(n);
	}

	@Override
	public int available() throws IOException {
		return this.is.available();
	}

	@Override
	public void close() throws IOException {
		this.is.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		this.is.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		this.is.reset();
	}

	@Override
	public boolean markSupported() {
		return this.is.markSupported();
	}


}
