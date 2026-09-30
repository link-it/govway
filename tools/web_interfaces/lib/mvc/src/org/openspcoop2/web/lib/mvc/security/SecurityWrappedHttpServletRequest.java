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

package org.openspcoop2.web.lib.mvc.security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ReadListener;
import jakarta.servlet.RequestDispatcher;
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

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.security.exception.ValidationException;
import org.slf4j.Logger;

/**
 * SecurityWrappedHttpServletRequest
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityWrappedHttpServletRequest extends HttpServletRequestWrapper implements HttpServletRequest {
	
	private Logger log;
	private SecurityProperties sc;
	private Validatore validator;

	private Integer headerNameMaxLength = 256;
	private Integer queryParamNameMaxLength = 256;

	private static String PREFIX_ERROR_PARAMETER = "Il valore del parametro [";
	
	/** Indica se i parametri delle richieste 'multipart/form-data' devono essere letti dal body e validati */
	private boolean gestioneMultipart = false;
	/** Body della richiesta multipart, letto una sola volta e restituito ad ogni invocazione di getInputStream() */
	private byte[] multipartBody = null;
	/** Parametri testuali (parti prive di filename) della richiesta multipart */
	private Map<String, List<String>> multipartParameters = null;
	private boolean multipartParametersRead = false;
	
	public SecurityWrappedHttpServletRequest(HttpServletRequest httpServletRequest, Logger log, boolean gestioneMultipart) {
		this(httpServletRequest, log);
		this.gestioneMultipart = gestioneMultipart;
	}
	
	public SecurityWrappedHttpServletRequest(HttpServletRequest httpServletRequest, Logger log) {
		super(httpServletRequest);
		this.log = log;
		this.sc = SecurityProperties.getInstance();
		this.validator = Validatore.getInstance();

		try {
			this.headerNameMaxLength = this.sc.getIntProp(Costanti.REQUEST_HEADER_NAME_MAX_LENGTH);
			this.queryParamNameMaxLength = this.sc.getIntProp(Costanti.REQUEST_QUERY_PARAM_NAME_MAX_LENGTH);
		} catch (UtilsException e) {
			this.log.error("Errore durante la lettura delle properties: " + e.getMessage(),e);
		}
	}
	
    private HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest)super.getRequest();
    }

	@Override
	public int getContentLength() {
		return this.getHttpServletRequest().getContentLength();
	}
	
	@Override
	public String getContentType() {
		return this.getHttpServletRequest().getContentType();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if(this.isMultipart()) {
			return new BufferedServletInputStream(this.getMultipartBody());
		}
		return this.getHttpServletRequest().getInputStream();
	}
	
	@Override
	public BufferedReader getReader() throws IOException {
		if(this.isMultipart()) {
			String encoding = this.getHttpServletRequest().getCharacterEncoding();
			Charset charset = encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;
			return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.getMultipartBody()), charset));
		}
		return this.getHttpServletRequest().getReader();
	}
	
	
	/* **** Gestione richieste multipart **** */
	
	/**
	 * Le richieste 'multipart/form-data' (form con campi di tipo file) non vengono analizzate dal container:
	 * i parametri inviati nel body non sarebbero quindi visibili, e di conseguenza neanche validati, tramite
	 * la request originale. Il body viene letto una sola volta ed i parametri testuali in esso contenuti vengono
	 * sottoposti alle stesse regole di sanificazione e validazione previste per gli altri parametri.
	 * Il contenuto dei file non viene invece restituito come parametro testuale: viene letto come binario
	 * dagli helper, tramite getInputStream().
	 */
	private boolean isMultipart() {
		return this.gestioneMultipart && FormDataMultipartUtils.isMultipartFormData(this.getHttpServletRequest().getContentType());
	}
	
	private byte[] getMultipartBody() throws IOException {
		if(this.multipartBody == null) {
			this.multipartBody = this.getHttpServletRequest().getInputStream().readAllBytes();
		}
		return this.multipartBody;
	}
	
	private Map<String, List<String>> getMultipartParameters() {
		if(!this.multipartParametersRead) {
			this.multipartParametersRead = true;
			if(this.isMultipart()) {
				try {
					this.multipartParameters = FormDataMultipartUtils.readTextParameters(this.getMultipartBody(), this.getHttpServletRequest().getContentType());
				} catch (Exception e) {
					this.log.error("Errore durante la lettura dei parametri della richiesta multipart: " + e.getMessage(),e);
				}
			}
		}
		return this.multipartParameters;
	}
	
	/** Valore non validato del parametro. Per le richieste multipart ha precedenza il valore presente nel body, come per la lettura effettuata dagli helper */
	private String getRawParameter(String key) {
		Map<String, List<String>> map = this.getMultipartParameters();
		if(map != null) {
			List<String> values = map.get(key);
			if(values != null && !values.isEmpty()) {
				return values.get(0);
			}
		}
		return this.getHttpServletRequest().getParameter(key);
	}
	
	private String[] getRawParameterValues(String key) {
		String[] values = this.getHttpServletRequest().getParameterValues(key);
		Map<String, List<String>> map = this.getMultipartParameters();
		List<String> multipartValues = map != null ? map.get(key) : null;
		if(multipartValues == null || multipartValues.isEmpty()) {
			return values;
		}
		List<String> l = new ArrayList<>(multipartValues);
		if(values != null) {
			l.addAll(java.util.Arrays.asList(values));
		}
		return l.toArray(new String[l.size()]);
	}
	
	private Map<String, String[]> getRawParameterMap() {
		Map<String, List<String>> map = this.getMultipartParameters();
		if(map == null || map.isEmpty()) {
			return this.getHttpServletRequest().getParameterMap();
		}
		Map<String, String[]> rawMap = new LinkedHashMap<>(this.getHttpServletRequest().getParameterMap());
		for (String name : map.keySet()) {
			rawMap.put(name, this.getRawParameterValues(name));
		}
		return rawMap;
	}
	
	private List<String> getRawParameterNames() {
		Set<String> names = new LinkedHashSet<>();
		Enumeration<String> en = this.getHttpServletRequest().getParameterNames();
		while (en.hasMoreElements()) {
			names.add(en.nextElement());
		}
		Map<String, List<String>> map = this.getMultipartParameters();
		if(map != null) {
			names.addAll(map.keySet());
		}
		return new ArrayList<>(names);
	}
	
	private boolean usaValidazioneTextArea(String key) {
		return ServletUtils.usaValidazioneTextAreaByIdentificativi(this.getRawParameter(org.openspcoop2.web.lib.mvc.Costanti.PARAMETRO_IDENTIFICATIVI_TEXT_AREA), key);
	}
	private boolean usaValidazioneTextAreaSingleLine(String key) {
		return ServletUtils.usaValidazioneTextAreaSingleLineByIdentificativi(this.getRawParameter(org.openspcoop2.web.lib.mvc.Costanti.PARAMETRO_IDENTIFICATIVI_TEXT_AREA_SINGLE_LINE), key);
	}
	private boolean usaValidazionePassword(String key) {
		return ServletUtils.usaValidazionePasswordByIdentificativi(this.getRawParameter(org.openspcoop2.web.lib.mvc.Costanti.PARAMETRO_IDENTIFICATIVI_PS), key);
	}
	
	private static class BufferedServletInputStream extends ServletInputStream {
		private final ByteArrayInputStream bin;
		BufferedServletInputStream(byte[] content) {
			this.bin = new ByteArrayInputStream(content);
		}
		@Override
		public int read() {
			return this.bin.read();
		}
		@Override
		public int read(byte[] b, int off, int len) {
			return this.bin.read(b, off, len);
		}
		@Override
		public int available() {
			return this.bin.available();
		}
		@Override
		public boolean isFinished() {
			return this.bin.available() <= 0;
		}
		@Override
		public boolean isReady() {
			return true;
		}
		@Override
		public void setReadListener(ReadListener readListener) {
			throw new UnsupportedOperationException("Non supportato per una richiesta multipart già letta");
		}
	}
	
	
	/* **** Accesso ai parametri **** */
	
	public String getOriginalParameter(String key) {
		String value = this.getRawParameter(key);
		boolean skipSanitize = this.usaValidazioneTextArea(key) || this.usaValidazionePassword(key);
		return this.validator.getParametroSanificato(value, skipSanitize);
	}

	@Override
	public String getParameter(String key) {
		String val = this.getRawParameter(key);
		if(val != null) {
			try {
				boolean usaValidazioneTextArea = this.usaValidazioneTextArea(key);
				boolean usaValidazionePassword = this.usaValidazionePassword(key);
				boolean usaValidazioneTextAreaSingleLine = this.usaValidazioneTextAreaSingleLine(key);
				boolean skipSanitize = usaValidazioneTextArea || usaValidazionePassword || usaValidazioneTextAreaSingleLine;
				val = this.validator.getParametroSanificato(val, skipSanitize);
				String pattern;
				boolean checkSqlInjection;
				if(usaValidazionePassword) {
					pattern = Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE_PASSWORD;
					checkSqlInjection = false;
				} else if(usaValidazioneTextAreaSingleLine) {
					pattern = Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE_TEXT_AREA_SINGLE_LINE;
					checkSqlInjection = false;
				} else if(usaValidazioneTextArea) {
					pattern = Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE_TEXT_AREA;
					checkSqlInjection = false;
				} else {
					pattern = Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE;
					checkSqlInjection = true;
				}
				return this.validator.validate(PREFIX_ERROR_PARAMETER + key + "]:["+val+"]", val, null, true, checkSqlInjection, pattern);
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: {}", e.getMessage(),e);
				return "";
			}
		}
		return null;
	}

	@Override
	public Map<java.lang.String,java.lang.String[]> getParameterMap() {
		Map<String,String[]> map = this.getRawParameterMap();
		Map<String,String[]> cleanMap = new HashMap<>();
		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			try {
				String name = entry.getKey();

				boolean usaValidazioneTextArea = this.usaValidazioneTextArea(name);
				boolean usaValidazionePassword = this.usaValidazionePassword(name);
				boolean usaValidazioneTextAreaSingleLine = this.usaValidazioneTextAreaSingleLine(name);
				boolean skipSanitize = usaValidazioneTextArea || usaValidazionePassword || usaValidazioneTextAreaSingleLine;
				String pattern;
				boolean checkSqlInjection;
				if(usaValidazionePassword) {
					pattern = Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE_PASSWORD;
					checkSqlInjection = false;
				} else if(usaValidazioneTextAreaSingleLine) {
					pattern = Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE_TEXT_AREA_SINGLE_LINE;
					checkSqlInjection = false;
				} else if(usaValidazioneTextArea) {
					pattern = Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE_TEXT_AREA;
					checkSqlInjection = false;
				} else {
					pattern = Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE;
					checkSqlInjection = true;
				}

				String cleanName = this.validator.validate("Il nome del parametro [" + name + "]", name, this.queryParamNameMaxLength, true, Costanti.PATTERN_REQUEST_HTTP_PARAMETER_NAME);

				String[] value = entry.getValue();
				String[] cleanValues = new String[value.length];
				for (int j = 0; j < value.length; j++) {
					String val = this.validator.getParametroSanificato(value[j], skipSanitize);
					String cleanValue = this.validator.validate(PREFIX_ERROR_PARAMETER + name + "]:["+val+"]", val, null, true, checkSqlInjection, pattern);
					cleanValues[j] = cleanValue;
				}
				cleanMap.put(cleanName, cleanValues);
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: {}", e.getMessage(),e);
			}
		}
		return cleanMap;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		List<String> v = new ArrayList<>();
		for (String name : this.getRawParameterNames()) {
			try {
				String clean = this.validator.validate("Il nome del parametro [" + name + "]", name, this.queryParamNameMaxLength, true, Costanti.PATTERN_REQUEST_HTTP_PARAMETER_NAME);
				v.add(clean);
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: {}", e.getMessage(),e);
			}
		}
		return Collections.enumeration(v);
	}

	@Override
	public String[] getParameterValues(String arg0) {
		String[] values = this.getRawParameterValues(arg0);
		List<String> newValues;

		if(values == null)
			return values;
		newValues = new ArrayList<>();

		boolean usaValidazioneTextArea = this.usaValidazioneTextArea(arg0);
		boolean usaValidazionePassword = this.usaValidazionePassword(arg0);
		boolean usaValidazioneTextAreaSingleLine = this.usaValidazioneTextAreaSingleLine(arg0);
		boolean skipSanitize = usaValidazioneTextArea || usaValidazionePassword || usaValidazioneTextAreaSingleLine;
		String pattern;
		boolean checkSqlInjection;
		if(usaValidazionePassword) {
			pattern = Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE_PASSWORD;
			checkSqlInjection = false;
		} else if(usaValidazioneTextAreaSingleLine) {
			pattern = Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE_TEXT_AREA_SINGLE_LINE;
			checkSqlInjection = false;
		} else if(usaValidazioneTextArea) {
			pattern = Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE_TEXT_AREA;
			checkSqlInjection = false;
		} else {
			pattern = Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE;
			checkSqlInjection = true;
		}

		for (String value : values) {
			try {
				String val = this.validator.getParametroSanificato(value, skipSanitize);
				newValues.add(this.validator.validate(PREFIX_ERROR_PARAMETER + arg0 + "]:["+val+"]", val, null, true, checkSqlInjection, pattern));
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: {}", e.getMessage(),e);
			}
		}
		return newValues.toArray(new String[newValues.size()]);
	}

	@Override
	public String getRequestURI() {
		return this.getHttpServletRequest().getRequestURI();
	}
		
	@Override
	public String getContextPath() {
		return this.getHttpServletRequest().getContextPath();
	}
	
	@Override
	public String getPathInfo() {
		return this.getHttpServletRequest().getPathInfo();
	}
	
	@Override
	public String getPathTranslated() {
		return this.getHttpServletRequest().getPathTranslated();
	}

	@Override
	public String getQueryString() {
		return this.getHttpServletRequest().getQueryString();
	}

	@Override
	public StringBuffer getRequestURL() {
		return this.getHttpServletRequest().getRequestURL();
	}

	@Override
	public String getServletPath() {
		return this.getHttpServletRequest().getServletPath();
	}

	// jakarta api 5
	public String getRealPath(String arg0) {
		if(arg0!=null) {
			return this.getHttpServletRequest().getContextPath();
		}
		return null;
	}
	
	@Override
	public Object getAttribute(String arg0) {
		return this.getHttpServletRequest().getAttribute(arg0);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return this.getHttpServletRequest().getAttributeNames();
	}

	@Override
	public String getCharacterEncoding() {
		return this.getHttpServletRequest().getCharacterEncoding();
	}

	@Override
	public String getLocalAddr() {
		return this.getHttpServletRequest().getLocalAddr();
	}

	@Override
	public String getLocalName() {
		return this.getHttpServletRequest().getLocalName();
	}

	@Override
	public int getLocalPort() {
		return this.getHttpServletRequest().getLocalPort();
	}

	@Override
	public Locale getLocale() {
		return this.getHttpServletRequest().getLocale();
	}

	@Override
	public Enumeration<java.util.Locale> getLocales() {
		return this.getHttpServletRequest().getLocales();
	}

	@Override
	public String getProtocol() {
		return this.getHttpServletRequest().getProtocol();
	}

	@Override
	public String getRemoteAddr() {
		return this.getHttpServletRequest().getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return this.getHttpServletRequest().getRemoteHost();
	}

	@Override
	public int getRemotePort() {
		return this.getHttpServletRequest().getRemotePort();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return this.getHttpServletRequest().getRequestDispatcher(arg0);
	}

	@Override
	public String getScheme() {
		return this.getHttpServletRequest().getScheme();
	}

	@Override
	public String getServerName() {
		return this.getHttpServletRequest().getServerName();
	}

	@Override
	public int getServerPort() {
		return this.getHttpServletRequest().getServerPort();
	}

	@Override
	public boolean isSecure() {
		return this.getHttpServletRequest().isSecure();
	}

	@Override
	public void removeAttribute(String arg0) {
		this.getHttpServletRequest().removeAttribute(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		this.getHttpServletRequest().setAttribute(arg0,arg1);
	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		this.getHttpServletRequest().setCharacterEncoding(arg0);
	}

	@Override
	public String getAuthType() {
		return this.getHttpServletRequest().getAuthType();
	}

	@Override
	public Cookie[] getCookies() {
		Cookie[] cookies = this.getHttpServletRequest().getCookies();
        if (cookies == null) return new Cookie[0];
        
        List<Cookie> newCookies = new ArrayList<>();
        for (Cookie c : cookies) {
            // build a new clean cookie
            try {
                // get data from original cookie
                String name = this.validator.validate("Il nome del Cookie [" + c.getName()+ "]", c.getName(), this.headerNameMaxLength, false, Costanti.PATTERN_HTTP_COOKIE_NAME);
                String value = this.validator.validate("Il valore del Cookie [" + c.getName()+ "]:["+c.getValue()+"]", c.getValue(), null, true, Costanti.PATTERN_HTTP_COOKIE_VALUE);
                int maxAge = c.getMaxAge();
                String domain = c.getDomain();
                String path = c.getPath();

                Cookie n = new Cookie(name, value);
                n.setMaxAge(maxAge);

                if (domain != null) {
                    n.setDomain(this.validator.validate("Il domain del Cookie [" + c.getName()+ "]:["+domain+"]", domain, null, false, Costanti.PATTERN_RESPONSE_HTTP_HEADER_VALUE));
                }
                if (path != null) {
                    n.setPath(this.validator.validate("Il path del Cookie [" + c.getName()+ "]:["+path+"]", path, null, false, Costanti.PATTERN_RESPONSE_HTTP_HEADER_VALUE));
                }
                newCookies.add(n);
            } catch (ValidationException e) {
                this.log.warn("Ignoro cookie malformato: {}={}", c.getName(), c.getValue(), e);
            }
        }
        return newCookies.toArray(new Cookie[newCookies.size()]);
	}

	@Override
	public long getDateHeader(String arg0) {
		return this.getHttpServletRequest().getDateHeader(arg0);
	}

	@Override
	public String getHeader(String name) {
		String value = this.getHttpServletRequest().getHeader(name);

		if(value != null) {
			try {
				value = this.validator.getParametroSanificato(value, false);
				return this.validator.validate("Il valore dell'Header [" + name+ "]:["+value+"]", value, null, true, Costanti.PATTERN_REQUEST_HTTP_HEADER_VALUE);
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: {}", e.getMessage(),e);
				return "";
			}
		}
		return null;
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		List<String> v = new ArrayList<>();
		Enumeration<String> en = this.getHttpServletRequest().getHeaderNames();

		while (en.hasMoreElements()) {
			try {
				String name = en.nextElement();
				String validValue = this.validator.validate("Il nome dell'Header [" + name+ "]", name, null, true, Costanti.PATTERN_REQUEST_HTTP_HEADER_NAME);
				v.add(validValue);
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: {}", e.getMessage(),e);
			}
		}
		return Collections.enumeration(v);
	}

	@Override
	public Enumeration<String> getHeaders(String arg0) {
		List<String> v = new ArrayList<>();
		Enumeration<String> en = this.getHttpServletRequest().getHeaders(arg0);

		while (en.hasMoreElements()) {
			try {
				String value = en.nextElement();
				value = this.validator.getParametroSanificato(value, false);
				v.add(this.validator.validate("Il valore dell'Header [" + arg0+ "]:["+value+"]", value, null, true, Costanti.PATTERN_REQUEST_HTTP_HEADER_VALUE));
			} catch (ValidationException e) {
				this.log.warn("Errore di validazione: {}", e.getMessage(),e);
			}
		}
		return Collections.enumeration(v);
	}

	@Override
	public int getIntHeader(String arg0) {
		return this.getHttpServletRequest().getIntHeader(arg0);
	}

	@Override
	public String getMethod() {
		return this.getHttpServletRequest().getMethod();
	}

	@Override
	public String getRemoteUser() {
		return this.getHttpServletRequest().getRemoteUser();
	}

	@Override
	public String getRequestedSessionId() {
		return this.getHttpServletRequest().getRequestedSessionId();
	}

	@Override
	public HttpSession getSession() {
		return this.getHttpServletRequest().getSession();
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		return this.getHttpServletRequest().getSession(arg0);
	}

	@Override
	public Principal getUserPrincipal() {
		return this.getHttpServletRequest().getUserPrincipal();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return this.getHttpServletRequest().isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return this.getHttpServletRequest().isRequestedSessionIdFromURL();
	}

	// jakarta api 5
	public boolean isRequestedSessionIdFromUrl() {
		return this.getHttpServletRequest().isRequestedSessionIdFromURL();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return this.getHttpServletRequest().isRequestedSessionIdValid();
	}

	@Override
	public boolean isUserInRole(String arg0) {
		return this.getHttpServletRequest().isUserInRole(arg0);
	}

	// v3
	
	@Override
	public AsyncContext getAsyncContext() {
		return this.getHttpServletRequest().getAsyncContext();
	}

	@Override
	public long getContentLengthLong() {
		return this.getHttpServletRequest().getContentLengthLong();
	}

	@Override
	public DispatcherType getDispatcherType() {
		return this.getHttpServletRequest().getDispatcherType();
	}

	@Override
	public ServletContext getServletContext() {
		return this.getHttpServletRequest().getServletContext();
	}

	@Override
	public boolean isAsyncStarted() {
		return this.getHttpServletRequest().isAsyncStarted();
	}

	@Override
	public boolean isAsyncSupported() {
		return this.getHttpServletRequest().isAsyncSupported();
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		return this.getHttpServletRequest().startAsync();
	}

	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
		return this.getHttpServletRequest().startAsync(arg0,arg1);
	}

	@Override
	public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException {
		return this.getHttpServletRequest().authenticate(arg0);
	}

	@Override
	public String changeSessionId() {
		return this.getHttpServletRequest().changeSessionId();
	}

	@Override
	public Part getPart(String arg0) throws IOException, ServletException {
		return this.getHttpServletRequest().getPart(arg0);
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		return this.getHttpServletRequest().getParts();
	}

	@Override
	public void login(String arg0, String arg1) throws ServletException {
		this.getHttpServletRequest().login(arg0,arg1);
	}

	@Override
	public void logout() throws ServletException {
		this.getHttpServletRequest().logout();
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0) throws IOException, ServletException {
		return this.getHttpServletRequest().upgrade(arg0);
	}
	
}
