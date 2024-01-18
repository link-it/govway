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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.openspcoop2.utils.Utilities;

/**
* MultipartFilter
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class MultipartFilter  implements Filter{

	/** Multipart request start */
	public static final String MULTIPART = "multipart/";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		BufferedRequestWrapper reqWrapper = null;
		if(isMultipartRequest(httpServletRequest)){
//			log.debug("Ricevuta Richiesta Multipart..."); 
			reqWrapper = new BufferedRequestWrapper((HttpServletRequest) request);
//			log.debug("Contenuto: [" +new String(reqWrapper.getBuffer())+"]");
			chain.doFilter(reqWrapper, response);
		}else {
			chain.doFilter(request, response);
		}
	}

	private boolean isMultipartRequest(HttpServletRequest request) {
		if (!"post".equals(request.getMethod().toLowerCase())) {
			return false;
		}

		String contentType = request.getContentType();
		if (contentType == null) {
			return false;
		}

		if (contentType.toLowerCase().startsWith(MULTIPART)) {
			return true;
		}

		return false;
	}

	@Override
	public void destroy() {	
	}


	private class BufferedRequestWrapper extends HttpServletRequestWrapper {

		private ByteArrayInputStream bais;

		private BufferedServletInputStream bsis;

		private byte[] buffer;

		public BufferedRequestWrapper(HttpServletRequest req) throws IOException {
			super(req);
			try {
				this.buffer = Utilities.getAsByteArray(req.getInputStream());
			}catch(Exception e) {
				throw new IOException(e);
			}
		}

		@Override
		public ServletInputStream getInputStream() {
			try {
				this.bais = new ByteArrayInputStream(this.buffer);
				this.bsis = new BufferedServletInputStream(this.bais);
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}

			return this.bsis;
		}

		@SuppressWarnings("unused")
		public byte[] getBuffer() {
			return this.buffer;
		}
	}

	class BufferedServletInputStream extends ServletInputStream {

		private ByteArrayInputStream bais;

		public BufferedServletInputStream(ByteArrayInputStream bais) {
			this.bais = bais;
		}

		@Override
		public int available() {
			return this.bais.available();
		}

		@Override
		public int read() {
			return this.bais.read();
		}

		@Override
		public int read(byte[] buf, int off, int len) {
			return this.bais.read(buf, off, len);
		}

		@Override
		public boolean isFinished() {
			return false;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setReadListener(ReadListener arg0) {
			
		}

	}

}
