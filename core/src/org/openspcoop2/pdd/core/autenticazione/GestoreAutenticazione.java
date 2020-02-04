/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.core.autenticazione;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.IdCredenzialeMittente;
import org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.core.transazioni.utils.credenziali.AbstractCredenziale;
import org.openspcoop2.core.transazioni.utils.credenziali.AbstractSearchCredenziale;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeClientAddress;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeEventi;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeGruppi;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchClientAddress;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchEvento;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchGruppo;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchToken;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchTrasporto;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeToken;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeTrasporto;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.utils.WWWAuthenticateErrorCode;
import org.openspcoop2.message.utils.WWWAuthenticateGenerator;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autenticazione.pa.DatiInvocazionePortaApplicativa;
import org.openspcoop2.pdd.core.autenticazione.pa.EsitoAutenticazionePortaApplicativa;
import org.openspcoop2.pdd.core.autenticazione.pa.IAutenticazionePortaApplicativa;
import org.openspcoop2.pdd.core.autenticazione.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.pdd.core.autenticazione.pd.EsitoAutenticazionePortaDelegata;
import org.openspcoop2.pdd.core.autenticazione.pd.IAutenticazionePortaDelegata;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.token.Costanti;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * Classe utilizzata per la gestione del processo di autenticazione Buste
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreAutenticazione {

	
	/** Chiave della cache per l'autenticazione Buste  */
	private static final String AUTENTICAZIONE_CACHE_NAME = "autenticazione";
	/** Cache */
	private static Cache cacheAutenticazione = null;
	private static final Boolean semaphoreAutenticazionePD = true;
	private static final Boolean semaphoreAutenticazionePA = true;
	private static final Boolean semaphoreCredenzialiMittente = true;
	/** Logger log */
	private static Logger logger = null;
	private static Logger logConsole = OpenSPCoop2Logger.getLoggerOpenSPCoopConsole();

	/** ClassName */
	private static ClassNameProperties className = ClassNameProperties.getInstance();


	/* --------------- Cache --------------------*/
	public static void resetCache() throws AutenticazioneException{
		if(GestoreAutenticazione.cacheAutenticazione!=null){
			try{
				GestoreAutenticazione.cacheAutenticazione.clear();
			}catch(Exception e){
				throw new AutenticazioneException(e.getMessage(),e);
			}
		}
	}
	public static String printStatsCache(String separator) throws AutenticazioneException{
		try{
			if(GestoreAutenticazione.cacheAutenticazione!=null){
				return GestoreAutenticazione.cacheAutenticazione.printStats(separator);
			}
			else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new AutenticazioneException("Visualizzazione Statistiche riguardante la cache dell'autenticazione non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws AutenticazioneException{
		if(GestoreAutenticazione.cacheAutenticazione!=null)
			throw new AutenticazioneException("Cache gia' abilitata");
		else{
			try{
				GestoreAutenticazione.cacheAutenticazione = new Cache(GestoreAutenticazione.AUTENTICAZIONE_CACHE_NAME);
			}catch(Exception e){
				throw new AutenticazioneException(e.getMessage(),e);
			}
		}
	}
	public static void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond) throws AutenticazioneException{
		if(GestoreAutenticazione.cacheAutenticazione!=null)
			throw new AutenticazioneException("Cache gia' abilitata");
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
				
				GestoreAutenticazione.initCacheAutenticazione(dimensioneCacheInt, algoritmoCache, itemIdleTimeLong, itemLifeSecondLong, null);
			}catch(Exception e){
				throw new AutenticazioneException(e.getMessage(),e);
			}
		}
	}
	public static void disabilitaCache() throws AutenticazioneException{
		if(GestoreAutenticazione.cacheAutenticazione==null)
			throw new AutenticazioneException("Cache gia' disabilitata");
		else{
			try{
				GestoreAutenticazione.cacheAutenticazione.clear();
				GestoreAutenticazione.cacheAutenticazione = null;
			}catch(Exception e){
				throw new AutenticazioneException(e.getMessage(),e);
			}
		}
	}
	public static boolean isCacheAbilitata(){
		return GestoreAutenticazione.cacheAutenticazione != null;
	}
	public static String listKeysCache(String separator) throws AutenticazioneException{
		if(GestoreAutenticazione.cacheAutenticazione!=null){
			try{
				return GestoreAutenticazione.cacheAutenticazione.printKeys(separator);
			}catch(Exception e){
				throw new AutenticazioneException(e.getMessage(),e);
			}
		}else{
			throw new AutenticazioneException("Cache non abilitata");
		}
	}
	public static String getObjectCache(String key) throws AutenticazioneException{
		if(GestoreAutenticazione.cacheAutenticazione!=null){
			try{
				Object o = GestoreAutenticazione.cacheAutenticazione.get(key);
				if(o!=null){
					return o.toString();
				}else{
					return "oggetto con chiave ["+key+"] non presente";
				}
			}catch(Exception e){
				throw new AutenticazioneException(e.getMessage(),e);
			}
		}else{
			throw new AutenticazioneException("Cache non abilitata");
		}
	}
	public static void removeObjectCache(String key) throws AutenticazioneException{
		if(GestoreAutenticazione.cacheAutenticazione!=null){
			try{
				GestoreAutenticazione.cacheAutenticazione.remove(key);
			}catch(Exception e){
				throw new AutenticazioneException(e.getMessage(),e);
			}
		}else{
			throw new AutenticazioneException("Cache non abilitata");
		}
	}
	


	/*----------------- INIZIALIZZAZIONE --------------------*/
	public static void initialize(Logger log) throws Exception{
		GestoreAutenticazione.initialize(false, -1,null,-1l,-1l, log);
	}
	public static void initialize(int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{
		GestoreAutenticazione.initialize(true, dimensioneCache,algoritmoCache,idleTime,itemLifeSecond, log);
	}

	private static void initialize(boolean cacheAbilitata,int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{

		// Inizializzo log
		GestoreAutenticazione.logger = log;
		
		// Inizializzazione Cache
		if(cacheAbilitata){
			GestoreAutenticazione.initCacheAutenticazione(dimensioneCache, algoritmoCache, idleTime, itemLifeSecond, log);
		}

	}


	public static void initCacheAutenticazione(int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception {
		
		if(log!=null)
			log.info("Inizializzazione cache Autenticazione");

		GestoreAutenticazione.cacheAutenticazione = new Cache(GestoreAutenticazione.AUTENTICAZIONE_CACHE_NAME);

		if( (dimensioneCache>0) ||
				(algoritmoCache != null) ){

			if( dimensioneCache>0 ){
				try{
					String msg = "Dimensione della cache (Autenticazione) impostata al valore: "+dimensioneCache;
					if(log!=null)
						log.info(msg);
					GestoreAutenticazione.logConsole.info(msg);
					GestoreAutenticazione.cacheAutenticazione.setCacheSize(dimensioneCache);
				}catch(Exception error){
					throw new AutenticazioneException("Parametro errato per la dimensione della cache (Gestore Messaggi): "+error.getMessage(),error);
				}
			}
			if(algoritmoCache != null ){
				String msg = "Algoritmo di cache (Autenticazione) impostato al valore: "+algoritmoCache;
				if(log!=null)
					log.info(msg);
				GestoreAutenticazione.logConsole.info(msg);
				if(CostantiConfigurazione.CACHE_MRU.toString().equalsIgnoreCase(algoritmoCache))
					GestoreAutenticazione.cacheAutenticazione.setCacheAlgoritm(CacheAlgorithm.MRU);
				else
					GestoreAutenticazione.cacheAutenticazione.setCacheAlgoritm(CacheAlgorithm.LRU);
			}

		}

		if( idleTime > 0  ){
			try{
				String msg = "Attributo 'IdleTime' (Autenticazione) impostato al valore: "+idleTime;
				if(log!=null)
					log.info(msg);
				GestoreAutenticazione.logConsole.info(msg);
				GestoreAutenticazione.cacheAutenticazione.setItemIdleTime(idleTime);
			}catch(Exception error){
				throw new AutenticazioneException("Parametro errato per l'attributo 'IdleTime' (Gestore Messaggi): "+error.getMessage(),error);
			}
		}

		try{
			String msg = "Attributo 'MaxLifeSecond' (Autenticazione) impostato al valore: "+itemLifeSecond;
			if(log!=null)
				log.info(msg);
			GestoreAutenticazione.logConsole.info(msg);
			GestoreAutenticazione.cacheAutenticazione.setItemLifeTime(itemLifeSecond);
		}catch(Exception error){
			throw new AutenticazioneException("Parametro errato per l'attributo 'MaxLifeSecond' (Gestore Messaggi): "+error.getMessage(),error);
		}

	}
	
	public static void disableSyncronizedGet() throws UtilsException {
		if(GestoreAutenticazione.cacheAutenticazione==null) {
			throw new UtilsException("Cache disabled");
		}
		GestoreAutenticazione.cacheAutenticazione.disableSyncronizedGet();
	}
	public static boolean isDisableSyncronizedGet() throws UtilsException {
		if(GestoreAutenticazione.cacheAutenticazione==null) {
			throw new UtilsException("Cache disabled");
		}
		return GestoreAutenticazione.cacheAutenticazione.isDisableSyncronizedGet();
	}
	

    public static EsitoAutenticazionePortaDelegata verificaAutenticazionePortaDelegata(String tipoAutenticazione, 
    		DatiInvocazionePortaDelegata datiInvocazione, ParametriAutenticazione parametriAutenticazione,
 		  PdDContext pddContext,IProtocolFactory<?> protocolFactory, OpenSPCoop2Message msg) throws Exception{
    	return _verificaAutenticazionePortaDelegata(false, tipoAutenticazione, 
    			datiInvocazione, parametriAutenticazione, 
    			pddContext, protocolFactory, msg);
    }
    public static EsitoAutenticazionePortaDelegata verificaAutenticazioneMessageBox(String tipoAutenticazione, 
    		DatiInvocazionePortaDelegata datiInvocazione, ParametriAutenticazione parametriAutenticazione,
   		  PdDContext pddContext,IProtocolFactory<?> protocolFactory) throws Exception{
    	return _verificaAutenticazionePortaDelegata(true, tipoAutenticazione, 
    			datiInvocazione, parametriAutenticazione, 
    			pddContext, protocolFactory, null);
    }
    
    private static EsitoAutenticazionePortaDelegata _verificaAutenticazionePortaDelegata(boolean messageBox, String tipoAutenticazione, 
    		DatiInvocazionePortaDelegata datiInvocazione, ParametriAutenticazione parametriAutenticazione,
   		  PdDContext pddContext,IProtocolFactory<?> protocolFactory, OpenSPCoop2Message msg) throws Exception{
    	
    	checkDatiPortaDelegata(messageBox,datiInvocazione);
    	
    	IAutenticazionePortaDelegata auth = newInstanceAuthPortaDelegata(tipoAutenticazione, pddContext, protocolFactory);
    	auth.initParametri(parametriAutenticazione);
    	
    	try {
	    	if(GestoreAutenticazione.cacheAutenticazione==null || !auth.saveAuthenticationResultInCache()){
	    		return auth.process(datiInvocazione);
			}
	    	else{
	    		String keyCache = buildCacheKey(messageBox, true, tipoAutenticazione, datiInvocazione.getKeyCache(), auth.getSuffixKeyAuthenticationResultInCache(datiInvocazione));
	
	    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
	    		org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreAutenticazione.cacheAutenticazione.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreAutenticazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutenticazionePortaDelegata) in cache.");
						EsitoAutenticazionePortaDelegata esito = (EsitoAutenticazionePortaDelegata) response.getObject();
						esito.setEsitoPresenteInCache(true);
						return esito;
					}else if(response.getException()!=null){
						GestoreAutenticazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutenticazionePortaDelegata) in cache.");
						throw (Exception) response.getException();
					}else{
						GestoreAutenticazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
	    		
				synchronized (GestoreAutenticazione.semaphoreAutenticazionePD) {
	
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreAutenticazione.cacheAutenticazione.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreAutenticazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutenticazionePortaDelegata) in cache.");
							EsitoAutenticazionePortaDelegata esito = (EsitoAutenticazionePortaDelegata) response.getObject();
							esito.setEsitoPresenteInCache(true);
							return esito;
						}else if(response.getException()!=null){
							GestoreAutenticazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutenticazionePortaDelegata) in cache.");
							throw (Exception) response.getException();
						}else{
							GestoreAutenticazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
	
					// Effettuo la query
					GestoreAutenticazione.logger.debug("oggetto con chiave ["+keyCache+"] (method:verificaAutenticazionePortaDelegata) ricerco nella configurazione...");
					EsitoAutenticazionePortaDelegata esito = auth.process(datiInvocazione);
	
					// Aggiungo la risposta in cache (se esiste una cache)	
					// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
					// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
					// - impostare il noCache a true
					if(esito!=null){
						if(!esito.isNoCache()){
							GestoreAutenticazione.logger.info("Aggiungo oggetto ["+keyCache+"] in cache");
							try{	
								org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
								responseCache.setObject(esito);
								GestoreAutenticazione.cacheAutenticazione.put(keyCache,responseCache);
							}catch(UtilsException e){
								GestoreAutenticazione.logger.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
							}
						}
						return esito;
					}else{
						throw new AutenticazioneException("Metodo (GestoreAutenticazione.autenticazionePortaDelegata.process) ha ritornato un valore di esito null");
					}
				}
	    	}
    	}finally {
    		if(msg!=null) {
    			auth.cleanPostAuth(msg);
    		}
    	}
    }
	
    public static EsitoAutenticazionePortaApplicativa verificaAutenticazionePortaApplicativa(String tipoAutenticazione, 
    		DatiInvocazionePortaApplicativa datiInvocazione, ParametriAutenticazione parametriAutenticazione,
    		PdDContext pddContext,IProtocolFactory<?> protocolFactory, OpenSPCoop2Message msg) throws Exception{
    	  
    	checkDatiPortaApplicativa(datiInvocazione);
    	
    	IAutenticazionePortaApplicativa auth = newInstanceAuthPortaApplicativa(tipoAutenticazione, pddContext, protocolFactory);
    	auth.initParametri(parametriAutenticazione);
    	
    	try {
	    	if(GestoreAutenticazione.cacheAutenticazione==null || !auth.saveAuthenticationResultInCache()){
	    		return auth.process(datiInvocazione);
			}
	    	else{
	    		String keyCache = buildCacheKey(false, false, tipoAutenticazione, datiInvocazione.getKeyCache(), auth.getSuffixKeyAuthenticationResultInCache(datiInvocazione));
	
	    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
				org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreAutenticazione.cacheAutenticazione.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreAutenticazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutenticazionePortaApplicativa) in cache.");
						EsitoAutenticazionePortaApplicativa esito = (EsitoAutenticazionePortaApplicativa) response.getObject();
						esito.setEsitoPresenteInCache(true);
						return esito;
					}else if(response.getException()!=null){
						GestoreAutenticazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutenticazionePortaApplicativa) in cache.");
						throw (Exception) response.getException();
					}else{
						GestoreAutenticazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
	    		
				synchronized (GestoreAutenticazione.semaphoreAutenticazionePA) {
	
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreAutenticazione.cacheAutenticazione.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreAutenticazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutenticazionePortaApplicativa) in cache.");
							EsitoAutenticazionePortaApplicativa esito = (EsitoAutenticazionePortaApplicativa) response.getObject();
							esito.setEsitoPresenteInCache(true);
							return esito;
						}else if(response.getException()!=null){
							GestoreAutenticazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutenticazionePortaApplicativa) in cache.");
							throw (Exception) response.getException();
						}else{
							GestoreAutenticazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
	
					// Effettuo la query
					GestoreAutenticazione.logger.debug("oggetto con chiave ["+keyCache+"] (method:verificaAutenticazionePortaApplicativa) ricerco nella configurazione...");
					EsitoAutenticazionePortaApplicativa esito = auth.process(datiInvocazione);
	
					// Aggiungo la risposta in cache (se esiste una cache)	
					// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
					// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
					// - impostare il noCache a true
					if(esito!=null){
						if(!esito.isNoCache()){
							GestoreAutenticazione.logger.info("Aggiungo oggetto ["+keyCache+"] in cache");
							try{	
								org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
								responseCache.setObject(esito);
								GestoreAutenticazione.cacheAutenticazione.put(keyCache,responseCache);
							}catch(UtilsException e){
								GestoreAutenticazione.logger.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
							}
						}
						return esito;
					}else{
						throw new AutenticazioneException("Metodo (GestoreAutenticazione.autenticazionePortaApplicativa.process) ha ritornato un valore di esito null");
					}
				}
	    	}
    	}finally {
    		if(msg!=null) {
    			auth.cleanPostAuth(msg);
    		}
    	}
    }
        
    private static void checkDatiPortaDelegata(boolean messageBox, DatiInvocazionePortaDelegata datiInvocazione) throws AutenticazioneException{
    	
    	InfoConnettoreIngresso infoConnettoreIngresso = datiInvocazione.getInfoConnettoreIngresso();
    	if(infoConnettoreIngresso==null)
			throw new AutenticazioneException("(Parametri) InfoConnettoreIngresso non definito");
		if(infoConnettoreIngresso.getCredenziali()==null)
			throw new AutenticazioneException("(Parametri) InfoConnettoreIngresso.credenziali non definito");
		
		if(messageBox==false){
	    	IDPortaDelegata idPD = datiInvocazione.getIdPD();
	    	if(idPD==null)
				throw new AutenticazioneException("(Parametri) IDPortaDelegata non definito");
	    	if(idPD.getNome()==null)
				throw new AutenticazioneException("(Parametri) IDPortaDelegata.nome non definito");
			
			PortaDelegata pd = datiInvocazione.getPd();
			if(pd==null)
				throw new AutenticazioneException("(Parametri) PortaDelegata non definito");
		}
		
    }
    
    private static void checkDatiPortaApplicativa(DatiInvocazionePortaApplicativa datiInvocazione) throws AutenticazioneException{
    	
    	InfoConnettoreIngresso infoConnettoreIngresso = datiInvocazione.getInfoConnettoreIngresso();
    	if(infoConnettoreIngresso==null)
			throw new AutenticazioneException("(Parametri) InfoConnettoreIngresso non definito");
		if(infoConnettoreIngresso.getCredenziali()==null)
			throw new AutenticazioneException("(Parametri) InfoConnettoreIngresso.credenziali non definito");

		// Dipende dal ruolo della Busta, se c'e' la porta applicativa o delegata
//    	IDPortaApplicativaByNome idPA = datiInvocazione.getIdPA();
//    	if(idPA==null)
//			throw new AutenticazioneException("(Parametri) IDPortaApplicativaByNome non definito");
//    	if(idPA.getNome()==null)
//			throw new AutenticazioneException("(Parametri) IDPortaApplicativaByNome.nome non definito");
//    	IDSoggetto idSoggettoProprietarioPA = idPA.getSoggetto();
//		if(idSoggettoProprietarioPA==null || idSoggettoProprietarioPA.getTipo()==null || idSoggettoProprietarioPA.getNome()==null)
//			throw new AutenticazioneException("(Parametri) IDPortaApplicativaByNome.Soggetto non definito");
//		
//		PortaApplicativa pa = datiInvocazione.getPa();
//		if(pa==null)
//			throw new AutenticazioneException("(Parametri) PortaApplicativa non definito");
		
    }
    
    private static String buildCacheKey(boolean messageBox, boolean portaDelegata, String tipoAutenticazione, String keyCache, String suffixKeyCache) throws AutenticazioneException{
    	StringBuilder bf = new StringBuilder();
    	
    	if(portaDelegata){
    		if(messageBox){
    			bf.append("MessageBox ");
    		}
    		else{
    			bf.append("PD ");
    		}
    	}else{
    		bf.append("PA ");
    	}
    	
    	bf.append(" Auth:").append(tipoAutenticazione).append(" ");
    	
    	bf.append(keyCache);
    	
    	if(suffixKeyCache!=null && !"".equals(suffixKeyCache)){
    		bf.append(" ");
    		bf.append(suffixKeyCache);
    	}
    	
    	return bf.toString();
    }
    
    private static IAutenticazionePortaDelegata newInstanceAuthPortaDelegata(String tipoAutenticazione,PdDContext pddContext, IProtocolFactory<?> protocolFactory) throws AutenticazioneException{
    	String classType = null; 
		IAutenticazionePortaDelegata auth = null;
		try{
			classType = GestoreAutenticazione.className.getAutenticazionePortaDelegata(tipoAutenticazione);
			auth = (IAutenticazionePortaDelegata) Loader.getInstance().newInstance(classType);
			AbstractCore.init(auth, pddContext, protocolFactory);
			return auth;
		}catch(Exception e){
			throw new AutenticazioneException("Riscontrato errore durante il caricamento della classe ["+classType+
					"] da utilizzare per l'autenticazione via PD: "+e.getMessage(),e);
		}
    }
    
    private static IAutenticazionePortaApplicativa newInstanceAuthPortaApplicativa(String tipoAutenticazione,PdDContext pddContext, IProtocolFactory<?> protocolFactory) throws AutenticazioneException{
    	String classType = null; 
    	IAutenticazionePortaApplicativa auth = null;
		try{
			classType = GestoreAutenticazione.className.getAutenticazionePortaApplicativa(tipoAutenticazione);
			auth = (IAutenticazionePortaApplicativa) Loader.getInstance().newInstance(classType);
			AbstractCore.init(auth, pddContext, protocolFactory);
			return auth;
		}catch(Exception e){
			throw new AutenticazioneException("Riscontrato errore durante il caricamento della classe ["+classType+
					"] da utilizzare per l'autenticazione delle buste via PA: "+e.getMessage(),e);
		}
    }
    
    public static boolean isAutenticazioneTokenEnabled(GestioneTokenAutenticazione gestioneTokenAutenticazione) {
    	return
				StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getIssuer()) ||
				StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getClientId()) ||
				StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getSubject()) ||
				StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getUsername()) ||
				StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getEmail());
    }
    
    public static String getLabel(GestioneTokenAutenticazione gestioneTokenAutenticazione) {
    	return _getActions(gestioneTokenAutenticazione);
    }
    public static String getActions(GestioneTokenAutenticazione gestioneTokenAutenticazione) {
    	return _getActions(gestioneTokenAutenticazione);
    }
    private static String _getActions(GestioneTokenAutenticazione gestioneTokenAutenticazione) {
    	StringBuilder bf = new StringBuilder();
    	if(StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getIssuer())) {
    		if(bf.length()>0) {
    			bf.append(",");
    		}
    		bf.append(Costanti.GESTIONE_TOKEN_AUTENTICAZIONE_ISSUER);
    	}
    	if(StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getClientId())) {
    		if(bf.length()>0) {
    			bf.append(",");
    		}
    		bf.append(Costanti.GESTIONE_TOKEN_AUTENTICAZIONE_CLIENT_ID);
    	}
    	if(StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getSubject())) {
    		if(bf.length()>0) {
    			bf.append(",");
    		}
    		bf.append(Costanti.GESTIONE_TOKEN_AUTENTICAZIONE_SUBJECT);
    	}
    	if(StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getUsername())) {
    		if(bf.length()>0) {
    			bf.append(",");
    		}
    		bf.append(Costanti.GESTIONE_TOKEN_AUTENTICAZIONE_USERNAME);
    	}
    	if(StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getEmail())) {
    		if(bf.length()>0) {
    			bf.append(",");
    		}
    		bf.append(Costanti.GESTIONE_TOKEN_AUTENTICAZIONE_EMAIL);
    	}
    	return bf.toString();
    }
    
    public static EsitoAutenticazionePortaDelegata verificaAutenticazioneTokenPortaDelegata(GestioneTokenAutenticazione tipoAutenticazione, DatiInvocazionePortaDelegata datiInvocazione,
    		PdDContext pddContext,IProtocolFactory<?> protocolFactory, OpenSPCoop2Message msg) throws Exception{
    	
    	EsitoAutenticazionePortaDelegata esito = new EsitoAutenticazionePortaDelegata();
      	esito.setClientAuthenticated(true);
      	
      	esito = (EsitoAutenticazionePortaDelegata) autenticazioneToken(tipoAutenticazione, esito, pddContext, datiInvocazione);
      	if(esito.isClientAuthenticated()==false) {
      		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_445_TOKEN_AUTORIZZAZIONE_FALLITA.getErroreIntegrazione());
    	}
      	return esito;
    }
    
    public static EsitoAutenticazionePortaApplicativa verificaAutenticazioneTokenPortaApplicativa(GestioneTokenAutenticazione tipoAutenticazione, DatiInvocazionePortaApplicativa datiInvocazione,
    		PdDContext pddContext,IProtocolFactory<?> protocolFactory, OpenSPCoop2Message msg) throws Exception{
    	
    	EsitoAutenticazionePortaApplicativa esito = new EsitoAutenticazionePortaApplicativa();
      	esito.setClientAuthenticated(true);
      	
      	esito = (EsitoAutenticazionePortaApplicativa) autenticazioneToken(tipoAutenticazione, esito, pddContext, datiInvocazione);
      	if(esito.isClientAuthenticated()==false) {
      		esito.setErroreCooperazione(ErroriCooperazione.TOKEN_AUTORIZZAZIONE_FALLITA.getErroreCooperazione());
    	}
      	return esito;
    	
    }
    
    private static EsitoAutenticazione autenticazioneToken(GestioneTokenAutenticazione tipoAutenticazione, EsitoAutenticazione esito, 
    		PdDContext pddContext, AbstractDatiInvocazione datiInvocazione) throws Exception {
    	
    	boolean autenticato = true;
		String errorMessage = null;
    	if(tipoAutenticazione!=null) {
    		
    		boolean issuer = StatoFunzionalita.ABILITATO.equals(tipoAutenticazione.getIssuer());
    		boolean clientId = StatoFunzionalita.ABILITATO.equals(tipoAutenticazione.getClientId());
    		boolean subject = StatoFunzionalita.ABILITATO.equals(tipoAutenticazione.getSubject());
    		boolean username = StatoFunzionalita.ABILITATO.equals(tipoAutenticazione.getUsername());
    		boolean email = StatoFunzionalita.ABILITATO.equals(tipoAutenticazione.getEmail());
    		
    		if(issuer || clientId || subject || username ||email ) {
    				
	    		InformazioniToken informazioniTokenNormalizzate = null;
	    		Object oInformazioniTokenNormalizzate = pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
	    		if(oInformazioniTokenNormalizzate!=null) {
	    			informazioniTokenNormalizzate = (InformazioniToken) oInformazioniTokenNormalizzate;
	    		}
	    	
	    		if(issuer) {
	    			if(informazioniTokenNormalizzate.getIss()==null || "".equals(informazioniTokenNormalizzate.getIss())) {
	    				autenticato = false;
		    			errorMessage = "Token without issuer claim";
	    			}
	    		}
	    		
	    		if(clientId) {
	    			if(informazioniTokenNormalizzate.getClientId()==null || "".equals(informazioniTokenNormalizzate.getClientId())) {
	    				autenticato = false;
		    			errorMessage = "Token without clientId claim";
	    			}
	    		}
	    		
	    		if(subject) {
	    			if(informazioniTokenNormalizzate.getSub()==null || "".equals(informazioniTokenNormalizzate.getSub())) {
	    				autenticato = false;
		    			errorMessage = "Token without subject claim";
	    			}
	    		}
	    		
	    		if(username) {
	    			if(informazioniTokenNormalizzate.getUsername()==null || "".equals(informazioniTokenNormalizzate.getUsername())) {
	    				autenticato = false;
		    			errorMessage = "Token without username claim";
	    			}
	    		}
	    		
	    		if(email) {
	    			if(informazioniTokenNormalizzate.getUserInfo()==null || 
	    					informazioniTokenNormalizzate.getUserInfo().getEMail()==null || 
	    					"".equals(informazioniTokenNormalizzate.getUserInfo().getEMail())) {
	    				autenticato = false;
		    			errorMessage = "Token without email claim";
	    			}
	    		}
			}
    		
    	}
    	
    	if(!autenticato) {
    		
    		esito.setDetails(errorMessage);
    		
    		String realm = "OpenSPCoop";
			Object oRealm = pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_REALM);
    		if(oRealm!=null) {
    			realm = (String) oRealm;
    		}
			
			boolean emptyMessage = false;
			Object oEmptyMessage = pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_MESSAGE_ERROR_BODY_EMPTY);
    		if(oEmptyMessage!=null) {
    			emptyMessage = (Boolean) oEmptyMessage;
    		}
    		
    		boolean genericMessage = false;
			Object oGenericMessage = pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_MESSAGE_ERROR_GENERIC_MESSAGE);
    		if(oGenericMessage!=null) {
    			genericMessage = (Boolean) oGenericMessage;
    		}
			
    		esito.setClientAuthenticated(false);
    		if(emptyMessage) {
    			esito.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.insufficient_scope, realm, genericMessage, errorMessage));
    		}
    		else {
    			esito.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.insufficient_scope, realm, genericMessage, errorMessage));
    		}
		}
		
    	
    	return esito;
    }
    
    
    public static void updateCredenzialiTrasporto(IDSoggetto dominio, String modulo, String idTransazione, 
    		String tipoAutenticazione, String credential, CredenzialiMittente credenzialiMittente) throws Exception{
    	
    	CredenzialeSearchTrasporto trasportoSearch = new CredenzialeSearchTrasporto(tipoAutenticazione);
    	trasportoSearch.disableConvertToDBValue();
		CredenzialeTrasporto trasporto = new CredenzialeTrasporto(tipoAutenticazione, credential);
    	credenzialiMittente.setTrasporto(getCredenzialeMittente(dominio, modulo, idTransazione, 
    			trasportoSearch, trasporto));
    	
    }
    
    public static void updateCredenzialiToken(IDSoggetto dominio, String modulo, String idTransazione, 
    		InformazioniToken informazioniTokenNormalizzate, CredenzialiMittente credenzialiMittente) throws Exception{
    	
    	if(informazioniTokenNormalizzate.getIss()!=null) {
    		CredenzialeSearchToken tokenSearch = new CredenzialeSearchToken(TipoCredenzialeMittente.token_issuer);
    		tokenSearch.disableConvertToDBValue();
    		CredenzialeToken token = new CredenzialeToken(TipoCredenzialeMittente.token_issuer, informazioniTokenNormalizzate.getIss());
    		credenzialiMittente.setToken_issuer(getCredenzialeMittente(dominio, modulo, idTransazione, 
    				tokenSearch, token));
    	}
    	if(informazioniTokenNormalizzate.getClientId()!=null) {
    		CredenzialeSearchToken tokenSearch = new CredenzialeSearchToken(TipoCredenzialeMittente.token_clientId);
    		tokenSearch.disableConvertToDBValue();
    		CredenzialeToken token = new CredenzialeToken(TipoCredenzialeMittente.token_clientId, informazioniTokenNormalizzate.getClientId());
    		credenzialiMittente.setToken_clientId(getCredenzialeMittente(dominio, modulo, idTransazione, 
    				tokenSearch, token));
    	}
    	if(informazioniTokenNormalizzate.getSub()!=null) {
    		CredenzialeSearchToken tokenSearch = new CredenzialeSearchToken(TipoCredenzialeMittente.token_subject);
    		tokenSearch.disableConvertToDBValue();
    		CredenzialeToken token = new CredenzialeToken(TipoCredenzialeMittente.token_subject, informazioniTokenNormalizzate.getSub());
    		credenzialiMittente.setToken_subject(getCredenzialeMittente(dominio, modulo, idTransazione, 
    				tokenSearch, token));
    	}
    	if(informazioniTokenNormalizzate.getUsername()!=null) {
    		CredenzialeSearchToken tokenSearch = new CredenzialeSearchToken(TipoCredenzialeMittente.token_username);
    		tokenSearch.disableConvertToDBValue();
    		CredenzialeToken token = new CredenzialeToken(TipoCredenzialeMittente.token_username, informazioniTokenNormalizzate.getUsername());
    		credenzialiMittente.setToken_username(getCredenzialeMittente(dominio, modulo, idTransazione, 
    				tokenSearch, token));
    	}
    	if(informazioniTokenNormalizzate.getUserInfo()!=null && informazioniTokenNormalizzate.getUserInfo().getEMail()!=null) {
    		CredenzialeSearchToken tokenSearch = new CredenzialeSearchToken(TipoCredenzialeMittente.token_eMail);
    		tokenSearch.disableConvertToDBValue();
    		CredenzialeToken token = new CredenzialeToken(TipoCredenzialeMittente.token_eMail, informazioniTokenNormalizzate.getUserInfo().getEMail());
    		credenzialiMittente.setToken_eMail(getCredenzialeMittente(dominio, modulo, idTransazione, 
    				tokenSearch, token));
    	}
    	
    }
    
    public static CredenzialeMittente convertGruppiToCredenzialiMittenti(IDSoggetto dominio, String modulo, String idTransazione,
    		List<String> gruppiList) throws Exception{
    	if(gruppiList!=null && !gruppiList.isEmpty()) {
	    	CredenzialeSearchGruppo gruppiSearch = new CredenzialeSearchGruppo();
	    	gruppiSearch.disableConvertToDBValue();
	    	CredenzialeGruppi gruppi = new CredenzialeGruppi(gruppiList);
	    	return getCredenzialeMittente(dominio, modulo, idTransazione, 
	    			gruppiSearch, gruppi);
    	}
    	return null;
    }
    
    public static CredenzialeMittente convertEventiToCredenzialiMittenti(IDSoggetto dominio, String modulo, String idTransazione,
    		List<String> eventiList) throws Exception{
    	if(eventiList!=null && !eventiList.isEmpty()) {
	    	CredenzialeSearchEvento eventiSearch = new CredenzialeSearchEvento();
	    	eventiSearch.disableConvertToDBValue();
	    	CredenzialeEventi eventi = new CredenzialeEventi(eventiList);
	    	return getCredenzialeMittente(dominio, modulo, idTransazione, 
	    			eventiSearch, eventi);
    	}
    	return null;
    }
    
    public static CredenzialeMittente convertClientCredentialToCredenzialiMittenti(IDSoggetto dominio, String modulo, String idTransazione,
    		String socketAddress, String transportAddress) throws Exception{
    	boolean socketAddressDefined = socketAddress!=null && !"".equals(socketAddress);
		boolean transportAddressDefined = transportAddress!=null && !"".equals(transportAddress);
    	if(socketAddressDefined || transportAddressDefined) {
	    	CredenzialeSearchClientAddress clientAddressSearch = new CredenzialeSearchClientAddress(socketAddressDefined, transportAddressDefined, true);
	    	clientAddressSearch.disableConvertToDBValue();
	    	CredenzialeClientAddress clientAddress = new CredenzialeClientAddress(socketAddress, transportAddress);
	    	return getCredenzialeMittente(dominio, modulo, idTransazione, 
	    			clientAddressSearch, clientAddress);
    	}
    	return null;
    }
    
    private static CredenzialeMittente getCredenzialeMittente(IDSoggetto dominio, String modulo, String idTransazione, 
    		AbstractSearchCredenziale searchCredential, AbstractCredenziale credentialParam) throws Exception{
      	
    	if(dominio==null)
			throw new AutenticazioneException("(Parametri) dominio non definito");
    	if(modulo==null)
			throw new AutenticazioneException("(Parametri) modulo non definito");
    	if(idTransazione==null)
			throw new AutenticazioneException("(Parametri) idTransazione non definito");
    	if(searchCredential==null)
			throw new AutenticazioneException("(Parametri) searchCredential non definito");
		if(credentialParam==null)
			throw new AutenticazioneException("(Parametri) credenziali non definite");
		      	
		String credential = credentialParam.getCredenziale();
		int maxLengthCredenziali = OpenSPCoop2Properties.getInstance().getTransazioniCredenzialiMittenteMaxLength();
		int maxLifeSeconds = OpenSPCoop2Properties.getInstance().getTransazioniCredenzialiMittenteLifeSeconds();
		Date scadenzaEntry = new Date(DateManager.getTimeMillis()-(maxLifeSeconds*1000));
		if(credential.length()>maxLengthCredenziali) {
			logger.error("Attenzione: credenziale '"+searchCredential.getTipo()+"' ricevuta supera la dimensione massima consentita '"+maxLengthCredenziali+"'. Verrà salvata troncata a tale dimensione. Credenziale: '"+credential+"'");
			credential = credential.substring(0,maxLengthCredenziali);
			try {
				credentialParam.updateCredenziale(credential);
			}catch(Exception e) {
				logger.error("Non è possibile troncare la credenziale di tipo '"+searchCredential.getTipo()+"', l'informazione non verrà salvata");
			}
		}
		
    	if(GestoreAutenticazione.cacheAutenticazione==null){
    		return _getCredenzialeMittente(dominio, modulo, idTransazione, scadenzaEntry, searchCredential, credentialParam);
		}
    	else{
    		String keyCache = buildCacheKey(searchCredential, credential);

    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreAutenticazione.cacheAutenticazione.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					GestoreAutenticazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:getCredenzialeMittente) in cache.");
					CredenzialeMittente credenziale = (CredenzialeMittente) response.getObject();
					if(credenziale.getOraRegistrazione().after(scadenzaEntry)) {
						return credenziale; // informazione in cache valida
					}
				}else if(response.getException()!=null){
					GestoreAutenticazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:getCredenzialeMittente) in cache.");
					throw (Exception) response.getException();
				}else{
					GestoreAutenticazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
				}
			}
    		
			synchronized (GestoreAutenticazione.semaphoreCredenzialiMittente) {

				response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreAutenticazione.cacheAutenticazione.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreAutenticazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:getCredenzialeMittente) in cache.");
						CredenzialeMittente credenziale = (CredenzialeMittente) response.getObject();
						if(credenziale.getOraRegistrazione().after(scadenzaEntry)) {
							return credenziale; // informazione in cache valida
						}
					}else if(response.getException()!=null){
						GestoreAutenticazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:getCredenzialeMittente) in cache.");
						throw (Exception) response.getException();
					}else{
						GestoreAutenticazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}

				// Effettuo la query
				GestoreAutenticazione.logger.debug("oggetto con chiave ["+keyCache+"] (method:getCredenzialeMittente) ricerco nella configurazione...");
				CredenzialeMittente credenziale = _getCredenzialeMittente(dominio, modulo, idTransazione, scadenzaEntry, searchCredential, credentialParam);

				// Aggiungo la risposta in cache (se esiste una cache)	
				// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
				// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
				// - impostare il noCache a true
				if(credenziale!=null){
					GestoreAutenticazione.logger.info("Aggiungo oggetto ["+keyCache+"] in cache");
					try{	
						org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
						responseCache.setObject(credenziale);
						GestoreAutenticazione.cacheAutenticazione.put(keyCache,responseCache);
					}catch(UtilsException e){
						GestoreAutenticazione.logger.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
					}
					return credenziale;
				}else{
					throw new AutenticazioneException("Metodo (GestoreAutenticazione.getCredenzialeMittente) ha ritornato un valore di esito null");
				}
			}
    	}
    }
    
    private static String buildCacheKey(AbstractSearchCredenziale searchCredential, String credential) throws AutenticazioneException{
    	StringBuilder bf = new StringBuilder();

    	bf.append(searchCredential.getTipo()).append(" ");
    	
    	bf.append(credential);
    	
    	return bf.toString();
    }
    
    private static CredenzialeMittente _getCredenzialeMittente(IDSoggetto dominio, String modulo, String idTransazione, 
    		Date scadenzaEntry,
    		AbstractSearchCredenziale searchCredential, AbstractCredenziale credential) throws Exception {
    	DBTransazioniManager dbManager = null;
    	Resource r = null;
    	try{
			dbManager = DBTransazioniManager.getInstance();
			r = dbManager.getResource(dominio, modulo+".credenzialeMittente", idTransazione);
			if(r==null){
				throw new Exception("Risorsa al database non disponibile");
			}
			Connection con = (Connection) r.getResource();
			if(con == null)
				throw new Exception("Connessione non disponibile");	

			boolean debug = OpenSPCoop2Properties.getInstance().isTransazioniDebug();
			Logger logSql =  OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSql(debug);
			DAOFactory daoFactory = DAOFactory.getInstance(logSql);
			DAOFactoryProperties daoFactoryProperties = DAOFactoryProperties.getInstance(logSql);
			ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
			daoFactoryServiceManagerPropertiesTransazioni.setShowSql(debug);
			daoFactoryServiceManagerPropertiesTransazioni.setDatabaseType(DBTransazioniManager.getInstance().getTipoDatabase());
			
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM = 
					(org.openspcoop2.core.transazioni.dao.IServiceManager) 
						daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), con, daoFactoryServiceManagerPropertiesTransazioni, logSql);	

			ICredenzialeMittenteService credenzialiMittenteService = transazioniSM.getCredenzialeMittenteService();
			boolean ricercaEsatta = true;
	    	boolean caseSensitive = true;
	    	IPaginatedExpression pagEpression = searchCredential.createExpression(credenzialiMittenteService, credential.getCredenziale(), ricercaEsatta, caseSensitive);
			List<CredenzialeMittente> list = credenzialiMittenteService.findAll(pagEpression);
			if(list==null || list.size()<=0) {
				// not exists
				return createCredenzialeMittente(credenzialiMittenteService, scadenzaEntry, searchCredential, credential);
			}
			else if(list.size()>1) {
				throw new Exception("Trovata più di un'occorrenza di credenziale di tipo '"+searchCredential.getTipo()+"'; credenziale: ["+credential+"]");
			}
			else {
				CredenzialeMittente credenziale = list.get(0);
				if(credenziale.getOraRegistrazione().after(scadenzaEntry)) {
					return credenziale; // informazione in cache valida
				}
				else {
					return updateDataCredenzialeMittente(credenzialiMittenteService, scadenzaEntry, credenziale);
				}
			}
			
		}finally{
			try{
				if(r!=null)
					dbManager.releaseResource(dominio, modulo, r);
			}catch(Exception eClose){}
		}
    }
    
    private static synchronized CredenzialeMittente createCredenzialeMittente(ICredenzialeMittenteService credenzialeMittentiService,
    		Date scadenzaEntry,
    		AbstractSearchCredenziale searchCredential, AbstractCredenziale credential) throws Exception {
    	boolean ricercaEsatta = true;
    	boolean caseSensitive = true;
    	IPaginatedExpression pagEpression = searchCredential.createExpression(credenzialeMittentiService, credential.getCredenziale(), ricercaEsatta, caseSensitive); 
    	List<CredenzialeMittente> list = credenzialeMittentiService.findAll(pagEpression);
		if(list==null || list.size()<=0) {
			// not exists
			CredenzialeMittente credenzialeMittente = new CredenzialeMittente();
			credenzialeMittente.setTipo(credential.getTipo());
			credenzialeMittente.setOraRegistrazione(DateManager.getDate());
			credenzialeMittente.setCredenziale(credential.getCredenziale());
			credenzialeMittentiService.create(credenzialeMittente);
			return credenzialeMittente;
		}
		else if(list.size()>1) {
			throw new Exception("Trovata più di un'occorrenza di credenziale di tipo '"+searchCredential.getTipo()+"'; credenziale: ["+credential.getCredenziale()+"]");
		}
		else {
			CredenzialeMittente credenziale = list.get(0);
			if(credenziale.getOraRegistrazione().after(scadenzaEntry)) {
				return credenziale; // informazione in cache valida
			}
			else {
				return updateDataCredenzialeMittente(credenzialeMittentiService, scadenzaEntry, credenziale);
			}
		}
    }
    private static synchronized CredenzialeMittente updateDataCredenzialeMittente(ICredenzialeMittenteService credenzialeMittentiService,
    		Date scadenzaEntry, CredenzialeMittente credenziale) throws Exception {
    	
    	IdCredenzialeMittente id = credenzialeMittentiService.convertToId(credenziale);
    	CredenzialeMittente credenzialeCheck = credenzialeMittentiService.get(id);
    	if(credenziale.getOraRegistrazione().after(scadenzaEntry)) {
			return credenzialeCheck; // informazione in cache già aggiornata da una chiamata concorrente
		}
		else {
			//System.out.println("Update Credenziale '"+id.getTipo()+"'");
			credenzialeCheck.setOraRegistrazione(DateManager.getDate()); // update
			credenzialeMittentiService.update(id, credenzialeCheck);
			return credenzialeCheck;
		}
    	
    }
    
}
