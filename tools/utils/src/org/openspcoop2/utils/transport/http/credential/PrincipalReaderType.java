/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
package org.openspcoop2.utils.transport.http.credential;

import java.io.Serializable;

/**
 * Enum dei tipi di reader disponibili
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author: mergefairy $
 * @version $Rev: 12774 $, $Date: 2017-03-10 10:44:01 +0100 (Fri, 10 Mar 2017) $
 */
public enum PrincipalReaderType implements  Serializable , Cloneable {
	
	PRINCIPAL ("principal"), COOKIE("cookie");

	/** Value */
	private String value;
	public String getValue()
	{
		return this.value;
	}

	/** Official Constructor */
	PrincipalReaderType(String value)
	{
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.value;
	}

	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static PrincipalReaderType toEnumConstant(String value){
		PrincipalReaderType res = null;
		for (PrincipalReaderType tmp : values()) {
			if(tmp.getValue().equals(value)){
				res = tmp;
				break;
			}
		}
		return res;
	}
}
