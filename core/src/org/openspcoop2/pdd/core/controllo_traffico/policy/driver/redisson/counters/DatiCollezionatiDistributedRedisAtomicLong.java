/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
import org.openspcoop2.utils.SemaphoreLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;


/**
 * Scelgo questa implementazione, dove la versione distribuita eredita da DatiCollezionati e replica i dati da distribuitre.
 * In questo modo, le varie PolicyGroupByActiveThreads* continueranno a restituire un oggetto di tipo DatiCollezionati. Con dati che sono presenti
 * nella ram del nodo locale, evitando così che il PolicyVerifier o altre classi che utilizzano i DatiCollezionatiDistributed vadano 
 * a fare richieste remote, ottenendo valori che non sono quelli che ci si aspettava dopo la richiesta.
 * 
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DatiCollezionatiDistributedRedisAtomicLong extends DatiCollezionati implements IDatiCollezionatiDistributed{

	private static final long serialVersionUID = 1L;
	
	private final transient org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("DatiCollezionatiDistributedRedisAtomicLong");
	
	private final transient RedissonClient redisson;
	private final IDUnivocoGroupByPolicyMapId groupByPolicyMapId;
	private final int groupByPolicyMapIdHashCode;

	// data di registrazione/aggiornamento policy
	
	// final: sono i contatori che non dipendono da una finestra temporale
	
	// sono rimasti AtomicLong le date
	private final transient DatoRAtomicLong distributedUpdatePolicyDate; // data di ultima modifica della policy
	private final transient DatoRAtomicLong distributedPolicyDate; // intervallo corrente su cui vengono costruiti gli altri contatori

	private transient DatoRAtomicLong distributedPolicyRequestCounter;	// numero di richieste effettuato nell'intervallo
	private transient DatoRAtomicLong distributedPolicyCounter; // utilizzato per tempi o banda
	
	private final transient DatoRAtomicLong distributedPolicyDegradoPrestazionaleDate; // intervallo corrente su cui vengono costruiti gli altri contatori
	private transient  DatoRAtomicLong distributedPolicyDegradoPrestazionaleRequestCounter; // numero di richieste effettuato nell'intervallo
	private transient DatoRAtomicLong distributedPolicyDegradoPrestazionaleCounter; // contatore del degrado

	private final boolean distribuitedActiveRequestCounterPolicyRichiesteSimultanee;
	private final transient DatoRAtomicLong distributedActiveRequestCounterForStats; // numero di richieste simultanee
	private transient DatoRAtomicLong distributedActiveRequestCounterForCheck; // numero di richieste simultanee
	
	private transient DatoRAtomicLong distributedPolicyDenyRequestCounter; // policy bloccate
	
	// I contatori da eliminare 
	// Se si effettua il drop di un contatore quando si rileva il cambio di intervallo, potrebbe succedere che in un altro nodo del cluster che sta effettuando la fase di 'end'
	// non rilevi più il contatore e di fatto quindi lo riprende partenzo da 0. Poi a sua volta capisce il cambio di intervallo e lo rielimina.
	// per questo motivo, il drop viene effettuato al secondo cambio di intervallo, e ad ogni cambio i contatori vengono collezionati nel cestino
	private transient List<DatoRAtomicLong> cestinoPolicyCounters = new ArrayList<>();
	private transient List<DatoRAtomicLong> cestinoPolicyCountersDegradoPrestazionale = new ArrayList<>();
	
	private boolean initialized = false;

	public DatiCollezionatiDistributedRedisAtomicLong(Logger log, Date updatePolicyDate, Date gestorePolicyConfigDate, RedissonClient redisson, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy) {
		super(updatePolicyDate, gestorePolicyConfigDate);
	
		this.redisson = redisson;
		this.groupByPolicyMapId = groupByPolicyMapId;
		this.groupByPolicyMapIdHashCode = this.groupByPolicyMapId.hashCode();
		
		this.initDatiIniziali(activePolicy);
		this.checkDate(log, activePolicy); // inizializza le date se ci sono
		
		this.distributedPolicyDate = this.initPolicyDate();
		this.distributedUpdatePolicyDate = this.initUpdatePolicyDate();
		
		this.distributedPolicyDegradoPrestazionaleDate = this.initPolicyDegradoPrestazionaleDate();
		
		this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee = activePolicy.getConfigurazionePolicy().isSimultanee() &&
				TipoControlloPeriodo.REALTIME.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo());
		if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
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
		if(this.policyRealtime!=null && this.policyRealtime &&
			updatePolicyDate != null && this.distributedUpdatePolicyDate!=null && this.distributedUpdatePolicyDate.get() < updatePolicyDate.getTime()) {
			this.resetCounters(updatePolicyDate);
		}
		
		this.initialized = true;
	}
		
	public DatiCollezionatiDistributedRedisAtomicLong(Logger log, DatiCollezionati dati, RedissonClient redisson, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy) {
		super(dati.getUpdatePolicyDate(), dati.getGestorePolicyConfigDate());
		
		if(log!=null) {
			// nop
		}
		
		// Inizializzo il padre con i valori in RAM, dopo uso 'super' per essere  sicuro di usare quelli
		dati.setValuesIn(this, false);
		
		this.redisson = redisson;
		this.groupByPolicyMapId = groupByPolicyMapId;
		this.groupByPolicyMapIdHashCode = this.groupByPolicyMapId.hashCode();
		
		this.distributedPolicyDate = this.initPolicyDate();
		this.distributedUpdatePolicyDate = this.initUpdatePolicyDate();
		
		this.distributedPolicyDegradoPrestazionaleDate = this.initPolicyDegradoPrestazionaleDate();
		
		this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee = activePolicy.getConfigurazionePolicy().isSimultanee() &&
				TipoControlloPeriodo.REALTIME.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo());
		if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
			this.distributedActiveRequestCounterForCheck = this.initActiveRequestCounters();
			this.distributedActiveRequestCounterForStats = null;
		}
		else {
			this.distributedActiveRequestCounterForStats = this.initActiveRequestCounters();
		}
		

		// Se non ho la policyDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if (super.getPolicyDate() != null) {

			// Non serve: essendo già persistente, si va a sommare un dato gia' esistente
			/**
			
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
			*/
				Long polDate = this.distributedPolicyDate!=null ? this.distributedPolicyDate.get() : null;
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
					this.distributedPolicyDegradoPrestazionaleRequestCounter.addAndGet(getPolicyDegradoPrestazionaleRequestCounter);
				}
				
				Long getPolicyDegradoPrestazionaleCounter = super.getPolicyDegradoPrestazionaleCounter(true);
				if (getPolicyDegradoPrestazionaleCounter != null) {
					this.distributedPolicyDegradoPrestazionaleCounter.addAndGet(getPolicyDegradoPrestazionaleCounter);
				}
				
			}  else {
			*/	
				Long degradoPrestazionaleTime = this.distributedPolicyDegradoPrestazionaleDate!=null ? this.distributedPolicyDegradoPrestazionaleDate.get() : null;
				initPolicyCountersDegradoPrestazionale(degradoPrestazionaleTime);
				
			/**}*/

		}
		
		this.initialized = true;
	}
	
	private DatoRAtomicLong initPolicyDate() {
		if(this.policyRealtime!=null && this.policyRealtime){
			return new DatoRAtomicLong(this.redisson,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_POLICY_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
		}
		return null;
	}
	private DatoRAtomicLong initUpdatePolicyDate() {
		if(this.policyRealtime!=null && this.policyRealtime){
			return new DatoRAtomicLong(this.redisson,
					this.groupByPolicyMapIdHashCode
					+BuilderDatiCollezionatiDistributed.DISTRUBUITED_UPDATE_POLICY_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
		}
		return null;
	}
	private DatoRAtomicLong initPolicyDegradoPrestazionaleDate() {
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			return new DatoRAtomicLong(this.redisson,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_POLICY_DEGRADO_PRESTAZIONALE_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
		}
		return null;
	}
	
	private DatoRAtomicLong initActiveRequestCounters() {
		return new DatoRAtomicLong(this.redisson,
				this.groupByPolicyMapIdHashCode+
				BuilderDatiCollezionatiDistributed.DISTRUBUITED_ACTIVE_REQUEST_COUNTER+
				(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
	}
	
	private void initPolicyCounters(Long policyDate) {

		if(this.policyRealtime!=null && this.policyRealtime){
						
			this.distributedPolicyRequestCounter = new DatoRAtomicLong(this.redisson,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_REQUEST_COUNTER+
					policyDate+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
			
			this.distributedPolicyDenyRequestCounter = new DatoRAtomicLong(this.redisson,
					this.groupByPolicyMapIdHashCode+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DENY_REQUEST_COUNTER+
					policyDate+
					BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
					(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
			
			if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
				this.distributedPolicyCounter = new DatoRAtomicLong(this.redisson,
						this.groupByPolicyMapIdHashCode+
						BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_COUNTER+
						policyDate+
						BuilderDatiCollezionatiDistributed.DISTRUBUITED_SUFFIX_CONFIG_DATE+
						(this.gestorePolicyConfigDate!=null ? this.gestorePolicyConfigDate.getTime() : -1));
			}
		}
	}
	
	private void initPolicyCountersDegradoPrestazionale(Long policyDate) {
		
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			this.distributedPolicyDegradoPrestazionaleCounter = new DatoRAtomicLong(this.redisson,
					this.groupByPolicyMapIdHashCode+BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_COUNTER+policyDate);
			
			this.distributedPolicyDegradoPrestazionaleRequestCounter = new DatoRAtomicLong(this.redisson,
					this.groupByPolicyMapIdHashCode+BuilderDatiCollezionatiDistributed.DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_REQUEST_COUNTER+policyDate);
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
						for (DatoRAtomicLong iAtomicLong : this.cestinoPolicyCounters) {
							iAtomicLong.delete();
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
					initPolicyCounters(date.getTime());
					
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
						for (DatoRAtomicLong iAtomicLong : this.cestinoPolicyCountersDegradoPrestazionale) {
							iAtomicLong.delete();
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
	protected void internalRegisterStartRequestIncrementActiveRequestCounter(DatiCollezionati datiCollezionatiPerPolicyVerifier) {
		if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
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
			super.activeRequestCounter = this.distributedActiveRequestCounterForCheck.decrementAndGet();
		}
		else {
			this.distributedActiveRequestCounterForStats.decrementAndGetAsync();
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
			this.distributedPolicyDate.delete();
		}
		if(this.distributedUpdatePolicyDate!=null) {
			this.distributedUpdatePolicyDate.delete();
		}
		
		if(this.distributedPolicyRequestCounter!=null) {
			this.distributedPolicyRequestCounter.delete();
		}
		if(this.distributedPolicyCounter!=null) {
			this.distributedPolicyCounter.delete();
		}
		
		if(this.distributedPolicyDegradoPrestazionaleDate!=null) {
			this.distributedPolicyDegradoPrestazionaleDate.delete();
		}
		if(this.distributedPolicyDegradoPrestazionaleRequestCounter!=null) {
			this.distributedPolicyDegradoPrestazionaleRequestCounter.delete();
		}
		if(this.distributedPolicyDegradoPrestazionaleCounter!=null) {
			this.distributedPolicyDegradoPrestazionaleCounter.delete();
		}
		
		if(this.distributedActiveRequestCounterForStats!=null) {
			this.distributedActiveRequestCounterForStats.delete();
		}
		if(this.distributedActiveRequestCounterForCheck!=null) {
			this.distributedActiveRequestCounterForCheck.delete();
		}
		
		if(this.distributedPolicyDenyRequestCounter!=null) {
			this.distributedPolicyDenyRequestCounter.delete();
		}
	}
	
	
	// Getters necessari poichè non viene aggiornato il field nella classe padre DatiCollezionati, poichè si usa il metodo Async nel caso di informazioni statistiche
	
	@Override
	public Long getActiveRequestCounter(boolean readRemoteInfo) {
		if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
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
