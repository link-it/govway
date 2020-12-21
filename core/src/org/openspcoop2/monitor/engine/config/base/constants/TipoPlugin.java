/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.config.base.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * Enumeration dell'elemento tipo-plugin xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "tipo-plugin")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum TipoPlugin implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("TRANSAZIONE")
	TRANSAZIONE ("TRANSAZIONE"),
	@javax.xml.bind.annotation.XmlEnumValue("RICERCA")
	RICERCA ("RICERCA"),
	@javax.xml.bind.annotation.XmlEnumValue("STATISTICA")
	STATISTICA ("STATISTICA"),
	@javax.xml.bind.annotation.XmlEnumValue("ALLARME")
	ALLARME ("ALLARME"),
	@javax.xml.bind.annotation.XmlEnumValue("CONNETTORE")
	CONNETTORE ("CONNETTORE"),
	@javax.xml.bind.annotation.XmlEnumValue("AUTENTICAZIONE")
	AUTENTICAZIONE ("AUTENTICAZIONE"),
	@javax.xml.bind.annotation.XmlEnumValue("AUTORIZZAZIONE")
	AUTORIZZAZIONE ("AUTORIZZAZIONE"),
	@javax.xml.bind.annotation.XmlEnumValue("AUTORIZZAZIONE_CONTENUTI")
	AUTORIZZAZIONE_CONTENUTI ("AUTORIZZAZIONE_CONTENUTI"),
	@javax.xml.bind.annotation.XmlEnumValue("INTEGRAZIONE")
	INTEGRAZIONE ("INTEGRAZIONE"),
	@javax.xml.bind.annotation.XmlEnumValue("MESSAGE_HANDLER")
	MESSAGE_HANDLER ("MESSAGE_HANDLER"),
	@javax.xml.bind.annotation.XmlEnumValue("SERVICE_HANDLER")
	SERVICE_HANDLER ("SERVICE_HANDLER"),
	@javax.xml.bind.annotation.XmlEnumValue("BEHAVIOUR")
	BEHAVIOUR ("BEHAVIOUR"),
	@javax.xml.bind.annotation.XmlEnumValue("RATE_LIMITING")
	RATE_LIMITING ("RATE_LIMITING");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	TipoPlugin(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(TipoPlugin object){
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
		if( !(object instanceof TipoPlugin) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((TipoPlugin)object));
	}
	public String toString(boolean reportHTML){
		return toString();
	}
  	public String toString(boolean reportHTML,List<String> fieldsNotIncluded){
  		return toString();
  	}
  	public String diff(Object object,StringBuilder bf,boolean reportHTML){
		return bf.toString();
	}
	public String diff(Object object,StringBuilder bf,boolean reportHTML,List<String> fieldsNotIncluded){
		return bf.toString();
	}
	
	
	/** Utilities */
	
	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoPlugin tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoPlugin tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoPlugin tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static TipoPlugin toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static TipoPlugin toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		TipoPlugin res = null;
		for (TipoPlugin tmp : values()) {
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
		TipoPlugin res = null;
		for (TipoPlugin tmp : values()) {
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
