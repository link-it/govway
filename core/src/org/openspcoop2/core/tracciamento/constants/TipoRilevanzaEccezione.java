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
package org.openspcoop2.core.tracciamento.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**     
 * Enumeration dell'elemento TipoRilevanzaEccezione xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "TipoRilevanzaEccezione")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum TipoRilevanzaEccezione implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("DEBUG")
	DEBUG ("DEBUG"),
	@javax.xml.bind.annotation.XmlEnumValue("INFO")
	INFO ("INFO"),
	@javax.xml.bind.annotation.XmlEnumValue("WARN")
	WARN ("WARN"),
	@javax.xml.bind.annotation.XmlEnumValue("ERROR")
	ERROR ("ERROR"),
	@javax.xml.bind.annotation.XmlEnumValue("FATAL")
	FATAL ("FATAL"),
	@javax.xml.bind.annotation.XmlEnumValue("Sconosciuto")
	SCONOSCIUTO ("Sconosciuto");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	TipoRilevanzaEccezione(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(TipoRilevanzaEccezione object){
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
		if( !(object instanceof TipoRilevanzaEccezione) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((TipoRilevanzaEccezione)object));
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
		for (TipoRilevanzaEccezione tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoRilevanzaEccezione tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoRilevanzaEccezione tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static TipoRilevanzaEccezione toEnumConstant(String value){
		TipoRilevanzaEccezione res = null;
		if(TipoRilevanzaEccezione.DEBUG.getValue().equals(value)){
			res = TipoRilevanzaEccezione.DEBUG;
		}else if(TipoRilevanzaEccezione.INFO.getValue().equals(value)){
			res = TipoRilevanzaEccezione.INFO;
		}else if(TipoRilevanzaEccezione.WARN.getValue().equals(value)){
			res = TipoRilevanzaEccezione.WARN;
		}else if(TipoRilevanzaEccezione.ERROR.getValue().equals(value)){
			res = TipoRilevanzaEccezione.ERROR;
		}else if(TipoRilevanzaEccezione.FATAL.getValue().equals(value)){
			res = TipoRilevanzaEccezione.FATAL;
		}else if(TipoRilevanzaEccezione.SCONOSCIUTO.getValue().equals(value)){
			res = TipoRilevanzaEccezione.SCONOSCIUTO;
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		TipoRilevanzaEccezione res = null;
		if(TipoRilevanzaEccezione.DEBUG.toString().equals(value)){
			res = TipoRilevanzaEccezione.DEBUG;
		}else if(TipoRilevanzaEccezione.INFO.toString().equals(value)){
			res = TipoRilevanzaEccezione.INFO;
		}else if(TipoRilevanzaEccezione.WARN.toString().equals(value)){
			res = TipoRilevanzaEccezione.WARN;
		}else if(TipoRilevanzaEccezione.ERROR.toString().equals(value)){
			res = TipoRilevanzaEccezione.ERROR;
		}else if(TipoRilevanzaEccezione.FATAL.toString().equals(value)){
			res = TipoRilevanzaEccezione.FATAL;
		}else if(TipoRilevanzaEccezione.SCONOSCIUTO.toString().equals(value)){
			res = TipoRilevanzaEccezione.SCONOSCIUTO;
		}
		return res;
	}
}
