package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

/**
 * Non usiamo lock distribuite e la concorrenza all'interno del nodo la gestiamo con una ConcurrentHashMap 
 * Visto che accettiamo un inconsistenza nei contatori, non cloniamo neanche.
 * 
 * @author Francesco Scarlato
 *
 */
public class PolicyGroupByActiveThreadsDistributedPuntualeNoLock  implements Serializable,IPolicyGroupByActiveThreadsInMemory {

	private static final long serialVersionUID = 1L;
	
	private final Map<IDUnivocoGroupByPolicy, DatiCollezionati> mapActiveThreads = new ConcurrentHashMap<IDUnivocoGroupByPolicy, DatiCollezionati>();


    private final HazelcastInstance hazelcast;
    private final String uniqueMapId;
	
	private final ActivePolicy activePolicy;
	private final TipoDatiCollezionati tipoDatiCollezionati;

	public PolicyGroupByActiveThreadsDistributedPuntualeNoLock(ActivePolicy activePolicy, String uniqueMapId, HazelcastInstance hazelcast, TipoDatiCollezionati tipoDatiCollezionati) {
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
		
		if(map!=null){
				for( var e : map.entrySet()) {
					this.mapActiveThreads.put(e.getKey(), BuilderDatiCollezionatiDistributed.build(this.tipoDatiCollezionati,e.getValue(), this.hazelcast, this.uniqueMapId));
				}
			}
	}
	
	
	@Override
	public void resetCounters(){
		
		if(this.mapActiveThreads.size()>0){
			Iterator<DatiCollezionati> datiCollezionati = this.mapActiveThreads.values().iterator();
			while (datiCollezionati.hasNext()) {
				DatiCollezionati item = (DatiCollezionati) datiCollezionati.next();
				item.resetCounters();
			}
		}
	}
	
	
	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy) throws PolicyException{
		
		DatiCollezionati datiCollezionati;
		
		datiCollezionati = this.mapActiveThreads.get(datiGroupBy);
	
		if (datiCollezionati == null){				
			datiCollezionati = BuilderDatiCollezionatiDistributed.build(this.tipoDatiCollezionati, this.activePolicy.getInstanceConfiguration().getUpdateTime(), this.hazelcast, this.uniqueMapId);
			this.mapActiveThreads.put(datiGroupBy, datiCollezionati); // registro nuova immagine
		}
			
		
		// incremento il numero di thread, fuori dal lock, perchè lavoriamo con atomic
		// e accettiamo inconsistenze
		datiCollezionati.registerStartRequest(log, this.activePolicy);
								
		return datiCollezionati;
	}
	
	
	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy) throws PolicyException,PolicyNotFoundException{
		
		DatiCollezionati datiCollezionati = this.mapActiveThreads.get(datiGroupBy);
		if(datiCollezionati == null) {
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
		}
		
		// incremento il numero dei contatori
		boolean updated = datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy);
								
		// mi salvo fuori dal synchronized l'attuale stato
		if(updated) {
			return datiCollezionati;
		}
			
		return null;
	}
	
	
	@Override
	public void registerStopRequest(Logger log, String idTransazione,IDUnivocoGroupByPolicy datiGroupBy, MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata) throws PolicyException,PolicyNotFoundException{

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

	
	@Override
	public long getActiveThreads(){
		return this.getActiveThreads(null);
	}
	
	
	@Override
	public long getActiveThreads(IDUnivocoGroupByPolicy filtro){
		
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
	
	
	@Override
	public String printInfos(Logger log, String separatorGroups) throws UtilsException{

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
	}


	@Override
	public void remove() throws UtilsException {
		// TODO Vedi con andrea come implementarla
		
	}

}
