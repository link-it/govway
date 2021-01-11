/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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


package org.openspcoop2.example.pdd.client.as4.domibus;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.ErrorResultImpl;
import org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.ErrorResultImplArray;
import org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.StatusRequest;
import org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.GetErrorsRequest;
import org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.ListPendingMessagesResponse;
import org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.MessageStatus;
import org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.Messaging;
import org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.RetrieveMessageRequest;
import org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.RetrieveMessageResponse;
import org.openspcoop2.protocol.as4.utils.AS4StubUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;



/**
 * Client per il servizio Domibus	
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DomibusClient {
	
	private static final QName SERVICE_NAME = new QName("http://org.ecodex.backend/1_1/", "BackendService_1_1");

	public static void main (String [] argv) {

		try {

			java.util.Properties reader = new java.util.Properties();
			try{  
				InputStreamReader isr = new InputStreamReader(
					    new FileInputStream("DomibusClient.properties"), "UTF-8");
				reader.load(isr);
			}catch(java.io.IOException e) {
				System.err.println("ERROR : "+e.toString());
				return;
			}

			String url = reader.getProperty("urlServizio");
			if(url == null){
				System.err.println("ERROR : URL del Servizio non definito all'interno del file 'DomibusClient.properties'");
				return;
			}
			url = url.trim();
			
			String wsdlLocationPath = reader.getProperty("wsdlLocation");
			URL wsdlLocation = null;
			if(wsdlLocationPath == null){
				System.out.println("WARNING : Default wsdl location: " + url+ "?wsdl" );
				wsdlLocation = new URL(url+ "?wsdl");
			} else {
				wsdlLocation = new URL(url.trim());
			}


			String comando =reader.getProperty("operazione");
			if(comando == null){
				System.err.println("ERROR : operazione non definito all'interno del file 'DomibusClient.properties'");
				return;
			}
			comando = comando.trim();

			
			String username =reader.getProperty("username");
			if(username!=null){
				username = username.trim();
			}
			String password =reader.getProperty("password");
			if(password!=null){
				password = password.trim();
			}
			
			String msgID =reader.getProperty("msgID");
			if(msgID!=null){
				msgID = msgID.trim();
			}
			
			
			// Parametri invocazione
			if(("downloadMessage".equals(comando) || "getMessageStatus".equals(comando) || "getMessageErrors".equals(comando)) && (msgID==null)){
				System.err.println("ERROR : Identificativo del messaggio non definito all'interno del file 'DomibusClient.properties'");
				return;
			}

			
			org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.BackendInterface domibusPort = null;
			org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.BackendService11 domibusService = null;
			try{
				domibusService = new org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.BackendService11(wsdlLocation, SERVICE_NAME);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			domibusPort = domibusService.getBACKENDPORT();
			BindingProvider imProviderMessageBox = (BindingProvider)domibusPort;
			imProviderMessageBox.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
			if(username !=null && password!=null){
				// to use Basic HTTP Authentication: 
				imProviderMessageBox.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				imProviderMessageBox.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
			}
	
			
			
			if(comando.equals("listPendingMessages")){
				
				ListPendingMessagesResponse response = domibusPort.listPendingMessages("");
				if(response!=null){
					if(response.getMessageID()==null || response.getMessageID().size()<=0){
						System.out.println("Invocazione effettuata: nessuna richiesta pendente presente");
					}
					else{
						System.out.println("Invocazione effettuata: trovati "+response.getMessageID().size()+" messaggi");
						for (int i = 0; i < response.getMessageID().size(); i++) {
							System.out.println("Msg["+i+"]=["+response.getMessageID().get(i)+"]");
						}
					}
				}
				else{
					System.out.println("Invocazione fallita: nessuna oggetto ritornato");
				}
				
			}
			
			else if(comando.equals("retrieveMessage")){
				
				RetrieveMessageRequest msgRequest = new RetrieveMessageRequest();
				msgRequest.setMessageID(msgID);
				
				// DownloadMessageResponse msgRespone = new
				Holder<RetrieveMessageResponse> msgRespone = new Holder<RetrieveMessageResponse>();
				Holder<Messaging> headerResponse = new Holder<Messaging>();
				
				domibusPort.retrieveMessage(msgRequest, msgRespone, headerResponse);
				
				if(msgRespone.value==null){
					System.out.println("Invocazione fallita: nessuna oggetto ritornato");
				}
				if(headerResponse.value==null){
					System.out.println("Invocazione fallita: nessuna informazione as4 sull'oggetto ritornata");
				}
				
				UserMessage msg = AS4StubUtils.convertTo(headerResponse.value);
				System.out.println("Header: \n"+msg.toXml());
				System.out.println("\n\n");
				if(msgRespone.value.getPayload()==null || msgRespone.value.getPayload().size()<=0){
					System.out.println("Nessun payload associato al messaggio");
				}
				else{
					System.out.println("Invocazione effettuata: trovati "+msgRespone.value.getPayload().size()+" payload");
					String payloadName = "payload";
					for (int i = 0; i < msgRespone.value.getPayload().size(); i++) {
						String ext = "bin";
						if(msgRespone.value.getPayload().get(i).getContentType()!=null){
							String baseType = ContentTypeUtilities.readBaseTypeFromContentType(msgRespone.value.getPayload().get(i).getContentType());
							ext = MimeTypes.getInstance().getExtension(baseType);
						}
						File f = new File(payloadName+(i+1)+"."+ext);
						byte [] content = Utilities.getAsByteArray(msgRespone.value.getPayload().get(i).getValue().getInputStream());
						FileSystemUtilities.writeFile(f, content);
						System.out.println("Payload["+i+"] contentType["+msgRespone.value.getPayload().get(i).getContentType()+"] id["
								+msgRespone.value.getPayload().get(i).getPayloadId()+"] savedTo["+f.getName()+"]");
					}
				}

			}
			
			else if(comando.equals("getStatus")){
				
				StatusRequest getStatusRequest = new StatusRequest();
				getStatusRequest.setMessageID(msgID);
				
				MessageStatus status = domibusPort.getStatus(getStatusRequest);
				if(status==null){
					System.out.println("Nessun stato trovato per il messaggio");
				}
				else{
					System.out.println("Stato: "+status);
				}
				
			}
			
			else if(comando.equals("getMessageErrors")){
				
				GetErrorsRequest getErrorsRequest = new GetErrorsRequest();
				getErrorsRequest.setMessageID(msgID);
				
				ErrorResultImplArray errors = domibusPort.getMessageErrors(getErrorsRequest);
				if(errors==null || errors.getItem()==null || errors.getItem().size()<=0){
					System.out.println("Nessun errore rilevato per il messaggio");
				}
				else{
					String format = "yyyy-MM-dd_HH:mm:ss.SSS";
					SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter(format);
					int index = 1;
					for (ErrorResultImpl error : errors.getItem()) {
						String data = null;
						if(error.getTimestamp()!=null) {
							data = " (timestamp:"+dateformat.format(error.getTimestamp().toGregorianCalendar().getTime())+")";
						}
						String notified = null;
						if(error.getNotified()!=null) {
							notified = " (notified:"+dateformat.format(error.getNotified().toGregorianCalendar().getTime())+")";
						}
						System.out.println("Errore["+index+"]"+data+notified+" id:"+error.getMessageInErrorId()+ " msh-role:"+error.getMshRole()
							+ " code:"+error.getErrorCode()+" details: "+error.getErrorDetail());
						index++;
					}
				}
				
			}
			
			else{
				System.out.println("ERROR, comando non definito");
				return;	
			}


		}
		catch (Exception e) {
			System.out.println("ClientError: "+e.getMessage());
			//e.printStackTrace();
		}
	}

	

}
