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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

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
	
	// I thread attivi oltre ai seguenti sono i worker thread del web server e il pool di thread utilizzato dalla libreria client NIO
	
	/* Per i client della libreria NIO utilizzando CloseableHttpAsyncClient (dalla libreria Apache HttpComponents), 
	 * il pool di thread predefinito dipende dal java.util.concurrent.ExecutorService utilizzato internamente. 
	 * Se non configurato esplicitamente un executor, il client utilizza un DefaultConnectingIOReactor per gestire le connessioni, che a sua volta crea un numero di worker thread pari a:
	 * 
	 * Runtime.getRuntime().availableProcessors()
	 * 
	 * Il numero di worker thread è uguale al numero di core della CPU disponibile.
	 * Il pool utilizza un comportamento predefinito per la gestione degli I/O asincroni.
	 * 
	 * Ulteriori spiegazioni in org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPCOREConnectionManager
	 */
	
	/**
	 * thread lanciato in org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCORE
	 * Viene utilizzato per inviare in streaming i messaggi SOAP tramite l'utilizzo di un PipedUnblockedStream
	 * Questa funzionalità per default non è attiva.
	 * Si attiva disabilitando l'utilizzo del MessageObjectEntity per spedire in streaming tramite la proprietà 'org.openspcoop2.pdd.connettori.syncClient.useCustomMessageObjectEntity'
	 */
	private static ExecutorService syncRequestPool;
	
	/**
	 * thread lanciato in org.openspcoop2.pdd.services.connector.AbstractRicezioneConnectorAsync
	 * Viene utilizzato per sganciare subito il worker thread del connettore NIO del webserver, in modo che tutta la logica di gestione della richiesta avvenga su un thread applicativo,
	 * mentre il worker thread del connettore NIO può esssere utilizzato per accettare una nuova richiesta e/o consegnare una risposta pronta 
	 * Questa funzionalità per default è attiva.
	 * Si gestisce tramite la proprietà 'org.openspcoop2.pdd.connettori.asyncRequest.stream'
	 */
	private static ExecutorService asyncRequestPool;
	
	/**
	 * thread lanciato in org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPCOREInputStreamEntityConsumer
	 * Viene utilizzato per sganciare subito il worker thread del connettore NIO della libreria client, in modo che tutta la logica di gestione della risposta avvenga su un thread applicativo,
	 * mentre il worker thread del connettore NIO può esssere utilizzato per gestire l'invio di una nuova richiesta e/o ricevere una risposta 
	 * Questa funzionalità per default è attiva.
	 * Si gestisce tramite la proprietà 'org.openspcoop2.pdd.connettori.asyncResponse.stream'
	 */
	private static ExecutorService asyncResponsePool;
	
	public static void initialize(OpenSPCoop2Properties op2) {
		if(!op2.isBIOConfigSyncClientUseCustomMessageObjectEntity()) {
			int size = op2.getBIOConfigSyncClientApplicativeThreadPoolSize();
			if(size>0) {
				syncRequestPool = Executors.newFixedThreadPool(size, new ConnectorApplicativeThreadFactory("request-bio-nonblocking-io"));
			}
		}
		
		if(op2.isNIOEnabled()) {
			if(op2.isNIOConfigAsyncRequestStreamEnabled()) {
				int size = op2.getNIOConfigAsyncRequestApplicativeThreadPoolSize();
				if(size>0) {
					asyncRequestPool = Executors.newFixedThreadPool(size, new ConnectorApplicativeThreadFactory("request-nio-worker"));
				}
			}
			if(op2.isNIOConfigAsyncResponseStreamEnabled()) {
				int size = op2.getNIOConfigAsyncResponseApplicativeThreadPoolSize();
				if(size>0) {
					asyncResponsePool = Executors.newFixedThreadPool(size, new ConnectorApplicativeThreadFactory("response-nio-worker"));
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

class ConnectorApplicativeThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public ConnectorApplicativeThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(this.namePrefix +"-" + this.threadNumber.getAndIncrement());
        return thread;
    }
}
