/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.timers.statistiche.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.slf4j.Logger;

/**
 * StatisticheServerDBManager
 *
 * Gestisce le connessioni al database per il server statistiche.
 * Supporta sia l'accesso tramite DataSource (JNDI) che tramite connessione diretta (JDBC).
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticheServerDBManager {

	private static Logger checkLogger = null;
	private static volatile boolean checkIsClosed = true;
	private static volatile boolean checkAutocommit = true;
	public static boolean isCheckIsClosed() {
		return checkIsClosed;
	}
	public static void setCheckIsClosed(boolean checkIsClosed) {
		StatisticheServerDBManager.checkIsClosed = checkIsClosed;
	}
	public static boolean isCheckAutocommit() {
		return checkAutocommit;
	}
	public static void setCheckAutocommit(boolean checkAutocommit) {
		StatisticheServerDBManager.checkAutocommit = checkAutocommit;
	}
	public static Logger getCheckLogger() {
		return checkLogger;
	}
	public static void setCheckLogger(Logger checkLogger) {
		StatisticheServerDBManager.checkLogger = checkLogger;
	}
	
	
	private static StatisticheServerDBManager instance = null;

	private final Logger log;
	private final DAOFactoryProperties daoFactoryProperties;

	// Modalità datasource
	private DataSource dataSource;

	// Modalità connection
	private String connectionUrl;
	private String connectionUsername;
	private String connectionPassword;

	private boolean useDatasource;

	private StatisticheServerDBManager(Logger log) throws Exception {
		this.log = log;
		this.daoFactoryProperties = DAOFactoryProperties.getInstance(log);

		// Usa ProjectInfo delle statistiche come riferimento per la configurazione
		IProjectInfo projectInfo = org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance();

		this.useDatasource = this.daoFactoryProperties.isTipoAccessoTramiteDatasource(projectInfo);

		if (this.useDatasource) {
			initDatasource(projectInfo);
		} else {
			initConnection(projectInfo);
		}
	}

	private void initDatasource(IProjectInfo projectInfo) throws Exception {
		String jndiName = this.daoFactoryProperties.getDatasourceJNDIName(projectInfo);
		Properties jndiContext = this.daoFactoryProperties.getDatasourceJNDIContext(projectInfo);

		this.log.info("Inizializzazione DataSource tramite JNDI: {}", jndiName);

		try {
			InitialContext ctx;
			if (jndiContext != null && !jndiContext.isEmpty()) {
				ctx = new InitialContext(jndiContext);
			} else {
				ctx = new InitialContext();
			}
			this.dataSource = (DataSource) ctx.lookup(jndiName);
			this.log.info("DataSource inizializzato con successo");
		} catch (NamingException e) {
			throw new IllegalStateException("Errore durante il lookup JNDI del DataSource [" + jndiName + "]: " + e.getMessage(), e);
		}
	}

	private void initConnection(IProjectInfo projectInfo) throws Exception {
		this.connectionUrl = this.daoFactoryProperties.getConnectionUrl(projectInfo);
		String connectionDriver = this.daoFactoryProperties.getConnectionDriverJDBC(projectInfo);
		this.connectionUsername = this.daoFactoryProperties.getConnectionAuthUsername(projectInfo);
		this.connectionPassword = this.daoFactoryProperties.getConnectionAuthPassword(projectInfo);

		this.log.info("Inizializzazione connessione JDBC diretta: {}", this.connectionUrl);

		// Carica il driver JDBC
		try {
			Class.forName(connectionDriver);
			this.log.info("Driver JDBC caricato: {}", connectionDriver);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Driver JDBC non trovato [" + connectionDriver + "]: " + e.getMessage(), e);
		}
	}

	public static synchronized StatisticheServerDBManager getInstance(Logger log) throws Exception {
		if (instance == null) {
			instance = new StatisticheServerDBManager(log);
		}
		return instance;
	}

	public static StatisticheServerDBManager getInstance()  {
		// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
		if(instance==null) {
			synchronized (StatisticheServerDBManager.class) {
				throw new IllegalStateException("StatisticheServerDBManager non inizializzato. Chiamare prima getInstance(Logger)");
			}
		}
		return instance;
	}

	/**
	 * Ottiene una nuova connessione al database.
	 *
	 * @return Una connessione al database
	 * @throws SQLException Se non è possibile ottenere la connessione
	 */
	public Connection getConnection() throws SQLException {
		if (this.useDatasource) {
			return this.dataSource.getConnection();
		} else {
			return DriverManager.getConnection(
				this.connectionUrl,
				this.connectionUsername,
				this.connectionPassword
			);
		}
	}

	/**
	 * Rilascia una connessione al database.
	 *
	 * @param connection La connessione da rilasciare
	 */
	public void releaseConnection(Connection connection) {
		if (connection != null) {
			try {
				JDBCUtilities.closeConnection(checkLogger, connection, checkAutocommit, checkIsClosed);
			} catch (SQLException e) {
				this.log.error("closeConnection config db", e);
			}
		}
	}

	/**
	 * @return true se il manager usa un DataSource, false se usa connessioni dirette
	 */
	public boolean isUsingDatasource() {
		return this.useDatasource;
	}

}
