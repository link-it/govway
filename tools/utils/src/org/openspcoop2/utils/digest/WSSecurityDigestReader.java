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

package org.openspcoop2.utils.digest;

import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * WSSDigestReader
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSSecurityDigestReader implements IDigestReader {

	private Logger log;
	private AbstractXPathExpressionEngine xpathEngine;
	private AbstractXMLUtils xmlUtils;

	public WSSecurityDigestReader(AbstractXMLUtils xmlUtils,AbstractXPathExpressionEngine xpathEngine) {
		this(LoggerWrapperFactory.getLogger(WSSecurityDigestReader.class),xmlUtils,xpathEngine);
	}
	public WSSecurityDigestReader(Logger log,AbstractXMLUtils xmlUtils,AbstractXPathExpressionEngine xpathEngine) {
		this.log = log;
		this.xmlUtils = xmlUtils;
		this.xpathEngine = xpathEngine;
	}

	@Override
	public String getDigest(Element element,String referenceId,DynamicNamespaceContext dnc) throws UtilsException{

		try {

			// Search ReferenceElement
			String xPathReferenceElement = 
					"//{"+Constants.WSS_HEADER_DS_NAMESPACE+"}"+
							Constants.WSS_HEADER_DS_REFERENCE_ELEMENT+
							"[@"+Constants.WSS_HEADER_DS_REFERENCE_ATTRIBUTE_URI+"='"+referenceId+"']";
			Node n = null;
			try{
				this.log.debug("Search referenceId con xpath ["+xPathReferenceElement+"] ...");
				n = (Node) this.xpathEngine.getMatchPattern(element, dnc, xPathReferenceElement , XPathReturnType.NODE);
			}catch(XPathNotFoundException notFound){
				xPathReferenceElement = 
						"//"+Constants.WSS_HEADER_DS_REFERENCE_ELEMENT+
						"[@"+Constants.WSS_HEADER_DS_REFERENCE_ATTRIBUTE_URI+"='"+referenceId+"']";
				this.log.debug("Search(2) referenceId con xpath ["+xPathReferenceElement+"] ...");
				try{
					n = (Node) this.xpathEngine.getMatchPattern(element, dnc, xPathReferenceElement , XPathReturnType.NODE);
				}catch(XPathNotFoundException notFoundInternal){
					this.log.debug("Reference id ["+referenceId+"] non troavata");
					return null;
				}
			}
			byte [] nBytes= this.xmlUtils.toByteArray(n);
			this.log.debug("Found Reference Element ["+this.xmlUtils.toString(n)+"]");

			// Search DigestValue
			String xPathDigestValue = "//{"+Constants.WSS_HEADER_DS_NAMESPACE+"}"+Constants.WSS_HEADER_DS_REFERENCE_DIGEST_VALUE_ELEMENT+"/text()";
			String digest = null;
			try{
				this.log.debug("Search digestValue con xpath ["+xPathDigestValue+"] ...");
				digest = (String) this.xpathEngine.getMatchPattern(this.xmlUtils.newDocument(nBytes), dnc, xPathDigestValue , XPathReturnType.STRING);
			}catch(XPathNotFoundException notFound){
				xPathDigestValue = "//"+Constants.WSS_HEADER_DS_REFERENCE_DIGEST_VALUE_ELEMENT+"/text()";
				this.log.debug("Search(2) digestValue con xpath ["+xPathDigestValue+"] ...");
				digest = (String) this.xpathEngine.getMatchPattern(element, dnc, xPathReferenceElement , XPathReturnType.STRING);
			}
			this.log.debug("Found DigestValue ["+digest+"]");

			return digest;
		} catch (Exception e) {
			this.log.error("Errore durante la getDigest", e);
			throw new UtilsException(e.getMessage(),e);
		} 
	

	}

}
