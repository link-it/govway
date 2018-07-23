/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;

import javax.mail.internet.ContentType;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.notifier.NotifierInputStream;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.mime.MultipartUtils;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Factory per la costruzione di messaggi OpenSPCoop2Message. 
 * 
 * @author Lorenzo Nardi (nardi@link.it)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class OpenSPCoop2MessageFactory {
	
	
	public static String messageFactoryImpl = org.openspcoop2.message.OpenSPCoop2MessageFactory_impl.class.getName();
	
	public static void setMessageFactoryImpl(String messageFactoryImpl) {
		if(messageFactoryImpl != null)
			OpenSPCoop2MessageFactory.messageFactoryImpl = messageFactoryImpl;
	}

	protected static OpenSPCoop2MessageFactory openspcoopMessageFactory = null;
	public static OpenSPCoop2MessageFactory getMessageFactory() {
		if(OpenSPCoop2MessageFactory.openspcoopMessageFactory == null)
			try { OpenSPCoop2MessageFactory.initMessageFactory(); } catch (Exception e) { throw new RuntimeException(e); }
		return OpenSPCoop2MessageFactory.openspcoopMessageFactory;
	}
	
	public static void initMessageFactory() throws MessageException {
		initMessageFactory(false);
	}
	public static void initMessageFactory(boolean force) throws MessageException {
		try {
			if(OpenSPCoop2MessageFactory.openspcoopMessageFactory==null || force){
				OpenSPCoop2MessageFactory.openspcoopMessageFactory = (OpenSPCoop2MessageFactory) Loader.getInstance().newInstance(OpenSPCoop2MessageFactory.messageFactoryImpl);
				//System.out.println("CREATO F("+force+") ["+OpenSPCoop2MessageFactory.openspcoopMessageFactory+"] ["+OpenSPCoop2MessageFactory.messageFactoryImpl+"]");
			}
	//		else{
	//			System.out.println("GIA ESISTE ["+OpenSPCoop2MessageFactory.openspcoopMessageFactory+"]");
	//		}
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	
	
	
	// ********** SOAP - FACTORY *************
		
	protected static SOAPFactory soapFactory11 = null;
	protected static SOAPFactory soapFactory12 = null;
	public SOAPFactory getSoapFactory11(){
		if(OpenSPCoop2MessageFactory.soapFactory11==null){
			initSoapFactory();
		}
		return OpenSPCoop2MessageFactory.soapFactory11;
	}	
	public SOAPFactory getSoapFactory12(){
		if(OpenSPCoop2MessageFactory.soapFactory12==null){
			initSoapFactory();
		}
		return OpenSPCoop2MessageFactory.soapFactory12;
	}
	protected abstract void initSoapFactory();
	
	
	protected static MessageFactory soapMessageFactory = null;
	public MessageFactory getSoapMessageFactory() throws SOAPException {
		if(OpenSPCoop2MessageFactory.soapMessageFactory==null){
			initSoapMessageFactory();
		}
		return OpenSPCoop2MessageFactory.soapMessageFactory;
	}
	protected abstract void initSoapMessageFactory() throws SOAPException; 
	
	public abstract SOAPConnectionFactory getSOAPConnectionFactory() throws SOAPException;
	
	
	
	
	// ********** NODE Utilities mediati dall'implementazione dell'OpenSPCoop2Message *************
	
	public static String getAsString(Node ele, boolean consume){
		// E' indipendente dal tipo SOAP11, il tipo viene utilizzato come uno qualsiasi
		return OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(MessageType.SOAP_11,MessageRole.NONE).getAsString(ele,true);
	}
	public static byte[] getAsByte(Node ele, boolean consume){
		// E' indipendente dal tipo SOAP11, il tipo viene utilizzato come uno qualsiasi
		return OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(MessageType.SOAP_11,MessageRole.NONE).getAsByte(ele,true);
	}
	
	
	// ********** SOAP Utilities mediati dall'implementazione dell'OpenSPCoop2Message *************
	
	public static SOAPElement createSOAPElement(MessageType messageType,byte[] element) throws MessageException, MessageNotSupportedException{
		OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getMessageFactory();
		OpenSPCoop2Message message = mf.createEmptyMessage(messageType,MessageRole.NONE);
		OpenSPCoop2SoapMessage soapMsg = message.castAsSoap();
		return soapMsg.createSOAPElement(element);
	}
	public static Element getFirstChildElement(MessageType messageType,SOAPElement element) throws MessageException, MessageNotSupportedException{
		return OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(messageType,MessageRole.NONE).castAsSoap().getFirstChildElement(element);
	}
	

	
	
	
	// ********** SAAJ *************
	
	public abstract String getDocumentBuilderFactoryClass();
	
    public abstract Element convertoForXPathSearch(Element contenutoAsElement);
    
    public abstract void normalizeDocument(Document document);
	
    
    
    
    // ********** OpenSPCoop2Message builder *************
    
	protected abstract OpenSPCoop2Message _createMessage(MessageType messageType, SOAPMessage msg) throws MessageException;
	
	protected abstract OpenSPCoop2Message _createEmptyMessage(MessageType messageType) throws MessageException;
	
	protected abstract OpenSPCoop2Message _createMessage(MessageType messageType, TransportRequestContext requestContext, 
			InputStream is,  AttachmentsProcessingMode attachmentsProcessingMode, long overhead) throws MessageException;	
	protected abstract OpenSPCoop2Message _createMessage(MessageType messageType, TransportResponseContext responseContext, 
			InputStream is,  AttachmentsProcessingMode attachmentsProcessingMode, long overhead) throws MessageException;	
	protected abstract OpenSPCoop2Message _createMessage(MessageType messageType, String contentType, 
			InputStream is,  AttachmentsProcessingMode attachmentsProcessingMode, long overhead) throws MessageException;
	
	
	
	
	
	// ********** MessageType *************

	public MessageType getMessageType(SOAPMessage soapMessage) throws MessageException {
		try{
			Object o = soapMessage.getProperty(Costanti.SOAP_MESSAGE_PROPERTY_MESSAGE_TYPE);
			if(o!=null && o instanceof MessageType){
				return (MessageType) o;
			}
			return null;
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
    
    
    
    
    
	
	// ********** INTERNAL CREATE (chiamate dai metodi pubblici) *************
	
    private OpenSPCoop2Message _internalCreateMessage(MessageType messageType, MessageRole role, SOAPMessage msg) throws MessageException{
    	
    	if(!MessageType.SOAP_11.equals(messageType) && !MessageType.SOAP_11.equals(messageType)){
			throw new MessageException("Message Type ["+messageType+"] unsupported");
		}
    	
    	OpenSPCoop2Message msgNew = this._createMessage(messageType, msg);
    	
    	msgNew.castAsSoap(); // check
		
    	msgNew.setMessageType(messageType);
    	msgNew.setMessageRole(role);
    	
    	return msgNew;
    }
    
    private OpenSPCoop2Message _internalCreateEmptyMessage(MessageType messageType, MessageRole role) throws MessageException{
    	
    	OpenSPCoop2Message msgNew = this._createEmptyMessage(messageType);
    	
    	MessageUtilities.checkType(messageType, msgNew);
		
		msgNew.setMessageType(messageType);
		msgNew.setMessageRole(role);
		
        return msgNew;
    	
    }
    
    private OpenSPCoop2MessageParseResult _internalCreateMessage(MessageType messageType, MessageRole messageRole, Object context, 
			Object msgParam, NotifierInputStreamParams notifierInputStreamParams,
			AttachmentsProcessingMode attachmentsProcessingMode, long overhead) {	
		
		OpenSPCoop2MessageParseResult result = new OpenSPCoop2MessageParseResult();
		try{
			InputStream is = null;
			if(msgParam!=null){
				if(msgParam instanceof byte[]){
					is = new ByteArrayInputStream( (byte[]) msgParam);
				}
				else if(msgParam instanceof InputStream){
					is = (InputStream) msgParam;
				}
				else{
					throw new Exception("Tipo di messaggio non supportato: "+msgParam.getClass().getName());
				}
			}
			
			InputStream nis = null;
			
//			if(is==null){
//				throw new Exception("Original InputStream undefined");
//			}
//			
			String contentType = null;
			TransportRequestContext transportRequestContext = null; 
			TransportResponseContext transportResponseContext = null;
			String codiceTrasportoRisposta = null;
			if(context instanceof TransportRequestContext){
				transportRequestContext = (TransportRequestContext) context;
				contentType = transportRequestContext.getParameterTrasporto(HttpConstants.CONTENT_TYPE);
			}
			else if(context instanceof TransportResponseContext){
				transportResponseContext = (TransportResponseContext) context;
				contentType = transportResponseContext.getParameterTrasporto(HttpConstants.CONTENT_TYPE);
				codiceTrasportoRisposta = transportResponseContext.getCodiceTrasporto();
			}
			else if(context instanceof String){
				contentType = (String) context;
			}
			else{
				throw new MessageException("Unsupported Context ["+context.getClass().getName()+"]");
			}
			
			
			if(notifierInputStreamParams!=null && is!=null){
				nis = new NotifierInputStream(is,contentType,notifierInputStreamParams);
			}
			else{
				nis = is;
			}
			
			OpenSPCoop2Message op2Msg = null;
			if(transportRequestContext!=null){
				op2Msg = this._createMessage(messageType, transportRequestContext, nis, attachmentsProcessingMode, overhead);
				
				if(MessageType.SOAP_11.equals(messageType) || MessageType.SOAP_12.equals(messageType)){
					String soapAction = null;
					if(MessageType.SOAP_11.equals(messageType)){
						soapAction = transportRequestContext.getParameterTrasporto(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION);
					}
					else{
						// The SOAP 1.1 mandatory SOAPAction HTTP header has been removed in SOAP 1.2. 
						// In its place is an optional action parameter on the application/soap+xml media type.
						if(contentType!=null){
							ContentType ct = new ContentType(contentType);
							if(ct.getParameterList()!=null && ct.getParameterList().size()>0){
								Enumeration<?> names = ct.getParameterList().getNames();
								while (names.hasMoreElements()) {
									String name = (String) names.nextElement();
									if(Costanti.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION.equals(name)){
										soapAction = ct.getParameterList().get(name);
									}	
								}
							}
						}
					}
					
					OpenSPCoop2SoapMessage soapMessage = op2Msg.castAsSoap();
					if(soapAction!=null){
						soapMessage.setSoapAction(soapAction);
					}
				}
			}
			else if(transportResponseContext!=null){
				op2Msg = this._createMessage(messageType, transportResponseContext, nis, attachmentsProcessingMode, overhead);
			}
			else{
				op2Msg = this._createMessage(messageType, contentType, nis, attachmentsProcessingMode, overhead);
			}
			if(op2Msg==null){
				throw new Exception("Create message failed");
			}
			if(notifierInputStreamParams!=null && nis!=null){
				op2Msg.setNotifierInputStream((NotifierInputStream)nis);
			}
			
			if(context instanceof TransportRequestContext){
				op2Msg.setTransportRequestContext(transportRequestContext);
			}
			else if(context instanceof TransportResponseContext){
				op2Msg.setTransportResponseContext(transportResponseContext);
				if(codiceTrasportoRisposta!=null){
					op2Msg.setForcedResponseCode(codiceTrasportoRisposta);
				}
			}
			
			op2Msg.setMessageRole(messageRole);
			op2Msg.setMessageType(messageType);
			
			if(MessageType.SOAP_11.equals(messageType) || MessageType.SOAP_12.equals(messageType)){
				op2Msg.castAsSoap().getSOAPHeader(); // Verifica struttura
			}
			
			result.setMessage(op2Msg);
		}catch(Throwable t){
			result.setParseException(ParseExceptionUtils.buildParseException(t));
		}
		return result;
	}
	
	private OpenSPCoop2MessageParseResult _internalEnvelopingMessage(MessageType messageType, MessageRole messageRole, 
			String contentTypeForEnvelope, String soapAction,
			Object context, 
			Object msgParam, NotifierInputStreamParams notifierInputStreamParams, 
			AttachmentsProcessingMode attachmentsProcessingMode,
			boolean eraserXmlTag) {
				
		try{
		
			byte[] byteMsg = null;
			if(msgParam instanceof byte[]){
				byteMsg = (byte[]) msgParam;
			}
			else if(msgParam instanceof InputStream){
				byteMsg = Utilities.getAsByteArray((InputStream)msgParam);
			}
			else{
				throw new Exception("Tipo di messaggio non supportato: "+msgParam.getClass().getName());
			}
			
			if(byteMsg==null || byteMsg.length==0) 
				throw new Exception("Nessun contenuto fornito da inserire all'interno del SoapBody");
			
			if(MessageType.SOAP_11.equals(messageType)){
				if(contentTypeForEnvelope==null){
					contentTypeForEnvelope = HttpConstants.CONTENT_TYPE_SOAP_1_1;
				}
			} else if(MessageType.SOAP_12.equals(messageType)){
				if(contentTypeForEnvelope==null){
					contentTypeForEnvelope = HttpConstants.CONTENT_TYPE_SOAP_1_2;
				}
			}
			else{
				throw new MessageException("Unsupported Type ["+messageType+"]");
			}
			
			int offset = 0;
			InputStream messageInput = null;
			
			if(MultipartUtils.messageWithAttachment(byteMsg)){
				
				// ** Attachments **
				
				contentTypeForEnvelope = ContentTypeUtilities.buildMultipartContentType(byteMsg, contentTypeForEnvelope);
				String boundary = MultipartUtils.findBoundary(byteMsg);

				// Il messaggio deve essere imbustato.
				String msg = new String(byteMsg);
				
				int firstBound = msg.indexOf(boundary);
				int secondBound = msg.indexOf(boundary,firstBound+boundary.length());
				if(firstBound==-1 || secondBound==-1)
					throw new Exception("multipart/related non correttamente formato (bound not found)");
				String bodyOriginal = msg.substring(firstBound+boundary.length(), secondBound);
				
				// Cerco "\r\n\r\n";
				int indexCarriage = bodyOriginal.indexOf("\r\n\r\n");
				if(indexCarriage==-1)
					throw new Exception("multipart/related non correttamente formato (\\r\\n\\r\\n not found)");
				String contenutoBody = bodyOriginal.substring(indexCarriage+"\r\n\r\n".length());
				
				// brucio spazi vuoti
				int puliziaSpaziBianchi_e_XML = 0;
				for( ; puliziaSpaziBianchi_e_XML<contenutoBody.length(); puliziaSpaziBianchi_e_XML++){
					if(contenutoBody.charAt(puliziaSpaziBianchi_e_XML)!=' '){
						break;
					}
				}
				String bodyPulito = contenutoBody.substring(puliziaSpaziBianchi_e_XML);
				
				// se presente <?xml && eraserXMLTag
				if(bodyPulito.startsWith("<?xml")){
					if(eraserXmlTag){
						// eliminazione tag <?xml
						for(puliziaSpaziBianchi_e_XML=0 ; puliziaSpaziBianchi_e_XML<contenutoBody.length(); puliziaSpaziBianchi_e_XML++){
							if(contenutoBody.charAt(puliziaSpaziBianchi_e_XML)=='>'){
								break;
							}
						}
						bodyPulito = bodyPulito.substring(puliziaSpaziBianchi_e_XML+1);
					}else{
						// lancio eccezione
						throw new Exception("Tag <?xml non permesso con la funzionalita di imbustamento SOAP");
					}
				}
									
				// ImbustamentoSOAP
				String contenutoBodyImbustato = null;
				if(MessageType.SOAP_11.equals(messageType)){
					contenutoBodyImbustato = 
							"<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Body>"+
									bodyPulito+
							"</SOAP-ENV:Body></SOAP-ENV:Envelope>\r\n";	
				} else {
					contenutoBodyImbustato = 
							"<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\"><SOAP-ENV:Body>"+
									bodyPulito+
							"</SOAP-ENV:Body></SOAP-ENV:Envelope>\r\n";	
				}
				
				// MessaggioImbustato
				String bodyOriginalImbustato = bodyOriginal.replace(contenutoBody, contenutoBodyImbustato);
				msg = msg.replace(bodyOriginal, bodyOriginalImbustato);
				byteMsg = msg.getBytes();
				
				messageInput = new ByteArrayInputStream(byteMsg,offset,byteMsg.length);
			}
			else{
				
				// No Attachments
				//Controllo <?xml
				
				// brucio spazi vuoti
				int i = 0;
				for( ; i<byteMsg.length; i++){
					if(((char)byteMsg[i])!=' '){
						break;
					}
				}
				
				// se presente <?xml
				offset = readOffsetXmlInstruction(byteMsg, i, eraserXmlTag, offset, false);
				InputStream messageInputXml = new ByteArrayInputStream(byteMsg,offset,byteMsg.length);
				
				ByteArrayInputStream binSoapOpen = null;
				if(MessageType.SOAP_11.equals(messageType)){
					binSoapOpen = new ByteArrayInputStream("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Body>".getBytes());
				}
				else {
					binSoapOpen = new ByteArrayInputStream("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\"><SOAP-ENV:Body>".getBytes());
				}
				ByteArrayInputStream binSoapClose = new ByteArrayInputStream("</SOAP-ENV:Body></SOAP-ENV:Envelope>".getBytes());
				
				// UNIRE GLI STREAM E LEVARE QUELLO SOTTO
				InputStream startInput = new SequenceInputStream(binSoapOpen, messageInputXml);
				messageInput = new SequenceInputStream(startInput, binSoapClose);
			}
						
			OpenSPCoop2MessageParseResult result = null;
			if(context==null){
				result = _internalCreateMessage(messageType, messageRole, contentTypeForEnvelope, messageInput, notifierInputStreamParams, attachmentsProcessingMode, 0);
			}
			else if(context instanceof TransportRequestContext){
				TransportRequestContext trc = (TransportRequestContext) context;
				trc.getParametersTrasporto().remove(HttpConstants.CONTENT_TYPE);
				trc.getParametersTrasporto().remove(HttpConstants.CONTENT_TYPE.toLowerCase());
				trc.getParametersTrasporto().remove(HttpConstants.CONTENT_TYPE.toUpperCase());
				trc.getParametersTrasporto().put(HttpConstants.CONTENT_TYPE, contentTypeForEnvelope);
				
				result = _internalCreateMessage(messageType, messageRole, trc, messageInput, notifierInputStreamParams, attachmentsProcessingMode, 0);
				
			}
			else if(context instanceof TransportResponseContext){
				TransportResponseContext trc = (TransportResponseContext) context;
				trc.getParametersTrasporto().remove(HttpConstants.CONTENT_TYPE);
				trc.getParametersTrasporto().remove(HttpConstants.CONTENT_TYPE.toLowerCase());
				trc.getParametersTrasporto().remove(HttpConstants.CONTENT_TYPE.toUpperCase());
				trc.getParametersTrasporto().put(HttpConstants.CONTENT_TYPE, contentTypeForEnvelope);
				
				result = _internalCreateMessage(messageType, messageRole, trc, messageInput, notifierInputStreamParams, attachmentsProcessingMode, 0);
			}
			else{
				throw new MessageException("Unsupported Context ["+context.getClass().getName()+"]");
			}
			
			// Verifico la costruzione del messaggio SOAP
			if(result.getMessage()!=null){
				OpenSPCoop2SoapMessage soapMessage = null;
				try{
					soapMessage = result.getMessage().castAsSoap();
					soapMessage.getSOAPHeader();
				} catch (Throwable soapException) {
					result.setMessage(null);
					result.setParseException(ParseExceptionUtils.buildParseException(soapException));
				}
				
				soapMessage.setSoapAction(soapAction);
			}
						
			return result;
		}catch(Throwable t){
			OpenSPCoop2MessageParseResult result = new OpenSPCoop2MessageParseResult();
			result.setParseException(ParseExceptionUtils.buildParseException(t));
			return result;
		}
	}
	
	private static int readOffsetXmlInstruction(byte[]byteMsg, int startFrom,boolean eraserXmlTag, int offsetParam, boolean cleanEmptyChar) throws Exception{
		//System.out.println("START["+(startFrom)+"] OFFSET["+offsetParam+"]");
		int offset = offsetParam;
		int i = startFrom;
		
		// brucio spazi vuoti
		if(cleanEmptyChar){
			for( ; i<byteMsg.length; i++){
				if(((char)byteMsg[i])=='<'){
					break;
				}
			}
		}
		
		String xml = "";
		if(byteMsg.length>i+5){
			xml = "" + ((char)byteMsg[i]) + ((char)byteMsg[i+1]) + ((char)byteMsg[i+2]) +((char)byteMsg[i+3]) + ((char)byteMsg[i+4]);
			//System.out.println("CHECK ["+xml+"]");
			if(xml.equals("<?xml")){
				if(eraserXmlTag){
					// eliminazione tag <?xml
					for( ; i<byteMsg.length; i++){
						if(((char)byteMsg[i])=='>'){
							break;
						}
					}
					offset = i+1;
				}else{
					// lancio eccezione
					throw new Exception("Tag <?xml non permesso con la funzionalita di imbustamento SOAP");
				}
				//System.out.println("RIGIRO CON START["+(i+1)+"] OFFSET["+offset+"]");
				return readOffsetXmlInstruction(byteMsg, (i+1), eraserXmlTag, offset, true);
			}
			else{
				//System.out.println("FINE A["+offset+"]");
				return offset;
			}
		}
		else{
			//System.out.println("FINE B["+offset+"]");
			return offset;
		}
	}
	
	
	
	
	// ********** METODI PUBBLICI *************
		
	
	/*
	 * Messaggi ottenuti tramite InputStream o byte[]
	 */
	
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportRequestContext requestContext, 
			InputStream messageInput, NotifierInputStreamParams notifierInputStreamParams) {	
		return this.createMessage(messageType, requestContext, messageInput, notifierInputStreamParams, 
				AttachmentsProcessingMode.getMemoryCacheProcessingMode());
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportRequestContext requestContext, 
			InputStream messageInput, NotifierInputStreamParams notifierInputStreamParams, 
			AttachmentsProcessingMode attachmentsProcessingMode) {	
		return _internalCreateMessage(messageType, MessageRole.REQUEST, requestContext, messageInput, notifierInputStreamParams,  
				attachmentsProcessingMode, 0);
	}
	
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportRequestContext requestContext, 
			byte[] messageInput) {	
		return this.createMessage(messageType, requestContext, messageInput, null, 
				AttachmentsProcessingMode.getMemoryCacheProcessingMode());
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportRequestContext requestContext, 
			byte[] messageInput, 
			AttachmentsProcessingMode attachmentsProcessingMode) {	
		return this.createMessage(messageType, requestContext, messageInput, null, 
				attachmentsProcessingMode);
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportRequestContext requestContext, 
			byte[] messageInput, NotifierInputStreamParams notifierInputStreamParams) {	
		return this.createMessage(messageType, requestContext, messageInput, notifierInputStreamParams, 
				AttachmentsProcessingMode.getMemoryCacheProcessingMode());
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportRequestContext requestContext, 
			byte[] messageInput, NotifierInputStreamParams notifierInputStreamParams, 
			AttachmentsProcessingMode attachmentsProcessingMode) {	
		return _internalCreateMessage(messageType, MessageRole.REQUEST, requestContext, messageInput, notifierInputStreamParams,  
				attachmentsProcessingMode, 0);
	}
	
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportResponseContext responseContext, 
			InputStream messageInput, NotifierInputStreamParams notifierInputStreamParams) {	
		return this.createMessage(messageType, responseContext, messageInput, notifierInputStreamParams, 
				AttachmentsProcessingMode.getMemoryCacheProcessingMode());
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportResponseContext responseContext, 
			InputStream messageInput, NotifierInputStreamParams notifierInputStreamParams, 
			AttachmentsProcessingMode attachmentsProcessingMode) {	
		return _internalCreateMessage(messageType, MessageRole.RESPONSE, responseContext, messageInput, notifierInputStreamParams,  
				attachmentsProcessingMode, 0);
	}
	
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportResponseContext responseContext, 
			byte[] messageInput) {	
		return this.createMessage(messageType, responseContext, messageInput, null, 
				AttachmentsProcessingMode.getMemoryCacheProcessingMode());
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportResponseContext responseContext, 
			byte[] messageInput, 
			AttachmentsProcessingMode attachmentsProcessingMode) {	
		return this.createMessage(messageType, responseContext, messageInput, null, 
				attachmentsProcessingMode);
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportResponseContext responseContext, 
			byte[] messageInput, NotifierInputStreamParams notifierInputStreamParams) {	
		return this.createMessage(messageType, responseContext, messageInput, notifierInputStreamParams, 
				AttachmentsProcessingMode.getMemoryCacheProcessingMode());
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportResponseContext responseContext, 
			byte[] messageInput, NotifierInputStreamParams notifierInputStreamParams, 
			AttachmentsProcessingMode attachmentsProcessingMode) {	
		return _internalCreateMessage(messageType, MessageRole.RESPONSE, responseContext, messageInput, notifierInputStreamParams,  
				attachmentsProcessingMode, 0);
	}
	
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, MessageRole messageRole, String contentType, 
			InputStream messageInput, NotifierInputStreamParams notifierInputStreamParams) {	
		return this.createMessage(messageType, messageRole, contentType, messageInput, notifierInputStreamParams, 
				AttachmentsProcessingMode.getMemoryCacheProcessingMode());
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, MessageRole messageRole, String contentType, 
			InputStream messageInput, NotifierInputStreamParams notifierInputStreamParams, 
			AttachmentsProcessingMode attachmentsProcessingMode) {	
		return _internalCreateMessage(messageType, messageRole, contentType, messageInput, notifierInputStreamParams, 
				attachmentsProcessingMode, 0);
	}
	
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, MessageRole messageRole, String contentType, 
			byte[] messageInput) {	
		return this.createMessage(messageType, messageRole, contentType, messageInput, null, 
				AttachmentsProcessingMode.getMemoryCacheProcessingMode());
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, MessageRole messageRole, String contentType, 
			byte[] messageInput,
			AttachmentsProcessingMode attachmentsProcessingMode) {	
		return this.createMessage(messageType, messageRole, contentType, messageInput, null, 
				attachmentsProcessingMode);
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, MessageRole messageRole, String contentType, 
			byte[] messageInput, NotifierInputStreamParams notifierInputStreamParams) {	
		return this.createMessage(messageType, messageRole, contentType, messageInput, notifierInputStreamParams, 
				AttachmentsProcessingMode.getMemoryCacheProcessingMode());
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, MessageRole messageRole, String contentType, 
			byte[] messageInput, NotifierInputStreamParams notifierInputStreamParams, 
			AttachmentsProcessingMode attachmentsProcessingMode) {	
		return _internalCreateMessage(messageType, messageRole, contentType, messageInput, notifierInputStreamParams, 
				attachmentsProcessingMode, 0);
	}
	
	
	
	
	/*
	 * Enveloping
	 */
	
	public OpenSPCoop2MessageParseResult envelopingMessage(MessageType messageType, String contentTypeForEnvelope, String soapAction,
			TransportRequestContext requestContext, 
			InputStream messageInput,NotifierInputStreamParams notifierInputStreamParams, 
			boolean eraserXmlTag) {
		return envelopingMessage(messageType, contentTypeForEnvelope, soapAction, requestContext,
				messageInput, notifierInputStreamParams, AttachmentsProcessingMode.getMemoryCacheProcessingMode(), eraserXmlTag);
	}
	public OpenSPCoop2MessageParseResult envelopingMessage(MessageType messageType, String contentTypeForEnvelope, String soapAction,
			TransportRequestContext requestContext, 
			InputStream messageInput,NotifierInputStreamParams notifierInputStreamParams, 
			AttachmentsProcessingMode attachmentsProcessingMode,
			boolean eraserXmlTag) {
		return this._internalEnvelopingMessage(messageType, MessageRole.REQUEST, contentTypeForEnvelope, soapAction, requestContext, 
				messageInput, notifierInputStreamParams, attachmentsProcessingMode, eraserXmlTag);
	}
	
	public OpenSPCoop2MessageParseResult envelopingMessage(MessageType messageType, String contentTypeForEnvelope, String soapAction,
			TransportRequestContext requestContext, 
			byte[] messageInput,NotifierInputStreamParams notifierInputStreamParams, 
			boolean eraserXmlTag) {
		return envelopingMessage(messageType, contentTypeForEnvelope, soapAction, requestContext,
				messageInput, notifierInputStreamParams, AttachmentsProcessingMode.getMemoryCacheProcessingMode(), eraserXmlTag);
	}
	public OpenSPCoop2MessageParseResult envelopingMessage(MessageType messageType, String contentTypeForEnvelope, String soapAction,
			TransportRequestContext requestContext, 
			byte[] messageInput,NotifierInputStreamParams notifierInputStreamParams, 
			AttachmentsProcessingMode attachmentsProcessingMode,
			boolean eraserXmlTag) {
		return this._internalEnvelopingMessage(messageType, MessageRole.REQUEST, contentTypeForEnvelope, soapAction, requestContext, 
				messageInput, notifierInputStreamParams, attachmentsProcessingMode, eraserXmlTag);
	}
	
	public OpenSPCoop2MessageParseResult envelopingMessage(MessageType messageType, String contentTypeForEnvelope, String soapAction,
			TransportResponseContext responseContext, 
			InputStream messageInput,NotifierInputStreamParams notifierInputStreamParams, 
			boolean eraserXmlTag) {
		return envelopingMessage(messageType, contentTypeForEnvelope, soapAction, responseContext,
				messageInput, notifierInputStreamParams, AttachmentsProcessingMode.getMemoryCacheProcessingMode(), eraserXmlTag);
	}
	public OpenSPCoop2MessageParseResult envelopingMessage(MessageType messageType, String contentTypeForEnvelope, String soapAction,
			TransportResponseContext responseContext, 
			InputStream messageInput,NotifierInputStreamParams notifierInputStreamParams, 
			AttachmentsProcessingMode attachmentsProcessingMode,
			boolean eraserXmlTag) {
		return this._internalEnvelopingMessage(messageType, MessageRole.RESPONSE, contentTypeForEnvelope, soapAction, responseContext, 
				messageInput, notifierInputStreamParams, attachmentsProcessingMode, eraserXmlTag);
	}
	
	public OpenSPCoop2MessageParseResult envelopingMessage(MessageType messageType, String contentTypeForEnvelope, String soapAction,
			TransportResponseContext responseContext, 
			byte[] messageInput,NotifierInputStreamParams notifierInputStreamParams, 
			boolean eraserXmlTag) {
		return envelopingMessage(messageType, contentTypeForEnvelope, soapAction, responseContext,
				messageInput, notifierInputStreamParams, AttachmentsProcessingMode.getMemoryCacheProcessingMode(), eraserXmlTag);
	}
	public OpenSPCoop2MessageParseResult envelopingMessage(MessageType messageType, String contentTypeForEnvelope, String soapAction,
			TransportResponseContext responseContext, 
			byte[] messageInput,NotifierInputStreamParams notifierInputStreamParams, 
			AttachmentsProcessingMode attachmentsProcessingMode,
			boolean eraserXmlTag) {
		return this._internalEnvelopingMessage(messageType, MessageRole.RESPONSE, contentTypeForEnvelope, soapAction, responseContext, 
				messageInput, notifierInputStreamParams, attachmentsProcessingMode, eraserXmlTag);
	}
	
	public OpenSPCoop2MessageParseResult envelopingMessage(MessageType messageType, MessageRole messageRole, 
			String contentTypeForEnvelope, String soapAction,
			InputStream messageInput,NotifierInputStreamParams notifierInputStreamParams, 
			boolean eraserXmlTag) {
		return envelopingMessage(messageType, messageRole, contentTypeForEnvelope, soapAction,
				messageInput, notifierInputStreamParams, AttachmentsProcessingMode.getMemoryCacheProcessingMode(), eraserXmlTag);
	}
	public OpenSPCoop2MessageParseResult envelopingMessage(MessageType messageType, MessageRole messageRole, 
			String contentTypeForEnvelope, String soapAction,
			InputStream messageInput,NotifierInputStreamParams notifierInputStreamParams, 
			AttachmentsProcessingMode attachmentsProcessingMode,
			boolean eraserXmlTag) {
		return this._internalEnvelopingMessage(messageType, messageRole, contentTypeForEnvelope, soapAction, null, 
				messageInput, notifierInputStreamParams, attachmentsProcessingMode, eraserXmlTag);
	}
	
	public OpenSPCoop2MessageParseResult envelopingMessage(MessageType messageType, MessageRole messageRole, 
			String contentTypeForEnvelope, String soapAction,
			byte[] messageInput,NotifierInputStreamParams notifierInputStreamParams, 
			boolean eraserXmlTag) {
		return envelopingMessage(messageType, messageRole, contentTypeForEnvelope, soapAction,
				messageInput, notifierInputStreamParams, AttachmentsProcessingMode.getMemoryCacheProcessingMode(), eraserXmlTag);
	}
	public OpenSPCoop2MessageParseResult envelopingMessage(MessageType messageType, MessageRole messageRole, 
			String contentTypeForEnvelope, String soapAction,
			byte[] messageInput,NotifierInputStreamParams notifierInputStreamParams, 
			AttachmentsProcessingMode attachmentsProcessingMode,
			boolean eraserXmlTag) {
		return this._internalEnvelopingMessage(messageType, messageRole, contentTypeForEnvelope, soapAction, null, 
				messageInput, notifierInputStreamParams, attachmentsProcessingMode, eraserXmlTag);
	}
	
	
	
		
	/*
	 * Messaggi
	 */
	
	public OpenSPCoop2Message createMessage(MessageType messageType, MessageRole role, SOAPMessage msg) throws MessageException{
		return this._internalCreateMessage(messageType, role, msg);
	}
	
	
	
	/*
	 * Messaggi vuoti
	 */
	
	public OpenSPCoop2Message createEmptyMessage(MessageType messageType, MessageRole role) {
		return this.createEmptyMessage(messageType,role, null);
	}
	public OpenSPCoop2Message createEmptyMessage(MessageType messageType, MessageRole role, NotifierInputStreamParams notifierInputStreamParams) {
		try{
			if(MessageType.SOAP_11.equals(messageType) || MessageType.SOAP_12.equals(messageType)){
				
				byte[] xml = null;
				if(MessageType.SOAP_11.equals(messageType)){
					xml = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Body/></SOAP-ENV:Envelope>".getBytes();
				}
				else if(MessageType.SOAP_12.equals(messageType)){
					xml = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\"><SOAP-ENV:Body/></SOAP-ENV:Envelope>".getBytes();
				}
				
				OpenSPCoop2MessageParseResult result = this.createMessage(messageType, role , MessageUtilities.getDefaultContentType(messageType), 
						xml, notifierInputStreamParams);
				if(result.getParseException()!=null){
					// non dovrebbe succedere
					throw result.getParseException().getSourceException();
				}
				return result.getMessage();
			}
			else{
				return _internalCreateEmptyMessage(messageType, role);
			}
		}
		catch(Throwable e){
			System.err.println("Exception non gestibile durante la creazione di un messaggio vuoto. " + e);
			e.printStackTrace(System.err);
		}
		return null;
		
	}
	
	/*
	 * Messaggi di Errore
	 */
		
	public OpenSPCoop2Message createFaultMessage(MessageType messageType) {
		return this.createFaultMessage(messageType, Costanti.DEFAULT_SOAP_FAULT_STRING,null);
	}
	public OpenSPCoop2Message createFaultMessage(MessageType messageType, NotifierInputStreamParams notifierInputStreamParams) {
		return createFaultMessage(messageType, Costanti.DEFAULT_SOAP_FAULT_STRING,notifierInputStreamParams);
	}
	
	public OpenSPCoop2Message createFaultMessage(MessageType messageType, Throwable t) {
		return this.createFaultMessage(messageType, t,null);
	}
	public OpenSPCoop2Message createFaultMessage(MessageType messageType, Throwable t,NotifierInputStreamParams notifierInputStreamParams) {
		return createFaultMessage(messageType, t.getMessage(),notifierInputStreamParams);
	}
	
	public OpenSPCoop2Message createFaultMessage(MessageType messageType, String errore) {
		return this.createFaultMessage(messageType, errore,null);
	}
	public OpenSPCoop2Message createFaultMessage(MessageType messageType, String errore,NotifierInputStreamParams notifierInputStreamParams){
		try{
			String fault = null;
			String contentType = MessageUtilities.getDefaultContentType(messageType);
			if(MessageType.SOAP_11.equals(messageType)){
				fault = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
						+"<SOAP-ENV:Header/><SOAP-ENV:Body>"
						+"<SOAP-ENV:Fault>"
						+"<faultcode>SOAP-ENV:Server</faultcode>"
						+"<faultstring>" + errore + "</faultstring>"
						+"<faultactor>"+Costanti.DEFAULT_SOAP_FAULT_ACTOR+"</faultactor>"
						+"</SOAP-ENV:Fault>"
						+"</SOAP-ENV:Body></SOAP-ENV:Envelope>";
			}
			else if(MessageType.SOAP_12.equals(messageType)){
				fault = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\">"
						+"<SOAP-ENV:Header/><SOAP-ENV:Body>"
						+"<SOAP-ENV:Fault>"
						+"<SOAP-ENV:Code><SOAP-ENV:Value>SOAP-ENV:Server</SOAP-ENV:Value></SOAP-ENV:Code>"
						+"<SOAP-ENV:Reason><SOAP-ENV:Text xml:lang=\"en-US\">" + errore + "</SOAP-ENV:Text></SOAP-ENV:Reason>"
						+"<SOAP-ENV:Role>"+Costanti.DEFAULT_SOAP_FAULT_ACTOR+"</SOAP-ENV:Role>"
						+"</SOAP-ENV:Fault>"
						+"</SOAP-ENV:Body></SOAP-ENV:Envelope>";
			}
			else if(MessageType.XML.equals(messageType)){
				fault = "<op2:Fault xmlns:op2=\""+Costanti.DEFAULT_SOAP_FAULT_ACTOR+"\">"
						+"<op2:Message>"+errore+"</op2:Message>"
						+"</op2:Fault>";
			}
			else if(MessageType.JSON.equals(messageType)){
				fault = "{ \"fault\" : { \"message\" : \""+errore+"\" , \"namespace\" : \""+Costanti.DEFAULT_SOAP_FAULT_ACTOR+"\" } }";
			}
			else{
				// default uso xml
				fault = "<op2:Fault xmlns:op2=\""+Costanti.DEFAULT_SOAP_FAULT_ACTOR+"\">"
						+"<op2:Message>"+errore+"</op2:Message>"
						+"</op2:Fault>";
				contentType = MessageUtilities.getDefaultContentType(MessageType.XML);
			}
			
			//System.out.println("XML ["+versioneSoap+"] ["+xml+"]");
			
			byte[] xmlByte = fault.getBytes();
			OpenSPCoop2MessageParseResult result = this.createMessage(messageType, MessageRole.FAULT , contentType, 
					xmlByte, notifierInputStreamParams);
			if(result.getParseException()!=null){
				// non dovrebbe succedere
				throw result.getParseException().getSourceException();
			}
			return result.getMessage();
		}
		catch(Throwable e){
			System.err.println("Exception non gestibile durante la creazione di un Fault. " + e);
			e.printStackTrace(System.err);
		}
		return null;
	}
	
	public static boolean isFaultXmlMessage(Node nodeXml){
		try{
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME_ERRORE_APPLICATIVO+"]vs["+nodeXml.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+nodeXml.getNamespaceURI()+"]");
			if("Fault".equals(nodeXml.getLocalName()) && 
					Costanti.DEFAULT_SOAP_FAULT_ACTOR.equals(nodeXml.getNamespaceURI() ) 
				){
				return true;
			}
			else{
				return false;
			}
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isFaultJsonMessage(String jsonBody, Logger log){
		try{
			String namespace = JsonPathExpressionEngine.extractAndConvertResultAsString(jsonBody, "$.fault.namespace", log);
			if(namespace!=null && Costanti.DEFAULT_SOAP_FAULT_ACTOR.equals(namespace)) {
				return true;
			}
			return  false;
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}

}
