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
package org.openspcoop2.monitor.engine.statistic;

import org.openspcoop2.monitor.engine.exceptions.EngineException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.monitor.engine.config.MonitorProperties;
import org.openspcoop2.monitor.engine.constants.CostantiConfigurazione;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.InstanceProperties;
import org.openspcoop2.utils.properties.PropertiesReader;
import org.openspcoop2.utils.transport.http.HttpRequestConfig;
import org.slf4j.Logger;

/**
 * StatisticsConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsConfig {

	private Logger logCore = null;
	private Logger logSql = null;

	/** Indicazione se deve essere effettuato il log delle query */
	private boolean debug = false;	
	
	/** Indicazione se deve essere usata la union per calcolare i tempi di latenza */
	private boolean useUnionForLatency = false;	
	
	/** Indicazione se devono essere generate le statistiche custom */
	private boolean generazioneStatisticheCustom = false;
	
	/** Indicazione se devono analizzate le transazioni custom */
	private boolean analisiTransazioniCustom = false;
	
	/** Intervallo di attesa prima di passare al prossimo intervallo */
	private long waitMsBeforeNextInterval = -1;
	
	/** Attesa che tutte le transazioni siano state consegnate con successo prima di passare al prossimo intervallo */
	private boolean waitStatiInConsegna = false;
	
	/** Tipologie di statistiche */
	private boolean statisticheOrarie = false;
	private boolean statisticheGiornaliere = false;
	private boolean statisticheSettimanali = false;
	private boolean statisticheMensili = false;
	
	/** Tipologie di statistiche: gestione ultimo intervallo */
	private boolean statisticheOrarieGestioneUltimoIntervallo = false;
	private boolean statisticheGiornaliereGestioneUltimoIntervallo = false;
	private boolean statisticheSettimanaliGestioneUltimoIntervallo = false;
	private boolean statisticheMensiliGestioneUltimoIntervallo = false;
		
	/** Lista di indici forzati */
	private StatisticsForceIndexConfig forceIndexConfig = null;
	
	/** PDND Tracing **/
	private boolean pdndTracciamentoGenerazione = false;
	private boolean pdndTracciamentoPubblicazione = false;
	private HttpRequestConfig pdndTracciamentoRequestConfig;
	private boolean pdndTracciamentoSoggettiDisabled = false;
	private Set<String> pdndTracciamentoSoggettiEnabled;
	private Integer pdndTracciamentoMaxAttempt = null;
	private List<Integer> pdndTracciamentoPendingCheck = null;
	private boolean pdndTracciamentoErogazioniEnabled = true;
	private boolean pdndTracciamentoFruizioniEnabled = true;
	private int pdndTracciamentoGenerazioneDelayMinutes = 0;
	private int pdndTracciamentoGenerazioneDbBatchSize = 200;
	private int pdndTracciamentoPubblicazioneDbBatchSize = 200;

	/** Group By Configuration **/
	private StatisticsGroupByConfig groupByConfig = new StatisticsGroupByConfig(); // default

	private static final String FALSE = "false";
	
	/** Costruttore */
	public StatisticsConfig(boolean readPropertiesFromFile) throws EngineException{
	
		try{
			if(readPropertiesFromFile){
				
				this.logCore = LoggerWrapperFactory.getLogger("org.openspcoop2.monitor.engine.statistic");
				this.logSql = LoggerWrapperFactory.getLogger("org.openspcoop2.monitor.engine.statistic.sql");
				
				MonitorProperties props = MonitorProperties.getInstance(this.logCore);
	
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_DEBUG, FALSE, true))) {
					this.debug = true;
				} else {
					this.debug = false;
				}
				
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_USE_UNION_FOR_LATENCY, FALSE, true))) {
					this.useUnionForLatency = true;
				} else {
					this.useUnionForLatency = false;
				}
				
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_CUSTOM_STATISTICS, FALSE, true))) {
					this.generazioneStatisticheCustom = true;
				} else {
					this.generazioneStatisticheCustom = false;
				}
				
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_CUSTOM_TRANSACTION_STATISTICS, FALSE, true))) {
					this.analisiTransazioniCustom = true;
				} else {
					this.analisiTransazioniCustom = false;
				}
		
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_HOURLY, "true", true))) {
					this.statisticheOrarie = true;
				} else {
					this.statisticheOrarie = false;
				}
				
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_DAILY, "true", true))) {
					this.statisticheGiornaliere = true;
				} else {
					this.statisticheGiornaliere = false;
				}
		
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_WEEKLY, "true", true))) {
					this.statisticheSettimanali = true;
				} else {
					this.statisticheSettimanali = false;
				}
		
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_MONTHLY, "true", true))) {
					this.statisticheMensili = true;
				} else {
					this.statisticheMensili = false;
				}
				
			    this.pdndTracciamentoGenerazione = parsePdndGenerazioneTracciamentoProperty(props);
			    this.pdndTracciamentoPubblicazione = parsePdndPubblicazioneTracciamentoProperty(props);
			    this.pdndTracciamentoRequestConfig = parsePdndTracingRequestConfig(props);
			    this.pdndTracciamentoSoggettiEnabled = parsePdndTracingSoggettiEnabled(props);
			    this.pdndTracciamentoSoggettiDisabled = parsePdndTracingSoggettiDisabled(props);
			    this.pdndTracciamentoErogazioniEnabled = parsePdndTracingErogazioniEnabled(props);
			    this.pdndTracciamentoFruizioniEnabled = parsePdndTracingFruizioniEnabled(props);
			    this.pdndTracciamentoGenerazioneDelayMinutes = parsePdndTracingGenerazioneDelayMinutes(props);
			    this.pdndTracciamentoPendingCheck = parsePdndTracciamentoPendingCheck(props);
			    this.pdndTracciamentoMaxAttempt = parsePdndTracciamentoMaxAttempts(props);
			    this.pdndTracciamentoGenerazioneDbBatchSize = parsePdndTracciamentoDbBatchSize(props, CostantiConfigurazione.PDND_TRACCIAMENTO_GENERAZIONE_DB_BATCH_SIZE);
			    this.pdndTracciamentoPubblicazioneDbBatchSize = parsePdndTracciamentoDbBatchSize(props, CostantiConfigurazione.PDND_TRACCIAMENTO_PUBBLICAZIONE_DB_BATCH_SIZE);
			    
				if ("true".equals(props.getProperty(CostantiConfigurazione.PDND_PUBBLICAZIONE_TRACCIAMENTO_ENABLED, "true", true))) {
					this.pdndTracciamentoPubblicazione = true;
				} else {
					this.pdndTracciamentoPubblicazione = false;
				}
		
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_HOURLY_LASTINT, "true", true))) {
					this.statisticheOrarieGestioneUltimoIntervallo = true;
				} else {
					this.statisticheOrarieGestioneUltimoIntervallo = false;
				}
				
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_DAILY_LASTINT, "true", true))) {
					this.statisticheGiornaliereGestioneUltimoIntervallo = true;
				} else {
					this.statisticheGiornaliereGestioneUltimoIntervallo = false;
				}
		
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_WEEKLY_LASTINT, "true", true))) {
					this.statisticheSettimanaliGestioneUltimoIntervallo = true;
				} else {
					this.statisticheSettimanaliGestioneUltimoIntervallo = false;
				}
		
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_MONTHLY_LASTINT, "true", true))) {
					this.statisticheMensiliGestioneUltimoIntervallo = true;
				} else {
					this.statisticheMensiliGestioneUltimoIntervallo = false;
				}

				// Inizializzazione groupByConfig
				this.groupByConfig = parseGroupByConfig(props.getReader(), "org.openspcoop2.monitor.statistic.");

			}

		}catch(Exception e){
			throw new EngineException(e.getMessage(),e);
		}
	}

	private static boolean parsePdndGenerazioneTracciamentoProperty(MonitorProperties props) throws UtilsException {
		String propId = CostantiConfigurazione.PDND_GENERAZIONE_TRACCIAMENTO_ENABLED;
		return "true".equals(props.getProperty(propId, "true", true));
	}
	
	private static boolean parsePdndPubblicazioneTracciamentoProperty(MonitorProperties props) throws UtilsException {
		String propId = CostantiConfigurazione.PDND_PUBBLICAZIONE_TRACCIAMENTO_ENABLED;
		return "true".equals(props.getProperty(propId, "true", true));
	}
	
	private static boolean parsePdndTracingErogazioniEnabled(MonitorProperties props) throws UtilsException {
		String propId = CostantiConfigurazione.PDND_PUBBLICAZIONE_TRACCIAMENTO_EROGAZIONI_ENABLED;
		return "true".equals(props.getProperty(propId, "true", true));
	}
	
	private static boolean parsePdndTracingFruizioniEnabled(MonitorProperties props) throws UtilsException {
		String propId = CostantiConfigurazione.PDND_PUBBLICAZIONE_TRACCIAMENTO_FRUIZIONI_ENABLED;
		return "true".equals(props.getProperty(propId, "true", true));
	}
	
	private static int parsePdndTracingGenerazioneDelayMinutes(MonitorProperties props) throws UtilsException {
		String propId = CostantiConfigurazione.PDND_GENERAZIONE_TRACCIAMENTO_DELAY_MINUTES;
		String provValue = props.getProperty(propId, "0", true);
		return Integer.valueOf(provValue);
	}
	
	private static int parsePdndTracciamentoDbBatchSize(MonitorProperties props, String propId) throws UtilsException {
		String provValue = props.getProperty(propId, "200", true);
		return Integer.valueOf(provValue);
	}
	
	private static HttpRequestConfig parsePdndTracingRequestConfig(MonitorProperties props) {
		return new HttpRequestConfig("pdnd.tracciamento", str -> {
			try {
				return props.getProperty(str, false, true);
			} catch (UtilsException e) {
				return null;
			}
		});
	}
	
	private static Set<String> parsePdndTracingSoggettiEnabled(MonitorProperties props) throws UtilsException {
		String propId = CostantiConfigurazione.PDND_PUBBLICAZIONE_TRACCIAMENTO_SOGGETTI_ENABLED;
		String value = props.getProperty(propId, "", false);
		if (value == null || StringUtils.isEmpty(value.trim()) || "*".equals(value.trim()))
			return Set.of();
		return Arrays.stream(value.split(","))
				.map(String::trim)
				.collect(Collectors.toSet());
	}
	private boolean parsePdndTracingSoggettiDisabled(MonitorProperties props) throws UtilsException {
		String propId = CostantiConfigurazione.PDND_PUBBLICAZIONE_TRACCIAMENTO_SOGGETTI_ENABLED;
		String value = props.getProperty(propId, "", false);
		return value==null || StringUtils.isEmpty(value.trim());
	}
	
	private List<Integer> parsePdndTracciamentoPendingCheck(MonitorProperties props) throws UtilsException {
		String propId = CostantiConfigurazione.PDND_TRACCIAMENTO_PUBBLICAZIONE_PENDING_CHECK;
		String value = props.getProperty(propId, "", false);
		return value == null || StringUtils.isEmpty(value.trim()) ? List.of(0) : Arrays.stream(value.split(","))
				.map(Integer::valueOf)
				.collect(Collectors.toList());
	}
	
	private Integer parsePdndTracciamentoMaxAttempts(MonitorProperties props) throws UtilsException {
		String propId = CostantiConfigurazione.PDND_TRACCIAMENTO_PUBBLICAZIONE_MAX_ATTEMPTS;
		String value = props.getProperty(propId, "", false);
		return value == null || StringUtils.isEmpty(value.trim()) ? null : Integer.valueOf(value);
	}

	public static StatisticsGroupByConfig parseGroupByConfig(InstanceProperties props, String prefix) throws UtilsException {
		return parseGroupByConfig(props,  null, prefix);
	}
	public static StatisticsGroupByConfig parseGroupByConfig(PropertiesReader props, String prefix) throws UtilsException {
		return parseGroupByConfig(null, props, prefix);
	}
	private static StatisticsGroupByConfig parseGroupByConfig(InstanceProperties propsIP,  PropertiesReader propsPR, String prefix) throws UtilsException {
		StatisticsGroupByConfig config = new StatisticsGroupByConfig();

		// Lettura proprietà per ogni campo del GROUP BY
		// Se la proprietà non esiste, rimane il default (true)
		// Il prefix viene concatenato con "groupBy.<nomeCampo>"
		String groupByPrefix = prefix + "groupBy.";

		config.setTipoMittente(getBooleanProperty(propsIP, propsPR, groupByPrefix + "tipoMittente"));
		config.setNomeMittente(getBooleanProperty(propsIP, propsPR, groupByPrefix + "nomeMittente"));
		config.setTipoDestinatario(getBooleanProperty(propsIP, propsPR, groupByPrefix + "tipoDestinatario"));
		config.setNomeDestinatario(getBooleanProperty(propsIP, propsPR, groupByPrefix + "nomeDestinatario"));
		config.setTipoServizio(getBooleanProperty(propsIP, propsPR, groupByPrefix + "tipoServizio"));
		config.setServizio(getBooleanProperty(propsIP, propsPR, groupByPrefix + "servizio"));
		config.setVersioneServizio(getBooleanProperty(propsIP, propsPR, groupByPrefix + "versioneServizio"));
		config.setAzione(getBooleanProperty(propsIP, propsPR, groupByPrefix + "azione"));
		config.setServizioApplicativo(getBooleanProperty(propsIP, propsPR, groupByPrefix + "servizioApplicativo"));
		config.setTrasportoMittente(getBooleanProperty(propsIP, propsPR, groupByPrefix + "trasportoMittente"));
		config.setTokenIssuer(getBooleanProperty(propsIP, propsPR, groupByPrefix + "tokenIssuer"));
		config.setTokenClientId(getBooleanProperty(propsIP, propsPR, groupByPrefix + "tokenClientId"));
		config.setTokenSubject(getBooleanProperty(propsIP, propsPR, groupByPrefix + "tokenSubject"));
		config.setTokenUsername(getBooleanProperty(propsIP, propsPR, groupByPrefix + "tokenUsername"));
		config.setTokenMail(getBooleanProperty(propsIP, propsPR, groupByPrefix + "tokenMail"));
		config.setEsito(getBooleanProperty(propsIP, propsPR, groupByPrefix + "esito"));
		config.setEsitoContesto(getBooleanProperty(propsIP, propsPR, groupByPrefix + "esitoContesto"));
		config.setGruppo(getBooleanProperty(propsIP, propsPR, groupByPrefix + "gruppo"));
		config.setApi(getBooleanProperty(propsIP, propsPR, groupByPrefix + "api"));
		config.setClusterId(getBooleanProperty(propsIP, propsPR, groupByPrefix + "clusterId"));
		config.setClientAddress(getBooleanProperty(propsIP, propsPR, groupByPrefix + "clientAddress"));

		return config;
	}

	private static boolean getBooleanProperty(InstanceProperties propsIP,  PropertiesReader propsPR, String propertyName) throws UtilsException {
		// Se la proprietà non esiste, ritorna true (default)
		String value = null;
		if(propsIP!=null) {
			value = propsIP.getValueConvertEnvProperties(propertyName);
		}
		else if(propsPR!=null) {
			value = propsPR.getValue_convertEnvProperties(propertyName);
		} 
		if(value == null) {
			return true;
		}
		return "true".equalsIgnoreCase(value.trim());
	}

	public Logger getLogCore() {
		return this.logCore;
	}
	
	public void setLogCore(Logger logCore) {
		this.logCore = logCore;
	}

	public Logger getLogSql() {
		return this.logSql;
	}

	public void setLogSql(Logger logSql) {
		this.logSql = logSql;
	}
	
	public boolean isDebug() {
		return this.debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isUseUnionForLatency() {
		return this.useUnionForLatency;
	}

	public void setUseUnionForLatency(boolean useUnionForLatency) {
		this.useUnionForLatency = useUnionForLatency;
	}
	
	public boolean isGenerazioneStatisticheCustom() {
		return this.generazioneStatisticheCustom;
	}

	public void setGenerazioneStatisticheCustom(boolean generazioneStatisticheCustom) {
		this.generazioneStatisticheCustom = generazioneStatisticheCustom;
	}

	public boolean isStatisticheOrarie() {
		return this.statisticheOrarie;
	}

	public void setStatisticheOrarie(boolean statisticheOrarie) {
		this.statisticheOrarie = statisticheOrarie;
	}

	public boolean isStatisticheGiornaliere() {
		return this.statisticheGiornaliere;
	}

	public void setStatisticheGiornaliere(boolean statisticheGiornaliere) {
		this.statisticheGiornaliere = statisticheGiornaliere;
	}

	public boolean isStatisticheSettimanali() {
		return this.statisticheSettimanali;
	}

	public void setStatisticheSettimanali(boolean statisticheSettimanali) {
		this.statisticheSettimanali = statisticheSettimanali;
	}

	public boolean isStatisticheMensili() {
		return this.statisticheMensili;
	}

	public void setStatisticheMensili(boolean statisticheMensili) {
		this.statisticheMensili = statisticheMensili;
	}

	public boolean isStatisticheOrarieGestioneUltimoIntervallo() {
		return this.statisticheOrarieGestioneUltimoIntervallo;
	}
	

	public void setStatisticheOrarieGestioneUltimoIntervallo(
			boolean statisticheOrarieGestioneUltimoIntervallo) {
		this.statisticheOrarieGestioneUltimoIntervallo = statisticheOrarieGestioneUltimoIntervallo;
	}

	public boolean isStatisticheGiornaliereGestioneUltimoIntervallo() {
		return this.statisticheGiornaliereGestioneUltimoIntervallo;
	}

	public void setStatisticheGiornaliereGestioneUltimoIntervallo(
			boolean statisticheGiornaliereGestioneUltimoIntervallo) {
		this.statisticheGiornaliereGestioneUltimoIntervallo = statisticheGiornaliereGestioneUltimoIntervallo;
	}

	public boolean isStatisticheSettimanaliGestioneUltimoIntervallo() {
		return this.statisticheSettimanaliGestioneUltimoIntervallo;
	}

	public void setStatisticheSettimanaliGestioneUltimoIntervallo(
			boolean statisticheSettimanaliGestioneUltimoIntervallo) {
		this.statisticheSettimanaliGestioneUltimoIntervallo = statisticheSettimanaliGestioneUltimoIntervallo;
	}

	public boolean isStatisticheMensiliGestioneUltimoIntervallo() {
		return this.statisticheMensiliGestioneUltimoIntervallo;
	}

	public void setStatisticheMensiliGestioneUltimoIntervallo(
			boolean statisticheMensiliGestioneUltimoIntervallo) {
		this.statisticheMensiliGestioneUltimoIntervallo = statisticheMensiliGestioneUltimoIntervallo;
	}
	
	public boolean isAnalisiTransazioniCustom() {
		return this.analisiTransazioniCustom;
	}

	public void setAnalisiTransazioniCustom(boolean analisiTransazioniCustom) {
		this.analisiTransazioniCustom = analisiTransazioniCustom;
	}
	
	public StatisticsForceIndexConfig getForceIndexConfig() {
		return this.forceIndexConfig;
	}

	public void setForceIndexConfig(StatisticsForceIndexConfig forceIndexConfig) {
		this.forceIndexConfig = forceIndexConfig;
	}
	
	public long getWaitMsBeforeNextInterval() {
		return this.waitMsBeforeNextInterval;
	}

	public void setWaitMsBeforeNextInterval(long waitMsBeforeNextInterval) {
		this.waitMsBeforeNextInterval = waitMsBeforeNextInterval;
	}

	public boolean isWaitStatiInConsegna() {
		return this.waitStatiInConsegna;
	}

	public void setWaitStatiInConsegna(boolean waitStatiInConsegna) {
		this.waitStatiInConsegna = waitStatiInConsegna;
	}
	
	public boolean isPdndTracciamentoPubblicazione() {
		return this.pdndTracciamentoPubblicazione;
	}

	public void setPdndTracciamentoPubblicazione(boolean pdndPubblicazioneTracciamento) {
		this.pdndTracciamentoPubblicazione = pdndPubblicazioneTracciamento;
	}
	
	public boolean isPdndTracciamentoGenerazione() {
		return this.pdndTracciamentoGenerazione;
	}

	public void setPdndTracciamentoGenerazione(boolean pdndGenerazioneTracciamento) {
		this.pdndTracciamentoGenerazione = pdndGenerazioneTracciamento;
	}
	
	public HttpRequestConfig getPdndTracciamentoRequestConfig() {
		return this.pdndTracciamentoRequestConfig;
	}

	public void setPdndTracciamentoRequestConfig(HttpRequestConfig pdndTracingRequestConfig) {
		this.pdndTracciamentoRequestConfig = pdndTracingRequestConfig;
	}
	
	public void setPdndTracciamentoMaxAttempt(Integer maxAttempt) {
		this.pdndTracciamentoMaxAttempt = maxAttempt;
	}
	
	public Integer getPdndTracciamentoMaxAttempt() {
		return this.pdndTracciamentoMaxAttempt;
	}
	
	public void setPdndTracciamentoPendingCheck(List<Integer> pdndTracciamentoPendingCheck) {
		this.pdndTracciamentoPendingCheck = pdndTracciamentoPendingCheck;
	}
	
	public List<Integer> getPdndTracciamentoPendingCheck() {
		return this.pdndTracciamentoPendingCheck;
	}
	
	public void setPdndTracciamentoSoggettiEnabled(Set<String> pdndTracingSoggettiEnabled) {
		this.pdndTracciamentoSoggettiEnabled = pdndTracingSoggettiEnabled;
	}
	
	public Set<String> getPdndTracciamentoSoggettiEnabled() {
		return this.pdndTracciamentoSoggettiEnabled;
	}

	public boolean isPdndTracciamentoSoggettiDisabled() {
		return this.pdndTracciamentoSoggettiDisabled;
	}

	public void setPdndTracciamentoSoggettiDisabled(boolean pdndTracciamentoSoggettiDisabled) {
		this.pdndTracciamentoSoggettiDisabled = pdndTracciamentoSoggettiDisabled;
	}
	
	public boolean isPdndTracciamentoErogazioniEnabled() {
		return this.pdndTracciamentoErogazioniEnabled;
	}

	public void setPdndTracciamentoErogazioniEnabled(boolean pdndTracingErogazioniEnabled) {
		this.pdndTracciamentoErogazioniEnabled = pdndTracingErogazioniEnabled;
	}

	public boolean isPdndTracciamentoFruizioniEnabled() {
		return this.pdndTracciamentoFruizioniEnabled;
	}

	public void setPdndTracciamentoFruizioniEnabled(boolean pdndTracingFruizioniEnabled) {
		this.pdndTracciamentoFruizioniEnabled = pdndTracingFruizioniEnabled;
	}
	
	public int getPdndTracciamentoGenerazioneDelayMinutes() {
		return this.pdndTracciamentoGenerazioneDelayMinutes;
	}

	public void setPdndTracciamentoGenerazioneDelayMinutes(int pdndTracciamentoGenerazioneDelayMinutes) {
		this.pdndTracciamentoGenerazioneDelayMinutes = pdndTracciamentoGenerazioneDelayMinutes;
	}

	public int getPdndTracciamentoGenerazioneDbBatchSize() {
		return this.pdndTracciamentoGenerazioneDbBatchSize;
	}

	public void setPdndTracciamentoGenerazioneDbBatchSize(int pdndTracciamentoGenerazioneDbBatchSize) {
		this.pdndTracciamentoGenerazioneDbBatchSize = pdndTracciamentoGenerazioneDbBatchSize;
	}
	
	public int getPdndTracciamentoPubblicazioneDbBatchSize() {
		return this.pdndTracciamentoPubblicazioneDbBatchSize;
	}

	public void setPdndTracciamentoPubblicazioneDbBatchSize(int pdndTracciamentoPubblicazioneDbBatchSize) {
		this.pdndTracciamentoPubblicazioneDbBatchSize = pdndTracciamentoPubblicazioneDbBatchSize;
	}
	
	public StatisticsGroupByConfig getGroupByConfig() {
		return this.groupByConfig;
	}

	public void setGroupByConfig(StatisticsGroupByConfig groupByConfig) {
		this.groupByConfig = groupByConfig;
	}
	
}
