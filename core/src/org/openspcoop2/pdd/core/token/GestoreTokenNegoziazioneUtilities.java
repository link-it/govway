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
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TipoPdD;
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
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.pdd.core.token.parser.ClaimsNegoziazione;
import org.openspcoop2.pdd.core.token.parser.INegoziazioneTokenParser;
import org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.JWKSetStore;
import org.openspcoop2.security.keystore.KeyPairStore;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.JwtHeaders;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**     
 * GestoreTokenNegoziazioneUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreTokenNegoziazioneUtilities {
	
	private GestoreTokenNegoziazioneUtilities() {}

	static EsitoNegoziazioneToken endpointTokenEngine(boolean debug, Logger log, PolicyNegoziazioneToken policyNegoziazioneToken,
			Busta busta, RequestInfo requestInfo, TipoPdD tipoPdD,
			NegoziazioneTokenDynamicParameters dynamicParameters, IProtocolFactory<?> protocolFactory,
			IState state, boolean delegata, String idModulo, PortaApplicativa pa, PortaDelegata pd,
			IDSoggetto idDominio, IDServizio idServizio,
			InformazioniNegoziazioneToken previousToken,
			InformazioniNegoziazioneToken_DatiRichiesta datiRichiesta) {
		
		EsitoNegoziazioneToken esito = null;
		
		boolean refreshModeEnabled = false;
		try {
			if(policyNegoziazioneToken.isClientCredentialsGrant()) {
				refreshModeEnabled = OpenSPCoop2Properties.getInstance().isGestioneRetrieveToken_refreshToken_grantType_clientCredentials();	
				if(datiRichiesta!=null) {
					datiRichiesta.setGrantType(Costanti.ID_RETRIEVE_TOKEN_METHOD_CLIENT_CREDENTIAL);
				}
			}
			else if(policyNegoziazioneToken.isUsernamePasswordGrant()) {
				refreshModeEnabled = OpenSPCoop2Properties.getInstance().isGestioneRetrieveToken_refreshToken_grantType_usernamePassword();	
				if(datiRichiesta!=null) {
					datiRichiesta.setGrantType(Costanti.ID_RETRIEVE_TOKEN_METHOD_USERNAME_PASSWORD);
				}
			}
			else if(policyNegoziazioneToken.isRfc7523x509Grant()) {
				refreshModeEnabled = OpenSPCoop2Properties.getInstance().isGestioneRetrieveToken_refreshToken_grantType_rfc7523_x509();	
				if(datiRichiesta!=null) {
					datiRichiesta.setGrantType(Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_X509);
				}
			}
			else if(policyNegoziazioneToken.isRfc7523ClientSecretGrant()) {
				refreshModeEnabled = OpenSPCoop2Properties.getInstance().isGestioneRetrieveToken_refreshToken_grantType_rfc7523_clientSecret();
				if(datiRichiesta!=null) {
					datiRichiesta.setGrantType(Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_CLIENT_SECRET);
				}
			}
			else if(policyNegoziazioneToken.isCustomGrant()) {
				refreshModeEnabled = OpenSPCoop2Properties.getInstance().isGestioneRetrieveToken_refreshToken_grantType_custom();	
				if(datiRichiesta!=null) {
					datiRichiesta.setGrantType(Costanti.ID_RETRIEVE_TOKEN_METHOD_CUSTOM);
				}
			}
		}catch(Exception t) {
			log.error("Errore durante la comprensione della modalità di refresh: "+t.getMessage(),t);
		}
		
		
		if(refreshModeEnabled && previousToken!=null && previousToken.getRefreshToken()!=null) {
			try {
				// Verico scadenza refresh
				if(previousToken.getRefreshExpiresIn()!=null) {
					Date now = DateManager.getDate();
					int secondsPreExpire = -1;
					OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
					if(properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_percent()!=null && properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_percent()>0) {
						int percent = properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_percent();
						long secondsExpire = previousToken.getRefreshExpiresIn().getTime() / 1000l;
						long secondsIat = previousToken.getRetrievedRefreshTokenIn().getTime() / 1000l;
						long secondsDiff = secondsExpire - secondsIat;
						if(secondsDiff>0) {
							float perc1 = Float.parseFloat(secondsDiff+"") / 100f;
							float perc2 = perc1 * Float.parseFloat(percent+"");
							int s = Math.round(perc2);
							if(s>0) {
								secondsPreExpire = s;
							}
						}
					}
					else if(properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_seconds()!=null && properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_seconds()>0) {
						secondsPreExpire = properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_seconds();
					}
					if(secondsPreExpire>0) {
						now = new Date(now.getTime() + (secondsPreExpire*1000));
						/**System.out.println("Controllo scadenza per refresh token now+tollerance("+secondsPreExpire+")["+org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(now)+"] exp["+
						//		org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(previousToken.getRefreshExpiresIn())+"]");*/
					}			
					if(!now.before(previousToken.getRefreshExpiresIn())){
						// scaduto refresh token
						log.debug("Refresh token scaduto");
						refreshModeEnabled = false;
					}
				}
				if(refreshModeEnabled) {
					esito = invokeEndpointToken(debug, log, policyNegoziazioneToken,
							busta, requestInfo, tipoPdD,
							dynamicParameters, protocolFactory, 
							state, delegata, idModulo, pa, pd,
							idDominio, idServizio,
							refreshModeEnabled, previousToken,
							datiRichiesta);
					if(esito!=null && esito.isValido()) {
						// ricontrollo tutte le date (l'ho appena preso, dovrebbero essere buone) 
						boolean checkPerRinegoziazione = false;
						validazioneInformazioniNegoziazioneToken(checkPerRinegoziazione, esito,  
								policyNegoziazioneToken.isSaveErrorInCache());
						if(datiRichiesta!=null) {
							datiRichiesta.setRefresh(true);
						}
						return esito;
					}
					if(esito==null || esito.getEccezioneProcessamento()==null) {
						throw new TokenException("token not refreshed");
					}
					else {
						throw new TokenException("token not refreshed: "+esito.getEccezioneProcessamento());
					}
				}
			}catch(Exception t) {
				String msgError = "Refresh token failed: "+t.getMessage();
				log.error(msgError);
			}
		}
		
		if(datiRichiesta!=null) {
			datiRichiesta.setRefresh(null);
		}
		return invokeEndpointToken(debug, log, policyNegoziazioneToken,
				busta, requestInfo, tipoPdD,
				dynamicParameters, protocolFactory, 
				state, delegata, idModulo, pa, pd,
				idDominio, idServizio,
				false, null,
				datiRichiesta);
		
	}
	
	
	private static EsitoNegoziazioneToken invokeEndpointToken(boolean debug, Logger log, PolicyNegoziazioneToken policyNegoziazioneToken,
			Busta busta, RequestInfo requestInfo, TipoPdD tipoPdD,
			NegoziazioneTokenDynamicParameters dynamicParameters, IProtocolFactory<?> protocolFactory,
			IState state, boolean delegata, String idModulo, PortaApplicativa pa, PortaDelegata pd,
			IDSoggetto idDominio, IDServizio idServizio,
			boolean refreshModeEnabled, InformazioniNegoziazioneToken previousToken,
			InformazioniNegoziazioneToken_DatiRichiesta datiRichiesta) {
	
		EsitoNegoziazioneToken esitoNegoziazioneToken = new EsitoNegoziazioneToken();
		
		esitoNegoziazioneToken.setTokenInternalError();
		
		try{
			String detailsError = null;
			InformazioniNegoziazioneToken informazioniToken = null;
			Exception eProcess = null;
			
			INegoziazioneTokenParser tokenParser = policyNegoziazioneToken.getNegoziazioneTokenParser();
			
			HttpResponse httpResponse = null;
			Integer httpResponseCode = null;
			byte[] risposta = null;
			try {
				httpResponse = http(debug, log, policyNegoziazioneToken,
						busta, requestInfo, tipoPdD,
						dynamicParameters, protocolFactory, 
						state, delegata, idModulo, pa, pd,
						idDominio, idServizio,
						refreshModeEnabled, (refreshModeEnabled && previousToken!=null) ? previousToken.getRefreshToken() : null,
						datiRichiesta);
				risposta = httpResponse.getContent();
				httpResponseCode = httpResponse.getResultHTTPOperation();
			}catch(Exception e) {
				detailsError = "(Errore di Connessione) "+ e.getMessage();
				eProcess = e;
			}
			
			if(detailsError==null) {
				try {
					if(datiRichiesta!=null && datiRichiesta.getPrepareRequest()!=null) {
						datiRichiesta.setParseResponse(DateManager.getDate());
					}
					informazioniToken = new InformazioniNegoziazioneToken(datiRichiesta, httpResponseCode, new String(risposta),tokenParser,previousToken);
				}catch(Exception e) {
					detailsError = "Risposta del servizio di negoziazione token non valida: "+e.getMessage();
					eProcess = e;
				}
			}
    		  		
    		if(informazioniToken!=null && informazioniToken.isValid()) {
    			esitoNegoziazioneToken.setTokenValido();
    			esitoNegoziazioneToken.setInformazioniNegoziazioneToken(informazioniToken);
    			esitoNegoziazioneToken.setToken(informazioniToken.getAccessToken());
    			esitoNegoziazioneToken.setNoCache(false);
			}
    		else {
    			esitoNegoziazioneToken.setTokenValidazioneFallita();
    			esitoNegoziazioneToken.setNoCache(!policyNegoziazioneToken.isSaveErrorInCache());
    			esitoNegoziazioneToken.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoNegoziazioneToken.setDetails(detailsError);	
				}
				else {
					esitoNegoziazioneToken.setDetails("AccessToken non recuperabile");	
				} 
    			
    			// comunque lo aggiungo per essere consultabile nei casi di errore
    			if(OpenSPCoop2Properties.getInstance().isGestioneRetrieveToken_saveTokenInfo_retrieveFailed()) {
    				if(informazioniToken!=null) {
		    			if(httpResponseCode!=null) {
		    				informazioniToken.setHttpResponseCode(httpResponseCode+"");
		    			}
		    			informazioniToken.setErrorDetails(esitoNegoziazioneToken.getDetails());
    				}
    				else {
    					informazioniToken = new InformazioniNegoziazioneToken(datiRichiesta,
    							esitoNegoziazioneToken.getDetails(), httpResponseCode, risposta);
    				}
    				esitoNegoziazioneToken.setInformazioniNegoziazioneToken(informazioniToken); 
    			}
    		}
    		
		}catch(Exception e){
			esitoNegoziazioneToken.setTokenInternalError();
			esitoNegoziazioneToken.setDetails(e.getMessage());
			esitoNegoziazioneToken.setEccezioneProcessamento(e);
    	}
		
		if(datiRichiesta!=null && datiRichiesta.getPrepareRequest()!=null) {
			datiRichiesta.setProcessComplete(DateManager.getDate());
		}
		return esitoNegoziazioneToken;
	}
	
	static String buildPrefixCacheKeyNegoziazione(String policy, String funzione) {
		StringBuilder bf = new StringBuilder(funzione);
    	bf.append("_");
    	bf.append(policy);
    	bf.append("_");
    	return bf.toString();
	}
	static String buildCacheKeyNegoziazione(String policy, String funzione, boolean portaDelegata, NegoziazioneTokenDynamicParameters dynamicParameters) {
    	StringBuilder bf = new StringBuilder();
    	bf.append(buildPrefixCacheKeyNegoziazione(policy, funzione));
    	if(portaDelegata){ // serve per non aver classcast exception nei risultati
    		bf.append("PD");
    	}
    	else {
    		bf.append("PA");
    	}
    	bf.append("_");
    	
    	String dynamicParametersKeyCache = dynamicParameters.toString("_", true);
    	bf.append(dynamicParametersKeyCache);
    	
    	return bf.toString();
    }	
	static void validazioneInformazioniNegoziazioneToken(boolean checkPerRinegoziazione, EsitoNegoziazioneToken esitoNegoziazioneToken, boolean saveErrorInCache) {
		
		Date now = DateManager.getDate();
		
		if(esitoNegoziazioneToken.isValido()) {
			esitoNegoziazioneToken.setDateValide(true); // tanto le ricontrollo adesso
		}
		
		if(esitoNegoziazioneToken.isValido() &&		
			esitoNegoziazioneToken.getInformazioniNegoziazioneToken().getExpiresIn()!=null) {			
				
			if(checkPerRinegoziazione) {
				int secondsPreExpire = -1;
				OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
				if(properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_percent()!=null && properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_percent()>0) {
					int percent = properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_percent();
					long secondsExpire = esitoNegoziazioneToken.getInformazioniNegoziazioneToken().getExpiresIn().getTime() / 1000l;
					long secondsIat = esitoNegoziazioneToken.getInformazioniNegoziazioneToken().getRetrievedIn().getTime() / 1000l;
					long secondsDiff = secondsExpire - secondsIat;
					if(secondsDiff>0) {
						float perc1 = Float.parseFloat(secondsDiff+"") / 100f;
						float perc2 = perc1 * Float.parseFloat(percent+"");
						int s = Math.round(perc2);
						if(s>0) {
							secondsPreExpire = s;
						}
					}
				}
				else if(properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_seconds()!=null && properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_seconds()>0) {
					secondsPreExpire = properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_seconds();
				}
				if(secondsPreExpire>0) {
					now = new Date(now.getTime() + (secondsPreExpire*1000));
					/**System.out.println("Controllo scadenza per rinegoziazione now+tollerance("+secondsPreExpire+")["+org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(now)+"] exp["+
					//		org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(esitoNegoziazioneToken.getInformazioniNegoziazioneToken().getExpiresIn())+"]");*/
				}
			}
			
			/*
			 *  The lifetime in seconds of the access token.  For example, the value "3600" denotes that the access token will
			 * expire in one hour from the time the response was generated.
			 * If omitted, the authorization server SHOULD provide the expiration time via other means or document the default value. 
			 **/
			if(!now.before(esitoNegoziazioneToken.getInformazioniNegoziazioneToken().getExpiresIn())){
				esitoNegoziazioneToken.setTokenScaduto();
				esitoNegoziazioneToken.setDateValide(false);
				esitoNegoziazioneToken.setDetails("AccessToken expired");	
			}
			
		}
			
		if(!esitoNegoziazioneToken.isValido()) {
			esitoNegoziazioneToken.setNoCache(!saveErrorInCache);
		}
	}
	
	static Map<String, Object> buildDynamicNegoziazioneTokenMap(Busta busta, 
			RequestInfo requestInfo, PdDContext pddContext, Logger log) {
	
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
				
		ErrorHandler errorHandler = new ErrorHandler();
		DynamicUtils.fillDynamicMapRequest(log, dynamicMap, pddContext, urlInvocazione,
				null,
				null, 
				busta, 
				pTrasporto, 
				pQuery,
				pForm,
				errorHandler);
		
		return dynamicMap;
	}
	
	private static HttpResponse http(boolean debug, Logger log, PolicyNegoziazioneToken policyNegoziazioneToken,
			Busta busta, RequestInfo requestInfo, TipoPdD tipoPdD,
			NegoziazioneTokenDynamicParameters dynamicParameters, IProtocolFactory<?> protocolFactory,
			IState state, boolean delegata, String idModulo, PortaApplicativa pa, PortaDelegata pd,
			IDSoggetto idDominio, IDServizio idServizio,
			boolean refreshModeEnabled, String refreshToken,
			InformazioniNegoziazioneToken_DatiRichiesta datiRichiesta) throws TokenException, ProviderException, ProviderValidationException, UtilsException, SecurityException {
		
		
		// *** Raccola Parametri ***
		
		String endpoint = dynamicParameters.getEndpoint();
		if(datiRichiesta!=null) {
			datiRichiesta.setEndpoint(endpoint);
		}
		
		HttpRequestMethod httpMethod = dynamicParameters.getHttpMethod();
		
		// Nell'endpoint config ci finisce i timeout e la configurazione proxy
		Properties endpointConfig = policyNegoziazioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_CONFIG);
		
		boolean https = policyNegoziazioneToken.isEndpointHttps();
		boolean httpsClient = false;
		Properties sslConfig = null;
		Properties sslClientConfig = null;
		if(https) {
			sslConfig = policyNegoziazioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CONFIG);
			httpsClient = policyNegoziazioneToken.isHttpsAuthentication();
			if(httpsClient) {
				sslClientConfig = policyNegoziazioneToken.getProperties().get(Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG);
			}
		}
		
		boolean basic = policyNegoziazioneToken.isBasicAuthentication();
		boolean basicAsAuthorizationHeader = policyNegoziazioneToken.isBasicAuthenticationAsAuthorizationHeader();
		String username = null;
		String password = null;
		if(basic) {
			username = dynamicParameters.getBasicUsername();
			password = dynamicParameters.getBasicPassword();
			if(datiRichiesta!=null) {
				datiRichiesta.setClientId(username);
			}
		}
		
		boolean bearer = policyNegoziazioneToken.isBearerAuthentication();
		String bearerToken = null;
		if(bearer) {
			bearerToken = dynamicParameters.getBearerToken();
			if(datiRichiesta!=null) {
				datiRichiesta.setClientToken(bearerToken);
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
		policyConfig.setPolicyNegoziazione(policyNegoziazioneToken.getName());
		connettoreMsg.setPolicyTimeoutConfig(policyConfig);
		
		ForwardProxy forwardProxy = null;
		ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
		if(configurazionePdDManager.isForwardProxyEnabled(requestInfo)) {
			try {
				IDGenericProperties policy = new IDGenericProperties();
				policy.setTipologia(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE);
				policy.setNome(policyNegoziazioneToken.getName());
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
		if(forwardProxy!=null && forwardProxy.isEnabled() && forwardProxy.getConfigToken()!=null && forwardProxy.getConfigToken().isTokenRetrieveEnabled()) {
			connettoreMsg.setForwardProxy(forwardProxy);
		}
		
		connettore.setForceDisable_proxyPassReverse(true);
		connettore.init(dynamicParameters.getPddContext(), protocolFactory);
		connettore.setRegisterSendIntoContext(false);
		
		if(basic && basicAsAuthorizationHeader){
			InvocazioneCredenziali credenziali = new InvocazioneCredenziali();
			credenziali.setUser(username);
			credenziali.setPassword(password);
			connettoreMsg.setCredenziali(credenziali);
		}
		
		connettoreMsg.setConnectorProperties(new java.util.HashMap<>());
		connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_LOCATION, endpoint);
		OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
		Boolean debugTramiteProperties = properties.getGestioneRetrieveToken_debug();
		if(debugTramiteProperties!=null) {
			if(debugTramiteProperties) {
				connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_DEBUG, true+"");
			}
		}
		else if(debug) { // impostazione del connettore
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
		
		
		TransportRequestContext transportRequestContext = new TransportRequestContext(log);
		transportRequestContext.setRequestType(httpMethod.name());
		transportRequestContext.setHeaders(new HashMap<>());
		if(bearer) {
			String authorizationHeader = HttpConstants.AUTHORIZATION_PREFIX_BEARER+bearerToken;
			TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.AUTHORIZATION, authorizationHeader);
		}
		
		Map<String, List<String>> pContent = new HashMap<>();
		boolean custom = false;
		if(refreshModeEnabled) {
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE, ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE_REFRESH_TOKEN);
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REFRESH_TOKEN, refreshToken);
		}
		else if(policyNegoziazioneToken.isClientCredentialsGrant()) {
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE, ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE_CLIENT_CREDENTIALS_GRANT);
		}
		else if(policyNegoziazioneToken.isRfc7523x509Grant() || policyNegoziazioneToken.isRfc7523ClientSecretGrant()) {
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE, ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE_CLIENT_CREDENTIALS_GRANT);
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_CLIENT_ASSERTION_TYPE, ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_CLIENT_ASSERTION_TYPE_RFC_7523);
		}
		else if(policyNegoziazioneToken.isUsernamePasswordGrant()) {
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE, ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE_RESOURCE_OWNER_PASSWORD_CREDENTIALS_GRANT);
		}
		else if(policyNegoziazioneToken.isCustomGrant()) {
			custom = true;
		}
		else {
			throw new TokenException("Nessuna modalità definita");
		}
		
		transportRequestContext.removeHeader(HttpConstants.CONTENT_TYPE);
		String contentType = null;
		if(custom) {
			contentType = dynamicParameters.getHttpContentType();
			if(contentType!=null && StringUtils.isNotEmpty(contentType)) {
				TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.CONTENT_TYPE, contentType);
			}
		}
		else {
			contentType = HttpConstants.CONTENT_TYPE_X_WWW_FORM_URLENCODED;
			TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.CONTENT_TYPE, contentType);
		}
		
		if(!custom && basic && !basicAsAuthorizationHeader){
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_CLIENT_ID, username);
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_CLIENT_SECRET, password);
		}
		
		if(policyNegoziazioneToken.isUsernamePasswordGrant()) {
			String usernamePasswordGrantUsername = dynamicParameters.getUsernamePasswordGrantUsername();
			String usernamePasswordGrantPassword = dynamicParameters.getUsernamePasswordGrantPassword();
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_USERNAME, usernamePasswordGrantUsername);
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_PASSWORD, usernamePasswordGrantPassword);
			if(datiRichiesta!=null) {
				datiRichiesta.setUsername(usernamePasswordGrantUsername);
			}
		}
		
		if(policyNegoziazioneToken.isRfc7523x509Grant() || policyNegoziazioneToken.isRfc7523ClientSecretGrant()) {
			String jwt = buildJwt(policyNegoziazioneToken,
					busta, tipoPdD,
					dynamicParameters,
					protocolFactory, requestInfo);
			String signedJwt = signJwt(policyNegoziazioneToken, jwt, contentType, dynamicParameters, requestInfo);
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_CLIENT_ASSERTION, signedJwt);
			if(datiRichiesta!=null) {
				boolean infoNormalizzate = properties.isGestioneRetrieveToken_grantType_rfc7523_saveClientAssertionJWTInfo_transazioniRegistrazioneInformazioniNormalizzate();
				datiRichiesta.setJwtClientAssertion(new InformazioniJWTClientAssertion(log, signedJwt, infoNormalizzate));
			}
		}
		
		if(!custom) {
			List<String> scopes = policyNegoziazioneToken.getScopes(dynamicParameters);
			if(scopes!=null && !scopes.isEmpty()) {
				StringBuilder bf = new StringBuilder();
				for (String scope : scopes) {
					if(bf.length()>0) {
						bf.append(" ");
					}
					bf.append(scope);
				}
				if(bf.length()>0) {
					TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_SCOPE, bf.toString());
					if(datiRichiesta!=null) {
						datiRichiesta.setScope(scopes);
					}
				}
			}
			
			String aud = dynamicParameters.getAudience();
			if(aud!=null && !"".equals(aud)) {
				TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_AUDIENCE, aud);
				if(datiRichiesta!=null) {
					datiRichiesta.setAudience(aud);
				}
			}
		}
		
		if(policyNegoziazioneToken.isPDND()) {
			String formClientId = dynamicParameters.getFormClientId();
			if(formClientId!=null && !"".equals(formClientId) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(formClientId)) {
				TransportUtils.setParameter(pContent,Costanti.PDND_OAUTH2_RFC_6749_REQUEST_CLIENT_ID, formClientId);
				if(datiRichiesta!=null && datiRichiesta.getClientId()==null) { // dovrebbe essere null poiche' il tipo PDND non e' abilitabile per il tipo clientId/Secret
					datiRichiesta.setClientId(formClientId);
				}
			}
			
			String formResource = dynamicParameters.getFormResource();
			if(formResource!=null && !"".equals(formResource) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(formResource)) {
				TransportUtils.setParameter(pContent,Costanti.PDND_OAUTH2_RFC_6749_REQUEST_RESOURCE, formResource);
			}
		}
		
		String parameters = dynamicParameters.getParameters();
		if(!custom && parameters!=null && !"".equals(parameters)) {
			Properties convertTextToProperties = PropertiesUtilities.convertTextToProperties(parameters);
			if(convertTextToProperties!=null && !convertTextToProperties.isEmpty()) {
				Map<String, String> mapParameters = new HashMap<>();
				Enumeration<Object> keys = convertTextToProperties.keys();
				while (keys.hasMoreElements()) {
					String nome = (String) keys.nextElement();
					if(nome!=null && !"".equals(nome)) {
						String valore = convertTextToProperties.getProperty(nome);
						if(valore!=null) {
							TransportUtils.setParameter(pContent, nome, valore);
							mapParameters.put(nome, valore);
						}
					}
				}
				if(datiRichiesta!=null && !mapParameters.isEmpty()) {
					datiRichiesta.setParameters(mapParameters);
				}
			}
		}
		
		String httpHeaders = dynamicParameters.getHttpHeaders();
		if(httpHeaders!=null && !"".equals(httpHeaders)) {
			Properties convertTextToProperties = PropertiesUtilities.convertTextToProperties(httpHeaders);
			if(convertTextToProperties!=null && !convertTextToProperties.isEmpty()) {
				Map<String, String> mapHttpHeaders = new HashMap<>();
				Enumeration<Object> keys = convertTextToProperties.keys();
				while (keys.hasMoreElements()) {
					String nome = (String) keys.nextElement();
					if(nome!=null && !"".equals(nome)) {
						String valore = convertTextToProperties.getProperty(nome);
						if(valore!=null) {
							TransportUtils.setHeader(transportRequestContext.getHeaders(),nome, valore);
							mapHttpHeaders.put(nome, valore);
						}
					}
				}
				if(datiRichiesta!=null && !mapHttpHeaders.isEmpty()) {
					datiRichiesta.setHttpHeaders(mapHttpHeaders);
				}
			}
		}

		String prefixUrl = "PREFIX?";
		byte [] content = null;
		if(custom) {
			String contentString = dynamicParameters.getHttpPayload();
			if(contentString!=null && StringUtils.isNotEmpty(contentString)) {
				content = contentString.getBytes();
				connettoreMsg.forceSendContent();
			}
		}
		else {
			String contentString = TransportUtils.buildUrlWithParameters(pContent, prefixUrl , log);
			contentString = contentString.substring(prefixUrl.length());
			if(contentString.startsWith("&") && contentString.length()>1) {
				contentString = contentString.substring(1);
			}
			content = contentString.getBytes();
		}
			
		try {
			OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(MessageType.BINARY, transportRequestContext, content);
			OpenSPCoop2Message msg = pr.getMessage_throwParseException();
			connettoreMsg.setRequestMessage(msg);
			connettoreMsg.setGenerateErrorWithConnectorPrefix(false);
						
			connettore.setHttpMethod(msg);
			connettore.setPa(pa);
			connettore.setPd(pd);
		}catch(Exception e) {
			throw new TokenException(e.getMessage(),e);
		}
		
		ResponseCachingConfigurazione responseCachingConfigurazione = new ResponseCachingConfigurazione();
		responseCachingConfigurazione.setStato(StatoFunzionalita.DISABILITATO);
		String prefixConnettore = "[EndpointNegoziazioneToken: "+endpoint+"] ";
		if(endpointConfig.containsKey(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME)) {
			String hostProxy = endpointConfig.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME);
			String portProxy = endpointConfig.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT);
			prefixConnettore = prefixConnettore+GestoreToken.getMessageViaProxy(hostProxy, portProxy);
		}
		if(datiRichiesta!=null && datiRichiesta.getPrepareRequest()!=null) {
			datiRichiesta.setSendRequest(DateManager.getDate());
		}
		boolean send = connettore.send(responseCachingConfigurazione, connettoreMsg);
		if(datiRichiesta!=null && datiRichiesta.getPrepareRequest()!=null) {
			datiRichiesta.setReceiveResponse(DateManager.getDate());
		}
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
			try {
				bout = new ByteArrayOutputStream();
				if(msgResponse!=null) {
					msgResponse.writeTo(bout, true);
				}
				bout.flush();
				bout.close();
			}catch(Exception e) {
				throw new TokenException(e.getMessage(),e);
			}
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
	
	private static String buildJwt(PolicyNegoziazioneToken policyNegoziazioneToken,
			Busta busta, TipoPdD tipoPdD,
			NegoziazioneTokenDynamicParameters dynamicParameters, 
			IProtocolFactory<?> protocolFactory, RequestInfo requestInfo) throws TokenException, UtilsException {
		
		// https://datatracker.ietf.org/doc/html/rfc7523
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		JSONUtils jsonUtils = JSONUtils.getInstance(false);
		
		ObjectNode jwtPayload = jsonUtils.newObjectNode();

		// add iat, nbf, exp
		long nowMs = DateManager.getTimeMillis();
		long nowSeconds = nowMs/1000;
		
		String issuer = dynamicParameters.getSignedJwtIssuer();
		if(issuer==null) {
			if(TipoPdD.APPLICATIVA.equals(tipoPdD) && busta!=null && busta.getDestinatario()!=null) {
				issuer = busta.getDestinatario();
			}
			else if(TipoPdD.DELEGATA.equals(tipoPdD) && busta!=null && busta.getMittente()!=null) {
				issuer = busta.getMittente();
			}
			else {
				issuer = op2Properties.getIdentitaPortaDefault(protocolFactory!=null ? protocolFactory.getProtocol() : null, requestInfo).getNome();
			}
		}
		if(!Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(issuer)) {
			jwtPayload.put(Claims.OIDC_ID_TOKEN_ISSUER, issuer);
		}
		
		// For client authentication, the subject MUST be the "client_id" of the OAuth client.
		String clientId = dynamicParameters.getSignedJwtClientId();
		if(clientId==null) {
			throw new TokenException("ClientID undefined");
		}
		if(!Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(clientId)) {
			jwtPayload.put(Claims.INTROSPECTION_RESPONSE_RFC_7662_CLIENT_ID, clientId);
		}
		
		String subject = dynamicParameters.getSignedJwtSubject();
		if(StringUtils.isNotEmpty(subject)) {
			if(!Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(subject)) {
				jwtPayload.put(Claims.OIDC_ID_TOKEN_SUBJECT, subject);
			}
		}
		else {
			if(!Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(clientId)) {
				jwtPayload.put(Claims.OIDC_ID_TOKEN_SUBJECT, clientId);
			}
		}
		
		// The JWT MUST contain an "aud" (audience) claim containing a value that identifies the authorization server as an intended audience. 
		String jwtAudience = dynamicParameters.getSignedJwtAudience();
		if(jwtAudience==null) {
			throw new TokenException("JWT-Audience undefined");
		}
		if(!Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(jwtAudience)) {
			jwtPayload.put(Claims.OIDC_ID_TOKEN_AUDIENCE, jwtAudience);
		}
		
		// The JWT MAY contain an "iat" (issued at) claim that identifies the time at which the JWT was issued. 
		jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT, nowSeconds);
		
		// The JWT MAY contain an "nbf" (not before) claim that identifies the time before which the token MUST NOT be accepted for processing.
		jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE, nowSeconds);
		
		// The JWT MUST contain an "exp" (expiration time) claim that limits the time window during which the JWT can be used.
		int ttl = -1;
		try {
			ttl = policyNegoziazioneToken.getJwtTtlSeconds();
		}catch(Exception e) {
			throw new TokenException("Invalid JWT-TimeToLive value: "+e.getMessage(),e);
		}
		long expired = nowSeconds+ttl;
		jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED, expired);
		
		// The JWT MAY contain a "jti" (JWT ID) claim that provides a unique identifier for the token.
		String jti = dynamicParameters.getSignedJwtJti();
		if(StringUtils.isNotEmpty(jti)) {
			if(!Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(jti)) {
				jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID, jti);
			}
		}
		else {
			// per default genero un uuid
			String uuid = null;
			try {
				uuid = UniqueIdentifierManager.newUniqueIdentifier().toString();
			}catch(Exception e) {
				throw new TokenException("Invalid JWT-TimeToLive value: "+e.getMessage(),e);
			}
			jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID, uuid);
		}
		
		if(policyNegoziazioneToken.isPDND()) {
			
			// purposeId
			String jwtPurposeId = dynamicParameters.getSignedJwtPurposeId();
			if(jwtPurposeId==null) {
				throw new TokenException("JWT-PurposeId undefined");
			}
			jwtPayload.put(Costanti.PDND_PURPOSE_ID, jwtPurposeId);
			
			// sessionInfo
			String sessionInfo = dynamicParameters.getSignedJwtSessionInfo();
			if(sessionInfo!=null && !"".equals(sessionInfo)) {
				Properties convertTextToProperties = PropertiesUtilities.convertTextToProperties(sessionInfo);
				ObjectNode sessionInfoPayload = null;
				if(convertTextToProperties!=null && !convertTextToProperties.isEmpty()) {
					Enumeration<Object> keys = convertTextToProperties.keys();
					while (keys.hasMoreElements()) {
						String nome = (String) keys.nextElement();
						if(nome!=null && !"".equals(nome)) {
							String valore = convertTextToProperties.getProperty(nome);
							if(valore!=null) {
								if(sessionInfoPayload ==null) {
									sessionInfoPayload = jsonUtils.newObjectNode();
								}
								jsonUtils.putValue(sessionInfoPayload, nome, valore);
							}
						}
					}
				}
				if(sessionInfoPayload!=null) {
					jwtPayload.set(Costanti.PDND_SESSION_INFO, sessionInfoPayload);
				}
			}
		}
		
		// digest Audit
		String digestAudit = dynamicParameters.getSignedJwtAuditDigest();
		if(digestAudit!=null && StringUtils.isNotEmpty(digestAudit)) {
			String algorithm = dynamicParameters.getSignedJwtAuditDigestAlgo();
			if(algorithm==null || StringUtils.isEmpty(algorithm)) {
				algorithm = Costanti.PDND_DIGEST_ALG_DEFAULT_VALUE;
			}
			
			ObjectNode digest = jsonUtils.newObjectNode();
			digest.put(Costanti.PDND_DIGEST_ALG, algorithm);
			digest.put(Costanti.PDND_DIGEST_VALUE, digestAudit);
			jwtPayload.set(Costanti.PDND_DIGEST, digest);
		}
		
		// claims
		String claims = dynamicParameters.getSignedJwtClaims();
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
		
		return jsonUtils.toString(jwtPayload);
	}
	

	
	private static String signJwt(PolicyNegoziazioneToken policyNegoziazioneToken, String payload, String contentType,
			NegoziazioneTokenDynamicParameters dynamicParameters,
			RequestInfo requestInfo) throws TokenException, SecurityException, UtilsException {
		
		String signAlgo = policyNegoziazioneToken.getJwtSignAlgorithm();
		if(signAlgo==null) {
			throw new TokenException("SignAlgorithm undefined");
		}
		
		KeyStore ks = null;
		KeyPairStore keyPairStore = null;
		JWKSetStore jwtStore = null;
		String keyAlias = null;
		String keyPassword = null;
		if(policyNegoziazioneToken.isRfc7523x509Grant()) {
			
			if(policyNegoziazioneToken.isJwtSignKeystoreApplicativoModI()) {
				
				String keystoreType = dynamicParameters.getTipoKeystoreApplicativoModI();
				if(keystoreType==null) {
					throw new TokenException(GestoreToken.KEYSTORE_TYPE_UNDEFINED);
				}
				if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType)) {
					keyPairStore = dynamicParameters.getKeyPairStoreApplicativoModI();
					if(keyPairStore==null) {
						throw new TokenException(GestoreToken.KEYSTORE_KEYPAIR_UNDEFINED);
					}
				}
				else if(SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreType)) {
					jwtStore =  dynamicParameters.getJWKSetStoreApplicativoModI();
					if(jwtStore==null) {
						throw new TokenException(GestoreToken.KEYSTORE_KEYSTORE_UNDEFINED);
					}
				}
				else {
					ks = dynamicParameters.getKeystoreApplicativoModI();
					if(ks==null) {
						throw new TokenException(GestoreToken.KEYSTORE_KEYSTORE_UNDEFINED);
					}
				}
				
				keyAlias = dynamicParameters.getKeyAliasApplicativoModI();
				if(keyAlias==null && 
						!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType) && 
						!SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
					throw new TokenException(GestoreToken.KEY_ALIAS_UNDEFINED);
				}
				keyPassword = dynamicParameters.getKeyPasswordApplicativoModI();
				if(keyPassword==null && 
						!SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreType) && 
						!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType) && 
						!SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
					throw new TokenException(GestoreToken.KEY_PASSWORD_UNDEFINED);
				}
			}
			else if(policyNegoziazioneToken.isJwtSignKeystoreFruizioneModI()) {
				
				String keystoreType = dynamicParameters.getTipoKeystoreFruizioneModI();
				if(keystoreType==null) {
					throw new TokenException(GestoreToken.KEYSTORE_TYPE_UNDEFINED);
				}
				if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType)) {
					keyPairStore = dynamicParameters.getKeyPairStoreFruizioneModI();
					if(keyPairStore==null) {
						throw new TokenException(GestoreToken.KEYSTORE_KEYPAIR_UNDEFINED);
					}
				}
				else if(SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreType)) {
					jwtStore =  dynamicParameters.getJWKSetStoreFruizioneModI();
					if(jwtStore==null) {
						throw new TokenException(GestoreToken.KEYSTORE_KEYSTORE_UNDEFINED);
					}
				}
				else {
					ks = dynamicParameters.getKeystoreFruizioneModI();
					if(ks==null) {
						throw new TokenException(GestoreToken.KEYSTORE_KEYSTORE_UNDEFINED);
					}
				}
				
				keyAlias = dynamicParameters.getKeyAliasFruizioneModI();
				if(keyAlias==null && 
						!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType) && 
						!SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
					throw new TokenException(GestoreToken.KEY_ALIAS_UNDEFINED);
				}
				keyPassword = dynamicParameters.getKeyPasswordFruizioneModI();
				if(keyPassword==null && 
						!SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreType) && 
						!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType) && 
						!SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
					throw new TokenException(GestoreToken.KEY_PASSWORD_UNDEFINED);
				}
			}
			else {
				String keystoreType = policyNegoziazioneToken.getJwtSignKeystoreType();
				if(keystoreType==null) {
					throw new TokenException(GestoreToken.KEYSTORE_TYPE_UNDEFINED);
				}
				
				String keystoreFile = null;
				String keystoreFilePublicKey = null;
				String keyPairAlgorithm = null;
				if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType)) {
					keystoreFile = policyNegoziazioneToken.getJwtSignKeystoreFile();
					if(keystoreFile==null) {
						throw new TokenException("JWT Signature private key file undefined");
					}
					keystoreFilePublicKey = policyNegoziazioneToken.getJwtSignKeystoreFilePublicKey();
					if(keystoreFilePublicKey==null) {
						throw new TokenException("JWT Signature public key file undefined");
					}
					keyPairAlgorithm = policyNegoziazioneToken.getJwtSignKeyPairAlgorithm();
					if(keyPairAlgorithm==null) {
						throw new TokenException("JWT Signature key pair algorithm undefined");
					}
				}
				else {
					keystoreFile = policyNegoziazioneToken.getJwtSignKeystoreFile();
					if(keystoreFile==null) {
						throw new TokenException("JWT Signature keystore file undefined");
					}
				}
				
				String keystorePassword = policyNegoziazioneToken.getJwtSignKeystorePassword();
				if(keystorePassword==null && 
						!SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreType) && 
						!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType) && 
						!SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
					throw new TokenException("JWT Signature keystore password undefined");
				}
				
				keyAlias = policyNegoziazioneToken.getJwtSignKeyAlias();
				if(keyAlias==null && 
						!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType) && 
						!SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
					throw new TokenException(GestoreToken.KEY_ALIAS_UNDEFINED);
				}
				
				keyPassword = policyNegoziazioneToken.getJwtSignKeyPassword();
				if(keyPassword==null && 
						!SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreType) && 
						!SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreType) && 
						!SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreType)) {
					throw new TokenException(GestoreToken.KEY_PASSWORD_UNDEFINED);
				}
				
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
			}
		}
		
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
		
		JwtHeaders jwtHeaders = new JwtHeaders();
		if(policyNegoziazioneToken.isJwtSignIncludeKeyIdWithKeyAlias()) {
			jwtHeaders.setKid(keyAlias);
		}
		else if(policyNegoziazioneToken.isJwtSignIncludeKeyIdWithClientId()) {
			String clientId = dynamicParameters.getSignedJwtClientId();
			jwtHeaders.setKid(clientId);
		}
		else if(policyNegoziazioneToken.isJwtSignIncludeKeyIdCustom()) {
			String customId = dynamicParameters.getSignedJwtCustomId();
			jwtHeaders.setKid(customId);
		}
		else if(policyNegoziazioneToken.isJwtSignIncludeKeyIdApplicativoModI()) {
			String clientId = dynamicParameters.getKidApplicativoModI();
			jwtHeaders.setKid(clientId);
		}
		else if(policyNegoziazioneToken.isJwtSignIncludeKeyIdFruizioneModI()) {
			String clientId = dynamicParameters.getKidFruizioneModI();
			jwtHeaders.setKid(clientId);
		}
		if(policyNegoziazioneToken.isJwtSignIncludeX509Cert()) {
			jwtHeaders.setAddX5C(true);
		}
		String url = dynamicParameters.getSignedJwtX509Url();
		if(url!=null && !"".equals(url)) {
			try {
				jwtHeaders.setX509Url(new URI(url));
			}catch(Exception e) {
				throw new TokenException(e.getMessage(),e);
			}
		}
		if(policyNegoziazioneToken.isJwtSignIncludeX509CertSha1()) {
			jwtHeaders.setX509IncludeCertSha1(true);
		}
		if(policyNegoziazioneToken.isJwtSignIncludeX509CertSha256()) {
			jwtHeaders.setX509IncludeCertSha256(true);
		}
		if(policyNegoziazioneToken.isJwtSignJoseContentType() &&
			contentType!=null && !"".equals(contentType)) {
			jwtHeaders.setContentType(contentType);
		}
		String type = policyNegoziazioneToken.getJwtSignJoseType();
		if(type!=null && !"".equals(type) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(type)) { // funzionalita' undefined undocumented
			jwtHeaders.setType(type);
		}
		if(ks!=null) {
			Certificate cert = ks.getCertificate(keyAlias);
			if(cert instanceof X509Certificate) {
				jwtHeaders.addX509cert((X509Certificate)cert);
			}
		}
		
		if(policyNegoziazioneToken.isRfc7523ClientSecretGrant()) {
		
			String clientSecret = policyNegoziazioneToken.getJwtClientSecret();
			if(clientSecret==null) {
				throw new TokenException("ClientSecret undefined");
			}
			
			JsonSignature jsonSignature = new JsonSignature(clientSecret, signAlgo, jwtHeaders, options);
			return jsonSignature.sign(payload);
		}
		else if(policyNegoziazioneToken.isRfc7523x509Grant()) {
						
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
		else {
			throw new TokenException("JWT Signed mode unknown");
		}
		
	}
}
