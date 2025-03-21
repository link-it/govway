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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicyMapId;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.constants.Costanti;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreadsInMemory;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

/**     
 *  PolicyGroupByActiveThreadsDistributedAbstract
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractPolicyGroupByActiveThreadsDistributed implements IPolicyGroupByActiveThreadsInMemory {
	
	protected final HazelcastInstance hazelcast;
	protected final IMap<IDUnivocoGroupByPolicy, DatiCollezionati> distributedMap;
	protected final ActivePolicy activePolicy;
	
	protected PolicyGroupByActiveThreadsType type;
	
	protected String uniqueIdMap_idActivePolicy;
	protected Date uniqueIdMap_updateTime;
	
	public AbstractPolicyGroupByActiveThreadsDistributed(ActivePolicy policy, String uniqueIdMap, PolicyGroupByActiveThreadsType type, HazelcastInstance hazelcast) throws PolicyException {
		this.activePolicy = policy;
		this.hazelcast = hazelcast;
	
		this.uniqueIdMap_idActivePolicy = UniqueIdentifierUtilities.extractIdActivePolicy(uniqueIdMap);
		try {
			this.uniqueIdMap_updateTime = UniqueIdentifierUtilities.extractUpdateTimeActivePolicy(uniqueIdMap);
		}catch(Exception e) {
			throw new PolicyException(e.getMessage(),e);
		}
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(op2Properties.isControlloTrafficoDebug());
	
		this.type = type;
		String mapName = "hazelcast-";
		switch (type) {
		case HAZELCAST_MAP:
			mapName = "hazelcast-";
			break;
		case HAZELCAST_NEAR_CACHE:
			mapName = "hazelcast-near-cache-";
			break;
		case HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP:
			mapName = "hazelcast-near-cache-unsafe-sync-map-";
			break;
		case HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP:
			mapName = "hazelcast-near-cache-unsafe-async-map-";
			break;
		case HAZELCAST_LOCAL_CACHE:
			mapName = "hazelcast-local-cache-";
			break;
		default:
			break;
		}
		
		boolean oneMapForeachPolicy = OpenSPCoop2Properties.getInstance().isControlloTrafficoGestorePolicyInMemoryHazelcastOneMapForeachPolicy();
		if(oneMapForeachPolicy && PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE.equals(this.type)) {
			log.warn("Property isControlloTrafficoGestorePolicyInMemoryHazelcastOneMapForeachPolicy non compatibile con HAZELCAST_LOCAL_CACHE");
			oneMapForeachPolicy = false;
		}
		
		if (oneMapForeachPolicy) {
			this.distributedMap = this.hazelcast.getMap(mapName + this.uniqueIdMap_idActivePolicy + "-rate-limiting");
			log.info("Hazelcast: Utilizzo Una Distributed Map per gruppo.");
		} else {
			this.distributedMap = this.hazelcast.getMap(mapName+"rate-limiting");
			log.info("Hazelcast: Utilizzo Una Distributed Map globale.");
		}
		
		// dummy get per inizializzare la map
		if(this.distributedMap.get(new IDUnivocoGroupByPolicy())!=null) {
			// ignore
		}

	}
	

	@Override
	public ActivePolicy getActivePolicy() {
		return this.activePolicy;
	}


	@Override
	public Map<IDUnivocoGroupByPolicy, DatiCollezionati> getMapActiveThreads() {
		return this.distributedMap;
	}
	
	public IMap<IDUnivocoGroupByPolicy, DatiCollezionati> getDistributedMapActiveThreads(){
		return this.distributedMap;
	}


	@Override
	public long getActiveThreads() {
		return this.getActiveThreads(null);
	}
	
	@Override
	public void initMap(Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) {
		if(map!=null && !map.isEmpty()) {
			for (IDUnivocoGroupByPolicy datiGroupBy : map.keySet()) {
				datiGroupBy = augmentIDUnivoco(datiGroupBy);
				DatiCollezionati dati = map.get(datiGroupBy);
				InitProcessor initProcessor = new InitProcessor(dati);
				this.distributedMap.executeOnKey(datiGroupBy, initProcessor);			
			}
		}
		
	}


	@Override
	public long getActiveThreads(IDUnivocoGroupByPolicy filtro) {

		long counter = 0;
		
		// Quando leggo dalla distributedMap non aumento l'idUnivoco perchè
		// mi aspetto che sulla map vengano già registrati così.
		
		if(filtro!=null){
	//		FIX: iterando nella maniera sottostante si ottiene il seguente errore se si usa la near-cache: key cannot be of type Data! hazelcast 
	//		for (var entry : this.distributedMap) {
			for (IDUnivocoGroupByPolicy datiGroupBy : this.distributedMap.keySet()) {
				if(!datiGroupBy.match(filtro)){
					continue;
				}
				DatiCollezionati datiCollezionati = this.distributedMap.get(datiGroupBy);
				counter += datiCollezionati.getActiveRequestCounter();
			}
		}
		
		return counter;
	}


	@Override
	public void resetCounters() {
		this.distributedMap.executeOnEntries(new ResetCountersProcessor());
	}

	@Override
	public void remove() throws UtilsException{
	//		FIX: iterando nella maniera sottostante si ottiene il seguente errore se si usa la near-cache: key cannot be of type Data! hazelcast 
	//		for (var entry : this.distributedMap) {
		List<IDUnivocoGroupByPolicy> deleteList = new ArrayList<IDUnivocoGroupByPolicy>();
		for (IDUnivocoGroupByPolicy datiGroupBy : this.distributedMap.keySet()) {
			if(datiGroupBy instanceof IDUnivocoGroupByPolicyMapId){
				IDUnivocoGroupByPolicyMapId mapId = (IDUnivocoGroupByPolicyMapId) datiGroupBy;
				if(this.uniqueIdMap_idActivePolicy.equals(mapId.getUniqueMapId())) {
					deleteList.add(datiGroupBy);
				}
			}
		}
		while(!deleteList.isEmpty()) {
			IDUnivocoGroupByPolicy id = deleteList.remove(0);
			this.distributedMap.remove(id);	
		}
	}
	
	@Override
	public String printInfos(Logger log, String separatorGroups) throws UtilsException {
		return printInfos(log, separatorGroups, this.distributedMap);
	}
	protected String printInfos(Logger log, String separatorGroups, Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) throws UtilsException {
		StringBuilder bf = new StringBuilder();

		//System.out.println("\n\nPRINT INFO");
		
		for (IDUnivocoGroupByPolicy datiGroupBy : map.keySet()) {
			
			DatiCollezionati datiCollezionati = map.get(datiGroupBy);
			
//		FIX: iterando nella maniera sottostante si ottiene il seguente errore se si usa la near-cache: key cannot be of type Data! hazelcast 
//		for (var entry : this.distributedMap) {
//			IDUnivocoGroupByPolicy datiGroupBy = entry.getKey();
			
			if (!OpenSPCoop2Properties.getInstance().isControlloTrafficoGestorePolicyInMemoryHazelcastOneMapForeachPolicy()) {
				IDUnivocoGroupByPolicyMapId mapId = (IDUnivocoGroupByPolicyMapId) datiGroupBy;
				if(!this.uniqueIdMap_idActivePolicy.equals(mapId.getUniqueMapId())) {
					continue;
				}
			}
			
			//System.out.println("ID["+datiGroupBy.hashCode()+"] ["+datiGroupBy.toString()+"] ["+datiGroupBy.toString(false)+"]");
					
			bf.append(separatorGroups);
			bf.append("\n");
			bf.append(Costanti.LABEL_MODALITA_SINCRONIZZAZIONE).append(" ").append(this.type.toLabel());
			bf.append("\n");
			bf.append("Criterio di Collezionamento dei Dati\n");
			bf.append(datiGroupBy.toString(true));
			bf.append("\n");
//			entry.getValue().checkDate(log, this.activePolicy); // imposta correttamente gli intervalli
//			bf.append(entry.getValue().toString());
			datiCollezionati.checkDate(log, this.activePolicy); // imposta correttamente gli intervalli
			bf.append(datiCollezionati.toString());
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
	
	
	protected IDUnivocoGroupByPolicy augmentIDUnivoco(IDUnivocoGroupByPolicy idUnivoco) {
		// utile sempre aggiungere un id per l'inizializzazione
		if (OpenSPCoop2Properties.getInstance().isControlloTrafficoGestorePolicyInMemoryHazelcastOneMapForeachPolicy()) {
			return idUnivoco;
		} else {
			if(idUnivoco instanceof IDUnivocoGroupByPolicyMapId) {
				return idUnivoco;
			}
			else {
				return new IDUnivocoGroupByPolicyMapId(idUnivoco, this.uniqueIdMap_idActivePolicy); // NOTA: non serve gestirlo all'interno poichè verrà creato un nuovo identificativo //, this.uniqueIdMap_updateTime);
			}
		}
	}
	
	

}
