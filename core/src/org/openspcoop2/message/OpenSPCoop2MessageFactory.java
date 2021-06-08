/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import java.util.HashMap;

import javax.mail.internet.ContentType;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import org.openspcoop2.message.config.FaultBuilderConfig;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_soap_impl;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.notifier.NotifierInputStream;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.mime.MultipartUtils;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.rest.problem.JsonSerializer;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807Builder;
import org.openspcoop2.utils.rest.problem.XmlSerializer;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;


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
	public static OpenSPCoop2MessageFactory getDefaultMessageFactory() {
		if(OpenSPCoop2MessageFactory.openspcoopMessageFactory == null)
			try { OpenSPCoop2MessageFactory.initDefaultMessageFactory(); } catch (Exception e) { throw new RuntimeException(e); }
		return OpenSPCoop2MessageFactory.openspcoopMessageFactory;
	}
	
	public static void initDefaultMessageFactory() throws MessageException {
		initDefaultMessageFactory(false);
	}
	public static synchronized void initDefaultMessageFactory(boolean force) throws MessageException {
		try {
			if(OpenSPCoop2MessageFactory.openspcoopMessageFactory==null || force){
				OpenSPCoop2MessageFactory.openspcoopMessageFactory = (OpenSPCoop2MessageFactory) Loader.getInstance().newInstance(OpenSPCoop2MessageFactory.messageFactoryImpl);
				
				SOAPDocumentImpl.setCustomDocumentBuilderFactory(OpenSPCoop2MessageFactory.openspcoopMessageFactory.getDocumentBuilderFactoryClass());
				SOAPDocumentImpl.setCustomSAXParserFactory(OpenSPCoop2MessageFactory.openspcoopMessageFactory.getSAXParserFactoryClass());
				
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
	
	private static OpenSPCoop2Message _instanceForUtilities = null;
	private static synchronized void initInstanceForUtilities(OpenSPCoop2MessageFactory messageFactory) {
		if(_instanceForUtilities==null) {
			_instanceForUtilities = messageFactory.createEmptyMessage(MessageType.SOAP_11,MessageRole.NONE);
		}
	}
	private static OpenSPCoop2Message getInstanceForUtilities(OpenSPCoop2MessageFactory messageFactory) {
		if(_instanceForUtilities==null) {
			initInstanceForUtilities(messageFactory);
		}
		return _instanceForUtilities;
	}
	public static String getAsString(OpenSPCoop2MessageFactory messageFactory, Node ele, boolean consume){
		// E' indipendente dal tipo SOAP11, il tipo viene utilizzato come uno qualsiasi
		return getInstanceForUtilities(messageFactory).getAsString(ele,true);
	}
	public static byte[] getAsByte(OpenSPCoop2MessageFactory messageFactory, Node ele, boolean consume){
		// E' indipendente dal tipo SOAP11, il tipo viene utilizzato come uno qualsiasi
		return getInstanceForUtilities(messageFactory).getAsByte(ele,true);
	}
	
	
	// ********** SOAP Utilities mediati dall'implementazione dell'OpenSPCoop2Message *************
	
	private static HashMap<String, OpenSPCoop2Message> _instanceForSOAPUtilities = new HashMap<>();
	private static synchronized void initInstanceForSOAPUtilities(OpenSPCoop2MessageFactory messageFactory, MessageType messageType) {
		if(!_instanceForSOAPUtilities.containsKey(messageType.name())) {
			OpenSPCoop2Message msg = messageFactory.createEmptyMessage(messageType,MessageRole.NONE);
			_instanceForSOAPUtilities.put(messageType.name(), msg);
		}
	}
	private static OpenSPCoop2Message getInstanceForSOAPUtilities(OpenSPCoop2MessageFactory messageFactory, MessageType messageType) {
		if(!_instanceForSOAPUtilities.containsKey(messageType.name())) {
			initInstanceForSOAPUtilities(messageFactory, messageType);
		}
		return _instanceForSOAPUtilities.get(messageType.name());
	}
	
	public static SOAPElement createSOAPElement(OpenSPCoop2MessageFactory messageFactory, MessageType messageType,byte[] element) throws MessageException, MessageNotSupportedException{
		OpenSPCoop2Message message = getInstanceForSOAPUtilities(messageFactory, messageType);
		OpenSPCoop2SoapMessage soapMsg = message.castAsSoap();
		return soapMsg.createSOAPElement(element);
	}
	public static Element getFirstChildElement(OpenSPCoop2MessageFactory messageFactory, MessageType messageType,SOAPElement element) throws MessageException, MessageNotSupportedException{
		return getInstanceForSOAPUtilities(messageFactory, messageType).castAsSoap().getFirstChildElement(element);
	}
	

	
	
	
	// ********** SAAJ *************
	
	public abstract String getDocumentBuilderFactoryClass();
	
	public abstract String getSAXParserFactoryClass();
	
    public abstract Element convertoForXPathSearch(Element contenutoAsElement);
    
    public abstract void normalizeDocument(Document document);
	
    
    
    
    // ********** OpenSPCoop2Message builder *************
    
	protected abstract OpenSPCoop2Message _createMessage(MessageType messageType, SOAPMessage msg) throws MessageException;
	
	protected abstract OpenSPCoop2Message _createEmptyMessage(MessageType messageType) throws MessageException;
	
	protected abstract OpenSPCoop2Message _createMessage(MessageType messageType, TransportRequestContext requestContext, 
			InputStream is,  AttachmentsProcessingMode attachmentsProcessingMode, long overhead,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) throws MessageException;	
	protected abstract OpenSPCoop2Message _createMessage(MessageType messageType, TransportResponseContext responseContext, 
			InputStream is,  AttachmentsProcessingMode attachmentsProcessingMode, long overhead,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) throws MessageException;	
	protected abstract OpenSPCoop2Message _createMessage(MessageType messageType, String contentType, 
			InputStream is,  AttachmentsProcessingMode attachmentsProcessingMode, long overhead,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) throws MessageException;
	
	
	
	
	
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
			AttachmentsProcessingMode attachmentsProcessingMode, long overhead,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) {	
		
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
				contentType = transportRequestContext.getHeaderFirstValue(HttpConstants.CONTENT_TYPE);
			}
			else if(context instanceof TransportResponseContext){
				transportResponseContext = (TransportResponseContext) context;
				contentType = transportResponseContext.getHeaderFirstValue(HttpConstants.CONTENT_TYPE);
				codiceTrasportoRisposta = transportResponseContext.getCodiceTrasporto();
			}
			else if(context instanceof String){
				contentType = (String) context;
			}
			else{
				if(context==null) {
					if(!MessageType.BINARY.equals(messageType)) {
						throw new MessageException("Unsupported Empty Context for message '"+messageType+"' (No Content Type?)");
					}
				}
				else {
					throw new MessageException("Unsupported Context ["+context.getClass().getName()+"]");
				}
			}
			
			
			if(notifierInputStreamParams!=null && is!=null){
				nis = new NotifierInputStream(is,contentType,notifierInputStreamParams);
			}
			else{
				nis = is;
			}
			
			OpenSPCoop2Message op2Msg = null;
			if(transportRequestContext!=null){
				op2Msg = this._createMessage(messageType, transportRequestContext, nis, attachmentsProcessingMode, overhead, soapStreamReader);
				
				if(MessageType.SOAP_11.equals(messageType) || MessageType.SOAP_12.equals(messageType)){
					String soapAction = null;
					if(MessageType.SOAP_11.equals(messageType)){
						soapAction = transportRequestContext.getHeaderFirstValue(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION);
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
				op2Msg = this._createMessage(messageType, transportResponseContext, nis, attachmentsProcessingMode, overhead, soapStreamReader);
			}
			else{
				op2Msg = this._createMessage(messageType, contentType, nis, attachmentsProcessingMode, overhead, soapStreamReader);
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
				if(! (op2Msg instanceof AbstractOpenSPCoop2Message_soap_impl)) {
					op2Msg.castAsSoap().getSOAPHeader(); // Verifica struttura
				}
				else {
					// la verifica verra' fatta quando viene costruito il contenuto saaj
				}
			}
			
			result.setMessage(op2Msg);
		}catch(Throwable t){
			try {
				result.setParseException(ParseExceptionUtils.buildParseException(t));
			}catch(Throwable e) {
//				System.err.println("ECCEZIONE?");
//				e.printStackTrace(System.err);
				System.err.println("Message reading error: "+t.getMessage());
				t.printStackTrace(System.err);
				throw new RuntimeException(t.getMessage(),t);
			}
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
				result = _internalCreateMessage(messageType, messageRole, contentTypeForEnvelope, messageInput, notifierInputStreamParams, attachmentsProcessingMode, 0, null);
			}
			else if(context instanceof TransportRequestContext){
				TransportRequestContext trc = (TransportRequestContext) context;
				TransportUtils.removeObject(trc.getHeaders(), HttpConstants.CONTENT_TYPE);
				TransportUtils.addHeader(trc.getHeaders(), HttpConstants.CONTENT_TYPE, contentTypeForEnvelope);
				
				result = _internalCreateMessage(messageType, messageRole, trc, messageInput, notifierInputStreamParams, attachmentsProcessingMode, 0, null);
				
			}
			else if(context instanceof TransportResponseContext){
				TransportResponseContext trc = (TransportResponseContext) context;
				TransportUtils.removeObject(trc.getHeaders(), HttpConstants.CONTENT_TYPE);
				TransportUtils.addHeader(trc.getHeaders(), HttpConstants.CONTENT_TYPE, contentTypeForEnvelope);
				
				result = _internalCreateMessage(messageType, messageRole, trc, messageInput, notifierInputStreamParams, attachmentsProcessingMode, 0, null);
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
				attachmentsProcessingMode, 0, null);
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportRequestContext requestContext, 
			InputStream messageInput, NotifierInputStreamParams notifierInputStreamParams,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) {	
		return this.createMessage(messageType, requestContext, messageInput, notifierInputStreamParams, soapStreamReader, 
				AttachmentsProcessingMode.getMemoryCacheProcessingMode());
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportRequestContext requestContext, 
			InputStream messageInput, NotifierInputStreamParams notifierInputStreamParams,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader, 
			AttachmentsProcessingMode attachmentsProcessingMode) {	
		return _internalCreateMessage(messageType, MessageRole.REQUEST, requestContext, messageInput, notifierInputStreamParams,  
				attachmentsProcessingMode, 0, soapStreamReader);
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
				attachmentsProcessingMode, 0, null);
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
				attachmentsProcessingMode, 0, null);
	}
	
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportResponseContext responseContext, 
			InputStream messageInput, NotifierInputStreamParams notifierInputStreamParams,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) {	
		return this.createMessage(messageType, responseContext, messageInput, notifierInputStreamParams, soapStreamReader,
				AttachmentsProcessingMode.getMemoryCacheProcessingMode());
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, TransportResponseContext responseContext, 
			InputStream messageInput, NotifierInputStreamParams notifierInputStreamParams,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader, 
			AttachmentsProcessingMode attachmentsProcessingMode) {	
		return _internalCreateMessage(messageType, MessageRole.RESPONSE, responseContext, messageInput, notifierInputStreamParams,  
				attachmentsProcessingMode, 0, soapStreamReader);
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
				attachmentsProcessingMode, 0, null);
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
				attachmentsProcessingMode, 0, null);
	}
	
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, MessageRole messageRole, String contentType, 
			InputStream messageInput, NotifierInputStreamParams notifierInputStreamParams,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) {	
		return this.createMessage(messageType, messageRole, contentType, messageInput, notifierInputStreamParams, soapStreamReader,
				AttachmentsProcessingMode.getMemoryCacheProcessingMode());
	}
	public OpenSPCoop2MessageParseResult createMessage(MessageType messageType, MessageRole messageRole, String contentType, 
			InputStream messageInput, NotifierInputStreamParams notifierInputStreamParams,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader, 
			AttachmentsProcessingMode attachmentsProcessingMode) {	
		return _internalCreateMessage(messageType, messageRole, contentType, messageInput, notifierInputStreamParams, 
				attachmentsProcessingMode, 0, soapStreamReader);
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
				attachmentsProcessingMode, 0, null);
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
			// Fix per performance: creare un messaggio vuoto tramite il meccanismo sottostante, faceva utilizzare il metodo 'createMessage -> _internalCreateMessage' che portava
			// a richiemare la struttura del messaggio tramite il controllo: op2Msg.castAsSoap().getSOAPHeader(); // Verifica struttura
			// Tale controllo era costoso in termini di performance per questi messaggi vuoti.
			/*
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
			*/
			OpenSPCoop2Message msgEmpty = _internalCreateEmptyMessage(messageType, role);
			if(MessageType.SOAP_11.equals(messageType) || MessageType.SOAP_12.equals(messageType)){
				msgEmpty.setContentType(MessageUtilities.getDefaultContentType(messageType));
			}
			return msgEmpty;
			//}
				
		}catch(Throwable e){
			System.err.println("Exception non gestibile durante la creazione di un messaggio vuoto. " + e);
			e.printStackTrace(System.err);
		}
		return null;
		
	}
	
	/*
	 * Messaggi di Errore
	 */
		
	public OpenSPCoop2Message createFaultMessage(MessageType messageType, boolean useProblemRFC7807, FaultBuilderConfig faultBuilderConfig) {
		return this.createFaultMessage(messageType, useProblemRFC7807, faultBuilderConfig, Costanti.DEFAULT_SOAP_FAULT_STRING,null);
	}
	public OpenSPCoop2Message createFaultMessage(MessageType messageType, boolean useProblemRFC7807) {
		return this.createFaultMessage(messageType, useProblemRFC7807, Costanti.DEFAULT_SOAP_FAULT_STRING,null);
	}
	
	public OpenSPCoop2Message createFaultMessage(MessageType messageType, boolean useProblemRFC7807, FaultBuilderConfig faultBuilderConfig, NotifierInputStreamParams notifierInputStreamParams) {
		return createFaultMessage(messageType, useProblemRFC7807, faultBuilderConfig, Costanti.DEFAULT_SOAP_FAULT_STRING,notifierInputStreamParams);
	}
	public OpenSPCoop2Message createFaultMessage(MessageType messageType, boolean useProblemRFC7807, NotifierInputStreamParams notifierInputStreamParams) {
		return createFaultMessage(messageType, useProblemRFC7807, Costanti.DEFAULT_SOAP_FAULT_STRING,notifierInputStreamParams);
	}
	
	public OpenSPCoop2Message createFaultMessage(MessageType messageType,boolean useProblemRFC7807, FaultBuilderConfig faultBuilderConfig, Throwable t) {
		return this.createFaultMessage(messageType,useProblemRFC7807, faultBuilderConfig, t,null);
	}
	public OpenSPCoop2Message createFaultMessage(MessageType messageType,boolean useProblemRFC7807, Throwable t) {
		return this.createFaultMessage(messageType,useProblemRFC7807, t,null);
	}
	
	public OpenSPCoop2Message createFaultMessage(MessageType messageType,boolean useProblemRFC7807, FaultBuilderConfig faultBuilderConfig, 
			Throwable t,NotifierInputStreamParams notifierInputStreamParams) {
		return createFaultMessage(messageType, useProblemRFC7807, faultBuilderConfig, t.getMessage(),notifierInputStreamParams);
	}
	public OpenSPCoop2Message createFaultMessage(MessageType messageType,boolean useProblemRFC7807, 
			Throwable t,NotifierInputStreamParams notifierInputStreamParams) {
		return createFaultMessage(messageType, useProblemRFC7807, t.getMessage(),notifierInputStreamParams);
	}
	
	public OpenSPCoop2Message createFaultMessage(MessageType messageType, boolean useProblemRFC7807, FaultBuilderConfig faultBuilderConfig, 
			String errore) {
		return this.createFaultMessage(messageType, useProblemRFC7807, faultBuilderConfig, errore,null);
	}
	public OpenSPCoop2Message createFaultMessage(MessageType messageType, boolean useProblemRFC7807, 
			String errore) {
		return this.createFaultMessage(messageType, useProblemRFC7807, errore,null);
	}
	
	public OpenSPCoop2Message createFaultMessage(MessageType messageType,boolean useProblemRFC7807, 
			String errore,NotifierInputStreamParams notifierInputStreamParams){
		return this.createFaultMessage(messageType, useProblemRFC7807, null, errore, notifierInputStreamParams);
	}
	public OpenSPCoop2Message createFaultMessage(MessageType messageType,boolean useProblemRFC7807, FaultBuilderConfig faultBuilderConfig, 
			String erroreParam,NotifierInputStreamParams notifierInputStreamParams){ 
	
		if(faultBuilderConfig==null) {
			faultBuilderConfig = new FaultBuilderConfig();
		}
		if(faultBuilderConfig.getHttpReturnCode()==null) {
			faultBuilderConfig.setHttpReturnCode(503);
		}
		if(faultBuilderConfig.getGovwayReturnCode()==null) {
			faultBuilderConfig.setGovwayReturnCode(faultBuilderConfig.getHttpReturnCode());
		}
		
		String errore = erroreParam;
		if(faultBuilderConfig.getDetails()!=null) {
			errore = faultBuilderConfig.getDetails();
		}
		
		try{
			String fault = null;
			String contentType = MessageUtilities.getDefaultContentType(messageType);
			if(MessageType.SOAP_11.equals(messageType)){
				
				String prefixSoap = "SOAP-ENV";
				if(faultBuilderConfig.getPrefixSoap()!=null) {
					prefixSoap = faultBuilderConfig.getPrefixSoap();
				}
				
				fault = "<"+prefixSoap+":Envelope xmlns:"+prefixSoap+"=\"http://schemas.xmlsoap.org/soap/envelope/\">"
						+"<"+prefixSoap+":Header/><"+prefixSoap+":Body>"
						+"<"+prefixSoap+":Fault>";
				if(faultBuilderConfig.getErrorCode()!=null) {
					if(prefixSoap.equals(faultBuilderConfig.getErrorCode().getPrefix())) {
						fault = fault
								+"<faultcode>"+faultBuilderConfig.getErrorCode().getPrefix()+":"+faultBuilderConfig.getErrorCode().getLocalPart()+"</faultcode>";
					}
					else {
						fault = fault
								+"<faultcode xmlns:"+faultBuilderConfig.getErrorCode().getPrefix()+"=\""+faultBuilderConfig.getErrorCode().getNamespaceURI()+
										"\">"+faultBuilderConfig.getErrorCode().getPrefix()+":"+faultBuilderConfig.getErrorCode().getLocalPart()+"</faultcode>";
					}
				}
				else {
					fault = fault		
							+"<faultcode>"+prefixSoap+":"+Costanti.SOAP11_FAULT_CODE_SERVER+"</faultcode>";
				}
				fault = fault
						+"<faultstring>" + errore + "</faultstring>";
				if(faultBuilderConfig.getActor()!=null) {
					fault = fault
							+"<faultactor>"+faultBuilderConfig.getActor()+"</faultactor>";
				}
				else {
					fault = fault
							+"<faultactor>"+Costanti.DEFAULT_SOAP_FAULT_ACTOR+"</faultactor>";
				}
				fault = fault
						+"</"+prefixSoap+":Fault>"
						+"</"+prefixSoap+":Body></"+prefixSoap+":Envelope>";
			}
			else if(MessageType.SOAP_12.equals(messageType)){
				
				String prefixSoap = "env";
				if(faultBuilderConfig.getPrefixSoap()!=null) {
					prefixSoap = faultBuilderConfig.getPrefixSoap();
				}
				
				String code12 = Costanti.SOAP12_FAULT_CODE_SERVER;
				if(faultBuilderConfig.getGovwayReturnCode()!=null) {
					if(faultBuilderConfig.getGovwayReturnCode()<=499) {
						code12 = Costanti.SOAP12_FAULT_CODE_CLIENT;
					}
				}
				fault = "<"+prefixSoap+":Envelope xmlns:"+prefixSoap+"=\"http://www.w3.org/2003/05/soap-envelope\">"
						+"<"+prefixSoap+":Header/><"+prefixSoap+":Body>"
						+"<"+prefixSoap+":Fault>"
						+"<"+prefixSoap+":Code><"+prefixSoap+":Value>"+prefixSoap+":"+code12+"</"+prefixSoap+":Value>";
				if(faultBuilderConfig.getErrorCode()!=null) {
					fault = fault
							+"<"+prefixSoap+":Subcode><"+prefixSoap+":Value xmlns:"+faultBuilderConfig.getErrorCode().getPrefix()+"=\""+faultBuilderConfig.getErrorCode().getNamespaceURI()+
								"\">"+faultBuilderConfig.getErrorCode().getPrefix()+":"+faultBuilderConfig.getErrorCode().getLocalPart()+"</"+prefixSoap+":Value></"+prefixSoap+":Subcode>";
				}
				fault = fault	
						+"</"+prefixSoap+":Code>"
						+"<"+prefixSoap+":Reason><"+prefixSoap+":Text xml:lang=\"en-US\">" + errore + "</"+prefixSoap+":Text></"+prefixSoap+":Reason>";
				if(faultBuilderConfig.getActor()!=null) {
					fault = fault
							+"<"+prefixSoap+":Role>"+faultBuilderConfig.getActor()+"</"+prefixSoap+":Role>";
				}
				else {
					fault = fault
							+"<"+prefixSoap+":Role>"+Costanti.DEFAULT_SOAP_FAULT_ACTOR+"</"+prefixSoap+":Role>";
				}
				fault = fault
						+"</"+prefixSoap+":Fault>"
						+"</"+prefixSoap+":Body></"+prefixSoap+":Envelope>";
			}
			else if(MessageType.XML.equals(messageType)){
				if(useProblemRFC7807) {
					ProblemRFC7807Builder builder = null;
					if(faultBuilderConfig.isRfc7807Type()) {
						if(faultBuilderConfig.getRfc7807WebSite()!=null) {
							builder = new ProblemRFC7807Builder(faultBuilderConfig.getRfc7807WebSite());
						}
						else {
							builder = new ProblemRFC7807Builder(true);
						}
					}
					else {
						builder = new ProblemRFC7807Builder(false);
					}
					ProblemRFC7807 problemRFC7807 = builder.buildProblem(faultBuilderConfig.getGovwayReturnCode());
					if(faultBuilderConfig.getRfc7807Title()!=null) {
						problemRFC7807.setTitle(faultBuilderConfig.getRfc7807Title());
					}
					if(errore!=null && !Costanti.DEFAULT_SOAP_FAULT_STRING.equals(errore)) {
						problemRFC7807.setDetail(errore);
					}
					XmlSerializer xmlSerializer = new XmlSerializer();
					fault = xmlSerializer.toString(problemRFC7807);
					contentType = HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807;
				}else {
					fault = "<op2:Fault xmlns:op2=\""+(faultBuilderConfig.getActor()!=null ? faultBuilderConfig.getActor() : Costanti.DEFAULT_SOAP_FAULT_ACTOR)+"\">"
							+"<op2:Message>"+errore+"</op2:Message>"
							+"</op2:Fault>";
				}
			}
			else if(MessageType.JSON.equals(messageType)){
				if(useProblemRFC7807) {
					ProblemRFC7807Builder builder = null;
					if(faultBuilderConfig.isRfc7807Type()) {
						if(faultBuilderConfig.getRfc7807WebSite()!=null) {
							builder = new ProblemRFC7807Builder(faultBuilderConfig.getRfc7807WebSite());
						}
						else {
							builder = new ProblemRFC7807Builder(true);
						}
					}
					else {
						builder = new ProblemRFC7807Builder(false);
					}
					ProblemRFC7807 problemRFC7807 = builder.buildProblem(faultBuilderConfig.getGovwayReturnCode());
					if(faultBuilderConfig.getRfc7807Title()!=null) {
						problemRFC7807.setTitle(faultBuilderConfig.getRfc7807Title());
					}
					if(errore!=null && !Costanti.DEFAULT_SOAP_FAULT_STRING.equals(errore)) {
						problemRFC7807.setDetail(errore);
					}
					JsonSerializer jsonSerializer = new JsonSerializer();
					fault = jsonSerializer.toString(problemRFC7807);
					contentType = HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807;
				}else {
					fault = "{ \"fault\" : { \"message\" : \""+errore+"\" , \"namespace\" : \""+(faultBuilderConfig.getActor()!=null ? faultBuilderConfig.getActor() : Costanti.DEFAULT_SOAP_FAULT_ACTOR)+"\" } }";
				}
			}
			else{
				// default uso xml
//				fault = "<op2:Fault xmlns:op2=\""+Costanti.DEFAULT_SOAP_FAULT_ACTOR+"\">"
//						+"<op2:Message>"+errore+"</op2:Message>"
//						+"</op2:Fault>";
//				contentType = MessageUtilities.getDefaultContentType(MessageType.XML);
				
				// modifica default in json
				if(useProblemRFC7807) {
					ProblemRFC7807Builder builder = null;
					if(faultBuilderConfig.isRfc7807Type()) {
						if(faultBuilderConfig.getRfc7807WebSite()!=null) {
							builder = new ProblemRFC7807Builder(faultBuilderConfig.getRfc7807WebSite());
						}
						else {
							builder = new ProblemRFC7807Builder(true);
						}
					}
					else {
						builder = new ProblemRFC7807Builder(false);
					}
					ProblemRFC7807 problemRFC7807 = builder.buildProblem(faultBuilderConfig.getGovwayReturnCode());
					if(faultBuilderConfig.getRfc7807Title()!=null) {
						problemRFC7807.setTitle(faultBuilderConfig.getRfc7807Title());
					}
					if(errore!=null && !Costanti.DEFAULT_SOAP_FAULT_STRING.equals(errore)) {
						problemRFC7807.setDetail(errore);
					}
					if(faultBuilderConfig.getRfc7807GovWayTypeHeaderErrorTypeName()!=null && faultBuilderConfig.getRfc7807GovWayTypeHeaderErrorTypeValue()!=null) {
						problemRFC7807.getCustom().put(faultBuilderConfig.getRfc7807GovWayTypeHeaderErrorTypeName(), 
								faultBuilderConfig.getRfc7807GovWayTypeHeaderErrorTypeValue());
					}
					JsonSerializer jsonSerializer = new JsonSerializer();
					fault = jsonSerializer.toString(problemRFC7807);
					contentType = HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807;
				}else {
					fault = "{ \"fault\" : { \"message\" : \""+errore+"\" , \"namespace\" : \""+(faultBuilderConfig.getActor()!=null ? faultBuilderConfig.getActor() : Costanti.DEFAULT_SOAP_FAULT_ACTOR)+"\" } }";
					contentType = MessageUtilities.getDefaultContentType(MessageType.JSON);
				}
			}
			
			//System.out.println("XML ["+versioneSoap+"] ["+xml+"]");
			
			byte[] xmlByte = fault.getBytes();
			OpenSPCoop2MessageParseResult result = this.createMessage(messageType, MessageRole.FAULT , contentType, 
					xmlByte, notifierInputStreamParams);
			if(result.getParseException()!=null){
				// non dovrebbe succedere
				throw result.getParseException().getSourceException();
			}
			OpenSPCoop2Message msg = result.getMessage();
			
			msg.addContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY, useProblemRFC7807 ? 
					org.openspcoop2.message.constants.Costanti.TIPO_RFC7807 : org.openspcoop2.message.constants.Costanti.TIPO_GOVWAY );
			
			if(faultBuilderConfig.getErrorCode()!=null) {
				if(faultBuilderConfig.getErrorCode().getPrefix()!=null) {
					msg.addContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY_PREFIX_CODE, faultBuilderConfig.getErrorCode().getPrefix() );
				}
				if(faultBuilderConfig.getErrorCode().getLocalPart()!=null) {
					msg.addContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY_CODE, faultBuilderConfig.getErrorCode().getLocalPart() );
				}
				msg.addContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY_DETAILS, errore );
			}
			
			if(faultBuilderConfig.getHeaderErrorTypeName()!=null && faultBuilderConfig.getHeaderErrorTypeValue()!=null) {
				msg.forceTransportHeader(faultBuilderConfig.getHeaderErrorTypeName(), faultBuilderConfig.getHeaderErrorTypeValue());
			}
			
			msg.setForcedResponseCode(faultBuilderConfig.getHttpReturnCode()+"");
			
			return msg;
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
