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

package org.openspcoop2.example.pdd.server.richiestastatofamiglia;

import java.io.FileInputStream;

import jakarta.xml.ws.Endpoint;

/**
 * RichiestaStatoFamiglia_RichiestaStatoFamigliaInterfaceEndpoint_Server
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RichiestaStatoFamiglia_RichiestaStatoFamigliaInterfaceEndpoint_Server{

    protected RichiestaStatoFamiglia_RichiestaStatoFamigliaInterfaceEndpoint_Server(String endpoint) throws java.lang.Exception {
        System.out.println("Starting Server");
        Object implementor = new RichiestaStatoFamigliaImpl();
        Endpoint.publish(endpoint, implementor);
    }
    
    public static void main(String args[]) throws java.lang.Exception { 
   
    	java.util.Properties reader = new java.util.Properties();
		try{  
			reader.load(new FileInputStream("Server.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}
		
		String endpoint = reader.getProperty("endpoint");
		if(endpoint == null){
			System.err.println("ERROR : Punto di Accesso del servizio non definito all'interno del file 'Server.properties'");
			return;
		}else{
			endpoint = endpoint.trim();
		}
    	
    	new RichiestaStatoFamiglia_RichiestaStatoFamigliaInterfaceEndpoint_Server(endpoint);
        System.out.println("Server ready..."); 
        
        Thread.sleep(5 * 60 * 1000); 
        System.out.println("Server exiting");
        System.exit(0);
    }
}
