/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved.
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
	MP17 ("MP17"),
	@javax.xml.bind.annotation.XmlEnumValue("MP18")
	MP18 ("MP18"),
	@javax.xml.bind.annotation.XmlEnumValue("MP19")
	MP19 ("MP19"),
	@javax.xml.bind.annotation.XmlEnumValue("MP20")
	MP20 ("MP20"),
	@javax.xml.bind.annotation.XmlEnumValue("MP21")
	MP21 ("MP21");
	
	
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
		ModalitaPagamentoType res = null;
		if(ModalitaPagamentoType.MP01.getValue().equals(value)){
			res = ModalitaPagamentoType.MP01;
		}else if(ModalitaPagamentoType.MP02.getValue().equals(value)){
			res = ModalitaPagamentoType.MP02;
		}else if(ModalitaPagamentoType.MP03.getValue().equals(value)){
			res = ModalitaPagamentoType.MP03;
		}else if(ModalitaPagamentoType.MP04.getValue().equals(value)){
			res = ModalitaPagamentoType.MP04;
		}else if(ModalitaPagamentoType.MP05.getValue().equals(value)){
			res = ModalitaPagamentoType.MP05;
		}else if(ModalitaPagamentoType.MP06.getValue().equals(value)){
			res = ModalitaPagamentoType.MP06;
		}else if(ModalitaPagamentoType.MP07.getValue().equals(value)){
			res = ModalitaPagamentoType.MP07;
		}else if(ModalitaPagamentoType.MP08.getValue().equals(value)){
			res = ModalitaPagamentoType.MP08;
		}else if(ModalitaPagamentoType.MP09.getValue().equals(value)){
			res = ModalitaPagamentoType.MP09;
		}else if(ModalitaPagamentoType.MP10.getValue().equals(value)){
			res = ModalitaPagamentoType.MP10;
		}else if(ModalitaPagamentoType.MP11.getValue().equals(value)){
			res = ModalitaPagamentoType.MP11;
		}else if(ModalitaPagamentoType.MP12.getValue().equals(value)){
			res = ModalitaPagamentoType.MP12;
		}else if(ModalitaPagamentoType.MP13.getValue().equals(value)){
			res = ModalitaPagamentoType.MP13;
		}else if(ModalitaPagamentoType.MP14.getValue().equals(value)){
			res = ModalitaPagamentoType.MP14;
		}else if(ModalitaPagamentoType.MP15.getValue().equals(value)){
			res = ModalitaPagamentoType.MP15;
		}else if(ModalitaPagamentoType.MP16.getValue().equals(value)){
			res = ModalitaPagamentoType.MP16;
		}else if(ModalitaPagamentoType.MP17.getValue().equals(value)){
			res = ModalitaPagamentoType.MP17;
		}else if(ModalitaPagamentoType.MP18.getValue().equals(value)){
			res = ModalitaPagamentoType.MP18;
		}else if(ModalitaPagamentoType.MP19.getValue().equals(value)){
			res = ModalitaPagamentoType.MP19;
		}else if(ModalitaPagamentoType.MP20.getValue().equals(value)){
			res = ModalitaPagamentoType.MP20;
		}else if(ModalitaPagamentoType.MP21.getValue().equals(value)){
			res = ModalitaPagamentoType.MP21;
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		ModalitaPagamentoType res = null;
		if(ModalitaPagamentoType.MP01.toString().equals(value)){
			res = ModalitaPagamentoType.MP01;
		}else if(ModalitaPagamentoType.MP02.toString().equals(value)){
			res = ModalitaPagamentoType.MP02;
		}else if(ModalitaPagamentoType.MP03.toString().equals(value)){
			res = ModalitaPagamentoType.MP03;
		}else if(ModalitaPagamentoType.MP04.toString().equals(value)){
			res = ModalitaPagamentoType.MP04;
		}else if(ModalitaPagamentoType.MP05.toString().equals(value)){
			res = ModalitaPagamentoType.MP05;
		}else if(ModalitaPagamentoType.MP06.toString().equals(value)){
			res = ModalitaPagamentoType.MP06;
		}else if(ModalitaPagamentoType.MP07.toString().equals(value)){
			res = ModalitaPagamentoType.MP07;
		}else if(ModalitaPagamentoType.MP08.toString().equals(value)){
			res = ModalitaPagamentoType.MP08;
		}else if(ModalitaPagamentoType.MP09.toString().equals(value)){
			res = ModalitaPagamentoType.MP09;
		}else if(ModalitaPagamentoType.MP10.toString().equals(value)){
			res = ModalitaPagamentoType.MP10;
		}else if(ModalitaPagamentoType.MP11.toString().equals(value)){
			res = ModalitaPagamentoType.MP11;
		}else if(ModalitaPagamentoType.MP12.toString().equals(value)){
			res = ModalitaPagamentoType.MP12;
		}else if(ModalitaPagamentoType.MP13.toString().equals(value)){
			res = ModalitaPagamentoType.MP13;
		}else if(ModalitaPagamentoType.MP14.toString().equals(value)){
			res = ModalitaPagamentoType.MP14;
		}else if(ModalitaPagamentoType.MP15.toString().equals(value)){
			res = ModalitaPagamentoType.MP15;
		}else if(ModalitaPagamentoType.MP16.toString().equals(value)){
			res = ModalitaPagamentoType.MP16;
		}else if(ModalitaPagamentoType.MP17.toString().equals(value)){
			res = ModalitaPagamentoType.MP17;
		}else if(ModalitaPagamentoType.MP18.toString().equals(value)){
			res = ModalitaPagamentoType.MP18;
		}else if(ModalitaPagamentoType.MP19.toString().equals(value)){
			res = ModalitaPagamentoType.MP19;
		}else if(ModalitaPagamentoType.MP20.toString().equals(value)){
			res = ModalitaPagamentoType.MP20;
		}else if(ModalitaPagamentoType.MP21.toString().equals(value)){
			res = ModalitaPagamentoType.MP21;
		}
		return res;
	}
}
