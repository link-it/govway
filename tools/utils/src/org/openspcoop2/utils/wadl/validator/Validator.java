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

package org.openspcoop2.utils.wadl.validator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.jvnet.ws.wadl.Param;
import org.jvnet.ws.wadl.ParamStyle;
import org.jvnet.ws.wadl.ast.AbstractNode;
import org.jvnet.ws.wadl.ast.FaultNode;
import org.jvnet.ws.wadl.ast.MethodNode;
import org.jvnet.ws.wadl.ast.RepresentationNode;
import org.jvnet.ws.wadl.ast.ResourceNode;
import org.openspcoop2.utils.wadl.ApplicationWrapper;
import org.openspcoop2.utils.wadl.WADLException;
import org.openspcoop2.utils.wadl.WADLUtilities;
import org.openspcoop2.utils.wadl.entity.HttpBaseEntity;
import org.openspcoop2.utils.wadl.entity.HttpBaseRequestEntity;
import org.openspcoop2.utils.wadl.entity.HttpBaseResponseEntity;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.ValidatoreXSD;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Validator
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Validator {

	private ApplicationWrapper application;
	private Logger log;
	private AbstractXMLUtils xmlUtils;
	private Schema schema;
		
	public Validator(Logger log,ApplicationWrapper application) throws WADLException{
		this(log, application, XMLUtils.getInstance());
	}
	public Validator(Logger log,ApplicationWrapper application,AbstractXMLUtils xmlUtils) throws WADLException{
		
		try{
		
			this.log = log;
			this.application = application;
			this.xmlUtils = xmlUtils;
			
			// Costruisco l'eventuale schema XSD necessario per una validazione rispetto a schemi XSD
			if(this.application.getResources().size()>0){
				this.schema = this.application.buildSchemaCollection(this.log).buildSchema(this.log);
			}
			
		}catch(Exception e){
			throw new WADLException(e.getMessage(),e);
		}
	}
	
	
	public void validate(HttpBaseEntity<?> httpEntity) throws WADLException,WADLValidatorException{
		
		// Recupera il resource node corrispondente alla url
		ResourceNode resourceNode = WADLUtilities.findResourceNode(this.application.getApplicationNode(),httpEntity.getUrl());
		
		// Recupera il metodo corrispondente al method invocato
		MethodNode methodNode = WADLUtilities.findMethodNode(resourceNode,httpEntity.getMethod());
		
		// Validazione XSD
		validateAgainstXSDSchema(httpEntity, resourceNode, methodNode);
		
		// Validazione di conformance con quando definito nel WADL
		wadlConformanceCheck(httpEntity, resourceNode, methodNode);
		
	}
	
	private void validateAgainstXSDSchema(HttpBaseEntity<?> httpEntity,ResourceNode resourceNode,MethodNode methodNode) throws WADLException,WADLValidatorException{

		if(this.schema!=null){
		
			try{
			
				AbstractValidatoreXSD validatore = null;
				try{
					validatore = new ValidatoreXSD(this.schema);
	
				}catch(Exception e){
					throw new WADLException("Riscontrato errore durante la costruzione del validatore XSD per il contenuto applicativo: "+e.getMessage(),e);
				}
				
				javax.xml.namespace.QName elementAtteso = null;
				
				AbstractNode nodeXML = getNode(httpEntity, methodNode);
				
				if(nodeXML != null) {
					if(nodeXML instanceof RepresentationNode) {
						elementAtteso = ((RepresentationNode)nodeXML).getElement();
					} else if(nodeXML instanceof FaultNode) {
						elementAtteso = ((FaultNode)nodeXML).getElement();
					}  
				}
				
				if(elementAtteso!=null){
					// Devo effettivamente effettuare la validazione XSD
					
					Node node = this.readNode(elementAtteso, httpEntity);
					
					String nomeElemento = null;
					try{
						nomeElemento = node.getLocalName();
						validatore.valida(node,true);
					}catch(Exception e){
						StringBuffer errorMsgValidazioneXSD = new StringBuffer();
						errorMsgValidazioneXSD.append("validazione fallita");
						errorMsgValidazioneXSD.append(" (elemento "+nomeElemento+"): "+e.getMessage());
						String elementNonValidato = null;
						try{
							elementNonValidato = this.xmlUtils.toString(node);
						}catch(Exception eString){
							this.log.error("Errore durante la conversione del Node in String: "+eString.getMessage(),eString);
						}
						this.log.error("Validazione fallita (elemento "+nomeElemento+") ["+elementNonValidato+"]: "+e.getMessage(),e);
						throw new WADLValidatorException(errorMsgValidazioneXSD.toString(),e);
					}
	
				}
			}catch(WADLException e){
				throw e;
			}catch(WADLValidatorException e){
				throw e;
			}catch(Exception e){
				throw new WADLException(e.getMessage(),e);
			}
		}
		
	}
	
	private AbstractNode getNode(HttpBaseEntity<?> httpEntity, MethodNode methodNode)throws WADLException,WADLValidatorException{
		// Navigare il methodNode secondo la seguente specifica:
		// Se siamo nella richiesta (httpEntity instanceof HttpRequestEntity)
		//    verificare se esiste un input con mediaType 'application/xml (o text/xml)'
		//    se esiste recuperare l'elementNameAtteso
		// else se siamo nella risposta (httpEntity instanceof HttpResponseEntity)
		//    verificare se esiste un output o un fault con mediaType 'application/xml (o text/xml)' e status uguale a quello presente nella risposta http
		//    se esiste recuperare l'elementNameAtteso

		if(httpEntity instanceof HttpBaseRequestEntity<?>) {
			if(methodNode.getSupportedInputs() != null) {
				for(RepresentationNode input: methodNode.getSupportedInputs()) {
					if(input.getMediaType().equals("application/xml") || input.getMediaType().equals("text/xml")) {
						return input;
					}
				}
			}
		} else if(httpEntity instanceof HttpBaseResponseEntity<?>) {
			int status = ((HttpBaseResponseEntity<?>)httpEntity).getStatus();
			
			List<RepresentationNode> lstOutputs = getSupportedOutputs(methodNode.getSupportedOutputs(), status);

			if(lstOutputs != null) {
				for(RepresentationNode output: lstOutputs) {
					if(output.getMediaType().equals("application/xml") || output.getMediaType().equals("text/xml")) {
						return output;
					} 
				}
			}

			List<FaultNode> lstFaults = getSupportedFaults(methodNode.getFaults(), status);
			if(lstFaults != null) {
				for(FaultNode fault: lstFaults) {
					if(fault.getMediaType().equals("application/xml") || fault.getMediaType().equals("text/xml")) {
						return fault;
					} 
				}
			}
			
			lstOutputs = methodNode.getSupportedOutputs().get(new ArrayList<Long>());

			if(lstOutputs != null) {
				for(RepresentationNode output: lstOutputs) {
					if(output.getMediaType().equals("application/xml") || output.getMediaType().equals("text/xml")) {
						return output;
					} 
				}
			}

			
		}
		
		return null;

	}
	
	private List<RepresentationNode> getSupportedOutputs(MultivaluedMap<List<Long>, RepresentationNode> outputs,
			int status) {
		for(List<Long> lst: outputs.keySet()) {
			for(Long longValue: lst) {
				if(longValue.intValue() == status)
					return outputs.get(lst);
			}
		}
		return null;
	}
	
	private List<FaultNode> getSupportedFaults(MultivaluedMap<List<Long>, FaultNode> faults,
			int status) {
		for(List<Long> lst: faults.keySet()) {
			for(Long longValue: lst) {
				if(longValue.intValue() == status)
					return faults.get(lst);
			}
		}
		return null;
	}
	
	private void wadlConformanceCheck(HttpBaseEntity<?> httpEntity,ResourceNode resourceNode,MethodNode methodNode) throws WADLException,WADLValidatorException{

		try{
			
			javax.xml.namespace.QName elementAtteso = null;
			List<Param> paramsAttesi = methodNode.getRequiredParameters();
			// ... altri vedere un po ....

			AbstractNode nodeXML = getNode(httpEntity, methodNode);

			if(nodeXML != null) {
				if(nodeXML instanceof RepresentationNode) {
					RepresentationNode representationNode = (RepresentationNode)nodeXML;
					elementAtteso = representationNode.getElement();
				} else if(nodeXML instanceof FaultNode) {
					FaultNode faultNode = (FaultNode)nodeXML;
					elementAtteso = faultNode.getElement();
				}  
			}
			

			// Navigare il methodNode secondo la seguente specifica:
			// Se siamo nella richiesta (httpEntity instanceof HttpRequestEntity)
			//    verificare se esiste un input con mediaType 'application/xml (o text/xml)'
			//    se esiste recuperare gli elementi attesi
			// else se siamo nella risposta (httpEntity instanceof HttpResponseEntity)
			//    verificare se esiste un output o un fault con mediaType 'application/xml (o text/xml)' e status uguale a quello presente nella risposta http
			//    se esiste recuperare gli elementi attesi
			if(httpEntity.getContentType() != null) {
				boolean contentTypeSupported = false;
				if(httpEntity instanceof HttpBaseRequestEntity<?>) {
					if(methodNode.getSupportedInputs() != null) {
						for(RepresentationNode input: methodNode.getSupportedInputs()) {
							if(input.getMediaType().equals(httpEntity.getContentType())) {
								contentTypeSupported = true;
							} 
						}
					}
				} else if(httpEntity instanceof HttpBaseResponseEntity<?>) {
					int status = ((HttpBaseResponseEntity<?>)httpEntity).getStatus();
					
					List<RepresentationNode> lstOutputs = getSupportedOutputs(methodNode.getSupportedOutputs(), status);
					if(lstOutputs != null) {
						for(RepresentationNode output: lstOutputs) {
							if(output.getMediaType().equals(httpEntity.getContentType())) {
								contentTypeSupported = true;
							} 
						}
					}
	
					List<FaultNode> lstFaults = getSupportedFaults(methodNode.getFaults(),status);
					if(lstFaults != null) {
						for(FaultNode fault: lstFaults) {
							if(fault.getMediaType().equals(httpEntity.getContentType())) {
								contentTypeSupported = true;
							} 
						}
					}
					
					lstOutputs = methodNode.getSupportedOutputs().get(new ArrayList<Long>());

					if(lstOutputs != null) {
						for(RepresentationNode output: lstOutputs) {
							if(output.getMediaType().equals(httpEntity.getContentType())) {
								contentTypeSupported = true;
							} 
						}
					}
				}
				
				if(!contentTypeSupported) {
					throw new WADLValidatorException("Verifica content type ["+httpEntity.getContentType()+"] supportato fallita.");
				}
			}
			
			if(elementAtteso!=null){
				
				// Verifica rootNode
				Node node = this.readNode(elementAtteso, httpEntity);
				if(node.getLocalName()==null){
					throw new WADLValidatorException("Verifica presenza element ["+elementAtteso+"] fallita. Element presente nel payload http non contiene un local-name?");
				}
				if(node.getNamespaceURI()==null){
					throw new WADLValidatorException("Verifica presenza element ["+elementAtteso+"] fallita. Element presente nel payload http non contiene un namespace?");
				}
				if(node.getLocalName().equals(elementAtteso.getLocalPart())==false){
					throw new WADLValidatorException("Verifica presenza element ["+elementAtteso+"] fallita. Element presente nel payload http contiene un local-name ["+node.getLocalName()+"] differente da quello atteso ["+elementAtteso.getLocalPart()+"]");
				}
				if(node.getNamespaceURI().equals(elementAtteso.getNamespaceURI())==false){
					throw new WADLValidatorException("Verifica presenza element ["+elementAtteso+"] fallita. Element presente nel payload http contiene un namespace ["+node.getNamespaceURI()+"] differente da quello atteso ["+elementAtteso.getNamespaceURI()+"]");
				}
			}
			
			if(httpEntity instanceof HttpBaseRequestEntity<?>) {
			
				HttpBaseRequestEntity<?> request = (HttpBaseRequestEntity<?>) httpEntity;
				
				if(paramsAttesi != null && !paramsAttesi.isEmpty()) {
					for(Param param: paramsAttesi) {
						if(param.getStyle().equals(ParamStyle.QUERY)) {
							if(request.getParametersFormBased() == null || !request.getParametersFormBased().containsKey(param.getName())) {
								throw new WADLValidatorException("Verifica presenza header formBased ["+param.getName()+"] fallita.");
							}
						} else if(param.getStyle().equals(ParamStyle.HEADER)) {
							if(request.getParametersTrasporto() == null || !request.getParametersTrasporto().containsKey(param.getName())) {
								throw new WADLValidatorException("Verifica presenza header trasporto ["+param.getName()+"] fallita.");
							}
						}
					}
				}
			}

		}catch(WADLException e){
			throw e;
		}catch(WADLValidatorException e){
			throw e;
		}catch(Exception e){
			throw new WADLException(e.getMessage(),e);
		}
		
	}

	private Node readNode(javax.xml.namespace.QName elementAtteso, HttpBaseEntity<?> httpEntity) throws WADLValidatorException, WADLException{
		Node node = null;
		try{
			if(httpEntity.getContent()==null){
				throw new WADLValidatorException("Verifica presenza element ["+elementAtteso+"] fallita. Non è stato riscontrato alcun dato nel payload http");
			}
			Object content = httpEntity.getContent();
			
			if(content instanceof Document){
				node = ((Document) content).getDocumentElement();
			}
			else if(content instanceof byte[]){
				byte [] bytes = (byte[]) content;
				node = this.xmlUtils.newDocument(bytes);
			}
			else if(content instanceof String){
				byte [] bytes = ((String)content).getBytes();
				node = this.xmlUtils.newDocument(bytes);
			}
			else if(content instanceof InputStream){
				InputStream is = (InputStream) content;
				node = this.xmlUtils.newDocument(is);
			}
			else{
				throw new WADLException("HttpBaseEntity ["+httpEntity.getClass().getName()+"] non gestita");
			}
			
			return node;
			
			
		}catch(WADLException e){
			throw e;
		}catch(WADLValidatorException e){
			throw e;
		}catch(Exception e){
			throw new WADLValidatorException("Verifica presenza element ["+elementAtteso+"] fallita. Non è stato riscontrato nel payload http dei dati che contengano una struttura xml valida, "+e.getMessage(),e);
		}
	}

}
