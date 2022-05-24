package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupBy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreadsInMemory;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.redisson.api.RMap;
import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;
import org.redisson.api.TransactionOptions;
import org.redisson.transaction.TransactionException;
import org.slf4j.Logger;

public class PolicyGroupByActiveThreadsDistributedRedis  implements IPolicyGroupByActiveThreadsInMemory  {
	
	private final ActivePolicy activePolicy;
	private final RedissonClient redisson;
	private final RMap<IDUnivocoGroupByPolicy, DatiCollezionati> distributedMap;
	private final String mapId;
	
	public PolicyGroupByActiveThreadsDistributedRedis(ActivePolicy activePolicy, String uniqueIdMap,
			RedissonClient redisson) {
		this.activePolicy = activePolicy;
		this.redisson = redisson;
		this.mapId = "redis-" + uniqueIdMap + "-rate-limiting";
		this.distributedMap = redisson.getMap(this.mapId);
	}

	
	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy)
			throws PolicyException {
		
		RTransaction transaction = this.redisson.createTransaction(TransactionOptions.defaults());
		RMap<IDUnivocoGroupByPolicy, DatiCollezionati> map = transaction.getMap(this.mapId);
		
		DatiCollezionati datiCollezionati = map.get(datiGroupBy);
		if (datiCollezionati == null) {
			datiCollezionati = new DatiCollezionati();
		}
		
		datiCollezionati.registerStartRequest(log, this.activePolicy);
		map.fastPut(datiGroupBy, datiCollezionati);
		
		try {
			transaction.commit();
		} catch(TransactionException e) {
			transaction.rollback();
			throw new PolicyException("Errore durante la transazione registerStartRequest di Redis", e);
		}
		
		return datiCollezionati;
	}

	
	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione,
			IDUnivocoGroupByPolicy datiGroupBy) throws PolicyException, PolicyNotFoundException {
		
		RTransaction transaction = this.redisson.createTransaction(TransactionOptions.defaults());
		RMap<IDUnivocoGroupByPolicy, DatiCollezionati> map = transaction.getMap(this.mapId);

		DatiCollezionati datiCollezionati = map.get(datiGroupBy);
		if(datiCollezionati == null){
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
		}		
		
		datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy);
		map.fastPut(datiGroupBy, datiCollezionati);
		
		try {
			transaction.commit();
		} catch(TransactionException e) {
			transaction.rollback();
			throw new PolicyException("Errore durante la transazione registerStartRequest di Redis", e);
		}
		
		return datiCollezionati;
	}

	@Override
	public void registerStopRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy,
			MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata)
			throws PolicyException, PolicyNotFoundException {
		
		RTransaction transaction = this.redisson.createTransaction(TransactionOptions.defaults());
		RMap<IDUnivocoGroupByPolicy, DatiCollezionati> map = transaction.getMap(this.mapId);

		DatiCollezionati datiCollezionati = map.get(datiGroupBy);
		if(datiCollezionati == null){
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
		}	
		
		if(isApplicabile) {
			datiCollezionati.registerEndRequest(log, this.activePolicy, dati);
			List<Integer> esitiCodeOk = null;
			List<Integer> esitiCodeKo_senzaFaultApplicativo = null;
			List<Integer> esitiCodeFaultApplicativo = null;
			try {
				// In queste tre di sotto pare il logger non venga utilizzato
				esitiCodeOk = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeOk_senzaFaultApplicativo();
				esitiCodeKo_senzaFaultApplicativo = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeKo_senzaFaultApplicativo();
				esitiCodeFaultApplicativo = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeFaultApplicativo();
				datiCollezionati.updateDatiEndRequestApplicabile(
						log, 	// logger
						this.activePolicy, dati,
						esitiCodeOk,esitiCodeKo_senzaFaultApplicativo, esitiCodeFaultApplicativo, 
						isViolata);
			}catch(Exception e) {
				throw new PolicyException(e.getMessage(),e);
			}
			map.fastPut(datiGroupBy, datiCollezionati);
		} else {
			datiCollezionati.registerEndRequest(null, this.activePolicy, dati);
			map.fastPut(datiGroupBy, datiCollezionati);
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

	
	@Override
	public void initMap(Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) {
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
	public String printInfos(Logger log, String separatorGroups) throws UtilsException {
		StringBuilder bf = new StringBuilder();

		this.distributedMap.forEach( (var id, var dati) -> {
			IDUnivocoGroupByPolicy datiGroupBy = id;
			bf.append(separatorGroups);
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
