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

package org.openspcoop2.pdd.core.connettori.nio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.util.TimeValue;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.SSLUtilities;

/**
 * ConnettoreHTTPCORE5_connectionManager
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORE5_connectionManager {

	// ******** STATIC **********

	static Map<String, PoolingAsyncClientConnectionManager> mapPoolingConnectionManager = new HashMap<String, PoolingAsyncClientConnectionManager>();
	static Map<String, ConnettoreHTTPCORE5_connection> mapConnection = new HashMap<String, ConnettoreHTTPCORE5_connection>();
	
	private static ConnettoreHTTPCORE5_connectionEvictor idleConnectionEvictor;
		
	public static synchronized void initialize() throws ConnettoreException {
		try {
			if(ConnettoreHTTPCORE5_connectionManager.idleConnectionEvictor==null) {
				OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
				Integer closeIdleConnectionsAfterSeconds = op2Properties.getNIOConfig_asyncClient_closeIdleConnectionsAfterSeconds();
				boolean idleConnectionEvictorEnabled = (closeIdleConnectionsAfterSeconds!=null && closeIdleConnectionsAfterSeconds>0);
				if(idleConnectionEvictorEnabled) {
					int sleepTimeSeconds = op2Properties.getNIOConfig_asyncClient_closeIdleConnectionsCheckIntervalSeconds();
					boolean debug = op2Properties.isNIOConfig_asyncClient_closeIdleConnectionsDebug();
					ConnettoreHTTPCORE5_connectionManager.idleConnectionEvictor = 
							new ConnettoreHTTPCORE5_connectionEvictor(debug, sleepTimeSeconds, closeIdleConnectionsAfterSeconds);
					idleConnectionEvictor.start();
				}
			}
		}catch(Throwable t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
	}
	
	private static synchronized void initialize(String key, SSLContext sslContext, HostnameVerifier hostnameVerifier) throws ConnettoreException {
		
		if(!ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager.containsKey(key)){
		
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
												
				//ConnettoreHTTPCORE5_connectionManager.mapIoReactor.put(key, ioReactor);
				ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager.put(key, poolingConnectionManager);
				
			}catch(Throwable t) {
				throw new ConnettoreException(t.getMessage(),t);
			}
			
		}
		
	}
	public static synchronized void stop() throws ConnettoreException {
			
		List<Throwable> listT = new ArrayList<Throwable>();
		
		// Shut down the evictor thread
		if(ConnettoreHTTPCORE5_connectionManager.idleConnectionEvictor!=null) {
			try {			
				ConnettoreHTTPCORE5_connectionManager.idleConnectionEvictor.setStop(true);
				idleConnectionEvictor.waitShutdown();
			}catch(Throwable t) {
				listT.add(t);
			}	
		}
		
		// close client
		if(mapConnection!=null && !mapConnection.isEmpty()) {
			Iterator<String> it = mapConnection.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				ConnettoreHTTPCORE5_connection connection = mapConnection.get(key);
				try {
					connection.getHttpclient().close();
				}catch(Throwable t) {
					listT.add(new ConnettoreException("NIO Connection ["+key+"] close error: "+t.getMessage(),t));
				}
			}
		}
		
		// Shut down connManager
		if(ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager!=null && ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager.isEmpty()) {
			for (String key : ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager.keySet()) {
				if(key!=null) {
					PoolingAsyncClientConnectionManager cm = ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager.get(key);
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
	
	private static final org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("ConnettoreHTTPCORE5_connectionManager");
	
	private static void init(ConnectionConfiguration connectionConfig,
			Loader loader, ConnettoreLogger logger, StringBuilder bf,
			RequestInfo requestInfo) throws ConnettoreException {
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
				ConnettoreHTTPCORE5_connection resource = buildAsyncClient(connectionConfig, loader, logger, bf, key, requestInfo);
				mapConnection.put(key, resource);
			}
		}finally {
			semaphore.release("initConnection",idTransazione);
		}
	}
	private static ConnettoreHTTPCORE5_connection update(ConnectionConfiguration connectionConfig,
			Loader loader, ConnettoreLogger logger, StringBuilder bf,
			RequestInfo requestInfo) throws ConnettoreException {
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
				ConnettoreHTTPCORE5_connection con = mapConnection.remove(key);
				mapConnection.put("expired_"+key+"_"+UUID.randomUUID().toString(), con);
			}
			ConnettoreHTTPCORE5_connection resource = buildAsyncClient(connectionConfig, loader, logger, bf, key, requestInfo);
			mapConnection.put(key, resource);
			return resource;
		}finally {
			semaphore.release("updateConnection",idTransazione);
		}
	}

	private static ConnettoreHTTPCORE5_connection buildAsyncClient(ConnectionConfiguration connectionConfig,
			Loader loader, ConnettoreLogger logger, StringBuilder bf,
			String key,
			RequestInfo requestInfo) throws ConnettoreException {
		try {				
			RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
			
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
			requestConfigBuilder.setConnectionRequestTimeout( connectionTimeout, TimeUnit.MILLISECONDS );  //  timeout in milliseconds used when requesting a connection from the connection manager.
			requestConfigBuilder.setConnectTimeout( connectionTimeout, TimeUnit.MILLISECONDS ); // Determines the timeout in milliseconds until a connection is established.
			
			
			if(bf.length()>0) {
				bf.append("\n");
			}
			int readTimeout = -1;
			if(connectionConfig.getReadTimeout()!=null) {
				readTimeout = connectionConfig.getReadTimeout();
				bf.append("Connection Read Timeout: ").append(readTimeout);
			}
			else {
				readTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
				bf.append("Connection Read Timeout Default: ").append(readTimeout);
			}
			requestConfigBuilder.setResponseTimeout( readTimeout, TimeUnit.MILLISECONDS  ); // // Defines the socket timeout (SO_TIMEOUT) in milliseconds, which is the timeout for waiting for data or, put differently, a maximum period inactivity between two consecutive data packets).
			
			// Proxy
			if(connectionConfig.getProxyHost()!=null && connectionConfig.getProxyPort()!=null) {
				if(bf.length()>0) {
					bf.append("\n");
				}
				bf.append("Proxy: ").append(connectionConfig.getProxyHost()).append(":").append(connectionConfig.getProxyPort());
				requestConfigBuilder.setProxy(new HttpHost(connectionConfig.getProxyHost(), connectionConfig.getProxyPort()));
			}
			
			// KeepAlive
			//requestConfigBuilder.setDefaultKeepAlive(readTimeout, null);
			
			// Redirect
			//requestConfigBuilder.setRedirectsEnabled(false);
			requestConfigBuilder.setRedirectsEnabled(connectionConfig.isFollowRedirect());
			requestConfigBuilder.setCircularRedirectsAllowed(true); // da file properties
			requestConfigBuilder.setMaxRedirects(connectionConfig.getMaxNumberRedirects());
			
			RequestConfig requestConfig = requestConfigBuilder.build();
			
			// Pool
			String keyPool = connectionConfig.toString(true);
			if(!ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager.containsKey(keyPool)){
				
				SSLContext sslContext = null;
				HostnameVerifier hostnameVerifier = null;
				if(connectionConfig.getSslContextProperties()!=null) {
					if(bf.length()>0) {
						bf.append("\n");
					}
					sslContext = TlsContextBuilder.buildSSLContext(connectionConfig.getSslContextProperties(), logger, bf, requestInfo);
					hostnameVerifier = SSLUtilities.generateHostnameVerifier(connectionConfig.getSslContextProperties(), bf, logger.getLogger(), loader);
				}
				
				ConnettoreHTTPCORE5_connectionManager.initialize(keyPool, sslContext, hostnameVerifier);
			}
			PoolingAsyncClientConnectionManager cm = ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager.get(keyPool);
			
			HttpAsyncClientBuilder clientBuilder = 
					HttpAsyncClients.custom(); // Qua si gestisce il pipe (ci sono i metodi che gestiscono le richieste una dopo l'altra o prima tutte le richieste e poi le risposte ...)
			clientBuilder.setConnectionManager( cm );
			clientBuilder.setConnectionManagerShared(true); // senno' la close di una connessione fa si che venga chiuso il reactor
			clientBuilder.setDefaultRequestConfig(requestConfig);
				
			clientBuilder.disableAuthCaching();
			
			//clientBuilder.setH2Config(H2Config.DEFAULT);
			//clientBuilder.setVersionPolicy(HttpVersionPolicy.FORCE_HTTP_2);
			clientBuilder.setVersionPolicy(HttpVersionPolicy.NEGOTIATE);
			
			//clientBuilder.setProxyAuthenticationStrategy(null);
			
//			DefaultClientConnectionReuseStrateg defaultClientConnectionReuseStrategy = new DefaultClientConnectionReuseStrategy();
//			clientBuilder.setConnectionReuseStrategy(defaultClientConnectionReuseStrategy);
			
//			ConnectionKeepAliveStrategy keepAliveStrategy = null; // TODO
//			if(keepAliveStrategy!=null){
//				clientBuilder.setKeepAliveStrategy(keepAliveStrategy);
//			}
			clientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
			
			if(connectionConfig.isFollowRedirect()) {
				clientBuilder.setRedirectStrategy(DefaultRedirectStrategy.INSTANCE);
			}
			
			//IOReactorConfig.Builder reactorConfigBuilder = IOReactorConfig.custom();
			//reactorConfigBuilder.setIoThreadCount(ioThreadCount);
			//clientBuilder.setIOReactorConfig(reactorConfigBuilder.build());
			clientBuilder.setIoReactorExceptionCallback(new Callback<Exception>() {
				@Override
				public void execute(Exception arg0) {
					System.out.println("ERRORE!!!!!!!!!: "+arg0.getMessage());
					arg0.printStackTrace(System.out);
				}
			});
			
			CloseableHttpAsyncClient httpclient = clientBuilder.build();
			httpclient.start();
			
			return  new ConnettoreHTTPCORE5_connection(key, httpclient);
			
		} catch ( Throwable t ) {
			throw new ConnettoreException( t.getMessage(),t );
		}
	}
	
	public static ConnettoreHTTPCORE5_connection getConnettoreNIO(ConnectionConfiguration connectionConfig,
			Loader loader, ConnettoreLogger logger, StringBuilder bf,
			RequestInfo requestInfo) throws ConnettoreException  {
		
		boolean usePoolAsyncClient = true; // e' inutilizzabile senza
		ConnettoreHTTPCORE5_connection connection = null;
		if(usePoolAsyncClient) {
			String key = connectionConfig.toString();
			if(!mapConnection.containsKey(key)) {
				init(connectionConfig, loader, logger, bf, requestInfo);
				connection = mapConnection.get(key);
				connection.refresh();
			}
			else {
				connection = mapConnection.get(key);
				connection.refresh();
				if(connection.isExpired()) {
					connection = update(connectionConfig, loader, logger, bf, requestInfo);
				}
			}
		}
		else {
			connection =  buildAsyncClient(connectionConfig, loader, logger, bf,
					connectionConfig.toString(),
					requestInfo);
			connection.refresh();
		}
		return connection;
	}
	
}
