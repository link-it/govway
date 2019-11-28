/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.pdd.core.behaviour.built_in.load_balance;

import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.behaviour.BehaviourEmitDiagnosticException;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.pdd.core.behaviour.conditional.ConditionalFilterResult;
import org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.slf4j.Logger;

/**     
 * GestoreCacheKeystore
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreLoadBalancerCaching {

	/** Chiave della cache */
	private static final String LOAD_BALANCER_CACHE_NAME = "consegnaApplicativi";
	/** Cache */
	private static Cache cache = null;
	

	/* --------------- Cache --------------------*/
	public static boolean isCacheAbilitata() {
		return cache!=null;
	}
	public static void resetCache() throws Exception{
		try{
			if(cache!=null){
				cache.clear();
			}
		}catch(Exception e){
			throw new Exception("Reset della cache per i dati contenenti i dati di bilanciamento del carico non riuscita: "+e.getMessage(),e);
		}
	}
	public static String printStatsCache(String separator) throws Exception{
		try{
			if(cache!=null){
				try{
					String stats = cache.printStats(separator);
					String lifeTimeLabel = "LifeTime:";
					if(stats.contains(lifeTimeLabel)) {
						
						StringBuilder bf = new StringBuilder();
						bf.append("CRLsLifeTime:");
						long lifeTime = GestoreLoadBalancerCaching.getItemCrlLifeSecond();
						if(lifeTime>0){
							bf.append(Utilities.convertSystemTimeIntoString_millisecondi(lifeTime*1000,false));
						}
						else if(lifeTime==0){
							bf.append("0");
						}
						else if(lifeTime<0){
							bf.append("Infinito");
						}
						bf.append(separator);
					
						return stats.replace(lifeTimeLabel, bf.toString() + lifeTimeLabel);
					}
					else {
						return stats;
					}
				}catch(Exception e){
					throw new Exception(e.getMessage(),e);
				}
			}else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new Exception("Visualizzazione Statistiche riguardante la cache per i dati contenenti i dati di bilanciamento del carico non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws Exception{
		try{
			if(cache!=null)
				throw new Exception("Cache gia' abilitata");
			else{
				cache = new Cache(LOAD_BALANCER_CACHE_NAME);
			}
		}catch(Exception e){
			throw new Exception("Abilitazione cache per i dati contenenti i dati di bilanciamento del carico non riuscita: "+e.getMessage(),e);
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
			throw new Exception("Abilitazione cache per i dati contenenti i dati di bilanciamento del carico non riuscita: "+e.getMessage(),e);
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
			throw new Exception("Disabilitazione cache per i dati contenenti i dati di bilanciamento del carico non riuscita: "+e.getMessage(),e);
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
			throw new Exception("Visualizzazione chiavi presenti nella cache per i dati contenenti i dati di bilanciamento del carico non riuscita: "+e.getMessage(),e);
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
			throw new Exception("Visualizzazione oggetto presente nella cache per i dati contenenti i dati di bilanciamento del carico non riuscita: "+e.getMessage(),e);
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
			throw new Exception("Rimozione oggetto presente nella cache per i dati contenenti i dati di bilanciamento del carico non riuscita: "+e.getMessage(),e);
		}
	}
	

	/*----------------- INIZIALIZZAZIONE --------------------*/

	public static void initialize(Logger log) throws Exception{
		GestoreLoadBalancerCaching.initialize(false, -1,null,-1l,-1l, log);
	}
	public static void initialize(int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{
		GestoreLoadBalancerCaching.initialize(true, dimensioneCache,algoritmoCache,idleTime,itemLifeSecond, log);
	}

	private static void initialize(boolean cacheAbilitata,int dimensioneCache,String algoritmoCache,
			long idleTime, long itemLifeSecond, Logger log) throws Exception{

		// Inizializzazione Cache
		if(cacheAbilitata){
			GestoreLoadBalancerCaching.initCache(dimensioneCache, algoritmoCache, idleTime, itemLifeSecond, log);
		}

	}
	private static void initCache(Integer dimensioneCache,String algoritmoCache,Long itemIdleTime,Long itemLifeSecond,Logger alog) throws Exception{
		initCache(dimensioneCache, CostantiConfigurazione.CACHE_LRU.toString().equalsIgnoreCase(algoritmoCache), itemIdleTime, itemLifeSecond, alog);
	}
	
	private static void initCache(Integer dimensioneCache,boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond,Logger alog) throws Exception{
		
		cache = new Cache(LOAD_BALANCER_CACHE_NAME);
	
		// dimensione
		if(dimensioneCache!=null && dimensioneCache>0){
			try{
				String msg = "Dimensione della cache (ResponseCaching) impostata al valore: "+dimensioneCache;
				alog.info(msg);
				cache.setCacheSize(dimensioneCache);
			}catch(Exception error){
				String msg = "Parametro errato per la dimensione della cache (ResponseCaching): "+error.getMessage();
				alog.error(msg);
				throw new Exception(msg,error);
			}
		}
		
		// algoritno
		String msg = "Algoritmo di cache (ResponseCaching) impostato al valore: LRU";
		if(!algoritmoCacheLRU){
			msg = "Algoritmo di cache (ResponseCaching) impostato al valore: MRU";
		}
		alog.info(msg);
		if(!algoritmoCacheLRU)
			cache.setCacheAlgoritm(CacheAlgorithm.MRU);
		else
			cache.setCacheAlgoritm(CacheAlgorithm.LRU);
		
		
		// idle time
		if(itemIdleTime!=null && itemIdleTime>0){
			try{
				msg = "Attributo 'IdleTime' (ResponseCaching) impostato al valore: "+itemIdleTime;
				alog.info(msg);
				cache.setItemIdleTime(itemIdleTime);
			}catch(Exception error){
				msg = "Parametro errato per l'attributo 'IdleTime' (ResponseCaching): "+error.getMessage();
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
			msg = "Attributo 'MaxLifeSecond' (ResponseCaching) impostato al valore: "+longItemLife;
			alog.info(msg);
			cache.setItemLifeTime(longItemLife);
		}catch(Exception error){
			msg = "Parametro errato per l'attributo 'MaxLifeSecond' (ResponseCaching): "+error.getMessage();
			alog.error(msg);
			throw new Exception(msg,error);
		}
		
		
		// impostazione di JCS nel gestore delle cache dei keystore 
		
		org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.setKeystoreCacheJCS(true, longItemLife>0 ? (int)longItemLife : 7200, cache);
	}
	
	private static long itemCrlLifeSecond; 
	public static void setCacheCrlLifeSeconds(long itemCrlLifeSecondParam,Logger alog) throws Exception{
		itemCrlLifeSecond = itemCrlLifeSecondParam;
		org.openspcoop2.security.keystore.cache.GestoreKeystoreCache.setKeystoreCacheJCS_crlLifeSeconds(itemCrlLifeSecond>0 ? (int)itemCrlLifeSecond : 7200);
	}
	public static long getItemCrlLifeSecond() {
		return itemCrlLifeSecond;
	}
	
	
	private static GestoreLoadBalancerCaching staticInstance = null;
	public static synchronized void initialize() throws Exception{
		if(staticInstance==null){
			staticInstance = new GestoreLoadBalancerCaching();
		}
	}
	public static GestoreLoadBalancerCaching getInstance() throws Exception{
		if(staticInstance==null){
			throw new Exception("GestoreKeystore non inizializzato");
		}
		return staticInstance;
	}
	
	@SuppressWarnings("unused")
	private Logger log;
	
	public GestoreLoadBalancerCaching() throws Exception{
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
	}
	
	
	
	
	
	
	
	
	
	/* ********************** ENGINE ************************** */
	
	public static LoadBalancerPool getLoadBalancerPool(PortaApplicativa pa, OpenSPCoop2Message message, Busta busta, 
			RequestInfo requestInfo, PdDContext pddContext, 
			MsgDiagnostico msgDiag, Logger log) throws BehaviourException, BehaviourEmitDiagnosticException{
    	
    	if(GestoreLoadBalancerCaching.cache==null){
    		throw new BehaviourException("La funzionalità di Load Balancer richiede che sia abilitata la cache dedicata alla funzionalità");
		}
    	else{
    		
    		ConditionalFilterResult filterResult = 
    				ConditionalUtils.filter(pa, message, busta, requestInfo, pddContext, msgDiag, log, true);
    		
    		String keyCache = "["+pa.getBehaviour().getNome()+"] "+pa.getTipoSoggettoProprietario()+"/"+pa.getNomeSoggettoProprietario()+" "+pa.getNome();
    		if(filterResult!=null) {
    			if(filterResult.isByFilter()) {
    				keyCache = keyCache +" [Conditional-By-Filter] ";
        		}
    			else {
    				keyCache = keyCache +" [Conditional-By-Name] ";
    			}
    			if(filterResult.getRegola()!=null) {
    				keyCache = keyCache +"[Regola "+filterResult.getRegola()+"] ";
    			}
    			if(filterResult.getCondition()!=null) {
    				keyCache = keyCache +" "+filterResult.getCondition();
    			}
    		}

			synchronized (GestoreLoadBalancerCaching.cache) {

				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreLoadBalancerCaching.cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:getLoadBalancerPool) in cache.");
						return (LoadBalancerPool) response.getObject();
					}else if(response.getException()!=null){
						log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:getLoadBalancerPool) in cache.");
						throw new BehaviourException( (Exception) response.getException() );
					}else{
						log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}

				// Effettuo la query
				log.debug("oggetto con chiave ["+keyCache+"] (method:getLoadBalancerPool) ricerco nella configurazione...");
				LoadBalancerPool pool = readLoadBalancerPool(pa, filterResult);
				
				// Aggiungo la risposta in cache (se esiste una cache)	
				// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
				// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
				// - impostare il noCache a true
				if(pool!=null){
					log.info("Aggiungo oggetto ["+keyCache+"] in cache");
					try{	
						org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
						responseCache.setObject(pool);
						GestoreLoadBalancerCaching.cache.put(keyCache,responseCache);
					}catch(UtilsException e){
						log.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
					}
					return pool;
				}else{
					throw new BehaviourException("Metodo (getLoadBalancerPool) non è riuscito a costruire un pool");
				}
			}
    	}
    	
    }
	
	
	private static LoadBalancerPool readLoadBalancerPool(PortaApplicativa pa, ConditionalFilterResult filterResult) throws BehaviourException {
		
		List<PortaApplicativaServizioApplicativo> listSA = null;
		if(filterResult==null) {
			listSA = pa.getServizioApplicativoList();
		}
		else {
			listSA = filterResult.getListServiziApplicativi();
		}
		
		LoadBalancerPool pool = new LoadBalancerPool();
		if(!listSA.isEmpty()) {
			for (PortaApplicativaServizioApplicativo servizioApplicativo : listSA) {
				if(servizioApplicativo.getDatiConnettore()==null || servizioApplicativo.getDatiConnettore().getStato()==null || 
						StatoFunzionalita.ABILITATO.equals(servizioApplicativo.getDatiConnettore().getStato())) {
					int weight = -1;
					if(servizioApplicativo.getDatiConnettore()!=null && servizioApplicativo.getDatiConnettore().sizeProprietaList()>0) {
						String weightDefined = null;
						for (Proprieta p : servizioApplicativo.getDatiConnettore().getProprietaList()) {
							if(Costanti.LOAD_BALANCER_WEIGHT.equals(p.getNome())) {
								weightDefined = p.getValore();
							}
						}
						if(weightDefined!=null) {
							try {
								weight = Integer.valueOf(weightDefined);
							}catch(Exception e) {}
						}
					}
					String nomeConnettore = servizioApplicativo.getDatiConnettore().getNome();
					if(nomeConnettore==null) {
						nomeConnettore = org.openspcoop2.pdd.core.behaviour.built_in.Costanti.NOME_CONNETTORE_DEFAULT;
					}
					if(weight>0) {
						pool.addConnector(nomeConnettore, weight);
					}
					else {
						pool.addConnector(nomeConnettore);
					}
				}
			}
		}
		return pool;
	}
}
