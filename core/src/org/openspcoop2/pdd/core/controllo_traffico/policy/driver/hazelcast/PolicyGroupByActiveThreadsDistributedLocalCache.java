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

package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast;

import java.util.HashMap;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.PolicyGroupByActiveThreads;
import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;

/**     
 *  PolicyGroupByActiveThreadsDistributedLocalCache
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyGroupByActiveThreadsDistributedLocalCache  extends AbstractPolicyGroupByActiveThreadsDistributed {

	private PolicyGroupByActiveThreads localPolicy;
	
	public PolicyGroupByActiveThreadsDistributedLocalCache(ActivePolicy policy, String uniqueIdMap,
			HazelcastInstance hazelcast) throws PolicyException {
		super(policy, uniqueIdMap, PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE, hazelcast);
		
		this.localPolicy = new PolicyGroupByActiveThreads(policy, PolicyGroupByActiveThreadsType.LOCAL); // USO LOCAL
	
	}
	

	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx)
			throws PolicyException {
		
		datiGroupBy = augmentIDUnivoco(datiGroupBy);
		
		// Lavoro in asincrono sui contatori della copia remota
		this.distributedMap.submitToKey(datiGroupBy, new StartRequestProcessor(this.activePolicy,ctx));
		
		return this.localPolicy.registerStartRequest(log, idTransazione, datiGroupBy, ctx);
	}


	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione,
			IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx) throws PolicyException, PolicyNotFoundException {

		datiGroupBy = augmentIDUnivoco(datiGroupBy);
		
		// Lavoro sulla copia remota
		this.distributedMap.submitToKey(datiGroupBy, new UpdateDatiRequestProcessor(this.activePolicy, ctx));
		
		return this.localPolicy.updateDatiStartRequestApplicabile(log, idTransazione, datiGroupBy, ctx);
	}

	
	@Override
	public void registerStopRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx,
			MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata)
			throws PolicyException, PolicyNotFoundException {
		
		datiGroupBy = augmentIDUnivoco(datiGroupBy);
		
		// Lavoro sulla copia remota
		this.distributedMap.submitToKey(datiGroupBy, new EndRequestProcessor(this.activePolicy, ctx, dati, isApplicabile, isViolata));			
		
		this.localPolicy.registerStopRequest(log, idTransazione, datiGroupBy, ctx, dati, isApplicabile, isViolata);
	}


	public PolicyGroupByActiveThreads getLocalPolicy() {
		return this.localPolicy;
	}
	
	
	@Override
	public void initMap(java.util.Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) {
		super.initMap(map);
		
		java.util.Map<IDUnivocoGroupByPolicy, DatiCollezionati> newMap = new HashMap<>();
		for (var e: map.entrySet()) {
			newMap.put(augmentIDUnivoco(e.getKey()), e.getValue());
		}
		
		this.localPolicy.initMap(newMap);
	}
	
	
	@Override
	public long getActiveThreads(IDUnivocoGroupByPolicy filtro) {
		return this.localPolicy.getActiveThreads(filtro);
	}
	
	
	@Override
	public void resetCounters() {
		this.localPolicy.resetCounters();
		super.resetCounters();
	}
	
	
	@Override
	public String printInfos(Logger log, String separatorGroups) throws UtilsException {
		return printInfos(log, separatorGroups, this.localPolicy.getMapActiveThreads());
	}

}
