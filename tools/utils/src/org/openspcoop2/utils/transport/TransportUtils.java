/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import java.util.Properties;

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

	public static String buildLocationWithURLBasedParameter(Properties propertiesURLBased, String location){
		return buildLocationWithURLBasedParameter(propertiesURLBased, location, false, LoggerWrapperFactory.getLogger(TransportUtils.class));
	}
	public static String buildLocationWithURLBasedParameter(Properties propertiesURLBased, String location, Logger log){
		return buildLocationWithURLBasedParameter(propertiesURLBased, location, false, log);
	}
	public static String buildLocationWithURLBasedParameter(Properties propertiesURLBased, String location, boolean encodeLocation){
		return buildLocationWithURLBasedParameter(propertiesURLBased, location, encodeLocation, LoggerWrapperFactory.getLogger(TransportUtils.class));
	}
	public static String buildLocationWithURLBasedParameter(Properties propertiesURLBased, String location, boolean encodeLocation, Logger log){
		
		String locationEncoded = location;
		if(encodeLocation) {
			locationEncoded = UriUtils.encodeQuery(location,Charset.UTF_8.getValue());
		}
		
		if(propertiesURLBased != null && propertiesURLBased.size()>0){
			StringBuffer urlBuilder = new StringBuffer(locationEncoded);
			Enumeration<?> enumForm = propertiesURLBased.keys();
			while( enumForm.hasMoreElements() ) {
				if(urlBuilder.toString().contains("?")==false)
					urlBuilder.append("?");
				else
					urlBuilder.append("&");
				
				String key = (String) enumForm.nextElement();
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
