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




package org.openspcoop2.core.registry.wsdl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;

import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.constants.BindingStyle;
import org.openspcoop2.core.registry.constants.BindingUse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.wsdl.WSDLException;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.ValidatoreXSD;
import org.slf4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 * Classe utilizzata per validare i messaggi applicativi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lezza Aldo (lezza@openspcoop.org)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class WSDLValidator {

	public static final String XMLSCHEMA_INSTANCE_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String XMLSCHEMA_INSTANCE_LOCAL_NAME_TYPE = "type";
	private static final String PREFIX_RPC_AGGIUNTO = "op2RPC";
	
	/** OpenSPCoop2Message */
	private OpenSPCoop2Message openspcoop2Message;
	/** SOAPVersion */
	private MessageType messageType;
	/** Element */
	private Element element;
	/** WSDL Associato al servizio */
	private AccordoServizioWrapper accordoServizioWrapper = null;
	/** Logger */
	private Logger logger = null;
	/** XMLUtils */
	private AbstractXMLUtils xmlUtils = null;
	
	/** Nodo xsiType Aggiunto */
	private boolean gestioneXsiType_rpcLiteral;
	private SOAPElement rpcChildElement;
	private QName rpcChildElementNamespaceAggiunto;
	private QName rpcChildElementXSITypeAggiunto;
	
	/** Prefix error */
	private boolean addPrefixError = true;
	public boolean isAddPrefixError() {
		return this.addPrefixError;
	}
	public void setAddPrefixError(boolean addPrefixError) {
		this.addPrefixError = addPrefixError;
	}
	
	/* ------ Costruttore -------------- */
	private static Element _getEnvelopeCatchException(OpenSPCoop2Message msg, boolean bufferMessage_readOnly, String idTransazione) throws WSDLException {
		try {
			boolean checkSoapBodyEmpty = true;
			return MessageUtils.getContentElement(msg, checkSoapBodyEmpty, bufferMessage_readOnly, idTransazione);
		}catch(Exception e){
			throw new WSDLException(e.getMessage(),e);
		}
	}
	public WSDLValidator(OpenSPCoop2Message msg,AbstractXMLUtils xmlUtils,AccordoServizioWrapper accordoServizioWrapper,Logger log,
			boolean gestioneXsiType_rpcLiteral, boolean addPrefixError,
			boolean bufferMessage_readOnly, String idTransazione)throws WSDLException{
		this(msg.getMessageType(), _getEnvelopeCatchException(msg,bufferMessage_readOnly,idTransazione), xmlUtils, accordoServizioWrapper, log, gestioneXsiType_rpcLiteral, addPrefixError);
		this.openspcoop2Message = msg;
	}
	// Il costruttore sottostante non puo' sfruttare la funzionalita' addNamespaceXSITypeIfNotExists
	// per questo e' stato reso privato, poiche' tale funzionalita' richiede openspcoop2Message
	private WSDLValidator(MessageType messageType, Element element,AbstractXMLUtils xmlUtils,AccordoServizioWrapper accordoServizioWrapper,Logger log,
			boolean gestioneXsiType_rpcLiteral, boolean addPrefixError)throws WSDLException{
		
		this.messageType = messageType;
		
		if(element==null){
			throw new WSDLException("Contenuto da validatore non fornito");
		}
		this.element = element;
		if(MessageType.SOAP_11.equals(this.messageType) || MessageType.SOAP_12.equals(this.messageType)){
			SOAPEnvelope envelope = (SOAPEnvelope) element;
			SOAPBody body = null;
			try{
				body = envelope.getBody();
			}catch(Exception e){
				if(this.logger!=null) {
					this.logger.error("SOAPEnvelope.getBody failed: "+e.getMessage(),e);
				}
				throw new WSDLException("SOAPEnvelope senza body");
			}
			
			if(body==null || (body.hasChildNodes()==false)){
				throw new WSDLException("SOAPBody non esistente");
			}
		}
		
		this.logger = log;
		this.xmlUtils = xmlUtils;
		this.accordoServizioWrapper = accordoServizioWrapper;
		
		this.gestioneXsiType_rpcLiteral = gestioneXsiType_rpcLiteral;
		
		this.addPrefixError = addPrefixError;
	}
	
	
	
	
	
	
	
	/* -------------- FINALIZE --------------------- */
	public void wsdlConformanceCheck_restoreOriginalDocument(){
		if(this.gestioneXsiType_rpcLiteral && this.rpcChildElement!=null){
			try{
				if(this.rpcChildElementXSITypeAggiunto!=null){
					this.rpcChildElement.removeAttribute(this.rpcChildElementXSITypeAggiunto);
				}
			}catch(Exception e){
				this.logger.error("Errore durante l'eliminazione dell'attributo ["+this.rpcChildElementXSITypeAggiunto+"] dal rpc element");
			}
			try{
				if(this.rpcChildElementNamespaceAggiunto!=null){
					// alcune implementazioni usano l'uno o l'altro per eliminarlo
					this.rpcChildElement.removeAttribute(this.rpcChildElementNamespaceAggiunto);
					this.rpcChildElement.removeNamespaceDeclaration(PREFIX_RPC_AGGIUNTO);
				}
			}catch(Exception e){
				this.logger.error("Errore durante l'eliminazione dell'attributo ["+this.rpcChildElementNamespaceAggiunto+"] dal rpc element");
			}
		}
	}
	
	
	
	
	
	
	
	/* -------------- VALIDAZIONE XSD SENZA WSDL --------------------- */
	
	/**
	 * Validazione xsd
	 * 
	 * @param isRichiesta Indicazione sul tipo di messaggio applicativo da gestire
	 * @throws WSDLException (contiene codice e msg di errore)
	 */
	public void validateAgainstXSDSchema(boolean isRichiesta,String operationName) throws WSDLException,WSDLValidatorException{
		this.validateAgainstXSDSchema(isRichiesta, operationName, this.ptWsdlConformanceCheck, this.opWsdlConformanceCheck);
	}
	public void validateAgainstXSDSchema(boolean isRichiesta,String operationName,PortType portType,Operation operation) throws WSDLException,WSDLValidatorException{

		AbstractValidatoreXSD validatoreBodyApplicativo = null;
		try{
			if(this.accordoServizioWrapper.getSchema()!=null){
				validatoreBodyApplicativo = new ValidatoreXSD(this.accordoServizioWrapper.getSchema());
			}else{
				throw new Exception("Schema non costruito?");
			}
		}catch(Exception e){
			throw new WSDLException("Riscontrato errore durante la costruzione del validatore XSD per il contenuto applicativo: "+e.getMessage(),e);
		}
		
		
		/** ricerca ulteriore port type e operation in caso di validazione xsd */
		if(portType==null && this.accordoServizioWrapper.getNomePortType()!=null){
			// provo a cercarlo. Magari non e' stato fatto girare prima il metodo wsdlConformanceCheck
			// o magari volutamente non c'e' un wsdl perche' la validazione prevista e' XSD
			// in tal caso non e' un errore se il port type non e' presente.
			portType = this.accordoServizioWrapper.getPortType(this.accordoServizioWrapper.getNomePortType());
			if(portType==null){
				try{
					IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
					String uriAccordo = idAccordoFactory.getUriFromIDAccordo(this.accordoServizioWrapper.getIdAccordoServizio());
					if(this.accordoServizioWrapper.isPortTypesLoadedFromWSDL()){
						this.logger.debug("Port-Type ["+this.accordoServizioWrapper.getNomePortType()+"] non esistente nei wsdl registrati all'interno dell'accordo di servizio "+uriAccordo);
					}else{
						this.logger.debug("Servizio ["+this.accordoServizioWrapper.getNomePortType()+"] non esistente nell'accordo di servizio "+uriAccordo);
					}
				}catch(Exception e){
					this.logger.error("Errore durante l'emissione del log che indica la non esistenza del porttype/servizio ["+this.accordoServizioWrapper.getNomePortType()+
							"] nei wsdl registrati all'interno dell'accordo di servizio: "+e.getMessage(),e);
				}
			}
		}
		if(operation==null && operationName!=null && portType!=null){
			// provo a cercare l'operation. Magari non e' stato fatto girare prima il metodo wsdlConformanceCheck
			// o magari volutamente non c'e' un wsdl perche' la validazione prevista e' XSD
			// in tal caso non e' un errore se l'operation non e' presente.	
			for(int i=0; i<portType.sizeAzioneList();i++){
				if(operationName.equals(portType.getAzione(i).getNome())){
					operation = portType.getAzione(i);
					break;
				}
			}
		}
		
		
		StringBuilder errorMsgValidazioneXSD = new StringBuilder();
		
		
		/** Validazione XSD senza considerare gli usi e gli stili (rpc/document e literal/encoded) */
		if(portType==null || operation==null){
			this.logger.debug("Validazione XSD senza considerare il WSDLAccordoServizio e quindi senza considerare style (document/rpc) e gli use (literal/encoded)");
			
			List<Node> nodeList = new ArrayList<Node>();
			if(MessageType.SOAP_11.equals(this.messageType) || MessageType.SOAP_12.equals(this.messageType)){
				SOAPEnvelope envelope = (SOAPEnvelope) this.element;
				SOAPBody body = null;
				try{
					body = envelope.getBody();
				}catch(Exception e){
					// eccezione che non dovrebbe accadere. Lo stesso controllo viene fatto nel costruttore
					throw new RuntimeException(e.getMessage(),e);
				}
				org.w3c.dom.NodeList nl = body.getChildNodes();
				for(int i=0; i<nl.getLength(); i++){
					if ( (nl.item(i) instanceof Text) || (nl.item(i) instanceof Comment) ){
						continue;
					}
					nodeList.add(nl.item(i));
				}
			}
			else{
				nodeList.add(this.element);
			}
			
			for(int i=0; i<nodeList.size(); i++){
				String nomeElemento = null;
				String namespaceElemento = null;
				Node n = null;
				try{
					n = nodeList.get(i);
					nomeElemento = n.getLocalName();
					namespaceElemento = n.getNamespaceURI();
					
					// Bug Fix: OPPT-784: Validazione fallisce in presenza di xsi:type e normalizzazione da axiom
					if(this.openspcoop2Message!=null) {
						this.openspcoop2Message.addNamespaceXSITypeIfNotExists(n, this.element);
					}
					
					validatoreBodyApplicativo.valida(n,true);
				}catch(Exception e){
					if(errorMsgValidazioneXSD.length()==0){
						if(this.addPrefixError) {
							if(isRichiesta) {
								errorMsgValidazioneXSD.append("Request");
							}
							else {
								errorMsgValidazioneXSD.append("Response");
							}
							errorMsgValidazioneXSD.append("content not conform to XSD specification\n");
						}
					}else{
						errorMsgValidazioneXSD.append("\n");
					}
					if(namespaceElemento!=null){
						nomeElemento = "{"+namespaceElemento+"}"+nomeElemento;
					}
					errorMsgValidazioneXSD.append("(element "+nomeElemento+") "+e.getMessage());
					String elementNonValidato = null;
					try{
						if(n!=null) {
							elementNonValidato = this.xmlUtils.toString(n);
						}
					}catch(Exception eString){
						this.logger.error("Errore durante la conversione del Node in String: "+eString.getMessage(),eString);
					}
					this.logger.error("Validazione fallita (elemento "+nomeElemento+") ["+elementNonValidato+"]: "+e.getMessage(),e);
				}
			}
		}
		
		/** Validazione XSD che considerare gli usi e gli stili (rpc/document e literal/encoded) */
		else{
			this.logger.debug("Validazione XSD che considera il WSDLAccordoServizio (ho localizzato prima il port-type:"+portType.getNome()+" e l'operation:"+operation.getNome()+")");
			BindingStyle style = CostantiRegistroServizi.WSDL_STYLE_DOCUMENT;
			BindingUse use = CostantiRegistroServizi.WSDL_USE_LITERAL;
			String azione = operationName;
			
			try{
	
				if(portType.getStyle()!=null && ("".equals(portType.getStyle().getValue())==false) && 
						CostantiRegistroServizi.WSDL_STYLE_RPC.equals(portType.getStyle()))
					style = CostantiRegistroServizi.WSDL_STYLE_RPC;
				
				if(operation.getStyle()!=null && ("".equals(operation.getStyle().getValue())==false)){
					if(CostantiRegistroServizi.WSDL_STYLE_RPC.equals(operation.getStyle()))
						style = CostantiRegistroServizi.WSDL_STYLE_RPC;
					else if(CostantiRegistroServizi.WSDL_STYLE_DOCUMENT.equals(operation.getStyle()))
						style = CostantiRegistroServizi.WSDL_STYLE_DOCUMENT;
				}
				
			}catch(Exception e){
				if(errorMsgValidazioneXSD.length()>0){
					errorMsgValidazioneXSD.append("\n");
				}
				errorMsgValidazioneXSD.append("Error in recognizing wsdl style rpc/document '"+e.getMessage()+"'");
				this.logger.error("Validazione fallita durante il riconoscimento del wsdl style rpc/document: "+e.getMessage(),e);
			}
				
			try{
				if(isRichiesta){
					if(operation.getMessageInput()!=null && operation.getMessageInput().getUse()!=null &&
							("".equals(operation.getMessageInput().getUse().getValue())==false) &&
							CostantiRegistroServizi.WSDL_USE_ENCODED.equals(operation.getMessageInput().getUse()))
						use = CostantiRegistroServizi.WSDL_USE_ENCODED; 
				}else{
					if(operation.getMessageOutput()!=null && operation.getMessageOutput().getUse()!=null &&
							("".equals(operation.getMessageOutput().getUse().getValue())==false) &&
							CostantiRegistroServizi.WSDL_USE_ENCODED.equals(operation.getMessageOutput().getUse()))
						use = CostantiRegistroServizi.WSDL_USE_ENCODED; 
				}

			}catch(Exception e){
				if(errorMsgValidazioneXSD.length()>0){
					errorMsgValidazioneXSD.append("\n");
				}
				errorMsgValidazioneXSD.append("Error in recognizing wsdl use literal/encoded '"+e.getMessage()+"'");
				this.logger.error("Validazione fallita durante il riconoscimento del wsdl style literal/encoded: "+e.getMessage(),e);
			}
			
			if(errorMsgValidazioneXSD.length()==0){
				this.logger.debug("Validazione XSD con style["+style+"] e use["+use+"]...");
				
				List<Node> nodeList = new ArrayList<Node>();
				if(MessageType.SOAP_11.equals(this.messageType) || MessageType.SOAP_12.equals(this.messageType)){
					SOAPEnvelope envelope = (SOAPEnvelope) this.element;
					SOAPBody body = null;
					try{
						body = envelope.getBody();
					}catch(Exception e){
						// eccezione che non dovrebbe accadere. Lo stesso controllo viene fatto nel costruttore
						throw new RuntimeException(e.getMessage(),e);
					}
					org.w3c.dom.NodeList nl = body.getChildNodes();
					for(int i=0; i<nl.getLength(); i++){
						if ( (nl.item(i) instanceof Text) || (nl.item(i) instanceof Comment) ){
							continue;
						}
						nodeList.add(nl.item(i));
					}
				}
				else{
					nodeList.add(this.element);
				}
				
				if(CostantiRegistroServizi.WSDL_STYLE_RPC.equals(style)){
					int children=0;
					for(int i=0; i<nodeList.size(); i++){
						children++;
					}
					
					if(children>1){
						errorMsgValidazioneXSD.append("Operation '"+azione+"' (style RPC) bring more than one root element"); 
					}
				}

				if(errorMsgValidazioneXSD.length()==0){
					for(int i=0; i<nodeList.size(); i++){
						String nomeElemento = null;
						String namespaceElemento = null;
						Node n = null;
						Node nodo = null;
						Node nChild = null;
						try{
							n = nodeList.get(i);
							nomeElemento = n.getLocalName();
							namespaceElemento = n.getNamespaceURI();
							if(CostantiRegistroServizi.WSDL_USE_ENCODED.equals(use)){
								this.logger.debug("Validazione XSD con style["+style+"] e use["+use+"], richiede la pulizia dei xsi:types prima della validazione...");
								nodo = this.cleanXSITypes(n);
							}else{
								nodo = n;
							}
									
							if(CostantiRegistroServizi.WSDL_STYLE_RPC.equals(style)){
								this.logger.debug("Validazione XSD con style["+style+"] e use["+use+"] RPC Validation...");
								String nomeAtteso = azione;
								if(isRichiesta==false)
									nomeAtteso = azione+"Response";
								if(nomeAtteso.equals(nomeElemento)==false){
									throw new Exception("Root element ["+nomeElemento+"] non equivale all'operation name "+nomeAtteso +" (RPC Style)");
								}
								// Valido figli
								org.w3c.dom.NodeList nlChilds = nodo.getChildNodes();
								this.logger.debug("Valido figli size: "+nlChilds.getLength());
								for(int j=0; j<nlChilds.getLength(); j++){
									//this.logger.debug("Tipo["+j+"]: "+nlChilds.item(j).getClass().getName());
									//if (nlChilds.item(j) instanceof Text && ((Text) nlChilds.item(j)).getData().trim().length() == 0) { continue; }
									if ( (nlChilds.item(j) instanceof Text) || (nlChilds.item(j) instanceof Comment) ){
										continue;
									}
									nChild = nlChilds.item(j);
									
									// Bug Fix: OPPT-784: Validazione fallisce in presenza di xsi:type e normalizzazione da axiom
									if(this.openspcoop2Message!=null) {
										this.openspcoop2Message.addNamespaceXSITypeIfNotExists(nChild, this.element);
									}
									
									validatoreBodyApplicativo.valida(nChild,true);
								}
							}else{
								this.logger.debug("Validazione XSD con style["+style+"] e use["+use+"] Document Validation...");
								
								// Bug Fix: OPPT-784: Validazione fallisce in presenza di xsi:type e normalizzazione da axiom
								if(this.openspcoop2Message!=null) {
									this.openspcoop2Message.addNamespaceXSITypeIfNotExists(nodo, this.element);
								}
								
								validatoreBodyApplicativo.valida(nodo,true);
							}
							
						}catch(Exception e){
							if(errorMsgValidazioneXSD.length()==0){
								if(this.addPrefixError) {
									if(isRichiesta) {
										errorMsgValidazioneXSD.append("Request");
									}
									else {
										errorMsgValidazioneXSD.append("Response");
									}
									errorMsgValidazioneXSD.append("content not conform to XSD specification\n");
								}
							}else{
								errorMsgValidazioneXSD.append("\n");
							}
							if(namespaceElemento!=null){
								nomeElemento = "{"+namespaceElemento+"}"+nomeElemento;
							}
							errorMsgValidazioneXSD.append("(element "+nomeElemento+") "+e.getMessage());
							
							String elementNonValidato = null;
							try{
								if(n!=null) {
									elementNonValidato = this.xmlUtils.toString(n);
								}
							}catch(Exception eString){
								this.logger.error("Errore durante la conversione del Node in String: "+eString.getMessage(),eString);
							}
							
							String elementNonValidato_cleanXSIType = null;
							try{
								if(nodo!=null) {
									elementNonValidato_cleanXSIType = this.xmlUtils.toString(nodo);
								}
							}catch(Exception eString){
								this.logger.error("Errore durante la conversione del Node (clean xsiType) in String: "+eString.getMessage(),eString);
							}
							
							String elementNonValidato_child = null;
							try{
								if(nChild!=null)
									elementNonValidato_child = this.xmlUtils.toString(nChild);
							}catch(Exception eString){
								this.logger.error("Errore durante la conversione del Node (clean xsiType) in String: "+eString.getMessage(),eString);
							}
							
							this.logger.error("Validazione fallita (elemento "+nomeElemento+") originale["+
									elementNonValidato+"] cleanXsiType["+
									elementNonValidato_cleanXSIType+"] nChild["+
									elementNonValidato_child+"]: "+e.getMessage(),e);
						}
					}
				}
			}
		}
		if(errorMsgValidazioneXSD.length()>0){
			throw new WSDLValidatorException(errorMsgValidazioneXSD.toString());
		}
	}

	
	
	
	
	
	
	
	
	
	
	/* -------------- VALIDAZIONE XSD CON WSDL --------------------- */
	
	/**
	 * Validazione WSDL
	 * 
	 * @param isRichiesta Indicazione sul tipo di messaggio applicativo da gestire
	 * 
	 */
	private PortType ptWsdlConformanceCheck = null;
	private Operation opWsdlConformanceCheck = null;
	public PortType getPtWsdlConformanceCheck() {
		return this.ptWsdlConformanceCheck;
	}
	public Operation getOpWsdlConformanceCheck() {
		return this.opWsdlConformanceCheck;
	}
//	public void wsdlConformanceCheck(boolean isRichiesta,String soapAction,String operationName) throws WSDLValidatorException {
//		this.wsdlConformanceCheck(isRichiesta, soapAction, operationName, true, false);
//	}
	public void wsdlConformanceCheck(boolean isRichiesta,String soapAction,String operationName,boolean throwSOAPActionException,boolean logErrorAsDebug) throws WSDLValidatorException {
				
		String portType = this.accordoServizioWrapper.getNomePortType();
		
		if(portType!=null){
			
			// l'accordo di servizio parte specifica e' stato collegato ad un port type nella parte comune.
			// In tal caso uso la validazione ottimale rispetto al port type ed all'operation
			
			// l'azione busta deve essere obbligatoriamente presente.
			if(operationName==null){
				throw new WSDLValidatorException("Operation name undefined");
			}
			
			this.logger.info("WSDL, effettuo validazione wsdlConformanceCheck ottimale sia con port type che operation ...");
			this._engineWsdlConformanceCheck(isRichiesta, soapAction, portType, operationName, throwSOAPActionException, logErrorAsDebug);
			
			// se la validazione ha avuto successo salvo il pt e l'operation
			this.ptWsdlConformanceCheck = this.accordoServizioWrapper.getPortType(portType);
			for(int i=0; i<this.ptWsdlConformanceCheck.sizeAzioneList(); i++){
				Operation operationAS = this.ptWsdlConformanceCheck.getAzione(i);
				if (operationAS.getNome().equals(operationName)) {
					this.opWsdlConformanceCheck = operationAS;
					break;
				}
			}
		}
		else{
			
			// l'accordo di servizio parte specifica non e' stato collegato ad un port type nella parte comune.	
			// effettuo una validazione meno stringente.
				
			if(operationName!=null){
				
				// *** cerco l'operation all'interno del wsdl. Se esiste una o piu' operation con tale nome, proviamo a validare il messaggio. ***
				this.logger.info("WSDL, effettuo validazione wsdlConformanceCheck cercando una qualche operation con nome ["+operationName+"]...");
				StringBuilder bfEccezione = new StringBuilder();
				boolean operationFound = _engineWsdlConformanceCheckAll(isRichiesta, soapAction, operationName, bfEccezione, throwSOAPActionException, logErrorAsDebug);
				if(operationFound){
					if(bfEccezione.length()>0){
						throw new WSDLValidatorException(bfEccezione.toString());
					}
					else{
						return;
					}
				}else{
					try{
						IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
						String uriAccordo = idAccordoFactory.getUriFromIDAccordo(this.accordoServizioWrapper.getIdAccordoServizio());
						if(this.accordoServizioWrapper.isPortTypesLoadedFromWSDL()){
							throw new WSDLValidatorException("Operation '"+operationName+"' undefined in the WSDL specification '"+uriAccordo+"'");
						}else{
							throw new WSDLValidatorException("Operation '"+operationName+"' undefined in the API specification '"+uriAccordo+"'");
						}
					}catch(DriverRegistroServiziException de){
						String msgErrore = "Errore durante la registrazione del messaggio di errore Operation ["+operationName+"] non trovata in alcun port-type esistente nei wsdl registrati all'interno dell'accordo di servizio";
						if(logErrorAsDebug){
							this.logger.debug(msgErrore);
						}else{
							this.logger.error(msgErrore,de);
						}
						if(this.accordoServizioWrapper.isPortTypesLoadedFromWSDL()){
							throw new WSDLValidatorException("Operation '"+operationName+"' undefined in the WSDL specification");
						}else{
							throw new WSDLValidatorException("Operation '"+operationName+"' undefined in the API specification");
						}
					}
				}
			}
			else{
			
				// *** 
				//     non avendo trovato una azione che matchava con il nome dell'operation name, 
				//     valido rispetto a tutti i messaggi possibili, rispettando pero' il ruolo tra richiesta e risposta 
				//  ***		
				StringBuilder bfEccezione = new StringBuilder();
				this.logger.info("WSDL, effettuo validazione wsdlConformanceCheck utilizzando una qualunque operation ...");
				_engineWsdlConformanceCheckAll(isRichiesta, soapAction, null, bfEccezione, throwSOAPActionException, logErrorAsDebug);
				if(bfEccezione.length()>0){
					throw new WSDLValidatorException(bfEccezione.toString());
				}
				else{
					return;
				}
				
			}
			
		}
		
	}
	
	private boolean _engineWsdlConformanceCheckAll(boolean isRichiesta,String soapAction,String operationName, StringBuilder bfEccezione,boolean throwSOAPActionException,boolean logErrorAsDebug) throws WSDLValidatorException{
		
		try{
		
			IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
			String uriAccordo = idAccordoFactory.getUriFromIDAccordo(this.accordoServizioWrapper.getIdAccordoServizio());
			
			PortType [] pts = this.accordoServizioWrapper.getPortTypeList();
			if(pts==null || pts.length<=0){
				if(this.accordoServizioWrapper.isPortTypesLoadedFromWSDL()){
					throw new WSDLValidatorException("PortTypes undefined in the WSDL specification '"+uriAccordo+"'");
				}else{
					throw new WSDLValidatorException("PortTypes undefined in the API specification '"+uriAccordo+"'");
				}
			}
			boolean operationFound = false;
			for (int i = 0; i < pts.length; i++) {
				PortType pt = pts[i];
				List<Operation> ops = pt.getAzioneList();
				if(ops==null || ops.size()<=0){
					if(this.accordoServizioWrapper.isPortTypesLoadedFromWSDL()){
						throw new WSDLValidatorException("Operations undefined in PortType '"+pt.getNome()+"' of the WSDL specification '"+uriAccordo+"'");
					}else{
						throw new WSDLValidatorException("Operations undefined in Service '"+pt.getNome()+"' of the API specification '"+uriAccordo+"'");
					}
				}
				boolean validazioneCompletataConSuccesso = false;
				for (Operation operation : ops) {
					
					boolean verify = false;
					if(operationName==null){
						verify = true;
					}
					else{
						verify = (operation.getNome().equals(operationName));
						if(verify){
							operationFound = true;
						}
					}
					
					if(verify){
						try{
							this.logger.info("WSDL, effettuo validazione wsdlConformanceCheck per operation ["+operation.getNome()+"] del port type ["+pt.getNome()+"]...");
							this._engineWsdlConformanceCheck(isRichiesta, soapAction, pt.getNome(), operation.getNome(), throwSOAPActionException, logErrorAsDebug);
							
							// se la validazione ha avuto successo salvo il pt e l'operation
							this.ptWsdlConformanceCheck = pt;
							this.opWsdlConformanceCheck = operation;
							
							validazioneCompletataConSuccesso = true;
							break;
							
						}catch(WSDLValidatorException exception){
//							if(bfEccezione.length()>0){
							bfEccezione.append("\n");
							//}
							bfEccezione.append("[Tentativo validazione come PortType:").
								append(pt.getNome()).
								append(" Operation:").
								append(operation.getNome()).
								append(" fallito]: ").
								append(exception.getMessage());
						}
					}
				}
				
				if(validazioneCompletataConSuccesso){
					break;
				}
			}
			
			return operationFound;
			
		}catch(WSDLValidatorException e){
			throw e;
		}catch(Exception e){
			// Si entra in questo catch solo in caso di bug
			String msgErrore = "Validazione WSDL ("+isRichiesta+") fallita: "+e.getMessage();
			if(logErrorAsDebug){
				this.logger.debug(msgErrore);
			}else{
				this.logger.error(msgErrore,e);
			}
			throw new WSDLValidatorException("WSDL Validation 'all-"+(isRichiesta?"request":"response")+"' failed: "+e.getMessage(),e);
		}
	}
	
	private void _engineWsdlConformanceCheck(boolean isRichiesta,String soapAction,String portType,String operation,boolean throwSOAPActionException,boolean logErrorAsDebug) throws WSDLValidatorException {
		
		String errorMsgValidazioneXSD = null;
		try{
			
			SOAPEnvelope envelope = null;
			SOAPBody body = null;
			if(MessageType.SOAP_11.equals(this.messageType) || MessageType.SOAP_12.equals(this.messageType)){
				envelope = (SOAPEnvelope) this.element;
				try{
					body = envelope.getBody();
				}catch(Exception e){
					// eccezione che non dovrebbe accadere. Lo stesso controllo viene fatto nel costruttore
					throw new RuntimeException(e.getMessage(),e);
				}
			}
			else{
				throw new Exception("Tipo di validazione non supportata con Service Binding REST e tipologia messaggio: "+this.messageType.getMessageVersionAsString());
			}
			
	
			// cerco port-type
			BindingStyle style = CostantiRegistroServizi.WSDL_STYLE_DOCUMENT;
			BindingUse use = CostantiRegistroServizi.WSDL_USE_LITERAL;
			String namespaceRPC = null;
			Node rpcElement = null;
			IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
			String uriAccordo = idAccordoFactory.getUriFromIDAccordo(this.accordoServizioWrapper.getIdAccordoServizio());
			PortType portTypeAS = this.accordoServizioWrapper.getPortType(portType);
			if(portTypeAS==null){
				if(this.accordoServizioWrapper.isPortTypesLoadedFromWSDL()){
					throw new WSDLValidatorException("PortType ["+portType+"] undefined in the WSDL specification '"+uriAccordo+"'");
				}else{
					throw new WSDLValidatorException("Service ["+portType+"] undefined in the API specification '"+uriAccordo+"'");
				}
			}
			if(portTypeAS.getStyle()!=null && ("".equals(portTypeAS.getStyle().getValue())==false) && 
					CostantiRegistroServizi.WSDL_STYLE_RPC.equals(portTypeAS.getStyle()))
				style = CostantiRegistroServizi.WSDL_STYLE_RPC;
			
			//si itera sulle operazioni del portType perche' potrebbe esserci
			//overload di operazioni
			if(portTypeAS.sizeAzioneList()<=0)
				throw new Exception("operations per il port type ["+portType+"] non presenti");
			boolean matchingNameOperation = false, matchingArgomentsOperation = false;
			String soapActionWSDL = null;
			StringBuilder eccezioni = new StringBuilder();
			StringBuilder eccezioneActionMatch = new StringBuilder();
			for(int i=0; i<portTypeAS.sizeAzioneList(); i++){
				Operation operationAS = portTypeAS.getAzione(i);
				if (operationAS.getNome().equals(operation)) {
					matchingNameOperation = true;
					// Prendo la definizione del messaggio di input se e' una richiesta, di output se e' una risposta
					Message argumentsOperation = isRichiesta ? operationAS.getMessageInput() : operationAS.getMessageOutput();
					
					if(operationAS.getStyle()!=null && ("".equals(operationAS.getStyle().getValue())==false)){
						if(CostantiRegistroServizi.WSDL_STYLE_RPC.equals(operationAS.getStyle()))
							style = CostantiRegistroServizi.WSDL_STYLE_RPC;
						else if(CostantiRegistroServizi.WSDL_STYLE_DOCUMENT.equals(operationAS.getStyle()))
							style = CostantiRegistroServizi.WSDL_STYLE_DOCUMENT;
					}
					
					if(argumentsOperation!=null && argumentsOperation.getUse()!=null &&
							("".equals(argumentsOperation.getUse().getValue())==false) &&
							CostantiRegistroServizi.WSDL_USE_ENCODED.equals(argumentsOperation.getUse()))
						use = CostantiRegistroServizi.WSDL_USE_ENCODED; 
					
					if(CostantiRegistroServizi.WSDL_STYLE_RPC.equals(style)){
						if(argumentsOperation!=null && argumentsOperation.getSoapNamespace()!=null &&
								(!"".equals(argumentsOperation.getSoapNamespace()))){
							namespaceRPC = argumentsOperation.getSoapNamespace();
						}
					}
										
					this.logger.debug("WSDL, esamino operation["+operation+"] con style["+style+"] e use["+use+"] (RPCNamespace:"+namespaceRPC+") ...");
					
					// il controllo sul nome dell'operation non basta.
					// Vi puo' essere overriding del metodo per parametri diversi
					// controllo matching dei parametri 
					org.w3c.dom.NodeList nodiContenutoApplicativo = null;
					Node nodoPossiedeContenutoApplicativo = null; // body nel caso document, rpc-element nel caso rpc
					if(CostantiRegistroServizi.WSDL_STYLE_RPC.equals(style)){
						// RPC
						String nomeAtteso = operation;
						if(isRichiesta==false)
							nomeAtteso = operation+"Response";
						SOAPElement childRPCElement = SoapUtils.getNotEmptyFirstChildSOAPElement(body);
						if(childRPCElement==null){
							this.logger.debug("WSDL, esamino operation["+operation+"] con style["+style+"] e use["+use+"]: Root element RCP non trovato rispetto all'operation name "+nomeAtteso +" (RPC Style)");
							continue;
						}
						if(nomeAtteso.equals(childRPCElement.getLocalName())==false){
							this.logger.debug("WSDL, esamino operation["+operation+"] con style["+style+"] e use["+use+"]: Root element ["+childRPCElement.getLocalName()+"] non equivale all'operation name "+nomeAtteso +" (RPC Style)");
							continue;
						}
						
						nodiContenutoApplicativo = body.getChildNodes();
						for(int ii=0;ii<nodiContenutoApplicativo.getLength();ii++){
//							if (!(nodiContenutoApplicativo.item(ii) instanceof Text && 
//									((Text) nodiContenutoApplicativo.item(ii)).getData().trim().length() == 0)) { 
							if (! ( (nodiContenutoApplicativo.item(ii) instanceof Text) || (nodiContenutoApplicativo.item(ii) instanceof Comment) )){
								nodoPossiedeContenutoApplicativo = nodiContenutoApplicativo.item(ii);
								rpcElement = nodoPossiedeContenutoApplicativo;
								nodiContenutoApplicativo = nodoPossiedeContenutoApplicativo.getChildNodes();
								break;
							}
						}
						
					}else{
						// Document
						nodiContenutoApplicativo = body.getChildNodes();
						nodoPossiedeContenutoApplicativo = body;
					}
					
					int sizeArgumentsOperation = 0;
					if(argumentsOperation!=null){
						sizeArgumentsOperation = argumentsOperation.sizePartList();
					}
					int nodiContenutoApplicativoLength = 0;
					StringBuilder nodiMessaggioErrore = new StringBuilder();
					for(int ii=0;ii<nodiContenutoApplicativo.getLength();ii++){
//						if (!(nodiContenutoApplicativo.item(ii) instanceof Text && 
//								((Text) nodiContenutoApplicativo.item(ii)).getData().trim().length() == 0)) { 
						if (! ( (nodiContenutoApplicativo.item(ii) instanceof Text) || (nodiContenutoApplicativo.item(ii) instanceof Comment) )){
							
							if(nodiMessaggioErrore.length()>0){
								nodiMessaggioErrore.append(", ");
							}
							Node n = nodiContenutoApplicativo.item(ii);
							RootElementBody rootElementBody = new RootElementBody(envelope, nodoPossiedeContenutoApplicativo, 
									CostantiRegistroServizi.WSDL_STYLE_RPC.equals(style), n);
							nodiMessaggioErrore.append(rootElementBody.toString());
							
							nodiContenutoApplicativoLength++;
							continue;
						}
					}
					if(sizeArgumentsOperation!=nodiContenutoApplicativoLength){
						
						if(eccezioneActionMatch.length()>0){
							eccezioneActionMatch.append("\n");
						}
						eccezioneActionMatch.append("Found "+nodiContenutoApplicativoLength+" parameter"+(nodiContenutoApplicativoLength>1?"s":"")+": ");
						eccezioneActionMatch.append(nodiMessaggioErrore.toString());
						
						this.logger.debug("WSDL, esamino operation["+operation+"] con style["+style+"] e use["+use+"]: Argomenti attesi["+sizeArgumentsOperation+"], trovati nel body["+nodiContenutoApplicativo.getLength()+"]");
						continue;
					}
										
					String tipo  = "output";
					if(isRichiesta)
						tipo = "input";
					
					if(argumentsOperation!=null && argumentsOperation.sizePartList()>0){
						
						// Mi conservo gli elementi presenti nel body
						List<RootElementBody> elementRootBody = new ArrayList<RootElementBody>();
						StringBuilder bodyElements = new StringBuilder();
						int numeroBodyElements = 0;
						int realIndexBody = 0;
						for(int indexBody = 0 ; indexBody<nodiContenutoApplicativo.getLength(); indexBody++){
//							if (nodiContenutoApplicativo.item(indexBody) instanceof Text && 
//									((Text) nodiContenutoApplicativo.item(indexBody)).getData().trim().length() == 0) { 
							if ( (nodiContenutoApplicativo.item(indexBody) instanceof Text) || (nodiContenutoApplicativo.item(indexBody) instanceof Comment) ){
								continue;
							}
							
							Node n = nodiContenutoApplicativo.item(indexBody);
							
							RootElementBody rootElementBody = new RootElementBody(envelope, nodoPossiedeContenutoApplicativo, 
									CostantiRegistroServizi.WSDL_STYLE_RPC.equals(style), n);
							elementRootBody.add(rootElementBody);
							
							if(realIndexBody>0)
								bodyElements.append(",");
							realIndexBody++;
							bodyElements.append(rootElementBody.toString());
						}
						numeroBodyElements = elementRootBody.size();
						
						int soapBodyArguments = nodiContenutoApplicativoLength;
						//per ogni tipo si itera
						int wsdlIndex=0;
						for( ; wsdlIndex<argumentsOperation.sizePartList(); wsdlIndex++){
							if (wsdlIndex == soapBodyArguments) { 
								//più oggetti definiti nel WSDL di quanti ce ne siano nel messaggio
								this.logger.debug("WSDL, esamino operation["+operation+"] con style["+style+"] e use["+use+"]: Più oggetti definiti nel WSDL di quanti ce ne siano nel messaggio");
								continue;
							}
							MessagePart argument = argumentsOperation.getPart(wsdlIndex);
							String nomeElementAtteso = null;
							String namespaceElementAtteso = null;
							String tipoXSIAtteso = null;
							boolean validazioneTipologiaElement = (argument.getElementName()!=null);
							String argomentoAtteso = null;
							if(argument.getElementName()!=null){
								nomeElementAtteso = argument.getElementName();
								namespaceElementAtteso = argument.getElementNamespace();
							}
							else{
								nomeElementAtteso = argument.getName(); // definito nel message del wsdl
								namespaceElementAtteso = argument.getTypeNamespace();
								tipoXSIAtteso = argument.getTypeName();
							}
							argomentoAtteso = RootElementBody.toString(nomeElementAtteso, namespaceElementAtteso, tipoXSIAtteso);
							boolean find = false;
							for(int indexBody = 0 ; indexBody<elementRootBody.size(); indexBody++){
								RootElementBody r = elementRootBody.get(indexBody);
								if(validazioneTipologiaElement){
									if(nomeElementAtteso.equals(r.getLocalName()) && namespaceElementAtteso.equals(r.getNamespace())){
										find = true;
										elementRootBody.remove(indexBody);
										continue;
									}
									else if(CostantiRegistroServizi.WSDL_USE_ENCODED.equals(use)){
										if(nomeElementAtteso.equals(r.getLocalName()) && namespaceElementAtteso.equals(r.getNamespaceElementoCheContieneXSIType())){
											find = true;
											elementRootBody.remove(indexBody);
											continue;
										}
									}
								}
								else{
									if(nomeElementAtteso.equals(r.getLocalName()) && 
											namespaceElementAtteso.equals(r.getNamespace()) &&
											tipoXSIAtteso.equals(r.getXsiType())){
										find = true;
										elementRootBody.remove(indexBody);
										continue;
									}
								}
							}
	
							if (!find) { //tipi non concordi nella sequenza
								boolean error = true;
								if(CostantiRegistroServizi.WSDL_STYLE_RPC.equals(style) && CostantiRegistroServizi.WSDL_USE_LITERAL.equals(use) &&
										tipoXSIAtteso!=null &&
										elementRootBody.size()==1 && // elementi nel body sono uno
										argumentsOperation.sizePartList()==1 // elementi attesi nel wsdl uno 
										){
									
									SOAPElement rpcOperation = SoapUtils.getNotEmptyFirstChildSOAPElement(body);
									if(rpcOperation!=null){
										SOAPElement childRpc = SoapUtils.getNotEmptyFirstChildSOAPElement(rpcOperation);
										if(this.gestioneXsiType_rpcLiteral && 
												childRpc!=null && nomeElementAtteso!=null && nomeElementAtteso.equals(childRpc.getLocalName())){
											try{
//												System.out.println("PRIMA: "+org.openspcoop2.message.OpenSPCoop2MessageFactory.getMessageFactory().createEmptySOAPMessage(SOAPVersion.SOAP11).
//														getAsString(childRpc, false));
												
												QName namespaceId = new QName("xmlns:"+PREFIX_RPC_AGGIUNTO);
												childRpc.addAttribute(namespaceId, namespaceElementAtteso);
												this.rpcChildElementNamespaceAggiunto = namespaceId;
											
												QName id = new QName(XMLSCHEMA_INSTANCE_NAMESPACE,XMLSCHEMA_INSTANCE_LOCAL_NAME_TYPE,"xsi");
												String value = PREFIX_RPC_AGGIUNTO+":"+tipoXSIAtteso;
												childRpc.addAttribute(id, value);
												this.rpcChildElementXSITypeAggiunto = id;
											
//												System.out.println("DOPO: "+org.openspcoop2.message.OpenSPCoop2MessageFactory.getMessageFactory().createEmptySOAPMessage(SOAPVersion.SOAP11).
//														getAsString(childRpc, false));
												error= false;
												this.rpcChildElement = childRpc;
											}catch(Exception e){
												this.logger.error("Errore durante la registrazione degli attributi richiesti per la validazione rpc con type: "+e.getMessage(),e);
												try{
													if(this.rpcChildElementNamespaceAggiunto!=null){
														// ripulisco
														// alcune implementazioni usano l'uno o l'altro per eliminarlo
														this.rpcChildElement.removeAttribute(this.rpcChildElementNamespaceAggiunto);
														this.rpcChildElement.removeNamespaceDeclaration(PREFIX_RPC_AGGIUNTO);
													}
												}catch(Exception eClose){
													// ignore
												}
											}
										}
									}
								}
								if(error){
									eccezioni.append("\nRequired "+tipo+" parameter '"+argomentoAtteso+"' undefined in "+numeroBodyElements+" body root-element("+(numeroBodyElements>1?"s":"")+") founded: "+bodyElements);
									this.logger.debug("WSDL, esamino operation["+operation+"] con style["+style+"] e use["+use+"]: Atteso "+argomentoAtteso+" body "+bodyElements);
									break;
								}
							}
						}
						if (wsdlIndex == soapBodyArguments) {
							soapActionWSDL = operationAS.getSoapAction();
							matchingArgomentsOperation = true;
							continue;
						}
					}

				} //fine if
			}

			if (!matchingArgomentsOperation) {
				if (matchingNameOperation) {
					if(eccezioni.length()>0){
						if(this.accordoServizioWrapper.isPortTypesLoadedFromWSDL()){
							throw new WSDLValidatorException("Invalid "+(isRichiesta?"request":"response")+" by WSDL specification '"+uriAccordo+"' (port-type:"+portType+", operation:"+operation+", style:"+style+", use:"+use+"): "+eccezioni.toString());
						}else{
							throw new WSDLValidatorException("Invalid "+(isRichiesta?"request":"response")+" by API specification '"+uriAccordo+"' (service:"+portType+", operation:"+operation+", style:"+style+", use:"+use+"): "+eccezioni.toString());
						}
					}
					else if(eccezioneActionMatch.length()>0){
						StringBuilder bfMessage = new StringBuilder();
						for(int i=0; i<portTypeAS.sizeAzioneList(); i++){
							Operation operationAS = portTypeAS.getAzione(i);
							if (operationAS.getNome().equals(operation)) {
								Message argumentsOperation = isRichiesta ? operationAS.getMessageInput() : operationAS.getMessageOutput();
								int length = 0;
								if(argumentsOperation!=null && argumentsOperation.getPartList()!=null){
									length = argumentsOperation.getPartList().size();
								}
								bfMessage.append("\n");
								bfMessage.append("Expected "+length+" parameter"+(length>1?"s":"")+": ");
								if(length>0){
									for (int j = 0; j < length; j++) {
										MessagePart argument = argumentsOperation.getPart(j);
										String nomeElementAtteso = null;
										String namespaceElementAtteso = null;
										String tipoXSIAtteso = null;
										String argomentoAtteso = null;
										if(argument.getElementName()!=null){
											nomeElementAtteso = argument.getElementName();
											namespaceElementAtteso = argument.getElementNamespace();
										}
										else{
											nomeElementAtteso = argument.getName(); // definito nel message del wsdl
											namespaceElementAtteso = argument.getTypeNamespace();
											tipoXSIAtteso = argument.getTypeName();
										}
										argomentoAtteso = RootElementBody.toString(nomeElementAtteso, namespaceElementAtteso, tipoXSIAtteso);
										if(j>0){
											bfMessage.append(", ");
										}
										bfMessage.append(argomentoAtteso);
									}
								}
							}
						}
						bfMessage.append("\n").append(eccezioneActionMatch.toString());

						if(this.accordoServizioWrapper.isPortTypesLoadedFromWSDL()){
							throw new WSDLValidatorException("Invalid "+(isRichiesta?"request":"response")+" by WSDL specification '"+uriAccordo+"' (port-type:"+portType+", operation:"+operation+", style:"+style+", use:"+use+"): "+bfMessage.toString());
						}else{
							throw new WSDLValidatorException("Invalid "+(isRichiesta?"request":"response")+" by API specification '"+uriAccordo+"' (service:"+portType+", operation:"+operation+", style:"+style+", use:"+use+"): "+bfMessage.toString());
						}

					}
					else{
						if(this.accordoServizioWrapper.isPortTypesLoadedFromWSDL()){
							throw new WSDLValidatorException("Invalid "+(isRichiesta?"request":"response")+" by WSDL specification '"+uriAccordo+"' (port-type:"+portType+", operation:"+operation+", style:"+style+", use:"+use+")");
						}else{
							throw new WSDLValidatorException("Invalid "+(isRichiesta?"request":"response")+" by API specification '"+uriAccordo+"' (service:"+portType+", operation:"+operation+", style:"+style+", use:"+use+")");
						}
					}
				} else {
					if(this.accordoServizioWrapper.isPortTypesLoadedFromWSDL()){
						throw new WSDLValidatorException("Operation '"+operation+"' undefined in PortType '"+portType+"' of the WSDL specification '"+uriAccordo+"'");
					}else{
						throw new WSDLValidatorException("Operation '"+operation+"' undefined in Service '"+portType+"' of the API specification '"+uriAccordo+"'");
					}
				}
			}
			
			if( isRichiesta && soapActionWSDL!=null){
				this.logger.debug("CheckSOAPAction");
				String soapActionRipulita = null;
				if(soapAction!=null){
					soapActionRipulita = soapAction.trim();
					if(soapActionRipulita.startsWith("\"")){
						soapActionRipulita = soapActionRipulita.substring(1);
					}
					if(soapActionRipulita.endsWith("\"")){
						soapActionRipulita = soapActionRipulita.substring(0,(soapActionRipulita.length()-1));
					}
				}
				if(soapActionWSDL.equalsIgnoreCase(soapActionRipulita)==false){
					boolean tmpThrowSOAPActionException = throwSOAPActionException;
					if(soapActionRipulita==null && MessageType.SOAP_12.equals(this.messageType)){
						// The SOAP 1.1 mandatory SOAPAction HTTP header has been removed in SOAP 1.2. In its place is an optional action parameter on the application/soap+xml media type.
						// Quindi se nella richiesta non era presente una soapAction, non devo sollevare eccezione
						tmpThrowSOAPActionException = false;
					}
					// Validazione SOAPAction
					if(tmpThrowSOAPActionException){
						if(this.accordoServizioWrapper.isPortTypesLoadedFromWSDL()){
							throw new WSDLValidatorException("Invalid soap action '"+soapActionRipulita+"' by WSDL specification '"+uriAccordo+"' (port-type:"+portType+", operation:"+operation+", soap action:"+soapActionWSDL+")");
						}else{
							throw new WSDLValidatorException("Invalid soap action '"+soapActionRipulita+"' by API specification '"+uriAccordo+"' (service:"+portType+", operation:"+operation+", soap action:"+soapActionWSDL+")");
						}
					}
				}
			}
			
			// Il controlllo sul namespaceRPC è opzionale.
			// Viene effettuato in caso di validazione 'openspcoop' solo se dichiarato.
			if(namespaceRPC!=null && rpcElement!=null){
				this.logger.debug("CheckRPCNamespace");
				if(rpcElement.getNamespaceURI()==null){
					if(this.accordoServizioWrapper.isPortTypesLoadedFromWSDL()){
						throw new WSDLValidatorException("Unqualified rpc "+(isRichiesta?"request":"response")+" element '"+rpcElement.getLocalName()+"' by WSDL specification '"+uriAccordo+"' (port-type:"+portType+", operation:"+operation+")");
					}else{
						throw new WSDLValidatorException("Unqualified rpc "+(isRichiesta?"request":"response")+" element '"+rpcElement.getLocalName()+"' by API specification '"+uriAccordo+"' (service:"+portType+", operation:"+operation+")");
					}
				}
				if(!rpcElement.getNamespaceURI().equals(namespaceRPC)){
					if(this.accordoServizioWrapper.isPortTypesLoadedFromWSDL()){
						throw new WSDLValidatorException("Invalid rpc "+(isRichiesta?"request":"response")+" element '"+rpcElement.getLocalName()+"' by WSDL specification '"+uriAccordo+"' (port-type:"+portType+", operation:"+operation+"): expected namespace '"+namespaceRPC+"'; found namespace '"+rpcElement.getNamespaceURI()+"'");
					}else{
						throw new WSDLValidatorException("Invalid rpc "+(isRichiesta?"request":"response")+" element '"+rpcElement.getLocalName()+"' by API specification '"+uriAccordo+"' (service:"+portType+", operation:"+operation+"): expected namespace '"+namespaceRPC+"'; found namespace '"+rpcElement.getNamespaceURI()+"'");
					}
				}
			}
			
		}catch(Exception e){
			String msgErrore = "Validazione WSDL ("+isRichiesta+") fallita: "+e.getMessage();
			if(logErrorAsDebug){
				this.logger.debug(msgErrore);
			}else{
				this.logger.error(msgErrore,e);
			}
			if( (e instanceof WSDLValidatorException)==false )
				this.logger.debug("Validazione WSDL fallita",e);
			// è gia nella definizione dell'error
			StringBuilder errorMsgValidazioneXSDBuilder = new StringBuilder();
			if(this.addPrefixError) {
				if(isRichiesta) {
					errorMsgValidazioneXSDBuilder.append("Request");
				}
				else {
					errorMsgValidazioneXSDBuilder.append("Response");
				}
				errorMsgValidazioneXSDBuilder.append("content not conform to ").append(this.accordoServizioWrapper.isPortTypesLoadedFromWSDL()?"WSDL":"API").append(" specification; ");
			}
			errorMsgValidazioneXSDBuilder.append(e.getMessage());
			errorMsgValidazioneXSD = errorMsgValidazioneXSDBuilder.toString();
		}
		if(errorMsgValidazioneXSD!=null){
			throw new WSDLValidatorException(errorMsgValidazioneXSD);
		}
		
	}
	
	
	
	
	
	
	
	
	
	/* -------------- VALIDAZIONE UTILITIES --------------------- */
	
	public Element cleanXSITypes(Node node) throws Exception{
		
		//La versione non e' rilevante.
		byte[] element = this.eraserType (this.xmlUtils.toByteArray(node,true));
		Document doc = this.xmlUtils.newDocument(element);
		Element domElem = doc.getDocumentElement();
		
		/*  vecchia impl: 
		MessageElement daClonare = (MessageElement) node;
		//System.out.println("PRIMA ["+daClonare.getAsString()+"] ");
		byte[]element = this.eraserType(daClonare.getAsString().getBytes());
		//System.out.println("DOPO ["+new String(element)+"] ");
		Element e =  XMLUtils.newElement(element);
		*/
		
		return domElem;
	}
	/**
	 * Metodo che si occupa di effettuare l'eliminazione degli xsd:type String
	 *
	 * utility che elimina gli xsd type
	 * @param xml Xml su cui effettuare la pulizia dell'header.
	 * @return byte[] contenente un xml 'pulito'.
	 * 
	 */
	public byte[] eraserType(byte[] xml) throws UtilsException{

		ByteArrayOutputStream cleanXML = null;
		String prefix = "";
		try{
			cleanXML = new ByteArrayOutputStream();

			// Elimino import
			for(int i=0; i<xml.length ; ){

				if(xml[i] == ' '){

					// Cerco Stringa  " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
					String importXSITYPE = "http://www.w3.org/2001/XMLSchema-instance"; 
					if(i+"xmlns:".length() < xml.length){
						if( (xml[i+1] == 'x') &&
							(xml[i+2] == 'm') &&
							(xml[i+3] == 'l') &&
							(xml[i+4] == 'n') &&
							(xml[i+5] == 's') &&
							(xml[i+6] == ':')){
							StringBuilder test = new StringBuilder("xmlns:");
							StringBuilder prefixTest = new StringBuilder();
							int contatoreFineAttributo = 0;
							for(int k=7 ; ; k++){
								if((i+k)>=xml.length)
									break;
								if((char)xml[i+k] == '"')
									contatoreFineAttributo++;
								if(contatoreFineAttributo==0)
									prefixTest.append((char)xml[i+k]);
								test.append((char)xml[i+k]);
								if(contatoreFineAttributo>=2)
									break;
							}
							//System.out.println("TROVATO ["+test.toString()+"] PREFIX["+prefixTest.toString()+"]");
							// Prelevo valore
							int indexFirst = test.toString().indexOf('"');
							int secondFirst = test.toString().indexOf('"', indexFirst+1);
							String testEquals = test.toString().substring(indexFirst+1, secondFirst);
							//System.out.println("TROVATO SUBSTRING ["+testEquals+"]");
							if(importXSITYPE.equalsIgnoreCase(testEquals)){
								//System.out.println("CANCELLO");
								prefix = prefixTest.toString().substring(0, prefixTest.length()-1);
								//System.out.println("CANCELLO P["+prefix+"]");
								// Cancello la stringa trovata
								i = i + test.toString().length() +1;
								continue;
							}
						}
					}
					
					cleanXML.write(xml[i]);
					i++;

				}else{
					cleanXML.write(xml[i]);
					i++;
				}

			}
			byte [] risultato = cleanXML.toByteArray();
			cleanXML = new ByteArrayOutputStream();
			//System.out.println("DOPO step 1 ["+new String(risultato)+"] ");
			cleanXML.close();
			
			// Elimino xsi type
			for(int i=0; i<risultato.length ; ){

				if(risultato[i] == ' '){

					// Cerco Stringa  " xsi:type=\"xsd:TYPE\""
					String XSITYPE_PREFIX = prefix+":type=\""; 
					if(i+XSITYPE_PREFIX.length()+2 < risultato.length){
						StringBuilder test = new StringBuilder("");
						int contatoreFineAttributo = 0;
						for(int k=1 ; ; k++){
							if((i+k)>=risultato.length)
								break;
							if((char)risultato[i+k] == '"')
								contatoreFineAttributo++;
							test.append((char)risultato[i+k]);
							if(contatoreFineAttributo>=2)
								break;
						}
						//stem.out.println("TROVATO ["+test.toString()+"] START WITH["+XSITYPE_PREFIX+"]");
						if(test.toString().startsWith(XSITYPE_PREFIX)){
							// Cancello la stringa trovata
							//System.out.println("CANCELLO");
							i = i + test.toString().length()+1;
							continue;
						}
					}
					
					cleanXML.write(risultato[i]);
					i++;

				}else{
					cleanXML.write(risultato[i]);
					i++;
				}

			}
			risultato = cleanXML.toByteArray();
			cleanXML.close();
			
			return risultato;

		} catch(Exception e) {
			this.logger.error("Utilities.eraserType",e);
			try{
				if(cleanXML!=null)
					cleanXML.close();
			}catch(Exception eis){
				// ignore
			}
			throw new UtilsException("Eliminazione xsi:type per validazione non riuscita "+e.getMessage(),e);
		}
	}  
	
	
	
	
	
	

}

