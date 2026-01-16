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

import java.util.Date;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicyMapId;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;

/**
 * 
 * L'incremento e il decremento dei contatori viene fatto in maniera asincrona, il reset resta sincrono.
 * 
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiCollezionatiDistributedAtomicLongAsync extends DatiCollezionatiDistributedAtomicLong {

	private static final long serialVersionUID = 1L;

	public DatiCollezionatiDistributedAtomicLongAsync(Logger log, Date updatePolicyDate, Date gestorePolicyConfigDate, HazelcastInstance hazelcast, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy,
			PolicyGroupByActiveThreadsType policyType) {
		super(log, updatePolicyDate, gestorePolicyConfigDate, hazelcast, groupByPolicyMapId, activePolicy, policyType);
	}


	public DatiCollezionatiDistributedAtomicLongAsync(Logger log, DatiCollezionati dati, HazelcastInstance hazelcast, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy,
			PolicyGroupByActiveThreadsType policyType) {
		super(log, dati, hazelcast, groupByPolicyMapId, activePolicy, policyType);
	}

	
	@Override
	protected void internalRegisterStartRequestIncrementActiveRequestCounter(DatiCollezionati datiCollezionatiPerPolicyVerifier, org.openspcoop2.utils.Map<Object> ctx) {
		if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
			// Verifica se l'intervallo è cambiato (solo se la gestione a intervalli è abilitata)
			if(this.richiesteSimultaneeIntervalloSecondi > 0) {
				checkActiveRequestCounterIntervalChangeForCheck();
			}

			if(datiCollezionatiPerPolicyVerifier!=null) {
				this.distributedActiveRequestCounterForCheck.incrementAndGetAsync();
				super.activeRequestCounter = datiCollezionatiPerPolicyVerifier.setAndGetActiveRequestCounter(this.distributedActiveRequestCounterForCheck.get());
			}
			else {
				this.distributedActiveRequestCounterForCheck.incrementAndGetAsync();
			}

			// Salva la data dell'intervallo nel contesto DOPO l'increment
			// In caso di cambio intervallo durante la richiesta, potrebbe causare valori negativi
			// che sono preferibili ai positivi (negativi = permissivo, positivi = restrittivo)
			saveIntervalDateInContextForALAsync(ctx);
		}
		else {
			super.internalRegisterStartRequestIncrementActiveRequestCounter(datiCollezionatiPerPolicyVerifier, ctx);
		}
	}

	/**
	 * Salva la data dell'intervallo corrente nel contesto.
	 * Deve essere chiamato DOPO l'increment.
	 */
	private void saveIntervalDateInContextForALAsync(org.openspcoop2.utils.Map<Object> ctx) {
		if(ctx != null && this.richiesteSimultaneeIntervalloSecondi > 0) {
			Long intervalDate = getActiveRequestCounterIntervalDate();
			saveActiveRequestCounterIntervalDateInContext(ctx, intervalDate);
		}
	}
	
	
	@Override
	protected void internalUpdateDatiStartRequestApplicabileIncrementRequestCounter(DatiCollezionati datiCollezionatiPerPolicyVerifier) {
		if(datiCollezionatiPerPolicyVerifier!=null) {
			this.distributedPolicyRequestCounter.incrementAndGetAsync();
			super.policyRequestCounter = datiCollezionatiPerPolicyVerifier.setAndGetPolicyRequestCounter(this.distributedPolicyRequestCounter.get());
		}
		else {
			this.distributedPolicyRequestCounter.incrementAndGetAsync();
		}
	}
	
	
	@Override
	protected void internalRegisterEndRequestDecrementActiveRequestCounter() {
		if(this.distribuitedActiveRequestCounterPolicyRichiesteSimultanee){
			// Evita valori negativi: decrementa solo se > 0
			if(this.distributedActiveRequestCounterForCheck.get() > 0) {
				this.distributedActiveRequestCounterForCheck.decrementAndGet();
			}
		}
		else {
			super.internalRegisterEndRequestDecrementActiveRequestCounter();
		}
	}
	@Override
	protected void internalRegisterEndRequestIncrementDegradoPrestazionaleRequestCounter() {
		this.distributedPolicyDegradoPrestazionaleRequestCounter.incrementAndGetAsync();
	}
	@Override
	protected void internalRegisterEndRequestIncrementDegradoPrestazionaleCounter(long latenza) {
		this.distributedPolicyDegradoPrestazionaleCounter.addAndGetAsync(latenza);
	}
	
	
	@Override
	protected void internalUpdateDatiEndRequestApplicabileIncrementRequestCounter() {
		this.distributedPolicyRequestCounter.incrementAndGetAsync();
	}
	@Override
	protected void internalUpdateDatiEndRequestApplicabileDecrementRequestCounter() {
		this.distributedPolicyRequestCounter.decrementAndGetAsync();
	}
	@Override
	protected void internalUpdateDatiEndRequestApplicabileIncrementCounter(long v) {
		this.distributedPolicyCounter.addAndGetAsync(v);
	}
	
	
	// Getters necessari poichè non viene aggiornato il field nella classe nonno DatiCollezionati, poichè si usa il metodo Async
	// Quindi in fase di lettura deve SEMPRE essere utilizzato il get
	
	@Override
	public Long getActiveRequestCounter(boolean readRemoteInfo) {
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
				return 0L;
			}
		}
	}
	@Override
	public Long getPolicyDenyRequestCounter(boolean readRemoteInfo) {
		if(this.distributedPolicyDenyRequestCounter!=null) {
			try {
				return this.distributedPolicyDenyRequestCounter.get();
			} catch (DistributedObjectDestroyedException e) {
				// Durante lo shutdown o cambio intervallo, il contatore potrebbe essere stato distrutto
				return super.policyDenyRequestCounter;
			}
		}
		else {
			return null;
		}
	}

	@Override
	public Long getPolicyRequestCounter(boolean readRemoteInfo) {
		if(this.distributedPolicyRequestCounter!=null) {
			try {
				return this.distributedPolicyRequestCounter.get();
			} catch (DistributedObjectDestroyedException e) {
				// Durante lo shutdown o cambio intervallo, il contatore potrebbe essere stato distrutto
				return super.policyRequestCounter;
			}
		}
		else {
			return null;
		}
	}
	@Override
	public Long getPolicyCounter(boolean readRemoteInfo) {
		if(this.distributedPolicyCounter!=null) {
			try {
				return this.distributedPolicyCounter.get();
			} catch (DistributedObjectDestroyedException e) {
				// Durante lo shutdown o cambio intervallo, il contatore potrebbe essere stato distrutto
				return super.policyCounter;
			}
		}
		else {
			return null;
		}
	}

	@Override
	public Long getPolicyDegradoPrestazionaleRequestCounter(boolean readRemoteInfo) {
		if(this.distributedPolicyDegradoPrestazionaleRequestCounter!=null) {
			try {
				return this.distributedPolicyDegradoPrestazionaleRequestCounter.get();
			} catch (DistributedObjectDestroyedException e) {
				// Durante lo shutdown o cambio intervallo, il contatore potrebbe essere stato distrutto
				return super.policyDegradoPrestazionaleRequestCounter;
			}
		}
		else {
			return null;
		}
	}
	@Override
	public Long getPolicyDegradoPrestazionaleCounter(boolean readRemoteInfo) {
		if(this.distributedPolicyDegradoPrestazionaleCounter!=null) {
			try {
				return this.distributedPolicyDegradoPrestazionaleCounter.get();
			} catch (DistributedObjectDestroyedException e) {
				// Durante lo shutdown o cambio intervallo, il contatore potrebbe essere stato distrutto
				return super.policyDegradoPrestazionaleCounter;
			}
		}
		else {
			return null;
		}
	}

}
