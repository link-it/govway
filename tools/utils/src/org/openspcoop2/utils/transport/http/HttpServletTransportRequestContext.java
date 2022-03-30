/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.Credential;
import org.slf4j.Logger;

/**
 * URL Protocol Context
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class HttpServletTransportRequestContext extends org.openspcoop2.utils.transport.TransportRequestContext implements java.io.Serializable {

	// Senno se l'oggetto non e' serializzabile
	private transient HttpServletRequest httpServletRequest;
	
	public HttpServletRequest getHttpServletRequest() {
		return this.httpServletRequest;
	}
	public void updateHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	public HttpServletTransportRequestContext() throws UtilsException{
		super();
	}
	public HttpServletTransportRequestContext(Logger log) throws UtilsException{
		super(log);
	}
	public HttpServletTransportRequestContext(HttpServletRequest req,Logger logCore) throws UtilsException{
		this(req,logCore,false);
	}
	public HttpServletTransportRequestContext(HttpServletRequest req,Logger logCore, boolean debug) throws UtilsException{
		super(logCore);
		
		try {
			
			this.httpServletRequest = req;
			
			// Properties FORM Based
			this.parameters = new HashMap<String, List<String>>();	       
			java.util.Enumeration<?> en = req.getParameterNames();
			while(en.hasMoreElements()){
				String nomeProperty = (String)en.nextElement();
				String [] s = req.getParameterValues(nomeProperty);
				List<String> values = new ArrayList<String>();
				if(s!=null && s.length>0) {
					for (int i = 0; i < s.length; i++) {
						String value = s[i];
						values.add(value);
						//logCore.info("Parameter ["+nomeProperty+"] valore-"+i+" ["+value+"]");
					}
				}
				else {
					//logCore.info("Parameter ["+nomeProperty+"] valore ["+req.getParameter(nomeProperty)+"]");
					values.add(req.getParameter(nomeProperty));
				}
				this.parameters.put(nomeProperty,values);
			}

			// Hedear Trasporto
			this.headers = new HashMap<String, List<String>>();		    
			java.util.Enumeration<?> enTrasporto = req.getHeaderNames();
			while(enTrasporto.hasMoreElements()){
				String nomeHeader = (String)enTrasporto.nextElement();
				Enumeration<String> enValues = req.getHeaders(nomeHeader);
				List<String> values = new ArrayList<String>();
				if(enValues!=null) {
					@SuppressWarnings("unused")
					int i = 0;
					while (enValues.hasMoreElements()) {
						String value = (String) enValues.nextElement();
						values.add(value);
						//logCore.info("Header ["+nomeHeader+"] valore-"+i+" ["+value+"]");
						i++;
					}
				}
				if(values.isEmpty()) {
					//logCore.info("Header ["+nomeHeader+"] valore ["+req.getHeader(nomeHeader)+"]");
					values.add(req.getHeader(nomeHeader));
				}
				this.headers.put(nomeHeader,values);
			}
			
			// Cookies
			this.cookiesValue = new HashMap<String, String>();
			this.cookiesMaxAge = new HashMap<String, Integer>();
			Cookie [] cookies = req.getCookies();
			if(cookies!=null && cookies.length>0) {
				for (Cookie cookie : cookies) {
					String cookieName = cookie.getName();
					String value = URLDecoder.decode(cookie.getValue(), Charset.UTF_8.getValue());
					this.cookiesValue.put(cookieName,value);
					if(cookie.getMaxAge()>0) {
						this.cookiesMaxAge.put(cookieName, cookie.getMaxAge());
					}
				}
			}
			
			this.webContext = req.getContextPath();
			this.requestURI = req.getRequestURI();
			this.requestType = req.getMethod();
			
			this.credential = new HttpServletCredential(req,logCore,debug);
			
			this.source = buildSource(req, this.credential);
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static String buildSource(HttpServletRequest req, Credential credenziali){
		String protocollo = "http";
		if(credenziali.getSubject()!=null)
			protocollo = "https";
		String ip = req.getRemoteAddr();
		String port = ""+req.getRemotePort();
		String user = "";
		if(credenziali.getUsername()!=null || credenziali.getSubject()!=null){
			user = HttpConstants.SEPARATOR_SOURCE;
			if(credenziali.getSubject()!=null)
				user=user+credenziali.getSubject();
			else
				user=user+credenziali.getUsername();
		}
		return protocollo+HttpConstants.SEPARATOR_SOURCE+
				ip+HttpConstants.SEPARATOR_SOURCE+port+user;
	}
	

	@Override
	public String toString() {
		return this.toString("");
	}
	@Override
	public String toString(String prefix) {
		StringBuilder sb = new StringBuilder(super.toString(prefix));
		if(this.httpServletRequest!=null) {
			sb.append("\n").append(prefix).append("httpServletRequest: disponibile");
		}
		if(this.httpServletRequest.getRequestURI()!=null) {
			sb.append("\n").append(prefix).append("httpServletRequest.getRequestURI: ").append(this.httpServletRequest.getRequestURI());
		}
		if(this.httpServletRequest.getRequestURL()!=null) {
			sb.append("\n").append(prefix).append("httpServletRequest.getRequestURL: ").append(this.httpServletRequest.getRequestURL());
		}
		if(this.httpServletRequest.getQueryString()!=null) {
			sb.append("\n").append(prefix).append("httpServletRequest.getQueryString: ").append(this.httpServletRequest.getQueryString());
		}
		return sb.toString();
	}
}
