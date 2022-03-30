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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.openspcoop2.message.AbstractBaseOpenSPCoop2MessageDynamicContent;
import org.openspcoop2.message.ForwardConfig;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.context.MessageContext;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.soap.dynamic.DynamicSOAPHeader;
import org.openspcoop2.message.soap.dynamic.DynamicSOAPMessage;
import org.openspcoop2.message.soap.dynamic.DynamicSOAPPart;
import org.openspcoop2.message.soap.mtom.MTOMUtilities;
import org.openspcoop2.message.soap.mtom.MtomXomPackageInfo;
import org.openspcoop2.message.soap.mtom.MtomXomReference;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementazione dell'OpenSPCoop2Message utilizzabile per messaggi soap
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractOpenSPCoop2Message_soap_impl<T extends AbstractOpenSPCoop2Message_saaj_impl> extends AbstractBaseOpenSPCoop2MessageDynamicContent<T> implements OpenSPCoop2SoapMessage {


	
	protected long overhead;
	protected MimeHeaders mhs;
	
	/* OpenSPCoop2SoapMessageCore */
	protected OpenSPCoop2SoapMessageCore soapCore;
	

	public AbstractOpenSPCoop2Message_soap_impl(OpenSPCoop2MessageFactory messageFactory, MimeHeaders mhs, InputStream is, long overhead,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) throws MessageException {
		super(messageFactory, is, SoapUtils.getContentType(mhs), true, soapStreamReader);
		this.overhead = overhead;
		this.mhs = mhs;
		this.soapCore = new OpenSPCoop2SoapMessageCore();
	}
		
	@Override
	protected String buildContentAsString() throws MessageException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.serializeContent(bout, true);
			bout.flush();
			bout.close();
			return bout.toString(this.contentTypeCharsetName);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	protected byte[] buildContentAsByteArray() throws MessageException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.serializeContent(bout, true);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	protected void serializeContent(OutputStream os, boolean consume) throws MessageException {
		try{
			this.content.writeTo(os, consume);
			os.flush();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	protected void addSoapHeaderModifiedIntoSoapReader(SOAPHeader hdr, SOAPEnvelope envelope) throws MessageException {
		try {
			if(this.contentUpdatable && this.getSoapReader()!=null && this.getSoapReader().isSoapHeaderModified()) {
				SOAPHeader newHdr = this.getSoapReader().getHeader();
				
				//Node newHdrAdopted = envelope.getOwnerDocument().adoptNode(newHdr);
				if(hdr!=null) {
					/*org.w3c.dom.NodeList l = envelope.getChildNodes();
					for (int i = 0; i < l.getLength(); i++) {
						System.out.println("BEFORE ["+l.item(i).getClass().getName()+"] ["+l.item(i).getLocalName()+"]");
					}*/
					
					//envelope.replaceChild(hdr, newHdrAdopted);
					// devo preservare i commenti, cdata ...
					
					/*
					In questa maniera creo un nuovo header e chi ha preso il riferimento se lo perde (es. sbustamento Modi, wsaddressing)
					Node newHdrAdopted = envelope.getOwnerDocument().importNode(newHdr, true);
					if((hdr.getPrefix()==null && newHdrAdopted.getPrefix()!=null) || (!hdr.getPrefix().equals(newHdrAdopted.getPrefix()))) {
						//System.out.println("ALLINEAO AD HEADER PREFIX ["+hdr.getPrefix()+"]");
						newHdrAdopted.setPrefix(hdr.getPrefix()); // allineo alla costruzione del DOM
					}
					envelope.insertBefore(newHdrAdopted, hdr);
					envelope.removeChild(hdr);
					*/
					
					// rimpiazzo i figli
					hdr.removeContents();
					NodeList list = newHdr.getChildNodes();
					//System.out.println("LIST["+list.getLength()+"] ["+org.openspcoop2.utils.xml.XMLUtils.getInstance().toString(newHdr)+"]");
					if(list!=null && list.getLength()>0) {
						for (int i = 0; i < list.getLength(); i++) {
							Node n = list.item(i);
							//System.out.println("i["+i+"] ["+n.getClass().getName()+"] ["+n.getLocalName()+"]");
							Node newNodeAdopted = envelope.getOwnerDocument().importNode(n, true);
							hdr.appendChild(newNodeAdopted);
						}
					}
					
					/*l = envelope.getChildNodes();
					for (int i = 0; i < l.getLength(); i++) {
						System.out.println("AFTER ["+l.item(i).getClass().getName()+"] ["+l.item(i).getLocalName()+"]");
					}*/
				}
				else {
					Node newHdrAdopted = envelope.getOwnerDocument().importNode(newHdr, true);
					if((envelope.getPrefix()==null && newHdrAdopted.getPrefix()!=null) || (!envelope.getPrefix().equals(newHdrAdopted.getPrefix()))) {
						//System.out.println("ALLINEAO AD ENVELOPE PREFIX ["+envelope.getPrefix()+"]");
						newHdrAdopted.setPrefix(envelope.getPrefix()); // allineo alla costruzione del DOM
					}
					SOAPBody body = envelope.getBody();
					if(body!=null) {
						envelope.insertBefore(newHdrAdopted, body);
					}
					else {
						envelope.appendChild(newHdrAdopted);
					}
				}
				
				// Rilascio memoria tanto verra usato sempre il DOM costruito
				//if(this.contentUpdatable) {
				//System.out.println("ADD SOAP HEADER e RILASCIO RISORSE");
				this.getSoapReader().clearHeader();
				this.clearDynamicResources();
				//}
				//else {
				//	System.out.println("ADD SOAP HEADER SENZA RILASCIARE RISORSE");
				//}
				
				this.releaseSoapReader();
			}
			
			// Rilascio SOAPReader tanto verra usato sempre il DOM costruito
			//if(this.contentUpdatable) {
			//this.releaseSoapReader();
			//}
			
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	@Override
	protected void setUpdatableContent() throws MessageException{
		try{
			
			if(
					//this.contentUpdatable && 
					this.getSoapReader()!=null && this.getSoapReader().isSoapHeaderModified()) {
				SOAPHeader hdr = this.content.getSOAPHeader(); 
				SOAPEnvelope envelope = this.content.getSOAPPart().getEnvelope();
				this.addSoapHeaderModifiedIntoSoapReader(hdr, envelope);
			}
			
		}catch(Throwable t){
			throw SoapUtils.buildMessageException("Unable to update envelope header: ",t);
		}
	}
	
	
	
	
	// ---------- Metodi SAAJ re-implementati (in questi metodi l'accesso al contenuto avviene solo se precedentemente effettuato) ------------
	
	/* Copy Resources to another instance */
	
	@Override
	public MessageContext serializeResourcesTo() throws MessageException{
//		if(this.isContentBuilded()) {
//			return this.content.serializeResourcesTo();
//		}
//		else {
		MessageContext messageContext = super.serializeResourcesTo();
		return this.soapCore.serializeResourcesTo(messageContext);
//		}
	}
	
	@Override
	public void readResourcesFrom(MessageContext messageContext) throws MessageException{
//		if(this.isContentBuilded()) {
//			this.content.readResourcesFrom(messageContext);
//		}
//		else {
		super.readResourcesFrom(messageContext);
		this.soapCore.readResourcesFrom(messageContext);
//		}
	}
	
	/* SOAPAction */
	
	@Override
	public String getSoapAction(){
//		if(this.isContentBuilded()) {
//			return this.content.getSoapAction();
//		}
//		else {
		return this.soapCore.getSoapAction();
//		}
	}
	
	@Override
	public void setSoapAction(String soapAction){
		if(this.isContentBuilded()) {
			this.content.setSoapAction(soapAction);
		}
//		else {
		this.soapCore.setSoapAction(soapAction);
//		}
	}
	
	/* WSSecurity */
	
	@Override
	public boolean isThrowExceptionIfFoundMoreSecurityHeader() {
//		if(this.isContentBuilded()) {
//			return this.content.isThrowExceptionIfFoundMoreSecurityHeader();
//		}
//		else {
		return this.soapCore.isThrowExceptionIfFoundMoreSecurityHeader();
//		}
	}

	@Override
	public void setThrowExceptionIfFoundMoreSecurityHeader(boolean throwExceptionIfFoundMoreSecurityHeader) {
		if(this.isContentBuilded()) {
			this.content.setThrowExceptionIfFoundMoreSecurityHeader(throwExceptionIfFoundMoreSecurityHeader);
		}
//		else {
		this.soapCore.setThrowExceptionIfFoundMoreSecurityHeader(throwExceptionIfFoundMoreSecurityHeader);
//		}
	}
	
	/* Trasporto */
	
	@Override
	public OpenSPCoop2MessageProperties getForwardTransportHeader(ForwardConfig forwardConfig) throws MessageException{
		OpenSPCoop2MessageProperties msg = super.getForwardTransportHeader(forwardConfig);
		if(this.isContentBuilded()) {
			return new OpenSPCoop2MessageMimeHeaderProperties(this.content._getSOAPMessage(),msg);
		}
		else {
			return msg;
		}
	}
	
	
	/* ContentType */
	
	@Override
	public void updateContentType() throws MessageException {
		if(this.isContentBuilded()) {
			this.content.updateContentType();
			if(!this.contentUpdatable) {
				// aggiorno anche struttura leggera
				super.updateContentType();
			}
		}
		else {
			super.updateContentType();
		}
	}
	
	@Override
	public void setContentType(String type) {
		if(this.isContentBuilded()) {
			try{
				this.content.setContentType(type);
			}catch(Exception eInternal){
				throw new RuntimeException(eInternal.getMessage(),eInternal);
			}
			if(!this.contentUpdatable) {
				// aggiorno anche struttura leggera
				super.setContentType(type);
			}
		}
		else {
			super.setContentType(type);
		}
	}
	
	@Override
	public String getContentType() {
		if(this.contentBuffer!=null && !this.contentUpdatable) {
			return _getContentType();
		}
		else if(this.isContentBuilded()) {
			try{
				return this.content.getContentType();
			}catch(Exception eInternal){
				throw new RuntimeException(eInternal.getMessage(),eInternal);
			}
		}
		else {
			return _getContentType();
		}
	}
	protected String _getContentType() {
		return super.getContentType();
	}
	
	/* Save */
	
	@Override
	public void saveChanges() throws MessageException{
		if(this.isContentBuilded()) {
			this.content.saveChanges();
			if(!this.contentUpdatable) {
				// aggiorno anche struttura leggera
				super.saveChanges();
			}
		}
		else {
			super.saveChanges();
		}
	}
	
	@Override
	public boolean saveRequired(){
		if(this.contentBuffer!=null && !this.contentUpdatable) {
			return super.saveRequired();
		}
		else if(this.isContentBuilded()) {
			return this.content.saveRequired();
		}
		else {
			return super.saveRequired();
		}
	}
	
	/* ContentID Attachments SOAP */
	
	@Override
	public String createContentID(String ns) throws MessageException, MessageNotSupportedException {
		if(this.isContentBuilded()) {
			return this.content.createContentID(ns);
		}
		else {
			return AbstractOpenSPCoop2Message_saaj_impl._createContentID(ns);
		}
	}
	
	/* SOAP Utilities */

	@Override
	public Element getFirstChildElement(SOAPElement element) throws MessageException, MessageNotSupportedException {
		if(this.isContentBuilded()) {
			return this.content.getFirstChildElement(element);
		}
		else {
			return AbstractOpenSPCoop2Message_saaj_impl._getFirstChildElement(element);
		}
	}
	@Override
	public SOAPElement createSOAPElement(byte[] bytes) throws MessageException, MessageNotSupportedException {
		if(this.isContentBuilded()) {
			return this.content.createSOAPElement(bytes);
		}
		else {
			return AbstractOpenSPCoop2Message_saaj_impl._createSOAPElement(bytes, this.getMessageType(), this.messageFactory);
		}
	}
	@Override
	public SOAPHeaderElement newSOAPHeaderElement(SOAPHeader hdr, QName name)
			throws MessageException, MessageNotSupportedException {
		if(this.isContentBuilded()) {
			return this.content.newSOAPHeaderElement(hdr,name);
		}
		else {
			return AbstractOpenSPCoop2Message_saaj_impl._newSOAPHeaderElement(hdr,name);
		}
	}
	@Override
	public void addHeaderElement(SOAPHeader hdr, SOAPHeaderElement hdrElement)
			throws MessageException, MessageNotSupportedException {
		if(this.isContentBuilded()) {
			this.content.addHeaderElement(hdr, hdrElement);
		}
		else {
			AbstractOpenSPCoop2Message_saaj_impl._addHeaderElement(hdr, hdrElement);
		}
	}
	@Override
	public void removeHeaderElement(SOAPHeader hdr, SOAPHeaderElement hdrElement)
			throws MessageException, MessageNotSupportedException {
		if(this.isContentBuilded()) {
			this.content.removeHeaderElement(hdr, hdrElement);
		}
		else {
			AbstractOpenSPCoop2Message_saaj_impl._removeHeaderElement(hdr, hdrElement);
		}
	}
	@Override
	public void setFaultCode(SOAPFault fault, SOAPFaultCode code, QName eccezioneName)
			throws MessageException, MessageNotSupportedException {
		if(this.isContentBuilded()) {
			this.content.setFaultCode(fault, code, eccezioneName);
		}
		else {
			AbstractOpenSPCoop2Message_saaj_impl._setFaultCode(fault, code, eccezioneName);
		}
	}
	@Override
	public void setFaultString(SOAPFault fault, String message) throws MessageException, MessageNotSupportedException {
		if(this.isContentBuilded()) {
			this.content.setFaultString(fault, message);
		}
		else {
			AbstractOpenSPCoop2Message_saaj_impl._setFaultString(fault, message);
		}
	}
	@Override
	public void setFaultString(SOAPFault fault, String message, Locale locale)
			throws MessageException, MessageNotSupportedException {
		if(this.isContentBuilded()) {
			this.content.setFaultString(fault, message, locale);
		}
		else {
			AbstractOpenSPCoop2Message_saaj_impl._setFaultString(fault, message, locale);
		}
	}
	
	/* Ws Security (SoapBox) */
	
	@Override
	public String getEncryptedDataHeaderBlockClass() {
		if(this.isContentBuilded()) {
			return this.content.getEncryptedDataHeaderBlockClass();
		}
		else {
			return AbstractOpenSPCoop2Message_saaj_impl._getEncryptedDataHeaderBlockClass();
		}
	}
	@Override
	public String getProcessPartialEncryptedMessageClass() {
		if(this.isContentBuilded()) {
			return this.content.getProcessPartialEncryptedMessageClass();
		}
		else {
			return AbstractOpenSPCoop2Message_saaj_impl._getProcessPartialEncryptedMessageClass();
		}
	}
	@Override
	public String getSignPartialMessageProcessorClass() {
		if(this.isContentBuilded()) {
			return this.content.getSignPartialMessageProcessorClass();
		}
		else {
			return AbstractOpenSPCoop2Message_saaj_impl._getSignPartialMessageProcessorClass();
		}
	}
	
	
	
	
	// ------------ Metodi SAAJ wrappati con la chiamata 'getContent' che fa si che venga costruito il messaggio --------------------
	
	public boolean isSoapHeaderOptimizable() {
		OpenSPCoop2MessageSoapStreamReader soapReader = this.getSoapReader();
		if( ( (!this.isContentBuilded()) || !this.contentUpdatable ) 
				&& 
				soapReader!=null && soapReader.isParsingComplete() && soapReader.isSoapHeaderOptimizable()) {
			return true;
		}
		return false;
	}
	
	/* Elementi SOAP */
	
	private DynamicSOAPMessage<T> dynamicSOAPMessage = null;
	@Override
	public SOAPMessage getSOAPMessage() throws MessageException, MessageNotSupportedException {
		if(this.dynamicSOAPMessage==null) {
			if(isSoapHeaderOptimizable()) {
				this.dynamicSOAPMessage = new DynamicSOAPMessage<T>(this);
			}
		}
		if(this.dynamicSOAPMessage!=null) {
			return this.dynamicSOAPMessage;
		}
		else {
			return this.getContent().getSOAPMessage();
		}
	}
	
	private DynamicSOAPPart<T> dynamicSOAPPart = null;
	@Override
	public SOAPPart getSOAPPart() throws MessageException, MessageNotSupportedException {
		if(this.dynamicSOAPPart==null) {
			if(isSoapHeaderOptimizable()) {
				this.dynamicSOAPPart = new DynamicSOAPPart<T>(this);
			}
		}
		if(this.dynamicSOAPPart!=null) {
			return this.dynamicSOAPPart;
		}
		else {
			return this.getContent().getSOAPPart();
		}
	}
	
	private DynamicSOAPHeader<T> dynamicSOAPHeader = null;
	@Override
	public SOAPHeader getSOAPHeader() throws MessageException, MessageNotSupportedException {
		if(this.dynamicSOAPHeader==null) {
			if(isSoapHeaderOptimizable()) {
				this.dynamicSOAPHeader = new DynamicSOAPHeader<T>(this);
			}
		}
		if(this.dynamicSOAPHeader!=null) {
			return this.dynamicSOAPHeader;
		}
		else {
			return this.getContent().getSOAPHeader();
		}
	}
	
	public void clearDynamicResources() {
		
		this.dynamicSOAPHeader = null;
		
		if(this.dynamicSOAPPart!=null) {
			this.dynamicSOAPPart.clearDynamicResources();
		}
		this.dynamicSOAPPart=null;
		
		if(this.dynamicSOAPMessage!=null) {
			this.dynamicSOAPMessage.clearDynamicResources();
		}
		this.dynamicSOAPMessage=null;
	}
	
	@Override
	public SOAPBody getSOAPBody() throws MessageException, MessageNotSupportedException {
		return this.getContent().getSOAPBody();
	}
	@Override
	protected SOAPBody _getSOAPBody_internalAnalyze()throws MessageException, MessageNotSupportedException {
		// usato in isFault
		SOAPBody body = null;
		if(this.content!=null && this.contentBuffer!=null) {
			// per lasciare readOnly
			body = this.content.getSOAPBody();
		}
		else {
			body = getSOAPBody();
		}
		return body;
	}
	@Override
	public boolean hasSOAPFault() throws MessageException,MessageNotSupportedException{
		OpenSPCoop2MessageSoapStreamReader soapReader = getSoapReader();
		if(!this.isContentBuilded() && soapReader!=null && soapReader.isParsingComplete()) {
			return soapReader.isFault();
		}
		
		SOAPBody body = null;
		if(this.content!=null && this.contentBuffer!=null) {
			// per lasciare readOnly
			body = this.content.getSOAPBody();
		}
		else {
			body = getSOAPBody();
		}
		return body!=null && body.hasFault();
	}
	@Override
	public boolean isSOAPBodyEmpty() throws MessageException,MessageNotSupportedException{
		OpenSPCoop2MessageSoapStreamReader soapReader = getSoapReader();
		if(!this.isContentBuilded() && soapReader!=null && soapReader.isParsingComplete()) {
			return soapReader.isEmpty();
		}
		
		SOAPBody body = null;
		if(this.content!=null && this.contentBuffer!=null) {
			// per lasciare readOnly
			body = this.content.getSOAPBody();
		}
		else {
			body = getSOAPBody();
		}
		
		boolean hasContent = body!=null;
		if(hasContent){
			hasContent = SoapUtils.getFirstNotEmptyChildNode(this.messageFactory, body, false)!=null;
		}
		return !hasContent;
	}
	
	/* Attachments SOAP */
	
	@Override
	public void addAttachmentPart(AttachmentPart attachmentPart) throws MessageException, MessageNotSupportedException {
		this.getContent().addAttachmentPart(attachmentPart);
	}
	@Override
	public AttachmentPart createAttachmentPart(DataHandler dataHandler)
			throws MessageException, MessageNotSupportedException {
		return this.getContent().createAttachmentPart(dataHandler);
	}
	@Override
	public AttachmentPart createAttachmentPart() throws MessageException, MessageNotSupportedException {
		return this.getContent().createAttachmentPart();
	}
	@Override
	public boolean hasAttachments() throws MessageException,MessageNotSupportedException{
		if(this.isContentBuilded()){
			return this.content.countAttachments()>0;
		}
		else {
			String ct = this.getContentType();
			try {
				return ct!=null && ContentTypeUtilities.isMultipart(ct);
			}catch(Exception e) {
				throw new MessageException(e.getMessage(),e);
			}
		}
	}
	@Override
	public int countAttachments() throws MessageException, MessageNotSupportedException {
		if(this.isContentBuilded()){
			return this.content.countAttachments();
		}
		else {
			return this.getContent().countAttachments();
		}
	}
	@Override
	public Iterator<?> getAttachments() throws MessageException, MessageNotSupportedException {
		if(this.isContentBuilded()){
			return this.content.getAttachments();
		}
		else {
			return this.getContent().getAttachments();
		}
	}
	@Override
	public Iterator<?> getAttachments(MimeHeaders headers) throws MessageException, MessageNotSupportedException {
		if(this.isContentBuilded()){
			return this.content.getAttachments(headers);
		}
		else {
			return this.getContent().getAttachments(headers);
		}
	}
	@Override
	public AttachmentPart getAttachment(SOAPElement element) throws MessageException, MessageNotSupportedException {
		if(this.isContentBuilded()){
			return this.getContent().getAttachment(element);
		}
		else {
			return this.getContent().getAttachment(element);
		}
	}
	@Override
	public void removeAllAttachments() throws MessageException, MessageNotSupportedException {
		this.getContent().removeAllAttachments();
	}
	@Override
	public void removeAttachments(MimeHeaders mhs) throws MessageException, MessageNotSupportedException {
		this.getContent().removeAttachments(mhs);
	}
	@Override
	public void updateAttachmentPart(AttachmentPart ap, byte[] content, String contentType)
			throws MessageException, MessageNotSupportedException {
		this.getContent().updateAttachmentPart(ap, content, contentType);
	}
	@Override
	public void updateAttachmentPart(AttachmentPart ap, String content, String contentType)
			throws MessageException, MessageNotSupportedException {
		this.getContent().updateAttachmentPart(ap, content, contentType);
	}
	@Override
	public void updateAttachmentPart(AttachmentPart ap, DataHandler dh)
			throws MessageException, MessageNotSupportedException {
		this.getContent().updateAttachmentPart(ap, dh);
	}

	/* Ws Security */

	@Override
	public List<Reference> getWSSDirtyElements(String actor, boolean mustUnderstand)
			throws MessageException, MessageNotSupportedException {		
		if(this.isContentBuilded()){
			return this.content.getWSSDirtyElements(actor, mustUnderstand);
		}
		else {
			return this.getContent().getWSSDirtyElements(actor, mustUnderstand);
		}
	}
	@Override
	public void cleanWSSDirtyElements(String actor, boolean mustUnderstand, List<Reference> elementsToClean,
			boolean detachHeaderWSSecurity, boolean removeAllIdRef)
			throws MessageException, MessageNotSupportedException {
		this.getContent().cleanWSSDirtyElements(actor, mustUnderstand, elementsToClean,
				detachHeaderWSSecurity, removeAllIdRef);
	}
	
	/* MTOM */

	@Override
	public List<MtomXomReference> mtomUnpackaging() throws MessageException,MessageNotSupportedException{
		return MTOMUtilities.unpackaging(this.getContent(), false, true);
	}
	@Override
	public List<MtomXomReference> mtomPackaging( List<MtomXomPackageInfo> packageInfos) throws MessageException,MessageNotSupportedException{
		return MTOMUtilities.packaging(this.getContent(), packageInfos, true);
	}
	@Override
	public List<MtomXomReference> mtomVerify( List<MtomXomPackageInfo> packageInfos) throws MessageException,MessageNotSupportedException{
		return MTOMUtilities.verify((this.isContentBuilded() ? this.content : this.getContent()), packageInfos, true);
	}
	@Override
	public List<MtomXomReference> mtomFastUnpackagingForXSDConformance() throws MessageException,MessageNotSupportedException{
		return MTOMUtilities.unpackaging(this.getContent(), true, true);
	}
	@Override
	public void mtomRestoreAfterXSDConformance(List<MtomXomReference> references) throws MessageException,MessageNotSupportedException{
		MTOMUtilities.restoreAfterFastUnpackaging(this.getContent(), references, true);
	}
	
}
