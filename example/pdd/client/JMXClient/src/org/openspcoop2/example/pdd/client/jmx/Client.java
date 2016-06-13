/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.example.pdd.client.jmx;

import java.io.*;
import java.util.Hashtable;


import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.security.SecurityAssociation;
import org.jboss.security.SimplePrincipal;


/**
 * Client
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Client {
	public static void main(String[] args) throws Exception {

		System.out.println();

		if (args.length  < 1) {
			System.err.println("ERROR, Usage:  java Client " +
			"soapEnvelopefile.xml");
			System.err.println("PortaDelegata,User,Password impostabili da file 'properties'.");
			System.exit(1);
		}

		java.util.Properties reader = new java.util.Properties();
		try{  
			reader.load(new FileInputStream("Client.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}

		String factory = reader.getProperty("jmx.factory");
		if(factory == null){
			System.err.println("ERROR : JMX.Factory non definita all'interno del file 'Client.properties'");
			return;
		}else{
			factory = factory.trim();
		}
		
		String serverUrl = reader.getProperty("jmx.server");
		if(serverUrl == null){
			System.err.println("ERROR : JMX.Server non definito all'interno del file 'Client.properties'");
			return;
		}else{
			serverUrl = serverUrl.trim();
		}
		
		
		String username = reader.getProperty("jmx.username");
		if(username != null){
			username = username.trim();
		}
		String password = reader.getProperty("jmx.password");
		if(password != null){
			password = password.trim();
		}


		
		// AccessoRegistroServizi
		String tmp = reader.getProperty("accessoRegistroServizi.readProperties");
		String[]propertiesAccessoRegistroServizi = null;
		if(tmp != null){
			propertiesAccessoRegistroServizi = tmp.trim().split(",");
			for(int i=0; i<propertiesAccessoRegistroServizi.length;i++){
				propertiesAccessoRegistroServizi[i] = propertiesAccessoRegistroServizi[i].trim();
			}
		}
		
		tmp = reader.getProperty("accessoRegistroServizi.resetCache");
		boolean resetCacheAccessoRegistroServizi = false;
		if(tmp!=null){
			resetCacheAccessoRegistroServizi = Boolean.parseBoolean(tmp.trim());
		}
		
		
		
		// ConfigurazionePdD
		tmp = reader.getProperty("configurazionePdD.readProperties");
		String[]propertiesConfigurazionePdD = null;
		if(tmp != null){
			propertiesConfigurazionePdD = tmp.trim().split(",");
			for(int i=0; i<propertiesConfigurazionePdD.length;i++){
				propertiesConfigurazionePdD[i] = propertiesConfigurazionePdD[i].trim();
			}
		}
		
		tmp = reader.getProperty("configurazionePdD.resetCache");
		boolean resetConfigurazionePdD = false;
		if(tmp!=null){
			resetConfigurazionePdD = Boolean.parseBoolean(tmp.trim());
		}
		
		
		
		// AutorizzazioneBuste
		tmp = reader.getProperty("autorizzazioneBuste.readProperties");
		String[]propertiesAutorizzazioneBuste = null;
		if(tmp != null){
			propertiesAutorizzazioneBuste = tmp.trim().split(",");
			for(int i=0; i<propertiesAutorizzazioneBuste.length;i++){
				propertiesAutorizzazioneBuste[i] = propertiesAutorizzazioneBuste[i].trim();
			}
		}
		
		tmp = reader.getProperty("autorizzazioneBuste.resetCache");
		boolean resetCacheAutorizzazioneBuste = false;
		if(tmp!=null){
			resetCacheAutorizzazioneBuste = Boolean.parseBoolean(tmp.trim());
		}
		
		
		
		
		// Repository Messaggi
		tmp = reader.getProperty("repositoryMessaggi.readProperties");
		String[]propertiesRepositoryMessaggi = null;
		if(tmp != null){
			propertiesRepositoryMessaggi = tmp.trim().split(",");
			for(int i=0; i<propertiesRepositoryMessaggi.length;i++){
				propertiesRepositoryMessaggi[i] = propertiesRepositoryMessaggi[i].trim();
			}
		}
		
		tmp = reader.getProperty("repositoryMessaggi.resetCache");
		boolean resetRepositoryMessaggi = false;
		if(tmp!=null){
			resetRepositoryMessaggi = Boolean.parseBoolean(tmp.trim());
		}
		
		
		
		// NOTA: gestire un eventuale nuovo a.s. anche nella classe  org.openspcoop2.utils.resources.GestoreRisorseJMX
		try{
			if(username!=null && password!=null){
				System.out.println("Use credential ["+username+"] ["+password+"]");
			}
			
			MBeanServerConnection mconn = null;
			if(args[0].equals("jboss7") ||
					args[0].equals("wildfly8") ||
					args[0].equals("wildfly9") ||
					args[0].equals("wildfly10") ||
					args[0].startsWith("tomcat")){
				JMXServiceURL serviceURL = new JMXServiceURL(serverUrl);  
				Hashtable<String, Object> env = null;
				if(username!=null && password!=null){
					String[] creds = {username, password};
					env = new Hashtable<String, Object>();
					env.put(JMXConnector.CREDENTIALS, creds);
				}
				JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL, env);             
				mconn = jmxConnector.getMBeanServerConnection();
			}
			else{
				Hashtable<String, Object> env = new Hashtable<String, Object>();
				env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
				env.put(Context.PROVIDER_URL, serverUrl);
				
				Context ctx = new InitialContext(env);
				mconn = (MBeanServerConnection) ctx.lookup("jmx/invoker/RMIAdaptor");
				SecurityAssociation.setPrincipal(new SimplePrincipal(username));
				SecurityAssociation.setCredential(password);	
			}
		
			// AccessoRegistroServizi
			System.out.println();
			ObjectName name = new ObjectName("org.openspcoop2.pdd:type=AccessoRegistroServizi");
			invoke(mconn,name,propertiesAccessoRegistroServizi,resetCacheAccessoRegistroServizi);
			
			// ConfigurazionePdD
			System.out.println();
			name = new ObjectName("org.openspcoop2.pdd:type=ConfigurazionePdD");
			invoke(mconn,name,propertiesConfigurazionePdD,resetConfigurazionePdD);
			
			// AutorizzazioneSPCoop
			System.out.println();
			name = new ObjectName("org.openspcoop2.pdd:type=DatiAutorizzazione");
			invoke(mconn,name,propertiesAutorizzazioneBuste,resetCacheAutorizzazioneBuste);
			
			// AutorizzazioneSPCoop
			System.out.println();
			name = new ObjectName("org.openspcoop2.pdd:type=RepositoryMessaggi");
			invoke(mconn,name,propertiesRepositoryMessaggi,resetRepositoryMessaggi);
			
			
		}catch(Exception e){
			System.err.println("ERROR :"+e.getMessage());
		}
	}


	
	
	
	public static void invoke(MBeanServerConnection mconn,ObjectName name,String[]properties,boolean resetCache) throws Exception{
		if(properties!=null){
			for(int i=0; i<properties.length;i++){
				Object val = mconn.getAttribute(name, properties[i]);
				if(val instanceof String)
					System.out.println(name + "."+properties[i]+" = " + (String)val);
				else if(val instanceof Boolean)
					System.out.println(name + "."+properties[i]+" = " + (Boolean)val);
				else if(val instanceof String[]){
					String[] s =  (String []) val;
					StringBuffer bf = new StringBuffer();
					bf.append(name + "."+properties[i]+" = \n");
					for(int j=0; j<s.length; j++){
						if(j>0)
							bf.append("\n\t\t");
						bf.append(s[j]);
					}
					System.out.println(bf.toString());
				}
			}
		}		
		if(resetCache){
			Object response = mconn.invoke(name, "resetCache", null, null);
			System.out.println(name+".resetCache() : "+response);
		}
	}
}
