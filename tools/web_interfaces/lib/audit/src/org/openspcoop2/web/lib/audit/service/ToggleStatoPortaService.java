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

import java.util.List;

import org.openspcoop2.core.commons.StatoWrapper;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.web.lib.audit.appender.AuditAppender;
import org.openspcoop2.web.lib.audit.appender.AuditDisabilitatoException;
import org.openspcoop2.web.lib.audit.appender.IDOperazione;
import org.openspcoop2.web.lib.audit.log.constants.Stato;
import org.openspcoop2.web.lib.audit.log.constants.Tipologia;
import org.slf4j.Logger;

/**
 * Service condiviso che incapsula la logica di abilitazione/disabilitazione di
 * una porta applicativa o delegata. Esegue, in ordine:
 * <ol>
 *   <li>Lettura dell'oggetto porta dal DB di configurazione.</li>
 *   <li>Idempotenza: nessuna azione se lo stato e' gia' quello richiesto.</li>
 *   <li>Registrazione audit (Stato REQUESTING) tramite {@link AuditAppender}.</li>
 *   <li>Aggiornamento dello stato sul DB di configurazione.</li>
 *   <li>Propagazione sui nodi runtime tramite {@link JmxToggleStrategy}.</li>
 *   <li>Registrazione audit (Stato COMPLETED) o (Stato ERROR) in caso di eccezione.</li>
 * </ol>
 *
 * E' utilizzato sia dalla Console di Monitoraggio (JSF) sia dalle API REST
 * di monitoraggio. La logica di invocazione JMX e la sorgente dell'audit sono
 * delegate al chiamante via {@link JmxToggleStrategy} e {@code sorgenteAudit}.
 *
 * @author Pintori Giuliano (pintori@link.it)
 *
 */
public class ToggleStatoPortaService {

	private final DriverConfigurazioneDB driverConfigDB;
	private final AuditAppender auditAppender;
	private final boolean auditingRegistrazioneElementiBinari;
	private final JmxToggleStrategy jmxStrategy;
	private final List<String> jmxAliases;
	private final String sorgenteAudit;
	private final Logger log;

	public ToggleStatoPortaService(DriverConfigurazioneDB driverConfigDB,
			AuditAppender auditAppender,
			boolean auditingRegistrazioneElementiBinari,
			JmxToggleStrategy jmxStrategy,
			List<String> jmxAliases,
			String sorgenteAudit,
			Logger log) {
		this.driverConfigDB = driverConfigDB;
		this.auditAppender = auditAppender;
		this.auditingRegistrazioneElementiBinari = auditingRegistrazioneElementiBinari;
		this.jmxStrategy = jmxStrategy;
		this.jmxAliases = jmxAliases;
		this.sorgenteAudit = sorgenteAudit;
		this.log = log;
	}

