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

package org.openspcoop2.pdd.core.connettori.httpcore5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.connettori.AbstractConnettoreConnectionConfig;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreHttpPoolParams;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.openspcoop2.utils.transport.http.WrappedLogSSLSocketFactory;

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

	static Map<String, PoolingHttpClientConnectionManager> mapPoolingConnectionManager = new HashMap<>();
	static Map<String, ConnettoreHTTPCOREConnection> mapConnection = new HashMap<>();
	
	private static ConnettoreHTTPCOREConnectionEvictor idleConnectionEvictor;
		
	public static synchronized void initialize() throws ConnettoreException {
		try {
			if(ConnettoreHTTPCOREConnectionManager.idleConnectionEvictor==null) {
				OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
				Integer closeIdleConnectionsAfterSeconds = op2Properties.getBIOConfigSyncClientCloseIdleConnectionsAfterSeconds();
				boolean idleConnectionEvictorEnabled = (closeIdleConnectionsAfterSeconds!=null && closeIdleConnectionsAfterSeconds>0);
				if(idleConnectionEvictorEnabled) {
					int sleepTimeSeconds = op2Properties.getBIOConfigSyncClientCloseIdleConnectionsCheckIntervalSeconds();
					boolean debug = op2Properties.isBIOConfigSyncClientCloseIdleConnectionsDebug();
					ConnettoreHTTPCOREConnectionManager.idleConnectionEvictor = 
							new ConnettoreHTTPCOREConnectionEvictor(debug, sleepTimeSeconds, closeIdleConnectionsAfterSeconds);
					idleConnectionEvictor.start();
				}
			}
		}catch(Exception t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
	}
	
	private static synchronized void initialize(String key, SSLConnectionSocketFactory sslConnectionSocketFactory, ConnettoreHTTPCOREConnectionConfig connectionConfig) throws ConnettoreException {
		
		if(!ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.containsKey(key)){
		
			try {			
				PoolingHttpClientConnectionManagerBuilder poolingConnectionManagerBuilder = PoolingHttpClientConnectionManagerBuilder.create();
				
				if(sslConnectionSocketFactory!=null) {
					poolingConnectionManagerBuilder.setSSLSocketFactory(sslConnectionSocketFactory);
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
				
				PoolingHttpClientConnectionManager poolingConnectionManager = poolingConnectionManagerBuilder.build();
								
				/** Gestito con 'setSSLSocketFactory' 
	               if(sslConnectionSocketFactory!=null) {
	                       Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
	                               .<ConnectionSocketFactory> create().register("https", sslConnectionSocketFactory)
	                               .build();
	                       cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
	               }
	               else {
	                       cm = new PoolingHttpClientConnectionManager();
	               }
				 */
				
				ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.put(key, poolingConnectionManager);
				
			}catch(Exception t) {
				throw new ConnettoreException(t.getMessage(),t);
			}
			
		}
		
	}
	private static ConnectionConfig buildConnectionConfig(ConnettoreHttpPoolParams poolParams, long connectionTimeout) {
		ConnectionConfig.Builder buider = ConnectionConfig.custom();
		buider.setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
		/**
		 * specifica l'intervallo di tempo di inattività dopo il quale le connessioni persistenti dovrebbero essere convalidate prima di essere riutilizzate. 
		 * Questo è importante per evitare l'uso di connessioni che potrebbero essere state chiuse dal server o interrotte per altri motivi.
		 * Un parametro troppo basso, a meno che non ci sia una necessità specifica, non va usato poiché ciò potrebbe introdurre un overhead significativo e influire sulle prestazioni complessive dell'applicazione.
		 **/
		if(poolParams.getValidateAfterInactivity()!=null) {
			buider.setValidateAfterInactivity(poolParams.getValidateAfterInactivity().intValue(), TimeUnit.MILLISECONDS);
		}
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		Integer closeIdleConnectionsAfterSeconds = op2Properties.getBIOConfigSyncClientCloseIdleConnectionsAfterSeconds();
		boolean idleConnectionEvictorEnabled = (closeIdleConnectionsAfterSeconds!=null && closeIdleConnectionsAfterSeconds>0);
		if(idleConnectionEvictorEnabled) {
			// Defines the total span of time connections can be kept alive or execute requests
			int sleepTimeSeconds = op2Properties.getBIOConfigSyncClientCloseIdleConnectionsAfterSeconds();
			buider.setTimeToLive(sleepTimeSeconds, TimeUnit.SECONDS);
		}
		
		return buider.build();
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
	public static synchronized void stopClients(List<Throwable> listT) throws ConnettoreException {
		if(mapConnection!=null && !mapConnection.isEmpty()) {
			Iterator<String> it = mapConnection.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				ConnettoreHTTPCOREConnection connection = mapConnection.get(key);
				try {
					connection.close(); // GRACEFUL tenta di chiudere le connessioni in modo ordinato, permettendo alle richieste in corso di completarsi.
				}catch(Exception t) {
					listT.add(new ConnettoreException("Connection ["+key+"] close error: "+t.getMessage(),t));
				}
			}
		}
	}
	public static synchronized void stopConnectionManager(List<Throwable> listT) {
		if(ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager!=null && ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.isEmpty()) {
			for (String key : ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.keySet()) {
				if(key!=null) {
					PoolingHttpClientConnectionManager cm = ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.get(key);
					stopConnectionManager(cm, listT);
				}
			}
		}
	}
	public static synchronized void stopConnectionManager(PoolingHttpClientConnectionManager cm, List<Throwable> listT) {
		if(cm!=null) {
			try {			
				cm.close(CloseMode.GRACEFUL); // tenta di chiudere le connessioni in modo ordinato, permettendo alle richieste in corso di completarsi.
			}catch(Exception t) {
				listT.add(t);
			}
		}
	}
	
	private static final org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore(ConnettoreHTTPCORE.ID_HTTPCORE+"-ConnectionManager");
	
	private static void init(ConnettoreHTTPCOREConnectionConfig connectionConfig,SSLSocketFactory sslSocketFactory,
			Loader loader, ConnettoreLogger logger, 
			ConnectionKeepAliveStrategy keepAliveStrategy,
			ConnettoreHttpRequestInterceptor httpRequestInterceptor) throws ConnettoreException {
		String key = connectionConfig.toKeyConnectionManager();
		String idTransazione = logger!=null ? logger.getIdTransazione() : null;
		try {
			semaphore.acquire("initConnection",idTransazione);
		}catch(Exception t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
		try {
			if(!mapConnection.containsKey(key)) {
				ConnettoreHTTPCOREConnection resource = buildClient(connectionConfig, sslSocketFactory,
						loader, logger, key,
						keepAliveStrategy,
						httpRequestInterceptor);
				mapConnection.put(key, resource);
			}
		}finally {
			semaphore.release("initConnection",idTransazione);
		}
	}
	private static ConnettoreHTTPCOREConnection update(ConnettoreHTTPCOREConnectionConfig connectionConfig,SSLSocketFactory sslSocketFactory,
			Loader loader, ConnettoreLogger logger, 
			ConnectionKeepAliveStrategy keepAliveStrategy,
			ConnettoreHttpRequestInterceptor httpRequestInterceptor) throws ConnettoreException {
		String key = connectionConfig.toKeyConnectionManager();
		String idTransazione = logger!=null ? logger.getIdTransazione() : null;
		try {
			semaphore.acquire("updateConnection",idTransazione);
		}catch(Exception t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
		try {
			if(mapConnection.containsKey(key)) {
				ConnettoreHTTPCOREConnection con = mapConnection.remove(key);
				mapConnection.put("expired_"+key+"_"+UUID.randomUUID().toString(), con);
			}
			ConnettoreHTTPCOREConnection resource = buildClient(connectionConfig, sslSocketFactory,
					loader, logger, key,
					keepAliveStrategy,
					httpRequestInterceptor);
			mapConnection.put(key, resource);
			return resource;
		}finally {
			semaphore.release("updateConnection",idTransazione);
		}
	}

	private static ConnettoreHTTPCOREConnection buildClient(ConnettoreHTTPCOREConnectionConfig connectionConfig,SSLSocketFactory sslSocketFactory,
			Loader loader, ConnettoreLogger logger, String key,
			ConnectionKeepAliveStrategy keepAliveStrategy,
			ConnettoreHttpRequestInterceptor httpRequestInterceptor) throws ConnettoreException {
		try {				
						
			RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
			
			// ** Timeout **
			setTimeout(requestConfigBuilder, connectionConfig);
			
			// ** Redirect **
			setRedirect(requestConfigBuilder, connectionConfig);
			
			// ** Proxy **
			setProxy(requestConfigBuilder, connectionConfig);
			
			RequestConfig requestConfig = requestConfigBuilder.build();
			
			// Pool
			String keyPool = connectionConfig.toKeyConnectionManager();
			
			if(!ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.containsKey(keyPool)){
				SSLConnectionSocketFactory sslConnectionSocketFactory = buildSSLConnectionSocketFactory(connectionConfig,
						sslSocketFactory,
						loader, logger);
				ConnettoreHTTPCOREConnectionManager.initialize(keyPool, sslConnectionSocketFactory, connectionConfig);
			}
			PoolingHttpClientConnectionManager cm = ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.get(keyPool);
			
			HttpClientBuilder httpClientBuilder = HttpClients.custom();
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
			
			CloseableHttpClient httpclient = httpClientBuilder.build();
			
			OpenSPCoop2Properties op2 = OpenSPCoop2Properties.getInstance();
			int expireUnusedAfterSeconds = op2.getBIOConfigSyncClientExpireUnusedAfterSeconds(); 
			int closeUnusedAfterSeconds = op2.getBIOConfigSyncClientCloseUnusedAfterSeconds(); 
			
			return  new ConnettoreHTTPCOREConnection(key, httpclient, requestConfig,
					expireUnusedAfterSeconds, closeUnusedAfterSeconds);
			
		} catch ( Exception t ) {
			throw new ConnettoreException( t.getMessage(),t );
		}
	}
	private static void setTimeout(RequestConfig.Builder requestConfigBuilder, AbstractConnettoreConnectionConfig connectionConfig) {
		int connectionTimeout = -1;
		if(connectionConfig.getConnectionTimeout()!=null) {
			connectionTimeout = connectionConfig.getConnectionTimeout();
		}
		else {
			connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
		}
		requestConfigBuilder.setConnectionRequestTimeout(connectionTimeout, TimeUnit.MILLISECONDS); // quanto tempo il client aspetterà per ottenere una connessione dal pool.
		/** requestConfigBuilder.setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS); spostato in initialize della connnection */
		
		
		int readTimeout = -1;
		if(connectionConfig.getReadTimeout()!=null) {
			readTimeout = connectionConfig.getReadTimeout();
		}
		else {
			readTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
		}
		requestConfigBuilder.setResponseTimeout( readTimeout, TimeUnit.MILLISECONDS  ); // // Defines the socket timeout (SO_TIMEOUT) in milliseconds, which is the timeout for waiting for data or, put differently, a maximum period inactivity between two consecutive data packets).
		
	}
	private static void setRedirect(RequestConfig.Builder requestConfigBuilder, AbstractConnettoreConnectionConfig connectionConfig) {
		requestConfigBuilder.setRedirectsEnabled(connectionConfig.isFollowRedirect());
		requestConfigBuilder.setCircularRedirectsAllowed(true); // da file properties
		requestConfigBuilder.setMaxRedirects(connectionConfig.getMaxNumberRedirects());
	}
	private static void setProxy(RequestConfig.Builder requestConfigBuilder, AbstractConnettoreConnectionConfig connectionConfig) {
		if(connectionConfig.getProxyHost()!=null && connectionConfig.getProxyPort()!=null) {
			requestConfigBuilder.setProxy(new HttpHost(connectionConfig.getProxyHost(), connectionConfig.getProxyPort()));
		}
	}
	private static SSLConnectionSocketFactory buildSSLConnectionSocketFactory(AbstractConnettoreConnectionConfig connectionConfig,
			SSLSocketFactory sslSocketFactory,
			Loader loader, ConnettoreLogger logger) throws UtilsException {
		SSLConnectionSocketFactory sslConnectionSocketFactory = null;
		if(connectionConfig.getSslContextProperties()!=null) {
			if(connectionConfig.isDebug()) {
				String clientCertificateConfigurated = connectionConfig.getSslContextProperties().getKeyStoreLocation();
				sslSocketFactory = new WrappedLogSSLSocketFactory(sslSocketFactory, 
						logger.getLogger(), logger.buildMsg(""),
						clientCertificateConfigurated);
			}		
			
			StringBuilder bfLog = new StringBuilder();
			HostnameVerifier hostnameVerifier = SSLUtilities.generateHostnameVerifier(connectionConfig.getSslContextProperties(), bfLog, 
					logger.getLogger(), loader);
			if(connectionConfig.isDebug()) {
				logger.debug(bfLog.toString());
			}
			
			if(hostnameVerifier==null) {
				hostnameVerifier = new DefaultHostnameVerifier();
			}
			sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslSocketFactory, hostnameVerifier);
		}
		return sslConnectionSocketFactory;
	}
	
	private static final boolean USE_POOL = true;

	public static ConnettoreHTTPCOREConnection getConnettoreHTTPCOREConnection(ConnettoreHTTPCOREConnectionConfig connectionConfig,SSLSocketFactory sslSocketFactory,
			Loader loader, ConnettoreLogger logger,
			ConnectionKeepAliveStrategy keepAliveStrategy,
			ConnettoreHttpRequestInterceptor httpRequestInterceptor) throws ConnettoreException  {
		
		ConnettoreHTTPCOREConnection connection = null;
		if(USE_POOL) {
			String key = connectionConfig.toKeyConnectionManager();
			if(!mapConnection.containsKey(key)) {
				init(connectionConfig, sslSocketFactory,
						loader, logger,
						keepAliveStrategy, httpRequestInterceptor);
				connection = mapConnection.get(key);
				connection.refresh();
			}
			else {
				connection = mapConnection.get(key);
				connection.refresh();
				if(connection.isExpired()) {
					connection = update(connectionConfig, sslSocketFactory,
							loader, logger,
							keepAliveStrategy, httpRequestInterceptor);
				}
			}
		}
		else {
			connection = buildClient(connectionConfig, sslSocketFactory,
					loader, logger, connectionConfig.toString(),
					keepAliveStrategy,
					httpRequestInterceptor);
			connection.refresh();
		}
		return connection;
	}
	
}
