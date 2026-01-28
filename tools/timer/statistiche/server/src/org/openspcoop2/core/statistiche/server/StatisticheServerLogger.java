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
package org.openspcoop2.core.statistiche.server;

import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * StatisticheServerLogger
 *
 * Singleton che gestisce i logger per ogni tipo di statistica, allineato con OpenSPCoop2Logger.
 * Ogni tipo di statistica ha il proprio file di log separato.
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticheServerLogger {

	private static StatisticheServerLogger instance;

	// Logger Statistiche Orarie
	private Logger loggerStatisticheOrarie;
	private Logger loggerStatisticheOrarieError;
	private Logger loggerStatisticheOrarieSql;
	private Logger loggerStatisticheOrarieSqlError;

	// Logger Statistiche Giornaliere
	private Logger loggerStatisticheGiornaliere;
	private Logger loggerStatisticheGiornaliereError;
	private Logger loggerStatisticheGiornaliereSql;
	private Logger loggerStatisticheGiornaliereSqlError;

	// Logger Statistiche Settimanali
	private Logger loggerStatisticheSettimanali;
	private Logger loggerStatisticheSettimanaliError;
	private Logger loggerStatisticheSettimanaliSql;
	private Logger loggerStatisticheSettimanaliSqlError;

	// Logger Statistiche Mensili
	private Logger loggerStatisticheMensili;
	private Logger loggerStatisticheMensiliError;
	private Logger loggerStatisticheMensiliSql;
	private Logger loggerStatisticheMensiliSqlError;

	// Logger PDND Generazione
	private Logger loggerStatistichePdndGenerazione;
	private Logger loggerStatistichePdndGenerazioneError;
	private Logger loggerStatistichePdndGenerazioneSql;
	private Logger loggerStatistichePdndGenerazioneSqlError;

	// Logger PDND Pubblicazione
	private Logger loggerStatistichePdndPubblicazione;
	private Logger loggerStatistichePdndPubblicazioneError;
	private Logger loggerStatistichePdndPubblicazioneSql;
	private Logger loggerStatistichePdndPubblicazioneSqlError;

	// Logger Server (generale)
	private Logger loggerServer;
	private Logger loggerServerError;

	private StatisticheServerLogger() {
		// Statistiche Orarie
		this.loggerStatisticheOrarie = LoggerWrapperFactory.getLogger("govway.statistiche_orarie.generazione");
		this.loggerStatisticheOrarieError = LoggerWrapperFactory.getLogger("govway.statistiche_orarie.generazione.error");
		this.loggerStatisticheOrarieSql = LoggerWrapperFactory.getLogger("govway.statistiche_orarie.generazione.sql");
		this.loggerStatisticheOrarieSqlError = LoggerWrapperFactory.getLogger("govway.statistiche_orarie.generazione.sql.error");

		// Statistiche Giornaliere
		this.loggerStatisticheGiornaliere = LoggerWrapperFactory.getLogger("govway.statistiche_giornaliere.generazione");
		this.loggerStatisticheGiornaliereError = LoggerWrapperFactory.getLogger("govway.statistiche_giornaliere.generazione.error");
		this.loggerStatisticheGiornaliereSql = LoggerWrapperFactory.getLogger("govway.statistiche_giornaliere.generazione.sql");
		this.loggerStatisticheGiornaliereSqlError = LoggerWrapperFactory.getLogger("govway.statistiche_giornaliere.generazione.sql.error");

		// Statistiche Settimanali
		this.loggerStatisticheSettimanali = LoggerWrapperFactory.getLogger("govway.statistiche_settimanali.generazione");
		this.loggerStatisticheSettimanaliError = LoggerWrapperFactory.getLogger("govway.statistiche_settimanali.generazione.error");
		this.loggerStatisticheSettimanaliSql = LoggerWrapperFactory.getLogger("govway.statistiche_settimanali.generazione.sql");
		this.loggerStatisticheSettimanaliSqlError = LoggerWrapperFactory.getLogger("govway.statistiche_settimanali.generazione.sql.error");

		// Statistiche Mensili
		this.loggerStatisticheMensili = LoggerWrapperFactory.getLogger("govway.statistiche_mensili.generazione");
		this.loggerStatisticheMensiliError = LoggerWrapperFactory.getLogger("govway.statistiche_mensili.generazione.error");
		this.loggerStatisticheMensiliSql = LoggerWrapperFactory.getLogger("govway.statistiche_mensili.generazione.sql");
		this.loggerStatisticheMensiliSqlError = LoggerWrapperFactory.getLogger("govway.statistiche_mensili.generazione.sql.error");

		// PDND Generazione
		this.loggerStatistichePdndGenerazione = LoggerWrapperFactory.getLogger("govway.statistiche_pdnd_tracciamento.generazione");
		this.loggerStatistichePdndGenerazioneError = LoggerWrapperFactory.getLogger("govway.statistiche_pdnd_tracciamento.generazione.error");
		this.loggerStatistichePdndGenerazioneSql = LoggerWrapperFactory.getLogger("govway.statistiche_pdnd_tracciamento.generazione.sql");
		this.loggerStatistichePdndGenerazioneSqlError = LoggerWrapperFactory.getLogger("govway.statistiche_pdnd_tracciamento.generazione.sql.error");

		// PDND Pubblicazione
		this.loggerStatistichePdndPubblicazione = LoggerWrapperFactory.getLogger("govway.statistiche_pdnd_tracciamento.pubblicazione");
		this.loggerStatistichePdndPubblicazioneError = LoggerWrapperFactory.getLogger("govway.statistiche_pdnd_tracciamento.pubblicazione.error");
		this.loggerStatistichePdndPubblicazioneSql = LoggerWrapperFactory.getLogger("govway.statistiche_pdnd_tracciamento.pubblicazione.sql");
		this.loggerStatistichePdndPubblicazioneSqlError = LoggerWrapperFactory.getLogger("govway.statistiche_pdnd_tracciamento.pubblicazione.sql.error");

		// Server (generale)
		this.loggerServer = LoggerWrapperFactory.getLogger("govway.statistiche.server");
		this.loggerServerError = LoggerWrapperFactory.getLogger("govway.statistiche.server.error");
	}

	public static synchronized StatisticheServerLogger getInstance() {
		if (instance == null) {
			instance = new StatisticheServerLogger();
		}
		return instance;
	}

	/**
	 * Ottiene il logger core per il tipo di statistica specificato.
	 *
	 * @param tipoStatistica Tipo di statistica
	 * @param debug Se true, usa il logger debug (livello INFO), altrimenti error (livello ERROR)
	 * @return Logger appropriato
	 */
	public Logger getLoggerStatistiche(TipoIntervalloStatistico tipoStatistica, boolean debug) {
		if (debug) {
			switch (tipoStatistica) {
			case STATISTICHE_ORARIE:
				return this.loggerStatisticheOrarie;
			case STATISTICHE_GIORNALIERE:
				return this.loggerStatisticheGiornaliere;
			case STATISTICHE_SETTIMANALI:
				return this.loggerStatisticheSettimanali;
			case STATISTICHE_MENSILI:
				return this.loggerStatisticheMensili;
			case PDND_GENERAZIONE_TRACCIAMENTO:
				return this.loggerStatistichePdndGenerazione;
			case PDND_PUBBLICAZIONE_TRACCIAMENTO:
				return this.loggerStatistichePdndPubblicazione;
			}
		} else {
			switch (tipoStatistica) {
			case STATISTICHE_ORARIE:
				return this.loggerStatisticheOrarieError;
			case STATISTICHE_GIORNALIERE:
				return this.loggerStatisticheGiornaliereError;
			case STATISTICHE_SETTIMANALI:
				return this.loggerStatisticheSettimanaliError;
			case STATISTICHE_MENSILI:
				return this.loggerStatisticheMensiliError;
			case PDND_GENERAZIONE_TRACCIAMENTO:
				return this.loggerStatistichePdndGenerazioneError;
			case PDND_PUBBLICAZIONE_TRACCIAMENTO:
				return this.loggerStatistichePdndPubblicazioneError;
			}
		}
		return this.loggerServer;
	}

	/**
	 * Ottiene il logger SQL per il tipo di statistica specificato.
	 *
	 * @param tipoStatistica Tipo di statistica
	 * @param debug Se true, usa il logger debug (livello INFO), altrimenti error (livello ERROR)
	 * @return Logger SQL appropriato
	 */
	public Logger getLoggerStatisticheSql(TipoIntervalloStatistico tipoStatistica, boolean debug) {
		if (debug) {
			switch (tipoStatistica) {
			case STATISTICHE_ORARIE:
				return this.loggerStatisticheOrarieSql;
			case STATISTICHE_GIORNALIERE:
				return this.loggerStatisticheGiornaliereSql;
			case STATISTICHE_SETTIMANALI:
				return this.loggerStatisticheSettimanaliSql;
			case STATISTICHE_MENSILI:
				return this.loggerStatisticheMensiliSql;
			case PDND_GENERAZIONE_TRACCIAMENTO:
				return this.loggerStatistichePdndGenerazioneSql;
			case PDND_PUBBLICAZIONE_TRACCIAMENTO:
				return this.loggerStatistichePdndPubblicazioneSql;
			}
		} else {
			switch (tipoStatistica) {
			case STATISTICHE_ORARIE:
				return this.loggerStatisticheOrarieSqlError;
			case STATISTICHE_GIORNALIERE:
				return this.loggerStatisticheGiornaliereSqlError;
			case STATISTICHE_SETTIMANALI:
				return this.loggerStatisticheSettimanaliSqlError;
			case STATISTICHE_MENSILI:
				return this.loggerStatisticheMensiliSqlError;
			case PDND_GENERAZIONE_TRACCIAMENTO:
				return this.loggerStatistichePdndGenerazioneSqlError;
			case PDND_PUBBLICAZIONE_TRACCIAMENTO:
				return this.loggerStatistichePdndPubblicazioneSqlError;
			}
		}
		return this.loggerServer;
	}

	/**
	 * @return Logger generale del server
	 */
	public Logger getLoggerServer() {
		return this.loggerServer;
	}

	/**
	 * @return Logger errori generale del server
	 */
	public Logger getLoggerServerError() {
		return this.loggerServerError;
	}
}
