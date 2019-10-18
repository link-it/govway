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
package org.openspcoop2.pdd.core.handlers.transazioni;

import org.openspcoop2.pdd.core.transazioni.OutRequestStatefulObject;
import org.openspcoop2.pdd.core.transazioni.RepositoryGestioneStateful;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionDeletedException;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;
import org.openspcoop2.pdd.core.transazioni.TransactionStatefulNotSupportedException;

import java.util.Date;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutRequestContext;

/**     
 * OutRequestHandler
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OutRequestHandler extends LastPositionHandler implements  org.openspcoop2.pdd.core.handlers.OutRequestHandler{

	@Override
	public void invoke(OutRequestContext context) throws HandlerException {

		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		if(op2Properties.isTransazioniEnabled()==false) {
			return;
		}
		
		String idTransazione = (String) context.getPddContext().getObject(Costanti.ID_TRANSAZIONE);
		
		//System.out.println("------------- OutRequestHandler ("+idTransazione+")("+context.getTipoPorta().getTipo()+") -------------------");
		
		
		
		if(context.getTransazioneApplicativoServer()!=null) {
			
			try{
			
				context.getTransazioneApplicativoServer().setDataUscitaRichiesta(context.getDataElaborazioneMessaggio());
				
				if(context.getConnettore()!=null){
					context.getTransazioneApplicativoServer().setLocationConnettore(context.getConnettore().getLocation());
				}
				
			}catch(Exception e){
				throw new HandlerException("Errore durante il processamento delle informazioni relative alla consegna per l'applicativo '"+context.getTransazioneApplicativoServer().getServizioApplicativoErogatore()+"': "+e.getMessage(),e);
			}
			
		}
		else {
		
		
			//if(context.getIntegrazione()!=null)
			//	System.out.println("GESTIONE STATELESS OutRequestHandler ["+context.getIntegrazione().isGestioneStateless()+"] ["+context.getTipoPorta()+"]");
			if(context.getIntegrazione()!=null && 
					context.getIntegrazione().isGestioneStateless()!=null &&
					!context.getIntegrazione().isGestioneStateless()){
				if(op2Properties.isTransazioniStatefulEnabled()==false){
					throw new HandlerException("Gestione delle transazioni stateful non abilita");
				}
			}
			
			boolean gestioneStateful = false;
			Transaction tr = null;
			try{
				tr = TransactionContext.getTransaction(idTransazione);
			}catch(TransactionNotExistsException e){
				gestioneStateful = true;
			}
					
			try{
			
				OutRequestStatefulObject sObject = null;
				
				if(tr==null && gestioneStateful){
					
					sObject = new OutRequestStatefulObject();
					
					//System.out.println("@@@@@REPOSITORY@@@@@ OutRequestHandler ID TRANSAZIONE ["+idTransazione+"] GESTIONE COMPLETA");
									
					Date dataElaborazioneMessaggio = context.getDataElaborazioneMessaggio();
					// INEFFICENTE: RepositoryGestioneStateful.addDataUscitaRichiesta(idTransazione, dataElaborazioneMessaggio);
					sObject.setDataUscitaRichiesta(dataElaborazioneMessaggio);
									
					if(context.getProtocollo()!=null){
						// INEFFICENTE: RepositoryGestioneStateful.addScenarioCooperazione(idTransazione, context.getProtocollo().getScenarioCooperazione());
						sObject.setScenarioCooperazione(context.getProtocollo().getScenarioCooperazione());
					}
					
					if(context.getConnettore()!=null){
						
						// INEFFICENTE: RepositoryGestioneStateful.addTipoConnettore(idTransazione, context.getConnettore().getTipoConnettore());
						sObject.setTipoConnettore(context.getConnettore().getTipoConnettore());
						
						// INEFFICENTE: RepositoryGestioneStateful.addLocation(idTransazione, context.getConnettore().getLocation());
						sObject.setLocation(context.getConnettore().getLocation());
						
					}
					
					if(context.getIntegrazione()!=null){
						
						// INEFFICENTE: RepositoryGestioneStateful.addServizioApplicativoErogatore(context.getIntegrazione().getServizioApplicativoErogatore(i));
						for (int i = 0; i < context.getIntegrazione().sizeServiziApplicativiErogatori(); i++) {
							sObject.addServizioApplicativoErogatore(context.getIntegrazione().getServizioApplicativoErogatore(i));	
						}
						
					}
					
				}
				
				else{
				
					Date dataElaborazioneMessaggio = context.getDataElaborazioneMessaggio();
					try{
						tr.setDataUscitaRichiesta(dataElaborazioneMessaggio);
						//System.out.println("SET DATA ("+dataElaborazioneMessaggio.toString()+")");
					}catch(TransactionDeletedException e){
						//System.out.println("@@@@@REPOSITORY@@@@@ OutRequestHandler SET DATA ("+dataElaborazioneMessaggio.toString()+")");
						// INEFFICENTE: RepositoryGestioneStateful.addDataUscitaRichiesta(idTransazione, dataElaborazioneMessaggio);
						if(sObject==null)
							sObject = new OutRequestStatefulObject();
						sObject.setDataUscitaRichiesta(dataElaborazioneMessaggio);
					}
					
					try{
						if(context.getProtocollo()!=null){
							//System.out.println("SET SCENARIO ["+context.getProtocollo().getScenarioCooperazione()+"]");
							tr.setScenarioCooperazione(context.getProtocollo().getScenarioCooperazione());
						}
					}catch(TransactionDeletedException e){
						//System.out.println("@@@@@REPOSITORY@@@@@ OutRequestHandler SET SCENARIO ["+context.getProtocollo().getScenarioCooperazione()+"]");
						// INEFFICENTE: RepositoryGestioneStateful.addScenarioCooperazione(idTransazione, context.getProtocollo().getScenarioCooperazione());
						if(sObject==null)
							sObject = new OutRequestStatefulObject();
						sObject.setScenarioCooperazione(context.getProtocollo().getScenarioCooperazione());
					}
					
					if(context.getConnettore()!=null){
						
						try{
							//System.out.println("SET TIPO CONNETTORE ["+context.getConnettore().getTipoConnettore()+"]");
							tr.setTipoConnettore(context.getConnettore().getTipoConnettore());
						}catch(TransactionDeletedException e){
							//System.out.println("@@@@@REPOSITORY@@@@@ OutRequestHandler SET TIPO CONNETTORE ["+context.getConnettore().getTipoConnettore()+"]");
							// INEFFICENTE: RepositoryGestioneStateful.addTipoConnettore(idTransazione, context.getConnettore().getTipoConnettore());
							if(sObject==null)
								sObject = new OutRequestStatefulObject();
							sObject.setTipoConnettore(context.getConnettore().getTipoConnettore());
						}
						
						try{
							//System.out.println("SET LOCATION ["+context.getConnettore().getLocation()+"]");
							tr.setLocation(context.getConnettore().getLocation());
						}catch(TransactionDeletedException e){
							//System.out.println("@@@@@REPOSITORY@@@@@ OutRequestHandler SET LOCATION ["+context.getConnettore().getLocation()+"]");
							// INEFFICENTE: RepositoryGestioneStateful.addLocation(idTransazione, context.getConnettore().getLocation());
							if(sObject==null)
								sObject = new OutRequestStatefulObject();
							sObject.setLocation(context.getConnettore().getLocation());
						}
						
					}
					
					if(context.getIntegrazione()!=null){
						
						for (int i = 0; i < context.getIntegrazione().sizeServiziApplicativiErogatori(); i++) {
							try{
								//	System.out.println("ADD SERVIZIO APPLICATIVO EROGATORE ["+context.getConnettore().getLocation()+"]");
								tr.addServizioApplicativoErogatore(context.getIntegrazione().getServizioApplicativoErogatore(i));
							}catch(TransactionDeletedException e){
								//System.out.println("@@@@@REPOSITORY@@@@@ OutRequestHandler ADD SERVIZIO APPLICATIVO EROGATORE ["+context.getIntegrazione().getServizioApplicativoErogatore(i)+"]");
								// INEFFICENTE: RepositoryGestioneStateful.addServizioApplicativoErogatore(context.getIntegrazione().getServizioApplicativoErogatore(i));
								if(sObject==null)
									sObject = new OutRequestStatefulObject();
								sObject.addServizioApplicativoErogatore(context.getIntegrazione().getServizioApplicativoErogatore(i));	
							}
						}
						
					}
				}
				
				
				if(sObject!=null){
					// Gestione stateful
					RepositoryGestioneStateful.addOutRequestStatefulObject(context.getProtocolFactory().getProtocol(),idTransazione, sObject);
				}
				
			}catch(TransactionStatefulNotSupportedException e){
				throw new HandlerException("Errore durante il processamento dell'handler: "+e.getMessage(),e);
			}
		}
		
	}


}