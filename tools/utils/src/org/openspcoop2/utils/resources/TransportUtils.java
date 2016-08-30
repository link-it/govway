/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Properties;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

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
		return buildLocationWithURLBasedParameter(propertiesURLBased, location, LoggerWrapperFactory.getLogger(TransportUtils.class));
	}
	public static String buildLocationWithURLBasedParameter(Properties propertiesURLBased, String location, Logger log){
		if(propertiesURLBased != null && propertiesURLBased.size()>0){
			StringBuffer urlBuilder = new StringBuffer(location);
			Enumeration<?> enumForm = propertiesURLBased.keys();
			while( enumForm.hasMoreElements() ) {
				if(urlBuilder.toString().contains("?")==false)
					urlBuilder.append("?");
				else
					urlBuilder.append("&");
				
				String key = (String) enumForm.nextElement();
				String value = (String) propertiesURLBased.get(key);
				
				try{
					key = URLEncoder.encode(key,"UTF8");
				}catch(Exception e){
					e.printStackTrace(System.out);
					log.error("URLEncode [key] error: "+e.getMessage(),e);
				}
				
				try{
					value = URLEncoder.encode(value,"UTF8");
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
			return location;
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
	
}
