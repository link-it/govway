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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * CookiePathFilter
 *
 * Filtro che normalizza il path dei cookie al context path dell'applicazione.
 * Utile quando si usa STRICT_SERVLET_COMPLIANCE=true su Tomcat con applicazioni
 * che usano Servlet spec 2.5, dove i cookie potrebbero avere path più restrittivi.
 *
 * Configurazione init-param:
 * - cookiePath.enabled: true/false (default: true)
 * - cookiePath.logCategory: categoria di log (default: classe del filtro)
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CookiePathFilter implements Filter {

	private boolean enabled = true;
	private boolean debug = false;
	private Logger log;

	private static final String CONFIG_ENABLED = "cookiePath.enabled";
	private static final String CONFIG_LOG = "cookiePath.logCategory";
	private static final String CONFIG_DEBUG = "cookiePath.debug";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if(filterConfig != null) {
			String tmp = filterConfig.getInitParameter(CONFIG_ENABLED);
			if(tmp != null && "false".equalsIgnoreCase(tmp.trim())) {
				this.enabled = false;
			}

			tmp = filterConfig.getInitParameter(CONFIG_LOG);
			if(tmp != null) {
				this.log = LoggerWrapperFactory.getLogger(tmp.trim());
			} else {
				this.log = LoggerWrapperFactory.getLogger(CookiePathFilter.class);
			}
			
			tmp = filterConfig.getInitParameter(CONFIG_DEBUG);
			if(tmp != null && "true".equalsIgnoreCase(tmp.trim())) {
				this.debug = true;
			}
		}
	}

	private void log(String msg) {
		if(this.debug && this.log!=null) {
			this.log.debug(msg);
		}
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (!this.enabled) {
			chain.doFilter(request, response);
			return;
		}

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String contextPath = httpRequest.getContextPath();
		if(contextPath == null || contextPath.isEmpty()) {
			contextPath = "/";
		}

		log("[CookiePathFilter] doFilter - URI: " + httpRequest.getRequestURI() + ", contextPath: " + contextPath);

		chain.doFilter(request, new CookiePathResponseWrapper(httpResponse, contextPath, this.log, this.debug));
	}

	@Override
	public void destroy() {
		// nop
	}
}

class CookiePathResponseWrapper extends HttpServletResponseWrapper {

	private static final String PATH_ATTR = "Path=";
	private static final Pattern PATH_PATTERN = Pattern.compile("(;\\s*Path=)([^;\\s]*)", Pattern.CASE_INSENSITIVE);

	private final HttpServletResponse originalResponse;
	private final String contextPath;
	private final Logger log;
	private final boolean debug;
	private boolean cookiesProcessed = false;

	public CookiePathResponseWrapper(HttpServletResponse response, String contextPath, Logger log, boolean debug) {
		super(response);
		this.originalResponse = response;
		this.contextPath = contextPath;
		this.log = log;
		this.debug = debug;
	}

	void log(String msg) {
		if(this.debug && this.log!=null) {
			this.log.debug(msg);
		}
	}
	
	@Override
	public void addCookie(Cookie cookie) {
		log("[CookiePathFilter] addCookie chiamato - cookie: " + (cookie != null ? cookie.getName() : "null"));
		if (cookie != null) {
			String currentPath = cookie.getPath();
			log("[CookiePathFilter] addCookie - nome: " + cookie.getName() + ", path originale: '" + currentPath + "', contextPath: '" + this.contextPath + "'");

			// Se il path non è impostato o è più restrittivo del context path, lo normalizziamo
			if (currentPath == null || currentPath.isEmpty()) {
				cookie.setPath(this.contextPath);
				log("[CookiePathFilter] addCookie - path impostato a: '" + this.contextPath + "'");
			} else if (currentPath.startsWith(this.contextPath) && currentPath.length() > this.contextPath.length()) {
				log("[CookiePathFilter] addCookie - path normalizzato da '" + currentPath + "' a '" + this.contextPath + "'");
				cookie.setPath(this.contextPath);
			} else {
				log("[CookiePathFilter] addCookie - path non modificato: '" + currentPath + "'");
			}
		}
		super.addCookie(cookie);
	}

