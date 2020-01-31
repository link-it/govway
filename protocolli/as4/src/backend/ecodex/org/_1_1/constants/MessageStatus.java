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
package backend.ecodex.org._1_1.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * Enumeration dell'elemento messageStatus xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "messageStatus")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum MessageStatus implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("READY_TO_SEND")
	READY_TO_SEND ("READY_TO_SEND"),
	@javax.xml.bind.annotation.XmlEnumValue("READY_TO_PULL")
	READY_TO_PULL ("READY_TO_PULL"),
	@javax.xml.bind.annotation.XmlEnumValue("BEING_PULLED")
	BEING_PULLED ("BEING_PULLED"),
	@javax.xml.bind.annotation.XmlEnumValue("SEND_ENQUEUED")
	SEND_ENQUEUED ("SEND_ENQUEUED"),
	@javax.xml.bind.annotation.XmlEnumValue("SEND_IN_PROGRESS")
	SEND_IN_PROGRESS ("SEND_IN_PROGRESS"),
	@javax.xml.bind.annotation.XmlEnumValue("WAITING_FOR_RECEIPT")
	WAITING_FOR_RECEIPT ("WAITING_FOR_RECEIPT"),
	@javax.xml.bind.annotation.XmlEnumValue("ACKNOWLEDGED")
	ACKNOWLEDGED ("ACKNOWLEDGED"),
	@javax.xml.bind.annotation.XmlEnumValue("ACKNOWLEDGED_WITH_WARNING")
	ACKNOWLEDGED_WITH_WARNING ("ACKNOWLEDGED_WITH_WARNING"),
	@javax.xml.bind.annotation.XmlEnumValue("SEND_ATTEMPT_FAILED")
	SEND_ATTEMPT_FAILED ("SEND_ATTEMPT_FAILED"),
	@javax.xml.bind.annotation.XmlEnumValue("SEND_FAILURE")
	SEND_FAILURE ("SEND_FAILURE"),
	@javax.xml.bind.annotation.XmlEnumValue("NOT_FOUND")
	NOT_FOUND ("NOT_FOUND"),
	@javax.xml.bind.annotation.XmlEnumValue("WAITING_FOR_RETRY")
	WAITING_FOR_RETRY ("WAITING_FOR_RETRY"),
	@javax.xml.bind.annotation.XmlEnumValue("RECEIVED")
	RECEIVED ("RECEIVED"),
	@javax.xml.bind.annotation.XmlEnumValue("RECEIVED_WITH_WARNINGS")
	RECEIVED_WITH_WARNINGS ("RECEIVED_WITH_WARNINGS"),
	@javax.xml.bind.annotation.XmlEnumValue("DELETED")
	DELETED ("DELETED"),
	@javax.xml.bind.annotation.XmlEnumValue("DOWNLOADED")
	DOWNLOADED ("DOWNLOADED");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	MessageStatus(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(MessageStatus object){
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
		if( !(object instanceof MessageStatus) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((MessageStatus)object));
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
		for (MessageStatus tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (MessageStatus tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (MessageStatus tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static MessageStatus toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static MessageStatus toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		MessageStatus res = null;
		for (MessageStatus tmp : values()) {
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
		MessageStatus res = null;
		for (MessageStatus tmp : values()) {
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
