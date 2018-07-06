/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupBy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreads;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.protocol.utils.EsitiProperties;

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
	
	private Hashtable<IDUnivocoGroupByPolicy, DatiCollezionati> mapActiveThreads = new Hashtable<IDUnivocoGroupByPolicy, DatiCollezionati>();
	
	private Boolean semaphore = new Boolean(false);
		
	private ActivePolicy activePolicy;

	public PolicyGroupByActiveThreads(ActivePolicy activePolicy) {
		this.activePolicy = activePolicy;
	}
	
	
	public ActivePolicy getActivePolicy() {
		return this.activePolicy;
	}
	public Hashtable<IDUnivocoGroupByPolicy, DatiCollezionati> getMapActiveThreads(){
		return this.mapActiveThreads;
	}
	
	public void resetCounters(){
		synchronized (this.semaphore) {		
			if(this.mapActiveThreads.size()>0){
				Iterator<DatiCollezionati> datiCollezionati = this.mapActiveThreads.values().iterator();
				while (datiCollezionati.hasNext()) {
					DatiCollezionati item = (DatiCollezionati) datiCollezionati.next();
					item.resetCounters();
				}
			}
		}
	}
	
	@Override
	public DatiCollezionati registerStartRequest(Logger log, IDUnivocoGroupByPolicy datiGroupBy) throws PolicyException{
				
		DatiCollezionati datiCollezionatiReaded = null;
		synchronized (this.semaphore) {		
			DatiCollezionati datiCollezionati = null;
			if(this.mapActiveThreads.containsKey(datiGroupBy)){
				//System.out.println("CHECK CONTAINS ["+datiGroupBy+"]=true");
				datiCollezionati = this.mapActiveThreads.get(datiGroupBy);	
			}
			else{
				//System.out.println("CHECK CONTAINS ["+datiGroupBy+"]=false");
				datiCollezionati = new DatiCollezionati();
				//System.out.println("PUT COUNTER["+counter+"] in ["+datiRichiesta+"]");
				this.mapActiveThreads.put(datiGroupBy, datiCollezionati); // registro nuova immagine
			}
			
			// incremento il numero di thread
			datiCollezionati.registerStartRequest(log, this.activePolicy);
									
			// mi salvo fuori dal synchronized l'attuale stato
			datiCollezionatiReaded = (DatiCollezionati) datiCollezionati.clone(); 
			
		}
		
		// Tutti i restanti controlli sono effettuati usando il valore di datiCollezionatiReaded, che e' gia' stato modificato
		// Inoltre e' stato re-inserito nella map come oggetto nuovo, quindi il valore dentro il metodo non subira' trasformazioni (essendo stato fatto il clone)
		// E' possibile procedere con l'analisi rispetto al valore che possiedono il counter dentro questo scope.
		
		return datiCollezionatiReaded;

	}
	
	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, IDUnivocoGroupByPolicy datiGroupBy) throws PolicyException,PolicyNotFoundException{
		
		DatiCollezionati datiCollezionatiReaded = null;
		synchronized (this.semaphore) {		
			DatiCollezionati datiCollezionati = null;
			if(this.mapActiveThreads.containsKey(datiGroupBy)==false){
				//System.out.println("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiRichiesta.toString()+"]");
				throw new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
			}
			else{
				datiCollezionati = this.mapActiveThreads.get(datiGroupBy);	
			}
			
			// incremento il numero dei contatori
			datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy);
									
			// mi salvo fuori dal synchronized l'attuale stato
			datiCollezionatiReaded = (DatiCollezionati) datiCollezionati.clone(); 
			
		}
		
		// Tutti i restanti controlli sono effettuati usando il valore di datiCollezionatiReaded, che e' gia' stato modificato
		// Inoltre e' stato re-inserito nella map come oggetto nuovo, quindi il valore dentro il metodo non subira' trasformazioni (essendo stato fatto il clone)
		// E' possibile procedere con l'analisi rispetto al valore che possiedono il counter dentro questo scope.
		
		return datiCollezionatiReaded;

	}
	
	@Override
	public void registerStopRequest(Logger log,IDUnivocoGroupByPolicy datiGroupBy, MisurazioniTransazione dati, boolean isApplicabile) throws PolicyException,PolicyNotFoundException{
		synchronized (this.semaphore) {			
			if(this.mapActiveThreads.containsKey(datiGroupBy)==false){
				//System.out.println("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiRichiesta.toString()+"]");
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
						esitiCodeOk = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeOk();
						esitiCodeKo_senzaFaultApplicativo = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeKo_senzaFaultApplicativo();
						esitiCodeFaultApplicativo = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeFaultApplicativo();
					}catch(Exception e) {
						throw new PolicyException(e.getMessage(),e);
					}
					datiCollezionati.updateDatiEndRequestApplicabile(log, this.activePolicy, dati,
							esitiCodeOk,esitiCodeKo_senzaFaultApplicativo, esitiCodeFaultApplicativo);
				}
			}
		}		
	}

	
	public Long getActiveThreads(){
		return this.getActiveThreads(null);
	}
	public Long getActiveThreads(IDUnivocoGroupByPolicy filtro){
		
		synchronized (this.semaphore) {			
		
			Long counter = 0l;
			
			Enumeration<IDUnivocoGroupByPolicy> ids =this.mapActiveThreads.keys();
			while (ids.hasMoreElements()) {
				IDUnivocoGroupByPolicy datiGroupBy = 
						(IDUnivocoGroupByPolicy) ids.nextElement();
				
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
	}
	
	public String printInfos(Logger log, String separatorGroups) throws UtilsException{
		synchronized (this.semaphore) {
			StringBuffer bf = new StringBuffer();
			Enumeration<IDUnivocoGroupByPolicy> ids =this.mapActiveThreads.keys();
			while (ids.hasMoreElements()) {
				IDUnivocoGroupByPolicy datiGroupBy = 
						(IDUnivocoGroupByPolicy) ids.nextElement();
				bf.append(separatorGroups);
				bf.append("\n");
				bf.append("Criterio di Collezionamento dei Dati\n");
				bf.append(datiGroupBy.toString(true));
				bf.append("\n");
				this.mapActiveThreads.get(datiGroupBy).checkDate(log, this.activePolicy); // imposta correttamente gli intervalli
				bf.append(this.mapActiveThreads.get(datiGroupBy).toString());
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
	}
}
