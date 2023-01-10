/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

/**
 * Scelgo questa implementazione, dove la versione distribuita eredita da DatiCollezionati e replica
 * i dati da distribuitre.
 * In questo modo, le varie PolicyGroupByActiveThreads* continueranno a restituire un oggetto di tipo DatiCollezionati. Con dati che sono presenti
 * nella ram del nodo locale, evitando così che il PolicyVerifier o altre classi che utilizzano i DatiCollezionatiDistributed vadano 
 * a fare richieste remote, ottenendo valori che non sono quelli che ci si aspettava dopo la richiesta.
 * 
 *  CP_SUBSYSTEM (https://docs.hazelcast.com/imdg/4.2/cp-subsystem/cp-subsystem)
 *	Sottosistema hazelcast che garantisce consistenza e partizionamento per gli IAtomicLong e altri oggetti distribuiti (Non le MAP)
 *	Richide almeno tre nodi nel cluster.
 * - Strong Consistency: La vista dei dati è la stessa per tutti i nodi del cluster, garantida dal sistema CP
 * - Weak Consistency: Dopo un certo lasso di tempo senza aggiornamenti, la vista dei dati diventa la stessa per tutti i nodi del cluster
 * - Partitioning:
 *  	A partition is a communications break within a distributed system—a lost or temporarily delayed connection between two nodes.
 *   	Partition tolerance means that the cluster must continue to work despite any number of communication breakdowns between nodes in the system.	
 * 
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiCollezionatiDistributedAtomicLong extends DatiCollezionati implements IDatiCollezionatiDistributed{

	private static final long serialVersionUID = 1L;
	
	private final org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedAtomicLong");
	
	private final HazelcastInstance hazelcast;
	private final IDUnivocoGroupByPolicyMapId groupByPolicyMapId;
	private final int groupByPolicyMapId_hashCode;
	
	// data di registrazione/aggiornamento policy
	
	// final: sono i contatori che non dipendono da una finestra temporale
	
	protected final IAtomicLong distributedUpdatePolicyDate; // data di ultima modifica della policy
	protected final IAtomicLong distributedPolicyDate; // intervallo corrente su cui vengono costruiti gli altri contatori
	
	protected IAtomicLong distributedPolicyRequestCounter; // numero di richieste effettuato nell'intervallo
	protected IAtomicLong distributedPolicyCounter; // utilizzato per tempi o banda
	
	protected final IAtomicLong distributedPolicyDegradoPrestazionaleDate; // intervallo corrente su cui vengono costruiti gli altri contatori
	protected IAtomicLong distributedPolicyDegradoPrestazionaleRequestCounter; // numero di richieste effettuato nell'intervallo
	protected IAtomicLong distributedPolicyDegradoPrestazionaleCounter; // contatore del degrado
	
	protected final boolean distribuitedActiveRequestCounter_policyRichiesteSimultanee;
	protected final IAtomicLong distributedActiveRequestCounterForStats; // numero di richieste simultanee
	protected IAtomicLong distributedActiveRequestCounterForCheck; // numero di richieste simultanee
	
	protected IAtomicLong distributedPolicyDenyRequestCounter; // policy bloccate
	
	// I contatori da eliminare 
	// Se si effettua il drop di un contatore quando si rileva il cambio di intervallo, potrebbe succedere che in un altro nodo del cluster che sta effettuando la fase di 'end'
	// non rilevi più il contatore e di fatto quindi lo riprende partenzo da 0. Poi a sua volta capisce il cambio di intervallo e lo rielimina.
	// per questo motivo, il drop viene effettuato al secondo cambio di intervallo, e ad ogni cambio i contatori vengono collezionati nel cestino
	private List<IAtomicLong> cestino_policyCounters = new ArrayList<IAtomicLong>();
	private List<IAtomicLong> cestino_policyCountersDegradoPrestazionale = new ArrayList<IAtomicLong>();
		
	private boolean initialized = false;

	public DatiCollezionatiDistributedAtomicLong(Logger log, Date updatePolicyDate, Date gestorePolicyConfigDate, HazelcastInstance hazelcast, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy) {
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
			this.distributedActiveRequestCounterForCheck = initActiveRequestCounters();
			this.distributedActiveRequestCounterForStats = null;
		}
		else {
			this.distributedActiveRequestCounterForStats = initActiveRequestCounters();
		}
		
		if(this.policyRealtime!=null && this.policyRealtime){
			initPolicyCounters(super.getPolicyDate().getTime());
		}
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			initPolicyCountersDegradoPrestazionale(super.getPolicyDegradoPrestazionaleDate().getTime());
		}
		
		// Gestisco la updatePolicyDate qui, in questo modo risparmio un accesso in rete per ogni registerStartRequest.
		// se updatePolicyDate è > this.distributedUpdatePolicyDate.get() allora resetto i contatori del cluster e setto la nuova data distribuita.
		// Questo per via di come funziona l'aggiornamento delle policy: i datiCollezionati correnti per una map<IDUnivoco..., DatiCollezionati> vengono cancellati e reinizializzati.
		// Per gli altri nodi in esecuzione, la updatePolicyDate locale resta sempre la stessa, ma non viene usata.
		if(this.policyRealtime!=null && this.policyRealtime){
			if (updatePolicyDate != null && this.distributedUpdatePolicyDate.get() < updatePolicyDate.getTime()) {
				this.resetCounters(updatePolicyDate);
			}
		}
		
		this.initialized = true;
	}
		
	public DatiCollezionatiDistributedAtomicLong(Logger log, DatiCollezionati dati, HazelcastInstance hazelcast, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy) {
		super(dati.getUpdatePolicyDate(), dati.getGestorePolicyConfigDate());
		
		// Inizializzo il padre con i valori in RAM, dopo uso 'super' per essere  sicuro di usare quelli
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
			this.distributedActiveRequestCounterForCheck = initActiveRequestCounters();
			this.distributedActiveRequestCounterForStats = null;
		}
		else {
			this.distributedActiveRequestCounterForStats = initActiveRequestCounters();
		}
		
		
		// Se non ho la policyDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if (super.getPolicyDate() != null) {

			// Se ci sono altri nodi che stanno andando, la distributedPolicyDate DEVE essere != 0 
			if (this.distributedPolicyDate.compareAndSet(0, super.getPolicyDate().getTime())) {
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
					this.distributedPolicyDenyRequestCounter.set(getPolicyDenyRequestCounter);
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
						this.distributedActiveRequestCounterForCheck.set(getActiveRequestCounter);
					}
					else {
						this.distributedActiveRequestCounterForStats.set(getActiveRequestCounter);
					}
				}
								
			} else {
				
				Long polDate = this.distributedPolicyDate.get();
				initPolicyCounters(polDate);
				
			}
		}
		
		// Se non ho la policyDegradoPrestazionaleDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			if (super.getPolicyDegradoPrestazionaleDate() != null) {
				
				// Imposto i contatori distribuiti solo se nel frattempo non l'ha fatto un altro thread del cluster.
				if (this.distributedPolicyDegradoPrestazionaleDate.compareAndSet(0, super.getPolicyDegradoPrestazionaleDate().getTime())) {
					
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
					
					Long degradoPrestazionaleTime = this.distributedPolicyDegradoPrestazionaleDate.get();
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
	
	private IAtomicLong initActiveRequestCounters() {
		return this.hazelcast.getCPSubsystem().getAtomicLong(this.groupByPolicyMapId_hashCode+
				BuilderDatiCollezionatiDistributed.DISTRUBUITED_ACTIVE_REQUEST_COUNTER+
				(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
	}
	
	private void initPolicyCounters(Long policyDate) {

		if(this.policyRealtime!=null && this.policyRealtime){
						
			this.distributedPolicyRequestCounter = this.hazelcast.getCPSubsystem().getAtomicLong(
					this.groupByPolicyMapId_hashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_REQUEST_COUNTER+
					policyDate+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
			//System.out.println("INIT DATA["+org.openspcoop2.utils.date.DateManager.getDate()+"]["+org.openspcoop2.utils.date.DateManager.getTimeMillis()+"] cout-> ["+this.distributedPolicyRequestCounter.getName()+"]");
			
			this.distributedPolicyDenyRequestCounter = this.hazelcast.getCPSubsystem().getAtomicLong(
					this.groupByPolicyMapId_hashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DENY_REQUEST_COUNTER+
					policyDate+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
			//System.out.println("INIT DATA["+org.openspcoop2.utils.date.DateManager.getDate()+"]["+org.openspcoop2.utils.date.DateManager.getTimeMillis()+"] cout-> ["+this.distributedPolicyDenyRequestCounter.getName()+"]");
			
			if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
				this.distributedPolicyCounter = this.hazelcast.getCPSubsystem().getAtomicLong(
						this.groupByPolicyMapId_hashCode+
						BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_COUNTER+
						policyDate+
						BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
						(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
				//System.out.println("INIT DATA["+org.openspcoop2.utils.date.DateManager.getDate()+"]["+org.openspcoop2.utils.date.DateManager.getTimeMillis()+"] cout-> ["+this.distributedPolicyCounter.getName()+"]");
			}
		}
	}
	
	private void initPolicyCountersDegradoPrestazionale(Long policyDate) {
		
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			this.distributedPolicyDegradoPrestazionaleCounter = this.hazelcast.getCPSubsystem().getAtomicLong(
					this.groupByPolicyMapId_hashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_COUNTER+
					policyDate+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
			
			this.distributedPolicyDegradoPrestazionaleRequestCounter = this.hazelcast.getCPSubsystem().getAtomicLong(
					this.groupByPolicyMapId_hashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_REQUEST_COUNTER+
					policyDate+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
		}	
	}
	

	
	@Override
	protected void resetPolicyCounterForDate(Date date) {

		if(this.initialized) {
			
			this.lock.acquireThrowRuntime("resetPolicyCounterForDate");
			try {
				//System.out.println("RESET");
				long policyDate = date.getTime();
				long actual = this.distributedPolicyDate.get();
				long actualSuper = super.policyDate!=null ? super.policyDate.getTime() : -1;
				if(actualSuper!=policyDate && actual<policyDate && this.distributedPolicyDate.compareAndSet(actual, policyDate)) {					
				
					// Solo 1 nodo del cluster deve entrare in questo codice, altrimenti vengono fatti destroy più volte sullo stesso contatore
					// Potrà capitare che il cestino di un nodo non venga svuotato se si entra sempre sull'altro, cmq sia rimarrà 1 cestino con dei contatori di 1 intervallo.
					// Non appena ci entra poi li distruggerà.
					
					//System.out.println("RESET ENTRO!");
					
					// effettuo il drop creati due intervalli indietro
					if(!this.cestino_policyCounters.isEmpty()) {
						for (IAtomicLong iAtomicLong : this.cestino_policyCounters) {
							//System.out.println("DATA["+org.openspcoop2.utils.date.DateManager.getDate()+"]["+org.openspcoop2.utils.date.DateManager.getTimeMillis()+"] destroy ["+iAtomicLong.getName()+"]");
							iAtomicLong.destroy();
						}
						this.cestino_policyCounters.clear();
					}
					
					if(this.distributedPolicyRequestCounter!=null || this.distributedPolicyDenyRequestCounter!=null || this.distributedPolicyCounter!=null) {
						// conservo precedenti contatori
						if(this.distributedPolicyRequestCounter!=null) {
							this.cestino_policyCounters.add(this.distributedPolicyRequestCounter);
							//System.out.println("SALVO IN CESTINO DATA["+org.openspcoop2.utils.date.DateManager.getDate()+"]["+org.openspcoop2.utils.date.DateManager.getTimeMillis()+"] c ["+this.distributedPolicyRequestCounter.getName()+"]");
						}
						if(this.distributedPolicyDenyRequestCounter!=null) {
							this.cestino_policyCounters.add(this.distributedPolicyDenyRequestCounter);
							//System.out.println("SALVO IN CESTINO DATA["+org.openspcoop2.utils.date.DateManager.getDate()+"]["+org.openspcoop2.utils.date.DateManager.getTimeMillis()+"] c ["+this.distributedPolicyDenyRequestCounter.getName()+"]");
						}
						if(this.distributedPolicyCounter!=null) {
							this.cestino_policyCounters.add(this.distributedPolicyCounter);
							//System.out.println("SALVO IN CESTINO DATA["+org.openspcoop2.utils.date.DateManager.getDate()+"]["+org.openspcoop2.utils.date.DateManager.getTimeMillis()+"] c ["+this.distributedPolicyCounter.getName()+"]");
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
				long actual = this.distributedPolicyDegradoPrestazionaleDate.get();
				long actualSuper = super.policyDegradoPrestazionaleDate!=null ? super.policyDegradoPrestazionaleDate.getTime() : -1;
				if(actualSuper!=policyDate && actual<policyDate && this.distributedPolicyDegradoPrestazionaleDate.compareAndSet(actual, policyDate)) {	
				
					// Solo 1 nodo del cluster deve entrare in questo codice, altrimenti vengono fatti destroy più volte sullo stesso contatore
					// Potrà capitare che il cestino di un nodo non venga svuotato se si entra sempre sull'altro, cmq sia rimarrà 1 cestino con dei contatori di 1 intervallo.
					// Non appena ci entra poi li distruggerà.
					
					// effettuo il drop creati due intervalli indietro
					if(!this.cestino_policyCountersDegradoPrestazionale.isEmpty()) {
						for (IAtomicLong iAtomicLong : this.cestino_policyCountersDegradoPrestazionale) {
							iAtomicLong.destroy();
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
			this.distributedPolicyDenyRequestCounter.set(0);
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
			this.distributedActiveRequestCounterForStats.incrementAndGetAsync();
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
			this.distributedActiveRequestCounterForStats.decrementAndGetAsync();
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
		this.distributedPolicyDenyRequestCounter.incrementAndGetAsync();
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
	
	
	// Getters necessari poichè non viene aggiornato il field nella classe padre DatiCollezionati, poichè si usa il metodo Async nel caso di informazioni statistiche
	
	@Override
	public Long getActiveRequestCounter(boolean readRemoteInfo) {
		if(this.distribuitedActiveRequestCounter_policyRichiesteSimultanee){
			if(readRemoteInfo) {
				return this.distributedActiveRequestCounterForCheck.get();	
			}
			else {
				return super.activeRequestCounter; // nelle operazioni di incremento/decremento l'ho aggiarnato via via e quindi il check utilizzerà questa informazione nel PolicyVerifier
			}
		}
		else {
			return this.distributedActiveRequestCounterForStats.get();
		}
	}
	
	// Getters non necessari, sono utili solo se viene richiesta una lettura del dato remoto
	
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
