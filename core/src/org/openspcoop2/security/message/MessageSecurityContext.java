/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPHeaderElement;
import jakarta.xml.soap.SOAPPart;

import org.apache.wss4j.common.ConfigurationConstants;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;
import org.openspcoop2.core.transazioni.utils.TempiElaborazione;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.saml.SAMLConstants;
import org.openspcoop2.security.message.utils.EncryptionBean;
import org.openspcoop2.security.message.utils.SignatureBean;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.digest.IDigestReader;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.slf4j.Logger;

/**
 * Classe di base per la gestione della sicurezza
 *
 * @author Spadafora Marcello (Ma.Spadafora@finsiel.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class MessageSecurityContext{
	
      
	protected Map<String,Object> incomingProperties = new HashMap<>();
	protected Map<String,Object> outgoingProperties = new HashMap<>();

	protected boolean useActorDefaultIfNotDefined = true;
	protected String actorDefault = null;
	protected String actor;

	protected String msgErrore;
	protected CodiceErroreCooperazione codiceErrore;
	/** Eventuale subCodici di errore */
	protected List<SubErrorCodeSecurity> listaSubCodiceErrore = new ArrayList<>();
		
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
	
	private List<Reference> references;
	public List<Reference> getReferences() {
		return this.references;
	}
	public void setReferences(List<Reference> references) {
		this.references = references;
	}
	
	
	// I due encrypt part servono per un utilizzo manuale delle classi di sicurezza
	
	private String manualAttachmentsEncryptPart = null;
	private String manualAttachmentsSignaturePart = null;
	
	public String getManualAttachmentsEncryptPart() {
		return this.manualAttachmentsEncryptPart;
	}
	public void setManualAttachmentsEncryptPart(String manualAttachmentsEncryptPart) {
		this.manualAttachmentsEncryptPart = manualAttachmentsEncryptPart;
	}
	public String getManualAttachmentsSignaturePart() {
		return this.manualAttachmentsSignaturePart;
	}
	public void setManualAttachmentsSignaturePart(String manualAttachmentsSignaturePart) {
		this.manualAttachmentsSignaturePart = manualAttachmentsSignaturePart;
	}
	
	// I due bean signatureBean e encryptionBean servono per un utilizzo manuale delle classi di sicurezza.
	
	private SignatureBean signatureBean;
	private EncryptionBean encryptionBean;
	public SignatureBean getSignatureBean() {
		return this.signatureBean;
	}
	public void setSignatureBean(SignatureBean signatureBean) {
		this.signatureBean = signatureBean;
	}
	public EncryptionBean getEncryptionBean() {
		return this.encryptionBean;
	}
	public void setEncryptionBean(EncryptionBean encryptionBean) {
		this.encryptionBean = encryptionBean;
	}
	
	/**
	 * Costruttore. 
	 * 
	 * @param messageSecurityContextParameters contextParameters
	 */
	protected MessageSecurityContext(MessageSecurityContextParameters messageSecurityContextParameters){
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
	
	// incoming
	protected abstract boolean processIncoming(OpenSPCoop2Message message, Busta busta, org.openspcoop2.utils.Map<Object> ctx);
	public boolean processIncoming(OpenSPCoop2Message message, Busta busta, org.openspcoop2.utils.Map<Object> ctx, TempiElaborazione tempiElaborazione) {
		MessageRole messageRole = message.getMessageRole();
		if(MessageRole.REQUEST.equals(messageRole)) {
			if(tempiElaborazione!=null) {
				tempiElaborazione.startSicurezzaMessaggioRichiesta();
			}
		}
		else {
			if(tempiElaborazione!=null) {
				tempiElaborazione.startSicurezzaMessaggioRisposta();
			}
		}
		try {
			return this.processIncoming(message, busta, ctx);
		}
		finally {
			if(MessageRole.REQUEST.equals(messageRole)) {
				if(tempiElaborazione!=null) {
					tempiElaborazione.endSicurezzaMessaggioRichiesta();
				}
			}
			else {
				if(tempiElaborazione!=null) {
					tempiElaborazione.endSicurezzaMessaggioRisposta();
				}
			}
		}
	}

	// outcoming
	protected abstract boolean processOutgoing(OpenSPCoop2Message message, org.openspcoop2.utils.Map<Object> ctx);
	public boolean processOutgoing(OpenSPCoop2Message message, org.openspcoop2.utils.Map<Object> ctx, TempiElaborazione tempiElaborazione) {
		MessageRole messageRole = message.getMessageRole();
		if(MessageRole.REQUEST.equals(messageRole)) {
			if(tempiElaborazione!=null) {
				tempiElaborazione.startSicurezzaMessaggioRichiesta();
			}
		}
		else {
			if(tempiElaborazione!=null) {
				tempiElaborazione.startSicurezzaMessaggioRisposta();
			}
		}
		try {
			return this.processOutgoing(message, ctx);
		}
		finally {
			if(MessageRole.REQUEST.equals(messageRole)) {
				if(tempiElaborazione!=null) {
					tempiElaborazione.endSicurezzaMessaggioRichiesta();
				}
			}
			else {
				if(tempiElaborazione!=null) {
					tempiElaborazione.endSicurezzaMessaggioRisposta();
				}
			}
		}
	}
	

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
    	
    	Map<String,Object> securityProperties = null;
    	if(incoming){
    		securityProperties = this.incomingProperties;
    	}else{
    		securityProperties = this.outgoingProperties;
    	}
		if (securityProperties != null && securityProperties.size() > 0) {
			
			for (String key : securityProperties.keySet()) {
				String value = null;
				Object oValue = securityProperties.get(key);
				if(oValue instanceof String) {
					value = (String) oValue;
				}
				
				/**System.out.println(key + " -> " + value );*/
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
			// Aggiungo actor 'govway'
			this.actor = this.actorDefault;
		}
    }
	
	
	
    /** Security Message Properties */
	/**
	 * Set/get  proprieta' che verranno usate per i messaggi in ingresso dal MessageSecurity
	 * @param secProperties
	 */
    public void setIncomingProperties(Map<String,Object> secProperties) throws SecurityException{
    	this.setIncomingProperties(secProperties, true, false);
    }
    public void setIncomingProperties(Map<String,Object> secProperties, boolean convertSecProperties) throws SecurityException{
    	this.setIncomingProperties(secProperties, convertSecProperties, false);
    }
    public void setIncomingProperties(Map<String,Object> secProperties, boolean convertSecProperties, boolean preserveNotStringProperty) throws SecurityException{
    	if(secProperties!=null && secProperties.size()>0 && convertSecProperties) {
    		this.incomingProperties = convertSecProperties(secProperties, preserveNotStringProperty);
    	}
    	else {
    		this.incomingProperties = secProperties;
    	}
    	this.setActor(true);
    	this.readMessageSecurityEngine(true);
    	this.resolvePWCallback(true);
    }
    public Map<String,Object> getIncomingProperties() {
    	return this.incomingProperties;
    }
	/**
	 * Set/get  proprieta' che verranno usate per i messaggi in uscita dal MessageSecurity
	 * @param secProperties
	 */
    public void setOutgoingProperties(Map<String,Object> secProperties) throws SecurityException{
    	this.setOutgoingProperties(secProperties, true, false);
    }
    public void setOutgoingProperties(Map<String,Object> secProperties, boolean convertSecProperties) throws SecurityException{
    	this.setOutgoingProperties(secProperties, convertSecProperties, false);
    }
    public void setOutgoingProperties(Map<String,Object> secProperties, boolean convertSecProperties, boolean preserveNotStringProperty) throws SecurityException{
    	
    	if(secProperties!=null && secProperties.size()>0 && convertSecProperties) {
    		this.outgoingProperties = convertSecProperties(secProperties, preserveNotStringProperty);
    	}
    	else {
    		this.outgoingProperties = secProperties;
    	}
    	this.setActor(false);
    	this.readMessageSecurityEngine(false);
    	this.resolvePWCallback(false);
    }
    public Map<String,Object> getOutgoingProperties() {
    	return this.outgoingProperties;
    }
    
    private Map<String,Object> convertSecProperties(Map<String,Object> secProperties, boolean preserveNotStringProperty) throws SecurityException{
    	
    	try {
    	
    		Map<String, Object> mapNoStringProperty = new HashMap<>();
    		
	    	Map<String, String> map = new HashMap<>();
	    	if (secProperties != null && secProperties.size() > 0) {
				
				for (String key : secProperties.keySet()) {
					Object value = secProperties.get(key);
					String v = null;
					if(value instanceof String) {
						v = (String) value;
					}
					map.put(key, v);
					
					if(preserveNotStringProperty &&
						value!=null && !(value instanceof String)) {
						mapNoStringProperty.put(key, value);
					}
				}
	    	}
	    	
	    	Map<String, Properties> multiMap = DBPropertiesUtils.toMultiMap(map);
	    	
	    	Map<String, Object> table = new HashMap<>();
	    	
	    	Properties defaultProperties = MultiPropertiesUtilities.removeDefaultProperties(multiMap);
	    	if(defaultProperties!=null && defaultProperties.size()>0) {
	    		Iterator<?> it = defaultProperties.keySet().iterator();
	    		while (it.hasNext()) {
					Object oKey = it.next();
					if(oKey instanceof String) {
						String key = (String) oKey;
						String value = null;
						Object oValue = defaultProperties.get(key);
						if(oValue instanceof String) {
							value = (String) oValue;
						}
						table.put(key, value);
					}
				}
	    	}
	    	
	    	if(multiMap.size()>0) { // ho rimosso la mappa di default
	    		
	    		if(preserveNotStringProperty && mapNoStringProperty.size()>0) {
	    			
	    			List<String> keysMultiMap = new ArrayList<>();
	    			keysMultiMap.addAll(multiMap.keySet());
	    			
	    			Iterator<String> it = mapNoStringProperty.keySet().iterator();
	    			while (it.hasNext()) {
						String keyWithPrefix = it.next();
						Object object = mapNoStringProperty.get(keyWithPrefix);
						
		    			String prefix = DBPropertiesUtils.startsWith(keysMultiMap, keyWithPrefix);	
		    			if(prefix==null) {
		    				table.put(keyWithPrefix, object);
		    			}
		    			else {
		    				String keyWithoutPrefix = DBPropertiesUtils.normalizePropertyName(prefix, keyWithPrefix);
		    				multiMap.get(prefix).put(keyWithoutPrefix, object);
		    			}
					}
	    			
	    		}
	    		
	    		table.putAll(multiMap);
	    		
	    	}
	    	else {
	    		if(preserveNotStringProperty && mapNoStringProperty.size()>0) {
	    			table.putAll(mapNoStringProperty);
	    		}
	    	}
	    	
	    	return table;
	    	
		}catch(Exception e) {
			throw new SecurityException(e.getMessage(),e);
		}
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
	public IDigestReader getDigestReader(OpenSPCoop2MessageFactory messageFactory) throws SecurityException{
		if(this.messageSecurityDigest!=null){
			return this.getMessageSecurityDigest().getDigestReader(messageFactory, this);
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
    				// DEFAULT: this.securityEngine = SecurityConstants.SECURITY_ENGINE_WSS4J;
    			}
    			else if(SecurityConstants.SECURITY_ENGINE_JOSE.equals(engineProperty)){
    				this.securityEngine = SecurityConstants.SECURITY_ENGINE_JOSE;
    			}
    			else if(SecurityConstants.SECURITY_ENGINE_XML.equals(engineProperty)){
    				this.securityEngine = SecurityConstants.SECURITY_ENGINE_XML;
    			}
    			else{
    				throw new SecurityException("Security engine impostato ["+engineProperty+"] non supportato");
    			}
    		}
    		if(SecurityConstants.SECURITY_ENGINE_WSS4J.equals(this.securityEngine)){
    			this.messageSecurityContext = (IMessageSecurityContext) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.wss4j.MessageSecurityContext_wss4j");
    			this.messageSecuritySender = (IMessageSecuritySender) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.wss4j.MessageSecuritySender_wss4j");
    			this.messageSecurityReceiver = (IMessageSecurityReceiver) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.wss4j.MessageSecurityReceiver_wss4j");
    			this.messageSecurityDigest = (IMessageSecurityDigest) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.wss4j.MessageSecurityDigest_wss4j");
    		}else if(SecurityConstants.SECURITY_ENGINE_JOSE.equals(this.securityEngine)){
    			this.messageSecurityContext = (IMessageSecurityContext) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.jose.MessageSecurityContext_jose");
    			this.messageSecuritySender = (IMessageSecuritySender) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.jose.MessageSecuritySender_jose");
    			this.messageSecurityReceiver = (IMessageSecurityReceiver) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.jose.MessageSecurityReceiver_jose");
    			this.messageSecurityDigest = (IMessageSecurityDigest) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.jose.MessageSecurityDigest_jose");
    		}else if(SecurityConstants.SECURITY_ENGINE_XML.equals(this.securityEngine)){
    			this.messageSecurityContext = (IMessageSecurityContext) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.xml.MessageSecurityContext_xml");
    			this.messageSecuritySender = (IMessageSecuritySender) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.xml.MessageSecuritySender_xml");
    			this.messageSecurityReceiver = (IMessageSecurityReceiver) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.xml.MessageSecurityReceiver_xml");
    			this.messageSecurityDigest = (IMessageSecurityDigest) ClassLoaderUtilities.newInstance("org.openspcoop2.security.message.xml.MessageSecurityDigest_xml");
    		}
    		this.messageSecurityContext.init(this);
    		
    	}catch(Exception e){
    		throw new SecurityException(e.getMessage(),e);
    	}
    }
    
    
    
    
       
    
    /** resolvePWCallback */
    private void resolvePWCallback(boolean incoming) throws SecurityException{
    	try{
    		Map<String, Object> props = null;
    		if(incoming) {
    			props = this.incomingProperties;
    		}else {
    			props = this.outgoingProperties;
    		}
    		
    		boolean pwCallback = props.containsKey(SecurityConstants.PASSWORD_CALLBACK_REF); // non controllo il valore, mi basta la presenza
    		props.remove(SecurityConstants.PASSWORD_CALLBACK_REF);
    		if(pwCallback) {
    			
    			String aliasGenerico = null;
    			if(props.containsKey(SecurityConstants.USER)) {
    				aliasGenerico = (String) props.get(SecurityConstants.USER);
				}  
    			
    			HashMap<String, String> mapAliasToPassword = new HashMap<>();
    			
    			if(props.containsKey(SecurityConstants.SIGNATURE_PASSWORD)) {
    				String password = (String) props.get(SecurityConstants.SIGNATURE_PASSWORD);
    				String alias = null;
    				if(props.containsKey(SecurityConstants.SIGNATURE_USER)) {
    					alias = (String) props.get(SecurityConstants.SIGNATURE_USER);
    				}
    				else {
    					alias = aliasGenerico;
    				}
    				if(alias!=null){
    					mapAliasToPassword.put(alias, password);
    				}
    			}
    			
    			if(props.containsKey(SecurityConstants.ENCRYPTION_PASSWORD)) {
    				String password = (String) props.get(SecurityConstants.ENCRYPTION_PASSWORD);
    				String alias = null;
    				if(props.containsKey(SecurityConstants.ENCRYPTION_USER)) {
    					alias = (String) props.get(SecurityConstants.ENCRYPTION_USER);
    				}
    				else {
    					alias = aliasGenerico;
    				}
    				if(alias!=null){
    					mapAliasToPassword.put(alias, password);
    				}
    			}

    			if(props.containsKey(SecurityConstants.DECRYPTION_PASSWORD)) {
    				String password = (String) props.get(SecurityConstants.DECRYPTION_PASSWORD);
    				String alias = null;
    				if(props.containsKey(SecurityConstants.DECRYPTION_USER)) {
    					alias = (String) props.get(SecurityConstants.DECRYPTION_USER);
    				}
    				else {
    					alias = aliasGenerico;
    				}
    				if(alias!=null){
    					mapAliasToPassword.put(alias, password);
    				}
    			}
    			
    			if(props.containsKey(SecurityConstants.USERNAME_TOKEN_PW)) {
    				String password = (String) props.get(SecurityConstants.USERNAME_TOKEN_PW);
    				String alias = aliasGenerico;
    				if(alias!=null){
    					mapAliasToPassword.put(alias, password);
    				}
    			}
    			
    			if(props.containsKey(SecurityConstants.USERNAME_TOKEN_PW_MAP)) {
	    			String mapUserPassword = (String) props.get(SecurityConstants.USERNAME_TOKEN_PW_MAP);
	    			if(mapUserPassword!=null && !"".equals(mapUserPassword)) {
	    				Properties convertTextToProperties = PropertiesUtilities.convertTextToProperties(mapUserPassword);
	    				if(convertTextToProperties!=null && !convertTextToProperties.isEmpty()) {
	    					for (Map.Entry<Object,Object> entry : convertTextToProperties.entrySet()) {
								if((entry.getKey() instanceof String) && (entry.getValue() instanceof String)) {
									String key = (String) entry.getKey();
									String value = (String) entry.getValue();
									mapAliasToPassword.put(key, value);
								}
							}
	    				}
	    			}
    			}
    			
    			if(mapAliasToPassword.size()>0) {
    				
    				CallbackHandler pwCallbackHandler = new CallbackHandler() {
    						
						private HashMap<String, String> mapAliasToPasswordParam = mapAliasToPassword;
						
						@Override
						public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
							 for (int i = 0; i < callbacks.length; i++) {
								 if (callbacks[i] instanceof WSPasswordCallback) {
						                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
						                if(this.mapAliasToPasswordParam.containsKey(pc.getIdentifier())) {
						                	pc.setPassword(this.mapAliasToPasswordParam.get(pc.getIdentifier()));
						                }
						            } else {
						                throw new UnsupportedCallbackException(callbacks[i],
						                        "Unrecognized Callback");
						            }
						        }
						}
						
					};
    			
    				props.put(SecurityConstants.PASSWORD_CALLBACK_REF, pwCallbackHandler);
    			}
    		
    		}
    	}catch(Exception e){
    		throw new SecurityException(e.getMessage(),e);
    	}
    }
    
    
    /** Utility per verificare l'esistenza di un header di sicurezza */
    public boolean existsSecurityHeader(OpenSPCoop2Message msg,String actor){
    	SOAPHeader header = null;
    	try{
	    	if(msg==null){
	    		return false;
	    	}
	    	if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
	    		OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
	    		header = soapMsg.getSOAPHeader();
	    	}
	    	else{
	    		return true;
	    	}
    	}catch(Exception e){
    		if(this.log!=null)
    			this.log.error("existsHeaderMessageSecurity error con actor["+actor+"]",e);
    		return false;
    	}
    	return existsSecurityHeader(msg.getFactory(), msg.getMessageType(), 
    			header, actor);
    }
    public boolean existsSecurityHeader(OpenSPCoop2Message msg,String actor, 
    		boolean bufferMessage_readOnly, String idTransazione){
    	SOAPEnvelope soapEnvelope = null;
    	try{
    		if(msg==null){
	    		return false;
	    	}
	    	if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
	    		OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
	    		SOAPPart soapPart = MessageUtils.getSOAPPart(soapMsg, bufferMessage_readOnly, idTransazione);
	    		if(soapPart!=null) {
	    			soapEnvelope = soapPart.getEnvelope();
	    		}
	    	}
	    	else{
	    		return true;
	    	}
    	}catch(Exception e){
    		if(this.log!=null)
    			this.log.error("existsHeaderMessageSecurity error con actor["+actor+"]",e);
    		return false;
    	}
    	return existsSecurityHeader(msg.getFactory(), msg.getMessageType(), 
    			soapEnvelope, actor);
    }
    public boolean existsSecurityHeader(OpenSPCoop2MessageFactory messageFactory, MessageType messageType, 
    		SOAPEnvelope soapEnvelope,String actor){
    	SOAPHeader header = null;
    	try{
    		header = soapEnvelope.getHeader();
    	}catch(Exception e){
    		if(this.log!=null)
    			this.log.error("existsHeaderMessageSecurity error con actor["+actor+"]",e);
    		return false;
    	}
    	return existsSecurityHeader(messageFactory, messageType, 
    			header, actor);
    }
    public boolean existsSecurityHeader(OpenSPCoop2MessageFactory messageFactory, MessageType messageType, 
    		SOAPHeader header,String actor){
    	try{
	    	if(header==null || (SoapUtils.getNotEmptyChildNodes(messageFactory, header).isEmpty()) ){
	    		return false;
	    	}
	       	java.util.Iterator<?> it = header.examineAllHeaderElements();
			while( it.hasNext()  ){
				
				// Test Header Element
				SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();
				if(   SecurityConstants.WSS_HEADER_ELEMENT.equals(headerElement.getLocalName()) &&
						SecurityConstants.WSS_HEADER_ELEMENT_NAMESPACE.equals(headerElement.getNamespaceURI()) ){
						// potenziale header, verifico l'actor
					String actorCheck = SoapUtils.getSoapActor(headerElement, messageType);
					if(actor==null){
						if(actorCheck==null) {
							return true;
						}
					}else{
						if(actor.equals(actorCheck)) {
							return true;
						}
					}
				}
				
			}
			return false;
    	}catch(Exception e){
    		if(this.log!=null)
    			this.log.error("existsHeaderMessageSecurity error con actor["+actor+"]",e);
    		return false;
    	}
    }
    
    

    public SOAPHeaderElement getSecurityHeader(OpenSPCoop2Message msg,String actor){
    	SOAPHeader header = null;
    	try{
	    	if(msg==null){
	    		return null;
	    	}
	    	if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
	    		OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
	    		header = soapMsg.getSOAPHeader();
	    	}
	    	else{
	    		return null;
	    	}
    	}catch(Exception e){
    		if(this.log!=null)
    			this.log.error("getHeaderMessageSecurity error con actor["+actor+"]",e);
    		return null;
    	}
    	return getSecurityHeader(msg.getFactory(), msg.getMessageType(), 
    			header, actor);
    }
    public SOAPHeaderElement getSecurityHeader(OpenSPCoop2Message msg,String actor, 
    		boolean bufferMessageReadOnly, String idTransazione){
    	SOAPEnvelope soapEnvelope = null;
    	try{
    		if(msg==null){
	    		return null;
	    	}
	    	if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
	    		OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
	    		SOAPPart soapPart = MessageUtils.getSOAPPart(soapMsg, bufferMessageReadOnly, idTransazione);
	    		if(soapPart!=null) {
	    			soapEnvelope = soapPart.getEnvelope();
	    		}
	    	}
	    	else{
	    		return null;
	    	}
    	}catch(Exception e){
    		if(this.log!=null)
    			this.log.error("getHeaderMessageSecurity error con actor["+actor+"]",e);
    		return null;
    	}
    	return getSecurityHeader(msg.getFactory(), msg.getMessageType(), 
    			soapEnvelope, actor);
    }
    public SOAPHeaderElement getSecurityHeader(OpenSPCoop2MessageFactory messageFactory, MessageType messageType, 
    		SOAPEnvelope soapEnvelope,String actor){
    	SOAPHeader header = null;
    	try{
    		header = soapEnvelope.getHeader();
    	}catch(Exception e){
    		if(this.log!=null)
    			this.log.error("getHeaderMessageSecurity error con actor["+actor+"]",e);
    		return null;
    	}
    	return getSecurityHeader(messageFactory, messageType, 
    			header, actor);
    }
    public SOAPHeaderElement getSecurityHeader(OpenSPCoop2MessageFactory messageFactory, MessageType messageType, 
    		SOAPHeader header,String actor){
    	try{
    		if(header==null || (SoapUtils.getNotEmptyChildNodes(messageFactory, header).isEmpty()) ){
	    		return null;
	    	}
	       	java.util.Iterator<?> it = header.examineAllHeaderElements();
			while( it.hasNext()  ){
				
				// Test Header Element
				SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();
				if(   SecurityConstants.WSS_HEADER_ELEMENT.equals(headerElement.getLocalName()) &&
						SecurityConstants.WSS_HEADER_ELEMENT_NAMESPACE.equals(headerElement.getNamespaceURI()) ){
						// potenziale header, verifico l'actor
					String actorCheck = SoapUtils.getSoapActor(headerElement, messageType);
					if(actor==null){
						if(actorCheck==null) {
							return headerElement;
						}
					}else{
						if(actor.equals(actorCheck)) {
							return headerElement;
						}
					}
				}
				
			}
			return null;
    	}catch(Exception e){
    		if(this.log!=null)
    			this.log.error("getHeaderMessageSecurity error con actor["+actor+"]",e);
    		return null;
    	}
    }

    
    /** Utility per verificare l'esistenza di un header di sicurezza */
    public SOAPElement getSAMLTokenInSecurityHeader(OpenSPCoop2MessageFactory messageFactory, SOAPHeaderElement securityHeader,String samlVersion){
    	try{
    		SOAPElement samlElement = null;
						
    		DynamicNamespaceContext dnc = new DynamicNamespaceContext();
    		dnc.findPrefixNamespace(securityHeader);
						
    		AbstractXPathExpressionEngine xpathExpressionEngine = new XPathExpressionEngine(messageFactory);
    		String xpath =null;
    		if(SecurityConstants.SAML_VERSION_XMLCONFIG_ID_VALUE_20.equals(samlVersion)) {
    			xpath = SAMLConstants.XPATH_SAML_20_ASSERTION;
    		}
    		else {
    			xpath = SAMLConstants.XPATH_SAML_11_ASSERTION;
    		}
    		try {
    			Object o = xpathExpressionEngine.getMatchPattern(securityHeader, dnc, xpath, XPathReturnType.NODE);
    			samlElement = (SOAPElement) o;
    		} catch(XPathNotFoundException e) {
    			// ignore
    		}
    		
    		return samlElement;

    	}catch(Exception e){
    		if(this.log!=null)
    			this.log.error("getSAMLTokenInSecurityHeader error, saml version: ["+samlVersion+"]",e);
    		return null;
    	}
    }

    public String getSAMLTokenSubjectConfirmationMethodInSecurityHeader(OpenSPCoop2MessageFactory messageFactory, SOAPElement samlToken,String samlVersion){
    	try{
    			
    		DynamicNamespaceContext dnc = new DynamicNamespaceContext();
    		dnc.findPrefixNamespace(samlToken);
					
    		AbstractXPathExpressionEngine xpathExpressionEngine = new XPathExpressionEngine(messageFactory);
    		String xpath =null;
    		if(SecurityConstants.SAML_VERSION_XMLCONFIG_ID_VALUE_20.equals(samlVersion)) {
    			xpath = SAMLConstants.XPATH_SAML_20_ASSERTION_SUBJECT_CONFIRMATION_METHOD;
    		}
    		else {
    			xpath = SAMLConstants.XPATH_SAML_11_ASSERTION_SUBJECT_CONFIRMATION_METHOD;
    		}
    		String method = null;
			try {
				Object o = xpathExpressionEngine.getMatchPattern(samlToken, dnc, xpath, XPathReturnType.STRING);
				method = (String) o;
			} catch(XPathNotFoundException e) {
				// ignore
			}
    		
			return method;

    	}catch(Exception e){
    		if(this.log!=null)
    			this.log.error("getSAMLTokenSubjectConfirmationMethodInSecurityHeader error, saml version: ["+samlVersion+"]",e);
    		return null;
    	}	
}
    
}

