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

package org.openspcoop2.pdd.core.dynamic;

import java.util.List;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.utils.json.JsonPathNotFoundException;
import org.openspcoop2.utils.json.JsonPathNotValidException;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathNotValidException;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.openspcoop2.utils.xml2json.JsonXmlPathExpressionEngine;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 * GestorePathEstrazione
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PatternExtractor {

	private Logger log;
	
	private OpenSPCoop2MessageFactory messageFactory;
	private Element element = null;
	private Boolean refresh = null;
	private DynamicNamespaceContext dnc;
	
	private String elementJson = null;
	
	public PatternExtractor(OpenSPCoop2MessageFactory messageFactory, Element element, Logger log) {
		this.messageFactory = messageFactory;
		this.element = element; 
		this.dnc = new DynamicNamespaceContext();
		this.dnc.findPrefixNamespace(element);
		this.log = log;
	}
	public PatternExtractor(String elementJson, Logger log) {
		this.elementJson = elementJson;
		this.log = log;
	}
	
	public void refreshContent() {
		if(this.element!=null && this.refresh==null) {
			this._refreshContent();
		}
	}
	private synchronized void _refreshContent() {
		// effettuo il refresh, altrimenti le regole xpath applicate sulla richiesta, nel flusso di risposta (es. header http della risposta) non funzionano.
		try {
			this.refresh = true;
			if(this.element!=null) {
				XMLUtils xmlUtils = XMLUtils.getInstance(this.messageFactory);
				this.element = xmlUtils.newElement(xmlUtils.toByteArray(this.element));
			}
		}catch(Exception e){
			this.log.error("Refresh fallito: "+e.getMessage(),e);
		}
	}
	
	public boolean match(String pattern) throws DynamicException {
		String v = read(pattern);
		return v!=null && !"".equals(v);
	}
	
	public String read(String pattern) throws DynamicException {
		String valore = null;
		try {
			if(this.element!=null) {
				AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(this.messageFactory);
				valore = AbstractXPathExpressionEngine.extractAndConvertResultAsString(this.element, this.dnc, xPathEngine, pattern, this.log);
			}
			else {
				valore = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(this.elementJson, pattern, this.log);
			}
		}
		catch(XPathNotFoundException e){
			this.log.debug("Estrazione '"+pattern+"' non ha trovato risultati: "+e.getMessage(),e);
		}
		catch(XPathNotValidException e){
			throw new DynamicException(e.getMessage(),e);
		}
		catch(JsonPathNotFoundException e){
			this.log.debug("Estrazione '"+pattern+"' non ha trovato risultati: "+e.getMessage(),e);
		}
		catch(JsonPathNotValidException e){
			throw new DynamicException(e.getMessage(),e);
		}
		catch(org.openspcoop2.utils.UtilsMultiException e) {
			int index = 0;
			boolean notFound = true;
			boolean notValid = true;
			for (Throwable t : e.getExceptions()) {
				if(t instanceof XPathNotFoundException || t instanceof JsonPathNotFoundException) {
					this.log.debug("Estrazione ("+index+") '"+pattern+"' fallita: "+t.getMessage(),t);
				}
				else {
					notFound = false;
				}
				
				if(!(t instanceof XPathNotValidException) && !(t instanceof JsonPathNotValidException)) {
					notValid = false;
				}
				
				index++;
			}
			if(!notFound) {
				if(notValid) {
					throw new DynamicException(e.getMessage(),e);
				}
				else {
					throw new DynamicException("Estrazione '"+pattern+"' fallita: "+e.getMessage(),e);
				}
			}
		}
		catch(Exception e){
			throw new DynamicException("Estrazione '"+pattern+"' fallita: "+e.getMessage(),e);
		}
		return valore;
	}
	
	public List<String> readList(String pattern) throws DynamicException {
		List<String> valore = null;
		try {
			if(this.element!=null) {
				AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(this.messageFactory);
				xPathEngine.getMatchPattern(this.element, this.dnc, pattern, XPathReturnType.BOOLEAN);
				valore = AbstractXPathExpressionEngine.extractAndConvertResultAsList(this.element, this.dnc, xPathEngine, pattern, this.log);
			}
			else {
				valore = JsonXmlPathExpressionEngine.extractAndConvertResultAsList(this.elementJson, pattern, this.log);
			}
		}
		catch(XPathNotFoundException e){
			this.log.debug("Estrazione '"+pattern+"' non ha trovato risultati: "+e.getMessage(),e);
		}
		catch(XPathNotValidException e){
			throw new DynamicException(e.getMessage(),e);
		}
		catch(JsonPathNotFoundException e){
			this.log.debug("Estrazione '"+pattern+"' non ha trovato risultati: "+e.getMessage(),e);
		}
		catch(JsonPathNotValidException e){
			throw new DynamicException(e.getMessage(),e);
		}
		catch(org.openspcoop2.utils.UtilsMultiException e) {
			int index = 0;
			boolean notFound = true;
			boolean notValid = true;
			for (Throwable t : e.getExceptions()) {
				if(t instanceof XPathNotFoundException || t instanceof JsonPathNotFoundException) {
					this.log.debug("Estrazione ("+index+") '"+pattern+"' fallita: "+t.getMessage(),t);
				}
				else {
					notFound = false;
				}
				
				if(!(t instanceof XPathNotValidException) && !(t instanceof JsonPathNotValidException)) {
					notValid = false;
				}
				
				index++;
			}
			if(!notFound) {
				if(notValid) {
					throw new DynamicException(e.getMessage(),e);
				}
				else {
					throw new DynamicException("Estrazione '"+pattern+"' fallita: "+e.getMessage(),e);
				}
			}
		}
		catch(Exception e){
			throw new DynamicException("Estrazione '"+pattern+"' fallita: "+e.getMessage(),e);
		}
		return valore;
	}
}
