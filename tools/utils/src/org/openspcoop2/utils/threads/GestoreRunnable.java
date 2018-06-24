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



package org.openspcoop2.utils.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * GestoreRunnable
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreRunnable extends Thread{
	
	/** Logger utilizzato per debug. */
	private RunnableLogger log = null;
	
	/** ThreadsPool */
	private ExecutorService threadsPool = null;
	private int poolSize = -1;
	private List<Runnable> threads = new ArrayList<>();
    
	/** Instance */
	private IGestoreRunnableInstance gestoreRunnable;
	
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
	
	
	/** Costruttore */
	public GestoreRunnable(String name, int poolSize, IGestoreRunnableInstance gestoreRunnable, Logger log) throws UtilsException{
		
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
				this.log.info("Non sono stati definiti threads");
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
		
		try {		
			// Avvio threads
			for (int i = 0; i < this.poolSize; i++) {
				
				String threadName = this.name+"-t"+(i+1);
				Runnable thread = this.gestoreRunnable.newRunnable(new RunnableLogger(threadName,this.log.getLog()));
				this.log.debug("Avvio thread "+threadName+" ...");
				
				this.threadsPool.execute(thread);
				this.threads.add(thread);
				this.log.debug("Avviato thread "+threadName+"");
			}			
		}catch(Throwable t) {
			this.log.error("Errore durante l'avvio dei threads: "+t.getMessage(),t);
		}
		
		
		while(this.stop == false){
			
			Utilities.sleep(1000);
			
		}
		
		try {		
			this.log.debug("Richiedo sospensione threads ...");
			// Fermo threads
			for (int i = 0; i < this.poolSize; i++) {
				this.threads.get(i).setStop(true);
			}			
		}catch(Throwable t) {
			this.log.error("Errore durante lo stop dei threads: "+t.getMessage(),t);
		}
			
		try{
			// Attendo chiusura dei threads
			int timeout = 10;
			boolean terminated = false;
			while(terminated == false){
				this.log.info((this.poolSize)+" threads avviati correttamente, attendo terminazione (timeout "+timeout+"s) ...");
				for (int i = 0; i < timeout*4; i++) {
					boolean tmpTerminated = true;
					for (Runnable processorThread : this.threads) {
						if(processorThread.isFinished()==false){
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
			
		}catch(Exception e){
			this.log.error("Errore durante l'attesa della terminazione dei threads: "+e.getMessage(),e);
		}finally{
		}
		
		
		
	}
}
