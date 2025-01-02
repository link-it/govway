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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.UtilsException;

/**
 * HttpHeaderTypes
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpHeaderTypes {

	private Map<String, String> requestStandard = new HashMap<>();
	private Map<String, String> requestNonStandard = new HashMap<>();
	
	private Map<String, String> responseStandard = new HashMap<>();
	private Map<String, String> responseNonStandard = new HashMap<>();
	
	private HttpHeaderTypes() throws UtilsException{
		
		InputStream is =null;
		BufferedReader br = null;
		InputStreamReader ir = null;
		try{
			String file = "/org/openspcoop2/utils/transport/http/httpheader.types";
			is = HttpHeaderTypes.class.getResourceAsStream(file);
			if(is==null){
				throw new UtilsException("File ["+file+"] in classpath not found");
			}
			
			ir = new InputStreamReader(is);
			br = new BufferedReader(ir);
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if(!line.startsWith("#") && !"".equals(line)){
					String [] tmp = line.split(" "); 
					if(tmp.length<4){
						throw new UtilsException("Line ["+line+"] format wrong");
					}
					
					String property = tmp[0];
					
					String richiestaRisposta = tmp[1];
					
					String standardNonStandard = tmp[2];
					
					int length = property.length() + 1 + richiestaRisposta.length() + 1 + standardNonStandard.length() + 1;
					String descrizione = line.substring(length);
					
					if("[request]".equalsIgnoreCase(richiestaRisposta)){
						if("[standard]".equalsIgnoreCase(standardNonStandard)){
							this.requestStandard.put(property, descrizione);
						}
						else if("[non-standard]".equalsIgnoreCase(standardNonStandard)){
							this.requestNonStandard.put(property, descrizione);
						}
						else{
							throw new UtilsException("Line ["+line+"] with wrong value ["+standardNonStandard+"] in third parameter (expected: [standard] o [non-standard] )");
						}
					}
					else if("[response]".equalsIgnoreCase(richiestaRisposta)){
						if("[standard]".equalsIgnoreCase(standardNonStandard)){
							this.responseStandard.put(property, descrizione);
						}
						else if("[non-standard]".equalsIgnoreCase(standardNonStandard)){
							this.responseNonStandard.put(property, descrizione);
						}
						else{
							throw new UtilsException("Line ["+line+"] with wrong value ["+standardNonStandard+"] in third parameter (expected: [standard] o [non-standard] )");
						}
					}
					else{
						throw new UtilsException("Line ["+line+"] with wrong value ["+richiestaRisposta+"] in second parameter (expected: [request] o [response] )");
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
			}catch(Exception eClose){
				// ignore
			}
			try{
				if(ir!=null){
					ir.close();
				}
			}catch(Exception eClose){
				// ignore
			}
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){
				// close
			}
		}
		
	} 
	
	public List<String> getHeaders(){
		List<String> list = new ArrayList<>();
		list.addAll(this.getRequestHeaders());
		list.addAll(this.getResponseHeaders());
		return list;
	}
	
	public List<String> getRequestHeaders(){
		List<String> list = new ArrayList<>();
		list.addAll(this.getRequestStandardHeaders());
		list.addAll(this.getRequestNonStandardHeaders());
		return list;
	}
	public List<String> getRequestStandardHeaders(){
		List<String> list = new ArrayList<>();
		list.addAll(this.requestStandard.keySet());
		return list;
	}
	public List<String> getRequestNonStandardHeaders(){
		List<String> list = new ArrayList<>();
		list.addAll(this.requestNonStandard.keySet());
		return list;
	}
	
	public List<String> getResponseHeaders(){
		List<String> list = new ArrayList<>();
		list.addAll(this.getResponseStandardHeaders());
		list.addAll(this.getResponseNonStandardHeaders());
		return list;
	}
	public List<String> getResponseStandardHeaders(){
		List<String> list = new ArrayList<>();
		list.addAll(this.responseStandard.keySet());
		return list;
	}
	public List<String> getResponseNonStandardHeaders(){
		List<String> list = new ArrayList<>();
		list.addAll(this.responseNonStandard.keySet());
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
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED'
			synchronized (HttpHeaderTypes.class) {
				init();
			}
		}
		return httpHeaderTypes;
	}
	
}
