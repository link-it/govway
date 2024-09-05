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


package org.openspcoop2.core.constants;

import org.openspcoop2.utils.UtilsException;

/**
 * Contiene le libreria per implementare comunicazioni http
 *
 * @author apoli@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public enum ConnettoriHttpImpl {

	HTTP_URL_CONNECTION ("java.net.HttpURLConnection"),
	HTTP_CORE5 ("org.apache.hc.client5");	
	
	private final String nome;

	ConnettoriHttpImpl(String nome)
	{
		this.nome = nome;
	}

	public String getNome()
	{
		return this.nome;
	}
	
	public static ConnettoriHttpImpl getConnettoreHttpImplSafe(String value) {
		try {
			return getConnettoreHttpImpl(value);
		}catch(UtilsException e) {
			// ignore
		}
		return null;
	}
	public static ConnettoriHttpImpl getConnettoreHttpImpl(String value) throws UtilsException{
		if(HTTP_URL_CONNECTION.toString().equals(value)){
			return HTTP_URL_CONNECTION;
		}
		else if(HTTP_CORE5.toString().equals(value)){
			return HTTP_CORE5;
		}
		else{
			throw new UtilsException("Unknown type '"+value+"' (supported values: "+ConnettoriHttpImpl.stringValues()+"): "+value);
		}
	}
	
	public static String stringValues(){
		StringBuilder res = new StringBuilder();
		int i=0;
		for (ConnettoriHttpImpl tmp : ConnettoriHttpImpl.values()) {
			if(i>0)
				res.append(",");
			res.append(tmp.getNome());
			i++;
		}
		return res.toString();
	}
	
	public static String[] toStringArray(){
		String[] res = new String[ConnettoriHttpImpl.values().length];
		int i=0;
		for (ConnettoriHttpImpl tmp : ConnettoriHttpImpl.values()) {
			res[i]=tmp.getNome();
			i++;
		}
		return res;
	}
	
	@Override
	public String toString() {
		return this.nome;
	}
	
	public boolean equals(String tlm){
		if(tlm==null) {
			return false;
		}
		return this.toString().equals(tlm);
	}
}

