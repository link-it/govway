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

package org.openspcoop2.pdd.core.handlers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * ExtendedTransactionInfo
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExtendedTransactionInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> keys = new ArrayList<>();
	private Hashtable<String, String> mapKeyToValue = new Hashtable<>();
	
	public void addCustomKey(String key,String value){
		this.keys.add(key);
		this.mapKeyToValue.put(key, value);
	}
	
	public String getValue(String key) throws Exception{
		if(this.mapKeyToValue.containsKey(key)==false){
			throw new Exception("Key ["+key+"] not exists");
		}
		return this.mapKeyToValue.get(key);
	}
	
	public List<String> keys(){
		return this.keys;
	}
	
	public void deleteKey(String key){
		int delete = -1;
		for (int i = 0; i < this.keys.size(); i++) {
			String keyCheck = this.keys.get(i);
			if(keyCheck.equals(key)){
				delete = i;
				break;
			}
		}
		if(delete>=0){
			this.keys.remove(delete);
		}
		this.mapKeyToValue.remove(key);
	}
	
}
