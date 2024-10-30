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

package org.openspcoop2.utils.transport.ldap;

import java.net.URI;
import java.util.List;

import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;

/**
 * Interfaccia del client ldap
 * 
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @version $Rev$, $Date$
 *
 */
public interface LdapClientInterface {
	
	public List<Attributes> search(LdapQuery filter);
	public LdapClientInterface base(LdapName base);
	public LdapClientInterface uri(URI url);
	public LdapClientInterface username(LdapName name);
	public LdapClientInterface password(String password);
	
	// default method utility
	public default List<Attributes> search() {
		return this.search(new LdapQuery());
	}
}
