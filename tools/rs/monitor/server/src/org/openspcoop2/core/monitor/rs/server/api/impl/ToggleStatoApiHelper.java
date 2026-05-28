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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.monitor.rs.server.config.ApiMonitorAuditException;
import org.openspcoop2.core.monitor.rs.server.config.ApiMonitorAuditManager;
import org.openspcoop2.core.monitor.rs.server.config.DBManager;
import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.core.monitor.rs.server.model.FiltroApiStato;
import org.openspcoop2.core.monitor.rs.server.model.StatoConfigurazioneApi;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.TransazioneRuoloEnum;
import org.openspcoop2.utils.service.beans.utils.ProfiloUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.lib.audit.appender.AuditAppender;
import org.openspcoop2.web.lib.audit.service.ToggleStatoPortaService;
import org.slf4j.Logger;

/**
 * Helper REST per le operazioni di abilitazione/disabilitazione dello stato
 * di una erogazione o fruizione di un'API, esposte via
 * {@code /reportistica/configurazione-api/stato}. Si occupa di:
 * <ul>
 *   <li>risolvere i parametri di ricerca verso uno o piu' identificatori di porta;</li>
 *   <li>filtrare per la lista di gruppi (azioni/risorse) eventualmente specificata;</li>
 *   <li>delegare il toggle effettivo (DB + JMX + audit) a {@link ToggleStatoPortaService}.</li>
 * </ul>
 *
 * Il controllo di autorizzazione e' demandato all'ACL applicativa di rs-api-monitor
 * (vedi {@code AuthorizationManager.authorize(context, getAuthorizationConfig())}).
 *
 * @author Pintori Giuliano (pintori@link.it)
 *
 */
public class ToggleStatoApiHelper {

	private ToggleStatoApiHelper() {}

	/** Adatta la chiamata "bySimpleSearch" alla forma "byFullSearch" e delega. */
	public static void executeBySimpleSearch(IContext context, TransazioneRuoloEnum tipo, Boolean abilitato,
			String nomeServizio, ProfiloEnum profilo, String soggetto, String soggettoRemoto,
			String tipoServizio, Integer versioneServizio) throws Exception {
		StatoConfigurazioneApi body = new StatoConfigurazioneApi();
		body.setTipo(tipo);
		body.setAbilitato(abilitato);
		body.setGruppi(null); // simple search: si applica a tutti i gruppi
		FiltroApiStato api = new FiltroApiStato();
		api.setTipo(tipoServizio);
		api.setNome(nomeServizio);
		api.setVersione(versioneServizio);
		if(TransazioneRuoloEnum.FRUIZIONE.equals(tipo)) {
			api.setErogatore(soggettoRemoto);
		}
		body.setApi(api);
		executeByFullSearch(context, body, profilo, soggetto);
	}

	public static void executeByFullSearch(IContext context, StatoConfigurazioneApi body, ProfiloEnum profilo, String soggetto)
			throws Exception {
		validate(body);

		String utente = context.getAuthentication().getName();
		TransazioneRuoloEnum tipo = body.getTipo();
		boolean enable = Boolean.TRUE.equals(body.isAbilitato());
		FiltroApiStato apiFiltro = body.getApi();
		List<String> gruppiFiltro = body.getGruppi();

		ProfiloEnum profiloEffettivo = profilo;
		if(profiloEffettivo == null) {
			profiloEffettivo = ProfiloUtils.getMapProtocolloToProfilo().get(ServerProperties.getInstance().getProtocolloDefault());
		}
		String protocollo = ProfiloUtils.toProtocollo(profiloEffettivo);
		String tipoSoggetto = ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(protocollo);

		String tipoServizioRichiesto = apiFiltro.getTipo();
		if(tipoServizioRichiesto == null || tipoServizioRichiesto.isEmpty()) {
			tipoServizioRichiesto = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).createProtocolConfiguration().getTipoServizioDefault(null);
		}
		Integer versioneServizio = apiFiltro.getVersione() != null ? apiFiltro.getVersione() : Integer.valueOf(1);

