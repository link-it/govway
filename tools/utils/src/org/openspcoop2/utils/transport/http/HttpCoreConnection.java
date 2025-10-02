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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.util.Timeout;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;


/**
 * HttpCoreConnection
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
class HttpCoreConnection implements HttpLibraryConnection {

	
	private HttpHost setupProxy(HttpClientBuilder builder, Map<String, List<String>> addHeaders, HttpRequest request) {
		if (request.getProxyType() == null || request.getProxyHostname() == null)
			return null;
		
		HttpHost proxy = new HttpHost(request.getProxyHostname(), request.getProxyPort());
        builder.setProxy(proxy);
        
        // Proxy Authentication BASIC
		if(request.getProxyUsername() != null && request.getProxyPassword() != null){
			String authentication = request.getProxyUsername() + ":" + request.getProxyPassword();
			authentication = HttpConstants.AUTHORIZATION_PREFIX_BASIC + Base64Utilities.encodeAsString(authentication.getBytes());
			addHeaders.put(HttpConstants.PROXY_AUTHORIZATION, List.of(authentication));
		}
		
		return proxy;
	}
	
	private void enableRedirect(HttpClientBuilder builder, HttpRequest request) {
		if (Boolean.TRUE.equals(request.getFollowRedirects())) {
        	DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        	builder.setRedirectStrategy(redirectStrategy);
        } else {
        	builder.disableRedirectHandling();
        }
	}
	
	private void setupSSL(HttpClientBuilder builder, HttpRequest request, SSLContext sslContext) {
		// Set SSL context if provided
        if (sslContext != null) {
        	// 1. Create the socket factory
        	TlsSocketStrategy strategy = ClientTlsStrategyBuilder.create()
        			.setHostnameVerifier(request.isHostnameVerifier() ? new DefaultHostnameVerifier() : NoopHostnameVerifier.INSTANCE)
        			.setSslContext(sslContext)
        			.buildClassic();

        	// 2. Build the connection manager
        	PoolingHttpClientConnectionManager connManager = PoolingHttpClientConnectionManagerBuilder.create()
        	    .setTlsSocketStrategy(strategy)
        	    .build();
        	
        	builder.setConnectionManager(connManager);
        }
	}
	
	private void addHeader(HttpUriRequestBase httpRequest, Map<String, List<String>> overrideHeaders, HttpRequest request) {
		for (Map.Entry<String, List<String>> entry : overrideHeaders.entrySet()) {
        	for (String value : entry.getValue()) {
                httpRequest.addHeader(entry.getKey(), value);
            }
        }
        
        for (Map.Entry<String, List<String>> entry : request.getHeadersValues().entrySet()) {
            if (overrideHeaders.containsKey(entry.getKey()))
            	continue;
        	for (String value : entry.getValue()) {
                httpRequest.addHeader(entry.getKey(), value);
            }
        }
        
        // Content-Type
        if (request.getContentType() != null) {
            httpRequest.setHeader(HttpHeaders.CONTENT_TYPE, request.getContentType());
        }

        // Auth - Basic
        if (request.getUsername() != null && request.getPassword() != null) {
            String creds = request.getUsername() + ":" + request.getPassword();
            String encoded = Base64.getEncoder().encodeToString(creds.getBytes(StandardCharsets.UTF_8));
            httpRequest.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoded);
        }
	}
	
	private void setupTimeouts(RequestConfig.Builder builder, HttpRequest request) {
		builder.setConnectionRequestTimeout(Timeout.ofMilliseconds(request.getConnectTimeout()))
        	.setResponseTimeout(Timeout.ofMilliseconds(request.getReadTimeout()));
	}
	
	private void setupMaxRedirect(RequestConfig.Builder builder, HttpRequest request) {
		builder.setMaxRedirects(request.getMaxHopRedirects());
	}
	
	@Override
	public HttpResponse send(HttpRequest request, SSLContext sslContext, OCSPTrustManager ocspTrustManager) throws UtilsException, IOException {
	    
		
		if(request.getMethod()==null){
			throw new UtilsException("HttpMethod required");
		}
		
		try {
	        // builder per la creazione del client
	        HttpClientBuilder builder = HttpClients.custom();
	        Map<String, List<String>> overrideHeaders = new HashMap<>();
	        
	        // abilito o disabilito i redirect
	        enableRedirect(builder, request);
	        
	        // aggiungo il proxy se necessario
	        HttpHost proxy = setupProxy(builder, overrideHeaders, request);

	        // imposto la gestione SSL
	        setupSSL(builder, request, sslContext);

	        // creo la richiesta
	        HttpUriRequestBase httpRequest = new HttpUriRequestBase(
	                request.getMethod().name(), URI.create(request.getUrl()));

	        // aggiungo gli headers
	        addHeader(httpRequest, overrideHeaders, request);

	        // imposto i timeout e il massimo di redirect
	        RequestConfig.Builder configBuilder = RequestConfig.custom();
	        setupMaxRedirect(configBuilder, request);
	        setupTimeouts(configBuilder, request);
	        
	        httpRequest.setConfig(configBuilder.build());

	        // gestione del body
	        InputStream contentStream = null;
	        if (request.getContent() != null && request.getContent().length > 0) {
	        	contentStream = new ByteArrayInputStream(request.getContent());
	        } else if (request.getContentStream() != null) {
	        	contentStream = request.getContentStream();
	        }
	        
	        if (contentStream != null) {
	            // chunked or throttled stream
	            Integer contentLength = request.getContent().length;
	            
	            if (request.getThrottlingSendByte() != null && request.getThrottlingSendMs() != null) {
	            	contentStream = new ChunkedInputStream(contentStream, request.getThrottlingSendByte(), request.getThrottlingSendMs());
	                contentLength = -1;
	            }
	            
	            if (request.isForceTransferEncodingChunked())
	            	contentLength = -1;
	         
	            InputStreamEntity entity = new InputStreamEntity(
	            		contentStream, contentLength, ContentType.parse(request.getContentType()));
	            httpRequest.setEntity(entity);
	        }

	        CloseableHttpClient client = builder.build();
	        ClassicHttpResponse httpResp = client.executeOpen(proxy, httpRequest, HttpClientContext.create());

	        // === Build response ===
	        HttpResponse response = new HttpResponse();
	        response.setResultHTTPOperation(httpResp.getCode());

	        Header[] responseHeaders = httpResp.getHeaders();
	        for (Header h : responseHeaders) {
	            response.addHeader(h.getName(), List.of(h.getValue()));
	        }

	        HttpEntity entity = httpResp.getEntity();
	        if (entity != null) {
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            entity.writeTo(out);
	            response.setContent(out.toByteArray());
	            response.setContentType(entity.getContentType());
	        }

	        client.close();
	        
	        // per mantenere la retro compatibilità con urlConnection
	        String returnCode = httpResp.getVersion() + " " + httpResp.getCode() + " " + httpResp.getReasonPhrase();
	        response.addHeader("ReturnCode", List.of(returnCode));
	        
	        // certificati server
	        if(ocspTrustManager!=null) {
	     		response.setServerCertificate(ocspTrustManager.getPeerCertificates());
	     	}
	     			
	        return response;
	    } catch (IOException e) {
	    	throw e;
	    } catch (Exception e) {
	        throw new UtilsException(e);
	    }
	}
	
	

}
