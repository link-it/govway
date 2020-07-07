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

package org.openspcoop2.core.monitor.rs.server.config;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.dao.DBLoginDAO;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * AuthenticationProvider
 * 
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider{

	private Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

	private UserDetailsService userDetailsService;
	private String operatorRoleName = "operatore";	
	private String diagnosticRoleName = "diagnostica";	
	private String reportRoleName = "reportistica";	

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String username = authentication.getName();
		Object passwordObject = authentication.getCredentials();
		String password = (String) passwordObject;

		if(username==null || password==null) {
			throw new AuthenticationCredentialsNotFoundException("Credentials not found");
		}
		
		DBManager dbManager = DBManager.getInstance();
		Connection connection = null;
		try {
			connection = dbManager.getConnectionConfig();
			ServiceManagerProperties smp = dbManager.getServiceManagerPropertiesConfig();
			DBLoginDAO loginService = new DBLoginDAO(connection, true, smp, LoggerProperties.getLoggerDAO());
			
			UserDetailsBean u = null;
			try {
				u = loginService.loadUserByUsername(username, false); // il controllo e' fatto nella acl
			}
			catch(NotFoundException e) {
				throw new UsernameNotFoundException("Username '"+username+"' not found", e);
			}
			catch(Exception e) {
				LoggerProperties.getLoggerCore().error(e.getMessage(),e);
				throw new AuthenticationServiceException("AuthenticationProvider,ricerca utente fallita: "+e.getMessage(),e);
			}
			
			boolean correct = false;
			try {
				loginService.setPasswordManager(ServerProperties.getInstance().getUtenzeCryptConfig());
				correct = loginService.login(username, password);
			}catch(Exception e) {
				LoggerProperties.getLoggerCore().error(e.getMessage(),e);
				throw new AuthenticationServiceException("Inizializzazione AuthenticationProvider fallita: "+e.getMessage(),e);
			}
			if(!correct) {
				throw new BadCredentialsException("Bad credentials");
			}

			List<GrantedAuthority> roles = new ArrayList<>();
			if(u.getUtente()!=null && u.getUtente().getPermessi()!=null) {
				if(u.getUtente().getPermessi().isDiagnostica()) {
					GrantedAuthority grant = new SimpleGrantedAuthority(this.diagnosticRoleName);
					roles.add(grant);
				}
				if(u.getUtente().getPermessi().isReportistica()) {
					GrantedAuthority grant = new SimpleGrantedAuthority(this.reportRoleName);
					roles.add(grant);
				}
				if(roles.size()==2) {
					// operatore se li ha tutti e due
					GrantedAuthority grant = new SimpleGrantedAuthority(this.operatorRoleName);
					roles.add(grant);
				}
			}
			// vi sono le acl per questo
//			else {
//				throw new BadCredentialsException(LoginCostanti.MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE);
//			}
			
			
			// Wrap in UsernamePasswordAuthenticationToken
			UsernamePasswordAuthenticationToken userAuth = null;
			if(this.userDetailsService!=null) {
				try {
		            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
		            userAuth = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
			    }catch(UsernameNotFoundException e){
			    	String msg = "User '"+username+"' unknown: "+e.getMessage();
			    	this.log.debug(msg,e);
			    	throw new BadCredentialsException(msg,e);
			    }
			}
			else {	
				User user = new User(username, "secret", true, true, true, true, roles);
				userAuth = new UsernamePasswordAuthenticationToken(user, "secret", user.getAuthorities());
			}
			userAuth.setDetails(authentication.getDetails());
			return userAuth;
		}
		finally {
			dbManager.releaseConnectionConfig(connection);
		}

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	public String getOperatorRoleName() {
		return this.operatorRoleName;
	}
	public void setOperatorRoleName(String operatorRoleName) {
		this.operatorRoleName = operatorRoleName;
	}

	public String getDiagnosticRoleName() {
		return this.diagnosticRoleName;
	}
	public void setDiagnosticRoleName(String diagnosticRoleName) {
		this.diagnosticRoleName = diagnosticRoleName;
	}

	public String getReportRoleName() {
		return this.reportRoleName;
	}
	public void setReportRoleName(String reportRoleName) {
		this.reportRoleName = reportRoleName;
	}

}

