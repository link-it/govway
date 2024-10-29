package org.openspcoop2.utils.transport.ldap;


/**
 * Factory pattern per ottenere un implementazione concreta di un client ldap
 * 
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @version $Rev$, $Date$
 *
 */
public class LdapClientFactory {

		public static LdapClientInterface getClient(LdapEngineType type) {
			switch (type) {
			case SPRING_FRAMEWORK:
				return new LdapSpringClient();
			}
			
			return null;
		}
}
