/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.utils;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.exception.NotAuthorizedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.Identity;

/**
 * AuthorizationManager
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuthorizationManager {
	
	private boolean basic = false;
	private boolean ssl = false;
	private boolean principal = false;
	
	private boolean authorizedRequired;

	private boolean identifiedMethodOrRelation = true;
	
	private Properties basicAuthorizedIdentitiesSearch = null;
	private Properties basicAuthorizedIdentitiesCRUD = null;
	
	private Properties sslAuthorizedIdentitiesSearch = null;
	private Properties sslAuthorizedIdentitiesCRUD = null;
	
	private Properties principalAuthorizedIdentitiesSearch = null;
	private Properties principalAuthorizedIdentitiesCRUD = null;
	
	public AuthorizationManager(Properties serverProperties) throws ServiceException{
		this(new ServerProperties(serverProperties));
	}
	public AuthorizationManager(ServerProperties serverProperties) throws ServiceException{
		
		String identifiedMethodOrRelationTmp = serverProperties.getProperty("identifiedMethod.orRelation", false);
		if(identifiedMethodOrRelationTmp!=null){
			this.identifiedMethodOrRelation = Boolean.parseBoolean(identifiedMethodOrRelationTmp);
		}
		
		String authorizationMethodEnabled = serverProperties.getProperty("identifiedMethod", false);
		if(authorizationMethodEnabled!=null){
			String [] split = authorizationMethodEnabled.split(",");
			for (int i = 0; i < split.length; i++) {
				String method = split[i].trim();
				if("basic".equalsIgnoreCase(method)){
					this.basic = true;
					
					Properties authBasicAll = serverProperties.readProperties("authorized.basic.");
					this.basicAuthorizedIdentitiesSearch = new Properties();
					this.basicAuthorizedIdentitiesSearch.putAll(authBasicAll);
					this.basicAuthorizedIdentitiesCRUD = new Properties();
					this.basicAuthorizedIdentitiesCRUD.putAll(authBasicAll);
					
					Properties authBasicSearch = serverProperties.readProperties("authorized.search.basic.");
					if(authBasicSearch.size()>0){
						Enumeration<?> en = authBasicSearch.keys();
						while (en.hasMoreElements()) {
							String key = (String) en.nextElement();
							if(this.basicAuthorizedIdentitiesSearch.containsKey(key)){
								throw new ServiceException("Property ["+key+"] already defined for basic authentication of search method (see authorized.basic.* and authorized.search.basic.*)");
							}
							this.basicAuthorizedIdentitiesSearch.put(key, authBasicSearch.get(key));
						}
					}
					
					Properties authBasicCRUD = serverProperties.readProperties("authorized.crud.basic.");
					if(authBasicCRUD.size()>0){
						Enumeration<?> en = authBasicCRUD.keys();
						while (en.hasMoreElements()) {
							String key = (String) en.nextElement();
							if(this.basicAuthorizedIdentitiesCRUD.containsKey(key)){
								throw new ServiceException("Property ["+key+"] already defined for basic authentication of crud method (see authorized.basic.* and authorized.crud.basic.*)");
							}
							this.basicAuthorizedIdentitiesCRUD.put(key, authBasicCRUD.get(key));
						}
					}
					
					if(this.basicAuthorizedIdentitiesCRUD.size()<=0 && this.basicAuthorizedIdentitiesSearch.size()<=0){
						throw new ServiceException("Required authorizedMethod ["+method+"] without a list of authorized users");
					}
				}
				else if("ssl".equalsIgnoreCase(method)){
					this.ssl = true;
					
					Properties authSslAll = serverProperties.readProperties("authorized.ssl.");
					this.sslAuthorizedIdentitiesSearch = new Properties();
					this.sslAuthorizedIdentitiesSearch.putAll(authSslAll);
					this.sslAuthorizedIdentitiesCRUD = new Properties();
					this.sslAuthorizedIdentitiesCRUD.putAll(authSslAll);
					
					Properties authSslSearch = serverProperties.readProperties("authorized.search.ssl.");
					if(authSslSearch.size()>0){
						Enumeration<?> en = authSslSearch.keys();
						while (en.hasMoreElements()) {
							String key = (String) en.nextElement();
							if(this.sslAuthorizedIdentitiesSearch.containsKey(key)){
								throw new ServiceException("Property ["+key+"] already defined for ssl authentication of search method (see authorized.ssl.* and authorized.search.ssl.*)");
							}
							this.sslAuthorizedIdentitiesSearch.put(key, authSslSearch.get(key));
						}
					}
					
					Properties authSslCRUD = serverProperties.readProperties("authorized.crud.ssl.");
					if(authSslCRUD.size()>0){
						Enumeration<?> en = authSslCRUD.keys();
						while (en.hasMoreElements()) {
							String key = (String) en.nextElement();
							if(this.sslAuthorizedIdentitiesCRUD.containsKey(key)){
								throw new ServiceException("Property ["+key+"] already defined for ssl authentication of crud method (see authorized.ssl.* and authorized.crud.ssl.*)");
							}
							this.sslAuthorizedIdentitiesCRUD.put(key, authSslCRUD.get(key));
						}
					}
					
					if(this.sslAuthorizedIdentitiesCRUD.size()<=0 && this.sslAuthorizedIdentitiesSearch.size()<=0){
						throw new ServiceException("Required authorizedMethod ["+method+"] without a list of authorized users");
					}
				}
				else if("principal".equalsIgnoreCase(method)){
					this.principal = true;
					
					Properties authPrincipalAll = serverProperties.readProperties("authorized.principal.");
					this.principalAuthorizedIdentitiesSearch = new Properties();
					this.principalAuthorizedIdentitiesSearch.putAll(authPrincipalAll);
					this.principalAuthorizedIdentitiesCRUD = new Properties();
					this.principalAuthorizedIdentitiesCRUD.putAll(authPrincipalAll);
					
					Properties authPrincipalSearch = serverProperties.readProperties("authorized.search.principal.");
					if(authPrincipalSearch.size()>0){
						Enumeration<?> en = authPrincipalSearch.keys();
						while (en.hasMoreElements()) {
							String key = (String) en.nextElement();
							if(this.principalAuthorizedIdentitiesSearch.containsKey(key)){
								throw new ServiceException("Property ["+key+"] already defined for principal authentication of search method (see authorized.principal.* and authorized.search.principal.*)");
							}
							this.principalAuthorizedIdentitiesSearch.put(key, authPrincipalSearch.get(key));
						}
					}
					
					Properties authPrincipalCRUD = serverProperties.readProperties("authorized.crud.principal.");
					if(authPrincipalCRUD.size()>0){
						Enumeration<?> en = authPrincipalCRUD.keys();
						while (en.hasMoreElements()) {
							String key = (String) en.nextElement();
							if(this.principalAuthorizedIdentitiesCRUD.containsKey(key)){
								throw new ServiceException("Property ["+key+"] already defined for principal authentication of crud method (see authorized.principal.* and authorized.crud.principal.*)");
							}
							this.principalAuthorizedIdentitiesCRUD.put(key, authPrincipalCRUD.get(key));
						}
					}
					
					if(this.principalAuthorizedIdentitiesCRUD.size()<=0 && this.principalAuthorizedIdentitiesSearch.size()<=0){
						throw new ServiceException("Required authorizedMethod ["+method+"] without a list of authorized users");
					}
				}
				else{
					throw new ServiceException("AuthorizedMethod ["+method+"] unknow");
				}
			}
		}
		
		this.authorizedRequired = this.basic || this.ssl || this.principal;
	}

	private void logError(Logger log,String message,StringBuffer bf){
		if(bf.length()>0){
			bf.append("\n");
		}
		bf.append(message);
		log.error(message);
	}
	
	public void authorize(HttpServletRequest httpServletRequest,Logger log,boolean searchMethod) throws NotAuthorizedException{
		
		if(this.authorizedRequired==false){
			return;
		}
		
		Identity identity = new Identity(httpServletRequest);
		StringBuffer bf = new StringBuffer();
		
		boolean basicOk = false;
		if(this.basic){
			String username = identity.getUsername();
			String password = identity.getPassword();
			if(username!=null){
				if(password!=null){
					boolean isRegistered = false;
					String passwordRegistrata = null;
					if(searchMethod){
						isRegistered = this.basicAuthorizedIdentitiesSearch.containsKey(username);
						if(isRegistered){
							passwordRegistrata = this.basicAuthorizedIdentitiesSearch.getProperty(username);
						}
					}
					else{
						isRegistered = this.basicAuthorizedIdentitiesCRUD.containsKey(username);
						if(isRegistered){
							passwordRegistrata = this.basicAuthorizedIdentitiesCRUD.getProperty(username);
						}
					}
					if(isRegistered){
						if(password.equals(passwordRegistrata)){
							log.debug("Detected in the http request a credential basic (username:"+username+") that identifies an authorized user");
							basicOk = true;
						}else{
							logError(log, "Detected in the http request a credential basic (username:"+username
									+") that contains a wrong password ["+password+"]", bf);
						}
					}else{
						logError(log, "Detected in the http request a credential basic (username:"+username+" password"+password
								+") not authorized", bf);
					}
				}else{
					logError(log, "Detected in the http request a credential basic not usable (username:"+username
							+"), password not defined???", bf);
				}
			}
		}
		
		boolean sslOk = false;
		if(this.ssl){
			String subject = identity.getSubject();
			if(subject!=null){
				boolean isRegistered = false;
				if(searchMethod){
					isRegistered = this.sslAuthorizedIdentitiesSearch.containsValue(subject);
				}
				else{
					isRegistered = this.sslAuthorizedIdentitiesCRUD.containsValue(subject);
				}
				if(isRegistered){
					log.debug("Detected in the http request a credential ssl (subject:"+subject+") that identifies an authorized user");
					sslOk = true;
				}else{
					logError(log, "Detected in the http request a credential ssl (subject:"+subject+") not authorized", bf);
				}
			}
		}
		
		boolean principalOk = false;
		if(this.principal){
			String principalId = identity.getPrincipal();
			if(principalId!=null){
				boolean isRegistered = false;
				if(searchMethod){
					isRegistered = this.principalAuthorizedIdentitiesSearch.containsValue(principalId);
				}
				else{
					isRegistered = this.principalAuthorizedIdentitiesCRUD.containsValue(principalId);
				}
				if(isRegistered){
					log.debug("Detected in the http request a credential principal (id:"+principalId+") that identifies an authorized user");
					principalOk = true;
				}else{
					logError(log, "Detected in the http request a credential principal (id:"+principalId+") not authorized", bf);
				}
			}
		}
		
		if(this.identifiedMethodOrRelation){
			if( !basicOk && !sslOk && !principalOk){
				if(bf.length()>0){
					throw new NotAuthorizedException(bf.toString());
				}
				else{
					throw new NotAuthorizedException("No credentials found in the request");
				}
			}
		}
		else{
			if(this.basic && !basicOk){
				if(bf.length()>0){
					throw new NotAuthorizedException(bf.toString());
				}
				else{
					throw new NotAuthorizedException("No basic credentials found in the request");
				}
			}
			if(this.ssl && !sslOk){
				if(bf.length()>0){
					throw new NotAuthorizedException(bf.toString());
				}
				else{
					throw new NotAuthorizedException("No ssl credentials found in the request");
				}
			}
			if(this.principal && !principalOk){
				if(bf.length()>0){
					throw new NotAuthorizedException(bf.toString());
				}
				else{
					throw new NotAuthorizedException("No principal credentials found in the request");
				}
			}
		}
		
	}
	
}
