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

package org.openspcoop2.message.xml;

import java.io.IOException;

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
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public class DynamicNamespaceContextFactory extends org.openspcoop2.utils.xml.DynamicNamespaceContextFactory {


	private static DynamicNamespaceContextFactory dncfactory = null;
	private static synchronized void init(){
		if(DynamicNamespaceContextFactory.dncfactory==null){
			DynamicNamespaceContextFactory.dncfactory = new DynamicNamespaceContextFactory();
		}
	}
	public static DynamicNamespaceContextFactory getInstance(){
		if(DynamicNamespaceContextFactory.dncfactory==null){
			DynamicNamespaceContextFactory.init();
		}
		return DynamicNamespaceContextFactory.dncfactory;
	}
	
	
	@Override
	public DynamicNamespaceContext getNamespaceContextFromSoapEnvelope11(
			byte[] soapenvelope) throws SAXException, SOAPException,
			IOException, Exception {
		
		SOAPEnvelope envelope = (SOAPEnvelope) OpenSPCoop2MessageFactory.createSOAPElement(MessageType.SOAP_11, soapenvelope);
		return this.getNamespaceContextFromSoapEnvelope(MessageType.SOAP_11, envelope);
	}

	@Override
	public DynamicNamespaceContext getNamespaceContextFromSoapEnvelope12(
			byte[] soapenvelope) throws SAXException, SOAPException,
			IOException, Exception {
		
		SOAPEnvelope envelope = (SOAPEnvelope) OpenSPCoop2MessageFactory.createSOAPElement(MessageType.SOAP_12, soapenvelope);
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
				prefix = OpenSPCoop2MessageFactory.getFirstChildElement(messageType,body).getPrefix();
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
