package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import java.util.Map;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupBy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreadsInMemory;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

public abstract class PolicyGroupByActiveThreadsDistributedAbstract implements IPolicyGroupByActiveThreadsInMemory {
	
	protected final HazelcastInstance hazelcast;
	protected final IMap<IDUnivocoGroupByPolicy, DatiCollezionati> distributedMap;
	protected final StartRequestProcessor startRequestProcessor;
	protected final UpdateDatiRequestProcessor updateDatiRequestProcessor;
	protected final ActivePolicy activePolicy;
	
	public PolicyGroupByActiveThreadsDistributedAbstract(ActivePolicy policy, String uniqueIdMap, HazelcastInstance hazelcast) {
		this.activePolicy = policy;
		 
		 this.hazelcast = hazelcast;
		 this.distributedMap = this.hazelcast.getMap("hazelcast-" + uniqueIdMap + "-rate-limiting");
		 this.startRequestProcessor = new StartRequestProcessor(policy);
		 this.updateDatiRequestProcessor = new UpdateDatiRequestProcessor(policy);
	}
	
	
	@Override
	public ActivePolicy getActivePolicy() {
		return this.activePolicy;
	}


	@Override
	public Map<IDUnivocoGroupByPolicy, DatiCollezionati> getMapActiveThreads() {
		return this.distributedMap;
	}


	@Override
	public long getActiveThreads() {
		return this.getActiveThreads(null);
	}


	@Override
	public long getActiveThreads(IDUnivocoGroupByPolicy filtro) {

		long counter = 0;
		
		for (var entry : this.distributedMap) {
			if(filtro!=null){
				IDUnivocoGroupBy<IDUnivocoGroupByPolicy> idAstype = (IDUnivocoGroupBy<IDUnivocoGroupByPolicy>) entry.getKey();
				if(!idAstype.match(filtro)){
					continue;
				}
				counter += entry.getValue().getActiveRequestCounter();
			}
		}
		
		return counter;
	}


	@Override
	public void resetCounters() {
		this.distributedMap.executeOnEntries(new ResetCountersProcessor());
	}


	@Override
	public String printInfos(Logger log, String separatorGroups) throws UtilsException {
		StringBuilder bf = new StringBuilder();

		for (var entry : this.distributedMap) {
			IDUnivocoGroupByPolicy datiGroupBy = entry.getKey();
			bf.append(separatorGroups);
			bf.append("\n");
			bf.append("Criterio di Collezionamento dei Dati\n");
			bf.append(datiGroupBy.toString(true));
			bf.append("\n");
			entry.getValue().checkDate(log, this.activePolicy); // imposta correttamente gli intervalli
			bf.append(entry.getValue().toString());
			bf.append("\n");
		}

		if(bf.length()<=0){
			bf.append("Nessuna informazione disponibile");
			return bf.toString();
		}
		else{
			return bf.toString()+separatorGroups;
		}
		
	}

}
