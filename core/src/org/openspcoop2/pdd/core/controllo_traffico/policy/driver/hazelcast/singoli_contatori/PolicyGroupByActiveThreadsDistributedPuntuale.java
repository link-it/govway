package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.singoli_contatori;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupBy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.driver.CostantiControlloTraffico;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreadsInMemory;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.TipoDatiCollezionati;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;

/**
 * Questa implementazione segue di pari passo quella normale di PolicyGroupByActiveThreads.
 * Il lock viene sostituito con un lock distribuito.
 * I DatiCollezionati istanziati sono quelli distribuiti.
 * 
 * @author Francesco Scarlato
 *
 */

public class PolicyGroupByActiveThreadsDistributedPuntuale  implements Serializable,IPolicyGroupByActiveThreadsInMemory {
	
	private static final long serialVersionUID = 1L;
	
	private final org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("PolicyGroupByActiveThreadsDistributedPuntualeNoLock");

	private final Map<IDUnivocoGroupByPolicy, DatiCollezionati> mapActiveThreads = new HashMap<IDUnivocoGroupByPolicy, DatiCollezionati>();
	// TODO Ritornare alla ConcurrentHashMap nel caso riusciamo a togliere le lock locali.


    private final HazelcastInstance hazelcast;
    private final String uniqueMapId;
	
	private final ActivePolicy activePolicy;
	private final TipoDatiCollezionati tipoDatiCollezionati;

