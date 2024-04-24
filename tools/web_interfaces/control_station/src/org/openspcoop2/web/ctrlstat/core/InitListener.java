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


package org.openspcoop2.web.ctrlstat.core;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.Arrays;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.XMLDiff;
import org.openspcoop2.monitor.engine.alarm.AlarmConfigProperties;
import org.openspcoop2.monitor.engine.alarm.AlarmEngineConfig;
import org.openspcoop2.monitor.engine.alarm.AlarmManager;
import org.openspcoop2.monitor.engine.dynamic.CorePluginLoader;
import org.openspcoop2.monitor.engine.dynamic.PluginLoader;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntimeProperties;
import org.openspcoop2.pdd.logger.filetrace.FileTraceGovWayState;
import org.openspcoop2.protocol.basic.archive.BasicArchive;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Semaphore;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.certificate.ocsp.OCSPManager;
import org.openspcoop2.utils.json.YamlSnakeLimits;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.xml.XMLDiffImplType;
import org.openspcoop2.utils.xml.XMLDiffOptions;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.config.DatasourceProperties;
import org.openspcoop2.web.ctrlstat.config.RegistroServiziRemotoProperties;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB_LIB;
import org.openspcoop2.web.ctrlstat.gestori.GestoreConsistenzaDati;
import org.openspcoop2.web.ctrlstat.gestori.GestoriStartupThread;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneRegistroPluginsReader;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementParameter;
import org.openspcoop2.web.lib.queue.config.QueueProperties;
import org.slf4j.Logger;

