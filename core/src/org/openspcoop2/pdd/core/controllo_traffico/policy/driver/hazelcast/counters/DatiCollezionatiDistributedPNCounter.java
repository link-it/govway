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


package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.counters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicyMapId;
import org.openspcoop2.core.controllo_traffico.beans.IDatiCollezionatiDistributed;
import org.openspcoop2.core.controllo_traffico.constants.TipoControlloPeriodo;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.BuilderDatiCollezionatiDistributed;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.crdt.pncounter.PNCounter;
/**
 *  Con il PNCounter accettiamo qualche tipo di inconsistenza ovvero:
 *  
 * 	I valori dei contatori su di un nodo possono differire da quelle di un altro nodo perchè le notifiche sui contatori giunte ai due nodi fino al tempo T sono state diverse.
 * 	Dunque capita che lo stato locale non rifletta lo stato globale. 
 * 	I PNCounter garantiscono comunque che dopo un certo lasso di tempo il valore locale dei contatori converge al valore esatto per tutti i nodi, dalla documentazione infatti:
 * 
 * 	The counter guarantees that whenever two members have received the same set of updates, possibly in a different order, their state is identical, and any conflicting updates are merged automatically. 
 * 	If no new updates are made to the shared state, all members that can communicate will eventually have the same data.
 * 
 * 	The updates to the counter are applied locally when invoked on a CRDT replica.
 * 
 *  https://docs.hazelcast.com/imdg/latest/data-structures/pn-counter
 *
 * Attenzione:	Nel caso si diminuisse il numero delle repliche per il PNCounter, che di default è a INTEGER.MAX_VALUE per cui ogni membro ha la sua replica, 
 *	gli aggiornamenti potrebbero non raggiungere il membro che ha fatto partire la modifica, sollevando una ConsistencyLostException.
 *  
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiCollezionatiDistributedPNCounter extends DatiCollezionati implements IDatiCollezionatiDistributed {

	private static final long serialVersionUID = 1L;
	
	private final org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedPNCounter");
	
	private final HazelcastInstance hazelcast;
	private final IDUnivocoGroupByPolicyMapId groupByPolicyMapId;
	private final int groupByPolicyMapId_hashCode;

	// data di registrazione/aggiornamento policy
	
	// final: sono i contatori che non dipendono da una finestra temporale
	
	// sono rimasti AtomicLong le date
	
	private final IAtomicLong distributedUpdatePolicyDate; // data di ultima modifica della policy
	private final IAtomicLong distributedPolicyDate; // intervallo corrente su cui vengono costruiti gli altri contatori
	
	private PNCounter distributedPolicyRequestCounter; // numero di richieste effettuato nell'intervallo
	private PNCounter distributedPolicyCounter; // utilizzato per tempi o banda
		
	private final IAtomicLong distributedPolicyDegradoPrestazionaleDate; // intervallo corrente su cui vengono costruiti gli altri contatori
	private PNCounter distributedPolicyDegradoPrestazionaleRequestCounter; // numero di richieste effettuato nell'intervallo
	private PNCounter distributedPolicyDegradoPrestazionaleCounter; // contatore del degrado
	
	private final boolean distribuitedActiveRequestCounter_policyRichiesteSimultanee;
	private final PNCounter distributedActiveRequestCounterForStats; // numero di richieste simultanee
	private PNCounter distributedActiveRequestCounterForCheck; // numero di richieste simultanee
	
	private PNCounter distributedPolicyDenyRequestCounter; // policy bloccate
	
	// I contatori da eliminare 
	// Se si effettua il drop di un contatore quando si rileva il cambio di intervallo, potrebbe succedere che in un altro nodo del cluster che sta effettuando la fase di 'end'
	// non rilevi più il contatore e di fatto quindi lo riprende partenzo da 0. Poi a sua volta capisce il cambio di intervallo e lo rielimina.
	// per questo motivo, il drop viene effettuato al secondo cambio di intervallo, e ad ogni cambio i contatori vengono collezionati nel cestino
	private List<PNCounter> cestino_policyCounters = new ArrayList<PNCounter>();
	private List<PNCounter> cestino_policyCountersDegradoPrestazionale = new ArrayList<PNCounter>();
	
	private boolean initialized = false;

	private String getPNCounterName(String name, Long date) {
		String t = "";
		if(date!=null) {
			t = date + "";
		}
		
		String configDate = BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
				(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1);
		
		return "pncounter-"+this.groupByPolicyMapId_hashCode+"-"+name+t+configDate+"-rl"; // non modificare inizio e fine poichè configurato in hazelcast config
	}
	
	public DatiCollezionatiDistributedPNCounter(Logger log, Date updatePolicyDate, Date gestorePolicyConfigDate, HazelcastInstance hazelcast, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy) {
		super(updatePolicyDate, gestorePolicyConfigDate);
	
		this.hazelcast = hazelcast;
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
		
	public DatiCollezionatiDistributedPNCounter(Logger log, DatiCollezionati dati, HazelcastInstance hazelcast, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy) {
		super(dati.getUpdatePolicyDate(), dati.getGestorePolicyConfigDate());
		
		// Inizializzo il padre
		dati.setValuesIn(this, false);
		
		this.hazelcast = hazelcast;
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

			// Se ci sono altri nodi che stanno andando, la distributedPolicyDate DEVE essere != 0 
			// Se la policyDate è a zero vuol dire che questo è il primo nodo del cluster che sta inizializzando la policy per mezzo di un backup e che su tutto il cluster
			// non sono ancora transitate richieste.
			if (this.distributedPolicyDate!=null && this.distributedPolicyDate.compareAndSet(0, super.getPolicyDate().getTime())) {
				// Se la data distribuita non era inizializzata e questo nodo l'ha settata, imposto i contatori come da immagine bin.
				//	Faccio la addAndGet, in quanto tutti valori positivi, non entriamo in conflitto con gli altri nodi che stanno effettuando lo startup nello stesso momento
				
				Long polDate = super.getPolicyDate().getTime();
				initPolicyCounters(polDate);
				
				Long getPolicyRequestCounter = super.getPolicyRequestCounter(true);
				if (getPolicyRequestCounter != null) {
					this.distributedPolicyRequestCounter.addAndGet(getPolicyRequestCounter);
				}
				
				Long getPolicyDenyRequestCounter = super.getPolicyDenyRequestCounter(true);
				if (getPolicyDenyRequestCounter != null) {
					this.distributedPolicyDenyRequestCounter.addAndGet(getPolicyDenyRequestCounter);
				}
				
				if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
					Long getPolicyCounter = super.getPolicyCounter(true);
					if (getPolicyCounter != null) {
						this.distributedPolicyCounter.addAndGet(getPolicyCounter);
					}
				}
				
				Long getActiveRequestCounter = super.getActiveRequestCounter(true);
				if (getActiveRequestCounter!=null && getActiveRequestCounter != 0) {
					if(this.distribuitedActiveRequestCounter_policyRichiesteSimultanee){
						this.distributedActiveRequestCounterForCheck.addAndGet(getActiveRequestCounter);
					}
					else {
						if(this.distributedActiveRequestCounterForStats!=null) {
							this.distributedActiveRequestCounterForStats.addAndGet(getActiveRequestCounter);
						}
					}
				}
												
			} else {
				
				Long polDate = this.distributedPolicyDate!=null ? this.distributedPolicyDate.get() : null;
				initPolicyCounters(polDate);
				
			}
		}
		
		// Se non ho la policyDegradoPrestazionaleDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			if (super.getPolicyDegradoPrestazionaleDate() != null) {
				
				// Imposto i contatori distribuiti solo se nel frattempo non l'ha fatto un altro thread del cluster.
				if (this.distributedPolicyDegradoPrestazionaleDate!=null && this.distributedPolicyDegradoPrestazionaleDate.compareAndSet(0, super.getPolicyDegradoPrestazionaleDate().getTime())) {
					
					Long degradoPrestazionaleTime = super.getPolicyDegradoPrestazionaleDate().getTime();
					initPolicyCountersDegradoPrestazionale(degradoPrestazionaleTime);
					
					Long getPolicyDegradoPrestazionaleRequestCounter = super.getPolicyDegradoPrestazionaleRequestCounter(true);
					if (getPolicyDegradoPrestazionaleRequestCounter != null) {
						this.distributedPolicyDegradoPrestazionaleRequestCounter.addAndGet(getPolicyDegradoPrestazionaleRequestCounter);
					}
					
					Long getPolicyDegradoPrestazionaleCounter = super.getPolicyDegradoPrestazionaleCounter(true);
					if (getPolicyDegradoPrestazionaleCounter != null) {
						this.distributedPolicyDegradoPrestazionaleCounter.addAndGet(getPolicyDegradoPrestazionaleCounter);
					}
				}  else {
					
					Long degradoPrestazionaleTime = this.distributedPolicyDegradoPrestazionaleDate!=null ? this.distributedPolicyDegradoPrestazionaleDate.get() : null;
					initPolicyCountersDegradoPrestazionale(degradoPrestazionaleTime);
					
				}
			}
		}
		
		this.initialized = true;
	}
	
	private IAtomicLong initPolicyDate() {
		if(this.policyRealtime!=null && this.policyRealtime){
			return this.hazelcast.getCPSubsystem().getAtomicLong(this.groupByPolicyMapId_hashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_POLICY_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
		}
		return null;
	}
	private IAtomicLong initUpdatePolicyDate() {
		if(this.policyRealtime!=null && this.policyRealtime){
			return this.hazelcast.getCPSubsystem().getAtomicLong(this.groupByPolicyMapId_hashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_UPDATE_POLICY_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
		}
		return null;
	}
	private IAtomicLong initPolicyDegradoPrestazionaleDate() {
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			return this.hazelcast.getCPSubsystem().getAtomicLong(this.groupByPolicyMapId_hashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_POLICY_DEGRADO_PRESTAZIONALE_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
		}
		return null;
	}
	
	private PNCounter initActiveRequestCounters() {
		return this.hazelcast.getPNCounter(
				getPNCounterName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_ACTIVE_REQUEST_COUNTER,
						(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1)));
	}
	
	private void initPolicyCounters(Long policyDate) {

		if(this.policyRealtime!=null && this.policyRealtime){
						
			this.distributedPolicyRequestCounter = this.hazelcast.getPNCounter(this.getPNCounterName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_REQUEST_COUNTER,policyDate));
			
			this.distributedPolicyDenyRequestCounter = this.hazelcast.getPNCounter(this.getPNCounterName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DENY_REQUEST_COUNTER,policyDate));
			
			if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
				this.distributedPolicyCounter = this.hazelcast.getPNCounter(this.getPNCounterName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_COUNTER,policyDate));
			}
		}
	}
	
	private void initPolicyCountersDegradoPrestazionale(Long policyDate) {
		
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			this.distributedPolicyDegradoPrestazionaleCounter = this.hazelcast.getPNCounter(this.getPNCounterName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_COUNTER,policyDate));

			this.distributedPolicyDegradoPrestazionaleRequestCounter = this.hazelcast.getPNCounter(this.getPNCounterName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_REQUEST_COUNTER,policyDate));

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
						for (PNCounter pnCounter : this.cestino_policyCounters) {
							pnCounter.destroy();
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
						for (PNCounter pnCounter : this.cestino_policyCountersDegradoPrestazionale) {
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
			this.distributedPolicyDenyRequestCounter.subtractAndGet(this.distributedPolicyDenyRequestCounter.get());
		}
		
		if (this.distributedPolicyRequestCounter != null)  {
			this.distributedPolicyRequestCounter.subtractAndGet(this.distributedPolicyRequestCounter.get());
		}
		
		if (this.distributedPolicyCounter != null) {
			this.distributedPolicyCounter.subtractAndGet(this.distributedPolicyCounter.get());
		}
		
		if (this.distributedPolicyDegradoPrestazionaleRequestCounter != null) {
				this.distributedPolicyDegradoPrestazionaleRequestCounter.subtractAndGet(this.distributedPolicyDegradoPrestazionaleRequestCounter.get());
		}
		
		if (this.distributedPolicyDegradoPrestazionaleCounter != null) {
			this.distributedPolicyDegradoPrestazionaleCounter.subtractAndGet(this.distributedPolicyDegradoPrestazionaleCounter.get());
		}
		
	}
	
	
	
	@Override
	protected void _registerStartRequest_incrementActiveRequestCounter(DatiCollezionati datiCollezionatiPerPolicyVerifier) {
		if(this.distribuitedActiveRequestCounter_policyRichiesteSimultanee){
			if(datiCollezionatiPerPolicyVerifier!=null) {
				super.activeRequestCounter = datiCollezionatiPerPolicyVerifier.setAndGetActiveRequestCounter(this.distributedActiveRequestCounterForCheck.incrementAndGet());
			}
			else {
				super.activeRequestCounter = this.distributedActiveRequestCounterForCheck.incrementAndGet();
			}
		}
		else {
			super.activeRequestCounter = this.distributedActiveRequestCounterForStats.incrementAndGet();
		}
	}
	
	
	@Override
	protected void _updateDatiStartRequestApplicabile_incrementRequestCounter(DatiCollezionati datiCollezionatiPerPolicyVerifier) {
		if(datiCollezionatiPerPolicyVerifier!=null) {
			super.policyRequestCounter = datiCollezionatiPerPolicyVerifier.setAndGetPolicyRequestCounter(this.distributedPolicyRequestCounter.incrementAndGet());
		}
		else {
			super.policyRequestCounter = this.distributedPolicyRequestCounter.incrementAndGet();
		}
	}
	
	
	
	@Override
	protected void _registerEndRequest_decrementActiveRequestCounter() {
		if(this.distribuitedActiveRequestCounter_policyRichiesteSimultanee){
			super.activeRequestCounter = this.distributedActiveRequestCounterForCheck.decrementAndGet();
		}
		else {
			super.activeRequestCounter = this.distributedActiveRequestCounterForStats.decrementAndGet();
		}
	}
	@Override
	protected void _registerEndRequest_incrementDegradoPrestazionaleRequestCounter() {
		super.policyDegradoPrestazionaleRequestCounter = this.distributedPolicyDegradoPrestazionaleRequestCounter.incrementAndGet();
	}
	@Override
	protected void _registerEndRequest_incrementDegradoPrestazionaleCounter(long latenza) {
		super.policyDegradoPrestazionaleCounter = this.distributedPolicyDegradoPrestazionaleCounter.addAndGet(latenza);
	}
	
	
	@Override
	protected void _updateDatiEndRequestApplicabile_incrementRequestCounter() {
		super.policyRequestCounter = this.distributedPolicyRequestCounter.incrementAndGet();
	}
	@Override
	protected void _updateDatiEndRequestApplicabile_decrementRequestCounter() {
		super.policyRequestCounter = this.distributedPolicyRequestCounter.decrementAndGet();
	}
	@Override
	protected void _updateDatiEndRequestApplicabile_incrementDenyRequestCounter() {
		super.policyDenyRequestCounter = this.distributedPolicyDenyRequestCounter.incrementAndGet();
	}
	@Override
	protected void _updateDatiEndRequestApplicabile_incrementCounter(long v) {
		super.policyCounter = this.distributedPolicyCounter.addAndGet(v);
	}
	
	
	
	@Override
	public void destroyDatiDistribuiti() {
		if(this.distributedPolicyDate!=null) {
			this.distributedPolicyDate.destroy();
		}
		if(this.distributedUpdatePolicyDate!=null) {
			this.distributedUpdatePolicyDate.destroy();
		}
		
		if(this.distributedPolicyRequestCounter!=null) {
			this.distributedPolicyRequestCounter.destroy();
		}
		if(this.distributedPolicyCounter!=null) {
			this.distributedPolicyCounter.destroy();
		}
		
		if(this.distributedPolicyDegradoPrestazionaleDate!=null) {
			this.distributedPolicyDegradoPrestazionaleDate.destroy();
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
	
	
	
	// Getters non necessari, sono utili solo se viene richiesta una lettura del dato remoto
	
	@Override
	public Long getActiveRequestCounter(boolean readRemoteInfo) {
		if(readRemoteInfo) {
			if(this.distribuitedActiveRequestCounter_policyRichiesteSimultanee){
				return this.distributedActiveRequestCounterForCheck.get();	
			}
			else {
				return this.distributedActiveRequestCounterForStats.get();
			}
		}
		else {
			return super.getActiveRequestCounter(readRemoteInfo);
		}
	}
		
	@Override
	public Long getPolicyDenyRequestCounter(boolean readRemoteInfo) {
		if(readRemoteInfo) {
			if(this.distributedPolicyDenyRequestCounter!=null) {
				return this.distributedPolicyDenyRequestCounter.get();
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
				return this.distributedPolicyRequestCounter.get();
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
				return this.distributedPolicyCounter.get();
			}
			else {
				return null;
			}
		}
		else {
			return super.getPolicyCounter(readRemoteInfo);
		}
	}	
	
	@Override
	public Long getPolicyDegradoPrestazionaleRequestCounter(boolean readRemoteInfo) {
		if(readRemoteInfo) {
			if(this.distributedPolicyDegradoPrestazionaleRequestCounter!=null) {
				return this.distributedPolicyDegradoPrestazionaleRequestCounter.get();
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
				return this.distributedPolicyDegradoPrestazionaleCounter.get();
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
