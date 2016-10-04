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


package org.openspcoop2.example.pdd.client.im;


import java.io.*;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage;
import org.openspcoop2.pdd.services.cxf.ProtocolHeaderInfo;

/**
 * Client axis14 per il servizio IntegrationManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationManagerClient_cxf {
	private static final QName SERVICE_NAME_PD = new QName("http://services.pdd.openspcoop2.org", "PDService");
	private static final QName SERVICE_NAME_MessageBox = new QName("http://services.pdd.openspcoop2.org", "MessageBoxService");
	public static void main (String [] argv) {

		try {

			java.util.Properties reader = new java.util.Properties();
			try{  
				reader.load(new java.io.FileInputStream("IntegrationManagerClient.properties")); 
			}catch(java.io.IOException e) {
				System.err.println("ERROR : "+e.toString());
				return;
			}

			String url = reader.getProperty("urlServizio");
			if(url == null){
				System.err.println("ERROR : URL del Servizio non definito all'interno del file 'IntegrationManagerClient.properties'");
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
				System.err.println("ERROR : operazione non definito all'interno del file 'IntegrationManagerClient.properties'");
				return;
			}
			comando = comando.trim();

			String idMessaggio =reader.getProperty("idMessaggio");
			if(idMessaggio!=null){
				idMessaggio = idMessaggio.trim();
			}
			String riferimentoMessaggio =reader.getProperty("riferimentoMessaggio");
			if(riferimentoMessaggio!=null){
				riferimentoMessaggio = riferimentoMessaggio.trim();
			}
			String collaborazione =reader.getProperty("collaborazione");
			if(collaborazione!=null){
				collaborazione = collaborazione.trim();
			}
			String idPerRiferimento =reader.getProperty("idMessaggioInvocazionePDperRiferimento");
			if(idPerRiferimento!=null){
				idPerRiferimento = idPerRiferimento.trim();
			}
			
			// Dati Header Info SPCoop
			String tipoDestinatario =reader.getProperty("tipoDestinatario");
			if(tipoDestinatario!=null){
				tipoDestinatario = tipoDestinatario.trim();
			}
			String destinatario =reader.getProperty("destinatario");
			if(destinatario!=null){
				destinatario = destinatario.trim();
			}
			String tipoServizio =reader.getProperty("tipoServizio");
			if(tipoServizio!=null){
				tipoServizio = tipoServizio.trim();
			}
			String servizio =reader.getProperty("servizio");
			if(servizio!=null){
				servizio = servizio.trim();
			}
			String azione =reader.getProperty("azione");
			if(azione!=null){
				azione = azione.trim();
			}

			String idCorrelazioneApplicativa =reader.getProperty("identificativoCorrelazioneApplicativa");
			if(idCorrelazioneApplicativa!=null){
				idCorrelazioneApplicativa = idCorrelazioneApplicativa.trim();
			}
			String nomeServizioApplicativo =reader.getProperty("servizioApplicativo");
			if(nomeServizioApplicativo!=null){
				nomeServizioApplicativo = nomeServizioApplicativo.trim();
			}
			
			
			String counterS =reader.getProperty("counter");
			int counter = -1;
			if(counterS!=null){
				counterS = counterS.trim();
			}
			try{
				counter = Integer.parseInt(counterS);
			}catch(Exception e){}
			
			String offsetS =reader.getProperty("offset");
			int offset = -1;
			if(offsetS!=null){
				offsetS = offsetS.trim();
			}
			try{
				offset = Integer.parseInt(offsetS);
			}catch(Exception e){}

			String username =reader.getProperty("username");
			if(username!=null){
				username = username.trim();
			}
			String password =reader.getProperty("password");
			if(password!=null){
				password = password.trim();
			}
			String msgSoap = reader.getProperty("richiestaSOAP");
			if(msgSoap != null){
				msgSoap = msgSoap.trim();
			}
			String imbustamentoS =reader.getProperty("imbustamento");
			boolean imbustamento = false;
			if(imbustamentoS!=null){
				imbustamentoS = imbustamentoS.trim();
			}
			try{
				imbustamento = Boolean.parseBoolean(imbustamentoS);
			}catch(Exception e){}
			String locationPD =reader.getProperty("openspcoop.PD");
			if(locationPD != null){
				locationPD = locationPD.trim();
			}
			

			// Parametri invocazione porta delegata
			if(("invocazionePortaDelegata".equals(comando)) && (msgSoap==null)){
				System.err.println("ERROR : Messaggio di richiesta del Servizio non definito all'interno del file 'IntegrationManagerClient.properties'");
				return;
			}
			if(("invocazionePortaDelegata".equals(comando)) && (locationPD==null)){
				System.err.println("ERROR : porta delegata non definita all'interno del file 'IntegrationManagerClient.properties'");
				return;
			}

			// Parametri invocazione porta delegata per riferimento
			if(("invocazionePortaDelegataPerRiferimento".equals(comando)) && (idPerRiferimento==null)){
				System.err.println("ERROR : Identificatore Messaggio (getMessageByReference) non definito all'interno del file 'IntegrationManagerClient.properties'");
				return;
			}
			if(("invocazionePortaDelegataPerRiferimento".equals(comando)) && (locationPD==null)){
				System.err.println("ERROR : porta delegata non definita all'interno del file 'IntegrationManagerClient.properties'");
				return;
			}

			// Parametri invocazione sendRispostaAsincrona
			if(("sendRispostaAsincronaSimmetrica".equals(comando)) && (msgSoap==null)){
				System.err.println("ERROR : Messaggio di richiesta del Servizio non definito all'interno del file 'IntegrationManagerClient.properties'");
				return;
			}
			if(("sendRispostaAsincronaSimmetrica".equals(comando)) && (locationPD==null)){
				System.err.println("ERROR : porta delegata non definita all'interno del file 'IntegrationManagerClient.properties'");
				return;
			}
			if(("sendRispostaAsincronaSimmetrica".equals(comando)) && (riferimentoMessaggio==null)){
				System.err.println("ERROR : Identificatore di Correlazione non definito all'interno del file 'IntegrationManagerClient.properties'");
				return;
			}

			// Parametri invocazione sendRichiestaStato
			if(("sendRichiestaStatoAsincronaAsimmetrica".equals(comando)) && (msgSoap==null)){
				System.err.println("ERROR : Messaggio di richiesta del Servizio non definito all'interno del file 'IntegrationManagerClient.properties'");
				return;
			}
			if(("sendRichiestaStatoAsincronaAsimmetrica".equals(comando)) && (locationPD==null)){
				System.err.println("ERROR : porta delegata non definita all'interno del file 'IntegrationManagerClient.properties'");
				return;
			}
			if(("sendRichiestaStatoAsincronaAsimmetrica".equals(comando)) && (riferimentoMessaggio==null)){
				System.err.println("ERROR : Identificatore di Correlazione non definito all'interno del file 'IntegrationManagerClient.properties'");
				return;
			}

			// Invocazione effettuata non come PortaDelegata
			if( (username==null) && 
					( ("invocazionePortaDelegata".equals(comando)==false) && 
							("invocazionePortaDelegataPerRiferimento".equals(comando)==false) &&
							("sendRispostaAsincronaSimmetrica".equals(comando)==false) &&
							("sendRichiestaStatoAsincronaAsimmetrica".equals(comando)==false ) 
					)
			){
				System.out.println("ERROR : Username non definito all'interno del file 'IntegrationManagerClient.properties', obbligatorio per consultare il servizio di message box");
				return;
			}
			if( (password==null) && 
					( ("invocazionePortaDelegata".equals(comando)==false) && 
							("invocazionePortaDelegataPerRiferimento".equals(comando)==false) &&
							("sendRispostaAsincronaSimmetrica".equals(comando)==false) &&
							("sendRichiestaStatoAsincronaAsimmetrica".equals(comando)==false ) 
					) 
			){
				System.out.println("ERROR : Password non definita all'interno del file 'IntegrationManagerClient.properties', obbligatorio per consultare il servizio di message box");
				return;
			}
			
			
			org.openspcoop2.pdd.services.cxf.MessageBox imMessageBoxPort = null;
			org.openspcoop2.pdd.services.cxf.PD imPDPort = null;
			
			if(url.endsWith("MessageBox")){
				org.openspcoop2.pdd.services.cxf.MessageBoxService imMessageBoxService = null;
				try{
					imMessageBoxService = new org.openspcoop2.pdd.services.cxf.MessageBoxService(wsdlLocation, SERVICE_NAME_MessageBox);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				imMessageBoxPort = imMessageBoxService.getMessageBox();
				BindingProvider imProviderMessageBox = (BindingProvider)imMessageBoxPort;
				imProviderMessageBox.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
				if(username !=null && password!=null){
					// to use Basic HTTP Authentication: 
					imProviderMessageBox.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
					imProviderMessageBox.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				}
			}
			else{
				org.openspcoop2.pdd.services.cxf.PDService imPDService = null;
				try{
					imPDService = new org.openspcoop2.pdd.services.cxf.PDService(wsdlLocation, SERVICE_NAME_PD);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				imPDPort = imPDService.getPD();
				BindingProvider imProviderPD = (BindingProvider)imPDPort;
				imProviderPD.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
				if(username !=null && password!=null){
					// to use Basic HTTP Authentication: 
					imProviderPD.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
					imProviderPD.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				}
			}
	
			
			// Gruppo di invocazione Porta Delegata
			if(comando.equals("invocazionePortaDelegata") ||
			   comando.equals("invocazionePortaDelegataPerRiferimento") || 
			   comando.equals("sendRispostaAsincronaSimmetrica") ||
			   comando.equals("sendRichiestaStatoAsincronaAsimmetrica") ){
		
				System.out.println(comando+"()");

				// Lettura msg di richiesta (non richiesto per invocazione PD per riferimento)
				byte [] b = null;
				if( comando.equals("invocazionePortaDelegataPerRiferimento")==false ){
					java.io.FileInputStream fin = new java.io.FileInputStream(msgSoap);
					java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
					copy(fin,bout);
					fin.close();
					b = bout.toByteArray();
					System.out.println("invocazionePortaDelegata["+locationPD+"] con msgSOAP: "+new String(b));
				}else{
					System.out.println("invocazionePortaDelegata["+locationPD+"] per riferimento con id: "+idPerRiferimento);
				}
					
				// Costruzione SPCoopHeaderInfo
				ProtocolHeaderInfo spcoopHeaderInfo = new ProtocolHeaderInfo();
				spcoopHeaderInfo.setTipoDestinatario(tipoDestinatario);
				spcoopHeaderInfo.setDestinatario(destinatario);
				spcoopHeaderInfo.setTipoServizio(tipoServizio);
				spcoopHeaderInfo.setServizio(servizio);
				spcoopHeaderInfo.setAzione(azione);
				// id spcoop di collaborazione
				spcoopHeaderInfo.setIdCollaborazione(collaborazione);
				// riferimento messaggio per risposte asincrone
				spcoopHeaderInfo.setRiferimentoMessaggio(riferimentoMessaggio);
				
								
				// Costruzione Messaggio SPCoop
				org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage msg = new org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage();
				msg.setMessage(b);
				msg.setImbustamento(imbustamento);
				msg.setProtocolHeaderInfo(spcoopHeaderInfo);
				msg.setIDApplicativo(idCorrelazioneApplicativa);
				msg.setServizioApplicativo(nomeServizioApplicativo);
				
				// invoco porta delegata
				org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage msgResponse = null;
				if(comando.equals("invocazionePortaDelegata")){
					msgResponse = imPDPort.invocaPortaDelegata(locationPD,msg);
				}
				else if( comando.equals("invocazionePortaDelegataPerRiferimento") ){
					msgResponse = imPDPort.invocaPortaDelegataPerRiferimento(locationPD,msg,idPerRiferimento);
				}else if(comando.equals("sendRispostaAsincronaSimmetrica")){
					msgResponse = imPDPort.sendRispostaAsincronaSimmetrica(locationPD,msg);
				}else if(comando.equals("sendRichiestaStatoAsincronaAsimmetrica")){
					msgResponse = imPDPort.sendRichiestaStatoAsincronaAsimmetrica(locationPD,msg);
				}
				
				// Stampo Risposta
				printSPCoopMessage(msgResponse);
			}

			

			
			// getAllMessageID
			else if(comando.equals("getAllMessagesId")){
				System.out.println("getAllMessagesId()");
				List<String> ids =  imMessageBoxPort.getAllMessagesId();
				if(ids != null){
				System.out.println(ids.size() + " messaggi presenti");
				for (int i=0; i<ids.size(); i++ )
					System.out.println("Messaggio num"+(i+1)+" con ID: "+ids.get(i));
				}
				else{
					System.out.println("Lista vuota. nessun id trovato");
				}
			}

			// getAllMessageIDByService
			else if(comando.equals("getAllMessagesIdByService")){
				System.out.println("getAllMessagesIdByService()");
				List<String> ids =  imMessageBoxPort.getAllMessagesIdByService(tipoServizio,servizio,azione);
				System.out.println(ids.size() + " messaggi presenti");
				for (int i=0; i<ids.size(); i++ )
					System.out.println("Messaggio num"+(i+1)+" con ID: "+ids.get(i));
			}

			// getNextMessagesId
			else if(comando.equals("getNextMessagesId")){
				if(counter <= 0){
					System.out.println("ERROR : Numero di id (counter) non definito all'interno del file 'IntegrationManagerClient.properties'");
					return;
				}
				System.out.println("getNextMessagesId()");
				List<String> ids =  imMessageBoxPort.getNextMessagesId(counter);
				System.out.println(ids.size() + " messaggi presenti");
				for (int i=0; i<ids.size(); i++ )
					System.out.println("Messaggio num"+(i+1)+" con ID: "+ids.get(i));
			}

			// getNextMessagesIdByService
			else if(comando.equals("getNextMessagesIdByService")){
				if(counter <= 0){
					System.out.println("ERROR : Numero di id (counter) non definito all'interno del file 'IntegrationManagerClient.properties'");
					return;
				}
				System.out.println("getNextMessagesIdByService()");
				List<String> ids =  imMessageBoxPort.getNextMessagesIdByService(counter,tipoServizio,servizio,azione);
				System.out.println(ids.size() + " messaggi presenti");
				for (int i=0; i<ids.size(); i++ )
					System.out.println("Messaggio num"+(i+1)+" con ID: "+ids.get(i));
			}
			
			// getMessagesIdArray
			else if(comando.equals("getMessagesIdArray")){
				if(counter <= 0){
					System.out.println("ERROR : Numero di id (counter) non definito all'interno del file 'IntegrationManagerClient.properties'");
					return;
				}
				if(offset < 0){
					System.out.println("ERROR : Offset non definito all'interno del file 'IntegrationManagerClient.properties'");
					return;
				}
				System.out.println("getMessagesIdArray()");
				List<String> ids =  imMessageBoxPort.getMessagesIdArray(offset,counter);
				System.out.println(ids.size() + " messaggi presenti");
				for (int i=0; i<ids.size(); i++ )
					System.out.println("Messaggio num"+(i+1)+" con ID: "+ids.get(i));
			}

			// getMessagesIdArrayByService
			else if(comando.equals("getMessagesIdArrayByService")){
				if(counter <= 0){
					System.out.println("ERROR : Numero di id (counter) non definito all'interno del file 'IntegrationManagerClient.properties'");
					return;
				}
				if(offset < 0){
					System.out.println("ERROR : Offset non definito all'interno del file 'IntegrationManagerClient.properties'");
					return;
				}
				System.out.println("getMessagesIdArrayByService()");
				List<String> ids =  imMessageBoxPort.getMessagesIdArrayByService(offset,counter,tipoServizio,servizio,azione);
				System.out.println(ids.size() + " messaggi presenti");
				for (int i=0; i<ids.size(); i++ )
					System.out.println("Messaggio num"+(i+1)+" con ID: "+ids.get(i));
			}


			// getMessage
			else if(comando.equals("getMessage")){
				if(idMessaggio == null){
					System.out.println("ERROR : id EGov non definito all'interno del file 'IntegrationManagerClient.properties'");
					return;
				}
				System.out.println("getMessage()");
				org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage msg = imMessageBoxPort.getMessage(idMessaggio);
				
				//	Stampo Risposta
				printSPCoopMessage(msg);

			}

			// getMessageByReference
			else if(comando.equals("getMessageByReference")){
				if(riferimentoMessaggio == null){
					System.out.println("ERROR : Riferimento Messaggio non definito all'interno del file 'IntegrationManagerClient.properties'");
					return;
				}
				System.out.println("getMessageByReference()");
				org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage msg = imMessageBoxPort.getMessageByReference(riferimentoMessaggio);

				// Stampo Risposta
				printSPCoopMessage(msg);

			}

			// deleteMessage
			else if(comando.equals("deleteMessage")){
				if(idMessaggio == null){
					System.out.println("ERROR : id EGov non definito all'interno del file 'IntegrationManagerClient.properties'");
					return;
				}
				System.out.println("deleteMessage()");
				imMessageBoxPort.deleteMessage(idMessaggio);
			}

			// deleteMessageByReference
			else if(comando.equals("deleteMessageByReference")){
				if(riferimentoMessaggio == null){
					System.out.println("ERROR : Riferimento Messaggio non definito all'interno del file 'IntegrationManagerClient.properties'");
					return;
				}
				System.out.println("deleteMessageByReference()");
				imMessageBoxPort.deleteMessageByReference(riferimentoMessaggio);
			}

			// deleteMessages
			else if(comando.equals("deleteAllMessages")){
				System.out.println("deleteAllMessages()");
				imMessageBoxPort.deleteAllMessages();
			}
			else{
				System.out.println("ERROR, comando non definito");
				return;	
			}


		}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception ex){
			System.out.println("Ritornato messaggio di errore applicativo con CodiceEccezione["+ex.getFaultInfo().getCodiceEccezione()+"] ("+
					ex.getFaultInfo().getOraRegistrazione()+")\n"+   
					"msg di tipo ["+ex.getFaultInfo().getTipoEccezione()+"] inviato dal dominio["+ex.getFaultInfo().getIdentificativoPorta()+"] (mod.fun.["
					+ex.getFaultInfo().getIdentificativoFunzione()+"]): \n"+
					ex.getFaultInfo().getDescrizioneEccezione());
			return;
		}
		catch (Exception e) {
			System.out.println("ClientError: "+e.getMessage());
			//e.printStackTrace();
		}
	}

	// copy method from From E.R. Harold's book "Java I/O"
	public static void copy(InputStream in, OutputStream out) 
	throws IOException {

		// do not allow other threads to read from the
		// input or write to the output while copying is
		// taking place

		synchronized (in) {
			synchronized (out) {

				byte[] buffer = new byte[256];
				while (true) {
					int bytesRead = in.read(buffer);
					if (bytesRead == -1) break;
					out.write(buffer, 0, bytesRead);
				}
			}
		}
	} 

	
	
	// Stampa risposta
	public static void printSPCoopMessage(IntegrationManagerMessage msg){
		ProtocolHeaderInfo spcoopHeaderInfoRisposta = msg.getProtocolHeaderInfo();
		System.out.println("------------ SPCoopHeaderInfo risposta ------------");
		if(spcoopHeaderInfoRisposta==null){
			System.out.println("Non sono presenti informazioni.");
		}else{
			System.out.println("TipoMittente: "+spcoopHeaderInfoRisposta.getTipoMittente()+
					"   Mittente: "+spcoopHeaderInfoRisposta.getMittente());
			System.out.println("TipoDestinatario: "+spcoopHeaderInfoRisposta.getTipoDestinatario()+
					"   Destinatario: "+spcoopHeaderInfoRisposta.getDestinatario());
			System.out.println("TipoServizio: "+spcoopHeaderInfoRisposta.getTipoServizio()+
					"   Servizio: "+spcoopHeaderInfoRisposta.getServizio());
			if(spcoopHeaderInfoRisposta.getAzione()!=null){
				System.out.println("Azione: "+spcoopHeaderInfoRisposta.getAzione());
			}
			System.out.println("Identificativo e-Gov: "+spcoopHeaderInfoRisposta.getID());
			if(spcoopHeaderInfoRisposta.getIdCollaborazione()!=null){
				System.out.println("Identificativo Collaborazione: "+spcoopHeaderInfoRisposta.getIdCollaborazione());
			}
			if(spcoopHeaderInfoRisposta.getRiferimentoMessaggio()!=null){
				System.out.println("Riferimento Messaggio: "+spcoopHeaderInfoRisposta.getRiferimentoMessaggio());
			}				
		}
		System.out.println("---------------------------------------------------\n");
		System.out.println("--------------- Risposta applicativa --------------");
		if(msg.getMessage()!=null)
			System.out.println(new String(msg.getMessage()));
		else
			System.out.println("SOAP Envelope non presente");
		System.out.println("---------------------------------------------------");
	}
}
