/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
 * Enumeration dell'elemento item.errorCode xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "errorCode")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum ErrorCode implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0001")
	EBMS_0001 ("EBMS_0001"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0002")
	EBMS_0002 ("EBMS_0002"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0003")
	EBMS_0003 ("EBMS_0003"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0004")
	EBMS_0004 ("EBMS_0004"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0005")
	EBMS_0005 ("EBMS_0005"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0006")
	EBMS_0006 ("EBMS_0006"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0007")
	EBMS_0007 ("EBMS_0007"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0008")
	EBMS_0008 ("EBMS_0008"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0009")
	EBMS_0009 ("EBMS_0009"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0010")
	EBMS_0010 ("EBMS_0010"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0011")
	EBMS_0011 ("EBMS_0011"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0101")
	EBMS_0101 ("EBMS_0101"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0102")
	EBMS_0102 ("EBMS_0102"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0103")
	EBMS_0103 ("EBMS_0103"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0201")
	EBMS_0201 ("EBMS_0201"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0202")
	EBMS_0202 ("EBMS_0202"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0301")
	EBMS_0301 ("EBMS_0301"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0302")
	EBMS_0302 ("EBMS_0302"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0303")
	EBMS_0303 ("EBMS_0303"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0020")
	EBMS_0020 ("EBMS_0020"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0021")
	EBMS_0021 ("EBMS_0021"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0022")
	EBMS_0022 ("EBMS_0022"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0023")
	EBMS_0023 ("EBMS_0023"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0030")
	EBMS_0030 ("EBMS_0030"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0031")
	EBMS_0031 ("EBMS_0031"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0040")
	EBMS_0040 ("EBMS_0040"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0041")
	EBMS_0041 ("EBMS_0041"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0042")
	EBMS_0042 ("EBMS_0042"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0043")
	EBMS_0043 ("EBMS_0043"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0044")
	EBMS_0044 ("EBMS_0044"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0045")
	EBMS_0045 ("EBMS_0045"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0046")
	EBMS_0046 ("EBMS_0046"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0047")
	EBMS_0047 ("EBMS_0047"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0048")
	EBMS_0048 ("EBMS_0048"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0049")
	EBMS_0049 ("EBMS_0049"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0050")
	EBMS_0050 ("EBMS_0050"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0051")
	EBMS_0051 ("EBMS_0051"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0052")
	EBMS_0052 ("EBMS_0052"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0053")
	EBMS_0053 ("EBMS_0053"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0054")
	EBMS_0054 ("EBMS_0054"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0055")
	EBMS_0055 ("EBMS_0055"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0060")
	EBMS_0060 ("EBMS_0060"),
	@javax.xml.bind.annotation.XmlEnumValue("EBMS_0065")
	EBMS_0065 ("EBMS_0065");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	ErrorCode(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(ErrorCode object){
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
		if( !(object instanceof ErrorCode) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((ErrorCode)object));
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
		for (ErrorCode tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (ErrorCode tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (ErrorCode tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static ErrorCode toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static ErrorCode toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		ErrorCode res = null;
		for (ErrorCode tmp : values()) {
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
		ErrorCode res = null;
		for (ErrorCode tmp : values()) {
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
