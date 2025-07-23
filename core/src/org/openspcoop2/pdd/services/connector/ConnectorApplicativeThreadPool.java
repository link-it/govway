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

package org.openspcoop2.pdd.services.connector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.utils.threads.MonitoredVirtualThreadExecutor;

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
	 * Ulteriori spiegazioni in org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPCOREConnectionManager e nella documentazione BIO/NIO
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
	// erogazioni
	private static String inRequestThreadPoolId = null;
	public String getInRequestThreadPoolId() {
		return inRequestThreadPoolId;
	}
	// fruizioni
	private static String outRequestThreadPoolId = null;
	public String getOutRequestThreadPoolId() {
		return outRequestThreadPoolId;
	}	
		
	/**
	 * thread lanciato in org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPCOREInputStreamEntityConsumer
	 * Viene utilizzato per sganciare subito il worker thread del connettore NIO della libreria client, in modo che tutta la logica di gestione della risposta avvenga su un thread applicativo,
	 * mentre il worker thread del connettore NIO può esssere utilizzato per gestire l'invio di una nuova richiesta e/o ricevere una risposta 
	 * Questa funzionalità per default è attiva.
	 * Si gestisce tramite la proprietà 'org.openspcoop2.pdd.connettori.asyncResponse.stream'
	 */
	// erogazioni
	private static String outResponseThreadPoolId = null;
	public String getOutResponseThreadPoolId() {
		return outResponseThreadPoolId;
	}
	// fruizioni
	private static String inResponseThreadPoolId = null;
	public String getInResponseThreadPoolId() {
		return inResponseThreadPoolId;
	}
	
	/** Pool Thread Asincroni */
	private static Map<String, ExecutorService> asyncThreadPool = null;
	
	
	public static void initialize(OpenSPCoop2Properties op2) {
		if(!op2.isBIOConfigSyncClientUseCustomMessageObjectEntity()) {
			int size = op2.getBIOConfigSyncClientApplicativeThreadPoolSize();
			if(size>0) {
				syncRequestPool = Executors.newFixedThreadPool(size, new ConnectorApplicativeThreadFactory("request-bio-nonblocking-io"));
			}
		}
		
		if(op2.isNIOEnabled() &&
			(op2.isNIOConfigAsyncRequestStreamEnabled() || op2.isNIOConfigAsyncResponseStreamEnabled()) 
			){
			ConnectorAsyncThreadPoolConfig poolConfig = op2.getNIOConfigAsyncThreadPoolConfig();
			inRequestThreadPoolId = poolConfig.getInRequestThreadPoolId();
			outRequestThreadPoolId = poolConfig.getOutRequestThreadPoolId();
			inResponseThreadPoolId = poolConfig.getInResponseThreadPoolId();
			outResponseThreadPoolId = poolConfig.getOutResponseThreadPoolId();
			asyncThreadPool = new HashMap<>();
			for (Map.Entry<String,Boolean> entry : poolConfig.getPoolVirtualThreadType().entrySet()) {
				String poolName = entry.getKey();
				boolean virtualThreads = entry.getValue();
				if(virtualThreads) {
					asyncThreadPool.put(poolName, new MonitoredVirtualThreadExecutor(poolName + "-worker-")); // non limita la concorrenza, perché i virtual threads sono leggeri (puoi averne migliaia senza problemi)
				}
				else {
					int size = poolConfig.getPoolSize().get(poolName);
					asyncThreadPool.put(poolName, Executors.newFixedThreadPool(size, new ConnectorApplicativeThreadFactory(poolName+"-worker"))); // limita la concorrenza a size thread fisici
				}
			}
		}
	}
	
	
	public static void executeBySyncRequestPool(Runnable runnable) {
		syncRequestPool.execute(runnable);
	}
	
	public static void executeByAsyncInRequestPool(Runnable runnable) {
		asyncThreadPool.get(inRequestThreadPoolId).execute(runnable);
	}
	public static void executeByAsyncOutRequestPool(Runnable runnable) {
		asyncThreadPool.get(outRequestThreadPoolId).execute(runnable);
	}
	public static void executeByAsyncInResponsePool(Runnable runnable) {
		asyncThreadPool.get(inResponseThreadPoolId).execute(runnable);
	}
	public static void executeByAsyncOutResponsePool(Runnable runnable) {
		asyncThreadPool.get(outResponseThreadPoolId).execute(runnable);
	}
	
	public static ExecutorService getSyncRequestPool() {
		return syncRequestPool;
	}

	public static ExecutorService getAsyncInRequestPool() {
		return asyncThreadPool.get(inRequestThreadPoolId);
	}
	public static ExecutorService getAsyncOutRequestPool() {
		return asyncThreadPool.get(outRequestThreadPoolId);
	}
	public static ExecutorService getAsyncInResponsePool() {
		return asyncThreadPool.get(inResponseThreadPoolId);
	}
	public static ExecutorService getAsyncOutResponsePool() {
		return asyncThreadPool.get(outResponseThreadPoolId);
	}
	
	public static void shutdown() {
		if(syncRequestPool!=null) {
			syncRequestPool.shutdown();
		}
		
		for (Map.Entry<String,ExecutorService> entry : asyncThreadPool.entrySet()) {
			if(entry.getValue()!=null) {
				entry.getValue().shutdown();
			}
		}
	}
	
	public static String getSyncRequestPoolThreadsImage() {
		if(syncRequestPool instanceof ThreadPoolExecutor tpe) {
			return getThreadsImage(tpe);
		}
		return null;
	}
	public static boolean isSyncRequestPoolThreadsEnabled() {
		return syncRequestPool!=null;
	}
	
	public static String getAsyncPoolThreadsImage() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String,ExecutorService> entry : asyncThreadPool.entrySet()) {
			if(entry.getKey()!=null) {
				fillAsyncPoolThreadsImage(entry, sb);
			}
		}
		if(sb.length()>0) {
			return sb.toString();
		}
		return null;
	}
	private static void fillAsyncPoolThreadsImage(Map.Entry<String,ExecutorService> entry, StringBuilder sb) {
		if(entry.getValue() instanceof ThreadPoolExecutor tpe) {
			if(sb.length()>0) {
				sb.append("\n\n");
			}
			sb.append("[").append(entry.getKey()).append("] ").
				append(getThreadsImage(tpe)).append(", virtualThreads:false");
		}
		else if(entry.getValue() instanceof MonitoredVirtualThreadExecutor tpe) {
			if(sb.length()>0) {
				sb.append("\n\n");
			}
			sb.append("[").append(entry.getKey()).append("] ").
				append(tpe.getStatus()).append(", virtualThreads:true");
		}
	}
	public static boolean isAsyncPoolThreadsEnabled() {
		return asyncThreadPool!=null;
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