	@Override
	public void setHeader(String name, String value) {
		if (HttpConstants.SET_COOKIE.equalsIgnoreCase(name) && value != null) {
			String originalValue = value;
			value = normalizeCookiePath(value);
			String msg = "[CookiePathFilter] setHeader Set-Cookie - originale: '" + originalValue + "' -> normalizzato : '" + value + "'";
			log(msg);
		}
		super.setHeader(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		if (HttpConstants.SET_COOKIE.equalsIgnoreCase(name) && value != null) {
			String originalValue = value;
			value = normalizeCookiePath(value);
			log("[CookiePathFilter] addHeader Set-Cookie - originale: '" + originalValue + "' -> normalizzato: '" + value + "'");
		}
		super.addHeader(name, value);
	}

	/**
	 * Processa e riscrive i cookie Set-Cookie prima del commit della risposta
	 */
	private synchronized void processSetCookieHeaders() {
		if (this.cookiesProcessed) {
			return;
		}
		this.cookiesProcessed = true;

		try {
			Collection<String> cookieHeaders = this.originalResponse.getHeaders(HttpConstants.SET_COOKIE);
			if (cookieHeaders == null || cookieHeaders.isEmpty()) {
				log("[CookiePathFilter] processSetCookieHeaders - nessun Set-Cookie trovato");
				return;
			}

			log("[CookiePathFilter] processSetCookieHeaders - trovati " + cookieHeaders.size() + " header Set-Cookie");

			boolean first = true;
			for (String cookieHeader : cookieHeaders) {
				if (cookieHeader == null || cookieHeader.isEmpty()) {
					continue;
				}

				String normalizedHeader = normalizeCookiePath(cookieHeader);
				log("[CookiePathFilter] processSetCookieHeaders - originale: '" + cookieHeader + "' -> normalizzato: '" + normalizedHeader + "'");

				if (first) {
					// Il primo setHeader sovrascrive tutti gli header Set-Cookie esistenti
					this.originalResponse.setHeader(HttpConstants.SET_COOKIE, normalizedHeader);
					first = false;
				} else {
					this.originalResponse.addHeader(HttpConstants.SET_COOKIE, normalizedHeader);
				}
			}
		} catch (Exception e) {
			log("[CookiePathFilter] processSetCookieHeaders - errore: " + e.getMessage());
			/**e.printStackTrace();*/
		}
	}

	@Override
	public void flushBuffer() throws IOException {
		log("[CookiePathFilter] flushBuffer chiamato");
		processSetCookieHeaders();
		super.flushBuffer();
	}

	@Override
	public void sendError(int sc) throws IOException {
		String msg = "[CookiePathFilter] sendError(" + sc + ") chiamato ";
		log(msg);
		processSetCookieHeaders();
		super.sendError(sc);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		log("[CookiePathFilter] sendError(" + sc + ", " + msg + ") chiamato");
		processSetCookieHeaders();
		super.sendError(sc, msg);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		log("[CookiePathFilter] sendRedirect(" + location + ") chiamato");
		processSetCookieHeaders();
		super.sendRedirect(location);
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		log("[CookiePathFilter] getWriter chiamato");
		// Wrappa il writer per processare i cookie alla prima scrittura
		return new CookiePathPrintWriter(super.getWriter(), this);
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		log("[CookiePathFilter] getOutputStream chiamato");
		// Wrappa l'output stream per processare i cookie alla prima scrittura
		return new CookiePathServletOutputStream(super.getOutputStream(), this);
	}

	void triggerCookieProcessing() {
		processSetCookieHeaders();
	}

	private String normalizeCookiePath(String cookieHeader) {
		Matcher matcher = PATH_PATTERN.matcher(cookieHeader);

		if (matcher.find()) {
			String currentPath = matcher.group(2);

			// Rimuovi virgolette se presenti (Tomcat con STRICT_SERVLET_COMPLIANCE le aggiunge)
			String unquotedPath = unquote(currentPath);
			log("[CookiePathFilter] normalizeCookiePath - path: '" + currentPath + "' -> unquoted: '" + unquotedPath + "'");

			// Se il path ha virgolette o è più restrittivo del context path, lo normalizziamo
			boolean hasQuotes = currentPath!=null && !currentPath.equals(unquotedPath);
			boolean isMoreRestrictive = unquotedPath!=null && unquotedPath.startsWith(this.contextPath) && unquotedPath.length() > this.contextPath.length();

			if (hasQuotes || isMoreRestrictive) {
				// Sostituiamo sempre con il context path SENZA virgolette
				String newHeader = matcher.replaceFirst("$1" + Matcher.quoteReplacement(this.contextPath));
				log("[CookiePathFilter] normalizeCookiePath - PATH NORMALIZZATO da '" + currentPath + "' a '" + this.contextPath + "' (hasQuotes=" + hasQuotes + ", isMoreRestrictive=" + isMoreRestrictive + ")");
				return newHeader;
			}
		} else {
			// Nessun Path presente, aggiungiamo il context path
			String newHeader = cookieHeader + "; " + PATH_ATTR + this.contextPath;
			log("[CookiePathFilter] normalizeCookiePath - NESSUN PATH trovato, aggiunto: '" + this.contextPath + "'");
			return newHeader;
		}

		return cookieHeader;
	}

	private String unquote(String value) {
		if (value != null && value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
			return value.substring(1, value.length() - 1);
		}
		return value;
	}
}

class CookiePathServletOutputStream extends ServletOutputStream {
	private final ServletOutputStream wrapped;
	private final CookiePathResponseWrapper responseWrapper;
	private boolean firstWrite = true;
	
