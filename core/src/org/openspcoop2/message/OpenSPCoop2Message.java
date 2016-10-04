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

package org.openspcoop2.message;


import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPPart;

import org.apache.wss4j.common.ext.WSSecurityException;
import org.openspcoop2.message.mtom.MtomXomPackageInfo;
import org.openspcoop2.message.mtom.MtomXomReference;
import org.openspcoop2.message.reference.Reference;
import org.openspcoop2.utils.io.notifier.NotifierInputStream;
import org.openspcoop2.utils.resources.TransportRequestContext;
import org.openspcoop2.utils.resources.TransportResponseContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Interfaccia del messaggio OpenSPCoop
 * 
 * Oltre ai metodi SAAJ usati per le operazioni di lettura e scrittura 
 * del messaggio XML, fornisce alcuni metodi di utilita' per facilitare
 * un eventuale cambio di implementazione
 *
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface OpenSPCoop2Message {
	
	/* Metodi SOAP */
	
	public SOAPPart getSOAPPart();
	public SOAPBody getSOAPBody() throws SOAPException;
	public SOAPHeader getSOAPHeader() throws SOAPException;
	
	/* Attachments */
	
	public void addAttachmentPart(AttachmentPart AttachmentPart);
	public AttachmentPart createAttachmentPart(DataHandler dataHandler);
	public AttachmentPart createAttachmentPart();
	public int countAttachments();
	public Iterator<?> getAttachments();
	public Iterator<?> getAttachments(MimeHeaders headers);
	public AttachmentPart getAttachment(SOAPElement element) throws SOAPException;
	public void removeAllAttachments();
	public abstract void removeAttachments(MimeHeaders mhs);
	public void updateAttachmentPart(AttachmentPart ap,byte[] content,String contentType);
	public void updateAttachmentPart(AttachmentPart ap,String content,String contentType);
	public void updateAttachmentPart(AttachmentPart ap,DataHandler dh);
	
	/* Trasporto */
	
	public MimeHeaders getMimeHeaders();
	public void setTransportRequestContext(TransportRequestContext transportRequestContext);
	public TransportRequestContext getTransportRequestContext();
	public void setTransportResponseContext(TransportResponseContext transportResponseContext);
	public TransportResponseContext getTransportResponseContext();
	public void setForcedResponseCode(String code);
	public String getForcedResponseCode();
	
	/* SAAJUtils */
	
	public void setContentDescription(String description);
	public String getContentDescription();
	
	public void saveChanges() throws SOAPException;
	public boolean saveRequired();
	
	public void setProperty(String property, Object value);
	public Object getProperty(String property) throws SOAPException;
	
	public void addContextProperty(String property, Object value);
	public Iterator<String> keysContextProperty();
	public Object getContextProperty(String property);
	public Object removeContextProperty(String property);
		
	/* SOAP Content Constants */
	
	public abstract String createContentID(String ns) throws UnsupportedEncodingException;	
	public abstract String getContentID(AttachmentPart ap);

	public abstract void addContentTypeParameter(String name,String value) throws SOAPException;	
	public abstract void removeContentTypeParameter(String name) throws SOAPException;	
	public abstract void updateContentType() throws SOAPException;	
	public abstract void setContentType(String type);
	public abstract String getContentType();

	public abstract SOAPVersion getVersioneSoap();
	
	/* Write To */
	
	public abstract void writeTo(OutputStream os, boolean consume) throws SOAPException, IOException;
		
	/* Content Length */
	
	public abstract void updateIncomingMessageContentLength();
	public abstract void updateIncomingMessageContentLength(long incomingsize);
	public abstract long getIncomingMessageContentLength();	
	public abstract void updateOutgoingMessageContentLength(long outgoingsize);
	public abstract long getOutgoingMessageContentLength();
		
	/* SOAP Utilities */
	
	public abstract Element getFirstChildElement(SOAPElement element);
	
	public abstract SOAPElement createSOAPElement(byte[] bytes) throws IOException, ParserConfigurationException, SAXException, SOAPException;
	
	public abstract SOAPHeaderElement cleanXSITypes(SOAPHeaderElement header) throws SOAPException;
	public abstract SOAPElement cleanXSITypes(SOAPElement header) throws SOAPException;
	
	public abstract SOAPHeaderElement newSOAPHeaderElement(SOAPHeader hdr, QName name) throws SOAPException;	
	public abstract void addHeaderElement(SOAPHeader hdr,SOAPHeaderElement hdrElement) throws SOAPException;
	public abstract void removeHeaderElement(SOAPHeader hdr,SOAPHeaderElement hdrElement) throws SOAPException;
	
	public abstract void setFaultCode(SOAPFault fault, SOAPFaultCode code, QName eccezioneName) throws SOAPException;
	
	/* Node Utilities */
	
	public abstract String getAsString(Node ele, boolean consume);
	
	public abstract byte[] getAsByte(Node ele, boolean consume);
	
	/* Ws Security */
	
	public abstract List<Reference> getWSSDirtyElements(String actor, boolean mustUnderstand) throws SOAPException, WSSecurityException;
	
	public abstract void cleanWSSDirtyElements(String actor, boolean mustUnderstand, List<Reference> elementsToClean) throws SOAPException, WSSecurityException;
	
	/* Errors */
	
	public abstract ParseException getParseException();
	public abstract void setParseException(Throwable eParsing);
	public abstract void setParseException(ParseException eParsing);
	
	/* Protocol Plugin */
	
	public abstract void setProtocolName(String protocolName);
	public abstract String getProtocolName();
	
	/* Stream */
	
	public void setNotifierInputStream(NotifierInputStream is);	
	public NotifierInputStream getNotifierInputStream();
	
	/* MTOM */
	
	public List<MtomXomReference> mtomUnpackaging() throws OpenSPCoop2MessageException;
	public List<MtomXomReference> mtomPackaging( List<MtomXomPackageInfo> packageInfos) throws OpenSPCoop2MessageException;
	public List<MtomXomReference> mtomVerify( List<MtomXomPackageInfo> packageInfos) throws OpenSPCoop2MessageException;
	public List<MtomXomReference> mtomFastUnpackagingForXSDConformance() throws OpenSPCoop2MessageException;
	public void mtomRestoreAfterXSDConformance(List<MtomXomReference> references) throws OpenSPCoop2MessageException;
	
}
