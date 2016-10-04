/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.utils.csv;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.UtilsException;

/**
 * MapResult
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MapResult {

	// E' stata fatta questa classe per evitare i nullPointer nel caso di valori null se si usa una hashtable
	
	private List<Result> list = new ArrayList<Result>();
	private List<String> keys = new ArrayList<String>();
	
	public void add(String name,String value) throws UtilsException{
		
		if(this.keys.contains(name)){
			Result r = this.getResult(name);
			r.value = value;
		}
		else{
			Result r = new Result();
			r.name = name;
			r.value = value;
			this.list.add(r);
			this.keys.add(name);
		}
	}
	public String get(String name) throws UtilsException{
		return this.getResult(name).value;
	}
	public List<String> keys(){
		return this.keys;
	}
	public int size(){
		return this.keys.size();
	}
	
	private Result getResult(String name) throws UtilsException{
		for (Result r : this.list) {
			if(r.name.equals(name)){
				return r;
			}
		}
		throw new UtilsException("Entry with key ["+name+"] not exists");
	}
}

class Result{
	String name;
	String value;
}
