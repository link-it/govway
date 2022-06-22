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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson;

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
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.singoli_contatori.IDatiCollezionatiDistributed;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;


/**
 * Scelgo questa implementazione, dove la versione distribuita eredita da DatiCollezionati e replica
 * i dati da distribuitre.
 * In questo modo, le varie PolicyGroupByActiveThreads* continueranno a restituire un oggetto di tipo DatiCollezionati. Con dati che sono presenti
 * nella ram del nodo locale, evitando così che il PolicyVerifier o altre classi che utilizzano i DatiCollezionatiDistributed vadano 
 * a fare richieste remote, ottenendo valori che non sono quelli che ci si aspettava dopo la richiesta.
 * 
 * @author Francesco Scarlato
 *
 */
public class DatiCollezionatiDistributedRedisAtomicLong extends DatiCollezionati implements IDatiCollezionatiDistributed{

	private static final long serialVersionUID = 1L;
	
	private final String uniquePrefixId;
	
	// data di registrazione/aggiornamento policy
	protected final RAtomicLong distributedUpdatePolicyDate;
	protected final RAtomicLong distributedPolicyDate;			
	
	protected RAtomicLong distributedPolicyRequestCounter;	
	protected RAtomicLong distributedPolicyCounter;						 // utilizzato per tempi o banda
	
	protected final RAtomicLong distributedPolicyDegradoPrestazionaleDate;	
	protected  RAtomicLong distributedPolicyDegradoPrestazionaleRequestCounter;
	protected RAtomicLong distributedPolicyDegradoPrestazionaleCounter;

	private final RedissonClient redisson;
	
	
	public DatiCollezionatiDistributedRedisAtomicLong(Date updatePolicyDate, RedissonClient redisson, String uniquePrefixId) {
		super(updatePolicyDate);
	
		this.redisson = redisson;
		this.uniquePrefixId = uniquePrefixId;
		this.distributedPolicyDate = this.redisson.getAtomicLong(this.uniquePrefixId+"-distributedPolicyDate");
		this.distributedUpdatePolicyDate = this.redisson.getAtomicLong(this.uniquePrefixId+"-distributedUpdatePolicyDate");
		this.distributedPolicyDegradoPrestazionaleDate = this.redisson.getAtomicLong(this.uniquePrefixId+"-distributedPolicyDegradoPrestazionaleDate");
		
		// Gestisco la updatePolicyDate qui.
		// se updatePolicyDate è > this.distributedUpdatePolicyDate.get() allora resetto i contatori del cluster e setto la nuova data distribuita.
		// Questo per via di come funziona l'aggiornamento delle policy: i datiCollezionati correnti per una map<IDUnivoco..., DatiCollezionati> vengono cancellati e reinizializzati.
		// Per gli altri nodi in esecuzione, la updatePolicyDate locale resta sempre la stessa, ma non viene usata.
		
		if (updatePolicyDate != null && this.distributedUpdatePolicyDate.get() < updatePolicyDate.getTime()) {
			this.resetCounters(updatePolicyDate);
		}
	}
	
	
	public DatiCollezionatiDistributedRedisAtomicLong(DatiCollezionati dati, RedissonClient redisson, String uniquePrefixId) {
		this(dati.getUpdatePolicyDate(), redisson, uniquePrefixId);
		
		// Inizializzo il padre con i valori in RAM, dopo uso 'super' per essere  sicuro di usare quelli
		dati.clone_impl(this);
		
		// Inizializzo la updatePolicyDate se necessario, ma questo devo farlo altrove.
		if (super.getUpdatePolicyDate() != null &&this.distributedUpdatePolicyDate.get() == 0) {
			this.distributedUpdatePolicyDate.compareAndSet(0,super.getUpdatePolicyDate().getTime());
		}
		
		// Se non ho la policyDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if (super.getPolicyDate() != null) {

			// Se ci sono altri nodi che stanno andando, la distributedPolicyDate DEVE essere != 0 
			if (this.distributedPolicyDate.compareAndSet(0, super.getPolicyDate().getTime())) {
				// Se la data distribuita non era inizializzata e questo nodo l'ha settata, imposto i contatori come da immagine bin.
				//	Faccio la addAndGet, in quanto tutti valori positivi, non entriamo in conflitto con gli altri nodi che stanno effettuando lo startup nello stesso momento
				
				this.distributedPolicyCounter = this.redisson.getAtomicLong(
						this.uniquePrefixId+"-distributedPolicyCounter"+super.getPolicyDate().getTime());
				
				this.distributedPolicyRequestCounter = this.redisson.getAtomicLong(
						this.uniquePrefixId+"-distributedPolicyRequestCounter"+super.getPolicyDate().getTime());
				
				if (super.getPolicyRequestCounter() != null) {
					this.distributedPolicyRequestCounter.addAndGet(super.getPolicyRequestCounter());
				}
				
				if (super.getPolicyCounter() != null) {
					this.distributedPolicyCounter.addAndGet(super.getPolicyCounter());
				}
								
			} else {
				Long polDate = this.distributedPolicyDate.get();
				this.distributedPolicyCounter = this.redisson.getAtomicLong(
						this.uniquePrefixId+"-distributedPolicyCounter"+polDate);
				
				this.distributedPolicyRequestCounter = this.redisson.getAtomicLong(
						this.uniquePrefixId+"-distributedPolicyRequestCounter"+polDate);
			}
		}
		
