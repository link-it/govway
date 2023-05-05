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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rt.security.rs.RSSecurityConstants;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDGenericProperties;
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
import org.openspcoop2.pdd.config.ForwardProxy;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreBaseHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreHTTPS;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.token.pa.EsitoGestioneTokenPortaApplicativa;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.pdd.core.token.parser.ITokenParser;
import org.openspcoop2.pdd.core.token.pd.EsitoGestioneTokenPortaDelegata;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.RestMessageSecurityToken;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.CRLCertstore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.keystore.cache.GestoreOCSPResource;
import org.openspcoop2.security.keystore.cache.GestoreOCSPValidator;
import org.openspcoop2.security.message.jose.JOSEUtils;
import org.openspcoop2.utils.LoggerBuffer;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
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
	
	
	// ********* [VALIDAZIONE-TOKEN] VALIDAZIONE JWT TOKEN ****************** */
	
	static EsitoGestioneToken validazioneJWTTokenEngine(Logger log, AbstractDatiInvocazione datiInvocazione, EsitoPresenzaToken esitoPresenzaToken, String token, boolean portaDelegata, PdDContext pddContext) {
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
			
			ITokenParser tokenParser = policyGestioneToken.getValidazioneJWT_TokenParser();
			
    		if(Costanti.POLICY_TOKEN_TYPE_JWS.equals(tokenType)) {
    			// JWS Compact   			
    			JsonVerifySignature jsonCompactVerify = null;
    			try {
    				jsonCompactVerify = getJsonVerifySignatureJWS(log, datiInvocazione, policyGestioneToken);
    				
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
    				JWTOptions options = new JWTOptions(JOSESerialization.COMPACT);
    				Properties p = policyGestioneToken.getProperties().get(Costanti.POLICY_VALIDAZIONE_JWE_DECRYPT_PROP_REF_ID);
    				JOSEUtils.injectKeystore(datiInvocazione.getRequestInfo(), p, log); // serve per leggere il keystore dalla cache
    				jsonDecrypt = new JsonDecrypt(p, options);
    				jsonDecrypt.decrypt(token);
    				informazioniToken = new InformazioniToken(SorgenteInformazioniToken.JWT,jsonDecrypt.getDecodedPayload(),tokenParser);
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
    			esitoGestioneToken.setNoCache(!policyGestioneToken.isValidazioneJWT_saveErrorInCache());
    			esitoGestioneToken.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoGestioneToken.setDetails(detailsError);	
				}
				else {
					esitoGestioneToken.setDetails(GestoreToken.TOKEN_NON_VALIDO);	
				}
    			
    			// comunque lo aggiungo per essere consultabile nei casi di errore
    			if(OpenSPCoop2Properties.getInstance().isGestioneToken_saveTokenInfo_validationFailed()) {
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
	private static JsonVerifySignature getJsonVerifySignatureJWS(Logger log, AbstractDatiInvocazione datiInvocazione, PolicyGestioneToken policyGestioneToken) throws TokenException, UtilsException, SecurityException {
		// JWS Compact   			
		JsonVerifySignature jsonCompactVerify = null;

		JWTOptions options = new JWTOptions(JOSESerialization.COMPACT);
		Properties p = policyGestioneToken.getProperties().get(Costanti.POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID);
		JOSEUtils.injectKeystore(datiInvocazione.getRequestInfo(), p, log); // serve per leggere il keystore dalla cache
		
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
			/**options.setPermitUseHeaderX5T(aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5T256) || aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C_X5T256));*/
			options.setPermitUseHeaderX5T_256(aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5T256) || aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5C_X5T256));
			options.setPermitUseHeaderKID(aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_KID));
			options.setPermitUseHeaderX5U(aliasMode.equals(Costanti.ID_VALIDAZIONE_JWT_TRUSTSTORE_TYPE_SELECT_CERTIFICATE_VALUE_X5U));
									
			if(p.containsKey(RSSecurityConstants.RSSEC_KEY_STORE)) {
				Object oKeystore = p.get(RSSecurityConstants.RSSEC_KEY_STORE);
				if(oKeystore instanceof java.security.KeyStore) {
					java.security.KeyStore keystore = (java.security.KeyStore) oKeystore;
					jsonCompactVerify = new JsonVerifySignature(keystore, options);
					
					String signatureOCSP = policyGestioneToken.getValidazioneJWT_ocspPolicy();
					String signatureCRL = policyGestioneToken.getValidazioneJWT_crl();
					
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
			else if(p.containsKey(JoseConstants.RSSEC_KEY_STORE_JWKSET)) {
				Object oKeystore = p.get(JoseConstants.RSSEC_KEY_STORE_JWKSET);
				if(oKeystore instanceof String) {
					String keystore = (String) oKeystore;
					JsonWebKeys jwksKeystore = new JWKSet(keystore).getJsonWebKeys();
					jsonCompactVerify = new JsonVerifySignature(jwksKeystore, options);
				}
			}
		}
		    				
		if(jsonCompactVerify==null &&
			p.containsKey(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS)) {
			String alias = p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_ALIAS);
			if(alias!=null && 
					(
							alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5C)
							||
							alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5T)
							||
							alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5C_X5T)
					)
				&&
				(p.containsKey(RSSecurityConstants.RSSEC_KEY_STORE)) // backward compatibility
				){
				Object oKeystore = p.get(RSSecurityConstants.RSSEC_KEY_STORE);
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
		
		return jsonCompactVerify;
	}
	
	
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] USER INFO TOKEN ****************** */
	
	static EsitoGestioneToken userInfoTokenEngine(Logger log, AbstractDatiInvocazione datiInvocazione, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			String token, boolean portaDelegata,
			IDSoggetto idDominio, IDServizio idServizio) {
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
			
			ITokenParser tokenParser = policyGestioneToken.getUserInfo_TokenParser();
			
			HttpResponse httpResponse = null;
			Integer httpResponseCode = null;
			byte[] risposta = null;
			try {
				httpResponse = http(log, policyGestioneToken, USER_INFO, token,
						pddContext, protocolFactory,
						datiInvocazione.getState(), portaDelegata,
						idDominio, idServizio,
						datiInvocazione.getRequestInfo());
				risposta = httpResponse.getContent();
				httpResponseCode = httpResponse.getResultHTTPOperation();
			}catch(Exception e) {
				detailsError = "(Errore di Connessione) "+ e.getMessage();
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
    			esitoGestioneToken.setNoCache(!policyGestioneToken.isUserInfo_saveErrorInCache());
    			esitoGestioneToken.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoGestioneToken.setDetails(detailsError);	
				}
				else {
					esitoGestioneToken.setDetails(GestoreToken.TOKEN_NON_VALIDO);	
				}
    			
    			// comunque lo aggiungo per essere consultabile nei casi di errore se una connessione http è terminata
    			if(OpenSPCoop2Properties.getInstance().isGestioneToken_saveSourceTokenInfo() && httpResponseCode!=null) {
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
			boolean forwardUserInfo, String forwardUserInfoMode, String forwardUserInfoName) throws Exception {
		
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
				headerNames = properties.getKeyValue_gestioneTokenHeaderIntegrazioneTrasporto();
				if(portaDelegata) {
					set = properties.getKeyPDSetEnabled_gestioneTokenHeaderIntegrazioneTrasporto();
				}
				else {
					set = properties.getKeyPASetEnabled_gestioneTokenHeaderIntegrazioneTrasporto();
				}
				String pattern = properties.getGestioneTokenFormatDate();
				if(pattern!=null && !"".equals(pattern)) {
					sdf = DateUtils.getDefaultDateTimeFormatter(pattern);
				}
			}
			else {
				if(portaDelegata) {
					set = properties.getKeyPDSetEnabled_gestioneTokenHeaderIntegrazioneJson();
				}
				else {
					set = properties.getKeyPASetEnabled_gestioneTokenHeaderIntegrazioneJson();
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
							bf.append(properties.getGestioneTokenHeaderIntegrazioneTrasporto_audienceSeparator());
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
							bf.append(properties.getGestioneTokenHeaderIntegrazioneTrasporto_roleSeparator());
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
							bf.append(properties.getGestioneTokenHeaderIntegrazioneTrasporto_scopeSeparator());
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
			}
			List<String> listCustomClaims = properties.getCustomClaimsKeys_gestioneTokenForward();
			if(listCustomClaims!=null && !listCustomClaims.isEmpty()) {
				
				ArrayNode customClaimsNode = null;
				if(!op2headers) {
					customClaimsNode = jsonUtils.newArrayNode();
				}
				
				boolean add = false;
				
				for (String claimKey : listCustomClaims) {
				
					String claimName = properties.getCustomClaimsName_gestioneTokenHeaderIntegrazione(claimKey);
					
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
								headerName = properties.getCustomClaimsHeaderName_gestioneTokenHeaderIntegrazioneTrasporto(claimKey);
								if(portaDelegata) {
									setCustomClaims = properties.getCustomClaimsKeyPDSetEnabled_gestioneTokenHeaderIntegrazioneTrasporto(claimKey);
								}
								else {
									setCustomClaims = properties.getCustomClaimsKeyPASetEnabled_gestioneTokenHeaderIntegrazioneTrasporto(claimKey);
								}
							}
							else {
								headerName = properties.getCustomClaimsJsonPropertyName_gestioneTokenHeaderIntegrazioneJson(claimKey);
								if(portaDelegata) {
									setCustomClaims = properties.getCustomClaimsKeyPDSetEnabled_gestioneTokenHeaderIntegrazioneJson(claimKey);
								}
								else {
									setCustomClaims = properties.getCustomClaimsKeyPASetEnabled_gestioneTokenHeaderIntegrazioneJson(claimKey);
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
	
	static void validazioneInformazioniToken(EsitoGestioneToken esitoGestioneToken, PolicyGestioneToken policyGestioneToken, boolean saveErrorInCache) throws Exception {
		
		Date now = DateManager.getDate();
		
		if(esitoGestioneToken.isValido()) {
			esitoGestioneToken.setDateValide(true); // tanto le ricontrollo adesso
		}
			
		if(esitoGestioneToken.isValido()) {
			
			boolean enabled = OpenSPCoop2Properties.getInstance().isGestioneToken_expTimeCheck();
			
			if(enabled && esitoGestioneToken.getInformazioniToken().getExp()!=null) {	
				
				Date checkNow = now;
				Long tolerance = OpenSPCoop2Properties.getInstance().getGestioneToken_expTimeCheck_tolerance_milliseconds();
				if(tolerance!=null && tolerance.longValue()>0) {
					checkNow = new Date(now.getTime() - tolerance.longValue());
				}

				/*
				 *   The "exp" (expiration time) claim identifies the expiration time on
   				 *   or after which the JWT MUST NOT be accepted for processing.  The
   				 *   processing of the "exp" claim requires that the current date/time
   				 *   MUST be before the expiration date/time listed in the "exp" claim.
				 **/
				if(!checkNow.before(esitoGestioneToken.getInformazioniToken().getExp())){
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
			
		if(esitoGestioneToken.isValido() &&
			esitoGestioneToken.getInformazioniToken().getNbf()!=null &&			
			/*
			 *   The "nbf" (not before) claim identifies the time before which the JWT
			 *   MUST NOT be accepted for processing.  The processing of the "nbf"
			 *   claim requires that the current date/time MUST be after or equal to
			 *   the not-before date/time listed in the "nbf" claim. 
			 **/
			!esitoGestioneToken.getInformazioniToken().getNbf().before(now)
			){
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
		
		if(esitoGestioneToken.isValido() &&
			esitoGestioneToken.getInformazioniToken().getIat()!=null) {				
			/*
			 *   The "iat" (issued at) claim identifies the time at which the JWT was
			 *   issued.  This claim can be used to determine the age of the JWT.
			 *   The iat Claim can be used to reject tokens that were issued too far away from the current time, 
			 *   limiting the amount of time that nonces need to be stored to prevent attacks. The acceptable range is Client specific. 
			 **/
			Long old = OpenSPCoop2Properties.getInstance().getGestioneToken_iatTimeCheck_milliseconds();
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
			
			Long future = OpenSPCoop2Properties.getInstance().getGestioneToken_iatTimeCheck_futureTolerance_milliseconds();
			if(future!=null) {
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
	
	static final boolean INTROSPECTION = true;
	static final boolean USER_INFO = false;
	static HttpResponse http(Logger log, PolicyGestioneToken policyGestioneToken, boolean introspection, String token,
			PdDContext pddContext, IProtocolFactory<?> protocolFactory, 
			IState state, boolean delegata,
			IDSoggetto idDominio, IDServizio idServizio,
			RequestInfo requestInfo) throws Exception {
		
		// *** Raccola Parametri ***
		
		String endpoint = null;
		String prefixConnettore = null;
		if(introspection) {
			endpoint = policyGestioneToken.getIntrospection_endpoint();
			prefixConnettore = "[EndpointIntrospection: "+endpoint+"] ";
		}else {
			endpoint = policyGestioneToken.getUserInfo_endpoint();
			prefixConnettore = "[EndpointUserInfo: "+endpoint+"] ";
		}
		
		TipoTokenRequest tipoTokenRequest = null;
		String positionTokenName = null;
		if(introspection) {
			tipoTokenRequest = policyGestioneToken.getIntrospection_tipoTokenRequest();
		}else {
			tipoTokenRequest = policyGestioneToken.getUserInfo_tipoTokenRequest();
		}
		switch (tipoTokenRequest) {
		case authorization:
			break;
		case header:
			if(introspection) {
				positionTokenName = policyGestioneToken.getIntrospection_tipoTokenRequest_headerName();
			}else {
				positionTokenName = policyGestioneToken.getUserInfo_tipoTokenRequest_headerName();
			}
			break;
		case url:
			if(introspection) {
				positionTokenName = policyGestioneToken.getIntrospection_tipoTokenRequest_urlPropertyName();
			}else {
				positionTokenName = policyGestioneToken.getUserInfo_tipoTokenRequest_urlPropertyName();
			}
			break;
		case form:
			if(introspection) {
				positionTokenName = policyGestioneToken.getIntrospection_tipoTokenRequest_formPropertyName();
			}else {
				positionTokenName = policyGestioneToken.getUserInfo_tipoTokenRequest_formPropertyName();
			}
			break;
		}
		
		String contentType = null;
		if(introspection) {
			contentType = policyGestioneToken.getIntrospection_contentType();
		}else {
			contentType = policyGestioneToken.getUserInfo_contentType();
		}
		
		HttpRequestMethod httpMethod = null;
		if(introspection) {
			httpMethod = policyGestioneToken.getIntrospection_httpMethod();
		}else {
			httpMethod = policyGestioneToken.getUserInfo_httpMethod();
		}
		
		
		// Nell'endpoint config ci finisce i timeout e la configurazione proxy
		Properties endpointConfig = policyGestioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_CONFIG);
		if(endpointConfig.containsKey(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME)) {
			String hostProxy = endpointConfig.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME);
			String portProxy = endpointConfig.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT);
			prefixConnettore = prefixConnettore+GestoreToken.getMessageViaProxy(hostProxy, portProxy);
		}
		
		boolean https = policyGestioneToken.isEndpointHttps();
		boolean httpsClient = false;
		Properties sslConfig = null;
		Properties sslClientConfig = null;
		if(https) {
			sslConfig = policyGestioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
			if(introspection) {
				httpsClient = policyGestioneToken.isIntrospection_httpsAuthentication();
			}else {
				httpsClient = policyGestioneToken.isUserInfo_httpsAuthentication();
			}
			if(httpsClient) {
				sslClientConfig = policyGestioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
			}
		}
		
		boolean basic = false;
		String username = null;
		String password = null;
		if(introspection) {
			basic = policyGestioneToken.isIntrospection_basicAuthentication();
		}else {
			basic = policyGestioneToken.isUserInfo_basicAuthentication();
		}
		if(basic) {
			if(introspection) {
				username = policyGestioneToken.getIntrospection_basicAuthentication_username();
				password = policyGestioneToken.getIntrospection_basicAuthentication_password();
			}
			else {
				username = policyGestioneToken.getUserInfo_basicAuthentication_username();
				password = policyGestioneToken.getUserInfo_basicAuthentication_password();
			}
		}
		
		boolean bearer = false;
		String bearerToken = null;
		if(introspection) {
			bearer = policyGestioneToken.isIntrospection_bearerAuthentication();
		}else {
			bearer = policyGestioneToken.isUserInfo_bearerAuthentication();
		}
		if(bearer) {
			if(introspection) {
				bearerToken = policyGestioneToken.getIntrospection_beareAuthentication_token();
			}
			else {
				bearerToken = policyGestioneToken.getUserInfo_beareAuthentication_token();
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
		
		ForwardProxy forwardProxy = null;
		ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
		if(configurazionePdDManager.isForwardProxyEnabled(requestInfo)) {
			try {
				IDGenericProperties policy = new IDGenericProperties();
				policy.setTipologia(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION);
				policy.setNome(policyGestioneToken.getName());
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
		if(forwardProxy!=null && forwardProxy.isEnabled() && forwardProxy.getConfigToken()!=null &&
				(
					(introspection && forwardProxy.getConfigToken().isTokenIntrospectionEnabled())
					|| 
					(!introspection && forwardProxy.getConfigToken().isTokenUserInfoEnabled()) 
				)
			){
			connettoreMsg.setForwardProxy(forwardProxy);
		}
		
		connettore.setForceDisable_proxyPassReverse(true);
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
		if(introspection) {
			debug = properties.isGestioneToken_introspection_debug();	
		}
		else {
			debug = properties.isGestioneToken_userInfo_debug();
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
		transportRequestContext.setRequestType(httpMethod.name());
		transportRequestContext.setHeaders(new HashMap<>());
		if(bearer) {
			String authorizationHeader = HttpConstants.AUTHORIZATION_PREFIX_BEARER+bearerToken;
			TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.AUTHORIZATION, authorizationHeader);
		}
		if(contentType!=null) {
			TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.CONTENT_TYPE, contentType);
		}
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
		
		OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(MessageType.BINARY, transportRequestContext, content);
		OpenSPCoop2Message msg = pr.getMessage_throwParseException();
		connettoreMsg.setRequestMessage(msg);
		connettoreMsg.setGenerateErrorWithConnectorPrefix(false);
		connettore.setHttpMethod(msg);
		
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
	
}
