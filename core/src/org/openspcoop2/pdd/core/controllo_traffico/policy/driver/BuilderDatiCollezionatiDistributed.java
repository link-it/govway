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

package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicyMapId;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyDateUtils;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.HazelcastManager;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.counters.DatiCollezionatiDistributedAtomicLong;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.counters.DatiCollezionatiDistributedAtomicLongAsync;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.counters.DatiCollezionatiDistributedPNCounter;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson.RedissonManager;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson.counters.DatiCollezionatiDistributedLongAdder;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson.counters.DatiCollezionatiDistributedRedisAtomicLong;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.Map;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;

/**
 * BuilderDatiCollezionatiDistributed
 * 
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BuilderDatiCollezionatiDistributed implements Serializable {
	
	// - c config: configuration date
	// - i intervallo: intervallo di validità del contatore
	
	private static final long serialVersionUID = 1L;
	
	public final static String DISTRUBUITED_UPDATE_POLICY_DATE = "-updatePolicyDate-c-";
	public final static String DISTRUBUITED_POLICY_DATE = "-policyDate-c";
	
	public final static String DISTRUBUITED_SUFFIX_CONFIG_DATE = "-c-";
	
	public final static String DISTRUBUITED_INTERVAL_POLICY_REQUEST_COUNTER = "-policyRequestCounter-i-";
	public final static String DISTRUBUITED_INTERVAL_POLICY_COUNTER = "-policyCounter-i-";
	
	public final static String DISTRUBUITED_POLICY_DEGRADO_PRESTAZIONALE_DATE = "-policyDegradoPrestazionaleDate-c-";
	public final static String DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_COUNTER = "-policyDegradoPrestazionaleCounter-i-";
	public final static String DISTRUBUITED_INTERVAL_POLICY_DEGRADO_PRESTAZIONALE_REQUEST_COUNTER = "-policyDegradoPrestazionaleRequestCounter-i-";
	
	public final static String DISTRUBUITED_ACTIVE_REQUEST_COUNTER = "-activeRequestCounter-c-";
	
	public final static String DISTRUBUITED_INTERVAL_POLICY_DENY_REQUEST_COUNTER = "-policyDenyRequestCounter-i-";
	
	
	public final PolicyGroupByActiveThreadsType tipoPolicy;
	
	private transient HazelcastInstance _hazelcast = null;
	private synchronized void initHazelcast() throws PolicyException {
		if(this._hazelcast==null) {
			this._hazelcast = HazelcastManager.getInstance(this.tipoPolicy);
		}
	}
	public HazelcastInstance getHazelcast() throws RuntimeException{
		if(this._hazelcast==null) {
			try {
				initHazelcast();
			}catch(Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		return this._hazelcast;
	}
	
	private transient RedissonClient _redisson = null;
	private synchronized void initRedisson() throws PolicyException {
		if(this._redisson==null) {
			boolean throwInitializingException = true;
			try {
				this._redisson = RedissonManager.getRedissonClient(throwInitializingException);
			}catch(Exception e) {
				throw new PolicyException(e.getMessage(),e);
			}
		}
	}
	public RedissonClient getRedisson() throws RuntimeException{
		if(this._redisson==null) {
			try {
				initRedisson();
			}catch(Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		return this._redisson;
	}
	
	
	private static final java.util.Map<PolicyGroupByActiveThreadsType, BuilderDatiCollezionatiDistributed> builderCache = new HashMap<>();
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
			this._hazelcast = HazelcastManager.getInstance(this.tipoPolicy);
			this._redisson  = null;
		} else {
			boolean throwInitializingException = true;
			try {
				this._redisson = RedissonManager.getRedissonClient(throwInitializingException);
			}catch(Exception e) {
				throw new PolicyException(e.getMessage(),e);
			}
			this._hazelcast = null;
		}
	}
	
	
	public DatiCollezionati build(DatiCollezionati dati, IDUnivocoGroupByPolicyMapId id, ActivePolicy activePolicy) {
		DatiCollezionati ret;
		
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(OpenSPCoop2Properties.getInstance().isControlloTrafficoDebug());
		
		switch (this.tipoPolicy) {
		case HAZELCAST_ATOMIC_LONG:
			ret = new DatiCollezionatiDistributedAtomicLong(log, dati, this.getHazelcast(), id, activePolicy);
			break;
		case HAZELCAST_ATOMIC_LONG_ASYNC:
			ret = new DatiCollezionatiDistributedAtomicLongAsync(log, dati, this.getHazelcast(), id, activePolicy);
			break;
		case HAZELCAST_PNCOUNTER:
			ret = new DatiCollezionatiDistributedPNCounter(log, dati, this.getHazelcast(), id, activePolicy);
			break;
		case REDISSON_ATOMIC_LONG:
			ret = new DatiCollezionatiDistributedRedisAtomicLong(log, dati,  this.getRedisson(), id, activePolicy);
			break;
		case REDISSON_LONGADDER:
			ret = new DatiCollezionatiDistributedLongAdder(log, dati, this.getRedisson(), id, activePolicy);
			break;
		default:
			throw new RuntimeException("Tipo Policy" + this.tipoPolicy + " Sconosciuto per il builder. Sono controllati nel costruttore, non dovrebbe mai accadere!");
		}
		
		return ret;
	}
	
	
	public DatiCollezionati build(Date updatePolicyDate, IDUnivocoGroupByPolicyMapId id, ActivePolicy activePolicy, Map<Object> ctx) {
		DatiCollezionati ret;
		
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(OpenSPCoop2Properties.getInstance().isControlloTrafficoDebug());
		
		Date gestorePolicyConfigDate = PolicyDateUtils.readGestorePolicyConfigDateIntoContext(ctx);
				
		switch (this.tipoPolicy) {
		case HAZELCAST_ATOMIC_LONG:
			ret = new DatiCollezionatiDistributedAtomicLong(log, updatePolicyDate, gestorePolicyConfigDate, this.getHazelcast(), id, activePolicy);
			break;
		case HAZELCAST_ATOMIC_LONG_ASYNC:
			ret = new DatiCollezionatiDistributedAtomicLongAsync(log, updatePolicyDate, gestorePolicyConfigDate, this.getHazelcast(), id, activePolicy);
			break;
		case HAZELCAST_PNCOUNTER:
			ret = new DatiCollezionatiDistributedPNCounter(log, updatePolicyDate, gestorePolicyConfigDate, this.getHazelcast(), id, activePolicy);
			break;
		case REDISSON_ATOMIC_LONG:
			ret = new DatiCollezionatiDistributedRedisAtomicLong(log, updatePolicyDate, gestorePolicyConfigDate, this.getRedisson(), id, activePolicy);
			break;
		case REDISSON_LONGADDER:
			ret = new DatiCollezionatiDistributedLongAdder(log, updatePolicyDate, gestorePolicyConfigDate, this.getRedisson(), id, activePolicy);
			break;
		default:
			throw new RuntimeException("Tipo Policy" + this.tipoPolicy + " Sconosciuto per il builder. Sono controllati nel costruttore, non dovrebbe mai accadere!");
		}
		
		return ret;	
	}

	
	private static synchronized void initBuilder(PolicyGroupByActiveThreadsType type) throws PolicyException {
		
		var ret = builderCache.get(type);
		if (ret == null) {
			ret = new BuilderDatiCollezionatiDistributed(type);
			builderCache.put(type,  ret);
		}

	}
	public static BuilderDatiCollezionatiDistributed getBuilder(PolicyGroupByActiveThreadsType type) throws PolicyException {
		
		var ret = builderCache.get(type);
		if (ret == null) {
			initBuilder(type);
			ret = builderCache.get(type);
		}

		return ret;
	}
	
	
}
