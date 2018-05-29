package org.openspcoop2.pdd.timers;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.utils.OpenSPCoopAppenderUtilities;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.fs_recovery.FSRecoveryConfig;
import org.openspcoop2.monitor.engine.fs_recovery.FSRecoveryLibrary;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.utils.Utilities;
import org.slf4j.Logger;


public class TimerFileSystemRecoveryThread extends Thread{

	private static final String ID_MODULO = "TimerFileSystemRecovery";
	
	/**
	 * Timeout che definisce la cadenza di avvio di questo timer. 
	 */
	private long timeout = 10; // ogni 10 secondi avvio il Thread
	
	/** Logger utilizzato per debug. */
	private Logger logCore = null;
	private Logger logSql = null;
	
	/** Indicazione se deve essere effettuato il log delle query */
	private boolean debug = false;	
	
	private boolean recoveryEventi;
	private boolean recoveryTransazioni;
	
	private OpenSPCoop2Properties properties;
	
	/** Database */
	private String tipoDatabaseRuntime = null; //tipoDatabase
	private DAOFactory daoFactory = null;
    private Logger daoFactoryLogger = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesPluginsEventi = null;
	/** OpenSPCoopAppender */
	/** Appender personalizzati per i tracciamenti di OpenSPCoop */
	private ITracciaProducer loggerTracciamentoOpenSPCoopAppender = null; 
	/** Appender personalizzati per i messaggi diagnostici di OpenSPCoop2 */
	private IDiagnosticProducer loggerMsgDiagnosticoOpenSPCoopAppender = null; 
	/** Appender personalizzati per i dump di OpenSPCoop2 */
	private IDumpProducer loggerDumpOpenSPCoopAppender = null; 
	
    // VARIABILE PER STOP
	private boolean stop = false;
	
	public boolean isStop() {
		return this.stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	/** Costruttore */
	public TimerFileSystemRecoveryThread(Logger logCore, Logger logSql) throws Exception{
	
		this.properties = OpenSPCoop2Properties.getInstance();
		
		this.logCore = logCore;
		this.logSql = logSql;
	
		this.timeout = this.properties.getFileSystemRecoveryTimerIntervalSeconds();
		
		this.debug = this.properties.isFileSystemRecoveryDebug();
		
		this.recoveryEventi = this.properties.isFileSystemRecoveryTimerEventEnabled();
		this.recoveryTransazioni = this.properties.isFileSystemRecoveryTimerTransactionEnabled();
		
		DAOFactoryProperties daoFactoryProperties = null;
		try{
			
			this.tipoDatabaseRuntime = this.properties.getDatabaseType();			
			if(this.tipoDatabaseRuntime==null){
				throw new Exception("Tipo Database non definito");
			}

			// DAOFactory
			this.daoFactoryLogger = this.logSql;
			this.daoFactory = DAOFactory.getInstance(this.daoFactoryLogger);
			daoFactoryProperties = DAOFactoryProperties.getInstance(this.daoFactoryLogger);
			
			if(this.recoveryTransazioni){
				this.daoFactoryServiceManagerPropertiesTransazioni = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
				this.daoFactoryServiceManagerPropertiesTransazioni.setShowSql(this.debug);
			}
						
			if(this.recoveryEventi){
				this.daoFactoryServiceManagerPropertiesPluginsEventi = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.eventi.utils.ProjectInfo.getInstance());
				this.daoFactoryServiceManagerPropertiesPluginsEventi.setShowSql(this.debug);
			}
			
		}catch(Exception e){
			throw new Exception("Errore durante l'inizializzazione del datasource: "+e.getMessage(),e);
		}
		
		if(this.recoveryTransazioni){
			
			boolean usePdDConnection = true;
			
			try{
				
				// Init
				this.loggerTracciamentoOpenSPCoopAppender = new org.openspcoop2.pdd.logger.TracciamentoOpenSPCoopProtocolAppender();
				OpenspcoopAppender tracciamentoOpenSPCoopAppender = new OpenspcoopAppender();
				tracciamentoOpenSPCoopAppender.setTipo("__timerFileSystemRecovery");
				List<Property> tracciamentoOpenSPCoopAppenderProperties = new ArrayList<Property>();
	
				// Verra poi utilizzata la connessione ottenuta ogni volta che il timer viene eseguito, infatti si usa usePdDConnection
				OpenSPCoopAppenderUtilities.addParameters(this.daoFactoryLogger, tracciamentoOpenSPCoopAppenderProperties, 
						null, // nessun datasource
						null, null, null, null,  // nessuna connection
						this.tipoDatabaseRuntime,
						usePdDConnection, // viene usata la connessione della PdD 
						this.debug
						);
				OpenSPCoopAppenderUtilities.addCheckProperties(tracciamentoOpenSPCoopAppenderProperties, false);
	
				tracciamentoOpenSPCoopAppender.setPropertyList(tracciamentoOpenSPCoopAppenderProperties);
				this.loggerTracciamentoOpenSPCoopAppender.initializeAppender(tracciamentoOpenSPCoopAppender);
				this.loggerTracciamentoOpenSPCoopAppender.isAlive();
				
			}catch(Exception e){
				throw new Exception("Errore durante l'inizializzazione del TracciamentoAppender: "+e.getMessage(),e);
			} 
			
			try{
				
				// Init
				this.loggerMsgDiagnosticoOpenSPCoopAppender = new org.openspcoop2.pdd.logger.MsgDiagnosticoOpenSPCoopProtocolAppender();
				OpenspcoopAppender diagnosticoOpenSPCoopAppender = new OpenspcoopAppender();
				diagnosticoOpenSPCoopAppender.setTipo("__timerFileSystemRecovery");
				List<Property> diagnosticoOpenSPCoopAppenderProperties = new ArrayList<Property>();
	
				// Verra poi utilizzata la connessione ottenuta ogni volta che il timer viene eseguito, infatti si usa usePdDConnection
				OpenSPCoopAppenderUtilities.addParameters(this.daoFactoryLogger, diagnosticoOpenSPCoopAppenderProperties, 
						null, // nessun datasource
						null, null, null, null,  // nessuna connection
						this.tipoDatabaseRuntime,
						usePdDConnection, // viene usata la connessione della PdD
						this.debug
						);
				OpenSPCoopAppenderUtilities.addCheckProperties(diagnosticoOpenSPCoopAppenderProperties, false);
	
				diagnosticoOpenSPCoopAppender.setPropertyList(diagnosticoOpenSPCoopAppenderProperties);
				this.loggerMsgDiagnosticoOpenSPCoopAppender.initializeAppender(diagnosticoOpenSPCoopAppender);
				this.loggerMsgDiagnosticoOpenSPCoopAppender.isAlive();
				
			}catch(Exception e){
				throw new Exception("Errore durante l'inizializzazione del DiagnosticoAppender: "+e.getMessage(),e);
			} 
			
			try{
				
				// Init
				this.loggerDumpOpenSPCoopAppender = new org.openspcoop2.pdd.logger.DumpOpenSPCoopProtocolAppender();
				OpenspcoopAppender dumpOpenSPCoopAppender = new OpenspcoopAppender();
				dumpOpenSPCoopAppender.setTipo("__timerFileSystemRecovery");
				List<Property> dumpOpenSPCoopAppenderProperties = new ArrayList<Property>();
	
				// Verra poi utilizzata la connessione ottenuta ogni volta che il timer viene eseguito, infatti si usa usePdDConnection
				OpenSPCoopAppenderUtilities.addParameters(this.daoFactoryLogger, dumpOpenSPCoopAppenderProperties, 
						null, // nessun datasource
						null, null, null, null,  // nessuna connection
						this.tipoDatabaseRuntime,
						usePdDConnection, // viene usata la connessione della PdD 
						this.debug
						);
				OpenSPCoopAppenderUtilities.addCheckProperties(dumpOpenSPCoopAppenderProperties, false);
	
				dumpOpenSPCoopAppender.setPropertyList(dumpOpenSPCoopAppenderProperties);
				this.loggerDumpOpenSPCoopAppender.initializeAppender(dumpOpenSPCoopAppender);
				this.loggerDumpOpenSPCoopAppender.isAlive();
				
			}catch(Exception e){
				throw new Exception("Errore durante l'inizializzazione del DumpAppender: "+e.getMessage(),e);
			} 
			
		}
	}
	
