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

package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupBy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicyMapId;
import org.openspcoop2.core.controllo_traffico.beans.IDatiCollezionatiDistributed;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.driver.CostantiControlloTraffico;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreadsInMemory;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.protocol.utils.EsitiProperties;
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

	private final org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("PolicyGroupByActiveThreadsDistributedCounters"); // usato solo in creazione e quando si accede a tutta la mappa
		
	private final Map<IDUnivocoGroupByPolicy, DatiCollezionati> mapActiveThreads = new HashMap<IDUnivocoGroupByPolicy, DatiCollezionati>();
	
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
	public Map<IDUnivocoGroupByPolicy, DatiCollezionati> getMapActiveThreads(){
		return this.mapActiveThreads;
	}


	@Override
	public void initMap(Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) {
		this.lock.acquireThrowRuntime("initMap");
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
			this.lock.release("initMap");
		}
	}


	@Override
	public void resetCounters(){

		this.lock.acquireThrowRuntime("resetCounters");
		try {
			if(this.mapActiveThreads.size()>0){
				Iterator<DatiCollezionati> datiCollezionati = this.mapActiveThreads.values().iterator();
				while (datiCollezionati.hasNext()) {
					DatiCollezionati item = (DatiCollezionati) datiCollezionati.next();
					item.resetCounters();
				}
			}
		}
		finally {
			this.lock.release("resetCounters");
		}
	}
	
	
	@Override
	public void remove() throws UtilsException {
		this.lock.acquireThrowRuntime("remove");

		try {
			List<IDUnivocoGroupByPolicy> deleteList = new ArrayList<IDUnivocoGroupByPolicy>();
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
			this.lock.release("remove");
		}
	}


	private DatiCollezionati initStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicyMapId datiGroupByMapId, Map<String, Object> ctx) throws PolicyException{
		this.lock.acquireThrowRuntime("initStartRequest");
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
//			else {
//				if(datiCollezionati.getUpdatePolicyDate()!=null) {
//					if(!datiCollezionati.getUpdatePolicyDate().equals(this.activePolicy.getInstanceConfiguration().getUpdateTime())) {
//						// data aggiornata
//						datiCollezionati.resetCounters(this.activePolicy.getInstanceConfiguration().getUpdateTime());
//					}
//				}
//			}
			return datiCollezionati;
		}
		finally {
			this.lock.release("initStartRequest");
		}
	}
	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<String, Object> ctx) throws PolicyException{

		IDUnivocoGroupByPolicyMapId datiGroupByMapId = augmentIDUnivoco(datiGroupBy);
		DatiCollezionati datiCollezionati = this.mapActiveThreads.get(datiGroupByMapId);
		if (datiCollezionati == null){				
			datiCollezionati = initStartRequest(log, idTransazione, datiGroupByMapId, ctx);
		}
		DatiCollezionati datiCollezionatiPerPolicyVerifier = (DatiCollezionati) datiCollezionati.clone(); // i valori utilizzati dal policy verifier verranno impostati con il valore remoto corretto
		// l'oggetto datiCollezionati, anche se appena creato, è già stato inizializzato dentro il costruttore di DatiCollezionatiXXX
		
		// incremento il numero di thread
		datiCollezionati.registerStartRequest(log, this.activePolicy, ctx, datiCollezionatiPerPolicyVerifier);
		
		return datiCollezionatiPerPolicyVerifier;

	}


	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<String, Object> ctx) throws PolicyException,PolicyNotFoundException{

		IDUnivocoGroupByPolicyMapId datiGroupByMapId = augmentIDUnivoco(datiGroupBy);
		DatiCollezionati datiCollezionati = this.mapActiveThreads.get(datiGroupByMapId);
		if(datiCollezionati == null) {
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupByMapId.toString()+"]");
		}
		DatiCollezionati datiCollezionatiPerPolicyVerifier = (DatiCollezionati) datiCollezionati.clone(); // i valori utilizzati dal policy verifier verranno impostati con il valore remoto corretto
		
		// incremento il numero dei contatori
		boolean updated = datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy, ctx, datiCollezionatiPerPolicyVerifier);

		// mi salvo fuori dal synchronized l'attuale stato
		if(updated) {
			return datiCollezionatiPerPolicyVerifier;
		}

		return null;
	}


	@Override
	public void registerStopRequest(Logger log, String idTransazione,IDUnivocoGroupByPolicy datiGroupBy, Map<String, Object> ctx, 
			MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata) throws PolicyException,PolicyNotFoundException{

		IDUnivocoGroupByPolicyMapId datiGroupByMapId = augmentIDUnivoco(datiGroupBy);
		DatiCollezionati datiCollezionati =  this.mapActiveThreads.get(datiGroupByMapId);
		if(datiCollezionati == null) {
			throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupByMapId.toString()+"]");
		}
		
		datiCollezionati.registerEndRequest(log, this.activePolicy, ctx, dati);
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
			datiCollezionati.updateDatiEndRequestApplicabile(log, this.activePolicy, ctx, dati,
					esitiCodeOk,esitiCodeKo_senzaFaultApplicativo, esitiCodeFaultApplicativo, isViolata);
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
					bf.append(CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE).append(" ").append(this.builderDatiCollezionati.tipoPolicy.toLabel());
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


	protected IDUnivocoGroupByPolicyMapId augmentIDUnivoco(IDUnivocoGroupByPolicy idUnivoco) {
		if(idUnivoco instanceof IDUnivocoGroupByPolicyMapId) {
			return (IDUnivocoGroupByPolicyMapId) idUnivoco;
		}
		else {
			return new IDUnivocoGroupByPolicyMapId(idUnivoco, this.uniqueIdMap_idActivePolicy); // NOTA: non serve gestirlo all'interno poichè verrà creato un nuovo identificativo //, this.uniqueIdMap_updateTime);
		}
	}
}
