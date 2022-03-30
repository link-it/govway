/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

/**
 * AsyncThreadPool
 *
 * @author Poli Andrea (apoli@link.it)
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AsyncThreadPool {

	private static ExecutorService requestPool;
	private static ExecutorService responsePool;
	
	public static void initialize(int requestSizePool, int responseSizePool) {
		if(requestSizePool>0) {
			requestPool = Executors.newFixedThreadPool(requestSizePool);
		}
		if(responseSizePool>0) {
			responsePool = Executors.newFixedThreadPool(responseSizePool);
		}
	}
	
	public static void executeInRequestPool(Runnable runnable) {
		requestPool.execute(runnable);
	}
	public static void executeInResponsePool(Runnable runnable) {
		responsePool.execute(runnable);
	}
	
	public static ExecutorService getRequestPool() {
		return requestPool;
	}
	public static ExecutorService getResponsePool() {
		return responsePool;
	}
	
	public static void shutdown() {
		if(requestPool!=null) {
			requestPool.shutdown();
		}
		if(responsePool!=null) {
			responsePool.shutdown();
		}
	}
	
	public String getRequestPoolThreadsImage() {
		if(requestPool instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor tpe = (ThreadPoolExecutor) requestPool;
			return getThreadsImage(tpe);
		}
		return null;
	}
	public String getResponsePoolThreadsImage() {
		if(responsePool instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor tpe = (ThreadPoolExecutor) responsePool;
			return getThreadsImage(tpe);
		}
		return null;
	}
	private String getThreadsImage(ThreadPoolExecutor tpe) {
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
