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
package org.openspcoop2.example.pdd.server.identificasoggetto;

import java.io.FileInputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.openspcoop2.example.pdd.client.esitoidentificazione.EsitoIdentificazione_EsitoIdentificazioneInterfaceEndpoint_Client;

/**
 * IdentificaSoggettoImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.jws.WebService(
                      serviceName = "IdentificaSoggettoService",
                      portName = "IdentificaSoggettoInterfaceEndpoint",
                      targetNamespace = "http://openspcoop2.org/example/pdd/server/IdentificaSoggetto",
                      wsdlLocation = "file:configurazionePdD/wsdl/implementativoErogatore.wsdl",
                      endpointInterface = "org.openspcoop2.example.pdd.server.identificasoggetto.IdentificaSoggetto")
                      
public class IdentificaSoggettoImpl implements IdentificaSoggetto {

    @Resource 
    private WebServiceContext context;
    private String riferimento = "";
    @Override
	public java.lang.String cerca(Identifica identificaRequestPart) { 
        
    	java.util.Properties reader = new java.util.Properties();
		try{  
			reader.load(new FileInputStream("Server.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			throw new RuntimeException("Server.properties not found");
		}
		
		String identificativo_egov = null;
		identificativo_egov = reader.getProperty("informazioniIntegrazione.identificativoEGov");
		if(identificativo_egov == null){
			System.err.println("ERROR : Nome dell'informazione di integrazione riguardante l'identificativo egov della porta di dominio non definito all'interno del file 'Server.properties'");
			throw new RuntimeException("Nome dell'informazione di integrazione riguardante l'identificativo egov della porta di dominio non definito all'interno del file 'Client.properties'");
		}else{
			identificativo_egov = identificativo_egov.trim();
		}
    	
        System.out.println("Ricercato soggetto con documento " + identificaRequestPart.tipoDocumento + " n " + identificaRequestPart.codiceDocumento);
        MessageContext ctx = this.context.getMessageContext();
        HttpServletRequest request = (HttpServletRequest) ctx.get(AbstractHTTPDestination.HTTP_REQUEST);
        if(request!=null && identificativo_egov!=null){
            this.riferimento = request.getHeader(identificativo_egov);	
            if(this.riferimento!=null)
            	System.out.println("ID Busta da utilizzare per correlazione asincrona: "+this.riferimento);
            else
            	throw new RuntimeException("ID Busta da utilizzare per correlazione asincrona non presente nell'header HTTP del messaggio ricevuto (header atteso: "+identificativo_egov+")");
        }
        try {
            Risposta t = new Risposta();
            t.start();
            java.lang.String _return = (new java.util.Random().nextLong()) + "";
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    
    
    
    class Risposta extends Thread {
        @Override
		public void run() {
        	 try {
        		 Thread.sleep(5000);
        	 } catch (Exception ex) {}
        	 
        	EsitoIdentificazione_EsitoIdentificazioneInterfaceEndpoint_Client client = new EsitoIdentificazione_EsitoIdentificazioneInterfaceEndpoint_Client();
        	try{
        		client.rispondi("file:configurazionePdD/wsdl/implementativoFruitore.wsdl",IdentificaSoggettoImpl.this.riferimento);
        	}
        	catch(Exception e){
        		e.printStackTrace();
        	}
        }
    }
}
