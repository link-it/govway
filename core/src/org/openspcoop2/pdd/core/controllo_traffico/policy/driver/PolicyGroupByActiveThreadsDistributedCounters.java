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

package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupBy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicyMapId;
import org.openspcoop2.core.controllo_traffico.beans.IDatiCollezionatiDistributed;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.constants.Costanti;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreadsInMemory;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * PolicyGroupByActiveThreadsDistributedCounters
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyGroupByActiveThreadsDistributedCounters implements Serializable,IPolicyGroupByActiveThreadsInMemory {

	private static final long serialVersionUID = 1L;

	private transient org.openspcoop2.utils.Semaphore _lock = null; // usato solo in creazione e quando si accede a tutta la mappa
	private synchronized void initLock() {
		if(this._lock==null) {
			this._lock = new org.openspcoop2.utils.Semaphore("PolicyGroupByActiveThreadsDistributedCounters"); 
		}
	}
	public org.openspcoop2.utils.Semaphore getLock(){
		if(this._lock==null) {
			initLock();
		}
		return this._lock;
	}	
	
	private final java.util.Map<IDUnivocoGroupByPolicy, DatiCollezionati> mapActiveThreads = new HashMap<IDUnivocoGroupByPolicy, DatiCollezionati>();
	
	private String uniqueIdMap_idActivePolicy;
	@SuppressWarnings("unused")
	private Date uniqueIdMap_updateTime;
	
	private final ActivePolicy activePolicy;
	private final BuilderDatiCollezionatiDistributed builderDatiCollezionati;

	public PolicyGroupByActiveThreadsDistributedCounters(ActivePolicy activePolicy, String uniqueIdMap, BuilderDatiCollezionatiDistributed builder) throws PolicyException {
		this.activePolicy = activePolicy;
		this.builderDatiCollezionati = builder;
		
		this.uniqueIdMap_idActivePolicy = UniqueIdentifierUtilities.extractIdActivePolicy(uniqueIdMap);
		try {
			this.uniqueIdMap_updateTime = UniqueIdentifierUtilities.extractUpdateTimeActivePolicy(uniqueIdMap);
		}catch(Exception e) {
			throw new PolicyException(e.getMessage(),e);
		}
	}


	@Override
	public ActivePolicy getActivePolicy() {
		return this.activePolicy;
	}
	@Override
	public java.util.Map<IDUnivocoGroupByPolicy, DatiCollezionati> getMapActiveThreads(){
		return this.mapActiveThreads;
	}


	@Override
	public void initMap(java.util.Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) {
		SemaphoreLock slock = this.getLock().acquireThrowRuntime("initMap");
		try {
			if(map!=null && !map.isEmpty()) {
				for (IDUnivocoGroupByPolicy datiGroupBy : map.keySet()) {
					IDUnivocoGroupByPolicyMapId datiGroupByMapId = augmentIDUnivoco(datiGroupBy);
					DatiCollezionati dati = map.get(datiGroupBy);
					DatiCollezionati datiContatoriDistribuiti = this.builderDatiCollezionati.build(dati, datiGroupByMapId, this.activePolicy);
					this.mapActiveThreads.put(datiGroupByMapId, datiContatoriDistribuiti);
				}
			}
		}
		finally {
			this.getLock().release(slock, "initMap");
		}
	}


	@Override
	public void resetCounters(){

		SemaphoreLock slock = this.getLock().acquireThrowRuntime("resetCounters");
		try {
			if(this.mapActiveThreads.size()>0){
				Iterator<DatiCollezionati> datiCollezionati = this.mapActiveThreads.values().iterator();
				while (datiCollezionati.hasNext()) {
					DatiCollezionati item = datiCollezionati.next();
					item.resetCounters();
				}
			}
		}
		finally {
			this.getLock().release(slock, "resetCounters");
		}
	}
	
	
	@Override
	public void remove() throws UtilsException {
		SemaphoreLock slock = this.getLock().acquireThrowRuntime("remove");

		try {
			List<IDUnivocoGroupByPolicy> deleteList = new ArrayList<>();
			for (IDUnivocoGroupByPolicy datiGroupBy : this.mapActiveThreads.keySet()) {
				if(datiGroupBy instanceof IDUnivocoGroupByPolicyMapId){
					IDUnivocoGroupByPolicyMapId mapId = (IDUnivocoGroupByPolicyMapId) datiGroupBy;
					if(this.uniqueIdMap_idActivePolicy.equals(mapId.getUniqueMapId())) {
						deleteList.add(datiGroupBy);
					}
				}
			}
			while(!deleteList.isEmpty()) {
				IDUnivocoGroupByPolicy id = deleteList.remove(0);
				IDatiCollezionatiDistributed dati = (IDatiCollezionatiDistributed) this.mapActiveThreads.remove(id);	
				dati.destroyDatiDistribuiti();
			}
			
		} 	finally {
			this.getLock().release(slock, "remove");
		}
	}


	private DatiCollezionati initStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicyMapId datiGroupByMapId, Map<Object> ctx) throws PolicyException{
		SemaphoreLock slock = this.getLock().acquireThrowRuntime("initStartRequest");
		DatiCollezionati datiCollezionati = null;
		try {
			datiCollezionati = this.mapActiveThreads.get(datiGroupByMapId);
			if (datiCollezionati == null){				
				datiCollezionati = this.builderDatiCollezionati.build( 
						this.activePolicy.getInstanceConfiguration().getUpdateTime(), 
						datiGroupByMapId,
						this.activePolicy,
						ctx
						);

				this.mapActiveThreads.put(datiGroupByMapId, datiCollezionati); // registro nuova immagine
			}
			// La gestione dell'else è stata spostata dentro il costruttore degli oggetti DatiCollezionatiDistributedXXXX
/**			else {
//				if(datiCollezionati.getUpdatePolicyDate()!=null) {
//					if(!datiCollezionati.getUpdatePolicyDate().equals(this.activePolicy.getInstanceConfiguration().getUpdateTime())) {
//						// data aggiornata
//						datiCollezionati.resetCounters(this.activePolicy.getInstanceConfiguration().getUpdateTime());
//					}
//				}
//			}*/
			return datiCollezionati;
		}
		finally {
			this.getLock().release(slock, "initStartRequest");
		}
	}
	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx) throws PolicyException{

		IDUnivocoGroupByPolicyMapId datiGroupByMapId = augmentIDUnivoco(datiGroupBy);
		DatiCollezionati datiCollezionati = this.mapActiveThreads.get(datiGroupByMapId);
		if (datiCollezionati == null){				
			datiCollezionati = initStartRequest(log, idTransazione, datiGroupByMapId, ctx);
		}
		DatiCollezionati datiCollezionatiPerPolicyVerifier = datiCollezionati.newInstance(); // i valori utilizzati dal policy verifier verranno impostati con il valore remoto corretto
		// l'oggetto datiCollezionati, anche se appena creato, è già stato inizializzato dentro il costruttore di DatiCollezionatiXXX
		
		// incremento il numero di thread
		datiCollezionati.registerStartRequest(log, this.activePolicy, ctx, datiCollezionatiPerPolicyVerifier);
		
		return datiCollezionatiPerPolicyVerifier;

	}


	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx) throws PolicyException,PolicyNotFoundException{

		IDUnivocoGroupByPolicyMapId datiGroupByMapId = augmentIDUnivoco(datiGroupBy);
		DatiCollezionati datiCollezionati = this.mapActiveThreads.get(datiGroupByMapId);
		if(datiCollezionati == null) {
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupByMapId.toString()+"]");
		}
		DatiCollezionati datiCollezionatiPerPolicyVerifier = datiCollezionati.newInstance(); // i valori utilizzati dal policy verifier verranno impostati con il valore remoto corretto
		
		// incremento il numero dei contatori
		boolean updated = datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy, ctx, datiCollezionatiPerPolicyVerifier);

		// mi salvo fuori dal synchronized l'attuale stato
		if(updated) {
			return datiCollezionatiPerPolicyVerifier;
		}

		return null;
	}


	@Override
	public void registerStopRequest(Logger log, String idTransazione,IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx, 
			MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata) throws PolicyException,PolicyNotFoundException{

		IDUnivocoGroupByPolicyMapId datiGroupByMapId = augmentIDUnivoco(datiGroupBy);
		DatiCollezionati datiCollezionati =  this.mapActiveThreads.get(datiGroupByMapId);
		if(datiCollezionati == null) {
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupByMapId.toString()+"]");
		}
		
		datiCollezionati.registerEndRequest(log, this.activePolicy, ctx, dati);
		if(isApplicabile){

			List<Integer> esitiCodeOk = null;
			List<Integer> esitiCodeKoSenzaFaultApplicativo = null;
			List<Integer> esitiCodeFaultApplicativo = null;
			try {
				EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(log,dati.getProtocollo());
				esitiCodeOk = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
				esitiCodeKoSenzaFaultApplicativo = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo();
				esitiCodeFaultApplicativo = esitiProperties.getEsitiCodeFaultApplicativo();
			}catch(Exception e) {
				throw new PolicyException(e.getMessage(),e);
			}
			datiCollezionati.updateDatiEndRequestApplicabile(log, this.activePolicy, ctx, dati,
					esitiCodeOk,esitiCodeKoSenzaFaultApplicativo, esitiCodeFaultApplicativo, isViolata);
		}

	}


	@Override
	public long getActiveThreads(){
		return this.getActiveThreads(null);
	}
	@Override
	public long getActiveThreads(IDUnivocoGroupByPolicy filtro){

		SemaphoreLock slock = this.getLock().acquireThrowRuntime("getActiveThreads");
		try {
			long counter = 0l;

			if(this.mapActiveThreads!=null && !this.mapActiveThreads.isEmpty()) {
				for (IDUnivocoGroupByPolicy datiGroupBy : this.mapActiveThreads.keySet()) {
	
					if(filtro!=null){
						IDUnivocoGroupBy<IDUnivocoGroupByPolicy> idAstype = datiGroupBy;
						if(!idAstype.match(filtro)){
							continue;
						}
					}
	
					counter = counter + this.mapActiveThreads.get(datiGroupBy).getActiveRequestCounter();
				}
			}

			return counter;
		}
		finally {
			this.getLock().release(slock, "getActiveThreads");
		}

	}


	@Override
	public String printInfos(Logger log, String separatorGroups) throws UtilsException{

		SemaphoreLock slock = this.getLock().acquireThrowRuntime("printInfos");
		try {
			StringBuilder bf = new StringBuilder();
			if(this.mapActiveThreads!=null && !this.mapActiveThreads.isEmpty()) {
				for (IDUnivocoGroupByPolicy datiGroupBy : this.mapActiveThreads.keySet()) {
					bf.append(separatorGroups);
					bf.append("\n");
					bf.append(Costanti.LABEL_MODALITA_SINCRONIZZAZIONE).append(" ").append(this.builderDatiCollezionati.tipoPolicy.toLabel());
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
			this.getLock().release(slock, "printInfos");
		}

	}


	protected IDUnivocoGroupByPolicyMapId augmentIDUnivoco(IDUnivocoGroupByPolicy idUnivoco) {
		if(idUnivoco instanceof IDUnivocoGroupByPolicyMapId) {
			return (IDUnivocoGroupByPolicyMapId) idUnivoco;
		}
		else {
			return new IDUnivocoGroupByPolicyMapId(idUnivoco, this.uniqueIdMap_idActivePolicy); // NOTA: non serve gestirlo all'interno poichè verrà creato un nuovo identificativo //, this.uniqueIdMap_updateTime);
		}
	}
}
