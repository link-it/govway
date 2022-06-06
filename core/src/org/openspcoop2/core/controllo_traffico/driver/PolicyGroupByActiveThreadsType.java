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
	HAZELCAST,
	HAZELCAST_NEAR_CACHE,
	HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP,
	HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP,
	HAZELCAST_LOCAL_CACHE,
	REDISSON;
	
	public boolean isHazelcast() {
		return PolicyGroupByActiveThreadsType.HAZELCAST.equals(this) ||
				PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE.equals(this) ||
				PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP.equals(this) ||
				PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP.equals(this) ||
				PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE.equals(this);
	}
	
	public String toLabel() {
		switch (this) {
		case LOCAL:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE;
		case LOCAL_DIVIDED_BY_NODES:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE_SUDDIVISA_TRA_NODI;
		case DATABASE:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_DATABASE;
		case HAZELCAST:
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
		case REDISSON:
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
		case HAZELCAST:
			return list;
		case HAZELCAST_NEAR_CACHE:
			list.remove(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE);
			return list;
		case HAZELCAST_LOCAL_CACHE:
			return list;
		case HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP:
		case HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP:
			list.remove(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE);
			return list;
		case REDISSON:
			return list;
		}
		return null;
	}
	public boolean isSupportedResource(TipoRisorsaPolicyAttiva resource) {
		return this.getSupportedResources().contains(resource);
	}
}
