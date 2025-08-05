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
package org.openspcoop2.pdd.core.token;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.rest.OpenSPCoop2Message_binary_impl;
import org.openspcoop2.message.utils.WWWAuthenticateErrorCode;
import org.openspcoop2.message.utils.WWWAuthenticateGenerator;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.ForwardProxy;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.PDNDResolver;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreBaseHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreHTTPS;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.controllo_traffico.PolicyTimeoutConfig;
import org.openspcoop2.pdd.core.dynamic.DynamicException;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.token.pa.DatiInvocazionePortaApplicativa;
import org.openspcoop2.pdd.core.token.pa.EsitoDynamicDiscoveryPortaApplicativa;
import org.openspcoop2.pdd.core.token.pa.EsitoGestioneTokenPortaApplicativa;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.pdd.core.token.parser.IDynamicDiscoveryParser;
import org.openspcoop2.pdd.core.token.parser.ITokenParser;
import org.openspcoop2.pdd.core.token.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.pdd.core.token.pd.EsitoDynamicDiscoveryPortaDelegata;
import org.openspcoop2.pdd.core.token.pd.EsitoGestioneTokenPortaDelegata;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.PDNDTokenInfo;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.RestMessageSecurityToken;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.CRLCertstore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.keystore.cache.GestoreOCSPResource;
import org.openspcoop2.security.keystore.cache.GestoreOCSPValidator;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.LoggerBuffer;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.security.CertificateValidityCheck;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWEOptions;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JWTOptions;
import org.openspcoop2.utils.security.JsonDecrypt;
import org.openspcoop2.utils.security.JsonEncrypt;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.JsonVerifySignature;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.IOCSPValidator;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**     
 * GestoreTokenValidazioneUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreTokenValidazioneUtilities {

	private GestoreTokenValidazioneUtilities() {}
	
	private static final String PREFIX_ERROR_CONNESSIONE = "(Errore di Connessione) ";
	
	
	
	// ********* [VALIDAZIONE-TOKEN] DYNAMIC DISCOVERY ****************** */
		
	static EsitoDynamicDiscovery dynamicDiscoveryEngine(Logger log, AbstractDatiInvocazione datiInvocazione, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			String token, boolean portaDelegata,
			Busta busta, IDSoggetto idDominio, IDServizio idServizio) {
		
		
		EsitoDynamicDiscovery esitoGestioneToken = null;
		if(portaDelegata) {
			esitoGestioneToken = new EsitoDynamicDiscoveryPortaDelegata();
		}
		else {
			esitoGestioneToken = new EsitoDynamicDiscoveryPortaApplicativa();
		}
		
		esitoGestioneToken.setTokenInternalError();
		esitoGestioneToken.setToken(token);
		
		try{
			PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
    		
    		String detailsError = null;
    		DynamicDiscovery dd = null;
    		Exception eProcess = null;
			
			IDynamicDiscoveryParser ddParser = policyGestioneToken.getDynamicDiscoveryParser();
			
			PortaApplicativa pa = null;
			PortaDelegata pd = null;
			if(datiInvocazione instanceof DatiInvocazionePortaApplicativa) {
				pa = ((DatiInvocazionePortaApplicativa)datiInvocazione).getPa();
			}
			else if(datiInvocazione instanceof DatiInvocazionePortaDelegata) {
				pd = ((DatiInvocazionePortaDelegata)datiInvocazione).getPd();
			}
			
			HttpResponse httpResponse = null;
			Integer httpResponseCode = null;
			byte[] risposta = null;
			try {
				httpResponse = GestoreTokenValidazioneUtilities.http(log, policyGestioneToken, HTTP_TYPE.DYNAMIC_DISCOVERY, 
						null, token,
						pddContext, protocolFactory,
						datiInvocazione.getState(), portaDelegata, datiInvocazione.getIdModulo(), pa, pd,
						idDominio, idServizio,
						busta, datiInvocazione.getRequestInfo());
				risposta = httpResponse.getContent();
				httpResponseCode = httpResponse.getResultHTTPOperation();
			}catch(Exception e) {
				detailsError = PREFIX_ERROR_CONNESSIONE+ e.getMessage();
				eProcess = e;
			}
			
			if(detailsError==null) {
				try {
					dd = new DynamicDiscovery(httpResponseCode, policyGestioneToken.getDynamicDiscoveryType(), new String(risposta),ddParser);
				}catch(Exception e) {
					detailsError = "Risposta del servizio 'Dynamic Discovery' non valida: "+e.getMessage();
					eProcess = e;
				}
			}
    		  		
    		if(dd!=null && dd.isValid()) {
    			esitoGestioneToken.setTokenValido();
    			esitoGestioneToken.setDynamicDiscovery(dd);
    			esitoGestioneToken.setNoCache(false);
			}
    		else {
    			esitoGestioneToken.setTokenValidazioneFallita();
    			esitoGestioneToken.setNoCache(!policyGestioneToken.isIntrospectionSaveErrorInCache());
    			esitoGestioneToken.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoGestioneToken.setDetails(detailsError);	
				}
				else {
					esitoGestioneToken.setDetails(GestoreToken.TOKEN_NON_VALIDO);	
				}
    			
    			// comunque lo aggiungo per essere consultabile nei casi di errore se una connessione http è terminata
    			if(OpenSPCoop2Properties.getInstance().isGestioneTokenSaveSourceTokenInfo() && httpResponseCode!=null) {
    				dd = new DynamicDiscovery(esitoGestioneToken.getDetails(), httpResponseCode, risposta, policyGestioneToken.getDynamicDiscoveryType());
    				esitoGestioneToken.setDynamicDiscovery(dd);
    			}
    			
    			if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
    				esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoGestioneToken.getDetails()));   			
    			}
    			else {
    				esitoGestioneToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoGestioneToken.getDetails()));
    			} 	
    		}
    		
		}catch(Exception e){
			esitoGestioneToken.setTokenInternalError();
			esitoGestioneToken.setDetails(e.getMessage());
			esitoGestioneToken.setEccezioneProcessamento(e);
    	}
		
		return esitoGestioneToken;
	}
	
	
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] VALIDAZIONE JWT TOKEN ****************** */
	
	static EsitoGestioneToken validazioneJWTTokenEngine(Logger log, AbstractDatiInvocazione datiInvocazione, EsitoPresenzaToken esitoPresenzaToken, DynamicDiscovery dynamicDiscovery, 
			String token, boolean portaDelegata, PdDContext pddContext,
			Busta busta, IDSoggetto idDominio, IDServizio idServizio) {
		EsitoGestioneToken esitoGestioneToken = null;
		if(portaDelegata) {
			esitoGestioneToken = new EsitoGestioneTokenPortaDelegata();
		}
		else {
			esitoGestioneToken = new EsitoGestioneTokenPortaApplicativa();
		}
		
		esitoGestioneToken.setTokenInternalError();
		esitoGestioneToken.setToken(token);
		
		try{
			PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
    		String tokenType = policyGestioneToken.getTipoToken();
    		
    		String detailsError = null;
			InformazioniToken informazioniToken = null;
			Exception eProcess = null;
			RestMessageSecurityToken restSecurityToken = null;
			
			ITokenParser tokenParser = policyGestioneToken.getValidazioneJWTTokenParser();
			
    		if(Costanti.POLICY_TOKEN_TYPE_JWS.equals(tokenType)) {
    			// JWS Compact   			
    			JsonVerifySignature jsonCompactVerify = null;
    			try {
    				jsonCompactVerify = getJsonVerifySignatureJWS(log, pddContext, datiInvocazione, policyGestioneToken, dynamicDiscovery, 
    						busta, idDominio, idServizio,
    						portaDelegata);
    				
    				if(jsonCompactVerify.verify(token)) {
    					informazioniToken = new InformazioniToken(SorgenteInformazioniToken.JWT,jsonCompactVerify.getDecodedPayload(),tokenParser);
    					if( pddContext!=null ) {
    						restSecurityToken = new RestMessageSecurityToken();
    						if(jsonCompactVerify.getX509Certificate()!=null) {
    							restSecurityToken.setCertificate(new CertificateInfo(jsonCompactVerify.getX509Certificate(), "access_token"));
    						}
    						if(jsonCompactVerify.getKid()!=null) {
    							restSecurityToken.setKid(jsonCompactVerify.getKid());
    						}
    						restSecurityToken.setToken(token);
    						if(esitoPresenzaToken!=null) {
    							restSecurityToken.setHttpHeaderName(esitoPresenzaToken.getHeaderHttp());
    							restSecurityToken.setQueryParameterName(esitoPresenzaToken.getPropertyUrl());
    							restSecurityToken.setFormParameterName(esitoPresenzaToken.getPropertyFormBased());
    						}
    					}
    					
    					try {
    						validazioneInformazioniTokenHeader(jsonCompactVerify.getDecodedHeader(), policyGestioneToken);
    					}catch(Exception e) {
    						informazioniToken.setValid(false);
    						log.debug(GestoreToken.getMessageTokenNonValido(e),e);
    	    				detailsError = GestoreToken.getMessageTokenNonValido(e);
    	    				eProcess = e;
    					}
    				}
    				else {
    					detailsError = GestoreToken.TOKEN_NON_VALIDO;
    				}
    			}catch(Exception e) {
    				log.debug(GestoreToken.getMessageTokenNonValido(e),e);
    				detailsError = GestoreToken.getMessageTokenNonValido(e);
    				eProcess = e;
    			}
    		}
    		else {
    			// JWE Compact
    			JsonDecrypt jsonDecrypt = null;
    			try {
    				jsonDecrypt = getJsonDecrypt(log, pddContext, datiInvocazione, policyGestioneToken, dynamicDiscovery,
    						busta, idDominio, idServizio,
    						portaDelegata);
    				jsonDecrypt.decrypt(token);
    				informazioniToken = new InformazioniToken(SorgenteInformazioniToken.JWT,jsonDecrypt.getDecodedPayload(),tokenParser);
    				if( pddContext!=null ) {
						restSecurityToken = new RestMessageSecurityToken();
						if(jsonDecrypt.getX509Certificate()!=null) {
							restSecurityToken.setCertificate(new CertificateInfo(jsonDecrypt.getX509Certificate(), "access_token"));
						}
						if(jsonDecrypt.getKid()!=null) {
							restSecurityToken.setKid(jsonDecrypt.getKid());
						}
						restSecurityToken.setJweDecodedPayload(jsonDecrypt.getDecodedPayload());
						restSecurityToken.setToken(token);
						if(esitoPresenzaToken!=null) {
							restSecurityToken.setHttpHeaderName(esitoPresenzaToken.getHeaderHttp());
							restSecurityToken.setQueryParameterName(esitoPresenzaToken.getPropertyUrl());
							restSecurityToken.setFormParameterName(esitoPresenzaToken.getPropertyFormBased());
						}
					}
    			}catch(Exception e) {
    				log.debug(GestoreToken.getMessageTokenNonValido(e),e);
    				detailsError = GestoreToken.getMessageTokenNonValido(e);
    				eProcess = e;
    			}
    		}
    		  		
    		if(informazioniToken!=null && informazioniToken.isValid()) {
    			esitoGestioneToken.setTokenValido();
    			esitoGestioneToken.setInformazioniToken(informazioniToken);
    			esitoGestioneToken.setNoCache(false);
    			esitoGestioneToken.setRestSecurityToken(restSecurityToken);
			}
    		else {
    			esitoGestioneToken.setTokenValidazioneFallita();
    			esitoGestioneToken.setNoCache(!policyGestioneToken.isValidazioneJWTSaveErrorInCache());
    			esitoGestioneToken.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoGestioneToken.setDetails(detailsError);	
				}
				else {
					esitoGestioneToken.setDetails(GestoreToken.TOKEN_NON_VALIDO);	
				}
    			
    			// comunque lo aggiungo per essere consultabile nei casi di errore
    			if(OpenSPCoop2Properties.getInstance().isGestioneTokenSaveTokenInfoValidationFailed()) {
    				informazioniToken = new InformazioniToken(esitoGestioneToken.getDetails(), SorgenteInformazioniToken.JWT, token);
    				esitoGestioneToken.setInformazioniToken(informazioniToken);
    			}
    			
    			if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
    				esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoGestioneToken.getDetails()));   			
    			}
    			else {
    				esitoGestioneToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoGestioneToken.getDetails()));
    			}
    		}
    		
		}catch(Exception e){
			esitoGestioneToken.setTokenInternalError();
			esitoGestioneToken.setDetails(e.getMessage());
			esitoGestioneToken.setEccezioneProcessamento(e);
    	}
		
		return esitoGestioneToken;
	}
	
	private static JsonVerifySignature getJsonVerifySignatureJWS(Logger log, Context context, AbstractDatiInvocazione datiInvocazione, PolicyGestioneToken policyGestioneToken, DynamicDiscovery dynamicDiscovery,
			Busta busta, IDSoggetto idDominio, IDServizio idServizio,
			boolean portaDelegata) throws TokenException, UtilsException, SecurityException {
		// JWS Compact   			
		JsonVerifySignature jsonCompactVerify = null;

		JWTOptions options = new JWTOptions(JOSESerialization.COMPACT);
		Properties p = policyGestioneToken.getProperties().get(Costanti.POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID);
		TokenUtilities.injectJOSEConfig(p, policyGestioneToken, dynamicDiscovery,  
				busta, idDominio, idServizio,
				context, log,
				datiInvocazione.getRequestInfo(), datiInvocazione.getState(), portaDelegata);
		
		// serve per leggere il keystore dalla cache
		TokenKeystoreInjectUtilities inject = new TokenKeystoreInjectUtilities(log, datiInvocazione.getRequestInfo() ,
				datiInvocazione.getRequestInfo()!=null ? datiInvocazione.getRequestInfo().getProtocolFactory() : null, 
				context, datiInvocazione.getState(), busta);
		if(datiInvocazione instanceof DatiInvocazionePortaApplicativa) {
			DatiInvocazionePortaApplicativa dati = (DatiInvocazionePortaApplicativa) datiInvocazione;
			inject.initTokenPolicyValidazioneJwt(policyGestioneToken.getName(), portaDelegata, dati.getPd(), dati.getPa(), p);
		}
		else {
			DatiInvocazionePortaDelegata dati = (DatiInvocazionePortaDelegata) datiInvocazione;
			inject.initTokenPolicyValidazioneJwt(policyGestioneToken.getName(), portaDelegata, dati.getPd(), null, p);
		}
		inject.inject(p);
		
		
		String aliasMode = p.getProperty(SecurityConstants.JOSE_KEYSTORE_KEY_ALIAS+".mode"); 
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
			/**options.setPermitUseHeaderX5T(aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5T256) || aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C_X5T256));*/
			options.setPermitUseHeaderX5T_256(aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5T256) || aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C_X5T256));
			options.setPermitUseHeaderKID(aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_KID));
			options.setPermitUseHeaderX5U(aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5U));
									
			if(p.containsKey(SecurityConstants.JOSE_KEYSTORE)) {
				Object oKeystore = p.get(SecurityConstants.JOSE_KEYSTORE);
				if(oKeystore instanceof java.security.KeyStore) {
					java.security.KeyStore keystore = (java.security.KeyStore) oKeystore;
					jsonCompactVerify = new JsonVerifySignature(keystore, options);
					
					CertificateValidityCheck validityCheck = OpenSPCoop2Properties.getInstance().getGestioneTokenValidityCheck();
					List<Proprieta> proprieta = null;
		    		if(datiInvocazione instanceof DatiInvocazionePortaApplicativa) {
		    			DatiInvocazionePortaApplicativa dati = (DatiInvocazionePortaApplicativa)datiInvocazione;
		    			if(dati.getPa()!=null) {
		    				proprieta = dati.getPa().getProprietaList();
		    			}
		    			else if(dati.getPd()!=null) {
		    				proprieta = dati.getPd().getProprietaList();
		    			}
		    		}
		    		else if(datiInvocazione instanceof DatiInvocazionePortaDelegata) {
		    			DatiInvocazionePortaDelegata dati = (DatiInvocazionePortaDelegata)datiInvocazione;
		    			if(dati.getPd()!=null) {
		    				proprieta = dati.getPd().getProprietaList();
		    			}
		    		}
		    		validityCheck = CostantiProprieta.getTokenValidationCertificateValidityCheck(proprieta, validityCheck);
		    		jsonCompactVerify.setValidityCheck(validityCheck);
					
					String signatureOCSP = policyGestioneToken.getValidazioneJWTOcspPolicy();
					String signatureCRL = policyGestioneToken.getValidazioneJWTCrl();
					
					boolean crlByOcsp = false;
					if(signatureOCSP!=null && !"".equals(signatureOCSP)) {
						LoggerBuffer lb = new LoggerBuffer();
						lb.setLogDebug(log);
						lb.setLogError(log);
						GestoreOCSPResource ocspResourceReader = new GestoreOCSPResource(datiInvocazione.getRequestInfo());
						IOCSPValidator ocspValidator = null;
						try {
							org.openspcoop2.utils.certificate.KeyStore trustStore = new org.openspcoop2.utils.certificate.KeyStore(keystore);
							ocspValidator = new GestoreOCSPValidator(datiInvocazione.getRequestInfo(), lb, 
									trustStore,
									signatureCRL, 
									signatureOCSP, 
									ocspResourceReader);
						}catch(Exception e){
							throw new TokenException("ocsp initialization (policy:'"+signatureOCSP+"') failed: "+e.getMessage(),e);
						}
						jsonCompactVerify.setOcspValidatorX509(ocspValidator);
						GestoreOCSPValidator gOcspValidator = (GestoreOCSPValidator) ocspValidator;
						if(gOcspValidator.getOcspConfig()!=null) {
							crlByOcsp = gOcspValidator.getOcspConfig().isCrl();
						}
					}
					
					if(signatureCRL!=null && !"".equals(signatureCRL) && !crlByOcsp) {
						CRLCertstore crlCertstore = GestoreKeystoreCache.getCRLCertstore(datiInvocazione.getRequestInfo(), signatureCRL);
						if(crlCertstore==null) {
							throw new TokenException("Process CRL '"+signatureCRL+"' failed");
						}
						jsonCompactVerify.setCrlX509(crlCertstore.getCertStore());
					}

				}
			}
			else if(p.containsKey(SecurityConstants.JOSE_KEYSTORE_JWKSET)) {
				Object oKeystore = p.get(SecurityConstants.JOSE_KEYSTORE_JWKSET);
				if(oKeystore instanceof String) {
					String keystore = (String) oKeystore;
					JsonWebKeys jwksKeystore = new JWKSet(keystore).getJsonWebKeys();
					jsonCompactVerify = new JsonVerifySignature(jwksKeystore, options);
				}
			}
		}
		    				
		if(jsonCompactVerify==null &&
			p.containsKey(SecurityConstants.JOSE_KEYSTORE_KEY_ALIAS)) {
			String alias = p.getProperty(SecurityConstants.JOSE_KEYSTORE_KEY_ALIAS);
			if(alias!=null && 
					(
							alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5C)
							||
							alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5T)
							||
							alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5C_X5T)
					)
				&&
				(p.containsKey(SecurityConstants.JOSE_KEYSTORE)) // backward compatibility
				){
				Object oKeystore = p.get(SecurityConstants.JOSE_KEYSTORE);
				if(oKeystore instanceof java.security.KeyStore) {
					java.security.KeyStore keystore = (java.security.KeyStore) oKeystore;
					options.setPermitUseHeaderX5C(alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5C) || alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5C_X5T));
					options.setPermitUseHeaderX5T(alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5T) || alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5C_X5T));
					options.setPermitUseHeaderX5T_256(alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5T) || alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5C_X5T));
					jsonCompactVerify = new JsonVerifySignature(keystore, options);
				}
			}	
		}
		
		if(jsonCompactVerify==null) {
			jsonCompactVerify = new JsonVerifySignature(p, options);
		}
		
		jsonCompactVerify.setJksPasswordRequired(DBUtils.isTruststoreJksPasswordRequired());
		jsonCompactVerify.setPkcs12PasswordRequired(DBUtils.isTruststorePkcs12PasswordRequired());
		
		return jsonCompactVerify;
	}
	
	private static JsonDecrypt getJsonDecrypt(Logger log, Context context, AbstractDatiInvocazione datiInvocazione, PolicyGestioneToken policyGestioneToken, DynamicDiscovery dynamicDiscovery,
			Busta busta, IDSoggetto idDominio, IDServizio idServizio,
			boolean portaDelegata) throws TokenException, UtilsException, SecurityException {
		JsonDecrypt jsonDecrypt = null;
		JWTOptions options = new JWTOptions(JOSESerialization.COMPACT);
		Properties p = policyGestioneToken.getProperties().get(Costanti.POLICY_VALIDAZIONE_JWE_DECRYPT_PROP_REF_ID);
		TokenUtilities.injectJOSEConfig(p, policyGestioneToken, dynamicDiscovery,  
				busta, idDominio, idServizio,
				context, log,
				datiInvocazione.getRequestInfo(), datiInvocazione.getState(), portaDelegata);
		
		// serve per leggere il keystore dalla cache
		TokenKeystoreInjectUtilities inject = new TokenKeystoreInjectUtilities(log, datiInvocazione.getRequestInfo() ,
				datiInvocazione.getRequestInfo()!=null ? datiInvocazione.getRequestInfo().getProtocolFactory() : null, 
				context, datiInvocazione.getState(), busta);
		if(datiInvocazione instanceof DatiInvocazionePortaApplicativa) {
			DatiInvocazionePortaApplicativa dati = (DatiInvocazionePortaApplicativa) datiInvocazione;
			inject.initTokenPolicyValidazioneJwt(policyGestioneToken.getName(), portaDelegata, dati.getPd(), dati.getPa(), p);
		}
		else {
			DatiInvocazionePortaDelegata dati = (DatiInvocazionePortaDelegata) datiInvocazione;
			inject.initTokenPolicyValidazioneJwt(policyGestioneToken.getName(), portaDelegata, dati.getPd(), null, p);
		}
		inject.inject(p);
		
		jsonDecrypt = new JsonDecrypt(p, options, 
				DBUtils.isKeystoreJksPasswordRequired(), 
				DBUtils.isKeystorePkcs12PasswordRequired());
		
		return jsonDecrypt;
	}
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] INTROSPECTION TOKEN ****************** */
	
	static EsitoGestioneToken introspectionTokenEngine(Logger log, AbstractDatiInvocazione datiInvocazione,  
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			DynamicDiscovery dynamicDiscovery, 
			String token, boolean portaDelegata,
			Busta busta, IDSoggetto idDominio, IDServizio idServizio) {
		EsitoGestioneToken esitoGestioneToken = null;
		if(portaDelegata) {
			esitoGestioneToken = new EsitoGestioneTokenPortaDelegata();
		}
		else {
			esitoGestioneToken = new EsitoGestioneTokenPortaApplicativa();
		}
		
		esitoGestioneToken.setTokenInternalError();
		esitoGestioneToken.setToken(token);
		
		try{
			PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
    		
    		String detailsError = null;
			InformazioniToken informazioniToken = null;
			Exception eProcess = null;
			
			ITokenParser tokenParser = policyGestioneToken.getIntrospectionTokenParser();
			
			PortaApplicativa pa = null;
			PortaDelegata pd = null;
			if(datiInvocazione instanceof DatiInvocazionePortaApplicativa) {
				pa = ((DatiInvocazionePortaApplicativa)datiInvocazione).getPa();
			}
			else if(datiInvocazione instanceof DatiInvocazionePortaDelegata) {
				pd = ((DatiInvocazionePortaDelegata)datiInvocazione).getPd();
			}
			
			HttpResponse httpResponse = null;
			Integer httpResponseCode = null;
			byte[] risposta = null;
			try {
				httpResponse = GestoreTokenValidazioneUtilities.http(log, policyGestioneToken, HTTP_TYPE.INTROSPECTION, 
						dynamicDiscovery, token,
						pddContext, protocolFactory,
						datiInvocazione.getState(), portaDelegata, datiInvocazione.getIdModulo(), pa, pd,
						idDominio, idServizio,
						busta, datiInvocazione.getRequestInfo());
				risposta = httpResponse.getContent();
				httpResponseCode = httpResponse.getResultHTTPOperation();
			}catch(Exception e) {
				detailsError = PREFIX_ERROR_CONNESSIONE+ e.getMessage();
				eProcess = e;
			}
			
			if(detailsError==null) {
				try {
					informazioniToken = new InformazioniToken(httpResponseCode, SorgenteInformazioniToken.INTROSPECTION, new String(risposta),tokenParser);
				}catch(Exception e) {
					detailsError = "Risposta del servizio di Introspection non valida: "+e.getMessage();
					eProcess = e;
				}
			}
    		  		
    		if(informazioniToken!=null && informazioniToken.isValid()) {
    			esitoGestioneToken.setTokenValido();
    			esitoGestioneToken.setInformazioniToken(informazioniToken);
    			esitoGestioneToken.setNoCache(false);
			}
    		else {
    			esitoGestioneToken.setTokenValidazioneFallita();
    			esitoGestioneToken.setNoCache(!policyGestioneToken.isIntrospectionSaveErrorInCache());
    			esitoGestioneToken.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoGestioneToken.setDetails(detailsError);	
				}
				else {
					esitoGestioneToken.setDetails(GestoreToken.TOKEN_NON_VALIDO);	
				}
    			
    			// comunque lo aggiungo per essere consultabile nei casi di errore se una connessione http è terminata
    			if(OpenSPCoop2Properties.getInstance().isGestioneTokenSaveSourceTokenInfo() && httpResponseCode!=null) {
    				informazioniToken = new InformazioniToken(esitoGestioneToken.getDetails(), httpResponseCode, risposta, SorgenteInformazioniToken.INTROSPECTION, token);
    				esitoGestioneToken.setInformazioniToken(informazioniToken);
    			}
    			
    			if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
    				esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoGestioneToken.getDetails()));   			
    			}
    			else {
    				esitoGestioneToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoGestioneToken.getDetails()));
    			} 	
    		}
    		
		}catch(Exception e){
			esitoGestioneToken.setTokenInternalError();
			esitoGestioneToken.setDetails(e.getMessage());
			esitoGestioneToken.setEccezioneProcessamento(e);
    	}
		
		return esitoGestioneToken;
	}
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] USER INFO TOKEN ****************** */
	
	static EsitoGestioneToken userInfoTokenEngine(Logger log, AbstractDatiInvocazione datiInvocazione, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			DynamicDiscovery dynamicDiscovery, 
			String token, boolean portaDelegata,
			Busta busta, IDSoggetto idDominio, IDServizio idServizio) {
		EsitoGestioneToken esitoGestioneToken = null;
		if(portaDelegata) {
			esitoGestioneToken = new EsitoGestioneTokenPortaDelegata();
		}
		else {
			esitoGestioneToken = new EsitoGestioneTokenPortaApplicativa();
		}
		
		esitoGestioneToken.setTokenInternalError();
		esitoGestioneToken.setToken(token);
		
		try{
			PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
    		
    		String detailsError = null;
			InformazioniToken informazioniToken = null;
			Exception eProcess = null;
			
			ITokenParser tokenParser = policyGestioneToken.getUserInfoTokenParser();
			
			PortaApplicativa pa = null;
			PortaDelegata pd = null;
			if(datiInvocazione instanceof DatiInvocazionePortaApplicativa) {
				pa = ((DatiInvocazionePortaApplicativa)datiInvocazione).getPa();
			}
			else if(datiInvocazione instanceof DatiInvocazionePortaDelegata) {
				pd = ((DatiInvocazionePortaDelegata)datiInvocazione).getPd();
			}
			
			HttpResponse httpResponse = null;
			Integer httpResponseCode = null;
			byte[] risposta = null;
			try {
				httpResponse = http(log, policyGestioneToken, HTTP_TYPE.USER_INFO, 
						dynamicDiscovery, token,
						pddContext, protocolFactory,
						datiInvocazione.getState(), portaDelegata, datiInvocazione.getIdModulo(), pa, pd,
						idDominio, idServizio,
						busta, datiInvocazione.getRequestInfo());
				risposta = httpResponse.getContent();
				httpResponseCode = httpResponse.getResultHTTPOperation();
			}catch(Exception e) {
				detailsError = PREFIX_ERROR_CONNESSIONE+ e.getMessage();
				eProcess = e;
			}
			
			if(detailsError==null) {
				try {
					informazioniToken = new InformazioniToken(httpResponseCode, SorgenteInformazioniToken.USER_INFO, new String(risposta),tokenParser);
				}catch(Exception e) {
					detailsError = "Risposta del servizio di UserInfo non valida: "+e.getMessage();
					eProcess = e;
				}
			}
    		  		
    		if(informazioniToken!=null && informazioniToken.isValid()) {
    			esitoGestioneToken.setTokenValido();
    			esitoGestioneToken.setInformazioniToken(informazioniToken);
    			esitoGestioneToken.setNoCache(false);
			}
    		else {
    			esitoGestioneToken.setTokenValidazioneFallita();
    			esitoGestioneToken.setNoCache(!policyGestioneToken.isUserInfoSaveErrorInCache());
    			esitoGestioneToken.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoGestioneToken.setDetails(detailsError);	
				}
				else {
					esitoGestioneToken.setDetails(GestoreToken.TOKEN_NON_VALIDO);	
				}
    			
    			// comunque lo aggiungo per essere consultabile nei casi di errore se una connessione http è terminata
    			if(OpenSPCoop2Properties.getInstance().isGestioneTokenSaveSourceTokenInfo() && httpResponseCode!=null) {
	    			informazioniToken = new InformazioniToken(esitoGestioneToken.getDetails(), httpResponseCode, risposta, SorgenteInformazioniToken.USER_INFO, token);
	    			esitoGestioneToken.setInformazioniToken(informazioniToken);
    			}
    			
    			if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
    				esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoGestioneToken.getDetails()));   			
    			}
    			else {
    				esitoGestioneToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoGestioneToken.getDetails()));
    			} 		
    		}
    		
		}catch(Exception e){
			esitoGestioneToken.setTokenInternalError();
			esitoGestioneToken.setDetails(e.getMessage());
			esitoGestioneToken.setEccezioneProcessamento(e);
    	}
		
		return esitoGestioneToken;
	}
	
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] FORWARD TOKEN ****************** */
	
	static boolean deleteTokenReceived(AbstractDatiInvocazione datiInvocazione, EsitoPresenzaToken esitoPresenzaToken,
			boolean trasparente, String forwardTrasparenteModeHeader, String forwardTrasparenteModeUrl) throws TokenException {
		
		PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
		
		boolean remove = false;
		if(esitoPresenzaToken.getHeaderHttp()!=null) {
			if( 
				(!policyGestioneToken.isForwardToken()) 
				||
				(!trasparente) 
				||
				(!esitoPresenzaToken.getHeaderHttp().equalsIgnoreCase(forwardTrasparenteModeHeader))
			){
				remove = true;
			}
			if(remove) {
				datiInvocazione.getMessage().getTransportRequestContext().removeHeader(esitoPresenzaToken.getHeaderHttp());
			}
		}
		else if(esitoPresenzaToken.getPropertyUrl()!=null) {
			if( 
				(!policyGestioneToken.isForwardToken()) 
				||	
				(!trasparente) 
				||
				(!esitoPresenzaToken.getPropertyUrl().equals(forwardTrasparenteModeUrl)) 
			){
				remove = true;
			}
			if(remove) {
				datiInvocazione.getMessage().getTransportRequestContext().removeParameter(esitoPresenzaToken.getPropertyUrl());
			}
		}
		else if(esitoPresenzaToken.getPropertyFormBased()!=null) {
			if(
				(!policyGestioneToken.isForwardToken()) 
				||
				(!trasparente) 
			){
				remove = true;
			}
			if(remove) {
				try {
					byte[] contenuto = datiInvocazione.getMessage().castAsRestBinary().getContent().getContent(); // dovrebbe essere un binary
					// MyVariableOne=ValueOne&MyVariableTwo=ValueTwo
					String contenutoAsString = new String(contenuto);
					String [] split = contenutoAsString.split("&");
					StringBuilder bf = new StringBuilder();
					for (int i = 0; i < split.length; i++) {
						String nameValue = split[i];
						if(nameValue.contains("=")) {
							String [] tmp = nameValue.split("=");
							String name = tmp[0];
							String value = tmp[1];
							if(!esitoPresenzaToken.getPropertyFormBased().equals(name)) {
								bf.append(name).append("=").append(value);
							}
						}
						else {
							if(bf.length()>0) {
								bf.append("&");
							}
							bf.append(nameValue);
						}
					}
					if(bf.length()>0) {
						byte [] newContenuto = bf.toString().getBytes();
						((OpenSPCoop2Message_binary_impl)datiInvocazione.getMessage().castAsRestBinary()).updateContent(newContenuto);
					}
					else {
						datiInvocazione.getMessage().castAsRestBinary().updateContent(null);
					}
				}catch(Exception e) {
					throw new TokenException(e.getMessage(),e);
				}
			}
		}
		return remove;
	}
	
	static void forwardTokenTrasparenteEngine(String token, EsitoPresenzaToken esitoPresenzaToken, TokenForward tokenForward,
			String forwardTrasparenteMode, String forwardTrasparenteModeHeader, String forwardTrasparenteModeUrl) throws TokenException {
		if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_AS_RECEIVED.equals(forwardTrasparenteMode)) {
			if(esitoPresenzaToken.getHeaderHttp()!=null) {
				if(HttpConstants.AUTHORIZATION.equals(esitoPresenzaToken.getHeaderHttp())) {
					TransportUtils.setHeader(tokenForward.getTrasporto(),HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+token);
				}
				else {
					TransportUtils.setHeader(tokenForward.getTrasporto(),esitoPresenzaToken.getHeaderHttp(), token);
				}
			}
			else if(esitoPresenzaToken.getPropertyUrl()!=null) {
				TransportUtils.setParameter(tokenForward.getUrl(),esitoPresenzaToken.getPropertyUrl(), token);
			}
			else if(esitoPresenzaToken.getPropertyFormBased()!=null) {
				throw new TokenException("Configurazione non supportata"); // non dovrebbe mai entrare in questo ramo poichè il token non viene eliminato
			}
		}
		else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_HEADER.equals(forwardTrasparenteMode)) {
			TransportUtils.setHeader(tokenForward.getTrasporto(),HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+token);
		}
		else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_URL.equals(forwardTrasparenteMode)) {
			TransportUtils.setParameter(tokenForward.getUrl(),Costanti.RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN, token);
		}
		else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER.equals(forwardTrasparenteMode)) {
			TransportUtils.setHeader(tokenForward.getTrasporto(),forwardTrasparenteModeHeader, token);
		}
		else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL.equals(forwardTrasparenteMode)) {
			TransportUtils.setParameter(tokenForward.getUrl(),forwardTrasparenteModeUrl, token);
		}
	}
	
	static void forwardInfomazioniRaccolteEngine(boolean portaDelegata, String idTransazione, TokenForward tokenForward,
			EsitoGestioneToken esitoValidazioneJWT, EsitoGestioneToken esitoIntrospection, EsitoGestioneToken esitoUserInfo,
			InformazioniToken informazioniTokenNormalizzate,
			String forwardInforRaccolteMode, Properties jwtSecurity, boolean encodeBase64,
			boolean forwardValidazioneJWT, String forwardValidazioneJWTMode, String forwardValidazioneJWTName,
			boolean forwardIntrospection, String forwardIntrospectionMode, String forwardIntrospectionName,
			boolean forwardUserInfo, String forwardUserInfoMode, String forwardUserInfoName) throws CoreException, UtilsException {
		
		if(informazioniTokenNormalizzate==null) {
			return; // può succedere nei casi warning only, significa che non vi sono token validi
		}
		
		if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_HEADERS.equals(forwardInforRaccolteMode) ||
				Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JSON.equals(forwardInforRaccolteMode) ||
				Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS.equals(forwardInforRaccolteMode)) {
			
			OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
			Map<String,String> headerNames = null;
			Map<String, Boolean> set = null;
			JSONUtils jsonUtils = null;
			ObjectNode jsonNode = null;
			boolean op2headers = Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_HEADERS.equals(forwardInforRaccolteMode);
			SimpleDateFormat sdf = null;
			if(op2headers) {
				headerNames = properties.getKeyValueGestioneTokenHeaderIntegrazioneTrasporto();
				if(portaDelegata) {
					set = properties.getKeyPDSetEnabledGestioneTokenHeaderIntegrazioneTrasporto();
				}
				else {
					set = properties.getKeyPASetEnabledGestioneTokenHeaderIntegrazioneTrasporto();
				}
				String pattern = properties.getGestioneTokenFormatDate();
				if(pattern!=null && !"".equals(pattern)) {
					sdf = DateUtils.getDefaultDateTimeFormatter(pattern);
				}
			}
			else {
				if(portaDelegata) {
					set = properties.getKeyPDSetEnabledGestioneTokenHeaderIntegrazioneJson();
				}
				else {
					set = properties.getKeyPASetEnabledGestioneTokenHeaderIntegrazioneJson();
				}
				jsonUtils = JSONUtils.getInstance();
				jsonNode = jsonUtils.newObjectNode();
				jsonNode.put("id", idTransazione);
			}
			
			if(informazioniTokenNormalizzate.getIss()!=null &&
				set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUER).booleanValue()) {
				if(op2headers) {
					TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUER), informazioniTokenNormalizzate.getIss());
				}
				else {
					jsonNode.put("issuer", informazioniTokenNormalizzate.getIss());
				}
			}
			if(informazioniTokenNormalizzate.getSub()!=null &&
				set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SUBJECT).booleanValue()) {
				if(op2headers) {
					TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SUBJECT), informazioniTokenNormalizzate.getSub());
				}
				else {
					jsonNode.put("subject", informazioniTokenNormalizzate.getSub());
				}
			}
			if(informazioniTokenNormalizzate.getUsername()!=null &&
				set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_USERNAME).booleanValue()) {
				if(op2headers) {
					TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_USERNAME), informazioniTokenNormalizzate.getUsername());
				}
				else {
					jsonNode.put("username", informazioniTokenNormalizzate.getUsername());
				}
			}
			if(informazioniTokenNormalizzate.getAud()!=null &&
				set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_AUDIENCE).booleanValue()) {
				ArrayNode array = null;
				StringBuilder bf = new StringBuilder();
				if(!op2headers) {
					array =  jsonUtils.newArrayNode();
				}
				for (String role : informazioniTokenNormalizzate.getAud()) {
					if(op2headers) {
						if(bf.length()>0) {
							bf.append(properties.getGestioneTokenHeaderIntegrazioneTrasportoAudienceSeparator());
						}
						bf.append(role);
					}
					else {
						array.add(role);
					}
				}
				if(op2headers) {
					TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_AUDIENCE), bf.toString());
				}
				else {
					jsonNode.set("audience", array);
				}
			}
			if(informazioniTokenNormalizzate.getClientId()!=null &&
				set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_CLIENT_ID).booleanValue()) {
				if(op2headers) {
					TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_CLIENT_ID), informazioniTokenNormalizzate.getClientId());
				}
				else {
					jsonNode.put("clientId", informazioniTokenNormalizzate.getClientId());
				}
			}
			if(informazioniTokenNormalizzate.getIat()!=null &&
				set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUED_AT).booleanValue()) {
				if(op2headers) {
					String value = null;
					if(sdf!=null) {
						value = sdf.format(informazioniTokenNormalizzate.getIat());
					}
					else {
						value = (informazioniTokenNormalizzate.getIat().getTime() / 1000) + "";
					}
					TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUED_AT), value);
				}
				else {
					jsonNode.put("iat", jsonUtils.getDateFormat().format(informazioniTokenNormalizzate.getIat()));
				}
			}
			if(informazioniTokenNormalizzate.getExp()!=null &&
				set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_EXPIRED).booleanValue()) {
				if(op2headers) {
					String value = null;
					if(sdf!=null) {
						value = sdf.format(informazioniTokenNormalizzate.getExp());
					}
					else {
						value = (informazioniTokenNormalizzate.getExp().getTime() / 1000) + "";
					}
					TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_EXPIRED), value);
				}
				else {
					jsonNode.put("expire", jsonUtils.getDateFormat().format(informazioniTokenNormalizzate.getExp()));
				}
			}
			if(informazioniTokenNormalizzate.getNbf()!=null &&
				set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_NBF).booleanValue()) {
				if(op2headers) {
					String value = null;
					if(sdf!=null) {
						value = sdf.format(informazioniTokenNormalizzate.getNbf());
					}
					else {
						value = (informazioniTokenNormalizzate.getNbf().getTime() / 1000) + "";
					}
					TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_NBF), value);
				}
				else {
					jsonNode.put("nbf", jsonUtils.getDateFormat().format(informazioniTokenNormalizzate.getNbf()));
				}
			}
			if(informazioniTokenNormalizzate.getRoles()!=null && !informazioniTokenNormalizzate.getRoles().isEmpty() &&
				set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ROLES).booleanValue()) {
				ArrayNode array = null;
				StringBuilder bf = new StringBuilder();
				if(!op2headers) {
					array =  jsonUtils.newArrayNode();
				}
				for (String role : informazioniTokenNormalizzate.getRoles()) {
					if(op2headers) {
						if(bf.length()>0) {
							bf.append(properties.getGestioneTokenHeaderIntegrazioneTrasportoRoleSeparator());
						}
						bf.append(role);
					}
					else {
						array.add(role);
					}
				}
				if(op2headers) {
					TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ROLES), bf.toString());
				}
				else {
					jsonNode.set("roles", array);
				}
			}
			if(informazioniTokenNormalizzate.getScopes()!=null && !informazioniTokenNormalizzate.getScopes().isEmpty() &&
				set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SCOPES).booleanValue()) {
				ArrayNode array = null;
				StringBuilder bf = new StringBuilder();
				if(!op2headers) {
					array =  jsonUtils.newArrayNode();
				}
				for (String scope : informazioniTokenNormalizzate.getScopes()) {
					if(op2headers) {
						if(bf.length()>0) {
							bf.append(properties.getGestioneTokenHeaderIntegrazioneTrasportoScopeSeparator());
						}
						bf.append(scope);
					}
					else {
						array.add(scope);
					}
				}
				if(op2headers) {
					TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SCOPES), bf.toString());
				}
				else {
					jsonNode.set("scopes", array);
				}
			}
			if(informazioniTokenNormalizzate.getUserInfo()!=null) {
				ObjectNode userInfoNode = null;
				if(!op2headers) {
					userInfoNode = jsonUtils.newObjectNode();
				}
				
				boolean add = false;
				
				if(informazioniTokenNormalizzate.getUserInfo().getFullName()!=null &&
					set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FULL_NAME).booleanValue()) {
					if(op2headers) {
						TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FULL_NAME), informazioniTokenNormalizzate.getUserInfo().getFullName());
					}
					else {
						userInfoNode.put("fullName", informazioniTokenNormalizzate.getUserInfo().getFullName());
						add = true;
					}
				}
				if(informazioniTokenNormalizzate.getUserInfo().getFirstName()!=null &&
					set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FIRST_NAME).booleanValue()) {
					if(op2headers) {
						TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FIRST_NAME), informazioniTokenNormalizzate.getUserInfo().getFirstName());
					}
					else {
						userInfoNode.put("firstName", informazioniTokenNormalizzate.getUserInfo().getFirstName());
						add = true;
					}
				}
				if(informazioniTokenNormalizzate.getUserInfo().getMiddleName()!=null &&
					set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_MIDDLE_NAME).booleanValue()) {
					if(op2headers) {
						TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_MIDDLE_NAME), informazioniTokenNormalizzate.getUserInfo().getMiddleName());
					}
					else {
						userInfoNode.put("middleName", informazioniTokenNormalizzate.getUserInfo().getMiddleName());
						add = true;
					}
				}
				if(informazioniTokenNormalizzate.getUserInfo().getFamilyName()!=null &&
					set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FAMILY_NAME).booleanValue()) {
					if(op2headers) {
						TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FAMILY_NAME), informazioniTokenNormalizzate.getUserInfo().getFamilyName());
					}
					else {
						userInfoNode.put("familyName", informazioniTokenNormalizzate.getUserInfo().getFamilyName());
						add = true;
					}
				}
				if(informazioniTokenNormalizzate.getUserInfo().getEMail()!=null &&
					set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_EMAIL).booleanValue()) {
					if(op2headers) {
						TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_EMAIL), informazioniTokenNormalizzate.getUserInfo().getEMail());
					}
					else {
						userInfoNode.put("eMail", informazioniTokenNormalizzate.getUserInfo().getEMail());
						add = true;
					}
				}
				
				if(!op2headers && add) {
					jsonNode.set("userInfo", userInfoNode);
				}
			}	
			if(informazioniTokenNormalizzate.getClaims()!=null && !informazioniTokenNormalizzate.getClaims().isEmpty()) {
				if(informazioniTokenNormalizzate.getClaims().containsKey(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID)) {
					Object oPid = informazioniTokenNormalizzate.getClaims().get(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID);
					if(oPid instanceof String) {
						String pId = (String) oPid;
						if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_JTI).booleanValue()) {
							if(op2headers) {
								TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_JTI), pId);
							}
							else {
								jsonNode.put("jti", pId);
							}
						}
					}
				}
				if(informazioniTokenNormalizzate.getClaims().containsKey(Costanti.PDND_PURPOSE_ID)) {
					Object oPid = informazioniTokenNormalizzate.getClaims().get(Costanti.PDND_PURPOSE_ID);
					if(oPid instanceof String) {
						String pId = (String) oPid;
						if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PURPOSE_ID).booleanValue()) {
							if(op2headers) {
								TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PURPOSE_ID), pId);
							}
							else {
								jsonNode.put("purposeId", pId);
							}
						}
					}
				}
				if(informazioniTokenNormalizzate.getClaims().containsKey(Costanti.PDND_CONSUMER_ID)) {
					Object oPid = informazioniTokenNormalizzate.getClaims().get(Costanti.PDND_CONSUMER_ID);
					if(oPid instanceof String) {
						String pId = (String) oPid;
						if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_CONSUMER_ID).booleanValue()) {
							if(op2headers) {
								TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_CONSUMER_ID), pId);
							}
							else {
								jsonNode.put("consumerId", pId);
							}
						}
					}
				}
				if(informazioniTokenNormalizzate.getClaims().containsKey(Costanti.PDND_PRODUCER_ID)) {
					Object oPid = informazioniTokenNormalizzate.getClaims().get(Costanti.PDND_PRODUCER_ID);
					if(oPid instanceof String) {
						String pId = (String) oPid;
						if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PRODUCER_ID).booleanValue()) {
							if(op2headers) {
								TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PRODUCER_ID), pId);
							}
							else {
								jsonNode.put("producerId", pId);
							}
						}
					}
				}
				if(informazioniTokenNormalizzate.getClaims().containsKey(Costanti.PDND_SERVICE_ID)) {
					Object oPid = informazioniTokenNormalizzate.getClaims().get(Costanti.PDND_SERVICE_ID);
					if(oPid instanceof String) {
						String pId = (String) oPid;
						if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SERVICE_ID).booleanValue()) {
							if(op2headers) {
								TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SERVICE_ID), pId);
							}
							else {
								jsonNode.put("eserviceId", pId);
							}
						}
					}
				}
				if(informazioniTokenNormalizzate.getClaims().containsKey(Costanti.PDND_DESCRIPTOR_ID)) {
					Object oPid = informazioniTokenNormalizzate.getClaims().get(Costanti.PDND_DESCRIPTOR_ID);
					if(oPid instanceof String) {
						String pId = (String) oPid;
						if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_DESCRIPTOR_ID).booleanValue()) {
							if(op2headers) {
								TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_DESCRIPTOR_ID), pId);
							}
							else {
								jsonNode.put("descriptorId", pId);
							}
						}
					}
				}
				if(informazioniTokenNormalizzate.getPdnd()!=null && !informazioniTokenNormalizzate.getPdnd().isEmpty()) {
					
					ObjectNode pdndNode = null;
					ObjectNode organizationNode = null;
					if(!op2headers) {
						pdndNode = jsonUtils.newObjectNode();
						organizationNode = jsonUtils.newObjectNode();
						pdndNode.set("organization", organizationNode);
					}
					
					boolean add = false;
					
					String organizationName = PDNDTokenInfo.readOrganizationNameFromPDNDMap(informazioniTokenNormalizzate.getPdnd());
					if(organizationName!=null && StringUtils.isNotEmpty(organizationName) &&
							set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PDND_ORGANIZATION_NAME).booleanValue()) {
						if(op2headers) {
							TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PDND_ORGANIZATION_NAME), organizationName);
						}
						else {
							organizationNode.put("name", organizationName);
							add = true;
						}
					}
					
					String organizationCategory = PDNDTokenInfo.readOrganizationCategoryFromPDNDMap(informazioniTokenNormalizzate.getPdnd());
					if(organizationCategory!=null && StringUtils.isNotEmpty(organizationCategory) &&
							set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PDND_ORGANIZATION_CATEGORY).booleanValue()) {
						if(op2headers) {
							TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PDND_ORGANIZATION_CATEGORY), organizationCategory);
						}
						else {
							organizationNode.put("category", organizationCategory);
							add = true;
						}
					}
					
					String organizationExternalOrigin = PDNDTokenInfo.readOrganizationExternalOriginFromPDNDMap(informazioniTokenNormalizzate.getPdnd());
					String organizationExternalId = PDNDTokenInfo.readOrganizationExternalIdFromPDNDMap(informazioniTokenNormalizzate.getPdnd());
					StringBuilder sbOrganizationExternal = new StringBuilder();
					
					if(organizationExternalOrigin!=null && StringUtils.isNotEmpty(organizationExternalOrigin)) {
						sbOrganizationExternal.append(organizationExternalOrigin);
						if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PDND_ORGANIZATION_EXTERNAL_ORIGIN).booleanValue()) {
							if(op2headers) {
								TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PDND_ORGANIZATION_EXTERNAL_ORIGIN), organizationExternalOrigin);
							}
							else {
								organizationNode.put("externalOrigin", organizationExternalOrigin);
								add = true;
							}
						}
					}
					if(organizationExternalId!=null && StringUtils.isNotEmpty(organizationExternalId)) {
						if(sbOrganizationExternal.length()>0) {
							sbOrganizationExternal.append(" ");
						}
						sbOrganizationExternal.append(organizationExternalId);
						if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PDND_ORGANIZATION_EXTERNAL_ID).booleanValue()) {
							if(op2headers) {
								TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PDND_ORGANIZATION_EXTERNAL_ID), organizationExternalId);
							}
							else {
								organizationNode.put("externalId", organizationExternalId);
								add = true;
							}
						}
					}
					
					String organizationExternal = sbOrganizationExternal.toString();
					if(organizationExternal!=null && StringUtils.isNotEmpty(organizationExternal) &&
							set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PDND_ORGANIZATION_EXTERNAL).booleanValue()) {
						if(op2headers) {
							TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PDND_ORGANIZATION_EXTERNAL), organizationExternal);
						}
						else {
							organizationNode.put("external", organizationExternal);
							add = true;
						}
					}
					
					if(!op2headers && add) {
						jsonNode.set("pdnd", pdndNode);
					}
				}
			}
			List<String> listCustomClaims = properties.getCustomClaimsKeysGestioneTokenForward();
			if(listCustomClaims!=null && !listCustomClaims.isEmpty()) {
				
				ArrayNode customClaimsNode = null;
				if(!op2headers) {
					customClaimsNode = jsonUtils.newArrayNode();
				}
				
				boolean add = false;
				
				for (String claimKey : listCustomClaims) {
				
					String claimName = properties.getCustomClaimsNameGestioneTokenHeaderIntegrazione(claimKey);
					
					if(informazioniTokenNormalizzate.getClaims()!=null && informazioniTokenNormalizzate.getClaims().containsKey(claimName)) {
						
						Object claimValueObject = informazioniTokenNormalizzate.getClaims().get(claimName);
						List<String> claimValues = null;
						if(claimValueObject!=null) {
							claimValues = TokenUtilities.getClaimValues(claimValueObject);
						}
						String headerName = null;
						if(claimValues!=null && !claimValues.isEmpty()) {
							boolean setCustomClaims = false;
							if(op2headers) {
								headerName = properties.getCustomClaimsHeaderNameGestioneTokenHeaderIntegrazioneTrasporto(claimKey);
								if(portaDelegata) {
									setCustomClaims = properties.getCustomClaimsKeyPDSetEnabledGestioneTokenHeaderIntegrazioneTrasporto(claimKey);
								}
								else {
									setCustomClaims = properties.getCustomClaimsKeyPASetEnabledGestioneTokenHeaderIntegrazioneTrasporto(claimKey);
								}
							}
							else {
								headerName = properties.getCustomClaimsJsonPropertyNameGestioneTokenHeaderIntegrazioneJson(claimKey);
								if(portaDelegata) {
									setCustomClaims = properties.getCustomClaimsKeyPDSetEnabledGestioneTokenHeaderIntegrazioneJson(claimKey);
								}
								else {
									setCustomClaims = properties.getCustomClaimsKeyPASetEnabledGestioneTokenHeaderIntegrazioneJson(claimKey);
								}
							}
							
							if(setCustomClaims) {
								String claimValue = TokenUtilities.getClaimValuesAsString(claimValues);
								if(op2headers) {
									TransportUtils.setHeader(tokenForward.getTrasporto(),headerName, claimValue);
								}
								else {
									ObjectNode propertyNode = jsonUtils.newObjectNode();
									propertyNode.put("name", headerName);
									propertyNode.put("value", claimValue);
									customClaimsNode.add(propertyNode);
									add = true;
								}
							}
						}
					}
					
				}
				
				if(!op2headers && add) {
					jsonNode.set("claims", customClaimsNode);
				}
			}

			Date processTime = DateManager.getDate();
			if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PROCESS_TIME).booleanValue()) {
				if(op2headers) {
					String value = null;
					if(sdf!=null) {
						value = sdf.format(processTime);
					}
					else {
						value = (processTime.getTime() / 1000) + "";
					}
					TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PROCESS_TIME), value);
				}
				else {
					jsonNode.put("processTime", jsonUtils.getDateFormat().format(processTime));
				}
			}
			
			if(!op2headers) {
				String json = jsonUtils.toString(jsonNode);
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JSON.equals(forwardInforRaccolteMode)) {
					String headerName = properties.getGestioneTokenHeaderTrasportoJSON();
					if(encodeBase64) {
						TransportUtils.setHeader(tokenForward.getTrasporto(),headerName, Base64Utilities.encodeAsString(json.getBytes()));
					}
					else {
						TransportUtils.setHeader(tokenForward.getTrasporto(),headerName, json);
					}
				}
				else {
					// JWS Compact
					JWSOptions jwsOptions = new JWSOptions(JOSESerialization.COMPACT);
	    			JsonSignature jsonCompactSignature = new JsonSignature(jwtSecurity,jwsOptions);
	    			String compact = jsonCompactSignature.sign(json);
	    			String headerName = properties.getGestioneTokenHeaderTrasportoJWT();
	    			TransportUtils.setHeader(tokenForward.getTrasporto(),headerName, compact);
				}
			}
		} 
		else {
			
			if(forwardValidazioneJWT && esitoValidazioneJWT!=null && esitoValidazioneJWT.isValido() && 
					esitoValidazioneJWT.getInformazioniToken()!=null &&
					esitoValidazioneJWT.getInformazioniToken().getRawResponse()!=null) {
				
				String value = esitoValidazioneJWT.getInformazioniToken().getRawResponse();
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(forwardInforRaccolteMode)) {
					// JWS Compact   			
					JWSOptions jwsOptions = new JWSOptions(JOSESerialization.COMPACT);
	    			JsonSignature jsonCompactSignature = new JsonSignature(jwtSecurity,jwsOptions);
	    			value = jsonCompactSignature.sign(value);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(forwardInforRaccolteMode)) {
					// JWE Compact
					JWEOptions jweOptions = new JWEOptions(JOSESerialization.COMPACT);
	    			JsonEncrypt jsonCompactEncrypt = new JsonEncrypt(jwtSecurity,jweOptions);
					value = jsonCompactEncrypt.encrypt(value);
				}
				else {
					//Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON
					if(encodeBase64) {
						value = Base64Utilities.encodeAsString(value.getBytes());
					}
				}
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardValidazioneJWTMode)) {
					TransportUtils.setHeader(tokenForward.getTrasporto(),forwardValidazioneJWTName, value);
				}
				else {
					TransportUtils.setParameter(tokenForward.getUrl(),forwardValidazioneJWTName, value);
				}
				
			}
			
			if(forwardIntrospection && esitoIntrospection!=null && esitoIntrospection.isValido() && 
					esitoIntrospection.getInformazioniToken()!=null &&
					esitoIntrospection.getInformazioniToken().getRawResponse()!=null) {
				
				String value = esitoIntrospection.getInformazioniToken().getRawResponse();
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(forwardInforRaccolteMode)) {
					// JWS Compact   			
					JWSOptions jwsOptions = new JWSOptions(JOSESerialization.COMPACT);
	    			JsonSignature jsonCompactSignature = new JsonSignature(jwtSecurity,jwsOptions);
	    			value = jsonCompactSignature.sign(value);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(forwardInforRaccolteMode)) {
					// JWE Compact
					JWEOptions jweOptions = new JWEOptions(JOSESerialization.COMPACT);
	    			JsonEncrypt jsonCompactEncrypt = new JsonEncrypt(jwtSecurity,jweOptions);
					value = jsonCompactEncrypt.encrypt(value);
				}
				else {
					//Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON
					if(encodeBase64) {
						value = Base64Utilities.encodeAsString(value.getBytes());
					}
				}
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardIntrospectionMode)) {
					TransportUtils.setHeader(tokenForward.getTrasporto(),forwardIntrospectionName, value);
				}
				else {
					TransportUtils.setParameter(tokenForward.getUrl(),forwardIntrospectionName, value);
				}
				
			}
			
			if(forwardUserInfo && esitoUserInfo!=null && esitoUserInfo.isValido() && 
					esitoUserInfo.getInformazioniToken()!=null &&
					esitoUserInfo.getInformazioniToken().getRawResponse()!=null) {
				
				String value = esitoUserInfo.getInformazioniToken().getRawResponse();
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(forwardInforRaccolteMode)) {
					// JWS Compact   			
					JWSOptions jwsOptions = new JWSOptions(JOSESerialization.COMPACT);
	    			JsonSignature jsonCompactSignature = new JsonSignature(jwtSecurity,jwsOptions);
	    			value = jsonCompactSignature.sign(value);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(forwardInforRaccolteMode)) {
					// JWE Compact
					JWEOptions jweOptions = new JWEOptions(JOSESerialization.COMPACT);
	    			JsonEncrypt jsonCompactEncrypt = new JsonEncrypt(jwtSecurity,jweOptions);
					value = jsonCompactEncrypt.encrypt(value);
				}
				else {
					//Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JSON
					if(encodeBase64) {
						value = Base64Utilities.encodeAsString(value.getBytes());
					}
				}
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardUserInfoMode)) {
					TransportUtils.setHeader(tokenForward.getTrasporto(),forwardUserInfoName, value);
				}
				else {
					TransportUtils.setParameter(tokenForward.getUrl(),forwardUserInfoName, value);
				}
				
			}
			
		}
		
	}
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] UTILITIES INTERNE ****************** */
	
	public static boolean isExpired(Date now, Date exp) throws TokenException {
		
		Date checkNow = now;
		Long tolerance = null;
		try {
			tolerance = OpenSPCoop2Properties.getInstance().getGestioneTokenExpTimeCheckToleranceMilliseconds();
		}catch(Exception e) {
			throw new TokenException(e.getMessage(),e);
		}
		if(tolerance!=null && tolerance.longValue()>0) {
			checkNow = new Date(now.getTime() - tolerance.longValue());
		}
		
		return !checkNow.before(exp);
	}
	public static boolean isFuture(Date now, Date nbf) throws TokenException {
		
		Date checkNow = now;
		Long tolerance = null;
		try {
			tolerance = OpenSPCoop2Properties.getInstance().getGestioneTokenNbfTimeCheckToleranceMilliseconds();
		}catch(Exception e) {
			throw new TokenException(e.getMessage(),e);
		}
		if(tolerance!=null && tolerance.longValue()>0) {
			checkNow = new Date(now.getTime() + tolerance.longValue());
		}
		
		return !nbf.before(checkNow);
	}
	
	public static boolean isProfiloModIPAEnabled() {
		try {
			Enumeration<String> en = ProtocolFactoryManager.getInstance().getProtocolNames();
			while (en.hasMoreElements()) {
				String protocollo = en.nextElement();
				if(protocollo.equals(CostantiLabel.MODIPA_PROTOCOL_NAME)) {
					return true;
				}
			}
			return false;
		}catch(Throwable t) {
			return false;
		}
	}
	
	private static List<String> policyGestioneTokenPDND = null;
	private static synchronized void initPolicyGestioneTokenPDND(Logger log) {
		if(policyGestioneTokenPDND==null) {
			try {
				policyGestioneTokenPDND = new ArrayList<>();
				if(isProfiloModIPAEnabled()) {
					initPolicyGestioneTokenPDND();
				}
			}catch(Exception e) {
				log.error("Errore di inizializzazione policy 'PDND': "+e.getMessage(), e);
			}
		}
	}
	private static synchronized void initPolicyGestioneTokenPDND() throws ProtocolException {
		List<RemoteStoreConfig> listRSC = ModIUtils.getRemoteStoreConfig();
		if(listRSC!=null && !listRSC.isEmpty()) {
			for (RemoteStoreConfig r : listRSC) {
				if(!policyGestioneTokenPDND.contains(r.getTokenPolicy())) {
					policyGestioneTokenPDND.add(r.getTokenPolicy());
				}
			}
		}
	}
	private static boolean isPdndTokenPolicy(Logger log, String tokenPolicy) {
		if(policyGestioneTokenPDND==null) {
			initPolicyGestioneTokenPDND(log) ;
		}
		return policyGestioneTokenPDND.contains(tokenPolicy);
	}
	
	private static List<Proprieta> readProprieta(AbstractDatiInvocazione datiInvocazione) throws DriverConfigurazioneException {
		List<Proprieta> l = null;
		if(datiInvocazione instanceof DatiInvocazionePortaDelegata) {
			DatiInvocazionePortaDelegata d = (DatiInvocazionePortaDelegata) datiInvocazione;
			PortaDelegata pd = d.getPd();
			if(pd==null && d.getIdPD()!=null) {
				ConfigurazionePdDManager configManager = ConfigurazionePdDManager.getInstance(datiInvocazione.getState());
				pd = configManager.getPortaDelegataSafeMethod(d.getIdPD(), datiInvocazione.getRequestInfo());
			}
			if(pd!=null) {
				return pd.getProprieta();
			}
		}
		else {
			DatiInvocazionePortaApplicativa d = (DatiInvocazionePortaApplicativa) datiInvocazione;
			PortaApplicativa pa = d.getPa();
			if(pa==null && d.getIdPA()!=null) {
				ConfigurazionePdDManager configManager = ConfigurazionePdDManager.getInstance(datiInvocazione.getState());
				pa = configManager.getPortaApplicativaSafeMethod(d.getIdPA(), datiInvocazione.getRequestInfo());
			}
			if(pa!=null) {
				return pa.getProprieta();
			}
		}
		return l;
	}
	
	static boolean isIatRequired(Logger log, PolicyGestioneToken policyGestioneToken, AbstractDatiInvocazione datiInvocazione, IProtocolFactory<?> protocollo, OpenSPCoop2Properties op2Properties) throws CoreException {
		
		String p = protocollo!=null ? protocollo.getProtocol() : "";
		
		boolean delegata = datiInvocazione instanceof DatiInvocazionePortaDelegata;
		
		boolean required = op2Properties.isGestioneTokenIatRequired();
		BooleanNullable bn = op2Properties.isGestioneTokenIatRequired(delegata, p);
		if(bn!=null && bn.getValue()!=null) {
			required = bn.getValue().booleanValue();
		}
		
		if(!delegata && isPdndTokenPolicy(log, policyGestioneToken.getName())) {
			bn = op2Properties.isGestioneTokenIatPdndRequired();
			if(bn!=null && bn.getValue()!=null) {
				required = bn.getValue().booleanValue();
			}
		}
		
		try {
			List<Proprieta> props = readProprieta(datiInvocazione);
			return CostantiProprieta.isTokenValidationClaimsIatRequired(props, required);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	static boolean isExpRequired(Logger log, PolicyGestioneToken policyGestioneToken, AbstractDatiInvocazione datiInvocazione, IProtocolFactory<?> protocollo, OpenSPCoop2Properties op2Properties) throws CoreException {
		
		String p = protocollo!=null ? protocollo.getProtocol() : "";
		
		boolean delegata = datiInvocazione instanceof DatiInvocazionePortaDelegata;
		
		boolean required = op2Properties.isGestioneTokenExpRequired();
		BooleanNullable bn = op2Properties.isGestioneTokenExpRequired(delegata, p);
		if(bn!=null && bn.getValue()!=null) {
			required = bn.getValue().booleanValue();
		}
		
		if(!delegata && isPdndTokenPolicy(log, policyGestioneToken.getName())) {
			bn = op2Properties.isGestioneTokenExpPdndRequired();
			if(bn!=null && bn.getValue()!=null) {
				required = bn.getValue().booleanValue();
			}
		}
		
		try {
			List<Proprieta> props = readProprieta(datiInvocazione);
			return CostantiProprieta.isTokenValidationClaimsExpRequired(props, required);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	static boolean isNbfRequired(Logger log, PolicyGestioneToken policyGestioneToken, AbstractDatiInvocazione datiInvocazione, IProtocolFactory<?> protocollo, OpenSPCoop2Properties op2Properties) throws CoreException {
		
		String p = protocollo!=null ? protocollo.getProtocol() : "";
		
		boolean delegata = datiInvocazione instanceof DatiInvocazionePortaDelegata;
		
		boolean required = op2Properties.isGestioneTokenNbfRequired();
		BooleanNullable bn = op2Properties.isGestioneTokenNbfRequired(delegata, p);
		if(bn!=null && bn.getValue()!=null) {
			required = bn.getValue().booleanValue();
		}
		
		if(!delegata && isPdndTokenPolicy(log, policyGestioneToken.getName())) {
			bn = op2Properties.isGestioneTokenNbfPdndRequired();
			if(bn!=null && bn.getValue()!=null) {
				required = bn.getValue().booleanValue();
			}
		}
		
		try {
			List<Proprieta> props = readProprieta(datiInvocazione);
			return CostantiProprieta.isTokenValidationClaimsNbfRequired(props, required);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	static void validazioneInformazioniToken(boolean checkRequired, // verranno controllati solo nella validazione jwt
			Logger log, AbstractDatiInvocazione datiInvocazione, IProtocolFactory<?> protocolFactory, 
			EsitoGestioneToken esitoGestioneToken, PolicyGestioneToken policyGestioneToken, boolean saveErrorInCache) throws TokenException, CoreException {
		
		Date now = DateManager.getDate();
		
		if(esitoGestioneToken.isValido()) {
			esitoGestioneToken.setDateValide(true); // tanto le ricontrollo adesso
		}
			
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
		/** === EXP === ***/
		
		if(esitoGestioneToken.isValido() && checkRequired) {
			
			boolean check = isExpRequired(log, policyGestioneToken, datiInvocazione, protocolFactory, op2Properties);
			if(check && esitoGestioneToken.getInformazioniToken().getExp()==null) {
				esitoGestioneToken.setTokenValidazioneFallita();
				esitoGestioneToken.setDateValide(false);
				esitoGestioneToken.setDetails("Token rejected; the 'exp' (expiration time) claim is required but missing.");
				if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
					esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
	    					esitoGestioneToken.getDetails()));  		
    			}
    			else {
    				esitoGestioneToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
    						false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
    						esitoGestioneToken.getDetails()));
    			} 	
			}
			
		}
		
		if(esitoGestioneToken.isValido()) {
			
			boolean enabled = op2Properties.isGestioneTokenExpTimeCheck();
			
			if(enabled && esitoGestioneToken.getInformazioniToken().getExp()!=null) {	
				
				boolean expired = isExpired(now, esitoGestioneToken.getInformazioniToken().getExp());

				/*
				 *   The "exp" (expiration time) claim identifies the expiration time on
   				 *   or after which the JWT MUST NOT be accepted for processing.  The
   				 *   processing of the "exp" claim requires that the current date/time
   				 *   MUST be before the expiration date/time listed in the "exp" claim.
				 **/
				if(expired){
					esitoGestioneToken.setTokenScaduto();
					esitoGestioneToken.setDateValide(false);
					esitoGestioneToken.setDetails("Token expired");
					if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
						esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
		    					false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
		    					esitoGestioneToken.getDetails()));  		
	    			}
	    			else {
	    				esitoGestioneToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    						false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
	    						esitoGestioneToken.getDetails()));
	    			} 	
				}
			}
			
		}
			
		
		
		/** === NBF === ***/
		
		if(esitoGestioneToken.isValido() && checkRequired) {
			
			boolean check = isNbfRequired(log, policyGestioneToken, datiInvocazione, protocolFactory, op2Properties);
			if(check && esitoGestioneToken.getInformazioniToken().getNbf()==null) {
				esitoGestioneToken.setTokenValidazioneFallita();
				esitoGestioneToken.setDateValide(false);
				esitoGestioneToken.setDetails("Token rejected; the 'nbf' (not before) claim is required but missing.");
				if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
					esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
	    					esitoGestioneToken.getDetails()));  		
    			}
    			else {
    				esitoGestioneToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
    						false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
    						esitoGestioneToken.getDetails()));
    			} 	
			}
			
		}
		
		if(esitoGestioneToken.isValido() &&
			esitoGestioneToken.getInformazioniToken().getNbf()!=null) {
			
			boolean future = isFuture(now, esitoGestioneToken.getInformazioniToken().getNbf());
			
			/*
			 *   The "nbf" (not before) claim identifies the time before which the JWT
			 *   MUST NOT be accepted for processing.  The processing of the "nbf"
			 *   claim requires that the current date/time MUST be after or equal to
			 *   the not-before date/time listed in the "nbf" claim. 
			 **/
			
			if(future){
				esitoGestioneToken.setTokenNotUsableBefore();
				esitoGestioneToken.setDateValide(false);
				SimpleDateFormat sdf = DateUtils.getDefaultDateTimeFormatter(GestoreToken.DATE_FORMAT);
				esitoGestioneToken.setDetails("Token not usable before "+sdf.format(esitoGestioneToken.getInformazioniToken().getNbf()));
				if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
					esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
	    					esitoGestioneToken.getDetails()));  
				}
				else {
					esitoGestioneToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
							false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
							esitoGestioneToken.getDetails()));
				} 
			}
		}
		
		
		
		
		/** === IAT === ***/
		
		if(esitoGestioneToken.isValido() && checkRequired) {
			
			boolean check = isIatRequired(log, policyGestioneToken, datiInvocazione, protocolFactory, op2Properties);
			if(check && esitoGestioneToken.getInformazioniToken().getIat()==null) {
				esitoGestioneToken.setTokenValidazioneFallita();
				esitoGestioneToken.setDateValide(false);
				esitoGestioneToken.setDetails("Token rejected; the 'iat' (issued at) claim is required but missing.");
				if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
					esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
	    					esitoGestioneToken.getDetails()));  		
    			}
    			else {
    				esitoGestioneToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
    						false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
    						esitoGestioneToken.getDetails()));
    			} 	
			}
			
		}
		
		if(esitoGestioneToken.isValido() &&
			esitoGestioneToken.getInformazioniToken().getIat()!=null) {				
			/*
			 *   The "iat" (issued at) claim identifies the time at which the JWT was
			 *   issued.  This claim can be used to determine the age of the JWT.
			 *   The iat Claim can be used to reject tokens that were issued too far away from the current time, 
			 *   limiting the amount of time that nonces need to be stored to prevent attacks. The acceptable range is Client specific. 
			 **/
			Long old = op2Properties.getGestioneTokenIatTimeCheckMilliseconds();
			if(old!=null) {
				Date oldMax = new Date((DateManager.getTimeMillis() - old.longValue()));
				if(esitoGestioneToken.getInformazioniToken().getIat().before(oldMax)) {
					esitoGestioneToken.setTokenScaduto();
					esitoGestioneToken.setDateValide(false);
					SimpleDateFormat sdf = DateUtils.getDefaultDateTimeFormatter(GestoreToken.DATE_FORMAT);
					esitoGestioneToken.setDetails("Token expired; iat time '"+sdf.format(esitoGestioneToken.getInformazioniToken().getIat())+"' too old");
					if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
						esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
		    					false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
		    					esitoGestioneToken.getDetails()));  
					}
	    			else {
	    				esitoGestioneToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    						false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
	    						esitoGestioneToken.getDetails()));
	    			} 
				}
			}
			
			Long future = op2Properties.getGestioneTokenIatTimeCheckFutureToleranceMilliseconds();
			if(future!=null && future.longValue()>0) {
				Date futureMax = new Date((DateManager.getTimeMillis() + future.longValue()));
				if(esitoGestioneToken.getInformazioniToken().getIat().after(futureMax)) {
					esitoGestioneToken.setTokenInTheFuture();
					esitoGestioneToken.setDateValide(false);
					SimpleDateFormat sdf = DateUtils.getDefaultDateTimeFormatter(GestoreToken.DATE_FORMAT);
					esitoGestioneToken.setDetails("Token valid in the future; iat time '"+sdf.format(esitoGestioneToken.getInformazioniToken().getIat())+"' is in the future");
					if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
						esitoGestioneToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
		    					false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
		    					esitoGestioneToken.getDetails()));  
					}
	    			else {
	    				esitoGestioneToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    						false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
	    						esitoGestioneToken.getDetails()));
	    			} 
				}
			}
			
		}
		
		if(!esitoGestioneToken.isValido()) {
			esitoGestioneToken.setNoCache(!saveErrorInCache);
		}
	}
	
	static void validazioneInformazioniTokenEnrichPDNDClientInfo(EsitoGestioneToken esitoGestioneToken, PolicyGestioneToken policyGestioneToken, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory, AbstractDatiInvocazione datiInvocazione,
			SecurityToken securityTokenForContext) throws CoreException, ProtocolException {
		
		if( 
				(policyGestioneToken!=null && policyGestioneToken.getName()!=null) // dal nome della policy viene capito se e' attivo il recupero dei dati PDND
				&&
				(esitoGestioneToken!=null && esitoGestioneToken.getInformazioniToken()!=null && esitoGestioneToken.getInformazioniToken().getClientId()!=null) // il clientId serve per poter effettuare il recupero dei dati
				&&
				(datiInvocazione instanceof DatiInvocazionePortaApplicativa) // solo per erogazioni
				&&
				(org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROTOCOL_NAME.equals(protocolFactory.getProtocol())) // solo per profilo modi
				) {
			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			if(op2Properties.isGestoreChiaviPDNDretrieveClientInfoAfterVoucherPDNDValidation()) {
				
				IDSoggetto idSoggetto = null;
				DatiInvocazionePortaApplicativa datiPA = (DatiInvocazionePortaApplicativa) datiInvocazione;
				if(datiPA.getPa()!=null && datiPA.getPa().getTipoSoggettoProprietario()!=null && datiPA.getPa().getNomeSoggettoProprietario()!=null) {
					idSoggetto = new IDSoggetto(datiPA.getPa().getTipoSoggettoProprietario(), datiPA.getPa().getNomeSoggettoProprietario());
				}
				else if(datiPA.getRequestInfo()!=null && datiPA.getRequestInfo().getIdentitaPdD()!=null) {
					idSoggetto = datiPA.getRequestInfo().getIdentitaPdD();
				}
					
				PDNDResolver pdndResolver = new PDNDResolver(pddContext, ModIUtils.getRemoteStoreConfig());
				pdndResolver.enrichTokenInfo(datiInvocazione.getRequestInfo(), idSoggetto,
						esitoGestioneToken.getInformazioniToken(), securityTokenForContext);
			}
		}
		
	}
	
	static void validazioneInformazioniTokenHeader(String jsonHeader, PolicyGestioneToken policyGestioneToken) throws TokenException {
		
		if(policyGestioneToken.isValidazioneJWTHeader()) {
			
			try {
			
				JSONUtils jsonUtils = JSONUtils.getInstance();
				if(jsonUtils.isJson(jsonHeader)) {
					JsonNode root = jsonUtils.getAsNode(jsonHeader);
					Map<String, Serializable> readClaims = jsonUtils.convertToSimpleMap(root);
					
					validazioneInformazioniTokenHeader(readClaims, policyGestioneToken.getValidazioneJWTHeaderTyp(), Claims.JSON_WEB_TOKEN_RFC_7515_TYPE);
					
					validazioneInformazioniTokenHeader(readClaims, policyGestioneToken.getValidazioneJWTHeaderCty(), Claims.JSON_WEB_TOKEN_RFC_7515_CONTENT_TYPE);
					
					validazioneInformazioniTokenHeader(readClaims, policyGestioneToken.getValidazioneJWTHeaderAlg(), Claims.JSON_WEB_TOKEN_RFC_7515_ALGORITHM);
				}	
				
			}catch(Exception e) {
				throw new TokenException("JWT header validation failed; "+e.getMessage(),e);
			}
			
		}
		
	}
	
	private static void validazioneInformazioniTokenHeader(Map<String, Serializable> readClaims, List<String> expectedValues, String claim) throws TokenException {
		if(expectedValues!=null && !expectedValues.isEmpty()) {
			String v = null;
			if(readClaims!=null && !readClaims.isEmpty()) {
				v = TokenUtilities.getClaimAsString(readClaims,claim);
			}
			if(v==null || StringUtils.isEmpty(v)) {
				throw new TokenException("Expected claim '"+claim+"' not found");
			}
			boolean find = false;
			for (String vCheck : expectedValues) {
				if(v.equalsIgnoreCase(vCheck)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new TokenException("Claim '"+claim+"' with invalid value '"+v+"'");
			}
		}
	}
	
	static String buildPrefixCacheKeyValidazione(String policy, String funzione) {
		StringBuilder bf = new StringBuilder(funzione);
    	bf.append("_");
    	bf.append(policy);
    	bf.append("_");
    	return bf.toString();
	}
	static String buildCacheKeyValidazione(String policy, String funzione, boolean portaDelegata, String token) {
    	StringBuilder bf = new StringBuilder();
    	bf.append(buildPrefixCacheKeyValidazione(policy, funzione));
    	if(portaDelegata){
    		bf.append("PD");
    	}
    	else {
    		bf.append("PA");
    	}
    	bf.append("_");
    	bf.append(token);
    	return bf.toString();
    }
	
	static HttpResponse http(Logger log, PolicyGestioneToken policyGestioneToken, HTTP_TYPE httpType, 
			DynamicDiscovery dynamicDiscovery, String token,
			PdDContext pddContext, IProtocolFactory<?> protocolFactory, 
			IState state, boolean delegata, String idModulo, PortaApplicativa pa, PortaDelegata pd,
			IDSoggetto idDominio, IDServizio idServizio,
			Busta busta, RequestInfo requestInfo) throws Exception {
		
		// *** Raccola Parametri ***
		
		Map<String, Object> dynamicMap = TokenUtilities.buildDynamicMap(busta, requestInfo, pddContext, log);
		
		String endpoint = null;
		String prefixConnettore = null;
		boolean introspectionService = false;
		boolean userInfoService = false;
		switch (httpType) {
		case DYNAMIC_DISCOVERY:
			endpoint = policyGestioneToken.getDynamicDiscoveryEndpoint();
			if(endpoint!=null && !"".equals(endpoint)) {
				endpoint = DynamicUtils.convertDynamicPropertyValue("endpoint.gwt", endpoint, dynamicMap, pddContext);	
			}
			prefixConnettore = "[EndpointDynamicDiscovery: "+endpoint+"] ";
			break;
		case INTROSPECTION:
			if(policyGestioneToken.isDynamicDiscovery()) {
				check(dynamicDiscovery);
				endpoint = dynamicDiscovery.getIntrospectionEndpoint();
				if(endpoint!=null && !"".equals(endpoint)) {
					endpoint = DynamicUtils.convertDynamicPropertyValue("endpoint.gwt", endpoint, dynamicMap, pddContext);	
				}
				if(endpoint==null || StringUtils.isEmpty(endpoint)) {
					throw new TokenException("DynamicDiscovery.introspectionEndpoint undefined");
				}
			}
			else {
				endpoint = policyGestioneToken.getIntrospectionEndpoint();
			}
			prefixConnettore = "[EndpointIntrospection: "+endpoint+"] ";	
			introspectionService = true;
			break;
		case USER_INFO:
			if(policyGestioneToken.isDynamicDiscovery()) {
				check(dynamicDiscovery);
				endpoint = dynamicDiscovery.getUserinfoEndpoint();
				if(endpoint!=null && !"".equals(endpoint)) {
					endpoint = DynamicUtils.convertDynamicPropertyValue("endpoint.gwt", endpoint, dynamicMap, pddContext);	
				}
				if(endpoint==null || StringUtils.isEmpty(endpoint)) {
					throw new TokenException("DynamicDiscovery.userinfoEndpoint undefined");
				}
			}
			else {
				endpoint = policyGestioneToken.getUserInfoEndpoint();
			}
			prefixConnettore = "[EndpointUserInfo: "+endpoint+"] ";		
			userInfoService = true;
			break;
		}
		
		TipoTokenRequest tipoTokenRequest = null;
		String positionTokenName = null;
		if(introspectionService) {
			tipoTokenRequest = policyGestioneToken.getIntrospectionTipoTokenRequest();	
		}
		else if(userInfoService) {
			tipoTokenRequest = policyGestioneToken.getUserInfoTipoTokenRequest();		
		}
		if(tipoTokenRequest!=null) {
			switch (tipoTokenRequest) {
			case authorization:
				break;
			case header:
				if(introspectionService) {
					positionTokenName = policyGestioneToken.getIntrospectionTipoTokenRequestHeaderName();
				}else {
					positionTokenName = policyGestioneToken.getUserInfoTipoTokenRequestHeaderName();
				}
				break;
			case url:
				if(introspectionService) {
					positionTokenName = policyGestioneToken.getIntrospectionTipoTokenRequestUrlPropertyName();
				}else {
					positionTokenName = policyGestioneToken.getUserInfoTipoTokenRequestUrlPropertyName();
				}
				break;
			case form:
				if(introspectionService) {
					positionTokenName = policyGestioneToken.getIntrospectionTipoTokenRequestFormPropertyName();
				}else {
					positionTokenName = policyGestioneToken.getUserInfoTipoTokenRequestFormPropertyName();
				}
				break;
			}
		}
		if(positionTokenName!=null && !"".equals(positionTokenName)) {
			positionTokenName = DynamicUtils.convertDynamicPropertyValue("positionTokenName.gwt", positionTokenName, dynamicMap, pddContext);	
		}
		
		String contentType = null;
		if(introspectionService || userInfoService) {
			if(introspectionService) {
				contentType = policyGestioneToken.getIntrospectionContentType();
			}else {
				contentType = policyGestioneToken.getUserInfoContentType();
			}
		}
		if(contentType!=null && !"".equals(contentType)) {
			contentType = DynamicUtils.convertDynamicPropertyValue("contentType.gwt", contentType, dynamicMap, pddContext);	
		}
		
		HttpRequestMethod httpMethod = null;
		switch (httpType) {
		case DYNAMIC_DISCOVERY:
			httpMethod = HttpRequestMethod.GET;
			break;
		case INTROSPECTION:
			httpMethod = policyGestioneToken.getIntrospectionHttpMethod();
			break;
		case USER_INFO:
			httpMethod = policyGestioneToken.getUserInfoHttpMethod();
			break;
		}
		
		
		
		// Nell'endpoint config ci finisce i timeout e la configurazione proxy
		Properties endpointConfig = policyGestioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_CONFIG);
		resolveDynamicProperyValues(endpointConfig, dynamicMap, pddContext);
		if(endpointConfig.containsKey(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME)) {
			String hostProxy = endpointConfig.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME);
			String portProxy = endpointConfig.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT);
			prefixConnettore = prefixConnettore+GestoreToken.getMessageViaProxy(hostProxy, portProxy);
		}
		
		boolean https = false;
		if(!https) {
			switch (httpType) {
			case DYNAMIC_DISCOVERY:
				https = policyGestioneToken.isEndpointHttps(false, false);
				break;
			case INTROSPECTION:
				https = policyGestioneToken.isEndpointHttps(true, false);
				break;
			case USER_INFO:
				https = policyGestioneToken.isEndpointHttps(false, true);
				break;
			}
		}
		boolean httpsClient = false;
		Properties sslConfig = null;
		Properties sslClientConfig = null;
		if(https) {
			sslConfig = policyGestioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
			resolveDynamicProperyValues(sslConfig, dynamicMap, pddContext);
			if(introspectionService || userInfoService) {
				if(introspectionService) {
					httpsClient = policyGestioneToken.isIntrospectionHttpsAuthentication();
				}else {
					httpsClient = policyGestioneToken.isUserInfoHttpsAuthentication();
				}
			}
			if(httpsClient) {
				sslClientConfig = policyGestioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
				TokenUtilities.injectSameKeystoreForHttpsClient(sslConfig, sslClientConfig);
				resolveDynamicProperyValues(sslClientConfig, dynamicMap, pddContext);
			}
		}
		
		boolean basic = false;
		String username = null;
		String password = null;
		if(introspectionService || userInfoService) {
			if(introspectionService) {
				basic = policyGestioneToken.isIntrospectionBasicAuthentication();
			}else {
				basic = policyGestioneToken.isUserInfoBasicAuthentication();
			}
		}
		if(basic) {
			if(introspectionService) {
				username = policyGestioneToken.getIntrospectionBasicAuthenticationUsername();
				password = policyGestioneToken.getIntrospectionBasicAuthenticationPassword();
			}
			else {
				username = policyGestioneToken.getUserInfoBasicAuthenticationUsername();
				password = policyGestioneToken.getUserInfoBasicAuthenticationPassword();
			}
			if(username!=null && !"".equals(username)) {
				username = DynamicUtils.convertDynamicPropertyValue("username.gwt", username, dynamicMap, pddContext);	
			}
			if(password!=null && !"".equals(password)) {
				password = DynamicUtils.convertDynamicPropertyValue("password.gwt", password, dynamicMap, pddContext);	
			}
		}
		
		boolean bearer = false;
		String bearerToken = null;
		if(introspectionService || userInfoService) {
			if(introspectionService) {
				bearer = policyGestioneToken.isIntrospectionBearerAuthentication();
			}else {
				bearer = policyGestioneToken.isUserInfoBearerAuthentication();
			}
		}
		if(bearer) {
			if(introspectionService) {
				bearerToken = policyGestioneToken.getIntrospectionBeareAuthenticationToken();
			}
			else {
				bearerToken = policyGestioneToken.getUserInfoBeareAuthenticationToken();
			}
			if(bearerToken!=null && !"".equals(bearerToken)) {
				bearerToken = DynamicUtils.convertDynamicPropertyValue("bearerToken.gwt", bearerToken, dynamicMap, pddContext);	
			}
		}
		
		
		
		// *** Definizione Connettore ***
		
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
		switch (httpType) {
		case DYNAMIC_DISCOVERY:
			policyConfig.setPolicyValidazioneDynamicDiscovery(policyGestioneToken.getName());	
			break;
		case INTROSPECTION:
			policyConfig.setPolicyValidazioneIntrospection(policyGestioneToken.getName());
			break;
		case USER_INFO:
			policyConfig.setPolicyValidazioneUserInfo(policyGestioneToken.getName());
			break;
		}
		connettoreMsg.setPolicyTimeoutConfig(policyConfig);
		
		ForwardProxy forwardProxy = TokenUtilities.getForwardProxy(policyGestioneToken,
				requestInfo, state, delegata,
				idDominio, idServizio);
		if(forwardProxy!=null && forwardProxy.isEnabled() && forwardProxy.getConfigToken()!=null) {
			boolean enabled = false;
			switch (httpType) {
			case DYNAMIC_DISCOVERY:
				enabled = forwardProxy.getConfigToken().isTokenDynamicDiscoveryEnabled();
				break;
			case INTROSPECTION:
				enabled = forwardProxy.getConfigToken().isTokenIntrospectionEnabled();
				break;
			case USER_INFO:
				enabled = forwardProxy.getConfigToken().isTokenUserInfoEnabled();
				break;
			}
			if(enabled) {
				connettoreMsg.setForwardProxy(forwardProxy);
			}
		}
		
		connettore.setForceDisableProxyPassReverse(true);
		connettore.init(pddContext, protocolFactory);
		connettore.setRegisterSendIntoContext(false);
		
		if(basic){
			InvocazioneCredenziali credenziali = new InvocazioneCredenziali();
			credenziali.setUser(username);
			credenziali.setPassword(password);
			connettoreMsg.setCredenziali(credenziali);
		}
		
		connettoreMsg.setConnectorProperties(new java.util.HashMap<>());
		connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_LOCATION, endpoint);
		boolean debug = false;
		OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
		switch (httpType) {
		case DYNAMIC_DISCOVERY:
			debug = properties.isGestioneTokenDynamicDiscoveryDebug();
			break;
		case INTROSPECTION:
			debug = properties.isGestioneTokenIntrospectionDebug();	
			break;
		case USER_INFO:
			debug = properties.isGestioneTokenUserInfoDebug();
			break;
		}
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
		if(httpMethod!=null) {
			transportRequestContext.setRequestType(httpMethod.name());
		}
		transportRequestContext.setHeaders(new HashMap<>());
		if(bearer) {
			String authorizationHeader = HttpConstants.AUTHORIZATION_PREFIX_BEARER+bearerToken;
			TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.AUTHORIZATION, authorizationHeader);
		}
		if(contentType!=null) {
			TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.CONTENT_TYPE, contentType);
		}
		if(tipoTokenRequest!=null) {
			switch (tipoTokenRequest) {
			case authorization:
				transportRequestContext.removeHeader(HttpConstants.AUTHORIZATION);
				String authorizationHeader = HttpConstants.AUTHORIZATION_PREFIX_BEARER+token;
				TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.AUTHORIZATION, authorizationHeader);
				break;
			case header:
				TransportUtils.setHeader(transportRequestContext.getHeaders(),positionTokenName, token);
				break;
			case url:
				transportRequestContext.setParameters(new HashMap<>());
				TransportUtils.setParameter(transportRequestContext.getParameters(),positionTokenName, token);
				break;
			case form:
				transportRequestContext.removeHeader(HttpConstants.CONTENT_TYPE);
				TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_X_WWW_FORM_URLENCODED);
				content = (positionTokenName+"="+token).getBytes();
				break;
			}
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
	
	private static void resolveDynamicProperyValues(Properties p,
			Map<String, Object> dynamicMap, Context context) throws DynamicException {
		if(p!=null && !p.isEmpty()) {
			Enumeration<?> oKey = p.keys();
			while (oKey.hasMoreElements()) {
				Object object = oKey.nextElement();
				if(object instanceof String) {
					String key = (String) object;
					String value = p.getProperty(key);
					if(value!=null && !"".equals(value) && value.contains("{") && value.contains("}")) {
						value = DynamicUtils.convertDynamicPropertyValue(key+".gwt", value, dynamicMap, context);
						p.put(key, value);
					}			
				}
			}
		}
	}

	static void check(DynamicDiscovery dynamicDiscovery) throws TokenException {
		if(dynamicDiscovery==null) {
			throw new TokenException("DynamicDiscovery information not found");
		}
		if(!dynamicDiscovery.isValid()) {
			throw new TokenException("DynamicDiscovery failed");
		}
	}
	
}

enum HTTP_TYPE {
	
	DYNAMIC_DISCOVERY, INTROSPECTION, USER_INFO
	
}