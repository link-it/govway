/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * TipoGestorePolicy
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoGestorePolicy {

	IN_MEMORY("inMemory"),
	WS("ws");
	
	private String tipo;
	
	public String getTipo() {
		return this.tipo;
	}

	TipoGestorePolicy(String tipo){
		this.tipo = tipo;
	}
	
	public static TipoGestorePolicy toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static TipoGestorePolicy toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		TipoGestorePolicy res = null;
		for (TipoGestorePolicy tmp : values()) {
			if(tmp.getTipo().equals(value)){
				res = tmp;
				break;
			}
		}
		if(res==null && throwNotFoundException){
			throw new NotFoundException("Enum with value ["+value+"] not found");
		}
		return res;
	}
}
