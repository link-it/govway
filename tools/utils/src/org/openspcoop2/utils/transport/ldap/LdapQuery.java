package org.openspcoop2.utils.transport.ldap;

import java.util.ArrayList;
import java.util.List;

import javax.naming.ldap.LdapName;

/**
 * Query generica per la funzionalita di ricerca ldap
 * viene definito un filtro, gli attributi da ritornare e una quantita di risultati
 * 
 * @author Tommaso Burlon (tommaso.burlon@link.it)
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
