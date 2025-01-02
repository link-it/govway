/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.config.vault.cli;

import org.openspcoop2.generic_project.exception.NotFoundException;

/**
* VaultOperationType
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public enum VaultOperationType {

	UPDATE_CONFIG("update_config"),ENCRYPT("encrypt"),DECRYPT("decrypt");
	
	/** Value */
	private String value;
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	VaultOperationType(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equalsEnum(String object){
		if(object==null)
			return false;
		return object.equals(this.getValue());	
	}
	
		
	
	
	
	/** Utilities */
	
	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (VaultOperationType tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (VaultOperationType tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (VaultOperationType tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static VaultOperationType toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static VaultOperationType toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		VaultOperationType res = null;
		for (VaultOperationType tmp : values()) {
			if(tmp.getValue().equals(value)){
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
