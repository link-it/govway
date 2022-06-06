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
		this.restTest.richiestePerMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST);
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
		this.restTest.richiestePerMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST);
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
		this.restTest.richiesteSimultaneeErogazione(PolicyGroupByActiveThreadsType.HAZELCAST);
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
		this.restTest.richiesteSimultaneeFruizione(PolicyGroupByActiveThreadsType.HAZELCAST);
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
}
