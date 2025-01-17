/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.datasource;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.UUIDUtilsGenerator;
import org.openspcoop2.utils.jdbc.JDBCUtilities;

/**
 * Connection
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Connection implements java.sql.Connection {

	/** Connessione Originale */
	private java.sql.Connection wrappedConnection;
	
	/** Identificatore univoco risorsa */
	private String id = null;
	
	/** Data di rilascio della risorsa */
	private Date date = null;
	
	/** Modulo funzionale richiedente della risorsa */
	private Object moduloFunzionale = null; // object in modo da poter fornire qualsiasi oggetto, anche una stringa
	
	/** IDTransazione */
	private String idTransazione = null;
	
	/** Tipo di database */
	private TipiDatabase tipoDatabase = null;
	
	/** Identificativo Datasource */
	private String uuidDatasource = null;
	
	protected Connection(java.sql.Connection connection, TipiDatabase tipoDatabase, String idTransazione, Object moduloFunzionale,
			String uuidDatasource) throws UtilsException{
		this.wrappedConnection = connection;
		this.date = DateManager.getDate();
		this.moduloFunzionale = moduloFunzionale;
		this.idTransazione = idTransazione;
		this.tipoDatabase = tipoDatabase;
		try{
			this.id = UUIDUtilsGenerator.newUUID();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		this.uuidDatasource = uuidDatasource;
	}
	
	
	public java.sql.Connection getWrappedConnection() {
		return this.wrappedConnection;
	}
	
	protected void closeWrappedConnection() throws SQLException{
		if(!this.wrappedConnection.isClosed()){
			JDBCUtilities.closeConnection(DataSource.checkLogger, this.wrappedConnection, DataSource.checkAutocommit, DataSource.checkIsClosed);
		}
	}
	
	@Override
	public void close() throws SQLException {
		try{
			DataSourceFactory.getInstance(this.uuidDatasource).unregisterConnection(this);
		}catch(Exception e){
			throw new SQLException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean isClosed() throws SQLException {
		return this.wrappedConnection.isClosed();
	}
	
	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		if(java.sql.Connection.TRANSACTION_SERIALIZABLE == level){
			JDBCUtilities.setTransactionIsolationSerializable(this.tipoDatabase, this.wrappedConnection);
		}
		else{
			this.wrappedConnection.setTransactionIsolation(level);
		}
	}
	
	@Override
	public int getTransactionIsolation() throws SQLException {
		return this.wrappedConnection.getTransactionIsolation();
	}
	
	
	
	
	// **** GET ****
	
	public String getId() {
		return this.id;
	}

	public Date getDate() {
		return this.date;
	}

	public Object getModuloFunzionale() {
		return this.moduloFunzionale;
	}

	public String getIdTransazione() {
		return this.idTransazione;
	}

	public TipiDatabase getTipoDatabase() {
		return this.tipoDatabase;
	}
	
	
	
	
	// **** METODI SU CUI E' STATO SOLAMENTE EFFETTUATO IL WRAP ******
	
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return this.wrappedConnection.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.wrappedConnection.isWrapperFor(iface);
	}

	@Override
	public Statement createStatement() throws SQLException {
		return this.wrappedConnection.createStatement();
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return this.wrappedConnection.prepareStatement(sql);
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		return this.wrappedConnection.prepareCall(sql);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		return this.wrappedConnection.nativeSQL(sql);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.wrappedConnection.setAutoCommit(autoCommit);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return this.wrappedConnection.getAutoCommit();
	}

	@Override
	public void commit() throws SQLException {
		this.wrappedConnection.commit();
	}

	@Override
	public void rollback() throws SQLException {
		this.wrappedConnection.rollback();
	}
	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return this.wrappedConnection.getMetaData();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		this.wrappedConnection.setReadOnly(readOnly);
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return this.wrappedConnection.isReadOnly();
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		this.wrappedConnection.setCatalog(catalog);
	}

	@Override
	public String getCatalog() throws SQLException {
		return this.wrappedConnection.getCatalog();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return this.wrappedConnection.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		this.wrappedConnection.clearWarnings();
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return this.wrappedConnection.createStatement(resultSetType, resultSetConcurrency);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return this.wrappedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return this.wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return this.wrappedConnection.getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		this.wrappedConnection.setTypeMap(map);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		this.wrappedConnection.setHoldability(holdability);
	}

	@Override
	public int getHoldability() throws SQLException {
		return this.wrappedConnection.getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return this.wrappedConnection.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return this.wrappedConnection.setSavepoint(name);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		this.wrappedConnection.rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		this.wrappedConnection.releaseSavepoint(savepoint);
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return this.wrappedConnection.createStatement(resultSetType, resultSetConcurrency,resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return this.wrappedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency,resultSetHoldability);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return this.wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency,resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return this.wrappedConnection.prepareStatement(sql, autoGeneratedKeys);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return this.wrappedConnection.prepareStatement(sql, columnIndexes);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return this.wrappedConnection.prepareStatement(sql, columnNames);
	}

	@Override
	public Clob createClob() throws SQLException {
		return this.wrappedConnection.createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		return this.wrappedConnection.createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return this.wrappedConnection.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return this.wrappedConnection.createSQLXML();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return this.wrappedConnection.isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		this.wrappedConnection.setClientInfo(name,value);
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		this.wrappedConnection.setClientInfo(properties);
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return this.wrappedConnection.getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return this.wrappedConnection.getClientInfo();
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return this.wrappedConnection.createArrayOf(typeName,elements);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return this.wrappedConnection.createStruct(typeName,attributes);
	}


	@Override
	public void setSchema(String schema) throws SQLException {
		this.wrappedConnection.setSchema(schema);
	}


	@Override
	public String getSchema() throws SQLException {
		return this.wrappedConnection.getSchema();
	}


	@Override
	public void abort(Executor executor) throws SQLException {
		this.wrappedConnection.abort(executor);
	}


	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		this.wrappedConnection.setNetworkTimeout(executor, milliseconds);
	}


	@Override
	public int getNetworkTimeout() throws SQLException {
		return this.wrappedConnection.getNetworkTimeout();
	}

}
