package org.openspcoop2.pdd.timers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreadsInMemory;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyShutdownException;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.GestorePolicyAttiveInMemory;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.PolicyGroupByActiveThreads;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.PolicyGroupByActiveThreadsDistributedLocalCache;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;

public class TimerClusteredRateLimitingLocalCache extends BaseThread{
	
	private final GestorePolicyAttiveInMemory gestorePolicy;
	private Logger log = null;
	
	public TimerClusteredRateLimitingLocalCache(Logger log, GestorePolicyAttiveInMemory gestorePolicy) {
		this.log = log;
		this.gestorePolicy = gestorePolicy;
	}
	
	@Override
	protected void process() {
		try {
			//System.out.println("TIMER DI AGGIORNAMENTO LOCAL CACHE MAP");
			
			updateLocalCacheMap();
			
		} catch (PolicyException e) {
			this.log.error(e.getMessage(),e);
		} catch (PolicyShutdownException e) {
			this.setStop(true);
		}
		
	}

	private void updateLocalCacheMap() throws PolicyShutdownException, PolicyException {
		
		Set<Entry<String, IPolicyGroupByActiveThreadsInMemory>> activeThreadsPolicies = this.gestorePolicy.entrySet();
		
		
		for (var policy : activeThreadsPolicies) {
			PolicyGroupByActiveThreadsDistributedLocalCache distributedPolicy = (PolicyGroupByActiveThreadsDistributedLocalCache) policy.getValue();
			
			Map<IDUnivocoGroupByPolicy, DatiCollezionati> mapActiveThreads = new HashMap<IDUnivocoGroupByPolicy, DatiCollezionati>();
			for (var entry : distributedPolicy.getDistributedMapActiveThreads().entrySet()) {
				mapActiveThreads.put(entry.getKey(), entry.getValue());
			}
			
			PolicyGroupByActiveThreads localPolicy = distributedPolicy.getLocalPolicy();
			localPolicy.setMapActiveThreads(mapActiveThreads);
		}
		
	}
}

