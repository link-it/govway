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
 * AbstractXMLDigestReader
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractXMLDigestReader implements IDigestReader {

	private Logger log;
	private AbstractXPathExpressionEngine xpathEngine;
	private AbstractXMLUtils xmlUtils;

	public AbstractXMLDigestReader(AbstractXMLUtils xmlUtils,AbstractXPathExpressionEngine xpathEngine) {
		this(LoggerWrapperFactory.getLogger(AbstractXMLDigestReader.class),xmlUtils,xpathEngine);
	}
	public AbstractXMLDigestReader(Logger log,AbstractXMLUtils xmlUtils,AbstractXPathExpressionEngine xpathEngine) {
		this.log = log;
		this.xmlUtils = xmlUtils;
		this.xpathEngine = xpathEngine;
	}

	@Override
	public String getDigest(Element element,String referenceId,DynamicNamespaceContext dnc) throws UtilsException{

		try {

			// Search ReferenceElement
			String xPathReferenceElement = 
					"//{"+Constants.DS_NAMESPACE+"}"+
							Constants.DS_REFERENCE_ELEMENT+
							"[@"+Constants.DS_REFERENCE_ATTRIBUTE_URI+"='"+referenceId+"']";
			Node n = null;
			try{
				this.log.debug("Search referenceId con xpath ["+xPathReferenceElement+"] ...");
				n = (Node) this.xpathEngine.getMatchPattern(element, dnc, xPathReferenceElement , XPathReturnType.NODE);
			}catch(XPathNotFoundException notFound){
				xPathReferenceElement = 
						"//"+Constants.DS_REFERENCE_ELEMENT+
						"[@"+Constants.DS_REFERENCE_ATTRIBUTE_URI+"='"+referenceId+"']";
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
			String xPathDigestValue = "//{"+Constants.DS_NAMESPACE+"}"+Constants.DS_REFERENCE_DIGEST_VALUE_ELEMENT+"/text()";
			String digest = null;
			try{
				this.log.debug("Search digestValue con xpath ["+xPathDigestValue+"] ...");
				digest = (String) this.xpathEngine.getMatchPattern(this.xmlUtils.newDocument(nBytes), dnc, xPathDigestValue , XPathReturnType.STRING);
			}catch(XPathNotFoundException notFound){
				xPathDigestValue = "//"+Constants.DS_REFERENCE_DIGEST_VALUE_ELEMENT+"/text()";
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
