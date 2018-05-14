/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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



package org.openspcoop2.security.message;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import org.apache.wss4j.common.ConfigurationConstants;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.digest.IDigestReader;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.slf4j.Logger;

/**
 * Classe di base per la gestione della sicurezza
 *
 * @author Spadafora Marcello <Ma.Spadafora@finsiel.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class MessageSecurityContext{
	
      
	protected Hashtable<String,Object> incomingProperties = new Hashtable<String,Object>();
	protected Hashtable<String,Object> outgoingProperties = new Hashtable<String,Object>();
	
	protected boolean useActorDefaultIfNotDefined = true;
	protected String actorDefault = null;
	protected String actor;

	protected String msgErrore;
	protected CodiceErroreCooperazione codiceErrore;
	/** Eventuale subCodici di errore */
	protected List<SubErrorCodeSecurity> listaSubCodiceErrore = new ArrayList<SubErrorCodeSecurity>();
		
	protected boolean functionAsClient = true;
	
	protected String prefixWsuId;
	
	protected boolean removeAllWsuIdRef;
	
	protected String securityEngine;
	protected IMessageSecurityContext messageSecurityContext;
	protected IMessageSecuritySender messageSecuritySender;
	protected IMessageSecurityReceiver messageSecurityReceiver;
	protected IMessageSecurityDigest messageSecurityDigest;
	
	protected Logger log;
	public Logger getLog() {
		return this.log;
	}

	private IDSoggetto idFruitore;
	private String pddFruitore;
	private IDServizio idServizio;
	private String pddErogatore;
	public IDSoggetto getIdFruitore() {
		return this.idFruitore;
	}
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public String getPddFruitore() {
		return this.pddFruitore;
	}
	public String getPddErogatore() {
		return this.pddErogatore;
	}
	
	private boolean useXMLSec = true;
	public boolean isUseXMLSec() {
		return this.useXMLSec;
	}
	public void setUseXMLSec(boolean useXMLSec) {
		this.useXMLSec = useXMLSec;
	}
	
	private List<Reference> references;
	public List<Reference> getReferences() {
		return this.references;
	}
	public void setReferences(List<Reference> references) {
		this.references = references;
	}
	
	/**
	 * Costruttore. 
	 * 
	 * @param messageSecurityContextParameters contextParameters
	 */
	public MessageSecurityContext(MessageSecurityContextParameters messageSecurityContextParameters){
		this.useActorDefaultIfNotDefined = messageSecurityContextParameters.isUseActorDefaultIfNotDefined();
    	this.actorDefault = messageSecurityContextParameters.getActorDefault();
    	if(messageSecurityContextParameters.getLog()!=null)
    		this.log = messageSecurityContextParameters.getLog();
    	else
    		this.log = LoggerWrapperFactory.getLogger(MessageSecurityContext.class);
    	this.functionAsClient = messageSecurityContextParameters.isFunctionAsClient();
    	this.prefixWsuId = messageSecurityContextParameters.getPrefixWsuId();
    	this.removeAllWsuIdRef = messageSecurityContextParameters.isRemoveAllWsuIdRef();
    	this.idFruitore = messageSecurityContextParameters.getIdFruitore();
    	this.idServizio = messageSecurityContextParameters.getIdServizio();
    	this.pddFruitore = messageSecurityContextParameters.getPddFruitore();
    	this.pddErogatore = messageSecurityContextParameters.getPddErogatore();
	}
	
	
    /** Process */
	public abstract boolean processIncoming(OpenSPCoop2Message message, Busta busta);
	public abstract boolean processOutgoing(OpenSPCoop2Message message);
	

	/** Function As Client */
	public void setFunctionAsClient(boolean functionAsClient) {
		this.functionAsClient = functionAsClient;
	}
	public boolean isFunctionAsClient() {
		return this.functionAsClient;
	}
	
	
	/** Prefix WSUID */
	public String getPrefixWsuId() {
		return this.prefixWsuId;
	}
	

	/**  indicazione se rimuovere tutti gli attributi WsuId negli elementi 'toccati' dalla sicurezza */
	public boolean isRemoveAllWsuIdRef() {
		return this.removeAllWsuIdRef;
	}
	
	
	
	/** GetValues ottenuti dopo il processamento */
	public abstract String getSubject();
	public String getMsgErrore() { return this.msgErrore; }
	public CodiceErroreCooperazione getCodiceErrore() { return this.codiceErrore; }
	public List<SubErrorCodeSecurity> getListaSubCodiceErrore() {
		return this.listaSubCodiceErrore;
	}

	
	
	
	/** Actor utilizzato nell'header di sicurezza */
	/**
     * Set/get dell'Actor
     */
	public String getActor() {
		return this.actor;
	}
	private void setActor(boolean incoming) {
    	boolean actorDefinito = false;
    	boolean mustUnderstandTrue=false;
    	
    	Hashtable<?,?> securityProperties = null;
    	if(incoming){
    		securityProperties = this.incomingProperties;
    	}else{
    		securityProperties = this.outgoingProperties;
    	}
		if (securityProperties != null && securityProperties.size() > 0) {
			
			for (Enumeration<?> e = securityProperties.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String value = (String) securityProperties.get(key);
				//System.out.println(key + " -> " + value );
				// check actor
				if(ConfigurationConstants.ACTOR.equals(key)){
					this.actor = value;
				}
				
				// Check Fix Bug#18 Test-1
				if(ConfigurationConstants.MUST_UNDERSTAND.equals(key)){
					if("true".equals(value)){
						mustUnderstandTrue = true;
					}
				}else if("actor".equals(key)){
					actorDefinito = true;
				}
				
			}
		}
		if(mustUnderstandTrue && actorDefinito==false && this.useActorDefaultIfNotDefined ){
			// Aggiungo actor 'openspcoop' Porta di Dominio
			this.actor = this.actorDefault;
		}
    }
	
	
	
    /** Security Message Properties */
	/**
	 * Set/get  proprieta' che verranno usate per i messaggi in ingresso dal MessageSecurity
	 * @param secProperties
	 */
    public void setIncomingProperties(Hashtable<String,Object> secProperties) throws SecurityException{
    	this.incomingProperties = secProperties;
    	this.setActor(true);
    	this.readMessageSecurityEngine(true);
    	this.readSignatureEngine(true);
    }
    public Hashtable<String,Object> getIncomingProperties() {
    	return this.incomingProperties;
    }
	/**
	 * Set/get  proprieta' che verranno usate per i messaggi in uscita dal MessageSecurity
	 * @param secProperties
	 */
    public void setOutgoingProperties(Hashtable<String,Object> secProperties) throws SecurityException{
    	this.outgoingProperties = secProperties;
    	this.setActor(false);
    	this.readMessageSecurityEngine(false);
    	this.readSignatureEngine(false);
    }
    public Hashtable<String,Object> getOutgoingProperties() {
    	return this.outgoingProperties;
    }
    
    
    
    /** Message Security Engine */   
	public String getSecurityEngine() {
		return this.securityEngine;
	}
	public IMessageSecurityContext getMessageSecurityContext() {
		return this.messageSecurityContext;
	} 
	public IMessageSecuritySender getMessageSecuritySender() {
		return this.messageSecuritySender;
	}
	public IMessageSecurityReceiver getMessageSecurityReceiver() {
		return this.messageSecurityReceiver;
	}
	public IMessageSecurityDigest getMessageSecurityDigest() {
		return this.messageSecurityDigest;
	}
	public IDigestReader getDigestReader() throws SecurityException{
		if(this.messageSecurityDigest!=null){
			return this.getMessageSecurityDigest().getDigestReader(this);
		}
		return null;
	}
    private void readMessageSecurityEngine(boolean incoming) throws SecurityException{
    	try{
    		
    		this.securityEngine = SecurityConstants.SECURITY_ENGINE_WSS4J;
    		String engineProperty = null;
    		if(incoming){
    			engineProperty = (String) this.incomingProperties.get(SecurityConstants.SECURITY_ENGINE);
    		}else{
    			engineProperty = (String) this.outgoingProperties.get(SecurityConstants.SECURITY_ENGINE);
    		}
    		if(engineProperty!=null){
    			engineProperty = engineProperty.trim();
    			if(SecurityConstants.SECURITY_ENGINE_WSS4J.equals(engineProperty)){
    				this.securityEngine = SecurityConstants.SECURITY_ENGINE_WSS4J;
    			}
    			else if(SecurityConstants.SECURITY_ENGINE_SOAPBOX.equals(engineProperty)){
    				this.securityEngine = SecurityConstants.SECURITY_ENGINE_SOAPBOX;
    			}
//    			else if(SecurityConstants.SECURITY_ENGINE_DSS.equals(engineProperty)){
//    				this.securityEngine = SecurityConstants.SECURITY_ENGINE_DSS;
//    			}
    			else{
    				throw new SecurityException("Security engine impostato ["+engineProperty+"] non supportato");
    			}
    		}
    		if(SecurityConstants.SECURITY_ENGINE_WSS4J.equals(this.securityEngine)){
    			this.messageSecurityContext = (IMessageSecurityContext) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.wss4j.MessageSecurityContext_wss4j");
    			this.messageSecuritySender = (IMessageSecuritySender) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.wss4j.MessageSecuritySender_wss4j");
    			this.messageSecurityReceiver = (IMessageSecurityReceiver) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.wss4j.MessageSecurityReceiver_wss4j");
    			this.messageSecurityDigest = (IMessageSecurityDigest) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.wss4j.MessageSecurityDigest_wss4j");
    		}else if(SecurityConstants.SECURITY_ENGINE_SOAPBOX.equals(this.securityEngine)){
    			this.messageSecurityContext = (IMessageSecurityContext) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.soapbox.MessageSecurityContext_soapbox");
    			this.messageSecuritySender = (IMessageSecuritySender) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.soapbox.MessageSecuritySender_soapbox");
    			this.messageSecurityReceiver = (IMessageSecurityReceiver) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.soapbox.MessageSecurityReceiver_soapbox");
    			this.messageSecurityDigest = (IMessageSecurityDigest) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.soapbox.MessageSecurityDigest_soapbox");
//    		} else if(SecurityConstants.SECURITY_ENGINE_DSS.equals(this.securityEngine)){
//			this.messageSecurityContext = (IMessageSecurityContext) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.dss.MessageSecurityContext_dss");
//			this.messageSecuritySender = (IMessageSecuritySender) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.dss.MessageSecuritySender_dss");
//			this.messageSecurityReceiver = (IMessageSecurityReceiver) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.dss.MessageSecurityReceiver_dss");
//			this.messageSecurityDigest = (IMessageSecurityDigest) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.dss.MessageSecurityDigest_dss");
		}
    		this.messageSecurityContext.init(this);
    		
    	}catch(Exception e){
    		throw new SecurityException(e.getMessage(),e);
    	}
    }
    
    
    
    
    
    /** SignatureEngine */
    private void readSignatureEngine(boolean incoming) throws SecurityException{
    	try{
    		
    		this.useXMLSec = true; // default
    		String engineProperty = null;
    		if(incoming){
    			engineProperty = (String) this.incomingProperties.get(SecurityConstants.SIGNATURE_ENGINE);
    		}else{
    			engineProperty = (String) this.outgoingProperties.get(SecurityConstants.SIGNATURE_ENGINE);
    		}
    		if(engineProperty!=null){
    			engineProperty = engineProperty.trim();
    			if(SecurityConstants.SIGNATURE_ENGINE_SUN.equalsIgnoreCase(engineProperty)){
    				this.useXMLSec = false;
    			}
    			else if(SecurityConstants.SIGNATURE_ENGINE_XMLSEC.equalsIgnoreCase(engineProperty)){
    				this.useXMLSec = true;
    			}
    			else{
    				throw new SecurityException("Signature engine impostato ["+engineProperty+"] non supportato");
    			}
    		}
    		
    	}catch(Exception e){
    		throw new SecurityException(e.getMessage(),e);
    	}
    }
        
    
    
    
    
    /** Utility per verificare l'esistenza di un header di sicurezza */
    public boolean existsSecurityHeader(OpenSPCoop2Message msg,String actor){
    	try{
	    	if(msg==null){
	    		return false;
	    	}
	    	if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
		    	SOAPHeader header = msg.castAsSoap().getSOAPHeader();
		    	if(header==null || (SoapUtils.getNotEmptyChildNodes(header).size()==0) ){
		    		return false;
		    	}
		       	java.util.Iterator<?> it = header.examineAllHeaderElements();
				while( it.hasNext()  ){
					
					// Test Header Element
					SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();
					if(   SecurityConstants.WSS_HEADER_ELEMENT.equals(headerElement.getLocalName()) &&
							SecurityConstants.WSS_HEADER_ELEMENT_NAMESPACE.equals(headerElement.getNamespaceURI()) ){
							// potenziale header, verifico l'actor
						String actorCheck = SoapUtils.getSoapActor(headerElement, msg.getMessageType());
						if(actor==null){
							return actorCheck==null;
						}else{
							return actor.equals(actorCheck);
						}
					}
				}
				return false;
	    	}
	    	else{
	    		// TODO
	    		return true;
	    	}
    	}catch(Exception e){
    		if(this.log!=null)
    			this.log.error("existsHeaderMessageSecurity error con actor["+actor+"]",e);
    		return false;
    	}
    }

}

