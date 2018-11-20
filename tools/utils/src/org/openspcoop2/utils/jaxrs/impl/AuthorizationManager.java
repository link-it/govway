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

package org.openspcoop2.utils.jaxrs.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.openspcoop2.utils.jaxrs.fault.FaultCode;
import org.springframework.security.core.GrantedAuthority;

/**
 * AuthorizationManager
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuthorizationManager {

	public static void authorize(ServiceContext context, AuthorizationConfig config) {		
		
		if(config==null || config.getAclList()==null || config.getAclList().isEmpty()) {
			return; // non esistono regole acl
		}
			
		try {
			
			// 1. Cerco acl che ha un match con la richiesta in essere
			String method = context.getServletRequest().getMethod();
			String restPath = context.getRestPath(); // a differenza del context.getServletRequest().getRequestURI(), il path è normalizzato per i parametri dinamici {}
			AuthorizationConfigACL acl = null;
			for (AuthorizationConfigACL check : config.getAclList()) {
				
				// method
				String methodCheck = check.getMethod();
				if(!AuthorizationConfigACL.WILDCARD.equals(methodCheck)) {
					if(!methodCheck.toUpperCase().equals(method.toUpperCase())) {
						continue;
					}
				}
				
				// path
				String pathCheck = check.getPath();
				if(!AuthorizationConfigACL.WILDCARD.equals(pathCheck)) {
					if(pathCheck.endsWith(AuthorizationConfigACL.WILDCARD)) {
						String patchCheckWitoutStar = pathCheck.substring(0, (pathCheck.length()-AuthorizationConfigACL.WILDCARD.length()));
						if(!normalizePath(restPath).startsWith(normalizePath(patchCheckWitoutStar))) {
							continue;
						}
					}
					else {
						if(!normalizePath(restPath).equals(normalizePath(pathCheck))) {
							continue;
						}
					}
				}
				
				acl = check;
				break;
			}
			if(acl==null) {
				throw new Exception("Acl rule match for request not found");
			}
			
			// 2. Verifico acl trovata
			if(acl.getPrincipal().isEmpty() && acl.getRoles().isEmpty()) {
				return; // invocazione pubblica
			}
			if(!acl.getPrincipal().isEmpty()) {
				for (String check : acl.getPrincipal()) {
					if(check.equals(context.getAuthentication().getName())) {
						return; // principal match
					}
				}
			}
			if(!acl.getRoles().isEmpty()) {
				for (String check : acl.getRoles()) {
					boolean found = false;
					if(context.getAuthentication().getAuthorities()!=null) {
						List<String> listaRuoliUtenza = context.getAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
						if(listaRuoliUtenza!=null && !listaRuoliUtenza.isEmpty()) {
							for (String role : listaRuoliUtenza) {
								if(check.equals(role)) {
									if(!acl.isRolesMatchAll()) {
										return; // role match
									}
									else {
										found = true;
										break;
									}
								}			
							}
						}
					}
					if(acl.isRolesMatchAll() && !found) {
						break;
					}
				}
			}
			throw new Exception("Acl rule '"+acl.getName()+"' not satisfied");

		}catch(Exception eAuthorized) {
			FaultCode.AUTORIZZAZIONE.throwException(String.format("L'utente <%s> non è autorizzato ad invocare l'operazione '%s': %s",
					context.getAuthentication().getName(), context.getMethodName(), eAuthorized.getMessage()));
		}
	}
	
	private static String normalizePath(String path) {
		if(path.endsWith("/")) {
			return path.substring(0, path.length()-1);
		}
		else {
			return path;
		}
	}

}
