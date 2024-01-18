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


package org.openspcoop2.protocol.sdk;

import java.io.Serializable;

import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;

/**
 * Context
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Context extends org.openspcoop2.utils.Map<Object> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2577197242840238762L;
	
	
	// STATIC
	
	@Deprecated
	public static String getValue(String key,Context pddContext){
		if(key==null) {
			return null;
		}
		return getValue(Map.newMapKey(key), pddContext);
	}
	public static String getValue(MapKey<String> key,Context pddContext){
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
