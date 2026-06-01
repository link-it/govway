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
package org.openspcoop2.core.monitor.rs.server.api.impl;

import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.config.InvokerNodiRuntime;
import org.openspcoop2.web.lib.audit.AuditException;
import org.openspcoop2.web.lib.audit.service.JmxToggleStrategy;
import org.slf4j.Logger;

/**
 * Implementazione di {@link JmxToggleStrategy} per le API REST di monitoraggio.
 * Legge la configurazione dei metodi JMX da {@link ServerProperties} e invoca
 * tramite {@link InvokerNodiRuntime}.
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ApiMonitorJmxToggleStrategy implements JmxToggleStrategy {

	private static final String MSG_OPERAZIONE_OK = "Operazione effettuata con successo";
	private static final String MSG_OPERAZIONE_OK_PREFIX = "Operazione effettuata con successo; ";

	private final ServerProperties serverProperties;
	private final InvokerNodiRuntime invoker;

	public ApiMonitorJmxToggleStrategy(ServerProperties serverProperties, Logger log) {
		this.serverProperties = serverProperties;
		this.invoker = new InvokerNodiRuntime(log, ConfigurazioneNodiRuntime.getConfigurazioneNodiRuntime());
	}

	@Override
	public void invokeOnAlias(String alias, String nomePorta, boolean isPA, boolean enable) throws AuditException {
		String metodo;
		String esito;
		try {
			if(isPA) {
				metodo = enable
						? this.serverProperties.getJmxConfigurazioneSistemaNomeMetodoEnablePortaApplicativa(alias)
						: this.serverProperties.getJmxConfigurazioneSistemaNomeMetodoDisablePortaApplicativa(alias);
			} else {
				metodo = enable
						? this.serverProperties.getJmxConfigurazioneSistemaNomeMetodoEnablePortaDelegata(alias)
						: this.serverProperties.getJmxConfigurazioneSistemaNomeMetodoDisablePortaDelegata(alias);
			}
	
			esito = this.invoker.invokeJMXMethod(alias,
					this.serverProperties.getJmxConfigurazioneSistemaTipo(alias),
					this.serverProperties.getJmxConfigurazioneSistemaNomeRisorsa(alias),
					metodo,
					nomePorta);
		}catch(Exception e) {
			throw new AuditException(e.getMessage(),e);
		}
		if(esito == null) {
			throw new AuditException("Aggiornamento fallito");
		}
		if(!(MSG_OPERAZIONE_OK.equals(esito) || esito.startsWith(MSG_OPERAZIONE_OK_PREFIX))) {
			throw new AuditException(esito);
		}
	}
}
