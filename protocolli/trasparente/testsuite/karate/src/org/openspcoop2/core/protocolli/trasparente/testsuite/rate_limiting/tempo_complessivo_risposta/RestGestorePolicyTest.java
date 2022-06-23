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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.tempo_complessivo_risposta;

import org.junit.Test;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;

/**
* RestTestGestorePolicy
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestGestorePolicyTest extends ConfigLoader {

	private RestTest restTest;
	public RestGestorePolicyTest() {
		this.restTest = new RestTest();
		if(Utils.logRateLimiting==null) {
			Utils.logRateLimiting=ConfigLoader.getLoggerRateLimiting();
		}
	}
	
	
	@Test
	public void perMinutoErogazione_local() throws Exception {
		this.restTest.perMinutoErogazione(PolicyGroupByActiveThreadsType.LOCAL);
	}
	@Test
	public void perMinutoErogazione_local_divided_by_nodes() throws Exception {
		this.restTest.perMinutoErogazione(PolicyGroupByActiveThreadsType.LOCAL_DIVIDED_BY_NODES);
	}
	@Test
	public void perMinutoErogazione_database() throws Exception {
		this.restTest.perMinutoErogazione(PolicyGroupByActiveThreadsType.DATABASE);
	}
	@Test
	public void perMinutoErogazione_hazelcast() throws Exception {
		this.restTest.perMinutoErogazione(PolicyGroupByActiveThreadsType.HAZELCAST);
	}
	@Test
	public void perMinutoErogazione_hazelcast_nearCache() throws Exception {
		this.restTest.perMinutoErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE);
	}
	@Test
	public void perMinutoErogazione_hazelcast_localCache() throws Exception {
		this.restTest.perMinutoErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE);
	}
	@Test
	public void perMinutoErogazione_hazelcast_nearCache_putSync() throws Exception {
		this.restTest.perMinutoErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP);
	}
	@Test
	public void perMinutoErogazione_hazelcast_nearCache_putAsync() throws Exception {
		this.restTest.perMinutoErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP);
	}
	@Test
	public void perMinutoErogazione_hazelcast_contatoriSingoliPNCounter() throws Exception {
		this.restTest.perMinutoErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER);
	}
	@Test
	public void perMinutoErogazione_hazelcast_contatoriSingoliAtomicLong() throws Exception {
		this.restTest.perMinutoErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG);
	}
	@Test
	public void perMinutoErogazione_hazelcast_contatoriSingoliAtomicLongAsync() throws Exception {
		this.restTest.perMinutoErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG_ASYNC);
	}
	@Test
	public void richiestePerMinutoErogazione_hazelcast_replicatedMap() throws Exception {
		this.restTest.perMinutoErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_REPLICATED_MAP);
	}
	@Test
	public void richiestePerMinutoErogazione_redis_atomicLong() throws Exception {
		this.restTest.perMinutoErogazione(PolicyGroupByActiveThreadsType.REDISSON_ATOMIC_LONG);
	}
	@Test
	public void richiestePerMinutoErogazione_redis_longAdder() throws Exception {
		this.restTest.perMinutoErogazione(PolicyGroupByActiveThreadsType.REDISSON_LONGADDER);
	}
	
	
	
	
	
	
	@Test
	public void perMinutoFruizione_local() throws Exception {
		this.restTest.perMinutoFruizione(PolicyGroupByActiveThreadsType.LOCAL);
	}
	@Test
	public void perMinutoFruizione_local_divided_by_nodes() throws Exception {
		this.restTest.perMinutoFruizione(PolicyGroupByActiveThreadsType.LOCAL_DIVIDED_BY_NODES);
	}
	@Test
	public void perMinutoFruizione_database() throws Exception {
		this.restTest.perMinutoFruizione(PolicyGroupByActiveThreadsType.DATABASE);
	}
	@Test
	public void perMinutoFruizione_hazelcast() throws Exception {
		this.restTest.perMinutoFruizione(PolicyGroupByActiveThreadsType.HAZELCAST);
	}
	@Test
	public void perMinutoFruizione_hazelcast_nearCache() throws Exception {
		this.restTest.perMinutoFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE);
	}
	@Test
	public void perMinutoFruizione_hazelcast_localCache() throws Exception {
		this.restTest.perMinutoFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE);
	}
	@Test
	public void perMinutoFruizione_hazelcast_contatoriSingoliPNCounter() throws Exception {
		this.restTest.perMinutoFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER);
	}
	@Test
	public void perMinutoFruizione_hazelcast_contatoriSingoliAtomicLong() throws Exception {
		this.restTest.perMinutoFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG);
	}
	@Test
	public void perMinutoFruizione_hazelcast_contatoriSingoliAtomicLongAsync() throws Exception {
		this.restTest.perMinutoFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG_ASYNC);
	}
	@Test
	public void richiestePerMinutoFruizione_hazelcast_replicatedMap() throws Exception {
		this.restTest.perMinutoFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_REPLICATED_MAP);
	}
	@Test
	public void richiestePerMinutoFruizione_redis_atomicLong() throws Exception {
		this.restTest.perMinutoFruizione(PolicyGroupByActiveThreadsType.REDISSON_ATOMIC_LONG);
	}
	@Test
	public void richiestePerMinutoFruizione_redis_longAdder() throws Exception {
		this.restTest.perMinutoFruizione(PolicyGroupByActiveThreadsType.REDISSON_LONGADDER);
	}
	
	
	

	
}
