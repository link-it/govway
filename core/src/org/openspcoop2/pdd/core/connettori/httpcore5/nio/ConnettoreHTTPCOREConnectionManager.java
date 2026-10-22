/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.connettori.httpcore5.nio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.HostnameVerificationPolicy;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.pool.PoolStats;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.connettori.AbstractConnettoreConnectionConfig;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreHttpPoolParams;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;
import org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCOREPoolRegistry;
import org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCOREUtils;
import org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHttpRequestInterceptor;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.keystore.SSLContentFingerprint;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.http.SSLConfig;
import org.openspcoop2.utils.transport.http.SSLUtilities;

/**
 * ConnettoreHTTPCOREConnectionManager
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCOREConnectionManager {

	private ConnettoreHTTPCOREConnectionManager() {}

	// ******** STATIC **********
	
	private static final org.openspcoop2.utils.Semaphore semaphorePoolingConnectionManager = new org.openspcoop2.utils.Semaphore(ConnettoreHTTPCORE.ID_HTTPCORE+"-PoolingConnectionManager"); // usato per instanziare un pool
	
	public static final boolean USE_POOL_CONNECTION = true; // e' inutilizzabile senza
	private static final org.openspcoop2.utils.Semaphore semaphoreConnection = new org.openspcoop2.utils.Semaphore(ConnettoreHTTPCORE.ID_HTTPCORE+"-ConnectionManager"); // usato per negoziare una connessione da un pool
	
	static Map<String, PoolingAsyncClientConnectionManager> mapPoolingConnectionManager = new ConcurrentHashMap<>();
	// concorrente: i client associati a un pool dismesso vengono rimossi alla chiusura del pool, senza acquisire 'semaphoreConnection'
	static Map<String, ConnettoreHTTPCOREConnection> mapConnection = new ConcurrentHashMap<>();
	static final ConnettoreHTTPCOREPoolRegistry poolRegistry = new ConnettoreHTTPCOREPoolRegistry(); // distinto da quello dei pool BIO
	
	private static ConnettoreHTTPCOREConnectionEvictor idleConnectionEvictor;
		
	public static synchronized void initialize() throws ConnettoreException {
		try {
			if(ConnettoreHTTPCOREConnectionManager.idleConnectionEvictor==null) {
				OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
				Integer closeIdleConnectionsAfterSeconds = op2Properties.getNIOConfigAsyncClientCloseIdleConnectionsAfterSeconds();
				boolean idleConnectionEvictorEnabled = (closeIdleConnectionsAfterSeconds!=null && closeIdleConnectionsAfterSeconds>0);
				if(idleConnectionEvictorEnabled) {
					int sleepTimeSeconds = op2Properties.getNIOConfigAsyncClientCloseIdleConnectionsCheckIntervalSeconds();
					boolean debug = op2Properties.isNIOConfigAsyncClientCloseIdleConnectionsDebug();
					ConnettoreHTTPCOREConnectionManager.idleConnectionEvictor = 
							new ConnettoreHTTPCOREConnectionEvictor(debug, sleepTimeSeconds, closeIdleConnectionsAfterSeconds);
					idleConnectionEvictor.start();
				}
			}
		}catch(Exception t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
	}
	
	public static String getHttpClientConnectionManagerStatus() {
		if(idleConnectionEvictor!=null) {
			StringBuilder sb = new StringBuilder();
			idleConnectionEvictor.internalCheck(sb);
			return sb.toString();
		}
		return null;
	}

	/**
	 * Restituisce le statistiche di tutti i pool di connessioni attivi (per l'esposizione come metriche).
	 * Lettura best-effort: in caso di errore restituisce quanto raccolto.
	 */
	public static List<PoolStats> getPoolsStats() {
		List<PoolStats> stats = new ArrayList<>();
		try {
			SemaphoreLock lock = semaphorePoolingConnectionManager.acquire("getPoolsStats");
			try {
				for (PoolingAsyncClientConnectionManager cm : mapPoolingConnectionManager.values()) {
					if(cm!=null) {
						stats.add(cm.getTotalStats());
					}
				}
			}finally {
				semaphorePoolingConnectionManager.release(lock, "getPoolsStats");
			}
		}catch(Exception e) {
			// best-effort
		}
		return stats;
	}
	public static String getNIOHttpClientConnectionManagerIOThreadCount() {
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		Integer numeroThread = op2Properties.getNIOConfigAsyncHttpclientIoReactorThread();
		if(numeroThread!=null && numeroThread.intValue()>0) {
			return numeroThread.intValue()+"";
		}
		else {
			return "[default] availableProcessors:"+Runtime.getRuntime().availableProcessors();
		}
	}
	
	private static void initialize(String key, TlsStrategy tlsStrategy, 
			ConnettoreHTTPCOREConnectionConfig connectionConfig, ConnettoreLogger logger) throws ConnettoreException {
		
		String idTransazione = logger!=null ? logger.getIdTransazione() : null;
		SemaphoreLock lock = null;
		try {
			lock = semaphorePoolingConnectionManager.acquire("initPoolingConnectionManager",idTransazione);
		}catch(Exception t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
		try {
			if(!ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.containsKey(key)){
			
				try {			
					PoolingAsyncClientConnectionManager poolingConnectionManager = buildPoolingConnectionManager(tlsStrategy, connectionConfig);
													
					ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.put(key, poolingConnectionManager);
					poolRegistry.registraCreazione(key, connectionConfig.toKeyConnectionManagerWithoutContentFingerprint());
					
				}catch(Exception t) {
					throw new ConnettoreException(t.getMessage(),t);
				}
				
				// un nuovo pool può renderne dismesso uno precedente: verifica anche in assenza del timer di controllo delle connessioni
				closePoolDismessiEngine(logger);
			}
		}finally {
			semaphorePoolingConnectionManager.release(lock, "initPoolingConnectionManager",idTransazione);
		}
	}
	
	/**
	 * Chiude i pool dismessi (sostituiti da un pool creato per un contenuto aggiornato dei keystore)
	 * che non hanno connessioni in uso e non vengono utilizzati da almeno la soglia di inattività,
	 * insieme ai client che li utilizzano.
	 */
	public static void closePoolDismessi(ConnettoreLogger logger) {
		String idTransazione = logger!=null ? logger.getIdTransazione() : null;
		SemaphoreLock lock = null;
		try {
			lock = semaphorePoolingConnectionManager.acquire("closePoolDismessi",idTransazione);
		}catch(Exception t) {
			OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori().error("closePoolDismessi: "+t.getMessage(),t);
			return;
		}
		try {
			closePoolDismessiEngine(logger);
		}finally {
			semaphorePoolingConnectionManager.release(lock, "closePoolDismessi",idTransazione);
		}
	}
	private static void closePoolDismessiEngine(ConnettoreLogger logger) {
		Integer closeIdleConnectionsAfterSeconds = OpenSPCoop2Properties.getInstance().getNIOConfigAsyncClientCloseIdleConnectionsAfterSeconds();
		int inattivita = (closeIdleConnectionsAfterSeconds!=null && closeIdleConnectionsAfterSeconds>0) ? 
				closeIdleConnectionsAfterSeconds : ConnettoreHTTPCOREPoolRegistry.INATTIVITA_POOL_DISMESSO_DEFAULT_SECONDS;
		for (String key : poolRegistry.getPoolDismessiDaChiudere(mapPoolingConnectionManager, inattivita)) {
			PoolingAsyncClientConnectionManager cm = mapPoolingConnectionManager.remove(key);
			if(!poolRegistry.isChiudibile(key, cm, inattivita)) {
				// utilizzato nel frattempo da una richiesta
				if(cm!=null) {
					mapPoolingConnectionManager.put(key, cm);
				}
				continue;
			}
			poolRegistry.rimuovi(key);
			List<Throwable> listT = new ArrayList<>();
			
			// prima i client che utilizzano il pool, poi il pool
			closeClients(key, listT);
			stopConnectionManager(cm, listT);
			
			logPoolDismessoChiuso(key, listT, logger);
		}
	}
	private static void closeClients(String keyPool, List<Throwable> listT) {
		Iterator<Map.Entry<String, ConnettoreHTTPCOREConnection>> it = mapConnection.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ConnettoreHTTPCOREConnection> entry = it.next();
			if(keyPool.equals(entry.getValue().getKeyPool())) {
				it.remove();
				try {
					entry.getValue().close();
				}catch(Exception t) {
					listT.add(t);
				}
			}
		}
	}
	private static void logPoolDismessoChiuso(String keyPool, List<Throwable> listT, ConnettoreLogger logger) {
		String msg = "ConnectionManager dismesso chiuso ("+keyPool+")";
		if(!listT.isEmpty()) {
			msg = msg + ": "+listT.get(0).getMessage();
		}
		if(logger!=null) {
			logger.debug(msg);
		}
		else {
			OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori().debug(msg);
		}
	}
	
	static boolean isPoolDismesso(String key) {
		return poolRegistry.isDismesso(key);
	}
	private static PoolingAsyncClientConnectionManager buildPoolingConnectionManager(TlsStrategy tlsStrategy, 
			ConnettoreHTTPCOREConnectionConfig connectionConfig) {
		PoolingAsyncClientConnectionManagerBuilder poolingConnectionManagerBuilder = PoolingAsyncClientConnectionManagerBuilder.create();
		
		if(tlsStrategy!=null) {
			poolingConnectionManagerBuilder.setTlsStrategy(tlsStrategy);
		}
		else {
			poolingConnectionManagerBuilder.useSystemProperties();
		}
							
		// LAX: Higher concurrency but with lax connection max limit guarantees.
		// STRICT: Strict connection max limit guarantees.
		poolingConnectionManagerBuilder.setPoolConcurrencyPolicy(PoolConcurrencyPolicy.LAX);
	
		// FIFO: Re-use all connections equally preventing them from becoming idle and expiring.
		// LIFO: Re-use as few connections as possible making it possible for connections to become idle and expire.
		poolingConnectionManagerBuilder.setConnPoolPolicy(PoolReusePolicy.FIFO);
		
		
		// PoolingNHttpClientConnectionManager maintains a maximum limit of connection on a per route basis and in total. 
		// Per default this implementation will create no more than than 2 concurrent connections per given route and no more 20 connections in total.
		// For many real-world applications these limits may prove too constraining, especially if they use HTTP as a transport protocol for their services. 
		// Connection limits, however, can be adjusted using ConnPoolControl methods.
		// Increase max total connection
		ConnettoreHttpPoolParams poolParams = connectionConfig.getHttpPoolParams();
		Integer defaultMaxPerRoute = poolParams.getDefaultMaxPerRoute();
		Integer maxTotal = poolParams.getMaxTotal();
		if(maxTotal!=null && maxTotal>0) {
			poolingConnectionManagerBuilder.setMaxConnTotal(poolParams.getMaxTotal());
		}
		// Increase default max connection per route
		if(defaultMaxPerRoute!=null && defaultMaxPerRoute>0) {
			poolingConnectionManagerBuilder.setMaxConnPerRoute(poolParams.getDefaultMaxPerRoute());
		}
		// Increase max connections for localhost:80
		/**HttpHost localhost = new HttpHost("locahost", 80);
		poolingConnectionManagerBuilder.setMaxPerRoute(new HttpRoute(localhost), 50);*/
		
		ConnectionConfig config = buildConnectionConfig(poolParams, connectionConfig.getConnectionTimeout());
		poolingConnectionManagerBuilder.setDefaultConnectionConfig(config);

		// HTTP version policy (ALPN): determina se tentare la negoziazione HTTP/2 sulla TLS del pool
		ConnettoreHTTPCOREVersionPolicy versionPolicy = connectionConfig.getHttpVersionPolicy();
		if(versionPolicy!=null) {
			poolingConnectionManagerBuilder.setDefaultTlsConfig(TlsConfig.custom()
					.setVersionPolicy(versionPolicy.getHttpVersionPolicy())
					.build());
		}
		
		return poolingConnectionManagerBuilder.build();
	}
	
	private static ConnectionConfig buildConnectionConfig(ConnettoreHttpPoolParams poolParams, long connectionTimeout) {
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		Integer closeIdleConnectionsAfterSeconds = op2Properties.getNIOConfigAsyncClientCloseIdleConnectionsAfterSeconds();
		boolean idleConnectionEvictorEnabled = (closeIdleConnectionsAfterSeconds!=null && closeIdleConnectionsAfterSeconds>0);
		int sleepTimeSeconds = -1;
		if(idleConnectionEvictorEnabled) {
			sleepTimeSeconds = op2Properties.getNIOConfigAsyncClientCloseIdleConnectionsCheckIntervalSeconds();
		}
		return ConnettoreHTTPCOREUtils.buildConnectionConfig(poolParams, connectionTimeout, idleConnectionEvictorEnabled, sleepTimeSeconds);
	}
	
	
	public static synchronized void stop() throws ConnettoreException {
			
		List<Throwable> listT = new ArrayList<>();
		
		// Shut down the evictor thread
		if(ConnettoreHTTPCOREConnectionManager.idleConnectionEvictor!=null) {
			try {			
				ConnettoreHTTPCOREConnectionManager.idleConnectionEvictor.setStop(true);
				idleConnectionEvictor.waitShutdown();
			}catch(Exception t) {
				listT.add(t);
			}	
		}
		
		// close client
		stopClients(listT);
		
		// Shut down connManager
		stopConnectionManager(listT);
		
		// gestione eccezione
		throwExceptions(listT);
		
	}
	public static synchronized void restartConnectionManager() throws ConnettoreException {
		
		List<Throwable> listT = new ArrayList<>();
		
		// close client
		stopClients(listT);
		
		// Shut down connManager
		stopConnectionManager(listT);
		
		// gestione eccezione
		throwExceptions(listT);
		
	}
	private static void throwExceptions(List<Throwable> listT) throws ConnettoreException {
		if(listT!=null && !listT.isEmpty()) {
			if(listT.size()==1) {
				throw new ConnettoreException(listT.get(0).getMessage(),listT.get(0));
			}
			else {
				UtilsMultiException multiExc = new UtilsMultiException(listT.toArray(new Throwable[1]));
				throw new ConnettoreException(multiExc.getMessage(),multiExc);
			}
		}
	}
	private static void stopClients(List<Throwable> listT) throws ConnettoreException {
		if(mapConnection!=null && !mapConnection.isEmpty()) {
			Iterator<String> it = mapConnection.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				ConnettoreHTTPCOREConnection connection = mapConnection.get(key);
				try {
					connection.close(); // GRACEFUL tenta di chiudere le connessioni in modo ordinato, permettendo alle richieste in corso di completarsi.
				}catch(Exception t) {
					listT.add(new ConnettoreException("NIO Connection ["+key+"] close error: "+t.getMessage(),t));
				}
			}
			mapConnection.clear();
		}
	}
	private static void stopConnectionManager(List<Throwable> listT) {
		if(ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager!=null && !ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.isEmpty()) {
			for (String key : ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.keySet()) {
				if(key!=null) {
					PoolingAsyncClientConnectionManager cm = ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.get(key);
					stopConnectionManager(cm, listT);
				}
			}
			ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.clear();
		}
		poolRegistry.clear();
	}
	private static void stopConnectionManager(PoolingAsyncClientConnectionManager cm, List<Throwable> listT) {
		if(cm!=null) {
			try {			
				cm.close(CloseMode.GRACEFUL); // tenta di chiudere le connessioni in modo ordinato, permettendo alle richieste in corso di completarsi.
			}catch(Exception t) {
				listT.add(t);
			}
		}
	}
	
	

	private static void init(ConnettoreHTTPCOREConnectionConfig connectionConfig,
			Loader loader, ConnettoreLogger logger, 
			RequestInfo requestInfo,
			ConnectionKeepAliveStrategy keepAliveStrategy,
			ConnettoreHttpRequestInterceptor httpRequestInterceptor) throws ConnettoreException {
		String key = connectionConfig.toKeyConnection();
		String idTransazione = logger!=null ? logger.getIdTransazione() : null;
		SemaphoreLock lock = null;
		try {
			lock = semaphoreConnection.acquire("initConnection",idTransazione);
		}catch(Exception t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
		try {
			if(!mapConnection.containsKey(key)) {
				ConnettoreHTTPCOREConnection resource = buildAsyncClient(connectionConfig, 
						loader, logger, key, 
						requestInfo,
						keepAliveStrategy,
						httpRequestInterceptor);
				mapConnection.put(key, resource);
			}
		}finally {
			semaphoreConnection.release(lock, "initConnection",idTransazione);
		}
	}
	private static ConnettoreHTTPCOREConnection update(ConnettoreHTTPCOREConnectionConfig connectionConfig,
			Loader loader, ConnettoreLogger logger, 
			RequestInfo requestInfo,
			ConnectionKeepAliveStrategy keepAliveStrategy,
			ConnettoreHttpRequestInterceptor httpRequestInterceptor) throws ConnettoreException {
		String key = connectionConfig.toKeyConnection();
		String idTransazione = logger!=null ? logger.getIdTransazione() : null;
		SemaphoreLock lock = null;
		try {
			lock= semaphoreConnection.acquire("updateConnection",idTransazione);
		}catch(Exception t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
		try {
			if(mapConnection.containsKey(key)) {
				ConnettoreHTTPCOREConnection con = mapConnection.remove(key);
				mapConnection.put("expired_"+key+"_"+UUID.randomUUID().toString(), con);
			}
			ConnettoreHTTPCOREConnection resource = buildAsyncClient(connectionConfig, 
					loader, logger, key, 
					requestInfo,
					keepAliveStrategy,
					httpRequestInterceptor);
			mapConnection.put(key, resource);
			return resource;
		}finally {
			semaphoreConnection.release(lock, "updateConnection",idTransazione);
		}
	}

	private static ConnettoreHTTPCOREConnection buildAsyncClient(ConnettoreHTTPCOREConnectionConfig connectionConfig,
			Loader loader, ConnettoreLogger logger, String key,
			RequestInfo requestInfo,
			ConnectionKeepAliveStrategy keepAliveStrategy,
			ConnettoreHttpRequestInterceptor httpRequestInterceptor) throws ConnettoreException {
		try {				
			RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
			
			OpenSPCoop2Properties op2 = OpenSPCoop2Properties.getInstance();
						
			// ** Timeout **
			ConnettoreHTTPCOREUtils.setTimeout(requestConfigBuilder, connectionConfig);
						
			// ** Redirect **
			ConnettoreHTTPCOREUtils.setRedirect(requestConfigBuilder, connectionConfig);
			
			RequestConfig requestConfig = requestConfigBuilder.build();
			
			// Pool
			String keyPool = connectionConfig.toKeyConnectionManager();
			
			// l'utilizzo viene registrato prima del recupero: un pool dismesso utilizzato non viene chiuso
			poolRegistry.registraUtilizzo(keyPool);
			PoolingAsyncClientConnectionManager cm = ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.get(keyPool);
			if(cm==null){
				TlsStrategy tlsStrategy = buildClientTlsStrategyBuilder(connectionConfig, loader, 
						requestInfo, logger);
				ConnettoreHTTPCOREConnectionManager.initialize(keyPool, tlsStrategy, connectionConfig, logger);
				cm = ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.get(keyPool);
			}
			
			HttpAsyncClientBuilder httpClientBuilder =
					HttpAsyncClients.custom(); // Qua si gestisce il pipe (ci sono i metodi che gestiscono le richieste una dopo l'altra o prima tutte le richieste e poi le risposte ...)
			httpClientBuilder.setConnectionManager( cm );
			httpClientBuilder.setConnectionManagerShared(true); // senno' la close di una connessione fa si che venga chiuso il reactor
			httpClientBuilder.setDefaultRequestConfig(requestConfig);
			// Limiti sugli header della risposta ricevuta dal backend, applicati in base al protocollo utilizzato dalla connessione
			// (dettagli in ConnettoreHTTPCOREUtils.buildHttp1Config e ConnettoreHTTPCOREUtils.buildH2Config)
			httpClientBuilder.setHttp1Config(ConnettoreHTTPCOREUtils.buildHttp1Config(
					op2.getNIOConfigAsyncClientMaxHeaderLineLength(),
					op2.getNIOConfigAsyncClientMaxHeaderCount()));
			H2Config h2Config = ConnettoreHTTPCOREUtils.buildH2Config(op2.getNIOConfigAsyncClientMaxHeaderListSizeHttp2());
			if(h2Config!=null) {
				httpClientBuilder.setH2Config(h2Config);
			}
			httpClientBuilder.disableAuthCaching();
			/**
			 * GovWay agisce da gateway: deve essere trasparente verso le risposte del backend.
			 * Header come 'Retry-After' e status code 429/503 vanno veicolati al client a monte
			 * senza essere interpretati/consumati dal client HTTP. La decisione di ritentare
			 * spetta al chiamante originale, non al gateway.
			 * Nota: in HttpClient 5.6 la verifica responseTimeout vs retry-interval e' stata
			 * spostata dentro DefaultHttpRequestRetryStrategy.retryRequest e confronta solo
			 * 'defaultRetryInterval' (1s) anziche' il valore reale ricavato dall'header
			 * 'Retry-After', causando pause di durata pari al valore dell'header anche quando
			 * supera il responseTimeout (regressione rispetto a 5.5).
			 */
			httpClientBuilder.disableAutomaticRetries();
			/**
			 * Ulteriori automatismi del client HTTP disabilitati per garantire la trasparenza
			 * del gateway:
			 * - cookie management: i cookie vanno propagati come header opachi tra client a
			 *   monte e backend; il CookieStore interno del client li intercetterebbe creando
			 *   leak tra request distinte;
			 * - connection state: senza disable il pool segrega le connessioni per principal
			 *   auth (NTLM/Kerberos) frammentando il riuso; il gateway non usa auth automatica
			 *   HttpClient quindi puo' riusare per host;
			 * - content compression: il body deve transitare cosi' come ricevuto dal backend
			 *   con il proprio Content-Encoding; la decompressione automatica altererebbe il
			 *   payload veicolato. Per mantenere uniformita' fra connettore JDK e connettori
			 *   HC5 sync/NIO, la decompressione opt-in (property
			 *   'connettori.contentEncoding.response.decompress' / 'connettori.contentEncoding.decompress')
			 *   e' centralizzata in
			 *   ConnettoreBaseHTTP#decodeResponseBodyContentEncoding (invocato prima del dump
			 *   cosi' che il payload registrato sia gia' in chiaro), evitando di delegarla ad
			 *   Apache che non ha visibilita' del flag GovWay e degli header stale da rimuovere.
			 * Nota: HttpAsyncClientBuilder non espone disableDefaultUserAgent(); l'interceptor
			 * RequestUserAgent comunque non sovrascrive lo User-Agent gia' presente in request,
			 * quindi lo UA del client a monte viene preservato.
			 */
			httpClientBuilder.disableCookieManagement();
			httpClientBuilder.disableConnectionState();
			httpClientBuilder.disableContentCompression();

			ConnectionReuseStrategy defaultClientConnectionReuseStrategy = new DefaultConnectionReuseStrategy();
			httpClientBuilder.setConnectionReuseStrategy(defaultClientConnectionReuseStrategy);
			
			if(keepAliveStrategy!=null){
				httpClientBuilder.setKeepAliveStrategy(keepAliveStrategy);
			}
			
			/**System.out.println("PRESA LA CONNESSIONE AVAILABLE["+cm.getTotalStats().getAvailable()+"] LEASED["
					+cm.getTotalStats().getLeased()+"] MAX["+cm.getTotalStats().getMax()+"] PENDING["+cm.getTotalStats().getPending()+"]");
			System.out.println("-----GET CONNECTION [END] ----");*/
			
			if(httpRequestInterceptor!=null) {
				httpClientBuilder.addRequestInterceptorLast(httpRequestInterceptor);
			}
			
			if(connectionConfig.isFollowRedirect()) {
				httpClientBuilder.setRedirectStrategy(DefaultRedirectStrategy.INSTANCE);
			} else {
				/*
				 * Default HttpClient 5.x: i redirect sono seguiti automaticamente. Senza
				 * disable, configurare followRedirect=false a livello connettore non avrebbe
				 * effetto e i 3xx verrebbero risolti internamente anziche' veicolati al
				 * client a monte.
				 */
				httpClientBuilder.disableRedirectHandling();
			}

			// ** Proxy **
			setProxy(httpClientBuilder, connectionConfig);
			
			/*
			 * ** Reactor **
			 * Interazione tra PoolingAsyncClientConnectionManager e Thread.
			 * Il PoolingAsyncClientConnectionManager è progettato per gestire un pool di connessioni asincrone.
			 * Ogni connessione è associata a un worker thread solo durante l'esecuzione di operazioni asincrone,
			 * ma il numero di thread disponibili per gestire queste connessioni dipende dall'IOReactorConfig o dal ExecutorService configurato.
			 *
			 * Thread I/O (IOReactor): determinano quanti worker thread possono gestire operazioni asincrone contemporaneamente.
			 * Pool di connessioni (PoolingAsyncClientConnectionManager): limita il numero massimo di connessioni totali o per host, ma non controlla direttamente il numero di thread.
			 *
			 * Di default il numero di worker thread è uguale al numero di core della CPU disponibile, ricavabile dalla chiamata Runtime.getRuntime().availableProcessors().
			 * Il numero di thread da utilizzare può essere indicato con la proprietà 'org.openspcoop2.pdd.connettori.asyncRequest.httpclient.ioreactor.threadCount'.
			 */
			Integer numeroThread = op2.getNIOConfigAsyncHttpclientIoReactorThread();
			if(numeroThread!=null && numeroThread.intValue()>0) {
				org.apache.hc.core5.reactor.IOReactorConfig ioReactorConfig = org.apache.hc.core5.reactor.IOReactorConfig.custom()
				        .setIoThreadCount(numeroThread)
				        .build();
				httpClientBuilder.setIOReactorConfig(ioReactorConfig);
			}
			/*
			 * Configurazione di un Executor personalizzato (opzionale): rimane da capire come agganciare al client
			 * un DefaultConnectingIOReactor (es. ConnettoreHTTPCORECustomConnectingIOReactor) costruito sull'IOReactorConfig.
			 */
			
			// Callback exception
			Callback<Exception> callback = e -> {
			    OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori().error("[IoReactorExceptionCallback] " + e.getMessage(), e);
			    OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("[IoReactorExceptionCallback] " + e.getMessage(), e);
			};
			httpClientBuilder.setIoReactorExceptionCallback(callback);
			
			// build e start client
			CloseableHttpAsyncClient httpclient = httpClientBuilder.build();
			httpclient.start();
			
			int expireUnusedAfterSeconds = op2.getNIOConfigAsyncClientExpireUnusedAfterSeconds(); 
			int closeUnusedAfterSeconds = op2.getNIOConfigAsyncClientCloseUnusedAfterSeconds(); 
			
			ConnettoreHTTPCOREConnection connection = new ConnettoreHTTPCOREConnection(key, httpclient, requestConfig,
					expireUnusedAfterSeconds, closeUnusedAfterSeconds);
			connection.setKeyPool(keyPool);
			return connection;
			
		} catch ( Exception t ) {
			throw new ConnettoreException( t.getMessage(),t );
		}
	}
	private static void setProxy(HttpAsyncClientBuilder httpClientBuilder, AbstractConnettoreConnectionConfig connectionConfig) {
		if(connectionConfig.getProxyHost()!=null && connectionConfig.getProxyPort()!=null) {
			HttpHost proxy = new HttpHost(connectionConfig.getProxyHost(), connectionConfig.getProxyPort());
			httpClientBuilder.setProxy(proxy);
		}
		else {
			// Quando non c'è un proxy configurato esplicitamente, usa useSystemProperties() per leggere le proprietà JAVA_OPTS
			// (http.proxyHost, http.proxyPort, https.proxyHost, https.proxyPort, ecc.)
			httpClientBuilder.useSystemProperties();
		}
	}
	private static TlsStrategy buildClientTlsStrategyBuilder(ConnettoreHTTPCOREConnectionConfig connectionConfig, Loader loader,
			RequestInfo requestInfo,
			ConnettoreLogger logger) throws UtilsException {
		// Se non ci sono proprietà SSL configurate, restituisce null per permettere l'utilizzo di useSystemProperties()
		// Stesso comportamento della versione BIO (ConnettoreHTTPCOREConnectionManager.buildSSLConnectionSocketFactory)
		TlsStrategy tlsStrategy = null;
		if(connectionConfig.getSslContextProperties()!=null) {
			SSLContext sslContext = null;
			HostnameVerifier hostnameVerifier = null;
			StringBuilder bfLog = new StringBuilder();
			sslContext = buildSSLContext(connectionConfig.getSslContextProperties(), requestInfo, logger, bfLog);
			hostnameVerifier = SSLUtilities.generateHostnameVerifier(connectionConfig.getSslContextProperties(), bfLog, logger.getLogger(), loader);
			if(connectionConfig.isDebug()) {
				logger.debug(bfLog.toString());
			}
			ClientTlsStrategyBuilder tlsBuilder = ClientTlsStrategyBuilder.create();
			tlsBuilder.setSslContext(sslContext);
			tlsBuilder.setHostnameVerifier(hostnameVerifier);
			/*
			 * HttpClient 5.6: HostnameVerificationPolicy controlla dove avviene la verifica hostname.
			 * Con BOTH (default) il JDK imposta endpointIdentificationAlgorithm="HTTPS" sull'SSLEngine,
			 * e la verifica fallisce PRIMA che il HostnameVerifier custom venga chiamato.
			 * Con CLIENT la verifica avviene solo via HostnameVerifier: usiamo questa policy quando il verifier
			 * non e' quello strict di default, cosi' un verifier permissivo (es. SSLHostNameVerifierDisabled)
			 * ha effetto (altrimenti il JDK blocca l'handshake).
			 */
			HostnameVerificationPolicy hostnameVerificationPolicy = (hostnameVerifier instanceof DefaultHostnameVerifier)
					? HostnameVerificationPolicy.BOTH
					: HostnameVerificationPolicy.CLIENT;
			tlsBuilder.setHostVerificationPolicy(hostnameVerificationPolicy);
			tlsStrategy = tlsBuilder.buildAsync();
		}
		return tlsStrategy;
	}
	private static SSLContext buildSSLContext(SSLConfig httpsProperties, RequestInfo requestInfo, 
			ConnettoreLogger logger, StringBuilder bfLog) throws UtilsException {
		readStores(httpsProperties, requestInfo, logger);
		return SSLUtilities.generateSSLContext(httpsProperties, bfLog);
	}
	private static void readStores(SSLConfig httpsProperties, RequestInfo requestInfo, 
			ConnettoreLogger logger) {
		// provo a leggere i keystore dalla cache
		if(httpsProperties.getKeyStore()==null &&
			httpsProperties.getKeyStoreLocation()!=null) {
			try {
				httpsProperties.setKeyStore(GestoreKeystoreCache.getMerlinKeystore(requestInfo, httpsProperties.getKeyStoreLocation(), 
						httpsProperties.getKeyStoreType(), httpsProperties.getKeyStorePassword()).getKeyStore().getKeystore());
			}catch(Exception e) {
				String msgError = "Lettura keystore '"+httpsProperties.getKeyStoreLocation()+"' dalla cache fallita: "+e.getMessage();
				logger.error(msgError, e);
			}
		}
		if(httpsProperties.getTrustStore()==null &&
			httpsProperties.getTrustStoreLocation()!=null) {
			try {
				httpsProperties.setTrustStore(GestoreKeystoreCache.getMerlinTruststore(requestInfo, httpsProperties.getTrustStoreLocation(), 
						httpsProperties.getTrustStoreType(), httpsProperties.getTrustStorePassword()).getTrustStore().getKeystore());
			}catch(Exception e) {
				String msgError = "Lettura truststore '"+httpsProperties.getTrustStoreLocation()+"' dalla cache fallita: "+e.getMessage();
				logger.error(msgError, e);
			}
		}
		if(httpsProperties.getTrustStoreCRLs()==null &&
			httpsProperties.getTrustStoreCRLsLocation()!=null) {
			try {
				httpsProperties.setTrustStoreCRLs(GestoreKeystoreCache.getCRLCertstore(requestInfo, httpsProperties.getTrustStoreCRLsLocation()).getCertStore());
			}catch(Exception e) {
				String msgError = "Lettura CRLs '"+httpsProperties.getTrustStoreLocation()+"' dalla cache CRL fallita: "+e.getMessage();
				logger.error(msgError, e);
			}
		}
	}
	
	public static ConnettoreHTTPCOREConnection getConnettoreHTTPCOREConnection(ConnettoreHTTPCOREConnectionConfig connectionConfig,
			Loader loader, ConnettoreLogger logger, 
			RequestInfo requestInfo,
			ConnectionKeepAliveStrategy keepAliveStrategy,
			ConnettoreHttpRequestInterceptor httpRequestInterceptor) throws ConnettoreException  {
		
		// Il contesto SSL viene creato una sola volta, alla nascita del pool: 
		// l'impronta del contenuto dei keystore nelle chiavi di client e pool fa nascere un nuovo pool quando il contenuto cambia
		// (i keystore letti dalla cache restano nella configurazione e vengono riusati alla creazione del contesto SSL)
		if(connectionConfig.getSslContextProperties()!=null) {
			readStores(connectionConfig.getSslContextProperties(), requestInfo, logger);
			connectionConfig.setSslContentFingerprint(SSLContentFingerprint.compute(connectionConfig.getSslContextProperties()));
		}
		
		// l'utilizzo viene registrato prima del recupero del client (che riferisce il pool): un pool dismesso utilizzato non viene chiuso
		poolRegistry.registraUtilizzo(connectionConfig.toKeyConnectionManager());
		
		ConnettoreHTTPCOREConnection connection = null;
		if(USE_POOL_CONNECTION) {
			String key = connectionConfig.toKeyConnection();
			connection = mapConnection.get(key); // il client può essere rimosso alla chiusura del pool dismesso che utilizza
			if(connection==null) {
				init(connectionConfig, 
						loader, logger, 
						requestInfo,
						keepAliveStrategy,
						httpRequestInterceptor);
				connection = mapConnection.get(key);
				connection.refresh();
			}
			else {
				connection.refresh();
				if(connection.isExpired()) {
					connection = update(connectionConfig, 
							loader, logger, 
							requestInfo,
							keepAliveStrategy,
							httpRequestInterceptor);
				}
			}
		}
		else {
			connection = buildAsyncClient(connectionConfig, 
					loader, logger, connectionConfig.toKeyConnection(),
					requestInfo,
					keepAliveStrategy,
					httpRequestInterceptor);
			connection.refresh();
		}
		return connection;
	}
	
}
