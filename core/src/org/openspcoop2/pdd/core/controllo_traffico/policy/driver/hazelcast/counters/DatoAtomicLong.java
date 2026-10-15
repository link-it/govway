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
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

/**
 * Contatore distribuito di tipo 'long' con operazioni atomiche, utilizzato dalle policy
 * HAZELCAST_ATOMIC_LONG, HAZELCAST_ATOMIC_LONG_ASYNC e, per le date di coordinamento, HAZELCAST_PNCOUNTER.
 *
 * Il contatore è una chiave della IMap {@link #MAP_NAME} (configurata da HazelcastManager) e ogni modifica
 * viene effettuata tramite gli EntryProcessor di {@link DatoAtomicLongEntryProcessors}, eseguiti in modo esclusivo
 * sul member proprietario della partizione della chiave e replicati in modo sincrono sul backup.
 * Una chiave non presente equivale al valore 0, come per un contatore appena creato.
 *
 * Rispetto all'IAtomicLong del CP Subsystem (utilizzato fino alla versione 5.3 di Hazelcast, nella Community Edition
 * solo in modalità 'unsafe'), i contatori sono distribuiti su tutte le partizioni del cluster
 * e un contatore eliminato può essere ricreato.
 *
 * I contatori legati ad un intervallo temporale (nome contenente '-i-') vengono creati con una scadenza pari alla soglia
 * 'org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.HAZELCAST.orphanedProxy.threshold', ricalcolata ad ogni aggiornamento:
 * in questo modo vengono rimossi anche i contatori non eliminati esplicitamente (es. contatori ricreati da un nodo
 * che non ha ancora rilevato il cambio di intervallo, o non eliminati a causa del riavvio del nodo).
 * I contatori di coordinamento (senza intervallo) non hanno scadenza e vengono eliminati con la policy.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DatoAtomicLong {

	/** IMap contenente i contatori; non deve corrispondere al pattern 'hazelcast-*-rate-limiting' utilizzato dalle altre policy */
	public static final String MAP_NAME = "govway-atomiclong-counters";

	private static final String INTERVAL_MARKER = "-i-";

	private final IMap<String, Long> map;
	private final String name;
	private final long ttlMs;

	public DatoAtomicLong(HazelcastInstance hazelcast, String name) {
		this.map = hazelcast.getMap(MAP_NAME);
		this.name = name;
		if(name.contains(INTERVAL_MARKER)) {
			Long threshold = OpenSPCoop2Properties.getInstance().getControlloTrafficoGestorePolicyInMemoryHazelcastOrphanedProxyThresholdMs();
			this.ttlMs = threshold!=null ? threshold.longValue() : -1;
		}
		else {
			this.ttlMs = -1;
		}
	}

	public String getName() {
		return this.name;
	}

	public void set(long value) {
		this.map.executeOnKey(this.name, new DatoAtomicLongEntryProcessors.SetValue(value, this.ttlMs));
	}
	public long get() {
		Long v = this.map.get(this.name);
		return v!=null ? v.longValue() : 0l;
	}
	public long addAndGet(long value) {
		return this.map.executeOnKey(this.name, new DatoAtomicLongEntryProcessors.AddAndGet(value, this.ttlMs));
	}
	public long incrementAndGet() {
		return addAndGet(1);
	}
	public long decrementAndGet() {
		return addAndGet(-1);
	}
	public CompletionStage<Long> addAndGetAsync(long value) {
		return this.map.submitToKey(this.name, new DatoAtomicLongEntryProcessors.AddAndGet(value, this.ttlMs));
	}
	public CompletionStage<Long> incrementAndGetAsync() {
		return addAndGetAsync(1);
	}
	public CompletionStage<Long> decrementAndGetAsync() {
		return addAndGetAsync(-1);
	}
	public CompletionStage<Long> getAsync() {
		return this.map.getAsync(this.name).thenApply(v -> v!=null ? v : 0l);
	}
	public boolean compareAndSet(long compare, long set) {
		Boolean r = this.map.executeOnKey(this.name, new DatoAtomicLongEntryProcessors.CompareAndSet(compare, set, this.ttlMs));
		return r!=null && r.booleanValue();
	}
	public void destroy() {
		this.map.delete(this.name);
	}
	/**
	 * Elimina il contatore registrando nel log un eventuale errore, senza propagarlo al chiamante.
	 *
	 * Un nodo che utilizza ancora il contatore (es. non ha ancora rilevato il cambio di intervallo) lo ricrea al successivo aggiornamento
	 * partendo da 0: il contatore ricreato appartiene ad un intervallo già concluso e viene rimosso alla scadenza.
	 */
	public void destroySafe() {
		try {
			destroy();
		} catch(Throwable t) {
			Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(OpenSPCoop2Properties.getInstance().isControlloTrafficoDebug());
			log.error("[Hazelcast-AtomicLong-"+this.name+"] destroySafe non riuscito: "+t.getMessage(),t);
		}
	}

}
