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


package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.PolicyGroupByActiveThreadsInMemoryEnum;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;

/**
 * Gestore che utilizza nearCache senza entry processor con put asincrone o sincrone
 * 
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractPolicyGroupByActiveThreadsDistributedNearCacheWithoutEntryProcessor extends AbstractPolicyGroupByActiveThreadsDistributed  {

	private boolean putAsync = false;

	public AbstractPolicyGroupByActiveThreadsDistributedNearCacheWithoutEntryProcessor(ActivePolicy policy, String uniqueIdMap, HazelcastInstance hazelcast, PolicyGroupByActiveThreadsInMemoryEnum type) throws PolicyException {
		super(policy, uniqueIdMap, type, hazelcast);
		this.putAsync = PolicyGroupByActiveThreadsInMemoryEnum.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP.equals(type);
	}

	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy)
			throws PolicyException {
		
		datiGroupBy = augmentIDUnivoco(datiGroupBy);
		
		DatiCollezionati datiCollezionati = this.distributedMap.get(datiGroupBy);
		if (datiCollezionati == null) {
			datiCollezionati = new DatiCollezionati(this.activePolicy.getInstanceConfiguration().getUpdateTime());
		}
		else {
			if(datiCollezionati.getUpdatePolicyDate()!=null) {
				if(!datiCollezionati.getUpdatePolicyDate().equals(this.activePolicy.getInstanceConfiguration().getUpdateTime())) {
					// data aggiornata
					datiCollezionati.resetCounters(this.activePolicy.getInstanceConfiguration().getUpdateTime());
				}
			}
		}
		
		datiCollezionati.registerStartRequest(log, this.activePolicy);
		
		if(this.putAsync) {
			this.distributedMap.putAsync(datiGroupBy, datiCollezionati);
		}
		else {
			this.distributedMap.put(datiGroupBy, datiCollezionati);
		}
		
		return datiCollezionati;
	}
	
	
	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione,
			IDUnivocoGroupByPolicy datiGroupBy) throws PolicyException, PolicyNotFoundException {
		
		datiGroupBy = augmentIDUnivoco(datiGroupBy);

		DatiCollezionati datiCollezionati = this.distributedMap.get(datiGroupBy);			
		if(datiCollezionati == null) {
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
		} else {
			boolean updated = datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy);	
			if(updated) {
				if(this.putAsync) {
					this.distributedMap.putAsync(datiGroupBy, datiCollezionati);
				}
				else {
					this.distributedMap.put(datiGroupBy, datiCollezionati);
				}
			}
			
			return datiCollezionati;
		}
	}
	
	@Override
	public void registerStopRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata)
			throws PolicyException, PolicyNotFoundException {
		
		datiGroupBy = augmentIDUnivoco(datiGroupBy);

		DatiCollezionati datiCollezionati = this.distributedMap.get(datiGroupBy);			
		if(datiCollezionati == null) {
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
		} else {
			//System.out.println("<"+idTransazione+">registerStopRequest registerEndRequest ...");
			datiCollezionati.registerEndRequest(log, this.activePolicy, dati);
			//System.out.println("<"+idTransazione+">registerStopRequest registerEndRequest ok");
			if(isApplicabile){
				//System.out.println("<"+idTransazione+">registerStopRequest updateDatiEndRequestApplicabile ...");
				List<Integer> esitiCodeOk = null;
				List<Integer> esitiCodeKo_senzaFaultApplicativo = null;
				List<Integer> esitiCodeFaultApplicativo = null;
				try {
					esitiCodeOk = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeOk_senzaFaultApplicativo();
					esitiCodeKo_senzaFaultApplicativo = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeKo_senzaFaultApplicativo();
					esitiCodeFaultApplicativo = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeFaultApplicativo();
				}catch(Exception e) {
					throw new PolicyException(e.getMessage(),e);
				}
				datiCollezionati.updateDatiEndRequestApplicabile(log, this.activePolicy, dati,
						esitiCodeOk,esitiCodeKo_senzaFaultApplicativo, esitiCodeFaultApplicativo, isViolata);
				//System.out.println("<"+idTransazione+">registerStopRequest updateDatiEndRequestApplicabile ok");
				if(this.putAsync) {
					this.distributedMap.putAsync(datiGroupBy, datiCollezionati);
				}
				else {
					this.distributedMap.put(datiGroupBy, datiCollezionati);
				}
			}
		}
	}
	
	@Override
	public void resetCounters() {
		
		if(this.distributedMap.size()>0){
			//		FIX: iterando nella maniera sottostante si ottiene il seguente errore se si usa la near-cache: key cannot be of type Data! hazelcast 
			//		for (var entry : this.distributedMap) {
			for (IDUnivocoGroupByPolicy datiGroupBy : this.distributedMap.keySet()) {
				DatiCollezionati datiCollezionati = this.distributedMap.get(datiGroupBy);
				datiCollezionati.resetCounters();
				this.distributedMap.put(datiGroupBy, datiCollezionati);
			}
		}			
	}
	
	@Override
	public void initMap(Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) {
		if(map!=null && map.size()>0){
			this.distributedMap.putAll(map);
		}
	}
	
}
