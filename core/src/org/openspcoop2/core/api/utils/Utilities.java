package org.openspcoop2.core.api.utils;

public class Utilities {

	public static String updateLocation(String httpMethod, String location){
		if(httpMethod!=null)
			return location+" api-method:"+httpMethod;
		else
			return location;
	}
	
}
