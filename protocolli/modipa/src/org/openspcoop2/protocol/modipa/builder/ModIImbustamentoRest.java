/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.io.ByteArrayOutputStream;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModIKeystoreConfig;
import org.openspcoop2.protocol.modipa.utils.ModISecurityConfig;
import org.openspcoop2.protocol.modipa.utils.ModIUtilities;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContextParameters;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.engine.MessageSecurityContext_impl;
import org.openspcoop2.security.message.jose.MessageSecuritySender_jose;
import org.openspcoop2.security.message.utils.SignatureBean;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * ModIImbustamentoRest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIImbustamentoRest {

	@SuppressWarnings("unused")
	private Logger log;
	private ModIProperties modiProperties;
	public ModIImbustamentoRest(Logger log) throws ProtocolException {
		this.log = log;
		this.modiProperties = ModIProperties.getInstance();
	}
	
	public void addSyncInteractionProfile(OpenSPCoop2Message msg, RuoloMessaggio ruoloMessaggio) throws Exception {
	
		if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)) {
			
			// nop
			
		}
		else {
			
			// Flusso di Risposta
			
			String returnCode = null;
			int returnCodeInt = -1;
			if(msg.getTransportResponseContext()!=null) {
				returnCode = msg.getTransportResponseContext().getCodiceTrasporto();
				if(returnCode!=null) {
					try {
						returnCodeInt = Integer.valueOf(returnCode);
					}catch(Exception e) {}
				}
			}
			
			Integer [] returnCodeAttesi = this.modiProperties.getRestBloccanteHttpStatus();
			
			// Fix: il controllo deve essere fatto solo per i codici che non rientrano in 4xx e 5xx
			boolean is_4xx_5xx = (returnCodeInt>=400) && (returnCodeInt<=599);
			
			if(!is_4xx_5xx && returnCodeAttesi!=null) {				
				boolean found = false;
				for (Integer integer : returnCodeAttesi) {
					if(integer.intValue() == ModICostanti.MODIPA_PROFILO_INTERAZIONE_HTTP_CODE_2XX_INT_VALUE) {
						if((returnCodeInt >= 200) && (returnCodeInt<=299) ) {
							found = true;
							break;
						}
					}
					else if(integer.intValue() == returnCodeInt) {
						found = true;
						break;
					}
				}
				if(!found) {
					StringBuilder sb = new StringBuilder();
					for (Integer integer : returnCodeAttesi) {
						
						if(integer.intValue() == ModICostanti.MODIPA_PROFILO_INTERAZIONE_HTTP_CODE_2XX_INT_VALUE) {
							sb = new StringBuilder();
							sb.append("2xx");
							break;
						}
						
						if(sb.length()>0) {
							sb.append(",");
						}
						sb.append(integer.intValue());
					}
					ProtocolException pe = new ProtocolException("HTTP Status '"+returnCodeInt+"' differente da quello atteso per il profilo bloccante (atteso: "+sb.toString()+")");
					pe.setInteroperabilityError(true);
					throw pe;
				}
			}
			
		}
		
	}
	
	public void addAsyncInteractionProfile(OpenSPCoop2Message msg, Busta busta, RuoloMessaggio ruoloMessaggio,
			String asyncInteractionType, String asyncInteractionRole,
			String replyTo,
			AccordoServizioParteComune apiContenenteRisorsa, String azione) throws Exception {
		
		if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)) {
			
			// Flusso di Richiesta
			
			if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionType)) {
			
				if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
				
					String headerReplyName = this.modiProperties.getRestReplyToHeader();
					
					if(this.modiProperties.isRestSecurityTokenPushReplyToUpdateOrCreateInFruizione()) {
						msg.getTransportRequestContext().removeHeader(headerReplyName); // rimuovo se già esiste
						msg.forceTransportHeader(headerReplyName, replyTo);
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_REPLY_TO, replyTo);
					}
					else {
						String replyToFound = msg.getTransportRequestContext().getHeaderFirstValue(headerReplyName);
						if(replyToFound!=null && !"".equals(replyToFound)) {
							busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_REPLY_TO, replyToFound);
						}
						else {
							ProtocolException pe = new ProtocolException("Header http '"+headerReplyName+"', richiesto dal profilo non bloccante PUSH, non trovato");
							pe.setInteroperabilityError(true);
							throw pe;
						}
					}
					
				}
				else if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA.equals(asyncInteractionRole)) {
					
					String headerCorrelationId = this.modiProperties.getRestCorrelationIdHeader();
					
					String correlationIdFound = msg.getTransportRequestContext().getHeaderFirstValue(headerCorrelationId);
					
					if(correlationIdFound!=null && !"".equals(correlationIdFound)) {
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE, correlationIdFound);
						if(correlationIdFound.length()<=255) {
							busta.setCollaborazione(correlationIdFound);
						}
					}
					else if(busta.getCollaborazione()!=null) {
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE, busta.getCollaborazione());
						msg.forceTransportHeader(headerCorrelationId, busta.getCollaborazione());
					}
					else if(busta.getRiferimentoMessaggio()!=null) {
						busta.setCollaborazione(busta.getRiferimentoMessaggio());
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE, busta.getRiferimentoMessaggio());
						msg.forceTransportHeader(headerCorrelationId, busta.getRiferimentoMessaggio());
					}
					else {
						ProtocolException pe = new ProtocolException("Header http '"+headerCorrelationId+"', richiesto dal profilo non bloccante PUSH, non trovato");
						pe.setInteroperabilityError(true);
						throw pe;
					}

				}
				
			}
			else {
				
				// pull
				if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO.equals(asyncInteractionRole) ||
						ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA.equals(asyncInteractionRole)) {
					
					String urlInvocata = msg.getTransportRequestContext().getFunctionParameters();
					if(urlInvocata!=null && !"".equals(urlInvocata)) {
						
						String resourcePathAzione = null;
						for (Resource r : apiContenenteRisorsa.getResourceList()) {
							if(r.getNome().equals(azione)) {
								resourcePathAzione = r.getPath();
								break;
							}
						}
						
						if(!urlInvocata.startsWith("/")) {
							urlInvocata = "/" + urlInvocata;
						}
						String correlationIdExtracted = ModIUtilities.extractCorrelationIdFromLocation(resourcePathAzione, urlInvocata, false, this.log);
						if(correlationIdExtracted!=null && correlationIdExtracted.length()<=255) {
							busta.setCollaborazione(correlationIdExtracted);
						}
					}
					
				}
				
			}
		}
		else {
			
			// Flusso di Risposta
			
			String returnCode = null;
			int returnCodeInt = -1;
			if(msg.getTransportResponseContext()!=null) {
				returnCode = msg.getTransportResponseContext().getCodiceTrasporto();
				if(returnCode!=null) {
					try {
						returnCodeInt = Integer.valueOf(returnCode);
					}catch(Exception e) {}
				}
			}
			
			// Fix: il controllo deve essere fatto solo per i codici che non rientrano in 4xx e 5xx
			boolean is_4xx_5xx = (returnCodeInt>=400) && (returnCodeInt<=599);
			
			Integer [] returnCodeAttesi = null;
			
			if(!is_4xx_5xx) {
				if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionType)) {
					
					if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
					
						String headerCorrelationId = this.modiProperties.getRestCorrelationIdHeader();
						
						String correlationIdFound = msg.getTransportResponseContext().getHeaderFirstValue(headerCorrelationId);
						if(correlationIdFound!=null && !"".equals(correlationIdFound) && 
								!ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE_AGGIUNTO_PER_CONSENTIRE_VALIDAZIONE_CONTENUTI.equals(correlationIdFound)) {
							busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE, correlationIdFound);
							busta.setCollaborazione(correlationIdFound);
						}
						else {
							if(this.modiProperties.isRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists()) {
								String idTransazione = msg.getTransactionId();
								msg.forceTransportHeader(headerCorrelationId, idTransazione);
								busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE, idTransazione);
								busta.setCollaborazione(idTransazione);
							}
							else {
								ProtocolException pe = new ProtocolException("Header http '"+headerCorrelationId+"', richiesto dal profilo non bloccante PUSH, non trovato");
								pe.setInteroperabilityError(true);
								throw pe;
							}
						}
						
						returnCodeAttesi = this.modiProperties.getRestNonBloccantePushRequestHttpStatus();
					}
					else {
						
						returnCodeAttesi = this.modiProperties.getRestNonBloccantePushResponseHttpStatus();
						
					}
					
				}
				else {
					
					// pull
					
					String location = null;
					String locationHeader = null;
					
					if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole) ||
							ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO.equals(asyncInteractionRole)) {
						
						locationHeader = this.modiProperties.getRestLocationHeader();
						if(msg!=null) {
							if(msg.getTransportResponseContext()!=null) {
								location = msg.getTransportResponseContext().getHeaderFirstValue(locationHeader);
							}
						}
					}
					
					if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
						if(location==null || "".equals(location)) {
							ProtocolException pe = new ProtocolException("Header http '"+locationHeader+"', richiesto dal profilo non bloccante PULL, non trovato");
							pe.setInteroperabilityError(true);
							throw pe;
						}
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_LOCATION, location);
						
						String correlationIdExtracted = ModIUtilities.extractCorrelationId(location, apiContenenteRisorsa, azione, asyncInteractionRole, this.log);
						if(correlationIdExtracted!=null && correlationIdExtracted.length()<=255) {
							busta.setCollaborazione(correlationIdExtracted);
						}
						
						returnCodeAttesi = this.modiProperties.getRestNonBloccantePullRequestHttpStatus();
					}
					else if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO.equals(asyncInteractionRole)) {
						Integer [] returnCodeResourceReady = this.modiProperties.getRestNonBloccantePullRequestStateOkHttpStatus();
						boolean isReady = false;
						for (Integer integer : returnCodeResourceReady) {
							if(integer.intValue() == returnCodeInt) {
								isReady = true;
								break;
							}
						}
						
						if(isReady) {
							if(location==null || "".equals(location)) {
								ProtocolException pe = new ProtocolException("Header http '"+locationHeader+"', richiesto dal profilo non bloccante PULL, non trovato");
								pe.setInteroperabilityError(true);
								throw pe;
							}
							busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_LOCATION, location);
							
							String correlationIdExtracted = ModIUtilities.extractCorrelationId(location, apiContenenteRisorsa, azione, asyncInteractionRole, this.log);
							if(correlationIdExtracted!=null && correlationIdExtracted.length()<=255) {
								busta.setCollaborazione(correlationIdExtracted);
							}
							
							returnCodeAttesi = returnCodeResourceReady;
						}
						else {
							Integer [] returnCodeAttesi_notReady = this.modiProperties.getRestNonBloccantePullRequestStateNotReadyHttpStatus();
							returnCodeAttesi = new Integer[returnCodeResourceReady.length+returnCodeAttesi_notReady.length];
							int i = 0;
							for (int j=0; j < returnCodeAttesi_notReady.length; j++) {
								returnCodeAttesi[i] = returnCodeAttesi_notReady[j];
								i++;
							}
							for (int j=0; j < returnCodeResourceReady.length; j++) {
								returnCodeAttesi[i] = returnCodeResourceReady[j];
								i++;
							}
						}
					}
					else {
						returnCodeAttesi = this.modiProperties.getRestNonBloccantePullResponseHttpStatus();
					}
					
				}
			}
			
			if(returnCodeAttesi!=null) {
				boolean found = false;
				for (Integer integer : returnCodeAttesi) {
					if(integer.intValue() == returnCodeInt) {
						found = true;
						break;
					}
				}
				if(!found) {
					StringBuilder sb = new StringBuilder();
					for (Integer integer : returnCodeAttesi) {
						if(sb.length()>0) {
							sb.append(",");
						}
						sb.append(integer.intValue());
					}
					ProtocolException pe = new ProtocolException("HTTP Status '"+returnCodeInt+"' differente da quello atteso per il profilo non bloccante '"+asyncInteractionType+"' con ruolo '"+asyncInteractionRole+"' (atteso: "+sb.toString()+")");
					pe.setInteroperabilityError(true);
					throw pe;
				}
			}
		}
		
	}
	
	public String addToken(OpenSPCoop2Message msg, Context context, ModIKeystoreConfig keystoreConfig, ModISecurityConfig securityConfig,
			Busta busta, String securityMessageProfile, String headerTokenRest, boolean corniceSicurezza, RuoloMessaggio ruoloMessaggio, boolean includiRequestDigest,
			Long now, String jti, boolean headerDuplicati,
			Map<String, Object> dynamicMap) throws Exception {
		
		boolean integrita = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(securityMessageProfile) || 
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(securityMessageProfile);
		
		
		/*
		 * == realizzo token ==
		 */
						
		JSONUtils jsonUtils = JSONUtils.getInstance();
		
		ObjectNode payloadToken = jsonUtils.newObjectNode();
		
		// add iat, nbf, exp
		long nowMs = now!=null ? now.longValue() : DateManager.getTimeMillis();
		long nowSeconds = nowMs/1000;
		Date nowDateUsatoPerProprietaTraccia = new Date((nowSeconds*1000)); // e' inutile che traccio i millisecondi, tanto poi nel token non viaggiano
		
		payloadToken.put(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT, nowSeconds);
		String iatValue = DateUtils.getSimpleDateFormatMs().format(nowDateUsatoPerProprietaTraccia);
		if(!headerDuplicati || HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_IAT, iatValue);
		}
		else {
			String iatAuthorization = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_IAT);
			if(!iatValue.equals(iatAuthorization)) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_IAT, iatValue);
			}
		}
		
		payloadToken.put(Claims.JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE, nowSeconds);
		String nbfValue = DateUtils.getSimpleDateFormatMs().format(nowDateUsatoPerProprietaTraccia);
		if(!headerDuplicati || HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_NBF, nbfValue);
		}
		else {
			String nbfAuthorization = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_NBF);
			if(!nbfValue.equals(nbfAuthorization)) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_NBF, nbfValue);
			}
		}
		
		long expired = nowSeconds+securityConfig.getTtlSeconds();
		Date expiredDateUsatoPerProprietaTraccia = new Date(expired*1000);
		payloadToken.put(Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED, expired);
		String expValue = DateUtils.getSimpleDateFormatMs().format(expiredDateUsatoPerProprietaTraccia);
		if(!headerDuplicati || HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_EXP, expValue);
		}
		else {
			String expAuthorization = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_EXP);
			if(!expValue.equals(expAuthorization)) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_EXP, nbfValue);
			}
		}
		
		if(jti!=null) {
			payloadToken.put(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID, jti);
			if(headerDuplicati && (busta==null || !jti.equals(busta.getID()))) {
				if(integrita) {
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_ID, jti);
				}
				else {
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUTHORIZATION_ID, jti);
				}
			}
			else {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_ID, jti);
			}
		}
		
		if(securityConfig.getAudience()!=null) {
			String audience = securityConfig.getAudience();
			//if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio)) {
			// sulla richiesta direttamente come valore dinamico, sulla risposta come claims
			try {
				audience = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE, 
						audience, dynamicMap, context);
			}catch(Exception e) {
				this.log.error(e.getMessage(),e);
				ProtocolException pe = new ProtocolException(e.getMessage());
				pe.setInteroperabilityError(true);
				throw pe;
			}
			//}
			payloadToken.put(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE, audience);
			if(!headerDuplicati || HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE, audience);
			}
			else {
				String audAuthorization = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE);
				if(!audience.equals(audAuthorization)) {
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_AUDIENCE, audience);
				}
			}
		}
		
		if(securityConfig.getClientId()!=null &&
				!DynamicHelperCostanti.NOT_GENERATE.equalsIgnoreCase(securityConfig.getClientId())) {
			String claimName = this.modiProperties.getRestSecurityTokenClaimsClientIdHeader(); //Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID;
			String clientId = securityConfig.getClientId();
			if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio)) {
				try {
					clientId = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_CLIENT_ID, 
							clientId, dynamicMap, context);
				}catch(Exception e) {
					this.log.error(e.getMessage(),e);
					ProtocolException pe = new ProtocolException(e.getMessage());
					pe.setInteroperabilityError(true);
					throw pe;
				}
			}
			payloadToken.put(claimName, clientId);
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_CLIENT_ID, clientId);
		}
						
		if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio)) {
			corniceSicurezza = false; // permessa solo per i messaggi di richiesta
		}
		boolean addIss = true;
		boolean addSub = true;
		
		if(integrita && RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio) && includiRequestDigest) {
			if(context.containsKey(ModICostanti.MODIPA_CONTEXT_REQUEST_DIGEST)) {
				Object o = context.getObject(ModICostanti.MODIPA_CONTEXT_REQUEST_DIGEST);
				String value = (String) o;
				String claimNameRequestDigest = this.modiProperties.getRestSecurityTokenClaimRequestDigest();
				payloadToken.put(claimNameRequestDigest, value);
			}
		}
		
		if(corniceSicurezza) {
			
			String claimNameCodiceEnte = this.modiProperties.getSicurezzaMessaggio_corniceSicurezza_rest_codice_ente();
			if(Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER.equals(claimNameCodiceEnte)) {
				addIss = false;
			}
			String codiceEnte = null;
			try {
				codiceEnte = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL+" - "+ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_LABEL, 
						securityConfig.getCorniceSicurezzaCodiceEnteRule(), dynamicMap, context);
			}catch(Exception e) {
				this.log.error(e.getMessage(),e);
				ProtocolException pe = new ProtocolException(e.getMessage());
				pe.setInteroperabilityError(true);
				throw pe;
			}
			payloadToken.put(claimNameCodiceEnte, codiceEnte);
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CORNICE_SICUREZZA_ENTE, codiceEnte);
			
			String claimNameUser = this.modiProperties.getSicurezzaMessaggio_corniceSicurezza_rest_user();
			if(Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT.equals(claimNameUser)) {
				addSub = false;
			}
			String utente = null;
			try {
				utente = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL+" - "+ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_LABEL,
						securityConfig.getCorniceSicurezzaUserRule(), dynamicMap, context);
			}catch(Exception e) {
				this.log.error(e.getMessage(),e);
				ProtocolException pe = new ProtocolException(e.getMessage());
				pe.setInteroperabilityError(true);
				throw pe;
			}
			payloadToken.put(claimNameUser, utente);
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CORNICE_SICUREZZA_USER, utente);
			
			String claimNameIpUser = this.modiProperties.getSicurezzaMessaggio_corniceSicurezza_rest_ipuser();
			String indirizzoIpPostazione = null;
			try {
				indirizzoIpPostazione = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL+" - "+ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_LABEL,
						securityConfig.getCorniceSicurezzaIpUserRule(), dynamicMap, context);
			}catch(Exception e) {
				this.log.error(e.getMessage(),e);
				ProtocolException pe = new ProtocolException(e.getMessage());
				pe.setInteroperabilityError(true);
				throw pe;
			}
			payloadToken.put(claimNameIpUser, indirizzoIpPostazione);
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CORNICE_SICUREZZA_USER_IP, indirizzoIpPostazione);
						
		}
		
		if(addIss) {
			if(securityConfig.getIssuer()!=null &&
					!DynamicHelperCostanti.NOT_GENERATE.equalsIgnoreCase(securityConfig.getIssuer())) {
				String issuer = null;
				try {
					issuer = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_ISSUER, 
							securityConfig.getIssuer(), dynamicMap, context);
				}catch(Exception e) {
					this.log.error(e.getMessage(),e);
					ProtocolException pe = new ProtocolException(e.getMessage());
					pe.setInteroperabilityError(true);
					throw pe;
				}
				payloadToken.put(Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER, issuer);
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_ISSUER, issuer);
			}
		}
		if(addSub) {
			if(securityConfig.getSubject()!=null &&
					!DynamicHelperCostanti.NOT_GENERATE.equalsIgnoreCase(securityConfig.getSubject())) {
				String subject = null;
				try {
					subject = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_SUBJECT, 
							securityConfig.getSubject(), dynamicMap, context);
				}catch(Exception e) {
					this.log.error(e.getMessage(),e);
					ProtocolException pe = new ProtocolException(e.getMessage());
					pe.setInteroperabilityError(true);
					throw pe;
				}
				payloadToken.put(Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT, subject);
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_SUBJECT, subject);
			}
		}
		
		Map<String, String> claimsCustom = new HashMap<String, String>();
		if(securityConfig.getMultipleHeaderClaims()!=null && !securityConfig.getMultipleHeaderClaims().isEmpty()) {
			for (Object oKeyP : securityConfig.getMultipleHeaderClaims().keySet()) {
				if(oKeyP!=null && oKeyP instanceof String) {
					String key = (String) oKeyP;
					String value = securityConfig.getMultipleHeaderClaims().getProperty(key);
					claimsCustom.put(key, value);
				}
			}
		}
		if(securityConfig.getClaims()!=null && !securityConfig.getClaims().isEmpty()) {
			for (Object oKeyP : securityConfig.getClaims().keySet()) {
				if(oKeyP!=null && oKeyP instanceof String) {
					String key = (String) oKeyP;
					String value = securityConfig.getClaims().getProperty(key);
					if(!claimsCustom.containsKey(key)) {
						claimsCustom.put(key, value);
					}
				}
			}
		}
		if(!claimsCustom.isEmpty()) {
			for (String key : claimsCustom.keySet()) {
				String value =  claimsCustom.get(key);
				try {
					value = ModIUtilities.getDynamicValue(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_LABEL+" - "+key, 
							value, dynamicMap, context);
				}catch(Exception e) {
					this.log.error(e.getMessage(),e);
					ProtocolException pe = new ProtocolException(e.getMessage());
					pe.setInteroperabilityError(true);
					throw pe;
				}
				if(value!=null) {
					payloadToken.put(key, value);
					// Non lo aggiungo alla traccia per adesso
					//busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_CLAIM_PREFIX+key, value);
				}
			}
		}
		
				
		
		if(integrita) {
			
			String digestValue = null;
			if(msg.castAsRest().hasContent()) {
			
				// Produco Digest Headers
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				msg.writeTo(bout, false);
				bout.flush();
				bout.close();
				
				digestValue = HttpUtilities.getDigestHeaderValue(bout.toByteArray(), securityConfig.getDigestAlgorithm());
				msg.forceTransportHeader(HttpConstants.DIGEST, digestValue);
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_DIGEST, digestValue);
			
			}
				
			if(securityConfig.getHttpHeaders()!=null && !securityConfig.getHttpHeaders().isEmpty()) {
				
				ArrayNode signedHeaders = jsonUtils.newArrayNode();
				
				Map<String, List<String>> mapForceTransportHeaders = msg.getForceTransportHeaders();
				
				for (String httpHeader : securityConfig.getHttpHeaders()) {
					
					ObjectNode httpHeaderNode = null;
					String hdrName = null;
					List<String> hdrValues = new ArrayList<String>();
					
					if(httpHeader.toLowerCase().equals(HttpConstants.DIGEST.toLowerCase())) {
						if(digestValue!=null) {
							hdrName = HttpConstants.DIGEST;
							hdrValues.add(digestValue);
						}
						else if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio) && msg.getTransportResponseContext()!=null && 
								msg.getTransportRequestContext()!=null && org.openspcoop2.utils.transport.http.HttpRequestMethod.HEAD.equals(msg.getTransportRequestContext().getRequestType()) &&
								this.modiProperties.isRestSecurityTokenResponseDigestHEADuseServerHeader()) {
							List<String> digestValueHEAD = msg.getTransportResponseContext().getHeaderValues(HttpConstants.DIGEST);
							if(digestValueHEAD!=null && !digestValueHEAD.isEmpty()) {
								hdrName = HttpConstants.DIGEST;
								hdrValues.addAll(digestValueHEAD);
							}
						}
					}
					else {
						List<String> values = null;
						if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio) && msg.getTransportRequestContext()!=null) {
							values = msg.getTransportRequestContext().getHeaderValues(httpHeader);
						}
						else if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio) && msg.getTransportResponseContext()!=null) {
							values = msg.getTransportResponseContext().getHeaderValues(httpHeader);
						}
						
						// guardo anche eventuali header aggiunti tramite trasformazioni e da precedente chiamata di questo metodo (Authorization)
						// Se presenti sovrascrivono gli header iniziali(metodo forward usato nei connettori)
						if(mapForceTransportHeaders!=null && !mapForceTransportHeaders.isEmpty()) {
							List<String> v = TransportUtils.getRawObject(mapForceTransportHeaders, httpHeader);
							if(v!=null && !v.isEmpty()) {
								values = v;
							}
						}
						
						if(values!=null && !values.isEmpty()) {
							hdrName = httpHeader;
							hdrValues.addAll(values);
						}
						
					}
					if(hdrName!=null) {
						for (String hdrValue : hdrValues) {
							httpHeaderNode = jsonUtils.newObjectNode();
							String hdrNameLowerCase = hdrName.toLowerCase(); // uso come nome dell'header tutto minuscolo come indicato nella specifica, tra gli esempi
							httpHeaderNode.put(hdrNameLowerCase, hdrValue); 
							if(HttpConstants.AUTHORIZATION.equalsIgnoreCase(hdrNameLowerCase) && headerDuplicati) {
								ModIUtilities.addHeaderProperty(busta, hdrNameLowerCase, HttpConstants.AUTHORIZATION_PREFIX_BEARER +"...TOKEN...");	
							}
							else {
								ModIUtilities.addHeaderProperty(busta, hdrNameLowerCase, hdrValue);
							}
							signedHeaders.add(httpHeaderNode);	
						}
					}
				}
				
				if(signedHeaders.size()>0) {
					String claimSignedHeader = this.modiProperties.getRestSecurityTokenClaimSignedHeaders();
					payloadToken.set(claimSignedHeader, signedHeaders);
				}
			}
		}
		
		String jsonPayload = jsonUtils.toString(payloadToken);
		OpenSPCoop2Message payload = msg.getFactory().createMessage(MessageType.JSON, MessageRole.NONE, 
				HttpConstants.CONTENT_TYPE_JSON, jsonPayload.getBytes(), null, null).getMessage_throwParseException();
		
		
		/*
		 * == signature ==
		 */
		
		MessageSecuritySender_jose joseSignature = new MessageSecuritySender_jose();
		MessageSecurityContextParameters messageSecurityContextParameters = new MessageSecurityContextParameters();
		MessageSecurityContext messageSecurityContext = new MessageSecurityContext_impl(messageSecurityContextParameters);
		Hashtable<String,Object> secProperties = new Hashtable<>();
		secProperties.put(SecurityConstants.SECURITY_ENGINE, SecurityConstants.SECURITY_ENGINE_JOSE);
		secProperties.put(SecurityConstants.ACTION, SecurityConstants.SIGNATURE_ACTION);
		secProperties.put(SecurityConstants.SIGNATURE_MODE, SecurityConstants.SIGNATURE_MODE_COMPACT);
		secProperties.put(SecurityConstants.SIGNATURE_DETACHED, SecurityConstants.SIGNATURE_DETACHED_FALSE);
		secProperties.put(SecurityConstants.JOSE_USE_HEADERS, SecurityConstants.JOSE_USE_HEADERS_TRUE);
		secProperties.put(SecurityConstants.JOSE_CONTENT_TYPE, SecurityConstants.JOSE_CONTENT_TYPE_FALSE);
		secProperties.put(SecurityConstants.JOSE_TYPE, org.apache.cxf.rs.security.jose.common.JoseConstants.TYPE_JWT);
		
		// algoritmi
		secProperties.put(SecurityConstants.SIGNATURE_ALGORITHM, securityConfig.getAlgorithm());
		
		// spedizione certificato
		secProperties.put(SecurityConstants.JOSE_KID, SecurityConstants.JOSE_KID_TRUE); // kid
		if(securityConfig.isX5c()) {
			secProperties.put(SecurityConstants.JOSE_INCLUDE_CERT, SecurityConstants.JOSE_INCLUDE_CERT_TRUE);
			if(!securityConfig.isUseSingleCertificate()) {
				secProperties.put(SecurityConstants.JOSE_INCLUDE_CERT_CHAIN, SecurityConstants.JOSE_INCLUDE_CERT_CHAIN_TRUE);
			}
		}
		if(securityConfig.isX5t()) {
			secProperties.put(SecurityConstants.JOSE_INCLUDE_CERT_SHA, SecurityConstants.JOSE_INCLUDE_CERT_SHA_256);
		}
		if(securityConfig.isX5u()) {
			secProperties.put(SecurityConstants.JOSE_X509_URL, securityConfig.getX5url());
		}
		
		// keystore
