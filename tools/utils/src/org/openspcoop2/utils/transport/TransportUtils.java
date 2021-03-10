/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
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

	public static boolean containsKey(Map<String, List<String>> p, String name) {
		return _hasKey(p, name);
	}
	@Deprecated
	public static boolean hasKey(Map<String, String> p, String name) {
		return _hasKey(p, name);
	}
	private static boolean _hasKey(Map<String, ?> p, String name) {
		
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
	
	// Usare i metodi sottostanti come getRawObject e getObjectAsString
	@Deprecated
	public static String get(Map<String, String> p, String name) {
		Object o = _properties(p, name, true);
		return (o !=null && o instanceof String) ? ((String)o) : null;
	}
	@Deprecated
	public static Object remove(Map<String, String> p, String name) {
		return _properties(p, name, false);
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
		return (String) _map(map, name, true, true);
	}
	public static String removeObjectAsString(Map<String, ?> map, String name) {
		return (String) _map(map, name, false, true);
	}
	@SuppressWarnings("unchecked")
	public static <T> T getRawObject(Map<String, T> map, String name) {
		return (T) _map(map, name, true, false);
	}
	@SuppressWarnings("unchecked")
	public static <T> T removeRawObject(Map<String, T> map, String name) {
		return (T) _map(map, name, false, false);
	}
	public static void removeObject(Map<String, ?> map, String name) {
		_map(map, name, false, false);
	}
	private static Object _map(Map<String, ?> map, String name, boolean get, boolean returnAsString) {
		
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
		if(!returnAsString) {
			return value; 
		}
		else {
			if(value instanceof String) {
				return (String) value;
			}
			else if(value instanceof List<?>) {
				List<?> l = (List<?>) value;
				String v = convertToSingleValue(l);
				return v;
			}
			else {
				return value.toString();
			}
		}
		
	}
	
	
	
	/* HttpServlet */
	
	@Deprecated
	public static String getParameter(HttpServletRequest request, String name) {
		return getParameterFirstValue(request, name);
	}
	public static String getParameterFirstValue(HttpServletRequest request, String name) {
		List<String> l = getParameterValues(request, name);
		if(l!=null) {
			return l.get(0);
		}
		return null;
	}
	public static List<String> getParameterValues(HttpServletRequest request, String name) {
		// rfc7230#page-22: Each header field consists of a case-insensitive field name followed by a colon (":")
		
		if(request==null) {
			return null;
		}
		if(name==null) {
			return null;
		}
		String exactKeyName = null;
		Enumeration<String> keys = request.getParameterNames();
		if(keys!=null) {
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String keyCaseInsensitive = key.toLowerCase();
				String nameCaseInsensitive = name.toLowerCase();
				if(keyCaseInsensitive.equals(nameCaseInsensitive)) {
					exactKeyName = key;
					break;
				}
			}
		}
		if(exactKeyName==null) {
			return null;
		}
		
		String [] s = request.getParameterValues(exactKeyName);
		List<String> values = new ArrayList<String>();
		if(s!=null && s.length>0) {
			for (int i = 0; i < s.length; i++) {
				String value = s[i];
				values.add(value);
				//System.out("Parameter ["+nomeProperty+"] valore-"+i+" ["+value+"]");
			}
		}
		else {
			//System.out("Parameter ["+nomeProperty+"] valore ["+req.getParameter(nomeProperty)+"]");
			values.add(request.getParameter(exactKeyName));
		}
		
		return values;
		
	}
	
	@Deprecated
	public static String getHeader(HttpServletRequest request, String name) {
		return getHeaderFirstValue(request, name);
	}
	public static String getHeaderFirstValue(HttpServletRequest request, String name) {
		List<String> l = getHeaderValues(request, name);
		if(l!=null) {
			return l.get(0);
		}
		return null;
	}
	public static List<String> getHeaderValues(HttpServletRequest request, String name) {
		
		// rfc7230#page-22: Each header field consists of a case-insensitive field name followed by a colon (":")
		
		if(request==null) {
			return null;
		}
		if(name==null) {
			return null;
		}
		String exactKeyName = null;
		Enumeration<String> keys = request.getHeaderNames();
		if(keys!=null) {
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String keyCaseInsensitive = key.toLowerCase();
				String nameCaseInsensitive = name.toLowerCase();
				if(keyCaseInsensitive.equals(nameCaseInsensitive)) {
					exactKeyName = key;
					break;
				}
			}
		}
		if(exactKeyName==null) {
			return null;
		}
		
		Enumeration<String> enValues = request.getHeaders(exactKeyName);
		List<String> values = new ArrayList<String>();
		if(enValues!=null) {
			@SuppressWarnings("unused")
			int i = 0;
			while (enValues.hasMoreElements()) {
				String value = (String) enValues.nextElement();
				values.add(value);
				//System.out("Header ["+nomeHeader+"] valore-"+i+" ["+value+"]");
				i++;
			}
		}
		if(values.isEmpty()) {
			//System.out("Header ["+nomeHeader+"] valore ["+req.getHeader(nomeHeader)+"]");
			values.add(request.getHeader(exactKeyName));
		}
		
		return values;
		
	}
	
	@Deprecated
	public static String getHeader(HttpServletResponse response, String name) {
		return getHeaderFirstValue(response, name);
	}
	public static String getHeaderFirstValue(HttpServletResponse response, String name) {
		List<String> l = getHeaderValues(response, name);
		if(l!=null) {
			return l.get(0);
		}
		return null;
	}
	public static List<String> getHeaderValues(HttpServletResponse response, String name) {
				
		// rfc7230#page-22: Each header field consists of a case-insensitive field name followed by a colon (":")
		
		if(response==null) {
			return null;
		}
		if(name==null) {
			return null;
		}
		String exactKeyName = null;
		if(response.getHeaderNames()!=null && !response.getHeaderNames().isEmpty()) {
			Iterator<String> keys = response.getHeaderNames().iterator();
			if(keys!=null) {
				while (keys.hasNext()) {
					String key = keys.next();
					String keyCaseInsensitive = key.toLowerCase();
					String nameCaseInsensitive = name.toLowerCase();
					if(keyCaseInsensitive.equals(nameCaseInsensitive)) {
						exactKeyName = key;
					}
				}
			}
		}
		if(exactKeyName==null) {
			return null;
		}
		
		Collection<String> enValues = response.getHeaders(exactKeyName);
		List<String> values = new ArrayList<String>();
		if(enValues!=null && !enValues.isEmpty()) {
			@SuppressWarnings("unused")
			int i = 0;
			for (String value : enValues) {
				values.add(value);
				//System.out("Header ["+nomeHeader+"] valore-"+i+" ["+value+"]");
				i++;
			}
		}
		if(values.isEmpty()) {
			//System.out("Header ["+nomeHeader+"] valore ["+req.getHeader(nomeHeader)+"]");
			values.add(response.getHeader(exactKeyName));
		}
		
		return values;
		
	}
	
	public static String getCookie(HttpServletRequest request, String name) {
		
		// rfc7230#page-22: Each header field consists of a case-insensitive field name followed by a colon (":")
		
		if(request==null) {
			return null;
		}
		if(name==null) {
			return null;
		}
		Cookie[] c = request.getCookies();
		if(c==null || c.length<=0) {
			return null;
		}
		for (Cookie cookie : c) {
			if(cookie!=null && cookie.getName()!=null) {
				if(cookie.getName().equalsIgnoreCase(name)) {
					try {
						return URLDecoder.decode(cookie.getValue(), Charset.UTF_8.getValue());
					}catch(Exception e) {
						throw new RuntimeException(e.getMessage(),e);
					}
				}
			}
		}
		return null;

	}
	
	
	
	
	
	/* Gestione URL */
	
	@Deprecated
	public static String buildLocationWithURLBasedParameter(Map<String, String> propertiesURLBased, String location){
		return buildLocationWithURLBasedParameter(propertiesURLBased, location, false, LoggerWrapperFactory.getLogger(TransportUtils.class));
	}
	@Deprecated
	public static String buildLocationWithURLBasedParameter(Map<String, String> propertiesURLBased, String location, Logger log){
		return buildLocationWithURLBasedParameter(propertiesURLBased, location, false, log);
	}
	@Deprecated
	public static String buildLocationWithURLBasedParameter(Map<String, String> propertiesURLBased, String location, boolean encodeLocation){
		return buildLocationWithURLBasedParameter(propertiesURLBased, location, encodeLocation, LoggerWrapperFactory.getLogger(TransportUtils.class));
	}
	@Deprecated
	public static String buildLocationWithURLBasedParameter(Map<String, String> propertiesURLBased, String location, boolean encodeLocation, Logger log){
		Map<String, List<String>> parameters = convertToMapListValues(propertiesURLBased);
		return buildUrlWithParameters(parameters, location, encodeLocation, log);
	}
	
	public static String buildUrlWithParameters(Map<String, List<String>> parameters, String location){
		return buildUrlWithParameters(parameters, location, false, LoggerWrapperFactory.getLogger(TransportUtils.class));
	}
	public static String buildUrlWithParameters(Map<String, List<String>> parameters, String location, Logger log){
		return buildUrlWithParameters(parameters, location, false, log);
	}
	public static String buildUrlWithParameters(Map<String, List<String>> parameters, String location, boolean encodeLocation){
		return buildUrlWithParameters(parameters, location, encodeLocation, LoggerWrapperFactory.getLogger(TransportUtils.class));
	}
	public static String buildUrlWithParameters(Map<String, List<String>> parameters, String location, boolean encodeLocation, Logger log){
		String locationEncoded = location;
		if(encodeLocation) {
			locationEncoded = UriUtils.encodeQuery(location,Charset.UTF_8.getValue());
		}
		
		if(parameters != null && parameters.size()>0){
			StringBuilder urlBuilder = new StringBuilder(locationEncoded);
			Iterator<String> keys = parameters.keySet().iterator();
			while (keys.hasNext()) {
				
				String key = (String) keys.next();
				List<String> list = parameters.get(key);
				if(list!=null && !list.isEmpty()) {
				
					for (String value : list) {
						
						if(urlBuilder.toString().contains("?")==false)
							urlBuilder.append("?");
						else
							urlBuilder.append("&");
						
						try{
							key = urlEncodeParam(key,Charset.UTF_8.getValue());
						}catch(Exception e){
							if(log!=null) {
								log.error("URLEncode key["+key+"] error: "+e.getMessage(),e);
							}
							else {
								e.printStackTrace(System.out);
							}
						}
						
						try{
							value = urlEncodeParam(value,Charset.UTF_8.getValue());
						}catch(Exception e){
							if(log!=null) {
								log.error("URLEncode value:["+value+"] error: "+e.getMessage(),e);
							}
							else {
								e.printStackTrace(System.out);
							}
						}
						
						String keyValue = key+"="+value;
						urlBuilder.append(keyValue);
						
					}
					
				}
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
	
	
	
	/* ************** UTILITIES *************/
	
	public static String convertToSingleValue(List<?> l) {
		if(l!=null && !l.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Object value : l) {
				if(value==null) {
					continue;
				}
				if(sb.length()>0) {
					sb.append(",");
				}
				sb.append(value);
			}
			if(sb.length()>0) {
				return sb.toString();
			}
			else {
				return null;
			}
		}
		return null;
	}
	
	public static Map<String, List<String>> convertToMapListValues(Map<String, String> mapSingleValue) {
		Map<String, List<String>> mapMultipleValues = null;
		if(mapSingleValue!=null && !mapSingleValue.isEmpty()) {
			mapMultipleValues = new HashMap<String, List<String>>();
			Iterator<String> keys = mapSingleValue.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String value = mapSingleValue.get(key);
				List<String> l = new ArrayList<String>();
				l.add(value);
				mapMultipleValues.put(key, l);
			}
		}
		return mapMultipleValues;
	}
	
	public static Map<String, String> convertToMapSingleValue(Map<String, List<String>> mapMultipleValues) {
		Map<String, String> mapSingleValue = null;
		if(mapMultipleValues!=null && !mapMultipleValues.isEmpty()) {
			mapSingleValue = new HashMap<String, String>();
			Iterator<String> keys = mapMultipleValues.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				List<String> values = mapMultipleValues.get(key);
				String v = convertToSingleValue(values);
				if(v!=null) {
					mapSingleValue.put(key, v);
				}
			}
		}
		return mapSingleValue;
	}
	
	public static void addHeader(Map<String, List<String>> headers, String name, String value) {
		put(headers, name, value, true);
	}
	public static void setHeader(Map<String, List<String>> headers, String name, String value) {
		put(headers, name, value, false);
	}
	
	public static void addParameter(Map<String, List<String>> parameters, String name, String value) {
		put(parameters, name, value, true);
	}
	public static void setParameter(Map<String, List<String>> parameters, String name, String value) {
		put(parameters, name, value, false);
	}
	
	public static void put(Map<String, List<String>> map, String name, String value, boolean add) {
		
		if(value==null) {
			return;
		}
		
		List<String> l = map.get(name);
		if(add) {
			if(l==null) {
				l = new ArrayList<String>();
				map.put(name, l);
			}
		}
		else {
			if(l==null) {
				l = new ArrayList<String>();
				map.put(name, l);
			}
			else {
				l.clear();
			}
		}
		l.add(value);
	}
	
	
	public static String getFirstValue(Map<String, List<String>> map, String key) {
		if(map!=null && !map.isEmpty()) {
			List<String> l = getRawObject(map, key); 
			if(l!=null && !l.isEmpty()) {
				return l.get(0);
			}
		}
		return null;
	}
	public static String getFirstValue(List<String> list) {
		if(list!=null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}
