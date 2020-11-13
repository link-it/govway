/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
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
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.utils.resources.FileSystemUtilities;
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
	private static Logger logSql = LoggerWrapperFactory.getLogger(Loader.class);
	
	public static void main(String[] args) throws Exception {
		
		Connection connectionSQL = null;
		try {
		
			// Logger
			InputStream inPropLog4j = null;
			Properties propertiesLog4j = new Properties();
			try {
				inPropLog4j = Loader.class.getResourceAsStream("/config_loader.cli.log4j2.properties");
				propertiesLog4j.load(inPropLog4j);
				LoggerWrapperFactory.setLogConfiguration(propertiesLog4j);
			} catch(java.lang.Exception e) {
				throw new Exception("Impossibile leggere i dati dal file 'config_loader.cli.log4j2.properties': "+e.getMessage());
			} finally {
				try {
					if (inPropLog4j != null)
						inPropLog4j.close();
				} catch (Exception e) {}
			}
			logCore=LoggerWrapperFactory.getLogger("config_loader.core");	
			logSql=LoggerWrapperFactory.getLogger("config_loader.sql");	
			
			
			logCore.debug("Raccolta parametri in corso...");
			
			// costanti
			ArchiveModeType modeType = org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_ARCHIVE_MODE_TYPE;
			ArchiveMode mode = org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_ARCHIVE_MODE;
			boolean validateDocuments = true;
			MapPlaceholder importInformationMissing_globalPlaceholder = new MapPlaceholder();
			boolean showCorrelazioneAsincronaInAccordi = true;
			boolean isShowGestioneWorkflowStatoDocumenti = false;
			boolean isShowAccordiColonnaAzioni = false;
			boolean smista = false;
			
			// properties
			LoaderProperties loaderProperties = LoaderProperties.getInstance();
			String confDir = null; // non sembra servire
			String protocolloDefault = loaderProperties.getProtocolloDefault();
			String import_nomePddOperativa = loaderProperties.getNomePddOperativa();
			String import_tipoPddArchivi = loaderProperties.getTipoPddArchivio();
			String userLogin = loaderProperties.getUtente();
			boolean importDeletePolicyConfig = loaderProperties.isPolicy_enable();
			boolean importConfig = loaderProperties.isConfigurazioneGenerare_enable();
			boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto = loaderProperties.isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto();
			boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto = loaderProperties.isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto();
			CryptConfig utenzeCryptConfig = new CryptConfig(loaderProperties.getUtenze_password());
			CryptConfig applicativiCryptConfig = new CryptConfig(loaderProperties.getApplicativi_password());
			int applicativi_api_key_passwordGenerated_length=loaderProperties.getApplicativi_apiKey_passwordGenerated_length();
			boolean applicativi_basic_password_enableConstraints=loaderProperties.isApplicativi_basic_password_enableConstraints();
			CryptConfig soggettiCryptConfig = new CryptConfig(loaderProperties.getSoggetti_password());
			int soggetti_api_key_passwordGenerated_length=loaderProperties.getSoggetti_apiKey_passwordGenerated_length();
			boolean soggetti_basic_password_enableConstraints=loaderProperties.isSoggetti_basic_password_enableConstraints();
			
			// args
			boolean delete = false;
			boolean updateAbilitato = true;
			String utilizzoErrato = "Usage error: Loader <operationType> <archivePath>";
			if(args.length<2 || args[0]==null || args[1]==null) {
				throw new Exception(utilizzoErrato);
			}
			LoaderOperationType opType = null;
			try {
				opType = LoaderOperationType.valueOf(args[0].trim());
			}catch(Exception e) {
				throw new Exception(utilizzoErrato+"\nIl tipo di operazione indicato ("+args[0].trim()+") non è gestito, valori ammessi: "+
						LoaderOperationType.create+","+LoaderOperationType.createUpdate+","+LoaderOperationType.delete);
			}
			switch (opType) {
			case create:
				delete=false;
				updateAbilitato = false;
				break;
			case createUpdate:
				delete=false;
				updateAbilitato = true;
				break;
			case delete:
				delete=true;
				updateAbilitato = false;
				break;
			}
			
			String filePath = args[1].trim();
			File fFilePath = new File(filePath);
			if(!fFilePath.exists()) {
				throw new Exception("L'archivio indicato ("+fFilePath.getAbsolutePath()+") non esiste");
			}
			if(!fFilePath.canRead()) {
				throw new Exception("L'archivio indicato ("+fFilePath.getAbsolutePath()+") non è accessibile");
			}
			if(!fFilePath.isFile()) {
				throw new Exception("L'archivio indicato ("+fFilePath.getAbsolutePath()+") non è un file");
			}
			byte [] archiveFile = FileSystemUtilities.readBytesFromFile(fFilePath);
			
			logCore.debug("Raccolta parametri terminata");
			
			
			logCore.debug("Inizializzazione connessione database in corso...");
			
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
			
			//String subContext = "java:";
			String jndiName = "org.govway.datasource.console";
			DataSource ds = new SingleConnectionDataSource(connectionSQL, true); 
			System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
			System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
			InitialContext ic = new InitialContext();
			try{
				//ic.createSubcontext(subContext);
				ic.bind(jndiName, ds);
			}catch(javax.naming.NameAlreadyBoundException already){
				//	capita in caso di più threads
			}
			
			logCore.debug("Inizializzazione connessione database terminata");
			
			
			
			logCore.debug("Inizializzazione risorse libreria in corso...");
			
			try {
				ConfigurazionePdD configPdD = new ConfigurazionePdD();
				configPdD.setAttesaAttivaJDBC(-1);
				configPdD.setCheckIntervalJDBC(-1);
				configPdD.setLoader(new org.openspcoop2.utils.resources.Loader(Loader.class.getClassLoader()));
				configPdD.setLog(logCore);
				ProtocolFactoryManager.initialize(logCore, configPdD,
						protocolloDefault);
			} catch (Exception e) {
				throw new Exception("Errore (InitConfigurazione - ProtocolFactoryManager): "+e.getMessage(),e);
			}
			
			try{
				ExtendedInfoManager.initialize(new org.openspcoop2.utils.resources.Loader(Loader.class.getClassLoader()), null, null, null);
			}catch(Exception e){
				throw new Exception("Inizializzazione [ExtendedInfoManager] fallita",e);
			}
			
			Properties p = new Properties();
			p.put("dataSource", jndiName);
			p.put("tipoDatabase", tipoDatabase);
			if(org.openspcoop2.web.ctrlstat.config.DatasourceProperties.initialize(p,logCore)==false){
				throw new Exception("Inizializzazione fallita");
			}
		
			try {
				ControlStationCore.setUtenzePasswordEncryptEngine_apiMode(utenzeCryptConfig);
				
				ControlStationCore.setApplicativiPasswordEncryptEngine_apiMode(applicativiCryptConfig);
				ControlStationCore.setApplicativiApiKeyPasswordGeneratedLength_apiMode(applicativi_api_key_passwordGenerated_length);
				if(applicativi_basic_password_enableConstraints) {
					PasswordVerifier applicativiPasswordVerifier = new PasswordVerifier("/org/openspcoop2/utils/crypt/consolePassword.properties");
					ControlStationCore.setApplicativiPasswordVerifierEngine_apiMode(applicativiPasswordVerifier);
				}
				
				ControlStationCore.setSoggettiPasswordEncryptEngine_apiMode(soggettiCryptConfig);
				ControlStationCore.setSoggettiApiKeyPasswordGeneratedLength_apiMode(soggetti_api_key_passwordGenerated_length);
				if(soggetti_basic_password_enableConstraints) {
					PasswordVerifier soggettiPasswordVerifier = new PasswordVerifier("/org/openspcoop2/utils/crypt/consolePassword.properties");
					ControlStationCore.setSoggettiPasswordVerifierEngine_apiMode(soggettiPasswordVerifier);
				}
				
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			}
			
			try{
				Connettori.initialize(logCore, true, confDir, protocolloDefault);
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
			
			logCore.debug("Inizializzazione risorse libreria terminata");

			

			logCore.debug("Inizializzazione driver ...");
			
			// istanzio il driver
			DriverConfigurazioneDB driverConfigDB = new DriverConfigurazioneDB(connectionSQL, logSql, tipoDatabase);
			DriverRegistroServiziDB driverRegistroDB = new DriverRegistroServiziDB(connectionSQL, logSql,tipoDatabase);
			RegistryReader registryReader = new RegistryReader(driverRegistroDB,logSql);
			ConfigIntegrationReader configReader = new ConfigIntegrationReader(driverConfigDB,logSql);
			
			// istanzio driver per ControlloTraffico
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDatabase);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager jdbcServiceManagerControlloTraffico = 
					new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(connectionSQL, properties, logSql);

			// Istanzio ArchiviEngineControlStation
			ControlStationCore core = new ControlStationCore(true, null, protocolloDefault);
			ArchiviCore archiviCore = new ArchiviCore(core);
			ArchiveEngine importerEngine = new ArchiveEngine(driverRegistroDB, 
					driverConfigDB,
					jdbcServiceManagerControlloTraffico,
					archiviCore, smista, userLogin);
			
			logCore.debug("Inizializzazione driver terminata");
			
			logCore.info("Inizializzazione engine terminata");			
			
			
			
			// parsing
			logCore.info("Lettura archivio ...");
			IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolloDefault);
			IArchive archiveEngine = pf.createArchive();
			Archive archive = archiveEngine.importArchive(archiveFile, mode, modeType, registryReader, configReader,
					validateDocuments, importInformationMissing_globalPlaceholder);
			logCore.info("Lettura archivio effettuata");
			
			
			// validate
			logCore.info("Validazione archivio ...");
			ArchiveValidator validator = new ArchiveValidator(registryReader);
			ImportInformationMissingCollection importInformationMissingCollection = new ImportInformationMissingCollection();
			validator.validateArchive(archive, protocolloDefault, validateDocuments, importInformationMissingCollection, userLogin, 
					showCorrelazioneAsincronaInAccordi,delete);
			logCore.info("Validazione archivio effettuata");
			
			// finalize
			logCore.info("Finalizzazione archivio ...");
			archiveEngine.finalizeImportArchive(archive, mode, modeType, registryReader, configReader,
					validateDocuments, importInformationMissing_globalPlaceholder);
			logCore.info("Finalizzazione archivio effettuata");
			
			// store
			String esito = null;
			if(delete){
				
				logCore.info("Eliminazione in corso ...");
				
				DeleterArchiveUtils deleterArchiveUtils = 
						new DeleterArchiveUtils(importerEngine, logCore, userLogin,
								importDeletePolicyConfig);
				
				ArchiveEsitoDelete esitoDelete = deleterArchiveUtils.deleteArchive(archive, userLogin);
				
				esito = archiveEngine.toString(esitoDelete, mode);
				
				logCore.info("Eliminazione completata");
				
			}else{
				
				logCore.info("Importazione (aggiornamento:"+updateAbilitato+") in corso ...");
				
				ImporterArchiveUtils importerArchiveUtils = 
						new ImporterArchiveUtils(importerEngine, logCore, userLogin, import_nomePddOperativa, import_tipoPddArchivi, 
								isShowGestioneWorkflowStatoDocumenti, updateAbilitato,
								importDeletePolicyConfig, importConfig);
				
				ArchiveEsitoImport esitoImport = importerArchiveUtils.importArchive(archive, userLogin, 
						isShowAccordiColonnaAzioni,
						isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto, 
						isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto);
				
				esito = archiveEngine.toString(esitoImport, mode);
				
				logCore.info("Importazione (aggiornamento:"+updateAbilitato+") completata");
			}
			
			logCore.info("Operazione terminata con esito:\n"+esito);
			
		}
		catch(Throwable t) {
			if(logCore!=null) {
				logCore.error(t.getMessage(),t);
			}
			throw t;
		}
		finally {
			if(connectionSQL!=null) {
				connectionSQL.close();
			}
		}

	}

}
