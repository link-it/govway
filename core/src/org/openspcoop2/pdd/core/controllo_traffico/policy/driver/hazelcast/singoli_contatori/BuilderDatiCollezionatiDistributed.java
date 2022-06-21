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

package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.singoli_contatori;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.HazelcastManager;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.TipoDatiCollezionati;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson.DatiCollezionatiDistributedLongAdder;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson.DatiCollezionatiDistributedRedisAtomicLong;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson.RedissonManager;
import org.redisson.api.RedissonClient;

import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @author Francesco Scarlato
 *
 */
public class BuilderDatiCollezionatiDistributed {

	@Deprecated
	public static DatiCollezionati build(TipoDatiCollezionati tipoDatiCollezionati,DatiCollezionati dati, HazelcastInstance hazelcast, String uniquePrefixId) {
		DatiCollezionati ret;
		
		switch (tipoDatiCollezionati) {
		case ATOMIC_LONG:
			ret = new DatiCollezionatiDistributedAtomicLong(dati, hazelcast, uniquePrefixId);
			break;
		case ATOMIC_LONG_ASYNC:
			ret = new DatiCollezionatiDistributedAtomicLongAsync(dati, hazelcast, uniquePrefixId);
			break;
		case PNCOUNTER:
			ret = new DatiCollezionatiDistributedPNCounter(dati, hazelcast, uniquePrefixId);
			break;
		default:
			throw new RuntimeException("TipoDatiCollezionati sconosciuto: " + tipoDatiCollezionati);
		}
		
		return ret;
	}
	
	
	@Deprecated
	public static DatiCollezionati build(TipoDatiCollezionati tipoDatiCollezionati, Date updatePolicyDate, HazelcastInstance hazelcast, String uniquePrefixId) {
		DatiCollezionati ret;
		
		switch (tipoDatiCollezionati) {
		case ATOMIC_LONG:
			ret = new DatiCollezionatiDistributedAtomicLong(updatePolicyDate, hazelcast, uniquePrefixId);
			break;
		case ATOMIC_LONG_ASYNC:
			ret = new DatiCollezionatiDistributedAtomicLongAsync(updatePolicyDate, hazelcast, uniquePrefixId);
			break;
		case PNCOUNTER:
			ret = new DatiCollezionatiDistributedPNCounter(updatePolicyDate, hazelcast, uniquePrefixId);
			break;
		default:
			throw new RuntimeException("TipoDatiCollezionati sconosciuto: " + tipoDatiCollezionati);
		}
		
		return ret;
	}
	
	public final PolicyGroupByActiveThreadsType tipoPolicy;
	private final HazelcastInstance hazelcast;
	private final RedissonClient  redisson;
	
	
	private static  final HashMap<PolicyGroupByActiveThreadsType, BuilderDatiCollezionatiDistributed> builderCache = new HashMap<>();
	private static final List<PolicyGroupByActiveThreadsType> policyAmmesse = List.of(
			PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG, 
			PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG_ASYNC,
			PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER,
			PolicyGroupByActiveThreadsType.REDISSON_ATOMIC_LONG,
			PolicyGroupByActiveThreadsType.REDISSON_LONGADDER);
			
	
	private BuilderDatiCollezionatiDistributed(PolicyGroupByActiveThreadsType tipoPolicy) throws PolicyException {
		
		if (!policyAmmesse.contains(tipoPolicy)) {
			throw new PolicyException("Tipo Policy " + tipoPolicy + " non supportato per il BuilderDatiCollezionatiDistributed, utilizzare una delle policyAmmesse.");
		}
		
		this.tipoPolicy = tipoPolicy;
		
		if (tipoPolicy.isHazelcast() ) {
			this.hazelcast = HazelcastManager.getInstance(this.tipoPolicy);
			this.redisson  = null;
		} else {
			this.redisson = RedissonManager.getRedissonClient();
			this.hazelcast = null;
		}
	}
	
	
	public DatiCollezionati build(DatiCollezionati dati, String uniquePrefixId) {
		DatiCollezionati ret;
		
		switch (this.tipoPolicy) {
		case HAZELCAST_ATOMIC_LONG:
			ret = new DatiCollezionatiDistributedAtomicLong(dati, this.hazelcast, uniquePrefixId);
			break;
		case HAZELCAST_ATOMIC_LONG_ASYNC:
			ret = new DatiCollezionatiDistributedAtomicLongAsync(dati, this.hazelcast, uniquePrefixId);
			break;
		case HAZELCAST_PNCOUNTER:
			ret = new DatiCollezionatiDistributedPNCounter(dati, this.hazelcast, uniquePrefixId);
			break;
		case REDISSON_ATOMIC_LONG:
			ret = new DatiCollezionatiDistributedRedisAtomicLong(dati,  this.redisson, uniquePrefixId);
			break;
		case REDISSON_LONGADDER:
			ret = new DatiCollezionatiDistributedLongAdder(dati, this.redisson, uniquePrefixId);
			break;
		default:
			throw new RuntimeException("Tipo Policy" + this.tipoPolicy + " Sconosciuto per il builder. Sono controllati nel costruttore, non dovrebbe mai accadere!");
		}
		
		return ret;
	}
	
	
	public DatiCollezionati build(Date updatePolicyDate, String uniquePrefixId) {
		DatiCollezionati ret;
		
		switch (this.tipoPolicy) {
		case HAZELCAST_ATOMIC_LONG:
			ret = new DatiCollezionatiDistributedAtomicLong(updatePolicyDate, this.hazelcast, uniquePrefixId);
			break;
		case HAZELCAST_ATOMIC_LONG_ASYNC:
			ret = new DatiCollezionatiDistributedAtomicLongAsync(updatePolicyDate, this.hazelcast, uniquePrefixId);
			break;
		case HAZELCAST_PNCOUNTER:
			ret = new DatiCollezionatiDistributedPNCounter(updatePolicyDate, this.hazelcast, uniquePrefixId);
			break;
		case REDISSON_ATOMIC_LONG:
			ret = new DatiCollezionatiDistributedRedisAtomicLong(updatePolicyDate, this.redisson, uniquePrefixId);
			break;
		case REDISSON_LONGADDER:
			ret = new DatiCollezionatiDistributedLongAdder(updatePolicyDate, this.redisson, uniquePrefixId);
			break;
		default:
			throw new RuntimeException("Tipo Policy" + this.tipoPolicy + " Sconosciuto per il builder. Sono controllati nel costruttore, non dovrebbe mai accadere!");
		}
		
		return ret;	
	}

	
	public static BuilderDatiCollezionatiDistributed getBuilder(PolicyGroupByActiveThreadsType type) throws PolicyException {
		
		var ret = builderCache.get(type);
		if (ret == null) {
			ret = new BuilderDatiCollezionatiDistributed(type);
			builderCache.put(type,  ret);
		}

		return ret;
	}
	
	
}
