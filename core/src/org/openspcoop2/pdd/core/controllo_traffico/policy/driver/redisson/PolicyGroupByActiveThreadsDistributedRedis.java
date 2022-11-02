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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupBy;
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
import org.redisson.api.RMap;
import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;
import org.redisson.api.TransactionOptions;
import org.redisson.transaction.TransactionException;
import org.slf4j.Logger;

/**     
 *  PolicyGroupByActiveThreadsDistributedRedis
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyGroupByActiveThreadsDistributedRedis  implements IPolicyGroupByActiveThreadsInMemory  {
	
	private final ActivePolicy activePolicy;
	private final RedissonClient redisson;
	private final RMap<IDUnivocoGroupByPolicy, DatiCollezionati> distributedMap;
	private String mapId = null;
	
	protected PolicyGroupByActiveThreadsType type;
	
	protected String uniqueIdMap_idActivePolicy;
	protected Date uniqueIdMap_updateTime;
	
	public PolicyGroupByActiveThreadsDistributedRedis(ActivePolicy activePolicy, String uniqueIdMap,
			RedissonClient redisson) throws PolicyException {
		this.activePolicy = activePolicy;
		this.redisson = redisson;
		
		this.uniqueIdMap_idActivePolicy = UniqueIdentifierUtilities.extractIdActivePolicy(uniqueIdMap);
		try {
			this.uniqueIdMap_updateTime = UniqueIdentifierUtilities.extractUpdateTimeActivePolicy(uniqueIdMap);
		}catch(Exception e) {
			throw new PolicyException(e.getMessage(),e);
		}
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(op2Properties.isControlloTrafficoDebug());
		
		this.type = PolicyGroupByActiveThreadsType.REDISSON_MAP;
		
		boolean oneMapForeachPolicy = OpenSPCoop2Properties.getInstance().isControlloTrafficoGestorePolicyInMemoryRedisOneMapForeachPolicy();
		String mapName = "redis-";
		if (oneMapForeachPolicy) {
			this.mapId = mapName + this.uniqueIdMap_idActivePolicy + "-rate-limiting";
			this.distributedMap = redisson.getMap(this.mapId);
			log.info("Hazelcast: Utilizzo Una Distributed Map per gruppo.");
		} else {
			this.mapId = mapName + "rate-limiting";
			this.distributedMap = redisson.getMap(this.mapId);
			log.info("Hazelcast: Utilizzo Una Distributed Map globale.");
		}
	}

	
	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx)
			throws PolicyException {
		
		RTransaction transaction = this.redisson.createTransaction(TransactionOptions.defaults());
		RMap<IDUnivocoGroupByPolicy, DatiCollezionati> map = transaction.getMap(this.mapId);
		
		DatiCollezionati datiCollezionati = map.get(datiGroupBy);
		if (datiCollezionati == null) {
			Date gestorePolicyConfigDate = PolicyDateUtils.readGestorePolicyConfigDateIntoContext(ctx);
			datiCollezionati = new DatiCollezionati(this.activePolicy.getInstanceConfiguration().getUpdateTime(), gestorePolicyConfigDate);
		}
		else {
			if(datiCollezionati.getUpdatePolicyDate()!=null) {
				if(!datiCollezionati.getUpdatePolicyDate().equals(this.activePolicy.getInstanceConfiguration().getUpdateTime())) {
					// data aggiornata
					datiCollezionati.resetCounters(this.activePolicy.getInstanceConfiguration().getUpdateTime());
				}
			}
		}
		
		datiCollezionati.registerStartRequest(log, this.activePolicy, ctx);
		
		// mi salvo l'attuale stato
		DatiCollezionati datiCollezionatiReaded = (DatiCollezionati) datiCollezionati.clone(); 
		
		map.fastPut(datiGroupBy, datiCollezionati);
		
		try {
			transaction.commit();
		} catch(TransactionException e) {
			transaction.rollback();
			throw new PolicyException("Errore durante la transazione registerStartRequest di Redis", e);
		}
		
		return datiCollezionatiReaded;
	}

	
	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione,
			IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx) throws PolicyException, PolicyNotFoundException {
		
		RTransaction transaction = this.redisson.createTransaction(TransactionOptions.defaults());
		RMap<IDUnivocoGroupByPolicy, DatiCollezionati> map = transaction.getMap(this.mapId);

		DatiCollezionati datiCollezionati = map.get(datiGroupBy);
		if(datiCollezionati == null){
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
		}		
		
		datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy, ctx);
		
		// mi salvo l'attuale stato
		DatiCollezionati datiCollezionatiReaded = (DatiCollezionati) datiCollezionati.clone(); 
		
		map.fastPut(datiGroupBy, datiCollezionati);
		
		try {
			transaction.commit();
		} catch(TransactionException e) {
			transaction.rollback();
			throw new PolicyException("Errore durante la transazione registerStartRequest di Redis", e);
		}
		
		return datiCollezionatiReaded;
	}

	@Override
	public void registerStopRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx,
			MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata)
			throws PolicyException, PolicyNotFoundException {
		
		RTransaction transaction = this.redisson.createTransaction(TransactionOptions.defaults());
		RMap<IDUnivocoGroupByPolicy, DatiCollezionati> map = transaction.getMap(this.mapId);

		DatiCollezionati datiCollezionati = map.get(datiGroupBy);
		if(datiCollezionati == null){
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
		}	
		
		if(isApplicabile) {
			datiCollezionati.registerEndRequest(log, this.activePolicy, ctx, dati);
			List<Integer> esitiCodeOk = null;
			List<Integer> esitiCodeKo_senzaFaultApplicativo = null;
			List<Integer> esitiCodeFaultApplicativo = null;
			try {
				// In queste tre di sotto pare il logger non venga utilizzato
				EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(log,dati.getProtocollo());
				esitiCodeOk = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
				esitiCodeKo_senzaFaultApplicativo = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo();
				esitiCodeFaultApplicativo = esitiProperties.getEsitiCodeFaultApplicativo();
				datiCollezionati.updateDatiEndRequestApplicabile(
						log, 	// logger
						this.activePolicy, ctx, dati,
						esitiCodeOk,esitiCodeKo_senzaFaultApplicativo, esitiCodeFaultApplicativo, 
						isViolata);
			}catch(Exception e) {
				throw new PolicyException(e.getMessage(),e);
			}
			map.fastPut(datiGroupBy, datiCollezionati);
		} else {
			datiCollezionati.registerEndRequest(null, this.activePolicy, ctx, dati);
			map.fastPut(datiGroupBy, datiCollezionati);
		}
		
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
	public void initMap(java.util.Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) {
		this.distributedMap.putAll(map);		
	}

	
	@Override
	public long getActiveThreads() {
		return getActiveThreads(null);
	}

	@Override
	public long getActiveThreads(IDUnivocoGroupByPolicy filtro) {
		long counter = 0;
		
		// Recupero tutta la map distribuita
		var cloned = this.distributedMap.readAllEntrySet();
		
		for (var entry :  cloned) {
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
		this.distributedMap.forEach( (var id, var dati) -> {
			dati.resetCounters();
		});
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
		StringBuilder bf = new StringBuilder();

		this.distributedMap.forEach( (var id, var dati) -> {
			IDUnivocoGroupByPolicy datiGroupBy = id;
			bf.append(separatorGroups);
			bf.append("\n");
			bf.append(Costanti.LABEL_MODALITA_SINCRONIZZAZIONE).append(" ").append(this.type.toLabel());
			bf.append("\n");
			bf.append("Criterio di Collezionamento dei Dati\n");
			bf.append(datiGroupBy.toString(true));
			bf.append("\n");
			dati.checkDate(log, this.activePolicy); // imposta correttamente gli intervalli
			bf.append(dati.toString());
			bf.append("\n");			
		});
		
		if(bf.length()<=0){
			bf.append("Nessuna informazione disponibile");
			return bf.toString();
		}
		else{
			return bf.toString()+separatorGroups;
		}
	}

}
