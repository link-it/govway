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

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.redisson.api.RLongAdder;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;

/**
 * DatoRLongAdder - Wrapper per RLongAdder di Redisson con supporto TTL
 *
 * Questa classe gestisce contatori LongAdder distribuiti in Redis con supporto
 * opzionale per TTL (Time To Live), permettendo la pulizia automatica dei
 * contatori non più utilizzati.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatoRLongAdder {

	private RedissonClient redisson;
	private String name;

	private RLongAdder counter;

	// TTL configuration
	private RedisTTLConfig ttlConfig;
	private boolean ttlApplied = false;

	private int failover = -1;
	public void setFailover(int failover) {
		this.failover = failover;
	}
	private int failoverCheckEveryMs = -1;

	private Logger logControlloTraffico;
	private void logDebug(String msg) {
		if (this.logControlloTraffico != null) {
			this.logControlloTraffico.debug(msg);
		}
	}
	private void logWarn(String msg) {
		if (this.logControlloTraffico != null) {
			this.logControlloTraffico.warn(msg);
		}
	}

	/**
	 * Costruttore originale senza TTL (mantiene compatibilità)
	 */
	public DatoRLongAdder(RedissonClient redisson, String name) {
		this(redisson, name, null);
	}

	/**
	 * Costruttore con configurazione TTL
	 */
	public DatoRLongAdder(RedissonClient redisson, String name, RedisTTLConfig ttlConfig) {
		this.redisson = redisson;
		this.name = name;
		this.ttlConfig = ttlConfig;
		this.initCounter();
		OpenSPCoop2Properties op2Props = OpenSPCoop2Properties.getInstance();
		this.failover = -1; // da gestire in futuro se serve
		this.failoverCheckEveryMs = -1;
		this.logControlloTraffico = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(op2Props.isControlloTrafficoDebug());
	}

	private void initCounter() {
		this.counter = this.redisson.getLongAdder(this.name);
	}

	/**
	 * Applica il TTL al contatore se configurato e non ancora applicato
	 */
	private void applyTTLIfNeeded() {
		if (this.ttlConfig != null && this.ttlConfig.isEnabled() && !this.ttlApplied) {
			try {
				long ttlSeconds = this.ttlConfig.getTtlSeconds();
				if (ttlSeconds > 0) {
					applyTTLIfNeededAsync(ttlSeconds); 
				}
			} catch (Exception e) {
				if (this.logControlloTraffico != null) {
					this.logWarn("[Redis-TTL] Impossibile applicare TTL al LongAdder " +
						this.name + ": " + e.getMessage());
				}
			}
		}
	}
	private void applyTTLIfNeededAsync(long ttlSeconds) {
		// expire() ritorna true solo se la chiave esiste e il TTL è stato applicato
		boolean applied = this.counter.expire(Duration.ofSeconds(ttlSeconds));
		if (applied) {
			this.ttlApplied = true;
			if (this.logControlloTraffico != null && this.logControlloTraffico.isDebugEnabled()) {
				this.logDebug("[Redis-TTL] Applicato TTL di " + ttlSeconds +
					" secondi al LongAdder: " + this.name);
			}
		}
		// Se applied è false, la chiave non esiste ancora - riproveremo alla prossima operazione
	}

	/**
	 * Rinnova il TTL se configurato per farlo ad ogni scrittura
	 */
	private void renewTTLIfNeeded() {
		if (this.ttlConfig != null && this.ttlConfig.isEnabled() && this.ttlConfig.isRenewTTLOnWrite()) {
			try {
				long ttlSeconds = this.ttlConfig.getTtlSeconds();
				if (ttlSeconds > 0) {
					this.counter.expire(Duration.ofSeconds(ttlSeconds));
				}
			} catch (Exception e) {
				if (this.logControlloTraffico != null && this.logControlloTraffico.isDebugEnabled()) {
					this.logDebug("[Redis-TTL] Impossibile rinnovare TTL per LongAdder " +
						this.name + ": " + e.getMessage());
				}
			}
		}
	}

	public long sum() {
		RLongAdderResponse r = process(RLongAdderOperation.SUM, -1, -1);
		// Non rinnoviamo TTL su sum() per evitare che letture passive mantengano vivo il contatore
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}

	public void add(long value) {
		process(RLongAdderOperation.ADD, value, -1);
		applyTTLIfNeeded();
		renewTTLIfNeeded();
	}

	public void increment() {
		process(RLongAdderOperation.INCREMENT, -1, -1);
		applyTTLIfNeeded();
		renewTTLIfNeeded();
	}

	public void decrement() {
		process(RLongAdderOperation.DECREMENT, -1, -1);
		// Non rinnoviamo TTL su decrement per permettere la naturale scadenza
	}

	public void reset() {
		process(RLongAdderOperation.RESET, -1, -1);
	}

	public void destroy() {
		process(RLongAdderOperation.DESTROY, -1, -1);
	}

	/**
	 * Restituisce il tempo rimanente prima della scadenza del TTL (in millisecondi)
	 * @return tempo rimanente in ms, -1 se nessun TTL, -2 se il contatore non esiste
	 */
	public long remainTimeToLive() {
		try {
			return this.counter.remainTimeToLive();
		} catch (Exception e) {
			if (this.logControlloTraffico != null && this.logControlloTraffico.isDebugEnabled()) {
				this.logDebug("[Redis-TTL] Impossibile ottenere TTL rimanente per LongAdder " +
					this.name + ": " + e.getMessage());
			}
			return -2;
		}
	}

	/**
	 * Verifica se il contatore esiste in Redis
	 */
	public boolean exists() {
		try {
			return this.counter.isExists();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Imposta esplicitamente un nuovo TTL
	 */
	public void setTTL(long ttlSeconds) {
		if (ttlSeconds > 0) {
			try {
				this.counter.expire(Duration.ofSeconds(ttlSeconds));
			} catch (Exception e) {
				if (this.logControlloTraffico != null) {
					this.logWarn("[Redis-TTL] Impossibile impostare TTL per LongAdder " +
						this.name + ": " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Rimuove il TTL dal contatore (diventa persistente)
	 */
	public void clearTTL() {
		try {
			this.counter.clearExpire();
		} catch (Exception e) {
			if (this.logControlloTraffico != null) {
				this.logWarn("[Redis-TTL] Impossibile rimuovere TTL per LongAdder " +
					this.name + ": " + e.getMessage());
			}
		}
	}

	/**
	 * Restituisce la configurazione TTL corrente
	 */
	public RedisTTLConfig getTTLConfig() {
		return this.ttlConfig;
	}

	private RLongAdderResponse process(RLongAdderOperation op, long arg1, long arg2) {
		String prefix = "[Redis-RLongAdder-"+this.name+" operation:"+op+"] ";
		if(this.failover>0) {
			return processFailOver(prefix, op, arg1, arg2);
		}
		else {
			return operation(prefix, op, arg1, arg2);
		}
	}

	private RLongAdderResponse processFailOver(String prefix, RLongAdderOperation op, long arg1, long arg2) {
		boolean success = false;
		Exception eFinal = null; // capire l'eccezione
		RLongAdderResponse v = null;
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

	private RLongAdderResponse operation(String prefix, RLongAdderOperation op, long arg1, long arg2){
		
		if(arg2>0) {
			// nop
		}
		
		switch (op) {
		case SUM:
			return new RLongAdderResponse(this.counter.sum());
		case ADD:
			this.counter.add(arg1);
			return null;
		case INCREMENT:
			this.counter.increment();
			return null;
		case DECREMENT:
			this.counter.decrement();
			return null;
		case RESET:
			this.counter.reset();
			return null;
		case DESTROY:
			try {
				this.counter.destroy();
			}catch(Throwable e) {
				this.logControlloTraffico.error(prefix+"delete non riuscito: "+e.getMessage(),e);
				throw e;
			}
			return null;
		}
		return null;
	}
}

enum RLongAdderOperation {
	SUM,
	ADD, INCREMENT, DECREMENT,
	RESET,
	DESTROY
}

class RLongAdderResponse{
	RLongAdderResponse(long l){
		this.valueL = l;
	}
	RLongAdderResponse(boolean b){
		this.valueB = b;
	}
	RLongAdderResponse(CompletionStage<Long> v){
		this.valueAsync = v;
	}
	long valueL;
	boolean valueB;
	CompletionStage<Long> valueAsync;
}