	/**
	 * Metodo che fa partire il Thread. 
	 *
	 */
	@Override
	public void run(){
		
		FSRecoveryConfig conf = null;
		try{
			conf = new FSRecoveryConfig(false);
			
			conf.setLogCore(this.logCore);
			
			conf.setLogSql(this.logSql);
			
			conf.setDebug(this.debug);
			
			conf.setDefaultProtocol(this.properties.getDefaultProtocolName());
			
			conf.setRepository(this.properties.getFileSystemRecovery_repository().getAbsolutePath());
			
			conf.setRipristinoEventi(this.recoveryEventi);
			
			conf.setRipristinoTransazioni(this.recoveryTransazioni);
			
			conf.setTentativi(this.properties.getFileSystemRecoveryMaxAttempts());
			
		}catch(Exception e){
			this.logCore.error("Errore durante il recovery da file system (InitConfigurazione): "+e.getMessage(),e);
		}
		
		while(this.stop == false){
			
			DBTransazioniManager dbManager = null;
	    	Resource r = null;
			try{
				dbManager = DBTransazioniManager.getInstance();
				r = dbManager.getResource(this.properties.getIdentitaPortaDefault(null), ID_MODULO, null);
				if(r==null){
					throw new Exception("Risorsa al database non disponibile");
				}
				Connection con = (Connection) r.getResource();
				if(con == null)
					throw new Exception("Connessione non disponibile");	
	
				org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM = null;
				if(this.recoveryTransazioni){
					transazioniSM = (org.openspcoop2.core.transazioni.dao.IServiceManager) 
							this.daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), con,
							this.daoFactoryServiceManagerPropertiesTransazioni, this.daoFactoryLogger);
				}

				org.openspcoop2.core.eventi.dao.IServiceManager pluginsSM = null;
				if(this.recoveryEventi){
					pluginsSM = (org.openspcoop2.core.eventi.dao.IServiceManager) 
							this.daoFactory.getServiceManager(org.openspcoop2.core.eventi.utils.ProjectInfo.getInstance(), con,
							this.daoFactoryServiceManagerPropertiesPluginsEventi, this.daoFactoryLogger);
				}
									
				FSRecoveryLibrary.generate(conf, transazioniSM, 
						this.loggerTracciamentoOpenSPCoopAppender, 
						this.loggerMsgDiagnosticoOpenSPCoopAppender,
						this.loggerDumpOpenSPCoopAppender,
						pluginsSM, con);
				
			}catch(Exception e){
				this.logCore.error("Errore durante il recovery da file system: "+e.getMessage(),e);
			}finally{
				try{
					if(r!=null)
						dbManager.releaseResource(this.properties.getIdentitaPortaDefault(null), ID_MODULO, r);
				}catch(Exception eClose){}
			}
			
					
			// CheckInterval
			if(this.stop==false){
				int i=0;
				while(i<this.timeout){
					Utilities.sleep(1000);
					if(this.stop){
						break; // thread terminato, non lo devo far piu' dormire
					}
					i++;
				}
			}
		} 
		
		this.logCore.info("Thread per il recovery da file system terminato");

	}
}
