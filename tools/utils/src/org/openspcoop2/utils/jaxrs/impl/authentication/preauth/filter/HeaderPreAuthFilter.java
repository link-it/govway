/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
package org.openspcoop2.utils.jaxrs.impl.authentication.preauth.filter;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

/**
 * HeaderPreAuthFilter
 * 
 * Classe per la lettura del principal dell'utenza da un header.
 * Estendere la classe e ridefinire il metodo getPrincipalHeaderName per leggere il nome dell'header da properties.
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HeaderPreAuthFilter extends RequestHeaderAuthenticationFilter {

	public HeaderPreAuthFilter() {
		super();
		String headerAuth = this.getPrincipalHeaderName();
		
		// se ho definito un header name allora lo uso per leggere il principal, altrimenti il filtro usa l'header 'SM_USER'
		if(StringUtils.isNotEmpty(headerAuth))
			this.setPrincipalRequestHeader(headerAuth);
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		return super.getPreAuthenticatedPrincipal(request);
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		return super.getPreAuthenticatedCredentials(request);
	}

	/**
	 * Restituisce il nome dell'header dove e' contenuto il principal da usare
	 * Ridefinire il metodo per leggere il nome dell'header per esempio da file di properties
	 * 
	 * @return PrincipalHeaderName
	 */
	protected String getPrincipalHeaderName() {
		return null;
	}
}
