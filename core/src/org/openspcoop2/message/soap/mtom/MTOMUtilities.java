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
package org.openspcoop2.message.soap.mtom;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import javax.mail.internet.ContentType;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * MTOMUtilities
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MTOMUtilities {
	
	public static boolean isMtom(String cType) throws MessageException{
		try{
			return ContentTypeUtilities.isMtom(cType);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	
	public static String readInternalMultipartMtomContentType(String contentType) throws MessageException{
		try{
			return readInternalMultipartMtomContentType(new ContentType(contentType));
		}
		catch(MessageException e){
			throw e;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	public static String readInternalMultipartMtomContentType(ContentType contentType) throws MessageException{
		try{
			return ContentTypeUtilities.readInternalMultipartMtomContentType(contentType);
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	public static List<MtomXomReference> unpackaging(OpenSPCoop2Message msgParam, boolean fast, boolean body) throws MessageException, MessageNotSupportedException{
		
		if(!MessageType.SOAP_11.equals(msgParam.getMessageType()) && !MessageType.SOAP_12.equals(msgParam.getMessageType())){
			throw MessageNotSupportedException.newMessageNotSupportedException(msgParam.getMessageType());
		}
		OpenSPCoop2SoapMessage message = msgParam.castAsSoap();
		
		try{
			List<MtomXomReference> list = new ArrayList<MtomXomReference>();
			
			Element element = null;
			if(body){
				element = message.getSOAPBody();
			}
			else{
				element = message.getSOAPPart().getEnvelope();
			}
			if(element==null){
				return list;
			}
			
			boolean restoredXomReference = false;
			
			AbstractXPathExpressionEngine xpathEngine = new XPathExpressionEngine(msgParam.getFactory());
			DynamicNamespaceContext dnc = org.openspcoop2.message.xml.DynamicNamespaceContextFactory.getInstance(msgParam.getFactory()).getNamespaceContext(element);
			Object oNode = null;
			try{
				oNode = xpathEngine.getMatchPattern(element, dnc, org.openspcoop2.message.soap.mtom.Costanti.MTOM_XOP_REFERENCES, XPathReturnType.NODESET);
			}catch(XPathNotFoundException notFound){}
			if(oNode==null){
				// non esistono xom reference
				return list;
			}
			if( !(oNode instanceof NodeList) ){
				throw new MessageException("XpathEngine (expr:"+org.openspcoop2.message.soap.mtom.Costanti.MTOM_XOP_REFERENCES+") return wrong type (expected: "+
						NodeList.class.getName()+"): "+oNode.getClass().getName());
			}
			NodeList nodeList = (NodeList) oNode;
			//System.out.println("Ritornato: size: "+nodeList.getLength());
			for (int i = 0; i < nodeList.getLength(); i++) {
				
				// xomReference
				Node xomReference = nodeList.item(i);
				if(xomReference.getAttributes()==null || xomReference.getAttributes().getLength()<=0){
					throw new MessageException("Found XOM Reference without attributes ('"+org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"' attribute required)");
				}
				Node xomHref = xomReference.getAttributes().getNamedItem(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF);
				if(xomHref == null){
					throw new MessageException("Found XOM Reference without attribute '"+org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"'");
				}
				String contentId = xomHref.getNodeValue();
				if(contentId == null){
					throw new MessageException("Found XOM Reference with attribute '"+org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"' without value?");
				}
				if(contentId.startsWith(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE)){
					contentId = contentId.substring(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE.length());
				}
				MimeHeaders mhs = new MimeHeaders();
				mhs.addHeader(HttpConstants.CONTENT_ID, contentId);
				Iterator<?> itAttachments = message.getAttachments(mhs);
				if(itAttachments == null || itAttachments.hasNext()==false){
					throw new MessageException("Found XOM Reference with attribute ["+
							org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"] but the message hasn't attachments");
				}
				AttachmentPart ap = null;
				while (itAttachments.hasNext()) {
					if(ap!=null){
						throw new MessageException("Found XOM Reference with attribute ["+
								org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"] but exists more than one attachment with same id");
					}
					ap = (AttachmentPart) itAttachments.next();
				}
				if(ap==null){
					throw new MessageException("Found XOM Reference with attribute ["+
							org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"] but not exists attachment with same id");
				}
		
				
				// base64Element
				Node elementBase64Binary = xomReference.getParentNode();
				if(elementBase64Binary==null){
					throw new MessageException("Found XOM Reference without parent node?");
				}
				QName qname = new QName(elementBase64Binary.getNamespaceURI(), elementBase64Binary.getLocalName());
				//System.out.println("Found Element ["+qname.toString()+"]");
				
		
				// unpackaging
				String base64Binary = null;
				if(fast){
					// fast unpackaging (for validation)
					base64Binary = Base64Utilities.encodeAsString("FAST".getBytes());
				}else{
					base64Binary = Utilities.getAsByteArrayOuputStream(ap.getBase64Content()).toString();
					message.removeAttachments(mhs);
					restoredXomReference = true;
				}
				elementBase64Binary.removeChild(xomReference);
				elementBase64Binary.setTextContent(base64Binary);
				
				
				// add reference
				MtomXomReference reference = new MtomXomReference();
				reference.setNodeName(qname);
				reference.setXomReference(xomReference);
				reference.setNode(elementBase64Binary);
				reference.setContentId(contentId);
				list.add(reference);
			}
			
			
			if(restoredXomReference){
				
				message.removeContentTypeParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE);
				message.removeContentTypeParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_START_INFO);
				
				if(message.countAttachments()>0){
					// esistono altri attachments "originali" non derivati dalla serializzazione mtom
					if(MessageType.SOAP_11.equals(message.getMessageType())){
						message.addContentTypeParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE, HttpConstants.CONTENT_TYPE_SOAP_1_1);
					}
					else if(MessageType.SOAP_12.equals(message.getMessageType())){
						message.addContentTypeParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE, HttpConstants.CONTENT_TYPE_SOAP_1_2);
					}
				}
				else {
					
					ContentType cType = null;
					if(MessageType.SOAP_11.equals(message.getMessageType())){
						cType = new ContentType(HttpConstants.CONTENT_TYPE_SOAP_1_1);
					}
					else if(MessageType.SOAP_12.equals(message.getMessageType())){
						cType = new ContentType(HttpConstants.CONTENT_TYPE_SOAP_1_2);
					}
					
					String charsetParam =  ContentTypeUtilities.readCharsetFromContentType(message.getContentType());
					if(charsetParam!=null && !"".equals(charsetParam)) {
						cType.setParameter(HttpConstants.CONTENT_TYPE_PARAMETER_CHARSET, charsetParam);
					}
					
					message.setContentType(cType.toString());
				}
			}
			
			//message.saveChanges();
			
			return list;
			
		}
		catch(MessageException e){
			throw e;
		}
		catch(MessageNotSupportedException e){
			throw e;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	public static List<MtomXomReference> packaging(OpenSPCoop2Message msgParam, List<MtomXomPackageInfo> packageInfos, boolean body) throws MessageException, MessageNotSupportedException{
		
		if(!MessageType.SOAP_11.equals(msgParam.getMessageType()) && !MessageType.SOAP_12.equals(msgParam.getMessageType())){
			throw MessageNotSupportedException.newMessageNotSupportedException(msgParam.getMessageType());
		}
		OpenSPCoop2SoapMessage message = msgParam.castAsSoap();
		
		try{
		
			List<MtomXomReference> list = new ArrayList<MtomXomReference>();
			
			Element element = null;
			Document documentSOAPPart = message.getSOAPPart();
			if(body){
				element = message.getSOAPBody();
			}
			else{
				element = message.getSOAPPart().getEnvelope();
			}
			if(element==null){
				return list;
			}
			AbstractXPathExpressionEngine xpathEngine = new XPathExpressionEngine(msgParam.getFactory());
			DynamicNamespaceContext dnc = org.openspcoop2.message.xml.DynamicNamespaceContextFactory.getInstance(msgParam.getFactory()).getNamespaceContext(element);
			
			boolean addAttachment = false;
			
			if(packageInfos==null || packageInfos.size()<=0){
				return list;
			}
			for (MtomXomPackageInfo mtomXomPackageInfo : packageInfos) {
				
				String xpathExpressionName = mtomXomPackageInfo.getName();
				String xpathExpression = mtomXomPackageInfo.getXpathExpression();
				
				Object oNode = null;
				try{
					oNode = xpathEngine.getMatchPattern(element, dnc, xpathExpression, XPathReturnType.NODESET);
				}catch(XPathNotFoundException notFound){}
				if(oNode==null){
					// non esistono reference all'oggetto su cui effetture packaging
					if(mtomXomPackageInfo.isRequired()){
						throw new MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") not found reference");
					}
					continue;
				}
				if( !(oNode instanceof NodeList) ){
					throw new MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") return wrong type (expected: "+
							NodeList.class.getName()+"): "+oNode.getClass().getName());
				}
				NodeList nodeList = (NodeList) oNode;
				if(nodeList.getLength()<=0){
					// non esistono reference all'oggetto su cui effetture packaging
					if(mtomXomPackageInfo.isRequired()){
						throw new MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") not found reference (node list is empty)");
					}
					continue;
				}
				//System.out.println("Ritornato: size: "+nodeList.getLength());
				for (int i = 0; i < nodeList.getLength(); i++) {
					
					// elementBase64
					Node elementBase64Binary = nodeList.item(i);
					
					// verifico che non sia gia un xom reference
					if(MTOMUtilities.getIfExistsXomReference(msgParam.getFactory(), (Element)elementBase64Binary)!=null){
						continue; // esiste gia' una xom reference.
					}
					
					QName qname = new QName(elementBase64Binary.getNamespaceURI(), elementBase64Binary.getLocalName());
					//System.out.println("Found Element ["+qname.toString()+"]");
					List<Node> elementBase64BinaryChilds = SoapUtils.getNotEmptyChildNodes(msgParam.getFactory(), elementBase64Binary,false);
					if(elementBase64BinaryChilds!=null && elementBase64BinaryChilds.size()>0){
						throw new MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") found element ("+qname+
								") with childs, mtom optimize packaging is only valid for base64Binary xsd element");
					}
					String contentTypeXMIMEAttribute = null;
					if(elementBase64Binary.hasAttributes()){
						NamedNodeMap attr = elementBase64Binary.getAttributes();
						if(attr!=null){
							for (int j = 0; j < attr.getLength(); j++) {
								Node n = attr.item(j);
								String localName = n.getNodeName();
								if(localName.contains(":")){
									localName = localName.split(":")[1];
								}
								
								// ignoro dichiarazioni di namespace (l'xml verra riorganizzato)
								if(org.openspcoop2.message.constants.Costanti.XMLNS_NAMESPACE.equals(n.getNamespaceURI()) &&
										org.openspcoop2.message.constants.Costanti.XMLNS_LOCAL_NAME.equals(localName)){
									continue;
								}
								
								if(org.openspcoop2.message.soap.mtom.Costanti.XMIME_NAMESPACE.equals(n.getNamespaceURI()) &&
										org.openspcoop2.message.soap.mtom.Costanti.XMIME_ATTRIBUTE_CONTENT_TYPE.equals(localName)){
									contentTypeXMIMEAttribute = n.getNodeValue();
								}else{
									throw new MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") found element ("+qname+
											") with attribute ({"+n.getNamespaceURI()+"}"+localName+
											"), mtom optimize packaging is only valid for base64Binary xsd element (permit only {"+
											org.openspcoop2.message.soap.mtom.Costanti.XMIME_NAMESPACE+"}"+org.openspcoop2.message.soap.mtom.Costanti.XMIME_ATTRIBUTE_CONTENT_TYPE+")");		
								}
							}
						}
					}
					String base64Content = elementBase64Binary.getTextContent();
					if(base64Content==null){
						throw new MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") found element ("+qname+
								") without base64 value (textContent), mtom optimize packaging is only valid for base64Binary xsd element");
					}
					
					
					
					// Attachment
					String contentId = null;
					try{
						AttachmentPart ap = message.createAttachmentPart();
						String contentType = mtomXomPackageInfo.getContentType();
						if(contentType==null){
							if(contentTypeXMIMEAttribute!=null){
								contentType = contentTypeXMIMEAttribute;
							}
							else{
								contentType = HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
							}
						}
						ap.setContentId(message.createContentID(org.openspcoop2.message.soap.mtom.Costanti.OPENSPCOOP2_MTOM_NAMESPACE));
						contentId = ap.getContentId();
						if(contentId.startsWith("<")){
							contentId = contentId.substring(1);
						}
						if(contentId.endsWith(">")){
							contentId = contentId.substring(0,contentId.length()-1);
						}
						ap.setBase64Content(new ByteArrayInputStream(base64Content.getBytes()), contentType);
						message.addAttachmentPart(ap);
						addAttachment = true;
					}catch(Exception e){
						throw new MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") found element ("+qname+
								") with base64 value (textContent). Error occurs during mtom optimize packaging (decodeBase64/createAttach): "+e.getMessage(),e);
					}
					
					
					// packaging
					elementBase64Binary.setTextContent(null);
					Element xomReference = documentSOAPPart.createElementNS(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_NAMESPACE, "xop:"+org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_LOCAL_NAME);
					// Non funziona in axiom
					//Attr xomReferenceHrefAttribute = documentSOAPPart.createAttribute(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF);
					//xomReferenceHrefAttribute.setTextContent(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE+contentId);
					//xomReference.getAttributes().setNamedItem(xomReferenceHrefAttribute);
					xomReference.setAttribute(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF, org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE+contentId);
					elementBase64Binary.appendChild(xomReference);
					
					// add reference
					MtomXomReference reference = new MtomXomReference();
					reference.setNodeName(qname);
					reference.setXomReference(xomReference);
					reference.setNode(elementBase64Binary);
					reference.setContentId(contentId);
					list.add(reference);
				}
				
				
			}
			
			if(addAttachment){
				message.addContentTypeParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE, HttpConstants.CONTENT_TYPE_APPLICATION_XOP_XML);
				if(MessageType.SOAP_11.equals(message.getMessageType())){
					message.addContentTypeParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_START_INFO, HttpConstants.CONTENT_TYPE_SOAP_1_1);
				}
				else if(MessageType.SOAP_12.equals(message.getMessageType())){
					message.addContentTypeParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_START_INFO, HttpConstants.CONTENT_TYPE_SOAP_1_2);
				}
			}
			
			//message.saveChanges();
			
			return list;
			
		}
		catch(MessageException e){
			throw e;
		}
		catch(MessageNotSupportedException e){
			throw e;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
				
	}
	
	public static List<MtomXomReference> verify(OpenSPCoop2Message msgParam, List<MtomXomPackageInfo> packageInfos, boolean body) throws MessageException,MessageNotSupportedException {
		
		if(!MessageType.SOAP_11.equals(msgParam.getMessageType()) && !MessageType.SOAP_12.equals(msgParam.getMessageType())){
			throw MessageNotSupportedException.newMessageNotSupportedException(msgParam.getMessageType());
		}
		OpenSPCoop2SoapMessage message = msgParam.castAsSoap();
		
		try{
			
			List<MtomXomReference> list = new ArrayList<MtomXomReference>();
			
			Element element = null;
			if(body){
				element = message.getSOAPBody();
			}
			else{
				element = message.getSOAPPart().getEnvelope();
			}
			if(element==null){
				return list;
			}
			AbstractXPathExpressionEngine xpathEngine = new XPathExpressionEngine(msgParam.getFactory());
			DynamicNamespaceContext dnc = org.openspcoop2.message.xml.DynamicNamespaceContextFactory.getInstance(msgParam.getFactory()).getNamespaceContext(element);
			
			if(packageInfos==null || packageInfos.size()<=0){
				return list;
			}
			for (MtomXomPackageInfo mtomXomPackageInfo : packageInfos) {
				
				String xpathExpressionName = mtomXomPackageInfo.getName();
				String xpathExpression = mtomXomPackageInfo.getXpathExpression();
				
				Object oNode = null;
				try{
					oNode = xpathEngine.getMatchPattern(element, dnc, xpathExpression, XPathReturnType.NODESET);
				}catch(XPathNotFoundException notFound){}
				if(oNode==null){
					// non esistono reference all'oggetto su cui effetture packaging
					if(mtomXomPackageInfo.isRequired()){
						throw new MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") not found reference");
					}
					continue;
				}
				if( !(oNode instanceof NodeList) ){
					throw new MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") return wrong type (expected: "+
							NodeList.class.getName()+"): "+oNode.getClass().getName());
				}
				NodeList nodeList = (NodeList) oNode;
				if(nodeList.getLength()<=0){
					// non esistono reference all'oggetto su cui effetture packaging
					if(mtomXomPackageInfo.isRequired()){
						throw new MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") not found reference (node list is empty)");
					}
					continue;
				}
				//System.out.println("Ritornato: size: "+nodeList.getLength());
				for (int i = 0; i < nodeList.getLength(); i++) {
					
					// elementBase64
					Node elementBase64Binary = nodeList.item(i);
					QName qname = new QName(elementBase64Binary.getNamespaceURI(), elementBase64Binary.getLocalName());
					//System.out.println("Found Element ["+qname.toString()+"]");
					if(elementBase64Binary.hasAttributes()){
						NamedNodeMap attr = elementBase64Binary.getAttributes();
						if(attr!=null){
							for (int j = 0; j < attr.getLength(); j++) {
								Node n = attr.item(j);
								String localName = n.getNodeName();
								if(localName.contains(":")){
									localName = localName.split(":")[1];
								}
								
								// ignoro dichiarazioni di namespace (l'xml verra riorganizzato)
								if(org.openspcoop2.message.constants.Costanti.XMLNS_NAMESPACE.equals(n.getNamespaceURI()) &&
										org.openspcoop2.message.constants.Costanti.XMLNS_LOCAL_NAME.equals(localName)){
									continue;
								}
								
								// ignoro dichiarazione di content type
								if(org.openspcoop2.message.soap.mtom.Costanti.XMIME_NAMESPACE.equals(n.getNamespaceURI()) &&
										org.openspcoop2.message.soap.mtom.Costanti.XMIME_ATTRIBUTE_CONTENT_TYPE.equals(localName)){
									continue;
								}
								
								throw new MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") found element ("+qname+
										") with attribute ({"+n.getNamespaceURI()+"}"+localName+
											"), mtom optimize packaging structure invalid");
							}
						}
							
					}
					List<Node> nodeListBase64 = SoapUtils.getNotEmptyChildNodes(msgParam.getFactory(), elementBase64Binary,false);
					if(nodeListBase64==null || nodeListBase64.size()<=0){
						throw new MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") found element ("+qname+
								") without childs, mtom optimize packaging require xop:"+org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_LOCAL_NAME+" element");
					}
					if(nodeListBase64.size()>1){
						throw new MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") found element ("+qname+
								") with more than one child, mtom optimize packaging structure invalid");
					}
					
					// xomReference
					Node xomReference = nodeListBase64.get(0);
					if(xomReference.getAttributes()==null || xomReference.getAttributes().getLength()<=0){
						throw new MessageException("Found XOM Reference without attributes ('"+org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"' attribute required)");
					}
					Node xomHref = xomReference.getAttributes().getNamedItem(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF);
					if(xomHref == null){
						throw new MessageException("Found XOM Reference without attribute '"+org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"'");
					}
					String contentId = xomHref.getNodeValue();
					if(contentId == null){
						throw new MessageException("Found XOM Reference with attribute '"+org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"' without value?");
					}
					if(contentId.startsWith(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE)){
						contentId = contentId.substring(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE.length());
					}
					MimeHeaders mhs = new MimeHeaders();
					mhs.addHeader(HttpConstants.CONTENT_ID, contentId);
					Iterator<?> itAttachments = message.getAttachments(mhs);
					if(itAttachments == null || itAttachments.hasNext()==false){
						throw new MessageException("Found XOM Reference with attribute ["+
								org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"] but the message hasn't attachments");
					}
					AttachmentPart ap = null;
					while (itAttachments.hasNext()) {
						if(ap!=null){
							throw new MessageException("Found XOM Reference with attribute ["+
									org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"] but exists more than one attachment with same id");
						}
						ap = (AttachmentPart) itAttachments.next();
					}
					if(ap==null){
						throw new MessageException("Found XOM Reference with attribute ["+
								org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"] but not exists attachment with same id");
					}	
					String contentType = mtomXomPackageInfo.getContentType();
					if(contentType==null){
						contentType = HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
					}
					if(ap.getContentType()==null){
						throw new MessageException("Found XOM Reference with attribute ["+
								org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"]. The attachment hasn't content-type");
					}
					else{
						String apCT = ap.getContentType();
						String base = ContentTypeUtilities.readBaseTypeFromContentType(apCT);
						if(contentType.equals(base)==false){
							throw new MessageException("Found XOM Reference with attribute ["+
									org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"]. The attachment has wrong content-type (expected:"+contentType+"): "+ap.getContentType());
						}
					}
										
					// add reference
					MtomXomReference reference = new MtomXomReference();
					reference.setNodeName(qname);
					reference.setXomReference(xomReference);
					reference.setNode(elementBase64Binary);
					reference.setContentId(contentId);
					list.add(reference);
				}
				
				
			}
			
			return list;
			
		}
		catch(MessageException e){
			throw e;
		}
		catch(MessageNotSupportedException e){
			throw e;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
		
	}
	
	public static void restoreAfterFastUnpackaging(OpenSPCoop2Message msgParam, List<MtomXomReference> references, boolean body) throws MessageException,MessageNotSupportedException {
	
		if(!MessageType.SOAP_11.equals(msgParam.getMessageType()) && !MessageType.SOAP_12.equals(msgParam.getMessageType())){
			throw MessageNotSupportedException.newMessageNotSupportedException(msgParam.getMessageType());
		}
		OpenSPCoop2SoapMessage message = msgParam.castAsSoap();
		
		try{
		
			Element element = null;
			if(body){
				element = message.getSOAPBody();
			}
			else{
				element = message.getSOAPPart().getEnvelope();
			}
			if(element==null){
				return;
			}
			
			if(references==null || references.size()<=0){
				return;
			}
			
			for (MtomXomReference mtomXomReference : references) {
				
				if(mtomXomReference.getNode()==null){
					throw new MessageException("XomReference ["+mtomXomReference.getNodeName()+"] with undefined node");
				}
				if(mtomXomReference.getXomReference()==null){
					throw new MessageException("XomReference ["+mtomXomReference.getNodeName()+"] with undefined xomRerefence");
				}
				mtomXomReference.getNode().setTextContent(null);
				mtomXomReference.getNode().appendChild(mtomXomReference.getXomReference());
				
			}
			
			//message.saveChanges();
		}
		catch(MessageException e){
			throw e;
		}
		catch(MessageNotSupportedException e){
			throw e;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
		
	}
	
	public static Element getIfExistsXomReference(OpenSPCoop2MessageFactory messageFactory, Element element){
		
		try{
			Node n = SoapUtils.getFirstNotEmptyChildNode(messageFactory, element,false);
			if(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_LOCAL_NAME.equals(n.getLocalName()) &&
					org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_NAMESPACE.equals(n.getNamespaceURI())){
				return (Element)n;
			}
//			
//			La ricerca con XPath ritornava anche gli elementi del padre. 
//			AbstractXPathExpressionEngine xpathEngine = new XPathExpressionEngine();
//			DynamicNamespaceContext dnc = org.openspcoop2.message.DynamicNamespaceContextFactory.getInstance().getNamespaceContext(element);
//			Object oNode = xpathEngine.getMatchPattern(element, dnc, org.openspcoop2.message.mtom.Costanti.MTOM_XOP_REFERENCES, XPathReturnType.NODE);
//			return (Element) oNode;
			
			return null;
			
		}catch(Exception e){
			return null;
		}
	}
	
	public static String getCidXomReference(Element element) throws MessageException {
		if(element.getAttributes()==null || element.getAttributes().getLength()<=0){
			throw new MessageException("Found XOM Reference without attributes ('"+org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"' attribute required)");
		}
		Node xomHref = element.getAttributes().getNamedItem(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF);
		if(xomHref == null){
			throw new MessageException("Found XOM Reference without attribute '"+org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"'");
		}
		String contentId = xomHref.getNodeValue();
		if(contentId == null){
			throw new MessageException("Found XOM Reference with attribute '"+org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"' without value?");
		}
		if(contentId.startsWith(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE)){
			contentId = contentId.substring(org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE.length());
		}
		return contentId;
	}
	
	public static AttachmentPart getAttachmentPart(OpenSPCoop2Message msgParam, String cidReference) throws MessageException,MessageNotSupportedException {
		
		if(!MessageType.SOAP_11.equals(msgParam.getMessageType()) && !MessageType.SOAP_12.equals(msgParam.getMessageType())){
			throw MessageNotSupportedException.newMessageNotSupportedException(msgParam.getMessageType());
		}
		OpenSPCoop2SoapMessage message = msgParam.castAsSoap();
		
		MimeHeaders mhs = new MimeHeaders();
		mhs.addHeader(HttpConstants.CONTENT_ID, cidReference);
		Iterator<?> itAttachments = message.getAttachments(mhs);
		if(itAttachments == null || itAttachments.hasNext()==false){
			throw new MessageException("Found XOM Reference with attribute ["+
					org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+cidReference+"] but the message hasn't attachments");
		}
		AttachmentPart ap = null;
		while (itAttachments.hasNext()) {
			if(ap!=null){
				throw new MessageException("Found XOM Reference with attribute ["+
						org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+cidReference+"] but exists more than one attachment with same id");
			}
			ap = (AttachmentPart) itAttachments.next();
		}
		if(ap==null){
			throw new MessageException("Found XOM Reference with attribute ["+
					org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+cidReference+"] but not exists attachment with same id");
		}
		return ap;
	}
}
