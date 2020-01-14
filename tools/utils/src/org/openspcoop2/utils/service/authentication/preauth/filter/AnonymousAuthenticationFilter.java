/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.service.authentication.preauth.filter;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * AnonymousAuthenticationFilter
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AnonymousAuthenticationFilter extends org.springframework.security.web.authentication.AnonymousAuthenticationFilter{

	public AnonymousAuthenticationFilter(String key) {
		super(key, getPrincipalUtenzaAnonima(), getAuthoritiesUtenzaAnonima());
	}
	
	public static Object getPrincipalUtenzaAnonima() {
		UserDetails principal = new User("anonymousUser", "SECRET", getAuthoritiesUtenzaAnonima());
		return principal;
	}
	
	public static List<GrantedAuthority> getAuthoritiesUtenzaAnonima() {
		return AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
	}
}
