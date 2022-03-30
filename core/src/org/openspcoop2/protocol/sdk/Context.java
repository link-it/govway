/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.protocol.sdk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Context implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2577197242840238762L;
	
	protected Map<String, Object> ctx = new HashMap<String, Object>();
	
	public Map<String, Object> getContext() {
		return this.ctx;
	}

	public boolean isEmpty() {
		return this.ctx.isEmpty();
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
	
	public List<String> keys(){
		List<String> keys = null;
		if(!this.ctx.isEmpty()) {
			keys = new ArrayList<String>();
			for (String key : this.ctx.keySet()) {
				keys.add(key);
			}
		}
		return keys;
	}
	
	public boolean containsKey(String key){
		return this.ctx.containsKey(key);
	}
	
	public void addAll(Context pddContext,boolean overwriteIfExists){
		List<String> keys = pddContext.keys();
		if(keys!=null) {
			for (String key : keys) {
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
	}
	
	public static String getValue(String key,Context pddContext){
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
