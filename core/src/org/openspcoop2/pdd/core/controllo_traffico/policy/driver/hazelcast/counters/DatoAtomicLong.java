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

import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.HazelcastManager;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.spi.exception.TargetNotMemberException;

/**
 * DatoAtomicLong
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DatoAtomicLong {

	private HazelcastInstance hazelcast;
	private String name;

	private IAtomicLong counter;

	private int failover = -1;
	private int failoverCheckEveryMs = -1;

	private Logger logControlloTraffico;

	// Per il registry IMap (cleanup degli orfani)
	private PolicyGroupByActiveThreadsType policyType;
	private boolean registryEnabled = false;

	public DatoAtomicLong(HazelcastInstance hazelcast, String name) {
		this(hazelcast, name, null);
	}

	public DatoAtomicLong(HazelcastInstance hazelcast, String name, PolicyGroupByActiveThreadsType policyType) {
		this.hazelcast = hazelcast;
		this.name = name;
		this.policyType = policyType;
		this.initCounter();
		OpenSPCoop2Properties op2Props = OpenSPCoop2Properties.getInstance();
		this.failover = op2Props.getHazelcastCPSubsystemDistributedObjectDestroyedExceptionFailover();
		this.failoverCheckEveryMs = op2Props.getHazelcastCPSubsystemDistributedObjectDestroyedExceptionFailoverCheckEveryMs();
		this.logControlloTraffico = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(op2Props.isControlloTrafficoDebug());

		// Registra nel registry se abilitato e il tipo è specificato
		if(this.policyType != null && op2Props.isControlloTrafficoGestorePolicyInMemoryHazelcastAtomicLongRegistryEnabled()) {
			this.registryEnabled = true;
			HazelcastManager.registerAtomicLongCounter(this.policyType, this.name);
		}
	}
	private void initCounter() {
		this.counter = this.hazelcast.getCPSubsystem().getAtomicLong(this.name);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void set(long value) {
		process(AtomicLongOperation.SET, value, -1);
	}
	public long get() {
		AtomicLongResponse r = process(AtomicLongOperation.GET, -1, -1);
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}
	public long addAndGet(long value) {
		AtomicLongResponse r = process(AtomicLongOperation.ADD_AND_GET, value, -1);
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}
	public long incrementAndGet() {
		AtomicLongResponse r = process(AtomicLongOperation.INCREMENT_AND_GET, -1, -1);
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}
	public long decrementAndGet() {
		AtomicLongResponse r = process(AtomicLongOperation.DECREMENT_AND_GET, -1, -1);
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}
	public CompletionStage<Long> addAndGetAsync(long value) {
		AtomicLongResponse r = process(AtomicLongOperation.ADD_AND_GET_ASYNC, value, -1);
		return r!=null ? r.valueAsync : null; // else non dovrebbe succedere mai
	}
	public CompletionStage<Long> incrementAndGetAsync() {
		AtomicLongResponse r = process(AtomicLongOperation.INCREMENT_AND_GET_ASYNC, -1, -1);
		return r!=null ? r.valueAsync : null; // else non dovrebbe succedere mai
	}
	public CompletionStage<Long> decrementAndGetAsync() {
		AtomicLongResponse r = process(AtomicLongOperation.DECREMENT_AND_GET_ASYNC, -1, -1);
		return r!=null ? r.valueAsync : null; // else non dovrebbe succedere mai
	}
	public boolean compareAndSet(long compare, long set) {
		AtomicLongResponse r = process(AtomicLongOperation.COMPARE_AND_SET, compare, set);
		return r!=null && r.valueB; // else non dovrebbe succedere mai
	}
	public void destroy() {
		process(AtomicLongOperation.DESTROY, -1, -1);
		// Deregistra dal registry dopo aver distrutto con successo
		// Se destroy fallisce (lancia eccezione), il contatore rimane nel registry per tentativi futuri
		if(this.registryEnabled && this.policyType != null) {
			HazelcastManager.unregisterAtomicLongCounter(this.policyType, this.name);
		}
	}
	
	private AtomicLongResponse process(AtomicLongOperation op, long arg1, long arg2) {
		String prefix = "[Hazelcast-IAtomicLong-"+this.name+" operation:"+op+"] ";
		if(this.failover>0) {
			return processFailOver(prefix, op, arg1, arg2);
		}
		else {
			return operation(prefix, op, arg1, arg2);
		}
	}
	private AtomicLongResponse processFailOver(String prefix, AtomicLongOperation op, long arg1, long arg2) {
		boolean success = false;
		RuntimeException eFinal = null;
		AtomicLongResponse v = null;
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
			} catch (RuntimeException e) {
				// Controlla se è causata da MemberLeftException o TargetNotMemberException
				Throwable cause = e.getCause();
				String exceptionName = e.getClass().getSimpleName();
				String causeName = cause != null ? cause.getClass().getSimpleName() : "null";

				if (isMemberRelatedError(e, cause, exceptionName, causeName)) {
					eFinal = e;
					if(i==0) {
						this.logControlloTraffico.error(prefix+"errore relativo a membro cluster ("+exceptionName+"/"+causeName+") durante operazione (verrà riprovata l'operazione): "+e.getMessage(),e);
					}
					else {
						this.logControlloTraffico.error(prefix+"il tenativo i="+i+" è fallito per errore membro cluster ("+exceptionName+"/"+causeName+"): "+e.getMessage(),e);
					}
				} else {
					// Se non è un errore relativo ai membri, rilancia immediatamente
					throw e;
				}
			}
		}
		if(!success) {
			throwDistributedObjectDestroyedException(prefix, eFinal);
		}
		return v;
	}

	private boolean isMemberRelatedError(Throwable e, Throwable cause, String exceptionName, String causeName) {
		// Controlla prima con instanceof per verifica diretta del tipo
		if (e instanceof MemberLeftException || e instanceof TargetNotMemberException) {
			return true;
		}
		if (cause instanceof MemberLeftException || cause instanceof TargetNotMemberException) {
			return true;
		}

		// Controlla per nome della classe (fallback se instanceof non funziona)
		if (exceptionName.contains("MemberLeft") || exceptionName.contains("TargetNotMember")) {
			return true;
		}
		if (causeName.contains("MemberLeft") || causeName.contains("TargetNotMember")) {
			return true;
		}

		// Controlla anche nel messaggio dell'eccezione
		String message = e.getMessage();
		return message != null && (message.contains("Member") && message.contains("left cluster") ||
		                        message.contains("Not Member") ||
		                        message.contains("target") && message.contains("not") && message.contains("member"));
	}

	private void throwDistributedObjectDestroyedException(String prefix, RuntimeException eFinal) {
		String msg = prefix+"tutti i tentativi di eseguire l'operazione sul contatore sono falliti";
		this.logControlloTraffico.error(msg);
		if(eFinal!=null) {
			throw eFinal;
		}
		else {
			throw new UtilsRuntimeException("tutti i tentativi di eseguire l'operazione sul contatore sono falliti");
		}
	}
	
	private AtomicLongResponse operation(String prefix, AtomicLongOperation op, long arg1, long arg2){
		switch (op) {
		case SET:
			this.counter.set(arg1);
			return null;
		case GET:
			return new AtomicLongResponse(this.counter.get());
		case ADD_AND_GET:
			return new AtomicLongResponse(this.counter.addAndGet(arg1));
		case INCREMENT_AND_GET:
			return new AtomicLongResponse(this.counter.incrementAndGet());
		case DECREMENT_AND_GET:
			return new AtomicLongResponse(this.counter.decrementAndGet());
		case ADD_AND_GET_ASYNC:
			return new AtomicLongResponse(this.counter.addAndGetAsync(arg1));
		case INCREMENT_AND_GET_ASYNC:
			return new AtomicLongResponse(this.counter.incrementAndGetAsync());
		case DECREMENT_AND_GET_ASYNC:
			return new AtomicLongResponse(this.counter.decrementAndGetAsync());
		case COMPARE_AND_SET:
			return new AtomicLongResponse(this.counter.compareAndSet(arg1, arg2));
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

enum AtomicLongOperation {
	SET,
	GET,
	ADD_AND_GET, INCREMENT_AND_GET, DECREMENT_AND_GET,
	ADD_AND_GET_ASYNC, INCREMENT_AND_GET_ASYNC, DECREMENT_AND_GET_ASYNC,
	COMPARE_AND_SET,
	DESTROY
}

class AtomicLongResponse{
	AtomicLongResponse(long l){
		this.valueL = l;
	}
	AtomicLongResponse(boolean b){
		this.valueB = b;
	}
	AtomicLongResponse(CompletionStage<Long> v){
		this.valueAsync = v;
	}
	long valueL;	
	boolean valueB;
	CompletionStage<Long> valueAsync;
}
