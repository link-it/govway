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

package org.openspcoop2.protocol.modipa.builder;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.soap.wsaddressing.WSAddressingHeader;
import org.openspcoop2.message.soap.wsaddressing.WSAddressingUtilities;
import org.openspcoop2.message.soap.wsaddressing.WSAddressingValue;
import org.openspcoop2.message.xml.DynamicNamespaceContextFactory;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModIKeystoreConfig;
import org.openspcoop2.protocol.modipa.utils.ModISecurityConfig;
import org.openspcoop2.protocol.modipa.utils.ModIUtilities;
import org.openspcoop2.protocol.modipa.validator.ModISOAPSecurity;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.security.keystore.FixTrustAnchorsNotEmpty;
import org.openspcoop2.security.keystore.KeystoreConstants;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContextParameters;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.constants.SignatureDigestAlgorithm;
import org.openspcoop2.security.message.engine.MessageSecurityContext_impl;
import org.openspcoop2.security.message.saml.SAMLBuilderConfigConstants;
import org.openspcoop2.security.message.wss4j.MessageSecuritySender_wss4j;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.slf4j.Logger;
import org.w3c.dom.NodeList;

/**
 * ModIImbustamentoSoap
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIImbustamentoSoap {

	private Logger log;
	private ModIProperties modiProperties;
	public ModIImbustamentoSoap(Logger log) throws ProtocolException {
		this.log = log;
		this.modiProperties = ModIProperties.getInstance();
	}
	
	public void addInteractionProfile(OpenSPCoop2Message msg, Busta busta, RuoloMessaggio ruoloMessaggio,
			String asyncInteractionType, String asyncInteractionRole,
			String replyTo,
			AccordoServizioParteComune apiContenenteRisorsa, String azione) throws Exception {
		
		OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
		
		if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)) {
			
			// Flusso di Richiesta
			
			if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionType)) {
			
				if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
				
					if(this.modiProperties.isSoapSecurityTokenPushReplyToUpdateOrCreateInFruizione()) {
						
						ModIUtilities.addSOAPHeaderReplyTo(soapMessage, replyTo); // aggiorna il valore se già esistente
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_REPLY_TO, replyTo);
					}
					else {
						String replyToFound = ModIUtilities.getSOAPHeaderReplyToValue(soapMessage);
						if(replyToFound!=null && !"".equals(replyToFound)) {
							busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_REPLY_TO, replyToFound);
						}
						else {
							ProtocolException pe = new ProtocolException("Header SOAP '"+this.modiProperties.getSoapReplyToName()+"', richiesto dal profilo non bloccante PUSH, non trovato");
							pe.setInteroperabilityError(true);
							throw pe;
						}
					}
					
				}
				else if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA.equals(asyncInteractionRole)) {
					
					processCorrelationId(soapMessage, busta, true, asyncInteractionType, true);

				}
				
			}
			else {
				
				// pull
				if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO.equals(asyncInteractionRole) ||
						ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA.equals(asyncInteractionRole)) {
					
					processCorrelationId(soapMessage, busta, true, asyncInteractionType, true);
					
				}
				
			}
		}
		else {
			
			// Flusso di Risposta
			
			if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionType)) {
				
				if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
				
					boolean foundCorrelationId = processCorrelationId(soapMessage, busta, false, asyncInteractionType, false);
					
					if(!foundCorrelationId) {
					
						if(this.modiProperties.isSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists()) {
							String idTransazione = msg.getTransactionId();
							ModIUtilities.addSOAPHeaderCorrelationId(soapMessage, idTransazione); 
							busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE, idTransazione);
							busta.setCollaborazione(idTransazione);
						}
						else {
							ProtocolException pe = new ProtocolException("Header SOAP '"+this.modiProperties.getSoapCorrelationIdName()+"', richiesto dal profilo non bloccante PUSH, non trovato");
							pe.setInteroperabilityError(true);
							throw pe;
						}
					}
					
				}
				
			}
			else {
				
				// pull
				
				if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole) ) {
					
					boolean foundCorrelationId = processCorrelationId(soapMessage, busta, false, asyncInteractionType, false);
					
					if(!foundCorrelationId) {
					
						if(this.modiProperties.isSoapSecurityTokenPullCorrelationIdUseTransactionIdIfNotExists()) {
							String idTransazione = msg.getTransactionId();
							ModIUtilities.addSOAPHeaderCorrelationId(soapMessage, idTransazione); 
							busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE, idTransazione);
							busta.setCollaborazione(idTransazione);
						}
						else {
							ProtocolException pe = new ProtocolException("Header SOAP '"+this.modiProperties.getSoapCorrelationIdName()+"', richiesto dal profilo non bloccante PULL, non trovato");
							pe.setInteroperabilityError(true);
							throw pe;
						}
					}
					
				}
				
			}
		}
		
	}
	
	private boolean processCorrelationId(OpenSPCoop2SoapMessage soapMessage, Busta busta, boolean notFoundException, String profilo, boolean useAlternativeMethod) throws Exception {
		String correlationIdFound = ModIUtilities.getSOAPHeaderCorrelationIdValue(soapMessage); 
		if(correlationIdFound!=null && !"".equals(correlationIdFound)) {
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE, correlationIdFound);
			if(correlationIdFound.length()<=255) {
				busta.setCollaborazione(correlationIdFound);
			}
			return true;
		}
		
		if(useAlternativeMethod) {
		
			String headerCorrelationIdHttp = this.modiProperties.getRestCorrelationIdHeader();
			String correlationIdFoundHttp = null;
			if(soapMessage.getTransportRequestContext()!=null) {
				correlationIdFoundHttp = soapMessage.getTransportRequestContext().getParameterTrasporto(headerCorrelationIdHttp);
			}
			else if(soapMessage.getTransportResponseContext()!=null) {
				correlationIdFoundHttp = soapMessage.getTransportResponseContext().getParameterTrasporto(headerCorrelationIdHttp);
			}
			
			if(correlationIdFoundHttp!=null && !"".equals(correlationIdFoundHttp)) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE, correlationIdFoundHttp);
				if(correlationIdFoundHttp.length()<=255) {
					busta.setCollaborazione(correlationIdFoundHttp);
				}
				ModIUtilities.addSOAPHeaderCorrelationId(soapMessage, correlationIdFoundHttp); 
				return true;
			}
			else if(busta.getCollaborazione()!=null) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE, busta.getCollaborazione());
				ModIUtilities.addSOAPHeaderCorrelationId(soapMessage, busta.getCollaborazione()); 
				return true;
			}
			else if(busta.getRiferimentoMessaggio()!=null) {
				busta.setCollaborazione(busta.getRiferimentoMessaggio());
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE, busta.getRiferimentoMessaggio());
				ModIUtilities.addSOAPHeaderCorrelationId(soapMessage, busta.getRiferimentoMessaggio());
				return true;
			}
			
		}
		
		if(notFoundException) {
			ProtocolException pe = new ProtocolException("Header SOAP '"+this.modiProperties.getSoapCorrelationIdName()+"', richiesto dal profilo non bloccante "+profilo+", non trovato");
			pe.setInteroperabilityError(true);
			throw pe;
		}
		else {
			return false;
		}
		
	}
	
	public SOAPEnvelope addSecurity(OpenSPCoop2Message msg, Context context, ModIKeystoreConfig keystoreConfig, ModISecurityConfig securityConfig,
			Busta busta, String securityMessageProfile, boolean corniceSicurezza, RuoloMessaggio ruoloMessaggio, boolean includiRequestDigest) throws Exception {
	
		ModIProperties modIProperties = ModIProperties.getInstance();
	
		OpenSPCoop2MessageFactory messageFactory = msg!=null ? msg.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		
		OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
		SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
		
		boolean integrita = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(securityMessageProfile) || 
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(securityMessageProfile);
		

		/*
		 * == request digest ==
		 */
		SOAPHeaderElement requestDigest = null;
		if(integrita && RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio) && includiRequestDigest) {
			if(context.containsKey(ModICostanti.MODIPA_CONTEXT_REQUEST_DIGEST)) {
				Object o = context.getObject(ModICostanti.MODIPA_CONTEXT_REQUEST_DIGEST);
				NodeList nodeList = (NodeList) o;
				requestDigest = ModIUtilities.addSOAPHeaderRequestDigest(soapMessage, nodeList); 
			}
		}
		
		
		/*
		 * == wsaddressing ==
		 */
		
		WSAddressingUtilities wsaddressingUtilities = new WSAddressingUtilities(this.log);
		WSAddressingValue wsAddressingValue = new WSAddressingValue();
		
		if(busta.getID()!=null) {
			wsAddressingValue.setId(busta.getID());
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_ID, busta.getID());
		}
		
		if(securityConfig.getAudience()!=null) {
			wsAddressingValue.setTo(securityConfig.getAudience());
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SOAP_WSA_TO, securityConfig.getAudience());
		}
		
		if(securityConfig.getClientId()!=null) {
			wsAddressingValue.setFrom(securityConfig.getClientId());
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SOAP_WSA_FROM, securityConfig.getClientId());
		}
		
		if(busta.getRiferimentoMessaggio()!=null) {
			wsAddressingValue.setRelatesTo(busta.getRiferimentoMessaggio());
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_RELATES_TO, busta.getRiferimentoMessaggio());
		}
		
		wsAddressingValue.setReplyToAnonymouys();
		
		if(soapMessage.getSoapAction()!=null) {
			String soapAction = soapMessage.getSoapAction();
			soapAction = soapAction.trim();
			if(soapAction.startsWith("\"")) {
				if(soapAction.length()>1) {
					soapAction = soapAction.substring(1);
				}
			}
			if(soapAction.endsWith("\"")) {
				if(soapAction.length()>1) {
					soapAction = soapAction.substring(0,(soapAction.length()-1));
				}
			}
			wsAddressingValue.setAction(soapAction);
		}
		
		WSAddressingHeader wsAddressingHeaders = wsaddressingUtilities.build(soapMessage, 
				modIProperties.getSoapWSAddressingActor(), modIProperties.isSoapWSAddressingMustUnderstand(), 
				wsAddressingValue);
		wsaddressingUtilities.addHeader(wsAddressingHeaders, soapMessage);
		
		
		
		/*
		 * == signature ==
		 */
		
		MessageSecuritySender_wss4j wss4jSignature = new MessageSecuritySender_wss4j();
		MessageSecurityContextParameters messageSecurityContextParameters = new MessageSecurityContextParameters();
		messageSecurityContextParameters.setFunctionAsClient(true);
		messageSecurityContextParameters.setPrefixWsuId(OpenSPCoop2Properties.getInstance().getPrefixWsuId()); // NOTA: deve essere lo stesso di govway usato in altri profili
		MessageSecurityContext messageSecurityContext = new MessageSecurityContext_impl(messageSecurityContextParameters);
		Hashtable<String,Object> secProperties = new Hashtable<>();
		secProperties.put(SecurityConstants.SECURITY_ENGINE, SecurityConstants.SECURITY_ENGINE_WSS4J);
		if(modIProperties.getSoapSecurityTokenActor()!=null && !"".equals(modIProperties.getSoapSecurityTokenActor())) {
			secProperties.put(SecurityConstants.ACTOR, modIProperties.getSoapSecurityTokenActor());
		}
		secProperties.put(SecurityConstants.MUST_UNDERSTAND, modIProperties.isSoapSecurityTokenMustUnderstand()+"");
		secProperties.put(SecurityConstants.TIMESTAMP_TTL, securityConfig.getTtlSeconds()+"");
		secProperties.put(SecurityConstants.IS_BSP_COMPLIANT, SecurityConstants.TRUE);
		
		// cornice sicurezza
		if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio)) {
			corniceSicurezza = false; // permessa solo per i messaggi di richiesta
		}
		if(corniceSicurezza) {
			addCorniceSicurezza(secProperties, msg, context, busta, securityConfig);
		}
		
		// action
		StringBuilder bfAction = new StringBuilder();
		if(corniceSicurezza) {
			bfAction.append(SecurityConstants.ACTION_SAML_TOKEN_UNSIGNED).append(" ");
		}
		bfAction.append(SecurityConstants.TIMESTAMP_ACTION).append(" ").append(SecurityConstants.SIGNATURE_ACTION);
		secProperties.put(SecurityConstants.ACTION, bfAction.toString());
		
		// parti da firmare
		StringBuilder bf = new StringBuilder();
		bf.append("{Element}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp");
		if(wsAddressingHeaders.getTo()!=null) {
			if(bf.length()>0) {
				bf.append(";");
			}
			bf.append("{Element}{").append(wsAddressingHeaders.getTo().getNamespaceURI()).append("}").append(wsAddressingHeaders.getTo().getLocalName());
		}
		if(wsAddressingHeaders.getFrom()!=null) {
			if(bf.length()>0) {
				bf.append(";");
			}
			bf.append("{Element}{").append(wsAddressingHeaders.getFrom().getNamespaceURI()).append("}").append(wsAddressingHeaders.getFrom().getLocalName());
		}
		if(wsAddressingHeaders.getAction()!=null) {
			if(bf.length()>0) {
				bf.append(";");
			}
			bf.append("{Element}{").append(wsAddressingHeaders.getAction().getNamespaceURI()).append("}").append(wsAddressingHeaders.getAction().getLocalName());
		}
		if(wsAddressingHeaders.getId()!=null) {
			if(bf.length()>0) {
				bf.append(";");
			}
			bf.append("{Element}{").append(wsAddressingHeaders.getId().getNamespaceURI()).append("}").append(wsAddressingHeaders.getId().getLocalName());
		}
		if(wsAddressingHeaders.getRelatesTo()!=null) {
			if(bf.length()>0) {
				bf.append(";");
			}
			bf.append("{Element}{").append(wsAddressingHeaders.getRelatesTo().getNamespaceURI()).append("}").append(wsAddressingHeaders.getRelatesTo().getLocalName());
		}
		if(wsAddressingHeaders.getReplyTo()!=null) {
			if(bf.length()>0) {
				bf.append(";");
			}
			bf.append("{Element}{").append(wsAddressingHeaders.getReplyTo().getNamespaceURI()).append("}").append(wsAddressingHeaders.getReplyTo().getLocalName());
		}
		if(wsAddressingHeaders.getFaultTo()!=null) {
			if(bf.length()>0) {
				bf.append(";");
			}
			bf.append("{Element}{").append(wsAddressingHeaders.getFaultTo().getNamespaceURI()).append("}").append(wsAddressingHeaders.getFaultTo().getLocalName());
		}
		if(integrita) {
			if(bf.length()>0) {
				bf.append(";");
			}
			bf.append("{Element}{").append(soapEnvelope.getNamespaceURI()).append("}Body");
		}
		if(corniceSicurezza) {
			if(bf.length()>0) {
				bf.append(";");
			}
			bf.append("{Element}{").append(Costanti.SAML_20_NAMESPACE).append("}Assertion");
		}
		if(requestDigest!=null) {
			if(bf.length()>0) {
				bf.append(";");
			}
			bf.append("{Element}{").append(requestDigest.getNamespaceURI()).append("}").append(requestDigest.getLocalName());
		}
		secProperties.put(SecurityConstants.SIGNATURE_PARTS, bf.toString());
		
		// algoritmi
		secProperties.put(SecurityConstants.SIGNATURE_ALGORITHM, securityConfig.getAlgorithm());
		secProperties.put(SecurityConstants.SIGNATURE_C14N_ALGORITHM, securityConfig.getC14nAlgorithm());
		secProperties.put(SecurityConstants.SIGNATURE_DIGEST_ALGORITHM, securityConfig.getDigestAlgorithm());
		
		// spedizione certificato
		secProperties.put(SecurityConstants.SIGNATURE_KEY_IDENTIFIER, securityConfig.getKeyIdentifierMode());
		if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_BINARY_SECURITY_TOKEN.equals(securityConfig.getKeyIdentifierMode())) {
			secProperties.put(SecurityConstants.KEY_IDENTIFIER_BST_DIRECT_REFERENCE_USE_SINGLE_CERTIFICATE, securityConfig.isUseSingleCertificate()+"");
		}
		else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_SECURITY_TOKEN_REFERENCE.equals(securityConfig.getKeyIdentifierMode()) 
				||
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_THUMBPRINT.equals(securityConfig.getKeyIdentifierMode())
				||
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_SKI.equals(securityConfig.getKeyIdentifierMode())) {
			secProperties.put(SecurityConstants.KEY_IDENTIFIER_INCLUDE_SIGNATURE_TOKEN, securityConfig.isIncludeSignatureToken()+"");
		}
		
		// keystore
		Properties pKeystore = new Properties();
		//pKeystore.put(KeystoreConstants.PROPERTY_PROVIDER, KeystoreConstants.PROVIDER_DEFAULT);
		pKeystore.put(KeystoreConstants.PROPERTY_PROVIDER, KeystoreConstants.PROVIDER_GOVWAY);
		pKeystore.put(KeystoreConstants.PROPERTY_KEYSTORE_TYPE, keystoreConfig.getSecurityMessageKeystoreType());
		pKeystore.put(KeystoreConstants.PROPERTY_KEYSTORE_PASSWORD, keystoreConfig.getSecurityMessageKeystorePassword());
		if(keystoreConfig.getSecurityMessageKeystorePath()!=null) {
			pKeystore.put(KeystoreConstants.PROPERTY_KEYSTORE_PATH, keystoreConfig.getSecurityMessageKeystorePath());
		}
		else {
			pKeystore.put(KeystoreConstants.PROPERTY_KEYSTORE_ARCHIVE, keystoreConfig.getSecurityMessageKeystoreArchive());
		}
		secProperties.put(SecurityConstants.SIGNATURE_PROPERTY_REF_ID, pKeystore);
		
		secProperties.put(SecurityConstants.SIGNATURE_USER, keystoreConfig.getSecurityMessageKeyAlias());
		secProperties.put(SecurityConstants.SIGNATURE_PASSWORD, keystoreConfig.getSecurityMessageKeyPassword());
		secProperties.put(SecurityConstants.PASSWORD_CALLBACK_REF, SecurityConstants.TRUE);
		
		// setProperties
		messageSecurityContext.setOutgoingProperties(secProperties, false);
				
		// firma
		wss4jSignature.process(messageSecurityContext, msg);
		
		// Aggiungo a traccia informazioni sul certificato utilizzato
		KeyStore ks = null;
		if(keystoreConfig.getSecurityMessageKeystorePath()!=null) {
			MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(keystoreConfig.getSecurityMessageKeystorePath(), keystoreConfig.getSecurityMessageKeystoreType(), 
					keystoreConfig.getSecurityMessageKeystorePassword());
			if(merlinKs==null) {
				throw new Exception("Accesso al keystore '"+keystoreConfig.getSecurityMessageKeystorePath()+"' non riuscito");
			}
			ks = merlinKs.getKeyStore();
		}
		else {
			ks = KeyStore.getInstance(keystoreConfig.getSecurityMessageKeystoreType());
			try(ByteArrayInputStream bin = new ByteArrayInputStream(keystoreConfig.getSecurityMessageKeystoreArchive())){
				ks.load(bin, keystoreConfig.getSecurityMessageKeystorePassword().toCharArray());
			}
			FixTrustAnchorsNotEmpty.addCertificate(ks);
		}
		Certificate certificate = ks.getCertificate(keystoreConfig.getSecurityMessageKeyAlias());
		if(certificate!=null && certificate instanceof X509Certificate) {
			X509Certificate x509 = (X509Certificate) certificate;
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_X509_SUBJECT, x509.getSubjectX500Principal().toString());
			if(x509.getIssuerX500Principal()!=null) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_X509_ISSUER, x509.getIssuerX500Principal().toString());
			}
		}
		
		
		/*
		 * == header per tracciatura ==
		 */
		
		SOAPHeaderElement securityHeader = messageSecurityContext.getSecurityHeader(msg, modIProperties.getSoapSecurityTokenActor());
		
		DynamicNamespaceContext dnc = DynamicNamespaceContextFactory.getInstance(messageFactory).getNamespaceContext(securityHeader);
		String wsuNamespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
		XPathExpressionEngine xpathEngine = new XPathExpressionEngine(messageFactory);
		
		String patternCreated = "//{"+wsuNamespace+"}Timestamp/{"+wsuNamespace+"}Created/text()";
		String created = null;
		try {
			created = xpathEngine.getStringMatchPattern(securityHeader, dnc, patternCreated);
		}catch(XPathNotFoundException notFound) {}
		if(created!=null) {
			java.time.Instant iCreated = java.time.Instant.parse(created);
			Date dCreated = new Date(iCreated.toEpochMilli());
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_IAT, DateUtils.getSimpleDateFormatMs().format(dCreated));
		}
		
		String patternExpires = "//{"+wsuNamespace+"}Timestamp/{"+wsuNamespace+"}Expires/text()";
		String expires = null;
		try {
			expires = xpathEngine.getStringMatchPattern(securityHeader, dnc, patternExpires);
		}catch(XPathNotFoundException notFound) {}
		if(expires!=null) {
			java.time.Instant iExpires = java.time.Instant.parse(expires);
			Date dExpires = new Date(iExpires.toEpochMilli());
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_EXP, DateUtils.getSimpleDateFormatMs().format(dExpires));
		}
		
		//WSAddressingHeader wsAddressingHeader = wsaddressingUtilities.read(soapMessage, this.modiProperties.getSoapWSAddressingActor(), true);
		
		ModISOAPSecurity soapSecurity = new ModISOAPSecurity();		
		soapSecurity.setSecurityHeader(securityHeader);
		soapSecurity.setWsAddressingHeader(wsAddressingHeaders);
		soapSecurity.setRequestDigestHeader(requestDigest);
		if(integrita) {
			QName qname = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
			String wsuIdBodyRef = msg.castAsSoap().getSOAPBody().getAttributeValue(qname);
			soapSecurity.setWsuIdBodyRef(wsuIdBodyRef);
			
			if(wsuIdBodyRef!=null) {
				String digestNamespace = "http://www.w3.org/2000/09/xmldsig#";
				String digestReferencePattern = "//{"+digestNamespace+"}:Reference[@URI='#"+wsuIdBodyRef+"']";
				String digestValuePattern = digestReferencePattern+"/{"+digestNamespace+"}:DigestValue/text()";
				String digestAlgorithmPattern = digestReferencePattern+"/{"+digestNamespace+"}:DigestMethod/@Algorithm";

				String digestValue = null;
				try {
					digestValue = xpathEngine.getStringMatchPattern(securityHeader, dnc, digestValuePattern);
				}catch(XPathNotFoundException notFound) {}
				String digestAlgorithm = null;
				try {
					digestAlgorithm = xpathEngine.getStringMatchPattern(securityHeader, dnc, digestAlgorithmPattern);
				}catch(XPathNotFoundException notFound) {}
				
				if(digestValue!=null && digestAlgorithm!=null) {
					SignatureDigestAlgorithm s = SignatureDigestAlgorithm.toEnumConstant(digestAlgorithm);
					String digestValueBusta = s!=null ? (s.name()+"=") : "";
					digestValueBusta = digestValueBusta + digestValue;
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_DIGEST, digestValueBusta);
				}
			}
		}
		
		return soapSecurity.buildTraccia(msg.getMessageType());
	}
	
	private void addCorniceSicurezza(Hashtable<String,Object> secProperties, 
			OpenSPCoop2Message msg, Context context, Busta busta,
			ModISecurityConfig securityConfig) throws Exception {
		
		String nomeSoggettoMittente = busta.getMittente();
		
		Map<String, Object> dynamicMap = DynamicUtils.buildDynamicMap(msg, context, busta, this.log);
		
		String attributeNameCodiceEnte = this.modiProperties.getSicurezzaMessaggio_corniceSicurezza_soap_codice_ente();
		String codiceEnte = ModIUtilities.getDynamicValue("CorniceSicurezza-CodiceEnte", securityConfig.getCorniceSicurezzaCodiceEnteRule(), dynamicMap, context);
			
		String attributeNameUser = this.modiProperties.getSicurezzaMessaggio_corniceSicurezza_soap_user();
		String utente = ModIUtilities.getDynamicValue("CorniceSicurezza-User", securityConfig.getCorniceSicurezzaUserRule(), dynamicMap, context);
		
		String attributeNameIpUser = this.modiProperties.getSicurezzaMessaggio_corniceSicurezza_soap_ipuser();
		String indirizzoIpPostazione = ModIUtilities.getDynamicValue("CorniceSicurezza-IPUser", securityConfig.getCorniceSicurezzaIpUserRule(), dynamicMap, context);
		
		Properties pSaml = new Properties();
		
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION, SecurityConstants.FALSE); // lo faccio globalmente!
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SIGN_ASSERTION_SEND_KEY_VALUE, SecurityConstants.FALSE); // lo faccio globalmente!
		
		//       410 | signatureDigestAlgorithm                                                       | http://www.w3.org/2001/04/xmlenc#sha256           | 360
		//       410 | signatureC14nAlgorithmExclusive                                                | http://www.w3.org/2001/10/xml-exc-c14n#           | 371
		//      410 | signatureAlgorithm                                                             | http://www.w3.org/2001/04/xmldsig-more#rsa-sha256 | 374

		
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_VERSION, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_VERSION_20);
		
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ISSUER_VALUE, nomeSoggettoMittente);
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ISSUER_FORMAT, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_UNSPECIFIED);
		
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_VALUE, codiceEnte);
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_NAMEID_FORMAT_VALUE_UNSPECIFIED);
		busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CORNICE_SICUREZZA_ENTE, codiceEnte);
		
		boolean senderVouche = true;
		if(senderVouche) {
			pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_SENDER_VOUCHES);
		}
		else {
			pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_VALUE_BEARER);
		}
		
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_STATEMENT_ENABLED, SecurityConstants.TRUE);
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_AUTHN_VALUE_UNSPECIFIED);

		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_CONDITIONS_DATA_NOT_ON_OR_AFTER, "60");
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_CONDITIONS_DATA_NOT_BEFORE, "0");
		
		//pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX+".enabled", SecurityConstants.TRUE);
		
		if(attributeNameCodiceEnte!=null && !"".equals(attributeNameCodiceEnte)) {
			// se si desidera avere un attributo anche per il codice utente oltre al saml2:subject
			
			pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX+"ATTR_0"+
					SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_QUALIFIED_NAME, attributeNameCodiceEnte);
			pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX+"ATTR_0"+
					SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME, 
					SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_VALUE_UNSPECIFIED);
			pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX+"ATTR_0"+
					SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_VALUE_SEPARATOR, ",");
			pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX+"ATTR_0"+
					SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_VALUE, codiceEnte);
			
		}
		
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX+"ATTR_1"+
				SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_QUALIFIED_NAME, attributeNameUser);
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX+"ATTR_1"+
				SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME, 
				SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_VALUE_UNSPECIFIED);
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX+"ATTR_1"+
				SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_VALUE_SEPARATOR, ",");
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX+"ATTR_1"+
				SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_VALUE, utente);
		busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CORNICE_SICUREZZA_USER, utente);
		
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX+"ATTR_2"+
				SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_QUALIFIED_NAME, attributeNameIpUser);
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX+"ATTR_2"+
				SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME, 
				SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_VALUE_UNSPECIFIED);
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX+"ATTR_2"+
				SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_VALUE_SEPARATOR, ",");
		pSaml.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_PREFIX+"ATTR_2"+
				SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_VALUE, indirizzoIpPostazione);
		busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CORNICE_SICUREZZA_USER_IP, indirizzoIpPostazione);
		
		secProperties.put(SecurityConstants.SAML_PROF_REF_ID, pSaml);

	}
}
