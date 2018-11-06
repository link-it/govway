/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.pdd.core.handlers.transazioni;

import org.openspcoop2.pdd.core.transazioni.InResponseStatefulObject;
import org.openspcoop2.pdd.core.transazioni.RepositoryGestioneStateful;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionDeletedException;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;
import org.openspcoop2.pdd.core.transazioni.TransactionStatefulNotSupportedException;
import org.openspcoop2.pdd.logger.DumpUtility;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.json.JSONUtils;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InResponseContext;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**     
 * InResponseHandler
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InResponseHandler extends FirstPositionHandler implements  org.openspcoop2.pdd.core.handlers.InResponseHandler{

	@Override
	public void invoke(InResponseContext context) throws HandlerException {
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		if(op2Properties.isTransazioniEnabled()==false) {
			return;
		}

		String idTransazione = (String) context.getPddContext().getObject(Costanti.ID_TRANSAZIONE);
		
		//System.out.println("------------- InResponseHandler ("+idTransazione+")("+context.getTipoPorta().getTipo()+") -------------------");
		
		boolean gestioneStateful = false;
		Transaction tr = null;
		try{
			tr = TransactionContext.getTransaction(idTransazione);
		}catch(TransactionNotExistsException e){
			gestioneStateful = true;
		}
		
		try{
		
			InResponseStatefulObject sObject = null;
			
			// Gestione FAULT
			String fault = null;
			String formatoFault = null;
			try{
				if(context.getMessaggio()!=null){
					if(ServiceBinding.SOAP.equals(context.getMessaggio().getServiceBinding())) {
						OpenSPCoop2SoapMessage soapMsg = context.getMessaggio().castAsSoap();
						if(soapMsg.getSOAPBody()!=null && soapMsg.getSOAPBody().hasFault()){
							
							ByteArrayOutputStream bout = new ByteArrayOutputStream();
							bout.write(context.getMessaggio().getAsByte(soapMsg.getSOAPPart().getEnvelope(), false));
							bout.flush();
							bout.close();
							
							Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioni(op2Properties.isTransazioniDebug());
							if(op2Properties.isTransazioniFaultPrettyPrint()){
								// Faccio una pretty-print: potevo fare anche direttamente passando il fault a metodo prettyPrint,
								// Pero' non veniva stampato correttamente il SOAPFault. Mi appoggio allora a SoapUtils.
								//byte [] content = org.openspcoop2.message.soap.TunnelSoapUtils.sbustamentoMessaggio(context.getMessaggio());
								byte [] content = bout.toByteArray();
								fault = DumpUtility.toString(XMLUtils.getInstance().newDocument(content), log, context.getMessaggio());
								//System.out.println("IMPOSTATO FAULT IN TRANSACTION ["+fault+"]");
							}
							else{
								
								fault = bout.toString();
							}
							
							formatoFault = soapMsg.getMessageType().name();
							
						}
					}
					else {
						OpenSPCoop2RestMessage<?> restMsg = context.getMessaggio().castAsRest();
						if(restMsg.isProblemDetailsForHttpApis_RFC7807() || MessageRole.FAULT.equals(restMsg.getMessageRole())) {
							switch (restMsg.getMessageType()) {
							case XML:
								
								ByteArrayOutputStream bout = new ByteArrayOutputStream();
								restMsg.writeTo(bout, false);
								bout.flush();
								bout.close();
								
								Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioni(op2Properties.isTransazioniDebug());
								if(op2Properties.isTransazioniFaultPrettyPrint()){
									// Faccio una pretty-print: potevo fare anche direttamente passando il fault a metodo prettyPrint,
									// Pero' non veniva stampato correttamente il SOAPFault. Mi appoggio allora a SoapUtils.
									//byte [] content = org.openspcoop2.message.soap.TunnelSoapUtils.sbustamentoMessaggio(context.getMessaggio());
									byte [] content = bout.toByteArray();
									fault = DumpUtility.toString(XMLUtils.getInstance().newDocument(content), log, context.getMessaggio());
									//System.out.println("IMPOSTATO FAULT IN TRANSACTION ["+fault+"]");
								}
								else{
									
									fault = bout.toString();
								}
								
								formatoFault = restMsg.getMessageType().name();
								
								break;
								
							case JSON:
								
								bout = new ByteArrayOutputStream();
								restMsg.writeTo(bout, false);
								bout.flush();
								bout.close();
								
								if(op2Properties.isTransazioniFaultPrettyPrint()){
									
									JSONUtils jsonUtils = JSONUtils.getInstance(true);
									byte [] content = bout.toByteArray();
									JsonNode jsonNode = jsonUtils.getAsNode(content);
									fault = jsonUtils.toString(jsonNode);
									
								}
								else{
									
									fault = bout.toString();
								}
								
								formatoFault = restMsg.getMessageType().name();
								
								break;

							default:
								break;
							}
						}
					}
				}
			}catch(Exception e){
				throw new HandlerException("Errore durante il dump del soap fault",e);
			}
						
			
			if(tr==null && gestioneStateful){
				
				sObject = new InResponseStatefulObject();
				
				//System.out.println("@@@@@REPOSITORY@@@@@ InResponseHandler ID TRANSAZIONE ["+idTransazione+"] GESTIONE COMPLETA");
				
				Date dataAccettazioneRisposta = context.getDataAccettazioneRisposta();
				// La porta di dominio mi passa sempre questa informazione.
				// Nel PddMonitor, invece, la data deve essere visualizzata solo se la dimensione e' diverso da 0 e cioe' se c'e' un messaggio di risposta.
				//if(dimensione!=null && dimensione>0){
				// L'INFORMAZIONE DEVE INVECE ESSERE SEMPRE SALVATA PER LA SIMULAZIONE DEI MESSAGGI DIAGNOSTICI
				// INEFFICENTE: RepositoryGestioneStateful.addDataAccettazioneRisposta(idTransazione, dataAccettazioneRisposta);
				sObject.setDataAccettazioneRisposta(dataAccettazioneRisposta);
				
				Date dataIngressoRisposta = context.getDataElaborazioneMessaggio();
				if(context.getDataTerminataInvocazioneConnettore()!=null) {
					dataIngressoRisposta = context.getDataTerminataInvocazioneConnettore(); // nella latenza porta deve rientrare anche la negoziazione della connessione
				}
				// La porta di dominio mi passa sempre questa informazione.
				// Nel PddMonitor, invece, la data deve essere visualizzata solo se la dimensione e' diverso da 0 e cioe' se c'e' un messaggio di risposta.
				//if(dimensione!=null && dimensione>0){
				// L'INFORMAZIONE DEVE INVECE ESSERE SEMPRE SALVATA PER LA SIMULAZIONE DEI MESSAGGI DIAGNOSTICI
				// INEFFICENTE: RepositoryGestioneStateful.addDataIngressoRisposta(idTransazione, dataElaborazioneMessaggio);
				sObject.setDataIngressoRisposta(dataIngressoRisposta);
				
				if(context.getDataPrimaInvocazioneConnettore()!=null) {
					// aggiorno informazione sulla data, poiche' piu' precisa (nella latenza porta deve rientrare anche il dump ed il rilascio della connessione)
					sObject.setDataUscitaRichiesta(context.getDataPrimaInvocazioneConnettore());
				}
				
				// INEFFICENTE: RepositoryGestioneStateful.addCodiceTrasportoRichiesta(idTransazione, context.getReturnCode()+"");
				sObject.setReturnCode(context.getReturnCode()+"");
				
				if(context.getConnettore()!=null){
					
					// INEFFICENTE: RepositoryGestioneStateful.addLocation(idTransazione, context.getConnettore().getLocation());
					sObject.setLocation(context.getConnettore().getLocation());
					
				}
				
				if(fault!=null){
					if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta())){
						sObject.setFaultIntegrazione(fault);
						sObject.setFormatoFaultIntegrazione(formatoFault);
					}
					else{
						sObject.setFaultCooperazione(fault);
						sObject.setFormatoFaultCooperazione(formatoFault);
					}
				}
			
			}else{
					
				Date dataAccettazioneRisposta = context.getDataAccettazioneRisposta();
				// La porta di dominio mi passa sempre questa informazione.
				// Nel PddMonitor, invece, la data deve essere visualizzata solo se la dimensione e' diverso da 0 e cioe' se c'e' un messaggio di risposta.
				//if(dimensione!=null && dimensione>0){
				// L'INFORMAZIONE DEVE INVECE ESSERE SEMPRE SALVATA PER LA SIMULAZIONE DEI MESSAGGI DIAGNOSTICI
				try{
					tr.setDataAccettazioneRisposta(dataAccettazioneRisposta);
					//System.out.println("SET DATA ("+dataAccettazioneRisposta.toString()+")");
				}catch(TransactionDeletedException e){
					//System.out.println("@@@@@REPOSITORY@@@@@ InResponseHandler SET DATA ("+dataAccettazioneRisposta.toString()+")");
					// INEFFICENTE: RepositoryGestioneStateful.addDataAccettazioneRisposta(idTransazione, dataAccettazioneRisposta);
					if(sObject==null)
						sObject = new InResponseStatefulObject();
					sObject.setDataAccettazioneRisposta(dataAccettazioneRisposta);
				}
				
				Date dataIngressoRisposta = context.getDataElaborazioneMessaggio();
				if(context.getDataTerminataInvocazioneConnettore()!=null) {
					dataIngressoRisposta = context.getDataTerminataInvocazioneConnettore(); // nella latenza porta deve rientrare anche la negoziazione della connessione
				}
				// La porta di dominio mi passa sempre questa informazione.
				// Nel PddMonitor, invece, la data deve essere visualizzata solo se la dimensione e' diverso da 0 e cioe' se c'e' un messaggio di risposta.
				//if(dimensione!=null && dimensione>0){
				// L'INFORMAZIONE DEVE INVECE ESSERE SEMPRE SALVATA PER LA SIMULAZIONE DEI MESSAGGI DIAGNOSTICI
				try{
					tr.setDataIngressoRisposta(dataIngressoRisposta);
					//System.out.println("SET DATA ("+dataElaborazioneMessaggio.toString()+")");
				}catch(TransactionDeletedException e){
					//System.out.println("@@@@@REPOSITORY@@@@@ InResponseHandler SET DATA ("+dataElaborazioneMessaggio.toString()+")");
					// INEFFICENTE: RepositoryGestioneStateful.addDataIngressoRisposta(idTransazione, dataElaborazioneMessaggio);
					if(sObject==null)
						sObject = new InResponseStatefulObject();
					sObject.setDataIngressoRisposta(dataIngressoRisposta);
				}
					
				if(context.getDataPrimaInvocazioneConnettore()!=null) {
					// aggiorno informazione sulla data, poiche' piu' precisa (nella latenza porta deve rientrare anche il dump ed il rilascio della connessione)
					try{
						tr.setDataUscitaRichiesta(context.getDataPrimaInvocazioneConnettore());
						//System.out.println("SET DATA ("+dataElaborazioneMessaggio.toString()+")");
					}catch(TransactionDeletedException e){
						//System.out.println("@@@@@REPOSITORY@@@@@ InResponseHandler SET DATA ("+dataElaborazioneMessaggio.toString()+")");
						// INEFFICENTE: RepositoryGestioneStateful.addDataIngressoRisposta(idTransazione, dataElaborazioneMessaggio);
						if(sObject==null)
							sObject = new InResponseStatefulObject();
						sObject.setDataUscitaRichiesta(context.getDataPrimaInvocazioneConnettore());
					}
				}
				
				try{
					//System.out.println("SET CODICE TRASPORTO RICHIESTA ["+context.getReturnCode()+"]");
					tr.setCodiceTrasportoRichiesta(context.getReturnCode()+"");
				}catch(TransactionDeletedException e){
					//System.out.println("@@@@@REPOSITORY@@@@@ InResponseHandler SET CODICE TRASPORTO RICHIESTA ["+context.getReturnCode()+"]");
					// INEFFICENTE: RepositoryGestioneStateful.addCodiceTrasportoRichiesta(idTransazione, context.getReturnCode()+"");
					if(sObject==null)
						sObject = new InResponseStatefulObject();
					sObject.setReturnCode(context.getReturnCode()+"");
				}
				
				try{
					// update location impostata nel OutRequest (location modificata dal connettore)
					//System.out.println("SET LOCATION ["+context.getConnettore().getLocation()+"]");
					tr.setLocation(context.getConnettore().getLocation());
				}catch(TransactionDeletedException e){
					//System.out.println("@@@@@REPOSITORY@@@@@ OutRequestHandler SET LOCATION ["+context.getConnettore().getLocation()+"]");
					// INEFFICENTE: RepositoryGestioneStateful.addLocation(idTransazione, context.getConnettore().getLocation());
					if(sObject==null)
						sObject = new InResponseStatefulObject();
					sObject.setLocation(context.getConnettore().getLocation());
				}
				
				try{
					if(fault!=null){
						if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta())){
							tr.setFaultIntegrazione(fault);
							tr.setFormatoFaultIntegrazione(formatoFault);
						}
						else{
							tr.setFaultCooperazione(fault);
							tr.setFormatoFaultCooperazione(formatoFault);
						}
					}
				}catch(TransactionDeletedException e){
					if(fault!=null){
						if(sObject==null)
							sObject = new InResponseStatefulObject();
						if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta())){
							sObject.setFaultIntegrazione(fault);
							sObject.setFormatoFaultIntegrazione(formatoFault);
						}
						else{
							sObject.setFaultCooperazione(fault);
							sObject.setFormatoFaultCooperazione(formatoFault);
						}
					}
				}
			
			}
			
			if(sObject!=null){
				// Gestione stateful
				RepositoryGestioneStateful.addInResponseStatefulObject(context.getProtocolFactory().getProtocol(),idTransazione, sObject);
			}
			
		}catch(TransactionStatefulNotSupportedException e){
			throw new HandlerException("Errore durante il processamento dell'handler: "+e.getMessage(),e);
		}
		
	}

}
