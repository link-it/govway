/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import org.openspcoop2.utils.UtilsException;

/**
 * ICacheImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ICacheImpl {

	public CacheType getType();
	public String getName();
	
	//  *** Inizializzazione ***
	
	public int getCacheSize();
	public void setCacheSize(int cacheSize);
	
	public CacheAlgorithm getCacheAlgoritm();
	public void setCacheAlgoritm(CacheAlgorithm cacheAlgoritm);
	
	public long getItemIdleTime() throws UtilsException;
	public void setItemIdleTime(long itemIdleTimeCache) throws UtilsException;

	public long getItemLifeTime() throws UtilsException;
	public boolean isEternal() throws UtilsException;
	public void setItemLifeTime(long itemLifeTimeCache) throws UtilsException;
	
	public void build() throws UtilsException;
	
	
	//  *** Gestione ***
	
	public void clear() throws UtilsException;
	
	public Object get(String key);
	
	public void remove(String key) throws UtilsException;
	
	public void put(String key,org.openspcoop2.utils.cache.CacheResponse value) throws UtilsException;
	public void put(String key,Serializable value) throws UtilsException;
	
	public int getItemCount()  throws UtilsException;
	
	public List<String> keys() throws UtilsException;
	
	
	//  *** Info ***
	
	public String printStats(String separator) throws UtilsException;
	
	public void printStats(OutputStream out, String separator) throws UtilsException;
	
	public String printKeys(String separator) throws UtilsException;
		
	
	//  *** Sincronizzazione ***
	
	@Deprecated
	public default void disableSyncronizedGet() throws UtilsException{
	}
	@Deprecated
	public default boolean isDisableSyncronizedGet() throws UtilsException{
		return false;
	}
	@Deprecated
	public default void enableDebugSystemOut() throws UtilsException {
	}
	@Deprecated
	public default boolean isEnableDebugSystemOut() throws UtilsException {
		return false;
	}
}
