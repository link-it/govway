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

package org.openspcoop2.core.transazioni.utils;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.dao.ITransazioneApplicativoServerService;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.utils.date.DateManager;

/**     
 * TransactionServerUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionServerUtils {

	// *** SAVE ***
	
	public static boolean recover(ITransazioneApplicativoServerService transazioneService, TransazioneApplicativoServer serverInfoParam) throws Exception {
		
		String idTransazione = serverInfoParam.getIdTransazione();
		if(idTransazione==null) {
			throw new CoreException("IdTransazione non esistente nel contesto");
		}
		
		String servizioApplicativoErogatore = serverInfoParam.getServizioApplicativoErogatore();
		if(servizioApplicativoErogatore==null) {
			throw new CoreException("Id servizioApplicativoErogatore non esistente nel contesto");
		}
		
		boolean firstEntry = serverInfoParam.getDataUscitaRichiesta()==null && serverInfoParam.getDataPrelievoIm()==null && serverInfoParam.getDataEliminazioneIm()==null;
		
		if(firstEntry) {
			return save(transazioneService, serverInfoParam, false, false);		
		}
		else {
			IdTransazioneApplicativoServer idTransazioneApplicativoServer = new IdTransazioneApplicativoServer();
			idTransazioneApplicativoServer.setIdTransazione(idTransazione);
			idTransazioneApplicativoServer.setServizioApplicativoErogatore(servizioApplicativoErogatore);
			if(transazioneService.exists(idTransazioneApplicativoServer)) {
				
				TransazioneApplicativoServer transazioneApplicativoServer = transazioneService.get(idTransazioneApplicativoServer);
				if(!transazioneApplicativoServer.isConsegnaTerminata() && transazioneApplicativoServer.getDataEliminazioneIm()==null && transazioneApplicativoServer.getDataMessaggioScaduto()==null) {
					return save(transazioneService, serverInfoParam, true, false);
				}
				// else ho già registrato l'ultima informazione, è inutile fare update delle informazioni parziali.
				else {
					return false;
				}
			}
			else {
				throw new Exception("In attesa della registrazione dell'informazione capostipite");
			}
		}
		
	}
	
	public static boolean save(ITransazioneApplicativoServerService transazioneService, TransazioneApplicativoServer serverInfoParam, boolean update, boolean throwNotFoundIfNotExists) throws Exception {
		
		String idTransazione = serverInfoParam.getIdTransazione();
		if(idTransazione==null) {
			throw new CoreException("IdTransazione non esistente nel contesto");
		}
		
		String servizioApplicativoErogatore = serverInfoParam.getServizioApplicativoErogatore();
		if(servizioApplicativoErogatore==null) {
			throw new CoreException("Id servizioApplicativoErogatore non esistente nel contesto");
		}
		
		//System.out.println("SAVE id["+idTransazione+"] ["+servizioApplicativoErogatore+"] update:"+update+" ....");
		
		String protocol = serverInfoParam.getProtocollo();
		if(protocol==null) {
			throw new CoreException("Protocollo non esistente nel contesto");
		}
		
		IdTransazioneApplicativoServer idTransazioneApplicativoServer = new IdTransazioneApplicativoServer();
		idTransazioneApplicativoServer.setIdTransazione(idTransazione);
		idTransazioneApplicativoServer.setServizioApplicativoErogatore(servizioApplicativoErogatore);
		
		
		
		if(update) {
			
			// inefficente: fa 2 query
			// TransazioneApplicativoServer transazioneApplicativoServer = transazioneService.get(idTransazioneApplicativoServer);
			
			IExpression expression = transazioneService.newExpression();
			expression.equals(TransazioneApplicativoServer.model().ID_TRANSAZIONE, idTransazione);
			expression.and();
			expression.equals(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE, servizioApplicativoErogatore);
			TransazioneApplicativoServer transazioneApplicativoServerReadFromDB = null;
			try {
				transazioneApplicativoServerReadFromDB = transazioneService.find(expression);
			}catch(NotFoundException notfound) {
				if(throwNotFoundIfNotExists) {
					throw notfound;
				}
				else {
					return false;
				}
			}
						
			if(serverInfoParam.getDataMessaggioScaduto()!=null) {
				
				if( transazioneApplicativoServerReadFromDB.getDataMessaggioScaduto()!=null ) {
					return false; // l'informazione salvata sul database indica già un messaggio scaduto.
				}
				
				// date
				transazioneApplicativoServerReadFromDB.setDataMessaggioScaduto(serverInfoParam.getDataMessaggioScaduto());
				
			}
			else if(serverInfoParam.getDataEliminazioneIm()!=null) {
				
				if( transazioneApplicativoServerReadFromDB.getDataEliminazioneIm()!=null ) {
					return false; // l'informazione salvata sul database indica già un messaggio eliminato via I.M..
				}
				
				// date
				transazioneApplicativoServerReadFromDB.setDataEliminazioneIm(serverInfoParam.getDataEliminazioneIm());
				
				// cluster id
				transazioneApplicativoServerReadFromDB.setClusterIdEliminazioneIm(serverInfoParam.getClusterIdEliminazioneIm());
				
			}
			else if(serverInfoParam.getDataPrelievoIm()!=null) {
				
				if( transazioneApplicativoServerReadFromDB.getDataEliminazioneIm()!=null ) {
					return false; // l'informazione salvata sul database indica già un messaggio eliminato via I.M..
				}
				
				// date
				transazioneApplicativoServerReadFromDB.setDataPrelievoIm(serverInfoParam.getDataPrelievoIm());
				
				// data primo tentativo
				if(transazioneApplicativoServerReadFromDB.getDataPrimoPrelievoIm()==null) {
					if(serverInfoParam.getDataPrelievoIm()!=null) {
						transazioneApplicativoServerReadFromDB.setDataPrimoPrelievoIm(serverInfoParam.getDataPrelievoIm()); // primo tentativo di prelievo
					}
				}
				
				// numero tentativi
				transazioneApplicativoServerReadFromDB.setNumeroPrelieviIm(transazioneApplicativoServerReadFromDB.getNumeroPrelieviIm()+1);	
	
				// cluster id
				transazioneApplicativoServerReadFromDB.setClusterIdPrelievoIm(serverInfoParam.getClusterIdPrelievoIm());
				
			}
			else {
			
				if( transazioneApplicativoServerReadFromDB.isConsegnaTerminata() ) {
					return false; // l'informazione salvata sul database indica già un messaggio completato.
				}
				
				// ** successivi invii **
				
				// id transazione, servizio applicativo erogatore e data registrazione non sono da aggiornare
											
				// esito
				transazioneApplicativoServerReadFromDB.setConsegnaTerminata(serverInfoParam.isConsegnaTerminata());
				transazioneApplicativoServerReadFromDB.setDettaglioEsito(serverInfoParam.getDettaglioEsito());
				
				// date
				transazioneApplicativoServerReadFromDB.setDataAccettazioneRichiesta(serverInfoParam.getDataAccettazioneRichiesta());
				transazioneApplicativoServerReadFromDB.setDataUscitaRichiesta(serverInfoParam.getDataUscitaRichiesta());
				transazioneApplicativoServerReadFromDB.setDataAccettazioneRisposta(serverInfoParam.getDataAccettazioneRisposta());
				transazioneApplicativoServerReadFromDB.setDataIngressoRisposta(serverInfoParam.getDataIngressoRisposta());
				
				// dimensioni
				transazioneApplicativoServerReadFromDB.setRichiestaUscitaBytes(serverInfoParam.getRichiestaUscitaBytes());
				transazioneApplicativoServerReadFromDB.setRispostaIngressoBytes(serverInfoParam.getRispostaIngressoBytes());
			
				// location e codice risposta
				transazioneApplicativoServerReadFromDB.setLocationConnettore(serverInfoParam.getLocationConnettore());
				transazioneApplicativoServerReadFromDB.setCodiceRisposta(serverInfoParam.getCodiceRisposta());
				
				// fault
				transazioneApplicativoServerReadFromDB.setFault(serverInfoParam.getFault());
				transazioneApplicativoServerReadFromDB.setFormatoFault(serverInfoParam.getFormatoFault());
				
				// data primo tentativo
				if(transazioneApplicativoServerReadFromDB.getDataPrimoTentativo()==null) {
					if(serverInfoParam.getDataUscitaRichiesta()!=null) {
						transazioneApplicativoServerReadFromDB.setDataPrimoTentativo(serverInfoParam.getDataUscitaRichiesta()); // primo tentativo di consegna
					}
				}
				
				// numero tentativi
				transazioneApplicativoServerReadFromDB.setNumeroTentativi(transazioneApplicativoServerReadFromDB.getNumeroTentativi()+1);	
	
				// cluster id
				transazioneApplicativoServerReadFromDB.setClusterIdConsegna(serverInfoParam.getClusterIdConsegna());
				
				
				// aggiorno errore e fault se ho uno stato non ok
				
				if(!serverInfoParam.isConsegnaTerminata()) {
					
					if(serverInfoParam.getDataUscitaRichiesta()!=null) {
						transazioneApplicativoServerReadFromDB.setDataUltimoErrore(serverInfoParam.getDataUscitaRichiesta());
					}
					else {
						transazioneApplicativoServerReadFromDB.setDataUltimoErrore(serverInfoParam.getDataAccettazioneRichiesta());
					}
					
					transazioneApplicativoServerReadFromDB.setDettaglioEsitoUltimoErrore(serverInfoParam.getDettaglioEsito());
					
					transazioneApplicativoServerReadFromDB.setCodiceRispostaUltimoErrore(serverInfoParam.getCodiceRisposta());
					
					transazioneApplicativoServerReadFromDB.setUltimoErrore(serverInfoParam.getUltimoErrore());
					
					transazioneApplicativoServerReadFromDB.setLocationUltimoErrore(serverInfoParam.getLocationConnettore());
					
					transazioneApplicativoServerReadFromDB.setClusterIdUltimoErrore(serverInfoParam.getClusterIdConsegna());
									
					transazioneApplicativoServerReadFromDB.setFaultUltimoErrore(serverInfoParam.getFault());
					transazioneApplicativoServerReadFromDB.setFormatoFaultUltimoErrore(serverInfoParam.getFormatoFault());
				}
				
			}
			
			transazioneService.update(idTransazioneApplicativoServer, transazioneApplicativoServerReadFromDB);
			
			return true;
		}
		else {
			
			// ** primo invio **
			
			TransazioneApplicativoServer transazioneApplicativoServer = serverInfoParam;
			
			// protocollo
			transazioneApplicativoServer.setProtocollo(protocol);
			
			// data registrazione
			if(transazioneApplicativoServer.getDataRegistrazione()==null) {
				transazioneApplicativoServer.setDataRegistrazione(DateManager.getDate());
			}
			
			// data primo tentativo
			if(transazioneApplicativoServer.getDataUscitaRichiesta()!=null) {
				transazioneApplicativoServer.setDataPrimoTentativo(transazioneApplicativoServer.getDataUscitaRichiesta()); // primo tentativo di consegna
			}
			if(transazioneApplicativoServer.getDataPrelievoIm()!=null) {
				transazioneApplicativoServer.setDataPrimoPrelievoIm(transazioneApplicativoServer.getDataPrelievoIm());
			}			
			
			// numero tentativi
			transazioneApplicativoServer.setNumeroTentativi(0);
			transazioneApplicativoServer.setNumeroPrelieviIm(0);
			
			// cluster id
			transazioneApplicativoServer.setClusterIdPresaInCarico(serverInfoParam.getClusterIdPresaInCarico());
							
			// CREO
			transazioneService.create(transazioneApplicativoServer);
			
			return true;
		}

	}
	
}
