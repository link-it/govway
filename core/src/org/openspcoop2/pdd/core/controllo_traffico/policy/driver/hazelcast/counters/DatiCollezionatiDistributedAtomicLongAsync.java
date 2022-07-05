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


package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.counters;

import java.util.Date;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicyMapId;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;

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

	public DatiCollezionatiDistributedAtomicLongAsync(Logger log, Date updatePolicyDate, Date gestorePolicyConfigDate, HazelcastInstance hazelcast, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy) {
		super(log, updatePolicyDate, gestorePolicyConfigDate, hazelcast, groupByPolicyMapId, activePolicy);
	}
	
	
	public DatiCollezionatiDistributedAtomicLongAsync(Logger log, DatiCollezionati dati, HazelcastInstance hazelcast, IDUnivocoGroupByPolicyMapId groupByPolicyMapId, ActivePolicy activePolicy) {
		super(log, dati, hazelcast, groupByPolicyMapId, activePolicy);
	}

	
	@Override
	protected void _registerStartRequest_incrementActiveRequestCounter(DatiCollezionati datiCollezionatiPerPolicyVerifier) {
		if(this.distribuitedActiveRequestCounter_policyRichiesteSimultanee){
			if(datiCollezionatiPerPolicyVerifier!=null) {
				this.distributedActiveRequestCounterForCheck.incrementAndGetAsync();
				super.activeRequestCounter = datiCollezionatiPerPolicyVerifier.setAndGetActiveRequestCounter(this.distributedActiveRequestCounterForCheck.get());
			}
			else {
				this.distributedActiveRequestCounterForCheck.incrementAndGetAsync();
			}
		}
		else {
			super._registerStartRequest_incrementActiveRequestCounter(datiCollezionatiPerPolicyVerifier);
		}
	}
	
	
	@Override
	protected void _updateDatiStartRequestApplicabile_incrementRequestCounter(DatiCollezionati datiCollezionatiPerPolicyVerifier) {
		if(datiCollezionatiPerPolicyVerifier!=null) {
			this.distributedPolicyRequestCounter.incrementAndGetAsync();
			super.policyRequestCounter = datiCollezionatiPerPolicyVerifier.setAndGetPolicyRequestCounter(this.distributedPolicyRequestCounter.get());
		}
		else {
			this.distributedPolicyRequestCounter.incrementAndGetAsync();
		}
	}
	
	
	@Override
	protected void _registerEndRequest_decrementActiveRequestCounter() {
		if(this.distribuitedActiveRequestCounter_policyRichiesteSimultanee){
			this.distributedActiveRequestCounterForCheck.decrementAndGet();
		}
		else {
			super._registerEndRequest_decrementActiveRequestCounter();
		}
	}
	@Override
	protected void _registerEndRequest_incrementDegradoPrestazionaleRequestCounter() {
		this.distributedPolicyDegradoPrestazionaleRequestCounter.incrementAndGetAsync();
	}
	@Override
	protected void _registerEndRequest_incrementDegradoPrestazionaleCounter(long latenza) {
		this.distributedPolicyDegradoPrestazionaleCounter.addAndGetAsync(latenza);
	}
	
	
	@Override
	protected void _updateDatiEndRequestApplicabile_incrementRequestCounter() {
		this.distributedPolicyRequestCounter.incrementAndGetAsync();
	}
	@Override
	protected void _updateDatiEndRequestApplicabile_decrementRequestCounter() {
		this.distributedPolicyRequestCounter.decrementAndGetAsync();
	}
	@Override
	protected void _updateDatiEndRequestApplicabile_incrementCounter(long v) {
		this.distributedPolicyCounter.addAndGetAsync(v);
	}
	
	
	// Getters necessari poichè non viene aggiornato il field nella classe nonno DatiCollezionati, poichè si usa il metodo Async
	// Quindi in fase di lettura deve SEMPRE essere utilizzato il get
	
	@Override
	public Long getActiveRequestCounter(boolean readRemoteInfo) {
		if(this.distribuitedActiveRequestCounter_policyRichiesteSimultanee){
			return this.distributedActiveRequestCounterForCheck.get();
		}
		else {
			return this.distributedActiveRequestCounterForStats.get();
		}
	}
	@Override
	public Long getPolicyDenyRequestCounter(boolean readRemoteInfo) {
		if(this.distributedPolicyDenyRequestCounter!=null) {
			return this.distributedPolicyDenyRequestCounter.get();
		}
		else {
			return null;
		}
	}
	
	@Override
	public Long getPolicyRequestCounter(boolean readRemoteInfo) {
		if(this.distributedPolicyRequestCounter!=null) {
			return this.distributedPolicyRequestCounter.get();
		}
		else {
			return null;
		}
	}
	@Override
	public Long getPolicyCounter(boolean readRemoteInfo) {
		if(this.distributedPolicyCounter!=null) {
			return this.distributedPolicyCounter.get();
		}
		else {
			return null;
		}
	}	
	
	@Override
	public Long getPolicyDegradoPrestazionaleRequestCounter(boolean readRemoteInfo) {
		if(this.distributedPolicyDegradoPrestazionaleRequestCounter!=null) {
			return this.distributedPolicyDegradoPrestazionaleRequestCounter.get();
		}
		else {
			return null;
		}
	}
	@Override
	public Long getPolicyDegradoPrestazionaleCounter(boolean readRemoteInfo) {
		if(this.distributedPolicyDegradoPrestazionaleCounter!=null) {
			return this.distributedPolicyDegradoPrestazionaleCounter.get();
		}
		else {
			return null;
		}
	}

}
