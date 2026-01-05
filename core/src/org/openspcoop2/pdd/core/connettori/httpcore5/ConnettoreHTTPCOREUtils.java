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

package org.openspcoop2.pdd.core.connettori.httpcore5;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpOptions;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpTrace;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.openspcoop2.pdd.core.connettori.AbstractConnettoreConnectionConfig;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreHttpPoolParams;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * ConnettoreHTTPCOREUtils
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCOREUtils {

	private ConnettoreHTTPCOREUtils() {}
	
	public static ConnectionConfig buildConnectionConfig(ConnettoreHttpPoolParams poolParams, long connectionTimeout,
			boolean idleConnectionEvictorEnabled, int sleepTimeSeconds) {
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
		
		if(idleConnectionEvictorEnabled) {
			// Defines the total span of time connections can be kept alive or execute requests
			buider.setTimeToLive(sleepTimeSeconds, TimeUnit.SECONDS);
		}
		
		return buider.build();
	}

	public static void setTimeout(RequestConfig.Builder requestConfigBuilder, AbstractConnettoreConnectionConfig connectionConfig) {
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
	
	public static void setRedirect(RequestConfig.Builder requestConfigBuilder, AbstractConnettoreConnectionConfig connectionConfig) {
		requestConfigBuilder.setRedirectsEnabled(connectionConfig.isFollowRedirect());
		requestConfigBuilder.setCircularRedirectsAllowed(true); // da file properties
		requestConfigBuilder.setMaxRedirects(connectionConfig.getMaxNumberRedirects());
	}
	
	public static HttpUriRequestBase buildHttpRequest(HttpRequestMethod httpMethod, URL url) throws ConnettoreException {
		if(httpMethod==null){
			throw new ConnettoreException("HttpRequestMethod non definito");
		}
		HttpUriRequestBase httpRequest = null;
		switch (httpMethod) {
			case GET:
				httpRequest = new HttpGet(url.toString());
				break;
			case DELETE:
				httpRequest = new HttpDelete(url.toString());
				break;
			case HEAD:
				httpRequest = new HttpHead(url.toString());
				break;
			case POST:
				httpRequest = new HttpPost(url.toString());
				break;
			case PUT:
				httpRequest = new HttpPut(url.toString());
				break;
			case OPTIONS:
				httpRequest = new HttpOptions(url.toString());
				break;
			case TRACE:
				httpRequest = new HttpTrace(url.toString());
				break;
			case PATCH:
				httpRequest = new HttpPatch(url.toString());
				break;	
			default:
				httpRequest = new CustomHttpCoreEntity(httpMethod, url.toString());
				break;
		}
		return httpRequest;
	}
	
}
