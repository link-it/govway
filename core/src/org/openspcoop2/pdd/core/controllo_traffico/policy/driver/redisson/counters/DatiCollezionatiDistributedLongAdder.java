/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson.counters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicyMapId;
import org.openspcoop2.core.controllo_traffico.beans.IDatiCollezionatiDistributed;
import org.openspcoop2.core.controllo_traffico.constants.TipoControlloPeriodo;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.BuilderDatiCollezionatiDistributed;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLongAdder;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;

/**
 * Versione simile a quella del PNCounter per hazelcast. Abbiamo un contatore distribuito molto veloce, il LongAdder.
 * 
 * Per il PNCounter abbiamo le versioni incrementAndGet e addAndGet e in questo caso invece no.
 * Significa che durante gli incrementi, nella PNCCounter si setta subito anche il valore aggiornato che verrà poi consultato dal PolicyVerifier, poichè le get ritornano il valore in RAM.
 * Mentre in questa implementazione si aggiornano i contatori distributi senza poter aggiornare il valore in ram. 
 * Dopodichè una volta che lo si recupera tramite la 'sum()' potrebbero essere tornati anche risultati derivanti da altre richieste in corso.
 * Il PolicyVerifier valuterà quindi informazioni non consistenti.
 * 
 * Si sceglie inoltre di fare prima la sum() e poi l'incremento sia remoto che in ram, per evitare che una sum() effettuata dopo l'increment, ritorni il risultato di tutti gli increment degli altri thread e quindi il 429 immediato.
 * Per avere una atomicita almeno sul solito nodo si mette il lock su queste operazioni.
 * 
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiCollezionatiDistributedLongAdder  extends DatiCollezionati implements IDatiCollezionatiDistributed {
	
	private static final long serialVersionUID = 1L;
	
	private final org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedLongAdder");
	
	private final org.openspcoop2.utils.Semaphore lockActiveRequestCounterGetAndSum = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedLongAdder_ActiveRequestCounterGetAndSum");
	private final org.openspcoop2.utils.Semaphore lockRequestCounterGetAndSum = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedLongAdder_RequestCounterGetAndSum");
	private final org.openspcoop2.utils.Semaphore lockCounterGetAndSum = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedLongAdder_CounterGetAndSum");
	private final org.openspcoop2.utils.Semaphore lockDegradoPrestazionaleRequestCounterGetAndSum = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedLongAdder_DegradoPrestazionaleRequestCounterGetAndSum");
	private final org.openspcoop2.utils.Semaphore lockDegradoPrestazionaleCounterGetAndSum = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedLongAdder_DegradoPrestazionaleCounterGetAndSum");
	
	private final RedissonClient redisson;
	private final IDUnivocoGroupByPolicyMapId groupByPolicyMapId;
	private final int groupByPolicyMapId_hashCode;

	// data di registrazione/aggiornamento policy
	
	// final: sono i contatori che non dipendono da una finestra temporale

	// sono rimasti AtomicLong le date

	private final RAtomicLong distributedUpdatePolicyDate; // data di ultima modifica della policy
	private final RAtomicLong distributedPolicyDate; // intervallo corrente su cui vengono costruiti gli altri contatori	
	
	private RLongAdder distributedPolicyRequestCounter; // numero di richieste effettuato nell'intervallo
	private RLongAdder distributedPolicyCounter;  // utilizzato per tempi o banda
	
	private final RAtomicLong distributedPolicyDegradoPrestazionaleDate; // intervallo corrente su cui vengono costruiti gli altri contatori
	private RLongAdder distributedPolicyDegradoPrestazionaleRequestCounter; // numero di richieste effettuato nell'intervallo
	private RLongAdder distributedPolicyDegradoPrestazionaleCounter; // contatore del degrado
	
	private final boolean distribuitedActiveRequestCounter_policyRichiesteSimultanee;
	private final RLongAdder distributedActiveRequestCounterForStats; // numero di richieste simultanee
	private RLongAdder distributedActiveRequestCounterForCheck; // numero di richieste simultanee
	
	private RLongAdder distributedPolicyDenyRequestCounter; // policy bloccate
	
	// I contatori da eliminare 
	// Se si effettua il drop di un contatore quando si rileva il cambio di intervallo, potrebbe succedere che in un altro nodo del cluster che sta effettuando la fase di 'end'
	// non rilevi più il contatore e di fatto quindi lo riprende partenzo da 0. Poi a sua volta capisce il cambio di intervallo e lo rielimina.
	// per questo motivo, il drop viene effettuato al secondo cambio di intervallo, e ad ogni cambio i contatori vengono collezionati nel cestino
	private List<RLongAdder> cestino_policyCounters = new ArrayList<RLongAdder>();
	private List<RLongAdder> cestino_policyCountersDegradoPrestazionale = new ArrayList<RLongAdder>();
	
	private boolean initialized = false;

	private String getRLongAdderName(String name, Long date) {
		String t = "";
		if(date!=null) {
			t = date + "";
		}
		
		String configDate = BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
				(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1);
		
		return "longadder-"+this.groupByPolicyMapId_hashCode+"-"+name+t+configDate+"-rl"; // non modificare inizio e fine
	}
		
	public DatiCollezionatiDistributedLongAdder(Logger log, Date updatePolicyDate, Date gestorePolicyConfigDate, RedissonClient  redisson, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy) {
		super(updatePolicyDate, gestorePolicyConfigDate);
	
		this.redisson = redisson;
		this.groupByPolicyMapId = groupByPolicyMapId;
		this.groupByPolicyMapId_hashCode = this.groupByPolicyMapId.hashCode();
		
		this.initDatiIniziali(activePolicy);
		this.checkDate(log, activePolicy); // inizializza le date se ci sono
		
		this.distributedPolicyDate = this.initPolicyDate();
		this.distributedUpdatePolicyDate = this.initUpdatePolicyDate();
		
		this.distributedPolicyDegradoPrestazionaleDate = this.initPolicyDegradoPrestazionaleDate();

		this.distribuitedActiveRequestCounter_policyRichiesteSimultanee = activePolicy.getConfigurazionePolicy().isSimultanee() &&
				TipoControlloPeriodo.REALTIME.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo());
		if(this.distribuitedActiveRequestCounter_policyRichiesteSimultanee){
			this.distributedActiveRequestCounterForCheck = this.initActiveRequestCounters();
			this.distributedActiveRequestCounterForStats = null;
		}
		else {
			this.distributedActiveRequestCounterForStats = this.initActiveRequestCounters();
		}
		
		if(this.policyRealtime!=null && this.policyRealtime){
			initPolicyCounters(super.getPolicyDate().getTime());
		}
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			initPolicyCountersDegradoPrestazionale(super.getPolicyDegradoPrestazionaleDate().getTime());
		}
		
		// Gestisco la updatePolicyDate qui.
		// se updatePolicyDate è > this.distributedUpdatePolicyDate.get() allora resetto i contatori del cluster e setto la nuova data distribuita.
		// Questo per via di come funziona l'aggiornamento delle policy: i datiCollezionati correnti per una map<IDUnivoco..., DatiCollezionati> vengono cancellati e reinizializzati.
		// Per gli altri nodi in esecuzione, la updatePolicyDate locale resta sempre la stessa, ma non viene usata.
		if(this.policyRealtime!=null && this.policyRealtime){
			if (updatePolicyDate != null && this.distributedUpdatePolicyDate!=null && this.distributedUpdatePolicyDate.get() < updatePolicyDate.getTime()) {
				this.resetCounters(updatePolicyDate);
			}
		}
		
		this.initialized = true;
	}
	
	
	public DatiCollezionatiDistributedLongAdder(Logger log, DatiCollezionati dati, RedissonClient redisson, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy) {
		super(dati.getUpdatePolicyDate(), dati.getGestorePolicyConfigDate());
		
		// Inizializzo il padre
		dati.setValuesIn(this, false);
		
		this.redisson = redisson;
		this.groupByPolicyMapId = groupByPolicyMapId;
		this.groupByPolicyMapId_hashCode = this.groupByPolicyMapId.hashCode();
		
		this.distributedPolicyDate = this.initPolicyDate();
		this.distributedUpdatePolicyDate = this.initUpdatePolicyDate();
		
		this.distributedPolicyDegradoPrestazionaleDate = this.initPolicyDegradoPrestazionaleDate();

		this.distribuitedActiveRequestCounter_policyRichiesteSimultanee = activePolicy.getConfigurazionePolicy().isSimultanee() &&
				TipoControlloPeriodo.REALTIME.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo());
		if(this.distribuitedActiveRequestCounter_policyRichiesteSimultanee){
			this.distributedActiveRequestCounterForCheck = this.initActiveRequestCounters();
			this.distributedActiveRequestCounterForStats = null;
		}
		else {
			this.distributedActiveRequestCounterForStats = this.initActiveRequestCounters();
		}
		
		// Se non ho la policyDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if (super.getPolicyDate() != null) {

			// Non serve: essendo già persistente, si va a sommare un dato gia' esistente
			/*
			
			// Se ci sono altri nodi che stanno andando, la distributedPolicyDate DEVE essere != 0 
			// Se la policyDate è a zero vuol dire che questo è il primo nodo del cluster che sta inizializzando la policy per mezzo di un backup e che su tutto il cluster
			// non sono ancora transitate richieste.
			if (this.distributedPolicyDate.compareAndSet(0, super.getPolicyDate().getTime())) {
				// Se la data distribuita non era inizializzata e questo nodo l'ha settata, imposto i contatori come da immagine bin.
				//	Faccio la addAndGet, in quanto tutti valori positivi, non entriamo in conflitto con gli altri nodi che stanno effettuando lo startup nello stesso momento
				
				Long polDate = super.getPolicyDate().getTime();
				initPolicyCounters(polDate);
				
				Long getPolicyRequestCounter = super.getPolicyRequestCounter(true);
				if (getPolicyRequestCounter != null) {
					this.distributedPolicyRequestCounter.add(getPolicyRequestCounter);
				}
				
				Long getPolicyDenyRequestCounter = super.getPolicyDenyRequestCounter(true); // sarà comunque l'immagine del file
				if (getPolicyDenyRequestCounter != null) {
					this.distributedPolicyDenyRequestCounter.add(getPolicyDenyRequestCounter);
				}
				
				if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){	
					Long getPolicyCounter = super.getPolicyCounter(true);
					if (getPolicyCounter != null) {
						this.distributedPolicyCounter.add(getPolicyCounter);
					}
				}
				
				Long getActiveRequestCounter = super.getActiveRequestCounter(true); // sarà comunque l'immagine del file
				if (getActiveRequestCounter!=null && getActiveRequestCounter != 0) {
					if(this.distribuitedActiveRequestCounter_policyRichiesteSimultanee){
						this.distributedActiveRequestCounterForCheck.add(getActiveRequestCounter);
					}
					else {
						this.distributedActiveRequestCounterForStats.add(getActiveRequestCounter);
					}
				}
										
			} else {
			*/
				Long polDate = this.distributedPolicyDegradoPrestazionaleDate!=null ? this.distributedPolicyDegradoPrestazionaleDate.get() : null;
				initPolicyCounters(polDate);
				
			//}
		}
		
		// Se non ho la policyDegradoPrestazionaleDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			if (super.getPolicyDegradoPrestazionaleDate() != null) {
				
				// Non serve: essendo già persistente, si va a sommare un dato gia' esistente
				/*
				// Imposto i contatori distribuiti solo se nel frattempo non l'ha fatto un altro thread del cluster.
				if (this.distributedPolicyDegradoPrestazionaleDate.compareAndSet(0, super.getPolicyDegradoPrestazionaleDate().getTime())) {
					
					Long degradoPrestazionaleTime = super.getPolicyDegradoPrestazionaleDate().getTime();
					initPolicyCountersDegradoPrestazionale(degradoPrestazionaleTime);
									
					Long getPolicyDegradoPrestazionaleRequestCounter = super.getPolicyDegradoPrestazionaleRequestCounter(true);
					if (getPolicyDegradoPrestazionaleRequestCounter != null) {
						this.distributedPolicyDegradoPrestazionaleRequestCounter.add(getPolicyDegradoPrestazionaleRequestCounter);
					}
					
					Long getPolicyDegradoPrestazionaleCounter = super.getPolicyDegradoPrestazionaleCounter(true);
					if (getPolicyDegradoPrestazionaleCounter != null) {
						this.distributedPolicyDegradoPrestazionaleCounter.add(getPolicyDegradoPrestazionaleCounter);
					}
				}  else {
				*/	
					Long degradoPrestazionaleTime = this.distributedPolicyDate!=null ? this.distributedPolicyDate.get() : null;
					initPolicyCountersDegradoPrestazionale(degradoPrestazionaleTime);
					
				//}
			}
		}
		
		this.initialized = true;
	}
	
	private RAtomicLong initPolicyDate() {
		if(this.policyRealtime!=null && this.policyRealtime){
			return this.redisson.getAtomicLong(this.groupByPolicyMapId_hashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_POLICY_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
		}
		return null;
	}
	private RAtomicLong initUpdatePolicyDate() {
		if(this.policyRealtime!=null && this.policyRealtime){
			return this.redisson.getAtomicLong(this.groupByPolicyMapId_hashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_UPDATE_POLICY_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
		}
		return null;
	}
	private RAtomicLong initPolicyDegradoPrestazionaleDate() {
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			return this.redisson.getAtomicLong(this.groupByPolicyMapId_hashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_POLICY_DEGRADO_PRESTAZIONALE_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
		}
		return null;
	}
	
	private RLongAdder initActiveRequestCounters() {
		return this.redisson.getLongAdder(
				getRLongAdderName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_ACTIVE_REQUEST_COUNTER,
						(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1)));
	}
	
	private void initPolicyCounters(Long policyDate) {

		if(this.policyRealtime!=null && this.policyRealtime){
						
			this.distributedPolicyRequestCounter = this.redisson.getLongAdder(this.getRLongAdderName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_REQUEST_COUNTER,policyDate));
			
			this.distributedPolicyDenyRequestCounter = this.redisson.getLongAdder(this.getRLongAdderName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DENY_REQUEST_COUNTER,policyDate));
			
			if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
				this.distributedPolicyCounter = this.redisson.getLongAdder(this.getRLongAdderName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_COUNTER,policyDate));
			}
		}
		
	}
	
	private void initPolicyCountersDegradoPrestazionale(Long policyDate) {
		
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			this.distributedPolicyDegradoPrestazionaleCounter = this.redisson.getLongAdder(this.getRLongAdderName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_COUNTER,policyDate));

			this.distributedPolicyDegradoPrestazionaleRequestCounter = this.redisson.getLongAdder(this.getRLongAdderName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_REQUEST_COUNTER,policyDate));

		}	
		
	}
	
	

	@Override
	protected void resetPolicyCounterForDate(Date date) {
		
		if(this.initialized) {
			this.lock.acquireThrowRuntime("resetPolicyCounterForDate");
			try {
				long policyDate = date.getTime();
				long actual = this.distributedPolicyDate.get();
				long actualSuper = super.policyDate!=null ? super.policyDate.getTime() : -1;
				if(actualSuper!=policyDate && actual<policyDate && this.distributedPolicyDate.compareAndSet(actual, policyDate)) {					
				
					// Solo 1 nodo del cluster deve entrare in questo codice, altrimenti vengono fatti destroy più volte sullo stesso contatore
					// Potrà capitare che il cestino di un nodo non venga svuotato se si entra sempre sull'altro, cmq sia rimarrà 1 cestino con dei contatori di 1 intervallo.
					// Non appena ci entra poi li distruggerà.
					
					// effettuo il drop creati due intervalli indietro
					if(!this.cestino_policyCounters.isEmpty()) {
						for (RLongAdder rLongAdder : this.cestino_policyCounters) {
							rLongAdder.destroy();
						}
						this.cestino_policyCounters.clear();
					}
					
					if(this.distributedPolicyRequestCounter!=null || this.distributedPolicyDenyRequestCounter!=null || this.distributedPolicyCounter!=null) {
						// conservo precedenti contatori
						if(this.distributedPolicyRequestCounter!=null) {
							this.cestino_policyCounters.add(this.distributedPolicyRequestCounter);
						}
						if(this.distributedPolicyDenyRequestCounter!=null) {
							this.cestino_policyCounters.add(this.distributedPolicyDenyRequestCounter);
						}
						if(this.distributedPolicyCounter!=null) {
							this.cestino_policyCounters.add(this.distributedPolicyCounter);
						}
					}
										
				}

				if(actualSuper!=policyDate) {
					
					// Serve per inizializzare i nuovi riferimenti ai contatori
					initPolicyCounters(policyDate);
					
					// Serve per aggiornare la copia in ram del nodo in cui non si e' entrati nell'if precedente
					super.resetPolicyCounterForDate(date);
				}
				
			}finally {
				this.lock.release("resetPolicyCounterForDate");
			}
		}
		else {
			super.resetPolicyCounterForDate(date);
		}
	}
	
	
	@Override
	protected void resetPolicyCounterForDateDegradoPrestazionale(Date date) {
		
		if(this.initialized) {
			
			this.lock.acquireThrowRuntime("resetPolicyCounterForDateDegradoPrestazionale");
			try {
				long policyDate = date.getTime();
				long actual = this.distributedPolicyDate.get();
				long actualSuper = super.policyDegradoPrestazionaleDate!=null ? super.policyDegradoPrestazionaleDate.getTime() : -1;
				if(actualSuper!=policyDate && actual<policyDate && this.distributedPolicyDegradoPrestazionaleDate.compareAndSet(actual, policyDate)) {	
				
					// Solo 1 nodo del cluster deve entrare in questo codice, altrimenti vengono fatti destroy più volte sullo stesso contatore
					// Potrà capitare che il cestino di un nodo non venga svuotato se si entra sempre sull'altro, cmq sia rimarrà 1 cestino con dei contatori di 1 intervallo.
					// Non appena ci entra poi li distruggerà.
					
					// effettuo il drop creati due intervalli indietro
					if(!this.cestino_policyCountersDegradoPrestazionale.isEmpty()) {
						for (RLongAdder pnCounter : this.cestino_policyCountersDegradoPrestazionale) {
							pnCounter.destroy();
						}
						this.cestino_policyCountersDegradoPrestazionale.clear();
					}
					
					if(this.distributedPolicyRequestCounter!=null || this.distributedPolicyDenyRequestCounter!=null || this.distributedPolicyCounter!=null) {
						// conservo precedenti contatori
						if(this.distributedPolicyDegradoPrestazionaleCounter!=null) {
							this.cestino_policyCountersDegradoPrestazionale.add(this.distributedPolicyDegradoPrestazionaleCounter);
						}
						if(this.distributedPolicyDegradoPrestazionaleRequestCounter!=null) {
							this.cestino_policyCountersDegradoPrestazionale.add(this.distributedPolicyDegradoPrestazionaleRequestCounter);
						}
					}
										
				}
				
				if(actualSuper!=policyDate) {
					
					// Serve per inizializzare i nuovi riferimenti ai contatori
					initPolicyCountersDegradoPrestazionale(policyDate);
					
					// Serve per aggiornare la copia in ram del nodo in cui non si e' entrati nell'if precedente
					super.resetPolicyCounterForDateDegradoPrestazionale(date);
				}
				
			}finally {
				this.lock.release("resetPolicyCounterForDateDegradoPrestazionale");
			}
		}
		else {
			super.resetPolicyCounterForDateDegradoPrestazionale(date);
		}
	}
	
	
	@Override
	public void resetCounters(Date updatePolicyDate) {
		super.resetCounters(updatePolicyDate);
		
		if(updatePolicyDate!=null) {
			this.distributedUpdatePolicyDate.set(updatePolicyDate.getTime());
		}
		
		if (this.distributedPolicyDenyRequestCounter != null) {
			this.distributedPolicyDenyRequestCounter.reset();
		}
		
		if (this.distributedPolicyRequestCounter != null)  {
			this.distributedPolicyRequestCounter.reset();
		}
		
		if (this.distributedPolicyCounter != null) {
			this.distributedPolicyCounter.reset();
		}
		
		if (this.distributedPolicyDegradoPrestazionaleRequestCounter != null) {
			this.distributedPolicyDegradoPrestazionaleRequestCounter.reset();
		}
		
		if (this.distributedPolicyDegradoPrestazionaleCounter != null) {
			this.distributedPolicyDegradoPrestazionaleCounter.reset();
		}
		
	}
	
	
	// NOTA:  Si sceglie inoltre di fare prima la sum() e poi l'incremento sia remoto che in ram, per evitare che una sum() effettuata dopo l'increment, ritorni il risultato di tutti gli increment degli altri thread e quindi il 429 immediato.
	//        Per avere una atomicita almeno sul solito nodo si mette il lock su queste operazioni.
	//        Vedi commento in java doc della classe.
	
	@Override
	protected void _registerStartRequest_incrementActiveRequestCounter(DatiCollezionati datiCollezionatiPerPolicyVerifier) {
		if(this.distribuitedActiveRequestCounter_policyRichiesteSimultanee){
			this.lockActiveRequestCounterGetAndSum.acquireThrowRuntime("_registerStartRequest_incrementActiveRequestCounter");
			try {
				// prendo il valore. Non esiste un unico incrementAndGet
				if(datiCollezionatiPerPolicyVerifier!=null) {
					super.activeRequestCounter = datiCollezionatiPerPolicyVerifier.setAndGetActiveRequestCounter(this.distributedActiveRequestCounterForCheck.sum());
				}
				else {
					super.activeRequestCounter = this.distributedActiveRequestCounterForCheck.sum(); 
				}
				this.distributedActiveRequestCounterForCheck.increment();
				super.activeRequestCounter++;
			}finally {
				this.lockActiveRequestCounterGetAndSum.release("_registerStartRequest_incrementActiveRequestCounter");
			}
		}
		else {
			this.distributedActiveRequestCounterForStats.increment();
		}
	}
	
	
	@Override
	protected void _updateDatiStartRequestApplicabile_incrementRequestCounter(DatiCollezionati datiCollezionatiPerPolicyVerifier) {
		this.lockRequestCounterGetAndSum.acquireThrowRuntime("_updateDatiStartRequestApplicabile_incrementRequestCounter");
		try {
			// prendo il valore. Non esiste un unico incrementAndGet
			if(datiCollezionatiPerPolicyVerifier!=null) {
				super.policyRequestCounter = datiCollezionatiPerPolicyVerifier.setAndGetPolicyRequestCounter(this.distributedPolicyRequestCounter.sum());
			}
			else {
				super.policyRequestCounter = this.distributedPolicyRequestCounter.sum(); 
			}
			this.distributedPolicyRequestCounter.increment();
			super.policyRequestCounter++;
		}finally {
			this.lockRequestCounterGetAndSum.release("_updateDatiStartRequestApplicabile_incrementRequestCounter");
		}
	}
	
	
	@Override
	protected void _registerEndRequest_decrementActiveRequestCounter() {
		if(this.distribuitedActiveRequestCounter_policyRichiesteSimultanee){
			this.lockActiveRequestCounterGetAndSum.acquireThrowRuntime("_updateDatiStartRequestApplicabile_incrementRequestCounter");
			try {
				super.activeRequestCounter = this.distributedActiveRequestCounterForCheck.sum(); // prendo il valore. Non esiste un unico incrementAndGet
				this.distributedActiveRequestCounterForCheck.decrement();
				super.activeRequestCounter--;
			}finally {
				this.lockActiveRequestCounterGetAndSum.release("_updateDatiStartRequestApplicabile_incrementRequestCounter");
			}
		}
		else {
			this.distributedActiveRequestCounterForStats.decrement();
		}
	}
	@Override
	protected void _registerEndRequest_incrementDegradoPrestazionaleRequestCounter() {
		this.lockDegradoPrestazionaleRequestCounterGetAndSum.acquireThrowRuntime("_registerEndRequest_incrementDegradoPrestazionaleRequestCounter");
		try {
			super.policyDegradoPrestazionaleRequestCounter = this.distributedPolicyDegradoPrestazionaleRequestCounter.sum(); // prendo il valore. Non esiste un unico incrementAndGet
			this.distributedPolicyDegradoPrestazionaleRequestCounter.increment();
			super.policyDegradoPrestazionaleRequestCounter++;
		}finally {
			this.lockDegradoPrestazionaleRequestCounterGetAndSum.release("_registerEndRequest_incrementDegradoPrestazionaleRequestCounter");
		}
	}
	@Override
	protected void _registerEndRequest_incrementDegradoPrestazionaleCounter(long latenza) {
		this.lockDegradoPrestazionaleCounterGetAndSum.acquireThrowRuntime("_registerEndRequest_incrementDegradoPrestazionaleCounter");
		try {
			super.policyDegradoPrestazionaleCounter = this.distributedPolicyDegradoPrestazionaleCounter.sum(); // prendo il valore. Non esiste un unico incrementAndGet
			this.distributedPolicyDegradoPrestazionaleCounter.add(latenza);
			super.policyDegradoPrestazionaleCounter++;
		}finally {
			this.lockDegradoPrestazionaleCounterGetAndSum.release("_registerEndRequest_incrementDegradoPrestazionaleRequestCounter");
		}
	}
	
	
	@Override
	protected void _updateDatiEndRequestApplicabile_incrementRequestCounter() {
		this.lockRequestCounterGetAndSum.acquireThrowRuntime("_updateDatiEndRequestApplicabile_incrementRequestCounter");
		try {
			super.policyRequestCounter = this.distributedPolicyRequestCounter.sum(); // prendo il valore. Non esiste un unico incrementAndGet
			this.distributedPolicyRequestCounter.increment();
			super.policyRequestCounter++;
		}finally {
			this.lockRequestCounterGetAndSum.release("_updateDatiEndRequestApplicabile_incrementRequestCounter");
		}
	}
	@Override
	protected void _updateDatiEndRequestApplicabile_decrementRequestCounter() {
		this.lockRequestCounterGetAndSum.acquireThrowRuntime("_updateDatiEndRequestApplicabile_incrementRequestCounter");
		try {
			super.policyRequestCounter = this.distributedPolicyRequestCounter.sum(); // prendo il valore. Non esiste un unico incrementAndGet
			this.distributedPolicyRequestCounter.decrement();
			super.policyRequestCounter--;
		}finally {
			this.lockRequestCounterGetAndSum.release("_updateDatiEndRequestApplicabile_incrementRequestCounter");
		}
	}
	@Override
	protected void _updateDatiEndRequestApplicabile_incrementDenyRequestCounter() {
		this.distributedPolicyDenyRequestCounter.increment();
	}
	@Override
	protected void _updateDatiEndRequestApplicabile_incrementCounter(long v) {
		this.lockCounterGetAndSum.acquireThrowRuntime("_updateDatiEndRequestApplicabile_incrementCounter");
		try {
			super.policyCounter = this.distributedPolicyCounter.sum(); // prendo il valore. Non esiste un unico incrementAndGet
			this.distributedPolicyCounter.add(v);
			super.policyCounter+=v;
		}finally {
			this.lockCounterGetAndSum.release("_updateDatiEndRequestApplicabile_incrementCounter");
		}
	}
	
	
	
	
	@Override
	public void destroyDatiDistribuiti() {
		if(this.distributedPolicyDate!=null) {
			this.distributedPolicyDate.delete();
		}
		if(this.distributedUpdatePolicyDate!=null) {
			this.distributedUpdatePolicyDate.delete();
		}
		
		if(this.distributedPolicyRequestCounter!=null) {
			this.distributedPolicyRequestCounter.destroy();
		}
		if(this.distributedPolicyCounter!=null) {
			this.distributedPolicyCounter.destroy();
		}
		
		if(this.distributedPolicyDegradoPrestazionaleDate!=null) {
			this.distributedPolicyDegradoPrestazionaleDate.delete();
		}
		if(this.distributedPolicyDegradoPrestazionaleRequestCounter!=null) {
			this.distributedPolicyDegradoPrestazionaleRequestCounter.destroy();
		}
		if(this.distributedPolicyDegradoPrestazionaleCounter!=null) {
			this.distributedPolicyDegradoPrestazionaleCounter.destroy();
		}
		
		if(this.distributedActiveRequestCounterForStats!=null) {
			this.distributedActiveRequestCounterForStats.destroy();
		}
		if(this.distributedActiveRequestCounterForCheck!=null) {
			this.distributedActiveRequestCounterForCheck.destroy();
		}
		
		if(this.distributedPolicyDenyRequestCounter!=null) {
			this.distributedPolicyDenyRequestCounter.destroy();
		}
	}
	
	
	
	// Getters necessari poichè non viene aggiornato il field nella classe nonno DatiCollezionati, poichè i metodi increment, decrement e add non ritornano anche il valore
	
	@Override
	public Long getActiveRequestCounter(boolean readRemoteInfo) {
		if(this.distribuitedActiveRequestCounter_policyRichiesteSimultanee){
			if(readRemoteInfo) {
				return this.distributedActiveRequestCounterForCheck.sum();
			}
			else {
				return super.activeRequestCounter; // nelle operazioni di incremento/decremento l'ho aggiarnato via via e quindi il check utilizzerà questa informazione nel PolicyVerifier
			}
		}
		else {
			return this.distributedActiveRequestCounterForStats.sum();
		}
	}
	
	// Getters non necessari, sono utili solo se viene richiesta una lettura del dato remoto
	
	@Override
	public Long getPolicyDenyRequestCounter(boolean readRemoteInfo) {
		if(readRemoteInfo) {
			if(this.distributedPolicyDenyRequestCounter!=null) {
				return this.distributedPolicyDenyRequestCounter.sum();
			}
			else {
				return null;
			}
		}
		else {
			return super.getPolicyDenyRequestCounter(readRemoteInfo);
		}	
	}
	
	@Override
	public Long getPolicyRequestCounter(boolean readRemoteInfo) {
		if(readRemoteInfo) {
			if(this.distributedPolicyRequestCounter!=null) {
				return this.distributedPolicyRequestCounter.sum();
			}
			else {
				return null;
			}
		}
		else {
			return super.getPolicyRequestCounter(readRemoteInfo);
		}
	}
	@Override
	public Long getPolicyCounter(boolean readRemoteInfo) {
		if(readRemoteInfo) {
			if(this.distributedPolicyCounter!=null) {
				return this.distributedPolicyCounter.sum();
			}
			else {
				return null;
			}
		}
		else {
			return super.getPolicyRequestCounter(readRemoteInfo);
		}
	}	
	@Override
	public Long getPolicyDegradoPrestazionaleRequestCounter(boolean readRemoteInfo) {
		if(readRemoteInfo) {
			if(this.distributedPolicyDegradoPrestazionaleRequestCounter!=null) {
				return this.distributedPolicyDegradoPrestazionaleRequestCounter.sum();
			}
			else {
				return null;
			}
		}
		else {
			return super.getPolicyDegradoPrestazionaleRequestCounter(readRemoteInfo);
		}
	}
	@Override
	public Long getPolicyDegradoPrestazionaleCounter(boolean readRemoteInfo) {
		if(readRemoteInfo) {
			if(this.distributedPolicyDegradoPrestazionaleCounter!=null) {
				return this.distributedPolicyDegradoPrestazionaleCounter.sum();
			}
			else {
				return null;
			}
		}
		else {
			return super.getPolicyDegradoPrestazionaleCounter(readRemoteInfo);
		}
	}
	
}
