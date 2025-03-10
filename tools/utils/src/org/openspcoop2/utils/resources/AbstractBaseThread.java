/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.resources;

import org.openspcoop2.utils.Utilities;

/**
 * BaseThread
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractBaseThread extends Thread {

    // VARIABILE PER STOP
	private boolean stop = false;
	
	public boolean isStop() {
		return this.stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	// VARIABILE PER CONTROLLARE OGNI QUANTO VIENE ESEGUITA LA BUSINESS LOGIC DEL TIMER
	
	private int timeout = 10; // ogni 10 secondi per default
	
	public int getTimeout() {
		return this.timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	// VARIABILE PER CONTROLLARE LO STATO DI VITA
	
	private boolean finished = false;
	
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	public void finished() {
		this.finished = true;
	}
	public boolean isFinished() {
		return this.finished;
	}
	public void waitShutdown() {
		this.waitShutdown(200, 60000);
	}
	public void waitShutdown(int checkMs, int maxMs) {
		int msWait = 0;
		while(!this.finished && msWait<maxMs) {
			Utilities.sleep(checkMs);
			msWait = msWait + checkMs;
		}
	}
	
	// METODI DI UTILITA
	
	public void sleepForNextCheck(int maxLimitSleepCount, int sleepMs) {
		// CheckInterval
		if(!this.stop){
			int i=0;
			while(i<maxLimitSleepCount){
				Utilities.sleep(sleepMs);
				if(this.isStop()){
					break; // thread terminato, non lo devo far piu' dormire
				}
				i++;
			}
		}
	}
	

	// RUN

	protected boolean initialize() {
		return true;
	}
	protected abstract void process();
	protected void close() {}
	
	@Override
	public void run(){
		
		try {
			
			if(!this.initialize()) {
				return;
			}
			
			while(!this.isStop()){
				
				this.process();
				
				// CheckInterval
				this.sleepForNextCheck(this.timeout, 1000);
			}
			
		}finally {
			this.finished();
		}
		
		try {
			this.close();
		}catch(Exception t) {
			// ignore
		}
	}

}
