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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.utils.UtilsException;

/**
 * Identity
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpHeaderTypes {

	private Hashtable<String, String> request_standard = new Hashtable<String, String>();
	private Hashtable<String, String> request_nonStandard = new Hashtable<String, String>();
	
	private Hashtable<String, String> response_standard = new Hashtable<String, String>();
	private Hashtable<String, String> response_nonStandard = new Hashtable<String, String>();
	
	HttpHeaderTypes() throws UtilsException{
		
		InputStream is =null;
		BufferedReader br = null;
		InputStreamReader ir = null;
		try{
			String file = "/org/openspcoop2/utils/resources/httpheader.types";
			is = HttpHeaderTypes.class.getResourceAsStream(file);
			if(is==null){
				throw new Exception("File ["+file+"] in classpath not found");
			}
			
			ir = new InputStreamReader(is);
			br = new BufferedReader(ir);
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if(!line.startsWith("#") && !"".equals(line)){
					String [] tmp = line.split(" "); 
					if(tmp.length<4){
						throw new Exception("Line ["+line+"] format wrong");
					}
					
					String property = tmp[0];
					
					String richiestaRisposta = tmp[1];
					
					String standardNonStandard = tmp[2];
					
					int length = property.length() + 1 + richiestaRisposta.length() + 1 + standardNonStandard.length() + 1;
					String descrizione = line.substring(length);
					
					if("[request]".equalsIgnoreCase(richiestaRisposta)){
						if("[standard]".equalsIgnoreCase(standardNonStandard)){
							this.request_standard.put(property, descrizione);
						}
						else if("[non-standard]".equalsIgnoreCase(standardNonStandard)){
							this.request_nonStandard.put(property, descrizione);
						}
						else{
							throw new Exception("Line ["+line+"] with wrong value ["+standardNonStandard+"] in third parameter (expected: [standard] o [non-standard] )");
						}
					}
					else if("[response]".equalsIgnoreCase(richiestaRisposta)){
						if("[standard]".equalsIgnoreCase(standardNonStandard)){
							this.response_standard.put(property, descrizione);
						}
						else if("[non-standard]".equalsIgnoreCase(standardNonStandard)){
							this.response_nonStandard.put(property, descrizione);
						}
						else{
							throw new Exception("Line ["+line+"] with wrong value ["+standardNonStandard+"] in third parameter (expected: [standard] o [non-standard] )");
						}
					}
					else{
						throw new Exception("Line ["+line+"] with wrong value ["+richiestaRisposta+"] in second parameter (expected: [request] o [response] )");
					}
				}
			}
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}finally{
			try{
				if(br!=null){
					br.close();
				}
			}catch(Exception eClose){}
			try{
				if(ir!=null){
					ir.close();
				}
			}catch(Exception eClose){}
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){}
		}
		
	} 
	
	public List<String> getHeaders(){
		List<String> list = new ArrayList<String>();
		list.addAll(this.getRequestHeaders());
		list.addAll(this.getResponseHeaders());
		return list;
	}
	
	public List<String> getRequestHeaders(){
		List<String> list = new ArrayList<String>();
		list.addAll(this.getRequestStandardHeaders());
		list.addAll(this.getRequestNonStandardHeaders());
		return list;
	}
	public List<String> getRequestStandardHeaders(){
		List<String> list = new ArrayList<String>();
		list.addAll(this.request_standard.keySet());
		return list;
	}
	public List<String> getRequestNonStandardHeaders(){
		List<String> list = new ArrayList<String>();
		list.addAll(this.request_nonStandard.keySet());
		return list;
	}
	
	public List<String> getResponseHeaders(){
		List<String> list = new ArrayList<String>();
		list.addAll(this.getResponseStandardHeaders());
		list.addAll(this.getResponseNonStandardHeaders());
		return list;
	}
	public List<String> getResponseStandardHeaders(){
		List<String> list = new ArrayList<String>();
		list.addAll(this.response_standard.keySet());
		return list;
	}
	public List<String> getResponseNonStandardHeaders(){
		List<String> list = new ArrayList<String>();
		list.addAll(this.response_nonStandard.keySet());
		return list;
	}
	

	
	
	// static
	
	private static HttpHeaderTypes httpHeaderTypes = null;
	private static synchronized void init() throws UtilsException{
		if(httpHeaderTypes==null){
			httpHeaderTypes = new HttpHeaderTypes();
		}
	} 
	public static HttpHeaderTypes getInstance() throws UtilsException{
		if(httpHeaderTypes==null){
			init();
		}
		return httpHeaderTypes;
	}
	
}
