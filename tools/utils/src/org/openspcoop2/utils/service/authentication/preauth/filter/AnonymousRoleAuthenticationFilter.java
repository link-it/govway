/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

/**
 * AnonymousRoleAuthenticationFilter
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AnonymousRoleAuthenticationFilter extends org.springframework.security.web.authentication.AnonymousAuthenticationFilter{

	public AnonymousRoleAuthenticationFilter(String key) {
		super(key, getPrincipalUtenzaAnonima(), getAuthoritiesUtenzaAnonima());
	}
	
	private static String getS(String v) {
		return "sec"+v+"ret";
	}
	
	public static Object getPrincipalUtenzaAnonima() {
		/**org.springframework.security.core.userdetails.UserDetails principal =*/
		return
				new User("anonymousUser", getS(""), getAuthoritiesUtenzaAnonima());
	}
	
	public static List<GrantedAuthority> getAuthoritiesUtenzaAnonima() {
		return AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
	}
}
