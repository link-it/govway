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

package org.openspcoop2.protocol.modipa.example.soap.non_blocking.push;

import java.io.FileInputStream;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.apache.logging.log4j.Level;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.server.stub.AComplexType;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.server.stub.ErrorMessageException;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.server.stub.MRequest;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.server.stub.MRequestResponse;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.server.stub.MType;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.server.stub.SOAPCallback;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.push.server.stub.SOAPCallbackService;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * Client
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class ClientPushRequest {

   
    private ClientPushRequest() {
    }

    public static void main(String args[]) throws java.lang.Exception {
    	
    	LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
		
		java.util.Properties reader = new java.util.Properties();
		try{  
			reader.load(new FileInputStream("Client.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}
		
		String address = reader.getProperty("push.request.endpoint");
		if(address==null){
			throw new Exception("Property [endpoint] not definded");
		}
		else{
			address = address.trim();
		}
		if(address.endsWith("/")==false) {
			address = address + "/";
		}
		
		int codice = 222;
		if(args!=null && args.length>0 && !"${idResource}".equals(args[0])) {
			codice = Integer.valueOf(args[0]);
		}
		String codiceInput = reader.getProperty("idResource");
		if(codiceInput!=null) {
			codice = Integer.valueOf(codiceInput.trim());
		}
		
		
		System.out.println("Spedisco richiesta all'indirizzo: "+address);
		
		String username = reader.getProperty("push.request.username");
		if(username!=null){
			username = username.trim();
		}
		String password = reader.getProperty("push.request.password");
		if(password!=null){
			password = password.trim();
		}
		
		SOAPCallbackService service = new SOAPCallbackService();
		SOAPCallback port = service.getSOAPCallbackPort();
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
		
		String soapAction = reader.getProperty("push.request.soapAction");
		if(soapAction==null){
			throw new Exception("Property [soapAction] not definded");
		}
		else{
			soapAction = address.trim();
		}
		((BindingProvider)port).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY,  true);
		((BindingProvider)port).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY,  soapAction);
		
		String replyTo = reader.getProperty("push.request.replyTo");
		if(replyTo!=null){
			replyTo = replyTo.trim();   
		}


		 
		MRequest mRequest = new MRequest();
		MType request = new MType();
		request.setOId(codice);
		request.setA(new AComplexType());
		request.getA().getA1S().add("1");
		request.getA().getA1S().add("2");
		request.getA().setA2("RGFuJ3MgVG9vbHMgYXJlIGNvb2wh");
		request.setB("Stringa di esempio");
		mRequest.setM(request);
		
		Holder<MRequestResponse> response = new Holder<>();
		Holder<String> correlationId = new Holder<>();
		
		try {
			port.mRequest(mRequest, replyTo, response, correlationId);
			System.out.println("Ricevuto ack: "+response.value.getReturn().getOutcome());
			System.out.println("Ricevuto id di correlazione: "+correlationId.value);
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
