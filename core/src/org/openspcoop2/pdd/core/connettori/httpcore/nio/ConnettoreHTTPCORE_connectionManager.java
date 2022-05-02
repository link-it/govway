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

package org.openspcoop2.pdd.core.connettori.httpcore.nio;

import java.io.IOException;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.client.DefaultClientConnectionReuseStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorExceptionHandler;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;
import org.openspcoop2.pdd.core.connettori.nio.ConnectionConfiguration;
import org.openspcoop2.pdd.core.connettori.nio.TlsContextBuilder;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.SSLUtilities;

/**
 * ConnettoreHTTPCORE_connectionManager
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORE_connectionManager {

	// ******** STATIC **********

	static Map<String, ConnectingIOReactor> mapIoReactor = new HashMap<String, ConnectingIOReactor>();
	static Map<String, PoolingNHttpClientConnectionManager> mapPoolingConnectionManager = new HashMap<String, PoolingNHttpClientConnectionManager>();
	static Map<String, ConnettoreHTTPCORE_connection> mapConnection = new HashMap<String, ConnettoreHTTPCORE_connection>();
	
	private static ConnettoreHTTPCORE_connectionEvictor idleConnectionEvictor;
		
	public static synchronized void initialize() throws ConnettoreException {
		try {
			if(ConnettoreHTTPCORE_connectionManager.idleConnectionEvictor==null) {
				OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
				Integer closeIdleConnectionsAfterSeconds = op2Properties.getNIOConfig_asyncClient_closeIdleConnectionsAfterSeconds();
				boolean idleConnectionEvictorEnabled = (closeIdleConnectionsAfterSeconds!=null && closeIdleConnectionsAfterSeconds>0);
				if(idleConnectionEvictorEnabled) {
					int sleepTimeSeconds = op2Properties.getNIOConfig_asyncClient_closeIdleConnectionsCheckIntervalSeconds();
					boolean debug = op2Properties.isNIOConfig_asyncClient_closeIdleConnectionsDebug();
					ConnettoreHTTPCORE_connectionManager.idleConnectionEvictor = 
							new ConnettoreHTTPCORE_connectionEvictor(debug, sleepTimeSeconds, closeIdleConnectionsAfterSeconds);
					idleConnectionEvictor.start();
				}
			}
		}catch(Throwable t) {
			throw new ConnettoreException(t.getMessage(),t);
		}
	}
	
	private static synchronized void initialize(String key, SSLContext sslContext, HostnameVerifier hostnameVerifier) throws ConnettoreException {
		
		if(!ConnettoreHTTPCORE_connectionManager.mapPoolingConnectionManager.containsKey(key)){
		
			try {
				IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
						//				 .setIoThreadCount(Runtime.getRuntime().availableProcessors())
										 .build();
				ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
				if(ioReactor instanceof DefaultConnectingIOReactor) {
					((DefaultConnectingIOReactor) ioReactor).setExceptionHandler(new IOReactorExceptionHandler() {
						@Override
						public boolean handle(RuntimeException runtimeException) {
							OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori().error("[HTTPCore] IOReactor ("+key+") runtimeException: "+runtimeException.getMessage(),runtimeException);
							return false;
						}
						@Override
						public boolean handle(IOException ioException) {
							OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori().error("[IOException] IOReactor ("+key+") ioException: "+ioException.getMessage(),ioException);
							return false;
						}
					});
				}
				
				PoolingNHttpClientConnectionManager poolingConnectionManager = null;
				if(sslContext!=null) {
					Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder.
							<SchemeIOSessionStrategy> create().register("https", new SSLIOSessionStrategy(sslContext, hostnameVerifier)).build();
					poolingConnectionManager = new PoolingNHttpClientConnectionManager(ioReactor, sessionStrategyRegistry);
				}
				else {
					poolingConnectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
				}
				
				ConnectionConfig connectionConfig =
						ConnectionConfig.custom()
							.setMalformedInputAction(CodingErrorAction.IGNORE)
							.setUnmappableInputAction(CodingErrorAction.IGNORE)
							.setCharset(Consts.UTF_8)
							.build();
				poolingConnectionManager.setDefaultConnectionConfig(connectionConfig);
				
				// PoolingNHttpClientConnectionManager maintains a maximum limit of connection on a per route basis and in total. 
				// Per default this implementation will create no more than than 2 concurrent connections per given route and no more 20 connections in total.
				// For many real-world applications these limits may prove too constraining, especially if they use HTTP as a transport protocol for their services. 
				// Connection limits, however, can be adjusted using ConnPoolControl methods.
				OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
				Integer defaultMaxPerRoute = op2Properties.getNIOConfig_asyncClient_maxPerRoute();
				Integer maxTotal = op2Properties.getNIOConfig_asyncClient_maxTotal();
				if(defaultMaxPerRoute!=null && defaultMaxPerRoute>0) {
					poolingConnectionManager.setDefaultMaxPerRoute( defaultMaxPerRoute ); // 2 default
				}
				if(maxTotal!=null && maxTotal>0) {
					poolingConnectionManager.setMaxTotal( maxTotal ); // 20 default
				}
				
				ConnettoreHTTPCORE_connectionManager.mapIoReactor.put(key, ioReactor);
				ConnettoreHTTPCORE_connectionManager.mapPoolingConnectionManager.put(key, poolingConnectionManager);
				
			}catch(Throwable t) {
				throw new ConnettoreException(t.getMessage(),t);
			}
			
		}
		
	}
	public static synchronized void stop() throws ConnettoreException {
			
		List<Throwable> listT = new ArrayList<Throwable>();
		
		// Shut down the evictor thread
		if(ConnettoreHTTPCORE_connectionManager.idleConnectionEvictor!=null) {
			try {			
				ConnettoreHTTPCORE_connectionManager.idleConnectionEvictor.setStop(true);
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
				ConnettoreHTTPCORE_connection connection = mapConnection.get(key);
				if(connection.getHttpclient().isRunning()) {
					try {
						connection.getHttpclient().close();
					}catch(Throwable t) {
						listT.add(new ConnettoreException("NIO Connection ["+key+"] close error: "+t.getMessage(),t));
					}
				}
			}
		}
		
		// Shut down connManager
		if(ConnettoreHTTPCORE_connectionManager.mapPoolingConnectionManager!=null && ConnettoreHTTPCORE_connectionManager.mapPoolingConnectionManager.isEmpty()) {
			for (String key : ConnettoreHTTPCORE_connectionManager.mapPoolingConnectionManager.keySet()) {
				if(key!=null) {
					PoolingNHttpClientConnectionManager cm = ConnettoreHTTPCORE_connectionManager.mapPoolingConnectionManager.get(key);
					if(cm!=null) {
						try {			
							cm.shutdown();
						}catch(Throwable t) {
							listT.add(t);
						}
					}
				}
			}
		}
		
		// Shut down ioReactor
		if(ConnettoreHTTPCORE_connectionManager.mapIoReactor!=null && ConnettoreHTTPCORE_connectionManager.mapIoReactor.isEmpty()) {
			for (String key : ConnettoreHTTPCORE_connectionManager.mapIoReactor.keySet()) {
				if(key!=null) {
					ConnectingIOReactor ioReactor = ConnettoreHTTPCORE_connectionManager.mapIoReactor.get(key);
					if(ioReactor!=null) {
						try {			
							ioReactor.shutdown();
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
		
	
	private static final org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("ConnettoreHTTPCORE_connectionManager");
	
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
				ConnettoreHTTPCORE_connection resource = buildAsyncClient(connectionConfig, loader, logger, bf, key, requestInfo);
				mapConnection.put(key, resource);
			}
		}finally {
			semaphore.release("initConnection",idTransazione);
		}
	}
	private static ConnettoreHTTPCORE_connection update(ConnectionConfiguration connectionConfig,
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
				ConnettoreHTTPCORE_connection con = mapConnection.remove(key);
				mapConnection.put("expired_"+key+"_"+UUID.randomUUID().toString(), con);
			}
			ConnettoreHTTPCORE_connection resource = buildAsyncClient(connectionConfig, loader, logger, bf, key, requestInfo);
			mapConnection.put(key, resource);
			return resource;
		}finally {
			semaphore.release("updateConnection",idTransazione);
		}
	}
	private static ConnettoreHTTPCORE_connection buildAsyncClient(ConnectionConfiguration connectionConfig,
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
			requestConfigBuilder.setConnectionRequestTimeout( connectionTimeout );  //  timeout in milliseconds used when requesting a connection from the connection manager.
			requestConfigBuilder.setConnectTimeout( connectionTimeout ); // Determines the timeout in milliseconds until a connection is established.
			
			
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
			requestConfigBuilder.setSocketTimeout( readTimeout ); // // Defines the socket timeout (SO_TIMEOUT) in milliseconds, which is the timeout for waiting for data or, put differently, a maximum period inactivity between two consecutive data packets).
			
			// Proxy
			if(connectionConfig.getProxyHost()!=null && connectionConfig.getProxyPort()!=null) {
				if(bf.length()>0) {
					bf.append("\n");
				}
				bf.append("Proxy: ").append(connectionConfig.getProxyHost()).append(":").append(connectionConfig.getProxyPort());
				requestConfigBuilder.setProxy(new HttpHost(connectionConfig.getProxyHost(), connectionConfig.getProxyPort()));
			}
			
			// Redirect
			//requestConfigBuilder.setRedirectsEnabled(false);
			requestConfigBuilder.setRedirectsEnabled(connectionConfig.isFollowRedirect());
			requestConfigBuilder.setCircularRedirectsAllowed(false); // da file properties
			requestConfigBuilder.setMaxRedirects(connectionConfig.getMaxNumberRedirects());
			
			RequestConfig requestConfig = requestConfigBuilder.build();
			
			// Pool
			//String keyPool = connectionConfig.toString(true);
			// Non utilizzabile poiche' l'opzione 'setConnectionManagerShared' non funziona
			String keyPool = key; // uso medesima chiave della connessione
			if(!ConnettoreHTTPCORE_connectionManager.mapPoolingConnectionManager.containsKey(keyPool)){
				
				SSLContext sslContext = null;
				HostnameVerifier hostnameVerifier = null;
				if(connectionConfig.getSslContextProperties()!=null) {
					if(bf.length()>0) {
						bf.append("\n");
					}
					sslContext = TlsContextBuilder.buildSSLContext(connectionConfig.getSslContextProperties(), logger, bf, requestInfo);
					hostnameVerifier = SSLUtilities.generateHostnameVerifier(connectionConfig.getSslContextProperties(), bf, logger.getLogger(), loader);
				}
				
				ConnettoreHTTPCORE_connectionManager.initialize(keyPool, sslContext, hostnameVerifier);
			}
			PoolingNHttpClientConnectionManager cm = ConnettoreHTTPCORE_connectionManager.mapPoolingConnectionManager.get(keyPool);
			
			HttpAsyncClientBuilder clientBuilder = 
					HttpAsyncClients.custom(); // Qua si gestisce il pipe (ci sono i metodi che gestiscono le richieste una dopo l'altra o prima tutte le richieste e poi le risposte ...)
			clientBuilder.setConnectionManager( cm );
			//clientBuilder.setConnectionManagerShared(true);
			clientBuilder.setDefaultRequestConfig(requestConfig);
				
			clientBuilder.disableAuthCaching();
			
			DefaultClientConnectionReuseStrategy defaultClientConnectionReuseStrategy = new DefaultClientConnectionReuseStrategy();
			clientBuilder.setConnectionReuseStrategy(defaultClientConnectionReuseStrategy);
			
//			ConnectionKeepAliveStrategy keepAliveStrategy = null; // TODO
//			if(keepAliveStrategy!=null){
//				clientBuilder.setKeepAliveStrategy(keepAliveStrategy);
//			}
			
			CloseableHttpAsyncClient httpclient = clientBuilder.build();
			httpclient.start();
			return new ConnettoreHTTPCORE_connection(key, httpclient);
						
		} catch ( Throwable t ) {
			throw new ConnettoreException( t.getMessage(),t );
		}
	}
	
	public static ConnettoreHTTPCORE_connection getConnettoreNIO(ConnectionConfiguration connectionConfig,
			Loader loader, ConnettoreLogger logger, StringBuilder bf,
			RequestInfo requestInfo) throws ConnettoreException  {
		boolean usePoolAsyncClient = true; // e' inutilizzabile senza
		ConnettoreHTTPCORE_connection connection = null;
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
