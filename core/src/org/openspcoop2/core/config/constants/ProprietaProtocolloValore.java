/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * Enumeration dell'elemento ProprietaProtocolloValore xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "ProprietaProtocolloValore")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum ProprietaProtocolloValore implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("tipoMittente")
	TIPO_MITTENTE ("tipoMittente"),
	@javax.xml.bind.annotation.XmlEnumValue("mittente")
	MITTENTE ("mittente"),
	@javax.xml.bind.annotation.XmlEnumValue("identificativoPortaMittente")
	IDENTIFICATIVO_PORTA_MITTENTE ("identificativoPortaMittente"),
	@javax.xml.bind.annotation.XmlEnumValue("tipoDestinatario")
	TIPO_DESTINATARIO ("tipoDestinatario"),
	@javax.xml.bind.annotation.XmlEnumValue("destinatario")
	DESTINATARIO ("destinatario"),
	@javax.xml.bind.annotation.XmlEnumValue("identificativoPortaDestinatario")
	IDENTIFICATIVO_PORTA_DESTINATARIO ("identificativoPortaDestinatario"),
	@javax.xml.bind.annotation.XmlEnumValue("tipoServizio")
	TIPO_SERVIZIO ("tipoServizio"),
	@javax.xml.bind.annotation.XmlEnumValue("servizio")
	SERVIZIO ("servizio"),
	@javax.xml.bind.annotation.XmlEnumValue("versioneServizio")
	VERSIONE_SERVIZIO ("versioneServizio"),
	@javax.xml.bind.annotation.XmlEnumValue("azione")
	AZIONE ("azione"),
	@javax.xml.bind.annotation.XmlEnumValue("identificativo")
	IDENTIFICATIVO ("identificativo"),
	@javax.xml.bind.annotation.XmlEnumValue("identificativoCorrelazioneApplicativa")
	IDENTIFICATIVO_CORRELAZIONE_APPLICATIVA ("identificativoCorrelazioneApplicativa");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	ProprietaProtocolloValore(String value)
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
		if( !(object instanceof ProprietaProtocolloValore) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((ProprietaProtocolloValore)object));
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
		for (ProprietaProtocolloValore tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (ProprietaProtocolloValore tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (ProprietaProtocolloValore tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static ProprietaProtocolloValore toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static ProprietaProtocolloValore toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		ProprietaProtocolloValore res = null;
		for (ProprietaProtocolloValore tmp : values()) {
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
		ProprietaProtocolloValore res = null;
		for (ProprietaProtocolloValore tmp : values()) {
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
