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

package org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull;

import java.io.FileInputStream;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Holder;

import org.apache.logging.log4j.Level;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.AComplexType;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.ErrorMessageException;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MProcessingStatus;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MProcessingStatusResponse;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MRequest;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MRequestResponse;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MResponse;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MResponseResponse;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.MType;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.SOAPPull;
import org.openspcoop2.protocol.modipa.example.soap.non_blocking.pull.stub.SOAPPullService;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * Client
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Client {

	public static void main(String[] args) throws Exception {
		
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
		
		java.util.Properties reader = new java.util.Properties();
		try{  
			reader.load(new FileInputStream("Client.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}
		
		String address = reader.getProperty("endpoint");
		if(address==null){
			throw new Exception("Property [endpoint] not definded");
		}
		else{
			address = address.trim();
		}
		if(address.endsWith("/")==false) {
			address = address + "/";
		}
				
		String username = reader.getProperty("username");
		if(username!=null){
			username = username.trim();
		}
		String password = reader.getProperty("password");
		if(password!=null){
			password = password.trim();
		}
		
		SOAPPullService service = new SOAPPullService();
		SOAPPull port = service.getSOAPPullPort();
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

		String soapAction = reader.getProperty("soapAction");
		if(soapAction==null){
			throw new Exception("Property [soapAction] not definded");
		}
		else{
			soapAction = address.trim();
		}
		((BindingProvider)port).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY,  true);
		((BindingProvider)port).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY,  soapAction);
		
		String tipo = reader.getProperty("tipo");
		if(tipo==null){
			throw new Exception("Property [tipo] not definded");
		}
		else{
			tipo = tipo.trim();
		}
		
		String idCorrelazione = null;
		
		if("richiesta".equals(tipo) || "all".equals(tipo)) {
			
			System.out.println("\n===========================================");
			System.out.println("Genero nuova richiesta applicativa ...");
			
			MType request = new MType();
			request.setA(new AComplexType());
			request.getA().getA1S().add("1");
			request.getA().getA1S().add("2");
			request.getA().setA2("RGFuJ3MgVG9vbHMgYXJlIGNvb2wh");
			request.setB("Stringa di esempio");
			MRequest mRequest = new MRequest();
			mRequest.setM(request);
			
			Holder<MRequestResponse> response = new Holder<>();
			Holder<String> correlationId = new Holder<>();
			
			try {
				port.mRequest(mRequest, response, correlationId);
				System.out.println("Ricevuta risposta ("+response.value.getReturn().getStatus()+"): "+response.value.getReturn().getMessage());
				System.out.println("Ricevuto id dell'operazione per recuperare lo stato: "+correlationId.value);	
				idCorrelazione = correlationId.value;
			}catch(Exception e) {
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
		
		if("richiestaStato".equals(tipo) || "all".equals(tipo)) {
			
			if("richiestaStato".equals(tipo)) {
				if(args!=null && args.length>0 && !"${idCorrelazione}".equals(args[0])) {
					idCorrelazione = args[0];
				}
				String codiceInput = reader.getProperty("idCorrelazione");
				if(codiceInput!=null) {
					idCorrelazione = codiceInput.trim();
				}
				if(idCorrelazione==null){
					throw new Exception("Property [idCorrelazione] not definded");
				}
			}
			else {
				if(idCorrelazione==null) {
					return;
				}
			}
			

			// 1. verifico risorsa non ancora pronta prima e poi risorsa effettiva
			
			for (int i = 0; i < 2; i++) {
				
				System.out.println("\n===========================================");
				System.out.println("Genero nuova richiesta stato ...");
				
				try {
					MProcessingStatusResponse response;
					if(i==0) {
						response = port.mProcessingStatus(new MProcessingStatus(), idCorrelazione+"_NOT_READY");
					}
					else {
						response = port.mProcessingStatus(new MProcessingStatus(), idCorrelazione);
					}
					System.out.println("Ricevuta risposta ("+response.getReturn().getStatus()+"): "+response.getReturn().getMessage());
				}catch(Exception e) {
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
		
		
		if("risposta".equals(tipo) || "all".equals(tipo)) {
			
			System.out.println("\n===========================================");
			System.out.println("Recupero risposta applicativa ...");
			
			if("risposta".equals(tipo)) {
				if(args!=null && args.length>0 && !"${idCorrelazione}".equals(args[0])) {
					idCorrelazione = args[0];
				}
				String codiceInput = reader.getProperty("idCorrelazione");
				if(codiceInput!=null) {
					idCorrelazione = codiceInput.trim();
				}
				if(idCorrelazione==null){
					throw new Exception("Property [idCorrelazione] not definded");
				}
			}
			else {
				if(idCorrelazione==null) {
					return;
				}
			}
			
			try {
				MResponseResponse response = port.mResponse(new MResponse(), idCorrelazione);
				System.out.println("Ricevuta risposta: "+response.getReturn().getC());
			}catch(Exception e) {
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
}
