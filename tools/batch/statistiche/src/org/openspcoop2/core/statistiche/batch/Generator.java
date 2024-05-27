/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.core.statistiche.batch;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.monitor.engine.statistic.StatisticsConfig;
import org.openspcoop2.monitor.engine.statistic.StatisticsLibrary;
import org.openspcoop2.pdd.core.byok.BYOKMapProperties;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.properties.MapProperties;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.security.ProviderUtils;
import org.slf4j.Logger;

/**
* Generator
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Generator {

	private static final String LOGGER_PREFIX = "govway.batch.";
	
	public static void main(String[] args) throws UtilsException {

		StringBuilder bf = new StringBuilder();
		String [] tipi = TipoIntervalloStatistico.toStringArray();
		if(tipi!=null) {
			for (String t : tipi) {
				if(bf.length()>0) {
					bf.append(",");
				}
				bf.append(t);
			}
		}
		String usage = "\n\nUse: generator.sh tipo\n\ttipo: "+bf.toString();
		
		if(args.length<=0) {
			throw new UtilsException("ERROR: tipo di statistica da generare non fornito"+usage);
		}
		
		String tipo = args[0].trim();
		TipoIntervalloStatistico tipoStatistica = null;
		try {
			tipoStatistica = TipoIntervalloStatistico.toEnumConstant(tipo, true);
		}catch(Exception e) {
			throw new UtilsException("ERROR: tipo di statistica fornita ("+tipo+") sconosciuta"+usage);
		}

		String nomeLogger = null;
		switch (tipoStatistica) {
		case STATISTICHE_ORARIE:
			nomeLogger = "statistiche_orarie";
			break;
		case STATISTICHE_GIORNALIERE:
			nomeLogger = "statistiche_giornaliere";
			break;
		case STATISTICHE_SETTIMANALI:
			nomeLogger = "statistiche_settimanali";
			break;
		case STATISTICHE_MENSILI:
			nomeLogger = "statistiche_mensili";
			break;
		}
		
		Logger logCore = null;
		Logger logSql = null;
		try{
			Properties props = new Properties();
			try(FileInputStream fis = new FileInputStream(Generator.class.getResource("/batch-statistiche.log4j2.properties").getFile())){
				props.load(fis);
				LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
				LoggerWrapperFactory.setLogConfiguration(props);
				logCore = LoggerWrapperFactory.getLogger(LOGGER_PREFIX+nomeLogger+".generazione.error");
				logSql = LoggerWrapperFactory.getLogger(LOGGER_PREFIX+nomeLogger+".generazione.sql.error");
			}
		}catch(Exception e) {
			throw new UtilsException("Impostazione logging fallita: "+e.getMessage());
		}
		
		GeneratorProperties generatorProperties = GeneratorProperties.getInstance();
		
		if(generatorProperties.isStatisticheGenerazioneDebug()) {
			logCore = LoggerWrapperFactory.getLogger(LOGGER_PREFIX+nomeLogger+".generazione");
			logSql = LoggerWrapperFactory.getLogger(LOGGER_PREFIX+nomeLogger+".generazione.sql");
		}
		
		// Map (environment)
		try {
			String mapConfig = generatorProperties.getEnvMapConfig();
			if(StringUtils.isNotEmpty(mapConfig)) {
				logCore.info("Inizializzazione environment in corso...");
				MapProperties.initialize(logCore, mapConfig, generatorProperties.isEnvMapConfigRequired());
				MapProperties mapProperties = MapProperties.getInstance();
				mapProperties.initEnvironment();
				String msgInit = "Environment inizializzato con le variabili definite nel file '"+mapConfig+"'"+
						"\n\tJavaProperties: "+mapProperties.getJavaMap().keys()+
						"\n\tEnvProperties: "+mapProperties.getEnvMap().keys()+
						"\n\tObfuscateMode: "+mapProperties.getObfuscateModeDescription()+
						"\n\tObfuscatedJavaKeys: "+mapProperties.getObfuscatedJavaKeys()+
						"\n\tObfuscatedEnvKeys: "+mapProperties.getObfuscatedEnvKeys();
				logCore.info(msgInit);
			}
		} catch (Exception e) {
			doError(logCore, "Errore durante l'inizializzazione dell'ambiente",e);
		}
		
		// Load Security Provider
		logCore.info("Inizializzazione security provider...");
		try {
			if(generatorProperties.isSecurityLoadBouncyCastleProvider()) {
				ProviderUtils.addBouncyCastleAfterSun(true);
				logCore.info("Aggiunto Security Provider org.bouncycastle.jce.provider.BouncyCastleProvider");
			}
		} catch (Exception e) {
			doError(logCore, "Errore durante l'inizializzazione dei security provider",e);
		}
		logCore.info("Inizializzazione security provider effettuata con successo");
		
		// inizializzo HSM Manager
		try {
			String hsmConfig = generatorProperties.getHSMConfigurazione();
			if(StringUtils.isNotEmpty(hsmConfig)) {
				logCore.info("Inizializzazione HSM in corso...");
				File f = new File(hsmConfig);
				HSMManager.init(f, generatorProperties.isHSMRequired(), logCore, false);
				HSMUtils.setHsmConfigurableKeyPassword(generatorProperties.isHSMKeyPasswordConfigurable());
				logCore.info("Inizializzazione HSM effettuata con successo");
			}
		} catch (Exception e) {
			doError(logCore, "Errore durante l'inizializzazione del manager HSM",e);
		}
		
		// inizializzo BYOK Manager
		BYOKManager byokManager = null;
		try {
			String byokConfig = generatorProperties.getBYOKConfigurazione();
			if(StringUtils.isNotEmpty(byokConfig)) {
				logCore.info("Inizializzazione BYOK in corso...");
				File f = new File(byokConfig);
				BYOKManager.init(f, generatorProperties.isBYOKRequired(), logCore);
				byokManager = BYOKManager.getInstance();
				String msgInit = "Gestore BYOK inizializzato;"+
						"\n\tHSM registrati: "+byokManager.getKeystoreTypes()+
						"\n\tSecurityEngine registrati: "+byokManager.getSecurityEngineTypes()+
						"\n\tGovWaySecurityEngine: "+byokManager.getSecurityEngineGovWayDescription();
				logCore.info(msgInit);
			}
		} catch (Exception e) {
			doError(logCore, "Errore durante l'inizializzazione del manager BYOK",e);
		}
		
		// Secrets (environment)
		try {
			String secretsConfig = generatorProperties.getBYOKEnvSecretsConfig();
			if(byokManager!=null && StringUtils.isNotEmpty(secretsConfig)) {
				logCore.info("Inizializzazione secrets in corso...");
				String securityPolicy = BYOKManager.getSecurityEngineGovWayInstance();
				String securityRemotePolicy = BYOKManager.getSecurityRemoteEngineGovWayInstance();
				
				Map<String, Object> dynamicMap = new HashMap<>();
				DynamicInfo dynamicInfo = new  DynamicInfo();
				DynamicUtils.fillDynamicMap(logCore, dynamicMap, dynamicInfo);

				BYOKMapProperties.initialize(logCore, secretsConfig, generatorProperties.isBYOKEnvSecretsConfigRequired(), 
						securityPolicy, securityRemotePolicy, 
						dynamicMap, true);
				BYOKMapProperties secretsProperties = BYOKMapProperties.getInstance();
				secretsProperties.initEnvironment();
				String msgInit = "Environment inizializzato con i secrets definiti nel file '"+secretsConfig+"'"+
						"\n\tJavaProperties: "+secretsProperties.getJavaMap().keys()+
						"\n\tEnvProperties: "+secretsProperties.getEnvMap().keys()+
						"\n\tObfuscateMode: "+secretsProperties.getObfuscateModeDescription();
				logCore.info(msgInit);
			}
		} catch (Exception e) {
			doError(logCore, "Errore durante l'inizializzazione dell'ambiente (secrets)",e);
		}		
		
		// Inizializza restanti proprietà
		generatorProperties.initProperties();
		
		// Inizializza Protocol Factory Manager
		try {
			ConfigurazionePdD configPdD = new ConfigurazionePdD();
			configPdD.setAttesaAttivaJDBC(-1);
			configPdD.setCheckIntervalJDBC(-1);
			configPdD.setLoader(new Loader(Generator.class.getClassLoader()));
			configPdD.setLog(logCore);
			ProtocolFactoryManager.initialize(logCore, configPdD,
					generatorProperties.getProtocolloDefault());
		} catch (Exception e) {
			throw new UtilsException("Errore durante la generazione delle statistiche (InitConfigurazione - ProtocolFactoryManager): "+e.getMessage(),e);
		}
		
		// Inizializza configurazione statistiche
		StatisticsConfig statisticsConfig = null;
		try{
			statisticsConfig = new StatisticsConfig(false);
			
			statisticsConfig.setLogCore(logCore);
			statisticsConfig.setLogSql(logSql);
			statisticsConfig.setGenerazioneStatisticheCustom(generatorProperties.isGenerazioneStatisticheCustom());
			statisticsConfig.setAnalisiTransazioniCustom(generatorProperties.isAnalisiTransazioniCustom());
			statisticsConfig.setDebug(generatorProperties.isStatisticheGenerazioneDebug());
			statisticsConfig.setUseUnionForLatency(generatorProperties.isGenerazioneStatisticheUseUnionForLatency());
			switch (tipoStatistica) {
			case STATISTICHE_ORARIE:
				statisticsConfig.setStatisticheOrarie(true);
				statisticsConfig.setStatisticheOrarieGestioneUltimoIntervallo(true);
				break;
			case STATISTICHE_GIORNALIERE:
				statisticsConfig.setStatisticheGiornaliere(true);
				statisticsConfig.setStatisticheGiornaliereGestioneUltimoIntervallo(true);
				break;
			case STATISTICHE_SETTIMANALI:
				statisticsConfig.setStatisticheSettimanali(true);
				statisticsConfig.setStatisticheSettimanaliGestioneUltimoIntervallo(true);
				break;
			case STATISTICHE_MENSILI:
				statisticsConfig.setStatisticheMensili(true);
				statisticsConfig.setStatisticheMensiliGestioneUltimoIntervallo(true);
				break;
			}
			statisticsConfig.setWaitMsBeforeNextInterval(generatorProperties.getGenerazioneTradeOffMs());
			statisticsConfig.setWaitStatiInConsegna(generatorProperties.isGenerazioneAttendiCompletamentoTransazioniInFasiIntermedie());
			
			// aggiorno configurazione per forceIndex
			statisticsConfig.setForceIndexConfig(generatorProperties.getStatisticheGenerazioneForceIndexConfig());
			
		}catch(Exception e){
			throw new UtilsException("Errore durante la generazione delle statistiche (InitConfigurazione): "+e.getMessage(),e);
		}
		
		StatisticsLibrary sLibrary = null;
		try {
			try {
				DAOFactory daoFactory = DAOFactory.getInstance(logSql);
				
				org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM = 
					(org.openspcoop2.core.statistiche.dao.IServiceManager) 
						daoFactory.getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(), 
							logSql);
					
				org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM = 
					(org.openspcoop2.core.transazioni.dao.IServiceManager) 
						daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), 
							logSql);
				
				org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM = null;
				org.openspcoop2.core.plugins.dao.IServiceManager pluginsBaseSM = null;
				org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM = null;
				org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM = null;
				
				if(generatorProperties.isGenerazioneStatisticheCustom()){
					
					pluginsStatisticheSM = (org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager) 
						daoFactory.getServiceManager(
							org.openspcoop2.monitor.engine.config.statistiche.utils.ProjectInfo.getInstance(), 
								logSql);
					
					pluginsBaseSM = (org.openspcoop2.core.plugins.dao.IServiceManager) 
						daoFactory.getServiceManager(
							org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),
								logSql);
					
					utilsSM = (org.openspcoop2.core.commons.search.dao.IServiceManager) 
						daoFactory.getServiceManager(
							org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), 
								logSql);
					
					if(generatorProperties.isAnalisiTransazioniCustom()){
						
						pluginsTransazioniSM = (org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) 
							daoFactory.getServiceManager(
								org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance(), 
									logSql);
						
					}
					
				}
				
				sLibrary = new StatisticsLibrary(statisticsConfig, statisticheSM, transazioniSM, 
						pluginsStatisticheSM, pluginsBaseSM, utilsSM, pluginsTransazioniSM);
			}catch(Exception e){
				throw new UtilsException("Errore durante la generazione delle statistiche (InitConnessioni): "+e.getMessage(),e);
			}
			
			try {
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
				}	
			}catch(Exception e){
				throw new UtilsException("Errore durante la generazione delle statistiche: "+e.getMessage(),e);
			}
		}finally {
			if(sLibrary!=null) {
				sLibrary.close();
			}
		}
	}

	private static void doError(Logger logCore,String msg,Exception e) throws UtilsException {
		String msgErrore = msg+": " + e.getMessage();
		logCore.error(msgErrore,e);
		throw new UtilsException(msgErrore,e);
	}
}
