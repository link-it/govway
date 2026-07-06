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
package org.openspcoop2.pdd.core.llm.pii;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Accumula i risultati del PII Masking (per tipo detector → regola → valori distinti mascherati)
 * per la generazione dei diagnostici (uno per tipo detector, con i nomi delle regole).
 *
 * <p>Il conteggio è sui valori <b>distinti</b>, non sulle occorrenze: lo stesso valore reale
 * ripetuto più volte nel payload produce sempre lo stesso pseudonimo (il {@link PiiVault} è
 * idempotente) e va contato una sola volta. Come chiave di distinzione si usa lo <b>pseudonimo</b>
 * (bijettivo col valore reale) per non trattenere PII reale in questo oggetto.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class PiiFindings {

	// detectorType -> (ruleName -> insieme degli pseudonimi distinti coniati)
	private final Map<String, Map<String, Set<String>>> byType = new LinkedHashMap<>();

	/** Registra un valore mascherato da una regola (deduplicato per pseudonimo). */
	public synchronized void record(String detectorType, String ruleName, String pseudonym) {
		this.byType
			.computeIfAbsent(detectorType, k -> new LinkedHashMap<>())
			.computeIfAbsent(ruleName, k -> new LinkedHashSet<>())
			.add(pseudonym);
	}

	/**
	 * Descrizione dei valori distinti individuati per un tipo detector, con i soli nomi delle
	 * Regole PII e il conteggio dei valori distinti, es.: {@code eMailAddress (2), codiceFiscale (1)}
	 * (la categoria è implicita nel nome della regola). Restituisce stringa vuota se il tipo non ha
	 * individuato nulla.
	 */
	public synchronized String describeType(String detectorType) {
		Map<String, Set<String>> rules = this.byType.get(detectorType);
		if (rules == null || rules.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Set<String>> ruleEntry : rules.entrySet()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(ruleEntry.getKey()).append(" (").append(ruleEntry.getValue().size()).append(')');
		}
		return sb.toString();
	}
}
