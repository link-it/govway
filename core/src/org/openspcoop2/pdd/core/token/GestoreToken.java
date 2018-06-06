package org.openspcoop2.pdd.core.token;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.message.ForcedResponseMessage;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.token.pa.EsitoGestioneTokenPortaApplicativa;
import org.openspcoop2.pdd.core.token.pa.EsitoPresenzaTokenPortaApplicativa;
import org.openspcoop2.pdd.core.token.parser.ITokenParser;
import org.openspcoop2.pdd.core.token.pd.EsitoGestioneTokenPortaDelegata;
import org.openspcoop2.pdd.core.token.pd.EsitoPresenzaTokenPortaDelegata;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.security.JOSERepresentation;
import org.openspcoop2.utils.security.JsonDecrypt;
import org.openspcoop2.utils.security.JsonVerifySignature;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

public class GestoreToken {

	/** Chiave della cache per la gestione dei token  */
	private static final String TOKEN_CACHE_NAME = "token";
	/** Cache */
	private static Cache cacheToken = null;
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
			throw new TokenException("Visualizzazione Statistiche riguardante la cache sulla gestione dei token della Porta di Dominio non riuscita: "+e.getMessage(),e);
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

		if( (idleTime > 0) ||
				(itemLifeSecond > 0) ){

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
			if( itemLifeSecond > 0  ){
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

		}
	}
	
	
	
	
	
	
	
	// ********* VALIDAZIONE CONFIGURAZIONE ****************** */
	
	public static void validazioneConfigurazione(AbstractDatiInvocazione datiInvocazione) throws ProviderException, ProviderValidationException {
		TokenProvider p = new TokenProvider();
		p.validate(datiInvocazione.getPolicyGestioneToken().getProperties());
	}
	
	
	
	
	// ********* VERIFICA POSIZIONE TOKEN ****************** */
	
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
    		Properties properties = policyGestioneToken.getDefaultProperties();
    		String source = properties.getProperty(Costanti.POLICY_TOKEN_SOURCE);
    		
    		String detailsError = null;
			String token = null;
			    		