class RootElementBody{
	
	private String localName;
	private String namespace;
	private String xsiType;
	
	// Questo elemento viene valorizzato solo nei casi di wsdl encoded dove gli elementi vengono definiti con wsdl:part element e non type.
	private String namespaceElementoCheContieneXSIType;
		
	public RootElementBody(SOAPEnvelope soapEnvelope,Node nodoPadre,boolean rpc, Node n) throws Exception{
		
		NamedNodeMap attributes = n.getAttributes();
		this.localName = n.getLocalName();
		
		// Prima verifico presenza di xsi:types ...
		if(attributes!=null && attributes.getLength()>0){
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attribute = attributes.item(i);
				if(attribute instanceof Attr){
					Attr a = (Attr) attribute;
					//String attrName = a.getName(); // type
					//String attrPrefix = a.getPrefix(); // xsi
					String attrLocalName = a.getLocalName();
					String attrNamespace = a.getNamespaceURI(); // http://www.w3.org/2001/XMLSchema-instance
					String value = a.getNodeValue(); // messaggioSII:esitoProcessMessaggioSIIType
					//System.out.println("ATTRNAME["+attrName+"] ATTRPREFIX["+attrPrefix+"] ATTRNAMESPACE["+attrNamespace+"] VALUE["+value+"]");
					if(WSDLValidator.XMLSCHEMA_INSTANCE_NAMESPACE.equals(attrNamespace) &&
							WSDLValidator.XMLSCHEMA_INSTANCE_LOCAL_NAME_TYPE.equals(attrLocalName) && 
							value!=null){
						String prefix = "";
						String typeName = value;
						if(value.contains(":")){
							prefix = value.split(":")[0];
							typeName = value.split(":")[1];
						}
						this.xsiType = typeName;
						
						// Cerco namespace corrispondente al prefix.
						// 1. cerco prima nel nodo stesso
						this.namespace = this.mappingPrefixToNamespace(n, prefix);
						if(this.namespace==null){
							if(rpc){
								// 2. cerco nel rpc element
								this.namespace = this.mappingPrefixToNamespace(nodoPadre, prefix);
								if(this.namespace==null){
									// 3. cerco nel soap body
									this.namespace = this.mappingPrefixToNamespace(soapEnvelope.getBody(), prefix);
									if(this.namespace==null){
										// 4. cerco nel soap envelope
										this.namespace = this.mappingPrefixToNamespace(soapEnvelope, prefix);
										if(this.namespace==null){
											throw new Exception("[RPCStyle] Namespace (for prefix "+prefix+") not found for element ["+n.getLocalName()+"] with xsi:type=\""+value+"\"");
										}
									}	
								}
							}else{
								// 2. cerco nel soap body (il nodo padre)
								this.namespace = this.mappingPrefixToNamespace(nodoPadre, prefix);
								if(this.namespace==null){
									// 3. cerco nel soap envelope
									this.namespace = this.mappingPrefixToNamespace(soapEnvelope, prefix);
									if(this.namespace==null){
										throw new Exception("[DocumentStyle] Namespace (for prefix "+prefix+") not found for element ["+n.getLocalName()+"] with xsi:type=\""+value+"\"");
									}
								}	
							}
						}
					}
				}
			}
		}
		
