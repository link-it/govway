/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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
package org.openspcoop2.core.config.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**     
 * Enumeration dell'elemento Severita xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "Severita")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum Severita implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("off")
	OFF ("off"),
	@javax.xml.bind.annotation.XmlEnumValue("fatal")
	FATAL ("fatal"),
	@javax.xml.bind.annotation.XmlEnumValue("errorProtocoll")
	ERROR_PROTOCOLL ("errorProtocoll"),
	@javax.xml.bind.annotation.XmlEnumValue("errorIntegration")
	ERROR_INTEGRATION ("errorIntegration"),
	@javax.xml.bind.annotation.XmlEnumValue("infoProtocoll")
	INFO_PROTOCOLL ("infoProtocoll"),
	@javax.xml.bind.annotation.XmlEnumValue("infoIntegration")
	INFO_INTEGRATION ("infoIntegration"),
	@javax.xml.bind.annotation.XmlEnumValue("debugLow")
	DEBUG_LOW ("debugLow"),
	@javax.xml.bind.annotation.XmlEnumValue("debugMedium")
	DEBUG_MEDIUM ("debugMedium"),
	@javax.xml.bind.annotation.XmlEnumValue("debugHigh")
	DEBUG_HIGH ("debugHigh"),
	@javax.xml.bind.annotation.XmlEnumValue("all")
	ALL ("all");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	Severita(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(Severita object){
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
		if( !(object instanceof Severita) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((Severita)object));
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
		for (Severita tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (Severita tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (Severita tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static Severita toEnumConstant(String value){
		Severita res = null;
		if(Severita.OFF.getValue().equals(value)){
			res = Severita.OFF;
		}else if(Severita.FATAL.getValue().equals(value)){
			res = Severita.FATAL;
		}else if(Severita.ERROR_PROTOCOLL.getValue().equals(value)){
			res = Severita.ERROR_PROTOCOLL;
		}else if(Severita.ERROR_INTEGRATION.getValue().equals(value)){
			res = Severita.ERROR_INTEGRATION;
		}else if(Severita.INFO_PROTOCOLL.getValue().equals(value)){
			res = Severita.INFO_PROTOCOLL;
		}else if(Severita.INFO_INTEGRATION.getValue().equals(value)){
			res = Severita.INFO_INTEGRATION;
		}else if(Severita.DEBUG_LOW.getValue().equals(value)){
			res = Severita.DEBUG_LOW;
		}else if(Severita.DEBUG_MEDIUM.getValue().equals(value)){
			res = Severita.DEBUG_MEDIUM;
		}else if(Severita.DEBUG_HIGH.getValue().equals(value)){
			res = Severita.DEBUG_HIGH;
		}else if(Severita.ALL.getValue().equals(value)){
			res = Severita.ALL;
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		Severita res = null;
		if(Severita.OFF.toString().equals(value)){
			res = Severita.OFF;
		}else if(Severita.FATAL.toString().equals(value)){
			res = Severita.FATAL;
		}else if(Severita.ERROR_PROTOCOLL.toString().equals(value)){
			res = Severita.ERROR_PROTOCOLL;
		}else if(Severita.ERROR_INTEGRATION.toString().equals(value)){
			res = Severita.ERROR_INTEGRATION;
		}else if(Severita.INFO_PROTOCOLL.toString().equals(value)){
			res = Severita.INFO_PROTOCOLL;
		}else if(Severita.INFO_INTEGRATION.toString().equals(value)){
			res = Severita.INFO_INTEGRATION;
		}else if(Severita.DEBUG_LOW.toString().equals(value)){
			res = Severita.DEBUG_LOW;
		}else if(Severita.DEBUG_MEDIUM.toString().equals(value)){
			res = Severita.DEBUG_MEDIUM;
		}else if(Severita.DEBUG_HIGH.toString().equals(value)){
			res = Severita.DEBUG_HIGH;
		}else if(Severita.ALL.toString().equals(value)){
			res = Severita.ALL;
		}
		return res;
	}
}
