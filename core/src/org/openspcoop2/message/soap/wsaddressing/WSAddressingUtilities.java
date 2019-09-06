/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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



package org.openspcoop2.message.soap.wsaddressing;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.xml.ValidatoreXSD;
import org.slf4j.Logger;


/**
 * Classe contenenti utilities per le integrazioni.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSAddressingUtilities {

	private Logger log;
	public WSAddressingUtilities(Logger log) {
		this.log = log;
	}
	
	
	
	
	// ***** BUILD *****
	
	public WSAddressingHeader build(OpenSPCoop2SoapMessage msg,String actor,  
			WSAddressingValue values) throws MessageException {
		return this.build(msg, actor, false, values);
	}
	public WSAddressingHeader build(OpenSPCoop2SoapMessage msg,String actor, boolean mustUnderstand, 
			WSAddressingValue values) throws MessageException {
		WSAddressingHeader hdr = new WSAddressingHeader();
		if(!StringUtils.isEmpty(values.getTo())) {
			hdr.setTo(buildWSATo(msg, actor, mustUnderstand, values.getTo()));
		}
		if(!StringUtils.isEmpty(values.getFrom())) {
			hdr.setFrom(buildWSAFrom(msg, actor, mustUnderstand, values.getFrom()));
		}
		if(!StringUtils.isEmpty(values.getAction())) {
			hdr.setAction(buildWSAAction(msg, actor, mustUnderstand, values.getAction()));
		}
		if(!StringUtils.isEmpty(values.getId())) {
			hdr.setId(buildWSAID(msg, actor, mustUnderstand, values.getId()));
		}
		if(!StringUtils.isEmpty(values.getRelatesTo())) {
			hdr.setRelatesTo(buildWSARelatesTo(msg, actor, mustUnderstand, values.getRelatesTo()));
		}
		if(!StringUtils.isEmpty(values.getReplyTo())) {
			hdr.setReplyTo(buildWSAReplyTo(msg, actor, mustUnderstand, values.getReplyTo()));
		}
		if(!StringUtils.isEmpty(values.getFaultTo())) {
			hdr.setFaultTo(buildWSAFaultTo(msg, actor, mustUnderstand, values.getFaultTo()));
		}
		return hdr;
	}
	
	public void addHeader(WSAddressingHeader headerWSAddressing, OpenSPCoop2SoapMessage msg) throws MessageException{
		
		try {
		
			if(headerWSAddressing!=null && msg!=null) {
				
				SOAPHeader header = msg.getSOAPHeader();
				if(header==null){
					header = msg.getSOAPPart().getEnvelope().addHeader();
				}
				
				if(headerWSAddressing.getTo()!=null) {
					msg.addHeaderElement(header, headerWSAddressing.getTo());
				}
				if(headerWSAddressing.getFrom()!=null) {
					msg.addHeaderElement(header, headerWSAddressing.getFrom());
				}
				if(headerWSAddressing.getAction()!=null) {
					msg.addHeaderElement(header, headerWSAddressing.getAction());
				}
				if(headerWSAddressing.getId()!=null) {
					msg.addHeaderElement(header, headerWSAddressing.getId());
				}
				if(headerWSAddressing.getRelatesTo()!=null) {
					msg.addHeaderElement(header, headerWSAddressing.getRelatesTo());
				}
				if(headerWSAddressing.getReplyTo()!=null) {
					msg.addHeaderElement(header, headerWSAddressing.getReplyTo());
				}
				if(headerWSAddressing.getFaultTo()!=null) {
					msg.addHeaderElement(header, headerWSAddressing.getFaultTo());
				}
			}
			
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}
		
	}
	
	
	
	// ***** BUILD STATIC UTILS *****
	
	private final static boolean BUILD_VALUE_AS_EPR = true;
	private final static boolean BUILD_VALUE_RAW = false;
	
	public static SOAPHeaderElement buildWSATo(OpenSPCoop2SoapMessage msg,String actor,boolean mustUnderstand, String value) throws MessageException{
		QName name =  new QName(Costanti.WSA_NAMESPACE,Costanti.WSA_SOAP_HEADER_TO,Costanti.WSA_PREFIX);
		SOAPHeaderElement header = WSAddressingUtilities.buildHeaderElement(msg,name,
				value,actor,mustUnderstand, BUILD_VALUE_RAW);
		return header;
	}
	
	public static SOAPHeaderElement buildWSAFrom(OpenSPCoop2SoapMessage msg,String actor,boolean mustUnderstand, String value) throws MessageException{
		QName name =  new QName(Costanti.WSA_NAMESPACE,Costanti.WSA_SOAP_HEADER_FROM,Costanti.WSA_PREFIX);
		SOAPHeaderElement header = WSAddressingUtilities.buildHeaderElement(msg,name,
				value,actor,mustUnderstand, BUILD_VALUE_AS_EPR);
		return header;
	}
	
	public static SOAPHeaderElement buildWSAAction(OpenSPCoop2SoapMessage msg,String actor,boolean mustUnderstand, String value) throws MessageException{
		QName name =  new QName(Costanti.WSA_NAMESPACE,Costanti.WSA_SOAP_HEADER_ACTION,Costanti.WSA_PREFIX);
		SOAPHeaderElement header = WSAddressingUtilities.buildHeaderElement(msg,name,
				value,actor,mustUnderstand, BUILD_VALUE_RAW);
		return header;
	}
	
	public static SOAPHeaderElement buildWSAID(OpenSPCoop2SoapMessage msg,String actor,boolean mustUnderstand, String value) throws MessageException{
		QName name =  new QName(Costanti.WSA_NAMESPACE,Costanti.WSA_SOAP_HEADER_ID,Costanti.WSA_PREFIX);
		SOAPHeaderElement header = WSAddressingUtilities.buildHeaderElement(msg,name,
				value,actor,mustUnderstand, BUILD_VALUE_RAW);
		return header;
	}
	
	public static SOAPHeaderElement buildWSARelatesTo(OpenSPCoop2SoapMessage msg,String actor, boolean mustUnderstand, String value) throws MessageException{
		QName name =  new QName(Costanti.WSA_NAMESPACE,Costanti.WSA_SOAP_HEADER_RELATES_TO,Costanti.WSA_PREFIX);
		SOAPHeaderElement header = WSAddressingUtilities.buildHeaderElement(msg,name,
				value,actor,mustUnderstand, BUILD_VALUE_RAW);
		return header;
	}
	
	public static SOAPHeaderElement buildWSAReplyTo(OpenSPCoop2SoapMessage msg,String actor,boolean mustUnderstand, String value) throws MessageException{
		QName name =  new QName(Costanti.WSA_NAMESPACE,Costanti.WSA_SOAP_HEADER_REPLY_TO,Costanti.WSA_PREFIX);
		SOAPHeaderElement header = WSAddressingUtilities.buildHeaderElement(msg,name,
				value,actor,mustUnderstand, BUILD_VALUE_AS_EPR);
		return header;
	}
	
	public static SOAPHeaderElement buildWSAFaultTo(OpenSPCoop2SoapMessage msg,String actor,boolean mustUnderstand, String value) throws MessageException{
		QName name =  new QName(Costanti.WSA_NAMESPACE,Costanti.WSA_SOAP_HEADER_FAULT_TO,Costanti.WSA_PREFIX);
		SOAPHeaderElement header = WSAddressingUtilities.buildHeaderElement(msg,name,
				value,actor,mustUnderstand, BUILD_VALUE_AS_EPR);
		return header;
	}
		
	private static SOAPHeaderElement buildHeaderElement(OpenSPCoop2SoapMessage msg,QName name,String value,String actor,boolean mustUnderstand, boolean epr) throws MessageException{
		
		try {
		
			SOAPHeader hdr = msg.getSOAPHeader();
			if(hdr==null){
				hdr = msg.getSOAPPart().getEnvelope().addHeader(); 
			}
			SOAPHeaderElement element = msg.newSOAPHeaderElement(hdr, name); 
			if(actor!=null) {
				element.setActor(actor);
			}
			element.setMustUnderstand(mustUnderstand);
			
			if(epr==false){
				element.setValue(value);
			}
			else{
				QName nameAddressEPR =  new QName(Costanti.WSA_NAMESPACE,Costanti.WSA_SOAP_HEADER_EPR_ADDRESS,Costanti.WSA_PREFIX);
				element.addChildElement(nameAddressEPR).setValue(value);
			}
			return element;
			
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}
		
	}
	

	
	
	
	
	// ***** VALIDATE *****
	
	private static ValidatoreXSD _validatoreXSD = null;
	private static synchronized void _initValidatore(Logger log){
		if(_validatoreXSD==null) {
			try{
				_validatoreXSD = new ValidatoreXSD(log,WSAddressingUtilities.class.getResourceAsStream("/ws-addr.xsd"));
			}catch(Exception e){
				log.error("ws-addr.xsd, errore durante la costruzione del validatore xsd: "+e.getMessage(),e);
			}
		}
	}
	private ValidatoreXSD getValidatoreXSD() {
		if(_validatoreXSD==null) {
			_initValidatore(this.log);
		}
		return _validatoreXSD;
	}
	
	public void validate(OpenSPCoop2SoapMessage message, WSAddressingHeader header) throws MessageException {
		this.log.debug("Validazione XSD...");
		_validaElementoWSA(getValidatoreXSD(),header.getTo(),message);
		_validaElementoWSA(getValidatoreXSD(),header.getFrom(),message);
		_validaElementoWSA(getValidatoreXSD(),header.getAction(),message);
		_validaElementoWSA(getValidatoreXSD(),header.getId(),message);
		_validaElementoWSA(getValidatoreXSD(),header.getRelatesTo(),message);
		_validaElementoWSA(getValidatoreXSD(),header.getReplyTo(),message);
		_validaElementoWSA(getValidatoreXSD(),header.getFaultTo(),message);
		this.log.debug("Validazione XSD effettuate");
	}
	
	private void _validaElementoWSA(ValidatoreXSD validatoreXSD,SOAPHeaderElement headerElement,OpenSPCoop2SoapMessage msg) throws MessageException{
		if(headerElement!=null){
			try {
				this.log.debug("Validazione XSD ["+headerElement.getLocalName()+"]...");
				// validazione XSD
				if(validatoreXSD==null)
					throw new Exception("Validatore XSD non istanziato");
				validatoreXSD.valida(new java.io.ByteArrayInputStream(msg.getAsByte(headerElement, false)));
			}catch(Exception e) {
				throw new MessageException(e.getMessage(),e);
			}
		}
	}
	
	
	
	
	
	
	// ***** READ *****
	
	public WSAddressingHeader read(OpenSPCoop2SoapMessage message, String actor) throws MessageException{
		return this.read(message, actor, false);
	}
	public WSAddressingHeader read(OpenSPCoop2SoapMessage message, String actor, boolean validate) throws MessageException{

		try{

			SOAPHeader header = message.getSOAPHeader();
			if(header==null){
				return null;
			}
			
			SOAPHeaderElement wsaTO = null; 
			SOAPHeaderElement wsaFROM = null; 
			SOAPHeaderElement wsaAction = null; 
			SOAPHeaderElement wsaID = null; 
			SOAPHeaderElement wsaRelatesTo = null; 
			SOAPHeaderElement wsaReplyTo = null; 
			SOAPHeaderElement wsaFaultTo = null; 
			
			java.util.Iterator<?> it = header.examineAllHeaderElements();
			while( it.hasNext()  ){
				// Test Header Element
				SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();
				
				//Controllo Namespace
				String namespace = headerElement.getNamespaceURI();
				String actorCheck = SoapUtils.getSoapActor(headerElement, message.getMessageType());
				
				boolean actorCompatible = false;
				if(actor==null) {
					actorCompatible = (actorCheck==null);
				}
				else {
					actorCompatible = actor.equals(actorCheck);
				}
				
				if(actorCompatible && Costanti.WSA_NAMESPACE.equals(namespace)){
					
					if(Costanti.WSA_SOAP_HEADER_TO.equals(headerElement.getLocalName())){
						wsaTO = headerElement;
					}
					else if(Costanti.WSA_SOAP_HEADER_FROM.equals(headerElement.getLocalName())){
						wsaFROM = headerElement;
					}
					else if(Costanti.WSA_SOAP_HEADER_ACTION.equals(headerElement.getLocalName())){
						wsaAction = headerElement;
					}
					else if(Costanti.WSA_SOAP_HEADER_ID.equals(headerElement.getLocalName())){
						wsaID = headerElement;
					}
					else if(Costanti.WSA_SOAP_HEADER_RELATES_TO.equals(headerElement.getLocalName())){
						wsaRelatesTo = headerElement;
					}
					else if(Costanti.WSA_SOAP_HEADER_REPLY_TO.equals(headerElement.getLocalName())){
						wsaReplyTo = headerElement;
					}
					else if(Costanti.WSA_SOAP_HEADER_FAULT_TO.equals(headerElement.getLocalName())){
						wsaFaultTo = headerElement;
					}
				}
				
			}
			
			WSAddressingHeader wsaHeader = new WSAddressingHeader();
			wsaHeader.setTo(wsaTO);
			wsaHeader.setFrom(wsaFROM);
			wsaHeader.setAction(wsaAction);
			wsaHeader.setId(wsaID);
			wsaHeader.setRelatesTo(wsaRelatesTo);
			wsaHeader.setReplyTo(wsaReplyTo);
			wsaHeader.setFaultTo(wsaFaultTo);
			
			if(validate) {
				this.validate(message, wsaHeader);
			}
			
			return wsaHeader;
			
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
		
	
	
	
	// ***** DELETE *****
	
	public void delete(OpenSPCoop2SoapMessage message,String actor) throws MessageException{

		try{

			SOAPHeader header = message.getSOAPHeader();
			if(header==null){
				return;
			}
			
			SOAPHeaderElement wsaTO = null; 
			SOAPHeaderElement wsaFROM = null; 
			SOAPHeaderElement wsaAction = null; 
			SOAPHeaderElement wsaID = null; 
			SOAPHeaderElement wsaRelatesTo = null; 
			SOAPHeaderElement wsaReplyTo = null; 
			SOAPHeaderElement wsaFaultTo = null; 
			
			java.util.Iterator<?> it = header.examineAllHeaderElements();
			while( it.hasNext()  ){
				// Test Header Element
				SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();
				
				//Controllo Namespace
				String namespace = headerElement.getNamespaceURI();
				String actorCheck = SoapUtils.getSoapActor(headerElement, message.getMessageType());
				
				boolean actorCompatible = false;
				if(actor==null) {
					actorCompatible = (actorCheck==null);
				}
				else {
					actorCompatible = actor.equals(actorCheck);
				}
				
				if(actorCompatible && Costanti.WSA_NAMESPACE.equals(namespace)){
					if(Costanti.WSA_SOAP_HEADER_TO.equals(headerElement.getLocalName())){
						wsaTO = headerElement;
					}
					else if(Costanti.WSA_SOAP_HEADER_FROM.equals(headerElement.getLocalName())){
						wsaFROM = headerElement;
					}
					else if(Costanti.WSA_SOAP_HEADER_ACTION.equals(headerElement.getLocalName())){
						wsaAction = headerElement;
					}
					else if(Costanti.WSA_SOAP_HEADER_ID.equals(headerElement.getLocalName())){
						wsaID = headerElement;
					}
					else if(Costanti.WSA_SOAP_HEADER_RELATES_TO.equals(headerElement.getLocalName())){
						wsaRelatesTo = headerElement;
					}
					else if(Costanti.WSA_SOAP_HEADER_REPLY_TO.equals(headerElement.getLocalName())){
						wsaReplyTo = headerElement;
					}
					else if(Costanti.WSA_SOAP_HEADER_FAULT_TO.equals(headerElement.getLocalName())){
						wsaFaultTo = headerElement;
					}
				}
				
				
			}
			if(wsaTO==null && wsaFROM==null  &&  wsaAction==null && wsaID==null  &&  wsaRelatesTo==null &&  wsaReplyTo==null &&  wsaFaultTo==null){
				return;
			}
			
			// delete
			if(wsaTO!=null){
				header.removeChild(wsaTO);
			}
			if(wsaFROM!=null){
				header.removeChild(wsaFROM);
			}
			if(wsaAction!=null){
				header.removeChild(wsaAction);
			}
			if(wsaID!=null){
				header.removeChild(wsaID);
			}	
			if(wsaRelatesTo!=null){
				header.removeChild(wsaRelatesTo);
			}
			if(wsaReplyTo!=null){
				header.removeChild(wsaReplyTo);
			}
			if(wsaFaultTo!=null){
				header.removeChild(wsaFaultTo);
			}
		
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
}
