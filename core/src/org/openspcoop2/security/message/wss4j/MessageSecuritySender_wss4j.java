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


package org.openspcoop2.security.message.wss4j;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.xml.soap.SOAPMessage;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.phase.PhaseInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.IMessageSecuritySender;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.saml.SAMLBuilderConfig;
import org.openspcoop2.security.message.saml.SAMLCallbackHandler;
import org.openspcoop2.security.message.saml.SAMLUtilities;
import org.openspcoop2.security.message.utils.AttachmentProcessingPart;
import org.openspcoop2.security.message.utils.AttachmentsConfigReaderUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.id.IDUtilities;

/**
 * Classe per la gestione della WS-Security (WSDoAllSender).
 *
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MessageSecuritySender_wss4j implements IMessageSecuritySender{

	
     @Override
	public void process(MessageSecurityContext wssContext,OpenSPCoop2Message messageParam) throws SecurityException{
		try{ 	
			
			if(ServiceBinding.SOAP.equals(messageParam.getServiceBinding())==false){
				throw new SecurityException("WSS4J Engine usable only with SOAP Binding");
			}
			OpenSPCoop2SoapMessage message = messageParam.castAsSoap();
			
			
			// ** Inizializzo handler CXF **/
			
	        WSS4JOutInterceptor ohandler = new WSS4JOutInterceptor();
	        PhaseInterceptor<SoapMessage> handler = ohandler.createEndingInterceptor();
	        SoapMessage msgCtx = new SoapMessage(new MessageImpl());
	        Exchange ex = new ExchangeImpl();
	        ex.setInMessage(msgCtx);
	        msgCtx.setContent(SOAPMessage.class, message.getSOAPMessage());
	        List<?> results = new ArrayList<Object>();
	        msgCtx.put(WSHandlerConstants.RECV_RESULTS, results);
	        
	        
	        // ** Localizzo attachments da trattare **/
	        
	        AttachmentProcessingPart app = AttachmentsConfigReaderUtils.getSecurityOnAttachments(wssContext);
	        
	        
	        // ** Imposto configurazione nel messaggio **/
	        // NOTA: farlo dopo getSecurityOnAttachments poichè si modifica la regola di quali attachments trattare.
	        
	        setOutgoingProperties(wssContext,msgCtx,messageParam);
	        
	        
	        // ** Registro attachments da trattare **/
	        
	        List<Attachment> listAttachments = null;
	        if(app!=null){
	        	listAttachments = org.openspcoop2.security.message.wss4j.WSSUtilities.readAttachments(app, message);
	        	if(listAttachments!=null && listAttachments.size()>0){
	        		msgCtx.setAttachments(listAttachments);
	        	}
	        }
	        
	        
	        // ** Applico sicurezza tramite CXF **/
	        
	        handler.handleMessage(msgCtx);
	        wssContext.getLog().debug("Print wssSender results...");
	        org.openspcoop2.security.message.wss4j.WSSUtilities.printWSResult(wssContext.getLog(), results);
			
			
			// ** Riporto modifica degli attachments **/
			
			org.openspcoop2.security.message.wss4j.WSSUtilities.updateAttachments(listAttachments, message);
					
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

    private void setOutgoingProperties(MessageSecurityContext wssContext,SoapMessage msgCtx,OpenSPCoop2Message message) throws Exception{
    	boolean mustUnderstand = false;
    	boolean signatureUser = false;
    	boolean user = false;
    	Hashtable<String,Object> wssOutgoingProperties = wssContext.getOutgoingProperties();
		if (wssOutgoingProperties != null && wssOutgoingProperties.size() > 0) {
			
			// preprocess per SAML
			SAMLUtilities.injectSignaturePropRefIdIntoSamlConfig(wssOutgoingProperties);
			
			for (Enumeration<?> e = wssOutgoingProperties.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				Object oValue = wssOutgoingProperties.get(key);
				String value = null;
				if(oValue!=null && oValue instanceof String) {
					value = (String) oValue;
				}
				if (SecurityConstants.ENCRYPTION_USER.equals(key) && SecurityConstants.USE_REQ_SIG_CERT.equals(value)) {
					// value = ...;
				}
				
				// src/site/xdoc/migration/wss4j20.xml:the "samlPropFile" and "samlPropRefId" configuration tags have been removed. 
				// Per ottenere lo stesso effetto di poter utilizzare tale file di proprietà, si converta la proprietà nella nuova voce: 'samlCallbackRef'
				if(SecurityConstants.SAML_PROF_FILE.equals(key)){
					//System.out.println("CONVERT ["+key+"] ["+value+"] ...");
					SAMLBuilderConfig config = SAMLBuilderConfig.getSamlConfig(value);
					SAMLCallbackHandler samlCallbackHandler = new SAMLCallbackHandler(config);
					msgCtx.put(SecurityConstants.SAML_CALLBACK_REF, samlCallbackHandler);
				}
				else if(SecurityConstants.SAML_PROF_REF_ID.equals(key)){
					if(oValue instanceof Properties) {
						Properties p = (Properties) oValue;
						SAMLBuilderConfig config = SAMLBuilderConfig.getSamlConfig(p);
						SAMLCallbackHandler samlCallbackHandler = new SAMLCallbackHandler(config);
						msgCtx.put(SecurityConstants.SAML_CALLBACK_REF, samlCallbackHandler);
					}
					else {
						throw new Exception("Property ["+key+"] with uncorrect type: "+oValue.getClass().getName());
					}
				}
				else if(SecurityConstants.ENCRYPTION_PARTS.equals(key) || SecurityConstants.SIGNATURE_PARTS.equals(key)){
					msgCtx.put(key, normalizeWss4jParts(value,message));
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
						String id = key+"_"+IDUtilities.getUniqueSerialNumber();
						msgCtx.put(key, id);
						msgCtx.put(id, oValue);
					}
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
					bf.append("{"+SecurityConstants.PART_ELEMENT+"}"+SecurityConstants.CID_ATTACH_WSS4j);
				}
				else {
					bf.append("{"+SecurityConstants.PART_CONTENT+"}"+SecurityConstants.CID_ATTACH_WSS4j);
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





