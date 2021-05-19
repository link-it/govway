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

package org.openspcoop2.message;

import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_soap_impl;
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
