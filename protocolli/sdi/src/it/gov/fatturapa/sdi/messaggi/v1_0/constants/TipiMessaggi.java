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
package it.gov.fatturapa.sdi.messaggi.v1_0.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**     
 * Enumeration dell'elemento TipiMessaggi xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "TipiMessaggi")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum TipiMessaggi implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("RC")
	RC ("RC"),
	@javax.xml.bind.annotation.XmlEnumValue("NS")
	NS ("NS"),
	@javax.xml.bind.annotation.XmlEnumValue("MC")
	MC ("MC"),
	@javax.xml.bind.annotation.XmlEnumValue("NE")
	NE ("NE"),
	@javax.xml.bind.annotation.XmlEnumValue("MT")
	MT ("MT"),
	@javax.xml.bind.annotation.XmlEnumValue("EC")
	EC ("EC"),
	@javax.xml.bind.annotation.XmlEnumValue("SE")
	SE ("SE"),
	@javax.xml.bind.annotation.XmlEnumValue("DT")
	DT ("DT"),
	@javax.xml.bind.annotation.XmlEnumValue("AT")
	AT ("AT");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	TipiMessaggi(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(TipiMessaggi object){
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
		if( !(object instanceof TipiMessaggi) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((TipiMessaggi)object));
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
		for (TipiMessaggi tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipiMessaggi tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipiMessaggi tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static TipiMessaggi toEnumConstant(String value){
		TipiMessaggi res = null;
		if(TipiMessaggi.RC.getValue().equals(value)){
			res = TipiMessaggi.RC;
		}else if(TipiMessaggi.NS.getValue().equals(value)){
			res = TipiMessaggi.NS;
		}else if(TipiMessaggi.MC.getValue().equals(value)){
			res = TipiMessaggi.MC;
		}else if(TipiMessaggi.NE.getValue().equals(value)){
			res = TipiMessaggi.NE;
		}else if(TipiMessaggi.MT.getValue().equals(value)){
			res = TipiMessaggi.MT;
		}else if(TipiMessaggi.EC.getValue().equals(value)){
			res = TipiMessaggi.EC;
		}else if(TipiMessaggi.SE.getValue().equals(value)){
			res = TipiMessaggi.SE;
		}else if(TipiMessaggi.DT.getValue().equals(value)){
			res = TipiMessaggi.DT;
		}else if(TipiMessaggi.AT.getValue().equals(value)){
			res = TipiMessaggi.AT;
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		TipiMessaggi res = null;
		if(TipiMessaggi.RC.toString().equals(value)){
			res = TipiMessaggi.RC;
		}else if(TipiMessaggi.NS.toString().equals(value)){
			res = TipiMessaggi.NS;
		}else if(TipiMessaggi.MC.toString().equals(value)){
			res = TipiMessaggi.MC;
		}else if(TipiMessaggi.NE.toString().equals(value)){
			res = TipiMessaggi.NE;
		}else if(TipiMessaggi.MT.toString().equals(value)){
			res = TipiMessaggi.MT;
		}else if(TipiMessaggi.EC.toString().equals(value)){
			res = TipiMessaggi.EC;
		}else if(TipiMessaggi.SE.toString().equals(value)){
			res = TipiMessaggi.SE;
		}else if(TipiMessaggi.DT.toString().equals(value)){
			res = TipiMessaggi.DT;
		}else if(TipiMessaggi.AT.toString().equals(value)){
			res = TipiMessaggi.AT;
		}
		return res;
	}
}
