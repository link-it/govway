/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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


package org.openspcoop2.pdd.core.connettori;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.slf4j.Logger;

/**
 * Classe utilizzata per effettuare consegne di messaggi Soap, attraverso
 * l'invocazione di un server http. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreNIO_connectionManager {

	// ******** STATIC **********

	private static ConnectingIOReactor ioReactor;
	private static PoolingNHttpClientConnectionManager connManager;
	private static IdleConnectionEvictor connEvictor;
	public static synchronized void initialize(Integer defaultMaxPerRoute, Integer maxTotal, Integer closeIdleConnectionsAfterSeconds) throws ConnettoreException {
		
		if(ConnettoreNIO_connectionManager.ioReactor==null) {
			try {
				IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
		//				 .setIoThreadCount(Runtime.getRuntime().availableProcessors())
						 .build();
				ConnettoreNIO_connectionManager.ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
				
				ConnettoreNIO_connectionManager.connManager = new PoolingNHttpClientConnectionManager(ConnettoreNIO_connectionManager.ioReactor);
				ConnectionConfig connectionConfig =
						ConnectionConfig.custom()
//										.setMalformedInputAction(CodingErrorAction.IGNORE)
//										.setUnmappableInputAction(CodingErrorAction.IGNORE)
										.build();
				ConnettoreNIO_connectionManager. connManager.setDefaultConnectionConfig(connectionConfig);
				// PoolingNHttpClientConnectionManager maintains a maximum limit of connection on a per route basis and in total. 
				// Per default this implementation will create no more than than 2 concurrent connections per given route and no more 20 connections in total.
				// For many real-world applications these limits may prove too constraining, especially if they use HTTP as a transport protocol for their services. 
				// Connection limits, however, can be adjusted using ConnPoolControl methods.
				if(defaultMaxPerRoute!=null && defaultMaxPerRoute>0) {
					ConnettoreNIO_connectionManager.connManager.setDefaultMaxPerRoute( defaultMaxPerRoute ); // 2 default
				}
				if(maxTotal!=null && maxTotal>0) {
					ConnettoreNIO_connectionManager.connManager.setMaxTotal( maxTotal ); // 20 default
				}
				
				int closeIdleConnectionsAfterSecondsValue = closeIdleConnectionsAfterSeconds!=null && closeIdleConnectionsAfterSeconds>0 ? closeIdleConnectionsAfterSeconds : 30;
				ConnettoreNIO_connectionManager.connEvictor = new IdleConnectionEvictor(ConnettoreNIO_connectionManager.connManager, closeIdleConnectionsAfterSecondsValue);
				ConnettoreNIO_connectionManager.connEvictor.start();
				
			}catch(Throwable t) {
				throw new ConnettoreException(t.getMessage(),t);
			}
		}
		
	}
	public static synchronized void stop() throws ConnettoreException {
		if(ConnettoreNIO_connectionManager.ioReactor!=null) {
			
			List<Throwable> listT = new ArrayList<Throwable>();
			
			// close client
			if(map!=null && !map.isEmpty()) {
				Iterator<String> it = map.keySet().iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					ConnettoreNIO_connection connection = map.get(key);
					if(connection.getHttpclient().isRunning()) {
						try {
							connection.getHttpclient().close();
						}catch(Throwable t) {
							listT.add(new ConnettoreException("NIO Connection ["+key+"] close error: "+t.getMessage(),t));
						}
					}
				}
			}

			try {
				
	            // Shut down the evictor thread
				ConnettoreNIO_connectionManager.connEvictor.shutdown();
				ConnettoreNIO_connectionManager.connEvictor.join();
				
			}catch(Throwable t) {
				listT.add(t);
			}
			try {			
				// Shut down connManager
				ConnettoreNIO_connectionManager.connManager.shutdown();
			}catch(Throwable t) {
				listT.add(t);
			}
			try {		
				// Shut down ioReactor
				ConnettoreNIO_connectionManager.ioReactor.shutdown();
			}catch(Throwable t) {
				listT.add(t);
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
	
	public static class IdleConnectionEvictor extends Thread {

        private final PoolingNHttpClientConnectionManager connMgr;

        private volatile boolean shutdown;
        private int closeIdleConnectionsAfterSeconds; 

        public IdleConnectionEvictor(PoolingNHttpClientConnectionManager connMgr, int closeIdleConnectionsAfterSeconds) {
            super();
            this.connMgr = connMgr;
            this.closeIdleConnectionsAfterSeconds = closeIdleConnectionsAfterSeconds;
        }

        @Override
        public void run() {
            try {
                while (!this.shutdown) {
                    synchronized (this) {
                        wait(5000);
                        
                        // DEBUG
                        System.out.println("Connessioni attive: "+map.size());
                        Iterator<String> it = map.keySet().iterator();
                        int index = 1;
        				while (it.hasNext()) {
        					String key = (String) it.next();
        					ConnettoreNIO_connection connection = map.get(key);
        					System.out.println("- "+index+" ("+key+"): isRunning: "+connection.getHttpclient().isRunning());
        					index++;
        				}

                        // Close expired connections
                        this.connMgr.closeExpiredConnections();
                        // Optionally, close connections
                        // that have been idle longer than 5 sec
                        this.connMgr.closeIdleConnections(this.closeIdleConnectionsAfterSeconds, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                // terminate
            }
        }

        public void shutdown() {
            this.shutdown = true;
            synchronized (this) {
                notify();
            }
        }

    }

	
	
	private static Map<String, ConnettoreNIO_connection> map = new HashMap<String, ConnettoreNIO_connection>();
	private static void init(ConnettoreNIO_connectionConfig connectionConfig, 
			Loader loader, Logger log, StringBuffer bf) throws ConnettoreException {
		String key = connectionConfig.toString();
		synchronized(map) {
			if(!map.containsKey(key)) {
				
				try {				
					ConnettoreNIO_connection resource = new ConnettoreNIO_connection();
								
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
					requestConfigBuilder.setRedirectsEnabled(false);
	//				requestConfigBuilder.setRedirectsEnabled(redirectsEnabled); // ???
	//				requestConfigBuilder.setMaxRedirects(maxRedirects); // ?? Altre config
					
					RequestConfig requestConfig = requestConfigBuilder.build();
					
					HttpAsyncClientBuilder clientBuilder = 
							HttpAsyncClients.custom(); // Qua si gestisce il pipe (ci sono i metodi che gestiscono le richieste una dopo l'altra o prima tutte le richieste e poi le trisposte ...)
					clientBuilder.setConnectionManager( ConnettoreNIO_connectionManager.connManager );
					clientBuilder.setDefaultRequestConfig(requestConfig);
	
					// SSL
					if(connectionConfig.getSslConfig()!=null) {
						if(bf.length()>0) {
							bf.append("\n");
						}
						clientBuilder.setSSLContext(SSLUtilities.generateSSLContext(connectionConfig.getSslConfig(), bf));
						
						HostnameVerifier hostnameVerifier = SSLUtilities.generateHostnameVerifier(connectionConfig.getSslConfig(), bf, log, loader);
						if(hostnameVerifier!=null){
							clientBuilder.setSSLHostnameVerifier(hostnameVerifier);
						}
					}
					
					clientBuilder.disableAuthCaching();
					resource.setHttpclient(clientBuilder.build());
					resource.getHttpclient().start();
					
					map.put(key, resource);
					
				} catch ( Throwable t ) {
					throw new ConnettoreException( t.getMessage(),t );
				}
			}
		}
	}
	
	private static void reInit(ConnettoreNIO_connectionConfig connectionConfig) {
		String key = connectionConfig.toString();
		synchronized(map) {
			if(map.containsKey(key)) {
				ConnettoreNIO_connection connection = map.get(key);
				if(!connection.getHttpclient().isRunning()) {
					connection.getHttpclient().start();
				}
			}
		}
	}
	
	public static ConnettoreNIO_connection getConnettoreNIO(ConnettoreNIO_connectionConfig connectionConfig,
			Loader loader, Logger log, StringBuffer bf) throws ConnettoreException  {
		String key = connectionConfig.toString();
		if(!map.containsKey(key)) {
			init(connectionConfig, loader, log, bf);
		}
		ConnettoreNIO_connection connection = map.get(key);
		if(!connection.getHttpclient().isRunning()) {
			reInit(connectionConfig);
		}
		return connection;
	}
	
}
