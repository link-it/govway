/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.jcs3.JCS;
import org.apache.commons.jcs3.access.CacheAccess;
import org.apache.commons.jcs3.admin.CountingOnlyOutputStream;
import org.apache.commons.jcs3.admin.JCSAdminBean;
import org.apache.commons.jcs3.engine.CacheElementSerialized;
import org.apache.commons.jcs3.engine.behavior.ICacheElement;
import org.apache.commons.jcs3.engine.behavior.ICompositeCacheAttributes;
import org.apache.commons.jcs3.engine.behavior.IElementAttributes;
import org.apache.commons.jcs3.engine.control.CompositeCache;
import org.apache.commons.jcs3.engine.memory.behavior.IMemoryCache;
import org.apache.commons.jcs3.log.LogManager;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.CollectionProperties;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.slf4j.Logger;

/**
 * Cache
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Cache {

	public static boolean initialize(Logger logConsole,Logger logCore,
			String cachePropertiesName,
			String rootDirectory,Properties objectProperties,
			String OPENSPCOOP2_LOCAL_HOME,String OPENSPCOOP2_CACHE_PROPERTIES,String OPENSPCOOP2_CACHE_LOCAL_PATH){
		try{
			
			// Originale
			java.util.Properties cacheProperties = new java.util.Properties();
			java.io.File loggerFile = new java.io.File(rootDirectory+cachePropertiesName);
			if(loggerFile .exists() == false ){
				cacheProperties.load(Cache.class.getResourceAsStream("/"+cachePropertiesName));
			}else{
				FileInputStream fin = null;
				try{
					fin = new java.io.FileInputStream(loggerFile);
					cacheProperties.load(fin);
				}finally{
					try{
						if(fin!=null){
							fin.close();
						}
					}catch(Exception eClose){}
				}
			}
			
			// File Local Implementation
			CollectionProperties cachePropertiesRidefinito =  
				PropertiesUtilities.searchLocalImplementation(OPENSPCOOP2_LOCAL_HOME,logConsole, OPENSPCOOP2_CACHE_PROPERTIES ,OPENSPCOOP2_CACHE_LOCAL_PATH, rootDirectory);
			if(cachePropertiesRidefinito!=null && cachePropertiesRidefinito.size()>0){
				Enumeration<?> ridefinito = cachePropertiesRidefinito.keys();
				while (ridefinito.hasMoreElements()) {
					String key = (String) ridefinito.nextElement();
					String value = (String) cachePropertiesRidefinito.get(key);
					if(cacheProperties.containsKey(key)){
						//Object o = 
						cacheProperties.remove(key);
					}
					cacheProperties.put(key, value);
					//System.out.println("CHECK NUOVO VALORE: "+loggerProperties.get(key));
				}
			}
			
			// File Object Implementation
			if(objectProperties!=null && objectProperties.size()>0){
				Enumeration<?> ridefinito = objectProperties.keys();
				while (ridefinito.hasMoreElements()) {
					String key = (String) ridefinito.nextElement();
					String value = (String) objectProperties.get(key);
					if(cacheProperties.containsKey(key)){
						//Object o = 
						cacheProperties.remove(key);
					}
					cacheProperties.put(key, value);
					//System.out.println("CHECK NUOVO VALORE: "+loggerProperties.get(key));
				}
			}
			
			JCS.setConfigProperties(cacheProperties);
			
			return true;
			
		}catch(Exception e){
			logCore.error("Riscontrato errore durante l'inizializzazione del sistema di cache: "
					+e.getMessage(),e);
			return false;
		}
	}
	
	public static String printStatistics(String separatorStat, String separatorCache) throws UtilsException {
		Cache cache = new Cache();
		return cache.printAllStats(separatorStat, separatorCache);
	}
	public static void printStatistics(OutputStream out, String separatorStat, String separatorCache) throws UtilsException {
		Cache cache = new Cache();
		cache.printAllStats(out, separatorStat, separatorCache);
	}
	
	
	private CacheAccess<Object, Serializable> cache = null;
	@SuppressWarnings("unused")
	private JCSAdminBean cacheAdmin = null;
	private String cacheName = null;
	
	private Cache() {
		setLog4jSystem();
		this.cacheAdmin = new JCSAdminBean();
	}
	public Cache(String name) throws UtilsException{
		this(name, DEFAULT_DISABLE_SYNCRONIZED_GET);
	}
	@Deprecated
	private Cache(String name, boolean disableSyncronizedGet) throws UtilsException{
		setLog4jSystem();
		try{
			this.cacheAdmin = new JCSAdminBean();
			this.cacheName = name;
			this.cache = JCS.getInstance(name);
			if(disableSyncronizedGet) {
				// Dalla versione 3.0 non è più presente la gestione del synchronized
				//this.cache.getCacheControl().setSyncDisabled(disableSyncronizedGet);
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static void setLog4jSystem() {
		System.setProperty("jcs.logSystem", LogManager.LOGSYSTEM_LOG4J2);
	}
	
	private static boolean DEFAULT_DISABLE_SYNCRONIZED_GET = false;
	public static boolean DISABLE_SYNCRONIZED_GET = true;
	public static void disableSyncronizedGetAsDefault() {
		DEFAULT_DISABLE_SYNCRONIZED_GET = DISABLE_SYNCRONIZED_GET;
	}
	public static boolean isDisableSyncronizedGetAsDefault() {
		return DEFAULT_DISABLE_SYNCRONIZED_GET;
	}
	
	@Deprecated
	public void disableSyncronizedGet() throws UtilsException {
		if(this.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		try{
			// Dalla versione 3.0 non è più presente la gestione del synchronized
			//this.cache.getCacheControl().setSyncDisabled(true);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	@Deprecated
	public boolean isDisableSyncronizedGet() throws UtilsException {
		if(this.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		try{
			// Dalla versione 3.0 non è più presente la gestione del synchronized
			//return this.cache.getCacheControl().isSyncDisabled();
			return true;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	@Deprecated
	public void enableDebugSystemOut() throws UtilsException {
		if(this.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		try{
			// Dalla versione 3.0 non è più presente la gestione del synchronized
			//this.cache.getCacheControl().setDebugSystemOut(true);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	@Deprecated
	boolean isEnableDebugSystemOut() throws UtilsException {
		if(this.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		try{
			// Dalla versione 3.0 non è più presente la gestione del synchronized
			//return this.cache.getCacheControl().isDebugSystemOut();
			return false;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	

	public int getCacheSize() {
		return this.cache.getCacheAttributes().getMaxObjects();
	}
	public void setCacheSize(int cacheSize) {
		// E' necessario prendere l'oggetto e poi risettarlo a causa di un bug.
		ICompositeCacheAttributes attr = this.cache.getCacheAttributes();
		attr.setMaxObjects(cacheSize);
		this.cache.setCacheAttributes(attr);
	}

	public CacheAlgorithm getCacheAlgoritm() {
		return CacheAlgorithm.toEnum(this.cache.getCacheAttributes().getCacheName());
	}
	public void setCacheAlgoritm(CacheAlgorithm cacheAlgoritm) {
		// E' necessario prendere l'oggetto e poi risettarlo a causa di un bug.
		ICompositeCacheAttributes attr = this.cache.getCacheAttributes();
		attr.setCacheName(cacheAlgoritm.getAlgorithm());
		this.cache.setCacheAttributes(attr);
	}

	public long getItemIdleTime() throws UtilsException {
		try{
			return this.cache.getDefaultElementAttributes().getIdleTime();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void setItemIdleTime(long itemIdleTimeCache) throws UtilsException {
		try{
			// E' necessario prendere l'oggetto e poi risettarlo a causa di un bug.
			IElementAttributes el = this.cache.getDefaultElementAttributes();
			el.setIdleTime(itemIdleTimeCache);
			this.cache.setDefaultElementAttributes(el);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

	public long getItemLifeTime() throws UtilsException {
		try{
			return this.cache.getDefaultElementAttributes().getMaxLife();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public boolean isEternal() throws UtilsException {
		try{
			return this.cache.getDefaultElementAttributes().getIsEternal();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void setItemLifeTime(long itemLifeTimeCache) throws UtilsException {
		try{
			// E' necessario prendere l'oggetto e poi risettarlo a causa di un bug.
			IElementAttributes el = this.cache.getDefaultElementAttributes();
			if(itemLifeTimeCache>0) {
				el.setIsEternal(false);
				el.setMaxLife(itemLifeTimeCache);
			}
			else {
				el.setIsEternal(true);
				el.setMaxLife(-1);
			}
			this.cache.setDefaultElementAttributes(el);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	public void clear() throws UtilsException{
		if(this.cache!=null){
			try{
				this.cache.clear();
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}

	
	public Object get(String key){
		return this.cache.get(this.formatKeyCache(key));
	}
	
	public void remove(String key) throws UtilsException{
		try{
			this.cache.remove(this.formatKeyCache(key));
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	
	public void put(String key,org.openspcoop2.utils.cache.CacheResponse value) throws UtilsException{
		try{
			this.cache.put(this.formatKeyCache(key), value);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void put(String key,Serializable value) throws UtilsException{
		try{
			this.cache.put(this.formatKeyCache(key), value);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public int getItemCount()  throws UtilsException {
		CompositeCache<Object, Serializable> cache = JCSAdminBean.getCompositeCacheManager().getCache(this.cacheName);
		return cache.getSize();
	}
	
	public List<String> keys() throws UtilsException {
		try{
			
			List<String> keys = new ArrayList<String>();
			Serializable[] keysObject = this.cache.getCacheControl().getKeySet().toArray(new Serializable[0]);
			if(keysObject!=null){
				for (int i = 0; i < keysObject.length; i++) {
					keys.add((String)keysObject[i]);
				}
			}
			return keys;
        
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public String printStats(String separator) throws UtilsException {
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.printStats(bout,separator);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void printStats(OutputStream out, String separator) throws UtilsException {
		this.printStats(this.cacheName,out,separator,true);
	}
	
	public String printAllStats(String separatorStat, String separatorCache) throws UtilsException {
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.printAllStats(bout,separatorStat,separatorCache);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void printAllStats(OutputStream out, String separatorStat, String separatorCache) throws UtilsException {
		try{
			Set<String> cacheNames_tmp = JCSAdminBean.getCompositeCacheManager().getCacheNames();
			if(cacheNames_tmp.size()>0) {
				String[] cacheNames = new String[cacheNames_tmp.size()];
				int index = 0;
				for (String name : cacheNames_tmp) {
					cacheNames[index] = name;
					index++;
				}
		        Arrays.sort( cacheNames );
		        for ( int i = 0; i < cacheNames.length; i++ ) {
			    	this.printStats(cacheNames[i],out,separatorStat,false);
			    	out.write(separatorCache.getBytes());
			    }
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	// PRIVATE
	
	private boolean errorOccursCountingBytes_debug = false;
	private boolean errorOccursCountingBytes = false;
    public <K, V> long getByteCount(CompositeCache<K, V> cache)
    {
    	this.errorOccursCountingBytes = false;
    	
    	if (cache == null)
    	{
    		throw new IllegalArgumentException("The cache object specified was null.");
    	}

    	long size = 0;
    	IMemoryCache<K, V> memCache = cache.getMemoryCache();

    	try {
    		for (K key : memCache.getKeySet())
    		{
    			ICacheElement<K, V> ice = null;
    			try
    			{
    				ice = memCache.get(key);
    			}
    			catch (IOException e)
    			{
    				//Modificato per openspcoop
    				if(this.errorOccursCountingBytes_debug) {
    					System.err.println("["+this.cacheName+"] Element cache get");
    					e.printStackTrace(System.err);
    				}
    				this.errorOccursCountingBytes = true;
    				continue;
    				//throw new RuntimeException("IOException while trying to get a cached element", e);
    			}

    			if (ice == null)
    			{
    				continue;
    			}

    			if (ice instanceof CacheElementSerialized)
    			{
    				size += ((CacheElementSerialized<K, V>) ice).getSerializedValue().length;
    			}
    			else
    			{
    				Object element = ice.getVal();
    				if(element == null) {
    					//Modificato per openspcoop
    					if(this.errorOccursCountingBytes_debug) {
    						System.err.println("["+this.cacheName+"] Element cache is null");
    					}
    					this.errorOccursCountingBytes = true;
    					continue;
    				}

    				//CountingOnlyOutputStream: Keeps track of the number of bytes written to it, but doesn't write them anywhere.
    				CountingOnlyOutputStream counter = new CountingOnlyOutputStream();
    				try (ObjectOutputStream out = new ObjectOutputStream(counter);)
    				{
    					out.writeObject(element);
    				}
    				catch (IOException e)
    				{
    					// Modificato per openspcoop
    					if(this.errorOccursCountingBytes_debug) {
    						System.err.println("["+this.cacheName+"] Element cache writeObject ("+element.getClass().getName()+")");
    						e.printStackTrace(System.err);
    					}
    					this.errorOccursCountingBytes = true;
    					continue;
    					//throw new RuntimeException("IOException while trying to measure the size of the cached element", e);
    				}
    				finally
    				{
    					try
    					{
    						counter.close();
    					}
    					catch (IOException e)
    					{
    						// ignore
    					}
    				}

    				// 4 bytes lost for the serialization header
		    	   size += counter.getCount() - 4;
    			}
    		}
    	}
    	catch ( Exception e )
    	{
    		System.err.println( "Problem getting byte count (Modified by OpenSPCoop).  Likley cause is a non serilizable object." + e.getMessage() );
    		e.printStackTrace();   
    	}

    	return size;
    }
    
    

	
	
	
	private void printStats(String cacheName, OutputStream out, String separator, boolean thisCache) throws UtilsException {
		
		try{
		
			StringBuilder bf = new StringBuilder();
			
			CompositeCache<Object, Serializable> cache = JCSAdminBean.getCompositeCacheManager().getCache(cacheName);
						
			//bf.append(Utilities.convertBytesToFormatString(region.getByteCount()));
			// NOTA: e' stato re-implementato il metodo poiche' casualmente avveniva un errore del tipo:
			//		 Problem getting byte count.  Likley cause is a non serilizable object.null
			//		 Il problema derivava dall'implementazione del metodo all'interno della classe org/apache/commons/jcs/admin/JCSAdminBean
			//		 Viene utilizzato l'iterator dentro una struttura dinamica che cambia.
			//		 A volte, quando poi veniva registrato l'errore soprastante, avveniva questo errore (scoperto aggiungendo stampe nelle classi di JCS)
			//		 java.util.ConcurrentModificationException
			//		 	at java.util.Hash table$Enumerator.next(Hash table.java:1031)
			//			at org.apache.commons.jcs3.engine.memory.lru.LRUMemoryCache$IteratorWrapper.next(LRUMemoryCache.java:428)
			//			at org.apache.commons.jcs3.admin.JCSAdminBean.getByteCount(JCSAdminBean.java:95)
			int tentativi = 0;
			long sizeAttuale = -1;
			while (tentativi<10) {
				sizeAttuale = this.getByteCount(cache);
				if(this.errorOccursCountingBytes==false){
					break;
				}
				if(thisCache){
					//System.err.println("PROVO ALTRO TENTATIVO");
					tentativi++;
					cache = JCSAdminBean.getCompositeCacheManager().getCache(cacheName);
				}
				else{
					break;
				}
			}
				
			
			bf.append("Nome:");
			bf.append(cacheName);
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("Stato:");
			bf.append(cache.getStatus());
			bf.append(" ");
			
			bf.append(separator);
			
			// Dalla versione 3.0 non è più presente la gestione del synchronized
//			bf.append("GetSyncDisabled:");
//			bf.append(cache.isSyncDisabled());
//			bf.append(" ");
//			
//			bf.append(separator);
			
			if(cache.getCacheAttributes()!=null){
			
				bf.append("Algoritmo:");
				String cacheAlgoName = cache.getCacheAttributes().getCacheName();
				CacheAlgorithm cacheEnum = CacheAlgorithm.toEnum(cacheAlgoName);
				if(cacheEnum!=null){
					bf.append(cacheEnum.name());
				}else{
					bf.append(cacheAlgoName);
				}
				bf.append(" ");
				
				bf.append(separator);
				
				bf.append("Dimensione:");
				bf.append(cache.getCacheAttributes().getMaxObjects());
				bf.append(" ");
				
				bf.append(separator);
				
			}
			
			bf.append("ElementiInCache:");
			bf.append(cache.getSize());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("MemoriaOccupata:");
			if(this.errorOccursCountingBytes){
				bf.append("[WARN !Error occurs counting bytes, re-try!]");
			}
			bf.append(Utilities.convertBytesToFormatString(sizeAttuale));
			bf.append(" ");
			
			bf.append(separator);
			
			if(cache.getElementAttributes()!=null){
			
				bf.append("IdleTime:");
				long idleTime = cache.getElementAttributes().getIdleTime();
				if(idleTime>0){
					bf.append(Utilities.convertSystemTimeIntoString_millisecondi(idleTime*1000,false));
				}
				else if(idleTime==0){
					bf.append("0");
				}
				else if(idleTime<0){
					bf.append("Infinito");
				}
				bf.append(" ");
				
				bf.append(separator);
				
				bf.append("LifeTime:");
				long lifeTime = cache.getElementAttributes().getMaxLife();
				if(lifeTime>0){
					bf.append(Utilities.convertSystemTimeIntoString_millisecondi(lifeTime*1000,false));
				}
				else if(lifeTime==0){
					bf.append("0");
				}
				else if(lifeTime<0){
					if(cache.getElementAttributes().getIsEternal()) {
						bf.append("Infinito");
					}
					else {
						bf.append("Infinito (NoEternal?)");
					}
				}
				bf.append(" ");
				
				bf.append(separator);
			
			}
			
			bf.append("PutCount:");
			bf.append(cache.getUpdateCount());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("HitCount(Aux):");
			bf.append(cache.getHitCountAux());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("HitCount(Ram):");
			bf.append(cache.getHitCountRam());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("MissCount(Expired):");
			bf.append(cache.getMissCountExpired());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("MissCount(NotFound):");
			bf.append(cache.getMissCountNotFound());
			bf.append(" ");
			
			out.write(bf.toString().getBytes());
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	

	
	
	
	/**
	 * Ritorna un intero che rappresenta la chiave di una stringa.
	 *
	 * @param key Stringa su cui effettuare la traduzione.
	 * @return hash della stringa.
	 * 
	 */
	private String formatKeyCache(String key) {
		// org/apache/commons/jcs/engine/control/CompositeCache.java
		//		if ( cacheElement.getKey() instanceof String
		//	            && cacheElement.getKey().toString().endsWith( NAME_COMPONENT_DELIMITER ) )
		//	        {
		//	            throw new IllegalArgumentException( "key must not end with " + NAME_COMPONENT_DELIMITER
		//	                + " for a put operation" );
		//	        }
		//
		// Dove in org.apache.commons.jcs3.engine.behavior.ICache
		// 		public final static String NAME_COMPONENT_DELIMITER = ":";

		/*StringBuilder bf = new StringBuilder(key);
		if(bf.toString().endsWith(org.apache.commons.jcs3.engine.behavior.ICache.NAME_COMPONENT_DELIMITER)){
			bf.append("_");
		}
		return bf.toString();*/
		if(key.endsWith(org.apache.commons.jcs3.engine.behavior.ICache.NAME_COMPONENT_DELIMITER)) {
			return key+"_";
		}
		else {
			return key;
		}
	}
	
	
	
	
	@Override
	public String toString(){
		
		StringBuilder bf = new StringBuilder();
		
		try {
			bf.append("CACHE SIZE["+this.getCacheSize()
					+"] ITEM_COUNT["+this.getItemCount()+"] ITEM_IDLE_TIME["+this.getItemIdleTime()+"] ITEM_IDLE_LIFE["+this.getItemLifeTime()
					+"] IS_ETERNAL["+this.isEternal()
					+"] CACHE_ALGO["+this.getCacheAlgoritm()+"] STATS{"+this.printStats("")+"}");
			return bf.toString();
		} catch (UtilsException e) {
			return "NonDisponibile";
		}
	}
	public String printKeys(String separator) throws UtilsException{
		StringBuilder bf = new StringBuilder();
		java.util.List<String> keys = this.keys();
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			if(i>0){
				bf.append(separator);
			}
			bf.append("Cache["+i+"]=["+key+"]");
		}
		return bf.toString();
	}

	
}
