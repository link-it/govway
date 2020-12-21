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




package org.openspcoop2.pdd.logger;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.IPdDContextSerializer;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.builder.DateBuilder;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.properties.CollectionProperties;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * Contiene la definizione un Logger utilizzato dai nodi dell'infrastruttura di OpenSPCoop
 * per la registrazione di messaggi riguardanti :.
 * <ul>
 * <li> Tracciamento
 * <li> MsgDiagnostici
 * </ul>
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class OpenSPCoop2Logger {

	/**  Logger log4j utilizzato per scrivere i tracciamenti */
	protected static Logger loggerTracciamento = null;
	/** Logger log4j utilizzato per scrivere i tracciamenti: impostazione */
	public static boolean loggerTracciamentoAbilitato = false;
	/**  Logger log4j utilizzato per scrivere i msgDiagnostici */
	protected static org.apache.logging.log4j.Logger loggerMsgDiagnostico = null;
	/** Logger log4j utilizzato per scrivere i msg diagnostici: impostazione */
	public static boolean loggerMsgDiagnosticoAbilitato = false;
	/**  Logger log4j utilizzato per scrivere i msgDiagnostici */
	protected static org.apache.logging.log4j.Logger loggerOpenSPCoop2 = null;
	/** Logger log4j utilizzato per scrivere i msg diagnostici 'readable': impostazione */
	public static boolean loggerMsgDiagnosticoReadableAbilitato = false;
	/**  Logger log4j utilizzato per scrivere i msgDiagnostici del servizio di IntegrationManager */
	protected static org.apache.logging.log4j.Logger loggerIntegrationManager = null;
	/** Logger log4j utilizzato per scrivere i msg diagnostici 'readable' del servizio di IntegrationManager: impostazione */
	public static boolean loggerIntegrationManagerAbilitato = false;
	/**  Logger log4j utilizzato per effettuare un dump dei messaggi applicativi */
	protected static Logger loggerDump = null;
	/** Logger log4j utilizzato per scrivere i dump applicativi: impostazione */
	public static boolean loggerDumpAbilitato = false;
	/**  Logger log4j utilizzato per segnalare a video errori gravi (FATAL) o di informazione sull'inizializzazione (INFO)*/
	protected static Logger loggerOpenSPCoopConsole = LoggerWrapperFactory.getLogger("govway.startup");
	/** Logger log4j utilizzato per segnalare a video errori gravi (FATAL) o di informazione sull'inizializzazione (INFO) */
	protected static boolean loggerOpenSPCoopConsoleStartupAgganciatoLog = false;
	/**  Logger log4j utilizzato per il core di OpenSPCoop */
	protected static Logger loggerOpenSPCoopCore = null;
	/**  Logger log4j utilizzato per registrare le operazioni dei Timer che ripuliscono i repository buste e messaggi*/
	protected static Logger loggerOpenSPCoopTimers = null;
	/**  Logger log4j utilizzato per registrare le operazioni di monitoraggio delle risorse utilizzate dalla PdD (Timer che verficano le risorse della PdD) */
	protected static Logger loggerOpenSPCoopResources = null;
	protected static org.apache.logging.log4j.Logger loggerOpenSPCoopResourcesAsLoggerImpl = null;
	/**  Logger log4j utilizzato per la configurazione di sistema */
	protected static Logger loggerOpenSPCoopConfigurazioneSistema = null;
	/**  Logger log4j utilizzato per i connettori */
	protected static Logger loggerOpenSPCoopConnettori = null;
	/**  Logger log4j utilizzato per i dati binari del servizio PD */
	protected static Logger loggerOpenSPCoopDumpBinarioPD = null;
	/**  Logger log4j utilizzato per i dati binari del servizio PA */
	protected static Logger loggerOpenSPCoopDumpBinarioPA = null;
	/**  Logger log4j utilizzato per gli eventi */
	protected static Logger loggerOpenSPCoopEventi = null;
	protected static Logger loggerOpenSPCoopEventiError = null;
	/**  Logger log4j utilizzato per le transazioni */
	protected static Logger loggerOpenSPCoopTransazioni = null;
	protected static Logger loggerOpenSPCoopTransazioniError = null;
	protected static Logger loggerOpenSPCoopTransazioniDevNull = null;
	/**  Logger log4j utilizzato per le transazioni */
	protected static Logger loggerOpenSPCoopTransazioniSql = null;
	protected static Logger loggerOpenSPCoopTransazioniSqlError = null;
	/**  Logger log4j utilizzato per le transazioni stateful */
	protected static Logger loggerOpenSPCoopTransazioniStateful = null;
	protected static Logger loggerOpenSPCoopTransazioniStatefulError = null;
	/**  Logger log4j utilizzato per le transazioni stateful */
	protected static Logger loggerOpenSPCoopTransazioniStatefulSql = null;
	protected static Logger loggerOpenSPCoopTransazioniStatefulSqlError = null;
	/**  Logger log4j utilizzato per attività di file system recovery */
	protected static Logger loggerOpenSPCoopFileSystemRecovery = null;
	protected static Logger loggerOpenSPCoopFileSystemRecoveryError = null;
	/**  Logger log4j utilizzato per attività di file system recovery (sql) */
	protected static Logger loggerOpenSPCoopFileSystemRecoverySql = null;
	protected static Logger loggerOpenSPCoopFileSystemRecoverySqlError = null;
	/**  Logger log4j utilizzato per il Controllo del Traffico */
	protected static Logger loggerOpenSPCoopControlloTraffico = null;
	protected static Logger loggerOpenSPCoopControlloTrafficoError = null;
	/**  Logger log4j utilizzato per il Controllo del Traffico (sql) */
	protected static Logger loggerOpenSPCoopControlloTrafficoSql = null;
	protected static Logger loggerOpenSPCoopControlloTrafficoSqlError = null;
	/**  Logger log4j utilizzato per le statistiche */
	protected static Logger loggerOpenSPCoopStatisticheOrarie = null;
	protected static Logger loggerOpenSPCoopStatisticheOrarieError = null;
	protected static Logger loggerOpenSPCoopStatisticheGiornaliere = null;
	protected static Logger loggerOpenSPCoopStatisticheGiornaliereError = null;
	protected static Logger loggerOpenSPCoopStatisticheSettimanali = null;
	protected static Logger loggerOpenSPCoopStatisticheSettimanaliError = null;
	protected static Logger loggerOpenSPCoopStatisticheMensili = null;
	protected static Logger loggerOpenSPCoopStatisticheMensiliError = null;
	/**  Logger log4j utilizzato per le statistiche */
	protected static Logger loggerOpenSPCoopStatisticheOrarieSql = null;
	protected static Logger loggerOpenSPCoopStatisticheOrarieSqlError = null;
	protected static Logger loggerOpenSPCoopStatisticheGiornaliereSql = null;
	protected static Logger loggerOpenSPCoopStatisticheGiornaliereSqlError = null;
	protected static Logger loggerOpenSPCoopStatisticheSettimanaliSql = null;
	protected static Logger loggerOpenSPCoopStatisticheSettimanaliSqlError = null;
	protected static Logger loggerOpenSPCoopStatisticheMensiliSql = null;
	protected static Logger loggerOpenSPCoopStatisticheMensiliSqlError = null;
	/**  Logger log4j utilizzato per consegna contenuti */
	protected static Logger loggerOpenSPCoopConsegnaContenuti = null;
	protected static Logger loggerOpenSPCoopConsegnaContenutiError = null;
	/**  Logger log4j utilizzato per consegna contenuti */
	protected static Logger loggerOpenSPCoopConsegnaContenutiSql = null;
	protected static Logger loggerOpenSPCoopConsegnaContenutiSqlError = null;
	/**  Logger log4j utilizzato per i Plugins */
	protected static Logger loggerOpenSPCoopPlugins = null;
	protected static Logger loggerOpenSPCoopPluginsError = null;
	/**  Logger log4j utilizzato per i Plugins (sql) */
	protected static Logger loggerOpenSPCoopPluginsSql = null;
	protected static Logger loggerOpenSPCoopPluginsSqlError = null;
	/** Appender personalizzati per i messaggi diagnostici di OpenSPCoop */
	public static List<IDiagnosticProducer> loggerMsgDiagnosticoOpenSPCoopAppender = new ArrayList<IDiagnosticProducer>(); 
	public static List<String> tipoMsgDiagnosticoOpenSPCoopAppender = new ArrayList<String>();
	/** Appender personalizzati per i tracciamenti di OpenSPCoop */
	public static List<ITracciaProducer> loggerTracciamentoOpenSPCoopAppender = new ArrayList<ITracciaProducer>(); 
	public static List<String> tipoTracciamentoOpenSPCoopAppender = new ArrayList<String>();
	/** Appender personalizzati per i dump applicativi di OpenSPCoop */
	public static List<IDumpProducer> loggerDumpOpenSPCoopAppender = new ArrayList<IDumpProducer>(); 
	public static List<String> tipoDumpOpenSPCoopAppender = new ArrayList<String>();
	/** PdDContextSerializer */
	private static IPdDContextSerializer pddContextSerializer = null;
	/** LogDir */
	private static List<File> logDirs;
	public static List<File> getLogDirs() {
		return logDirs;
	}

	public static boolean isLoggerOpenSPCoopConsoleStartupAgganciatoLog(){
		return OpenSPCoop2Logger.loggerOpenSPCoopConsoleStartupAgganciatoLog;
	}
	
	private static List<String> filesCheck;
	private static void initializeLogDirs(Properties p, boolean append){
		
		if(!append) {
			logDirs = new ArrayList<>();
			filesCheck = new ArrayList<>();
		}
		
		Enumeration<?> en = p.keys();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			key = key.trim();
			String value = p.getProperty(key);
			value = value.trim();
			if(key.endsWith(".fileName") || key.endsWith(".filePattern")) {
				File fTmp = new File(value);
				if(fTmp.getParentFile()!=null) {
					if(filesCheck.contains(fTmp.getParentFile().getAbsolutePath())==false) {
						logDirs.add(fTmp.getParentFile());
						filesCheck.add(fTmp.getParentFile().getAbsolutePath());
					}
				}
			}
		}
		
	}
	
	/**
	 * Il Metodo si occupa di inizializzare il Logger utilizzato da OpenSPCoop (file,DB scelti da govway.log4j2.properties)
	 *
	 * @param logConsole Log console
	 * @return true in caso di inizializzazione con successo, false altrimenti.
	 * 
	 */
	public static boolean initializeLogConsole(Logger logConsole){
		InputStream isOp2 = null;
		InputStream isLogger = null;
		try{
			isOp2 = OpenSPCoop2Logger.class.getResourceAsStream("/govway.properties");
			String confDir = null;
			if(isOp2!=null){
				java.util.Properties op2Properties = new java.util.Properties();
				op2Properties.load(isOp2);
				confDir = op2Properties.getProperty("org.openspcoop2.pdd.confDirectory");
			}
			
			java.util.Properties loggerProperties = new java.util.Properties();
			isLogger = OpenSPCoop2Logger.class.getResourceAsStream("/govway.log4j2.properties");
			if(isLogger!=null){
				loggerProperties.load(isLogger);
			}
			
			// Cerco eventuale ridefinizione
			CollectionProperties loggerPropertiesRidefinito =  
					PropertiesUtilities.searchLocalImplementation(CostantiPdD.OPENSPCOOP2_LOCAL_HOME,LoggerWrapperFactory.getLogger(OpenSPCoop2Logger.class), 
							CostantiPdD.OPENSPCOOP2_LOGGER_PROPERTIES, CostantiPdD.OPENSPCOOP2_LOGGER_LOCAL_PATH, 
							confDir);
			
			if(loggerPropertiesRidefinito!=null && loggerPropertiesRidefinito.size()>0){
				Enumeration<?> ridefinito = loggerPropertiesRidefinito.keys();
				while (ridefinito.hasMoreElements()) {
					String key = (String) ridefinito.nextElement();
					String value = (String) loggerPropertiesRidefinito.get(key);
					if(loggerProperties.containsKey(key)){
						//Object o = 
						loggerProperties.remove(key);
					}
					loggerProperties.put(key, value);
					//System.out.println("CHECK NUOVO VALORE: "+loggerProperties.get(key));
				}
			}
			
			LoggerWrapperFactory.setLogConfiguration(loggerProperties);
			initializeLogDirs(loggerProperties, false);
			
			return true;
		}catch(Exception e){
			OpenSPCoop2Logger.loggerOpenSPCoopConsole.error("Riscontrato errore durante l'inizializzazione del sistema di logging di OpenSPCoop: "
					+e.getMessage(),e);
			return false;
		}finally{
			try{
				if(isOp2!=null){
					isOp2.close();
				}
			}catch(Exception eClose){}
			try{
				if(isLogger!=null){
					isLogger.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	
	public static boolean initialize(Logger logConsole,String rootDirectory,Properties objectProperties){
		return initialize(logConsole, rootDirectory, objectProperties, true);
	}
	public static boolean initialize(Logger logConsole,String rootDirectory,Properties objectProperties, boolean loadExternalConfiguration){
		try{
			
			// Originale
			java.util.Properties loggerProperties = new java.util.Properties();
			java.io.File loggerFile = new java.io.File(rootDirectory+"govway.log4j2.properties");
			if(loggerFile .exists() == false ){
				loggerProperties.load(OpenSPCoop2Logger.class.getResourceAsStream("/govway.log4j2.properties"));
			}else{
				FileInputStream fin = null;
				try{
					fin = new java.io.FileInputStream(loggerFile);
					loggerProperties.load(fin);
				}finally{
					try{
						if(fin!=null){
							fin.close();
						}
					}catch(Exception eClose){}
				}
			}
			
			if(loadExternalConfiguration){
			
				loadExternal(logConsole, CostantiPdD.OPENSPCOOP2_LOGGER_PROPERTIES ,CostantiPdD.OPENSPCOOP2_LOGGER_LOCAL_PATH, 
						rootDirectory, loggerProperties, objectProperties);
				
			}

			LoggerWrapperFactory.setLogConfiguration(loggerProperties);
			initializeLogDirs(loggerProperties, false);
			
			// STARTUP CONSOLE
			String tmp = loggerProperties.getProperty("logger.govway_startup.level");
			if(tmp!=null){
				if(!tmp.equalsIgnoreCase("OFF")){
					OpenSPCoop2Logger.loggerOpenSPCoopConsoleStartupAgganciatoLog = true;
				}
			}
			
			// TRACCIAMENTO
			OpenSPCoop2Logger.loggerTracciamento = LoggerWrapperFactory.getLogger("govway.tracciamento");
			if(OpenSPCoop2Logger.loggerTracciamento==null)
				throw new Exception("Logger govway.tracciamento non trovato");
			// Abilitazione log da Log4j
			tmp = loggerProperties.getProperty("logger.govway_tracciamento.level");
			if(tmp!=null){
				tmp.trim();
				if(tmp.equalsIgnoreCase("OFF")){
					OpenSPCoop2Logger.loggerOpenSPCoopConsole.info("Sistema di logging delle tracce disabilitato da log4j (OFF).");
					OpenSPCoop2Logger.loggerTracciamentoAbilitato = false;
				}
				else
					OpenSPCoop2Logger.loggerTracciamentoAbilitato = true;
			}
			
			// MESSAGGI DIAGNOSTICI
			OpenSPCoop2Logger.loggerMsgDiagnostico = LoggerWrapperFactory.getLoggerImpl("govway.msgDiagnostico");
			if(OpenSPCoop2Logger.loggerMsgDiagnostico==null)
				throw new Exception("Logger govway.msgDiagnostico non trovato");
			// Abilitazione log da Log4j
			tmp = loggerProperties.getProperty("logger.govway_diagnostici.level");
			if(tmp!=null){
				tmp.trim();
				if(tmp.equalsIgnoreCase("OFF")){
					OpenSPCoop2Logger.loggerOpenSPCoopConsole.info("Sistema di logging dei messaggi diagnostici disabilitato da log4j (OFF).");
					OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato = false;
				}else
					OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato = true;
			}
			
			// MESSAGGI DIAGNOSTICI LEGGIBILI
			OpenSPCoop2Logger.loggerOpenSPCoop2 = LoggerWrapperFactory.getLoggerImpl("govway.portaDiDominio");
			if(OpenSPCoop2Logger.loggerOpenSPCoop2==null)
				throw new Exception("Logger govway.portaDiDominio non trovato");
			// Abilitazione log da Log4j
			tmp = loggerProperties.getProperty("logger.govway_portaDiDominio.level");
			if(tmp!=null){
				tmp.trim();
				if(tmp.equalsIgnoreCase("OFF")){
					OpenSPCoop2Logger.loggerOpenSPCoopConsole.info("Sistema di logging dei messaggi diagnostici 'readable' disabilitato da log4j (OFF).");
					OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato = false;
				}else
					OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato = true;
			}
			
			// MESSAGGI DIAGNOSTICI DELL'INTEGRATION MANAGER
			OpenSPCoop2Logger.loggerIntegrationManager = LoggerWrapperFactory.getLoggerImpl("govway.integrationManager");
			if(OpenSPCoop2Logger.loggerIntegrationManager==null)
				throw new Exception("Logger govway.integrationManager non trovato");
			// Abilitazione log da Log4j
			tmp = loggerProperties.getProperty("logger.govway_integrationManager.level");
			if(tmp!=null){
				tmp.trim();
				if(tmp.equalsIgnoreCase("OFF")){
					OpenSPCoop2Logger.loggerOpenSPCoopConsole.info("Sistema di logging dei messaggi diagnostici 'readable' per il servizio di IntegrationManager disabilitato da log4j (OFF).");
					OpenSPCoop2Logger.loggerIntegrationManagerAbilitato = false;
				}else
					OpenSPCoop2Logger.loggerIntegrationManagerAbilitato = true;
			}
			
			// DUMP APPLICATIVO
			OpenSPCoop2Logger.loggerDump = LoggerWrapperFactory.getLogger("govway.dump");
			if(OpenSPCoop2Logger.loggerDump==null)
				throw new Exception("Logger govway.dump non trovato");
			// Abilitazione log da Log4j
			tmp = loggerProperties.getProperty("logger.govway_dump.level");
			if(tmp!=null){
				tmp.trim();
				if(tmp.equalsIgnoreCase("OFF")){
					OpenSPCoop2Logger.loggerOpenSPCoopConsole.info("Sistema di logging dei contenuti applicativi (dump) disabilitato da log4j (OFF).");
					OpenSPCoop2Logger.loggerDumpAbilitato = false;
				}
				else
					OpenSPCoop2Logger.loggerDumpAbilitato = true;
			}

			// OPENSPCOOP CORE
			OpenSPCoop2Logger.loggerOpenSPCoopCore = LoggerWrapperFactory.getLogger("govway.core");
			if(OpenSPCoop2Logger.loggerOpenSPCoopCore==null)
				throw new Exception("Logger govway.core non trovato");
			
			// TIMERS LOG
			OpenSPCoop2Logger.loggerOpenSPCoopTimers = LoggerWrapperFactory.getLogger("govway.timers");
			if(OpenSPCoop2Logger.loggerOpenSPCoopTimers==null)
				throw new Exception("Logger govway.timers non trovato");
			
			// RESOURCES LOG
			OpenSPCoop2Logger.loggerOpenSPCoopResources = LoggerWrapperFactory.getLogger("govway.resources");
			if(OpenSPCoop2Logger.loggerOpenSPCoopResources==null)
				throw new Exception("Logger govway.resources non trovato");
			OpenSPCoop2Logger.loggerOpenSPCoopResourcesAsLoggerImpl = LoggerWrapperFactory.getLoggerImpl("govway.resources");
			if(OpenSPCoop2Logger.loggerOpenSPCoopResourcesAsLoggerImpl==null)
				throw new Exception("Logger(Impl) govway.resources non trovato");
			
			// CONFIGURAZIONE SISTEMA LOG
			OpenSPCoop2Logger.loggerOpenSPCoopConfigurazioneSistema = LoggerWrapperFactory.getLogger("govway.configurazioneSistema");
			if(OpenSPCoop2Logger.loggerOpenSPCoopConfigurazioneSistema==null)
				throw new Exception("Logger govway.configurazioneSistema non trovato");
			
			// CONNETTORI LOG
			OpenSPCoop2Logger.loggerOpenSPCoopConnettori = LoggerWrapperFactory.getLogger("govway.connettori");
			if(OpenSPCoop2Logger.loggerOpenSPCoopConnettori==null)
				throw new Exception("Logger govway.connettori non trovato");
						
			// RAW DATA SERVIZIO PD LOG
			OpenSPCoop2Logger.loggerOpenSPCoopDumpBinarioPD = LoggerWrapperFactory.getLogger("govway.dumpBinarioPD");
			if(OpenSPCoop2Logger.loggerOpenSPCoopDumpBinarioPD==null)
				throw new Exception("Logger govway.dumpBinarioPD non trovato");
			
			// RAW DATA SERVIZIO PD LOG
			OpenSPCoop2Logger.loggerOpenSPCoopDumpBinarioPA = LoggerWrapperFactory.getLogger("govway.dumpBinarioPA");
			if(OpenSPCoop2Logger.loggerOpenSPCoopDumpBinarioPA==null)
				throw new Exception("Logger govway.dumpBinarioPA non trovato");
			
			// EVENTI LOG
			OpenSPCoop2Logger.loggerOpenSPCoopEventi = LoggerWrapperFactory.getLogger("govway.eventi");
			if(OpenSPCoop2Logger.loggerOpenSPCoopEventi==null)
				throw new Exception("Logger govway.eventi non trovato");
			
			// TRANSAZIONI LOG
			OpenSPCoop2Logger.loggerOpenSPCoopTransazioni = LoggerWrapperFactory.getLogger("govway.transazioni");
			if(OpenSPCoop2Logger.loggerOpenSPCoopTransazioni==null)
				throw new Exception("Logger govway.transazioni non trovato");
			
			// TRANSAZIONI LOG (ERROR)
			OpenSPCoop2Logger.loggerOpenSPCoopTransazioniError = LoggerWrapperFactory.getLogger("govway.transazioni.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopTransazioniError==null)
				throw new Exception("Logger govway.transazioni.error non trovato");
			
			// TRANSAZIONI LOG (DEVNULL)
			OpenSPCoop2Logger.loggerOpenSPCoopTransazioniDevNull = LoggerWrapperFactory.getLogger("govway.transazioni.devnull");
			if(OpenSPCoop2Logger.loggerOpenSPCoopTransazioniDevNull==null)
				throw new Exception("Logger govway.transazioni.devnull non trovato");
			
			// TRANSAZIONI SQL LOG
			OpenSPCoop2Logger.loggerOpenSPCoopTransazioniSql = LoggerWrapperFactory.getLogger("govway.transazioni.sql");
			if(OpenSPCoop2Logger.loggerOpenSPCoopTransazioniSql==null)
				throw new Exception("Logger govway.transazioni.sql non trovato");
			
			// TRANSAZIONI SQL LOG (ERROR)
			OpenSPCoop2Logger.loggerOpenSPCoopTransazioniSqlError = LoggerWrapperFactory.getLogger("govway.transazioni.sql.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopTransazioniSqlError==null)
				throw new Exception("Logger govway.transazioni.sql.error non trovato");
			
			// TRANSAZIONI STATEFUL LOG
			OpenSPCoop2Logger.loggerOpenSPCoopTransazioniStateful = LoggerWrapperFactory.getLogger("govway.transazioni.stateful");
			if(OpenSPCoop2Logger.loggerOpenSPCoopTransazioniStateful==null)
				throw new Exception("Logger govway.transazioni.stateful non trovato");
			
			// TRANSAZIONI STATEFUL LOG (ERROR)
			OpenSPCoop2Logger.loggerOpenSPCoopTransazioniStatefulError = LoggerWrapperFactory.getLogger("govway.transazioni.stateful.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopTransazioniStatefulError==null)
				throw new Exception("Logger govway.transazioni.stateful.error non trovato");
			
			// TRANSAZIONI STATEFUL SQL LOG
			OpenSPCoop2Logger.loggerOpenSPCoopTransazioniStatefulSql = LoggerWrapperFactory.getLogger("govway.transazioni.stateful.sql");
			if(OpenSPCoop2Logger.loggerOpenSPCoopTransazioniStatefulSql==null)
				throw new Exception("Logger govway.transazioni.stateful.sql non trovato");
			
			// TRANSAZIONI STATEFUL SQL LOG (ERROR)
			OpenSPCoop2Logger.loggerOpenSPCoopTransazioniStatefulSqlError = LoggerWrapperFactory.getLogger("govway.transazioni.stateful.sql.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopTransazioniStatefulSqlError==null)
				throw new Exception("Logger govway.transazioni.stateful.sql.error non trovato");
				
			// EVENTI LOG (ERROR)
			OpenSPCoop2Logger.loggerOpenSPCoopEventiError = LoggerWrapperFactory.getLogger("govway.eventi.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopEventiError==null)
				throw new Exception("Logger govway.eventi.error non trovato");
			
			// FILE SYSTEM RECOVERY LOG
			OpenSPCoop2Logger.loggerOpenSPCoopFileSystemRecovery = LoggerWrapperFactory.getLogger("govway.recoveryFileSystem");
			if(OpenSPCoop2Logger.loggerOpenSPCoopFileSystemRecovery==null)
				throw new Exception("Logger govway.recoveryFileSystem non trovato");
			
			// FILE SYSTEM RECOVERY LOG (ERROR)
			OpenSPCoop2Logger.loggerOpenSPCoopFileSystemRecoveryError = LoggerWrapperFactory.getLogger("govway.recoveryFileSystem.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopFileSystemRecoveryError==null)
				throw new Exception("Logger govway.recoveryFileSystem.error non trovato");
			
			// FILE SYSTEM RECOVERY SQL LOG
			OpenSPCoop2Logger.loggerOpenSPCoopFileSystemRecoverySql = LoggerWrapperFactory.getLogger("govway.recoveryFileSystem.sql");
			if(OpenSPCoop2Logger.loggerOpenSPCoopFileSystemRecoverySql==null)
				throw new Exception("Logger govway.recoveryFileSystem.sql non trovato");
			
			// FILE SYSTEM RECOVERY SQL LOG (ERROR)
			OpenSPCoop2Logger.loggerOpenSPCoopFileSystemRecoverySqlError = LoggerWrapperFactory.getLogger("govway.recoveryFileSystem.sql.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopFileSystemRecoverySqlError==null)
				throw new Exception("Logger govway.recoveryFileSystem.sql.error non trovato");
			
			// CONTROLLO TRAFFICO LOG
			OpenSPCoop2Logger.loggerOpenSPCoopControlloTraffico = LoggerWrapperFactory.getLogger("govway.controlloTraffico");
			if(OpenSPCoop2Logger.loggerOpenSPCoopControlloTraffico==null)
				throw new Exception("Logger govway.controlloTraffico non trovato");
			
			// CONTROLLO TRAFFICO LOG (ERROR)
			OpenSPCoop2Logger.loggerOpenSPCoopControlloTrafficoError = LoggerWrapperFactory.getLogger("govway.controlloTraffico.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopControlloTrafficoError==null)
				throw new Exception("Logger govway.controlloTraffico.error non trovato");
			
			// CONTROLLO TRAFFICO SQL LOG
			OpenSPCoop2Logger.loggerOpenSPCoopControlloTrafficoSql = LoggerWrapperFactory.getLogger("govway.controlloTraffico.sql");
			if(OpenSPCoop2Logger.loggerOpenSPCoopControlloTrafficoSql==null)
				throw new Exception("Logger govway.controlloTraffico.sql non trovato");
			
			// CONTROLLO TRAFFICO SQL LOG (ERROR)
			OpenSPCoop2Logger.loggerOpenSPCoopControlloTrafficoSqlError = LoggerWrapperFactory.getLogger("govway.controlloTraffico.sql.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopControlloTrafficoSqlError==null)
				throw new Exception("Logger govway.controlloTraffico.sql.error non trovato");
			
			// STATISTICHE LOG 'Orarie'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheOrarie = LoggerWrapperFactory.getLogger("govway.statistiche_orarie.generazione");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheOrarie==null)
				throw new Exception("Logger govway.statistiche.generazione non trovato");
			
			// STATISTICHE LOG (ERROR) 'Orarie'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheOrarieError = LoggerWrapperFactory.getLogger("govway.statistiche_orarie.generazione.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheOrarieError==null)
				throw new Exception("Logger govway.statistiche.generazione.error non trovato");
			
			// STATISTICHE SQL LOG 'Orarie'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheOrarieSql = LoggerWrapperFactory.getLogger("govway.statistiche_orarie.generazione.sql");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheOrarieSql==null)
				throw new Exception("Logger govway.statistiche.generazione.sql non trovato");
			
			// STATISTICHE SQL LOG (ERROR) 'Orarie'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheOrarieSqlError = LoggerWrapperFactory.getLogger("govway.statistiche_orarie.generazione.sql.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheOrarieSqlError==null)
				throw new Exception("Logger govway.statistiche.generazione.sql.error non trovato");
						
			// STATISTICHE LOG 'Giornaliere'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheGiornaliere = LoggerWrapperFactory.getLogger("govway.statistiche_giornaliere.generazione");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheGiornaliere==null)
				throw new Exception("Logger govway.statistiche.generazione non trovato");
			
			// STATISTICHE LOG (ERROR) 'Giornaliere'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheGiornaliereError = LoggerWrapperFactory.getLogger("govway.statistiche_giornaliere.generazione.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheGiornaliereError==null)
				throw new Exception("Logger govway.statistiche.generazione.error non trovato");
			
			// STATISTICHE SQL LOG 'Giornaliere'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheGiornaliereSql = LoggerWrapperFactory.getLogger("govway.statistiche_giornaliere.generazione.sql");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheGiornaliereSql==null)
				throw new Exception("Logger govway.statistiche.generazione.sql non trovato");
			
			// STATISTICHE SQL LOG (ERROR) 'Giornaliere'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheGiornaliereSqlError = LoggerWrapperFactory.getLogger("govway.statistiche_giornaliere.generazione.sql.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheGiornaliereSqlError==null)
				throw new Exception("Logger govway.statistiche.generazione.sql.error non trovato");
			
			// STATISTICHE LOG 'Settimanali'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheSettimanali = LoggerWrapperFactory.getLogger("govway.statistiche_settimanali.generazione");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheSettimanali==null)
				throw new Exception("Logger govway.statistiche.generazione non trovato");
			
			// STATISTICHE LOG (ERROR) 'Settimanali'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheSettimanaliError = LoggerWrapperFactory.getLogger("govway.statistiche_settimanali.generazione.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheSettimanaliError==null)
				throw new Exception("Logger govway.statistiche.generazione.error non trovato");
			
			// STATISTICHE SQL LOG 'Settimanali'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheSettimanaliSql = LoggerWrapperFactory.getLogger("govway.statistiche_settimanali.generazione.sql");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheSettimanaliSql==null)
				throw new Exception("Logger govway.statistiche.generazione.sql non trovato");
			
			// STATISTICHE SQL LOG (ERROR) 'Settimanali'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheSettimanaliSqlError = LoggerWrapperFactory.getLogger("govway.statistiche_settimanali.generazione.sql.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheSettimanaliSqlError==null)
				throw new Exception("Logger govway.statistiche.generazione.sql.error non trovato");
				
			// STATISTICHE LOG 'Mensili'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheMensili = LoggerWrapperFactory.getLogger("govway.statistiche_mensili.generazione");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheMensili==null)
				throw new Exception("Logger govway.statistiche.generazione non trovato");
			
			// STATISTICHE LOG (ERROR) 'Mensili'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheMensiliError = LoggerWrapperFactory.getLogger("govway.statistiche_mensili.generazione.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheMensiliError==null)
				throw new Exception("Logger govway.statistiche.generazione.error non trovato");
			
			// STATISTICHE SQL LOG 'Mensili'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheMensiliSql = LoggerWrapperFactory.getLogger("govway.statistiche_mensili.generazione.sql");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheMensiliSql==null)
				throw new Exception("Logger govway.statistiche.generazione.sql non trovato");
			
			// STATISTICHE SQL LOG (ERROR) 'Mensili'
			OpenSPCoop2Logger.loggerOpenSPCoopStatisticheMensiliSqlError = LoggerWrapperFactory.getLogger("govway.statistiche_mensili.generazione.sql.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopStatisticheMensiliSqlError==null)
				throw new Exception("Logger govway.statistiche.generazione.sql.error non trovato");
				
			// CONSEGNA_CONTENUTI LOG
			OpenSPCoop2Logger.loggerOpenSPCoopConsegnaContenuti = LoggerWrapperFactory.getLogger("govway.consegna_messaggi");
			if(OpenSPCoop2Logger.loggerOpenSPCoopConsegnaContenuti==null)
				throw new Exception("Logger govway.consegna_messaggi non trovato");
			
			// CONSEGNA_CONTENUTI LOG (ERROR)
			OpenSPCoop2Logger.loggerOpenSPCoopConsegnaContenutiError = LoggerWrapperFactory.getLogger("govway.consegna_messaggi.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopConsegnaContenutiError==null)
				throw new Exception("Logger govway.consegna_messaggi.error non trovato");
			
			// CONSEGNA_CONTENUTI SQL LOG
			OpenSPCoop2Logger.loggerOpenSPCoopConsegnaContenutiSql = LoggerWrapperFactory.getLogger("govway.consegna_messaggi.sql");
			if(OpenSPCoop2Logger.loggerOpenSPCoopConsegnaContenutiSql==null)
				throw new Exception("Logger govway.consegna_messaggi.sql non trovato");
			
			// CONSEGNA_CONTENUTI SQL LOG (ERROR)
			OpenSPCoop2Logger.loggerOpenSPCoopConsegnaContenutiSqlError = LoggerWrapperFactory.getLogger("govway.consegna_messaggi.sql.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopConsegnaContenutiSqlError==null)
				throw new Exception("Logger govway.consegna_messaggi.sql.error non trovato");

			// PLUGINS LOG
			OpenSPCoop2Logger.loggerOpenSPCoopPlugins = LoggerWrapperFactory.getLogger("govway.plugins");
			if(OpenSPCoop2Logger.loggerOpenSPCoopPlugins==null)
				throw new Exception("Logger govway.plugins non trovato");
			
			// PLUGINS LOG (ERROR)
			OpenSPCoop2Logger.loggerOpenSPCoopPluginsError = LoggerWrapperFactory.getLogger("govway.plugins.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopPluginsError==null)
				throw new Exception("Logger govway.plugins.error non trovato");
			
			// PLUGINS SQL LOG
			OpenSPCoop2Logger.loggerOpenSPCoopPluginsSql = LoggerWrapperFactory.getLogger("govway.plugins.sql");
			if(OpenSPCoop2Logger.loggerOpenSPCoopPluginsSql==null)
				throw new Exception("Logger govway.plugins.sql non trovato");
			
			// PLUGINS SQL LOG (ERROR)
			OpenSPCoop2Logger.loggerOpenSPCoopPluginsSqlError = LoggerWrapperFactory.getLogger("govway.plugins.sql.error");
			if(OpenSPCoop2Logger.loggerOpenSPCoopPluginsSqlError==null)
				throw new Exception("Logger govway.plugins.sql.error non trovato");
			
			// CONSOLE
			OpenSPCoop2Logger.loggerOpenSPCoopConsole.info("Sistema di logging correttamente inizializzato.");
			
			// PddContextSerializer
			OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
			String pddContextSerializerClass = propertiesReader.getPddContextSerializer();
			if(pddContextSerializerClass!=null && !CostantiConfigurazione.NONE.equals(pddContextSerializerClass)){
				try{
					OpenSPCoop2Logger.pddContextSerializer = (IPdDContextSerializer) Loader.getInstance().newInstance(pddContextSerializerClass);
				}catch(Exception e){
					throw new Exception("Inizializzione IPdDContextSerializer non riuscita ["+pddContextSerializerClass+"]:"+e.getMessage(),e);
				}
			}
			
			return true;
		}catch(Exception e){
			OpenSPCoop2Logger.loggerOpenSPCoopConsole.error("Riscontrato errore durante l'inizializzazione del sistema di logging di OpenSPCoop: "
					+e.getMessage(),e);
			return false;
		}
	}
	
	private static void loadExternal(Logger logConsole, String loggerProperty, String loggerPath, String rootDirectory,
			java.util.Properties loggerProperties, Properties objectProperties) {
			
		// File Local Implementation
		CollectionProperties loggerPropertiesRidefinito =  
			PropertiesUtilities.searchLocalImplementation(CostantiPdD.OPENSPCOOP2_LOCAL_HOME,logConsole, loggerProperty ,loggerPath, rootDirectory);
		if(loggerPropertiesRidefinito!=null && loggerPropertiesRidefinito.size()>0){
			Enumeration<?> ridefinito = loggerPropertiesRidefinito.keys();
			while (ridefinito.hasMoreElements()) {
				String key = (String) ridefinito.nextElement();
				String value = (String) loggerPropertiesRidefinito.get(key);
				if(loggerProperties.containsKey(key)){
					//Object o = 
					loggerProperties.remove(key);
				}
				loggerProperties.put(key, value);
				//System.out.println("CHECK NUOVO VALORE: "+loggerProperties.get(key));
			}
		}
		
		// File Object Implementation
		if(objectProperties!=null && objectProperties.size()>0){
			Enumeration<?> ridefinito = objectProperties.keys();
			while (ridefinito.hasMoreElements()) {
				String key = (String) ridefinito.nextElement();
				String value = (String) objectProperties.get(key);
				if(loggerProperties.containsKey(key)){
					//Object o = 
					loggerProperties.remove(key);
				}
				loggerProperties.put(key, value);
				//System.out.println("CHECK NUOVO VALORE: "+loggerProperties.get(key));
			}
		}

	}
	
	public static boolean initializeProtocolLogger(Logger logConsole, boolean loadExternalConfiguration,String rootDirectory,
			Properties objectProperties) {
		
		try{
		
			ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
			Enumeration<String> protocolNames = protocolFactoryManager.getProtocolNames();
			while (protocolNames.hasMoreElements()) {
				String protocol = (String) protocolNames.nextElement();
				if(protocolFactoryManager.isSupportedProtocolLogger(protocol)) {
					
					java.util.Properties loggerPropertiesProtocolAdjunct = null;
					InputStream isLoggerProtocol = OpenSPCoop2Logger.class.getResourceAsStream("/govway.protocolAdjunct.log4j2.properties");
					if(isLoggerProtocol!=null){
						String content = Utilities.getAsString(isLoggerProtocol, Charset.UTF_8.getValue());
						if(content!=null) {
							content = content.replaceAll(CostantiPdD.OPENSPCOOP2_LOGGER_PROTOCOL_ID_PROTOCOLLO, protocol);
						}
						loggerPropertiesProtocolAdjunct = new java.util.Properties();
						StringReader sr = new StringReader(content);
						loggerPropertiesProtocolAdjunct.load(sr);
						sr.close();
					}
					if(loadExternalConfiguration){
						
						loadExternal(logConsole, CostantiPdD.getOpenspcoop2LoggerProtocolProperties(protocol),
								CostantiPdD.getOpenspcoop2LoggerProtocolLocalPath(protocol) ,
								rootDirectory, loggerPropertiesProtocolAdjunct, objectProperties);
						
					}
					logConsole.info("Protocol '"+protocol+"': Log4j config append");
					LoggerWrapperFactory.setLogConfiguration(loggerPropertiesProtocolAdjunct,true);
					initializeLogDirs(loggerPropertiesProtocolAdjunct, true);
					
					Logger log = LoggerWrapperFactory.getLogger(CostantiPdD.getOpenspcoop2LoggerFactoryName(protocol));
					protocolFactoryManager.getProtocolFactoryByName(protocol).initProtocolLogger(log);
					log.info("Inizializzazione completata");
					
				}
			}
			
			return true;
			
		}catch(Exception e){
			OpenSPCoop2Logger.loggerOpenSPCoopConsole.error("Riscontrato errore durante l'inizializzazione del sistema di logging di OpenSPCoop per i protocolli: "
					+e.getMessage(),e);
			return false;
		}
	}

	/**
	 * Il Metodo si occupa di inizializzare gli appender personalizzati di OpenSPCoop che permettono di effettuare log dei msg diagnostici senza passare da log4j
	 * @param msgDiagConfig Configurazione
	 * @return true in caso di inizializzazione con successo, false altrimenti.
	 */
	public static boolean initializeMsgDiagnosticiOpenSPCoopAppender(MessaggiDiagnostici msgDiagConfig){
		try{
			// Inizializzazione msg diagnostici appender personalizzati
			if(msgDiagConfig!=null){
				ClassNameProperties prop = ClassNameProperties.getInstance();
				for(int i=0; i< msgDiagConfig.sizeOpenspcoopAppenderList(); i++){

					// MsgDiagAppenderClass
					String msgDiagAppenderClass = prop.getMsgDiagnosticoOpenSPCoopAppender(msgDiagConfig.getOpenspcoopAppender(i).getTipo());
					if(msgDiagAppenderClass == null){
						throw new Exception("Riscontrato errore durante il caricamento del msg diagnostico appender ["+msgDiagConfig.getOpenspcoopAppender(i).getTipo()+"]: appender non registrato.");
					}

					// Carico appender richiesto
					IDiagnosticProducer appender = null;
					try{
						appender = (IDiagnosticProducer) Loader.getInstance().newInstance(msgDiagAppenderClass);
						appender.initializeAppender(msgDiagConfig.getOpenspcoopAppender(i));
					}catch(ClassNotFoundException e){
						throw new Exception("Riscontrato errore durante il caricamento del msg diagnostico appender specificato ["+msgDiagConfig.getOpenspcoopAppender(i).getTipo()+"]: "+e.getMessage());
					}catch(Exception e){
						throw new Exception("Riscontrato errore durante l'inizializzazione del msg diagnostico appender specificato ["+msgDiagConfig.getOpenspcoopAppender(i).getTipo()+"]: "+e.getMessage());
					}
					
					// Aggiungo agli appender registrati
					OpenSPCoop2Logger.loggerMsgDiagnosticoOpenSPCoopAppender.add(appender);
					OpenSPCoop2Logger.tipoMsgDiagnosticoOpenSPCoopAppender.add(msgDiagConfig.getOpenspcoopAppender(i).getTipo());
					
					OpenSPCoop2Logger.loggerOpenSPCoopConsole.info("Sistema di logging: MsgDiagnosticoOpenSPCoopAppender di tipo ["+msgDiagConfig.getOpenspcoopAppender(i).getTipo()+"] correttamente inizializzato.");
					
				}
			}
			return true;
		}catch(Exception e){
			// Azzero gli appender personalizzati
			OpenSPCoop2Logger.loggerMsgDiagnosticoOpenSPCoopAppender.clear(); 
			OpenSPCoop2Logger.loggerOpenSPCoopConsole.error("Riscontrato errore durante l'inizializzazione degli appender personalizzati per msg diagnostici: "
					+e.getMessage());
			return false;
		}
	}

	
	/**
	 * Il Metodo si occupa di inizializzare gli appender personalizzati di OpenSPCoop che permettono di effettuare log dei tracciamenti senza passare da log4j
	 * @param tracciamentoConfig Configurazione
	 * @return true in caso di inizializzazione con successo, false altrimenti.
	 */
	public static boolean initializeTracciamentoOpenSPCoopAppender(org.openspcoop2.core.config.Tracciamento tracciamentoConfig){
		try{
			// Inizializzazione tracciamento appender personalizzati
			if(tracciamentoConfig!=null){
				ClassNameProperties prop = ClassNameProperties.getInstance();
				for(int i=0; i< tracciamentoConfig.sizeOpenspcoopAppenderList(); i++){
					
					// Tracciamento appender class
					String tracciamentoAppenderClass = prop.getTracciamentoOpenSPCoopAppender(tracciamentoConfig.getOpenspcoopAppender(i).getTipo());
					if(tracciamentoAppenderClass == null){
						throw new Exception("Riscontrato errore durante il caricamento del tracciamento appender ["+tracciamentoConfig.getOpenspcoopAppender(i).getTipo()+"]: appender non registrato.");
					}

					// Carico appender richiesto
					ITracciaProducer appender = null;
					try{
						appender = (ITracciaProducer) Loader.getInstance().newInstance(tracciamentoAppenderClass);
						appender.initializeAppender(tracciamentoConfig.getOpenspcoopAppender(i));
					}catch(ClassNotFoundException e){
						throw new Exception("Riscontrato errore durante il caricamento del tracciamento appender specificato ["+tracciamentoConfig.getOpenspcoopAppender(i).getTipo()+"]: "+e.getMessage());
					}catch(Exception e){
						throw new Exception("Riscontrato errore durante l'inizializzazione del tracciamento appender specificato ["+tracciamentoConfig.getOpenspcoopAppender(i).getTipo()+"]: "+e.getMessage());
					}
					
					// Aggiungo agli appender registrati
					OpenSPCoop2Logger.loggerTracciamentoOpenSPCoopAppender.add(appender);
					OpenSPCoop2Logger.tipoTracciamentoOpenSPCoopAppender.add(tracciamentoConfig.getOpenspcoopAppender(i).getTipo());
					
					OpenSPCoop2Logger.loggerOpenSPCoopConsole.info("Sistema di logging: TracciamentoOpenSPCoopAppender di tipo ["+tracciamentoConfig.getOpenspcoopAppender(i).getTipo()+"] correttamente inizializzato.");
					
				}
			}
			return true;
		}catch(Exception e){
			// Azzero gli appender personalizzati
			OpenSPCoop2Logger.loggerTracciamentoOpenSPCoopAppender.clear(); 
			OpenSPCoop2Logger.loggerOpenSPCoopConsole.error("Riscontrato errore durante l'inizializzazione degli appender personalizzati per il tracciamento: "
					+e.getMessage());
			return false;
		}
	}
	
	/**
	 * Il Metodo si occupa di inizializzare gli appender personalizzati di OpenSPCoop che permettono di effettuare log dei dump applicativi senza passare da log4j
	 * @param dumpConfig Configurazione
	 * @return true in caso di inizializzazione con successo, false altrimenti.
	 */
	public static boolean initializeDumpOpenSPCoopAppender(org.openspcoop2.core.config.Dump dumpConfig){
		try{
			// Inizializzazione dump appender personalizzati
			if(dumpConfig!=null){
				ClassNameProperties prop = ClassNameProperties.getInstance();
				for(int i=0; i< dumpConfig.sizeOpenspcoopAppenderList(); i++){
					
					// Dump Appender class
					String dumpAppenderClass = prop.getDumpOpenSPCoopAppender(dumpConfig.getOpenspcoopAppender(i).getTipo());
					if(dumpAppenderClass == null){
						throw new Exception("Riscontrato errore durante il caricamento del dump appender ["+dumpConfig.getOpenspcoopAppender(i).getTipo()+"]: appender non registrato.");
					}

					// Carico appender richiesto
					IDumpProducer appender = null;
					try{
						appender = (IDumpProducer) Loader.getInstance().newInstance(dumpAppenderClass);
						appender.initializeAppender(dumpConfig.getOpenspcoopAppender(i));
					}catch(ClassNotFoundException e){
						throw new Exception("Riscontrato errore durante il caricamento del dump appender specificato ["+dumpConfig.getOpenspcoopAppender(i).getTipo()+"]: "+e.getMessage());
					}catch(Exception e){
						throw new Exception("Riscontrato errore durante l'inizializzazione del dump appender specificato ["+dumpConfig.getOpenspcoopAppender(i).getTipo()+"]: "+e.getMessage());
					}
					
					// Aggiungo agli appender registrati
					OpenSPCoop2Logger.loggerDumpOpenSPCoopAppender.add(appender);
					OpenSPCoop2Logger.tipoDumpOpenSPCoopAppender.add(dumpConfig.getOpenspcoopAppender(i).getTipo());
					
					OpenSPCoop2Logger.loggerOpenSPCoopConsole.info("Sistema di logging: DumpOpenSPCoopAppender di tipo ["+dumpConfig.getOpenspcoopAppender(i).getTipo()+"] correttamente inizializzato.");
				}
			}
			return true;
		}catch(Exception e){
			// Azzero gli appender personalizzati
			OpenSPCoop2Logger.loggerDumpOpenSPCoopAppender.clear(); 
			OpenSPCoop2Logger.loggerOpenSPCoopConsole.error("Riscontrato errore durante l'inizializzazione degli appender personalizzati per il dump applicativo: "
					+e.getMessage());
			return false;
		}
	}
	

	public static String humanReadable(MsgDiagnostico msgDiag,IProtocolFactory<?> protocolFactory){
		return humanReadable(msgDiag, null, null, 
				null, false, null, null, null, protocolFactory);
	}
	/**
	 * Trasforma il messaggio diagnostico in una forma leggibile.
	 * 
	 * @param msgDiag MSg diagnostico
	 * @param idCorrelazioneApplicativa Identificativo di correlazione applicativa
	 * @param porta Identificativo della Porta
	 * @param delegata Indicazione se siamo in un contesto di porta delegata o applicativa
	 * @param fruitore Fruitore
	 * @param servizio Servizio
	 * @param servizioApplicativo Servizio Applicativo
	 * @return messaggio diagnostico in una forma leggibile.
	 */
	public static String humanReadable(MsgDiagnostico msgDiag,String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta,
			String porta,boolean delegata,IDSoggetto fruitore,IDServizio servizio,String servizioApplicativo,IProtocolFactory<?> protocolFactory){

		String protocol = null;
		if(protocolFactory!=null) {
			protocol = protocolFactory.getProtocol();
		}
		
		boolean formatValues = false;
		if(protocol!=null) {
			formatValues = OpenSPCoop2Properties.getInstance().isRegistrazioneDiagnosticaFile_intestazione_formatValues();
		}
		
		Date gdo = msgDiag.getGdo();
		String idPorta = msgDiag.getIdSoggetto().getCodicePorta()+"."+msgDiag.getIdSoggetto().toString();
		if(formatValues) {
			try {
				idPorta = NamingUtils.getLabelSoggetto(protocol, msgDiag.getIdSoggetto());
			}catch(Exception e) {}
		}
		String idFunzione = msgDiag.getIdFunzione();
		int valueLivello = msgDiag.getSeverita();
		String text = msgDiag.getMessaggio();
		String idBusta = msgDiag.getIdBusta();
		String codiceDiagnostico = msgDiag.getCodice();
		
		StringBuilder showMsg = new StringBuilder();
		
		if(protocol!=null) {
			String labelP = protocol;
			if(formatValues) {
				try {
					labelP = NamingUtils.getLabelProtocollo(protocol);
				}catch(Exception e) {}
			}
			showMsg.append("<").append(labelP).append(">");
		}
		
		if(msgDiag.getIdTransazione()!=null) {
			if(showMsg.length()>0){
				showMsg.append(" ");
			}
			showMsg.append(msgDiag.getIdTransazione());
		}
		
		if(OpenSPCoop2Logger.pddContextSerializer!=null){
			Hashtable<String, String> contextSerializerParameters = OpenSPCoop2Logger.pddContextSerializer.getLoggerKeywords();
			if(contextSerializerParameters!=null && contextSerializerParameters.size()>0){
				Enumeration<String> keywordContext = contextSerializerParameters.keys();
				while(keywordContext.hasMoreElements()){
					String keyword =  keywordContext.nextElement();
					if(msgDiag.getPropertiesNames()!=null){
						String [] propertyNames = msgDiag.getPropertiesNames();
						for (int i = 0; i < propertyNames.length; i++) {
							if(keyword.equals(propertyNames[i])){
								if(showMsg.length()>0){
									showMsg.append(" ");
								}
								showMsg.append(propertyNames[i]+":");
								showMsg.append(msgDiag.getProperty(propertyNames[i]));
							}
						}
					}
				}
			}
		}
		
		if(codiceDiagnostico!=null){
			if(showMsg.length()>0){
				showMsg.append(" ");
			}
			showMsg.append(codiceDiagnostico);
			showMsg.append(" ");
		}
		showMsg.append(idPorta);
		showMsg.append(" ");
		showMsg.append(idFunzione);
		showMsg.append(" <");
		showMsg.append(DateBuilder.getDate_Format(gdo));
		showMsg.append("> ");
		showMsg.append("(");
		showMsg.append((LogLevels.toLog4J(valueLivello)).toString());
		showMsg.append(")");
		if(idBusta!=null){
			showMsg.append(" ID:");
			showMsg.append(idBusta);
		}
		if(idCorrelazioneApplicativa!=null){
			showMsg.append(" IDApplicativo:");
			showMsg.append(idCorrelazioneApplicativa);
		}
		if(idCorrelazioneApplicativaRisposta!=null){
			showMsg.append(" IDApplicativoRisposta:");
			showMsg.append(idCorrelazioneApplicativaRisposta);
		}
		if(porta!=null && !"".equals(porta)) {
			String labelPorta = porta;
			// E' meglio vedere esattamente il nome della PD/PA. O in alternativa levarlo proprio. Senno' le PD/PA specific come si visualizzano??
//			if(formatValues) {
//				try {
//					org.openspcoop2.protocol.utils.PorteNamingUtils utils = new org.openspcoop2.protocol.utils.PorteNamingUtils(protocolFactory);
//					if(delegata) {
//						labelPorta = utils.normalizePD(porta);
//					}
//					else {
//						labelPorta = utils.normalizePA(porta);
//					}
//				}catch(Exception e) {}
//			}
			if(delegata) {
				showMsg.append(" OUT:"+labelPorta);
			}else {
				showMsg.append(" IN:"+labelPorta);
			}
		}
		if(servizioApplicativo!=null){
			if(formatValues) {
				if(delegata && !CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(servizioApplicativo)) {
					showMsg.append(" Applicativo:"+servizioApplicativo);
				}
			}
			else {
				showMsg.append(" SA:"+servizioApplicativo);
			}
		}
		if( fruitore!=null && fruitore.getNome()!=null ){
			showMsg.append(" FR:");
			String fruitoreLabel = fruitore.toString();
			if(formatValues) {
				try {
					fruitoreLabel = NamingUtils.getLabelSoggetto(protocol, fruitore);
				}catch(Exception e) {}
			}
			showMsg.append(fruitoreLabel);
		}
		if( fruitore!=null && servizio!=null)
			showMsg.append(" -> ");
		if( servizio!=null ){
			if(servizio.getNome()!=null){
				showMsg.append(" S:");
				String servizioLabel = null;
				try{
					servizioLabel = IDServizioFactory.getInstance().getUriFromIDServizio(servizio);
				}catch(Exception e){
					servizioLabel = servizio.toString(false);
				}
				if(formatValues) {
					try {
						servizioLabel = NamingUtils.getLabelAccordoServizioParteSpecifica(protocol, servizio);
					}catch(Exception e) {}
				}
				showMsg.append(servizioLabel);
			}else if(servizio.getSoggettoErogatore()!=null){
				showMsg.append(" ER:");
				String erogatoreLabel = servizio.getSoggettoErogatore().toString();
				if(formatValues) {
					try {
						erogatoreLabel = NamingUtils.getLabelSoggetto(protocol, servizio.getSoggettoErogatore());
					}catch(Exception e) {}
				}
				showMsg.append(erogatoreLabel);
			}
			
			if(servizio.getAzione()!=null){
				showMsg.append(" A:");
				showMsg.append(servizio.getAzione());
			}
		}
				
		showMsg.append("\n");
		showMsg.append(text);
		showMsg.append("\n");

		return showMsg.toString();


	}

	public static Logger getLoggerOpenSPCoopConsole() {
		return OpenSPCoop2Logger.loggerOpenSPCoopConsole;
	}

	public static Logger getLoggerOpenSPCoopCore() {
		return OpenSPCoop2Logger.loggerOpenSPCoopCore;
	}
	
	public static Logger getLoggerOpenSPCoopTimers() {
		return OpenSPCoop2Logger.loggerOpenSPCoopTimers;
	}
	
	public static Logger getLoggerOpenSPCoopResources() {
		return OpenSPCoop2Logger.loggerOpenSPCoopResources;
	}

	public static Logger getLoggerOpenSPCoopConfigurazioneSistema() {
		return OpenSPCoop2Logger.loggerOpenSPCoopConfigurazioneSistema;
	}
	
	public static Logger getLoggerOpenSPCoopConnettori() {
		return OpenSPCoop2Logger.loggerOpenSPCoopConnettori;
	}
	
	public static Logger getLoggerOpenSPCoopDumpBinarioPD() {
		return OpenSPCoop2Logger.loggerOpenSPCoopDumpBinarioPD;
	}
	
	public static Logger getLoggerOpenSPCoopDumpBinarioPA() {
		return OpenSPCoop2Logger.loggerOpenSPCoopDumpBinarioPA;
	}
	
	public static Logger getLoggerOpenSPCoopTransazioni(boolean debug) {
		if(debug) {
			return OpenSPCoop2Logger.loggerOpenSPCoopTransazioni;
		}
		else {
			return OpenSPCoop2Logger.loggerOpenSPCoopTransazioniError;
		}
	}
	public static Logger getLoggerOpenSPCoopTransazioniDevNull() {
		return OpenSPCoop2Logger.loggerOpenSPCoopTransazioniDevNull;
	}
	
	public static Logger getLoggerOpenSPCoopTransazioniSql(boolean debug) {
		if(debug) {
			return OpenSPCoop2Logger.loggerOpenSPCoopTransazioniSql;
		}
		else {
			return OpenSPCoop2Logger.loggerOpenSPCoopTransazioniSqlError;
		}
	}
	
	public static Logger getLoggerOpenSPCoopTransazioniStateful(boolean debug) {
		if(debug) {
			return OpenSPCoop2Logger.loggerOpenSPCoopTransazioniStateful;
		}
		else {
			return OpenSPCoop2Logger.loggerOpenSPCoopTransazioniStatefulError;
		}
	}
	
	public static Logger getLoggerOpenSPCoopTransazioniStatefulSql(boolean debug) {
		if(debug) {
			return OpenSPCoop2Logger.loggerOpenSPCoopTransazioniStatefulSql;
		}
		else {
			return OpenSPCoop2Logger.loggerOpenSPCoopTransazioniStatefulSqlError;
		}
	}
	
	public static Logger getLoggerOpenSPCoopEventi(boolean debug) {
		if(debug) {
			return OpenSPCoop2Logger.loggerOpenSPCoopEventi;
		}
		else {
			return OpenSPCoop2Logger.loggerOpenSPCoopEventiError;
		}
	}
	
	public static Logger getLoggerOpenSPCoopFileSystemRecovery(boolean debug) {
		if(debug) {
			return OpenSPCoop2Logger.loggerOpenSPCoopFileSystemRecovery;
		}
		else {
			return OpenSPCoop2Logger.loggerOpenSPCoopFileSystemRecoveryError;
		}
	}
	
	public static Logger getLoggerOpenSPCoopFileSystemRecoverySql(boolean debug) {
		if(debug) {
			return OpenSPCoop2Logger.loggerOpenSPCoopFileSystemRecoverySql;
		}
		else {
			return OpenSPCoop2Logger.loggerOpenSPCoopFileSystemRecoverySqlError;
		}
	}
	
	public static Logger getLoggerOpenSPCoopControlloTraffico(boolean debug) {
		if(debug) {
			return OpenSPCoop2Logger.loggerOpenSPCoopControlloTraffico;
		}
		else {
			return OpenSPCoop2Logger.loggerOpenSPCoopControlloTrafficoError;
		}
	}
	
	public static Logger getLoggerOpenSPCoopControlloTrafficoSql(boolean debug) {
		if(debug) {
			return OpenSPCoop2Logger.loggerOpenSPCoopControlloTrafficoSql;
		}
		else {
			return OpenSPCoop2Logger.loggerOpenSPCoopControlloTrafficoSqlError;
		}
	}
	
	public static Logger getLoggerOpenSPCoopStatistiche(TipoIntervalloStatistico tipoStatistica, boolean debug) {
		if(debug) {
			switch (tipoStatistica) {
			case STATISTICHE_ORARIE:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheOrarie;
			case STATISTICHE_GIORNALIERE:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheGiornaliere;
			case STATISTICHE_SETTIMANALI:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheSettimanali;
			case STATISTICHE_MENSILI:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheMensili;
			}
		}
		else {
			switch (tipoStatistica) {
			case STATISTICHE_ORARIE:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheOrarieError;
			case STATISTICHE_GIORNALIERE:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheGiornaliereError;
			case STATISTICHE_SETTIMANALI:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheSettimanaliError;
			case STATISTICHE_MENSILI:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheMensiliError;
			}
		}
		return null;
	}
	
	public static Logger getLoggerOpenSPCoopStatisticheSql(TipoIntervalloStatistico tipoStatistica, boolean debug) {
		if(debug) {
			switch (tipoStatistica) {
			case STATISTICHE_ORARIE:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheOrarieSql;
			case STATISTICHE_GIORNALIERE:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheGiornaliereSql;
			case STATISTICHE_SETTIMANALI:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheSettimanaliSql;
			case STATISTICHE_MENSILI:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheMensiliSql;
			}
		}
		else {
			switch (tipoStatistica) {
			case STATISTICHE_ORARIE:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheOrarieSqlError;
			case STATISTICHE_GIORNALIERE:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheGiornaliereSqlError;
			case STATISTICHE_SETTIMANALI:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheSettimanaliSqlError;
			case STATISTICHE_MENSILI:
				return OpenSPCoop2Logger.loggerOpenSPCoopStatisticheMensiliSqlError;
			}
		}
		return null;
	}
	
	public static Logger getLoggerOpenSPCoopConsegnaContenuti(boolean debug) {
		if(debug) {
			return OpenSPCoop2Logger.loggerOpenSPCoopConsegnaContenuti;
		}
		else {
			return OpenSPCoop2Logger.loggerOpenSPCoopConsegnaContenutiError;
		}
	}
	
	public static Logger getLoggerOpenSPCoopConsegnaContenutiSql(boolean debug) {
		if(debug) {
			return OpenSPCoop2Logger.loggerOpenSPCoopConsegnaContenutiSql;
		}
		else {
			return OpenSPCoop2Logger.loggerOpenSPCoopConsegnaContenutiSqlError;
		}
	}
	
	public static Logger getLoggerOpenSPCoopPlugins(boolean debug) {
		if(debug) {
			return OpenSPCoop2Logger.loggerOpenSPCoopPlugins;
		}
		else {
			return OpenSPCoop2Logger.loggerOpenSPCoopPluginsError;
		}
	}
	
	public static Logger getLoggerOpenSPCoopPluginsSql(boolean debug) {
		if(debug) {
			return OpenSPCoop2Logger.loggerOpenSPCoopPluginsSql;
		}
		else {
			return OpenSPCoop2Logger.loggerOpenSPCoopPluginsSqlError;
		}
	}
	
	public static List<IDiagnosticProducer> getLoggerMsgDiagnosticoOpenSPCoopAppender() {
		return OpenSPCoop2Logger.loggerMsgDiagnosticoOpenSPCoopAppender;
	}

	public static List<String> getTipoMsgDiagnosticoOpenSPCoopAppender() {
		return OpenSPCoop2Logger.tipoMsgDiagnosticoOpenSPCoopAppender;
	}

	public static List<ITracciaProducer> getLoggerTracciamentoOpenSPCoopAppender() {
		return OpenSPCoop2Logger.loggerTracciamentoOpenSPCoopAppender;
	}

	public static List<String> getTipoTracciamentoOpenSPCoopAppender() {
		return OpenSPCoop2Logger.tipoTracciamentoOpenSPCoopAppender;
	}

}


