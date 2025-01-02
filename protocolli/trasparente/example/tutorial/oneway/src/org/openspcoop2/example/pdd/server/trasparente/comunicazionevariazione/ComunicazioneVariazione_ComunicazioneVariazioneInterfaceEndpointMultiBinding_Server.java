/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

package org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione;

import org.springframework.context.support.ClassPathXmlApplicationContext;
 
/**
 * ComunicazioneVariazione_ComunicazioneVariazioneInterfaceEndpointMultiBinding_Server
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ComunicazioneVariazione_ComunicazioneVariazioneInterfaceEndpointMultiBinding_Server{

	private ClassPathXmlApplicationContext context;
	
    protected ComunicazioneVariazione_ComunicazioneVariazioneInterfaceEndpointMultiBinding_Server() throws java.lang.Exception {
    	System.out.println("Starting Server");
        this.context = new ClassPathXmlApplicationContext("configurazionePdD/server/cxf.xml");
        this. context.toString();
    }
    
    public void close(){
    	this.context.close();
    }
    
    public static void main(String args[]) throws java.lang.Exception { 
    	ComunicazioneVariazione_ComunicazioneVariazioneInterfaceEndpointMultiBinding_Server server = new ComunicazioneVariazione_ComunicazioneVariazioneInterfaceEndpointMultiBinding_Server();
        System.out.println("Server ready..."); 
        
        Thread.sleep(5 * 60 * 1000); 
        System.out.println("Server exiting");
        server.close();
        System.exit(0);
              
    }
}
