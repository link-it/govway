/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;

/**
 * Runnable
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Runnable extends Thread{

	/** Logger utilizzato per debug. */
	private RunnableLogger log = null;

	// VARIABILE PER STOP
	private boolean stop = false;
	
	public boolean isStop() {
		return this.stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	private boolean finished = false;
	public boolean isFinished() {
		return this.finished;
	}

	private int checkIntervalMs = -1; // ogni X ms reinvoco l'instance
	private IRunnableInstance instance;
	
	private boolean initialized = false; 
	
	/** Costruttore */
	public Runnable(RunnableLogger runnableLogger, IRunnableInstance instance,int checkIntervalMs) throws UtilsException{
		
		this.initialized = true;
		
		this.log = runnableLogger;
		this.instance = instance;
		this.checkIntervalMs = checkIntervalMs;
		
		this.instance.initialize(runnableLogger);
		
		this.log.info("Avviato");
	}
	
	public Runnable(IRunnableInstance instance,int checkIntervalMs) throws UtilsException{
		
		this.instance = instance;
		this.checkIntervalMs = checkIntervalMs;
		
	}
	public void initialize(RunnableLogger runnableLogger)  throws UtilsException{
				
		this.initialized = true;
		
		this.log = runnableLogger;
		
		this.instance.initialize(runnableLogger);
		
		this.log.info("Avviato");
	}
	public String getIdentifier() {
		return this.instance.getIdentifier();
	}
	
	/**
	 * Metodo che fa partire il Thread. 
	 *
	 */
	@Override
	public void run(){
		
		if(this.initialized==false) {
			return; // non inizializzato correttamente
		}
		
		while(this.stop == false){
			
			try{
				this.instance.check();
			}catch(Exception e){
				this.log.error("Errore generale: "+e.getMessage(),e);
			}finally{
			}
			
			if(this.instance.isContinuousRunning()==false) {
				// dopo una invocazione del metodo 'check' il thread termina
				this.stop = true;
			}
					
			// CheckInterval
			if(this.stop==false){
				if(this.checkIntervalMs<=1000) {
					Utilities.sleep(1000);
				}
				else {		
					int checkIntervalSeconds = this.checkIntervalMs / 1000;
					int checkInterval_resto = this.checkIntervalMs % 1000;
					int i=0;
					while(i<checkIntervalSeconds){
						Utilities.sleep(1000);
						if(this.stop){
							break; // thread terminato, non lo devo far piu' dormire
						}
						i++;
					}
					if(!this.stop && checkInterval_resto>0){
						Utilities.sleep(checkInterval_resto);
					}
				}
			}
		} 
		
		this.finished=true;
	}
}
