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


package org.openspcoop2.utils.resources;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import org.apache.soap.encoding.soapenc.Base64;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;


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
				mimeType = HEADER_X_DOWNLOAD;
			}
		}
		else{
			mimeType = HEADER_X_DOWNLOAD;
		}
		
		setOutputFile(response, noCache, fileName, mimeType);
	}
	
	public static void setOutputFile(HttpServletResponse response, boolean noCache, String fileName, String mimeType) throws UtilsException{
				
		if(mimeType!=null){
			response.setContentType(mimeType);
		}

		response.setHeader(HEADER_CONTENT_DISPOSITION, (new StringBuilder()).append(HEADER_ATTACH_FILE+"\"").append(fileName).append("\"").toString());
		
		// no cache
		if(noCache){
			setNoCache(response);
		}
		
	}
	
	
	
	public static void setNoCache(HttpServletResponse response) throws UtilsException{
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setDateHeader("Expires", 0); // Proxies.
	}
	
	
	
	public static void setChunkedStreamingMode(HttpURLConnection httpConn, int chunkLength, String httpMethod, String contentType) throws UtilsException{
		
		HttpBodyParameters params = new HttpBodyParameters(httpMethod, contentType);
		
		// Devo impostarlo solo se e' previsto un output
		if(params.isDoOutput()){
			httpConn.setChunkedStreamingMode(chunkLength);
		}
	}
	
	public static boolean isHttpBodyPermitted(boolean isRequest,String httpMethod, String contentType) throws UtilsException{
		
		HttpBodyParameters params = new HttpBodyParameters(httpMethod, contentType);
		if(isRequest){
			return params.isDoOutput();
		}
		else{
			return params.isDoInput();
		}
		
	}
	

	public static void setStream(HttpURLConnection httpConn, String httpMethod) throws UtilsException{
		setStream(httpConn, httpMethod, null);
	}
	public static void setStream(HttpURLConnection httpConn, String httpMethod, String contentType) throws UtilsException{
		try{
			HttpBodyParameters params = new HttpBodyParameters(httpMethod, contentType);
						
			httpConn.setRequestMethod(httpMethod);
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
		HttpResponseBody res = getHTTPResponse(path, readTimeout, connectTimeout, username, password);
		return res.getResponse();
	}
	public static HttpResponseBody getHTTPResponse(String path) throws UtilsException{
		return getHTTPResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null);
	}
	public static HttpResponseBody getHTTPResponse(String path,int readTimeout,int connectTimeout) throws UtilsException{
		return getHTTPResponse(path, readTimeout, connectTimeout, null, null);
	}
	public static HttpResponseBody getHTTPResponse(String path,String username,String password) throws UtilsException{
		return getHTTPResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password);
	}	
	public static HttpResponseBody getHTTPResponse(String path,int readTimeout,int connectTimeout,String username,String password) throws UtilsException{
		InputStream is = null;
		ByteArrayOutputStream outResponse = null;
		try{
			URL url = new URL(path);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;

			httpConn.setConnectTimeout(connectTimeout);
			httpConn.setReadTimeout(readTimeout);
			
			if(username!=null && password!=null){
				String authentication = username + ":" + password;
				authentication = "Basic " + 
				Base64.encode(authentication.getBytes());
				httpConn.setRequestProperty("Authorization",authentication);
			}
			
			setStream(httpConn, "GET");

			int resultHTTPOperation = httpConn.getResponseCode();
			if(resultHTTPOperation==404){
				throw new UtilsException("404");
			}

			// Ricezione Risposta
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
			// fine HTTP.
			httpConn.disconnect();

			byte[] xmlottenuto = outResponse.toByteArray();
			outResponse.close();
			
			HttpResponseBody response = new HttpResponseBody();
			response.setResponse(xmlottenuto);
			response.setResultHTTPOperation(resultHTTPOperation);
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
			if(e.getMessage()!=null && e.getMessage().contains("404"))
				throw new UtilsException("404");
			else
				throw new UtilsException("Utilities.requestHTTPFile error "+e.getMessage(),e);
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
				authentication = "Basic " + 
				Base64.encode(authentication.getBytes());
				httpConn.setRequestProperty("Authorization",authentication);
			}
			
			setStream(httpConn, "GET");

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
