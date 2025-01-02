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

package org.openspcoop2.utils.transport.ldap;

import java.util.ArrayList;
import java.util.List;

import javax.naming.ldap.LdapName;

/**
 * Query generica per la funzionalita di ricerca ldap
 * viene definito un filtro, gli attributi da ritornare e una quantita di risultati
 * 
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$  
 * @version $Rev$, $Date$
 *
 */
public class LdapQuery {
	private Integer limit;
	private List<String> attributes;
	private LdapFilter filter;
	private LdapName base;
	
	public LdapQuery() {
		this.attributes = new ArrayList<>();
		this.limit = null;
		this.filter = LdapFilter.isPresent("cn");
		this.base = null;
	}
	
	
	public Integer getLimit() {
		return this.limit;
	}
	
	public List<String> getAttributes() {
		return this.attributes;
	}
	
	public LdapFilter getFilter() {
		return this.filter;
	}
	
	public LdapName getBase() {
		return this.base;
	}
	
	public LdapQuery filter(LdapFilter filter) {
		this.filter = filter;
		return this;
	}
	
	public LdapQuery limit(Integer limit) {
		this.limit = limit;
		return this;
	}
	
	public LdapQuery attributes(String ...attributes) {
		this.attributes = List.of(attributes);
		return this;
	}
	
	public LdapQuery base(LdapName base) {
		this.base = base;
		return this;
	}
	
	
}
