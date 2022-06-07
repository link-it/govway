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

/**     
 *  TimerClusteredRateLimitingLocalCache
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
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
			
			this.log.info("Sync RateLimiting policy counters ...");
			
			updateLocalCacheMap();
			
			this.log.info("Sync RateLimiting policy counters finished");
			
		} catch (PolicyException e) {
			this.log.error(e.getMessage(),e);
		} catch (PolicyShutdownException e) {
			this.setStop(true);
		}
		
	}

	private void updateLocalCacheMap() throws PolicyShutdownException, PolicyException {
		
		Set<Entry<String, IPolicyGroupByActiveThreadsInMemory>> activeThreadsPolicies = this.gestorePolicy.entrySet();
		
		
		for (var policy : activeThreadsPolicies) {
			
			this.log.debug("["+policy.getKey()+"] update ...");
			
			PolicyGroupByActiveThreadsDistributedLocalCache distributedPolicy = (PolicyGroupByActiveThreadsDistributedLocalCache) policy.getValue();
			
			Map<IDUnivocoGroupByPolicy, DatiCollezionati> mapActiveThreads = new HashMap<IDUnivocoGroupByPolicy, DatiCollezionati>();
			for (var entry : distributedPolicy.getDistributedMapActiveThreads().entrySet()) {
				mapActiveThreads.put(entry.getKey(), entry.getValue());
			}
			
			PolicyGroupByActiveThreads localPolicy = distributedPolicy.getLocalPolicy();
			localPolicy.setMapActiveThreads(mapActiveThreads);
			
			this.log.debug("["+policy.getKey()+"] update ok");
		}
		
	}
}

