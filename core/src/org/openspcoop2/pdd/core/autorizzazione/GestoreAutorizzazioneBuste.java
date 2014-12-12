/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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

import org.apache.log4j.Logger;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.state.IState;
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
public class GestoreAutorizzazioneBuste {

	/** TipoAutorizzazioneBuste */
	private static String tipoAutorizzazioneBuste = null;
	/** Chiave della cache per l'autorizzazione Buste  */
	private static final String AUTORIZZAZIONE_BUSTE_CACHE_NAME = "autorizzazioneBuste";
	/** GestoreMessaggi Cache */
	private static Cache cacheAutorizzazioneBuste = null;
	/** Logger log */
	private static Logger logger = null;
	private static Logger logConsole = OpenSPCoop2Logger.getLoggerOpenSPCoopConsole();

	/** ClassName */
	private static ClassNameProperties className = ClassNameProperties.getInstance();
	/** Indicazione se un meccanismo di autorizzazione Buste e' attivo */
	private static boolean isAttivoAutorizzazioneBuste = false;


	/* --------------- Cache --------------------*/
	public static void resetCache() throws AutorizzazioneException{
		if(GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste!=null){
			try{
				GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste.clear();
			}catch(Exception e){
				throw new AutorizzazioneException(e.getMessage(),e);
			}
		}
	}
	public static String printStatsCache(String separator) throws AutorizzazioneException{
		try{
			if(GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste!=null){
				return GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste.printStats(separator);
			}
			else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new AutorizzazioneException("Visualizzazione Statistiche riguardante la cache delle autorizzazioni della Porta di Dominio non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws AutorizzazioneException{
		if(GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste!=null)
			throw new AutorizzazioneException("Cache gia' abilitata");
		else{
			try{
				GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste = new Cache(GestoreAutorizzazioneBuste.AUTORIZZAZIONE_BUSTE_CACHE_NAME);
			}catch(Exception e){
				throw new AutorizzazioneException(e.getMessage(),e);
			}
		}
	}
	public static void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond) throws AutorizzazioneException{
		if(GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste!=null)
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
				
				GestoreAutorizzazioneBuste.initCacheAutorizzazione(dimensioneCacheInt, algoritmoCache, itemIdleTimeLong, itemLifeSecondLong, null);
			}catch(Exception e){
				throw new AutorizzazioneException(e.getMessage(),e);
			}
		}
	}
	public static void disabilitaCache() throws AutorizzazioneException{
		if(GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste==null)
			throw new AutorizzazioneException("Cache gia' disabilitata");
		else{
			try{
				GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste.clear();
				GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste = null;
			}catch(Exception e){
				throw new AutorizzazioneException(e.getMessage(),e);
			}
		}
	}
	public static boolean isCacheAbilitata(){
		return GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste != null;
	}
	public static String listKeysCache(String separator) throws AutorizzazioneException{
		if(GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste!=null){
			try{
				return GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste.printKeys(separator);
			}catch(Exception e){
				throw new AutorizzazioneException(e.getMessage(),e);
			}
		}else{
			throw new AutorizzazioneException("Cache non abilitata");
		}
	}
	public static String getObjectCache(String key) throws AutorizzazioneException{
		if(GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste!=null){
			try{
				Object o = GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste.get(key);
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
	


	/*----------------- INIZIALIZZAZIONE --------------------*/
	public static void initialize(String tipoAutorizzazioneBuste, Logger log) throws Exception{
		GestoreAutorizzazioneBuste.initialize(tipoAutorizzazioneBuste, false, -1,null,-1l,-1l, log);
	}
	public static void initialize(String tipoAutorizzazioneBuste, int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{
		GestoreAutorizzazioneBuste.initialize(tipoAutorizzazioneBuste, true, dimensioneCache,algoritmoCache,idleTime,itemLifeSecond, log);
	}

	private static void initialize(String tipoAutorizzazioneBuste,boolean cacheAbilitata,int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{

		// Inizializzo log
		GestoreAutorizzazioneBuste.logger = log;
		GestoreAutorizzazioneBuste.tipoAutorizzazioneBuste = tipoAutorizzazioneBuste;

		if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equalsIgnoreCase(tipoAutorizzazioneBuste)){
			return;
		}
		
		// Inizializzazione Cache
		if(cacheAbilitata){
			GestoreAutorizzazioneBuste.initCacheAutorizzazione(dimensioneCache, algoritmoCache, idleTime, itemLifeSecond, log);
		}

		// Inizializzazione effettuata
		GestoreAutorizzazioneBuste.isAttivoAutorizzazioneBuste = true;
	}


	public static void initCacheAutorizzazione(int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception {
		
		if(log!=null)
			log.info("Inizializzazione cache Autorizzazione Buste");

		GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste = new Cache(GestoreAutorizzazioneBuste.AUTORIZZAZIONE_BUSTE_CACHE_NAME);

		if( (dimensioneCache>0) ||
				(algoritmoCache != null) ){

			if( dimensioneCache>0 ){
				try{
					String msg = "Dimensione della cache (Autorizzazione Buste) impostata al valore: "+dimensioneCache;
					if(log!=null)
						log.info(msg);
					GestoreAutorizzazioneBuste.logConsole.info(msg);
					GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste.setCacheSize(dimensioneCache);
				}catch(Exception error){
					throw new AutorizzazioneException("Parametro errato per la dimensione della cache (Gestore Messaggi): "+error.getMessage(),error);
				}
			}
			if(algoritmoCache != null ){
				String msg = "Algoritmo di cache (Autorizzazione Buste) impostato al valore: "+algoritmoCache;
				if(log!=null)
					log.info(msg);
				GestoreAutorizzazioneBuste.logConsole.info(msg);
				if(CostantiConfigurazione.CACHE_MRU.toString().equalsIgnoreCase(algoritmoCache))
					GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste.setCacheAlgoritm(CacheAlgorithm.MRU);
				else
					GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste.setCacheAlgoritm(CacheAlgorithm.LRU);
			}

		}

		if( (idleTime > 0) ||
				(itemLifeSecond > 0) ){

			if( idleTime > 0  ){
				try{
					String msg = "Attributo 'IdleTime' (Autorizzazione Buste) impostato al valore: "+idleTime;
					if(log!=null)
						log.info(msg);
					GestoreAutorizzazioneBuste.logConsole.info(msg);
					GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste.setItemIdleTime(idleTime);
				}catch(Exception error){
					throw new AutorizzazioneException("Parametro errato per l'attributo 'IdleTime' (Gestore Messaggi): "+error.getMessage(),error);
				}
			}
			if( itemLifeSecond > 0  ){
				try{
					String msg = "Attributo 'MaxLifeSecond' (Autorizzazione Buste) impostato al valore: "+itemLifeSecond;
					if(log!=null)
						log.info(msg);
					GestoreAutorizzazioneBuste.logConsole.info(msg);
					GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste.setItemLifeTime(itemLifeSecond);
				}catch(Exception error){
					throw new AutorizzazioneException("Parametro errato per l'attributo 'MaxLifeSecond' (Gestore Messaggi): "+error.getMessage(),error);
				}
			}

		}
	}
	

	public static EsitoAutorizzazioneCooperazione verificaAutorizzazioneBuste(InfoConnettoreIngresso infoConnettoreIngresso,Credenziali credenzialiPdDMittente,String identitaServizioApplicativo,
			String subjectMessageSecurityServizioApplicativo,IDSoggetto soggettoFruitore,IDServizio idServizio,IState state,
			PdDContext pddContext,IProtocolFactory protocolFactory)throws Exception{

		// Inizializzazione IAutorizzazioneBuste
		String classType = null; 
		IAutorizzazioneBuste auth = null;
		try{
			classType = GestoreAutorizzazioneBuste.className.getAutorizzazioneBuste(tipoAutorizzazioneBuste);
			auth = (IAutorizzazioneBuste) Loader.getInstance().newInstance(classType);
			AbstractCore.init(auth, pddContext, protocolFactory);
		}catch(Exception e){
			throw new AutorizzazioneException("Riscontrato errore durante il caricamento della classe ["+classType+
					"] da utilizzare per l'autorizzazione delle buste: "+e.getMessage(),e);
		}
		
		if(GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste==null){

			return auth.process(infoConnettoreIngresso,credenzialiPdDMittente,identitaServizioApplicativo,
					subjectMessageSecurityServizioApplicativo,soggettoFruitore,idServizio,state);

		}else{

			// Raccolta dati
			if(soggettoFruitore==null || soggettoFruitore.getTipo()==null || soggettoFruitore.getNome()==null)
				throw new AutorizzazioneException("(Parametri) Soggetto fruitore non definito");
			if(idServizio==null)
				throw new AutorizzazioneException("(Parametri) IDServizio non definito");
			if(idServizio.getTipoServizio()==null || idServizio.getServizio()==null)
				throw new AutorizzazioneException("(Parametri) Servizio non definito");
			if(idServizio.getSoggettoErogatore()==null || idServizio.getSoggettoErogatore().getTipo()==null || idServizio.getSoggettoErogatore().getNome()==null)
				throw new AutorizzazioneException("(Parametri) Soggetto erogatore non definito");

			// Creazione key
			String keyCache = null;
			keyCache = soggettoFruitore.toString() + " -> " + idServizio.toString();
			if(credenzialiPdDMittente!=null)
				keyCache=keyCache+" mittente("+credenzialiPdDMittente.toString()+")";
			if(identitaServizioApplicativo!=null)
				keyCache=keyCache+" identitaSA("+identitaServizioApplicativo+")";
			if(subjectMessageSecurityServizioApplicativo!=null)
				keyCache=keyCache+" subjectMessageSecurity("+subjectMessageSecurityServizioApplicativo+")";

			synchronized (GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste) {

				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreAutorizzazioneBuste.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (methodo:verificaAutorizzazioneBuste) in cache.");
						return (EsitoAutorizzazioneCooperazione) response.getObject();
					}else if(response.getException()!=null){
						GestoreAutorizzazioneBuste.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (methodo:verificaAutorizzazioneBuste) in cache.");
						throw (Exception) response.getException();
					}else{
						GestoreAutorizzazioneBuste.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}

				// Effettuo la query
				GestoreAutorizzazioneBuste.logger.debug("oggetto con chiave ["+keyCache+"] (methodo:verificaAutorizzazioneBuste) ricerco nella configurazione...");
				EsitoAutorizzazioneCooperazione esito = auth.process(infoConnettoreIngresso,credenzialiPdDMittente,identitaServizioApplicativo,
						subjectMessageSecurityServizioApplicativo,soggettoFruitore,idServizio,state);

				// Aggiungo la risposta in cache (se esiste una cache)	
				// A meno che non sia un errore di processamento
				// Quindi aggiungo in cache se:
				// - servizio e' autorizzato
				// - servizio non e' autorizzato (Codice 201)
				if(esito!=null){
					if( (esito.isServizioAutorizzato()) || (CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.equals(esito.getErroreCooperazione().getCodiceErrore())) ){
						GestoreAutorizzazioneBuste.logger.info("Aggiungo oggetto ["+keyCache+"] in cache");
						try{	
							org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
							responseCache.setObject(esito);
							GestoreAutorizzazioneBuste.cacheAutorizzazioneBuste.put(keyCache,responseCache);
						}catch(UtilsException e){
							GestoreAutorizzazioneBuste.logger.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
						}
					}
					return esito;
				}else{
					throw new AutorizzazioneException("Metodo (GestoreAutorizzazioneBuste.autorizzazioneBuste.process) ha ritornato un valore di esito null");
				}
			}
		}

	}

	/**
	 * @return the isAttivoAutorizzazioneBuste
	 */
	public static boolean isAttivoAutorizzazioneBuste() {
		return GestoreAutorizzazioneBuste.isAttivoAutorizzazioneBuste;
	}
	public static String getTipoAutorizzazioneBuste() {
		return GestoreAutorizzazioneBuste.tipoAutorizzazioneBuste;
	}

}
