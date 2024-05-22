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

package org.openspcoop2.pdd.core.batch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.protocol.engine.driver.repository.GestoreRepositoryFactory;
import org.openspcoop2.protocol.engine.driver.repository.IGestoreRepository;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.slf4j.Logger;

/**
* GestoreMessaggi
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public abstract class AbstractGestore {

	protected TipoRuntimeRepository tipoRuntimeRepository;
	
	protected IGestoreRepository repository;
		
	protected boolean debug;
	protected boolean logQuery;
	protected Logger logCore;
	protected void logCoreDebug(String msg) {
		if(this.logCore!=null) {
			this.logCore.debug(msg);
		}
	}
	protected void logCoreError(String msg) {
		if(this.logCore!=null) {
			this.logCore.error(msg);
		}
	}
	protected void logCoreError(String msg, Throwable e) {
		if(this.logCore!=null) {
			this.logCore.error(msg,e);
		}
	}
	protected Logger logSql;
	protected int finestraSecondi;
	
	protected String connectionUrl;
	protected String driverJdbc;
	protected String connectionUsername;
	protected String connectionPassword;
	protected String tipoDatabase;
	
	private int refreshConnection;
	private Date lastDate;
	private Connection connection;
	
	protected AbstractGestore(TipoRuntimeRepository tipoRuntimeRepository,
			boolean debug, boolean logQuery,
			Logger logCore, Logger logSql,
			int finestraSecondi, int refreshConnection,
			String tipoRepositoryBuste) throws UtilsException {
		
		this.tipoRuntimeRepository = tipoRuntimeRepository;
		
		this.debug = debug;
		this.logQuery = logQuery;
		this.logCore = logCore;
		this.logSql = logSql;
		this.finestraSecondi = finestraSecondi;
		if(this.debug) {
			this.logCoreDebug("finestraSecondi: "+this.finestraSecondi);
		}
		
		this.refreshConnection = refreshConnection;
		if(this.debug) {
			this.logCoreDebug("refreshConnection: "+this.refreshConnection);
		}
		
		DAOFactoryProperties daoFactoryProperties = null;
		try {
			daoFactoryProperties = DAOFactoryProperties.getInstance(logCore);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		IProjectInfo tipoDAO = new ProjectInfo();
		
		try {
			this.tipoDatabase = daoFactoryProperties.getTipoDatabase(tipoDAO);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		if(this.debug) {
			this.logCoreDebug("tipoDatabase: "+this.tipoDatabase);
		}
		
		try {
			if(daoFactoryProperties.isTipoAccessoTramiteConnection(tipoDAO)) {
				this.connectionUrl = daoFactoryProperties.getConnectionUrl(tipoDAO);
				this.driverJdbc = daoFactoryProperties.getConnectionDriverJDBC(tipoDAO);
				this.connectionUsername = daoFactoryProperties.getConnectionAuthUsername(tipoDAO);
				this.connectionPassword = daoFactoryProperties.getConnectionAuthPassword(tipoDAO);
				if(this.debug) {
					this.logCoreDebug("connectionUrl: "+this.connectionUrl);
					this.logCoreDebug("driverJdbc: "+this.driverJdbc);
					this.logCoreDebug("connectionUsername: "+this.connectionUsername);
					/**this.logCoreDebug("connectionPassword: "+this.connectionPassword);*/
				}
				Class.forName(this.driverJdbc);
			}
			else {
				throw new UtilsException("Tipo di configurazione (datasource) non supportata");
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
		String tipoRealeRepositoryBuste = tipoRepositoryBuste;
		if(CostantiConfigurazione.REPOSITORY_BUSTE_AUTO_BYTEWISE.equals(tipoRepositoryBuste)){
			try {
				tipoRealeRepositoryBuste = GestoreRepositoryFactory.getTipoRepositoryBuste(this.tipoDatabase);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
		if(CostantiConfigurazione.REPOSITORY_BUSTE_DEFAULT.equalsIgnoreCase(tipoRealeRepositoryBuste)) {
			this.repository = new org.openspcoop2.protocol.engine.driver.repository.GestoreRepositoryDefault();
		}
		else if(CostantiConfigurazione.REPOSITORY_BUSTE_BYTEWISE.equalsIgnoreCase(tipoRealeRepositoryBuste)) {
			this.repository = new org.openspcoop2.protocol.engine.driver.repository.GestoreRepositoryBytewise();
		}
		else if(CostantiConfigurazione.REPOSITORY_BUSTE_BYTEWISE_ORACLE.equalsIgnoreCase(tipoRealeRepositoryBuste)) {
			this.repository = new org.openspcoop2.protocol.engine.driver.repository.GestoreRepositoryOracle();
		}
		else if(CostantiConfigurazione.REPOSITORY_BUSTE_BIT_OR_AND_FUNCTION.equalsIgnoreCase(tipoRealeRepositoryBuste)) {
			this.repository = new org.openspcoop2.protocol.engine.driver.repository.GestoreRepositoryBitOrAndFunction();
		}
		else {
			throw new UtilsException("Tipo di repository buste indicato '"+tipoRealeRepositoryBuste+"' sconosciuto");
		}
	}
	
	public abstract void process() throws UtilsException;
		
	protected String format(SortedMap<Integer> map) {
		StringBuilder sb = new StringBuilder();
		if(map!=null && map.size()>0) {
			for (String key : map.keys()) {
				if(sb.length()>0) {
					sb.append(" , ");
				}
				sb.append(key);
				sb.append(":");
				sb.append(map.get(key));
			}
		}
		return sb.toString();
	}
	
	protected Connection refreshConnection() throws SQLException {
		
		if(this.connection!=null) {
			if(this.refreshConnection>0) {
				// Verifico che la connessione non sia più vecchia di X secondi, nel caso la refresh
				Date now = DateManager.getDate();
				Date scaduta = new Date(now.getTime() - (this.refreshConnection*1000));
				if(scaduta.after(this.lastDate)) {
					refresh(scaduta); 
				}
			}
		}
		else {
			if(this.debug) {
				this.logCoreDebug("Apertura nuova connessione ...");
			}
			this.connection = DriverManager.getConnection(this.connectionUrl,this.connectionUsername,this.connectionPassword);
			this.lastDate = DateManager.getDate();
			if(this.debug) {
				this.logCoreDebug("Apertura nuova connessione completata");
			}
		}
		
		return this.connection;
		
	}
	private void refresh(Date scaduta) throws SQLException {
		if(this.debug) {
			this.logCoreDebug("Rilevata connessione presa in data '"+DateUtils.getSimpleDateFormatMs().format(this.lastDate)+"' su cui effettuare il refresh (refresh time seconds: "+this.refreshConnection+", scadenza:"+DateUtils.getSimpleDateFormatMs().format(scaduta)+")");
			this.logCoreDebug("Chiusura connessione ...");
		}
		try {
			this.connection.close();
			if(this.debug) {
				this.logCoreDebug("Chiusura connessione completata");
			}
		}catch(Exception t) {
			this.logCoreError("Chiusura connessione fallita: "+t.getMessage(),t);
		}
		if(this.debug) {
			this.logCoreDebug("Apertura nuova connessione ...");
		}
		this.connection = DriverManager.getConnection(this.connectionUrl,this.connectionUsername,this.connectionPassword);
		this.lastDate = DateManager.getDate();
		if(this.debug) {
			this.logCoreDebug("Apertura nuova connessione completata");
		}
	}
	
	protected void closeConnection() throws SQLException {
		try {
			if(this.connection!=null) {
				this.connection.close();
				this.connection = null;
				this.lastDate = null;
				if(this.debug) {
					this.logCoreDebug("Chiusura connessione completata");
				}
			}
			else {
				if(this.debug) {
					this.logCoreDebug("Connessione non inizializzata; chiusura non necessaria");
				}
			}
		}catch(Exception t) {
			this.logCoreError("Chiusura connessione fallita: "+t.getMessage(),t);
		}
	}
	
}
