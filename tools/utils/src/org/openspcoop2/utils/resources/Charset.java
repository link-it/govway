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

package org.openspcoop2.utils.resources;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.CharEncoding;

/**
* CharsetEncoding
*
* @author Andrea Poli <apoli@link.it>
* @author $Author$
* @version $Rev$, $Date$
*/
public enum Charset implements Serializable , Cloneable {

	ISO_8859_1 (CharEncoding.ISO_8859_1),
	UTF_8 (CharEncoding.UTF_8),
	UTF_16 (CharEncoding.UTF_16),
	UTF_16BE (CharEncoding.UTF_16BE),
	UTF_16LE (CharEncoding.UTF_16LE),
	US_ASCII (CharEncoding.US_ASCII);
	
	
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
	public boolean equals(Charset object){
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
  	public String diff(Object object,StringBuffer bf,boolean reportHTML){
		return bf.toString();
	}
	public String diff(Object object,StringBuffer bf,boolean reportHTML,List<String> fieldsNotIncluded){
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
