/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.pdf;

import java.io.ByteArrayOutputStream;

import org.apache.pdfbox.pdmodel.interactive.form.PDXFAResource;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
* XFADocument
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class XFADocument {

	public static final String XFA_DATA_NAMESPACE = "http://www.xfa.org/schema/xfa-data/1.0/";
	public static final String XFA_DATASETS_LOCAL_NAME = "datasets";
	public static final String XFA_DATA_LOCAL_NAME = "data";
	
	public static byte[] addXfaDatasets(byte[] content) throws UtilsException {
		try {
			String xmlPrefix = "<xfa:"+XFA_DATASETS_LOCAL_NAME+" xmlns:xfa=\""+XFA_DATA_NAMESPACE+"\">\n"+
					"<xfa:"+XFA_DATA_LOCAL_NAME+">\n";
			String xmlSuffix = "</xfa:"+XFA_DATA_LOCAL_NAME+">\n</xfa:"+XFA_DATASETS_LOCAL_NAME+">";
				
			ByteArrayOutputStream bout = null;
			bout = new ByteArrayOutputStream();
			bout.write(xmlPrefix.getBytes());
			bout.write(content);
			bout.write(xmlSuffix.getBytes());
			bout.flush();
			bout.close();	
			return bout.toByteArray();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	private PDXFAResource xfa;
	private Document document;
	private byte[] content;
	
	public PDXFAResource getXfa() {
		return this.xfa;
	}
	public void setXfa(PDXFAResource xfa) {
		this.xfa = xfa;
	}
	public Document getDocument() {
		return this.document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	public byte[] getContent() {
		return this.content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public boolean isXfaDataContent() {
		Node n = this.getXfaDataContentNode();
		return n!=null;
	}
	public Node getXfaDataContentNode() {
		if(this.document!=null && this.document.getDocumentElement()!=null) {
			Element rootElement = this.document.getDocumentElement();
			if(XFA_DATA_NAMESPACE.equals(rootElement.getNamespaceURI()) && XFA_DATASETS_LOCAL_NAME.equals(rootElement.getLocalName())) {
				XMLUtils xmlUtils = XMLUtils.getInstance();
				Node nData = xmlUtils.getFirstNotEmptyChildNode(rootElement, false);
				if(XFA_DATA_NAMESPACE.equals(nData.getNamespaceURI()) && XFA_DATA_LOCAL_NAME.equals(nData.getLocalName())) {
					return xmlUtils.getFirstNotEmptyChildNode(nData, false);
				}
			}
		}
		return null;
	}
	public byte[] getXfaDataContent() throws UtilsException {
		try {
			byte[] contentData = null;
			Node n = this.getXfaDataContentNode();
			if(n!=null) {
				XMLUtils xmlUtils = XMLUtils.getInstance();
				contentData = xmlUtils.toByteArray(n, true);
			}
			return contentData;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
