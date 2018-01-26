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

package org.openspcoop2.protocol.as4.utils;

import java.util.List;

import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * AS4PropertiesUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4PropertiesUtils {

	public static String getRequiredStringValue(List<ProtocolProperty> list, String propertyName) throws ProtocolException{
		String value = getStringValue(list, propertyName, true);
		if(value==null){
			throw new ProtocolException("Property ["+propertyName+"] with null value?");
		}
		return value;
	}
	public static String getOptionalStringValue(List<ProtocolProperty> list, String propertyName) throws ProtocolException{
		String value = getStringValue(list, propertyName, false);
		return value;
	}
	private static String getStringValue(List<ProtocolProperty> list, String propertyName, boolean throwNotFoundException) throws ProtocolException{
		
		ProtocolProperty pp = getProtocolProperty(list, propertyName, throwNotFoundException);
		if(pp!=null){
			return pp.getValue();
		}
		if(throwNotFoundException){
			throw new ProtocolException("Property ["+propertyName+"] not found");
		}
		else{
			return null;
		}
		
	}
	
	public static ProtocolProperty getProtocolProperty(List<ProtocolProperty> list, String propertyName, boolean throwNotFoundException) throws ProtocolException{
		
		if(list==null || list.size()<=0){
			return null;
		}
		for (ProtocolProperty protocolProperty : list) {
			if(propertyName.equals(protocolProperty.getName())){
				return protocolProperty;
			}
		}
		
		if(throwNotFoundException){
			throw new ProtocolException("Property ["+propertyName+"] not found");
		}
		else{
			return null;
		}
	}
	
}
