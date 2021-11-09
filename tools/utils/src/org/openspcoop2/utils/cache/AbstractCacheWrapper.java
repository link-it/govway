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

package org.openspcoop2.utils.cache;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.openspcoop2.utils.UtilsException;

/**
 * AbstractCacheManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractCacheWrapper {

	private final org.openspcoop2.utils.Semaphore lockCache = new org.openspcoop2.utils.Semaphore("AbstractCacheWrapper");
	private Cache cache = null;
	private Logger log = null;
	private String cacheName = null;
	
	public AbstractCacheWrapper(String cacheName, Logger log) throws UtilsException{
		
		this(cacheName, true, log, null, null, null, null);
		
	}
	
	public AbstractCacheWrapper(String cacheName, boolean initializeCache, Logger log) throws UtilsException{
		
		this(cacheName, initializeCache, log, null, null, null, null);
		
	}
	
	public AbstractCacheWrapper(String cacheName, Logger log,
			Integer cacheSize,CacheAlgorithm cacheAlgorithm,
			Integer itemIdleTimeSeconds, Integer itemLifeTimeSeconds) throws UtilsException{
		
		this(cacheName, true, log, cacheSize, cacheAlgorithm, itemIdleTimeSeconds, itemLifeTimeSeconds);
		
	}
	
	private AbstractCacheWrapper(String cacheName, boolean initializeCache, Logger log,
			Integer cacheSize,CacheAlgorithm cacheAlgorithm,
			Integer itemIdleTimeSeconds, Integer itemLifeTimeSeconds) throws UtilsException{
		
		if(cacheName==null){
			throw new UtilsException("Cache name undefined");
		}
		this.cacheName = cacheName;
		
		if(log==null){
			throw new UtilsException("Logger undefined");
		}
		this.log = log;
		
		if(initializeCache){
			this.initCacheConfigurazione(cacheSize, cacheAlgorithm, itemIdleTimeSeconds, itemLifeTimeSeconds);
		}
	}
	
	public String getCacheName() {
		return this.cacheName;
	}
		
	private void initCacheConfigurazione(Integer cacheSize,CacheAlgorithm cacheAlgorithm,
			Integer itemIdleTimeSeconds, Integer itemLifeTimeSeconds) throws UtilsException{
		
		this.cache = new Cache(this.cacheName);
		
		String msg = null;
		if( (cacheSize!=null) ||
				(cacheAlgorithm != null) ){
	     
			if( cacheSize!=null ){
				int dimensione = -1;
				try{
					dimensione = cacheSize.intValue();
					msg = "Cache size ("+this.cacheName+"): "+dimensione;
					this.log.debug(msg);
					this.cache.setCacheSize(dimensione);
				}catch(Exception error){
					throw new UtilsException("Cache size parameter wrong ("+this.cacheName+"): "+error.getMessage());
				}
			}
			if( cacheAlgorithm != null ){
				msg = "Cache algorithm ("+this.cacheName+"): "+cacheAlgorithm.name();
				this.log.debug(msg);
				this.cache.setCacheAlgoritm(cacheAlgorithm);
			}
			
		}
		
		if( (itemIdleTimeSeconds != null) ||
				(itemLifeTimeSeconds != null) ){

			if( itemIdleTimeSeconds != null  ){
				int itemIdleTime = -1;
				try{
					itemIdleTime = itemIdleTimeSeconds.intValue();
					msg = "Cache 'IdleTime' attribute ("+this.cacheName+"): "+itemIdleTimeSeconds;
					this.log.debug(msg);
					this.cache.setItemIdleTime(itemIdleTime);
				}catch(Exception error){
					throw new UtilsException("Cache 'IdleTime' attribute wrong ("+this.cacheName+"): "+error.getMessage());
				}
			}
			if( itemLifeTimeSeconds != null  ){
				int itemLifeSecond = -1;
				try{
					itemLifeSecond = itemLifeTimeSeconds.intValue();
					msg = "Cache 'LifeTime' attribute ("+this.cacheName+"): "+itemLifeSecond;
					this.log.debug(msg);
					this.cache.setItemLifeTime(itemLifeSecond);
				}catch(Exception error){
					throw new UtilsException("Cache 'LifeTime' attribute wrong ("+this.cacheName+"): "+error.getMessage());
				}
			}
			
		}
	}
	
	
	
	/* --------------- Cache --------------------*/
	
	public Logger getLog() {
		return this.log;
	}
	
	public boolean isCacheEnabled(){
		return this.cache!=null;
	}
	
	public void disableSyncronizedGet() throws UtilsException {
		if(this.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		this.cache.disableSyncronizedGet();
	}
	public boolean isDisableSyncronizedGet() throws UtilsException {
		if(this.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		return this.cache.isDisableSyncronizedGet();
	}
	
	public void resetCache() throws UtilsException{
		if(this.cache!=null){
			try{
				this.cache.clear();
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public String printStatsCache(String separator) throws UtilsException{
		if(this.cache!=null){
			try{
				return this.cache.printStats(separator);
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}else{
			throw new UtilsException("Cache disabled");
		}
	}
	public void enableCache() throws UtilsException{
		if(this.cache!=null)
			throw new UtilsException("Cache already enabled");
		else{
			try{
				this.cache = new Cache(this.cacheName);
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public void enableCache(Integer cacheSize,Boolean cacheAlgorithmLRU,Integer itemIdleTimeSeconds,Integer itemLifeTimeSeconds) throws UtilsException{
		
		CacheAlgorithm cacheAlgorithm = null;
		if(cacheAlgorithmLRU!=null){
			if(cacheAlgorithmLRU){
				cacheAlgorithm = CacheAlgorithm.LRU;
			}
			else{
				cacheAlgorithm = CacheAlgorithm.MRU;
			}
		}
		
		this.enableCache(cacheSize, cacheAlgorithm, itemIdleTimeSeconds, itemLifeTimeSeconds);
		
	}
	public void enableCache(Integer cacheSize,CacheAlgorithm cacheAlgorithm,Integer itemIdleTimeSeconds,Integer itemLifeTimeSeconds) throws UtilsException{
		if(this.cache!=null)
			throw new UtilsException("Cache already enabled");
		else{
			try{
				initCacheConfigurazione(cacheSize, cacheAlgorithm, itemIdleTimeSeconds, itemLifeTimeSeconds);
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public void disableCache() throws UtilsException{
		if(this.cache==null)
			throw new UtilsException("Cache already disabled");
		else{
			try{
				this.cache.clear();
				this.cache = null;
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public String listKeysCache(String separator) throws UtilsException{
		if(this.cache!=null){
			try{
				return this.cache.printKeys(separator);
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}else{
			throw new UtilsException("Cache disabled");
		}
	}
	public String getObjectCache(String key) throws UtilsException{
		if(this.cache!=null){
			try{
				Object o = this.cache.get(key);
				if(o!=null){
					return o.toString();
				}else{
					return "Object with key ["+key+"] not exists";
				}
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}else{
			throw new UtilsException("Cache disabled");
		}
	}
	public String removeObjectCache(String key) throws UtilsException{
		if(this.cache!=null){
			try{
				Object o = this.cache.get(key);
				if(o!=null){
					this.cache.remove(key);
					return "Object with key ["+key+"] deleted";
				}else{
					return "Object with key ["+key+"] not exists";
				}
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}else{
			throw new UtilsException("Cache disabled");
		}
	}
	
	
	
	
	
	/* --------------- CacheWrapper --------------------*/
	
	public abstract Object getDriver(Object param) throws UtilsException;
	public abstract boolean isCachableException(Throwable e);
	
	// Serve per poter sapere quale sia la chiave ed usarla nei metodi soprastanti
	public String getKeyCache(String keyCacheParam,String methodName){
		return this.buildKeyCache(keyCacheParam, methodName); 
	}
	
	private String buildKeyCache(String keyCacheParam,String methodName){
		if(keyCacheParam!=null && !"".equals(keyCacheParam))
			return methodName + "." + keyCacheParam;
		else
			return methodName;
	}
	
	public void duplicateObjectCache(String oldKeyCacheParam,String oldMethodName, 
			String newKeyCacheParam,String newMethodName,
			boolean debug,boolean throwExceptionIfNotExists) throws UtilsException{
		
		if(this.cache==null){
			throw new UtilsException("Cache disabled");
		}
		
		//synchronized(this.cache){
		this.lockCache.acquireThrowRuntime("duplicateObjectCache");
		try {
			
//			if(debug){
//				this.log.debug("@"+keyCache+"@ Cache info: "+this.cache.toString());
//				this.log.debug("@"+keyCache+"@ Keys: \n\t"+this.cache.printKeys("\n\t"));
//			}
			
			String oldKey = this.buildKeyCache(oldKeyCacheParam, oldMethodName);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(oldKey);
			if(response != null){
				String newKey = this.buildKeyCache(newKeyCacheParam, newMethodName);
				this.cache.remove(newKey);
				this.cache.put(newKey,response);
				this.log.debug("@"+newKey+"@ Entry add");
			}
			else{
				if(debug){
					this.log.debug("@"+oldKey+"@ Entry not exists");
				}
				if(throwExceptionIfNotExists){
					throw new UtilsException("Entry with key ["+oldKey+"] not exists");
				}
			}
		}finally {
			this.lockCache.release("duplicateObjectCache");
		}
		
	}
	
	public Object getObjectCache(Object driverParam,boolean debug,String keyCacheParam,String methodName,Object... arguments) throws Throwable{
		return _getObjectCache(driverParam,debug,keyCacheParam,methodName,null,arguments);
	}
	public Object getObjectCache(Object driverParam,boolean debug,String keyCacheParam,String methodName,Class<?> [] cArguments, Object... arguments) throws Throwable{
		return _getObjectCache(driverParam,debug,keyCacheParam,methodName,cArguments,arguments);
	}
	public Object _getObjectCache(Object driverParam,boolean debug,String keyCacheParam,String methodName,Class<?> [] cArguments, Object... arguments) throws Throwable{
				
		
		Throwable cachableException = null;
		Throwable notCachableException = null;
		Object obj = null;
		boolean throwException = false;
		
		if(methodName == null)
			throw new UtilsException("MethodName undefined");
		
		String keyCache = null;
		
		if(this.cache==null){
			if(debug){
				this.log.debug("@method:"+methodName+"@ (Cache Disabled) search object with driver...");
			}
			try{
				obj = getObject(driverParam, debug, methodName, cArguments, arguments);
			}catch(Throwable e){
				if(this.isCachableException(e)){
					cachableException = e;
				}
				else{
					notCachableException = e;
				}
			}
		}
		else{
		
			// Fix: devo prima verificare se ho la chiave in cache prima di mettermi in sincronizzazione.
//			if(keyCacheParam == null)
//				throw new UtilsException("["+methodName+"]: KeyCache undefined");	
			keyCache = this.buildKeyCache(keyCacheParam, methodName);
			
//			if(debug){
//				this.log.debug("@"+keyCache+"@ Cache info: "+this.cache.toString());
//				this.log.debug("@"+keyCache+"@ Keys: \n\t"+this.cache.printKeys("\n\t"));
//			}
	
			// se e' attiva una cache provo ad utilizzarla
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(keyCache);
			if(response != null){
				if(response.getObject()!=null){
					if(debug){
						this.log.debug("@"+keyCache+"@ Object (type:"+response.getObject().getClass().getName()+") (method:"+methodName+") found in cache.");
					}
					return response.getObject();
				}
				else if(response.isObjectNull()){
					if(debug){
						this.log.debug("@"+keyCache+"@ Response null (method:"+methodName+") found in cache.");
					}
					return null;
				}
				else if(response.getException()!=null){
					this.log.debug("@"+keyCache+"@ Exception (type:"+response.getException().getClass().getName()+") (method:"+methodName+") found in cache.");
					throwException = true;
					throw (Throwable) response.getException();
				}else{
					this.log.error("@"+keyCache+"@ Found entry in cache with key ["+keyCache+"] (method:"+methodName+") without object and exception???");
				}
			}
			
			//synchronized(this.cache){
			this.lockCache.acquire("getObjectCache");
			try {
				
				try{
										
//					if(debug){
//						this.log.debug("@"+keyCache+"@ Cache info: "+this.cache.toString());
//						this.log.debug("@"+keyCache+"@ Keys: \n\t"+this.cache.printKeys("\n\t"));
//					}
		
					// se e' attiva una cache provo ad utilizzarla
					response = 
						(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(keyCache);
					if(response != null){
						if(response.getObject()!=null){
							if(debug){
								this.log.debug("@"+keyCache+"@ Object (type:"+response.getObject().getClass().getName()+") (method:"+methodName+") found in cache.");
							}
							return response.getObject();
						}
						else if(response.isObjectNull()){
							if(debug){
								this.log.debug("@"+keyCache+"@ Response null (method:"+methodName+") found in cache.");
							}
							return null;
						}
						else if(response.getException()!=null){
							this.log.debug("@"+keyCache+"@ Exception (type:"+response.getException().getClass().getName()+") (method:"+methodName+") found in cache.");
							throwException = true;
							throw (Throwable) response.getException();
						}else{
							this.log.error("@"+keyCache+"@ Found entry in cache with key ["+keyCache+"] (method:"+methodName+") without object and exception???");
						}
					}
		
					// Effettuo le query
					if(debug){
						this.log.debug("@"+keyCache+"@ search object (method:"+methodName+") with driver...");
					}
					boolean nullObject = false;
					boolean cacheble = false;
					try{
						obj = getObject(driverParam, debug, methodName, cArguments, arguments);
						nullObject = true;
						cacheble = true;
					}catch(Throwable e){
						if(this.isCachableException(e)){
							cachableException = e;
							cacheble = true;
						}
						else{
							notCachableException = e;
						}
					}
					
							
					// Aggiungo la risposta in cache (se esiste una cache)	
					// Se ho una eccezione aggiungo in cache solo una not found
					if( cacheble ){ 	
						try{	
							org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
							if(cachableException!=null){
								if(debug){
									this.log.debug("@"+keyCache+"@ Add Exception in cache");
								}
								responseCache.setException(cachableException);
							}else if(obj!=null){
								if(debug){
									this.log.debug("@"+keyCache+"@ Add Object in cache");
								}
								responseCache.setObject((java.io.Serializable)obj);
							}else if(nullObject){
								if(debug){
									this.log.debug("@"+keyCache+"@ Add Null Response in cache");
								}
								responseCache.setObjectNull(true);
							}
							this.cache.put(keyCache,responseCache);
						}catch(UtilsException e){
							this.log.error("@"+keyCache+"@ error occurs during insert in cache: "+e.getMessage(),e);
						}
					}
		
				}catch(Throwable e){
					if(throwException == false){
						this.log.error("@"+keyCache+"@ Error occurs: "+e.getMessage(),e);
						throw new UtilsException(e.getMessage(),e);
					}
					else{
						cachableException = e;
					}
				}
				
			}finally {
				this.lockCache.release("getObjectCache");
			}
		}
		
		String cacheMsg = "";
		if(this.cache!=null && keyCache!=null){
			cacheMsg = "@key:"+keyCache+"@";
		}
		else{
			cacheMsg = "@method:"+methodName+"@";
		}
		if(cachableException!=null){
			if(debug){
				this.log.debug(cacheMsg+" throw CachableException: "+cachableException.getClass().getName());
			}
			throw cachableException;
		}
		else if(notCachableException!=null){
			if(debug){
				this.log.debug(cacheMsg+" throw NotCachableException: "+notCachableException.getClass().getName());
			}
			throw notCachableException;
		}
		else if(obj!=null){
			if(debug){
				this.log.debug(cacheMsg+" return Object: "+obj.getClass().getName());
			}
			return obj;
		}else{
			if(debug){
				this.log.debug(cacheMsg+" return null response");
			}
			return null;
		}
	}
	
	private Object getObject(Object driverParam,boolean debug,String methodName,Class<?> [] cArguments, Object... arguments) throws Throwable{

		
		// Effettuo le query nella mia gerarchia di registri.
		Object obj = null;
		try{
			Object driver = this.getDriver(driverParam);
			if(driver==null){
				throw new UtilsException("Driver undefined");
			}
			if(arguments.length==0){
				Method method =  driver.getClass().getMethod(methodName);
				obj = method.invoke(driver);
			}else if(arguments.length==1){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()));
				obj = method.invoke(driver,
						arguments[0]);
			}else if(arguments.length==2){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()),
						(cArguments!=null ? cArguments[1] :arguments[1].getClass()));
				obj = method.invoke(driver,
						arguments[0],
						arguments[1]);
			}else if(arguments.length==3){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()),
						(cArguments!=null ? cArguments[1] :arguments[1].getClass()),
						(cArguments!=null ? cArguments[2] :arguments[2].getClass()));
				obj = method.invoke(driver,
						arguments[0],
						arguments[1],
						arguments[2]);
			}else if(arguments.length==4){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()),
						(cArguments!=null ? cArguments[1] :arguments[1].getClass()),
						(cArguments!=null ? cArguments[2] :arguments[2].getClass()),
						(cArguments!=null ? cArguments[3] :arguments[3].getClass()));
				obj = method.invoke(driver,
						arguments[0],
						arguments[1],
						arguments[2],
						arguments[3]);
			}else if(arguments.length==5){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()),
						(cArguments!=null ? cArguments[1] :arguments[1].getClass()),
						(cArguments!=null ? cArguments[2] :arguments[2].getClass()),
						(cArguments!=null ? cArguments[3] :arguments[3].getClass()),
						(cArguments!=null ? cArguments[4] :arguments[4].getClass()));
				obj = method.invoke(driver,
						arguments[0],
						arguments[1],
						arguments[2],
						arguments[3],
						arguments[4]);
			}else if(arguments.length==6){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()),
						(cArguments!=null ? cArguments[1] :arguments[1].getClass()),
						(cArguments!=null ? cArguments[2] :arguments[2].getClass()),
						(cArguments!=null ? cArguments[3] :arguments[3].getClass()),
						(cArguments!=null ? cArguments[4] :arguments[4].getClass()),
						(cArguments!=null ? cArguments[5] :arguments[5].getClass()));
				obj = method.invoke(driver,
						arguments[0],
						arguments[1],
						arguments[2],
						arguments[3],
						arguments[4],
						arguments[5]);
			}else if(arguments.length==7){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()),
						(cArguments!=null ? cArguments[1] :arguments[1].getClass()),
						(cArguments!=null ? cArguments[2] :arguments[2].getClass()),
						(cArguments!=null ? cArguments[3] :arguments[3].getClass()),
						(cArguments!=null ? cArguments[4] :arguments[4].getClass()),
						(cArguments!=null ? cArguments[5] :arguments[5].getClass()),
						(cArguments!=null ? cArguments[6] :arguments[6].getClass()));
				obj = method.invoke(driver,
						arguments[0],
						arguments[1],
						arguments[2],
						arguments[3],
						arguments[4],
						arguments[5],
						arguments[6]);
			}else if(arguments.length==8){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()),
						(cArguments!=null ? cArguments[1] :arguments[1].getClass()),
						(cArguments!=null ? cArguments[2] :arguments[2].getClass()),
						(cArguments!=null ? cArguments[3] :arguments[3].getClass()),
						(cArguments!=null ? cArguments[4] :arguments[4].getClass()),
						(cArguments!=null ? cArguments[5] :arguments[5].getClass()),
						(cArguments!=null ? cArguments[6] :arguments[6].getClass()),
						(cArguments!=null ? cArguments[7] :arguments[7].getClass()));
				obj = method.invoke(driver,
						arguments[0],
						arguments[1],
						arguments[2],
						arguments[3],
						arguments[4],
						arguments[5],
						arguments[6],
						arguments[7]);
			}else if(arguments.length==9){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()),
						(cArguments!=null ? cArguments[1] :arguments[1].getClass()),
						(cArguments!=null ? cArguments[2] :arguments[2].getClass()),
						(cArguments!=null ? cArguments[3] :arguments[3].getClass()),
						(cArguments!=null ? cArguments[4] :arguments[4].getClass()),
						(cArguments!=null ? cArguments[5] :arguments[5].getClass()),
						(cArguments!=null ? cArguments[6] :arguments[6].getClass()),
						(cArguments!=null ? cArguments[7] :arguments[7].getClass()),
						(cArguments!=null ? cArguments[8] :arguments[8].getClass()));
				obj = method.invoke(driver,
						arguments[0],
						arguments[1],
						arguments[2],
						arguments[3],
						arguments[4],
						arguments[5],
						arguments[6],
						arguments[7],
						arguments[8]);
			}else if(arguments.length==10){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()),
						(cArguments!=null ? cArguments[1] :arguments[1].getClass()),
						(cArguments!=null ? cArguments[2] :arguments[2].getClass()),
						(cArguments!=null ? cArguments[3] :arguments[3].getClass()),
						(cArguments!=null ? cArguments[4] :arguments[4].getClass()),
						(cArguments!=null ? cArguments[5] :arguments[5].getClass()),
						(cArguments!=null ? cArguments[6] :arguments[6].getClass()),
						(cArguments!=null ? cArguments[7] :arguments[7].getClass()),
						(cArguments!=null ? cArguments[8] :arguments[8].getClass()),
						(cArguments!=null ? cArguments[9] :arguments[9].getClass()));
				obj = method.invoke(driver,
						arguments[0],
						arguments[1],
						arguments[2],
						arguments[3],
						arguments[4],
						arguments[5],
						arguments[6],
						arguments[7],
						arguments[8],
						arguments[9]);
			}else if(arguments.length==11){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()),
						(cArguments!=null ? cArguments[1] :arguments[1].getClass()),
						(cArguments!=null ? cArguments[2] :arguments[2].getClass()),
						(cArguments!=null ? cArguments[3] :arguments[3].getClass()),
						(cArguments!=null ? cArguments[4] :arguments[4].getClass()),
						(cArguments!=null ? cArguments[5] :arguments[5].getClass()),
						(cArguments!=null ? cArguments[6] :arguments[6].getClass()),
						(cArguments!=null ? cArguments[7] :arguments[7].getClass()),
						(cArguments!=null ? cArguments[8] :arguments[8].getClass()),
						(cArguments!=null ? cArguments[9] :arguments[9].getClass()),
						(cArguments!=null ? cArguments[10] :arguments[10].getClass()));
				obj = method.invoke(driver,
						arguments[0],
						arguments[1],
						arguments[2],
						arguments[3],
						arguments[4],
						arguments[5],
						arguments[6],
						arguments[7],
						arguments[8],
						arguments[9],
						arguments[10]);
			}else if(arguments.length==12){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()),
						(cArguments!=null ? cArguments[1] :arguments[1].getClass()),
						(cArguments!=null ? cArguments[2] :arguments[2].getClass()),
						(cArguments!=null ? cArguments[3] :arguments[3].getClass()),
						(cArguments!=null ? cArguments[4] :arguments[4].getClass()),
						(cArguments!=null ? cArguments[5] :arguments[5].getClass()),
						(cArguments!=null ? cArguments[6] :arguments[6].getClass()),
						(cArguments!=null ? cArguments[7] :arguments[7].getClass()),
						(cArguments!=null ? cArguments[8] :arguments[8].getClass()),
						(cArguments!=null ? cArguments[9] :arguments[9].getClass()),
						(cArguments!=null ? cArguments[10] :arguments[10].getClass()),
						(cArguments!=null ? cArguments[11] :arguments[11].getClass()));
				obj = method.invoke(driver,
						arguments[0],
						arguments[1],
						arguments[2],
						arguments[3],
						arguments[4],
						arguments[5],
						arguments[6],
						arguments[7],
						arguments[8],
						arguments[9],
						arguments[10],
						arguments[11]);	
			}else if(arguments.length==13){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()),
						(cArguments!=null ? cArguments[1] :arguments[1].getClass()),
						(cArguments!=null ? cArguments[2] :arguments[2].getClass()),
						(cArguments!=null ? cArguments[3] :arguments[3].getClass()),
						(cArguments!=null ? cArguments[4] :arguments[4].getClass()),
						(cArguments!=null ? cArguments[5] :arguments[5].getClass()),
						(cArguments!=null ? cArguments[6] :arguments[6].getClass()),
						(cArguments!=null ? cArguments[7] :arguments[7].getClass()),
						(cArguments!=null ? cArguments[8] :arguments[8].getClass()),
						(cArguments!=null ? cArguments[9] :arguments[9].getClass()),
						(cArguments!=null ? cArguments[10] :arguments[10].getClass()),
						(cArguments!=null ? cArguments[11] :arguments[11].getClass()),
						(cArguments!=null ? cArguments[12] :arguments[12].getClass()));
				obj = method.invoke(driver,
						arguments[0],
						arguments[1],
						arguments[2],
						arguments[3],
						arguments[4],
						arguments[5],
						arguments[6],
						arguments[7],
						arguments[8],
						arguments[9],
						arguments[10],
						arguments[11],
						arguments[12]);	
			}else if(arguments.length==14){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()),
						(cArguments!=null ? cArguments[1] :arguments[1].getClass()),
						(cArguments!=null ? cArguments[2] :arguments[2].getClass()),
						(cArguments!=null ? cArguments[3] :arguments[3].getClass()),
						(cArguments!=null ? cArguments[4] :arguments[4].getClass()),
						(cArguments!=null ? cArguments[5] :arguments[5].getClass()),
						(cArguments!=null ? cArguments[6] :arguments[6].getClass()),
						(cArguments!=null ? cArguments[7] :arguments[7].getClass()),
						(cArguments!=null ? cArguments[8] :arguments[8].getClass()),
						(cArguments!=null ? cArguments[9] :arguments[9].getClass()),
						(cArguments!=null ? cArguments[10] :arguments[10].getClass()),
						(cArguments!=null ? cArguments[11] :arguments[11].getClass()),
						(cArguments!=null ? cArguments[12] :arguments[12].getClass()),
						(cArguments!=null ? cArguments[13] :arguments[13].getClass()));
				obj = method.invoke(driver,
						arguments[0],
						arguments[1],
						arguments[2],
						arguments[3],
						arguments[4],
						arguments[5],
						arguments[6],
						arguments[7],
						arguments[8],
						arguments[9],
						arguments[10],
						arguments[11],
						arguments[12],
						arguments[13]);	
			}else if(arguments.length==15){
				Method method =  driver.getClass().getMethod(methodName,
						(cArguments!=null ? cArguments[0] :arguments[0].getClass()),
						(cArguments!=null ? cArguments[1] :arguments[1].getClass()),
						(cArguments!=null ? cArguments[2] :arguments[2].getClass()),
						(cArguments!=null ? cArguments[3] :arguments[3].getClass()),
						(cArguments!=null ? cArguments[4] :arguments[4].getClass()),
						(cArguments!=null ? cArguments[5] :arguments[5].getClass()),
						(cArguments!=null ? cArguments[6] :arguments[6].getClass()),
						(cArguments!=null ? cArguments[7] :arguments[7].getClass()),
						(cArguments!=null ? cArguments[8] :arguments[8].getClass()),
						(cArguments!=null ? cArguments[9] :arguments[9].getClass()),
						(cArguments!=null ? cArguments[10] :arguments[10].getClass()),
						(cArguments!=null ? cArguments[11] :arguments[11].getClass()),
						(cArguments!=null ? cArguments[12] :arguments[12].getClass()),
						(cArguments!=null ? cArguments[13] :arguments[13].getClass()),
						(cArguments!=null ? cArguments[14] :arguments[14].getClass()));
				obj = method.invoke(driver,
						arguments[0],
						arguments[1],
						arguments[2],
						arguments[3],
						arguments[4],
						arguments[5],
						arguments[6],
						arguments[7],
						arguments[8],
						arguments[9],
						arguments[10],
						arguments[11],
						arguments[12],
						arguments[13],
						arguments[14]);
			}else
				throw new Exception("More than 15 arguments unsupported");
		}catch(java.lang.reflect.InvocationTargetException e){
			if(e.getTargetException()!=null){
				throw e.getTargetException();
			}else{
				throw e;
			}
		}
		catch(Exception e){
			throw e;
		}
		
		return obj;
		
	}
}
