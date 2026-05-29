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
package org.openspcoop2.core.commons;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Registro dichiarativo che associa ad ogni "scope padre" (es. erogazione, fruizione)
 * l'insieme delle idLista delle sue liste figlie il cui stato di ricerca deve essere
 * azzerato quando l'utente cambia l'istanza padre corrente (es. cambio di mapping
 * erogazione/fruizione, cambio di gruppo).
 *
 * Le costanti referenziate sono definite in {@link Liste}.
 *
 * @author Giuliano Pintori (pintori@link.it)
 */
public final class ScopedListeRegistry {

	private ScopedListeRegistry() {
		// utility class
	}

	/** Scope: dettaglio di una Porta Applicativa (erogazione / mapping erogazione / gruppo). */
	public static final String SCOPE_PORTA_APPLICATIVA = "PA";

	/** Scope: dettaglio di una Porta Delegata (fruizione / mapping fruizione / gruppo). */
	public static final String SCOPE_PORTA_DELEGATA = "PD";

	/**
	 * Scope: dettaglio di un Accordo Servizio Parte Specifica (erogazione o fruizione
	 * specifica). Padre di tutte le PORTE_APPLICATIVE_* e PORTE_DELEGATE_*: quando cambia
	 * l'APS/fruizione corrente le ricerche di tutte le porte (gruppi) figlie sono azzerate.
	 */
	public static final String SCOPE_APS = "APS";

	/** Scope: dettaglio di un Soggetto. */
	public static final String SCOPE_SOGGETTO = "SOGGETTO";

	/** Scope: dettaglio di un Servizio Applicativo. */
	public static final String SCOPE_APPLICATIVO = "APPLICATIVO";

	/** Scope: dettaglio di un Accordo di Servizio Parte Comune. */
	public static final String SCOPE_APC = "APC";

	/** Scope: dettaglio di un Accordo di Cooperazione. */
	public static final String SCOPE_AC = "AC";

	private static final Map<String, Set<Integer>> CHILDREN_BY_SCOPE = new HashMap<>();

	static {
		CHILDREN_BY_SCOPE.put(SCOPE_PORTA_APPLICATIVA, buildPortaApplicativaChildren());
		CHILDREN_BY_SCOPE.put(SCOPE_PORTA_DELEGATA, buildPortaDelegataChildren());
		CHILDREN_BY_SCOPE.put(SCOPE_APS, buildApsChildren());
		CHILDREN_BY_SCOPE.put(SCOPE_SOGGETTO, buildSoggettoChildren());
		CHILDREN_BY_SCOPE.put(SCOPE_APPLICATIVO, buildApplicativoChildren());
		CHILDREN_BY_SCOPE.put(SCOPE_APC, buildApcChildren());
		CHILDREN_BY_SCOPE.put(SCOPE_AC, buildAcChildren());
	}

	private static Set<Integer> buildSoggettoChildren() {
		Set<Integer> s = new HashSet<>();
		s.add(Liste.SOGGETTI_RUOLI);
		s.add(Liste.SOGGETTI_PROP);
		return Collections.unmodifiableSet(s);
	}

	private static Set<Integer> buildApplicativoChildren() {
		Set<Integer> s = new HashSet<>();
		s.add(Liste.SERVIZIO_APPLICATIVO_RUOLI);
		s.add(Liste.SERVIZI_APPLICATIVI_PROP);
		return Collections.unmodifiableSet(s);
	}

	private static Set<Integer> buildApcChildren() {
		Set<Integer> s = new HashSet<>();
		s.add(Liste.ACCORDI_AZIONI);
		s.add(Liste.ACCORDI_EROGATORI);
		s.add(Liste.ACCORDI_PORTTYPE);
		s.add(Liste.ACCORDI_PORTTYPE_AZIONI);
		s.add(Liste.ACCORDI_PORTTYPE_AZIONI_MESSAGE_INPUT);
		s.add(Liste.ACCORDI_PORTTYPE_AZIONI_MESSAGE_OUTPUT);
		s.add(Liste.ACCORDI_ALLEGATI);
		s.add(Liste.ACCORDI_API_RESOURCES);
		s.add(Liste.ACCORDI_API_RESOURCES_RESPONSE);
		s.add(Liste.ACCORDI_API_RESOURCES_REPRESENTATION_REQUEST);
		s.add(Liste.ACCORDI_API_RESOURCES_REPRESENTATION_RESPONSE);
		s.add(Liste.ACCORDI_API_RESOURCES_PARAMETERS_REQUEST);
		s.add(Liste.ACCORDI_API_RESOURCES_PARAMETERS_RESPONSE);
		return Collections.unmodifiableSet(s);
	}

	private static Set<Integer> buildAcChildren() {
		Set<Integer> s = new HashSet<>();
		s.add(Liste.ACCORDI_COOP_PARTECIPANTI);
		s.add(Liste.ACCORDI_COOP_ALLEGATI);
		return Collections.unmodifiableSet(s);
	}

