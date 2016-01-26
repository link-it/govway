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

package org.openspcoop2.example.pdd.server.esitoidentificazione;

import java.io.FileInputStream;

import javax.xml.ws.Endpoint;

/**
 * EsitoIdentificazione_EsitoIdentificazioneInterfaceEndpoint_Server
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitoIdentificazione_EsitoIdentificazioneInterfaceEndpoint_Server{

    public EsitoIdentificazione_EsitoIdentificazioneInterfaceEndpoint_Server(String endpoint) throws Exception {
        System.out.println("Starting Server");
        Object implementor = new EsitoIdentificazioneImpl();
        Endpoint.publish(endpoint, implementor);
    }
    
    public static void main(String args[]) throws Exception {
    	
    	java.util.Properties reader = new java.util.Properties();
		try{  
			reader.load(new FileInputStream("Server.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}
		
		String endpoint = reader.getProperty("endpoint.risposta");
		if(endpoint == null){
			System.err.println("ERROR : Punto di Accesso del servizio di risposta non definito all'interno del file 'Server.properties'");
			return;
		}else{
			endpoint = endpoint.trim();
		}
    	
        new EsitoIdentificazione_EsitoIdentificazioneInterfaceEndpoint_Server(endpoint);
        System.out.println("Server ready..."); 
        
        Thread.sleep(5 * 60 * 1000); 
        System.out.println("Server exiting");
        System.exit(0);
    }
}
