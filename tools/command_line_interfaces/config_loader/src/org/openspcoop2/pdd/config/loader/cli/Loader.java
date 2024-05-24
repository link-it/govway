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

package org.openspcoop2.pdd.config.loader.cli;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.dynamic.CorePluginLoader;
import org.openspcoop2.monitor.engine.dynamic.PluginLoader;
import org.openspcoop2.pdd.core.byok.BYOKMapProperties;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.protocol.basic.registry.ConfigIntegrationReader;
import org.openspcoop2.protocol.basic.registry.RegistryReader;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.archive.ArchiveValidator;
import org.openspcoop2.protocol.engine.archive.DeleterArchiveUtils;
import org.openspcoop2.protocol.engine.archive.ImportInformationMissingCollection;
import org.openspcoop2.protocol.engine.archive.ImporterArchiveUtils;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoDelete;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImport;
import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchiveModeType;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.archive.MapPlaceholder;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.utils.properties.MapProperties;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.security.ProviderUtils;
import org.openspcoop2.web.ctrlstat.core.Connettori;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiveEngine;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.slf4j.Logger;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 *  Loader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Loader {

	private static Logger logCore = LoggerWrapperFactory.getLogger(Loader.class);
	private static void logCoreDebug(String msg) {
		logCore.debug(msg);
	}
	private static void logCoreInfo(String msg) {
		logCore.info(msg);
	}
	private static void logCoreError(String msg, Exception e) {
		logCore.error(msg,e);
	}
	private static Logger logSql = LoggerWrapperFactory.getLogger(Loader.class);
	
	public static void main(String[] args) throws CoreException, SQLException {
		
		Connection connectionSQL = null;
		try {
		
			// Logger
			initLogger();
			logCore=LoggerWrapperFactory.getLogger("config_loader.core");	
			logSql=LoggerWrapperFactory.getLogger("config_loader.sql");	
			
			
			logCoreDebug("Raccolta parametri in corso...");
			
			// costanti
			ArchiveModeType modeType = org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_ARCHIVE_MODE_TYPE;
			ArchiveMode mode = org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_ARCHIVE_MODE;
			boolean validateDocuments = true;
			MapPlaceholder importInformationMissingGlobalPlaceholder = new MapPlaceholder();
			boolean showCorrelazioneAsincronaInAccordi = true;
			boolean isShowGestioneWorkflowStatoDocumenti = false;
			boolean isShowAccordiColonnaAzioni = false;
			boolean smista = false;
			
			// properties
			LoaderProperties loaderProperties = LoaderProperties.getInstance();
			String confDir = null; // non sembra servire
			String protocolloDefault = loaderProperties.getProtocolloDefault();
			String importNomePddOperativa = loaderProperties.getNomePddOperativa();
			String importTipoPddArchivi = loaderProperties.getTipoPddArchivio();
			String userLogin = loaderProperties.getUtente();
			boolean importDeletePolicyConfig = loaderProperties.isPolicyEnable();
			boolean importDeletePluginConfig = loaderProperties.isPluginEnable();
			boolean importCheckPluginReferences = loaderProperties.isPluginCheckReferences();
			boolean importConfig = loaderProperties.isConfigurazioneGeneraleEnable();
			boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto = loaderProperties.isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto();
			boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto = loaderProperties.isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto();
			boolean isSoggettiApplicativiCredenzialiBasicPermitSameCredentials = loaderProperties.isSoggettiApplicativiCredenzialiBasicPermitSameCredentials();
			boolean isSoggettiApplicativiCredenzialiSslPermitSameCredentials = loaderProperties.isSoggettiApplicativiCredenzialiSslPermitSameCredentials();
			boolean isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials = loaderProperties.isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials();

			
			// args
			boolean delete = false;
			boolean updateAbilitato = true;
			String utilizzoErrato = "Usage error: Loader <operationType> <archivePath>";
			if(args.length<2 || args[0]==null || args[1]==null) {
				throw new CoreException(utilizzoErrato);
			}
			LoaderOperationType opType = parseOperationType(utilizzoErrato, args);
			switch (opType) {
			case CREATE:
				delete=false;
				updateAbilitato = false;
				break;
			case CREATE_UPDATE:
				delete=false;
				updateAbilitato = true;
				break;
			case DELETE:
				delete=true;
				updateAbilitato = false;
				break;
			}
			
			String filePath = args[1].trim();
			File fFilePath = new File(filePath);
			String prefix = "L'archivio indicato ("+fFilePath.getAbsolutePath()+") ";
			if(!fFilePath.exists()) {
				throw new CoreException(prefix+"non esiste");
			}
			if(!fFilePath.canRead()) {
				throw new CoreException(prefix+"non è accessibile");
			}
			if(!fFilePath.isFile()) {
				throw new CoreException(prefix+"non è un file");
			}
			byte [] archiveFile = FileSystemUtilities.readBytesFromFile(fFilePath);
			
			logCoreDebug("Raccolta parametri terminata");
			
			
			// Map (environment)
			initMap(loaderProperties);
			
			// Load Security Provider
			if(loaderProperties.isSecurityLoadBouncyCastleProvider()) {
				initBouncyCastle();
			}
			
			// inizializzo HSM Manager
			initHsm(loaderProperties);
			
			// inizializzo BYOK Manager
			BYOKManager byokManager = initBYOK(loaderProperties);
			
			// Secrets (environment)
			initSecrets(loaderProperties, byokManager);
			
			
			logCoreDebug("Inizializzazione connessione database in corso...");
			
			LoaderDatabaseProperties databaseProperties = LoaderDatabaseProperties.getInstance();
			String tipoDatabase = databaseProperties.getTipoDatabase();
			String driver = databaseProperties.getDriver();
			String username = databaseProperties.getUsername();
			String password = databaseProperties.getPassword();
			String connectionURL = databaseProperties.getConnectionUrl();

			org.openspcoop2.utils.resources.Loader.getInstance().newInstance(driver);
			if(username!=null && password!=null){
				connectionSQL = DriverManager.getConnection(connectionURL,username,password);
			}else{
				connectionSQL = DriverManager.getConnection(connectionURL);
			}
			
			String jndiName = "org.govway.datasource.console";
			DataSource ds = new SingleConnectionDataSource(connectionSQL, true); 
			System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
			System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
			bindDatasource(ds, jndiName);
			
			logCoreDebug("Inizializzazione connessione database terminata");
			
			
			
			logCoreDebug("Inizializzazione risorse libreria in corso...");
			
			ConfigurazionePdD configPdD = initProtocolFactory(protocolloDefault);
			
			initExtendedInfoManager();
			
			initCorePluginLoader(configPdD, loaderProperties);
			
			Properties p = new Properties();
			p.put("dataSource", jndiName);
			p.put("tipoDatabase", tipoDatabase);
			if(!org.openspcoop2.web.ctrlstat.config.DatasourceProperties.initialize(p,logCore)){
				throw new CoreException("Inizializzazione fallita");
			}
		
			initUtenze(loaderProperties);
			
			initConnettori(confDir, protocolloDefault);
						
			logCoreDebug("Inizializzazione risorse libreria terminata");

			

			logCoreDebug("Inizializzazione driver ...");
			
			// istanzio il driver
			DriverConfigurazioneDB driverConfigDB = new DriverConfigurazioneDB(connectionSQL, logSql, tipoDatabase);
			DriverRegistroServiziDB driverRegistroDB = new DriverRegistroServiziDB(connectionSQL, logSql,tipoDatabase);
						
			// Reader
			RegistryReader registryReader = new RegistryReader(driverRegistroDB,logSql);
			ConfigIntegrationReader configReader = new ConfigIntegrationReader(driverConfigDB,logSql);
						
			// istanzio driver per Plugins
			ServiceManagerProperties propertiesPlugins = new ServiceManagerProperties();
			propertiesPlugins.setDatabaseType(tipoDatabase);
			propertiesPlugins.setShowSql(true);
			org.openspcoop2.core.plugins.dao.jdbc.JDBCServiceManager jdbcServiceManagerPlugins = 
					new org.openspcoop2.core.plugins.dao.jdbc.JDBCServiceManager(connectionSQL, propertiesPlugins, logSql);
			
			// istanzio driver per ControlloTraffico
			ServiceManagerProperties propertiesControlloTraffico = new ServiceManagerProperties();
			propertiesControlloTraffico.setDatabaseType(tipoDatabase);
			propertiesControlloTraffico.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager jdbcServiceManagerControlloTraffico = 
					new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(connectionSQL, propertiesControlloTraffico, logSql);
			
			// istanzio driver per Allarmi
			ServiceManagerProperties propertiesAllarmi = new ServiceManagerProperties();
			propertiesAllarmi.setDatabaseType(tipoDatabase);
			propertiesAllarmi.setShowSql(true);
			org.openspcoop2.core.allarmi.dao.jdbc.JDBCServiceManager jdbcServiceManagerAllarmi = 
					new org.openspcoop2.core.allarmi.dao.jdbc.JDBCServiceManager(connectionSQL, propertiesAllarmi, logSql);

			// BYOK
			String securityRuntimePolicy = BYOKManager.getSecurityEngineGovWayInstance();
			if(securityRuntimePolicy!=null) {
				logCoreDebug("Inizializzazione driver BYOK con configurazione ["+loaderProperties.getBYOKConfigurazione()+"]");
				/**org.openspcoop2.pdd.core.byok.DriverBYOK driverBYOK = new org.openspcoop2.pdd.core.byok.DriverBYOK(logCore, securityRuntimePolicy);
				driverConfigDB.initialize(driverBYOK, true, false);
				driverRegistroDB.initialize(driverBYOK, true, false);*/
				// Automatico
			}
			
			// Istanzio ArchiviEngineControlStation
			ControlStationCore core = new ControlStationCore(true, null, protocolloDefault);
			ArchiviCore archiviCore = new ArchiviCore(core);
			ArchiveEngine importerEngine = new ArchiveEngine(driverRegistroDB, 
					driverConfigDB,
					jdbcServiceManagerPlugins,
					jdbcServiceManagerControlloTraffico,
					jdbcServiceManagerAllarmi,
					archiviCore, smista, userLogin);
			
			logCoreDebug("Inizializzazione driver terminata");
			
			logCoreInfo("Inizializzazione engine terminata");			
			
			
			
			// parsing
			logCoreInfo("Lettura archivio ...");
			IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolloDefault);
			IArchive archiveEngine = pf.createArchive();
			Archive archive = archiveEngine.importArchive(archiveFile, mode, modeType, registryReader, configReader,
					validateDocuments, importInformationMissingGlobalPlaceholder);
			logCoreInfo("Lettura archivio effettuata");
			
			
			// validate
			logCoreInfo("Validazione archivio ...");
			ArchiveValidator validator = new ArchiveValidator(registryReader);
			ImportInformationMissingCollection importInformationMissingCollection = new ImportInformationMissingCollection();
			validator.validateArchive(archive, protocolloDefault, validateDocuments, importInformationMissingCollection, userLogin, 
					showCorrelazioneAsincronaInAccordi,delete);
			logCoreInfo("Validazione archivio effettuata");
			
			// finalize
			logCoreInfo("Finalizzazione archivio ...");
			archiveEngine.finalizeImportArchive(archive, mode, modeType, registryReader, configReader,
					validateDocuments, importInformationMissingGlobalPlaceholder);
			logCoreInfo("Finalizzazione archivio effettuata");
			
			// store
			String esito = null;
			if(delete){
				
				logCoreInfo("Eliminazione in corso ...");
				
				DeleterArchiveUtils deleterArchiveUtils = 
						new DeleterArchiveUtils(importerEngine, logCore, userLogin,
								importDeletePolicyConfig,
								importDeletePluginConfig);
				
				ArchiveEsitoDelete esitoDelete = deleterArchiveUtils.deleteArchive(archive, userLogin);
				
				esito = archiveEngine.toString(esitoDelete, mode);
				
				logCoreInfo("Eliminazione completata");
				
			}else{
				
				logCoreInfo("Importazione (aggiornamento:"+updateAbilitato+") in corso ...");
				
				ImporterArchiveUtils importerArchiveUtils = 
						new ImporterArchiveUtils(importerEngine, logCore, userLogin, importNomePddOperativa, importTipoPddArchivi, 
								isShowGestioneWorkflowStatoDocumenti, updateAbilitato,
								importDeletePolicyConfig, 
								importDeletePluginConfig, importCheckPluginReferences,
								importConfig);
				
				ArchiveEsitoImport esitoImport = importerArchiveUtils.importArchive(archive, userLogin, 
						isShowAccordiColonnaAzioni,
						isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto, 
						isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto,
						isSoggettiApplicativiCredenzialiBasicPermitSameCredentials,
						isSoggettiApplicativiCredenzialiSslPermitSameCredentials,
						isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials);
				
				esito = archiveEngine.toString(esitoImport, mode);
				
				logCoreInfo("Importazione (aggiornamento:"+updateAbilitato+") completata");
			}
			
			logCoreInfo("Operazione terminata con esito:\n"+esito);
			
		}
		catch(Exception t) {
			if(logCore!=null) {
				logCore.error(t.getMessage(),t);
			}
			throw new CoreException(t.getMessage(),t);
		}
		finally {
			if(connectionSQL!=null) {
				connectionSQL.close();
			}
		}

	}

	private static void initLogger() throws CoreException {
		Properties propertiesLog4j = new Properties();
		try (InputStream inPropLog4j = Loader.class.getResourceAsStream("/config_loader.cli.log4j2.properties");){
			propertiesLog4j.load(inPropLog4j);
			LoggerWrapperFactory.setLogConfiguration(propertiesLog4j);
		} catch(java.lang.Exception e) {
			throw new CoreException("Impossibile leggere i dati dal file 'config_loader.cli.log4j2.properties': "+e.getMessage());
		} 
	}
	private static LoaderOperationType parseOperationType(String utilizzoErrato,String [] args) throws CoreException{
		LoaderOperationType opType = null;
		try {
			opType = LoaderOperationType.toEnumConstant(args[0].trim(), true);
		}catch(Exception e) {
			throw new CoreException(utilizzoErrato+"\nIl tipo di operazione indicato ("+args[0].trim()+") non è gestito, valori ammessi: "+
					LoaderOperationType.CREATE.getValue()+","+LoaderOperationType.CREATE_UPDATE.getValue()+","+LoaderOperationType.DELETE.getValue());
		}
		return opType;
	}
	private static InitialContext bindDatasource(DataSource ds, String jndiName) throws NamingException {
		InitialContext ic = new InitialContext();
		try{
			ic.bind(jndiName, ds);
		}catch(javax.naming.NameAlreadyBoundException already){
			//	capita in caso di più threads
		}
		return ic;
	}
	private static void initBouncyCastle() throws CoreException {
		try{
			ProviderUtils.addBouncyCastleAfterSun(true);
			logCoreInfo("Aggiunto Security Provider org.bouncycastle.jce.provider.BouncyCastleProvider");
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}
	private static void initMap(LoaderProperties loaderProperties) throws CoreException {
		try {
			String mapConfig = loaderProperties.getEnvMapConfig();
			if(StringUtils.isNotEmpty(mapConfig)) {
				logCoreInfo("Inizializzazione environment in corso...");
				MapProperties.initialize(logCore, mapConfig, loaderProperties.isEnvMapConfigRequired());
				MapProperties mapProperties = MapProperties.getInstance();
				mapProperties.initEnvironment();
				String msgInit = "Environment inizializzato con le variabili definite nel file '"+mapConfig+"'"+
						"\n\tJavaProperties: "+mapProperties.getJavaMap().keys()+
						"\n\tEnvProperties: "+mapProperties.getEnvMap().keys()+
						"\n\tObfuscateMode: "+mapProperties.getObfuscateModeDescription()+
						"\n\tObfuscatedJavaKeys: "+mapProperties.getObfuscatedJavaKeys()+
						"\n\tObfuscatedEnvKeys: "+mapProperties.getObfuscatedEnvKeys();
				logCoreInfo(msgInit);
			}
		} catch (Exception e) {
			doError("Errore durante l'inizializzazione dell'ambiente",e);
		}
	}
	private static void initHsm(LoaderProperties loaderProperties) throws CoreException {
		// inizializzo HSM Manager
		try {
			String hsmConfig = loaderProperties.getHSMConfigurazione();
			if(StringUtils.isNotEmpty(hsmConfig)) {
				logCoreInfo("Inizializzazione HSM in corso...");
				File f = new File(hsmConfig);
				HSMManager.init(f, loaderProperties.isHSMRequired(), logCore, false);
				HSMUtils.setHsmConfigurableKeyPassword(loaderProperties.isHSMKeyPasswordConfigurable());
				logCoreInfo("Inizializzazione HSM effettuata con successo");
			}
		} catch (Exception e) {
			doError("Errore durante l'inizializzazione del manager HSM",e);
		}
	}
	private static BYOKManager initBYOK(LoaderProperties loaderProperties) throws CoreException {
		BYOKManager byokManager = null;
		try {
			String byokConfig = loaderProperties.getBYOKConfigurazione();
			if(StringUtils.isNotEmpty(byokConfig)) {
				logCoreInfo("Inizializzazione BYOK in corso...");
				File f = new File(byokConfig);
				BYOKManager.init(f, loaderProperties.isBYOKRequired(), logCore);
				byokManager = BYOKManager.getInstance();
				
				logCoreInfo("Inizializzazione BYOK effettuata con successo");
			}
		} catch (Exception e) {
			doError("Errore durante l'inizializzazione del manager BYOK",e);
		}
		return byokManager;
	}
	private static void initSecrets(LoaderProperties loaderProperties, BYOKManager byokManager) throws CoreException {
		try {
			String secretsConfig = loaderProperties.getBYOKEnvSecretsConfig();
			if(byokManager!=null && StringUtils.isNotEmpty(secretsConfig)) {
				logCoreInfo("Inizializzazione secrets in corso...");
				String securityPolicy = BYOKManager.getSecurityEngineGovWayInstance();
				String securityRemotePolicy = BYOKManager.getSecurityRemoteEngineGovWayInstance();
				
				Map<String, Object> dynamicMap = new HashMap<>();
				DynamicInfo dynamicInfo = new  DynamicInfo();
				DynamicUtils.fillDynamicMap(logCore, dynamicMap, dynamicInfo);
				
				BYOKMapProperties.initialize(logCore, secretsConfig, loaderProperties.isBYOKEnvSecretsConfigRequired(), 
						securityPolicy, securityRemotePolicy, 
						dynamicMap, true);
				BYOKMapProperties secretsProperties = BYOKMapProperties.getInstance();
				secretsProperties.initEnvironment();
				String msgInit = "Environment inizializzato con i secrets definiti nel file '"+secretsConfig+"'"+
						"\n\tJavaProperties: "+secretsProperties.getJavaMap().keys()+
						"\n\tEnvProperties: "+secretsProperties.getEnvMap().keys()+
						"\n\tObfuscateMode: "+secretsProperties.getObfuscateModeDescription();
				logCoreInfo(msgInit);
			}
		} catch (Exception e) {
			doError("Errore durante l'inizializzazione dell'ambiente (secrets)",e);
		}	
	}
	private static ConfigurazionePdD initProtocolFactory(String protocolloDefault) throws CoreException {
		ConfigurazionePdD configPdD = null;
		try {
			configPdD = new ConfigurazionePdD();
			configPdD.setAttesaAttivaJDBC(-1);
			configPdD.setCheckIntervalJDBC(-1);
			configPdD.setLoader(new org.openspcoop2.utils.resources.Loader(Loader.class.getClassLoader()));
			configPdD.setLog(logCore);
			ProtocolFactoryManager.initialize(logCore, configPdD,
					protocolloDefault);
		} catch (Exception e) {
			throw new CoreException("Errore (InitConfigurazione - ProtocolFactoryManager): "+e.getMessage(),e);
		}
		return configPdD;
	}
	private static void initExtendedInfoManager() throws CoreException {
		try{
			ExtendedInfoManager.initialize(new org.openspcoop2.utils.resources.Loader(Loader.class.getClassLoader()), null, null, null);
		}catch(Exception e){
			throw new CoreException("Inizializzazione [ExtendedInfoManager] fallita",e);
		}
	}
	private static void initCorePluginLoader(ConfigurazionePdD configPdD, LoaderProperties loaderProperties) throws CoreException {
		try{
			CorePluginLoader.initialize(configPdD.getLoader(), logSql,
					PluginLoader.class,
					new LoaderRegistroPluginsService(logSql),
					loaderProperties.getPluginSeconds());
		}catch(Exception e){
			throw new CoreException("Inizializzazione [PluginManager] fallita",e);
		}
	}
	private static void initUtenze(LoaderProperties loaderProperties) throws CoreException {
		try {
			CryptConfig utenzeCryptConfig = new CryptConfig(loaderProperties.getUtenzePassword());
			CryptConfig applicativiCryptConfig = new CryptConfig(loaderProperties.getApplicativiPassword());
			int applicativiApiKeyPasswordGeneratedLength=loaderProperties.getApplicativiApiKeyPasswordGeneratedLength();
			boolean applicativiBasicPasswordEnableConstraints=loaderProperties.isApplicativiBasicPasswordEnableConstraints();
			CryptConfig soggettiCryptConfig = new CryptConfig(loaderProperties.getSoggettiPassword());
			int soggettiApiKeyPasswordGeneratedLength=loaderProperties.getSoggettiApiKeyPasswordGeneratedLength();
			boolean soggettiBasicPasswordEnableConstraints=loaderProperties.isSoggettiBasicPasswordEnableConstraints();
			
			ControlStationCore.setUtenzePasswordEncryptEngineApiMode(utenzeCryptConfig);
			
			ControlStationCore.setApplicativiPasswordEncryptEngineApiMode(applicativiCryptConfig);
			ControlStationCore.setApplicativiApiKeyPasswordGeneratedLengthApiMode(applicativiApiKeyPasswordGeneratedLength);
			if(applicativiBasicPasswordEnableConstraints) {
				PasswordVerifier applicativiPasswordVerifier = new PasswordVerifier("/org/openspcoop2/utils/crypt/consolePassword.properties");
				ControlStationCore.setApplicativiPasswordVerifierEngineApiMode(applicativiPasswordVerifier);
			}
			
			ControlStationCore.setSoggettiPasswordEncryptEngineApiMode(soggettiCryptConfig);
			ControlStationCore.setSoggettiApiKeyPasswordGeneratedLengthApiMode(soggettiApiKeyPasswordGeneratedLength);
			if(soggettiBasicPasswordEnableConstraints) {
				PasswordVerifier soggettiPasswordVerifier = new PasswordVerifier("/org/openspcoop2/utils/crypt/consolePassword.properties");
				ControlStationCore.setSoggettiPasswordVerifierEngineApiMode(soggettiPasswordVerifier);
			}
			
		} catch (Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	private static void initConnettori(String confDir, String protocolloDefault) throws CoreException {
		try{
			Connettori.initialize(logCore, true, confDir, protocolloDefault);
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}

	private static void doError(String msg,Exception e) throws CoreException {
		String msgErrore = msg+": " + e.getMessage();
		logCoreError(msgErrore,e);
		throw new CoreException(msgErrore,e);
	}
}
