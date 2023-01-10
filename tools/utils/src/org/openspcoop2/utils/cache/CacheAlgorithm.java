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

/**
 * CacheAlgorithm
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum CacheAlgorithm {

	LRU(org.apache.commons.jcs3.engine.memory.lru.LRUMemoryCache.class.getName()),
	MRU(org.apache.commons.jcs3.engine.memory.mru.MRUMemoryCache.class.getName());
	
	private String algorithm;
	
	CacheAlgorithm(String alg){
		this.algorithm = alg;
	}
	
	public String getAlgorithm() {
		return this.algorithm;
	}
	
	public static CacheAlgorithm toEnum(String value){
		if(LRU.getAlgorithm().equals(value)){
			return LRU;
		}
		else if(MRU.getAlgorithm().equals(value)){
			return MRU;
		}
		else{
			return null;
		}
	}
	
}
