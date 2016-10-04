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

package org.openspcoop2.testsuite.axis14;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.soap.MessageFactoryImpl;
import org.apache.axis.soap.SOAPFactoryImpl;
import org.openspcoop2.message.AttachmentsUtils;
import org.openspcoop2.message.OpenSPCoop2DataContentHandler;
import org.openspcoop2.message.OpenSPCoop2DataContentHandlerInputStream;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * SoapUtilities basate su axis 14
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Axis14SoapUtils {
	
	private static SOAPFactory soapfactory = null;
	public static SOAPFactory getSoapFactory() throws SOAPException {
		if(soapfactory==null){
			initSoapFactory();
		}
		return soapfactory;
	}
	public static synchronized void initSoapFactory() throws SOAPException {
		if(soapfactory==null){
			//System.out.println("INIT FACTORY");
			soapfactory = new SOAPFactoryImpl();
		}
	}

	private static MessageFactory messagefactory = null;
	public static MessageFactory getMessageFactory() throws SOAPException {
		if(messagefactory==null){
			initMessageFactory();
		}
		return messagefactory;
	}
	public static synchronized void initMessageFactory() throws SOAPException {
		if(messagefactory==null){
			//System.out.println("INIT FACTORY");
			messagefactory = new MessageFactoryImpl();
		}
	}
	
	
	
	
	
	
	
	
	
	/* ********  C O N T E X T   S O A P   E N G I N E  ******** */
	
	/* AxisServer */
	private static org.apache.axis.server.AxisServer axisServer = null;
	public static synchronized void initAxisServer(){
		if(axisServer==null){
			axisServer=new org.apache.axis.server.AxisServer();
		}
	}
	public static org.apache.axis.server.AxisServer getAxisServer() {
		if(axisServer==null)
			initAxisServer();
		return axisServer;
	}

	/* AxisClient */
	private static org.apache.axis.client.AxisClient axisClient = null;
	public static synchronized void initAxisClient(){
		if(axisClient==null){
			axisClient=new org.apache.axis.client.AxisClient();
		}
	}
	public static org.apache.axis.client.AxisClient getAxisClient() {
		if(axisClient==null)
			initAxisClient();
		return axisClient;
	}


	/* Message Context */
	private static org.apache.axis.MessageContext msgContext = null;
	public static synchronized void initMessageContext(){
		if(msgContext==null){
			msgContext=new org.apache.axis.MessageContext(new org.apache.axis.server.AxisServer());
			msgContext.setProperty(org.apache.axis.AxisEngine.PROP_SEND_XSI, false);
		}
	}
	
	
	
	
	
	
	
	
	/* ********  L E T T U R A  V A L O R I  ******** */
	
	public static String getSoapAction(org.apache.axis.Message msg)throws UtilsException{
		
		String soapAction = null;
		try{
			javax.xml.soap.MimeHeaders mhs =  msg.getMimeHeaders();
			java.util.Iterator<?> it = mhs.getAllHeaders();
			while(it.hasNext()){
				javax.xml.soap.MimeHeader mh = (javax.xml.soap.MimeHeader) it.next();
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

	
	

	
	



	/* ********  M E S S A G G I    O K   ******** */ 

	/**
	 * Metodo che si occupa di costruire un messaggio SOAPElement 'openspcoop OK'. 
	 *
	 * @return messaggio Soap 'OK' in caso di successo, null altrimenti.
	 * 
	 */
	public static org.apache.axis.Message buildOpenSPCoopOK_axisMsg(String idMessaggio) throws UtilsException{
		try{

			ByteArrayInputStream inputBody = new ByteArrayInputStream(Axis14SoapUtils.buildOpenSPCoopOK(idMessaggio));
			org.apache.axis.soap.MessageFactoryImpl mf = new org.apache.axis.soap.MessageFactoryImpl();
			org.apache.axis.Message responseAxisMessage = (org.apache.axis.Message) mf.createMessage();
			SOAPBody soapBody = responseAxisMessage.getSOAPBody();
			org.apache.axis.message.InputStreamBody isBody = new org.apache.axis.message.InputStreamBody(inputBody);
			soapBody.addChildElement(isBody);
			inputBody.close();

			return responseAxisMessage;

		} catch(Exception e) {
			throw new UtilsException("Creazione MsgOpenSPCoopOK non riuscito: "+e.getMessage(),e);
		}
	}


	/**
	 * Metodo che si occupa di costruire un messaggio SOAPElement 'openspcoop OK'. 
	 *
	 * @return bytes del messaggio Soap 'OK' in caso di successo, null altrimenti.
	 * 
	 */
	public static byte[] buildOpenSPCoopOK(String idMessaggio) throws UtilsException{
		try{
			Name name = 
				new org.apache.axis.message.PrefixedQName("http://www.openspcoop2.org/core/integrazione",
						"esito-richiesta","OpenSPCoop2");	    
			//Name name = 
			//	new org.apache.axis.message.PrefixedQName(new javax.xml.namespace.QName("Risposta"));

			org.apache.axis.message.MessageElement ok = 
				new org.apache.axis.message.MessageElement(name);
			ok.setAttribute("stato","PRESA_IN_CARICO");


			//return Utilities.eraserType(ok.getAsString().getBytes());
			return getAsString(ok).getBytes();

		} catch(Exception e) {
			throw new UtilsException("Creazione MsgOpenSPCoopOK non riuscito: "+e.getMessage(),e);
		}
	}


	public static String toString(SOAPFault fault) throws UtilsException{
		return toString(fault,true);
	}
	
	public static String toString(SOAPFault fault,boolean printDetails) throws UtilsException{
		try{
			if(printDetails && (fault!=null) && (fault instanceof org.apache.axis.message.SOAPFault)){
				AxisFault af = ((org.apache.axis.message.SOAPFault)fault).getFault();
				af.removeHostname();
				af.removeFaultDetail(new QName("http://xml.apache.org/axis/","stackTrace"));
				String print = af.dumpToString();
				if(print.startsWith("AxisFault")){
					print = print.substring("AxisFault".length());
				}
				return print;		
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
					
					if(printDetails){
						@SuppressWarnings("unused")
						org.apache.axis.message.Detail details = (org.apache.axis.message.Detail) fault.addDetail();
						details = (org.apache.axis.message.Detail) fault.getDetail();
						java.util.Iterator<?> elemChilds = fault.getDetail().getChildElements();
						while(elemChilds.hasNext()){
							MessageElement mChild = (MessageElement) elemChilds.next();
							bf.append(" detail["+mChild.getAsString()+"]");
						}
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
	public static org.apache.axis.Message build_Soap_Empty() {

		try{
			org.apache.axis.soap.MessageFactoryImpl mf = new org.apache.axis.soap.MessageFactoryImpl();
			org.apache.axis.Message msg = (org.apache.axis.Message) mf.createMessage();
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
	 * @param aDetailPrefix array di DetailPrefix da inserire nel Detail del SOAPFault.
	 * @param aDetailLocalName array di DetailLocalName da inserire nel Detail del SOAPFault.
	 * @param aDetailURI array di DetailURI da inserire nel Detail del SOAPFault.
	 * @return <tt>byte[]</tt> del messaggio Soap Fault costruito in caso di successo, <tt>null</tt> altrimenti.
	 * 
	 */
	public static byte[] build_Soap_Fault(String aFault, String aActor, String aCode, String [] aDetailPrefix, String [] aDetailLocalName , String [] aDetailURI) throws UtilsException  {

		ByteArrayOutputStream byteMessaggio = null;
		try{
			org.apache.axis.soap.MessageFactoryImpl mf = new org.apache.axis.soap.MessageFactoryImpl();
			SOAPMessage msg = mf.createMessage();
			SOAPEnvelope env = (msg.getSOAPPart()).getEnvelope();

			String xsi = "http://www.w3.org/2001/XMLSchema-instance";
			env.addNamespaceDeclaration("xsi", xsi);
			env.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
			env.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

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
			if (aDetailPrefix.length > 0){
				fault.addDetail();	
				Detail d = fault.getDetail();
				for(int i=0;i<aDetailPrefix.length;i++){
					Name name =  env.createName(aDetailLocalName[i], aDetailPrefix[i], aDetailURI[i]);
					d.addDetailEntry(name);
				}
			}

			//log.info("Build complete MessageSOAPFault");

			// Return byte ....
			byteMessaggio = new ByteArrayOutputStream();
			msg.writeTo(byteMessaggio);
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

	/**
	 * Metodo che si occupa di costruire un messaggio SOAPFault 'openspcoop Errore Interno del Server'. 
	 *
	 * @param soggetto Soggetto che ha generato l'errore
	 * @param text Nome dell'errore (Senza spazi!)
	 * @return bytes del messaggio SoapFault costruito in caso di successo, null altrimenti.
	 * 
	 */
	public static byte[] build_Server_Error(String soggetto, String text , String faultString, String faultCode) throws UtilsException{
		try{
			String t = text;
			String [] testText = text.split(" ");
			if(testText.length > 1)
				t = testText[0];

			String actorString = null;
			String [] detailPrefix = {soggetto};
			String [] detailLocalName = {t};
			String [] detailURI = {"http://www.openspcoop.org"};

			return Axis14SoapUtils.build_Soap_Fault(faultString,actorString,faultCode,detailPrefix,detailLocalName,detailURI);

		} catch(Exception e) {
			throw new UtilsException("Creazione MsgSOAPFault_ServerError non riuscito: "+e.getMessage(),e);
		}
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ********  S B U S T A M E N T O    M E S S A G G I  ******** */ 

	public static boolean isTunnelOpenSPCoopSoap(SOAPBody body){
		if(body!=null && 
				body.hasChildNodes() && 
				body.getFirstChild()!=null &&
				"SOAPTunnel".equals(body.getFirstChild().getLocalName()) &&
				"http://www.openspcoop2.org/pdd/services/PDtoSOAP".equals(body.getFirstChild().getNamespaceURI()) &&
				"OpenSPCoop2".equals(body.getFirstChild().getPrefix())){
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
	 * @param stream Stream su cui scrivere il messaggio sbustato
	 * 
	 */
	public static void sbustamentoMessaggio(org.apache.axis.Message msg,java.io.OutputStream stream) throws UtilsException{
		
		try{
			// Nota: non viene usato DocumentToStream, poiche' altrimenti viene prodotto l'<?xml ... /> 
			
			//	Sbustamento Senza Attachments
			if(msg.countAttachments() == 0){
				org.apache.axis.message.SOAPBody bd = (org.apache.axis.message.SOAPBody) msg.getSOAPBody();
				if(bd.hasFault()){
					org.apache.axis.message.SOAPFault fault =
						(org.apache.axis.message.SOAPFault) bd.getFault();
					stream.write(fault.getAsString().getBytes());
				}else{
					java.util.Iterator<?> it = bd.getChildElements();
					while(it.hasNext()){
						org.apache.axis.message.MessageElement bodyElement = 
							(org.apache.axis.message.MessageElement) it.next();
						stream.write(bodyElement.getAsString().getBytes());
					}
				}
			}
			// Sbustamento Con Attachmnets
			else{
				SOAPBody body = msg.getSOAPBody();
				if(Axis14SoapUtils.isTunnelOpenSPCoopSoap(body)){
					// Sbustamento OpenSPCoop
					AttachmentPart ap  = (AttachmentPart) msg.getAttachments().next();
					Object o = ap.getContent();
					//DataHandler dh = ap.getDataHandler();
					//InputStream inputDH = (InputStream) dh.getContent();
					InputStream inputDH = null;
					try{
						if(o instanceof OpenSPCoop2DataContentHandlerInputStream){
							inputDH = (OpenSPCoop2DataContentHandlerInputStream) o;
						}
						else if(o instanceof InputStream){
							inputDH = (InputStream) OpenSPCoop2DataContentHandler.getContent((InputStream)o);
						}else{
							throw new Exception("Tipo non gestito: "+o.getClass().getName());
						}
						java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
				    	byte [] readB = new byte[8192];
						int readByte = 0;
						while((readByte = inputDH.read(readB))!= -1)
							bout.write(readB,0,readByte);
						bout.close();
						stream.write(bout.toByteArray());
					}finally{
						try{
							if(inputDH!=null){
								inputDH.close();	
							}
						}catch(Exception eClose){}
					}
				}else{
				
					ByteArrayOutputStream sbustamentoAttachments = new ByteArrayOutputStream();
					msg.writeTo(sbustamentoAttachments);
					String msgString = sbustamentoAttachments.toString();
					byte [] msgByte =  sbustamentoAttachments.toByteArray();
					String soapEnvelopeStart = "<" + msg.getSOAPEnvelope().getPrefix() + ":" + msg.getSOAPEnvelope().getName();
					String xmlTagDecode = "<?xml";
					String soapEnvelopeStop =  "</" + msg.getSOAPEnvelope().getPrefix() + ":" + msg.getSOAPEnvelope().getName()+">";
					//System.out.println("SoapStart: "+soapEnvelopeStart);
					//System.out.println("SoapStartIndexOf: "+msgString.indexOf(soapEnvelopeStart));
					//System.out.println("SoapStop: "+soapEnvelopeStop);
					//System.out.println("SoapStopIndexOf: "+msgString.indexOf(soapEnvelopeStop));
					// Prima parte del Multipart
					if(msgString.indexOf(xmlTagDecode)!=-1){
						stream.write(msgByte,0,msgString.indexOf(xmlTagDecode));
					}else{
						stream.write(msgByte,0,msgString.indexOf(soapEnvelopeStart));
					}
					// Body
					org.apache.axis.message.SOAPBody bd = (org.apache.axis.message.SOAPBody) msg.getSOAPBody();
					if(bd.hasFault()){
						org.apache.axis.message.SOAPFault fault =
							(org.apache.axis.message.SOAPFault) bd.getFault();
						stream.write(fault.getAsString().getBytes());
					}else{
						java.util.Iterator<?> it = bd.getChildElements();
						while(it.hasNext()){
							org.apache.axis.message.MessageElement bodyElement = 
								(org.apache.axis.message.MessageElement) it.next();
							stream.write(bodyElement.getAsString().getBytes());
						}
					}
					// Resto degli attachments
					int indexOf = msgString.indexOf(soapEnvelopeStop)+soapEnvelopeStop.length();
					stream.write(msgByte,indexOf,msgByte.length - indexOf);
				}
			}
		}catch (Exception e){
			throw new UtilsException("Sbustamento AxisMsg_inputStream non riuscito: "+e.getMessage(),e);
		}
		
		
	}
	
	/**
	 * Ritorna i bytes del contenuto del messaggio Soap passato come parametro
	 *
	 * @param msg Messaggio Soap da sbustare
	 * @return byte del contenuto (sbustati dalla SoapEnvelope).
	 * 
	 */
	public static byte[] sbustamentoMessaggio(org.apache.axis.Message msg) throws UtilsException{
		ByteArrayOutputStream bodySbustato = new ByteArrayOutputStream();
		try{
			Axis14SoapUtils.sbustamentoMessaggio(msg,bodySbustato);
			return bodySbustato.toByteArray();	
		}catch(Exception e){
			try{
				if(bodySbustato!=null)
					bodySbustato.close();
			}catch(Exception eis){}	
			throw new UtilsException("Sbustamento AxisMsg non riuscito: "+e.getMessage(),e);
		}
	}

	/**
	 * Ritorna i bytes del contenuto del messaggio Soap passato come parametro
	 *
	 * @param env SoapEnvelope da sbustare
	 * 
	 */
	public static byte[] sbustamentoSOAPEnvelope(org.apache.axis.message.SOAPEnvelope env) throws UtilsException{

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try{
			//	Nota: non viene usato DocumentToStream, poiche' altrimenti viene prodotto l'<?xml ... /> 
			//        org.apache.axis.utils.XMLUtils.DocumentToStream(bodyElement.getAsDocument(), stream);
			//  Nota: non viene usato ElementToStream, poiche' viene dimenticato il namespace.
			//        org.apache.axis.utils.XMLUtils.ElementToStream(bodyElement,stream);
						
			org.apache.axis.message.SOAPBody bd = (org.apache.axis.message.SOAPBody) env.getBody();
			if(bd.hasFault()){
				org.apache.axis.message.SOAPFault fault =
					(org.apache.axis.message.SOAPFault) bd.getFault();
				stream.write(fault.getAsString().getBytes());
			}else{
				java.util.Iterator<?> it = bd.getChildElements();
				while(it.hasNext()){
					org.apache.axis.message.MessageElement bodyElement = 
						(org.apache.axis.message.MessageElement) it.next();
					//org.apache.axis.utils.XMLUtils.DocumentToStream(bodyElement.getAsDocument(), stream);
					//org.apache.axis.utils.XMLUtils.ElementToStream(bodyElement,stream);
					stream.write(bodyElement.getAsString().getBytes());
				}
			}

			byte[] body = stream.toByteArray();
			stream.close();
			return body;

		}catch (Exception e){
			try{
				stream.close();
			}catch(Exception eis){}
			throw new UtilsException("Sbustamento SoapEnvelope non riuscito: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	

	
	
	
	/* ********  I M B U S T A M E N T O    M E S S A G G I  ******** */ 

	/**
	 * Ritorna un messaggio axis che contiene i byte in un attachment
	 *
	 * @param inputBody contenuto.
	 * @return msg Messaggio Soap imbustato
	 * 
	 */
	public static org.apache.axis.Message imbustamentoMessaggioConAttachment(InputStream inputBody,String tipoAttachment,boolean buildAsDataHandler,String contentTypeMessaggioOriginale)throws UtilsException{
		
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
			
			return imbustamentoMessaggioConAttachment(byteBuffer.toByteArray(), tipoAttachment, buildAsDataHandler, contentTypeMessaggioOriginale);
			
		}catch (Exception e){
			throw new UtilsException("Imbustamento msgConAttachment_inputStream non riuscito: "+e.getMessage(),e);
		}
	}
	public static org.apache.axis.Message imbustamentoMessaggioConAttachment(byte [] inputBody,String tipoAttachment,boolean buildAsDataHandler,String contentTypeMessaggioOriginale)throws UtilsException{
		org.apache.axis.Message msg = null;
		try{
			
			if(inputBody==null || inputBody.length<=0){
				throw new UtilsException("Contenuto da imbustare non presente");
			}
			
			org.apache.axis.soap.MessageFactoryImpl mf = new org.apache.axis.soap.MessageFactoryImpl();
			msg = (org.apache.axis.Message) mf.createMessage();
			SOAPBody soapBody = msg.getSOAPBody();
			Name name = null;
			if("application/openspcoop2".equals(tipoAttachment)){
				name = new org.apache.axis.message.PrefixedQName("http://www.openspcoop2.org/pdd/services/PDtoSOAP",
						"SOAPTunnel","OpenSPCoop2");	    
			}else{
				name = new org.apache.axis.message.PrefixedQName("http://www.openspcoop2.org/pdd/services/PDtoSOAP",
						"Attachments","OpenSPCoop2");	 
			}
			SOAPBodyElement bodyElement = soapBody.addBodyElement(name);
			
			if("application/openspcoop2".equals(tipoAttachment)){
				if(contentTypeMessaggioOriginale==null){
					throw new Exception("ContentType messaggio per cui applicare il tunnel non definito?");
				}else{
					bodyElement.setValue(contentTypeMessaggioOriginale);
				}
			}
			
			org.apache.axis.attachments.AttachmentPart ap = (org.apache.axis.attachments.AttachmentPart) msg.createAttachmentPart();
			//ap.setContentId(rs.getString("CONTENT_ID"));
			if(buildAsDataHandler){
				ap.setDataHandler(new DataHandler(new java.io.ByteArrayInputStream(inputBody),tipoAttachment));
				saveAttachmentOpenSPCoop(ap);				
			}else{
				ap.setContent(new java.io.ByteArrayInputStream(inputBody),tipoAttachment);
			}
			msg.addAttachmentPart(ap);
			
			return msg;

		}catch (Exception e){
			throw new UtilsException("Imbustamento msgConAttachment_inputStream non riuscito: "+e.getMessage(),e);
		}
	}
	private static void saveAttachmentOpenSPCoop(org.apache.axis.attachments.AttachmentPart ap) throws SOAPException,IOException{
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
	 * Ritorna un messaggio axis che contiene i byte in un attachment
	 *
	 * @param body byte del contenuto.
	 * @return msg Messaggio Soap imbustato
	 * 
	 */
	public static org.apache.axis.Message imbustamentoMessaggioConAttachment(byte [] body) throws UtilsException{
		org.apache.axis.Message risposta = null;
		try{	    
			risposta = Axis14SoapUtils.imbustamentoMessaggioConAttachment(body,"text/plain",false, null);
			return risposta;
		}catch (Exception e){
			throw new UtilsException("Imbustamento msgConAttachment non riuscito: "+e.getMessage(),e);
		}
	}
	/**
	 * Ritorna un messaggio axis che contiene i byte nel body
	 *
	 * @param body byte del contenuto.
	 * @return msg Messaggio Soap imbustato
	 * 
	 */
	public static org.apache.axis.Message imbustamentoMessaggio(byte [] body,boolean eraserXMLTag) throws UtilsException{
		return Axis14SoapUtils.build(body,true,eraserXMLTag);
	}
	
	
	
	
	
	
	
	
	
	
	
	/* ********  B U I L D    M E S S A G G I  ******** */ 
	
	/**
	 * Dato un array di byte, si occupa di mappare l'array in un messaggio Axis 
	 *
	 *
	 * @param byteMsg Messaggio Axis in bytes.
	 * @param isBodyStream Indicazione se deve essere effettuato un imbustamento (true) o meno (false)
	 * @return il Messaggio Axis definito dal parametro.
	 * 
	 */
	public static org.apache.axis.Message build(byte[] byteMsg,boolean isBodyStream) throws UtilsException{
		return Axis14SoapUtils.build(SOAPVersion.SOAP11,byteMsg,isBodyStream,false,true);
	}
	public static org.apache.axis.Message build(SOAPVersion soapVersion,byte[] byteMsg,boolean isBodyStream) throws UtilsException{
		return Axis14SoapUtils.build(soapVersion,byteMsg,isBodyStream,false,true);
	}
	public static org.apache.axis.Message build(byte[] byteMsg,boolean isBodyStream,boolean eraserXMLTag) throws UtilsException{
		return Axis14SoapUtils.build(SOAPVersion.SOAP11,byteMsg,isBodyStream,eraserXMLTag,true);
	}
	public static org.apache.axis.Message build(SOAPVersion soapVersion,byte[] byteMsg,boolean isBodyStream,boolean eraserXMLTag) throws UtilsException{
		return Axis14SoapUtils.build(soapVersion,byteMsg,isBodyStream,eraserXMLTag,true);
	}
	public static org.apache.axis.Message build(byte[] byteMsg,boolean isBodyStream,boolean eraserXMLTag,boolean checkEmptyBody) throws UtilsException{
		return Axis14SoapUtils.build(SOAPVersion.SOAP11,byteMsg,isBodyStream,eraserXMLTag,checkEmptyBody);
	}
	public static org.apache.axis.Message build(SOAPVersion soapVersion, byte[] byteMsg,boolean isBodyStream,boolean eraserXMLTag,boolean checkEmptyBody) throws UtilsException{

		try{
			
			if(byteMsg==null || byteMsg.length==0) 
				throw new Exception("Nessun contenuto su cui costruire il messaggio");
			
			int offset = 0;
			
			String contentType = SOAPConstants.SOAP_1_1_CONTENT_TYPE;
			if(SOAPVersion.SOAP12.equals(soapVersion)){
				contentType = SOAPConstants.SOAP_1_2_CONTENT_TYPE;
			}
			String IDfirst  = null;
			//BNCL TEST: String contentType = "application/xml";
			if(AttachmentsUtils.messageWithAttachment(byteMsg)){
				// con attachments
				
				IDfirst  = AttachmentsUtils.firstContentID(byteMsg);
				String boundary = AttachmentsUtils.findBoundary(byteMsg);
				//if(IDfirst==null){
					//SoapUtils.log.warn("Errore avvenuto durante la lettura del punto di start del multipart message.");
					// Dalla specifica, l'IDFirst e' opzionale.
					//throw new Exception("Errore avvenuto durante la lettura del punto di start del multipart message.");
				//}
				if(boundary==null){
					throw new Exception("Errore avvenuto durante la lettura del boundary associato al multipart message.");
				}

				switch (soapVersion) {
				case SOAP11:
					if(IDfirst==null)
						contentType = "multipart/related; type=\""+SOAPConstants.SOAP_1_1_CONTENT_TYPE+"\"; \tboundary=\""+boundary.substring(2,boundary.length())+"\" "; 
					else
						contentType = "multipart/related; type=\""+SOAPConstants.SOAP_1_1_CONTENT_TYPE+"\"; start=\""+IDfirst+"\"; \tboundary=\""+boundary.substring(2,boundary.length())+"\" "; 
					break;
				case SOAP12:
					if(IDfirst==null)
						contentType = "multipart/related; type=\""+SOAPConstants.SOAP_1_2_CONTENT_TYPE+"\"; \tboundary=\""+boundary.substring(2,boundary.length())+"\" "; 
					else
						contentType = "multipart/related; type=\""+SOAPConstants.SOAP_1_2_CONTENT_TYPE+"\"; start=\""+IDfirst+"\"; \tboundary=\""+boundary.substring(2,boundary.length())+"\" "; 
					break;
				}
				
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
						if(eraserXMLTag){
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
					String xml = "";
					if(byteMsg.length>i+5){
						xml = "" + ((char)byteMsg[i]) + ((char)byteMsg[i+1]) + ((char)byteMsg[i+2]) +((char)byteMsg[i+3]) + ((char)byteMsg[i+4]);
						if(xml.equals("<?xml")){
							if(eraserXMLTag){
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
						}
					}
				}
			}
			
			ByteArrayInputStream messageInput = new ByteArrayInputStream(byteMsg,offset,byteMsg.length);
			org.apache.axis.Message message = 
				new org.apache.axis.Message(messageInput,isBodyStream,contentType,null);
			
			
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
			if(checkEmptyBody){
				if (message.getSOAPBody()==null || message.getSOAPBody().hasChildNodes()==false){
					throw new Exception("Costruzione di un msg soap non riuscita: soap body senza contenuto");
				}
			}
			
			return message;
			
		}catch(Exception e){
			throw new UtilsException("Build msg non riuscito: "+e.getMessage(),e);
		}
		
	}     
	
	

	
	
	
	
	
	
	
	
	
	/* ********  T R A S F O R M A Z I O N I   M E S S A G G I  ******** */ 
	
	/**
	 * Ritorna un array di byte rappresentanti il soap element passato come parametro <var>elem</header>.
	 *
	 * @param elem da trasformare in array di byte
	 * @return byte[] dell'elemento passato come parametro.
	 * 
	 */
	public static byte[] msgElementoToByte(org.apache.axis.message.MessageElement elem) throws UtilsException{
		try{
			elem.addNamespaceDeclaration("xsd","http://www.w3.org/2001/XMLSchema");
			elem.addNamespaceDeclaration("xsi","http://www.w3.org/2001/XMLSchema-instance");
			byte [] risultato = getAsString(elem).getBytes();
			
			return risultato;

		} catch (java.lang.Exception e) {
			throw new UtilsException("MsgElementToByte non riuscito: "+e.getMessage(),e);
		}
	}

	/**
	 * Trasforma un messaggio di axis in stringa
	 * 
	 * @param me
	 * @return Trasforma il messaggio axis in stringa
	 * @throws UtilsException
	 */
	public static String getAsString(MessageElement me) throws UtilsException {
		return getAsString(me,false);
	}

	/**
	 * Trasforma un messaggio di axis in stringa
	 * 
	 * @param me
	 * @return Trasforma il messaggio axis in stringa
	 * @throws UtilsException
	 */
	public static String getAsString(MessageElement me,boolean pretty) throws UtilsException {
		try{
			java.io.StringWriter writer = new java.io.StringWriter();
			if(msgContext==null)
				initMessageContext();
			org.apache.axis.MessageContext msgContext = Axis14SoapUtils.msgContext;
			org.apache.axis.encoding.SerializationContext serializeContext = 
				new org.apache.axis.encoding.SerializationContext(writer, msgContext);
			if(pretty){
				serializeContext.setPretty(true);
			}
			serializeContext.setSendDecl(false);
			me.setDirty(false);
			me.output(serializeContext);
			writer.close();
			return writer.getBuffer().toString();
		}catch(Exception e){
			throw new UtilsException("Utilities.getAsString, errore: "+e.getMessage(),e);
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
	public static void saveAxisMessage(String path,Message msg) throws UtilsException{

		FileOutputStream fos = null;
		try{

			File fileMsg = new File(path);
			if(fileMsg.exists() == true){
				throw new UtilsException("L'identificativo del Messaggio risulta gia' registrato: "+path);
			}	

			fos = new FileOutputStream(path);
			// Scrittura Messaggio su FileSystem
			msg.writeTo(fos);
			fos.close();

		}catch(Exception e){
			try{
				if( fos != null )
					fos.close();
			} catch(Exception er) {}
			throw new UtilsException("Utilities.saveAxisMessage error "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	/* ********  L E N G H T   M E S S A G G I  ******** */ 
	
	/* getLength axis:
	 * Gestisce il bug, che se viene chiamato getLength in presenza di attachments, il messaggio non e' piu' modificabile a meno di aggungere un nuovo attachments
	 * Inoltre considera la dichiarazione di <?xml aggiunta durante la serializzazione per quanto riguarda la lunghezza di una SOAPEnvelope senza attachments
	 *  */
	public static long getLength(Message msg,boolean permettiUlterioriModifiche) throws Exception{
		
		// Patch Axis per forzare l'ottimizzazione dei namespace SOAP prima del writeTo finale
		// Quello che succede e' che se viene effettuato la seguente catena di eventi:
		// 1. getLength con questo metodo
		// 2. soap.hasFault
		// 3. writeTo, il messaggio serializzato avra' una lunghezza diversa da quanto ottenuto con getLenth, poiche' nel punto 2 vengono effettuate le ottimizzazioni
		if(msg.getSOAPBody()!=null)
			msg.getSOAPBody().hasFault();
		
		long length = msg.getContentLength();
		
		if(msg.countAttachments()>0){
			if(permettiUlterioriModifiche){
				org.apache.axis.attachments.AttachmentPart ap = 
					(org.apache.axis.attachments.AttachmentPart) msg.createAttachmentPart();
				ap.setContent("GESTONE_CONTENT_LENGTH","text/txt");
				msg.addAttachmentPart(ap);
				msg.getAttachmentsImpl().removeAttachmentPart(ap.getContentIdRef());
			}
		}
		else{
			msg.getSOAPBody(); // forza la riscrittura del soap header (dopo aver effettuato il content length), in modo da ottimizzare i namespace
			String xml = msg.getSOAPEnvelope().getAsString();
			//System.out.println("LUNGHEZZA CALCOLATA AXIS: "+length);
			length = xml.length();
			//System.out.println("LUNGHEZZA RI-CALCOLATA: "+length);
			//System.out.println("MSG ["+xml+"]");
			
			if(!xml.startsWith("<?xml")){
				StringBuffer bf = new StringBuffer();
				bf.append("<?xml version=\"1.0\" encoding=\"");
				bf.append(((org.apache.axis.SOAPPart)msg.getSOAPPart()).getEncoding());
				bf.append("\"?>");
				//System.out.println("MSG ADD ["+bf.toString()+"]");
				length = length + bf.length();
				//System.out.println("LUNGHEZZA RI-CALCOLATA CON <?xml: "+length);
			}
		}
		
		return length;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ********  B Y B A S S   S O A P    H E A D E R  ******** */
	
	/**
	 * Nel caso in cui un header venga impostato con l'attributo mustUnderstand="1" 
	 * e non sia presente l'"actor" l'architettura di Axis manda in errore il servizio 
	 * perche' pensa che debba gestire l'header. 
	 * L'utility in questione, inserita in testa ad un servizio axis (in un handler) 
	 * permette di bypassare il problema segnalando come "processed" l' header.
	 * Con questa utility e' possibile quindi impostare a processato (<var>isProcessed</var>=true)
	 * o a non processato (<var>isProcessed</var>=false) gli header,
	 * definiti nel parametro <var>filtri</var>, che posseggono mustUnderstand="1" e non possiedono un actor.
	 * 
	 * @param header Header da elaborare
	 * @param filtri Filtri da applicare
	 * @param isProcessed Processed/NotProcessed
	 */
	public static void bypassMustUnderstandAxisHeaderElement(org.apache.axis.message.SOAPHeader header,Properties filtri,boolean isProcessed)throws UtilsException{

		try{

			java.util.Iterator<?> it = header.getChildElements();
			while(it.hasNext()){
				SOAPHeaderElement element = 
					(SOAPHeaderElement) it.next();

				//System.out.println("Elemento "+element.getLocalName()+" con actor ["+element.getActor()+"] e mustUnderstand ["+element.getMustUnderstand()+"]");

				if(element.getActor()==null&&element.getMustUnderstand()==true){
					java.util.Enumeration<?> en = filtri.keys();
					while(en.hasMoreElements()){
						String key = (String) en.nextElement();
						if(key.equals(element.getLocalName()) && filtri.get(key).equals(element.getNamespaceURI())){
							//System.out.println("Elemento "+element.getLocalName()+" PROCESSED");
							element.setProcessed(isProcessed);
							break;
						}
					}
				}
			}

		}catch(java.lang.Exception e) {
			throw new UtilsException("Utilities.bypassMustUnderstandAxisHeaderElement Riscontrato errore durante l'applicazione del bypassFilter: "+e.getMessage(),e);
		}  

	}

	/**
	 * Nel caso in cui un header venga impostato con l'attributo mustUnderstand="1" 
	 * e non sia presente l'"actor" l'architettura di Axis manda in errore il servizio 
	 * perche' pensa che debba gestire l'header. 
	 * L'utility in questione, inserita in testa ad un servizio axis (in un handler) 
	 * permette di bypassare il problema segnalando come "processed" l' header.
	 * Con questa utility e' possibile quindi impostare a processato (<var>isProcessed</var>=true)
	 * o a non processato (<var>isProcessed</var>=false) tutti gli header che posseggono mustUnderstand="1" e non possiedono un actor.
	 * 
	 * @param header Header da elaborare
	 * @param isProcessed Processed/NotProcessed
	 */
	public static void bypassMustUnderstandAxisHeaderElement(org.apache.axis.message.SOAPHeader header,boolean isProcessed)throws UtilsException{

		try{

			java.util.Iterator<?> it = header.getChildElements();
			while(it.hasNext()){
				SOAPHeaderElement element = 
					(SOAPHeaderElement) it.next();

				//System.out.println("Elemento "+element.getLocalName()+" con actor ["+element.getActor()+"] e mustUnderstand ["+element.getMustUnderstand()+"]");

				if(element.getActor()==null&&element.getMustUnderstand()==true){
					//System.out.println("Elemento "+element.getLocalName()+" PROCESSED");
					element.setProcessed(isProcessed);
				}
			}

		}catch(java.lang.Exception e) {
			throw new UtilsException("Utilities.bypassMustUnderstandAxisHeaderElement Riscontrato errore durante l'applicazione del bypassFilter: "+e.getMessage(),e);
		}  

	}
}
