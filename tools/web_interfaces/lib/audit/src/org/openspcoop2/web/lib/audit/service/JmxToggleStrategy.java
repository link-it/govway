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
package org.openspcoop2.web.lib.audit.service;

import org.openspcoop2.web.lib.audit.AuditException;

/**
 * Astrazione della chiamata JMX di abilitazione/disabilitazione di una porta
 * applicativa o delegata sul singolo nodo runtime (alias).
 *
 * Il loop sugli alias e' demandato al {@link ToggleStatoPortaService}: l'
 * implementazione concreta si limita a effettuare la chiamata su un singolo
 * alias, leggendo la configurazione dei metodi JMX dal proprio contesto
 * (web console oppure web api).
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public interface JmxToggleStrategy {

	/**
	 * Invoca via JMX l'abilitazione/disabilitazione di una porta sul nodo
	 * runtime identificato da {@code alias}.
	 *
	 * @param alias identificativo del nodo runtime configurato
	 * @param nomePorta nome della porta applicativa o delegata
	 * @param isPA {@code true} se porta applicativa, {@code false} se delegata
	 * @param enable {@code true} per abilitare, {@code false} per disabilitare
	 * @throws Exception in caso di errore sull'invocazione JMX
	 */
	void invokeOnAlias(String alias, String nomePorta, boolean isPA, boolean enable) throws AuditException;
}
