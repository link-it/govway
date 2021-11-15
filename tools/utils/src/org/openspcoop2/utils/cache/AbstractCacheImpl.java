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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.commons.jcs3.admin.CountingOnlyOutputStream;
import org.apache.commons.jcs3.engine.CacheStatus;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;

/**
 * AbstractCacheImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractCacheImpl implements ICacheImpl {

	protected String cacheName = null;
	protected CacheType cacheType = null;
	
	public AbstractCacheImpl() {
		
	}
	public AbstractCacheImpl(CacheType cacheType, String cacheName) {
		this.cacheType = cacheType;
		this.cacheName = cacheName;
	}
	
	@Override
	public CacheType getType() {
		return this.cacheType;
	}
	@Override
	public String getName() {
		return this.cacheName;
	}
	
	@Override
	public boolean isEternal() throws UtilsException{
		return true;
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
	@Override
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
	
	@Override
	public String printStats(String separator) throws UtilsException{
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
	
	protected String _printStats(String separator, boolean thisCache) throws UtilsException {
		
		try{
		
			int tentativi = 0;
			long sizeAttuale = -1;
			while (tentativi<10) {
				sizeAttuale = this.getByteCount();
				if(this.errorOccursCountingBytes==false){
					break;
				}
				if(thisCache){
					//System.err.println("PROVO ALTRO TENTATIVO");
					tentativi++;
				}
				else{
					break;
				}
			}
			
			
			StringBuilder bf = new StringBuilder();
						
			bf.append("Nome:");
			bf.append(this.cacheName);
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("Tipo:");
			bf.append(this.cacheType);
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("Stato:");
			bf.append(CacheStatus.ALIVE); // uso terminologia JCS
			bf.append(" ");
			
			bf.append(separator);
			
			// Dalla versione 3.0 non è più presente la gestione del synchronized
//			bf.append("GetSyncDisabled:");
//			bf.append(cache.isSyncDisabled());
//			bf.append(" ");
//			
//			bf.append(separator);
			
			bf.append("Algoritmo:");
			CacheAlgorithm cacheEnum = this.getCacheAlgoritm();
			if(cacheEnum!=null){
				bf.append(cacheEnum.name());
			}else{
				bf.append("-");
			}
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("Dimensione:");
			bf.append(this.getCacheSize());
			bf.append(" ");
			
			bf.append(separator);
			
			
			bf.append("ElementiInCache:");
			bf.append(this.getItemCount());
			bf.append(" ");
			
			bf.append(separator);
			
			bf.append("MemoriaOccupata:");
			bf.append(Utilities.convertBytesToFormatString(sizeAttuale));
			bf.append(" ");
			
			bf.append(separator);
			
			
			bf.append("IdleTime:");
			long idleTime = this.getItemIdleTime();
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
			long lifeTime = this.getItemLifeTime();
			if(lifeTime>0){
				bf.append(Utilities.convertSystemTimeIntoString_millisecondi(lifeTime*1000,false));
			}
			else if(lifeTime==0){
				bf.append("0");
			}
			else if(lifeTime<0){
				if(this.isEternal()) {
					bf.append("Infinito");
				}
				else {
					bf.append("Infinito (NoEternal?)");
				}
			}
			bf.append(" ");
			
			bf.append(separator);
			
			return bf.toString();
			
		}catch(Throwable e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

	protected void _printStats(OutputStream out, String separator, boolean thisCache) throws UtilsException {
		try{
			out.write(_printStats(separator, thisCache).getBytes());
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	private boolean errorOccursCountingBytes_debug = false;
	private boolean errorOccursCountingBytes = false;
	protected long getByteCount() {
    	
    	this.errorOccursCountingBytes = false;
    	long size = 0;
    	try {
    		for (String key : this.keys())
    		{
    			
    			Object element = null;
    			try
    			{
    				element = this.get(key);
    			}
    			catch (Throwable e)
    			{
    				if(this.errorOccursCountingBytes_debug) {
    					System.err.println("["+this.cacheName+"] Element cache get");
    					e.printStackTrace(System.err);
    				}
    				this.errorOccursCountingBytes = true;
    				continue;
    				//throw new RuntimeException("IOException while trying to get a cached element", e);
    			}

    			if (element == null)
    			{
    				continue;
    			}

				//CountingOnlyOutputStream: Keeps track of the number of bytes written to it, but doesn't write them anywhere.
				CountingOnlyOutputStream counter = new CountingOnlyOutputStream();
				try (ObjectOutputStream out = new ObjectOutputStream(counter);)
				{
					out.writeObject(element);
				}
				catch (Throwable e)
				{
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
    	catch ( Exception e )
    	{
    		System.err.println( "Problem getting byte count (Modified by GovWay).  Likley cause is a non serilizable object." + e.getMessage() );
    		e.printStackTrace();   
    	}
    	
    	return size;
    }
}
