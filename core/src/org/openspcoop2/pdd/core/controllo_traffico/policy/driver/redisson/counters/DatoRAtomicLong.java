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
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;

/**
 * DatoRAtomicLong - Wrapper per RAtomicLong di Redisson con supporto TTL
 *
 * Questa classe gestisce contatori atomici distribuiti in Redis con supporto
 * opzionale per TTL (Time To Live), permettendo la pulizia automatica dei
 * contatori non più utilizzati.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatoRAtomicLong {

	private RedissonClient redisson;
	private String name;

	private RAtomicLong counter;

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
	public DatoRAtomicLong(RedissonClient redisson, String name) {
		this(redisson, name, null);
	}

	/**
	 * Costruttore con configurazione TTL
	 */
	public DatoRAtomicLong(RedissonClient redisson, String name, RedisTTLConfig ttlConfig) {
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
		this.counter = this.redisson.getAtomicLong(this.name);
		// NOTA: il TTL viene applicato alla prima operazione di scrittura (set, increment, ecc.)
		// perché Redis non permette di impostare EXPIRE su chiavi che non esistono ancora.
		// getAtomicLong() non crea la chiave in Redis finché non viene fatta una scrittura.
	}

	/**
	 * Applica il TTL al contatore se configurato e non ancora applicato (versione sincrona)
	 */
	private void applyTTLIfNeeded() {
		if (this.ttlConfig != null && this.ttlConfig.isEnabled() && !this.ttlApplied) {
			try {
				long ttlSeconds = this.ttlConfig.getTtlSeconds();
				if (ttlSeconds > 0) {
					// expire() ritorna true solo se la chiave esiste e il TTL è stato applicato
					boolean applied = this.counter.expire(Duration.ofSeconds(ttlSeconds));
					if (applied) {
						this.ttlApplied = true;
						/** System.out.println("[DEBUG-TTL] TTL APPLIED OK for: " + this.name + " | ttl=" + ttlSeconds + "s"); */
					} else {
						/** DEBUG: capire perché expire() ritorna false
						boolean exists = this.counter.isExists();
						long currentTTL = this.counter.remainTimeToLive();
						System.out.println("[DEBUG-TTL] expire() returned FALSE for: " + this.name +
							" | exists=" + exists + " | currentTTL=" + currentTTL + " | ttlToSet=" + ttlSeconds); */
					}
				}
			} catch (Exception e) {
				/** System.out.println("[DEBUG-TTL] EXCEPTION in applyTTLIfNeeded for: " + this.name +
					" | " + e.getClass().getName() + ": " + e.getMessage()); */
				this.logWarn("[Redis-TTL] Impossibile applicare TTL al contatore " +
						this.name + ": " + e.getMessage());
			}
		}
	}

	/**
	 * Applica il TTL al contatore in modo asincrono (da usare nei callback async)
	 */
	private void applyTTLIfNeededAsync() {
		if (this.ttlConfig != null && this.ttlConfig.isEnabled() && !this.ttlApplied) {
			try {
				long ttlSeconds = this.ttlConfig.getTtlSeconds();
				if (ttlSeconds > 0) {
					applyTTLIfNeededAsync(ttlSeconds);
				}
			} catch (Exception e) {
				if (this.logControlloTraffico != null) {
					this.logWarn("[Redis-TTL] Impossibile applicare TTL async al contatore " +
						this.name + ": " + e.getMessage());
				}
			}
		}
	}
	private void applyTTLIfNeededAsync(long ttlSeconds) {
		// Usa expireAsync() per evitare "Sync methods can't be invoked from async listeners"
		this.counter.expireAsync(Duration.ofSeconds(ttlSeconds)).thenAccept(applied -> {
			if (applied != null && applied.booleanValue()) {
				this.ttlApplied = true;
				if (this.logControlloTraffico != null && this.logControlloTraffico.isDebugEnabled()) {
					this.logDebug("[Redis-TTL] Applicato TTL async di " + ttlSeconds +
						" secondi al contatore: " + this.name);
				}
			}
		});
	}

	/**
	 * Rinnova il TTL se configurato per farlo ad ogni scrittura (versione sincrona)
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
					this.logDebug("[Redis-TTL] Impossibile rinnovare TTL per " +
						this.name + ": " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Rinnova il TTL in modo asincrono (da usare nei callback async)
	 */
	private void renewTTLIfNeededAsync() {
		if (this.ttlConfig != null && this.ttlConfig.isEnabled() && this.ttlConfig.isRenewTTLOnWrite()) {
			try {
				long ttlSeconds = this.ttlConfig.getTtlSeconds();
				if (ttlSeconds > 0) {
					// Usa expireAsync() per evitare "Sync methods can't be invoked from async listeners"
					this.counter.expireAsync(Duration.ofSeconds(ttlSeconds));
				}
			} catch (Exception e) {
				if (this.logControlloTraffico != null && this.logControlloTraffico.isDebugEnabled()) {
					this.logDebug("[Redis-TTL] Impossibile rinnovare TTL async per " +
						this.name + ": " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Assicura che il TTL sia applicato al contatore, anche se è stato creato da un altro nodo.
	 * Da chiamare esplicitamente dopo operazioni di sola lettura (get) su contatori che devono avere un TTL.
	 * Utile per contatori come policyDate che potrebbero essere letti senza mai essere scritti da questo nodo.
	 *
	 * NOTA: Se la chiave non esiste ancora in Redis (get() ritorna 0 di default senza creare la chiave),
	 * il TTL non può essere applicato. In questo caso il flag ttlApplied NON viene impostato,
	 * così alla prossima scrittura (set, compareAndSet, ecc.) il TTL verrà applicato.
	 */
	public void ensureTTLApplied() {
		if (this.ttlConfig != null && this.ttlConfig.isEnabled() && !this.ttlApplied) {
			// Verifica se la chiave esiste prima di tentare di applicare il TTL
			// perché get() di Redisson ritorna 0 senza creare la chiave
			if (this.counter.isExists()) {
				applyTTLIfNeeded();
			}
			else {
				// Se la chiave non esiste, non facciamo nulla: il TTL sarà applicato
				// alla prima operazione di scrittura (set, compareAndSet, increment, ecc.)
			}
		}
	}

	public void set(long value) {
		process(RAtomicLongOperation.SET, value, -1);
		applyTTLIfNeeded();
		renewTTLIfNeeded();
	}

	public long get() {
		RAtomicLongResponse r = process(RAtomicLongOperation.GET, -1, -1);
		// Non rinnoviamo TTL su get() per evitare che letture passive mantengano vivo il contatore
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}

	public long addAndGet(long value) {
		RAtomicLongResponse r = process(RAtomicLongOperation.ADD_AND_GET, value, -1);
		applyTTLIfNeeded();
		renewTTLIfNeeded();
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}

	public long incrementAndGet() {
		RAtomicLongResponse r = process(RAtomicLongOperation.INCREMENT_AND_GET, -1, -1);
		applyTTLIfNeeded();
		renewTTLIfNeeded();
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}

	public long decrementAndGet() {
		RAtomicLongResponse r = process(RAtomicLongOperation.DECREMENT_AND_GET, -1, -1);
		// Non rinnoviamo TTL su decrement per permettere la naturale scadenza
		return r!=null ? r.valueL : -1; // else non dovrebbe succedere mai
	}

	public CompletionStage<Long> addAndGetAsync(long value) {
		RAtomicLongResponse r = process(RAtomicLongOperation.ADD_AND_GET_ASYNC, value, -1);
		if (r == null || r.valueAsync == null) {
			return null;
		}
		// Applica TTL dopo che l'operazione asincrona è completata,
		// quando la chiave esiste sicuramente in Redis.
		// Usa versioni async per evitare "Sync methods can't be invoked from async listeners"
		return r.valueAsync.thenApply(result -> {
			applyTTLIfNeededAsync();
			renewTTLIfNeededAsync();
			return result;
		});
	}

	public CompletionStage<Long> incrementAndGetAsync() {
		RAtomicLongResponse r = process(RAtomicLongOperation.INCREMENT_AND_GET_ASYNC, -1, -1);
		if (r == null || r.valueAsync == null) {
			return null;
		}
		// Applica TTL dopo che l'operazione asincrona è completata,
		// quando la chiave esiste sicuramente in Redis.
		// Usa versioni async per evitare "Sync methods can't be invoked from async listeners"
		return r.valueAsync.thenApply(result -> {
			applyTTLIfNeededAsync();
			renewTTLIfNeededAsync();
			return result;
		});
	}

	public CompletionStage<Long> decrementAndGetAsync() {
		RAtomicLongResponse r = process(RAtomicLongOperation.DECREMENT_AND_GET_ASYNC, -1, -1);
		// Non applichiamo/rinnoviamo TTL su decrement per permettere la naturale scadenza
		return r!=null ? r.valueAsync : null;
	}

	public boolean compareAndSet(long compare, long set) {
		RAtomicLongResponse r = process(RAtomicLongOperation.COMPARE_AND_SET, compare, set);
		boolean casResult = r!=null && r.valueB;
		/** System.out.println("[DEBUG-TTL] compareAndSet() for: " + this.name +
			" | compare=" + compare + " | set=" + set + " | result=" + casResult); */
		if (casResult) {
			// CAS ha avuto successo: la chiave è stata modificata.
			// Dobbiamo applicare/rinnovare il TTL SEMPRE (ignora il flag ttlApplied),
			// perché questo contatore (es. policyDate) può essere riutilizzato per più intervalli
			// e il TTL deve essere esteso ad ogni scrittura per evitare scadenza prematura.
			forceApplyTTL();
		} else {
			// CAS fallito: la chiave esiste già con valore diverso.
			// Applichiamo il TTL solo se non l'abbiamo già fatto (primo accesso da questo nodo).
			applyTTLIfNeeded();
		}
		return casResult;
	}

	/**
	 * Applica o rinnova forzatamente il TTL, ignorando il flag ttlApplied.
	 * Usato dopo operazioni che modificano il valore (compareAndSet con successo)
	 * per estendere la vita del contatore.
	 */
	private void forceApplyTTL() {
		if (this.ttlConfig != null && this.ttlConfig.isEnabled()) {
			try {
				long ttlSeconds = this.ttlConfig.getTtlSeconds();
				if (ttlSeconds > 0) {
					boolean applied = this.counter.expire(Duration.ofSeconds(ttlSeconds));
					if (applied) {
						this.ttlApplied = true;
						/** System.out.println("[DEBUG-TTL] TTL FORCED OK for: " + this.name + " | ttl=" + ttlSeconds + "s"); */
					} else {
						/** System.out.println("[DEBUG-TTL] forceApplyTTL() expire returned FALSE for: " + this.name); */
					}
				}
			} catch (Exception e) {
				/** System.out.println("[DEBUG-TTL] EXCEPTION in forceApplyTTL for: " + this.name +
					" | " + e.getClass().getName() + ": " + e.getMessage()); */
			}
		}
	}

	public void delete() {
		process(RAtomicLongOperation.DELETE, -1, -1);
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
				this.logDebug("[Redis-TTL] Impossibile ottenere TTL rimanente per " +
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
					this.logWarn("[Redis-TTL] Impossibile impostare TTL per " +
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
				this.logWarn("[Redis-TTL] Impossibile rimuovere TTL per " +
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
