package org.openspcoop2.pdd.timers;

import org.openspcoop2.core.controllo_traffico.driver.PolicyShutdownException;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.GestorePolicyAttiveInMemory;
import org.openspcoop2.utils.threads.BaseThread;

public class TimerClusteredRateLimitingLocalCache  extends BaseThread{
	
	private final GestorePolicyAttiveInMemory gestorePolicy;
	
	public TimerClusteredRateLimitingLocalCache(GestorePolicyAttiveInMemory gestorePolicy) {
		this.gestorePolicy = gestorePolicy;
	}
	
	@Override
	protected void process() {
		try {
			System.out.println("TIMER DI AGGIORNAMENTO LOCAL CACHE MAP");
			this.gestorePolicy.updateLocalCacheMap();
		} catch (PolicyShutdownException e) {
			this.setStop(true);
		}
		
	}


}

