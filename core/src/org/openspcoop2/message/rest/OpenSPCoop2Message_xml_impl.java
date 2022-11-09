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

package org.openspcoop2.message.rest;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Implementazione dell'OpenSPCoop2Message utilizzabile per messaggi xml
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2Message_xml_impl extends AbstractBaseOpenSPCoop2RestMessage<Element> implements OpenSPCoop2RestXmlMessage {

	public OpenSPCoop2Message_xml_impl(OpenSPCoop2MessageFactory messageFactory) {
		super(messageFactory);
	}
	public OpenSPCoop2Message_xml_impl(OpenSPCoop2MessageFactory messageFactory, InputStream is,String contentType) throws MessageException {
		super(messageFactory, is, contentType);
	}
	
	@Override
	protected Element buildContent() throws MessageException{
		try{
			return buildContent(this._getInputStream());
		}finally{
			try{
				this._getInputStream().close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	@Override
	protected Element buildContent(DumpByteArrayOutputStream contentBuffer) throws MessageException{
		try{
			if(contentBuffer.isSerializedOnFileSystem()) {
				try(InputStream is = new FileInputStream(contentBuffer.getSerializedFile())){
					return buildContent(is);
				}
			}
			else {
				try(InputStream is = new ByteArrayInputStream(contentBuffer.toByteArray())){
					return buildContent(is);
				}
			}
		}
		catch(MessageException me) {
			throw me;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	protected Element buildContent(InputStream is) throws MessageException{
		InputStreamReader isr = null;
		InputSource isSax = null;
		try{
			isr = new InputStreamReader(is,this.contentTypeCharsetName);
			isSax = new InputSource(isr);
			isSax.setEncoding(this.contentTypeCharsetName);
			return MessageXMLUtils.getInstance(this.messageFactory).newElement(isSax);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}finally{
			try{
				if(isr!=null){
					isr.close();
				}
			}catch(Exception eClose){
				// close
			}
		}
	}

	@Override
	protected String buildContentAsString() throws MessageException{
		try{
			return this.getAsString(this.content, false);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	protected byte[] buildContentAsByteArray() throws MessageException{
		try{
			return this.getAsString(this.content, false).getBytes(this.contentTypeCharsetName);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	protected void serializeContent(OutputStream os, boolean consume) throws MessageException {
		try{
			MessageXMLUtils.getInstance(this.messageFactory).writeTo(this.content, os, true, this.contentTypeCharsetName);
			os.flush();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	@Override
	public boolean isProblemDetailsForHttpApis_RFC7807() throws MessageException,MessageNotSupportedException {
		try{
			if(this.contentType==null) {
				return false;
			}
			String baseType = ContentTypeUtilities.readBaseTypeFromContentType(this.contentType);
			return HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807.equalsIgnoreCase(baseType);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	
	@Override
	public void addElement(String name, String value)  throws MessageException,MessageNotSupportedException{
		 _processElement(name, null, value, true, false, null);
	}
	@Override
	public void addElement(String name, String namespace, String value)  throws MessageException,MessageNotSupportedException{
		_processElement(name, namespace, value, true, false, null);
	}
	
	@Override
	public void addElementIn(String pattern, String name, String value)  throws MessageException,MessageNotSupportedException{
		_processElement(name, null, value, true, false, pattern);
	}
	@Override
	public void addElementIn(String pattern, String name, String namespace, String value)  throws MessageException,MessageNotSupportedException{
		_processElement(name, namespace, value, true, false, pattern);
	}
	
	@Override
	public void removeElement(String name) throws MessageException,MessageNotSupportedException{
		 _processElement(name, null, null, false, true, null);
	}
	@Override
	public void removeElement(String name, String namespace) throws MessageException,MessageNotSupportedException{
		 _processElement(name, namespace, null, false, true, null);
	}
	
	@Override
	public void removeElementIn(String pattern, String name) throws MessageException,MessageNotSupportedException{
		 _processElement(name, null, null, false, true, pattern);
	}
	@Override
	public void removeElementIn(String pattern, String name, String namespace) throws MessageException,MessageNotSupportedException{
		 _processElement(name, namespace, null, false, true, pattern);
	}
	
	public void _processElement(String name, String namespace, String value, boolean add, boolean remove, String pattern) throws MessageException {
		try {
			if(!this.hasContent()) {
				return;
			}
			
			Element content = this.getContent();
			Node element = null;
			if(pattern!=null) {
				XPathExpressionEngine engine = new XPathExpressionEngine(this.messageFactory);
				DynamicNamespaceContext dnc = new DynamicNamespaceContext();
				dnc.findPrefixNamespace(content);
				element = (Node) engine.getMatchPattern(content, dnc, pattern, XPathReturnType.NODE);
			}
			else {
				element = content;
			}
				
			if(remove) {
				List<Node> list = MessageXMLUtils.getInstance(this.messageFactory).getNotEmptyChildNodes(element, false);
				List<Node> removeNodes = new ArrayList<Node>();
				if(list!=null && !list.isEmpty()) {
					for (Node node : list) {
						if(namespace!=null) {
							if(namespace.equals(node.getNamespaceURI()) && name.equals(node.getLocalName())) {
								removeNodes.add(node);
							}
						}
						else {
							if(node.getNamespaceURI()==null && name.equals(node.getLocalName())) {
								removeNodes.add(node);
							}
						}
					}
				}
				while(!removeNodes.isEmpty()) {
					Node n = removeNodes.remove(0);
					element.removeChild(n);
				}
				
				/*NodeList list = null;
				if(namespace!=null) {
					list = element.getElementsByTagNameNS(namespace, name);
				}
				else {
					list = element.getElementsByTagName(name);
				}
				if(list!=null && list.getLength()>0) {
					for (int i = 0; i < list.getLength(); i++) {
						Node n = list.item(i);
						element.removeChild(n);
					}
				}*/
			}
			else if(add) {
				Element eAdd = null;
				if(namespace!=null) {
					eAdd = element.getOwnerDocument().createElementNS(namespace, name);
				}
				else {
					eAdd = element.getOwnerDocument().createElement(name);
				}
				eAdd.setTextContent(value);
				element.appendChild(eAdd);
			}
			
		}catch(Exception e) {
			if(pattern!=null) {
				throw new MessageException("Operazione fallita (pattern: "+pattern+"): "+e.getMessage(),e);
			}
			else {
				throw new MessageException("Operazione fallita: "+e.getMessage(),e);
			}
		}
	}
}
