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
package org.openspcoop2.protocol.modipa.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Classe per simulare una risposta servlet http.
 *
 * Estende HttpServletResponseWrapper per garantire la compatibilità con
 * l'opzione Tomcat STRICT_SERVLET_COMPLIANCE (verifica SRV.8.2 e SRV.14.2.5.1).
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MockHttpServletResponse extends HttpServletResponseWrapper {

	private String contentType;
	private Long contentLength;
	private Map<String, List<String>> headers = new HashMap<>();
	private int status;
	private Integer bufferSize = 10;
	private String characterEncoding = null;
	private MockServletOutputStream outputStream = new MockServletOutputStream();
	private boolean committed = false;
	private Locale locale = null;
	private List<Cookie> cookies = new ArrayList<>();

	public MockHttpServletResponse(HttpServletResponse response) {
		super(response);
	}

	@Override
	public void flushBuffer() throws IOException {
		this.committed = true;
	}

	@Override
	public int getBufferSize() {
		return this.bufferSize;
	}

	@Override
	public String getCharacterEncoding() {
		return this.characterEncoding;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public Locale getLocale() {
		return this.locale;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return this.outputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(this.outputStream);
	}

	@Override
	public boolean isCommitted() {
		return this.committed;
	}

	@Override
	public void reset() {
		this.resetBuffer();
		this.headers.clear();
		this.status = 0;
	}

	@Override
	public void resetBuffer() {
		// Non creare un nuovo outputStream: mantieni lo stesso riferimento
		// per evitare la perdita di dati quando Tomcat chiama resetBuffer
		this.outputStream.getByteArrayOutputStream().reset();
	}

	@Override
	public void setBufferSize(int arg0) {
		this.bufferSize = arg0;
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		this.characterEncoding = arg0;
	}

	@Override
	public void setContentLength(int arg0) {
		this.contentLength = Long.valueOf(arg0);
	}

	@Override
	public void setContentLengthLong(long arg0) {
		this.contentLength = arg0;
	}

	@Override
	public void setContentType(String arg0) {
		this.contentType = arg0;
	}

	public Long getContentLength() {
		return this.contentLength;
	}

	@Override
	public void setLocale(Locale arg0) {
		this.locale = arg0;
	}

	@Override
	public void addCookie(Cookie arg0) {
		this.cookies.add(arg0);
	}

	@Override
	public void addDateHeader(String arg0, long arg1) {
		this.headers.computeIfAbsent(arg0, k -> new ArrayList<>()).add(String.valueOf(arg1));
	}

	@Override
	public void addHeader(String arg0, String arg1) {
		this.headers.computeIfAbsent(arg0, k -> new ArrayList<>()).add(arg1);
	}

	@Override
	public void addIntHeader(String arg0, int arg1) {
		this.headers.computeIfAbsent(arg0, k -> new ArrayList<>()).add(String.valueOf(arg1));
	}

	@Override
	public boolean containsHeader(String arg0) {
		return this.headers.containsKey(arg0);
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		return arg0;
	}

	/**
	 * @deprecated
	 */
	@Override
	@SuppressWarnings("deprecation")
	@Deprecated(since="3.3")
	public String encodeRedirectUrl(String arg0) {
		return arg0;
	}

	@Override
	public String encodeURL(String arg0) {
		return arg0;
	}

	/**
	 * @deprecated
	 */
	@Override
	@SuppressWarnings("deprecation")
	@Deprecated(since="3.3")
	public String encodeUrl(String arg0) {
		return arg0;
	}

	@Override
	public String getHeader(String arg0) {
		return String.join(",", this.headers.getOrDefault(arg0, List.of()));
	}

	@Override
	public Collection<String> getHeaderNames() {
		return this.headers.keySet();
	}

	@Override
	public Collection<String> getHeaders(String arg0) {
		return this.headers.getOrDefault(arg0, List.of());
	}

	@Override
	public int getStatus() {
		return this.status;
	}

	@Override
	public void sendError(int code) throws IOException {
		this.status = code;
	}

	@Override
	public void sendError(int code, String arg1) throws IOException {
		this.status = code;
	}

	@Override
	public void sendRedirect(String arg0) throws IOException {
		// ignore
	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		List<String> list = this.headers.computeIfAbsent(arg0, k -> new ArrayList<>());
		list.clear();
		list.add(String.valueOf(arg1));
	}

	@Override
	public void setHeader(String arg0, String arg1) {
		List<String> list = new ArrayList<>();
		list.addAll(List.of(arg1.split(",")));
		this.headers.put(arg0, list);
	}

	@Override
	public void setIntHeader(String arg0, int arg1) {
		this.headers.put(arg0, List.of(String.valueOf(arg1)));
	}

	@Override
	public void setStatus(int arg0) {
		this.status = arg0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setStatus(int arg0, String arg1) {
		this.status = arg0;
	}

}
