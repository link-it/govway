package org.openspcoop2.protocol.as4.utils;

import java.util.List;

import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.sdk.ProtocolException;

public class AS4PropertiesUtils {

	public static String getRequiredStringValue(List<ProtocolProperty> list, String propertyName, boolean throwNotFoundException) throws ProtocolException{
		String value = getStringValue(list, propertyName, throwNotFoundException);
		if(value==null){
			throw new ProtocolException("Property ["+propertyName+"] with null value?");
		}
		return value;
	}
	public static String getStringValue(List<ProtocolProperty> list, String propertyName, boolean throwNotFoundException) throws ProtocolException{
		
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
