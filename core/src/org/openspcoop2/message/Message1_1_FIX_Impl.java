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

package org.openspcoop2.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.io.input.CountingInputStream;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.messaging.saaj.soap.ver1_1.Message1_1Impl;

/**
 * Message1_1_FIX_Impl
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Message1_1_FIX_Impl extends Message1_1Impl {
	private Message1_1Impl msg;
	private CountingInputStream cis;
	public Message1_1_FIX_Impl() {
        super();
    }
    
    public Message1_1_FIX_Impl(boolean isFastInfoset, boolean acceptFastInfoset) {
        super(isFastInfoset, acceptFastInfoset);
    }

    public Message1_1_FIX_Impl(SOAPMessage msg) {
   		this.msg = (Message1_1Impl) msg;
    }

    public Message1_1_FIX_Impl(MimeHeaders headers, CountingInputStream in)
        throws IOException, SOAPExceptionImpl {
        super(headers, in);
        this.cis = in;
    }

    public Message1_1_FIX_Impl(MimeHeaders headers, ContentType ct, int stat, InputStream in)
        throws SOAPExceptionImpl {
        super(headers,ct,stat,in);
    }
	
    
    // Metodi MessageImpl
    @Override
    public String getContentType() {
    	if (this.msg!= null) return this.msg.getContentType();	
    	else return super.getContentType();
    }
    
    // Metodi SOAPMessage

    @Override
	public void addAttachmentPart(AttachmentPart attachment) {
		if (this.msg!= null) this.msg.addAttachmentPart(attachment);	
		else super.addAttachmentPart(attachment);
	}
	
	@Override
	public int countAttachments() {
		if (this.msg!= null) return this.msg.countAttachments();
		else return super.countAttachments();
	}
	
	@Override
	public AttachmentPart createAttachmentPart() {
		if (this.msg!= null) return this.msg.createAttachmentPart();
		else return super.createAttachmentPart();
	}
	
	@Override
	public AttachmentPart getAttachment(SOAPElement element) throws SOAPException {
		if (this.msg!= null) return this.msg.getAttachment(element);
		else return super.getAttachment(element);
	}
	
	@Override
	public Iterator<?> getAttachments() {
		if (this.msg!= null) return this.msg.getAttachments();
		else return super.getAttachments();
	}
	
	@Override
	public Iterator<?> getAttachments(MimeHeaders headers) {
		if (this.msg!= null) return this.msg.getAttachments(headers);
		else return super.getAttachments(headers);
	}
	
	@Override
	public String getContentDescription() {
		if (this.msg!= null) return this.msg.getContentDescription();
		else return super.getContentDescription();
	}
	
	@Override
	public MimeHeaders getMimeHeaders() {
		if (this.msg!= null) return this.msg.getMimeHeaders();
		else return super.getMimeHeaders();
	}
	
	@Override
	public SOAPPart getSOAPPart() {
		if (this.msg!= null) return this.msg.getSOAPPart();
		else return super.getSOAPPart();
	}
	
	@Override
	public void removeAllAttachments() {
		if (this.msg!= null) this.msg.removeAllAttachments();
		else super.removeAllAttachments();
	}
	
	@Override
	public void removeAttachments(MimeHeaders headers) {
		if (this.msg!= null) this.msg.removeAttachments(headers);
		else super.removeAttachments(headers);
	}
	
	@Override
	public void saveChanges() throws SOAPException {
		if (this.msg!= null) this.msg.saveChanges();
		else super.saveChanges();
	}
	
	@Override
	public boolean saveRequired() {
		if (this.msg!= null) return this.msg.saveRequired();
		else return super.saveRequired();
	}
	
	@Override
	public void setContentDescription(String description) {
		if (this.msg!= null) this.msg.setContentDescription(description);
		else super.setContentDescription(description);
	}
	
	@Override
	public void writeTo(OutputStream out) throws SOAPException, IOException {
		if (this.msg!= null) this.msg.writeTo(out);
		else super.writeTo(out);
	}
	
	public CountingInputStream getCountingInputStream(){
		return this.cis;
	}
	
}
