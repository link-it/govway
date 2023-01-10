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

package org.openspcoop2.example.server.mtom.ws;

import javax.xml.ws.Endpoint;

/**
 * MTOMServiceExample_MTOMServiceExampleSOAP12InterfaceEndpoint_Server
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MTOMServiceExample_MTOMServiceExampleSOAP12InterfaceEndpoint_Server{

    protected MTOMServiceExample_MTOMServiceExampleSOAP12InterfaceEndpoint_Server() throws java.lang.Exception {
        System.out.println("Starting Server");
        Object implementor = new MTOMServiceExampleSOAP12Impl();
        String address = "http://localhost:8888/MTOMExample/soap12";
        Endpoint.publish(address, implementor);
    }
    
    public static void main(String args[]) throws java.lang.Exception { 
        new MTOMServiceExample_MTOMServiceExampleSOAP12InterfaceEndpoint_Server();
        System.out.println("Server ready..."); 
        
        Thread.sleep(5 * 60 * 1000); 
        System.out.println("Server exiting");
        System.exit(0);
    }
}
