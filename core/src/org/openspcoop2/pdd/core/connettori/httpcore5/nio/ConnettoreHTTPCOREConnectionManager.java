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

package org.openspcoop2.pdd.core.connettori.httpcore5.nio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.connettori.AbstractConnettoreConnectionConfig;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreHttpPoolParams;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;
import org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCOREUtils;
import org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHttpRequestInterceptor;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
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
	
	static Map<String, PoolingAsyncClientConnectionManager> mapPoolingConnectionManager = new HashMap<>();
	static Map<String, ConnettoreHTTPCOREConnection> mapConnection = new HashMap<>();
	
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
	
	private static void initialize(String key, TlsStrategy tlsStrategy, 
			ConnettoreHTTPCOREConnectionConfig connectionConfig, ConnettoreLogger logger) throws ConnettoreException {
		
		String idTransazione = logger!=null ? logger.getIdTransazione() : null;
		try {
			semaphorePoolingConnectionManager.acquire("initPoolingConnectionManager",idTransazione);
		}catch(Exception t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
		try {
			if(!ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.containsKey(key)){
			
				try {			
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
					
					PoolingAsyncClientConnectionManager poolingConnectionManager = poolingConnectionManagerBuilder.build();
													
					ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.put(key, poolingConnectionManager);
					
				}catch(Exception t) {
					throw new ConnettoreException(t.getMessage(),t);
				}
				
			}
		}finally {
			semaphorePoolingConnectionManager.release("initPoolingConnectionManager",idTransazione);
		}
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
		if(!listT.isEmpty()) {
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
		}
	}
	private static void stopConnectionManager(List<Throwable> listT) {
		if(ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager!=null && ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.isEmpty()) {
			for (String key : ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.keySet()) {
				if(key!=null) {
					PoolingAsyncClientConnectionManager cm = ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.get(key);
					stopConnectionManager(cm, listT);
				}
			}
		}
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
		try {
			semaphoreConnection.acquire("initConnection",idTransazione);
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
			semaphoreConnection.release("initConnection",idTransazione);
		}
	}
	private static ConnettoreHTTPCOREConnection update(ConnettoreHTTPCOREConnectionConfig connectionConfig,
			Loader loader, ConnettoreLogger logger, 
			RequestInfo requestInfo,
			ConnectionKeepAliveStrategy keepAliveStrategy,
			ConnettoreHttpRequestInterceptor httpRequestInterceptor) throws ConnettoreException {
		String key = connectionConfig.toKeyConnection();
		String idTransazione = logger!=null ? logger.getIdTransazione() : null;
		try {
			semaphoreConnection.acquire("updateConnection",idTransazione);
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
			semaphoreConnection.release("updateConnection",idTransazione);
		}
	}

	private static ConnettoreHTTPCOREConnection buildAsyncClient(ConnettoreHTTPCOREConnectionConfig connectionConfig,
			Loader loader, ConnettoreLogger logger, String key,
			RequestInfo requestInfo,
			ConnectionKeepAliveStrategy keepAliveStrategy,
			ConnettoreHttpRequestInterceptor httpRequestInterceptor) throws ConnettoreException {
		try {				
			RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
			
			// ** Timeout **
			ConnettoreHTTPCOREUtils.setTimeout(requestConfigBuilder, connectionConfig);
						
			// ** Redirect **
			ConnettoreHTTPCOREUtils.setRedirect(requestConfigBuilder, connectionConfig);
			
			RequestConfig requestConfig = requestConfigBuilder.build();
			
			// Pool
			String keyPool = connectionConfig.toKeyConnectionManager();
			
			if(!ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.containsKey(keyPool)){
				TlsStrategy tlsStrategy = buildClientTlsStrategyBuilder(connectionConfig, loader, 
						requestInfo, logger);
				ConnettoreHTTPCOREConnectionManager.initialize(keyPool, tlsStrategy, connectionConfig, logger);
			}
			PoolingAsyncClientConnectionManager cm = ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.get(keyPool);		
			
			HttpAsyncClientBuilder httpClientBuilder = 
					HttpAsyncClients.custom(); // Qua si gestisce il pipe (ci sono i metodi che gestiscono le richieste una dopo l'altra o prima tutte le richieste e poi le risposte ...)
			httpClientBuilder.setConnectionManager( cm );
			httpClientBuilder.setConnectionManagerShared(true); // senno' la close di una connessione fa si che venga chiuso il reactor
			httpClientBuilder.setDefaultRequestConfig(requestConfig);
			httpClientBuilder.disableAuthCaching();
			
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
			}
			
			// ** Proxy **
			setProxy(httpClientBuilder, connectionConfig);
			
			// ** Reactor **
			/**IOReactorConfig.Builder reactorConfigBuilder = IOReactorConfig.custom();
			reactorConfigBuilder.setIoThreadCount(ioThreadCount);
			httpClientBuilder.setIOReactorConfig(reactorConfigBuilder.build());*/
			Callback<Exception> callback = e -> {
			    OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori().error("[IoReactorExceptionCallback] " + e.getMessage(), e);
			    OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("[IoReactorExceptionCallback] " + e.getMessage(), e);
			};
			httpClientBuilder.setIoReactorExceptionCallback(callback);
			
			CloseableHttpAsyncClient httpclient = httpClientBuilder.build();
			httpclient.start();
			
			OpenSPCoop2Properties op2 = OpenSPCoop2Properties.getInstance();
			int expireUnusedAfterSeconds = op2.getNIOConfigAsyncClientExpireUnusedAfterSeconds(); 
			int closeUnusedAfterSeconds = op2.getNIOConfigAsyncClientCloseUnusedAfterSeconds(); 
			
			return new ConnettoreHTTPCOREConnection(key, httpclient, requestConfig,
					expireUnusedAfterSeconds, closeUnusedAfterSeconds);
			
		} catch ( Exception t ) {
			throw new ConnettoreException( t.getMessage(),t );
		}
	}
	private static void setProxy(HttpAsyncClientBuilder httpClientBuilder, AbstractConnettoreConnectionConfig connectionConfig) {
		if(connectionConfig.getProxyHost()!=null && connectionConfig.getProxyPort()!=null) {
			HttpHost proxy = new HttpHost(connectionConfig.getProxyHost(), connectionConfig.getProxyPort());
			httpClientBuilder.setProxy(proxy);
		}
	}
	private static TlsStrategy buildClientTlsStrategyBuilder(ConnettoreHTTPCOREConnectionConfig connectionConfig, Loader loader, 
			RequestInfo requestInfo, 
			ConnettoreLogger logger) throws UtilsException {
		SSLContext sslContext = null;
		HostnameVerifier hostnameVerifier = null;
		if(connectionConfig.getSslContextProperties()!=null) {
			StringBuilder bfLog = new StringBuilder();
			sslContext = buildSSLContext(connectionConfig.getSslContextProperties(), requestInfo, logger, bfLog);
			hostnameVerifier = SSLUtilities.generateHostnameVerifier(connectionConfig.getSslContextProperties(), bfLog, logger.getLogger(), loader);
			if(connectionConfig.isDebug()) {
				logger.debug(bfLog.toString());
			}
		}
		ClientTlsStrategyBuilder tlsBuilder = ClientTlsStrategyBuilder.create();
		tlsBuilder.setSslContext(sslContext);
		tlsBuilder.setHostnameVerifier(hostnameVerifier);
		return tlsBuilder.build();
	}
	private static SSLContext buildSSLContext(SSLConfig httpsProperties, RequestInfo requestInfo, 
			ConnettoreLogger logger, StringBuilder bfLog) throws UtilsException {
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
		
		return SSLUtilities.generateSSLContext(httpsProperties, bfLog);
	}
	
	public static ConnettoreHTTPCOREConnection getConnettoreHTTPCOREConnection(ConnettoreHTTPCOREConnectionConfig connectionConfig,
			Loader loader, ConnettoreLogger logger, 
			RequestInfo requestInfo,
			ConnectionKeepAliveStrategy keepAliveStrategy,
			ConnettoreHttpRequestInterceptor httpRequestInterceptor) throws ConnettoreException  {
		
		ConnettoreHTTPCOREConnection connection = null;
		if(USE_POOL_CONNECTION) {
			String key = connectionConfig.toKeyConnection();
			if(!mapConnection.containsKey(key)) {
				init(connectionConfig, 
						loader, logger, 
						requestInfo,
						keepAliveStrategy,
						httpRequestInterceptor);
				connection = mapConnection.get(key);
				connection.refresh();
			}
			else {
				connection = mapConnection.get(key);
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
