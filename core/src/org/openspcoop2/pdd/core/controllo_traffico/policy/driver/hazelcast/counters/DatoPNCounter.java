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

import java.util.concurrent.CompletionStage;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.Utilities;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.crdt.pncounter.PNCounter;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;

/**
 * DatoPNCounter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DatoPNCounter {

	private HazelcastInstance hazelcast;
	private String name;
	
	private PNCounter counter;
	
	private int failover = -1;
	private int failoverCheckEveryMs = -1;

	private Logger logControlloTraffico;

	private boolean cleanupTimerEnabled = false;

	public DatoPNCounter(HazelcastInstance hazelcast, String name) {
		this.hazelcast = hazelcast;
		this.name = name;
		this.initCounter();
		OpenSPCoop2Properties op2Props = OpenSPCoop2Properties.getInstance();
		this.failover = op2Props.getHazelcastCPSubsystemDistributedObjectDestroyedExceptionFailover();
		this.failoverCheckEveryMs = op2Props.getHazelcastCPSubsystemDistributedObjectDestroyedExceptionFailoverCheckEveryMs();
		this.logControlloTraffico = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(op2Props.isControlloTrafficoDebug());
		this.cleanupTimerEnabled = op2Props.isControlloTrafficoGestorePolicyInMemoryHazelcastOrphanedProxiesCleanupEnabled();
	}
	private void initCounter() {
		this.counter = this.hazelcast.getPNCounter(this.name);
	}
	
	public String getName() {
		return this.name;
	}
	
	public long get() {
		PNCounterResponse r = process(PNCounterOperation.GET, -1, -1);
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}
	public long addAndGet(long value) {
		PNCounterResponse r = process(PNCounterOperation.ADD_AND_GET, value, -1);
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}
	public long incrementAndGet() {
		PNCounterResponse r = process(PNCounterOperation.INCREMENT_AND_GET, -1, -1);
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}
	public long decrementAndGet() {
		PNCounterResponse r = process(PNCounterOperation.DECREMENT_AND_GET, -1, -1);
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}
	public long subtractAndGet(long value) {
		PNCounterResponse r = process(PNCounterOperation.SUBTRACT_AND_GET, value, -1);
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}
	public void destroy() {
		process(PNCounterOperation.DESTROY, -1, -1);
	}
	/**
	 * Rilascia il contatore in modo sicuro, evitando la 'DistributedObjectDestroyedException'.
	 *
	 * Nel CP Subsystem di Hazelcast, una volta chiamato destroy(), il nome dell'oggetto resta "avvelenato"
	 * e non può essere ricreato: qualsiasi successiva getCPSubsystem().getAtomicLong(stessoNome)
	 * restituisce un oggetto in stato "destroyed", causando:
	 * 'com.hazelcast.spi.exception.DistributedObjectDestroyedException: AtomicValue[...] is already destroyed!'
	 *
	 * Questo si verifica quando un altro nodo del cluster sta ancora utilizzando il contatore
	 * nel momento in cui viene distrutto, anche se il destroy avviene su contatori di 2 intervalli indietro (pattern "cestino").
	 *
	 * Se il timer di cleanup dei contatori orfani è abilitato (default), il destroy non viene eseguito
	 * e la pulizia viene delegata al timer (HazelcastManager.cleanupOrphanedAtomicLongCounters).
	 * Se il timer è disabilitato, il destroy viene eseguito direttamente;
	 * eventuali errori vengono solo loggati senza propagare l'eccezione,
	 * come avviene nel timer di cleanup (HazelcastManager.cleanupOrphanedProxies).
	 */
	public void destroySafe() {
		if(this.cleanupTimerEnabled) {
			return;
		}
		try {
			destroy();
		} catch(Throwable t) {
			this.logControlloTraffico.error("[Hazelcast-PNCounter-"+this.name+"] destroySafe non riuscito: "+t.getMessage(),t);
		}
	}

	private PNCounterResponse process(PNCounterOperation op, long arg1, long arg2) {
		String prefix = "[Hazelcast-PNCounter-"+this.name+" operation:"+op+"] ";
		if(this.failover>0) {
			return processFailOver(prefix, op, arg1, arg2);
		}
		else {
			return operation(prefix, op, arg1, arg2);
		}
	}
	private PNCounterResponse processFailOver(String prefix, PNCounterOperation op, long arg1, long arg2) {
		boolean success = false;
		DistributedObjectDestroyedException eFinal = null;
		PNCounterResponse v = null;
		for (int i = 0; i < this.failover; i++) {
			try {
				if(i>0 && this.failoverCheckEveryMs>0) {
					Utilities.sleep(this.failoverCheckEveryMs);
					initCounter();
				}
				v = operation(prefix, op, arg1, arg2);
				success=true;
				break;
			} catch (DistributedObjectDestroyedException e) {
				eFinal = e;
				if(i==0) {
					this.logControlloTraffico.error(prefix+"rilevato contatore distrutto (verrà riprovata la creazione): "+e.getMessage(),e);
				}
				else {
					this.logControlloTraffico.error(prefix+"il tenativo i="+i+" di ricreare il contatore è fallito: "+e.getMessage(),e);
				}
			}
		}
		if(!success) {
			throwDistributedObjectDestroyedException(prefix, eFinal);
		}
		return v;
	}
	private void throwDistributedObjectDestroyedException(String prefix, DistributedObjectDestroyedException eFinal) {
		String msg = prefix+"tutti i tentativi di ricreare il contatore sono falliti";
		this.logControlloTraffico.error(msg);
		if(eFinal!=null) {
			throw eFinal;
		}
		else {
			throw new DistributedObjectDestroyedException("tutti i tentativi di ricreare il contatore sono falliti"); // l'eccezione eFinal esiste
		}
	}
	
	private PNCounterResponse operation(String prefix, PNCounterOperation op, long arg1, long arg2){
		switch (op) {
		case GET:
			return new PNCounterResponse(this.counter.get());
		case ADD_AND_GET:
			return new PNCounterResponse(this.counter.addAndGet(arg1));
		case INCREMENT_AND_GET:
			return new PNCounterResponse(this.counter.incrementAndGet());
		case DECREMENT_AND_GET:
			return new PNCounterResponse(this.counter.decrementAndGet());
		case SUBTRACT_AND_GET:
			return new PNCounterResponse(this.counter.subtractAndGet(arg1));
		case DESTROY:
			try {
				this.counter.destroy();
			}catch(Throwable e) {
				this.logControlloTraffico.error(prefix+"destroy non riuscito: "+e.getMessage(),e);
				throw e;
			}
			return null;		
		}
		return null;
	}
}

enum PNCounterOperation {
	GET,
	ADD_AND_GET, INCREMENT_AND_GET, DECREMENT_AND_GET, SUBTRACT_AND_GET,
	DESTROY
}

class PNCounterResponse{
	PNCounterResponse(long l){
		this.valueL = l;
	}
	PNCounterResponse(boolean b){
		this.valueB = b;
	}
	PNCounterResponse(CompletionStage<Long> v){
		this.valueAsync = v;
	}
	long valueL;	
	boolean valueB;
	CompletionStage<Long> valueAsync;
}
