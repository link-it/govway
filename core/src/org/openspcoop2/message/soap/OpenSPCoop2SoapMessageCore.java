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

package org.openspcoop2.message.soap;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;

import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.context.MessageContext;
import org.openspcoop2.message.context.Soap;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;

/**
 * OpenSPCoop2SoapMessageCore
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2SoapMessageCore {

	/* SOAPAction */
	public String soapAction;
	
	/* boolean throwExceptionIfFoundMoreSecurityHeader */
	public boolean throwExceptionIfFoundMoreSecurityHeader = true; // se esistono due header con stesso actor e role viene normalmente lanciata una eccezione

	
	public void copyInstanceFieldValueTo(OpenSPCoop2SoapMessageCore core) {
		core.soapAction = this.soapAction;
		core.throwExceptionIfFoundMoreSecurityHeader = this.throwExceptionIfFoundMoreSecurityHeader;
	}
	
	/* Copy Resources to another instance */
	
	public MessageContext serializeResourcesTo(MessageContext messageContext) throws MessageException{
		Soap soap = new Soap();
		soap.setSoapAction(this.soapAction);
		messageContext.setSoap(soap);
		return messageContext;
	}
	
	public void readResourcesFrom(MessageContext messageContext) throws MessageException{
		
		if(messageContext.getSoap()!=null) {
			
			/* SOAPAction */
			if(messageContext.getSoap().getSoapAction()!=null) {
				this.soapAction = messageContext.getSoap().getSoapAction();
			}
		}
	}
	
	
	/* Elementi SOAP */
	
	public SOAPMessage getSOAPMessage(SOAPMessage soapMessage, MessageType messageType) throws MessageException,MessageNotSupportedException{
		try{
			if(soapMessage!=null){
				soapMessage.setProperty(Costanti.SOAP_MESSAGE_PROPERTY_MESSAGE_TYPE, messageType);
				
				if(MessageType.SOAP_11.equals(messageType)) {
					MimeHeaders mhs = soapMessage.getMimeHeaders();
					mhs.removeHeader(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION);
					mhs.addHeader(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, this.getSoapAction());
				}
			}
			return soapMessage;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	

	/* SOAPAction */
	
	public String getSoapAction(){
		return this.soapAction;
	}
	
	public void setSoapAction(String soapAction){
		this.soapAction = soapAction;
	}
	
		
	
	/* WSSecurity */
	
	public boolean isThrowExceptionIfFoundMoreSecurityHeader() {
		return this.throwExceptionIfFoundMoreSecurityHeader;
	}

	public void setThrowExceptionIfFoundMoreSecurityHeader(boolean throwExceptionIfFoundMoreSecurityHeader) {
		this.throwExceptionIfFoundMoreSecurityHeader = throwExceptionIfFoundMoreSecurityHeader;
	}
}
