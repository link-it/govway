/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.core.api.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;

import org.openspcoop2.core.api.HeaderParameter;
import org.openspcoop2.core.api.HeaderParameters;
import org.openspcoop2.core.api.Invocation;
import org.openspcoop2.core.api.Resource;
import org.openspcoop2.core.api.UrlParameter;
import org.openspcoop2.core.api.UrlParameters;
import org.openspcoop2.core.api.constants.CostantiApi;
import org.openspcoop2.core.api.constants.MessageType;
import org.openspcoop2.core.api.constants.MethodType;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.message.Costanti;
import org.openspcoop2.message.MailcapActivationReader;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.resources.HttpHeaderTypes;
import org.w3c.dom.Element;

/**
 * Imbustamento
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Imbustamento {

	private Invocation apiObject = null;
	private OpenSPCoop2Message message = null;
	
	public Imbustamento(SOAPVersion soapVersion, NotifierInputStreamParams nisParams, InputStream is, MethodType method, String contentType, 
			Properties headerBased, Properties urlBased, String resourcePath, List<String> whiteListHeader) throws UtilsException{
		this(soapVersion, nisParams, is, true, method, contentType, headerBased, urlBased, resourcePath, null, null,whiteListHeader);
	}
	public Imbustamento(SOAPVersion soapVersion, NotifierInputStreamParams nisParams, InputStream is, MethodType method, String contentType, 
			Properties headerBased, Integer responseStatus, String responseMessage, List<String> whiteListHeader) throws UtilsException{
		this(soapVersion, nisParams, is, false, method, contentType, headerBased, null, null, responseStatus, responseMessage,whiteListHeader);
	}
	private Imbustamento(SOAPVersion soapVersion, NotifierInputStreamParams nisParams, InputStream is, boolean isRichiesta, MethodType method, String contentType, 
			Properties headerBased, Properties urlBased, String resourcePath , Integer responseStatus, String responseMessage, List<String> whiteListHeader) throws UtilsException{
		this.apiObject = new Invocation();
		
		this.apiObject.setResource(new Resource());
		if(isRichiesta){
			this.apiObject.getResource().setPath(resourcePath);
		}
		
		this.apiObject.getResource().setMethod(method);
		
		if(isRichiesta){
			this.apiObject.getResource().setType(MessageType.REQUEST);
		}
		else{
			this.apiObject.getResource().setType(MessageType.RESPONSE);
		}
		
		this.apiObject.getResource().setMediaType(contentType);
		
		this.apiObject.getResource().setResponseStatus(responseStatus);
		
		this.apiObject.getResource().setResponseMessage(responseMessage);
		
		if(urlBased!=null && urlBased.size()>0){
			this.apiObject.setUrlParameters(new UrlParameters());
			Enumeration<?> enumUrl = urlBased.keys();
			while (enumUrl.hasMoreElements()) {
				String key = (String) enumUrl.nextElement();
				UrlParameter urlParameter = new UrlParameter();
				urlParameter.setNome(key);
				urlParameter.setBase(urlBased.getProperty(key));
				this.apiObject.getUrlParameters().addUrlParameter(urlParameter);	
			}
		}
		
		List<HeaderParameter> listHeader = new ArrayList<HeaderParameter>();
		HttpHeaderTypes httpHeader = HttpHeaderTypes.getInstance();
		List<String> headers = httpHeader.getHeaders();
		if(headerBased!=null && headerBased.size()>0){
			Enumeration<?> enumHeader = headerBased.keys();
			while (enumHeader.hasMoreElements()) {
				String key = (String) enumHeader.nextElement();
				if(isRichiesta==false){
					if(CostantiConnettori.HEADER_HTTP_RETURN_CODE.equalsIgnoreCase(key)){
						continue;
					}
				}
				if( (headers.contains(key)==false) || (whiteListHeader.contains(key)) ){
					HeaderParameter headerParameter = new HeaderParameter();
					headerParameter.setNome(key);
					headerParameter.setBase(headerBased.getProperty(key));	
					listHeader.add(headerParameter);
				}
			}
		}
		if(listHeader.size()>0){
			this.apiObject.setHeaderParameters(new HeaderParameters());
			this.apiObject.getHeaderParameters().setHeaderParameterList(listHeader);
		}
		
		
		AttachmentPart ap = null;
		if(is!=null){
					
			try{
				byte[] originalContent = Utilities.getAsByteArray(is);
				if(originalContent!=null && originalContent.length>0){
					
					// Verifica se il metodo http lo consente
					// Faccio sollevare l'eccezione durante la fase di sbustamento per far produrre l'errore applicativo ritornato al client
//					if(HttpUtilities.isHttpBodyPermitted(isRichiesta, method.name(), contentType)==false){						
//						if(isRichiesta)
//							throw new Exception("Http don't support body in "+method.name()+" request method");
//						else
//							throw new Exception("Http don't support body in "+method.name()+" response method");
//					}
					
					if(contentType==null){
						contentType = Costanti.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
					}
					
					OpenSPCoop2Message message = SoapUtils.
						imbustamentoMessaggioConAttachment(soapVersion,originalContent,contentType,
								MailcapActivationReader.existsDataContentHandler(contentType),contentType, ProjectInfo.getInstance().getProjectNamespace());
					ap = (AttachmentPart) message.getAttachments().next();
				}
			}catch(Exception e){
				throw new UtilsException("BodyMessage create failed: "+e.getMessage(),e);
			}
			
		}
	
		OpenSPCoop2MessageFactory messageFactory = null;
		try{
			messageFactory = OpenSPCoop2MessageFactory.getMessageFactory();
			this.message = messageFactory.createEmptySOAPMessage(soapVersion,nisParams);
		}catch(Exception e){
			throw new UtilsException("EmptyMessage create failed: "+e.getMessage(),e);
		}
				
		try{
			org.openspcoop2.core.api.utils.serializer.JaxbSerializer serializer = new org.openspcoop2.core.api.utils.serializer.JaxbSerializer();
			byte[] elemByte = serializer.toByteArray(this.apiObject);
			Element elem = XMLUtils.getInstance().newElement(elemByte);
			SOAPElement soapElem = null;
			switch (soapVersion) {
			case SOAP11:
				soapElem = messageFactory.getSoapFactory11().createElement(elem);
				break;
			case SOAP12:
				soapElem = messageFactory.getSoapFactory12().createElement(elem);
				break;
			}
			this.message.getSOAPBody().addChildElement(soapElem);
			
			this.message.addContextProperty(CostantiApi.MESSAGGIO_API, true);
						
		}catch(Exception e){
			throw new UtilsException("ApiMessage create failed: "+e.getMessage(),e);
		}
		
		if(ap!=null){
			try{
				this.message.addAttachmentPart(ap);
			}catch(Exception e){
				throw new UtilsException("ApiMessage (attach) create failed: "+e.getMessage(),e);
			}
		}
		
	} 
	
	public Invocation getApiObject() {
		return this.apiObject;
	}
	public OpenSPCoop2Message getMessage() {
		return this.message;
	}
}
