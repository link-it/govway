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
package org.openspcoop2.protocol.sdk.constants;

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
	
	PORTA_DELEGATA ("RicezioneContenutiApplicativi"),
	PORTA_DELEGATA_NIO ("RicezioneContenutiApplicativiNIO"),
	
	PORTA_DELEGATA_XML_TO_SOAP ("RicezioneContenutiApplicativiHTTP"),
	PORTA_DELEGATA_XML_TO_SOAP_NIO ("RicezioneContenutiApplicativiHTTPNIO"),
	
	PORTA_DELEGATA_INTEGRATION_MANAGER ("RicezioneContenutiApplicativiIntegrationManager"),
	
	PORTA_APPLICATIVA ("RicezioneBuste"),
	PORTA_APPLICATIVA_NIO ("RicezioneBusteNIO"),
	
	INTEGRATION_MANAGER_SOAP ("IntegrationManager"),
	
	CHECK_PDD ("Check"),
	PROXY ("Proxy");
	
	
	// ID: 7 cifre (parlante)
	private final static String ID_OPENSPCOOP_SERVLET = "OP20000";
	
	private final static String ID_PORTA_DELEGATA = "PD00000";
	private final static String ID_PORTA_DELEGATA_NIO = "PDNIO00";
	
	private final static String ID_PORTA_DELEGATA_IMBUSTAMENTO_SOAP = "PD2SOAP";
	private final static String ID_PORTA_DELEGATA_IMBUSTAMENTO_SOAP_NIO = "PD2SOAN";
	
	private final static String ID_PORTA_DELEGATA_INTEGRATION_MANAGER = "PDIM000";
	
	private final static String ID_PORTA_APPLICATIVA = "PA00000";
	private final static String ID_PORTA_APPLICATIVA_NIO = "PANIO00";
	
	private final static String ID_INTEGRATION_MANAGER = "IM00000";
	
	private final static String ID_CHECK_PDD = "CHKPDD0";
	private final static String ID_PROXY = "PROXY00";
	
	public String getCode() throws ProtocolException{
		switch (this) {
		
		case OPENSPCOOP2_SERVLET:
			return ID_OPENSPCOOP_SERVLET;
			
		case PORTA_DELEGATA:
			return ID_PORTA_DELEGATA;
		case PORTA_DELEGATA_NIO:
			return ID_PORTA_DELEGATA_NIO;
			
		case PORTA_DELEGATA_XML_TO_SOAP:
			return ID_PORTA_DELEGATA_IMBUSTAMENTO_SOAP;
		case PORTA_DELEGATA_XML_TO_SOAP_NIO:
			return ID_PORTA_DELEGATA_IMBUSTAMENTO_SOAP_NIO;
			
		case PORTA_DELEGATA_INTEGRATION_MANAGER:
			return ID_PORTA_DELEGATA_INTEGRATION_MANAGER;
			
		case PORTA_APPLICATIVA:
			return ID_PORTA_APPLICATIVA;
		case PORTA_APPLICATIVA_NIO:
			return ID_PORTA_APPLICATIVA_NIO;
			
		case INTEGRATION_MANAGER_SOAP:
			return ID_INTEGRATION_MANAGER;
			
		case CHECK_PDD:
			return ID_CHECK_PDD;
		case PROXY:
			return ID_PROXY;
			
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
