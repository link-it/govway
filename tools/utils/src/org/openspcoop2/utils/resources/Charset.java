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

package org.openspcoop2.utils.resources;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
* CharsetEncoding
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public enum Charset implements Serializable , Cloneable {

	ISO_8859_1 (StandardCharsets.ISO_8859_1.name()),
	UTF_8 (StandardCharsets.UTF_8.name()),
	UTF_16 (StandardCharsets.UTF_16.name()),
	UTF_16BE (StandardCharsets.UTF_16BE.name()),
	UTF_16LE (StandardCharsets.UTF_16LE.name()),
	US_ASCII (StandardCharsets.US_ASCII.name());
	
	
	/** Value */
	private String value;
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	Charset(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(String value){
		if(value==null)
			return false;
		return value.equals(this.getValue());	
	}
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,List<String> fieldsNotCheck){
		if( !(object instanceof Charset) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((Charset)object));
	}
	public String toString(boolean reportHTML){
		return toString();
	}
  	public String toString(boolean reportHTML,List<String> fieldsNotIncluded){
  		return toString();
  	}
  	public String diff(Object object,StringBuilder bf,boolean reportHTML){
		return bf.toString();
	}
	public String diff(Object object,StringBuilder bf,boolean reportHTML,List<String> fieldsNotIncluded){
		return bf.toString();
	}
	
	
	/** Utilities */
	
	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (Charset tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (Charset tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (Charset tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static Charset toEnumConstant(String value){
		Charset res = null;
		if(Charset.ISO_8859_1.getValue().equals(value)){
			res = Charset.ISO_8859_1;
		}else if(Charset.US_ASCII.getValue().equals(value)){
			res = Charset.US_ASCII;
		}else if(Charset.UTF_8.getValue().equals(value)){
			res = Charset.UTF_8;
		}else if(Charset.UTF_16.getValue().equals(value)){
			res = Charset.UTF_16;
		}else if(Charset.UTF_16BE.getValue().equals(value)){
			res = Charset.UTF_16BE;
		}else if(Charset.UTF_16LE.getValue().equals(value)){
			res = Charset.UTF_16LE;
		}
		return res;
	}
	
	public static Charset toEnumConstantFromString(String value){
		Charset res = null;
		if(Charset.ISO_8859_1.toString().equals(value)){
			res = Charset.ISO_8859_1;
		}else if(Charset.US_ASCII.toString().equals(value)){
			res = Charset.US_ASCII;
		}else if(Charset.UTF_8.toString().equals(value)){
			res = Charset.UTF_8;
		}else if(Charset.UTF_16.toString().equals(value)){
			res = Charset.UTF_16;
		}else if(Charset.UTF_16BE.toString().equals(value)){
			res = Charset.UTF_16BE;
		}else if(Charset.UTF_16LE.toString().equals(value)){
			res = Charset.UTF_16LE;
		}
		return res;
	}
	
}
