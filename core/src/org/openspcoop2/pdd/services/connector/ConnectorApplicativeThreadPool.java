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

package org.openspcoop2.pdd.services.connector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;

/**
 * ConnectorApplicativeThreadPool
 *
 * @author Poli Andrea (apoli@link.it)
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnectorApplicativeThreadPool {
	
	private ConnectorApplicativeThreadPool() {}
	
	private static ExecutorService syncRequestPool;
	
	private static ExecutorService asyncRequestPool;
	private static ExecutorService asyncResponsePool;
	
	public static void initialize(OpenSPCoop2Properties op2) {
		if(!op2.isBIOConfigSyncClientUseCustomMessageObjectEntity()) {
			int size = op2.getBIOConfigSyncClientApplicativeThreadPoolSize();
			if(size>0) {
				syncRequestPool = Executors.newFixedThreadPool(size);
			}
		}
		
		if(op2.isNIOEnabled()) {
			if(op2.isNIOConfigAsyncRequestStreamEnabled()) {
				int size = op2.getNIOConfigAsyncRequestApplicativeThreadPoolSize();
				if(size>0) {
					asyncRequestPool = Executors.newFixedThreadPool(size);
				}
			}
			if(op2.isNIOConfigAsyncResponseStreamEnabled()) {
				int size = op2.getNIOConfigAsyncResponseApplicativeThreadPoolSize();
				if(size>0) {
					asyncResponsePool = Executors.newFixedThreadPool(size);
				}
			}
		}
	}
	
	
	public static void executeInSyncRequestPool(Runnable runnable) {
		syncRequestPool.execute(runnable);
	}
	
	public static void executeInAsyncRequestPool(Runnable runnable) {
		asyncRequestPool.execute(runnable);
	}
	public static void executeInAsyncResponsePool(Runnable runnable) {
		asyncResponsePool.execute(runnable);
	}
	
	public static ExecutorService getSyncRequestPool() {
		return syncRequestPool;
	}

	public static ExecutorService getAsyncRequestPool() {
		return asyncRequestPool;
	}
	public static ExecutorService getAsyncResponsePool() {
		return asyncResponsePool;
	}
	
	public static void shutdown() {
		if(syncRequestPool!=null) {
			syncRequestPool.shutdown();
		}
		
		if(asyncRequestPool!=null) {
			asyncRequestPool.shutdown();
		}
		if(asyncResponsePool!=null) {
			asyncResponsePool.shutdown();
		}
	}
	
	public static String getSyncRequestPoolThreadsImage() {
		if(syncRequestPool instanceof ThreadPoolExecutor tpe) {
			return getThreadsImage(tpe);
		}
		return null;
	}
	
	public static String getAsyncRequestPoolThreadsImage() {
		if(asyncRequestPool instanceof ThreadPoolExecutor tpe) {
			return getThreadsImage(tpe);
		}
		return null;
	}
	public static String getAsyncResponsePoolThreadsImage() {
		if(asyncResponsePool instanceof ThreadPoolExecutor tpe) {
			return getThreadsImage(tpe);
		}
		return null;
	}
	
	private static String getThreadsImage(ThreadPoolExecutor tpe) {
		return
                String.format("(queue:%d) [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                		tpe.getQueue()!=null ? tpe.getQueue().size() : -1,
                		tpe.getPoolSize(),
                		tpe.getCorePoolSize(),
                		tpe.getActiveCount(),
                		tpe.getCompletedTaskCount(),
                		tpe.getTaskCount(),
                		tpe.isShutdown(),
                		tpe.isTerminated());
	}
}
