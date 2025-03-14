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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupBy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.constants.Costanti;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreadsInMemory;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyDateUtils;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**     
 * PolicyGroupByActiveThreads
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyGroupByActiveThreads implements Serializable,IPolicyGroupByActiveThreadsInMemory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private java.util.Map<IDUnivocoGroupByPolicy, DatiCollezionati> mapActiveThreads = new HashMap<IDUnivocoGroupByPolicy, DatiCollezionati>();
	
	//private final Boolean semaphore = Boolean.valueOf(false);
	private transient org.openspcoop2.utils.Semaphore _lock = null;
	private synchronized void initLock() {
		if(this._lock==null) {
			this._lock = new org.openspcoop2.utils.Semaphore("PolicyGroupByActiveThreads"); 
		}
	}
	public org.openspcoop2.utils.Semaphore getLock(){
		if(this._lock==null) {
			initLock();
		}
		return this._lock;
	}
	
	private ActivePolicy activePolicy;
	private PolicyGroupByActiveThreadsType tipoGestore;

	public PolicyGroupByActiveThreads(ActivePolicy activePolicy, PolicyGroupByActiveThreadsType tipoGestore) {
		this.activePolicy = activePolicy;
		this.tipoGestore = tipoGestore;
	}
	
	
	@Override
	public ActivePolicy getActivePolicy() {
		return this.activePolicy;
	}
	@Override
	public java.util.Map<IDUnivocoGroupByPolicy, DatiCollezionati> getMapActiveThreads(){
		return this.mapActiveThreads;
	}
	
	
	public void setMapActiveThreads(java.util.Map<IDUnivocoGroupByPolicy, DatiCollezionati> value) {
		this.mapActiveThreads = value;
	}
	
	@Override
	public void initMap(java.util.Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) {
		/**synchronized (this.semaphore) {*/
		SemaphoreLock slock =this.getLock().acquireThrowRuntime("initMap");
		try {
			if(map!=null && map.size()>0){
				this.mapActiveThreads.putAll(map);
			}
		}finally {
			this.getLock().release(slock, "initMap");
		}
	}
	
	@Override
	public void resetCounters(){
		/**synchronized (this.semaphore) {*/
		SemaphoreLock slock =this.getLock().acquireThrowRuntime("resetCounters");
		try {
			if(this.mapActiveThreads.size()>0){
				Iterator<DatiCollezionati> datiCollezionati = this.mapActiveThreads.values().iterator();
				while (datiCollezionati.hasNext()) {
					DatiCollezionati item = datiCollezionati.next();
					item.resetCounters();
				}
			}
		}finally {
			this.getLock().release(slock, "resetCounters");
		}
	}
	
	@Override
	public void remove() throws UtilsException{
		// nop;
	}
	
	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx) throws PolicyException{
				
		DatiCollezionati datiCollezionatiReaded = null;
		/**System.out.println("<"+idTransazione+">registerStartRequest ...");*/
		/**synchronized (this.semaphore) {*/
		SemaphoreLock slock =this.getLock().acquireThrowRuntime("registerStartRequest", idTransazione);
		try {
			/**System.out.println("<"+idTransazione+">registerStartRequest entrato");*/
			
			DatiCollezionati datiCollezionati = null;
			if(this.mapActiveThreads.containsKey(datiGroupBy)){
				/**System.out.println("<"+idTransazione+">registerStartRequest CHECK CONTAINS ["+datiGroupBy+"]=true");*/
				datiCollezionati = this.mapActiveThreads.get(datiGroupBy);	
			}
			else{
				/**System.out.println("<"+idTransazione+">registerStartRequest CHECK CONTAINS ["+datiGroupBy+"]=false");*/
				Date gestorePolicyConfigDate = PolicyDateUtils.readGestorePolicyConfigDateIntoContext(ctx);
				datiCollezionati = new DatiCollezionati(this.activePolicy.getInstanceConfiguration().getUpdateTime(), gestorePolicyConfigDate);
				/**System.out.println("<"+idTransazione+">registerStartRequest PUT");*/
				this.mapActiveThreads.put(datiGroupBy, datiCollezionati); // registro nuova immagine
			}
			
			// incremento il numero di thread
			/**System.out.println("<"+idTransazione+">registerStartRequest in datiCollezionati ...");*/
			datiCollezionati.registerStartRequest(log, this.activePolicy, ctx);
			/**System.out.println("<"+idTransazione+">registerStartRequest in datiCollezionati ok: "+datiCollezionati.getActiveRequestCounter());*/
									
			// mi salvo fuori dal synchronized l'attuale stato
			datiCollezionatiReaded = datiCollezionati.newInstance(); 
		
			/**System.out.println("<"+idTransazione+">registerStartRequest esco");*/
		}finally {
			this.getLock().release(slock, "registerStartRequest", idTransazione);
		}
		
		
		// Tutti i restanti controlli sono effettuati usando il valore di datiCollezionatiReaded, che e' gia' stato modificato
		// Inoltre e' stato re-inserito nella map come oggetto nuovo, quindi il valore dentro il metodo non subira' trasformazioni (essendo stato fatto il clone)
		// E' possibile procedere con l'analisi rispetto al valore che possiedono il counter dentro questo scope.
		
		return datiCollezionatiReaded;

	}
	
	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx) throws PolicyException,PolicyNotFoundException{
		
		DatiCollezionati datiCollezionatiReaded = null;
		/**System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile ...");*/
		/**synchronized (this.semaphore) {*/
		SemaphoreLock slock =this.getLock().acquireThrowRuntime("updateDatiStartRequestApplicabile", idTransazione);
		try {
			/**System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile entrato");*/
			
			DatiCollezionati datiCollezionati = null;
			if(this.mapActiveThreads.containsKey(datiGroupBy)==false){
				/**System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");*/
				throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
			}
			else{
				datiCollezionati = this.mapActiveThreads.get(datiGroupBy);	
			}
			
			// incremento il numero dei contatori
			/**System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile updateDatiStartRequestApplicabile ...");*/
			boolean updated = datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy, ctx);
			/**System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile updateDatiStartRequestApplicabile ok");*/
									
			// mi salvo fuori dal synchronized l'attuale stato
			if(updated) {
				datiCollezionatiReaded = datiCollezionati.newInstance();
			}
			
			/**System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile esco");*/
		}finally {
			this.getLock().release(slock, "updateDatiStartRequestApplicabile", idTransazione);
		}
		
		// Tutti i restanti controlli sono effettuati usando il valore di datiCollezionatiReaded, che e' gia' stato modificato
		// Inoltre e' stato re-inserito nella map come oggetto nuovo, quindi il valore dentro il metodo non subira' trasformazioni (essendo stato fatto il clone)
		// E' possibile procedere con l'analisi rispetto al valore che possiedono il counter dentro questo scope.
		
		return datiCollezionatiReaded;

	}
	
	@Override
	public void registerStopRequest(Logger log, String idTransazione,IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx, 
			MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata) throws PolicyException,PolicyNotFoundException{
		/**System.out.println("<"+idTransazione+">registerStopRequest ...");*/
		/**synchronized (this.semaphore) {*/
		SemaphoreLock slock =this.getLock().acquireThrowRuntime("registerStopRequest", idTransazione);
		try {
			/**System.out.println("<"+idTransazione+">registerStopRequest entro");*/
			
			if(this.mapActiveThreads.containsKey(datiGroupBy)==false){
				/**System.out.println("<"+idTransazione+">registerStopRequest Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");*/
				throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
			}
			else{
				/**System.out.println("<"+idTransazione+">registerStopRequest get ...");*/
				DatiCollezionati datiCollezionati = this.mapActiveThreads.get(datiGroupBy);	
				/**System.out.println("<"+idTransazione+">registerStopRequest registerEndRequest ...");*/
				datiCollezionati.registerEndRequest(log, this.activePolicy, ctx, dati);
				/**System.out.println("<"+idTransazione+">registerStopRequest registerEndRequest ok");*/
				if(isApplicabile){
					/**System.out.println("<"+idTransazione+">registerStopRequest updateDatiEndRequestApplicabile ...");*/
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
					/**System.out.println("<"+idTransazione+">registerStopRequest updateDatiEndRequestApplicabile ok");*/
				}
			}
			
			/**System.out.println("<"+idTransazione+">registerStopRequest esco");*/
		}finally {
			this.getLock().release(slock, "registerStopRequest", idTransazione);
		}	
	}

	
	@Override
	public long getActiveThreads(){
		return this.getActiveThreads(null);
	}
	@Override
	public long getActiveThreads(IDUnivocoGroupByPolicy filtro){
		
		/**synchronized (this.semaphore) {*/
		SemaphoreLock slock =this.getLock().acquireThrowRuntime("getActiveThreads");
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
		}finally {
			this.getLock().release(slock, "getActiveThreads");
		}
	}
	
	@Override
	public String printInfos(Logger log, String separatorGroups) throws UtilsException{
		/**synchronized (this.semaphore) {*/
		SemaphoreLock slock =this.getLock().acquireThrowRuntime("printInfos");
		try {
			StringBuilder bf = new StringBuilder();
			if(this.mapActiveThreads!=null && !this.mapActiveThreads.isEmpty()) {
				for (IDUnivocoGroupByPolicy datiGroupBy : this.mapActiveThreads.keySet()) {
					bf.append(separatorGroups);
					bf.append("\n");
					bf.append(Costanti.LABEL_MODALITA_SINCRONIZZAZIONE).append(" ").append(this.tipoGestore.toLabel());
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
			this.getLock().release(slock, "printInfos");
		}
	}
}
