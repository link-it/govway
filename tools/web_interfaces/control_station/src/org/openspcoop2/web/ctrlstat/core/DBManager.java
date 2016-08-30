/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.slf4j.Logger;

/**
 * Contiene la gestione delle connessioni ad un Database. Il nome della risorsa
 * JNDI da cui e' possibile attingere connessioni verso il Database, viene
 * selezionato attraverso le impostazioni lette dal file
 * 'console.datasource.properties'.
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
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

	/** DataSource dove attingere connessioni */
	private DataSource dataSource = null;

	private String dataSourceName = null;
	private java.util.Properties dataSourceContext = null;
	public String getDataSourceName() {
		return this.dataSourceName;
	}

	public java.util.Properties getDataSourceContext() {
		return this.dataSourceContext;
	}
	
	/**
	 * Viene chiamato in causa per istanziare il datasource
	 * 
	 * @param jndiName
	 *            Nome JNDI del Datasource
	 * @param context
	 *            Contesto JNDI da utilizzare
	 */
	private DBManager(String jndiName, java.util.Properties context) throws Exception {
		try {
			DBManager.log = ControlStationLogger.getPddConsoleCoreLogger();
			this.dataSourceName = jndiName;
			this.dataSourceContext = context;
			
			if (context != null) {
				DBManager.log.info("Proprieta' di contesto:" + context.size());
				Enumeration<?> en = context.keys();
				while (en.hasMoreElements()) {
					String key = (String) en.nextElement();
					DBManager.log.info("\tNome[" + key + "] Valore[" + context.getProperty(key) + "]");
				}
			} else {
				DBManager.log.info("Proprieta' di contesto non fornite");
			}

			DBManager.log.info("Nome dataSource:" + jndiName);

			InitialContext initC = null;
			if (context != null && context.size() > 0)
				initC = new InitialContext(context);
			else
				initC = new InitialContext();
			this.dataSource = (DataSource) initC.lookup(jndiName);
			initC.close();

		} catch (Exception e) {
			DBManager.log.error("Lookup datasource non riuscita", e);
			throw e;
		}
	}

	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader del QueueManager
	 * 
	 * @param jndiName
	 *            Nome JNDI del Datasource
	 * @param context
	 *            Contesto JNDI da utilizzare
	 */
	public static boolean initialize(String jndiName, java.util.Properties context) throws Exception {
		try {
			if (DBManager.manager == null) {
				DBManager.manager = new DBManager(jndiName, context);
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
	public java.sql.Connection getConnection() {
		if (this.dataSource == null) {
			return null;
		}

		Connection connectionDB = null;
		try {
			connectionDB = this.dataSource.getConnection();
		} catch (Exception e) {
			DBManager.log.error("getConnection from db", e);
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
	public void releaseConnection(java.sql.Connection connectionDB) {
		try {
			if (connectionDB != null) {
				connectionDB.close();
			}
		} catch (SQLException e) {
			DBManager.log.error("closeConnection db", e);
		}
	}

	public static boolean isInitialized() {
		return DBManager.initialized;
	}

	private static void setInitialized(boolean initialized) {
		DBManager.initialized = initialized;
	}
}
