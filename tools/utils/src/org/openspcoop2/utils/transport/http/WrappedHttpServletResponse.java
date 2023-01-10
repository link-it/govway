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

package org.openspcoop2.utils.transport.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * WrappedHttpServletResponse
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WrappedHttpServletResponse implements HttpServletResponse {

	protected HttpServletResponse httpServletResponse;

	public WrappedHttpServletResponse(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
		
	}
	
	@Override
	public void flushBuffer() throws IOException {
		this.httpServletResponse.flushBuffer();
	}

	@Override
	public int getBufferSize() {
		return this.httpServletResponse.getBufferSize();
	}

	@Override
	public String getCharacterEncoding() {
		return this.httpServletResponse.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return this.httpServletResponse.getContentType();
	}

	@Override
	public Locale getLocale() {
		return this.httpServletResponse.getLocale();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return this.httpServletResponse.getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return this.httpServletResponse.getWriter();
	}

	@Override
	public boolean isCommitted() {
		return this.httpServletResponse.isCommitted();
	}

	@Override
	public void reset() {
		this.httpServletResponse.reset();
	}

	@Override
	public void resetBuffer() {
		this.httpServletResponse.resetBuffer();
	}

	@Override
	public void setBufferSize(int arg0) {
		this.httpServletResponse.setBufferSize(arg0);
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		this.httpServletResponse.setCharacterEncoding(arg0);
	}

	@Override
	public void setContentLength(int arg0) {
		this.httpServletResponse.setContentLength(arg0);
	}

	@Override
	public void setContentType(String arg0) {
		this.httpServletResponse.setContentType(arg0);
	}

	@Override
	public void setLocale(Locale arg0) {
		this.httpServletResponse.setLocale(arg0);
	}

	@Override
	public void addCookie(Cookie arg0) {
		this.httpServletResponse.addCookie(arg0);
	}

	@Override
	public void addDateHeader(String arg0, long arg1) {
		this.httpServletResponse.addDateHeader(arg0, arg1);
	}

	@Override
	public void addHeader(String arg0, String arg1) {
		this.httpServletResponse.addHeader(arg0, arg1);
	}

	@Override
	public void addIntHeader(String arg0, int arg1) {
		this.httpServletResponse.addIntHeader(arg0, arg1);
	}

	@Override
	public boolean containsHeader(String arg0) {
		return this.httpServletResponse.containsHeader(arg0);
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		return this.httpServletResponse.encodeRedirectURL(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String encodeRedirectUrl(String arg0) {
		return this.httpServletResponse.encodeRedirectUrl(arg0);
	}

	@Override
	public String encodeURL(String arg0) {
		return this.httpServletResponse.encodeURL(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String encodeUrl(String arg0) {
		return this.httpServletResponse.encodeUrl(arg0);
	}

	@Override
	public void sendError(int arg0) throws IOException {
		this.httpServletResponse.sendError(arg0);
	}

	@Override
	public void sendError(int arg0, String arg1) throws IOException {
		this.httpServletResponse.sendError(arg0, arg1);
	}

	@Override
	public void sendRedirect(String arg0) throws IOException {
		this.httpServletResponse.sendRedirect(arg0);
	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		this.httpServletResponse.setDateHeader(arg0, arg1);
	}

	@Override
	public void setHeader(String arg0, String arg1) {
		this.httpServletResponse.setHeader(arg0, arg1);
	}

	@Override
	public void setIntHeader(String arg0, int arg1) {
		this.httpServletResponse.setIntHeader(arg0, arg1);
	}

	@Override
	public void setStatus(int arg0) {
		this.httpServletResponse.setStatus(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setStatus(int arg0, String arg1) {
		this.httpServletResponse.setStatus(arg0, arg1);
	}

	// v3
	
	@Override
	public void setContentLengthLong(long arg0) {
		this.httpServletResponse.setContentLengthLong(arg0);
	}

	@Override
	public String getHeader(String arg0) {
		return this.httpServletResponse.getHeader(arg0);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return this.httpServletResponse.getHeaderNames();
	}

	@Override
	public Collection<String> getHeaders(String arg0) {
		return this.httpServletResponse.getHeaders(arg0);
	}

	@Override
	public int getStatus() {
		return this.httpServletResponse.getStatus();
	}

}
