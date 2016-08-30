/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.utils.cache;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.jcs.JCS;
import org.apache.jcs.admin.CacheRegionInfo;
import org.apache.jcs.admin.CountingOnlyOutputStream;
import org.apache.jcs.admin.JCSAdminBean;
import org.apache.jcs.engine.behavior.ICacheElement;
import org.apache.jcs.engine.behavior.ICompositeCacheAttributes;
import org.apache.jcs.engine.behavior.IElementAttributes;
import org.apache.jcs.engine.control.CompositeCache;
import org.apache.jcs.engine.memory.behavior.IMemoryCache;
import org.slf4j.Logger;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.CollectionProperties;
import org.openspcoop2.utils.resources.PropertiesUtilities;

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
	
	
	private JCS cache = null;
	private JCSAdminBean cacheAdmin = null;
	private String cacheName = null;
	
	private Cache() {
		this.cacheAdmin = new JCSAdminBean();
	}
	public Cache(String name) throws UtilsException{
		try{
			this.cacheAdmin = new JCSAdminBean();
			this.cacheName = name;
			this.cache = JCS.getInstance(name);
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
			return this.cache.getDefaultElementAttributes().getMaxLifeSeconds();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void setItemLifeTime(long itemLifeTimeCache) throws UtilsException {
		try{
			// E' necessario prendere l'oggetto e poi risettarlo a causa di un bug.
			IElementAttributes el = this.cache.getDefaultElementAttributes();
			el.setMaxLifeSeconds(itemLifeTimeCache);
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
		return this.getCacheRegionInfo().getCache().getSize();
	}
	
	public List<String> keys() throws UtilsException {
		try{
			
			List<String> keys = new ArrayList<String>();
			CacheRegionInfo record = this.getCacheRegionInfo();
			Object[] keysObject = record.getCache().getMemoryCache().getKeyArray();
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
		this.printStats(this.getCacheRegionInfo(),out,separator,true);
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
			List<CacheRegionInfo> list = this.listCacheRegionInfo();
		    Iterator<CacheRegionInfo> it = list.iterator();
		    while (it.hasNext()) {
		    	CacheRegionInfo record = it.next();
		    	this.printStats(record,out,separatorStat,false);
		    	out.write(separatorCache.getBytes());
		    }		    
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	// PRIVATE
	private boolean errorOccursCountingBytes = false;
    private int getByteCount( CompositeCache cache )
            throws Exception {
    	
    	this.errorOccursCountingBytes = false;
    	
        IMemoryCache memCache = cache.getMemoryCache();

        CountingOnlyOutputStream counter = new CountingOnlyOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( counter );

        // non serializable objects will cause problems here
        // stop at the first non serializable exception.
        try
        {
        	// OLD Iteration:
        	/*
        	Iterator<?> iter = memCache.getIterator();
            while ( iter.hasNext() )
            {
                ICacheElement ce = (ICacheElement) ( (java.util.Map.Entry) iter.next() ).getValue();

                out.writeObject( ce.getVal() );
            }
            */
        	
        	// Fixed Iteration:
        	Object[] keys = memCache.getKeyArray();
        	if(keys==null){
        		return 0;
        	}
        	else{
        		for (int i = 0; i < keys.length; i++) {
        			ICacheElement ce = cache.get((String)keys[i]);
        			//System.out.println("Serialize["+keys[i]+"] ...");
        			if(ce!=null){
	        			Serializable object = ce.getVal();
	        			if(object!=null){
	        				out.writeObject( object );
	        				//System.out.println("Serialize["+ce.getKey()+"] ok");
	        			}else{
	        				//System.err.println("Serialize["+keys[i]+"] ok-null");
	        				this.errorOccursCountingBytes = true;
	        			}
        			}else{
        				//System.err.println("Serialize["+keys[i]+"] ok-ce-null");
        				this.errorOccursCountingBytes = true;
        			}
				}
        	}
        	
        }
        catch ( Exception e )
        {
            System.err.println( "Problem getting byte count (Modified by OpenSPCoop).  Likley cause is a non serilizable object." + e.getMessage() );
            e.printStackTrace();
            
        }finally{
        	out.flush();
        	out.close();
        	counter.flush();
        	counter.close();
        }

        // 4 bytes lost for the serialization header
        int count = counter.getCount();
        if(count<4){
        	return 0;
        }
        return  count - 4;
    }

	
	private void printStats(CacheRegionInfo region, OutputStream out, String separator, boolean thisCache) throws UtilsException {
		
		try{
		
			StringBuffer bf = new StringBuffer();
			
			//bf.append(Utilities.convertBytesToFormatString(region.getByteCount()));
			// NOTA: e' stato re-implementato il metodo poiche' casualmente avveniva un errore del tipo:
			//		 Problem getting byte count.  Likley cause is a non serilizable object.null
			//		 Il problema derivava dall'implementazione del metodo all'interno della classe org/apache/jcs/admin/JCSAdminBean
			//		 Viene utilizzato l'iterator dentro una struttura dinamica che cambia.
			//		 A volte, quando poi veniva registrato l'errore soprastante, avveniva questo errore (scoperto aggiungendo stampe nelle classi di JCS)
			//		 java.util.ConcurrentModificationException
			//		 	at java.util.Hashtable$Enumerator.next(Hashtable.java:1031)
			//			at org.apache.jcs.engine.memory.lru.LRUMemoryCache$IteratorWrapper.next(LRUMemoryCache.java:428)
			//			at org.apache.jcs.admin.JCSAdminBean.getByteCount(JCSAdminBean.java:95)
			int tentativi = 0;
			int sizeAttuale = -1;
			while (tentativi<10) {
				sizeAttuale = this.getByteCount(region.getCache());
				if(this.errorOccursCountingBytes==false){
					break;
				}
				if(thisCache){
					//System.err.println("PROVO ALTRO TENTATIVO");
					tentativi++;
					region = this.getCacheRegionInfo();
				}
				else{
					break;
				}
			}
				
			
			bf.append("Nome:");
			bf.append(region.getCache().getCacheName());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("Stato:");
			bf.append(region.getStatus());
			bf.append(" ");
			
			bf.append(separator);
			
			if(region.getCache().getCacheAttributes()!=null){
			
				bf.append("Algoritmo:");
				String cacheAlgoName = region.getCache().getCacheAttributes().getCacheName();
				CacheAlgorithm cacheEnum = CacheAlgorithm.toEnum(cacheAlgoName);
				if(cacheEnum!=null){
					bf.append(cacheEnum.name());
				}else{
					bf.append(cacheAlgoName);
				}
				bf.append(" ");
				
				bf.append(separator);
				
				bf.append("Dimensione:");
				bf.append(region.getCache().getCacheAttributes().getMaxObjects());
				bf.append(" ");
				
				bf.append(separator);
				
			}
			
			bf.append("ElementiInCache:");
			bf.append(region.getCache().getMemoryCache().getSize());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("MemoriaOccupata:");
			if(this.errorOccursCountingBytes){
				bf.append("[WARN !Error occurs counting bytes, re-try!]");
			}
			bf.append(Utilities.convertBytesToFormatString(sizeAttuale));
			bf.append(" ");
			
			bf.append(separator);
			
			if(region.getCache().getElementAttributes()!=null){
			
				bf.append("IdleTime:");
				long idleTime = region.getCache().getElementAttributes().getIdleTime();
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
				long lifeTime = region.getCache().getElementAttributes().getMaxLifeSeconds();
				if(lifeTime>0){
					bf.append(Utilities.convertSystemTimeIntoString_millisecondi(lifeTime*1000,false));
				}
				else if(lifeTime==0){
					bf.append("0");
				}
				else if(lifeTime<0){
					bf.append("Infinito");
				}
				bf.append(" ");
				
				bf.append(separator);
			
			}
			
			bf.append("PutCount:");
			bf.append(region.getCache().getUpdateCount());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("HitCount(Aux):");
			bf.append(region.getCache().getHitCountAux());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("HitCount(Ram):");
			bf.append(region.getCache().getHitCountRam());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("MissCount(Expired):");
			bf.append(region.getCache().getMissCountExpired());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("MissCount(NotFound):");
			bf.append(region.getCache().getMissCountNotFound());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("RemoveCount:");
			bf.append(region.getCache().getRemoveCount());
			bf.append(" ");
			
			out.write(bf.toString().getBytes());
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	private CacheRegionInfo getCacheRegionInfo() throws UtilsException{
		try{
			List<CacheRegionInfo> list = this.listCacheRegionInfo();
		    Iterator<CacheRegionInfo> it = list.iterator();
		
		    while (it.hasNext()) {
		
		        CacheRegionInfo record = it.next();
		        if(this.cacheName.equals(record.getCache().getCacheName())){
		        	return record;
		        }
		    }
		    
		    throw new Exception("Not found");
		    
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<CacheRegionInfo> listCacheRegionInfo() throws UtilsException{
		try{
			return this.cacheAdmin.buildCacheInfo();
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
		// org/apache/jcs/engine/control/CompositeCache.java
		//		if ( cacheElement.getKey() instanceof String
		//	            && cacheElement.getKey().toString().endsWith( CacheConstants.NAME_COMPONENT_DELIMITER ) )
		//	        {
		//	            throw new IllegalArgumentException( "key must not end with " + CacheConstants.NAME_COMPONENT_DELIMITER
		//	                + " for a put operation" );
		//	        }
		//
		// Dove in org.apache.jcs.engine.CacheConstants
		// 		public final static String NAME_COMPONENT_DELIMITER = ":";
		StringBuffer bf = new StringBuffer(key);
		if(bf.toString().endsWith(org.apache.jcs.engine.CacheConstants.NAME_COMPONENT_DELIMITER)){
			bf.append("_");
		}
		return bf.toString();
	}
	
	
	
	
	@Override
	public String toString(){
		
		StringBuffer bf = new StringBuffer();
		
		try {
			bf.append("CACHE SIZE["+this.getCacheSize()
					+"] ITEM_COUNT["+this.getItemCount()+"] ITEM_IDLE_TIME["+this.getItemIdleTime()+"] ITEM_IDLE_LIFE["+this.getItemLifeTime()
					+"] CACHE_ALGO["+this.getCacheAlgoritm()+"] STATS{"+this.printStats("")+"}");
			return bf.toString();
		} catch (UtilsException e) {
			return "NonDisponibile";
		}
	}
	public String printKeys(String separator) throws UtilsException{
		StringBuffer bf = new StringBuffer();
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
