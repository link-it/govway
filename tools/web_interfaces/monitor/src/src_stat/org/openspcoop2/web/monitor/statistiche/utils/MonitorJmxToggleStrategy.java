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
package org.openspcoop2.web.monitor.statistiche.utils;

import org.openspcoop2.web.lib.audit.AuditException;
import org.openspcoop2.web.lib.audit.service.JmxToggleStrategy;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.utils.JmxUtils;
import org.slf4j.Logger;

/**
 * Implementazione di {@link JmxToggleStrategy} per la Console di Monitoraggio.
 * Legge la configurazione dei metodi JMX da {@link PddMonitorProperties} e
 * invoca tramite {@link JmxUtils}.
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class MonitorJmxToggleStrategy implements JmxToggleStrategy {

	private static final String MSG_OPERAZIONE_OK = "Operazione effettuata con successo";
	private static final String MSG_OPERAZIONE_OK_PREFIX = "Operazione effettuata con successo; ";

	private final Logger log;

	public MonitorJmxToggleStrategy(Logger log) {
		this.log = log;
	}

	@Override
	public void invokeOnAlias(String alias, String nomePorta, boolean isPA, boolean enable) throws AuditException {
		
		String metodo;
		String stato;
		
		try {
			PddMonitorProperties monitorProperties = PddMonitorProperties.getInstance(this.log);
			JmxUtils jmxUtils = new JmxUtils(this.log);
	
			if(isPA) {
				metodo = enable
						? monitorProperties.getJmxPdD_configurazioneSistema_nomeMetodo_enablePortaApplicativa(alias)
						: monitorProperties.getJmxPdD_configurazioneSistema_nomeMetodo_disablePortaApplicativa(alias);
			} else {
				metodo = enable
						? monitorProperties.getJmxPdD_configurazioneSistema_nomeMetodo_enablePortaDelegata(alias)
						: monitorProperties.getJmxPdD_configurazioneSistema_nomeMetodo_disablePortaDelegata(alias);
			}
	
			stato = jmxUtils.getInvoker().invokeJMXMethod(alias,
					monitorProperties.getJmxPdD_configurazioneSistema_tipo(alias),
					monitorProperties.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias),
					metodo,
					nomePorta);
		}catch(Exception e) {
			throw new AuditException(e.getMessage(),e);
		}
		if(stato == null) {
			throw new AuditException("Aggiornamento fallito");
		}
		if(!(MSG_OPERAZIONE_OK.equals(stato) || stato.startsWith(MSG_OPERAZIONE_OK_PREFIX))) {
			throw new AuditException(stato);
		}
	}
}
