/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import org.apache.commons.lang.StringUtils;
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
class UrlConnectionConnection implements HttpLibraryConnection {

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
				connection = url.openConnection();
			} else { 
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
				httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
				if(!request.isHostnameVerifier()) {
					SSLHostNameVerifierDisabled disabilitato = new SSLHostNameVerifierDisabled(LoggerWrapperFactory.getLogger(HttpUtilities.class));
					httpsConn.setHostnameVerifier(disabilitato);
				}
			}
			
			// imposto il contentType
			String contentType = request.getContentType();
			if(contentType!=null){
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
			httpConn.setConnectTimeout(request.getConnectTimeout());
			httpConn.setReadTimeout(request.getReadTimeout());
			
			// imposto la gestione dei redirects
			if(request.getFollowRedirects()!=null) {
				httpConn.setInstanceFollowRedirects(request.getFollowRedirects());
			}
			else {
				httpConn.setInstanceFollowRedirects(false);
			}
			
			// autenticazione basic
			if(request.getUsername() != null && request.getPassword() != null){
				String authentication = request.getUsername() + ":" + request.getPassword();
				authentication = HttpConstants.AUTHORIZATION_PREFIX_BASIC + Base64Utilities.encodeAsString(authentication.getBytes());
				httpConn.setRequestProperty(HttpConstants.AUTHORIZATION,authentication);
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
							httpConn.addRequestProperty(key, value);		
						}
					}
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

}
