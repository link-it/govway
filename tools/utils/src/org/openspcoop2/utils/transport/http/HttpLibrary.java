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

import org.openspcoop2.utils.UtilsException;

/**
 * HttpLibrary
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum HttpLibrary {
		
	HTTP_URL_CONNECTION ("java.net.HttpURLConnection"),
	HTTP_CORE5 ("org.apache.hc.client5");	
	
	private final String name;
	private HttpLibrary(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static HttpLibrary fromName(String name) {
		HttpLibrary[] libs = HttpLibrary.values();
		
		for (HttpLibrary lib : libs)
			if (lib.getName().equals(name))
				return lib;
		return null;
	}
	
	public static HttpLibrary getHttpLibrarySafe(String value) {
		try {
			return getHttpLibrary(value);
		}catch(UtilsException e) {
			// ignore
		}
		return null;
	}
	public static HttpLibrary getHttpLibrary(String value) throws UtilsException{
		if(HTTP_URL_CONNECTION.toString().equals(value)){
			return HTTP_URL_CONNECTION;
		}
		else if(HTTP_CORE5.toString().equals(value)){
			return HTTP_CORE5;
		}
		else{
			throw new UtilsException("Unknown type '"+value+"' (supported values: "+HttpLibrary.stringValues()+"): "+value);
		}
	}
	
	public static String stringValues(){
		StringBuilder res = new StringBuilder();
		int i=0;
		for (HttpLibrary tmp : HttpLibrary.values()) {
			if(i>0)
				res.append(",");
			res.append(tmp.getName());
			i++;
		}
		return res.toString();
	}
	
	public static String[] toStringArray(){
		String[] res = new String[HttpLibrary.values().length];
		int i=0;
		for (HttpLibrary tmp : HttpLibrary.values()) {
			res[i]=tmp.getName();
			i++;
		}
		return res;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public boolean equals(String tlm){
		if(tlm==null) {
			return false;
		}
		return this.toString().equals(tlm);
	}
}
