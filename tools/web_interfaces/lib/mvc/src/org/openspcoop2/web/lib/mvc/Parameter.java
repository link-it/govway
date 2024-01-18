/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.web.lib.mvc;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Parameter
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Parameter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String value;

	public Parameter() {}
	
	public Parameter(String name,String value){
		this.name = name;
		this.value = value;
	}

	/****
	 * 
	 * Costruttore per la costruzione di un parametro con link che va a finire nel titolo della pagina.
	 *  
	 * 
	 * @param name
	 * @param baseUrl
	 * @param parameter
	 */
	public Parameter(String name, String baseUrl, Parameter ... parameter){
		this.name = name;

		StringBuilder sb = new StringBuilder();

		sb.append(baseUrl);

		if(parameter != null && parameter.length > 0){
			sb.append("?");

			for (int i = 0; i < parameter.length; i++) {
				sb.append(parameter[i].toString());
				if(i < parameter.length -1){
					sb.append("&");
				}
			}
		}

		this.value = sb.toString().replaceAll(" ", "%20");
	}
	
	public Parameter(String name, String baseUrl, List<Parameter> parameterList){
		this(name, baseUrl, parameterList.toArray(new Parameter[parameterList.size()]));
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String val = null;
		if(this.value!=null) {
			try {
				val = URLEncoder.encode(this.value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				val = this.value;
			}
		}
		else {
			val = "";
		}
		sb.append(this.name).append("=").append(val);
		return sb.toString();
	}

	public static String estraiPathDaUrl(String url) {
		if(url.contains("?")) {
			return url.substring(0, url.indexOf("?"));
		} else 
			return url;
	}
	
	public static List<Parameter> estraiParametriDaUrl(String url) {
		List<Parameter> lista = new ArrayList<>();
		if(url.contains("?")) {
			String queryString = url.substring(url.indexOf("?")+1);
			
			String [] pair = queryString.split("&");
			
			if(pair != null && pair.length > 0) {
				for (String pairTmp : pair) {
					String [] coppia = pairTmp.split("=");
					if(coppia != null && coppia.length > 0) {
						String key = coppia[0];
						String val = "";
						if(coppia.length == 2) {
							val = coppia[1];
						}
						lista.add(new Parameter(key, val));
					}
				}
			}
		}
		
		return lista;
	}
	
}
