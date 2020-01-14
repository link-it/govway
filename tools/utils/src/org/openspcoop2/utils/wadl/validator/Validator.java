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

package org.openspcoop2.utils.wadl.validator;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.validation.Schema;

import org.jvnet.ws.wadl.ast.AbstractNode;
import org.jvnet.ws.wadl.ast.FaultNode;
import org.jvnet.ws.wadl.ast.MethodNode;
import org.jvnet.ws.wadl.ast.RepresentationNode;
import org.jvnet.ws.wadl.ast.ResourceNode;
import org.openspcoop2.utils.rest.AbstractApiValidator;
import org.openspcoop2.utils.rest.ApiValidatorConfig;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.ValidatorException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.api.ApiSchemaTypeRestriction;
import org.openspcoop2.utils.rest.entity.HttpBaseEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseRequestEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseResponseEntity;
import org.openspcoop2.utils.wadl.ApplicationWrapper;
import org.openspcoop2.utils.wadl.WADLApi;
import org.openspcoop2.utils.wadl.WADLException;
import org.openspcoop2.utils.wadl.WADLUtilities;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.ValidatoreXSD;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Validator
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Validator extends AbstractApiValidator implements IApiValidator {

	private WADLApi wadlApi;
	private ApplicationWrapper application;
	private Logger log;
	private AbstractXMLUtils xmlUtils;
	private Schema schema;
	private boolean initialize = false;
		
	
	@Override
	public void init(Logger log, Api api, ApiValidatorConfig config) throws WADLException {
		try{
			this.wadlApi = (WADLApi) api;
			this.log = log;
			this.application = this.wadlApi.getApplicationWadlWrapper();
			this.xmlUtils = config.getXmlUtils();
			
			// Costruisco l'eventuale schema XSD necessario per una validazione rispetto a schemi XSD
			if(this.application.getResources().size()>0){
				this.schema = this.application.buildSchemaCollection(this.log).buildSchema(this.log);
			}
			
			this.initialize = true;
		}catch(Exception e){
			throw new WADLException(e.getMessage(),e);
		}
	}
	
	public Validator(){}
	
	@Override
	public void close(Logger log, Api api, ApiValidatorConfig config) throws ProcessingException{
		
	}
	
	@Override
	public void validate(HttpBaseEntity<?> httpEntity) throws ProcessingException, ValidatorException{
		
		if(!this.initialize){
			throw new WADLException("Validatore non inizializzato");
		}

		List<Object> context = new ArrayList<>();
		
		this.validate(this.wadlApi, httpEntity, context);

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void validatePreConformanceCheck(HttpBaseEntity<?> httpEntity,ApiOperation operation,Object ... args) throws ProcessingException,ValidatorException{
		
		// Effettuo la validazione del contenuto
		
		// Recupera il resource node corrispondente alla url
		ResourceNode resourceNode = WADLUtilities.findResourceNode(this.application.getApplicationNode(),httpEntity.getUrl());
		
		// Recupera il metodo corrispondente al method invocato
		MethodNode methodNode = WADLUtilities.findMethodNode(resourceNode,httpEntity.getMethod());
		
		// Aggiungo in context
		((List<Object>)args[0]).add(resourceNode);
		((List<Object>)args[0]).add(methodNode);
		
		this.validateAgainstXSDSchema(httpEntity, resourceNode, methodNode);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void validatePostConformanceCheck(HttpBaseEntity<?> httpEntity,ApiOperation operation,Object ... args) throws ProcessingException,ValidatorException{
		
		// Verifico che un eventuale contenuto che rispetta lo schema, sia esattamente anche quello atteso per l'operazione
		
		ResourceNode resourceNode = (ResourceNode) ((List<Object>)args[0]).get(0);
		MethodNode methodNode = (MethodNode) ((List<Object>)args[0]).get(1);
		
		this.wadlConformanceCheck(httpEntity, resourceNode, methodNode);
	}
	
	@Override
	public void validateValueAsType(String value,String type, ApiSchemaTypeRestriction typeRestriction) throws ProcessingException,ValidatorException{
		
		// Tipi XSD : {http://www.w3.org/2001/XMLSchema}string
		if(type.startsWith("{http://www.w3.org/2001/XMLSchema}")){
			String tipoEffettivo = type.substring("{http://www.w3.org/2001/XMLSchema}".length()); 
			if(tipoEffettivo!=null){
				tipoEffettivo = tipoEffettivo.trim();
				
				if("byte".equalsIgnoreCase(tipoEffettivo) || "unsignedByte".equalsIgnoreCase(tipoEffettivo)){
					try{
						Byte.parseByte(value);
					}catch(Exception e){
						throw new ValidatorException(e.getMessage(),e);
					}
				}
				else if("char".equalsIgnoreCase(tipoEffettivo)){
					if(value.length()>1){
						throw new ValidatorException("More than one character");
					}
				}
				else if("double".equalsIgnoreCase(tipoEffettivo) || "decimal".equalsIgnoreCase(tipoEffettivo)){
					try{
						Double.parseDouble(value);
					}catch(Exception e){
						throw new ValidatorException(e.getMessage(),e);
					}
				}
				else if("float".equalsIgnoreCase(tipoEffettivo)){
					try{
						Float.parseFloat(value);
					}catch(Exception e){
						throw new ValidatorException(e.getMessage(),e);
					}
				}
				else if("int".equalsIgnoreCase(tipoEffettivo) || "integer".equalsIgnoreCase(tipoEffettivo) || 
						"positiveInteger".equalsIgnoreCase(tipoEffettivo) || "negativeInteger".equalsIgnoreCase(tipoEffettivo) ||
						"nonPositiveInteger".equalsIgnoreCase(tipoEffettivo) || "nonNegativeInteger".equalsIgnoreCase(tipoEffettivo) || 
						"unsignedInt".equalsIgnoreCase(tipoEffettivo)){
					try{
						int i = Integer.parseInt(value);
						if("positiveInteger".equalsIgnoreCase(tipoEffettivo)){
							if(i<=0){
								throw new ValidatorException("Expected a positive value");
							}
						}
						else if("nonNegativeInteger".equalsIgnoreCase(tipoEffettivo)){
							if(i<0){
								throw new ValidatorException("Expected a non negative value");
							}
						}
						else if("negativeInteger".equalsIgnoreCase(tipoEffettivo)){
							if(i>=0){
								throw new ValidatorException("Expected a negative value");
							}
						}
						else if("nonPositiveInteger".equalsIgnoreCase(tipoEffettivo)){
							if(i>0){
								throw new ValidatorException("Expected a non positive value");
							}
						}
						else if("unsignedInt".equalsIgnoreCase(tipoEffettivo)){
							if(i<0){
								throw new ValidatorException("Expected a unsigned value");
							}
						}
					}catch(Exception e){
						throw new ValidatorException(e.getMessage(),e);
					}
				}
				else if("long".equalsIgnoreCase(tipoEffettivo) || "unsignedLong".equalsIgnoreCase(tipoEffettivo)){
					try{
						long l = Long.parseLong(value);
						if("unsignedLong".equalsIgnoreCase(tipoEffettivo)){
							if(l<0){
								throw new ValidatorException("Expected a unsigned value");
							}
						}
					}catch(Exception e){
						throw new ValidatorException(e.getMessage(),e);
					}
				}
				else if("short".equalsIgnoreCase(tipoEffettivo) || "unsignedShort".equalsIgnoreCase(tipoEffettivo)){
					try{
						short s = Short.parseShort(value);
						if("unsignedShort".equalsIgnoreCase(tipoEffettivo)){
							if(s<0){
								throw new ValidatorException("Expected a unsigned value");
							}
						}
					}catch(Exception e){
						throw new ValidatorException(e.getMessage(),e);
					}
				}
				else if("boolean".equalsIgnoreCase(tipoEffettivo)){
					try{
						Boolean.parseBoolean(value);
					}catch(Exception e){
						throw new ValidatorException(e.getMessage(),e);
					}
				}
				else if("anyURI".equalsIgnoreCase(tipoEffettivo)){
					try{
						new URI(value);
					}catch(Exception e){
						throw new ValidatorException(e.getMessage(),e);
					}
				}
			}
			
		}
		
		// altri tipi non li valido per ora
		
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
			
			if(content instanceof Element){
				node = ((Element) content);
			}
			else if(content instanceof Document){
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
