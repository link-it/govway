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

package org.openspcoop2.message;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.ContentType;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.openspcoop2.message.mtom.MTOMUtilities;

/**
 * SOAPVersion
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum SOAPVersion {
	SOAP11, SOAP12;

	public String getSoapEnvelopeNS() {
		if(this.equals(SOAP11))
			return SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE;
		else
			return SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE;
	}
	
	public String getSoapVersionAsString(){
		if(this.equals(SOAP11))
			return "SOAP 1.1";
		else
			return "SOAP 1.2";
	}
	
	public static final String SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION = "SOAPAction";
	public static final String SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION = "action";
	
	public static final String MEDIA_TYPE_MULTIPART_RELATED = "multipart/related";
	public String[] getContentTypes(){
		if(this.equals(SOAP11))
			return new String [] { SOAPConstants.SOAP_1_1_CONTENT_TYPE , MEDIA_TYPE_MULTIPART_RELATED  };
		else
			return new String [] { SOAPConstants.SOAP_1_2_CONTENT_TYPE , MEDIA_TYPE_MULTIPART_RELATED  };
	}
	public static String[] getKnownContentTypes(){
		return getKnownContentTypes(true, true);
	}
	public static String[] getKnownContentTypes(boolean soap11, boolean soap12){
		List<String> lista = new ArrayList<String>();
		
		if(soap11){
			String [] soap11Array = SOAP11.getContentTypes();
			for (String ct : soap11Array) {
				if(lista.contains(ct)==false){
					lista.add(ct);
				}
			}
		}
		
		if(soap12){
			String [] soap12Array = SOAP12.getContentTypes();
			for (String ct : soap12Array) {
				if(lista.contains(ct)==false){
					lista.add(ct);
				}
			}
		}
		
		return  lista.toArray(new String[1]);
	}
	public static String getKnownContentTypesAsString(){
		return getKnownContentTypesAsString(true, true);
	}
	public static String getKnownContentTypesAsString(boolean soap11, boolean soap12){
		StringBuffer bf = new StringBuffer();
		for (String ct : getKnownContentTypes(soap11,soap12)) {
			if(bf.length()>0){
				bf.append(", ");
			}
			bf.append(ct);
		}
		return bf.toString();
	}
	
	public String getContentTypesAsString(){
		StringBuffer bf = new StringBuffer();
		for (String ct : this.getContentTypes()) {
			if(bf.length()>0){
				bf.append(", ");
			}
			bf.append(ct);
		}
		return bf.toString();
	}
	
	public String getContentTypeForMessageWithoutAttachments(){
		if(this.equals(SOAP11))
			return SOAPConstants.SOAP_1_1_CONTENT_TYPE;
		else
			return SOAPConstants.SOAP_1_2_CONTENT_TYPE;
	}
	
	public static boolean isMtom(Logger log, String cType){
		try{
			ContentType contentType = new ContentType(cType);
			String baseType = contentType.getBaseType().toLowerCase(); 
			String soapContentType = null;
			if(baseType == null)
				soapContentType = null;
			boolean mtom = false;
			if(baseType.equals(Costanti.CONTENT_TYPE_MULTIPART)){
				String typeParam = contentType.getParameter(Costanti.CONTENT_TYPE_MULTIPART_TYPE); 
				if (typeParam == null) {
					throw new SOAPException("Missing '"+Costanti.CONTENT_TYPE_MULTIPART_TYPE+"' parameter in "+Costanti.CONTENT_TYPE_MULTIPART);
				} else {
					soapContentType = typeParam.toLowerCase();
					if(Costanti.CONTENT_TYPE_APPLICATION_XOP_XML.equals(soapContentType)){
						mtom = true;
					}
				} 
			}
			return mtom;
			
		} catch (Exception e) {
			if(log!=null)
				log.error("Unable to retrive SOAP Version: "+e.getMessage(),e);
			else
				e.printStackTrace(System.out);
			return false;
		}
	}
	
	public static boolean isMultipart(Logger log, String cType){
		try{
			ContentType contentType = new ContentType(cType);
			String baseType = contentType.getBaseType().toLowerCase(); 
			if(baseType!=null && baseType.equals(Costanti.CONTENT_TYPE_MULTIPART)){
				return true;
			}
			return false;
			
		} catch (Exception e) {
			if(log!=null)
				log.error("Unable to retrive SOAP Version: "+e.getMessage(),e);
			else
				e.printStackTrace(System.out);
			return false;
		}
	}
	
	public static SOAPVersion getVersioneSoap(Logger log, String cType, boolean logError) {
		try{
			ContentType contentType = new ContentType(cType);
			String baseType = contentType.getBaseType().toLowerCase(); 
			String soapContentType = null;
			if(baseType == null)
				soapContentType = null;
			boolean mtom = false;
			if(baseType.equals(Costanti.CONTENT_TYPE_MULTIPART)){
				String typeParam = contentType.getParameter(Costanti.CONTENT_TYPE_MULTIPART_TYPE); 
				if (typeParam == null) {
					throw new SOAPException("Missing '"+Costanti.CONTENT_TYPE_MULTIPART_TYPE+"' parameter in "+Costanti.CONTENT_TYPE_MULTIPART);
				} else {
					soapContentType = typeParam.toLowerCase();
					if(Costanti.CONTENT_TYPE_APPLICATION_XOP_XML.equals(soapContentType)){
						mtom = true;
					}
				} 
			}
			else {
				soapContentType = baseType;
			}
			
			if (SOAPVersion.SOAP11.getContentTypeForMessageWithoutAttachments().equals(soapContentType)) {
				return SOAPVersion.SOAP11;
			} else if (SOAPVersion.SOAP12.getContentTypeForMessageWithoutAttachments().equals(soapContentType)) {
				return SOAPVersion.SOAP12;
			} else if(mtom) {
				return MTOMUtilities.readSoapVersionFromMtomContentType(contentType);
			}else {
				throw new SOAPException("["+soapContentType+"] non supportato");
			} 
		} catch (Exception e) {
			if(logError){
				if(log!=null)
					log.error("Unable to retrive SOAP Version: "+e.getMessage(),e);
				else
					e.printStackTrace(System.out);
			}
			return null;
		}
	}
}