	public void toggleStatoPorta(String nomePorta, PddRuolo ruolo, boolean enable, String utente) throws ServiceException {
		this.log.info("toggleStatoPorta: inizio - porta [{}] ruolo [{}] enable [{}] utente [{}]", nomePorta, ruolo, enable, utente);
		IDOperazione idOperazioneAudit = null;
		Object oggettoPortaAudit = null;
		String nomeGruppo = null;
		String descrizioneGruppo = null;
		boolean isDefaultGruppo = false;
		try {
			StatoFunzionalita nuovoStato = enable ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO;

			if(PddRuolo.APPLICATIVA.equals(ruolo)) {
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(nomePorta);
				PortaApplicativa pa = this.driverConfigDB.getPortaApplicativa(idPA);
				oggettoPortaAudit = pa;

				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(
						pa.getServizio().getTipo(), pa.getServizio().getNome(),
						pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(),
						pa.getServizio().getVersione());
				try {
					MappingErogazionePortaApplicativa mapping = this.driverConfigDB.getMappingErogazione(idServizio, idPA);
					if(mapping != null) {
						nomeGruppo = mapping.getNome();
						descrizioneGruppo = mapping.getDescrizione();
						isDefaultGruppo = mapping.isDefault();
					}
				} catch(DriverConfigurazioneNotFound ignored) {
					/* nessun mapping per questa PA: l'audit ID non riportera' il gruppo */
				}

				if(nuovoStato.equals(pa.getStato())) {
					this.log.debug("toggleStatoPorta: stato gia' [{}] per PA [{}], skip", nuovoStato, nomePorta);
					return;
				}

				boolean statoPrecedenteAbilitato = pa.getStato() == null ||
						StatoFunzionalita.ABILITATO.equals(pa.getStato());
				this.log.info("toggleStatoPorta: aggiornamento PA [{}] da [{}] a [{}]", nomePorta,
						statoPrecedenteAbilitato ? "abilitato" : "disabilitato",
						enable ? "abilitato" : "disabilitato");

				idOperazioneAudit = registraInElaborazione(utente, pa, enable, nomeGruppo, descrizioneGruppo, isDefaultGruppo);

				pa.setStato(nuovoStato);
				this.driverConfigDB.updatePortaApplicativa(pa);
				this.log.debug("toggleStatoPorta: update DB completato per PA [{}]", nomePorta);

				invokeJmxToggle(nomePorta, true, enable);

				registraCompletata(idOperazioneAudit, utente, pa, enable, nomeGruppo, descrizioneGruppo, isDefaultGruppo);

			} else {
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(nomePorta);
				PortaDelegata pd = this.driverConfigDB.getPortaDelegata(idPD);
				oggettoPortaAudit = pd;

				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(
						pd.getServizio().getTipo(), pd.getServizio().getNome(),
						pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome(),
						pd.getServizio().getVersione());
				IDSoggetto idFruitore = new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario());
				try {
					MappingFruizionePortaDelegata mapping = this.driverConfigDB.getMappingFruizione(idServizio, idFruitore, idPD);
					if(mapping != null) {
						nomeGruppo = mapping.getNome();
						descrizioneGruppo = mapping.getDescrizione();
						isDefaultGruppo = mapping.isDefault();
					}
				} catch(DriverConfigurazioneNotFound ignored) {
					/* nessun mapping per questa PD: l'audit ID non riportera' il gruppo */
				}

				if(nuovoStato.equals(pd.getStato())) {
					this.log.debug("toggleStatoPorta: stato gia' [{}] per PD [{}], skip", nuovoStato, nomePorta);
					return;
				}

				boolean statoPrecedenteAbilitato = pd.getStato() == null ||
						StatoFunzionalita.ABILITATO.equals(pd.getStato());
				this.log.info("toggleStatoPorta: aggiornamento PD [{}] da [{}] a [{}]", nomePorta,
						statoPrecedenteAbilitato ? "abilitato" : "disabilitato",
						enable ? "abilitato" : "disabilitato");

				idOperazioneAudit = registraInElaborazione(utente, pd, enable, nomeGruppo, descrizioneGruppo, isDefaultGruppo);

				pd.setStato(nuovoStato);
				this.driverConfigDB.updatePortaDelegata(pd);
				this.log.debug("toggleStatoPorta: update DB completato per PD [{}]", nomePorta);

				invokeJmxToggle(nomePorta, false, enable);

				registraCompletata(idOperazioneAudit, utente, pd, enable, nomeGruppo, descrizioneGruppo, isDefaultGruppo);
			}
			this.log.info("toggleStatoPorta: completato con successo per porta [{}]", nomePorta);

		} catch(DriverConfigurazioneNotFound e) {
			this.log.error("toggleStatoPorta: porta non trovata [{}]: {}", nomePorta, e.getMessage(), e);
			registraErrore(idOperazioneAudit, utente, oggettoPortaAudit, enable, nomeGruppo, descrizioneGruppo, isDefaultGruppo, e.getMessage());
			throw new ServiceException("Porta '" + nomePorta + "' non trovata: " + e.getMessage(), e);
		} catch(DriverConfigurazioneException e) {
			this.log.error("toggleStatoPorta: errore DB per porta [{}]: {}", nomePorta, e.getMessage(), e);
			registraErrore(idOperazioneAudit, utente, oggettoPortaAudit, enable, nomeGruppo, descrizioneGruppo, isDefaultGruppo, e.getMessage());
			throw new ServiceException("Errore durante il toggle dello stato della porta '" + nomePorta + "': " + e.getMessage(), e);
		} catch(Exception e) {
			this.log.error("toggleStatoPorta: errore per porta [{}]: {}", nomePorta, e.getMessage(), e);
			registraErrore(idOperazioneAudit, utente, oggettoPortaAudit, enable, nomeGruppo, descrizioneGruppo, isDefaultGruppo, e.getMessage());
			throw new ServiceException("Errore durante il toggle dello stato della porta '" + nomePorta + "': " + e.getMessage(), e);
		}
	}

	private void invokeJmxToggle(String nomePorta, boolean isPA, boolean enable) {
		if(this.jmxStrategy == null || this.jmxAliases == null || this.jmxAliases.isEmpty()) {
			return;
		}
		for(String alias : this.jmxAliases) {
			try {
				this.jmxStrategy.invokeOnAlias(alias, nomePorta, isPA, enable);
			} catch(Exception e) {
				String tipo = isPA ? "PortaApplicativa" : "PortaDelegata";
				this.log.error("toggleStatoPorta: JMX toggle {} [{}] alias [{}] fallito: {}", tipo, nomePorta, alias, e.getMessage(), e);
			}
		}
	}

	private IDOperazione registraInElaborazione(String utente, Object oggetto, boolean enable,
			String nomeGruppo, String descrizioneGruppo, boolean isDefaultGruppo) {
		StatoWrapper wrapped = buildStatoWrapper(oggetto, enable, nomeGruppo, descrizioneGruppo, isDefaultGruppo);
		String msg = generaMsgAuditing(utente, Stato.REQUESTING, Tipologia.CHANGE, wrapped);
		try {
			IDOperazione idOp = this.auditAppender.registraOperazioneInFaseDiElaborazione(
					Tipologia.CHANGE, wrapped, utente, msg, this.auditingRegistrazioneElementiBinari, null);
			this.log.info(msg);
			return idOp;
		} catch(AuditDisabilitatoException disabilitato) {
			this.log.debug("Auditing dell'operazione [{}] non effettuato: {}", msg, disabilitato.getMessage());
			return null;
		} catch(Exception e) {
			this.log.error("Auditing dell'operazione [{}] non riuscito: {}", msg, e.getMessage(), e);
			return null;
		}
	}

	private void registraCompletata(IDOperazione idOperazione, String utente, Object oggetto, boolean enable,
			String nomeGruppo, String descrizioneGruppo, boolean isDefaultGruppo) {
		if(idOperazione == null) {
			return;
		}
		StatoWrapper wrapped = buildStatoWrapper(oggetto, enable, nomeGruppo, descrizioneGruppo, isDefaultGruppo);
		String msg = generaMsgAuditing(utente, Stato.COMPLETED, Tipologia.CHANGE, wrapped);
		try {
			this.auditAppender.registraOperazioneCompletataConSuccesso(idOperazione, msg);
			this.log.info(msg);
		} catch(AuditDisabilitatoException disabilitato) {
			this.log.debug("Auditing dell'operazione [{}] non effettuato: {}", msg, disabilitato.getMessage());
		} catch(Exception e) {
			this.log.error("Auditing dell'operazione [{}] non riuscito: {}", msg, e.getMessage(), e);
		}
	}

	private void registraErrore(IDOperazione idOperazione, String utente, Object oggetto, boolean enable,
			String nomeGruppo, String descrizioneGruppo, boolean isDefaultGruppo, String motivoErrore) {
		if(idOperazione == null) {
			return;
		}
		StatoWrapper wrapped = buildStatoWrapper(oggetto, enable, nomeGruppo, descrizioneGruppo, isDefaultGruppo);
		String msg = generaMsgAuditing(utente, Stato.ERROR, Tipologia.CHANGE, wrapped);
		try {
			this.auditAppender.registraOperazioneTerminataConErrore(idOperazione, motivoErrore, msg);
			this.log.info(msg);
		} catch(AuditDisabilitatoException disabilitato) {
			this.log.debug("Auditing dell'operazione [{}] non effettuato: {}", msg, disabilitato.getMessage());
		} catch(Exception e) {
			this.log.error("Auditing dell'operazione [{}] non riuscito: {}", msg, e.getMessage(), e);
		}
	}

	private static StatoWrapper buildStatoWrapper(Object oggetto, boolean enable,
			String nomeGruppo, String descrizioneGruppo, boolean isDefaultGruppo) {
		StatoWrapper wrapped = new StatoWrapper(oggetto, enable);
		wrapped.setNomeGruppo(nomeGruppo);
		wrapped.setDescrizioneGruppo(descrizioneGruppo);
		wrapped.setDefaultGruppo(isDefaultGruppo);
		return wrapped;
	}

	private String generaMsgAuditing(String utente, Stato stato, Tipologia tipo, Object oggetto) {
		StringBuilder msg = new StringBuilder();
		if(this.sorgenteAudit != null && !this.sorgenteAudit.isEmpty()) {
			msg.append(this.sorgenteAudit).append(":");
		}
		msg.append(utente).append(":")
				.append(stato.toString()).append(":")
				.append(tipo.toString());

		Object inner = oggetto;
		String suffix = "";
		if(oggetto instanceof StatoWrapper) {
			StatoWrapper sw = (StatoWrapper) oggetto;
			inner = sw.getObj();
			suffix = sw.isEnable() ? "AbilitazioneStato" : "DisabilitazioneStato";
		}

		if(inner instanceof PortaApplicativa) {
			PortaApplicativa pa = (PortaApplicativa) inner;
			msg.append(":").append(inner.getClass().getSimpleName()).append(suffix);
			msg.append(":<").append(pa.getNome()).append(">");
		} else if(inner instanceof PortaDelegata) {
			PortaDelegata pd = (PortaDelegata) inner;
			msg.append(":").append(inner.getClass().getSimpleName()).append(suffix);
			msg.append(":<").append(pd.getNome()).append(">");
		} else if(inner != null) {
			msg.append(":").append(inner.getClass().getSimpleName()).append(suffix);
		}
		return msg.toString();
	}
}
