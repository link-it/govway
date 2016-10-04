/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
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
package org.openspcoop2.protocol.engine.constants;

import java.io.Serializable;
import java.net.ProtocolException;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**     
 * Enumeration dell'elemento MethodType xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum IDService implements IEnumeration , Serializable , Cloneable {

	OPENSPCOOP2_SERVLET ("OpenSPCoop2Servlet"),
	
	PORTA_DELEGATA_SOAP ("RicezioneContenutiApplicativiSOAP"),
	PORTA_DELEGATA_INTEGRATION_MANAGER ("RicezioneContenutiApplicativiIntegrationManager"),
	PORTA_DELEGATA_XML_TO_SOAP ("RicezioneContenutiApplicativiHTTP"),
	PORTA_DELEGATA_API ("RicezioneContenutiApplicativiAPI"),
	
	PORTA_APPLICATIVA_SOAP ("RicezioneBusteSOAP"),
	PORTA_APPLICATIVA_API ("RicezioneBusteAPI"),
	
	INTEGRATION_MANAGER_SOAP ("IntegrationManager"),
	
	INTEGRATION_MANAGER_API ("MessageBoxAPI"),
	
	CHECK_PDD ("CheckPdD");
	
	
	// ID: 7 cifre (parlante)
	private final static String ID_OPENSPCOOP_SERVLET = "OP20000";
	private final static String ID_PORTA_DELEGATA = "PD00000";
	private final static String ID_PORTA_APPLICATIVA = "PA00000";
	private final static String ID_PORTA_DELEGATA_IMBUSTAMENTO_SOAP = "PD2SOAP";
	private final static String ID_INTEGRATION_MANAGER = "IM00000";
	private final static String ID_PORTA_DELEGATA_INTEGRATION_MANAGER = "PDIM000";
	private final static String ID_API_PORTA_DELEGATA = "APIPD00";
	private final static String ID_API_PORTA_APPLICATIVA = "APIPA00";
	private final static String ID_API_MESSAGE_BOX = "APIMB00";
	private final static String ID_CHECK_PDD = "CHKPDD0";
	
	public String getCode() throws ProtocolException{
		switch (this) {
		case OPENSPCOOP2_SERVLET:
			return ID_OPENSPCOOP_SERVLET;
		case PORTA_DELEGATA_SOAP:
			return ID_PORTA_DELEGATA;
		case PORTA_DELEGATA_XML_TO_SOAP:
			return ID_PORTA_DELEGATA_IMBUSTAMENTO_SOAP;
		case PORTA_DELEGATA_INTEGRATION_MANAGER:
			return ID_PORTA_DELEGATA_INTEGRATION_MANAGER;
		case PORTA_DELEGATA_API:
			return ID_API_PORTA_DELEGATA;
		case PORTA_APPLICATIVA_SOAP:
			return ID_PORTA_APPLICATIVA;
		case PORTA_APPLICATIVA_API:
			return ID_API_PORTA_APPLICATIVA;
		case INTEGRATION_MANAGER_SOAP:
			return ID_INTEGRATION_MANAGER;
		case INTEGRATION_MANAGER_API:
			return ID_API_MESSAGE_BOX;
		case CHECK_PDD:
			return ID_CHECK_PDD;
		}
		throw new ProtocolException("Service ["+this.name()+"] unsupported");
	}
	
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	IDService(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(IDService object){
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
		if( !(object instanceof IDService) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((IDService)object));
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
		for (IDService tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (IDService tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (IDService tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static IDService toEnumConstant(String value){
		IDService res = null;
		for (IDService tmp : values()) {
			if(tmp.getValue().equals(value)){
				res = tmp;
				break;
			}
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		IDService res = null;
		for (IDService tmp : values()) {
			if(tmp.toString().equals(value)){
				res = tmp;
				break;
			}
		}
		return res;
	}
}
