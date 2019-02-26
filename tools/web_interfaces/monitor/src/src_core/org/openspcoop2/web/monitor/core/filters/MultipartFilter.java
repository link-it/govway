/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.core.filters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

import org.slf4j.Logger;

import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

/**
 * MultipartFilter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class MultipartFilter  implements Filter{

	/** Logger utilizzato per debug. * */
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private boolean filtroAttivo;

	/** Multipart request start */
	public static final String MULTIPART = "multipart/";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try{
			this.filtroAttivo = PddMonitorProperties.getInstance(log).isMultipartRequestCache();
		}catch(Exception e){
			log.error("Errore durante la init del filtro: "+ e.getMessage(),e);
			throw new ServletException(e);
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		BufferedRequestWrapper reqWrapper = null;
		if(this.filtroAttivo && isMultipartRequest(httpServletRequest)){
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

		private ByteArrayOutputStream baos;

		private BufferedServletInputStream bsis;

		private byte[] buffer;

		public BufferedRequestWrapper(HttpServletRequest req) throws IOException {
			super(req);
			InputStream is = req.getInputStream();
			this.baos = new ByteArrayOutputStream();
			byte buf[] = new byte[1024];
			int letti;
			while ((letti = is.read(buf)) > 0) {
				this.baos.write(buf, 0, letti);
			}
			this.buffer = this.baos.toByteArray();
		}

		@Override
		public ServletInputStream getInputStream() {
			try {
				this.bais = new ByteArrayInputStream(this.buffer);
				this.bsis = new BufferedServletInputStream(this.bais);
			} catch (Exception ex) {
				ex.printStackTrace();
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
