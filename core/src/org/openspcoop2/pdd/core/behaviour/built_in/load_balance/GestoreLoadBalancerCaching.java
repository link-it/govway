/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.util.Date;
import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoBehaviour;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.behaviour.BehaviourEmitDiagnosticException;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.health_check.HealthCheckUtils;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyConnector;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyResult;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyUtils;
import org.openspcoop2.pdd.core.behaviour.conditional.ConditionalFilterResult;
import org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
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
	private static final org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("GestoreLoadBalancerCaching");
	

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
					return cache.printStats(separator);
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
		
	}
	
	@SuppressWarnings("deprecation")
	@Deprecated
	public static void disableSyncronizedGet() throws UtilsException {
		if(GestoreLoadBalancerCaching.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		GestoreLoadBalancerCaching.cache.disableSyncronizedGet();
	}
	@SuppressWarnings("deprecation")
	@Deprecated
	public static boolean isDisableSyncronizedGet() throws UtilsException {
		if(GestoreLoadBalancerCaching.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		return GestoreLoadBalancerCaching.cache.isDisableSyncronizedGet();
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
	
	public static LoadBalancerInstance getLoadBalancerInstance(PortaApplicativa pa, OpenSPCoop2Message message, Busta busta, 
			RequestInfo requestInfo, PdDContext pddContext, 
			MsgDiagnostico msgDiag, Logger log,
			LoadBalancerType loadBalancerType) throws BehaviourException, BehaviourEmitDiagnosticException{
		
		LoadBalancerInstance instance = new LoadBalancerInstance();
		
		ConditionalFilterResult filterResult = 
				ConditionalUtils.filter(pa, message, busta, requestInfo, pddContext, msgDiag, log, 
						TipoBehaviour.CONSEGNA_LOAD_BALANCE);
		
		String keyCache = getKeyCache(pa, message, busta, 
				requestInfo, pddContext, 
				msgDiag, log,
				filterResult);
		
		String keyCacheLoadBalancerPool = "[POOL] "+keyCache;
		instance.setLoadBalancerPool(getLoadBalancerPool(pa, message, busta, 
				requestInfo, pddContext, 
				msgDiag, log, 
				keyCacheLoadBalancerPool, filterResult));
		
		StickyResult stickyResult = null;
		if(LoadBalancerType.IP_HASH.equals(loadBalancerType)) {
			String clientIp = LoadBalancer.getIpSourceFromContet(pddContext);
			stickyResult = new StickyResult();
			stickyResult.setCondition(clientIp);
			stickyResult.setFound(true);
		}
		else {
			stickyResult = getStickyInfo(pa, message, busta, 
					requestInfo, pddContext, 
					msgDiag, log);
		}
		
		if(stickyResult!=null && stickyResult.isFound()) {
			String keyCacheSticky = "[STICKY '"+stickyResult.getCondition()+"'] "+keyCache;
			instance.setConnectorSelected(getNomeConnettore(pa, message, busta, 
					requestInfo, pddContext, 
					msgDiag, log, 
					keyCacheSticky, instance.getLoadBalancerPool(), loadBalancerType, stickyResult));
		}
		
		if(instance.getConnectorSelected()==null) {
			instance.setConnectorSelected(selectConnector(loadBalancerType, instance.getLoadBalancerPool(), pddContext));
		}
		
		return instance;
	}
	
	private static StickyResult getStickyInfo(PortaApplicativa pa, OpenSPCoop2Message message, Busta busta, 
			RequestInfo requestInfo, PdDContext pddContext, 
			MsgDiagnostico msgDiag, Logger log) throws BehaviourException, BehaviourEmitDiagnosticException{
		
		if(StickyUtils.isConfigurazioneSticky(pa, log)) {
			return StickyUtils.getStickyResult(pa, message, busta, requestInfo, pddContext, msgDiag, log);
		}
		
		return null;
		
	}
	
	private static String getKeyCache(PortaApplicativa pa, OpenSPCoop2Message message, Busta busta, 
			RequestInfo requestInfo, PdDContext pddContext, 
			MsgDiagnostico msgDiag, Logger log,
			ConditionalFilterResult filterResult) throws BehaviourException, BehaviourEmitDiagnosticException {
		
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
		
		return keyCache;
		
	}
	
	private static LoadBalancerPool getLoadBalancerPool(PortaApplicativa pa, OpenSPCoop2Message message, Busta busta, 
			RequestInfo requestInfo, PdDContext pddContext, 
			MsgDiagnostico msgDiag, Logger log,
			String keyCache, ConditionalFilterResult filterResult) throws BehaviourException, BehaviourEmitDiagnosticException{
    	
    	if(GestoreLoadBalancerCaching.cache==null){
    		throw new BehaviourException("La funzionalità di Load Balancer richiede che sia abilitata la cache dedicata alla funzionalità");
		}
    	else{
    		
    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
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
    		
			String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
			//synchronized (GestoreLoadBalancerCaching.cache) {
			GestoreLoadBalancerCaching.lock.acquireThrowRuntime("getLoadBalancerPool", idTransazione);
			try {
				
				response = 
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
				LoadBalancerPool lbPool = readLoadBalancerPool(log, pa, filterResult);
				
				// Aggiungo la risposta in cache (se esiste una cache)	
				// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
				// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
				// - impostare il noCache a true
				if(lbPool!=null){
					log.info("Aggiungo oggetto ["+keyCache+"] in cache");
					try{	
						org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
						responseCache.setObject(lbPool);
						GestoreLoadBalancerCaching.cache.put(keyCache,responseCache);
					}catch(UtilsException e){
						log.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
					}
					return lbPool;
				}else{
					throw new BehaviourException("Metodo (getLoadBalancerPool) non è riuscito a costruire un pool");
				}
			}finally {
				GestoreLoadBalancerCaching.lock.release("getLoadBalancerPool", idTransazione);
			}
    	}
    	
    }
	
	
	private static LoadBalancerPool readLoadBalancerPool(Logger log, PortaApplicativa pa, ConditionalFilterResult filterResult) throws BehaviourException {
		
		List<PortaApplicativaServizioApplicativo> listSA = null;
		if(filterResult==null) {
			listSA = pa.getServizioApplicativoList();
		}
		else {
			listSA = filterResult.getListServiziApplicativi();
		}
		
		LoadBalancerPool pool = new LoadBalancerPool(HealthCheckUtils.read(pa, log));
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
					String nomeConnettore = null;
					if(servizioApplicativo.getDatiConnettore()!=null) {
						nomeConnettore = servizioApplicativo.getDatiConnettore().getNome();
					}
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
	
	private static String getNomeConnettore(PortaApplicativa pa, OpenSPCoop2Message message, Busta busta, 
			RequestInfo requestInfo, PdDContext pddContext, 
			MsgDiagnostico msgDiag, Logger log,
			String keyCache, LoadBalancerPool loadBalancerPool, LoadBalancerType loadBalancerType, StickyResult stickyResult) throws BehaviourException, BehaviourEmitDiagnosticException{
    	
    	if(GestoreLoadBalancerCaching.cache==null){
    		throw new BehaviourException("La funzionalità di Load Balancer richiede che sia abilitata la cache dedicata alla funzionalità");
		}
    	else{
    		
    		// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
    		
    		org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) GestoreLoadBalancerCaching.cache.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:getLoadBalancerPool) in cache.");
					
					StickyConnector stickyConnector = (StickyConnector) response.getObject();
					if(stickyConnector.getExpirationDate()!=null) {
						Date now = DateManager.getDate();
						if(stickyConnector.getExpirationDate().after(now)){
							String c = stickyConnector.getConnector();
							if(loadBalancerPool.getConnectorNames(true).contains(c)) {
								return c;
							}
							else {
								// eseguo operazione in synchronized mode
							}
						}
						else {
							// eseguo operazione in synchronized mode
						}
					}
					else {
						String c = stickyConnector.getConnector();
						if(loadBalancerPool.getConnectorNames(true).contains(c)) {
							return c;
						}
						else {
							// eseguo operazione in synchronized mode
						}
					}
					
				}else if(response.getException()!=null){
					log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:getLoadBalancerPool) in cache.");
					throw new BehaviourException( (Exception) response.getException() );
				}else{
					log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
				}
			}
    		
			String idTransazione = (pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) ? PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext) : null;
			//synchronized (GestoreLoadBalancerCaching.cache) {
			GestoreLoadBalancerCaching.lock.acquireThrowRuntime("getNomeConnettore", idTransazione);
			try {
				
				response = 
					(org.openspcoop2.utils.cache.CacheResponse) GestoreLoadBalancerCaching.cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:getLoadBalancerPool) in cache.");
						
						StickyConnector stickyConnector = (StickyConnector) response.getObject();
						if(stickyConnector.getExpirationDate()!=null) {
							Date now = DateManager.getDate();
							if(stickyConnector.getExpirationDate().after(now)){
								String c = stickyConnector.getConnector();
								if(loadBalancerPool.getConnectorNames(true).contains(c)) {
									return c;
								}
								else {
									String msg = "Connettore '"+stickyConnector.getConnector()+"' per sticky '"+stickyResult.getCondition()+"' non risulta più valido dopo il passive health check";
									log.debug(msg);
									try {
										GestoreLoadBalancerCaching.cache.remove(keyCache);
									}catch(Exception e) {
										throw new BehaviourException(msg+". Rimozione dalla cache non riuscita: "+e.getMessage(),e);
									}
								}
							}
							else {
								String msg = "Connettore '"+stickyConnector.getConnector()+"' scaduto per sticky '"+stickyResult.getCondition()+"' in data "+
										DateUtils.getSimpleDateFormatMs().format(stickyConnector.getExpirationDate())+"";
								log.debug(msg);
								try {
									GestoreLoadBalancerCaching.cache.remove(keyCache);
								}catch(Exception e) {
									throw new BehaviourException(msg+". Rimozione dalla cache non riuscita: "+e.getMessage(),e);
								}
							}
						}
						else {
							String c = stickyConnector.getConnector();
							if(loadBalancerPool.getConnectorNames(true).contains(c)) {
								return c;
							}
							else {
								String msg = "Connettore '"+stickyConnector.getConnector()+"' per sticky '"+stickyResult.getCondition()+"' non risulta più valido dopo il passive health check";
								log.debug(msg);
								try {
									GestoreLoadBalancerCaching.cache.remove(keyCache);
								}catch(Exception e) {
									throw new BehaviourException(msg+". Rimozione dalla cache non riuscita: "+e.getMessage(),e);
								}
							}
						}
						
					}else if(response.getException()!=null){
						log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:getLoadBalancerPool) in cache.");
						throw new BehaviourException( (Exception) response.getException() );
					}else{
						log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}

				// Effettuo la query
				log.debug("oggetto con chiave ["+keyCache+"] (method:getNomeConnettore) ricerco nella configurazione...");
				String selectedConnector = selectConnector(loadBalancerType, loadBalancerPool, pddContext);
				if(selectedConnector==null) {
					throw new BehaviourException("Metodo (getNomeConnettore) non è riuscito a selezionare un connettore");
				}
				StickyConnector stickyConnector = new StickyConnector();
				stickyConnector.setConnector(selectedConnector);
				if(stickyResult.getMaxAgeSeconds()!=null && stickyResult.getMaxAgeSeconds().intValue()>0) {
					Date expire = new Date((DateManager.getTimeMillis() + (1000*stickyResult.getMaxAgeSeconds().intValue())));
					stickyConnector.setExpirationDate(expire);
				}
				
				// Aggiungo la risposta in cache (se esiste una cache)	
				// Sempre. Se la risposta non deve essere cachata l'implementazione può in alternativa:
				// - impostare una eccezione di processamento (che setta automaticamente noCache a true)
				// - impostare il noCache a true
				log.info("Aggiungo oggetto ["+keyCache+"] in cache");
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					responseCache.setObject(stickyConnector);
					GestoreLoadBalancerCaching.cache.put(keyCache,responseCache);
				}catch(UtilsException e){
					log.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
				}
				return stickyConnector.getConnector();

			}finally {
				GestoreLoadBalancerCaching.lock.release("getNomeConnettore", idTransazione);
			}
    	}
    	
    }

	private static String selectConnector(LoadBalancerType loadBalancerType, LoadBalancerPool pool, PdDContext pddContext) throws BehaviourException {
		LoadBalancer lb = new LoadBalancer(loadBalancerType, pool, pddContext);
		String nomeConnettore = lb.selectConnector();
		if(nomeConnettore==null) {
			throw new BehaviourException("Nessun connettore selezionato");
		}
		return nomeConnettore;
	}
}
