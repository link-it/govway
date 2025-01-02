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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Key;
import java.security.KeyStore;
import java.security.Provider;
import java.security.cert.CertStore;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.CertPathTrustManagerParameters;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.certificate.KeystoreUtils;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.random.RandomGenerator;
import org.openspcoop2.utils.regexp.RegExpUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.TransportUtils;
import org.slf4j.Logger;


/**
 * Classe che contiene utility per accedere a risorse http 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpUtilities {
	
	private HttpUtilities() {}

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

	
	public static List<String> getClientAddressHeaders() {
		// X-Forwarded-For: A de facto standard for identifying the originating IP address of a client connecting to a web server through an HTTP proxy or load balancer
		// Forwarded: Disclose original information of a client connecting to a web server through an HTTP proxy.'
		List<String> possibiliHeaders = new ArrayList<>();
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
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public static String getClientAddressFirstValue(HttpServletRequest request) throws UtilsException{
		return getClientAddress(request);
	}
	private static String getClientAddress(List<String> headers, HttpServletRequest request) throws UtilsException{
		if(!headers.isEmpty()){
			for (String header : headers) {
				List<String> l = TransportUtils.getHeaderValues(request,header);
				if(l!=null && !l.isEmpty()) {
					return l.get(0);
				}
			}
		}
		return null;
	}
	
	@Deprecated
	public static String getClientAddress(Map<String, String> transportProperties) throws UtilsException{
		return getClientAddress(getClientAddressHeaders(), TransportUtils.convertToMapListValues(transportProperties));
	}
	public static String getClientAddressFirstValue(Map<String, List<String>> headers) throws UtilsException{
		return getClientAddress(getClientAddressHeaders(), headers);
	}
	private static String getClientAddress(List<String> headers, Map<String, List<String>> transportProperties) {
		if(!headers.isEmpty()){
			for (String header : headers) {
				List<String> l = TransportUtils.getRawObject(transportProperties,header);
				if(l!=null && !l.isEmpty()) {
					return l.get(0);
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
	
	
	
	public static final String HEADER_X_DOWNLOAD = "application/x-download";
	public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
	public static final String HEADER_ATTACH_FILE = "attachment; filename=";
	
	public static void setOutputFile(HttpServletResponse response, boolean noCache, String fileName) throws UtilsException{
		
		if(fileName==null) {
			throw new UtilsException("Param filename is null");
		}
		
		// setto content-type e header per gestire il download lato client
		String mimeType = null;
		if(fileName.contains(".")){
			String ext = null;
			try{
				ext = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
			}catch(Exception e){
				// ext non disponibile
			}
			MimeTypes mimeTypes = MimeTypes.getInstance();
			if(ext!=null && mimeTypes.existsExtension(ext)){
				mimeType = mimeTypes.getMimeType(ext);
				/**System.out.println("CUSTOM ["+mimeType+"]");*/		
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
				
		if(response==null) {
			throw new UtilsException("Param response is null");
		}
		
		if(mimeType!=null){
			response.setContentType(mimeType);
		}

		response.setHeader(HttpConstants.CONTENT_DISPOSITION, (new StringBuilder()).append(HttpConstants.CONTENT_DISPOSITION_ATTACH_FILE_PREFIX+"\"").append(fileName).append("\"").toString());
		
		// no cache
		if(noCache){
			setNoCache(response);
		}
		
	}
	
	
	
	public static void setNoCache(HttpServletResponse response) {
		response.setHeader(HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_HTTP_1_1_DISABLE_CACHE); // HTTP 1.1.
		response.setHeader(HttpConstants.CACHE_STATUS_HTTP_1_0, HttpConstants.CACHE_STATUS_HTTP_1_0_DISABLE_CACHE); // HTTP 1.0.
		response.setDateHeader(HttpConstants.CACHE_STATUS_PROXY_EXPIRES, HttpConstants.CACHE_STATUS_PROXY_EXPIRES_DISABLE_CACHE); // Proxies.
		response.setHeader(HttpConstants.CACHE_STATUS_VARY, HttpConstants.CACHE_STATUS_VARY_UNCACHABLE);
	}
	
	
	@Deprecated
	public static boolean isNoCache(Map<String, String> headers) throws UtilsException{
		return isDirectiveNoCache(TransportUtils.convertToMapListValues(headers));
	}
	public static boolean isDirectiveNoCache(Map<String, List<String>> headers) throws UtilsException{
		List<String> l = getCacheControlDirectives(headers);
		if(l.isEmpty()) {
			l = getPragmaDirectives(headers);
		}
		if(l.isEmpty()) {
			return false;
		}
		else {
			return l.contains(HttpConstants.CACHE_STATUS_DIRECTIVE_NO_CACHE);
		}
	}
	
	@Deprecated
	public static boolean isNoStore(Map<String, String> headers) throws UtilsException{
		return isDirectiveNoStore(TransportUtils.convertToMapListValues(headers));
	}
	public static boolean isDirectiveNoStore(Map<String, List<String>> headers) throws UtilsException{
		List<String> l = getCacheControlDirectives(headers);
		if(l.isEmpty()) {
			l = getPragmaDirectives(headers);
			if(l.isEmpty()) {
				return false;
			}else{
				return l.contains(HttpConstants.CACHE_STATUS_DIRECTIVE_NO_CACHE); // no cache in pragma vale anche per nostore.
			}
		}
		else {
			return l.contains(HttpConstants.CACHE_STATUS_DIRECTIVE_NO_STORE);
		}
	}
	
	@Deprecated
	public static Integer getCacheMaxAge(Map<String, String> headers) {
		return getDirectiveCacheMaxAge(TransportUtils.convertToMapListValues(headers));
	}
	public static Integer getDirectiveCacheMaxAge(Map<String, List<String>> headers) {
		List<String> l = getCacheControlDirectives(headers);
		if(l.isEmpty()) {
			return null;
		}
		else {
			for (String direttiva : l) {
				if(direttiva!=null && direttiva.startsWith(HttpConstants.CACHE_STATUS_DIRECTIVE_MAX_AGE+"=") && !direttiva.endsWith("=")) {
					try {
						String age = direttiva.substring((HttpConstants.CACHE_STATUS_DIRECTIVE_MAX_AGE+"=").length(), direttiva.length());
						return Integer.valueOf(age);
					}catch(Exception t) {
						// ignore
					}
				}
			}
			return null;
		}
	}
	
	@Deprecated
	public static List<String> getDirectiveCacheControl(Map<String, String> headers){
		return getCacheControlDirectives(TransportUtils.convertToMapListValues(headers));
	}
	public static List<String> getCacheControlDirectives(Map<String, List<String>> headers){
		
		List<String> l = TransportUtils.getRawObject(headers, HttpConstants.CACHE_STATUS_HTTP_1_1);
		String cacheControl = null;
		if(l!=null && !l.isEmpty()) {
			cacheControl = l.get(0);
		}
		
		List<String> values = new ArrayList<>();
		if(cacheControl!=null) {
			if(cacheControl.contains(",")) {
				String [] tmp = cacheControl.split(",");
				for (int i = 0; i < tmp.length; i++) {
					values.add(tmp[i].trim());
				}
			}
			else {
				values.add(cacheControl);
			}
		}
		
		return values;
	}
	
	@Deprecated
	public static List<String> getDirectivePragma(Map<String, String> headers){
		return getPragmaDirectives(TransportUtils.convertToMapListValues(headers));
	}
	public static List<String> getPragmaDirectives(Map<String, List<String>> headers){
		
		List<String> l = TransportUtils.getRawObject(headers, HttpConstants.CACHE_STATUS_HTTP_1_0);
		String cacheControl = null;
		if(l!=null && !l.isEmpty()) {
			cacheControl = l.get(0);
		}
		
		List<String> values = new ArrayList<>();
		if(cacheControl!=null) {
			if(cacheControl.contains(",")) {
				String [] tmp = cacheControl.split(",");
				for (int i = 0; i < tmp.length; i++) {
					values.add(tmp[i].trim());
				}
			}
			else {
				values.add(cacheControl);
			}
		}
		
		return values;
	}
	
	
	
	public static void enableHttpUrlConnectionForwardRestrictedHeaders() {
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
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
						
			setHttpMethod(httpConn, httpMethod);
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
	public static void setHttpMethod(HttpURLConnection httpConn, HttpRequestMethod httpMethod) throws UtilsException {

		// NOTA: comunque l'invio di metodi PATCH,LINK,... funzionano solo con java 8
		
		try {
			if(httpMethod.isStandardMethod()) {
				httpConn.setRequestMethod(httpMethod.name());
			}
			else {
				// Fix errore:
				//java.net.ProtocolException: HTTP method PATCH doesn't support output
				//	at sun.net.www.protocol.http.HttpURLConnection.getOutputStream(HttpURLConnection.java:1081)
				try {
					Object objectModifyMethodField = httpConn;
					
					 Class<?> classHttpsUrlConnectionImpl = getClassHttpsURLConnectionImpl(); // all'interno c'e' un delegate su cui deve essere impostato il metodo http
					
					// Change protected field "method" of public class HttpURLConnection
					// Nella classe  HttpsURLConnectionImpl il field "method" non viene ereditato dalla superclasse ma deve essere modificato nell'oggetto 'delegate'
					if(classHttpsUrlConnectionImpl!=null && httpConn.getClass().isAssignableFrom(classHttpsUrlConnectionImpl)) {
						// Devo gestire il delegate
						/**System.out.println("GESTIONE HTTPS");*/
						objectModifyMethodField = readDelegateHttpsURLConnection(classHttpsUrlConnectionImpl, httpConn);
					}
					
					setProtectedFieldValue(HttpURLConnection.class, "method", objectModifyMethodField, httpMethod.name());
					/**System.out.println("DOPO '"+httpConn.getRequestMethod()+"'");*/
				} catch (Throwable ex) {
					throw new Exception("Unsupported Method '"+httpMethod+"' and set by reflection error: "+ex.getMessage(),ex);
				}
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private static void setProtectedFieldValue(Class<?> clazz, String fieldName, Object object, Object newValue) throws Exception {
				
		/**System.out.println("SET IN '"+object.getClass().getName()+"'");*/
		
		Field field = getField(clazz, fieldName);

		field.setAccessible(true);
		field.set(object, newValue);
		
		/**System.out.println("SET OK IN '"+object.getClass().getName()+"' (field:"+field.getName()+"): "+field.get(object));*/
	}
	private static Field getField(Class<?> clazz, String fieldName) throws UtilsException {
		Field field = null;
		try {
			field = clazz.getDeclaredField(fieldName);
		}catch(Exception e) {
			Field [] f = clazz.getDeclaredFields();
			System.out.println("================= (size:"+f.length+" class:"+clazz.getName()+" fieldName:"+fieldName+") ==========================");
			for (int i = 0; i < f.length; i++) {
				System.out.println("NOME["+f[i].getName()+"] TIPO["+f[i].getType()+"]");
			}
			f = clazz.getFields();
			System.out.println("================= (FIELDS size:"+f.length+" class:"+clazz.getName()+" fieldName:"+fieldName+") ==========================");
			for (int i = 0; i < f.length; i++) {
				System.out.println("NOME["+f[i].getName()+"] TIPO["+f[i].getType()+"]");
			}
			throw new UtilsException(e.getMessage(),e);
		}
		return field;
	}
	private static Class<?> classHttpsURLConnectionImpl = null;
	private static Boolean classHttpsURLConnectionImplRead = null;
	private static synchronized void initClassHttpsURLConnectionImpl() {
		if(classHttpsURLConnectionImplRead==null) {
			try {
				classHttpsURLConnectionImpl = Class.forName("sun.net.www.protocol.https.HttpsURLConnectionImpl");
			}catch(Exception e) {
				System.out.println("Classe 'sun.net.www.protocol.https.HttpsURLConnectionImpl' non esistente: "+e.getMessage());
			} finally {
				classHttpsURLConnectionImplRead = true;
			}
		}
	}
	private static Class<?> getClassHttpsURLConnectionImpl() {
		if(classHttpsURLConnectionImplRead==null) {
			initClassHttpsURLConnectionImpl();
		}
		return classHttpsURLConnectionImpl;
	}
	private static Object readDelegateHttpsURLConnection(Class<?> classHttpsUrlConnectionImpl, HttpURLConnection httpConn) throws IllegalArgumentException, IllegalAccessException, UtilsException {
		Field field = getField(classHttpsUrlConnectionImpl, "delegate");
		field.setAccessible(true);
		Object fieldInstance = field.get(httpConn);
		/**System.out.println("Delegate '"+fieldInstance.getClass().getName()+"'");*/
		return fieldInstance;
	}


	
	
	/* ********* HTTP INVOKE ************ */

	// getHTTPResponse (no options)
	
	public static byte[] requestHTTPFile(String path) throws UtilsException{
		return requestHTTPFile(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null, 
				(HttpOptions[]) null);
	}
	public static byte[] requestHTTPFile(String path,int readTimeout,int connectTimeout) throws UtilsException{
		return requestHTTPFile(path, readTimeout, connectTimeout, null, null, 
				(HttpOptions[]) null);
	}
	public static byte[] requestHTTPFile(String path,String username,String password) throws UtilsException{
		return requestHTTPFile(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password, 
				(HttpOptions[]) null);
	}	
	public static byte[] requestHTTPFile(String path,int readTimeout,int connectTimeout,String username,String password) throws UtilsException{
		HttpResponse res = getHTTPResponse(path, readTimeout, connectTimeout, username, password, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static HttpResponse getHTTPResponse(String path) throws UtilsException{
		return getHTTPResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPResponse(String path,int readTimeout,int connectTimeout) throws UtilsException{
		return getHTTPResponse(path, readTimeout, connectTimeout, null, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPResponse(String path,String username,String password) throws UtilsException{
		return getHTTPResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password, 
				(HttpOptions[]) null);
	}
	
	public static HttpResponse getHTTPResponse(String path,int readTimeout,int connectTimeout,String username,String password) throws UtilsException{
		return getHTTPResponse(path, readTimeout, connectTimeout, username, password, 
				(HttpOptions[]) null);
	}
	
	
	// getHTTPResponse (options)
	
	public static byte[] requestHTTPFile(String path,
			HttpOptions ... options) throws UtilsException{
		return requestHTTPFile(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				options);
	}
	public static byte[] requestHTTPFile(String path,int readTimeout,int connectTimeout,
			HttpOptions ... options) throws UtilsException{
		return requestHTTPFile(path, readTimeout, connectTimeout, null, null,
				options);
	}
	public static byte[] requestHTTPFile(String path,String username,String password,
			HttpOptions ... options) throws UtilsException{
		return requestHTTPFile(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				options);
	}	
	public static byte[] requestHTTPFile(String path,int readTimeout,int connectTimeout,String username,String password,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPResponse(path, readTimeout, connectTimeout, username, password,
				options);
		return res.getContent();
	}
	public static HttpResponse getHTTPResponse(String path,
			HttpOptions ... options) throws UtilsException{
		return getHTTPResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				options);
	}
	public static HttpResponse getHTTPResponse(String path,int readTimeout,int connectTimeout,
			HttpOptions ... options) throws UtilsException{
		return getHTTPResponse(path, readTimeout, connectTimeout, null, null,
				options);
	}
	public static HttpResponse getHTTPResponse(String path,String username,String password,
			HttpOptions ... options) throws UtilsException{
		return getHTTPResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				options);
	}	
	public static HttpResponse getHTTPResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			HttpOptions ... options) throws UtilsException{
		
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(path);
		httpRequest.setReadTimeout(readTimeout);
		httpRequest.setConnectTimeout(connectTimeout);
		httpRequest.setUsername(username);
		httpRequest.setPassword(password);
		httpRequest.setMethod(HttpRequestMethod.GET);
		
		if(options!=null && options.length>0) {
			for (HttpOptions httpOptions : options) {
				httpOptions.fill(httpRequest);
			}
		}
		
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
	
	
	
	
	
	// getHTTPSResponse (no options)
	
	public static byte[] requestHTTPSFile(String path,
			String trustStore, String trustStorePassword, String trustStoreType) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	
	public static byte[] requestHTTPSFile(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}	
	public static byte[] requestHTTPSFile(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}	
	public static byte[] requestHTTPSFile(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}	
	
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	
	public static HttpResponse getHTTPSResponse(String path,
			String trustStore, String trustStorePassword, String trustStoreType) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, ocspValidator, 
				(HttpOptions[]) null);
	}
	
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, ocspValidator, 
				(HttpOptions[]) null);
	}
	
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, ocspValidator, 
				(HttpOptions[]) null);
	}
	
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType) throws UtilsException{
		return getHTTPSResponse(path,readTimeout,connectTimeout,username,password,
				trustStore, trustStorePassword, trustStoreType,
				null, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath) throws UtilsException{
		return getHTTPSResponse(path,readTimeout,connectTimeout,username,password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path,readTimeout,connectTimeout,username,password,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path,readTimeout,connectTimeout,username,password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, ocspValidator, 
				(HttpOptions[]) null);
	}
	
	
	// getHTTPSResponse (options)
	
	public static byte[] requestHTTPSFile(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, ocspValidator,
				options);
		return res.getContent();
	}
	
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, ocspValidator,
				options);
		return res.getContent();
	}
	
	public static byte[] requestHTTPSFile(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null,
				options);
		return res.getContent();
	}	
	public static byte[] requestHTTPSFile(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator,
				options);
		return res.getContent();
	}	
	public static byte[] requestHTTPSFile(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, ocspValidator,
				options);
		return res.getContent();
	}	
	
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator,
				options);
		return res.getContent();
	}
	
	public static HttpResponse getHTTPSResponse(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, ocspValidator,
				options);
	}
	
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, ocspValidator,
				options);
	}
	
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, ocspValidator,
				options);
	}
	
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path,readTimeout,connectTimeout,username,password,
				trustStore, trustStorePassword, trustStoreType,
				null, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path,readTimeout,connectTimeout,username,password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path,readTimeout,connectTimeout,username,password,
				trustStore, trustStorePassword, trustStoreType,
				null, ocspValidator,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(path);
		httpRequest.setReadTimeout(readTimeout);
		httpRequest.setConnectTimeout(connectTimeout);
		httpRequest.setUsername(username);
		httpRequest.setPassword(password);
		httpRequest.setMethod(HttpRequestMethod.GET);
		
		httpRequest.setTrustStorePath(trustStore);
		httpRequest.setTrustStorePassword(trustStorePassword);
		httpRequest.setTrustStoreType(trustStoreType);
		httpRequest.setCrlPath(crlPath);
		httpRequest.setOcspValidator(ocspValidator);
		
		if(options!=null && options.length>0) {
			for (HttpOptions httpOptions : options) {
				httpOptions.fill(httpRequest);
			}
		}
		
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
	
	
	
	// getHTTPSResponse (no options)
	
	public static byte[] requestHTTPSFile(String path,
			KeyStore trustStore) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, null, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,
			KeyStore trustStore, CertStore crlStore) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, crlStore, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,
			KeyStore trustStore, IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, null, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, crlStore, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, null, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore, CertStore crlStore) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, crlStore, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore, IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, null, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, crlStore, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	
	public static byte[] requestHTTPSFile(String path,String username,String password,
			KeyStore trustStore) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, null, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,String username,String password,
			KeyStore trustStore, CertStore crlStore) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, crlStore, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,String username,String password,
			KeyStore trustStore, IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, null, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,String username,String password,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, crlStore, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, null, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore, CertStore crlStore) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, crlStore, null, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore, IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, null, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, crlStore, ocspValidator, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	
	public static HttpResponse getHTTPSResponse(String path,
			KeyStore trustStore) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, null, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,
			KeyStore trustStore, CertStore crlStore) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, crlStore, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,
			KeyStore trustStore, IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, null, ocspValidator, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, crlStore, ocspValidator, 
				(HttpOptions[]) null);
	}
	
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, null, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore, CertStore crlStore) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, crlStore, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore, IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, null, ocspValidator, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, crlStore, ocspValidator, 
				(HttpOptions[]) null);
	}
	
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			KeyStore trustStore) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, null, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			KeyStore trustStore, CertStore crlStore) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, crlStore, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			KeyStore trustStore, IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, null, ocspValidator, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, crlStore, ocspValidator, 
				(HttpOptions[]) null);
	}
	
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore) throws UtilsException{
		return getHTTPSResponse(path,readTimeout,connectTimeout,username,password,
				trustStore, null, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore, CertStore crlStore) throws UtilsException{
		return getHTTPSResponse(path,readTimeout,connectTimeout,username,password,
				trustStore, crlStore, null, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore, IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path,readTimeout,connectTimeout,username,password,
				trustStore, null, ocspValidator, 
				(HttpOptions[]) null);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator) throws UtilsException{
		return getHTTPSResponse(path,readTimeout,connectTimeout,username,password,
				trustStore, crlStore, ocspValidator, 
				(HttpOptions[]) null);
	}
	
	
	// getHTTPSResponse (options)
	
	public static byte[] requestHTTPSFile(String path,
			KeyStore trustStore,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, null, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,
			KeyStore trustStore, CertStore crlStore,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, crlStore, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,
			KeyStore trustStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, null, ocspValidator,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, crlStore, ocspValidator,
				options);
		return res.getContent();
	}
	
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, null, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore, CertStore crlStore,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, crlStore, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, null, ocspValidator,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, crlStore, ocspValidator,
				options);
		return res.getContent();
	}
	
	public static byte[] requestHTTPSFile(String path,String username,String password,
			KeyStore trustStore,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, null, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,String username,String password,
			KeyStore trustStore, CertStore crlStore,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, crlStore, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,String username,String password,
			KeyStore trustStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, null, ocspValidator,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,String username,String password,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, crlStore, ocspValidator,
				options);
		return res.getContent();
	}
	
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, null, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore, CertStore crlStore,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, crlStore, null,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, null, ocspValidator,
				options);
		return res.getContent();
	}
	public static byte[] requestHTTPSFile(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse(path, readTimeout, connectTimeout, username, password,
				trustStore, crlStore, ocspValidator,
				options);
		return res.getContent();
	}
	
	public static HttpResponse getHTTPSResponse(String path,
			KeyStore trustStore,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, null, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,
			KeyStore trustStore, CertStore crlStore,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, crlStore, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,
			KeyStore trustStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, null, ocspValidator,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, crlStore, ocspValidator,
				options);
	}
	
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, null, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore, CertStore crlStore,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, crlStore, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, null, ocspValidator,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, readTimeout, connectTimeout, null, null,
				trustStore, crlStore, ocspValidator,
				options);
	}
	
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			KeyStore trustStore,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, null, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			KeyStore trustStore, CertStore crlStore,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, crlStore, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			KeyStore trustStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, null, ocspValidator,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,String username,String password,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, crlStore, ocspValidator,
				options);
	}
	
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path,readTimeout,connectTimeout,username,password,
				trustStore, null, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore, CertStore crlStore,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path,readTimeout,connectTimeout,username,password,
				trustStore, crlStore, null,
				options);
	}
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse(path,readTimeout,connectTimeout,username,password,
				trustStore, null, ocspValidator,
				options);
	}
		
	public static HttpResponse getHTTPSResponse(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore, CertStore crlStore, IOCSPValidator ocspValidator,
			HttpOptions ... options) throws UtilsException{
		
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(path);
		httpRequest.setReadTimeout(readTimeout);
		httpRequest.setConnectTimeout(connectTimeout);
		httpRequest.setUsername(username);
		httpRequest.setPassword(password);
		httpRequest.setMethod(HttpRequestMethod.GET);
		
		httpRequest.setTrustStore(trustStore);
		httpRequest.setCrlStore(crlStore);
		httpRequest.setOcspValidator(ocspValidator);
		
		if(options!=null && options.length>0) {
			for (HttpOptions httpOptions : options) {
				httpOptions.fill(httpRequest);
			}
		}
		
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
	
	
	
	
	// getHTTPSResponse_trustAllCerts (no options)
	
	public static byte[] requestHTTPSFile_trustAllCerts(String path) throws UtilsException{
		return requestHTTPSFile_trustAllCerts(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null, 
				(HttpOptions[]) null);
	}
	
	public static byte[] requestHTTPSFile_trustAllCerts(String path,int readTimeout,int connectTimeout) throws UtilsException{
		return requestHTTPSFile_trustAllCerts(path, readTimeout, connectTimeout, null, null, 
				(HttpOptions[]) null);
	}
	
	public static byte[] requestHTTPSFile_trustAllCerts(String path,String username,String password) throws UtilsException{
		return requestHTTPSFile_trustAllCerts(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password, 
				(HttpOptions[]) null);
	}

	public static byte[] requestHTTPSFile_trustAllCerts(String path,int readTimeout,int connectTimeout,String username,String password) throws UtilsException{
		HttpResponse res = getHTTPSResponse_trustAllCerts(path, readTimeout, connectTimeout, username, password, 
				(HttpOptions[]) null);
		return res.getContent();
	}
	
	public static HttpResponse getHTTPSResponse_trustAllCerts(String path) throws UtilsException{
		return getHTTPSResponse_trustAllCerts(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null, 
				(HttpOptions[]) null);
	}
	
	public static HttpResponse getHTTPSResponse_trustAllCerts(String path,int readTimeout,int connectTimeout) throws UtilsException{
		return getHTTPSResponse_trustAllCerts(path, readTimeout, connectTimeout, null, null, 
				(HttpOptions[]) null);
	}
	
	public static HttpResponse getHTTPSResponse_trustAllCerts(String path,String username,String password) throws UtilsException{
		return getHTTPSResponse_trustAllCerts(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password, 
				(HttpOptions[]) null);
	}	
	
	public static HttpResponse getHTTPSResponse_trustAllCerts(String path,int readTimeout,int connectTimeout,String username,String password) throws UtilsException{
		return getHTTPSResponse_trustAllCerts(path, readTimeout, connectTimeout, username, password, 
				(HttpOptions[]) null);
	}
	
	
	// getHTTPSResponse_trustAllCerts (options)
	
	public static byte[] requestHTTPSFile_trustAllCerts(String path,
			HttpOptions ... options) throws UtilsException{
		return requestHTTPSFile_trustAllCerts(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				options);
	}
	
	public static byte[] requestHTTPSFile_trustAllCerts(String path,int readTimeout,int connectTimeout,
			HttpOptions ... options) throws UtilsException{
		return requestHTTPSFile_trustAllCerts(path, readTimeout, connectTimeout, null, null,
				options);
	}
	
	public static byte[] requestHTTPSFile_trustAllCerts(String path,String username,String password,
			HttpOptions ... options) throws UtilsException{
		return requestHTTPSFile_trustAllCerts(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				options);
	}

	public static byte[] requestHTTPSFile_trustAllCerts(String path,int readTimeout,int connectTimeout,String username,String password,
			HttpOptions ... options) throws UtilsException{
		HttpResponse res = getHTTPSResponse_trustAllCerts(path, readTimeout, connectTimeout, username, password,
				options);
		return res.getContent();
	}
	
	public static HttpResponse getHTTPSResponse_trustAllCerts(String path,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse_trustAllCerts(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				options);
	}
	
	public static HttpResponse getHTTPSResponse_trustAllCerts(String path,int readTimeout,int connectTimeout,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse_trustAllCerts(path, readTimeout, connectTimeout, null, null,
				options);
	}
	
	public static HttpResponse getHTTPSResponse_trustAllCerts(String path,String username,String password,
			HttpOptions ... options) throws UtilsException{
		return getHTTPSResponse_trustAllCerts(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				options);
	}	
	
	public static HttpResponse getHTTPSResponse_trustAllCerts(String path,int readTimeout,int connectTimeout,String username,String password,
			HttpOptions ... options) throws UtilsException{
		
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(path);
		httpRequest.setReadTimeout(readTimeout);
		httpRequest.setConnectTimeout(connectTimeout);
		httpRequest.setUsername(username);
		httpRequest.setPassword(password);
		httpRequest.setMethod(HttpRequestMethod.GET);
		
		httpRequest.setTrustAllCerts(true);
		
		if(options!=null && options.length>0) {
			for (HttpOptions httpOptions : options) {
				httpOptions.fill(httpRequest);
			}
		}
		
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
	
	private static final String TEST_FILE_ORIGIN = org.openspcoop2.utils.Costanti.PROTOCOL_FILE_PREFIX;
	
	public static HttpResponse httpInvoke(HttpRequest request) throws UtilsException{
		
		String path = request.getUrl();
		if(path!=null && path.startsWith(TEST_FILE_ORIGIN)){
			String filePath = path.substring(TEST_FILE_ORIGIN.length());
			File f = new File(filePath);
			if(!f.exists()){
				throw new UtilsException("404");
			}
			if(!f.canRead()){
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
		InputStream finKeyStore = null;
		InputStream finTrustStore = null;
		try {
			SSLContext sslContext = null;
			OCSPTrustManager ocspTrustManager = null;
			
			if(request.isTrustAllCerts() || request.getTrustStore()!=null || request.getTrustStorePath()!=null){
				
				KeyManager[] km = null;
				TrustManager[] tm = null;
								
				// Autenticazione CLIENT
				if(request.getKeyStore()!=null || request.getKeyStorePath()!=null){
					String location = null;
					try {
						location = request.getKeyStorePath(); // per debug
					
						boolean hsmKeystore = false;
						HSMManager hsmManager = HSMManager.getInstance();
						String hsmType = null;
						if(hsmManager!=null) {
							if(request.getKeyStore()!=null) {
								hsmType = request.getKeyStore().getType();
							}
							else {
								hsmType = request.getKeyStoreType();
							}
							if(hsmType!=null) {
								hsmKeystore = hsmManager.existsKeystoreType(hsmType);
								if(!hsmKeystore) {
									hsmType = null;
								}
							}
						}
						
						KeyStore keystore = null;
						KeyStore keystoreParam = null;
						@SuppressWarnings("unused")
						Provider keystoreProvider = null;
						if(request.getKeyStore()!=null) {
							keystoreParam = request.getKeyStore();
							if(hsmKeystore) {
								keystoreProvider = keystoreParam.getProvider();
							}
						}
						else {
							if(hsmKeystore) {
								org.openspcoop2.utils.certificate.KeyStore ks = hsmManager.getKeystore(hsmType);
								if(ks==null) {
									throw new UtilsException("Keystore not found");
								}
								keystoreParam = ks.getKeystore();
								keystoreProvider = keystoreParam.getProvider();
							}
							else {
								File file = new File(location);
								if(file.exists()) {
									finKeyStore = new FileInputStream(file);
								}
								else {
									finKeyStore = SSLUtilities.class.getResourceAsStream(location);
								}
								if(finKeyStore == null) {
									throw new UtilsException("Keystore not found");
								}
								String tipo = request.getKeyStoreType()!=null ? request.getKeyStoreType() : KeystoreType.JKS.getNome();
								keystoreParam = KeystoreUtils.readKeystore(finKeyStore, tipo, request.getKeyStorePassword());
							}
						}
						
						boolean oldMethodKeyAlias = false; // Questo metodo non funzirequestonava con PKCS11
						if(oldMethodKeyAlias && request.getKeyAlias()!=null) {
							Key key = keystoreParam.getKey(request.getKeyAlias(), request.getKeyPassword().toCharArray());
							if(key==null) {
								throw new UtilsException("Key with alias '"+request.getKeyAlias()+"' not found");
							}
							if(hsmKeystore) {
								// uso un JKS come tmp
								keystore = KeyStore.getInstance(KeystoreType.JKS.getNome());
							}
							else {
								keystore = KeyStore.getInstance(request.getKeyStoreType());
							}
							keystore.load(null); // inizializza il keystore
							keystore.setKeyEntry(request.getKeyAlias(), key, 
									request.getKeyPassword().toCharArray(), keystoreParam.getCertificateChain(request.getKeyAlias()));
						}
						else {
							keystore = keystoreParam;
						}
						
						KeyManagerFactory keyManagerFactory = null;
						// NO: no such algorithm: SunX509 for provider SunPKCS11-xxx
						/**if(keystoreProvider!=null) {
						//	keyManagerFactory = KeyManagerFactory.getInstance(sslConfig.getKeyManagementAlgorithm(), keystoreProvider);
						//}
						//else {*/
						keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
						
						keyManagerFactory.init(keystore, request.getKeyPassword().toCharArray());
						km = keyManagerFactory.getKeyManagers();
						if(!oldMethodKeyAlias && request.getKeyAlias()!=null) {
							if(km!=null && km.length>0 && km[0]!=null && km[0] instanceof X509KeyManager) {
								
								String alias = request.getKeyAlias();
								
								// Fix case insensitive
								Enumeration<String> enAliases = keystore.aliases();
								if(enAliases!=null) {
									while (enAliases.hasMoreElements()) {
										String a = enAliases.nextElement();
										if(a.equalsIgnoreCase(alias)) {
											alias = a; // uso quello presente nel keystore
											break;
										}
									}
								}
								
								X509KeyManager wrapperX509KeyManager = new SSLX509ManagerForcedClientAlias(alias, (X509KeyManager)km[0] );
								km[0] = wrapperX509KeyManager;
							}
						}
					}catch(Throwable e) {
						if(location!=null) {
							throw new UtilsException("["+location+"] "+e.getMessage(),e);
						}
						else {
							throw new UtilsException(e.getMessage(),e);
						}
					}
				}
				
				// Autenticazione Server
				KeyStore truststore = null;
				if(request.isTrustAllCerts()) {
					tm = SSLUtilities.getTrustAllCertsManager();
				}
				else {
					
					boolean hsmTruststore = false;
					HSMManager hsmManager = HSMManager.getInstance();
					if(hsmManager!=null) {
						if(request.getTrustStore()!=null) {
							hsmTruststore = request.isTrustStoreHsm();
						}
						else {
							if(request.getTrustStoreType()!=null && hsmManager.existsKeystoreType(request.getTrustStoreType())) {
								hsmTruststore = true;			
							}
						}
					}
					
					@SuppressWarnings("unused")
					Provider truststoreProvider = null;
					if(request.getTrustStore()!=null ) {
						truststore = request.getTrustStore();
						if(hsmTruststore) {
							truststoreProvider = truststore.getProvider();
						}
					}
					else {
						if(request.getTrustStoreType()==null) {
							throw new UtilsException("Ssl TrustStore type required");
						}
						if(request.getTrustStorePassword()==null) {
							throw new UtilsException("Ssl TrustStore password required");
						}
						if(hsmTruststore) {
							org.openspcoop2.utils.certificate.KeyStore ks = hsmManager.getKeystore(request.getTrustStoreType());
							if(ks==null) {
								throw new UtilsException("Keystore not found");
							}
							truststore = ks.getKeystore();
							truststoreProvider = truststore.getProvider();
						}
						else {
							File file = new File(request.getTrustStorePath());
							if(file.exists()) {
								finTrustStore = new FileInputStream(file);
							}
							else {
								finTrustStore = SSLUtilities.class.getResourceAsStream(request.getTrustStorePath());
							}
							if(finTrustStore == null) {
								throw new UtilsException("Keystore ["+request.getTrustStorePath()+"] not found");
							}
							truststore = KeystoreUtils.readKeystore(finTrustStore, request.getTrustStoreType(), request.getTrustStorePassword());
						}
					}
										
					TrustManagerFactory trustManagerFactory = null;
					// NO: no such algorithm: PKIX for provider SunPKCS11-xxx
					/**if(truststoreProvider!=null) {
					//	trustManagerFactory = TrustManagerFactory.getInstance("PKIX", truststoreProvider);
					//}
					//else {*/
					trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
					
					Provider sigProvider = null;					
					CertPathTrustManagerParameters params = SSLUtilities.buildCertPathTrustManagerParameters(truststore, request.getCrlStore(), request.getCrlPath(), sigProvider);
					trustManagerFactory.init(params);
					tm = trustManagerFactory.getTrustManagers();
				}

				IOCSPValidator ocspValidator = request.getOcspValidator();
				if(truststore!=null && ocspValidator==null && request.getOcspPolicy()!=null && StringUtils.isNotEmpty(request.getOcspPolicy())) {
					Logger log = LoggerWrapperFactory.getLogger(HttpUtilities.class);
					IOCSPValidatorBuilder builder = (IOCSPValidatorBuilder) new Loader().newInstance("org.openspcoop2.utils.certificate.ocsp.OCSPValidatorBuilderImpl");
					ocspValidator = builder.newInstance(log, truststore, request.getCrlPath(), request.getOcspPolicy());
				}
				if(ocspValidator!=null) {
					tm = OCSPTrustManager.wrap(tm, ocspValidator);
					ocspTrustManager = OCSPTrustManager.read(tm);
					ocspValidator.setOCSPTrustManager(ocspTrustManager);
				}

				
				sslContext = SSLContext.getInstance(SSLUtilities.getSafeDefaultProtocol()); // ritorna l'ultima versione disponibile
				
				if(request.isSecureRandom()) {
					RandomGenerator randomGenerator = null;
					if(request.getSecureRandomAlgorithm()!=null && !"".equals(request.getSecureRandomAlgorithm())) {
						randomGenerator = new RandomGenerator(true, request.getSecureRandomAlgorithm());
					}
					else {
						randomGenerator = new RandomGenerator(true);
					}
					java.security.SecureRandom secureRandom = (java.security.SecureRandom) randomGenerator.getRandomEngine();
					sslContext.init(km, tm, secureRandom);
				}
				else {
					sslContext.init(km, tm, null);
				}
			}
			
			if(request.getUrl()==null){
				throw new UtilsException("Url required");
			}
			String connectionUrl = request.getUrl();
			if(request.getForwardProxyEndpoint()!=null && request.getForwardProxyConfig()!=null) {
				Map<String, List<String>> parameters = new HashMap<>();
				if(request.getForwardProxyConfig().getQuery()!=null) {
					if(request.getForwardProxyConfig().isQueryBase64()) {
						String base64Location = Base64Utilities.encodeAsString(connectionUrl.getBytes());
						TransportUtils.addParameter(parameters,request.getForwardProxyConfig().getQuery(), base64Location);
					}
					else {
						TransportUtils.addParameter(parameters,request.getForwardProxyConfig().getQuery(), connectionUrl);
					}
				}
				else if(request.getForwardProxyConfig().getHeader()!=null) {
					if(request.getForwardProxyConfig().isHeaderBase64()) {
						String base64Location = Base64Utilities.encodeAsString(connectionUrl.getBytes());
						request.addHeader(request.getForwardProxyConfig().getHeader(), base64Location);
					}
					else {
						request.addHeader(request.getForwardProxyConfig().getHeader(), connectionUrl);
					}
				}
				boolean encodeBaseLocation = true; // la base location può contenere dei parametri
				connectionUrl = TransportUtils.buildUrlWithParameters(parameters, request.getForwardProxyEndpoint(), encodeBaseLocation, LoggerWrapperFactory.getLogger(HttpUtilities.class));
			}
			URL url = new URI(connectionUrl).toURL();
			URLConnection connection = null;
			if(request.getProxyType()==null){
				connection = url.openConnection();
			}
			else {
				if(request.getProxyHostname()==null) {
					throw new UtilsException("Proxy require a hostname");
				}
				Proxy proxy = new Proxy(request.getProxyType(), new InetSocketAddress(request.getProxyHostname(), request.getProxyPort()));
				connection = url.openConnection(proxy);
				
				// Proxy Authentication BASIC
				if(request.getProxyUsername()!=null && request.getProxyPassword()!=null){
					String authentication = request.getProxyUsername() + ":" + request.getProxyPassword();
					authentication = HttpConstants.AUTHORIZATION_PREFIX_BASIC + Base64Utilities.encodeAsString(authentication.getBytes());
					request.addHeader(HttpConstants.PROXY_AUTHORIZATION,authentication);
				}
			}
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			if(sslContext!=null) {
				
				HttpsURLConnection httpsConn = (HttpsURLConnection) httpConn;
				httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
				if(!request.isHostnameVerifier()) {
					SSLHostNameVerifierDisabled disabilitato = new SSLHostNameVerifierDisabled(LoggerWrapperFactory.getLogger(HttpUtilities.class));
					httpsConn.setHostnameVerifier(disabilitato);
				}
			}

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
				authentication = HttpConstants.AUTHORIZATION_PREFIX_BASIC + Base64Utilities.encodeAsString(authentication.getBytes());
				httpConn.setRequestProperty(HttpConstants.AUTHORIZATION,authentication);
			}
			
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
			
			boolean sendThrottling = false;
			if(request.getThrottlingSendByte()!=null && request.getThrottlingSendByte()>0 && 
					request.getThrottlingSendMs()!=null && request.getThrottlingSendMs()>0) {
				sendThrottling = true;
			}
			
			if(request.getMethod()==null){
				throw new UtilsException("HttpMethod required");
			}
			if(sendThrottling) {
				httpConn.setChunkedStreamingMode(0);
			}
			setStream(httpConn, request.getMethod(), contentType);

			HttpBodyParameters httpContent = new  HttpBodyParameters(request.getMethod(), contentType);
			// Spedizione byte
			if(httpContent.isDoOutput() && request.getContent() != null){
				OutputStream out = httpConn.getOutputStream();
				/**System.out.println("Classe '"+out.getClass().getName()+"'");*/
				if(sendThrottling) {
					int lengthSendContent = request.getContent().length;
					for (int i = 0; i < lengthSendContent; ) {
						int length = request.getThrottlingSendByte();
						int remaining = lengthSendContent-i;
						if(remaining<length) {
							length = remaining;
						}
						out.write(request.getContent(),i,length);
						i = i+length;
						out.flush();
						/**System.out.println("Spediti "+length+" bytes");*/
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
		}catch(Exception e){
			try{
				if(is!=null)
					is.close();
			}catch(Exception eis){
				// ignore
			}
			try{
				if(outResponse!=null)
					outResponse.close();
			}catch(Exception eis){
				// ignore
			}
			throw new UtilsException(e.getMessage(),e);
		}
		finally{
			try{
				if(finKeyStore!=null){
					finKeyStore.close();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(finTrustStore!=null){
					finTrustStore.close();
				}
			}catch(Exception e){
				// close
			}
		}
	}




	
	
	/* ********* CHECK ************ */
	
	// check (no options)
	
	public static void check(String path) throws UtilsException,HttpUtilsException{
		check(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null, 
				(HttpOptions[]) null);
	}
	public static void check(String path,int readTimeout,int connectTimeout) throws UtilsException,HttpUtilsException{
		check(path, readTimeout, connectTimeout, null, null, 
				(HttpOptions[]) null);
	}
	public static void check(String path,String username,String password) throws UtilsException,HttpUtilsException{
		check(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password, 
				(HttpOptions[]) null);
	}
	public static void check(String path,int readTimeout,int connectTimeout,String username,String password) throws UtilsException,HttpUtilsException{
		check(path, readTimeout, connectTimeout, username, password, 
				(HttpOptions[]) null);
	}
	
	// check (options)
	
	public static void check(String path,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		check(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				options);
	}
	public static void check(String path,int readTimeout,int connectTimeout,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		check(path, readTimeout, connectTimeout, null, null,
				options);
	}
	public static void check(String path,String username,String password,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		check(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				options);
	}	
	public static void check(String path,int readTimeout,int connectTimeout,String username,String password,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(path);
		httpRequest.setReadTimeout(readTimeout);
		httpRequest.setConnectTimeout(connectTimeout);
		httpRequest.setUsername(username);
		httpRequest.setPassword(password);
		httpRequest.setMethod(HttpRequestMethod.GET);
		
		if(options!=null && options.length>0) {
			for (HttpOptions httpOptions : options) {
				httpOptions.fill(httpRequest);
			}
		}
		
		check(httpRequest);
		
	}
	
	
	
	
	// checkHTTPS (no options)
	
	public static void checkHTTPS(String path,
			String trustStore, String trustStorePassword, String trustStoreType) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, 
				(HttpOptions[]) null);
	}
	public static void checkHTTPS(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, 
				(HttpOptions[]) null);
	}
	
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null, 
				(HttpOptions[]) null);
	}
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, 
				(HttpOptions[]) null);
	}
	
	public static void checkHTTPS(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null, 
				(HttpOptions[]) null);
	}
	public static void checkHTTPS(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, 
				(HttpOptions[]) null);
	}
	
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType) throws UtilsException,HttpUtilsException{
		checkHTTPS(path,readTimeout,connectTimeout,username,password,
				trustStore, trustStorePassword, trustStoreType,
				null, 
				(HttpOptions[]) null);
	}
	
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath) throws UtilsException,HttpUtilsException{
		checkHTTPS(path,readTimeout,connectTimeout,username,password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath, 
				(HttpOptions[]) null);
	}
	
	
	// checkHTTPS (options)
	
	public static void checkHTTPS(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null,
				options);
	}
	public static void checkHTTPS(String path,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath,
				options);
	}
	
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				null,
				options);
	}
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, readTimeout, connectTimeout, null, null,
				trustStore, trustStorePassword, trustStoreType,
				crlPath,
				options);
	}
	
	public static void checkHTTPS(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				null,
				options);
	}
	public static void checkHTTPS(String path,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, trustStorePassword, trustStoreType,
				crlPath,
				options);
	}
	
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		checkHTTPS(path,readTimeout,connectTimeout,username,password,
				trustStore, trustStorePassword, trustStoreType,
				null,
				options);
	}
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,String username,String password,
			String trustStore, String trustStorePassword, String trustStoreType,
			String crlPath,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(path);
		httpRequest.setReadTimeout(readTimeout);
		httpRequest.setConnectTimeout(connectTimeout);
		httpRequest.setUsername(username);
		httpRequest.setPassword(password);
		httpRequest.setMethod(HttpRequestMethod.GET);
		
		httpRequest.setTrustStorePath(trustStore);
		httpRequest.setTrustStorePassword(trustStorePassword);
		httpRequest.setTrustStoreType(trustStoreType);
		httpRequest.setCrlPath(crlPath);
		
		if(options!=null && options.length>0) {
			for (HttpOptions httpOptions : options) {
				httpOptions.fill(httpRequest);
			}
		}
		
		check(httpRequest);
		
	}
	
	
	
	
	
	// checkHTTPS (no options)
	
	public static void checkHTTPS(String path,
			KeyStore trustStore) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, null, 
				(HttpOptions[]) null);
	}
	public static void checkHTTPS(String path,
			KeyStore trustStore, CertStore crlStore) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, crlStore, 
				(HttpOptions[]) null);
	}
	
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, readTimeout, connectTimeout, null, null,
				trustStore, null, 
				(HttpOptions[]) null);
	}
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore, CertStore crlStore) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, readTimeout, connectTimeout, null, null,
				trustStore, crlStore, 
				(HttpOptions[]) null);
	}
	
	public static void checkHTTPS(String path,String username,String password,
			KeyStore trustStore) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, null, 
				(HttpOptions[]) null);
	}
	public static void checkHTTPS(String path,String username,String password,
			KeyStore trustStore, CertStore crlStore) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, crlStore, 
				(HttpOptions[]) null);
	}	
	
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore) throws UtilsException,HttpUtilsException{
		checkHTTPS(path,readTimeout,connectTimeout,username,password,
				trustStore, null, 
				(HttpOptions[]) null);
	}
	
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore, CertStore crlStore) throws UtilsException,HttpUtilsException{
		checkHTTPS(path,readTimeout,connectTimeout,username,password,
				trustStore, crlStore, 
				(HttpOptions[]) null);
	}
	
	
	
	// checkHTTPS (options)
	
	public static void checkHTTPS(String path,
			KeyStore trustStore,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, null,
				options);
	}
	public static void checkHTTPS(String path,
			KeyStore trustStore, CertStore crlStore,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				trustStore, crlStore,
				options);
	}
	
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, readTimeout, connectTimeout, null, null,
				trustStore, null,
				options);
	}
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,
			KeyStore trustStore, CertStore crlStore,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, readTimeout, connectTimeout, null, null,
				trustStore, crlStore,
				options);
	}
	
	public static void checkHTTPS(String path,String username,String password,
			KeyStore trustStore,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, null,
				options);
	}
	public static void checkHTTPS(String path,String username,String password,
			KeyStore trustStore, CertStore crlStore,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		checkHTTPS(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				trustStore, crlStore,
				options);
	}	
	
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		checkHTTPS(path,readTimeout,connectTimeout,username,password,
				trustStore, null,
				options);
	}
	
	public static void checkHTTPS(String path,int readTimeout,int connectTimeout,String username,String password,
			KeyStore trustStore, CertStore crlStore,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(path);
		httpRequest.setReadTimeout(readTimeout);
		httpRequest.setConnectTimeout(connectTimeout);
		httpRequest.setUsername(username);
		httpRequest.setPassword(password);
		httpRequest.setMethod(HttpRequestMethod.GET);
		
		httpRequest.setTrustStore(trustStore);
		httpRequest.setCrlStore(crlStore);
		
		if(options!=null && options.length>0) {
			for (HttpOptions httpOptions : options) {
				httpOptions.fill(httpRequest);
			}
		}
		
		check(httpRequest);
		
	}
	
	
	
	// checkHTTPS_trustAllCerts (no options)
	
	public static void checkHTTPS_trustAllCerts(String path) throws UtilsException, HttpUtilsException{
		checkHTTPS_trustAllCerts(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null, 
				(HttpOptions[]) null);
	}
	public static void checkHTTPS_trustAllCerts(String path,int readTimeout,int connectTimeout) throws UtilsException, HttpUtilsException{
		checkHTTPS_trustAllCerts(path, readTimeout, connectTimeout, null, null, 
				(HttpOptions[]) null);
	}
	public static void checkHTTPS_trustAllCerts(String path,String username,String password) throws UtilsException, HttpUtilsException{
		checkHTTPS_trustAllCerts(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password, 
				(HttpOptions[]) null);
	}	
	public static void checkHTTPS_trustAllCerts(String path,int readTimeout,int connectTimeout,String username,String password) throws UtilsException,HttpUtilsException{
		checkHTTPS_trustAllCerts(path, readTimeout, connectTimeout, username, password, 
				(HttpOptions[]) null);
	}
	
	// checkHTTPS_trustAllCerts (options)
	
	public static void checkHTTPS_trustAllCerts(String path,
			HttpOptions ... options) throws UtilsException, HttpUtilsException{
		checkHTTPS_trustAllCerts(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, null, null,
				options);
	}
	public static void checkHTTPS_trustAllCerts(String path,int readTimeout,int connectTimeout,
			HttpOptions ... options) throws UtilsException, HttpUtilsException{
		checkHTTPS_trustAllCerts(path, readTimeout, connectTimeout, null, null,
				options);
	}
	public static void checkHTTPS_trustAllCerts(String path,String username,String password,
			HttpOptions ... options) throws UtilsException, HttpUtilsException{
		checkHTTPS_trustAllCerts(path, HTTP_READ_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT, username, password,
				options);
	}	
	public static void checkHTTPS_trustAllCerts(String path,int readTimeout,int connectTimeout,String username,String password,
			HttpOptions ... options) throws UtilsException,HttpUtilsException{
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(path);
		httpRequest.setReadTimeout(readTimeout);
		httpRequest.setConnectTimeout(connectTimeout);
		httpRequest.setUsername(username);
		httpRequest.setPassword(password);
		httpRequest.setMethod(HttpRequestMethod.GET);
		
		httpRequest.setTrustAllCerts(true);
		
		if(options!=null && options.length>0) {
			for (HttpOptions httpOptions : options) {
				httpOptions.fill(httpRequest);
			}
		}
		
		check(httpRequest);
		
	}
	
	public static void check(HttpRequest httpRequest) throws UtilsException,HttpUtilsException{
		
		HttpResponse response = null;
		try{
			response = httpInvoke(httpRequest);
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		
		check(response);
		
	}
	
	public static void check(HttpResponse response) throws HttpUtilsException{
		if(response.getResultHTTPOperation()!=200){
			if(response.getContent()!=null){
				throw new HttpUtilsException(response.getResultHTTPOperation(), "Response Code ("+response.getResultHTTPOperation()+"): "+new String(response.getContent()));
			}
			else{
				throw new HttpUtilsException(response.getResultHTTPOperation(), "Response Code ("+response.getResultHTTPOperation()+")");
			}
		}
	}
	
	
	
	
	
	
	/* ********* VALIDATE URI ************ */
	
	public static void validateUri(String uri,boolean checkEsistenzaFile) throws UtilsException,java.net.MalformedURLException, URISyntaxException{
		
		if (uri.startsWith(org.openspcoop2.utils.Costanti.PROTOCOL_HTTP_PREFIX)
				|| uri.startsWith(TEST_FILE_ORIGIN)) {

			if(checkEsistenzaFile)
				HttpUtilities.requestHTTPFile(uri);
			else
				RegExpUtilities.validateUrl(uri);

		} else {
			File f = new File(uri);
			if(checkEsistenzaFile){
				if(f.exists()==false){
					throw new UtilsException("File non esistente");
				}
				else if(f.isDirectory()){
					throw new UtilsException("File e' una directory");
				}else if(f.canRead()==false){
					throw new UtilsException("File non accessibile");
				}
			}
		}
	}
	
	
	/* ********* DIGEST HEADER ************ */
	
	@Deprecated
	public static String getDigestHeaderValueByCommons(byte[] content, String algorithm) throws UtilsException{
		String digestValue = null;
		if(algorithm.equals(HttpConstants.DIGEST_ALGO_MD5)) {
			digestValue = DigestUtils.md5Hex(content);
		}
		else if(algorithm.equals(HttpConstants.DIGEST_ALGO_SHA_1) || algorithm.equals(HttpConstants.DIGEST_ALGO_SHA)) {
			digestValue = DigestUtils.sha1Hex(content);
		}
		else if(algorithm.equals(HttpConstants.DIGEST_ALGO_SHA_256)) {
			digestValue = DigestUtils.sha256Hex(content);
		}
		else if(algorithm.equals(HttpConstants.DIGEST_ALGO_SHA_384)) {
			digestValue = DigestUtils.sha384Hex(content);
		}
		else if(algorithm.equals(HttpConstants.DIGEST_ALGO_SHA_512)) {
			digestValue = DigestUtils.sha512Hex(content);
		}
		else {
			throw new UtilsException("Digest algorithm '"+algorithm+"' unsupported");
		}
		return algorithm+"="+digestValue;
	}
	
	public static String getDigestHeaderValue(byte[] content, String algorithm) throws UtilsException{
		// RFC 5843, il quale estende il precedente RFC 3230, vengono specificati sia gli algoritmi SHA 256 e 512 che la loro codifica, la quale deve essere base64.
		return getDigestHeaderValue(content, algorithm, DigestEncoding.BASE64);
	}
	public static String getDigestHeaderValue(byte[] content, String algorithm, DigestEncoding digestEncoding) throws UtilsException{
		boolean rfc3230 = true; // aggiunge prefisso algoritmo=
		return org.openspcoop2.utils.digest.DigestUtils.getDigestValue(content, algorithm, digestEncoding, rfc3230);
	}
	public static Map<DigestEncoding, String> getDigestHeaderValues(byte[] content, String algorithm, DigestEncoding ... digestEncoding) throws UtilsException{
		boolean rfc3230 = true; // aggiunge prefisso algoritmo=
		return org.openspcoop2.utils.digest.DigestUtils.getDigestValues(content, algorithm, rfc3230, digestEncoding);
	}
}
