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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicyMapId;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.constants.Costanti;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreadsInMemory;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyDateUtils;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.replicatedmap.ReplicatedMap;

/**
 * Map Replicata su ciascun nodo, le put vengono notificate e dsitribuite.
 * Vista la natura asincrona e concorrente degli aggiornamenti, il conteggio non è esatto senza una lock remota.
 * Non ha senso a questo punto neanche mantenerlo corretto all'interno di un nodo per mezzo di una lock locale.
 * 
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class PolicyGroupByActiveThreadsDistributedReplicatedMap implements IPolicyGroupByActiveThreadsInMemory {
	
	protected final HazelcastInstance hazelcast;
	protected final ReplicatedMap<IDUnivocoGroupByPolicy, DatiCollezionati> distributedMap;

	protected final ActivePolicy activePolicy;
	
	protected String uniqueIdMap_idActivePolicy;
	protected Date uniqueIdMap_updateTime;
	
	public PolicyGroupByActiveThreadsDistributedReplicatedMap(ActivePolicy policy, String uniqueIdMap,  HazelcastInstance hazelcast) throws PolicyException {
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
	
		String mapName = "hazelcast-replicated-map";
		
		boolean oneMapForeachPolicy = OpenSPCoop2Properties.getInstance().isControlloTrafficoGestorePolicyInMemoryHazelcastOneMapForeachPolicy();
		
		if (oneMapForeachPolicy) {
			this.distributedMap = this.hazelcast.getReplicatedMap(mapName + this.uniqueIdMap_idActivePolicy + "-rate-limiting");
			log.info("Hazelcast: Utilizzo Una Distributed Map per gruppo.");
		} else {
			this.distributedMap = this.hazelcast.getReplicatedMap(mapName+"rate-limiting");
			log.info("Hazelcast: Utilizzo Una Distributed Map globale.");
		}
		 
		// dummy get per inizializzare la map
		this.distributedMap.get(new IDUnivocoGroupByPolicy());

	}
	

	@Override
	public ActivePolicy getActivePolicy() {
		return this.activePolicy;
	}


	@Override
	public java.util.Map<IDUnivocoGroupByPolicy, DatiCollezionati> getMapActiveThreads() {
		return this.distributedMap;
	}
	

	@Override
	public long getActiveThreads() {
		return this.getActiveThreads(null);
	}
	
	@Override
	public void initMap(java.util.Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) {
		if(map!=null && map.size()>0){
			for (IDUnivocoGroupByPolicy datiGroupBy : map.keySet()) {
				datiGroupBy = augmentIDUnivoco(datiGroupBy);
				DatiCollezionati dati = map.get(datiGroupBy);
				this.distributedMap.put(datiGroupBy, dati);			
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
		
		if(this.distributedMap.size()>0){
			//		FIX: iterando nella maniera sottostante si ottiene il seguente errore se si usa la near-cache: key cannot be of type Data! hazelcast 
			//		for (var entry : this.distributedMap) {
			for (IDUnivocoGroupByPolicy datiGroupBy : this.distributedMap.keySet()) {
				DatiCollezionati datiCollezionati = this.distributedMap.get(datiGroupBy);
				datiCollezionati.resetCounters();
				this.distributedMap.put(datiGroupBy, datiCollezionati);
			}
		}			
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
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx)
			throws PolicyException {
		
		datiGroupBy = augmentIDUnivoco(datiGroupBy);
		
		DatiCollezionati datiCollezionati = this.distributedMap.get(datiGroupBy);
		boolean newDati = false;
		if (datiCollezionati == null) {
			Date gestorePolicyConfigDate = PolicyDateUtils.readGestorePolicyConfigDateIntoContext(ctx);
			datiCollezionati = new DatiCollezionati(this.activePolicy.getInstanceConfiguration().getUpdateTime(), gestorePolicyConfigDate);
			newDati = true;
		}
		else {
			if(datiCollezionati.getUpdatePolicyDate()!=null) {
				if(!datiCollezionati.getUpdatePolicyDate().equals(this.activePolicy.getInstanceConfiguration().getUpdateTime())) {
					// data aggiornata
					datiCollezionati.resetCounters(this.activePolicy.getInstanceConfiguration().getUpdateTime());
				}
			}
		}
		DatiCollezionati datiCollezionatiPerPolicyVerifier = (DatiCollezionati) datiCollezionati.clone(); // i valori utilizzati dal policy verifier verranno impostati con il valore impostato nell'operazione chiamata
		if(newDati) {
			datiCollezionatiPerPolicyVerifier.initDatiIniziali(this.activePolicy);
			datiCollezionatiPerPolicyVerifier.checkDate(log, this.activePolicy); // inizializza le date se ci sono
		}
		
		datiCollezionati.registerStartRequest(log, this.activePolicy, ctx, datiCollezionatiPerPolicyVerifier);
		
		this.distributedMap.put(datiGroupBy, datiCollezionati);
		
		return datiCollezionatiPerPolicyVerifier;
	}
	
	
	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione,
			IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx) throws PolicyException, PolicyNotFoundException {
		
		datiGroupBy = augmentIDUnivoco(datiGroupBy);

		DatiCollezionati datiCollezionati = this.distributedMap.get(datiGroupBy);
		if(datiCollezionati == null) {
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
		} else {
			DatiCollezionati datiCollezionatiPerPolicyVerifier = (DatiCollezionati) datiCollezionati.clone(); // i valori utilizzati dal policy verifier verranno impostati con il valore impostato nell'operazione chiamata
			
			boolean updated = datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy, ctx, datiCollezionatiPerPolicyVerifier);	
			if(updated) {
				this.distributedMap.put(datiGroupBy, datiCollezionati);
				return datiCollezionatiPerPolicyVerifier;
			}
			
			return null;
		}
	}
	
	@Override
	public void registerStopRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx, MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata)
			throws PolicyException, PolicyNotFoundException {
		
		datiGroupBy = augmentIDUnivoco(datiGroupBy);

		DatiCollezionati datiCollezionati = this.distributedMap.get(datiGroupBy);
		if(datiCollezionati == null) {
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
		} else {
			
			//System.out.println("<"+idTransazione+">registerStopRequest registerEndRequest ...");
			datiCollezionati.registerEndRequest(log, this.activePolicy, ctx, dati);
			//System.out.println("<"+idTransazione+">registerStopRequest registerEndRequest ok");
			if(isApplicabile){
				//System.out.println("<"+idTransazione+">registerStopRequest updateDatiEndRequestApplicabile ...");
				List<Integer> esitiCodeOk = null;
				List<Integer> esitiCodeKo_senzaFaultApplicativo = null;
				List<Integer> esitiCodeFaultApplicativo = null;
				try {
					EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(log,dati.getProtocollo());
					esitiCodeOk = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
					esitiCodeKo_senzaFaultApplicativo = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo();
					esitiCodeFaultApplicativo = esitiProperties.getEsitiCodeFaultApplicativo();
				}catch(Exception e) {
					throw new PolicyException(e.getMessage(),e);
				}
				datiCollezionati.updateDatiEndRequestApplicabile(log, this.activePolicy, ctx, dati,
						esitiCodeOk,esitiCodeKo_senzaFaultApplicativo, esitiCodeFaultApplicativo, isViolata);
				//System.out.println("<"+idTransazione+">registerStopRequest updateDatiEndRequestApplicabile ok");
				this.distributedMap.put(datiGroupBy, datiCollezionati);
			}
		}
	}


	
	
	
	@Override
	public String printInfos(Logger log, String separatorGroups) throws UtilsException {
		return printInfos(log, separatorGroups, this.distributedMap);
	}
	protected String printInfos(Logger log, String separatorGroups, java.util.Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) throws UtilsException {
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
			bf.append(Costanti.LABEL_MODALITA_SINCRONIZZAZIONE).append(" ").append(PolicyGroupByActiveThreadsType.HAZELCAST_REPLICATED_MAP.toLabel());
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
