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
 *  https://docs.hazelcast.com/hazelcast/5.3/data-structures/pn-counter
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

	// Prefisso per i PNCounter - non modificare, configurato in hazelcast config (govway.hazelcast-pn-counters.yaml)
	public static final String PNCOUNTER_PREFIX = "pncounter-";
	// Prefisso per i DatoAtomicLong ausiliari (date) - per evitare collisioni con altre implementazioni (es. AtomicLong)
	public static final String ATOMIC_LONG_PREFIX = "pn_al-";
	
	private final transient org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedPNCounter");

	private final transient PolicyGroupByActiveThreadsType policyType;
	private final transient HazelcastInstance hazelcast;
	private final IDUnivocoGroupByPolicyMapId groupByPolicyMapId;
	private final int groupByPolicyMapIdHashCode;

	// data di registrazione/aggiornamento policy
	
	// final: sono i contatori che non dipendono da una finestra temporale
	
	// sono rimasti AtomicLong le date
	
	private final transient DatoAtomicLong distributedUpdatePolicyDate; // data di ultima modifica della policy
	private final transient DatoAtomicLong distributedPolicyDate; // intervallo corrente su cui vengono costruiti gli altri contatori
	
	private transient DatoPNCounter distributedPolicyRequestCounter; // numero di richieste effettuato nell'intervallo
	private transient DatoPNCounter distributedPolicyCounter; // utilizzato per tempi o banda
		
	private final transient DatoAtomicLong distributedPolicyDegradoPrestazionaleDate; // intervallo corrente su cui vengono costruiti gli altri contatori
	private transient DatoPNCounter distributedPolicyDegradoPrestazionaleRequestCounter; // numero di richieste effettuato nell'intervallo
	private transient DatoPNCounter distributedPolicyDegradoPrestazionaleCounter; // contatore del degrado
	
	private final boolean distribuitedActiveRequestCounterPolicyRichiesteSimultanee;
	private transient DatoPNCounter distributedActiveRequestCounterForStats; // numero di richieste simultanee
	private transient DatoPNCounter distributedActiveRequestCounterForCheck; // numero di richieste simultanee

	// Gestione intervalli per richieste simultanee (per evitare contatori orfani in ambienti distribuiti)
	private final transient int richiesteSimultaneeIntervalloSecondi; // intervallo in secondi, <= 0 disabilitato
	private final transient DatoAtomicLong distributedActiveRequestCounterDate; // data intervallo corrente (solo se intervallo > 0)

	private transient DatoPNCounter distributedPolicyDenyRequestCounter; // policy bloccate
	
	// I contatori da eliminare 
	// Se si effettua il drop di un contatore quando si rileva il cambio di intervallo, potrebbe succedere che in un altro nodo del cluster che sta effettuando la fase di 'end'
	// non rilevi più il contatore e di fatto quindi lo riprende partenzo da 0. Poi a sua volta capisce il cambio di intervallo e lo rielimina.
	// per questo motivo, il drop viene effettuato al secondo cambio di intervallo, e ad ogni cambio i contatori vengono collezionati nel cestino
	private transient List<DatoPNCounter> cestinoPolicyCounters = new ArrayList<>();
	private transient List<DatoPNCounter> cestinoPolicyCountersDegradoPrestazionale = new ArrayList<>();
	private transient List<DatoPNCounter> cestinoActiveRequestCounters = new ArrayList<>();

	private boolean initialized = false;

	private String getPNCounterName(String name, Long date) {
		String t = "";
		if(date!=null) {
			t = date + "";
		}
		
		String configDate = BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
				(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1);
		
		return PNCOUNTER_PREFIX+this.groupByPolicyMapIdHashCode+"-"+name+t+configDate+"-rl"; // non modificare inizio e fine poichè configurato in hazelcast config
	}
	
	public DatiCollezionatiDistributedPNCounter(Logger log, Date updatePolicyDate, Date gestorePolicyConfigDate, HazelcastInstance hazelcast, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy,
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
			this.distributedActiveRequestCounterForCheck = this.initActiveRequestCounters(getActiveRequestCounterIntervalDate());
			this.distributedActiveRequestCounterForStats = null;
		}
		else {
			this.distributedActiveRequestCounterForStats = this.initActiveRequestCounters(getActiveRequestCounterIntervalDate());
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
		if(this.policyRealtime!=null && this.policyRealtime &&
			updatePolicyDate != null && this.distributedUpdatePolicyDate!=null && this.distributedUpdatePolicyDate.get() < updatePolicyDate.getTime()) {
			this.resetCounters(updatePolicyDate);
		}

		this.initialized = true;
	}

	public DatiCollezionatiDistributedPNCounter(Logger log, DatiCollezionati dati, HazelcastInstance hazelcast, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy,
			PolicyGroupByActiveThreadsType policyType) {
		super(dati.getUpdatePolicyDate(), dati.getGestorePolicyConfigDate());

		if(log!=null) {
			// nop
		}

		this.policyType = policyType;
		
		// Inizializzo il padre
		dati.setValuesIn(this, false);
		
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
			this.distributedActiveRequestCounterForCheck = this.initActiveRequestCounters(getActiveRequestCounterIntervalDate());
			this.distributedActiveRequestCounterForStats = null;
		}
		else {
			this.distributedActiveRequestCounterForStats = this.initActiveRequestCounters(getActiveRequestCounterIntervalDate());
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
					if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
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
					ATOMIC_LONG_PREFIX+
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
					ATOMIC_LONG_PREFIX+
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
					ATOMIC_LONG_PREFIX+
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
					ATOMIC_LONG_PREFIX+
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_POLICY_DATE+
					"activeRequest"+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.policyType);
		}
		return null;
	}

	@Override
	protected Long getActiveRequestCounterIntervalDate() {
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

	private DatoPNCounter initActiveRequestCounters(Long intervalDate) {
		// Se intervalDate è valorizzato, crea un contatore con timestamp nel nome (gestione a intervalli)
		// Altrimenti crea un contatore senza timestamp (comportamento legacy)
		if(intervalDate != null && intervalDate > 0) {
			return new DatoPNCounter(this.hazelcast,
					getPNCounterName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_ACTIVE_REQUEST_COUNTER, intervalDate));
		} else {
			return new DatoPNCounter(this.hazelcast,
					getPNCounterName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_ACTIVE_REQUEST_COUNTER, null));
		}
	}
	
	private void initPolicyCounters(Long policyDate) {

		if(this.policyRealtime!=null && this.policyRealtime){
						
			this.distributedPolicyRequestCounter = new DatoPNCounter(this.hazelcast,
					this.getPNCounterName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_REQUEST_COUNTER,policyDate));
			
			this.distributedPolicyDenyRequestCounter = new DatoPNCounter(this.hazelcast,
					this.getPNCounterName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DENY_REQUEST_COUNTER,policyDate));
			
			if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
				this.distributedPolicyCounter = new DatoPNCounter(this.hazelcast,
						this.getPNCounterName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_COUNTER,policyDate));
			}
		}
	}
	
	private void initPolicyCountersDegradoPrestazionale(Long policyDate) {
		
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			this.distributedPolicyDegradoPrestazionaleCounter = new DatoPNCounter(this.hazelcast,
					this.getPNCounterName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_COUNTER,policyDate));

			this.distributedPolicyDegradoPrestazionaleRequestCounter = new DatoPNCounter(this.hazelcast,
					this.getPNCounterName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_REQUEST_COUNTER,policyDate));

		}	
	}
	

	@Override
	protected void resetPolicyCounterForDate(Date date) {
		if(this.initialized) {
			SemaphoreLock slock = this.lock.acquireThrowRuntime("resetPolicyCounterForDate");
			try {
				long policyDate = date.getTime();
				long actual = this.distributedPolicyDate.get();
				long actualSuper = super.policyDate!=null ? super.policyDate.getTime() : -1;
				if(actualSuper!=policyDate && actual<policyDate && this.distributedPolicyDate.compareAndSet(actual, policyDate)) {					
				
					// Solo 1 nodo del cluster deve entrare in questo codice, altrimenti vengono fatti destroy più volte sullo stesso contatore
					// Potrà capitare che il cestino di un nodo non venga svuotato se si entra sempre sull'altro, cmq sia rimarrà 1 cestino con dei contatori di 1 intervallo.
					// Non appena ci entra poi li distruggerà.
	
					// effettuo il drop creati due intervalli indietro
					if(!this.cestinoPolicyCounters.isEmpty()) {
						for (DatoPNCounter pnCounter : this.cestinoPolicyCounters) {
							pnCounter.destroy();
						}
						this.cestinoPolicyCounters.clear();
					}
					
					if(this.distributedPolicyRequestCounter!=null || this.distributedPolicyDenyRequestCounter!=null || this.distributedPolicyCounter!=null) {
						// conservo precedenti contatori
						if(this.distributedPolicyRequestCounter!=null) {
							this.cestinoPolicyCounters.add(this.distributedPolicyRequestCounter);
						}
						if(this.distributedPolicyDenyRequestCounter!=null) {
							this.cestinoPolicyCounters.add(this.distributedPolicyDenyRequestCounter);
						}
						if(this.distributedPolicyCounter!=null) {
							this.cestinoPolicyCounters.add(this.distributedPolicyCounter);
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
				long actual = this.distributedPolicyDate.get();
				long actualSuper = super.policyDegradoPrestazionaleDate!=null ? super.policyDegradoPrestazionaleDate.getTime() : -1;
				if(actualSuper!=policyDate && actual<policyDate && this.distributedPolicyDegradoPrestazionaleDate.compareAndSet(actual, policyDate)) {	
				
					// Solo 1 nodo del cluster deve entrare in questo codice, altrimenti vengono fatti destroy più volte sullo stesso contatore
					// Potrà capitare che il cestino di un nodo non venga svuotato se si entra sempre sull'altro, cmq sia rimarrà 1 cestino con dei contatori di 1 intervallo.
					// Non appena ci entra poi li distruggerà.
					
					// effettuo il drop creati due intervalli indietro
					if(!this.cestinoPolicyCountersDegradoPrestazionale.isEmpty()) {
						for (DatoPNCounter pnCounter : this.cestinoPolicyCountersDegradoPrestazionale) {
							pnCounter.destroy();
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
	
	
	
	/**
	 * Verifica se l'intervallo delle richieste simultanee è cambiato (per ForCheck).
	 * Se cambiato, crea un nuovo contatore con il nuovo timestamp e mette il vecchio nel cestino.
	 */
	private void checkActiveRequestCounterIntervalChangeForCheck() {
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
						for (DatoPNCounter counter : this.cestinoActiveRequestCounters) {
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
	private void checkActiveRequestCounterIntervalChangeForStats() {
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
						for (DatoPNCounter counter : this.cestinoActiveRequestCounters) {
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

			super.activeRequestCounter = this.distributedActiveRequestCounterForStats.incrementAndGet();

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
				super.activeRequestCounter = this.distributedActiveRequestCounterForStats.decrementAndGet();
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
		super.policyDenyRequestCounter = this.distributedPolicyDenyRequestCounter.incrementAndGet();
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
			for (DatoPNCounter counter : this.cestinoActiveRequestCounters) {
				counter.destroy();
			}
			this.cestinoActiveRequestCounters.clear();
		}
		// Svuota il cestino dei contatori delle policy (intervalli precedenti)
		if(this.cestinoPolicyCounters!=null && !this.cestinoPolicyCounters.isEmpty()) {
			for (DatoPNCounter counter : this.cestinoPolicyCounters) {
				counter.destroy();
			}
			this.cestinoPolicyCounters.clear();
		}
		// Svuota il cestino dei contatori del degrado prestazionale (intervalli precedenti)
		if(this.cestinoPolicyCountersDegradoPrestazionale!=null && !this.cestinoPolicyCountersDegradoPrestazionale.isEmpty()) {
			for (DatoPNCounter counter : this.cestinoPolicyCountersDegradoPrestazionale) {
				counter.destroy();
			}
			this.cestinoPolicyCountersDegradoPrestazionale.clear();
		}

		if(this.distributedPolicyDenyRequestCounter!=null) {
			this.distributedPolicyDenyRequestCounter.destroy();
		}
	}
	
	
	
	// Getters non necessari, sono utili solo se viene richiesta una lettura del dato remoto
	
	@Override
	public Long getActiveRequestCounter(boolean readRemoteInfo) {
		if(readRemoteInfo) {
			if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
				try {
					return this.distributedActiveRequestCounterForCheck.get();
				} catch (DistributedObjectDestroyedException e) {
					// Durante lo shutdown o cambio intervallo, il contatore potrebbe essere stato distrutto
					return super.activeRequestCounter;
				}
			}
			else {
				try {
					return this.distributedActiveRequestCounterForStats.get();
				} catch (DistributedObjectDestroyedException e) {
					// Durante lo shutdown o cambio intervallo, il contatore potrebbe essere stato distrutto
					return super.activeRequestCounter;
				}
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
