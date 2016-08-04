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
package it.gov.fatturapa.sdi.fatturapa.v1_0.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * Enumeration dell'elemento ModalitaPagamentoType xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "ModalitaPagamentoType")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum ModalitaPagamentoType implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("MP01")
	MP01 ("MP01"),
	@javax.xml.bind.annotation.XmlEnumValue("MP02")
	MP02 ("MP02"),
	@javax.xml.bind.annotation.XmlEnumValue("MP03")
	MP03 ("MP03"),
	@javax.xml.bind.annotation.XmlEnumValue("MP04")
	MP04 ("MP04"),
	@javax.xml.bind.annotation.XmlEnumValue("MP05")
	MP05 ("MP05"),
	@javax.xml.bind.annotation.XmlEnumValue("MP06")
	MP06 ("MP06"),
	@javax.xml.bind.annotation.XmlEnumValue("MP07")
	MP07 ("MP07"),
	@javax.xml.bind.annotation.XmlEnumValue("MP08")
	MP08 ("MP08"),
	@javax.xml.bind.annotation.XmlEnumValue("MP09")
	MP09 ("MP09"),
	@javax.xml.bind.annotation.XmlEnumValue("MP10")
	MP10 ("MP10"),
	@javax.xml.bind.annotation.XmlEnumValue("MP11")
	MP11 ("MP11"),
	@javax.xml.bind.annotation.XmlEnumValue("MP12")
	MP12 ("MP12"),
	@javax.xml.bind.annotation.XmlEnumValue("MP13")
	MP13 ("MP13"),
	@javax.xml.bind.annotation.XmlEnumValue("MP14")
	MP14 ("MP14"),
	@javax.xml.bind.annotation.XmlEnumValue("MP15")
	MP15 ("MP15"),
	@javax.xml.bind.annotation.XmlEnumValue("MP16")
	MP16 ("MP16"),
	@javax.xml.bind.annotation.XmlEnumValue("MP17")
	MP17 ("MP17");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	ModalitaPagamentoType(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(ModalitaPagamentoType object){
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
		if( !(object instanceof ModalitaPagamentoType) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((ModalitaPagamentoType)object));
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
		for (ModalitaPagamentoType tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (ModalitaPagamentoType tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (ModalitaPagamentoType tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static ModalitaPagamentoType toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static ModalitaPagamentoType toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		ModalitaPagamentoType res = null;
		for (ModalitaPagamentoType tmp : values()) {
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
		ModalitaPagamentoType res = null;
		for (ModalitaPagamentoType tmp : values()) {
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
