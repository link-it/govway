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


package org.openspcoop2.utils.date;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.utils.UtilsException;


/**     
 * Enumeration dell'elemento tipo-periodo xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "tipo-periodo")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum UnitaTemporale implements Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("secondi")
	SECONDI ("secondi"),
	@javax.xml.bind.annotation.XmlEnumValue("minuti")
	MINUTI ("minuti"),
	@javax.xml.bind.annotation.XmlEnumValue("orario")
	ORARIO ("orario"),
	@javax.xml.bind.annotation.XmlEnumValue("giornaliero")
	GIORNALIERO ("giornaliero"),
	@javax.xml.bind.annotation.XmlEnumValue("settimanale")
	SETTIMANALE ("settimanale"),
	@javax.xml.bind.annotation.XmlEnumValue("mensile")
	MENSILE ("mensile");
	
	
	/** Value */
	private String value;
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	UnitaTemporale(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(UnitaTemporale object){
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
		if( !(object instanceof UnitaTemporale) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((UnitaTemporale)object));
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
		for (UnitaTemporale tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (UnitaTemporale tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (UnitaTemporale tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static UnitaTemporale toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(UtilsException notFound){
			return null;
		}
	}
	public static UnitaTemporale toEnumConstant(String value, boolean throwNotFoundException) throws UtilsException{
		UnitaTemporale res = null;
		for (UnitaTemporale tmp : values()) {
			if(tmp.getValue().equals(value)){
				res = tmp;
				break;
			}
		}
		if(res==null && throwNotFoundException){
			throw new UtilsException("Enum with value ["+value+"] not found");
		}
		return res;
	}
	
	public static UnitaTemporale toEnumConstantFromString(String value){
		try{
			return toEnumConstantFromString(value,false);
		}catch(UtilsException notFound){
			return null;
		}
	}
	public static UnitaTemporale toEnumConstantFromString(String value, boolean throwNotFoundException) throws UtilsException{
		UnitaTemporale res = null;
		for (UnitaTemporale tmp : values()) {
			if(tmp.toString().equals(value)){
				res = tmp;
				break;
			}
		}
		if(res==null && throwNotFoundException){
			throw new UtilsException("Enum with value ["+value+"] not found");
		}
		return res;
	}
}
