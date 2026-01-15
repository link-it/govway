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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson.counters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicyMapId;
import org.openspcoop2.core.controllo_traffico.beans.IDatiCollezionatiDistributed;
import org.openspcoop2.core.controllo_traffico.constants.TipoControlloPeriodo;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.ActiveRequestDistributedIntervalManager;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.BuilderDatiCollezionatiDistributed;
import org.openspcoop2.utils.SemaphoreLock;
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
	
	private final transient org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedLongAdder");
	
	private final transient org.openspcoop2.utils.Semaphore lockActiveRequestCounterGetAndSum = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedLongAdder_ActiveRequestCounterGetAndSum");
	private final transient org.openspcoop2.utils.Semaphore lockRequestCounterGetAndSum = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedLongAdder_RequestCounterGetAndSum");
	private final transient org.openspcoop2.utils.Semaphore lockCounterGetAndSum = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedLongAdder_CounterGetAndSum");
	private final transient org.openspcoop2.utils.Semaphore lockDegradoPrestazionaleRequestCounterGetAndSum = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedLongAdder_DegradoPrestazionaleRequestCounterGetAndSum");
	private final transient org.openspcoop2.utils.Semaphore lockDegradoPrestazionaleCounterGetAndSum = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedLongAdder_DegradoPrestazionaleCounterGetAndSum");
	
	private final transient RedissonClient redisson;
	private final IDUnivocoGroupByPolicyMapId groupByPolicyMapId;
	private final int groupByPolicyMapIdHashCode;

	// TTL configuration per la pulizia automatica dei contatori
	private final transient RedisTTLConfig ttlConfig;
	// TTL config per contatori senza intervallo temporale (es. activeRequestCounter, policyDate)
	// Questi contatori devono avere renewTTLOnWrite=true per rimanere attivi
	private final transient RedisTTLConfig ttlConfigNoInterval;

	// data di registrazione/aggiornamento policy
	
	// final: sono i contatori che non dipendono da una finestra temporale

	// sono rimasti AtomicLong le date

	private final transient DatoRAtomicLong distributedUpdatePolicyDate; // data di ultima modifica della policy
	private final transient DatoRAtomicLong distributedPolicyDate; // intervallo corrente su cui vengono costruiti gli altri contatori	
	
	private transient DatoRLongAdder distributedPolicyRequestCounter; // numero di richieste effettuato nell'intervallo
	private transient DatoRLongAdder distributedPolicyCounter;  // utilizzato per tempi o banda
	
	private final transient DatoRAtomicLong distributedPolicyDegradoPrestazionaleDate; // intervallo corrente su cui vengono costruiti gli altri contatori
	private transient DatoRLongAdder distributedPolicyDegradoPrestazionaleRequestCounter; // numero di richieste effettuato nell'intervallo
	private transient DatoRLongAdder distributedPolicyDegradoPrestazionaleCounter; // contatore del degrado
	
	private final boolean distribuitedActiveRequestCounterPolicyRichiesteSimultanee;
	private transient DatoRLongAdder distributedActiveRequestCounterForStats; // numero di richieste simultanee
	private transient DatoRLongAdder distributedActiveRequestCounterForCheck; // numero di richieste simultanee

	// Gestione intervalli per richieste simultanee (per evitare contatori orfani in ambienti distribuiti)
	private final transient int richiesteSimultaneeIntervalloSecondi; // intervallo in secondi, <= 0 disabilitato
	private final transient DatoRAtomicLong distributedActiveRequestCounterDate; // data intervallo corrente (solo se intervallo > 0)

	private transient DatoRLongAdder distributedPolicyDenyRequestCounter; // policy bloccate
	
	// I contatori da eliminare 
	// Se si effettua il drop di un contatore quando si rileva il cambio di intervallo, potrebbe succedere che in un altro nodo del cluster che sta effettuando la fase di 'end'
	// non rilevi più il contatore e di fatto quindi lo riprende partenzo da 0. Poi a sua volta capisce il cambio di intervallo e lo rielimina.
	// per questo motivo, il drop viene effettuato al secondo cambio di intervallo, e ad ogni cambio i contatori vengono collezionati nel cestino
	private transient List<DatoRLongAdder> cestinoPolicyCounters = new ArrayList<>();
	private transient List<DatoRLongAdder> cestinoPolicyCountersDegradoPrestazionale = new ArrayList<>();
	private transient List<DatoRLongAdder> cestinoActiveRequestCounters = new ArrayList<>();

	private boolean initialized = false;

	private String getRLongAdderName(String name, Long date) {
		String t = "";
		if(date!=null) {
			t = date + "";
		}
		
		String configDate = BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
				(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1);
		
		return "longadder-"+this.groupByPolicyMapIdHashCode+"-"+name+t+configDate+"-rl"; // non modificare inizio e fine
	}
		
	public DatiCollezionatiDistributedLongAdder(Logger log, Date updatePolicyDate, Date gestorePolicyConfigDate, RedissonClient  redisson, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy) {
		super(updatePolicyDate, gestorePolicyConfigDate);

		this.redisson = redisson;
		this.groupByPolicyMapId = groupByPolicyMapId;
		this.groupByPolicyMapIdHashCode = this.groupByPolicyMapId.hashCode();

		// Inizializza configurazione TTL basata sulla policy
		this.ttlConfig = RedisTTLConfig.fromPolicy(activePolicy);
		// TTL config per contatori senza intervallo (activeRequestCounter, policyDate)
		this.ttlConfigNoInterval = RedisTTLConfig.forCountersWithoutInterval();

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


	public DatiCollezionatiDistributedLongAdder(Logger log, DatiCollezionati dati, RedissonClient redisson, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy) {
		super(dati.getUpdatePolicyDate(), dati.getGestorePolicyConfigDate());

		if(log!=null) {
			// nop
		}

		// Inizializzo il padre
		dati.setValuesIn(this, false);

		this.redisson = redisson;
		this.groupByPolicyMapId = groupByPolicyMapId;
		this.groupByPolicyMapIdHashCode = this.groupByPolicyMapId.hashCode();

		// Inizializza configurazione TTL basata sulla policy
		this.ttlConfig = RedisTTLConfig.fromPolicy(activePolicy);
		// TTL config per contatori senza intervallo (activeRequestCounter, policyDate)
		this.ttlConfigNoInterval = RedisTTLConfig.forCountersWithoutInterval();

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

			// Non serve: essendo già persistente, si va a sommare un dato gia' esistente
			/**
			
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
				Long polDate = null;
				if(this.distributedPolicyDegradoPrestazionaleDate!=null) {
					polDate = this.distributedPolicyDegradoPrestazionaleDate.get();
					// Assicura che il TTL sia applicato anche se il contatore è stato creato da un altro nodo
					this.distributedPolicyDegradoPrestazionaleDate.ensureTTLApplied();
				}
				initPolicyCounters(polDate);

			/**}*/
		}
		
		// Se non ho la policyDegradoPrestazionaleDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime &&
			super.getPolicyDegradoPrestazionaleDate() != null) {
				
			// Non serve: essendo già persistente, si va a sommare un dato gia' esistente
			/**
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
				Long degradoPrestazionaleTime = null;
				if(this.distributedPolicyDate!=null) {
					degradoPrestazionaleTime = this.distributedPolicyDate.get();
					// Assicura che il TTL sia applicato anche se il contatore è stato creato da un altro nodo
					this.distributedPolicyDate.ensureTTLApplied();
				}
				initPolicyCountersDegradoPrestazionale(degradoPrestazionaleTime);

			/**}*/
		}

		this.initialized = true;
	}
	
	private DatoRAtomicLong initPolicyDate() {
		if(this.policyRealtime!=null && this.policyRealtime){
			// Usa ttlConfigNoInterval perché policyDate non è un contatore di intervallo:
			// mantiene il timestamp dell'intervallo corrente e vive attraverso più finestre temporali
			return new DatoRAtomicLong(this.redisson,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_POLICY_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.ttlConfigNoInterval);
		}
		return null;
	}
	private DatoRAtomicLong initUpdatePolicyDate() {
		if(this.policyRealtime!=null && this.policyRealtime){
			// Usa ttlConfigNoInterval perché updatePolicyDate non è un contatore di intervallo
			return new DatoRAtomicLong(this.redisson,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_UPDATE_POLICY_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.ttlConfigNoInterval);
		}
		return null;
	}
	private DatoRAtomicLong initPolicyDegradoPrestazionaleDate() {
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			// Usa ttlConfigNoInterval perché policyDegradoPrestazionaleDate non è un contatore di intervallo
			return new DatoRAtomicLong(this.redisson,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_POLICY_DEGRADO_PRESTAZIONALE_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.ttlConfigNoInterval);
		}
		return null;
	}

	private DatoRAtomicLong initActiveRequestCounterDate() {
		// Crea il contatore per la data dell'intervallo solo se la gestione a intervalli è abilitata
		if(this.richiesteSimultaneeIntervalloSecondi > 0) {
			return new DatoRAtomicLong(this.redisson,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_POLICY_DATE+
					"activeRequest"+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1),
					this.ttlConfigNoInterval);
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

	private DatoRLongAdder initActiveRequestCounters(Long intervalDate) {
		// Se intervalDate è valorizzato, crea un contatore con timestamp nel nome (gestione a intervalli)
		// Altrimenti crea un contatore senza timestamp (comportamento legacy)
		if(intervalDate != null && intervalDate > 0) {
			return new DatoRLongAdder(this.redisson,
					getRLongAdderName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_ACTIVE_REQUEST_COUNTER+intervalDate,
							(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1)),
					this.ttlConfigNoInterval);
		} else {
			// Usa ttlConfigNoInterval perché activeRequestCounter non ha un intervallo temporale:
			// conta le richieste attive in quel momento e deve rimanere attivo finché il client fa richieste
			return new DatoRLongAdder(this.redisson,
					getRLongAdderName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_ACTIVE_REQUEST_COUNTER,
							(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1)),
					this.ttlConfigNoInterval);
		}
	}

	private void initPolicyCounters(Long policyDate) {

		if(this.policyRealtime!=null && this.policyRealtime){

			this.distributedPolicyRequestCounter = new DatoRLongAdder(this.redisson,
					this.getRLongAdderName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_REQUEST_COUNTER,policyDate),
					this.ttlConfig);

			this.distributedPolicyDenyRequestCounter = new DatoRLongAdder(this.redisson,
					this.getRLongAdderName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DENY_REQUEST_COUNTER,policyDate),
					this.ttlConfig);

			if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
				this.distributedPolicyCounter = new DatoRLongAdder(this.redisson,
						this.getRLongAdderName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_COUNTER,policyDate),
						this.ttlConfig);
			}
		}

	}

	private void initPolicyCountersDegradoPrestazionale(Long policyDate) {

		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			this.distributedPolicyDegradoPrestazionaleCounter = new DatoRLongAdder(this.redisson,
					this.getRLongAdderName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_COUNTER,policyDate),
					this.ttlConfig);

			this.distributedPolicyDegradoPrestazionaleRequestCounter = new DatoRLongAdder(this.redisson,
					this.getRLongAdderName(BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_REQUEST_COUNTER,policyDate),
					this.ttlConfig);

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
						for (DatoRLongAdder rLongAdder : this.cestinoPolicyCounters) {
							rLongAdder.destroy();
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
				else {
					// Se compareAndSet fallisce, il contatore esiste già (creato da altro nodo).
					// Assicuriamo che abbia il TTL applicato.
					this.distributedPolicyDate.ensureTTLApplied();
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
						for (DatoRLongAdder pnCounter : this.cestinoPolicyCountersDegradoPrestazionale) {
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
				else {
					// Se compareAndSet fallisce, il contatore esiste già (creato da altro nodo).
					// Assicuriamo che abbia il TTL applicato.
					this.distributedPolicyDegradoPrestazionaleDate.ensureTTLApplied();
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
						for (DatoRLongAdder counter : this.cestinoActiveRequestCounters) {
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
						for (DatoRLongAdder counter : this.cestinoActiveRequestCounters) {
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

			SemaphoreLock slock = this.lockActiveRequestCounterGetAndSum.acquireThrowRuntime("internalRegisterStartRequestIncrementActiveRequestCounter");
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
				this.lockActiveRequestCounterGetAndSum.release(slock, "internalRegisterStartRequestIncrementActiveRequestCounter");
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

			this.distributedActiveRequestCounterForStats.increment();

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
		SemaphoreLock slock = this.lockRequestCounterGetAndSum.acquireThrowRuntime("internalUpdateDatiStartRequestApplicabileIncrementRequestCounter");
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
			this.lockRequestCounterGetAndSum.release(slock, "internalUpdateDatiStartRequestApplicabileIncrementRequestCounter");
		}
	}
	
	
	@Override
	protected void internalRegisterEndRequestDecrementActiveRequestCounter() {
		if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
			SemaphoreLock slock = this.lockActiveRequestCounterGetAndSum.acquireThrowRuntime("internalRegisterEndRequestDecrementActiveRequestCounter");
			try {
				super.activeRequestCounter = this.distributedActiveRequestCounterForCheck.sum(); // prendo il valore. Non esiste un unico incrementAndGet
				// Evita valori negativi: decrementa solo se > 0
				if(super.activeRequestCounter > 0) {
					this.distributedActiveRequestCounterForCheck.decrement();
					super.activeRequestCounter--;
				}
			}finally {
				this.lockActiveRequestCounterGetAndSum.release(slock, "internalRegisterEndRequestDecrementActiveRequestCounter");
			}
		}
		else {
			// Per le statistiche, decrementa solo se > 0
			if(this.distributedActiveRequestCounterForStats.sum() > 0) {
				this.distributedActiveRequestCounterForStats.decrement();
			}
		}
	}
	@Override
	protected void internalRegisterEndRequestIncrementDegradoPrestazionaleRequestCounter() {
		SemaphoreLock slock = this.lockDegradoPrestazionaleRequestCounterGetAndSum.acquireThrowRuntime("internalRegisterEndRequestIncrementDegradoPrestazionaleRequestCounter");
		try {
			super.policyDegradoPrestazionaleRequestCounter = this.distributedPolicyDegradoPrestazionaleRequestCounter.sum(); // prendo il valore. Non esiste un unico incrementAndGet
			this.distributedPolicyDegradoPrestazionaleRequestCounter.increment();
			super.policyDegradoPrestazionaleRequestCounter++;
		}finally {
			this.lockDegradoPrestazionaleRequestCounterGetAndSum.release(slock, "internalRegisterEndRequestIncrementDegradoPrestazionaleRequestCounter");
		}
	}
	@Override
	protected void internalRegisterEndRequestIncrementDegradoPrestazionaleCounter(long latenza) {
		SemaphoreLock slock = this.lockDegradoPrestazionaleCounterGetAndSum.acquireThrowRuntime("internalRegisterEndRequestIncrementDegradoPrestazionaleCounter");
		try {
			super.policyDegradoPrestazionaleCounter = this.distributedPolicyDegradoPrestazionaleCounter.sum(); // prendo il valore. Non esiste un unico incrementAndGet
			this.distributedPolicyDegradoPrestazionaleCounter.add(latenza);
			super.policyDegradoPrestazionaleCounter++;
		}finally {
			this.lockDegradoPrestazionaleCounterGetAndSum.release(slock, "internalRegisterEndRequestIncrementDegradoPrestazionaleCounter");
		}
	}
	
	
	@Override
	protected void internalUpdateDatiEndRequestApplicabileIncrementRequestCounter() {
		SemaphoreLock slock = this.lockRequestCounterGetAndSum.acquireThrowRuntime("internalUpdateDatiEndRequestApplicabileIncrementRequestCounter");
		try {
			super.policyRequestCounter = this.distributedPolicyRequestCounter.sum(); // prendo il valore. Non esiste un unico incrementAndGet
			this.distributedPolicyRequestCounter.increment();
			super.policyRequestCounter++;
		}finally {
			this.lockRequestCounterGetAndSum.release(slock, "internalUpdateDatiEndRequestApplicabileIncrementRequestCounter");
		}
	}
	@Override
	protected void internalUpdateDatiEndRequestApplicabileDecrementRequestCounter() {
		SemaphoreLock slock = this.lockRequestCounterGetAndSum.acquireThrowRuntime("internalUpdateDatiEndRequestApplicabileDecrementRequestCounter");
		try {
			super.policyRequestCounter = this.distributedPolicyRequestCounter.sum(); // prendo il valore. Non esiste un unico incrementAndGet
			this.distributedPolicyRequestCounter.decrement();
			super.policyRequestCounter--;
		}finally {
			this.lockRequestCounterGetAndSum.release(slock, "internalUpdateDatiEndRequestApplicabileDecrementRequestCounter");
		}
	}
	@Override
	protected void internalUpdateDatiEndRequestApplicabileIncrementDenyRequestCounter() {
		this.distributedPolicyDenyRequestCounter.increment();
	}
	@Override
	protected void internalUpdateDatiEndRequestApplicabileIncrementCounter(long v) {
		SemaphoreLock slock = this.lockCounterGetAndSum.acquireThrowRuntime("internalUpdateDatiEndRequestApplicabileIncrementCounter");
		try {
			super.policyCounter = this.distributedPolicyCounter.sum(); // prendo il valore. Non esiste un unico incrementAndGet
			this.distributedPolicyCounter.add(v);
			super.policyCounter+=v;
		}finally {
			this.lockCounterGetAndSum.release(slock, "internalUpdateDatiEndRequestApplicabileIncrementCounter");
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
		if(this.distributedActiveRequestCounterDate!=null) {
			this.distributedActiveRequestCounterDate.delete();
		}
		// Svuota il cestino dei contatori delle richieste simultanee
		if(this.cestinoActiveRequestCounters!=null && !this.cestinoActiveRequestCounters.isEmpty()) {
			for (DatoRLongAdder counter : this.cestinoActiveRequestCounters) {
				counter.destroy();
			}
			this.cestinoActiveRequestCounters.clear();
		}
		// Svuota il cestino dei contatori delle policy (intervalli precedenti)
		if(this.cestinoPolicyCounters!=null && !this.cestinoPolicyCounters.isEmpty()) {
			for (DatoRLongAdder counter : this.cestinoPolicyCounters) {
				counter.destroy();
			}
			this.cestinoPolicyCounters.clear();
		}
		// Svuota il cestino dei contatori del degrado prestazionale (intervalli precedenti)
		if(this.cestinoPolicyCountersDegradoPrestazionale!=null && !this.cestinoPolicyCountersDegradoPrestazionale.isEmpty()) {
			for (DatoRLongAdder counter : this.cestinoPolicyCountersDegradoPrestazionale) {
				counter.destroy();
			}
			this.cestinoPolicyCountersDegradoPrestazionale.clear();
		}

		if(this.distributedPolicyDenyRequestCounter!=null) {
			this.distributedPolicyDenyRequestCounter.destroy();
		}
	}
	
	
	
	// Getters necessari poichè non viene aggiornato il field nella classe nonno DatiCollezionati, poichè i metodi increment, decrement e add non ritornano anche il valore
	
	@Override
	public Long getActiveRequestCounter(boolean readRemoteInfo) {
		if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
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
