/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste;

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
	public void richiestePerMinutoDefaultErogazione_local() throws Exception {
		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.LOCAL);
	}
	@Test
	public void richiestePerMinutoDefaultErogazione_local_divided_by_nodes() throws Exception {
		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.LOCAL_DIVIDED_BY_NODES);
	}
	@Test
	public void richiestePerMinutoDefaultErogazione_database() throws Exception {
		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.DATABASE);
	}
	@Test
	public void richiestePerMinutoDefaultErogazione_hazelcast() throws Exception {
		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_MAP);
	}
	@Test
	public void richiestePerMinutoDefaultErogazione_hazelcast_nearCache() throws Exception {
		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE);
	}
	@Test
	public void richiestePerMinutoDefaultErogazione_hazelcast_localCache() throws Exception {
		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE);
	}
	@Test
	public void richiestePerMinutoDefaultErogazione_hazelcast_nearCache_putSync() throws Exception {
		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP);
	}
	@Test
	public void richiestePerMinutoDefaultErogazione_hazelcast_nearCache_putAsync() throws Exception {
		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP);
	}
	@Test
	public void richiestePerMinutoDefaultErogazione_hazelcast_contatoriSingoli_PNCounter() throws Exception {
		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER);
	}
	@Test
	public void richiestePerMinutoDefaultErogazione_hazelcast_contatoriSingoli_AtomicLong() throws Exception {
		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG);
	}
	@Test
	public void richiestePerMinutoDefaultErogazione_hazelcast_contatoriSingoli_AtomicLongAsync() throws Exception {
		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG_ASYNC);
	}
//	@Test
//	public void richiestePerMinutoDefaultErogazione_hazelcast_replicatedMap() throws Exception {
//		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_REPLICATED_MAP);
//	}
	@Test
	public void richiestePerMinutoDefaultErogazione_redis_atomicLong() throws Exception {
		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.REDISSON_ATOMIC_LONG);
	}
//	@Test
//	public void richiestePerMinutoDefaultErogazione_redis_longAdder() throws Exception {
//		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.REDISSON_LONGADDER);
//	}
	
	
	
	
	
	
	@Test
	public void richiestePerMinutoDefaultFruizione_local() throws Exception {
		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.LOCAL);
	}
	@Test
	public void richiestePerMinutoDefaultFruizione_local_divided_by_nodes() throws Exception {
		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.LOCAL_DIVIDED_BY_NODES);
	}
	@Test
	public void richiestePerMinutoDefaultFruizione_database() throws Exception {
		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.DATABASE);
	}
	@Test
	public void richiestePerMinutoDefaultFruizione_hazelcast() throws Exception {
		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_MAP);
	}
	@Test
	public void richiestePerMinutoDefaultFruizione_hazelcast_nearCache() throws Exception {
		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE);
	}
	@Test
	public void richiestePerMinutoDefaultFruizione_hazelcast_localCache() throws Exception {
		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE);
	}
	@Test
	public void richiestePerMinutoDefaultFruizione_hazelcast_nearCache_putSync() throws Exception {
		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP);
	}
	@Test
	public void richiestePerMinutoDefaultFruizione_hazelcast_nearCache_putAsync() throws Exception {
		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP);
	}
	@Test
	public void richiestePerMinutoDefaultFruizione_hazelcast_contatoriSingoli_PNCounter() throws Exception {
		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER);
	}
	@Test
	public void richiestePerMinutoDefaultFruizione_hazelcast_contatoriSingoli_AtomicLong() throws Exception {
		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG);
	}
	@Test
	public void richiestePerMinutoDefaultFruizione_hazelcast_contatoriSingoliAtomicLongAsync() throws Exception {
		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG_ASYNC);
	}
//	@Test
//	public void richiestePerMinutoDefaultFruizione_hazelcast_replicatedMap() throws Exception {
//		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_REPLICATED_MAP);
//	}
	@Test
	public void richiestePerMinutoDefaultFruizione_redis_atomicLong() throws Exception {
		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.REDISSON_ATOMIC_LONG);
	}
