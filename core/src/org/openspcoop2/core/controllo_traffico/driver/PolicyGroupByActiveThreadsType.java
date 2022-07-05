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
package org.openspcoop2.core.controllo_traffico.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;

/**
 * PolicyGroupByActiveThreadsInMemoryEnum 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum PolicyGroupByActiveThreadsType {

	LOCAL, 
	LOCAL_DIVIDED_BY_NODES, 
	DATABASE,
	// hazelcast counters
	HAZELCAST_ATOMIC_LONG,
	HAZELCAST_ATOMIC_LONG_ASYNC,
	HAZELCAST_PNCOUNTER,
	// hazelcast map
	HAZELCAST_MAP,
	HAZELCAST_NEAR_CACHE,
	HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP,
	HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP,
	HAZELCAST_LOCAL_CACHE,
	HAZELCAST_REPLICATED_MAP,
	// redis counters
	REDISSON_ATOMIC_LONG,
	REDISSON_LONGADDER,
	// redis map
	REDISSON_MAP;
	
	private static List<PolicyGroupByActiveThreadsType> hazelcastMapTypes = List.of( 
			HAZELCAST_MAP, 
			HAZELCAST_NEAR_CACHE,
			HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP,
			HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP, 
			HAZELCAST_LOCAL_CACHE,
			HAZELCAST_REPLICATED_MAP );
	private static List<PolicyGroupByActiveThreadsType> hazelcastCountersTypes = List.of( 
			HAZELCAST_PNCOUNTER, 
			HAZELCAST_ATOMIC_LONG, 
			HAZELCAST_ATOMIC_LONG_ASYNC );
	private static List<PolicyGroupByActiveThreadsType> hazelcastTypes = new ArrayList<PolicyGroupByActiveThreadsType>();
	static {
		hazelcastTypes.addAll(hazelcastMapTypes);
		hazelcastTypes.addAll(hazelcastCountersTypes);
	}
	
	private static List<PolicyGroupByActiveThreadsType> redisMapTypes = List.of( 
			REDISSON_MAP );
	private static List<PolicyGroupByActiveThreadsType> redisCountersTypes = List.of( 
			REDISSON_ATOMIC_LONG,
			REDISSON_LONGADDER );
	private static List<PolicyGroupByActiveThreadsType> redisTypes = new ArrayList<PolicyGroupByActiveThreadsType>();
	static {
		redisTypes.addAll(redisMapTypes);
		redisTypes.addAll(redisCountersTypes);
	}
	

	private static Set<PolicyGroupByActiveThreadsType> exactModes = Set.of(
			PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG,
			PolicyGroupByActiveThreadsType.REDISSON_ATOMIC_LONG,
			PolicyGroupByActiveThreadsType.HAZELCAST_MAP,
			PolicyGroupByActiveThreadsType.REDISSON_MAP
		);
	
	
	private static Set<PolicyGroupByActiveThreadsType> approxModes = Set.of(
			PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER,
			PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG_ASYNC,
			PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE,
			PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE
		);
	
	private static Set<PolicyGroupByActiveThreadsType> inconsistentModes = Set.of(
			PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP,
			PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP,
			PolicyGroupByActiveThreadsType.HAZELCAST_REPLICATED_MAP,
			/*
			 * Si potrebbe pensare che debba finire in approxModes come per il PNCounter 
			 * Non è così poichè per il PNCounter abbiamo le versioni incrementAndGet e addAndGet e in questo caso invece no, si fa prima l'increment e poi si ottiene il valore tramite sum(). 
			 * Una volta che lo si recupera tramite la 'sum()' potrebbero essere tornati anche risultati derivanti da altre richieste in corso.
			 **/
			PolicyGroupByActiveThreadsType.REDISSON_LONGADDER
		);
			
		
	

	
	
	public boolean isHazelcast() {
		return hazelcastTypes.contains(this);
	}
	public boolean isHazelcastMap() {
		return hazelcastMapTypes.contains(this);
	}
	public boolean isHazelcastCounters() {
		return hazelcastCountersTypes.contains(this);
	}
	
	public boolean isRedis() {
		return redisTypes.contains(this);
	}
	public boolean isRedisMap() {
		return redisMapTypes.contains(this);
	}
	public boolean isRedisCounters() {
		return redisCountersTypes.contains(this);
	}
	
	public boolean isExact() {
		return exactModes.contains(this);
	}
	public boolean isApproximated() {
		return approxModes.contains(this);
	}
	public boolean isInconsistent() {
		return inconsistentModes.contains(this);
	}
	
	public String toLabel() {
		switch (this) {
		case LOCAL:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE;
		case LOCAL_DIVIDED_BY_NODES:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE_SUDDIVISA_TRA_NODI;
		case DATABASE:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_DATABASE;
		case HAZELCAST_MAP:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_HAZELCAST +
					" ("+CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_HAZELCAST_FULL_SYNC+")";
		case HAZELCAST_NEAR_CACHE:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_HAZELCAST +
					" ("+CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_HAZELCAST_NEAR_CACHE+")";
		case HAZELCAST_LOCAL_CACHE:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_HAZELCAST +
					" ("+CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_HAZELCAST_LOCAL_CACHE+")";
		case HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_HAZELCAST +
					" ("+CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_SYNC+")";
		case HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_HAZELCAST +
					" ("+CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_ASYNC+")";
		case HAZELCAST_PNCOUNTER:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_HAZELCAST +
					" ("+CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_PNCOUNTER+")";
		case HAZELCAST_ATOMIC_LONG:
				return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_HAZELCAST +
						" ("+CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_ATOMIC_LONG+")";
		case HAZELCAST_ATOMIC_LONG_ASYNC:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_HAZELCAST +
					" ("+CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_ATOMIC_LONG_ASYNC+")";
		case HAZELCAST_REPLICATED_MAP:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_HAZELCAST +
					" ("+CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REPLICATED_MAP+")";
		case REDISSON_ATOMIC_LONG:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_REDIS  +
					" ("+CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_REDIS_CONTATORI_ATOMIC_LONG+")";
		case REDISSON_LONGADDER:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_REDIS +
				" ("+CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_REDIS_CONTATORI_LONGADDER+")"; 
		case REDISSON_MAP:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_REDIS +
					" ("+CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_REDIS_REDDISSON+")";
		}
		return null;
	}
	
	public List<TipoRisorsaPolicyAttiva> getSupportedResources(){
		List<TipoRisorsaPolicyAttiva> list = new ArrayList<TipoRisorsaPolicyAttiva>();
		for (TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva : TipoRisorsaPolicyAttiva.values()) {
			list.add(tipoRisorsaPolicyAttiva);
		}
		switch (this) {
		case LOCAL:
			return list;
		case LOCAL_DIVIDED_BY_NODES:
			list.remove(TipoRisorsaPolicyAttiva.OCCUPAZIONE_BANDA);
			list.remove(TipoRisorsaPolicyAttiva.TEMPO_COMPLESSIVO_RISPOSTA);
			list.remove(TipoRisorsaPolicyAttiva.TEMPO_MEDIO_RISPOSTA);
			return list;
		case DATABASE:
			return list;
		case HAZELCAST_MAP:
		case HAZELCAST_PNCOUNTER:
		case HAZELCAST_ATOMIC_LONG:
		case HAZELCAST_ATOMIC_LONG_ASYNC:
		case REDISSON_ATOMIC_LONG:
			return list;
		case REDISSON_LONGADDER:
			list.remove(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE);
			return list;
		case HAZELCAST_NEAR_CACHE:
			list.remove(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE);
			return list;
		case HAZELCAST_LOCAL_CACHE:
			return list;
		case HAZELCAST_REPLICATED_MAP:
			list.remove(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE);
			return list;
		case HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP:
		case HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP:
			list.remove(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE);
			return list;
		case REDISSON_MAP:
			return list;
		}
		return null;
	}
	
	
	public boolean isSupportedResource(TipoRisorsaPolicyAttiva resource) {
		return this.getSupportedResources().contains(resource);
	}
}
