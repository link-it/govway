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

import java.io.ByteArrayOutputStream;
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
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeMultipart;

/**
 * Message1_2_FIX_Impl
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Message1_2_FIX_Impl extends com.sun.xml.messaging.saaj.soap.ver1_2.Message1_2Impl {
	
	// La classe Message1_2Impl presenta un problema per il costruttore con SOAPMessage 
	// Non funziona in presenza di messaggi con attachment. 
	// C'e' un bug nell'implementazione della sun che non copia gli attachment
	// In particolare il parametro super.mimePart (protetto non accessibile).
	// Per questo motivo essite la classe 1_2 FIX che utilizza direttamente il messaggio fornito 
	
	private SOAPMessage msg; // messaggio fornito nel costruttore
	private CountingInputStream cis;
	
	
	// Costruttori
	
	public Message1_2_FIX_Impl() {
        super();
    }
    
    public Message1_2_FIX_Impl(boolean isFastInfoset, boolean acceptFastInfoset) {
        super(isFastInfoset, acceptFastInfoset);
    }

    // !!Costruttore che presenta il problema!!
    public Message1_2_FIX_Impl(SOAPMessage msg) {
    	try {
    		this.msg = (com.sun.xml.messaging.saaj.soap.ver1_2.Message1_2Impl) msg;
    	}catch(Throwable t) {
    		// Fix: java.lang.ClassCastException: com.sun.xml.messaging.saaj.soap.ver1_2.Message1_2Impl cannot be cast to com.sun.xml.messaging.saaj.soap.ver1_2.Message1_2Impl
    		// Versioni differenti tra saaj-impl nel software e quello nell'application server
    		if(t instanceof java.lang.ClassCastException) {
    			try {
	    			ByteArrayOutputStream bout = new ByteArrayOutputStream();
	        		msg.writeTo(bout);
	        		bout.flush();
	        		bout.close();
	        		this.msg = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(MessageType.SOAP_12, MessageRole.NONE, 
	        				SoapUtils.getContentType(msg), bout.toByteArray(), null, null).getMessage_throwParseThrowable().castAsSoap().getSOAPMessage();
    			}catch(Throwable tNew) {
    				throw new RuntimeException(new org.openspcoop2.utils.UtilsMultiException(tNew, t));
    			}
    		}
    		else {
    			throw new RuntimeException(t);
    		}
    	}
    }

    public Message1_2_FIX_Impl(MimeHeaders headers, CountingInputStream in)
        throws IOException, SOAPExceptionImpl {
        super(headers, in);
        this.cis = in;
    }

    public Message1_2_FIX_Impl(MimeHeaders headers, ContentType ct, int stat, InputStream in)
        throws SOAPExceptionImpl {
        super(headers,ct,stat,in);
    }
	
    
    // CountingInputStream
    
	public CountingInputStream getCountingInputStream(){
		return this.cis;
	}

	// Wrapper
	
    public MimeMultipart getMimeMultipart(){
    	if (this.msg!= null) {
    		if(this.msg instanceof Message1_2_FIX_Impl)
    			return ((Message1_2_FIX_Impl)this.msg).getMimeMultipart();
    		else
    			return super.mmp;
    	}	
    	else return super.mmp;
    }
    
    // Metodi MessageImpl
    
    @Override
    public String getContentType() {
    	if (this.msg!= null) return _getContentType(this.msg);	
    	else return super.getContentType();
    }
    private String _getContentType(SOAPMessage soapMsg) {
    	try {
    		com.sun.xml.messaging.saaj.soap.ver1_2.Message1_2Impl msg = (com.sun.xml.messaging.saaj.soap.ver1_2.Message1_2Impl) soapMsg;
    		return msg.getContentType();
    	}catch(Throwable t) {
    		// Fix: java.lang.ClassCastException: com.sun.xml.messaging.saaj.soap.ver1_2.Message1_2Impl cannot be cast to com.sun.xml.messaging.saaj.soap.ver1_2.Message1_2Impl
    		// Versioni differenti tra saaj-impl nel software e quello nell'application server
    		return SoapUtils.getContentType(this.msg);
    	}
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
	public Iterator<AttachmentPart> getAttachments() {
		if (this.msg!= null) return this.msg.getAttachments();
		else return super.getAttachments();
	}
	
	@Override
	public Iterator<AttachmentPart> getAttachments(MimeHeaders headers) {
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

}
