package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast;

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
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreadsInMemory;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;

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
	
	private final Map<IDUnivocoGroupByPolicy, DatiCollezionati> mapActiveThreads = new HashMap<IDUnivocoGroupByPolicy, DatiCollezionati>();
    
    private final HazelcastInstance hazelcast;
    private final String uniqueMapId;
	private final FencedLock lock;
	
	private final ActivePolicy activePolicy;
	private final TipoDatiCollezionati tipoDatiCollezionati;

	public PolicyGroupByActiveThreadsDistributedPuntuale(ActivePolicy activePolicy, String uniqueMapId, HazelcastInstance hazelcast, TipoDatiCollezionati tipoDatiCollezionati) {
		this.activePolicy = activePolicy;
		this.hazelcast = hazelcast;
		this.uniqueMapId = uniqueMapId;
		this.lock = hazelcast.getCPSubsystem().getLock(uniqueMapId+"-lock");
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
		
		this.lock.lock();
		try {
			if(map!=null){
				for( var e : map.entrySet()) {
					this.mapActiveThreads.put(e.getKey(), BuilderDatiCollezionatiDistributed.build(this.tipoDatiCollezionati,  e.getValue(), this.hazelcast, this.uniqueMapId));
				}
			}
		}finally {
			this.lock.unlock();
		}
	}
	
	
	@Override
	public void resetCounters(){
		this.lock.lock();
		try {
			if(this.mapActiveThreads.size()>0){
				Iterator<DatiCollezionati> datiCollezionati = this.mapActiveThreads.values().iterator();
				while (datiCollezionati.hasNext()) {
					DatiCollezionati item = (DatiCollezionati) datiCollezionati.next();
					item.resetCounters();
				}
			}
		}finally {
			this.lock.unlock();
		}
	}
	
	
	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy) throws PolicyException{
				
		DatiCollezionati datiCollezionatiReaded = null;

		this.lock.lock();
		try {
			
			DatiCollezionati datiCollezionati = null;
			if(this.mapActiveThreads.containsKey(datiGroupBy)){
				datiCollezionati = (DatiCollezionatiDistributed) this.mapActiveThreads.get(datiGroupBy);	
			}
			else{
				datiCollezionati = BuilderDatiCollezionatiDistributed.build(this.tipoDatiCollezionati, this.activePolicy.getInstanceConfiguration().getUpdateTime(), this.hazelcast, this.uniqueMapId);
				this.mapActiveThreads.put(datiGroupBy, datiCollezionati); // registro nuova immagine
			}
			
			// incremento il numero di thread
			datiCollezionati.registerStartRequest(log, this.activePolicy);
									
			// mi salvo fuori dal synchronized l'attuale stato
			datiCollezionatiReaded = (DatiCollezionati) datiCollezionati.clone(); 
		
		}finally {
			this.lock.unlock();
		}
		
		return datiCollezionatiReaded;
	}
	
	
	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy) throws PolicyException,PolicyNotFoundException{
		
		DatiCollezionati datiCollezionatiReaded = null;
		
		this.lock.lock();
		try {

			DatiCollezionati datiCollezionati = null;
			if(this.mapActiveThreads.containsKey(datiGroupBy)==false){
				throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
			}
			else{
				datiCollezionati = this.mapActiveThreads.get(datiGroupBy);	
			}
			
			// incremento il numero dei contatori
			boolean updated = datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy);
									
			// mi salvo fuori dal synchronized l'attuale stato
			if(updated) {
				datiCollezionatiReaded = (DatiCollezionati) datiCollezionati.clone();
			}
			
		}finally {
			this.lock.unlock();
		}
		
		return datiCollezionatiReaded;
	}
	
	
	@Override
	public void registerStopRequest(Logger log, String idTransazione,IDUnivocoGroupByPolicy datiGroupBy, MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata) throws PolicyException,PolicyNotFoundException{

		this.lock.lock();
		try {
			
			if(this.mapActiveThreads.containsKey(datiGroupBy)==false){
				throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
			}
			else{
				DatiCollezionati datiCollezionati = this.mapActiveThreads.get(datiGroupBy);
				
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
			
		}finally {
			this.lock.unlock();
		}	
	}

	
	@Override
	public long getActiveThreads(){
		return this.getActiveThreads(null);
	}
	
	
	@Override
	public long getActiveThreads(IDUnivocoGroupByPolicy filtro){
		
		this.lock.lock();
		try {
			
			long counter = 0l;
			
			if(this.mapActiveThreads!=null && !this.mapActiveThreads.isEmpty()) {
				for (IDUnivocoGroupByPolicy datiGroupBy : this.mapActiveThreads.keySet()) {
					
					if(filtro!=null){
						IDUnivocoGroupBy<IDUnivocoGroupByPolicy> idAstype = (IDUnivocoGroupBy<IDUnivocoGroupByPolicy>) datiGroupBy;
						if(!idAstype.match(filtro)){
							continue;
						}
					}
					
					counter = counter + this.mapActiveThreads.get(datiGroupBy).getActiveRequestCounter();
				}
			}
			
			return counter;
		}finally {
			this.lock.unlock();
		}
	}
	
	
	@Override
	public String printInfos(Logger log, String separatorGroups) throws UtilsException{
		//synchronized (this.semaphore) {
		this.lock.lock();
		try {
			StringBuilder bf = new StringBuilder();
			if(this.mapActiveThreads!=null && !this.mapActiveThreads.isEmpty()) {
				for (IDUnivocoGroupByPolicy datiGroupBy : this.mapActiveThreads.keySet()) {
					bf.append(separatorGroups);
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
		}finally {
			this.lock.unlock();
		}
	}


	@Override
	public void remove() throws UtilsException {
		// TODO parla con andrea su come implementarla
		
	}

}
