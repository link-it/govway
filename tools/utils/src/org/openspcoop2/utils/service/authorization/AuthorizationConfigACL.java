/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.service.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.utils.UtilsException;

/**
 * AuthorizationConfigACL
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuthorizationConfigACL {

	public static final String WILDCARD = "*";
	
	public static final String PREFIX = "auth.";
	public static final String SUFFIX_METHOD = ".resource.method";
	public static final String SUFFIX_PATH = ".resource.path";
	public static final String SUFFIX_PRINCIPAL = ".principal";
	public static final String SUFFIX_ROLES = ".roles";
	public static final String SUFFIX_ROLES_MATCH_ALL = ".roles.matchAll";
	
	private String name;
	
	private String method;
	private String path;
	
	private List<String> principal = new ArrayList<>();
	private List<String> roles = new ArrayList<>();
	private boolean rolesMatchAll = false;
	
	public AuthorizationConfigACL(String name) {
		this.name = name;
	}
	public AuthorizationConfigACL(String name, Properties p) throws UtilsException {
		this(name);
		
		this.method = p.getProperty(PREFIX+name+SUFFIX_METHOD);
		if(this.method==null) {
			throw new UtilsException("Method undefined for acl '"+this.name+"'");
		}
		else {
			this.method = this.method.trim();
		}
		
		this.path = p.getProperty(PREFIX+name+SUFFIX_PATH);
		if(this.path==null) {
			throw new UtilsException("Path undefined for acl '"+this.name+"'");
		}
		else {
			this.path = this.path.trim();
		}
		
		String tmp = p.getProperty(PREFIX+name+SUFFIX_PRINCIPAL);
		if(tmp!=null) {
			tmp = tmp.trim();
			if(tmp.contains(",")) {
				String [] tmpList = tmp.split(",");
				for (int i = 0; i < tmpList.length; i++) {
					this.principal.add(tmpList[i].trim());
				}
			}
			else {
				this.principal.add(tmp);
			}
		}
		
		tmp = p.getProperty(PREFIX+name+SUFFIX_ROLES);
		if(tmp!=null) {
			tmp = tmp.trim();
			if(tmp.contains(",")) {
				String [] tmpList = tmp.split(",");
				for (int i = 0; i < tmpList.length; i++) {
					this.roles.add(tmpList[i].trim());
				}
			}
			else {
				this.roles.add(tmp);
			}
		}
		
		if(!this.roles.isEmpty()) {
			tmp = p.getProperty(PREFIX+name+SUFFIX_ROLES_MATCH_ALL);
			if(tmp!=null) {
				tmp = tmp.trim();
				this.rolesMatchAll = Boolean.parseBoolean(tmp);
			}
		}
	}
	
	
	public String getName() {
		return this.name;
	}

	public String getMethod() {
		return this.method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getPath() {
		return this.path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public List<String> getPrincipal() {
		return this.principal;
	}
	public void addPrincipal(String principal) {
		this.principal.add(principal);
	}
	
	public List<String> getRoles() {
		return this.roles;
	}
	public void addRole(String role) {
		this.roles.add(role);
	}
	
	public boolean isRolesMatchAll() {
		return this.rolesMatchAll;
	}
	public void setRolesMatchAll(boolean rolesMatchAll) {
		this.rolesMatchAll = rolesMatchAll;
	}
	
}
