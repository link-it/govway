/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.transport;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Charset;
import org.slf4j.Logger;
import org.springframework.web.util.UriUtils;

/**
 * TransportUtils
 *
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransportUtils {

	/* Gestione CaseInsensitive per Properties */
	
	public static boolean hasKey(Map<String, String> p, String name) {
		
		// rfc7230#page-22: Each header field consists of a case-insensitive field name followed by a colon (":")
		
		if(p==null || p.isEmpty()) {
			return false;
		}
		if(name==null) {
			return false;
		}
		if(p.containsKey(name)) {
			return true;
		}
		if(p.containsKey(name.toLowerCase())) {
			return true;
		}
		if(p.containsKey(name.toUpperCase())) {
			return true;
		}
		Iterator<String> keys = p.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String keyCaseInsensitive = key.toLowerCase();
			String nameCaseInsensitive = name.toLowerCase();
			if(keyCaseInsensitive.equals(nameCaseInsensitive)) {
				return true;
			}
		}
		return false;
		
	}
	
	public static String get(Map<String, String> p, String name) {
		Object o = _properties(p, name, true);
		return (o !=null && o instanceof String) ? ((String)o) : null;
	}
	public static Object remove(Map<String, String> p, String name) {
		return _properties(p, name, false);
	}
	public static <T> T getObject(Map<String, T> p, String name) {
		return _properties(p, name, true);
	}
	private static <T> T _properties(Map<String, T> p, String name, boolean get) {
		
		// rfc7230#page-22: Each header field consists of a case-insensitive field name followed by a colon (":")
		
		if(p==null || p.isEmpty()) {
			return null;
		}
		if(name==null) {
			return null;
		}
		T value = get ? p.get(name) : p.remove(name);
		if(value==null){
			value = get ? p.get(name.toLowerCase()) : p.remove(name.toLowerCase());
		}
		if(value==null){
			value = get ? p.get(name.toUpperCase()) : p.remove(name.toUpperCase()); 
		}
		if(value==null){
			Iterator<String> keys = p.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String keyCaseInsensitive = key.toLowerCase();
				String nameCaseInsensitive = name.toLowerCase();
				if(keyCaseInsensitive.equals(nameCaseInsensitive)) {
					return get ? p.get(key) : p.remove(key);
				}
			}
		}
		return value;
		
	}
	
	
	
	/* Gestione CaseInsensitive per Map */
	
	public static boolean mapHasKey(Map<String, Object> map, String name) {
		
		// rfc7230#page-22: Each header field consists of a case-insensitive field name followed by a colon (":")
		
		if(map==null || map.isEmpty()) {
			return false;
		}
		if(name==null) {
			return false;
		}
		if(map.containsKey(name)) {
			return true;
		}
		if(map.containsKey(name.toLowerCase())) {
			return true;
		}
		if(map.containsKey(name.toUpperCase())) {
			return true;
		}
		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			if(key!=null) {
				String keyCaseInsensitive = key.toLowerCase();
				String nameCaseInsensitive = name.toLowerCase();
				if(keyCaseInsensitive.equals(nameCaseInsensitive)) {
					return true;
				}
			}
		}
		return false;
		
	}
	public static String getObjectAsString(Map<String, ?> map, String name) {
		return _map(map, name, true);
	}
	public static String removeObjectAsString(Map<String, ?> map, String name) {
		return _map(map, name, false);
	}
	public static void removeObject(Map<String, ?> map, String name) {
		_map(map, name, false);
	}
	private static String _map(Map<String, ?> map, String name, boolean get) {
		
		// rfc7230#page-22: Each header field consists of a case-insensitive field name followed by a colon (":")
		
		if(map==null || map.isEmpty()) {
			return null;
		}
		if(name==null) {
			return null;
		}
		Object value = get? map.get(name) : map.remove(name);
		if(value==null){
			value = get? map.get(name.toLowerCase()) : map.remove(name.toLowerCase());
		}
		if(value==null){
			value = get? map.get(name.toUpperCase()) : map.remove(name.toUpperCase());
		}
		if(value==null){
			Iterator<String> keys = map.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				if(key!=null) {
					String keyCaseInsensitive = key.toLowerCase();
					String nameCaseInsensitive = name.toLowerCase();
					if(keyCaseInsensitive.equals(nameCaseInsensitive)) {
						value = get? map.get(key) : map.remove(key);
					}
				}
			}
		}
		if(value==null) {
			return null;
		}
		if(value instanceof String) {
			return (String) value;
		}
		else if(value instanceof List<?>) {
			List<?> l = (List<?>) value;
			StringBuilder bfHttpResponse = new StringBuilder();
			for(int i=0;i<l.size();i++){
				if(i>0){
					bfHttpResponse.append(",");
				}
				bfHttpResponse.append(l.get(i));
			}
			if(bfHttpResponse.length()>0) {
				return bfHttpResponse.toString();
			}
			else {
				return null;
			}
		}
		else {
			return value.toString();
		}
		
	}
	
	
	
	/* HttpServlet */
	
	public static String getParameter(HttpServletRequest request, String name) {
		
		// rfc7230#page-22: Each header field consists of a case-insensitive field name followed by a colon (":")
		
		if(request==null) {
			return null;
		}
		if(name==null) {
			return null;
		}
		String value = request.getParameter(name);
		if(value==null){
			value = request.getParameter(name.toLowerCase());
		}
		if(value==null){
			value = request.getParameter(name.toUpperCase());
		}
		if(value==null){
			Enumeration<String> keys = request.getParameterNames();
			if(keys!=null) {
				while (keys.hasMoreElements()) {
					String key = keys.nextElement();
					String keyCaseInsensitive = key.toLowerCase();
					String nameCaseInsensitive = name.toLowerCase();
					if(keyCaseInsensitive.equals(nameCaseInsensitive)) {
						return request.getParameter(key);
					}
				}
			}
		}
		return value;
		
	}
	
	public static String getHeader(HttpServletRequest request, String name) {
		
		// rfc7230#page-22: Each header field consists of a case-insensitive field name followed by a colon (":")
		
		if(request==null) {
			return null;
		}
		if(name==null) {
			return null;
		}
		String value = request.getHeader(name);
		if(value==null){
			value = request.getHeader(name.toLowerCase());
		}
		if(value==null){
			value = request.getHeader(name.toUpperCase());
		}
		if(value==null){
			Enumeration<String> keys = request.getHeaderNames();
			if(keys!=null) {
				while (keys.hasMoreElements()) {
					String key = keys.nextElement();
					String keyCaseInsensitive = key.toLowerCase();
					String nameCaseInsensitive = name.toLowerCase();
					if(keyCaseInsensitive.equals(nameCaseInsensitive)) {
						return request.getHeader(key);
					}
				}
			}
		}
		return value;
		
	}
	
	public static String getHeader(HttpServletResponse response, String name) {
		
		// rfc7230#page-22: Each header field consists of a case-insensitive field name followed by a colon (":")
		
		if(response==null) {
			return null;
		}
		if(name==null) {
			return null;
		}
		String value = response.getHeader(name);
		if(value==null){
			value = response.getHeader(name.toLowerCase());
		}
		if(value==null){
			value = response.getHeader(name.toUpperCase());
		}
		if(value==null){
			if(response.getHeaderNames()!=null && !response.getHeaderNames().isEmpty()) {
				Iterator<String> keys = response.getHeaderNames().iterator();
				if(keys!=null) {
					while (keys.hasNext()) {
						String key = keys.next();
						String keyCaseInsensitive = key.toLowerCase();
						String nameCaseInsensitive = name.toLowerCase();
						if(keyCaseInsensitive.equals(nameCaseInsensitive)) {
							return response.getHeader(key);
						}
					}
				}
			}
		}
		return value;
		
	}
	
	
	
	
	
	/* Gestione URL */
	
	public static String buildLocationWithURLBasedParameter(Map<String, String> propertiesURLBased, String location){
		return buildLocationWithURLBasedParameter(propertiesURLBased, location, false, LoggerWrapperFactory.getLogger(TransportUtils.class));
	}
	public static String buildLocationWithURLBasedParameter(Map<String, String> propertiesURLBased, String location, Logger log){
		return buildLocationWithURLBasedParameter(propertiesURLBased, location, false, log);
	}
	public static String buildLocationWithURLBasedParameter(Map<String, String> propertiesURLBased, String location, boolean encodeLocation){
		return buildLocationWithURLBasedParameter(propertiesURLBased, location, encodeLocation, LoggerWrapperFactory.getLogger(TransportUtils.class));
	}
	public static String buildLocationWithURLBasedParameter(Map<String, String> propertiesURLBased, String location, boolean encodeLocation, Logger log){
		
		String locationEncoded = location;
		if(encodeLocation) {
			locationEncoded = UriUtils.encodeQuery(location,Charset.UTF_8.getValue());
		}
		
		if(propertiesURLBased != null && propertiesURLBased.size()>0){
			StringBuilder urlBuilder = new StringBuilder(locationEncoded);
			Iterator<String> keys = propertiesURLBased.keySet().iterator();
			while (keys.hasNext()) {
				
				if(urlBuilder.toString().contains("?")==false)
					urlBuilder.append("?");
				else
					urlBuilder.append("&");
				
				String key = (String) keys.next();
				String value = (String) propertiesURLBased.get(key);
				
				try{
					key = urlEncodeParam(key,Charset.UTF_8.getValue());
				}catch(Exception e){
					e.printStackTrace(System.out);
					log.error("URLEncode [key] error: "+e.getMessage(),e);
				}
				
				try{
					value = urlEncodeParam(value,Charset.UTF_8.getValue());
				}catch(Exception e){
					e.printStackTrace(System.out);
					log.error("URLEncode [value] error: "+e.getMessage(),e);
				}
				
				String keyValue = key+"="+value;
				urlBuilder.append(keyValue);
			}
			return urlBuilder.toString();
		}
		else{
			return locationEncoded;
		}
	}

	public static String limitLocation255Character(String location){
		if(location.length()>255){
			return location.substring(0,251)+" ...";
		}
		else{
			return location;
		}
	}
	
	
	public static String urlEncodeParam(String value, String charset) throws Exception {
		
		// Note that Java’s URLEncoder class encodes space character(" ") into a + sign. 
		// This is contrary to other languages like Javascript that encode space character into %20.
		
		/*
		 *  URLEncoder is not for encoding URLs, but for encoding parameter names and values for use in GET-style URLs or POST forms. 
		 *  That is, for transforming plain text into the application/x-www-form-urlencoded MIME format as described in the HTML specification. 
		 **/
				
		//return java.net.URLEncoder.encode(value,"UTF8");
		
		// fix
		String p = UriUtils.encodeQueryParam(value, charset);
		int index = 0;
		while(p.contains("+") && index<1000) {
			p = p.replace("+", "%2B");
			index++;
		}
		return p;
	}
}
