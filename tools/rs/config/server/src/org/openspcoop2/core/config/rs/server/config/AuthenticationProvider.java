/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.config.rs.server.config;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.login.LoginHelper;
import org.openspcoop2.web.ctrlstat.servlet.login.LoginTipologia;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;


/**
 * AuthenticationProvider
 * 
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider{

	private Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

	private String configuratorRoleName = "configuratore";	
	
	private static String getS(String v) {
		return "sec"+v+"ret";
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String username = authentication.getName();
		Object passwordObject = authentication.getCredentials();
		String password = (String) passwordObject;

		if(username==null || password==null) {
			throw new AuthenticationCredentialsNotFoundException("Credentials not found");
		}
		
		String tipoProtocollo = null;
		ControlStationCore core = null;
		UtentiCore utentiCore = null;
		try {
			tipoProtocollo = BaseHelper.tipoProtocolloFromProfilo.get(Helper.getProfiloDefault());
			core = new ControlStationCore(true, ServerProperties.getInstance().getConfDirectory() ,tipoProtocollo);
			utentiCore = new UtentiCore(core);
		}catch(Exception e) {
			throw new AuthenticationServiceException("Inizializzazione AuthenticationProvider fallita: "+e.getMessage(),e);
		}
		
		StringBuilder denyReason = new StringBuilder();
		org.openspcoop2.web.lib.users.dao.User u = null;
		try {
			u = LoginHelper.loginCheckData(this.log, utentiCore, LoginTipologia.WITH_PASSWORD, username, password, denyReason);
		}catch(Exception e) {
			throw new AuthenticationServiceException("AuthenticationProvider,ricerca utente fallita: "+e.getMessage(),e);
		}
		
		if(u==null) {
			throw new BadCredentialsException("Bad credentials");
		}
		
		
		List<GrantedAuthority> roles = new ArrayList<>();
		if(u.getPermessi()!=null && u.getPermessi().isServizi()) {
			GrantedAuthority grant = new SimpleGrantedAuthority(this.configuratorRoleName);
			roles.add(grant);
		}
		// vi sono le acl per questo
		/**else {
			throw new BadCredentialsException(LoginCostanti.MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE);
		}*/
	
		// Wrap in UsernamePasswordAuthenticationToken
		User user = new User(username, getS(""), true, true, true, true, roles);
		UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(user, getS(""), user.getAuthorities());
		userAuth.setDetails(authentication.getDetails());
		return userAuth;
		
        

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	public String getConfiguratorRoleName() {
		return this.configuratorRoleName;
	}
	public void setConfiguratorRoleName(String configuratorRoleName) {
		this.configuratorRoleName = configuratorRoleName;
	}
}

