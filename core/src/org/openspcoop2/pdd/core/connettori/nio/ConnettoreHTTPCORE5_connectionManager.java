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

import java.io.IOException;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.reactor.DefaultConnectingIOReactor;
import org.apache.hc.core5.reactor.IOReactor;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.TimeValue;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
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

	static Map<String, IOReactor> mapIoReactor = new HashMap<String, IOReactor>();
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
//				IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
//						//				 .setIoThreadCount(Runtime.getRuntime().availableProcessors())
//										 .build();
//				IOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
//				if(ioReactor instanceof DefaultConnectingIOReactor) {
//					((DefaultConnectingIOReactor) ioReactor).setExceptionHandler(new IOReactorExceptionHandler() {
//						@Override
//						public boolean handle(RuntimeException runtimeException) {
//							OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori().error("[HTTPCore] IOReactor ("+key+") runtimeException: "+runtimeException.getMessage(),runtimeException);
//							return false;
//						}
//						@Override
//						public boolean handle(IOException ioException) {
//							OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori().error("[IOException] IOReactor ("+key+") ioException: "+ioException.getMessage(),ioException);
//							return false;
//						}
//					});
//				}
				
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
		if(ConnettoreHTTPCORE5_connectionManager.mapIoReactor!=null && !ConnettoreHTTPCORE5_connectionManager.mapIoReactor.isEmpty()) {
			
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
			
			// Shut down ioReactor
			if(ConnettoreHTTPCORE5_connectionManager.mapIoReactor!=null && ConnettoreHTTPCORE5_connectionManager.mapIoReactor.isEmpty()) {
				for (String key : ConnettoreHTTPCORE5_connectionManager.mapIoReactor.keySet()) {
					if(key!=null) {
						IOReactor ioReactor = ConnettoreHTTPCORE5_connectionManager.mapIoReactor.get(key);
						if(ioReactor!=null) {
							try {			
								ioReactor.close(CloseMode.GRACEFUL);
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
	}
		
	
	private static void init(ConnectionConfiguration connectionConfig,
			Loader loader, ConnettoreLogger logger, StringBuilder bf) throws ConnettoreException {
		String key = connectionConfig.toString();
		synchronized(mapConnection) {
			if(!mapConnection.containsKey(key)) {
				
				try {				
					ConnettoreHTTPCORE5_connection resource = new ConnettoreHTTPCORE5_connection();
								
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
					
					// Redirect
					requestConfigBuilder.setRedirectsEnabled(false);
	//				requestConfigBuilder.setCircularRedirectsAllowed(false);
	//				requestConfigBuilder.setRedirectsEnabled(redirectsEnabled); // ???
	//				requestConfigBuilder.setMaxRedirects(maxRedirects); // ?? Altre config
					
					RequestConfig requestConfig = requestConfigBuilder.build();
					
					// Pool
					if(!ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager.containsKey(key)){
						
						SSLContext sslContext = null;
						HostnameVerifier hostnameVerifier = null;
						if(connectionConfig.getSslContextProperties()!=null) {
							if(bf.length()>0) {
								bf.append("\n");
							}
							sslContext = SSLUtilities.generateSSLContext(connectionConfig.getSslContextProperties(), bf);
							
							hostnameVerifier = SSLUtilities.generateHostnameVerifier(connectionConfig.getSslContextProperties(), bf, logger.getLogger(), loader);
						}
						
						ConnettoreHTTPCORE5_connectionManager.initialize(key, sslContext, hostnameVerifier);
					}
					PoolingAsyncClientConnectionManager cm = ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager.get(key);
					
					HttpAsyncClientBuilder clientBuilder = 
							HttpAsyncClients.custom(); // Qua si gestisce il pipe (ci sono i metodi che gestiscono le richieste una dopo l'altra o prima tutte le richieste e poi le risposte ...)
					clientBuilder.setConnectionManager( cm );
					//clientBuilder.setConnectionManagerShared(true);
					clientBuilder.setDefaultRequestConfig(requestConfig);
						
					clientBuilder.disableAuthCaching();
					
					//clientBuilder.setProxyAuthenticationStrategy(null);
					
//					DefaultClientConnectionReuseStrateg defaultClientConnectionReuseStrategy = new DefaultClientConnectionReuseStrategy();
//					clientBuilder.setConnectionReuseStrategy(defaultClientConnectionReuseStrategy);
					
//					ConnectionKeepAliveStrategy keepAliveStrategy = null; // TODO
//					if(keepAliveStrategy!=null){
//						clientBuilder.setKeepAliveStrategy(keepAliveStrategy);
//					}
					
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
					
					resource.setHttpclient(httpclient);
					resource.getHttpclient().start();
					
					mapConnection.put(key, resource);
					
				} catch ( Throwable t ) {
					throw new ConnettoreException( t.getMessage(),t );
				}
			}
		}
	}
	
//	private static void reInit(ConnectionConfiguration connectionConfig) {
//		String key = connectionConfig.toString();
//		synchronized(mapConnection) {
//			if(mapConnection.containsKey(key)) {
//				ConnettoreHTTPCORE_connection connection = mapConnection.get(key);
//				if(!connection.getHttpclient().isRunning()) {
//					connection.getHttpclient().start();
//				}
//			}
//		}
//	}
	
	public static ConnettoreHTTPCORE5_connection getConnettoreNIO(ConnectionConfiguration connectionConfig,
			Loader loader, ConnettoreLogger logger, StringBuilder bf) throws ConnettoreException  {
		String key = connectionConfig.toString();
		if(!mapConnection.containsKey(key)) {
			init(connectionConfig, loader, logger, bf);
		}
		ConnettoreHTTPCORE5_connection connection = mapConnection.get(key);
//		if(!connection.getHttpclient().isRunning()) {
//			reInit(connectionConfig);
//		}
		return connection;
	}
	
}
