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
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;


/**     
 * TimerFileSystemRecoveryThread
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerFileSystemRecoveryThread extends BaseThread{

	private static TimerState STATE = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	
	public static TimerState getSTATE() {
		return STATE;
	}
	public static void setSTATE(TimerState sTATE) {
		STATE = sTATE;
	}

	public static final String ID_MODULO = "TimerFileSystemRecovery";
		
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
	private boolean transazioniRegistrazioneDumpHeadersCompactEnabled = false;

	/** Costruttore */
	public TimerFileSystemRecoveryThread(Logger logCore, Logger logSql) throws Exception{
	
		this.properties = OpenSPCoop2Properties.getInstance();
		
		this.logCore = logCore;
		this.logSql = logSql;
	
		this.setTimeout(this.properties.getFileSystemRecoveryTimerIntervalSeconds());
		
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
				this.daoFactoryServiceManagerPropertiesTransazioni.setDatabaseType(DBTransazioniManager.getInstance().getTipoDatabase());
			}
						
			if(this.recoveryEventi){
				this.daoFactoryServiceManagerPropertiesPluginsEventi = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.eventi.utils.ProjectInfo.getInstance());
				this.daoFactoryServiceManagerPropertiesPluginsEventi.setShowSql(this.debug);
				this.daoFactoryServiceManagerPropertiesPluginsEventi.setDatabaseType(DBTransazioniManager.getInstance().getTipoDatabase());
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
				
				// Indicazioni sulle modalita' di salvataggio degli header del dump
				this.transazioniRegistrazioneDumpHeadersCompactEnabled = this.properties.isTransazioniRegistrazioneDumpHeadersCompactEnabled();
				
			}catch(Exception e){
				throw new Exception("Errore durante l'inizializzazione del DumpAppender: "+e.getMessage(),e);
			} 
			
		}
	}
	
	private FSRecoveryConfig conf = null;
	
	@Override
	public boolean initialize(){
		try{
			this.conf = new FSRecoveryConfig(false);
			
			this.conf.setLogCore(this.logCore);
			
			this.conf.setLogSql(this.logSql);
			
			this.conf.setDebug(this.debug);
			
			this.conf.setDefaultProtocol(this.properties.getDefaultProtocolName());
			
			this.conf.setRepository(this.properties.getFileSystemRecovery_repository().getAbsolutePath());
			
			this.conf.setRipristinoEventi(this.recoveryEventi);
			
			this.conf.setRipristinoTransazioni(this.recoveryTransazioni);
			
			this.conf.setTentativi(this.properties.getFileSystemRecoveryMaxAttempts());
			return true;
		}catch(Exception e){
			this.logCore.error("Errore durante il recovery da file system (InitConfigurazione): "+e.getMessage(),e);
			return false;
		}
	}
	
	@Override
	public void process(){
				
		if(TimerState.ENABLED.equals(STATE)) {
		
			DBTransazioniManager dbManager = null;
	    	Resource r = null;
			try{
				dbManager = DBTransazioniManager.getInstance();
				r = dbManager.getResource(this.properties.getIdentitaPortaDefaultWithoutProtocol(), ID_MODULO, null);
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
									
				FSRecoveryLibrary.generate(this.conf, 
						this.daoFactory, this.daoFactoryLogger, this.daoFactoryServiceManagerPropertiesTransazioni,
						this.properties.getGestioneSerializableDB_AttesaAttiva(), this.properties.getGestioneSerializableDB_CheckInterval(),
						transazioniSM, 
						this.loggerTracciamentoOpenSPCoopAppender, 
						this.loggerMsgDiagnosticoOpenSPCoopAppender,
						this.loggerDumpOpenSPCoopAppender, this.transazioniRegistrazioneDumpHeadersCompactEnabled,
						pluginsSM, con);
				
			}catch(Exception e){
				this.logCore.error("Errore durante il recovery da file system: "+e.getMessage(),e);
			}finally{
				try{
					if(r!=null)
						dbManager.releaseResource(this.properties.getIdentitaPortaDefaultWithoutProtocol(), ID_MODULO, r);
				}catch(Exception eClose){
					// ignore
				}
			}
			
		}
		else {
			this.logCore.info("Timer "+ID_MODULO+" disabilitato");
		}
				
	}
	
	@Override
	public void close(){			
			
			this.logCore.info("Thread per il recovery da file system terminato");
			
	}
}