	public PolicyGroupByActiveThreadsDistributedPuntuale(ActivePolicy activePolicy, String uniqueMapId, HazelcastInstance hazelcast, TipoDatiCollezionati tipoDatiCollezionati) {
		this.activePolicy = activePolicy;
		this.hazelcast = hazelcast;
		this.uniqueMapId = uniqueMapId;
		this.tipoDatiCollezionati = tipoDatiCollezionati;
	}
	
	
	@Override
	public ActivePolicy getActivePolicy() {
		return this.activePolicy;
	}
	
	
	@Override
	public Map<IDUnivocoGroupByPolicy, DatiCollezionati> getMapActiveThreads(){
		return this.mapActiveThreads;
	}
	
	
	@Override
	public void initMap(Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) {
		
		this.lock.acquireThrowRuntime("initMap");
		try {
			if(map!=null){
					for( var e : map.entrySet()) {
						this.mapActiveThreads.put(
								e.getKey(), 
								BuilderDatiCollezionatiDistributed.build(this.tipoDatiCollezionati,e.getValue(), this.hazelcast, String.valueOf( (this.uniqueMapId+e.getKey()).hashCode())));
					}
				}
		}
			finally {
				this.lock.release("initMap");
			}
	}
	
	
	@Override
	public void resetCounters(){
		
		this.lock.acquireThrowRuntime("resetCounters");
		try {
			Iterator<DatiCollezionati> datiCollezionati = this.mapActiveThreads.values().iterator();
			while (datiCollezionati.hasNext()) {
				DatiCollezionati item = (DatiCollezionati) datiCollezionati.next();
				item.resetCounters();
			}
		}
		finally {
			this.lock.release("resetCounters");
		}
	}
	
	
	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy) throws PolicyException{
		
		DatiCollezionati datiCollezionati;
		
		this.lock.acquireThrowRuntime("registerStartRequest");
		try {
			datiCollezionati = this.mapActiveThreads.get(datiGroupBy);
			
			if (datiCollezionati == null){				
				datiCollezionati = BuilderDatiCollezionatiDistributed.build(
						this.tipoDatiCollezionati, 
						this.activePolicy.getInstanceConfiguration().getUpdateTime(), 
						this.hazelcast, 
						String.valueOf( (this.uniqueMapId+datiGroupBy).hashCode() ));
				
				this.mapActiveThreads.put(datiGroupBy, datiCollezionati); // registro nuova immagine
			}
				
			// incremento il numero di thread
			datiCollezionati.registerStartRequest(log, this.activePolicy);
			
			return (DatiCollezionati) datiCollezionati.clone();
		}
		finally {
			this.lock.release("registerStartRequest");
		}
	
	}
	
	
	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy) throws PolicyException,PolicyNotFoundException{
		
		this.lock.acquireThrowRuntime("updateDatiStartRequestApplicabile");
		
		try {
			DatiCollezionati datiCollezionati = this.mapActiveThreads.get(datiGroupBy);
			if(datiCollezionati == null) {
				throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
			}
			
			// incremento il numero dei contatori
			boolean updated = datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy);
									
			// mi salvo fuori dal synchronized l'attuale stato
			if(updated) {
				return (DatiCollezionati) datiCollezionati.clone();
			}
		
		}
		finally {
			this.lock.release("updateDatiStartRequestApplicabile");
		}
		
		return null;
	}
	
	
	@Override
	public void registerStopRequest(Logger log, String idTransazione,IDUnivocoGroupByPolicy datiGroupBy, MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata) throws PolicyException,PolicyNotFoundException{
		
		// TODO: Qui la lock locale invece non dovrebbe servire: non restituiamo dati e gli unici dati che modifichiamo sono quelli distribuiti,
		//		quindi già sincronizzati.
		this.lock.acquireThrowRuntime("registerStopRequest");
		try {
			DatiCollezionati datiCollezionati =  this.mapActiveThreads.get(datiGroupBy);
			if(datiCollezionati == null) {
				throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
			}
				
			datiCollezionati.registerEndRequest(log, this.activePolicy, dati);
			if(isApplicabile){
				
				List<Integer> esitiCodeOk = null;
				List<Integer> esitiCodeKo_senzaFaultApplicativo = null;
				List<Integer> esitiCodeFaultApplicativo = null;
				try {
					esitiCodeOk = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeOk_senzaFaultApplicativo();
					esitiCodeKo_senzaFaultApplicativo = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeKo_senzaFaultApplicativo();
					esitiCodeFaultApplicativo = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeFaultApplicativo();
				}catch(Exception e) {
					throw new PolicyException(e.getMessage(),e);
				}
				datiCollezionati.updateDatiEndRequestApplicabile(log, this.activePolicy, dati,
						esitiCodeOk,esitiCodeKo_senzaFaultApplicativo, esitiCodeFaultApplicativo, isViolata);
			}
		
		}
		finally {
			this.lock.release("registerStopRequest");
		}

	}

	
	@Override
	public long getActiveThreads(){
		return this.getActiveThreads(null);
	}
	
	
	@Override
	public long getActiveThreads(IDUnivocoGroupByPolicy filtro){
		
		this.lock.acquireThrowRuntime("getActiveThreads");
		try {
			long counter = 0l;
			
			for (IDUnivocoGroupByPolicy datiGroupBy : this.mapActiveThreads.keySet()) {
				
				if(filtro!=null){
					IDUnivocoGroupBy<IDUnivocoGroupByPolicy> idAstype = (IDUnivocoGroupBy<IDUnivocoGroupByPolicy>) datiGroupBy;
					if(!idAstype.match(filtro)){
						continue;
					}
				}
				
				counter = counter + this.mapActiveThreads.get(datiGroupBy).getActiveRequestCounter();
			}
		
			return counter;
		}
		finally {
			this.lock.release("getActiveThreads");
		}
		
	}
	
	
	@Override
	public String printInfos(Logger log, String separatorGroups) throws UtilsException{
		
		this.lock.acquireThrowRuntime("printInfos");
		try {
			StringBuilder bf = new StringBuilder();
			if(this.mapActiveThreads!=null && !this.mapActiveThreads.isEmpty()) {
				for (IDUnivocoGroupByPolicy datiGroupBy : this.mapActiveThreads.keySet()) {
					bf.append(separatorGroups);
					bf.append("\n");
					bf.append(CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE).append(" ").append(PolicyGroupByActiveThreadsType.HAZELCAST_PUNTUALE.toLabel());
					bf.append("\n");
					bf.append("Criterio di Collezionamento dei Dati\n");
					bf.append(datiGroupBy.toString(true));
					bf.append("\n");
					this.mapActiveThreads.get(datiGroupBy).checkDate(log, this.activePolicy); // imposta correttamente gli intervalli
					bf.append(this.mapActiveThreads.get(datiGroupBy).toString());
					bf.append("\n");
				}
			}
			if(bf.length()<=0){
				bf.append("Nessuna informazione disponibile");
				return bf.toString();
			}
			else{
				return bf.toString()+separatorGroups;
			}
		}
		finally {
			this.lock.release("printInfos");
		}

	}


	// TODO: Testa questo
	@Override
	public void remove() throws UtilsException {
		this.lock.acquireThrowRuntime("remove");

		try {
			for (var e : this.mapActiveThreads.entrySet()) {
				IDatiCollezionatiDistributed dati = (IDatiCollezionatiDistributed) e.getValue();
				dati.destroyDatiDistribuiti();
			}
		} 	finally {
			this.lock.release("printInfos");
		}
	}

}
