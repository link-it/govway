package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.PolicyGroupByActiveThreads;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @author Francesco Scarlato
 * 
 * Questa funziona solo con oneMapPerGroup = true!
 *
 */
public class PolicyGroupByActiveThreadsDistributedLocalCache  extends PolicyGroupByActiveThreadsDistributedAbstract {

	private PolicyGroupByActiveThreads localPolicy;
	
	public PolicyGroupByActiveThreadsDistributedLocalCache(ActivePolicy policy, String uniqueIdMap,
			HazelcastInstance hazelcast) {
		super(policy, uniqueIdMap, hazelcast);
		
		this.localPolicy = new PolicyGroupByActiveThreads(policy);
	
	}
	

	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy)
			throws PolicyException {
		
		// Lavoro in asincrono sui contatori della copia remota
		this.distributedMap.submitToKey(datiGroupBy, this.startRequestProcessor);
		
		return this.localPolicy.registerStartRequest(log, idTransazione, datiGroupBy);
	}


	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione,
			IDUnivocoGroupByPolicy datiGroupBy) throws PolicyException, PolicyNotFoundException {
		
		// Lavoro sulla copia remota
		this.distributedMap.submitToKey(datiGroupBy, this.updateDatiRequestProcessor);
		
		return this.localPolicy.updateDatiStartRequestApplicabile(log, idTransazione, datiGroupBy);
	}

	
	@Override
	public void registerStopRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy,
			MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata)
			throws PolicyException, PolicyNotFoundException {
		
		// Lavoro sulla copia remota
		this.distributedMap.submitToKey(datiGroupBy, new EndRequestProcessor(this.activePolicy, dati, isApplicabile, isViolata));			
		
		this.localPolicy.registerStopRequest(log, idTransazione, datiGroupBy, dati, isApplicabile, isViolata);
	}


	public PolicyGroupByActiveThreads getLocalPolicy() {
		return this.localPolicy;
	}

}
