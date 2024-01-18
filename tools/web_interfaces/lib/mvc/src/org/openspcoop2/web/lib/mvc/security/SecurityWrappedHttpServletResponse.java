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

package org.openspcoop2.web.lib.mvc.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import org.openspcoop2.web.lib.mvc.security.exception.ValidationException;
import org.slf4j.Logger;

/**
 * SecurityWrappedHttpServletResponse
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityWrappedHttpServletResponse extends HttpServletResponseWrapper implements HttpServletResponse {

	private Logger log;
	private Validatore validator;

	public SecurityWrappedHttpServletResponse(HttpServletResponse httpServletResponse, Logger log) {
		 super(httpServletResponse);
		this.log = log;
		this.validator = Validatore.getInstance();

	}
	
    private HttpServletResponse getHttpServletResponse() {
        return (HttpServletResponse)super.getResponse();
    }
	
	@Override
	public void flushBuffer() throws IOException {
		this.getHttpServletResponse().flushBuffer();
	}

	@Override
	public int getBufferSize() {
		return this.getHttpServletResponse().getBufferSize();
	}

	@Override
	public String getCharacterEncoding() {
		return this.getHttpServletResponse().getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return this.getHttpServletResponse().getContentType();
	}

	@Override
	public Locale getLocale() {
		return this.getHttpServletResponse().getLocale();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return this.getHttpServletResponse().getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return this.getHttpServletResponse().getWriter();
	}

	@Override
	public boolean isCommitted() {
		return this.getHttpServletResponse().isCommitted();
	}

	@Override
	public void reset() {
		this.getHttpServletResponse().reset();
	}

	@Override
	public void resetBuffer() {
		this.getHttpServletResponse().resetBuffer();
	}

	@Override
	public void setBufferSize(int arg0) {
		this.getHttpServletResponse().setBufferSize(arg0);
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		this.getHttpServletResponse().setCharacterEncoding(arg0);
	}

	@Override
	public void setContentLength(int arg0) {
		this.getHttpServletResponse().setContentLength(arg0);
	}

	@Override
	public void setContentType(String arg0) {
		this.getHttpServletResponse().setContentType(arg0);
	}

	@Override
	public void setLocale(Locale arg0) {
		this.getHttpServletResponse().setLocale(arg0);
	}

	@Override
	public void addCookie(Cookie cookie) {
		this.getHttpServletResponse().addCookie(cookie);
	}

	@Override
	public void addDateHeader(String arg0, long arg1) {
		this.getHttpServletResponse().addDateHeader(arg0, arg1);
	}

	@Override
	public void addHeader(String arg0, String arg1) {
		String strippedValue = SecurityWrappedHttpServletResponse.stripControls(arg1);
		String safeValue = null;

		try {
			safeValue = this.validator.validate("Il valore dell'Header [" + arg0+ "]:["+arg1+"]", strippedValue, null, false, Costanti.PATTERN_RESPONSE_HTTP_HEADER_VALUE);
		} catch (ValidationException e) {
			this.log.warn("Impossibile impostare un header con il valore errato ["+arg0+"]: " + e.getMessage(), e);
		}

		boolean validValue = SecurityWrappedHttpServletResponse.notNullOrEmpty(safeValue, true);

		if (validValue) {
			this.getHttpServletResponse().addHeader(arg0, safeValue);
		}
	}

	@Override
	public void addIntHeader(String arg0, int arg1) {
		this.getHttpServletResponse().addIntHeader(arg0, arg1);
	}

	@Override
	public boolean containsHeader(String arg0) {
		return this.getHttpServletResponse().containsHeader(arg0);
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		return this.getHttpServletResponse().encodeRedirectURL(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String encodeRedirectUrl(String arg0) {
		return this.getHttpServletResponse().encodeRedirectUrl(arg0);
	}

	@Override
	public String encodeURL(String arg0) {
		return this.getHttpServletResponse().encodeURL(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String encodeUrl(String arg0) {
		return this.getHttpServletResponse().encodeUrl(arg0);
	}

	@Override
	public void sendError(int arg0) throws IOException {
		this.getHttpServletResponse().sendError(arg0);
	}

	@Override
	public void sendError(int arg0, String arg1) throws IOException {
		this.getHttpServletResponse().sendError(arg0, arg1);
	}

	@Override
	public void sendRedirect(String arg0) throws IOException {
		this.getHttpServletResponse().sendRedirect(arg0);
	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		this.getHttpServletResponse().setDateHeader(arg0, arg1);
	}

	@Override
	public void setHeader(String arg0, String arg1) {
		String strippedValue = SecurityWrappedHttpServletResponse.stripControls(arg1);
		String safeValue = null;
		try {
			safeValue = this.validator.validate("Il valore dell'Header [" + arg0+ "]:["+arg1+"]", strippedValue, null, false, Costanti.PATTERN_RESPONSE_HTTP_HEADER_VALUE);
		} catch (ValidationException e) {
			this.log.warn("Impossibile impostare un header con il valore errato ["+arg0+"]: " + e.getMessage(), e);
		}

		boolean validValue = SecurityWrappedHttpServletResponse.notNullOrEmpty(safeValue, true);

		if (validValue) {
			this.getHttpServletResponse().setHeader(arg0, safeValue);
		}
	}

	@Override
	public void setIntHeader(String arg0, int arg1) {
		this.getHttpServletResponse().setIntHeader(arg0, arg1);
	}

	@Override
	public void setStatus(int arg0) {
		this.getHttpServletResponse().setStatus(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public void setStatus(int arg0, String arg1) {
		this.getHttpServletResponse().setStatus(arg0, arg1);
	}

	// v3
	
	@Override
	public void setContentLengthLong(long arg0) {
		this.getHttpServletResponse().setContentLengthLong(arg0);
	}

	@Override
	public String getHeader(String arg0) {
		String value = this.getHttpServletResponse().getHeader(arg0);
		if(value != null) {
			try {
				return this.validator.validate("Il valore dell'Header [" + arg0+ "]:["+value+"]", value, null, true, Costanti.PATTERN_RESPONSE_HTTP_HEADER_VALUE);
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: "+ e.getMessage(),e);
				return "";
			}
		}
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		return this.getHttpServletResponse().getHeaderNames();
	}

	@Override
	public Collection<String> getHeaders(String arg0) {
		List<String> v = new ArrayList<>();
		Collection<String> headers = this.getHttpServletResponse().getHeaders(arg0);
		List<String> lst = new ArrayList<>();
		if(headers != null && !headers.isEmpty())
			lst.addAll(headers);

		for (String value : lst) {
			try {
				v.add(this.validator.validate("Il valore dell'Header [" + arg0+ "]:["+value+"]", value, null, true, Costanti.PATTERN_REQUEST_HTTP_HEADER_VALUE));
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: "+ e.getMessage(),e);
			}
		}
		return v;

	}

	@Override
	public int getStatus() {
		return this.getHttpServletResponse().getStatus();
	}
	
	/**
	 * Removes all unprintable characters from a string
	 * and replaces with a space.
	 * @param input
	 * @return the stripped value
	 */
	public static String stripControls( String input ) {
		StringBuilder sb = new StringBuilder();
		for ( int i=0; i<input.length(); i++ ) {
			char c = input.charAt( i );
			if ( c > 0x20 && c < 0x7f ) {
				sb.append( c );
			} else {
				sb.append( ' ' );
			}
		}
		return sb.toString();
	}

	public static boolean notNullOrEmpty(String str, boolean trim) {
		if ( trim ) {
			return !( str == null || str.trim().equals("") );
		} else {
			return !( str == null || str.equals("") );
		}
	}
}
