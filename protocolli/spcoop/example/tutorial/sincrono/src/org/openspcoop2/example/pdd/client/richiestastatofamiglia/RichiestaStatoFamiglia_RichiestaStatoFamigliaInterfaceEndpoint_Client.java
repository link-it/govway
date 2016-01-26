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

package org.openspcoop2.example.pdd.client.richiestastatofamiglia;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.openspcoop2.example.pdd.server.richiestastatofamiglia.PersonaType;
import org.openspcoop2.example.pdd.server.richiestastatofamiglia.RichiestaStatoFamiglia;
import org.openspcoop2.example.pdd.server.richiestastatofamiglia.RichiestaStatoFamigliaService;

/**
 * RichiestaStatoFamiglia_RichiestaStatoFamigliaInterfaceEndpoint_Client
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class RichiestaStatoFamiglia_RichiestaStatoFamigliaInterfaceEndpoint_Client {

    private static final QName SERVICE_NAME = new QName("http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", "RichiestaStatoFamigliaService");

    private RichiestaStatoFamiglia_RichiestaStatoFamigliaInterfaceEndpoint_Client() {
    }

    public static void main(String args[]) throws Exception {
    	/*
    	 * Leggo la configurazione
    	 */
    	
    	java.util.Properties reader = new java.util.Properties();
		try{  
			reader.load(new FileInputStream("Client.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}
		
		String invocazioneTramitePdD = reader.getProperty("invocazioneTramitePdD");
		Boolean isInvocazioneTramitePdD = false;
		if(invocazioneTramitePdD == null){
			System.err.println("ERROR : Tipo di invocazione 'invocazioneTramitePdD' (true/false) non definito all'interno del file 'Client.properties'");
			return;
		}else{
			invocazioneTramitePdD = invocazioneTramitePdD.trim();
			isInvocazioneTramitePdD = Boolean.parseBoolean(invocazioneTramitePdD);
		}
		
		
		
		String url = null;
		
		if(isInvocazioneTramitePdD){
			
			String urlPD = reader.getProperty("portaDiDominio");
			if(urlPD == null){
				System.err.println("ERROR : Punto di Accesso della porta di dominio non definito all'interno del file 'Client.properties'");
				return;
			}else{
				urlPD = urlPD.trim();
			}
			
			String PD = reader.getProperty("portaDelegata");
			if(PD == null){
				System.err.println("ERROR : PortaDelegata non definita all'interno del file 'Client.properties'");
				return;
			}else{
				PD = PD.trim();
			}
			
			if(urlPD.endsWith("/")==false)
				urlPD = urlPD + "/"; 
			url = urlPD + PD; 
			
		}
		else{
			
			String endpoint = reader.getProperty("endpoint");
			if(endpoint == null){
				System.err.println("ERROR : Punto di Accesso del servizio non definito all'interno del file 'Server.properties'");
				return;
			}else{
				url = endpoint.trim();
			}
			
		}
		
		
		String username = reader.getProperty("username");
		if(username == null){
			System.err.println("ERROR : Username non definita all'interno del file 'Client.properties'");
			return;
		}else{
			username = username.trim();
		}
		
		String password = reader.getProperty("password");
		if(password == null){
			System.err.println("ERROR : Password non definita all'interno del file 'Client.properties'");
			return;
		}else{
			password = password.trim();
		}
		
		
        URL wsdlURL = RichiestaStatoFamigliaService.WSDL_LOCATION;
        if (args.length > 0) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        RichiestaStatoFamigliaService ss = new RichiestaStatoFamigliaService(wsdlURL, SERVICE_NAME);
        RichiestaStatoFamiglia port = ss.getRichiestaStatoFamigliaInterfaceEndpoint();  
        
        /*
         * Imposto la url della porta di dominio come destinazione
         * Imposto username e password per l'autenticazione
         */
        
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  url);
        ((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY,  username);
    	((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,  password);

    	
    	System.out.println("Invoking acquisisci...");
        java.lang.String _acquisisci_statoFamigliaRequestPart = "DDDFFF22G22G222G";
        PersonaType _acquisisci__return = port.acquisisci(_acquisisci_statoFamigliaRequestPart);
    
        System.out.println("========== Ricevuti dati del Soggetto ==============");
		System.out.println("== Nuovo nome: " + _acquisisci__return.getNome());
		System.out.println("== Nuovo cognome: " + _acquisisci__return.getCognome());
		System.out.println("== Nuovo codice fiscale: " + _acquisisci__return.getCodiceFiscale());
		System.out.println("== Nuova data di nascita: " + _acquisisci__return.getNascita());
		System.out.println("== Nuovo stato civile: " + _acquisisci__return.getStatoCivile());


        System.exit(0);
    }

}
