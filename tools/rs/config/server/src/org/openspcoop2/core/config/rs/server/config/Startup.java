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


package org.openspcoop2.core.config.rs.server.config;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.monitor.engine.alarm.AlarmConfigProperties;
import org.openspcoop2.monitor.engine.alarm.AlarmEngineConfig;
import org.openspcoop2.monitor.engine.alarm.AlarmManager;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.core.byok.BYOKMapProperties;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.certificate.ocsp.OCSPManager;
import org.openspcoop2.utils.json.YamlSnakeLimits;
import org.openspcoop2.utils.properties.MapProperties;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.security.ProviderUtils;
import org.openspcoop2.web.ctrlstat.core.Connettori;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.slf4j.Logger;
/**
 * Questa classe si occupa di inizializzare tutte le risorse necessarie al webService.
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class Startup implements ServletContextListener {

	private static Logger log = null;
	public static Logger getLog() {
		return log;
	}

	private static InitRuntimeConfigReader initRuntimeConfigReader;
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(Startup.log!=null)
			Startup.log.info("Undeploy webService in corso...");

		 if(initRuntimeConfigReader!=null) {
				initRuntimeConfigReader.setStop(true);
		 }
		
		if(Startup.log!=null)
			Startup.log.info("Undeploy webService effettuato.");

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		Startup.initLog();
		
		Startup.initResources();

	}
	

	// LOG
	
	private static boolean initializedLog = false;
	
	public static boolean isInitializedLog() {
		return initializedLog;
	}

	public static synchronized String initLog(){
		
		String confDir = null;
		try{
			InputStream is = Startup.class.getResourceAsStream("/rs-api-config.properties");
			try{
				if(is!=null){
					Properties p = new Properties();
					p.load(is);
					confDir = p.getProperty("confDirectory");
					if(confDir!=null){
						confDir = confDir.trim();
					}
				}
			}finally{
				try{
					if(is!=null){
						is.close();
					}
				}catch(Exception eClose){
					// close
				}
			}

		}catch(Exception e){
			// ignore
		}
		
		if(!Startup.initializedLog){
			
			try{
				Startup.log = LoggerWrapperFactory.getLogger(Startup.class);
				LoggerProperties.initialize(Startup.log, confDir, null);
				Startup.initializedLog = true;
				Startup.log = LoggerProperties.getLoggerCore();
				
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
		}
		
		return confDir;
	}
	
	
	// RESOURCES
	
	private static boolean initializedResources = false;
	
	public static synchronized void initResources(){
		if(!Startup.initializedResources){
			
			String confDir = Startup.initLog();
			
			Startup.log.info("Inizializzazione rs api config in corso...");
			
			if(!ServerProperties.initialize(confDir,Startup.log)){
				return;
			}
			ServerProperties serverProperties = null;
			try {
				serverProperties = ServerProperties.getInstance();
			} catch (Exception e) {
				doError("Errore durante l'inizializzazione del serverProperties",e);
			}
			
			// Map (environment)
			try {
				String mapConfig = serverProperties.getEnvMapConfig();
				if(StringUtils.isNotEmpty(mapConfig)) {
					Startup.log.info("Inizializzazione environment in corso...");
					MapProperties.initialize(Startup.log, mapConfig, serverProperties.isEnvMapConfigRequired());
					MapProperties mapProperties = MapProperties.getInstance();
					mapProperties.initEnvironment();
					String msgInit = "Environment inizializzato con le variabili definite nel file '"+mapConfig+"'"+
							"\n\tJavaProperties: "+mapProperties.getJavaMap().keys()+
							"\n\tEnvProperties: "+mapProperties.getEnvMap().keys()+
							"\n\tObfuscateMode: "+mapProperties.getObfuscateModeDescription()+
							"\n\tObfuscatedJavaKeys: "+mapProperties.getObfuscatedJavaKeys()+
							"\n\tObfuscatedEnvKeys: "+mapProperties.getObfuscatedEnvKeys();
					Startup.log.info(msgInit);
				}
			} catch (Exception e) {
				doError("Errore durante l'inizializzazione dell'ambiente",e);
			}
			
			// Load Security Provider
			Startup.log.info("Inizializzazione security provider...");
			try {
				if(serverProperties.isSecurityLoadBouncyCastleProvider()) {
					ProviderUtils.addBouncyCastleAfterSun(true);
					Startup.log.info("Aggiunto Security Provider org.bouncycastle.jce.provider.BouncyCastleProvider");
				}
			} catch (Exception e) {
				doError("Errore durante l'inizializzazione dei security provider",e);
			}
			Startup.log.info("Inizializzazione security provider effettuata con successo");
			
			// inizializzo HSM Manager
			try {
				String hsmConfig = serverProperties.getHSMConfigurazione();
				if(StringUtils.isNotEmpty(hsmConfig)) {
					Startup.log.info("Inizializzazione HSM in corso...");
					File f = new File(hsmConfig);
					HSMManager.init(f, serverProperties.isHSMRequired(), log, false);
					HSMUtils.setHsmConfigurableKeyPassword(serverProperties.isHSMKeyPasswordConfigurable());
					Startup.log.info("Inizializzazione HSM effettuata con successo");
				}
			} catch (Exception e) {
				doError("Errore durante l'inizializzazione del manager HSM",e);
			}
			
			// inizializzo BYOK Manager
			BYOKManager byokManager = null;
			try {
				String byokConfig = serverProperties.getBYOKConfigurazione();
				if(StringUtils.isNotEmpty(byokConfig)) {
					Startup.log.info("Inizializzazione BYOK in corso...");
					File f = new File(byokConfig);
					BYOKManager.init(f, serverProperties.isBYOKRequired(), log);
					byokManager = BYOKManager.getInstance();
					String msgInit = "Gestore BYOK inizializzato;"+
							"\n\tHSM registrati: "+byokManager.getKeystoreTypes()+
							"\n\tSecurityEngine registrati: "+byokManager.getSecurityEngineTypes()+
							"\n\tGovWaySecurityEngine: "+byokManager.getSecurityEngineGovWayDescription();
					Startup.log.info(msgInit);
				}
			} catch (Exception e) {
				doError("Errore durante l'inizializzazione del manager BYOK",e);
			}
			
			// inizializzo OCSP Manager
			Startup.log.info("Inizializzazione OCSP in corso...");
			try {
				String ocspConfig = serverProperties.getOCSPConfigurazione();
				if(StringUtils.isNotEmpty(ocspConfig)) {
					File f = new File(ocspConfig);
					OCSPManager.init(f, serverProperties.isOCSPRequired(), serverProperties.isOCSPLoadDefault(), log);
				}
			} catch (Exception e) {
				doError("Errore durante l'inizializzazione del manager OCSP",e);
			}
			Startup.log.info("Inizializzazione OCSP effettuata con successo");
			
			// Secrets (environment)
			boolean reInitSecretMaps = false;
			try {
				String secretsConfig = serverProperties.getBYOKEnvSecretsConfig();
				if(byokManager!=null && StringUtils.isNotEmpty(secretsConfig)) {
					Startup.log.info("Inizializzazione secrets in corso...");
					String securityPolicy = BYOKManager.getSecurityEngineGovWayInstance();
					String securityRemotePolicy = BYOKManager.getSecurityRemoteEngineGovWayInstance();
					
					Map<String, Object> dynamicMap = new HashMap<>();
					DynamicInfo dynamicInfo = new  DynamicInfo();
					DynamicUtils.fillDynamicMap(log, dynamicMap, dynamicInfo);
					if(byokManager.isBYOKRemoteGovWayNodeUnwrapConfig(securityPolicy)) {
						// i secrets cifrati verranno riletti quando i nodi sono attivi (verificato in InitRuntimeConfigReader)
						reInitSecretMaps = true;
						securityPolicy = null;
						securityRemotePolicy = null;
					}
					
					BYOKMapProperties.initialize(Startup.log, secretsConfig, serverProperties.isBYOKEnvSecretsConfigRequired(), 
							securityPolicy, securityRemotePolicy, 
							dynamicMap, true);
					BYOKMapProperties secretsProperties = BYOKMapProperties.getInstance();
					secretsProperties.initEnvironment();
					String msgInit = "Environment inizializzato con i secrets definiti nel file '"+secretsConfig+"'"+
							"\n\tJavaProperties: "+secretsProperties.getJavaMap().keys()+
							"\n\tEnvProperties: "+secretsProperties.getEnvMap().keys()+
							"\n\tObfuscateMode: "+secretsProperties.getObfuscateModeDescription();
					Startup.log.info(msgInit);
				}
			} catch (Exception e) {
				doError("Errore durante l'inizializzazione dell'ambiente (secrets)",e);
			}		
			
			// Database
			if(!DatasourceProperties.initialize(confDir,Startup.log)){
				return;
			}
			try {
				if(!org.openspcoop2.web.ctrlstat.config.DatasourceProperties.initialize(DatasourceProperties.getInstance().getPropertiesConsole(),Startup.log)){
					return;
				}
			}catch(Exception e) {
				Startup.log.error("Inizializzazione database console fallita: "+e.getMessage(),e);
			}
			
			// Extended Manager
			Startup.log.info("Inizializzazione ExtendedInfoManager in corso...");
			try{
				ExtendedInfoManager.initialize(new Loader(), null, null, null);
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			Startup.log.info("Inizializzazione ExtendedInfoManager effettuata con successo");
			
			// inizializza nodi runtime
			Startup.log.info("Inizializzazione NodiRuntime in corso...");
			try {
				ConfigurazioneNodiRuntime.initialize(serverProperties.getConfigurazioneNodiRuntime());
			} catch (Exception e) {
				doError("Errore durante l'inizializzazione del gestore dei nodi run",e);
			}
			Startup.log.info("Inizializzazione NodiRuntime effettuata con successo");
			
			// Risorse Statiche
			Startup.log.info("Inizializzazione Risorse Statiche Console in corso...");
			try {
				ConsoleHelper.setTipoInterfacciaAPI(InterfaceType.STANDARD);
				
				ControlStationCore.setUtenzePasswordEncryptEngineApiMode(serverProperties.getUtenzeCryptConfig());
				
				ControlStationCore.setApplicativiPasswordEncryptEngineApiMode(serverProperties.getApplicativiCryptConfig());
				ControlStationCore.setApplicativiApiKeyPasswordGeneratedLengthApiMode(serverProperties.getApplicativiApiKeyPasswordGeneratedLength());
				if(serverProperties.isApplicativiBasicPasswordEnableConstraints()) {
					ControlStationCore.setApplicativiPasswordVerifierEngineApiMode(serverProperties.getApplicativiPasswordVerifier());
				}
				
				ControlStationCore.setSoggettiPasswordEncryptEngineApiMode(serverProperties.getSoggettiCryptConfig());
				ControlStationCore.setSoggettiApiKeyPasswordGeneratedLengthApiMode(serverProperties.getSoggettiApiKeyPasswordGeneratedLength());
				if(serverProperties.isSoggettiBasicPasswordEnableConstraints()) {
					ControlStationCore.setSoggettiPasswordVerifierEngineApiMode(serverProperties.getSoggettiPasswordVerifier());
				}
				
				Properties yamlSnakeLimits = serverProperties.getApiYamlSnakeLimits();
				if(yamlSnakeLimits!=null && !yamlSnakeLimits.isEmpty()) {
					YamlSnakeLimits.initialize(Startup.log, yamlSnakeLimits);
				}
				
				ControlStationCore.setIsSoggettiApplicativiCredenzialiBasicPermitSameCredentialsApiMode(serverProperties.isSoggettiApplicativiCredenzialiBasicPermitSameCredentials());
				ControlStationCore.setIsSoggettiApplicativiCredenzialiSslPermitSameCredentialsApiMode(serverProperties.isSoggettiApplicativiCredenzialiSslPermitSameCredentials());
				ControlStationCore.setIsSoggettiApplicativiCredenzialiPrincipalPermitSameCredentialsApiMode(serverProperties.isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials());
				
			} catch (Exception e) {
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			Startup.log.info("Inizializzazione Risorse Statiche Console effettuata con successo");
			
			Startup.log.info("Inizializzazione Connettori in corso...");
			try{
				Connettori.initialize(log, true, confDir, ServerProperties.getInstance().getProtocolloDefault());
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			Startup.log.info("Inizializzazione Connettori effettuata con successo");
			
			// Allarmi
			try {
				if(ServerProperties.getInstance().isConfigurazioneAllarmiEnabled()!=null && ServerProperties.getInstance().isConfigurazioneAllarmiEnabled().booleanValue()) {
					Startup.log.info("Inizializzazione Allarmi in corso...");
					AlarmEngineConfig alarmEngineConfig = AlarmConfigProperties.getAlarmConfiguration(log, ServerProperties.getInstance().getAllarmiConfigurazione(), ServerProperties.getInstance().getConfDirectory());
					AlarmManager.setAlarmEngineConfig(alarmEngineConfig);
					CostantiDB.setAllarmiEnabled(true);
					Startup.log.info("Inizializzazione Allarmi effettuata con successo");
				}
			} catch (Exception e) {
				doError("Errore durante l'inizializzazione degli allarmi",e);
			}
				
			// InitRuntimeConfigReader
			if(reInitSecretMaps) {
				try{
					initRuntimeConfigReader = new InitRuntimeConfigReader(serverProperties, ConfigurazioneNodiRuntime.getConfigurazioneNodiRuntime(), reInitSecretMaps);
					initRuntimeConfigReader.start();
					Startup.log.info("RuntimeConfigReader avviato con successo.");
				} catch (Exception e) {
					/**doError("Errore durante l'inizializzazione del RuntimeConfigReader",e);*/
					// non sollevo l'eccezione, e' solo una informazione informativa, non voglio mettere un vincolo che serve per forza un nodo acceso
					Startup.log.error("Errore durante l'inizializzazione del RuntimeConfigReader: "+e.getMessage(),e);
				}
			}
			
			Startup.initializedResources = true;
			
			Startup.log.info("Inizializzazione rs api config effettuata con successo.");
		}
	}

	private static void doError(String msg,Exception e) {
		String msgErrore = msg+": " + e.getMessage();
		Startup.log.error(msgErrore,e);
		throw new UtilsRuntimeException(msgErrore,e);
	}
}
