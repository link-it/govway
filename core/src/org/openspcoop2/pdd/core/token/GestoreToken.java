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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.InvocazioneCredenziali;
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
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
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
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.dynamic.ErrorHandler;
import org.openspcoop2.pdd.core.dynamic.MessageContent;
import org.openspcoop2.pdd.core.dynamic.Template;
import org.openspcoop2.pdd.core.token.attribute_authority.AttributeAuthorityDynamicParameters;
import org.openspcoop2.pdd.core.token.attribute_authority.AttributeAuthorityProvider;
import org.openspcoop2.pdd.core.token.attribute_authority.BasicRetrieveAttributeAuthorityResponseParser;
import org.openspcoop2.pdd.core.token.attribute_authority.EsitoRecuperoAttributi;
import org.openspcoop2.pdd.core.token.attribute_authority.IRetrieveAttributeAuthorityResponseParser;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.core.token.attribute_authority.PolicyAttributeAuthority;
import org.openspcoop2.pdd.core.token.attribute_authority.RequiredAttributes;
import org.openspcoop2.pdd.core.token.attribute_authority.pa.EsitoRecuperoAttributiPortaApplicativa;
import org.openspcoop2.pdd.core.token.attribute_authority.pd.EsitoRecuperoAttributiPortaDelegata;
import org.openspcoop2.pdd.core.token.pa.EsitoGestioneTokenPortaApplicativa;
import org.openspcoop2.pdd.core.token.pa.EsitoPresenzaTokenPortaApplicativa;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.pdd.core.token.parser.ClaimsNegoziazione;
import org.openspcoop2.pdd.core.token.parser.INegoziazioneTokenParser;
import org.openspcoop2.pdd.core.token.parser.ITokenParser;
import org.openspcoop2.pdd.core.token.pd.EsitoGestioneTokenPortaDelegata;
import org.openspcoop2.pdd.core.token.pd.EsitoPresenzaTokenPortaDelegata;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.SecurityTokenUtilities;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.RestMessageSecurityToken;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.security.keystore.JWKSetStore;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.message.jose.JOSEUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWEOptions;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JWTOptions;
import org.openspcoop2.utils.security.JsonDecrypt;
import org.openspcoop2.utils.security.JsonEncrypt;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.JsonVerifySignature;
import org.openspcoop2.utils.security.JwtHeaders;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**     
 * GestoreToken
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreToken {

	public static final boolean PORTA_DELEGATA = true;
	public static final boolean PORTA_APPLICATIVA = false;
	
	/** Chiave della cache per la gestione dei token  */
	private static final String TOKEN_CACHE_NAME = "token";
	/** Cache */
	private static Cache cacheToken = null;
//	private static final Boolean semaphoreJWT = true;
//	private static final Boolean semaphoreIntrospection = true;
//	private static final Boolean semaphoreUserInfo = true;
//	private static final Boolean semaphoreNegoziazione = true;
//	private static final Boolean semaphoreAttributeAuthority = true;
	private static final org.openspcoop2.utils.Semaphore lockJWT = new org.openspcoop2.utils.Semaphore("GestoreTokenValidazioneJWT");
	private static final org.openspcoop2.utils.Semaphore lockIntrospection = new org.openspcoop2.utils.Semaphore("GestoreTokenIntrospection");
	private static final org.openspcoop2.utils.Semaphore lockUserInfo = new org.openspcoop2.utils.Semaphore("GestoreTokenUserInfo");
	private static final org.openspcoop2.utils.Semaphore lockNegoziazione = new org.openspcoop2.utils.Semaphore("GestoreTokenNegoziazione");
	private static final org.openspcoop2.utils.Semaphore lockAttributeAuthority = new org.openspcoop2.utils.Semaphore("GestoreTokenAttributeAuthority");
	/** Logger log */
	private static Logger logger = null;
	private static Logger logConsole = OpenSPCoop2Logger.getLoggerOpenSPCoopConsole();
	
	/* --------------- Cache --------------------*/
	public static void resetCache() throws TokenException{
		if(GestoreToken.cacheToken!=null){
			try{
				GestoreToken.cacheToken.clear();
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	public static String printStatsCache(String separator) throws TokenException{
		try{
			if(GestoreToken.cacheToken!=null){
				return GestoreToken.cacheToken.printStats(separator);
			}
			else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new TokenException("Visualizzazione Statistiche riguardante la cache sulla gestione dei token non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws TokenException{
		if(GestoreToken.cacheToken!=null)
			throw new TokenException("Cache gia' abilitata");
		else{
			try{
				GestoreToken.cacheToken = new Cache(GestoreToken.TOKEN_CACHE_NAME);
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	public static void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond) throws TokenException{
		if(GestoreToken.cacheToken!=null)
			throw new TokenException("Cache gia' abilitata");
		else{
			try{
				int dimensioneCacheInt = -1;
				if(dimensioneCache!=null){
					dimensioneCacheInt = dimensioneCache.intValue();
				}
				
				String algoritmoCache = null;
				if(algoritmoCacheLRU!=null){
					if(algoritmoCacheLRU)
						 algoritmoCache = CostantiConfigurazione.CACHE_LRU.toString();
					else
						 algoritmoCache = CostantiConfigurazione.CACHE_MRU.toString();
				}else{
					algoritmoCache = CostantiConfigurazione.CACHE_LRU.toString();
				}
				
				long itemIdleTimeLong = -1;
				if(itemIdleTime!=null){
					itemIdleTimeLong = itemIdleTime;
				}
				
				long itemLifeSecondLong = -1;
				if(itemLifeSecond!=null){
					itemLifeSecondLong = itemLifeSecond;
				}
				
				GestoreToken.initCacheToken(dimensioneCacheInt, algoritmoCache, itemIdleTimeLong, itemLifeSecondLong, null);
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	public static void disabilitaCache() throws TokenException{
		if(GestoreToken.cacheToken==null)
			throw new TokenException("Cache gia' disabilitata");
		else{
			try{
				GestoreToken.cacheToken.clear();
				GestoreToken.cacheToken = null;
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	public static boolean isCacheAbilitata(){
		return GestoreToken.cacheToken != null;
	}
	public static String listKeysCache(String separator) throws TokenException{
		if(GestoreToken.cacheToken!=null){
			try{
				return GestoreToken.cacheToken.printKeys(separator);
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException("Cache non abilitata");
		}
	}
	public static List<String> keysCache() throws TokenException{
		if(GestoreToken.cacheToken!=null){
			try{
				return GestoreToken.cacheToken.keys();
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException("Cache non abilitata");
		}
	}
	public static String getObjectCache(String key) throws TokenException{
		if(GestoreToken.cacheToken!=null){
			try{
				Object o = GestoreToken.cacheToken.get(key);
				if(o!=null){
					return o.toString();
				}else{
					return "oggetto con chiave ["+key+"] non presente";
				}
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException("Cache non abilitata");
		}
	}
	public static void removeObjectCache(String key) throws TokenException{
		if(GestoreToken.cacheToken!=null){
			try{
				GestoreToken.cacheToken.remove(key);
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException("Cache non abilitata");
		}
	}
	
	
	
	
	
	/*----------------- CLEANER --------------------*/
	
	public static void removeGenericProperties(IDGenericProperties idGP) throws Exception {
		
		if(GestoreToken.isCacheAbilitata()){
			
			String prefixKeyValidazioneJwt = null;
			String prefixKeyIntrospection = null;
			String prefixKeyUserInfo = null;
			
			String prefixKeyRetrieveToken = null;
			
			String prefixKeyAA = null;
			
			boolean checkKeys = false;
			
			if(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION.equals(idGP.getTipologia())) {
				
				prefixKeyValidazioneJwt = buildPrefixCacheKeyValidazione(idGP.getNome(), VALIDAZIONE_JWT_FUNCTION);
				prefixKeyIntrospection = buildPrefixCacheKeyValidazione(idGP.getNome(), INTROSPECTION_FUNCTION);
				prefixKeyUserInfo = buildPrefixCacheKeyValidazione(idGP.getNome(), USERINFO_FUNCTION);
				
				checkKeys = true;
			}
			else if(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE.equals(idGP.getTipologia())) {
				
				prefixKeyRetrieveToken = buildPrefixCacheKeyNegoziazione(idGP.getNome(), RETRIEVE_FUNCTION);
				
				checkKeys = true;
				
			}
			else if(CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY.equals(idGP.getTipologia())) {
				
				prefixKeyAA = buildCacheKeyRecuperoAttributiPrefix(idGP.getNome(), ATTRIBUTE_AUTHORITY_FUNCTION);
				
				checkKeys = true;
			}
			
			if(checkKeys) {
				List<String> keyForClean = new ArrayList<String>();
				List<String> keys = GestoreToken.keysCache();
				if(keys!=null && !keys.isEmpty()) {
					for (String key : keys) {
						if(key!=null) {
							if(prefixKeyValidazioneJwt!=null && key.startsWith(prefixKeyValidazioneJwt)) {
								keyForClean.add(key);
							}
							else if(prefixKeyIntrospection!=null && key.startsWith(prefixKeyIntrospection)) {
								keyForClean.add(key);
							}
							else if(prefixKeyUserInfo!=null && key.startsWith(prefixKeyUserInfo)) {
								keyForClean.add(key);
							}
							else if(prefixKeyRetrieveToken!=null && key.startsWith(prefixKeyRetrieveToken)) {
								keyForClean.add(key);
							}
							else if(prefixKeyAA!=null && key.startsWith(prefixKeyAA)) {
								keyForClean.add(key);
							}
						}
					}
				}
				if(keyForClean!=null && !keyForClean.isEmpty()) {
					for (String key : keyForClean) {
						removeObjectCache(key);
					}
				}
			}
				
		}
		
	}
	
	
	
	
	


	/*----------------- INIZIALIZZAZIONE --------------------*/
	public static void initialize(Logger log) throws Exception{
		GestoreToken.initialize(false, -1,null,-1l,-1l, log);
	}
	public static void initialize(int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{
		GestoreToken.initialize(true, dimensioneCache,algoritmoCache,idleTime,itemLifeSecond, log);
	}

	private static void initialize(boolean cacheAbilitata,int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{

		// Inizializzo log
		GestoreToken.logger = log;
				
		// Inizializzazione Cache
		if(cacheAbilitata){
			GestoreToken.initCacheToken(dimensioneCache, algoritmoCache, idleTime, itemLifeSecond, log);
		}

	}


	public static void initCacheToken(int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception {
		
		if(log!=null)
			log.info("Inizializzazione cache Token");

		GestoreToken.cacheToken = new Cache(GestoreToken.TOKEN_CACHE_NAME);

		if( (dimensioneCache>0) ||
				(algoritmoCache != null) ){

			if( dimensioneCache>0 ){
				try{
					String msg = "Dimensione della cache (Token) impostata al valore: "+dimensioneCache;
					if(log!=null)
						log.info(msg);
					GestoreToken.logConsole.info(msg);
					GestoreToken.cacheToken.setCacheSize(dimensioneCache);
				}catch(Exception error){
					throw new TokenException("Parametro errato per la dimensione della cache (Gestore Messaggi): "+error.getMessage(),error);
				}
			}
			if(algoritmoCache != null ){
				String msg = "Algoritmo di cache (Token) impostato al valore: "+algoritmoCache;
				if(log!=null)
					log.info(msg);
				GestoreToken.logConsole.info(msg);
				if(CostantiConfigurazione.CACHE_MRU.toString().equalsIgnoreCase(algoritmoCache))
					GestoreToken.cacheToken.setCacheAlgoritm(CacheAlgorithm.MRU);
				else
					GestoreToken.cacheToken.setCacheAlgoritm(CacheAlgorithm.LRU);
			}

		}

		if( idleTime > 0  ){
			try{
				String msg = "Attributo 'IdleTime' (Token) impostato al valore: "+idleTime;
				if(log!=null)
					log.info(msg);
				GestoreToken.logConsole.info(msg);
				GestoreToken.cacheToken.setItemIdleTime(idleTime);
			}catch(Exception error){
				throw new TokenException("Parametro errato per l'attributo 'IdleTime' (Gestore Messaggi): "+error.getMessage(),error);
			}
		}
		try{
			String msg = "Attributo 'MaxLifeSecond' (Token) impostato al valore: "+itemLifeSecond;
			if(log!=null)
				log.info(msg);
			GestoreToken.logConsole.info(msg);
			GestoreToken.cacheToken.setItemLifeTime(itemLifeSecond);
		}catch(Exception error){
			throw new TokenException("Parametro errato per l'attributo 'MaxLifeSecond' (Gestore Messaggi): "+error.getMessage(),error);
		}

	}
	
	

	public static void disableSyncronizedGet() throws UtilsException {
		if(GestoreToken.cacheToken==null) {
			throw new UtilsException("Cache disabled");
		}
		GestoreToken.cacheToken.disableSyncronizedGet();
	}
	public static boolean isDisableSyncronizedGet() throws UtilsException {
		if(GestoreToken.cacheToken==null) {
			throw new UtilsException("Cache disabled");
		}
		return GestoreToken.cacheToken.isDisableSyncronizedGet();
	}
	
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] VALIDAZIONE CONFIGURAZIONE ****************** */
	
	public static void validazioneConfigurazione(AbstractDatiInvocazione datiInvocazione) throws ProviderException, ProviderValidationException {
		TokenProvider p = new TokenProvider();
		p.validate(datiInvocazione.getPolicyGestioneToken().getProperties());
	}
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] VERIFICA POSIZIONE TOKEN ****************** */
	
	public static EsitoPresenzaToken verificaPosizioneToken(Logger log, AbstractDatiInvocazione datiInvocazione, boolean portaDelegata) {
		
		EsitoPresenzaToken esitoPresenzaToken = null;
		if(portaDelegata) {
			esitoPresenzaToken = new EsitoPresenzaTokenPortaDelegata();
		}
		else {
			esitoPresenzaToken = new EsitoPresenzaTokenPortaApplicativa();
		}
		
		esitoPresenzaToken.setPresente(false);
		try{
			PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
    		String source = policyGestioneToken.getTokenSource();
    		

			String token = null;

    		String detailsErrorHeader = null;
    		if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source) ||
    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_HEADER.equals(source) ||
    				Costanti.POLICY_TOKEN_SOURCE_CUSTOM_HEADER.equals(source)) {
    			if(datiInvocazione.getInfoConnettoreIngresso()==null || 
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext()==null) {
    				detailsErrorHeader = "Informazioni di trasporto non presenti";
    			}
    			else {
    				URLProtocolContext urlProtocolContext = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext();
    				if(urlProtocolContext.getHeaders()==null || urlProtocolContext.getHeaders().size()<=0) {
    					detailsErrorHeader = "Header di trasporto non presenti";
        			}
    				if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source) ||
    	    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_HEADER.equals(source)) {
    					if(urlProtocolContext.getCredential()==null || urlProtocolContext.getCredential().getBearerToken()==null) {
    						if(urlProtocolContext.getCredential()!=null && urlProtocolContext.getCredential().getUsername()!=null) {
    							detailsErrorHeader = "Riscontrato header http '"+HttpConstants.AUTHORIZATION+"' valorizzato tramite autenticazione '"+HttpConstants.AUTHORIZATION_PREFIX_BASIC+
    									"'; la configurazione richiede invece la presenza di un token valorizzato tramite autenticazione '"+HttpConstants.AUTHORIZATION_PREFIX_BEARER+"' ";
    						}
    						else {
    							detailsErrorHeader = "Non è stato riscontrato un header http '"+HttpConstants.AUTHORIZATION+"' valorizzato tramite autenticazione '"+HttpConstants.AUTHORIZATION_PREFIX_BEARER+"' e contenente un token";
    						}
    					}
    					else {
    						token = urlProtocolContext.getCredential().getBearerToken();
    						esitoPresenzaToken.setHeaderHttp(HttpConstants.AUTHORIZATION);
    					}
    				}
    				else {
    					String headerName = policyGestioneToken.getTokenSourceHeaderName();
    					List<String> values =  urlProtocolContext.getHeaderValues(headerName);
    					if(values!=null && !values.isEmpty()) {
    						token = values.get(0);
    					}
    					if(token==null) {
    						detailsErrorHeader = "Non è stato riscontrato l'header http '"+headerName+"' contenente il token";
    					}
    					else if(values.size()>1) {
    						detailsErrorHeader = "Sono stati rilevati più di un header http '"+headerName+"'";
    					}
    					else {
    						esitoPresenzaToken.setHeaderHttp(headerName);
    					}
    				}
    			}
    		}
    		
    		String detailsErrorUrl = null;
    		if( (token==null && Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source)) ||
    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_URL.equals(source) ||
    				Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL.equals(source)) {
    			if(datiInvocazione.getInfoConnettoreIngresso()==null || 
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext()==null) {
    				detailsErrorUrl = "Informazioni di trasporto non presenti";
    			}
    			else {
    				URLProtocolContext urlProtocolContext = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext();
    				if(urlProtocolContext.getParameters()==null || urlProtocolContext.getParameters().size()<=0) {
    					detailsErrorUrl = "Parametri nella URL non presenti";
        			}
    				String propertyUrlName = null;
    				if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source) ||
    	    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_URL.equals(source)) {
    					propertyUrlName = Costanti.RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN;
    				}
    				else {
    					propertyUrlName = policyGestioneToken.getTokenSourceUrlPropertyName();
    				}
    				List<String> values = urlProtocolContext.getParameterValues(propertyUrlName);
					if(values!=null && !values.isEmpty()) {
						token = values.get(0);
					}
					if(token==null) {
						detailsErrorUrl = "Non è stato riscontrata la proprietà della URL '"+propertyUrlName+"' contenente il token";
					}
					else if(values.size()>1) {
						detailsErrorHeader = "Sono state rilevate più proprietà della URL '"+propertyUrlName+"'";
					}
					else {
						esitoPresenzaToken.setPropertyUrl(propertyUrlName);
					}
    			}
    		}
    		
    		String detailsErrorForm = null;
    		if( (token==null && Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source)) ||
    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_FORM.equals(source)) {
    			if(datiInvocazione.getInfoConnettoreIngresso()==null || 
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext()==null ||
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext().getHttpServletRequest()==null) {
    				detailsErrorForm = "Informazioni di trasporto non presenti";
    			}
    			else {
    				HttpServletRequest httpServletRequest = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext().getHttpServletRequest();
    				if(httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
    					FormUrlEncodedHttpServletRequest form = (FormUrlEncodedHttpServletRequest) httpServletRequest;
    					List<String> values = form.getFormUrlEncodedParameterValues(Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN);
    					if(values!=null && !values.isEmpty()) {
    						token = values.get(0);
    					}
    					if(token==null) {
    						detailsErrorForm = "Non è stato riscontrata la proprietà della Form '"+Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN+"' contenente il token";
    					}
    					else if(values.size()>1) {
    						detailsErrorHeader = "Sono state rilevate più proprietà della Form '"+Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN+"'";
    					}
    					else {
    						esitoPresenzaToken.setPropertyFormBased(Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN);
    					}
    				}
    				else if(FormUrlEncodedHttpServletRequest.isFormUrlEncodedRequest(httpServletRequest)) {
    					List<String> values = TransportUtils.getParameterValues(httpServletRequest, Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN);
    					if(values!=null && !values.isEmpty()) {
    						token = values.get(0);
    					}
    					if(token==null) {
    						detailsErrorForm = "Non è stato riscontrata la proprietà della Form '"+Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN+"' contenente il token";
    					}
    					else if(values.size()>1) {
    						detailsErrorHeader = "Sono state rilevate più proprietà della Form '"+Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN+"'";
    					}
    					else {
    						esitoPresenzaToken.setPropertyFormBased(Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN);
    					}
    				}
    				else {
    					detailsErrorForm = "Non è stato riscontrata la presenza di un contenuto 'Form-Encoded'";
    				}
	    		}
    		}
    		
    		String detailsError = null;
    		if(detailsErrorHeader!=null) {
    			if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source)) {
//    				if(detailsError!=null) {
//    					detailsError = detailsError+"; ";
//    				}
    				if(detailsErrorUrl!=null || detailsErrorForm!=null) {
    					detailsError = "\n";
    				}
    				else {
    					detailsError = "";
    				}
    				detailsError = detailsError + "(Authorization Request Header) " +detailsErrorHeader;
    			}
    			else {
    				detailsError = detailsErrorHeader;
    			}
    		}
    		if(detailsErrorUrl!=null) {
    			if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source)) {
    				if(detailsError!=null) {
    					detailsError = detailsError+"\n";
    				}
    				else {
    					if(detailsErrorForm!=null) {
    						detailsError = "\n";
    					}
    					else {
    						detailsError = "";
    					}
    				}
    				detailsError = detailsError + "(URI Query Parameter) " +detailsErrorUrl;
    			}
    			else {
    				detailsError = detailsErrorUrl;
    			}
    		}
    		if(detailsErrorForm!=null) {
    			if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source)) {
    				if(detailsError!=null) {
    					detailsError = detailsError+"\n";
    				}
    				else {
    					detailsError = "";
    				}
    				detailsError = detailsError + "(Form-Encoded Body Parameter) " +detailsErrorForm;
    			}
    			else {
    				detailsError = detailsErrorForm;
    			}
    		}
    		
    		
    		if(token!=null) {
				esitoPresenzaToken.setToken(token);
				esitoPresenzaToken.setPresente(true);
			}
    		else {
    			if(detailsError!=null) {
					esitoPresenzaToken.setDetails(detailsError);	
				}
				else {
					esitoPresenzaToken.setDetails("Token non individuato tramite la configurazione indicata");	
				}
    			if(policyGestioneToken.isMessageErrorGenerateEmptyMessage()) {
	    			esitoPresenzaToken.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.invalid_request, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoPresenzaToken.getDetails()));   			
    			}
    			else {
    				esitoPresenzaToken.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.invalid_request, policyGestioneToken.getRealm(), 
	    					policyGestioneToken.isMessageErrorGenerateGenericMessage(), esitoPresenzaToken.getDetails()));
    			}
    		}
    		
    	}catch(Exception e){
    		esitoPresenzaToken.setDetails(e.getMessage());
    		esitoPresenzaToken.setEccezioneProcessamento(e);
    	}
		
		return esitoPresenzaToken;
	}
	
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] VALIDAZIONE JWT TOKEN ****************** */
	
	public static final String VALIDAZIONE_JWT_FUNCTION = "ValidazioneJWT";
		
	public static EsitoGestioneToken validazioneJWTToken(Logger log, AbstractDatiInvocazione datiInvocazione, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			String token, boolean portaDelegata) throws Exception {
		
		EsitoGestioneToken esitoGestioneToken = null;
		
		if(GestoreToken.cacheToken==null){
			esitoGestioneToken = _validazioneJWTToken(log, datiInvocazione, token, portaDelegata, pddContext);
		}
    	else{
    		String funzione = VALIDAZIONE_JWT_FUNCTION;
    		String keyCache = buildCacheKeyValidazione(datiInvocazione.getPolicyGestioneToken().getName(), funzione, portaDelegata, token);

    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
    		
    		org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheToken.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					GestoreToken.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
					esitoGestioneToken = (EsitoGestioneToken) response.getObject();
					esitoGestioneToken.setInCache(true);
				}else if(response.getException()!=null){
					GestoreToken.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
					throw (Exception) response.getException();
				}else{
					GestoreToken.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
				}
			}
    		
			if(esitoGestioneToken==null) {
				String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
				//synchronized (GestoreToken.semaphoreJWT) {
				GestoreToken.lockJWT.acquire("validazioneJWTToken", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheToken.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreToken.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
							esitoGestioneToken = (EsitoGestioneToken) response.getObject();
							esitoGestioneToken.setInCache(true);
						}else if(response.getException()!=null){
							GestoreToken.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
							throw (Exception) response.getException();
						}else{
							GestoreToken.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
	
					if(esitoGestioneToken==null) {
						// Effettuo la query
						GestoreToken.logger.debug("oggetto con chiave ["+keyCache+"] (method:"+funzione+") eseguo operazione...");
						esitoGestioneToken = _validazioneJWTToken(log, datiInvocazione, token, portaDelegata, pddContext);
							
						// Aggiungo la risposta in cache (se esiste una cache)	
						// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
						// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
						// - impostare il noCache a true
						if(esitoGestioneToken!=null){
							esitoGestioneToken.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
							if(!esitoGestioneToken.isNoCache()){
								GestoreToken.logger.info("Aggiungo oggetto ["+keyCache+"] in cache");
								try{	
									org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
									responseCache.setObject(esitoGestioneToken);
									GestoreToken.cacheToken.put(keyCache,responseCache);
								}catch(UtilsException e){
									GestoreToken.logger.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
								}
							}
						}else{
							throw new TokenException("Metodo (GestoreToken."+funzione+") ha ritornato un valore di esito null");
						}
					}
				}finally {
					GestoreToken.lockJWT.release("validazioneJWTToken", idTransazione);
				}
			}
    	}
		
		if(esitoGestioneToken.isValido()) {
			// ricontrollo tutte le date
			_validazioneInformazioniToken(esitoGestioneToken, datiInvocazione.getPolicyGestioneToken(), 
					datiInvocazione.getPolicyGestioneToken().isValidazioneJWT_saveErrorInCache());
		}
		
		return esitoGestioneToken;
	}
	
	private static EsitoGestioneToken _validazioneJWTToken(Logger log, AbstractDatiInvocazione datiInvocazione, String token, boolean portaDelegata, PdDContext pddContext) {
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
			
			ITokenParser tokenParser = policyGestioneToken.getValidazioneJWT_TokenParser();
			
    		if(Costanti.POLICY_TOKEN_TYPE_JWS.equals(tokenType)) {
    			// JWS Compact   			
    			JsonVerifySignature jsonCompactVerify = null;
    			try {
    				JWTOptions options = new JWTOptions(JOSESerialization.COMPACT);
    				Properties p = policyGestioneToken.getProperties().get(Costanti.POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID);
    				JOSEUtils.injectKeystore(p, log); // serve per leggere il keystore dalla cache
    				jsonCompactVerify = new JsonVerifySignature(p, options);
    				if(jsonCompactVerify.verify(token)) {
    					informazioniToken = new InformazioniToken(SorgenteInformazioniToken.JWT,jsonCompactVerify.getDecodedPayload(),tokenParser);
    					if(jsonCompactVerify.getX509Certificate()!=null && pddContext!=null) {
    						SecurityToken securityToken = SecurityTokenUtilities.newSecurityToken(pddContext);
    						RestMessageSecurityToken restSecurityToken = new RestMessageSecurityToken();
    						restSecurityToken.setCertificate(new CertificateInfo(jsonCompactVerify.getX509Certificate(), "access_token"));
    						restSecurityToken.setToken(token);	
    						securityToken.setAccessToken(restSecurityToken);
    					}
    				}
    				else {
    					detailsError = "Token non valido";
    				}
    			}catch(Exception e) {
    				log.debug("Token non valido: "+e.getMessage(),e);
    				detailsError = "Token non valido: "+e.getMessage();
    				eProcess = e;
    			}
    		}
    		else {
    			// JWE Compact
    			JsonDecrypt jsonDecrypt = null;
    			try {
    				JWTOptions options = new JWTOptions(JOSESerialization.COMPACT);
    				Properties p = policyGestioneToken.getProperties().get(Costanti.POLICY_VALIDAZIONE_JWE_DECRYPT_PROP_REF_ID);
    				JOSEUtils.injectKeystore(p, log); // serve per leggere il keystore dalla cache
    				jsonDecrypt = new JsonDecrypt(p, options);
    				jsonDecrypt.decrypt(token);
    				informazioniToken = new InformazioniToken(SorgenteInformazioniToken.JWT,jsonDecrypt.getDecodedPayload(),tokenParser);
    			}catch(Exception e) {
    				log.debug("Token non valido: "+e.getMessage(),e);
    				detailsError = "Token non valido: "+e.getMessage();
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
    			if(policyGestioneToken.isValidazioneJWT_saveErrorInCache()) {
    				esitoGestioneToken.setNoCache(false);
    			}
    			else {
    				esitoGestioneToken.setNoCache(true);
    			}
    			esitoGestioneToken.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoGestioneToken.setDetails(detailsError);	
				}
				else {
					esitoGestioneToken.setDetails("Token non valido");	
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
	
	
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] INTROSPECTION TOKEN ****************** */
	
	public static final String INTROSPECTION_FUNCTION = "Introspection";
	
	public static EsitoGestioneToken introspectionToken(Logger log, AbstractDatiInvocazione datiInvocazione, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			String token, boolean portaDelegata,
			IDSoggetto idDominio, IDServizio idServizio) throws Exception {
		EsitoGestioneToken esitoGestioneToken = null;
		
		if(GestoreToken.cacheToken==null){
			esitoGestioneToken = _introspectionToken(log, datiInvocazione, 
					pddContext, protocolFactory,
					token, portaDelegata,
					idDominio, idServizio);
		}
    	else{
    		String funzione = INTROSPECTION_FUNCTION;
    		String keyCache = buildCacheKeyValidazione(datiInvocazione.getPolicyGestioneToken().getName(), funzione, portaDelegata, token);

    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
    		
    		org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheToken.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					GestoreToken.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
					esitoGestioneToken = (EsitoGestioneToken) response.getObject();
					esitoGestioneToken.setInCache(true);
				}else if(response.getException()!=null){
					GestoreToken.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
					throw (Exception) response.getException();
				}else{
					GestoreToken.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
				}
			}
    		
			if(esitoGestioneToken==null) {
				
				String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
				//synchronized (GestoreToken.semaphoreIntrospection) {
				GestoreToken.lockIntrospection.acquire("introspectionToken", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheToken.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreToken.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
							esitoGestioneToken = (EsitoGestioneToken) response.getObject();
							esitoGestioneToken.setInCache(true);
						}else if(response.getException()!=null){
							GestoreToken.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
							throw (Exception) response.getException();
						}else{
							GestoreToken.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
	
					if(esitoGestioneToken==null) {
						// Effettuo la query
						GestoreToken.logger.debug("oggetto con chiave ["+keyCache+"] (method:"+funzione+") eseguo operazione...");
						esitoGestioneToken = _introspectionToken(log, datiInvocazione, 
								pddContext, protocolFactory,
								token, portaDelegata,
								idDominio, idServizio);
							
						// Aggiungo la risposta in cache (se esiste una cache)	
						// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
						// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
						// - impostare il noCache a true
						if(esitoGestioneToken!=null){
							esitoGestioneToken.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
							if(!esitoGestioneToken.isNoCache()){
								GestoreToken.logger.info("Aggiungo oggetto ["+keyCache+"] in cache");
								try{	
									org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
									responseCache.setObject(esitoGestioneToken);
									GestoreToken.cacheToken.put(keyCache,responseCache);
								}catch(UtilsException e){
									GestoreToken.logger.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
								}
							}
						}else{
							throw new TokenException("Metodo (GestoreToken."+funzione+") ha ritornato un valore di esito null");
						}
					}
				}finally {
					GestoreToken.lockIntrospection.release("introspectionToken", idTransazione);
				}
			}
    	}
		
		if(esitoGestioneToken.isValido()) {
			// ricontrollo tutte le date
			_validazioneInformazioniToken(esitoGestioneToken, datiInvocazione.getPolicyGestioneToken(), 
					datiInvocazione.getPolicyGestioneToken().isIntrospection_saveErrorInCache());
		}
		
		return esitoGestioneToken;
	}
	
	private static EsitoGestioneToken _introspectionToken(Logger log, AbstractDatiInvocazione datiInvocazione, 
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
			
			ITokenParser tokenParser = policyGestioneToken.getIntrospection_TokenParser();
			
			HttpResponse httpResponse = null;
			Integer httpResponseCode = null;
			byte[] risposta = null;
			try {
				httpResponse = http(log, policyGestioneToken, INTROSPECTION, token,
						pddContext, protocolFactory,
						datiInvocazione.getState(), portaDelegata,
						idDominio, idServizio);
				risposta = httpResponse.getContent();
				httpResponseCode = httpResponse.getResultHTTPOperation();
			}catch(Exception e) {
				detailsError = "(Errore di Connessione) "+ e.getMessage();
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
    			if(policyGestioneToken.isIntrospection_saveErrorInCache()) {
    				esitoGestioneToken.setNoCache(false);
    			}
    			else {
    				esitoGestioneToken.setNoCache(true);
    			}
    			esitoGestioneToken.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoGestioneToken.setDetails(detailsError);	
				}
				else {
					esitoGestioneToken.setDetails("Token non valido");	
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
	
	public static final String USERINFO_FUNCTION = "UserInfo";
	
	public static EsitoGestioneToken userInfoToken(Logger log, AbstractDatiInvocazione datiInvocazione, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			String token, boolean portaDelegata,
			IDSoggetto idDominio, IDServizio idServizio) throws Exception {
		EsitoGestioneToken esitoGestioneToken = null;
		
		if(GestoreToken.cacheToken==null){
			esitoGestioneToken = _userInfoToken(log, datiInvocazione, 
					pddContext, protocolFactory,
					token, portaDelegata,
					idDominio, idServizio);
		}
    	else{
    		String funzione = USERINFO_FUNCTION;
    		String keyCache = buildCacheKeyValidazione(datiInvocazione.getPolicyGestioneToken().getName(), funzione, portaDelegata, token);

    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
    		
    		org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheToken.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					GestoreToken.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
					esitoGestioneToken = (EsitoGestioneToken) response.getObject();
					esitoGestioneToken.setInCache(true);
				}else if(response.getException()!=null){
					GestoreToken.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
					throw (Exception) response.getException();
				}else{
					GestoreToken.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
				}
			}
    		
			if(esitoGestioneToken==null) {
				String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
				//synchronized (GestoreToken.semaphoreUserInfo) {
				GestoreToken.lockUserInfo.acquire("userInfoToken", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheToken.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreToken.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
							esitoGestioneToken = (EsitoGestioneToken) response.getObject();
							esitoGestioneToken.setInCache(true);
						}else if(response.getException()!=null){
							GestoreToken.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
							throw (Exception) response.getException();
						}else{
							GestoreToken.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
	
					if(esitoGestioneToken==null) {
						// Effettuo la query
						GestoreToken.logger.debug("oggetto con chiave ["+keyCache+"] (method:"+funzione+") eseguo operazione...");
						esitoGestioneToken = _userInfoToken(log, datiInvocazione, 
								pddContext, protocolFactory,
								token, portaDelegata,
								idDominio, idServizio);
							
						// Aggiungo la risposta in cache (se esiste una cache)	
						// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
						// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
						// - impostare il noCache a true
						if(esitoGestioneToken!=null){
							esitoGestioneToken.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
							if(!esitoGestioneToken.isNoCache()){
								GestoreToken.logger.info("Aggiungo oggetto ["+keyCache+"] in cache");
								try{	
									org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
									responseCache.setObject(esitoGestioneToken);
									GestoreToken.cacheToken.put(keyCache,responseCache);
								}catch(UtilsException e){
									GestoreToken.logger.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
								}
							}
						}else{
							throw new TokenException("Metodo (GestoreToken."+funzione+") ha ritornato un valore di esito null");
						}
					}
				}finally {
					GestoreToken.lockUserInfo.release("userInfoToken", idTransazione);
				}
			}
    	}
		
		if(esitoGestioneToken.isValido()) {
			// ricontrollo tutte le date
			_validazioneInformazioniToken(esitoGestioneToken, datiInvocazione.getPolicyGestioneToken(), 
					datiInvocazione.getPolicyGestioneToken().isUserInfo_saveErrorInCache());
		}
		
		return esitoGestioneToken;
	}
	
	private static EsitoGestioneToken _userInfoToken(Logger log, AbstractDatiInvocazione datiInvocazione, 
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
						idDominio, idServizio);
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
    			if(policyGestioneToken.isUserInfo_saveErrorInCache()) {
    				esitoGestioneToken.setNoCache(false);
    			}
    			else {
    				esitoGestioneToken.setNoCache(true);
    			}
    			esitoGestioneToken.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoGestioneToken.setDetails(detailsError);	
				}
				else {
					esitoGestioneToken.setDetails("Token non valido");	
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
	
	public static void forwardToken(Logger log, String idTransazione, AbstractDatiInvocazione datiInvocazione, EsitoPresenzaToken esitoPresenzaToken, 
			EsitoGestioneToken esitoValidazioneJWT, EsitoGestioneToken esitoIntrospection, EsitoGestioneToken esitoUserInfo, 
			InformazioniToken informazioniTokenNormalizzate,
			boolean portaDelegata) throws Exception {
		
		PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
			
		TokenForward tokenForward = new TokenForward();
		String token = esitoPresenzaToken.getToken();
		
		boolean trasparente = false;
		String forwardTrasparenteMode = null;
		String forwardTrasparenteMode_header = null;
		String forwardTrasparenteMode_url = null;
		if(policyGestioneToken.isForwardToken()) {
			trasparente = policyGestioneToken.isForwardToken_trasparente();
			if(trasparente) {
				forwardTrasparenteMode = policyGestioneToken.getForwardToken_trasparenteMode();
				if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_AS_RECEIVED.equals(forwardTrasparenteMode)) {
					forwardTrasparenteMode_header = esitoPresenzaToken.getHeaderHttp();
					forwardTrasparenteMode_url = esitoPresenzaToken.getPropertyUrl();
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_HEADER.equals(forwardTrasparenteMode)) {
					forwardTrasparenteMode_header = HttpConstants.AUTHORIZATION;
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_URL.equals(forwardTrasparenteMode)) {
					forwardTrasparenteMode_url = Costanti.RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN;
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER.equals(forwardTrasparenteMode)) {
					forwardTrasparenteMode_header = policyGestioneToken.getForwardToken_trasparenteModeCustomHeader();
				} 
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL.equals(forwardTrasparenteMode)) {
					forwardTrasparenteMode_url = policyGestioneToken.getForwardToken_trasparenteModeCustomUrl();
				} 
			}
		}
		
		boolean infoRaccolte = false;
		String forwardInformazioniRaccolteMode = null;
		Properties jwtSecurity = null;
		boolean encodeBase64 = false;
		boolean forwardValidazioneJWT = false;	
		String forwardValidazioneJWT_mode = null;
		String forwardValidazioneJWT_name = null;
		boolean forwardIntrospection = false;	
		String forwardIntrospection_mode = null;
		String forwardIntrospection_name = null;
		boolean forwardUserInfo = false;	
		String forwardUserInfo_mode = null;
		String forwardUserInfo_name = null;
		if(policyGestioneToken.isForwardToken()) {
			infoRaccolte = policyGestioneToken.isForwardToken_informazioniRaccolte();
			if(infoRaccolte) {
				forwardInformazioniRaccolteMode = policyGestioneToken.getForwardToken_informazioniRaccolteMode();
				encodeBase64 = policyGestioneToken.isForwardToken_informazioniRaccolteEncodeBase64();
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS.equals(forwardInformazioniRaccolteMode) ||
						Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(forwardInformazioniRaccolteMode)) {
					jwtSecurity = policyGestioneToken.getProperties().get(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_SIGNATURE_PROP_REF_ID);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(forwardInformazioniRaccolteMode)) {
					jwtSecurity = policyGestioneToken.getProperties().get(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_ENCRYP_PROP_REF_ID);
				}
				
				if(jwtSecurity!=null) {
					JOSEUtils.injectKeystore(jwtSecurity, log); // serve per leggere il keystore dalla cache
				}
				
				forwardValidazioneJWT = policyGestioneToken.isForwardToken_informazioniRaccolte_validazioneJWT();
				if(forwardValidazioneJWT) {
					forwardValidazioneJWT_mode = policyGestioneToken.getForwardToken_informazioniRaccolte_validazioneJWT_mode();
					if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardValidazioneJWT_mode)) {
						forwardValidazioneJWT_name = policyGestioneToken.getForwardToken_informazioniRaccolte_validazioneJWT_mode_headerName();
					}
					else {
						forwardValidazioneJWT_name = policyGestioneToken.getForwardToken_informazioniRaccolte_validazioneJWT_mode_queryParameterName();
					}
				}
				
				forwardIntrospection = policyGestioneToken.isForwardToken_informazioniRaccolte_introspection();
				if(forwardIntrospection) {
					forwardIntrospection_mode = policyGestioneToken.getForwardToken_informazioniRaccolte_introspection_mode();
					if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardIntrospection_mode)) {
						forwardIntrospection_name = policyGestioneToken.getForwardToken_informazioniRaccolte_introspection_mode_headerName();
					}
					else {
						forwardIntrospection_name = policyGestioneToken.getForwardToken_informazioniRaccolte_introspection_mode_queryParameterName();
					}
				}
				
				forwardUserInfo = policyGestioneToken.isForwardToken_informazioniRaccolte_userInfo();
				if(forwardUserInfo) {
					forwardUserInfo_mode = policyGestioneToken.getForwardToken_informazioniRaccolte_userInfo_mode();
					if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardUserInfo_mode)) {
						forwardUserInfo_name = policyGestioneToken.getForwardToken_informazioniRaccolte_userInfo_mode_headerName();
					}
					else {
						forwardUserInfo_name = policyGestioneToken.getForwardToken_informazioniRaccolte_userInfo_mode_queryParameterName();
					}
				}
			}
		}
		
		// Elimino token ricevuto
		boolean delete = _deleteTokenReceived(datiInvocazione, esitoPresenzaToken, trasparente, forwardTrasparenteMode_header, forwardTrasparenteMode_url);
		
		if(trasparente) {
			
			// Forward trasparente
			
			if(delete) { 
				// la delete ha tenuto conto dell'opzione di forward prima di eliminare
				_forwardTokenTrasparente(token, esitoPresenzaToken, tokenForward, forwardTrasparenteMode, forwardTrasparenteMode_header, forwardTrasparenteMode_url);
			}
		}
		
		if(infoRaccolte) {
			
			// Forward informazioni raccolte
			
			_forwardInfomazioniRaccolte(portaDelegata, idTransazione, token, tokenForward, 
					esitoValidazioneJWT, esitoIntrospection, esitoUserInfo, 
					informazioniTokenNormalizzate,
					forwardInformazioniRaccolteMode, jwtSecurity, encodeBase64, 
					forwardValidazioneJWT, forwardValidazioneJWT_mode, forwardValidazioneJWT_name,
					forwardIntrospection, forwardIntrospection_mode, forwardIntrospection_name,
					forwardUserInfo, forwardUserInfo_mode, forwardUserInfo_name);
			
		}
		
		// Imposto token forward nel messaggio
		if(tokenForward.getUrl().size()>0 || tokenForward.getTrasporto().size()>0) {
			datiInvocazione.getMessage().addContextProperty(Costanti.MSG_CONTEXT_TOKEN_FORWARD, tokenForward);
		}

	}
	
	private static boolean _deleteTokenReceived(AbstractDatiInvocazione datiInvocazione, EsitoPresenzaToken esitoPresenzaToken,
			boolean trasparente, String forwardTrasparenteMode_header, String forwardTrasparenteMode_url) throws Exception {
		
		PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
		
		boolean remove = false;
		if(esitoPresenzaToken.getHeaderHttp()!=null) {
			if(!policyGestioneToken.isForwardToken()) {
				remove = true;
			}
			else if(!trasparente) {
				remove = true;
			}
			else if(!esitoPresenzaToken.getHeaderHttp().equalsIgnoreCase(forwardTrasparenteMode_header)) {
				remove = true;
			}
			if(remove) {
				datiInvocazione.getMessage().getTransportRequestContext().removeHeader(esitoPresenzaToken.getHeaderHttp());
			}
		}
		else if(esitoPresenzaToken.getPropertyUrl()!=null) {
			if(!policyGestioneToken.isForwardToken()) {
				remove = true;
			}
			else if(!trasparente) {
				remove = true;
			}
			else if(!esitoPresenzaToken.getPropertyUrl().equals(forwardTrasparenteMode_url)) {
				remove = true;
			}
			if(remove) {
				datiInvocazione.getMessage().getTransportRequestContext().removeParameter(esitoPresenzaToken.getPropertyUrl());
			}
		}
		else if(esitoPresenzaToken.getPropertyFormBased()!=null) {
			if(!policyGestioneToken.isForwardToken()) {
				remove = true;
			}
			else if(!trasparente) {
				remove = true;
			}
			if(remove) {
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
			}
		}
		return remove;
	}
	
	private static void _forwardTokenTrasparente(String token, EsitoPresenzaToken esitoPresenzaToken, TokenForward tokenForward,
			String forwardTrasparenteMode, String forwardTrasparenteMode_header, String forwardTrasparenteMode_url) throws Exception {
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
				throw new Exception("Configurazione non supportata"); // non dovrebbe mai entrare in questo ramo poichè il token non viene eliminato
			}
		}
		else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_HEADER.equals(forwardTrasparenteMode)) {
			TransportUtils.setHeader(tokenForward.getTrasporto(),HttpConstants.AUTHORIZATION, HttpConstants.AUTHORIZATION_PREFIX_BEARER+token);
		}
		else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_URL.equals(forwardTrasparenteMode)) {
			TransportUtils.setParameter(tokenForward.getUrl(),Costanti.RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN, token);
		}
		else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER.equals(forwardTrasparenteMode)) {
			TransportUtils.setHeader(tokenForward.getTrasporto(),forwardTrasparenteMode_header, token);
		}
		else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL.equals(forwardTrasparenteMode)) {
			TransportUtils.setParameter(tokenForward.getUrl(),forwardTrasparenteMode_url, token);
		}
	}
	
	private static void _forwardInfomazioniRaccolte(boolean portaDelegata, String idTransazione, String token, TokenForward tokenForward,
			EsitoGestioneToken esitoValidazioneJWT, EsitoGestioneToken esitoIntrospection, EsitoGestioneToken esitoUserInfo,
			InformazioniToken informazioniTokenNormalizzate,
			String forwardInforRaccolteMode, Properties jwtSecurity, boolean encodeBase64,
			boolean forwardValidazioneJWT, String forwardValidazioneJWT_mode, String forwardValidazioneJWT_name,
			boolean forwardIntrospection, String forwardIntrospection_mode, String forwardIntrospection_name,
			boolean forwardUserInfo, String forwardUserInfo_mode, String forwardUserInfo_name) throws Exception {
		
		if(informazioniTokenNormalizzate==null) {
			return; // può succedere nei casi warning only, significa che non vi sono token validi
		}
		
		if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_HEADERS.equals(forwardInforRaccolteMode) ||
				Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JSON.equals(forwardInforRaccolteMode) ||
				Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS.equals(forwardInforRaccolteMode)) {
			
			OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
			java.util.concurrent.ConcurrentHashMap<String,String> headerNames = null;
			java.util.concurrent.ConcurrentHashMap<String, Boolean> set = null;
			JSONUtils jsonUtils = null;
			ObjectNode jsonNode = null;
			boolean op2headers = Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_HEADERS.equals(forwardInforRaccolteMode);
			SimpleDateFormat sdf = null;
			if(op2headers) {
				headerNames = properties.getKeyValue_gestioneTokenHeaderIntegrazioneTrasporto();
				if(portaDelegata) {
					set = properties.getKeyPASetEnabled_gestioneTokenHeaderIntegrazioneTrasporto();
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
					set = properties.getKeyPASetEnabled_gestioneTokenHeaderIntegrazioneJson();
				}
				else {
					set = properties.getKeyPASetEnabled_gestioneTokenHeaderIntegrazioneJson();
				}
				jsonUtils = JSONUtils.getInstance();
				jsonNode = jsonUtils.newObjectNode();
				jsonNode.put("id", idTransazione);
			}
			
			if(informazioniTokenNormalizzate.getIss()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUER)) {
					if(op2headers) {
						TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUER), informazioniTokenNormalizzate.getIss());
					}
					else {
						jsonNode.put("issuer", informazioniTokenNormalizzate.getIss());
					}
				}
			}
			if(informazioniTokenNormalizzate.getSub()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SUBJECT)) {
					if(op2headers) {
						TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SUBJECT), informazioniTokenNormalizzate.getSub());
					}
					else {
						jsonNode.put("subject", informazioniTokenNormalizzate.getSub());
					}
				}
			}
			if(informazioniTokenNormalizzate.getUsername()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_USERNAME)) {
					if(op2headers) {
						TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_USERNAME), informazioniTokenNormalizzate.getUsername());
					}
					else {
						jsonNode.put("username", informazioniTokenNormalizzate.getUsername());
					}
				}
			}
			if(informazioniTokenNormalizzate.getAud()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_AUDIENCE)) {
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
			}
			if(informazioniTokenNormalizzate.getClientId()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_CLIENT_ID)) {
					if(op2headers) {
						TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_CLIENT_ID), informazioniTokenNormalizzate.getClientId());
					}
					else {
						jsonNode.put("clientId", informazioniTokenNormalizzate.getClientId());
					}
				}
			}
			if(informazioniTokenNormalizzate.getIat()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUED_AT)) {
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
			}
			if(informazioniTokenNormalizzate.getExp()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_EXPIRED)) {
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
			}
			if(informazioniTokenNormalizzate.getNbf()!=null) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_NBF)) {
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
			}
			if(informazioniTokenNormalizzate.getRoles()!=null && informazioniTokenNormalizzate.getRoles().size()>0) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ROLES)) {
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
			}
			if(informazioniTokenNormalizzate.getScopes()!=null && informazioniTokenNormalizzate.getScopes().size()>0) {
				if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SCOPES)) {
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
			}
			if(informazioniTokenNormalizzate.getUserInfo()!=null) {
				ObjectNode userInfoNode = null;
				if(!op2headers) {
					userInfoNode = jsonUtils.newObjectNode();
				}
				
				boolean add = false;
				
				if(informazioniTokenNormalizzate.getUserInfo().getFullName()!=null) {
					if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FULL_NAME)) {
						if(op2headers) {
							TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FULL_NAME), informazioniTokenNormalizzate.getUserInfo().getFullName());
						}
						else {
							userInfoNode.put("fullName", informazioniTokenNormalizzate.getUserInfo().getFullName());
							add = true;
						}
					}
				}
				if(informazioniTokenNormalizzate.getUserInfo().getFirstName()!=null) {
					if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FIRST_NAME)) {
						if(op2headers) {
							TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FIRST_NAME), informazioniTokenNormalizzate.getUserInfo().getFirstName());
						}
						else {
							userInfoNode.put("firstName", informazioniTokenNormalizzate.getUserInfo().getFirstName());
							add = true;
						}
					}
				}
				if(informazioniTokenNormalizzate.getUserInfo().getMiddleName()!=null) {
					if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_MIDDLE_NAME)) {
						if(op2headers) {
							TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_MIDDLE_NAME), informazioniTokenNormalizzate.getUserInfo().getMiddleName());
						}
						else {
							userInfoNode.put("middleName", informazioniTokenNormalizzate.getUserInfo().getMiddleName());
							add = true;
						}
					}
				}
				if(informazioniTokenNormalizzate.getUserInfo().getFamilyName()!=null) {
					if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FAMILY_NAME)) {
						if(op2headers) {
							TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FAMILY_NAME), informazioniTokenNormalizzate.getUserInfo().getFamilyName());
						}
						else {
							userInfoNode.put("familyName", informazioniTokenNormalizzate.getUserInfo().getFamilyName());
							add = true;
						}
					}
				}
				if(informazioniTokenNormalizzate.getUserInfo().getEMail()!=null) {
					if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_EMAIL)) {
						if(op2headers) {
							TransportUtils.setHeader(tokenForward.getTrasporto(),headerNames.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_EMAIL), informazioniTokenNormalizzate.getUserInfo().getEMail());
						}
						else {
							userInfoNode.put("eMail", informazioniTokenNormalizzate.getUserInfo().getEMail());
							add = true;
						}
					}
				}
				
				if(!op2headers && add) {
					jsonNode.set("userInfo", userInfoNode);
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
			if(set.get(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PROCESS_TIME)) {
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
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardValidazioneJWT_mode)) {
					TransportUtils.setHeader(tokenForward.getTrasporto(),forwardValidazioneJWT_name, value);
				}
				else {
					TransportUtils.setParameter(tokenForward.getUrl(),forwardValidazioneJWT_name, value);
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
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardIntrospection_mode)) {
					TransportUtils.setHeader(tokenForward.getTrasporto(),forwardIntrospection_name, value);
				}
				else {
					TransportUtils.setParameter(tokenForward.getUrl(),forwardIntrospection_name, value);
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
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardUserInfo_mode)) {
					TransportUtils.setHeader(tokenForward.getTrasporto(),forwardUserInfo_name, value);
				}
				else {
					TransportUtils.setParameter(tokenForward.getUrl(),forwardUserInfo_name, value);
				}
				
			}
			
		}
		
	}
	
	public static List<InformazioniToken> getInformazioniTokenValide(EsitoGestioneToken esitoValidazioneJWT, EsitoGestioneToken esitoIntrospection, EsitoGestioneToken esitoUserInfo){
		List<InformazioniToken> list = new ArrayList<>();
		if(esitoValidazioneJWT!=null && esitoValidazioneJWT.isValido() && esitoValidazioneJWT.getInformazioniToken()!=null) {
			list.add(esitoValidazioneJWT.getInformazioniToken());
		}
		if(esitoIntrospection!=null && esitoIntrospection.isValido() && esitoIntrospection.getInformazioniToken()!=null) {
			list.add(esitoIntrospection.getInformazioniToken());
		}
		if(esitoUserInfo!=null && esitoUserInfo.isValido() && esitoUserInfo.getInformazioniToken()!=null) {
			list.add(esitoUserInfo.getInformazioniToken());
		}
		return list;
	}
	
	public static List<InformazioniToken> getInformazioniTokenNonValide(EsitoGestioneToken esitoValidazioneJWT, EsitoGestioneToken esitoIntrospection, EsitoGestioneToken esitoUserInfo){
		
		// A differenza del metodo sopra on si controlla che il token sia valido.
		
		List<InformazioniToken> list = new ArrayList<>();
		if(esitoValidazioneJWT!=null && esitoValidazioneJWT.getInformazioniToken()!=null) {
			list.add(esitoValidazioneJWT.getInformazioniToken());
		}
		if(esitoIntrospection!=null && esitoIntrospection.getInformazioniToken()!=null) {
			list.add(esitoIntrospection.getInformazioniToken());
		}
		if(esitoUserInfo!=null && esitoUserInfo.getInformazioniToken()!=null) {
			list.add(esitoUserInfo.getInformazioniToken());
		}
		return list;
	}
	
	public static InformazioniToken normalizeInformazioniToken(List<InformazioniToken> list) throws Exception {
		if(list.size()==1) {
			return list.get(0);
		}
		else {
			return new InformazioniToken(OpenSPCoop2Properties.getInstance().isGestioneToken_saveSourceTokenInfo(), list.toArray(new InformazioniToken[1]));
		}
	}
	
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] UTILITIES INTERNE ****************** */
	
	private static final String format = "yyyy-MM-dd HH:mm:ss.SSS";
	
	private static void _validazioneInformazioniToken(EsitoGestioneToken esitoGestioneToken, PolicyGestioneToken policyGestioneToken, boolean saveErrorInCache) throws Exception {
		
		Date now = DateManager.getDate();
		
		if(esitoGestioneToken.isValido()) {			
			if(esitoGestioneToken.getInformazioniToken().getExp()!=null) {				
				/*
				 *   The "exp" (expiration time) claim identifies the expiration time on
   				 *   or after which the JWT MUST NOT be accepted for processing.  The
   				 *   processing of the "exp" claim requires that the current date/time
   				 *   MUST be before the expiration date/time listed in the "exp" claim.
				 **/
				if(!now.before(esitoGestioneToken.getInformazioniToken().getExp())){
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
			
		if(esitoGestioneToken.isValido()) {
			if(esitoGestioneToken.getInformazioniToken().getNbf()!=null) {				
				/*
				 *   The "nbf" (not before) claim identifies the time before which the JWT
				 *   MUST NOT be accepted for processing.  The processing of the "nbf"
				 *   claim requires that the current date/time MUST be after or equal to
				 *   the not-before date/time listed in the "nbf" claim. 
				 **/
				if(!esitoGestioneToken.getInformazioniToken().getNbf().before(now)){
					esitoGestioneToken.setTokenNotUsableBefore();
					esitoGestioneToken.setDateValide(false);
					SimpleDateFormat sdf = DateUtils.getDefaultDateTimeFormatter(format);
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
		}
		
		if(esitoGestioneToken.isValido()) {
			if(esitoGestioneToken.getInformazioniToken().getIat()!=null) {				
				/*
				 *   The "iat" (issued at) claim identifies the time at which the JWT was
   				 *   issued.  This claim can be used to determine the age of the JWT.
   				 *   The iat Claim can be used to reject tokens that were issued too far away from the current time, 
   				 *   limiting the amount of time that nonces need to be stored to prevent attacks. The acceptable range is Client specific. 
				 **/
				Integer old = OpenSPCoop2Properties.getInstance().getGestioneToken_iatTimeCheck_milliseconds();
				if(old!=null) {
					Date oldMax = new Date((DateManager.getTimeMillis() - old.intValue()));
					if(esitoGestioneToken.getInformazioniToken().getIat().before(oldMax)) {
						esitoGestioneToken.setTokenScaduto();
						esitoGestioneToken.setDateValide(false);
						SimpleDateFormat sdf = DateUtils.getDefaultDateTimeFormatter(format);
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
			}
			
		}
		
		if(esitoGestioneToken.isValido()==false) {
			if(saveErrorInCache) {
				esitoGestioneToken.setNoCache(false);
			}
			else {
				esitoGestioneToken.setNoCache(true);
			}
		}
	}
	
	private static String buildPrefixCacheKeyValidazione(String policy, String funzione) {
		StringBuilder bf = new StringBuilder(funzione);
    	bf.append("_");
    	bf.append(policy);
    	bf.append("_");
    	return bf.toString();
	}
	private static String buildCacheKeyValidazione(String policy, String funzione, boolean portaDelegata, String token) {
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
	
	private static final boolean INTROSPECTION = true;
	private static final boolean USER_INFO = false;
	private static HttpResponse http(Logger log, PolicyGestioneToken policyGestioneToken, boolean introspection, String token,
			PdDContext pddContext, IProtocolFactory<?> protocolFactory, 
			IState state, boolean delegata,
			IDSoggetto idDominio, IDServizio idServizio) throws Exception {
		
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
			prefixConnettore = prefixConnettore+" [via Proxy: "+hostProxy+":"+portProxy+"] ";
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
		if(configurazionePdDManager.isForwardProxyEnabled()) {
			try {
				IDGenericProperties policy = new IDGenericProperties();
				policy.setTipologia(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION);
				policy.setNome(policyGestioneToken.getName());
				if(delegata) {
					forwardProxy = configurazionePdDManager.getForwardProxyConfigFruizione(idDominio, idServizio, policy);
				}
				else {
					forwardProxy = configurazionePdDManager.getForwardProxyConfigErogazione(idDominio, idServizio, policy);
				}
			}catch(Exception e) {
				throw new Exception("Configurazione errata per la funzionalità govway-proxy; "+e.getMessage(),e);
			}
		}
		if(forwardProxy!=null && forwardProxy.isEnabled() && forwardProxy.getConfigToken()!=null) {
			if(introspection && forwardProxy.getConfigToken().isTokenIntrospectionEnabled()) {
				connettoreMsg.setForwardProxy(forwardProxy);
			}
			else if(!introspection && forwardProxy.getConfigToken().isTokenUserInfoEnabled()) {
				connettoreMsg.setForwardProxy(forwardProxy);
			}
		}
		
		connettore.setForceDisable_rest_proxyPassReverse(true);
		connettore.init(pddContext, protocolFactory);
		connettore.setRegisterSendIntoContext(false);
		
		if(basic){
			InvocazioneCredenziali credenziali = new InvocazioneCredenziali();
			credenziali.setUser(username);
			credenziali.setPassword(password);
			connettoreMsg.setCredenziali(credenziali);
		}
		
		connettoreMsg.setConnectorProperties(new java.util.HashMap<String,String>());
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
		addProperties(connettoreMsg, endpointConfig);
		if(https) {
			addProperties(connettoreMsg, sslConfig);
			if(httpsClient) {
				addProperties(connettoreMsg, sslClientConfig);
			}
		}
		
		byte[] content = null;
		
		TransportRequestContext transportRequestContext = new TransportRequestContext(log);
		transportRequestContext.setRequestType(httpMethod.name());
		transportRequestContext.setHeaders(new HashMap<String, List<String>>());
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
			transportRequestContext.setParameters(new HashMap<String, List<String>>());
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
		if(send==false) {
			if(connettore.getEccezioneProcessamento()!=null) {
				throw new Exception(prefixConnettore+connettore.getErrore(), connettore.getEccezioneProcessamento());
			}
			else {
				throw new Exception(prefixConnettore+connettore.getErrore());
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
			String msgSuccess = prefixConnettore+"Connessione completata con successo (codice trasporto: "+connettore.getCodiceTrasporto()+")";
			if(bout!=null && bout.size()>0) {
				log.debug(msgSuccess);
				httpResponse.setContent(bout.toByteArray());
				return httpResponse;
			}
			else {
				throw new Exception(msgSuccess+"; non è pervenuta alcuna risposta");
			}
		}
		else {
			String msgError = prefixConnettore+"Connessione terminata con errore (codice trasporto: "+connettore.getCodiceTrasporto()+")";
			if(bout!=null && bout.size()>0) {
				log.debug(msgError+": "+bout.toString());
				httpResponse.setContent(bout.toByteArray());
				return httpResponse;
			}
			else {
				log.error(msgError);
				throw new Exception(msgError);
			}
		}
		
		
	}
	
	private static void addProperties(ConnettoreMsg connettoreMsg, Properties p) {
		if(p!=null && p.size()>0) {
			Enumeration<?> en = p.propertyNames();
			while (en.hasMoreElements()) {
				Object oKey = (Object) en.nextElement();
				if(oKey!=null) {
					String key = (String) oKey;
					String value = p.getProperty(key);
					connettoreMsg.getConnectorProperties().put(key,value);
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// ********* [NEGOZIAZIONE-TOKEN] VALIDAZIONE CONFIGURAZIONE ****************** */
	
	public static void validazioneConfigurazione(PolicyNegoziazioneToken policyNegoziazioneToken) throws ProviderException, ProviderValidationException {
		NegoziazioneTokenProvider p = new NegoziazioneTokenProvider();
		p.validate(policyNegoziazioneToken.getProperties());
	}
	
	
	// ********* [NEGOZIAZIONE-TOKEN] ENDPOINT TOKEN ****************** */
	
	public static final String RETRIEVE_FUNCTION = "Negoziazione";
		
	public static EsitoNegoziazioneToken endpointToken(boolean debug, Logger log, PolicyNegoziazioneToken policyNegoziazioneToken, 
			Busta busta, RequestInfo requestInfo, TipoPdD tipoPdD,
			PdDContext pddContext, IProtocolFactory<?> protocolFactory) throws Exception {
		return _endpointToken(debug, log, policyNegoziazioneToken, 
				busta, requestInfo, tipoPdD,
				pddContext,  protocolFactory,
				false, null,
				null);
	}
	private static EsitoNegoziazioneToken _endpointToken(boolean debug, Logger log, PolicyNegoziazioneToken policyNegoziazioneToken, 
			Busta busta, RequestInfo requestInfo, TipoPdD tipoPdD,
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			boolean rinegozia, InformazioniNegoziazioneToken previousToken,
			InformazioniNegoziazioneToken_DatiRichiesta datiRichiesta) throws Exception {
		EsitoNegoziazioneToken esitoNegoziazioneToken = null;
		boolean riavviaNegoziazione = false;
		
		IState state = null;
		boolean delegata = TipoPdD.DELEGATA.equals(tipoPdD);
		IDSoggetto idDominio = null;
		IDServizio idServizio = null;
		if(busta!=null) {
			if(delegata) {
				if(busta.getTipoMittente()!=null && busta.getMittente()!=null) {
					idDominio = new IDSoggetto(busta.getTipoMittente(), busta.getMittente());
				}
			}
			else {
				if(busta.getTipoDestinatario()!=null && busta.getDestinatario()!=null) {
					idDominio = new IDSoggetto(busta.getTipoDestinatario(), busta.getDestinatario());
				}
			}
			if(busta.getTipoDestinatario()!=null && busta.getDestinatario()!=null && 
					busta.getTipoServizio()!=null && busta.getServizio()!=null && busta.getVersioneServizio()!=null) {
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(busta.getTipoServizio(), busta.getServizio(), 
						busta.getTipoDestinatario(), busta.getDestinatario(), 
						busta.getVersioneServizio());	
			}
		}
		
		String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		if(datiRichiesta==null && op2Properties.isGestioneRetrieveToken_saveAsTokenInfo_saveSourceRequest()) {
			datiRichiesta = new InformazioniNegoziazioneToken_DatiRichiesta();
			datiRichiesta.setPolicy(policyNegoziazioneToken.getName());
			datiRichiesta.setTransactionId(idTransazione);
			if(op2Properties.isGestioneRetrieveToken_saveAsTokenInfo_saveSourceRequest_date()) {
				datiRichiesta.setPrepareRequest(DateManager.getDate());	
			}
		}
		
		Map<String, Object> dynamicMap = buildDynamicNegoziazioneTokenMap(busta, requestInfo, pddContext, log, policyNegoziazioneToken.getName());
		NegoziazioneTokenDynamicParameters dynamicParameters = new NegoziazioneTokenDynamicParameters(dynamicMap, pddContext, policyNegoziazioneToken);
		
		if(GestoreToken.cacheToken==null){
			esitoNegoziazioneToken = _endpointToken(debug, log, policyNegoziazioneToken, 
					busta, requestInfo, tipoPdD,
					dynamicParameters, protocolFactory, idTransazione,
					state, delegata,
					idDominio, idServizio,
					rinegozia, previousToken,
					datiRichiesta);
			
			if(esitoNegoziazioneToken!=null && esitoNegoziazioneToken.isValido()) {
				// ricontrollo tutte le date (l'ho appena preso, dovrebbero essere buone) 
				boolean checkPerRinegoziazione = false;
				_validazioneInformazioniNegoziazioneToken(checkPerRinegoziazione, esitoNegoziazioneToken, policyNegoziazioneToken, 
						policyNegoziazioneToken.isSaveErrorInCache());
			}
			
		}
    	else{
    		String funzione = RETRIEVE_FUNCTION;
    		String keyCache = buildCacheKeyNegoziazione(policyNegoziazioneToken.getName(), funzione, delegata, dynamicParameters);

    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
    		
    		org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheToken.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					GestoreToken.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
					esitoNegoziazioneToken = (EsitoNegoziazioneToken) response.getObject();
					esitoNegoziazioneToken.setInCache(true);
				}else if(response.getException()!=null){
					GestoreToken.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
					throw (Exception) response.getException();
				}else{
					GestoreToken.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
				}
			}
    		
			if(esitoNegoziazioneToken==null) {
				//synchronized (GestoreToken.semaphoreNegoziazione) {
				GestoreToken.lockNegoziazione.acquire("endpointToken", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheToken.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreToken.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
							esitoNegoziazioneToken = (EsitoNegoziazioneToken) response.getObject();
							esitoNegoziazioneToken.setInCache(true);
						}else if(response.getException()!=null){
							GestoreToken.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
							throw (Exception) response.getException();
						}else{
							GestoreToken.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
	
					if(esitoNegoziazioneToken==null) {
						// Effettuo la query
						GestoreToken.logger.debug("oggetto con chiave ["+keyCache+"] (method:"+funzione+") eseguo operazione...");
						esitoNegoziazioneToken = _endpointToken(debug, log, policyNegoziazioneToken, 
								busta, requestInfo, tipoPdD,
								dynamicParameters, protocolFactory, idTransazione,
								state, delegata,
								idDominio, idServizio,
								rinegozia, previousToken,
								datiRichiesta);
							
						// Aggiungo la risposta in cache (se esiste una cache)	
						// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
						// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
						// - impostare il noCache a true
						if(esitoNegoziazioneToken!=null){ // altrimenti lo mettero' in cache al giro dopo.
							esitoNegoziazioneToken.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
							if(!esitoNegoziazioneToken.isNoCache()){
								GestoreToken.logger.info("Aggiungo oggetto ["+keyCache+"] in cache");
								try{	
									org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
									responseCache.setObject(esitoNegoziazioneToken);
									GestoreToken.cacheToken.put(keyCache,responseCache);
								}catch(UtilsException e){
									GestoreToken.logger.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
								}
							}
						}else{
							throw new TokenException("Metodo (GestoreToken."+funzione+") ha ritornato un valore di esito null");
						}
						
						if(esitoNegoziazioneToken.isValido()) {
							// ricontrollo tutte le date (l'ho appena preso, dovrebbero essere buone) 
							boolean checkPerRinegoziazione = false;
							_validazioneInformazioniNegoziazioneToken(checkPerRinegoziazione, esitoNegoziazioneToken, policyNegoziazioneToken, 
									policyNegoziazioneToken.isSaveErrorInCache());
						}
					}
					else {
					
						// l'ho preso in cache
						
						if(esitoNegoziazioneToken.isValido()) {
							// controllo la data qua
							boolean checkPerRinegoziazione = true;
							_validazioneInformazioniNegoziazioneToken(checkPerRinegoziazione, esitoNegoziazioneToken, policyNegoziazioneToken, 
									policyNegoziazioneToken.isSaveErrorInCache());
							if(!esitoNegoziazioneToken.isValido() && !esitoNegoziazioneToken.isDateValide()) {
								// DEVO riavviare la negoziazione poichè è scaduto
								GestoreToken.cacheToken.remove(keyCache);
								riavviaNegoziazione = true;
								//System.out.println("Riavvia negoziazione");
							}
						}
						
					}
					
				}finally {
					// fine synchronized
					GestoreToken.lockNegoziazione.release("endpointToken", idTransazione);
				}
			}
			else {
				
				// l'ho preso in cache
				
				if(esitoNegoziazioneToken.isValido()) {
					// controllo la data qua
					boolean checkPerRinegoziazione = true;
					_validazioneInformazioniNegoziazioneToken(checkPerRinegoziazione, esitoNegoziazioneToken, policyNegoziazioneToken, 
							policyNegoziazioneToken.isSaveErrorInCache());
					if(!esitoNegoziazioneToken.isValido() && !esitoNegoziazioneToken.isDateValide()) {
						
						GestoreToken.lockNegoziazione.acquire("removeToken", idTransazione);
						try {
							// DEVO riavviare la negoziazione poichè è scaduto
							GestoreToken.cacheToken.remove(keyCache);
							riavviaNegoziazione = true;
							//System.out.println("Riavvia negoziazione");
						}finally {
							// fine synchronized
							GestoreToken.lockNegoziazione.release("removeToken", idTransazione);
						}
					}
				}
				
			}
    	}
		
		if(riavviaNegoziazione) {
			return _endpointToken(debug, log, policyNegoziazioneToken, 
					busta, requestInfo, tipoPdD,
					pddContext, protocolFactory,
					riavviaNegoziazione, esitoNegoziazioneToken.getInformazioniNegoziazioneToken(),// refresh
					datiRichiesta); 
		}
		
		try {
			if(idTransazione!=null) {
				Transaction t = TransactionContext.getTransaction(idTransazione);
				t.setInformazioniNegoziazioneToken(esitoNegoziazioneToken.getInformazioniNegoziazioneToken());
			}
		}catch(Throwable t) {}
		
		return esitoNegoziazioneToken;
	}
	
	private static EsitoNegoziazioneToken _endpointToken(boolean debug, Logger log, PolicyNegoziazioneToken policyNegoziazioneToken,
			Busta busta, RequestInfo requestInfo, TipoPdD tipoPdD,
			NegoziazioneTokenDynamicParameters dynamicParameters, IProtocolFactory<?> protocolFactory,String idTransazione,
			IState state, boolean delegata,
			IDSoggetto idDominio, IDServizio idServizio,
			boolean rinegozia, InformazioniNegoziazioneToken previousToken,
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
			else if(policyNegoziazioneToken.isRfc7523_x509_Grant()) {
				refreshModeEnabled = OpenSPCoop2Properties.getInstance().isGestioneRetrieveToken_refreshToken_grantType_rfc7523_x509();	
				if(datiRichiesta!=null) {
					datiRichiesta.setGrantType(Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_X509);
				}
			}
			else if(policyNegoziazioneToken.isRfc7523_clientSecret_Grant()) {
				refreshModeEnabled = OpenSPCoop2Properties.getInstance().isGestioneRetrieveToken_refreshToken_grantType_rfc7523_clientSecret();
				if(datiRichiesta!=null) {
					datiRichiesta.setGrantType(Costanti.ID_RETRIEVE_TOKEN_METHOD_RFC_7523_CLIENT_SECRET);
				}
			}
		}catch(Throwable t) {
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
							float perc1 = Float.valueOf(secondsDiff+"").floatValue() / 100f;
							float perc2 = perc1 * Float.valueOf(percent+"").floatValue();
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
						//System.out.println("Controllo scadenza per refresh token now+tollerance("+secondsPreExpire+")["+org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(now)+"] exp["+
						//		org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(previousToken.getRefreshExpiresIn())+"]");
					}			
					if(!now.before(previousToken.getRefreshExpiresIn())){
						// scaduto refresh token
						log.debug("Refresh token scaduto");
						refreshModeEnabled = false;
					}
				}
				if(refreshModeEnabled) {
					esito = _invoke_endpointToken(debug, log, policyNegoziazioneToken,
							busta, requestInfo, tipoPdD,
							dynamicParameters, protocolFactory, idTransazione,
							state, delegata,
							idDominio, idServizio,
							refreshModeEnabled, previousToken,
							datiRichiesta);
					if(esito!=null && esito.isValido()) {
						// ricontrollo tutte le date (l'ho appena preso, dovrebbero essere buone) 
						boolean checkPerRinegoziazione = false;
						_validazioneInformazioniNegoziazioneToken(checkPerRinegoziazione, esito, policyNegoziazioneToken, 
								policyNegoziazioneToken.isSaveErrorInCache());
						if(datiRichiesta!=null) {
							datiRichiesta.setRefresh(true);
						}
						return esito;
					}
					if(esito==null || esito.getEccezioneProcessamento()==null) {
						throw new Exception("token not refreshed");
					}
					else {
						throw new Exception("token not refreshed: "+esito.getEccezioneProcessamento());
					}
				}
			}catch(Throwable t) {
				String msgError = "Refresh token failed: "+t.getMessage();
				log.error(msgError);
			}
		}
		
		if(datiRichiesta!=null) {
			datiRichiesta.setRefresh(null);
		}
		return _invoke_endpointToken(debug, log, policyNegoziazioneToken,
				busta, requestInfo, tipoPdD,
				dynamicParameters, protocolFactory, idTransazione,
				state, delegata,
				idDominio, idServizio,
				false, null,
				datiRichiesta);
		
	}
	
	
	private static EsitoNegoziazioneToken _invoke_endpointToken(boolean debug, Logger log, PolicyNegoziazioneToken policyNegoziazioneToken,
			Busta busta, RequestInfo requestInfo, TipoPdD tipoPdD,
			NegoziazioneTokenDynamicParameters dynamicParameters, IProtocolFactory<?> protocolFactory,String idTransazione,
			IState state, boolean delegata,
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
						dynamicParameters, protocolFactory, idTransazione,
						state, delegata,
						idDominio, idServizio,
						esitoNegoziazioneToken,
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
    			if(policyNegoziazioneToken.isSaveErrorInCache()) {
    				esitoNegoziazioneToken.setNoCache(false);
    			}
    			else {
    				esitoNegoziazioneToken.setNoCache(true);
    			}
    			esitoNegoziazioneToken.setEccezioneProcessamento(eProcess);
    			if(detailsError!=null) {
    				esitoNegoziazioneToken.setDetails(detailsError);	
				}
				else {
					esitoNegoziazioneToken.setDetails("AccessToken non recuperabile");	
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
	
	
	
	
	
	// ********* [NEGOZIAZIONE-TOKEN]  UTILITIES INTERNE ****************** */
	
	private static String buildPrefixCacheKeyNegoziazione(String policy, String funzione) {
		StringBuilder bf = new StringBuilder(funzione);
    	bf.append("_");
    	bf.append(policy);
    	bf.append("_");
    	return bf.toString();
	}
	private static String buildCacheKeyNegoziazione(String policy, String funzione, boolean portaDelegata, NegoziazioneTokenDynamicParameters dynamicParameters) {
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
	private static void _validazioneInformazioniNegoziazioneToken(boolean checkPerRinegoziazione, EsitoNegoziazioneToken esitoNegoziazioneToken, PolicyNegoziazioneToken policyNegoziazioneToken, boolean saveErrorInCache) throws Exception {
		
		Date now = DateManager.getDate();
		
		if(esitoNegoziazioneToken.isValido()) {			
			if(esitoNegoziazioneToken.getInformazioniNegoziazioneToken().getExpiresIn()!=null) {			
				
				if(checkPerRinegoziazione) {
					int secondsPreExpire = -1;
					OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
					if(properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_percent()!=null && properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_percent()>0) {
						int percent = properties.getGestioneRetrieveToken_refreshTokenBeforeExpire_percent();
						long secondsExpire = esitoNegoziazioneToken.getInformazioniNegoziazioneToken().getExpiresIn().getTime() / 1000l;
						long secondsIat = esitoNegoziazioneToken.getInformazioniNegoziazioneToken().getRetrievedIn().getTime() / 1000l;
						long secondsDiff = secondsExpire - secondsIat;
						if(secondsDiff>0) {
							float perc1 = Float.valueOf(secondsDiff+"").floatValue() / 100f;
							float perc2 = perc1 * Float.valueOf(percent+"").floatValue();
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
						//System.out.println("Controllo scadenza per rinegoziazione now+tollerance("+secondsPreExpire+")["+org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(now)+"] exp["+
						//		org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(esitoNegoziazioneToken.getInformazioniNegoziazioneToken().getExpiresIn())+"]");
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
			
		}
			
		if(esitoNegoziazioneToken.isValido()==false) {
			if(saveErrorInCache) {
				esitoNegoziazioneToken.setNoCache(false);
			}
			else {
				esitoNegoziazioneToken.setNoCache(true);
			}
		}
	}
	
	private static Map<String, Object> buildDynamicNegoziazioneTokenMap(Busta busta, 
			RequestInfo requestInfo, PdDContext pddContext, Logger log,
			String nomeTokenPolicy) throws Exception {
	
		Map<String, Object> dynamicMap = new HashMap<String, Object>();
		
		Map<String, List<String>> pTrasporto = null;
		String urlInvocazione = null;
		Map<String, List<String>> pQuery = null;
		Map<String, List<String>> pForm = null;
		if(requestInfo!=null && requestInfo.getProtocolContext()!=null) {
			pTrasporto = requestInfo.getProtocolContext().getHeaders();
			urlInvocazione = requestInfo.getProtocolContext().getUrlInvocazione_formBased();
			pQuery = requestInfo.getProtocolContext().getParameters();
			if(requestInfo.getProtocolContext() instanceof HttpServletTransportRequestContext) {
				HttpServletTransportRequestContext httpServletContext = (HttpServletTransportRequestContext) requestInfo.getProtocolContext();
				HttpServletRequest httpServletRequest = httpServletContext.getHttpServletRequest();
				if(httpServletRequest!=null && httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
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
	
	public static HttpResponse http(boolean debug, Logger log, PolicyNegoziazioneToken policyNegoziazioneToken,
			Busta busta, RequestInfo requestInfo, TipoPdD tipoPdD,
			NegoziazioneTokenDynamicParameters dynamicParameters, IProtocolFactory<?> protocolFactory, String idTransazione,
			IState state, boolean delegata,
			IDSoggetto idDominio, IDServizio idServizio,
			EsitoNegoziazioneToken esitoNegoziazioneToken,
			boolean refreshModeEnabled, String refreshToken,
			InformazioniNegoziazioneToken_DatiRichiesta datiRichiesta) throws Exception {
		
		
		// *** Raccola Parametri ***
		
		String endpoint = dynamicParameters.getEndpoint();
		if(datiRichiesta!=null) {
			datiRichiesta.setEndpoint(endpoint);
		}
		
		HttpRequestMethod httpMethod = HttpRequestMethod.POST;	
		
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
			password = policyNegoziazioneToken.getBasicAuthentication_password();
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
		
		ForwardProxy forwardProxy = null;
		ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
		if(configurazionePdDManager.isForwardProxyEnabled()) {
			try {
				IDGenericProperties policy = new IDGenericProperties();
				policy.setTipologia(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE);
				policy.setNome(policyNegoziazioneToken.getName());
				if(delegata) {
					forwardProxy = configurazionePdDManager.getForwardProxyConfigFruizione(idDominio, idServizio, policy);
				}
				else {
					forwardProxy = configurazionePdDManager.getForwardProxyConfigErogazione(idDominio, idServizio, policy);
				}
			}catch(Exception e) {
				throw new Exception("Configurazione errata per la funzionalità govway-proxy; "+e.getMessage(),e);
			}
		}
		if(forwardProxy!=null && forwardProxy.isEnabled() && forwardProxy.getConfigToken()!=null && forwardProxy.getConfigToken().isTokenRetrieveEnabled()) {
			connettoreMsg.setForwardProxy(forwardProxy);
		}
		
		connettore.setForceDisable_rest_proxyPassReverse(true);
		connettore.init(dynamicParameters.getPddContext(), protocolFactory);
		connettore.setRegisterSendIntoContext(false);
		
		if(basic && basicAsAuthorizationHeader){
			InvocazioneCredenziali credenziali = new InvocazioneCredenziali();
			credenziali.setUser(username);
			credenziali.setPassword(password);
			connettoreMsg.setCredenziali(credenziali);
		}
		
		connettoreMsg.setConnectorProperties(new java.util.HashMap<String,String>());
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
		addProperties(connettoreMsg, endpointConfig);
		if(https) {
			addProperties(connettoreMsg, sslConfig);
			if(httpsClient) {
				addProperties(connettoreMsg, sslClientConfig);
			}
		}
		
		byte[] content = null;
		
		TransportRequestContext transportRequestContext = new TransportRequestContext(log);
		transportRequestContext.setRequestType(httpMethod.name());
		transportRequestContext.setHeaders(new HashMap<String, List<String>>());
		if(bearer) {
			String authorizationHeader = HttpConstants.AUTHORIZATION_PREFIX_BEARER+bearerToken;
			TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.AUTHORIZATION, authorizationHeader);
		}
		transportRequestContext.removeHeader(HttpConstants.CONTENT_TYPE);
		String contentType = HttpConstants.CONTENT_TYPE_X_WWW_FORM_URLENCODED;
		TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.CONTENT_TYPE, contentType);
		
		Map<String, List<String>> pContent = new HashMap<String, List<String>>();
		if(refreshModeEnabled) {
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE, ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE_REFRESH_TOKEN);
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REFRESH_TOKEN, refreshToken);
		}
		else if(policyNegoziazioneToken.isClientCredentialsGrant()) {
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE, ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE_CLIENT_CREDENTIALS_GRANT);
		}
		else if(policyNegoziazioneToken.isRfc7523_x509_Grant() || policyNegoziazioneToken.isRfc7523_clientSecret_Grant()) {
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE, ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE_CLIENT_CREDENTIALS_GRANT);
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_CLIENT_ASSERTION_TYPE, ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_CLIENT_ASSERTION_TYPE_RFC_7523);
		}
		else if(policyNegoziazioneToken.isUsernamePasswordGrant()) {
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE, ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_GRANT_TYPE_RESOURCE_OWNER_PASSWORD_CREDENTIALS_GRANT);
		}
		else {
			throw new Exception("Nessuna modalità definita");
		}
		
		if(basic && !basicAsAuthorizationHeader){
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_CLIENT_ID, username);
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_CLIENT_SECRET, password);
		}
		
		if(policyNegoziazioneToken.isUsernamePasswordGrant()) {
			String usernamePasswordGrant = dynamicParameters.getUsernamePasswordGrant();
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_USERNAME, usernamePasswordGrant);
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_PASSWORD, policyNegoziazioneToken.getUsernamePasswordGrant_password());
			if(datiRichiesta!=null) {
				datiRichiesta.setUsername(usernamePasswordGrant);
			}
		}
		
		if(policyNegoziazioneToken.isRfc7523_x509_Grant() || policyNegoziazioneToken.isRfc7523_clientSecret_Grant()) {
			String jwt = buildJwt(policyNegoziazioneToken,
					busta, tipoPdD,
					dynamicParameters);
			String signedJwt = signJwt(policyNegoziazioneToken, jwt, contentType, dynamicParameters);
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_CLIENT_ASSERTION, signedJwt);
			if(datiRichiesta!=null) {
				boolean infoNormalizzate = properties.isGestioneRetrieveToken_grantType_rfc7523_saveClientAssertionJWTInfo_transazioniRegistrazioneInformazioniNormalizzate();
				datiRichiesta.setJwtClientAssertion(new InformazioniJWTClientAssertion(log, signedJwt, infoNormalizzate));
			}
		}
		
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
			}
		}
		
		String aud = dynamicParameters.getAudience();
		if(aud!=null && !"".equals(aud)) {
			TransportUtils.setParameter(pContent,ClaimsNegoziazione.OAUTH2_RFC_6749_REQUEST_AUDIENCE, aud);
		}
		
		if(policyNegoziazioneToken.isPDND()) {
			String formClientId = dynamicParameters.getFormClientId();
			if(formClientId!=null && !"".equals(formClientId) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(formClientId)) {
				TransportUtils.setParameter(pContent,Costanti.PDND_OAUTH2_RFC_6749_REQUEST_CLIENT_ID, formClientId);
			}
		}
		
		String parameters = dynamicParameters.getParameters();
		if(parameters!=null && !"".equals(parameters)) {
			Properties convertTextToProperties = PropertiesUtilities.convertTextToProperties(parameters);
			if(convertTextToProperties!=null && !convertTextToProperties.isEmpty()) {
				Enumeration<Object> keys = convertTextToProperties.keys();
				while (keys.hasMoreElements()) {
					String nome = (String) keys.nextElement();
					String valore = convertTextToProperties.getProperty(nome);
					if(nome!=null && !"".equals(nome) && valore!=null) {
						TransportUtils.setParameter(pContent, nome, valore);
					}
				}
			}
		}
		
		String prefixUrl = "PREFIX?";
		String contentString = TransportUtils.buildUrlWithParameters(pContent, prefixUrl , log);
		contentString = contentString.substring(prefixUrl.length());
		if(contentString.startsWith("&") && contentString.length()>1) {
			contentString = contentString.substring(1);
		}
		content = contentString.getBytes();
			
		OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(MessageType.BINARY, transportRequestContext, content);
		OpenSPCoop2Message msg = pr.getMessage_throwParseException();
		connettoreMsg.setRequestMessage(msg);
		connettoreMsg.setGenerateErrorWithConnectorPrefix(false);
		connettore.setHttpMethod(msg);
		
		ResponseCachingConfigurazione responseCachingConfigurazione = new ResponseCachingConfigurazione();
		responseCachingConfigurazione.setStato(StatoFunzionalita.DISABILITATO);
		String prefixConnettore = "[EndpointNegoziazioneToken: "+endpoint+"] ";
		if(endpointConfig.containsKey(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME)) {
			String hostProxy = endpointConfig.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME);
			String portProxy = endpointConfig.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT);
			prefixConnettore = prefixConnettore+" [via Proxy: "+hostProxy+":"+portProxy+"] ";
		}
		if(datiRichiesta!=null && datiRichiesta.getPrepareRequest()!=null) {
			datiRichiesta.setSendRequest(DateManager.getDate());
		}
		boolean send = connettore.send(responseCachingConfigurazione, connettoreMsg);
		if(datiRichiesta!=null && datiRichiesta.getPrepareRequest()!=null) {
			datiRichiesta.setReceiveResponse(DateManager.getDate());
		}
		if(send==false) {
			if(connettore.getEccezioneProcessamento()!=null) {
				throw new Exception(prefixConnettore+connettore.getErrore(), connettore.getEccezioneProcessamento());
			}
			else {
				throw new Exception(prefixConnettore+connettore.getErrore());
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
			String msgSuccess = prefixConnettore+"Connessione completata con successo (codice trasporto: "+connettore.getCodiceTrasporto()+")";
			if(bout!=null && bout.size()>0) {
				log.debug(msgSuccess);
				httpResponse.setContent(bout.toByteArray());
				return httpResponse;
			}
			else {
				throw new Exception(msgSuccess+"; non è pervenuta alcuna risposta");
			}
		}
		else {
			String msgError = prefixConnettore+"Connessione terminata con errore (codice trasporto: "+connettore.getCodiceTrasporto()+")";
			if(bout!=null && bout.size()>0) {
				log.debug(msgError+": "+bout.toString());
				httpResponse.setContent(bout.toByteArray());
				return httpResponse;
			}
			else {
				log.error(msgError);
				throw new Exception(msgError);
			}
		}
		
		
	}
	
	private static String buildJwt(PolicyNegoziazioneToken policyNegoziazioneToken,
			Busta busta, TipoPdD tipoPdD,
			NegoziazioneTokenDynamicParameters dynamicParameters) throws Exception {
		
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
				issuer = op2Properties.getIdentitaPortaDefault(null).getNome();
			}
		}
		if(!Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(issuer)) {
			jwtPayload.put(Claims.OIDC_ID_TOKEN_ISSUER, issuer);
		}
		
		// For client authentication, the subject MUST be the "client_id" of the OAuth client.
		String clientId = dynamicParameters.getSignedJwtClientId();
		if(clientId==null) {
			throw new Exception("ClientID undefined");
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
			throw new Exception("JWT-Audience undefined");
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
			throw new Exception("Invalid JWT-TimeToLive value: "+e.getMessage(),e);
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
				throw new Exception("Invalid JWT-TimeToLive value: "+e.getMessage(),e);
			}
			jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID, uuid);
		}
		
		if(policyNegoziazioneToken.isPDND()) {
			
			// purposeId
			String jwtPurposeId = dynamicParameters.getSignedJwtPurposeId();
			if(jwtPurposeId==null) {
				throw new Exception("JWT-PurposeId undefined");
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
						String valore = convertTextToProperties.getProperty(nome);
						if(nome!=null && !"".equals(nome) && valore!=null) {
							if(sessionInfoPayload ==null) {
								sessionInfoPayload = jsonUtils.newObjectNode();
							}
							
							if(valore.trim().startsWith("[") && valore.trim().endsWith("]")) {
								JsonNode node = jsonUtils.getAsNode(valore);
								sessionInfoPayload.set(nome, node);
							}
							else if(valore.trim().startsWith("{") && valore.trim().endsWith("}")) {
								JsonNode node = jsonUtils.getAsNode(valore);
								sessionInfoPayload.set(nome, node);
							}
							else {
								sessionInfoPayload.put(nome, valore);
							}
						}
					}
				}
				if(sessionInfoPayload!=null) {
					jwtPayload.set(Costanti.PDND_SESSION_INFO, sessionInfoPayload);
				}
			}
		}
		
		// claims
		String claims = dynamicParameters.getSignedJwtClaims();
		if(claims!=null && !"".equals(claims)) {
			Properties convertTextToProperties = PropertiesUtilities.convertTextToProperties(claims);
			if(convertTextToProperties!=null && !convertTextToProperties.isEmpty()) {
				Enumeration<Object> keys = convertTextToProperties.keys();
				while (keys.hasMoreElements()) {
					String nome = (String) keys.nextElement();
					String valore = convertTextToProperties.getProperty(nome);
					if(nome!=null && !"".equals(nome) && valore!=null) {
						if(valore.trim().startsWith("[") && valore.trim().endsWith("]")) {
							JsonNode node = jsonUtils.getAsNode(valore);
							jwtPayload.set(nome, node);
						}
						else if(valore.trim().startsWith("{") && valore.trim().endsWith("}")) {
							JsonNode node = jsonUtils.getAsNode(valore);
							jwtPayload.set(nome, node);
						}
						else {
							jwtPayload.put(nome, valore);
						}
					}
				}
			}
		}
		
		return jsonUtils.toString(jwtPayload);
	}
	
	private static String signJwt(PolicyNegoziazioneToken policyNegoziazioneToken, String payload, String contentType,
			NegoziazioneTokenDynamicParameters dynamicParameters) throws Exception {
		
		String signAlgo = policyNegoziazioneToken.getJwtSignAlgorithm();
		if(signAlgo==null) {
			throw new Exception("SignAlgorithm undefined");
		}
		
		KeyStore ks = null;
		JWKSetStore jwtStore = null;
		String keyAlias = null;
		String keyPassword = null;
		if(policyNegoziazioneToken.isRfc7523_x509_Grant()) {
			
			String keystoreType = policyNegoziazioneToken.getJwtSignKeystoreType();
			if(keystoreType==null) {
				throw new Exception("JWT Signature keystore type undefined");
			}
			String keystoreFile = policyNegoziazioneToken.getJwtSignKeystoreFile();
			if(keystoreFile==null) {
				throw new Exception("JWT Signature keystore file undefined");
			}
			String keystorePassword = policyNegoziazioneToken.getJwtSignKeystorePassword();
			if(keystorePassword==null) {
				throw new Exception("JWT Signature keystore password undefined");
			}
			keyAlias = policyNegoziazioneToken.getJwtSignKeyAlias();
			if(keyAlias==null) {
				throw new Exception("JWT Signature key alias undefined");
			}
			keyPassword = policyNegoziazioneToken.getJwtSignKeyPassword();
			if(keyPassword==null) {
				throw new Exception("JWT Signature key password undefined");
			}
			
			if("jwk".equalsIgnoreCase(keystoreType)) {
				jwtStore = GestoreKeystoreCache.getJwkSetStore(keystoreFile);
			}
			else {
				MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(keystoreFile, keystoreType, keystorePassword);
				if(merlinKs==null) {
					throw new Exception("Accesso al keystore '"+keystoreFile+"' non riuscito");
				}
				ks = merlinKs.getKeyStore();
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
		if(policyNegoziazioneToken.isJwtSignIncludeX509Cert()) {
			jwtHeaders.setAddX5C(true);
		}
		String url = dynamicParameters.getSignedJwtX509Url();
		if(url!=null && !"".equals(url)) {
			jwtHeaders.setX509Url(new URI(url));
		}
		if(policyNegoziazioneToken.isJwtSignIncludeX509CertSha1()) {
			jwtHeaders.setX509IncludeCertSha1(true);
		}
		if(policyNegoziazioneToken.isJwtSignIncludeX509CertSha256()) {
			jwtHeaders.setX509IncludeCertSha256(true);
		}
		if(policyNegoziazioneToken.isJwtSignJoseContentType()) {
			if(contentType!=null && !"".equals(contentType)) {
				jwtHeaders.setContentType(contentType);
			}
		}
		String type = policyNegoziazioneToken.getJwtSignJoseType();
		if(type!=null && !"".equals(type) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(type)) { // funzionalita' undefined undocumented
			jwtHeaders.setType(type);
		}
		if(ks!=null) {
			Certificate cert = ks.getCertificate(keyAlias);
			if(cert!=null && cert instanceof X509Certificate) {
				jwtHeaders.addX509cert((X509Certificate)cert);
			}
		}
		
		if(policyNegoziazioneToken.isRfc7523_clientSecret_Grant()) {
		
			String clientSecret = policyNegoziazioneToken.getJwtClientSecret();
			if(clientSecret==null) {
				throw new Exception("ClientSecret undefined");
			}
			
			JsonSignature jsonSignature = new JsonSignature(clientSecret, signAlgo, jwtHeaders, options);
			return jsonSignature.sign(payload);
		}
		else if(policyNegoziazioneToken.isRfc7523_x509_Grant()) {
						
			JsonSignature jsonSignature = null;
			if(jwtStore!=null) {
				jsonSignature = new JsonSignature(jwtStore.getJwkSet().getJsonWebKeys(), false, keyAlias, signAlgo, jwtHeaders, options);
			}
			else {
				jsonSignature = new JsonSignature(ks, false, keyAlias,  keyPassword, signAlgo, jwtHeaders, options);
			}
			return jsonSignature.sign(payload);
		}
		else {
			throw new Exception("JWT Signed mode unknown");
		}
		
	}
	
	
	
	
	
	
	
	
	
	// ********* [ATTRIBUTE AUTHORITY] VALIDAZIONE CONFIGURAZIONE ****************** */
	
	public static void validazioneConfigurazione(PolicyAttributeAuthority policyAttributeAuthority) throws ProviderException, ProviderValidationException {
		AttributeAuthorityProvider p = new AttributeAuthorityProvider();
		p.validate(policyAttributeAuthority.getProperties());
	}
	
	
	// ********* [ATTRIBUTE AUTHORITY] ENDPOINT TOKEN ****************** */
	
	public static final String ATTRIBUTE_AUTHORITY_FUNCTION = "AttributeAuthority";
	
	public static EsitoRecuperoAttributi readAttributes(Logger log, org.openspcoop2.pdd.core.token.attribute_authority.AbstractDatiInvocazione datiInvocazione,
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			boolean portaDelegata,
			IDSoggetto idDominio, IDServizio idServizio) throws Exception {
		EsitoRecuperoAttributi esitoRecuperoAttributi = null;
		boolean riavviaNegoziazione = false;
			
		PolicyAttributeAuthority policyAttributeAuthority = datiInvocazione.getPolicyAttributeAuthority();
		OpenSPCoop2Message message = datiInvocazione.getMessage();
		Busta busta = datiInvocazione.getBusta();
		RequestInfo requestInfo = datiInvocazione.getRequestInfo();	
		
		Map<String, Object> dynamicMap = buildDynamicAAMap(message, busta, requestInfo, pddContext, log,
				policyAttributeAuthority.getName(), datiInvocazione);
		AttributeAuthorityDynamicParameters dynamicParameters = new AttributeAuthorityDynamicParameters(dynamicMap, pddContext, policyAttributeAuthority);
		
		ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(datiInvocazione.getState());
		
		if(GestoreToken.cacheToken==null){
						
			boolean addIdAndDate = true;
			String request = buildDynamicAARequest(configurazionePdDManager, message, busta, requestInfo, log, policyAttributeAuthority, dynamicParameters, addIdAndDate);
			
			esitoRecuperoAttributi = _readAttributes(log, policyAttributeAuthority, 
					protocolFactory,
					dynamicParameters,
					request, portaDelegata,
					datiInvocazione.getState(),
					idDominio, idServizio);
			
			if(esitoRecuperoAttributi!=null && esitoRecuperoAttributi.isValido()) {
				// ricontrollo tutte le date (l'ho appena preso, dovrebbero essere buone) 
				_validazioneInformazioniAttributiRecuperati(esitoRecuperoAttributi, policyAttributeAuthority, 
						policyAttributeAuthority.isSaveErrorInCache(),
						protocolFactory,
						dynamicParameters);
			}
			
		}
    	else{
    		String funzione = ATTRIBUTE_AUTHORITY_FUNCTION;
    		
    		boolean addIdAndDate = true;
    		
    		// Aggiungo anche la richiesta poichè può venire costruita con freemarker o template e quindi può essere dinamica a sua volta (non considero però le date)
    		String requestKeyCache = buildDynamicAARequest(configurazionePdDManager, message, busta, requestInfo, log, policyAttributeAuthority, dynamicParameters, !addIdAndDate);
    		
    		String keyCache = buildCacheKeyRecuperoAttributi(policyAttributeAuthority.getName(), funzione, portaDelegata, dynamicParameters, requestKeyCache);

    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
    		
    		org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheToken.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					GestoreToken.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
					esitoRecuperoAttributi = (EsitoRecuperoAttributi) response.getObject();
					esitoRecuperoAttributi.setInCache(true);
				}else if(response.getException()!=null){
					GestoreToken.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
					throw (Exception) response.getException();
				}else{
					GestoreToken.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
				}
			}
    		
			String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
			
			if(esitoRecuperoAttributi==null) {
			
				//synchronized (GestoreToken.semaphoreAttributeAuthority) {
				GestoreToken.lockAttributeAuthority.acquire("readAttributes", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheToken.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreToken.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
							esitoRecuperoAttributi = (EsitoRecuperoAttributi) response.getObject();
							esitoRecuperoAttributi.setInCache(true);
						}else if(response.getException()!=null){
							GestoreToken.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.");
							throw (Exception) response.getException();
						}else{
							GestoreToken.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
	
					if(esitoRecuperoAttributi==null) {
						// Effettuo la query
						GestoreToken.logger.debug("oggetto con chiave ["+keyCache+"] (method:"+funzione+") eseguo operazione...");
						
						String request = null;
						if(policyAttributeAuthority.isRequestDynamicPayloadJwt()) {
							// ricostruisco per considerare le date
							request = buildDynamicAARequest(configurazionePdDManager, message, busta, requestInfo, log, policyAttributeAuthority, dynamicParameters, addIdAndDate);
						}
						else {
							request = requestKeyCache;
						}
						
						esitoRecuperoAttributi = _readAttributes(log, policyAttributeAuthority, 
								protocolFactory,
								dynamicParameters,
								request, portaDelegata,
								datiInvocazione.getState(),
								idDominio, idServizio);
							
						// Aggiungo la risposta in cache (se esiste una cache)	
						// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
						// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
						// - impostare il noCache a true
						if(esitoRecuperoAttributi!=null){ // altrimenti lo mettero' in cache al giro dopo.
							esitoRecuperoAttributi.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
							if(!esitoRecuperoAttributi.isNoCache()){
								GestoreToken.logger.info("Aggiungo oggetto ["+keyCache+"] in cache");
								try{	
									org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
									responseCache.setObject(esitoRecuperoAttributi);
									GestoreToken.cacheToken.put(keyCache,responseCache);
								}catch(UtilsException e){
									GestoreToken.logger.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
								}
							}
						}else{
							throw new TokenException("Metodo (GestoreToken."+funzione+") ha ritornato un valore di esito null");
						}
						
						if(esitoRecuperoAttributi.isValido()) {
							// ricontrollo tutte le date (l'ho appena preso, dovrebbero essere buone) 
							_validazioneInformazioniAttributiRecuperati(esitoRecuperoAttributi, policyAttributeAuthority, 
									policyAttributeAuthority.isSaveErrorInCache(),
									protocolFactory,
									dynamicParameters);
						}
					}
					else {
					
						// l'ho preso in cache
						
						if(esitoRecuperoAttributi.isValido()) {
							// controllo la data qua
							_validazioneInformazioniAttributiRecuperati(esitoRecuperoAttributi, policyAttributeAuthority, 
									policyAttributeAuthority.isSaveErrorInCache(),
									protocolFactory,
									dynamicParameters);
							if(!esitoRecuperoAttributi.isValido() && !esitoRecuperoAttributi.isDateValide()) {
								// DEVO riavviare la negoziazione poichè è scaduto
								GestoreToken.cacheToken.remove(keyCache);
								riavviaNegoziazione = true;
							}
						}
						
					}
					
				} finally{
					// fine synchronized
					GestoreToken.lockAttributeAuthority.release("readAttributes", idTransazione);
				}
			}
			else {
				
				// l'ho preso in cache
				
				if(esitoRecuperoAttributi.isValido()) {
					// controllo la data qua
					_validazioneInformazioniAttributiRecuperati(esitoRecuperoAttributi, policyAttributeAuthority, 
							policyAttributeAuthority.isSaveErrorInCache(),
							protocolFactory,
							dynamicParameters);
					if(!esitoRecuperoAttributi.isValido() && !esitoRecuperoAttributi.isDateValide()) {
						GestoreToken.lockAttributeAuthority.acquire("removeAttributes", idTransazione);
						try {
							// DEVO riavviare la negoziazione poichè è scaduto
							GestoreToken.cacheToken.remove(keyCache);
							riavviaNegoziazione = true;
						} finally{
							// fine synchronized
							GestoreToken.lockAttributeAuthority.release("removeAttributes", idTransazione);
						}
					}
				}
				
			}
    	}
		
		if(riavviaNegoziazione) {
			return readAttributes(log, datiInvocazione, pddContext, protocolFactory, portaDelegata,
					idDominio, idServizio);
		}
		return esitoRecuperoAttributi;
	}
	
	private static EsitoRecuperoAttributi _readAttributes(Logger log, PolicyAttributeAuthority policyAttributeAuthority,
			IProtocolFactory<?> protocolFactory,
			AttributeAuthorityDynamicParameters dynamicParameters,
			String request, boolean portaDelegata,
			IState state,
			IDSoggetto idDominio, IDServizio idServizio) {
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
						state, portaDelegata,
						idDominio, idServizio);
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
	    				JOSEUtils.injectKeystore(p, log); // serve per leggere il keystore dalla cache
	    				jsonCompactVerify = new JsonVerifySignature(p, options);
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
    			if(policyAttributeAuthority.isSaveErrorInCache()) {
    				esitoRecuperoAttributi.setNoCache(false);
    			}
    			else {
    				esitoRecuperoAttributi.setNoCache(true);
    			}
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
	
	
	
	
	
	// ********* [ATTRIBUTE AUTHORITY]  UTILITIES INTERNE ****************** */
	
	public static InformazioniAttributi normalizeInformazioniAttributi(List<InformazioniAttributi> list, List<AttributeAuthority> aaList) throws Exception {
		
		//int size = list.size();
		// Fix: deve basarsi sulle AA configurate e non su quelle da cui si e' ottenuto effettivamente una risposta, altrimenti poi le regole cambiano (prefisso AA presente o meno)
		int size = aaList.size();
		if(size==1) {
			return list.get(0);
		}
		else {
			return new InformazioniAttributi(OpenSPCoop2Properties.getInstance().isGestioneAttributeAuthority_saveSourceAttributeResponseInfo(), list.toArray(new InformazioniAttributi[1]));
		}
	}
	
	private static String buildCacheKeyRecuperoAttributiPrefix(String nomePolicy, String funzione) {
    	StringBuilder bf = new StringBuilder("AttributeAuthority_"+funzione);
    	bf.append("_");
    	bf.append(nomePolicy);
    	bf.append("_");
    	return bf.toString();
	}
	private static String buildCacheKeyRecuperoAttributi(String nomePolicy, String funzione, boolean portaDelegata,
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
	
	
	private static Map<String, Object> buildDynamicAAMap(OpenSPCoop2Message message, Busta busta, 
			RequestInfo requestInfo, PdDContext pddContext, Logger log,
			String nomeAttributeAuthority,
			org.openspcoop2.pdd.core.token.attribute_authority.AbstractDatiInvocazione datiInvocazione) throws Exception {
	
		Map<String, Object> dynamicMap = new HashMap<String, Object>();
		
		Map<String, List<String>> pTrasporto = null;
		String urlInvocazione = null;
		Map<String, List<String>> pQuery = null;
		Map<String, List<String>> pForm = null;
		if(requestInfo!=null && requestInfo.getProtocolContext()!=null) {
			pTrasporto = requestInfo.getProtocolContext().getHeaders();
			urlInvocazione = requestInfo.getProtocolContext().getUrlInvocazione_formBased();
			pQuery = requestInfo.getProtocolContext().getParameters();
			if(requestInfo.getProtocolContext() instanceof HttpServletTransportRequestContext) {
				HttpServletTransportRequestContext httpServletContext = (HttpServletTransportRequestContext) requestInfo.getProtocolContext();
				HttpServletRequest httpServletRequest = httpServletContext.getHttpServletRequest();
				if(httpServletRequest!=null && httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
					FormUrlEncodedHttpServletRequest formServlet = (FormUrlEncodedHttpServletRequest) httpServletRequest;
					if(formServlet.getFormUrlEncodedParametersValues()!=null &&
							!formServlet.getFormUrlEncodedParametersValues().isEmpty()) {
						pForm = formServlet.getFormUrlEncodedParametersValues();
					}
				}
			}
		}
		
		MessageContent messageContent = null;
		boolean bufferMessage_readOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
		if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
			messageContent = new MessageContent(message.castAsSoap(), bufferMessage_readOnly, pddContext);
		}
		else{
			if(MessageType.XML.equals(message.getMessageType())){
				messageContent = new MessageContent(message.castAsRestXml(), bufferMessage_readOnly, pddContext);
			}
			else if(MessageType.JSON.equals(message.getMessageType())){
				messageContent = new MessageContent(message.castAsRestJson(), bufferMessage_readOnly, pddContext);
			}
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
			listAttributi = new ArrayList<String>();
		}
		//if(listAttributi!=null && listAttributi.size()>0) {
		RequiredAttributes reqAttrs = new RequiredAttributes(listAttributi);
		dynamicMap.put(org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUIRED_ATTRIBUTES, reqAttrs);
		dynamicMap.put(org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUIRED_ATTRIBUTES.toLowerCase(), reqAttrs);
		//}
		
		return dynamicMap;
	}
	
	private static String buildDynamicAARequest(ConfigurazionePdDManager configurazionePdDManager,
			OpenSPCoop2Message message, Busta busta, 
			RequestInfo requestInfo, Logger log, 
			PolicyAttributeAuthority policyAttributeAuthority,
			AttributeAuthorityDynamicParameters dynamicParameters,
			boolean addIdAndDate) throws Exception {
			
		String dynamicContent = policyAttributeAuthority.getRequestDynamicPayload();
		String request = null;
		
		if(policyAttributeAuthority.isRequestDynamicPayloadTemplate() || policyAttributeAuthority.isRequestDynamicPayloadJwt()) {
			if(policyAttributeAuthority.isRequestDynamicPayloadTemplate()) {
				request = dynamicParameters.getRequestDynamicPayloadTemplate();
			}
			else {
				request = buildAAJwt(policyAttributeAuthority, dynamicParameters, addIdAndDate);
			}
		}
		else if(policyAttributeAuthority.isRequestDynamicPayloadFreemarkerTemplate()) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			Template template = configurazionePdDManager.getTemplateAttributeAuthorityRequest(policyAttributeAuthority.getName(), dynamicContent.getBytes());
			DynamicUtils.convertFreeMarkerTemplate(template, dynamicParameters.getDynamicMap(), bout);
			bout.flush();
			bout.close();
			request = bout.toString();
			
			if(policyAttributeAuthority.isRequestJws()) {
				request = normalizeJwtPayload(request);
			}
		}
		else if(policyAttributeAuthority.isRequestDynamicPayloadVelocityTemplate()) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			Template template = configurazionePdDManager.getTemplateAttributeAuthorityRequest(policyAttributeAuthority.getName(), dynamicContent.getBytes());
			DynamicUtils.convertVelocityTemplate(template, dynamicParameters.getDynamicMap(), bout);
			bout.flush();
			bout.close();
			request = bout.toString();
			
			if(policyAttributeAuthority.isRequestJws()) {
				request = normalizeJwtPayload(request);
			}
		}
		if(request==null || "".equals(request)) {
			throw new Exception("Request undefined");
		}
		
		return request;
	}
	
	private static String normalizeJwtPayload(String payload) throws Exception {
		JSONUtils jsonUtils = JSONUtils.getInstance(false);
		JsonNode jwtPayload = jsonUtils.getAsNode(payload);
		return jsonUtils.toString(jwtPayload);
	}
	
	private static String buildAAJwt(PolicyAttributeAuthority policyAttributeAuthority, 
			AttributeAuthorityDynamicParameters dynamicParameters,
			boolean addIdAndDate) throws Exception {
		
		// https://datatracker.ietf.org/doc/html/rfc7523
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		JSONUtils jsonUtils = JSONUtils.getInstance(false);
		
		ObjectNode jwtPayload = jsonUtils.newObjectNode();

		// add iat, nbf, exp
		long nowMs = DateManager.getTimeMillis();
		long nowSeconds = nowMs/1000;
		
		String issuer = dynamicParameters.getIssuer();
		if(issuer==null || "".equals(issuer)) {
			issuer = op2Properties.getIdentitaPortaDefault(null).getNome();
		}
		jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_ISSUER, issuer);
		
		String subject = dynamicParameters.getSubject();
		if(subject==null) {
			throw new Exception("JWT-Subject undefined");
		}
		jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_SUBJECT, subject);
		
		// The JWT MUST contain an "aud" (audience) claim containing a value that identifies the authorization server as an intended audience. 
		String audience = dynamicParameters.getAudience();
		if(audience==null) {
			throw new Exception("JWT-Audience undefined");
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
					String valore = convertTextToProperties.getProperty(nome);
					if(nome!=null && !"".equals(nome) && valore!=null) {
						if(valore.trim().startsWith("[") && valore.trim().endsWith("]")) {
							JsonNode node = jsonUtils.getAsNode(valore);
							jwtPayload.set(nome, node);
						}
						else if(valore.trim().startsWith("{") && valore.trim().endsWith("}")) {
							JsonNode node = jsonUtils.getAsNode(valore);
							jwtPayload.set(nome, node);
						}
						else {
							jwtPayload.put(nome, valore);
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
				throw new Exception("Invalid JWT-TimeToLive value: "+e.getMessage(),e);
			}
			long expired = nowSeconds+ttl;
			jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED, expired);
			
			// The JWT MAY contain a "jti" (JWT ID) claim that provides a unique identifier for the token.
			String uuid = null;
			try {
				uuid = UniqueIdentifierManager.newUniqueIdentifier().toString();
			}catch(Exception e) {
				throw new Exception("Invalid JWT-TimeToLive value: "+e.getMessage(),e);
			}
			jwtPayload.put(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID, uuid);
			
		}
		
		return jsonUtils.toString(jwtPayload);
	}
	
	private static void _validazioneInformazioniAttributiRecuperati(EsitoRecuperoAttributi esitoRecuperoAttributi, PolicyAttributeAuthority policyAttributeAuthority, boolean saveErrorInCache,
			IProtocolFactory<?> protocolFactory,
			AttributeAuthorityDynamicParameters dynamicParameters) throws Exception {
		
		Date now = DateManager.getDate();
		
		if(esitoRecuperoAttributi.isValido()) {			
			if(esitoRecuperoAttributi.getInformazioniAttributi().getExp()!=null) {				
				/*
				 *  The lifetime in seconds of the access token.  For example, the value "3600" denotes that the access token will
				 * expire in one hour from the time the response was generated.
				 * If omitted, the authorization server SHOULD provide the expiration time via other means or document the default value. 
				 **/
				if(!now.before(esitoRecuperoAttributi.getInformazioniAttributi().getExp())){
					esitoRecuperoAttributi.setTokenScaduto();
					esitoRecuperoAttributi.setDateValide(false);
					esitoRecuperoAttributi.setDetails("Attributes response expired");	
				}
			}
			
		}
			
		if(esitoRecuperoAttributi.isValido()) {
			if(esitoRecuperoAttributi.getInformazioniAttributi().getNbf()!=null) {				
				/*
				 *   The "nbf" (not before) claim identifies the time before which the JWT MUST NOT be accepted for processing.  
				 **/
				if(!esitoRecuperoAttributi.getInformazioniAttributi().getNbf().before(now)){
					esitoRecuperoAttributi.setTokenNotUsableBefore();
					esitoRecuperoAttributi.setDateValide(false);
					SimpleDateFormat sdf = DateUtils.getDefaultDateTimeFormatter(format);
					esitoRecuperoAttributi.setDetails("Attributes response not usable before "+sdf.format(esitoRecuperoAttributi.getInformazioniAttributi().getNbf()));
				}
			}
		}
		
		if(esitoRecuperoAttributi.isValido()) {
			if(esitoRecuperoAttributi.getInformazioniAttributi().getIat()!=null) {				
				/*
				 *   The "iat" (issued at) claim identifies the time at which the JWT was issued.  This claim can be used to determine the age of the JWT.
   				 *   The iat Claim can be used to reject tokens that were issued too far away from the current time, 
   				 *   limiting the amount of time that nonces need to be stored to prevent attacks. The acceptable range is Client specific. 
				 **/
				Integer old = OpenSPCoop2Properties.getInstance().getGestioneToken_iatTimeCheck_milliseconds();
				if(old!=null) {
					Date oldMax = new Date((DateManager.getTimeMillis() - old.intValue()));
					if(esitoRecuperoAttributi.getInformazioniAttributi().getIat().before(oldMax)) {
						esitoRecuperoAttributi.setTokenScaduto();
						esitoRecuperoAttributi.setDateValide(false);
						SimpleDateFormat sdf = DateUtils.getDefaultDateTimeFormatter(format);
						esitoRecuperoAttributi.setDetails("Attributes response expired; iat time '"+sdf.format(esitoRecuperoAttributi.getInformazioniAttributi().getIat())+"' too old");
					}
				}
			}
			
		}
		
		if(esitoRecuperoAttributi.isValido()) {		
			String audience = dynamicParameters.getResponseAudience();
			if(audience!=null && !"".equals(audience)) {
				if( esitoRecuperoAttributi.getInformazioniAttributi().getAud()==null || !esitoRecuperoAttributi.getInformazioniAttributi().getAud().contains(audience) ) {				
					esitoRecuperoAttributi.setTokenValidazioneFallita();
	    			if(policyAttributeAuthority.isSaveErrorInCache()) {
	    				esitoRecuperoAttributi.setNoCache(false);
	    			}
	    			else {
	    				esitoRecuperoAttributi.setNoCache(true);
	    			}
					esitoRecuperoAttributi.setDetails("Invalid audience");	
				}
			}
			
		}
		
		if(esitoRecuperoAttributi.isValido()) {		
			if(esitoRecuperoAttributi.getInformazioniAttributi().getAttributes()==null || esitoRecuperoAttributi.getInformazioniAttributi().getAttributes().isEmpty()) {
				if(policyAttributeAuthority.isSaveErrorInCache()) {
    				esitoRecuperoAttributi.setNoCache(false);
    			}
    			else {
    				esitoRecuperoAttributi.setNoCache(true);
    			}
				esitoRecuperoAttributi.setDetails("No attributes retrieved");
			}
		}
		
		if(esitoRecuperoAttributi.isValido()==false) {
			if(saveErrorInCache) {
				esitoRecuperoAttributi.setNoCache(false);
			}
			else {
				esitoRecuperoAttributi.setNoCache(true);
			}
		}
	}
	
	public static HttpResponse http(Logger log, PolicyAttributeAuthority policyAttributeAuthority,
			IProtocolFactory<?> protocolFactory,
			AttributeAuthorityDynamicParameters dynamicParameters,
			String request,
			IState state, boolean delegata,
			IDSoggetto idDominio, IDServizio idServizio) throws Exception {
		
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
			password = policyAttributeAuthority.getBasicAuthentication_password();
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
		
		ForwardProxy forwardProxy = null;
		ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
		if(configurazionePdDManager.isForwardProxyEnabled()) {
			try {
				IDGenericProperties policy = new IDGenericProperties();
				policy.setTipologia(CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY);
				policy.setNome(policyAttributeAuthority.getName());
				if(delegata) {
					forwardProxy = configurazionePdDManager.getForwardProxyConfigFruizione(idDominio, idServizio, policy);
				}
				else {
					forwardProxy = configurazionePdDManager.getForwardProxyConfigErogazione(idDominio, idServizio, policy);
				}
			}catch(Exception e) {
				throw new Exception("Configurazione errata per la funzionalità govway-proxy; "+e.getMessage(),e);
			}
		}
		if(forwardProxy!=null && forwardProxy.isEnabled() && forwardProxy.getConfigToken()!=null && forwardProxy.getConfigToken().isAttributeAuthorityEnabled()) {
			connettoreMsg.setForwardProxy(forwardProxy);
		}
		
		connettore.setForceDisable_rest_proxyPassReverse(true);
		connettore.init(dynamicParameters.getPddContext(), protocolFactory);
		connettore.setRegisterSendIntoContext(false);
		
		if(basic){
			InvocazioneCredenziali credenziali = new InvocazioneCredenziali();
			credenziali.setUser(username);
			credenziali.setPassword(password);
			connettoreMsg.setCredenziali(credenziali);
		}
		
		connettoreMsg.setConnectorProperties(new java.util.HashMap<String,String>());
		connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_LOCATION, endpoint);
		OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
		boolean debug = properties.isGestioneAttributeAuthority_debug();	
		if(debug) {
			connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_DEBUG, true+"");
		}
		connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_HTTP_DATA_TRANSFER_MODE, TransferLengthModes.CONTENT_LENGTH.getNome());
		addProperties(connettoreMsg, endpointConfig);
		if(https) {
			addProperties(connettoreMsg, sslConfig);
			if(httpsClient) {
				addProperties(connettoreMsg, sslClientConfig);
			}
		}
		
		byte[] content = null;
		
		TransportRequestContext transportRequestContext = new TransportRequestContext(log);
		transportRequestContext.setRequestType(httpMethod.name());
		transportRequestContext.setHeaders(new HashMap<String, List<String>>());
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
			requestPayload = signAAJwt(policyAttributeAuthority, request, contentType);
		}
		if(policyAttributeAuthority.isRequestPositionBearer()) {
			if(transportRequestContext.getHeaders()==null) {
				transportRequestContext.setHeaders(new HashMap<String, List<String>>());
			}
			String authorizationHeader = HttpConstants.AUTHORIZATION_PREFIX_BEARER+requestPayload;
			TransportUtils.setHeader(transportRequestContext.getHeaders(),HttpConstants.AUTHORIZATION, authorizationHeader);
		}
		else if(policyAttributeAuthority.isRequestPositionHeader()) {
			if(transportRequestContext.getHeaders()==null) {
				transportRequestContext.setHeaders(new HashMap<String, List<String>>());
			}
			String headerName = policyAttributeAuthority.getRequestPositionHeaderName();
			TransportUtils.setHeader(transportRequestContext.getHeaders(),headerName, requestPayload);
		}
		else if(policyAttributeAuthority.isRequestPositionQuery()) {
			if(transportRequestContext.getParameters()==null) {
				transportRequestContext.setParameters(new HashMap<String, List<String>>());
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
		
		ResponseCachingConfigurazione responseCachingConfigurazione = new ResponseCachingConfigurazione();
		responseCachingConfigurazione.setStato(StatoFunzionalita.DISABILITATO);
		String prefixConnettore = "[EndpointAttributeAuthority: "+endpoint+"] ";
		if(endpointConfig.containsKey(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME)) {
			String hostProxy = endpointConfig.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME);
			String portProxy = endpointConfig.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT);
			prefixConnettore = prefixConnettore+" [via Proxy: "+hostProxy+":"+portProxy+"] ";
		}
		boolean send = connettore.send(responseCachingConfigurazione, connettoreMsg);
		if(send==false) {
			if(connettore.getEccezioneProcessamento()!=null) {
				throw new Exception(prefixConnettore+connettore.getErrore(), connettore.getEccezioneProcessamento());
			}
			else {
				throw new Exception(prefixConnettore+connettore.getErrore());
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
			String msgSuccess = prefixConnettore+"Connessione completata con successo (codice trasporto: "+connettore.getCodiceTrasporto()+")";
			if(bout!=null && bout.size()>0) {
				log.debug(msgSuccess);
				httpResponse.setContent(bout.toByteArray());
				return httpResponse;
			}
			else {
				throw new Exception(msgSuccess+"; non è pervenuta alcuna risposta");
			}
		}
		else {
			String msgError = prefixConnettore+"Connessione terminata con errore (codice trasporto: "+connettore.getCodiceTrasporto()+")";
			if(bout!=null && bout.size()>0) {
				log.debug(msgError+": "+bout.toString());
				httpResponse.setContent(bout.toByteArray());
				return httpResponse;
			}
			else {
				log.error(msgError);
				throw new Exception(msgError);
			}
		}
		
		
	}
	
	private static String signAAJwt(PolicyAttributeAuthority policyAttributeAuthority, String payload, String contentType) throws Exception {
		
		String signAlgo = policyAttributeAuthority.getRequestJwtSignAlgorithm();
		if(signAlgo==null) {
			throw new Exception("SignAlgorithm undefined");
		}
		
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
		
		String keystoreType = policyAttributeAuthority.getRequestJwtSignKeystoreType();
		if(keystoreType==null) {
			throw new Exception("JWT Signature keystore type undefined");
		}
		String keystoreFile = policyAttributeAuthority.getRequestJwtSignKeystoreFile();
		if(keystoreFile==null) {
			throw new Exception("JWT Signature keystore file undefined");
		}
		String keystorePassword = policyAttributeAuthority.getRequestJwtSignKeystorePassword();
		if(keystorePassword==null) {
			throw new Exception("JWT Signature keystore password undefined");
		}
		String keyAlias = policyAttributeAuthority.getRequestJwtSignKeyAlias();
		if(keyAlias==null) {
			throw new Exception("JWT Signature key alias undefined");
		}
		String keyPassword = policyAttributeAuthority.getRequestJwtSignKeyPassword();
		if(keyPassword==null) {
			throw new Exception("JWT Signature key password undefined");
		}
				
		KeyStore ks = null;
		JWKSetStore jwtStore = null;
		if("jwk".equalsIgnoreCase(keystoreType)) {
			jwtStore = GestoreKeystoreCache.getJwkSetStore(keystoreFile);
		}
		else {
			MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(keystoreFile, keystoreType, keystorePassword);
			if(merlinKs==null) {
				throw new Exception("Accesso al keystore '"+keystoreFile+"' non riuscito");
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
			jwtHeaders.setX509Url(new URI(url));
		}
		if(policyAttributeAuthority.isRequestJwtSignIncludeX509CertSha1()) {
			jwtHeaders.setX509IncludeCertSha1(true);
		}
		if(policyAttributeAuthority.isRequestJwtSignIncludeX509CertSha256()) {
			jwtHeaders.setX509IncludeCertSha256(true);
		}
		if(policyAttributeAuthority.isRequestJwtSignJoseContentType()) {
			if(contentType!=null && !"".equals(contentType)) {
				jwtHeaders.setContentType(contentType);
			}
		}
		String type = policyAttributeAuthority.getRequestJwtSignJoseType();
		if(type!=null && !"".equals(type) && !Costanti.POLICY_RETRIEVE_TOKEN_JWT_CLAIM_UNDEFINED.equals(type)) { // funzionalita' undefined undocumented
			jwtHeaders.setType(type);
		}
		if(ks!=null) {
			Certificate cert = ks.getCertificate(keyAlias);
			if(cert!=null && cert instanceof X509Certificate) {
				jwtHeaders.addX509cert((X509Certificate)cert);
			}
		}
		
		JsonSignature jsonSignature = null;
		if(jwtStore!=null) {
			jsonSignature = new JsonSignature(jwtStore.getJwkSet().getJsonWebKeys(), false, keyAlias, signAlgo, jwtHeaders, options);
		}
		else {
			jsonSignature = new JsonSignature(ks, false, keyAlias,  keyPassword, signAlgo, jwtHeaders, options);
		}
		
		return jsonSignature.sign(payload);
		
	}
}

