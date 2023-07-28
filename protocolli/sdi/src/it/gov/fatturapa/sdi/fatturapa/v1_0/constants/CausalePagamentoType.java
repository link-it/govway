/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package it.gov.fatturapa.sdi.fatturapa.v1_0.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * Enumeration dell'elemento CausalePagamentoType xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@jakarta.xml.bind.annotation.XmlType(name = "CausalePagamentoType")
@jakarta.xml.bind.annotation.XmlEnum(java.lang.String.class)
public enum CausalePagamentoType implements IEnumeration , Serializable , Cloneable {

	@jakarta.xml.bind.annotation.XmlEnumValue("A")
	A ('A'),
	@jakarta.xml.bind.annotation.XmlEnumValue("B")
	B ('B'),
	@jakarta.xml.bind.annotation.XmlEnumValue("C")
	C ('C'),
	@jakarta.xml.bind.annotation.XmlEnumValue("D")
	D ('D'),
	@jakarta.xml.bind.annotation.XmlEnumValue("E")
	E ('E'),
	@jakarta.xml.bind.annotation.XmlEnumValue("F")
	F ('F'),
	@jakarta.xml.bind.annotation.XmlEnumValue("G")
	G ('G'),
	@jakarta.xml.bind.annotation.XmlEnumValue("H")
	H ('H'),
	@jakarta.xml.bind.annotation.XmlEnumValue("I")
	I ('I'),
	@jakarta.xml.bind.annotation.XmlEnumValue("L")
	L ('L'),
	@jakarta.xml.bind.annotation.XmlEnumValue("M")
	M ('M'),
	@jakarta.xml.bind.annotation.XmlEnumValue("N")
	N ('N'),
	@jakarta.xml.bind.annotation.XmlEnumValue("O")
	O ('O'),
	@jakarta.xml.bind.annotation.XmlEnumValue("P")
	P ('P'),
	@jakarta.xml.bind.annotation.XmlEnumValue("Q")
	Q ('Q'),
	@jakarta.xml.bind.annotation.XmlEnumValue("R")
	R ('R'),
	@jakarta.xml.bind.annotation.XmlEnumValue("S")
	S ('S'),
	@jakarta.xml.bind.annotation.XmlEnumValue("T")
	T ('T'),
	@jakarta.xml.bind.annotation.XmlEnumValue("U")
	U ('U'),
	@jakarta.xml.bind.annotation.XmlEnumValue("V")
	V ('V'),
	@jakarta.xml.bind.annotation.XmlEnumValue("W")
	W ('W'),
	@jakarta.xml.bind.annotation.XmlEnumValue("X")
	X ('X'),
	@jakarta.xml.bind.annotation.XmlEnumValue("Y")
	Y ('Y'),
	@jakarta.xml.bind.annotation.XmlEnumValue("Z")
	Z ('Z');
	
	
	/** Value */
	private java.lang.Character value;
	@Override
	public java.lang.Character getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	CausalePagamentoType(java.lang.Character value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value+"";
	}
	public boolean equals(java.lang.Character object){
		if(object==null)
			return false;
		return object.toString().equals(this.getValue().toString());	
	}
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,List<String> fieldsNotCheck){
		if( !(object instanceof CausalePagamentoType) ){
			java.lang.StringBuilder sb = new java.lang.StringBuilder();
			if(fieldsNotCheck!=null && !fieldsNotCheck.isEmpty()){
				sb.append(" (fieldsNotCheck: ").append(fieldsNotCheck).append(")");
			}
			throw new org.openspcoop2.utils.UtilsRuntimeException("Wrong type"+sb.toString()+": "+object.getClass().getName());
		}
		return this.equals(object);
	}
	private String toStringEngine(Object object,boolean reportHTML,List<String> fieldsNotIncluded, StringBuilder bf){
		java.lang.StringBuilder sb = new java.lang.StringBuilder();
		if(reportHTML){
			sb.append(" (reportHTML)");
		}
		if(fieldsNotIncluded!=null && !fieldsNotIncluded.isEmpty()){
			sb.append(" (fieldsNotIncluded: ").append(fieldsNotIncluded).append(")");
		}
		if(object!=null){
			sb.append(" (object: ").append(object.getClass().getName()).append(")");
		}
		if(sb.length()>0) {
			bf.append(sb.toString());
		}
		return sb.length()>0 ? this.toString()+sb.toString() : this.toString();
	}
	public String toString(boolean reportHTML){
		return toStringEngine(null, reportHTML, null, null);
	}
  	public String toString(boolean reportHTML,List<String> fieldsNotIncluded){
  		return toStringEngine(null, reportHTML, fieldsNotIncluded, null);
  	}
  	public String diff(Object object,StringBuilder bf,boolean reportHTML){
  		return toStringEngine(object, reportHTML, null, bf);
	}
	public String diff(Object object,StringBuilder bf,boolean reportHTML,List<String> fieldsNotIncluded){
		return toStringEngine(object, reportHTML, fieldsNotIncluded, bf);
	}
	
	
	/** Utilities */
	
	public static java.lang.Character[] toArray(){
		java.lang.Character[] res = new java.lang.Character[values().length];
		int i=0;
		for (CausalePagamentoType tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (CausalePagamentoType tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (CausalePagamentoType tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(java.lang.Character value){
		return toEnumConstant(value)!=null;
	}
	
	public static CausalePagamentoType toEnumConstant(java.lang.Character value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static CausalePagamentoType toEnumConstant(java.lang.Character value, boolean throwNotFoundException) throws NotFoundException{
		CausalePagamentoType res = null;
		for (CausalePagamentoType tmp : values()) {
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
		CausalePagamentoType res = null;
		for (CausalePagamentoType tmp : values()) {
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
