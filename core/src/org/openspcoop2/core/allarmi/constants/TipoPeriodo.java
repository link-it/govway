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
package org.openspcoop2.core.allarmi.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * Enumeration dell'elemento tipo-periodo xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "tipo-periodo")
@javax.xml.bind.annotation.XmlEnum(java.lang.String.class)
public enum TipoPeriodo implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("g")
	G ('g'),
	@javax.xml.bind.annotation.XmlEnumValue("h")
	H ('h'),
	@javax.xml.bind.annotation.XmlEnumValue("m")
	M ('m');
	
	
	/** Value */
	private java.lang.Character value;
	@Override
	public java.lang.Character getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	TipoPeriodo(java.lang.Character value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value+"";
	}
	public boolean equals(TipoPeriodo object){
		if(object==null)
			return false;
		return object.getValue().toString().equals(this.getValue().toString());	
	}
	public boolean equals(java.lang.Character object){
		if(object==null)
			return false;
		return object.toString().equals(this.getValue().toString());	
	}
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,List<String> fieldsNotCheck){
		if( !(object instanceof TipoPeriodo) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((TipoPeriodo)object));
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
	
	public static java.lang.Character[] toArray(){
		java.lang.Character[] res = new java.lang.Character[values().length];
		int i=0;
		for (TipoPeriodo tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoPeriodo tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoPeriodo tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(java.lang.Character value){
		return toEnumConstant(value)!=null;
	}
	
	public static TipoPeriodo toEnumConstant(java.lang.Character value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static TipoPeriodo toEnumConstant(java.lang.Character value, boolean throwNotFoundException) throws NotFoundException{
		TipoPeriodo res = null;
		for (TipoPeriodo tmp : values()) {
			if(tmp.getValue().toString().equals(value!=null?value.toString():null)){
				res = tmp;
				break;
			}
		}
		if(res==null && throwNotFoundException){
			throw new NotFoundException("Enum with value ["+value+"] not found");
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		try{
			return toEnumConstantFromString(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static IEnumeration toEnumConstantFromString(String value, boolean throwNotFoundException) throws NotFoundException{
		TipoPeriodo res = null;
		for (TipoPeriodo tmp : values()) {
			if(tmp.toString().equals(value)){
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
