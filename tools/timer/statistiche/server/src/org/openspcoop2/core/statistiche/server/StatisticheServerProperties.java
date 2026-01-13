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

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.PropertiesEnvUtils;
import org.openspcoop2.monitor.engine.statistic.StatisticsConfig;
import org.openspcoop2.monitor.engine.statistic.StatisticsForceIndexConfig;
import org.openspcoop2.monitor.engine.statistic.StatisticsGroupByConfig;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.PropertiesReader;
import org.openspcoop2.utils.transport.http.HttpRequestConfig;
import org.slf4j.Logger;

/**
 * StatisticheServerProperties
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticheServerProperties {

	private static StatisticheServerProperties staticInstance = null;
	private static synchronized void init(Logger log) throws UtilsException {
		if(StatisticheServerProperties.staticInstance == null) {
			StatisticheServerProperties.staticInstance = new StatisticheServerProperties(log);
		}
	}
	public static StatisticheServerProperties getInstance(Logger log) throws UtilsException {
		if(StatisticheServerProperties.staticInstance == null) {
			synchronized (StatisticheServerProperties.class) {
				StatisticheServerProperties.init(log);
			}
		}
		return StatisticheServerProperties.staticInstance;
	}
	public static StatisticheServerProperties getInstance() throws UtilsException {
		if(StatisticheServerProperties.staticInstance == null) {
			throw new UtilsException("StatisticheServerProperties non inizializzato");
		}
		return StatisticheServerProperties.staticInstance;
	}


	private static final String PROPERTIES_FILE = "/statistiche-server.properties";

	private String protocolloDefault = null;

	private volatile boolean statisticheGenerazioneBaseOrariaGestioneUltimaOra = false;
	private volatile boolean statisticheGenerazioneBaseGiornalieraGestioneUltimoGiorno = false;
	private volatile boolean statisticheGenerazioneBaseSettimanaleGestioneUltimaSettimana = false;
	private volatile boolean statisticheGenerazioneBaseMensileGestioneUltimoMese = false;

	private StatisticsForceIndexConfig statisticheGenerazioneForceIndexConfig = null;

	private volatile long waitMsBeforeNextInterval = -1;

	private volatile boolean waitStatiInConsegna = false;

	private volatile boolean generazioneStatisticheUseUnionForLatency = true;

	private volatile boolean generazioneStatisticheCustom = false;
	private volatile boolean analisiTransazioniCustom = false;

	private File pddMonitorFrameworkRepositoryJars = null;

	private HttpRequestConfig pdndTracingRequestConfig;
	private volatile boolean pdndTracingSoggettiDisabled = false;
	private Set<String> pdndTracingSoggettiEnabled;
	private Integer pdndTracingMaxAttempt = null;
	private volatile boolean pdndTracingErogazioniEnabled = true;
	private volatile boolean pdndTracingFruizioniEnabled = true;
	private List<Integer> pdndTracingPendingCheck = null;
	private volatile int pdndTracingGenerazioneDelayMinutes = 0;

	// Configurazione scheduling
	private volatile boolean timerStatisticheOrarieEnabled = true;
	private volatile boolean timerStatisticheGiornaliereEnabled = true;
	private volatile boolean timerStatisticheSettimanaliEnabled = true;
	private volatile boolean timerStatisticheMensiliEnabled = true;
	private volatile boolean timerPdndGenerazioneEnabled = false;
	private volatile boolean timerPdndPubblicazioneEnabled = false;

	private volatile long timerStatisticheOrarieIntervalloMinuti = 60;
	private volatile long timerStatisticheGiornaliereIntervalloMinuti = 1440; // 24 ore
	private volatile long timerStatisticheSettimanaliIntervalloMinuti = 10080; // 7 giorni
	private volatile long timerStatisticheMensiliIntervalloMinuti = 43200; // 30 giorni
	private volatile long timerPdndGenerazioneIntervalloMinuti = 60;
	private volatile long timerPdndPubblicazioneIntervalloMinuti = 60;

	private volatile long timerStatisticheOrarieDelayInizialeMinuti = 5;
	private volatile long timerStatisticheGiornaliereDelayInizialeMinuti = 10;
	private volatile long timerStatisticheSettimanaliDelayInizialeMinuti = 15;
	private volatile long timerStatisticheMensiliDelayInizialeMinuti = 20;
	private volatile long timerPdndGenerazioneDelayInizialeMinuti = 5;
	private volatile long timerPdndPubblicazioneDelayInizialeMinuti = 10;

	// Configurazione Semaforo/Lock
	private volatile boolean semaphoreEnabled = true;
	private String semaphoreNodeId = null;
	private String semaphoreDatabaseType = null;
	private volatile long semaphoreLockMaxLife = 30000; // 30 secondi
	private volatile long semaphoreLockIdleTime = 30000; // 30 secondi
	private volatile long semaphoreSerializableTimeWaitMs = 60000; // 60 secondi
	private volatile int semaphoreSerializableNextIntervalTimeMs = 100;
	private volatile long semaphoreLockAttesaAttiva = 60000; // 60 secondi
	private volatile int semaphoreLockCheckInterval = 500; // 500 ms

	private PropertiesReader props;


	private StatisticheServerProperties(Logger log) throws UtilsException {

		Properties pr = new Properties();
		try {
			InputStream is = StatisticheServerProperties.class.getResourceAsStream(StatisticheServerProperties.PROPERTIES_FILE);
			if(is == null) {
				throw new UtilsException("File '"+PROPERTIES_FILE+"' not found in classpath");
			}
			pr.load(is);
		} catch(UtilsException e) {
			throw e;
		} catch(Exception e) {
			throw new UtilsException("Errore durante l'init delle properties: "+e.getMessage(), e);
		}
		this.props = new PropertiesReader(pr, true);

		try {
			PropertiesEnvUtils.checkRequiredEnvProperties(this.props.readProperties("env."), log, "statistiche-server");
		} catch(Exception e) {
			throw new UtilsException("Errore durante l'init delle properties: "+e.getMessage(), e);
		}

		try {
			this.statisticheGenerazioneForceIndexConfig = new StatisticsForceIndexConfig(pr);
		} catch(Exception e) {
			throw new UtilsException(e.getMessage(), e);
		}
	}

	public void initProperties() throws UtilsException {

		this.protocolloDefault = this.getProperty("protocolloDefault", true);

		this.statisticheGenerazioneBaseOrariaGestioneUltimaOra = this.getBooleanProperty("statistiche.generazione.baseOraria.gestioneUltimaOra", true);
		this.statisticheGenerazioneBaseGiornalieraGestioneUltimoGiorno = this.getBooleanProperty("statistiche.generazione.baseGiornaliera.gestioneUltimoGiorno", true);
		this.statisticheGenerazioneBaseSettimanaleGestioneUltimaSettimana = this.getBooleanProperty("statistiche.generazione.baseSettimanale.gestioneUltimaSettimana", true);
		this.statisticheGenerazioneBaseMensileGestioneUltimoMese = this.getBooleanProperty("statistiche.generazione.baseMensile.gestioneUltimoMese", true);

		String p = this.getProperty("statistiche.generazione.tradeOffSeconds", false);
		this.waitMsBeforeNextInterval = p != null ? Long.parseLong(p) : -1l;

		this.waitStatiInConsegna = this.getBooleanProperty("statistiche.generazione.attendiCompletamentoTransazioniInFasiIntermedie", false);

		this.generazioneStatisticheUseUnionForLatency = this.getBooleanProperty("statistiche.generazione.useUnionForLatency", true);

		this.generazioneStatisticheCustom = this.getBooleanProperty("statistiche.generazione.custom.enabled", true);
		this.analisiTransazioniCustom = this.getBooleanProperty("statistiche.generazione.custom.transazioniSdk.enabled", true);

		String tmp = this.getProperty("statistiche.pddmonitorframework.sdk.repositoryJars", false);
		if(tmp != null) {
			this.pddMonitorFrameworkRepositoryJars = new File(tmp);
		}

		this.pdndTracingRequestConfig = new HttpRequestConfig("statistiche.pdnd.tracciamento", this.props);

		String value = getProperty("statistiche.pdnd.tracciamento.soggetti.enabled", false);
		if (value == null || StringUtils.isEmpty(value.trim())) {
			this.pdndTracingSoggettiDisabled = true;
			this.pdndTracingSoggettiEnabled = Set.of();
		}
		else if("*".equals(value)) {
			this.pdndTracingSoggettiEnabled = Set.of();
		}
		else {
			this.pdndTracingSoggettiEnabled = Arrays.stream(value.split(","))
				.map(String::trim)
				.collect(Collectors.toSet());
		}
		value = getProperty("statistiche.pdnd.tracciamento.maxAttempts", false);
		if (value != null) {
			this.pdndTracingMaxAttempt = Integer.valueOf(value);
		}

		value = getProperty("statistiche.pdnd.tracciamento.erogazioni.enabled", false);
		if (Boolean.FALSE.toString().equals(value)) {
			this.pdndTracingErogazioniEnabled = false;
		}

		value = getProperty("statistiche.pdnd.tracciamento.fruizioni.enabled", false);
		if (Boolean.FALSE.toString().equals(value)) {
			this.pdndTracingFruizioniEnabled = false;
		}

		value = getProperty("statistiche.pdnd.tracciamento.pending.check", false);
		this.pdndTracingPendingCheck = (value == null || StringUtils.isEmpty(value.trim())) ? List.of(0) : Arrays.stream(value.split(","))
				.map(Integer::valueOf)
				.collect(Collectors.toList());

		value = getProperty("statistiche.pdnd.tracciamento.generazione.delayMinutes", false);
		if(value != null && StringUtils.isNotEmpty(value.trim())) {
			this.pdndTracingGenerazioneDelayMinutes = Integer.parseInt(value);
		}

		// Timer configuration
		this.timerStatisticheOrarieEnabled = this.getBooleanProperty("timer.statistiche.orarie.enabled", false);
		this.timerStatisticheGiornaliereEnabled = this.getBooleanProperty("timer.statistiche.giornaliere.enabled", false);
		this.timerStatisticheSettimanaliEnabled = this.getBooleanProperty("timer.statistiche.settimanali.enabled", false);
		this.timerStatisticheMensiliEnabled = this.getBooleanProperty("timer.statistiche.mensili.enabled", false);
		this.timerPdndGenerazioneEnabled = this.getBooleanProperty("timer.pdnd.generazione.enabled", false);
		this.timerPdndPubblicazioneEnabled = this.getBooleanProperty("timer.pdnd.pubblicazione.enabled", false);

		this.timerStatisticheOrarieIntervalloMinuti = this.getLongProperty("timer.statistiche.orarie.intervalloMinuti", false, 60);
		this.timerStatisticheGiornaliereIntervalloMinuti = this.getLongProperty("timer.statistiche.giornaliere.intervalloMinuti", false, 1440);
		this.timerStatisticheSettimanaliIntervalloMinuti = this.getLongProperty("timer.statistiche.settimanali.intervalloMinuti", false, 10080);
		this.timerStatisticheMensiliIntervalloMinuti = this.getLongProperty("timer.statistiche.mensili.intervalloMinuti", false, 43200);
		this.timerPdndGenerazioneIntervalloMinuti = this.getLongProperty("timer.pdnd.generazione.intervalloMinuti", false, 60);
		this.timerPdndPubblicazioneIntervalloMinuti = this.getLongProperty("timer.pdnd.pubblicazione.intervalloMinuti", false, 60);

		this.timerStatisticheOrarieDelayInizialeMinuti = this.getLongProperty("timer.statistiche.orarie.delayInizialeMinuti", false, 5);
		this.timerStatisticheGiornaliereDelayInizialeMinuti = this.getLongProperty("timer.statistiche.giornaliere.delayInizialeMinuti", false, 10);
		this.timerStatisticheSettimanaliDelayInizialeMinuti = this.getLongProperty("timer.statistiche.settimanali.delayInizialeMinuti", false, 15);
		this.timerStatisticheMensiliDelayInizialeMinuti = this.getLongProperty("timer.statistiche.mensili.delayInizialeMinuti", false, 20);
		this.timerPdndGenerazioneDelayInizialeMinuti = this.getLongProperty("timer.pdnd.generazione.delayInizialeMinuti", false, 5);
		this.timerPdndPubblicazioneDelayInizialeMinuti = this.getLongProperty("timer.pdnd.pubblicazione.delayInizialeMinuti", false, 10);

		// Semaphore configuration
		this.semaphoreEnabled = this.getBooleanProperty("semaphore.enabled", false);
		this.semaphoreNodeId = this.getProperty("semaphore.nodeId", false);
		if(this.semaphoreNodeId == null) {
			// Default: hostname
			try {
				this.semaphoreNodeId = java.net.InetAddress.getLocalHost().getHostName();
			} catch(Exception e) {
				this.semaphoreNodeId = "node-" + System.currentTimeMillis();
			}
		}
		this.semaphoreDatabaseType = this.getProperty("semaphore.databaseType", this.semaphoreEnabled);
		this.semaphoreLockMaxLife = this.getLongProperty("semaphore.lock.maxLife", false, 30000);
		this.semaphoreLockIdleTime = this.getLongProperty("semaphore.lock.idleTime", false, 30000);
		this.semaphoreSerializableTimeWaitMs = this.getLongProperty("semaphore.serializable.timeWaitMs", false, 60000);
		this.semaphoreSerializableNextIntervalTimeMs = (int) this.getLongProperty("semaphore.serializable.nextIntervalTimeMs", false, 100);
		this.semaphoreLockAttesaAttiva = this.getLongProperty("semaphore.lock.attesaAttiva", false, 60000);
		this.semaphoreLockCheckInterval = (int) this.getLongProperty("semaphore.lock.checkInterval", false, 500);
	}

	private String getProperty(String name, boolean required) throws UtilsException {
		String tmp = this.props.getValue_convertEnvProperties(name);
		if(tmp == null) {
			if(required) {
				throw new UtilsException("Property '"+name+"' not found");
			}
			else {
				return null;
			}
		}
		else {
			return tmp.trim();
		}
	}
	public String readProperty(boolean required, String property) throws UtilsException {
		return getProperty(property, required);
	}

	private boolean getBooleanProperty(String name, boolean required) throws UtilsException {
		String tmp = this.getProperty(name, required);
		if(tmp != null) {
			try {
				return Boolean.parseBoolean(tmp);
			} catch(Exception e) {
				throw new UtilsException("Property '"+name+"' wrong boolean format: "+e.getMessage());
			}
		}
		else {
			return false;
		}
	}

	private long getLongProperty(String name, boolean required, long defaultValue) throws UtilsException {
		String tmp = this.getProperty(name, required);
		if(tmp != null) {
			try {
				return Long.parseLong(tmp);
			} catch(Exception e) {
				throw new UtilsException("Property '"+name+"' wrong long format: "+e.getMessage());
			}
		}
		else {
			return defaultValue;
		}
	}

	private BooleanNullable readBooleanProperty(boolean required, String property) throws UtilsException {
		String tmp = this.getProperty(property, required);
		if(tmp == null && !required) {
			return BooleanNullable.NULL();
		}
		if(!"true".equalsIgnoreCase(tmp) && !"false".equalsIgnoreCase(tmp)) {
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (true/false expected)");
		}
		return Boolean.parseBoolean(tmp) ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
	}

	private boolean parse(BooleanNullable b, boolean defaultValue) {
		return (b != null && b.getValue() != null) ? b.getValue() : defaultValue;
	}


	// Getters

	public String getProtocolloDefault() {
		return this.protocolloDefault;
	}

	public boolean isStatisticheGenerazioneDebug() throws UtilsException {
		return this.getBooleanProperty("statistiche.generazione.debug", true);
	}

	public boolean isStatisticheGenerazioneBaseOrariaGestioneUltimaOra() {
		return this.statisticheGenerazioneBaseOrariaGestioneUltimaOra;
	}
	public boolean isStatisticheGenerazioneBaseGiornalieraGestioneUltimoGiorno() {
		return this.statisticheGenerazioneBaseGiornalieraGestioneUltimoGiorno;
	}
	public boolean isStatisticheGenerazioneBaseSettimanaleGestioneUltimaSettimana() {
		return this.statisticheGenerazioneBaseSettimanaleGestioneUltimaSettimana;
	}
	public boolean isStatisticheGenerazioneBaseMensileGestioneUltimoMese() {
		return this.statisticheGenerazioneBaseMensileGestioneUltimoMese;
	}

	public StatisticsForceIndexConfig getStatisticheGenerazioneForceIndexConfig() {
		return this.statisticheGenerazioneForceIndexConfig;
	}

	private StatisticsGroupByConfig statisticheGenerazioneGroupByConfig = null;
	public StatisticsGroupByConfig getStatisticheGenerazioneGroupByConfig() throws UtilsException {
		if(this.statisticheGenerazioneGroupByConfig == null) {
			this.statisticheGenerazioneGroupByConfig = StatisticsConfig.parseGroupByConfig(this.props, "statistiche.generazione.");
		}
		return this.statisticheGenerazioneGroupByConfig;
	}

	public boolean isGenerazioneStatisticheUseUnionForLatency() {
		return this.generazioneStatisticheUseUnionForLatency;
	}

	public boolean isGenerazioneStatisticheCustom() {
		return this.generazioneStatisticheCustom;
	}
	public boolean isAnalisiTransazioniCustom() {
		return this.analisiTransazioniCustom;
	}

	public File getPddMonitorFrameworkRepositoryJars() {
		return this.pddMonitorFrameworkRepositoryJars;
	}

	public long getGenerazioneTradeOffMs() {
		return this.waitMsBeforeNextInterval;
	}
	public boolean isGenerazioneAttendiCompletamentoTransazioniInFasiIntermedie() {
		return this.waitStatiInConsegna;
	}

	public boolean isSecurityLoadBouncyCastleProvider() throws UtilsException {
		BooleanNullable b = this.readBooleanProperty(false, "security.addBouncyCastleProvider");
		return parse(b, false);
	}


	public String getEnvMapConfig() throws UtilsException {
		return this.readProperty(false, "env.map.config");
	}
	public boolean isEnvMapConfigRequired() throws UtilsException {
		BooleanNullable b = this.readBooleanProperty(false, "env.map.required");
		return this.parse(b, false);
	}


	public String getHSMConfigurazione() throws UtilsException {
		return this.readProperty(false, "hsm.config");
	}
	public boolean isHSMRequired() throws UtilsException {
		BooleanNullable b = this.readBooleanProperty(false, "hsm.required");
		return this.parse(b, false);
	}
	public boolean isHSMKeyPasswordConfigurable() throws UtilsException {
		BooleanNullable b = this.readBooleanProperty(false, "hsm.keyPassword");
		return this.parse(b, false);
	}


	public String getBYOKConfigurazione() throws UtilsException {
		return this.readProperty(false, "byok.config");
	}
	public boolean isBYOKRequired() throws UtilsException {
		BooleanNullable b = this.readBooleanProperty(false, "byok.required");
		return parse(b, false);
	}
	public String getBYOKEnvSecretsConfig() throws UtilsException {
		return this.readProperty(false, "byok.env.secrets.config");
	}
	public boolean isBYOKEnvSecretsConfigRequired() throws UtilsException {
		BooleanNullable b = this.readBooleanProperty(false, "byok.env.secrets.required");
		return this.parse(b, false);
	}

	public HttpRequestConfig getPdndTracingRequestConfig() {
		return this.pdndTracingRequestConfig;
	}

	public Set<String> getPdndTracingSoggettiEnabled() {
		return this.pdndTracingSoggettiEnabled;
	}

	public boolean isPdndTracingSoggettiDisabled() {
		return this.pdndTracingSoggettiDisabled;
	}

	public boolean isPdndTracingErogazioniEnabled() {
		return this.pdndTracingErogazioniEnabled;
	}

	public boolean isPdndTracingFruizioniEnabled() {
		return this.pdndTracingFruizioniEnabled;
	}

	public Integer getPdndTracingMaxAttempt() {
		return this.pdndTracingMaxAttempt;
	}

	public List<Integer> getPdndTracingPendingCheck() {
		return this.pdndTracingPendingCheck;
	}

	public int getPdndTracingGenerazioneDelayMinutes() {
		return this.pdndTracingGenerazioneDelayMinutes;
	}

	// Timer getters

	public boolean isTimerStatisticheOrarieEnabled() {
		return this.timerStatisticheOrarieEnabled;
	}
	public boolean isTimerStatisticheGiornaliereEnabled() {
		return this.timerStatisticheGiornaliereEnabled;
	}
	public boolean isTimerStatisticheSettimanaliEnabled() {
		return this.timerStatisticheSettimanaliEnabled;
	}
	public boolean isTimerStatisticheMensiliEnabled() {
		return this.timerStatisticheMensiliEnabled;
	}
	public boolean isTimerPdndGenerazioneEnabled() {
		return this.timerPdndGenerazioneEnabled;
	}
	public boolean isTimerPdndPubblicazioneEnabled() {
		return this.timerPdndPubblicazioneEnabled;
	}

	public long getTimerStatisticheOrarieIntervalloMinuti() {
		return this.timerStatisticheOrarieIntervalloMinuti;
	}
	public long getTimerStatisticheGiornaliereIntervalloMinuti() {
		return this.timerStatisticheGiornaliereIntervalloMinuti;
	}
	public long getTimerStatisticheSettimanaliIntervalloMinuti() {
		return this.timerStatisticheSettimanaliIntervalloMinuti;
	}
	public long getTimerStatisticheMensiliIntervalloMinuti() {
		return this.timerStatisticheMensiliIntervalloMinuti;
	}
	public long getTimerPdndGenerazioneIntervalloMinuti() {
		return this.timerPdndGenerazioneIntervalloMinuti;
	}
	public long getTimerPdndPubblicazioneIntervalloMinuti() {
		return this.timerPdndPubblicazioneIntervalloMinuti;
	}

	public long getTimerStatisticheOrarieDelayInizialeMinuti() {
		return this.timerStatisticheOrarieDelayInizialeMinuti;
	}
	public long getTimerStatisticheGiornaliereDelayInizialeMinuti() {
		return this.timerStatisticheGiornaliereDelayInizialeMinuti;
	}
	public long getTimerStatisticheSettimanaliDelayInizialeMinuti() {
		return this.timerStatisticheSettimanaliDelayInizialeMinuti;
	}
	public long getTimerStatisticheMensiliDelayInizialeMinuti() {
		return this.timerStatisticheMensiliDelayInizialeMinuti;
	}
	public long getTimerPdndGenerazioneDelayInizialeMinuti() {
		return this.timerPdndGenerazioneDelayInizialeMinuti;
	}
	public long getTimerPdndPubblicazioneDelayInizialeMinuti() {
		return this.timerPdndPubblicazioneDelayInizialeMinuti;
	}

	// Semaphore getters

	public boolean isSemaphoreEnabled() {
		return this.semaphoreEnabled;
	}
	public String getSemaphoreNodeId() {
		return this.semaphoreNodeId;
	}
	public String getSemaphoreDatabaseType() {
		return this.semaphoreDatabaseType;
	}
	public long getSemaphoreLockMaxLife() {
		return this.semaphoreLockMaxLife;
	}
	public long getSemaphoreLockIdleTime() {
		return this.semaphoreLockIdleTime;
	}
	public long getSemaphoreSerializableTimeWaitMs() {
		return this.semaphoreSerializableTimeWaitMs;
	}
	public int getSemaphoreSerializableNextIntervalTimeMs() {
		return this.semaphoreSerializableNextIntervalTimeMs;
	}
	public long getSemaphoreLockAttesaAttiva() {
		return this.semaphoreLockAttesaAttiva;
	}
	public int getSemaphoreLockCheckInterval() {
		return this.semaphoreLockCheckInterval;
	}
}
