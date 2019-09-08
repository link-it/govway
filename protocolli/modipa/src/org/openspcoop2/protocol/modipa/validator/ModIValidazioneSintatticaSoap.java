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

package org.openspcoop2.protocol.modipa.validator;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeaderElement;

import org.apache.commons.codec.binary.Hex;
import org.apache.wss4j.dom.WSDataRef;
import org.apache.wss4j.dom.message.token.Timestamp;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.message.soap.wsaddressing.Costanti;
import org.openspcoop2.message.soap.wsaddressing.WSAddressingHeader;
import org.openspcoop2.message.soap.wsaddressing.WSAddressingUtilities;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModISecurityConfig;
import org.openspcoop2.protocol.modipa.utils.ModITruststoreConfig;
import org.openspcoop2.protocol.modipa.utils.ModIUtilities;
import org.openspcoop2.protocol.sdk.Busta;
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
import org.openspcoop2.security.message.engine.MessageSecurityContext_impl;
import org.openspcoop2.security.message.wss4j.MessageSecurityReceiver_wss4j;
import org.openspcoop2.utils.Utilities;
import org.slf4j.Logger;

/**
 * ModIValidazioneSintatticaSoap
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIValidazioneSintatticaSoap extends AbstractModIValidazioneSintatticaCommons{

	public ModIValidazioneSintatticaSoap(Logger log, IState state, ModIProperties modiProperties, ValidazioneUtils validazioneUtils) {
		super(log, state, modiProperties, validazioneUtils);
	}

	public void validateInteractionProfile(OpenSPCoop2Message msg, boolean request, String asyncInteractionType, String asyncInteractionRole, 
			Busta busta, List<Eccezione> erroriValidazione) throws Exception {
		
		OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
		
		String correlationIdName = this.modiProperties.getSoapCorrelationIdName();
		String correlationId = ModIUtilities.getSOAPHeaderCorrelationIdValue(soapMessage);
		
		String replyToName = this.modiProperties.getSoapReplyToName();
		String replyToAddress = ModIUtilities.getSOAPHeaderReplyToValue(soapMessage);

		
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
	
	public SOAPEnvelope validateSecurityProfile(OpenSPCoop2Message msg, boolean request, String securityMessageProfile, 
			Busta busta, List<Eccezione> erroriValidazione,
			ModITruststoreConfig trustStoreCertificati, ModISecurityConfig securityConfig) throws Exception {
		
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
		
		if(messageSecurityContext.existsSecurityHeader(msg, this.modiProperties.getSoapSecurityTokenActor())==false){
			if(attesoSecurityHeader) {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_PRESENTE, 
						"Header Message Security non riscontrato nella SOAPEnvelope ricevuta"));
			}
			return null;
		}
		
		OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
		SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
		
		boolean filtroDuplicati = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(securityMessageProfile) || 
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(securityMessageProfile);
		boolean integrita =ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(securityMessageProfile) ||
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(securityMessageProfile);

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
		MessageSecurityReceiver_wss4j wss4jSignature = null;
		try {
		
			wss4jSignature = new MessageSecurityReceiver_wss4j();
			Hashtable<String,Object> secProperties = new Hashtable<>();
			secProperties.put(SecurityConstants.SECURITY_ENGINE, SecurityConstants.SECURITY_ENGINE_WSS4J);
			secProperties.put(SecurityConstants.ACTION, SecurityConstants.TIMESTAMP_ACTION+" "+SecurityConstants.SIGNATURE_ACTION);
			if(this.modiProperties.getSoapSecurityTokenActor()!=null && !"".equals(this.modiProperties.getSoapSecurityTokenActor())) {
				secProperties.put(SecurityConstants.ACTOR, this.modiProperties.getSoapSecurityTokenActor());
			}
			secProperties.put(SecurityConstants.MUST_UNDERSTAND, this.modiProperties.isSoapSecurityTokenMustUnderstand()+"");
			secProperties.put(SecurityConstants.IS_BSP_COMPLIANT, SecurityConstants.TRUE);
			
			// parti attese come firmate
			// La funzionalità solleva errori se trova anche altre parti firmate non attese.
			// Implemento questo controllo tramite i WSDataRef
			/*
			StringBuffer bf = new StringBuffer();
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
			
			// setProperties
			messageSecurityContext.setIncomingProperties(secProperties, false);
			
			
			// ** Raccolgo elementi "toccati" da Security per pulirli poi in fase di sbustamento ** /
			elementsToClean = wss4jSignature.getDirtyElements(messageSecurityContext, soapMessage);
			
			
			// ** Verifica firma elementi richiesti dalla configurazione ** /
			List<SubErrorCodeSecurity> listaErroriRiscontrati = new ArrayList<SubErrorCodeSecurity>();
			wss4jSignature.checkEncryptSignatureParts(messageSecurityContext,elementsToClean, soapMessage,listaErroriRiscontrati);
			if(listaErroriRiscontrati.size()>0){
				StringBuffer bf = new StringBuffer();
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
			wss4jSignature.process(messageSecurityContext, soapMessage, busta);
			
			
			// ** Leggo certificato client **/
			x509 = wss4jSignature.getX509Certificate();
		
			
			// ** Leggo security header **/
			securityHeader = messageSecurityContext.getSecurityHeader(msg, this.modiProperties.getSoapSecurityTokenActor());
			timestamp = wss4jSignature.getTimestamp();
			List<WSDataRef> signatureRefs = wss4jSignature.getSignatureRefs();	
			if(signatureRefs!=null && !signatureRefs.isEmpty()) {
				for (WSDataRef wsDataRef : signatureRefs) {
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
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_IAT, Utilities.getSimpleDateFormatMs().format(d));
				}
				else {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_PRESENTE, 
							"Header WSSecurity Timestamp; elemento 'Created'"));
				}
				if(timestamp.getExpires()!=null) {
					long ms = timestamp.getExpires().toEpochMilli();
					Date d = new Date(ms); // la conversione serve a risolvere il timezone
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_EXP, Utilities.getSimpleDateFormatMs().format(d));
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
				digestValue = digestValue + Hex.encodeHexString(bodyRef.getDigestValue());
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_DIGEST, digestValue);
			}
		}

		
		

		/*
		 * == addressing ==
		 */
		WSAddressingHeader wsAddressingHeader = null;
		try {
			WSAddressingUtilities wsaddressingUtilities = new WSAddressingUtilities(this.log);
			wsAddressingHeader = wsaddressingUtilities.read(soapMessage, this.modiProperties.getSoapWSAddressingActor(), true);
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
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(request ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_PRESENTE :
					CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_PRESENTE, 
						"Header WSAddressing '"+Costanti.WSA_SOAP_HEADER_TO+"' non presente"));
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
		 * == sbustamento/traccia ==
		 */
		
		ModISOAPSecurity soapSecurity = new ModISOAPSecurity();
		
		// elementi per costruire la traccia
		soapSecurity.setSecurityHeader(securityHeader);
		soapSecurity.setWsAddressingHeader(wsAddressingHeader);
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
}