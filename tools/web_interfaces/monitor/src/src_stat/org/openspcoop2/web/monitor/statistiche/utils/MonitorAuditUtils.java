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

import org.openspcoop2.core.commons.StatoWrapper;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.web.lib.audit.appender.AuditAppender;
import org.openspcoop2.web.lib.audit.appender.AuditDisabilitatoException;
import org.openspcoop2.web.lib.audit.appender.IDOperazione;
import org.openspcoop2.web.lib.audit.log.constants.Stato;
import org.openspcoop2.web.lib.audit.log.constants.Tipologia;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.MonitorCoreAuditManager;
import org.slf4j.Logger;

/**
 * MonitorAuditUtils - Utility per la registrazione degli audit dalla Console di Monitoraggio.
 *
 * Speculare a ControlStationCore.performAuditRequest/performAuditCompleted (pattern split:
 * REQUESTING prima dell'operazione DB, COMPLETED/ERROR dopo). Per il toggle di stato
 * delle porte, l'oggetto trasportato e' avvolto in {@link StatoWrapper} per produrre
 * tipoOggetto custom (PortaApplicativaAbilitazioneStato/DisabilitazioneStato, idem PD).
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class MonitorAuditUtils {

	private static final Logger log = LoggerManager.getPddMonitorCoreLogger();

	private static final String SORGENTE = "Console di Monitoraggio";

	private MonitorAuditUtils() {}

	public static IDOperazione registraOperazioneToggleStatoInElaborazione(String utente, Object oggetto, boolean nuovoStatoAbilitato) {
		StatoWrapper wrapped = new StatoWrapper(oggetto, nuovoStatoAbilitato);
		String msg = generaMsgAuditing(utente, Stato.REQUESTING, Tipologia.CHANGE, wrapped);
		try {
			AuditAppender auditManager = MonitorCoreAuditManager.getAuditManagerInstance();
			IDOperazione idOp = auditManager.registraOperazioneInFaseDiElaborazione(
					Tipologia.CHANGE, wrapped, utente, msg,
					isAuditingRegistrazioneElementiBinari(),
					null);
			log.info(msg);
			return idOp;
		} catch (AuditDisabilitatoException disabilitato) {
			log.debug("Auditing dell'operazione [{}] non effettuato: {}", msg, disabilitato.getMessage());
			return null;
		} catch (Exception e) {
			log.error("Auditing dell'operazione [{}] non riuscito: {}", msg, e.getMessage(), e);
			return null;
		}
	}

	public static void registraOperazioneToggleStatoCompletata(IDOperazione idOperazione, String utente, Object oggetto, boolean nuovoStatoAbilitato) {
		if (idOperazione == null) {
			return;
		}
		StatoWrapper wrapped = new StatoWrapper(oggetto, nuovoStatoAbilitato);
		String msg = generaMsgAuditing(utente, Stato.COMPLETED, Tipologia.CHANGE, wrapped);
		try {
			AuditAppender auditManager = MonitorCoreAuditManager.getAuditManagerInstance();
			auditManager.registraOperazioneCompletataConSuccesso(idOperazione, msg);
			log.info(msg);
		} catch (AuditDisabilitatoException disabilitato) {
			log.debug("Auditing dell'operazione [{}] non effettuato: {}", msg, disabilitato.getMessage());
		} catch (Exception e) {
			log.error("Auditing dell'operazione [{}] non riuscito: {}", msg, e.getMessage(), e);
		}
	}

	public static void registraOperazioneToggleStatoErrore(IDOperazione idOperazione, String utente, Object oggetto, boolean nuovoStatoAbilitato, String motivoErrore) {
		if (idOperazione == null) {
			return;
		}
		StatoWrapper wrapped = new StatoWrapper(oggetto, nuovoStatoAbilitato);
		String msg = generaMsgAuditing(utente, Stato.ERROR, Tipologia.CHANGE, wrapped);
		try {
			AuditAppender auditManager = MonitorCoreAuditManager.getAuditManagerInstance();
			auditManager.registraOperazioneTerminataConErrore(idOperazione, motivoErrore, msg);
			log.info(msg);
		} catch (AuditDisabilitatoException disabilitato) {
			log.debug("Auditing dell'operazione [{}] non effettuato: {}", msg, disabilitato.getMessage());
		} catch (Exception e) {
			log.error("Auditing dell'operazione [{}] non riuscito: {}", msg, e.getMessage(), e);
		}
	}

	private static boolean isAuditingRegistrazioneElementiBinari() {
		try {
			return PddMonitorProperties.getInstance(log).isAuditingRegistrazioneElementiBinari();
		} catch (Exception e) {
			return false;
		}
	}

	private static String generaMsgAuditing(String user, Stato statoOperazione, Tipologia tipoOperazione, Object oggetto) {
		StringBuilder msg = new StringBuilder();
		msg.append(SORGENTE).append(":")
				.append(user).append(":")
				.append(statoOperazione.toString()).append(":")
				.append(tipoOperazione.toString());

		Object inner = oggetto;
		String suffix = "";
		if (oggetto instanceof StatoWrapper) {
			StatoWrapper sw = (StatoWrapper) oggetto;
			inner = sw.getObj();
			suffix = sw.isEnable() ? "AbilitazioneStato" : "DisabilitazioneStato";
		}

		if (inner instanceof PortaApplicativa) {
			PortaApplicativa pa = (PortaApplicativa) inner;
			msg.append(":").append(inner.getClass().getSimpleName()).append(suffix);
			msg.append(":<").append(pa.getNome()).append(">");
		} else if (inner instanceof PortaDelegata) {
			PortaDelegata pd = (PortaDelegata) inner;
			msg.append(":").append(inner.getClass().getSimpleName()).append(suffix);
			msg.append(":<").append(pd.getNome()).append(">");
		} else if (inner != null) {
			msg.append(":").append(inner.getClass().getSimpleName()).append(suffix);
		}
		return msg.toString();
	}
}
