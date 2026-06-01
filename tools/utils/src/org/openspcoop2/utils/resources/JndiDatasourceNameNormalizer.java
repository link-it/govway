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

package org.openspcoop2.utils.resources;

/**
 * Normalizzazione "safe" del prefisso del contesto JNDI (ENC) dei DataSource il cui nome
 * viene risolto a runtime a partire da valori memorizzati sulla base dati (es. registro
 * servizi su DB, appender DB di diagnostica/tracciamento/dump, audit DB appender).
 *
 * Tali valori possono essere stati salvati per un Application Server diverso da quello su
 * cui il software sta girando: tipicamente su Tomcat i nomi sono prefissati con il contesto
 * ENC 'java:/comp/env/', mentre su WildFly/JBoss non lo sono. La normalizzazione, in base
 * alla configurazione del runtime corrente, aggiunge il prefisso se atteso e mancante, oppure
 * lo rimuove se non atteso ed presente; e' idempotente (se il nome e' gia' coerente non viene
 * modificato).
 *
 * La configurazione e' statica e va impostata una sola volta all'avvio dell'archivio
 * (ogni archivio dispiega la propria copia di utils.jar, quindi la configurazione e'
 * indipendente per ciascun archivio).
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JndiDatasourceNameNormalizer {

	private JndiDatasourceNameNormalizer() {}

	/** Prefisso ENC (Environment Naming Context) standard, usato come default. */
	public static final String DEFAULT_CONTEXT_PREFIX = "java:/comp/env/";

	/** Varianti note del prefisso ENC riconosciute in fase di rimozione/verifica presenza. */
	private static final String[] DEFAULT_KNOWN_ENC_PREFIXES = { DEFAULT_CONTEXT_PREFIX, "java:comp/env/" };

	private static boolean enabled = false;
	private static String contextPrefix = null;
	private static boolean expected = false;

	/**
	 * Configura la normalizzazione (da invocare una volta all'avvio dell'archivio).
	 *
	 * @param enabledNormalize abilita/disabilita l'intera normalizzazione
	 * @param contextPrefixValue valore del prefisso ENC (es. 'java:/comp/env/')
	 * @param expectedInThisAS true se il prefisso e' atteso dall'AS in esecuzione (Tomcat), false altrimenti (WildFly/JBoss)
	 */
	public static synchronized void configure(boolean enabledNormalize, String contextPrefixValue, boolean expectedInThisAS) {
		enabled = enabledNormalize;
		contextPrefix = (contextPrefixValue!=null) ? contextPrefixValue.trim() : null;
		expected = expectedInThisAS;
	}

	public static synchronized boolean isEnabled() {
		return enabled;
	}

	/**
	 * Restituisce il nome JNDI normalizzato in base alla configurazione corrente.
	 * Se la normalizzazione e' disabilitata, o il prefisso non e' configurato, o il nome e'
	 * gia' coerente, il nome viene restituito invariato.
	 *
	 * @param jndiName nome JNDI risolto dalla base dati
	 * @return nome JNDI normalizzato
	 */
	public static synchronized String normalize(String jndiName) {

		if(!enabled || jndiName==null) {
			return jndiName;
		}

		String prefix = contextPrefix;
		if(prefix==null || prefix.isEmpty()) {
			return jndiName;
		}

		// Riconoscimento prefisso gia' presente: si considerano sia il prefisso configurato sia
		// le varianti ENC note (es. 'java:comp/env/' senza '/' iniziale). Si verifica dal piu' lungo
		// al piu' corto per un match deterministico.
		String matched = null;
		for(String p : knownPrefixesLongestFirst(prefix)) {
			if(p!=null && !p.isEmpty() && jndiName.startsWith(p)) {
				matched = p;
				break;
			}
		}
		boolean present = (matched != null);

		if(expected && !present) {
			// AS che attende il prefisso (Tomcat): aggiungo se manca
			return prefix + jndiName;
		}
		if(!expected && present) {
			// AS che non attende il prefisso (WildFly/JBoss): rimuovo se presente
			return jndiName.substring(matched.length());
		}
		// gia' coerente: invariato
		return jndiName;
	}

	private static String[] knownPrefixesLongestFirst(String configuredPrefix) {
		java.util.List<String> prefixes = new java.util.ArrayList<>();
		if(configuredPrefix!=null && !configuredPrefix.isEmpty()) {
			prefixes.add(configuredPrefix);
		}
		for(String p : DEFAULT_KNOWN_ENC_PREFIXES) {
			if(!prefixes.contains(p)) {
				prefixes.add(p);
			}
		}
		prefixes.sort((a,b) -> Integer.compare(b.length(), a.length()));
		return prefixes.toArray(new String[0]);
	}

}
