/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.example.pdd.client.jmsreceiver;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

import jakarta.jms.ConnectionFactory;

/**
 * Utilities
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Utilities {
	
	private Utilities() {}

	private static final String WILDFLY = "wildfly";
	
	public static final boolean BYTES = true;
	public static final boolean TEXT = false;
	
	private static Logger log = null;
	private static synchronized void initLog() {
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.INFO);
		log = LoggerWrapperFactory.getLogger(Utilities.class);
	}
	public static void info(String msg){
		if(log==null) {
			initLog();
		}
		log.info(msg);
	}
	
	public static ConnectionFactory getConnectionFactory(String as, InitialContext ctx) throws NamingException {
		if(as.startsWith(WILDFLY)){
			return (ConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
		}
		else{
			return (ConnectionFactory) ctx.lookup("ConnectionFactory");
		}
	}
	
	public static Properties initProperties(String as, String username, String password) {
		Properties properties = new Properties();
		if(as.startsWith(WILDFLY)){
			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			properties.put(Context.PROVIDER_URL, "http-remoting://127.0.0.1:8080");
		}
		else{
			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
			properties.put(Context.URL_PKG_PREFIXES, "org.jnp.interfaces");
			properties.put(Context.PROVIDER_URL, "127.0.0.1");
		}
		
		if(username!=null && password!=null){
			info("Set autenticazione ["+username+"]["+password+"]");
			properties.put(Context.SECURITY_PRINCIPAL, username);
			properties.put(Context.SECURITY_CREDENTIALS, password);
		}
		return properties;
	}
	
}


