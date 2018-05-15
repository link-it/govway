package org.openspcoop2.monitor.engine.transaction;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.IdDumpMessaggio;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.dao.IDumpMessaggioService;
import org.openspcoop2.core.transazioni.dao.IDumpMessaggioServiceSearch;
import org.openspcoop2.core.transazioni.dao.ITransazioneServiceSearch;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.monitor.sdk.constants.ContentResourceNames;
import org.openspcoop2.monitor.sdk.constants.MessageType;
import org.openspcoop2.monitor.sdk.exceptions.TransactionException;
import org.openspcoop2.monitor.sdk.exceptions.TransactionExceptionCode;
import org.openspcoop2.monitor.sdk.transaction.AbstractContentResource;
import org.openspcoop2.monitor.sdk.transaction.Attachment;
import org.openspcoop2.monitor.sdk.transaction.AttachmentResource;
import org.openspcoop2.monitor.sdk.transaction.ContentResource;
import org.openspcoop2.monitor.sdk.transaction.SOAPEnvelopeResource;
import org.openspcoop2.monitor.sdk.transaction.Transaction;
import org.openspcoop2.monitor.sdk.transaction.TransportHeaderResource;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

/**
 * TransactionManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionManager {

	public static Transaction getTransaction(DAOFactory daoFactory, Logger log, String id, boolean debug)
			throws TransactionException {
		
		Transaction transactionInfo = null;

		ITracciaDriver driverTracciamento = null;
		IDiagnosticDriver driverDiagnostici = null;
		try {
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM = 
					(org.openspcoop2.core.transazioni.dao.IServiceManager) daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
			ITransazioneServiceSearch transazioniSearchDAO = transazioniSM.getTransazioneServiceSearch();

			driverTracciamento = (ITracciaDriver) 
					daoFactory.getServiceManager(org.openspcoop2.core.tracciamento.utils.ProjectInfo.getInstance());
			
			driverDiagnostici = (IDiagnosticDriver) 
					daoFactory.getServiceManager(org.openspcoop2.core.diagnostica.utils.ProjectInfo.getInstance());
			
			// prelevo la transazione
			IExpression exprFindTraccia = transazioniSearchDAO.newExpression();
			exprFindTraccia.equals(Transazione.model().ID_TRANSAZIONE, id);
			Transazione transazione = null;
			try{
				transazione = transazioniSearchDAO.find(exprFindTraccia);
				transactionInfo = new Transaction(log,daoFactory,transazione);
			}catch(NotFoundException notFound){
				log.info("Transazione con id ["+id+"] non trovata: "+notFound.getMessage(),notFound);
				throw new TransactionException(TransactionExceptionCode.NOT_FOUND,"Transazione con id ["+id+"] non trovata: "+notFound.getMessage(),notFound);
			}
						
			// tracce / diagnostici
			Hashtable<String, String> propertiesRicerca = new Hashtable<String, String>();
			
			propertiesRicerca.put("id_transazione", id);
			Traccia tracciaRichiesta = null;
			try{
				tracciaRichiesta = driverTracciamento.getTraccia(RuoloMessaggio.RICHIESTA, propertiesRicerca);
				transactionInfo.setRequestTrace(tracciaRichiesta);
			}catch(DriverTracciamentoNotFoundException notFound){
				if(debug)
					log.debug("Traccia di richiesta non trovata per la transazione con id ["+id+"]: "+notFound.getMessage(),notFound);
			}
			Traccia tracciaRisposta = null;
			try{
				tracciaRisposta = driverTracciamento.getTraccia(RuoloMessaggio.RISPOSTA, propertiesRicerca);
				transactionInfo.setResponseTrace(tracciaRisposta);
			}catch(DriverTracciamentoNotFoundException notFound){
				if(debug)
					log.debug("Traccia di risposta non trovata per la transazione con id ["+id+"]: "+notFound.getMessage(),notFound);
			}
			
			FiltroRicercaDiagnosticiConPaginazione filtro = new FiltroRicercaDiagnosticiConPaginazione();
			filtro.setProperties(propertiesRicerca);
			List<MsgDiagnostico> listDiagnostici = null;
			try{
				listDiagnostici = driverDiagnostici.getMessaggiDiagnostici(filtro);
				transactionInfo.setMsgdiagnosticiList(listDiagnostici);
			}catch(DriverMsgDiagnosticiNotFoundException notFound){
				if(debug)
					log.debug("Diagnostici non presenti per la transazione con id ["+id+"]: "+notFound.getMessage(),notFound);
			}
			
			// risorse di contenuto
			IDumpMessaggioServiceSearch dumpMessaggioSearchDAO = transazioniSM.getDumpMessaggioServiceSearch();
			TransactionManager.retrieveContentResources(
						dumpMessaggioSearchDAO, transactionInfo);

		} catch (Exception e) {
			log.error(
					"TransactionManager.getTransaction(" + id
							+ ") ha generato un errore: " + e.getMessage(), e);
			throw new TransactionException(TransactionExceptionCode.GENERIC_ERROR,e.getMessage(),e);
		}finally{
			try{
				driverTracciamento.close();
			}catch(Exception eClose){}
			try{
				driverDiagnostici.close();
			}catch(Exception eClose){}
		}

		return transactionInfo;
	}

	
	public static void updateContentResources(Transaction transaction)
			throws TransactionException {

		// !AGGIORNA L'IMMAGINE PRESENTE SUL DATABASE!
		// delle risorse NOME VALORE
		
		try {
			List<ContentResource> resList = transaction.getContentResourcesByType(ContentResource.class);
			if (resList.size() != 0) {
				
				org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM = 
						(org.openspcoop2.core.transazioni.dao.IServiceManager) transaction.getDAOFactory().getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
				IDumpMessaggioService dumpMessaggioDAO = transazioniSM.getDumpMessaggioService();
				IDumpMessaggioServiceSearch dumpMessaggioSearchDAO = transazioniSM.getDumpMessaggioServiceSearch();

				IPaginatedExpression expr = dumpMessaggioSearchDAO.newPaginatedExpression();
				expr.
					limit(1000).
					equals(DumpMessaggio.model().ID_TRANSAZIONE,transaction.getIdTransazione());

				List<DumpMessaggio> list = dumpMessaggioSearchDAO.findAll(expr);

				// posso trovare 0/1/2 entries nella tabella dump_messaggi per
				// ogni transazione
				for (DumpMessaggio dumpMessaggio : list) {
					
					boolean update = updateResources(dumpMessaggio, transaction);

					// Se e' necessario aggiornare il database procedo.
					if (update) {
						IdDumpMessaggio idDumpMsg = new IdDumpMessaggio();
						idDumpMsg.setIdTransazione(transaction.getIdTransazione());
						idDumpMsg.setTipoMessaggio(dumpMessaggio.getTipoMessaggio());
						dumpMessaggioDAO.update(idDumpMsg, dumpMessaggio);
					}
				}
			
			}
		} catch (Exception e) {
			transaction.getLogger().error(
					"TransactionManager.updateContentResources() ha generato un errore: "
							+ e.getMessage(), e);
			throw new TransactionException(TransactionExceptionCode.GENERIC_ERROR,e.getMessage(),e);
		}
	}

	public static boolean updateResources(DumpMessaggio dumpMessaggio, Transaction transaction) throws Exception{
		
		boolean update = false;
		
		List<ContentResource> resList = transaction.getContentResourcesByType(ContentResource.class);
		if (resList.size() != 0) {
		
			// aggiorno i valori di proprieta' esistenti e rimuovo quelle presenti che non sono nella nuova immagine passata come parametro al metodo
			List<DumpContenuto> lstContenuto = dumpMessaggio.getContenutoList();
			for (DumpContenuto dumpContenuto : lstContenuto) {
				String resName = dumpContenuto.getNome();
				ContentResource resource = (ContentResource) transaction.getContentResourceByName(resName);
				if (resource != null) {
					//if(!resource.getValue().equals(dumpContenuto.getValore())){
					if(!resource.getValue().equals(TransactionContentUtils.getDumpContenutoValue(dumpContenuto))){
						TransactionContentUtils.setDumpContenutoValue(dumpContenuto, resource.getValue());
						update = true;
					}
				} else{
					lstContenuto.remove(dumpContenuto);
					update = true;
				}
				resList.remove(resource);
			}
	
			// aggiungo le nuove proprieta' presenti nel parametro passato al metodo e non presenti su database
			for (int i = 0; i < resList.size(); i++) {
				ContentResource cr = (ContentResource) resList.get(i);
				if(resList.get(i).getName()==null){
					throw new Exception("Trovata risorsa di contenuto senza nome");
				}
				if(resList.get(i).getValue()==null){
					throw new Exception("Trovata risorsa di contenuto con nome ["+resList.get(i).getName()+"] con valore non definito");
				}
				DumpContenuto c = 
						TransactionContentUtils.createDumpContenuto(resList.get(i).getName(), 
								resList.get(i).getValue(), 
								DateManager.getDate());
				if(dumpMessaggio.getTipoMessaggio().equals(TipoMessaggio.RICHIESTA_INGRESSO) || dumpMessaggio.getTipoMessaggio().equals(TipoMessaggio.RICHIESTA_USCITA)){
					if(cr.isRequest()){
						dumpMessaggio.addContenuto(c);
						update = true;
					}
				}
				else{
					if(cr.isResponse()){
						dumpMessaggio.addContenuto(c);
						update = true;
					}
				}
				
			}
			
		}
		
		
			
		TransportHeaderResource resource = null;
		if(TipoMessaggio.RICHIESTA_INGRESSO.equals(dumpMessaggio.getTipoMessaggio()) || TipoMessaggio.RICHIESTA_USCITA.equals(dumpMessaggio.getTipoMessaggio())){
			resource = (TransportHeaderResource) transaction.getContentResourceByName(ContentResourceNames.REQ_TRANSPORT_HEADER);
		}
		else{
			resource = (TransportHeaderResource) transaction.getContentResourceByName(ContentResourceNames.RES_TRANSPORT_HEADER);
		}
		List<String> keys = new ArrayList<String>();
		if(resource!=null && resource.keys()!=null && resource.keys().size()>0){
			keys.addAll(resource.keys());
		}
		
		// aggiorno i valori di proprieta' esistenti e rimuovo quelle presenti che non sono nella nuova immagine passata come parametro al metodo
		List<DumpHeaderTrasporto> lstHeader = dumpMessaggio.getHeaderTrasportoList();
		for (DumpHeaderTrasporto dumpHeader : lstHeader) {
			String resName = dumpHeader.getNome();
			if (resource != null) {
				String valore = resource.getProperty(resName);
				if(valore!=null){
					if(!dumpHeader.getValore().equals(valore)){
						dumpHeader.setValore(valore);	
						update = true;
					}
				} else{
					lstHeader.remove(dumpHeader);
					update = true;
				}
			} else{
				lstHeader.remove(dumpHeader);
				update = true;
			}
			for (int i = 0; i < keys.size(); i++) {
				if(keys.get(i).equals(resName)){
					keys.remove(i);
					break;
				}
			}
		}

		// aggiungo le nuove proprieta' presenti nel parametro passato al metodo e non presenti su database
		for (int i = 0; i < keys.size(); i++) {
			DumpHeaderTrasporto c = new DumpHeaderTrasporto();
			if(keys.get(i)==null){
				throw new Exception("Trovato header di trasporto senza nome");
			}
			if(resource.getProperty(keys.get(i))==null){
				throw new Exception("Trovato header di trasporto con nome ["+keys.get(i)+"] con valore non definito");
			}
			c.setNome(keys.get(i));
			c.setValore(resource.getProperty(keys.get(i)));
			c.setDumpTimestamp(DateManager.getDate());
			dumpMessaggio.addHeaderTrasporto(c);
			update = true;
		}
		
		
		
		
		
		
		AttachmentResource attachResource = null;
		if(TipoMessaggio.RICHIESTA_INGRESSO.equals(dumpMessaggio.getTipoMessaggio()) || TipoMessaggio.RICHIESTA_USCITA.equals(dumpMessaggio.getTipoMessaggio())){
			attachResource = (AttachmentResource) transaction.getContentResourceByName(ContentResourceNames.REQ_ATTACHMENT);
		}
		else{
			attachResource = (AttachmentResource) transaction.getContentResourceByName(ContentResourceNames.RES_ATTACHMENT);
		}
		List<String> cids = null;
		if(attachResource!=null){
			cids = attachResource.cids();
		}
		
		// aggiorno i valori di proprieta' esistenti e rimuovo quelle presenti che non sono nella nuova immagine passata come parametro al metodo
		List<DumpAllegato> lstAllegati = dumpMessaggio.getAllegatoList();
		for (DumpAllegato dumpAllegato : lstAllegati) {
			String cid = dumpAllegato.getContentId();
			if (attachResource != null) {
				Attachment attach = attachResource.getAttachmentByContentId(cid);
				if(attach.isUpdated()){
					dumpAllegato.setContentType(attach.getContentType());
					dumpAllegato.setAllegato(attach.getContentAsByte());
					update = true;
				} 
			} else{
				lstAllegati.remove(dumpAllegato);
				update = true;
			}
			for (int i = 0; i < cids.size(); i++) {
				if(cids.get(i).equals(cid)){
					cids.remove(i);
					break;
				}
			}
		}

		// aggiungo i nuovi attach presenti nel parametro passato al metodo e non presenti su database
		for (int i = 0; i < cids.size(); i++) {
			Attachment attach = attachResource.getAttachmentByContentId(cids.get(i));
			DumpAllegato c = new DumpAllegato();
			c.setAllegato(attach.getContentAsByte());
			c.setContentId(attach.getContentID());
			c.setContentType(attach.getContentType());
			c.setDumpTimestamp(DateManager.getDate());
			dumpMessaggio.addAllegato(c);
			update = true;
		}
		
		
		
		return update;		
	}
	
	private static void addContentResource(Transaction transaction, AbstractContentResource res){
		try {
			transaction.addContentResource(res);
		} catch (TransactionException e) {
			if (TransactionExceptionCode.ADD_RES_EXIST.equals(e.getCode()) ) {
				transaction.getLogger().error(e.getMessage(),e);
			} else {
				// si sta aggiungendo una risorsa già presente,
				try {
					transaction.updateContentResource(res);
				} catch (TransactionException e1) {
					transaction.getLogger().error(e1.getMessage(),e1);
				}
			}
		}
	}
	
	private static void retrieveContentResources(
			IDumpMessaggioServiceSearch dumpMessaggioSearchDAO,
			Transaction transaction)  throws Exception{

		IPaginatedExpression expr = dumpMessaggioSearchDAO.newPaginatedExpression();
		expr.
			limit(1000).
			equals(DumpMessaggio.model().ID_TRANSAZIONE,
				transaction.getIdTransazione());

		List<DumpMessaggio> list = dumpMessaggioSearchDAO.findAll(expr);

		for (DumpMessaggio dumpMessaggio : list) {
			
			setContentResourcesInTransaction(transaction, dumpMessaggio);
			
		}



	}
	
	public static void setContentResourcesInTransaction(Transaction transaction, DumpMessaggio dumpMessaggio){
		MessageType tipoMessaggio = MessageType.valueOf(dumpMessaggio
				.get_value_tipoMessaggio());

		
		// **** SOAP ENVELOPE **** 
		
		SOAPEnvelopeResource soapEnvelope = new SOAPEnvelopeResource(tipoMessaggio);
		addContentResource(transaction, soapEnvelope);
		
		
		
		
		// **** RISORSE NOME VALORE **** 
		
		for (DumpContenuto dumpContenuto : dumpMessaggio.getContenutoList()) {
							
			ContentResource res = new ContentResource(tipoMessaggio);
			res.setName(dumpContenuto.getNome());
			res.setValue(TransactionContentUtils.getDumpContenutoValue(dumpContenuto));

			addContentResource(transaction, res);
		}
		
		
		
		// **** HEADER HTTP *****
		
		TransportHeaderResource res = new TransportHeaderResource(tipoMessaggio);
		for (DumpHeaderTrasporto dumpHeader : dumpMessaggio.getHeaderTrasportoList()) {
			res.setProperty(dumpHeader.getNome(),dumpHeader.getValore());
		}
		addContentResource(transaction, res);
		
		
		
		
		// **** ATTACHMENTS *****

		AttachmentResource attRes = new AttachmentResource(tipoMessaggio);
		for (DumpAllegato dumpAllegato : dumpMessaggio.getAllegatoList()) {
			Attachment attach = new Attachment(
					dumpAllegato.getAllegato(),
					dumpAllegato.getContentId(),
					dumpAllegato.getContentType());

			attRes.addAttachment(attach);
		}
		addContentResource(transaction, attRes);
	}




}
