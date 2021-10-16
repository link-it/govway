/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.util.concurrent.TimeUnit;

/**
 * Semaphore
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Semaphore {

	public static long TIMEOUT_MS = 30000;
	public static boolean DEBUG = false;
	
	private final java.util.concurrent.Semaphore semaphore = new java.util.concurrent.Semaphore(1, true);
	private String semaphoreName = null;
	
	public Semaphore(String name) {
		this.semaphoreName = name;
	}
	
	public boolean hasQueuedThreads() {
		return this.semaphore.hasQueuedThreads();
	}
	public boolean available() {
		return this.semaphore.availablePermits()>0;
	}
	
	private String getPrefix(String methodName, String idTransazione) {
		String idTr = "";
		if(idTransazione!=null) {
			idTr = " (idTransazione:"+idTransazione+")";
		}
		return this.semaphoreName+"."+methodName+" [Thread:"+Thread.currentThread().getName()+"]"+idTr+" ";
	}
	
	public void acquire(String methodName) throws InterruptedException {
		this.acquire(methodName, null);
	}
	public void acquire(String methodName, String idTransazione) throws InterruptedException {
		try {
			if(TIMEOUT_MS<=0) {
				if(DEBUG) {
					System.out.println(getPrefix(methodName, idTransazione)+" acquire ...");
				}
				this.semaphore.acquire();
				if(DEBUG) {
					System.out.println(getPrefix(methodName, idTransazione)+" acquired");
				}
			}
			else {
				if(DEBUG) {
					System.out.println(getPrefix(methodName, idTransazione)+" acquire("+TIMEOUT_MS+"ms) ...");
				}
				boolean acquire = this.semaphore.tryAcquire(TIMEOUT_MS, TimeUnit.MILLISECONDS);
				if(!acquire) {
					throw new InterruptedException("["+this.semaphoreName+"] Could not acquire semaphore after "+TIMEOUT_MS+"ms");
				}
				if(DEBUG) {
					System.out.println(getPrefix(methodName, idTransazione)+" acquired");
				}
			}
		}catch(InterruptedException ie) {
			if(DEBUG) {
				System.out.println(getPrefix(methodName, idTransazione)+" acquire("+TIMEOUT_MS+"ms) failed: "+ie.getMessage());
			}
			throw ie;
		}
	}
	
	public void acquireThrowRuntime(String methodName) {
		this.acquireThrowRuntime(methodName, null);
	}
	public void acquireThrowRuntime(String methodName, String idTransazione) {
		try {
			this.acquire(methodName, idTransazione);
		}catch(Throwable t) {
			throw new RuntimeException(t.getMessage(),t);
		}
	}
	
	public void release(String methodName) {
		this.release(methodName, null);
	}
	public void release(String methodName, String idTransazione) {
		if(DEBUG) {
			System.out.println(getPrefix(methodName, idTransazione)+" release ...");
		}
		this.semaphore.release();
		if(DEBUG) {
			System.out.println(getPrefix(methodName, idTransazione)+" released");
		}
	}
}
