/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
package org.openspcoop2.utils.credential;

import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;

/**
 * Implementazione dell'interfaccia {@link IPrincipalReader} che utilizza un cookie come input per riconoscere il principal.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CookiePrincipalReader implements IPrincipalReader {

	private Logger log = null;
	
	public static final String COOKIENAME_PROP_NAME = "cookieName";
	private String cookieName = null;

	private Properties paramentriConfigurazione = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CookiePrincipalReader(Logger log){
		this.log = log;
		this.log.debug("CookiePrincipalReader inizializzato.");
	}

	@Override
	public String getPrincipal(HttpServletRequest request) throws PrincipalReaderException {
		this.log.debug("Estrazione principal attraverso il cookie ["+ this.cookieName+"] in corso...");
		String username = "amministratore";
		try{
			Cookie[] cookies = request.getCookies();

			if (cookies != null && cookies.length > 0) {
				for (Cookie cookie : cookies) {
					this.log.debug("Decodifica cookie: ["+cookie.getName()+"]");
					String decode = decode(cookie.getValue());
					this.log.debug("cookie: ["+decode+"]");
				}
			}



			this.log.debug("Username trovato nel cookie: ["+username+"]");

			return username;
		}catch (Exception e) {
			this.log.error("Errore durante la lettura del principal: "+ e.getMessage(),e);
			throw new PrincipalReaderException(e);
		}
	}

	@Override
	public void init(Object... parametri) throws PrincipalReaderException {
		if(parametri != null && parametri.length > 0){
			this.paramentriConfigurazione = (Properties) parametri[0];
			
			this.log.debug("Configurazione: ["+this.paramentriConfigurazione.toString()+"]"); 
			
			if(!this.paramentriConfigurazione.containsKey(COOKIENAME_PROP_NAME))
				throw new PrincipalReaderException("Impossibile utilizzare CookiePrincipalReader: proprieta' '"+COOKIENAME_PROP_NAME+"' non trovata nella configurazione");
			
			this.cookieName = this.paramentriConfigurazione.getProperty(COOKIENAME_PROP_NAME);
		}
		else 
			throw new PrincipalReaderException("Impossibile utilizzare CookiePrincipalReader senza parametri di configurazione");
	}

	/**
	 * @param cookieValue The value of the cookie to decode
	 * @return Returns the decoded string
	 */
	public String decode(String cookieValue) {
		if (cookieValue == null || "".equals(cookieValue)) {
			return null;
		}
		if (!cookieValue.endsWith("=")) {
			cookieValue = padString(cookieValue);
		}
		if (this.log.isDebugEnabled()) {
			this.log.debug("Decoding string: " + cookieValue);
		}
		Base64 base64 = new Base64();
		byte[] encodedBytes = cookieValue.getBytes();
		byte[] decodedBytes = base64.decode(encodedBytes);
		String result = new String(decodedBytes);
		if (this.log.isDebugEnabled()) {
			this.log.debug("Decoded string to: " + result);
		}
		return result;
	}

	private String padString(String value) {
		int mod = value.length() % 4;
		if (mod <= 0) {
			return value;
		}
		int numEqs = 4 - mod;
		if (this.log.isDebugEnabled()) {
			this.log.debug("Padding value with " + numEqs + " = signs");
		}
		for (int i = 0; i < numEqs; i++) {
			value += "=";
		}
		return value;
	}
}
