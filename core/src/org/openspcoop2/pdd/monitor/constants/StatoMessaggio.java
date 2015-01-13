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
package org.openspcoop2.pdd.monitor.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**     
 * Enumeration dell'elemento StatoMessaggio xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "StatoMessaggio")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum StatoMessaggio implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("consegna")
	CONSEGNA ("consegna"),
	@javax.xml.bind.annotation.XmlEnumValue("spedizione")
	SPEDIZIONE ("spedizione"),
	@javax.xml.bind.annotation.XmlEnumValue("processamento")
	PROCESSAMENTO ("processamento"),
	@javax.xml.bind.annotation.XmlEnumValue("cancellato")
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
	public boolean equals(StatoMessaggio object){
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
		if( !(object instanceof StatoMessaggio) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((StatoMessaggio)object));
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
		StatoMessaggio res = null;
		if(StatoMessaggio.CONSEGNA.getValue().equals(value)){
			res = StatoMessaggio.CONSEGNA;
		}else if(StatoMessaggio.SPEDIZIONE.getValue().equals(value)){
			res = StatoMessaggio.SPEDIZIONE;
		}else if(StatoMessaggio.PROCESSAMENTO.getValue().equals(value)){
			res = StatoMessaggio.PROCESSAMENTO;
		}else if(StatoMessaggio.CANCELLATO.getValue().equals(value)){
			res = StatoMessaggio.CANCELLATO;
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		StatoMessaggio res = null;
		if(StatoMessaggio.CONSEGNA.toString().equals(value)){
			res = StatoMessaggio.CONSEGNA;
		}else if(StatoMessaggio.SPEDIZIONE.toString().equals(value)){
			res = StatoMessaggio.SPEDIZIONE;
		}else if(StatoMessaggio.PROCESSAMENTO.toString().equals(value)){
			res = StatoMessaggio.PROCESSAMENTO;
		}else if(StatoMessaggio.CANCELLATO.toString().equals(value)){
			res = StatoMessaggio.CANCELLATO;
		}
		return res;
	}
}