		// Se non ho la policyDegradoPrestazionaleDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if (super.getPolicyDegradoPrestazionaleDate() != null) {
			
			// Imposto i contatori distribuiti solo se nel frattempo non l'ha fatto un altro thread del cluster.
			if (this.distributedPolicyDegradoPrestazionaleDate.compareAndSet(0, super.getPolicyDegradoPrestazionaleDate().getTime())) {
				Long degradoPrestazionaleTime = super.getPolicyDegradoPrestazionaleDate().getTime();

				this.distributedPolicyDegradoPrestazionaleCounter = this.redisson.getAtomicLong(
						this.uniquePrefixId+"-distributedPolicyDegradoPrestazionaleCounter"+degradoPrestazionaleTime);
				
				this.distributedPolicyDegradoPrestazionaleRequestCounter = this.redisson.getAtomicLong(
						this.uniquePrefixId+"-distributedPolicyDegradoPrestazionaleRequestCounter"+degradoPrestazionaleTime);
				
				if (super.getPolicyDegradoPrestazionaleRequestCounter() != null) {
					this.distributedPolicyDegradoPrestazionaleRequestCounter.addAndGet(super.getPolicyDegradoPrestazionaleRequestCounter());
				}
				
				if (super.getPolicyDegradoPrestazionaleCounter() != null) {
					this.distributedPolicyDegradoPrestazionaleCounter.addAndGet(super.getPolicyDegradoPrestazionaleCounter());
				}
				
			}  else {
				Long degradoPrestazionaleTime = this.distributedPolicyDate.get();
				this.distributedPolicyDegradoPrestazionaleCounter = this.redisson.getAtomicLong(
						this.uniquePrefixId+"-distributedPolicyDegradoPrestazionaleCounter"+degradoPrestazionaleTime);
				
				this.distributedPolicyDegradoPrestazionaleRequestCounter = this.redisson.getAtomicLong(
						this.uniquePrefixId+"-distributedPolicyDegradoPrestazionaleRequestCounter"+degradoPrestazionaleTime);
			}
		}
		
	}
	
	@Override
	protected void resetPolicyCounterForDate(Date date) {

		Long policyDate = date.getTime();
		this.distributedPolicyDate.set(policyDate);
		
		// Prendo i nuovi contatori
		if(this.policyRealtime!=null && this.policyRealtime){
			this.distributedPolicyRequestCounter = this.redisson.getAtomicLong(
					this.uniquePrefixId+"-policyRequestCounter"+policyDate);
			
			if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
				this.distributedPolicyCounter = this.redisson.getAtomicLong(
						this.uniquePrefixId+"-policyCounter"+policyDate);
			}
		}
		
		// Resetto le copie in RAM
		super.resetPolicyCounterForDate(date);
	}
	
	
	@Override
	protected void resetPolicyCounterForDateDegradoPrestazionale(Date date) {
		
		this.distributedPolicyDegradoPrestazionaleDate.set(date.getTime());
		
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			this.distributedPolicyDegradoPrestazionaleCounter = this.redisson.getAtomicLong(
					this.uniquePrefixId+"-distributedPolicyDegradoPrestazionaleCounter"+date.getTime());
			
			this.distributedPolicyDegradoPrestazionaleRequestCounter = this.redisson.getAtomicLong(
					this.uniquePrefixId+"-distributedPolicyDegradoPrestazionaleRequestCounter"+date.getTime());
		}
		
		super.resetPolicyCounterForDateDegradoPrestazionale(date);
	}

	@Override
	protected void initDatiIniziali(ActivePolicy activePolicy){
		super.initDatiIniziali(activePolicy);
	}
	
	
	/*@Override
	public void registerStartRequest(Logger log, ActivePolicy activePolicy){
		
		if(super.getCreationDate() ==null){
			this.initDatiIniziali(activePolicy);
		}
		
		//super.setActiveRequestCounter(.incrementAndGet());
		
		if(this.getPolicyDateWindowInterval()!=null && 
				TipoFinestra.SCORREVOLE.equals(this.getPolicyDateWindowInterval())==false){
			
			this.checkPolicyCounterForDate(log,activePolicy);
		}

		if(this.getPolicyDegradoPrestazionaleDateWindowInterval()!=null && 
				TipoFinestra.SCORREVOLE.equals(this.getPolicyDegradoPrestazionaleDateWindowInterval())==false){
			
			this.checkPolicyCounterForDateDegradoPrestazionale(log,activePolicy);
			
			//if(this.policyDegradoPrestazionaleRealtime){
			// Essendo il degrado un tempo medio anche il numero di richieste lo devo incrementare quando aggiungo anche la latenza, 
			// senno poi la divisione (per l'avg) rispetto al numero di richieste è falsata
			// poiche' tengo conto anche delle richieste in corso, nonostante per queste non disponga ancora nella latenza
			// }
		}
		
	}*/
	
	
	@Override
	public void registerEndRequest(Logger log, ActivePolicy activePolicy, MisurazioniTransazione dati){
		
		if(super.getCreationDate()==null){
			return; // non inizializzato?
		}
		
		//super.setActiveRequestCounter(this.activeRequestCounter.decrementAndGet());
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
				super.setPolicyDegradoPrestazionaleRequestCounter(this.distributedPolicyDegradoPrestazionaleRequestCounter.incrementAndGet());
				super.setPolicyDegradoPrestazionaleCounter(this.distributedPolicyDegradoPrestazionaleCounter.addAndGet(latenza));
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
					super.setPolicyRequestCounter(this.distributedPolicyRequestCounter.incrementAndGet());
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
            		super.setPolicyRequestCounter(this.distributedPolicyRequestCounter.decrementAndGet());
            		//this.policyRequestCounter--; // l'avevo incrementato nello start
            	}
            	if(isViolata) {
            		// Aumento solamente il contatore della policy la quale ha bloccato la transazione
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
						super.setPolicyRequestCounter(this.distributedPolicyRequestCounter.incrementAndGet());
						break;
					}
				}
				break;
				
			case OCCUPAZIONE_BANDA:
				
				// viene misurata la banda "generata" dalle applicazioni
				//System.out.println("Incremento banda da "+this.policyCounter);
				super.setPolicyCounter(
						this.distributedPolicyCounter.addAndGet(this.getBanda(activePolicy.getConfigurazionePolicy().getValoreTipoBanda(), dati))
						);
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
						super.setPolicyCounter(
								this.distributedPolicyCounter.addAndGet(latenza)
								);
						//System.out.println("Incremento tempo a "+this.policyCounter);
						found = true;
						break;
					}
				}
			
				if(found && TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(activePolicy.getTipoRisorsaPolicy())){
					super.setPolicyRequestCounter(this.distributedPolicyRequestCounter.incrementAndGet());
				}
				
				break;
			}
		}
			
					
	}
	

	@Override
	public void resetCounters(Date updatePolicyDate) {
		super.resetCounters(updatePolicyDate);
		
		if(updatePolicyDate!=null) {
			this.distributedUpdatePolicyDate.set(updatePolicyDate.getTime());
		}
		
		if (this.distributedPolicyRequestCounter != null) {
			this.distributedPolicyRequestCounter.set(0l);
		}
		
		if (this.distributedPolicyCounter != null) {
			this.distributedPolicyCounter.set(0l);
		}

		if (this.distributedPolicyDegradoPrestazionaleRequestCounter != null) {
			this.distributedPolicyDegradoPrestazionaleRequestCounter.set(0l);
		}
		
		if (this.distributedPolicyDegradoPrestazionaleCounter != null) {
			this.distributedPolicyDegradoPrestazionaleCounter.set(0l);
		}
		
	}

	
	
	@Override
	public void destroyDatiDistribuiti() {
		this.distributedPolicyCounter.delete();
		this.distributedUpdatePolicyDate.delete();
		
		this.distributedPolicyRequestCounter.delete();	
		this.distributedPolicyCounter.delete();
		
		this.distributedPolicyDegradoPrestazionaleDate.delete();	
		this.distributedPolicyDegradoPrestazionaleRequestCounter.delete();
		this.distributedPolicyDegradoPrestazionaleCounter.delete();
	}
	

}
