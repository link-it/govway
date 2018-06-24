/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.message.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;

/**
 * ContextUrlCollection
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContextUrlCollection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Vengono utilizzate due liste per preservare l'ordine di inserimento che si perde in una hashtable,
	private List<String> map_contextUrl = new ArrayList<String>();
	private List<MessageType> map_messageProcessor = new ArrayList<MessageType>();
	private List<List<String>> map_contentTypesRestriction = new ArrayList<List<String>>();

	private SoapBinding soapBinding;
	private RestBinding restBinding;
	
	public ContextUrlCollection(SoapBinding soapBinding, RestBinding restBinding){
		this.soapBinding = soapBinding;
		this.restBinding = restBinding;
	}
	
	public int sizeContextUrl(){
		return this.map_contextUrl.size();
	}
	
	private void checkVersion(MessageType version) throws MessageException{
		if(MessageType.XML.equals(version)){
			if(this.restBinding==null){
				throw new MessageException("Rest disabled");
			}
			if(!this.restBinding.isBinding_xml()){
				throw new MessageException("MessageType ["+version+"] not supported in RestBinding; Xml disabled");
			}
		}
		else if(MessageType.JSON.equals(version)){
			if(this.restBinding==null){
				throw new MessageException("Rest disabled");
			}
			if(!this.restBinding.isBinding_json()){
				throw new MessageException("MessageType ["+version+"] not supported in RestBinding; Json disabled");
			}
		}
		else if(MessageType.BINARY.equals(version)){
			if(this.restBinding==null){
				throw new MessageException("Rest disabled");
			}
			if(!this.restBinding.isBinding_binary()){
				throw new MessageException("MessageType ["+version+"] not supported in RestBinding; Binary disabled");
			}
		}
		else if(MessageType.MIME_MULTIPART.equals(version)){
			if(this.restBinding==null){
				throw new MessageException("Rest disabled");
			}
			if(!this.restBinding.isBinding_mimeMultipart()){
				throw new MessageException("MessageType ["+version+"] not supported in RestBinding; MimeMultipart disabled");
			}
		}
		else if(MessageType.SOAP_11.equals(version)){
			if(this.soapBinding==null){
				throw new MessageException("Soap disabled");
			}
			if(!this.soapBinding.isBinding_soap11()){
				throw new MessageException("MessageType ["+version+"] not supported in SoapBinding; Soap11 disabled");
			}
		}
		else if(MessageType.SOAP_12.equals(version)){
			if(this.soapBinding==null){
				throw new MessageException("Soap disabled");
			}
			if(!this.soapBinding.isBinding_soap12()){
				throw new MessageException("MessageType ["+version+"] not supported in SoapBinding; Soap12 disabled");
			}
		}
		else{
			throw new MessageException("MessageType ["+version+"] not supported in Binding");
		}
	}
	
	public void addContext(String protocol, String function, String functionParam,MessageType version,String ... contentTypesRestriction) throws MessageException{
		String context = ServiceBindingConfiguration.normalizeContext(protocol);
		String f = ServiceBindingConfiguration.normalizeContext(function);
		String subContext_functionParam = ServiceBindingConfiguration.normalizeContext(functionParam);
		
		String key = context +" - " + f +" - " +subContext_functionParam;
		
		if(version==null){
			throw new MessageException("MessageProcessorVersion not defined");
		}
		this.checkVersion(version);
		if(this.map_contextUrl.contains(key)){
			throw new MessageException("ContextUrl already defined for MessageType "+this.getMessageType(protocol,function,functionParam));
		}
		this.map_contextUrl.add(key);
		this.map_messageProcessor.add(version);
		
		List<String> contentTypesRestrictionList = new ArrayList<String>();
		if(contentTypesRestriction!=null){
			for (int i = 0; i < contentTypesRestriction.length; i++) {
				contentTypesRestrictionList.add(contentTypesRestriction[i]);
			}
		}
		this.map_contentTypesRestriction.add(contentTypesRestrictionList);
	}
	
	public MessageType getMessageType(String protocol, String function, String functionParam) throws MessageException{
		String context = ServiceBindingConfiguration.normalizeContext(protocol);
		String f = ServiceBindingConfiguration.normalizeContext(function);
		String subContext_functionParam = ServiceBindingConfiguration.normalizeContext(functionParam);
		
		String key = context +" - " + f +" - " +subContext_functionParam;
		
		for (int i = 0; i < this.map_contextUrl.size(); i++) {
			if(this.map_contextUrl.get(i).equals(key)){
				return this.map_messageProcessor.get(i);
			}
		}	
		
		// provo a cercarlo senza utilizzare la funzione specifica
		if(function!=null && !"".equals(function)){
			function = null;
			f = ServiceBindingConfiguration.normalizeContext(function);
			key = context +" - " + f +" - " +subContext_functionParam;
			
			for (int i = 0; i < this.map_contextUrl.size(); i++) {
				if(this.map_contextUrl.get(i).equals(key)){
					return this.map_messageProcessor.get(i);
				}
			}	
		}
		
		return null; // ritorno anzi null per gestire la differenza rispetto all'eccezione sopra
	}
	
	public List<String> getContentTypesRestriction(String protocol, String function, String functionParam) {
		String context = ServiceBindingConfiguration.normalizeContext(protocol);
		String f = ServiceBindingConfiguration.normalizeContext(function);
		String subContext_functionParam = ServiceBindingConfiguration.normalizeContext(functionParam);
		
		String key = context +" - " + f +" - " +subContext_functionParam;
		
		for (int i = 0; i < this.map_contextUrl.size(); i++) {
			if(this.map_contextUrl.get(i).equals(key)){
				return this.map_contentTypesRestriction.get(i);
			}
		}
		
		// provo a cercarlo senza utilizzare la funzione specifica
		if(function!=null && !"".equals(function)){
			function = null;
			f = ServiceBindingConfiguration.normalizeContext(function);
			key = context +" - " + f +" - " +subContext_functionParam;
			
			for (int i = 0; i < this.map_contextUrl.size(); i++) {
				if(this.map_contextUrl.get(i).equals(key)){
					return this.map_contentTypesRestriction.get(i);
				}
			}	
		}
		
		return null;
	}
}
