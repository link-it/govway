/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

/**
 * CheckServlet
 *
 * Servlet che verifica lo stato di salute del monitor GovWay.
 * Ritorna 200 se il monitor è correttamente inizializzato e tutte le risorse database sono disponibili.
 * Ritorna 503 se il monitor non è ancora inizializzato.
 * Ritorna 500 in caso di problemi con le risorse database.
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CheckServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private static final String LOG_PREFIX = "[GovWayMonitorCheck] ";
	private static final String DB_CONFIGURAZIONE = "Database Configurazione";
	private static final String DB_TRACCIAMENTO = "Database Tracciamento";
	private static final String DB_STATISTICHE = "Database Statistiche";
	private static final String SERVICE_MANAGER_NON_DISPONIBILE = "ServiceManager non disponibile";
	private static final String DB_NON_DISPONIBILE = " non disponibile: ";
	private static final String SEND_ERROR_FAILED = "SendError failed: ";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		// Verifica se il monitor è inizializzato
		if(!ConsoleStartupListener.initialized) {
			serializeNotInitializedResponse(res);
			return;
		}

		// Verifica le risorse database
		StringBuilder sbError = new StringBuilder();
		boolean allDbOk = true;

		// 1. Verifica database Configurazione
		if(!checkDatabaseConfigurazione(sbError)) {
			allDbOk = false;
		}

		// 2. Verifica database Tracciamento
		if(!checkDatabaseTracciamento(sbError)) {
			allDbOk = false;
		}

		// 3. Verifica database Statistiche
		if(!checkDatabaseStatistiche(sbError)) {
			allDbOk = false;
		}

		// Risposta
		if(allDbOk) {
			serializeOkResponse(res);
		} else {
			serializeErrorResponse(res, sbError.toString());
		}
	}

	private boolean checkDatabaseConfigurazione(StringBuilder sbError) {
		org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager = null;
		try {
			// Inizializza il service manager per il database di configurazione
			utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager)
					DAOFactory.getInstance(log).getServiceManager(
							org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), log);

			if(utilsServiceManager == null) {
				appendError(sbError, DB_CONFIGURAZIONE, SERVICE_MANAGER_NON_DISPONIBILE);
				return false;
			}

			// Legge la query custom dalle properties
			PddMonitorProperties props = PddMonitorProperties.getInstance(log);
			String query = props.getCheckDbConfigurazioneQuery();
			if(query == null || query.trim().isEmpty()) {
				// Fallback alla query di default
				query = "SELECT id FROM configurazione";
			}
			
			List<Class<?>> returnTypes = new ArrayList<>();
			returnTypes.add(Long.class); // id
			
			Object[] parameters = new Object[] {};

			utilsServiceManager.getResourceServiceSearch().nativeQuery(query, returnTypes, parameters);
			
			log.debug(LOG_PREFIX + DB_CONFIGURAZIONE + ": OK");
			return true;

		} catch(Exception e) {
			appendError(sbError, DB_CONFIGURAZIONE, e.getMessage());
			log.error(LOG_PREFIX + DB_CONFIGURAZIONE + DB_NON_DISPONIBILE + e.getMessage(), e);
			return false;
		} finally {
			// Non chiudere la connection, è gestita dal service manager
		}
	}

	private boolean checkDatabaseTracciamento(StringBuilder sbError) {
		org.openspcoop2.core.transazioni.dao.IServiceManager transazioniServiceManager = null;
		try {
			// Inizializza il service manager per il database di tracciamento
			transazioniServiceManager = (org.openspcoop2.core.transazioni.dao.IServiceManager)
					DAOFactory.getInstance(log).getServiceManager(
							org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), log);

			if(transazioniServiceManager == null) {
				appendError(sbError, DB_TRACCIAMENTO, SERVICE_MANAGER_NON_DISPONIBILE);
				return false;
			}

			// Legge la query custom dalle properties
			PddMonitorProperties props = PddMonitorProperties.getInstance(log);
			String query = props.getCheckDbTracciamentoQuery();
			if(query == null || query.trim().isEmpty()) {
				// Fallback alla query di default
				query = "SELECT id FROM transazioni_esiti";
			}

			List<Class<?>> returnTypes = new ArrayList<>();
			returnTypes.add(Long.class); // id

			Object[] parameters = new Object[] {};

			transazioniServiceManager.getTransazioneServiceSearch().nativeQuery(query, returnTypes, parameters);

			log.debug(LOG_PREFIX + DB_TRACCIAMENTO + ": OK");
			return true;

		} catch(Exception e) {
			appendError(sbError, DB_TRACCIAMENTO, e.getMessage());
			log.error(LOG_PREFIX + DB_TRACCIAMENTO + DB_NON_DISPONIBILE + e.getMessage(), e);
			return false;
		} finally {
			// Non chiudere la connection, è gestita dal service manager
		}
	}

	private boolean checkDatabaseStatistiche(StringBuilder sbError) {
		org.openspcoop2.core.statistiche.dao.IServiceManager statisticheServiceManager = null;
		try {
			// Inizializza il service manager per il database di statistiche
			statisticheServiceManager = (org.openspcoop2.core.statistiche.dao.IServiceManager)
					DAOFactory.getInstance(log).getServiceManager(
							org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(), log);

			if(statisticheServiceManager == null) {
				appendError(sbError, DB_STATISTICHE, SERVICE_MANAGER_NON_DISPONIBILE);
				return false;
			}

			// Legge la query custom dalle properties
			PddMonitorProperties props = PddMonitorProperties.getInstance(log);
			String query = props.getCheckDbStatisticheQuery();
			if(query == null || query.trim().isEmpty()) {
				// Fallback alla query di default
				query = "SELECT id FROM statistiche";
			}

			List<Class<?>> returnTypes = new ArrayList<>();
			returnTypes.add(Long.class); // id

			Object[] parameters = new Object[] {};

			statisticheServiceManager.getStatisticaInfoServiceSearch().nativeQuery(query, returnTypes, parameters);

			log.debug(LOG_PREFIX + DB_STATISTICHE + ": OK");
			return true;

		} catch(Exception e) {
			appendError(sbError, DB_STATISTICHE, e.getMessage());
			log.error(LOG_PREFIX + DB_STATISTICHE + DB_NON_DISPONIBILE + e.getMessage(), e);
			return false;
		} finally {
			// Non chiudere la connection, è gestita dal service manager
		}
	}

	private void appendError(StringBuilder sb, String resourceName, String errorMsg) {
		if(!sb.isEmpty()) {
			sb.append("; ");
		}
		sb.append(resourceName).append(": ").append(errorMsg);
	}

	private void serializeNotInitializedResponse(HttpServletResponse res) {
		String msg = "GovWay Monitor non inizializzato";
		log.error(LOG_PREFIX + "{}", msg);
		res.setStatus(503);
		res.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		try {
			res.getOutputStream().write(msg.getBytes());
		} catch(Exception e) {
			log.error(LOG_PREFIX + SEND_ERROR_FAILED + e.getMessage(), e);
		}
	}

	private void serializeOkResponse(HttpServletResponse res) {
		log.debug(LOG_PREFIX + "GovWay Monitor OK");
		res.setStatus(200);
		// Non viene inviato nessun body in caso di risposta positiva
	}

	private void serializeErrorResponse(HttpServletResponse res, String errorDetails) {
		String msg = "GovWay Monitor ERROR: " + errorDetails;
		log.error(LOG_PREFIX + "{}", msg);
		res.setStatus(500);
		res.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		try {
			res.getOutputStream().write(msg.getBytes());
		} catch(Exception e) {
			log.error(LOG_PREFIX + SEND_ERROR_FAILED + e.getMessage(), e);
		}
	}
}
