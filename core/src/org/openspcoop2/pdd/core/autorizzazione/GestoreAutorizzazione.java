/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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



package org.openspcoop2.pdd.core.autorizzazione;

import org.slf4j.Logger;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autorizzazione.pa.DatiInvocazionePortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.pa.EsitoAutorizzazioneCooperazione;
import org.openspcoop2.pdd.core.autorizzazione.pa.IAutorizzazionePortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.pdd.core.autorizzazione.pd.EsitoAutorizzazioneIntegrazione;
import org.openspcoop2.pdd.core.autorizzazione.pd.IAutorizzazionePortaDelegata;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.resources.Loader;

/**
 * Classe utilizzata per la gestione del processo di autorizzazione Buste
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreAutorizzazione {

	
	/** Chiave della cache per l'autorizzazione Buste  */
	private static final String AUTORIZZAZIONE_CACHE_NAME = "autorizzazione";
	/** Cache */
	private static Cache cacheAutorizzazione = null;
	/** Logger log */
	private static Logger logger = null;
	private static Logger logConsole = OpenSPCoop2Logger.getLoggerOpenSPCoopConsole();

	/** ClassName */
	private static ClassNameProperties className = ClassNameProperties.getInstance();


	/* --------------- Cache --------------------*/
	public static void resetCache() throws AutorizzazioneException{
		if(GestoreAutorizzazione.cacheAutorizzazione!=null){
			try{
				GestoreAutorizzazione.cacheAutorizzazione.clear();
			}catch(Exception e){
				throw new AutorizzazioneException(e.getMessage(),e);
			}
		}
	}
	public static String printStatsCache(String separator) throws AutorizzazioneException{
		try{
			if(GestoreAutorizzazione.cacheAutorizzazione!=null){
				return GestoreAutorizzazione.cacheAutorizzazione.printStats(separator);
			}
			else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new AutorizzazioneException("Visualizzazione Statistiche riguardante la cache delle autorizzazioni della Porta di Dominio non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws AutorizzazioneException{
		if(GestoreAutorizzazione.cacheAutorizzazione!=null)
			throw new AutorizzazioneException("Cache gia' abilitata");
		else{
			try{
				GestoreAutorizzazione.cacheAutorizzazione = new Cache(GestoreAutorizzazione.AUTORIZZAZIONE_CACHE_NAME);
			}catch(Exception e){
				throw new AutorizzazioneException(e.getMessage(),e);
			}
		}
	}
	public static void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond) throws AutorizzazioneException{
		if(GestoreAutorizzazione.cacheAutorizzazione!=null)
			throw new AutorizzazioneException("Cache gia' abilitata");
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
				
				GestoreAutorizzazione.initCacheAutorizzazione(dimensioneCacheInt, algoritmoCache, itemIdleTimeLong, itemLifeSecondLong, null);
			}catch(Exception e){
				throw new AutorizzazioneException(e.getMessage(),e);
			}
		}
	}
	public static void disabilitaCache() throws AutorizzazioneException{
		if(GestoreAutorizzazione.cacheAutorizzazione==null)
			throw new AutorizzazioneException("Cache gia' disabilitata");
		else{
			try{
				GestoreAutorizzazione.cacheAutorizzazione.clear();
				GestoreAutorizzazione.cacheAutorizzazione = null;
			}catch(Exception e){
				throw new AutorizzazioneException(e.getMessage(),e);
			}
		}
	}
	public static boolean isCacheAbilitata(){
		return GestoreAutorizzazione.cacheAutorizzazione != null;
	}
	public static String listKeysCache(String separator) throws AutorizzazioneException{
		if(GestoreAutorizzazione.cacheAutorizzazione!=null){
			try{
				return GestoreAutorizzazione.cacheAutorizzazione.printKeys(separator);
			}catch(Exception e){
				throw new AutorizzazioneException(e.getMessage(),e);
			}
		}else{
			throw new AutorizzazioneException("Cache non abilitata");
		}
	}
	public static String getObjectCache(String key) throws AutorizzazioneException{
		if(GestoreAutorizzazione.cacheAutorizzazione!=null){
			try{
				Object o = GestoreAutorizzazione.cacheAutorizzazione.get(key);
				if(o!=null){
					return o.toString();
				}else{
					return "oggetto con chiave ["+key+"] non presente";
				}
			}catch(Exception e){
				throw new AutorizzazioneException(e.getMessage(),e);
			}
		}else{
			throw new AutorizzazioneException("Cache non abilitata");
		}
	}
	public static void removeObjectCache(String key) throws AutorizzazioneException{
		if(GestoreAutorizzazione.cacheAutorizzazione!=null){
			try{
				GestoreAutorizzazione.cacheAutorizzazione.remove(key);
			}catch(Exception e){
				throw new AutorizzazioneException(e.getMessage(),e);
			}
		}else{
			throw new AutorizzazioneException("Cache non abilitata");
		}
	}
	


	/*----------------- INIZIALIZZAZIONE --------------------*/
	public static void initialize(Logger log) throws Exception{
		GestoreAutorizzazione.initialize(false, -1,null,-1l,-1l, log);
	}
	public static void initialize(int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{
		GestoreAutorizzazione.initialize(true, dimensioneCache,algoritmoCache,idleTime,itemLifeSecond, log);
	}

	private static void initialize(boolean cacheAbilitata,int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{

		// Inizializzo log
		GestoreAutorizzazione.logger = log;
		
		// Inizializzazione Cache
		if(cacheAbilitata){
			GestoreAutorizzazione.initCacheAutorizzazione(dimensioneCache, algoritmoCache, idleTime, itemLifeSecond, log);
		}

	}


	public static void initCacheAutorizzazione(int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception {
		
		if(log!=null)
			log.info("Inizializzazione cache Autorizzazione Buste");

		GestoreAutorizzazione.cacheAutorizzazione = new Cache(GestoreAutorizzazione.AUTORIZZAZIONE_CACHE_NAME);

		if( (dimensioneCache>0) ||
				(algoritmoCache != null) ){

			if( dimensioneCache>0 ){
				try{
					String msg = "Dimensione della cache (Autorizzazione Buste) impostata al valore: "+dimensioneCache;
					if(log!=null)
						log.info(msg);
					GestoreAutorizzazione.logConsole.info(msg);
					GestoreAutorizzazione.cacheAutorizzazione.setCacheSize(dimensioneCache);
				}catch(Exception error){
					throw new AutorizzazioneException("Parametro errato per la dimensione della cache (Gestore Messaggi): "+error.getMessage(),error);
				}
			}
			if(algoritmoCache != null ){
				String msg = "Algoritmo di cache (Autorizzazione Buste) impostato al valore: "+algoritmoCache;
				if(log!=null)
					log.info(msg);
				GestoreAutorizzazione.logConsole.info(msg);
				if(CostantiConfigurazione.CACHE_MRU.toString().equalsIgnoreCase(algoritmoCache))
					GestoreAutorizzazione.cacheAutorizzazione.setCacheAlgoritm(CacheAlgorithm.MRU);
				else
					GestoreAutorizzazione.cacheAutorizzazione.setCacheAlgoritm(CacheAlgorithm.LRU);
			}

		}

		if( (idleTime > 0) ||
				(itemLifeSecond > 0) ){

			if( idleTime > 0  ){
				try{
					String msg = "Attributo 'IdleTime' (Autorizzazione Buste) impostato al valore: "+idleTime;
					if(log!=null)
						log.info(msg);
					GestoreAutorizzazione.logConsole.info(msg);
					GestoreAutorizzazione.cacheAutorizzazione.setItemIdleTime(idleTime);
				}catch(Exception error){
					throw new AutorizzazioneException("Parametro errato per l'attributo 'IdleTime' (Gestore Messaggi): "+error.getMessage(),error);
				}
			}
			if( itemLifeSecond > 0  ){
				try{
					String msg = "Attributo 'MaxLifeSecond' (Autorizzazione Buste) impostato al valore: "+itemLifeSecond;
					if(log!=null)
						log.info(msg);
					GestoreAutorizzazione.logConsole.info(msg);
					GestoreAutorizzazione.cacheAutorizzazione.setItemLifeTime(itemLifeSecond);
				}catch(Exception error){
					throw new AutorizzazioneException("Parametro errato per l'attributo 'MaxLifeSecond' (Gestore Messaggi): "+error.getMessage(),error);
				}
			}

		}
	}
	

	// NOTA: le chiamate ad autorizzazione per contenuto non possono essere cachete, poiche' variano sempre i contenuti.

    public static EsitoAutorizzazioneIntegrazione verificaAutorizzazionePortaDelegata(String tipoAutorizzazione, DatiInvocazionePortaDelegata datiInvocazione,
 		  PdDContext pddContext,IProtocolFactory protocolFactory) throws Exception{
    	
    	checkDatiPortaDelegata(datiInvocazione);
    	
    	IAutorizzazionePortaDelegata auth = newInstanceAuthPortaDelegata(tipoAutorizzazione, pddContext, protocolFactory);
    	
    	if(GestoreAutorizzazione.cacheAutorizzazione==null || !auth.saveAuthorizationResultInCache()){
    		return auth.process(datiInvocazione);
		}
    	else{
    		String keyCache = buildCacheKey(true, tipoAutorizzazione, datiInvocazione.getKeyCache());

			synchronized (GestoreAutorizzazione.cacheAutorizzazione) {

				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreAutorizzazione.cacheAutorizzazione.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreAutorizzazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaDelegata) in cache.");
						return (EsitoAutorizzazioneIntegrazione) response.getObject();
					}else if(response.getException()!=null){
						GestoreAutorizzazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaDelegata) in cache.");
						throw (Exception) response.getException();
					}else{
						GestoreAutorizzazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}

				// Effettuo la query
				GestoreAutorizzazione.logger.debug("oggetto con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaDelegata) ricerco nella configurazione...");
				EsitoAutorizzazioneIntegrazione esito = auth.process(datiInvocazione);

				// Aggiungo la risposta in cache (se esiste una cache)	
				// A meno che non sia un errore di processamento
				// Quindi aggiungo in cache se:
				// - servizio e' autorizzato
				// - servizio non e' autorizzato (Codice 201)
				// - se la risposta ottenuta e' cachabile (attributo nocache)
				if(esito!=null && !esito.isNoCache()){
					if( (esito.isServizioAutorizzato()) || 
							(CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.equals(esito.getErroreIntegrazione().getCodiceErrore())) || 
							(CodiceErroreIntegrazione.CODICE_401_PD_INESISTENTE.equals(esito.getErroreIntegrazione().getCodiceErrore())) ){
						GestoreAutorizzazione.logger.info("Aggiungo oggetto ["+keyCache+"] in cache");
						try{	
							org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
							responseCache.setObject(esito);
							GestoreAutorizzazione.cacheAutorizzazione.put(keyCache,responseCache);
						}catch(UtilsException e){
							GestoreAutorizzazione.logger.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
						}
					}
					return esito;
				}else{
					throw new AutorizzazioneException("Metodo (GestoreAutorizzazione.autorizzazionePortaDelegata.process) ha ritornato un valore di esito null");
				}
			}
    	}
    }
	
    public static EsitoAutorizzazioneCooperazione verificaAutorizzazionePortaApplicativa(String tipoAutorizzazione, DatiInvocazionePortaApplicativa datiInvocazione,
    		PdDContext pddContext,IProtocolFactory protocolFactory) throws Exception{
    	  
    	checkDatiPortaApplicativa(datiInvocazione);
    	
    	IAutorizzazionePortaApplicativa auth = newInstanceAuthPortaApplicativa(tipoAutorizzazione, pddContext, protocolFactory);
    	
    	if(GestoreAutorizzazione.cacheAutorizzazione==null || !auth.saveAuthorizationResultInCache()){
    		return auth.process(datiInvocazione);
		}
    	else{
    		String keyCache = buildCacheKey(false, tipoAutorizzazione, datiInvocazione.getKeyCache());

			synchronized (GestoreAutorizzazione.cacheAutorizzazione) {

				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreAutorizzazione.cacheAutorizzazione.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreAutorizzazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaApplicativa) in cache.");
						return (EsitoAutorizzazioneCooperazione) response.getObject();
					}else if(response.getException()!=null){
						GestoreAutorizzazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaApplicativa) in cache.");
						throw (Exception) response.getException();
					}else{
						GestoreAutorizzazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}

				// Effettuo la query
				GestoreAutorizzazione.logger.debug("oggetto con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaApplicativa) ricerco nella configurazione...");
				EsitoAutorizzazioneCooperazione esito = auth.process(datiInvocazione);

				// Aggiungo la risposta in cache (se esiste una cache)	
				// A meno che non sia un errore di processamento
				// Quindi aggiungo in cache se:
				// - servizio e' autorizzato
				// - servizio non e' autorizzato (Codice 201)
				// - se la risposta ottenuta e' cachabile (attributo nocache)
				if(esito!=null && !esito.isNoCache()){
					if( (esito.isServizioAutorizzato()) || 
							(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.equals(esito.getErroreCooperazione().getCodiceErrore())) ){
						GestoreAutorizzazione.logger.info("Aggiungo oggetto ["+keyCache+"] in cache");
						try{	
							org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
							responseCache.setObject(esito);
							GestoreAutorizzazione.cacheAutorizzazione.put(keyCache,responseCache);
						}catch(UtilsException e){
							GestoreAutorizzazione.logger.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
						}
					}
					return esito;
				}else{
					throw new AutorizzazioneException("Metodo (GestoreAutorizzazione.autorizzazionePortaApplicativa.process) ha ritornato un valore di esito null");
				}
			}
    	}
    }
    
    private static void checkDatiPortaDelegata(DatiInvocazionePortaDelegata datiInvocazione) throws AutorizzazioneException{
    	
    	IDServizio idServizio = datiInvocazione.getIdServizio();
    	if(idServizio==null)
			throw new AutorizzazioneException("(Parametri) IDServizio non definito");
		if(idServizio.getTipoServizio()==null || idServizio.getServizio()==null)
			throw new AutorizzazioneException("(Parametri) Servizio non definito");
		if(idServizio.getSoggettoErogatore()==null || idServizio.getSoggettoErogatore().getTipo()==null || idServizio.getSoggettoErogatore().getNome()==null)
			throw new AutorizzazioneException("(Parametri) Soggetto erogatore non definito");
    	
    	IDPortaDelegata idPD = datiInvocazione.getIdPD();
    	if(idPD==null)
			throw new AutorizzazioneException("(Parametri) IDPortaDelegata non definito");
    	if(idPD.getLocationPD()==null)
			throw new AutorizzazioneException("(Parametri) IDPortaDelegata.locationPD non definito");
    	IDSoggetto idSoggettoFruitore = idPD.getSoggettoFruitore();
		if(idSoggettoFruitore==null || idSoggettoFruitore.getTipo()==null || idSoggettoFruitore.getNome()==null)
			throw new AutorizzazioneException("(Parametri) IDPortaDelegata.Soggetto non definito");
		
		PortaDelegata pd = datiInvocazione.getPd();
		if(pd==null)
			throw new AutorizzazioneException("(Parametri) PortaDelegata non definito");
		
		IDServizioApplicativo idSA = datiInvocazione.getIdServizioApplicativo();
    	if(idSA==null)
			throw new AutorizzazioneException("(Parametri) IDServizioApplicativo non definito");
    	if(idSA.getNome()==null)
			throw new AutorizzazioneException("(Parametri) IDServizioApplicativo.nome non definito");
    	IDSoggetto idSoggettoProprietario = idSA.getIdSoggettoProprietario();
		if(idSoggettoProprietario==null || idSoggettoProprietario.getTipo()==null || idSoggettoProprietario.getNome()==null)
			throw new AutorizzazioneException("(Parametri) IDServizioApplicativo.Soggetto non definito");
		
    }
    
    private static void checkDatiPortaApplicativa(DatiInvocazionePortaApplicativa datiInvocazione) throws AutorizzazioneException{
    	
    	IDServizio idServizio = datiInvocazione.getIdServizio();
    	if(idServizio==null)
			throw new AutorizzazioneException("(Parametri) IDServizio non definito");
		if(idServizio.getTipoServizio()==null || idServizio.getServizio()==null)
			throw new AutorizzazioneException("(Parametri) Servizio non definito");
		if(idServizio.getSoggettoErogatore()==null || idServizio.getSoggettoErogatore().getTipo()==null || idServizio.getSoggettoErogatore().getNome()==null)
			throw new AutorizzazioneException("(Parametri) Soggetto erogatore non definito");

		// Dipende dal ruolo della Busta, se c'e' la porta applicativa o delegata
//    	IDPortaApplicativaByNome idPA = datiInvocazione.getIdPA();
//    	if(idPA==null)
//			throw new AutorizzazioneException("(Parametri) IDPortaApplicativaByNome non definito");
//    	if(idPA.getNome()==null)
//			throw new AutorizzazioneException("(Parametri) IDPortaApplicativaByNome.nome non definito");
//    	IDSoggetto idSoggettoProprietarioPA = idPA.getSoggetto();
//		if(idSoggettoProprietarioPA==null || idSoggettoProprietarioPA.getTipo()==null || idSoggettoProprietarioPA.getNome()==null)
//			throw new AutorizzazioneException("(Parametri) IDPortaApplicativaByNome.Soggetto non definito");
//		
//		PortaApplicativa pa = datiInvocazione.getPa();
//		if(pa==null)
//			throw new AutorizzazioneException("(Parametri) PortaApplicativa non definito");
		
    	IDSoggetto idSoggettoFruitore = datiInvocazione.getIdSoggettoFruitore();
		if(idSoggettoFruitore==null || idSoggettoFruitore.getTipo()==null || idSoggettoFruitore.getNome()==null)
			throw new AutorizzazioneException("(Parametri) IDSoggettoFruitore non definito");
		
		RuoloBusta ruolo = datiInvocazione.getRuoloBusta();
		if(ruolo==null)
			throw new AutorizzazioneException("(Parametri) RuoloBusta non definito");
    }
    
    private static String buildCacheKey(boolean portaDelegata, String tipoAutorizzazione, String keyCache) throws AutorizzazioneException{
    	StringBuffer bf = new StringBuffer();
    	
    	if(portaDelegata)
    		bf.append("PD ");
    	else
    		bf.append("PA ");
    	
    	bf.append(" Auth:").append(tipoAutorizzazione).append(" ");
    	
    	bf.append(keyCache);
    	
    	return bf.toString();
    }
    
    private static IAutorizzazionePortaDelegata newInstanceAuthPortaDelegata(String tipoAutorizzazione,PdDContext pddContext, IProtocolFactory protocolFactory) throws AutorizzazioneException{
    	String classType = null; 
		IAutorizzazionePortaDelegata auth = null;
		try{
			classType = GestoreAutorizzazione.className.getAutorizzazione(tipoAutorizzazione);
			auth = (IAutorizzazionePortaDelegata) Loader.getInstance().newInstance(classType);
			AbstractCore.init(auth, pddContext, protocolFactory);
			return auth;
		}catch(Exception e){
			throw new AutorizzazioneException("Riscontrato errore durante il caricamento della classe ["+classType+
					"] da utilizzare per l'autorizzazione via PD: "+e.getMessage(),e);
		}
    }
    
    private static IAutorizzazionePortaApplicativa newInstanceAuthPortaApplicativa(String tipoAutorizzazione,PdDContext pddContext, IProtocolFactory protocolFactory) throws AutorizzazioneException{
    	String classType = null; 
    	IAutorizzazionePortaApplicativa auth = null;
		try{
			classType = GestoreAutorizzazione.className.getAutorizzazioneBuste(tipoAutorizzazione);
			auth = (IAutorizzazionePortaApplicativa) Loader.getInstance().newInstance(classType);
			AbstractCore.init(auth, pddContext, protocolFactory);
			return auth;
		}catch(Exception e){
			throw new AutorizzazioneException("Riscontrato errore durante il caricamento della classe ["+classType+
					"] da utilizzare per l'autorizzazione delle buste via PA: "+e.getMessage(),e);
		}
    }
    

}
