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
