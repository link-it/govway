/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.soap.encoding.soapenc.Base64;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.mime.MimeTypes;


/**
 * Classe che contiene utility per accedere a risorse http 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpUtilities {

	/**
	 * Si occupa di effettuare la connessione verso l'indirizzo specificato dal<var>path</var>.
	 * 
	 *
	 * @param path url del file xml contenente la definizione di un servizio.
	 * @return i byte del file xml situato all'indirizzo <var>path</var>.
	 * 
	 */
	/** TIMEOUT_CONNECTION (2 minuti) */
	public static final int HTTP_CONNECTION_TIMEOUT = 10000; 
	/** TIMEOUT_READ (2 minuti) */
	public static final int HTTP_READ_CONNECTION_TIMEOUT = 120000; 


	public static void setOutputFile(HttpServletResponse response, boolean noCache, String fileName) throws UtilsException{
		
		// setto content-type e header per gestire il download lato client
		String mimeType = null;
		if(fileName.contains(".")){
			String ext = null;
			try{
				ext = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
			}catch(Exception e){}
			MimeTypes mimeTypes = MimeTypes.getInstance();
			if(ext!=null && mimeTypes.existsExtension(ext)){
				mimeType = mimeTypes.getMimeType(ext);
				//System.out.println("CUSTOM ["+mimeType+"]");		
			}
			else{
				mimeType = HttpConstants.CONTENT_TYPE_X_DOWNLOAD;
			}
		}
		else{
			mimeType = HttpConstants.CONTENT_TYPE_X_DOWNLOAD;
		}
		
		setOutputFile(response, noCache, fileName, mimeType);
	}
	
	public static void setOutputFile(HttpServletResponse response, boolean noCache, String fileName, String mimeType) throws UtilsException{
				
		if(mimeType!=null){
			response.setContentType(mimeType);
		}

		response.setHeader(HttpConstants.CONTENT_DISPOSITION, (new StringBuilder()).append(HttpConstants.CONTENT_DISPOSITION_ATTACH_FILE_PREFIX+"\"").append(fileName).append("\"").toString());
		
		// no cache
		if(noCache){
			setNoCache(response);
		}
		
	}
	
	
	
	public static void setNoCache(HttpServletResponse response) throws UtilsException{
		response.setHeader(HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_HTTP_1_1_DISABLE_CACHE); // HTTP 1.1.
		response.setHeader(HttpConstants.CACHE_STATUS_HTTP_1_0, HttpConstants.CACHE_STATUS_HTTP_1_0_DISABLE_CACHE); // HTTP 1.0.
		response.setDateHeader(HttpConstants.CACHE_STATUS_PROXY_EXPIRES, HttpConstants.CACHE_STATUS_PROXY_EXPIRES_DISABLE_CACHE); // Proxies.
	}
	
	
	
	public static void setChunkedStreamingMode(HttpURLConnection httpConn, int chunkLength, HttpRequestMethod httpMethod, String contentType) throws UtilsException{
		
		HttpBodyParameters params = new HttpBodyParameters(httpMethod, contentType);
		
		// Devo impostarlo solo se e' previsto un output
		if(params.isDoOutput()){
			httpConn.setChunkedStreamingMode(chunkLength);
		}
	}
	
	public static boolean isHttpBodyPermitted(boolean isRequest,HttpRequestMethod httpMethod, String contentType) throws UtilsException{
		
		HttpBodyParameters params = new HttpBodyParameters(httpMethod, contentType);
		if(isRequest){
			return params.isDoOutput();
		}
		else{
			return params.isDoInput();
		}
		
	}
	

	public static void setStream(HttpURLConnection httpConn, HttpRequestMethod httpMethod) throws UtilsException{
		setStream(httpConn, httpMethod, null);
	}
	public static void setStream(HttpURLConnection httpConn, HttpRequestMethod httpMethod, String contentType) throws UtilsException{
		try{
			HttpBodyParameters params = new HttpBodyParameters(httpMethod, contentType);
						
			httpConn.setRequestMethod(httpMethod.name());
			if(params.isDoOutput()){
				httpConn.setDoOutput(params.isDoOutput());
			}
			if(params.isDoInput()){
				httpConn.setDoInput(params.isDoInput());
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	} 
	

	/**
	 * Si occupa di effettuare la connessione verso l'indirizzo specificato dal<var>path</var>.
	 * 
	 *
	 * @param path url del file xml contenente la definizione di un servizio.
	 * @return i byte del file xml situato all'indirizzo <var>path</var>.
	 * 
	 */
	public static byte[] requestHTTPFile(String path) throws UtilsException{
		return requestHTTPFile(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null);
	}
	public static byte[] requestHTTPFile(String path,int readTimeout,int connectTimeout) throws UtilsException{
		return requestHTTPFile(path, readTimeout, connectTimeout, null, null);
	}
	public static byte[] requestHTTPFile(String path,String username,String password) throws UtilsException{
		return requestHTTPFile(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password);
	}	
	public static byte[] requestHTTPFile(String path,int readTimeout,int connectTimeout,String username,String password) throws UtilsException{
		HttpResponse res = getHTTPResponse(path, readTimeout, connectTimeout, username, password);
		return res.getContent();
	}
	public static HttpResponse getHTTPResponse(String path) throws UtilsException{
		return getHTTPResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null);
	}
	public static HttpResponse getHTTPResponse(String path,int readTimeout,int connectTimeout) throws UtilsException{
		return getHTTPResponse(path, readTimeout, connectTimeout, null, null);
	}
	public static HttpResponse getHTTPResponse(String path,String username,String password) throws UtilsException{
		return getHTTPResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password);
	}	
	public static HttpResponse getHTTPResponse(String path,int readTimeout,int connectTimeout,String username,String password) throws UtilsException{
		
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(path);
		httpRequest.setReadTimeout(readTimeout);
		httpRequest.setConnectTimeout(connectTimeout);
		httpRequest.setUsername(username);
		httpRequest.setPassword(password);
		httpRequest.setMethod(HttpRequestMethod.GET);
		
		HttpResponse response = null;
		try{
			response = httpInvoke(httpRequest);
			
		}catch(Exception e){
			throw new UtilsException("Utilities.requestHTTPFile error "+e.getMessage(),e);
		}
		if(response.getResultHTTPOperation()==404){
			throw new UtilsException("404");
		}
		return response;
		
	}
	
	
	public static HttpResponse httpInvoke(HttpRequest request) throws UtilsException{
		InputStream is = null;
		ByteArrayOutputStream outResponse = null;
		try{
			if(request.getUrl()==null){
				throw new UtilsException("Url required");
			}
			URL url = new URL(request.getUrl());
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;

			if(request.getContentType()!=null){
				httpConn.setRequestProperty(HttpConstants.CONTENT_TYPE,request.getContentType());
			}
			else if(request.getContent()!=null){
				throw new UtilsException("Content required with ContentType");
			}
			
			httpConn.setConnectTimeout(request.getConnectTimeout());
			httpConn.setReadTimeout(request.getReadTimeout());
			
			if(request.getUsername()!=null && request.getPassword()!=null){
				String authentication = request.getUsername() + ":" + request.getPassword();
				authentication = HttpConstants.AUTHORIZATION_PREFIX_BASIC + 
				Base64.encode(authentication.getBytes());
				httpConn.setRequestProperty(HttpConstants.AUTHORIZATION,authentication);
			}
			
			Map<String, String> requestHeaders = request.getHeaders();
			if(requestHeaders!=null && requestHeaders.size()>0){
				Iterator<String> itReq = requestHeaders.keySet().iterator();
				while (itReq.hasNext()) {
					String key = (String) itReq.next();
					String value = request.getHeader(key);
					httpConn.setRequestProperty(key,value);
				}
			}
			
			if(request.getMethod()==null){
				throw new UtilsException("HttpMethod required");
			}
			setStream(httpConn, request.getMethod());

			HttpBodyParameters httpContent = new  HttpBodyParameters(request.getMethod(), request.getContentType());
			// Spedizione byte
			if(httpContent.isDoOutput() && request.getContent() != null){
				OutputStream out = httpConn.getOutputStream();
				out.write(request.getContent());
				out.flush();
				out.close();
			}
			
			HttpResponse response = new HttpResponse();
			
			// Ricezione header
			Map<String, List<String>> mapHeaderHttpResponse = httpConn.getHeaderFields();
			if(mapHeaderHttpResponse!=null && mapHeaderHttpResponse.size()>0){
				Iterator<String> itHttpResponse = mapHeaderHttpResponse.keySet().iterator();
				while(itHttpResponse.hasNext()){
					String keyHttpResponse = itHttpResponse.next();
					List<String> valueHttpResponse = mapHeaderHttpResponse.get(keyHttpResponse);
					StringBuffer bfHttpResponse = new StringBuffer();
					for(int i=0;i<valueHttpResponse.size();i++){
						if(i>0){
							bfHttpResponse.append(",");
						}
						bfHttpResponse.append(valueHttpResponse.get(i));
					}
					if(keyHttpResponse==null){ // Check per evitare la coppia che ha come chiave null e come valore HTTP OK 200
						keyHttpResponse=HttpConstants.RETURN_CODE;
					}
					response.addHeader(keyHttpResponse, bfHttpResponse.toString());
				}
			}
			
			// ContentType Risposta
			if(response.getHeaders()!=null && response.getHeaders().size()>0){
				response.setContentType(response.getHeader(HttpConstants.CONTENT_TYPE));
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
				byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
				int readByte = 0;
				while((readByte = is.read(readB))!= -1){
					outResponse.write(readB,0,readByte);
				}
				is.close();
				outResponse.flush();
				outResponse.close();
				response.setContent(outResponse.toByteArray());
			}
				
			// fine HTTP.
			httpConn.disconnect();
	
			return response;
		}catch(Exception e){
			try{
				if(is!=null)
					is.close();
			}catch(Exception eis){}
			try{
				if(outResponse!=null)
					outResponse.close();
			}catch(Exception eis){}
			throw new UtilsException(e.getMessage(),e);
		}
	}






	public static void check(String path) throws Exception{
		check(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null);
	}
	public static void check(String path,int readTimeout,int connectTimeout) throws Exception{
		check(path, readTimeout, connectTimeout, null, null);
	}
	public static void check(String path,String username,String password) throws Exception{
		check(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password);
	}
	public static void check(String path,int readTimeout,int connectTimeout,String username,String password) throws Exception{

		InputStream is = null;
		ByteArrayOutputStream outResponse = null;
		HttpURLConnection httpConn = null;
		try{
			URL url = new URL(path);
			URLConnection connection = url.openConnection();
			httpConn = (HttpURLConnection) connection;

			httpConn.setConnectTimeout(connectTimeout);
			httpConn.setReadTimeout(readTimeout);
			
			if(username!=null && password!=null){
				String authentication = username + ":" + password;
				authentication = HttpConstants.AUTHORIZATION_PREFIX_BASIC+ 
				Base64.encode(authentication.getBytes());
				httpConn.setRequestProperty(HttpConstants.AUTHORIZATION,authentication);
			}
			
			setStream(httpConn, HttpRequestMethod.GET);

			int resultHTTPOperation = httpConn.getResponseCode();

			if(resultHTTPOperation!=200){
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
				if(is!=null){
					// Ricezione Risposta
					outResponse = new ByteArrayOutputStream();
					byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
					int readByte = 0;
					while((readByte = is.read(readB))!= -1){
						outResponse.write(readB,0,readByte);
					}
					is.close();
					outResponse.flush();
					outResponse.close();
					throw new Exception("Response Code ("+resultHTTPOperation+"): "+outResponse.toString());
				}
				else{
					throw new Exception("Response Code ("+resultHTTPOperation+")");
				}
			}
		}finally{
			try{
				// fine HTTP.
				if(httpConn!=null)
					httpConn.disconnect();
			}catch(Exception eClose){}
			try{
				if(is!=null)
					is.close();
			}catch(Exception eis){}
			try{
				if(outResponse!=null)
					outResponse.close();
			}catch(Exception eis){}
		}
	}
	
}
