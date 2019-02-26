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
package org.openspcoop2.monitor.engine.transaction;

import java.util.Date;
import java.util.List;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.monitor.engine.config.BasicServiceLibraryReader;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibrary;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibraryReader;
import org.openspcoop2.monitor.sdk.plugins.ITransactionProcessing;
import org.openspcoop2.monitor.sdk.transaction.Transaction;
import org.slf4j.Logger;

/**
 * TransactionProcessorThread
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionProcessorThread implements Runnable {

	private List<String> idTransazioni;
	private Logger log;
	
	private BasicServiceLibraryReader basicServiceLibraryReader;
	private TransactionServiceLibraryReader transactionServiceLibraryReader;
	private DAOFactory daoFactory;
	
	private int threadNumber;
	private boolean finished = false;

	private boolean debug;

	public TransactionProcessorThread(List<String> idTransazioni,Logger log,
			BasicServiceLibraryReader basicServiceLibraryReader,
			TransactionServiceLibraryReader transactionServiceLibraryReader,
			DAOFactory daoFactory,
			int threadNumber,
			boolean debug){
		this.idTransazioni = idTransazioni;
		this.log = log;
		this.basicServiceLibraryReader = basicServiceLibraryReader;
		this.transactionServiceLibraryReader = transactionServiceLibraryReader;
		this.daoFactory = daoFactory;
		this.threadNumber = threadNumber;
		this.debug = debug;
	}
	
	private Date lastDateTransaction;
	private String error;
	

	@Override
	public void run() {
		
		String identificativo = null;
		try{
		
			for (int i = 0; i < this.idTransazioni.size(); i++) {
				
				String idTransazione  = this.idTransazioni.get(i);
				Transaction transaction = TransactionManager.getTransaction(this.daoFactory,this.log,idTransazione, this.debug);
				
				identificativo = idTransazione+"-(#T-"+this.threadNumber+" #M-"+(i+1)+")";
				
				if(this.debug)
					this.log.debug("["+identificativo+"] Check Transazione ["+i+"]=["+idTransazione+"] ...");
				
				IDServizio idServizio = null;
				if(transaction.getTransaction().getTipoSoggettoErogatore()!=null && transaction.getTransaction().getNomeSoggettoErogatore()!=null &&
						transaction.getTransaction().getTipoServizio()!=null && transaction.getTransaction().getNomeServizio()!=null){
					idServizio =  IDServizioFactory.getInstance().getIDServizioFromValues(transaction.getTransaction().getTipoServizio(), 
							transaction.getTransaction().getNomeServizio(), 
							transaction.getTransaction().getTipoSoggettoErogatore(), 
							transaction.getTransaction().getNomeSoggettoErogatore(), 
							transaction.getTransaction().getVersioneServizio());
					idServizio.setAzione(transaction.getTransaction().getAzione());
				}
				
				if(idServizio!=null){
				
					if(this.debug)
						this.log.debug("["+identificativo+"] Search and Apply plugins SDK for idService["+idServizio.toString()+"] ...");
					
					TransactionServiceLibrary transactionServiceLibrary = null;
					try{
						transactionServiceLibrary = TransactionLibrary.getTransactionServiceLibrary(idServizio, this.basicServiceLibraryReader, 
								this.transactionServiceLibraryReader, this.log);
					}catch(NotFoundException notFound){
						if(this.debug)
							this.log.debug("["+identificativo+"] Library not found: "+notFound.getMessage(),notFound);
					}
					if(transactionServiceLibrary!=null){
					
						List<ITransactionProcessing> listPlugins = transactionServiceLibrary.mergeServiceActionTransactionLibrary_sdkPlugins(this.log);
						if(listPlugins!=null && listPlugins.size()>0){
							for (ITransactionProcessing iTransactionProcessing : listPlugins) {
								if(this.debug)
									this.log.debug("["+identificativo+"] Applico plugin ["+iTransactionProcessing.getClass().getName()+"] per la transazione " + transaction.getIdTransazione()+" ...");
								iTransactionProcessing.postProcessTransaction(transaction);
								if(this.debug)
									this.log.info("["+identificativo+"] Applicato plugin ["+iTransactionProcessing.getClass().getName()+"] per la transazione " + transaction.getIdTransazione());
							}
						}
						
						// Aggiorno stato su db della transazione
						TransactionManager.updateContentResources(transaction);
						
					}
					
				}
				
				this.lastDateTransaction = transaction.getTransaction().getDataIngressoRichiesta();
				
				if(this.debug)
					this.log.debug("["+identificativo+"] Check Transazione ["+i+"]=["+idTransazione+"] ...");
				
			}
			
			
			this.log.info("Thread #"+this.threadNumber+" finished");
			
		}catch(Exception e){
			String msg = "Thread #"+this.threadNumber+" finished with error (id in corso di gestione: "+identificativo+"): "+e.getMessage();
			this.log.error(msg,e);
			this.error = msg;
		}
		finally{
			this.finished = true;
		}
		
	}

	public boolean isFinished() {
		return this.finished;
	}
	public Date getLastDateTransaction() {
		return this.lastDateTransaction;
	}
	public String getError() {
		return this.error;
	}
}