		if(this.xsiType==null){
			this.namespace = n.getNamespaceURI();
		}
		else{
			this.namespaceElementoCheContieneXSIType = n.getNamespaceURI();
		}
	}
	
	private String mappingPrefixToNamespace(Node n,String prefix) {
		NamedNodeMap attributes = n.getAttributes();
		if(attributes==null || attributes.getLength()<=0){
			return null;
		}
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if(attribute instanceof Attr){
				Attr a = (Attr) attribute;
				String attrName = a.getName(); // type
				//String attrPrefix = a.getPrefix(); // xsi
				//String attrNamespace = a.getNamespaceURI(); // http://www.w3.org/2001/XMLSchema-instance
				String value = a.getNodeValue(); // messaggioSII:esitoProcessMessaggioSIIType
				//System.out.println("CHECK XMLNS Search["+prefix+"] ATTRNAME["+attrName+"] ATTRPREFIX["+attrPrefix+"] ATTRNAMESPACE["+attrNamespace+"] VALUE["+value+"]");
				
				if(attrName.startsWith("xmlns")){
				
					if(prefix==null || prefix.equals("")){
						if("xmlns".equals(attrName)){
							//System.out.println("FOUND! ["+value+"]");
							return value;
						}
					}
					else{
						if(attrName.equals("xmlns:"+prefix)){
							//System.out.println("FOUND! ["+value+"]");
							return value;
						}
					}
					
				}
				
			}
		}
		return null;
	}
	
	public String getLocalName() {
		return this.localName;
	}
	public void setLocalName(String localName) {
		this.localName = localName;
	}
	public String getNamespace() {
		return this.namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getXsiType() {
		return this.xsiType;
	}
	public void setXsiType(String xsiType) {
		this.xsiType = xsiType;
	}
	public String getNamespaceElementoCheContieneXSIType() {
		return this.namespaceElementoCheContieneXSIType;
	}
	public void setNamespaceElementoCheContieneXSIType(
			String namespaceElementoCheContieneXSIType) {
		this.namespaceElementoCheContieneXSIType = namespaceElementoCheContieneXSIType;
	}
	
	@Override
	public String toString(){
		return RootElementBody.toString(this.localName, this.namespace, this.xsiType, this.namespaceElementoCheContieneXSIType);
	}
	
	public static String toString(String localName,String namespace,String xsiType){
		return toString(localName, namespace, xsiType, null);
	}
	private static String toString(String localName,String namespace,String xsiType,String namespaceElementoCheContieneXSIType){
		StringBuilder bf = new StringBuilder();
		if(xsiType==null){
			bf.append("{");
			bf.append(namespace);
			bf.append("}");
			bf.append(localName);
		}else{
			bf.append("[xsi:type=\"{");
			bf.append(namespace);
			bf.append("\"}"+xsiType+"]");
			// Questo elemento viene valorizzato solo nei casi di wsdl encoded dove gli elementi vengono definiti con wsdl:part element e non type.
			if(namespaceElementoCheContieneXSIType!=null){
				bf.append("{");
				bf.append(namespaceElementoCheContieneXSIType);
				bf.append("}");
			}
			bf.append(localName);
		}
		return bf.toString();
	}
}
