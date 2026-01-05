/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.HazelcastManager;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;

/**
 *  TimerHazelcastOrphanedProxiesCleanup
 *
 *  Timer per la pulizia dei proxy Hazelcast orfani (contatori di intervalli terminati da più di N ore).
 *  Pulisce solo i contatori con intervallo (-i-) che sono più vecchi della soglia configurata.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerHazelcastOrphanedProxiesCleanup extends BaseThread{

	private Logger log = null;

	public TimerHazelcastOrphanedProxiesCleanup(Logger log) {
		this.log = log;
	}

	@Override
	protected void process() {
		try {
			this.log.debug("Cleanup Hazelcast orphaned proxies ...");

			// Cleanup proxy Hazelcast orfani (contatori di intervalli terminati)
			Long thresholdMs = OpenSPCoop2Properties.getInstance().getControlloTrafficoGestorePolicyInMemoryHazelcastOrphanedProxyThresholdMs();
			int removedProxies = HazelcastManager.cleanupOrphanedProxies(thresholdMs);

			if(removedProxies > 0) {
				String msg = "Cleanup Hazelcast orphaned proxies finished: removed " + removedProxies + " proxies";
				this.log.info(msg);
			} else {
				this.log.debug("Cleanup Hazelcast orphaned proxies finished: no proxies removed");
			}

		} catch (Throwable e) {
			this.log.error("Errore durante cleanup Hazelcast orphaned proxies: " + e.getMessage(), e);
		}
	}
}
