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

package org.openspcoop2.message.xml;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.xml.sax.SAXException;

/**
 * DynamicNamespaceContextFactory
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageDynamicNamespaceContextFactory extends org.openspcoop2.utils.xml.DynamicNamespaceContextFactory {


	private static HashMap<String, MessageDynamicNamespaceContextFactory> dncfactoryMap = new HashMap<>();
	private static synchronized void init(OpenSPCoop2MessageFactory messageFactory){
		String key = messageFactory.getClass().getName();
		if(!MessageDynamicNamespaceContextFactory.dncfactoryMap.containsKey(key)){
			MessageDynamicNamespaceContextFactory fac = new MessageDynamicNamespaceContextFactory(messageFactory);
			dncfactoryMap.put(key, fac);
		}
	}
	public static MessageDynamicNamespaceContextFactory getInstance(OpenSPCoop2MessageFactory messageFactory){
		String key = messageFactory.getClass().getName();
		if(!MessageDynamicNamespaceContextFactory.dncfactoryMap.containsKey(key)){
			MessageDynamicNamespaceContextFactory.init(messageFactory);
		}
		return MessageDynamicNamespaceContextFactory.dncfactoryMap.get(key);
	}
	
	private OpenSPCoop2MessageFactory messageFactory;
	
	public MessageDynamicNamespaceContextFactory(OpenSPCoop2MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}
	
	
	
	@Override
	public DynamicNamespaceContext getNamespaceContextFromSoapEnvelope11(
			byte[] soapenvelope) throws SAXException, SOAPException,
			IOException, Exception {
		
		SOAPEnvelope envelope = (SOAPEnvelope) OpenSPCoop2MessageFactory.createSOAPElement(this.messageFactory, MessageType.SOAP_11, soapenvelope);
		return this.getNamespaceContextFromSoapEnvelope(MessageType.SOAP_11, envelope);
	}

	@Override
	public DynamicNamespaceContext getNamespaceContextFromSoapEnvelope12(
			byte[] soapenvelope) throws SAXException, SOAPException,
			IOException, Exception {
		
		SOAPEnvelope envelope = (SOAPEnvelope) OpenSPCoop2MessageFactory.createSOAPElement(this.messageFactory, MessageType.SOAP_12, soapenvelope);
		return this.getNamespaceContextFromSoapEnvelope(MessageType.SOAP_12, envelope);
	}

	@Override
	public DynamicNamespaceContext getNamespaceContextFromSoapEnvelope11(
			SOAPEnvelope soapenvelope) throws SAXException, SOAPException {
		return this.getNamespaceContextFromSoapEnvelope(MessageType.SOAP_11, soapenvelope);
	}

	@Override
	public DynamicNamespaceContext getNamespaceContextFromSoapEnvelope12(
			SOAPEnvelope soapenvelope) throws SAXException, SOAPException {
		return this.getNamespaceContextFromSoapEnvelope(MessageType.SOAP_12, soapenvelope);
	}

	private DynamicNamespaceContext getNamespaceContextFromSoapEnvelope(MessageType messageType, javax.xml.soap.SOAPEnvelope soapenvelope) throws SAXException, SOAPException
	{		
		DynamicNamespaceContext dnc = new DynamicNamespaceContext();
		SOAPBody body = soapenvelope.getBody();
		if(body.hasFault()){
			dnc.setSoapFault(true);
		}else if(body.hasChildNodes() == false){
			dnc.setSoapBodyEmpty(true);
		}else{
			String prefix = null;
			try {
				prefix = OpenSPCoop2MessageFactory.getFirstChildElement(this.messageFactory, messageType,body).getPrefix();
			} catch (Throwable e) {
				prefix = body.getFirstChild().getPrefix();
			}
			if(prefix==null)
				prefix = "";
			else if(prefix!=null && !"".equals(prefix)){
				prefix = prefix+":";
			}
			dnc.setPrefixChildSoapBody(prefix);
		}
		dnc.findPrefixNamespace(soapenvelope);
		return dnc;
	}
	
}
