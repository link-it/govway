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
package org.openspcoop2.protocol.modipa.utils;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPPart;

import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_soap_impl;
import org.openspcoop2.message.soap.SoapUtils;

/**
 * SOAPInfo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SOAPInfo {

	private SOAPHeader header = null;
	private String rootElementNamespace = null;
	private String rootElementPrefix = null;
	private String envelopeNamespace = null;
	
	public SOAPHeader getHeader() {
		return this.header;
	}
	public String getRootElementNamespace() {
		return this.rootElementNamespace;
	}
	public String getRootElementPrefix() {
		return this.rootElementPrefix;
	}
	public String getEnvelopeNamespace() {
		return this.envelopeNamespace;
	}
	
	public void read(boolean useSoapReader, OpenSPCoop2SoapMessage soapMessage, boolean bufferMessage_readOnly, String idTransazione,
			boolean readEnvelopeNamespace, boolean readHeader, boolean readRootElementInfo) throws Exception {
		
		boolean read = false;
		if(useSoapReader && soapMessage instanceof AbstractOpenSPCoop2Message_soap_impl<?>) {
			AbstractOpenSPCoop2Message_soap_impl<?> soap = (AbstractOpenSPCoop2Message_soap_impl<?>)soapMessage;
			if(readHeader) {
				if(soap.isSoapHeaderOptimizable()) {
					this.header = soap.getSOAPHeader();
					if(readRootElementInfo) {
						this.rootElementNamespace = soap.getSoapReader().getRootElementNamespace();
						this.rootElementPrefix = soap.getSoapReader().getRootElementPrefix();
					}
					if(readEnvelopeNamespace) {
						this.envelopeNamespace = soap.getSoapReader().getNamespace();
					}
					read = true;
				}
			}
			else if(readRootElementInfo || readEnvelopeNamespace) {
				if(soap.getSoapReader()!=null && soap.getSoapReader().isParsingComplete()) {
					if(readRootElementInfo) {
						this.rootElementNamespace = soap.getSoapReader().getRootElementNamespace();
						this.rootElementPrefix = soap.getSoapReader().getRootElementPrefix();
					}
					if(readEnvelopeNamespace) {
						this.envelopeNamespace = soap.getSoapReader().getNamespace();
					}
				}
				read = true;
			}
		}
		
		if(!read) {
			SOAPPart soapPart = MessageUtils.getSOAPPart(soapMessage, bufferMessage_readOnly, idTransazione);
			SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
			SOAPBody body = null;
			if(soapEnvelope!=null) {
				if(readHeader) {
					this.header = soapEnvelope.getHeader();
				}
				
				if(readRootElementInfo) {
					body = soapEnvelope.getBody();
					if(readRootElementInfo) {
						this.readSOAPChildBodyInfo(body);
					}
				}
				
				if(readEnvelopeNamespace) {
					this.envelopeNamespace = soapEnvelope.getNamespaceURI();
				}
			}
		}
	}
	
	private void readSOAPChildBodyInfo(SOAPBody soapBody) throws Exception {
		
		if(soapBody==null) {
			throw new Exception("Messaggio senza Body");
		}
		SOAPElement child = SoapUtils.getNotEmptyFirstChildSOAPElement(soapBody);
		if(child==null) {
			throw new Exception("Messaggio senza un contenuto nel Body");
		}
		this.rootElementNamespace = child.getNamespaceURI();
		this.rootElementPrefix = child.getPrefix();
	}
	
}
