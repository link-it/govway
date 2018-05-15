package org.openspcoop2.monitor.engine.fs_recovery;

import org.openspcoop2.monitor.engine.exceptions.EngineException;
import org.openspcoop2.monitor.engine.config.MonitorProperties;
import org.openspcoop2.monitor.engine.constants.CostantiConfigurazione;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * FSRecoveryConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FSRecoveryConfig {

	private Logger logCore = null;
	private Logger logSql = null;

	/** Indicazione sul numero massimo di tentativi di recovery */
	private int tentativi;

	/** Indicazione se deve essere effettuato il log delle query */
	private boolean debug = false;	
	
	/** Indicazione se devono essere ripristinati gli allarmi */
	private boolean ripristinoEventi = false;

	/** Indicazione se devono essere ripristinati le transazioni (con tracce e diagnostici) */
	private boolean ripristinoTransazioni = false;
	
	/** Repository */
	private String repository = null;
	
	/** DefaultProtocol */
	private String defaultProtocol = null;
	
	/** Costruttore */
	public FSRecoveryConfig(boolean readPropertiesFromFile) throws EngineException{
	
		try{
			if(readPropertiesFromFile){
				
				this.logCore = LoggerWrapperFactory.getLogger("org.openspcoop2.monitor.engine.fs_recovery");
				this.logSql = LoggerWrapperFactory.getLogger("org.openspcoop2.monitor.engine.fs_recovery.sql");
				
				MonitorProperties props = MonitorProperties.getInstance(this.logCore);
	
				if ("true".equals(props.getProperty(CostantiConfigurazione.FS_RECOVERY_DEBUG, "false", true))) {
					this.debug = true;
				} else {
					this.debug = false;
				}
				
				this.repository = props.getProperty(CostantiConfigurazione.FS_RECOVERY_REPOSITORY_DIR, true, true);
				
				if ("true".equals(props.getProperty(CostantiConfigurazione.FS_RECOVERY_EVENTS_ENABLED, "false", true))) {
					this.ripristinoEventi = true;
				} else {
					this.ripristinoEventi = false;
				}
				
				if ("true".equals(props.getProperty(CostantiConfigurazione.FS_RECOVERY_TRANSACTION_ENABLED, "true", true))) {
					this.ripristinoTransazioni = true;
				} else {
					this.ripristinoTransazioni = false;
				}
				
				try {
					this.tentativi = Integer.parseInt(props.getProperty(CostantiConfigurazione.FS_RECOVERY_MAX_ATTEMPTS, true, true));
				} catch (NumberFormatException e) {
					throw e;
				}
				
				this.defaultProtocol = props.getProperty(CostantiConfigurazione.PDD_MONITOR_DEFAULT_PROTOCOL, false, true);
			}
			
		}catch(Exception e){
			throw new EngineException(e.getMessage(),e);
		}
	}
	
	
	public Logger getLogCore() {
		return this.logCore;
	}

	public void setLogCore(Logger logCore) {
		this.logCore = logCore;
	}

	public Logger getLogSql() {
		return this.logSql;
	}

	public void setLogSql(Logger logSql) {
		this.logSql = logSql;
	}

	public boolean isDebug() {
		return this.debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public boolean isRipristinoEventi() {
		return this.ripristinoEventi;
	}


	public void setRipristinoEventi(boolean ripristinoEventi) {
		this.ripristinoEventi = ripristinoEventi;
	}


	public boolean isRipristinoTransazioni() {
		return this.ripristinoTransazioni;
	}


	public void setRipristinoTransazioni(boolean ripristinoTransazioni) {
		this.ripristinoTransazioni = ripristinoTransazioni;
	}


	public String getRepository() {
		return this.repository;
	}


	public void setRepository(String repository) {
		this.repository = repository;
	}

	public int getTentativi() {
		return this.tentativi;
	}


	public void setTentativi(int tentativi) {
		this.tentativi = tentativi;
	}
	
	public String getDefaultProtocol() {
		return this.defaultProtocol;
	}


	public void setDefaultProtocol(String defaultProtocol) {
		this.defaultProtocol = defaultProtocol;
	}
}
