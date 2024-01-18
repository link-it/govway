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


package org.openspcoop2.pdd.core;

import java.io.Serializable;

import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.utils.MapKey;

/**
 * PdDContext
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PdDContext extends Context implements Serializable {
	
	public PdDContext() {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2577197242840238762L;
	
	public static String getValue(MapKey<String> key,PdDContext pddContext){
		String value = null;
		if(pddContext!=null){
			Object o = pddContext.getObject(key);
			if(o instanceof String ){
				value = (String) o;
			}
		}
		return value;
	}
	
	@Override
	public Object clone() {
		PdDContext newPdDContext = new PdDContext();
		if(!this.isEmpty()) {
			for (MapKey<String> key : this.keySet()) {
				Object o = this.getObject(key);
				newPdDContext.addObject(key, o);
			}
		}
		return newPdDContext;
	}
}
