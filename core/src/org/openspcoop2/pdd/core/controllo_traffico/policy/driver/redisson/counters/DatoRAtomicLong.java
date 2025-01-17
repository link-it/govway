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

import java.util.concurrent.CompletionStage;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;

/**
 * DatoRAtomicLong
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DatoRAtomicLong {

	private RedissonClient redisson;
	private String name;
	
	private RAtomicLong counter;
	
	private int failover = -1;
	public void setFailover(int failover) {
		this.failover = failover;
	}
	private int failoverCheckEveryMs = -1;
	
	private Logger logControlloTraffico;
	
	public DatoRAtomicLong(RedissonClient redisson, String name) {
		this.redisson = redisson;
		this.name = name;
		this.initCounter();
		OpenSPCoop2Properties op2Props = OpenSPCoop2Properties.getInstance();
		this.failover = -1; // da gestire in futuro se serve
		this.failoverCheckEveryMs = -1;
		this.logControlloTraffico = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(op2Props.isControlloTrafficoDebug());
	}
	private void initCounter() {
		this.counter = this.redisson.getAtomicLong(this.name);
	}
	
	public void set(long value) {
		process(RAtomicLongOperation.SET, value, -1);
	}
	public long get() {
		RAtomicLongResponse r = process(RAtomicLongOperation.GET, -1, -1);
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}
	public long addAndGet(long value) {
		RAtomicLongResponse r = process(RAtomicLongOperation.ADD_AND_GET, value, -1);
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}
	public long incrementAndGet() {
		RAtomicLongResponse r = process(RAtomicLongOperation.INCREMENT_AND_GET, -1, -1);
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}
	public long decrementAndGet() {
		RAtomicLongResponse r = process(RAtomicLongOperation.DECREMENT_AND_GET, -1, -1);
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}
	public CompletionStage<Long> addAndGetAsync(long value) {
		RAtomicLongResponse r = process(RAtomicLongOperation.ADD_AND_GET_ASYNC, value, -1);
		return r!=null ? r.valueAsync : null; // else non dovrebbe succedere mai
	}
	public CompletionStage<Long> incrementAndGetAsync() {
		RAtomicLongResponse r = process(RAtomicLongOperation.INCREMENT_AND_GET_ASYNC, -1, -1);
		return r!=null ? r.valueAsync : null; // else non dovrebbe succedere mai
	}
	public CompletionStage<Long> decrementAndGetAsync() {
		RAtomicLongResponse r = process(RAtomicLongOperation.DECREMENT_AND_GET_ASYNC, -1, -1);
		return r!=null ? r.valueAsync : null; // else non dovrebbe succedere mai
	}
	public boolean compareAndSet(long compare, long set) {
		RAtomicLongResponse r = process(RAtomicLongOperation.COMPARE_AND_SET, compare, set);
		return r!=null && r.valueB; // else non dovrebbe succedere mai
	}
	public void delete() {
		process(RAtomicLongOperation.DELETE, -1, -1);
	}
	
	private RAtomicLongResponse process(RAtomicLongOperation op, long arg1, long arg2) {
		String prefix = "[Redis-RAtomicLong-"+this.name+" operation:"+op+"] ";
		if(this.failover>0) {
			return processFailOver(prefix, op, arg1, arg2);
		}
		else {
			return operation(prefix, op, arg1, arg2);
		}
	}
	private RAtomicLongResponse processFailOver(String prefix, RAtomicLongOperation op, long arg1, long arg2) {
		boolean success = false;
		Exception eFinal = null; // capire l'eccezione
		RAtomicLongResponse v = null;
		for (int i = 0; i < this.failover; i++) {
			try {
				if(i>0 && this.failoverCheckEveryMs>0) {
					Utilities.sleep(this.failoverCheckEveryMs);
					initCounter();
				}
				v = operation(prefix, op, arg1, arg2);
				success=true;
				break;
			} catch (Exception e) {
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
	private void throwDistributedObjectDestroyedException(String prefix, Exception eFinal) {
		String msg = prefix+"tutti i tentativi di ricreare il contatore sono falliti";
		this.logControlloTraffico.error(msg);
		if(eFinal!=null) {
			throw new UtilsRuntimeException(eFinal);
		}
		else {
			throw new UtilsRuntimeException("tutti i tentativi di ricreare il contatore sono falliti"); // l'eccezione eFinal esiste
		}
	}
	
	private RAtomicLongResponse operation(String prefix, RAtomicLongOperation op, long arg1, long arg2){
		switch (op) {
		case SET:
			this.counter.set(arg1);
			return null;
		case GET:
			return new RAtomicLongResponse(this.counter.get());
		case ADD_AND_GET:
			return new RAtomicLongResponse(this.counter.addAndGet(arg1));
		case INCREMENT_AND_GET:
			return new RAtomicLongResponse(this.counter.incrementAndGet());
		case DECREMENT_AND_GET:
			return new RAtomicLongResponse(this.counter.decrementAndGet());
		case ADD_AND_GET_ASYNC:
			return new RAtomicLongResponse(this.counter.addAndGetAsync(arg1));
		case INCREMENT_AND_GET_ASYNC:
			return new RAtomicLongResponse(this.counter.incrementAndGetAsync());
		case DECREMENT_AND_GET_ASYNC:
			return new RAtomicLongResponse(this.counter.decrementAndGetAsync());
		case COMPARE_AND_SET:
			return new RAtomicLongResponse(this.counter.compareAndSet(arg1, arg2));
		case DELETE:
			try {
				this.counter.delete();
			}catch(Throwable e) {
				this.logControlloTraffico.error(prefix+"delete non riuscito: "+e.getMessage(),e);
				throw e;
			}
			return null;		
		}
		return null;
	}
}

enum RAtomicLongOperation {
	SET,
	GET,
	ADD_AND_GET, INCREMENT_AND_GET, DECREMENT_AND_GET,
	ADD_AND_GET_ASYNC, INCREMENT_AND_GET_ASYNC, DECREMENT_AND_GET_ASYNC,
	COMPARE_AND_SET,
	DELETE
}

class RAtomicLongResponse{
	RAtomicLongResponse(long l){
		this.valueL = l;
	}
	RAtomicLongResponse(boolean b){
		this.valueB = b;
	}
	RAtomicLongResponse(CompletionStage<Long> v){
		this.valueAsync = v;
	}
	long valueL;	
	boolean valueB;
	CompletionStage<Long> valueAsync;
}
