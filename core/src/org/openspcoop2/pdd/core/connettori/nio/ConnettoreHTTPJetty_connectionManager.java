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

package org.openspcoop2.pdd.core.connettori.nio;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpProxy;
import org.eclipse.jetty.client.ProxyConfiguration;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;
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
public class ConnettoreHTTPJetty_connectionManager {

	// ******** STATIC **********

	static ExecutorService executors = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );
	static Map<String, ConnettoreHTTPJetty_connection> mapConnection = new HashMap<String, ConnettoreHTTPJetty_connection>();

	// TBK da definire
//	private static ConnettoreHTTPCORE5_connectionEvictor idleConnectionEvictor;
		
	public static synchronized void initialize() throws ConnettoreException {
		try {
			// TBK
//			if(ConnettoreHTTPJetty_connectionManager.idleConnectionEvictor==null) {
//				OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
//				Integer closeIdleConnectionsAfterSeconds = op2Properties.getNIOConfig_asyncClient_closeIdleConnectionsAfterSeconds();
//				boolean idleConnectionEvictorEnabled = (closeIdleConnectionsAfterSeconds!=null && closeIdleConnectionsAfterSeconds>0);
//				if(idleConnectionEvictorEnabled) {
//					int sleepTimeSeconds = op2Properties.getNIOConfig_asyncClient_closeIdleConnectionsCheckIntervalSeconds();
//					boolean debug = op2Properties.isNIOConfig_asyncClient_closeIdleConnectionsDebug();
//					ConnettoreHTTPJetty_connectionManager.idleConnectionEvictor = 
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
		if(!ConnettoreHTTPJetty_connectionManager.mapPoolingConnectionManager.containsKey(key)){
		
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

				ConnettoreHTTPJetty_connectionManager.mapPoolingConnectionManager.put(key, poolingConnectionManager);
				
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
		if(ConnettoreHTTPJetty_connectionManager.idleConnectionEvictor!=null) {
			try {			
				ConnettoreHTTPJetty_connectionManager.idleConnectionEvictor.setStop(true);
				idleConnectionEvictor.waitShutdown();
			}catch(Throwable t) {
				listT.add(t);
			}	
		}
 */

		// close client
		if(mapConnection!=null && !mapConnection.isEmpty()) {
			mapConnection.forEach( (k,c) -> {
				try {
					c.getHttpclient().stop();
				}catch(Throwable t) {
					listT.add(new ConnettoreException("NIO Connection ["+k+"] stop error: "+t.getMessage(),t));
				}
			});
			mapConnection.clear();
		}
		
/* TBK da rivedere
		// Shut down connManager
		if(ConnettoreHTTPJetty_connectionManager.mapPoolingConnectionManager!=null && ConnettoreHTTPJetty_connectionManager.mapPoolingConnectionManager.isEmpty()) {
			for (String key : ConnettoreHTTPJetty_connectionManager.mapPoolingConnectionManager.keySet()) {
				if(key!=null) {
					PoolingAsyncClientConnectionManager cm = ConnettoreHTTPJetty_connectionManager.mapPoolingConnectionManager.get(key);
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
		
	
	private static final org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("ConnettoreHTTPJetty_connectionManager");
	
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
				ConnettoreHTTPJetty_connection resource = buildAsyncClient(connectionConfig, loader, logger, bf, key);
				mapConnection.put(key, resource);
			}
		}finally {
			semaphore.release("initConnection",idTransazione);
		}
	}
	private static ConnettoreHTTPJetty_connection update(ConnectionConfiguration connectionConfig,
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
				ConnettoreHTTPJetty_connection con = mapConnection.remove(key);
				try {
					con.getHttpclient().stop();
				} catch (Exception e) {
					// TBK
					e.printStackTrace();
				}
				mapConnection.put("expired_"+key+"_"+UUID.randomUUID().toString(), con);
			}
			ConnettoreHTTPJetty_connection resource = buildAsyncClient(connectionConfig, loader, logger, bf, key);
			mapConnection.put(key, resource);
			return resource;
		}finally {
			semaphore.release("updateConnection",idTransazione);
		}
	}

	private static ConnettoreHTTPJetty_connection buildAsyncClient(ConnectionConfiguration connectionConfig,
			Loader loader, ConnettoreLogger logger, StringBuilder bf,
			String key) throws ConnettoreException {
		try {
			HttpClient client = new HttpClient();

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
			client.setConnectTimeout( Duration.ofSeconds( connectionTimeout ).toMillis() );
//			client.setAddressResolutionTimeout( Duration.ofSeconds( connectionTimeout ).toMillis() );
//			client.setIdleTimeout( Duration.ofSeconds( connectionTimeout ).toMillis() );
			
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
				ProxyConfiguration proxyConfig = client.getProxyConfiguration();
				proxyConfig.getProxies().add( new HttpProxy( connectionConfig.getProxyHost(), connectionConfig.getProxyPort() ) );
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
			if(!ConnettoreHTTPJetty_connectionManager.mapPoolingConnectionManager.containsKey(keyPool)){
				
				SSLContext sslContext = null;
				HostnameVerifier hostnameVerifier = null;
				if(connectionConfig.getSslContextProperties()!=null) {
					if(bf.length()>0) {
						bf.append("\n");
					}
					sslContext = TlsContextBuilder.buildSSLContext(connectionConfig.getSslContextProperties(), logger, bf);
					hostnameVerifier = SSLUtilities.generateHostnameVerifier(connectionConfig.getSslContextProperties(), bf, logger.getLogger(), loader);
				}
				
				ConnettoreHTTPJetty_connectionManager.initialize(keyPool, sslContext, hostnameVerifier);
			}
			PoolingAsyncClientConnectionManager cm = ConnettoreHTTPJetty_connectionManager.mapPoolingConnectionManager.get(keyPool);
 */

			// TBK da rivedere
//			clientBuilder.disableAuthCaching();
			
			// TBK da rivedere ... vedi setIdleTimemout
//			ConnectionKeepAliveStrategy keepAliveStrategy = null; // TODO
//			if(keepAliveStrategy!=null){
//				clientBuilder.setKeepAliveStrategy(keepAliveStrategy);
//			}
//			clientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
			
			// TBK da rivedere
//			builder = builder.followRedirects( Redirect.NORMAL );
			if(connectionConfig.isFollowRedirect()) {
				client.setFollowRedirects(connectionConfig.isFollowRedirect() );
				client.setMaxRedirects( connectionConfig.getMaxNumberRedirects() );
//				clientBuilder.setRedirectStrategy(DefaultRedirectStrategy.INSTANCE);
			}

			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			Integer defaultMaxPerRoute = op2Properties.getNIOConfig_asyncClient_maxPerRoute();
			Integer maxTotal = op2Properties.getNIOConfig_asyncClient_maxTotal();
			if(defaultMaxPerRoute!=null && defaultMaxPerRoute>0) {
				client.setMaxConnectionsPerDestination( defaultMaxPerRoute ); // 2 default
			}
			if(maxTotal!=null && maxTotal>0) {
				// TBK
				client.setMaxRequestsQueuedPerDestination( maxTotal );
			}

			client.setExecutor( executors );
			client.start();
			
			return  new ConnettoreHTTPJetty_connection(key, client);
			
		} catch ( Throwable t ) {
			throw new ConnettoreException( t.getMessage(),t );
		}
	}
	
	public static ConnettoreHTTPJetty_connection getConnettoreNIO(ConnectionConfiguration connectionConfig,
			Loader loader, ConnettoreLogger logger, StringBuilder bf) throws ConnettoreException  {
		
		boolean usePoolAsyncClient = true; // e' inutilizzabile senza
		ConnettoreHTTPJetty_connection connection = null;
		if(usePoolAsyncClient) {
			String key = connectionConfig.toString();
			if ( !mapConnection.containsKey(key) ) {
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
