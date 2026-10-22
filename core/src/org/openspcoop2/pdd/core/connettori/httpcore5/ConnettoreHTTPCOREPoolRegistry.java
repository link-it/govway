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

package org.openspcoop2.pdd.core.connettori.httpcore5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.core5.pool.ConnPoolControl;
import org.apache.hc.core5.pool.PoolStats;

/**
 * ConnettoreHTTPCOREPoolRegistry
 *
 * Mantiene, per ogni pool di connessioni (ConnectionManager), la configurazione di appartenenza
 * (chiave del pool senza l'impronta del contenuto dei keystore) e l'istante dell'ultimo utilizzo.
 *
 * Quando il contenuto dei keystore cambia, per la stessa configurazione nasce un nuovo pool.
 * Un pool è 'dismesso' se, per la stessa configurazione, esiste un pool utilizzato più di recente.
 * Un pool dismesso non viene chiuso subito: può avere connessioni in uso e, finché le cache non sono allineate,
 * qualche richiesta può ancora riferirlo. Viene chiuso quando non ha connessioni in uso o in attesa
 * e non viene utilizzato da almeno la soglia di inattività indicata.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCOREPoolRegistry {

	/** Soglia di inattività usata se non è configurata la chiusura delle connessioni inattive */
	public static final int INATTIVITA_POOL_DISMESSO_DEFAULT_SECONDS = 30;

	private static class PoolInfo {
		private final String keyConfigurazione;
		private final long creazione;
		private volatile long ultimoUtilizzo;
		PoolInfo(String keyConfigurazione){
			this.keyConfigurazione = keyConfigurazione;
			this.creazione = System.nanoTime();
			this.ultimoUtilizzo = System.currentTimeMillis();
		}
	}

	private final Map<String, PoolInfo> mapInfo = new ConcurrentHashMap<>();

	public void registraCreazione(String keyPool, String keyConfigurazione) {
		this.mapInfo.put(keyPool, new PoolInfo(keyConfigurazione));
	}
	public void registraUtilizzo(String keyPool) {
		if(keyPool!=null) {
			PoolInfo info = this.mapInfo.get(keyPool);
			if(info!=null) {
				info.ultimoUtilizzo = System.currentTimeMillis();
			}
		}
	}
	public void rimuovi(String keyPool) {
		this.mapInfo.remove(keyPool);
	}
	public void clear() {
		this.mapInfo.clear();
	}

	/**
	 * Un pool è dismesso se per la stessa configurazione esiste un pool utilizzato più di recente
	 * (a parità di utilizzo, quello creato dopo).
	 */
	public boolean isDismesso(String keyPool) {
		PoolInfo info = this.mapInfo.get(keyPool);
		if(info==null) {
			return false;
		}
		for (Map.Entry<String, PoolInfo> entry : this.mapInfo.entrySet()) {
			PoolInfo altro = entry.getValue();
			if(!entry.getKey().equals(keyPool) && altro.keyConfigurazione.equals(info.keyConfigurazione) &&
					(altro.ultimoUtilizzo > info.ultimoUtilizzo ||
					(altro.ultimoUtilizzo == info.ultimoUtilizzo && altro.creazione > info.creazione))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Restituisce i pool dismessi che possono essere chiusi: nessuna connessione in uso o in attesa
	 * e nessun utilizzo da almeno 'inattivitaSeconds' secondi.
	 */
	public <T extends ConnPoolControl<HttpRoute>> List<String> getPoolDismessiDaChiudere(Map<String, T> mapPool, int inattivitaSeconds) {
		List<String> l = new ArrayList<>();
		for (Map.Entry<String, T> entry : mapPool.entrySet()) {
			if(isChiudibile(entry.getKey(), entry.getValue(), inattivitaSeconds)) {
				l.add(entry.getKey());
			}
		}
		return l;
	}
	
	/**
	 * Da invocare anche dopo aver rimosso il pool dalla mappa: una richiesta registra l'utilizzo prima di recuperare il pool,
	 * quindi un utilizzo avvenuto nel frattempo viene rilevato e il pool non deve essere chiuso.
	 */
	public <T extends ConnPoolControl<HttpRoute>> boolean isChiudibile(String keyPool, T pool, int inattivitaSeconds) {
		PoolInfo info = this.mapInfo.get(keyPool);
		if(info==null || pool==null) {
			return false;
		}
		long soglia = System.currentTimeMillis() - (inattivitaSeconds * 1000l);
		if(info.ultimoUtilizzo > soglia || !isDismesso(keyPool)) {
			return false;
		}
		PoolStats stats = pool.getTotalStats();
		return stats.getLeased()==0 && stats.getPending()==0;
	}

}
