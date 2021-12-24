/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.fs_recovery;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.monitor.engine.exceptions.EngineException;
import org.openspcoop2.monitor.engine.config.MonitorProperties;
import org.openspcoop2.monitor.engine.constants.CostantiConfigurazione;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.utils.OpenSPCoopAppenderUtilities;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.protocol.engine.BasicProtocolFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.resources.Loader;

/**
 * TimerFSRecoveryThread
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerFSRecoveryThread extends Thread{

	/**
	 * Timeout che definisce la cadenza di avvio di questo timer. 
	 */
	private long timeout = 10; // ogni 10 secondi avvio il Thread
	

	/** Configurazione */
	private FSRecoveryConfig fsRepositoryConfig;
	
	/** DataSource */
	private DataSource ds;
	private String dsJndiName;
	
	/** Connection */
	private Connection connection;
	private String connectionUrl = null;
	private String connectionDriver = null;
	private String connectionUsername = null;
	private String connectionPassword = null;
	
	/** DAOFactory */
	private DAOFactory daoFactory;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni = null;
	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM = null;
	private org.openspcoop2.core.eventi.dao.IServiceManager pluginsEventiSM = null;
	
	/** OpenSPCoopAppender */
	private ITracciaProducer tracciamentoAppender;
	private IDiagnosticProducer diagnosticoAppender;
	private IDumpProducer dumpAppender;
	private boolean transazioniRegistrazioneDumpHeadersCompactEnabled;
	
    // VARIABILE PER STOP
	private boolean stop = false;
	
	public boolean isStop() {
		return this.stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	
	/** Costruttore Datasource */
	public TimerFSRecoveryThread(DataSource ds, 
			ITracciaProducer tracciamentoAppender,
			IDiagnosticProducer diagnosticoAppender, 
			IDumpProducer dumpAppender, boolean transazioniRegistrazioneDumpHeadersCompactEnabled,
			FSRecoveryConfig fsRepositoryConfig) throws EngineException{
		this(fsRepositoryConfig);
		this.ds = ds;
		this.tracciamentoAppender = tracciamentoAppender;
		this.diagnosticoAppender = diagnosticoAppender;
		this.dumpAppender = dumpAppender;
		this.transazioniRegistrazioneDumpHeadersCompactEnabled = transazioniRegistrazioneDumpHeadersCompactEnabled;
	}
	public TimerFSRecoveryThread(String ds,Properties dsContext, FSRecoveryConfig fsRepositoryConfig) throws EngineException{
		this(fsRepositoryConfig);
		GestoreJNDI jndi = new GestoreJNDI(dsContext);
		try{
			this.dsJndiName = ds;
			this.ds = (DataSource) jndi.lookup(ds);
		}catch(Exception e){
			throw new EngineException(e.getMessage(),e);
		}
	}
	
	/** Costruttore Connection */
	public TimerFSRecoveryThread(Connection connection, 
			ITracciaProducer tracciamentoAppender,
			IDiagnosticProducer diagnosticoAppender,
			IDumpProducer dumpAppender, boolean transazioniRegistrazioneDumpHeadersCompactEnabled,
			OpenspcoopAppender appenderProperties, FSRecoveryConfig fsRepositoryConfig) throws EngineException{
		this(fsRepositoryConfig);
		this.connection = connection;
		this.tracciamentoAppender = tracciamentoAppender;
		this.diagnosticoAppender = diagnosticoAppender;
		this.dumpAppender = dumpAppender;
		this.transazioniRegistrazioneDumpHeadersCompactEnabled = transazioniRegistrazioneDumpHeadersCompactEnabled;
	}
	public TimerFSRecoveryThread(String connectionUrl,String driverJDBC,String username, String password, FSRecoveryConfig fsRepositoryConfig) throws EngineException{
		this(fsRepositoryConfig);
		try{
			this.connectionUrl = connectionUrl;
			this.connectionDriver = driverJDBC;
			this.connectionUsername = username;
			this.connectionPassword = password;
			Class.forName(driverJDBC);
			this.connection = DriverManager.getConnection(connectionUrl,username,password);
		}catch(Exception e){
			throw new EngineException(e.getMessage(),e);
		}
	}
	
	/** Costruttore */
	public TimerFSRecoveryThread(FSRecoveryConfig fsRepositoryConfig) throws EngineException{
	
		try{
		
			MonitorProperties props = MonitorProperties.getInstance(fsRepositoryConfig.getLogCore());

			try {
				this.timeout = Integer.parseInt(props.getProperty(CostantiConfigurazione.FS_RECOVERY_TIMEOUT, "10", true));
			} catch (NumberFormatException e) {
				this.timeout=10;
			}
			
			this.fsRepositoryConfig = fsRepositoryConfig;
			
			this.daoFactory = DAOFactory.getInstance(this.fsRepositoryConfig.getLogSql());
			this.daoFactoryServiceManagerPropertiesTransazioni = DAOFactoryProperties.getInstance(this.fsRepositoryConfig.getLogSql()).getServiceManagerProperties(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
			
		}catch(Exception e){
			throw new EngineException(e.getMessage(),e);
		}
	}
	
	/**
	 * Metodo che fa partire il Thread. 
	 *
	 */
	@Override
	public void run(){
			
		initResources();
		
		while(this.stop == false){
			
			FSRecoveryLibrary.generate(this.fsRepositoryConfig, 
					this.daoFactory, this.fsRepositoryConfig.getLogSql(), this.daoFactoryServiceManagerPropertiesTransazioni,
					Costanti.GESTIONE_SERIALIZABLE_ATTESA_ATTIVA, Costanti.GESTIONE_SERIALIZABLE_CHECK_INTERVAL,
					this.transazioniSM, 
					this.tracciamentoAppender, this.diagnosticoAppender, 
					this.dumpAppender, this.transazioniRegistrazioneDumpHeadersCompactEnabled,
					this.pluginsEventiSM);
							
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
		
		this.fsRepositoryConfig.getLogCore().info("Thread per il recovery da file system terminato");

	}
	
	private void initResources(){
		
		if(this.fsRepositoryConfig.isRipristinoTransazioni()){
			try{
			 	
				if(this.ds!=null){
					this.transazioniSM = (org.openspcoop2.core.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(),
							this.ds);
				}
				else if(this.connection!=null){
					this.transazioniSM = (org.openspcoop2.core.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(),
							this.connection);
				}
				else{
					this.transazioniSM = (org.openspcoop2.core.transazioni.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
				}
				
			}catch(Exception e){
				this.fsRepositoryConfig.getLogCore().error("Errore durante l'inizializzazione del Service Manager per le transazioni: "+e.getMessage(),e);
			} 
		}
		
		if(this.fsRepositoryConfig.isRipristinoEventi()){
			try{
			 	
				if(this.ds!=null){
					this.pluginsEventiSM = (org.openspcoop2.core.eventi.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.core.eventi.utils.ProjectInfo.getInstance(),
							this.ds);
				}
				else if(this.connection!=null){
					this.pluginsEventiSM = (org.openspcoop2.core.eventi.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.core.eventi.utils.ProjectInfo.getInstance(),
							this.connection);
				}
				else{
					this.pluginsEventiSM = (org.openspcoop2.core.eventi.dao.IServiceManager) this.daoFactory.getServiceManager(
							org.openspcoop2.core.eventi.utils.ProjectInfo.getInstance());
				}
				
			}catch(Exception e){
				this.fsRepositoryConfig.getLogCore().error("Errore durante l'inizializzazione del Service Manager per i plugins eventi: "+e.getMessage(),e);
			} 
		}
		
		if(this.fsRepositoryConfig.isRipristinoTransazioni() && 
				(this.tracciamentoAppender==null || this.diagnosticoAppender==null)){
		
			try{
			
				ConfigurazionePdD configPdD = new ConfigurazionePdD();
				configPdD.setAttesaAttivaJDBC(-1);
				configPdD.setCheckIntervalJDBC(-1);
				configPdD.setLoader(new Loader(this.getClass().getClassLoader()));
				configPdD.setLog(this.fsRepositoryConfig.getLogCore());
				ProtocolFactoryManager.initialize(this.fsRepositoryConfig.getLogCore(), configPdD, this.fsRepositoryConfig.getDefaultProtocol());
				
			}catch(Exception e){
				this.fsRepositoryConfig.getLogCore().error("Errore durante l'inizializzazione del ProtocolFactoryManager: "+e.getMessage(),e);
			} 
		
		}
			
		if(this.fsRepositoryConfig.isRipristinoTransazioni() && this.tracciamentoAppender==null){
			
			try{
			
				// Init
				this.tracciamentoAppender = new BasicProtocolFactory(this.fsRepositoryConfig.getLogCore()).createTracciaProducer();
				OpenspcoopAppender tracciamentoOpenSPCoopAppender = new OpenspcoopAppender();
				tracciamentoOpenSPCoopAppender.setTipo("__threadLibraryFileSystemRecovery");
				List<Property> tracciamentoOpenSPCoopAppenderProperties = new ArrayList<Property>();

				this.addParameters(tracciamentoOpenSPCoopAppenderProperties);

				tracciamentoOpenSPCoopAppender.setPropertyList(tracciamentoOpenSPCoopAppenderProperties);
				this.tracciamentoAppender.initializeAppender(tracciamentoOpenSPCoopAppender);
				this.tracciamentoAppender.isAlive();
				
			}catch(Exception e){
				this.fsRepositoryConfig.getLogCore().error("Errore durante l'inizializzazione del TracciamentoAppender: "+e.getMessage(),e);
			} 
		}
		
		if(this.fsRepositoryConfig.isRipristinoTransazioni() && this.diagnosticoAppender==null){
			
			try{
							
				// Init
				this.diagnosticoAppender = new BasicProtocolFactory(this.fsRepositoryConfig.getLogCore()).createDiagnosticProducer();
				OpenspcoopAppender diagnosticoOpenSPCoopAppender = new OpenspcoopAppender();
				diagnosticoOpenSPCoopAppender.setTipo("__threadLibraryFileSystemRecovery");
				List<Property> diagnosticoOpenSPCoopAppenderProperties = new ArrayList<Property>();

				this.addParameters(diagnosticoOpenSPCoopAppenderProperties);

				diagnosticoOpenSPCoopAppender.setPropertyList(diagnosticoOpenSPCoopAppenderProperties);
				this.diagnosticoAppender.initializeAppender(diagnosticoOpenSPCoopAppender);
				this.diagnosticoAppender.isAlive();
				
			}catch(Exception e){
				this.fsRepositoryConfig.getLogCore().error("Errore durante l'inizializzazione del DiagnosticoAppender: "+e.getMessage(),e);
			} 
		}
		
		if(this.fsRepositoryConfig.isRipristinoTransazioni() && this.dumpAppender==null){
			
			try{
							
				// Init
				this.dumpAppender = new BasicProtocolFactory(this.fsRepositoryConfig.getLogCore()).createDumpProducer();
				OpenspcoopAppender dumpOpenSPCoopAppender = new OpenspcoopAppender();
				dumpOpenSPCoopAppender.setTipo("__threadLibraryFileSystemRecovery");
				List<Property> dumpOpenSPCoopAppenderProperties = new ArrayList<Property>();

				this.addParameters(dumpOpenSPCoopAppenderProperties);

				dumpOpenSPCoopAppender.setPropertyList(dumpOpenSPCoopAppenderProperties);
				this.diagnosticoAppender.initializeAppender(dumpOpenSPCoopAppender);
				this.diagnosticoAppender.isAlive();
				
			}catch(Exception e){
				this.fsRepositoryConfig.getLogCore().error("Errore durante l'inizializzazione del DiagnosticoAppender: "+e.getMessage(),e);
			} 
		}
	}
	
	private void addParameters(List<Property> appenderProperties) throws Exception{
		DAOFactoryProperties daoFactoryProperties = DAOFactoryProperties.getInstance(this.fsRepositoryConfig.getLogSql());
		String tipoDatabase = daoFactoryProperties.getTipoDatabase(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
		
		String dsJndiName = null;
		String connectionUrl = null;
		String connectionDriver = null;
		String connectionUsername = null;
		String connectionPassword = null;
		if(this.dsJndiName!=null){
			dsJndiName = this.dsJndiName;
		}
		else if(this.connectionUrl!=null){
			connectionUrl = this.connectionUrl;
			connectionDriver = this.connectionDriver;
			connectionUsername = this.connectionUsername;
			connectionPassword = this.connectionPassword;
		}
		else{
			if(daoFactoryProperties.isTipoAccessoTramiteDatasource(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance())){
				dsJndiName = daoFactoryProperties.getDatasourceJNDIName(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
			}
			else{
				connectionUrl = daoFactoryProperties.getConnectionUrl(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
				connectionDriver = daoFactoryProperties.getConnectionDriverJDBC(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
				connectionUsername = daoFactoryProperties.getConnectionAuthUsername(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
				connectionPassword = daoFactoryProperties.getConnectionAuthPassword(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
			}
		}
		
		boolean debug = daoFactoryProperties.isShowSql(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
		
		OpenSPCoopAppenderUtilities.addParameters(this.fsRepositoryConfig.getLogCore(), appenderProperties, 
				dsJndiName, 
				connectionUrl, connectionDriver, connectionUsername, connectionPassword, 
				tipoDatabase,
				true, debug);
		
	}
}
