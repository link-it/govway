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
package org.openspcoop2.example.pdd.client.esitoidentificazione;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.openspcoop2.example.pdd.server.esitoidentificazione.EsitoIdentificazione;
import org.openspcoop2.example.pdd.server.esitoidentificazione.EsitoIdentificazioneService;

/**
 * EsitoIdentificazione_EsitoIdentificazioneInterfaceEndpoint_Client
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class EsitoIdentificazione_EsitoIdentificazioneInterfaceEndpoint_Client {

    private static final QName SERVICE_NAME = new QName("http://openspcoop2.org/example/pdd/server/IdentificaSoggetto", "EsitoIdentificazioneService");

    public EsitoIdentificazione_EsitoIdentificazioneInterfaceEndpoint_Client() {
    }

    public void rispondi(String url,String riferimento) throws Exception {
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
		
		
		
		String url_risultato = null;
		String riferimento_asincrono = null;
		
		if(isInvocazioneTramitePdD){
			
			String urlPD = reader.getProperty("portaDiDominio");
			if(urlPD == null){
				System.err.println("ERROR : Punto di Accesso della porta di dominio non definito all'interno del file 'Client.properties'");
				return;
			}else{
				urlPD = urlPD.trim();
			}
			if(urlPD.endsWith("/")==false)
				urlPD = urlPD + "/"; 
			
			String PD_risultato = reader.getProperty("portaDelegata.risultato");
			if(PD_risultato == null){
				System.err.println("ERROR : PortaDelegata 'risultato' non definita all'interno del file 'Client.properties'");
				return;
			}else{
				PD_risultato = PD_risultato.trim();
			}
			url_risultato = urlPD + PD_risultato; 
						
			riferimento_asincrono = reader.getProperty("informazioniIntegrazione.riferimentoAsincrono");
			if(riferimento_asincrono == null){
				System.err.println("ERROR : Nome dell'informazione di integrazione riguardante il riferimento alla richiesta asincrona della porta di dominio non definito all'interno del file 'Client.properties'");
				return;
			}else{
				riferimento_asincrono = riferimento_asincrono.trim();
			}
			
		}
		else{
			
			String endpoint = reader.getProperty("endpoint.risultato");
			if(endpoint == null){
				System.err.println("ERROR : Punto di Accesso del servizio 'risultato' non definito all'interno del file 'Server.properties'");
				return;
			}else{
				url_risultato = endpoint.trim();
			}
			
		}
		
		
		String username = reader.getProperty("username.risultato");
		if(username == null){
			System.err.println("ERROR : Username del servizio 'risultato' non definita all'interno del file 'Client.properties'");
			return;
		}else{
			username = username.trim();
		}
		
		String password = reader.getProperty("password.risultato");
		if(password == null){
			System.err.println("ERROR : Password del servizio 'risultato' non definita all'interno del file 'Client.properties'");
			return;
		}else{
			password = password.trim();
		}
		
		
        URL wsdlURL = EsitoIdentificazioneService.WSDL_LOCATION;
        if (url != null) { 
            File wsdlFile = new File(url);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(url);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        EsitoIdentificazioneService ss = new EsitoIdentificazioneService(wsdlURL, SERVICE_NAME);
        EsitoIdentificazione port = ss.getEsitoIdentificazioneInterfaceEndpoint();  
        ((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY,  username);
    	((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,  password);
    	
    	String urlRisultato = url_risultato;
    	if(riferimento!=null)
    		urlRisultato = urlRisultato+ "?"+riferimento_asincrono+"=" + riferimento ;

    	((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  urlRisultato);
      
    	System.out.println("Invoking risultato...");
        
        org.openspcoop2.example.pdd.server.esitoidentificazione.PersonaType _risultato_identificaRequestPart = new org.openspcoop2.example.pdd.server.esitoidentificazione.PersonaType();
        _risultato_identificaRequestPart.setCodiceFiscale("DDDFFF22G22G222G");
        _risultato_identificaRequestPart.setCognome("Rossi");
        _risultato_identificaRequestPart.setNome("Mario");
        _risultato_identificaRequestPart.setNascita(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("1980-01-01T12:00:00.000Z"));
        _risultato_identificaRequestPart.setStatoCivile("Celibe");
        String _risultato__return = port.risultato(_risultato_identificaRequestPart);
        System.out.println("risultato.result=" + _risultato__return);

        System.exit(0);
    }

}
