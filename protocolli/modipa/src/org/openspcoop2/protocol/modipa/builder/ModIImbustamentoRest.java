/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.pdd.core.token.Costanti;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.protocol.modipa.config.ModIAuditClaimConfig;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.constants.ModIHeaderType;
import org.openspcoop2.protocol.modipa.utils.ModIKeystoreConfig;
import org.openspcoop2.protocol.modipa.utils.ModISecurityConfig;
import org.openspcoop2.protocol.modipa.utils.ModIUtilities;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.RestMessageSecurityToken;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.keystore.JWKSetStore;
import org.openspcoop2.security.keystore.KeyPairStore;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContextParameters;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.engine.MessageSecurityContext_impl;
import org.openspcoop2.security.message.jose.MessageSecuritySender_jose;
import org.openspcoop2.security.message.utils.SignatureBean;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.random.RandomGenerator;
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

	private Logger log;
	private ModIProperties modiProperties;
	public ModIImbustamentoRest(Logger log) throws ProtocolException {
		this.log = log;
		this.modiProperties = ModIProperties.getInstance();
	}
	
	public void addSyncInteractionProfile(OpenSPCoop2Message msg, RuoloMessaggio ruoloMessaggio) throws ProtocolException {
	
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
					}catch(Exception e) {
						// ignore
					}
				}
			}
			
			Integer [] returnCodeAttesi = this.modiProperties.getRestBloccanteHttpStatus();
			
			// Fix: il controllo deve essere fatto solo per i codici che non rientrano in 4xx e 5xx
			boolean is4xx5xx = (returnCodeInt>=400) && (returnCodeInt<=599);
			
			if(!is4xx5xx && returnCodeAttesi!=null) {				
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
	
	private static String getHeaderHttpPrefix(String hdr) {
		return "Header http '"+hdr+"'";
	}
	private ProtocolException newProtocolExceptionAccessoKeystoreNonRiuscito(String type,String path) {
		return new ProtocolException("Accesso al keystore ["+type+"] '"+path+"' non riuscito");
	}
	private static final String RICHIESTO_NON_BLOCCANTE_PUSH = ", richiesto dal profilo non bloccante PUSH, non trovato";
	
	public void addAsyncInteractionProfile(OpenSPCoop2Message msg, Busta busta, RuoloMessaggio ruoloMessaggio,
			String asyncInteractionType, String asyncInteractionRole,
			String replyTo,
			AccordoServizioParteComune apiContenenteRisorsa, String azione) throws ProtocolException {
		
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
							ProtocolException pe = new ProtocolException(getHeaderHttpPrefix(headerReplyName)+RICHIESTO_NON_BLOCCANTE_PUSH);
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
						ProtocolException pe = new ProtocolException(getHeaderHttpPrefix(headerCorrelationId)+RICHIESTO_NON_BLOCCANTE_PUSH);
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
			if(msg!=null && msg.getTransportResponseContext()!=null) {
				returnCode = msg.getTransportResponseContext().getCodiceTrasporto();
				if(returnCode!=null) {
					try {
						returnCodeInt = Integer.valueOf(returnCode);
					}catch(Exception e) {
						// ignore
					}
				}
			}
			
			// Fix: il controllo deve essere fatto solo per i codici che non rientrano in 4xx e 5xx
			boolean is4xx5xx = (returnCodeInt>=400) && (returnCodeInt<=599);
			
			Integer [] returnCodeAttesi = null;
			
			if(!is4xx5xx) {
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
								ProtocolException pe = new ProtocolException(getHeaderHttpPrefix(headerCorrelationId)+RICHIESTO_NON_BLOCCANTE_PUSH);
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
						if(msg!=null &&
							msg.getTransportResponseContext()!=null) {
							location = msg.getTransportResponseContext().getHeaderFirstValue(locationHeader);
						}
					}
					
					if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
						if(location==null || "".equals(location)) {
							ProtocolException pe = new ProtocolException(getHeaderHttpPrefix(locationHeader)+", richiesto dal profilo non bloccante PULL, non trovato");
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
								ProtocolException pe = new ProtocolException(getHeaderHttpPrefix(locationHeader)+", richiesto dal profilo non bloccante PULL, non trovato");
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
							Integer [] returnCodeAttesiNotReady = this.modiProperties.getRestNonBloccantePullRequestStateNotReadyHttpStatus();
							returnCodeAttesi = new Integer[returnCodeResourceReady.length+returnCodeAttesiNotReady.length];
							int i = 0;
							for (int j=0; j < returnCodeAttesiNotReady.length; j++) {
								returnCodeAttesi[i] = returnCodeAttesiNotReady[j];
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
	
	public ModIJWTToken addToken(OpenSPCoop2Message msg, boolean request, Context context, ModIKeystoreConfig keystoreConfig, ModISecurityConfig securityConfig,
			Busta busta, String securityMessageProfile, boolean useKIDtokenHeader, String headerTokenRest, 
			boolean corniceSicurezza, String corniceSicurezzaPattern, String corniceSicurezzaSchema,
			RuoloMessaggio ruoloMessaggio, boolean includiRequestDigest,
			Long nowParam, String jti, ModIHeaderType headerType, boolean integritaCustom,
			Map<String, Object> dynamicMap, RequestInfo requestInfo,
			String purposeId, boolean sicurezzaRidefinitaOperazione) throws ProtocolException {
				
		if(busta==null) {
			throw new ProtocolException("Param busta is null");
		}
		
		boolean integritaX509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(securityMessageProfile) || 
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(securityMessageProfile);
		boolean integritaKid = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(securityMessageProfile) || 
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(securityMessageProfile);
		boolean integrita = integritaX509 || integritaKid;
		
		boolean headerDuplicati = headerType.isHeaderDuplicati();
		boolean headerAuthentication = headerType.isUsabledForAuthentication();
		
		boolean tokenAudit = corniceSicurezza && !CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD.equals(corniceSicurezzaPattern) && corniceSicurezzaSchema!=null;
		
		boolean audit02 = tokenAudit && CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02.equals(corniceSicurezzaPattern);
		
		boolean apiSoap = ServiceBinding.SOAP.equals(msg.getServiceBinding());
		
		boolean cacheble = !integrita;
		
		String idTransazione = msg.getTransactionId();
		if(idTransazione==null &&
			context!=null && context.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) {
			idTransazione = Context.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, context);
		}
		
		ModIJWTToken modiToken = new ModIJWTToken(idTransazione);
		String nomePorta = null;
		if(requestInfo!=null && requestInfo.getProtocolContext()!=null) {
			nomePorta = requestInfo.getProtocolContext().getInterfaceName();
		}
		
		ModIJWTTokenClaims modiTokenClaims = new ModIJWTTokenClaims(tokenAudit, idTransazione, request, nomePorta, busta.getAzione(), sicurezzaRidefinitaOperazione);
		modiToken.setClaims(modiTokenClaims);
		
		/*
		 * == realizzo token ==
		 */
						
		JSONUtils jsonUtils = JSONUtils.getInstance();
		
		ObjectNode payloadToken = null;
		try {
			payloadToken = jsonUtils.newObjectNode();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		// add iat, nbf, exp
		long nowMs = nowParam!=null ? nowParam.longValue() : DateManager.getTimeMillis();
		long nowSeconds = nowMs/1000;
		Date nowDateUsatoPerProprietaTraccia = new Date((nowSeconds*1000)); // e' inutile che traccio i millisecondi, tanto poi nel token non viaggiano
		
		// iat
		payloadToken.put(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT, nowSeconds);
		String iatValue = DateUtils.getSimpleDateFormatMs().format(nowDateUsatoPerProprietaTraccia);
		if(!headerDuplicati || HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
			busta.addProperty(tokenAudit ? ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_IAT : ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_IAT,
					iatValue);
		}
		else {
			String iatAuthorization = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_IAT);
			if(!iatValue.equals(iatAuthorization)) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_IAT, iatValue);
			}
		}
		modiTokenClaims.setIat(nowDateUsatoPerProprietaTraccia);
		modiTokenClaims.setIatValue(iatValue);
		
		// nbf
		payloadToken.put(Claims.JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE, nowSeconds);
		String nbfValue = DateUtils.getSimpleDateFormatMs().format(nowDateUsatoPerProprietaTraccia);
		if(!headerDuplicati || HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
			busta.addProperty(tokenAudit ? ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_NBF : ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_NBF, 
					nbfValue);
		}
		else {
			String nbfAuthorization = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_NBF);
			if(!nbfValue.equals(nbfAuthorization)) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_NBF, nbfValue);
			}
		}
		modiTokenClaims.setNbf(nowDateUsatoPerProprietaTraccia);
		modiTokenClaims.setNbfValue(nbfValue);
		
		// exp
		long expired = nowSeconds+securityConfig.getTtlSeconds();
		Date expiredDateUsatoPerProprietaTraccia = new Date(expired*1000);
		payloadToken.put(Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED, expired);
		String expValue = DateUtils.getSimpleDateFormatMs().format(expiredDateUsatoPerProprietaTraccia);
		if(!headerDuplicati || HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
			busta.addProperty(tokenAudit ? ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_EXP : ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_EXP, 
					expValue);
		}
		else {
			String expAuthorization = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_EXP);
			if(!expValue.equals(expAuthorization)) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_EXP, nbfValue);
			}
		}
		modiToken.setExp(expiredDateUsatoPerProprietaTraccia);
		modiTokenClaims.setExp(expiredDateUsatoPerProprietaTraccia);
		modiTokenClaims.setExpValue(expValue);
		
		// jti
		if(jti!=null) {
			payloadToken.put(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID, jti);
			if(tokenAudit) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_ID, jti);
			}
			else {
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
			modiTokenClaims.setJti(jti);
		}
		
		// audit
		String audience = null;
		if(tokenAudit) {
			audience = securityConfig.getCorniceSicurezzaAudience();
			// sulla richiesta direttamente come valore dinamico, sulla risposta come claims
			try {
				audience = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_AUDIENCE, 
						audience, dynamicMap, context);
			}catch(Exception e) {
				this.log.error(e.getMessage(),e);
				ProtocolException pe = new ProtocolException(e.getMessage());
				pe.setInteroperabilityError(true);
				throw pe;
			}
			/**}*/
			payloadToken.put(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE, audience);
			String audAuthorization = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE);
			if(audAuthorization==null || !audience.equals(audAuthorization)) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_AUDIENCE, audience);
			}
		}
		else if(securityConfig.getAudience()!=null) {
			audience = securityConfig.getAudience();
			/**if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio)) {*/
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
			/**}*/
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
		modiTokenClaims.setAudience(audience);
		
		// clientId
		boolean addClientId = false;
		if(tokenAudit) {
			if(useKIDtokenHeader) {
				addClientId = securityConfig.getCorniceSicurezzaSchemaConfig().isClientIdOAuth();
			}
			else {
				addClientId = securityConfig.getCorniceSicurezzaSchemaConfig().isClientIdLocale();
			}
		}
		else {
			addClientId = true;
		}
		
		String clientId = null;
		if(securityConfig.getClientId()!=null &&
				!DynamicHelperCostanti.NOT_GENERATE.equalsIgnoreCase(securityConfig.getClientId())) {
			clientId = securityConfig.getClientId();
		}
		boolean clientIdKid = false;
		if(integritaKid || useKIDtokenHeader || tokenAudit) {
			String clientIdIntegritaKid = securityConfig.getTokenClientId();
			if(clientIdIntegritaKid!=null) {
				try {
					clientIdIntegritaKid = ModIUtilities.getDynamicValue(tokenAudit ? ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_CLIENT_ID : ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_CLIENT_ID, 
							clientIdIntegritaKid, dynamicMap, context);
				}catch(Exception e) {
					this.log.error(e.getMessage(),e);
					ProtocolException pe = new ProtocolException(e.getMessage());
					pe.setInteroperabilityError(true);
					throw pe;
				}
			}
			if(clientIdIntegritaKid!=null && StringUtils.isNotEmpty(clientIdIntegritaKid)) {
				clientId = clientIdIntegritaKid;
				clientIdKid = true;
			}
		}
		if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio) && clientId!=null && !clientIdKid) {
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
		if(addClientId && clientId!=null) {
			String claimName = this.modiProperties.getRestSecurityTokenClaimsClientIdHeader(); 
			payloadToken.put(claimName, clientId);
			busta.addProperty(tokenAudit ? ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_CLIENT_ID : ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_CLIENT_ID, clientId);
			modiTokenClaims.setClientId(clientId);
		}
		
		// kid
		String kid = null;
		if(integritaKid || useKIDtokenHeader) {
			kid = securityConfig.getTokenKid();
			if(kid!=null) {
				try {
					kid = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_KID, 
							kid, dynamicMap, context);
				}catch(Exception e) {
					this.log.error(e.getMessage(),e);
					ProtocolException pe = new ProtocolException(e.getMessage());
					pe.setInteroperabilityError(true);
					throw pe;
				}
				modiTokenClaims.setKid(kid);
			}
		}
		
		if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio)) {
			corniceSicurezza = false; // permessa solo per i messaggi di richiesta
		}
		boolean addIss = true;
		boolean addSub = true;
		
		// requestDigest (integrita)
		if(integrita && RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio) && includiRequestDigest &&
			context.containsKey(ModICostanti.MODIPA_CONTEXT_REQUEST_DIGEST)) {
			Object o = context.getObject(ModICostanti.MODIPA_CONTEXT_REQUEST_DIGEST);
			String value = (String) o;
			String claimNameRequestDigest = this.modiProperties.getRestSecurityTokenClaimRequestDigest();
			payloadToken.put(claimNameRequestDigest, value);
			modiTokenClaims.setRequestDigest(value);
		}
		
		// coriniceSicurezza/auditInfo
		if(corniceSicurezza) {
			
			if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD.equals(corniceSicurezzaPattern)) {
				
				addIss = addCorniceSicurezzaLegacyCodiceEnte(securityConfig,
						dynamicMap, context,
						payloadToken, busta,
						modiTokenClaims);
				
				addSub = addCorniceSicurezzaLegacyUser(securityConfig,
						dynamicMap, context,
						payloadToken, busta,
						modiTokenClaims);
				
				addCorniceSicurezzaLegacyIpUser(securityConfig,
						dynamicMap, context,
						payloadToken, busta,
						modiTokenClaims);
			}
			else {
				
				addCorniceSicurezzaSchema(securityConfig,
						dynamicMap, context,
						payloadToken, busta,
						corniceSicurezzaSchema,
						modiTokenClaims);
				
				if(useKIDtokenHeader) {
					addIss = securityConfig.getCorniceSicurezzaSchemaConfig().isIssOAuth();
					addSub = securityConfig.getCorniceSicurezzaSchemaConfig().isSubOAuth();
				}
				else {
					addIss = securityConfig.getCorniceSicurezzaSchemaConfig().isIssLocale();
					addSub = securityConfig.getCorniceSicurezzaSchemaConfig().isSubLocale();
				}
				
			}
						
		}
		
		// iss
		if(addIss) {
			String issuer = null;
			if(tokenAudit) {
				if(clientId==null) {
					ProtocolException pe = new ProtocolException("ClientId undefined; required in audit token 'iss' claim");
					pe.setInteroperabilityError(true);
					throw pe;
				}
				issuer = clientId;
			}
			else if(securityConfig.getIssuer()!=null &&
					!DynamicHelperCostanti.NOT_GENERATE.equalsIgnoreCase(securityConfig.getIssuer())) {
				try {
					issuer = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_ISSUER, 
							securityConfig.getIssuer(), dynamicMap, context);
				}catch(Exception e) {
					this.log.error(e.getMessage(),e);
					ProtocolException pe = new ProtocolException(e.getMessage());
					pe.setInteroperabilityError(true);
					throw pe;
				}
			}
			if(issuer!=null) {
				payloadToken.put(Claims.INTROSPECTION_RESPONSE_RFC_7662_ISSUER, issuer);
				busta.addProperty(tokenAudit ? ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_ISSUER : ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_ISSUER, 
						issuer);
				modiTokenClaims.setIss(issuer);
			}
		}
		
		// sub
		if(addSub) {
			String subject = null;
			if(tokenAudit) {
				if(clientId==null) {
					ProtocolException pe = new ProtocolException("ClientId undefined; required in audit token 'sub' claim");
					pe.setInteroperabilityError(true);
					throw pe;
				}
				subject = clientId;
			}
			else if(securityConfig.getSubject()!=null &&
					!DynamicHelperCostanti.NOT_GENERATE.equalsIgnoreCase(securityConfig.getSubject())) {
				try {
					subject = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_SUBJECT, 
							securityConfig.getSubject(), dynamicMap, context);
				}catch(Exception e) {
					this.log.error(e.getMessage(),e);
					ProtocolException pe = new ProtocolException(e.getMessage());
					pe.setInteroperabilityError(true);
					throw pe;
				}
			}
			if(subject!=null) {
				payloadToken.put(Claims.INTROSPECTION_RESPONSE_RFC_7662_SUBJECT, subject);
				busta.addProperty(tokenAudit ? ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_SUBJECT : ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_SUBJECT, 
						subject);
				modiTokenClaims.setSub(subject);
			}
		}
		
		// purposeId
		if(purposeId!=null) {
			boolean addPurposeId = false;
			if(tokenAudit) {
				addPurposeId = this.modiProperties.isSecurityTokenAuditAddPurposeId();
			}
			else if(integritaX509) {
				addPurposeId = this.modiProperties.isSecurityTokenIntegrity01AddPurposeId();
			}
			else if(integritaKid) {
				addPurposeId = this.modiProperties.isSecurityTokenIntegrity02AddPurposeId();
			}
			if(addPurposeId) {
				String purposeValue = null;
				try {
					purposeValue = ModIUtilities.getDynamicValue(tokenAudit ? ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_PURPOSE_ID : ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_PURPOSE_ID, 
							purposeId, dynamicMap, context);
				}catch(Exception e) {
					this.log.error(e.getMessage(),e);
					ProtocolException pe = new ProtocolException(e.getMessage());
					pe.setInteroperabilityError(true);
					throw pe;
				}
				if(purposeValue!=null) {
					payloadToken.put(Costanti.PDND_PURPOSE_ID, purposeValue);
					busta.addProperty(tokenAudit ? ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_PURPOSE_ID : ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_PURPOSE_ID, 
							purposeValue);
					modiTokenClaims.setPurposeId(purposeValue);
				}
			}
		}
		
		if(audit02) {
			
			String dNonce = RandomGenerator.getDefaultInstance().nextRandomInt(this.modiProperties.getSecurityTokenAuditDnonceSize());
			payloadToken.put(Costanti.PDND_DNONCE, dNonce);
			modiTokenClaims.setdNonce(dNonce);
				
		}
		
		// custom claims
		Map<String, String> claimsCustom = new HashMap<>();
		if(securityConfig.getMultipleHeaderClaims()!=null && !securityConfig.getMultipleHeaderClaims().isEmpty()) {
			for (Object oKeyP : securityConfig.getMultipleHeaderClaims().keySet()) {
				if(oKeyP instanceof String) {
					String key = (String) oKeyP;
					String value = securityConfig.getMultipleHeaderClaims().getProperty(key);
					claimsCustom.put(key, value);
				}
			}
		}
		if(securityConfig.getClaims()!=null && !securityConfig.getClaims().isEmpty()) {
			for (Object oKeyP : securityConfig.getClaims().keySet()) {
				if(oKeyP instanceof String) {
					String key = (String) oKeyP;
					String value = securityConfig.getClaims().getProperty(key);
					if(!claimsCustom.containsKey(key)) {
						claimsCustom.put(key, value);
					}
				}
			}
		}
		if(!claimsCustom.isEmpty()) {
			
			List<String> blackListRegistrazioneCustomClaims = null;
			boolean tracciaCustomClaims = this.modiProperties.isGenerazioneTracceRegistraCustomClaims();
			if(tracciaCustomClaims) {
				blackListRegistrazioneCustomClaims = this.modiProperties.getGenerazioneTracceRegistraCustomClaimsBlackList();
			}
			
			for (Map.Entry<String,String> entry : claimsCustom.entrySet()) {
				String key = entry.getKey();
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
					try {
						jsonUtils.putValue(payloadToken, key, value); // per salvaguardare i tipi
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
					
					if(tracciaCustomClaims &&
							(blackListRegistrazioneCustomClaims==null || blackListRegistrazioneCustomClaims.isEmpty() || !blackListRegistrazioneCustomClaims.contains(key))) {
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_CLAIM_PREFIX+key, value);
					}
					
					modiTokenClaims.addCustomClaim(key, value);
				}
			}
		}
		
				
		// integrita
		if(integrita && !integritaCustom) {
			
			String digestValue = null;
			boolean hasContent = false;
			try {
				hasContent = msg.castAsRest().hasContent();
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			if(hasContent) {
			
				// Produco Digest Headers
				try {
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					msg.writeTo(bout, false);
					bout.flush();
					bout.close();
					
					digestValue = HttpUtilities.getDigestHeaderValue(bout.toByteArray(), securityConfig.getDigestAlgorithm(), securityConfig.getDigestEncoding());
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
				msg.forceTransportHeader(HttpConstants.DIGEST, digestValue);
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_DIGEST, digestValue);
			
			}
				
			if(securityConfig.getHttpHeaders()!=null && !securityConfig.getHttpHeaders().isEmpty()) {
				
				ArrayNode signedHeaders = null;
				try {
					signedHeaders = jsonUtils.newArrayNode();
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
				
				Map<String, List<String>> mapForceTransportHeaders = msg.getForceTransportHeaders();
				
				for (String httpHeader : securityConfig.getHttpHeaders()) {
					
					ObjectNode httpHeaderNode = null;
					String hdrName = null;
					List<String> hdrValues = new ArrayList<>();
					
					if(httpHeader.equalsIgnoreCase(HttpConstants.DIGEST)) {
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
					else if(httpHeader.equalsIgnoreCase(HttpConstants.CONTENT_TYPE)) {
						// l'engine potrebbe modificarlo (negli spazi o nell'ordine dei caratteri)
						String ct = null;
						try {
							ct = msg.getContentType();
						}catch(Exception e) {
							throw new ProtocolException(e.getMessage(),e);
						}
						if(ct!=null) {
							hdrName = httpHeader;
							hdrValues.add(ct);
						}
					}
					else {
						List<String> values = getHeaderValues(ruoloMessaggio, msg, mapForceTransportHeaders, httpHeader);
						if(values!=null && !values.isEmpty()) {
							hdrName = httpHeader;
							hdrValues.addAll(values);
						}
						
					}
					if(hdrName!=null) {
						for (String hdrValue : hdrValues) {
							try {
								httpHeaderNode = jsonUtils.newObjectNode();
							}catch(Exception e) {
								throw new ProtocolException(e.getMessage(),e);
							}
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
		

		
		
		

		
		
		/*
		 * == signature ==
		 */
		
		MessageSecuritySender_jose joseSignature = new MessageSecuritySender_jose();
		MessageSecurityContextParameters messageSecurityContextParameters = new MessageSecurityContextParameters();
		MessageSecurityContext messageSecurityContext = new MessageSecurityContext_impl(messageSecurityContextParameters);
		Map<String,Object> secProperties = new HashMap<>();
		secProperties.put(SecurityConstants.SECURITY_ENGINE, SecurityConstants.SECURITY_ENGINE_JOSE);
		secProperties.put(SecurityConstants.ACTION, SecurityConstants.SIGNATURE_ACTION);
		secProperties.put(SecurityConstants.SIGNATURE_MODE, SecurityConstants.SIGNATURE_MODE_COMPACT);
		secProperties.put(SecurityConstants.SIGNATURE_DETACHED, SecurityConstants.SIGNATURE_DETACHED_FALSE);
		secProperties.put(SecurityConstants.JOSE_USE_HEADERS, SecurityConstants.JOSE_USE_HEADERS_TRUE);
		secProperties.put(SecurityConstants.JOSE_CONTENT_TYPE, SecurityConstants.JOSE_CONTENT_TYPE_FALSE);
		secProperties.put(SecurityConstants.JOSE_TYPE, org.apache.cxf.rs.security.jose.common.JoseConstants.TYPE_JWT);
		
		
		// algoritmo
		String algorithm = null;
		if(tokenAudit && apiSoap) {
			algorithm = securityConfig.getAlgorithmConvertForREST();
		}
		else {
			algorithm = securityConfig.getAlgorithm();
		}
		secProperties.put(SecurityConstants.SIGNATURE_ALGORITHM, algorithm);
		modiTokenClaims.setAlgorithm(algorithm);
		
		
		// spedizione certificato
		if(integritaKid || useKIDtokenHeader) {
			if(kid==null) {
				// utilizzo l'alias
				kid = keystoreConfig.getSecurityMessageKeyAlias();
				if(kid==null) {
					throw new ProtocolException("Il pattern configurato richiede la definizione di un KID");
				}
			}
			secProperties.put(SecurityConstants.JOSE_KID_CUSTOM, kid); // kid
			modiTokenClaims.setKid(kid);
		}
		else {
			boolean addKid = tokenAudit ? this.modiProperties.isSecurityTokenAuditX509AddKid() : this.modiProperties.isSecurityTokenX509AddKid();
			if(addKid) {
				secProperties.put(SecurityConstants.JOSE_KID, SecurityConstants.JOSE_KID_TRUE); // kid
			}
			modiTokenClaims.setAddKid(addKid);
			
			boolean x5c = tokenAudit && apiSoap ? this.modiProperties.isSecurityTokenAuditApiSoapX509RiferimentoX5c() : securityConfig.isX5c();
			if(x5c) {
				secProperties.put(SecurityConstants.JOSE_INCLUDE_CERT, SecurityConstants.JOSE_INCLUDE_CERT_TRUE);
				boolean x5cUseSingleCertificate = tokenAudit && apiSoap ? this.modiProperties.isSecurityTokenAuditApiSoapX509RiferimentoX5cSingleCertificate() : securityConfig.isUseSingleCertificate();
				if(!x5cUseSingleCertificate) {
					secProperties.put(SecurityConstants.JOSE_INCLUDE_CERT_CHAIN, SecurityConstants.JOSE_INCLUDE_CERT_CHAIN_TRUE);
				}
				modiTokenClaims.setAddX5cChain(!x5cUseSingleCertificate);
			}
			modiTokenClaims.setAddX5c(x5c);
			
			boolean x5t = tokenAudit && apiSoap ? this.modiProperties.isSecurityTokenAuditApiSoapX509RiferimentoX5t() : securityConfig.isX5t();
			if(x5t) {
				secProperties.put(SecurityConstants.JOSE_INCLUDE_CERT_SHA, SecurityConstants.JOSE_INCLUDE_CERT_SHA_256);
			}
			modiTokenClaims.setAddX5t(x5t);
			
			boolean x5u = tokenAudit && apiSoap ? this.modiProperties.isSecurityTokenAuditApiSoapX509RiferimentoX5u() : securityConfig.isX5u();
			if(x5u) {
				String url = securityConfig.getX5url();
				secProperties.put(SecurityConstants.JOSE_X509_URL, url);
				modiTokenClaims.setX5uUrl(url);
			}
			modiTokenClaims.setAddX5u(x5u);
		}
		
		SignatureBean signatureBean = new SignatureBean();
		org.openspcoop2.utils.certificate.KeyStore ks = null;
		JWKSet jwkSet = null;
		String keyAlias = keystoreConfig.getSecurityMessageKeyAlias();
		if(keystoreConfig.getSecurityMessageKeystorePath()!=null || keystoreConfig.isSecurityMessageKeystoreHSM()) {
			if(CostantiDB.KEYSTORE_TYPE_JWK.equalsIgnoreCase(keystoreConfig.getSecurityMessageKeystoreType())) {
				try {
					JWKSetStore jwtStore = GestoreKeystoreCache.getJwkSetStore(requestInfo, keystoreConfig.getSecurityMessageKeystorePath());
					if(jwtStore==null) {
						throw newProtocolExceptionAccessoKeystoreNonRiuscito(keystoreConfig.getSecurityMessageKeystoreType(),keystoreConfig.getSecurityMessageKeystorePath());
					}
					jwkSet = jwtStore.getJwkSet();
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
			}
			else if(CostantiDB.KEYSTORE_TYPE_KEY_PAIR.equalsIgnoreCase(keystoreConfig.getSecurityMessageKeystoreType())) {
				try {
					KeyPairStore keyPairStore = GestoreKeystoreCache.getKeyPairStore(requestInfo, keystoreConfig.getSecurityMessageKeystorePath(), keystoreConfig.getSecurityMessageKeystorePathPublicKey(), 
							keystoreConfig.getSecurityMessageKeyPassword(), keystoreConfig.getSecurityMessageKeystoreKeyAlgorithm());
					if(keyPairStore==null) {
						throw newProtocolExceptionAccessoKeystoreNonRiuscito(keystoreConfig.getSecurityMessageKeystoreType(),keystoreConfig.getSecurityMessageKeystorePath());
					}
					jwkSet = keyPairStore.getJwkSet();
					keyAlias = keyPairStore.getJwkSetKid();
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
			}
			else {
				try {
					MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(requestInfo,
							keystoreConfig.getSecurityMessageKeystorePath(), keystoreConfig.getSecurityMessageKeystoreType(), 
							keystoreConfig.getSecurityMessageKeystorePassword());
					if(merlinKs==null) {
						throw newProtocolExceptionAccessoKeystoreNonRiuscito(keystoreConfig.getSecurityMessageKeystoreType(),keystoreConfig.getSecurityMessageKeystorePath());
					}
					ks = merlinKs.getKeyStore();
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
			}
		}
		else {
			try {
				MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(requestInfo,
						keystoreConfig.getSecurityMessageKeystoreArchive(), keystoreConfig.getSecurityMessageKeystoreType(), 
						keystoreConfig.getSecurityMessageKeystorePassword());
				if(merlinKs==null) {
					throw new ProtocolException("Accesso al keystore non riuscito");
				}
				ks = merlinKs.getKeyStore();
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		signatureBean.setKeystore(ks);
		signatureBean.setJwkSet(jwkSet);
		signatureBean.setUser(keyAlias);
		signatureBean.setPassword(keystoreConfig.getSecurityMessageKeyPassword());
		messageSecurityContext.setSignatureBean(signatureBean);
		
		
		
		
		// Aggiungo a traccia informazioni sul certificato utilizzato
		X509Certificate x509 = null;
		if(ks!=null) {
			Certificate certificate = null;
			try {
				certificate = ks.getCertificate(keystoreConfig.getSecurityMessageKeyAlias());
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			if(certificate instanceof X509Certificate) {
				x509 = (X509Certificate) certificate;
				busta.addProperty(tokenAudit ? ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_X509_SUBJECT : ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_X509_SUBJECT, 
						x509.getSubjectX500Principal().toString());
				if(x509.getIssuerX500Principal()!=null) {
					busta.addProperty(tokenAudit ? ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_X509_ISSUER : ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_X509_ISSUER, 
							x509.getIssuerX500Principal().toString());
				}
				if(cacheble) {
					try {
						String pem = CertificateUtils.toPEM(x509);
						modiTokenClaims.setPem(pem);
					}catch(Exception e) {
						throw new ProtocolException("X509 to PEM serialization failed: "+e.getMessage(),e);
					}
				}
			}
		}
		if(jwkSet!=null) {
			busta.addProperty(tokenAudit ? ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_KID : ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_KID,
					kid);
			if(cacheble) {
				try {
					modiTokenClaims.setJwk(jwkSet.getJson());
				}catch(Exception e) {
					throw new ProtocolException("JWK to Json serialization failed: "+e.getMessage(),e);
				}
			}
		}
		
		
		
		
		
		
		// Se cachable verifico che non esiste già in cache
		boolean prelevatoDallaCache = false;
		String funzioneCache = "ModI-"+(tokenAudit?"Audit":"Authorization");
		String keyCache = null;
		Date nowCacheDate = null;
		if(cacheble) {
			
			keyCache = modiTokenClaims.toCacheKey();
			
			ModIJWTToken moditokenFromCache = null;
			try {
				nowCacheDate = new Date(nowMs);
				moditokenFromCache = (ModIJWTToken) GestoreToken.getTokenCacheItem(keyCache, funzioneCache, nowCacheDate);
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			if(moditokenFromCache!=null) {
				
				moditokenFromCache.getClaims().setInfoNonCachableInBusta(busta);
				
				modiToken = moditokenFromCache;
				
				prelevatoDallaCache = true;
			}
			
		}
		
		
		if(!prelevatoDallaCache) {

			// Produco payload da firmare
			OpenSPCoop2Message payload = null;
			try {
				String jsonPayload = jsonUtils.toString(payloadToken);
				payload = msg.getFactory().createMessage(MessageType.JSON, MessageRole.NONE, 
					HttpConstants.CONTENT_TYPE_JSON, jsonPayload.getBytes(), null, null).getMessage_throwParseException();
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		
		
			// firma
			try {
				messageSecurityContext.setOutgoingProperties(secProperties, false);
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			try {
				joseSignature.process(messageSecurityContext, payload, context);
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		
			/*
			 * == return token ==
			 */
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				payload.writeTo(bout, true);
				bout.flush();
				bout.close();
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			
			String token = bout.toString();
			
			modiToken.setToken(token);
			
			
			
			
			if(audit02) {
				
				// digest per la negoziazione del token
				try {
				
					String digestAlgorithm = this.modiProperties.getSecurityTokenAuditDigestAlgorithm();
					String digestAuditValue = org.openspcoop2.utils.digest.DigestUtils.getDigestValue(token.getBytes(), digestAlgorithm, DigestEncoding.HEX, 
							false); // se rfc3230 true aggiunge prefisso algoritmo=
					modiToken.setDigestAlgorithm(digestAlgorithm);
					modiToken.setDigest(digestAuditValue);
										
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
				
			}
			
			
			
			
			if(cacheble) {
				GestoreToken.putTokenCacheItem(modiToken, keyCache, funzioneCache, nowCacheDate); 
			}
		}
		
		
		
		String securityTokenHeader = headerTokenRest;
		if(HttpConstants.AUTHORIZATION.equalsIgnoreCase(securityTokenHeader)) {
			msg.forceTransportHeader(securityTokenHeader, HttpConstants.AUTHORIZATION_PREFIX_BEARER+modiToken.getToken());
		}
		else {
			msg.forceTransportHeader(securityTokenHeader, modiToken.getToken());
		}
		

		if(audit02) {
			
			// inserisco digest per la negoziazione del token
			try {
			
				context.put(ModICostanti.MODIPA_CONTEXT_AUDIT_DIGEST, modiToken.getDigest());
				context.put(ModICostanti.MODIPA_CONTEXT_AUDIT_DIGEST_ALGO, modiToken.getDigestAlgorithm());
				
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			
		}
		
		
		
		if(request && context!=null) {
			
			SecurityToken securityTokenForContext = ModIUtilities.newSecurityToken(context);
			
			RestMessageSecurityToken restSecurityToken = new RestMessageSecurityToken();
			
			if(x509!=null) {
				restSecurityToken.setCertificate(new CertificateInfo(x509, securityTokenHeader));
			}
			if(kid!=null) {
				restSecurityToken.setKid(kid);
			}
			restSecurityToken.setToken(modiToken.getToken());
			restSecurityToken.setHttpHeaderName(securityTokenHeader);
			if(tokenAudit) {
				securityTokenForContext.setAudit(restSecurityToken);
			}
			else if(headerDuplicati) {
				if(headerAuthentication) {
					securityTokenForContext.setAuthorization(restSecurityToken);
				}
				else {
					securityTokenForContext.setIntegrity(restSecurityToken);	
				}
			}
			else {
				if(HttpConstants.AUTHORIZATION.equalsIgnoreCase(securityTokenHeader)) {
					securityTokenForContext.setAuthorization(restSecurityToken);
				}
				else {
					securityTokenForContext.setIntegrity(restSecurityToken);
				}
			}
			
		}
		
		
		
		
		return modiToken;
	}
	
	public static List<String> getHeaderValues(RuoloMessaggio ruoloMessaggio, OpenSPCoop2Message msg, Map<String, List<String>> mapForceTransportHeaders, String httpHeader){
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
		
		return values;
	}
	
	private void addCorniceSicurezzaSchema(ModISecurityConfig securityConfig,
			Map<String, Object> dynamicMap, Context context,
			ObjectNode payloadToken, Busta busta,
			String corniceSicurezzaSchema,
			ModIJWTTokenClaims modiTokenClaims) throws ProtocolException {
		if(corniceSicurezzaSchema!=null && StringUtils.isNotEmpty(corniceSicurezzaSchema)) {
			addCorniceSicurezzaSchema(
					dynamicMap, context,
					payloadToken, busta,
					securityConfig.getCorniceSicurezzaSchemaConfig()!=null ? securityConfig.getCorniceSicurezzaSchemaConfig().getClaims() : null,
					modiTokenClaims);
		}
	}
	private void addCorniceSicurezzaSchema(
			Map<String, Object> dynamicMap, Context context,
			ObjectNode payloadToken, Busta busta,
			List<ModIAuditClaimConfig> schemas,
			ModIJWTTokenClaims modiTokenClaims) throws ProtocolException {
		if(schemas!=null && !schemas.isEmpty()) {
			for (ModIAuditClaimConfig modIAuditClaimConfig : schemas) {
				addCorniceSicurezzaSchemaRule(dynamicMap, context,
						payloadToken, busta,
						modIAuditClaimConfig,
						modiTokenClaims);
			}
		}
	}
	private void addCorniceSicurezzaSchemaRule(Map<String, Object> dynamicMap, Context context,
			ObjectNode payloadToken, Busta busta,
			ModIAuditClaimConfig modIAuditClaimConfig,
			ModIJWTTokenClaims modiTokenClaims) throws ProtocolException {
		String claimName = modIAuditClaimConfig.getNome();
		String valore = null;
		try {
			List<String> claimRules = modIAuditClaimConfig.getRules();
			if(claimRules==null || claimRules.isEmpty()) {
				throw new ProtocolException("Rules not available for claim '"+claimName+"'");
			}
			
			valore = addCorniceSicurezzaSchemaPayload(dynamicMap, context,
					payloadToken, 
					modIAuditClaimConfig, claimName, claimRules,
					modiTokenClaims);
			
			if(modIAuditClaimConfig.isTrace()) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_PREFIX+claimName, valore);
			}
			
		}catch(Exception e) {
			if(modIAuditClaimConfig.isRequired()) {
				this.log.error(e.getMessage(),e);
				ProtocolException pe = new ProtocolException(e.getMessage());
				pe.setInteroperabilityError(true);
				throw pe;
			}
			else {
				this.log.debug(e.getMessage(),e);
			}
		}
	}
	private String addCorniceSicurezzaSchemaPayload(Map<String, Object> dynamicMap, Context context,
			ObjectNode payloadToken, 
			ModIAuditClaimConfig modIAuditClaimConfig, String claimName, List<String> rules,
			ModIJWTTokenClaims modiTokenClaims) throws ProtocolException {
		String valore = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL+" - "+modIAuditClaimConfig.getLabel(), 
				rules, dynamicMap, context);
		if(modIAuditClaimConfig.isStringType()) {
			payloadToken.put(claimName, valore);
		}
		else {
			if("true".equals(valore)) {
				payloadToken.put(claimName, true);
			}
			else if("false".equals(valore)) {
				payloadToken.put(claimName, false);
			}
			else {
				long l = Long.parseLong(valore);
				payloadToken.put(claimName, l);
			}
		}
		modiTokenClaims.addCorniceSicurezzaAudit(claimName, valore);
		return valore;
	}
	
	private boolean addCorniceSicurezzaLegacyCodiceEnte(ModISecurityConfig securityConfig,
			Map<String, Object> dynamicMap, Context context,
			ObjectNode payloadToken, Busta busta,
			ModIJWTTokenClaims modiTokenClaims) throws ProtocolException {
		
		boolean addIss = true;
		
		String claimNameCodiceEnte = this.modiProperties.getSicurezzaMessaggioCorniceSicurezzaRestCodiceEnte();
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
		modiTokenClaims.addCorniceSicurezzaAudit(claimNameCodiceEnte, codiceEnte);
		
		return addIss;
		
	}
	
	private boolean addCorniceSicurezzaLegacyUser(ModISecurityConfig securityConfig,
			Map<String, Object> dynamicMap, Context context,
			ObjectNode payloadToken, Busta busta,
			ModIJWTTokenClaims modiTokenClaims) throws ProtocolException {
		
		boolean addSub = true;
		
		String claimNameUser = this.modiProperties.getSicurezzaMessaggioCorniceSicurezzaRestUser();
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
		modiTokenClaims.addCorniceSicurezzaAudit(claimNameUser, utente);
		
		return addSub;
		
	}
		
	private void addCorniceSicurezzaLegacyIpUser(ModISecurityConfig securityConfig,
			Map<String, Object> dynamicMap, Context context,
			ObjectNode payloadToken, Busta busta,
			ModIJWTTokenClaims modiTokenClaims) throws ProtocolException {
	
		String claimNameIpUser = this.modiProperties.getSicurezzaMessaggioCorniceSicurezzaRestIpuser();
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
		modiTokenClaims.addCorniceSicurezzaAudit(claimNameIpUser, indirizzoIpPostazione);
	}
}
