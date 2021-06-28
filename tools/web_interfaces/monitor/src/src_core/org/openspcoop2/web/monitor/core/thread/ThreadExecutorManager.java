package org.openspcoop2.web.monitor.core.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

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
