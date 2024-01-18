/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import java.util.Date;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyDateUtils;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Map;
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

	public AbstractPolicyGroupByActiveThreadsDistributedNearCacheWithoutEntryProcessor(ActivePolicy policy, String uniqueIdMap, HazelcastInstance hazelcast, PolicyGroupByActiveThreadsType type) throws PolicyException {
		super(policy, uniqueIdMap, type, hazelcast);
		this.putAsync = PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP.equals(type);
	}

	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx)
			throws PolicyException {
		
		datiGroupBy = augmentIDUnivoco(datiGroupBy);
		
		DatiCollezionati datiCollezionati = this.distributedMap.get(datiGroupBy);
		boolean newDati = false;
		if (datiCollezionati == null) {
			Date gestorePolicyConfigDate = PolicyDateUtils.readGestorePolicyConfigDateIntoContext(ctx);
			datiCollezionati = new DatiCollezionati(this.activePolicy.getInstanceConfiguration().getUpdateTime(), gestorePolicyConfigDate);
			newDati = true;
		}
		else {
			if(datiCollezionati.getUpdatePolicyDate()!=null) {
				if(!datiCollezionati.getUpdatePolicyDate().equals(this.activePolicy.getInstanceConfiguration().getUpdateTime())) {
					// data aggiornata
					datiCollezionati.resetCounters(this.activePolicy.getInstanceConfiguration().getUpdateTime());
				}
			}
		}
		DatiCollezionati datiCollezionatiPerPolicyVerifier = (DatiCollezionati) datiCollezionati.newInstance(); // i valori utilizzati dal policy verifier verranno impostati con il valore impostato nell'operazione chiamata
		if(newDati) {
			datiCollezionatiPerPolicyVerifier.initDatiIniziali(this.activePolicy);
			datiCollezionatiPerPolicyVerifier.checkDate(log, this.activePolicy); // inizializza le date se ci sono
		}
		
		datiCollezionati.registerStartRequest(log, this.activePolicy, ctx, datiCollezionatiPerPolicyVerifier);
		
		if(this.putAsync) {
			this.distributedMap.putAsync(datiGroupBy, datiCollezionati);
		}
		else {
			this.distributedMap.put(datiGroupBy, datiCollezionati);
		}
		
		return datiCollezionatiPerPolicyVerifier;
	}
	
	
	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione,
			IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx) throws PolicyException, PolicyNotFoundException {
		
		datiGroupBy = augmentIDUnivoco(datiGroupBy);

		DatiCollezionati datiCollezionati = this.distributedMap.get(datiGroupBy);			
		if(datiCollezionati == null) {
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
		} else {
			DatiCollezionati datiCollezionatiPerPolicyVerifier = (DatiCollezionati) datiCollezionati.newInstance(); // i valori utilizzati dal policy verifier verranno impostati con il valore impostato nell'operazione chiamata
			
			boolean updated = datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy, ctx, datiCollezionatiPerPolicyVerifier);	
			if(updated) {
				if(this.putAsync) {
					this.distributedMap.putAsync(datiGroupBy, datiCollezionati);
				}
				else {
					this.distributedMap.put(datiGroupBy, datiCollezionati);
				}
				return datiCollezionatiPerPolicyVerifier;
			}
			
			return null;
		}
	}
	
	@Override
	public void registerStopRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx, 
			MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata)
			throws PolicyException, PolicyNotFoundException {
		
		datiGroupBy = augmentIDUnivoco(datiGroupBy);

		DatiCollezionati datiCollezionati = this.distributedMap.get(datiGroupBy);			
		if(datiCollezionati == null) {
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
		} else {
			//System.out.println("<"+idTransazione+">registerStopRequest registerEndRequest ...");
			datiCollezionati.registerEndRequest(log, this.activePolicy, ctx, dati);
			//System.out.println("<"+idTransazione+">registerStopRequest registerEndRequest ok");
			if(isApplicabile){
				//System.out.println("<"+idTransazione+">registerStopRequest updateDatiEndRequestApplicabile ...");
				List<Integer> esitiCodeOk = null;
				List<Integer> esitiCodeKo_senzaFaultApplicativo = null;
				List<Integer> esitiCodeFaultApplicativo = null;
				try {
					EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(log,dati.getProtocollo());
					esitiCodeOk = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
					esitiCodeKo_senzaFaultApplicativo = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo();
					esitiCodeFaultApplicativo = esitiProperties.getEsitiCodeFaultApplicativo();
				}catch(Exception e) {
					throw new PolicyException(e.getMessage(),e);
				}
				datiCollezionati.updateDatiEndRequestApplicabile(log, this.activePolicy, ctx, dati,
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
	public void initMap(java.util.Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) {
		if(map!=null && map.size()>0){
			this.distributedMap.putAll(map);
		}
	}
	
}
