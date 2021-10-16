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

package org.openspcoop2.web.monitor.core.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

/**
* ThreadExecutorManager
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ThreadExecutorManager {
	
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private static ExecutorService executorRicerche;

	private static boolean initialized = false;

	private static synchronized void init() throws Exception {
		if(!initialized) {
			try{
				PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(ThreadExecutorManager.log);
				int threadNotificaPoolSize = pddMonitorProperties.getDimensionePoolRicercheConTimeout();
				ThreadExecutorManager.log.info("Predisposizione pool di ricerca su db [NumThread: "+threadNotificaPoolSize+"]" );
				executorRicerche = Executors.newFixedThreadPool(threadNotificaPoolSize);
			}catch(Exception e){
				ThreadExecutorManager.log.error(e.getMessage(), e);
				throw e;
			}
		}
		initialized = true;
	}

	public static void setup() throws Exception {
		if(!initialized) {
			init();
		}
	}

	public static void shutdown() throws InterruptedException {
		executorRicerche.shutdown();
		while (!executorRicerche.isTerminated()) {
			Thread.sleep(500);
		}
	}

	public static ExecutorService getClientPoolExecutorRicerche() {
		return executorRicerche;
	}
}
