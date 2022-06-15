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


package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.singoli_contatori;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.constants.TipoFinestra;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * L'incremento e il decremento dei contatori viene fatto in maniera asincrona, il reset resta sincrono.
 * 
 * @author Francesco Scarlato
 *
 */
public class DatiCollezionatiDistributedAtomicLongAsync extends DatiCollezionatiDistributedAtomicLong {

	private static final long serialVersionUID = 1L;

	public DatiCollezionatiDistributedAtomicLongAsync(Date updatePolicyDate, HazelcastInstance hazelcast, String uniqueMapId) {
		super(updatePolicyDate, hazelcast, uniqueMapId);
	}
	
	
	public DatiCollezionatiDistributedAtomicLongAsync(DatiCollezionati dati, HazelcastInstance hazelcast, String uniqueMapId) {
		super(dati, hazelcast, uniqueMapId);
	}

	@Override
	public void registerEndRequest(Logger log, ActivePolicy activePolicy, MisurazioniTransazione dati){
		
		if(super.getCreationDate()==null){
			return; // non inizializzato?
		}
		
		super.setActiveRequestCounter(super.getActiveRequestCounter()-1);
				
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			
			long latenza = 0;
			
			int [] esitiValidi = null;
			
			if(TipoPdD.DELEGATA.equals(dati.getTipoPdD())){
				esitiValidi = activePolicy.getConfigurazioneControlloTraffico().getCalcoloLatenzaPortaDelegataEsitiConsiderati().get(dati.getProtocollo());
			}
			else{
				esitiValidi = activePolicy.getConfigurazioneControlloTraffico().getCalcoloLatenzaPortaApplicativaEsitiConsiderati().get(dati.getProtocollo());
			}
			boolean found = false;
			for (int esitoValido : esitiValidi) {
				if(dati.getEsitoTransazione() == esitoValido){
					latenza = this.getLatenza(activePolicy.getConfigurazionePolicy().getDegradoAvgTimeTipoLatenza(), dati);
					found = true;
					break;
				}
			}
			
			if(found){
				this.distributedPolicyDegradoPrestazionaleRequestCounter.incrementAndGetAsync();
				this.distributedPolicyDegradoPrestazionaleCounter.addAndGetAsync(latenza);
			}
			
		}
	}
	
	
	@Override
	public boolean updateDatiStartRequestApplicabile(Logger log, ActivePolicy activePolicy){
		
		if(this.getPolicyDateWindowInterval()!=null && 
				TipoFinestra.SCORREVOLE.equals(this.getPolicyDateWindowInterval())==false){
			
			if(this.policyRealtime){
				// il contatore delle richieste lo tengo per qualsiasi tipo di risorsa
				// Pero' per il tempo medio lo devo incrementare quando aggiungo anche la latenza, senno poi la divisione (per l'avg) rispetto al numero di richieste è falsata
				// poiche' tengo conto anche delle richieste in corso, nonostante per queste non disponga ancora nella latenza
				// Lo stesso per le richieste che dipendono dall'esito devo incrementarle solo quando conosco l'esito della transazione
				if( (TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(activePolicy.getTipoRisorsaPolicy()) == false) &&
						isRisorsaContaNumeroRichiesteDipendentiEsito(activePolicy.getTipoRisorsaPolicy())==false){
					this.distributedPolicyRequestCounter.incrementAndGetAsync();
					return true;
				}
			}
			
		}
		
		return false;

	}
	
	
	@Override
	public void updateDatiEndRequestApplicabile(Logger log, ActivePolicy activePolicy,
			MisurazioniTransazione dati,
			List<Integer> esitiCodeOk, List<Integer> esitiCodeKo_senzaFaultApplicativo, List<Integer> esitiCodeFaultApplicativo, 
			boolean isViolata) throws PolicyException{
		
		if(this.policyRealtime!=null && this.policyRealtime){
		
            int [] esitiPolicyViolate = activePolicy.getConfigurazioneControlloTraffico().getEsitiPolicyViolate().get(dati.getProtocollo());
            boolean foundEsitoDeny = false;
            for (int esitoViolato : esitiPolicyViolate) {
            	if(dati.getEsitoTransazione() == esitoViolato){
            		foundEsitoDeny = true;
            		break;
            	}
            }
            
            if(foundEsitoDeny){
            	if( (TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(activePolicy.getTipoRisorsaPolicy()) == false) &&
						isRisorsaContaNumeroRichiesteDipendentiEsito(activePolicy.getTipoRisorsaPolicy())==false){
            		this.distributedPolicyRequestCounter.decrementAndGetAsync();
            		//this.policyRequestCounter--; // l'avevo incrementato nello start
            	}
            	if(isViolata) {
            		// Aumento solamente il contatore della policy la quale ha bloccato la transazione
            		// TODO: chiedi ad andrea, distribuire?
            		super.setPolicyDenyRequestCounter(super.getPolicyDenyRequestCounter()+1);
            		//this.policyDenyRequestCounter++;
            	}
				return; // non incremento alcun contatore.
			}
            
			
			switch (activePolicy.getTipoRisorsaPolicy()) {
			
			case DIMENSIONE_MASSIMA_MESSAGGIO:
				
				// nop
				
				break;			
			
			case NUMERO_RICHIESTE:
				
				// nop
				
				break;
				
			case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
			case NUMERO_RICHIESTE_FALLITE:
			case NUMERO_FAULT_APPLICATIVI:
			case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
				
				List<Integer> esitiAppartenentiGruppo = null;
				try {
					if(TipoRisorsa.NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO.equals(activePolicy.getTipoRisorsaPolicy())) {
						//esitiAppartenentiGruppo = EsitiProperties.getInstance(log).getEsitiCodeOk();
						esitiAppartenentiGruppo = esitiCodeOk;
					}
					else if(TipoRisorsa.NUMERO_RICHIESTE_FALLITE.equals(activePolicy.getTipoRisorsaPolicy())) {
						//esitiAppartenentiGruppo = EsitiProperties.getInstance(log).getEsitiCodeKo_senzaFaultApplicativo();
						esitiAppartenentiGruppo = esitiCodeKo_senzaFaultApplicativo;
					}
					else if(TipoRisorsa.NUMERO_FAULT_APPLICATIVI.equals(activePolicy.getTipoRisorsaPolicy())) {
						//esitiAppartenentiGruppo = EsitiProperties.getInstance(log).getEsitiCodeFaultApplicativo();
						esitiAppartenentiGruppo = esitiCodeFaultApplicativo;
					}
					else {
						// NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI
						esitiAppartenentiGruppo = new ArrayList<Integer>();
						esitiAppartenentiGruppo.addAll(esitiCodeKo_senzaFaultApplicativo);
						esitiAppartenentiGruppo.addAll(esitiCodeFaultApplicativo);
					}
				}catch(Exception e) {
					throw new PolicyException(e.getMessage(),e);
				}
				for (int esitoAppartenenteGruppo : esitiAppartenentiGruppo) {
					if(dati.getEsitoTransazione() == esitoAppartenenteGruppo){
						this.distributedPolicyRequestCounter.incrementAndGetAsync();
						break;
					}
				}
				break;
				
			case OCCUPAZIONE_BANDA:
				
				// viene misurata la banda "generata" dalle applicazioni
				//System.out.println("Incremento banda da "+this.policyCounter);
				this.distributedPolicyCounter.addAndGetAsync(this.getBanda(activePolicy.getConfigurazionePolicy().getValoreTipoBanda(), dati));
				//System.out.println("Incremento banda a "+this.policyCounter);
								
				break;
	
				
			case TEMPO_COMPLESSIVO_RISPOSTA:
			case TEMPO_MEDIO_RISPOSTA:
				
				int [] esitiValidi = null;
				
				if(TipoPdD.DELEGATA.equals(dati.getTipoPdD())){
					esitiValidi = activePolicy.getConfigurazioneControlloTraffico().getCalcoloLatenzaPortaDelegataEsitiConsiderati().get(dati.getProtocollo());
				}
				else{
					esitiValidi = activePolicy.getConfigurazioneControlloTraffico().getCalcoloLatenzaPortaApplicativaEsitiConsiderati().get(dati.getProtocollo());
				}
				boolean found = false;
				for (int esitoValido : esitiValidi) {
					if(dati.getEsitoTransazione() == esitoValido){
						long latenza = this.getLatenza(activePolicy.getConfigurazionePolicy().getValoreTipoLatenza(), dati);
						//System.out.println("Incremento tempo da "+this.policyCounter);
						this.distributedPolicyCounter.addAndGetAsync(latenza);
						//System.out.println("Incremento tempo a "+this.policyCounter);
						found = true;
						break;
					}
				}
			
				if(found && TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(activePolicy.getTipoRisorsaPolicy())){
					this.distributedPolicyRequestCounter.incrementAndGetAsync();
				}
				
				break;
			}
		}
					
	}

	
	// Getters sincroni
	
	@Override
	public Long getPolicyRequestCounter() {
		return this.distributedPolicyRequestCounter.get();
	}
	@Override
	public Long getPolicyCounter() {
		return this.distributedPolicyCounter.get();
	}	
	@Override
	public Long getPolicyDegradoPrestazionaleRequestCounter() {
		return this.distributedPolicyDegradoPrestazionaleRequestCounter.get();
	}
	@Override
	public Long getPolicyDegradoPrestazionaleCounter() {
		return this.distributedPolicyDegradoPrestazionaleCounter.get();
	}

}
