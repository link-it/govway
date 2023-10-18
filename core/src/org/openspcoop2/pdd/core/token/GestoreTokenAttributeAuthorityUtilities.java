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

package org.openspcoop2.pdd.core.token;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rt.security.rs.RSSecurityConstants;
import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.ForwardProxy;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreBaseHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreHTTPS;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.controllo_traffico.PolicyTimeoutConfig;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.dynamic.ErrorHandler;
import org.openspcoop2.pdd.core.dynamic.MessageContent;
import org.openspcoop2.pdd.core.dynamic.Template;
import org.openspcoop2.pdd.core.token.attribute_authority.AttributeAuthorityDynamicParameters;
import org.openspcoop2.pdd.core.token.attribute_authority.BasicRetrieveAttributeAuthorityResponseParser;
import org.openspcoop2.pdd.core.token.attribute_authority.EsitoRecuperoAttributi;
import org.openspcoop2.pdd.core.token.attribute_authority.IRetrieveAttributeAuthorityResponseParser;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.core.token.attribute_authority.PolicyAttributeAuthority;
import org.openspcoop2.pdd.core.token.attribute_authority.RequiredAttributes;
import org.openspcoop2.pdd.core.token.attribute_authority.pa.EsitoRecuperoAttributiPortaApplicativa;
import org.openspcoop2.pdd.core.token.attribute_authority.pd.EsitoRecuperoAttributiPortaDelegata;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.CRLCertstore;
import org.openspcoop2.security.keystore.JWKSetStore;
import org.openspcoop2.security.keystore.KeyPairStore;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.keystore.cache.GestoreOCSPResource;
import org.openspcoop2.security.keystore.cache.GestoreOCSPValidator;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.jose.JOSEUtils;
import org.openspcoop2.utils.LoggerBuffer;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JWTOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.JsonVerifySignature;
import org.openspcoop2.utils.security.JwtHeaders;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;
import org.openspcoop2.utils.transport.http.IOCSPValidator;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**     
 * GestoreTokenAttributeAuthorityUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreTokenAttributeAuthorityUtilities {

	private GestoreTokenAttributeAuthorityUtilities(){}
		
	static EsitoRecuperoAttributi readAttributes(Logger log, PolicyAttributeAuthority policyAttributeAuthority,
			IProtocolFactory<?> protocolFactory,
			AttributeAuthorityDynamicParameters dynamicParameters,
			String request, boolean portaDelegata, String idModulo, PortaApplicativa pa, PortaDelegata pd,
			IState state,
			IDSoggetto idDominio, IDServizio idServizio,
			RequestInfo requestInfo) {
		EsitoRecuperoAttributi esitoRecuperoAttributi = null;
		if(portaDelegata) {
			esitoRecuperoAttributi = new EsitoRecuperoAttributiPortaDelegata();
		}
		else {
			esitoRecuperoAttributi = new EsitoRecuperoAttributiPortaApplicativa();
		}
		
		esitoRecuperoAttributi.setTokenInternalError();
		
		try{			
			String detailsError = null;
			InformazioniAttributi informazioniAttributi = null;
			Exception eProcess = null;
			
			IRetrieveAttributeAuthorityResponseParser tokenParser = policyAttributeAuthority.getRetrieveAttributeAuthorityResponseParser(log);
			
			HttpResponse httpResponse = null;
			Integer httpResponseCode = null;
			byte[] risposta = null;
			try {
				httpResponse = http(log, policyAttributeAuthority,
						protocolFactory,
						dynamicParameters,
						request,
						state, portaDelegata, idModulo, pa, pd,
						idDominio, idServizio,
						requestInfo);
				risposta = httpResponse.getContent();
				httpResponseCode = httpResponse.getResultHTTPOperation();
			}catch(Exception e) {
				detailsError = "(Errore di Connessione) "+ e.getMessage();
				eProcess = e;
			}
			
			if(detailsError==null) {
				
				String decodedPayload = null;
				byte[] decodedPayloadAsBytes = null;
				if(policyAttributeAuthority.isResponseJws()) {
	    			// JWS Compact   			
	    			JsonVerifySignature jsonCompactVerify = null;
	    			try {
	    				JWTOptions options = new JWTOptions(JOSESerialization.COMPACT);
	    				Properties p = policyAttributeAuthority.getProperties().get(org.openspcoop2.pdd.core.token.attribute_authority.Costanti.POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID);
	    				JOSEUtils.injectKeystore(requestInfo, p, log); // serve per leggere il keystore dalla cache
	    				
	    				String aliasMode = p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS+".mode"); 
						if(aliasMode!=null && 
								(
										aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C)
										||
										aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5T256)
										||
										aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C_X5T256)
										||
										aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_KID)
										||
										aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5U)
								)
							) {
							
							options.setPermitUseHeaderX5C(aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C) || aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C_X5T256));
	    					// se lo si vuole usare utilizzare la vecchia modalità alias special case
	    					/** options.setPermitUseHeaderX5T(aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5T256) || aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C_X5T256)); */
	    					options.setPermitUseHeaderX5T_256(aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5T256) || aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C_X5T256));
	    					options.setPermitUseHeaderKID(aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_KID));
	    					options.setPermitUseHeaderX5U(aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5U));
	    												
							if(p.containsKey(RSSecurityConstants.RSSEC_KEY_STORE)) {
								Object oKeystore = p.get(RSSecurityConstants.RSSEC_KEY_STORE);
								if(oKeystore instanceof java.security.KeyStore) {
									java.security.KeyStore keystore = (java.security.KeyStore) oKeystore;
			    					jsonCompactVerify = new JsonVerifySignature(keystore, options);
			    					
		    						String signatureOCSP = policyAttributeAuthority.getResponseJwsOcspPolicy();
		    						String signatureCRL = policyAttributeAuthority.getResponseJwsCrl();
		    						
		    						boolean crlByOcsp = false;
		    						if(keystore!=null && signatureOCSP!=null && !"".equals(signatureOCSP)) {
		    							LoggerBuffer lb = new LoggerBuffer();
		    							lb.setLogDebug(log);
		    							lb.setLogError(log);
		    							GestoreOCSPResource ocspResourceReader = new GestoreOCSPResource(requestInfo);
		    							IOCSPValidator ocspValidator = null;
		    							try {
		    								org.openspcoop2.utils.certificate.KeyStore trustStore = new org.openspcoop2.utils.certificate.KeyStore(keystore);
		    								ocspValidator = new GestoreOCSPValidator(requestInfo, lb, 
		    										trustStore,
		    										signatureCRL, 
		    										signatureOCSP, 
		    										ocspResourceReader);
		    							}catch(Exception e){
		    								throw new TokenException("ocsp initialization (policy:'"+signatureOCSP+"') failed: "+e.getMessage(),e);
		    							}
		    							if(ocspValidator!=null) {
		    								jsonCompactVerify.setOcspValidatorX509(ocspValidator);
		    								GestoreOCSPValidator gOcspValidator = (GestoreOCSPValidator) ocspValidator;
		    								if(gOcspValidator.getOcspConfig()!=null) {
		    									crlByOcsp = gOcspValidator.getOcspConfig().isCrl();
		    								}
		    							}
		    						}
		    						
		    						if(signatureCRL!=null && !"".equals(signatureCRL) && !crlByOcsp) {
		    							CRLCertstore crlCertstore = GestoreKeystoreCache.getCRLCertstore(requestInfo, signatureCRL);
		    							if(crlCertstore==null) {
		    								throw new TokenException("Process CRL '"+signatureCRL+"' failed");
		    							}
		    							jsonCompactVerify.setCrlX509(crlCertstore.getCertStore());
		    						}

								}
							}
							else if(p.containsKey(JoseConstants.RSSEC_KEY_STORE_JWKSET)) {
								Object oKeystore = p.get(JoseConstants.RSSEC_KEY_STORE_JWKSET);
								if(oKeystore instanceof String) {
									String keystore = (String) oKeystore;
									JsonWebKeys jwksKeystore = new JWKSet(keystore).getJsonWebKeys();
									jsonCompactVerify = new JsonVerifySignature(jwksKeystore, options);
								}
							}
						}
						    				
						if(jsonCompactVerify==null) {
							jsonCompactVerify = new JsonVerifySignature(p, options);
						}
						
	    				if(jsonCompactVerify.verify(new String(risposta))) {
	    					if(tokenParser instanceof BasicRetrieveAttributeAuthorityResponseParser) {
	    						decodedPayload = jsonCompactVerify.getDecodedPayload(); 
	    					}
	    					else {
	    						decodedPayloadAsBytes = jsonCompactVerify.getDecodedPayloadAsByte();
	    					}
	    				}
	    				else {
	    					detailsError = "Risposta non valida";
	    				}
	    			}catch(Exception e) {
	    				detailsError = "Risposta non valida: "+e.getMessage();
	    				eProcess = e;
	    			}
				}
				
				if(detailsError==null) {
					try {
						if(tokenParser instanceof BasicRetrieveAttributeAuthorityResponseParser) {
							informazioniAttributi = new InformazioniAttributi(httpResponseCode, policyAttributeAuthority.getName(),
									decodedPayload!=null ? decodedPayload : new String(risposta),
									tokenParser);		
						}
						else {
							informazioniAttributi = new InformazioniAttributi(httpResponseCode, policyAttributeAuthority.getName(), 
									decodedPayloadAsBytes!=null ? decodedPayloadAsBytes : risposta, 
									tokenParser);		
						}
					}catch(Exception e) {
						detailsError = "Risposta del servizio di negoziazione token non valida: "+e.getMessage();
						eProcess = e;
					}
				}
			}
    		  		
    		if(informazioniAttributi!=null && informazioniAttributi.isValid()) {
    			esitoRecuperoAttributi.setTokenValido();
    			esitoRecuperoAttributi.setInformazioniAttributi(informazioniAttributi);
    			esitoRecuperoAttributi.setNoCache(false);
			}
    		else {
    			esitoRecuperoAttributi.setTokenValidazioneFallita();
    			esitoRecuperoAttributi.setNoCache(!policyAttributeAuthority.isSaveErrorInCache());
    			esitoRecuperoAttributi.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoRecuperoAttributi.setDetails(detailsError);	
				}
				else {
					esitoRecuperoAttributi.setDetails("Attributi non recuperabili");	
				} 	
    		}
    		
		}catch(Exception e){
			esitoRecuperoAttributi.setTokenInternalError();
			esitoRecuperoAttributi.setDetails(e.getMessage());
			esitoRecuperoAttributi.setEccezioneProcessamento(e);
    	}
		
		return esitoRecuperoAttributi;
	}
	
	static String buildCacheKeyRecuperoAttributiPrefix(String nomePolicy, String funzione) {
    	StringBuilder bf = new StringBuilder("AttributeAuthority_"+funzione);
    	bf.append("_");
    	bf.append(nomePolicy);
    	bf.append("_");
    	return bf.toString();
	}
	static String buildCacheKeyRecuperoAttributi(String nomePolicy, String funzione, boolean portaDelegata,
			AttributeAuthorityDynamicParameters dynamicParameters, String request) {
    	StringBuilder bf = new StringBuilder();
    	bf.append(buildCacheKeyRecuperoAttributiPrefix(nomePolicy, funzione));
    	if(portaDelegata){ // serve per non aver classcast exception nei risultati
    		bf.append("PD");
    	}
    	else {
    		bf.append("PA");
    	}
    	bf.append("_");
    	
    	String dynamicParametersKeyCache = dynamicParameters.toString("_", true);
    	bf.append(dynamicParametersKeyCache);
    	bf.append("_");
    	
    	bf.append(Base64Utilities.encodeAsString(request.getBytes())); // codifico in base64 la richiesta
    	return bf.toString();
    }
	
	
	static Map<String, Object> buildDynamicAAMap(OpenSPCoop2Message message, Busta busta, 
			RequestInfo requestInfo, PdDContext pddContext, Logger log,
			String nomeAttributeAuthority,
			org.openspcoop2.pdd.core.token.attribute_authority.AbstractDatiInvocazione datiInvocazione) throws TokenException {
	
		Map<String, Object> dynamicMap = new HashMap<>();
		
		Map<String, List<String>> pTrasporto = null;
		String urlInvocazione = null;
		Map<String, List<String>> pQuery = null;
		Map<String, List<String>> pForm = null;
		if(requestInfo!=null && requestInfo.getProtocolContext()!=null) {
			pTrasporto = requestInfo.getProtocolContext().getHeaders();
			urlInvocazione = requestInfo.getProtocolContext().getUrlInvocazione_formBased();
			pQuery = requestInfo.getProtocolContext().getParameters();
			if(requestInfo.getProtocolContext() instanceof HttpServletTransportRequestContext) {
				HttpServletTransportRequestContext httpServletContext = requestInfo.getProtocolContext();
				HttpServletRequest httpServletRequest = httpServletContext.getHttpServletRequest();
				if(httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
					FormUrlEncodedHttpServletRequest formServlet = (FormUrlEncodedHttpServletRequest) httpServletRequest;
					if(formServlet.getFormUrlEncodedParametersValues()!=null &&
							!formServlet.getFormUrlEncodedParametersValues().isEmpty()) {
						pForm = formServlet.getFormUrlEncodedParametersValues();
					}
				}
			}
		}
		
		MessageContent messageContent = null;
		try {
			boolean bufferMessageReadOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
			if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
				messageContent = new MessageContent(message.castAsSoap(), bufferMessageReadOnly, pddContext);
			}
			else{
				if(MessageType.XML.equals(message.getMessageType())){
					messageContent = new MessageContent(message.castAsRestXml(), bufferMessageReadOnly, pddContext);
				}
				else if(MessageType.JSON.equals(message.getMessageType())){
					messageContent = new MessageContent(message.castAsRestJson(), bufferMessageReadOnly, pddContext);
				}
			}
		}catch(Exception e) {
			throw new TokenException(e.getMessage(),e);
		}
		
		ErrorHandler errorHandler = new ErrorHandler();
		DynamicUtils.fillDynamicMapRequest(log, dynamicMap, pddContext, urlInvocazione,
				message,
				messageContent, 
				busta, 
				pTrasporto, 
				pQuery,
				pForm,
				errorHandler);
		
		List<String> listAttributi = null;
		if(datiInvocazione instanceof org.openspcoop2.pdd.core.token.attribute_authority.pa.DatiInvocazionePortaApplicativa) {
			org.openspcoop2.pdd.core.token.attribute_authority.pa.DatiInvocazionePortaApplicativa datiPA = 
					(org.openspcoop2.pdd.core.token.attribute_authority.pa.DatiInvocazionePortaApplicativa) datiInvocazione;
			if(datiPA.getPa()!=null && datiPA.getPa().sizeAttributeAuthorityList()>0) {
				for (AttributeAuthority aa : datiPA.getPa().getAttributeAuthorityList()) {
					if(nomeAttributeAuthority.equals(aa.getNome())) {
						listAttributi = aa.getAttributoList();
						break;
					}
				}
			}
			else if(datiPA.getPd()!=null && datiPA.getPd().sizeAttributeAuthorityList()>0) {
				for (AttributeAuthority aa : datiPA.getPd().getAttributeAuthorityList()) {
					if(nomeAttributeAuthority.equals(aa.getNome())) {
						listAttributi = aa.getAttributoList();
						break;
					}
				}
			}
		}
		else if(datiInvocazione instanceof org.openspcoop2.pdd.core.token.attribute_authority.pd.DatiInvocazionePortaDelegata) {
			org.openspcoop2.pdd.core.token.attribute_authority.pd.DatiInvocazionePortaDelegata datiPD = 
					(org.openspcoop2.pdd.core.token.attribute_authority.pd.DatiInvocazionePortaDelegata) datiInvocazione;
			if(datiPD.getPd()!=null && datiPD.getPd().sizeAttributeAuthorityList()>0) {
				for (AttributeAuthority aa : datiPD.getPd().getAttributeAuthorityList()) {
					if(nomeAttributeAuthority.equals(aa.getNome())) {
						listAttributi = aa.getAttributoList();
						break;
					}
				}
			}
		}
		
		// Viene messo sempre per facilitare la scrittura delle regole
		if(listAttributi==null) {
			listAttributi = new ArrayList<>();
		}
		/**if(listAttributi!=null && listAttributi.size()>0) {*/
		RequiredAttributes reqAttrs = new RequiredAttributes(listAttributi);
		dynamicMap.put(org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUIRED_ATTRIBUTES, reqAttrs);
		dynamicMap.put(org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUIRED_ATTRIBUTES.toLowerCase(), reqAttrs);
		
		return dynamicMap;
	}
	
	static String buildDynamicAARequest(ConfigurazionePdDManager configurazionePdDManager,
			IProtocolFactory<?> protocolFactory, RequestInfo requestInfo, 
			PolicyAttributeAuthority policyAttributeAuthority,
			AttributeAuthorityDynamicParameters dynamicParameters,
			boolean addIdAndDate) throws TokenException, UtilsException {
			
		String dynamicContent = policyAttributeAuthority.getRequestDynamicPayload();
		String request = null;
		
		if(policyAttributeAuthority.isRequestDynamicPayloadTemplate() || policyAttributeAuthority.isRequestDynamicPayloadJwt()) {
			if(policyAttributeAuthority.isRequestDynamicPayloadTemplate()) {
				request = dynamicParameters.getRequestDynamicPayloadTemplate();
			}
			else {
				request = buildAAJwt(policyAttributeAuthority, dynamicParameters, 
						protocolFactory, requestInfo, 
						addIdAndDate);
			}
		}
		else if(policyAttributeAuthority.isRequestDynamicPayloadFreemarkerTemplate()) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				Template template = configurazionePdDManager.getTemplateAttributeAuthorityRequest(policyAttributeAuthority.getName(), dynamicContent.getBytes(), requestInfo);
				DynamicUtils.convertFreeMarkerTemplate(template, dynamicParameters.getDynamicMap(), bout);
				bout.flush();
				bout.close();
			}catch(Exception e) {
				throw new TokenException(e.getMessage(),e);
			}
			request = bout.toString();
			
			if(policyAttributeAuthority.isRequestJws()) {
				request = normalizeJwtPayload(request);
			}
		}
		else if(policyAttributeAuthority.isRequestDynamicPayloadVelocityTemplate()) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				Template template = configurazionePdDManager.getTemplateAttributeAuthorityRequest(policyAttributeAuthority.getName(), dynamicContent.getBytes(), requestInfo);
				DynamicUtils.convertVelocityTemplate(template, dynamicParameters.getDynamicMap(), bout);
				bout.flush();
				bout.close();
			}catch(Exception e) {
				throw new TokenException(e.getMessage(),e);
			}
			request = bout.toString();
			
			if(policyAttributeAuthority.isRequestJws()) {
				request = normalizeJwtPayload(request);
			}
		}
		if(request==null || "".equals(request)) {
			throw new TokenException("Request undefined");
		}
		
		return request;
	}
	
	private static String normalizeJwtPayload(String payload) throws UtilsException {
		JSONUtils jsonUtils = JSONUtils.getInstance(false);
		JsonNode jwtPayload = jsonUtils.getAsNode(payload);
		return jsonUtils.toString(jwtPayload);
	}
	
	private static String buildAAJwt(PolicyAttributeAuthority policyAttributeAuthority, 
			AttributeAuthorityDynamicParameters dynamicParameters,
			IProtocolFactory<?> protocolFactory, RequestInfo requestInfo,
			boolean addIdAndDate) throws TokenException, UtilsException {
		
		// https://datatracker.ietf.org/doc/html/rfc7523
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		JSONUtils jsonUtils = JSONUtils.getInstance(false);
		
		ObjectNode jwtPayload = jsonUtils.newObjectNode();

		// add iat, nbf, exp
		long nowMs = DateManager.getTimeMillis();
		long nowSeconds = nowMs/1000;
		
		String issuer = dynamicParameters.getIssuer();
		if(issuer==null || "".equals(issuer)) {
			issuer = op2Properties.getIdentitaPortaDefault(protocolFactory!=null ? protocolFactory.getProtocol() : null, requestInfo).getNome();
		}
		jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUER, issuer);
		
		String subject = dynamicParameters.getSubject();
		if(subject==null) {
			throw new TokenException("JWT-Subject undefined");
		}
		jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_SUBJECT, subject);
		
		// The JWT MUST contain an "aud" (audience) claim containing a value that identifies the authorization server as an intended audience. 
		String audience = dynamicParameters.getAudience();
		if(audience==null) {
			throw new TokenException("JWT-Audience undefined");
		}
		jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE, audience);
		
		// claims
		String claims = dynamicParameters.getClaims();
		if(claims!=null && !"".equals(claims)) {
			Properties convertTextToProperties = PropertiesUtilities.convertTextToProperties(claims);
			if(convertTextToProperties!=null && !convertTextToProperties.isEmpty()) {
				Enumeration<Object> keys = convertTextToProperties.keys();
				while (keys.hasMoreElements()) {
					String nome = (String) keys.nextElement();
					if(nome!=null && !"".equals(nome)) {
						String valore = convertTextToProperties.getProperty(nome);
						if(valore!=null) {
							jsonUtils.putValue(jwtPayload, nome, valore);
						}
					}
				}
			}
		}
		
		if(addIdAndDate) {
		
			// The JWT MAY contain an "iat" (issued at) claim that identifies the time at which the JWT was issued. 
			jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT, nowSeconds);
			
			// The JWT MAY contain an "nbf" (not before) claim that identifies the time before which the token MUST NOT be accepted for processing.
			jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE, nowSeconds);
			
			// The JWT MUST contain an "exp" (expiration time) claim that limits the time window during which the JWT can be used.
			int ttl = -1;
			try {
				ttl = policyAttributeAuthority.getJwtTtlSeconds();
			}catch(Exception e) {
				throw new TokenException("Invalid JWT-TimeToLive value: "+e.getMessage(),e);
			}
			long expired = nowSeconds+ttl;
			jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED, expired);
			
			// The JWT MAY contain a "jti" (JWT ID) claim that provides a unique identifier for the token.
			String uuid = null;
			try {
				uuid = UniqueIdentifierManager.newUniqueIdentifier().toString();
			}catch(Exception e) {
				throw new TokenException("Invalid JWT-TimeToLive value: "+e.getMessage(),e);
			}
			jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID, uuid);
			
		}
		
		return jsonUtils.toString(jwtPayload);
	}
	
	static void validazioneInformazioniAttributiRecuperati(EsitoRecuperoAttributi esitoRecuperoAttributi, PolicyAttributeAuthority policyAttributeAuthority, boolean saveErrorInCache,
			AttributeAuthorityDynamicParameters dynamicParameters) throws TokenException, ProviderException, ProviderValidationException {
		
		Date now = DateManager.getDate();
		
		if(esitoRecuperoAttributi.isValido()) {
			esitoRecuperoAttributi.setDateValide(true); // tanto le ricontrollo adesso
		}
		
		if(esitoRecuperoAttributi.isValido() &&		
			esitoRecuperoAttributi.getInformazioniAttributi().getExp()!=null &&				
			!now.before(esitoRecuperoAttributi.getInformazioniAttributi().getExp())){
			/*
			 *  The lifetime in seconds of the access token.  For example, the value "3600" denotes that the access token will
			 * expire in one hour from the time the response was generated.
			 * If omitted, the authorization server SHOULD provide the expiration time via other means or document the default value. 
			 **/
			esitoRecuperoAttributi.setTokenScaduto();
			esitoRecuperoAttributi.setDateValide(false);
			esitoRecuperoAttributi.setDetails("Response attributes expired");
		}
			
		if(esitoRecuperoAttributi.isValido() &&
			esitoRecuperoAttributi.getInformazioniAttributi().getNbf()!=null &&		
			!esitoRecuperoAttributi.getInformazioniAttributi().getNbf().before(now)){
			/*
			 *   The "nbf" (not before) claim identifies the time before which the JWT MUST NOT be accepted for processing.  
			 **/
			esitoRecuperoAttributi.setTokenNotUsableBefore();
			esitoRecuperoAttributi.setDateValide(false);
			SimpleDateFormat sdf = DateUtils.getDefaultDateTimeFormatter(GestoreToken.DATE_FORMAT);
			esitoRecuperoAttributi.setDetails("Response attributes not usable before "+sdf.format(esitoRecuperoAttributi.getInformazioniAttributi().getNbf()));
		}
		
		if(esitoRecuperoAttributi.isValido() &&
			esitoRecuperoAttributi.getInformazioniAttributi().getIat()!=null) {				
			/*
			 *   The "iat" (issued at) claim identifies the time at which the JWT was issued.  This claim can be used to determine the age of the JWT.
			 *   The iat Claim can be used to reject tokens that were issued too far away from the current time, 
			 *   limiting the amount of time that nonces need to be stored to prevent attacks. The acceptable range is Client specific. 
			 **/
			Long old = null;
			try {
				old = OpenSPCoop2Properties.getInstance().getGestioneToken_iatTimeCheck_milliseconds();
			}catch(Exception e) {
				throw new TokenException(e.getMessage(),e);
			}
			if(old!=null) {
				Date oldMax = new Date((DateManager.getTimeMillis() - old.longValue()));
				if(esitoRecuperoAttributi.getInformazioniAttributi().getIat().before(oldMax)) {
					esitoRecuperoAttributi.setTokenScaduto();
					esitoRecuperoAttributi.setDateValide(false);
					SimpleDateFormat sdf = DateUtils.getDefaultDateTimeFormatter(GestoreToken.DATE_FORMAT);
					esitoRecuperoAttributi.setDetails("Response attributes expired; iat time '"+sdf.format(esitoRecuperoAttributi.getInformazioniAttributi().getIat())+"' too old");
				}
			}
			Long future = null;
			try {
				future = OpenSPCoop2Properties.getInstance().getGestioneToken_iatTimeCheck_futureTolerance_milliseconds();
			}catch(Exception e) {
				throw new TokenException(e.getMessage(),e);
			}
			if(future!=null) {
				Date futureMax = new Date((DateManager.getTimeMillis() + future.longValue()));
				if(esitoRecuperoAttributi.getInformazioniAttributi().getIat().after(futureMax)) {
					esitoRecuperoAttributi.setTokenInTheFuture();
					esitoRecuperoAttributi.setDateValide(false);
					SimpleDateFormat sdf = DateUtils.getDefaultDateTimeFormatter(GestoreToken.DATE_FORMAT);
					esitoRecuperoAttributi.setDetails("Response attributes valid in the future; iat time '"+sdf.format(esitoRecuperoAttributi.getInformazioniAttributi().getIat())+"' is in the future");
				}
			}
		}
		
		if(esitoRecuperoAttributi.isValido()) {		
			String audience = dynamicParameters.getResponseAudience();
			if(audience!=null && !"".equals(audience) &&
				( esitoRecuperoAttributi.getInformazioniAttributi().getAud()==null || !esitoRecuperoAttributi.getInformazioniAttributi().getAud().contains(audience) ) 
				){				
				esitoRecuperoAttributi.setTokenValidazioneFallita();
    			esitoRecuperoAttributi.setNoCache(!policyAttributeAuthority.isSaveErrorInCache());
				esitoRecuperoAttributi.setDetails("Invalid audience");	
			}
		}
		
		if(
			esitoRecuperoAttributi.isValido() &&
			(esitoRecuperoAttributi.getInformazioniAttributi().getAttributes()==null || esitoRecuperoAttributi.getInformazioniAttributi().getAttributes().isEmpty())
			) {
			esitoRecuperoAttributi.setNoCache(!policyAttributeAuthority.isSaveErrorInCache());
			esitoRecuperoAttributi.setDetails("No attributes retrieved");
		}
		
		if(!esitoRecuperoAttributi.isValido()) {
			esitoRecuperoAttributi.setNoCache(!saveErrorInCache);
		}
	}
	
	private static HttpResponse http(Logger log, PolicyAttributeAuthority policyAttributeAuthority,
			IProtocolFactory<?> protocolFactory,
			AttributeAuthorityDynamicParameters dynamicParameters,
			String request,
			IState state, boolean delegata, String idModulo, PortaApplicativa pa, PortaDelegata pd,
			IDSoggetto idDominio, IDServizio idServizio,
			RequestInfo requestInfo) throws Exception {
		
		// *** Raccola Parametri ***
		
		String endpoint = dynamicParameters.getEndpoint();
		HttpRequestMethod httpMethod = policyAttributeAuthority.getRequestHttpMethod();
		
		
		// Nell'endpoint config ci finisce i timeout e la configurazione proxy
		Properties endpointConfig = policyAttributeAuthority.getProperties().get(Costanti.POLICY_ENDPOINT_CONFIG);
		
		boolean https = policyAttributeAuthority.isEndpointHttps();
		boolean httpsClient = false;
		Properties sslConfig = null;
		Properties sslClientConfig = null;
		if(https) {
			sslConfig = policyAttributeAuthority.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
			httpsClient = policyAttributeAuthority.isHttpsAuthentication();
			if(httpsClient) {
				sslClientConfig = policyAttributeAuthority.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
			}
		}
		
		boolean basic = policyAttributeAuthority.isBasicAuthentication();
		String username = null;
		String password = null;
		if(basic) {
			username = dynamicParameters.getBasicUsername();
			password = dynamicParameters.getBasicPassword();
		}
		
		boolean bearer = policyAttributeAuthority.isBearerAuthentication();
		String bearerToken = null;
		if(bearer) {
			bearerToken = dynamicParameters.getBearerToken();
		}
		
		
		
		// *** Definizione Endpoint ***
		
		ConnettoreMsg connettoreMsg = new ConnettoreMsg();
		ConnettoreBaseHTTP connettore = null;
		if(https) {
			connettoreMsg.setTipoConnettore(TipiConnettore.HTTPS.getNome());
			connettore = new ConnettoreHTTPS();
		}
		else {
			connettoreMsg.setTipoConnettore(TipiConnettore.HTTP.getNome());
			connettore = new ConnettoreHTTP();
		}
		connettoreMsg.setIdModulo(idModulo);
		connettoreMsg.setState(state);
		PolicyTimeoutConfig policyConfig = new PolicyTimeoutConfig();
		policyConfig.setAttributeAuthority(policyAttributeAuthority.getName());
		connettoreMsg.setPolicyTimeoutConfig(policyConfig);
		
		ForwardProxy forwardProxy = null;
		ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
		if(configurazionePdDManager.isForwardProxyEnabled(requestInfo)) {
			try {
				IDGenericProperties policy = new IDGenericProperties();
				policy.setTipologia(CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY);
				policy.setNome(policyAttributeAuthority.getName());
				if(delegata) {
					forwardProxy = configurazionePdDManager.getForwardProxyConfigFruizione(idDominio, idServizio, policy, requestInfo);
				}
				else {
					forwardProxy = configurazionePdDManager.getForwardProxyConfigErogazione(idDominio, idServizio, policy, requestInfo);
				}
			}catch(Exception e) {
				throw new TokenException(GestoreToken.getMessageErroreGovWayProxy(e),e);
			}
		}
		if(forwardProxy!=null && forwardProxy.isEnabled() && forwardProxy.getConfigToken()!=null && forwardProxy.getConfigToken().isAttributeAuthorityEnabled()) {
			connettoreMsg.setForwardProxy(forwardProxy);
		}
		
		connettore.setForceDisable_proxyPassReverse(true);
		connettore.init(dynamicParameters.getPddContext(), protocolFactory);
		connettore.setRegisterSendIntoContext(false);
		
		if(basic){
			InvocazioneCredenziali credenziali = new InvocazioneCredenziali();
			credenziali.setUser(username);
			credenziali.setPassword(password);
			connettoreMsg.setCredenziali(credenziali);
		}
		
		connettoreMsg.setConnectorProperties(new java.util.HashMap<>());
		connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_LOCATION, endpoint);
		OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
		boolean debug = properties.isGestioneAttributeAuthority_debug();	
		if(debug) {
			connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_DEBUG, true+"");
		}
		connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_HTTP_DATA_TRANSFER_MODE, TransferLengthModes.CONTENT_LENGTH.getNome());
		GestoreToken.addProperties(connettoreMsg, endpointConfig);
		if(https) {
			GestoreToken.addProperties(connettoreMsg, sslConfig);
			if(httpsClient) {
				GestoreToken.addProperties(connettoreMsg, sslClientConfig);
			}
		}
		
		byte[] content = null;
		
		TransportRequestContext transportRequestContext = new TransportRequestContext(log);
		transportRequestContext.setRequestType(httpMethod.name());
		transportRequestContext.setHeaders(new HashMap<>());
		if(bearer) {
			String authorizationHeader = HttpConstants.AUTHORIZATION_PREFIX_BEARER+bearerToken;
			TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.AUTHORIZATION, authorizationHeader);
		}
		
		
		// *** request *** 
		String contentType = policyAttributeAuthority.getRequestContentType();
		if(contentType!=null && !"".equals(contentType)) {
			transportRequestContext.removeHeader(HttpConstants.CONTENT_TYPE);
			TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.CONTENT_TYPE, contentType);
		}
		
		String requestPayload = request;
		if(policyAttributeAuthority.isRequestJws()) {
			requestPayload = signAAJwt(policyAttributeAuthority, request, contentType, requestInfo);
		}
		if(policyAttributeAuthority.isRequestPositionBearer()) {
			if(transportRequestContext.getHeaders()==null) {
				transportRequestContext.setHeaders(new HashMap<>());
			}
			String authorizationHeader = HttpConstants.AUTHORIZATION_PREFIX_BEARER+requestPayload;
			TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.AUTHORIZATION, authorizationHeader);
		}
		else if(policyAttributeAuthority.isRequestPositionHeader()) {
			if(transportRequestContext.getHeaders()==null) {
				transportRequestContext.setHeaders(new HashMap<>());
			}
			String headerName = policyAttributeAuthority.getRequestPositionHeaderName();
			TransportUtils.setHeader(transportRequestContext.getHeaders(),headerName, requestPayload);
		}
		else if(policyAttributeAuthority.isRequestPositionQuery()) {
			if(transportRequestContext.getParameters()==null) {
				transportRequestContext.setParameters(new HashMap<>());
			}
			String queryParameterName = policyAttributeAuthority.getRequestPositionQueryParameterName();
			TransportUtils.setParameter(transportRequestContext.getParameters(), queryParameterName, requestPayload);
		}
		else if(policyAttributeAuthority.isRequestPositionPayload()) {
			content = requestPayload.getBytes();
		}
		
			
		OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(MessageType.BINARY, transportRequestContext, content);
		OpenSPCoop2Message msg = pr.getMessage_throwParseException();
		connettoreMsg.setRequestMessage(msg);
		connettoreMsg.setGenerateErrorWithConnectorPrefix(false);
		connettore.setHttpMethod(msg);
		connettore.setPa(pa);
		connettore.setPd(pd);
		
		ResponseCachingConfigurazione responseCachingConfigurazione = new ResponseCachingConfigurazione();
		responseCachingConfigurazione.setStato(StatoFunzionalita.DISABILITATO);
		String prefixConnettore = "[EndpointAttributeAuthority: "+endpoint+"] ";
		if(endpointConfig.containsKey(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME)) {
			String hostProxy = endpointConfig.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME);
			String portProxy = endpointConfig.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT);
			prefixConnettore = prefixConnettore+GestoreToken.getMessageViaProxy(hostProxy, portProxy);
		}
		boolean send = connettore.send(responseCachingConfigurazione, connettoreMsg);
		if(!send) {
			if(connettore.getEccezioneProcessamento()!=null) {
				throw new TokenException(prefixConnettore+connettore.getErrore(), connettore.getEccezioneProcessamento());
			}
			else {
				throw new TokenException(prefixConnettore+connettore.getErrore());
			}
		}
		
		OpenSPCoop2Message msgResponse = connettore.getResponse();
		ByteArrayOutputStream bout = null;
		if(msgResponse!=null) {
			bout = new ByteArrayOutputStream();
			if(msgResponse!=null) {
				msgResponse.writeTo(bout, true);
			}
			bout.flush();
			bout.close();
		}
		
		HttpResponse httpResponse = new HttpResponse();
		httpResponse.setResultHTTPOperation(connettore.getCodiceTrasporto());
		
		if(connettore.getCodiceTrasporto() >= 200 &&  connettore.getCodiceTrasporto() < 299) {
			String msgSuccess = prefixConnettore+GestoreToken.getMessageConnettoreConnessioneSuccesso(connettore);
			if(bout!=null && bout.size()>0) {
				log.debug(msgSuccess);
				httpResponse.setContent(bout.toByteArray());
				return httpResponse;
			}
			else {
				throw new TokenException(msgSuccess+GestoreToken.CONNETTORE_RISPOSTA_NON_PERVENUTA);
			}
		}
		else {
			String msgError = prefixConnettore+GestoreToken.getMessageConnettoreConnessioneErrore(connettore);
			if(bout!=null && bout.size()>0) {
				String e = msgError+": "+bout.toString();
				log.debug(e);
				httpResponse.setContent(bout.toByteArray());
				return httpResponse;
			}
			else {
				log.error(msgError);
				throw new TokenException(msgError);
			}
		}
		
		
	}
	
	private static String signAAJwt(PolicyAttributeAuthority policyAttributeAuthority, String payload, String contentType,
			RequestInfo requestInfo) throws TokenException, SecurityException, UtilsException {
		
		String signAlgo = policyAttributeAuthority.getRequestJwtSignAlgorithm();
		if(signAlgo==null) {
			throw new TokenException("SignAlgorithm undefined");
		}
		
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
		
		String keystoreType = policyAttributeAuthority.getRequestJwtSignKeystoreType();
		if(keystoreType==null) {
			throw new TokenException("JWT Signature keystore type undefined");
		}
		
		String keystoreFile = null;
		String keystoreFilePublicKey = null;
		String keyPairAlgorithm = null;
		if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType)) {
			keystoreFile = policyAttributeAuthority.getRequestJwtSignKeystoreFile();
			if(keystoreFile==null) {
				throw new TokenException("JWT Signature private key file undefined");
			}
			keystoreFilePublicKey = policyAttributeAuthority.getRequestJwtSignKeystoreFilePublicKey();
			if(keystoreFilePublicKey==null) {
				throw new TokenException("JWT Signature public key file undefined");
			}
			keyPairAlgorithm = policyAttributeAuthority.getRequestJwtSignKeystoreFileAlgorithm();
			if(keyPairAlgorithm==null) {
				throw new TokenException("JWT Signature key pair algorithm undefined");
			}
		}
		else {
			keystoreFile = policyAttributeAuthority.getRequestJwtSignKeystoreFile();
			if(keystoreFile==null) {
				throw new TokenException("JWT Signature keystore file undefined");
			}
		}
		
		
		

		String keystorePassword = policyAttributeAuthority.getRequestJwtSignKeystorePassword();
		if(keystorePassword==null && 
				!SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreType) && 
				!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType)) {
			throw new TokenException("JWT Signature keystore password undefined");
		}
		
		String keyAlias = policyAttributeAuthority.getRequestJwtSignKeyAlias();
		if(keyAlias==null && 
				!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType)) {
			throw new TokenException(GestoreToken.KEY_ALIAS_UNDEFINED);
		}
		
		String keyPassword = policyAttributeAuthority.getRequestJwtSignKeyPassword();
		if(keyPassword==null && 
				!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType)) {
			throw new TokenException(GestoreToken.KEY_PASSWORD_UNDEFINED);
		}
				
		KeyStore ks = null;
		KeyPairStore keyPairStore = null;
		JWKSetStore jwtStore = null;
		if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType)) {
			keyPairStore = GestoreKeystoreCache.getKeyPairStore(requestInfo, keystoreFile, keystoreFilePublicKey, keyPassword, keyPairAlgorithm);
		}
		else if(SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreType)) {
			jwtStore = GestoreKeystoreCache.getJwkSetStore(requestInfo, keystoreFile);
		}
		else {
			MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(requestInfo, keystoreFile, keystoreType, keystorePassword);
			if(merlinKs==null) {
				throw new TokenException("Accesso al keystore '"+keystoreFile+"' non riuscito");
			}
			ks = merlinKs.getKeyStore();
		}
		
		JwtHeaders jwtHeaders = new JwtHeaders();
		if(policyAttributeAuthority.isRequestJwtSignIncludeKeyIdWithKeyAlias()) {
			jwtHeaders.setKid(keyAlias);
		}
		else if(policyAttributeAuthority.isRequestJwtSignIncludeKeyIdCustom()) {
			jwtHeaders.setKid(policyAttributeAuthority.getRequestJwtSignIncludeKeyIdCustom());
		}
		if(policyAttributeAuthority.isRequestJwtSignIncludeX509Cert()) {
			jwtHeaders.setAddX5C(true);
		}
		String url = policyAttributeAuthority.getRequestJwtSignIncludeX509URL();
		if(url!=null && !"".equals(url)) {
			try {
				jwtHeaders.setX509Url(new URI(url));
			}catch(Exception e) {
				throw new TokenException(e.getMessage(),e);
			}
		}
		if(policyAttributeAuthority.isRequestJwtSignIncludeX509CertSha1()) {
			jwtHeaders.setX509IncludeCertSha1(true);
		}
		if(policyAttributeAuthority.isRequestJwtSignIncludeX509CertSha256()) {
			jwtHeaders.setX509IncludeCertSha256(true);
		}
		if(policyAttributeAuthority.isRequestJwtSignJoseContentType() &&
			contentType!=null && !"".equals(contentType)) {
			jwtHeaders.setContentType(contentType);
		}
		String type = policyAttributeAuthority.getRequestJwtSignJoseType();
		if(type!=null && !"".equals(type) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(type)) { // funzionalita' undefined undocumented
			jwtHeaders.setType(type);
		}
		if(ks!=null) {
			Certificate cert = ks.getCertificate(keyAlias);
			if(cert instanceof X509Certificate) {
				jwtHeaders.addX509cert((X509Certificate)cert);
			}
		}
		
		JsonSignature jsonSignature = null;
		if(keyPairStore!=null || jwtStore!=null) {
			JsonWebKeys jwk = null;
			if(keyPairStore!=null) {
				jwk = keyPairStore.getJwkSet().getJsonWebKeys();
				keyAlias = keyPairStore.getJwkSetKid();
			}
			else {
				jwk = jwtStore.getJwkSet().getJsonWebKeys();
			}
			jsonSignature = new JsonSignature(jwk, false, keyAlias, signAlgo, jwtHeaders, options);
		}
		else {
			jsonSignature = new JsonSignature(ks, false, keyAlias,  keyPassword, signAlgo, jwtHeaders, options);
		}
		
		return jsonSignature.sign(payload);
		
	}
	
}
