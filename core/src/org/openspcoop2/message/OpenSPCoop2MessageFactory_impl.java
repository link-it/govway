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

package org.openspcoop2.message;

import java.io.InputStream;

import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPConnectionFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.rest.OpenSPCoop2Message_binary_impl;
import org.openspcoop2.message.rest.OpenSPCoop2Message_json_impl;
import org.openspcoop2.message.rest.OpenSPCoop2Message_mimeMultipart_impl;
import org.openspcoop2.message.rest.OpenSPCoop2Message_xml_impl;
import org.openspcoop2.message.soap.OpenSPCoop2Message_saaj_11_impl;
import org.openspcoop2.message.soap.OpenSPCoop2Message_saaj_12_impl;
import org.openspcoop2.message.soap.OpenSPCoop2Message_soap11_impl;
import org.openspcoop2.message.soap.OpenSPCoop2Message_soap12_impl;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Factory per la costruzione di messaggi OpenSPCoop2Message. 
 * 
 * @author Lorenzo Nardi (nardi@link.it)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class OpenSPCoop2MessageFactory_impl extends OpenSPCoop2MessageFactory {

	private static boolean soapPassthroughImpl = true;
	public static void setSoapPassthroughImpl(boolean soapPassthroughImpl) {
		OpenSPCoop2MessageFactory_impl.soapPassthroughImpl = soapPassthroughImpl;
	}

	
	// ********** SOAP - FACTORY *************

	@Override
	protected synchronized void initSoapFactory() {
		try{
            if(OpenSPCoop2MessageFactory.soapFactory11==null || OpenSPCoop2MessageFactory.soapFactory12==null){
            	OpenSPCoop2MessageFactory.initSoapFactory11(new com.sun.xml.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl());
            	OpenSPCoop2MessageFactory.initSoapFactory12(new com.sun.xml.messaging.saaj.soap.ver1_2.SOAPFactory1_2Impl());
            }
        }catch(Exception e){
                System.out.println("ERRORE: "+e.getMessage());
        }
	}

	@Override
	protected synchronized void initSoapMessageFactory() throws SOAPException {
		if(OpenSPCoop2MessageFactory.soapMessageFactory==null){
            OpenSPCoop2MessageFactory.initSoapMessageFactory(new com.sun.xml.messaging.saaj.soap.MessageFactoryImpl());
		}
	}
	
	@Override
	public SOAPConnectionFactory getSOAPConnectionFactory()
			throws SOAPException {
		return new com.sun.xml.messaging.saaj.client.p2p.HttpSOAPConnectionFactory();
	}
	
	
	
	
	// ********** SAAJ *************
	
	@Override
	public String getDocumentBuilderFactoryClass() {
		return org.apache.xerces.jaxp.DocumentBuilderFactoryImpl.class.getName();
	}
	
	@Override
	public String getSAXParserFactoryClass() {
		return org.apache.xerces.jaxp.SAXParserFactoryImpl.class.getName();
	}

    @Override
	public Element convertoForXPathSearch(Element contenutoAsElement){
    	return contenutoAsElement;
    }
    
    @Override
	public void normalizeDocument(Document document){
    	document.normalizeDocument();
    }
	
	
    
    
    // ********** OpenSPCoop2Message builder *************
	
	@Override
	public OpenSPCoop2Message _createMessage(MessageType messageType, SOAPMessage soapMsg) throws MessageException {
		OpenSPCoop2Message msg = null;
		if(MessageType.SOAP_11.equals(messageType)){
			msg = new OpenSPCoop2Message_saaj_11_impl(this, soapMsg);
		} else if(MessageType.SOAP_12.equals(messageType)){
			msg = new OpenSPCoop2Message_saaj_12_impl(this, soapMsg);
		}
		else{
			throw new MessageException("Message Type ["+messageType+"] unsupported");
		}
		
		return msg;
	}
	
	@Override
	public OpenSPCoop2Message _createEmptyMessage(MessageType messageType) throws MessageException  {
		OpenSPCoop2Message msg = null;
		switch (messageType) {
			case SOAP_11:
				msg = new OpenSPCoop2Message_saaj_11_impl(this);
				break;
			case SOAP_12:
				msg = new OpenSPCoop2Message_saaj_12_impl(this);
				break;
			case XML:
				msg = new OpenSPCoop2Message_xml_impl(this);
				break;
			case JSON:
				msg = new OpenSPCoop2Message_json_impl(this);
				break;
			case BINARY:
				msg = new OpenSPCoop2Message_binary_impl(this);
				break;
			case MIME_MULTIPART:
				msg = new OpenSPCoop2Message_mimeMultipart_impl(this);
				break;
		}
		
        return msg;
	}

	@Override
	protected OpenSPCoop2Message _createMessage(MessageType messageType, TransportRequestContext requestContext, InputStream is, 
			AttachmentsProcessingMode attachmentsProcessingMode, long overhead,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) throws MessageException {
		
		return this._createMessageEngine(messageType, requestContext, is, attachmentsProcessingMode, overhead, soapStreamReader);
	}
	
	@Override
	protected OpenSPCoop2Message _createMessage(MessageType messageType, TransportResponseContext responseContext, InputStream is, 
			AttachmentsProcessingMode attachmentsProcessingMode, long overhead,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) throws MessageException {
		
		return this._createMessageEngine(messageType, responseContext, is, attachmentsProcessingMode, overhead, soapStreamReader);
	}
	
	@Override
	protected OpenSPCoop2Message _createMessage(MessageType messageType, String contentType, InputStream is,  
			AttachmentsProcessingMode attachmentsProcessingMode, long overhead,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) throws MessageException{
		
		return this._createMessageEngine(messageType, contentType, is, attachmentsProcessingMode, overhead, soapStreamReader);
	}
	
	private OpenSPCoop2Message _createMessageEngine(MessageType messageType, Object context, InputStream is, 
			AttachmentsProcessingMode attachmentsProcessingMode, long overhead,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) throws MessageException {
		
		try{
		
			OpenSPCoop2Message msg = null;
			
			String contentType = null;
			TransportRequestContext transportRequestContext = null; 
			TransportResponseContext transportResponseContext = null;
			if(context instanceof TransportRequestContext){
				transportRequestContext = (TransportRequestContext) context;
				contentType = transportRequestContext.getHeaderFirstValue(HttpConstants.CONTENT_TYPE);
			}
			else if(context instanceof TransportResponseContext){
				transportResponseContext = (TransportResponseContext) context;
				contentType = transportResponseContext.getHeaderFirstValue(HttpConstants.CONTENT_TYPE);
			}
			else if(context instanceof String){
				contentType = (String) context;
			}
			else{
				if(context==null) {
					if(!MessageType.BINARY.equals(messageType)) {
						throw new MessageException("Unsupported Empty Context for message '"+messageType+"' (No Content Type?)");
					}
				}
				else {
					throw new MessageException("Unsupported Context ["+context+"]");
				}
			}
			
			switch (messageType) {
				case SOAP_11:
				case SOAP_12:
					
					MimeHeaders mhs = new MimeHeaders();
					if(contentType!=null){
						mhs.setHeader(HttpConstants.CONTENT_TYPE, contentType);
					}
					if(OpenSPCoop2MessageFactory_impl.soapPassthroughImpl) {
						if(MessageType.SOAP_11.equals(messageType)){
							msg = new OpenSPCoop2Message_soap11_impl(this, mhs, is, overhead, soapStreamReader);
						}
						else{
							msg = new OpenSPCoop2Message_soap12_impl(this, mhs, is, overhead, soapStreamReader);
						}
					}
					else {
						if(MessageType.SOAP_11.equals(messageType)){
							msg = new OpenSPCoop2Message_saaj_11_impl(this, mhs, is);
							((OpenSPCoop2Message_saaj_11_impl)msg).initialize(overhead);
						}
						else{
							msg = new OpenSPCoop2Message_saaj_12_impl(this, mhs, is);
							((OpenSPCoop2Message_saaj_12_impl)msg).initialize(overhead);
						}
					}
					break;
				case XML:
					msg = new OpenSPCoop2Message_xml_impl(this, is,contentType);
					break;
				case JSON:
					msg = new OpenSPCoop2Message_json_impl(this, is,contentType);
					break;
				case BINARY:
					msg = new OpenSPCoop2Message_binary_impl(this, is,contentType);
					break;
				case MIME_MULTIPART:
					msg = new OpenSPCoop2Message_mimeMultipart_impl(this, is,contentType);
					break;
			}
					
	        return msg;
	        
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}


	
}
