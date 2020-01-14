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



package org.openspcoop2.message.soap;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.io.output.CountingOutputStream;
import org.openspcoop2.message.OpenSPCoop2DataContentHandler;
import org.openspcoop2.message.OpenSPCoop2DataContentHandlerInputStream;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Libreria contenente metodi utili per la gestione della busta Soap.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TunnelSoapUtils {
	
	
	
	
	/* ********  ALLEGA BODY  ******** */ 
	
	public static void allegaBody(OpenSPCoop2Message message, String ns) throws MessageException{
		
		if(ServiceBinding.SOAP.equals(message.getServiceBinding())==false){
			throw new MessageException("Funzionalita 'ScartaBody' valida solamente per Service Binding SOAP");
		}
		OpenSPCoop2SoapMessage soapMessage = message.castAsSoap();				
		
		try{
		
			// E' permesso SOLO per messaggi senza attachment
			if (soapMessage.countAttachments() > 0) {
				throw new MessageException("La funzionalita' non e' permessa per messaggi SOAP With Attachments");
			}
			
			SOAPBody body = soapMessage.getSOAPPart().getEnvelope().getBody();
			if(body==null){
				throw new Exception("Body non presente");
			}
			if(body.hasFault()){
				throw new Exception("Body contenente un SOAPFault");
			}
			List<Node> listNode = SoapUtils.getNotEmptyChildNodes(message.getFactory(), body, false);
			boolean bodyWithMultiRootElement = false;
			if(listNode!=null && listNode.size()>1){
				//System.out.println("MULTI ELEMENT: "+listNode.size());
				bodyWithMultiRootElement = true;
			}
			byte[] bodySbustato = TunnelSoapUtils.sbustamentoSOAPEnvelope(message.getFactory(), soapMessage.getSOAPPart().getEnvelope());
			AttachmentPart ap = null;
			if(bodyWithMultiRootElement){
				//System.out.println("OCTECT");
				org.openspcoop2.utils.dch.InputStreamDataSource isSource = new org.openspcoop2.utils.dch.InputStreamDataSource("MultiRootElement", 
						HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM, bodySbustato);
				ap = soapMessage.createAttachmentPart(new DataHandler(isSource));
			}else{
				//System.out.println("XML");
				Element e = null;
				try{
					e = XMLUtils.getInstance(message.getFactory()).newElement(bodySbustato);
					Source streamSource = new DOMSource(e);
					ap = soapMessage.createAttachmentPart();
					ap.setContent(streamSource, HttpConstants.CONTENT_TYPE_TEXT_XML);
				}catch(Exception eParse){
					org.openspcoop2.utils.dch.InputStreamDataSource isSource = new org.openspcoop2.utils.dch.InputStreamDataSource("BodyNotParsable", 
							HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM, bodySbustato);
					ap = soapMessage.createAttachmentPart(new DataHandler(isSource));
				}
			}
			ap.setContentId(soapMessage.createContentID(ns));
			soapMessage.addAttachmentPart(ap);
			
			// Aggiungo contentID all'attachmet contenente la SOAPEnvelope
			// Necessario per essere compatibile con alcune implementazioni, es axis14, 
			// altrimenti essendo il ContentType senza Start element, Axis14 utilizza come xml per costruire la SOAPEnvelope 
			// il primo attachment nel messaggio MIME che contiene il ContentID.
			soapMessage.getSOAPPart().addMimeHeader(HttpConstants.CONTENT_ID, soapMessage.createContentID(ns));
			
			// Rimuovo contenuti del body
			soapMessage.getSOAPBody().removeContents();
			
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	/* ********  S B U S T A M E N T O    M E S S A G G I  ******** */ 

	public static boolean isTunnelOpenSPCoopSoap(OpenSPCoop2SoapMessage message) throws MessageException, MessageNotSupportedException{
		return isTunnelOpenSPCoopSoap(message.getFactory(), message.getSOAPBody());
	}
	public static boolean isTunnelOpenSPCoopSoap(OpenSPCoop2MessageFactory messageFactory, SOAPBody body){
		List<Node> bodyChildren = SoapUtils.getNotEmptyChildNodes(messageFactory, body);
		if(body!=null && 
				bodyChildren.size() > 0 && 
				bodyChildren.get(0)!=null &&
				"SOAPTunnel".equals(bodyChildren.get(0).getLocalName()) &&
				"http://www.govway.org/out/xml2soap".equals(bodyChildren.get(0).getNamespaceURI()) &&
				org.openspcoop2.utils.Costanti.OPENSPCOOP2.equals(bodyChildren.get(0).getPrefix())){
			return true;
		}else{
			return false;
		}
	}
	
	public static String getContentTypeTunnelOpenSPCoopSoap(SOAPBody body) throws MessageException {
		if(body!=null && body.hasChildNodes()){
			return ((SOAPElement)body.getChildElements().next()).getValue();
		}else{
			throw new MessageException("Body non presente");
		}
	}
	

	public static void sbustamentoMessaggio(OpenSPCoop2Message msgParam,OutputStream streamParam) throws MessageException, MessageNotSupportedException{

		if(!MessageType.SOAP_11.equals(msgParam.getMessageType()) && !MessageType.SOAP_12.equals(msgParam.getMessageType())){
			throw MessageNotSupportedException.newMessageNotSupportedException(msgParam.getMessageType());
		}
		OpenSPCoop2SoapMessage msg = msgParam.castAsSoap();
		
		CountingOutputStream cout = null;
		try{
						
			// Nota: non viene usato DocumentToStream, poiche' altrimenti viene prodotto l'<?xml ... /> 
			cout = new CountingOutputStream(streamParam);
			
			//	Sbustamento Senza Attachments
			if(msg.countAttachments() == 0){
				SOAPBody bd = msg.getSOAPBody();
				if(bd.hasFault()){
					SOAPFault fault = bd.getFault();
					cout.write(msg.getAsByte(fault, true));
				}else{
					java.util.Iterator<?> it = bd.getChildElements();
					while(it.hasNext()){
						Object bodyObject = it.next();
						if(!(bodyObject instanceof SOAPElement)) continue;
						SOAPElement bodyElement = (SOAPElement) bodyObject;
						cout.write(msg.getAsByte(bodyElement, true));
					}
				}
			}
			// Sbustamento Con Attachmnets
			else{
				SOAPBody body = msg.getSOAPBody();
				if(TunnelSoapUtils.isTunnelOpenSPCoopSoap(msgParam.getFactory(), body)){
					// Sbustamento OpenSPCoop
					AttachmentPart ap  = (AttachmentPart) msg.getAttachments().next();
					Object o = ap.getContent();
					//DataHandler dh = ap.getDataHandler();
					//InputStream inputDH = (InputStream) dh.getContent();
					InputStream inputDH = null;
					if(o instanceof OpenSPCoop2DataContentHandlerInputStream){
						inputDH = (OpenSPCoop2DataContentHandlerInputStream) o;
					}
					else if(o instanceof InputStream){
						inputDH = (InputStream) OpenSPCoop2DataContentHandler.getContent((InputStream)o);
					}
					else if(o instanceof byte[]){
						inputDH = (InputStream) OpenSPCoop2DataContentHandler.getContent(new ByteArrayInputStream((byte[]) o));
					} else {
						throw new Exception("Tipo non gestito: "+o.getClass().getName());
					}
					java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
			    	byte [] readB = new byte[8192];
					int readByte = 0;
					while((readByte = inputDH.read(readB))!= -1)
						bout.write(readB,0,readByte);
					inputDH.close();
					bout.close();
					cout.write(bout.toByteArray());
				}else{
				
					ByteArrayOutputStream sbustamentoAttachments = new ByteArrayOutputStream();
					msg.writeTo(sbustamentoAttachments, true);
					String msgString = sbustamentoAttachments.toString();
					byte [] msgByte =  sbustamentoAttachments.toByteArray();
					String soapEnvelopeStart = "<" + msg.getSOAPPart().getEnvelope().getPrefix() + ":" + msg.getSOAPPart().getEnvelope().getLocalName();
					String xmlTagDecode = "<?xml";
					String soapEnvelopeStop =  "</" + msg.getSOAPPart().getEnvelope().getPrefix() + ":" + msg.getSOAPPart().getEnvelope().getLocalName()+">";
					//System.out.println("SoapStart: "+soapEnvelopeStart);
					//System.out.println("SoapStartIndexOf: "+msgString.indexOf(soapEnvelopeStart));
					//System.out.println("SoapStop: "+soapEnvelopeStop);
					//System.out.println("SoapStopIndexOf: "+msgString.indexOf(soapEnvelopeStop));
					// Prima parte del Multipart
					if(msgString.indexOf(xmlTagDecode)!=-1){
						cout.write(msgByte,0,msgString.indexOf(xmlTagDecode));
					}else{
						cout.write(msgByte,0,msgString.indexOf(soapEnvelopeStart));
					}
					// Body
					SOAPBody bd = msg.getSOAPBody();
					if(bd.hasFault()){
						SOAPFault fault = bd.getFault();
						cout.write(msg.getAsByte(fault, true));
					}else{
						cout.write(msg.getAsByte(msg.getFirstChildElement(bd), true));
					}
					// Resto degli attachments
					int indexOf = msgString.indexOf(soapEnvelopeStop)+soapEnvelopeStop.length();
					cout.write(msgByte,indexOf,msgByte.length - indexOf);
				}
			}
			
			// Aggiorno le lunghezze del messaggio (normalmente le aggiorna la writeTo)
			msg.updateIncomingMessageContentLength();
			// Come dimensione di uscita utilizzo i bytes che produco
			cout.flush();
			msg.updateOutgoingMessageContentLength(cout.getByteCount());
			
		}
		catch (MessageException e){
			throw e;
		}
		catch (MessageNotSupportedException e){
			throw e;
		}
		catch (Exception e){
			throw new MessageException("Sbustamento msg_inputStream non riuscito: "+e.getMessage(),e);
		}finally{
			try{
				if(cout!=null)
					cout.close();
			}catch(Exception e){}
		}
		
		
	}
	
	/**
	 * Ritorna i bytes del contenuto del messaggio Soap passato come parametro
	 *
	 * @param msg Messaggio Soap da sbustare
	 * @return byte del contenuto (sbustati dalla SoapEnvelope).
	 * 
	 */
	public static byte[] sbustamentoMessaggio(OpenSPCoop2Message msg) throws MessageException, MessageNotSupportedException{
		ByteArrayOutputStream bodySbustato = new ByteArrayOutputStream();
		try{
			TunnelSoapUtils.sbustamentoMessaggio(msg,bodySbustato);
			return bodySbustato.toByteArray();	
		}
		catch (MessageException e){
			throw e;
		}
		catch (MessageNotSupportedException e){
			throw e;
		}
		catch(Exception e){
			try{
				if(bodySbustato!=null)
					bodySbustato.close();
			}catch(Exception eis){}	
			throw new MessageException("Sbustamento msg non riuscito: "+e.getMessage(),e);
		}
	}

	/**
	 * Ritorna i bytes del contenuto del messaggio Soap passato come parametro
	 *
	 * @param env SoapEnvelope da sbustare
	 * 
	 */
	public static byte[] sbustamentoSOAPEnvelope(OpenSPCoop2MessageFactory messageFactory, SOAPEnvelope env) throws MessageException, MessageNotSupportedException{
		return sbustamentoSOAPEnvelope(messageFactory, env, true);
	}
	public static byte[] sbustamentoSOAPEnvelope(OpenSPCoop2MessageFactory messageFactory, SOAPEnvelope env, boolean consume) throws MessageException, MessageNotSupportedException{
		ByteArrayOutputStream bout = null;
		
		try{
			SOAPBody bd = env.getBody();
			byte[] body = null;
			if(bd.hasFault()){
				SOAPFault fault = bd.getFault();
				body = OpenSPCoop2MessageFactory.getAsByte(messageFactory, fault, consume);
			}else{
				bout = new ByteArrayOutputStream();
				java.util.Iterator<?> it = bd.getChildElements();
				while(it.hasNext()){
					Object bodyElementObj = it.next();
					if(!(bodyElementObj instanceof SOAPElement)){
						continue;
					}
					SOAPElement bodyElement = (SOAPElement) bodyElementObj;
					bout.write(OpenSPCoop2MessageFactory.getAsByte(messageFactory, bodyElement, consume));
				}
				bout.flush();
				bout.close();
				body = bout.toByteArray();
				bout = null;
			}

			return body;

		}
		catch (Exception e){
			throw new MessageException("Sbustamento SoapEnvelope non riuscito: "+e.getMessage(),e);
		}
		finally{
			try{
				if(bout!=null)
					bout.close();
			}catch(Exception eis){}
		}
	}
	
	
	
	
	
	
	

	
	
	
	/* ********  I M B U S T A M E N T O    M E S S A G G I  ******** */ 

	/**
	 * Ritorna un messaggio che contiene i bytes in un attachment
	 *
	 * @param inputBody contenuto.
	 * @return msg Messaggio Soap imbustato
	 * 
	 */
	public static OpenSPCoop2Message imbustamentoMessaggioConAttachment(OpenSPCoop2MessageFactory messageFactory, MessageType messageType, MessageRole messageRole, 
			InputStream inputBody,String tipoAttachment,boolean buildAsDataHandler,String contentTypeMessaggioOriginale, String ns) throws MessageException, MessageNotSupportedException{
		
		if(!MessageType.SOAP_11.equals(messageType) && !MessageType.SOAP_12.equals(messageType)){
			throw MessageNotSupportedException.newMessageNotSupportedException(messageType);
		}
		try{
			// Metto inputBody in un byte[] proprio perche' il ByteArrayInputStream non deve essere chiuso.
			java.io.ByteArrayOutputStream byteBuffer = new java.io.ByteArrayOutputStream();
			byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
			int readByte = 0;
			while((readByte = inputBody.read(readB))!= -1){
				byteBuffer.write(readB,0,readByte);
			}
			inputBody.close();
			if(byteBuffer.size()==0){
				throw new MessageException("Contenuto da imbustare non presente");
			}
			
			return TunnelSoapUtils.imbustamentoMessaggioConAttachment(messageFactory, messageType, messageRole, byteBuffer.toByteArray(), tipoAttachment, buildAsDataHandler, contentTypeMessaggioOriginale, ns);
			
		}
		catch (MessageException e){
			throw e;
		}
		catch (MessageNotSupportedException e){
			throw e;
		}
		catch (Exception e){
			throw new MessageException("Imbustamento msgConAttachment_inputStream non riuscito: "+e.getMessage(),e);
		}
	}
	
	public static OpenSPCoop2Message imbustamentoMessaggioConAttachment(OpenSPCoop2MessageFactory messageFactory, MessageType messageType, MessageRole messageRole, 
			byte [] inputBody,String tipoAttachment,boolean buildAsDataHandler,String contentTypeMessaggioOriginale, String ns) throws MessageException, MessageNotSupportedException{
		
		if(!MessageType.SOAP_11.equals(messageType) && !MessageType.SOAP_12.equals(messageType)){
			throw MessageNotSupportedException.newMessageNotSupportedException(messageType);
		}
		OpenSPCoop2Message msg = null;
		try{
			msg = messageFactory.createEmptyMessage(messageType,messageRole);
			return imbustamentoMessaggioConAttachment(msg, inputBody, tipoAttachment, buildAsDataHandler, contentTypeMessaggioOriginale, ns);
		}
		catch (MessageException e){
			throw e;
		}
		catch (MessageNotSupportedException e){
			throw e;
		}
		catch (Exception e){
			throw new MessageException("Imbustamento msgConAttachment_inputStream non riuscito: "+e.getMessage(),e);
		}
	}
	public static OpenSPCoop2Message imbustamentoMessaggioConAttachment(OpenSPCoop2Message msgParam, 
			byte [] inputBody,String tipoAttachment,boolean buildAsDataHandler,String contentTypeMessaggioOriginale, String ns) throws MessageException, MessageNotSupportedException{
		
		if(!MessageType.SOAP_11.equals(msgParam.getMessageType()) && !MessageType.SOAP_12.equals(msgParam.getMessageType())){
			throw MessageNotSupportedException.newMessageNotSupportedException(msgParam.getMessageType());
		}
		OpenSPCoop2SoapMessage msg = msgParam.castAsSoap();
		try{
			
			if(inputBody==null || inputBody.length<=0){
				throw new UtilsException("Contenuto da imbustare non presente");
			}
						
			SOAPBody soapBody = msg.getSOAPBody();
			QName name = null;
			if(HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP.equals(tipoAttachment)){
				name = new QName(Costanti.SOAP_TUNNEL_NAMESPACE,
						Costanti.SOAP_TUNNEL_ATTACHMENT_ELEMENT_OPENSPCOOP2_TYPE,org.openspcoop2.utils.Costanti.OPENSPCOOP2);	    
			}else{
				name = new QName(Costanti.SOAP_TUNNEL_NAMESPACE,
						Costanti.SOAP_TUNNEL_ATTACHMENT_ELEMENT,org.openspcoop2.utils.Costanti.OPENSPCOOP2);	 
			}
			SOAPElement bodyElement = soapBody.addChildElement(name);
			
			if(HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP.equals(tipoAttachment)){
				if(contentTypeMessaggioOriginale==null){
					throw new Exception("ContentType messaggio per cui applicare il tunnel non definito?");
				}else{
					bodyElement.setValue(contentTypeMessaggioOriginale);
				}
			}
			AttachmentPart ap = msg.createAttachmentPart();			
			
			if(buildAsDataHandler){
				ap.setDataHandler(new DataHandler(new ByteArrayDataSource(inputBody,tipoAttachment)));
				TunnelSoapUtils.saveAttachmentOpenSPCoop(ap);		
			}else{
				ap.setRawContentBytes(inputBody,0,inputBody.length,tipoAttachment);
				//ap.setContent(new ByteArrayInputStream(inputBody),tipoAttachment);
			}
			ap.setContentId(msg.createContentID(ns));
			msg.addAttachmentPart(ap);
			//System.out.println("debug - start");
			//msg.writeTo(System.out);
			//System.out.println(msg.getAsString(msg.getSOAPPart().getEnvelope()));
			//System.out.println("debug - stop");
			return msg;

		}
		catch (MessageException e){
			throw e;
		}
		catch (MessageNotSupportedException e){
			throw e;
		}
		catch (Exception e){
			throw new MessageException("Imbustamento msgConAttachment_inputStream non riuscito: "+e.getMessage(),e);
		}
	}
	private static void saveAttachmentOpenSPCoop(AttachmentPart ap) throws MessageException{
		try{
			// FIX: se non c'e' il dump abilitato serve il codice seguente per forzare il salvataggio, in caso si passi da DataHandler.
			javax.activation.DataHandler dh= ap.getDataHandler();  
	    	java.io.InputStream inputDH = dh.getInputStream();
			java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
	    	byte [] readB = new byte[8192];
			int readByte = 0;
			while((readByte = inputDH.read(readB))!= -1)
				bout.write(readB,0,readByte);
			inputDH.close();
			bout.flush();
			bout.close();
			ap.setDataHandler(new DataHandler(bout.toByteArray(),ap.getContentType()));
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	/**
	 * Ritorna un messaggio  che contiene i byte in un attachment
	 *
	 * @param body byte del contenuto.
	 * @return msg Messaggio Soap imbustato
	 * 
	 */
	public static OpenSPCoop2Message imbustamentoMessaggioConAttachment(OpenSPCoop2MessageFactory messageFactory, MessageType messageType, MessageRole messageRole, byte [] body, String contentTypeMessaggioOriginale, String ns) throws MessageException, MessageNotSupportedException{
		
		if(!MessageType.SOAP_11.equals(messageType) && !MessageType.SOAP_12.equals(messageType)){
			throw MessageNotSupportedException.newMessageNotSupportedException(messageType);
		}
		
		OpenSPCoop2Message risposta = null;
		try{	    
			risposta = TunnelSoapUtils.imbustamentoMessaggioConAttachment(messageFactory, messageType, messageRole, body,HttpConstants.CONTENT_TYPE_PLAIN,false, contentTypeMessaggioOriginale, ns);
			return risposta;
		}
		catch (MessageException e){
			throw e;
		}
		catch (MessageNotSupportedException e){
			throw e;
		}
		catch (Exception e){
			throw new MessageException("Imbustamento msgConAttachment non riuscito: "+e.getMessage(),e);
		}
	}

	
	
}

