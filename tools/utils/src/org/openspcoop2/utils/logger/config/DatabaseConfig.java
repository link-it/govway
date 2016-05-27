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

package org.openspcoop2.utils.logger.config;

import java.sql.Connection;

import javax.sql.DataSource;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;

/**
 * DatabaseConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatabaseConfig {

	private TipiDatabase databaseType;
	private boolean logSql;
	
	private DataSource ds;
	
	private Connection connection;
	private Boolean isAutocommit;
	
	private DatabaseConfigDatasource configDatasource;
	
	private DatabaseConfigConnection configConnection;
	
	public TipiDatabase getDatabaseType() {
		return this.databaseType;
	}
	public void setDatabaseType(TipiDatabase databaseType) {
		this.databaseType = databaseType;
	}
	
	public boolean isLogSql() {
		return this.logSql;
	}
	public void setLogSql(boolean logSql) {
		this.logSql = logSql;
	}
	
	public DataSource getDs() {
		return this.ds;
	}

	public void setDs(DataSource ds) {
		this.ds = ds;
	}

	public Connection getConnection() {
		return this.connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Boolean getIsAutocommit() {
		return this.isAutocommit;
	}

	public void setIsAutocommit(Boolean isAutocommit) {
		this.isAutocommit = isAutocommit;
	}

	public DatabaseConfigDatasource getConfigDatasource() {
		return this.configDatasource;
	}

	public void setConfigDatasource(DatabaseConfigDatasource configDatasource) {
		this.configDatasource = configDatasource;
	}

	public DatabaseConfigConnection getConfigConnection() {
		return this.configConnection;
	}

	public void setConfigConnection(DatabaseConfigConnection configConnection) {
		this.configConnection = configConnection;
	}
	
	public static void validate(DatabaseConfig config) throws UtilsException{
		if(config==null){
			throw new UtilsException("Database configuration undefined (with enabled mode)");
		}
		if(config.getDatabaseType()==null){
			throw new UtilsException("Database configuration undefined (databaseType undefined)");
		}
		if(TipiDatabase.DEFAULT.equals(config.getDatabaseType())){
			throw new UtilsException("Database configuration undefined (databaseType DEFAULT unsupported)");
		}
		int dbConfigMode = 0;
		if(config.getDs()!=null){
			dbConfigMode++;
		}
		if(config.getConnection()!=null){
			dbConfigMode++;
		}
		if(config.getConfigConnection()!=null){
			dbConfigMode++;
		}
		if(config.getConfigDatasource()!=null){
			dbConfigMode++;
		}
		if(dbConfigMode==0){
			throw new UtilsException("Datbase configuration uncorrect: source db configuration undefined");
		}
		if(dbConfigMode>1){
			throw new UtilsException("Datbase configuration uncorrect: found multiple source db configuration");
		}
		 if(config.getConnection()!=null){
			 if(config.getIsAutocommit()==null){
				 throw new UtilsException("Database configuration undefined (autocommit mode undefined, required with connection)");
			 }
		 }
	}
}
