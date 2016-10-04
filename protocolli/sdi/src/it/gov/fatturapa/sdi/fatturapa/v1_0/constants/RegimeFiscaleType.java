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
package it.gov.fatturapa.sdi.fatturapa.v1_0.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * Enumeration dell'elemento RegimeFiscaleType xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "RegimeFiscaleType")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum RegimeFiscaleType implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("RF01")
	RF01 ("RF01"),
	@javax.xml.bind.annotation.XmlEnumValue("RF02")
	RF02 ("RF02"),
	@javax.xml.bind.annotation.XmlEnumValue("RF03")
	RF03 ("RF03"),
	@javax.xml.bind.annotation.XmlEnumValue("RF04")
	RF04 ("RF04"),
	@javax.xml.bind.annotation.XmlEnumValue("RF05")
	RF05 ("RF05"),
	@javax.xml.bind.annotation.XmlEnumValue("RF06")
	RF06 ("RF06"),
	@javax.xml.bind.annotation.XmlEnumValue("RF07")
	RF07 ("RF07"),
	@javax.xml.bind.annotation.XmlEnumValue("RF08")
	RF08 ("RF08"),
	@javax.xml.bind.annotation.XmlEnumValue("RF09")
	RF09 ("RF09"),
	@javax.xml.bind.annotation.XmlEnumValue("RF10")
	RF10 ("RF10"),
	@javax.xml.bind.annotation.XmlEnumValue("RF11")
	RF11 ("RF11"),
	@javax.xml.bind.annotation.XmlEnumValue("RF12")
	RF12 ("RF12"),
	@javax.xml.bind.annotation.XmlEnumValue("RF13")
	RF13 ("RF13"),
	@javax.xml.bind.annotation.XmlEnumValue("RF14")
	RF14 ("RF14"),
	@javax.xml.bind.annotation.XmlEnumValue("RF15")
	RF15 ("RF15"),
	@javax.xml.bind.annotation.XmlEnumValue("RF16")
	RF16 ("RF16"),
	@javax.xml.bind.annotation.XmlEnumValue("RF17")
	RF17 ("RF17"),
	@javax.xml.bind.annotation.XmlEnumValue("RF18")
	RF18 ("RF18");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	RegimeFiscaleType(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(RegimeFiscaleType object){
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
		if( !(object instanceof RegimeFiscaleType) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((RegimeFiscaleType)object));
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
		for (RegimeFiscaleType tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (RegimeFiscaleType tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (RegimeFiscaleType tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static RegimeFiscaleType toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static RegimeFiscaleType toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		RegimeFiscaleType res = null;
		for (RegimeFiscaleType tmp : values()) {
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
	
	public static IEnumeration toEnumConstantFromString(String value){
		try{
			return toEnumConstantFromString(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static IEnumeration toEnumConstantFromString(String value, boolean throwNotFoundException) throws NotFoundException{
		RegimeFiscaleType res = null;
		for (RegimeFiscaleType tmp : values()) {
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