		// Mappa tipo + (soggetto vs erogatore) su IDServizio + idSoggettoFruitore (se fruizione)
		IDServizio idServizio;
		IDSoggetto idSoggettoFruitore = null;
		if(TransazioneRuoloEnum.EROGAZIONE.equals(tipo)) {
			if(soggetto == null || soggetto.isEmpty()) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Per il tipo EROGAZIONE il soggetto erogatore deve essere fornito tramite il parametro 'soggetto'");
			}
			idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(
					tipoServizioRichiesto, apiFiltro.getNome(),
					tipoSoggetto, soggetto,
					versioneServizio);
		} else if(TransazioneRuoloEnum.FRUIZIONE.equals(tipo)) {
			String erogatore = apiFiltro.getErogatore();
			if(erogatore == null || erogatore.isEmpty()) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Per il tipo FRUIZIONE il soggetto erogatore deve essere fornito tramite il campo 'erogatore' del filtro");
			}
			if(soggetto == null || soggetto.isEmpty()) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Per il tipo FRUIZIONE il soggetto fruitore deve essere fornito tramite il parametro 'soggetto'");
			}
			idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(
					tipoServizioRichiesto, apiFiltro.getNome(),
					tipoSoggetto, erogatore,
					versioneServizio);
			idSoggettoFruitore = new IDSoggetto(tipoSoggetto, soggetto);
		} else {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo non valido: " + tipo);
		}

		// Recupera mapping (eventualmente filtrati per gruppi) tramite connessione DB di configurazione
		Logger log = LoggerWrapperFactory.getLogger(ToggleStatoApiHelper.class);
		DBManager dbManager = DBManager.getInstance();
		Connection connection = null;
		List<String> nomiPortaDaToggle = new ArrayList<>();
		String tipoDB = dbManager.getServiceManagerPropertiesConfig().getDatabaseType();
		try {
			connection = dbManager.getConnectionConfig();
			Set<String> filtroGruppi = (gruppiFiltro != null && !gruppiFiltro.isEmpty()) ? new HashSet<>(gruppiFiltro) : null;
			// I nomi user-visible dei gruppi (quelli esposti via API /erogazioni|fruizioni/{...}/gruppi
			// e accettati come input qui) corrispondono al campo 'descrizione' del mapping,
			// non al campo 'nome' che invece contiene un identificativo interno auto-generato
			// (vedi ErogazioniGruppiApiServiceImpl#getErogazioneGruppi: g.setNome(m.getDescrizione())).
			if(TransazioneRuoloEnum.EROGAZIONE.equals(tipo)) {
				List<MappingErogazionePortaApplicativa> mappings;
				try {
					mappings = DBMappingUtils.mappingErogazionePortaApplicativaList(connection, tipoDB, idServizio, false);
				} catch(Exception e) {
					throw FaultCode.NOT_FOUND.toException("API '" + idServizio.toString() + "' non trovata: " + e.getMessage());
				}
				if(mappings == null || mappings.isEmpty()) {
					throw FaultCode.NOT_FOUND.toException("Nessuna configurazione trovata per l'API '" + idServizio.toString() + "'");
				}
				for(MappingErogazionePortaApplicativa m : mappings) {
					if(filtroGruppi == null || filtroGruppi.contains(m.getDescrizione())) {
						nomiPortaDaToggle.add(m.getIdPortaApplicativa().getNome());
					}
				}
			} else {
				List<MappingFruizionePortaDelegata> mappings;
				try {
					mappings = DBMappingUtils.mappingFruizionePortaDelegataList(connection, tipoDB,
							idSoggettoFruitore, idServizio, null, false, false);
				} catch(Exception e) {
					throw FaultCode.NOT_FOUND.toException("Fruizione '" + idServizio.toString() + "' (fruitore " + idSoggettoFruitore + ") non trovata: " + e.getMessage());
				}
				if(mappings == null || mappings.isEmpty()) {
					throw FaultCode.NOT_FOUND.toException("Nessuna configurazione trovata per la fruizione '" + idServizio.toString() + "' (fruitore " + idSoggettoFruitore + ")");
				}
				for(MappingFruizionePortaDelegata m : mappings) {
					if(filtroGruppi == null || filtroGruppi.contains(m.getDescrizione())) {
						nomiPortaDaToggle.add(m.getIdPortaDelegata().getNome());
					}
				}
			}
			if(filtroGruppi != null && nomiPortaDaToggle.isEmpty()) {
				throw FaultCode.NOT_FOUND.toException("Nessun gruppo tra quelli indicati (" + gruppiFiltro + ") trovato per l'API '" + idServizio.toString() + "'");
			}
		} finally {
			dbManager.releaseConnectionConfig(connection);
		}

		// Allestisce il service condiviso. Usiamo i parametri del DBManager (gia' inizializzato in Startup)
		// invece di DAOFactoryProperties, perche' il modulo rs-api-monitor non definisce le proprieta'
		// "db.tipo" richieste da DAOFactoryProperties per il project info "core.commons.search".
		ServerProperties serverProperties = ServerProperties.getInstance();
		DriverConfigurazioneDB driverConfigDB;
		List<String> aliasJmx;
		try {
			String datasourceJNDIName = dbManager.getDataSourceConfigName();
			Properties datasourceJNDIContext = dbManager.getDataSourceConfigContext();
			driverConfigDB = new DriverConfigurazioneDB(datasourceJNDIName, datasourceJNDIContext, log, tipoDB);
			org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime nodiRuntime =
					org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime.getConfigurazioneNodiRuntime();
			aliasJmx = (nodiRuntime != null) ? nodiRuntime.getAliases() : null;
		} catch(Exception e) {
			throw FaultCode.ERRORE_INTERNO.toException("Inizializzazione DriverConfigurazioneDB non riuscita: " + e.getMessage());
		}

		AuditAppender auditAppender;
		try {
			auditAppender = ApiMonitorAuditManager.getAuditManagerInstance();
		} catch(ApiMonitorAuditException e) {
			throw FaultCode.ERRORE_INTERNO.toException("AuditManager non disponibile: " + e.getMessage());
		}

		ApiMonitorJmxToggleStrategy jmxStrategy = new ApiMonitorJmxToggleStrategy(serverProperties, log);
		ToggleStatoPortaService service = new ToggleStatoPortaService(
				driverConfigDB,
				auditAppender,
				false /* registrazioneElementiBinari: il toggle stato non porta binari */,
				jmxStrategy,
				aliasJmx,
				"API Monitoraggio",
				log);

		PddRuolo ruolo = TransazioneRuoloEnum.EROGAZIONE.equals(tipo) ? PddRuolo.APPLICATIVA : PddRuolo.DELEGATA;
		for(String nomePorta : nomiPortaDaToggle) {
			try {
				service.toggleStatoPorta(nomePorta, ruolo, enable, utente);
			} catch(ServiceException e) {
				Throwable cause = e.getCause();
				if(cause instanceof DriverConfigurazioneNotFound) {
					throw FaultCode.NOT_FOUND.toException("Porta '" + nomePorta + "' non trovata: " + e.getMessage());
				}
				throw FaultCode.ERRORE_INTERNO.toException("Errore durante il toggle dello stato della porta '" + nomePorta + "': " + e.getMessage());
			}
		}
	}

	private static void validate(StatoConfigurazioneApi body) {
		if(body == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Body assente");
		}
		
		TransazioneRuoloEnum tipo = body.getTipo();
		Boolean abilitato = body.isAbilitato();
		check(tipo, abilitato);
		
		FiltroApiStato filtroApi = body.getApi();
		check(filtroApi);
		
		String nome = body.getApi().getNome();
		checkNome(nome);
		
	}
	private static void check(TransazioneRuoloEnum tipo, Boolean abilitato) {
		if(tipo == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il campo 'tipo' e' obbligatorio");
		}
		if(abilitato == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il campo 'abilitato' e' obbligatorio");
		}
	}
	private static void check(FiltroApiStato filtroApi) {
		if(filtroApi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il campo 'api' e' obbligatorio");
		}
	}
	private static void checkNome(String nome) {
		if(nome == null || nome.isEmpty()) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il campo 'api.nome' e' obbligatorio");
		}
	}
}
