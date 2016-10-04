/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.example.pdd.client.identificasoggetto;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.openspcoop2.example.pdd.server.esitoidentificazione.EsitoIdentificazione_EsitoIdentificazioneInterfaceEndpoint_Server;
import org.openspcoop2.example.pdd.server.identificasoggetto.Identifica;
import org.openspcoop2.example.pdd.server.identificasoggetto.IdentificaSoggetto;
import org.openspcoop2.example.pdd.server.identificasoggetto.IdentificaSoggettoService;

/**
 * IdentificaSoggetto_IdentificaSoggettoInterfaceEndpoint_Client
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class IdentificaSoggetto_IdentificaSoggettoInterfaceEndpoint_Client {

	private static final QName SERVICE_NAME = new QName("http://openspcoop2.org/example/pdd/server/IdentificaSoggetto", "IdentificaSoggettoService");

	private IdentificaSoggetto_IdentificaSoggettoInterfaceEndpoint_Client() {
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
		
		
		
		String url_cerca = null;
		
		
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
			
			String PD_cerca = reader.getProperty("portaDelegata.cerca");
			if(PD_cerca == null){
				System.err.println("ERROR : PortaDelegata 'cerca' non definita all'interno del file 'Client.properties'");
				return;
			}else{
				PD_cerca = PD_cerca.trim();
			}
			url_cerca = urlPD + PD_cerca; 
						

			
		}
		else{
			
			String endpoint = reader.getProperty("endpoint.cerca");
			if(endpoint == null){
				System.err.println("ERROR : Punto di Accesso del servizio 'cerca' non definito all'interno del file 'Server.properties'");
				return;
			}else{
				url_cerca = endpoint.trim();
			}
			
		}
		
		
		String username = reader.getProperty("username.cerca");
		if(username == null){
			System.err.println("ERROR : Username del servizio 'cerca' non definita all'interno del file 'Client.properties'");
			return;
		}else{
			username = username.trim();
		}
		
		String password = reader.getProperty("password.cerca");
		if(password == null){
			System.err.println("ERROR : Password del servizio 'cerca' non definita all'interno del file 'Client.properties'");
			return;
		}else{
			password = password.trim();
		}
		
		


		URL wsdlURL = IdentificaSoggettoService.WSDL_LOCATION;
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

		IdentificaSoggettoService ss = new IdentificaSoggettoService(wsdlURL, SERVICE_NAME);
		IdentificaSoggetto port = ss.getIdentificaSoggettoInterfaceEndpoint();  
		RispostaServer s = new RispostaServer();
		s.start();
		Thread.sleep(2000);

        /*
         * Imposto la url della porta di dominio come destinazione
         */
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  url_cerca);
		
		((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY,  username);
		((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,  password);
		System.out.println("Invoking cerca...");
		org.openspcoop2.example.pdd.server.identificasoggetto.Identifica _cerca_identificaRequestPart = new Identifica();
		_cerca_identificaRequestPart.setCodiceDocumento("AABBCCDDEE");
		_cerca_identificaRequestPart.setTipoDocumento("CartaIdentita");
		java.lang.String _cerca__return = port.cerca(_cerca_identificaRequestPart);
		System.out.println("Codice ricerca =" + _cerca__return);

		Thread.sleep(10 * 1000);

		System.exit(0);
	}

	static class RispostaServer extends Thread {
		boolean terminated = false;
		@Override
		public void run() {
			try{
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
			}
			catch(Exception e){
				e.printStackTrace();
			}
			this.terminated = true;
		}
	}
}
