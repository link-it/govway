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

package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.utils.date.DateManager;

/**
 * Helper class per la gestione degli intervalli temporali dei contatori di richieste simultanee
 * in ambienti distribuiti. Centralizza la logica comune per evitare contatori orfani.
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ActiveRequestDistributedIntervalManager {

	private ActiveRequestDistributedIntervalManager() {
		// utility class
	}

	/**
	 * Restituisce l'intervallo configurato per le richieste simultanee in secondi.
	 * @return intervallo in secondi, o valore &lt;= 0 se disabilitato
	 */
	public static int getIntervalloSecondi() {
		return OpenSPCoop2Properties.getInstance().getControlloTrafficoGestorePolicyInMemoryRichiesteSimultaneeIntervalloSecondi();
	}

	/**
	 * Verifica se la gestione a intervalli per le richieste simultanee è abilitata.
	 * @return true se abilitata (intervallo &gt; 0)
	 */
	public static boolean isIntervalloAbilitato() {
		return getIntervalloSecondi() > 0;
	}

	/**
	 * Calcola il timestamp di inizio dell'intervallo corrente.
	 * @param intervalloSecondi intervallo in secondi
	 * @return timestamp di inizio dell'intervallo corrente, o null se intervallo &lt;= 0
	 */
	public static Long calcolaIntervalloCorrente(int intervalloSecondi) {
		if (intervalloSecondi <= 0) {
			return null;
		}
		long now = DateManager.getTimeMillis();
		long intervalMillis = intervalloSecondi * 1000L;
		return (now / intervalMillis) * intervalMillis;
	}

	/**
	 * Verifica se l'intervallo è cambiato rispetto a quello memorizzato.
	 * @param intervalloSecondi intervallo in secondi
	 * @param dataIntervalloMemorizzato timestamp dell'intervallo memorizzato
	 * @return true se l'intervallo è cambiato
	 */
	public static boolean isIntervalloCambiato(int intervalloSecondi, long dataIntervalloMemorizzato) {
		if (intervalloSecondi <= 0 || dataIntervalloMemorizzato <= 0) {
			return false;
		}
		Long intervalloCorrente = calcolaIntervalloCorrente(intervalloSecondi);
		return intervalloCorrente != null && intervalloCorrente > dataIntervalloMemorizzato;
	}
}
