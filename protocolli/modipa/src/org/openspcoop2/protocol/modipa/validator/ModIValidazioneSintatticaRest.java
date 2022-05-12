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

import java.io.ByteArrayOutputStream;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.pdd.core.token.parser.TokenUtils;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModISecurityConfig;
import org.openspcoop2.protocol.modipa.utils.ModITruststoreConfig;
import org.openspcoop2.protocol.modipa.utils.ModIUtilities;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.RestMessageSecurityToken;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ValidazioneUtils;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContextParameters;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.engine.MessageSecurityContext_impl;
import org.openspcoop2.security.message.jose.MessageSecurityReceiver_jose;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * ModIValidazioneSintatticaRest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIValidazioneSintatticaRest extends AbstractModIValidazioneSintatticaCommons{

	public ModIValidazioneSintatticaRest(Logger log, IState state, Context context, ModIProperties modiProperties, ValidazioneUtils validazioneUtils) {
		super(log, state, context, modiProperties, validazioneUtils);
	}
	
	
	public void validateSyncInteractionProfile(OpenSPCoop2Message msg, boolean request,
			List<Eccezione> erroriValidazione) throws Exception {
		
		if(!request) {
			
			String returnCode = null;
			int returnCodeInt = -1;
			if(!request && msg.getTransportResponseContext()!=null) {
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
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.PROFILO_TRASMISSIONE, 
							"HTTP Status '"+returnCodeInt+"' differente da quello atteso per il profilo bloccante (atteso: "+sb.toString()+")"));
					return;
				}
			}
		}
		
	}
	
	public void validateAsyncInteractionProfile(OpenSPCoop2Message msg, boolean request, String asyncInteractionType, String asyncInteractionRole, 
			AccordoServizioParteComune apiContenenteRisorsa, String azione,
			Busta busta, List<Eccezione> erroriValidazione,
			String replyTo) throws Exception {
				
		String correlationIdHeader = this.modiProperties.getRestCorrelationIdHeader();
		String correlationId = null;
		if(msg!=null) {
			if(request && msg.getTransportRequestContext()!=null) {
				correlationId = msg.getTransportRequestContext().getHeaderFirstValue(correlationIdHeader);
			}
			else if(!request && msg.getTransportResponseContext()!=null) {
				correlationId = msg.getTransportResponseContext().getHeaderFirstValue(correlationIdHeader);
			}
		}
		
		String replyToHeader = this.modiProperties.getRestReplyToHeader();
		String replyToAddress = null;
		if(msg!=null) {
			if(request && msg.getTransportRequestContext()!=null) {
				replyToAddress = msg.getTransportRequestContext().getHeaderFirstValue(replyToHeader);
			}
			else if(!request && msg.getTransportResponseContext()!=null) {
				replyToAddress = msg.getTransportResponseContext().getHeaderFirstValue(replyToHeader);
			}
		}
		
		String locationHeader = this.modiProperties.getRestLocationHeader();
		String location = null;
		if(msg!=null) {
			if(request && msg.getTransportRequestContext()!=null) {
				location = msg.getTransportRequestContext().getHeaderFirstValue(locationHeader);
			}
			else if(!request && msg.getTransportResponseContext()!=null) {
				location = msg.getTransportResponseContext().getHeaderFirstValue(locationHeader);
			}
		}
		
		if(replyToAddress!=null) {
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_REPLY_TO, replyToAddress);
		}
		if(correlationId!=null) {
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE, correlationId);
			if(correlationId.length()<=255) {
				busta.setCollaborazione(correlationId);
			}
		}
		
		String returnCode = null;
		int returnCodeInt = -1;
		if(!request && msg.getTransportResponseContext()!=null) {
			returnCode = msg.getTransportResponseContext().getCodiceTrasporto();
			if(returnCode!=null) {
				try {
					returnCodeInt = Integer.valueOf(returnCode);
				}catch(Exception e) {}
			}
		}
		
		// Fix: il controllo deve essere fatto solo per i codici che non rientrano in 4xx e 5xx
		boolean is_4xx_5xx = false;
		if(!request) {
			is_4xx_5xx = (returnCodeInt>=400) && (returnCodeInt<=599);
		}
		
		Integer [] returnCodeAttesi = null;
		
		if(!is_4xx_5xx && asyncInteractionType!=null) {
			if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionType)) {
				if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
					if(request) {
						if(replyToAddress==null || "".equals(replyToAddress)) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SERVIZIO_CORRELATO_NON_PRESENTE, 
									"Header HTTP '"+replyToHeader+"' non presente"));
							return;
						}
						if(this.modiProperties.isRestSecurityTokenPushReplyToUpdateInErogazione()) {
							if(msg.getTransportRequestContext()!=null) {
								msg.getTransportRequestContext().removeHeader(replyToHeader); // rimuovo se già esiste
							}
							msg.forceTransportHeader(replyToHeader, replyTo);
						}
					}
					else {
						if(correlationId==null || "".equals(correlationId)) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.COLLABORAZIONE_NON_PRESENTE, 
									"Header HTTP '"+correlationIdHeader+"' non presente"));
							return;
						}
						returnCodeAttesi = this.modiProperties.getRestNonBloccantePushRequestHttpStatus();
					}
				}
				else {
					if(request) {
						if(correlationId==null || "".equals(correlationId)) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.COLLABORAZIONE_NON_PRESENTE, 
									"Header HTTP '"+correlationIdHeader+"' non presente"));
							return;
						}
					}
					else {
						returnCodeAttesi = this.modiProperties.getRestNonBloccantePushResponseHttpStatus();
					}
				}
			}
			else {
				// pull
				if(request) {
					
					// Flusso di richiesta
					
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
				else {
					
					// Flusso di risposta
					
					if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
						if(location==null || "".equals(location)) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SERVIZIO_CORRELATO_NON_PRESENTE, 
									"Header HTTP '"+locationHeader+"' non presente"));
							return;
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
								erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SERVIZIO_CORRELATO_NON_PRESENTE, 
										"Header HTTP '"+locationHeader+"' non presente"));
								return;
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
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.PROFILO_TRASMISSIONE, 
						"HTTP Status '"+returnCodeInt+"' differente da quello atteso per il profilo non bloccante '"+asyncInteractionType+"' con ruolo '"+asyncInteractionRole+"' (atteso: "+sb.toString()+")"));
				return;
			}
		}
	}
	
	public String validateSecurityProfile(OpenSPCoop2Message msg, boolean request, String securityMessageProfile, String headerTokenRest, boolean corniceSicurezza, boolean includiRequestDigest, 
			Busta busta, List<Eccezione> erroriValidazione,
			ModITruststoreConfig trustStoreCertificati, ModITruststoreConfig trustStoreSsl, ModISecurityConfig securityConfig,
			boolean buildSecurityTokenInRequest, boolean headerDuplicati, boolean securityHeaderObbligatorio,
			Map<String, Object> dynamicMapParameter, Busta datiRichiesta) throws Exception {
		
		boolean bufferMessage_readOnly = this.modiProperties.isReadByPathBufferEnabled();
		String idTransazione = null;
		if(this.context!=null) {
			idTransazione = (String)this.context.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		}
		
		String securityTokenHeader = headerTokenRest;
		List<String> securityTokens = null;
		if(msg!=null) {
			if(request && msg.getTransportRequestContext()!=null) {
				securityTokens = msg.getTransportRequestContext().getHeaderValues(securityTokenHeader);
			}
			else if(!request && msg.getTransportResponseContext()!=null) {
				securityTokens = msg.getTransportResponseContext().getHeaderValues(securityTokenHeader);
			}
		}
		String securityToken = null;
		if(securityTokens!=null && !securityTokens.isEmpty()) {
			securityToken = securityTokens.get(0);
		}
		
		
		boolean attesoSecurityHeader = securityHeaderObbligatorio;
		if(!request) {
			if(msg.isFault()) {
				attesoSecurityHeader = false;
			}
		}
		
		if(securityToken==null || "".equals(securityToken)) {
			if(attesoSecurityHeader) {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_PRESENTE, 
						"Header HTTP '"+securityTokenHeader+"' non presente"));
			}
			return null;
		}
		if(securityTokens.size()>1) {
			if(attesoSecurityHeader) {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_TOKEN_PRESENTE_PIU_VOLTE, 
						"Header HTTP '"+securityTokenHeader+"' presente più volte"));
			}
			return null;
		}
		
		String token = securityToken;
		if(HttpConstants.AUTHORIZATION.equalsIgnoreCase(securityTokenHeader)) {
			if(request) {
				if(msg.getTransportRequestContext()==null || msg.getTransportRequestContext().getCredential()==null) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_PRESENTE, 
							"Header '"+HttpConstants.AUTHORIZATION+"' non presente"));
					return null;
				}
				if(msg.getTransportRequestContext().getCredential().getBearerToken()==null) {
					if(msg.getTransportRequestContext().getCredential().getUsername()!=null) {
						erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_PRESENTE, 
							"Header '"+HttpConstants.AUTHORIZATION+"' non presente con prefisso '"+HttpConstants.AUTHORIZATION_PREFIX_BEARER+"'"));
						return null;
					}
					else {
						erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_PRESENTE, 
								"Header '"+HttpConstants.AUTHORIZATION+"' non presente"));
						return null;
					}
				}
				token = msg.getTransportRequestContext().getCredential().getBearerToken();
			}
			else {
				if(token.toLowerCase().startsWith(HttpConstants.AUTHORIZATION_PREFIX_BEARER.toLowerCase())){
					token = token.substring(HttpConstants.AUTHORIZATION_PREFIX_BEARER.length());
				}
				else {
					if(token.toLowerCase().startsWith(HttpConstants.AUTHORIZATION_PREFIX_BASIC.toLowerCase())){
						if(attesoSecurityHeader) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_PRESENTE, 
									"Header '"+HttpConstants.AUTHORIZATION+"' non presente con prefisso '"+HttpConstants.AUTHORIZATION_PREFIX_BEARER+"'"));
						}
						return null;
					}
					else {
						if(attesoSecurityHeader) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_PRESENTE, 
									"Header '"+HttpConstants.AUTHORIZATION+"' non presente"));
						}
						return null;
					}
				}
			}
		}
		
		

		String prefix = "";
		if(headerDuplicati) {
			prefix = "[Header '"+headerTokenRest+"'] ";
		}
		
		/*
		 * == signature ==
		 */
		
		OpenSPCoop2Message msgToken = msg.getFactory().createMessage(MessageType.JSON, MessageRole.NONE, 
				HttpConstants.CONTENT_TYPE_JSON, token.getBytes(), null, null).getMessage_throwParseException();
		String payloadToken = null;
		X509Certificate x509 = null;
		try {
		
			//  ** Timestamp **
			Long timeToLive = this.modiProperties.getRestSecurityTokenClaimsIatTimeCheck_milliseconds();
			if(securityConfig.getCheckTtlIatMilliseconds()!=null) {
				timeToLive = securityConfig.getCheckTtlIatMilliseconds();
			}
			if(timeToLive!=null && msg!=null) {
				msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_IAT_TTL_CHECK, timeToLive);
			}
			
			MessageSecurityReceiver_jose joseSignature = new MessageSecurityReceiver_jose();
			MessageSecurityContextParameters messageSecurityContextParameters = new MessageSecurityContextParameters();
			MessageSecurityContext messageSecurityContext = new MessageSecurityContext_impl(messageSecurityContextParameters);
			Map<String,Object> secProperties = new HashMap<>();
			secProperties.put(SecurityConstants.SECURITY_ENGINE, SecurityConstants.SECURITY_ENGINE_JOSE);
			secProperties.put(SecurityConstants.ACTION, SecurityConstants.SIGNATURE_ACTION);
			secProperties.put(SecurityConstants.SIGNATURE_MODE, SecurityConstants.SIGNATURE_MODE_COMPACT);
			secProperties.put(SecurityConstants.SIGNATURE_DETACHED, SecurityConstants.SIGNATURE_DETACHED_FALSE);
			secProperties.put(SecurityConstants.DETACH_SECURITY_INFO, SecurityConstants.TRUE);
			secProperties.put(SecurityConstants.JOSE_USE_HEADERS, SecurityConstants.JOSE_USE_HEADERS_TRUE);
			secProperties.put(SecurityConstants.JOSE_USE_HEADERS_JWK, SecurityConstants.JOSE_USE_HEADERS_FALSE);
			secProperties.put(SecurityConstants.JOSE_USE_HEADERS_JKU, SecurityConstants.JOSE_USE_HEADERS_FALSE);
			secProperties.put(SecurityConstants.JOSE_USE_HEADERS_KID, SecurityConstants.JOSE_USE_HEADERS_FALSE);
			secProperties.put(SecurityConstants.JOSE_USE_HEADERS_X5T, SecurityConstants.JOSE_USE_HEADERS_FALSE);
			secProperties.put(SecurityConstants.JOSE_USE_HEADERS_X5C, securityConfig.isX5c() ? SecurityConstants.JOSE_USE_HEADERS_TRUE: SecurityConstants.JOSE_USE_HEADERS_FALSE);
			secProperties.put(SecurityConstants.JOSE_USE_HEADERS_X5U, securityConfig.isX5u() ? SecurityConstants.JOSE_USE_HEADERS_TRUE: SecurityConstants.JOSE_USE_HEADERS_FALSE);
			secProperties.put(SecurityConstants.JOSE_USE_HEADERS_X5T_256, securityConfig.isX5t() ? SecurityConstants.JOSE_USE_HEADERS_TRUE: SecurityConstants.JOSE_USE_HEADERS_FALSE);
			secProperties.put(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_FILE, trustStoreCertificati.getSecurityMessageTruststorePath());
			secProperties.put(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_TYPE, trustStoreCertificati.getSecurityMessageTruststoreType());
			secProperties.put(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_PASSWORD, trustStoreCertificati.getSecurityMessageTruststorePassword());
			if(trustStoreCertificati.getSecurityMessageTruststoreCRLs()!=null) {
				secProperties.put(SecurityConstants.SIGNATURE_CRL, trustStoreCertificati.getSecurityMessageTruststoreCRLs());
			}
			if(securityConfig.isX5u()) {
				if(trustStoreSsl!=null && trustStoreSsl.getSecurityMessageTruststorePath()!=null) {
					secProperties.put(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_FILE, trustStoreSsl.getSecurityMessageTruststorePath());
					secProperties.put(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_TYPE, trustStoreSsl.getSecurityMessageTruststoreType());
					secProperties.put(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_PASSWORD, trustStoreSsl.getSecurityMessageTruststorePassword());
					if(trustStoreSsl.getSecurityMessageTruststoreCRLs()!=null) {
						secProperties.put(SecurityConstants.JOSE_USE_HEADERS_TRUSTSTORE_SSL_CRL, trustStoreSsl.getSecurityMessageTruststoreCRLs());
					}
				}
			}
			messageSecurityContext.setIncomingProperties(secProperties, false);
			joseSignature.process(messageSecurityContext, msgToken, busta);
			joseSignature.detachSecurity(messageSecurityContext, msgToken.castAsRest());
			
			ModIRESTSecurity restSecurity = (ModIRESTSecurity) msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_SBUSTAMENTO_REST);
			if(restSecurity==null) {
				restSecurity = new ModIRESTSecurity(securityTokenHeader, request);
				msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_SBUSTAMENTO_REST, restSecurity);
			}
			else {
				restSecurity.getTokenHeaderNames().add(securityTokenHeader);
			}

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			msgToken.writeTo(bout, true);
			bout.flush();
			bout.close();
			
			payloadToken = bout.toString();
			//System.out.println("PAYLOAD TOKEN ["+payloadToken+"]");	
			
			x509 = joseSignature.getX509Certificate();
			//System.out.println("CERTIFICATE ["+x509.getSubjectX500Principal().toString()+"]");	
			
		}catch(Exception e) {
			this.log.error("Errore durante la validazione del token di sicurezza: "+e.getMessage(),e);
			erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA, 
					prefix+e.getMessage(),e));
			return token;
		}
		
		
		if(x509==null || x509.getSubjectX500Principal()==null) {
			erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
					request ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_PRESENTE :
						CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_PRESENTE, 
					prefix+"Certificato X509 mittente non presente"));
		}
		else {
			
			if(!headerDuplicati || HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_X509_SUBJECT, x509.getSubjectX500Principal().toString());
				if(x509.getIssuerX500Principal()!=null) {
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_X509_ISSUER, x509.getIssuerX500Principal().toString());
				}
				
				if(headerDuplicati) {
					this.context.addObject(ModICostanti.MODIPA_CONTEXT_X509_AUTHORIZATION, x509);
				}
			}
			else {
				/*
				String subjectAuthorization = busta.getProperty(busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_X509_SUBJECT));
				String issuerAuthorization = busta.getProperty(busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_X509_ISSUER));
				String subjectIntegrity = x509.getSubjectX500Principal().toString();
				String issuerIntegrity = x509.getIssuerX500Principal().toString();
				boolean subjectValid = false;
				if(subjectIntegrity!=null && subjectAuthorization!=null) {
					subjectValid = CertificateUtils.sslVerify(subjectAuthorization, subjectIntegrity, PrincipalType.subject, this.log);
				}
				boolean issuerValid = true;
				if(issuerAuthorization!=null && !"".equals(issuerAuthorization)) {
					if(issuerIntegrity==null) {
						issuerValid = false;
					}
					else {
						issuerValid = CertificateUtils.sslVerify(issuerAuthorization, issuerIntegrity, PrincipalType.issuer, this.log);
					}
				}
				else {
					issuerValid = (issuerIntegrity == null);
				}
				
				if(!subjectValid || !issuerValid){
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
							request ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_VALIDO :
								CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_VALIDO, 
							"I token '"+HttpConstants.AUTHORIZATION+"' e '"+headerTokenRest+"' risultano firmati da certificati differenti"));
				}*/
				X509Certificate x509Authorization = null;
				Object oX509Authorization = this.context.removeObject(ModICostanti.MODIPA_CONTEXT_X509_AUTHORIZATION);
				if(oX509Authorization!=null && oX509Authorization instanceof X509Certificate) {
					x509Authorization = (X509Certificate) oX509Authorization;
				}
				if(x509Authorization!=null) { 
					if(!x509Authorization.equals(x509)) {
						StringBuilder sb = new StringBuilder();
						sb.append(HttpConstants.AUTHORIZATION).append(" ");
						sb.append("subject=\"").append(x509Authorization.getSubjectX500Principal().toString()).append("\"");
						if(x509Authorization.getIssuerX500Principal()!=null) {
							sb.append(" issuer=\"").append(x509Authorization.getIssuerX500Principal().toString()).append("\"");
						}
						sb.append("\n");
						sb.append(headerTokenRest).append(" ");
						sb.append("subject=\"").append(x509.getSubjectX500Principal().toString()).append("\"");
						if(x509.getIssuerX500Principal()!=null) {
							sb.append(" issuer=\"").append(x509.getIssuerX500Principal().toString()).append("\"");
						}
						erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
								request ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_VALIDO :
									CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_VALIDO, 
								"I token '"+HttpConstants.AUTHORIZATION+"' e '"+headerTokenRest+"' risultano firmati da certificati differenti\n"+sb.toString()));
					}
				}
				else {
					if(erroriValidazione.isEmpty()) {
						erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_PRESENTE, 
								"Certificato x509 nell'header '"+HttpConstants.AUTHORIZATION+"' non presente"));
					}
				}
			}
			
			if(request) {
				if(!headerDuplicati || HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
					identificazioneApplicativoMittente(x509,busta);
				}
			}
		}
		
		if(request && this.context!=null) {
			
			SecurityToken securityTokenForContext = ModIUtilities.newSecurityToken(this.context);
			
			RestMessageSecurityToken restSecurityToken = new RestMessageSecurityToken();
			restSecurityToken.setCertificate(new CertificateInfo(x509, securityTokenHeader));
			restSecurityToken.setToken(token);		
			if(HttpConstants.AUTHORIZATION.equalsIgnoreCase(securityTokenHeader)) {
				securityTokenForContext.setAuthorization(restSecurityToken);
			}
			else {
				securityTokenForContext.setIntegrity(restSecurityToken);	
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
		
		
		try {
			JsonNode jsonNode = JSONUtils.getInstance().getAsNode(payloadToken);
			if(jsonNode==null || !(jsonNode instanceof ObjectNode)) {
				throw new Exception("Payload del token possiede una struttura non valida");
			}
			ObjectNode objectNode = (ObjectNode) jsonNode;
			
			if(objectNode.has(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT)) {
				Object iat = objectNode.get(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT);
				if(iat==null) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_PRESENTE, 
							prefix+"Token senza claim '"+Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT+"'"));
				}
				Date iatDate = null;
				try {
					iatDate = toDate(iat);
				}catch(Exception e) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALIDA, 
							prefix+"Token con claim '"+Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT+"' non valido: "+e.getMessage(),e));
				}
				String iatValue = DateUtils.getSimpleDateFormatMs().format(iatDate);
				if(!headerDuplicati || HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_IAT, iatValue);
				}
				else {
					String iatAuthorization = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_IAT);
					if(!iatValue.equals(iatAuthorization)) {
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_IAT, iatValue);
					}
				}
			}
			else {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_PRESENTE, 
						prefix+"Token senza claim '"+Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT+"'"));
			}
			
			if(objectNode.has(Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED)) {
				Object exp = objectNode.get(Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED);
				if(exp==null) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SCADENZA_NON_PRESENTE, 
							prefix+"Token senza claim '"+Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED+"'"));
				}
				Date expDate = null;
				try {
					expDate = toDate(exp);
				}catch(Exception e) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SCADENZA_NON_VALIDA, 
							prefix+"Token con claim '"+Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED+"' non valido: "+e.getMessage(),e));
				}
				String expValue = DateUtils.getSimpleDateFormatMs().format(expDate);
				if(!headerDuplicati || HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_EXP, expValue);
				}
				else {
					String expAuthorization = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_EXP);
					if(!expValue.equals(expAuthorization)) {
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_EXP, expValue);
					}
				}
			}
			else {
				erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SCADENZA_NON_PRESENTE, 
						prefix+"Token senza claim '"+Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED+"'"));
			}
			
			if(objectNode.has(Claims.JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE)) {
				Object nbf = objectNode.get(Claims.JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE);
				if(nbf!=null) {
					Date nbfDate = null;
					try {
						nbfDate = toDate(nbf);
					}catch(Exception e) {
						erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SCADENZA_NON_VALIDA, 
								prefix+"Token con claim '"+Claims.JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE+"' non valido: "+e.getMessage(),e));
					}
					String nbfValue = DateUtils.getSimpleDateFormatMs().format(nbfDate);
					if(!headerDuplicati || HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_NBF, nbfValue);
					}
					else {
						String nbfAuthorization = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_NBF);
						if(!nbfValue.equals(nbfAuthorization)) {
							busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_NBF, nbfValue);
						}
					}
				}
			}
			
			if(objectNode.has(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE)) {
				Object aud = objectNode.get(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE);
				if(aud==null) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
							request ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_PRESENTE :
								CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_PRESENTE, 
						prefix+"Token senza claim '"+Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE+"'"));
				}
				String audValue = toString(aud);
				if(!headerDuplicati || HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE, audValue);
				}
				else {
					String audAuthorization = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE);
					if(!audValue.equals(audAuthorization)) {
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_AUDIENCE, audValue);
					}
				}
			}
			else {
				if(request || buildSecurityTokenInRequest) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
							request ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_PRESENTE :
								CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_PRESENTE, 
						prefix+"Token senza claim '"+Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE+"'"));
				}
			}
			
			boolean jtiRequired = false;
			if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(securityMessageProfile) ||
					ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(securityMessageProfile)) {
				jtiRequired = true;
			}
			if(objectNode.has(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID)) {
				Object jti = objectNode.get(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID);
				if(jti==null) {
					if(jtiRequired) {
						erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE, 
								prefix+"Token senza claim '"+Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID+"'"));
					}
				}
				else {
					String id = toString(jti);
					boolean addAsIdBusta = true;
					if(headerDuplicati) {
						if(HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
							if(!securityConfig.isMultipleHeaderUseJtiAuthorizationAsIdMessaggio()) {
								String idActual = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_ID);
								if(idActual==null || !idActual.equals(id)) {
									busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUTHORIZATION_ID, id);
								}
								addAsIdBusta = false;
							}
						}
						else {
							if(securityConfig.isMultipleHeaderUseJtiAuthorizationAsIdMessaggio()) {
								String idActual = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_ID);
								if(idActual==null || !idActual.equals(id)) {
									busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_ID, id);
								}
								addAsIdBusta = false;
							}
							else {
								// verifico se ho gia' impostato authorization
								String idActualAuthorization = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUTHORIZATION_ID);
								if(idActualAuthorization!=null && idActualAuthorization.equals(id)) {
									busta.removeProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUTHORIZATION_ID);
								}
							}
						}
					}
					if(addAsIdBusta) {
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_ID, id);
						if(id.length()<=255) {
							busta.setID(id);
						}
					}
				}
			}
			else {
				if(jtiRequired) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE, 
							prefix+"Token senza claim '"+Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID+"'"));
				}
			}

			if(!request) {
				corniceSicurezza = false; // permessa solo per i messaggi di richiesta
			}
			
			boolean readIss = true;
			boolean readSub = true;
			
			if(corniceSicurezza) {
				
				String claimNameCodiceEnte = this.modiProperties.getSicurezzaMessaggio_corniceSicurezza_rest_codice_ente();
				if(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUER.equals(claimNameCodiceEnte)) {
					readIss = false;
				}
				if(objectNode.has(claimNameCodiceEnte)) {
					Object codiceEnte = objectNode.get(claimNameCodiceEnte);
					if(codiceEnte!=null) {
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CORNICE_SICUREZZA_ENTE, toString(codiceEnte));
					}
				}
				else {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE, 
							prefix+"Token senza claim '"+claimNameCodiceEnte+"'"));
				}
				
				String claimNameUser = this.modiProperties.getSicurezzaMessaggio_corniceSicurezza_rest_user();
				if(Claims.JSON_WEB_TOKEN_RFC_7519_SUBJECT.equals(claimNameUser)) {
					readSub = false;
				}
				if(objectNode.has(claimNameUser)) {
					Object user = objectNode.get(claimNameUser);
					if(user!=null) {
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CORNICE_SICUREZZA_USER, toString(user));
					}
				}
				else {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE, 
							prefix+"Token senza claim '"+claimNameUser+"'"));
				}
				
				String claimNameIpUser = this.modiProperties.getSicurezzaMessaggio_corniceSicurezza_rest_ipuser();
				if(objectNode.has(claimNameIpUser)) {
					Object userIp = objectNode.get(claimNameIpUser);
					if(userIp!=null) {
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CORNICE_SICUREZZA_USER_IP, toString(userIp));
					}
				}
				else {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE, 
							prefix+"Token senza claim '"+claimNameIpUser+"'"));
				}
			}
			
			if(readIss) {
				if(objectNode.has(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUER)) {
					Object iss = objectNode.get(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUER);
					if(iss!=null) {
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_ISSUER, toString(iss));
					}
				}
			}
			if(readSub) {
				if(objectNode.has(Claims.JSON_WEB_TOKEN_RFC_7519_SUBJECT)) {
					Object sub = objectNode.get(Claims.JSON_WEB_TOKEN_RFC_7519_SUBJECT);
					if(sub!=null) {
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_SUBJECT, toString(sub));
					}
				}
			}
			
			Object clientId = null;
			String claimName = this.modiProperties.getRestSecurityTokenClaimsClientIdHeader();
			if(objectNode.has(Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID)) {
				clientId = objectNode.get(Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID);
			}
			else if(objectNode.has(Claims.OIDC_ID_TOKEN_AZP)) {
				clientId = objectNode.get(Claims.OIDC_ID_TOKEN_AZP);
			}
			else if(objectNode.has(claimName)) {
				clientId = objectNode.get(claimName);
			}
			if(clientId!=null) {
				String clientIdValue = toString(clientId);
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_CLIENT_ID,clientIdValue);
			}
			
			boolean integrita = false;
			if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(securityMessageProfile) ||
					ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(securityMessageProfile)) {
				integrita = true;
			}
			
			String digestHeader = HttpConstants.DIGEST;
			String digestValueInHeaderHTTP = null;
			if(integrita && msg.castAsRest().hasContent()) {
				
				List<String> digests = null;
				if(msg!=null) {
					if(request && msg.getTransportRequestContext()!=null) {
						digests = msg.getTransportRequestContext().getHeaderValues(digestHeader);
					}
					else if(!request && msg.getTransportResponseContext()!=null) {
						digests = msg.getTransportResponseContext().getHeaderValues(digestHeader);
					}
				}	
				String digest = null;
				if(digests!=null && !digests.isEmpty()) {
					digest = digests.get(0);
				}
				if(digest==null) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_PRESENTE, 
							"Header HTTP '"+digestHeader+"' non presente"));
				}
				else if(digests.size()>1) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.PROFILO_TRASMISSIONE_PRESENTE_PIU_VOLTE, 
							"Header HTTP '"+digestHeader+"' presente più volte"));
				}
				else {
					digestValueInHeaderHTTP = toString(digest);
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_DIGEST, digestValueInHeaderHTTP);
					if(request && includiRequestDigest && this.context!=null) {
						this.context.addObject(ModICostanti.MODIPA_CONTEXT_REQUEST_DIGEST, digestValueInHeaderHTTP);
					}
				}
			}
			
			if(digestValueInHeaderHTTP!=null) {
				
				// Produco Digest Headers
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				msg.castAsRest().writeTo(bout, false, bufferMessage_readOnly, idTransazione);
				bout.flush();
				bout.close();
				
				List<DigestEncoding> digestEncoding = securityConfig.getDigestEncodingAccepted();
				Map<DigestEncoding, String> newDigestValue = null;
				boolean formatoSupportato = true;
				if(digestValueInHeaderHTTP.startsWith(HttpConstants.DIGEST_ALGO_SHA_256+"=")) {
					newDigestValue = HttpUtilities.getDigestHeaderValues(bout.toByteArray(), HttpConstants.DIGEST_ALGO_SHA_256,
							digestEncoding.toArray(new DigestEncoding[1]));
				}
				else if(digestValueInHeaderHTTP.startsWith(HttpConstants.DIGEST_ALGO_SHA_384+"=")) {
					newDigestValue = HttpUtilities.getDigestHeaderValues(bout.toByteArray(), HttpConstants.DIGEST_ALGO_SHA_384,
							digestEncoding.toArray(new DigestEncoding[1]));
				}
				else if(digestValueInHeaderHTTP.startsWith(HttpConstants.DIGEST_ALGO_SHA_512+"=")) {
					newDigestValue = HttpUtilities.getDigestHeaderValues(bout.toByteArray(), HttpConstants.DIGEST_ALGO_SHA_512,
							digestEncoding.toArray(new DigestEncoding[1]));
				}
				else {
					formatoSupportato = false;
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_INTESTAZIONE_NON_VALIDA, 
							"Header HTTP '"+digestHeader+"' con un formato non supportato"));
				}
				
				if(formatoSupportato) {
					if(newDigestValue==null || newDigestValue.isEmpty()) {
						erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_INTESTAZIONE_NON_VALIDA, 
								"Calcolo Digest fallito"));
					}
					else {
						boolean valido = false;
						for (DigestEncoding de : digestEncoding) {
							String check = newDigestValue.get(de);
							if(check==null) { // non deve succedere
								erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_INTESTAZIONE_NON_VALIDA, 
										"Encoding Digest '"+de+"' fallito"));
							}
							else if(check.equals(digestValueInHeaderHTTP)){
								valido=true;
								break;
							}
						}
						if(!valido) {
							erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA, 
									"Header HTTP '"+digestHeader+"' possiede un valore non corrispondente al messaggio"));
						}
