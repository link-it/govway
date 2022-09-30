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

package org.openspcoop2.pdd.core.dynamic;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.utils.json.JsonPathNotFoundException;
import org.openspcoop2.utils.json.JsonPathNotValidException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathNotValidException;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.openspcoop2.utils.xml2json.JsonXmlPathExpressionEngine;
import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * GestorePathEstrazione
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PatternExtractor {

	public static PatternExtractor getJsonPatternExtractor(String json, Logger log, boolean bufferMessage_readOnly, Context context) throws DynamicException {
		try {
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			OpenSPCoop2Message jsonMessageVerifica = messageFactory.createMessage(MessageType.JSON, MessageRole.NONE, 
					HttpConstants.CONTENT_TYPE_JSON, json.getBytes()).getMessage();
			MessageContent messageContent = new MessageContent(jsonMessageVerifica.castAsRestJson(), bufferMessage_readOnly, context);
			return new PatternExtractor(messageFactory, messageContent, log);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public static PatternExtractor getXmlPatternExtractor(Element element, Logger log, boolean bufferMessage_readOnly, Context context) throws DynamicException {
		try {
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			OpenSPCoop2Message jsonMessageVerifica = messageFactory.createMessage(MessageType.XML, MessageRole.NONE, 
					HttpConstants.CONTENT_TYPE_XML, XMLUtils.getInstance(messageFactory).toByteArray(element, true)).getMessage();
			MessageContent messageContent = new MessageContent(jsonMessageVerifica.castAsRestXml(), bufferMessage_readOnly, context);
			return new PatternExtractor(messageFactory, messageContent, log);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	private Logger log;
	
	private OpenSPCoop2MessageFactory messageFactory;
	private Element element = null;
	private Boolean refresh = null;
	private DynamicNamespaceContext dnc;
	
	private String elementJson = null;
	
	private MessageContent messageContent = null;
	private boolean messageContent_initialized = false;
	
	public PatternExtractor(OpenSPCoop2MessageFactory messageFactory, MessageContent messageContent, Logger log) {
		this.messageFactory = messageFactory;
		this.messageContent = messageContent;
		this.log = log;
	}
	
	// I seguenti costruttori li lascio per backward compatibility se usati dentro delle trasformazioni
	@Deprecated
	public PatternExtractor(OpenSPCoop2MessageFactory messageFactory, Element element, Logger log) {
		try {
			PatternExtractor pe = getXmlPatternExtractor(element, log, false, null);
			this.messageFactory = pe.messageFactory;
			this.messageContent = pe.messageContent;
			this.log = pe.log;
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	@Deprecated
	public PatternExtractor(String elementJson, Logger log) {
		try {
			PatternExtractor pe = getJsonPatternExtractor(elementJson, log, false, null);
			this.messageFactory = pe.messageFactory;
			this.messageContent = pe.messageContent;
			this.log = pe.log;
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	
	private void init() throws DynamicException {
		if(!this.messageContent_initialized) {
			_init();
		}
	}
	private org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("PatternExtractor");
	private void _init() throws DynamicException {
		this.semaphore.acquireThrowRuntime("init");
		try {
			if(!this.messageContent_initialized) {
				if(this.messageContent!=null) {
					try {
						//this.messageFactory = this.messageContent.getMessageFactory();
						if(this.messageContent.isJson()) {
							this.elementJson = this.messageContent.getElementJson();
						}
						else if(this.messageContent.isXml()) {
							this.element = this.messageContent.getElement();
							this.dnc = new DynamicNamespaceContext();
							this.dnc.findPrefixNamespace(this.element);
						}
						else if(this.messageContent.isRestMultipart()) {
							this.elementJson = this.messageContent.getElementJson();
							if(this.elementJson==null) {
								this.element = this.messageContent.getElement();
								if(this.element!=null) {
									this.dnc = new DynamicNamespaceContext();
									this.dnc.findPrefixNamespace(this.element);
								}
								else {
									throw new Exception("Invalid multipart content");
								}
							}
						}
					}catch(Exception e) {
						throw new DynamicException(e.getMessage(),e);
					}
				}
				this.messageContent_initialized = true;
			}
		}finally {
			this.semaphore.release("init");
		}
	}
	
	
	public void refreshContent() {
		if(this.element!=null && this.refresh==null) {
			this._refreshContent();
		}
	}
	private void _refreshContent() {
		this.semaphore.acquireThrowRuntime("refreshContent");
		// effettuo il refresh, altrimenti le regole xpath applicate sulla richiesta, nel flusso di risposta (es. header http della risposta) non funzionano.
		try {
			this.refresh = true;
			if(this.element!=null) {
				XMLUtils xmlUtils = XMLUtils.getInstance(this.messageFactory);
				this.element = xmlUtils.newElement(xmlUtils.toByteArray(this.element));
			}
		}catch(Exception e){
			this.log.error("Refresh fallito: "+e.getMessage(),e);
		}finally {
			this.semaphore.release("refreshContent");
		}
	}
	
	public boolean match(String pattern) throws DynamicException {
		init();
		
		String v = read(pattern);
		return v!=null && !"".equals(v);
	}
	
	public String read(String pattern) throws DynamicException {
		init();
		
		String operazione = "Read (pattern: "+pattern+")";
		
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
			this.log.debug(operazione+" failed, results not found: "+e.getMessage(),e);
		}
		catch(XPathNotValidException e){
			throw new DynamicException(e.getMessage(),e);
		}
		catch(JsonPathNotFoundException e){
			this.log.debug(operazione+" failed, results not found: "+e.getMessage(),e);
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
					this.log.debug("["+index+"] "+operazione+" failed: "+t.getMessage(),t);
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
					throw new DynamicException(operazione+" failed: "+e.getMessage(),e);
				}
			}
		}
		catch(Exception e){
			throw new DynamicException(operazione+" failed: "+e.getMessage(),e);
		}
		return valore;
	}
	
	public List<String> readList(String pattern) throws DynamicException {
		init();
		
		String operazione = "ReadList (pattern: "+pattern+")";
		
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
			this.log.debug(operazione+" failed, results not found: "+e.getMessage(),e);
		}
		catch(XPathNotValidException e){
			throw new DynamicException(e.getMessage(),e);
		}
		catch(JsonPathNotFoundException e){
			this.log.debug(operazione+" failed, results not found: "+e.getMessage(),e);
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
					this.log.debug("["+index+"] "+operazione+" failed: "+t.getMessage(),t);
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
					throw new DynamicException(operazione+" failed: "+e.getMessage(),e);
				}
			}
		}
		catch(Exception e){
			throw new DynamicException(operazione+" failed: "+e.getMessage(),e);
		}
		return valore;
	}
	
	// Lascio per utilizzi in vecchie trasformazioni
	@Deprecated
	public void remove(String pattern) throws DynamicException {
		this._remove(pattern, null);
	}
	public void removeByXPath(String pattern) throws DynamicException {
		if(pattern==null || StringUtils.isEmpty(pattern)) {
			throw new DynamicException("XPath pattern undefined");
		}
		this._remove(pattern, null);
	}
	public void removeJsonElement(String name) throws DynamicException {
		if(name==null || StringUtils.isEmpty(name)) {
			throw new DynamicException("JsonElement name undefined");
		}
		this._remove(null, name);
	}
	public void removeByJsonPath(String pattern, String name) throws DynamicException {
		if(pattern==null || StringUtils.isEmpty(pattern)) {
			throw new DynamicException("JsonPath pattern undefined");
		}
		if(name==null || StringUtils.isEmpty(name)) {
			throw new DynamicException("JsonElement name undefined");
		}
		this._remove(pattern, name);
	}
	private void _remove(String pattern, String name) throws DynamicException {
		init();
		
		String opName = "";
		if(name!=null) {
			opName="name:"+name+"";
		}
		String opPattern = "";
		if(pattern!=null) {
			opPattern="pattern:"+pattern+"";
			if(name!=null) {
				opPattern+=" ";
			}
		}
		String operazione = "Remove ("+opPattern+""+opName+")";
		
		try {
			this.messageContent.setUpdatable();
			
			if(this.element!=null) {
				AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(this.messageFactory);
				Node n = (Node) xPathEngine.getMatchPattern(this.element, this.dnc, pattern, XPathReturnType.NODE);
				if(n.getParentNode()!=null) {
					n.getParentNode().removeChild(n);	
				}
				else {
					this.element.removeChild(n);
				}
			}
			else {
				if(this.messageContent!=null && this.messageContent.isJson() && this.messageContent.getJsonMessage()!=null) {
					OpenSPCoop2RestJsonMessage json = this.messageContent.getJsonMessage();
					if(pattern!=null) {
						json.removeElement(pattern, name);
					}
					else {
						json.removeElement(name);
					}
				}
				else {
					// retrocompatibilita'
					String s = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(this.elementJson, pattern, this.log);
					this.elementJson = this.elementJson.replace(s, "");
				}
			}
		}
		catch(XPathNotFoundException e){
			this.log.debug(operazione+" failed, results not found: "+e.getMessage(),e);
		}
		catch(XPathNotValidException e){
			throw new DynamicException(e.getMessage(),e);
		}
		catch(JsonPathNotFoundException e){
			this.log.debug(operazione+" failed, results not found: "+e.getMessage(),e);
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
					this.log.debug("["+index+"] "+operazione+" failed: "+t.getMessage(),t);
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
					throw new DynamicException(operazione+" failed: "+e.getMessage(),e);
				}
			}
		}
		catch(Exception e){
			throw new DynamicException(operazione+" failed: "+e.getMessage(),e);
		}
	}
	
	// Lascio per utilizzi in vecchie trasformazioni
	@Deprecated
	public void removeList(String pattern) throws DynamicException {
		this._removeList(pattern);
	}
	public void removeListByXPath(String pattern) throws DynamicException {
		if(pattern==null || StringUtils.isEmpty(pattern)) {
			throw new DynamicException("XPath pattern undefined");
		}
		this._remove(pattern, null);
	}
	private void _removeList(String pattern) throws DynamicException {
		init();
		
		String operazione = "RemoveList (pattern: "+pattern+")";
		
		try {
			this.messageContent.setUpdatable();
			
			if(this.element!=null) {
				AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(this.messageFactory);
				NodeList nList = (NodeList) xPathEngine.getMatchPattern(this.element, this.dnc, pattern, XPathReturnType.NODESET);
				if(nList!=null && nList.getLength()>0) {
					for (int i = 0; i < nList.getLength(); i++) {
						Node n = nList.item(i);
						if(n.getParentNode()!=null) {
							n.getParentNode().removeChild(n);	
						}
						else {
							this.element.removeChild(n);
						}			
					}
				}
			}
			else {
				// Soluzione deprecata: usare removeByJsonPath
				List<String> valori = JsonXmlPathExpressionEngine.extractAndConvertResultAsList(this.elementJson, pattern, this.log);
				if(valori!=null && !valori.isEmpty()) {
					for (String valore : valori) {
						this.elementJson = this.elementJson.replace(valore, "");			
					}
				}
			}
		}
		catch(XPathNotFoundException e){
			this.log.debug(operazione+" failed, results not found: "+e.getMessage(),e);
		}
		catch(XPathNotValidException e){
			throw new DynamicException(e.getMessage(),e);
		}
		catch(JsonPathNotFoundException e){
			this.log.debug(operazione+" failed, results not found: "+e.getMessage(),e);
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
					this.log.debug("["+index+"] "+operazione+" failed: "+t.getMessage(),t);
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
					throw new DynamicException(operazione+" failed: "+e.getMessage(),e);
				}
			}
		}
		catch(Exception e){
			throw new DynamicException(operazione+" failed: "+e.getMessage(),e);
		}
	}
	
	
	
	public void replaceValueByXPath(String pattern, String newValue) throws DynamicException {
		if(pattern==null || StringUtils.isEmpty(pattern)) {
			throw new DynamicException("XPath pattern undefined");
		}
		if(newValue==null) {
			throw new DynamicException("New value undefined");
		}
		this._replaceValue(pattern, null, newValue);
	}
	public void replaceJsonElementValue(String name, Object newValue) throws DynamicException {
		if(name==null || StringUtils.isEmpty(name)) {
			throw new DynamicException("JsonElement name undefined");
		}
		if(newValue==null) {
			throw new DynamicException("New value undefined");
		}
		this._replaceValue(null, name, newValue);
	}
	public void replaceValueByJsonPath(String pattern, String name, Object newValue) throws DynamicException {
		if(pattern==null || StringUtils.isEmpty(pattern)) {
			throw new DynamicException("JsonPath pattern undefined");
		}
		if(name==null || StringUtils.isEmpty(name)) {
			throw new DynamicException("JsonElement name undefined");
		}
		if(newValue==null) {
			throw new DynamicException("New value undefined");
		}
		this._replaceValue(pattern, name, newValue);
	}
	private void _replaceValue(String pattern, String name, Object value) throws DynamicException {
		init();
		
		String opName = "";
		if(name!=null) {
			opName=" name:"+name+"";
		}
		String operazione = "Replace value (pattern: "+pattern+""+opName+")";
		
		try {
			this.messageContent.setUpdatable();
			
			if(this.element!=null) {
				AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(this.messageFactory);
				Node n = (Node) xPathEngine.getMatchPattern(this.element, this.dnc, pattern, XPathReturnType.NODE);
				n.setTextContent(value.toString());
			}
			else {
				if(this.messageContent!=null && this.messageContent.isJson() && this.messageContent.getJsonMessage()!=null) {
					OpenSPCoop2RestJsonMessage json = this.messageContent.getJsonMessage();
					if(pattern!=null) {
						json.replaceValue(pattern, name, value);
					}
					else {
						json.replaceValue(name, value);
					}
				}
			}
		}
		catch(XPathNotFoundException e){
			this.log.debug(operazione+" failed, results not found: "+e.getMessage(),e);
		}
		catch(XPathNotValidException e){
			throw new DynamicException(e.getMessage(),e);
		}
		catch(Exception e){
			throw new DynamicException(operazione+" failed: "+e.getMessage(),e);
		}
	}
	
	
	public void replaceValuesByXPath(String pattern, String newValue) throws DynamicException {
		if(pattern==null || StringUtils.isEmpty(pattern)) {
			throw new DynamicException("XPath pattern undefined");
		}
		if(newValue==null) {
			throw new DynamicException("New value undefined");
		}
		this._replaceValues(pattern, null, newValue);
	}
	private void _replaceValues(String pattern, String name, String value) throws DynamicException {
		init();
		
		String opName = "";
		if(name!=null) {
			opName=" name:"+name+"";
		}
		String operazione = "Replace values (pattern: "+pattern+""+opName+")";
		
		try {
			this.messageContent.setUpdatable();
			
			if(this.element!=null) {
				AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(this.messageFactory);
				NodeList nList = (NodeList) xPathEngine.getMatchPattern(this.element, this.dnc, pattern, XPathReturnType.NODESET);
				if(nList!=null && nList.getLength()>0) {
					for (int i = 0; i < nList.getLength(); i++) {
						Node n = nList.item(i);
						n.setTextContent(value);		
					}
				}
			}
			else {
				throw new Exception("Unsupported in json structure");
			}
		}
		catch(XPathNotFoundException e){
			this.log.debug(operazione+" failed, results not found: "+e.getMessage(),e);
		}
		catch(XPathNotValidException e){
			throw new DynamicException(e.getMessage(),e);
		}
		catch(JsonPathNotFoundException e){
			this.log.debug(operazione+" failed, results not found: "+e.getMessage(),e);
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
					this.log.debug("["+index+"] "+operazione+" failed: "+t.getMessage(),t);
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
					throw new DynamicException(operazione+" failed: "+e.getMessage(),e);
				}
			}
		}
		catch(Exception e){
			throw new DynamicException(operazione+" failed: "+e.getMessage(),e);
		}
	}
}
