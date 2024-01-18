/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.example.pdd.client.trasparente.comunicazionevariazione;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.BindingProvider;

import org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione.ComunicazioneVariazione;
import org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione.ComunicazioneVariazioneServiceSec12;
import org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione.ComunicazioneVariazione_Type;

/**
 * ComunicazioneVariazione_ComunicazioneVariazioneInterfaceEndpointSec12_Client
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class ComunicazioneVariazione_ComunicazioneVariazioneInterfaceEndpointSec12_Client {

    private static final QName SERVICE_NAME = new QName("http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione", "ComunicazioneVariazioneSOAP12SecService");

    private ComunicazioneVariazione_ComunicazioneVariazioneInterfaceEndpointSec12_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
    
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
			
			String PD = reader.getProperty("Contesto.soap12.security");
			if(PD == null){
				System.err.println("ERROR : Contesto non definito all'interno del file 'Client.properties'");
				return;
			}else{
				PD = PD.trim();
			}
			
			if(urlPD.endsWith("/")==false)
				urlPD = urlPD + "/"; 
			url = urlPD + PD; 
			
		}
		else{
			
			String endpoint = reader.getProperty("endpoint.soap12.security");
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
		
        URL wsdlURL = ComunicazioneVariazioneServiceSec12.WSDL_LOCATION;
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
      
        ComunicazioneVariazioneServiceSec12 ss = new ComunicazioneVariazioneServiceSec12(wsdlURL, SERVICE_NAME);
        ComunicazioneVariazione client = ss.getComunicazioneVariazioneInterfaceEndpointSec12();  
        
        /*
         * Imposto la url della porta di dominio come destinazione
         * Imposto username e password per l'autenticazione
         */
        ((BindingProvider)client).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  url);
        ((BindingProvider)client).getRequestContext().put(BindingProvider.USERNAME_PROPERTY,  username);
    	((BindingProvider)client).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,  password);
       
    	((BindingProvider)client).getRequestContext().put("ws-security.signature.properties",  "configurazionePdD/client/client-crypto.properties");
        ((BindingProvider)client).getRequestContext().put("ws-security.signature.username",  "clientkey");
     	((BindingProvider)client).getRequestContext().put("ws-security.encryption.properties",  "configurazionePdD/client/client-crypto.properties");
     	((BindingProvider)client).getRequestContext().put("ws-security.encryption.username",  "serverkey");
     	((BindingProvider)client).getRequestContext().put("ws-security.callback-handler",  "org.openspcoop2.example.pdd.client.trasparente.comunicazionevariazione.ClientCallback");
     	
        
        System.out.println("Invoking notifica...");
        ComunicazioneVariazione_Type comunicazioneVariazione = new ComunicazioneVariazione_Type();
        comunicazioneVariazione.setCF("BBBCCC11F11F111F");
        comunicazioneVariazione.setCodiceFiscale("DDDFFF22G22G222G");
        comunicazioneVariazione.setCognome("Rossi");
        comunicazioneVariazione.setNome("Mario");
        comunicazioneVariazione.setNascita(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("1980-01-01T12:00:00.000Z"));
        comunicazioneVariazione.setStatoCivile("Celibe");
        client.notifica(comunicazioneVariazione);


       

        System.exit(0);
    }

}
