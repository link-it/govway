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



package org.openspcoop2.utils.threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * GestoreCodaRunnable
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreCodaRunnable extends Thread{
	
	/** Logger utilizzato per debug. */
	private RunnableLogger log = null;
	
	/** ThreadsPool */
	private ExecutorService threadsPool = null;
	private int poolSize = -1;
	private int queueSize = -1;
	private int limit = -1;
	private Map<String, Runnable> threads = new HashMap<>();
    
	/** Sleep */
	private int timeoutNextCheck;
	
	/** Instance */
	private IGestoreCodaRunnableInstance gestoreRunnable;
	
	/** Nome */
	private String name;
	
    // VARIABILE PER STOP
	private boolean stop = false;
	
	public boolean isStop() {
		return this.stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	public String getThreadsImage() {
		if(this.threadsPool instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor tpe = (ThreadPoolExecutor) this.threadsPool;
			return
	                String.format("(queue:%d) [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
	                		this.threads.size(),
	                		tpe.getPoolSize(),
	                		tpe.getCorePoolSize(),
	                		tpe.getActiveCount(),
	                		tpe.getCompletedTaskCount(),
	                		tpe.getTaskCount(),
	                		tpe.isShutdown(),
	                		tpe.isTerminated());
		}
		return null;
	}
	
	
	/** Costruttore */
	public GestoreCodaRunnable(String name, int poolSize, int queueSize, int limit, int timeoutNextCheck, IGestoreCodaRunnableInstance gestoreRunnable, Logger log) throws UtilsException{
		
		this.name = name;
		this.log = new RunnableLogger(name, log);
		this.gestoreRunnable = gestoreRunnable;
		
		try {
			if(this.gestoreRunnable!=null) {
				this.gestoreRunnable.initialize(this.log);
			}
		}catch(Throwable t) {
			throw new UtilsException(t.getMessage(),t);
		}   

		try {

			this.poolSize = poolSize;
			if(this.poolSize>0) {
				this.threadsPool = Executors.newFixedThreadPool(this.poolSize);
				this.log.info("Inizializzato correttamente");
			}
			else {
				this.log.error("Non sono stati definiti threads");
			}
			
			this.queueSize = queueSize;
			if(this.queueSize<=0) {
				this.log.error("Non è stata definita la dimensione della coda");
			}
			
			this.limit = limit;
			if(this.limit<=0) {
				this.log.error("Non è stata definito il limite di quanti thread creare per volta");
			}
			
			this.timeoutNextCheck = timeoutNextCheck;
			if(this.limit<=0) {
				this.log.error("Non è stata definito il timeout di attesa prima di verificare la presenza di nuovi threads da attivare");
			}
			
		}catch(Exception e) {
			throw new UtilsException("Inizializzazione pool di threads non riuscita: "+e.getMessage(),e);
		}
		
		
	}
	

	/**
	 * Metodo che fa partire il Thread. 
	 *
	 */
	@Override
	public void run(){
		
		if(this.threadsPool==null) {
			return; // termino subito
		}
		
		HashMap<String, Object> context = new HashMap<String, Object>();
		this.gestoreRunnable.logCheckInProgress(context);
		
		while(this.stop == false) {
						
			// Print actual image status
			this.log.info("Immagine prima del controllo sui threads terminati: "+this.getThreadsImage());
			
			// Verifico se nella tabella dei threads registrati vi sono thread terminati
			if(!this.threads.isEmpty()) {
				this.log.debug("Verifico se tra i threads registrati ve ne sono alcuni terminati ...");
				List<String> ids = new ArrayList<>();
				ids.addAll(this.threads.keySet());
				for (String id : ids) {
					Runnable r = this.threads.get(id);
					if(r.isFinished()) {
						this.log.debug("Elimino dalla coda thread '"+id+"' terminato");
						this.threads.remove(id);
					}
				}
			}
			
			// Print actual image status
			this.log.info("Immagine dopo il controllo sui threads terminati: "+this.getThreadsImage());
			
			// Se vi è la possibilità di inserire in coda nuovi threads lo faccio
			int limit = this.queueSize - this.threads.size();
			boolean sleep = false;
			if(limit>0) {
				if(limit>this.limit) {
					limit = this.limit;
				}
				this.log.info("Ricerco nuovi threads da attivare (limit: "+limit+") ...");
				List<Runnable> list_nextRunnable = null;
				try {
					list_nextRunnable = this.gestoreRunnable.nextRunnable(limit);
				}catch(Throwable t) {
					this.log.error("Errore durante la ricerca di nuovi threads (limit: "+limit+"): "+t.getMessage(),t);
				}
				if(list_nextRunnable!=null && !list_nextRunnable.isEmpty()) {
					this.log.info("Trovati "+list_nextRunnable.size()+" threads da attivare");
					for (Runnable thread : list_nextRunnable) {
						String threadName = this.name+"-t"+getUniqueSerialNumber();
						if(thread.getIdentifier()!=null && !"".equals(thread.getIdentifier())) {
							threadName = threadName+"-"+thread.getIdentifier();
						}
						try {
							this.log.debug("Aggiungo in coda nuovo thread '"+threadName+"' ...");
							thread.initialize(new RunnableLogger(threadName,this.log.getLog()));
							this.threadsPool.execute(thread);
							this.threads.put(threadName, thread);
							this.log.info("Thread '"+threadName+"' aggiunto in coda");
						}catch(Throwable t) {
							this.log.error("Errore durante l'aggiunta in coda del thread '"+threadName+"': "+t.getMessage(),t);
						}
					}
					
					// Print actual image status
					this.log.info("Immagine dopo l'inserimento in coda dei nuovi threads: "+this.getThreadsImage());
					
					this.gestoreRunnable.logRegisteredThreads(context, list_nextRunnable.size());
				}
				else {
					this.log.info("Trovati "+0+" threads da attivare");
					sleep = true;
				}
			}
			else {
				this.log.info("La coda dei threads ha raggiunto la capacità massima (size: "+this.queueSize+")");
				sleep = true;
			}
			
			if(sleep) {
				
				this.gestoreRunnable.logCheckFinished(context);
				
				sleepForNextCheck();
				
				context = new HashMap<String, Object>();
				this.gestoreRunnable.logCheckInProgress(context);
			}
		}

		
		try {		
			this.log.debug("Richiedo sospensione threads ...");
			// Fermo threads
			Set<String> keySet = this.threads.keySet();
			for (String threadName : keySet) {
				Runnable thread = this.threads.get(threadName);
				thread.setStop(true);
			}			
		}catch(Throwable t) {
			this.log.error("Errore durante lo stop dei threads: "+t.getMessage(),t);
		}
			
		try{
			// Attendo chiusura dei threads
			int timeout = 10;
			boolean terminated = false;
			while(terminated == false){
				this.log.info((this.threads.size())+" threads avviati correttamente, attendo terminazione (timeout "+timeout+"s) ...");
				for (int i = 0; i < timeout*4; i++) {
					boolean tmpTerminated = true;
					Set<String> keySet = this.threads.keySet();
					for (String threadName : keySet) {
						Runnable thread = this.threads.get(threadName);
						if(thread.isFinished()==false){
							tmpTerminated = false;
							break;
						}
					}
					if(tmpTerminated==false){
						org.openspcoop2.utils.Utilities.sleep(250);
					}
					else{
						terminated = true;
					}
				}
			}
			this.log.info((this.threads.size())+" threads avviati correttamente, attesa della terminazione (timeout "+timeout+"s) completata");
			
		}catch(Exception e){
			this.log.error("Errore durante l'attesa della terminazione dei threads: "+e.getMessage(),e);
		}finally{
		}
		
		
		
	}
	
	private void sleepForNextCheck() {
		// CheckInterval
		if(this.stop==false){
			int i=0;
			while(i<this.timeoutNextCheck){
				Utilities.sleep(1000);
				if(this.stop){
					break; // thread terminato, non lo devo far piu' dormire
				}
				i++;
			}
		}
	}
	
	private long uniqueSerialNumber = 0;
	private synchronized long getUniqueSerialNumber(){
		if((this.uniqueSerialNumber+1) > Long.MAX_VALUE){
			this.uniqueSerialNumber = 0;
		} 
		this.uniqueSerialNumber++;
		return this.uniqueSerialNumber;
	}
}
