/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.message.mtom;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.mail.internet.ContentType;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;

import org.openspcoop2.message.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageException;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.message.XPathExpressionEngine;
import org.openspcoop2.utils.Utilities;
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
	
	public static SOAPVersion readSoapVersionFromMtomContentType(String contentType) throws OpenSPCoop2MessageException{
		try{
			return readSoapVersionFromMtomContentType(new ContentType(contentType));
		}
		catch(OpenSPCoop2MessageException e){
			throw e;
		}
		catch(Exception e){
			throw new OpenSPCoop2MessageException(e.getMessage(),e);
		}
	}
	public static SOAPVersion readSoapVersionFromMtomContentType(ContentType contentType) throws OpenSPCoop2MessageException{
		try{
			if(contentType==null){
				throw new OpenSPCoop2MessageException("ContentType non fornito");
			}
			
			// baseType
			if(contentType.getBaseType()==null){
				throw new OpenSPCoop2MessageException("ContentType.baseType non definito");
			}
			if(org.openspcoop2.message.Costanti.CONTENT_TYPE_MULTIPART.equals(contentType.getBaseType().toLowerCase())==false){
				throw new OpenSPCoop2MessageException("ContentType.baseType ["+contentType.getBaseType()+
						"] differente da quello atteso per un messaggio MTOM/XOP ["+org.openspcoop2.message.Costanti.CONTENT_TYPE_MULTIPART+"]");
			}
			if(contentType.getParameterList()==null || contentType.getParameterList().size()<=0){
				throw new OpenSPCoop2MessageException("ContentType non conforme a quanto definito nella specifica MTOM/XOP (non sono presenti parametri)");
			}
			
			// type
			String type = contentType.getParameter(org.openspcoop2.message.Costanti.CONTENT_TYPE_MULTIPART_TYPE);
			if(type==null){
				throw new OpenSPCoop2MessageException("ContentType non conforme a quanto definito nella specifica MTOM/XOP (Parametro '"+
						org.openspcoop2.message.Costanti.CONTENT_TYPE_MULTIPART_TYPE+"' non presente)");
			}
			if(org.openspcoop2.message.Costanti.CONTENT_TYPE_APPLICATION_XOP_XML.equals(type.toLowerCase())==false){
				throw new OpenSPCoop2MessageException("ContentType.parameters."+org.openspcoop2.message.Costanti.CONTENT_TYPE_MULTIPART_TYPE+" ["+type+
						"] differente da quello atteso per un messaggio MTOM/XOP ["+org.openspcoop2.message.Costanti.CONTENT_TYPE_APPLICATION_XOP_XML+"]");
			}
			
			// startInfo
			String startInfo = contentType.getParameter(org.openspcoop2.message.Costanti.CONTENT_TYPE_MULTIPART_START_INFO);
			if(startInfo==null){
				throw new OpenSPCoop2MessageException("ContentType non conforme a quanto definito nella specifica MTOM/XOP (Parametro '"+
						org.openspcoop2.message.Costanti.CONTENT_TYPE_MULTIPART_START_INFO+"' non presente)");
			}
			SOAPVersion v = null;
			if(org.openspcoop2.message.Costanti.CONTENT_TYPE_SOAP_1_1.equals(startInfo.toLowerCase())){
				v = SOAPVersion.SOAP11;
			}
			else if(org.openspcoop2.message.Costanti.CONTENT_TYPE_SOAP_1_2.equals(startInfo.toLowerCase())){
				v = SOAPVersion.SOAP12;
			}
			else{
				throw new OpenSPCoop2MessageException("ContentType.parameters."+org.openspcoop2.message.Costanti.CONTENT_TYPE_MULTIPART_START_INFO+" ["+startInfo+
						"] differente da quello atteso per un messaggio MTOM/XOP ["+org.openspcoop2.message.Costanti.CONTENT_TYPE_SOAP_1_1+" o "+
						org.openspcoop2.message.Costanti.CONTENT_TYPE_SOAP_1_2+"]");
			}
			return v;
			
		}
		catch(OpenSPCoop2MessageException e){
			throw e;
		}
		catch(Exception e){
			throw new OpenSPCoop2MessageException(e.getMessage(),e);
		}
	}
	
	public static List<MtomXomReference> unpackaging(OpenSPCoop2Message message, boolean fast, boolean body) throws OpenSPCoop2MessageException{
		
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
			
			AbstractXPathExpressionEngine xpathEngine = new XPathExpressionEngine();
			DynamicNamespaceContext dnc = org.openspcoop2.message.DynamicNamespaceContextFactory.getInstance().getNamespaceContext(element);
			Object oNode = null;
			try{
				oNode = xpathEngine.getMatchPattern(element, dnc, org.openspcoop2.message.mtom.Costanti.MTOM_XOP_REFERENCES, XPathReturnType.NODESET);
			}catch(XPathNotFoundException notFound){}
			if(oNode==null){
				// non esistono xom reference
				return list;
			}
			if( !(oNode instanceof NodeList) ){
				throw new OpenSPCoop2MessageException("XpathEngine (expr:"+org.openspcoop2.message.mtom.Costanti.MTOM_XOP_REFERENCES+") return wrong type (expected: "+
						NodeList.class.getName()+"): "+oNode.getClass().getName());
			}
			NodeList nodeList = (NodeList) oNode;
			//System.out.println("Ritornato: size: "+nodeList.getLength());
			for (int i = 0; i < nodeList.getLength(); i++) {
				
				// xomReference
				Node xomReference = nodeList.item(i);
				if(xomReference.getAttributes()==null || xomReference.getAttributes().getLength()<=0){
					throw new OpenSPCoop2MessageException("Found XOM Reference without attributes ('"+org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"' attribute required)");
				}
				Node xomHref = xomReference.getAttributes().getNamedItem(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF);
				if(xomHref == null){
					throw new OpenSPCoop2MessageException("Found XOM Reference without attribute '"+org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"'");
				}
				String contentId = xomHref.getNodeValue();
				if(contentId == null){
					throw new OpenSPCoop2MessageException("Found XOM Reference with attribute '"+org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"' without value?");
				}
				if(contentId.startsWith(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE)){
					contentId = contentId.substring(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE.length());
				}
				MimeHeaders mhs = new MimeHeaders();
				mhs.addHeader(Costanti.CONTENT_ID, contentId);
				Iterator<?> itAttachments = message.getAttachments(mhs);
				if(itAttachments == null || itAttachments.hasNext()==false){
					throw new OpenSPCoop2MessageException("Found XOM Reference with attribute ["+
							org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"] but the message hasn't attachments");
				}
				AttachmentPart ap = null;
				while (itAttachments.hasNext()) {
					if(ap!=null){
						throw new OpenSPCoop2MessageException("Found XOM Reference with attribute ["+
								org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"] but exists more than one attachment with same id");
					}
					ap = (AttachmentPart) itAttachments.next();
				}
				if(ap==null){
					throw new OpenSPCoop2MessageException("Found XOM Reference with attribute ["+
							org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"] but not exists attachment with same id");
				}
		
				
				// base64Element
				Node elementBase64Binary = xomReference.getParentNode();
				if(elementBase64Binary==null){
					throw new OpenSPCoop2MessageException("Found XOM Reference without parent node?");
				}
				QName qname = new QName(elementBase64Binary.getNamespaceURI(), elementBase64Binary.getLocalName());
				//System.out.println("Found Element ["+qname.toString()+"]");
				
		
				// unpackaging
				String base64Binary = null;
				if(fast){
					// fast unpackaging (for validation)
					base64Binary = org.apache.soap.encoding.soapenc.Base64.encode("FAST".getBytes());
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
				
				message.removeContentTypeParameter(Costanti.CONTENT_TYPE_MULTIPART_TYPE);
				message.removeContentTypeParameter(Costanti.CONTENT_TYPE_MULTIPART_START_INFO);
				
				if(message.countAttachments()>0){
					// esistono altri attachments "originali" non derivati dalla serializzazione mtom
					if(SOAPVersion.SOAP11.equals(message.getVersioneSoap())){
						message.addContentTypeParameter(Costanti.CONTENT_TYPE_MULTIPART_TYPE, Costanti.CONTENT_TYPE_SOAP_1_1);
					}
					else if(SOAPVersion.SOAP12.equals(message.getVersioneSoap())){
						message.addContentTypeParameter(Costanti.CONTENT_TYPE_MULTIPART_TYPE, Costanti.CONTENT_TYPE_SOAP_1_2);
					}
				}
			}
			
			//message.saveChanges();
			
			return list;
			
		}
		catch(OpenSPCoop2MessageException e){
			throw e;
		}
		catch(Exception e){
			throw new OpenSPCoop2MessageException(e.getMessage(),e);
		}
	}
	
	public static List<MtomXomReference> packaging(OpenSPCoop2Message message, List<MtomXomPackageInfo> packageInfos, boolean body) throws OpenSPCoop2MessageException{
		
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
			AbstractXPathExpressionEngine xpathEngine = new XPathExpressionEngine();
			DynamicNamespaceContext dnc = org.openspcoop2.message.DynamicNamespaceContextFactory.getInstance().getNamespaceContext(element);
			
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
						throw new OpenSPCoop2MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") not found reference");
					}
					continue;
				}
				if( !(oNode instanceof NodeList) ){
					throw new OpenSPCoop2MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") return wrong type (expected: "+
							NodeList.class.getName()+"): "+oNode.getClass().getName());
				}
				NodeList nodeList = (NodeList) oNode;
				if(nodeList.getLength()<=0){
					// non esistono reference all'oggetto su cui effetture packaging
					if(mtomXomPackageInfo.isRequired()){
						throw new OpenSPCoop2MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") not found reference (node list is empty)");
					}
					continue;
				}
				//System.out.println("Ritornato: size: "+nodeList.getLength());
				for (int i = 0; i < nodeList.getLength(); i++) {
					
					// elementBase64
					Node elementBase64Binary = nodeList.item(i);
					
					// verifico che non sia gia un xom reference
					if(MTOMUtilities.getIfExistsXomReference((Element)elementBase64Binary)!=null){
						continue; // esiste gia' una xom reference.
					}
					
					QName qname = new QName(elementBase64Binary.getNamespaceURI(), elementBase64Binary.getLocalName());
					//System.out.println("Found Element ["+qname.toString()+"]");
					Vector<Node> elementBase64BinaryChilds = SoapUtils.getNotEmptyChildNodes(elementBase64Binary,false);
					if(elementBase64BinaryChilds!=null && elementBase64BinaryChilds.size()>0){
						throw new OpenSPCoop2MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") found element ("+qname+
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
								if(org.openspcoop2.message.Costanti.XMLNS_NAMESPACE.equals(n.getNamespaceURI()) &&
										org.openspcoop2.message.Costanti.XMLNS_LOCAL_NAME.equals(localName)){
									continue;
								}
								
								if(org.openspcoop2.message.mtom.Costanti.XMIME_NAMESPACE.equals(n.getNamespaceURI()) &&
										org.openspcoop2.message.mtom.Costanti.XMIME_ATTRIBUTE_CONTENT_TYPE.equals(localName)){
									contentTypeXMIMEAttribute = n.getNodeValue();
								}else{
									throw new OpenSPCoop2MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") found element ("+qname+
											") with attribute ({"+n.getNamespaceURI()+"}"+localName+
											"), mtom optimize packaging is only valid for base64Binary xsd element (permit only {"+
											org.openspcoop2.message.mtom.Costanti.XMIME_NAMESPACE+"}"+org.openspcoop2.message.mtom.Costanti.XMIME_ATTRIBUTE_CONTENT_TYPE+")");		
								}
							}
						}
					}
					String base64Content = elementBase64Binary.getTextContent();
					if(base64Content==null){
						throw new OpenSPCoop2MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") found element ("+qname+
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
								contentType = Costanti.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
							}
						}
						ap.setContentId(message.createContentID(org.openspcoop2.message.mtom.Costanti.OPENSPCOOP2_MTOM_NAMESPACE));
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
						throw new OpenSPCoop2MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") found element ("+qname+
								") with base64 value (textContent). Error occurs during mtom optimize packaging (decodeBase64/createAttach): "+e.getMessage(),e);
					}
					
					
					// packaging
					elementBase64Binary.setTextContent(null);
					Element xomReference = documentSOAPPart.createElementNS(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_NAMESPACE, "xop:"+org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_LOCAL_NAME);
					// Non funziona in axiom
					//Attr xomReferenceHrefAttribute = documentSOAPPart.createAttribute(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF);
					//xomReferenceHrefAttribute.setTextContent(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE+contentId);
					//xomReference.getAttributes().setNamedItem(xomReferenceHrefAttribute);
					xomReference.setAttribute(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF, org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE+contentId);
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
				message.addContentTypeParameter(Costanti.CONTENT_TYPE_MULTIPART_TYPE, org.openspcoop2.message.mtom.Costanti.CONTENT_TYPE_APPLICATION_XOP_XML);
				if(SOAPVersion.SOAP11.equals(message.getVersioneSoap())){
					message.addContentTypeParameter(Costanti.CONTENT_TYPE_MULTIPART_START_INFO, Costanti.CONTENT_TYPE_SOAP_1_1);
				}
				else if(SOAPVersion.SOAP12.equals(message.getVersioneSoap())){
					message.addContentTypeParameter(Costanti.CONTENT_TYPE_MULTIPART_START_INFO, Costanti.CONTENT_TYPE_SOAP_1_2);
				}
			}
			
			//message.saveChanges();
			
			return list;
			
		}
		catch(OpenSPCoop2MessageException e){
			throw e;
		}
		catch(Exception e){
			throw new OpenSPCoop2MessageException(e.getMessage(),e);
		}
				
	}
	
	public static List<MtomXomReference> verify(OpenSPCoop2Message message, List<MtomXomPackageInfo> packageInfos, boolean body) throws OpenSPCoop2MessageException{
		
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
			AbstractXPathExpressionEngine xpathEngine = new XPathExpressionEngine();
			DynamicNamespaceContext dnc = org.openspcoop2.message.DynamicNamespaceContextFactory.getInstance().getNamespaceContext(element);
			
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
						throw new OpenSPCoop2MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") not found reference");
					}
					continue;
				}
				if( !(oNode instanceof NodeList) ){
					throw new OpenSPCoop2MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") return wrong type (expected: "+
							NodeList.class.getName()+"): "+oNode.getClass().getName());
				}
				NodeList nodeList = (NodeList) oNode;
				if(nodeList.getLength()<=0){
					// non esistono reference all'oggetto su cui effetture packaging
					if(mtomXomPackageInfo.isRequired()){
						throw new OpenSPCoop2MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") not found reference (node list is empty)");
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
								if(org.openspcoop2.message.Costanti.XMLNS_NAMESPACE.equals(n.getNamespaceURI()) &&
										org.openspcoop2.message.Costanti.XMLNS_LOCAL_NAME.equals(localName)){
									continue;
								}
								
								// ignoro dichiarazione di content type
								if(org.openspcoop2.message.mtom.Costanti.XMIME_NAMESPACE.equals(n.getNamespaceURI()) &&
										org.openspcoop2.message.mtom.Costanti.XMIME_ATTRIBUTE_CONTENT_TYPE.equals(localName)){
									continue;
								}
								
								throw new OpenSPCoop2MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") found element ("+qname+
										") with attribute ({"+n.getNamespaceURI()+"}"+localName+
											"), mtom optimize packaging structure invalid");
							}
						}
							
					}
					Vector<Node> nodeListBase64 = SoapUtils.getNotEmptyChildNodes(elementBase64Binary,false);
					if(nodeListBase64==null || nodeListBase64.size()<=0){
						throw new OpenSPCoop2MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") found element ("+qname+
								") without childs, mtom optimize packaging require xop:"+org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_LOCAL_NAME+" element");
					}
					if(nodeListBase64.size()>1){
						throw new OpenSPCoop2MessageException("XpathEngine (expr("+xpathExpressionName+"):"+xpathExpression+") found element ("+qname+
								") with more than one child, mtom optimize packaging structure invalid");
					}
					
					// xomReference
					Node xomReference = nodeListBase64.get(0);
					if(xomReference.getAttributes()==null || xomReference.getAttributes().getLength()<=0){
						throw new OpenSPCoop2MessageException("Found XOM Reference without attributes ('"+org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"' attribute required)");
					}
					Node xomHref = xomReference.getAttributes().getNamedItem(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF);
					if(xomHref == null){
						throw new OpenSPCoop2MessageException("Found XOM Reference without attribute '"+org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"'");
					}
					String contentId = xomHref.getNodeValue();
					if(contentId == null){
						throw new OpenSPCoop2MessageException("Found XOM Reference with attribute '"+org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"' without value?");
					}
					if(contentId.startsWith(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE)){
						contentId = contentId.substring(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE.length());
					}
					MimeHeaders mhs = new MimeHeaders();
					mhs.addHeader(Costanti.CONTENT_ID, contentId);
					Iterator<?> itAttachments = message.getAttachments(mhs);
					if(itAttachments == null || itAttachments.hasNext()==false){
						throw new OpenSPCoop2MessageException("Found XOM Reference with attribute ["+
								org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"] but the message hasn't attachments");
					}
					AttachmentPart ap = null;
					while (itAttachments.hasNext()) {
						if(ap!=null){
							throw new OpenSPCoop2MessageException("Found XOM Reference with attribute ["+
									org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"] but exists more than one attachment with same id");
						}
						ap = (AttachmentPart) itAttachments.next();
					}
					if(ap==null){
						throw new OpenSPCoop2MessageException("Found XOM Reference with attribute ["+
								org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"] but not exists attachment with same id");
					}	
					String contentType = mtomXomPackageInfo.getContentType();
					if(contentType==null){
						contentType = Costanti.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
					}
					if(ap.getContentType()==null){
						throw new OpenSPCoop2MessageException("Found XOM Reference with attribute ["+
								org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"]. The attachment hasn't content-type");
					}
					else if(contentType.equals(ap.getContentType())==false){
						throw new OpenSPCoop2MessageException("Found XOM Reference with attribute ["+
								org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"]. The attachment has wrong content-type (expected:"+contentType+"): "+ap.getContentType());
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
		catch(OpenSPCoop2MessageException e){
			throw e;
		}
		catch(Exception e){
			throw new OpenSPCoop2MessageException(e.getMessage(),e);
		}
		
	}
	
	public static void restoreAfterFastUnpackaging(OpenSPCoop2Message message, List<MtomXomReference> references, boolean body) throws OpenSPCoop2MessageException{
	
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
					throw new OpenSPCoop2MessageException("XomReference ["+mtomXomReference.getNodeName()+"] with undefined node");
				}
				if(mtomXomReference.getXomReference()==null){
					throw new OpenSPCoop2MessageException("XomReference ["+mtomXomReference.getNodeName()+"] with undefined xomRerefence");
				}
				mtomXomReference.getNode().setTextContent(null);
				mtomXomReference.getNode().appendChild(mtomXomReference.getXomReference());
				
			}
			
			//message.saveChanges();
		}
		catch(OpenSPCoop2MessageException e){
			throw e;
		}
		catch(Exception e){
			throw new OpenSPCoop2MessageException(e.getMessage(),e);
		}
		
	}
	
	public static Element getIfExistsXomReference(Element element){
		
		try{
			Node n = SoapUtils.getFirstNotEmptyChildNode(element,false);
			if(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_LOCAL_NAME.equals(n.getLocalName()) &&
					org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_NAMESPACE.equals(n.getNamespaceURI())){
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
	
	public static String getCidXomReference(Element element) throws OpenSPCoop2MessageException{
		if(element.getAttributes()==null || element.getAttributes().getLength()<=0){
			throw new OpenSPCoop2MessageException("Found XOM Reference without attributes ('"+org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"' attribute required)");
		}
		Node xomHref = element.getAttributes().getNamedItem(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF);
		if(xomHref == null){
			throw new OpenSPCoop2MessageException("Found XOM Reference without attribute '"+org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"'");
		}
		String contentId = xomHref.getNodeValue();
		if(contentId == null){
			throw new OpenSPCoop2MessageException("Found XOM Reference with attribute '"+org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"' without value?");
		}
		if(contentId.startsWith(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE)){
			contentId = contentId.substring(org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE.length());
		}
		return contentId;
	}
	
	public static AttachmentPart getAttachmentPart(OpenSPCoop2Message message, String cidReference) throws OpenSPCoop2MessageException{
		MimeHeaders mhs = new MimeHeaders();
		mhs.addHeader(Costanti.CONTENT_ID, cidReference);
		Iterator<?> itAttachments = message.getAttachments(mhs);
		if(itAttachments == null || itAttachments.hasNext()==false){
			throw new OpenSPCoop2MessageException("Found XOM Reference with attribute ["+
					org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+cidReference+"] but the message hasn't attachments");
		}
		AttachmentPart ap = null;
		while (itAttachments.hasNext()) {
			if(ap!=null){
				throw new OpenSPCoop2MessageException("Found XOM Reference with attribute ["+
						org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+cidReference+"] but exists more than one attachment with same id");
			}
			ap = (AttachmentPart) itAttachments.next();
		}
		if(ap==null){
			throw new OpenSPCoop2MessageException("Found XOM Reference with attribute ["+
					org.openspcoop2.message.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+cidReference+"] but not exists attachment with same id");
		}
		return ap;
	}
}
