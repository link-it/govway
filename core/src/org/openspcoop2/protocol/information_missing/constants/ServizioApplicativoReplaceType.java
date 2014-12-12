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
package org.openspcoop2.protocol.information_missing.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**     
 * Enumeration dell'elemento ServizioApplicativoReplaceType xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "ServizioApplicativoReplaceType")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum ServizioApplicativoReplaceType implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("RIFERIMENTO")
	RIFERIMENTO ("RIFERIMENTO"),
	@javax.xml.bind.annotation.XmlEnumValue("CONNETTORE")
	CONNETTORE ("CONNETTORE"),
	@javax.xml.bind.annotation.XmlEnumValue("CREDENZIALI_ACCESSO_PDD")
	CREDENZIALI_ACCESSO_PDD ("CREDENZIALI_ACCESSO_PDD"),
	@javax.xml.bind.annotation.XmlEnumValue("ALLINEA_CREDENZIALI_PD")
	ALLINEA_CREDENZIALI_PD ("ALLINEA_CREDENZIALI_PD");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	ServizioApplicativoReplaceType(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(ServizioApplicativoReplaceType object){
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
		if( !(object instanceof ServizioApplicativoReplaceType) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((ServizioApplicativoReplaceType)object));
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
		for (ServizioApplicativoReplaceType tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (ServizioApplicativoReplaceType tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (ServizioApplicativoReplaceType tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static ServizioApplicativoReplaceType toEnumConstant(String value){
		ServizioApplicativoReplaceType res = null;
		if(ServizioApplicativoReplaceType.RIFERIMENTO.getValue().equals(value)){
			res = ServizioApplicativoReplaceType.RIFERIMENTO;
		}else if(ServizioApplicativoReplaceType.CONNETTORE.getValue().equals(value)){
			res = ServizioApplicativoReplaceType.CONNETTORE;
		}else if(ServizioApplicativoReplaceType.CREDENZIALI_ACCESSO_PDD.getValue().equals(value)){
			res = ServizioApplicativoReplaceType.CREDENZIALI_ACCESSO_PDD;
		}else if(ServizioApplicativoReplaceType.ALLINEA_CREDENZIALI_PD.getValue().equals(value)){
			res = ServizioApplicativoReplaceType.ALLINEA_CREDENZIALI_PD;
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		ServizioApplicativoReplaceType res = null;
		if(ServizioApplicativoReplaceType.RIFERIMENTO.toString().equals(value)){
			res = ServizioApplicativoReplaceType.RIFERIMENTO;
		}else if(ServizioApplicativoReplaceType.CONNETTORE.toString().equals(value)){
			res = ServizioApplicativoReplaceType.CONNETTORE;
		}else if(ServizioApplicativoReplaceType.CREDENZIALI_ACCESSO_PDD.toString().equals(value)){
			res = ServizioApplicativoReplaceType.CREDENZIALI_ACCESSO_PDD;
		}else if(ServizioApplicativoReplaceType.ALLINEA_CREDENZIALI_PD.toString().equals(value)){
			res = ServizioApplicativoReplaceType.ALLINEA_CREDENZIALI_PD;
		}
		return res;
	}
}
