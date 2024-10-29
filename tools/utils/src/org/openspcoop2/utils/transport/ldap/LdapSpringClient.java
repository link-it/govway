package org.openspcoop2.utils.transport.ldap;

import java.net.URI;
import java.util.List;

import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQueryBuilder;

/**
 * Implementazione spring di un client ldap
 * 
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @version $Rev$, $Date$
 *
 */
public class LdapSpringClient implements LdapClientInterface{

	private LdapContextSource context;
	private LdapTemplate template;
	
	public LdapSpringClient() {
		this.context = new LdapContextSource();
		this.template = new LdapTemplate();
		
		this.template.setContextSource(null);
	}
	
	private LdapTemplate getTemplate() {
		if (this.template.getContextSource() == null) {
			this.context.afterPropertiesSet();
			this.template.setContextSource(this.context);
		}
		return this.template;
	}
	
	@Override
	public List<Attributes> search(LdapQuery query) {
		LdapTemplate ldapTemplate = this.getTemplate();
		LdapQueryBuilder queryBuilder = LdapQueryBuilder
				.query();
		
		if (query.getLimit() != null)
			queryBuilder.countLimit(query.getLimit());
		
		if (!query.getAttributes().isEmpty())
			queryBuilder.attributes(query.getAttributes().toArray(new String[0]));
		
		if (query.getBase() != null)
			queryBuilder.base(query.getBase());
		
		return ldapTemplate.search(
				queryBuilder.filter(query.getFilter().toString()), 
				(Attributes attrs) -> attrs);
	}

	@Override
	public LdapClientInterface base(LdapName base) {
		this.context.setBase(base.toString());
		return this;
	}

	@Override
	public LdapClientInterface uri(URI uri) {
		this.context.setUrl(uri.toString());
		return this;
	}
	
	@Override
	public LdapClientInterface username(LdapName username) {
		this.context.setUserDn(username.toString());
		return this;
	}
	
	@Override
	public LdapClientInterface password(String password) {
		this.context.setPassword(password);
		return this;
	}

}