//		if(keystoreConfig.getSecurityMessageKeystorePath()!=null) {
//			Properties pSignature = new Properties();
//			pSignature.put(org.apache.cxf.rs.security.jose.common.JoseConstants.RSSEC_KEY_STORE_FILE, keystoreConfig.getSecurityMessageKeystorePath());
//			pSignature.put(org.apache.cxf.rs.security.jose.common.JoseConstants.RSSEC_KEY_STORE_TYPE, keystoreConfig.getSecurityMessageKeystoreType());
//			pSignature.put(org.apache.cxf.rs.security.jose.common.JoseConstants.RSSEC_KEY_STORE_PSWD, keystoreConfig.getSecurityMessageKeystorePassword());
//			pSignature.put(org.apache.cxf.rs.security.jose.common.JoseConstants.RSSEC_KEY_STORE_ALIAS, keystoreConfig.getSecurityMessageKeyAlias());
//			pSignature.put(org.apache.cxf.rs.security.jose.common.JoseConstants.RSSEC_KEY_PSWD, keystoreConfig.getSecurityMessageKeyPassword());
//			pSignature.put(org.apache.cxf.rs.security.jose.common.JoseConstants.RSSEC_SIGNATURE_ALGORITHM, securityConfig.getAlgorithm());
//			pSignature.put(org.apache.cxf.rs.security.jose.common.JoseConstants.RSSEC_SIGNATURE_INCLUDE_KEY_ID, SecurityConstants.JOSE_KID_TRUE); // kid
//			if(securityConfig.isX5c()) {
//				pSignature.put(org.apache.cxf.rs.security.jose.common.JoseConstants.RSSEC_SIGNATURE_INCLUDE_CERT, SecurityConstants.JOSE_KID_TRUE);
//			}
//			if(securityConfig.isX5t()) {
//				pSignature.put(org.apache.cxf.rs.security.jose.common.JoseConstants.RSSEC_SIGNATURE_INCLUDE_CERT_SHA256, SecurityConstants.JOSE_KID_TRUE);
//			}
//			secProperties.put(SecurityConstants.SIGNATURE_PROPERTY_REF_ID, pSignature);
//		}
//		else {
		SignatureBean signatureBean = new SignatureBean();
		org.openspcoop2.utils.certificate.KeyStore ks = null;
		if(keystoreConfig.getSecurityMessageKeystorePath()!=null) {
			MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(keystoreConfig.getSecurityMessageKeystorePath(), keystoreConfig.getSecurityMessageKeystoreType(), 
					keystoreConfig.getSecurityMessageKeystorePassword());
			if(merlinKs==null) {
				throw new Exception("Accesso al keystore '"+keystoreConfig.getSecurityMessageKeystorePath()+"' non riuscito");
			}
			ks = merlinKs.getKeyStore();
		}
		else {
			MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(keystoreConfig.getSecurityMessageKeystoreArchive(), keystoreConfig.getSecurityMessageKeystoreType(), 
					keystoreConfig.getSecurityMessageKeystorePassword());
			if(merlinKs==null) {
				throw new Exception("Accesso al keystore non riuscito");
			}
			ks = merlinKs.getKeyStore();
		}
		signatureBean.setKeystore(ks);
		signatureBean.setUser(keystoreConfig.getSecurityMessageKeyAlias());
		signatureBean.setPassword(keystoreConfig.getSecurityMessageKeyPassword());
		messageSecurityContext.setSignatureBean(signatureBean);
		//}
				
		// setProperties
		messageSecurityContext.setOutgoingProperties(secProperties, false);

		// firma
		joseSignature.process(messageSecurityContext, payload);
		
		// Aggiungo a traccia informazioni sul certificato utilizzato
		Certificate certificate = ks.getCertificate(keystoreConfig.getSecurityMessageKeyAlias());
		if(certificate!=null && certificate instanceof X509Certificate) {
			X509Certificate x509 = (X509Certificate) certificate;
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_X509_SUBJECT, x509.getSubjectX500Principal().toString());
			if(x509.getIssuerX500Principal()!=null) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_X509_ISSUER, x509.getIssuerX500Principal().toString());
			}
		}
		
		
		/*
		 * == return token ==
		 */
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		payload.writeTo(bout, true);
		bout.flush();
		bout.close();
		
		String securityTokenHeader = headerTokenRest;
		if(HttpConstants.AUTHORIZATION.equalsIgnoreCase(securityTokenHeader)) {
			msg.forceTransportHeader(securityTokenHeader, HttpConstants.AUTHORIZATION_PREFIX_BEARER+bout.toString());
		}
		else {
			msg.forceTransportHeader(securityTokenHeader, bout.toString());
		}
		
		return bout.toString();
	}
	
}
