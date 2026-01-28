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

import java.sql.Connection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.statistic.StatisticsConfig;
import org.openspcoop2.monitor.engine.statistic.StatisticsLibrary;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.semaphore.Semaphore;
import org.openspcoop2.utils.semaphore.SemaphoreConfiguration;
import org.openspcoop2.utils.semaphore.SemaphoreMapping;
import org.slf4j.Logger;

/**
 * StatisticheServerEsecutor
 *
 * Gestisce l'esecuzione periodica dei job per il calcolo delle statistiche.
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticheServerExecutor {

	private final Logger logCore;
	private final StatisticheServerProperties serverProperties;

	private ScheduledExecutorService scheduler;

	private ScheduledFuture<?> timerStatisticheOrarie;
	private ScheduledFuture<?> timerStatisticheGiornaliere;
	private ScheduledFuture<?> timerStatisticheSettimanali;
	private ScheduledFuture<?> timerStatisticheMensili;
	private ScheduledFuture<?> timerPdndGenerazione;
	private ScheduledFuture<?> timerPdndPubblicazione;

	// Semafori per ogni tipo di statistica
	private Map<TipoIntervalloStatistico, Semaphore> semaphores;
	private Map<TipoIntervalloStatistico, InfoStatistics> semaphoreStatistics;

	public StatisticheServerExecutor(StatisticheServerProperties serverProperties) {
		this.logCore = StatisticheServerLogger.getInstance().getLoggerServer();
		this.serverProperties = serverProperties;
		this.semaphores = new EnumMap<>(TipoIntervalloStatistico.class);
		this.semaphoreStatistics = new EnumMap<>(TipoIntervalloStatistico.class);
	}

	public void start() {
		this.logCore.info("Avvio scheduler statistiche...");

		// Thread pool con numero di thread pari al numero di timer abilitati
		int numTimers = countEnabledTimers();
		if(numTimers == 0) {
			this.logCore.warn("Nessun timer statistiche abilitato");
			return;
		}

		// Inizializza semafori se abilitati
		if(this.serverProperties.isSemaphoreEnabled()) {
			initSemaphores();
		}

		this.scheduler = Executors.newScheduledThreadPool(numTimers);

		// Avvia i timer abilitati
		if(this.serverProperties.isTimerStatisticheOrarieEnabled()) {
			this.timerStatisticheOrarie = scheduleTimer(
				TipoIntervalloStatistico.STATISTICHE_ORARIE,
				this.serverProperties.getTimerStatisticheOrarieDelayInizialeSecondi(),
				this.serverProperties.getTimerStatisticheOrarieIntervalloSecondi()
			);
		}

		if(this.serverProperties.isTimerStatisticheGiornaliereEnabled()) {
			this.timerStatisticheGiornaliere = scheduleTimer(
				TipoIntervalloStatistico.STATISTICHE_GIORNALIERE,
				this.serverProperties.getTimerStatisticheGiornaliereDelayInizialeSecondi(),
				this.serverProperties.getTimerStatisticheGiornaliereIntervalloSecondi()
			);
		}

		if(this.serverProperties.isTimerStatisticheSettimanaliEnabled()) {
			this.timerStatisticheSettimanali = scheduleTimer(
				TipoIntervalloStatistico.STATISTICHE_SETTIMANALI,
				this.serverProperties.getTimerStatisticheSettimanaliDelayInizialeSecondi(),
				this.serverProperties.getTimerStatisticheSettimanaliIntervalloSecondi()
			);
		}

		if(this.serverProperties.isTimerStatisticheMensiliEnabled()) {
			this.timerStatisticheMensili = scheduleTimer(
				TipoIntervalloStatistico.STATISTICHE_MENSILI,
				this.serverProperties.getTimerStatisticheMensiliDelayInizialeSecondi(),
				this.serverProperties.getTimerStatisticheMensiliIntervalloSecondi()
			);
		}

		if(this.serverProperties.isTimerPdndGenerazioneEnabled()) {
			this.timerPdndGenerazione = scheduleTimer(
				TipoIntervalloStatistico.PDND_GENERAZIONE_TRACCIAMENTO,
				this.serverProperties.getTimerPdndGenerazioneDelayInizialeSecondi(),
				this.serverProperties.getTimerPdndGenerazioneIntervalloSecondi()
			);
		}

		if(this.serverProperties.isTimerPdndPubblicazioneEnabled()) {
			this.timerPdndPubblicazione = scheduleTimer(
				TipoIntervalloStatistico.PDND_PUBBLICAZIONE_TRACCIAMENTO,
				this.serverProperties.getTimerPdndPubblicazioneDelayInizialeSecondi(),
				this.serverProperties.getTimerPdndPubblicazioneIntervalloSecondi()
			);
		}

		this.logCore.info("Scheduler statistiche avviato con " + numTimers + " timer");
	}

	public void stop() {
		this.logCore.info("Arresto scheduler statistiche...");

		if(this.scheduler != null) {
			// Cancella tutti i timer
			cancelTimer(this.timerStatisticheOrarie, "statistiche orarie");
			cancelTimer(this.timerStatisticheGiornaliere, "statistiche giornaliere");
			cancelTimer(this.timerStatisticheSettimanali, "statistiche settimanali");
			cancelTimer(this.timerStatisticheMensili, "statistiche mensili");
			cancelTimer(this.timerPdndGenerazione, "PDND generazione");
			cancelTimer(this.timerPdndPubblicazione, "PDND pubblicazione");

			// Shutdown dello scheduler
			this.scheduler.shutdown();
			try {
				if(!this.scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
					this.logCore.warn("Timeout attesa terminazione scheduler, forzo shutdown...");
					this.scheduler.shutdownNow();
					if(!this.scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
						this.logCore.error("Scheduler non terminato correttamente");
					}
				}
			} catch (InterruptedException e) {
				this.logCore.warn("Interruzione durante attesa terminazione scheduler");
				this.scheduler.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}

		this.logCore.info("Scheduler statistiche arrestato");
	}

	private int countEnabledTimers() {
		int count = 0;
		if(this.serverProperties.isTimerStatisticheOrarieEnabled()) count++;
		if(this.serverProperties.isTimerStatisticheGiornaliereEnabled()) count++;
		if(this.serverProperties.isTimerStatisticheSettimanaliEnabled()) count++;
		if(this.serverProperties.isTimerStatisticheMensiliEnabled()) count++;
		if(this.serverProperties.isTimerPdndGenerazioneEnabled()) count++;
		if(this.serverProperties.isTimerPdndPubblicazioneEnabled()) count++;
		return count;
	}

	private void initSemaphores() {
		this.logCore.info("Inizializzazione semafori per lock distribuito...");
		try {
			TipiDatabase databaseType = TipiDatabase.toEnumConstant(this.serverProperties.getSemaphoreDatabaseType());

			// Crea configurazione comune (allineata con GestoreMessaggi.newSemaphoreConfiguration)
			SemaphoreConfiguration config = new SemaphoreConfiguration();
			config.setIdNode(this.serverProperties.getSemaphoreNodeId());
			config.setMaxLife(this.serverProperties.getSemaphoreLockMaxLife());
			config.setMaxIdleTime(this.serverProperties.getSemaphoreLockIdleTime());
			config.setSerializableTimeWaitMs(this.serverProperties.getSemaphoreSerializableTimeWaitMs());
			config.setSerializableNextIntervalTimeMs(this.serverProperties.getSemaphoreSerializableNextIntervalTimeMs());
			config.setEmitEvent(false);
			config.setSerializableLevel(false); // come in GestoreMessaggi

			// Crea un semaforo per ogni tipo di statistica abilitata
			for(TipoIntervalloStatistico tipo : TipoIntervalloStatistico.values()) {
				if(isTimerEnabledForType(tipo)) {
					// Usa gli stessi ID lock della PDD (TipoLock.getLockStatistico)
					String lockId = getLockIdForStatistica(tipo);
					InfoStatistics infoStats = new InfoStatistics();
					Semaphore semaphore = new Semaphore(infoStats, SemaphoreMapping.newInstance(lockId),
							config, databaseType, this.logCore);
					this.semaphores.put(tipo, semaphore);
					this.semaphoreStatistics.put(tipo, infoStats);
					this.logCore.info("Semaforo creato per {} con lockId={}", tipo.getValue(), lockId);
				}
			}
			this.logCore.info("Inizializzazione semafori completata");
		} catch(Exception e) {
			this.logCore.error("Errore durante inizializzazione semafori: " + e.getMessage(), e);
			// Disabilita semafori in caso di errore
			this.semaphores.clear();
			this.semaphoreStatistics.clear();
		}
	}

	/**
	 * Genera l'ID del lock per il tipo di statistica.
	 * IMPORTANTE: Deve essere identico a TipoLock.getLockStatistico() della PDD
	 * per garantire il lock distribuito tra PDD e WAR statistiche.
	 */
	private String getLockIdForStatistica(TipoIntervalloStatistico tipo) {
		if(TipoIntervalloStatistico.PDND_GENERAZIONE_TRACCIAMENTO.equals(tipo) ||
		   TipoIntervalloStatistico.PDND_PUBBLICAZIONE_TRACCIAMENTO.equals(tipo)) {
			return tipo.getValue();
		}
		return "Generazione" + tipo.getValue();
	}

	private boolean isTimerEnabledForType(TipoIntervalloStatistico tipo) {
		switch(tipo) {
		case STATISTICHE_ORARIE:
			return this.serverProperties.isTimerStatisticheOrarieEnabled();
		case STATISTICHE_GIORNALIERE:
			return this.serverProperties.isTimerStatisticheGiornaliereEnabled();
		case STATISTICHE_SETTIMANALI:
			return this.serverProperties.isTimerStatisticheSettimanaliEnabled();
		case STATISTICHE_MENSILI:
			return this.serverProperties.isTimerStatisticheMensiliEnabled();
		case PDND_GENERAZIONE_TRACCIAMENTO:
			return this.serverProperties.isTimerPdndGenerazioneEnabled();
		case PDND_PUBBLICAZIONE_TRACCIAMENTO:
			return this.serverProperties.isTimerPdndPubblicazioneEnabled();
		default:
			return false;
		}
	}

	private ScheduledFuture<?> scheduleTimer(TipoIntervalloStatistico tipo, long delayMinuti, long intervalloMinuti) {
		this.logCore.info("Scheduling timer {} - delay iniziale: {} secondi, intervallo: {} secondi",
			tipo.getValue(), delayMinuti, intervalloMinuti);

		return this.scheduler.scheduleAtFixedRate(
			() -> eseguiJob(tipo),
			delayMinuti,
			intervalloMinuti,
			TimeUnit.SECONDS
		);
	}

	private void cancelTimer(ScheduledFuture<?> timer, String nome) {
		if(timer != null && !timer.isCancelled()) {
			timer.cancel(false);
			this.logCore.debug("Timer {} cancellato", nome);
		}
	}

	private void eseguiJob(TipoIntervalloStatistico tipoStatistica) {
		String tipoStr = tipoStatistica.getValue();

		// Ottieni istanza singleton del logger
		StatisticheServerLogger serverLogger = StatisticheServerLogger.getInstance();

		// Logger del server per messaggi generali
		this.logCore.info("Avvio job statistiche: {}", tipoStr);


		StatisticsLibrary sLibrary = null;
		Connection connection = null;
		StatisticheServerDBManager dbManager = null;
		boolean lockAcquired = false;
		Semaphore semaphore = this.semaphores.get(tipoStatistica);

		try {
			boolean debug = this.serverProperties.isStatisticheGenerazioneDebug();
			Logger logStatistica = serverLogger.getLoggerStatistiche(tipoStatistica, debug);
			Logger logStatisticaSql = serverLogger.getLoggerStatisticheSql(tipoStatistica, debug);
			
			// Crea configurazione statistiche con i logger specifici
			StatisticsConfig statisticsConfig = createStatisticsConfig(tipoStatistica, logStatistica, logStatisticaSql);

			// Verifica se il job è abilitato (per PDND verifica protocollo ModI)
			if(!isJobEnabled(tipoStatistica, statisticsConfig)) {
				this.logCore.info("Job {} non abilitato, skip", tipoStr);
				return;
			}

			// Ottiene connessione dal DBManager
			dbManager = StatisticheServerDBManager.getInstance(logStatisticaSql);
			connection = dbManager.getConnection();
			if(connection == null) {
				throw new Exception("Connessione al database non disponibile");
			}

			// Acquisizione lock se semaforo abilitato
			if(semaphore != null) {
				connection.setAutoCommit(true);
				lockAcquired = acquireLock(semaphore, connection, tipoStr);
				if(!lockAcquired) {
					this.logCore.info("Lock non acquisito per {}, job già in esecuzione su altro nodo", tipoStr);
					return;
				}
				logStatistica.debug("Lock acquisito per {}", tipoStr);
			}

			// Crea ServiceManagers passando la connessione
			DAOFactory daoFactory = DAOFactory.getInstance(logStatisticaSql);
			DAOFactoryProperties daoFactoryProperties = DAOFactoryProperties.getInstance(logStatisticaSql);

			ServiceManagerProperties smPropertiesStatistiche =
				daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance());
			smPropertiesStatistiche.setShowSql(this.serverProperties.isStatisticheGenerazioneDebug());

			ServiceManagerProperties smPropertiesTransazioni =
				daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
			smPropertiesTransazioni.setShowSql(this.serverProperties.isStatisticheGenerazioneDebug());

			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM =
				(org.openspcoop2.core.statistiche.dao.IServiceManager)
					daoFactory.getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),
						connection, smPropertiesStatistiche, logStatisticaSql);

			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM =
				(org.openspcoop2.core.transazioni.dao.IServiceManager)
					daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(),
						connection, smPropertiesTransazioni, logStatisticaSql);

			org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM = null;
			org.openspcoop2.core.plugins.dao.IServiceManager pluginsBaseSM = null;
			org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM = null;
			org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM = null;

			if(this.serverProperties.isGenerazioneStatisticheCustom()
					|| statisticsConfig.isPdndTracciamentoGenerazione()
					|| statisticsConfig.isPdndTracciamentoPubblicazione()) {

				ServiceManagerProperties smPropertiesPluginsStatistiche =
					daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.monitor.engine.config.statistiche.utils.ProjectInfo.getInstance());
				smPropertiesPluginsStatistiche.setShowSql(this.serverProperties.isStatisticheGenerazioneDebug());

				ServiceManagerProperties smPropertiesPluginsBase =
					daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance());
				smPropertiesPluginsBase.setShowSql(this.serverProperties.isStatisticheGenerazioneDebug());

				ServiceManagerProperties smPropertiesUtils =
					daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
				smPropertiesUtils.setShowSql(this.serverProperties.isStatisticheGenerazioneDebug());

				pluginsStatisticheSM = (org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager)
					daoFactory.getServiceManager(
						org.openspcoop2.monitor.engine.config.statistiche.utils.ProjectInfo.getInstance(),
							connection, smPropertiesPluginsStatistiche, logStatisticaSql);

				pluginsBaseSM = (org.openspcoop2.core.plugins.dao.IServiceManager)
					daoFactory.getServiceManager(
						org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),
							connection, smPropertiesPluginsBase, logStatisticaSql);

				utilsSM = (org.openspcoop2.core.commons.search.dao.IServiceManager)
					daoFactory.getServiceManager(
						org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(),
							connection, smPropertiesUtils, logStatisticaSql);

				if(this.serverProperties.isAnalisiTransazioniCustom()) {
					ServiceManagerProperties smPropertiesPluginsTransazioni =
						daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance());
					smPropertiesPluginsTransazioni.setShowSql(this.serverProperties.isStatisticheGenerazioneDebug());

					pluginsTransazioniSM = (org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager)
						daoFactory.getServiceManager(
							org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance(),
								connection, smPropertiesPluginsTransazioni, logStatisticaSql);
				}
			}

			sLibrary = new StatisticsLibrary(statisticsConfig, statisticheSM, transazioniSM,
					pluginsStatisticheSM, pluginsBaseSM, utilsSM, pluginsTransazioniSM);

			// Esegui generazione
			switch (tipoStatistica) {
			case STATISTICHE_ORARIE:
				sLibrary.generateStatisticaOraria();
				break;
			case STATISTICHE_GIORNALIERE:
				sLibrary.generateStatisticaGiornaliera();
				break;
			case STATISTICHE_SETTIMANALI:
				sLibrary.generateStatisticaSettimanale();
				break;
			case STATISTICHE_MENSILI:
				sLibrary.generateStatisticaMensile();
				break;
			case PDND_GENERAZIONE_TRACCIAMENTO:
				sLibrary.generatePdndGenerazioneTracciamento();
				break;
			case PDND_PUBBLICAZIONE_TRACCIAMENTO:
				sLibrary.generatePdndPubblicazioneTracciamento();
				break;
			}

			this.logCore.info("Job statistiche {} completato con successo", tipoStr);

		} catch(Exception e) {
			Logger logStatisticaError = serverLogger.getLoggerStatistiche(tipoStatistica, false);
			logStatisticaError.error("Errore durante esecuzione job statistiche " + tipoStr + ": " + e.getMessage(), e);
		} finally {
			// Rilascia lock
			if(lockAcquired && semaphore != null && connection != null) {
				try {
					releaseLock(semaphore, connection, tipoStr);
					this.logCore.debug("Lock rilasciato per {}", tipoStr);
				} catch(Exception e) {
					Logger logStatisticaError = serverLogger.getLoggerStatistiche(tipoStatistica, false);
					logStatisticaError.error("Errore durante rilascio lock per " + tipoStr + ": " + e.getMessage(), e);
				}
			}
			// Rilascia connessione tramite DBManager
			if(dbManager != null && connection != null) {
				dbManager.releaseConnection(connection);
			}
			// Chiudi library
			if(sLibrary != null) {
				sLibrary.close();
			}
		}
	}

	private boolean acquireLock(Semaphore semaphore, Connection connection, String details) {
		try {
			long startTime = System.currentTimeMillis();
			long timeout = this.serverProperties.getSemaphoreLockAttesaAttiva();
			int checkInterval = this.serverProperties.getSemaphoreLockCheckInterval();

			while(System.currentTimeMillis() - startTime < timeout) {
				boolean acquired = semaphore.newLock(connection, details);
				if(acquired) {
					return true;
				}
				// Attendi prima di riprovare
				Thread.sleep(checkInterval);
			}
			return false;
		} catch(InterruptedException e) {
			Thread.interrupted();
			this.logCore.error("Errore durante acquisizione lock: " + e.getMessage(), e);
			return false;
		} catch (UtilsException e) {
			return false;
		}
	}

	private void releaseLock(Semaphore semaphore, Connection connection, String details) {
		try {
			semaphore.releaseLock(connection, details);
		} catch(Exception e) {
			this.logCore.error("Errore durante rilascio lock: " + e.getMessage(), e);
		}
	}

	private StatisticsConfig createStatisticsConfig(TipoIntervalloStatistico tipoStatistica,
			Logger logStatistica, Logger logStatisticaSql) throws Exception {
		StatisticsConfig statisticsConfig = new StatisticsConfig(false);

		statisticsConfig.setLogCore(logStatistica);
		statisticsConfig.setLogSql(logStatisticaSql);
		statisticsConfig.setPdndTracciamentoRequestConfig(this.serverProperties.getPdndTracingRequestConfig());
		statisticsConfig.setPdndTracciamentoSoggettiEnabled(this.serverProperties.getPdndTracingSoggettiEnabled());
		statisticsConfig.setPdndTracciamentoSoggettiDisabled(this.serverProperties.isPdndTracingSoggettiDisabled());
		statisticsConfig.setGenerazioneStatisticheCustom(this.serverProperties.isGenerazioneStatisticheCustom());
		statisticsConfig.setAnalisiTransazioniCustom(this.serverProperties.isAnalisiTransazioniCustom());
		statisticsConfig.setDebug(this.serverProperties.isStatisticheGenerazioneDebug());
		statisticsConfig.setUseUnionForLatency(this.serverProperties.isGenerazioneStatisticheUseUnionForLatency());
		statisticsConfig.setPdndTracciamentoFruizioniEnabled(this.serverProperties.isPdndTracingFruizioniEnabled());
		statisticsConfig.setPdndTracciamentoErogazioniEnabled(this.serverProperties.isPdndTracingErogazioniEnabled());
		statisticsConfig.setPdndTracciamentoMaxAttempt(this.serverProperties.getPdndTracingMaxAttempt());
		statisticsConfig.setPdndTracciamentoPendingCheck(this.serverProperties.getPdndTracingPendingCheck());
		statisticsConfig.setPdndTracciamentoGenerazioneDelayMinutes(this.serverProperties.getPdndTracingGenerazioneDelayMinutes());

		switch (tipoStatistica) {
		case STATISTICHE_ORARIE:
			statisticsConfig.setStatisticheOrarie(true);
			statisticsConfig.setStatisticheOrarieGestioneUltimoIntervallo(
				this.serverProperties.isStatisticheGenerazioneBaseOrariaGestioneUltimaOra());
			break;
		case STATISTICHE_GIORNALIERE:
			statisticsConfig.setStatisticheGiornaliere(true);
			statisticsConfig.setStatisticheGiornaliereGestioneUltimoIntervallo(
				this.serverProperties.isStatisticheGenerazioneBaseGiornalieraGestioneUltimoGiorno());
			break;
		case STATISTICHE_SETTIMANALI:
			statisticsConfig.setStatisticheSettimanali(true);
			statisticsConfig.setStatisticheSettimanaliGestioneUltimoIntervallo(
				this.serverProperties.isStatisticheGenerazioneBaseSettimanaleGestioneUltimaSettimana());
			break;
		case STATISTICHE_MENSILI:
			statisticsConfig.setStatisticheMensili(true);
			statisticsConfig.setStatisticheMensiliGestioneUltimoIntervallo(
				this.serverProperties.isStatisticheGenerazioneBaseMensileGestioneUltimoMese());
			break;
		case PDND_GENERAZIONE_TRACCIAMENTO:
			statisticsConfig.setPdndTracciamentoGenerazione(true);
			break;
		case PDND_PUBBLICAZIONE_TRACCIAMENTO:
			statisticsConfig.setPdndTracciamentoPubblicazione(true);
			break;
		}

		statisticsConfig.setWaitMsBeforeNextInterval(this.serverProperties.getGenerazioneTradeOffMs());
		statisticsConfig.setWaitStatiInConsegna(this.serverProperties.isGenerazioneAttendiCompletamentoTransazioniInFasiIntermedie());
		statisticsConfig.setForceIndexConfig(this.serverProperties.getStatisticheGenerazioneForceIndexConfig());
		statisticsConfig.setGroupByConfig(this.serverProperties.getStatisticheGenerazioneGroupByConfig());

		return statisticsConfig;
	}

	private boolean isJobEnabled(TipoIntervalloStatistico tipoStatistica, StatisticsConfig statisticsConfig) throws Exception {
		switch (tipoStatistica) {
		case PDND_GENERAZIONE_TRACCIAMENTO:
		case PDND_PUBBLICAZIONE_TRACCIAMENTO:
			// Per PDND verifica che il protocollo ModI sia disponibile e il tracing abilitato
			return ProtocolFactoryManager.getInstance().existsProtocolFactory(Costanti.MODIPA_PROTOCOL_NAME)
					&& ModIUtils.isTracingPDNDEnabled();
		default:
			return true;
		}
	}
}
