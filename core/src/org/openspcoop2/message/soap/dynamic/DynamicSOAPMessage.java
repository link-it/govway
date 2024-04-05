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

package org.openspcoop2.message.soap.dynamic;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import jakarta.activation.DataHandler;
import jakarta.xml.soap.AttachmentPart;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPPart;

import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_saaj_impl;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_soap_impl;

/**
 * DynamicSOAPMessage
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicSOAPMessage<T extends AbstractOpenSPCoop2Message_saaj_impl> extends jakarta.xml.soap.SOAPMessage {

	private AbstractOpenSPCoop2Message_soap_impl<T> wrapped;
	public DynamicSOAPMessage(AbstractOpenSPCoop2Message_soap_impl<T> wrapped) {
		this.wrapped = wrapped;
	}

	private jakarta.xml.soap.SOAPMessage _getSOAPMessage(){
		try {
			return this.wrapped.getContent().getSOAPMessage();
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	

	// Metodi ottimizzati per il dynamic behaviour
	
	private DynamicSOAPHeader<T> dynamicSoapHeader;
	
	@Override
	public SOAPHeader getSOAPHeader() throws SOAPException {
		if(this.wrapped.isSoapHeaderOptimizable()) {
			if(this.dynamicSoapHeader==null) {
				this.dynamicSoapHeader = new DynamicSOAPHeader<T>(this.wrapped);
			}
			return this.dynamicSoapHeader;
		}
		return _getSOAPMessage().getSOAPHeader();
	}
	
	private DynamicSOAPPart<T> dynamicSoapPart;
	
	@Override
	public SOAPPart getSOAPPart() {
		if(this.wrapped.isSoapHeaderOptimizable()) {
			if(this.dynamicSoapPart==null) {
				this.dynamicSoapPart = new DynamicSOAPPart<T>(this.wrapped);
			}
			return this.dynamicSoapPart;
		}
		return _getSOAPMessage().getSOAPPart();
	}
	
	public void clearDynamicResources() {
		this.dynamicSoapHeader=null;
		
		if(this.dynamicSoapPart!=null) {
			this.dynamicSoapPart.clearDynamicResources();
		}
		this.dynamicSoapPart=null;
	}
	
	
	// Metodi implementati in SAAJ
	
	@Override
	public void addAttachmentPart(AttachmentPart arg0) {
		_getSOAPMessage().addAttachmentPart(arg0);
	}

	@Override
	public int countAttachments() {
		return _getSOAPMessage().countAttachments();
	}

	@Override
	public AttachmentPart createAttachmentPart() {
		return _getSOAPMessage().createAttachmentPart();
	}

	@Override
	public AttachmentPart getAttachment(SOAPElement arg0) throws SOAPException {
		return _getSOAPMessage().getAttachment(arg0);
	}

	@Override
	public Iterator<AttachmentPart> getAttachments() {
		return _getSOAPMessage().getAttachments();
	}

	@Override
	public Iterator<AttachmentPart> getAttachments(MimeHeaders arg0) {
		return _getSOAPMessage().getAttachments(arg0);
	}

	@Override
	public String getContentDescription() {
		return _getSOAPMessage().getContentDescription();
	}

	@Override
	public MimeHeaders getMimeHeaders() {
		return _getSOAPMessage().getMimeHeaders();
	}

	@Override
	public void removeAllAttachments() {
		_getSOAPMessage().removeAllAttachments();
	}

	@Override
	public void removeAttachments(MimeHeaders arg0) {
		_getSOAPMessage().removeAttachments(arg0);
	}

	@Override
	public void saveChanges() throws SOAPException {
		_getSOAPMessage().saveChanges();
	}

	@Override
	public boolean saveRequired() {
		return _getSOAPMessage().saveRequired();
	}

	@Override
	public void setContentDescription(String arg0) {
		_getSOAPMessage().setContentDescription(arg0);
	}

	@Override
	public void writeTo(OutputStream arg0) throws SOAPException, IOException {
		_getSOAPMessage().writeTo(arg0);
	}

	@Override
	public AttachmentPart createAttachmentPart(DataHandler dataHandler) {
		return _getSOAPMessage().createAttachmentPart(dataHandler);
	}

	@Override
	public AttachmentPart createAttachmentPart(Object content, String contentType) {
		return _getSOAPMessage().createAttachmentPart(content, contentType);
	}

	@Override
	public Object getProperty(String property) throws SOAPException {
		return _getSOAPMessage().getProperty(property);
	}

	@Override
	public SOAPBody getSOAPBody() throws SOAPException {
		return _getSOAPMessage().getSOAPBody();
	}

	@Override
	public void setProperty(String property, Object value) throws SOAPException {
		_getSOAPMessage().setProperty(property, value);
	}
}
