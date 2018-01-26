/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.soap.encoding.soapenc.Base64;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.resources.FileSystemUtilities;


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

	
	public static List<String> getClientAddressHeaders() throws UtilsException{
		// X-Forwarded-For: A de facto standard for identifying the originating IP address of a client connecting to a web server through an HTTP proxy or load balancer
		// Forwarded: Disclose original information of a client connecting to a web server through an HTTP proxy.'
		List<String> possibiliHeaders = new ArrayList<String>();
		possibiliHeaders.add("X-Forwarded-For");
		possibiliHeaders.add("Forwarded-For"); // senza la 'X-' nel caso l'header venga fatto rendere uno standard
		possibiliHeaders.add("X-Forwarded");
		possibiliHeaders.add("Forwarded");
		possibiliHeaders.add("X-Client-IP");
		possibiliHeaders.add("Client-IP");
		possibiliHeaders.add("X-Cluster-Client-IP");
		possibiliHeaders.add("Cluster-Client-IP");
		return possibiliHeaders;
	}
	public static String getClientAddress(HttpServletRequest request) throws UtilsException{
		try{
			return getClientAddress(getClientAddressHeaders(), request);
		}catch(Throwable e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private static String getClientAddress(List<String> headers, HttpServletRequest request) throws UtilsException{
		if(headers.size()>0){
			for (String header : headers) {
				String transportAddr = request.getHeader(header);
				if(transportAddr!=null){
					return transportAddr;
				}
				transportAddr = request.getHeader(header.toLowerCase());
				if(transportAddr!=null){
					return transportAddr;
				}
				transportAddr = request.getHeader(header.toUpperCase());
				if(transportAddr!=null){
					return transportAddr;
				}
			}
		}
		return null;
	}
	public static String getClientAddress(Properties transportProperties) throws UtilsException{
		try{
			return getClientAddress(getClientAddressHeaders(), transportProperties);
		}catch(Throwable e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private static String getClientAddress(List<String> headers, Properties transportProperties) throws UtilsException{
		if(headers.size()>0){
			for (String header : headers) {
				String transportAddr = transportProperties.getProperty(header);
				if(transportAddr!=null){
					return transportAddr;
				}
				transportAddr = transportProperties.getProperty(header.toLowerCase());
				if(transportAddr!=null){
					return transportAddr;
				}
				transportAddr = transportProperties.getProperty(header.toUpperCase());
				if(transportAddr!=null){
					return transportAddr;
				}
			}
		}
		return null;
	}
	
	
	public static String getHttpReason(int status) {
	
		if(status==100) {
			return "Continue"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.2.1]
		}
		else if(status==101) {
			return "Switching Protocols"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.2.2]
		}
		else if(status==102) {
			return "Processing"; // WebDAV - RFC 2518  [RFC2518]
		}
		// 103-199 	Unassigned 	
		
				
		
		else if(status==200) {
			return "OK"; // HTTP/1.0 - RFC 1945  [RFC7231, Section 6.3.1]
		}
		else if(status==201) {
			return "Created"; // HTTP/1.0 - RFC 1945  [RFC7231, Section 6.3.2]
		}
		else if(status==202) {
			return "Accepted"; // HTTP/1.0 - RFC 1945  [RFC7231, Section 6.3.3]
		}
		else if(status==203) {
			return "Non-Authoritative Information"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.3.4]
		}
		else if(status==204) {
			return "No Content"; // HTTP/1.0 - RFC 1945  [RFC7231, Section 6.3.5]
		}
		else if(status==205) {
			return "Reset Content"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.3.6]
		}
		else if(status==206) {
			return "Partial Content"; // HTTP/1.1 - RFC 2616  [RFC7233, Section 4.1]
		}
		else if(status==207) {
			return "Multi-Status"; // (WebDAV - RFC 2518)  [RFC4918] or 207 Partial Update OK (HTTP/1.1 - draft-ietf-http-v11-spec-rev-01?)
		}
		else if(status==208) {
			return "Already Reported"; // WebDAV - RFC 2518  [RFC5842]
		}
		// 209-225 	Unassigned 	
		else if(status==226) {
			return "IM Used"; // [RFC3229]
		}
		// 227-299 	Unassigned 	
		
		
		else if(status==300) {
			return "Multiple Choices"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.4.1]
		}
		else if(status==301) {
			return "Moved Permanently"; // HTTP/1.0 - RFC 1945  [RFC7231, Section 6.4.2]
		}
		else if(status==302) {
			return "Found"; // HTTP/1.0 - RFC 1945  [RFC7231, Section 6.4.3]
		}
		else if(status==303) {
			return "See Other"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.4.4]
		}		
		else if(status==304) {
			return "Not Modified"; // HTTP/1.0 - RFC 1945  [RFC7232, Section 4.1]
		}
		else if(status==305) {
			return "Use Proxy"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.4.5]
		}
		else if(status==306) {
			return "Switch Proxy"; // (Unused)  [RFC7231, Section 6.4.6]
		}
		else if(status==307) {
			return "Temporary Redirect"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.4.7]
		}
		else if(status==308) {
			return "Permanent Redirect"; // [RFC7538]
		}
		// 309-399 	Unassigned 	
		

		else if(status==400) {
			return "Bad Request"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.5.1]
		}
		else if(status==401) {
			return "Unauthorized"; // HTTP/1.0 - RFC 1945  [RFC7235, Section 3.1]
		}
		else if(status==402) {
			return "Payment Required"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.5.2]
		}
		else if(status==403) {
			return "Forbidden"; // HTTP/1.0 - RFC 1945  [RFC7231, Section 6.5.3]
		}
		else if(status==404) {
			return "Not Found"; // HTTP/1.0 - RFC 1945  [RFC7231, Section 6.5.4]
		}
		else if(status==405) {
			return "Method Not Allowed"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.5.5]
		}
		else if(status==406) {
			return "Not Acceptable"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.5.6]
		}
		else if(status==407) {
			return "Proxy Authentication Required"; // HTTP/1.1 - RFC 2616  [RFC7235, Section 3.2]
		}
		else if(status==408) {
			return "Request Timeout"; // [RFC7231, Section 6.5.7]
		}
		else if(status==409) {
			return "Conflict"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.5.8]
		}
		else if(status==410) {
			return "Gone"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.5.9]
		}
		else if(status==411) {
			return "Length Required"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.5.10]
		}
		else if(status==412) {
			return "Precondition Failed"; // HTTP/1.1 - RFC 2616  [RFC7232, Section 4.2][RFC8144, Section 3.2]
		}
		else if(status==413) {
			return "Payload Too Large"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.5.11]
		}
		else if(status==414) {
			return "URI Too Long"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.5.12]
		}
		else if(status==415) {
			return "Unsupported Media Type"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.5.13][RFC7694, Section 3]
		}
		else if(status==416) {
			return "Range Not Satisfiable"; // HTTP/1.1 - RFC 2616  [RFC7233, Section 4.4]
		}
		else if(status==417) {
			return "Expectation Failed"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.5.14]
		}
		// 418 Unassigned
		else if(status==419) {
			return "Authentication Timeout"; // HTTP/1.1 - RFC 2616
		}
		// 420 Unassigned
		else if(status==421) {
			return "Misdirected Request"; // [RFC7540, Section 9.1.2]
		}
		else if(status==422) {
			return "Unprocessable Entity"; // WebDAV - RFC 2518  [RFC4918]
		}
		else if(status==423) {
			return "Locked"; //	(WebDAV - RFC 2518) [RFC4918]
		}
		else if(status==424) {
			return "Failed Dependency"; // WebDAV - RFC 2518 [RFC4918]
		}
		// 425 Unassigned
		else if(status==426) {
			return "Upgrade Required"; // [RFC7231, Section 6.5.15]
		}
		// 427 Unassigned
		else if(status==428) {
			return "Precondition Required"; // [RFC6585]
		}
		else if(status==429) {
			return "Too Many Requests"; // [RFC6585]
		}
		// 430 Unassigned
		else if(status==431) {
			return "Request Header Fields Too Large"; // [RFC6585]
		}
		// 432-450 	Unassigned
		else if(status==451) {
			return "Unavailable For Legal Reasons"; // [RFC7725]
		}
		// 452-499 	Unassigned 	
		

		else if(status==500) {
			return "Internal Server Error"; // HTTP/1.0 - RFC 1945  [RFC7231, Section 6.6.1]
		}
		else if(status==501) {
			return "Not Implemented"; // HTTP/1.0 - RFC 1945  [RFC7231, Section 6.6.2]
		}
		else if(status==502) {
			return "Bad Gateway"; // HTTP/1.0 - RFC 1945  [RFC7231, Section 6.6.3]
		}
		else if(status==503) {
			return "Service Unavailable"; // HTTP/1.0 - RFC 1945  [RFC7231, Section 6.6.4]
		}
		else if(status==504) {
			return "Gateway Timeout"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.6.5]
		}
		else if(status==505) {
			return "HTTP Version Not Supported"; // HTTP/1.1 - RFC 2616  [RFC7231, Section 6.6.6]
		}
		else if(status==506) {
			return "Variant Also Negotiates"; // [RFC2295]
		}
		else if(status==507) {
			return "Insufficient Storage"; // WebDAV - RFC 2518  [RFC4918]
		}
		else if(status==508) {
			return "Loop Detected"; // [RFC5842]
		}
		else if(status==509) {
			return "Bandwidth Limit Exceeded"; // [Apache]
		}
		else if(status==510) {
			return "Not Extended"; // [RFC2774]
		}
		else if(status==511) {
			return "Network Authentication Required"; // [RFC6585]
		}
		// 512-599 	Unassigned 	

		return null;
	}
	
	
	
	public final static String HEADER_X_DOWNLOAD = "application/x-download";
	public final static String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
	public final static String HEADER_ATTACH_FILE = "attachment; filename=";
	
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
		
		String path = request.getUrl();
		if(path!=null && path.startsWith("file://")){
			String filePath = path.substring("file://".length());
			File f = new File(filePath);
			if(f.exists()==false){
				throw new UtilsException("404");
			}
			if(f.canRead()==false){
				throw new UtilsException("404");
			}
			HttpResponse response = new HttpResponse();
			try{
				response.setContent(FileSystemUtilities.readBytesFromFile(f));
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
			response.setResultHTTPOperation(200);
			response.setContentType(MimeTypes.getInstance().getMimeType(f));
			return response;
		}
		
		
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
			
			if(request.getFollowRedirects()!=null) {
				httpConn.setInstanceFollowRedirects(request.getFollowRedirects());
			}
			else {
				httpConn.setInstanceFollowRedirects(false);
			}
			
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
					throw new HttpUtilsException(resultHTTPOperation, "Response Code ("+resultHTTPOperation+"): "+outResponse.toString());
				}
				else{
					throw new HttpUtilsException(resultHTTPOperation, "Response Code ("+resultHTTPOperation+")");
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
