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

package org.openspcoop2.protocol.modipa.example.rest.non_blocking.pull;

import java.io.FileInputStream;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.jaxrs.JacksonJsonProviderCustomized;

/**
 * Server
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Server {

	public static void main(String[] args) throws Exception {
		
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
		
		java.util.Properties reader = new java.util.Properties();
		try{  
			reader.load(new FileInputStream("Server.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}
		
		String address = reader.getProperty("endpoint");
		if(address==null){
			throw new Exception("Property [endpoint] not definded");
		}
		else{
			address = address.trim();
		}
		
		System.out.println("Avvio server sull'indirizzo: "+address);
		
		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		sf.setServiceClass(ServerImpl.class);
		sf.setServiceBean(new ServerImpl());
		sf.setAddress(address);
		sf.setProvider(new JacksonJsonProviderCustomized());
		sf.create();
		 System.out.println("Server ready..."); 
		
	}
}
