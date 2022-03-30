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
package org.openspcoop2.utils.service.authentication.provider;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

/**
 * UserDetailsByNameServiceWrapper
 * 
 * Classe da utilizzare per wrappare il bean che accede al db utenti.
 * rispetto a quello di default di spring-security e' stato ridefinito il metodo 'loadUserDetails'
 * per passare al livello sottostante l'intero token di autenticazione invece del solo nome utente.
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UserDetailsByNameServiceWrapper <T extends Authentication> implements AuthenticationUserDetailsService<T>, InitializingBean {
	
	private AuthenticationUserDetailsService<T> authenticationUserDetailsService = null;

	/**
	 * Constructs an empty wrapper for compatibility with Spring Security 2.0.x's method
	 * of using a setter.
	 */
	public UserDetailsByNameServiceWrapper() {
		// constructor for backwards compatibility with 2.0
	}

	/**
	 * Constructs a new wrapper using the supplied
	 * {@link org.springframework.security.core.userdetails.UserDetailsService} as the
	 * service to delegate to.
	 *
	 * @param authenticationUserDetailsService the UserDetailsService to delegate to.
	 */
	public UserDetailsByNameServiceWrapper(final AuthenticationUserDetailsService<T> authenticationUserDetailsService) {
		Assert.notNull(authenticationUserDetailsService, "authenticationUserDetailsService cannot be null.");
		this.authenticationUserDetailsService = authenticationUserDetailsService;
	}

	/**
	 * Check whether all required properties have been set.
	 *
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.authenticationUserDetailsService, "AuthenticationUserDetailsService must be set");
	}

	/**
	 * Get the UserDetails object from the wrapped UserDetailsService implementation
	 */
	@Override
	public UserDetails loadUserDetails(T authentication) throws UsernameNotFoundException {
		return this.authenticationUserDetailsService.loadUserDetails(authentication);
	}

	/**
	 * Set the wrapped UserDetailsService implementation
	 *
	 * @param authenticationUserDetailsService The wrapped UserDetailsService to set
	 */
	public void setAuthenticationUserDetailsService(AuthenticationUserDetailsService<T> authenticationUserDetailsService) {
		this.authenticationUserDetailsService = authenticationUserDetailsService;
	}

}
