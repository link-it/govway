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
package it.gov.fatturapa.sdi.fatturapa.v1_1.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * Enumeration dell'elemento TipoCassaType xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "TipoCassaType")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum TipoCassaType implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("TC01")
	TC01 ("TC01"),
	@javax.xml.bind.annotation.XmlEnumValue("TC02")
	TC02 ("TC02"),
	@javax.xml.bind.annotation.XmlEnumValue("TC03")
	TC03 ("TC03"),
	@javax.xml.bind.annotation.XmlEnumValue("TC04")
	TC04 ("TC04"),
	@javax.xml.bind.annotation.XmlEnumValue("TC05")
	TC05 ("TC05"),
	@javax.xml.bind.annotation.XmlEnumValue("TC06")
	TC06 ("TC06"),
	@javax.xml.bind.annotation.XmlEnumValue("TC07")
	TC07 ("TC07"),
	@javax.xml.bind.annotation.XmlEnumValue("TC08")
	TC08 ("TC08"),
	@javax.xml.bind.annotation.XmlEnumValue("TC09")
	TC09 ("TC09"),
	@javax.xml.bind.annotation.XmlEnumValue("TC10")
	TC10 ("TC10"),
	@javax.xml.bind.annotation.XmlEnumValue("TC11")
	TC11 ("TC11"),
	@javax.xml.bind.annotation.XmlEnumValue("TC12")
	TC12 ("TC12"),
	@javax.xml.bind.annotation.XmlEnumValue("TC13")
	TC13 ("TC13"),
	@javax.xml.bind.annotation.XmlEnumValue("TC14")
	TC14 ("TC14"),
	@javax.xml.bind.annotation.XmlEnumValue("TC15")
	TC15 ("TC15"),
	@javax.xml.bind.annotation.XmlEnumValue("TC16")
	TC16 ("TC16"),
	@javax.xml.bind.annotation.XmlEnumValue("TC17")
	TC17 ("TC17"),
	@javax.xml.bind.annotation.XmlEnumValue("TC18")
	TC18 ("TC18"),
	@javax.xml.bind.annotation.XmlEnumValue("TC19")
	TC19 ("TC19"),
	@javax.xml.bind.annotation.XmlEnumValue("TC20")
	TC20 ("TC20"),
	@javax.xml.bind.annotation.XmlEnumValue("TC21")
	TC21 ("TC21"),
	@javax.xml.bind.annotation.XmlEnumValue("TC22")
	TC22 ("TC22");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	TipoCassaType(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(TipoCassaType object){
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
		if( !(object instanceof TipoCassaType) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((TipoCassaType)object));
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
		for (TipoCassaType tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoCassaType tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoCassaType tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static TipoCassaType toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static TipoCassaType toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		TipoCassaType res = null;
		for (TipoCassaType tmp : values()) {
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
		TipoCassaType res = null;
		for (TipoCassaType tmp : values()) {
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
