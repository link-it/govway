/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autenticazione.pa.DatiInvocazionePortaApplicativa;
import org.openspcoop2.pdd.core.autenticazione.pa.EsitoAutenticazionePortaApplicativa;
import org.openspcoop2.pdd.core.autenticazione.pa.IAutenticazionePortaApplicativa;
import org.openspcoop2.pdd.core.autenticazione.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.pdd.core.autenticazione.pd.EsitoAutenticazionePortaDelegata;
import org.openspcoop2.pdd.core.autenticazione.pd.IAutenticazionePortaDelegata;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
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
			throw new AutenticazioneException("Visualizzazione Statistiche riguardante la cache dell'autenticazione della Porta di Dominio non riuscita: "+e.getMessage(),e);
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

		if( (idleTime > 0) ||
				(itemLifeSecond > 0) ){

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
			if( itemLifeSecond > 0  ){
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

		}
	}
	

    public static EsitoAutenticazionePortaDelegata verificaAutenticazionePortaDelegata(String tipoAutenticazione, DatiInvocazionePortaDelegata datiInvocazione,
 		  PdDContext pddContext,IProtocolFactory<?> protocolFactory) throws Exception{
    	return _verificaAutenticazionePortaDelegata(false, tipoAutenticazione, datiInvocazione, pddContext, protocolFactory);
    }
    public static EsitoAutenticazionePortaDelegata verificaAutenticazioneMessageBox(String tipoAutenticazione, DatiInvocazionePortaDelegata datiInvocazione,
   		  PdDContext pddContext,IProtocolFactory<?> protocolFactory) throws Exception{
    	return _verificaAutenticazionePortaDelegata(true, tipoAutenticazione, datiInvocazione, pddContext, protocolFactory);
    }
    private static EsitoAutenticazionePortaDelegata _verificaAutenticazionePortaDelegata(boolean messageBox, String tipoAutenticazione, DatiInvocazionePortaDelegata datiInvocazione,
   		  PdDContext pddContext,IProtocolFactory<?> protocolFactory) throws Exception{
    	
    	checkDatiPortaDelegata(messageBox,datiInvocazione);
    	
    	IAutenticazionePortaDelegata auth = newInstanceAuthPortaDelegata(tipoAutenticazione, pddContext, protocolFactory);
    	
    	if(GestoreAutenticazione.cacheAutenticazione==null || !auth.saveAuthenticationResultInCache()){
    		return auth.process(datiInvocazione);
		}
    	else{
    		String keyCache = buildCacheKey(messageBox, true, tipoAutenticazione, datiInvocazione.getKeyCache(), auth.getSuffixKeyAuthenticationResultInCache(datiInvocazione));

			synchronized (GestoreAutenticazione.cacheAutenticazione) {

				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreAutenticazione.cacheAutenticazione.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreAutenticazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutenticazionePortaDelegata) in cache.");
						return (EsitoAutenticazionePortaDelegata) response.getObject();
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
    }
	
    public static EsitoAutenticazionePortaApplicativa verificaAutenticazionePortaApplicativa(String tipoAutenticazione, DatiInvocazionePortaApplicativa datiInvocazione,
    		PdDContext pddContext,IProtocolFactory<?> protocolFactory) throws Exception{
    	  
    	checkDatiPortaApplicativa(datiInvocazione);
    	
    	IAutenticazionePortaApplicativa auth = newInstanceAuthPortaApplicativa(tipoAutenticazione, pddContext, protocolFactory);
    	
    	if(GestoreAutenticazione.cacheAutenticazione==null || !auth.saveAuthenticationResultInCache()){
    		return auth.process(datiInvocazione);
		}
    	else{
    		String keyCache = buildCacheKey(false, false, tipoAutenticazione, datiInvocazione.getKeyCache(), auth.getSuffixKeyAuthenticationResultInCache(datiInvocazione));

			synchronized (GestoreAutenticazione.cacheAutenticazione) {

				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreAutenticazione.cacheAutenticazione.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreAutenticazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutenticazionePortaApplicativa) in cache.");
						return (EsitoAutenticazionePortaApplicativa) response.getObject();
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
    	StringBuffer bf = new StringBuffer();
    	
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
    

}