//						else {
//							System.out.println("VERIFICATO DIGEST HTTP");
//						}
					}
				}
				
			}
			
			String claimSignedHeader = this.modiProperties.getRestSecurityTokenClaimSignedHeaders();
			if(objectNode.has(claimSignedHeader)) {
				
				boolean findDigestInClaimSignedHeader = false;
				
				Map<String, List<String>> headerHttpAttesi = new HashMap<String, List<String>>();
				
				Object signedHeaders = objectNode.get(claimSignedHeader);
				if(signedHeaders==null) {
					if(integrita && msg.castAsRest().hasContent()) {
						erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_INTESTAZIONE_NON_PRESENTE, 
								prefix+"Token senza claim '"+claimSignedHeader+"'"));
					}
				}
				else {
					try {
						if(signedHeaders instanceof ArrayNode) {
							ArrayNode arrayNode = (ArrayNode) signedHeaders;
							if(arrayNode.size()<=0) {
								throw new Exception("atteso un array con almeno un valore");
							}
							for (int i = 0; i < arrayNode.size(); i++) {
								JsonNode hdrNode = arrayNode.get(i);
								if(hdrNode==null) {
									throw new Exception("array["+i+"] non possiede un valore");
								}
								if(!(hdrNode instanceof ObjectNode)) {
									throw new Exception("array["+i+"] possiede una struttura errata");
								}
								ObjectNode oNode = (ObjectNode) hdrNode;
								if(oNode.size()<=0) {
									throw new Exception("array["+i+"] possiede una struttura senza elementi");
								}
								Iterator<String> fieldNames = oNode.fieldNames();
								while (fieldNames.hasNext()) {
									String hdrName = (String) fieldNames.next();
									try {
										String hdrValue = toString(oNode.get(hdrName));
										if(HttpConstants.AUTHORIZATION.equalsIgnoreCase(hdrName) && headerDuplicati) {
											ModIUtilities.addHeaderProperty(busta, hdrName, HttpConstants.AUTHORIZATION_PREFIX_BEARER +"...TOKEN...");	
										}
										else {
											ModIUtilities.addHeaderProperty(busta, hdrName, hdrValue);
										}
										TransportUtils.addHeader(headerHttpAttesi, hdrName, hdrValue);
									}catch(Exception e) {
										throw new Exception("array["+i+"] possiede header '"+hdrName+"' con un valore non valido: "+e.getMessage(),e);
									}
								}
							}
						}
						else {
							throw new Exception("atteso un array");
						}
					}catch(Exception e) {
						this.log.error("Errore durante il processamento del claim che definisce gli header HTTP firmati '"+claimSignedHeader+"': "+e.getMessage(),e);
						erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_INTESTAZIONE_NON_VALIDA, 
								prefix+"Claim '"+claimSignedHeader+"' con un formato non valido; "+ e.getMessage()));
					}
				}
				
				if(headerHttpAttesi!=null && !headerHttpAttesi.isEmpty()) {
					Iterator<String> headers = headerHttpAttesi.keySet().iterator();
					while (headers.hasNext()) {
						String hdrName = (String) headers.next();
						List<String> hrdAttesiValues = headerHttpAttesi.get(hdrName);
						boolean checkHdrAttesiSize = true;
						List<String> hdrFound = null;
						for (String hdrAttesoValue : hrdAttesiValues) {
							boolean valid = false;
							String valueInHttpHeader = null;
							boolean multiHeader = false;
							if(digestHeader.toLowerCase().equalsIgnoreCase(hdrName)) {
								checkHdrAttesiSize = false; // il controllo che non esista piu' di un header digest e' stato fatto in precedenza
								findDigestInClaimSignedHeader = true;
								if(digestValueInHeaderHTTP==null) {
									erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA, 
											"Header HTTP '"+hdrName+"', dichiarato tra gli header firmati, non trovato"));
									valid = true; // per non far segnalare l'eccezione, che e' stata personalizzata sopra
								}
								else {
									valueInHttpHeader = digestValueInHeaderHTTP;
									valid = hdrAttesoValue.equals(digestValueInHeaderHTTP); 
									//System.out.println("VALID DIGEST: "+valid);
								}
							}
							else {
								if(request && msg.getTransportRequestContext()!=null) {
									hdrFound = msg.getTransportRequestContext().getHeaderValues(hdrName);
								}
								else if(!request && msg.getTransportResponseContext()!=null) {
									hdrFound = msg.getTransportResponseContext().getHeaderValues(hdrName);
								}
								
								if(checkHdrAttesiSize && hdrFound!=null) {
									// Fix: per verificare che non esistano altri copie dell'header, oltre a quelli attesi, con valori differenti
									if(hrdAttesiValues.size()!=hdrFound.size()) {
										this.log.error("Header HTTP '"+hdrName+"' possiede "+(hdrFound!=null ? hdrFound.size() : 0)+" valori '"+(hdrFound!=null ? hdrFound.toString() : "non presente")+
												"' mentre negli header firmati sono presenti "+hrdAttesiValues.size()+" valori '"+hrdAttesiValues.toString()+"'");
										erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA, 
												"Header HTTP '"+hdrName+"' possiede un numero di valori ("+(hdrFound!=null ? hdrFound.size() : 0)
													+") differente rispetto al numero di valori ("+hrdAttesiValues.size()+") definiti negli header firmati"));
										valid = true; // per non far segnalare l'eccezione, che e' stata personalizzata sopra
										break; // e' inutile continuare a controllare i valori degli header
									}
									checkHdrAttesiSize=false; // questo controllo per ogni header basta 1 volta, non serve farlo per ogni valore
								}
								
								if(hdrFound==null || hdrFound.isEmpty()) {
									erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA, 
											"Header HTTP '"+hdrName+"', dichiarato tra gli header firmati, non trovato"));
									valid = true; // per non far segnalare l'eccezione, che e' stata personalizzata sopra
									break; // e' inutile continuare a controllare i valori degli header
								}
								else if(hdrFound.size()==1) {
									valueInHttpHeader = hdrFound.get(0);
									valid = hdrAttesoValue.equals(valueInHttpHeader);
								}
								else {
									multiHeader = true;
									valueInHttpHeader = hdrFound.toString();
									valid = hdrFound.contains(hdrAttesoValue); 
								}
																
								//System.out.println("VALID HDR '"+hdrName+"': "+valid);
							}
							if(!valid) {
								String errorMsg = "possiede un valore '"+valueInHttpHeader+"' differente";
								String errorExc = "possiede un valore differente";
								if(multiHeader) {
									errorMsg = "possiede dei valori '"+valueInHttpHeader+"' differenti";
									errorExc = "possiede dei valori differenti";
								}
								this.log.error("Header HTTP '"+hdrName+"' "+errorMsg+" rispetto a quello presente negli header firmati '"+hdrAttesoValue+"'");
								erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA, 
										"Header HTTP '"+hdrName+"' "+errorExc+" rispetto a quello presente negli header firmati"));
							}
						}
					}
				}
				
				if(integrita && msg.castAsRest().hasContent()) {
					if(!findDigestInClaimSignedHeader) {
						erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_INTESTAZIONE_NON_PRESENTE, 
								"Header HTTP '"+digestHeader+"' non presente nella lista degli header firmati (token claim '"+claimSignedHeader+"')"));
					}
				}
			}
			else {
				if(integrita && msg.castAsRest().hasContent()) {
					erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_FIRMA_INTESTAZIONE_NON_PRESENTE, 
							prefix+"Token senza claim '"+claimSignedHeader+"'"));
				}
			}
			
			
		}catch(Exception e) {
			this.log.error("Errore durante il processamento del payload del token di sicurezza: "+e.getMessage(),e);
			erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_VALIDO, 
					prefix+e.getMessage()));
		}
		
		return token;
	}
	
	private Date toDate(Object tmp) throws Exception {
		
		String tmpV = null;
		if(tmp==null) {
			throw new Exception("Value undefined");
		}
		if(tmp instanceof String) {
			tmpV = (String) tmp;
		}
		else {
			tmpV = toString(tmp);
		}
		
		Date d = TokenUtils.parseTimeInSecond(tmpV);
		if(d!=null) {
			return d;
		}
		
		throw new Exception("Value '"+tmpV+"' not valid");
	}
	
	private String toString(Object tmp) throws Exception {
		if(tmp instanceof TextNode) {
			TextNode text = (TextNode) tmp;
			return text.asText();
		}
		return tmp.toString();
	}
}
