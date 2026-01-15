/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.ActiveRequestDistributedIntervalManager;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.BuilderDatiCollezionatiDistributed;
import org.openspcoop2.utils.SemaphoreLock;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;

/**
 * Scelgo questa implementazione, dove la versione distribuita eredita da DatiCollezionati e replica
 * i dati da distribuitre.
 * In questo modo, le varie PolicyGroupByActiveThreads* continueranno a restituire un oggetto di tipo DatiCollezionati. Con dati che sono presenti
 * nella ram del nodo locale, evitando così che il PolicyVerifier o altre classi che utilizzano i DatiCollezionatiDistributed vadano 
 * a fare richieste remote, ottenendo valori che non sono quelli che ci si aspettava dopo la richiesta.
 * 
 *  CP_SUBSYSTEM (https://docs.hazelcast.com/hazelcast/5.3/cp-subsystem/cp-subsystem)
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
	
	private final transient org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedAtomicLong");

	protected final transient PolicyGroupByActiveThreadsType policyType;
	private final transient HazelcastInstance hazelcast;
	private final IDUnivocoGroupByPolicyMapId groupByPolicyMapId;
	private final int groupByPolicyMapIdHashCode;
	
	// data di registrazione/aggiornamento policy
	
	// final: sono i contatori che non dipendono da una finestra temporale
	
	protected final transient DatoAtomicLong distributedUpdatePolicyDate; // data di ultima modifica della policy
	protected final transient DatoAtomicLong distributedPolicyDate; // intervallo corrente su cui vengono costruiti gli altri contatori
	
	protected transient DatoAtomicLong distributedPolicyRequestCounter; // numero di richieste effettuato nell'intervallo
	protected transient DatoAtomicLong distributedPolicyCounter; // utilizzato per tempi o banda
	
	protected final transient DatoAtomicLong distributedPolicyDegradoPrestazionaleDate; // intervallo corrente su cui vengono costruiti gli altri contatori
	protected transient DatoAtomicLong distributedPolicyDegradoPrestazionaleRequestCounter; // numero di richieste effettuato nell'intervallo
	protected transient DatoAtomicLong distributedPolicyDegradoPrestazionaleCounter; // contatore del degrado
	
	protected final transient boolean distribuitedActiveRequestCounterPolicyRichiesteSimultanee;
	protected transient DatoAtomicLong distributedActiveRequestCounterForStats; // numero di richieste simultanee
	protected transient DatoAtomicLong distributedActiveRequestCounterForCheck; // numero di richieste simultanee

	// Gestione intervalli per richieste simultanee (per evitare contatori orfani in ambienti distribuiti)
	protected final transient int richiesteSimultaneeIntervalloSecondi; // intervallo in secondi, <= 0 disabilitato
	protected final transient DatoAtomicLong distributedActiveRequestCounterDate; // data intervallo corrente (solo se intervallo > 0)

	protected transient DatoAtomicLong distributedPolicyDenyRequestCounter; // policy bloccate

	// I contatori da eliminare
	// Se si effettua il drop di un contatore quando si rileva il cambio di intervallo, potrebbe succedere che in un altro nodo del cluster che sta effettuando la fase di 'end'
	// non rilevi più il contatore e di fatto quindi lo riprende partenzo da 0. Poi a sua volta capisce il cambio di intervallo e lo rielimina.
	// per questo motivo, il drop viene effettuato al secondo cambio di intervallo, e ad ogni cambio i contatori vengono collezionati nel cestino
	private transient List<DatoAtomicLong> cestinoPolicyCounters = new ArrayList<>();
	private transient List<DatoAtomicLong> cestinoPolicyCountersDegradoPrestazionale = new ArrayList<>();
	private transient List<DatoAtomicLong> cestinoActiveRequestCounters = new ArrayList<>();
		
	private boolean initialized = false;

	public DatiCollezionatiDistributedAtomicLong(Logger log, Date updatePolicyDate, Date gestorePolicyConfigDate, HazelcastInstance hazelcast, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy,
			PolicyGroupByActiveThreadsType policyType) {
		super(updatePolicyDate, gestorePolicyConfigDate);

		this.policyType = policyType;
		this.hazelcast = hazelcast;
		this.groupByPolicyMapId = groupByPolicyMapId;
		this.groupByPolicyMapIdHashCode = this.groupByPolicyMapId.hashCode();
		
		this.initDatiIniziali(activePolicy);
		this.checkDate(log, activePolicy); // inizializza le date se ci sono
		
		this.distributedPolicyDate = this.initPolicyDate();
		this.distributedUpdatePolicyDate = this.initUpdatePolicyDate();
		
		this.distributedPolicyDegradoPrestazionaleDate = this.initPolicyDegradoPrestazionaleDate();

		this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee = activePolicy.getConfigurazionePolicy().isSimultanee() &&
				TipoControlloPeriodo.REALTIME.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo());

		// Inizializza l'intervallo per le richieste (per tutte le policy, non solo richieste simultanee)
		this.richiesteSimultaneeIntervalloSecondi = ActiveRequestDistributedIntervalManager.getIntervalloSecondi();
		this.distributedActiveRequestCounterDate = initActiveRequestCounterDate();

		if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
			this.distributedActiveRequestCounterForCheck = initActiveRequestCounters(getActiveRequestCounterIntervalDate());
			this.distributedActiveRequestCounterForStats = null;
		}
		else {
			this.distributedActiveRequestCounterForStats = initActiveRequestCounters(getActiveRequestCounterIntervalDate());
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
		if(this.policyRealtime!=null && this.policyRealtime &&
			updatePolicyDate != null && this.distributedUpdatePolicyDate!=null && this.distributedUpdatePolicyDate.get() < updatePolicyDate.getTime()) {
			this.resetCounters(updatePolicyDate);
		}
		
		this.initialized = true;
	}
		
	public DatiCollezionatiDistributedAtomicLong(Logger log, DatiCollezionati dati, HazelcastInstance hazelcast, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy,
			PolicyGroupByActiveThreadsType policyType) {
		super(dati.getUpdatePolicyDate(), dati.getGestorePolicyConfigDate());

		if(log!=null) {
			// nop
		}

		// Inizializzo il padre con i valori in RAM, dopo uso 'super' per essere  sicuro di usare quelli
		dati.setValuesIn(this, false);

		this.policyType = policyType;
		this.hazelcast = hazelcast;
		this.groupByPolicyMapId = groupByPolicyMapId;
		this.groupByPolicyMapIdHashCode = this.groupByPolicyMapId.hashCode();
		
		this.distributedPolicyDate = this.initPolicyDate();
		this.distributedUpdatePolicyDate = this.initUpdatePolicyDate();
		
		this.distributedPolicyDegradoPrestazionaleDate = this.initPolicyDegradoPrestazionaleDate();

		this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee = activePolicy.getConfigurazionePolicy().isSimultanee() &&
				TipoControlloPeriodo.REALTIME.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo());

		// Inizializza l'intervallo per le richieste (per tutte le policy, non solo richieste simultanee)
		this.richiesteSimultaneeIntervalloSecondi = ActiveRequestDistributedIntervalManager.getIntervalloSecondi();
		this.distributedActiveRequestCounterDate = initActiveRequestCounterDate();

		if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
			this.distributedActiveRequestCounterForCheck = initActiveRequestCounters(getActiveRequestCounterIntervalDate());
			this.distributedActiveRequestCounterForStats = null;
		}
		else {
			this.distributedActiveRequestCounterForStats = initActiveRequestCounters(getActiveRequestCounterIntervalDate());
		}


		// Se non ho la policyDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if (super.getPolicyDate() != null) {

			// Se ci sono altri nodi che stanno andando, la distributedPolicyDate DEVE essere != 0 
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
					if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
						this.distributedActiveRequestCounterForCheck.set(getActiveRequestCounter);
					}
					else {
						if(this.distributedActiveRequestCounterForStats!=null) {
							this.distributedActiveRequestCounterForStats.set(getActiveRequestCounter);
						}
					}
				}
								
			} else {
				
				Long polDate = this.distributedPolicyDate!=null ? this.distributedPolicyDate.get() : null;
				initPolicyCounters(polDate);
				
			}
		}
		
		// Se non ho la policyDegradoPrestazionaleDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime &&
			super.getPolicyDegradoPrestazionaleDate() != null) {
				
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
		
		this.initialized = true;
	}

	private DatoAtomicLong initPolicyDate() {
		if(this.policyRealtime!=null && this.policyRealtime){
			return new DatoAtomicLong(this.hazelcast,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_POLICY_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.policyType);
		}
		return null;
	}
	private DatoAtomicLong initUpdatePolicyDate() {
		if(this.policyRealtime!=null && this.policyRealtime){
			return new DatoAtomicLong(this.hazelcast,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_UPDATE_POLICY_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.policyType);
		}
		return null;
	}
	private DatoAtomicLong initPolicyDegradoPrestazionaleDate() {
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			return new DatoAtomicLong(this.hazelcast,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_POLICY_DEGRADO_PRESTAZIONALE_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.policyType);
		}
		return null;
	}

	private DatoAtomicLong initActiveRequestCounterDate() {
		// Crea il contatore per la data dell'intervallo solo se la gestione a intervalli è abilitata
		if(this.richiesteSimultaneeIntervalloSecondi > 0) {
			return new DatoAtomicLong(this.hazelcast,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_POLICY_DATE+ // riuso lo stesso suffisso
					"activeRequest"+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.policyType);
		}
		return null;
	}

	@Override
	protected Long getActiveRequestCounterIntervalDate() {
		// Calcola la data dell'intervallo corrente per le richieste simultanee
		if(this.richiesteSimultaneeIntervalloSecondi <= 0) {
			return null;
		}

		long intervalStart = ActiveRequestDistributedIntervalManager.calcolaIntervalloCorrente(this.richiesteSimultaneeIntervalloSecondi);

		// Se esiste il contatore distribuito della data, verifica/aggiorna
		if(this.distributedActiveRequestCounterDate != null) {
			long distributedDate = this.distributedActiveRequestCounterDate.get();
			if(distributedDate == 0) {
				// Prima inizializzazione
				this.distributedActiveRequestCounterDate.compareAndSet(0, intervalStart);
				return intervalStart;
			} else if(distributedDate < intervalStart) {
				// L'intervallo è cambiato, prova ad aggiornare
				if(this.distributedActiveRequestCounterDate.compareAndSet(distributedDate, intervalStart)) {
					return intervalStart;
				}
				// Un altro nodo ha già aggiornato, leggi il nuovo valore
				return this.distributedActiveRequestCounterDate.get();
			}
			return distributedDate;
		}

		return intervalStart;
	}

	@Override
	protected String getPolicyIdForContext() {
		return String.valueOf(this.groupByPolicyMapIdHashCode);
	}

	private DatoAtomicLong initActiveRequestCounters(Long intervalDate) {
		// Se intervalDate è valorizzato, crea un contatore con timestamp nel nome (gestione a intervalli)
		// Altrimenti crea un contatore senza timestamp (comportamento legacy)
		if(intervalDate != null && intervalDate > 0) {
			return new DatoAtomicLong(this.hazelcast,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_ACTIVE_REQUEST_COUNTER+
					intervalDate+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.policyType);
		} else {
			return new DatoAtomicLong(this.hazelcast,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_ACTIVE_REQUEST_COUNTER+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.policyType);
		}
	}

	private void initPolicyCounters(Long policyDate) {

		if(this.policyRealtime!=null && this.policyRealtime){

			this.distributedPolicyRequestCounter = new DatoAtomicLong(this.hazelcast,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_REQUEST_COUNTER+
					policyDate+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.policyType);
			/**System.out.println("INIT DATA["+org.openspcoop2.utils.date.DateManager.getDate()+"]["+org.openspcoop2.utils.date.DateManager.getTimeMillis()+"] cout-> ["+this.distributedPolicyRequestCounter.getName()+"]");*/

			this.distributedPolicyDenyRequestCounter = new DatoAtomicLong(this.hazelcast,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DENY_REQUEST_COUNTER+
					policyDate+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.policyType);
			/**System.out.println("INIT DATA["+org.openspcoop2.utils.date.DateManager.getDate()+"]["+org.openspcoop2.utils.date.DateManager.getTimeMillis()+"] cout-> ["+this.distributedPolicyDenyRequestCounter.getName()+"]");*/

			if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
				this.distributedPolicyCounter = new DatoAtomicLong(this.hazelcast,
						this.groupByPolicyMapIdHashCode+
						BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_COUNTER+
						policyDate+
						BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
						(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
						this.policyType);
				/**System.out.println("INIT DATA["+org.openspcoop2.utils.date.DateManager.getDate()+"]["+org.openspcoop2.utils.date.DateManager.getTimeMillis()+"] cout-> ["+this.distributedPolicyCounter.getName()+"]");*/
			}
		}
	}
	
	private void initPolicyCountersDegradoPrestazionale(Long policyDate) {

		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			this.distributedPolicyDegradoPrestazionaleCounter = new DatoAtomicLong(this.hazelcast,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_COUNTER+
					policyDate+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.policyType);

			this.distributedPolicyDegradoPrestazionaleRequestCounter = new DatoAtomicLong(this.hazelcast,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_REQUEST_COUNTER+
					policyDate+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.policyType);
		}
	}
	

	
	@Override
	protected void resetPolicyCounterForDate(Date date) {

		if(this.initialized) {
			
			SemaphoreLock slock = this.lock.acquireThrowRuntime("resetPolicyCounterForDate");
			try {
				/**System.out.println("RESET");*/
				long policyDate = date.getTime();
				long actual = this.distributedPolicyDate.get();
				long actualSuper = super.policyDate!=null ? super.policyDate.getTime() : -1;
				if(actualSuper!=policyDate && actual<policyDate && this.distributedPolicyDate.compareAndSet(actual, policyDate)) {					
				
					// Solo 1 nodo del cluster deve entrare in questo codice, altrimenti vengono fatti destroy più volte sullo stesso contatore
					// Potrà capitare che il cestino di un nodo non venga svuotato se si entra sempre sull'altro, cmq sia rimarrà 1 cestino con dei contatori di 1 intervallo.
					// Non appena ci entra poi li distruggerà.
					
					/**System.out.println("RESET ENTRO!");*/
					
					// effettuo il drop creati due intervalli indietro
					if(!this.cestinoPolicyCounters.isEmpty()) {
						for (DatoAtomicLong iAtomicLong : this.cestinoPolicyCounters) {
							/**System.out.println("DATA["+org.openspcoop2.utils.date.DateManager.getDate()+"]["+org.openspcoop2.utils.date.DateManager.getTimeMillis()+"] destroy ["+iAtomicLong.getName()+"]");*/
							iAtomicLong.destroy();
						}
						this.cestinoPolicyCounters.clear();
					}
					
					if(this.distributedPolicyRequestCounter!=null || this.distributedPolicyDenyRequestCounter!=null || this.distributedPolicyCounter!=null) {
						// conservo precedenti contatori
						if(this.distributedPolicyRequestCounter!=null) {
							this.cestinoPolicyCounters.add(this.distributedPolicyRequestCounter);
							/**System.out.println("SALVO IN CESTINO DATA["+org.openspcoop2.utils.date.DateManager.getDate()+"]["+org.openspcoop2.utils.date.DateManager.getTimeMillis()+"] c ["+this.distributedPolicyRequestCounter.getName()+"]");*/
						}
						if(this.distributedPolicyDenyRequestCounter!=null) {
							this.cestinoPolicyCounters.add(this.distributedPolicyDenyRequestCounter);
							/**System.out.println("SALVO IN CESTINO DATA["+org.openspcoop2.utils.date.DateManager.getDate()+"]["+org.openspcoop2.utils.date.DateManager.getTimeMillis()+"] c ["+this.distributedPolicyDenyRequestCounter.getName()+"]");*/
						}
						if(this.distributedPolicyCounter!=null) {
							this.cestinoPolicyCounters.add(this.distributedPolicyCounter);
							/**System.out.println("SALVO IN CESTINO DATA["+org.openspcoop2.utils.date.DateManager.getDate()+"]["+org.openspcoop2.utils.date.DateManager.getTimeMillis()+"] c ["+this.distributedPolicyCounter.getName()+"]");*/
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
				this.lock.release(slock, "resetPolicyCounterForDate");
			}
			
		}
		else {
			super.resetPolicyCounterForDate(date);
		}
	}
	
	
	@Override
	protected void resetPolicyCounterForDateDegradoPrestazionale(Date date) {
		
		if(this.initialized) {
			
			SemaphoreLock slock = this.lock.acquireThrowRuntime("resetPolicyCounterForDateDegradoPrestazionale");
			try {
				long policyDate = date.getTime();
				long actual = this.distributedPolicyDegradoPrestazionaleDate.get();
				long actualSuper = super.policyDegradoPrestazionaleDate!=null ? super.policyDegradoPrestazionaleDate.getTime() : -1;
				if(actualSuper!=policyDate && actual<policyDate && this.distributedPolicyDegradoPrestazionaleDate.compareAndSet(actual, policyDate)) {	
				
					// Solo 1 nodo del cluster deve entrare in questo codice, altrimenti vengono fatti destroy più volte sullo stesso contatore
					// Potrà capitare che il cestino di un nodo non venga svuotato se si entra sempre sull'altro, cmq sia rimarrà 1 cestino con dei contatori di 1 intervallo.
					// Non appena ci entra poi li distruggerà.
					
					// effettuo il drop creati due intervalli indietro
					if(!this.cestinoPolicyCountersDegradoPrestazionale.isEmpty()) {
						for (DatoAtomicLong iAtomicLong : this.cestinoPolicyCountersDegradoPrestazionale) {
							iAtomicLong.destroy();
						}
						this.cestinoPolicyCountersDegradoPrestazionale.clear();
					}
					
					if(this.distributedPolicyRequestCounter!=null || this.distributedPolicyDenyRequestCounter!=null || this.distributedPolicyCounter!=null) {
						// conservo precedenti contatori
						if(this.distributedPolicyDegradoPrestazionaleCounter!=null) {
							this.cestinoPolicyCountersDegradoPrestazionale.add(this.distributedPolicyDegradoPrestazionaleCounter);
						}
						if(this.distributedPolicyDegradoPrestazionaleRequestCounter!=null) {
							this.cestinoPolicyCountersDegradoPrestazionale.add(this.distributedPolicyDegradoPrestazionaleRequestCounter);
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
				this.lock.release(slock, "resetPolicyCounterForDateDegradoPrestazionale");
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
	protected void internalRegisterStartRequestIncrementActiveRequestCounter(DatiCollezionati datiCollezionatiPerPolicyVerifier, org.openspcoop2.utils.Map<Object> ctx) {
		if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
			// Verifica se l'intervallo è cambiato (solo se la gestione a intervalli è abilitata)
			if(this.richiesteSimultaneeIntervalloSecondi > 0) {
				checkActiveRequestCounterIntervalChangeForCheck();
			}

			if(datiCollezionatiPerPolicyVerifier!=null) {
				super.activeRequestCounter = datiCollezionatiPerPolicyVerifier.setAndGetActiveRequestCounter(this.distributedActiveRequestCounterForCheck.incrementAndGet());
			}
			else {
				super.activeRequestCounter = this.distributedActiveRequestCounterForCheck.incrementAndGet();
			}

			// Salva la data dell'intervallo nel contesto DOPO l'increment
			// In caso di cambio intervallo durante la richiesta, potrebbe causare valori negativi
			// che sono preferibili ai positivi (negativi = permissivo, positivi = restrittivo)
			saveIntervalDateInContext(ctx);
		}
		else {
			// Verifica se l'intervallo è cambiato (solo se la gestione a intervalli è abilitata)
			if(this.richiesteSimultaneeIntervalloSecondi > 0) {
				checkActiveRequestCounterIntervalChangeForStats();
			}

			this.distributedActiveRequestCounterForStats.incrementAndGetAsync();

			// Salva la data dell'intervallo nel contesto DOPO l'increment
			saveIntervalDateInContext(ctx);
		}
	}

	/**
	 * Salva la data dell'intervallo corrente nel contesto.
	 * Deve essere chiamato DOPO l'increment.
	 */
	private void saveIntervalDateInContext(org.openspcoop2.utils.Map<Object> ctx) {
		if(ctx != null && this.richiesteSimultaneeIntervalloSecondi > 0) {
			Long intervalDate = getActiveRequestCounterIntervalDate();
			saveActiveRequestCounterIntervalDateInContext(ctx, intervalDate);
		}
	}

	/**
	 * Verifica se l'intervallo delle richieste simultanee è cambiato (per ForCheck).
	 * Se cambiato, crea un nuovo contatore con il nuovo timestamp e mette il vecchio nel cestino.
	 */
	protected void checkActiveRequestCounterIntervalChangeForCheck() {
		if(this.distributedActiveRequestCounterDate == null || this.distributedActiveRequestCounterForCheck == null) {
			return;
		}

		long distributedDate = this.distributedActiveRequestCounterDate.get();
		if(!ActiveRequestDistributedIntervalManager.isIntervalloCambiato(this.richiesteSimultaneeIntervalloSecondi, distributedDate)) {
			return;
		}

		long currentIntervalStart = ActiveRequestDistributedIntervalManager.calcolaIntervalloCorrente(this.richiesteSimultaneeIntervalloSecondi);

		// L'intervallo è cambiato
		SemaphoreLock slock = this.lock.acquireThrowRuntime("checkActiveRequestCounterIntervalChangeForCheck");
		try {
			// Rileggi il valore dopo aver acquisito il lock
			distributedDate = this.distributedActiveRequestCounterDate.get();
			if(ActiveRequestDistributedIntervalManager.isIntervalloCambiato(this.richiesteSimultaneeIntervalloSecondi, distributedDate)) {
				// Prova ad aggiornare la data
				if(this.distributedActiveRequestCounterDate.compareAndSet(distributedDate, currentIntervalStart)) {
					// Svuota il cestino (contatori di 2 intervalli fa)
					if(!this.cestinoActiveRequestCounters.isEmpty()) {
						for (DatoAtomicLong counter : this.cestinoActiveRequestCounters) {
							/**System.out.println("DESTROY ACTIVE ["+counter.getName()+"]");*/
							counter.destroy();
						}
						this.cestinoActiveRequestCounters.clear();
					}

					// Metti il vecchio contatore nel cestino
					this.cestinoActiveRequestCounters.add(this.distributedActiveRequestCounterForCheck);

					// Crea il nuovo contatore con il nuovo timestamp
					this.distributedActiveRequestCounterForCheck = initActiveRequestCounters(currentIntervalStart);
				} else {
					// Un altro thread ha già aggiornato, leggi il nuovo valore e aggiorna il contatore
					Long newDate = this.distributedActiveRequestCounterDate.get();
					this.distributedActiveRequestCounterForCheck = initActiveRequestCounters(newDate);
				}
			}
		} finally {
			this.lock.release(slock, "checkActiveRequestCounterIntervalChangeForCheck");
		}
	}

	/**
	 * Verifica se l'intervallo delle richieste è cambiato (per ForStats).
	 * Se cambiato, crea un nuovo contatore con il nuovo timestamp e mette il vecchio nel cestino.
	 */
	protected void checkActiveRequestCounterIntervalChangeForStats() {
		if(this.distributedActiveRequestCounterDate == null || this.distributedActiveRequestCounterForStats == null) {
			return;
		}

		long distributedDate = this.distributedActiveRequestCounterDate.get();
		if(!ActiveRequestDistributedIntervalManager.isIntervalloCambiato(this.richiesteSimultaneeIntervalloSecondi, distributedDate)) {
			return;
		}

		long currentIntervalStart = ActiveRequestDistributedIntervalManager.calcolaIntervalloCorrente(this.richiesteSimultaneeIntervalloSecondi);

		// L'intervallo è cambiato
		SemaphoreLock slock = this.lock.acquireThrowRuntime("checkActiveRequestCounterIntervalChangeForStats");
		try {
			// Rileggi il valore dopo aver acquisito il lock
			distributedDate = this.distributedActiveRequestCounterDate.get();
			if(ActiveRequestDistributedIntervalManager.isIntervalloCambiato(this.richiesteSimultaneeIntervalloSecondi, distributedDate)) {
				// Prova ad aggiornare la data
				if(this.distributedActiveRequestCounterDate.compareAndSet(distributedDate, currentIntervalStart)) {
					// Svuota il cestino (contatori di 2 intervalli fa)
					if(!this.cestinoActiveRequestCounters.isEmpty()) {
						for (DatoAtomicLong counter : this.cestinoActiveRequestCounters) {
							/**System.out.println("DESTROY STAT ["+counter.getName()+"]");*/
							counter.destroy();
						}
						this.cestinoActiveRequestCounters.clear();
					}

					// Metti il vecchio contatore nel cestino
					this.cestinoActiveRequestCounters.add(this.distributedActiveRequestCounterForStats);

					// Crea il nuovo contatore con il nuovo timestamp
					this.distributedActiveRequestCounterForStats = initActiveRequestCounters(currentIntervalStart);
				} else {
					// Un altro thread ha già aggiornato, leggi il nuovo valore e aggiorna il contatore
					Long newDate = this.distributedActiveRequestCounterDate.get();
					this.distributedActiveRequestCounterForStats = initActiveRequestCounters(newDate);
				}
			}
		} finally {
			this.lock.release(slock, "checkActiveRequestCounterIntervalChangeForStats");
		}
	}

	
	@Override
	protected void internalUpdateDatiStartRequestApplicabileIncrementRequestCounter(DatiCollezionati datiCollezionatiPerPolicyVerifier) {
		if(datiCollezionatiPerPolicyVerifier!=null) {
			super.policyRequestCounter = datiCollezionatiPerPolicyVerifier.setAndGetPolicyRequestCounter(this.distributedPolicyRequestCounter.incrementAndGet());
		}
		else {
			super.policyRequestCounter = this.distributedPolicyRequestCounter.incrementAndGet();
		}
	}
	
	
	@Override
	protected void internalRegisterEndRequestDecrementActiveRequestCounter() {
		if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
			// Evita valori negativi: decrementa solo se > 0
			if(this.distributedActiveRequestCounterForCheck.get() > 0) {
				super.activeRequestCounter = this.distributedActiveRequestCounterForCheck.decrementAndGet();
			}
		}
		else {
			// Per le statistiche, decrementa solo se > 0
			if(this.distributedActiveRequestCounterForStats.get() > 0) {
				this.distributedActiveRequestCounterForStats.decrementAndGetAsync();
			}
		}
	}
	@Override
	protected void internalRegisterEndRequestIncrementDegradoPrestazionaleRequestCounter() {
		super.policyDegradoPrestazionaleRequestCounter = this.distributedPolicyDegradoPrestazionaleRequestCounter.incrementAndGet();
	}
	@Override
	protected void internalRegisterEndRequestIncrementDegradoPrestazionaleCounter(long latenza) {
		super.policyDegradoPrestazionaleCounter = this.distributedPolicyDegradoPrestazionaleCounter.addAndGet(latenza);
	}
	

	@Override
	protected void internalUpdateDatiEndRequestApplicabileIncrementRequestCounter() {
		super.policyRequestCounter = this.distributedPolicyRequestCounter.incrementAndGet();
	}
	@Override
	protected void internalUpdateDatiEndRequestApplicabileDecrementRequestCounter() {
		super.policyRequestCounter = this.distributedPolicyRequestCounter.decrementAndGet();
	}
	@Override
	protected void internalUpdateDatiEndRequestApplicabileIncrementDenyRequestCounter() {
		this.distributedPolicyDenyRequestCounter.incrementAndGetAsync();
	}
	@Override
	protected void internalUpdateDatiEndRequestApplicabileIncrementCounter(long v) {
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
		if(this.distributedActiveRequestCounterDate!=null) {
			this.distributedActiveRequestCounterDate.destroy();
		}
		// Svuota il cestino dei contatori delle richieste simultanee
		if(this.cestinoActiveRequestCounters!=null && !this.cestinoActiveRequestCounters.isEmpty()) {
			for (DatoAtomicLong counter : this.cestinoActiveRequestCounters) {
				counter.destroy();
			}
			this.cestinoActiveRequestCounters.clear();
		}
		// Svuota il cestino dei contatori delle policy (intervalli precedenti)
		if(this.cestinoPolicyCounters!=null && !this.cestinoPolicyCounters.isEmpty()) {
			for (DatoAtomicLong counter : this.cestinoPolicyCounters) {
				counter.destroy();
			}
			this.cestinoPolicyCounters.clear();
		}
		// Svuota il cestino dei contatori del degrado prestazionale (intervalli precedenti)
		if(this.cestinoPolicyCountersDegradoPrestazionale!=null && !this.cestinoPolicyCountersDegradoPrestazionale.isEmpty()) {
			for (DatoAtomicLong counter : this.cestinoPolicyCountersDegradoPrestazionale) {
				counter.destroy();
			}
			this.cestinoPolicyCountersDegradoPrestazionale.clear();
		}

		if(this.distributedPolicyDenyRequestCounter!=null) {
			this.distributedPolicyDenyRequestCounter.destroy();
		}
	}
	
	
	// Getters necessari poichè non viene aggiornato il field nella classe padre DatiCollezionati, poichè si usa il metodo Async nel caso di informazioni statistiche
	
	@Override
	public Long getActiveRequestCounter(boolean readRemoteInfo) {
		if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
			if(readRemoteInfo) {
				try {
					return this.distributedActiveRequestCounterForCheck.get();
				} catch (DistributedObjectDestroyedException e) {
					// Durante lo shutdown o cambio intervallo, il contatore potrebbe essere stato distrutto
					/**System.out.println("getActiveRequestCounter CATTURATA DistributedObjectDestroyedException: " + e.getClass().getName() + " - " + e.getMessage());*/
					return super.activeRequestCounter;
				} 
			}
			else {
				return super.activeRequestCounter; // nelle operazioni di incremento/decremento l'ho aggiarnato via via e quindi il check utilizzerà questa informazione nel PolicyVerifier
			}
		}
		else {
			try {
				return this.distributedActiveRequestCounterForStats.get();
			} catch (DistributedObjectDestroyedException e) {
				// Durante lo shutdown o cambio intervallo, il contatore potrebbe essere stato distrutto
				/**System.out.println("getActiveRequestCounter (stats) CATTURATA DistributedObjectDestroyedException: " + e.getClass().getName() + " - " + e.getMessage());*/
				return 0L;
			}
		}
	}
	
	// Getters non necessari, sono utili solo se viene richiesta una lettura del dato remoto
	
	@Override
	public Long getPolicyDenyRequestCounter(boolean readRemoteInfo) {
		if(readRemoteInfo) {
			if(this.distributedPolicyDenyRequestCounter!=null) {
				try {
					return this.distributedPolicyDenyRequestCounter.get();
				} catch (DistributedObjectDestroyedException e) {
					// Durante lo shutdown o cambio intervallo, il contatore potrebbe essere stato distrutto
					return super.getPolicyDenyRequestCounter(false);
				}
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
				try {
					return this.distributedPolicyRequestCounter.get();
				} catch (DistributedObjectDestroyedException e) {
					// Durante lo shutdown o cambio intervallo, il contatore potrebbe essere stato distrutto
					return super.getPolicyRequestCounter(false);
				}
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
				try {
					return this.distributedPolicyCounter.get();
				} catch (DistributedObjectDestroyedException e) {
					// Durante lo shutdown o cambio intervallo, il contatore potrebbe essere stato distrutto
					return super.getPolicyCounter(false);
				}
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
				try {
					return this.distributedPolicyDegradoPrestazionaleRequestCounter.get();
				} catch (DistributedObjectDestroyedException e) {
					// Durante lo shutdown o cambio intervallo, il contatore potrebbe essere stato distrutto
					return super.getPolicyDegradoPrestazionaleRequestCounter(false);
				}
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
				try {
					return this.distributedPolicyDegradoPrestazionaleCounter.get();
				} catch (DistributedObjectDestroyedException e) {
					// Durante lo shutdown o cambio intervallo, il contatore potrebbe essere stato distrutto
					return super.getPolicyDegradoPrestazionaleCounter(false);
				}
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
