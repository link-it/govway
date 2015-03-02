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
 * Enumeration dell'elemento TipoProfiloCollaborazione xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "TipoProfiloCollaborazione")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum TipoProfiloCollaborazione implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("Oneway")
	ONEWAY ("Oneway"),
	@javax.xml.bind.annotation.XmlEnumValue("Sincrono")
	SINCRONO ("Sincrono"),
	@javax.xml.bind.annotation.XmlEnumValue("AsincronoSimmetrico")
	ASINCRONO_SIMMETRICO ("AsincronoSimmetrico"),
	@javax.xml.bind.annotation.XmlEnumValue("AsincronoAsimmetrico")
	ASINCRONO_ASIMMETRICO ("AsincronoAsimmetrico"),
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
	TipoProfiloCollaborazione(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(TipoProfiloCollaborazione object){
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
		if( !(object instanceof TipoProfiloCollaborazione) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((TipoProfiloCollaborazione)object));
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
		for (TipoProfiloCollaborazione tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoProfiloCollaborazione tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoProfiloCollaborazione tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static TipoProfiloCollaborazione toEnumConstant(String value){
		TipoProfiloCollaborazione res = null;
		if(TipoProfiloCollaborazione.ONEWAY.getValue().equals(value)){
			res = TipoProfiloCollaborazione.ONEWAY;
		}else if(TipoProfiloCollaborazione.SINCRONO.getValue().equals(value)){
			res = TipoProfiloCollaborazione.SINCRONO;
		}else if(TipoProfiloCollaborazione.ASINCRONO_SIMMETRICO.getValue().equals(value)){
			res = TipoProfiloCollaborazione.ASINCRONO_SIMMETRICO;
		}else if(TipoProfiloCollaborazione.ASINCRONO_ASIMMETRICO.getValue().equals(value)){
			res = TipoProfiloCollaborazione.ASINCRONO_ASIMMETRICO;
		}else if(TipoProfiloCollaborazione.SCONOSCIUTO.getValue().equals(value)){
			res = TipoProfiloCollaborazione.SCONOSCIUTO;
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		TipoProfiloCollaborazione res = null;
		if(TipoProfiloCollaborazione.ONEWAY.toString().equals(value)){
			res = TipoProfiloCollaborazione.ONEWAY;
		}else if(TipoProfiloCollaborazione.SINCRONO.toString().equals(value)){
			res = TipoProfiloCollaborazione.SINCRONO;
		}else if(TipoProfiloCollaborazione.ASINCRONO_SIMMETRICO.toString().equals(value)){
			res = TipoProfiloCollaborazione.ASINCRONO_SIMMETRICO;
		}else if(TipoProfiloCollaborazione.ASINCRONO_ASIMMETRICO.toString().equals(value)){
			res = TipoProfiloCollaborazione.ASINCRONO_ASIMMETRICO;
		}else if(TipoProfiloCollaborazione.SCONOSCIUTO.toString().equals(value)){
			res = TipoProfiloCollaborazione.SCONOSCIUTO;
		}
		return res;
	}
}
