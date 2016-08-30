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


package org.openspcoop2.core.registry.wsdl;

import java.util.List;

import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.wsdl.extensions.soap.SOAPHeaderFault;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.soap.SOAPEnvelope;

import org.slf4j.Logger;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.constants.BindingStyle;
import org.openspcoop2.core.registry.constants.BindingUse;
import org.openspcoop2.core.registry.driver.AccordoServizioUtils;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.HttpUtilities;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Document;


/**
 * Classe utilizzata per leggere/modificare i wsdl che formano un accordo di un servizio
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class AccordoServizioWrapperUtilities {

	private Logger logger = null;
	private RegistroOpenSPCoopUtilities openspcoopUtilities = null;
	private AbstractXMLUtils xmlUtils = null;
	private WSDLUtilities wsdlUtilities = null;
	private AccordoServizioUtils accordoServizioUtils = null;
	
	public AccordoServizioWrapperUtilities(Logger log){
		if(log!=null)
			this.logger = log;
		else
			this.logger = LoggerWrapperFactory.getLogger(AccordoServizioWrapperUtilities.class);
		this.openspcoopUtilities = new RegistroOpenSPCoopUtilities(this.logger);
		this.xmlUtils = XMLUtils.getInstance();
		this.wsdlUtilities = WSDLUtilities.getInstance(this.xmlUtils);
		this.accordoServizioUtils = new AccordoServizioUtils(this.logger);
	}
	
	
	/** Accordo Servizio */
	public AccordoServizioWrapperUtilities(Logger log, AccordoServizioWrapper accordoServizio){
		if(log!=null)
			this.logger = log;
		else
			this.logger = LoggerWrapperFactory.getLogger(AccordoServizioWrapperUtilities.class);
		this.accordoServizioWrapper = accordoServizio;
	}
	private AccordoServizioWrapper accordoServizioWrapper = null;
	public AccordoServizioWrapper getAccordoServizioWrapper() {
		return this.accordoServizioWrapper;
	}
	public void setAccordoServizio(AccordoServizioWrapper accordoServizio) {
		this.accordoServizioWrapper = accordoServizio;
	}
	
	
	
	
	
	
	
	/** Schema */
	public void buildSchema(boolean fromBytes) throws DriverRegistroServiziException {
		if(this.accordoServizioWrapper==null)
			throw new DriverRegistroServiziException("Accordo di Servizio Wrapper non definito, utilizza setAccordoServizioWrapper()");
		if(this.accordoServizioWrapper.getAccordoServizio()==null)
			throw new DriverRegistroServiziException("Accordo di Servizio OpenSPCoop non definito all'interno dell'Accordo di Servizio Wrapper");
		AccordoServizioParteComune as = this.accordoServizioWrapper.getAccordoServizio();
		this.accordoServizioWrapper.setSchema(this.accordoServizioUtils.buildSchema(as, fromBytes));
	}


		
	
	
	
	
	
	/** WSDL */
	public javax.wsdl.Definition buildWsdlErogatore() throws DriverRegistroServiziException{
		if(this.accordoServizioWrapper==null)
			throw new DriverRegistroServiziException("Accordo di Servizio Wrapper non definito, utilizza setAccordoServizioWrapper()");
		if(this.accordoServizioWrapper.getAccordoServizio()==null)
			throw new DriverRegistroServiziException("Accordo di Servizio OpenSPCoop non definito all'interno dell'Accordo di Servizio Wrapper");
		AccordoServizioParteComune as = this.accordoServizioWrapper.getAccordoServizio();
		if(as.getWsdlLogicoErogatore()==null || "".equals(as.getWsdlLogicoErogatore()) ){
			throw new DriverRegistroServiziException("Location del WsdlLogicoErogatore non definito");
		}
		javax.wsdl.Definition wsdl = this.buildWsdl(as.getWsdlLogicoErogatore());
		
		if(this.accordoServizioWrapper.getLocationWsdlImplementativoErogatore()!=null && ("".equals(this.accordoServizioWrapper.getLocationWsdlImplementativoErogatore())==false)){
			javax.wsdl.Definition wsdlImplementativo = this.buildWsdl(this.accordoServizioWrapper.getLocationWsdlImplementativoErogatore());
			this.logger.debug("Add parte implementativa...");
			this.openspcoopUtilities.addParteImplementativa(wsdl, wsdlImplementativo);
		}
		
		return wsdl;
	}
	public javax.wsdl.Definition buildWsdlFruitore() throws DriverRegistroServiziException{
		if(this.accordoServizioWrapper==null)
			throw new DriverRegistroServiziException("Accordo di Servizio non definito, utilizza setAccordoServizio()");
		if(this.accordoServizioWrapper.getAccordoServizio()==null)
			throw new DriverRegistroServiziException("Accordo di Servizio OpenSPCoop non definito all'interno dell'Accordo di Servizio Wrapper");
		AccordoServizioParteComune as = this.accordoServizioWrapper.getAccordoServizio();
		if(as.getWsdlLogicoFruitore()==null || "".equals(as.getWsdlLogicoFruitore()) ){
			throw new DriverRegistroServiziException("Location del WsdlLogicoFruitore non definito, utilizza setLocationWsdlLogicoFruitore(String)");
		}
		javax.wsdl.Definition wsdl = this.buildWsdl(as.getWsdlLogicoFruitore());
		
		if(this.accordoServizioWrapper.getLocationWsdlImplementativoFruitore()!=null && ("".equals(this.accordoServizioWrapper.getLocationWsdlImplementativoFruitore())==false)){
			javax.wsdl.Definition wsdlImplementativo = this.buildWsdl(this.accordoServizioWrapper.getLocationWsdlImplementativoFruitore());
			this.logger.debug("Add parte implementativa...");
			this.openspcoopUtilities.addParteImplementativa(wsdl, wsdlImplementativo);
		}
		
		return wsdl;
	}
	public javax.wsdl.Definition buildWsdlErogatoreFromBytes() throws DriverRegistroServiziException{
		if(this.accordoServizioWrapper==null)
			throw new DriverRegistroServiziException("Accordo di Servizio non definito, utilizza setAccordoServizio()");
		if(this.accordoServizioWrapper.getAccordoServizio()==null)
			throw new DriverRegistroServiziException("Accordo di Servizio OpenSPCoop non definito all'interno dell'Accordo di Servizio Wrapper");
		AccordoServizioParteComune as = this.accordoServizioWrapper.getAccordoServizio();
		Definition wsdl = this.openspcoopUtilities.buildWsdlFromObjects(as, this.accordoServizioWrapper.getBytesWsdlImplementativoErogatore(), true);
		return wsdl;
	}
	public javax.wsdl.Definition buildWsdlFruitoreFromBytes() throws DriverRegistroServiziException{
		if(this.accordoServizioWrapper==null)
			throw new DriverRegistroServiziException("Accordo di Servizio non definito, utilizza setAccordoServizio()");
		if(this.accordoServizioWrapper.getAccordoServizio()==null)
			throw new DriverRegistroServiziException("Accordo di Servizio OpenSPCoop non definito all'interno dell'Accordo di Servizio Wrapper");
		AccordoServizioParteComune as = this.accordoServizioWrapper.getAccordoServizio();
		Definition wsdl = this.openspcoopUtilities.buildWsdlFromObjects(as, this.accordoServizioWrapper.getBytesWsdlImplementativoFruitore(), false);
		return wsdl;
	}
	private javax.wsdl.Definition buildWsdl(String url) throws DriverRegistroServiziException{
		try{
			org.openspcoop2.message.XMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
						
			// Costruttore WSDL
			Document document = null;
			if(url.startsWith("http://") || url.startsWith("file://")){
				document = xmlUtils.newDocument(HttpUtilities.requestHTTPFile(url));
			}else{
				document = xmlUtils.newDocument(FileSystemUtilities.readBytesFromFile(url));
			}
			
			// Elimino schema xsd e import di altri wsdl, non servono per la validazione wsdl di openspcoop
			// TODO: Forse questi due imports servono! Nella costruzione del metodo this.openspcoopUtilities.buildWsdlFromObjects gli schemi vengono inseriti nel wsdl.
			this.wsdlUtilities.removeSchemiIntoTypes(document);
			this.wsdlUtilities.removeImports(document);
			
			javax.wsdl.Definition wsdl = this.wsdlUtilities.readWSDLFromDocument(document);
			return wsdl;
		}catch (Exception e) {
			throw new DriverRegistroServiziException("Riscontrato errore durante la costruzione del wsdl ["+url+"]: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	/** Utilities oggetti Registro OpenSPCoop */
	
	public static void addMessageInputOperation(javax.wsdl.Operation operationWSDL,Logger logger,Operation operationAS){
		// Prendo la definizione del messaggio di input
		if(operationWSDL.getInput()!=null && operationWSDL.getInput().getMessage()!=null &&
				operationWSDL.getInput().getMessage().getParts()!=null){
			java.util.Map<?,?> argumentsOperationInput = operationWSDL.getInput().getMessage().getParts();
			if(argumentsOperationInput!=null && argumentsOperationInput.size()>0){
				Message mInputAS = new Message();
				java.util.Iterator<?> argumentsIterator = argumentsOperationInput.values().iterator();
				while (argumentsIterator.hasNext()) {
					MessagePart partAS = new MessagePart();
					javax.wsdl.Part argument = (javax.wsdl.Part) argumentsIterator.next();
					partAS.setName(argument.getName());
					if(argument.getElementName()!=null){
						String name = argument.getElementName().getLocalPart();
						String namespace = argument.getElementName().getNamespaceURI();
						partAS.setElementName(name);
						partAS.setElementNamespace(namespace);
					}
					if(argument.getTypeName()!=null){
						String name = argument.getTypeName().getLocalPart();
						String namespace = argument.getTypeName().getNamespaceURI();
						partAS.setTypeName(name);
						partAS.setTypeNamespace(namespace);
					}
					mInputAS.addPart(partAS);
					logger.debug("add message input dell'operation["+operationWSDL.getName()+
							"] con name ["+partAS.getName()+"] element-info({"+partAS.getElementNamespace()+"}"+partAS.getElementName()+
							") type-info({"+partAS.getTypeNamespace()+"}"+partAS.getTypeName()+")");
				}
				operationAS.setMessageInput(mInputAS);
			}
		}
	}
	
	public static void addMessageOutputOperation(javax.wsdl.Operation operationWSDL,Logger logger,Operation operationAS){
		// Prendo la definizione del messaggio di output
		if(operationWSDL.getOutput()!=null && operationWSDL.getOutput().getMessage()!=null &&
				operationWSDL.getOutput().getMessage().getParts()!=null){
			java.util.Map<?,?> argumentsOperationOutput = operationWSDL.getOutput().getMessage().getParts();
			if(argumentsOperationOutput!=null && argumentsOperationOutput.size()>0){
				Message mOutputAS = new Message();
				java.util.Iterator<?> argumentsIterator = argumentsOperationOutput.values().iterator();
				while (argumentsIterator.hasNext()) {
					MessagePart partAS = new MessagePart();
					javax.wsdl.Part argument = (javax.wsdl.Part) argumentsIterator.next();
					partAS.setName(argument.getName());
					if(argument.getElementName()!=null){
						String name = argument.getElementName().getLocalPart();
						String namespace = argument.getElementName().getNamespaceURI();
						partAS.setElementName(name);
						partAS.setElementNamespace(namespace);
					}
					if(argument.getTypeName()!=null){
						String name = argument.getTypeName().getLocalPart();
						String namespace = argument.getTypeName().getNamespaceURI();
						partAS.setTypeName(name);
						partAS.setTypeNamespace(namespace);
					}
					mOutputAS.addPart(partAS);
					logger.debug("add message output dell'operation["+operationWSDL.getName()+
							"] con name ["+partAS.getName()+"] element-info({"+partAS.getElementNamespace()+"}"+partAS.getElementName()+
							") type-info({"+partAS.getTypeNamespace()+"}"+partAS.getTypeName()+")");
				}
				operationAS.setMessageOutput(mOutputAS);
			}
		}
	}
	
	public static void setPortTypeSoapBindingStyle(javax.wsdl.Binding bindingWSDL, Logger logger,PortType ptAS){
		String nomePortType = ptAS.getNome();
		List<?> extendibleElements = bindingWSDL.getExtensibilityElements();
		if(extendibleElements!=null){
			logger.debug("esamino binding extendibles ["+extendibleElements.size()+"] per port type: ["+nomePortType+"]");
			for(int i=0; i<extendibleElements.size(); i++){
				ExtensibilityElement elem = (ExtensibilityElement) extendibleElements.get(i);
				logger.debug("esamino binding extendibles di tipo:"+elem.getClass().getName()+" instance of SOAPBinding:"+(elem instanceof SOAPBinding));
				if(elem instanceof SOAPBinding){
					SOAPBinding soapBinding = (SOAPBinding) elem;
					ptAS.setStyle(BindingStyle.toEnumConstant(soapBinding.getStyle()));
					logger.debug("add style port type ["+nomePortType+"]: "+soapBinding.getStyle());
				}
				else if(elem instanceof javax.wsdl.extensions.soap12.SOAP12Binding){
					javax.wsdl.extensions.soap12.SOAP12Binding soapBinding = (javax.wsdl.extensions.soap12.SOAP12Binding) elem;
					ptAS.setStyle(BindingStyle.toEnumConstant(soapBinding.getStyle()));
					logger.debug("add style port type ["+nomePortType+"]: "+soapBinding.getStyle());
				}
			}
		}
	}
	
	public static void setOperationSoapBindingInformation(BindingOperation bindingOperation,Logger logger,
			Operation operationAS,PortType ptAS){
		String nomeOperation = operationAS.getNome();
		String nomePortType = ptAS.getNome();
		List<?> extendibleElementsOperations = bindingOperation.getExtensibilityElements();
		if(extendibleElementsOperations!=null){
			logger.debug("esamino binding extendibles ["+extendibleElementsOperations.size()+"] per azione["+nomeOperation+"] del port type["+nomePortType+"]");
			for(int j=0; j<extendibleElementsOperations.size(); j++){
				ExtensibilityElement elem = (ExtensibilityElement) extendibleElementsOperations.get(j);
				logger.debug("esamino binding extendibles di tipo:"+elem.getClass().getName()+" instance of SOAPOperation:"+(elem instanceof SOAPOperation));
				if(elem instanceof SOAPOperation){
					SOAPOperation soapOperation = (SOAPOperation) elem;
					operationAS.setStyle(BindingStyle.toEnumConstant(soapOperation.getStyle()));
					logger.debug("add style Operation ["+nomeOperation+"] del Port type ["+ptAS.getNome()+"]: "+soapOperation.getStyle());
					operationAS.setSoapAction(soapOperation.getSoapActionURI());
					logger.debug("add SOAPAction Operation ["+nomeOperation+"] del Port type ["+ptAS.getNome()+"]: "+soapOperation.getSoapActionURI());
				}
				else if(elem instanceof javax.wsdl.extensions.soap12.SOAP12Operation){
					javax.wsdl.extensions.soap12.SOAP12Operation soapOperation = (javax.wsdl.extensions.soap12.SOAP12Operation) elem;
					operationAS.setStyle(BindingStyle.toEnumConstant(soapOperation.getStyle()));
					logger.debug("add style Operation ["+nomeOperation+"] del Port type ["+ptAS.getNome()+"]: "+soapOperation.getStyle());
					operationAS.setSoapAction(soapOperation.getSoapActionURI());
					logger.debug("add SOAPAction Operation ["+nomeOperation+"] del Port type ["+ptAS.getNome()+"]: "+soapOperation.getSoapActionURI());
				}
			}
		}
	}
	
	public static void setMessageInputSoapBindingInformation(BindingOperation bindingOperation,Logger logger,
			Operation operationAS,PortType ptAS){
		String nomeOperation = operationAS.getNome();
		String nomePortType = ptAS.getNome();
		BindingInput bindingInput = bindingOperation.getBindingInput();
		if(bindingInput!=null){
			List<?> extendibleElementsMessageInput = bindingInput.getExtensibilityElements();
			if(extendibleElementsMessageInput!=null){
				logger.debug("esamino binding extendibles ["+extendibleElementsMessageInput.size()+"] per message-input dell'azione["+nomeOperation+"] del port type["+nomePortType+"]");
				for(int j=0; j<extendibleElementsMessageInput.size(); j++){
					ExtensibilityElement elem = (ExtensibilityElement) extendibleElementsMessageInput.get(j);
					logger.debug("esamino binding extendibles di tipo:"+elem.getClass().getName()+" instance of SOAPBody:"+(elem instanceof SOAPBody));
					setPartMessageSoapBindingOperationInfo(elem, logger, operationAS, ptAS, true, operationAS.getMessageInput());
				}
			}
		}
	}
	
	public static void setMessageOutputSoapBindingInformation(BindingOperation bindingOperation,Logger logger,
			Operation operationAS,PortType ptAS){
		String nomeOperation = operationAS.getNome();
		String nomePortType = ptAS.getNome();
		BindingOutput bindingOutput = bindingOperation.getBindingOutput();
		if(bindingOutput!=null){
			List<?> extendibleElementsMessageOutput = bindingOutput.getExtensibilityElements();
			if(extendibleElementsMessageOutput!=null){
				logger.debug("esamino binding extendibles ["+extendibleElementsMessageOutput.size()+"] per message-output dell'azione["+nomeOperation+"] del port type["+nomePortType+"]");
				for(int j=0; j<extendibleElementsMessageOutput.size(); j++){
					ExtensibilityElement elem = (ExtensibilityElement) extendibleElementsMessageOutput.get(j);
					logger.debug("esamino binding extendibles di tipo:"+elem.getClass().getName()+" instance of SOAPBody:"+(elem instanceof SOAPBody));
					setPartMessageSoapBindingOperationInfo(elem, logger, operationAS, ptAS, false, operationAS.getMessageOutput());
				}
			}
		}
	}
	
	public static void setPartMessageSoapBindingOperationInfo(ExtensibilityElement elem,Logger logger,
			Operation operationAS,PortType ptAS, boolean inputMessage, Message message){
		
		String nomeOperation = operationAS.getNome();
		String tipoMessage = "message-input";
		if(!inputMessage){
			tipoMessage = "message-output";
		}
		
		// *** SOAP11 ***
		if(elem instanceof SOAPBody){
			SOAPBody soapBody = (SOAPBody) elem;
			message.setUse(BindingUse.toEnumConstant(soapBody.getUse()));
			message.setSoapNamespace(soapBody.getNamespaceURI());
			StringBuffer bf = new StringBuffer();
			List<?> listParts = soapBody.getParts();
			if(listParts!=null){
				for (Object part : listParts) {
					if(bf.length()>0){
						bf.append(",");
					}
					bf.append(part);
				}
			}
			logger.debug("add use "+tipoMessage+" Operation ["+nomeOperation+"] (parts:"+bf.toString()+") del Port type ["+ptAS.getNome()+"]: "+soapBody.getUse());
		}
		else if(elem instanceof SOAPHeader){
			SOAPHeader soapHeader = (SOAPHeader) elem;
			String part = soapHeader.getPart();
			if(part!=null){
				if(message.sizePartList()>0){
					for (int i = 0; i <message.sizePartList(); i++) {
						if(part.equals(message.getPart(i).getName())){
							// Li rimuovo poiche' non vengono gestiti nella validazione XSD/WSDL attuale.
							// TODO: in futuro aggiungere supporto, classificando il part come header o body
							message.removePart(i);
							break;
						}
					}
				}
			}
		}
		else if(elem instanceof SOAPHeaderFault){
			SOAPHeaderFault soapHeaderFault = (SOAPHeaderFault) elem;
			String part = soapHeaderFault.getPart();
			if(part!=null){
				if(message.sizePartList()>0){
					for (int i = 0; i <message.sizePartList(); i++) {
						if(part.equals(message.getPart(i).getName())){
							// Li rimuovo poiche' non vengono gestiti nella validazione XSD/WSDL attuale.
							// TODO: in futuro aggiungere supporto, classificando il part come header o body
							message.removePart(i);
							break;
						}
					}
				}
			}
		}
		
		// *** SOAP12 ***
		else if(elem instanceof javax.wsdl.extensions.soap12.SOAP12Body){
			javax.wsdl.extensions.soap12.SOAP12Body soapBody = (javax.wsdl.extensions.soap12.SOAP12Body) elem;
			message.setUse(BindingUse.toEnumConstant(soapBody.getUse()));
			message.setSoapNamespace(soapBody.getNamespaceURI());
			StringBuffer bf = new StringBuffer();
			List<?> listParts = soapBody.getParts();
			if(listParts!=null){
				for (Object part : listParts) {
					if(bf.length()>0){
						bf.append(",");
					}
					bf.append(part);
				}
			}
			logger.debug("add use "+tipoMessage+" Operation ["+nomeOperation+"] (parts:"+bf.toString()+") del Port type ["+ptAS.getNome()+"]: "+soapBody.getUse());
		}
		else if(elem instanceof javax.wsdl.extensions.soap12.SOAP12Header){
			javax.wsdl.extensions.soap12.SOAP12Header soapHeader = (javax.wsdl.extensions.soap12.SOAP12Header) elem;
			String part = soapHeader.getPart();
			if(part!=null){
				if(message.sizePartList()>0){
					for (int i = 0; i <message.sizePartList(); i++) {
						if(part.equals(message.getPart(i).getName())){
							// Li rimuovo poiche' non vengono gestiti nella validazione XSD/WSDL attuale.
							// TODO: in futuro aggiungere supporto, classificando il part come header o body
							message.removePart(i);
							break;
						}
					}
				}
			}
		}
		else if(elem instanceof javax.wsdl.extensions.soap12.SOAP12HeaderFault){
			javax.wsdl.extensions.soap12.SOAP12HeaderFault soapHeaderFault = (javax.wsdl.extensions.soap12.SOAP12HeaderFault) elem;
			String part = soapHeaderFault.getPart();
			if(part!=null){
				if(message.sizePartList()>0){
					for (int i = 0; i <message.sizePartList(); i++) {
						if(part.equals(message.getPart(i).getName())){
							// Li rimuovo poiche' non vengono gestiti nella validazione XSD/WSDL attuale.
							// TODO: in futuro aggiungere supporto, classificando il part come header o body
							message.removePart(i);
							break;
						}
					}
				}
			}
		}
	}
	
	
	
	
	
	
	/** Accordo Servizio */

	public void buildAccordoServizioWrapperFromWsdl(javax.wsdl.Definition wsdl,boolean readParteImplementativa) throws DriverRegistroServiziException{
			
		try{
			if(this.accordoServizioWrapper==null){
				this.accordoServizioWrapper = new AccordoServizioWrapper();
			}
			this.accordoServizioWrapper.setPortTypesLoadedFromWSDL(true);
			
			javax.wsdl.PortType portTypeWSDL = null;
			java.util.Map<?,?> portTypes = wsdl.getPortTypes();
			if(portTypes==null || portTypes.size()<=0){
				throw new DriverRegistroServiziException("Port types non presenti");
			}
			java.util.Iterator<?> portTypeIterator = portTypes.values().iterator();
			while(portTypeIterator.hasNext()) {
				portTypeWSDL = (javax.wsdl.PortType) portTypeIterator.next();
				
				// PortType Registro (lo stile viene settato dopo)
				PortType ptAS = new PortType();
				ptAS.setNome(portTypeWSDL.getQName().getLocalPart());
				this.logger.debug("add port type: ["+portTypeWSDL.getQName().getLocalPart()+"]");
				
				java.util.List<?> operations = portTypeWSDL.getOperations();
				if(operations==null || operations.size()<=0)
					throw new DriverRegistroServiziException("operations per il port type ["+ptAS.getNome()+"] non presenti");
				for (int opIndex = 0; opIndex < operations.size(); opIndex++) {
					javax.wsdl.Operation operationWSDL = (javax.wsdl.Operation) operations.get(opIndex);
					
					// Operation Registro (lo stile, use e soapAction viene settato dopo)
					Operation operationAS = new Operation();
					operationAS.setNome(operationWSDL.getName());
					this.logger.debug("add operation: ["+operationWSDL.getName()+"]");
					
					// Prendo la definizione del messaggio di input
					addMessageInputOperation(operationWSDL, this.logger, operationAS);
					
					// Prendo la definizione del messaggio di output
					addMessageOutputOperation(operationWSDL, this.logger, operationAS);
					
					ptAS.addAzione(operationAS);
				}
				this.accordoServizioWrapper.addPortType(ptAS);
			}
			
			if(readParteImplementativa){
				java.util.Map<?,?> bindings = wsdl.getAllBindings();
				if(bindings==null || bindings.size()<=0){
					throw new DriverRegistroServiziException("bindings non presenti");
				}
				this.logger.debug("bindings presenti ["+bindings.size()+"]");
				javax.wsdl.Binding bindingWSDL = null;
				java.util.Iterator<?> bindingsIterator = bindings.values().iterator();
				while(bindingsIterator.hasNext()) {
					bindingWSDL = (javax.wsdl.Binding) bindingsIterator.next();
					
					// Raccolgo dati portType
					String nomePortType = bindingWSDL.getPortType().getQName().getLocalPart();
					PortType ptAS = this.accordoServizioWrapper.removePortType(nomePortType);
					if(ptAS==null)
						throw new DriverRegistroServiziException("Port type ["+nomePortType+"] non presente, nella lista dei port type, durante l'analisi della parte di binding");
					
					this.logger.debug("esamino binding per port type: ["+nomePortType+"]");
					
					// SoapBinding PortType
					setPortTypeSoapBindingStyle(bindingWSDL, this.logger, ptAS);
					
					// Definizione operations
					List<?> bindingsOperation = bindingWSDL.getBindingOperations();
					if(bindingsOperation==null || bindingsOperation.size()<=0)
						throw new DriverRegistroServiziException("Bindings operations per il port type ["+ptAS.getNome()+"] non presenti");
					for(int i=0; i<bindingsOperation.size();i++){
						BindingOperation bindingOperation = (BindingOperation) bindingsOperation.get(i);
						
						// Raccolgo dati operation
						String nomeOperation = bindingOperation.getName();
						Operation operationAS = null;
						for(int j=0;j<ptAS.sizeAzioneList();j++){
							if(nomeOperation.equals(ptAS.getAzione(j).getNome())){
								operationAS = ptAS.removeAzione(j);
								j++;
							}
						}
						if(operationAS==null){
							throw new DriverRegistroServiziException("Operation ["+nomeOperation+"] del Port type ["+ptAS.getNome()+"] non presente, nella lista delle operation, durante l'analisi della parte di binding");
						}
						
						// SoapBinding Operation
						setOperationSoapBindingInformation(bindingOperation, this.logger, operationAS, ptAS);
											
						// Raccolgo Message-Input
						setMessageInputSoapBindingInformation(bindingOperation, this.logger, operationAS, ptAS);
						
						// Raccolgo Message-Output
						setMessageOutputSoapBindingInformation(bindingOperation, this.logger, operationAS, ptAS);
						
						ptAS.addAzione(operationAS);
					}		
					this.accordoServizioWrapper.addPortType(ptAS);
				}
			}
			
		}catch (Exception e) {
			throw new DriverRegistroServiziException("Riscontrato errore durante la lettura del wsdl: "+e.getMessage(),e);
		}
	}
	public void buildAccordoServizioWrapperFromOpenSPCoopAS(AccordoServizioParteComune as) throws DriverRegistroServiziException{
		if(this.accordoServizioWrapper==null){
			this.accordoServizioWrapper = new AccordoServizioWrapper();
		}
		this.accordoServizioWrapper.setPortTypesLoadedFromWSDL(false);
		for(int i=0; i<as.sizePortTypeList();i++){
			this.accordoServizioWrapper.addPortType(as.getPortType(i));
		}
	}
	
	
	
	
	
	
	
	
	
	
	/** Search action  */
	
	public String searchOperationName(boolean isRichiesta, OpenSPCoop2Message message) throws DriverRegistroServiziException{
		return searchOperationName(isRichiesta, null, message);
	}
	public String searchOperationName(boolean isRichiesta, String portType, OpenSPCoop2Message message) throws DriverRegistroServiziException{
		
		try{
			if(this.accordoServizioWrapper==null){
				throw new DriverRegistroServiziException("Accordo Servizio Wrapper non definito");
			}
			if(message==null || message.getSOAPPart()==null){
				throw new DriverRegistroServiziException("Messaggio non fornito");
			}
			SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
			if(envelope==null){
				throw new DriverRegistroServiziException("Envelope non fornita");
			}
			WSDLValidator wsdlValidator = new WSDLValidator(message.getVersioneSoap(),envelope, this.xmlUtils, this.accordoServizioWrapper, this.logger);
			for (int i = 0; i < this.accordoServizioWrapper.sizePortTypeList(); i++) {
				PortType pt = this.accordoServizioWrapper.getPortType(i);
				if(portType!=null){
					if(pt.getNome().equals(portType)==false){
						continue;
					}
				}
				for (int j = 0; j < pt.sizeAzioneList(); j++) {
					Operation op = pt.getAzione(j);
					try{
						//System.out.println("Check azione ["+op.getNome()+"]...");
						wsdlValidator.wsdlConformanceCheck(isRichiesta, null, op.getNome(), false, true);
						return op.getNome();
					}catch(Exception e){
						this.logger.debug("@@@searchOperationName Azione ["+op.getNome()+"] mismatch: "+e.getMessage());
						//e.printStackTrace(System.out);
					}
				}
				
			}
			
			return null;
		}
		catch(DriverRegistroServiziException e){
			throw e;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		
	}
}
