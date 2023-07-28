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
@jakarta.xml.bind.annotation.XmlType(name = "errorCode")
@jakarta.xml.bind.annotation.XmlEnum(String.class)
public enum ErrorCode implements IEnumeration , Serializable , Cloneable {

	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0001")
	EBMS_0001 ("EBMS_0001"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0002")
	EBMS_0002 ("EBMS_0002"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0003")
	EBMS_0003 ("EBMS_0003"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0004")
	EBMS_0004 ("EBMS_0004"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0005")
	EBMS_0005 ("EBMS_0005"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0006")
	EBMS_0006 ("EBMS_0006"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0007")
	EBMS_0007 ("EBMS_0007"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0008")
	EBMS_0008 ("EBMS_0008"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0009")
	EBMS_0009 ("EBMS_0009"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0010")
	EBMS_0010 ("EBMS_0010"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0011")
	EBMS_0011 ("EBMS_0011"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0101")
	EBMS_0101 ("EBMS_0101"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0102")
	EBMS_0102 ("EBMS_0102"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0103")
	EBMS_0103 ("EBMS_0103"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0201")
	EBMS_0201 ("EBMS_0201"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0202")
	EBMS_0202 ("EBMS_0202"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0301")
	EBMS_0301 ("EBMS_0301"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0302")
	EBMS_0302 ("EBMS_0302"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0303")
	EBMS_0303 ("EBMS_0303"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0020")
	EBMS_0020 ("EBMS_0020"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0021")
	EBMS_0021 ("EBMS_0021"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0022")
	EBMS_0022 ("EBMS_0022"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0023")
	EBMS_0023 ("EBMS_0023"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0030")
	EBMS_0030 ("EBMS_0030"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0031")
	EBMS_0031 ("EBMS_0031"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0040")
	EBMS_0040 ("EBMS_0040"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0041")
	EBMS_0041 ("EBMS_0041"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0042")
	EBMS_0042 ("EBMS_0042"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0043")
	EBMS_0043 ("EBMS_0043"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0044")
	EBMS_0044 ("EBMS_0044"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0045")
	EBMS_0045 ("EBMS_0045"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0046")
	EBMS_0046 ("EBMS_0046"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0047")
	EBMS_0047 ("EBMS_0047"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0048")
	EBMS_0048 ("EBMS_0048"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0049")
	EBMS_0049 ("EBMS_0049"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0050")
	EBMS_0050 ("EBMS_0050"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0051")
	EBMS_0051 ("EBMS_0051"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0052")
	EBMS_0052 ("EBMS_0052"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0053")
	EBMS_0053 ("EBMS_0053"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0054")
	EBMS_0054 ("EBMS_0054"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0055")
	EBMS_0055 ("EBMS_0055"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0060")
	EBMS_0060 ("EBMS_0060"),
	@jakarta.xml.bind.annotation.XmlEnumValue("EBMS_0065")
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
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.getValue());	
	}
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,List<String> fieldsNotCheck){
		if( !(object instanceof ErrorCode) ){
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