	public CookiePathServletOutputStream(ServletOutputStream wrapped, CookiePathResponseWrapper responseWrapper) {
		this.wrapped = wrapped;
		this.responseWrapper = responseWrapper;
	}

	private void checkFirstWrite() {
		if (this.firstWrite) {
			this.firstWrite = false;
			this.responseWrapper.log("[CookiePathFilter] OutputStream - prima scrittura, processo cookie");
			this.responseWrapper.triggerCookieProcessing();
		}
	}

	@Override
	public void write(int b) throws IOException {
		checkFirstWrite();
		this.wrapped.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		checkFirstWrite();
		this.wrapped.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		checkFirstWrite();
		this.wrapped.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		checkFirstWrite();
		this.wrapped.flush();
	}

	@Override
	public void close() throws IOException {
		checkFirstWrite();
		this.wrapped.close();
	}

	@Override
	public boolean isReady() {
		return this.wrapped.isReady();
	}

	@Override
	public void setWriteListener(javax.servlet.WriteListener writeListener) {
		this.wrapped.setWriteListener(writeListener);
	}
}

class CookiePathPrintWriter extends PrintWriter {
	private final CookiePathResponseWrapper responseWrapper;
	private boolean firstWrite = true;

	public CookiePathPrintWriter(PrintWriter wrapped, CookiePathResponseWrapper responseWrapper) {
		super(wrapped);
		this.responseWrapper = responseWrapper;
	}

	private void checkFirstWrite() {
		if (this.firstWrite) {
			this.firstWrite = false;
			this.responseWrapper.log("[CookiePathFilter] PrintWriter - prima scrittura, processo cookie");
			this.responseWrapper.triggerCookieProcessing();
		}
	}

	@Override
	public void write(int c) {
		checkFirstWrite();
		super.write(c);
	}

	@Override
	public void write(char[] buf, int off, int len) {
		checkFirstWrite();
		super.write(buf, off, len);
	}

	@Override
	public void write(String s, int off, int len) {
		checkFirstWrite();
		super.write(s, off, len);
	}

	@Override
	public void flush() {
		checkFirstWrite();
		super.flush();
	}
}
