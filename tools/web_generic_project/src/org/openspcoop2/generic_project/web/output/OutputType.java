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
package org.openspcoop2.generic_project.web.output;

import java.io.Serializable;

import org.openspcoop2.generic_project.beans.IEnumeration;

/***
 * 
 * Enum dei tipi di elementi di output disponibili.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public enum OutputType implements IEnumeration , Serializable , Cloneable{
	
	TEXT ("text"), DATE("date"), NUMBER ("number") , IMAGE ("image"), BUTTON ("button");
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	OutputType(String value)
	{
		this.value = value;
	}

	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(OutputType object){
		if(object==null)
			return false;
		if(object.getValue()==null)
			return false;
		return object.getValue().equals(this.getValue());	
	}
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.getValue());	
	}

	/** Utilities */

	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (OutputType tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (OutputType tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (OutputType tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}

	public static OutputType toEnumConstant(String value){
		OutputType res = null;
		if(OutputType.TEXT.getValue().equals(value)){
			res = OutputType.TEXT;
		}else if(OutputType.DATE.getValue().equals(value)){
			res = OutputType.DATE;
		}else if(OutputType.NUMBER.getValue().equals(value)){
			res = OutputType.NUMBER;
		}else if(OutputType.IMAGE.getValue().equals(value)){
			res = OutputType.IMAGE;
		}else if(OutputType.BUTTON.getValue().equals(value)){
			res = OutputType.BUTTON;
		}
		return res;
	}

	public static IEnumeration toEnumConstantFromString(String value){
		OutputType res = null;
		if(OutputType.TEXT.toString().equals(value)){
			res = OutputType.TEXT;
		}else if(OutputType.DATE.toString().equals(value)){
			res = OutputType.DATE;
		}else if(OutputType.NUMBER.toString().equals(value)){
			res = OutputType.NUMBER;
		}else if(OutputType.IMAGE.toString().equals(value)){
			res = OutputType.IMAGE;
		}else if(OutputType.BUTTON.toString().equals(value)){
			res = OutputType.BUTTON;
		}
		return res;
	}
}
