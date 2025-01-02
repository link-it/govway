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
package org.openspcoop2.core.plugins.constants;

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
@jakarta.xml.bind.annotation.XmlType(name = "tipo-plugin")
@jakarta.xml.bind.annotation.XmlEnum(String.class)
public enum TipoPlugin implements IEnumeration , Serializable , Cloneable {

	@jakarta.xml.bind.annotation.XmlEnumValue("TRANSAZIONE")
	TRANSAZIONE ("TRANSAZIONE"),
	@jakarta.xml.bind.annotation.XmlEnumValue("RICERCA")
	RICERCA ("RICERCA"),
	@jakarta.xml.bind.annotation.XmlEnumValue("STATISTICA")
	STATISTICA ("STATISTICA"),
	@jakarta.xml.bind.annotation.XmlEnumValue("ALLARME")
	ALLARME ("ALLARME"),
	@jakarta.xml.bind.annotation.XmlEnumValue("CONNETTORE")
	CONNETTORE ("CONNETTORE"),
	@jakarta.xml.bind.annotation.XmlEnumValue("AUTENTICAZIONE")
	AUTENTICAZIONE ("AUTENTICAZIONE"),
	@jakarta.xml.bind.annotation.XmlEnumValue("AUTORIZZAZIONE")
	AUTORIZZAZIONE ("AUTORIZZAZIONE"),
	@jakarta.xml.bind.annotation.XmlEnumValue("AUTORIZZAZIONE_CONTENUTI")
	AUTORIZZAZIONE_CONTENUTI ("AUTORIZZAZIONE_CONTENUTI"),
	@jakarta.xml.bind.annotation.XmlEnumValue("INTEGRAZIONE")
	INTEGRAZIONE ("INTEGRAZIONE"),
	@jakarta.xml.bind.annotation.XmlEnumValue("MESSAGE_HANDLER")
	MESSAGE_HANDLER ("MESSAGE_HANDLER"),
	@jakarta.xml.bind.annotation.XmlEnumValue("SERVICE_HANDLER")
	SERVICE_HANDLER ("SERVICE_HANDLER"),
	@jakarta.xml.bind.annotation.XmlEnumValue("BEHAVIOUR")
	BEHAVIOUR ("BEHAVIOUR"),
	@jakarta.xml.bind.annotation.XmlEnumValue("RATE_LIMITING")
	RATE_LIMITING ("RATE_LIMITING"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TOKEN_DYNAMIC_DISCOVERY")
	TOKEN_DYNAMIC_DISCOVERY ("TOKEN_DYNAMIC_DISCOVERY"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TOKEN_VALIDAZIONE")
	TOKEN_VALIDAZIONE ("TOKEN_VALIDAZIONE"),
	@jakarta.xml.bind.annotation.XmlEnumValue("TOKEN_NEGOZIAZIONE")
	TOKEN_NEGOZIAZIONE ("TOKEN_NEGOZIAZIONE"),
	@jakarta.xml.bind.annotation.XmlEnumValue("ATTRIBUTE_AUTHORITY")
	ATTRIBUTE_AUTHORITY ("ATTRIBUTE_AUTHORITY");
	
	
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
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.getValue());	
	}
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,List<String> fieldsNotCheck){
		if( !(object instanceof TipoPlugin) ){
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
