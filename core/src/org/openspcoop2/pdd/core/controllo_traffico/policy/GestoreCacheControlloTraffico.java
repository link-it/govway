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
package org.openspcoop2.pdd.core.controllo_traffico.policy;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.RisultatoStatistico;
import org.openspcoop2.core.controllo_traffico.beans.RisultatoStato;
import org.openspcoop2.core.controllo_traffico.constants.TipoBanda;
import org.openspcoop2.core.controllo_traffico.constants.TipoFinestra;
import org.openspcoop2.core.controllo_traffico.constants.TipoLatenza;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoStatistico;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.core.controllo_traffico.utils.PolicyUtilities;
import org.openspcoop2.pdd.core.controllo_traffico.ConfigurazioneControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.INotify;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.date.DateUtils;
import org.slf4j.Logger;

/**     
 * GestoreCacheControlloTraffico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreCacheControlloTraffico {

	/** Chiave della cache per l'autenticazione Buste  */
	private static final String CONTROLLO_TRAFFICO_CACHE_NAME = "controlloTraffico";
	/** Cache */
	private static Cache cache = null;
	private static final Boolean semaphoreNumeroRichieste = true;
	private static final Boolean semaphoreOccupazioneBanda = true;
	private static final Boolean semaphoreLatenza = true;
	private static final Boolean semaphoreStato = true;

	/* --------------- Cache --------------------*/
	public static boolean isCacheAbilitata() throws Exception{
		return cache!=null;
	}
	public static void resetCache() throws Exception{
		try{
			if(cache!=null){
				cache.clear();
			}
		}catch(Exception e){
			throw new Exception("Reset della cache per i dati sul controllo del traffico non riuscita: "+e.getMessage(),e);
		}
	}
	public static String printStatsCache(String separator) throws Exception{
		try{
			if(cache!=null){
				try{
					return cache.printStats(separator);
				}catch(Exception e){
					throw new Exception(e.getMessage(),e);
				}
			}else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new Exception("Visualizzazione Statistiche riguardante la cache per i dati sul controllo del traffico non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws Exception{
		try{
			if(cache!=null)
				throw new Exception("Cache gia' abilitata");
			else{
				cache = new Cache(CONTROLLO_TRAFFICO_CACHE_NAME);
			}
		}catch(Exception e){
			throw new Exception("Abilitazione cache per i dati sul controllo del traffico non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond, Logger log) throws Exception{
		try{
			if(cache!=null)
				throw new Exception("Cache gia' abilitata");
			else{
				int dimensione = -1;
				if(dimensioneCache!=null){
					dimensione = dimensioneCache.intValue();
				}
				initCache(dimensione, algoritmoCacheLRU, itemIdleTime, itemLifeSecond, log);
			}
		}catch(Exception e){
			throw new Exception("Abilitazione cache per i dati sul controllo del traffico non riuscita: "+e.getMessage(),e);
		}
	}
	public static void disabilitaCache() throws Exception{
		try{
			if(cache==null)
				throw new Exception("Cache gia' disabilitata");
			else{
				cache.clear();
				cache = null;
			}
		}catch(Exception e){
			throw new Exception("Disabilitazione cache per i dati sul controllo del traffico non riuscita: "+e.getMessage(),e);
		}
	}	
	public static String listKeysCache(String separator) throws Exception{
		try{
			if(cache!=null){
				try{
					return cache.printKeys(separator);
				}catch(Exception e){
					throw new Exception(e.getMessage(),e);
				}
			}else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new Exception("Visualizzazione chiavi presenti nella cache per i dati sul controllo del traffico non riuscita: "+e.getMessage(),e);
		}
	}
	
	public static String getObjectCache(String key) throws Exception{
		try{
			if(cache!=null){
				try{
					Object o = cache.get(key);
					if(o!=null){
						return o.toString();
					}else{
						return "oggetto con chiave ["+key+"] non presente";
					}
				}catch(Exception e){
					throw new Exception(e.getMessage(),e);
				}
			}else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new Exception("Visualizzazione oggetto presente nella cache per i dati sul controllo del traffico non riuscita: "+e.getMessage(),e);
		}
	}
	
	public static void removeObjectCache(String key) throws Exception{
		try{
			if(cache!=null){
				try{
					cache.remove(key);
				}catch(Exception e){
					throw new Exception(e.getMessage(),e);
				}
			}else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new Exception("Rimozione oggetto presente nella cache per i dati sul controllo del traffico non riuscita: "+e.getMessage(),e);
		}
	}
	
	

	
	
	private static void initCache(Integer dimensioneCache,boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond,Logger alog) throws Exception{
		
		cache = new Cache(CONTROLLO_TRAFFICO_CACHE_NAME);
	
		// dimensione
		if(dimensioneCache!=null && dimensioneCache>0){
			try{
				String msg = "Dimensione della cache (ControlloTraffico) impostata al valore: "+dimensioneCache;
				alog.info(msg);
				cache.setCacheSize(dimensioneCache);
			}catch(Exception error){
				String msg = "Parametro errato per la dimensione della cache (ControlloTraffico): "+error.getMessage();
				alog.error(msg);
				throw new Exception(msg,error);
			}
		}
		
		// algoritno
		String msg = "Algoritmo di cache (ControlloTraffico) impostato al valore: LRU";
		if(!algoritmoCacheLRU){
			msg = "Algoritmo di cache (ControlloTraffico) impostato al valore: MRU";
		}
		alog.info(msg);
		if(!algoritmoCacheLRU)
			cache.setCacheAlgoritm(CacheAlgorithm.MRU);
		else
			cache.setCacheAlgoritm(CacheAlgorithm.LRU);
		
		
		// idle time
		if(itemIdleTime!=null && itemIdleTime>0){
			try{
				msg = "Attributo 'IdleTime' (ControlloTraffico) impostato al valore: "+itemIdleTime;
				alog.info(msg);
				cache.setItemIdleTime(itemIdleTime);
			}catch(Exception error){
				msg = "Parametro errato per l'attributo 'IdleTime' (ControlloTraffico): "+error.getMessage();
				alog.error(msg);
				throw new Exception(msg,error);
			}
		}
		
		// LifeSecond
		long longItemLife = -1; 
		if(itemLifeSecond!=null && itemLifeSecond>0){
			longItemLife = itemLifeSecond.longValue();
		}
		try{
			msg = "Attributo 'MaxLifeSecond' (ControlloTraffico) impostato al valore: "+longItemLife;
			alog.info(msg);
			cache.setItemLifeTime(longItemLife);
		}catch(Exception error){
			msg = "Parametro errato per l'attributo 'MaxLifeSecond' (ControlloTraffico): "+error.getMessage();
			alog.error(msg);
			throw new Exception(msg,error);
		}
		
	}
	
	public static void disableSyncronizedGet() throws UtilsException {
		if(cache==null) {
			throw new UtilsException("Cache disabled");
		}
		cache.disableSyncronizedGet();
	}
	public static boolean isDisableSyncronizedGet() throws UtilsException {
		if(cache==null) {
			throw new UtilsException("Cache disabled");
		}
		return cache.isDisableSyncronizedGet();
	}
	
	
	
	private static GestoreCacheControlloTraffico staticInstance = null;
	public static synchronized void initialize(ConfigurazioneControlloTraffico configurazioneControlloTraffico) throws Exception{
		if(staticInstance==null){
			staticInstance = new GestoreCacheControlloTraffico(configurazioneControlloTraffico);
		}
	}
	public static GestoreCacheControlloTraffico getInstance() throws Exception{
		if(staticInstance==null){
			throw new Exception("GestoreCacheControlloTraffico non inizializzato");
		}
		return staticInstance;
	}
	
	private DatiStatisticiDAOManager datiStatisticiReader = null;
	private INotify datiNotifierReader = null;
	private Logger log;
	private boolean debug;
	private ConfigurazioneControlloTraffico configurazioneControlloTraffico;
	
	public GestoreCacheControlloTraffico(ConfigurazioneControlloTraffico configurazioneControlloTraffico) throws Exception{
		this.datiStatisticiReader = DatiStatisticiDAOManager.getInstance();
		if(configurazioneControlloTraffico.isNotifierEnabled()){
			this.datiNotifierReader = configurazioneControlloTraffico.getNotifier();
		}
		this.configurazioneControlloTraffico = configurazioneControlloTraffico;
		this.debug = this.configurazioneControlloTraffico.isDebug();
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(this.debug);
	}
	
	
	
	
	
	
	
	
	/* ********************** NUMERO RICHIESTE ************************** */
	
	public RisultatoStatistico readNumeroRichieste(TipoRisorsa tipoRisorsa, 
			Date leftInterval, Date rightInterval,
			TipoFinestra tipoFinestra, TipoPeriodoStatistico tipoPeriodo,
			DatiTransazione datiTransazione,IDUnivocoGroupByPolicy groupByPolicy, AttivazionePolicyFiltro filtro,
    		IState state) throws Exception{
						
		// BuildKey
		SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
		
		TipoPdD tipoPdDTransazioneInCorso = datiTransazione.getTipoPdD();
		
		StringBuilder bfKey = new StringBuilder(tipoRisorsa.getValue()+" ");
		bfKey.append(tipoFinestra.getValue());
		bfKey.append(" ").append(tipoPeriodo.getValue());
		bfKey.append(" ").append(tipoPdDTransazioneInCorso.getTipo());
		bfKey.append(" [").append(dateformat.format(leftInterval)).append("]");
		bfKey.append("-[").append(dateformat.format(rightInterval)).append("]");
		bfKey.append(" GroupBy ");
		bfKey.append(groupByPolicy.toString());
		if(filtro!=null && filtro.isEnabled()) {
			bfKey.append(" Filtro ");
			bfKey.append(PolicyUtilities.toStringFilter(filtro));
		}
		String key = bfKey.toString();
		
		//System.out.println("CERCO OGGETTO CON CHIAVE ["+key+"]");
		
		RisultatoStatistico risultato = null;
		if(GestoreCacheControlloTraffico.cache!=null){
			risultato = this.readNumeroRichiesteInCache(key, tipoRisorsa, tipoFinestra, tipoPeriodo, leftInterval, rightInterval, 
					datiTransazione, groupByPolicy, filtro,
					state);
		}
		else{
			risultato = this.datiStatisticiReader.readNumeroRichieste(key, tipoRisorsa, tipoFinestra, tipoPeriodo, leftInterval, rightInterval, 
					datiTransazione, groupByPolicy, filtro,
					state);
		}
		return risultato;
	}
	
	private RisultatoStatistico readNumeroRichiesteInCache(String keyCache,
			TipoRisorsa tipoRisorsa,TipoFinestra tipoFinestra,TipoPeriodoStatistico tipoPeriodo, 
			Date leftInterval, Date rightInterval,
			DatiTransazione datiTransazione,IDUnivocoGroupByPolicy groupByPolicy, AttivazionePolicyFiltro filtro,
    		IState state) throws Exception{

		RisultatoStatistico obj = null;
		try{

			if(keyCache == null)
				throw new Exception("KeyCache non definita");

			// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
    		if(cache!=null){
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] valore["+response.getObject()+"] in cache.");
						//System.out.println("RITORNO OGGETTO IN CACHE CON CHIAVE ["+keyCache+"]: ["+response.getObject()+"]");
						return (RisultatoStatistico) response.getObject();
					}else if(response.getException()!=null){
						this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] in cache.");
						throw (Exception) response.getException();
					}else{
						this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}
			
			synchronized (semaphoreNumeroRichieste) {
				
				// se e' attiva una cache provo ad utilizzarla
				if(cache!=null){
					org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) cache.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] valore["+response.getObject()+"] in cache.");
							//System.out.println("RITORNO OGGETTO IN CACHE CON CHIAVE ["+keyCache+"]: ["+response.getObject()+"]");
							return (RisultatoStatistico) response.getObject();
						}else if(response.getException()!=null){
							this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] in cache.");
							throw (Exception) response.getException();
						}else{
							this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
				}
	
				// Effettuo le query nella mia gerarchia di registri.
				this.log.debug("oggetto con chiave ["+keyCache+"] non in cache, effettuo ricerca...");
				try{
					obj = this.datiStatisticiReader.readNumeroRichieste(keyCache, tipoRisorsa, tipoFinestra, tipoPeriodo, leftInterval, rightInterval, 
							datiTransazione, groupByPolicy, filtro,
							state);
				}catch(Exception e){
					throw e;
				}
	
				// Aggiungo la risposta in cache (se esiste una cache)	
				// Se ho una eccezione aggiungo in cache solo una not found
				if( cache!=null ){ 	
					if(obj!=null){
						this.log.debug("Aggiungo oggetto con chiave ["+keyCache+"] valore["+obj+"] in cache");
					}else{
						throw new Exception("Ricerca ha ritornato un valore null");
					}
					try{	
						org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
						responseCache.setObject((java.io.Serializable)obj);
						cache.put(keyCache,responseCache);
					}catch(UtilsException e){
						this.log.error("Errore durante l'inserimento in cache con chiave ["+keyCache+"] valore["+obj+"]: "+e.getMessage());
					}
				}
			}

		}
		catch(Exception e){
			this.log.error(e.getMessage(),e);
			throw new Exception("NumeroRichieste, Algoritmo di Cache fallito: "+e.getMessage(),e);
		}

		return obj;
	}
	
	
	
	
	
	
	/* ********************** OCCUPAZIONE BANDA ************************** */
	
	public RisultatoStatistico readOccupazioneBanda(TipoRisorsa tipoRisorsa, 
			Date leftInterval, Date rightInterval,
			TipoFinestra tipoFinestra, TipoPeriodoStatistico tipoPeriodo,
			TipoBanda tipoBanda,
			DatiTransazione datiTransazione,IDUnivocoGroupByPolicy groupByPolicy, AttivazionePolicyFiltro filtro,
    		IState state) throws Exception{
		
		// BuildKey
		SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
		
		TipoPdD tipoPdDTransazioneInCorso = datiTransazione.getTipoPdD();
		
		StringBuilder bfKey = new StringBuilder(tipoRisorsa.getValue()+" ");
		bfKey.append(tipoFinestra.getValue());
		bfKey.append(" ").append(tipoPeriodo.getValue());
		bfKey.append(" ").append(tipoBanda.getValue());
		bfKey.append(" ").append(tipoPdDTransazioneInCorso.getTipo());
		bfKey.append(" [").append(dateformat.format(leftInterval)).append("]");
		bfKey.append("-[").append(dateformat.format(rightInterval)).append("]");
		bfKey.append(" GroupBy ");
		bfKey.append(groupByPolicy.toString());
		if(filtro!=null && filtro.isEnabled()) {
			bfKey.append(" Filtro ");
			bfKey.append(PolicyUtilities.toStringFilter(filtro));
		}
		String key = bfKey.toString();
		
		//System.out.println("CERCO OGGETTO CON CHIAVE ["+key+"]");
		
		RisultatoStatistico risultato = null;
		if(GestoreCacheControlloTraffico.cache!=null){
			risultato = this.readOccupazioneBandaInCache(key, tipoRisorsa, tipoFinestra, tipoPeriodo, leftInterval, rightInterval, tipoBanda,
					datiTransazione, groupByPolicy, filtro,
					state);
		}
		else{
			risultato = this.datiStatisticiReader.readOccupazioneBanda(key, tipoRisorsa, tipoFinestra, tipoPeriodo, leftInterval, rightInterval, tipoBanda,
					datiTransazione, groupByPolicy, filtro,
					state);
		}
		return risultato;
	}
	
	private RisultatoStatistico readOccupazioneBandaInCache(String keyCache,
			TipoRisorsa tipoRisorsa, TipoFinestra tipoFinestra,TipoPeriodoStatistico tipoPeriodo, 
			Date leftInterval, Date rightInterval,
			TipoBanda tipoBanda,
			DatiTransazione datiTransazione,IDUnivocoGroupByPolicy groupByPolicy, AttivazionePolicyFiltro filtro,
    		IState state) throws Exception{

		RisultatoStatistico obj = null;
		try{

			if(keyCache == null)
				throw new Exception("KeyCache non definita");

			// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
			if(cache!=null){
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] valore["+response.getObject()+"] in cache.");
						//System.out.println("RITORNO OGGETTO IN CACHE CON CHIAVE ["+keyCache+"]: ["+response.getObject()+"]");
						return (RisultatoStatistico) response.getObject();
					}else if(response.getException()!=null){
						this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] in cache.");
						throw (Exception) response.getException();
					}else{
						this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}
			
			synchronized (semaphoreOccupazioneBanda) {
				
				// se e' attiva una cache provo ad utilizzarla
				if(cache!=null){
					org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) cache.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] valore["+response.getObject()+"] in cache.");
							//System.out.println("RITORNO OGGETTO IN CACHE CON CHIAVE ["+keyCache+"]: ["+response.getObject()+"]");
							return (RisultatoStatistico) response.getObject();
						}else if(response.getException()!=null){
							this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] in cache.");
							throw (Exception) response.getException();
						}else{
							this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
				}
	
				// Effettuo le query nella mia gerarchia di registri.
				this.log.debug("oggetto con chiave ["+keyCache+"] non in cache, effettuo ricerca...");
				try{
					obj = this.datiStatisticiReader.readOccupazioneBanda(keyCache, tipoRisorsa, tipoFinestra, tipoPeriodo, leftInterval, rightInterval, tipoBanda,
							datiTransazione, groupByPolicy, filtro,
							state);
				}catch(Exception e){
					throw e;
				}
	
				// Aggiungo la risposta in cache (se esiste una cache)	
				// Se ho una eccezione aggiungo in cache solo una not found
				if( cache!=null ){ 	
					if(obj!=null){
						this.log.debug("Aggiungo oggetto con chiave ["+keyCache+"] valore["+obj+"] in cache");
					}else{
						throw new Exception("Ricerca ha ritornato un valore null");
					}
					try{	
						org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
						responseCache.setObject((java.io.Serializable)obj);
						cache.put(keyCache,responseCache);
					}catch(UtilsException e){
						this.log.error("Errore durante l'inserimento in cache con chiave ["+keyCache+"] valore["+obj+"]: "+e.getMessage());
					}
				}
				
			}

		}
		catch(Exception e){
			this.log.error(e.getMessage(),e);
			throw new Exception("OccupazioneBanda, Algoritmo di Cache fallito: "+e.getMessage(),e);
		}

		return obj;
	}
	
	
	
	
	
	
	/* ********************** LATENZA ************************** */
	
	public RisultatoStatistico readLatenza(TipoRisorsa tipoRisorsa,
			Date leftInterval, Date rightInterval,
			TipoFinestra tipoFinestra, TipoPeriodoStatistico tipoPeriodo,
			TipoLatenza tipoLatenza,
			DatiTransazione datiTransazione,IDUnivocoGroupByPolicy groupByPolicy, AttivazionePolicyFiltro filtro,
    		IState state) throws Exception{
		
		// BuildKey
		SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
		
		TipoPdD tipoPdDTransazioneInCorso = datiTransazione.getTipoPdD();
		
		StringBuilder bfKey = null;
		bfKey = new StringBuilder(tipoRisorsa.getValue()+" ");
		bfKey.append(tipoFinestra.getValue());
		bfKey.append(" ").append(tipoPeriodo.getValue());
		bfKey.append(" ").append(tipoLatenza.getValue());
		bfKey.append(" ").append(tipoPdDTransazioneInCorso.getTipo());
		bfKey.append(" [").append(dateformat.format(leftInterval)).append("]");
		bfKey.append("-[").append(dateformat.format(rightInterval)).append("]");
		bfKey.append(" GroupBy ");
		bfKey.append(groupByPolicy.toString());
		if(filtro!=null && filtro.isEnabled()) {
			bfKey.append(" Filtro ");
			bfKey.append(PolicyUtilities.toStringFilter(filtro));
		}
		String key = bfKey.toString();
		
		//System.out.println("CERCO OGGETTO CON CHIAVE ["+key+"]");
		
		RisultatoStatistico risultato = null;
		if(GestoreCacheControlloTraffico.cache!=null){
			risultato = this.readLatenzaInCache(key, tipoRisorsa, tipoFinestra, tipoPeriodo, leftInterval, rightInterval, 
					tipoLatenza, datiTransazione, groupByPolicy, filtro,
					state);
		}
		else{
			risultato = this.datiStatisticiReader.readLatenza(key, tipoRisorsa, tipoFinestra, tipoPeriodo, leftInterval, rightInterval, 
					tipoLatenza, datiTransazione, groupByPolicy, filtro,
					state);
		}
		return risultato;
	}
	
	private RisultatoStatistico readLatenzaInCache(String keyCache,
			TipoRisorsa tipoRisorsa,TipoFinestra tipoFinestra,TipoPeriodoStatistico tipoPeriodo, 
			Date leftInterval, Date rightInterval,
			TipoLatenza tipoLatenza,
			DatiTransazione datiTransazione,IDUnivocoGroupByPolicy groupByPolicy, AttivazionePolicyFiltro filtro,
    		IState state) throws Exception{

		RisultatoStatistico obj = null;
		try{

			if(keyCache == null)
				throw new Exception("KeyCache non definita");

			// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
			if(cache!=null){
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] valore["+response.getObject()+"] in cache.");
						//System.out.println("RITORNO OGGETTO IN CACHE CON CHIAVE ["+keyCache+"]: ["+response.getObject()+"]");
						return (RisultatoStatistico) response.getObject();
					}else if(response.getException()!=null){
						this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] in cache.");
						throw (Exception) response.getException();
					}else{
						this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}
			
			synchronized (semaphoreLatenza) {
				
				// se e' attiva una cache provo ad utilizzarla
				if(cache!=null){
					org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) cache.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] valore["+response.getObject()+"] in cache.");
							//System.out.println("RITORNO OGGETTO IN CACHE CON CHIAVE ["+keyCache+"]: ["+response.getObject()+"]");
							return (RisultatoStatistico) response.getObject();
						}else if(response.getException()!=null){
							this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] in cache.");
							throw (Exception) response.getException();
						}else{
							this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
				}
	
				// Effettuo le query nella mia gerarchia di registri.
				this.log.debug("oggetto con chiave ["+keyCache+"] non in cache, effettuo ricerca...");
				try{
					obj = this.datiStatisticiReader.readLatenza(keyCache, tipoRisorsa, tipoFinestra, tipoPeriodo, leftInterval, rightInterval, 
							tipoLatenza, datiTransazione, groupByPolicy, filtro,
							state);
				}catch(Exception e){
					throw e;
				}
	
				// Aggiungo la risposta in cache (se esiste una cache)	
				// Se ho una eccezione aggiungo in cache solo una not found
				if( cache!=null ){ 	
					if(obj!=null){
						this.log.debug("Aggiungo oggetto con chiave ["+keyCache+"] valore["+obj+"] in cache");
					}else{
						throw new Exception("Ricerca ha ritornato un valore null");
					}
					try{	
						org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
						responseCache.setObject((java.io.Serializable)obj);
						cache.put(keyCache,responseCache);
					}catch(UtilsException e){
						this.log.error("Errore durante l'inserimento in cache con chiave ["+keyCache+"] valore["+obj+"]: "+e.getMessage());
					}
				}
				
			}

		}
		catch(Exception e){
			this.log.error(e.getMessage(),e);
			throw new Exception("Latenza, Algoritmo di Cache fallito: "+e.getMessage(),e);
		}

		return obj;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ********************** STATO ************************** */
	
	public RisultatoStato getStato(DatiTransazione datiTransazione, String idStato) throws Exception{
		
		// BuildKey
		StringBuilder bfKey = new StringBuilder("Stato ");
		bfKey.append(idStato);
		String key = bfKey.toString();
		
		//System.out.println("CERCO OGGETTO CON CHIAVE ["+key+"]");
		
		if(GestoreCacheControlloTraffico.cache!=null){
			return this.getStatoInCache(key, datiTransazione, idStato);
		}
		else{
			return this.datiNotifierReader.getStato(this.log, datiTransazione, idStato);
		}
	}
	
	private RisultatoStato getStatoInCache(String keyCache,DatiTransazione datiTransazione, 
			String idStato) throws Exception{

		RisultatoStato obj = null;
		try{

			if(keyCache == null)
				throw new Exception("KeyCache non definita");

			// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
			if(cache!=null){
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] valore["+response.getObject()+"] in cache.");
						//System.out.println("RITORNO OGGETTO IN CACHE CON CHIAVE ["+keyCache+"]: ["+response.getObject()+"]");
						return (RisultatoStato) response.getObject();
					}else if(response.getException()!=null){
						this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] in cache.");
						throw (Exception) response.getException();
					}else{
						this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}
			
			synchronized (semaphoreStato) {
				
				// se e' attiva una cache provo ad utilizzarla
				if(cache!=null){
					org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) cache.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] valore["+response.getObject()+"] in cache.");
							//System.out.println("RITORNO OGGETTO IN CACHE CON CHIAVE ["+keyCache+"]: ["+response.getObject()+"]");
							return (RisultatoStato) response.getObject();
						}else if(response.getException()!=null){
							this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] in cache.");
							throw (Exception) response.getException();
						}else{
							this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
						}
					}
				}
	
				// Effettuo le query nella mia gerarchia di registri.
				this.log.debug("oggetto con chiave ["+keyCache+"] non in cache, effettuo ricerca...");
				try{
					obj = this.datiNotifierReader.getStato(this.log, datiTransazione, idStato);
				}catch(Exception e){
					throw e;
				}
	
				// Aggiungo la risposta in cache (se esiste una cache)	
				// Se ho una eccezione aggiungo in cache solo una not found
				if( cache!=null ){ 	
					if(obj!=null){
						this.log.debug("Aggiungo oggetto con chiave ["+keyCache+"] valore["+obj+"] in cache");
					}else{
						throw new Exception("Ricerca ha ritornato un valore null");
					}
					try{	
						org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
						responseCache.setObject((java.io.Serializable)obj);
						cache.put(keyCache,responseCache);
					}catch(UtilsException e){
						this.log.error("Errore durante l'inserimento in cache con chiave ["+keyCache+"] valore["+obj+"]: "+e.getMessage());
					}
				}
				
			}

		}
		catch(Exception e){
			this.log.error(e.getMessage(),e);
			throw new Exception("Stato, Algoritmo di Cache fallito: "+e.getMessage(),e);
		}

		return obj;
	}
}
