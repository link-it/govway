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
package org.openspcoop2.example.pdd.client.stampadocumento;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.openspcoop2.example.pdd.server.stampadocumento.StampaDocumento;
import org.openspcoop2.example.pdd.server.stampadocumento.StampaDocumentoService;
import org.openspcoop2.example.pdd.server.stampadocumento.StampaDocumento_Type;

/**
 * StampaDocumento_StampaDocumentoInterfaceEndpoint_Client
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class StampaDocumento_StampaDocumentoInterfaceEndpoint_Client {

    private static final QName SERVICE_NAME = new QName("http://openspcoop2.org/example/pdd/server/StampaDocumento", "StampaDocumentoService");

    private StampaDocumento_StampaDocumentoInterfaceEndpoint_Client() {
    }
    
    private static String riferimento = null;
    
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
		
		
		
		String url_stampa = null;
		String url_stato = null;
		String identificativo_egov = null;
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
			
			String PD_stampa = reader.getProperty("portaDelegata.stampa");
			if(PD_stampa == null){
				System.err.println("ERROR : PortaDelegata 'stampa' non definita all'interno del file 'Client.properties'");
				return;
			}else{
				PD_stampa = PD_stampa.trim();
			}
			url_stampa = urlPD + PD_stampa; 
			
			String PD_stato = reader.getProperty("portaDelegata.stato");
			if(PD_stato == null){
				System.err.println("ERROR : PortaDelegata 'stato' non definita all'interno del file 'Client.properties'");
				return;
			}else{
				PD_stato = PD_stato.trim();
			}
			url_stato = urlPD + PD_stato; 
			
			identificativo_egov = reader.getProperty("informazioniIntegrazione.identificativoEGov");
			if(identificativo_egov == null){
				System.err.println("ERROR : Nome dell'informazione di integrazione riguardante l'identificativo egov della porta di dominio non definito all'interno del file 'Client.properties'");
				return;
			}else{
				identificativo_egov = identificativo_egov.trim();
			}
			
			riferimento_asincrono = reader.getProperty("informazioniIntegrazione.riferimentoAsincrono");
			if(riferimento_asincrono == null){
				System.err.println("ERROR : Nome dell'informazione di integrazione riguardante il riferimento alla richiesta asincrona della porta di dominio non definito all'interno del file 'Client.properties'");
				return;
			}else{
				riferimento_asincrono = riferimento_asincrono.trim();
			}
			
		}
		else{
			
			String endpoint = reader.getProperty("endpoint");
			if(endpoint == null){
				System.err.println("ERROR : Punto di Accesso del servizio non definito all'interno del file 'Server.properties'");
				return;
			}else{
				url_stampa = endpoint.trim();
				url_stato = url_stampa;
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
    	
    	

		
        URL wsdlURL = StampaDocumentoService.WSDL_LOCATION;
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
        
        StampaDocumentoService ss = new StampaDocumentoService(wsdlURL, SERVICE_NAME);
        StampaDocumento port = ss.getStampaDocumentoInterfaceEndpoint();  
        
        /*
         * Imposto username e password per l'autenticazione
         */
        ((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY,  username);
    	((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,  password);
        long id = 0;
        
        System.out.println("Richiedo stampa della carta d'Identita per il cf : DDDFFF22G22G222G");
        org.openspcoop2.example.pdd.server.stampadocumento.StampaDocumento_Type _stampa_stampaDocumentoRequestPart = new StampaDocumento_Type();
        _stampa_stampaDocumentoRequestPart.setCF("DDDFFF22G22G222G");
        _stampa_stampaDocumentoRequestPart.setCodiceDocumento("CartaIdentita");
        
        
        /*
         * Imposto un Interceptor per recuperare l'id SPCoop dagli header HTTP della risposta
         */
        Client client = org.apache.cxf.frontend.ClientProxy.getClient(port); 
        client.getInInterceptors().add((new StampaDocumento_StampaDocumentoInterfaceEndpoint_Client()).new SPCoopIdInInterceptor(identificativo_egov));
        //StampaDocumento_StampaDocumentoInterfaceEndpoint_Client a = new StampaDocumento_StampaDocumentoInterfaceEndpoint_Client();
                	
        /*
         * Imposto la url della porta di dominio come destinazione
         */
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  url_stampa);
        
        
        
        org.openspcoop2.example.pdd.server.stampadocumento.PresaConsegnaStampa _stampa__return = port.stampa(_stampa_stampaDocumentoRequestPart);
        
        
        id = _stampa__return.getIdStampa();
        System.out.println("Stampa in corso con id = " + _stampa__return.getIdStampa());
        System.out.println("Data stimata di completamento = " + _stampa__return.getStimaCompletamento());

        
        String stato = "Incompleto";
        
        long _stato_statoDocumentoRequestPart = id;
        
        /*
         * Faccio richiesta della risposta ad intervalli di un secondo finche' non mi viene recapitata.
         * Nella url della richiesta imposto l'id del riferimento SPCoop.
         */
        
        while(stato.compareToIgnoreCase("Completato") != 0)
        {
        	System.out.println("Richiedo stato di stampa per documento con id = " + id + " ------- " +  StampaDocumento_StampaDocumentoInterfaceEndpoint_Client.riferimento);
        	String urlStato = url_stato;
        	if(riferimento!=null)
        		urlStato = urlStato+ "?"+riferimento_asincrono+"=" + StampaDocumento_StampaDocumentoInterfaceEndpoint_Client.riferimento ;
            ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  urlStato);
        	
	        java.lang.String _stato__return = port.stato(_stato_statoDocumentoRequestPart);
	        System.out.println("Stampa del documento " + _stato__return);
	        stato = _stato__return;
	        Thread.sleep(1000);
        }

        System.exit(0);
    }
    
    
    
    
    
    
    
    
    
    public class SPCoopIdInInterceptor extends AbstractPhaseInterceptor<Message> {
        
    	private String nomeHeaderID = null;
    	
    	public SPCoopIdInInterceptor(String nomeHeaderID) {
            super(Phase.RECEIVE);
            this.nomeHeaderID = nomeHeaderID;
        }

        @Override
		public void handleMessage(Message message) {
        	if(riferimento!=null) return;
        	Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>)message.get(Message.PROTOCOL_HEADERS));
        	if(headers!=null && this.nomeHeaderID!=null){
	        	List<String> listaHeader = headers.get(this.nomeHeaderID);
	        	if(listaHeader!=null && listaHeader.size()>0)
	        		riferimento = headers.get(this.nomeHeaderID).get(0);
        	}
        }

        @Override
		public void handleFault(Message messageParam) {
        }
    }
    
    
    
    
    
    

}
