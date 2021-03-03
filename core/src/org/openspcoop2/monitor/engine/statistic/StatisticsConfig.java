/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import org.openspcoop2.monitor.engine.config.MonitorProperties;
import org.openspcoop2.monitor.engine.constants.CostantiConfigurazione;

import org.openspcoop2.utils.LoggerWrapperFactory;
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
	
	/** Tipologie di statistiche */
	private boolean statisticheOrarie = false;
	private boolean statisticheGiornaliere = false;
	private boolean statisticheSettimanali = false;
	private boolean statisticheMensili = false;
	
	/** Tipologie di statistiche: gestione ultimo intervallo */
	private boolean statisticheOrarie_gestioneUltimoIntervallo = false;
	private boolean statisticheGiornaliere_gestioneUltimoIntervallo = false;
	private boolean statisticheSettimanali_gestioneUltimoIntervallo = false;
	private boolean statisticheMensili_gestioneUltimoIntervallo = false;
	
	/** Lista di indici forzati */
	private StatisticsForceIndexConfig forceIndexConfig = null;
	
	
	/** Costruttore */
	public StatisticsConfig(boolean readPropertiesFromFile) throws EngineException{
	
		try{
			if(readPropertiesFromFile){
				
				this.logCore = LoggerWrapperFactory.getLogger("org.openspcoop2.monitor.engine.statistic");
				this.logSql = LoggerWrapperFactory.getLogger("org.openspcoop2.monitor.engine.statistic.sql");
				
				MonitorProperties props = MonitorProperties.getInstance(this.logCore);
	
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_DEBUG, "false", true))) {
					this.debug = true;
				} else {
					this.debug = false;
				}
				
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_USE_UNION_FOR_LATENCY, "false", true))) {
					this.useUnionForLatency = true;
				} else {
					this.useUnionForLatency = false;
				}
				
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_CUSTOM_STATISTICS, "false", true))) {
					this.generazioneStatisticheCustom = true;
				} else {
					this.generazioneStatisticheCustom = false;
				}
				
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_CUSTOM_TRANSACTION_STATISTICS, "false", true))) {
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
		
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_HOURLY_LASTINT, "true", true))) {
					this.statisticheOrarie_gestioneUltimoIntervallo = true;
				} else {
					this.statisticheOrarie_gestioneUltimoIntervallo = false;
				}
				
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_DAILY_LASTINT, "true", true))) {
					this.statisticheGiornaliere_gestioneUltimoIntervallo = true;
				} else {
					this.statisticheGiornaliere_gestioneUltimoIntervallo = false;
				}
		
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_WEEKLY_LASTINT, "true", true))) {
					this.statisticheSettimanali_gestioneUltimoIntervallo = true;
				} else {
					this.statisticheSettimanali_gestioneUltimoIntervallo = false;
				}
		
				if ("true".equals(props.getProperty(CostantiConfigurazione.STAT_MONTHLY_LASTINT, "true", true))) {
					this.statisticheMensili_gestioneUltimoIntervallo = true;
				} else {
					this.statisticheMensili_gestioneUltimoIntervallo = false;
				}
			}
			
		}catch(Exception e){
			throw new EngineException(e.getMessage(),e);
		}
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

	public boolean isStatisticheOrarie_gestioneUltimoIntervallo() {
		return this.statisticheOrarie_gestioneUltimoIntervallo;
	}

	public void setStatisticheOrarie_gestioneUltimoIntervallo(
			boolean statisticheOrarie_gestioneUltimoIntervallo) {
		this.statisticheOrarie_gestioneUltimoIntervallo = statisticheOrarie_gestioneUltimoIntervallo;
	}

	public boolean isStatisticheGiornaliere_gestioneUltimoIntervallo() {
		return this.statisticheGiornaliere_gestioneUltimoIntervallo;
	}

	public void setStatisticheGiornaliere_gestioneUltimoIntervallo(
			boolean statisticheGiornaliere_gestioneUltimoIntervallo) {
		this.statisticheGiornaliere_gestioneUltimoIntervallo = statisticheGiornaliere_gestioneUltimoIntervallo;
	}

	public boolean isStatisticheSettimanali_gestioneUltimoIntervallo() {
		return this.statisticheSettimanali_gestioneUltimoIntervallo;
	}

	public void setStatisticheSettimanali_gestioneUltimoIntervallo(
			boolean statisticheSettimanali_gestioneUltimoIntervallo) {
		this.statisticheSettimanali_gestioneUltimoIntervallo = statisticheSettimanali_gestioneUltimoIntervallo;
	}

	public boolean isStatisticheMensili_gestioneUltimoIntervallo() {
		return this.statisticheMensili_gestioneUltimoIntervallo;
	}

	public void setStatisticheMensili_gestioneUltimoIntervallo(
			boolean statisticheMensili_gestioneUltimoIntervallo) {
		this.statisticheMensili_gestioneUltimoIntervallo = statisticheMensili_gestioneUltimoIntervallo;
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
}