	private static Set<Integer> buildApsChildren() {
		Set<Integer> s = new HashSet<>();
		s.addAll(buildPortaApplicativaChildren());
		s.addAll(buildPortaDelegataChildren());
		// Liste a livello di APS (mapping erogazione/fruizione, ovvero la "lista gruppi")
		s.add(Liste.CONFIGURAZIONE_EROGAZIONE);
		s.add(Liste.CONFIGURAZIONE_FRUIZIONE);
		return Collections.unmodifiableSet(s);
	}

	private static Set<Integer> buildPortaApplicativaChildren() {
		Set<Integer> s = new HashSet<>();
		s.add(Liste.PORTE_APPLICATIVE_PROP);
		s.add(Liste.PORTE_APPLICATIVE_AZIONI);
		s.add(Liste.PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO);
		s.add(Liste.PORTE_APPLICATIVE_SOGGETTO);
		s.add(Liste.PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO);
		s.add(Liste.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
		s.add(Liste.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
		s.add(Liste.PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);
		s.add(Liste.PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA);
		s.add(Liste.PORTE_APPLICATIVE_MTOM_REQUEST);
		s.add(Liste.PORTE_APPLICATIVE_MTOM_RESPONSE);
		s.add(Liste.PORTE_APPLICATIVE_RUOLI);
		s.add(Liste.PORTE_APPLICATIVE_SCOPE);
		s.add(Liste.PORTE_APPLICATIVE_EXTENDED);
		s.add(Liste.PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA);
		s.add(Liste.PORTE_APPLICATIVE_TRASFORMAZIONI);
		s.add(Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE);
		s.add(Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER);
		s.add(Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_HEADER);
		s.add(Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRI);
		s.add(Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO);
		s.add(Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO);
		s.add(Liste.PORTE_APPLICATIVE_PROPRIETA_AUTENTICAZIONE);
		s.add(Liste.PORTE_APPLICATIVE_PROPRIETA_AUTORIZZAZIONE);
		s.add(Liste.PORTE_APPLICATIVE_PROPRIETA_AUTORIZZAZIONE_CONTENUTO);
		s.add(Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI);
		s.add(Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_PROPRIETA);
		s.add(Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPRIETA);
		s.add(Liste.PORTE_APPLICATIVE_TOKEN_SERVIZIO_APPLICATIVO);
		s.add(Liste.PORTE_APPLICATIVE_TOKEN_RUOLI);
		return Collections.unmodifiableSet(s);
	}

	private static Set<Integer> buildPortaDelegataChildren() {
		Set<Integer> s = new HashSet<>();
		s.add(Liste.PORTE_DELEGATE_PROP);
		s.add(Liste.PORTE_DELEGATE_SERVIZIO_APPLICATIVO);
		s.add(Liste.PORTE_DELEGATE_AZIONI);
		s.add(Liste.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
		s.add(Liste.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
		s.add(Liste.PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA);
		s.add(Liste.PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA);
		s.add(Liste.PORTE_DELEGATE_MTOM_REQUEST);
		s.add(Liste.PORTE_DELEGATE_MTOM_RESPONSE);
		s.add(Liste.PORTE_DELEGATE_RUOLI);
		s.add(Liste.PORTE_DELEGATE_SCOPE);
		s.add(Liste.PORTE_DELEGATE_EXTENDED);
		s.add(Liste.PORTE_DELEGATE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA);
		s.add(Liste.PORTE_DELEGATE_TRASFORMAZIONI);
		s.add(Liste.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE);
		s.add(Liste.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER);
		s.add(Liste.PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER);
		s.add(Liste.PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_PARAMETRI);
		s.add(Liste.PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO);
		s.add(Liste.PORTE_DELEGATE_PROPRIETA_AUTENTICAZIONE);
		s.add(Liste.PORTE_DELEGATE_PROPRIETA_AUTORIZZAZIONE);
		s.add(Liste.PORTE_DELEGATE_PROPRIETA_AUTORIZZAZIONE_CONTENUTO);
		s.add(Liste.PORTE_DELEGATE_TOKEN_SERVIZIO_APPLICATIVO);
		s.add(Liste.PORTE_DELEGATE_TOKEN_RUOLI);
		return Collections.unmodifiableSet(s);
	}

	/**
	 * Restituisce l'insieme delle idLista figlie associate allo scope indicato.
	 * Restituisce un set vuoto se lo scope non e' registrato.
	 */
	public static Set<Integer> getChildrenOf(String scopeName) {
		Set<Integer> children = CHILDREN_BY_SCOPE.get(scopeName);
		return (children != null) ? children : Collections.emptySet();
	}

	/**
	 * Restituisce gli altri scope il cui stato deve essere invalidato quando
	 * l'istanza padre di {@code scopeName} cambia. Esempio: cambiando APS, anche gli
	 * scope PA/PD memorizzati diventano "stale" perche' riferiti a porte/gruppi
	 * appartenenti all'APS precedente.
	 */
	public static Set<String> getInvalidatedScopesOnChange(String scopeName) {
		if (SCOPE_APS.equals(scopeName)) {
			Set<String> s = new HashSet<>();
			s.add(SCOPE_PORTA_APPLICATIVA);
			s.add(SCOPE_PORTA_DELEGATA);
			return Collections.unmodifiableSet(s);
		}
		return Collections.emptySet();
	}

}
