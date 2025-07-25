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
package org.openspcoop2.pdd.monitor.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * Enumeration dell'elemento StatoMessaggio xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@jakarta.xml.bind.annotation.XmlType(name = "StatoMessaggio")
@jakarta.xml.bind.annotation.XmlEnum(String.class)
public enum StatoMessaggio implements IEnumeration , Serializable , Cloneable {

	@jakarta.xml.bind.annotation.XmlEnumValue("consegna")
	CONSEGNA ("consegna"),
	@jakarta.xml.bind.annotation.XmlEnumValue("spedizione")
	SPEDIZIONE ("spedizione"),
	@jakarta.xml.bind.annotation.XmlEnumValue("processamento")
	PROCESSAMENTO ("processamento"),
	@jakarta.xml.bind.annotation.XmlEnumValue("cancellato")
	CANCELLATO ("cancellato");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	StatoMessaggio(String value)
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
		if( !(object instanceof StatoMessaggio) ){
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
		for (StatoMessaggio tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (StatoMessaggio tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (StatoMessaggio tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static StatoMessaggio toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static StatoMessaggio toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		StatoMessaggio res = null;
		for (StatoMessaggio tmp : values()) {
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
		StatoMessaggio res = null;
		for (StatoMessaggio tmp : values()) {
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
