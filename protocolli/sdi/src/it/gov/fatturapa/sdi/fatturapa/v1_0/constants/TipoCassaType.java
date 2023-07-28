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
 * Enumeration dell'elemento TipoCassaType xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@jakarta.xml.bind.annotation.XmlType(name = "TipoCassaType")
@jakarta.xml.bind.annotation.XmlEnum(String.class)
public enum TipoCassaType implements IEnumeration , Serializable , Cloneable {

	@jakarta.xml.bind.annotation.XmlEnumValue("TC01")
	TC01 ("TC01"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC02")
	TC02 ("TC02"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC03")
	TC03 ("TC03"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC04")
	TC04 ("TC04"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC05")
	TC05 ("TC05"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC06")
	TC06 ("TC06"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC07")
	TC07 ("TC07"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC08")
	TC08 ("TC08"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC09")
	TC09 ("TC09"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC10")
	TC10 ("TC10"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC11")
	TC11 ("TC11"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC12")
	TC12 ("TC12"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC13")
	TC13 ("TC13"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC14")
	TC14 ("TC14"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC15")
	TC15 ("TC15"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC16")
	TC16 ("TC16"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC17")
	TC17 ("TC17"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC18")
	TC18 ("TC18"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC19")
	TC19 ("TC19"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC20")
	TC20 ("TC20"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC21")
	TC21 ("TC21"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TC22")
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
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.getValue());	
	}
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,List<String> fieldsNotCheck){
		if( !(object instanceof TipoCassaType) ){
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