//	@Test
//	public void richiestePerMinutoDefaultFruizione_redis_longAdder() throws Exception {
//		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.REDISSON_LONGADDER);
//	}
	
	
	
	
	
	@Test
	public void richiesteSimultaneeErogazione_local() throws Exception {
		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.LOCAL);
	}
	@Test
	public void richiesteSimultaneeErogazione_local_divided_by_nodes() throws Exception {
		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.LOCAL_DIVIDED_BY_NODES);
	}
	@Test
	public void richiesteSimultaneeErogazione_database() throws Exception {
		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.DATABASE);
	}
	@Test
	public void richiesteSimultaneeErogazione_hazelcast() throws Exception {
		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_MAP);
	}
	@Test
	public void richiesteSimultaneeErogazione_hazelcast_nearCache() throws Exception {
		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE);
	}
	@Test
	public void richiesteSimultaneeErogazione_hazelcast_localCache() throws Exception {
		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE);
	}
	@Test
	public void richiesteSimultaneeErogazione_hazelcast_nearCache_putSync() throws Exception {
		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP);
	}
	@Test
	public void richiesteSimultaneeErogazione_hazelcast_nearCache_putAsync() throws Exception {
		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP);
	}
	@Test
	public void richiesteSimultaneeErogazione_hazelcast_contatoriSingoli_PNCounter() throws Exception {
		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER);
	}
	@Test
	public void richiesteSimultaneeErogazione_hazelcast_contatoriSingoliAtomicLong() throws Exception {
		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG);
	}
	@Test
	public void richiesteSimultaneeErogazione_hazelcast_contatoriSingoliAtomicLongAsync() throws Exception {
		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG_ASYNC);
	}
//	@Test
//	public void richiesteSimultaneeErogazione_hazelcast_replicatedMap() throws Exception {
//		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_REPLICATED_MAP);
//	}
	@Test
	public void richiesteSimultaneeErogazione_redis_contatoriSingoliAtomicLong() throws Exception {
		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.REDISSON_ATOMIC_LONG);
	}
//	@Test
//	public void richiesteSimultaneeErogazione_redis_contatoriSingoliLongAdder() throws Exception {
//		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.REDISSON_LONGADDER);
//	}	

	
	
	
	
	@Test
	public void richiesteSimultaneeFruizione_local() throws Exception {
		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.LOCAL);
	}
	@Test
	public void richiesteSimultaneeFruizione_local_divided_by_nodes() throws Exception {
		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.LOCAL_DIVIDED_BY_NODES);
	}
	@Test
	public void richiesteSimultaneeFruizione_database() throws Exception {
		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.DATABASE);
	}
	@Test
	public void richiesteSimultaneeFruizione_hazelcast() throws Exception {
		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_MAP);
	}
	@Test
	public void richiesteSimultaneeFruizione_hazelcast_nearCache() throws Exception {
		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE);
	}
	@Test
	public void richiesteSimultaneeFruizione_hazelcast_localCache() throws Exception {
		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE);
	}
	@Test
	public void richiesteSimultaneeFruizione_hazelcast_nearCache_putSync() throws Exception {
		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP);
	}
	@Test
	public void richiesteSimultaneeFruizione_hazelcast_nearCache_putAsync() throws Exception {
		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP);
	}
	@Test
	public void richiesteSimultaneeFruizione_hazelcast_contatoriSingoliPNCounter() throws Exception {
		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER);
	}
	@Test
	public void richiesteSimultaneeFruizione_hazelcast_contatoriSingoliAtomicLong() throws Exception {
		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG);
	}
	@Test
	public void richiesteSimultaneeFruizione_hazelcast_contatoriSingoliAtomicLongAsync() throws Exception {
		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG_ASYNC);
	}
//	@Test
//	public void richiesteSimultaneeFruizione_hazelcast_replicatedMap() throws Exception {
//		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_REPLICATED_MAP);
//	}
	@Test
	public void richiesteSimultaneeFruizione_redis_contatoriSingoliAtomicLong() throws Exception {
		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.REDISSON_ATOMIC_LONG);
	}
//	@Test
//	public void richiesteSimultaneeFruizione_redis_contatoriSingoliLongAdder() throws Exception {
//		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.REDISSON_LONGADDER);
//	}	

	
}
