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
package org.openspcoop2.core.transazioni.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * Enumeration dell'elemento tipo-messaggio xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "tipo-messaggio")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum TipoMessaggio implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("RichiestaIngresso")
	RICHIESTA_INGRESSO ("RichiestaIngresso"),
	@javax.xml.bind.annotation.XmlEnumValue("RichiestaUscita")
	RICHIESTA_USCITA ("RichiestaUscita"),
	@javax.xml.bind.annotation.XmlEnumValue("RispostaIngresso")
	RISPOSTA_INGRESSO ("RispostaIngresso"),
	@javax.xml.bind.annotation.XmlEnumValue("RispostaUscita")
	RISPOSTA_USCITA ("RispostaUscita"),
	@javax.xml.bind.annotation.XmlEnumValue("RichiestaIngressoDumpBinario")
	RICHIESTA_INGRESSO_DUMP_BINARIO ("RichiestaIngressoDumpBinario"),
	@javax.xml.bind.annotation.XmlEnumValue("RichiestaUscitaDumpBinario")
	RICHIESTA_USCITA_DUMP_BINARIO ("RichiestaUscitaDumpBinario"),
	@javax.xml.bind.annotation.XmlEnumValue("RispostaIngressoDumpBinario")
	RISPOSTA_INGRESSO_DUMP_BINARIO ("RispostaIngressoDumpBinario"),
	@javax.xml.bind.annotation.XmlEnumValue("RispostaUscitaDumpBinario")
	RISPOSTA_USCITA_DUMP_BINARIO ("RispostaUscitaDumpBinario"),
	@javax.xml.bind.annotation.XmlEnumValue("IntegrationManager")
	INTEGRATION_MANAGER ("IntegrationManager");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	TipoMessaggio(String value)
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
		if( !(object instanceof TipoMessaggio) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((TipoMessaggio)object));
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
		for (TipoMessaggio tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoMessaggio tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoMessaggio tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static TipoMessaggio toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static TipoMessaggio toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		TipoMessaggio res = null;
		for (TipoMessaggio tmp : values()) {
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
		TipoMessaggio res = null;
		for (TipoMessaggio tmp : values()) {
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
