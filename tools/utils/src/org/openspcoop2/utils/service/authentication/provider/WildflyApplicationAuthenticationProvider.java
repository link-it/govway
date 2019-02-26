/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;


/**
 * WildflyApplicationAuthenticationProvider
 * 
 * Classe che utilizza le configurazioni utenti create tramite wildfly
 * 
 * ...
 * <b:bean id="wildflyApplicationAuthenticationProvider" class="org.openspcoop2.utils.jaxrs.impl.authentication.provider.WildflyApplicationAuthenticationProvider" >
 *     <!-- <b:property name="userDetailsService" ref="userDetailServiceUtenze"/> -->
 * </b:bean>
 * ...
 * <authentication-manager alias="authenticationManager">
 *		<authentication-provider ref="wildflyApplicationAuthenticationProvider"/>
 * </authentication-manager>
 * ...
 * 
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date
 */
/**
 * @author poli
 *
 */
public class WildflyApplicationAuthenticationProvider implements AuthenticationProvider{

	private Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

	private String configDir = "jboss.server.config.dir";
	private String applicationUsersFileName = "application-users.properties";
	private String applicationRolesFileName = "application-roles.properties";
	private String realName = "ApplicationRealm";
	private String hashAlgorithm = "MD5";
	private String hashEncoding = "hex";
	private UserDetailsService userDetailsService;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String username = authentication.getName();
		Object passwordObject = authentication.getCredentials();
		String password = (String) passwordObject;

		if(username==null || password==null) {
			throw new AuthenticationCredentialsNotFoundException("Credentials not found");
		}

		String confDir = System.getProperty(this.configDir);
		if(confDir==null) {
			throw new ProviderNotFoundException("Property '"+this.configDir+"' not found");
		}
		File confDirJBoss = new File(confDir);
		if(!confDirJBoss.exists()) {
			throw new ProviderNotFoundException("File '"+confDirJBoss.getAbsolutePath()+"' not exists");
		}
		if(!confDirJBoss.isDirectory()) {
			throw new ProviderNotFoundException("File '"+confDirJBoss.getAbsolutePath()+"' isn't directory");
		}

		// check utenza da file application-users.properties
		File fUsers = new File(confDirJBoss, this.applicationUsersFileName);
		if(!fUsers.exists()) {
			throw new ProviderNotFoundException("File '"+fUsers.getAbsolutePath()+"' not exists");
		}
		if(!fUsers.canRead()) {
			throw new ProviderNotFoundException("File '"+fUsers.getAbsolutePath()+"' cannot read");
		}
		Properties pUsers = new Properties();
		try (FileInputStream fin = new FileInputStream(fUsers)){
			pUsers.load(fin);
		}catch(Exception e) {
			String msg = "File '"+fUsers.getAbsolutePath()+"' process error: "+e.getMessage();
			this.log.error(msg,e.getMessage());
			throw new ProviderNotFoundException(msg);
		}
		Iterator<?> itUsers = pUsers.keySet().iterator();
		boolean found = false;
		String passwordEncoded = null;
		while (itUsers.hasNext()) {
			String user = (String) itUsers.next();
			passwordEncoded =  pUsers.getProperty(user);
			if(username.equals(user)) {
				found = true;
				break;
			}
		}
		if(!found) {
			if(username==null || password==null) {
				throw new UsernameNotFoundException("Username '"+username+"' not found");
			}
		}

		// Check password
		String clearTextPassword=username+":"+this.realName+":"+password; 
		String hashedPassword=null;
		try {
			hashedPassword=encode(clearTextPassword, this.hashAlgorithm, this.hashEncoding);
		}catch(Exception e) {
			String msg = "Password verifier failed: "+e.getMessage();
			this.log.error(msg,e.getMessage());
			throw new AuthenticationServiceException(msg,e);
		}
		if(!passwordEncoded.equals(hashedPassword)) {
			throw new BadCredentialsException("Bad credentials");
		}

		// check ruoli utenza da file application-roles.properties
		List<GrantedAuthority> roles = new ArrayList<>();
		File fRoles = new File(confDirJBoss, this.applicationRolesFileName);
		if(!fRoles.exists()) {
			throw new ProviderNotFoundException("File '"+fRoles.getAbsolutePath()+"' not exists");
		}
		if(!fRoles.canRead()) {
			throw new ProviderNotFoundException("File '"+fRoles.getAbsolutePath()+"' cannot read");
		}
		Properties pRoles = new Properties();
		try (FileInputStream fin = new FileInputStream(fRoles)){
			pRoles.load(fin);
		}catch(Exception e) {
			String msg = "File '"+fRoles.getAbsolutePath()+"' process error: "+e.getMessage();
			this.log.error(msg,e.getMessage());
			throw new ProviderNotFoundException(msg);
		}
		Iterator<?> itRoles = pRoles.keySet().iterator();
		while (itRoles.hasNext()) {
			String user = (String) itRoles.next();
			if(username.equals(user)) {
				String userRoles =  pRoles.getProperty(user);
				if(userRoles!=null && !"".equals(userRoles)) {
					String [] tmp = userRoles.split(",");
					for (int i = 0; i < tmp.length; i++) {
						GrantedAuthority grant = new SimpleGrantedAuthority(tmp[i].trim());
						roles.add(grant);
					}
				}
				break;
			}
		}

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

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	
	public static final String BASE64_ENCODING = "BASE64";
	public static final String HEX_ENCODING = "HEX";
	private String encode(String password, String hashAlgorithm, String hashEncoding) throws Exception{
		String passwordHash = null;

		byte[] passBytes = password.getBytes();
		
		// calculate the hash and apply the encoding.
		byte[] hash = null;
		try{
			MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
			md.update(passBytes);
			hash = md.digest();
		}catch(Exception e)
		{
			throw new Exception("MessageDigest processing ('"+hashAlgorithm+"') failed: "+e.getMessage(),e);
		}
		if(hashEncoding.equalsIgnoreCase(BASE64_ENCODING))
		{
			passwordHash = Base64Utilities.encodeAsString(hash);
		}
		else if(hashEncoding.equalsIgnoreCase(HEX_ENCODING))
		{
			passwordHash = HexBinaryUtilities.encodeAsString(hash);
		}
		else
		{
			throw new Exception("Unsupported hashAlgorithm '"+hashAlgorithm+"'");
		}

		return passwordHash;
	}
	
	public String getConfigDir() {
		return this.configDir;
	}
	public void setConfigDir(String configDir) {
		this.configDir = configDir;
	}

	public String getApplicationUsersFileName() {
		return this.applicationUsersFileName;
	}
	public void setApplicationUsersFileName(String applicationUsersFileName) {
		this.applicationUsersFileName = applicationUsersFileName;
	}

	public String getApplicationRolesFileName() {
		return this.applicationRolesFileName;
	}
	public void setApplicationRolesFileName(String applicationRolesFileName) {
		this.applicationRolesFileName = applicationRolesFileName;
	}

	public String getRealName() {
		return this.realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getHashAlgorithm() {
		return this.hashAlgorithm;
	}
	public void setHashAlgorithm(String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}

	public String getHashEncoding() {
		return this.hashEncoding;
	}
	public void setHashEncoding(String hashEncoding) {
		this.hashEncoding = hashEncoding;
	}
	
    public UserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }
    public void setUserDetailsService(UserDetailsService userDetailsService) {
    	this.userDetailsService = userDetailsService;
    }


}

