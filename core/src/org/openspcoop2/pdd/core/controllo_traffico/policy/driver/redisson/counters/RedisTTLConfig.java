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

import java.io.Serializable;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoControlloPeriodo;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoRealtime;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoStatistico;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;

/**
 * RedisTTLConfig - Configurazione TTL per contatori Redis nel rate limiting
 *
 * Questa classe gestisce il calcolo del TTL (Time To Live) per i contatori Redis
 * usati nel rate limiting distribuito. Il TTL permette la pulizia automatica
 * dei contatori relativi a clientId non più attivi.
 *
 * Il comportamento del renewOnWrite dipende dal tipo di policy:
 * - Policy con intervallo temporale (es. 100 req/minuto): ad ogni finestra temporale
 *   vengono creati nuovi contatori, quindi non serve rinnovare il TTL sui vecchi
 * - Policy senza intervallo (es. richieste simultanee) o con TTL troncato al massimo:
 *   il rinnovo del TTL garantisce che i contatori rimangano attivi finché il client è attivo
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RedisTTLConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	// Valori di default
	public static final long DEFAULT_TTL_SECONDS = 300; // 5 minuti
	public static final long MIN_TTL_SECONDS = 60; // 1 minuto minimo
	public static final long MAX_TTL_SECONDS = 604800; // 7 giorni massimo
	public static final int DEFAULT_INTERVAL_MULTIPLIER = 2; // TTL = intervallo × 2

	private boolean enabled;
	private long ttlSeconds;
	private boolean renewTTLOnWrite;

	/**
	 * Costruttore di default - legge configurazione da properties
	 * Usa renewOnWrite per policy senza intervallo (default: true)
	 */
	public RedisTTLConfig() {
		OpenSPCoop2Properties props = OpenSPCoop2Properties.getInstance();
		this.enabled = props.isControlloTrafficoGestorePolicyInMemoryRedisTTLEnabled();
		this.ttlSeconds = props.getControlloTrafficoGestorePolicyInMemoryRedisTTLDefaultSeconds();
		// Senza policy, usiamo il default per policy senza intervallo
		this.renewTTLOnWrite = props.isControlloTrafficoGestorePolicyInMemoryRedisTTLRenewOnWriteWithoutInterval();
	}

	/**
	 * Costruttore con valori espliciti
	 */
	public RedisTTLConfig(boolean enabled, long ttlSeconds, boolean renewTTLOnWrite) {
		this.enabled = enabled;
		this.ttlSeconds = ttlSeconds;
		this.renewTTLOnWrite = renewTTLOnWrite;
	}

	/**
	 * Costruttore basato su ActivePolicy - calcola TTL e renewOnWrite automaticamente
	 */
	public RedisTTLConfig(ActivePolicy activePolicy) {
		OpenSPCoop2Properties props = OpenSPCoop2Properties.getInstance();
		this.enabled = props.isControlloTrafficoGestorePolicyInMemoryRedisTTLEnabled();

		if (this.enabled && activePolicy != null) {
			TTLCalculationResult result = calculateTTLFromPolicy(activePolicy, props);
			this.ttlSeconds = result.ttlSeconds;
			this.renewTTLOnWrite = result.renewOnWrite;
		} else {
			this.ttlSeconds = props.getControlloTrafficoGestorePolicyInMemoryRedisTTLDefaultSeconds();
			// Senza policy valida, usiamo il default per policy senza intervallo
			this.renewTTLOnWrite = props.isControlloTrafficoGestorePolicyInMemoryRedisTTLRenewOnWriteWithoutInterval();
		}
	}

	/**
	 * Risultato del calcolo TTL che include anche il renewOnWrite appropriato
	 */
	private static class TTLCalculationResult {
		long ttlSeconds;
		boolean renewOnWrite;

		TTLCalculationResult(long ttlSeconds, boolean renewOnWrite) {
			this.ttlSeconds = ttlSeconds;
			this.renewOnWrite = renewOnWrite;
		}
	}

	/**
	 * Calcola il TTL e il renewOnWrite basandosi sulla configurazione della policy di rate limiting
	 */
	private TTLCalculationResult calculateTTLFromPolicy(ActivePolicy activePolicy, OpenSPCoop2Properties props) {
		if (activePolicy == null || activePolicy.getConfigurazionePolicy() == null) {
			// Nessuna policy: usa default per policy senza intervallo
			return new TTLCalculationResult(
				props.getControlloTrafficoGestorePolicyInMemoryRedisTTLDefaultSeconds(),
				props.isControlloTrafficoGestorePolicyInMemoryRedisTTLRenewOnWriteWithoutInterval()
			);
		}

		ConfigurazionePolicy config = activePolicy.getConfigurazionePolicy();

		// Policy per richieste simultanee: non ha intervallo temporale
		// Il TTL è fisso e il renewOnWrite serve per mantenere attivi i contatori dei client attivi
		if (config.isSimultanee()) {
			return new TTLCalculationResult(
				props.getControlloTrafficoGestorePolicyInMemoryRedisTTLDefaultSeconds(),
				props.isControlloTrafficoGestorePolicyInMemoryRedisTTLRenewOnWriteWithoutInterval()
			);
		}

		// Calcola intervallo della policy in secondi
		long intervalloSeconds = calculateIntervalSeconds(config);

		if (intervalloSeconds <= 0) {
			// Intervallo non determinabile: usa default per policy senza intervallo
			return new TTLCalculationResult(
				props.getControlloTrafficoGestorePolicyInMemoryRedisTTLDefaultSeconds(),
				props.isControlloTrafficoGestorePolicyInMemoryRedisTTLRenewOnWriteWithoutInterval()
			);
		}

		// TTL = intervallo × moltiplicatore
		int multiplier = props.getControlloTrafficoGestorePolicyInMemoryRedisTTLIntervalMultiplier();
		long ttlCalcolato = intervalloSeconds * multiplier;

		// Recupera limiti min/max
		long minTTL = props.getControlloTrafficoGestorePolicyInMemoryRedisTTLMinSeconds();
		long maxTTL = props.getControlloTrafficoGestorePolicyInMemoryRedisTTLMaxSeconds();

		// Applica limite minimo
		long ttlFinale = Math.max(ttlCalcolato, minTTL);

		// Verifica se il TTL viene troncato al massimo
		boolean ttlTroncato = ttlFinale > maxTTL;
		if (ttlTroncato) {
			ttlFinale = maxTTL;
		}

		// Determina il renewOnWrite in base al tipo di policy:
		// - Se TTL troncato: il TTL potrebbe essere minore dell'intervallo, serve renewOnWrite
		// - Se TTL non troncato: il TTL è sufficiente, non serve renewOnWrite
		boolean renewOnWrite;
		if (ttlTroncato) {
			// TTL troncato al massimo: potrebbe scadere durante l'intervallo attivo,
			// quindi serve rinnovare il TTL ad ogni scrittura per mantenere attivi i contatori
			renewOnWrite = props.isControlloTrafficoGestorePolicyInMemoryRedisTTLRenewOnWriteWithoutInterval();
		} else {
			// TTL calcolato normalmente dall'intervallo: i contatori delle finestre precedenti
			// non ricevono più scritture, quindi rinnovare il TTL sarebbe inutile
			renewOnWrite = props.isControlloTrafficoGestorePolicyInMemoryRedisTTLRenewOnWriteIntervalBased();
		}

		return new TTLCalculationResult(ttlFinale, renewOnWrite);
	}

	/**
	 * Calcola l'intervallo della policy in secondi
	 */
	private long calculateIntervalSeconds(ConfigurazionePolicy config) {
		Integer intervallo = config.getIntervalloOsservazione();
		if (intervallo == null || intervallo <= 0) {
			return 0;
		}

		long intervalloSeconds = 0;

		if (TipoControlloPeriodo.REALTIME.equals(config.getModalitaControllo())) {
			intervalloSeconds = calculateRealtimeIntervalSeconds(config, intervallo);
		} else if (TipoControlloPeriodo.STATISTIC.equals(config.getModalitaControllo())) {
			intervalloSeconds = calculateStatisticIntervalSeconds(config, intervallo);
		}

		return intervalloSeconds;
	}

	private long calculateRealtimeIntervalSeconds(ConfigurazionePolicy config, int intervallo) {
		TipoPeriodoRealtime tipo = config.getTipoIntervalloOsservazioneRealtime();
		if (tipo == null) {
			return 0;
		}

		switch (tipo) {
			case SECONDI:
				return intervallo;
			case MINUTI:
				return intervallo * 60L;
			case ORARIO:
				return intervallo * 3600L;
			case GIORNALIERO:
				return intervallo * 86400L;
			default:
				return 0;
		}
	}

	private long calculateStatisticIntervalSeconds(ConfigurazionePolicy config, int intervallo) {
		TipoPeriodoStatistico tipo = config.getTipoIntervalloOsservazioneStatistico();
		if (tipo == null) {
			return 0;
		}

		switch (tipo) {
			case ORARIO:
				return intervallo * 3600L;
			case GIORNALIERO:
				return intervallo * 86400L;
			case SETTIMANALE:
				return intervallo * 604800L;
			case MENSILE:
				return intervallo * 2592000L; // 30 giorni
			default:
				return 0;
		}
	}

	// Getters e Setters

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public long getTtlSeconds() {
		return this.ttlSeconds;
	}

	public void setTtlSeconds(long ttlSeconds) {
		this.ttlSeconds = ttlSeconds;
	}

	public boolean isRenewTTLOnWrite() {
		return this.renewTTLOnWrite;
	}

	public void setRenewTTLOnWrite(boolean renewTTLOnWrite) {
		this.renewTTLOnWrite = renewTTLOnWrite;
	}

	/**
	 * Crea una configurazione con TTL disabilitato
	 */
	public static RedisTTLConfig disabled() {
		return new RedisTTLConfig(false, -1, false);
	}

	/**
	 * Crea una configurazione con TTL specifico
	 */
	public static RedisTTLConfig withTTL(long ttlSeconds) {
		return new RedisTTLConfig(true, ttlSeconds, true);
	}

	/**
	 * Crea una configurazione basata su ActivePolicy
	 */
	public static RedisTTLConfig fromPolicy(ActivePolicy activePolicy) {
		return new RedisTTLConfig(activePolicy);
	}

	/**
	 * Crea una configurazione per contatori senza intervallo temporale (es. activeRequestCounter).
	 * Questi contatori devono avere renewTTLOnWrite=true per rimanere attivi finché il client fa richieste.
	 */
	public static RedisTTLConfig forCountersWithoutInterval() {
		OpenSPCoop2Properties props = OpenSPCoop2Properties.getInstance();
		return new RedisTTLConfig(
			props.isControlloTrafficoGestorePolicyInMemoryRedisTTLEnabled(),
			props.getControlloTrafficoGestorePolicyInMemoryRedisTTLDefaultSeconds(),
			props.isControlloTrafficoGestorePolicyInMemoryRedisTTLRenewOnWriteWithoutInterval() // true di default
		);
	}

	@Override
	public String toString() {
		return "RedisTTLConfig [enabled=" + this.enabled + ", ttlSeconds=" + this.ttlSeconds +
			   ", renewTTLOnWrite=" + this.renewTTLOnWrite + "]";
	}
}
