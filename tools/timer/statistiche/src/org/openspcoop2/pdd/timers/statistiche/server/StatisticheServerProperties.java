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
package org.openspcoop2.pdd.timers.statistiche.server;

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
		// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
		if(StatisticheServerProperties.staticInstance==null) {
			synchronized (StatisticheServerProperties.class) {
				throw new UtilsException("StatisticheServerProperties non inizializzato");
			}
		}
		return StatisticheServerProperties.staticInstance;
	}


	

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
	private volatile int pdndTracciamentoGenerazioneDbBatchSize = 200;
	private volatile int pdndTracciamentoPubblicazioneDbBatchSize = 200;
	
	// Configurazione scheduling
	private volatile boolean timerStatisticheOrarieEnabled = true;
	private volatile boolean timerStatisticheGiornaliereEnabled = true;
	private volatile boolean timerStatisticheSettimanaliEnabled = true;
	private volatile boolean timerStatisticheMensiliEnabled = true;
	private volatile boolean timerPdndGenerazioneEnabled = false;
	private volatile boolean timerPdndPubblicazioneEnabled = false;

	// Intervalli in secondi (allineati con GovWay)
	private volatile long timerStatisticheOrarieIntervalloSecondi = 300; // ogni 5 minuti
	private volatile long timerStatisticheGiornaliereIntervalloSecondi = 1800; // ogni 30 minuti
	private volatile long timerStatisticheSettimanaliIntervalloSecondi = 3600; // ogni ora
	private volatile long timerStatisticheMensiliIntervalloSecondi = 7200; // ogni 2 ore
	private volatile long timerPdndGenerazioneIntervalloSecondi = 3600; // ogni ora
	private volatile long timerPdndPubblicazioneIntervalloSecondi = 3600; // ogni ora

	private volatile long timerStatisticheOrarieDelayInizialeSecondi = 60;
	private volatile long timerStatisticheGiornaliereDelayInizialeSecondi = 120;
	private volatile long timerStatisticheSettimanaliDelayInizialeSecondi = 180;
	private volatile long timerStatisticheMensiliDelayInizialeSecondi = 240;
	private volatile long timerPdndGenerazioneDelayInizialeSecondi = 300;
	private volatile long timerPdndPubblicazioneDelayInizialeSecondi = 360;

	// Configurazione Semaforo/Lock (allineata con GovWay)
	// Le properties sono in SECONDI (tranne checkInterval e serializable che sono in ms)
	// ma i getter restituiscono MILLISECONDI dove necessario
	private volatile boolean semaphoreEnabled = true;
	private String semaphoreNodeId = null;
	private volatile long semaphoreLockMaxLifeSeconds = 7200; // 2 ore (come GovWay)
	private volatile long semaphoreLockIdleTimeSeconds = 600; // 10 minuti (come GovWay)
	private volatile long semaphoreSerializableTimeWaitMs = 60000; // 60 secondi
	private volatile int semaphoreSerializableNextIntervalTimeMs = 100;
	private volatile long semaphoreLockAttesaAttivaSeconds = 90; // 90 secondi (come GovWay)
	private volatile int semaphoreLockCheckIntervalMs = 50; // 50 ms (come GovWay)

	private StatisticheServerInstanceProperties props;
	private Properties rawProperties;
	private String confDirectory;
	
	private boolean jdbcCloseConnectionCheckIsClosed = false;
	private boolean jdbcCloseConnectionCheckAutocommit = false;


	private StatisticheServerProperties(Logger log) throws UtilsException {

		this.rawProperties = new Properties();
		try {
			InputStream is = StatisticheServerProperties.class.getResourceAsStream("/"+StatisticheCostanti.PROPERTIES_FILE);
			if(is == null) {
				throw new UtilsException("File '/"+StatisticheCostanti.PROPERTIES_FILE+"' not found in classpath");
			}
			this.rawProperties.load(is);
		} catch(UtilsException e) {
			throw e;
		} catch(Exception e) {
			throw new UtilsException("Errore durante l'init delle properties: "+e.getMessage(), e);
		}

		// Leggo la directory di configurazione (prima di creare InstanceProperties)
		this.confDirectory = this.rawProperties.getProperty("confDirectory");
		if(this.confDirectory != null) {
			this.confDirectory = this.confDirectory.trim();
			this.confDirectory = PropertiesEnvUtils.resolveGovWayEnvVariables(this.confDirectory);
		}

		// Creo InstanceProperties con supporto per file locali di override
		this.props = new StatisticheServerInstanceProperties(this.rawProperties, log, this.confDirectory);

		try {
			PropertiesEnvUtils.checkRequiredEnvProperties(this.props.readPropertiesConvertEnvProperties("env."), log, "statistiche-server");
		} catch(Exception e) {
			throw new UtilsException("Errore durante l'init delle properties: "+e.getMessage(), e);
		}

		try {
			this.statisticheGenerazioneForceIndexConfig = new StatisticsForceIndexConfig(this.rawProperties);
		} catch(Exception e) {
			throw new UtilsException(e.getMessage(), e);
		}
		
		BooleanNullable b = this.readBooleanProperty(true, "jdbc.closeConnection.checkIsClosed");
		this.jdbcCloseConnectionCheckIsClosed = this.parse(b, true);
		
		b = this.readBooleanProperty(true, "jdbc.closeConnection.checkAutocommit");
		this.jdbcCloseConnectionCheckAutocommit = this.parse(b, true);
	}

	public String getConfDirectory() {
		return this.confDirectory;
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

		this.pdndTracingRequestConfig = new HttpRequestConfig("statistiche.pdnd.tracciamento", key -> {
			try {
				return this.props.getValueConvertEnvProperties(key);
			} catch (UtilsException e) {
				return null;
			}
		});

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
		
		value = getProperty("statistiche.pdnd.tracciamento.generazione.db.batchSize", false);
		if(value != null && StringUtils.isNotEmpty(value.trim())) {
			this.pdndTracciamentoGenerazioneDbBatchSize = Integer.parseInt(value);
		}
		
		value = getProperty("statistiche.pdnd.tracciamento.pubblicazione.db.batchSize", false);
		if(value != null && StringUtils.isNotEmpty(value.trim())) {
			this.pdndTracciamentoPubblicazioneDbBatchSize = Integer.parseInt(value);
		}

		// Timer configuration
		this.timerStatisticheOrarieEnabled = this.getBooleanProperty("timer.statistiche.orarie.enabled", false);
		this.timerStatisticheGiornaliereEnabled = this.getBooleanProperty("timer.statistiche.giornaliere.enabled", false);
		this.timerStatisticheSettimanaliEnabled = this.getBooleanProperty("timer.statistiche.settimanali.enabled", false);
		this.timerStatisticheMensiliEnabled = this.getBooleanProperty("timer.statistiche.mensili.enabled", false);
		this.timerPdndGenerazioneEnabled = this.getBooleanProperty("timer.pdnd.generazione.enabled", false);
		this.timerPdndPubblicazioneEnabled = this.getBooleanProperty("timer.pdnd.pubblicazione.enabled", false);

		// Intervalli in secondi (allineati con GovWay)
		this.timerStatisticheOrarieIntervalloSecondi = this.getLongProperty("timer.statistiche.orarie.intervalloSecondi", false, 300);
		this.timerStatisticheGiornaliereIntervalloSecondi = this.getLongProperty("timer.statistiche.giornaliere.intervalloSecondi", false, 1800);
		this.timerStatisticheSettimanaliIntervalloSecondi = this.getLongProperty("timer.statistiche.settimanali.intervalloSecondi", false, 3600);
		this.timerStatisticheMensiliIntervalloSecondi = this.getLongProperty("timer.statistiche.mensili.intervalloSecondi", false, 7200);
		this.timerPdndGenerazioneIntervalloSecondi = this.getLongProperty("timer.pdnd.generazione.intervalloSecondi", false, 3600);
		this.timerPdndPubblicazioneIntervalloSecondi = this.getLongProperty("timer.pdnd.pubblicazione.intervalloSecondi", false, 3600);

		this.timerStatisticheOrarieDelayInizialeSecondi = this.getLongProperty("timer.statistiche.orarie.delayInizialeSecondi", false, 60);
		this.timerStatisticheGiornaliereDelayInizialeSecondi = this.getLongProperty("timer.statistiche.giornaliere.delayInizialeSecondi", false, 120);
		this.timerStatisticheSettimanaliDelayInizialeSecondi = this.getLongProperty("timer.statistiche.settimanali.delayInizialeSecondi", false, 180);
		this.timerStatisticheMensiliDelayInizialeSecondi = this.getLongProperty("timer.statistiche.mensili.delayInizialeSecondi", false, 240);
		this.timerPdndGenerazioneDelayInizialeSecondi = this.getLongProperty("timer.pdnd.generazione.delayInizialeSecondi", false, 300);
		this.timerPdndPubblicazioneDelayInizialeSecondi = this.getLongProperty("timer.pdnd.pubblicazione.delayInizialeSecondi", false, 360);

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
		// Lettura in secondi (come GovWay), i getter convertiranno in ms dove necessario
		this.semaphoreLockMaxLifeSeconds = this.getLongProperty("semaphore.lock.maxLife", false, 7200);
		this.semaphoreLockIdleTimeSeconds = this.getLongProperty("semaphore.lock.idleTime", false, 600);
		this.semaphoreSerializableTimeWaitMs = this.getLongProperty("semaphore.serializable.timeWaitMs", false, 60000);
		this.semaphoreSerializableNextIntervalTimeMs = (int) this.getLongProperty("semaphore.serializable.nextIntervalTimeMs", false, 100);
		this.semaphoreLockAttesaAttivaSeconds = this.getLongProperty("semaphore.lock.attesaAttiva", false, 90);
		this.semaphoreLockCheckIntervalMs = (int) this.getLongProperty("semaphore.lock.checkInterval", false, 50);
				
	}

	private String getProperty(String name, boolean required) throws UtilsException {
		String tmp = this.props.getValueConvertEnvProperties(name);
		if(tmp == null) {
			if(required) {
				throw new UtilsException(String.format("Property '%s' not found", name));
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
				throw new UtilsException(String.format("Property '%s' wrong boolean format: %s", name, e.getMessage()));
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
	
	public int getPdndTracciamentoGenerazioneDbBatchSize() {
		return this.pdndTracciamentoGenerazioneDbBatchSize;
	}

	public int getPdndTracciamentoPubblicazioneDbBatchSize() {
		return this.pdndTracciamentoPubblicazioneDbBatchSize;
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

	public long getTimerStatisticheOrarieIntervalloSecondi() {
		return this.timerStatisticheOrarieIntervalloSecondi;
	}
	public long getTimerStatisticheGiornaliereIntervalloSecondi() {
		return this.timerStatisticheGiornaliereIntervalloSecondi;
	}
	public long getTimerStatisticheSettimanaliIntervalloSecondi() {
		return this.timerStatisticheSettimanaliIntervalloSecondi;
	}
	public long getTimerStatisticheMensiliIntervalloSecondi() {
		return this.timerStatisticheMensiliIntervalloSecondi;
	}
	public long getTimerPdndGenerazioneIntervalloSecondi() {
		return this.timerPdndGenerazioneIntervalloSecondi;
	}
	public long getTimerPdndPubblicazioneIntervalloSecondi() {
		return this.timerPdndPubblicazioneIntervalloSecondi;
	}

	public long getTimerStatisticheOrarieDelayInizialeSecondi() {
		return this.timerStatisticheOrarieDelayInizialeSecondi;
	}
	public long getTimerStatisticheGiornaliereDelayInizialeSecondi() {
		return this.timerStatisticheGiornaliereDelayInizialeSecondi;
	}
	public long getTimerStatisticheSettimanaliDelayInizialeSecondi() {
		return this.timerStatisticheSettimanaliDelayInizialeSecondi;
	}
	public long getTimerStatisticheMensiliDelayInizialeSecondi() {
		return this.timerStatisticheMensiliDelayInizialeSecondi;
	}
	public long getTimerPdndGenerazioneDelayInizialeSecondi() {
		return this.timerPdndGenerazioneDelayInizialeSecondi;
	}
	public long getTimerPdndPubblicazioneDelayInizialeSecondi() {
		return this.timerPdndPubblicazioneDelayInizialeSecondi;
	}

	// Semaphore getters

	public boolean isSemaphoreEnabled() {
		return this.semaphoreEnabled;
	}
	public String getSemaphoreNodeId() {
		return this.semaphoreNodeId;
	}
	/** @return maxLife in secondi (per SemaphoreConfiguration) */
	public long getSemaphoreLockMaxLife() {
		return this.semaphoreLockMaxLifeSeconds;
	}
	/** @return idleTime in secondi (per SemaphoreConfiguration) */
	public long getSemaphoreLockIdleTime() {
		return this.semaphoreLockIdleTimeSeconds;
	}
	public long getSemaphoreSerializableTimeWaitMs() {
		return this.semaphoreSerializableTimeWaitMs;
	}
	public int getSemaphoreSerializableNextIntervalTimeMs() {
		return this.semaphoreSerializableNextIntervalTimeMs;
	}
	/** @return attesaAttiva in millisecondi (per ciclo di acquisizione lock) */
	public long getSemaphoreLockAttesaAttiva() {
		return this.semaphoreLockAttesaAttivaSeconds * 1000; // converti in ms
	}
	/** @return checkInterval in millisecondi */
	public int getSemaphoreLockCheckInterval() {
		return this.semaphoreLockCheckIntervalMs;
	}
	
	public boolean isJdbcCloseConnectionCheckIsClosed(){
		return this.jdbcCloseConnectionCheckIsClosed;
	}
	public boolean isJdbcCloseConnectionCheckAutocommit(){
		return this.jdbcCloseConnectionCheckAutocommit;
	}
	
	private static final String ENV_PROPERTIES_PREFIX = "env.";
	public Properties getEnvProperties() throws UtilsException {
		try {
			return this.props.readProperties(ENV_PROPERTIES_PREFIX);
		} catch (Exception e) {
			throw new UtilsException("Error reading environment properties: " + e.getMessage(), e);
		}
	}
}
