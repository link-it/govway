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

package org.openspcoop2.pdd.core.connettori.httpjava.nio;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;
import org.openspcoop2.pdd.core.connettori.nio.ConnectionConfiguration;
import org.openspcoop2.pdd.services.connector.AsyncThreadPool;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * ConnettoreHTTPJava_connectionManager
 *
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPJava_connectionManager {

	// ******** STATIC **********

	static ExecutorService executors = null;
	static Map<String, ConnettoreHTTPJava_connection> mapConnection = new HashMap<String, ConnettoreHTTPJava_connection>();

	// TBK da definire
//	private static ConnettoreHTTPCORE5_connectionEvictor idleConnectionEvictor;
		
	public static synchronized void initialize() throws ConnettoreException {
		try {
			//executors = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );
			executors = AsyncThreadPool.getResponsePool();
			// TBK
//			if(ConnettoreHTTPJava_connectionManager.idleConnectionEvictor==null) {
//				OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
//				Integer closeIdleConnectionsAfterSeconds = op2Properties.getNIOConfig_asyncClient_closeIdleConnectionsAfterSeconds();
//				boolean idleConnectionEvictorEnabled = (closeIdleConnectionsAfterSeconds!=null && closeIdleConnectionsAfterSeconds>0);
//				if(idleConnectionEvictorEnabled) {
//					int sleepTimeSeconds = op2Properties.getNIOConfig_asyncClient_closeIdleConnectionsCheckIntervalSeconds();
//					boolean debug = op2Properties.isNIOConfig_asyncClient_closeIdleConnectionsDebug();
//					ConnettoreHTTPJava_connectionManager.idleConnectionEvictor = 
//							new ConnettoreHTTPCORE5_connectionEvictor(debug, sleepTimeSeconds, closeIdleConnectionsAfterSeconds);
//					idleConnectionEvictor.start();
//				}
//			}
		}catch(Throwable t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
	}
	
	@SuppressWarnings("unused")
	private static synchronized void initialize(String key, SSLContext sslContext, HostnameVerifier hostnameVerifier) throws ConnettoreException {
/* TBK da rivedere
		if(!ConnettoreHTTPJava_connectionManager.mapPoolingConnectionManager.containsKey(key)){
		
			try {			
				PoolingAsyncClientConnectionManagerBuilder poolingConnectionManagerBuilder = PoolingAsyncClientConnectionManagerBuilder.create();
				
				if(sslContext!=null) {
					ClientTlsStrategyBuilder tlsBuilder = ClientTlsStrategyBuilder.create();
					tlsBuilder.setSslContext(sslContext);
					tlsBuilder.setHostnameVerifier(hostnameVerifier);
					TlsStrategy tlsStrategy = tlsBuilder.build();
					poolingConnectionManagerBuilder.setTlsStrategy(tlsStrategy);
				}
				else {
					poolingConnectionManagerBuilder.useSystemProperties();
				}
				
				//poolingConnectionManagerBuilder.setDnsResolver(null);
				//poolingConnectionManagerBuilder.setSchemePortResolver(null);
				
				// LAX: Higher concurrency but with lax connection max limit guarantees.
				// STRICT: Strict connection max limit guarantees.
				poolingConnectionManagerBuilder.setPoolConcurrencyPolicy(PoolConcurrencyPolicy.LAX);

				// FIFO: Re-use all connections equally preventing them from becoming idle and expiring.
				// LIFO: Re-use as few connections as possible making it possible for connections to become idle and expire.
				poolingConnectionManagerBuilder.setConnPoolPolicy(PoolReusePolicy.FIFO);
				
				// IdleConnection
				OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
				Integer closeIdleConnectionsAfterSeconds = op2Properties.getNIOConfig_asyncClient_closeIdleConnectionsAfterSeconds();
				boolean idleConnectionEvictorEnabled = (closeIdleConnectionsAfterSeconds!=null && closeIdleConnectionsAfterSeconds>0);
				if(idleConnectionEvictorEnabled) {
					int sleepTimeSeconds = op2Properties.getNIOConfig_asyncClient_closeIdleConnectionsCheckIntervalSeconds();
					poolingConnectionManagerBuilder.setConnectionTimeToLive(TimeValue.ofSeconds(closeIdleConnectionsAfterSeconds));
					poolingConnectionManagerBuilder.setValidateAfterInactivity(TimeValue.ofSeconds(sleepTimeSeconds));
				}
				
							
				// PoolingNHttpClientConnectionManager maintains a maximum limit of connection on a per route basis and in total. 
				// Per default this implementation will create no more than than 2 concurrent connections per given route and no more 20 connections in total.
				// For many real-world applications these limits may prove too constraining, especially if they use HTTP as a transport protocol for their services. 
				// Connection limits, however, can be adjusted using ConnPoolControl methods.
				Integer defaultMaxPerRoute = op2Properties.getNIOConfig_asyncClient_maxPerRoute();
				Integer maxTotal = op2Properties.getNIOConfig_asyncClient_maxTotal();
				if(defaultMaxPerRoute!=null && defaultMaxPerRoute>0) {
					poolingConnectionManagerBuilder.setMaxConnPerRoute( defaultMaxPerRoute ); // 2 default
				}
				if(maxTotal!=null && maxTotal>0) {
					poolingConnectionManagerBuilder.setMaxConnTotal( maxTotal ); // 20 default
				}
				
				PoolingAsyncClientConnectionManager poolingConnectionManager = poolingConnectionManagerBuilder.build();

				ConnettoreHTTPJava_connectionManager.mapPoolingConnectionManager.put(key, poolingConnectionManager);
				
			}catch(Throwable t) {
				throw new ConnettoreException(t.getMessage(),t);
			}
			
		}
 */
	}
	public static synchronized void stop() throws ConnettoreException {
			
		List<Throwable> listT = new ArrayList<Throwable>();

/* TBK
		// Shut down the evictor thread
		if(ConnettoreHTTPJava_connectionManager.idleConnectionEvictor!=null) {
			try {			
				ConnettoreHTTPJava_connectionManager.idleConnectionEvictor.setStop(true);
				idleConnectionEvictor.waitShutdown();
			}catch(Throwable t) {
				listT.add(t);
			}	
		}
 */

		// close client
		if(mapConnection!=null && !mapConnection.isEmpty()) {
			// La chiusura verrà fatta dal Garbage Collector
			mapConnection.clear();
		}
		
/* TBK da rivedere
		// Shut down connManager
		if(ConnettoreHTTPJava_connectionManager.mapPoolingConnectionManager!=null && ConnettoreHTTPJava_connectionManager.mapPoolingConnectionManager.isEmpty()) {
			for (String key : ConnettoreHTTPJava_connectionManager.mapPoolingConnectionManager.keySet()) {
				if(key!=null) {
					PoolingAsyncClientConnectionManager cm = ConnettoreHTTPJava_connectionManager.mapPoolingConnectionManager.get(key);
					if(cm!=null) {
						try {			
							cm.close(CloseMode.GRACEFUL);
						}catch(Throwable t) {
							listT.add(t);
						}
					}
				}
			}
		}
 */

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
		
	
	private static final org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("ConnettoreHTTPJava_connectionManager");
	
	private static void init(ConnectionConfiguration connectionConfig,
			Loader loader, ConnettoreLogger logger, StringBuilder bf) throws ConnettoreException {
		String key = connectionConfig.toString();
		//synchronized(mapConnection) {
		String idTransazione = logger!=null ? logger.getIdTransazione() : null;
		try {
			semaphore.acquire("initConnection",idTransazione);
		}catch(Throwable t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
		try {
			if(!mapConnection.containsKey(key)) {
				ConnettoreHTTPJava_connection resource = buildAsyncClient(connectionConfig, loader, logger, bf, key);
				mapConnection.put(key, resource);
			}
		}finally {
			semaphore.release("initConnection",idTransazione);
		}
	}
	private static ConnettoreHTTPJava_connection update(ConnectionConfiguration connectionConfig,
			Loader loader, ConnettoreLogger logger, StringBuilder bf) throws ConnettoreException {
		String key = connectionConfig.toString();
		//synchronized(mapConnection) {
		String idTransazione = logger!=null ? logger.getIdTransazione() : null;
		try {
			semaphore.acquire("updateConnection",idTransazione);
		}catch(Throwable t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
		try {
			if(mapConnection.containsKey(key)) {
				ConnettoreHTTPJava_connection con = mapConnection.remove(key);
				mapConnection.put("expired_"+key+"_"+UUID.randomUUID().toString(), con);
			}
			ConnettoreHTTPJava_connection resource = buildAsyncClient(connectionConfig, loader, logger, bf, key);
			mapConnection.put(key, resource);
			return resource;
		}finally {
			semaphore.release("updateConnection",idTransazione);
		}
	}

	private static ConnettoreHTTPJava_connection buildAsyncClient(ConnectionConfiguration connectionConfig,
			Loader loader, ConnettoreLogger logger, StringBuilder bf,
			String key) throws ConnettoreException {
		try {
			HttpClient.Builder builder = HttpClient.newBuilder();

			// Timeout
			if(bf.length()>0) {
				bf.append("\n");
			}
			int connectionTimeout = -1;
			if(connectionConfig.getConnectionTimeout()!=null) {
				connectionTimeout = connectionConfig.getConnectionTimeout();
				bf.append("Connection Timeout: ").append(connectionTimeout);
			}
			else {
				connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
				bf.append("Connection Timeout Default: ").append(connectionTimeout);
			}
			// TBK da rivedere
//			requestConfigBuilder.setConnectionRequestTimeout( connectionTimeout, TimeUnit.MILLISECONDS );  //  timeout in milliseconds used when requesting a connection from the connection manager.
			builder = builder.connectTimeout( Duration.ofSeconds( connectionTimeout ) );
			
			// TBK da rivedere
//			if(bf.length()>0) {
//				bf.append("\n");
//			}
//			int readTimeout = -1;
//			if(connectionConfig.getReadTimeout()!=null) {
//				readTimeout = connectionConfig.getReadTimeout();
//				bf.append("Connection Read Timeout: ").append(readTimeout);
//			}
//			else {
//				readTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
//				bf.append("Connection Read Timeout Default: ").append(readTimeout);
//			}
//			requestConfigBuilder.setResponseTimeout( readTimeout, TimeUnit.MILLISECONDS  ); // // Defines the socket timeout (SO_TIMEOUT) in milliseconds, which is the timeout for waiting for data or, put differently, a maximum period inactivity between two consecutive data packets).
			
			// Proxy
			if(connectionConfig.getProxyHost()!=null && connectionConfig.getProxyPort()!=null) {
				if(bf.length()>0) {
					bf.append("\n");
				}
				bf.append("Proxy: ").append(connectionConfig.getProxyHost()).append(":").append(connectionConfig.getProxyPort());
				builder = builder.proxy( ProxySelector.of( new InetSocketAddress( connectionConfig.getProxyHost(), connectionConfig.getProxyPort() ) ) );
			}
			
			// KeepAlive
			// requestConfigBuilder.setDefaultKeepAlive(readTimeout, null);
			
			// Redirect
			// requestConfigBuilder.setRedirectsEnabled(false);
			// TBK da rivedere
//			requestConfigBuilder.setRedirectsEnabled(connectionConfig.isFollowRedirect());
//			requestConfigBuilder.setCircularRedirectsAllowed(true); // da file properties
//			requestConfigBuilder.setMaxRedirects(connectionConfig.getMaxNumberRedirects());
			
			// Pool
			// TBK da rivedere
/*
			builder = builder.sslContext( ... );
			builder = builder.sslParameters( ... );
			String keyPool = connectionConfig.toString(true);
			if(!ConnettoreHTTPJava_connectionManager.mapPoolingConnectionManager.containsKey(keyPool)){
				
				SSLContext sslContext = null;
				HostnameVerifier hostnameVerifier = null;
				if(connectionConfig.getSslContextProperties()!=null) {
					if(bf.length()>0) {
						bf.append("\n");
					}
					sslContext = TlsContextBuilder.buildSSLContext(connectionConfig.getSslContextProperties(), logger, bf);
					hostnameVerifier = SSLUtilities.generateHostnameVerifier(connectionConfig.getSslContextProperties(), bf, logger.getLogger(), loader);
				}
				
				ConnettoreHTTPJava_connectionManager.initialize(keyPool, sslContext, hostnameVerifier);
			}
			PoolingAsyncClientConnectionManager cm = ConnettoreHTTPJava_connectionManager.mapPoolingConnectionManager.get(keyPool);
 */

			// TBK da rivedere
//			clientBuilder.disableAuthCaching();
			
			// di default viene negoziata
//			builder = builder.version( HttpClient.Version.HTTP_1_1 );
			
			
			// TBK da rivedere ... di default è attivo e usa la "property jdk.httpclient.keppalive.timeout"
//			ConnectionKeepAliveStrategy keepAliveStrategy = null; // TODO
//			if(keepAliveStrategy!=null){
//				clientBuilder.setKeepAliveStrategy(keepAliveStrategy);
//			}
//			clientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
			
			// TBK da rivedere
//			builder = builder.followRedirects( Redirect.NORMAL );
//			if(connectionConfig.isFollowRedirect()) {
//				clientBuilder.setRedirectStrategy(DefaultRedirectStrategy.INSTANCE);
//			}

			builder = builder.executor( executors );
			HttpClient httpclient = builder.build();
			
			return  new ConnettoreHTTPJava_connection(key, httpclient);
			
		} catch ( Throwable t ) {
			throw new ConnettoreException( t.getMessage(),t );
		}
	}
	
	public static ConnettoreHTTPJava_connection getConnettoreNIO(ConnectionConfiguration connectionConfig,
			Loader loader, ConnettoreLogger logger, StringBuilder bf) throws ConnettoreException  {
		
		boolean usePoolAsyncClient = true; // e' inutilizzabile senza
		ConnettoreHTTPJava_connection connection = null;
		if(usePoolAsyncClient) {
			String key = connectionConfig.toString();
			if(!mapConnection.containsKey(key)) {
				init(connectionConfig, loader, logger, bf);
				connection = mapConnection.get(key);
				connection.refresh();
			}
			else {
				connection = mapConnection.get(key);
				connection.refresh();
				if(connection.isExpired()) {
					connection = update(connectionConfig, loader, logger, bf);
				}
			}
		}
		else {
			connection =  buildAsyncClient(connectionConfig, loader, logger, bf,
					connectionConfig.toString());
			connection.refresh();
		}
		return connection;
	}
	
}
