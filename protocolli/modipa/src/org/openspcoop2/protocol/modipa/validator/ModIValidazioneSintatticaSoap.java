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

package org.openspcoop2.protocol.modipa.validator;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPPart;

import org.apache.wss4j.dom.WSDataRef;
import org.apache.wss4j.dom.message.token.Timestamp;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.message.soap.wsaddressing.Costanti;
import org.openspcoop2.message.soap.wsaddressing.WSAddressingHeader;
import org.openspcoop2.message.soap.wsaddressing.WSAddressingUtilities;
import org.openspcoop2.message.xml.DynamicNamespaceContextFactory;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModISecurityConfig;
import org.openspcoop2.protocol.modipa.utils.ModITruststoreConfig;
import org.openspcoop2.protocol.modipa.utils.ModIUtilities;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ValidazioneUtils;
import org.openspcoop2.security.keystore.KeystoreConstants;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContextParameters;
import org.openspcoop2.security.message.SubErrorCodeSecurity;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.constants.SignatureDigestAlgorithm;
import org.openspcoop2.security.message.constants.WSSAttachmentsConstants;
import org.openspcoop2.security.message.engine.MessageSecurityContext_impl;
import org.openspcoop2.security.message.saml.SAMLBuilderConfigConstants;
import org.openspcoop2.security.message.wss4j.MessageSecurityReceiver_wss4j;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.slf4j.Logger;
import org.w3c.dom.NodeList;

