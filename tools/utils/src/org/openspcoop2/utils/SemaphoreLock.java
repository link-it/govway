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
package org.openspcoop2.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * SemaphoreLock
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SemaphoreLock {

	private static ScheduledExecutorService scheduler = null;
	public static void initScheduledExecutorService() {
		scheduler = Executors.newScheduledThreadPool(1);
	}
	public static boolean isInitializedScheduledExecutorService() {
		return scheduler!=null;
	}
	public static void releaseScheduledExecutorService() {
		if(scheduler!=null) {
			scheduler.shutdown();
		}
	}
	
	private Semaphore semaphore;
	private boolean released = false;
	
	private ScheduledFuture<?> future;
		
	public SemaphoreLock(Semaphore semaphore, String methodName, String idTransazione) {
		this.semaphore = semaphore;
		if(scheduler!=null && this.semaphore.getInstanceLockHoldTimeoutMs()>0) {
			initScheduler(this.semaphore.getInstanceLockHoldTimeoutMs(), this.semaphore.isInstanceDebug(), methodName, idTransazione);
		}
	}
	
	private void initScheduler(long ms, boolean debug, String methodName, String idTransazione) {
		final String logPrefix = " schedule lock hold timeout("+ms+"ms) ";
		if(debug) {
			this.semaphore.debug(this.semaphore.getPrefix(methodName, idTransazione)+logPrefix+"...");
		}
        this.future = scheduler.schedule(() -> {
    		if(!this.released) {
        		logReleaseLockStart(debug, methodName, idTransazione, logPrefix);
        		this.semaphore.release(null, methodName, idTransazione);
        		this.released=true;
        		logReleaseLockEnd(debug, methodName, idTransazione, logPrefix);
        	}
        }, ms, TimeUnit.MILLISECONDS);
	}
	private void logReleaseLockStart(boolean debug, String methodName, String idTransazione, String logPrefix) {
		String msg = this.semaphore.getPrefix(methodName, idTransazione)+logPrefix+"expired; release lock ...";
		// in caso di wake lo registro comunque poichè non dovrebbe succedere
		if(Semaphore.getLogDebug()!=null) {
			Semaphore.getLogDebug().error(msg); 
		}
		if(debug) {
			this.semaphore.debug(msg);
		}
	}
	private void logReleaseLockEnd(boolean debug, String methodName, String idTransazione, String logPrefix) {
		String msg = this.semaphore.getPrefix(methodName, idTransazione)+logPrefix+"expired; lock released";
		if(Semaphore.getLogDebug()!=null) {
			Semaphore.getLogDebug().error(msg); 
		}
		if(debug) {
			this.semaphore.debug(msg);
		}
	}
	
	public void release(String methodName, String idTransazione) {
		if(methodName!=null && idTransazione!=null) {
			// nop
		}
		if(!this.released && scheduler!=null) {
			this.future.cancel(false); // Cancella il task, se non è ancora stato eseguito
			this.released = true;
		}
		
	}
	
}
