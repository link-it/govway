/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.slf4j.Logger;

/**
 * DBManager
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBManager {

	/** Logger utilizzato per debug. */
	private static Logger log = null;

	/** DBManager */
	private static DBManager manager = null;

	private static boolean initialized = false;

	/** DataSource dove attingere connessioni della configurazione */

	private ServiceManagerProperties serviceManagerPropertiesConfig;
	public ServiceManagerProperties getServiceManagerPropertiesConfig() {
		return this.serviceManagerPropertiesConfig;
	}
	
	private DataSource dataSourceConfig = null;

	private String dataSourceConfigName = null;
	private java.util.Properties dataSourceConfigContext = null;
	public String getDataSourceConfigName() {
		return this.dataSourceConfigName;
	}

	public java.util.Properties getDataSourceConfigContext() {
		return this.dataSourceConfigContext;
	}
	
	/** DataSource dove attingere connessioni delle tracce */
	
	private ServiceManagerProperties serviceManagerPropertiesTracce;
	public ServiceManagerProperties getServiceManagerPropertiesTracce() {
		return this.serviceManagerPropertiesTracce;
	}
	
	private DataSource dataSourceTracce = null;

	private String dataSourceTracceName = null;
	private java.util.Properties dataSourceTracceContext = null;
	public String getDataSourceTracceName() {
		return this.dataSourceTracceName;
	}

	public java.util.Properties getDataSourceTracceContext() {
		return this.dataSourceTracceContext;
	}
	
	/** DataSource dove attingere connessioni delle statistiche */
	
	private ServiceManagerProperties serviceManagerPropertiesStatistiche;
	public ServiceManagerProperties getServiceManagerPropertiesStatistiche() {
		return this.serviceManagerPropertiesStatistiche;
	}
	
	private DataSource dataSourceStatistiche = null;

	private String dataSourceStatisticheName = null;
	private java.util.Properties dataSourceStatisticheContext = null;
	public String getDataSourceStatisticheName() {
		return this.dataSourceStatisticheName;
	}

	public java.util.Properties getDataSourceStatisticheContext() {
		return this.dataSourceStatisticheContext;
	}
	
	
	
	/**
	 * Viene chiamato in causa per istanziare il datasource
	 * 
	 * @param jndiName
	 *            Nome JNDI del Datasource
	 * @param context
	 *            Contesto JNDI da utilizzare
	 */
	private DBManager(String jndiNameConfig, java.util.Properties contextConfig, String tipoDBConfig, 
			String jndiNameTracce, java.util.Properties contextTracce, String tipoDBTracce, 
			String jndiNameStatistiche, java.util.Properties contextStatistiche, String tipoDBStatistiche, 
			boolean debug) throws Exception {
		try {
			DBManager.log = LoggerProperties.getLoggerCore();
			
			
			/** DataSource dove attingere connessioni della configurazione */
			
			this.dataSourceConfigName = jndiNameConfig;
			this.dataSourceConfigContext = contextConfig;
			
			if (this.dataSourceConfigContext != null) {
				DBManager.log.info("Proprieta' di contesto:" + this.dataSourceConfigContext.size());
				Enumeration<?> en = this.dataSourceConfigContext.keys();
				while (en.hasMoreElements()) {
					String key = (String) en.nextElement();
					DBManager.log.info("\tNome[" + key + "] Valore[" + this.dataSourceConfigContext.getProperty(key) + "]");
				}
			} else {
				DBManager.log.info("Proprieta' di contesto non fornite");
			}

			DBManager.log.info("Nome dataSource:" + this.dataSourceConfigName);

			InitialContext initC = null;
			if (this.dataSourceConfigContext != null && this.dataSourceConfigContext.size() > 0)
				initC = new InitialContext(this.dataSourceConfigContext);
			else
				initC = new InitialContext();
			this.dataSourceConfig = (DataSource) initC.lookup(this.dataSourceConfigName);
			initC.close();
			
			this.serviceManagerPropertiesConfig = new ServiceManagerProperties();
			this.serviceManagerPropertiesConfig.setDatabaseType(tipoDBConfig);
			this.serviceManagerPropertiesConfig.setShowSql(debug);
			
			
			/** DataSource dove attingere connessioni per le tracce */
			
			this.dataSourceTracceName = jndiNameTracce;
			this.dataSourceTracceContext = contextTracce;
			
			if (this.dataSourceTracceContext != null) {
				DBManager.log.info("Proprieta' di contesto:" + this.dataSourceTracceContext.size());
				Enumeration<?> en = this.dataSourceTracceContext.keys();
				while (en.hasMoreElements()) {
					String key = (String) en.nextElement();
					DBManager.log.info("\tNome[" + key + "] Valore[" + this.dataSourceTracceContext.getProperty(key) + "]");
				}
			} else {
				DBManager.log.info("Proprieta' di contesto non fornite");
			}

			DBManager.log.info("Nome dataSource:" + this.dataSourceTracceName);

			initC = null;
			if (this.dataSourceTracceContext != null && this.dataSourceTracceContext.size() > 0)
				initC = new InitialContext(this.dataSourceTracceContext);
			else
				initC = new InitialContext();
			this.dataSourceTracce = (DataSource) initC.lookup(this.dataSourceTracceName);
			initC.close();
			
			this.serviceManagerPropertiesTracce = new ServiceManagerProperties();
			this.serviceManagerPropertiesTracce.setDatabaseType(tipoDBTracce);
			this.serviceManagerPropertiesTracce.setShowSql(debug);
			
			
			/** DataSource dove attingere connessioni per le statistiche */
			
			this.dataSourceStatisticheName = jndiNameStatistiche;
			this.dataSourceStatisticheContext = contextStatistiche;
			
			if (this.dataSourceStatisticheContext != null) {
				DBManager.log.info("Proprieta' di contesto:" + this.dataSourceStatisticheContext.size());
				Enumeration<?> en = this.dataSourceStatisticheContext.keys();
				while (en.hasMoreElements()) {
					String key = (String) en.nextElement();
					DBManager.log.info("\tNome[" + key + "] Valore[" + this.dataSourceStatisticheContext.getProperty(key) + "]");
				}
			} else {
				DBManager.log.info("Proprieta' di contesto non fornite");
			}

			DBManager.log.info("Nome dataSource:" + this.dataSourceStatisticheName);

			initC = null;
			if (this.dataSourceStatisticheContext != null && this.dataSourceStatisticheContext.size() > 0)
				initC = new InitialContext(this.dataSourceStatisticheContext);
			else
				initC = new InitialContext();
			this.dataSourceStatistiche = (DataSource) initC.lookup(this.dataSourceStatisticheName);
			initC.close();
			
			this.serviceManagerPropertiesStatistiche = new ServiceManagerProperties();
			this.serviceManagerPropertiesStatistiche.setDatabaseType(tipoDBStatistiche);
			this.serviceManagerPropertiesStatistiche.setShowSql(debug);

		} catch (Exception e) {
			DBManager.log.error("Lookup datasource non riuscita", e);
			throw e;
		}
	}

	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader del QueueManager
	 * 
	 */
	public static boolean initialize(String jndiNameConfig, java.util.Properties contextConfig, String tipoDBConfig, 
			String jndiNameTracce, java.util.Properties contextTracce, String tipoDBTracce, 
			String jndiNameStatistiche, java.util.Properties contextStatistiche, String tipoDBStatistiche, 
			boolean debug) throws Exception {
		try {
			if (DBManager.manager == null) {
				DBManager.manager = new DBManager(jndiNameConfig, contextConfig, tipoDBConfig, 
						jndiNameTracce, contextTracce, tipoDBTracce,
						jndiNameStatistiche, contextStatistiche, tipoDBStatistiche,
						debug);
			}
			DBManager.setInitialized(true);
			return true;
		} catch (Exception e) {
			DBManager.setInitialized(false);
			DBManager.log.debug("Errore di inizializzazione del Database", e);
			throw e;
		}
	}

	/**
	 * Ritorna l'istanza di questo DBManager
	 * 
	 * @return Istanza di DBManager
	 */
	public static DBManager getInstance() {
		return DBManager.manager;
	}

	/**
	 * Viene chiamato in causa per ottenere una connessione al DB
	 */
	public java.sql.Connection getConnectionConfig() {
		if (this.dataSourceConfig == null) {
			return null;
		}

		Connection connectionDB = null;
		try {
			connectionDB = this.dataSourceConfig.getConnection();
		} catch (Exception e) {
			DBManager.log.error("getConnectionConfig from db", e);
			return null;
		}

		return connectionDB;
	}

	/**
	 * Viene chiamato in causa per rilasciare una connessione al DB, effettuando
	 * precedentemente un commit
	 * 
	 * @param connectionDB
	 *            Connessione da rilasciare.
	 */
	public void releaseConnectionConfig(java.sql.Connection connectionDB) {
		try {
			if (connectionDB != null) {
				connectionDB.close();
			}
		} catch (SQLException e) {
			DBManager.log.error("closeConnection config db", e);
		}
	}
	
	/**
	 * Viene chiamato in causa per ottenere una connessione al DB
	 */
	public java.sql.Connection getConnectionTracce() {
		if (this.dataSourceTracce == null) {
			return null;
		}

		Connection connectionDB = null;
		try {
			connectionDB = this.dataSourceTracce.getConnection();
		} catch (Exception e) {
			DBManager.log.error("getConnectionTracce from db", e);
			return null;
		}

		return connectionDB;
	}

	/**
	 * Viene chiamato in causa per rilasciare una connessione al DB, effettuando
	 * precedentemente un commit
	 * 
	 * @param connectionDB
	 *            Connessione da rilasciare.
	 */
	public void releaseConnectionTracce(java.sql.Connection connectionDB) {
		try {
			if (connectionDB != null) {
				connectionDB.close();
			}
		} catch (SQLException e) {
			DBManager.log.error("closeConnection tracce db", e);
		}
	}

	/**
	 * Viene chiamato in causa per ottenere una connessione al DB
	 */
	public java.sql.Connection getConnectionStatistiche() {
		if (this.dataSourceStatistiche == null) {
			return null;
		}

		Connection connectionDB = null;
		try {
			connectionDB = this.dataSourceStatistiche.getConnection();
		} catch (Exception e) {
			DBManager.log.error("getConnectionStatistiche from db", e);
			return null;
		}

		return connectionDB;
	}

	/**
	 * Viene chiamato in causa per rilasciare una connessione al DB, effettuando
	 * precedentemente un commit
	 * 
	 * @param connectionDB
	 *            Connessione da rilasciare.
	 */
	public void releaseConnectionStatistiche(java.sql.Connection connectionDB) {
		try {
			if (connectionDB != null) {
				connectionDB.close();
			}
		} catch (SQLException e) {
			DBManager.log.error("closeConnection statistiche db", e);
		}
	}

	
	public static boolean isInitialized() {
		return DBManager.initialized;
	}

	private static void setInitialized(boolean initialized) {
		DBManager.initialized = initialized;
	}
}
