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


package org.openspcoop2.web.ctrlstat.core;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.XMLDiff;
import org.openspcoop2.monitor.engine.alarm.AlarmConfigProperties;
import org.openspcoop2.monitor.engine.alarm.AlarmEngineConfig;
import org.openspcoop2.monitor.engine.alarm.AlarmManager;
import org.openspcoop2.monitor.engine.dynamic.CorePluginLoader;
import org.openspcoop2.monitor.engine.dynamic.PluginLoader;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.xml.XMLDiffImplType;
import org.openspcoop2.utils.xml.XMLDiffOptions;
import org.openspcoop2.web.ctrlstat.config.AllarmiConsoleConfig;
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

	//private static String IDMODULO = "ControlStation";
	protected static Logger log = null;
	private static boolean initialized = false;
	static {
		InitListener.log = LoggerWrapperFactory.getLogger(InitListener.class);
	}

	public static boolean isInitialized() {
		return InitListener.initialized;
	}

	private GestoriStartupThread gestoriStartupThread;
	private GestoreConsistenzaDati gestoreConsistenzaDati;
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		InitListener.log.info("Undeploy govwayConsole in corso...");

		InitListener.initialized = false;
		
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

		InitListener.log.info("Undeploy govwayConsole effettuato.");

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		String confDir = null;
		String confPropertyName = null;
		String confLocalPathPrefix = null;
		boolean appendActualConfiguration = false;
		try{
			InputStream is = InitListener.class.getResourceAsStream("/console.properties");
			try{
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
			}finally{
				try{
					if(is!=null){
						is.close();
					}
				}catch(Exception eClose){}
			}

		}catch(Exception e){}
				
		try{
			ControlStationLogger.initialize(InitListener.log, confDir, confPropertyName, confLocalPathPrefix, null, appendActualConfiguration);
			InitListener.log = ControlStationLogger.getPddConsoleCoreLogger();
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		
		
		InitListener.log.info("Inizializzazione resources (properties) govwayConsole in corso...");
		ConsoleProperties consoleProperties = null;
		try{
		
			if(ConsoleProperties.initialize(confDir, confPropertyName, confLocalPathPrefix,InitListener.log)==false){
				throw new Exception("ConsoleProperties not initialized");
			}
			consoleProperties = ConsoleProperties.getInstance();
			
			if(DatasourceProperties.initialize(confDir, confPropertyName, confLocalPathPrefix,InitListener.log)==false){
				throw new Exception("DatasourceProperties not initialized");
			}
			
			if(consoleProperties.isSinglePdD_RegistroServiziLocale()==false){
				if(RegistroServiziRemotoProperties.initialize(confDir, confPropertyName, confLocalPathPrefix,InitListener.log)==false){
					throw new Exception("RegistroServiziRemotoProperties not initialized");
				}
			}
			
			if(consoleProperties.isSinglePdD()==false){
				if(QueueProperties.initialize(confDir,InitListener.log)==false){
					throw new Exception("QueueProperties not initialized");
				}
			}
						
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		InitListener.log.info("Inizializzazione resources (properties) govwayConsole effettuata con successo.");

		
		InitListener.log.info("Inizializzazione ExtendedInfoManager in corso...");
		try{
			ExtendedInfoManager.initialize(new Loader(), 
					consoleProperties.getExtendedInfoDriverConfigurazione(), 
					consoleProperties.getExtendedInfoDriverPortaDelegata(), 
					consoleProperties.getExtendedInfoDriverPortaApplicativa());
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		InitListener.log.info("Inizializzazione ExtendedInfoManager effettuata con successo");
		
		InitListener.log.info("Inizializzazione resources govwayConsole in corso...");
		try{
		
			Connettori.initialize(InitListener.log);
			
			DriverControlStationDB_LIB.initialize(InitListener.log);
						
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		InitListener.log.info("Inizializzazione resources govwayConsole effettuata con successo.");
		
		InitListener.log.info("Inizializzazione XMLDiff in corso...");
		try{
			XMLDiff diff = new XMLDiff(OpenSPCoop2MessageFactory.getDefaultMessageFactory());
			diff.initialize(XMLDiffImplType.XML_UNIT, new XMLDiffOptions());
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		InitListener.log.info("Inizializzazione XMLDiff effettuata con successo");

		try{
			if(consoleProperties.isSinglePdD()==false){
				InitListener.log.info("Inizializzazione Gestori, della govwayConsole Centralizzata, in corso...");
			
                this.gestoriStartupThread = new GestoriStartupThread();
                new Thread(this.gestoriStartupThread).start();
				
				InitListener.log.info("Inizializzazione Gestori, della govwayConsole Centralizzata, effettuata con successo.");
			}
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
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
			throw new RuntimeException(e.getMessage(),e);
		}
		
		try{
			if(consoleProperties.isGestoreConsistenzaDatiEnabled()){
				this.gestoreConsistenzaDati = new GestoreConsistenzaDati(consoleProperties.isGestoreConsistenzaDati_forceCheckMapping());
                new Thread(this.gestoreConsistenzaDati).start();
                InitListener.log.info("Gestore Controllo Consistenza Dati avviato con successo.");
			}
			else{
				InitListener.log.info("Gestore Controllo Consistenza Dati disabilitato.");
			}
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
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
			throw new RuntimeException(e.getMessage(),e);
		}
		InitListener.log.info("Inizializzazione DataElement effettuata con successo");
		
		ServletContext servletContext = sce.getServletContext();

		InputStream isFont = null;

		try{
			String fontFileName = ConsoleProperties.getInstance().getConsoleFont();
			
			InitListener.log.debug("Caricato Font dal file: ["+fontFileName+"] in corso... ");
			
			isFont = servletContext.getResourceAsStream("/fonts/"+ fontFileName);

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			Font fontCaricato = Font.createFont(Font.PLAIN, isFont);
			
			InitListener.log.debug("Caricato Font: ["+fontCaricato.getName()+"] FontName: ["+fontCaricato.getFontName()+"] FontFamily: ["+fontCaricato.getFamily()+"] FontStyle: ["+fontCaricato.getStyle()+"]");
			
			ge.registerFont(fontCaricato);

			InitListener.log.debug("Check Graphics Environment: is HeadeLess ["+java.awt.GraphicsEnvironment.isHeadless()+"]");

			InitListener.log.debug("Elenco Nomi Font disponibili: " + Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
			
			ConsoleProperties.getInstance().setConsoleFontName(fontCaricato.getName());
			ConsoleProperties.getInstance().setConsoleFontFamilyName(fontCaricato.getFamily());
			ConsoleProperties.getInstance().setConsoleFontStyle(fontCaricato.getStyle());
			
			InitListener.log.debug("Caricato Font dal file: ["+fontFileName+"] completato.");
		}catch (Exception e) {
			InitListener.log.error(e.getMessage(),e);
		} finally {
			if(isFont != null){
				try {	isFont.close(); } catch (IOException e) {	}
			}
		}
		
		// inizializza il repository dei plugin
		try {
			if(consoleProperties.isConfigurazionePluginsEnabled()) {
				CorePluginLoader.initialize(new Loader(), InitListener.log,
						PluginLoader.class,
						new ConfigurazioneRegistroPluginsReader(new ControlStationCore()), 
						consoleProperties.getPluginsSeconds());
			}
			
			if(consoleProperties.isConfigurazioneAllarmiEnabled()) {
				AllarmiConsoleConfig allarmiConsoleConfig = new AllarmiConsoleConfig(consoleProperties);
				AlarmEngineConfig alarmEngineConfig = AlarmConfigProperties.getAlarmConfiguration(InitListener.log, allarmiConsoleConfig.getAllarmiConfigurazione());
				AlarmManager.setAlarmEngineConfig(alarmEngineConfig);
			}
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione del loader dei plugins: " + e.getMessage();
			InitListener.log.error(
					//					throw new ServletException(
					msgErrore,e);
			throw new RuntimeException(msgErrore,e);
		}
	}

	public static void setInitialized(boolean initialized) {
		InitListener.initialized = initialized;
	}

}
