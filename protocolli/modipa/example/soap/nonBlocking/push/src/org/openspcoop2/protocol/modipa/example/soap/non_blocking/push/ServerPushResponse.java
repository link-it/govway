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

package org.openspcoop2.protocol.modipa.example.soap.non_blocking.push;

import java.io.FileInputStream;

import jakarta.xml.ws.Endpoint;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * Server
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServerPushResponse {


	protected ServerPushResponse() throws java.lang.Exception {

		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);

		java.util.Properties reader = new java.util.Properties();
		try{  
			reader.load(new FileInputStream("Server.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}

		String address = reader.getProperty("push.response.endpoint");
		if(address==null){
			throw new Exception("Property [endpoint] not definded");
		}
		else{
			address = address.trim();
		}

		System.out.println("Avvio server 'push.response' sull'indirizzo: "+address);


		System.out.println("Starting Server");
		ServerPushResponseImpl implementor = new ServerPushResponseImpl();
		Endpoint.publish(address, implementor);
	}


	public static void main(String args[]) throws java.lang.Exception { 
		new ServerPushResponse();
		System.out.println("Server ready..."); 

		//	        Thread.sleep(5 * 60 * 1000); 
		//	        System.out.println("Server exiting");
		//	        System.exit(0);
	}

}