/**
 * Questa classe si occupa di inizializzare tutte le risorse necessarie alla
 * govwayConsole.
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class InitListener implements ServletContextListener {

	protected static Logger log = null;
	public static void setLog(Logger log) {
		InitListener.log = log;
	}
	static void logDebug(String msg) {
		if(InitListener.log!=null) {
			InitListener.log.debug(msg);
		}
	}
	static void logDebug(String msg, Throwable e) {
		if(InitListener.log!=null) {
			InitListener.log.debug(msg, e);
		}
	}
	static void logInfo(String msg) {
		if(InitListener.log!=null) {
			InitListener.log.info(msg);
		}
	}
	static void logError(String msg) {
		if(InitListener.log!=null) {
			InitListener.log.error(msg);
		}
	}
	static void logError(String msg, Throwable e) {
		if(InitListener.log!=null) {
			InitListener.log.error(msg,e);
		}
	}

	private static final Semaphore semaphoreInitListener = new Semaphore("InitListener");
	private static boolean initialized = false;
	static {
		InitListener.log = LoggerWrapperFactory.getLogger(InitListener.class);
	}

	public static boolean isInitialized() {
		return InitListener.initialized;
	}
	public static void setInitialized(boolean initialized) {
		InitListener.initialized = initialized;
	}

	private static FileTraceGovWayState fileTraceGovWayState;
	static void setFileTraceGovWayState(FileTraceGovWayState fileTraceGovWayState) {
		InitListener.fileTraceGovWayState = fileTraceGovWayState;
	}
	public static FileTraceGovWayState getFileTraceGovWayState() {
		return fileTraceGovWayState;
	}

	private GestoriStartupThread gestoriStartupThread;
	private GestoreConsistenzaDati gestoreConsistenzaDati;
	private InitRuntimeConfigReader initRuntimeConfigReader;
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		InitListener.log.info("Undeploy govwayConsole in corso...");

		InitListener.setInitialized(false);
		
        // Fermo i Gestori
		if(this.gestoriStartupThread!=null){
			this.gestoriStartupThread.stopGestori();
		}
		if(this.gestoreConsistenzaDati!=null){
			this.gestoreConsistenzaDati.setStop(true);
			int limite = 60;
			int index = 0;
			while(GestoreConsistenzaDati.gestoreConsistenzaDatiInEsecuzione && index<limite){
				Utilities.sleep(1000);
				index++;
			}
		}
		if(this.initRuntimeConfigReader!=null) {
			this.initRuntimeConfigReader.setStop(true);
		}

		// chiusura repository dei plugin
		try {
			CorePluginLoader.close(InitListener.log);
		} catch (Exception e) {
			String msgErrore = "Errore durante la chiusura del loader dei plugins: " + e.getMessage();
			InitListener.logError(msgErrore,e);
		}
		
		InitListener.log.info("Undeploy govwayConsole effettuato.");

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		semaphoreInitListener.acquireThrowRuntime("contextInitialized");
		try {
			
			if(InitListener.initialized) {
				return;
			}
			
			String confDir = null;
			String confPropertyName = null;
			String confLocalPathPrefix = null;
			boolean appendActualConfiguration = false;
			try{
				try (InputStream is = InitListener.class.getResourceAsStream("/console.properties");){
					if(is!=null){
						Properties p = new Properties();
						p.load(is);
						
						confDir = p.getProperty("confDirectory");
						if(confDir!=null){
							confDir = confDir.trim();
						}
						
						confPropertyName = p.getProperty("confPropertyName");
						if(confPropertyName!=null){
							confPropertyName = confPropertyName.trim();
						}
						
						confLocalPathPrefix = p.getProperty("confLocalPathPrefix");
						if(confLocalPathPrefix!=null){
							confLocalPathPrefix = confLocalPathPrefix.trim();
						}
						
						String tmpAppendActualConfiguration = p.getProperty("appendLog4j");
						if(tmpAppendActualConfiguration!=null){
							appendActualConfiguration = "true".equalsIgnoreCase(tmpAppendActualConfiguration.trim());
						}
					}
				}
	
			}catch(Exception e){
				// ignore
			}
					
			try{
				ControlStationLogger.initialize(InitListener.log, confDir, confPropertyName, confLocalPathPrefix, null, appendActualConfiguration);
				InitListener.setLog(ControlStationLogger.getPddConsoleCoreLogger());
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			
			
			InitListener.log.info("Inizializzazione resources (properties) govwayConsole in corso...");
			ConsoleProperties consoleProperties = null;
			try{
			
				if(!ConsoleProperties.initialize(confDir, confPropertyName, confLocalPathPrefix,InitListener.log)){
					throw new UtilsException("ConsoleProperties not initialized");
				}
				consoleProperties = ConsoleProperties.getInstance();
				
				if(!DatasourceProperties.initialize(confDir, confPropertyName, confLocalPathPrefix,InitListener.log)){
					throw new UtilsException("DatasourceProperties not initialized");
				}
				
				if(
					(consoleProperties.isSinglePddRegistroServiziLocale()==null || (!consoleProperties.isSinglePddRegistroServiziLocale().booleanValue()))
					&&
					(!RegistroServiziRemotoProperties.initialize(confDir, confPropertyName, confLocalPathPrefix,InitListener.log))
					){
					throw new UtilsException("RegistroServiziRemotoProperties not initialized");
				}
				
				if(
					(consoleProperties.isSinglePdD() == null ||  (!consoleProperties.isSinglePdD().booleanValue()))
					&&
					(!QueueProperties.initialize(confDir,InitListener.log))
					){
					throw new UtilsException("QueueProperties not initialized");
				}
							
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			InitListener.log.info("Inizializzazione resources (properties) govwayConsole effettuata con successo.");
	
			
			InitListener.log.info("Inizializzazione ExtendedInfoManager in corso...");
			try{
				ExtendedInfoManager.initialize(new Loader(), 
						consoleProperties.getExtendedInfoDriverConfigurazione(), 
						consoleProperties.getExtendedInfoDriverPortaDelegata(), 
						consoleProperties.getExtendedInfoDriverPortaApplicativa());
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			InitListener.log.info("Inizializzazione ExtendedInfoManager effettuata con successo");
			
			InitListener.log.info("Inizializzazione resources govwayConsole in corso...");
			try{
			
				Connettori.initialize(InitListener.log);
				
				DriverControlStationDB_LIB.initialize(InitListener.log);
							
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			InitListener.log.info("Inizializzazione resources govwayConsole effettuata con successo.");
			
			InitListener.log.info("Inizializzazione YAML Limits in corso...");
			try{
				Properties yamlSnakeLimits = consoleProperties.getApiYamlSnakeLimits();
				if(yamlSnakeLimits!=null && !yamlSnakeLimits.isEmpty()) {
					YamlSnakeLimits.initialize(InitListener.log, yamlSnakeLimits);
				}
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			InitListener.log.info("Inizializzazione YAML Limits con successo");
			
			InitListener.log.info("Inizializzazione XMLDiff in corso...");
			try{
				XMLDiff diff = new XMLDiff(OpenSPCoop2MessageFactory.getDefaultMessageFactory());
				diff.initialize(XMLDiffImplType.XML_UNIT, new XMLDiffOptions());
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			InitListener.log.info("Inizializzazione XMLDiff effettuata con successo");
	
			try{
				if(consoleProperties.isSinglePdD()==null || (!consoleProperties.isSinglePdD())){
					InitListener.log.info("Inizializzazione Gestori, della govwayConsole Centralizzata, in corso...");
				
	                this.gestoriStartupThread = new GestoriStartupThread();
	                new Thread(this.gestoriStartupThread).start();
					
					InitListener.log.info("Inizializzazione Gestori, della govwayConsole Centralizzata, effettuata con successo.");
				}
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			
			try{
				// Notes on Apache Commons FileUpload 1.3.3
				// Regarding potential security problems with the class called DiskFileItem, it is true, that this class exists, 
				// and can be serialized/deserialized in FileUpload versions, up to, and including 1.3.2. 
				// ...
				// Beginning with 1.3.3, the class DiskFileItem is still implementing the interface java.io.Serializable. 
				// In other words, it still declares itself as serializable, and deserializable to the JVM. 
				// In practice, however, an attempt to deserialize an instance of DiskFileItem will trigger an Exception. 
				// In the unlikely case, that your application depends on the deserialization of DiskFileItems, 
				// you can revert to the previous behaviour by setting the system property "org.apache.commons.fileupload.disk.DiskFileItem.serializable" to "true".
				// 
				// Purtroppo la classe 'org.apache.struts.upload.FormFile', all'interna utilizza DiskFileItem per serializzare le informazioni.
				// Tale classe viene usata nel meccanismo di import/export nei metodi writeFormFile e readFormFile della classe org.openspcoop2.web.ctrlstat.servlet.archivi.ImporterUtils
				// Per questo motivo si riabilita' l'opzione!
				InitListener.log.info("Inizializzazione DiskFileItem (opzione serializable), in corso...");
				System.setProperty("org.apache.commons.fileupload.disk.DiskFileItem.serializable", "true");
				InitListener.log.info("Inizializzazione DiskFileItem (opzione serializable), effettuata.");
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			
			try{
				if(consoleProperties.isGestoreConsistenzaDatiEnabled()){
					this.gestoreConsistenzaDati = new GestoreConsistenzaDati(consoleProperties.isGestoreConsistenzaDatiForceCheckMapping());
	                new Thread(this.gestoreConsistenzaDati).start();
	                InitListener.log.info("Gestore Controllo Consistenza Dati avviato con successo.");
				}
				else{
					InitListener.log.info("Gestore Controllo Consistenza Dati disabilitato.");
				}
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			
			InitListener.log.info("Inizializzazione DataElement in corso...");
			try{
				int consoleLunghezzaLabel = consoleProperties.getConsoleLunghezzaLabel();
				int numeroColonneTextArea = consoleProperties.getConsoleNumeroColonneDefaultTextArea();
				DataElementParameter dep = new DataElementParameter();
				dep.setSize(consoleLunghezzaLabel);
				dep.setCols(numeroColonneTextArea); 
				DataElement.initialize(dep);
			}catch(Exception e){
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
			InitListener.log.info("Inizializzazione DataElement effettuata con successo");
			
			ServletContext servletContext = sce.getServletContext();
	
			InputStream isFont = null;
	
			try{
				String fontFileName = ConsoleProperties.getInstance().getConsoleFont();
				
				InitListener.logDebug("Caricato Font dal file: ["+fontFileName+"] in corso... ");
				
				isFont = servletContext.getResourceAsStream("/fonts/"+ fontFileName);
	
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				Font fontCaricato = Font.createFont(Font.PLAIN, isFont);
				
				InitListener.logDebug("Caricato Font: ["+fontCaricato.getName()+"] FontName: ["+fontCaricato.getFontName()+"] FontFamily: ["+fontCaricato.getFamily()+"] FontStyle: ["+fontCaricato.getStyle()+"]");
				
				ge.registerFont(fontCaricato);
	
				InitListener.logDebug("Check Graphics Environment: is HeadeLess ["+java.awt.GraphicsEnvironment.isHeadless()+"]");
	
				InitListener.logDebug("Elenco Nomi Font disponibili: " + Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
				
				ConsoleProperties.getInstance().setConsoleFontName(fontCaricato.getName());
				ConsoleProperties.getInstance().setConsoleFontFamilyName(fontCaricato.getFamily());
				ConsoleProperties.getInstance().setConsoleFontStyle(fontCaricato.getStyle());
				
				InitListener.logDebug("Caricato Font dal file: ["+fontFileName+"] completato.");
			}catch (Exception e) {
				InitListener.logError(e.getMessage(),e);
			} finally {
				if(isFont != null){
					try {	isFont.close(); } catch (IOException e) {	
						// ignore
					}
				}
			}
			
			// inizializza nodi runtime
			try {
				ConfigurazioneNodiRuntimeProperties backwardCompatibility = new ConfigurazioneNodiRuntimeProperties(consoleProperties.getJmxPdDBackwardCompatibilityPrefix(), 
						consoleProperties.getJmxPdDBackwardCompatibilityProperties());
				ConfigurazioneNodiRuntime.initialize(consoleProperties.getJmxPdDExternalConfiguration(), backwardCompatibility);
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del gestore dei nodi run: " + e.getMessage();
				InitListener.logError(
						//					throw new ServletException(
						msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
				
			// inizializza il repository dei plugin
			try {
				if(consoleProperties.isConfigurazionePluginsEnabled()!=null && consoleProperties.isConfigurazionePluginsEnabled().booleanValue()) {
					CorePluginLoader.initialize(new Loader(), InitListener.log,
							PluginLoader.class,
							new ConfigurazioneRegistroPluginsReader(new ControlStationCore()), 
							consoleProperties.getPluginsSeconds());
				}
				
				if(consoleProperties.isConfigurazioneAllarmiEnabled()!=null && consoleProperties.isConfigurazioneAllarmiEnabled().booleanValue()) {
					AlarmEngineConfig alarmEngineConfig = AlarmConfigProperties.getAlarmConfiguration(InitListener.log, consoleProperties.getAllarmiConfigurazione(), consoleProperties.getConfDirectory());
					AlarmManager.setAlarmEngineConfig(alarmEngineConfig);
					CostantiDB.setAllarmiEnabled(true);
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del loader dei plugins: " + e.getMessage();
				InitListener.logError(
						//					throw new ServletException(
						msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
			
			// Load Security Provider
			try {
				if(consoleProperties.isSecurityLoadBouncyCastle()) {
					/**Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());*/
					Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
					InitListener.logInfo("Aggiunto Security Provider org.bouncycastle.jce.provider.BouncyCastleProvider");
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'aggiunta dei security provider: " + e.getMessage();
				InitListener.logError(
						//					throw new ServletException(
						msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
			
			// inizializzo HSM Manager
			try {
				String hsmConfig = consoleProperties.getHSMConfigurazione();
				if(StringUtils.isNotEmpty(hsmConfig)) {
					File f = new File(hsmConfig);
					HSMManager.init(f, consoleProperties.isHSMRequired(), log, false);
					HSMUtils.setHsmConfigurableKeyPassword(consoleProperties.isHSMKeyPasswordConfigurable());
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del manager HSM: " + e.getMessage();
				InitListener.logError(
						//					throw new ServletException(
						msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
			
			// inizializzo BYOK Manager
			try {
				String byokConfig = consoleProperties.getBYOKConfigurazione();
				if(StringUtils.isNotEmpty(byokConfig)) {
					File f = new File(byokConfig);
					BYOKManager.init(f, consoleProperties.isBYOKRequired(), log);
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del manager BYOK: " + e.getMessage();
				InitListener.logError(
						//					throw new ServletException(
						msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
			
			// inizializzo OCSP Manager
			try {
				String ocspConfig = consoleProperties.getOCSPConfigurazione();
				if(StringUtils.isNotEmpty(ocspConfig)) {
					File f = new File(ocspConfig);
					OCSPManager.init(f, consoleProperties.isOCSPRequired(), consoleProperties.isOCSPLoadDefault(), log);
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del manager OCSP: " + e.getMessage();
				InitListener.logError(
						//					throw new ServletException(
						msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
			
			// inizializzo OpenAPIValidator
			try {
				org.openapi4j.parser.validation.v3.OpenApi3Validator.VALIDATE_URI_REFERENCE_AS_URL = consoleProperties.isApiOpenAPIValidateUriReferenceAsUrl();
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del validatore OpenAPI: " + e.getMessage();
				InitListener.logError(
						//					throw new ServletException(
						msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
			
			// Basic Archive
			try {
				BasicArchive.setNormalizeDescription255(consoleProperties.isApiDescriptionTruncate255());
				BasicArchive.setNormalizeDescription4000(consoleProperties.isApiDescriptionTruncate4000());
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del BasicArchive: " + e.getMessage();
				InitListener.logError(
						//					throw new ServletException(
						msgErrore,e);
				throw new UtilsRuntimeException(msgErrore,e);
			}
			
			// fileTraceGovWayState
			try{
				this.initRuntimeConfigReader = new InitRuntimeConfigReader(consoleProperties);
				this.initRuntimeConfigReader.start();
				InitListener.log.info("RuntimeConfigReader avviato con successo.");
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del RuntimeConfigReader: " + e.getMessage();
				InitListener.logError(
						msgErrore,e);
				//throw new UtilsRuntimeException(msgErrore,e); non sollevo l'eccezione, e' solo una informazione informativa, non voglio mettere un vincolo che serve per forza un nodo acceso
			}
			
			InitListener.setInitialized(true);
		}finally {
			semaphoreInitListener.release("contextInitialized");
		}
	}

}
