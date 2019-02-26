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

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.IdDumpMessaggio;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.dao.IDumpMessaggioService;
import org.openspcoop2.monitor.engine.config.TransactionResource;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibrary;
import org.openspcoop2.monitor.engine.transaction.TransactionContentUtils;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.date.DateManager;

/**     
 * PostOutResponseHandler_ContenutiUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PostOutResponseHandler_ContenutiUtilities {

	private Logger logger;
	
	public PostOutResponseHandler_ContenutiUtilities(Logger log){
		this.logger = log;
	}
	
	public void insertContenuti(Transazione transactionDTO,
			Traccia tracciaRichiesta, Traccia tracciaRisposta,
			List<MsgDiagnostico> msgDiagnostici,
			IDumpMessaggioService dumpMessageService,
			Vector<TransactionResource> risorse,
			TransactionServiceLibrary transactionServiceLibrary,
			DAOFactory daoFactory) throws HandlerException{

		try {

			boolean updateRichiestaIngresso = false;
			boolean updateRichiestaUscita = false;
			boolean updateRispostaIngresso = false;
			boolean updateRispostaUscita = false;
			
			DumpMessaggio dumpMessaggioRichiestaIngresso = null;
			DumpMessaggio dumpMessaggioRichiestaUscita = null;
			DumpMessaggio dumpMessaggioRispostaIngresso = null;
			DumpMessaggio dumpMessaggioRispostaUscita = null;
			
			IdDumpMessaggio idDumpMessaggioRichiestaIngresso = new IdDumpMessaggio();
			idDumpMessaggioRichiestaIngresso.setIdTransazione(transactionDTO.getIdTransazione());
			idDumpMessaggioRichiestaIngresso.setTipoMessaggio(TipoMessaggio.RICHIESTA_INGRESSO);
			
			IdDumpMessaggio idDumpMessaggioRichiestaUscita = new IdDumpMessaggio();
			idDumpMessaggioRichiestaUscita.setIdTransazione(transactionDTO.getIdTransazione());
			idDumpMessaggioRichiestaUscita.setTipoMessaggio(TipoMessaggio.RICHIESTA_USCITA);
			
			IdDumpMessaggio idDumpMessaggioRispostaIngresso = new IdDumpMessaggio();
			idDumpMessaggioRispostaIngresso.setIdTransazione(transactionDTO.getIdTransazione());
			idDumpMessaggioRispostaIngresso.setTipoMessaggio(TipoMessaggio.RISPOSTA_INGRESSO);
			
			IdDumpMessaggio idDumpMessaggioRispostaUscita = new IdDumpMessaggio();
			idDumpMessaggioRispostaUscita.setIdTransazione(transactionDTO.getIdTransazione());
			idDumpMessaggioRispostaUscita.setTipoMessaggio(TipoMessaggio.RISPOSTA_USCITA);
			
			

			// ----------------------- Inserimento contenuti -------------------------
			if(risorse!=null && risorse.size()>0){
				
				
				for (TransactionResource risorsaCalcolata : risorse) {
					
					if(org.openspcoop2.core.transazioni.constants.TipoMessaggio.RICHIESTA_INGRESSO.equals(risorsaCalcolata.getTipoMessaggio())){
						
						if(dumpMessaggioRichiestaIngresso==null) {
							try{
								dumpMessaggioRichiestaIngresso = dumpMessageService.get(idDumpMessaggioRichiestaIngresso);
							}catch(NotFoundException notFound){}
						}
						
						if(dumpMessaggioRichiestaIngresso==null){
							dumpMessaggioRichiestaIngresso = new DumpMessaggio();
							dumpMessaggioRichiestaIngresso.setIdTransazione(transactionDTO.getIdTransazione());
							dumpMessaggioRichiestaIngresso.setTipoMessaggio(TipoMessaggio.RICHIESTA_INGRESSO);
							dumpMessaggioRichiestaIngresso.setDumpTimestamp(DateManager.getDate());
						}
						DumpContenuto contenuto = 
								TransactionContentUtils.createDumpContenuto(risorsaCalcolata.getNome(), 
										risorsaCalcolata.getValore(), 
										DateManager.getDate());
						dumpMessaggioRichiestaIngresso.addContenuto(contenuto);
						updateRichiestaIngresso = true;
					}
					
					else if(org.openspcoop2.core.transazioni.constants.TipoMessaggio.RICHIESTA_USCITA.equals(risorsaCalcolata.getTipoMessaggio())){
						
						if(dumpMessaggioRichiestaUscita==null) {
							try{
								dumpMessaggioRichiestaUscita = dumpMessageService.get(idDumpMessaggioRichiestaUscita);
							}catch(NotFoundException notFound){}
						}
						
						if(dumpMessaggioRichiestaUscita==null){
							dumpMessaggioRichiestaUscita = new DumpMessaggio();
							dumpMessaggioRichiestaUscita.setIdTransazione(transactionDTO.getIdTransazione());
							dumpMessaggioRichiestaUscita.setTipoMessaggio(TipoMessaggio.RICHIESTA_USCITA);
							dumpMessaggioRichiestaUscita.setDumpTimestamp(DateManager.getDate());
						}
						DumpContenuto contenuto = 
								TransactionContentUtils.createDumpContenuto(risorsaCalcolata.getNome(), 
										risorsaCalcolata.getValore(), 
										DateManager.getDate());
						dumpMessaggioRichiestaUscita.addContenuto(contenuto);
						updateRichiestaUscita = true;
					}
					
					else if(org.openspcoop2.core.transazioni.constants.TipoMessaggio.RISPOSTA_INGRESSO.equals(risorsaCalcolata.getTipoMessaggio())){
						
						if(dumpMessaggioRispostaIngresso==null) {
							try{
								dumpMessaggioRispostaIngresso = dumpMessageService.get(idDumpMessaggioRispostaIngresso);
							}catch(NotFoundException notFound){}
						}
						
						if(dumpMessaggioRispostaIngresso==null){
							dumpMessaggioRispostaIngresso = new DumpMessaggio();
							dumpMessaggioRispostaIngresso.setIdTransazione(transactionDTO.getIdTransazione());
							dumpMessaggioRispostaIngresso.setTipoMessaggio(TipoMessaggio.RISPOSTA_INGRESSO);
							dumpMessaggioRispostaIngresso.setDumpTimestamp(DateManager.getDate());
						}
						DumpContenuto contenuto = 
								TransactionContentUtils.createDumpContenuto(risorsaCalcolata.getNome(), 
										risorsaCalcolata.getValore(), 
										DateManager.getDate());
						dumpMessaggioRispostaIngresso.addContenuto(contenuto);
						updateRispostaIngresso = true;
					}
					
					else if(org.openspcoop2.core.transazioni.constants.TipoMessaggio.RISPOSTA_USCITA.equals(risorsaCalcolata.getTipoMessaggio())){
						
						if(dumpMessaggioRispostaUscita==null) {
							try{
								dumpMessaggioRispostaUscita = dumpMessageService.get(idDumpMessaggioRispostaUscita);
							}catch(NotFoundException notFound){}
						}
						
						if(dumpMessaggioRispostaUscita==null){
							dumpMessaggioRispostaUscita = new DumpMessaggio();
							dumpMessaggioRispostaUscita.setIdTransazione(transactionDTO.getIdTransazione());
							dumpMessaggioRispostaUscita.setTipoMessaggio(TipoMessaggio.RISPOSTA_USCITA);
							dumpMessaggioRispostaUscita.setDumpTimestamp(DateManager.getDate());
						}
						DumpContenuto contenuto = 
								TransactionContentUtils.createDumpContenuto(risorsaCalcolata.getNome(), 
										risorsaCalcolata.getValore(), 
										DateManager.getDate());
						dumpMessaggioRispostaUscita.addContenuto(contenuto);
						updateRispostaUscita = true;
					}
				}
			}
			
			
			
			// ----------------------- SDK -------------------------
				
			if(dumpMessaggioRichiestaIngresso==null && transactionServiceLibrary!=null){
				dumpMessaggioRichiestaIngresso = new DumpMessaggio();
				dumpMessaggioRichiestaIngresso.setIdTransazione(transactionDTO.getIdTransazione());
				dumpMessaggioRichiestaIngresso.setTipoMessaggio(TipoMessaggio.RICHIESTA_INGRESSO);
				dumpMessaggioRichiestaIngresso.setDumpTimestamp(DateManager.getDate());
			}
			
			if(dumpMessaggioRichiestaUscita==null && transactionServiceLibrary!=null){
				dumpMessaggioRichiestaUscita = new DumpMessaggio();
				dumpMessaggioRichiestaUscita.setIdTransazione(transactionDTO.getIdTransazione());
				dumpMessaggioRichiestaUscita.setTipoMessaggio(TipoMessaggio.RICHIESTA_USCITA);
				dumpMessaggioRichiestaUscita.setDumpTimestamp(DateManager.getDate());
			}
			
			if(dumpMessaggioRispostaIngresso==null && transactionServiceLibrary!=null){
				dumpMessaggioRispostaIngresso = new DumpMessaggio();
				dumpMessaggioRispostaIngresso.setIdTransazione(transactionDTO.getIdTransazione());
				dumpMessaggioRispostaIngresso.setTipoMessaggio(TipoMessaggio.RISPOSTA_INGRESSO);
				dumpMessaggioRispostaIngresso.setDumpTimestamp(DateManager.getDate());
			}
			
			if(dumpMessaggioRispostaUscita==null && transactionServiceLibrary!=null){
				dumpMessaggioRispostaUscita = new DumpMessaggio();
				dumpMessaggioRispostaUscita.setIdTransazione(transactionDTO.getIdTransazione());
				dumpMessaggioRispostaUscita.setTipoMessaggio(TipoMessaggio.RISPOSTA_USCITA);
				dumpMessaggioRispostaUscita.setDumpTimestamp(DateManager.getDate());
			}
			
			boolean messaggioModificatoSDK = false;
			if(transactionServiceLibrary!=null){
				messaggioModificatoSDK = transactionServiceLibrary.processResourcesBeforeSaveOnDatabase(transactionDTO,
						tracciaRichiesta, tracciaRisposta,
						msgDiagnostici,
						dumpMessaggioRichiestaIngresso, dumpMessaggioRichiestaUscita,
						dumpMessaggioRispostaIngresso, dumpMessaggioRispostaUscita, 
						transactionDTO.getStato(),
						this.logger,daoFactory);
			}
			
			
			
			
			// ----------------------- UPDATE -------------------------

			if(updateRichiestaIngresso || messaggioModificatoSDK){
				dumpMessageService.updateOrCreate(idDumpMessaggioRichiestaIngresso, dumpMessaggioRichiestaIngresso);
			}
			if(updateRichiestaUscita || messaggioModificatoSDK){
				dumpMessageService.updateOrCreate(idDumpMessaggioRichiestaUscita, dumpMessaggioRichiestaUscita);
			}
			
			if(updateRispostaIngresso || messaggioModificatoSDK){
				dumpMessageService.updateOrCreate(idDumpMessaggioRispostaIngresso, dumpMessaggioRispostaIngresso);
			}
			if(updateRispostaUscita || messaggioModificatoSDK){
				dumpMessageService.updateOrCreate(idDumpMessaggioRispostaUscita, dumpMessaggioRispostaUscita);
			}
			
		} catch (Exception e) {
			throw new HandlerException("Errore durante la scrittura della transazione sul database: " + e.getLocalizedMessage(), e);
		} 
	}
	
}
