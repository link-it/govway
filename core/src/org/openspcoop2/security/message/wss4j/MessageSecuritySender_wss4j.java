/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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


package org.openspcoop2.security.message.wss4j;


import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.callback.CallbackHandler;

import jakarta.xml.soap.SOAPMessage;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.phase.PhaseInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.KeystoreConstants;
import org.openspcoop2.security.message.IMessageSecuritySender;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.saml.SAMLBuilderConfig;
import org.openspcoop2.security.message.saml.SAMLCallbackHandler;
import org.openspcoop2.security.message.saml.SAMLUtilities;
import org.openspcoop2.security.message.utils.AttachmentProcessingPart;
import org.openspcoop2.security.message.utils.AttachmentsConfigReaderUtils;
import org.openspcoop2.security.message.utils.EncryptionBean;
import org.openspcoop2.security.message.utils.KeystoreUtils;
import org.openspcoop2.security.message.utils.SignatureBean;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.id.IDUtilities;

/**
 * Classe per la gestione della WS-Security (WSDoAllSender).
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MessageSecuritySender_wss4j implements IMessageSecuritySender{

	
     @Override
	public void process(MessageSecurityContext wssContext,OpenSPCoop2Message messageParam,org.openspcoop2.utils.Map<Object> ctx) throws SecurityException{
		try{ 	
			
			if(ServiceBinding.SOAP.equals(messageParam.getServiceBinding())==false){
				throw new SecurityException("WSS4J Engine usable only with SOAP Binding");
			}
			OpenSPCoop2SoapMessage message = messageParam.castAsSoap();
			
    		RequestInfo requestInfo = null;
    		if(ctx!=null && ctx.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
    			requestInfo = (RequestInfo) ctx.get(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
    		}
    		
			
			// ** Inizializzo handler CXF **/
			
	        WSS4JOutInterceptor ohandler = new WSS4JOutInterceptor();
	        PhaseInterceptor<SoapMessage> handler = ohandler.createEndingInterceptor();
	        SoapMessage msgCtx = new SoapMessage(new MessageImpl());
	        msgCtx.setVersion(MessageType.SOAP_12.equals(message.getMessageType()) ? org.apache.cxf.binding.soap.Soap12.getInstance() : org.apache.cxf.binding.soap.Soap11.getInstance());
			Exchange ex = new ExchangeImpl();
	        ex.setInMessage(msgCtx);
	        SOAPMessage soapMessage = MessageUtils.getSOAPMessage(message, false, message.getTransactionId());
	        msgCtx.setContent(SOAPMessage.class, soapMessage);
	        List<?> results = new ArrayList<>();
	        msgCtx.put(WSHandlerConstants.RECV_RESULTS, results);
	        
	        
	        // ** Localizzo attachments da trattare **/
	        
	        AttachmentProcessingPart app = AttachmentsConfigReaderUtils.getSecurityOnAttachments(wssContext);
	        
	        
	        // ** Imposto configurazione nel messaggio **/
	        // NOTA: farlo dopo getSecurityOnAttachments poichè si modifica la regola di quali attachments trattare.
	        
	        setOutgoingProperties(wssContext,msgCtx,messageParam,requestInfo,ctx);
	        
	        
	        // ** Registro attachments da trattare **/
	        
	        List<Attachment> listAttachments = null;
	        if(app!=null){
	        	listAttachments = org.openspcoop2.security.message.wss4j.WSSUtilities.readAttachments(app, message, msgCtx);
	        	if(listAttachments!=null && listAttachments.size()>0){
	        		msgCtx.setAttachments(listAttachments);
	        	}
	        }
	        
	        
	        // ** Applico sicurezza tramite CXF **/
	        
	        handler.handleMessage(msgCtx);
	        wssContext.getLog().debug("Print wssSender results...");
	        org.openspcoop2.security.message.wss4j.WSSUtilities.printWSResult(wssContext.getLog(), results);
			
			
			// ** Riporto modifica degli attachments **/
			
			org.openspcoop2.security.message.wss4j.WSSUtilities.updateAttachments(listAttachments, message, msgCtx);
					
		}
		catch(Exception e){
			
			String msg = Utilities.getInnerNotEmptyMessageException(e).getMessage();
			
			Throwable innerExc = Utilities.getLastInnerException(e);
			String innerMsg = null;
			if(innerExc!=null){
				innerMsg = innerExc.getMessage();
			}
			
			String messaggio = null;
			if(msg!=null){
				messaggio = new String(msg);
				if(innerMsg!=null && !innerMsg.equals(msg)){
					messaggio = messaggio + " ; " + innerMsg;
				}
			}
			else{
				if(innerMsg!=null){
					messaggio = innerMsg;
				}
			}
			
			// L'if scopre l'eventuale motivo preciso riguardo al fallimento della cifratura/firma.
			if(Utilities.existsInnerException(e, WSSecurityException.class)){
				Throwable t = Utilities.getLastInnerException(e);
				if(t instanceof WSSecurityException){
					if(messaggio!=null){
						messaggio = messaggio + " ; " + t.getMessage();
					}
					else{
						messaggio = t.getMessage();
					}
				}
			}
			
			SecurityException wssException = new SecurityException(messaggio, e);
			wssException.setMsgErrore(messaggio);
			throw wssException;
		}
		
    }

    private void setOutgoingProperties(MessageSecurityContext wssContext,SoapMessage msgCtx,OpenSPCoop2Message message, RequestInfo requestInfo,org.openspcoop2.utils.Map<Object> ctx) throws Exception{
    	boolean mustUnderstand = false;
    	boolean signatureUser = false;
    	boolean user = false;
    	Map<String,Object> wssOutgoingProperties = wssContext.getOutgoingProperties();
		if (wssOutgoingProperties != null && wssOutgoingProperties.size() > 0) {
			
			// preprocess per SAML
			SAMLUtilities.injectSignaturePropRefIdIntoSamlConfig(wssOutgoingProperties);
			
			// preprocess per multipropfile
			preprocessMultipropFile(wssContext, msgCtx, wssOutgoingProperties, requestInfo, ctx);
			
			for (String key : wssOutgoingProperties.keySet()) {
				Object oValue = wssOutgoingProperties.get(key);
				String value = null;
				if(oValue!=null && oValue instanceof String) {
					value = (String) oValue;
				}
//				if (SecurityConstants.ENCRYPTION_USER.equals(key) && SecurityConstants.USE_REQ_SIG_CERT.equals(value)) {
//					// value = ...;
//				}
				
				// src/site/xdoc/migration/wss4j20.xml:the "samlPropFile" and "samlPropRefId" configuration tags have been removed. 
				// Per ottenere lo stesso effetto di poter utilizzare tale file di proprietà, si converta la proprietà nella nuova voce: 'samlCallbackRef'
				if(SecurityConstants.SAML_PROF_FILE.equals(key)){
					//System.out.println("CONVERT ["+key+"] ["+value+"] ...");
					SAMLBuilderConfig config = SAMLBuilderConfig.getSamlConfig(value, requestInfo);
					SAMLCallbackHandler samlCallbackHandler = new SAMLCallbackHandler(config);
					msgCtx.put(SecurityConstants.SAML_CALLBACK_REF, samlCallbackHandler);
				}
				else if(SecurityConstants.SAML_PROF_REF_ID.equals(key)){
					if(oValue!=null && oValue instanceof Properties) {
						Properties p = (Properties) oValue;
						SAMLBuilderConfig config = SAMLBuilderConfig.getSamlConfig(p, requestInfo);
						SAMLCallbackHandler samlCallbackHandler = new SAMLCallbackHandler(config);
						msgCtx.put(SecurityConstants.SAML_CALLBACK_REF, samlCallbackHandler);
					}
					else {
						throw new Exception("Property ["+key+"] with uncorrect type: "+(oValue!=null ? oValue.getClass().getName() : "value is null"));
					}
				}
				else if(SecurityConstants.ENCRYPTION_PARTS.equals(key) || SecurityConstants.SIGNATURE_PARTS.equals(key)){
					if(value!=null) {
						msgCtx.put(key, normalizeWss4jParts(value,message));
					}
				}
				else if(SecurityConstants.PASSWORD_CALLBACK_REF.equals(key)) {
					msgCtx.put(key, oValue);
				}
				else if(SecurityConstants.SIGNATURE_PROPERTY_REF_ID.equals(key) || 
						SecurityConstants.SIGNATURE_VERIFICATION_PROPERTY_REF_ID.equals(key) || 
						SecurityConstants.ENCRYPTION_PROPERTY_REF_ID.equals(key) || 
						SecurityConstants.DECRYPTION_PROPERTY_REF_ID.equals(key) ) {
					if(value!=null) {
						msgCtx.put(key, value);
					}
					else { 
						String id = key+"_"+IDUtilities.getUniqueSerialNumber("wssSecurity.setOutgoingProperties");
						msgCtx.put(key, id);
						msgCtx.put(id, oValue);
						if(oValue!=null && oValue instanceof Properties) {
							Properties p = (Properties) oValue;
							p.put(KeystoreConstants.PROPERTY_REQUEST_INFO, requestInfo);
						}
					}
				}
				else if(SecurityConstants.ENCRYPT_ACTION_OLD.equals(key)) {
					// backward compatibility per adeguamento costante rispetto a wss4j 2.3.x
					msgCtx.put(SecurityConstants.ENCRYPTION_ACTION, value);
				}
				else{
					msgCtx.put(key, value);
					if(SecurityConstants.MUST_UNDERSTAND.equals(key)){
						mustUnderstand = true;
					}
					else if(SecurityConstants.SIGNATURE_USER.equals(key)){
						signatureUser = true;
					}
					else if(SecurityConstants.USER.equals(key)){
						user = true;
					}
				}
			}
		}
		if(!mustUnderstand){
			//Il mustUnderstand non e' stato specificato. Lo imposto a false.
			msgCtx.put(SecurityConstants.MUST_UNDERSTAND , SecurityConstants.FALSE);
		}
		if(wssContext.getActor()!=null){
			msgCtx.put(SecurityConstants.ACTOR, wssContext.getActor());
		}
		if(signatureUser && !user) {
			// fix: Caused by: org.apache.cxf.binding.soap.SoapFault: Empty username for specified action.
	        // at org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor$WSS4JOutInterceptorInternal.handleMessageInternal(WSS4JOutInterceptor.java:230) ~[cxf-rt-ws-security-3.2.6.jar:3.1.7]
	        //        at org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor$WSS4JOutInterceptorInternal.handleMessage(WSS4JOutInterceptor.java:135) ~[cxf-rt-ws-security-3.2.6.jar:3.1.7]
			msgCtx.put(SecurityConstants.USER, (String) msgCtx.get(SecurityConstants.SIGNATURE_USER));
		}
    }
    
    private void preprocessMultipropFile(MessageSecurityContext wssContext,SoapMessage msgCtx,Map<String,Object> wssOutgoingProperties, RequestInfo requestInfo,org.openspcoop2.utils.Map<Object> ctx) throws FileNotFoundException, UtilsException, SecurityException, URISyntaxException {
    	
    	String forceSignatureUser = null;
    	String forceEncryptionUser = null;
    	
        HashMap<String, String> mapAliasToPassword = new HashMap<>();
    	for (String key : wssOutgoingProperties.keySet()) {
	    	if(SecurityConstants.SIGNATURE_MULTI_PROPERTY_FILE.equals(key)) {
				SignatureBean bean = KeystoreUtils.getSenderSignatureBean(wssContext, ctx);
				if(bean.getMultiKeystore()==null) {
					throw new SecurityException("Multiproperty config not exists");
				}
				String keyAlias = bean.getUser();
				String internalAlias = bean.getMultiKeystore().getInternalConfigAlias(keyAlias);
				Properties p = new Properties();
				p.put(KeystoreConstants.PROPERTY_KEYSTORE_PATH, bean.getMultiKeystore().getKeystorePath(internalAlias));
				p.put(KeystoreConstants.PROPERTY_KEYSTORE_PASSWORD, bean.getMultiKeystore().getKeystorePassword(internalAlias));
				p.put(KeystoreConstants.PROPERTY_KEYSTORE_TYPE, bean.getMultiKeystore().getKeystoreType(internalAlias));
				p.put(KeystoreConstants.PROPERTY_PROVIDER, KeystoreConstants.PROVIDER_GOVWAY);
				p.put(KeystoreConstants.PROPERTY_REQUEST_INFO, requestInfo);
				String id = SecurityConstants.SIGNATURE_PROPERTY_REF_ID+"_"+IDUtilities.getUniqueSerialNumber("wssSecurity.setOutgoingProperties");
				msgCtx.put(SecurityConstants.SIGNATURE_PROPERTY_REF_ID, id);
				msgCtx.put(id, p);
				
				String password = bean.getPassword();
				msgCtx.put(SecurityConstants.SIGNATURE_PASSWORD, bean.getPassword());
				mapAliasToPassword.put(keyAlias, password);

				forceSignatureUser = keyAlias;
			}
			else if(SecurityConstants.ENCRYPTION_MULTI_PROPERTY_FILE.equals(key)) {
				EncryptionBean bean = KeystoreUtils.getSenderEncryptionBean(wssContext, ctx);
				if(bean.getMultiKeystore()==null) {
					throw new SecurityException("Multiproperty config not exists");
				}
				String keyAlias = bean.getUser();
				String internalAlias = bean.getMultiKeystore().getInternalConfigAlias(keyAlias);
				Properties p = new Properties();
				p.put(KeystoreConstants.PROPERTY_KEYSTORE_PATH, bean.getMultiKeystore().getKeystorePath(internalAlias));
				p.put(KeystoreConstants.PROPERTY_KEYSTORE_PASSWORD, bean.getMultiKeystore().getKeystorePassword(internalAlias));
				p.put(KeystoreConstants.PROPERTY_KEYSTORE_TYPE, bean.getMultiKeystore().getKeystoreType(internalAlias));
				p.put(KeystoreConstants.PROPERTY_PROVIDER, KeystoreConstants.PROVIDER_GOVWAY);
				p.put(KeystoreConstants.PROPERTY_REQUEST_INFO, requestInfo);
				String id = SecurityConstants.ENCRYPTION_PROPERTY_REF_ID +"_"+IDUtilities.getUniqueSerialNumber("wssSecurity.setOutgoingProperties");
				msgCtx.put(SecurityConstants.ENCRYPTION_PROPERTY_REF_ID , id);
				msgCtx.put(id, p);
				
				String password = bean.getPassword();
				mapAliasToPassword.put(keyAlias, password);

				forceEncryptionUser = keyAlias;
			}
    	}
    	
        if (!mapAliasToPassword.isEmpty()) {
            CallbackHandler pwCallbackHandler = MessageSecurityContext.newCallbackHandler(mapAliasToPassword);
            msgCtx.put(SecurityConstants.PASSWORD_CALLBACK_REF, pwCallbackHandler);
        }

    	if(forceSignatureUser!=null) {
    		wssOutgoingProperties.remove(SecurityConstants.SIGNATURE_USER);
    		wssOutgoingProperties.put(SecurityConstants.SIGNATURE_USER, forceSignatureUser);
    	}
    	if(forceEncryptionUser!=null) {
    		wssOutgoingProperties.remove(SecurityConstants.ENCRYPTION_USER);
    		wssOutgoingProperties.put(SecurityConstants.ENCRYPTION_USER, forceEncryptionUser);
    	}
    }
    
    private String normalizeWss4jParts(String parts,OpenSPCoop2Message message){
    	StringBuilder bf = new StringBuilder();
    	String[]split = ((String)parts).split(";");
		for (int i = 0; i < split.length; i++) {
			if(i>0){
				bf.append(";");
			}
			String n = split[i].trim();
			if(n.contains("{"+SecurityConstants.NAMESPACE_ATTACH+"}")){
				if(n.startsWith("{"+SecurityConstants.PART_ELEMENT+"}")){
					bf.append("{"+SecurityConstants.PART_ELEMENT+"}"+SecurityConstants.CID_ATTACH_WSS4J);
				}
				else {
					bf.append("{"+SecurityConstants.PART_CONTENT+"}"+SecurityConstants.CID_ATTACH_WSS4J);
				}
			}
			else{
				bf.append(n);
			}
		}
		//System.out.println("PRIMA ["+parts+"] DOPO ["+bf.toString()+"]");
		
		String newParts = bf.toString();
		
		while(newParts.contains(SecurityConstants.SOAP_NAMESPACE_TEMPLATE)) {
			String namespace = MessageType.SOAP_11.equals(message.getMessageType()) ? Costanti.SOAP_ENVELOPE_NAMESPACE : Costanti.SOAP12_ENVELOPE_NAMESPACE;
			newParts = newParts.replace(SecurityConstants.SOAP_NAMESPACE_TEMPLATE, namespace);
		}
		
		return newParts;
    }
 
}





