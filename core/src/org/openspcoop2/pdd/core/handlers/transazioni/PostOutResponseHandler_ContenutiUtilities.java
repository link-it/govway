package org.openspcoop2.pdd.core.handlers.transazioni;

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
			TransactionServiceLibrary transactionServiceLibrary) throws HandlerException{
/*
		try {

			boolean updateRichiesta = false;
			boolean updateRisposta = false;
			DumpMessaggio dumpMessaggioRichiesta = null;
			DumpMessaggio dumpMessaggioRisposta = null;
			
			IdDumpMessaggio idDumpMessaggioRichiesta = new IdDumpMessaggio();
			idDumpMessaggioRichiesta.setIdTransazione(transactionDTO.getIdTransazione());
			idDumpMessaggioRichiesta.setTipoMessaggio(TipoMessaggio.RICHIESTA);
			
			IdDumpMessaggio idDumpMessaggioRisposta = new IdDumpMessaggio();
			idDumpMessaggioRisposta.setIdTransazione(transactionDTO.getIdTransazione());
			idDumpMessaggioRisposta.setTipoMessaggio(TipoMessaggio.RISPOSTA);
			
			

			// ----------------------- Inserimento contenuti -------------------------
			if(risorse!=null && risorse.size()>0){
				
				try{
					dumpMessaggioRichiesta = dumpMessageService.get(idDumpMessaggioRichiesta);
				}catch(NotFoundException notFound){}
				try{
					dumpMessaggioRisposta = dumpMessageService.get(idDumpMessaggioRisposta);
				}catch(NotFoundException notFound){}
				
				for (TransactionResource risorsaCalcolata : risorse) {
					
					if(org.openspcoop2.core.transazioni.constants.TipoMessaggio.RICHIESTA.equals(risorsaCalcolata.getTipoMessaggio())){
						if(dumpMessaggioRichiesta==null){
							dumpMessaggioRichiesta = new DumpMessaggio();
							dumpMessaggioRichiesta.setIdTransazione(transactionDTO.getIdTransazione());
							dumpMessaggioRichiesta.setTipoMessaggio(TipoMessaggio.RICHIESTA);
							dumpMessaggioRichiesta.setDumpTimestamp(DateManager.getDate());
						}
						DumpContenuto contenuto = 
								TransactionContentUtils.createDumpContenuto(risorsaCalcolata.getNome(), 
										risorsaCalcolata.getValore(), 
										DateManager.getDate());
						dumpMessaggioRichiesta.addContenuto(contenuto);
						updateRichiesta = true;
					}
					else{
						if(dumpMessaggioRisposta==null){
							dumpMessaggioRisposta = new DumpMessaggio();
							dumpMessaggioRisposta.setIdTransazione(transactionDTO.getIdTransazione());
							dumpMessaggioRisposta.setTipoMessaggio(TipoMessaggio.RISPOSTA);
							dumpMessaggioRisposta.setDumpTimestamp(DateManager.getDate());
						}
						DumpContenuto contenuto = 
								TransactionContentUtils.createDumpContenuto(risorsaCalcolata.getNome(), 
										risorsaCalcolata.getValore(), 
										DateManager.getDate());
						dumpMessaggioRisposta.addContenuto(contenuto);
						updateRisposta = true;
					}
				}
			}
			
			
			
			// ----------------------- SDK -------------------------
				
			if(dumpMessaggioRichiesta==null && transactionServiceLibrary!=null){
				dumpMessaggioRichiesta = new DumpMessaggio();
				dumpMessaggioRichiesta.setIdTransazione(transactionDTO.getIdTransazione());
				dumpMessaggioRichiesta.setTipoMessaggio(TipoMessaggio.RICHIESTA);
				dumpMessaggioRichiesta.setDumpTimestamp(DateManager.getDate());
			}
			
			if(dumpMessaggioRisposta==null && transactionServiceLibrary!=null){
				dumpMessaggioRisposta = new DumpMessaggio();
				dumpMessaggioRisposta.setIdTransazione(transactionDTO.getIdTransazione());
				dumpMessaggioRisposta.setTipoMessaggio(TipoMessaggio.RISPOSTA);
				dumpMessaggioRisposta.setDumpTimestamp(DateManager.getDate());
			}
			
			boolean messaggioModificatoSDK = false;
			if(transactionServiceLibrary!=null){
				messaggioModificatoSDK = transactionServiceLibrary.processResourcesBeforeSaveOnDatabase(transactionDTO,
						tracciaRichiesta, tracciaRisposta,
						msgDiagnostici,
						dumpMessaggioRichiesta,dumpMessaggioRisposta,transactionDTO.getStato(),
						this.logger,daoFactory);
			}
			
			
			
			
			// ----------------------- UPDATE -------------------------

			if(updateRichiesta || messaggioModificatoSDK){
				dumpMessageService.updateOrCreate(idDumpMessaggioRichiesta, dumpMessaggioRichiesta);
			}
			
			if(updateRisposta || messaggioModificatoSDK){
				dumpMessageService.updateOrCreate(idDumpMessaggioRisposta, dumpMessaggioRisposta);
			}
			
		} catch (Exception e) {
			throw new HandlerException("Errore durante la scrittura della transazione sul database: " + e.getLocalizedMessage(), e);
		} */
	}
	
}
