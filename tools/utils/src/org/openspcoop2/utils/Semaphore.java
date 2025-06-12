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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

/**
 * Semaphore
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Semaphore {

	public static final long DEFAULT_LOCK_ACQUISITION_TIMEOUT_MS = 30000;
	private static long defaultLockAcquisitionTimeoutMs = DEFAULT_LOCK_ACQUISITION_TIMEOUT_MS;
	public static long getDefaultLockAcquisitionTimeoutMs() {
		return defaultLockAcquisitionTimeoutMs;
	}
	public static void setDefaultLockAcquisitionTimeoutMs(long timeoutMs) {
		defaultLockAcquisitionTimeoutMs = timeoutMs;
	}
	
	public static final long DEFAULT_LOCK_HOLD_TIMEOUT_MS = 60000;
	private static long defaultLockHoldTimeoutMs = DEFAULT_LOCK_HOLD_TIMEOUT_MS;
	public static long getDefaultLockHoldTimeoutMs() {
		return defaultLockHoldTimeoutMs;
	}
	public static void setDefaultLockHoldTimeoutMs(long timeoutMs) {
		defaultLockHoldTimeoutMs = timeoutMs;
	}
	
	private static Logger logDebug = null;
	public static Logger getLogDebug() {
		return logDebug;
	}
	public static void setLogDebug(Logger logDebug) {
		Semaphore.logDebug = logDebug;
	}

	private static boolean defaultDebug = false;
	public static boolean isDefaultDebug() {
		return defaultDebug;
	}
	public static void setDefaultDebug(boolean d) {
		defaultDebug = d;
	}
	
	private Long instanceLockAcquisitionTimeoutMs = null;
	public long getInstanceLockAcquisitionTimeoutMs() {
		return this.instanceLockAcquisitionTimeoutMs != null && this.instanceLockAcquisitionTimeoutMs.longValue()>0 ? this.instanceLockAcquisitionTimeoutMs : defaultLockAcquisitionTimeoutMs;
	}
	public void setInstanceLockAcquisitionTimeoutMs(Long timeoutMs) {
		this.instanceLockAcquisitionTimeoutMs = timeoutMs;
	}
	public void setInstanceLockAcquisitionTimeoutMs(long timeoutMs) {
		this.instanceLockAcquisitionTimeoutMs = timeoutMs;
	}
	
	private long instanceLockHoldTimeoutMs = -1;
	public long getInstanceLockHoldTimeoutMs() {
		return this.instanceLockHoldTimeoutMs>0 ? this.instanceLockHoldTimeoutMs : defaultLockHoldTimeoutMs;
	}
	public void setInstanceLockHoldTimeoutMs(long timeoutMs) {
		this.instanceLockHoldTimeoutMs = timeoutMs;
	}

	private boolean instanceDebug = false;
	public boolean isInstanceDebug() {
		return this.instanceDebug || defaultDebug;
	}
	public void setInstanceDebug(boolean d) {
		this.instanceDebug = d;
	}

	private static SemaphoreType semaphoreType = SemaphoreType.Semaphore;
	public static SemaphoreType getSemaphoreType() {
		return semaphoreType;
	}
	public static void setSemaphoreType(SemaphoreType semaphoreType) {
		Semaphore.semaphoreType = semaphoreType;
	}

	private static boolean fair = true;
	public static boolean isFair() {
		return fair;
	}
	public static void setFair(boolean fair) {
		Semaphore.fair = fair;
	}

	private final java.util.concurrent.Semaphore concurrentSemaphore;
	private final java.util.concurrent.locks.ReentrantLock reentrantLock;
	private String semaphoreName = null;
	private int permits = -1;
	
	public Semaphore(String name) {
		this(name, Semaphore.semaphoreType, Semaphore.fair);
	}
	public Semaphore(String name, boolean fair) {
		this(name, Semaphore.semaphoreType, fair);
	}
	public Semaphore(String name, SemaphoreType semaphoreType, boolean fair) {
		this.semaphoreName = name;
		switch (semaphoreType) {
		case ReentrantLock:
			this.reentrantLock = new ReentrantLock(fair); 
			this.concurrentSemaphore = null;
			break;
		case Semaphore:
			this.reentrantLock = null; 
			this.concurrentSemaphore = new java.util.concurrent.Semaphore(1, fair);
			break;
		default:
			this.reentrantLock = null;
			this.concurrentSemaphore = null;
		}
		this.permits = 1;
	}
	
	public Semaphore(String name, int permits) {
		this(name, permits, Semaphore.fair);
	}
	public Semaphore(String name, int permits, boolean fair) {
		this.semaphoreName = name;
		this.concurrentSemaphore = new java.util.concurrent.Semaphore(permits, fair);
		this.reentrantLock = null;
		this.permits = permits;
	}
	
	public boolean hasQueuedThreads() {
		if(this.concurrentSemaphore!=null) {
			return this.concurrentSemaphore.hasQueuedThreads();
		}
		else {
			return this.reentrantLock.hasQueuedThreads();
		}
	}
	public boolean available() {
		if(this.concurrentSemaphore!=null) {
			return this.concurrentSemaphore.availablePermits()>0;
		}
		else {
			return !this.reentrantLock.isLocked();
		}
	}
	public int availablePermits() {
		if(this.concurrentSemaphore!=null) {
			return this.concurrentSemaphore.availablePermits();
		}
		else {
			return -1;
		}
	}
	
	String getPrefix(String methodName, String idTransazione) {
		String idTr = "";
		if(idTransazione!=null) {
			idTr = " (idTransazione:"+idTransazione+")";
		}
		return this.semaphoreName+"."+methodName+" [Thread:"+Thread.currentThread().getName()+"]"+idTr+" ";
	}
	
	public SemaphoreLock acquire(String methodName) throws UtilsException {
		return this.acquire(methodName, null);
	}
	public SemaphoreLock acquire(String methodName, String idTransazione) throws UtilsException {
		try {
			if(this.getInstanceLockAcquisitionTimeoutMs()<=0) {
				return acquireWithoutTimeout(methodName, idTransazione);
			}
			else {
				return acquireWithTimeout(methodName, idTransazione);
			}
		}
		catch(InterruptedException ie) {
			if(this.isInstanceDebug()) {
				debug(getPrefix(methodName, idTransazione)+" acquire("+this.getInstanceLockAcquisitionTimeoutMs()+"ms) failed: "+ie.getMessage());
			}
			Thread.currentThread().interrupt();
			throw new UtilsException(ie.getMessage(),ie);
		}
	}
	private SemaphoreLock acquireWithoutTimeout(String methodName, String idTransazione) throws InterruptedException {
		if(this.isInstanceDebug()) {
			debug(getPrefix(methodName, idTransazione)+" acquire ...");
		}
		if(this.concurrentSemaphore!=null) {
			this.concurrentSemaphore.acquire();
		}
		else {
			this.reentrantLock.lock();
		}
		if(this.isInstanceDebug()) {
			debug(getPrefix(methodName, idTransazione)+" acquired");
		}
		return new SemaphoreLock(this, methodName, idTransazione);
	}
	private SemaphoreLock acquireWithTimeout(String methodName, String idTransazione) throws InterruptedException {
		if(this.isInstanceDebug()) {
			debug(getPrefix(methodName, idTransazione)+" acquire("+this.getInstanceLockAcquisitionTimeoutMs()+"ms) ...");
		}
		boolean acquire = false;
		if(this.concurrentSemaphore!=null) {
			acquire = this.concurrentSemaphore.tryAcquire(this.getInstanceLockAcquisitionTimeoutMs(), TimeUnit.MILLISECONDS);
		}
		else {
			acquire = this.reentrantLock.tryLock(this.getInstanceLockAcquisitionTimeoutMs(), TimeUnit.MILLISECONDS);
		}
		if(!acquire) {
			throw new InterruptedException("["+this.semaphoreName+"] Could not acquire semaphore after "+this.getInstanceLockAcquisitionTimeoutMs()+"ms");
		}
		if(this.isInstanceDebug()) {
			debug(getPrefix(methodName, idTransazione)+" acquired");
		}
		return new SemaphoreLock(this, methodName, idTransazione);
	}
	
	public SemaphoreLock acquireThrowRuntime(String methodName) throws SemaphoreRuntimeException {
		return this.acquireThrowRuntime(methodName, null);
	}
	public SemaphoreLock acquireThrowRuntime(String methodName, String idTransazione) throws SemaphoreRuntimeException {
		try {
			return this.acquire(methodName, idTransazione);
		}
		catch(Exception t) {
			throw new SemaphoreRuntimeException(t.getMessage(),t);
		}
	}
	
	public void release(SemaphoreLock semaphoreLock, String methodName) {
		this.release(semaphoreLock, methodName, null);
	}
	public void release(SemaphoreLock semaphoreLock, String methodName, String idTransazione) {
		if(this.isInstanceDebug()) {
			debug(getPrefix(methodName, idTransazione)+" release ...");
		}
		if(this.concurrentSemaphore!=null) {
			if(this.concurrentSemaphore.availablePermits()<this.permits) {
				this.concurrentSemaphore.release(); // altrimenti ogni release utilizzato male fa incrementare i permessi
			}
		}
		else {
			this.reentrantLock.unlock();
		}
		if(semaphoreLock!=null) {
			semaphoreLock.release(methodName, idTransazione);
		}
		if(this.isInstanceDebug()) {
			debug(getPrefix(methodName, idTransazione)+" released");
		}
	}
	
	void debug(String msg) {
		System.out.println(msg);
	}
}

@SuppressWarnings("serial")
class SemaphoreRuntimeException extends RuntimeException{

	public SemaphoreRuntimeException() {
		super();
	}

	public SemaphoreRuntimeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SemaphoreRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public SemaphoreRuntimeException(String message) {
		super(message);
	}

	public SemaphoreRuntimeException(Throwable cause) {
		super(cause);
	}
	
}
