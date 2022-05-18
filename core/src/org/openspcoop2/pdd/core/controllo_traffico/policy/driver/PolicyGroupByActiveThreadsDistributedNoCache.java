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

import java.util.Map;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupBy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreadsInMemory;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

/**
 * 
 * Gestore contatori policy che lavora solo sulla copia distribuita.
 * 
 * @author Francesco Scarlato
 *
 */
public class PolicyGroupByActiveThreadsDistributedNoCache implements IPolicyGroupByActiveThreadsInMemory {
		
	private final HazelcastInstance hazelcast;
	private final IMap<IDUnivocoGroupByPolicy, DatiCollezionati> distributedMap;
	private final StartRequestProcessor startRequestProcessor;
	private final UpdateDatiRequestProcessor updateDatiRequestProcessor;
	
	private final ActivePolicy activePolicy;

	
	public PolicyGroupByActiveThreadsDistributedNoCache(ActivePolicy policy, String uniqueIdMap, HazelcastInstance hazelcast) {
		 this.activePolicy = policy;
		 
		 this.hazelcast = hazelcast;
		 this.distributedMap = this.hazelcast.getMap(uniqueIdMap + "rate-limiting");
		 this.startRequestProcessor = new StartRequestProcessor(policy);
		 this.updateDatiRequestProcessor = new UpdateDatiRequestProcessor(policy);
	}

	
	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy)
			throws PolicyException {
		return this.distributedMap.executeOnKey(datiGroupBy, this.startRequestProcessor);			
	}
	
	
	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione,
			IDUnivocoGroupByPolicy datiGroupBy) throws PolicyException, PolicyNotFoundException {
		
		DatiCollezionati datiCollezionati = this.distributedMap.executeOnKey(datiGroupBy, this.updateDatiRequestProcessor);
		if (datiCollezionati == null) {
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
		}
		
		return datiCollezionati;
	}
	
	
	@Override
	public void registerStopRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata)
			throws PolicyException, PolicyNotFoundException {
		// Lavoro sulla copia remota
		this.distributedMap.executeOnKey(datiGroupBy, new EndRequestProcessor(this.activePolicy, dati, isApplicabile, isViolata));			
	}


	@Override
	public ActivePolicy getActivePolicy() {
		return this.activePolicy;
	}


	@Override
	public Map<IDUnivocoGroupByPolicy, DatiCollezionati> getMapActiveThreads() {
		return this.distributedMap;
	}


	@Override
	public long getActiveThreads() {
		return this.getActiveThreads(null);
	}


	@Override
	public long getActiveThreads(IDUnivocoGroupByPolicy filtro) {

		long counter = 0;
		
		for (var entry : this.distributedMap) {
			if(filtro!=null){
				IDUnivocoGroupBy<IDUnivocoGroupByPolicy> idAstype = (IDUnivocoGroupBy<IDUnivocoGroupByPolicy>) entry.getKey();
				if(!idAstype.match(filtro)){
					continue;
				}
				counter += entry.getValue().getActiveRequestCounter();
			}
		}
		
		return counter;
	}


	@Override
	public void resetCounters() {
		this.distributedMap.executeOnEntries(new ResetCountersProcessor());
	}


	@Override
	public String printInfos(Logger log, String separatorGroups) throws UtilsException {
		StringBuilder bf = new StringBuilder();

		for (var entry : this.distributedMap) {
			IDUnivocoGroupByPolicy datiGroupBy = entry.getKey();
			bf.append(separatorGroups);
			bf.append("\n");
			bf.append("Criterio di Collezionamento dei Dati\n");
			bf.append(datiGroupBy.toString(true));
			bf.append("\n");
			entry.getValue().checkDate(log, this.activePolicy); // imposta correttamente gli intervalli
			bf.append(entry.getValue().toString());
			bf.append("\n");
		}

		if(bf.length()<=0){
			bf.append("Nessuna informazione disponibile");
			return bf.toString();
		}
		else{
			return bf.toString()+separatorGroups;
		}
		
	}
		
}
