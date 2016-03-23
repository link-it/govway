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


package org.openspcoop2.pdd.core;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * PdDContext
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PdDContext implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2577197242840238762L;
	
	private Hashtable<String, Object> ctx = new Hashtable<String, Object>();
	
	public Hashtable<String, Object> getContext() {
		return this.ctx;
	}

	public void addObject(String key,Object o){
		if(key!=null && o!=null)
			this.ctx.put(key, o);
	}
	
	public Object getObject(String key){
		if(key!=null)
			return this.ctx.get(key);
		else
			return null;
	}
	
	public Object removeObject(String key){
		if(key!=null)
			return this.ctx.remove(key);
		else
			return null;
	}
	
	public Enumeration<String> keys(){
		return this.ctx.keys();
	}
	
	public boolean containsKey(String key){
		return this.ctx.containsKey(key);
	}
	
	public void addAll(PdDContext pddContext,boolean overwriteIfExists){
		Enumeration<String> keys = pddContext.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if(this.containsKey(key)){
				if(overwriteIfExists){
					this.removeObject(key);
					this.addObject(key, pddContext.getObject(key));
				}
			}
			else{
				this.addObject(key, pddContext.getObject(key));
			}
		}
	}
	
	public static String getValue(String key,PdDContext pddContext){
		String value = null;
		if(pddContext!=null){
			Object o = pddContext.getObject(key);
			if(o!=null && (o instanceof String) ){
				value = (String) o;
			}
		}
		return value;
	}
	
}
