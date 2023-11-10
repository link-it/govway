/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.monitor.engine.alarm.AlarmConfigProperties;
import org.openspcoop2.monitor.engine.alarm.AlarmEngineConfig;
import org.openspcoop2.monitor.engine.alarm.AlarmManager;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.ocsp.OCSPManager;
import org.openspcoop2.utils.json.YamlSnakeLimits;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.web.ctrlstat.core.Connettori;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.security.SecurityProperties;
import org.openspcoop2.web.lib.mvc.security.Validatore;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.slf4j.Logger;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
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
	
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(Startup.log!=null)
			Startup.log.info("Undeploy webService in corso...");

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
		try(InputStream is = Startup.class.getResourceAsStream("/rs-api-config.properties");){
			if(is!=null){
				Properties p = new Properties();
				p.load(is);
				confDir = p.getProperty("confDirectory");
				if(confDir!=null){
					confDir = confDir.trim();
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
	public static boolean isInitializedResources() {
		return initializedResources;
	}

	public static synchronized void initResources(){
		if(!Startup.initializedResources){
			
			String confDir = Startup.initLog();
			
			Startup.log.info("Inizializzazione rs api config in corso...");
			
			if(!ServerProperties.initialize(confDir,Startup.log)){
				return;
			}
			
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
			
			Startup.log.info("Inizializzazione ExtendedInfoManager in corso...");
			try{
				ExtendedInfoManager.initialize(new Loader(), null, null, null);
			}catch(Exception e){
				logAndThrow(e.getMessage(),e);
			}
			Startup.log.info("Inizializzazione ExtendedInfoManager effettuata con successo");
						
			initConsoleResources();
			
			initConnettori(confDir);
			
			initAllarmi();
			
			initHSM();
			
			initOCSP();
			
			
			Startup.initializedResources = true;
			
			Startup.log.info("Inizializzazione rs api config effettuata con successo.");
		}
	}
	private static void initConsoleResources() {
		
		Startup.log.info("Inizializzazione Risorse Statiche Console in corso...");
		try {
						
			ServerProperties serverProperties = ServerProperties.getInstance();
			
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
			
			Properties consoleSecurityConfiguration = serverProperties.getConsoleSecurityConfiguration();
			SecurityProperties.init(consoleSecurityConfiguration, log);
			Validatore.init(SecurityProperties.getInstance(), log);
			
		} catch (Exception e) {
			logAndThrow(e.getMessage(),e);
		}
		Startup.log.info("Inizializzazione Risorse Statiche Console effettuata con successo");
	}
	private static void initConnettori(String confDir) {
		Startup.log.info("Inizializzazione Connettori in corso...");
		try{
			Connettori.initialize(log, true, confDir, ServerProperties.getInstance().getProtocolloDefault());
		}catch(Exception e){
			logAndThrow(e.getMessage(),e);
		}
		Startup.log.info("Inizializzazione Connettori effettuata con successo");
	}
	private static void initAllarmi() {
		try {
			if(ServerProperties.getInstance().isConfigurazioneAllarmiEnabled()) {
				Startup.log.info("Inizializzazione Allarmi in corso...");
				AlarmEngineConfig alarmEngineConfig = AlarmConfigProperties.getAlarmConfiguration(log, ServerProperties.getInstance().getAllarmiConfigurazione(), ServerProperties.getInstance().getConfDirectory());
				AlarmManager.setAlarmEngineConfig(alarmEngineConfig);
				CostantiDB.setAllarmiEnabled(true);
				Startup.log.info("Inizializzazione Allarmi effettuata con successo");
			}
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione degli allarmi: " + e.getMessage();
			logAndThrow(msgErrore,e);
		}
	}
	private static void initHSM() {
		Startup.log.info("Inizializzazione HSM in corso...");
		try {
			ServerProperties serverProperties = ServerProperties.getInstance();
			
			String hsmConfig = serverProperties.getHSMConfigurazione();
			if(StringUtils.isNotEmpty(hsmConfig)) {
				File f = new File(hsmConfig);
				HSMManager.init(f, serverProperties.isHSMRequired(), log, false);
			}
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione del manager HSM: " + e.getMessage();
			logAndThrow(msgErrore,e);
		}
		Startup.log.info("Inizializzazione HSM effettuata con successo");
	}
	private static void initOCSP() {
		Startup.log.info("Inizializzazione OCSP in corso...");
		try {
			ServerProperties serverProperties = ServerProperties.getInstance();
			
			String ocspConfig = serverProperties.getOCSPConfigurazione();
			if(StringUtils.isNotEmpty(ocspConfig)) {
				File f = new File(ocspConfig);
				OCSPManager.init(f, serverProperties.isOCSPRequired(), serverProperties.isOCSPLoadDefault(), log);
			}
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione del manager OCSP: " + e.getMessage();
			logAndThrow(msgErrore,e);
		}
		Startup.log.info("Inizializzazione OCSP effettuata con successo");
	}
	
	private static void logAndThrow(String msgErrore, Exception e) {
		Startup.log.error(msgErrore,e);
		throw new UtilsRuntimeException(msgErrore,e);
	}

}
