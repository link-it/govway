/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.modipa.validator;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.message.soap.wsaddressing.WSAddressingHeader;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.wss4j.MessageSecurityReceiver_wss4j;

/**
 * ModISOAPSecurity
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModISOAPSecurity {

	private SOAPHeaderElement securityHeader;
	private WSAddressingHeader wsAddressingHeader;
	private String wsuIdBodyRef;
	
	private MessageSecurityReceiver_wss4j wss4jSignature;
	private MessageSecurityContext messageSecurityContext;
	private List<Reference> elementsToClean;
	
	public SOAPHeaderElement getSecurityHeader() {
		return this.securityHeader;
	}
	public void setSecurityHeader(SOAPHeaderElement securityHeader) {
		this.securityHeader = securityHeader;
	}
	public WSAddressingHeader getWsAddressingHeader() {
		return this.wsAddressingHeader;
	}
	public void setWsAddressingHeader(WSAddressingHeader wsAddressingHeader) {
		this.wsAddressingHeader = wsAddressingHeader;
	}
	public String getWsuIdBodyRef() {
		return this.wsuIdBodyRef;
	}
	public void setWsuIdBodyRef(String wsuIdBodyRef) {
		this.wsuIdBodyRef = wsuIdBodyRef;
	}
	public List<Reference> getElementsToClean() {
		return this.elementsToClean;
	}
	public void setElementsToClean(List<Reference> elementsToClean) {
		this.elementsToClean = elementsToClean;
	}
	public MessageSecurityReceiver_wss4j getWss4jSignature() {
		return this.wss4jSignature;
	}
	public void setWss4jSignature(MessageSecurityReceiver_wss4j wss4jSignature) {
		this.wss4jSignature = wss4jSignature;
	}
	public MessageSecurityContext getMessageSecurityContext() {
		return this.messageSecurityContext;
	}
	public void setMessageSecurityContext(MessageSecurityContext messageSecurityContext) {
		this.messageSecurityContext = messageSecurityContext;
	}
	
	public void clean(OpenSPCoop2SoapMessage soapMessage) throws SecurityException, MessageException, MessageNotSupportedException {
		boolean detachValue = true; // per default l'header WSS viene eliminato
		boolean removeAllIdRefValue = true;
		this.wss4jSignature.cleanDirtyElements(this.messageSecurityContext, soapMessage, this.elementsToClean, detachValue, removeAllIdRefValue);
		
		if(this.wsAddressingHeader!=null) {
			
			SOAPHeader header = soapMessage.getSOAPHeader();
			
			if(header!=null) {
				if(this.wsAddressingHeader.getTo()!=null) {
					header.removeChild(this.wsAddressingHeader.getTo());
				}
				if(this.wsAddressingHeader.getFrom()!=null) {
					header.removeChild(this.wsAddressingHeader.getFrom());
				}
				if(this.wsAddressingHeader.getAction()!=null) {
					header.removeChild(this.wsAddressingHeader.getAction());
				}
				if(this.wsAddressingHeader.getId()!=null) {
					header.removeChild(this.wsAddressingHeader.getId());
				}
				if(this.wsAddressingHeader.getRelatesTo()!=null) {
					header.removeChild(this.wsAddressingHeader.getRelatesTo());
				}
				if(this.wsAddressingHeader.getReplyTo()!=null) {
					header.removeChild(this.wsAddressingHeader.getReplyTo());
				}
				if(this.wsAddressingHeader.getFaultTo()!=null) {
					header.removeChild(this.wsAddressingHeader.getFaultTo());
				}
			}
		}
	}
	
	public SOAPEnvelope buildTraccia(MessageType type) throws MessageException {
		
		try {
			
			OpenSPCoop2Message msg = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createEmptyMessage(type, MessageRole.NONE);
			OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
			
			SOAPHeader header = soapMessage.getSOAPHeader();
			if(header==null) {
				header = soapMessage.getSOAPPart().getEnvelope().addHeader();
			}
			
			boolean add = false;
			
			if(this.securityHeader!=null) {
				header.addChildElement(this.securityHeader);
				add = true;
			}
			
			if(this.wsAddressingHeader!=null) {
				if(this.wsAddressingHeader.getTo()!=null) {
					header.addChildElement(this.wsAddressingHeader.getTo());
					add = true;
				}
				if(this.wsAddressingHeader.getFrom()!=null) {
					header.addChildElement(this.wsAddressingHeader.getFrom());
					add = true;
				}
				if(this.wsAddressingHeader.getAction()!=null) {
					header.addChildElement(this.wsAddressingHeader.getAction());
					add = true;
				}
				if(this.wsAddressingHeader.getId()!=null) {
					header.addChildElement(this.wsAddressingHeader.getId());
					add = true;
				}
				if(this.wsAddressingHeader.getRelatesTo()!=null) {
					header.addChildElement(this.wsAddressingHeader.getRelatesTo());
					add = true;
				}
				if(this.wsAddressingHeader.getReplyTo()!=null) {
					header.addChildElement(this.wsAddressingHeader.getReplyTo());
					add = true;
				}
				if(this.wsAddressingHeader.getFaultTo()!=null) {
					header.addChildElement(this.wsAddressingHeader.getFaultTo());
					add = true;
				}
			}
			
			SOAPBody body = soapMessage.getSOAPBody();
			if(body==null) {
				body = soapMessage.getSOAPPart().getEnvelope().addBody();
			}
			if(this.wsuIdBodyRef!=null) {
				QName qname = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id", "wsu");
				body.addAttribute(qname, this.wsuIdBodyRef);
			}
			body.setTextContent("...");
			
			if(add) {
				return soapMessage.getSOAPPart().getEnvelope();
			}
			else {
				return null;
			}
			
		}catch(Exception e) {
			throw new MessageException(e.getMessage(), e);
		}
		
	}
}
