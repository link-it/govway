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


package org.openspcoop2.core.monitor.rs.server.config;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.transazioni.utils.DumpUtils;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.core.byok.BYOKMapProperties;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.properties.MapProperties;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.security.ProviderUtils;
import org.openspcoop2.web.lib.mvc.security.SecurityProperties;
import org.openspcoop2.web.lib.mvc.security.Validatore;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.core.Utility;
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
		try(InputStream is = Startup.class.getResourceAsStream("/rs-api-monitor.properties");){
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
			
			Startup.log.info("Inizializzazione rs api monitor in corso...");
			
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
			
			// Secrets (environment)
			boolean reInitSecretMaps = false;
			try {
				String secretsConfig = serverProperties.getBYOKEnvSecretsConfig();
				if(byokManager!=null && StringUtils.isNotEmpty(secretsConfig)) {
					Startup.log.info("Inizializzazione secrets in corso...");
					
					boolean useSecurityEngine = true;
					Map<String, Object> dynamicMap = new HashMap<>();
					DynamicInfo dynamicInfo = new  DynamicInfo();
					DynamicUtils.fillDynamicMap(log, dynamicMap, dynamicInfo);
					if(byokManager.isBYOKRemoteGovWayNodeUnwrapConfig()) {
						// i secrets cifrati verranno riletti quando i nodi sono attivi (verificato in InitRuntimeConfigReader)
						reInitSecretMaps = true;
						useSecurityEngine = false;
					}
					
					BYOKMapProperties.initialize(Startup.log, secretsConfig, serverProperties.isBYOKEnvSecretsConfigRequired(), 
							useSecurityEngine, 
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
			Startup.log.info("Inizializzazione DBManager in corso...");
			try{
				DatasourceProperties dbProperties = DatasourceProperties.getInstance();
				DBManager.initialize(dbProperties.getConfigDataSource(), dbProperties.getConfigDataSourceContext(), dbProperties.getConfigTipoDatabase(),
						dbProperties.getTracceDataSource(), dbProperties.getTracceDataSourceContext(), dbProperties.getTracceTipoDatabase(),
						dbProperties.getStatisticheDataSource(), dbProperties.getStatisticheDataSourceContext(), dbProperties.getStatisticheTipoDatabase(),
						dbProperties.isShowSql());
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			Startup.log.info("Inizializzazione DBManager effettuata con successo");
			
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
				boolean configFileRequired = false;
				ConfigurazioneNodiRuntime.initialize(serverProperties.getConfigurazioneNodiRuntime(), configFileRequired);
			} catch (Exception e) {
				doError("Errore durante l'inizializzazione del gestore dei nodi run",e);
			}
			Startup.log.info("Inizializzazione NodiRuntime effettuata con successo");
			
			// Protocol Factory Manager
			Startup.log.info("Inizializzazione ProtocolFactoryManager in corso...");
			ServerProperties properties = null;
			try {
				properties = ServerProperties.getInstance();
				ConfigurazionePdD configPdD = new ConfigurazionePdD();
				configPdD.setAttesaAttivaJDBC(-1);
				configPdD.setCheckIntervalJDBC(-1);
				configPdD.setLoader(new Loader(Startup.class.getClassLoader()));
				configPdD.setLog(Startup.log);
				ProtocolFactoryManager.initialize(Startup.log, configPdD,
						properties.getProtocolloDefault());
			} catch (Exception e) {
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			Startup.log.info("ProtocolFactoryManager DBManager effettuata con successo");
			
			initResourceConsole(properties);
			
			initSogliaDimensioneMessaggi(properties);
						
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
			
			Startup.log.info("Inizializzazione rs api monitor effettuata con successo.");
		}
	}

	
	private static void initResourceConsole(ServerProperties properties) {
		Startup.log.info("Inizializzazione Risorse Statiche Console in corso...");
		DBManager dbManager = DBManager.getInstance();
		Connection connection = null;
		try {
			connection = dbManager.getConnectionConfig();
			ServiceManagerProperties smp = dbManager.getServiceManagerPropertiesConfig();
			
			Logger logSql = LoggerProperties.getLoggerDAO();
			
			LoginBean lb = new LoginBean(connection, true, smp, logSql);
			
			Utility.setStaticConfigurazioneGenerale(lb.getConfigurazioneGenerale());
			
			boolean multitenantAbilitato = Utility.isMultitenantAbilitato();
			Utility.setStaticFiltroDominioAbilitato(multitenantAbilitato);
			
			DriverRegistroServiziDB driverDB = new DriverRegistroServiziDB(connection, logSql, DatasourceProperties.getInstance().getConfigTipoDatabase());
			
			List<String> idsPdd = getAllIdPorteDominioTipoOperativo(driverDB);
			if(idsPdd!=null && !idsPdd.isEmpty()) {
				for (String idPdd : idsPdd) {
					List<IDSoggetto> idsSoggetti = getAllSoggettiByPdd(driverDB, idPdd);
					if(idsSoggetti!=null && !idsSoggetti.isEmpty()) {
						for (IDSoggetto idSoggetto : idsSoggetti) {
							Utility.putIdentificativoPorta(idSoggetto.getTipo(), idSoggetto.getNome(), driverDB.getSoggetto(idSoggetto).getIdentificativoPorta());
						}
					}
				}
			}
		
			Properties consoleSecurityConfiguration = properties.getConsoleSecurityConfiguration();
			SecurityProperties.init(consoleSecurityConfiguration, log);
			Validatore.init(SecurityProperties.getInstance(), log);
			
			Startup.log.info("Inizializzazione Risorse Statiche Console effettuata con successo");
		} 
		catch (Exception e) {
			throw new UtilsRuntimeException(e.getMessage(),e);
		}
		finally {
			dbManager.releaseConnectionConfig(connection);
		}
	}
	private static List<String> getAllIdPorteDominioTipoOperativo(DriverRegistroServiziDB driverDB) throws DriverRegistroServiziException{
		FiltroRicerca filtroRicercaPdd = new FiltroRicerca();
		filtroRicercaPdd.setTipo(PddTipologia.OPERATIVO.toString());
		List<String> idsPdd = null;
		try {
			idsPdd = driverDB.getAllIdPorteDominio(filtroRicercaPdd);
		}catch(DriverRegistroServiziNotFound notFound) {	
			// ignore
		}
		return idsPdd;
	}
	private static List<IDSoggetto> getAllSoggettiByPdd(DriverRegistroServiziDB driverDB, String idPdd) throws DriverRegistroServiziException{
		FiltroRicercaSoggetti filtroSoggetti = new FiltroRicercaSoggetti();
		filtroSoggetti.setNomePdd(idPdd);
		List<IDSoggetto> idsSoggetti = null;
		try {
			idsSoggetti = driverDB.getAllIdSoggetti(filtroSoggetti);
		}catch(DriverRegistroServiziNotFound notFound) {		
			// ignore
		}
		return idsSoggetti;
	}
	
	private static void initSogliaDimensioneMessaggi(ServerProperties properties) {
		Startup.log.info("Inizializzazione Soglia per Dimensione Messaggi in corso...");
		try {
			DumpUtils.setThreshold_readInMemory(properties.getTransazioniDettaglioVisualizzazioneMessaggiThreshold());
		} catch (Exception e) {
			throw new UtilsRuntimeException(e.getMessage(),e);
		}
		Startup.log.info("Inizializzazione Soglia per Dimensione Messaggi effettuata con successo");
	}
	
	private static void doError(String msg,Exception e) {
		String msgErrore = msg+": " + e.getMessage();
		Startup.log.error(msgErrore,e);
		throw new UtilsRuntimeException(msgErrore,e);
	}
}
