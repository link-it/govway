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
package org.openspcoop2.web.monitor.core.filters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import org.openspcoop2.web.lib.mvc.security.FormDataMultipartUtils;
import org.slf4j.Logger;

import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.servlet.UploadServlet;

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
	private long maxSize = FormDataMultipartUtils.DEFAULT_MAX_SIZE;

	/** Multipart request start */
	public static final String MULTIPART = "multipart/";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try{
			this.filtroAttivo = PddMonitorProperties.getInstance(log).isMultipartRequestCache();
			this.maxSize = PddMonitorProperties.getInstance(log).getMultipartRequestMaxSize();
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
		boolean multipart = isMultipartRequest(httpServletRequest);
		// verifica della dimensione massima dichiarata, prima di leggere il contenuto
		if(multipart && this.maxSize >= 0 && httpServletRequest.getContentLengthLong() > this.maxSize) {
			rifiutaRichiesta(httpServletRequest, response);
			return;
		}
		if(this.filtroAttivo && multipart){
//			log.debug("Ricevuta Richiesta Multipart..."); 
			try {
				reqWrapper = new BufferedRequestWrapper((HttpServletRequest) request, this.maxSize);
			}catch(FormDataMultipartUtils.MaxSizeExceededException e) {
				rifiutaRichiesta(httpServletRequest, response);
				return;
			}
//			log.debug("Contenuto: [" +new String(reqWrapper.getBuffer())+"]");
			chain.doFilter(reqWrapper, response);
		}else {
			chain.doFilter(request, response);
		}
	}

	private void rifiutaRichiesta(HttpServletRequest request, ServletResponse response) throws IOException {
		log.error("Richiesta multipart [{}] rifiutata: la dimensione supera il limite massimo consentito ({})", request.getRequestURI(), FormDataMultipartUtils.formatSize(this.maxSize));
		// risposta nello stesso formato utilizzato dalla servlet di upload, così che il messaggio possa essere mostrato all'utente
		UploadServlet.rispostaErrore((HttpServletResponse) response, 413, UploadServlet.getMessaggioDimensioneMassimaSuperata(this.maxSize));
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

		public BufferedRequestWrapper(HttpServletRequest req, long maxSize) throws IOException {
			super(req);
			InputStream is = req.getInputStream();
			// lettura con verifica della dimensione massima consentita
			this.buffer = FormDataMultipartUtils.readWithLimit(is, req.getContentLengthLong(), maxSize);
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
