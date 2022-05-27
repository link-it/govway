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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

/**
 * PolicyGroupByActiveThreadsInMemoryEnum 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum PolicyGroupByActiveThreadsInMemoryEnum {

	LOCAL, 
	DATABASE,
	HAZELCAST,
	HAZELCAST_NEAR_CACHE,
	HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP,
	HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP,
	HAZELCAST_LOCAL_CACHE,
	REDIS;
	
	public boolean isHazelcast() {
		return PolicyGroupByActiveThreadsInMemoryEnum.HAZELCAST.equals(this) ||
				PolicyGroupByActiveThreadsInMemoryEnum.HAZELCAST_NEAR_CACHE.equals(this) ||
				PolicyGroupByActiveThreadsInMemoryEnum.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP.equals(this) ||
				PolicyGroupByActiveThreadsInMemoryEnum.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP.equals(this) ||
				PolicyGroupByActiveThreadsInMemoryEnum.HAZELCAST_LOCAL_CACHE.equals(this);
	}
	
}
