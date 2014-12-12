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
package org.openspcoop2.core.config.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;

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
	public boolean equals(ProprietaProtocolloValore object){
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
		ProprietaProtocolloValore res = null;
		if(ProprietaProtocolloValore.TIPO_MITTENTE.getValue().equals(value)){
			res = ProprietaProtocolloValore.TIPO_MITTENTE;
		}else if(ProprietaProtocolloValore.MITTENTE.getValue().equals(value)){
			res = ProprietaProtocolloValore.MITTENTE;
		}else if(ProprietaProtocolloValore.IDENTIFICATIVO_PORTA_MITTENTE.getValue().equals(value)){
			res = ProprietaProtocolloValore.IDENTIFICATIVO_PORTA_MITTENTE;
		}else if(ProprietaProtocolloValore.TIPO_DESTINATARIO.getValue().equals(value)){
			res = ProprietaProtocolloValore.TIPO_DESTINATARIO;
		}else if(ProprietaProtocolloValore.DESTINATARIO.getValue().equals(value)){
			res = ProprietaProtocolloValore.DESTINATARIO;
		}else if(ProprietaProtocolloValore.IDENTIFICATIVO_PORTA_DESTINATARIO.getValue().equals(value)){
			res = ProprietaProtocolloValore.IDENTIFICATIVO_PORTA_DESTINATARIO;
		}else if(ProprietaProtocolloValore.TIPO_SERVIZIO.getValue().equals(value)){
			res = ProprietaProtocolloValore.TIPO_SERVIZIO;
		}else if(ProprietaProtocolloValore.SERVIZIO.getValue().equals(value)){
			res = ProprietaProtocolloValore.SERVIZIO;
		}else if(ProprietaProtocolloValore.VERSIONE_SERVIZIO.getValue().equals(value)){
			res = ProprietaProtocolloValore.VERSIONE_SERVIZIO;
		}else if(ProprietaProtocolloValore.AZIONE.getValue().equals(value)){
			res = ProprietaProtocolloValore.AZIONE;
		}else if(ProprietaProtocolloValore.IDENTIFICATIVO.getValue().equals(value)){
			res = ProprietaProtocolloValore.IDENTIFICATIVO;
		}else if(ProprietaProtocolloValore.IDENTIFICATIVO_CORRELAZIONE_APPLICATIVA.getValue().equals(value)){
			res = ProprietaProtocolloValore.IDENTIFICATIVO_CORRELAZIONE_APPLICATIVA;
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		ProprietaProtocolloValore res = null;
		if(ProprietaProtocolloValore.TIPO_MITTENTE.toString().equals(value)){
			res = ProprietaProtocolloValore.TIPO_MITTENTE;
		}else if(ProprietaProtocolloValore.MITTENTE.toString().equals(value)){
			res = ProprietaProtocolloValore.MITTENTE;
		}else if(ProprietaProtocolloValore.IDENTIFICATIVO_PORTA_MITTENTE.toString().equals(value)){
			res = ProprietaProtocolloValore.IDENTIFICATIVO_PORTA_MITTENTE;
		}else if(ProprietaProtocolloValore.TIPO_DESTINATARIO.toString().equals(value)){
			res = ProprietaProtocolloValore.TIPO_DESTINATARIO;
		}else if(ProprietaProtocolloValore.DESTINATARIO.toString().equals(value)){
			res = ProprietaProtocolloValore.DESTINATARIO;
		}else if(ProprietaProtocolloValore.IDENTIFICATIVO_PORTA_DESTINATARIO.toString().equals(value)){
			res = ProprietaProtocolloValore.IDENTIFICATIVO_PORTA_DESTINATARIO;
		}else if(ProprietaProtocolloValore.TIPO_SERVIZIO.toString().equals(value)){
			res = ProprietaProtocolloValore.TIPO_SERVIZIO;
		}else if(ProprietaProtocolloValore.SERVIZIO.toString().equals(value)){
			res = ProprietaProtocolloValore.SERVIZIO;
		}else if(ProprietaProtocolloValore.VERSIONE_SERVIZIO.toString().equals(value)){
			res = ProprietaProtocolloValore.VERSIONE_SERVIZIO;
		}else if(ProprietaProtocolloValore.AZIONE.toString().equals(value)){
			res = ProprietaProtocolloValore.AZIONE;
		}else if(ProprietaProtocolloValore.IDENTIFICATIVO.toString().equals(value)){
			res = ProprietaProtocolloValore.IDENTIFICATIVO;
		}else if(ProprietaProtocolloValore.IDENTIFICATIVO_CORRELAZIONE_APPLICATIVA.toString().equals(value)){
			res = ProprietaProtocolloValore.IDENTIFICATIVO_CORRELAZIONE_APPLICATIVA;
		}
		return res;
	}
}
