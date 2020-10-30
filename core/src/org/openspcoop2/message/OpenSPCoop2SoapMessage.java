/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.soap.SOAPFaultCode;
import org.openspcoop2.message.soap.mtom.MtomXomPackageInfo;
import org.openspcoop2.message.soap.mtom.MtomXomReference;
import org.openspcoop2.message.soap.reference.Reference;
import org.w3c.dom.Element;

/**
 * Interfaccia del messaggio OpenSPCoop
 * 
 * Oltre ai metodi SAAJ usati per le operazioni di lettura e scrittura 
 * del messaggio XML, fornisce alcuni metodi di utilita' per facilitare
 * un eventuale cambio di implementazione
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface OpenSPCoop2SoapMessage extends OpenSPCoop2Message {
	
	/* Elementi SOAP */
	
	public SOAPMessage getSOAPMessage() throws MessageException,MessageNotSupportedException;
	public SOAPPart getSOAPPart() throws MessageException,MessageNotSupportedException;
	public SOAPBody getSOAPBody() throws MessageException,MessageNotSupportedException;
	public SOAPHeader getSOAPHeader() throws MessageException,MessageNotSupportedException;
	
	
	/* Attachments SOAP */
	
	public void addAttachmentPart(AttachmentPart attachmentPart) throws MessageException,MessageNotSupportedException;
	public AttachmentPart createAttachmentPart(DataHandler dataHandler) throws MessageException,MessageNotSupportedException;
	public AttachmentPart createAttachmentPart() throws MessageException,MessageNotSupportedException;
	public int countAttachments() throws MessageException,MessageNotSupportedException;
	public Iterator<?> getAttachments() throws MessageException,MessageNotSupportedException;
	public Iterator<?> getAttachments(MimeHeaders headers) throws MessageException,MessageNotSupportedException;
	public AttachmentPart getAttachment(SOAPElement element) throws MessageException,MessageNotSupportedException;
	public void removeAllAttachments() throws MessageException,MessageNotSupportedException;
	public void removeAttachments(MimeHeaders mhs) throws MessageException,MessageNotSupportedException;
	public void updateAttachmentPart(AttachmentPart ap,byte[] content,String contentType) throws MessageException,MessageNotSupportedException;
	public void updateAttachmentPart(AttachmentPart ap,String content,String contentType) throws MessageException,MessageNotSupportedException;
	public void updateAttachmentPart(AttachmentPart ap,DataHandler dh) throws MessageException,MessageNotSupportedException;

	
	/* ContentID Attachments SOAP */
	
	public String createContentID(String ns) throws MessageException,MessageNotSupportedException;
	
	
	/* SOAPAction */
	
	public String getSoapAction() throws MessageException,MessageNotSupportedException;
	public void setSoapAction(String soapAction) throws MessageException,MessageNotSupportedException;

		
	/* SOAP Utilities */
	
	public Element getFirstChildElement(SOAPElement element) throws MessageException,MessageNotSupportedException;
	
	public SOAPElement createSOAPElement(byte[] bytes) throws MessageException,MessageNotSupportedException;
	
	public SOAPHeaderElement newSOAPHeaderElement(SOAPHeader hdr, QName name) throws MessageException,MessageNotSupportedException;	
	public void addHeaderElement(SOAPHeader hdr,SOAPHeaderElement hdrElement) throws MessageException,MessageNotSupportedException;
	public void removeHeaderElement(SOAPHeader hdr,SOAPHeaderElement hdrElement) throws MessageException,MessageNotSupportedException;
	
	public void setFaultCode(SOAPFault fault, SOAPFaultCode code, QName eccezioneName) throws MessageException,MessageNotSupportedException;
	public void setFaultString(SOAPFault fault, String message) throws MessageException,MessageNotSupportedException;
	public void setFaultString(SOAPFault fault, String message, Locale locale) throws MessageException,MessageNotSupportedException;
		
	
	/* Ws Security */
	
	public List<Reference> getWSSDirtyElements(String actor, boolean mustUnderstand) throws MessageException,MessageNotSupportedException;
	
	public void cleanWSSDirtyElements(String actor, boolean mustUnderstand, List<Reference> elementsToClean, boolean detachHeaderWSSecurity, boolean removeAllIdRef) throws MessageException,MessageNotSupportedException;
	
	
	/* Ws Security (SoapBox) */
	
	public abstract String getEncryptedDataHeaderBlockClass();
	public abstract String getProcessPartialEncryptedMessageClass();
	public abstract String getSignPartialMessageProcessorClass();
	
	
	/* MTOM */
	
	public List<MtomXomReference> mtomUnpackaging() throws MessageException,MessageNotSupportedException;
	public List<MtomXomReference> mtomPackaging( List<MtomXomPackageInfo> packageInfos) throws MessageException,MessageNotSupportedException;
	public List<MtomXomReference> mtomVerify( List<MtomXomPackageInfo> packageInfos) throws MessageException,MessageNotSupportedException;
	public List<MtomXomReference> mtomFastUnpackagingForXSDConformance() throws MessageException,MessageNotSupportedException;
	public void mtomRestoreAfterXSDConformance(List<MtomXomReference> references) throws MessageException,MessageNotSupportedException;
	
}
