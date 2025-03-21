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



package org.openspcoop2.pdd.core.autorizzazione;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.RuoloTipologia;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.utils.WWWAuthenticateErrorCode;
import org.openspcoop2.message.utils.WWWAuthenticateGenerator;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.dynamic.PddPluginLoader;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autorizzazione.pa.DatiInvocazionePortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.pa.EsitoAutorizzazionePortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.pa.IAutorizzazioneContenutoPortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.pa.IAutorizzazionePortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.pdd.core.autorizzazione.pd.EsitoAutorizzazionePortaDelegata;
import org.openspcoop2.pdd.core.autorizzazione.pd.IAutorizzazioneContenutoPortaDelegata;
import org.openspcoop2.pdd.core.autorizzazione.pd.IAutorizzazionePortaDelegata;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.cache.CacheResponse;
import org.openspcoop2.utils.cache.CacheType;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.slf4j.Logger;

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
	//private static final Boolean semaphoreAutorizzazionePD = true;
	//private static final Boolean semaphoreAutorizzazionePA = true;
	//private static final Boolean semaphoreAutorizzazioneContenutiPD = true;
	//private static final Boolean semaphoreAutorizzazioneContenutiPA = true;
	
	private static final Map<String, org.openspcoop2.utils.Semaphore> _lockAutorizzazionePD = new HashMap<>(); 
	private static synchronized org.openspcoop2.utils.Semaphore initLockAutorizzazionePD(String tipoAutorizzazione){
		org.openspcoop2.utils.Semaphore s = _lockAutorizzazionePD.get(tipoAutorizzazione);
		if(s==null) {
			Integer permits = OpenSPCoop2Properties.getInstance().getAutorizzazioneLockPermits(tipoAutorizzazione);
			if(permits==null) {
				permits = OpenSPCoop2Properties.getInstance().getAutorizzazioneLockPermits();
			}
			if(permits!=null && permits.intValue()>1) {
				s = new org.openspcoop2.utils.Semaphore("GestoreAutorizzazioneFruizioni_"+tipoAutorizzazione, permits);
			}
			else {
				s = new org.openspcoop2.utils.Semaphore("GestoreAutorizzazioneFruizioni_"+tipoAutorizzazione);
			}
			_lockAutorizzazionePD.put(tipoAutorizzazione, s);
		}
		return s;
	}
	private static org.openspcoop2.utils.Semaphore getLockAutorizzazionePD(String tipoAutorizzazione){
		org.openspcoop2.utils.Semaphore s = _lockAutorizzazionePD.get(tipoAutorizzazione);
		if(s==null) {
			s = initLockAutorizzazionePD(tipoAutorizzazione);
		}
		return s;
	}
	
	private static final Map<String, org.openspcoop2.utils.Semaphore> _lockAutorizzazionePA = new HashMap<>(); 
	private static synchronized org.openspcoop2.utils.Semaphore initLockAutorizzazionePA(String tipoAutorizzazione){
		org.openspcoop2.utils.Semaphore s = _lockAutorizzazionePA.get(tipoAutorizzazione);
		if(s==null) {
			Integer permits = OpenSPCoop2Properties.getInstance().getAutorizzazioneLockPermits(tipoAutorizzazione);
			if(permits==null) {
				permits = OpenSPCoop2Properties.getInstance().getAutorizzazioneLockPermits();
			}
			if(permits!=null && permits.intValue()>1) {
				s = new org.openspcoop2.utils.Semaphore("GestoreAutorizzazioneErogazioni_"+tipoAutorizzazione, permits);
			}
			else {
				s = new org.openspcoop2.utils.Semaphore("GestoreAutorizzazioneErogazioni_"+tipoAutorizzazione);
			}
			_lockAutorizzazionePA.put(tipoAutorizzazione, s);
		}
		return s;
	}
	private static org.openspcoop2.utils.Semaphore getLockAutorizzazionePA(String tipoAutorizzazione){
		org.openspcoop2.utils.Semaphore s = _lockAutorizzazionePA.get(tipoAutorizzazione);
		if(s==null) {
			s = initLockAutorizzazionePA(tipoAutorizzazione);
		}
		return s;
	}
	
	private static final Map<String, org.openspcoop2.utils.Semaphore> _lockAutorizzazioneContenutiPD = new HashMap<>(); 
	private static synchronized org.openspcoop2.utils.Semaphore initLockAutorizzazioneContenutiPD(String tipoAutorizzazioneContenuti){
		org.openspcoop2.utils.Semaphore s = _lockAutorizzazioneContenutiPD.get(tipoAutorizzazioneContenuti);
		if(s==null) {
			Integer permits = OpenSPCoop2Properties.getInstance().getAutorizzazioneContenutiLockPermits(tipoAutorizzazioneContenuti);
			if(permits==null) {
				permits = OpenSPCoop2Properties.getInstance().getAutorizzazioneContenutiLockPermits();
			}
			if(permits!=null && permits.intValue()>1) {
				s = new org.openspcoop2.utils.Semaphore("GestoreAutorizzazioneContenutiFruizioni_"+tipoAutorizzazioneContenuti, permits);
			}
			else {
				s = new org.openspcoop2.utils.Semaphore("GestoreAutorizzazioneContenutiFruizioni_"+tipoAutorizzazioneContenuti);
			}
			_lockAutorizzazioneContenutiPD.put(tipoAutorizzazioneContenuti, s);
		}
		return s;
	}
	private static org.openspcoop2.utils.Semaphore getLockAutorizzazioneContenutiPD(String tipoAutorizzazioneContenuti){
		org.openspcoop2.utils.Semaphore s = _lockAutorizzazioneContenutiPD.get(tipoAutorizzazioneContenuti);
		if(s==null) {
			s = initLockAutorizzazioneContenutiPD(tipoAutorizzazioneContenuti);
		}
		return s;
	}
	
	private static final Map<String, org.openspcoop2.utils.Semaphore> _lockAutorizzazioneContenutiPA = new HashMap<>(); 
	private static synchronized org.openspcoop2.utils.Semaphore initLockAutorizzazioneContenutiPA(String tipoAutorizzazioneContenuti){
		org.openspcoop2.utils.Semaphore s = _lockAutorizzazioneContenutiPA.get(tipoAutorizzazioneContenuti);
		if(s==null) {
			Integer permits = OpenSPCoop2Properties.getInstance().getAutorizzazioneContenutiLockPermits(tipoAutorizzazioneContenuti);
			if(permits==null) {
				permits = OpenSPCoop2Properties.getInstance().getAutorizzazioneContenutiLockPermits();
			}
			if(permits!=null && permits.intValue()>1) {
				s = new org.openspcoop2.utils.Semaphore("GestoreAutorizzazioneContenutiErogazioni_"+tipoAutorizzazioneContenuti, permits);
			}
			else {
				s = new org.openspcoop2.utils.Semaphore("GestoreAutorizzazioneContenutiErogazioni_"+tipoAutorizzazioneContenuti);
			}
			_lockAutorizzazioneContenutiPA.put(tipoAutorizzazioneContenuti, s);
		}
		return s;
	}
	private static org.openspcoop2.utils.Semaphore getLockAutorizzazioneContenutiPA(String tipoAutorizzazioneContenuti){
		org.openspcoop2.utils.Semaphore s = _lockAutorizzazioneContenutiPA.get(tipoAutorizzazioneContenuti);
		if(s==null) {
			s = initLockAutorizzazioneContenutiPA(tipoAutorizzazioneContenuti);
		}
		return s;
	}
	
	/** Logger log */
	private static Logger logger = null;
	private static Logger logConsole = OpenSPCoop2Logger.getLoggerOpenSPCoopConsole();



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
			throw new AutorizzazioneException("Visualizzazione Statistiche riguardante la cache delle autorizzazioni non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws AutorizzazioneException{
		if(GestoreAutorizzazione.cacheAutorizzazione!=null)
			throw new AutorizzazioneException("Cache gia' abilitata");
		else{
			_abilitaCache();
		}
	}
	private static synchronized void _abilitaCache() throws AutorizzazioneException{
		if(GestoreAutorizzazione.cacheAutorizzazione==null) {
			try{
				GestoreAutorizzazione.cacheAutorizzazione = new Cache(CacheType.JCS, GestoreAutorizzazione.AUTORIZZAZIONE_CACHE_NAME); // lascio JCS come default abilitato via jmx
				GestoreAutorizzazione.cacheAutorizzazione.build();
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
				
				GestoreAutorizzazione.initCacheAutorizzazione(CacheType.JCS, dimensioneCacheInt, algoritmoCache, itemIdleTimeLong, itemLifeSecondLong, null); // lascio JCS come default abilitato via jmx
			}catch(Exception e){
				throw new AutorizzazioneException(e.getMessage(),e);
			}
		}
	}
	public static void disabilitaCache() throws AutorizzazioneException{
		if(GestoreAutorizzazione.cacheAutorizzazione==null)
			throw new AutorizzazioneException("Cache gia' disabilitata");
		else{
			_disabilitaCache();
		}
	}
	private static synchronized void _disabilitaCache() throws AutorizzazioneException{
		if(GestoreAutorizzazione.cacheAutorizzazione!=null) {
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
	public static List<String> listKeysCache() throws AutorizzazioneException{
		if(GestoreAutorizzazione.cacheAutorizzazione!=null){
			try{
				return GestoreAutorizzazione.cacheAutorizzazione.keys();
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
	public static Object getRawObjectCache(String key) throws AutorizzazioneException{
		if(GestoreAutorizzazione.cacheAutorizzazione!=null){
			try{
				Object o = GestoreAutorizzazione.cacheAutorizzazione.get(key);
				if(o!=null){
					if(o instanceof CacheResponse) {
						CacheResponse cR = (CacheResponse) o;
						if(cR.getObject()!=null) {
							o = cR.getObject();
						}
						else if(cR.getException()!=null) {
							o = cR.getException();
						}
					}
					return o;
				}else{
					return null;
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
		GestoreAutorizzazione.initialize(null, false, -1,null,-1l,-1l, log);
	}
	public static void initialize(CacheType cacheType, int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{
		GestoreAutorizzazione.initialize(cacheType, true, dimensioneCache,algoritmoCache,idleTime,itemLifeSecond, log);
	}

	private static void initialize(CacheType cacheType, boolean cacheAbilitata,int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{

		// Inizializzo log
		GestoreAutorizzazione.logger = log;
		
		// Inizializzazione Cache
		if(cacheAbilitata){
			GestoreAutorizzazione.initCacheAutorizzazione(cacheType, dimensioneCache, algoritmoCache, idleTime, itemLifeSecond, log);
		}

	}


	public static void initCacheAutorizzazione(CacheType cacheType, int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception {
		
		if(log!=null)
			log.info("Inizializzazione cache Autorizzazione");

		GestoreAutorizzazione.cacheAutorizzazione = new Cache(cacheType, GestoreAutorizzazione.AUTORIZZAZIONE_CACHE_NAME);

		if( (dimensioneCache>0) ||
				(algoritmoCache != null) ){

			if( dimensioneCache>0 ){
				try{
					String msg = "Dimensione della cache (Autorizzazione) impostata al valore: "+dimensioneCache;
					if(log!=null)
						log.info(msg);
					GestoreAutorizzazione.logConsole.info(msg);
					GestoreAutorizzazione.cacheAutorizzazione.setCacheSize(dimensioneCache);
				}catch(Exception error){
					throw new AutorizzazioneException("Parametro errato per la dimensione della cache (Gestore Messaggi): "+error.getMessage(),error);
				}
			}
			if(algoritmoCache != null ){
				String msg = "Algoritmo di cache (Autorizzazione) impostato al valore: "+algoritmoCache;
				if(log!=null)
					log.info(msg);
				GestoreAutorizzazione.logConsole.info(msg);
				if(CostantiConfigurazione.CACHE_MRU.toString().equalsIgnoreCase(algoritmoCache))
					GestoreAutorizzazione.cacheAutorizzazione.setCacheAlgoritm(CacheAlgorithm.MRU);
				else
					GestoreAutorizzazione.cacheAutorizzazione.setCacheAlgoritm(CacheAlgorithm.LRU);
			}

		}

		if( idleTime > 0  ){
			try{
				String msg = "Attributo 'IdleTime' (Autorizzazione) impostato al valore: "+idleTime;
				if(log!=null)
					log.info(msg);
				GestoreAutorizzazione.logConsole.info(msg);
				GestoreAutorizzazione.cacheAutorizzazione.setItemIdleTime(idleTime);
			}catch(Exception error){
				throw new AutorizzazioneException("Parametro errato per l'attributo 'IdleTime' (Gestore Messaggi): "+error.getMessage(),error);
			}
		}
		try{
			String msg = "Attributo 'MaxLifeSecond' (Autorizzazione) impostato al valore: "+itemLifeSecond;
			if(log!=null)
				log.info(msg);
			GestoreAutorizzazione.logConsole.info(msg);
			GestoreAutorizzazione.cacheAutorizzazione.setItemLifeTime(itemLifeSecond);
		}catch(Exception error){
			throw new AutorizzazioneException("Parametro errato per l'attributo 'MaxLifeSecond' (Gestore Messaggi): "+error.getMessage(),error);
		}
		
		GestoreAutorizzazione.cacheAutorizzazione.build();

	}
	

	@SuppressWarnings("deprecation")
	@Deprecated
	public static void disableSyncronizedGet() throws UtilsException {
		if(GestoreAutorizzazione.cacheAutorizzazione==null) {
			throw new UtilsException("Cache disabled");
		}
		GestoreAutorizzazione.cacheAutorizzazione.disableSyncronizedGet();
	}
	@SuppressWarnings("deprecation")
	@Deprecated
	public static boolean isDisableSyncronizedGet() throws UtilsException {
		if(GestoreAutorizzazione.cacheAutorizzazione==null) {
			throw new UtilsException("Cache disabled");
		}
		return GestoreAutorizzazione.cacheAutorizzazione.isDisableSyncronizedGet();
	}
	
	
	
	
	
	
/*----------------- CLEANER --------------------*/
	
	public static void removePortaApplicativa(IDPortaApplicativa idPA) throws Exception {
		if(GestoreAutorizzazione.isCacheAbilitata()) {
			List<String> keyForClean = new ArrayList<>();
			List<String> keys = GestoreAutorizzazione.listKeysCache();
			if(keys!=null && !keys.isEmpty()) {
				String match = IDPortaApplicativa.PORTA_APPLICATIVA_PREFIX+idPA.getNome()+IDPortaApplicativa.PORTA_APPLICATIVA_SUFFIX;
				for (String key : keys) {
					if(key!=null && key.contains(match)) {
						keyForClean.add(key);
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
	
	public static void removePortaDelegata(IDPortaDelegata idPD) throws Exception {
		if(GestoreAutorizzazione.isCacheAbilitata()) {
			List<String> keyForClean = new ArrayList<>();
			List<String> keys = GestoreAutorizzazione.listKeysCache();
			if(keys!=null && !keys.isEmpty()) {
				String match = IDPortaDelegata.PORTA_DELEGATA_PREFIX+idPD.getNome()+IDPortaDelegata.PORTA_DELEGATA_SUFFIX;
				for (String key : keys) {
					if(key!=null && key.contains(match)) {
						keyForClean.add(key);
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
	
	public static void removeSoggetto(IDSoggetto idSoggetto) throws Exception {
		if(GestoreAutorizzazione.isCacheAbilitata()) {
			List<String> keyForClean = new ArrayList<>();
			List<String> keys = GestoreAutorizzazione.listKeysCache();
			if(keys!=null && !keys.isEmpty()) {
				String matchSoggettoFruitore = DatiInvocazionePortaApplicativa.SOGGETTO_FRUITORE_PREFIX+idSoggetto.toString()+DatiInvocazionePortaApplicativa.SOGGETTO_FRUITORE_SUFFIX;
				for (String key : keys) {
					if(key!=null && key.contains(matchSoggettoFruitore)) {
						keyForClean.add(key);
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
	
	public static void removeApplicativo(IDServizioApplicativo idApplicativo) throws Exception {
		if(GestoreAutorizzazione.isCacheAbilitata()) {
			List<String> keyForClean = new ArrayList<>();
			List<String> keys = GestoreAutorizzazione.listKeysCache();
			if(keys!=null && !keys.isEmpty()) {
				String matchApplicativoFruitore = DatiInvocazionePortaApplicativa.APPLICATIVO_FRUITORE_PREFIX+idApplicativo.toString()+DatiInvocazionePortaApplicativa.APPLICATIVO_FRUITORE_SUFFIX;
				String matchApplicativo = DatiInvocazionePortaDelegata.APPLICATIVO_PREFIX+idApplicativo.toString()+DatiInvocazionePortaDelegata.APPLICATIVO_SUFFIX;
				for (String key : keys) {
					if(key!=null && (key.contains(matchApplicativoFruitore) || key.contains(matchApplicativo))) {
						keyForClean.add(key);
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
	
	
	
	
	
	
	
	/*----------------- AUTORIZZAZIONE --------------------*/
	

	public static EsitoAutorizzazionePortaDelegata verificaAutorizzazionePortaDelegata(String tipoAutorizzazione, DatiInvocazionePortaDelegata datiInvocazione,
	 		  PdDContext pddContext,IProtocolFactory<?> protocolFactory, OpenSPCoop2Message msg, Logger log) throws Exception{
		
		checkDatiPortaDelegata(datiInvocazione);
    	
		EsitoAutorizzazionePortaDelegata esito = _verificaAutorizzazionePortaDelegata(tipoAutorizzazione, datiInvocazione, pddContext, protocolFactory, msg);
    	if(esito.isAutorizzato()==false) {
    		return esito;
    	}
		
    	// Verifiche Richieste
    	boolean verificaAutorizzazioneRichiedenti = false;
    	boolean verificaAutorizzazioneRuoli = false;
    	if(datiInvocazione.getPd()!=null) {
    		if(datiInvocazione.getPd().getAutorizzazioneToken()!=null) {
    			if(StatoFunzionalita.ABILITATO.equals(datiInvocazione.getPd().getAutorizzazioneToken().getAutorizzazioneApplicativi())){
    				verificaAutorizzazioneRichiedenti=true;
    			}
    			if(StatoFunzionalita.ABILITATO.equals(datiInvocazione.getPd().getAutorizzazioneToken().getAutorizzazioneRuoli())){
    				verificaAutorizzazioneRuoli = true;
    			}
    		}
    	}
    	
    	// Verifica Token Richiedenti
    	
    	IDServizioApplicativo idSAToken = null;
    	if(pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN)) {
    		idSAToken = (IDServizioApplicativo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN);
    	}
    	
    	boolean authRichiedenti = true;
    	if(verificaAutorizzazioneRichiedenti){
			authRichiedenti = false;
			if( idSAToken!=null &&
					datiInvocazione.getPd().getAutorizzazioneToken().getServiziApplicativi()!=null &&
					datiInvocazione.getPd().getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList()>0){
				for (PortaDelegataServizioApplicativo sa : datiInvocazione.getPd().getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativoList()) {
					if(idSAToken.getNome().equals(sa.getNome())) {
						authRichiedenti = true;
						break;
					}
				}
			}
		}

    	
    	// Verifica Token Roles
    	
    	boolean authRuoli = true;
    	StringBuilder detailsBufferRuoli = null;
    	if(verificaAutorizzazioneRuoli){
			boolean checkRuoloRegistro = false;
			boolean checkRuoloEsterno = false;
			if( datiInvocazione.getPd().getAutorizzazioneToken().getTipologiaRuoli()==null ||
				RuoloTipologia.QUALSIASI.equals(datiInvocazione.getPd().getAutorizzazioneToken().getTipologiaRuoli())){
				checkRuoloRegistro = true;
				checkRuoloEsterno = true;
			} 
			else if( RuoloTipologia.INTERNO.equals(datiInvocazione.getPd().getAutorizzazioneToken().getTipologiaRuoli())){
				checkRuoloRegistro = true;
			}
			else if( RuoloTipologia.ESTERNO.equals(datiInvocazione.getPd().getAutorizzazioneToken().getTipologiaRuoli())){
				checkRuoloEsterno = true;
			}
			detailsBufferRuoli = new StringBuilder();
			ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(datiInvocazione.getState());
			ServizioApplicativo sa = null;
			if(idSAToken!=null) {
				sa = configurazionePdDManager.getServizioApplicativo(idSAToken, datiInvocazione.getRequestInfo());
			}
			authRuoli = configurazionePdDManager.
					autorizzazioneTokenRoles(datiInvocazione.getPd(), sa, 
							datiInvocazione.getInfoConnettoreIngresso(), 
							pddContext, datiInvocazione.getRequestInfo(),
							checkRuoloRegistro, checkRuoloEsterno,
							detailsBufferRuoli);
    	}
    	
    	
    	// Esiti Verifiche Richieste
    	
    	if(verificaAutorizzazioneRichiedenti && verificaAutorizzazioneRuoli) {
    		// se una delle due autorizzazione e' andata a buon fine devo autorizzare
    		if(!authRichiedenti && !authRuoli) {
    			// uso eccezione per ruolo che e' più completa come messaggistica
    			EsitoAutorizzazionePortaDelegata esitoNew = esito;
	    		if(idSAToken!=null){
	    			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE, ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.
	    					getErrore404_AutorizzazioneFallitaServizioApplicativo(idSAToken.getNome()));
				}
				else{
					esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE, ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA_ANONIMO.
							getErrore404_AutorizzazioneFallitaServizioApplicativoAnonimo());
				}
				esito.setAutorizzato(false);
				if(detailsBufferRuoli.length()>0) {
					esito.setDetails(detailsBufferRuoli.toString());
				}
	    		return esitoNew;
    		}
    	}
    	else {
	    	if(verificaAutorizzazioneRichiedenti && !authRichiedenti) {
	    		EsitoAutorizzazionePortaDelegata esitoNew = esito;
	    		esitoNew.setAutorizzato(false);
				esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_DENY, ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.
						getErrore404_AutorizzazioneFallitaServizioApplicativo(idSAToken!=null ? idSAToken.getNome() : CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO ));
				return esitoNew;
	    	}
	    	if(verificaAutorizzazioneRuoli && !authRuoli) {
	    		EsitoAutorizzazionePortaDelegata esitoNew = esito;
	    		if(idSAToken!=null){
	    			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE, ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.
	    					getErrore404_AutorizzazioneFallitaServizioApplicativo(idSAToken.getNome()));
				}
				else{
					esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE, ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA_ANONIMO.
							getErrore404_AutorizzazioneFallitaServizioApplicativoAnonimo());
				}
				esito.setAutorizzato(false);
				if(detailsBufferRuoli.length()>0) {
					esito.setDetails(detailsBufferRuoli.toString());
				}
	    		return esitoNew;
	    	}
    	}
    	
    	
		// Verifica Token Scopes
    	
    	AutorizzazioneScope authScope = null;
    	if(datiInvocazione.getPd()!=null) {
    		authScope = datiInvocazione.getPd().getScope();
    	}
    	if(authScope!=null && authScope.sizeScopeList()>0) {
    		EsitoAutorizzazionePortaDelegata esitoNew = (EsitoAutorizzazionePortaDelegata) autorizzazioneScope(authScope, esito, pddContext, datiInvocazione);
    		if(esitoNew.isAutorizzato()==false) {
    			esitoNew.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_MISSING_SCOPE, ErroriIntegrazione.ERRORE_445_TOKEN_AUTORIZZAZIONE_FALLITA.getErroreIntegrazione());
    			return esitoNew;
        	}
    	}
    	
    	// Verifica Token Options
    	
    	if(datiInvocazione.getPd()!=null && datiInvocazione.getPd().getGestioneToken()!=null
    			&& datiInvocazione.getPd().getGestioneToken().getOptions()!=null) {
    		EsitoAutorizzazionePortaDelegata esitoNew = 
    				(EsitoAutorizzazionePortaDelegata) autorizzazioneTokenOptions(datiInvocazione.getPd().getGestioneToken().getOptions(), 
    						esito, pddContext, datiInvocazione,
    						log, msg);
    		if(esitoNew.isAutorizzato()==false) {
    			esitoNew.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_TOKEN_DENY, ErroriIntegrazione.ERRORE_445_TOKEN_AUTORIZZAZIONE_FALLITA.getErroreIntegrazione());
    			return esitoNew;
        	}
    	}
    	
    	return esito;
    	
	}
	
    private static EsitoAutorizzazionePortaDelegata _verificaAutorizzazionePortaDelegata(String tipoAutorizzazione, DatiInvocazionePortaDelegata datiInvocazione,
 		  PdDContext pddContext,IProtocolFactory<?> protocolFactory, OpenSPCoop2Message msg) throws Exception{
    	
    	IAutorizzazionePortaDelegata auth = newInstanceAuthPortaDelegata(tipoAutorizzazione, pddContext, protocolFactory);
    	
    	try {
	    	if(GestoreAutorizzazione.cacheAutorizzazione==null || !auth.saveAuthorizationResultInCache()){
	    		return auth.process(datiInvocazione);
			}
	    	else{
	    		String keyCache = buildCacheKey(true, tipoAutorizzazione, datiInvocazione.getKeyCache(), auth.getSuffixKeyAuthorizationResultInCache(datiInvocazione) );
	
	    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
	    		org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreAutorizzazione.cacheAutorizzazione.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreAutorizzazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaDelegata) in cache.");
						EsitoAutorizzazionePortaDelegata esito = (EsitoAutorizzazionePortaDelegata) response.getObject();
						esito.setEsitoPresenteInCache(true);
						return esito;
					}else if(response.getException()!=null){
						GestoreAutorizzazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaDelegata) in cache.");
						throw (Exception) response.getException();
					}else{
						GestoreAutorizzazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
    		
				String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
				//synchronized (GestoreAutorizzazione.semaphoreAutorizzazionePD) {
				org.openspcoop2.utils.Semaphore lockAutorizzazionePD = getLockAutorizzazionePD(tipoAutorizzazione);
				SemaphoreLock lock = lockAutorizzazionePD.acquire("verificaAutorizzazionePortaDelegata", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreAutorizzazione.cacheAutorizzazione.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreAutorizzazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaDelegata) in cache.");
							EsitoAutorizzazionePortaDelegata esito = (EsitoAutorizzazionePortaDelegata) response.getObject();
							esito.setEsitoPresenteInCache(true);
							return esito;
						}else if(response.getException()!=null){
							GestoreAutorizzazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaDelegata) in cache.");
							throw (Exception) response.getException();
						}else{
							GestoreAutorizzazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
	
					// Effettuo la query
					GestoreAutorizzazione.logger.debug("oggetto con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaDelegata) ricerco nella configurazione...");
					EsitoAutorizzazionePortaDelegata esito = auth.process(datiInvocazione);
	
					// Aggiungo la risposta in cache (se esiste una cache)	
					// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
					// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
					// - impostare il noCache a true
					if(esito!=null){
						if(!esito.isNoCache()){
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
				}finally {
					lockAutorizzazionePD.release(lock, "verificaAutorizzazionePortaDelegata", idTransazione);
				}
	    	}
    	}finally {
    		if(msg!=null) {
    			auth.cleanPostAuth(msg);
    		}
    	}
    }
	
    public static EsitoAutorizzazionePortaApplicativa verificaAutorizzazionePortaApplicativa(String tipoAutorizzazione, DatiInvocazionePortaApplicativa datiInvocazione,
    		PdDContext pddContext,IProtocolFactory<?> protocolFactory, OpenSPCoop2Message msg, Logger log) throws Exception{
    	  
    	checkDatiPortaApplicativa(datiInvocazione);
    
    	EsitoAutorizzazionePortaApplicativa esito = _verificaAutorizzazionePortaApplicativa(tipoAutorizzazione, datiInvocazione, pddContext, protocolFactory, msg);
    	if(esito.isAutorizzato()==false) {
    		return esito;
    	}
    	
    	    	
    	boolean skipProfiloModi = false;
    	if(protocolFactory!=null && Costanti.MODIPA_PROTOCOL_NAME.equals(protocolFactory.getProtocol())) {
    		skipProfiloModi=true;
    	}
    	
    	if(!skipProfiloModi) {
    	
        	// Verifiche Richieste
    		
	    	boolean verificaAutorizzazioneRichiedenti = false;
	    	boolean verificaAutorizzazioneRuoli = false;
	    	if(datiInvocazione.getPa()!=null) {
	    		if(datiInvocazione.getPa().getAutorizzazioneToken()!=null) {
	    			if(StatoFunzionalita.ABILITATO.equals(datiInvocazione.getPa().getAutorizzazioneToken().getAutorizzazioneApplicativi())){
	    				verificaAutorizzazioneRichiedenti=true;
	    			}
	    			if(StatoFunzionalita.ABILITATO.equals(datiInvocazione.getPa().getAutorizzazioneToken().getAutorizzazioneRuoli())){
	    				verificaAutorizzazioneRuoli = true;
	    			}
	    		}
	    	}
	    	else if(datiInvocazione.getPd()!=null){
	    		if(datiInvocazione.getPd().getAutorizzazioneToken()!=null) {
	    			if(StatoFunzionalita.ABILITATO.equals(datiInvocazione.getPd().getAutorizzazioneToken().getAutorizzazioneApplicativi())){
	    				verificaAutorizzazioneRichiedenti=true;
	    			}
	    			if(StatoFunzionalita.ABILITATO.equals(datiInvocazione.getPd().getAutorizzazioneToken().getAutorizzazioneRuoli())){
	    				verificaAutorizzazioneRuoli = true;
	    			}
	    		}
	    	}
    	

	    	// Verifica Token Richiedenti
    	
	    	IDServizioApplicativo idSAToken = null;
	    	if(pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN)) {
	    		idSAToken = (IDServizioApplicativo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN);
	    	}
	    	
	    	boolean authRichiedenti = true;
	    	if(datiInvocazione.getPa()!=null) {
	    		if(verificaAutorizzazioneRichiedenti){
    				authRichiedenti = false;
        			if( idSAToken!=null &&
        					datiInvocazione.getPa().getAutorizzazioneToken().getServiziApplicativi()!=null &&
        					datiInvocazione.getPa().getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList()>0){
        				for (PortaApplicativaAutorizzazioneServizioApplicativo sa : datiInvocazione.getPa().getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativoList()) {
    						if(idSAToken.getNome().equals(sa.getNome()) &&
    								idSAToken.getIdSoggettoProprietario().getTipo().equals(sa.getTipoSoggettoProprietario()) &&
    								idSAToken.getIdSoggettoProprietario().getNome().equals(sa.getNomeSoggettoProprietario())) {
    							authRichiedenti = true;
    							break;
    						}
    					}
        			}
	    		}
	    	}
	    	else if(datiInvocazione.getPd()!=null){
	    		if(verificaAutorizzazioneRichiedenti){
    				authRichiedenti = false;
        			if( idSAToken!=null &&
        					datiInvocazione.getPd().getAutorizzazioneToken().getServiziApplicativi()!=null &&
        					datiInvocazione.getPd().getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList()>0){
        				for (PortaDelegataServizioApplicativo sa : datiInvocazione.getPd().getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativoList()) {
    						if(idSAToken.getNome().equals(sa.getNome())) {
    							authRichiedenti = true;
    							break;
    						}
    					}
        			}
	    		}
	    	}
    	
    	
    		// Verifica Token Roles
    	
	    	boolean authRuoli = true;
	    	StringBuilder detailsBufferRuoli = null;
	    	if(datiInvocazione.getPa()!=null) {
	    		if(verificaAutorizzazioneRuoli){
    				boolean checkRuoloRegistro = false;
    				boolean checkRuoloEsterno = false;
    				if( datiInvocazione.getPa().getAutorizzazioneToken().getTipologiaRuoli()==null ||
    					RuoloTipologia.QUALSIASI.equals(datiInvocazione.getPa().getAutorizzazioneToken().getTipologiaRuoli())){
    					checkRuoloRegistro = true;
    					checkRuoloEsterno = true;
    				} 
    				else if( RuoloTipologia.INTERNO.equals(datiInvocazione.getPa().getAutorizzazioneToken().getTipologiaRuoli())){
    					checkRuoloRegistro = true;
    				}
    				else if( RuoloTipologia.ESTERNO.equals(datiInvocazione.getPa().getAutorizzazioneToken().getTipologiaRuoli())){
    					checkRuoloEsterno = true;
    				}
    				detailsBufferRuoli = new StringBuilder();
    				ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(datiInvocazione.getState());
    				ServizioApplicativo sa = null;
    				if(idSAToken!=null) {
    					sa = configurazionePdDManager.getServizioApplicativo(idSAToken, datiInvocazione.getRequestInfo());
    				}
    				authRuoli = configurazionePdDManager.
    						autorizzazioneTokenRoles(datiInvocazione.getPa(), sa, 
    								datiInvocazione.getInfoConnettoreIngresso(), 
    								pddContext, datiInvocazione.getRequestInfo(),
    								checkRuoloRegistro, checkRuoloEsterno,
    								detailsBufferRuoli);
	    		}
	    	}
	    	else if(datiInvocazione.getPd()!=null){
	    		if(verificaAutorizzazioneRuoli){
    				boolean checkRuoloRegistro = false;
    				boolean checkRuoloEsterno = false;
    				if( datiInvocazione.getPd().getAutorizzazioneToken().getTipologiaRuoli()==null ||
    					RuoloTipologia.QUALSIASI.equals(datiInvocazione.getPd().getAutorizzazioneToken().getTipologiaRuoli())){
    					checkRuoloRegistro = true;
    					checkRuoloEsterno = true;
    				} 
    				else if( RuoloTipologia.INTERNO.equals(datiInvocazione.getPd().getAutorizzazioneToken().getTipologiaRuoli())){
    					checkRuoloRegistro = true;
    				}
    				else if( RuoloTipologia.ESTERNO.equals(datiInvocazione.getPd().getAutorizzazioneToken().getTipologiaRuoli())){
    					checkRuoloEsterno = true;
    				}
    				detailsBufferRuoli = new StringBuilder();
    				ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(datiInvocazione.getState());
    				ServizioApplicativo sa = null;
    				if(idSAToken!=null) {
    					sa = configurazionePdDManager.getServizioApplicativo(idSAToken, datiInvocazione.getRequestInfo());
    				}
    				authRuoli = configurazionePdDManager.
    						autorizzazioneTokenRoles(datiInvocazione.getPd(), sa, 
    								datiInvocazione.getInfoConnettoreIngresso(), 
    								pddContext, datiInvocazione.getRequestInfo(),
    								checkRuoloRegistro, checkRuoloEsterno,
    								detailsBufferRuoli);
	    		}
	    	}
	    	
	    	
	    	// Esiti Verifiche Richieste
	    	
	    	if(verificaAutorizzazioneRichiedenti && verificaAutorizzazioneRuoli) {
	    		// se una delle due autorizzazione e' andata a buon fine devo autorizzare
	    		if(!authRichiedenti && !authRuoli) {
	    			// uso eccezione per ruolo che e' più completa come messaggistica
	    			EsitoAutorizzazionePortaApplicativa esitoNew = esito;
		    		String errore = "";
		    		esitoNew.setErroreCooperazione(IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE, ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
		    		esitoNew.setAutorizzato(false);
					if(detailsBufferRuoli.length()>0) {
						esitoNew.setDetails(detailsBufferRuoli.toString());
					}
		    		return esitoNew;
	    		}
	    	}
	    	else {
		    	if(verificaAutorizzazioneRichiedenti && !authRichiedenti) {
		    		EsitoAutorizzazionePortaApplicativa esitoNew = esito;
		    		esitoNew.setAutorizzato(false);
		    		IDServizio idServizio = datiInvocazione.getIdServizio();
		    		String errore = org.openspcoop2.pdd.core.autorizzazione.pa.AbstractAutorizzazioneBase.getErrorString(idSAToken, null, idServizio);
					if(esitoNew.getDetails()!=null){
						errore = errore + " ("+esitoNew.getDetails()+")";
					}
		    		esitoNew.setErroreCooperazione(IntegrationFunctionError.AUTHORIZATION_DENY, ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
					return esitoNew;
		    	}
		    	if(verificaAutorizzazioneRuoli && !authRuoli) {
		    		EsitoAutorizzazionePortaApplicativa esitoNew = esito;
		    		String errore = "";
		    		esitoNew.setErroreCooperazione(IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE, ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
		    		esitoNew.setAutorizzato(false);
					if(detailsBufferRuoli.length()>0) {
						esitoNew.setDetails(detailsBufferRuoli.toString());
					}
		    		return esitoNew;
		    	}
	    	}
    	}
    	
    	// Verifica Token Scopes
    	
    	AutorizzazioneScope authScope = null;
    	if(datiInvocazione.getPa()!=null) {
    		authScope = datiInvocazione.getPa().getScope();
    	}
    	else if(datiInvocazione.getPd()!=null){
    		authScope = datiInvocazione.getPd().getScope();
    	}
    	if(authScope!=null && authScope.sizeScopeList()>0) {
    		EsitoAutorizzazionePortaApplicativa esitoNew = (EsitoAutorizzazionePortaApplicativa) autorizzazioneScope(authScope, esito, pddContext, datiInvocazione);
    		if(!esitoNew.isAutorizzato()) {
    			esitoNew.setErroreCooperazione(IntegrationFunctionError.AUTHORIZATION_MISSING_SCOPE, ErroriCooperazione.TOKEN_AUTORIZZAZIONE_FALLITA.getErroreCooperazione());
    			return esitoNew;
        	}
    	}
    	
    	
    	// Verifica Token Options
    	
    	String tokenOptions = null;
    	if(datiInvocazione.getPa()!=null) {
    		if(datiInvocazione.getPa().getGestioneToken()!=null) {
    			tokenOptions = datiInvocazione.getPa().getGestioneToken().getOptions();
    		}
    	}
    	else if(datiInvocazione.getPd()!=null){
    		if(datiInvocazione.getPd().getGestioneToken()!=null) {
    			tokenOptions = datiInvocazione.getPd().getGestioneToken().getOptions();
    		}
    	}
    	
    	if(tokenOptions!=null) {
    		EsitoAutorizzazionePortaApplicativa esitoNew = 
    				(EsitoAutorizzazionePortaApplicativa) autorizzazioneTokenOptions(tokenOptions, 
    						esito, pddContext, datiInvocazione,
    						log, msg);
    		if(!esitoNew.isAutorizzato()) {
    			esitoNew.setErroreCooperazione(IntegrationFunctionError.AUTHORIZATION_TOKEN_DENY, ErroriCooperazione.TOKEN_AUTORIZZAZIONE_FALLITA.getErroreCooperazione());
    			return esitoNew;
        	}
    	}
    	
    	return esito;
    	
    }
    private static EsitoAutorizzazionePortaApplicativa _verificaAutorizzazionePortaApplicativa(String tipoAutorizzazione, DatiInvocazionePortaApplicativa datiInvocazione,
    		PdDContext pddContext,IProtocolFactory<?> protocolFactory, OpenSPCoop2Message msg) throws Exception{
    	
    	IAutorizzazionePortaApplicativa auth = newInstanceAuthPortaApplicativa(tipoAutorizzazione, pddContext, protocolFactory);
    	
    	try {
	    	if(GestoreAutorizzazione.cacheAutorizzazione==null || !auth.saveAuthorizationResultInCache()){
	    		return auth.process(datiInvocazione);
			}
	    	else{
	    		String keyCache = buildCacheKey(false, tipoAutorizzazione, datiInvocazione.getKeyCache(), auth.getSuffixKeyAuthorizationResultInCache(datiInvocazione));
	
	    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
	    		org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreAutorizzazione.cacheAutorizzazione.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreAutorizzazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaApplicativa) in cache.");
						EsitoAutorizzazionePortaApplicativa esito = (EsitoAutorizzazionePortaApplicativa) response.getObject();
						esito.setEsitoPresenteInCache(true);
						return esito;
					}else if(response.getException()!=null){
						GestoreAutorizzazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaApplicativa) in cache.");
						throw (Exception) response.getException();
					}else{
						GestoreAutorizzazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
	    		
				String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
				//synchronized (GestoreAutorizzazione.semaphoreAutorizzazionePA) {
				org.openspcoop2.utils.Semaphore lockAutorizzazionePA = getLockAutorizzazionePA(tipoAutorizzazione);
				SemaphoreLock lock = lockAutorizzazionePA.acquire("verificaAutorizzazionePortaApplicativa", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreAutorizzazione.cacheAutorizzazione.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreAutorizzazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaApplicativa) in cache.");
							EsitoAutorizzazionePortaApplicativa esito = (EsitoAutorizzazionePortaApplicativa) response.getObject();
							esito.setEsitoPresenteInCache(true);
							return esito;
						}else if(response.getException()!=null){
							GestoreAutorizzazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaApplicativa) in cache.");
							throw (Exception) response.getException();
						}else{
							GestoreAutorizzazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
	
					// Effettuo la query
					GestoreAutorizzazione.logger.debug("oggetto con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaApplicativa) ricerco nella configurazione...");
					EsitoAutorizzazionePortaApplicativa esito = auth.process(datiInvocazione);
	
					// Aggiungo la risposta in cache (se esiste una cache)	
					// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
					// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
					// - impostare il noCache a true
					if(esito!=null){
						if(!esito.isNoCache()){
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
				}finally {
					lockAutorizzazionePA.release(lock, "verificaAutorizzazionePortaApplicativa", idTransazione);
				}
	    	}
    	}finally {
    		if(msg!=null) {
    			auth.cleanPostAuth(msg);
    		}
    	}
    }
    
    private static void checkDatiPortaDelegata(DatiInvocazionePortaDelegata datiInvocazione) throws AutorizzazioneException{
    	
    	IDServizio idServizio = datiInvocazione.getIdServizio();
    	if(idServizio==null)
			throw new AutorizzazioneException("(Parametri) IDServizio non definito");
		if(idServizio.getTipo()==null || idServizio.getNome()==null || idServizio.getVersione()==null)
			throw new AutorizzazioneException("(Parametri) Servizio non definito");
		if(idServizio.getSoggettoErogatore()==null || idServizio.getSoggettoErogatore().getTipo()==null || idServizio.getSoggettoErogatore().getNome()==null)
			throw new AutorizzazioneException("(Parametri) Soggetto erogatore non definito");
    	
    	IDPortaDelegata idPD = datiInvocazione.getIdPD();
    	if(idPD==null)
			throw new AutorizzazioneException("(Parametri) IDPortaDelegata non definito");
    	if(idPD.getNome()==null)
			throw new AutorizzazioneException("(Parametri) IDPortaDelegata.nome non definito");
		
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
    	if(idServizio.getTipo()==null || idServizio.getNome()==null || idServizio.getVersione()==null)
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
		
		// In caso di autenticazione dei soggetti abilitata, il parametro soggetto fruitore può non essere definito
//    	IDSoggetto idSoggettoFruitore = datiInvocazione.getIdSoggettoFruitore();
//		if(idSoggettoFruitore==null || idSoggettoFruitore.getTipo()==null || idSoggettoFruitore.getNome()==null)
//			throw new AutorizzazioneException("(Parametri) IDSoggettoFruitore non definito");
		
		RuoloBusta ruolo = datiInvocazione.getRuoloBusta();
		if(ruolo==null)
			throw new AutorizzazioneException("(Parametri) RuoloBusta non definito");
    }
    
    private static String buildCacheKey(boolean portaDelegata, String tipoAutorizzazione, String keyCache, String suffixKeyCache) throws AutorizzazioneException{
    	StringBuilder bf = new StringBuilder();
    	
    	if(portaDelegata)
    		bf.append("PD ");
    	else
    		bf.append("PA ");
    	
    	bf.append(" Auth:").append(tipoAutorizzazione).append(" ");
    	
    	bf.append(keyCache);
    	
    	if(suffixKeyCache!=null && !"".equals(suffixKeyCache)){
    		bf.append(" ");
    		bf.append(suffixKeyCache);
    	}
    	
    	return bf.toString();
    }
    
    private static IAutorizzazionePortaDelegata newInstanceAuthPortaDelegata(String tipoAutorizzazione,PdDContext pddContext, IProtocolFactory<?> protocolFactory) throws AutorizzazioneException{
    	IAutorizzazionePortaDelegata auth = null;
		try{
			PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
			auth = (IAutorizzazionePortaDelegata) pluginLoader.newAutorizzazionePortaDelegata(tipoAutorizzazione);
		}catch(Exception e){
			throw new AutorizzazioneException(e.getMessage(),e); // descrizione errore già corretta
		}
		String classType = null; 
    	try{
			classType = auth.getClass().getName();
			AbstractCore.init(auth, pddContext, protocolFactory);
			return auth;
		}catch(Exception e){
			throw new AutorizzazioneException("Riscontrato errore durante l'inizializzazione della classe ["+classType+
					"] che definisce l'autorizzazione della fruizione: "+e.getMessage(),e);
		}
    }
    
    private static IAutorizzazionePortaApplicativa newInstanceAuthPortaApplicativa(String tipoAutorizzazione,PdDContext pddContext, IProtocolFactory<?> protocolFactory) throws AutorizzazioneException{
    	IAutorizzazionePortaApplicativa auth = null;
		try{
			PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
			auth = (IAutorizzazionePortaApplicativa) pluginLoader.newAutorizzazionePortaApplicativa(tipoAutorizzazione);
		}catch(Exception e){
			throw new AutorizzazioneException(e.getMessage(),e); // descrizione errore già corretta
		}
		String classType = null; 
    	try{
			classType = auth.getClass().getName();
			AbstractCore.init(auth, pddContext, protocolFactory);
			return auth;
		}catch(Exception e){
			throw new AutorizzazioneException("Riscontrato errore durante l'inizializzazione della classe ["+classType+
					"] che definisce l'autorizzazione della erogazione: "+e.getMessage(),e);
		}
    }
    
    private static EsitoAutorizzazione autorizzazioneScope(AutorizzazioneScope authScope, EsitoAutorizzazione esito, 
    		PdDContext pddContext, AbstractDatiInvocazione datiInvocazione) throws Exception {
    	if(authScope!=null && authScope.sizeScopeList()>0) {
    		
    		InformazioniToken informazioniTokenNormalizzate = null;
    		Object oInformazioniTokenNormalizzate = pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
    		if(oInformazioniTokenNormalizzate!=null) {
    			informazioniTokenNormalizzate = (InformazioniToken) oInformazioniTokenNormalizzate;
    		}
    		boolean autorizzato = true;
    		String errorMessage = null;
    		if(informazioniTokenNormalizzate==null || informazioniTokenNormalizzate.getScopes()==null || informazioniTokenNormalizzate.getScopes().size()<=0) {
    			errorMessage = "Token without scopes";
    			autorizzato = false;
    		}
    		else {
	    		boolean foundAlmostOne = false;
	    		for (Scope scope : authScope.getScopeList()) {
					org.openspcoop2.core.registry.Scope scopeOp2Registry = RegistroServiziManager.getInstance(datiInvocazione.getState()).getScope(scope.getNome(),null,datiInvocazione.getRequestInfo());
					String nomeScope = scopeOp2Registry.getNome();
					if(scopeOp2Registry.getNomeEsterno()!=null && !"".equals(scopeOp2Registry.getNomeEsterno())) {
						nomeScope = scopeOp2Registry.getNomeEsterno();
					}
					if(informazioniTokenNormalizzate.getScopes().contains(nomeScope)==false) {
						if(ScopeTipoMatch.ALL.equals(authScope.getMatch())) {
							autorizzato = false;
							errorMessage = "Scope '"+nomeScope+"' not found";
							break;
						}
					}
					else {
						foundAlmostOne = true;
					}
				}
	    		if(!foundAlmostOne) {
	    			autorizzato = false;
	    			errorMessage = "Scopes not found";
	    		}
    		}
    		
    		if(!autorizzato) {
    		
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
    			
        		if(genericMessage) {
        			esito.setDetails(errorMessage);
        		}
        		
        		String [] scopes = new String[authScope.sizeScopeList()];
        		for (int i = 0; i < authScope.sizeScopeList(); i++) {
        			org.openspcoop2.core.registry.Scope scopeOp2Registry = RegistroServiziManager.getInstance(datiInvocazione.getState()).getScope(authScope.getScope(i).getNome(),null,datiInvocazione.getRequestInfo());
					String nomeScope = scopeOp2Registry.getNome();
					if(scopeOp2Registry.getNomeEsterno()!=null) {
						nomeScope = scopeOp2Registry.getNomeEsterno();
					}
					scopes[i] = nomeScope;
        		}
        		
        		esito.setAutorizzato(false);
        		if(emptyMessage) {
        			esito.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.insufficient_scope, realm, genericMessage, errorMessage, scopes));
        		}
        		else {
        			esito.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.insufficient_scope, realm, genericMessage, errorMessage, scopes));
        		}
        		
    		}
    		
    		
    	}
    	
    	return esito;
    }

    private static final String ATTRIBUTE_AUTHORITY_PREFIX = "aa.";
    private static final String ATTRIBUTE_PREFIX = "attribute.";
    private static EsitoAutorizzazione autorizzazioneTokenOptions(String tokenOptions, EsitoAutorizzazione esito, 
    		PdDContext pddContext, AbstractDatiInvocazione datiInvocazione,
    		Logger log, OpenSPCoop2Message message) throws Exception {
    	
    	boolean autorizzato = true;
		String errorMessage = null;
    	if(tokenOptions!=null) {
    		
    		/**Properties properties = PropertiesUtilities.convertTextToProperties(tokenOptions);*/
    		// Fix per preservare l'ordine di configurazione
    		SortedMap<List<String>> properties = PropertiesUtilities.convertTextToSortedListMap(tokenOptions);
			if(properties!=null && properties.size()>0) {
			
	    		InformazioniToken informazioniTokenNormalizzate = null;
	    		Object oInformazioniTokenNormalizzate = pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
	    		if(oInformazioniTokenNormalizzate!=null) {
	    			informazioniTokenNormalizzate = (InformazioniToken) oInformazioniTokenNormalizzate;
	    		}
	    	
	    		if(informazioniTokenNormalizzate==null || informazioniTokenNormalizzate.getClaims()==null || informazioniTokenNormalizzate.getClaims().size()<=0) {
	    			autorizzato = false;
	    			errorMessage = "Token without claims";
	    		}
	    		else {
	    				    			
	    			/* Costruisco dynamic Map */
	    			boolean bufferMessageReadOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
	    			Map<String, Object> dynamicMap = DynamicUtils.buildDynamicMap(message, pddContext, datiInvocazione.getBusta(), log, bufferMessageReadOnly);
	    				    			
	    			/* Analisi claims di autorizzazione */
	    			
	    			/**System.out.println("\n\n@ ===== AUTH TOKEN CLAIMS =====");*/
					
					List<String> keys = properties.keys();
		    		for (String key : keys) {
		    			
		    			List<String> expectedValues = properties.get(key);
		    			if(expectedValues!=null && !expectedValues.isEmpty()) {
		    				
		    				for (String expectedValue : expectedValues) {
								
								/**System.out.println("check '"+key+"'='"+expectedValue+"'");*/
								
								String attributeAuthorityName = null;
								String attributeName = null;
								boolean findAttribute = false;
								Object valueAttributeObject = null; 
								
								// verifica presenza claim nel token
								log.debug("Verifico presenza '"+key+"' nel token ...");
								if(!informazioniTokenNormalizzate.getClaims().containsKey(key)) {
									
									if(key.startsWith(ATTRIBUTE_AUTHORITY_PREFIX) && key.length()>ATTRIBUTE_AUTHORITY_PREFIX.length()) {
										String tmp = key.substring(ATTRIBUTE_AUTHORITY_PREFIX.length());
										//System.out.println("DEBUG ["+tmp+"]");
										if(tmp!=null && tmp.contains(".")) {
											int indexOf = tmp.indexOf(".");
											if(indexOf>0 && indexOf<tmp.length()) {
												attributeAuthorityName = tmp.substring(0, indexOf);
												tmp = tmp.substring(indexOf);
												//System.out.println("DEBUG attributeAuthorityName["+attributeAuthorityName+"] tmp["+tmp+"]");
												if(tmp.startsWith(("."+ATTRIBUTE_PREFIX)) && tmp.length()>("."+ATTRIBUTE_PREFIX).length()) {
													indexOf = tmp.indexOf(".",1); // ho appurato che iniza con '.'
													if(indexOf>0 && indexOf<tmp.length()) {
														attributeName = tmp.substring(indexOf);
														if(attributeName.startsWith(".") && attributeName.length()>1) {
															attributeName = attributeName.substring(1);
														}
														//System.out.println("DEBUG attributeName["+attributeName+"]");
													}
												}
											}
										}
									}
									else if(key.startsWith(ATTRIBUTE_PREFIX) && key.length()>ATTRIBUTE_PREFIX.length()) {
										attributeName = key.substring(ATTRIBUTE_PREFIX.length());
									}
									
									if(attributeAuthorityName!=null && attributeName!=null) {
										if(informazioniTokenNormalizzate.getAa()!=null && 
												informazioniTokenNormalizzate.getAa().isMultipleAttributeAuthorities()!=null &&
												informazioniTokenNormalizzate.getAa().isMultipleAttributeAuthorities().getValue()!=null &&
												informazioniTokenNormalizzate.getAa().isMultipleAttributeAuthorities().getValue().booleanValue() &&
												informazioniTokenNormalizzate.getAa().getAttributes()!=null && 
												informazioniTokenNormalizzate.getAa().getAttributes().containsKey(attributeAuthorityName)) {
											Object o = informazioniTokenNormalizzate.getAa().getAttributes().get(attributeAuthorityName);
											if(o instanceof Map) {
												@SuppressWarnings("unchecked")
												Map<String, Object> map = (Map<String, Object>) o;
												if(map.containsKey(attributeName)) {
													findAttribute = true;
													valueAttributeObject = map.get(attributeName);
												}
											}
										}
									}
									else if(attributeName!=null) {
										if(informazioniTokenNormalizzate.getAa()!=null && 
												(
														informazioniTokenNormalizzate.getAa().isMultipleAttributeAuthorities()==null || 
														informazioniTokenNormalizzate.getAa().isMultipleAttributeAuthorities().getValue()==null || 
														!informazioniTokenNormalizzate.getAa().isMultipleAttributeAuthorities().getValue()
												) &&
												informazioniTokenNormalizzate.getAa().getAttributes()!=null && 
												informazioniTokenNormalizzate.getAa().getAttributes().containsKey(attributeName)) {
											findAttribute = true;
											valueAttributeObject = informazioniTokenNormalizzate.getAa().getAttributes().get(attributeName);
										}
									}
									
									if(!findAttribute) {
										if(CostantiAutorizzazione.AUTHZ_UNDEFINED.equalsIgnoreCase(expectedValue)) {
											continue;
										}
										autorizzato = false;
										if(attributeName!=null) {
											errorMessage = "Token without attribute '"+key+"'";
										}
										else {
											errorMessage = "Token without claim '"+key+"'";
										}
										break;
									}
								}
								
								List<String> lClaimValues =null;
								if(findAttribute && valueAttributeObject!=null) {
									lClaimValues = TokenUtilities.getClaimValues(valueAttributeObject);
								}
								else {
									Object valueClaimObject = informazioniTokenNormalizzate.getClaims().get(key);
									lClaimValues = TokenUtilities.getClaimValues(valueClaimObject);
								}
								String nomeClaimAttribute = (attributeName!=null) ? attributeName : key;
								if(lClaimValues==null || lClaimValues.isEmpty()) {
									if(CostantiAutorizzazione.AUTHZ_UNDEFINED.equalsIgnoreCase(expectedValue)) {
										continue;
									}
									autorizzato = false;
									if(attributeName!=null) {
										errorMessage = "Token with attribute '"+nomeClaimAttribute+"' without value";
									}
									else {
										errorMessage = "Token with claim '"+nomeClaimAttribute+"' without value";
									}
									break;
								}
								
								// verifica valore atteso per il claim
								String object = (attributeName!=null) ? "Attribute" : "Claim";
								log.debug("Verifico valore '"+expectedValue+"' per "+object.toLowerCase()+" '"+nomeClaimAttribute+"' nel token ...");
								if(expectedValue==null) {
									throw new Exception(object+" '"+nomeClaimAttribute+"' without expected value");
								}
								expectedValue = expectedValue.trim();
								
								if(CostantiAutorizzazione.AUTHZ_ANY_VALUE.equalsIgnoreCase(expectedValue)) {
									
									/** ANY VALUE */
									
									log.debug("Verifico valore "+object.toLowerCase()+" '"+nomeClaimAttribute+"' che non sia null e non sia vuoto ...");
									
									// basta che abbia un valore not null
									boolean ok = false;
									for (String v : lClaimValues) {
										if(v!=null && !"".equals(v)) {
											ok = true;
											break;
										}
									}
									if(!ok) {
										autorizzato = false;
										errorMessage = "Token "+object.toLowerCase()+" '"+nomeClaimAttribute+"' with unexpected empty value";
										break;
									}
								}
								else if(CostantiAutorizzazione.AUTHZ_UNDEFINED.equalsIgnoreCase(expectedValue)) { 
									
									/** NOT PRESENT */
									
									log.debug("Verifico valore "+object.toLowerCase()+" '"+nomeClaimAttribute+"' sia null o sia vuoto ...");
									
									// basta che abbia un valore
									boolean ok = false;
									for (String v : lClaimValues) {
										if(v!=null && !"".equals(v)) {
											ok = true;
											break;
										}
									}
									if(ok) {
										autorizzato = false;
										errorMessage = "Token unexpected "+object.toLowerCase()+" '"+nomeClaimAttribute+"'";
										break;
									}
									
								}
								else if(
										(
												expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_MATCH_PREFIX.toLowerCase())
												||
												expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_FIND_PREFIX.toLowerCase())
												||
												expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_NOT_MATCH_PREFIX.toLowerCase())
												||
												expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_NOT_FIND_PREFIX.toLowerCase())
										) 
										&&
										expectedValue.toLowerCase().endsWith(CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX.toLowerCase())) {
									
									/** REGULAR EXPRESSION MATCH/FIND */
									
									boolean match = expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_MATCH_PREFIX.toLowerCase())
											||
											expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_NOT_MATCH_PREFIX.toLowerCase());
									boolean not = expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_NOT_MATCH_PREFIX.toLowerCase())
											||
											expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_NOT_FIND_PREFIX.toLowerCase());
									String regexpPattern = null;
									if(match) {
										int length = -1;
										if(expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_MATCH_PREFIX.toLowerCase())) {
											length = CostantiAutorizzazione.AUTHZ_REGEXP_MATCH_PREFIX.length();
										}
										else {
											length = CostantiAutorizzazione.AUTHZ_REGEXP_NOT_MATCH_PREFIX.length();
										}
										if(expectedValue.length()<= (length+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX.length()) ) {
											throw new Exception(object+" '"+nomeClaimAttribute+"' without expected regexp match");
										}
										regexpPattern = expectedValue.substring(length, (expectedValue.length()-CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX.length()));
									}
									else {
										int length = -1;
										if(expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_FIND_PREFIX.toLowerCase())) {
											length = CostantiAutorizzazione.AUTHZ_REGEXP_FIND_PREFIX.length();
										}
										else {
											length = CostantiAutorizzazione.AUTHZ_REGEXP_NOT_FIND_PREFIX.length();
										}
										if(expectedValue.length()<= (length+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX.length()) ) {
											throw new Exception(object+" '"+nomeClaimAttribute+"' without expected regexp find");
										}
										regexpPattern = expectedValue.substring(length, (expectedValue.length()-CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX.length()));
									}
									regexpPattern = regexpPattern.trim();
									log.debug("Verifico valore del "+object.toLowerCase()+" '"+nomeClaimAttribute+"' tramite espressione regolare (match:"+match+") '"+regexpPattern+"' ...");
									
									// basta che un valore abbia match
									boolean ok = false;
									for (String v : lClaimValues) {
										if( match ? RegularExpressionEngine.isMatch(v, regexpPattern) : RegularExpressionEngine.isFind(v, regexpPattern)) {
											ok = true;
											break;
										}
									}
									if(not) {
										if(ok) {
											autorizzato = false;
											String tipo = match ? "match" : "find";
											errorMessage = "Token "+object.toLowerCase()+" '"+nomeClaimAttribute+"' with unexpected value (regExpr not "+tipo+" failed)";
											break;
										}
									}
									else {
										if(!ok) {
											autorizzato = false;
											String tipo = match ? "match" : "find";
											errorMessage = "Token "+object.toLowerCase()+" '"+nomeClaimAttribute+"' with unexpected value (regExpr "+tipo+" failed)";
											break;
										}
									}
									
								}
								else {
								
									/** VALUE (con PLACEHOLDERS) */
									
									boolean not = false;
									if(
											expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_NOT_PREFIX.toLowerCase())
											&&
											expectedValue.toLowerCase().endsWith(CostantiAutorizzazione.AUTHZ_NOT_SUFFIX.toLowerCase())) {
										not = true;
										if(expectedValue.length()<= (CostantiAutorizzazione.AUTHZ_NOT_PREFIX.length()+CostantiAutorizzazione.AUTHZ_NOT_SUFFIX.length()) ) {
											throw new Exception(object+" '"+nomeClaimAttribute+"' without value in not condition");
										}
										expectedValue = expectedValue.substring(CostantiAutorizzazione.AUTHZ_NOT_PREFIX.length(), (expectedValue.length()-CostantiAutorizzazione.AUTHZ_NOT_SUFFIX.length()));
									}
									
									boolean ignoreCase = false;
									if(
											expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_IGNORE_CASE_PREFIX.toLowerCase())
											&&
											expectedValue.toLowerCase().endsWith(CostantiAutorizzazione.AUTHZ_IGNORE_CASE_SUFFIX.toLowerCase())) {
										ignoreCase = true;
										if(expectedValue.length()<= (CostantiAutorizzazione.AUTHZ_IGNORE_CASE_PREFIX.length()+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_SUFFIX.length()) ) {
											throw new Exception(object+" '"+nomeClaimAttribute+"' without value in ignore case condition");
										}
										expectedValue = expectedValue.substring(CostantiAutorizzazione.AUTHZ_IGNORE_CASE_PREFIX.length(), (expectedValue.length()-CostantiAutorizzazione.AUTHZ_IGNORE_CASE_SUFFIX.length()));
									}
									
									try {
										expectedValue = DynamicUtils.convertDynamicPropertyValue(key, expectedValue, dynamicMap, pddContext);
									}catch(Exception e) {
										String msg = "Conversione valore per "+object.toLowerCase()+" '"+nomeClaimAttribute+"' non riuscita (valore: "+expectedValue+"): "+e.getMessage();
										//throw new Exception(msg,e);
										log.error(msg, e);
										autorizzato = false;
										errorMessage = "Token "+object.toLowerCase()+" '"+nomeClaimAttribute+"' not verifiable; unprocessable dynamic value '"+expectedValue+"': "+e.getMessage();
										break;
									}
									
									boolean ok = false;
									if(expectedValue.contains(",")) {
										String [] values = expectedValue.split(",");
										ok = false;
										for (int i = 0; i < values.length; i++) {
											String v = values[i].trim();
											if(ignoreCase) {
												boolean find = false;
												for (String claim : lClaimValues) {
													if(claim.equalsIgnoreCase(v)) {
														find = true;
														break;
													}	
												}
												if(find) {
													ok = true;
													break;
												}
											}
											else {
												if(lClaimValues.contains(v)) {
													ok = true;
													break;
												}
											}
										}
									}
									else {
										if(ignoreCase) {
											boolean find = false;
											for (String claim : lClaimValues) {
												if(claim.equalsIgnoreCase(expectedValue)) {
													find = true;
													break;
												}	
											}
											ok = find;
										}
										else {
											ok = lClaimValues.contains(expectedValue);
										}
									}
									
									if(not) {
										if(ok) {
											autorizzato = false;
											errorMessage = "Token "+object.toLowerCase()+" '"+nomeClaimAttribute+"' with unauthorized value";
											break;
										}
									}
									else {
										if(!ok) {
											autorizzato = false;
											errorMessage = "Token "+object.toLowerCase()+" '"+nomeClaimAttribute+"' with unexpected value";
											break;
										}
									}
								}
							}
		    			}
		    		}
	    		}
			}
    		
    	}
    	
    	if(!autorizzato) {
    		
			String realm = "GovWay";
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
			
    		if(genericMessage) {
    			esito.setDetails(errorMessage);
    		}
    		
    		esito.setAutorizzato(false);
    		if(emptyMessage) {
    			esito.setErrorMessage(WWWAuthenticateGenerator.buildErrorMessage(WWWAuthenticateErrorCode.insufficient_scope, realm, genericMessage, errorMessage));
    		}
    		else {
    			esito.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildHeaderValue(WWWAuthenticateErrorCode.insufficient_scope, realm, genericMessage, errorMessage));
    		}
		}
		
    	
    	return esito;
    }
    
    
    
    
    
    
    
    /*----------------- AUTORIZZAZIONE CONTENUTO --------------------*/
	

	// NOTA: le chiamate ad autorizzazione per contenuto possono essere cachate solamente se viene definito un suffix, 
    //       poiche' altrimenti variano sempre i contenuti e non finiscono nella chiave di cache.
	public static EsitoAutorizzazionePortaDelegata verificaAutorizzazioneContenutoPortaDelegata(String tipoAutorizzazione, DatiInvocazionePortaDelegata datiInvocazione,
	 		  PdDContext pddContext,IProtocolFactory<?> protocolFactory, OpenSPCoop2Message msg, Logger log) throws Exception{
		
		checkDatiPortaDelegataAuthContenuto(datiInvocazione, msg);
    	
		EsitoAutorizzazionePortaDelegata esito = _verificaAutorizzazioneContenutoPortaDelegata(tipoAutorizzazione, datiInvocazione, pddContext, protocolFactory, msg);
    	if(esito.isAutorizzato()==false) {
    		return esito;
    	}
		
    	return esito;
    	
	}
	
    private static EsitoAutorizzazionePortaDelegata _verificaAutorizzazioneContenutoPortaDelegata(String tipoAutorizzazione, DatiInvocazionePortaDelegata datiInvocazione,
 		  PdDContext pddContext,IProtocolFactory<?> protocolFactory, OpenSPCoop2Message msg) throws Exception{
    	
    	IAutorizzazioneContenutoPortaDelegata auth = newInstanceAuthContenutoPortaDelegata(tipoAutorizzazione, pddContext, protocolFactory);
    	
    	try {
	    	if(GestoreAutorizzazione.cacheAutorizzazione==null || !auth.saveAuthorizationResultInCache()){
	    		return auth.process(datiInvocazione, msg);
			}
	    	else{
	    		String keyCache = buildCacheKeyContenuto(true, tipoAutorizzazione, datiInvocazione.getKeyCache(), auth.getSuffixKeyAuthorizationResultInCache(datiInvocazione, msg) );
	
	    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
	    		org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreAutorizzazione.cacheAutorizzazione.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreAutorizzazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaDelegata) in cache.");
						EsitoAutorizzazionePortaDelegata esito = (EsitoAutorizzazionePortaDelegata) response.getObject();
						esito.setEsitoPresenteInCache(true);
						return esito;
					}else if(response.getException()!=null){
						GestoreAutorizzazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaDelegata) in cache.");
						throw (Exception) response.getException();
					}else{
						GestoreAutorizzazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
	    			    		
				String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
				//synchronized (GestoreAutorizzazione.semaphoreAutorizzazioneContenutiPD) {
				org.openspcoop2.utils.Semaphore lockAutorizzazioneContenutiPD = getLockAutorizzazioneContenutiPD(tipoAutorizzazione);
				SemaphoreLock lock = lockAutorizzazioneContenutiPD.acquire("verificaAutorizzazioneContenutoPortaDelegata", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreAutorizzazione.cacheAutorizzazione.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreAutorizzazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaDelegata) in cache.");
							EsitoAutorizzazionePortaDelegata esito = (EsitoAutorizzazionePortaDelegata) response.getObject();
							esito.setEsitoPresenteInCache(true);
							return esito;
						}else if(response.getException()!=null){
							GestoreAutorizzazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaDelegata) in cache.");
							throw (Exception) response.getException();
						}else{
							GestoreAutorizzazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
	
					// Effettuo la query
					GestoreAutorizzazione.logger.debug("oggetto con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaDelegata) ricerco nella configurazione...");
					EsitoAutorizzazionePortaDelegata esito = auth.process(datiInvocazione, msg);
	
					// Aggiungo la risposta in cache (se esiste una cache)	
					// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
					// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
					// - impostare il noCache a true
					if(esito!=null){
						if(!esito.isNoCache()){
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
				}finally {
					lockAutorizzazioneContenutiPD.release(lock, "verificaAutorizzazioneContenutoPortaDelegata", idTransazione);
				}
	    	}
    	}finally {
    		if(msg!=null) {
    			auth.cleanPostAuth(msg);
    		}
    	}
    }
	
    public static EsitoAutorizzazionePortaApplicativa verificaAutorizzazioneContenutoPortaApplicativa(String tipoAutorizzazione, DatiInvocazionePortaApplicativa datiInvocazione,
    		PdDContext pddContext,IProtocolFactory<?> protocolFactory, OpenSPCoop2Message msg, Logger log) throws Exception{
    	  
    	checkDatiPortaApplicativaAuthContenuto(datiInvocazione, msg);
    
    	EsitoAutorizzazionePortaApplicativa esito = _verificaAutorizzazioneContenutoPortaApplicativa(tipoAutorizzazione, datiInvocazione, pddContext, protocolFactory, msg);
    	if(esito.isAutorizzato()==false) {
    		return esito;
    	}
    	
    	return esito;
    	
    }
    private static EsitoAutorizzazionePortaApplicativa _verificaAutorizzazioneContenutoPortaApplicativa(String tipoAutorizzazione, DatiInvocazionePortaApplicativa datiInvocazione,
    		PdDContext pddContext,IProtocolFactory<?> protocolFactory, OpenSPCoop2Message msg) throws Exception{
    	
    	IAutorizzazioneContenutoPortaApplicativa auth = newInstanceAuthContenutoPortaApplicativa(tipoAutorizzazione, pddContext, protocolFactory);
    	
    	try {
	    	if(GestoreAutorizzazione.cacheAutorizzazione==null || !auth.saveAuthorizationResultInCache()){
	    		return auth.process(datiInvocazione, msg);
			}
	    	else{
	    		String keyCache = buildCacheKeyContenuto(false, tipoAutorizzazione, datiInvocazione.getKeyCache(), auth.getSuffixKeyAuthorizationResultInCache(datiInvocazione, msg));
	
	    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
	    		org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreAutorizzazione.cacheAutorizzazione.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						GestoreAutorizzazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaApplicativa) in cache.");
						EsitoAutorizzazionePortaApplicativa esito = (EsitoAutorizzazionePortaApplicativa) response.getObject();
						esito.setEsitoPresenteInCache(true);
						return esito;
					}else if(response.getException()!=null){
						GestoreAutorizzazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaApplicativa) in cache.");
						throw (Exception) response.getException();
					}else{
						GestoreAutorizzazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
	    		
				String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
				//synchronized (GestoreAutorizzazione.semaphoreAutorizzazioneContenutiPA) {
				org.openspcoop2.utils.Semaphore lockAutorizzazioneContenutiPA = getLockAutorizzazioneContenutiPA(tipoAutorizzazione);
				SemaphoreLock lock = lockAutorizzazioneContenutiPA.acquire("verificaAutorizzazioneContenutoPortaApplicativa", idTransazione);
				try {
					
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) GestoreAutorizzazione.cacheAutorizzazione.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							GestoreAutorizzazione.logger.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaApplicativa) in cache.");
							EsitoAutorizzazionePortaApplicativa esito = (EsitoAutorizzazionePortaApplicativa) response.getObject();
							esito.setEsitoPresenteInCache(true);
							return esito;
						}else if(response.getException()!=null){
							GestoreAutorizzazione.logger.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaApplicativa) in cache.");
							throw (Exception) response.getException();
						}else{
							GestoreAutorizzazione.logger.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
	
					// Effettuo la query
					GestoreAutorizzazione.logger.debug("oggetto con chiave ["+keyCache+"] (method:verificaAutorizzazionePortaApplicativa) ricerco nella configurazione...");
					EsitoAutorizzazionePortaApplicativa esito = auth.process(datiInvocazione, msg);
	
					// Aggiungo la risposta in cache (se esiste una cache)	
					// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
					// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
					// - impostare il noCache a true
					if(esito!=null){
						if(!esito.isNoCache()){
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
				}finally {
					lockAutorizzazioneContenutiPA.release(lock, "verificaAutorizzazioneContenutoPortaApplicativa", idTransazione);
				}
	    	}
    	}finally {
    		if(msg!=null) {
    			auth.cleanPostAuth(msg);
    		}
    	}
    }
    
    private static void checkDatiPortaDelegataAuthContenuto(DatiInvocazionePortaDelegata datiInvocazione, OpenSPCoop2Message msg) throws AutorizzazioneException{
    	
    	checkDatiPortaDelegata(datiInvocazione);
		
    }
    
    private static void checkDatiPortaApplicativaAuthContenuto(DatiInvocazionePortaApplicativa datiInvocazione, OpenSPCoop2Message msg) throws AutorizzazioneException{
    	
    	checkDatiPortaApplicativa(datiInvocazione);
    	
    }
    
    private static String buildCacheKeyContenuto(boolean portaDelegata, String tipoAutorizzazione, String keyCache, String suffixKeyCache) throws AutorizzazioneException{
    	StringBuilder bf = new StringBuilder();
    	
    	if(portaDelegata)
    		bf.append("PD ");
    	else
    		bf.append("PA ");
    	
    	bf.append(" AuthContenuti:").append(tipoAutorizzazione).append(" ");
    	
    	bf.append(keyCache);
    	
    	if(suffixKeyCache!=null && !"".equals(suffixKeyCache)){
    		bf.append(" ");
    		bf.append(suffixKeyCache);
    	}
    	else {
    		throw new AutorizzazioneException("Per salvare in cache l'esito dell'autorizzazione sui contenuti è richiesta la definizione di un suffisso per la chiave");
    	}
    	
    	return bf.toString();
    }
    
    private static IAutorizzazioneContenutoPortaDelegata newInstanceAuthContenutoPortaDelegata(String tipoAutorizzazione,PdDContext pddContext, IProtocolFactory<?> protocolFactory) throws AutorizzazioneException{
    	IAutorizzazioneContenutoPortaDelegata auth = null;
		try{
			PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
			auth = (IAutorizzazioneContenutoPortaDelegata) pluginLoader.newAutorizzazioneContenutiPortaDelegata(tipoAutorizzazione);
		}catch(Exception e){
			throw new AutorizzazioneException(e.getMessage(),e); // descrizione errore già corretta
		}
		String classType = null; 
    	try{
			classType = auth.getClass().getName();
			AbstractCore.init(auth, pddContext, protocolFactory);
			return auth;
		}catch(Exception e){
			throw new AutorizzazioneException("Riscontrato errore durante l'inizializzazione della classe ["+classType+
					"] che definisce l'autorizzazione dei contenuti della fruizione: "+e.getMessage(),e);
		}
    }
    
    private static IAutorizzazioneContenutoPortaApplicativa newInstanceAuthContenutoPortaApplicativa(String tipoAutorizzazione,PdDContext pddContext, IProtocolFactory<?> protocolFactory) throws AutorizzazioneException{
    	IAutorizzazioneContenutoPortaApplicativa auth = null;
		try{
			PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
			auth = (IAutorizzazioneContenutoPortaApplicativa) pluginLoader.newAutorizzazioneContenutiPortaApplicativa(tipoAutorizzazione);
		}catch(Exception e){
			throw new AutorizzazioneException(e.getMessage(),e); // descrizione errore già corretta
		}
		String classType = null; 
    	try{
			classType = auth.getClass().getName();
			AbstractCore.init(auth, pddContext, protocolFactory);
			return auth;
		}catch(Exception e){
			throw new AutorizzazioneException("Riscontrato errore durante l'inizializzazione della classe ["+classType+
					"] che definisce l'autorizzazione dei contenuti della erogazione: "+e.getMessage(),e);
		}
    }
}
