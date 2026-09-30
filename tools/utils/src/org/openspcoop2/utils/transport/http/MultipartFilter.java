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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

import org.openspcoop2.utils.Utilities;

/**
* MultipartFilter
*
* Memorizza il body delle richieste multipart per consentirne più letture.
* Tramite l'init-param opzionale 'maxSize' è possibile indicare la dimensione massima consentita per il body,
* in byte o con suffisso K, M o G (es. '250M'): le richieste che la superano vengono rifiutate con codice HTTP 413
* senza memorizzarne il contenuto. In assenza del parametro, o con un valore negativo, non viene applicato alcun limite.
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class MultipartFilter  implements Filter{

	/** Multipart request start */
	public static final String MULTIPART = "multipart/";

	/** Nome dell'init-param con la dimensione massima del body */
	public static final String INIT_PARAM_MAX_SIZE = "maxSize";

	private static final int HTTP_STATUS_PAYLOAD_TOO_LARGE = 413;

	/** Dimensione massima del body (valore negativo = nessun limite) */
	private long maxSize = -1;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String v = filterConfig != null ? filterConfig.getInitParameter(INIT_PARAM_MAX_SIZE) : null;
		if(v != null && !v.trim().isEmpty()) {
			try {
				this.maxSize = Utilities.convertFormatStringToBytes(v);
			}catch(Exception e) {
				throw new ServletException("Invalid value for init-param '"+INIT_PARAM_MAX_SIZE+"' ["+v+"]: "+e.getMessage(),e);
			}
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		BufferedRequestWrapper reqWrapper = null;
		if(isMultipartRequest(httpServletRequest)){
//			log.debug("Ricevuta Richiesta Multipart..."); 
			if(this.maxSize >= 0 && httpServletRequest.getContentLengthLong() > this.maxSize) {
				((HttpServletResponse) response).sendError(HTTP_STATUS_PAYLOAD_TOO_LARGE);
				return;
			}
			try {
				reqWrapper = new BufferedRequestWrapper((HttpServletRequest) request, this.maxSize);
			}catch(MaxSizeExceededException e) {
				((HttpServletResponse) response).sendError(HTTP_STATUS_PAYLOAD_TOO_LARGE);
				return;
			}
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


	private static class MaxSizeExceededException extends IOException {
		private static final long serialVersionUID = 1L;
		MaxSizeExceededException(long maxSize) {
			super("Body size exceeds the maximum allowed ("+maxSize+" bytes)");
		}
	}

	private class BufferedRequestWrapper extends HttpServletRequestWrapper {

		private ByteArrayInputStream bais;

		private BufferedServletInputStream bsis;

		private byte[] buffer;

		public BufferedRequestWrapper(HttpServletRequest req, long maxSize) throws IOException {
			super(req);
			if(maxSize < 0) {
				try {
					this.buffer = Utilities.getAsByteArray(req.getInputStream());
				}catch(Exception e) {
					throw new IOException(e);
				}
				return;
			}
			// lettura con verifica della dimensione massima consentita
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			InputStream is = req.getInputStream();
			byte[] buf = new byte[8192];
			long letti = 0;
			int n;
			while((n = is.read(buf)) != -1) {
				letti += n;
				if(letti > maxSize) {
					throw new MaxSizeExceededException(maxSize);
				}
				bout.write(buf, 0, n);
			}
			this.buffer = bout.toByteArray();
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
