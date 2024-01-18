/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import javax.mail.BodyPart;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.rest.MultipartContent;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_soap_impl;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.w3c.dom.Element;

/**
 * MessageUtils
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageUtils {

	public static void registerParseException(OpenSPCoop2Message msg, Throwable e){
		registerParseException(msg, e, false);
	}

	public static void registerParseException(OpenSPCoop2Message o, Throwable e, boolean allException){
		if(o!=null && o.getParseException()==null){
			if(allException){
				o.setParseException(e);
			}
			else{
				Throwable t = org.openspcoop2.message.exception.ParseExceptionUtils.getParseException(e);
				if(t!=null){
					o.setParseException(t);
				}
			}
			
		}
	}
	
	public static SOAPMessage getSOAPMessage(OpenSPCoop2SoapMessage soapMessage, boolean bufferMessage_readOnly, String idTransazione) throws MessageException {
		try {
			if(soapMessage instanceof AbstractOpenSPCoop2Message_soap_impl<?>) {
				AbstractOpenSPCoop2Message_soap_impl<?> soap = (AbstractOpenSPCoop2Message_soap_impl<?>)soapMessage;
				return soap.getContent(bufferMessage_readOnly, idTransazione).getSOAPMessage();
			}
			else {
				return soapMessage.getSOAPMessage();
			}
		}catch(MessageException me) {
			throw me;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	public static SOAPPart getSOAPPart(OpenSPCoop2SoapMessage soapMessage, boolean bufferMessage_readOnly, String idTransazione) throws MessageException {
		try {
			if(soapMessage instanceof AbstractOpenSPCoop2Message_soap_impl<?>) {
				AbstractOpenSPCoop2Message_soap_impl<?> soap = (AbstractOpenSPCoop2Message_soap_impl<?>)soapMessage;
				return soap.getContent(bufferMessage_readOnly, idTransazione).getSOAPPart();
			}
			else {
				return soapMessage.getSOAPPart();
			}
		}catch(MessageException me) {
			throw me;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	public static Element getContentElement(OpenSPCoop2Message msg, boolean checkSoapBodyEmpty, boolean bufferMessage_readOnly, String idTransazione) throws MessageException {
		try {
			if(MessageType.SOAP_11.equals(msg.getMessageType()) || MessageType.SOAP_12.equals(msg.getMessageType())) {
				OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
				if(!soapMessage.isSOAPBodyEmpty() || !checkSoapBodyEmpty) {
					SOAPPart soapPart = getSOAPPart(soapMessage, bufferMessage_readOnly, idTransazione);
					if(soapPart==null){
						throw new MessageException("Messaggio (SOAPPart) non fornito");
					}
					SOAPEnvelope envelope = soapPart.getEnvelope();
					if(envelope==null){
						throw new MessageException("Envelope non fornita");
					}
					return envelope;
				}
			}
			else if (MessageType.XML.equals(msg.getMessageType())){
				OpenSPCoop2RestXmlMessage xmlMsg = msg.castAsRestXml();
				if(xmlMsg.hasContent()) {
					return (Element) xmlMsg.getContent(bufferMessage_readOnly, idTransazione);
				}
			}
			else if(MessageType.MIME_MULTIPART.equals(msg.getMessageType())) {
				// Prendo il primo part che corrisponde ad un xml
				OpenSPCoop2RestMimeMultipartMessage mimeMsg = msg.castAsRestMimeMultipart();
				if(mimeMsg.hasContent()) {
					MultipartContent mc = mimeMsg.getContent();
					if(mc!=null) {
						MimeMultipart mm = mc.getMimeMultipart();
						if(mm!=null && mm.countBodyParts()>0) {
							for (int i = 0; i < mm.countBodyParts(); i++) {
								try {
									BodyPart bodyPart = mm.getBodyPart(i);
									String contentType = bodyPart.getContentType();
									if(contentType!=null) {
										/*
							  			<mediaType messageType="xml">text/xml</mediaType>
						    			<mediaType messageType="xml">application/xml</mediaType>
						    			<mediaType messageType="xml" regExpr="true">.*\+xml</mediaType>
						    			*/
										if(HttpConstants.CONTENT_TYPE_TEXT_XML.equals(contentType)
												||
												HttpConstants.CONTENT_TYPE_XML.equals(contentType)
												||
												RegularExpressionEngine.isMatch(contentType, ".*\\+xml")) {
											InputStream is = bodyPart.getInputStream();
											byte [] xmlBytes = Utilities.getAsByteArray(is);
											return MessageXMLUtils.getInstance(msg.getFactory()).newElement(xmlBytes);
										}
									}
								}catch(Throwable t) {}
							}
						}
					}
				}
			}
			return null;
		}
		catch(MessageException me) {
			throw me;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	public static String getContentString(OpenSPCoop2Message msg, boolean bufferMessage_readOnly, String idTransazione) throws MessageException {
		try {
			if(MessageType.JSON.equals(msg.getMessageType())){
				OpenSPCoop2RestJsonMessage json = msg.castAsRestJson();
				return json.getContent(bufferMessage_readOnly, idTransazione);
			}
			else if(MessageType.MIME_MULTIPART.equals(msg.getMessageType())) {
				// Prendo il primo part che corrisponde ad un json
				OpenSPCoop2RestMimeMultipartMessage mimeMsg = msg.castAsRestMimeMultipart();
				if(mimeMsg.hasContent()) {
					MultipartContent mc = mimeMsg.getContent();
					if(mc!=null) {
						MimeMultipart mm = mc.getMimeMultipart();
						if(mm!=null && mm.countBodyParts()>0) {
							for (int i = 0; i < mm.countBodyParts(); i++) {
								try {
									BodyPart bodyPart = mm.getBodyPart(i);
									String contentType = bodyPart.getContentType();
									if(contentType!=null) {
										/*
							  			<mediaType messageType="json">text/json</mediaType>
										<mediaType messageType="json">text/x-json</mediaType>
						    			<mediaType messageType="json">application/json</mediaType>
						    			<mediaType messageType="json">application/x-json</mediaType>
						    			<mediaType messageType="json" regExpr="true">.*\+json</mediaType>
						    			*/
										if(HttpConstants.CONTENT_TYPE_JSON.equals(contentType)
												||
												RegularExpressionEngine.isMatch(contentType, ".*/json|.*/x-json|.*\\+json")) {
											InputStream is = bodyPart.getInputStream();
											byte [] jsonBytes = Utilities.getAsByteArray(is);
											String charset = ContentTypeUtilities.readCharsetFromContentType(contentType);
											if(charset==null) {
												charset = Charset.UTF_8.getValue();
											}
											return new String(jsonBytes,charset);
										}
									}
								}catch(Throwable t) {}
							}
						}
					}
				}
			}
			return null;
		}
		catch(MessageException me) {
			throw me;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	public static void setUpdatable(OpenSPCoop2Message msg) throws MessageException {
		try {
			if(MessageType.SOAP_11.equals(msg.getMessageType()) || MessageType.SOAP_12.equals(msg.getMessageType())) {
				OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
				if(soapMessage instanceof AbstractOpenSPCoop2Message_soap_impl<?>) {
					AbstractOpenSPCoop2Message_soap_impl<?> soap = (AbstractOpenSPCoop2Message_soap_impl<?>)soapMessage;
					soap.setContentUpdatable();
				}
			}
			else {
				msg.castAsRest().setContentUpdatable();
			}
		}
		catch(MessageException me) {
			throw me;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
}
