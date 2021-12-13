/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupBy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreads;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**     
 * PolicyGroupByActiveThreads
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyGroupByActiveThreads implements Serializable,IPolicyGroupByActiveThreads {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<IDUnivocoGroupByPolicy, DatiCollezionati> mapActiveThreads = new HashMap<IDUnivocoGroupByPolicy, DatiCollezionati>();
	
	//private final Boolean semaphore = Boolean.valueOf(false);
	private final org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("PolicyGroupByActiveThreads");
	
	private ActivePolicy activePolicy;

	public PolicyGroupByActiveThreads(ActivePolicy activePolicy) {
		this.activePolicy = activePolicy;
	}
	
	
	public ActivePolicy getActivePolicy() {
		return this.activePolicy;
	}
	public Map<IDUnivocoGroupByPolicy, DatiCollezionati> getMapActiveThreads(){
		return this.mapActiveThreads;
	}
	
	public void resetCounters(){
		//synchronized (this.semaphore) {
		this.lock.acquireThrowRuntime("resetCounters");
		try {
			if(this.mapActiveThreads.size()>0){
				Iterator<DatiCollezionati> datiCollezionati = this.mapActiveThreads.values().iterator();
				while (datiCollezionati.hasNext()) {
					DatiCollezionati item = (DatiCollezionati) datiCollezionati.next();
					item.resetCounters();
				}
			}
		}finally {
			this.lock.release("resetCounters");
		}
	}
	
	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy) throws PolicyException{
				
		DatiCollezionati datiCollezionatiReaded = null;
		//System.out.println("<"+idTransazione+">registerStartRequest ...");
		//synchronized (this.semaphore) {
		this.lock.acquireThrowRuntime("registerStartRequest", idTransazione);
		try {
			//System.out.println("<"+idTransazione+">registerStartRequest entrato");
			
			DatiCollezionati datiCollezionati = null;
			if(this.mapActiveThreads.containsKey(datiGroupBy)){
				//System.out.println("<"+idTransazione+">registerStartRequest CHECK CONTAINS ["+datiGroupBy+"]=true");
				datiCollezionati = this.mapActiveThreads.get(datiGroupBy);	
			}
			else{
				//System.out.println("<"+idTransazione+">registerStartRequest CHECK CONTAINS ["+datiGroupBy+"]=false");
				datiCollezionati = new DatiCollezionati();
				//System.out.println("<"+idTransazione+">registerStartRequest PUT");
				this.mapActiveThreads.put(datiGroupBy, datiCollezionati); // registro nuova immagine
			}
			
			// incremento il numero di thread
			//System.out.println("<"+idTransazione+">registerStartRequest in datiCollezionati ...");
			datiCollezionati.registerStartRequest(log, this.activePolicy);
			//System.out.println("<"+idTransazione+">registerStartRequest in datiCollezionati ok: "+datiCollezionati.getActiveRequestCounter());
									
			// mi salvo fuori dal synchronized l'attuale stato
			datiCollezionatiReaded = (DatiCollezionati) datiCollezionati.clone(); 
		
			//System.out.println("<"+idTransazione+">registerStartRequest esco");
		}finally {
			this.lock.release("registerStartRequest", idTransazione);
		}
		
		
		// Tutti i restanti controlli sono effettuati usando il valore di datiCollezionatiReaded, che e' gia' stato modificato
		// Inoltre e' stato re-inserito nella map come oggetto nuovo, quindi il valore dentro il metodo non subira' trasformazioni (essendo stato fatto il clone)
		// E' possibile procedere con l'analisi rispetto al valore che possiedono il counter dentro questo scope.
		
		return datiCollezionatiReaded;

	}
	
	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy) throws PolicyException,PolicyNotFoundException{
		
		DatiCollezionati datiCollezionatiReaded = null;
		//System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile ...");
		//synchronized (this.semaphore) {
		this.lock.acquireThrowRuntime("updateDatiStartRequestApplicabile", idTransazione);
		try {
			//System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile entrato");
			
			DatiCollezionati datiCollezionati = null;
			if(this.mapActiveThreads.containsKey(datiGroupBy)==false){
				//System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
				throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
			}
			else{
				datiCollezionati = this.mapActiveThreads.get(datiGroupBy);	
			}
			
			// incremento il numero dei contatori
			//System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile updateDatiStartRequestApplicabile ...");
			boolean updated = datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy);
			//System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile updateDatiStartRequestApplicabile ok");
									
			// mi salvo fuori dal synchronized l'attuale stato
			if(updated) {
				datiCollezionatiReaded = (DatiCollezionati) datiCollezionati.clone();
			}
			
			//System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile esco");
		}finally {
			this.lock.release("updateDatiStartRequestApplicabile", idTransazione);
		}
		
		// Tutti i restanti controlli sono effettuati usando il valore di datiCollezionatiReaded, che e' gia' stato modificato
		// Inoltre e' stato re-inserito nella map come oggetto nuovo, quindi il valore dentro il metodo non subira' trasformazioni (essendo stato fatto il clone)
		// E' possibile procedere con l'analisi rispetto al valore che possiedono il counter dentro questo scope.
		
		return datiCollezionatiReaded;

	}
	
	@Override
	public void registerStopRequest(Logger log, String idTransazione,IDUnivocoGroupByPolicy datiGroupBy, MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata) throws PolicyException,PolicyNotFoundException{
		//System.out.println("<"+idTransazione+">registerStopRequest ...");
		//synchronized (this.semaphore) {
		this.lock.acquireThrowRuntime("registerStopRequest", idTransazione);
		try {
			//System.out.println("<"+idTransazione+">registerStopRequest entro");
			
			if(this.mapActiveThreads.containsKey(datiGroupBy)==false){
				//System.out.println("<"+idTransazione+">registerStopRequest Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
				throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
			}
			else{
				//System.out.println("<"+idTransazione+">registerStopRequest get ...");
				DatiCollezionati datiCollezionati = this.mapActiveThreads.get(datiGroupBy);	
				//System.out.println("<"+idTransazione+">registerStopRequest registerEndRequest ...");
				datiCollezionati.registerEndRequest(log, this.activePolicy, dati);
				//System.out.println("<"+idTransazione+">registerStopRequest registerEndRequest ok");
				if(isApplicabile){
					//System.out.println("<"+idTransazione+">registerStopRequest updateDatiEndRequestApplicabile ...");
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
					//System.out.println("<"+idTransazione+">registerStopRequest updateDatiEndRequestApplicabile ok");
				}
			}
			
			//System.out.println("<"+idTransazione+">registerStopRequest esco");
		}finally {
			this.lock.release("registerStopRequest", idTransazione);
		}	
	}

	
	public long getActiveThreads(){
		return this.getActiveThreads(null);
	}
	public long getActiveThreads(IDUnivocoGroupByPolicy filtro){
		
		//synchronized (this.semaphore) {
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
		}finally {
			this.lock.release("getActiveThreads");
		}
	}
	
	public String printInfos(Logger log, String separatorGroups) throws UtilsException{
		//synchronized (this.semaphore) {
		this.lock.acquireThrowRuntime("printInfos");
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
			this.lock.release("printInfos");
		}
	}
}
