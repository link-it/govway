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

package org.openspcoop2.utils.service.authorization;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.utils.UtilsException;

/**
 * AuthorizationConfig
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuthorizationConfig {

	private List<AuthorizationConfigACL> aclList = new ArrayList<>();

	public AuthorizationConfig() {
	}
	public AuthorizationConfig(Properties p) throws UtilsException {
		
		List<String> aclNames = new ArrayList<>();
		Enumeration<?> keys = p.keys();
		while (keys.hasMoreElements()) {
			Object object = (Object) keys.nextElement();
			if(object instanceof String) {
				String key = (String) object;
				if(key.startsWith(AuthorizationConfigACL.PREFIX) && key.endsWith(AuthorizationConfigACL.SUFFIX_METHOD)){
					String aclName = key.substring(AuthorizationConfigACL.PREFIX.length());
					aclName = aclName.substring(0, (aclName.length()-AuthorizationConfigACL.SUFFIX_METHOD.length()));
					aclNames.add(aclName);
				}
			}
		}
		
		if(aclNames.size()>0) {
			for (String aclName : aclNames) {
				this.aclList.add(new AuthorizationConfigACL(aclName, p));
			}
		}
		
	}
	
	
	public void addAcl(AuthorizationConfigACL acl) {
		this.aclList.add(acl);
	}
	public List<AuthorizationConfigACL> getAclList() {
		return this.aclList;
	}
	
}