    		if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source) ||
    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_HEADER.equals(source) ||
    				Costanti.POLICY_TOKEN_SOURCE_CUSTOM_HEADER.equals(source)) {
    			if(datiInvocazione.getInfoConnettoreIngresso()==null || 
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext()==null) {
    				detailsError = "Informazioni di trasporto non presenti";
    			}
    			else {
    				URLProtocolContext urlProtocolContext = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext();
    				if(urlProtocolContext.getParametersTrasporto()==null || urlProtocolContext.getParametersTrasporto().size()<=0) {
        				detailsError = "Header di trasporto non presenti";
        			}
    				if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source) ||
    	    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_HEADER.equals(source)) {
    					if(urlProtocolContext.getCredential()==null || urlProtocolContext.getCredential().getBearerToken()==null) {
    						if(urlProtocolContext.getCredential()!=null && urlProtocolContext.getCredential().getUsername()!=null) {
    							detailsError = "Riscontrato header http '"+HttpConstants.AUTHORIZATION+"' valorizzato tramite autenticazione '"+HttpConstants.AUTHORIZATION_PREFIX_BASIC+
    									"'; la configurazione richiede invece la presenza di un token valorizzato tramite autenticazione '"+HttpConstants.AUTHORIZATION_PREFIX_BEARER+"' ";
    						}
    						else {
    							detailsError = "Non è stato riscontrato un header http '"+HttpConstants.AUTHORIZATION+"' valorizzato tramite autenticazione '"+HttpConstants.AUTHORIZATION_PREFIX_BEARER+"' e contenente un token";
    						}
    					}
    					else {
    						token = urlProtocolContext.getCredential().getBearerToken();
    						esitoPresenzaToken.setHeaderHttp(HttpConstants.AUTHORIZATION);
    					}
    				}
    				else {
    					String headerName = properties.getProperty(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_HEADER_NAME);					
    					token =  urlProtocolContext.getParameterTrasporto(headerName);
    					if(token==null) {
    						detailsError = "Non è stato riscontrato l'header http '"+headerName+"' contenente il token";
    					}
    					else {
    						esitoPresenzaToken.setHeaderHttp(headerName);
    					}
    				}
    			}
    		}
    		
    		if( (token==null && Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source)) ||
    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_URL.equals(source) ||
    				Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL.equals(source)) {
    			if(datiInvocazione.getInfoConnettoreIngresso()==null || 
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext()==null) {
    				detailsError = "Informazioni di trasporto non presenti";
    			}
    			else {
    				URLProtocolContext urlProtocolContext = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext();
    				if(urlProtocolContext.getParametersFormBased()==null || urlProtocolContext.getParametersFormBased().size()<=0) {
        				detailsError = "Parametri nella URL non presenti";
        			}
    				String propertyUrlName = null;
    				if(Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source) ||
    	    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_URL.equals(source)) {
    					propertyUrlName = Costanti.RFC6750_URI_QUERY_PARAMETER_ACCESS_TOKEN;
    				}
    				else {
    					propertyUrlName = properties.getProperty(Costanti.POLICY_TOKEN_SOURCE_CUSTOM_URL_PROPERTY_NAME);	
    				}
    				token =  urlProtocolContext.getParameterFormBased(propertyUrlName);
					if(token==null) {
						detailsError = "Non è stato riscontrata la proprietà della URL '"+propertyUrlName+"' contenente il token";
					}
					else {
						esitoPresenzaToken.setPropertyUrl(propertyUrlName);
					}
    			}
    		}
    		
    		if( (token==null && Costanti.POLICY_TOKEN_SOURCE_RFC6750.equals(source)) ||
    				Costanti.POLICY_TOKEN_SOURCE_RFC6750_FORM.equals(source)) {
    			if(datiInvocazione.getInfoConnettoreIngresso()==null || 
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext()==null ||
    					datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext().getHttpServletRequest()==null) {
    				detailsError = "Informazioni di trasporto non presenti";
    			}
    			else {
    				HttpServletRequest httpServletRequest = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext().getHttpServletRequest();
    				if(httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
    					FormUrlEncodedHttpServletRequest form = (FormUrlEncodedHttpServletRequest) httpServletRequest;
    					token = form.getFormUrlEncodedParameter(Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN);
    					if(token==null) {
    						detailsError = "Non è stato riscontrata la proprietà della Form '"+Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN+"' contenente il token";
    					}
    					else {
    						esitoPresenzaToken.setPropertyFormBased(Costanti.RFC6750_FORM_PARAMETER_ACCESS_TOKEN);
    					}
    				}
    				else {
    					detailsError = "Non è stato riscontrata la presenza di un contenuto 'Form-Encoded'";
    				}
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
    			esitoPresenzaToken.setErrorMessage(buildErrorMessage(WWW_Authenticate_ErrorCode.invalid_request, policyGestioneToken.getRealm(), 
    					policyGestioneToken.isGenericError(), esitoPresenzaToken.getDetails()));   			
    		}
    		
    	}catch(Exception e){
    		esitoPresenzaToken.setDetails(e.getMessage());
    		esitoPresenzaToken.setEccezioneProcessamento(e);
    	}
		
		return esitoPresenzaToken;
	}
	
	
	
	
	
	// ********* VALIDAZIONE JWT TOKEN ****************** */
	
	public static EsitoGestioneToken validazioneJWTToken(AbstractDatiInvocazione datiInvocazione, String token, boolean portaDelegata) throws Exception {
		
		EsitoGestioneToken esitoGestioneToken = null;
		
		if(GestoreToken.cacheToken==null){
			esitoGestioneToken = _validazioneJWTToken(datiInvocazione, token, portaDelegata);
		}
    	else{
    		String funzione = "ValidazioneJWT";
    		String keyCache = buildCacheKey(funzione, portaDelegata, token);

			synchronized (GestoreToken.cacheToken) {

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

				// Effettuo la query
				GestoreToken.logger.debug("oggetto con chiave ["+keyCache+"] (method:"+funzione+") eseguo operazione...");
				esitoGestioneToken = _validazioneJWTToken(datiInvocazione, token, portaDelegata);
					
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
    	}
		
		if(esitoGestioneToken.isValido()) {
			// ricontrollo tutte le date
			_validazioneInformazioniToken(esitoGestioneToken, datiInvocazione.getPolicyGestioneToken(), 
					datiInvocazione.getPolicyGestioneToken().isValidazioneJWT_saveErrorInCache());
		}
		
		return esitoGestioneToken;
	}
	
	private static EsitoGestioneToken _validazioneJWTToken(AbstractDatiInvocazione datiInvocazione, String token, boolean portaDelegata) {
		EsitoGestioneToken esitoGestioneToken = null;
		if(portaDelegata) {
			esitoGestioneToken = new EsitoGestioneTokenPortaDelegata();
		}
		else {
			esitoGestioneToken = new EsitoGestioneTokenPortaApplicativa();
		}
		
		esitoGestioneToken.setValido(false);
		esitoGestioneToken.setToken(token);
		
		try{
			PolicyGestioneToken policyGestioneToken = datiInvocazione.getPolicyGestioneToken();
    		Properties properties = policyGestioneToken.getDefaultProperties();
    		String tokenType = properties.getProperty(Costanti.POLICY_TOKEN_TYPE);
    		
    		String detailsError = null;
			InformazioniToken informazioniToken = null;
			Exception eProcess = null;
			
			ITokenParser tokenParser = policyGestioneToken.getValidazioneJWT_TokenParser();
			
    		if(Costanti.POLICY_TOKEN_TYPE_JWS.equals(tokenType)) {
    			// JWS Compact   			
    			JsonVerifySignature jsonCompactVerify = null;
    			try {
    				jsonCompactVerify = new JsonVerifySignature(policyGestioneToken.getProperties().get(Costanti.POLICY_VALIDAZIONE_JWS_VERIFICA_PROP_REF_ID),
    						JOSERepresentation.COMPACT);
    				if(jsonCompactVerify.verify(token)) {
    					informazioniToken = new InformazioniToken(jsonCompactVerify.getDecodedPayload(),tokenParser);
    				}
    				else {
    					detailsError = "Token non valido";
    				}
    			}catch(Exception e) {
    				detailsError = "Token non valido: "+e.getMessage();
    				eProcess = e;
    			}
    		}
    		else {
    			// JWE Compact
    			JsonDecrypt jsonDecrypt = null;
    			try {
    				jsonDecrypt = new JsonDecrypt(policyGestioneToken.getProperties().get(Costanti.POLICY_VALIDAZIONE_JWE_DECRYPT_PROP_REF_ID),
    						JOSERepresentation.COMPACT);
    				jsonDecrypt.decrypt(token);
    				informazioniToken = new InformazioniToken(jsonDecrypt.getDecodedPayload(),tokenParser);
    			}catch(Exception e) {
    				detailsError = "Token non valido: "+e.getMessage();
    				eProcess = e;
    			}
    		}
    		  		
    		if(informazioniToken!=null) {
    			esitoGestioneToken.setValido(true);
    			esitoGestioneToken.setInformazioniToken(informazioniToken);
    			esitoGestioneToken.setNoCache(false);
			}
    		else {
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
					esitoGestioneToken.setDetails("Token non validao");	
				}
    			esitoGestioneToken.setErrorMessage(buildErrorMessage(WWW_Authenticate_ErrorCode.invalid_token, policyGestioneToken.getRealm(), 
    					policyGestioneToken.isGenericError(), esitoGestioneToken.getDetails()));   			
    		}
    		
		}catch(Exception e){
			esitoGestioneToken.setDetails(e.getMessage());
			esitoGestioneToken.setEccezioneProcessamento(e);
    	}
		
		return esitoGestioneToken;
	}
	
	
	
	
	
	
	// ********* INTROSPECTION TOKEN ****************** */
	
	public static EsitoGestioneToken introspectionToken(AbstractDatiInvocazione datiInvocazione, String token, boolean portaDelegata) throws Exception {
		throw new Exception("Not Implemented");
	}
	
	
	
	
	
	
	// ********* USER INFO TOKEN ****************** */
	
	public static EsitoGestioneToken userInfoToken(AbstractDatiInvocazione datiInvocazione, String token, boolean portaDelegata) throws Exception {
		throw new Exception("Not Implemented");
	}
	
	
	
	
	
	
	// ********* FORWARD TOKEN ****************** */
	
	public static void forwardToken(AbstractDatiInvocazione datiInvocazione, EsitoPresenzaToken esitoPresenzaToken, boolean portaDelegata) throws Exception {
		throw new Exception("Not Implemented");
	}
	
	
	
	
	
	// ********* UTILITIES INTERNE ****************** */
	
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
					esitoGestioneToken.setValido(false);
					esitoGestioneToken.setDetails("Token expired");
					esitoGestioneToken.setErrorMessage(buildErrorMessage(WWW_Authenticate_ErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
	    					esitoGestioneToken.getDetails()));  
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
					esitoGestioneToken.setValido(false);
					SimpleDateFormat sdf = new SimpleDateFormat(format);
					esitoGestioneToken.setDetails("Token not usable before "+sdf.format(esitoGestioneToken.getInformazioniToken().getNbf()));
					esitoGestioneToken.setErrorMessage(buildErrorMessage(WWW_Authenticate_ErrorCode.invalid_token, policyGestioneToken.getRealm(), 
	    					false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
	    					esitoGestioneToken.getDetails()));  
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
						esitoGestioneToken.setValido(false);
						SimpleDateFormat sdf = new SimpleDateFormat(format);
						esitoGestioneToken.setDetails("Token expired; iat time '"+sdf.format(esitoGestioneToken.getInformazioniToken().getIat())+"' too old");
						esitoGestioneToken.setErrorMessage(buildErrorMessage(WWW_Authenticate_ErrorCode.invalid_token, policyGestioneToken.getRealm(), 
		    					false, // ritorno l'errore preciso in questo caso // policyGestioneToken.isGenericError(), 
		    					esitoGestioneToken.getDetails()));  
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
	
	private static String buildCacheKey(String funzione, boolean portaDelegata, String token) {
    	StringBuffer bf = new StringBuffer(funzione);
    	bf.append("_");
    	if(portaDelegata){
    		bf.append("PD");
    	}
    	else {
    		bf.append("PD");
    	}
    	bf.append("_");
    	bf.append(token);
    	return bf.toString();
    }
	
	public static OpenSPCoop2Message buildErrorMessage(WWW_Authenticate_ErrorCode errorCode, String realm, boolean genericError, String error, String ... scope) {
		
		OpenSPCoop2Message errorMessage = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(MessageType.BINARY, MessageRole.FAULT);
		ForcedResponseMessage forcedResponseMessage = new ForcedResponseMessage();
		forcedResponseMessage.setContent(null); // vuoto
		forcedResponseMessage.setContentType(null); // vuoto
		StringBuffer bf = new StringBuffer(HttpConstants.AUTHORIZATION_PREFIX_BEARER);
		bf.append("realm=\"");
		bf.append(realm);
		bf.append("\", error=\"");
		bf.append(errorCode.name());
		bf.append("\", error_description=\"");
		if(genericError==false) {
			bf.append(error);
		}
		switch (errorCode) {
		case invalid_request:
			forcedResponseMessage.setResponseCode("400");
			if(genericError) {
				bf.append("The request is missing a required token parameter");
			}
			break;
		case invalid_token:
			forcedResponseMessage.setResponseCode("401");
			if(genericError) {
				bf.append("Token invalid");
			}
			break;
		case insufficient_scope:
			forcedResponseMessage.setResponseCode("403");
			if(genericError) {
				bf.append("The request requires higher privileges than provided by the access token");
			}
			break;
		}
		bf.append("\"");
		if(scope!=null && scope.length>0) {
			bf.append(", scope=\"");
			for (int i = 0; i < scope.length; i++) {
				if(i>0) {
					bf.append(",");
				}
				bf.append(scope[i]);	
			}
			bf.append("\"");
		}
		
		forcedResponseMessage.setHeaders(new Properties());
		forcedResponseMessage.getHeaders().put(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, bf.toString());
		errorMessage.forceResponse(forcedResponseMessage);
		
		return errorMessage;
	}
	
	
}

enum WWW_Authenticate_ErrorCode {
	
	 invalid_request,
//     The request is missing a required parameter, includes an
//     unsupported parameter or parameter value, repeats the same
//     parameter, uses more than one method for including an access
//     token, or is otherwise malformed.  The resource server SHOULD
//     respond with the HTTP 400 (Bad Request) status code.

	 invalid_token,
//     The access token provided is expired, revoked, malformed, or
//     invalid for other reasons.  The resource SHOULD respond with
//     the HTTP 401 (Unauthorized) status code.  The client MAY
//     request a new access token and retry the protected resource
//     request.

	 insufficient_scope;
//     The request requires higher privileges than provided by the
//     access token.  The resource server SHOULD respond with the HTTP
//     403 (Forbidden) status code and MAY include the "scope"
//     attribute with the scope necessary to access the protected
//     resource.
}
