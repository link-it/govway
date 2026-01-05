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

package org.openspcoop2.utils.transport.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;

/**
 * UrlConnectionConnection
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
class UrlConnectionConnection extends HttpLibraryConnection {

	@Override
	public HttpResponse send(HttpRequest request, SSLContext sslContext, OCSPTrustManager ocspTrustManager) throws UtilsException, IOException {
		
		
		if(request.getMethod()==null){
			throw new UtilsException("HttpMethod required");
		}
		
		ByteArrayOutputStream outResponse = null;
		InputStream is = null;
		
		try {
			URLConnection connection = null;
			URL url = URI.create(request.getUrl()).toURL();
			
			
			// istauro la connessione utilizzando un proxy se abilitato
			if(request.getProxyType()==null){
				if(request.isDebug()) {
					request.logInfo("Creazione connessione alla URL ["+request.getUrl()+"]...");
				}
				connection = url.openConnection();
			} else { 
				if(request.isDebug()) {
					request.logInfo("Creazione connessione alla URL ["+request.getUrl()+"] (via proxy "+
							request.getProxyHostname()+":"+request.getProxyPort()+") (username["+request.getProxyUsername()+"] password["+request.getProxyPassword()+"])...");
				}
				if(request.getProxyHostname()==null) {
					throw new UtilsException("Proxy require a hostname");
				}
				Proxy proxy = new Proxy(request.getProxyType(), new InetSocketAddress(request.getProxyHostname(), request.getProxyPort()));
				connection = url.openConnection(proxy);
				
				// Proxy Authentication BASIC
				if(request.getProxyUsername()!=null && request.getProxyPassword()!=null){
					String authentication = request.getProxyUsername() + ":" + request.getProxyPassword();
					authentication = HttpConstants.AUTHORIZATION_PREFIX_BASIC + Base64Utilities.encodeAsString(authentication.getBytes());
					connection.addRequestProperty(HttpConstants.PROXY_AUTHORIZATION,authentication);
				}
			}
			
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			
			// configuro il contesto ssl se abilitato
			if(sslContext!=null) {
				
				HttpsURLConnection httpsConn = (HttpsURLConnection) httpConn;
				
				SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
				if(request.isDebug()) {
					String clientCertificateConfigurated = request.getKeyStorePath();
					sslSocketFactory = new WrappedLogSSLSocketFactory(sslSocketFactory, 
							request.getLog(), "",
							clientCertificateConfigurated);
				}		
				httpsConn.setSSLSocketFactory(sslSocketFactory);
				
				if(!request.isHostnameVerifier()) {
					if(request.isDebug()) {
						request.logInfo("HostName verifier disabilitato");
					}
					SSLHostNameVerifierDisabled disabilitato = new SSLHostNameVerifierDisabled(LoggerWrapperFactory.getLogger(HttpUtilities.class));
					httpsConn.setHostnameVerifier(disabilitato);
				}
				else if(request.isDebug()) {
					request.logInfo("HostName verifier abilitato");
				}
			}
			else {
				if(request.isDebug() && (httpConn instanceof HttpsURLConnection)) { // compatibile compilazione java 11
					HttpsURLConnection httpsConn = (HttpsURLConnection) httpConn;
					if(httpsConn.getSSLSocketFactory()!=null) {
						SSLSocketFactory sslSocketFactory = httpsConn.getSSLSocketFactory();
						String clientCertificateConfigurated = SSLUtilities.getJvmHttpsClientCertificateConfigurated();
						sslSocketFactory = new WrappedLogSSLSocketFactory(sslSocketFactory, 
								request.getLog(), "",
								clientCertificateConfigurated);
						httpsConn.setSSLSocketFactory(sslSocketFactory);
					}
				}
			}
			
			// imposto il contentType
			String contentType = request.getContentType();
			if(contentType!=null){
				if(request.isDebug()) {
	        		request.logInfo("Impostazione Content-Type ["+contentType+"]");
	        	}
				httpConn.setRequestProperty(HttpConstants.CONTENT_TYPE,contentType);
			}
			else if(request.getContent()!=null){
				String ct = request.getHeaderFirstValue(HttpConstants.CONTENT_TYPE);
				if(ct==null || StringUtils.isEmpty(ct)) {
					throw new UtilsException("Content require a Content Type");
				}
				else {
					contentType = ct; // negli header verra impostato sotto
				}
			}
			
			// imposto i timeouts
			if(request.isDebug())
				request.logInfo("Impostazione http timeout CT["+request.getConnectTimeout()+"] RT["+request.getReadTimeout()+"]");
			httpConn.setConnectTimeout(request.getConnectTimeout());
			httpConn.setReadTimeout(request.getReadTimeout());
			
			// imposto la gestione dei redirects
			if(request.getFollowRedirects()!=null) {
				if(request.isDebug()) {
	        		request.logInfo("Redirect strategy abilitato");
	        	}
				httpConn.setInstanceFollowRedirects(request.getFollowRedirects());
			}
			else {
				if(request.isDebug()) {
	        		request.logInfo("Redirect strategy disabilitato");
	        	}
				httpConn.setInstanceFollowRedirects(false);
			}
			
			// autenticazione basic
			if(request.getUsername() != null && request.getPassword() != null){
				String authentication = request.getUsername() + ":" + request.getPassword();
				authentication = HttpConstants.AUTHORIZATION_PREFIX_BASIC + Base64Utilities.encodeAsString(authentication.getBytes());
				if(request.isDebug())
					request.logInfo("Impostazione autenticazione (username:"+request.getUsername()+" password:"+request.getPassword()+") ["+authentication+"]");
				httpConn.setRequestProperty(HttpConstants.AUTHORIZATION,authentication);
			}
			
			// autenticazione bearer
			if(request.getBearerToken()!=null){
				String authorizationHeader = HttpConstants.AUTHORIZATION_PREFIX_BEARER+request.getBearerToken();
				if(request.isDebug())
					request.logInfo("Impostazione autenticazione bearer ["+authorizationHeader+"]");
				httpConn.setRequestProperty(HttpConstants.AUTHORIZATION,authorizationHeader);
			}
			
			// Authentication Api Key
			String apiKey = request.getApiKey();
			if(apiKey!=null && StringUtils.isNotEmpty(apiKey)){
				String apiKeyHeader = request.getApiKeyHeader();
				if(apiKeyHeader==null || StringUtils.isEmpty(apiKeyHeader)) {
					apiKeyHeader = HttpConstants.AUTHORIZATION_HEADER_API_KEY;
				}
				httpConn.setRequestProperty(apiKeyHeader,apiKey);
				if(request.isDebug())
					request.logInfo("Impostazione autenticazione api key ["+apiKeyHeader+"]=["+apiKey+"]");
				
				String appId = request.getAppId();
				if(appId!=null && StringUtils.isNotEmpty(appId)){
					String appIdHeader = request.getAppIdHeader();
					if(appIdHeader==null || StringUtils.isEmpty(appIdHeader)) {
						appIdHeader = HttpConstants.AUTHORIZATION_HEADER_APP_ID;
					}
					httpConn.setRequestProperty(appIdHeader,appId);
					if(request.isDebug())
						request.logInfo("Impostazione autenticazione api key (app id) ["+appIdHeader+"]=["+appId+"]");
				}
			}
			
			// headers
			Map<String, List<String>> requestHeaders = request.getHeadersValues();
			if(requestHeaders!=null && requestHeaders.size()>0){
				Iterator<String> itReq = requestHeaders.keySet().iterator();
				while (itReq.hasNext()) {
					String key = itReq.next();
					List<String> values = requestHeaders.get(key);
					if(values!=null && !values.isEmpty()) {
						for (String value : values) {
							if(request.isDebug()) {
				        		request.logInfo("Aggiungo header ["+key+"]=["+value+"]");
				        	}
							httpConn.addRequestProperty(key, value);		
						}
					}
				}
			}
			
			// Verifica solo della connessione
			if(request.isCheckConnection()) {
				try {
					if(request.isDebug())
						request.logDebug("Connessione in corso ...");
					httpConn.connect();
					if(request.isDebug())
						request.logDebug("Connessione effettuata con successo");
					return null;
				}finally {
					safeDisconnect(httpConn);
				}
			}
			
			// gestione throttling
			boolean sendThrottling = false;
			if(request.getThrottlingSendByte()!=null && request.getThrottlingSendByte()>0 && 
					request.getThrottlingSendMs()!=null && request.getThrottlingSendMs()>0) {
				sendThrottling = true;
			}
			
			if(sendThrottling || request.isForceTransferEncodingChunked()) {
				httpConn.setChunkedStreamingMode(0);
			}
			HttpUtilities.setStream(httpConn, request.getMethod(), contentType);

			HttpBodyParameters httpContent = new  HttpBodyParameters(request.getMethod(), contentType);
			// Spedizione byte
			if(httpContent.isDoOutput() && request.getContent() != null){
				OutputStream out = httpConn.getOutputStream();
				if(sendThrottling) {
					int lengthSendContent = request.getContent().length;
					int length = 0;
					for (int i = 0; i < lengthSendContent; i += length) {
						length = request.getThrottlingSendByte();
						int remaining = lengthSendContent-i;
						if(remaining<length) {
							length = remaining;
						}
						out.write(request.getContent(),i,length);
						out.flush();
						Utilities.sleep(request.getThrottlingSendMs());
					}
				}
				else {
					out.write(request.getContent());
				}
				out.flush();
				out.close();
			}
			else if(httpContent.isDoOutput() && request.getContentStream() != null){
				OutputStream out = httpConn.getOutputStream();
				CopyStream.copy(request.getContentStream(), out);
			}
			
			HttpResponse response = new HttpResponse();
			
			// Ricezione header
			Map<String, List<String>> mapHeaderHttpResponse = httpConn.getHeaderFields();
			if(mapHeaderHttpResponse!=null && mapHeaderHttpResponse.size()>0){
				Iterator<String> itHttpResponse = mapHeaderHttpResponse.keySet().iterator();
				while(itHttpResponse.hasNext()){
					String keyHttpResponse = itHttpResponse.next();
					List<String> valueHttpResponse = mapHeaderHttpResponse.get(keyHttpResponse);
					if(keyHttpResponse==null){ // Check per evitare la coppia che ha come chiave null e come valore HTTP OK 200
						keyHttpResponse=HttpConstants.RETURN_CODE;
					}
					response.addHeader(keyHttpResponse, valueHttpResponse);
				}
			}
			
			// ContentType Risposta
			if(response.getHeadersValues()!=null && !response.getHeadersValues().isEmpty()){
				response.setContentType(response.getHeaderFirstValue(HttpConstants.CONTENT_TYPE));
			}

			// Ricezione Result HTTP Code
			int resultHTTPOperation = httpConn.getResponseCode();

			response.setResultHTTPOperation(resultHTTPOperation);
			
			// Ricezione Risposta
			if(httpContent.isDoInput()){
				outResponse = new ByteArrayOutputStream();
				if(resultHTTPOperation>399){
					is = httpConn.getErrorStream();
					if(is==null){
						is = httpConn.getInputStream();
					}
				}else{
					is = httpConn.getInputStream();
					if(is==null){
						is = httpConn.getErrorStream();
					}
				}
				CopyStream.copy(is, outResponse);
				is.close();
				outResponse.flush();
				outResponse.close();
				response.setContent(outResponse.toByteArray());
			}
				
			// fine HTTP.
			httpConn.disconnect();
	
			// certificati server
			if(ocspTrustManager!=null) {
				response.setServerCertificate(ocspTrustManager.getPeerCertificates());
			}
			
			return response;
			
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new UtilsException(e);
		} finally {
			if (is != null) {
				is.close();
			}
			
			if (outResponse != null) {
				outResponse.close();
			}
			
		}
	}

	private void safeDisconnect(HttpURLConnection httpConn) {
		try {
			httpConn.disconnect();
		}catch(Exception ignore) {
			// ignore
		}
	}
}
