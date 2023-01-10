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
package org.openspcoop2.monitor.engine.transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.TransazioneInfo;
import org.openspcoop2.core.transazioni.dao.ITransazioneInfoService;
import org.openspcoop2.core.transazioni.dao.ITransazioneInfoServiceSearch;
import org.openspcoop2.core.transazioni.dao.ITransazioneServiceSearch;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.monitor.engine.config.BasicServiceLibrary;
import org.openspcoop2.monitor.engine.config.BasicServiceLibraryReader;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibrary;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibraryReader;
import org.openspcoop2.utils.Utilities;
import org.slf4j.Logger;

/**
 * TransactionLibrary
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionLibrary {
	
	private static final String ID_TRANSACTION_PROCESSOR = TransactionLibrary.class.getName();
	
	private static HashMap<String,TransactionServiceLibrary> pluginTransazioni = new HashMap<String, TransactionServiceLibrary>();

	public static TransactionServiceLibrary getTransactionServiceLibrary(IDServizio idServizio,BasicServiceLibraryReader basicServiceLibraryReader,
			TransactionServiceLibraryReader transactionServiceLibraryReader,Logger log) throws Exception{
		TransactionServiceLibrary transactionServiceLibrary = null;
		if(pluginTransazioni.containsKey(idServizio.toString())){
			transactionServiceLibrary = pluginTransazioni.get(idServizio.toString());
		}
		else{
			transactionServiceLibrary = initAndGetTransactionServiceLibrary(idServizio, basicServiceLibraryReader, transactionServiceLibraryReader, log);
		}
		return transactionServiceLibrary;
	}
	private static synchronized TransactionServiceLibrary initAndGetTransactionServiceLibrary(IDServizio idServizio,BasicServiceLibraryReader basicServiceLibraryReader,
			TransactionServiceLibraryReader transactionServiceLibraryReader,Logger log) throws Exception{
		TransactionServiceLibrary transactionServiceLibrary = null;
		if(pluginTransazioni.containsKey(idServizio.toString())){
			transactionServiceLibrary = pluginTransazioni.get(idServizio.toString());
		}
		else{
			BasicServiceLibrary basicServiceLibrary = basicServiceLibraryReader.read(idServizio, log);
			if(basicServiceLibrary!=null){
				transactionServiceLibrary = transactionServiceLibraryReader.readConfigurazioneTransazione(basicServiceLibrary, log);
			}
			if(transactionServiceLibrary==null){
				transactionServiceLibrary = new TransactionServiceLibrary();
			}
			pluginTransazioni.put(idServizio.toString(), transactionServiceLibrary);
		}
		return transactionServiceLibrary;
	}
	
	public static void process(Logger logCore, DAOFactory daoFactory, boolean debug,
			int poolSize, int msgForThread) {
		
		int totaleTransazioni = 0;
		ExecutorService threadsPool = null;
		try {
			
			org.openspcoop2.core.transazioni.dao.IServiceManager serviceManagerTransazioni = (org.openspcoop2.core.transazioni.dao.IServiceManager) 
					daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
			org.openspcoop2.core.plugins.dao.IServiceManager serviceManagerPluginsBase = (org.openspcoop2.core.plugins.dao.IServiceManager) 
					daoFactory.getServiceManager(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance());
			org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager serviceManagerPluginsTransazioni = (org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) 
					daoFactory.getServiceManager(org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance());
			org.openspcoop2.core.commons.search.dao.IServiceManager serviceManagerUtils = (org.openspcoop2.core.commons.search.dao.IServiceManager) 
					daoFactory.getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			
			threadsPool = Executors.newFixedThreadPool(poolSize);
			
			
			// ServiceLibraryManager
			BasicServiceLibraryReader basicServiceLibraryReader = new BasicServiceLibraryReader(serviceManagerPluginsBase, serviceManagerUtils, debug);
			TransactionServiceLibraryReader transactionServiceLibraryReader = new TransactionServiceLibraryReader(serviceManagerPluginsTransazioni, debug);
			
			
			// Devo recuperare l'ultima data di esecuzione del processor.
			ITransazioneInfoService transazioneInfoDAO = serviceManagerTransazioni.getTransazioneInfoService();
			ITransazioneInfoServiceSearch  transazioneInfoSearchDAO = serviceManagerTransazioni.getTransazioneInfoServiceSearch();
			IExpression exprTransactionInfo = transazioneInfoSearchDAO.newExpression();
			exprTransactionInfo.
				equals(TransazioneInfo.model().TIPO, ID_TRANSACTION_PROCESSOR);
			TransazioneInfo tInfo = null;
			Date now = new Date();
			Date lastRunning = null;
			try{
				tInfo = transazioneInfoSearchDAO.find(exprTransactionInfo);
				lastRunning = tInfo.getData();
			}catch(NotFoundException notFound){
				// ignore
			}
			
			
			// Esamino le transazioni da processare
			ITransazioneServiceSearch transazioneSearchDAO = serviceManagerTransazioni.getTransazioneServiceSearch();
			IPaginatedExpression exprTransazioni = transazioneSearchDAO.newPaginatedExpression();
			exprTransazioni.and();
			if(lastRunning!=null){
				exprTransazioni.greaterThan(Transazione.model().DATA_INGRESSO_RICHIESTA, lastRunning);
			}
			exprTransazioni.lessEquals(Transazione.model().DATA_INGRESSO_RICHIESTA, now);
				
			NonNegativeNumber countTransazioniObject = transazioneSearchDAO.count(transazioneSearchDAO.toExpression(exprTransazioni));
			long countTransazioni = countTransazioniObject.longValue();
			logCore.info("Trovate ["+countTransazioni+"] transazioni da processare ...");
			
			
			exprTransazioni.sortOrder(SortOrder.ASC).addOrder(Transazione.model().DATA_INGRESSO_RICHIESTA);
			int limit = msgForThread*poolSize;
			exprTransazioni.limit(limit).offset(0);
			List<String> list = transazioneSearchDAO.findAllIds(exprTransazioni);
			
			Date lastTransactionProcessed = null;
			
			int threadNumber = 1;
			
			while(list!=null && list.size()>0){
				
				threadNumber = 1;
				
				lastTransactionProcessed = null;
				
				List<String> listIdTransazioni = new ArrayList<String>();
				
				List<TransactionProcessorThread> processorThreads = new ArrayList<TransactionProcessorThread>();
				
				for (String idTransazione : list) {

					listIdTransazioni.add(idTransazione);
					
					if(listIdTransazioni.size()==msgForThread){
						TransactionProcessorThread pt = new TransactionProcessorThread(listIdTransazioni, logCore, 
								basicServiceLibraryReader, transactionServiceLibraryReader, daoFactory,
								threadNumber, debug);
						threadsPool.execute(pt);
						processorThreads.add(pt);
						logCore.info("Avviato Thread #"+threadNumber+" ...");
						listIdTransazioni = new ArrayList<String>();
						threadNumber++;
					}
					
				}

				if(listIdTransazioni.size()>0){
					// sono rimasti messaggi da far gestire ad un thread.
					TransactionProcessorThread pt = new TransactionProcessorThread(listIdTransazioni, logCore,
							basicServiceLibraryReader, transactionServiceLibraryReader, daoFactory,
							threadNumber, debug);
					threadsPool.execute(pt);
					processorThreads.add(pt);
					logCore.info("Avviato (coda msg non piena) Thread #"+threadNumber+" ...");
					listIdTransazioni = new ArrayList<String>();
					threadNumber++;
				}
				
				int timeout = 10;
				boolean terminated = false;
				while(terminated == false){
					logCore.info((threadNumber-1)+" threads avviati correttamente, attendo terminazione (timeout "+timeout+"s) ...");
					for (int i = 0; i < timeout*4; i++) {
						boolean tmpTerminated = true;
						for (TransactionProcessorThread processorThread : processorThreads) {
							if(processorThread.isFinished()==false){
								tmpTerminated = false;
								break;
							}
						}
						if(tmpTerminated==false){
							Utilities.sleep(250);
						}
						else{
							terminated = true;
						}
					}
				}
				
				
				if(debug)
					logCore.debug("Check Last Update ...");
					
				String error = null;
				for (TransactionProcessorThread processorThread : processorThreads) {
					if(processorThread.getLastDateTransaction()!=null){
						if(lastTransactionProcessed==null){
							lastTransactionProcessed = processorThread.getLastDateTransaction();
						}
						else if(processorThread.getLastDateTransaction().after(lastTransactionProcessed)){
							lastTransactionProcessed = processorThread.getLastDateTransaction();
						}
					}
					if(error==null)
						error = processorThread.getError();
				}
				if(error!=null){
					logCore.error("Threads terminati con errori");
					return;
				}
				if(lastTransactionProcessed!=null){
					if(tInfo==null){
						tInfo = new TransazioneInfo();
						tInfo.setData(lastTransactionProcessed);
						tInfo.setTipo(ID_TRANSACTION_PROCESSOR);
						transazioneInfoDAO.create(tInfo);
					}else{
						tInfo.setData(lastTransactionProcessed);
						transazioneInfoDAO.update(tInfo);
					}
				}
				
				totaleTransazioni = totaleTransazioni + list.size();
					
				// al termine del ciclo si aggiorna il timestamp dell'ultima transazione processata
				logCore.info(totaleTransazioni+"/["+countTransazioni+"] transazioni processate (lastUpdate:"+lastTransactionProcessed+") ...");
				exprTransazioni.offset(totaleTransazioni);
				list = transazioneSearchDAO.findAllIds(exprTransazioni);
			}
						
			logCore.info(countTransazioni+"] transazioni processate correttamente");
			return;
			
		} catch (Exception e) {
			logCore.error("TransactionProcessor ha riscontrato un errore: "+e.getMessage(),e);
			return;
		}finally{
			try{
				logCore.info("Shutdown pool ...");
				threadsPool.shutdown(); 
				logCore.info("Shutdown pool ok");
			}catch(Throwable e){
				// ignore
			}
		}


	}

}
