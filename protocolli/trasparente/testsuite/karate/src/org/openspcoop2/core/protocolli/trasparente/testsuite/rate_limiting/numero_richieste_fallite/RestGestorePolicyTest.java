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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste_fallite;

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
	public void perMinutoDefaultErogazione_local() throws Exception {
		this.restTest.perMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.LOCAL);
	}
	@Test
	public void perMinutoDefaultErogazione_local_divided_by_nodes() throws Exception {
		this.restTest.perMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.LOCAL_DIVIDED_BY_NODES);
	}
	@Test
	public void perMinutoDefaultErogazione_database() throws Exception {
		this.restTest.perMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.DATABASE);
	}
	@Test
	public void perMinutoDefaultErogazione_hazelcast() throws Exception {
		this.restTest.perMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST);
	}
	@Test
	public void perMinutoDefaultErogazione_hazelcast_nearCache() throws Exception {
		this.restTest.perMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE);
	}
	@Test
	public void perMinutoDefaultErogazione_hazelcast_localCache() throws Exception {
		this.restTest.perMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE);
	}
	@Test
	public void perMinutoDefaultErogazione_hazelcast_nearCache_putSync() throws Exception {
		this.restTest.perMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP);
	}
	@Test
	public void perMinutoDefaultErogazione_hazelcast_nearCache_putAsync() throws Exception {
		this.restTest.perMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP);
	}
	@Test
	public void perMinutoDefaultErogazione_hazelcast_contatoriSingoli() throws Exception {
		this.restTest.perMinutoDefaultErogazione(PolicyGroupByActiveThreadsType.HAZELCAST_PUNTUALE);
	}
	
	
	
	
	
	@Test
	public void perMinutoDefaultFruizione_local() throws Exception {
		this.restTest.perMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.LOCAL);
	}
	@Test
	public void perMinutoDefaultFruizione_local_divided_by_nodes() throws Exception {
		this.restTest.perMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.LOCAL_DIVIDED_BY_NODES);
	}
	@Test
	public void perMinutoDefaultFruizione_database() throws Exception {
		this.restTest.perMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.DATABASE);
	}
	@Test
	public void perMinutoDefaultFruizione_hazelcast() throws Exception {
		this.restTest.perMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST);
	}
	@Test
	public void perMinutoDefaultFruizione_hazelcast_nearCache() throws Exception {
		this.restTest.perMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE);
	}
	@Test
	public void perMinutoDefaultFruizione_hazelcast_localCache() throws Exception {
		this.restTest.perMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE);
	}
	@Test
	public void perMinutoDefaultFruizione_hazelcast_nearCache_putSync() throws Exception {
		this.restTest.perMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP);
	}
	@Test
	public void perMinutoDefaultFruizione_hazelcast_nearCache_putAsync() throws Exception {
		this.restTest.perMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP);
	}
	@Test
	public void perMinutoDefaultFruizione_hazelcast_contatoriSingoli() throws Exception {
		this.restTest.perMinutoDefaultFruizione(PolicyGroupByActiveThreadsType.HAZELCAST_PUNTUALE);
	}
	
	
}
