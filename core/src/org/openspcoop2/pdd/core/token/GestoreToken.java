/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.utils.WWWAuthenticateErrorCode;
import org.openspcoop2.message.utils.WWWAuthenticateGenerator;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreBaseHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.dynamic.DynamicMapBuilderUtils;
import org.openspcoop2.pdd.core.token.attribute_authority.AttributeAuthorityDynamicParameters;
import org.openspcoop2.pdd.core.token.attribute_authority.AttributeAuthorityProvider;
import org.openspcoop2.pdd.core.token.attribute_authority.EsitoRecuperoAttributi;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.core.token.attribute_authority.PolicyAttributeAuthority;
import org.openspcoop2.pdd.core.token.attribute_authority.pa.DatiInvocazionePortaApplicativa;
import org.openspcoop2.pdd.core.token.attribute_authority.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.pdd.core.token.pa.EsitoPresenzaTokenPortaApplicativa;
import org.openspcoop2.pdd.core.token.pd.EsitoPresenzaTokenPortaDelegata;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest;
import org.openspcoop2.protocol.engine.SecurityTokenUtilities;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.security.message.jose.JOSEUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.cache.CacheType;
import org.openspcoop2.utils.cache.Constants;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**     
 * GestoreToken
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreToken {
	
	private GestoreToken() {}

	public static final boolean PORTA_DELEGATA = true;
	public static final boolean PORTA_APPLICATIVA = false;
	
	/** Chiave della cache per la gestione dei token  */
	private static final String GESTIONE_TOKEN_CACHE_NAME = "token";
	/** Chiave della cache per la gestione dei dati raccolti via attribute authority  */
	private static final String ATTRIBUTE_AUTHORITY_CACHE_NAME = "attributeAuthority";
	/** Cache */
	private static Cache cacheGestioneToken = null;
	private static Cache cacheAttributeAuthority = null;
	
	private static final Map<String, org.openspcoop2.utils.Semaphore> _lockDynamicDiscovery = new HashMap<>();
	private static synchronized org.openspcoop2.utils.Semaphore initLockDynamicDiscovery(String nomePolicy){
		org.openspcoop2.utils.Semaphore s = _lockDynamicDiscovery.get(nomePolicy);
		if(s==null) {
			Integer permits = OpenSPCoop2Properties.getInstance().getGestioneTokenDynamicDiscoveryLockPermits();
			if(permits!=null && permits.intValue()>1) {
				s = new org.openspcoop2.utils.Semaphore("GestoreTokenDynamicDiscovery_"+nomePolicy, permits);
			}
			else {
				s = new org.openspcoop2.utils.Semaphore("GestoreTokenDynamicDiscovery_"+nomePolicy);
			}
			_lockDynamicDiscovery.put(nomePolicy, s);
		}
		return s;
	}
	private static org.openspcoop2.utils.Semaphore getLockDynamicDiscovery(String nomePolicy){
		org.openspcoop2.utils.Semaphore s = _lockDynamicDiscovery.get(nomePolicy);
		if(s==null) {
			s = initLockDynamicDiscovery(nomePolicy);
		}
		return s;
	} 
	
	private static final Map<String, org.openspcoop2.utils.Semaphore> _lockJWT = new HashMap<>();
	private static synchronized org.openspcoop2.utils.Semaphore initLockJWT(String nomePolicy){
		org.openspcoop2.utils.Semaphore s = _lockJWT.get(nomePolicy);
		if(s==null) {
			Integer permits = OpenSPCoop2Properties.getInstance().getGestioneTokenValidazioneJWTLockPermits();
			if(permits!=null && permits.intValue()>1) {
				s = new org.openspcoop2.utils.Semaphore("GestoreTokenValidazioneJWT_"+nomePolicy, permits);
			}
			else {
				s = new org.openspcoop2.utils.Semaphore("GestoreTokenValidazioneJWT_"+nomePolicy);
			}
			_lockJWT.put(nomePolicy, s);
		}
		return s;
	}
	private static org.openspcoop2.utils.Semaphore getLockJWT(String nomePolicy){
		org.openspcoop2.utils.Semaphore s = _lockJWT.get(nomePolicy);
		if(s==null) {
			s = initLockJWT(nomePolicy);
		}
		return s;
	}
	
	private static final Map<String, org.openspcoop2.utils.Semaphore> _lockIntrospection = new HashMap<>();
	private static synchronized org.openspcoop2.utils.Semaphore initLockIntrospection(String nomePolicy){
		org.openspcoop2.utils.Semaphore s = _lockIntrospection.get(nomePolicy);
		if(s==null) {
			Integer permits = OpenSPCoop2Properties.getInstance().getGestioneTokenIntrospectionLockPermits();
			if(permits!=null && permits.intValue()>1) {
				s = new org.openspcoop2.utils.Semaphore("GestoreTokenIntrospection_"+nomePolicy, permits);
			}
			else {
				s = new org.openspcoop2.utils.Semaphore("GestoreTokenIntrospection_"+nomePolicy);
			}
			_lockIntrospection.put(nomePolicy, s);
		}
		return s;
	}
	private static org.openspcoop2.utils.Semaphore getLockIntrospection(String nomePolicy){
		org.openspcoop2.utils.Semaphore s = _lockIntrospection.get(nomePolicy);
		if(s==null) {
			s = initLockIntrospection(nomePolicy);
		}
		return s;
	} 
	
	private static final Map<String, org.openspcoop2.utils.Semaphore> _lockUserInfo = new HashMap<>();
	private static synchronized org.openspcoop2.utils.Semaphore initLockUserInfo(String nomePolicy){
		org.openspcoop2.utils.Semaphore s = _lockUserInfo.get(nomePolicy);
		if(s==null) {
			Integer permits = OpenSPCoop2Properties.getInstance().getGestioneTokenUserInfoLockPermits();
			if(permits!=null && permits.intValue()>1) {
				s = new org.openspcoop2.utils.Semaphore("GestoreTokenUserInfo_"+nomePolicy, permits);
			}
			else {
				s = new org.openspcoop2.utils.Semaphore("GestoreTokenUserInfo_"+nomePolicy);
			}
			_lockUserInfo.put(nomePolicy, s);
		}
		return s;
	}
	private static org.openspcoop2.utils.Semaphore getLockUserInfo(String nomePolicy){
		org.openspcoop2.utils.Semaphore s = _lockUserInfo.get(nomePolicy);
		if(s==null) {
			s = initLockUserInfo(nomePolicy);
		}
		return s;
	} 
	
	private static final Map<String, org.openspcoop2.utils.Semaphore> _lockNegoziazione = new HashMap<>();
	private static synchronized org.openspcoop2.utils.Semaphore initLockNegoziazione(String nomePolicy){
		org.openspcoop2.utils.Semaphore s = _lockNegoziazione.get(nomePolicy);
		if(s==null) {
			Integer permits = OpenSPCoop2Properties.getInstance().getGestioneRetrieveTokenLockPermits();
			if(permits!=null && permits.intValue()>1) {
				s = new org.openspcoop2.utils.Semaphore("GestoreTokenNegoziazione_"+nomePolicy, permits);
			}
			else {
				s = new org.openspcoop2.utils.Semaphore("GestoreTokenNegoziazione_"+nomePolicy);
			}
			_lockNegoziazione.put(nomePolicy, s);
		}
		return s;
	}
	private static org.openspcoop2.utils.Semaphore getLockNegoziazione(String nomePolicy){
		org.openspcoop2.utils.Semaphore s = _lockNegoziazione.get(nomePolicy);
		if(s==null) {
			s = initLockNegoziazione(nomePolicy);
		}
		return s;
	} 
	
	private static final Map<String, org.openspcoop2.utils.Semaphore> _lockAttributeAuthority = new HashMap<>();
	private static synchronized org.openspcoop2.utils.Semaphore initLockAttributeAuthority(String nomePolicy){
		org.openspcoop2.utils.Semaphore s = _lockAttributeAuthority.get(nomePolicy);
		if(s==null) {
			Integer permits = OpenSPCoop2Properties.getInstance().getGestioneAttributeAuthorityLockPermits();
			if(permits!=null && permits.intValue()>1) {
				s = new org.openspcoop2.utils.Semaphore("GestoreTokenAttributeAuthority_"+nomePolicy, permits);
			}
			else {
				s = new org.openspcoop2.utils.Semaphore("GestoreTokenAttributeAuthority_"+nomePolicy);
			}
			_lockAttributeAuthority.put(nomePolicy, s);
		}
		return s;
	}
	private static org.openspcoop2.utils.Semaphore getLockAttributeAuthority(String nomePolicy){
		org.openspcoop2.utils.Semaphore s = _lockAttributeAuthority.get(nomePolicy);
		if(s==null) {
			s = initLockAttributeAuthority(nomePolicy);
		}
		return s;
	} 
	
	private static final Map<String, org.openspcoop2.utils.Semaphore> _lockGenericToken = new HashMap<>();
	private static synchronized org.openspcoop2.utils.Semaphore initLockGenericToken(String funzione){
		org.openspcoop2.utils.Semaphore s = _lockGenericToken.get(funzione);
		if(s==null) {
			Integer permits = OpenSPCoop2Properties.getInstance().getGestioneRetrieveTokenLockPermits();
			if(permits!=null && permits.intValue()>1) {
				s = new org.openspcoop2.utils.Semaphore("GestoreGenericTokenCache_"+funzione, permits);
			}
			else {
				s = new org.openspcoop2.utils.Semaphore("GestoreGenericTokenCache_"+funzione);
			}
			_lockGenericToken.put(funzione, s);
		}
		return s;
	}
	private static org.openspcoop2.utils.Semaphore getLockGenericToken(String nomePolicy){
		org.openspcoop2.utils.Semaphore s = _lockGenericToken.get(nomePolicy);
		if(s==null) {
			s = initLockGenericToken(nomePolicy);
		}
		return s;
	} 
	
 
	

	static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	/** Logger log */
	private static Logger logger = null;
	private static Logger logConsole = OpenSPCoop2Logger.getLoggerOpenSPCoopConsole();
	
	private static void loggerDebug(String msg) {
		GestoreToken.logger.debug(msg);
	}
	private static void loggerInfo(String msg) {
		GestoreToken.logger.info(msg);
	}
	private static void loggerError(String msg) {
		GestoreToken.logger.error(msg);
	}
	private static void loggerError(String msg, Exception e) {
		GestoreToken.logger.error(msg,e);
	}
	private static String getMessageObjectInCache(org.openspcoop2.utils.cache.CacheResponse response, String keyCache, String funzione) {
		return "Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.";
	}
	private static String getMessageEccezioneInCache(org.openspcoop2.utils.cache.CacheResponse response, String keyCache, String funzione) {
		return "Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+funzione+") in cache.";
	}
	private static final String MESSAGE_ELEMENT_CACHE_NE_OGGETTO_NE_ECCEZIONE = "In cache non è presente ne un oggetto ne un'eccezione.";
	
	static final String TOKEN_NON_VALIDO = "Token non valido";
	static String getMessageTokenNonValido(Exception e) {
		return TOKEN_NON_VALIDO+": "+e.getMessage();
	}
	
	static String getMessageViaProxy(String hostProxy, String portProxy) {
		return " [via Proxy: "+hostProxy+":"+portProxy+"] ";
	}
	
	static String getMessageErroreGovWayProxy(Exception e) {
		return "Configurazione errata per la funzionalità govway-proxy; "+e.getMessage();
	}
	
	static String getMessageConnettoreConnessioneSuccesso(ConnettoreBaseHTTP connettore) {
		return "Connessione completata con successo (codice trasporto: "+connettore.getCodiceTrasporto()+")";
	}
	static String getMessageConnettoreConnessioneErrore(ConnettoreBaseHTTP connettore) {
		return "Connessione terminata con errore (codice trasporto: "+connettore.getCodiceTrasporto()+")";
	}
	static final String CONNETTORE_RISPOSTA_NON_PERVENUTA = "; non è pervenuta alcuna risposta";
	
	public static final String KEY_ALIAS_UNDEFINED = "JWT Signature key alias undefined";
	public static final String KEY_PASSWORD_UNDEFINED = "JWT Signature key password undefined";
	public static final String KEYSTORE_TYPE_UNDEFINED = "JWT Signature keystore type undefined";
	public static final String KEYSTORE_KEYPAIR_UNDEFINED = "JWT Signature keyPair undefined";
	public static final String KEYSTORE_KEYSTORE_UNDEFINED = "JWT Signature keystore undefined";
	public static final String KEYSTORE_KEYSTORE_FILE_UNDEFINED = "JWT Signature keystore file undefined";
	public static final String KEYSTORE_KEYSTORE_PASSWORD_UNDEFINED = "JWT Signature keystore password undefined";
	public static final String KEYSTORE_PRIVATE_KEY_UNDEFINED = "JWT Signature private key file undefined";
	public static final String KEYSTORE_PUBLIC_KEY_UNDEFINED = "JWT Signature public key file undefined";
	public static final String KEYSTORE_KEY_PAIR_ALGORITHM_UNDEFINED = "JWT Signature key pair algorithm undefined";
	
	/** Connettore Utils */
	static void addProperties(ConnettoreMsg connettoreMsg, Properties p) {
		if(p!=null && p.size()>0) {
			Enumeration<?> en = p.propertyNames();
			while (en.hasMoreElements()) {
				Object oKey = en.nextElement();
				if(oKey!=null) {
					String key = (String) oKey;
					String value = p.getProperty(key);
					connettoreMsg.getConnectorProperties().put(key,value);
				}
			}
		}
	}
	
	
	/* --------------- Cache --------------------*/
	private static String getPrefixOggettoConChiave(String key) {
		 return "oggetto con chiave ["+key+"]";
	}
	private static String getSuffixEseguiOperazione(String funzione) {
		return " (method:"+funzione+") eseguo operazione...";
	}
	private static String getMessaggioAggiuntaOggettoInCache(String keyCache) {
		return "Aggiungo oggetto ["+keyCache+"] in cache";
	}
	private static String getMessaggioErroreInserimentoInCache(String keyCache, Exception e) {
		return "Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage();
	}
	private static String getMessaggioErroreValoreEsitoNull(String funzione) {
		return "Metodo (GestoreToken."+funzione+") ha ritornato un valore di esito null";
	}
	
	public static void resetGestioneTokenCache() throws TokenException{
		if(GestoreToken.cacheGestioneToken!=null){
			try{
				GestoreToken.cacheGestioneToken.clear();
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	public static void resetAttributeAuthorityCache() throws TokenException{
		if(GestoreToken.cacheAttributeAuthority!=null){
			try{
				GestoreToken.cacheAttributeAuthority.clear();
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	
	public static String printStatsGestioneTokenCache(String separator) throws TokenException{
		try{
			if(GestoreToken.cacheGestioneToken!=null){
				return GestoreToken.cacheGestioneToken.printStats(separator);
			}
			else{
				throw new TokenException(Constants.MSG_CACHE_NON_ABILITATA);
			}
		}catch(Exception e){
			throw new TokenException("Visualizzazione Statistiche riguardante la cache sulla gestione dei token non riuscita: "+e.getMessage(),e);
		}
	}
	public static String printStatsAttributeAuthorityCache(String separator) throws TokenException{
		try{
			if(GestoreToken.cacheAttributeAuthority!=null){
				return GestoreToken.cacheAttributeAuthority.printStats(separator);
			}
			else{
				throw new TokenException(Constants.MSG_CACHE_NON_ABILITATA);
			}
		}catch(Exception e){
			throw new TokenException("Visualizzazione Statistiche riguardante la cache sui dati delle Attribute Authority non riuscita: "+e.getMessage(),e);
		}
	}
	
	public static void abilitaGestioneTokenCache() throws TokenException{
		if(GestoreToken.cacheGestioneToken!=null)
			throw new TokenException(Constants.MSG_CACHE_GIA_ABILITATA);
		else{
			abilitaGestioneTokenCacheEngine();
		}
	}
	public static void abilitaAttributeAuthorityCache() throws TokenException{
		if(GestoreToken.cacheAttributeAuthority!=null)
			throw new TokenException(Constants.MSG_CACHE_GIA_ABILITATA);
		else{
			abilitaAttributeAuthorityCacheEngine();
		}
	}
	
	private static synchronized void abilitaGestioneTokenCacheEngine() throws TokenException{
		if(GestoreToken.cacheGestioneToken==null) {
			try{
				GestoreToken.cacheGestioneToken = new Cache(CacheType.JCS, GestoreToken.GESTIONE_TOKEN_CACHE_NAME); // lascio JCS come default abilitato via jmx
				GestoreToken.cacheGestioneToken.build();
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	private static synchronized void abilitaAttributeAuthorityCacheEngine() throws TokenException{
		if(GestoreToken.cacheAttributeAuthority==null) {
			try{
				GestoreToken.cacheAttributeAuthority = new Cache(CacheType.JCS, GestoreToken.ATTRIBUTE_AUTHORITY_CACHE_NAME); // lascio JCS come default abilitato via jmx
				GestoreToken.cacheAttributeAuthority.build();
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	
	public static void abilitaGestioneTokenCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond) throws TokenException{
		if(GestoreToken.cacheGestioneToken!=null)
			throw new TokenException(Constants.MSG_CACHE_GIA_ABILITATA);
		else{
			try{
				setGestioneTokenCache(dimensioneCache, algoritmoCacheLRU, itemIdleTime, itemLifeSecond);
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	public static void abilitaAttributeAuthorityCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond) throws TokenException{
		if(GestoreToken.cacheAttributeAuthority!=null)
			throw new TokenException(Constants.MSG_CACHE_GIA_ABILITATA);
		else{
			try{
				setAttributeAuthorityCache(dimensioneCache, algoritmoCacheLRU, itemIdleTime, itemLifeSecond);
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	
	private static void setGestioneTokenCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond) throws TokenException, UtilsException{
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
		
		GestoreToken.initGestioneTokenCacheToken(CacheType.JCS, dimensioneCacheInt, algoritmoCache, itemIdleTimeLong, itemLifeSecondLong, null); // lascio JCS come default abilitato via jmx
	}
	private static void setAttributeAuthorityCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond) throws TokenException, UtilsException{
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
		
		GestoreToken.initAttributeAuthorityCacheToken(CacheType.JCS, dimensioneCacheInt, algoritmoCache, itemIdleTimeLong, itemLifeSecondLong, null); // lascio JCS come default abilitato via jmx
	}
	
	public static void disabilitaGestioneTokenCache() throws TokenException{
		if(GestoreToken.cacheGestioneToken==null)
			throw new TokenException(Constants.MSG_CACHE_GIA_DISABILITATA);
		else{
			disabilitaGestioneTokenCacheEngine();
		}
	}
	public static void disabilitaAttributeAuthorityCache() throws TokenException{
		if(GestoreToken.cacheAttributeAuthority==null)
			throw new TokenException(Constants.MSG_CACHE_GIA_DISABILITATA);
		else{
			disabilitaAttributeAuthorityCacheEngine();
		}
	}
	
	private static synchronized void disabilitaGestioneTokenCacheEngine() throws TokenException{
		if(GestoreToken.cacheGestioneToken!=null) {
			try{
				GestoreToken.cacheGestioneToken.clear();
				GestoreToken.cacheGestioneToken = null;
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	private static synchronized void disabilitaAttributeAuthorityCacheEngine() throws TokenException{
		if(GestoreToken.cacheAttributeAuthority!=null) {
			try{
				GestoreToken.cacheAttributeAuthority.clear();
				GestoreToken.cacheAttributeAuthority = null;
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}
	}
	
	public static boolean isGestioneTokenCacheAbilitata(){
		return GestoreToken.cacheGestioneToken != null;
	}
	public static boolean isAttributeAuthorityCacheAbilitata(){
		return GestoreToken.cacheAttributeAuthority != null;
	}
	
	public static String listKeysGestioneTokenCache(String separator) throws TokenException{
		if(GestoreToken.cacheGestioneToken!=null){
			try{
				return GestoreToken.cacheGestioneToken.printKeys(separator);
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException(Constants.MSG_CACHE_NON_ABILITATA);
		}
	}
	public static String listKeysAttributeAuthorityCache(String separator) throws TokenException{
		if(GestoreToken.cacheAttributeAuthority!=null){
			try{
				return GestoreToken.cacheAttributeAuthority.printKeys(separator);
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException(Constants.MSG_CACHE_NON_ABILITATA);
		}
	}
	
	public static List<String> keysGestioneTokenCache() throws TokenException{
		if(GestoreToken.cacheGestioneToken!=null){
			try{
				return GestoreToken.cacheGestioneToken.keys();
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException(Constants.MSG_CACHE_NON_ABILITATA);
		}
	}
	public static List<String> keysAttributeAuthorityCache() throws TokenException{
		if(GestoreToken.cacheAttributeAuthority!=null){
			try{
				return GestoreToken.cacheAttributeAuthority.keys();
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException(Constants.MSG_CACHE_NON_ABILITATA);
		}
	}
	
	public static String getObjectGestioneTokenCache(String key) throws TokenException{
		if(GestoreToken.cacheGestioneToken!=null){
			try{
				Object o = GestoreToken.cacheGestioneToken.get(key);
				if(o!=null){
					return o.toString();
				}else{
					return getPrefixOggettoConChiave(key)+" non presente";
				}
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException(Constants.MSG_CACHE_NON_ABILITATA);
		}
	}
	public static String getObjectAttributeAuthorityCache(String key) throws TokenException{
		if(GestoreToken.cacheAttributeAuthority!=null){
			try{
				Object o = GestoreToken.cacheAttributeAuthority.get(key);
				if(o!=null){
					return o.toString();
				}else{
					return getPrefixOggettoConChiave(key)+" non presente";
				}
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException(Constants.MSG_CACHE_NON_ABILITATA);
		}
	}
	
	public static void removeObjectGestioneTokenCache(String key) throws TokenException{
		if(GestoreToken.cacheGestioneToken!=null){
			try{
				GestoreToken.cacheGestioneToken.remove(key);
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException(Constants.MSG_CACHE_NON_ABILITATA);
		}
	}
	public static void removeObjectAttributeAuthorityCache(String key) throws TokenException{
		if(GestoreToken.cacheAttributeAuthority!=null){
			try{
				GestoreToken.cacheAttributeAuthority.remove(key);
			}catch(Exception e){
				throw new TokenException(e.getMessage(),e);
			}
		}else{
			throw new TokenException(Constants.MSG_CACHE_NON_ABILITATA);
		}
	}
	
	
	
	
	
	/*----------------- CLEANER --------------------*/
	
	public static void removeGenericPropertiesGestioneToken(IDGenericProperties idGP) throws TokenException {
		
		if(GestoreToken.isGestioneTokenCacheAbilitata()){
			
			String prefixKeyValidazioneJwt = null;
			String prefixKeyIntrospection = null;
			String prefixKeyUserInfo = null;
			
			String prefixKeyRetrieveToken = null;
			
			boolean checkKeys = false;
			
			if(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION.equals(idGP.getTipologia())) {
				
				prefixKeyValidazioneJwt = GestoreTokenValidazioneUtilities.buildPrefixCacheKeyValidazione(idGP.getNome(), VALIDAZIONE_JWT_FUNCTION);
				prefixKeyIntrospection = GestoreTokenValidazioneUtilities.buildPrefixCacheKeyValidazione(idGP.getNome(), INTROSPECTION_FUNCTION);
				prefixKeyUserInfo = GestoreTokenValidazioneUtilities.buildPrefixCacheKeyValidazione(idGP.getNome(), USERINFO_FUNCTION);
				
				checkKeys = true;
			}
			else if(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE.equals(idGP.getTipologia())) {
				
				prefixKeyRetrieveToken = GestoreTokenNegoziazioneUtilities.buildPrefixCacheKeyNegoziazione(idGP.getNome(), RETRIEVE_FUNCTION);
				
				checkKeys = true;
				
			}
			
			if(checkKeys) {
				checkKeys(prefixKeyValidazioneJwt, prefixKeyIntrospection, prefixKeyUserInfo,
						prefixKeyRetrieveToken);
			}
				
		}
		
	}
	public static void removeGenericPropertiesAttributeAuthority(IDGenericProperties idGP) throws TokenException {
		
		if(GestoreToken.isAttributeAuthorityCacheAbilitata()){
						
			String prefixKeyAA = null;
			
			boolean checkKeys = false;
			
			if(CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY.equals(idGP.getTipologia())) {
				
				prefixKeyAA = GestoreTokenAttributeAuthorityUtilities.buildCacheKeyRecuperoAttributiPrefix(idGP.getNome(), ATTRIBUTE_AUTHORITY_FUNCTION);
				
				checkKeys = true;
			}
			
			if(checkKeys) {
				checkKeys(prefixKeyAA);
			}
				
		}
		
	}
	private static void checkKeys(String prefixKeyValidazioneJwt, String prefixKeyIntrospection, String prefixKeyUserInfo,
			String prefixKeyRetrieveToken) throws TokenException {
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreToken.keysGestioneTokenCache();
		if(keys!=null && !keys.isEmpty()) {
			fillKeyForClean(prefixKeyValidazioneJwt, prefixKeyIntrospection, prefixKeyUserInfo,
					prefixKeyRetrieveToken, null,
					keys, keyForClean);
		}
		if(!keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				removeObjectGestioneTokenCache(key);
			}
		}
	}
	private static void checkKeys(String prefixKeyAA) throws TokenException {
		List<String> keyForClean = new ArrayList<>();
		List<String> keys = GestoreToken.keysAttributeAuthorityCache();
		if(keys!=null && !keys.isEmpty()) {
			fillKeyForClean(null, null, null,
					null, prefixKeyAA,
					keys, keyForClean);
		}
		if(!keyForClean.isEmpty()) {
			for (String key : keyForClean) {
				removeObjectAttributeAuthorityCache(key);
			}
		}
	}
	private static void fillKeyForClean(String prefixKeyValidazioneJwt, String prefixKeyIntrospection, String prefixKeyUserInfo,
			String prefixKeyRetrieveToken, String prefixKeyAA,
			List<String> keys, List<String> keyForClean) {
		for (String key : keys) {
			if(key!=null &&
				(
						(prefixKeyValidazioneJwt!=null && key.startsWith(prefixKeyValidazioneJwt))
						||
						(prefixKeyIntrospection!=null && key.startsWith(prefixKeyIntrospection))
						||
						(prefixKeyUserInfo!=null && key.startsWith(prefixKeyUserInfo))
						||
						(prefixKeyRetrieveToken!=null && key.startsWith(prefixKeyRetrieveToken))
						||
						(prefixKeyAA!=null && key.startsWith(prefixKeyAA))
					)
				){
				keyForClean.add(key);
			}
		}
	}
	
	
	
	


	/*----------------- INIZIALIZZAZIONE --------------------*/
	public static void initializeGestioneToken(Logger log) throws TokenException, UtilsException{
		GestoreToken.initializeGestioneToken(null, false, -1,null,-1l,-1l, log);
	}
	public static void initializeGestioneToken(CacheType cacheType,
			int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws TokenException, UtilsException{
		GestoreToken.initializeGestioneToken(cacheType, true, dimensioneCache,algoritmoCache,idleTime,itemLifeSecond, log);
	}
	private static void initializeGestioneToken(CacheType cacheType,
			boolean cacheAbilitata,int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws TokenException, UtilsException{

		// Inizializzo log
		GestoreToken.logger = log;
				
		// Inizializzazione Cache
		if(cacheAbilitata){
			GestoreToken.initGestioneTokenCacheToken(cacheType, dimensioneCache, algoritmoCache, idleTime, itemLifeSecond, log);
		}

	}
	
	public static void initializeAttributeAuthority(Logger log) throws TokenException, UtilsException{
		GestoreToken.initializeAttributeAuthority(null, false, -1,null,-1l,-1l, log);
	}
	public static void initializeAttributeAuthority(CacheType cacheType,
			int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws TokenException, UtilsException{
		GestoreToken.initializeAttributeAuthority(cacheType, true, dimensioneCache,algoritmoCache,idleTime,itemLifeSecond, log);
	}
	private static void initializeAttributeAuthority(CacheType cacheType,
			boolean cacheAbilitata,int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws TokenException, UtilsException{

		// Inizializzo log
		GestoreToken.logger = log;
				
		// Inizializzazione Cache
		if(cacheAbilitata){
			GestoreToken.initAttributeAuthorityCacheToken(cacheType, dimensioneCache, algoritmoCache, idleTime, itemLifeSecond, log);
		}

	}


	public static void initGestioneTokenCacheToken(CacheType cacheType, int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws TokenException, UtilsException {
		
		if(log!=null)
			log.info("Inizializzazione cache GestioneToken");

		GestoreToken.cacheGestioneToken = new Cache(cacheType, GestoreToken.GESTIONE_TOKEN_CACHE_NAME);

		if( (dimensioneCache>0) ||
				(algoritmoCache != null) ){

			initGestioneTokenCacheSize(dimensioneCache, algoritmoCache, log);

		}

		if( idleTime > 0  ){
			try{
				String msg = "Attributo 'IdleTime' (Token) impostato al valore: "+idleTime;
				if(log!=null)
					log.info(msg);
				GestoreToken.logConsole.info(msg);
				GestoreToken.cacheGestioneToken.setItemIdleTime(idleTime);
			}catch(Exception error){
				throw new TokenException("Parametro errato per l'attributo 'IdleTime' (Token): "+error.getMessage(),error);
			}
		}
		try{
			String msg = "Attributo 'MaxLifeSecond' (Token) impostato al valore: "+itemLifeSecond;
			if(log!=null)
				log.info(msg);
			GestoreToken.logConsole.info(msg);
			GestoreToken.cacheGestioneToken.setItemLifeTime(itemLifeSecond);
		}catch(Exception error){
			throw new TokenException("Parametro errato per l'attributo 'MaxLifeSecond' (Token): "+error.getMessage(),error);
		}

		GestoreToken.cacheGestioneToken.build();
	}
	public static void initAttributeAuthorityCacheToken(CacheType cacheType, int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws TokenException, UtilsException {
		
		if(log!=null)
			log.info("Inizializzazione cache Token");

		GestoreToken.cacheAttributeAuthority = new Cache(cacheType, GestoreToken.ATTRIBUTE_AUTHORITY_CACHE_NAME);

		if( (dimensioneCache>0) ||
				(algoritmoCache != null) ){

			initCacheSize(dimensioneCache, algoritmoCache, log);

		}

		if( idleTime > 0  ){
			try{
				String msg = "Attributo 'IdleTime' (AttributeAuthority) impostato al valore: "+idleTime;
				if(log!=null)
					log.info(msg);
				GestoreToken.logConsole.info(msg);
				GestoreToken.cacheAttributeAuthority.setItemIdleTime(idleTime);
			}catch(Exception error){
				throw new TokenException("Parametro errato per l'attributo 'IdleTime' (AttributeAuthority): "+error.getMessage(),error);
			}
		}
		try{
			String msg = "Attributo 'MaxLifeSecond' (AttributeAuthority) impostato al valore: "+itemLifeSecond;
			if(log!=null)
				log.info(msg);
			GestoreToken.logConsole.info(msg);
			GestoreToken.cacheAttributeAuthority.setItemLifeTime(itemLifeSecond);
		}catch(Exception error){
			throw new TokenException("Parametro errato per l'attributo 'MaxLifeSecond' (AttributeAuthority): "+error.getMessage(),error);
		}

		GestoreToken.cacheAttributeAuthority.build();
	}
	
	public static void initGestioneTokenCacheSize(int dimensioneCache,String algoritmoCache, Logger log) throws TokenException {
		if( dimensioneCache>0 ){
			try{
				String msg = "Dimensione della cache (Token) impostata al valore: "+dimensioneCache;
				if(log!=null)
					log.info(msg);
				GestoreToken.logConsole.info(msg);
				GestoreToken.cacheGestioneToken.setCacheSize(dimensioneCache);
			}catch(Exception error){
				throw new TokenException("Parametro errato per la dimensione della cache (Token): "+error.getMessage(),error);
			}
		}
		if(algoritmoCache != null ){
			String msg = "Algoritmo di cache (Token) impostato al valore: "+algoritmoCache;
			if(log!=null)
				log.info(msg);
			GestoreToken.logConsole.info(msg);
			if(CostantiConfigurazione.CACHE_MRU.toString().equalsIgnoreCase(algoritmoCache))
				GestoreToken.cacheGestioneToken.setCacheAlgoritm(CacheAlgorithm.MRU);
			else
				GestoreToken.cacheGestioneToken.setCacheAlgoritm(CacheAlgorithm.LRU);
		}
	}
	public static void initCacheSize(int dimensioneCache,String algoritmoCache, Logger log) throws TokenException {
		if( dimensioneCache>0 ){
			try{
				String msg = "Dimensione della cache (AttributeAuthority) impostata al valore: "+dimensioneCache;
				if(log!=null)
					log.info(msg);
				GestoreToken.logConsole.info(msg);
				GestoreToken.cacheAttributeAuthority.setCacheSize(dimensioneCache);
			}catch(Exception error){
				throw new TokenException("Parametro errato per la dimensione della cache (AttributeAuthority): "+error.getMessage(),error);
			}
		}
		if(algoritmoCache != null ){
			String msg = "Algoritmo di cache (AttributeAuthority) impostato al valore: "+algoritmoCache;
			if(log!=null)
				log.info(msg);
			GestoreToken.logConsole.info(msg);
			if(CostantiConfigurazione.CACHE_MRU.toString().equalsIgnoreCase(algoritmoCache))
				GestoreToken.cacheAttributeAuthority.setCacheAlgoritm(CacheAlgorithm.MRU);
			else
				GestoreToken.cacheAttributeAuthority.setCacheAlgoritm(CacheAlgorithm.LRU);
		}
	}
	
	

	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] VALIDAZIONE CONFIGURAZIONE ****************** */
	
	public static void validazioneConfigurazione(AbstractDatiInvocazione datiInvocazione) throws ProviderException, ProviderValidationException {
		TokenProvider p = new TokenProvider();
		p.validate(datiInvocazione.getPolicyGestioneToken().getProperties());
	}
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] VERIFICA POSIZIONE TOKEN ****************** */
	
	private static final String INFO_TRASPORTO_NON_PRESENTI = "Informazioni di trasporto non presenti";
	private static final String CONTENENTE_IL_TOKEN = "contenente il token";
	
	public static EsitoPresenzaToken verificaPosizioneToken(AbstractDatiInvocazione datiInvocazione, boolean portaDelegata) {
		
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
    				detailsErrorHeader = INFO_TRASPORTO_NON_PRESENTI;
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
    						detailsErrorHeader = "Non è stato riscontrato l'header http '"+headerName+"' "+CONTENENTE_IL_TOKEN;
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
    				detailsErrorUrl = INFO_TRASPORTO_NON_PRESENTI;
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
						detailsErrorUrl = "Non è stato riscontrata la proprietà della URL '"+propertyUrlName+"' "+CONTENENTE_IL_TOKEN;
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
    				detailsErrorForm = INFO_TRASPORTO_NON_PRESENTI;
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
    						detailsErrorForm = "Non è stato riscontrata la proprietà della Form '"+Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN+"' "+CONTENENTE_IL_TOKEN;
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
    						detailsErrorForm = "Non è stato riscontrata la proprietà della Form '"+Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN+"' "+CONTENENTE_IL_TOKEN;
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
	
	
	
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] DYNAMIC DISCOVERY ****************** */
	
	public static final String DYNAMIC_DISCOVERY_FUNCTION = "DynamicDiscovery";
	
	public static EsitoDynamicDiscovery dynamicDiscovery(Logger log, AbstractDatiInvocazione datiInvocazione, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			EsitoPresenzaToken esitoPresenzaToken, boolean portaDelegata,
			Busta busta, IDSoggetto idDominio, IDServizio idServizio) throws Exception {
		EsitoDynamicDiscovery esitoDynamicDiscovery = null;

		String token = esitoPresenzaToken.getToken();
		
		Cache cache = null;
		if(OpenSPCoop2Properties.getInstance().isGestioneTokenDynamicDiscoveryUseCacheConfig()) {
			cache = ConfigurazionePdDReader.getCache();
		}	
		else {
			cache = GestoreToken.cacheGestioneToken;
		}
		
		if(cache==null){
			esitoDynamicDiscovery = GestoreTokenValidazioneUtilities.dynamicDiscoveryEngine(log, datiInvocazione, 
					pddContext, protocolFactory,
					token, portaDelegata,
					busta, idDominio, idServizio);
		}
    	else{
    		String policyName = datiInvocazione.getPolicyGestioneToken().getName();
    		String endpoint = datiInvocazione.getPolicyGestioneToken().getDynamicDiscoveryEndpoint();
    		String funzione = DYNAMIC_DISCOVERY_FUNCTION;
    		
    		String tokenCache = "";
    		if(OpenSPCoop2Properties.getInstance().isGestioneTokenDynamicDiscoveryKeyCacheUseToken()) {
    			tokenCache = token;
    		}
    		tokenCache=tokenCache+"_endpoint:"+endpoint;
    		
    		String keyCache = GestoreTokenValidazioneUtilities.buildCacheKeyValidazione(policyName, funzione, portaDelegata, tokenCache);

    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
    		
    		org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) cache.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					GestoreToken.loggerDebug(GestoreToken.getMessageObjectInCache(response, keyCache, funzione));
					esitoDynamicDiscovery = (EsitoDynamicDiscovery) response.getObject();
					esitoDynamicDiscovery.setInCache(true);
				}else if(response.getException()!=null){
					GestoreToken.loggerDebug(GestoreToken.getMessageEccezioneInCache(response, keyCache, funzione));
					throw (Exception) response.getException();
				}else{
					GestoreToken.loggerError(MESSAGE_ELEMENT_CACHE_NE_OGGETTO_NE_ECCEZIONE);
				}
			}
    		
			if(esitoDynamicDiscovery==null) {
				
				String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
				org.openspcoop2.utils.Semaphore lockDynamicDiscovery = getLockDynamicDiscovery(policyName);
				lockDynamicDiscovery.acquire("dynamicDiscovery", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) cache.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreToken.loggerDebug(GestoreToken.getMessageObjectInCache(response, keyCache, funzione));
							esitoDynamicDiscovery = (EsitoDynamicDiscovery) response.getObject();
							esitoDynamicDiscovery.setInCache(true);
						}else if(response.getException()!=null){
							GestoreToken.loggerDebug(GestoreToken.getMessageEccezioneInCache(response, keyCache, funzione));
							throw (Exception) response.getException();
						}else{
							GestoreToken.loggerError(MESSAGE_ELEMENT_CACHE_NE_OGGETTO_NE_ECCEZIONE);
						}
					}
	
					if(esitoDynamicDiscovery==null) {
						// Effettuo la query
						GestoreToken.loggerDebug(getPrefixOggettoConChiave(keyCache)+getSuffixEseguiOperazione(funzione));
						esitoDynamicDiscovery = GestoreTokenValidazioneUtilities.dynamicDiscoveryEngine(log, datiInvocazione, 
								pddContext, protocolFactory,
								token, portaDelegata,
								busta, idDominio, idServizio);
							
						// Aggiungo la risposta in cache (se esiste una cache)	
						// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
						// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
						// - impostare il noCache a true
						esitoDynamicDiscovery.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
						if(!esitoDynamicDiscovery.isNoCache()){
							GestoreToken.loggerInfo(getMessaggioAggiuntaOggettoInCache(keyCache));
							try{	
								org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
								responseCache.setObject(esitoDynamicDiscovery);
								cache.put(keyCache,responseCache);
							}catch(UtilsException e){
								GestoreToken.loggerError(getMessaggioErroreInserimentoInCache(keyCache, e));
							}
						}
					}
				}finally {
					lockDynamicDiscovery.release("dynamicDiscovery", idTransazione);
				}
			}
    	}
		
		return esitoDynamicDiscovery;
	}
	
	
	
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] VALIDAZIONE JWT TOKEN ****************** */
	
	public static final String VALIDAZIONE_JWT_FUNCTION = "ValidazioneJWT";
		
	public static EsitoGestioneToken validazioneJWTToken(Logger log, AbstractDatiInvocazione datiInvocazione, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			EsitoPresenzaToken esitoPresenzaToken, EsitoDynamicDiscovery esitoDynamicDiscovery, boolean portaDelegata,
			Busta busta, IDSoggetto idDominio, IDServizio idServizio) throws Exception {
		
		EsitoGestioneToken esitoGestioneToken = null;
		
		String token = esitoPresenzaToken.getToken();
		DynamicDiscovery dynamicDiscovery = esitoDynamicDiscovery!=null ? esitoDynamicDiscovery.getDynamicDiscovery() : null;
		
		if(GestoreToken.cacheGestioneToken==null){
			esitoGestioneToken = GestoreTokenValidazioneUtilities.validazioneJWTTokenEngine(log, datiInvocazione, esitoPresenzaToken, 
					dynamicDiscovery, 
					token, portaDelegata, pddContext,
					busta, idDominio, idServizio);
		}
    	else{
    		String policyName = datiInvocazione.getPolicyGestioneToken().getName();
    		String funzione = VALIDAZIONE_JWT_FUNCTION;
    		String keyCache = GestoreTokenValidazioneUtilities.buildCacheKeyValidazione(policyName, funzione, portaDelegata, token);

    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
    		
    		org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheGestioneToken.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					GestoreToken.loggerDebug(GestoreToken.getMessageObjectInCache(response, keyCache, funzione));
					esitoGestioneToken = (EsitoGestioneToken) response.getObject();
					esitoGestioneToken.setInCache(true);
				}else if(response.getException()!=null){
					GestoreToken.loggerDebug(GestoreToken.getMessageEccezioneInCache(response, keyCache, funzione));
					throw (Exception) response.getException();
				}else{
					GestoreToken.loggerError(MESSAGE_ELEMENT_CACHE_NE_OGGETTO_NE_ECCEZIONE);
				}
			}
    		
			if(esitoGestioneToken==null) {
				String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
				org.openspcoop2.utils.Semaphore lockJWT = getLockJWT(policyName);
				lockJWT.acquire("validazioneJWTToken", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheGestioneToken.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreToken.loggerDebug(GestoreToken.getMessageObjectInCache(response, keyCache, funzione));
							esitoGestioneToken = (EsitoGestioneToken) response.getObject();
							esitoGestioneToken.setInCache(true);
						}else if(response.getException()!=null){
							GestoreToken.loggerDebug(GestoreToken.getMessageEccezioneInCache(response, keyCache, funzione));
							throw (Exception) response.getException();
						}else{
							GestoreToken.loggerError(MESSAGE_ELEMENT_CACHE_NE_OGGETTO_NE_ECCEZIONE);
						}
					}
	
					if(esitoGestioneToken==null) {
						// Effettuo la query
						GestoreToken.loggerDebug(getPrefixOggettoConChiave(keyCache)+getSuffixEseguiOperazione(funzione));
						esitoGestioneToken = GestoreTokenValidazioneUtilities.validazioneJWTTokenEngine(log, datiInvocazione, esitoPresenzaToken, 
								dynamicDiscovery, 
								token, portaDelegata, pddContext,
								busta, idDominio, idServizio);
							
						// Aggiungo la risposta in cache (se esiste una cache)	
						// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
						// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
						// - impostare il noCache a true
						if(esitoGestioneToken!=null){
							esitoGestioneToken.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
							if(!esitoGestioneToken.isNoCache()){
								GestoreToken.loggerInfo(getMessaggioAggiuntaOggettoInCache(keyCache));
								try{	
									org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
									responseCache.setObject(esitoGestioneToken);
									GestoreToken.cacheGestioneToken.put(keyCache,responseCache);
								}catch(UtilsException e){
									GestoreToken.loggerError(getMessaggioErroreInserimentoInCache(keyCache, e));
								}
							}
						}else{
							throw new TokenException(getMessaggioErroreValoreEsitoNull( funzione));
						}
					}
				}finally {
					lockJWT.release("validazioneJWTToken", idTransazione);
				}
			}
    	}
		
		if(esitoGestioneToken.isValido()) {
			// ricontrollo tutte le date
			GestoreTokenValidazioneUtilities.validazioneInformazioniToken(esitoGestioneToken, datiInvocazione.getPolicyGestioneToken(), 
					datiInvocazione.getPolicyGestioneToken().isValidazioneJWTSaveErrorInCache());
		}
		
		SecurityToken securityToken = null;
		if(
				// esitoGestioneToken.isValido() && se non si genera comunque un security token non si hanno i json recuperati tramite la PDND 
				esitoGestioneToken.getRestSecurityToken()!=null) {
			securityToken = SecurityTokenUtilities.newSecurityToken(pddContext);
			securityToken.setAccessToken(esitoGestioneToken.getRestSecurityToken());
		}
		
		// enrich PDND Client Info
		GestoreTokenValidazioneUtilities.validazioneInformazioniTokenEnrichPDNDClientInfo(esitoGestioneToken, datiInvocazione.getPolicyGestioneToken(), 
				pddContext, protocolFactory, datiInvocazione, securityToken);
		
		return esitoGestioneToken;
	}
	
	
	
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] INTROSPECTION TOKEN ****************** */
	
	public static final String INTROSPECTION_FUNCTION = "Introspection";
	
	public static EsitoGestioneToken introspectionToken(Logger log, AbstractDatiInvocazione datiInvocazione, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			EsitoPresenzaToken esitoPresenzaToken, EsitoDynamicDiscovery esitoDynamicDiscovery, boolean portaDelegata,
			Busta busta, IDSoggetto idDominio, IDServizio idServizio) throws Exception {
		EsitoGestioneToken esitoGestioneToken = null;
		
		String token = esitoPresenzaToken.getToken();
		DynamicDiscovery dynamicDiscovery = esitoDynamicDiscovery!=null ? esitoDynamicDiscovery.getDynamicDiscovery() : null;
		
		if(GestoreToken.cacheGestioneToken==null){
			esitoGestioneToken = GestoreTokenValidazioneUtilities.introspectionTokenEngine(log, datiInvocazione, 
					pddContext, protocolFactory,
					dynamicDiscovery, token, portaDelegata,
					busta, idDominio, idServizio);
		}
    	else{
    		String policyName = datiInvocazione.getPolicyGestioneToken().getName();
    		String funzione = INTROSPECTION_FUNCTION;
    		String keyCache = GestoreTokenValidazioneUtilities.buildCacheKeyValidazione(policyName, funzione, portaDelegata, token);

    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
    		
    		org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheGestioneToken.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					GestoreToken.loggerDebug(GestoreToken.getMessageObjectInCache(response, keyCache, funzione));
					esitoGestioneToken = (EsitoGestioneToken) response.getObject();
					esitoGestioneToken.setInCache(true);
				}else if(response.getException()!=null){
					GestoreToken.loggerDebug(GestoreToken.getMessageEccezioneInCache(response, keyCache, funzione));
					throw (Exception) response.getException();
				}else{
					GestoreToken.loggerError(MESSAGE_ELEMENT_CACHE_NE_OGGETTO_NE_ECCEZIONE);
				}
			}
    		
			if(esitoGestioneToken==null) {
				
				String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
				org.openspcoop2.utils.Semaphore lockIntrospection = getLockIntrospection(policyName);
				lockIntrospection.acquire("introspectionToken", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheGestioneToken.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreToken.loggerDebug(GestoreToken.getMessageObjectInCache(response, keyCache, funzione));
							esitoGestioneToken = (EsitoGestioneToken) response.getObject();
							esitoGestioneToken.setInCache(true);
						}else if(response.getException()!=null){
							GestoreToken.loggerDebug(GestoreToken.getMessageEccezioneInCache(response, keyCache, funzione));
							throw (Exception) response.getException();
						}else{
							GestoreToken.loggerError(MESSAGE_ELEMENT_CACHE_NE_OGGETTO_NE_ECCEZIONE);
						}
					}
	
					if(esitoGestioneToken==null) {
						// Effettuo la query
						GestoreToken.loggerDebug(getPrefixOggettoConChiave(keyCache)+getSuffixEseguiOperazione(funzione));
						esitoGestioneToken = GestoreTokenValidazioneUtilities.introspectionTokenEngine(log, datiInvocazione, 
								pddContext, protocolFactory,
								dynamicDiscovery, token, portaDelegata,
								busta, idDominio, idServizio);
							
						// Aggiungo la risposta in cache (se esiste una cache)	
						// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
						// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
						// - impostare il noCache a true
						esitoGestioneToken.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
						if(!esitoGestioneToken.isNoCache()){
							GestoreToken.loggerInfo(getMessaggioAggiuntaOggettoInCache(keyCache));
							try{	
								org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
								responseCache.setObject(esitoGestioneToken);
								GestoreToken.cacheGestioneToken.put(keyCache,responseCache);
							}catch(UtilsException e){
								GestoreToken.loggerError(getMessaggioErroreInserimentoInCache(keyCache, e));
							}
						}
					}
				}finally {
					lockIntrospection.release("introspectionToken", idTransazione);
				}
			}
    	}
		
		if(esitoGestioneToken.isValido()) {
			// ricontrollo tutte le date
			GestoreTokenValidazioneUtilities.validazioneInformazioniToken(esitoGestioneToken, datiInvocazione.getPolicyGestioneToken(), 
					datiInvocazione.getPolicyGestioneToken().isIntrospectionSaveErrorInCache());
		}
		
		return esitoGestioneToken;
	}
	
	
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] USER INFO TOKEN ****************** */
	
	public static final String USERINFO_FUNCTION = "UserInfo";
	
	public static EsitoGestioneToken userInfoToken(Logger log, AbstractDatiInvocazione datiInvocazione, 
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			EsitoPresenzaToken esitoPresenzaToken, EsitoDynamicDiscovery esitoDynamicDiscovery, boolean portaDelegata,
			Busta busta, IDSoggetto idDominio, IDServizio idServizio) throws Exception {
		EsitoGestioneToken esitoGestioneToken = null;
		
		String token = esitoPresenzaToken.getToken();
		DynamicDiscovery dynamicDiscovery = esitoDynamicDiscovery!=null ? esitoDynamicDiscovery.getDynamicDiscovery() : null;
		
		if(GestoreToken.cacheGestioneToken==null){
			esitoGestioneToken = GestoreTokenValidazioneUtilities.userInfoTokenEngine(log, datiInvocazione, 
					pddContext, protocolFactory,
					dynamicDiscovery, token, portaDelegata,
					busta, idDominio, idServizio);
		}
    	else{
    		String policyName = datiInvocazione.getPolicyGestioneToken().getName();
    		String funzione = USERINFO_FUNCTION;
    		String keyCache = GestoreTokenValidazioneUtilities.buildCacheKeyValidazione(policyName, funzione, portaDelegata, token);

    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
    		
    		org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheGestioneToken.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					GestoreToken.loggerDebug(GestoreToken.getMessageObjectInCache(response, keyCache, funzione));
					esitoGestioneToken = (EsitoGestioneToken) response.getObject();
					esitoGestioneToken.setInCache(true);
				}else if(response.getException()!=null){
					GestoreToken.loggerDebug(GestoreToken.getMessageEccezioneInCache(response, keyCache, funzione));
					throw (Exception) response.getException();
				}else{
					GestoreToken.loggerError(MESSAGE_ELEMENT_CACHE_NE_OGGETTO_NE_ECCEZIONE);
				}
			}
    		
			if(esitoGestioneToken==null) {
				String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
				org.openspcoop2.utils.Semaphore lockUserInfo = getLockUserInfo(policyName);
				lockUserInfo.acquire("userInfoToken", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheGestioneToken.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreToken.loggerDebug(GestoreToken.getMessageObjectInCache(response, keyCache, funzione));
							esitoGestioneToken = (EsitoGestioneToken) response.getObject();
							esitoGestioneToken.setInCache(true);
						}else if(response.getException()!=null){
							GestoreToken.loggerDebug(GestoreToken.getMessageEccezioneInCache(response, keyCache, funzione));
							throw (Exception) response.getException();
						}else{
							GestoreToken.loggerError(MESSAGE_ELEMENT_CACHE_NE_OGGETTO_NE_ECCEZIONE);
						}
					}
	
					if(esitoGestioneToken==null) {
						// Effettuo la query
						GestoreToken.loggerDebug(getPrefixOggettoConChiave(keyCache)+getSuffixEseguiOperazione(funzione));
						esitoGestioneToken = GestoreTokenValidazioneUtilities.userInfoTokenEngine(log, datiInvocazione, 
								pddContext, protocolFactory,
								dynamicDiscovery, token, portaDelegata,
								busta, idDominio, idServizio);
							
						// Aggiungo la risposta in cache (se esiste una cache)	
						// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
						// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
						// - impostare il noCache a true
						esitoGestioneToken.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
						if(!esitoGestioneToken.isNoCache()){
							GestoreToken.loggerInfo(getMessaggioAggiuntaOggettoInCache(keyCache));
							try{	
								org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
								responseCache.setObject(esitoGestioneToken);
								GestoreToken.cacheGestioneToken.put(keyCache,responseCache);
							}catch(UtilsException e){
								GestoreToken.loggerError(getMessaggioErroreInserimentoInCache(keyCache, e));
							}
						}
					}
				}finally {
					lockUserInfo.release("userInfoToken", idTransazione);
				}
			}
    	}
		
		if(esitoGestioneToken.isValido()) {
			// ricontrollo tutte le date
			GestoreTokenValidazioneUtilities.validazioneInformazioniToken(esitoGestioneToken, datiInvocazione.getPolicyGestioneToken(), 
					datiInvocazione.getPolicyGestioneToken().isUserInfoSaveErrorInCache());
		}
		
		return esitoGestioneToken;
	}
	
	
	
	
	
	
	
	
	// ********* [VALIDAZIONE-TOKEN] FORWARD TOKEN ****************** */
	
	public static void forwardToken(Logger log, String idTransazione, AbstractDatiInvocazione datiInvocazione, EsitoPresenzaToken esitoPresenzaToken, 
			EsitoGestioneToken esitoValidazioneJWT, EsitoGestioneToken esitoIntrospection, EsitoGestioneToken esitoUserInfo, 
			InformazioniToken informazioniTokenNormalizzate,
			boolean portaDelegata,
			PdDContext pddContext, Busta busta) throws Exception {
		
		PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
			
		TokenForward tokenForward = new TokenForward();
		String token = esitoPresenzaToken.getToken();
		
		boolean trasparente = false;
		String forwardTrasparenteMode = null;
		String forwardTrasparenteModeHeader = null;
		String forwardTrasparenteModeUrl = null;
		if(policyGestioneToken.isForwardToken()) {
			trasparente = policyGestioneToken.isForwardTokenTrasparente();
			if(trasparente) {
				forwardTrasparenteMode = policyGestioneToken.getForwardTokenTrasparenteMode();
				if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_AS_RECEIVED.equals(forwardTrasparenteMode)) {
					forwardTrasparenteModeHeader = esitoPresenzaToken.getHeaderHttp();
					forwardTrasparenteModeUrl = esitoPresenzaToken.getPropertyUrl();
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_HEADER.equals(forwardTrasparenteMode)) {
					forwardTrasparenteModeHeader = HttpConstants.AUTHORIZATION;
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_RFC6750_URL.equals(forwardTrasparenteMode)) {
					forwardTrasparenteModeUrl = Costanti.RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN;
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_HEADER.equals(forwardTrasparenteMode)) {
					forwardTrasparenteModeHeader = policyGestioneToken.getForwardTokenTrasparenteModeCustomHeader();
				} 
				else if(Costanti.POLICY_TOKEN_FORWARD_TRASPARENTE_MODE_CUSTOM_URL.equals(forwardTrasparenteMode)) {
					forwardTrasparenteModeUrl = policyGestioneToken.getForwardTokenTrasparenteModeCustomUrl();
				} 
			}
		}
		
		boolean infoRaccolte = false;
		String forwardInformazioniRaccolteMode = null;
		Properties jwtSecurity = null;
		boolean encodeBase64 = false;
		boolean forwardValidazioneJWT = false;	
		String forwardValidazioneJWTMode = null;
		String forwardValidazioneJWTName = null;
		boolean forwardIntrospection = false;	
		String forwardIntrospectionMode = null;
		String forwardIntrospectionName = null;
		boolean forwardUserInfo = false;	
		String forwardUserInfoMode = null;
		String forwardUserInfoName = null;
		if(policyGestioneToken.isForwardToken()) {
			infoRaccolte = policyGestioneToken.isForwardTokenInformazioniRaccolte();
			if(infoRaccolte) {
				forwardInformazioniRaccolteMode = policyGestioneToken.getForwardTokenInformazioniRaccolteMode();
				encodeBase64 = policyGestioneToken.isForwardTokenInformazioniRaccolteEncodeBase64();
				if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS.equals(forwardInformazioniRaccolteMode) ||
						Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(forwardInformazioniRaccolteMode)) {
					jwtSecurity = policyGestioneToken.getProperties().get(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_SIGNATURE_PROP_REF_ID);
				}
				else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(forwardInformazioniRaccolteMode)) {
					jwtSecurity = policyGestioneToken.getProperties().get(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_ENCRYP_PROP_REF_ID);
				}
				
				if(jwtSecurity!=null) {
					boolean throwError = true;
					Map<String,Object> dynamicMap = DynamicMapBuilderUtils.buildDynamicMap(busta, datiInvocazione.getRequestInfo(), pddContext, log);
    				JOSEUtils.injectKeystore(datiInvocazione.getRequestInfo(), dynamicMap, jwtSecurity, log, throwError); // serve per leggere il keystore dalla cache
				}
				
				forwardValidazioneJWT = policyGestioneToken.isForwardTokenInformazioniRaccolteValidazioneJWT();
				if(forwardValidazioneJWT) {
					forwardValidazioneJWTMode = policyGestioneToken.getForwardTokenInformazioniRaccolteValidazioneJWTMode();
					if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardValidazioneJWTMode)) {
						forwardValidazioneJWTName = policyGestioneToken.getForwardTokenInformazioniRaccolteValidazioneJWTModeHeaderName();
					}
					else {
						forwardValidazioneJWTName = policyGestioneToken.getForwardTokenInformazioniRaccolteValidazioneJWTModeQueryParameterName();
					}
				}
				
				forwardIntrospection = policyGestioneToken.isForwardTokenInformazioniRaccolteIntrospection();
				if(forwardIntrospection) {
					forwardIntrospectionMode = policyGestioneToken.getForwardTokenInformazioniRaccolteIntrospectionMode();
					if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardIntrospectionMode)) {
						forwardIntrospectionName = policyGestioneToken.getForwardTokenInformazioniRaccolteIntrospectionModeHeaderName();
					}
					else {
						forwardIntrospectionName = policyGestioneToken.getForwardTokenInformazioniRaccolteIntrospectionModeQueryParameterName();
					}
				}
				
				forwardUserInfo = policyGestioneToken.isForwardTokenInformazioniRaccolteUserInfo();
				if(forwardUserInfo) {
					forwardUserInfoMode = policyGestioneToken.getForwardTokenInformazioniRaccolteUserInfoMode();
					if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_NO_OPENSPCOOP_CUSTOM_HEADER.equals(forwardUserInfoMode)) {
						forwardUserInfoName = policyGestioneToken.getForwardTokenInformazioniRaccolteUserInfoModeHeaderName();
					}
					else {
						forwardUserInfoName = policyGestioneToken.getForwardTokenInformazioniRaccolteUserInfoModeQueryParameterName();
					}
				}
			}
		}
		
		// Elimino token ricevuto
		boolean delete = GestoreTokenValidazioneUtilities.deleteTokenReceived(datiInvocazione, esitoPresenzaToken, trasparente, forwardTrasparenteModeHeader, forwardTrasparenteModeUrl);
		
		if(trasparente && delete) {
			
			// Forward trasparente
			
			// la delete ha tenuto conto dell'opzione di forward prima di eliminare
			GestoreTokenValidazioneUtilities.forwardTokenTrasparenteEngine(token, esitoPresenzaToken, tokenForward, forwardTrasparenteMode, forwardTrasparenteModeHeader, forwardTrasparenteModeUrl);
		}
		
		if(infoRaccolte) {
			
			// Forward informazioni raccolte
			
			GestoreTokenValidazioneUtilities.forwardInfomazioniRaccolteEngine(portaDelegata, idTransazione, tokenForward, 
					esitoValidazioneJWT, esitoIntrospection, esitoUserInfo, 
					informazioniTokenNormalizzate,
					forwardInformazioniRaccolteMode, jwtSecurity, encodeBase64, 
					forwardValidazioneJWT, forwardValidazioneJWTMode, forwardValidazioneJWTName,
					forwardIntrospection, forwardIntrospectionMode, forwardIntrospectionName,
					forwardUserInfo, forwardUserInfoMode, forwardUserInfoName);
			
		}
		
		// Imposto token forward nel messaggio
		if(tokenForward.getUrl().size()>0 || tokenForward.getTrasporto().size()>0) {
			datiInvocazione.getMessage().addContextProperty(Costanti.MSG_CONTEXT_TOKEN_FORWARD, tokenForward);
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
		
		// A differenza del metodo sopra non si controlla che il token sia valido.
		
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
			return new InformazioniToken(OpenSPCoop2Properties.getInstance().isGestioneTokenSaveSourceTokenInfo(), list.toArray(new InformazioniToken[1]));
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
			Busta busta, RequestInfo requestInfo, TipoPdD tipoPdD, String idModulo, PortaApplicativa pa, PortaDelegata pd,
			PdDContext pddContext, IProtocolFactory<?> protocolFactory) throws Exception {
		return endpointTokenEngine(debug, log, policyNegoziazioneToken, 
				busta, requestInfo, tipoPdD, idModulo, pa, pd,
				pddContext,  protocolFactory,
				false, null,
				null);
	}
	private static EsitoNegoziazioneToken endpointTokenEngine(boolean debug, Logger log, PolicyNegoziazioneToken policyNegoziazioneToken, 
			Busta busta, RequestInfo requestInfo, TipoPdD tipoPdD, String idModulo, PortaApplicativa pa, PortaDelegata pd,
			PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			boolean rinegozia, InformazioniNegoziazioneToken previousToken,
			InformazioniNegoziazioneToken_DatiRichiesta datiRichiesta) throws Exception {
		EsitoNegoziazioneToken esitoNegoziazioneToken = null;
		boolean riavviaNegoziazione = rinegozia;
		riavviaNegoziazione = false;
		
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
		
		Map<String, Object> dynamicMap = GestoreTokenNegoziazioneUtilities.buildDynamicNegoziazioneTokenMap(busta, requestInfo, pddContext, log);
		NegoziazioneTokenDynamicParameters dynamicParameters = new NegoziazioneTokenDynamicParameters(dynamicMap, 
				pddContext, requestInfo, busta, state, protocolFactory, 
				policyNegoziazioneToken);
		
		if(GestoreToken.cacheGestioneToken==null){
			esitoNegoziazioneToken = GestoreTokenNegoziazioneUtilities.endpointTokenEngine(debug, log, policyNegoziazioneToken, 
					busta, requestInfo, tipoPdD,
					dynamicParameters, protocolFactory, 
					state, delegata, idModulo, pa, pd,
					idDominio, idServizio,
					previousToken,
					datiRichiesta);
			
			if(esitoNegoziazioneToken!=null && esitoNegoziazioneToken.isValido()) {
				// ricontrollo tutte le date (l'ho appena preso, dovrebbero essere buone) 
				boolean checkPerRinegoziazione = false;
				GestoreTokenNegoziazioneUtilities.validazioneInformazioniNegoziazioneToken(checkPerRinegoziazione, esitoNegoziazioneToken,  
						policyNegoziazioneToken.isSaveErrorInCache());
			}
			
		}
    	else{
    		String policyName = policyNegoziazioneToken.getName();
    		String funzione = RETRIEVE_FUNCTION;
    		String keyCache = GestoreTokenNegoziazioneUtilities.buildCacheKeyNegoziazione(policyName, funzione, delegata, dynamicParameters);

    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
    		
    		org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheGestioneToken.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					GestoreToken.loggerDebug(GestoreToken.getMessageObjectInCache(response, keyCache, funzione));
					esitoNegoziazioneToken = (EsitoNegoziazioneToken) response.getObject();
					esitoNegoziazioneToken.setInCache(true);
				}else if(response.getException()!=null){
					GestoreToken.loggerDebug(GestoreToken.getMessageEccezioneInCache(response, keyCache, funzione));
					throw (Exception) response.getException();
				}else{
					GestoreToken.loggerError(MESSAGE_ELEMENT_CACHE_NE_OGGETTO_NE_ECCEZIONE);
				}
			}
    		
			if(esitoNegoziazioneToken==null) {
				org.openspcoop2.utils.Semaphore lockNegoziazione = getLockNegoziazione(policyName);
				lockNegoziazione.acquire("endpointToken", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheGestioneToken.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreToken.loggerDebug(GestoreToken.getMessageObjectInCache(response, keyCache, funzione));
							esitoNegoziazioneToken = (EsitoNegoziazioneToken) response.getObject();
							esitoNegoziazioneToken.setInCache(true);
						}else if(response.getException()!=null){
							GestoreToken.loggerDebug(GestoreToken.getMessageEccezioneInCache(response, keyCache, funzione));
							throw (Exception) response.getException();
						}else{
							GestoreToken.loggerError(MESSAGE_ELEMENT_CACHE_NE_OGGETTO_NE_ECCEZIONE);
						}
					}
	
					if(esitoNegoziazioneToken==null) {
						// Effettuo la query
						GestoreToken.loggerDebug(getPrefixOggettoConChiave(keyCache)+getSuffixEseguiOperazione(funzione));
						esitoNegoziazioneToken = GestoreTokenNegoziazioneUtilities.endpointTokenEngine(debug, log, policyNegoziazioneToken, 
								busta, requestInfo, tipoPdD,
								dynamicParameters, protocolFactory, 
								state, delegata, idModulo, pa, pd,
								idDominio, idServizio,
								previousToken,
								datiRichiesta);
							
						// Aggiungo la risposta in cache (se esiste una cache)	
						// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
						// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
						// - impostare il noCache a true
						if(esitoNegoziazioneToken!=null){ // altrimenti lo mettero' in cache al giro dopo.
							esitoNegoziazioneToken.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
							if(!esitoNegoziazioneToken.isNoCache()){
								GestoreToken.loggerInfo(getMessaggioAggiuntaOggettoInCache(keyCache));
								try{	
									org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
									responseCache.setObject(esitoNegoziazioneToken);
									GestoreToken.cacheGestioneToken.put(keyCache,responseCache);
								}catch(UtilsException e){
									GestoreToken.loggerError(getMessaggioErroreInserimentoInCache(keyCache, e));
								}
							}
						}else{
							throw new TokenException(getMessaggioErroreValoreEsitoNull( funzione));
						}
						
						if(esitoNegoziazioneToken.isValido()) {
							// ricontrollo tutte le date (l'ho appena preso, dovrebbero essere buone) 
							boolean checkPerRinegoziazione = false;
							GestoreTokenNegoziazioneUtilities.validazioneInformazioniNegoziazioneToken(checkPerRinegoziazione, esitoNegoziazioneToken,  
									policyNegoziazioneToken.isSaveErrorInCache());
						}
					}
					else {
					
						// l'ho preso in cache
						
						if(esitoNegoziazioneToken.isValido()) {
							// controllo la data qua
							boolean checkPerRinegoziazione = true;
							GestoreTokenNegoziazioneUtilities.validazioneInformazioniNegoziazioneToken(checkPerRinegoziazione, esitoNegoziazioneToken,  
									policyNegoziazioneToken.isSaveErrorInCache());
							if(!esitoNegoziazioneToken.isValido() && !esitoNegoziazioneToken.isDateValide()) {
								// DEVO riavviare la negoziazione poichè è scaduto
								GestoreToken.cacheGestioneToken.remove(keyCache);
								riavviaNegoziazione = true;
								/** System.out.println("Riavvia negoziazione"); */
							}
						}
						
					}
					
				}finally {
					// fine synchronized
					lockNegoziazione.release("endpointToken", idTransazione);
				}
			}
			else {
				
				// l'ho preso in cache
				
				if(esitoNegoziazioneToken.isValido()) {
					// controllo la data qua
					boolean checkPerRinegoziazione = true;
					GestoreTokenNegoziazioneUtilities.validazioneInformazioniNegoziazioneToken(checkPerRinegoziazione, esitoNegoziazioneToken,  
							policyNegoziazioneToken.isSaveErrorInCache());
					if(!esitoNegoziazioneToken.isValido() && !esitoNegoziazioneToken.isDateValide()) {
						
						org.openspcoop2.utils.Semaphore lockNegoziazione = getLockNegoziazione(policyName);
						lockNegoziazione.acquire("removeToken", idTransazione);
						try {
							// DEVO riavviare la negoziazione poichè è scaduto
							GestoreToken.cacheGestioneToken.remove(keyCache);
							riavviaNegoziazione = true;
							/** System.out.println("Riavvia negoziazione"); */
						}finally {
							// fine synchronized
							lockNegoziazione.release("removeToken", idTransazione);
						}
					}
				}
				
			}
    	}
		
		if(riavviaNegoziazione) {
			return endpointTokenEngine(debug, log, policyNegoziazioneToken, 
					busta, requestInfo, tipoPdD, idModulo, pa, pd,
					pddContext, protocolFactory,
					riavviaNegoziazione, esitoNegoziazioneToken.getInformazioniNegoziazioneToken(),// refresh
					datiRichiesta); 
		}
		
		try {
			if(idTransazione!=null) {
				Transaction t = TransactionContext.getTransaction(idTransazione);
				if(esitoNegoziazioneToken!=null) {
					t.setInformazioniNegoziazioneToken(esitoNegoziazioneToken.getInformazioniNegoziazioneToken());
				}
			}
		}catch(Exception t) {
			// ignore
		}
		
		return esitoNegoziazioneToken;
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
		
		Map<String, Object> dynamicMap = GestoreTokenAttributeAuthorityUtilities.buildDynamicAAMap(message, busta, requestInfo, pddContext, log,
				policyAttributeAuthority.getName(), datiInvocazione);
		AttributeAuthorityDynamicParameters dynamicParameters = new AttributeAuthorityDynamicParameters(dynamicMap, pddContext, requestInfo, policyAttributeAuthority);
		
		ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(datiInvocazione.getState());
		
		PortaApplicativa pa = null;
		PortaDelegata pd = null;
		if(datiInvocazione instanceof DatiInvocazionePortaApplicativa) {
			pa = ((DatiInvocazionePortaApplicativa)datiInvocazione).getPa();
		}
		else if(datiInvocazione instanceof DatiInvocazionePortaDelegata) {
			pd = ((DatiInvocazionePortaDelegata)datiInvocazione).getPd();
		}
		
		if(GestoreToken.cacheAttributeAuthority==null){
						
			boolean addIdAndDate = true;
			String request = GestoreTokenAttributeAuthorityUtilities.buildDynamicAARequest(configurazionePdDManager,  
					protocolFactory, requestInfo, 
					policyAttributeAuthority, dynamicParameters, addIdAndDate);
			
			esitoRecuperoAttributi = GestoreTokenAttributeAuthorityUtilities.readAttributes(log, policyAttributeAuthority, 
					protocolFactory,
					dynamicParameters,
					request, portaDelegata, 
					pddContext, datiInvocazione.getBusta(),
					datiInvocazione.getIdModulo(), pa, pd,
					datiInvocazione.getState(),
					idDominio, idServizio,
					requestInfo);
			
			if(esitoRecuperoAttributi!=null && esitoRecuperoAttributi.isValido()) {
				// ricontrollo tutte le date (l'ho appena preso, dovrebbero essere buone) 
				GestoreTokenAttributeAuthorityUtilities.validazioneInformazioniAttributiRecuperati(esitoRecuperoAttributi, policyAttributeAuthority, 
						policyAttributeAuthority.isSaveErrorInCache(),
						dynamicParameters);
			}
			
		}
    	else{
    		String funzione = ATTRIBUTE_AUTHORITY_FUNCTION;
    		
    		boolean addIdAndDate = true;
    		
    		// Aggiungo anche la richiesta poichè può venire costruita con freemarker o template e quindi può essere dinamica a sua volta (non considero però le date)
    		String requestKeyCache = GestoreTokenAttributeAuthorityUtilities.buildDynamicAARequest(configurazionePdDManager, 
    				protocolFactory, requestInfo, 
    				policyAttributeAuthority, dynamicParameters, !addIdAndDate);
    		
    		String aaName = policyAttributeAuthority.getName();
    		String keyCache = GestoreTokenAttributeAuthorityUtilities.buildCacheKeyRecuperoAttributi(aaName, funzione, portaDelegata, dynamicParameters, requestKeyCache);

    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
    		
    		org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheAttributeAuthority.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					GestoreToken.loggerDebug(GestoreToken.getMessageObjectInCache(response, keyCache, funzione));
					esitoRecuperoAttributi = (EsitoRecuperoAttributi) response.getObject();
					esitoRecuperoAttributi.setInCache(true);
				}else if(response.getException()!=null){
					GestoreToken.loggerDebug(GestoreToken.getMessageEccezioneInCache(response, keyCache, funzione));
					throw (Exception) response.getException();
				}else{
					GestoreToken.loggerError(MESSAGE_ELEMENT_CACHE_NE_OGGETTO_NE_ECCEZIONE);
				}
			}
    		
			String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
			
			if(esitoRecuperoAttributi==null) {
			
				org.openspcoop2.utils.Semaphore lockAttributeAuthority = getLockAttributeAuthority(aaName);
				lockAttributeAuthority.acquire("readAttributes", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheAttributeAuthority.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreToken.loggerDebug(GestoreToken.getMessageObjectInCache(response, keyCache, funzione));
							esitoRecuperoAttributi = (EsitoRecuperoAttributi) response.getObject();
							esitoRecuperoAttributi.setInCache(true);
						}else if(response.getException()!=null){
							GestoreToken.loggerDebug(GestoreToken.getMessageEccezioneInCache(response, keyCache, funzione));
							throw (Exception) response.getException();
						}else{
							GestoreToken.loggerError(MESSAGE_ELEMENT_CACHE_NE_OGGETTO_NE_ECCEZIONE);
						}
					}
	
					if(esitoRecuperoAttributi==null) {
						// Effettuo la query
						GestoreToken.loggerDebug(getPrefixOggettoConChiave(keyCache)+getSuffixEseguiOperazione(funzione));
						
						String request = null;
						if(policyAttributeAuthority.isRequestDynamicPayloadJwt()) {
							// ricostruisco per considerare le date
							request = GestoreTokenAttributeAuthorityUtilities.buildDynamicAARequest(configurazionePdDManager, 
									protocolFactory, requestInfo, 
									policyAttributeAuthority, dynamicParameters, addIdAndDate);
						}
						else {
							request = requestKeyCache;
						}
						
						esitoRecuperoAttributi = GestoreTokenAttributeAuthorityUtilities.readAttributes(log, policyAttributeAuthority, 
								protocolFactory,
								dynamicParameters,
								request, portaDelegata, 
								pddContext, datiInvocazione.getBusta(),
								datiInvocazione.getIdModulo(), pa, pd,
								datiInvocazione.getState(),
								idDominio, idServizio,
								requestInfo);
							
						// Aggiungo la risposta in cache (se esiste una cache)	
						// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
						// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
						// - impostare il noCache a true
						if(esitoRecuperoAttributi!=null){ // altrimenti lo mettero' in cache al giro dopo.
							esitoRecuperoAttributi.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
							if(!esitoRecuperoAttributi.isNoCache()){
								GestoreToken.loggerInfo(getMessaggioAggiuntaOggettoInCache(keyCache));
								try{	
									org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
									responseCache.setObject(esitoRecuperoAttributi);
									GestoreToken.cacheAttributeAuthority.put(keyCache,responseCache);
								}catch(UtilsException e){
									GestoreToken.loggerError(getMessaggioErroreInserimentoInCache(keyCache, e));
								}
							}
						}else{
							throw new TokenException(getMessaggioErroreValoreEsitoNull( funzione));
						}
						
						if(esitoRecuperoAttributi.isValido()) {
							// ricontrollo tutte le date (l'ho appena preso, dovrebbero essere buone) 
							GestoreTokenAttributeAuthorityUtilities.validazioneInformazioniAttributiRecuperati(esitoRecuperoAttributi, policyAttributeAuthority, 
									policyAttributeAuthority.isSaveErrorInCache(),
									dynamicParameters);
						}
					}
					else {
					
						// l'ho preso in cache
						
						if(esitoRecuperoAttributi.isValido()) {
							// controllo la data qua
							GestoreTokenAttributeAuthorityUtilities.validazioneInformazioniAttributiRecuperati(esitoRecuperoAttributi, policyAttributeAuthority, 
									policyAttributeAuthority.isSaveErrorInCache(),
									dynamicParameters);
							if(!esitoRecuperoAttributi.isValido() && !esitoRecuperoAttributi.isDateValide()) {
								// DEVO riavviare la negoziazione poichè è scaduto
								GestoreToken.cacheAttributeAuthority.remove(keyCache);
								riavviaNegoziazione = true;
							}
						}
						
					}
					
				} finally{
					// fine synchronized
					lockAttributeAuthority.release("readAttributes", idTransazione);
				}
			}
			else {
				
				// l'ho preso in cache
				
				if(esitoRecuperoAttributi.isValido()) {
					// controllo la data qua
					GestoreTokenAttributeAuthorityUtilities.validazioneInformazioniAttributiRecuperati(esitoRecuperoAttributi, policyAttributeAuthority, 
							policyAttributeAuthority.isSaveErrorInCache(),
							dynamicParameters);
					if(!esitoRecuperoAttributi.isValido() && !esitoRecuperoAttributi.isDateValide()) {
						org.openspcoop2.utils.Semaphore lockAttributeAuthority = getLockAttributeAuthority(aaName);
						lockAttributeAuthority.acquire("removeAttributes", idTransazione);
						try {
							// DEVO riavviare la negoziazione poichè è scaduto
							GestoreToken.cacheAttributeAuthority.remove(keyCache);
							riavviaNegoziazione = true;
						} finally{
							// fine synchronized
							lockAttributeAuthority.release("removeAttributes", idTransazione);
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
	
	public static InformazioniAttributi normalizeInformazioniAttributi(List<InformazioniAttributi> list, List<AttributeAuthority> aaList) throws Exception {
		
		/** int size = list.size();
		// Fix: deve basarsi sulle AA configurate e non su quelle da cui si e' ottenuto effettivamente una risposta, altrimenti poi le regole cambiano (prefisso AA presente o meno) */
		int size = aaList.size();
		if(size==1) {
			return list.get(0);
		}
		else {
			return new InformazioniAttributi(OpenSPCoop2Properties.getInstance().isGestioneAttributeAuthoritySaveSourceAttributeResponseInfo(), list.toArray(new InformazioniAttributi[1]));
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ********* [GENERIC-TOKEN] ****************** */
	
	public static TokenCacheItem getTokenCacheItem(String keyCache, String funzione, Date now) throws TokenException {
		TokenCacheItem item = null;
		
		if(GestoreToken.cacheGestioneToken!=null){
			
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheGestioneToken.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					GestoreToken.loggerDebug(GestoreToken.getMessageObjectInCache(response, keyCache, funzione));
					item = (TokenCacheItem) response.getObject();
					item.setInCache(true);
				}else if(response.getException()!=null){
					Exception e = (Exception) response.getException();
					throw new TokenException("Trovata eccezione '"+response.getException().getClass().getName()+"' in cache non prevista: "+e.getMessage(),e);
				}else{
					GestoreToken.loggerError(MESSAGE_ELEMENT_CACHE_NE_OGGETTO_NE_ECCEZIONE);
				}
			}
			
			if(item!=null) {
				item = checkTokenCacheItem(item, keyCache, now, true);
			}
		}
		
		return item;
	}
	private static TokenCacheItem checkTokenCacheItem(TokenCacheItem item, String keyCache, Date now, boolean debug) {
		boolean tokenExpired = false;
		if(item.getExp()!=null) {
			try{
				tokenExpired = GestoreTokenValidazioneUtilities.isExpired(now, item.getExp());
			}catch(Exception e) {
				GestoreToken.loggerError("Token presente in cache con chiave '"+keyCache+"', verifica scadenza '"+DateUtils.getSimpleDateFormatMs().format(item.getExp())+"' fallita: "+e.getMessage(),e);
			}
		}
		if(tokenExpired) {
			if(debug) {
				GestoreToken.loggerDebug("Token presente in cache con chiave '"+keyCache+"' risulta scaduto in data '"+DateUtils.getSimpleDateFormatMs().format(item.getExp())+"'");
			}
			return null;
		}
		return item;
	}
	
	public static void putTokenCacheItem(TokenCacheItem item, String keyCache, String funzione, Date now) {
		
		if(GestoreToken.cacheGestioneToken!=null){
			
    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
			// Qua non va fatto come per gli altri casi, è nell'utilizzo del metodo getTokenCacheItem che lo si fa
    		
    		
			org.openspcoop2.utils.Semaphore lockNegoziazione = getLockGenericToken(funzione);
			lockNegoziazione.acquireThrowRuntime("putTokenCacheItem", item.getIdTransazione());
			try {
				
				// per gestire concorrenza
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreToken.cacheGestioneToken.get(keyCache);
				TokenCacheItem itemInCache = null;
				if(response != null &&
					response.getObject()!=null){
					GestoreToken.loggerDebug(GestoreToken.getMessageObjectInCache(response, keyCache, funzione));
					itemInCache = (TokenCacheItem) response.getObject();
					itemInCache.setInCache(true);
				}
				if(itemInCache!=null) {
					itemInCache = checkTokenCacheItem(itemInCache, keyCache, now, false);
				}

				if(itemInCache==null) {
						
					// Aggiungo la risposta in cache (se esiste una cache)	
					// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
					// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
					// - impostare il noCache a true
					item.setInCache(false); // la prima volta che lo recupero sicuramente non era in cache
					GestoreToken.loggerInfo(getMessaggioAggiuntaOggettoInCache(keyCache));
					try{	
						org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
						responseCache.setObject(item);
						GestoreToken.cacheGestioneToken.put(keyCache,responseCache);
					}catch(UtilsException e){
						GestoreToken.loggerError(getMessaggioErroreInserimentoInCache(keyCache, e));
					}
					
				}
				
			}finally {
				// fine synchronized
				lockNegoziazione.release("putTokenCacheItem", item.getIdTransazione());
			}

		}
	}
}

