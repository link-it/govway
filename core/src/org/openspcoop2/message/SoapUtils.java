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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.soap.util.mime.ByteArrayDataSource;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * Libreria contenente metodi utili per la gestione della busta Soap.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SoapUtils {
	
	public static SOAPFactory getSoapFactory(SOAPVersion versioneSoap) throws SOAPException {
		if(SOAPVersion.SOAP11.equals(versioneSoap))
			return OpenSPCoop2MessageFactory.getMessageFactory().getSoapFactory11();
		else
			return OpenSPCoop2MessageFactory.getMessageFactory().getSoapFactory12();
	}
	public static MessageFactory getMessageFactory() throws SOAPException {
		return OpenSPCoop2MessageFactory.getMessageFactory().getSoapMessageFactory();
	}
		
	public static Vector<Node> getNotEmptyChildNodes(Node e){
		return getNotEmptyChildNodes(e, true);
	}
	public static Vector<Node> getNotEmptyChildNodes(Node e, boolean consideraTextNotEmptyAsNode){
		return XMLUtils.getInstance().getNotEmptyChildNodes(e, consideraTextNotEmptyAsNode);
	}
	
	public static Node getFirstNotEmptyChildNode(Node e){
		return getFirstNotEmptyChildNode(e, true);
	}
	public static Node getFirstNotEmptyChildNode(Node e, boolean consideraTextNotEmptyAsNode){
		return XMLUtils.getInstance().getFirstNotEmptyChildNode(e, consideraTextNotEmptyAsNode);
	}
	
	public static Vector<SOAPElement> getNotEmptyChildSOAPElement(SOAPElement e){
		Vector<SOAPElement> vec = new Vector<SOAPElement>();
		Iterator<?> soapElements = e.getChildElements();
		while (soapElements.hasNext()) {
			Object soapElement = soapElements.next();
			if(soapElement instanceof SOAPElement){
				vec.add((SOAPElement)soapElement);
			}
		}
		return vec;
	}
	
	public static SOAPElement getNotEmptyFirstChildSOAPElement(SOAPElement e){
		Iterator<?> soapElements = e.getChildElements();
		while (soapElements.hasNext()) {
			Object soapElement = soapElements.next();
			if(soapElement instanceof SOAPElement){
				return (SOAPElement)soapElement;
			}
		}
		return null;
	}

	
	
	
	
	
	
	
	
	/* ********  L E T T U R A  V A L O R I  ******** */
	
	public static String getSoapAction(OpenSPCoop2Message msg)throws UtilsException{
		
		String soapAction = null;
		try{
			MimeHeaders mhs =  msg.getMimeHeaders();
			Iterator<?> it = mhs.getAllHeaders();
			while(it.hasNext()){
				MimeHeader mh = (MimeHeader) it.next();
				if("soapaction".equalsIgnoreCase(mh.getName()))
					soapAction = mh.getValue();
			}
			if(soapAction==null){
				if(msg.getProperty("SOAPAction")!=null){
					soapAction =  (String) msg.getProperty("SOAPAction");
				}
				else if(msg.getProperty("soapaction")!=null){
					soapAction =  (String) msg.getProperty("soapaction");
				}
				else if(msg.getProperty("soapAction")!=null){
					soapAction =  (String) msg.getProperty("soapAction");
				}
				else if(msg.getProperty("SoapAction")!=null){
					soapAction =  (String) msg.getProperty("SoapAction");
				}
				else if(msg.getProperty("SOAPACTION")!=null){
					soapAction =  (String) msg.getProperty("SOAPACTION");
				}
			}
			return soapAction;
		}catch(Exception e){
			throw new UtilsException("Lettura SOAPAction non riuscita",e);
		}
	}
	
	public static boolean matchLocalName(Node nodo,String nodeName,String prefix,String namespace){
		if(nodo==null)
			return false;
		if(nodo.getNodeName()==null)
			return false;
		// Il nodo possiede il prefisso atteso
		if(nodo.getNodeName().equals(prefix+nodeName))
			return true;
		// Il nodo puo' ridefinire il prefisso ridefinendo il namespace
		String namespaceNodo = nodo.getNamespaceURI();
		if(namespaceNodo!=null && namespaceNodo.equals(namespace)){
			String xmlns = nodo.getPrefix();
			if(xmlns == null){ 
				xmlns = "";
			}else if(!xmlns.equals("")){
				xmlns = xmlns + ":";
			}
			if(nodo.getNodeName().equals(xmlns+nodeName))
				return true;
		}
		return false;
	} 
	
	public static Node getAttributeNode(Node node,String attributeName){
		if (node == null)
		{
			return null;
		}
		NamedNodeMap map = node.getAttributes();
		if(map==null || map.getLength()==0){
			return null;
		}
		else{
			return map.getNamedItem(attributeName);
		}
	}
	public static Node getQualifiedAttributeNode(Node node,String attributeName,String namespace){
		if (node == null)
		{
			return null;
		}
		NamedNodeMap map = node.getAttributes();
		if(map==null || map.getLength()==0){
			return null;
		}
		else{
			return map.getNamedItemNS(namespace, attributeName);
		}
	}

	
	

	
	



	/* ********  FAULT  ******** */ 


	public static String toString(SOAPFault fault) throws UtilsException{
		return SoapUtils.toString(fault,true);
	}
	
	public static String toString(SOAPFault fault,boolean printDetails) throws UtilsException{
		try{
			if(printDetails){
				if(fault!=null){
					return OpenSPCoop2MessageFactory.getMessageFactory().createMessage(SOAPVersion.SOAP11).getAsString(fault,true);
				}else{
					return "SOAPFault non presente";
				}	
			}
			else{
				StringBuffer bf = new StringBuffer();
				if(fault!=null){
					bf.append("SOAPFault");
					if(fault.getFaultCode()!=null && !"".equals(fault.getFaultCode())){
						bf.append(" faultCode["+fault.getFaultCode()+"]");
					}
					if(fault.getFaultActor()!=null && !"".equals(fault.getFaultActor())){
						bf.append(" faultActor["+fault.getFaultActor()+"]");
					}
					if(fault.getFaultString()!=null && !"".equals(fault.getFaultString())){
						bf.append(" faultString["+fault.getFaultString()+"]");
					}
					
				}else{
					return "SOAPFault non presente";
				}
				
				return bf.toString();
			}
			
		} catch(Exception e) {
			throw new UtilsException("toString SOAPFault: "+e.getMessage(),e);
		}
	}



	
	
	
	
	/* ********  M E S S A G G I    V U O T I    ******** */ 

	/**
	 * Metodo che si occupa di costruire un messaggio SOAP vuoto (senza Header e Body). 
	 *
	 * @return bytes del messaggio Soap 'vuoto' in caso di successo, null altrimenti.
	 * 
	 */
	public static OpenSPCoop2Message build_Soap_Empty(SOAPVersion versioneSoap) {

		try{
			OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getMessageFactory();
			OpenSPCoop2Message msg = mf.createEmptySOAPMessage(versioneSoap);
			return msg;
		} catch(Exception e) {
			return null;
		}
	}


	
	
	
	
	
	
	
	
	
	/* ********  M E S S A G G I    F A U L T     S O A P    ******** */ 
	
	/**
	 * Metodo che si occupa di costruire un messaggio <code>SOAPFault</code>  con i seguenti campi :
	 * <p>
	 * <ul>
	 *   <li> FaultString : parametro <var>aFault</var>,
	 *   <li> FaultCode   : parametro <var>aCode</var>,
	 *   <li> FaultActor  : parametro <var>aActor</var>,
	 *   <li> <code>Detail</code> composto da tanti Detail quanto sono la lunghezza dei parametri seguenti :
	 *   <ul>
	 *       <li> LocalName : parametro <var>aDetailLocalName</var>,
	 *       <li> Prefix    : parametro <var>aDetailPrefix</var>,
	 *       <li> URI       : parametro <var>aDetailURI</var>,
	 *   </ul>
	 * </ul>
	 *
	 * @param aFault faultString del SOAPFault.
	 * @param aActor faultActor del SOAPFault.
	 * @param aCode  faultCode del SOAPFault.
	 * @param dettaglioEccezione Dettaglio dell'eccezione
	 * @return <tt>byte[]</tt> del messaggio Soap Fault costruito in caso di successo, <tt>null</tt> altrimenti.
	 * 
	 */
	public static byte[] build_Soap_Fault(SOAPVersion versioneSoap, String aFault, String aActor, QName aCode, 
			Element dettaglioEccezione,boolean generaDetails) throws UtilsException  {

		ByteArrayOutputStream byteMessaggio = null;
		try{
			OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getMessageFactory();
			OpenSPCoop2Message msg = mf.createMessage(versioneSoap);
			SOAPEnvelope env = (msg.getSOAPPart()).getEnvelope();

			//log.info("Setting SOAPBody with SOAPFault");
			SOAPBody bdy = env.getBody();
			bdy.addFault();
			SOAPFault fault = bdy.getFault();

			//log.info("Setting Fault");
			
			if(aFault != null)
				fault.setFaultString(aFault);
			else
				fault.setFaultString("");

			//log.info("Setting Fault Code");
			
			if(aCode != null)
				fault.setFaultCode(aCode);

			//log.info("Setting Actor");
			if(aActor != null)
				fault.setFaultActor(aActor);

			//log.info("Setting Details");
			if(generaDetails && dettaglioEccezione!=null){
				fault.addDetail();	
				Detail d = fault.getDetail();
				d.appendChild(d.getOwnerDocument().importNode(dettaglioEccezione,true));
			}

			//log.info("Build complete MessageSOAPFault");

			// Return byte ....
			byteMessaggio = new ByteArrayOutputStream();
			msg.writeTo(byteMessaggio, true);
			//log.info("-----Imbustamento------");
			//log.info(byteMessaggio.toString());
			//log.info("-----Imbustamento------");

			byte [] risultato = byteMessaggio.toByteArray();
			byteMessaggio.close();
			return risultato;
		} catch(Exception e) {
			try{
				if(byteMessaggio!=null)
					byteMessaggio.close();
			}catch(Exception eis){}
			throw new UtilsException("Creazione MsgSOAPFault non riuscito: "+e.getMessage(),e);
		}
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ********  S B U S T A M E N T O    M E S S A G G I  ******** */ 

	public static boolean isTunnelOpenSPCoopSoap(SOAPBody body){
		Vector<Node> bodyChildren = SoapUtils.getNotEmptyChildNodes(body);
		if(body!=null && 
				bodyChildren.size() > 0 && 
				bodyChildren.get(0)!=null &&
				"SOAPTunnel".equals(bodyChildren.get(0).getLocalName()) &&
				"http://www.openspcoop2.org/pdd/services/PDtoSOAP".equals(bodyChildren.get(0).getNamespaceURI()) &&
				org.openspcoop2.utils.Costanti.OPENSPCOOP2.equals(bodyChildren.get(0).getPrefix())){
			return true;
		}else{
			return false;
		}
	}
	
	public static String getContentTypeTunnelOpenSPCoopSoap(SOAPBody body) throws UtilsException {
		if(body!=null && body.hasChildNodes()){
			return ((SOAPElement)body.getChildElements().next()).getValue();
		}else{
			throw new UtilsException("Body non presente");
		}
	}
	
	/**
	 * Ritorna i bytes del contenuto del messaggio Soap passato come parametro
	 *
	 * @param msg Messaggio Soap da sbustare
	 * @param streamParam Stream su cui scrivere il messaggio sbustato
	 * 
	 */
	public static void sbustamentoMessaggio(OpenSPCoop2Message msg,OutputStream streamParam) throws UtilsException{

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
				if(SoapUtils.isTunnelOpenSPCoopSoap(body)){
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
			
		}catch (Exception e){
			throw new UtilsException("Sbustamento msg_inputStream non riuscito: "+e.getMessage(),e);
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
	public static byte[] sbustamentoMessaggio(OpenSPCoop2Message msg) throws UtilsException{
		ByteArrayOutputStream bodySbustato = new ByteArrayOutputStream();
		try{
			SoapUtils.sbustamentoMessaggio(msg,bodySbustato);
			return bodySbustato.toByteArray();	
		}catch(Exception e){
			try{
				if(bodySbustato!=null)
					bodySbustato.close();
			}catch(Exception eis){}	
			throw new UtilsException("Sbustamento msg non riuscito: "+e.getMessage(),e);
		}
	}

	/**
	 * Ritorna i bytes del contenuto del messaggio Soap passato come parametro
	 *
	 * @param env SoapEnvelope da sbustare
	 * 
	 */
	public static byte[] sbustamentoSOAPEnvelope(SOAPEnvelope env) throws UtilsException{
		ByteArrayOutputStream bout = null;
		
		try{
			SOAPBody bd = env.getBody();
			byte[] body = null;
			if(bd.hasFault()){
				SOAPFault fault = bd.getFault();
				body = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(SOAPVersion.SOAP11).getAsByte(fault, true);
			}else{
				bout = new ByteArrayOutputStream();
				java.util.Iterator<?> it = bd.getChildElements();
				while(it.hasNext()){
					Object bodyElementObj = it.next();
					if(!(bodyElementObj instanceof SOAPElement)){
						continue;
					}
					SOAPElement bodyElement = (SOAPElement) bodyElementObj;
					bout.write(OpenSPCoop2MessageFactory.getMessageFactory().createMessage(SOAPVersion.SOAP11).getAsByte(bodyElement, true));
				}
				bout.flush();
				bout.close();
				body = bout.toByteArray();
				bout = null;
			}

			return body;

		}catch (Exception e){
			throw new UtilsException("Sbustamento SoapEnvelope non riuscito: "+e.getMessage(),e);
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
	public static OpenSPCoop2Message imbustamentoMessaggioConAttachment(SOAPVersion versioneSoap, InputStream inputBody,String tipoAttachment,boolean buildAsDataHandler,String contentTypeMessaggioOriginale, String ns)throws UtilsException{
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
				throw new UtilsException("Contenuto da imbustare non presente");
			}
			
			return SoapUtils.imbustamentoMessaggioConAttachment(versioneSoap, byteBuffer.toByteArray(), tipoAttachment, buildAsDataHandler, contentTypeMessaggioOriginale, ns);
			
		}catch (Exception e){
			throw new UtilsException("Imbustamento msgConAttachment_inputStream non riuscito: "+e.getMessage(),e);
		}
	}
	
	public static OpenSPCoop2Message imbustamentoMessaggioConAttachment(SOAPVersion versioneSoap, byte [] inputBody,String tipoAttachment,boolean buildAsDataHandler,String contentTypeMessaggioOriginale, String ns)throws UtilsException{
		OpenSPCoop2Message msg = null;
		try{
			OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getMessageFactory();
			msg = mf.createMessage(versioneSoap);
			return imbustamentoMessaggioConAttachment(msg, inputBody, tipoAttachment, buildAsDataHandler, contentTypeMessaggioOriginale, ns);
		}catch (Exception e){
			throw new UtilsException("Imbustamento msgConAttachment_inputStream non riuscito: "+e.getMessage(),e);
		}
	}
	public static OpenSPCoop2Message imbustamentoMessaggioConAttachment(OpenSPCoop2Message msg, byte [] inputBody,String tipoAttachment,boolean buildAsDataHandler,String contentTypeMessaggioOriginale, String ns)throws UtilsException{
		try{
			
			if(inputBody==null || inputBody.length<=0){
				throw new UtilsException("Contenuto da imbustare non presente");
			}
						
			SOAPBody soapBody = msg.getSOAPBody();
			QName name = null;
			if(Costanti.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP.equals(tipoAttachment)){
				name = new QName(Costanti.SOAP_TUNNEL_NAMESPACE,
						Costanti.SOAP_TUNNEL_ATTACHMENT_ELEMENT_OPENSPCOOP2_TYPE,org.openspcoop2.utils.Costanti.OPENSPCOOP2);	    
			}else{
				name = new QName(Costanti.SOAP_TUNNEL_NAMESPACE,
						Costanti.SOAP_TUNNEL_ATTACHMENT_ELEMENT,org.openspcoop2.utils.Costanti.OPENSPCOOP2);	 
			}
			SOAPElement bodyElement = soapBody.addChildElement(name);
			
			if(Costanti.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP.equals(tipoAttachment)){
				if(contentTypeMessaggioOriginale==null){
					throw new Exception("ContentType messaggio per cui applicare il tunnel non definito?");
				}else{
					bodyElement.setValue(contentTypeMessaggioOriginale);
				}
			}
			AttachmentPart ap = msg.createAttachmentPart();			
			
			if(buildAsDataHandler){
				ap.setDataHandler(new DataHandler(new ByteArrayDataSource(inputBody,tipoAttachment)));
				SoapUtils.saveAttachmentOpenSPCoop(ap);		
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

		}catch (Exception e){
			throw new UtilsException("Imbustamento msgConAttachment_inputStream non riuscito: "+e.getMessage(),e);
		}
	}
	private static void saveAttachmentOpenSPCoop(AttachmentPart ap) throws SOAPException,IOException{
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
	}
	
	/**
	 * Ritorna un messaggio  che contiene i byte in un attachment
	 *
	 * @param body byte del contenuto.
	 * @return msg Messaggio Soap imbustato
	 * 
	 */
	public static OpenSPCoop2Message imbustamentoMessaggioConAttachment(SOAPVersion versioneSoap, byte [] body, String ns) throws UtilsException{
		OpenSPCoop2Message risposta = null;
		try{	    
			risposta = SoapUtils.imbustamentoMessaggioConAttachment(versioneSoap, body,"text/plain",false, null, ns);
			return risposta;
		}catch (Exception e){
			throw new UtilsException("Imbustamento msgConAttachment non riuscito: "+e.getMessage(),e);
		}
	}
	/**
	 * Ritorna un messaggio  che contiene i byte nel body
	 *
	 * @param body byte del contenuto.
	 * @return msg Messaggio Soap imbustato
	 * 
	 */
	public static OpenSPCoop2MessageParseResult imbustamentoMessaggio(NotifierInputStreamParams notifierInputStreamParams, byte [] body,boolean eraserXMLTag, boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold) throws UtilsException{
		return SoapUtils.build(new SoapUtilsBuildParameter(body,true,eraserXMLTag,fileCacheEnable,attachmentRepoDir,fileThreshold),notifierInputStreamParams);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/* ********  B U I L D    M E S S A G G I  ******** */ 
	
	/**
	 * Dato un array di byte, si occupa di mappare l'array in un messaggio  
	 *
	 *
	 * @param param Parametri di build
	 * @return il Messaggio  definito dal parametro.
	 * 
	 */

	public static OpenSPCoop2MessageParseResult build(SoapUtilsBuildParameter param,NotifierInputStreamParams notifierInputStreamParams) throws UtilsException{

		try{
			byte[] byteMsg = param.getByteMsg();
			boolean isBodyStream = param.isBodyStream();
			if(byteMsg==null || byteMsg.length==0) 
				throw new Exception("Nessun contenuto su cui costruire il messaggio");
			
			int offset = 0;
			
			// TODO: Gestire anche il charset correttamente.
			// Charset che puo' essere fornito come parametro o puo' essere capito dal ContentType della richiesta in ingresso.
			
			String contentType = "text/xml";
			//BNCL TEST: String contentType = "application/xml";
			if(AttachmentsUtils.messageWithAttachment(byteMsg)){
				// con attachments
				
				String IDfirst  = AttachmentsUtils.firstContentID(byteMsg);
				String boundary = AttachmentsUtils.findBoundary(byteMsg);
				//if(IDfirst==null){
					//SoapUtils.log.warn("Errore avvenuto durante la lettura del punto di start del multipart message.");
					// Dalla specifica, l'IDFirst e' opzionale.
					//throw new Exception("Errore avvenuto durante la lettura del punto di start del multipart message.");
				//}
				if(boundary==null){
					throw new Exception("Errore avvenuto durante la lettura del boundary associato al multipart message.");
				}
				if(IDfirst==null)
					contentType = "multipart/related; type=\"text/xml\"; \tboundary=\""+boundary.substring(2,boundary.length())+"\" "; 
				else
					contentType = "multipart/related; type=\"text/xml\"; start=\""+IDfirst+"\"; \tboundary=\""+boundary.substring(2,boundary.length())+"\" "; 
				
				if(isBodyStream){
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
						if(param.isEraserXMLTag()){
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
					String contenutoBodyImbustato = 
						"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
						" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Body>"+bodyPulito+"</soapenv:Body></soapenv:Envelope>\r\n";
					
					// MessaggioImbustato
					String bodyOriginalImbustato = bodyOriginal.replace(contenutoBody, contenutoBodyImbustato);
					msg = msg.replace(bodyOriginal, bodyOriginalImbustato);
					byteMsg = msg.getBytes();
					
					// L'ho imbustato io.
					isBodyStream = false;
					
				}
				
			}else{
				// senza attachments
				if(isBodyStream){
					// Imbustamento richiesto
					//Controllo <?xml
					
					// brucio spazi vuoti
					int i = 0;
					for( ; i<byteMsg.length; i++){
						if(((char)byteMsg[i])!=' '){
							break;
						}
					}
					
					// se presente <?xml
					offset = readOffsetXmlInstruction(byteMsg, i, param, offset, false);
				}
			}
			
//			ByteArrayInputStream messageInputTmp = new ByteArrayInputStream(byteMsg,offset,byteMsg.length);
//			System.out.println(new String(Utilities.getAsByteArray(messageInputTmp)));
			
			ByteArrayInputStream messageInput = new ByteArrayInputStream(byteMsg,offset,byteMsg.length);
			OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getMessageFactory();
			OpenSPCoop2MessageParseResult pr = mf.createMessage(messageInput,notifierInputStreamParams,isBodyStream,contentType,null, param.isFileCacheEnable(), param.getAttachmentRepoDir(), param.getFileThreshold());
			OpenSPCoop2Message message = pr.getMessage();
			if(message!=null){
				/*
				// save changes.
				// N.B. il countAttachments serve per il msg con attachments come saveMessage!
				if(message.countAttachments()==0){
					message.getSOAPPartAsBytes();
				}
				
				// checkConsistenza Msg
				try{
					message.getSOAPEnvelope().getAsString();
				}catch(Exception e){
					throw new Exception("Costruzione di un msg soap non riuscita per il pacchetto ["+new String(byteMsg)+"]: "+e.getMessage(),e);
				}
				*/
				if(param.isCheckEmptyBody()){
					if (message.getSOAPBody()==null || message.getSOAPBody().hasChildNodes()==false){
						throw new Exception("Costruzione di un msg soap non riuscita: soap body senza contenuto");
					}
				}
				
				// TODO: gestire come parametro la soap Action: OP-437
				String soapAction = null;
				if(message.getMimeHeaders()!=null){
					String [] hdrs = message.getMimeHeaders().getHeader(Costanti.SOAP_ACTION);
					if(hdrs!=null && hdrs.length>0){
						soapAction = hdrs[0];
					}
				}
				if(soapAction==null){
					soapAction="\"OpenSPCoop\"";
					message.setProperty(Costanti.SOAP_ACTION, soapAction);
					if(message.getMimeHeaders()!=null){
						message.getMimeHeaders().addHeader(org.openspcoop2.message.Costanti.SOAP_ACTION, soapAction);
					}
				}
			}
			
			return pr;
			
		}catch(Exception e){
			throw new UtilsException("Build msg non riuscito: "+e.getMessage(),e);
		}
		
	}     
	
	private static int readOffsetXmlInstruction(byte[]byteMsg, int startFrom,SoapUtilsBuildParameter param, int offsetParam, boolean cleanEmptyChar) throws Exception{
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
				if(param.isEraserXMLTag()){
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
				return readOffsetXmlInstruction(byteMsg, (i+1), param, offset, true);
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
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	/* ********  S A V E   M E S S A G G I  ******** */ 
	
	/**
	 * Si occupa di scrivere su file system il Messaggio passato come parametro 
	 * 
	 *
	 * @param path del file da creare
	 * @param msg Messaggio da scrivere su fileSystem.
	 * 
	 */
	public static void saveMessage(String path,OpenSPCoop2Message msg) throws UtilsException{

		FileOutputStream fos = null;
		try{

			File fileMsg = new File(path);
			if(fileMsg.exists() == true){
				throw new UtilsException("L'identificativo del Messaggio risulta gia' registrato: "+path);
			}	

			fos = new FileOutputStream(path);
			// Scrittura Messaggio su FileSystem
			msg.writeTo(fos,true);
			fos.close();

		}catch(Exception e){
			try{
				if( fos != null )
					fos.close();
			} catch(Exception er) {}
			throw new UtilsException("Utilities.saveMessage error "+e.getMessage(),e);
		}
	}


}

