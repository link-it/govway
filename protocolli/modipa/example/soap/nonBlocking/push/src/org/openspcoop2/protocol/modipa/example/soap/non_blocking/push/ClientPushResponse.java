/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

package org.openspcoop2.protocol.modipa.example.soap.non_blocking.push;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.apache.logging.log4j.Level;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.client.stub.ErrorMessageException;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.client.stub.MRequestResponse;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.client.stub.MRequestResponseResponse;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.client.stub.MResponseType;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.client.stub.SOAPCallbackClient;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.client.stub.SOAPCallbackClientService;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * Client
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class ClientPushResponse {

   
    private ClientPushResponse() {
    }

    @SuppressWarnings("unchecked")
	public static void main(String args[]) throws java.lang.Exception {
    	
    	LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
		
		java.util.Properties reader = new java.util.Properties();
		try{  
			reader.load(new FileInputStream("Client.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}
		
		String address = reader.getProperty("push.response.endpoint");
		if(address==null){
			throw new Exception("Property [endpoint] not definded");
		}
		else{
			address = address.trim();
		}
		if(address.endsWith("/")==false) {
			address = address + "/";
		}
		
		String idCorrelazione = null;
		if(args!=null && args.length>0 && !"${idCorrelazione}".equals(args[0])) {
			idCorrelazione = args[0];
		}
		String idCorrelazioneInput = reader.getProperty("push.response.correlationId");
		if(idCorrelazioneInput!=null) {
			idCorrelazione = idCorrelazioneInput.trim();
		}
//		if(idCorrelazione==null) {
//			throw new Exception("Property [correlationId] not definded");
//		}
		boolean addHeaderHTTP = false;
		if(idCorrelazione==null) {
			// provo ad usare meccanismo di integrazione govway
			//address = address +"?govway_relates_to=XXGWXX";
			address = address +"?govway_conversation_id=XXGWXX";
			
			// o header
			addHeaderHTTP = true;
		}
		
		System.out.println("Spedisco richiesta all'indirizzo (correlationId '"+idCorrelazione+"'): "+address);
		
		String username = reader.getProperty("push.response.username");
		if(username!=null){
			username = username.trim();
		}
		String password = reader.getProperty("push.response.password");
		if(password!=null){
			password = password.trim();
		}
		
		SOAPCallbackClientService service = new SOAPCallbackClientService();
		SOAPCallbackClient port = service.getSOAPCallbackClientPort();
        /*
         * Imposto la url della porta di dominio come destinazione
         * Imposto username e password per l'autenticazione
         */
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  address);
		
		if(username!=null && password!=null){
			System.out.println("Basic Auth username["+username+"] password["+password+"]");
	        ((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY,  username);
	    	((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,  password);
		}
		
		String soapAction = reader.getProperty("push.response.soapAction");
		if(soapAction==null){
			throw new Exception("Property [soapAction] not definded");
		}
		else{
			soapAction = address.trim();
		}
		((BindingProvider)port).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY,  true);
		((BindingProvider)port).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY,  soapAction);
		
		if(addHeaderHTTP) {
			Map<String, Object> context = ((BindingProvider)port).getRequestContext();
		    Map<String, List<String>> headers = null;
			if (context.containsKey(MessageContext.HTTP_REQUEST_HEADERS)) {
		        headers = (Map<String, List<String>>)
		                   context.get(MessageContext.HTTP_REQUEST_HEADERS);
		    }
		    if (headers == null) {
		        headers = new HashMap<String, List<String>>();
		        context.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
		    }
		    List<String> l = new ArrayList<String>();
		    l.add("XXGWXXHDR");
		    headers.put("X-Correlation-ID", l );
		}

		MRequestResponse mResponse = new MRequestResponse();
		MResponseType response = new MResponseType();
		response.setC("OK");
		mResponse.setReturn(response);
				
		try {
			MRequestResponseResponse responseResponse = port.mRequestResponse(mResponse, idCorrelazione);
			System.out.println("Ricevuto ack: "+responseResponse.getReturn().getOutcome());
		}
		catch(Exception e) {
			System.out.println("!Errore!");
			if(e instanceof ErrorMessageException) {
				ErrorMessageException eM = (ErrorMessageException) e;
				System.out.println("Eccezione applicativa ("+eM.getFaultInfo().getCustomFaultCode()+"): "+eM.getMessage());
			}
			else {
				System.out.println("Eccezione generica: "+e.getMessage());
				e.printStackTrace(System.out);
			}
		}
    	
    }
    
}