/**
 * ModIValidazioneSintatticaSoap
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIValidazioneSintatticaSoap extends AbstractModIValidazioneSintatticaCommons{

	public ModIValidazioneSintatticaSoap(Logger log, IState state, Context context, ModIProperties modiProperties, ValidazioneUtils validazioneUtils) {
		super(log, state, context, modiProperties, validazioneUtils);
	}

	public void validateAsyncInteractionProfile(OpenSPCoop2Message msg, boolean request, String asyncInteractionType, String asyncInteractionRole, 
			Busta busta, List<Eccezione> erroriValidazione,
			String replyTo) throws Exception {
		
		boolean bufferMessage_readOnly = this.modiProperties.isValidazioneBufferEnabled();
		String idTransazione = null;
		if(this.context!=null) {
			idTransazione = (String)this.context.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		}
		
		OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
		SOAPPart soapPart = MessageUtils.getSOAPPart(soapMessage, bufferMessage_readOnly, idTransazione);
		SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
		SOAPHeader header = null;
		SOAPBody body = null;
		if(soapEnvelope!=null) {
			header = soapEnvelope.getHeader();
			body = soapEnvelope.getBody();
		}
		
		String correlationIdName = this.modiProperties.getSoapCorrelationIdName();
		String correlationId = ModIUtilities.getSOAPHeaderCorrelationIdValue(soapMessage.getMessageType(), header, body);
		
		String replyToName = this.modiProperties.getSoapReplyToName();
		String replyToAddress = ModIUtilities.getSOAPHeaderReplyToValue(soapMessage.getMessageType(), header, body);

		
		if(replyToAddress!=null) {
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_REPLY_TO, replyToAddress);
		}
		if(correlationId!=null) {
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE, correlationId);
			if(correlationId.length()<=255) {
				busta.setCollaborazione(correlationId);
			}
		}
		
		if(asyncInteractionType!=null) {
			if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionType)) {
				if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
					if(request) {
						if(replyToAddress==null || "".equals(replyToAddress)) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SERVIZIO_CORRELATO_NON_PRESENTE, 
									"Header SOAP '"+replyToName+"' non presente"));
							return;
						}
						if(this.modiProperties.isSoapSecurityTokenPushReplyToUpdateInErogazione()) {
							ModIUtilities.addSOAPHeaderReplyTo(soapMessage, replyTo); // aggiorna il valore se già esistente
						}
					}
					else {
						if(correlationId==null || "".equals(correlationId)) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.COLLABORAZIONE_NON_PRESENTE, 
									"Header SOAP '"+correlationIdName+"' non presente"));
							return;
						}
					}
				}
				else {
					if(request) {
						if(correlationId==null || "".equals(correlationId)) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.COLLABORAZIONE_NON_PRESENTE, 
									"Header SOAP '"+correlationIdName+"' non presente"));
							return;
						}
					}
				}
			}
			else {
				// pull
				if(request) {
					
					// Flusso di richiesta
					
					if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO.equals(asyncInteractionRole) ||
							ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA.equals(asyncInteractionRole)) {
					
						if(correlationId==null || "".equals(correlationId)) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.COLLABORAZIONE_NON_PRESENTE, 
									"Header SOAP '"+correlationIdName+"' non presente"));
							return;
						}
						
					}
					
					
				}
				else {
					
					// Flusso di risposta
					
					if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
						
						if(correlationId==null || "".equals(correlationId)) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.COLLABORAZIONE_NON_PRESENTE, 
									"Header SOAP '"+correlationIdName+"' non presente"));
							return;
						}
						
					}
					
				}
			}
		}
		
	}
	
	public SOAPEnvelope validateSecurityProfile(OpenSPCoop2Message msg, boolean request, String securityMessageProfile, boolean corniceSicurezza, boolean includiRequestDigest, boolean signAttachments, 
			Busta busta, List<Eccezione> erroriValidazione,
			ModITruststoreConfig trustStoreCertificati, ModISecurityConfig securityConfig,
			boolean buildSecurityTokenInRequest,
			Map<String, Object> dynamicMapParameter, Busta datiRichiesta) throws Exception {
		
		MessageSecurityContextParameters messageSecurityContextParameters = new MessageSecurityContextParameters();
		messageSecurityContextParameters.setFunctionAsClient(false);
		messageSecurityContextParameters.setPrefixWsuId(OpenSPCoop2Properties.getInstance().getPrefixWsuId()); // NOTA: deve essere lo stesso di govway usato in altri profili
		MessageSecurityContext messageSecurityContext = new MessageSecurityContext_impl(messageSecurityContextParameters);
		
		boolean attesoSecurityHeader = true;
		if(!request) {
			if(msg.isFault()) {
				attesoSecurityHeader = false;
			}
		}
		
		boolean bufferMessage_readOnly = this.modiProperties.isValidazioneBufferEnabled();
		String idTransazione = null;
		if(this.context!=null) {
			idTransazione = (String)this.context.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		}
		
				
		if(messageSecurityContext.existsSecurityHeader(msg, this.modiProperties.getSoapSecurityTokenActor(),
				bufferMessage_readOnly, idTransazione)==false){
			if(attesoSecurityHeader) {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_PRESENTE, 
						"Header Message Security non riscontrato nella SOAPEnvelope ricevuta"));
			}
			return null;
		}
		
		OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
		SOAPPart soapPart = MessageUtils.getSOAPPart(soapMessage, bufferMessage_readOnly, idTransazione);
		SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
		SOAPHeader header = null;
		SOAPBody body = null;
		if(soapEnvelope!=null) {
			header = soapEnvelope.getHeader();
			body = soapEnvelope.getBody();
		}
		
		boolean filtroDuplicati = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(securityMessageProfile) || 
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(securityMessageProfile);
		boolean integrita =ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(securityMessageProfile) ||
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(securityMessageProfile);

		/*
		 * == requestDigest ==
		 */
		
		SOAPHeaderElement requestDigestHeader = null;
		if(integrita && !request && includiRequestDigest) {
			requestDigestHeader = ModIUtilities.getSOAPHeaderRequestDigest(soapMessage.getMessageType(), header, body);
		}
		
		
		/*
		 * == signature ==
		 */
		
		X509Certificate x509 = null;
		List<Reference> elementsToClean = null;
		SOAPHeaderElement securityHeader = null;
		Timestamp timestamp = null;
		WSDataRef timestamptRef = null;
		WSDataRef wsaToRef = null;
		WSDataRef wsaMessageIdRef = null;
		WSDataRef bodyRef = null;
		WSDataRef requestDigestRef = null;
		List<WSDataRef> attachmentsRef = new ArrayList<WSDataRef>();
		MessageSecurityReceiver_wss4j wss4jSignature = null;
		try {
		
			wss4jSignature = new MessageSecurityReceiver_wss4j();
			Map<String,Object> secProperties = new HashMap<>();
			secProperties.put(SecurityConstants.SECURITY_ENGINE, SecurityConstants.SECURITY_ENGINE_WSS4J);
			if(this.modiProperties.getSoapSecurityTokenActor()!=null && !"".equals(this.modiProperties.getSoapSecurityTokenActor())) {
				secProperties.put(SecurityConstants.ACTOR, this.modiProperties.getSoapSecurityTokenActor());
			}
			secProperties.put(SecurityConstants.MUST_UNDERSTAND, this.modiProperties.isSoapSecurityTokenMustUnderstand()+"");
			
			// cornice sicurezza
			if(!request) {
				corniceSicurezza = false; // permessa solo per i messaggi di richiesta
			}
			if(corniceSicurezza) {
				addValidationCorniceSicurezza(secProperties);
			}
			
			// action
			StringBuilder bfAction = new StringBuilder();
			bfAction.append(SecurityConstants.TIMESTAMP_ACTION).append(" ").append(SecurityConstants.SIGNATURE_ACTION);
			if(corniceSicurezza) {
				bfAction.append(" ").append(SecurityConstants.ACTION_SAML_TOKEN_UNSIGNED);
			}
			secProperties.put(SecurityConstants.ACTION, bfAction.toString());
						
			// parti attese come firmate
			// La funzionalità solleva errori se trova anche altre parti firmate non attese.
			// Implemento questo controllo tramite i WSDataRef
			/*
			StringBuilder bf = new StringBuilder();
			bf.append("{Element}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp");
			bf.append(";");
			bf.append("{Element}{").append(Costanti.WSA_NAMESPACE).append("}").append(Costanti.WSA_SOAP_HEADER_TO);
			if(filtroDuplicati) {
				bf.append(";");
				bf.append("{Element}{").append(Costanti.WSA_NAMESPACE).append("}").append(Costanti.WSA_SOAP_HEADER_ID);
			}
			if(integrita) {
				bf.append(";");
				bf.append("{Element}{").append(soapEnvelope.getNamespaceURI()).append("}Body");
				if(signAttachments) {
					if(bf.length()>0) {
						bf.append(";");
					}
					bf.append("{}"+SecurityConstants.CID_ATTACH_WSS4j);
				}
			}
			if(addCorniceSicurezza) {
				bf.append(";");
				bf.append("{Element}{").append(Costanti.SAML_20_NAMESPACE).append("}Assertion");
			}
			secProperties.put(SecurityConstants.SIGNATURE_PARTS, bf.toString());
			secProperties.put(SecurityConstants.SIGNATURE_PARTS_VERIFY, SecurityConstants.TRUE);
			*/
			
			// truststore
			Properties pTruststore = new Properties();
			//pTruststore.put(KeystoreConstants.PROPERTY_PROVIDER, KeystoreConstants.PROVIDER_DEFAULT);
			pTruststore.put(KeystoreConstants.PROPERTY_PROVIDER, KeystoreConstants.PROVIDER_GOVWAY);
			pTruststore.put(KeystoreConstants.PROPERTY_TRUSTSTORE_TYPE, trustStoreCertificati.getSecurityMessageTruststoreType());
			pTruststore.put(KeystoreConstants.PROPERTY_TRUSTSTORE_PASSWORD, trustStoreCertificati.getSecurityMessageTruststorePassword());
			pTruststore.put(KeystoreConstants.PROPERTY_TRUSTSTORE_PATH, trustStoreCertificati.getSecurityMessageTruststorePath());
			if(trustStoreCertificati.getSecurityMessageTruststoreCRLs()!=null) {
				pTruststore.put(KeystoreConstants.PROPERTY_CRL, trustStoreCertificati.getSecurityMessageTruststoreCRLs());
				secProperties.put(SecurityConstants.ENABLE_REVOCATION, SecurityConstants.TRUE);
			}
			secProperties.put(SecurityConstants.SIGNATURE_VERIFICATION_PROPERTY_REF_ID, pTruststore);
			if(corniceSicurezza) {
				secProperties.put(SecurityConstants.SIGNATURE_PROPERTY_REF_ID, pTruststore);
			}
			secProperties.put(SecurityConstants.IS_BSP_COMPLIANT, SecurityConstants.TRUE);
			
			
			//  ** Timestamp **
			Long timeToLive = this.modiProperties.getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds(); // viene usato per vedere la data di creazione quanto si discosta da adesso
			if(securityConfig.getCheckTtlIatMilliseconds()!=null) {
				timeToLive = securityConfig.getCheckTtlIatMilliseconds();
			}
			boolean set_TimeToLive = false;
			if(timeToLive!=null) {
				if(msg!=null) {
					msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_IAT_TTL_CHECK, timeToLive);
				}
				// Non imposto il valore qua, ma inseriro un valoro alto (3650 giorni) in modo che la libreria non effettui il controllo, che invece avviene nella validazione semantica
				// In modo da uniformare l'errore rispetto anche a REST
				/*
				int value = timeToLive.intValue();
				if(value>=1000) {
					value = value / 1000; // riporto in secondi
					secProperties.put(SecurityConstants.TIMESTAMP_TTL, value+"");
					set_TimeToLive = true;
				}*/
			}
			if(!set_TimeToLive) {
				// devo impostare un valore alto, altrimenti il default di wss4j e' 60 secondi
				// 3650 giorni (60s * 60m * 24h * 3650 giorni = 315360000)
				secProperties.put(SecurityConstants.TIMESTAMP_TTL, 315360000+"");
			}
			
			Integer futureTimeToLive = this.modiProperties.getSoapSecurityTokenTimestampCreatedFutureTimeCheck_milliseconds();
			if(futureTimeToLive!=null) {
				int value = futureTimeToLive.intValue();
				if(value>=1000) {
					value = value / 1000; // riporto in secondi
					secProperties.put(SecurityConstants.TIMESTAMP_FUTURE_TTL, value+"");
				}
			}
			
			// disabilito qua la validazione, in modo da implementarla sulla validazione semantica
			// In modo da uniformare l'errore rispetto anche a REST
			secProperties.put(SecurityConstants.TIMESTAMP_STRICT, false+"");
			
			
			// setProperties
			messageSecurityContext.setIncomingProperties(secProperties, false);
			if(signAttachments) {
				messageSecurityContext.setManualAttachmentsSignaturePart("{}"+SecurityConstants.CID_ATTACH_WSS4j);
			}
			
			
			// ** Raccolgo elementi "toccati" da Security per pulirli poi in fase di sbustamento ** /
			elementsToClean = wss4jSignature.getDirtyElements(messageSecurityContext, soapMessage);
			
			
			// ** Verifica firma elementi richiesti dalla configurazione ** /
			List<SubErrorCodeSecurity> listaErroriRiscontrati = new ArrayList<SubErrorCodeSecurity>();
			wss4jSignature.checkEncryptSignatureParts(messageSecurityContext,elementsToClean, soapMessage,listaErroriRiscontrati);
			if(listaErroriRiscontrati.size()>0){
				StringBuilder bf = new StringBuilder();
				for (Iterator<?> iterator = listaErroriRiscontrati.iterator(); iterator.hasNext();) {
					SubErrorCodeSecurity subCodiceErroreSicurezza = (SubErrorCodeSecurity) iterator.next();
					if(bf.length()>0){
						bf.append(" ; ");
					}
					bf.append(subCodiceErroreSicurezza.getMsgErrore());
				}
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_PRESENTE, 
						bf.toString()));
				return null;
			}
			
			
			// ** Applico sicurezza tramite engine **/
			wss4jSignature.process(messageSecurityContext, soapMessage, busta, 
					bufferMessage_readOnly, idTransazione);
			
			
			// ** Leggo certificato client **/
			x509 = wss4jSignature.getX509Certificate();
		
			
			// ** Leggo security header **/
			securityHeader = messageSecurityContext.getSecurityHeader(msg, this.modiProperties.getSoapSecurityTokenActor(), 
					bufferMessage_readOnly, idTransazione);
			timestamp = wss4jSignature.getTimestamp();
			List<WSDataRef> signatureRefs = wss4jSignature.getSignatureRefs();	
			if(signatureRefs!=null && !signatureRefs.isEmpty()) {
				for (WSDataRef wsDataRef : signatureRefs) {
					
					if(wsDataRef.getName()!=null && wsDataRef.getName().getLocalPart()!=null && wsDataRef.getName().getNamespaceURI()!=null) {
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SIGNED_SOAP_PREFIX+wsDataRef.getName().getLocalPart(), wsDataRef.getName().getNamespaceURI());
					}
					
					if(wsDataRef.getName()!=null && 
							"Timestamp".equals(wsDataRef.getName().getLocalPart()) &&
							"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd".equals(wsDataRef.getName().getNamespaceURI())) {
						timestamptRef = wsDataRef;
					}
					else if(wsDataRef.getName()!=null && 
							Costanti.WSA_SOAP_HEADER_TO.equals(wsDataRef.getName().getLocalPart()) &&
							Costanti.WSA_NAMESPACE.equals(wsDataRef.getName().getNamespaceURI())) {
						wsaToRef = wsDataRef;
					}
					else if(wsDataRef.getName()!=null && 
							Costanti.WSA_SOAP_HEADER_ID.equals(wsDataRef.getName().getLocalPart()) &&
							Costanti.WSA_NAMESPACE.equals(wsDataRef.getName().getNamespaceURI())) {
						wsaMessageIdRef = wsDataRef;
					}
					else if(wsDataRef.getName()!=null && 
							"Body".equals(wsDataRef.getName().getLocalPart()) &&
							soapEnvelope.getNamespaceURI().equals(wsDataRef.getName().getNamespaceURI())) {
						bodyRef = wsDataRef;
					}
					else if(requestDigestHeader!=null &&
							wsDataRef.getName()!=null && 
							requestDigestHeader.getLocalName().equals(wsDataRef.getName().getLocalPart()) &&
							requestDigestHeader.getNamespaceURI().equals(wsDataRef.getName().getNamespaceURI())) {
						requestDigestRef = wsDataRef;
					}
					else if(wsDataRef.getName()!=null && 
							"attachment".equals(wsDataRef.getName().getLocalPart()) &&
							WSSAttachmentsConstants.SWA_NS.equals(wsDataRef.getName().getNamespaceURI())) {
						attachmentsRef.add(wsDataRef);
					}
//					else {
//						System.out.println("TROVATO ALTRO WS DATA Name["+wsDataRef.getName()+"] wsuId["+wsDataRef.getWsuId()+"]");
//					}
				}
			}
			
		}catch(Exception e) {
			this.log.error("Errore durante la validazione del token di sicurezza: "+e.getMessage(),e);
			erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA, 
					e.getMessage(),e));
			return null;
		}
		
				
		if(x509==null || x509.getSubjectX500Principal()==null) {
			erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
					request ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_PRESENTE :
						CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_PRESENTE, 
					"Certificato X509 mittente non presente"));
		}
		else {
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_X509_SUBJECT, x509.getSubjectX500Principal().toString());
			if(x509.getIssuerX500Principal()!=null) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_X509_ISSUER, x509.getIssuerX500Principal().toString());
			}
			
			if(request) {
				identificazioneApplicativoMittente(x509,busta);
			}
		}
		
		
		// NOTA: Inizializzare da qua il dynamicMap altrimenti non ci finisce l'identificazione del mittente effettuata dal metodo sopra 'identificazioneApplicativoMittente'
		Map<String, Object> dynamicMap = null;
		Map<String, Object> dynamicMapRequest = null;
		if(!request) {
			dynamicMapRequest = ModIUtilities.removeDynamicMapRequest(this.context);
		}
		if(dynamicMapRequest!=null) {
			dynamicMap = DynamicUtils.buildDynamicMapResponse(msg, this.context, null, this.log, bufferMessage_readOnly, dynamicMapRequest);
		}
		else {
			dynamicMap = DynamicUtils.buildDynamicMap(msg, this.context, datiRichiesta, this.log, bufferMessage_readOnly);
			ModIUtilities.saveDynamicMapRequest(this.context, dynamicMap);
		}
		if(dynamicMapParameter!=null && dynamicMap!=null) {
			dynamicMapParameter.putAll(dynamicMap);
		}
		
	
		if(timestamp==null) {
			erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_PRESENTE, 
					"Header WSSecurity Timestamp"));
		}
		else {
			
			if(timestamptRef==null || timestamptRef.getDigestValue()==null || timestamptRef.getDigestAlgorithm()==null) {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALIDA, 
						"Header WSSecurity Timestamp non firmato"));
			}
			else {
				
				if(timestamp.getCreated()!=null) {
					long ms = timestamp.getCreated().toEpochMilli();
					Date d = new Date(ms); // la conversione serve a risolvere il timezone
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_IAT, DateUtils.getSimpleDateFormatMs().format(d));
				}
				else {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_PRESENTE, 
							"Header WSSecurity Timestamp; elemento 'Created'"));
				}
				if(timestamp.getExpires()!=null) {
					long ms = timestamp.getExpires().toEpochMilli();
					Date d = new Date(ms); // la conversione serve a risolvere il timezone
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_EXP, DateUtils.getSimpleDateFormatMs().format(d));
				}
				else {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SCADENZA_NON_PRESENTE, 
							"Header WSSecurity Timestamp; elemento 'Expires'"));
				}
				
			}
			
		}
		
		
		if(integrita) {
			if(bodyRef==null || bodyRef.getDigestValue()==null || bodyRef.getDigestAlgorithm()==null) {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO, 
						"Header WSSecurity Signature; SOAP Body non firmato"));
			}
			else {
				SignatureDigestAlgorithm s = SignatureDigestAlgorithm.toEnumConstant(bodyRef.getDigestAlgorithm());
				String digestValue = s!=null ? (s.name()+"=") : "";
				//System.out.println("In Hex: "+org.apache.commons.codec.binary.Hex.encodeHexString(bodyRef.getDigestValue()));
				digestValue = digestValue + Base64Utilities.encodeAsString(bodyRef.getDigestValue());
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_DIGEST, digestValue);
				
				if(request && includiRequestDigest && this.context!=null && securityHeader!=null) {
					
					String digestNamespace = "http://www.w3.org/2000/09/xmldsig#";
					String digestReferencePattern = "//{"+digestNamespace+"}:Reference";
					OpenSPCoop2MessageFactory messageFactory = msg!=null ? msg.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
					DynamicNamespaceContext dnc = DynamicNamespaceContextFactory.getInstance(messageFactory).getNamespaceContext(securityHeader);
					XPathExpressionEngine xpathEngine = new XPathExpressionEngine(messageFactory);
					
					Object res = null;
					try {
						res = xpathEngine.getMatchPattern(securityHeader, dnc, digestReferencePattern, XPathReturnType.NODESET);
					}catch(XPathNotFoundException notFound) {}
					if(res!=null) {
						if(res instanceof NodeList) {
							NodeList nodeList = (NodeList) res;
							this.context.addObject(ModICostanti.MODIPA_CONTEXT_REQUEST_DIGEST, nodeList);
						}
						else {
							this.log.error("Tipo non gestito ritornato dal xpath engine durante la raccolta delle signature references ["+res.getClass().getName()+"]");
						}
					}
				}
			}
			
			if(!request && includiRequestDigest) {
				if(requestDigestHeader==null) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO, 
							"Header Request Digest non presente"));
				}
				if(requestDigestRef==null || requestDigestRef.getDigestValue()==null || requestDigestRef.getDigestAlgorithm()==null) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO, 
							"Header WSSecurity Signature; Header Request Digest non firmato"));
				}
			}
			
			if(signAttachments) {
				
				List<String> cidAttachments = new ArrayList<String>();
				if(msg.castAsSoap().hasAttachments()){
					Iterator<?> itAttach = msg.castAsSoap().getAttachments();
					while (itAttach.hasNext()) {
						AttachmentPart ap = (AttachmentPart) itAttach.next();
						String contentId = normalizeContentID(ap.getContentId());
						cidAttachments.add(contentId);
						//System.out.println("ADD '"+contentId+"'");
					}
				}
				
				if(attachmentsRef.isEmpty()) {
					if(requestDigestRef==null || requestDigestRef.getDigestValue()==null || requestDigestRef.getDigestAlgorithm()==null) {
						erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_ALLEGATO_NON_PRESENTE, 
								"Header WSSecurity Signature; Allegati non firmati"));
					}
				}
				else {
					for (WSDataRef wsDateRef : attachmentsRef) {
						
						String cid = null;
						String idLog = "";
						if(wsDateRef!=null) {
							cid = normalizeContentID(wsDateRef.getWsuId());
							idLog = (wsDateRef.getWsuId()!=null) ? " '"+cid+"'" : "";
						}
						 												
						if(wsDateRef==null || wsDateRef.getDigestValue()==null || wsDateRef.getDigestAlgorithm()==null) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_ALLEGATO_NON_VALIDA, 
									"Header WSSecurity Signature; Allegato"+idLog+" non firmato (digest non presente)"));
						}
						if(cid==null) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_ALLEGATO_NON_VALIDA, 
									"Header WSSecurity Signature; Allegato"+idLog+" non firmato (cid non presente)"));
						}
						//System.out.println("CID FIRMATO '"+cid+"'");
						
						if(!cidAttachments.contains(cid)) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_ALLEGATO_NON_VALIDA, 
									"Header WSSecurity Signature; Allegato con id"+idLog+", riferito nella firma, non esiste"));
						}
						else {
							cidAttachments.remove(cid);
						}
					}
				}
				
				if(!cidAttachments.isEmpty()) {
					for (String cid : cidAttachments) {
						erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_ALLEGATO_NON_VALIDA, 
								"Header WSSecurity Signature; Allegato"+cid+" non firmato"));
					}
				}
				
			}
		}

		
		

		/*
		 * == addressing ==
		 */
		WSAddressingHeader wsAddressingHeader = null;
		try {
			WSAddressingUtilities wsaddressingUtilities = new WSAddressingUtilities(this.log);
			wsAddressingHeader = wsaddressingUtilities.read(soapMessage, soapEnvelope.getHeader(), this.modiProperties.getSoapWSAddressingActor(), this.modiProperties.isSoapWSAddressingSchemaValidation());
			if(wsAddressingHeader==null) {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_PRESENTE, 
						"Header WSAddressing non presenti"));
			}
		}catch(Exception e) {
			this.log.error("Errore durante la letttura degli header WSAddressing: "+e.getMessage(),e);
			erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_PRESENTE, 
					"Header WSAddressing; process failed: "+e.getMessage(),e));
		}
		
		if(wsAddressingHeader!=null) {
		
			if(wsAddressingHeader.getTo()==null || wsAddressingHeader.getToValue()==null) {
				if(request || buildSecurityTokenInRequest) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(request ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_PRESENTE :
						CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_PRESENTE, 
							"Header WSAddressing '"+Costanti.WSA_SOAP_HEADER_TO+"' non presente"));
				}
			}
			else {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SOAP_WSA_TO, wsAddressingHeader.getToValue());
				
				if(wsaToRef==null || wsaToRef.getDigestValue()==null || wsaToRef.getDigestAlgorithm()==null) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(request ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_VALIDO :
						CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_VALIDO, 
						"Header WSAddressing '"+Costanti.WSA_SOAP_HEADER_TO+"' non firmato"));
				}
			}
			
			if(wsAddressingHeader.getFrom()!=null && wsAddressingHeader.getFromValue()!=null) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SOAP_WSA_FROM, wsAddressingHeader.getFromValue());
			}
			
			if(wsAddressingHeader.getId()==null || wsAddressingHeader.getIdValue()==null) {
				if(filtroDuplicati) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE,
						"Header WSAddressing '"+Costanti.WSA_SOAP_HEADER_ID+"' non presente"));
				}
			}
			else {
				String id = wsAddressingHeader.getIdValue();
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_ID, id);
				if(id.length()<=255) {
					busta.setID(id);
				}
				
				if(filtroDuplicati) {
					if(wsaMessageIdRef==null || wsaMessageIdRef.getDigestValue()==null || wsaMessageIdRef.getDigestAlgorithm()==null) {
						erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO, 
								"Header WSAddressing '"+Costanti.WSA_SOAP_HEADER_ID+"' non firmato"));
					}
				}
			}
			
			if(wsAddressingHeader.getRelatesTo()!=null && wsAddressingHeader.getRelatesToValue()!=null) {
				String relatesTo =  wsAddressingHeader.getRelatesToValue();
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_RELATES_TO, relatesTo);
				if(relatesTo.length()<=255) {
					busta.setRiferimentoMessaggio(relatesTo);
				}
			}

		}
		if(wsAddressingHeader==null) {
			return null;
		}
	    
		
		/*
		 * == cornice sicurezza ==
		 */
		if(corniceSicurezza) {
			
			DynamicNamespaceContext dnc = DynamicNamespaceContextFactory.getInstance(msg.getFactory()).getNamespaceContext(securityHeader);
			XPathExpressionEngine engine = new XPathExpressionEngine(msg.getFactory());
			
			String xpathSaml2_CodiceEnte = new StringBuilder().append("//{").append(org.openspcoop2.message.constants.Costanti.SAML_20_NAMESPACE).
					append("}NameID/text()").toString();
			try {
				String codiceEnte = engine.getStringMatchPattern(securityHeader, dnc, xpathSaml2_CodiceEnte);
				if(codiceEnte==null || "".equals(codiceEnte)) {
					throw new XPathNotFoundException("non trovato");
				}
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CORNICE_SICUREZZA_ENTE, codiceEnte);
			}
			catch(XPathNotFoundException notFound) {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE, 
						ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL+"; elemento 'Subject/NameID'"));
			}catch(Exception e) {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_VALIDO, 
						ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL+"; elemento 'Subject/NameID'"));
			}
			
			String attributeNameUser = this.modiProperties.getSicurezzaMessaggio_corniceSicurezza_soap_user();
			String xpathSaml2_User = new StringBuilder().append("//{").append(org.openspcoop2.message.constants.Costanti.SAML_20_NAMESPACE).
					append("}Attribute[@Name='").append(attributeNameUser).append("']//{").
					append(org.openspcoop2.message.constants.Costanti.SAML_20_NAMESPACE).append("}AttributeValue/text()").toString();
			try {
				String user = engine.getStringMatchPattern(securityHeader, dnc, xpathSaml2_User);
				if(user==null || "".equals(user)) {
					throw new XPathNotFoundException("non trovato");
				}
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CORNICE_SICUREZZA_USER, user);
			}
			catch(XPathNotFoundException notFound) {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE, 
						ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL+"; elemento 'Attribute/"+attributeNameUser+"'"));
			}catch(Exception e) {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_VALIDO, 
						ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL+"; elemento 'Attribute/"+attributeNameUser+"'"));
			}
			
			String attributeNameIpUser = this.modiProperties.getSicurezzaMessaggio_corniceSicurezza_soap_ipuser();
			String xpathSaml2_IpUser = new StringBuilder().append("//{").append(org.openspcoop2.message.constants.Costanti.SAML_20_NAMESPACE).
					append("}Attribute[@Name='").append(attributeNameIpUser).append("']//{").
					append(org.openspcoop2.message.constants.Costanti.SAML_20_NAMESPACE).append("}AttributeValue/text()").toString();
			try {
				String ipUser = engine.getStringMatchPattern(securityHeader, dnc, xpathSaml2_IpUser);
				if(ipUser==null || "".equals(ipUser)) {
					throw new XPathNotFoundException("non trovato");
				}
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CORNICE_SICUREZZA_USER_IP, ipUser);
			}
			catch(XPathNotFoundException notFound) {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE, 
						ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL+"; elemento 'Attribute/"+attributeNameIpUser+"'"));
			}catch(Exception e) {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_VALIDO, 
						ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL+"; elemento 'Attribute/"+attributeNameIpUser+"'"));
			}
			
		}
		
		
		
		/*
		 * == sbustamento/traccia ==
		 */
		
		ModISOAPSecurity soapSecurity = new ModISOAPSecurity();
		
		// elementi per costruire la traccia
		soapSecurity.setSecurityHeader(securityHeader);
		soapSecurity.setWsAddressingHeader(wsAddressingHeader);
		soapSecurity.setRequestDigestHeader(requestDigestHeader);
		if(bodyRef!=null) {
			soapSecurity.setWsuIdBodyRef(bodyRef.getWsuId());
		}
		
		// elementi per lo sbustamento
		soapSecurity.setElementsToClean(elementsToClean);
		soapSecurity.setMessageSecurityContext(messageSecurityContext);
		soapSecurity.setWss4jSignature(wss4jSignature);
		msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_SBUSTAMENTO_SOAP, soapSecurity);
		
		return soapSecurity.buildTraccia(msg.getMessageType());
		
	}
	
	private String normalizeContentID(String contentId) {
		if(contentId==null) {
			return null;
		}
		contentId = contentId.replace("&lt;", "<");
		contentId = contentId.replace("&gt;", ">");
		if(contentId.startsWith("cid:") && contentId.length()>4){
			contentId = contentId.substring(4);
		}
		if(contentId.startsWith("<") && contentId.length()>1)
			contentId = contentId.substring(1);
		if(contentId.endsWith(">") && contentId.length()>1)
			contentId = contentId.substring(0,contentId.length()-1);
		return contentId;
	}
	
	private void addValidationCorniceSicurezza(Map<String,Object> secProperties) {

		secProperties.put(SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_VERSION, SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_VERSION_20);
		secProperties.put(SecurityConstants.SAML_ENVELOPED_SAML_SIGNATURE_XMLCONFIG_PREFIX_ID, SecurityConstants.TRUE);
		
		boolean senderVouche = true;
		if(senderVouche) {
			secProperties.put("validateSamlSubjectConfirmation", SecurityConstants.TRUE);
			secProperties.put(SecurityConstants.SAML_SUBJECT_CONFIRMATION_VALIDATION_METHOD_XMLCONFIG_ID, SecurityConstants.SAML_SUBJECT_CONFIRMATION_VALIDATION_METHOD_XMLCONFIG_ID_SENDER_VOUCHES);
		}
		else {
			secProperties.put("validateSamlSubjectConfirmation", SecurityConstants.FALSE);
		}

	}
}
