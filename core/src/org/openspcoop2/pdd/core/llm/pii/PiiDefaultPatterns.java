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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry condiviso delle espressioni regolari di default per ciascuna categoria di
 * dato sensibile. Unica sorgente di verità usata da:
 * <ul>
 *   <li>il pre-fill della textarea {@code patterns} nel form di una Regola PII (alla
 *       selezione di una categoria ≠ custom, vedi {@code LLMPiiMaskingConfigProvider});</li>
 *   <li>il seed delle Regole PII predefinite all'installazione;</li>
 *   <li>i default applicabili a runtime.</li>
 * </ul>
 *
 * <p>Le regex sono volutamente "loose": catturano un superset e l'eventuale validazione
 * category-specific (Luhn, mod97, codice di controllo CF, libphonenumber, ...) abbatte i
 * falsi positivi. In ordine di applicazione le categorie più specifiche vanno prima di
 * quelle greedy (es. IBAN/carta/CF prima del telefono): qui l'ordine non è significativo
 * perché ogni Regola PII è autonoma, ma è rispettato a runtime dal {@code CompositePiiMasker}.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class PiiDefaultPatterns {

	private static final Map<String, List<String>> DEFAULTS = buildDefaults();

	private static Map<String, List<String>> buildDefaults() {
		Map<String, List<String>> m = new LinkedHashMap<>();

		// Email
		m.put(Costanti.PII_CATEGORY_VALUE_EMAIL, List.of(
				"[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}"));

		// IBAN (forma IT/EU loose; validatore mod97)
		m.put(Costanti.PII_CATEGORY_VALUE_IBAN, List.of(
				"\\b[A-Z]{2}\\d{2}[A-Z0-9]{11,30}\\b"));

		// Carta di credito: 16 cifre eventualmente raggruppate (validatore Luhn)
		m.put(Costanti.PII_CATEGORY_VALUE_CARD, List.of(
				"\\b\\d{4}[ -]?\\d{4}[ -]?\\d{4}[ -]?\\d{4}\\b"));

		// Codice Fiscale italiano (validatore: carattere di controllo)
		m.put(Costanti.PII_CATEGORY_VALUE_CF, List.of(
				"\\b[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]\\b"));

		// Telefono (loose: +prefisso opzionale poi 7+ cifre con spazi/punti/trattini; validatore libphonenumber)
		m.put(Costanti.PII_CATEGORY_VALUE_PHONE, List.of(
				"(?<![\\w])\\+?\\d(?:[ .\\-]?\\d){6,}"));

		// Username dentro un home path: cattura prefisso + segmento utente (la sostituzione tiene il prefisso)
		m.put(Costanti.PII_CATEGORY_VALUE_USERNAME_PATH, List.of(
				"(/(?:home|Users)/)([^/\\s\"']+)"));

		// IP: IPv4 e IPv6 (loose)
		m.put(Costanti.PII_CATEGORY_VALUE_IP, List.of(
				"\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b",
				"\\b(?:[A-Fa-f0-9]{1,4}:){2,7}[A-Fa-f0-9]{1,4}\\b"));

		// Partita IVA italiana: 11 cifre, eventuale prefisso IT (validatore: checksum)
		m.put(Costanti.PII_CATEGORY_VALUE_PIVA, List.of(
				"\\b(?:IT)?\\d{11}\\b"));

		// Segreti/credenziali (redact-only): AWS access key, JWT, chiave privata PEM, bearer token, URL con credenziali
		m.put(Costanti.PII_CATEGORY_VALUE_SECRET, List.of(
				"\\bAKIA[0-9A-Z]{16}\\b",
				"\\beyJ[A-Za-z0-9_\\-]+\\.[A-Za-z0-9_\\-]+\\.[A-Za-z0-9_\\-]+\\b",
				"-----BEGIN (?:[A-Z ]+ )?PRIVATE KEY-----",
				"(?i)\\bbearer\\s+[A-Za-z0-9._\\-]+",
				"[a-zA-Z][a-zA-Z0-9+.\\-]*://[^/\\s:@]+:[^/\\s:@]+@"));

		// Documenti d'identità IT (approssimazioni): passaporto, patente
		m.put(Costanti.PII_CATEGORY_VALUE_DOC_ID, List.of(
				"\\b[A-Z]{2}\\d{7}\\b",
				"\\b[A-Z]{2}\\d{7}[A-Z]\\b"));

		// Targa veicolo italiana (AA000AA)
		m.put(Costanti.PII_CATEGORY_VALUE_PLATE, List.of(
				"\\b[A-Z]{2}\\d{3}[A-Z]{2}\\b"));

		// MAC address
		m.put(Costanti.PII_CATEGORY_VALUE_MAC, List.of(
				"\\b(?:[0-9A-Fa-f]{2}[:\\-]){5}[0-9A-Fa-f]{2}\\b"));

		// Personalizzato: nessun default (l'utente fornisce le proprie regex)
		m.put(Costanti.PII_CATEGORY_VALUE_CUSTOM, Collections.emptyList());

		return Collections.unmodifiableMap(m);
	}

	/** Restituisce le regex di default della categoria (lista vuota se categoria sconosciuta o custom). */
	public static List<String> getPatterns(String category) {
		if (category == null) {
			return Collections.emptyList();
		}
		return DEFAULTS.getOrDefault(category, Collections.emptyList());
	}

	/** Restituisce le regex di default della categoria come testo (una per riga), per pre-compilare la textarea. */
	public static String getPatternsAsText(String category) {
		List<String> patterns = getPatterns(category);
		return String.join("\n", patterns);
	}

	/** Indica se la categoria ha regex di default (false per custom o categoria sconosciuta). */
	public static boolean hasDefaults(String category) {
		return !getPatterns(category).isEmpty();
	}

	/** Elenco delle categorie note (ordine di dichiarazione). */
	public static List<String> getCategories() {
		return new ArrayList<>(DEFAULTS.keySet());
	}

	private PiiDefaultPatterns() {
		// utility class
	}
}
